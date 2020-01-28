package fi.jubic.easymapper.generator.def;

public enum PropertyAccess {
    Get("get"),
    Is("is");

    private final String name;

    PropertyAccess(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
