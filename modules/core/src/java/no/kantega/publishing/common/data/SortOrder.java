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

package no.kantega.publishing.common.data;

import no.kantega.commons.util.StringHelper;
import no.kantega.publishing.common.data.enums.ContentProperty;

public class SortOrder {
    private String sort1 = null;
    private String sort2 = null;

    private boolean descending = false;

    public SortOrder(String sortOrder) {
        if (sortOrder.indexOf(",") != -1) {
            sort1 = sortOrder.substring(0, sortOrder.indexOf(",")).trim();
            sort2 = sortOrder.substring(sortOrder.indexOf(",") + 1, sortOrder.length()).trim();
        } else {
            sort1 = sortOrder.trim();
        }

        if (sort1.equalsIgnoreCase(ContentProperty.RATING_SCORE) ||
            sort1.equalsIgnoreCase(ContentProperty.LAST_MODIFIED) ||
            sort1.equalsIgnoreCase(ContentProperty.LAST_MAJOR_CHANGE) ||
            sort1.equalsIgnoreCase(ContentProperty.PUBLISH_DATE) ||
            sort1.equalsIgnoreCase(ContentProperty.NUMBER_OF_COMMENTS) ||
            sort1.equalsIgnoreCase(ContentProperty.NUMBER_OF_RATINGS)) {
            this.descending = true;
        } else {
            this.descending = false;
        }

    }

    public SortOrder(String sortOrder, boolean descending) {
        if (sortOrder.indexOf(",") != -1) {
            sort1 = sortOrder.substring(0, sortOrder.indexOf(",")).trim();
            sort2 = sortOrder.substring(sortOrder.indexOf(",") + 1, sortOrder.length()).trim();
        } else {
            sort1 = sortOrder.trim();
        }
        this.descending = descending;
    }

    public String getSqlSort() {
        String sort = null;
        if (sort1.equalsIgnoreCase(ContentProperty.TITLE) ||
            sort1.equalsIgnoreCase(ContentProperty.ALT_TITLE) ||
            sort1.equalsIgnoreCase(ContentProperty.LAST_MODIFIED) ||
            sort1.equalsIgnoreCase(ContentProperty.LAST_MAJOR_CHANGE) ||
            sort1.equalsIgnoreCase(ContentProperty.PUBLISH_DATE) ||
            sort1.equalsIgnoreCase(ContentProperty.EXPIRE_DATE) ||
            sort1.equalsIgnoreCase(ContentProperty.DEPTH) ||
            sort1.equalsIgnoreCase(ContentProperty.ALIAS) ||
            sort1.equalsIgnoreCase(ContentProperty.NUMBER_OF_VIEWS) ||
            sort1.equalsIgnoreCase(ContentProperty.NUMBER_OF_COMMENTS) ||
            sort1.equalsIgnoreCase(ContentProperty.RATING_SCORE) ||
            sort1.equalsIgnoreCase(ContentProperty.NUMBER_OF_RATINGS) ||
            sort1.equalsIgnoreCase(ContentProperty.PRIORITY)) {
            sort = " order by " + sort1;
            if (descending) {
                sort += " desc";
            }
        }
        return sort;
    }

    public String getSort1() {
        return sort1;
    }

    public String getSort2() {
        return sort2;
    }

    public boolean sortDescending() {
        return descending;
    }
}
