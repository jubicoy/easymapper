package fi.jubic.easymapper.jooq;

import fi.jubic.easymapper.Mangler;
import fi.jubic.easymapper.Mapper;
import org.jooq.Record;
import org.jooq.Table;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public interface RecordMapper<R extends Record, T, B>
        extends Mapper<Record, T, B>, Mangler<Record, R, T> {
    RecordMapper<R, T, B> alias(Table<R> alias);

    Table<R> table();

    default <IdentityT> Collector<Record, ?, List<T>> partitionAndFlatten(
            JooqFieldAccessor<R, IdentityT> partitionKeyAccessor,
            Table<R> table,
            Collector<Record, ?, Optional<T>> collector
    ) {
        return Collectors.collectingAndThen(
                Collectors.groupingBy(
                        record -> Optional.ofNullable(
                                partitionKeyAccessor.extract(record.into(table))
                        ),
                        collector
                ),
                map -> map.entrySet()
                        .stream()
                        .filter(entry -> entry.getKey().isPresent())
                        .map(Map.Entry::getValue)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList())

        );
    }
}
