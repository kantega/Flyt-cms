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

package no.kantega.publishing.topicmaps;

import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.topicmaps.data.Topic;

import java.text.Collator;
import java.util.Comparator;

public class TopicComparator implements Comparator<Topic> {
    Collator collator = null;

    public TopicComparator() {
        collator = Collator.getInstance(Aksess.getDefaultLocale());
        collator.setStrength(Collator.PRIMARY);
    }


    public int compare(Topic t1, Topic t2) {
        if (t1 != null && t2 != null &&
                t1.getBaseName() != null && t2.getBaseName() != null) {
            return collator.compare(t1.getBaseName(), t2.getBaseName());
        }
        return 0;
    }
}

