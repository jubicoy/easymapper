package fi.jubic.easymapper.generator;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;
import fi.jubic.easymapper.FieldAccessor;
import fi.jubic.easymapper.ReferenceCollector;
import fi.jubic.easymapper.generator.def.PropertyDef;
import fi.jubic.easymapper.generator.def.ValueDef;
import fi.jubic.easymapper.jooq.Jooq;
import fi.jubic.easymapper.jooq.JooqFieldAccessor;
import fi.jubic.easymapper.jooq.JooqReferenceAccessor;
import fi.jubic.easymapper.jooq.RecordMapper;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;

import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"fi.jubic.easymapper.annotations.EasyId"})
public class JooqMapperGenerator extends AbstractMapperGenerator {
    private static TypeVariableName R = TypeVariableName.get("R");

    private static final String FIELD_NAME_TABLE = "table";

    private static final TypeName TYPE_TABLE = ParameterizedTypeName.get(
            ClassName.get(Table.class),
            R
    );

    @Override
    public Optional<TypeSpec> generate(
            ValueDef valueDef,
            Messager messager,
            RoundEnvironment roundEnvironment
    ) {
        if (!valueDef.getId().isPresent()) {
            return Optional.empty();
        }

        String selfName = valueDef.getName() + "RecordMapper";
        TypeName superInterface = ParameterizedTypeName.get(
                ClassName.get(RecordMapper.class),
                R,
                TypeName.get(valueDef.getElement().asType())
        );

        TypeSpec.Builder mapperBuilder = TypeSpec.classBuilder(selfName)
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(TypeVariableName.get("R", ClassName.get(Record.class)))
                .addSuperinterface(superInterface)
                .addField(
                        FieldSpec.builder(
                                TYPE_TABLE,
                                FIELD_NAME_TABLE,
                                Modifier.PRIVATE,
                                Modifier.FINAL
                        ).build()
                );

        // Property accessors
        valueDef.getProperties().forEach(
                property -> mapperBuilder.addField(
                        FieldSpec.builder(
                                typeJooqFieldAccessor(property.getType()),
                                firstToLower(property.getName() + "Accessor"),
                                Modifier.PRIVATE,
                                Modifier.FINAL
                        ).build()
                )
        );
        // Reference accessors
        valueDef.getReferences().forEach(
                reference -> mapperBuilder.addField(
                        FieldSpec.builder(
                                typeJooqReferenceAccessor(
                                        WildcardTypeName.subtypeOf(ClassName.get(Object.class)),
                                        reference.getType()
                                ),
                                firstToLower(reference.getName() + "Accessor"),
                                Modifier.PRIVATE,
                                Modifier.FINAL
                        ).build()
                )
        );
        // Reference mappers
        valueDef.getReferences().forEach(
                reference -> mapperBuilder.addField(
                        FieldSpec.builder(
                                typeRecordMapper(reference.getType()),
                                firstToLower(reference.getName()) + "Mapper",
                                Modifier.PRIVATE,
                                Modifier.FINAL
                        ).build()
                )
        );

        // region Constructor
        {
            MethodSpec.Builder constructorBuilder = MethodSpec
                    .constructorBuilder()
                    .addModifiers(Modifier.PRIVATE)
                    .addParameter(TYPE_TABLE, FIELD_NAME_TABLE)
                    .addStatement("this.table = table");

            valueDef.getProperties().forEach(
                    property -> constructorBuilder
                            .addParameter(
                                    typeJooqFieldAccessor(property.getType()),
                                    firstToLower(property.getName()) + "Accessor"
                            )
                            .addStatement(
                                    "this.$LAccessor = $LAccessor",
                                    firstToLower(property.getName()),
                                    firstToLower(property.getName())
                            )
            );

            valueDef.getReferences().forEach(
                    reference -> constructorBuilder
                            .addParameter(
                                    typeJooqReferenceAccessor(
                                            WildcardTypeName.subtypeOf(TypeName.get(Object.class)),
                                            reference.getType()
                                    ),
                                    firstToLower(reference.getName()) + "Accessor"
                            )
                            .addStatement(
                                    "this.$LAccessor = $LAccessor",
                                    firstToLower(reference.getName()),
                                    firstToLower(reference.getName())
                            )
            );
            valueDef.getReferences().forEach(
                    reference -> constructorBuilder
                            .addParameter(
                                    typeRecordMapper(reference.getType()),
                                    firstToLower(reference.getName()) + "Mapper"
                            )
                            .addStatement(
                                    "this.$LMapper = $LMapper",
                                    firstToLower(reference.getName()),
                                    firstToLower(reference.getName())
                            )
            );

            mapperBuilder.addMethod(constructorBuilder.build());
        }
        // endregion Constructor

        // region Reference withers
        valueDef.getReferences().forEach(reference -> {
            String valueParams = Stream.of(
                    valueDef.getProperties().stream()
                            .map(prop -> "this."
                                            + firstToLower(
                                    prop.getName() + "Accessor"
                                    )
                            ),
                    valueDef.getReferences().stream()
                            .map(ref -> "this."
                                            + firstToLower(
                                    ref.getName() + "Accessor"
                                    )
                            ),
                    valueDef.getReferences().stream()
                            .map(ref -> {
                                if (ref.getName().equals(reference.getName())) {
                                    return firstToLower(ref.getName() + "Mapper");
                                }
                                return "this." + firstToLower(ref.getName() + "Mapper");
                            })
            )
                    .flatMap(Function.identity())
                    .collect(Collectors.joining(", "));

                    mapperBuilder.addMethod(
                            MethodSpec.methodBuilder("with" + firstToUpper(reference.getName()))
                                    .addModifiers(Modifier.PUBLIC)
                                    .returns(
                                            ParameterizedTypeName.get(
                                                    ClassName.bestGuess(selfName),
                                                    R
                                            )
                                    )
                                    .addParameter(
                                            typeRecordMapper(reference.getType()),
                                            firstToLower(reference.getName() + "Mapper")
                                    )
                                    .addStatement(
                                            "return new $N<>(this.$L, $N)",
                                            selfName,
                                            FIELD_NAME_TABLE,
                                            valueParams
                                    )
                                    .build()
                    );
                }
        );
        // endregion Reference withers

        // region Mangler::write
        {
            MethodSpec.Builder writeBuilder = MethodSpec.methodBuilder("write")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(R)
                    .addParameter(
                            ClassName.get(Record.class),
                            "output"
                    )
                    .addParameter(
                            TypeName.get(valueDef.getElement().asType()),
                            "value"
                    )
                    .addStatement("$T modifiedOutput = output.into(table)", R);

            Stream
                    .concat(
                            valueDef.getProperties().stream()
                                    .filter(property -> valueDef.getId()
                                            .map(id -> !property.getName().equals(id.getName()))
                                            .orElse(true)
                                    ),
                            valueDef.getReferences().stream()
                    )
                    .forEach(prop -> writeBuilder.addStatement(
                            "modifiedOutput = $N.write(modifiedOutput, value.$L$N())",
                            firstToLower(prop.getName() + "Accessor"),
                            prop.getAccess().getName(),
                            firstToUpper(prop.getName())
                    ));

            mapperBuilder.addMethod(
                    writeBuilder
                            .addStatement("return modifiedOutput")
                            .build()
            );
        }
        // endregion Mangler::write

        // region Mapper::map
        {
            CodeBlock.Builder callBuilder = CodeBlock.builder()
                    .add(
                            "return $T.builder()",
                            TypeName.get(valueDef.getElement().asType())
                    );
            valueDef.getProperties().forEach(
                    property -> callBuilder.add(
                            ".set$L($LAccessor.extract(inputRecord))",
                            firstToUpper(property.getName()),
                            firstToLower(property.getName())
                    )
            );
            valueDef.getReferences().forEach(
                    reference -> callBuilder.add(
                            ".set$L("
                                    + "$T.ofNullable($LMapper)"
                                    + ".map(mapper -> mapper.map(input))"
                                    + ".orElse(null)"
                                    + ")",
                            firstToUpper(reference.getName()),
                            Optional.class,
                            firstToLower(reference.getName())
                    )
            );
            callBuilder.add(".build()");

            MethodSpec.Builder mapBuilder = MethodSpec.methodBuilder("map")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(TypeName.get(valueDef.getElement().asType()))
                    .addParameter(
                            ClassName.get(Record.class),
                            "input"
                    )
                    .addStatement(
                            "$T inputRecord = input.into($N)",
                            R,
                            FIELD_NAME_TABLE
                    );

            mapperBuilder.addMethod(
                    mapBuilder
                            .beginControlFlow(
                                    "if ($LAccessor.extract(inputRecord) == null)",
                                    firstToLower(
                                            valueDef.getId()
                                                    .orElseThrow(IllegalStateException::new)
                                                    .getName()
                                    ))
                            .addStatement("return null")
                            .endControlFlow()
                            .addStatement(callBuilder.build())
                            .build()
            );
        }
        // endregion Mapper::map

        {
            TypeVariableName varI = TypeVariableName.get("I");

            // region collectinWith reference
            valueDef.getReferences().forEach(
                    reference -> mapperBuilder.addMethod(
                            MethodSpec
                                    .methodBuilder(
                                            "collectingWith"
                                                    + firstToUpper(reference.getName())
                                    )
                                    .addModifiers(Modifier.PUBLIC)
                                    .addTypeVariable(varI)
                                    .returns(
                                            ParameterizedTypeName.get(
                                                    ClassName.get(Collector.class),
                                                    ClassName.get(Record.class),
                                                    WildcardTypeName.subtypeOf(Object.class),
                                                    ParameterizedTypeName.get(
                                                            ClassName.get(Optional.class),
                                                            TypeName.get(
                                                                    valueDef.getElement().asType()
                                                            )
                                                    )
                                            )
                                    )
                                    .addParameter(
                                            ParameterizedTypeName.get(
                                                    ClassName.get(Collector.class),
                                                    ClassName.get(Record.class),
                                                    WildcardTypeName.subtypeOf(Object.class),
                                                    ParameterizedTypeName.get(
                                                            ClassName.get(Optional.class),
                                                            reference.getType()
                                                    )
                                            ),
                                            "collector"
                                    )
                                    .addStatement(
                                            "return new $T<>("
                                                    + "this,"
                                                    + "collector,"
                                                    + "($L, $L) -> $L.isPresent() "
                                                    + "? $L.toBuilder().set$L($L.get()).build() "
                                                    + ": $L)",
                                            ReferenceCollector.class,
                                            firstToLower(valueDef.getName()),
                                            firstToLower(reference.getName()),
                                            firstToLower(reference.getName()),
                                            firstToLower(valueDef.getName()),
                                            firstToUpper(reference.getName()),
                                            firstToLower(reference.getName()),
                                            firstToLower(valueDef.getName())
                                    )
                                    .build()
                    )
            );
            // endregion collectingWith reference

            // region collectingWith collection
            valueDef.getCollectionReferences().forEach(
                    reference -> mapperBuilder.addMethod(
                            MethodSpec
                                    .methodBuilder(
                                            "collectingWith"
                                                    + firstToUpper(reference.getName())
                                    )
                                    .addModifiers(Modifier.PUBLIC)
                                    .addTypeVariable(varI)
                                    .returns(
                                            ParameterizedTypeName.get(
                                                    ClassName.get(Collector.class),
                                                    ClassName.get(Record.class),
                                                    WildcardTypeName.subtypeOf(Object.class),
                                                    ParameterizedTypeName.get(
                                                            ClassName.get(Optional.class),
                                                            TypeName.get(
                                                                    valueDef.getElement().asType()
                                                            )
                                                    )
                                            )
                                    )
                                    .addParameter(
                                            ParameterizedTypeName.get(
                                                    ClassName.get(Collector.class),
                                                    ClassName.get(Record.class),
                                                    WildcardTypeName.subtypeOf(Object.class),
                                                    reference.getType()
                                            ),
                                            "collector"
                                    )
                                    .addStatement(
                                            "return new $T<>("
                                                    + "this,"
                                                    + "collector,"
                                                    + "($L, $L) -> "
                                                    + "$L.toBuilder().set$L($L).build())",
                                            ReferenceCollector.class,
                                            firstToLower(valueDef.getName()),
                                            firstToLower(reference.getName()),
                                            firstToLower(valueDef.getName()),
                                            firstToUpper(reference.getName()),
                                            firstToLower(reference.getName())
                                    )
                                    .build()
                    )
            );
            // endregion collectingWith collection

            // region collectingManyWith reference
            valueDef.getReferences().forEach(
                    reference -> mapperBuilder.addMethod(
                            MethodSpec
                                    .methodBuilder(
                                            "collectingManyWith"
                                                    + firstToUpper(reference.getName())
                                    )
                                    .addModifiers(Modifier.PUBLIC)
                                    .addTypeVariable(varI)
                                    .returns(
                                            ParameterizedTypeName.get(
                                                    ClassName.get(Collector.class),
                                                    ClassName.get(Record.class),
                                                    WildcardTypeName.subtypeOf(Object.class),
                                                    ParameterizedTypeName.get(
                                                            ClassName.get(List.class),
                                                            TypeName.get(
                                                                    valueDef.getElement().asType()
                                                            )
                                                    )
                                            )
                                    )
                                    .addParameter(
                                            ParameterizedTypeName.get(
                                                    ClassName.get(Collector.class),
                                                    ClassName.get(Record.class),
                                                    WildcardTypeName.subtypeOf(Object.class),
                                                    ParameterizedTypeName.get(
                                                            ClassName.get(Optional.class),
                                                            reference.getType()
                                                    )
                                            ),
                                            "collector"
                                    )
                                    .addStatement(
                                            "return partitionAndFlatten("
                                                    + "$LAccessor, "
                                                    + "table, "
                                                    + "collectingWith$L(collector))",
                                            valueDef.getId()
                                                    .map(id -> firstToLower(id.getName()))
                                                    .orElseThrow(() -> new IllegalStateException(
                                                            "Cannot collect without an ID"
                                                    )),
                                            firstToUpper(reference.getName())
                                    )
                                    .build()
                    )
            );
            // endregion collectingManyWith reference

            // region collectingManyWith collection
            valueDef.getCollectionReferences().forEach(
                    reference -> mapperBuilder.addMethod(
                            MethodSpec
                                    .methodBuilder(
                                            "collectingManyWith"
                                                    + firstToUpper(reference.getName())
                                    )
                                    .addModifiers(Modifier.PUBLIC)
                                    .addTypeVariable(varI)
                                    .returns(
                                            ParameterizedTypeName.get(
                                                    ClassName.get(Collector.class),
                                                    ClassName.get(Record.class),
                                                    WildcardTypeName.subtypeOf(Object.class),
                                                    ParameterizedTypeName.get(
                                                            ClassName.get(List.class),
                                                            TypeName.get(
                                                                    valueDef.getElement().asType()
                                                            )
                                                    )
                                            )
                                    )
                                    .addParameter(
                                            ParameterizedTypeName.get(
                                                    ClassName.get(Collector.class),
                                                    ClassName.get(Record.class),
                                                    WildcardTypeName.subtypeOf(Object.class),
                                                    reference.getType()
                                            ),
                                            "collector"
                                    )
                                    .addStatement(
                                            "return partitionAndFlatten("
                                                    + "$LAccessor, "
                                                    + "table, "
                                                    + "collectingWith$L(collector))",
                                            valueDef.getId()
                                                    .map(id -> firstToLower(id.getName()))
                                                    .orElseThrow(() -> new IllegalStateException(
                                                            "Cannot collect without an ID"
                                                    )),
                                            firstToUpper(reference.getName())
                                    )
                                    .build()
                    )
            );
            // endregion collectingManyWith collection
        }

        // region alias
        mapperBuilder.addMethod(
                MethodSpec.methodBuilder("alias")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override.class)
                        .returns(
                                ParameterizedTypeName.get(
                                        ClassName.bestGuess(selfName),
                                        R
                                )
                        )
                        .addParameter(
                                ParameterizedTypeName.get(
                                        ClassName.get(Table.class),
                                        R
                                ),
                                "alias"
                        )
                        .addStatement(
                                "return new $T<>(alias, $L)",
                                ClassName.bestGuess(selfName),
                                Stream.concat(
                                        Stream
                                                .concat(
                                                        valueDef.getProperties().stream(),
                                                        valueDef.getReferences().stream()
                                                )
                                                .map(PropertyDef::getName)
                                                .map(this::firstToLower)
                                                .map(name -> name + "Accessor.alias(alias)"),
                                        valueDef.getReferences()
                                                .stream()
                                                .map(ref -> "null")
                                ).collect(Collectors.joining(", "))
                        )
                        .build()
        );
        // endregion alias

