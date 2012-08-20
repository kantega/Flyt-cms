package no.kantega.openaksess.search.provider.transformer;

import org.apache.commons.lang.StringUtils;

public class LocationUtil {
    public static String locationWithoutTrailingSlash(String path) {
        String cleanedPath = path;
        if(cleanedPath.endsWith("/") && cleanedPath.length() > 2){
            cleanedPath = StringUtils.substringBeforeLast(cleanedPath, "/");
        }
        return cleanedPath;
    }
}
