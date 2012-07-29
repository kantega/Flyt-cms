package no.kantega.search.api.retrieve;

public interface DocumentRetriever<D> {
    public String getSupportedContentType();

    D getObjectById(int id);
}
