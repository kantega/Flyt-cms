package no.kantega.openaksess.search.query;

/**
 * Class that is used to limit a search to a given associationId.
 */
public class AssociationIdCriterion extends FieldCriterion{

    /**
     * Creates a criterion-object that will search for pages with a given associationId.
     *
     * @param associationId The associationId to search for
     */

    public AssociationIdCriterion(int associationId) {
        super(Fields.ASSOCIATION_ID, String.valueOf(associationId));
    }
}
