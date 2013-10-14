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

package no.kantega.commons.client.util;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Utility class for getting request parameters
 */
public class RequestParameters  {
    private HttpServletRequest request;

    public RequestParameters(HttpServletRequest request) {
        this.request = request;
    }

    public RequestParameters(HttpServletRequest request, String encoding) {
        if (encoding != null) {
            try {
                request.setCharacterEncoding(encoding);
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException(encoding);
            }
        }
        this.request = request;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public Map<String, String> getParametersAsMap() {
        Map<String, String> param = new HashMap<String, String>();

        Enumeration parameterNames = request.getParameterNames();
        while(parameterNames.hasMoreElements()) {
            String paramName = (String)parameterNames.nextElement();
            param.put(paramName, request.getParameter(paramName));
        }
        return param;
    }

    public String getString(String name) {
        return request.getParameter(name);
    }

    public String getString(String name, boolean trim) {
        String value = request.getParameter(name);
        if (trim && value != null) {
            value = value.trim();
        }
        return value;
    }

    public String[] getStrings(String name) {
        return request.getParameterValues(name);
    }

    public String getString(String name, int maxLen) {
        String value = request.getParameter(name);
        if (value == null) {
            return null;
        }
        if (value.length() > maxLen) {
            value = value.substring(0, maxLen - 1);
        }

        return value;
    }

    public int[] getInts(String name) {
        String[] values = request.getParameterValues(name);
        if (values == null) {
            return null;
        }

        int[] intValues = new int[values.length];

        for (int i = 0; i < values.length; i++) {
            try {
                intValues[i] = Integer.parseInt(values[i], 10);
            } catch (NumberFormatException e) {
                intValues[i] = -1;
            }

        }
        return intValues;
    }

    public int getInt (String name) {
        String value = request.getParameter(name);
        if (value == null) {
            return -1;
        }
        try {
            return Integer.parseInt(value, 10);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public long getLong (String name) {
        String value = request.getParameter(name);
        if (value == null) {
            return -1;
        }
        try {
            return Long.parseLong(value, 10);
        } catch (NumberFormatException e) {
            return -1;
        }
    }


    public boolean getBoolean (String name, boolean defaultValue) {
        String value = request.getParameter(name);
        if ("true".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value) || "0".equalsIgnoreCase(value)) {
            return false;
        }

        return defaultValue;
    }

    public boolean getBoolean (String name) {
        return getBoolean(name, false);
    }

    public Date getDate(String name, String dateformat) throws ParseException {
        if (dateformat == null) {
            dateformat = "dd.MM.yyyy";
        }
        String value = request.getParameter(name);
        if ((value == null) || (value.length() == 0)) {
            return null;
        } else if (!Character.isDigit(value.charAt(0))) {
            return null;
        }

        DateFormat df = new SimpleDateFormat(dateformat);
        return df.parse(value);
    }

    public Date getDateAndTime(String name, String dateformat) throws ParseException {
        if (dateformat == null) {
            dateformat = "dd.MM.yyyy";
        }
        String date = request.getParameter(name + "_date");
        if ((date == null) || (date.length() < dateformat.length())) {
            return null;
        } else if (date.indexOf("dd") != -1) {
            return null;
        }

        String time = request.getParameter(name + "_time");
        if (time == null || time.length() == 0 || !Character.isDigit(time.charAt(0))) {
            // Blankt tidspunkt eller tt:mm
            time = "00:00";
        }

        DateFormat df = new SimpleDateFormat(dateformat + " HH:mm");
        return df.parse(date + " " + time);
    }


    public MultipartFile getFile(String wantedname) {
        MultipartFile file = null;

        MultipartHttpServletRequest multipart = getMultipartHttpServletRequest();
        if(multipart != null) {
            file = multipart.getFile(wantedname);
            if (file != null && file.isEmpty()) {
                file = null;
            }
        }

        return file;
    }

    public List<MultipartFile> getFiles(String wantedname) {
        List<MultipartFile> files = new ArrayList<MultipartFile>();

        MultipartHttpServletRequest multipart = getMultipartHttpServletRequest();
        if (request != null) {
            files = multipart.getFiles(wantedname);
        }

        return files;
    }

    public MultipartHttpServletRequest getMultipartHttpServletRequest() {
        MultipartHttpServletRequest multipart;
        HttpServletRequest req = request;
        while(!(req instanceof MultipartHttpServletRequest) && req instanceof ServletRequestWrapper) {
            req = (HttpServletRequest) ((ServletRequestWrapper)req).getRequest();
        }

        if (req instanceof MultipartHttpServletRequest) {
            multipart = (MultipartHttpServletRequest) req;
        } else {
            multipart = null;
        }
        return multipart;
    }

    public boolean isMultipart() {
        return getMultipartHttpServletRequest() != null;
    }
}

