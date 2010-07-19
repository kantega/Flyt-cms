package no.kantega.publishing.common.service.factory;

import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.security.SecuritySession;

import javax.servlet.http.HttpServletRequest;

/**
 * User: Kristian Seln�s
 * Date: 06.mai.2010
 * Time: 08:35:26
 */
public interface AksessServiceFactory {

    public ContentManagementService getContentManagementService(HttpServletRequest request);

    public TopicMapService getTopicMapService(HttpServletRequest request);

    public MultimediaService getMultimediaService(SecuritySession securitySession);
}
