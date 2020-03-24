package fi.jubic.easymapper.jooq;

import org.jooq.Record;
import org.jooq.TableField;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.function.Function;

public final class Jooq {
    public static <F, R extends Record> JooqFieldAccessor<R, F> jooqField(
            TableField<R, F> field
    ) {
        return new PlainJooqFieldAccessor<>(field);
    }

    public static <R extends Record, F, RecordT> JooqFieldAccessor<R, F> jooqField(
            TableField<R, RecordT> field,
            Function<F, RecordT> writeTransform,
            Function<RecordT, F> extractTransform
    ) {
        return new TransformingJooqFieldAccessor<>(
                field,
                writeTransform,
                extractTransform
        );
    }

    public static <R extends Record> JooqFieldAccessor<R, Instant> jooqTimestampField(
            TableField<R, Timestamp> field
    ) {
        return jooqField(
                field,
                Timestamp::from,
                Timestamp::toInstant
        );
    }

    public static <R extends Record, IdentityT, T>
            JooqReferenceAccessor<R, IdentityT, T> jooqReference(
            TableField<R, IdentityT> field,
            Function<T, IdentityT> idGetter
    ) {
        return new JooqReferenceAccessor<>(idGetter, jooqField(field));
    }
}
