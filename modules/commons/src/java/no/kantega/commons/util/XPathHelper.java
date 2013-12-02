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

import com.sun.org.apache.xpath.internal.XPathAPI;
import com.sun.org.apache.xpath.internal.objects.XObject;
import org.w3c.dom.Element;

/**
 *
 */
public class XPathHelper {

    public static String getString(Element element, String expr) {
        XObject obj = null;

        try {
            obj = XPathAPI.eval(element, expr + "/child::text()");
            return obj.str();
        } catch (Exception e) {
            return "";
        }
    }

    public static double getNum(Element element, String expr) {
        XObject obj = null;

        try {
            obj = XPathAPI.eval(element, expr + "/child::text()");
            return obj.num();
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    public static boolean getBool(Element element, String expr, boolean defaultValue) {
        XObject obj = null;

        try {
            obj = XPathAPI.eval(element, expr + "/child::text()");
            if (obj.num() == 1.0) {
                return true;
            } else if (obj.num() == 0.0) {
                return false;
            } else {
                return defaultValue;
            }
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
