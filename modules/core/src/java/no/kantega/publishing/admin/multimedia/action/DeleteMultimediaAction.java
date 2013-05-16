/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.multimedia.action;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.publishing.common.service.MultimediaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for handling deletion of multimedia objects, i.e. files or folders.
 */
@Controller
@RequestMapping("/admin/multimedia/DeleteMultimedia.action")
public class DeleteMultimediaAction {
    private static final String ERROR_VIEW = "/WEB-INF/jsp/admin/generic/popup-error.jsp";
    private static final String BEFORE_DELETE_VIEW = "/WEB-INF/jsp/admin/multimedia/delete/confirmdelete.jsp";
    private static final String CONFIRM_DELETE_VIEW = "/WEB-INF/jsp/admin/multimedia/reloadparent.jsp";

    /**
     * Ask if user wants to delete
     * @param model MVC model
     * @param itemIdToDelete id of the multimedia item (folder or file) to delete
     * @param currentNavigateItem the item currently active in the navigator. May potentially be different from the item to
     *                            delete if the user uses the context menu to select item to delete.
     * @param request current http request.
     * @return view asking the user to confirm or abort the delete action.
     */
    @RequestMapping(method = RequestMethod.GET)
    public String showConfirmDialog(Model model,
                                    @RequestParam("id") Integer itemIdToDelete,
                                    @RequestParam(required = false, defaultValue = "-1") Integer currentNavigateItem,
                                    HttpServletRequest request) {
        MultimediaService mediaService = new MultimediaService(request);

        Multimedia mm = mediaService.getMultimedia(itemIdToDelete);
        if (mm.getType() == MultimediaType.FOLDER) {
            model.addAttribute("message", "aksess.confirmdeletefolder.text");
        } else {
            model.addAttribute("message", "aksess.confirmdelete.text");
        }
        model.addAttribute("multimedia", mm);
        model.addAttribute("currentNavigateItem", currentNavigateItem);

        return BEFORE_DELETE_VIEW;
    }


    /**
     * Perform the actual delete operation.
     * @param model MVC model
     * @param itemIdToDelete id of the multimedia item (folder or file) to delete
     * @param currentNavigateItem the item currently active in the navigator. May potentially be different from the item to
     *                            delete if the user uses the context menu to select item to delete.
     * @param request current http request.
     * @return confirm view or error view if something happens, typically if an item is in use somewhere.
     */
    @RequestMapping(method = RequestMethod.POST)
    public String performDelete(Model model,
                                @RequestParam("id") Integer itemIdToDelete,
                                @RequestParam(required = false, defaultValue = "-1") Integer currentNavigateItem,
                                HttpServletRequest request) {
        MultimediaService mediaService = new MultimediaService(request);

        Multimedia mm = mediaService.getMultimedia(itemIdToDelete);
        if (mm != null) {
            // Go to the parent folder after deletion if attempting to delete the currently viewed folder,
            // otherwise stay at the current folder.
            int navigateTo = (currentNavigateItem == itemIdToDelete.intValue()) ?  mm.getParentId() : currentNavigateItem;
            model.addAttribute("navigateTo", navigateTo);

            if (mm.getType() == MultimediaType.FOLDER && (mm.getNoFiles() + mm.getNoSubFolders()) > 0) {
                try {
                    mediaService.deleteMultimediaFolder(itemIdToDelete);
                } catch (NotAuthorizedException e) {
                    model.addAttribute("message", "aksess.confirmdelete.notauthorized");
                    return ERROR_VIEW;
                }
            } else {
                try {
                    mediaService.deleteMultimedia(itemIdToDelete);
                } catch (ObjectInUseException e) {
                    model.addAttribute("error", "feil.no.kantega.publishing.common.exception.ObjectInUseException");
                    return ERROR_VIEW;
                } catch (NotAuthorizedException e) {
                    model.addAttribute("message", "aksess.confirmdelete.notauthorized");
                    return ERROR_VIEW;
                }
            }
        }
        model.addAttribute("message", "aksess.confirmdelete.multimedia.finished");
        return CONFIRM_DELETE_VIEW;
    }


}


