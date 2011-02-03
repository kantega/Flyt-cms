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

package no.kantega.publishing.admin.topicmaps.action.util;

import no.kantega.commons.exception.RegExpSyntaxException;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.RegExp;

public class TopicMapHelper {
    private static String SOURCE = "TopicMapHelper";

    public static String createTopicIdFromName(String name) {
        String id = name.toLowerCase();
        id = id.replace('æ', 'e');
        id = id.replace('ø', 'o');
        id = id.replace('å', 'a');
        try {
            id = RegExp.replace("[^a-z0-9]", id, "");
        } catch (RegExpSyntaxException e) {
            Log.error(SOURCE, "createTopicIdFromName(): " + name);
        }
        return id;
    }
}
