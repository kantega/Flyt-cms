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

package no.kantega.publishing.jobs.contentstate;

import no.kantega.publishing.common.ao.LinkDao;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.event.ContentListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: Anders Skar, Kantega AS
 * Date: Dec 6, 2007
 * Time: 2:04:36 PM
 */
public class RemoveFromLinkCheckerListener extends ContentListenerAdapter {
    @Autowired
    private LinkDao linkDao;

    public void contentExpired(Content content) {
        linkDao.deleteLinksForContentId(content.getId());
    }

    public void contentSaved(Content content) {
        if (content.getId() != -1) {
            linkDao.deleteLinksForContentId(content.getId());
        }
    }
}
