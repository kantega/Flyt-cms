package no.kantega.search.api.util;

public class FieldUtils {
    public static String getLanguageSuffix(String language) {
        if("eng".equals(language)){
            return "en";
        }else {
            return "no";
        }
    }
}
