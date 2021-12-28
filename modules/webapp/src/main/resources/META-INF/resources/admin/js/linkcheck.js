/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This script expects the following properties to be set:
 * * contextPath
 *
 */

/********************************************************************************
 * Namespace for linkcheck
 ********************************************************************************/
openaksess.linkcheck = {
    currentUrl : "",

    /**
     * Loads the list of subpages for the given url.
     * @param url
     */
    updateLinkList : function(sort, data) {
        openaksess.common.debug("openaksess.linkcheck.updateLinkList(): Triggering contentupdate event");
        openaksess.content.triggerContentUpdateEvent(openaksess.linkcheck.currentUrl);

        openaksess.common.debug("openaksess.linkcheck.updateLinkList(): Calling ListBrokenLinks.action");
        if (!data) {
            data = {};
        }
        data.itemIdentifier = openaksess.linkcheck.currentUrl;
        data.sort = sort;
        $("#MainPaneContent").load(properties.contextPath + "/admin/publish/ListBrokenLinks.action", data, function(success){
            openaksess.common.debug("openaksess.linkcheck.updateLinkList(): response from ListBrokenLinks.action received");
        });
    }
};



/********************************************************************************
 * Overridden functions.
 ********************************************************************************/

/**
 * Determines what should happen inside the main pane when an action that requires a reload of this occurs,
 * e.g. a navigator click.
 *
 * Overrides the default implementation. See navigate.js
 *
 * @param id - Current item id.
 * @param suppressNavigatorUpdate
 */
openaksess.navigate.updateMainPane = function(id, suppressNavigatorUpdate) {
    openaksess.common.debug("linkcheck.updateMainPane(): id: " + id);
    openaksess.linkcheck.currentUrl = openaksess.common.getContentUrlFromAssociationId(id);
    openaksess.common.debug("linkcheck.updateMainPane(): currentUrl: " + openaksess.linkcheck.currentUrl);
    openaksess.linkcheck.updateLinkList("title");
};

