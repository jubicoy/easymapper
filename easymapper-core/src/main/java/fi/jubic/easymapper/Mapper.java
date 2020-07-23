package fi.jubic.easymapper;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collector;

/**
 * Base value converter.
 *
 * @param <R> Record type
 * @param <T> Value type
 * @param <B> Intermediate representation
 */
public interface Mapper<R, T, B> extends DefaultCollector<R, T> {
    /**
     * Map value from flat source type R to object T.
     * @param source Source record
     * @throws MappingException if mapping fails.
     */
    T map(R source) throws MappingException;

    /**
     * Collect in intermediate format.
     * @param source source record
     * @return mapped value in intermediate representation
     * @throws MappingException if mapping fails
     */
    B intermediateMap(R source) throws MappingException;

    /**
     * Finalize the intermediate representation to final value representation.
     * @param intermediate the intermediate value representation
     * @return final value representation
     */
    T finalize(B intermediate);

    /**
     * Get an intermediate value representation collector.
     */
    default Collector<R, List<B>, List<B>> intermediateCollector() {
        return (DefaultCollector<R, B>) () -> (list, record) -> list.add(intermediateMap(record));
    }

    @Override
    default BiConsumer<List<T>, R> accumulator() {
        return (list, record) -> list.add(map(record));
    }
}
