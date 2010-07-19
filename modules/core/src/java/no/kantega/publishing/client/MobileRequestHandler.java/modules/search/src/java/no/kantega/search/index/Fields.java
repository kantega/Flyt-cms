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

package no.kantega.search.index;


public interface Fields {

    
    public static final String CONTENT_ID = "ContentId";
    public static final String ATTACHMENT_ID = "AttachmentId";
    public static final String CONTENT = "Content";
    public static final String TITLE = "Title";
    public static final String TITLE_UNANALYZED = "TitleUnanalyzed";
    public static final String ALT_TITLE = "AltTitle";
    public static final String DOCTYPE = "DocType";
    public static final String TYPE_CONTENT = "Content";
    public static final String TYPE_ATTACHMENT = "Attachment";
    public static final String ATTACHMENT_FILE_NAME = "AttachmentFileName";
    public static final String SUMMARY = "Summary";
    public static final int SUMMARY_LENGTH = 200;
    public static final String LAST_MODIFIED = "LastModified";
    public static final String FILE_TYPE = "FileType";
    public static final String TM_TOPICS = "TmTopics";
    public static final String CONTENT_PARENTS = "ContentParents";
    public static final String SITE_ID = "SiteId";
    public static final String LANGUAGE = "Language";
    public static final String KEYWORDS = "Keywords";
    public static final String ATTACHMENT_CONTENT_ID_REF = "AttachmentContentIdRef";
    public static final String CATEGORY = "Category";
    public static final String CONTENT_TEMPLATE_ID = "ContentTemplateId";
    public static final String CONTENT_UNSTEMMED = "ContentUnstemmed";
    public static final String CONTENT_STATUS = "ContentStatus";
    public static final String CONTENT_VISIBILITY_STATUS = "ContentVisibilityStatus";
    public static final String DOCUMENT_TYPE_ID = "DocumentTypeId";
    public static final String ALIAS = "Alias";

}
