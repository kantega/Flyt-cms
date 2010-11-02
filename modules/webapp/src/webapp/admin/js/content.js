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

/*
 * This script expects the following properties to be set:
 * * contextPath
 * * objectTypeAssociation
 * * content.labels.confirmDelete
 * * content.labels.copyPaste
 * * content.labels.publishinfoPeriod
 * * content.labels.editPermissions
 * * content.labels.reject
 * * content.labels.linkcheckField
 * * content.labels.linkcheckUrl
 * * content.labels.linkcheckStatus
 * * content.labels.linkcheckLastchecked
 * * content.labels.linkcheckTimeschecked
 * * content.labels.details
 * * content.labels.publishinfoAlias
 * * content.labels.contentTitle
 * * content.labels.contentLastModified
 * * content.labels.contentModifiedBy
 * * content.labels.contentApprovedBy
 * * content.labels.contentChangeFrom
 * * content.labels.contentExpireDate
 * * content.labels.contentOwnerPerson
 * * content.labels.contentDisplayTemplate
 * * content.labels.associations
 *
 */

$(document).ready(function(){
    openaksess.common.debug("content.$(document).ready()");
    openaksess.content.bindContentupdateEvents();
    openaksess.content.bindFilterEvents();
    openaksess.content.bindToolButtons();
    stateHandler.init("contentupdate");
});

/********************************************************************************
 *
 * OpenAksess content related functions.
 *
 ********************************************************************************/

