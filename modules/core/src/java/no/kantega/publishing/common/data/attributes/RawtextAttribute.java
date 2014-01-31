package no.kantega.publishing.common.data.attributes;

/**
 * Custom Aksess attribute.
 * Raw text without formatting. Used for publishing tracking scripts.
 */

public class RawtextAttribute extends Attribute {

    @Override
    public String getRenderer() {
        return "rawtext";
    }

    @Override
    public boolean isSearchable() {
        return false;
    }
}
