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

import no.kantega.publishing.spring.RootContext;
import no.kantega.publishing.forum.ForumProvider;

import java.util.List;
import java.util.Map;

/**
 * User: Anders Skar, Kantega AS
 * Date: Feb 19, 2007
 * Time: 3:44:20 PM
 */
public class ForumlistAttribute extends Attribute {
    public String getRenderer() {
        return "forumlist";
    }

    public String getForumListAsString() {
        String list = "";
        Map forumProviders = RootContext.getInstance().getBeansOfType(ForumProvider.class);

        if(forumProviders.size() > 0) {
            ForumProvider forumProvider = (ForumProvider) forumProviders.values().iterator().next();
            long forumId = -1;
            if (value != null && value.length() > 0) {
                forumId = Long.parseLong(value, 10);
            }
            list = forumProvider.getForumsAsOptionList(forumId);
        } else {
            list = "<option></option>";
        }

        return list;
    }
}