openaksess.content = {

    /**
     * Contains the binding of all elements that are listening to the contentupdate event.
     * New global listeners to this event should be added here.
     */
    bindContentupdateEvents : function () {
        //Enables the navigator to listen to contentupdate events. Called every time a contentupdate event is fired.
        $("#Navigator").bind("contentupdate", function(e, url){
            openaksess.common.debug("openaksess.content.bindContentupdateEvents(): #Navigator has received contentupdate event. Url: " + url);

            if (!suppressNavigatorUpdate) {
                openaksess.navigate.updateNavigator(url, true);
            } else {
                suppressNavigatorUpdate = false;
                openaksess.common.debug("openaksess.content.bindContentupdateEvents(): navigationUpdate suppressed");
            }
        });

        $("#Statusbar").bind("contentupdate", function(e, url){
            openaksess.common.debug("openaksess.content.bindContentupdateEvents(): #Statusbar has received contentupdate event. Url: " + url);
            openaksess.content.contentstatus.init();
            openaksess.content.contentstatus.disableButtons();
            $.post(properties.contextPath + "/admin/publish/ContentProperties.action", {url: url}, function(data){
                if (data) {
                    openaksess.content.contentstatus.breadcrumbs(data.path);
                    openaksess.content.contentstatus.brokenLinks(data.links);
                    openaksess.content.contentstatus.details(data);
                    openaksess.content.contentstatus.associations(data.associations);
                    openaksess.content.contentstatus.enableButtons(data.enabledButtons);
                    openaksess.content.contentstatus.showApproveOrReject(data.showApproveButtons);
                    openaksess.content.contentstatus.updateFilters(data.userPreferences);
                    openaksess.content.contentstatus.showContentHints(data.contentHints);
                    openaksess.content.contentstatus.lockedBy = data.lockedBy;
                }
            }, "json");
        });

//    New listeners can be added here:
//    $([element listening to event]).bind("contentupdate", function(e, url) {
//        [perform actions]
//    });
    },


    /**
     * Binds click/select events to the content filters, such as 'Hide expired'.
     */
    bindFilterEvents : function () {
        var $filterOptions = $("#Filteroptions");
        $filterOptions.find("#FilteroptionHideExpired").bind('change', function(){
            var value = $(this).is(":checked");
            openaksess.common.debug("openaksess.content.bindFilterEvents(): Hide expired clicked: " + value);
            openaksess.admin.userpreferences.setPreference(openaksess.admin.userpreferences.keys.filter.hideExpired, value, false, function() {
                openaksess.navigate.updateNavigator(openaksess.navigate.getCurrentItemIdentifier(), true);
            });
        });


        $filterOptions.find(".filtersToggle").live('click', function(){
            openaksess.common.debug("openaksess.content.bindFilterEvents(): Filter clicked. Opening infoslider widget.");
            $("#Navigation .infoslider").infoslider('option', {cssClasses: 'filters', floated: false}).infoslider('toggle', this, $filterOptions.find(".filters"));
        });

        $("#FilteroptionSort input[name=sort]").change(function(){
            var $this = $(this);
            openaksess.common.debug("openaksess.content.bindFilterEvents(): Sort clicked: "+ $this.val() + "="+$this.is(":checked"));
            if ($this.is(":checked")) {
                openaksess.admin.userpreferences.setPreference(openaksess.admin.userpreferences.keys.filter.sort, $this.val(), false, function() {
                    openaksess.navigate.updateNavigator(openaksess.navigate.getCurrentItemIdentifier(), true);
                });
            }
        });

        $("#FilteroptionSites input[name=sites]").change(function(){
            var hiddenSites = "";
            $("#FilteroptionSites input[name=sites]").each(function(){
                var $site = $(this);
                if (!$site.is(":checked")) {
                    if (hiddenSites != "") {
                        hiddenSites += ",";
                    }
                    hiddenSites += $site.val();
                }
            });

            openaksess.admin.userpreferences.setPreference(openaksess.admin.userpreferences.keys.filter.sites, hiddenSites, false, function() {
                openaksess.navigate.updateNavigator(openaksess.navigate.getCurrentItemIdentifier(), true);
            });

        });

        $("#hideexpiredFilteroptionSites_all").live('click',function(e){
            e.preventDefault();
            $("#Filteroptions .filters input[name=sites]").each(function(){
                $(this).attr("checked", "checked");
            });
            openaksess.admin.userpreferences.setPreference(openaksess.admin.userpreferences.keys.filter.sites, '', function() {
                openaksess.navigate.updateNavigator(openaksess.navigate.getCurrentItemIdentifier(), true);
            });

        });
    },

    /**
     * Fires an content update event for the given url
     * @url - Current url.
     */
    triggerContentUpdateEvent : function (url) {
        openaksess.common.debug("openaksess.content.triggerContentUpdateEvent(): url: " + url);
        openaksess.admin.notifyContentUpdate(url);
        //Event triggering is delegated to the state handler.
        stateHandler.setState(url);
    },

    /**
     * Publish tools.
     *
     * These are actions associated with the publish view.
     * Registers click event actions to each tool
     */
    bindToolButtons : function() {
        $("#ToolsMenu .button .newSubpage").click(function(){
            openaksess.content.publish.newSubpage(stateHandler.getState());
        });
        $("#ToolsMenu .button .delete").click(function(){
            openaksess.content.publish.deleteItem(stateHandler.getState());
        });
        $("#ToolsMenu .button .cut").click(function(){
            openaksess.content.publish.cut(stateHandler.getState());
        });
        $("#ToolsMenu .button .copy").click(function(){
            openaksess.content.publish.copy(stateHandler.getState());
        });
        $("#ToolsMenu .button .paste").click(function(){
            openaksess.content.publish.paste(stateHandler.getState());
        });
        $("#ToolsMenu .button .displayPeriod").click(function(){
            openaksess.content.publish.displayPeriod(stateHandler.getState());
        });
        $("#ToolsMenu .button .privileges").click(function(){
            openaksess.content.publish.managePrivileges(stateHandler.getState());
        });
    },


    /**
     * Actions associated with each tool.
     */
    publish : {
        open : function(url) {
            openaksess.common.debug("openaksess.content.publish.open(): url: " + url);
            openaksess.navigate.updateMainPane(url, false);
        },

        openInNewWindow : function(url) {
            window.open(properties.contextPath + "/content.ap" + url);
        },

        newSubpage : function(url) {
            openaksess.common.debug("openaksess.content.publish.newSubpage(): url: " + url);
            window.location.href = properties.contextPath + "/admin/publish/AddContent.action?url="+url;
        },

        edit: function(url) {
            openaksess.common.debug("openaksess.content.publish.editItem(): url: " + url);
            window.location.href = properties.contextPath + "/admin/publish/EditContent.action?url="+url;
        },

        deleteItem: function(url) {
            openaksess.common.debug("openaksess.content.publish.deleteItem(): url: " + url);
            openaksess.common.modalWindow.open({title:properties.content.labels.confirmDelete, iframe:true, href: properties.contextPath + "/admin/publish/DeleteAssociation.action?url=" + url,width: 450, height:250});
        },

        cut: function(url) {
            openaksess.common.debug("openaksess.content.publish.cut(): url: " + url);
            ContentClipboardHandler.cut(url);
            $(".contextMenu").enableContextMenuItems("#paste,#pasteAsShortcut");
            openaksess.content.contentstatus.enableButtons(['PasteButton']);
        },

        copy: function(url) {
            openaksess.common.debug("openaksess.content.publish.copy(): url: " + url);
            ContentClipboardHandler.copy(url);
            $(".contextMenu").enableContextMenuItems("#paste,#pasteAsShortcut");
            openaksess.content.contentstatus.enableButtons(['PasteButton']);
        },

        paste: function(url) {
            openaksess.common.debug("openaksess.content.publish.paste(): url: " + url);
            $(".contextMenu").disableContextMenuItems("#paste,#pasteAsShortcut");
            openaksess.content.contentstatus.disableButtons(['PasteButton']);
            openaksess.common.modalWindow.open({title:properties.content.labels.copyPaste, iframe:true, href: properties.contextPath + "/admin/publish/ConfirmCopyPaste.action?newParentUrl=" + url,width: 390, height:250});
        },

        pasteAsShortcut: function(url) {
            openaksess.common.debug("openaksess.content.publish.pasteAsShortcut(): url: " + url);
            $(".contextMenu").disableContextMenuItems("#paste,#pasteAsShortcut");
            openaksess.content.contentstatus.disableButtons(['PasteButton']);
            openaksess.common.modalWindow.open({title:properties.content.labels.copyPaste, iframe:true, href: properties.contextPath + "/admin/publish/ConfirmCopyPaste.action?pasteShortcut=true&amp;newParentUrl=" + url,width: 390, height:250});
        },

        displayPeriod: function(url) {
            openaksess.common.debug("openaksess.content.publish.displayPeriod(): url: " + url);
            openaksess.common.modalWindow.open({title:properties.content.labels.publishinfoPeriod, iframe:true, href: properties.contextPath + "/admin/publish/ViewDisplayPeriod.action?url=" + url,width: 350, height:220});
        },

        managePrivileges: function(url) {
            openaksess.common.debug("openaksess.content.publish.managePrivileges(): url: " + url);
            openaksess.common.modalWindow.open({title:properties.content.labels.editPermissions, iframe:true, href: properties.contextPath + "/admin/security/EditPermissions.action?url=" + url + "&type=" + properties.objectTypeAssociation,width: 650, height:560});
        },

        approve: function(url) {
            $.post(properties.contextPath + "/admin/publish/ApproveOrReject.action", {approve:true, url:url});
            openaksess.content.contentstatus.showApproveOrReject(false);
        },

        reject: function(url) {
            openaksess.common.modalWindow.open({title:properties.content.labels.reject, iframe:true, href: properties.contextPath + "/admin/publish/popups/RejectNote.action?url=" + url,width: 350, height:200});
        }
    },

    /**
     * Functions for updating the content status, i.e. the breadcrumbs, is it cross published, etc.
     */
    contentstatus : {
        lockedBy : "",


        init : function() {
            $("#Statusbar .statusDetails").remove();
            $("#Statusbar .crossPublish").hide();
            $("#Statusbar .brokenLink").hide();
        },

        breadcrumbs: function (path) {
            if (path) {
                var crumbs = '<ul class="breadcrumbs">';
                for (var i=0; i<path.length; i++) {
                    crumbs += "<li><a href=\"?thisId="+path[i].id+"\">"+path[i].title+"</a></li>";
                }
                crumbs += "</ul>";
                $("#Breadcrumbs").html(crumbs);
            }
        },

        brokenLinks: function (links) {
            if (links && links.length > 0) {
                openaksess.common.debug("openaksess.content.contentstatus.brokenLinks(): binding links icon to click. Number of links: " +links.length);
                $("#Statusbar .brokenLink").unbind('click').bind('click', function(){
                    openaksess.common.debug("openaksess.content.contentstatus.brokenLinks(): click");
                    var details = '<table>' +
                                '   <thead>' +
                                '       <tr>' +
                                '           <th class="field">' + properties.content.labels.linkcheckField + '</th>' +
                                '           <th class="url">' + properties.content.labels.linkcheckUrl + '</th>' +
                                '           <th class="status">' + properties.content.labels.linkcheckStatus + '</th>' +
                                '           <th class="lastChecked">' + properties.content.labels.linkcheckLastchecked + '</th>' +
                                '           <th class="timesChecked">' + properties.content.labels.linkcheckTimeschecked + '</th>' +
                                '       </tr>' +
                                '</thead>' +
                                '<tbody>';
                    for (var i = 0; i < links.length; i++) {
                        var statustxt = "";
                        if (links[i].status === 2) {
                            if (links[i].httpStatus == 401) {
                                statustxt = properties.content.labels.httpStatus401;
                            } else if (links[i].httpStatus == 404) {
                                statustxt = properties.content.labels.httpStatus404;
                            } else if (links[i].httpStatus == 500) {
                                statustxt = properties.content.labels.httpStatus500;
                            } else {
                                statustxt = "HTTP " + links[i].httpStatus;
                            }
                        } else {
                            statustxt = eval("properties.content.labels.linkcheckStatus" + links[i].status);
                        }

                        details += '<tr>' +
                                 '  <td>'+links[i].attributeName+'</td>' +
                                 '  <td>'+links[i].url+'</td>' +
                                 '  <td>'+statustxt+'</td>' +
                                 '  <td>'+links[i].lastChecked+'</td>' +
                                 '  <td>'+links[i].timesChecked+'</td>' +
                                 '</tr>';
                    }
                    details +='   </tbody>' +
                            '</table>';

                    $("#MainPane .infoslider").infoslider('option', {cssClasses: 'brokenlinks', floated: true, resizable: false}).infoslider('toggle', this, details);
                }).show();
            }
        },


        details: function(data) {
            var content = data.content;
            var details = "<h3>" + properties.content.labels.details + "</h3><ul>";

            if (content) {
                openaksess.common.debug("openaksess.content.contentstatus.details(): binding details icon to click");

                details += '<li><span class="label">' + properties.content.labels.contentTitle + ':</span>&nbsp;'+content.title+'</li>';
                if (content.alias) {
                    details += '<li><span class="label">' + properties.content.labels.publishinfoAlias + ':</span>&nbsp;'+content.alias+'</li>';
                }
                if (content.lastModified) {
                    details += '<li><span class="label">' + properties.content.labels.contentLastModified + ':</span>&nbsp;'+content.lastModified + ' ' + properties.content.labels.contentModifiedBy + ' ' + content.modifiedBy + '</li>';
                }
                if (content.modifiedBy != content.approvedBy) {
                    details += '<li><span class="label">' + properties.content.labels.contentApprovedBy + ':</span>&nbsp;'+content.approvedBy + '</li>';
                }
                if (content.changeFromDate) {
                    details += '<li><span class="label">' + properties.content.labels.contentChangeFrom + ':</span>&nbsp;'+content.changeFromDate+'</li>';
                }
                if (content.expireDate) {
                    details += '<li><span class="label">' + properties.content.labels.contentExpireDate + ':</span>&nbsp;'+content.expireDate+'</li>';
                }
                if (content.ownerperson) {
                    details += '<li><span class="label">' + properties.content.labels.contentOwnerPerson + ':</span>&nbsp;'+content.ownerperson+'</li>';
                }
            }

            var displayTemplate = data.displayTemplate;
            if (displayTemplate) {
                details += '<li><span class="label">' + properties.content.labels.contentDisplayTemplate + ':</span>&nbsp;'+displayTemplate.name+'&nbsp;('+displayTemplate.view+')</li>';
            }
            
            details +="</ul>";

            $("#Statusbar .details").unbind('click').bind('click', function(){
                openaksess.common.debug("openaksess.content.contentstatus.details(): click");
                $("#MainPane .infoslider").infoslider('option', {cssClasses: 'details', floated: true, resizable: false}).infoslider('toggle', this, details);
            }).show();
        },


        associations: function (associations) {
            if (associations && associations.length > 1) {
                openaksess.common.debug("openaksess.content.contentstatus.associations(): Number of associations: "+associations.length);
                $("#Statusbar .crossPublish").unbind('click').bind('click', function(){
                    openaksess.common.debug("openaksess.content.contentstatus.associations(): click");
                    var details = '<h3>' + properties.content.labels.associations + '</h3>';
                    for (var i = 0; i < associations.length; i++) {
                        details += '<ul class="breadcrumbs">';
                        for (var j = 0; j<associations[i].length; j++) {
                            details += '<li><a href="?thisId='+associations[i][j].id+'">' + associations[i][j].title + '</a></li>';
                        }
                        details += '</ul><div class="clearing"></div>';
                    }

                    $("#MainPane .infoslider").infoslider('option', {cssClasses: 'associations', floated: true, resizable: false}).infoslider('toggle', this, details);
                }).show();
            }
        },


        disableButtons: function() {
            $("#ToolsMenu a").addClass("disabled");
        },


        enableButtons: function(buttons) {
            if (buttons) {
                for (var i=0; i < buttons.length; i++) {
                    var b = buttons[i];
                    $("#" + b).removeClass("disabled");
                }
            }
        },


        showApproveOrReject: function(showButtons) {
            openaksess.common.debug("ContentStatus.showApproveOrReject: " + showButtons);
            var $approveButton = $("#EditContentButtons .approve");
            if ($approveButton.size() > 0) {
                var $buttons = $("#EditContentButtons");
                var isHidden = $buttons.is(":hidden");
                if (showButtons) {
                    $buttons.show();
                } else {
                    $buttons.hide();
                }
                if (isHidden != $buttons.is(":hidden")) {
                    $.event.trigger("resize");
                }
            }

        },

        showContentHints : function(hints) {
            var $navigateContentHints = $("#NavigateContentHints");
            var isHidden = $navigateContentHints.is(":hidden");

            if (hints && hints != '') {
                $navigateContentHints.html(hints);
                $navigateContentHints.show();
            } else {
                $navigateContentHints.hide();
            }
            if (isHidden != $navigateContentHints.is(":hidden")) {
                openaksess.common.debug("ContentStatus.showContentHints resizing to show content hints");
                $.event.trigger("resize");
            }
        },

        /**
         * Populates all content filters and sets the user's prefered values.
         * @param userPreferences - Key value pair of user preferences (which sites are hidden, sort etc)
         */
        updateFilters: function(userPreferences){

            var hiddenSites = [];
            var hideExpired = false;
            var sort = 'priority';

            if (userPreferences && userPreferences.length >0) {
                for (var i = 0; i < userPreferences.length; i++) {
                    if (userPreferences[i].key == openaksess.admin.userpreferences.keys.filter.sites) {
                        hiddenSites = userPreferences[i].value.split(",");
                    }
                    else if (userPreferences[i].key == openaksess.admin.userpreferences.keys.filter.hideExpired) {
                        hideExpired = (userPreferences[i].value == 'true');
                    }
                    else if (userPreferences[i].key == openaksess.admin.userpreferences.keys.filter.sort) {
                        sort = userPreferences[i].value;
                    }
                }
            }

            //Set sort order
            $("#FilteroptionSort input.radio").each(function(){
                var $this = $(this);
                if ($this.val() == sort) {
                    $this.attr("checked", "checked");
                }
            });

            $("#FilteroptionSites input.checkbox").each(function(){
                var $this = $(this);
                var isHidden = false;
                var siteId = $this.val();
                for (var j = 0; j < hiddenSites.length; j++) {
                    if (hiddenSites[j] == siteId) {
                        isHidden = true;
                    }
                }
                if (isHidden) {
                    $this.removeAttr("checked");
                } else {
                    $this.attr("checked", "checked");
                }
            });

            //Has the user chosen to hide expired elements?
            if (hideExpired) {
                $("#FilteroptionHideExpired").attr("checked", "checked");
            }

        }

    }


};



