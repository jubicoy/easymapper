package fi.jubic.easymapper.jooqtest;

import fi.jubic.easymapper.annotations.EasyId;
import fi.jubic.easyvalue.EasyValue;

import java.util.List;

@EasyValue
public abstract class TestClass {
    @EasyId
    public abstract Long getId();

    public abstract String getName();

    public abstract TestClass getTwin();

    public abstract List<TestClass> getSiblings();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends EasyValue_TestClass.Builder {

    }
}
