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

package no.kantega.publishing.admin.category;

import no.kantega.publishing.org.OrgUnit;
import no.kantega.publishing.org.UserCallbackHandler;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentCategory;
import no.kantega.commons.exception.SystemException;

import java.util.List;

public interface CategoryManager {
    public List getCategories(Content current, ContentCategory parentCategory) throws SystemException;
    public List getCategoryPath(ContentCategory parentCategory) throws SystemException;
    public ContentCategory getCategoryById(int categoryId) throws SystemException;
    public ContentCategory getDefaultCategory(Content current) throws SystemException;
}
