package fi.jubic.easymapper.jooq;

import fi.jubic.easymapper.FieldWriter;
import fi.jubic.easymapper.MappingException;
import fi.jubic.easymapper.ReferenceExtractor;
import org.jooq.Record;
import org.jooq.Table;

import java.util.Optional;
import java.util.function.Function;

public class JooqReferenceAccessor<R extends Record, IdentityT, T>
        implements FieldWriter<R, T>,
        ReferenceExtractor<Record, T, RecordMapper<R, T>> {
    private final Function<T, IdentityT> idGetter;
    private final JooqFieldAccessor<R, IdentityT> idAccessor;

    public JooqReferenceAccessor(
            Function<T, IdentityT> idGetter,
            JooqFieldAccessor<R, IdentityT> idAccessor
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
    public T extract(Record input, RecordMapper<R, T> mapper) throws MappingException {
        return mapper.map(input);
    }

    public JooqReferenceAccessor<R, IdentityT, T> alias(Table<R> tableAlias) {
        return new JooqReferenceAccessor<>(
                idGetter,
                idAccessor.alias(tableAlias)
        );
    }

    public static <R extends Record, IdentityT, T> JooqReferenceAccessor<R, IdentityT, T> noOp() {
        return new JooqReferenceAccessor<>(
                ignore -> null,
                new JooqFieldAccessor.NoOpAccessor<>()
        );
    }
}
