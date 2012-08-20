package no.kantega.openaksess.search.taglib.label;

import no.kantega.commons.util.LocaleLabels;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Locale;

public class FacetLabelTagLib extends TagSupport {

    private FacetLabelResolver labelResolver;

    private String key;
    private String bundle = LocaleLabels.DEFAULT_BUNDLE;

    @Override
    public int doStartTag() throws JspException {
        initIfNotAlreadyDone();

        String label = labelResolver.resolveLabel(key, bundle, getLocale());
        if(label == null) label = key;
        writeLabel(label);

        return SKIP_BODY;
    }

    private void writeLabel(String label) throws JspException {
        try {
            pageContext.getOut().print(label);
        } catch (IOException e) {
            throw new JspException("ERROR: FacetLabelResolver", e);
        }
    }

    private Locale getLocale() {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        Locale locale = (Locale)request.getAttribute("aksess_locale");
        if (locale == null) {
            locale = new Locale("no", "NO");
        }
        return locale;
    }

    @Override
    public int doEndTag() throws JspException {
        key = null;
        bundle =  LocaleLabels.DEFAULT_BUNDLE;
        return super.doEndTag();
    }

    private void throwIfNotLengthTwo(String key, String[] labelTypeAndKey) {
        if(labelTypeAndKey.length != 2){
            throw new IllegalArgumentException(String.format("Label key %s was not on the form type.key", key));
        }
    }

    private void initIfNotAlreadyDone() {
        if (labelResolver == null) {
            doInit();
        }
    }

    private synchronized void doInit() {
        WebApplicationContext requiredWebApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
        labelResolver = requiredWebApplicationContext.getBean(FacetLabelResolver.class);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }
}
