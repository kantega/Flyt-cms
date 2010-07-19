package no.kantega.publishing.admin.viewcontroller;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.exception.ExceptionHandler;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.log.Log;

import java.util.Map;
import java.util.HashMap;

/**
 * Author: Kristian Lier Selnæs, Kantega AS
 * Date: 02.jul.2009
 * Time: 08:48:04
 */
public abstract class AdminController implements Controller {


    public final ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = handleRequestInternal(request, response);
        Map model = modelAndView.getModel();
        if (model == null) {
            model = new HashMap();
        }
        model.put("aksess_locale", Aksess.getDefaultAdminLocale());

        String reqUri = request.getRequestURI();
        int start = reqUri.indexOf("/admin/");
        if (start != -1) {
            reqUri = reqUri.substring(start+"/admin/".length());
            reqUri = reqUri.substring(0, reqUri.indexOf("/"));
            model.put(reqUri + "Selected", "selected");
        }
        return modelAndView;
    }

    public abstract ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
