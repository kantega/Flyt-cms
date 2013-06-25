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

package no.kantega.publishing.event.impl;

import no.kantega.publishing.event.ContentEvent;
import no.kantega.publishing.event.ContentEventListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class LoggingContentListener extends ContentEventListenerAdapter {
    private static final Logger log = LoggerFactory.getLogger(LoggingContentListener.class);

    public void beforeContentSave(ContentEvent event) {
        log.info("beforeContentSave(" +event.getContent().getTitle() +")");
    }

    public void contentSaved(ContentEvent event) {
        log.info("contentSaved(" +event.getContent().getTitle() +")");
    }
}
