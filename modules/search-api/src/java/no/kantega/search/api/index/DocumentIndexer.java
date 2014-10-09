package no.kantega.search.api.index;

import no.kantega.search.api.IndexableDocument;

import java.util.List;

public interface DocumentIndexer {
    /**
     * Submit a document for indexing.
     * Do not force commit when the document is submitted.
     * @param document to be indexed
     */
    void indexDocument(IndexableDocument document);

    /**
     * Submit a document for indexing.
     * Force commit when the document is submitted.
     * @param document to be indexed
     */
    void indexDocumentAndCommit(IndexableDocument document);

    /**
     * Force a commit of the index.
     */
    void commit();

    /**
     * Delete documents identified by the given ids
     * @param Uids of the documents to be deleted.
     */
    void deleteByUid(List<String> Uids);

    /**
     * Delete all documents in the index.
     */
    void deleteAllDocuments();

    /**
     * Force a optimization of the index.
     */
    public void optimize();

    /**
     * Delete all documents with the given doctype
     * @param docType - the ContentType of the documents.
     */
    void deleteByDocType(String docType);
}
