package no.kantega.openaksess.search.index;

import java.util.Map;

/*
 * In some cases special indexing of certain fields is required, for instance to do phonetic indexing
 * of names. In order to tell the <code>ContentTransformer</code> to send the attribute value with the
 * appropriate field name specify a bean of this class with mappings on the form
 * "contenttemplatedbid.attributename" -> index field. e.g. "90.telefon" -> telefon_tlf.
 */
public class ContentAttributeNameToIndexFieldMapping {
    private Map<String, String> mappings;

    public Map<String, String> getMappings() {
        return mappings;
    }

    public void setMappings(Map<String, String> mappings) {
        this.mappings = mappings;
    }
}
