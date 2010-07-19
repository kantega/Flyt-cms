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

package no.kantega.publishing.common.data;

/**
 * User: Anders Skar, Kantega AS
 * Date: Feb 19, 2007
 * Time: 3:31:46 PM
 */
public class ListOption {
    private String value = "";
    private String text = "";
    private boolean isDefaultSelected;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isDefaultSelected() {
        return isDefaultSelected;
    }

    public void setDefaultSelected(boolean isDefaultSelected) {
        this.isDefaultSelected = isDefaultSelected;
    }
}
