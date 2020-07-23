package fi.jubic.easymapper;

public interface ReferenceExtractor<R, T, B, M extends Mapper<R, T, B>> {
    T extract(R input, M mapper) throws MappingException;
}
