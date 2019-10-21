package fi.jubic.easymapper.jooq;

import fi.jubic.easymapper.FieldAccessor;
import org.jooq.Record;
import org.jooq.Table;

public interface JooqFieldAccessor<R extends Record, F> extends FieldAccessor<R, F> {
    JooqFieldAccessor<R, F> alias(Table<R> tableAlias);
}
