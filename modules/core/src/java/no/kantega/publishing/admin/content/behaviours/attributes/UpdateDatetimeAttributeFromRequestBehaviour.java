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

package no.kantega.publishing.admin.content.behaviours.attributes;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.admin.content.util.AttributeHelper;
import no.kantega.publishing.admin.util.DateUtil;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.DatetimeAttribute;

/**
 * Author: Kristian Lier Selnæs, Kantega AS
 * Date: 24.jan.2008
 * Time: 13:07:32
 */
public class UpdateDatetimeAttributeFromRequestBehaviour implements UpdateAttributeFromRequestBehaviour {

    public void updateAttribute(RequestParameters param, Content content, Attribute attribute) {

        String inputField = AttributeHelper.getInputFieldName(attribute.getNameIncludingPath());

        DatetimeAttribute datetimeAttribute = (DatetimeAttribute) attribute;

        String date = param.getString("date_" + inputField);
        String time = param.getString("time_" + inputField);
        if (date == null || DateUtil.format(Aksess.getDefaultDateFormat(), Aksess.getDefaultAdminLocale()).equals(date)) {
            date = "";
        }
        if (time == null || DateUtil.format(Aksess.getDefaultTimeFormat(), Aksess.getDefaultAdminLocale()).equals(time)) {
            time = "";
        }

        date = date.trim();
        time = time.trim();

        String datetime;
        if (date.length() > 0 && time.length() > 0) {
            datetime = date + Aksess.getDefaultDatetimeSeparator() + time;
        } else {
            datetime = date + time;
        }

        datetimeAttribute.setValue(datetime);
    }
}
