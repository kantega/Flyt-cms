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
 * * editcontext.labels.selecttopic
 * * editcontext.labels.selectcontent
 * * editcontext.labels.selectorgunit
 * * editcontext.labels.warningMaxchoose
 * * editcontext.labels.warningElements
 * * editcontext.labels.adduser
 * * editcontext.labels.multimedia
 * * editcontext.labels.addrole
 * * editcontext.labels.editablelistValue
 *
 */

openaksess.editcontext = function()  {
    /*
     * Get elements from $select list as string
     */
    function getEntriesFromList(list) {
        var entries = "";

        for (var i=0; i < list.options.length; i++) {
            if (i > 0) {
                entries += ",";
            }
            entries += list.options[i].value;
        }
        return entries;
    }

    /*
     *  Used in URLs to force refresh
     */
    function getRefresh() {
        var dt = new Date();
        return "" + dt.getTime();
    }

    /*
     *  Update status set that page has been edited
     */
    return {
        focusField : null,
        focusFieldDefaultMaxWidth: 600,
        editIsModified : false,
        doInsertTag : false,
        doInsertUrl : false,

        setIsModified : function() {
            openaksess.editcontext.editIsModified = true;
        },

        saveAll : function() {
            // Save forms
            formSave();
            if (typeof tinyMCE != "undefined"){
                tinyMCE.triggerSave();
            }
        },

        isModified : function () {
            return openaksess.editcontext.editIsModified || (typeof tinyMCE != "undefined" && tinyMCE.activeEditor && tinyMCE.activeEditor.isDirty());
        },

        init : function() {
            openaksess.editcontext.bindFieldChangeListeners();
            openaksess.editcontext.setupRepeaterSorting();
        },

        bindFieldChangeListeners : function () {
            openaksess.common.debug("bindFieldChangeListeners");
            $("#EditContentForm").find(":input").change(openaksess.editcontext.setIsModified);
        },

        setupRepeaterSorting : function () {
            $("#EditContentPane").find("div.contentAttributeRepeater div.inputs").sortable({
                items: 'div.contentAttributeRepeaterRow',
                handle: '.repeaterHandle',
                not: 'a',
                axis: 'y',
                stop: function(e, ui){
                    var rows = $(ui.item).parent().find(".contentAttributeRepeaterRow");
                    rows.each(function(index) {
                        var html = $(this).html();
                        html = html.replace(/(_\d__dot_)/g, "_" + index + "__dot_");
                        $(this).html(html);
                    });
                }
            });
        },

        /*
         *  Sets field as focused element
         */
        setFocusField : function (field) {
            openaksess.common.debug("setFocusField:" + field.name);
            openaksess.editcontext.focusField = field;
        },


        blurField : function (field) {
            if(openaksess.editcontext.focusField == field ){
                openaksess.common.debug("blurField");
                openaksess.editcontext.focusField = null;
            } else if(openaksess.editcontext.focusField && field){
                openaksess.common.debug("blurField called from " + field.name + ", " + openaksess.editcontext.focusField.name + " was focused" );
            } else {
                openaksess.common.debug("blurField called but openaksess.editcontext.focusField was null");
            }

        },

        insertLink : function(attribs) {
            openaksess.common.debug("insertLink: " + JSON.stringify(attribs));
            var editor = getParent().tinymce.EditorManager.activeEditor;
            // from tinymce link plugin
            var data = attribs, selection = editor.selection, dom = editor.dom, selectedElm, anchorElm, initialText, onlyText;
            var href = data.href;
            initialText = selection.getContent({format : "text"});

            function isOnlyTextSelected(anchorElm) {
                var html = selection.getContent();

                // Partial html and not a fully selected anchor element
                if (/</.test(html) && (!/^<a [^>]+>[^<]+<\/a>$/.test(html) || html.indexOf('href=') == -1)) {
                    return false;
                }

                if (anchorElm) {
                    var nodes = anchorElm.childNodes, i;

                    if (nodes.length === 0) {
                        return false;
                    }

                    for (i = nodes.length - 1; i >= 0; i--) {
                        if (nodes[i].nodeType != 3) {
                            return false;
                        }
                    }
                }

                return true;
            }

            function insertLink() {
                var linkAttrs = {
                    href: href,
                    target: data.target ? data.target : null,
                    rel: data.rel ? data.rel : null,
                    "class": data["class"] ? data["class"] : null,
                    title: data.title ? data.title : null
                };

                if (anchorElm) {
                    editor.focus();

                    if (onlyText && data.text != initialText) {
                        if ("innerText" in anchorElm) {
                            anchorElm.innerText = data.text;
                        } else {
                            anchorElm.textContent = data.text;
                        }
                    }

                    dom.setAttribs(anchorElm, linkAttrs);

                    selection.select(anchorElm);
                    editor.undoManager.add();
                } else {
                    editor.execCommand('mceInsertLink', false, linkAttrs);
                }
            }

            openaksess.common.debug("insertLink: " + JSON.stringify(attribs));

            selectedElm = selection.getNode();
            anchorElm = dom.getParent(selectedElm, 'a[href]');
            onlyText = isOnlyTextSelected();

            data.text = initialText = anchorElm ? (anchorElm.innerText || anchorElm.textContent) : selection.getContent({format: 'text'});
            data.href = anchorElm ? dom.getAttrib(anchorElm, 'href') : '';

            if (anchorElm) {
                data.target = dom.getAttrib(anchorElm, 'target');
            }

            insertLink();

            openaksess.common.debug("insertLink done");
        },

        /**
         * Update topics
         */
        updateTopics : function (params) {
            var ths = this;
            $("#TopicList").load(properties.contextPath + "/admin/topicmaps/HandleContentTopics.action", params, function() {
                $(".topic > .buttonGroup > a.delete").click(function(event){
                    event.preventDefault();
                    var topicMapId = openaksess.common.getQueryParam("topicMapId", $(this).attr("href"));
                    var topicId = openaksess.common.getQueryParam("topicId", $(this).attr("href"));
                    openaksess.common.debug("bindTopicDeleteButtons(): click a.delete, topicId=" + topicId);
                    ths.removeTopic(topicId, topicMapId);
                });
                openaksess.common.debug("updateTopics(): received topics");
            });
        },

        addTopicAutocomplete : function() {
            var $input = $("#TopicInput");

            var defaultValue = $input.val();
            $input.focus(function(){
                $(this).val("");
            }).blur(function(){
                $(this).val(defaultValue);
            });

            var ths = this;
            $input.autocomplete({
                source: properties.contextPath + "/ajax/AutocompleteTopics.action",
                select: function(event, ui){
                    var topicName = ui.item.name;
                    var topic = ui.item.id.split(":");
                    openaksess.editcontext.focusField = null;
                    ths.addTopic(topic[0], topic[1], topicName);
                    $(this).blur();
                }
            });
        },

        autocompleteInsertIntoFormCallback : function(event, ui) {
            var idField = this.name.substring(0, this.name.length - 4);
            var value = ui.item.id;
            openaksess.common.debug("openaksess.editcontext.autocompleteInsertIntoFormCallback(): Option selected. Inserting value '"+value+"' into field '#"+idField+"'");
            $("#" + idField).val(value);
        },


        /**
         *  Remove topic from edited page
         */
        removeTopic : function  (topicId, topicMapId) {
            var params = new Object();
            params.topicId = topicId;
            params.topicMapId = topicMapId;
            params.remove = true;
            openaksess.editcontext.updateTopics(params);
            $.event.trigger("topicRemoved", {topicId: topicId, topicMapId: topicMapId});
        },



        /**
         *  Let user select a topic
         */
        selectTopic : function (formElement, multiple) {
            openaksess.editcontext.focusField = formElement;
            openaksess.common.modalWindow.open({title:properties.editcontext.labels.selecttopic, iframe:true, href: properties.contextPath + "/admin/topicmaps/SelectTopics.action?refresh=" + getRefresh(),width: 300, height:400});
        },

        /**
         *  Callback to add topic to current page
         */
        addTopic : function (topicMapId, topicId, topicName) {
            openaksess.editcontext.setIsModified();
            openaksess.common.debug("add topic: " + topicName + ", topicId:" + topicId + " topicMapId:" + topicMapId);
            if (openaksess.editcontext.focusField == null) {
                // Update topic list
                var params = new Object();
                params.topicId = topicId;
                params.topicMapId = topicMapId;
                params.add = true;
                openaksess.editcontext.updateTopics(params);
            } else {
                // Topic should be inserted into a input field
                openaksess.editcontext.insertValueAndNameIntoForm(topicMapId + ":" + topicId, topicName);
            }
            $.event.trigger("topicAdded", {topicId: topicId, topicMapId: topicMapId, topicName: topicName});
        },

        /*
         *  Popup window for selecting a page url
         */
        selectContentUrl : function (formElement) {
            openaksess.common.debug("selectContentUrl formElement: " + formElement);
            openaksess.editcontext.focusField = formElement;
            openaksess.editcontext.doInsertTag = true;
            openaksess.common.modalWindow.open({title:properties.editcontext.labels.selectcontent, iframe:true, href: properties.contextPath + "/admin/publish/popups/SelectContent.action?refresh=" + getRefresh(),width: 400, height:500});
        },

        /*
         *  Popup window for selecting a page url for content, attachemnt, external og multimedia.
         */
        selectUrl : function (formElement) {
            openaksess.common.debug("selectUrl formElement: " + formElement);
            openaksess.editcontext.focusField = formElement;
            openaksess.editcontext.doInsertTag = true;
            var href = properties.contextPath +"/publish/popups/SelectLink.action?url=" + encodeURI(formElement.value);
            openaksess.common.modalWindow.open({title:properties.editcontext.labels.selectcontent, iframe:true, href: href, width: 580, height:500});
        },

        /*
         *  Popup vindu for selecting a page id
         */
        selectContent : function (formElement, maxItems, startId, multiple, contentTemplate) {
            var items = 0;

            if (arguments.length < 2) {
                maxItems = 1;
            } else {
                var list = formElement.form[formElement.name + 'list'];
                if(list) {
                    items = list.length;
                }
            }
            if (arguments.length < 3) {
                startId = -1;
            }


            if (items >= maxItems) {
                alert(properties.editcontext.labels.warningMaxchoose + ' ' + maxItems + ' ' + properties.editcontext.labels.warningElements);
            } else {
                var selectContentUrl = properties.contextPath + "/admin/publish/popups/SelectContent.action?refresh=" + getRefresh() + "&startId=" + startId;
                if(typeof multiple != "undefined" && multiple){
                    selectContentUrl += "&multiple="+multiple;
                }
                if(typeof contentTemplate != "undefined" && contentTemplate){
                    selectContentUrl += "&contentTemplate="+contentTemplate;
                }
                openaksess.editcontext.focusField = formElement;
                openaksess.editcontext.doInsertTag = false;
                openaksess.common.modalWindow.open({title:properties.editcontext.labels.selectcontent, iframe:true, href: selectContentUrl ,width: 400, height:500});
            }
        },


        /*
         *  Popup window for selecting a user
         */
        selectUser : function (formElement) {
            openaksess.editcontext.focusField = formElement;
            openaksess.editcontext.doInsertTag = false;
            openaksess.common.modalWindow.open({title:properties.editcontext.labels.adduser, iframe:true, href: properties.contextPath + "/admin/security/SelectUsers.action?multiple=false&refresh=" + getRefresh(),width: 400, height:450});
        },

        /*
         *  Popup window for selecting a organizational unit
         */

        selectOrgunit : function (formElement) {
            openaksess.editcontext.focusField = formElement;
            openaksess.editcontext.doInsertTag = false;
            openaksess.common.modalWindow.open({title:properties.editcontext.labels.selectorgunit, iframe:true, href: properties.contextPath + "/admin/publish/popups/SelectOrgUnit.action?refresh=" + getRefresh(),width: 280, height:450});
        },

        /*
         *  Popup window for selecting media object
         */
        selectMultimedia : function (formElement, filter) {
            openaksess.editcontext.focusField = formElement;
            var id = -1;
            if (openaksess.editcontext.focusField.value != "") {
                id = openaksess.editcontext.focusField.value;
            }

            openaksess.editcontext.doInsertTag = false;
            openaksess.common.modalWindow.open({title:properties.editcontext.labels.multimedia, iframe:true, href: properties.contextPath + "/admin/multimedia/Navigate.action?id=" + id + "&filter=" + filter + "refresh=" + getRefresh(),width: 880, height:550});
        },

        /*
         *  Popup window for upload media object
         */
        uploadMultimedia : function (formElement) {
            openaksess.editcontext.focusField = formElement;

            openaksess.common.debug('uploadMultimedia' + formElement);
            openaksess.editcontext.doInsertTag = false;
            openaksess.common.modalWindow.open({title:properties.editcontext.labels.uploadmultimedia, iframe:true, href: properties.contextPath + "/admin/multimedia/ViewUploadMultimediaForm.action?fileUploadedFromEditor=true&refresh=" + getRefresh(),width: 450, height:450});
        },

        /*
         * Popup window for selecting media folder
         */
        selectMediaFolder : function (formElement) {
            openaksess.editcontext.focusField = eval(formElement);
            openaksess.editcontext.doInsertTag = false;
            openaksess.common.modalWindow.open({title:properties.editcontext.labels.multimedia, iframe:true, href: properties.contextPath + "/admin/publish/popups/SelectMediaFolder.action?refresh=" + getRefresh() ,width: 280, height:450});
        },


        /*
         * Popup window for selecting a role
         */
        selectRole : function (formElement) {
            openaksess.editcontext.focusField = formElement;
            openaksess.common.modalWindow.open({title:properties.editcontext.labels.addrole, iframe:true, href: properties.contextPath + "/admin/security/SelectRoles.action?multiple=false&refresh=" + getRefresh() ,width: 280, height:340});
        },


        /*
         * Popup for adding a list option used for editablelists
         */
        addListOption : function (formElement, attributeKey, language) {
            openaksess.editcontext.focusField = formElement;
            openaksess.common.modalWindow.open({title:properties.editcontext.labels.editablelistValue, iframe:true, href: properties.contextPath + "/admin/publish/EditListOption.action?attributeKey=" + escape(attributeKey) + "&language=" + language + "&refresh=" + getRefresh() ,width: 280, height:140});
        },


        /*
         * Adds option to select list
         */
        insertOptionIntoList : function (value) {
            var option = document.createElement("option");
            option.value = value;
            option.text = value;

            for(var i = 0; i < openaksess.editcontext.focusField.options.length; i++) {
                var o = openaksess.editcontext.focusField.options[i];
                if(value < o.value) {
                    try {
                        openaksess.editcontext.focusField.add(option, focusField.options[i]);
                    } catch(ex) { // IE
                        openaksess.editcontext.focusField.add(option, i);
                    }
                    break;
                } else if(i == openaksess.editcontext.focusField.options.length - 1) {
                    try {
                        openaksess.editcontext.focusField.add(option);
                    } catch (ex) {
                        openaksess.editcontext.focusField.add(option, null);
                    }

                    break;
                }

            }
            option.selected = true;
        },

        /*
         * Remove list option from list
         */
        removeOptionFromList : function removeOptionFromList(formElement, attributeKey, language) {
            $.post(properties.contextPath + "/admin/publish/RemoveListOption.action", {value:formElement.value, attributeKey: attributeKey, language:language}, function(data) {
                openaksess.common.debug("editable list - option remove:" + formElement.value);
                for (var i=0; i < formElement.options.length; i++) {
                    if (formElement.options[i].selected) {
                        formElement.options[i] = null;
                        openaksess.editcontext.setIsModified();
                    }
                }
            });
        },


        /*
         * Insert id and value into form field
         */
        insertValueAndNameIntoForm : function (id, text) {
            openaksess.editcontext.setIsModified();

            if (openaksess.editcontext.focusField != null) {
                var name = openaksess.editcontext.focusField.name;

                var textField = openaksess.editcontext.focusField.form.elements[name + 'text'];
                var listField = openaksess.editcontext.focusField.form.elements[name + 'list'];
                if (listField) {
                    // Field is a list
                    var found = false;
                    // Check if not added before
                    for(var i = 0; i < listField.options.length; i++) {
                        var val = listField.options[i].value ;
                        if(val == id) {
                            found = true;
                            break;
                        }
                    }
                    if(!found) {
                        listField.options[listField.options.length] = new Option(text, id, 0, 0);
                        openaksess.editcontext.focusField.value = getEntriesFromList(listField);
                    }
                } else if (textField) {
                    // Is textfield
                    openaksess.editcontext.focusField.value = "" + id;
                    textField.value = text;
                } else {
                    openaksess.editcontext.focusField.value = "" + id;
                }
            }
        },

        insertMultimedia: function(metadata) {
            openaksess.editcontext.insertValueAndNameIntoForm(metadata.id, metadata.name);
        },


        /*
         * Remove id and value from form
         */
        removeValueAndNameFromForm : function removeValueAndNameFromForm(field) {
            var frm = field.form;
            var text = frm.elements[field.name + 'text'];
            var list = frm.elements[field.name + 'list'];

            if (text) {
                openaksess.editcontext.setIsModified();
                field.value = "";
                text.value = "";
            } else if (list) {
                for (var i=0; i < list.options.length; i++) {
                    if (list.options[i].selected) {
                        list.options[i] = null;
                        openaksess.editcontext.setIsModified();
                    }
                }

                // Oppdaterer hidden felt med riktig verdi
                field.value = getEntriesFromList(list);
            } else {
                field.value = "";
            }
        },


        /*
         *  Move element in select list up or down
         */
        moveId : function (field, dir) {
            var list = field.form.elements[field.name + 'list'];

            for (var i=0; i < list.options.length; i++) {
                if (list.options[i].selected) {
                    if (dir < 0) {
                        if (i == 0) {
                            return;
                        }
                    } else if (dir > 0){
                        if (i == list.options.length - 1) {
                            return;
                        }
                    }

                    var tmpText  = list.options[i+dir].text;
                    var tmpValue = list.options[i+dir].value;

                    list.options[i+dir].text  = list.options[i].text;
                    list.options[i+dir].value = list.options[i].value;

                    list.options[i].text  = tmpText;
                    list.options[i].value = tmpValue;

                    list.options[i+dir].selected = true;

                    openaksess.editcontext.setIsModified();
                    field.value = getEntriesFromList(list);

                    return;
                }
            }
        },

        /*
         * Insert value into form
         */
        insertValueIntoForm : function (val) {
            openaksess.editcontext.setIsModified();
            if (openaksess.editcontext.focusField != null) {
                openaksess.editcontext.focusField.value = "" + val;
            } else {
                openaksess.common.debug("openaksess.editcontext.focusField == null");
            }
        },

        /*
         * Set flag indicating attachment should be deleted
         */
        removeAttachment : function (element) {
            openaksess.editcontext.setIsModified();
            var name = element.name;
            var field = element.form['delete_' + name];
            field.value = "1";

            // Remove link to down attachment
            var $parent = $(element).parent().parent();
            var $link = $(".fileattribute-link", $parent);
            $link.hide();

            // Hide delete button
            var $buttonGroup = $(".buttonGroup", $parent);
            $buttonGroup.hide();
        },

        clearDefaultValue : function(field, defaultValue) {
            if (field.value == defaultValue) {
                field.value = '';
            }
        },

        setDefaultValue : function(field, defaultValue) {
            if (field.value == '') {
                field.value = defaultValue;
            }
        },

        addRepeaterRow : function(repeaterPath) {
            $("#AddRepeaterRow").val(repeaterPath);
            saveContent("");
        },

        deleteRepeaterRow : function(repeaterId, elem) {
            var repeaterRow = $(elem).parents(".contentAttributeRepeaterRow");
            var repeater = $(repeaterRow).parents(".contentAttributeRepeater");
            var offset = $(repeater).find(".contentAttributeRepeaterRow").index(repeaterRow);

            var path = repeaterId + "[" + offset + "]";
            $("#DeleteRepeaterRow").val(path);
            saveContent("");
        },

        listHiddenAttributes : function() {
            openaksess.common.modalWindow.open({title: properties.editcontext.labels.addattribute, iframe:true, href: properties.contextPath + "/publish/popups/AddAttribute.action", width: 450, height:200});
        },

        showHiddenAttribute : function(id) {
            $("#" + id).removeClass("attributeHiddenEmpty");
            if ($(".attributeHiddenEmpty").size() == 0) {
                $("#AddAttributeContainer").hide();
            }
        }
    };
}();

