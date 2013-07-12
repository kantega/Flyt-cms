/*
 * Copyright 2010 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.api.forms.model;

public class DefaultFormValue implements FormValue {
    private String name;
    private String[] values;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValuesAsString() {
        StringBuffer sb = new StringBuffer();
        if (values != null) {
            for (String v : values) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(v);
            }
        }
        return sb.toString();
    }

    public void setValue(String value) {
        values = new String[]{value};
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }
}
