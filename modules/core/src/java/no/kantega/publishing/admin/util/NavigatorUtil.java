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

import no.kantega.commons.util.LocaleLabels;
import no.kantega.commons.util.StringHelper;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;

/**
 * Helper class for extracting properties associated with navigator menu items.
 */
public class NavigatorUtil {

    /**
     * Returns the name of the icon to be used in the navigator.
     *
     * @param type
     * @param vStatus
     * @param status
     * @return
     */
    public static String getIcon(ContentType type, int vStatus, ContentStatus status) {
        if (vStatus == ContentVisibilityStatus.WAITING.statusId) {
            return "waiting";
        } else if (vStatus == ContentVisibilityStatus.EXPIRED.statusId) {
            return "expired";
        } else if (vStatus == ContentVisibilityStatus.ARCHIVED.statusId) {
            return "expired";
        } else {
            if (type == ContentType.SHORTCUT) {
                return "shortcut";
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
                    ico = ico + "-draft";
                }

                return ico;
            }
        }
    }


    /**
     * Returns the text associated with a menu item icon. Typically shown on mouseover.
     *
     * @param type
     * @param vStatus
     * @param status
     * @return
     */
    //TODO: Use locale labels
    public static String getIconText(ContentType type, int vStatus, ContentStatus status) {
        if (vStatus == ContentVisibilityStatus.WAITING.statusId) {
            return "Utsatt publisering";
        } else if (vStatus == ContentVisibilityStatus.EXPIRED.statusId) {
            return "Utgått på dato - skjult";
        } else if (vStatus == ContentVisibilityStatus.ARCHIVED.statusId) {
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

    /**
     * Calculates a title for use by the navigator based on the objects complete title.
     *
     * Long titles are cropped.
     *
     *  @param type
     * @param contentTitle
     * @return
     */
    public static String getNavigatorTitle(ContentType type, String contentTitle) {
        if (type == ContentType.SHORTCUT) {
            contentTitle = contentTitle + "&nbsp;"+ LocaleLabels.getLabel("aksess.navigator.title.shortcut", Aksess.getDefaultAdminLocale());
        }
        return contentTitle;
    }

    /**
     * Returns the kind of context menu an item should have, based on it's content type.
     *
     * @param type
     * @param vStatus
     * @param status
     * @return
     */
    public static String getContextMenuType(ContentType type, int vStatus, ContentStatus status) {
        if (type == ContentType.LINK) {
            return "link";
        } else if (type == ContentType.FILE) {
            return "file";
        } else if (type == ContentType.SHORTCUT) {
            return "shortcut";
        } else {
            return "page";
        }
    }

       /**
     * Get a list of open folders. If "expand" parameter is set, path to select Content object will be added to list.
     * @param expand
     * @param openFoldersList
     * @param path - path to current selected object
     * @param currentId
        * @return - Comma separated list of open folders
     */
    public static String getOpenFolders(boolean expand, String openFoldersList, String path, int currentId) {
        if (openFoldersList == null) {
            openFoldersList = "";
        }

        StringBuilder openFolderBuilder = new StringBuilder(openFoldersList);
        if (expand && currentId != -1) {
            if (openFoldersList.endsWith(",")) {
                openFolderBuilder.append(currentId);
            } else {
                openFolderBuilder.append(",").append(currentId);
            }
        }

        // List of open folders
        int[] openFolders = StringHelper.getInts(openFolderBuilder.toString(), ",");

        if (expand && path != null) {
            // Add all elements in path to expand tree
            if (path.length() > 1) {
                int pathIds[] = StringHelper.getInts(path, "/");
                if (pathIds != null) {
                    for (int pId : pathIds) {
                        boolean exists = false;
                        for (int openFolder : openFolders) {
                            if (pId == openFolder) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            openFolderBuilder.append(",").append(pId);
                        }
                    }
                }
            }
        }

        return openFolderBuilder.toString();
    }
}
