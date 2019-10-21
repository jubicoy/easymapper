package fi.jubic.easymapper.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import fi.jubic.easymapper.annotations.EasyId;
import fi.jubic.easymapper.generator.def.PropertyDef;
import fi.jubic.easymapper.generator.def.ValueDef;
import fi.jubic.easyvalue.EasyValue;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractMapperGenerator extends AbstractProcessor {
    private Messager messager;

    /**
     * Generate specific mapper for a ValueDef.
     * @param valueDef Definition of a value class
     * @param messager Messager instance for logging.
     * @return Spec of the mapper class.
     */
    public abstract TypeSpec generate(ValueDef valueDef, Messager messager, RoundEnvironment roundEnvironment);

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        roundEnvironment
                .getElementsAnnotatedWith(EasyValue.class)
                .stream()
                .filter(element -> element.getKind() == ElementKind.CLASS)
                .map(element -> (TypeElement) element)
                .map(element -> parseDef(element, roundEnvironment))
                .forEach(def -> writeGenerator(
                        def,
                        generate(def, messager, roundEnvironment)
                ));
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private ValueDef parseDef(TypeElement element, RoundEnvironment roundEnvironment) {
        List<ExecutableElement> propertyElements = element.getEnclosedElements()
                .stream()
                .filter(method -> method.getKind() == ElementKind.METHOD)
                .map(method -> (ExecutableElement) method)
                .filter(method -> method.getModifiers().contains(Modifier.ABSTRACT))
                .filter(method -> method.getSimpleName().toString().startsWith("get"))
                .collect(Collectors.toList());

        return new ValueDef(
                element.getSimpleName().toString(),
                element,
                getIdField(element)
                        .map(id -> new PropertyDef(
                                id,
                                TypeName.get(id.getReturnType()),
                                id.getSimpleName().toString()
                                        .replaceAll("^get", "")
                        ))
                        .orElse(null),
                propertyElements.stream()
                        .filter(method -> !isReference(method, roundEnvironment))
                        .filter(method -> !isCollectionReference(method, roundEnvironment))
                        .map(method -> new PropertyDef(
                                method,
                                TypeName.get(method.getReturnType()),
                                method.getSimpleName().toString()
                                        .replaceAll("^get", "")
                        ))
                        .collect(Collectors.toList()),
                propertyElements.stream()
                        .filter(method -> isReference(method, roundEnvironment))
                        .map(method -> new PropertyDef(
                                method,
                                TypeName.get(method.getReturnType()),
                                method.getSimpleName().toString()
                                        .replaceAll("^get", "")
                        ))
                        .collect(Collectors.toList()),
                propertyElements.stream()
                        .filter(method -> isCollectionReference(method, roundEnvironment))
                        .map(method -> new PropertyDef(
                                method,
                                TypeName.get(method.getReturnType()),
                                method.getSimpleName().toString()
                                        .replaceAll("^get", "")
                        ))
                        .collect(Collectors.toList()),
                element.getEnclosedElements()
                        .stream()
                        .filter(innerClass -> innerClass.getKind() == ElementKind.CLASS)
                        .map(innerClass -> (TypeElement) innerClass)
                        .filter(innerClass -> innerClass.getSimpleName().toString().equals("Builder"))
                        .findFirst()
                        .orElseThrow(() -> {
                            messager.printMessage(
                                    Diagnostic.Kind.ERROR,
                                    String.format(
                                            "Could not find Builder for type [%s]",
                                            element.getSimpleName().toString()
                                    )
                            );
                            return new IllegalStateException();
                        })
        );
    }

    private void writeGenerator(
            ValueDef valueDef,
            TypeSpec generatorSpec
    ) {
        try {
            JavaFile
                    .builder(
                            processingEnv.getElementUtils()
                                    .getPackageOf(valueDef.getElement())
                                    .getQualifiedName()
                                    .toString(),
                            generatorSpec
                    )
                    .build()
                    .writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    String.format(
                            "Could not write class [%s]",
                            valueDef.getElement().getSimpleName().toString()
                    )
            );
            throw new RuntimeException(e);
        }
    }

    private boolean isReference(ExecutableElement element, RoundEnvironment roundEnvironment) {
        return findElementByTypeName(TypeName.get(element.getReturnType()), roundEnvironment)
                .flatMap(this::getIdField)
                .isPresent();
    }

    private boolean isCollectionReference(ExecutableElement element, RoundEnvironment roundEnvironment) {
        TypeName typeName = TypeName.get(element.getReturnType());
        if (!(typeName instanceof ParameterizedTypeName)) {
            return false;
        }
        ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) typeName;
        if (!parameterizedTypeName.rawType.equals(ClassName.get(List.class))) {
            return false;
        }

        return findElementByTypeName(parameterizedTypeName.typeArguments.get(0), roundEnvironment)
                .flatMap(this::getIdField)
                .isPresent();
    }

    protected Optional<ExecutableElement> getIdField(TypeElement element) {
        List<ExecutableElement> idProperties = element.getEnclosedElements()
                .stream()
                .filter(enclosed -> enclosed.getKind() == ElementKind.METHOD)
                .filter(enclosed -> enclosed.getAnnotation(EasyId.class) != null)
                .map(enclosed -> (ExecutableElement) enclosed)
                .filter(enclosed -> enclosed.getModifiers().contains(Modifier.ABSTRACT))
                .filter(enclosed -> enclosed.getParameters().isEmpty())
                .collect(Collectors.toList());

        if (idProperties.isEmpty()) return Optional.empty();
        if (idProperties.size() > 1) {
            throw new IllegalStateException("A value can have only one property marked as an id.");
        }

        return Optional.of(idProperties.get(0));
    }

    protected Optional<TypeElement> findElementByTypeName(TypeName typeName, RoundEnvironment roundEnvironment) {
        return roundEnvironment.getElementsAnnotatedWith(EasyValue.class)
                .stream()
                .filter(element -> typeName.equals(TypeName.get(element.asType())))
                .filter(element -> element.getKind() == ElementKind.CLASS)
                .map(element -> (TypeElement) element)
                .findFirst();
    }
}
