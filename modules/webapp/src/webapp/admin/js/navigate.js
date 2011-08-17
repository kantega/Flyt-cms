/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/********************************************************************************
 * Navigator
 *
 * Functions and actions related to the navigator
 ********************************************************************************/

var suppressNavigatorUpdate = false;


/**
 * Navigator load actions
 */
$(document).ready(function(){
    openaksess.common.debug("navigate.$(document).ready()");
    openaksess.navigate.bindNavigatorClickEvents();
    if (!openaksess.common.isPopup()) {
        openaksess.navigate.makeNavigatorResizable();
    }
    openaksess.search.bindSearchInput();
});


/**
 * Namespace for all navigation related objects and functions.
 */
openaksess.navigate = {

    dummy : function(){},
    /**
     * Performes necessary actions to make the navigator frame (left column) resizable.
     * Dragging the FramesplitDrag-element resizes the columns.
     * When dragging the navigator column, the content frame must be resized accordingly.
     *
     * TODO: Fix height after resize for IE
     */
    makeNavigatorResizable : function() {
        openaksess.common.debug("navigate.makeNavigatorResizable(): Init");
        $("#Navigation").resizable(
            {
                minWidth: 50,
                minHeight: 50,
                handles: 'e',
                start: function(){
                    if (typeof openaksess.navigate.navigatorResizeOnStart == 'function') {
                        openaksess.navigate.navigatorResizeOnStart();
                    }
                },
                stop: function() {
                    if (typeof openaksess.navigate.navigatorResizeOnStop == 'function') {
                        openaksess.navigate.navigatorResizeOnStop();
                    }
                },
                resize: function() {
                    if (typeof openaksess.navigate.navigatorResizeOnResize == 'function') {
                        openaksess.navigate.navigatorResizeOnResize();
                    }
                }
            }
        );
    },

    /**
     * Default implementations of methods available for overriding in the various layouts.
     */
    navigatorResizeOnStart : function(){},
    navigatorResizeOnStop : function(){},
    navigatorResizeOnResize : function(){},

    getClipBoardHandler : function() {
    },

    /**
     * Defines actions for clicks in the navigator.
     */
    bindNavigatorClickEvents : function () {
        $("#Navigator, #Breadcrumbs, #Statusbar .statusDetails .breadcrumbs").live('click', function(event) {
            openaksess.common.debug("openaksess.navigate.bindNavigatorClickEvents(): clicked tag: " + event.target.tagName);
            if (event.target.tagName == 'A') {
                event.preventDefault();

                var $target = $(event.target);
                if ($target.hasClass('open')) {
                    //Click on the openState (+) icon
                    openaksess.common.debug("openaksess.navigate.bindNavigatorClickEvents(): click closeTree");
                    event.preventDefault();
                    var itemIdentifier = openaksess.navigate.getItemIdentifierFromNavigatorHref($target.attr("href"));
                    openaksess.navigate.closeTree(itemIdentifier);
                } else if ($target.hasClass('closed')) {
                    //Click on the openState (-) icon
                    openaksess.common.debug("openaksess.navigate.bindNavigatorClickEvents(): click openTree");
                    event.preventDefault();
                    itemIdentifier = openaksess.navigate.getItemIdentifierFromNavigatorHref($target.attr("href"));
                    openaksess.navigate.openTree(itemIdentifier);
                } else {
                    // Click an item
                    var href = $target.attr("href");
                    itemIdentifier = openaksess.navigate.getItemIdentifierFromNavigatorHref(href);
                    // Always open tree when you navigator to it
                    openaksess.navigate.setFolderOpen(itemIdentifier);
                    openaksess.common.debug("openaksess.navigate.bindNavigatorClickEvents(): ItemIdentifier: "+itemIdentifier);
                    if(typeof openaksess.navigate.onNavigatorTitleClick == 'function') {
                        openaksess.navigate.onNavigatorTitleClick($target);
                    }
                    if(typeof openaksess.navigate.updateMainPane == 'function') {
                        openaksess.navigate.updateMainPane(itemIdentifier, true);
                    }
                    openaksess.common.triggerEvent("navigatorSelect", [itemIdentifier, openaksess.navigate.getOpenFolders(), $target]);
                }
            }

        });
    },

    /**
     * Empty default implementation.
     * @param clickedElement - jQuery object representing the clicked navigator menu element.
     */
    onNavigatorTitleClick : function(clickedElement){},

    /**
     * Empty default implementation.
     */
    updateMainPane : function(itemIdentifier, suppressNavigatorUpdate){},

    /**
     * Refreshes the navigator.
     *
     * @param itemIdentifier Current selected item
     * @param expand Expand the menu down to selected item.
     */
    updateNavigator : function(itemIdentifier, expand) {
        openaksess.common.debug("openaksess.navigate.updateNavigator(): itemIdentifier: " + itemIdentifier + ", expand: " + expand);
        var params = openaksess.navigate.getNavigatorParams();
        if (itemIdentifier) {
            params.itemIdentifier = itemIdentifier;
        }
        var openFolders = openaksess.navigate.getOpenFolders();
        if( openFolders != null) {
            params.openFolders = openFolders;
        }
        params.expand = expand;

        $("#Navigator").load(openaksess.navigate.getNavigatorAction(), params, function() {
            openaksess.common.debug("openaksess.navigate.updateNavigator(): response from " + openaksess.navigate.getNavigatorAction() + " received");

            var clipBoardHandler = openaksess.navigate.getClipBoardHandler();

            if (typeof clipBoardHandler == "object") {
                clipBoardHandler.isClipboardEmpty(function(clipboardEmpty){
                    openaksess.common.debug("openaksess.navigate.updateNavigator(): Response from DWR. Clipboard empty: " + clipboardEmpty);
                    openaksess.navigate.setContextMenus(clipboardEmpty);
                });
            }
        });
    },

    getOpenFolders: function() {
        return $("#NavigatorState .openFolders").html()
    },

    setOpenFolders: function(folderList){
        $("#NavigatorState .openFolders").html(folderList);
    },

    /**
     * Optional additional parameters to send to the navigator.
     *
     * Default implementation available for overriding.
     */
    getNavigatorParams : function() {
        return new Object();
    },

    /**
     * Default implementation available for overriding.
     *
     * @param clipboardEmpty - Boolean. Indicates whether or not there is content on the clipboard.
     */
    setContextMenus : function(clipboardEmpty){},


    /**
     * Binds a context menu to a specific navigator link type.
     *
     * @param type css class of the a-element in the navigator.
     * @param disabledOptions Array of disabled options. Must be the href-value in the menu's a-element, without the leading hash (#).
     */
    setContextMenu : function(type, disabledOptions) {
        $("#Navigator").contextMenu(
            {
                itemClass: type,
                itemTagName: 'A',
                menu: 'ContextMenu-'+type
            },
            function(action, el, pos) {
                var splitHref = $(el).attr("href").split("?");
                var href = splitHref[splitHref.length-1];           // Work-around for IE7's faulty implementation of getAttribute('href')
                openaksess.common.debug("openaksess.navigate.setContextMenu(): clicked url: "+ href);
                eval("openaksess.navigate.handleContextMenuClick_"+type+"(action, href)");
            }
        );
        if (disabledOptions) {
            var disable = "";
            for(var i = 0; i < disabledOptions.length; i++) {
                disable += "#"+disabledOptions[i];
                if (i != (disabledOptions.length-1)) {
                    disable += ",";
                }
            }
            openaksess.common.debug("openaksess.navigate.setContextMenu(): disabled options: "+ disable);
            $("#ContextMenu-"+type).disableContextMenuItems(disable);
        }

        openaksess.common.debug("openaksess.navigate.setContextMenu(): "+ type +" menu set");
    },

    openTree : function(itemIdentifier) {
        openaksess.common.debug("openaksess.navigate.openTree(): itemIdentifier: " + itemIdentifier);
        openaksess.navigate.setFolderOpen(itemIdentifier);
        openaksess.navigate.updateNavigator(openaksess.navigate.getCurrentItemIdentifier(), false);
    },


    closeTree : function (itemIdentifier) {
        openaksess.common.debug("openaksess.navigate.closeTree(): itemIdentifier: " + itemIdentifier);
        openaksess.navigate.setFolderClosed(itemIdentifier);
        openaksess.navigate.updateNavigator(openaksess.navigate.getCurrentItemIdentifier(), false);
    },



    setFolderOpen : function (id) {
        var newList = $("#NavigatorState .openFolders").html();
        openaksess.common.debug("openaksess.navigate.setFolderOpen(): current list: " + newList + ", id: " + id);

        var openList = newList.split(",");
        for (var i = 0; i < openList.length; i++) {
            var current = openList[i];
            if (id == current) {
                return;
            }
        }

        if (newList.length == 0) {
            newList = "" + id;
        } else {
            newList = newList + "," + id;
        }

        openaksess.common.debug("openaksess.navigate.setFolderOpen(): new list: " + newList);
        openaksess.navigate.setOpenFolders(newList);
        openaksess.common.triggerEvent("navigatorOpen", [id, newList]);
    },


    setFolderClosed : function (id) {
        var openList = $("#NavigatorState .openFolders").html().split(",");
        openaksess.common.debug("openaksess.navigate.setFolderClosed(): openList: " + openList + ", id: " + id);

        var newOpenList  = "";
        for (var i = 0; i < openList.length; i++) {
           var current = openList[i];
           if (id == current) {
              // Skip
           } else {
              if (newOpenList.length > 0) newOpenList += ",";
              newOpenList += current;
           }
        }

        openaksess.common.debug("openaksess.navigate.setFolderClosed(): newOpenList: " + newOpenList);
        openaksess.navigate.setOpenFolders(newOpenList);
        openaksess.common.triggerEvent("navigatorClose", [id, newOpenList]);
    }


};





