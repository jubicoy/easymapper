package fi.jubic.easymapper.jooq;

import fi.jubic.easymapper.MappingException;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import java.util.Optional;
import java.util.function.Function;

public class TransformingJooqFieldAccessor<R extends Record, F, FieldT>
        implements JooqFieldAccessor<R, F> {
    private final TableField<R, FieldT> field;
    private final Function<F, FieldT> writeTransform;
    private final Function<FieldT, F> extractTransform;

    public TransformingJooqFieldAccessor(
            TableField<R, FieldT> field,
            Function<F, FieldT> writeTransform,
            Function<FieldT, F> extractTransform
    ) {
        this.field = field;
        this.writeTransform = writeTransform;
        this.extractTransform = extractTransform;
    }

    @Override
    public R write(R output, F value) throws MappingException {
        output.set(
                field,
                Optional.ofNullable(value)
                        .map(writeTransform)
                        .orElse(null)
        );
        return output;
    }

    @Override
    public F extract(R input) throws MappingException {
        return Optional.ofNullable(input.get(field))
                .map(extractTransform)
                .orElse(null);
    }

    @Override
    public JooqFieldAccessor<R, F> alias(Table<R> tableAlias) {
        return new TransformingJooqFieldAccessor<>(
                (TableField<R, FieldT>) DSL.field(
                        tableAlias.getQualifiedName().append(field.getQualifiedName().last()),
                        field.getDataType()
                ),
                writeTransform,
                extractTransform
        );
    }
}
