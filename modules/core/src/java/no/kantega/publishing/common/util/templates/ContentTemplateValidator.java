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

import com.sun.org.apache.xpath.internal.XPathAPI;
import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.util.XMLHelper;
import no.kantega.publishing.admin.content.util.ResourceLoaderEntityResolver;
import no.kantega.publishing.common.data.ContentTemplate;
import no.kantega.publishing.common.data.TemplateConfigurationValidationError;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.factory.AttributeFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jan 14, 2009
 * Time: 4:34:56 PM
 */
public class ContentTemplateValidator {
    private ResourceLoader contentTemplateResourceLoader;
    private AttributeFactory attributeFactory;

    /**
     * Validates attributes in contenttemplate
     * @param contentTemplate - contenttemplate
     * @param contentTemplateType - type of contenttemplate
     * @return
     */
    public List<TemplateConfigurationValidationError> validate(ContentTemplate contentTemplate, int contentTemplateType) {
        List<TemplateConfigurationValidationError> errors = new ArrayList<>();

        // Check attributes in XML file
        try {
            Resource resource = contentTemplateResourceLoader.getResource(contentTemplate.getTemplateFile());
            if (resource == null) {                
                errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.attribute.missingtemplatefile", contentTemplate.getTemplateFile()));
                return errors;
            }
            ResourceLoaderEntityResolver entityResolver = new ResourceLoaderEntityResolver(contentTemplateResourceLoader);

            Document def = XMLHelper.openDocument(resource, entityResolver);

            boolean foundUrl = false;
            boolean foundForm = false;

            try {
                NodeList attributes = XPathAPI.selectNodeList(def.getDocumentElement(), "attributes/attribute");
                if (attributes.getLength()  == 0) {
                    attributes = XPathAPI.selectNodeList(def.getDocumentElement(), "attribute");
                }

                for (int i = 0; i < attributes.getLength(); i++) {
                    Element attr = (Element)attributes.item(i);
                    String name = attr.getAttribute("name");
                    if (name == null) {
                        errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.attribute.missingname", null));
                    }

                    if (name.contains("[") || name.contains("]")) {
                        errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.attribute.invalidname", null));
                    }

                    String field = attr.getAttribute("field");
                    String mapto = attr.getAttribute("mapto");
                    if ("url".equalsIgnoreCase(field) || "url".equalsIgnoreCase(mapto)) {
                        foundUrl = true;
                    }
                    String type = attr.getAttribute("type");
                    if (type != null) {
                        if (type.equalsIgnoreCase("form")) {
                            foundForm = true;
                        }

                        Attribute attribute = null;
                        try {
                            attribute = attributeFactory.newAttribute(type);
                        } catch (ClassNotFoundException e) {
                            errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.attribute.classnotfound", type + "(" + name + ")"));
                        } catch (Exception e) {
                            errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.attribute.exception",  type + "(" + name + ")"));
                        }

                    } else {
                        errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.attribute.missingtype", name));
                    }
                }
                if (attributes.getLength() == 0) {
                    errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.attribute.noattributes", null));
                }
            } catch (TransformerException e) {
                errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.attribute.xmlerror", null));
            }

            if (contentTemplateType == AttributeDataType.CONTENT_DATA) {
                if ((contentTemplate.getContentType() == ContentType.FILE || contentTemplate.getContentType() == ContentType.LINK) && !foundUrl) {
                    errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.attribute.missingurlattribute", null));
                }
                if (contentTemplate.getContentType() == ContentType.FORM && !foundForm) {
                    errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.attribute.missingformattribute", null));
                }
            }
        } catch (InvalidFileException e) {
            errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.attribute.missingtemplatefile", contentTemplate.getTemplateFile()));
        }
        return errors;
    }

    public void setContentTemplateResourceLoader(ResourceLoader contentTemplateResourceLoader) {
        this.contentTemplateResourceLoader = contentTemplateResourceLoader;
    }

    public void setAttributeFactory(AttributeFactory attributeFactory) {
        this.attributeFactory = attributeFactory;
    }
}
