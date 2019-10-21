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

    public static <R extends Record, F, FF> JooqFieldAccessor<R, F> jooqField(
            TableField<R, FF> field,
            Function<F, FF> writeTransform,
            Function<FF, F> extractTransform
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

    public static <R extends Record, ID, T> JooqReferenceAccessor<R, ID, T> jooqReference(
            TableField<R, ID> field,
            Function<T, ID> idGetter
    ) {
        return new JooqReferenceAccessor<>(idGetter, jooqField(field));
    }
}
