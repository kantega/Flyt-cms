package no.kantega.publishing.admin.topicmaps.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.topicmaps.data.ImportedTopicMap;
import no.kantega.publishing.topicmaps.data.exception.ImportTopicMapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class ImportTopicMapAction extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(ImportTopicMapAction.class);
        public final static String IMPORETED_TOPICMAP_SESSION_KEY = "importedTopicMapKey";
    private String view;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);
        Map<String, Object> model = new HashMap<>();
        int id =  param.getInt("id");
        if (id != -1) {
            log.info( "Importing topicmap with id: " + id);

            TopicMapService topicService = new TopicMapService(request);
            try{
                ImportedTopicMap importedTopicMap = topicService.importTopicMap(id);
                model.put("importedTopicMap", importedTopicMap);
                addTopicMapToSession(importedTopicMap, request);
            }catch (ImportTopicMapException e){
                model.put("errormessage", e.getMessage());
            }
        }
        return new ModelAndView(view, model);
    }

    private void addTopicMapToSession(ImportedTopicMap importedTopicMap, HttpServletRequest request) {
        request.getSession().setAttribute(IMPORETED_TOPICMAP_SESSION_KEY, importedTopicMap);
    }

    public void setView(String view) {
        this.view = view;
    }
}
