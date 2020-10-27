package fi.jubic.easymapper;

public interface FieldAccessor<R, F> extends FieldExtractor<R, F>, FieldWriter<R, F> {
    /**
     * A accessor implementation can tell the mapper if the extracted
     * value should be mapped or not by overriding this method.
     *
     * @return false if the extracted value should not be mapped
     */
    default boolean shouldExtract() {
        return true;
    }

    class NoOpAccessor<R, F> implements FieldAccessor<R, F> {
        @Override
        public boolean shouldExtract() {
            return false;
        }

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
