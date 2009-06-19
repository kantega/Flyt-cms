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

import no.kantega.publishing.event.ContentListenerAdapter;
import no.kantega.publishing.common.data.Content;
import org.apache.log4j.Logger;

/**
 *
 */
public class LoggingContentListener extends ContentListenerAdapter {
    private Logger log = Logger.getLogger(getClass());

    public void beforeContentSave(Content content) {
        log.info("beforeContentSave(" +content.getTitle() +")");
    }

    public void contentSaved(Content content) {
        log.info("contentSaved(" +content.getTitle() +")");
    }
}
