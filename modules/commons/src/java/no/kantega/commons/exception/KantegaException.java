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

package no.kantega.commons.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 */
public class KantegaException extends Exception {

    private String message;
    private String originalType;
    private String originalMessage;
    private String originalStack;
    private String errorCode;
    private String extendedMessage;
    private String description;

    public KantegaException(String message, Throwable original) {
        super(message);
        this.message = message;
        this.originalType = (original == null)?null:original.getClass().getName();
        this.originalMessage = (original == null)?null:original.getMessage();
        this.originalStack = (original == null)?null:createStacktrace(original);
    }

    public KantegaException(String message, String errorCode, String extendedMessage, String description, Throwable original) {
        super(message);
        this.message = message;
        this.errorCode = errorCode;
        this.extendedMessage = extendedMessage;
        this.description = description;
        this.originalType = (original == null)?null:original.getClass().getName();
        this.originalMessage = "";
        this.originalStack = (original == null)?null:createStacktrace(original);
    }

    private String createStacktrace(Throwable original) {
        String stacktrace = null;
        if (original != null) {
            try {
                StringWriter stringWriter = new StringWriter();
                original.printStackTrace(new PrintWriter(stringWriter));
                stacktrace = encodeString(stringWriter.toString());
            } catch (Throwable e) {
                try {
                    stacktrace = encodeString(original.toString());
                } catch (Exception e1) {
                    stacktrace = "" + original.getClass().getName();
                }
            }
        }
        return stacktrace;
    }

    private final String encodeString(String dirty) {
        if(dirty == null) return null;
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < dirty.length(); i++) {
            char ch = dirty.charAt(i);
            if (ch == '&') {
                buffer.append("&amp;");
            } else if (ch == '<') {
                buffer.append("&lt;");
            } else if (ch == '>') {
                buffer.append("&gt;");
            } else if (ch == '"') {
                buffer.append("&quot;");
            } else if (ch == '\'') {
                buffer.append("&apos;");
            } else {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }

    /**
     * Stacktrace bør alltid skrives ut med tre tabulator-tegn forran dersom den skal skrives som ren tekst
     * @return true dersom det fantes et linjeskift
     */
    private final String addThreeTabsAfterAllLinebreaks(String s) {
        if(s == null) return null;
        StringBuffer b = new StringBuffer(s);
        int p = 0;
        while (b.length() > (p + 1)) {
            // Leter frem linjeskift
            if (b.charAt(p) == '\n') {
                // Legger inn tre \t
                b.insert(p + 1, "\t\t\t");
            }
            p++;
        }
        return b.toString();
    }

    private  boolean hasLinebreaks(String s) {
        return s != null && (s.contains("\n"));
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("<exception>\n");
        b.append("\t<type>");
        b.append(this.getClass().getName());
        b.append("</type>\n");
        b.append("\t<message>");
        b.append(message);
        b.append("</message>\n");

        if(errorCode != null) {
            b.append("\t<errorCode>");
            b.append(errorCode);
            b.append("</errorCode>\n");
        } else {
            b.append("\t<errorCode/>");
        }
        if(extendedMessage != null) {
            b.append("\t<extendedMessage>");
            b.append(extendedMessage);
            b.append("</extendedMessage>\n");
        } else {
            b.append("\t<extErrMsg/>");
        }
        if(description != null) {
            b.append("\t<description>");
            b.append(description);
            b.append("</description>\n");
        } else {
            b.append("\t<errDesc/>");
        }

        if(originalType == null) {
            b.append("\t<original />");
        }
        else {
            b.append("\t<original>\n");
            b.append("\t\t<type>");
            b.append(originalType);
            b.append("</type>\n");
            b.append("\t\t<message>");
            b.append(originalMessage);
            b.append("</message>\n");

            if (originalStack == null) {
                b.append("\t\t<stacktrace />");
            }
            else {
                b.append("\t\t<stacktrace>\n\t\t\t");
                if (hasLinebreaks(originalStack)) {
                    b.append(addThreeTabsAfterAllLinebreaks(originalStack));
                    b.append("\t\t</stacktrace>\n");
                } else {
                    b.append(originalStack);
                    b.append("</stacktrace>\n");
                }
            }
            b.append("\t</original>\n");
        }
        b.append("</exception>\n");

        return b.toString();
    }
}
