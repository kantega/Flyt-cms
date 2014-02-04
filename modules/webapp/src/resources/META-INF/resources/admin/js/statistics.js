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

$(document).ready(function(){
    openaksess.common.debug("statistics.$(document).ready()");

    $("#PageStatistics").live('click', function(event) {
        event.preventDefault();
        openaksess.statistics.currentView = "Page";
        openaksess.statistics.updateStatistics(openaksess.statistics.currentUrl, openaksess.statistics.currentView);
    });
    $("#TotalStatistics").live('click', function(event) {
        event.preventDefault();
        openaksess.statistics.currentView = "Total";
        openaksess.statistics.updateStatistics(openaksess.statistics.currentUrl, openaksess.statistics.currentView);
    });

});



/********************************************************************************
 * Namespace for the statistics layout
 ********************************************************************************/

openaksess.statistics = {
    currentUrl : "",
    currentView : "Page",
    /**
     * Loads the list of subpages for the given url.
     * @param url
     */
    updateStatistics : function(url, view) {

        openaksess.common.debug("statistics.updatePageStatistics(): Triggering contentupdate event");
        openaksess.content.triggerContentUpdateEvent(url);

        openaksess.common.debug("statistics.updatePageStatistics(): Calling " + view + "Statistics.action");
        $("#MainPaneContent").load(properties.contextPath + "/admin/publish/" + view + "Statistics.action", {itemIdentifier: url}, function(success){
            openaksess.common.debug("statistics.updatePageStatistics(): response from PageStatistics.action received");
            openaksess.statistics.pageLoaded();
        });
    },

    /**
     * Default implementation of onload callback.
     */
    pageLoaded : function() {}
};







/********************************************************************************
 * Overridden functions from inherited namespaces.
 ********************************************************************************/


/**
 * Determines what should happen inside the main pane when an action that requires a reload of this occurs,
 * e.g. a navigator click.
 *
 * @param id - Current item id.
 * @param suppressNavigatorUpdate
 */
openaksess.navigate.updateMainPane = function(id, suppressNavigatorUpdate) {
    openaksess.common.debug("statistics.updateMainPane(): id: " + id);
    openaksess.statistics.currentUrl = openaksess.common.getContentUrlFromAssociationId(id);
    openaksess.common.debug("statistics.updateMainPane(): currentUrl: " + openaksess.statistics.currentUrl);
    openaksess.statistics.updateStatistics(openaksess.statistics.currentUrl, openaksess.statistics.currentView);
};



