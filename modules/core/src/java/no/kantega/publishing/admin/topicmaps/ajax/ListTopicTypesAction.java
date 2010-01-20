package no.kantega.publishing.admin.topicmaps.ajax;

import no.kantega.publishing.admin.viewcontroller.AdminController;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicMap;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.commons.client.util.RequestParameters;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * List all topic types for all topic maps
 */
public class ListTopicTypesAction extends AdminController {
    private String view;

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        RequestParameters param = new RequestParameters(request);
        int topicMapId = param.getInt("topicMapId");
        String topicId = param.getString("topicId");

        TopicMapService topicMapService = new TopicMapService(request);
        List<TopicMap> topicMaps = topicMapService.getTopicMaps();

        Topic currentTopicType = null;
        if (topicMapId == -1 || topicId == null) {
            currentTopicType = (Topic)request.getSession(true).getAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_TOPICTYPE);
        } else {
            currentTopicType = new Topic();
            currentTopicType.setTopicMapId(topicMapId);
            currentTopicType.setId(topicId);
        }

        SecuritySession securitySession = SecuritySession.getInstance(request);
        List<Topic> topicTypes = new ArrayList<Topic>();
        for (TopicMap topicMap : topicMaps) {
            if (securitySession.isAuthorized(topicMap, Privilege.UPDATE_CONTENT)) {
                List<Topic> topics = topicMapService.getTopicTypes(topicMap.getId());
                for (Topic topic : topics) {
                    topic.setBaseName(topicMap.getName() + ":" + topic.getBaseName());
                }
                topicTypes.addAll(topics);
            }
        }

        if (currentTopicType == null && topicTypes.size() > 0) {
            currentTopicType = topicTypes.get(0);
        }

        // Update session with current selected topic type
        request.getSession(true).setAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_TOPICTYPE, currentTopicType);

        if (currentTopicType != null) {
            model.put("topics", topicMapService.getTopicsByInstance(currentTopicType));
            model.put("currentTopicType", currentTopicType);
        }

        model.put("topicTypes", topicTypes);

        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }
}
