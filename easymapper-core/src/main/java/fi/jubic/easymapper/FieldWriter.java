package fi.jubic.easymapper;

/**
 * Writes value to a record.
 * @param <R> Type of record
 * @param <F> Type of value
 */
public interface FieldWriter<R, F> {
    R write(R output, F value) throws MappingException;
}
