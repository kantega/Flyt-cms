package no.kantega.search.criteria;

import no.kantega.search.index.Fields;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 * Class that is used to limit a search to a given associationId.
 */
public class AssociationIdCriterion extends AbstractCriterion{

    private int associationId;

    /**
     * Creates a criterion-object that will search for pages with a given associationId.
     *
     * @param associationId The associationId to search for
     */

    public AssociationIdCriterion(int associationId ) {
        this.associationId = associationId;
    }

    /**
     * {@inheritDoc}
     */
    public Query getQuery() {
        Query query;
        String associtionStr = Integer.toString(associationId);
        query = new TermQuery(new Term(Fields.ASSOCIATION_ID, associtionStr));
        return query;
    }
}
