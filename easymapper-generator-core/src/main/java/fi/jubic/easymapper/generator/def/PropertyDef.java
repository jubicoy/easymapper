package fi.jubic.easymapper.generator.def;

import com.squareup.javapoet.TypeName;

import javax.lang.model.element.ExecutableElement;

public class PropertyDef {
    private final PropertyAccess access;
    private final ExecutableElement element;
    private final TypeName type;
    private final String name;

    public PropertyDef(
            PropertyAccess access,
            ExecutableElement element,
            TypeName type,
            String name
    ) {
        this.access = access;
        this.element = element;
        if (type.isPrimitive()) {
            this.type = type.box();
        }
        else {
            this.type = type;
        }
        this.name = name;
    }

    public PropertyAccess getAccess() {
        return access;
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
}
