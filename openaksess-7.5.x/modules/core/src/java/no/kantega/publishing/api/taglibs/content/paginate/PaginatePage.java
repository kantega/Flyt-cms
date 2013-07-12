/*
 * Copyright 2011 Kantega AS
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

package no.kantega.publishing.api.taglibs.content.paginate;


public class PaginatePage {
    private int pageNumber = -1;
    private boolean isCurrentPage = false;
    private boolean isGap = false;

    public static PaginatePage createPage(int pageNumber, boolean isCurrentPage) {
        PaginatePage page = new PaginatePage();
        page.pageNumber = pageNumber;
        page.isCurrentPage = isCurrentPage;
        return page;
    }

    public static PaginatePage createGap() {
        PaginatePage gap = new PaginatePage();
        gap.isGap = true;
        return gap;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public boolean isCurrentPage() {
        return isCurrentPage;
    }

    public boolean isGap() {
        return isGap;
    }
}
