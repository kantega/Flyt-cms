package no.kantega.openaksess.search.provider.transformer;

import org.apache.commons.lang.StringUtils;

public class LocationUtil {
    public static String locationWithouthSiteIdAndTrailingSlash(String path, int siteId) {
        String cleanedPath = StringUtils.substringAfter(path, "/" + siteId);
        if(cleanedPath.endsWith("/") && cleanedPath.length() > 1){
            cleanedPath = StringUtils.substringBeforeLast(cleanedPath, "/");
        }
        return cleanedPath;
    }
}
