/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.controls.userprofile;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.controls.AksessController;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.topicmaps.data.Topic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * User: Kristian Selnæs / Anders Skar, Kantega AS
 * Date: Apr 26, 2007
 * Time: 2:14:23 PM
 */
public class UpdateProfileController implements AksessController {

    private static final String SOURCE = "aksess.UpdateProfileController";
    private int topicMapId = 1;

    /**
     * @see no.kantega.publishing.controls.AksessController#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();

        TopicMapService topicService = new TopicMapService(request);
        SecuritySession secSession = SecuritySession.getInstance(request);
        User user = secSession.getUser();

        if (request.getMethod().equalsIgnoreCase("POST") && user != null) {
            RequestParameters params = new RequestParameters(request);

            String[] topicIds = params.getStrings("topicId");

            if(topicIds != null) {
                //Legger til valgte emner
                for(int i = 0; i < topicIds.length; i++) {
                    String topicId = topicIds[i];
                    if (topicId != null) {
                        Topic t = topicService.getTopic(topicMapId, topicId);
                        if (t != null) {
                            Log.debug(SOURCE, "Valgt emne: " + t.getBaseName(), null, null);
                            topicService.addTopicSIDAssociation(t, user);
                            //Legger inn i sesjon også slik at det blir oppdatert nå
                            user.addTopic(t);
                        }
                    }
                }
            }

            //Fjerer emner som brukeren ønsker å fjerne fra profilen.
            List userTopics = topicService.getTopicsBySID(user);
            if(userTopics != null) {
                for(int i = 0; i < userTopics.size(); i++) {
                    Topic t = (Topic)userTopics.get(i);
                    if(t != null && !isSelectedTopic(t.getId(), topicIds)) {
                        topicService.removeTopicSIDAssociation(t, user);
                        user.removeTopic(t);
                    }
                }
            }

        }

        // Hent ut liste med alle emner
        List topicTypes = topicService.getTopicTypes(topicMapId);
        TreeMap allTopics = new TreeMap();

        for (int i = 0; i < topicTypes.size(); i++) {
            Topic topicType =  (Topic)topicTypes.get(i);
            List topics = topicService.getTopicsByInstance(topicType);
            allTopics.put(topicType.getBaseName(), topics);
        }
        model.put("allTopics", allTopics);

        if(user != null) {
            //Henter emner brukeren selv har valgt
            List userSelectedTopics = topicService.getTopicsBySID(user);
            model.put("userSelectedTopics", userSelectedTopics);

            //Finner forhåndsdefinerte emner for brukeren ved å ta differansen mellom alle emner og emner brukeren selv har valgt
            List userTopics = user.getTopics(); //Alle topics, inkludert brukervalgte
            if(userTopics == null) {
                userTopics = new ArrayList();
            }
            List userTopicsClone = new ArrayList();
            for(int i = 0; i < userTopics.size(); i++) {
                userTopicsClone.add(userTopics.get(i));
            }
            userTopicsClone.removeAll(userSelectedTopics); //Fjerer brukervalgte topics

            model.put("defaultTopics", userTopicsClone);
        }


        return model;
    }


    /**
     * Sjekker om en gitt topicId finnes i listen med topicIds
     * @param id
     * @param topicIds
     * @return
     */
    private boolean isSelectedTopic(String id, String[] topicIds) {
        if (topicIds == null) {
            return false;
        }
        for(int i = 0; i < topicIds.length; i ++) {
            if(id.equals(topicIds[i])) {
                return true;
            }
        }
        return false;
    }


    /**
     * @see no.kantega.publishing.controls.AksessController#getDescription() 
     */
    public String getDescription() {
        return "Oppdater brukerprofil med informasjonskategorier";
    }


    public void setTopicMapId(int topicMapId) {
        this.topicMapId = topicMapId;
    }
}
