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

package no.kantega.publishing.modules.mailsender;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.spring.RootContext;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;


/**
 *
 */
public class MailTextReader {

    public static final String SOURCE = "Akesss.MailTextReader";

    public static String getContent(String filename, String[] replaceStrings) throws SystemException {

        try {
            ResourceLoader source = (ResourceLoader) RootContext.getInstance().getBean("emailTemplateResourceLoader");
            Resource resource = source.getResource(filename);

            String content = IOUtils.toString(resource.getInputStream());

            StringBuilder result = new StringBuilder();
            int count = 0;
            char[] chars = content.toCharArray();
            for (int j = 0; j < chars.length; j++) {
                if(chars[j] == '%') {
                    if(count < replaceStrings.length) {
                        result.append(replaceStrings[count++]);
                    }
                } else {
                    result.append(chars[j]);
                }
            }

            return result.toString();
        } catch (Exception e) {
            throw new SystemException("Feil ved lesing av " + filename, e);
        }
    }
}
