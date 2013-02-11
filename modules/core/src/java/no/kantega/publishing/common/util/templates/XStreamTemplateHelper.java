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

package no.kantega.publishing.common.util.templates;

import com.thoughtworks.xstream.XStream;
import no.kantega.publishing.common.data.*;

/**
 * User: Anders Skar, Kantega AS
 * Date: Feb 10, 2009
 * Time: 1:39:54 PM
 */
public class XStreamTemplateHelper {
    public static XStream getXStream() {
        XStream xstream = new XStream();
        xstream.registerConverter(new DisplayTemplateControllerValueConverter());

        // Alias classnames
        xstream.alias("associationCategory", AssociationCategory.class);
        xstream.alias("documentType", DocumentType.class);
        xstream.alias("contentTemplate", ContentTemplate.class);
        xstream.alias("controller", DisplayTemplateControllerId.class);
        xstream.alias("displayTemplate", DisplayTemplate.class);
        xstream.alias("site", Site.class);
        xstream.alias("templateConfiguration", TemplateConfiguration.class);


        // Ids should be attributes
        xstream.useAttributeFor(Site.class, "id");
        xstream.aliasField("databaseId", Site.class, "id");
        xstream.useAttributeFor(Site.class, "publicId");
        xstream.aliasField("id", Site.class, "publicId");
        xstream.useAttributeFor(Site.class, "displayTemplateId");

        xstream.useAttributeFor(AssociationCategory.class, "id");
        xstream.aliasField("databaseId", AssociationCategory.class, "id");
        xstream.useAttributeFor(AssociationCategory.class, "publicId");
        xstream.aliasField("id", AssociationCategory.class, "publicId");

        xstream.useAttributeFor(DocumentType.class, "id");
        xstream.aliasField("databaseId", DocumentType.class, "id");
        xstream.useAttributeFor(DocumentType.class, "publicId");
        xstream.aliasField("id", DocumentType.class, "publicId");

        xstream.useAttributeFor(ContentTemplate.class, "id");
        xstream.aliasField("databaseId", ContentTemplate.class, "id");
        xstream.useAttributeFor(ContentTemplate.class, "publicId");
        xstream.aliasField("id", ContentTemplate.class, "publicId");

        xstream.useAttributeFor(DisplayTemplate.class, "id");
        xstream.aliasField("databaseId", DisplayTemplate.class, "id");
        xstream.useAttributeFor(DisplayTemplate.class, "publicId");
        xstream.aliasField("id", DisplayTemplate.class, "publicId");

        // Site aliases
        xstream.useAttributeFor(Site.class, "alias");

        // ContentTemplate aliases
        xstream.useAttributeFor(ContentTemplate.class, "contentType");
        xstream.useAttributeFor(ContentTemplate.class, "keepVersions");
        xstream.useAttributeFor(ContentTemplate.class, "isHearingEnabled");
        xstream.useAttributeFor(ContentTemplate.class, "expireAction");
        xstream.useAttributeFor(ContentTemplate.class, "expireMonths");
        xstream.useAttributeFor(ContentTemplate.class, "isSearchable");
        xstream.useAttributeFor(ContentTemplate.class, "isDefaultSearchable");

        // DisplayTemplate aliases
        xstream.useAttributeFor(DisplayTemplate.class, "allowMultipleUsages");
        xstream.useAttributeFor(DisplayTemplate.class, "isNewGroup");

        // Internal XStream attribute
        xstream.aliasSystemAttribute("refid", "id");

        return xstream;
    }
}
