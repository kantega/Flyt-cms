package no.kantega.openaksess.search.query;

public class FieldCriterion extends Criterion {
    private final String field;
    private final String query;

    public FieldCriterion(String field, String query) {
        this.field = field;
        this.query = query;
    }

    @Override
    public String getCriterionAsString() {
        return field + ":\"" + query + "\"";
    }
}
