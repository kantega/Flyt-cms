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
 * * objectTypeMultimedia
 * * multimedia.labels.confirmDelete
 * * multimedia.labels.copypaste
 * * multimedia.labels.editpermissions
 * * multimedia.labels.foldername
 * * multimedia.labels.aksessToolsUpload
 *
 */

var currentItemIdentifier = 0;

$(document).ready(function(){
    openaksess.common.debug("multimedia.$(document).ready(): multimedia");
    openaksess.multimedia.bindMultimediaupdateEvents();
    openaksess.multimedia.bindToolButtons();
    stateHandler.init("multimediaupdate");
    var currentFolder = openaksess.admin.userpreferences.getPreference(openaksess.admin.userpreferences.keys.multimedia.currentfolder);
    if (!currentFolder) {
        openaksess.common.debug("multimedia.$(document).ready(): No state found in user preferences. Opening root element.");
        currentFolder = currentItemIdentifier;
    } else {
        currentItemIdentifier = currentFolder;
    }
    openaksess.multimedia.triggerMultimediaupdateEvent(currentItemIdentifier);//Must be fired at startup in order to load the navigator
    openaksess.multimedia.addMediaitemClickListeners();
});


/********************************************************************************
 *
 * Namespace for multimediaspecific functions and objects.
 *
 ********************************************************************************/
