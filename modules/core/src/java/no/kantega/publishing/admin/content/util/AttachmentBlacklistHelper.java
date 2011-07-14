/*
 * Copyright 2010 Kantega AS
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

package no.kantega.publishing.admin.content.util;

import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.admin.content.action.AddAttachmentAction;
import no.kantega.publishing.admin.multimedia.action.UploadMultimediaAction;
import no.kantega.publishing.admin.multimedia.ajax.ViewUploadMultimediaFormAction;
import no.kantega.publishing.common.Aksess;
import org.springframework.web.multipart.MultipartFile;

/**
 * Utility class for black-listing of certain file types uploaded in the admin
 * user interface, i.e. as file attachment or in the multimedia archive.
 *
 * @author jogri
 * @see AddAttachmentAction
 * @see ViewUploadMultimediaFormAction
 * @see UploadMultimediaAction
 */
public class AttachmentBlacklistHelper {

    /**
     * Checks if the name of the uploaded file ends with a file type that is
     * contained within the list of black-listed file types returned by
     * {@link #getBlacklistedFileTypes()}.
     *
     * @param multipartFile
     * @return {@code true} if the file type is among one of those defined in the black-list; {@code false} otherwise.
     */
    public static boolean isFileTypeInBlacklist(MultipartFile multipartFile) {
        if (multipartFile == null) {
            return false;
        }

        boolean isBlacklisted = false;

        String[] blacklistedFileTypes = getBlacklistedFileTypes();
        if (blacklistedFileTypes != null) {
            for (String blacklistedFileType : blacklistedFileTypes) {
                if (multipartFile.getOriginalFilename().endsWith("." + blacklistedFileType)) {
                    Log.debug(AttachmentBlacklistHelper.class.getName(), "The file type (" + multipartFile.getOriginalFilename() + ") is blacklisted.");
                    isBlacklisted = true;
                }
            }
        }

        return isBlacklisted;
    }

    /**
     * Retrieves an array of black-listed file types from the project's config
     * file.
     * <p />
     * The property in the config file must be named
     * {@code attachment.filetypes.blacklisted}, and its values must be comma-
     * separated. Example:
     * <blockquote>
     *    <code>attachment.filetypes.blacklisted = doc,xls,ppt</code>
     * </blockquote>
     * <p />
     * If the {@code attachment.filetypes.blacklisted} property is empty or
     * missing, <i>all</i> file types can be uploaded.
     *
     * @return String array of black-listed file suffixes.
     */
    public static String[] getBlacklistedFileTypes() {
        String[] blacklistedFileTypes = null;
        
        try {
            blacklistedFileTypes = Aksess.getConfiguration().getStrings("attachment.filetypes.blacklisted");
        } catch (ConfigurationException ex) {
            Log.error(AttachmentBlacklistHelper.class.getName(), ex);
        } catch (IllegalArgumentException ex) {
            Log.error(AttachmentBlacklistHelper.class.getName(), ex);
        }

        return blacklistedFileTypes;
    }

    /**
     * Returns the error message to be displayed (in a JavaScript alert box)
     * when the user attempts to upload a black-listed file type.
     * You can set the message using a project-specific configuration file,
     * typically aksess-webapp.conf. If no message is set, a locale-specific
     * default message will be used.
     * <p />
     * Example:
     * <blockquote>
     *    <code>attachment.filetypes.blacklisted.errorMessage = The file type is not allowed.</code>
     * </blockquote>
     *
     * @return The error message
     */
    public static String getErrorMessage() {
        String errorMessage = LocaleLabels.getLabel("aksess.multimedia.filetype.blacklisted", Aksess.getDefaultAdminLocale());

        try {
            errorMessage = Aksess.getConfiguration().getString("attachment.filetypes.blacklisted.errorMessage", errorMessage);
        } catch (ConfigurationException ex) {
            Log.error(AttachmentBlacklistHelper.class.getName(), ex);
        } catch (IllegalArgumentException ex) {
            Log.error(AttachmentBlacklistHelper.class.getName(), ex);
        }

        return errorMessage;
    }
}
