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

package no.kantega.publishing.common;

import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Content;

import java.util.Comparator;

public class AssociationIdListComparator  implements Comparator<Content> {
    ContentIdentifier[] cids = null;

    public AssociationIdListComparator(ContentIdentifier[] cids) {
        this.cids = cids;
    }

    public int compare(Content c1, Content c2) {
        if (cids != null) {
            int pos1 = -1;
            int pos2 = -1;

            for (int i = 0; i < cids.length; i++) {
                ContentIdentifier cid = cids[i];
                if (cid.getAssociationId() == c1.getAssociation().getId()) {
                    pos1 = i;
                }
                if (cid.getAssociationId() == c2.getAssociation().getId()) {
                    pos2 = i;
                }
                if (pos1 != -1 && pos2 != -1) break;
            }
            if (pos1 != -1 && pos2 != -1) {
                if (pos2 > pos1) {
                    return -1;
                } else if (pos2 < pos1) {
                    return 1;
                }
            }
        }
        return 0;
    }
}
