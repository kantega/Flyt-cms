package no.kantega.publishing.admin.topicmaps.action;

import no.kantega.commons.log.Log;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.topicmaps.data.ImportedTopicMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SaveImportedTopicMapAction extends AbstractController {

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        TopicMapService topicService = new TopicMapService(request);
        ImportedTopicMap importedTopicMap = getImportedTopicMapFromSession(request);
        if(importedTopicMap != null){
            topicService.saveImportedTopicMap(importedTopicMap);
            removeImportedTopicMapFromSession(request);
        }else{
            Log.error(this.getClass().getName(),"Error getting importedTopicMap from session");
        }
        return new ModelAndView(new RedirectView("ListTopicMaps.action"));
    }

    private void removeImportedTopicMapFromSession(HttpServletRequest request) {
        request.getSession().removeAttribute(ImportTopicMapAction.IMPORETED_TOPICMAP_SESSION_KEY);
    }

    private ImportedTopicMap getImportedTopicMapFromSession(HttpServletRequest request) {
        ImportedTopicMap importedTopicMap = (ImportedTopicMap)request.getSession().getAttribute(ImportTopicMapAction.IMPORETED_TOPICMAP_SESSION_KEY);
        return importedTopicMap;
    }
}
