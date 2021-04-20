package fi.jubic.easymapper.jooq;

import fi.jubic.easymapper.MappingException;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

public class PlainJooqFieldAccessor<R extends Record, F> implements JooqFieldAccessor<R, F> {
    private final TableField<R, F> field;

    public PlainJooqFieldAccessor(TableField<R, F> field) {
        this.field = field;
    }

    @Override
    public R write(R output, F value) throws MappingException {
        output.set(field, value);
        return output;
    }

    @Override
    public F extract(R input) throws MappingException {
        return input.get(field);
    }

    @Override
    public JooqFieldAccessor<R, F> alias(Table<R> tableAlias) {
        return new PlainJooqFieldAccessor<>(
                (TableField<R, F>) DSL.field(
                        tableAlias.getQualifiedName().append(field.getQualifiedName().last()),
                        field.getDataType()
                )
        );
    }
}
