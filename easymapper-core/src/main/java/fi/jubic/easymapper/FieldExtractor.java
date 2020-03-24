package fi.jubic.easymapper;

/**
 * Extracts value from a record.
 * @param <R> Type of record
 * @param <F> Type of value
 */
public interface FieldExtractor<R, F> {
    F extract(R input) throws MappingException;
}
