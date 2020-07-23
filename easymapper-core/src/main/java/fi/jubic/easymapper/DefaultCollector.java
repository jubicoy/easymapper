package fi.jubic.easymapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public interface DefaultCollector<R, T> extends Collector<R, List<T>, List<T>> {
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
        return intermediateList -> intermediateList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    default Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
