package fi.jubic.easymapper;

import java.util.List;
import java.util.Optional;
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
    default T map(R source) throws MappingException {
        return Optional.ofNullable(intermediateMap(source))
                .map(this::finalize)
                .orElse(null);
    }

    /**
     * Map record to optional value or empty if mapping return null of throws a
     * {@link MappingException}.
     * @param source Source record
     */
    default Optional<T> mapOptional(R source) {
        try {
            return Optional.ofNullable(map(source));
        }
        catch (MappingException ignore) {
            return Optional.empty();
        }
    }

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
