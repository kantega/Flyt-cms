package no.kantega.commons.spring;

import no.kantega.commons.log.Log;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Extends Springs handler mapping with possibility to match only specified URLs. Can be used
 * to add custom interceptors to specific URLs when using annotations 
 */
public class SelectedAnnotationHandlerMapping extends DefaultAnnotationHandlerMapping {
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
                    Log.error(this.getClass().getName(), e);
                }
            }
        }
        return urlList.toArray(new String[urlList.size()]);
    }

    protected String[] determineUrlsForHandler(String s) {
        return getFiltered(super.determineUrlsForHandler(s));
    }
}


