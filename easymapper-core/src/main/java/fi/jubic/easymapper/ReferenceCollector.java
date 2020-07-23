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
 * Collector for reference values and value collections.
 *
 * @param <R> Source record
 * @param <T> Output type
 * @param <B> Intermediate representation type
 * @param <U> Reference collection type
 * @param <I> Intermediate reference collection type
 */
public class ReferenceCollector<R, T, B, U, I>
        implements Collector<R, IntermediateSubCollectionResult<B, I>, Optional<T>> {

    private final Mapper<R, T, B> rootMapper;
    private final Collector<R, I, U> subCollector;
    private final BiFunction<B, U, B> finisher;

    public ReferenceCollector(
            Mapper<R, T, B> rootMapper,
            Collector<R, I, U> subCollector,
            BiFunction<B, U, B> finisher
    ) {
        this.rootMapper = rootMapper;
        this.subCollector = subCollector;
        this.finisher = finisher;
    }


    @Override
    public Supplier<IntermediateSubCollectionResult<B, I>> supplier() {
        return () -> new IntermediateSubCollectionResult<>(
                null,
                subCollector.supplier().get()
        );
    }

    @Override
    public BiConsumer<IntermediateSubCollectionResult<B, I>, R> accumulator() {
        return (result, source) -> {
            if (result.getIntermediateRoot() == null) {
                result.setIntermediateRoot(rootMapper.intermediateMap(source));
            }
            subCollector.accumulator().accept(result.getSubCollection(), source);
        };
    }

    @Override
    public BinaryOperator<IntermediateSubCollectionResult<B, I>> combiner() {
        return (a, b) -> new IntermediateSubCollectionResult<>(
                Optional.ofNullable(a.getIntermediateRoot()).orElse(b.getIntermediateRoot()),
                subCollector.combiner().apply(a.getSubCollection(), b.getSubCollection())
        );
    }

    @Override
    public Function<IntermediateSubCollectionResult<B, I>, Optional<T>> finisher() {
        return intermediateResult -> Optional.ofNullable(intermediateResult.getIntermediateRoot())
                .map(root -> rootMapper.finalize(
                        finisher.apply(
                                root,
                                subCollector.finisher()
                                        .apply(intermediateResult.getSubCollection())
                        )
                ));
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
