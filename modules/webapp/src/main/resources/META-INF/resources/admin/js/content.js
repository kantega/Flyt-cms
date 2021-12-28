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
 * Flyt CMS content related functions.
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
            if (url != 'about:blank') {
                $.get(properties.contextPath + "/admin/publish/ContentProperties.action", {url: url}, function (data) {
                    if (data) {
                        openaksess.content.contentstatus.breadcrumbs(data.path);
                        openaksess.content.contentstatus.brokenLinks(data.links);
                        openaksess.content.contentstatus.details(data.contentProperties);
                        openaksess.content.contentstatus.associations(data.associations);
                        openaksess.content.contentstatus.enableButtons(data.enabledButtons);
                        openaksess.content.contentstatus.showApproveOrReject(data.showApproveButtons);
                        openaksess.content.contentstatus.updateFilters(data.userPreferences);
                        openaksess.content.contentstatus.showContentHints(data.contentHints);
                        openaksess.content.contentstatus.lockedBy = data.lockedBy;
                    }
                }, "json");
            }
        });

        $("#ContentmainContainer").bind("contentupdate", function(e, url){
            // Fix iframe scrolling on iPad - wait 3 sec to allow iframe to start loading
            setTimeout(function() {
                openaksess.common.addTouchScrollToIFrame("#Contentmain", "#ContentmainContainer");
            }, 3000);
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
        $("#ToolsMenu .button .newSubpage").click(function(event){
            if ($(event.target).parent().hasClass("disabled")) return false;
            openaksess.content.publish.newSubpage(stateHandler.getState());
            return false;
        });
        $("#ToolsMenu .button .delete").click(function(event){
            if ($(event.target).parent().hasClass("disabled")) return false;
            openaksess.content.publish.deleteItem(stateHandler.getState());
            return false;
        });
        $("#ToolsMenu .button .cut").click(function(event){
            if ($(event.target).parent().hasClass("disabled")) return false;
            openaksess.content.publish.cut(stateHandler.getState());
            return false;
        });
        $("#ToolsMenu .button .copy").click(function(event){
            if ($(event.target).parent().hasClass("disabled")) return false;
            openaksess.content.publish.copy(stateHandler.getState());
            return false;
        });
        $("#ToolsMenu .button .paste").click(function(event){
            if ($(event.target).parent().hasClass("disabled")) return false;
            openaksess.content.publish.paste(stateHandler.getState());
            return false;
        });
        $("#ToolsMenu .button .displayPeriod").click(function(event){
            if ($(event.target).parent().hasClass("disabled")) return false;
            openaksess.content.publish.displayPeriod(stateHandler.getState());
            return false;
        });
        $("#ToolsMenu .button .privileges").click(function(event){
            if ($(event.target).parent().hasClass("disabled")) return false;
            openaksess.content.publish.managePrivileges(stateHandler.getState());
            return false;
        });
    },


    /**
     * Actions associated with each tool.
     */
    publish : {
        open : function(url) {
            var associationId = openaksess.common.getQueryParam("thisId", url);
            openaksess.common.debug("openaksess.content.publish.open(): associationId: " + associationId);
            openaksess.navigate.updateMainPane(associationId, false);
        },

        openInNewWindow : function(url) {
            window.open(properties.contextPath + url);
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
            openaksess.common.modalWindow.open({title:properties.content.labels.copyPaste, iframe:true, href: properties.contextPath + "/admin/publish/ConfirmCopyPaste.action?pasteShortcut=true&newParentUrl=" + url,width: 390, height:250});
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
                openaksess.common.debug("openaksess.contentstatus.breadcrumbs(): Bread crumbs size: " + path.length + " elements");
                for (var i=0; i<path.length; i++) {
                    var visibleTitle = path[i].title;
                    //Truncate the path if it's 5 or more elements. Do not trucate the first or two last elements.
                    if (path.length > 4 && i < path.length-2) {
                        visibleTitle = openaksess.common.abbreviate(path[i].title, 3);
                    }
                    //Abbreviate all titles that are longer than 20 characters
                    if (visibleTitle.length > 20) {
                        visibleTitle = openaksess.common.abbreviate(visibleTitle, 20);
                    }
                    crumbs += "<li><a href=\"?thisId="+path[i].id+"\" title=\""+path[i].title+"\">"+visibleTitle+"</a></li>";
                }
                crumbs += "</ul>";
                $("#Breadcrumbs").html(crumbs);
            }
        },

        brokenLinks: function (links) {
            if (links && links.length > 0) {
                openaksess.common.debug("openaksess.content.contentstatus.brokenLinks(): binding links icon to click. Number of links: " +links.length);

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
                    var link = links[i];
                    if (link.status === 'HTTP_NOT_200') {
                        if (link.httpStatus == 401) {
                            statustxt = properties.content.labels.httpStatus401;
                        } else if (link.httpStatus == 404) {
                            statustxt = properties.content.labels.httpStatus404;
                        } else if (link.httpStatus == 500) {
                            statustxt = properties.content.labels.httpStatus500;
                        } else {
                            statustxt = "HTTP " + link.httpStatus;
                        }
                    } else {
                        statustxt = eval("properties.content.labels.linkcheckStatus" + link.status);
                    }

                    var lastCheckedStamp = link.lastChecked;
                    if (lastCheckedStamp) {
                        var lastChecked = new Date(lastCheckedStamp);
                        var lastCheckedFormated = lastChecked.getDate() + '-' + (lastChecked.getMonth() + 1)
                            + '-' + lastChecked.getFullYear();
                        details += '<tr>' +
                        '  <td>' + link.attributeName + '</td>' +
                        '  <td>' + link.url + '</td>' +
                        '  <td>' + statustxt + '</td>' +
                        '  <td>' + lastCheckedFormated + '</td>' +
                        '  <td>' + link.timesChecked + '</td>' +
                        '</tr>';
                    }
                }
                details +='   </tbody>' +
                        '</table>';

                openaksess.content.contentstatus.bindInfoSliderTrigger("#Statusbar .brokenLink", "brokenlinks", details);
            } else {
                openaksess.content.contentstatus.unbindInfoSliderTrigger("#Statusbar .brokenLink");
            }
        },


        details: function(contentProperties) {
            var details = "<h3>" + properties.content.labels.details + "</h3><ul>";

            if (contentProperties) {
                openaksess.common.debug("openaksess.content.contentstatus.details(): binding details icon to click");

                details += '<li><span class="label">' + properties.content.labels.contentTitle + ':</span>&nbsp;'+contentProperties.title+'</li>';
                if (contentProperties.alias) {
                    details += '<li><span class="label">' + properties.content.labels.publishinfoAlias + ':</span>&nbsp;'+contentProperties.alias+'</li>';
                }
                if (contentProperties.lastModified) {
                    details += '<li><span class="label">' + properties.content.labels.contentLastModified + ':</span>&nbsp;'+contentProperties.lastModified + ' ' + properties.content.labels.contentModifiedBy + ' ' + contentProperties.lastModifiedBy + '</li>';
                }
                if (contentProperties.modifiedBy != contentProperties.approvedBy) {
                    details += '<li><span class="label">' + properties.content.labels.contentApprovedBy + ':</span>&nbsp;'+contentProperties.approvedBy + '</li>';
                }
                if (contentProperties.changeFromDate) {
                    details += '<li><span class="label">' + properties.content.labels.contentChangeFrom + ':</span>&nbsp;'+contentProperties.changeFromDate+'</li>';
                }
                if (contentProperties.expireDate) {
                    details += '<li><span class="label">' + properties.content.labels.contentExpireDate + ':</span>&nbsp;'+contentProperties.expireDate+'</li>';
                }
                if (contentProperties.ownerperson) {
                    details += '<li><span class="label">' + properties.content.labels.contentOwnerPerson + ':</span>&nbsp;'+contentProperties.ownerperson+'</li>';
                }
                if (contentProperties.owner) {
                    details += '<li><span class="label">' + properties.content.labels.contentOwner + ':</span>&nbsp;'+contentProperties.owner+'</li>';
                }

                var displayTemplate = contentProperties.displayTemplate;
                if (displayTemplate) {
                    details += '<li><span class="label">' + properties.content.labels.contentDisplayTemplate + ':</span>&nbsp;'+displayTemplate.name+'&nbsp;('+displayTemplate.view+')</li>';
                }

                var contentTemplate = contentProperties.contentTemplate;
                if (contentTemplate) {
                    details += '<li><span class="label">' + properties.content.labels.contentContentTemplate + ':</span>&nbsp;'+contentTemplate.name+'&nbsp;('+contentTemplate.templateFile+')</li>';
                }

            }

            details +="</ul>";

            openaksess.content.contentstatus.bindInfoSliderTrigger("#Statusbar .details", "details", details);

        },


        associations: function (associations) {
            if (associations && associations.length > 1) {
                openaksess.common.debug("openaksess.content.contentstatus.associations(): Number of associations: "+associations.length);

                var details = '<h3>' + properties.content.labels.associations + '</h3>';
                for (var i = 0; i < associations.length; i++) {
                    details += '<ul class="breadcrumbs">';
                    for (var j = 0; j<associations[i].length; j++) {
                        details += '<li><a href="?thisId='+associations[i][j].id+'">' + associations[i][j].title + '</a></li>';
                    }
                    details += '</ul><div class="clearing"></div>';
                }

                openaksess.content.contentstatus.bindInfoSliderTrigger("#Statusbar .crossPublish", 'associations', details);
            } else {
                openaksess.content.contentstatus.unbindInfoSliderTrigger("#Statusbar .crossPublish");
            }
        },

        /**
         * Sets up the infoslider for each of the triggers, ie. broken links, associations, content details etc.
         * @param triggerSelector jQuery selector for the element that triggers the infoslider.
         * @param cssClass Infoslider css class for this trigger
         * @param content Infoslider content.
         */
        bindInfoSliderTrigger: function(triggerSelector, cssClass, content) {
            openaksess.common.debug("ContentStatus.bindInfoSliderTrigger(): triggerSelector: " + triggerSelector + ", cssClass: " + cssClass);
            var infoslider = $("#MainPane .infoslider"),
            infosliderTrigger = $(triggerSelector);

            //initialize the infoslider
            infoslider.infoslider('option', {cssClasses: cssClass, floated: true, resizable: false});
            //replaceContentIfOpen will keep the slider open and replace the content
            //if the current trigger is the trigger that opened the slider in the first place
            infoslider.infoslider('replaceContentIfOpen', infosliderTrigger[0], content);

            //Toggle the info slider upon trigger click.
            infosliderTrigger.unbind('click').bind('click', function(event){
                event.preventDefault();
                openaksess.common.debug("openaksess.content.contentstatus.bindInfoSliderTrigger(): click");
                infoslider.infoslider('toggle', infosliderTrigger[0], content);
            }).show();
        },

        /**
         * Removes info slider trigger for the given element.
         * @param triggerSelector jQuery selector.
         */
        unbindInfoSliderTrigger: function(triggerSelector) {
            openaksess.common.debug("ContentStatus.unbindInfoSliderTrigger(): triggerSelector: " + triggerSelector);
            $("#MainPane .infoslider").infoslider('close', $(triggerSelector)[0]);
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
                    $(window).trigger("resize");
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
                $(window).trigger("resize");
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
    var $mainContentIframeContainer = $("#ContentmainContainer");

    if ($mainPaneContent.size() > 0) {
        mainPaneContentPaddingTop = parseInt($mainPaneContent.css("paddingTop"));
        mainPaneContentPaddingBottom = parseInt($mainPaneContent.css("paddingBottom"));
    }

    var buttonsHeight = 0;
    if ($buttons.size() > 0 && !$buttons.is(":hidden")) {
        buttonsHeight = $buttons.height();
    }

    var contentHintsHeight = 0;
    if ($contentHints.size() > 0 && !$contentHints.is(":hidden")) {
        contentHintsHeight = $contentHints.outerHeight();
    }

    var preferredNavigationWidth = openaksess.admin.userpreferences.getPreference(openaksess.admin.userpreferences.keys.content.navigationwidth);
    if (preferredNavigationWidth) {
        var $navigation = $("#Navigation");
        $navigation.width(preferredNavigationWidth + "px");
        navigationWidth = preferredNavigationWidth;
    }
    var mainPaneWidth = (elementProperties.window.width-navigationWidth-elementProperties.framesplit.width)-2;


    openaksess.common.debug("openaksess.content.setLayoutSpecificSizes(): filteroptionsHeight: "+filteroptionsHeight+", statusbarHeight"+statusbarHeight + ", buttonsHeight: " + buttonsHeight);

    $navigator.height(elementProperties.window.height-elementProperties.top.height-filteroptionsHeight-parseInt(navigatorPaddingTop)-parseInt(navigatorPaddingBottom));
    $content.height(elementProperties.window.height-elementProperties.top.height-statusbarHeight);
    $mainPane.height(mainPaneHeight).width(mainPaneWidth);
    if ($mainContentIframeContainer.size() > 0) {
        $mainContentIframeContainer.height(elementProperties.window.height-elementProperties.top.height-statusbarHeight-buttonsHeight-contentHintsHeight).width(mainPaneWidth);
    }

    if ($mainPaneContent.size() > 0) {
        $mainPaneContent.height(mainPaneHeight-mainPaneContentPaddingTop-mainPaneContentPaddingBottom-statusbarHeight-buttonsHeight);
    }

};

