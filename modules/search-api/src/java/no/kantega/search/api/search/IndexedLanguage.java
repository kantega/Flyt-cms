package no.kantega.search.api.search;

/**
 * Textfields are indexed differently if the language specified on the indexed page
 * is English. To search in english content english language have to be specified.
 */
public enum IndexedLanguage {
    NO("no"), EN("en");
    public final String code;

    IndexedLanguage(String code){
        this.code = code;
    }
}
