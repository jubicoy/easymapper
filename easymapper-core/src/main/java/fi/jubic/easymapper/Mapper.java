package fi.jubic.easymapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Base value converter.
 *
 * @param <R> Record type
 * @param <T> Value type
 */
public interface Mapper<R, T> extends Collector<R, List<T>, List<T>> {
    /**
     * Map value from flat source type R to object T.
     * @param source Source record
     * @throws MappingException if mapping fails.
     */
    T map(R source) throws MappingException;

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

    @Override
    default Supplier<List<T>> supplier() {
        return ArrayList::new;
    }

    @Override
    default BinaryOperator<List<T>> combiner() {
        return (a, b) -> {
            ArrayList<T> combined = new ArrayList<>();

            combined.addAll(a);
            combined.addAll(b);

            return combined;
        };
    }

    @Override
    default Function<List<T>, List<T>> finisher() {
        return Collections::unmodifiableList;
    }

    @Override
    default Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}