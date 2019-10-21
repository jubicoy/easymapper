package fi.jubic.easymapper.generator.def;

import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Optional;

public class ValueDef {
    private final String name;
    private final TypeElement element;

    private final PropertyDef id;

    private final List<PropertyDef> properties;
    private final List<PropertyDef> references;
    private final List<PropertyDef> collectionReferences;

    private final TypeElement builder;

    public ValueDef(
            String name,
            TypeElement element,
            PropertyDef id,
            List<PropertyDef> properties,
            List<PropertyDef> references,
            List<PropertyDef> collectionReferences,
            TypeElement builder
    ) {
        this.name = name;
        this.element = element;
        this.id = id;
        this.properties = properties;
        this.references = references;
        this.collectionReferences = collectionReferences;
        this.builder = builder;
    }

    public String getName() {
        return name;
    }

    public TypeElement getElement() {
        return element;
    }

    public Optional<PropertyDef> getId() {
        return Optional.ofNullable(id);
    }

    public List<PropertyDef> getProperties() {
        return properties;
    }

    public List<PropertyDef> getReferences() {
        return references;
    }

    public List<PropertyDef> getCollectionReferences() {
        return collectionReferences;
    }

    public TypeElement getBuilder() {
        return builder;
    }
}
