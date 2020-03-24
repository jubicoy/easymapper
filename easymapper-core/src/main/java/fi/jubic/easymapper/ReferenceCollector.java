package fi.jubic.easymapper;

import java.util.Collections;
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
 * @param <U> Reference collection type
 * @param <I> Intermediate reference collection type
 */
public class ReferenceCollector<R, T, U, I>
        implements Collector<R, IntermediateSubCollectionResult<T, I>, Optional<T>> {

    private final Mapper<R, T> rootMapper;
    private final Collector<R, I, U> subCollector;
    private final BiFunction<T, U, T> finisher;

    public ReferenceCollector(
            Mapper<R, T> rootMapper,
            Collector<R, I, U> subCollector,
            BiFunction<T, U, T> finisher
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
    public Function<IntermediateSubCollectionResult<T, I>, Optional<T>> finisher() {
        return intermediateResult -> Optional.ofNullable(intermediateResult.getIntermediateRoot())
                .map(root -> finisher.apply(
                        root,
                        subCollector.finisher().apply(intermediateResult.getSubCollection())
                ));
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
