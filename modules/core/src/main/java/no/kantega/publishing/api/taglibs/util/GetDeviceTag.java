package no.kantega.publishing.api.taglibs.util;

import no.kantega.publishing.client.device.DeviceCategory;
import no.kantega.publishing.client.device.DeviceCategoryDetector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

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
