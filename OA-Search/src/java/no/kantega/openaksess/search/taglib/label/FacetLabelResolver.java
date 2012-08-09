package no.kantega.openaksess.search.taglib.label;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.openaksess.search.taglib.label.resolver.LabelResolver;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * AksessSearchTermTranslator
 */
public class FacetLabelResolver extends TagSupport {

    private Map<String, LabelResolver> labelResolvers;
    private Ehcache facetLabelCache;
    private Map<String,String> facetValueToLabelKeys;

    private String key;
    private String bundle = LocaleLabels.DEFAULT_BUNDLE;

    @Override
    public int doStartTag() throws JspException {
        initIfNotAlreadyDone();

        String label = resolveLabel();
        if(label == null) label = key;
        writeLabel(label);

        return SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        key = null;
        bundle =  LocaleLabels.DEFAULT_BUNDLE;
        return super.doEndTag();
    }

    private void writeLabel(String label) throws JspException {
        try {
            pageContext.getOut().print(label);
        } catch (IOException e) {
            throw new JspException("ERROR: FacetLabelResolver", e);
        }
    }

    private String resolveLabel() {
        String label;Locale locale = getLocale();
        String language = locale.getLanguage();
        String cacheKey = key.concat(language);
        Element element = facetLabelCache.get(cacheKey);
        if(element == null){
            if(facetValueToLabelKeys.containsKey(key)){
                key = facetValueToLabelKeys.get(key);
                label = LocaleLabels.getLabel(key, bundle, locale);
            }else {
                label = resolveLabelFromLabelResolvers(key);
            }
            facetLabelCache.put(new Element(cacheKey, label));
        }else {
            label = (String) element.getValue();
        }
        return label;
    }

    private Locale getLocale() {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        Locale locale = (Locale)request.getAttribute("aksess_locale");
        if (locale == null) {
            locale = new Locale("no", "NO");
        }
        return locale;
    }

    private String resolveLabelFromLabelResolvers(String key) {
        String[] labelTypeAndKey = key.split("\\.");

        throwIfNotLengthTwo(key, labelTypeAndKey);

        LabelResolver labelResolver = labelResolvers.get(labelTypeAndKey[0]);
        if(labelResolver == null){
            return null;
        }

        return labelResolver.resolveLabel(labelTypeAndKey[1]);
    }

    public void setLabelResolvers(Collection<LabelResolver> labelResolvers){
        this.labelResolvers = new HashMap<String, LabelResolver>();
        for (LabelResolver labelResolver : labelResolvers) {
            this.labelResolvers.put(labelResolver.handledPrefix(), labelResolver);
        }
    }

    private void throwIfNotLengthTwo(String key, String[] labelTypeAndKey) {
        if(labelTypeAndKey.length != 2){
            throw new IllegalArgumentException(String.format("Label key %s was not on the form type.key", key));
        }
    }

    private void initIfNotAlreadyDone() {
        if (labelResolvers == null) {
            doInit();
        }
    }

    private synchronized void doInit() {
        WebApplicationContext requiredWebApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
        Map<String, LabelResolver> beansOfType = requiredWebApplicationContext.getBeansOfType(LabelResolver.class);
        setLabelResolvers(beansOfType.values());
        facetValueToLabelKeys = requiredWebApplicationContext.getBean("facetValueToLabelKeys", Map.class);
        CacheManager cacheManager = requiredWebApplicationContext.getBean(CacheManager.class);
        facetLabelCache = cacheManager.getEhcache("FacetLabelCache");
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }
}
