package fi.jubic.easymapper.jooq;

import fi.jubic.easymapper.FieldAccessor;
import fi.jubic.easymapper.MappingException;
import org.jooq.Record;
import org.jooq.Table;

public interface JooqFieldAccessor<R extends Record, F> extends FieldAccessor<R, F> {
    JooqFieldAccessor<R, F> alias(Table<R> tableAlias);

    class NoOpAccessor<R extends Record, F> implements JooqFieldAccessor<R, F> {
        @Override
        public JooqFieldAccessor<R, F> alias(Table<R> tableAlias) {
            return this;
        }

        @Override
        public F extract(R input) throws MappingException {
            return null;
        }

        @Override
        public R write(R output, F value) throws MappingException {
            return output;
        }
    }
}
