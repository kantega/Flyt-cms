/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin;

/**
 * Convinience class of wrapping the names of HttpServletRequest parameters used in the admin interface.
 */
public class AdminRequestParameters {

    public static final String THIS_ID = "thisId";
    public static final String CONTENT_ID = "contentId";
    public static final String URL = "url";

    public static final String NAVIGATION_SORT_ORDER = "sort";
    public static final String NAVIGATION_OPEN_FOLDERS = "openFolders";
    public static final String NAVIGATION_SITES = "sites";

    public static final String EXPAND = "expand";
    
    public static final String PERMISSONS_CAN_UPDATE = "canUpdate";
    public static final String PERMISSONS_CAN_DELETE = "canDelete";
    public static final String PERMISSONS_CAN_CREATE_SUBPAGE = "canCreateSubpage";
    public static final String PERMISSONS_LOCKED_BY = "lockedBy";

    public static final String CLIPBOARD = "clipboard";

}
