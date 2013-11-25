package no.kantega.openaksess.search.provider.transformer;

import no.kantega.publishing.common.data.Association;

public class LocationUtil {
    public static String locationWithoutTrailingSlash(Association association) {
        String pathWithSite = "/" + association.getSiteId() + association.getPath();


        return pathWithSite.endsWith("/") ?
                pathWithSite.substring(0, pathWithSite.length() - 1)
                : pathWithSite;
    }
}
