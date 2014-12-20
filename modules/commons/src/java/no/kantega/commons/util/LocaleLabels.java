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

package no.kantega.commons.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;


public class LocaleLabels {
    private static final Logger log = LoggerFactory.getLogger(LocaleLabels.class);
    static public String DEFAULT_BUNDLE = "TextLabels";

    private static Map<String, PropertyResourceBundle> bundles = new HashMap<>();

    private static PropertyResourceBundle getBundle(String bundleName, String locale) {
        PropertyResourceBundle bundle;
        synchronized (bundles) {
            bundle = bundles.get(bundleName + "_" + locale);
            if (bundle == null) {
                String[] locArr = locale.split("_");
                try {
                    if (locArr.length > 2) {
                        bundle = (PropertyResourceBundle)ResourceBundle.getBundle(bundleName, new Locale(locArr[0], locArr[1], locArr[2]));
                    } else {
                        bundle = (PropertyResourceBundle)ResourceBundle.getBundle(bundleName, new Locale(locArr[0], locArr[1]));
                    }
                    bundles.put(bundleName + "_" + locale, bundle);
                } catch (MissingResourceException e) {
                    log.error("Could not find resource " + bundle, e);
                }
            }
        }
        return bundle;
    }

    private static String getLabel(String key, String bundleName, String locale, Map<String, Object> parameters) {
        String msg = key;

        PropertyResourceBundle bundle = getBundle(bundleName, locale);
        if (bundle == null) {
            return msg;
        }

        try {
            msg = bundle.getString(key);
        } catch (MissingResourceException e) {
            // Do nothing
        }

        if (parameters != null) {
            for (Map.Entry<String, ?> o : parameters.entrySet()) {
                Object value = o.getValue();
                if (value != null) {
                    msg = msg.replaceAll("\\$\\{" + o.getKey() + "\\}", value.toString());
                }
            }
        }
        return msg;
    }

    /**
     * Get label from bundle with specified locale
     * @param key - key to look up
     * @param bundleName - bundle (property-file) to use
     * @param locale - locale
     * @return - localized string
     */
    public static String getLabel(String key, String bundleName, Locale locale) {
        String loc = locale.getLanguage() + "_" + locale.getCountry();
        if (isNotBlank(locale.getVariant())) {
            loc += "_" + locale.getVariant();
        }
        return getLabel(key, bundleName, loc, null);
    }


    /**
     * Get label from bundle with specified locale, replaces parameters found in string
     * @param key - key to look up
     * @param bundleName - bundle (property-file) to use
     * @param locale - locale
     * @param parameters - parameters
     * @return - localized string
     */
    public static String getLabel(String key, String bundleName, Locale locale, Map<String, Object> parameters) {
        String loc = locale.getLanguage() + "_" + locale.getCountry();
        if (locale.getVariant() != null) {
            loc += "_" + locale.getVariant();
        }
        return getLabel(key, bundleName, loc, parameters);
    }

    /**
     * Get label from default bundle with specified locale
     * @param key - key to look up
     * @param locale - locale
     * @return - localized string
     */

    public static String getLabel(String key, Locale locale) {
        return getLabel(key, DEFAULT_BUNDLE, locale, null);
    }

    /**
     * Get label from default bundle with specified locale, replaces parameters found in string
     * @param key - key to look up
     * @param locale - locale
     * @param parameters - parameters
     * @return - localized string
     */

    public static String getLabel(String key, Locale locale, Map<String, Object> parameters) {
        return getLabel(key, DEFAULT_BUNDLE, locale, parameters);
    }

    public static Enumeration getKeys(String bundleName, Locale locale) {
        String loc = locale.getLanguage() + "_" + locale.getCountry();
        if (locale.getVariant() != null) {
            loc += "_" + locale.getVariant();
        }

        PropertyResourceBundle bundle = getBundle(bundleName, loc);
        if (bundle == null) {
            return null;
        }
        return bundle.getKeys();
    }

    /**
     *
     * @param request - the current request.
     * @return the value of request attribute aksess_locale or NO_no if aksess_locale is absent.
     */
    public static Locale getLocaleFromRequestOrDefault(HttpServletRequest request){
        Locale locale = (Locale)request.getAttribute("aksess_locale");
        if (locale == null) {
            locale = new Locale("no", "NO");
        }
        return locale;
    }
}
