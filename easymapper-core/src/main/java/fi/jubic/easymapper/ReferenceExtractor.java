package fi.jubic.easymapper;

public interface ReferenceExtractor<R, T, M extends Mapper<R, T>> {
    T extract(R input, M mapper) throws MappingException;
}