/********************************************************************************
 *
 * Content layout specific implementation of the openaksess.admin.setLayoutSpecificSizes.
 * Overrides the default implementation. See admin.js for further details.
 *
 ********************************************************************************/

openaksess.admin.setLayoutSpecificSizes = function (elementProperties){
    $("html, body").css("overflow", "hidden");
    var filteroptionsHeight = $("#Filteroptions").height(),
    statusbarHeight = $("#Statusbar").height(),
    navigationWidth = elementProperties.navigation.width,
    $navigator = $("#Navigator"),
    navigatorPaddingTop = $navigator.css("paddingTop"),
    navigatorPaddingBottom = $navigator.css("paddingBottom"),
    $buttons = $('#EditContentButtons'),
    $mainPane = $('#MainPane'),
    mainPaneHeight = (elementProperties.window.height-elementProperties.top.height),
    $content = $('#Content'),
    $contentHints = $('#NavigateContentHints'),
    $mainPaneContent = $("#MainPaneContent"),
    mainPaneContentPaddingTop = 0,
    mainPaneContentPaddingBottom = 0;
    var $mainContentIframe = $("#Maincontent");

    if ($mainPaneContent) {
        mainPaneContentPaddingTop = $mainPaneContent.css("paddingTop");
        mainPaneContentPaddingBottom = $mainPaneContent.css("paddingBottom");
    }

    var buttonsHeight = 0;
    if ($buttons && !$buttons.is(":hidden")) {
        buttonsHeight = $buttons.height();
    }

    var contentHintsHeight = 0;
    if ($contentHints && !$contentHints.is(":hidden")) {
        contentHintsHeight = $contentHints.height();
    }

    var preferredNavigationWidth = openaksess.admin.userpreferences.getPreference(openaksess.admin.userpreferences.keys.content.navigationwidth);
    if (preferredNavigationWidth) {
        var $navigation = $("#Navigation");
        $navigation.width(preferredNavigationWidth + "px");
        navigationWidth = preferredNavigationWidth;
    }
    var mainPaneWidth = (elementProperties.window.width-navigationWidth-elementProperties.framesplit.width);


    openaksess.common.debug("openaksess.admin.setLayoutSpecificSizes(): filteroptionsHeight: "+filteroptionsHeight+", statusbarHeight"+statusbarHeight + ", buttonsHeight: " + buttonsHeight);

    $navigator.height(elementProperties.window.height-elementProperties.top.height-filteroptionsHeight-parseInt(navigatorPaddingTop)-parseInt(navigatorPaddingBottom));
    $content.height(elementProperties.window.height-elementProperties.top.height-statusbarHeight);
    $mainPane.height(mainPaneHeight).width(mainPaneWidth);
    $mainContentIframe.height(elementProperties.window.height-elementProperties.top.height-statusbarHeight-buttonsHeight-contentHintsHeight).width(mainPaneWidth);

    if ($mainPaneContent) {
        $mainPaneContent.height(mainPaneHeight-parseInt(mainPaneContentPaddingTop)-parseInt(mainPaneContentPaddingBottom)-statusbarHeight-buttonsHeight);
    }

};