function oaAutocompleteWidget() {
    /**
     * Flyt CMS specific extension of the jQueryUI autocomplete plugin.
     */
    $.widget('ui.oaAutocomplete', $.ui.autocomplete, {
        _create: function() {
            openaksess.common.debug("openaksesswidgets.oaAutocomplete._create(): Widget created");
            //Add event listeners to focus and blur on the input field.
            $(this.element).focus($.proxy(this._focus, this)).blur($.proxy(this._blur, this));
            $.ui.autocomplete.prototype._create.apply(this);
        },

        _focus: function(){
            openaksess.common.debug("openaksesswidgets.oaAutocomplete._focus(): Focus on " + this.element.attr("id"));
            if (this.element.val() == this.options.defaultValue) {
                this.element.val('');
            }
        },
        _blur: function(){
            var formElement = this.element,
                elementName = formElement[0].name,
                elementId = formElement[0].id;
            openaksess.common.debug("openaksesswidget.oaAutocomplete._blur(): Blur on " + elementId);
            if (formElement.val().length == 0) {
                formElement.val(this.options.defaultValue);
                var idField = elementName.substring(0, elementName.length - 4);
                $("#" + idField).val('');
            }
        }

    });
};

function oaAutocompleteMultimediaWidget() {
    /**
     * Multimedia specific extension of the Flyt CMS autocomplete plugin.
     */
    $.widget('ui.oaAutocompleteMultimedia', $.ui.oaAutocomplete, {

        _renderItem: function( ul, item ) {
            return $( "<li></li>" )
                .data( "item.autocomplete", item )
                .append( "<a>" + item.image + " " + item.label + "</a>" )
                .appendTo( ul );
        }

    });
};
if ($ && $.ui && $.ui.autocomplete) {
    oaAutocompleteWidget();
    oaAutocompleteMultimediaWidget();
} else {
    $(document).ready(oaAutocompleteWidget);
    $(document).ready(oaAutocompleteMultimediaWidget);
}
