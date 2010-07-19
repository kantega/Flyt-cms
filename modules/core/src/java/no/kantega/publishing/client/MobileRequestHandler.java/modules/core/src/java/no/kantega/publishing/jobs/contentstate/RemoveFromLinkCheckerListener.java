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

import no.kantega.publishing.event.ContentEvent;
import no.kantega.publishing.event.ContentEventListenerAdapter;
import no.kantega.publishing.common.ao.LinkDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: Anders Skar, Kantega AS
 * Date: Dec 6, 2007
 * Time: 2:04:36 PM
 */
public class RemoveFromLinkCheckerListener extends ContentEventListenerAdapter {
    @Autowired
    private LinkDao linkDao;

    public void contentExpired(ContentEvent event) {
        linkDao.deleteLinksForContentId(event.getContent().getId());
    }

    public void contentSaved(ContentEvent event) {
        if (!event.getContent().isNew()) {
            linkDao.deleteLinksForContentId(event.getContent().getId());
        }
    }
}