/********************************************************************************
 *
 * Content layout specific implementations of various navigate functions.
 * See navigate.js for further details.
 *
 ********************************************************************************/

openaksess.navigate.navigatorResizeOnStart = function() {
    openaksess.common.debug("openaksess.content.navigatorResizeOnStart(): Adding overlay");
    var height = $("#MainPane").height();
    var width = $("#MainPane").width();
    var contentoverlay = $("<div/>").css({
        position: "absolute",
        height: height + "px",
        width: width + "px",
        background: "#ffffff",
        opacity: "0"
    }).attr("id", "Contentoverlay");

    $("#MainPane iframe[name=contentmain]").before(contentoverlay);
    openaksess.admin.userpreferences.deletePreference(openaksess.admin.userpreferences.keys.content.navigationwidth);
};

openaksess.navigate.navigatorResizeOnStop = function() {
    openaksess.common.debug("openaksess.content.navigatorResizeOnStop(): Removing overlay");
    openaksess.admin.userpreferences.setPreference(openaksess.admin.userpreferences.keys.content.navigationwidth, $("#Navigation").width());
    $.event.trigger("resize");
    $("#Contentoverlay").remove();
};

openaksess.navigate.navigatorResizeOnResize = function() {
    //TODO: Verify the performance of this in all browsers.
    $.event.trigger("resize");
};


