package no.kantega.commons.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Extends Springs handler mapping with possibility to match only specified URLs. Can be used
 * to add custom interceptors to specific URLs when using annotations 
 */
public class SelectedAnnotationHandlerMapping extends DefaultAnnotationHandlerMapping {
    private static final Logger log = LoggerFactory.getLogger(SelectedAnnotationHandlerMapping.class);
    private List<Pattern> urlPatterns;

    public void setUrls(List<String> urls) {
        urlPatterns = new ArrayList<Pattern>();
        for (String url : urls) {
            urlPatterns.add(Pattern.compile(url));
        }
    }

    public String[] getFiltered(String urls[]) {
        if (urls == null) {
            return null;
        }
        List<String> urlList = new ArrayList<String>();
        for (String str : urls) {
            for (Pattern urlPattern : urlPatterns) {
                try {
                    if (urlPattern.matcher(str).find()) {
                        urlList.add(str);
                        break;
                    }
                } catch (Exception e) {
                    log.error("Error getting filtered", e);
                }
            }
        }
        return urlList.toArray(new String[urlList.size()]);
    }

    protected String[] determineUrlsForHandler(String s) {
        return getFiltered(super.determineUrlsForHandler(s));
    }
}


