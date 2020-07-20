package fi.jubic.easymapper;

public interface FieldAccessor<R, F> extends FieldExtractor<R, F>, FieldWriter<R, F> {
    class NoOpAccessor<R, F> implements FieldAccessor<R, F> {
        @Override
        public F extract(R input) throws MappingException {
            return null;
        }

        @Override
        public R write(R output, F value) throws MappingException {
            return output;
        }
    }
}
