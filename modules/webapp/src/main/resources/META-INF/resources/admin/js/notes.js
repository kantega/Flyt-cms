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
 * Namespace for notes
 ********************************************************************************/
openaksess.notes = {
    currentUrl : "",
    /**
     * List notes for this page
     */
    listNotes : function() {
        openaksess.common.debug("openaksess.notes.listNotes(): Triggering contentupdate event");
        openaksess.content.triggerContentUpdateEvent(openaksess.notes.currentUrl);

        openaksess.common.debug("openaksess.notes.listNotes(): Calling ListBrokenLinks.action");
        $("#Notes").load(properties.contextPath + "/admin/publish/ListNotes.action", {itemIdentifier: openaksess.notes.currentUrl}, function(success){
            openaksess.common.debug("openaksess.notes.listNotes(): response from ListNotes.action received");
        });
    },

    addNote : function (note) {
        openaksess.common.debug("openaksess.notes.addNote(): Adding note for "  + openaksess.notes.currentUrl);
        if (note != "") {
            $("#Notes").load(properties.contextPath + "/admin/publish/AddNote.action", {itemIdentifier: openaksess.notes.currentUrl, note : note}, function(success){
                openaksess.common.debug("openaksess.notes.addNote(): response from AddNote.action received");
            });
        }
    },

    deleteNote : function (noteId) {
        openaksess.common.debug("openaksess.notes.deleteNote(): Delete note for "  + openaksess.notes.currentUrl + ", noteId:" + noteId);
        $("#Notes").load(properties.contextPath + "/admin/publish/DeleteNote.action", {itemIdentifier: openaksess.notes.currentUrl, noteId : noteId}, function(success){
            openaksess.common.debug("openaksess.notes.deleteNote(): response from DeleteNote.action received");
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
    openaksess.common.debug("notes.updateMainPane(): id: " + id);
    openaksess.notes.currentUrl = openaksess.common.getContentUrlFromAssociationId(id);
    openaksess.common.debug("notes.updateMainPane(): currentUrl: " + openaksess.notes.currentUrl);
    openaksess.notes.listNotes();
};
