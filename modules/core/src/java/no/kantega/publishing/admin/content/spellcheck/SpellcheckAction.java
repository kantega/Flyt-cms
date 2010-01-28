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

import no.kantega.commons.log.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 */
public class SpellcheckAction implements Controller {

    @Autowired
    private SpellcheckerService spellcheckerService;


    /**
     * Format på mottatt JSON-data (registrerte verdier i parantes):
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
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        setResponseValues(response);

        JSONObject jsonObject = getJSONObject(request);

        // Generate result
        JSONArray result = performAction(jsonObject);

        // Generate and print return data
        JSONObject retVal = new JSONObject("{'id':null,'error':null}");
        retVal.put("result", result);
        PrintWriter pw = response.getWriter();
        pw.println(retVal.toString());
        response.getWriter().flush();
        return null;
    }

    private void setResponseValues(HttpServletResponse response) {
        response.setContentType("text/plain; charset=utf-8");
//        response.setCharacterEncoding("utf-8");
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", System.currentTimeMillis());
    }

    /**
     * Parse json data from the request. The json data comes in one of two formats:
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
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = request.getReader();
        while (reader.ready()) {
            builder.append(reader.readLine() + "\n");
        }
        System.out.println(builder.toString());
        return new JSONObject(builder.toString());
    }

    private JSONArray performAction(JSONObject jsonObject) throws JSONException {
        JSONArray retVal;
        String id = jsonObject.getString("id");
        String method = jsonObject.getString("method");
        JSONArray params = jsonObject.getJSONArray("params");
        if ("checkWords".equals(method)) {
            retVal = checkWords(params);
        } else if ("getSuggestions".equals(method)) {
            retVal = getSuggestions(params);
        } else {
            // illegal method name
            retVal = new JSONArray();
        }
        return retVal;
    }

    private JSONArray checkWords(JSONArray params) throws JSONException {
        String lang = params.getString(0);
        JSONArray wordsArray = params.getJSONArray(1);
        List<String> words = toList(wordsArray);
        List<String> misspelledWords = spellcheckerService.spellcheck(words);
        return toJSONArray(misspelledWords);
    }

    private JSONArray getSuggestions(JSONArray params) throws JSONException {
        String lang = params.getString(0);
        String word = params.getString(1);
        List<String> suggestions = spellcheckerService.suggest(word);
        return toJSONArray(suggestions);
    }

    private List<String> toList(JSONArray array) {
        List<String> retVal = new ArrayList<String>();
        for (int i = 0; i < array.length(); i++) {
            try {
                retVal.add(array.getString(i));
            } catch (JSONException e) {
                Log.error(getClass().getClass().getName(), e, "toList", null);
            }
        }
        return retVal;
    }

    private JSONArray toJSONArray(List<String> list) {
        JSONArray retVal = new JSONArray();
        for (String element : list) {
            retVal.put(element);
        }
        return retVal;
    }

}
