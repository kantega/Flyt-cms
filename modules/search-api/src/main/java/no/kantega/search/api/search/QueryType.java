package no.kantega.search.api.search;

/**
 * Determine how to handle the content of a <code>SearchQuery</code>
 */
public enum QueryType {
    /**
     * The default way to handle a query is to search the field containing all
     * analyzed content and boost results by the content in selected raw fields.
     * Typical query string: «fox jumping»
     */
    Default,
    /**
     * If you need to query specific fields you need to use the Lucene <code>QueryType</code>.
     *  Typical query string: «title_no:fox jumping»
     */
    Lucene
}
