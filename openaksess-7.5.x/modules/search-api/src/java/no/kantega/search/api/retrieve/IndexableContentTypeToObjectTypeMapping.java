package no.kantega.search.api.retrieve;

/**
 * The values in no.kantega.publishing.common.data.enums.ObjectType is used when setting authorization
 * for objects. In order to be able to use the correct entry in object permissions this class is used to create
 * a mapping between the data from the index and the correct ObjectType.
 */
public class IndexableContentTypeToObjectTypeMapping {
    private final String indexableContentType;
    private final int objectType;

    public IndexableContentTypeToObjectTypeMapping(String indexableContentType, int objectType) {
        this.indexableContentType = indexableContentType;
        this.objectType = objectType;
    }

    public String getIndexableContentType() {
        return indexableContentType;
    }

    public int getObjectType() {
        return objectType;
    }
}
