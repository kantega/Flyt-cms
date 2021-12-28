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

package no.kantega.publishing.api.taglibs.mail;

import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.modules.mailsender.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;


public class SendMailTag extends BodyTagSupport {

    private String from;

    private String to;

    private String subject;

    private static final Logger log = LoggerFactory.getLogger(SendMailTag.class);


    public int doEndTag() throws JspException {
        try {
            Reader r = getBodyContent().getReader();

            StringWriter sw = new StringWriter();

            while(r.ready()) {
                sw.write(r.read());
            }

            if (from == null || from.trim().equals("")) {
                from = Aksess.getConfiguration().getString("mail.editor");
            }

            MailSender.send(from, to, subject, sw.toString());

        } catch (IOException | ConfigurationException | SystemException e) {
            log.error(e.getMessage(), e);
        }

        return SKIP_BODY;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
