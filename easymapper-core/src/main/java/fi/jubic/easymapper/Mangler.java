package fi.jubic.easymapper;

/**
 * Base value writer.
 */
public interface Mangler<R, T> {
    R write(R output, T value) throws MappingException;
}
