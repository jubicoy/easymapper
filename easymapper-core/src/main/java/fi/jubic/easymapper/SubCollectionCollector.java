package fi.jubic.easymapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 *
 * @param <R> Source record
 * @param <T> Output type
 * @param <U> Sub-collection type
 * @param <I> Intermediate sub-collection type
 */
public class SubCollectionCollector<R, T, U, I>
        implements Collector<R, IntermediateSubCollectionResult<T, I>, T> {

    private final Mapper<R, T> rootMapper;
    private final Collector<R, I, List<U>> subCollector;
    private final BiFunction<T, List<U>, T> finisher;

    public SubCollectionCollector(
            Mapper<R, T> rootMapper,
            Collector<R, I, List<U>> subCollector,
            BiFunction<T, List<U>, T> finisher
    ) {
        this.rootMapper = rootMapper;
        this.subCollector = subCollector;
        this.finisher = finisher;
    }


    @Override
    public Supplier<IntermediateSubCollectionResult<T, I>> supplier() {
        return () -> new IntermediateSubCollectionResult<>(
                null,
                subCollector.supplier().get()
        );
    }

    @Override
    public BiConsumer<IntermediateSubCollectionResult<T, I>, R> accumulator() {
        return (result, source) -> {
            if (result.getIntermediateRoot() == null) {
                result.setIntermediateRoot(rootMapper.map(source));
            }
            subCollector.accumulator().accept(result.getSubCollection(), source);
        };
    }

    @Override
    public BinaryOperator<IntermediateSubCollectionResult<T, I>> combiner() {
        return (a, b) -> new IntermediateSubCollectionResult<>(
                Optional.ofNullable(a.getIntermediateRoot()).orElse(b.getIntermediateRoot()),
                subCollector.combiner().apply(a.getSubCollection(), b.getSubCollection())
        );
    }

    @Override
    public Function<IntermediateSubCollectionResult<T, I>, T> finisher() {
        return intermediateResult -> {
            if (intermediateResult.getIntermediateRoot() == null) {
                return null;
            }
            return finisher.apply(
                    intermediateResult.getIntermediateRoot(),
                    subCollector.finisher().apply(intermediateResult.getSubCollection())
            );
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