        // region table
        mapperBuilder.addMethod(
                MethodSpec.methodBuilder("table")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override.class)
                        .returns(
                                ParameterizedTypeName.get(
                                        ClassName.get(Table.class),
                                        R
                                )
                        )
                        .addStatement("return table")
                        .build()
        );
        // endregion table

        // region builder()
        mapperBuilder.addMethod(
                MethodSpec.methodBuilder("builder")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addTypeVariable(
                                TypeVariableName.get(
                                        "R",
                                        ClassName.get(Record.class)
                                )
                        )
                        .returns(
                                ParameterizedTypeName.get(
                                        ClassName.bestGuess("Builder"),
                                        R
                                )
                        )
                        .addParameter(
                                ParameterizedTypeName.get(
                                        ClassName.get(Table.class),
                                        R
                                ),
                                "table"
                        )
                        .addStatement(
                                "return new $T<>(table)",
                                ClassName.bestGuess("Builder")
                        )
                        .build()
        );
        // endregion builder()

        // region Builder
        {
            TypeSpec.Builder builderBuilder = TypeSpec.classBuilder("Builder")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addTypeVariable(TypeVariableName.get("R", ClassName.get(Record.class)))
                    .addField(
                            ParameterizedTypeName.get(
                                    ClassName.get(Table.class),
                                    R
                            ),
                            "table",
                            Modifier.PRIVATE,
                            Modifier.FINAL
                    );

            // region fields
            // Property accessors
            valueDef.getProperties().forEach(
                    property -> builderBuilder.addField(
                            FieldSpec.builder(
                                    typeJooqFieldAccessor(property.getType()),
                                    firstToLower(property.getName() + "Accessor"),
                                    Modifier.PRIVATE,
                                    Modifier.FINAL
                            ).build()
                    )
            );
            // Reference accessors
            valueDef.getReferences().forEach(
                    reference -> builderBuilder.addField(
                            FieldSpec.builder(
                                    typeJooqReferenceAccessor(
                                            WildcardTypeName.subtypeOf(ClassName.get(Object.class)),
                                            reference.getType()
                                    ),
                                    firstToLower(reference.getName() + "Accessor"),
                                    Modifier.PRIVATE,
                                    Modifier.FINAL
                            ).build()
                    )
            );
            // endregion fields

            // region constructors
            {
                MethodSpec.Builder constructorBuilder = MethodSpec
                        .constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .addParameter(TYPE_TABLE, FIELD_NAME_TABLE)
                        .addStatement("this.table = table");

                valueDef.getProperties().forEach(
                        property -> constructorBuilder
                                .addParameter(
                                        typeJooqFieldAccessor(property.getType()),
                                        firstToLower(property.getName()) + "Accessor"
                                )
                                .addStatement(
                                        "this.$LAccessor = $LAccessor",
                                        firstToLower(property.getName()),
                                        firstToLower(property.getName())
                                )
                );

                valueDef.getReferences().forEach(
                        reference -> constructorBuilder
                                .addParameter(
                                        typeJooqReferenceAccessor(
                                                WildcardTypeName.subtypeOf(
                                                        TypeName.get(Object.class)
                                                ),
                                                reference.getType()
                                        ),
                                        firstToLower(reference.getName()) + "Accessor"
                                )
                                .addStatement(
                                        "this.$LAccessor = $LAccessor",
                                        firstToLower(reference.getName()),
                                        firstToLower(reference.getName())
                                )
                );

                builderBuilder.addMethod(constructorBuilder.build());
            }
            {
                MethodSpec.Builder constructorBuilder = MethodSpec
                        .constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TYPE_TABLE, FIELD_NAME_TABLE)
                        .addStatement("this.table = table");

                valueDef.getProperties().forEach(
                        property -> constructorBuilder
                                .addStatement(
                                        "this.$LAccessor = null",
                                        firstToLower(property.getName())
                                )
                );

                valueDef.getReferences().forEach(
                        reference -> constructorBuilder
                                .addStatement(
                                        "this.$LAccessor = null",
                                        firstToLower(reference.getName())
                                )
                );

                builderBuilder.addMethod(constructorBuilder.build());
            }
            // endregion constructors

            // region setters
            valueDef.getProperties().forEach(property -> {
                String name = String.format(
                        "set%sAccessor",
                        firstToUpper(property.getName())
                );
                TypeName returnType = ParameterizedTypeName.get(
                        ClassName.bestGuess("Builder"),
                        R
                );
                String builderParams = Stream.of(
                        Stream.of("this.table"),
                        valueDef.getProperties()
                                .stream()
                                .map(prop -> {
                                    if (prop.getName().equals(property.getName())) {
                                        return firstToLower(prop.getName()) + "Accessor";
                                    }
                                    return "this." + firstToLower(prop.getName()) + "Accessor";
                                }),
                        valueDef.getReferences()
                                .stream()
                                .map(ref -> "this." + firstToLower(ref.getName()) + "Accessor")
                )
                        .flatMap(Function.identity())
                        .collect(Collectors.joining(", "));

                builderBuilder.addMethod(
                        MethodSpec.methodBuilder(name)
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(
                                        typeJooqFieldAccessor(property.getType()),
                                        firstToLower(property.getName()) + "Accessor"
                                )
                                .returns(returnType)
                                .addStatement(
                                        "return new Builder<>($L)",
                                        builderParams
                                )
                                .build()
                );

                builderBuilder.addMethod(
                        MethodSpec
                                .methodBuilder(
                                        String.format(
                                                "without%s",
                                                firstToUpper(property.getName())
                                        )
                                )
                                .addModifiers(Modifier.PUBLIC)
                                .returns(returnType)
                                .addStatement(
                                        "return $L(new $T<>())",
                                        name,
                                        ClassName.get(JooqFieldAccessor.NoOpAccessor.class)
                                )
                                .build()
                );

                builderBuilder.addMethod(
                        MethodSpec.methodBuilder(name)
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(
                                        ParameterizedTypeName.get(
                                                ClassName.get(TableField.class),
                                                R,
                                                property.getType()
                                        ),
                                        "field"
                                )
                                .returns(returnType)
                                .addStatement(
                                        "return $L($T.jooqField(field))",
                                        name,
                                        Jooq.class
                                )
                                .build()
                );

                TypeVariableName varF = TypeVariableName.get("F");
                builderBuilder.addMethod(
                        MethodSpec.methodBuilder(name)
                                .addModifiers(Modifier.PUBLIC)
                                .addTypeVariable(varF)
                                .addParameter(
                                        ParameterizedTypeName.get(
                                                ClassName.get(TableField.class),
                                                R,
                                                varF
                                        ),
                                        "field"
                                )
                                .addParameter(
                                        ParameterizedTypeName.get(
                                                ClassName.get(Function.class),
                                                property.getType(),
                                                varF
                                        ),
                                        "write"
                                )
                                .addParameter(
                                        ParameterizedTypeName.get(
                                                ClassName.get(Function.class),
                                                varF,
                                                property.getType()
                                        ),
                                        "extract"
                                )
                                .returns(returnType)
                                .addStatement(
                                        "return $L($T.jooqField(field, write, extract))",
                                        name,
                                        Jooq.class
                                )
                                .build()
                );
            });
            valueDef.getReferences().forEach(reference -> {
                String name = String.format(
                        "set%sAccessor",
                        firstToUpper(reference.getName())
                );
                TypeName returnType = ParameterizedTypeName.get(
                        ClassName.bestGuess("Builder"),
                        R
                );
                String builderParams = Stream.of(
                        Stream.of("this.table"),
                        valueDef.getProperties()
                                .stream()
                                .map(prop -> "this." + firstToLower(prop.getName()) + "Accessor"),
                        valueDef.getReferences()
                                .stream()
                                .map(ref -> {
                                    if (ref.getName().equals(reference.getName())) {
                                        return firstToLower(ref.getName()) + "Accessor";
                                    }
                                    return "this." + firstToLower(ref.getName()) + "Accessor";
                                })
                )
                        .flatMap(Function.identity())
                        .collect(Collectors.joining(", "));

                builderBuilder.addMethod(
                        MethodSpec.methodBuilder(name)
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(
                                        typeJooqReferenceAccessor(
                                                WildcardTypeName.subtypeOf(
                                                        TypeName.get(Object.class)
                                                ),
                                                reference.getType()
                                        ),
                                        firstToLower(reference.getName()) + "Accessor"
                                )
                                .returns(returnType)
                                .addStatement(
                                        "return new Builder<>($L)",
                                        builderParams
                                )
                                .build()
                );

                builderBuilder.addMethod(
                        MethodSpec
                                .methodBuilder(
                                        String.format(
                                                "without%s",
                                                firstToUpper(reference.getName())
                                        )
                                )
                                .addModifiers(Modifier.PUBLIC)
                                .returns(returnType)
                                .addStatement(
                                        "return $L($T.noOp())",
                                        name,
                                        ClassName.get(JooqReferenceAccessor.class)
                                )
                                .build()
                );

                TypeVariableName varIdentityT = TypeVariableName.get("ID");
                builderBuilder.addMethod(
                        MethodSpec.methodBuilder(name)
                                .addModifiers(Modifier.PUBLIC)
                                .addTypeVariable(varIdentityT)
                                .addParameter(
                                        ParameterizedTypeName.get(
                                                ClassName.get(TableField.class),
                                                R,
                                                varIdentityT
                                        ),
                                        "field"
                                )
                                .addParameter(
                                        ParameterizedTypeName.get(
                                                ClassName.get(Function.class),
                                                reference.getType(),
                                                varIdentityT
                                        ),
                                        "idGetter"
                                )
                                .returns(returnType)
                                .addStatement(
                                        "return $L($T.jooqReference(field, idGetter))",
                                        name,
                                        Jooq.class
                                )
                                .build()
                );
            });
            // endregion setters

            // region build
            CodeBlock.Builder buildStatementBuilder = CodeBlock.builder()
                    .add("return new $T<>(this.table", ClassName.bestGuess(selfName));

            Stream
                    .concat(
                            valueDef.getProperties().stream(),
                            valueDef.getReferences().stream()
                    )
                    .map(PropertyDef::getName)
                    .map(this::firstToLower)
                    .map(name -> ", $T.requireNonNull(this." + name + "Accessor)")
                    .forEach(code -> buildStatementBuilder.add(code, Objects.class));
            valueDef.getReferences()
                    .forEach(ref -> buildStatementBuilder.add(", null"));


            buildStatementBuilder.add(")");

            builderBuilder.addMethod(
                    MethodSpec.methodBuilder("build")
                            .addModifiers(Modifier.PUBLIC)
                            .returns(
                                    ParameterizedTypeName.get(
                                            ClassName.bestGuess(selfName),
                                            R
                                    )
                            )
                            .addStatement(buildStatementBuilder.build())
                            .build()
            );
            // endregion build

            mapperBuilder.addType(builderBuilder.build());

        }
        // endregion Builder

        return Optional.of(mapperBuilder.build());
    }

    private TypeName typeJooqFieldAccessor(TypeName field) {
        return ParameterizedTypeName.get(
                ClassName.get(JooqFieldAccessor.class),
                R,
                field
        );
    }

    private TypeName typeJooqReferenceAccessor(TypeName idField, TypeName referred) {
        return ParameterizedTypeName.get(
                ClassName.get(JooqReferenceAccessor.class),
                R,
                idField,
                referred
        );
    }

    private TypeName typeRecordMapper(TypeName mappedType) {
        return ParameterizedTypeName.get(
                ClassName.get(RecordMapper.class),
                WildcardTypeName.subtypeOf(Record.class),
                mappedType
        );
    }

    private String firstToUpper(String input) {
        return input.substring(0, 1).toUpperCase()
                + input.substring(1);
    }

    private String firstToLower(String input) {
        return input.substring(0, 1).toLowerCase()
                + input.substring(1);
    }
}
