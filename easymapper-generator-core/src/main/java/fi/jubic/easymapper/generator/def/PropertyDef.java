package fi.jubic.easymapper.generator.def;

import com.squareup.javapoet.TypeName;

import javax.lang.model.element.ExecutableElement;

public class PropertyDef {
    private final ExecutableElement element;
    private final TypeName type;
    private final String name;

    public PropertyDef(
            ExecutableElement element,
            TypeName type,
            String name
    ) {
        this.element = element;
        this.type = type;
        this.name = name;
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
