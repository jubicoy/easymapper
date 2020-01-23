package fi.jubic.easymapper.jooq;

import fi.jubic.easymapper.Mangler;
import fi.jubic.easymapper.Mapper;
import fi.jubic.easymapper.MappingException;
import org.jooq.Record;
import org.jooq.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public interface TableMapper<R extends Record, T>
        extends Mapper<Record, T>, Mangler<Record, R, T> {
    TableMapper<R, T> alias(Table<R> alias);
    Table<R> table();

    default <ID> Collector<Record, ?, List<T>> partitionAndFlatten(
            JooqFieldAccessor<R, ID> partitionKeyAccessor,
            Table<R> table,
            Collector<Record, ?, T> collector
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
                map -> new ArrayList<>(map.values())
        );
    }

    @Override
    default BiConsumer<List<T>, Record> accumulator() {
        return (list, record) -> list.add(map(record));
    }
}
