package fi.jubic.easymapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
     */
    T map(R source) throws MappingException;

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