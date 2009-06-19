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

package no.kantega.publishing.admin.util;

import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.data.enums.ContentStatus;

public class NavigatorUtil {

    public static String getIcon(ContentType type, int vStatus, int status) {
        if (vStatus == ContentVisibilityStatus.WAITING) {
            return "waiting.gif";
        } else if (vStatus == ContentVisibilityStatus.EXPIRED) {
            return "expired.gif";
        } else if (vStatus == ContentVisibilityStatus.ARCHIVED) {
            return "expired.gif";
        } else {
            if (type == ContentType.SHORTCUT) {
                return "shortcut.gif";
            } else {
                String ico;
                if (type == ContentType.LINK) {
                    ico = "link";
                } else if (type == ContentType.FILE) {
                    ico = "file";
                } else {
                    ico = "page";
                }

                if (status == ContentStatus.DRAFT) {
                    ico = ico + "_draft";
                }

                return ico + ".gif";
            }
        }
    }

    public static String getIconText(ContentType type, int vStatus, int status) {
        if (vStatus == ContentVisibilityStatus.WAITING) {
            return "Utsatt publisering";
        } else if (vStatus == ContentVisibilityStatus.EXPIRED) {
            return "Utgått på dato - skjult";
        } else if (vStatus == ContentVisibilityStatus.ARCHIVED) {
            return "Utgått på dato - arkivert";
        } else {
            if (type == ContentType.SHORTCUT) {
                return "Snarvei";
            } else {
                String txt;
                if (type == ContentType.LINK) {
                    txt = "Lenke";
                } else if (type == ContentType.FILE) {
                    txt = "Fildokument";
                } else if (type == ContentType.FORM) {
                    txt = "Skjema";
                } else {
                    txt = "Innholdsside";
                }

                if (status == ContentStatus.DRAFT) {
                    txt = txt + " (kladd)";
                }

                return txt;
            }
        }
    }
}
