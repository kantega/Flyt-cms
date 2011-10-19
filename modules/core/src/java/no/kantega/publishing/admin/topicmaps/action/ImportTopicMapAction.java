package no.kantega.publishing.admin.topicmaps.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.service.TopicMapService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: hareve
 * Date: 9/23/11
 * Time: 12:36 PM
  */
public class ImportTopicMapAction extends AbstractController {
    private static String SOURCE = "aksess.ImportTopicMapAction";

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);
        int id =  param.getInt("id");
        if (id != -1) {
            Log.info(SOURCE, "Import topicmap:" + id, null, null);

            TopicMapService topicService = new TopicMapService(request);
            topicService.importTopicMap(id);
        }
        return new ModelAndView(new RedirectView("ListTopicMaps.action"));
    }
}
