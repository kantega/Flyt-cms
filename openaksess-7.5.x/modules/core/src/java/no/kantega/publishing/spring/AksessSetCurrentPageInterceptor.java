package no.kantega.publishing.spring;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.ContentIdHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.util.RequestHelper;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor which sets specified page as current page in request. Can be used to link a Spring controller with
 * a published page
 */
public class AksessSetCurrentPageInterceptor extends HandlerInterceptorAdapter {
    private String aksessAlias;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ContentManagementService cms = new ContentManagementService(request);
        try {
            ContentIdentifier cid = ContentIdHelper.fromRequestAndUrl(request, aksessAlias);
            Content currentPage = cms.getContent(cid, true);
            RequestHelper.setRequestAttributes(request, currentPage);
        } catch (NotAuthorizedException e) {
            Log.error(this.getClass().getName(), e);
        } catch (ContentNotFoundException e) {
            Log.error(this.getClass().getName(), e);
        }
        return true;
    }

    public void setAksessAlias(String aksessAlias) {
        this.aksessAlias = aksessAlias;
    }
}