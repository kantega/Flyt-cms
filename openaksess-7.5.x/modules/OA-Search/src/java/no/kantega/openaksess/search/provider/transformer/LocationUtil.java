package no.kantega.openaksess.search.provider.transformer;

import no.kantega.publishing.common.data.Association;
import org.apache.commons.lang.StringUtils;

public class LocationUtil {
    public static String locationWithoutTrailingSlash(Association association) {
        String cleanedPath = association.getPath();
        if(cleanedPath.contains("/")){
            cleanedPath = "/" + association.getSiteId();
        } else if(cleanedPath.endsWith("/") && cleanedPath.length() > 2){
            cleanedPath = StringUtils.substringBeforeLast(cleanedPath, "/");
        }
        return cleanedPath;
    }
}
