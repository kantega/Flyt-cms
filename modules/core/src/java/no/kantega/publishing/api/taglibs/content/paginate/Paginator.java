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

import java.util.ArrayList;
import java.util.List;

public class Paginator {

    public List<PaginatePage> getPaginatedList(int numberOfPages, int currentOffset, int beforeAndAfterLinks){
        List<PaginatePage> pages = new ArrayList<PaginatePage>();
        if (numberOfPages <= 1) {
            return pages;
        }

        int currentPageNo = currentOffset + 1;

        int pagesBeforeCurrent = beforeAndAfterLinks;
        int pagesAfterCurrent = beforeAndAfterLinks;

        if (currentOffset < beforeAndAfterLinks + 1) {
            pagesBeforeCurrent = currentOffset - 1;
            if (pagesBeforeCurrent < 0) {
                pagesBeforeCurrent = 0;
            }
            pagesAfterCurrent = beforeAndAfterLinks * 2 - pagesBeforeCurrent;
        } else if (currentOffset + 2 > numberOfPages - beforeAndAfterLinks) {
            pagesAfterCurrent = numberOfPages - currentOffset - 2;
            if (pagesAfterCurrent < 0) {
                pagesAfterCurrent = 0;
            }
            pagesBeforeCurrent = beforeAndAfterLinks * 2 - pagesAfterCurrent;
        }

        boolean gapWasAdded = false;
        for (int pageNo = 1; pageNo <= numberOfPages; pageNo++) {
            if (pageNo == 1 || pageNo == numberOfPages) {
                PaginatePage page = PaginatePage.createPage(pageNo, currentOffset == (pageNo - 1));
                pages.add(page);
                gapWasAdded = false;
            } else if (pageNo >= currentPageNo - pagesBeforeCurrent && pageNo <= currentPageNo + pagesAfterCurrent ) {
                PaginatePage page = PaginatePage.createPage(pageNo, currentOffset == (pageNo - 1));
                pages.add(page);
                gapWasAdded = false;
            } else {
                if (!gapWasAdded) {
                    PaginatePage gap = PaginatePage.createGap();
                    pages.add(gap);
                    gapWasAdded = true;
                }
            }



            /*

            1. skal alltid printes

            2. de beforeAndAfterLinks f¿r current page skal printes

            3. de beforeAndAfterLinks etter current page skal printes

             */



        }

        return pages;
    }
}