openaksess.multimedia = {

    /**
     * Contains the binding of all elements that are listening to the multimediaupdate event.
     * New global listeners to this event should be added here.
     */
    bindMultimediaupdateEvents : function() {
        //Enables the navigator to listen to contentupdate events. Called every time a contentupdate event is fired.
        $("#Navigator").bind("multimediaupdate", function(e, itemIdentifier){
            openaksess.common.debug("openaksess.multimedia.bindMultimediaupdateEvents(): "+e.type +" event received");
            if (!suppressNavigatorUpdate) {
                openaksess.navigate.updateNavigator(itemIdentifier, true);
            } else {
                suppressNavigatorUpdate = false;
                openaksess.common.debug("openaksess.multimedia.bindMultimediaupdateEvents(): navigationUpdate suppressed");
            }
            openaksess.navigate.updateMainPane(itemIdentifier, suppressNavigatorUpdate);
        });

        $("#Statusbar").bind("multimediaupdate", function(e, itemIdentifier){
            openaksess.common.debug("openaksess.multimedia.bindMultimediaupdateEvents(): #Statusbar has received multimediaupdate event. itemIdentifier: " + itemIdentifier);
            openaksess.multimedia.multimediastatus.disableButtons();
            $.post(properties.contextPath + "/admin/multimedia/MultimediaProperties.action", {itemIdentifier: itemIdentifier}, function(data){
                openaksess.common.debug("openaksess.multimedia.update breadcrumbs");
                openaksess.multimedia.multimediastatus.breadcrumbs(data.path);
                openaksess.multimedia.multimediastatus.enableButtons(data.enabledButtons);
            }, "json");
        });
    },

    triggerMultimediaupdateEvent : function(url) {
        openaksess.common.debug("openaksess.multimedia.triggerMultimediaupdateEvent(): mediaupdate event triggered. Url: " + url);
        openaksess.admin.userpreferences.setPreference(openaksess.admin.userpreferences.keys.multimedia.currentfolder, url);
        //Event triggering is delegated to the state handler.
        stateHandler.setState(url);
    },

    /**
     * Registers click event actions to each tool
     */
    bindToolButtons : function() {
        $("#UploadButton").click(function() {
            if (!$(this).hasClass("disabled")) {
                openaksess.multimedia.tools.showUploadForm(currentItemIdentifier);
            }
        });
        $("#NewFolderButton").click(function() {
            if (!$(this).hasClass("disabled")) {
                openaksess.multimedia.tools.createMediaFolder(currentItemIdentifier);
            }
        });
        $("#DeleteFolderButton").click(function() {
            if (!$(this).hasClass("disabled")) {
                openaksess.multimedia.tools.deleteItem(currentItemIdentifier);
            }
        });
    },

    /**
     * Actions associated with each tool.
     */
    tools :  {
        edit: function(id) {
            openaksess.common.debug("Multimedia.edit(): id: " + id);
            window.location.href = "EditMultimedia.action?id=" + id;
        },

        deleteItem: function(id) {
            openaksess.common.debug("multimedia.Multimedia.deleteItem(): id: " + id);
            openaksess.common.modalWindow.open({title:properties.multimedia.labels.confirmDelete, iframe:true, href: properties.contextPath + "/admin/multimedia/DeleteMultimedia.action?id=" + id,width: 450, height:250});
        },

        cut: function(id) {
            openaksess.common.debug("Multimedia.cut(): id: " + id);
            MultimediaClipboardHandler.cut(id);
            $(".contextMenu").enableContextMenuItems("#paste");
            //openaksess.content.contentstatus.enableButtons(['PasteButton']);
        },

        copy: function(id) {
            openaksess.common.debug("Multimedia.copy(): id: " + id);
            MultimediaClipboardHandler.copy(id);
            $(".contextMenu").enableContextMenuItems("#paste");
            //openaksess.content.contentstatus.enableButtons(['PasteButton']);
        },

        paste: function(id) {
            openaksess.common.debug("Multimedia.paste(): id: " + id);
            $(".contextMenu").disableContextMenuItems("#paste");
            //openaksess.content.contentstatus.disableButtons(['PasteButton']);
            openaksess.common.modalWindow.open({title:properties.multimedia.labels.copypaste, iframe:true, href: properties.contextPath + "/admin/multimedia/ConfirmCopyPaste.action?newParentId=" + id,width: 390, height:250});
        },

        managePrivileges: function(id) {
            openaksess.common.debug("Multimedia.managePrivileges(): id: " + id);
            openaksess.common.modalWindow.open({title:properties.multimedia.labels.editpermissions, iframe:true, href: properties.contextPath + "/admin/security/EditPermissions.action?id=" + id + "&type=" + properties.objectTypeMultimedia,width: 650, height:560});
        },

        createMediaFolder : function(id) {
            var folderName = prompt(properties.multimedia.labels.foldername, '');
            if (folderName != null && folderName != "") {
                openaksess.common.debug("openaksess.multimedia.createMediaFolder(): itemIdentifier: " + id + ", name: " + folderName);
                $.post(properties.contextPath + "/admin/multimedia/CreateMediaFolder.action", {itemIdentifier: id, name : folderName }, function(success) {
                    openaksess.multimedia.triggerMultimediaupdateEvent(id);
                });
            }
        },

        showUploadForm : function(id) {
            openaksess.common.debug("openaksess.multimedia.showUploadForm(): parentId: " + id);
            openaksess.common.modalWindow.open({title:properties.multimedia.labels.aksessToolsUpload, iframe:true, href: properties.contextPath + "/admin/multimedia/ViewUploadMultimediaForm.action?parentId=" + id + "&dummy=" + new Date().getTime(), width: 450, height:350});
        }
    },

    /**
     * Functions for updating the content status, i.e. the breadcrumbs, is it cross published, etc.
     */
    multimediastatus : {

        breadcrumbs: function (path) {
            if (path) {
                var crumbs = '<ul class="breadcrumbs">';
                for (var i=0; i<path.length; i++) {
                    crumbs += "<li><a href=\"?itemIdentifier="+path[i].id+"\">"+path[i].title+"</a></li>";
                }
                crumbs += "</ul>";
                $("#Breadcrumbs").html(crumbs);
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
        }

    },

    /**
     * Adds click listeners to the different media types and decides actions on these clicks.
     */
    addMediaitemClickListeners : function() {
        $("#MultimediaFolders").click(function(event){
            var $media = $(event.target).closest("div[id^=Media]");
            if ($(event.target).hasClass("name")) {
                event.stopPropagation();
                openaksess.multimedia.editMediaName($media);
            } else if ($media.size() > 0) {
                if ($media.hasClass("folder")) {
                    openaksess.common.debug("openaksess.multimedia.addMediaitemClickListeners(): folder click recieved");
                    if (openaksess.multimedia.isNameEditInProgress($media)) {
                        openaksess.common.debug("openaksess.multimedia.addMediaitemClickListeners(): Name edit in progress");
                        openaksess.multimedia.updateMediaName($media);
                    } else {
                        var idAttr = $media.attr("id");
                        currentItemIdentifier = idAttr.substring("Media".length, idAttr.length);
                        openaksess.multimedia.triggerMultimediaupdateEvent(currentItemIdentifier);
                    }
                } else if ($media.hasClass("media")) {
                    openaksess.common.debug("openaksess.multimedia.addMediaitemClickListeners(): media click recieved");
                    if (openaksess.multimedia.isNameEditInProgress($media)) {
                        openaksess.common.debug("openaksess.multimedia.addMediaitemClickListeners(): Name edit in progress");
                        openaksess.multimedia.updateMediaName($media);
                    } else {
                        window.location.href = $media.find(".icon a").attr("href");
                    }
                }
            }
        });

    },


    getViewFolderAction : function() {
        return properties.contextPath + "/admin/multimedia/ViewFolder.action";
    },

    getEditAction : function() {
        return properties.contextPath + "/admin/multimedia/EditMultimedia.action";
    },

    /**
     * Enables inline editing of the media item's name.
     *
     * @param obj - Must be the object's containing element, i.e. a div with id=Media[..]
     */
    editMediaName : function(obj) {
        if ($("input", obj).length == 0) {
            var $name = $(obj).find(".name");
            var value = $name.html();
            var html = '<input type="hidden" class="oldName" value="' + value + '">';
            html += '<input type="text" class="newName" maxlength="255" value="' + value + '">';
            $name.html(html);
            $("input.newName", obj).keypress(function(e) {
                if (e.which == 13 || e.which == 0) {//Enter or tab
                    openaksess.multimedia.updateMediaName(obj);
                }
            }).click(function(e){
                e.stopPropagation();
            }).focus();
        }
    },

    isNameEditInProgress : function(obj) {
        return $("input.newName", obj).length > 0;
    },

    updateMediaName : function(obj) {
        var $obj = $(obj);
        var idAttr = $obj.attr('id');
        var id = idAttr.substring("Media".length, idAttr.length);
        var newName = $obj.find("input.newName").val();
        var oldName = $obj.find("input.oldName").val();
        openaksess.common.debug("openaksess.multimedia.updateMediaName(): id: "+id+", newName: "+newName+", oldName:"+oldName);

        if (newName.length < 1) {
            return;
        }

        if (oldName != newName) {
            openaksess.common.debug("openaksess.multimedia.updateMediaName(): id: " + id + ", name: " + newName);
            $.post(properties.contextPath + "/admin/multimedia/UpdateMediaName.action", {itemIdentifier: id, name : newName }, function(success) {
                // Update navigation menu
                openaksess.navigate.updateNavigator(currentItemIdentifier, true);
            });
        }
        $obj.find(".name").html(newName);
    }

};

/********************************************************************************
 *
 * Overridden functions from inherited namespaces.
 *
 ********************************************************************************/

/**
 * Changes the content of the main pane
 *
 * @param itemIdentifier
 * @param suppressNavigatorUpdate true/false.
 */
openaksess.navigate.updateMainPane = function(itemIdentifier, suppressNavigatorUpdate) {
    openaksess.common.debug("multimedia:openaksess.navigate.updateMainPane(): itemIdentifier: " + itemIdentifier + ", suppressNavigatorUpdate: " + suppressNavigatorUpdate);
    if (suppressNavigatorUpdate) {
        suppressNavigatorUpdate = true;
    }
    $("#MultimediaFolders").load(openaksess.multimedia.getViewFolderAction(), {itemIdentifier: itemIdentifier}, function(success){
        $("#MultimediaFolders img.thumbnail").lazyload({
            placeholder : "../bitmaps/blank.gif",
            container: $("#MultimediaFolders")
        });
    });
};


/**
 * Sets the context (right click) menus in the navigator.
 */
openaksess.navigate.setContextMenus = function(clipboardEmpty) {
    var disabledElements = [];
    if (clipboardEmpty) {
        disabledElements[0] = 'paste';
    }
    openaksess.navigate.setContextMenu("media", disabledElements);
    openaksess.navigate.setContextMenu("folder", disabledElements);
    //Disable certain tools in the root folder context menu
    disabledElements.push('newFile');
    disabledElements.push('delete');
    disabledElements.push('copy');
    disabledElements.push('cut');
    openaksess.navigate.setContextMenu("root", disabledElements);
};

openaksess.navigate.handleContextMenuClick_folder = function(action, href) {
    openaksess.common.debug("openaksess.content.handleContextMenuClick_page(): action: " + action + ", href: " + href);

    var id = openaksess.common.getQueryParam("itemIdentifier", href);
    switch (action) {
        case 'newFolder':
            openaksess.multimedia.tools.createMediaFolder(id);
            break;
        case 'newFile':
            openaksess.multimedia.tools.showUploadForm(id);
            break;
        case 'edit':
            openaksess.multimedia.tools.edit(id);
            break;
        case 'delete':
            openaksess.multimedia.tools.deleteItem(id);
            break;
        case 'cut':
            openaksess.multimedia.tools.cut(id);
            break;
        case 'copy':
            openaksess.multimedia.tools.copy(id);
            break;
        case 'paste':
            openaksess.multimedia.tools.paste(id);
            break;
        case 'managePrivileges':
            openaksess.multimedia.tools.managePrivileges(id);
            break;
    }
};

openaksess.navigate.handleContextMenuClick_media = function(action, href) {
    return openaksess.navigate.handleContextMenuClick_folder(action, href);
};


openaksess.navigate.getNavigatorAction = function() {
    return properties.contextPath + "/admin/multimedia/MultimediaNavigator.action";
};

openaksess.navigate.getClipBoardHandler = function() {
    return MultimediaClipboardHandler;
};


openaksess.navigate.getItemIdentifierFromNavigatorHref = function(href) {
    return openaksess.common.getQueryParam("itemIdentifier", href);
};

openaksess.navigate.getCurrentItemIdentifier = function() {
    return currentItemIdentifier;
};

openaksess.navigate.onNavigatorTitleClick = function(elm) {
    var href = elm.attr("href");
    currentItemIdentifier = openaksess.navigate.getItemIdentifierFromNavigatorHref(href);
    openaksess.common.debug("multimedia:openaksess.navigate.onNavigatorTitleClick(): elem.href: "+href+", currentItemIdentifier: "+currentItemIdentifier);
    if(elm.hasClass("media")) {
        openaksess.common.debug("multimedia:openaksess.navigate.onNavigatorTitleClick(): elem has class 'media'. Opening for edit.");
        window.location.href = openaksess.multimedia.getEditAction()+"?id="+currentItemIdentifier;
    } else {
        openaksess.common.debug("multimedia:openaksess.navigate.onNavigatorTitleClick(): elem does not have class 'media'. Triggering update event.");
        openaksess.multimedia.triggerMultimediaupdateEvent(currentItemIdentifier);
    }
};

/**
 * Navigation layout specific implementation of the navigatorResizeOnResize-function.
 * See navigate.js
 */
openaksess.navigate.navigatorResizeOnResize = function() {
    $(window).trigger("resize");
};

openaksess.search.getSearchAction = function() {
    return properties.contextPath + "/admin/multimedia/Search.action";
};


/**
 * Multimedia layout specific implementation. Overrides the default openaksess.admin.setLayoutSpecificSizes.
 *
 * @param elementProperties
 */
openaksess.admin.setLayoutSpecificSizes = function(elementProperties) {
    openaksess.common.debug("multimedia:openaksess.admin.setLayoutSpecificSizes.setLayoutSpecificSizes()");
    $("html, body").css("overflow", "hidden");
    $("#Navigator").css('height', (elementProperties.window.height-elementProperties.top.height-5) + 'px');
    $('#Content').css('height', (elementProperties.window.height-elementProperties.top.height) + 'px');

    var $mainPane = $('#MainPane');

    $mainPane.height( (elementProperties.window.height-elementProperties.top.height) + 'px');


    if (elementProperties.navigation.width) {
        var navigationWidth = elementProperties.navigation.width;
        var preferredNavigationWidth = openaksess.admin.userpreferences.getPreference(openaksess.admin.userpreferences.keys.multimedia.navigationwidth);
        if (preferredNavigationWidth) {
            var $navigation = $("#Navigation");
            $navigation.width(preferredNavigationWidth + "px");
            navigationWidth = preferredNavigationWidth;
        }
        $mainPane.width( (elementProperties.window.width-navigationWidth-elementProperties.framesplit.width) + 'px');
    } else {
        $mainPane.width( (elementProperties.window.width-elementProperties.framesplit.width-$("#SideBar").outerWidth(true)) + 'px');
    }

    var $multimediaFolders = $("#MultimediaFolders");
    if ($multimediaFolders) {
        var multimediaFoldersHeight = $mainPane.height()-parseInt($multimediaFolders.css("paddingTop"))-parseInt($multimediaFolders.css("paddingBottom"));
        $multimediaFolders.css('height',  multimediaFoldersHeight + 'px');
    }

    $('#MultimediaMain').height( (parseInt($mainPane.height())-parseInt($("#EditMultimediaButtons").outerHeight(true))) + 'px').width($mainPane.outerWidth());

};

openaksess.navigate.navigatorResizeOnStart = function() {
    openaksess.admin.userpreferences.deletePreference(openaksess.admin.userpreferences.keys.multimedia.navigationwidth);
};

openaksess.navigate.navigatorResizeOnStop = function() {
    openaksess.admin.userpreferences.setPreference(openaksess.admin.userpreferences.keys.multimedia.navigationwidth, $("#Navigation").width());
};




