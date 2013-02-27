package no.kantega.search.api.util;

public class FieldUtils {
    public static String getLanguageSuffix(String language) {
        if(language.equals("eng")){
            return "en";
        }else {
            return "no";
        }
    }
}