/**
 * Under ideal conditions, the body should be as high as the view port (window).
 * Sometimes, for example when changing font size, the resize calculations fail, often beacause of a
 * temporary scroll bar added during the font size change process. This will cause the content frame iFrame
 * to wrap below the view port.
 *
 * To check if the content frame has wrapped below the window we check if the total body height is
 * higher than the view port.
 */
openaksess.admin.isResizeNecessary = function() {
    //Add 100px to the calculation just to make sure that it's acutually a frame wrap situation that
    //has occurred, not just an odd pixel difference.
    return $(document).height() > $(window).height()+100;
};





/********************************************************************************
 *
 * Content layout specific implementations of various navigate functions.
 * See navigate.js for further details.
 *
 ********************************************************************************/

openaksess.navigate.navigatorResizeOnStart = function() {
    openaksess.common.debug("openaksess.content.navigatorResizeOnStart(): Adding overlay");
    var mainPane = $("#MainPane");
    var height = mainPane.height();
    var width = mainPane.width();
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
    $(window).trigger("resize");
    $("#Contentoverlay").remove();
};

openaksess.navigate.navigatorResizeOnResize = function() {
    //TODO: Verify the performance of this in all browsers.
    openaksess.common.debug("openaksess.content.navigatorResizeOnResize(): Triggering resize event");
    $(window).trigger("resize");
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

openaksess.navigate.getClipBoardHandler = function() {
    return ContentClipboardHandler;
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
    return document.getElementById("Contentmain").contentWindow.document.location;
};

/**
 * Return id (int)
 *
 * @param href Navigator href value
 */
openaksess.navigate.getItemIdentifierFromNavigatorHref = function (href) {
    var thisId = openaksess.common.getQueryParam("thisId", href);
    if(thisId === null){
        var urlPattern = /\/content\/(\d+)\/.*/g;
        thisId = href.match(/\/content\/(\d+)\/.*/)[1];
    }
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
    var html = $("#NavigatorState .sort").html();
    if(html != null) {
        params.sort = html;
    }
    params.showExpired = !$("#FilteroptionHideExpired").is(":checked");
    return params;
};




/**
 * Changes the content of the contentmain iframe.
 * Such a change will trigger a contentupdate trigger if not suppressNavigatorUpdate is explicitly set to true
 *
 * Overrides the default implementation. See navigate.js
 *
 * If no arguments are supplied, i.e. no id=undefined and suppressNavigatorUpdate=undefined, an iframe reload is implied.
 *
 * @param id
 * @param suppressNavigatorUpdate true/false. A contentupdate event will be triggered unless set to true.
 */
openaksess.navigate.updateMainPane = function(id, suppressNavigatorUpdate) {
    openaksess.common.debug("navigate.updateMainPane(): itemIdentifier: " + id + ", suppressNavigatorUpdate: "+suppressNavigatorUpdate);
    if (suppressNavigatorUpdate) {
        suppressNavigatorUpdate = true;
    }
    var iframe = document.getElementById("Contentmain");
    if (iframe) {
        if (id == undefined && suppressNavigatorUpdate == undefined) {
            iframe.contentWindow.document.location.reload();
        } else {
            iframe.src = openaksess.common.getContentUrlFromAssociationId(id);
        }

    }
};


/*
 * Return URL to search action
 */
openaksess.search.getSearchAction = function() {
    return properties.contextPath + "/admin/publish/Search.action";
};
