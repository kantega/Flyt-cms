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

package no.kantega.publishing.spring;

import no.kantega.publishing.api.content.Language;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * User: Anders Skar, Kantega AS
 * Date: Apr 12, 2007
 * Time: 4:49:43 PM
 */
public class AksessLocaleResolver implements LocaleResolver {
    Locale defaultLocale = Language.getLanguageAsLocale(Language.NORWEGIAN_BO);

    public Locale resolveLocale(HttpServletRequest request) {
        Locale locale = (Locale)request.getAttribute("aksess_locale");
        if (locale == null) {
            locale = defaultLocale;
        }
        return locale;
    }

    public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {
        throw new UnsupportedOperationException();
    }

    public void setDefaultLocale(String locale) {

    }
}
