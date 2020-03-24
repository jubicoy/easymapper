package fi.jubic.easymapper.jooq;

import fi.jubic.easymapper.Mangler;
import fi.jubic.easymapper.Mapper;
import fi.jubic.easymapper.MappingException;
import org.jooq.Record;
import org.jooq.Table;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public interface RecordMapper<R extends Record, T>
        extends Mapper<Record, T>, Mangler<Record, R, T> {
    RecordMapper<R, T> alias(Table<R> alias);

    Table<R> table();

    default <IdentityT> Collector<Record, ?, List<T>> partitionAndFlatten(
            JooqFieldAccessor<R, IdentityT> partitionKeyAccessor,
            Table<R> table,
            Collector<Record, ?, Optional<T>> collector
    ) {
        return Collectors.collectingAndThen(
                Collectors.groupingBy(
                        record -> {
                            try {
                                return partitionKeyAccessor.extract(record.into(table));
                            }
                            catch (MappingException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        collector
                ),
                map -> map.values().stream()
                        .map(optValue -> optValue.orElseThrow(
                                () -> new IllegalStateException(
                                        "Parsed empty when collecting many."
                                )
                        ))
                        .collect(Collectors.toList())
        );
    }

    @Override
    default BiConsumer<List<T>, Record> accumulator() {
        return (list, record) -> list.add(map(record));
    }
}