/********************************************************************************
 * Search
 ********************************************************************************/

openaksess.search = {

    bindSearchInput : function() {
        $("#SearchForm").submit(function(e){
                e.preventDefault(); //Preventing the form from being subitted the ordinary way.
                var textInput = $("#SearchQuery");
                var query = textInput.val();            
                if (openaksess.search.inputValid(query)) {
                    openaksess.search.performSearch(query);
                }
        });
    },

    getSearchAction : function() {
        return null;
    },

    performSearch : function(query){
        openaksess.common.debug("openaksess.navigate.Search.search(): query: " + query);
        var searchAction = openaksess.search.getSearchAction();
        if (searchAction) {
            var searchUrl = searchAction + "?q=" + query;
            var content = '<iframe name="search" title="Search results" src="' + searchUrl + '" frameborder="0" style="height: 100%; width:100%; background: url(bitmaps/common/icons/small/loader_framework.gif) no-repeat center">';
            $("#MainPane .infoslider").infoslider('option', {cssClasses: 'search', resizable: true, floated: false}).infoslider('open', document.getElementById("SearchForm"), content);
        }
    },

    inputValid : function(query){
        openaksess.common.debug("openaksess.navigate.Search.inputValid(): query: " + query);
        query = $.trim(query);
        return query.length >= 3;
    }
};
