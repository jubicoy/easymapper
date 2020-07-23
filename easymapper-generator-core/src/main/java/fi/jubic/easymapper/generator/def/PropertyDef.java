package fi.jubic.easymapper.generator.def;

import com.squareup.javapoet.TypeName;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Optional;

public class PropertyDef {
    private final PropertyAccess access;
    private final PropertyKind kind;
    private final ExecutableElement element;
    private final TypeName type;
    private final String name;

    private final TypeElement referenceElement;

    public PropertyDef(
            PropertyAccess access,
            PropertyKind kind,
            ExecutableElement element,
            TypeName type,
            String name,
            TypeElement referenceElement
    ) {
        this.access = access;
        this.kind = kind;
        this.element = element;
        if (type.isPrimitive()) {
            this.type = type.box();
        }
        else {
            this.type = type;
        }
        this.name = name;
        this.referenceElement = referenceElement;
    }

    public PropertyAccess getAccess() {
        return access;
    }

    public PropertyKind getKind() {
        return kind;
    }

    public ExecutableElement getElement() {
        return element;
    }

    public TypeName getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Optional<TypeElement> getReferenceElement() {
        return Optional.ofNullable(referenceElement);
    }
}
