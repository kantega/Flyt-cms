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

/**
 * Author: Kristian Lier Selnæs, Kantega AS
 * Date: 02.jul.2009
 * Time: 08:48:04
 */
public abstract class AdminController implements Controller {


    public final ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(true);
        ContentManagementService aksessService = new ContentManagementService(request);
        RequestParameters param = new RequestParameters(request);

        String url = param.getString(AdminRequestParameters.URL);

        Content current = (Content)session.getAttribute(AdminSessionAttributes.CURRENT_CONTENT);

        if (url != null || request.getParameter(AdminRequestParameters.THIS_ID) != null || request.getParameter(AdminRequestParameters.CONTENT_ID) != null) {
            ContentIdentifier cid = null;
            if (url != null) {
                cid = new ContentIdentifier(request, url);
            } else {
                cid = new ContentIdentifier(request);
            }
            current = aksessService.getContent(cid);
        }

        if (current == null ) {
            // No current object, go to start page
            ContentIdentifier cid = new ContentIdentifier(request, "/");
            current = aksessService.getContent(cid);
        }


        // Updating the session with the current content object
        session.setAttribute(AdminSessionAttributes.CURRENT_CONTENT, current);
        session.setAttribute(AdminSessionAttributes.SHOW_CONTENT, current);


        return handleRequestInternal(request, response);
    }

    public abstract ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response);

}
