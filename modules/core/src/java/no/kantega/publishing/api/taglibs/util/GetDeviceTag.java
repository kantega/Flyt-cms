package no.kantega.publishing.api.taglibs.util;

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.publishing.client.device.DeviceCategory;
import no.kantega.publishing.client.device.DeviceCategoryDetector;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.enums.Language;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class GetDeviceTag extends TagSupport {
    String var = null;

    public void setVar(String var) {
        this.var = var;
    }

    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        DeviceCategory deviceCategory = new DeviceCategoryDetector().getUserAgentDeviceCategory(request);
        request.setAttribute(var, deviceCategory.toString());
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }
}
