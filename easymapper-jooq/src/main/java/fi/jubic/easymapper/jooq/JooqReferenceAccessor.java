package fi.jubic.easymapper.jooq;

import fi.jubic.easymapper.FieldWriter;
import fi.jubic.easymapper.MappingException;
import fi.jubic.easymapper.ReferenceExtractor;
import org.jooq.Record;
import org.jooq.Table;

import java.util.Optional;
import java.util.function.Function;

public class JooqReferenceAccessor<R extends Record, ID, T>
        implements FieldWriter<R, T>,
        ReferenceExtractor<Record, T, TableMapper<R, T>> {
    private final Function<T, ID> idGetter;
    private final JooqFieldAccessor<R, ID> idAccessor;

    public JooqReferenceAccessor(
            Function<T, ID> idGetter,
            JooqFieldAccessor<R, ID> idAccessor
    ) {
        this.idGetter = idGetter;
        this.idAccessor = idAccessor;
    }

    @Override
    public R write(R output, T value) throws MappingException {
        return idAccessor.write(
                output,
                Optional.ofNullable(value)
                        .map(idGetter)
                        .orElse(null)
        );
    }

    @Override
    public T extract(Record input, TableMapper<R, T> mapper) throws MappingException {
        return mapper.map(input);
    }

    public JooqReferenceAccessor<R, ID, T> alias(Table<R> tableAlias) {
        return new JooqReferenceAccessor<>(
                idGetter,
                idAccessor.alias(tableAlias)
        );
    }
}
