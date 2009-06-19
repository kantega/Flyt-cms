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

package no.kantega.publishing.common.data.attributes;

import no.kantega.publishing.admin.category.CategoryManager;
import no.kantega.publishing.spring.RootContext;
import no.kantega.publishing.common.data.ContentCategory;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.exception.RegExpSyntaxException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;

import java.util.Iterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.springframework.context.ApplicationContext;

public class CategoryAttribute extends Attribute {

    public ContentCategory getValueAsCategory() {
        ContentCategory category = null;

        if (value == null || value.length() == 0) {
            return category;
        }

        int categoryId = Integer.parseInt(value);
        ApplicationContext context = RootContext.getInstance();
        Iterator i = context.getBeansOfType(CategoryManager.class).values().iterator();
        if(i.hasNext()) {
            CategoryManager manager = (CategoryManager) i.next();
            try {
                category = manager.getCategoryById(categoryId);
            } catch (SystemException e) {
                Log.error("CategoryAttribute", e, null, null);
            }
        }
        return category;
    }

    public String getRenderer() {
        return "category";
    }
}
