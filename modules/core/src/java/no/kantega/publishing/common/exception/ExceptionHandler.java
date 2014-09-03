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

package no.kantega.publishing.common.exception;

import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.common.Aksess;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHandler {

    private Throwable ex;
    private String context = "";
    private String identity = "ukjent";

    public ExceptionHandler() {
    }

    public Throwable getException() {
        return ex;
    }

    public void setThrowable(Throwable ex) {
        this.ex = ex;
    }

    public void setThrowable(Throwable ex, String context) {
        this.ex = ex;
        this.context = context;
        ex.printStackTrace();
    }

    public void setThrowable(Throwable ex, String context, String identity) {
        this.ex = ex;
        this.context = context;
        this.identity = identity;
    }

    public String getMessage() {
        String key = "feil." + ex.getClass().getName();

        String msg = LocaleLabels.getLabel(key, Aksess.getDefaultAdminLocale());
        if (msg.equalsIgnoreCase(key)) {
            return LocaleLabels.getLabel("feil.no.kantega.commons.exception.SystemException", Aksess.getDefaultAdminLocale());
        }
        return msg;
    }

    public String getDetails() {
        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }
}
