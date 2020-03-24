package fi.jubic.easymapper;

/**
 * Base value writer.
 *
 * @param <R> Input record type.
 * @param <RecordT> Output record type. Can be set to same value as R.
 * @param <T> Value type.
 */
public interface Mangler<R, RecordT, T> {
    RecordT write(R output, T value) throws MappingException;
}
