/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.content.spellcheck;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class SpellcheckAction {
    private static final Logger log = LoggerFactory.getLogger(SpellcheckAction.class);

    @Autowired
    private SpellcheckerService spellcheckerService;

    /**
     * Format of received JSON-data (example values in parenthesis):
     * id -> String (c0)
     * method -> String (checkWords)
     * params -> JSONArray (ArrayList)
     *     [String (en)]
     *     [JSONArray ("Before","you","start","will",...)]
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/admin/publish/Spellcheck.action")
    public @ResponseBody Map<String, Object> handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        setResponseValues(response);
        JSONObject jsonObject = getJSONObject(request);
        model.put("id", null);
        model.put("error", null);
        model.put("result", performAction(jsonObject));
        return model;
    }

    private void setResponseValues(HttpServletResponse response) {
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", System.currentTimeMillis());
    }

    /**
     * Parse JSON data from the request. The JSON data comes in one of two formats:
     * 1:
     * {
     * "id":"c0",
     * "method":"getSuggestions",
     * "params":["en_us","MySQL"]
     * }
     *
     * 2:
     * {
     * "id":"c0",
     * "method":"checkWords",
     * "params":[
     *            "en_us",
     *            ["Before","you","start",...,"develop","own"]
     *          ]
     * }
     * 
     * @param request
     * @return
     * @throws IOException
     * @throws JSONException
     */
    private JSONObject getJSONObject(HttpServletRequest request) throws IOException, JSONException {
        String json = IOUtils.toString(request.getInputStream(), "utf-8");
        log.debug( "String:" + json);
        return new JSONObject(json);
    }

    private List<String> performAction(JSONObject jsonObject) throws JSONException {
        List<String> retVal;
        String method = jsonObject.getString("method");
        JSONArray params = jsonObject.getJSONArray("params");
        if ("checkWords".equals(method)) {
            retVal = checkWords(params);
        } else if ("getSuggestions".equals(method)) {
            retVal = getSuggestions(params);
        } else {
            // illegal method name
            retVal = new ArrayList<String>();
            log.debug( "Received '" + method + "' as value for method.");
        }
        return retVal;
    }

    private List<String> checkWords(JSONArray params) throws JSONException {
        String lang = params.getString(0);
        JSONArray wordsArray = params.getJSONArray(1);
        return spellcheckerService.spellcheck(toList(wordsArray), lang);
    }

    private List<String> getSuggestions(JSONArray params) throws JSONException {
        String lang = params.getString(0);
        String word = params.getString(1);
        return spellcheckerService.suggest(word, lang);
    }

    private List<String> toList(JSONArray array) {
        List<String> retVal = new ArrayList<String>();
        for (int i = 0; i < array.length(); i++) {
            try {
                retVal.add(array.getString(i));
            } catch (JSONException e) {
                log.error("", e);
            }
        }
        return retVal;
    }
}