/**
 * Sets the context (right click) menus in the navigator.
 */
openaksess.navigate.setContextMenus = function(clipboardEmpty) {
    openaksess.common.debug("openaksess.content.setContextMenus(): Clipboard empty: "+clipboardEmpty);
    var disabledElements = [];
    if (clipboardEmpty) {
        disabledElements[0] = 'paste';
        disabledElements[1] = 'pasteAsShortcut';
    }
    openaksess.navigate.setContextMenu("page", disabledElements);
    openaksess.navigate.setContextMenu("link", disabledElements);
    openaksess.navigate.setContextMenu("file", disabledElements);
    openaksess.navigate.setContextMenu("shortcut", disabledElements);

};

openaksess.navigate.getNavigatorAction = function() {
    return properties.contextPath + "/admin/publish/ContentNavigator.action";
};

/**
 * Handles selections in the context menu for menu items of type page.
 *
 * @param action The selected action e.g. 'copy'
 * @param href The value of the href-attribute for the selected item.
 */
openaksess.navigate.handleContextMenuClick_page = function(action, href) {
    openaksess.common.debug("openaksess.content.handleContextMenuClick_page(): action: " + action + ", href: " + href);
    switch (action) {
        case 'open':
            openaksess.content.publish.open(href);
            break;
        case 'openInNewWindow':
            openaksess.content.publish.openInNewWindow(href);
            break;
        case 'newSubpage':
            openaksess.content.publish.newSubpage(href);
            break;
        case 'edit':
            openaksess.content.publish.edit(href);
            break;
        case 'delete':
            openaksess.content.publish.deleteItem(href);
            break;
        case 'cut':
            openaksess.content.publish.cut(href);
            break;
        case 'copy':
            openaksess.content.publish.copy(href);
            break;
        case 'paste':
            openaksess.content.publish.paste(href);
            break;
        case 'pasteAsShortcut':
            openaksess.content.publish.pasteAsShortcut(href);
            break;
        case 'managePrivileges':
            openaksess.content.publish.managePrivileges(href);
            break;
    }
};

