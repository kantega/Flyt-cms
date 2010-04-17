/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.model;

import no.kantega.publishing.common.data.BaseObject;

import java.util.List;

/**
 * Contains items copied or clipped by the user.
 *
 * There is typically instance of Clipboard on the user's session for each content type, e.g. content and media.
 */
public class Clipboard {

    private List<BaseObject> items;
    private ClipboardStatus status;

    public List<BaseObject> getItems() {
        return items;
    }

    public void setItems(List<BaseObject> items) {
        this.items = items;
    }

    public ClipboardStatus getStatus() {
        return status;
    }

    public void setStatus(ClipboardStatus status) {
        this.status = status;
    }

    public boolean isEmpty() {
        return (items == null || items.size() == 0);
    }

    public void empty() {
        items = null;
        status = null;
    }
}
