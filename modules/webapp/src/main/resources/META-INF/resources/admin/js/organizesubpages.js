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
 * Namespace for organize subpages
 ********************************************************************************/

openaksess.organizesubpages = function()  {
    /**
     * Compiles the association categories and the order of their associations as a requestparameter.
     * Used as input to the ReorderSupPages.action
     */
    function getAssociationIdsAsParam () {
        var params = new Object();
        var categories = $("#SubPages ul.associationCategory");
        for (var i = 0; i < categories.length; i ++) {
            var currentCategory = $(categories[i]);
            var associations = currentCategory.find("li.page");
            var ids = "'";
            for (var j = 0; j < associations.length; j++) {
                ids += $(associations[j]).attr("id");
                if (j < (associations.length-1) ) {
                    ids += ",";
                }
            }
            ids += "'";

            eval("params.associationCategory" + currentCategory.attr("id") + "=" + ids);
        }
        return params;
    }

    return {
        currentUrl : "",
        /**
         * Loads the list of subpages for the given url.
         * @param url
         */
        updateSubPageList: function() {
            var ths = this;
            openaksess.common.debug("organizesubpages.updateSubPageList(): Triggering contentupdate event:" + this.currentUrl);
            openaksess.content.triggerContentUpdateEvent(this.currentUrl);

            openaksess.common.debug("organizesubpages.updateSubPageList(): Calling ListSubPages.action with: " + this.currentUrl);
            $("#SubPages").load(properties.contextPath + "/admin/publish/ListSubPages.action", {itemIdentifier: this.currentUrl}, function(success){
                openaksess.common.debug("organizesubpages.updateSubPageList(): response from ListSubPages.action received");
                $("#SubPages ul").sortable({
                    connectWith: '.associationCategory',
                    items: 'li:not(.menu)',
                    axis: 'y',
                    stop: function(e, ui){
                        $.post(properties.contextPath + "/admin/publish/ReorderSubPages.action", getAssociationIdsAsParam(), function(){
                            openaksess.navigate.updateNavigator(ths.currentUrl, true);
                        });
                    }
                });

                $("#SubPages li.page").mousedown(function(event){
                    var allSelected = $("#SubPages li.page.selected");
                    if (allSelected.size() == 0 || $(this).hasClass("selected") || event.ctrlKey) {
                        $(this).toggleClass('selected');
                    } else {
                        $(allSelected).each(function(){
                            $(this).removeClass("selected");
                        });
                        $(this).addClass("selected");
                    }
                });
            });
        }
    };
}();




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
    openaksess.common.debug("organizesubpages.updateMainPane(): id: " + id);
    openaksess.organizesubpages.currentUrl = openaksess.common.getContentUrlFromAssociationId(id);
    openaksess.common.debug("organizesubpages.updateMainPane(): currentUrl: " + openaksess.organizesubpages.currentUrl);
    openaksess.organizesubpages.updateSubPageList();
};
