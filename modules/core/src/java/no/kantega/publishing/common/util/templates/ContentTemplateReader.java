/*
 * Copyright 2010 Kantega AS
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

import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.XMLHelper;
import no.kantega.commons.util.XPathHelper;
import no.kantega.publishing.admin.content.util.ResourceLoaderEntityResolver;
import no.kantega.publishing.common.data.ContentTemplate;
import no.kantega.publishing.common.data.TemplateConfigurationValidationError;
import org.apache.xpath.XPathAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContentTemplateReader {
    private static final Logger log = LoggerFactory.getLogger(ContentTemplateReader.class);
    private ResourceLoader contentTemplateResourceLoader;
    /**
     * Validates attributes in contenttemplate
     * @param contentTemplate - contenttemplate
     * @return
     */
    public List<TemplateConfigurationValidationError> updateContentTemplateFromTemplateFile(ContentTemplate contentTemplate) {
        List<TemplateConfigurationValidationError> errors = new ArrayList<>();

        contentTemplate.setAttributeElements(new ArrayList<Element>());
        contentTemplate.setPropertyElements(new ArrayList<Element>());

        // Check attributes in XML file
        try {
            Resource resource = contentTemplateResourceLoader.getResource(contentTemplate.getTemplateFile());
            if (resource == null) {
                errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.attribute.missingtemplatefile", contentTemplate.getTemplateFile()));
                return errors;
            }
            ResourceLoaderEntityResolver entityResolver = new ResourceLoaderEntityResolver(contentTemplateResourceLoader, resource.getFile().getParentFile());

            Document def = XMLHelper.openDocument(resource, entityResolver);

            NodeList attributes = XPathAPI.selectNodeList(def.getDocumentElement(), "attributes/attribute|attributes/repeater");
            if (attributes.getLength()  == 0) {
                attributes = XPathAPI.selectNodeList(def.getDocumentElement(), "attribute|repeater");
            }
            for (int i = 0; i < attributes.getLength(); i++) {
                Element attr = (Element)attributes.item(i);
                contentTemplate.getAttributeElements().add(attr);
            }

            if (contentTemplate.getAttributeElements().size() == 0) {
                errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.attribute.emptyfile", contentTemplate.getTemplateFile()));
            }

            NodeList properties = XPathAPI.selectNodeList(def.getDocumentElement(), "properties/property");
            for (int i = 0; i < properties.getLength(); i++) {
                Element prop = (Element)properties.item(i);
                contentTemplate.getPropertyElements().add(prop);
            }

            String helptext = XPathHelper.getString(def.getDocumentElement(), "helptext");
            contentTemplate.setHelptext(helptext);

        } catch (SystemException | InvalidFileException | IOException e) {
            errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.attribute.missingtemplatefile", contentTemplate.getTemplateFile()));
            log.error("Error loading: " + contentTemplate.getTemplateFile(), e);
        } catch (TransformerException e) {
            errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.attribute.transformerexception", contentTemplate.getTemplateFile()));
            log.error("Error transforming: " + contentTemplate.getTemplateFile(), e);
        }
        return errors;
    }

    public void setContentTemplateResourceLoader(ResourceLoader contentTemplateResourceLoader) {
        this.contentTemplateResourceLoader = contentTemplateResourceLoader;
    }
}
