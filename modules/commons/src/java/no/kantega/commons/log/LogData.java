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

package no.kantega.commons.log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Beskriver et logg-innslag i trace-logg.
 */
public class LogData {

    private Object identity;
    private String description;
    private Object context;
    private Object details;

    private LogData() {
    }

    public static final LogData create(Object o) {
        LogData logData = new LogData();

        if (o instanceof no.kantega.commons.exception.KantegaException) {
            logData.details = o.toString();
        } else if (o instanceof Throwable) {
            Throwable t = (Throwable) o;
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            logData.details = sw.toString();
        }

        if (o instanceof Throwable) {
            logData.description = o.getClass().getName() + ": " + ((Throwable) o).getMessage();
        } else if (o != null) {
            logData.description = o.toString();
        }

        return logData;
    }

    public static final LogData create(Throwable throwable, Object context, Object identity) {
        LogData logData = create(throwable);
        logData.context = context;
        logData.identity = identity;
        return logData;
    }

    public static final LogData create(String description, Object context, Object identity) {
        LogData logData = new LogData();
        logData.description = description;
        logData.context = context;
        logData.identity = identity;
        return logData;
    }

    public String getDescription() {
        return description;
    }

    public String getContext() {
        return (context != null ? context.toString() : null);
    }

    public String getIdentity() {
        return (identity != null ? identity.toString() : null);
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        if (description != null) s.append("Description: " + description + " ");
        if (context != null) s.append("Context: " + context + " ");
        if (identity != null) s.append("Identity: " + identity + " ");
        if (details != null) s.append("Details: " + details);
        return s.toString();
    }
}