/**
 * Handles selections in the context menu for menu items of type link.
 *
 * @param action The selected action e.g. 'copy'
 * @param href The value of the href-attribute for the selected item.
 */
openaksess.navigate.handleContextMenuClick_link = function (action, href) {
    return openaksess.navigate.handleContextMenuClick_page(action, href);
};

/**
 * Handles selections in the context menu for menu items of type file.
 *
 * @param action The selected action e.g. 'copy'
 * @param href The value of the href-attribute for the selected item.
 */
openaksess.navigate.handleContextMenuClick_file = function (action, href) {
    return openaksess.navigate.handleContextMenuClick_page(action, href);
};

/**
 * Handles selections in the context menu for menu items of type shortcut.
 *
 * @param action The selected action e.g. 'copy'
 * @param href The value of the href-attribute for the selected item.
 */
openaksess.navigate.handleContextMenuClick_shortcut = function (action, href) {
    return openaksess.navigate.handleContextMenuClick_page(action, href);
};


/**
 * Returns the location object for the contentmain iframe.
 */
openaksess.navigate.getCurrentLocation = function() {
    openaksess.common.debug("openaksess.content.getCurrentLocation()");
    return document.getElementById("Contentmain").contentWindow.document.location;
};

/**
 * Return id (int)
 *
 * @param href Navigator href value
 */
openaksess.navigate.getItemIdentifierFromNavigatorHref = function (href) {
    var thisId = openaksess.common.getQueryParam("thisId", href);
    openaksess.common.debug("openaksess.content.getItemIdentifierFromNavigatorHref(): href: " + href + ", returns: " + thisId);
    return thisId;
};

/**
 * Method returns a URL
 */
openaksess.navigate.getCurrentItemIdentifier = function() {
    return stateHandler.getState();
};

openaksess.navigate.getNavigatorParams = function() {
    var params = new Object();
    if($("#NavigatorState .sort").html() != null) {
        params.sort = $("#NavigatorState .sort").html();
    }
    params.showExpired = !$("#FilteroptionHideExpired").is(":checked");
    return params;
};

/*
 * Return URL to search action
 */
openaksess.search.getSearchAction = function() {
    return properties.contextPath + "/admin/publish/Search.action";
};
