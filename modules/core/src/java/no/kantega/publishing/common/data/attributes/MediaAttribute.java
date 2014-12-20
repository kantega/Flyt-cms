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

package no.kantega.publishing.common.data.attributes;

import no.kantega.commons.util.FormatHelper;
import no.kantega.publishing.admin.content.behaviours.attributes.PersistAttributeBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.PersistMediaAttributeBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateAttributeFromRequestBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateMediaAttributeFromRequestBehaviour;
import no.kantega.publishing.common.ao.MultimediaAO;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.AttributeProperty;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.common.util.MultimediaTagCreator;
import no.kantega.publishing.spring.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Element;

import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Attribute referencing a media object.
 */
public class MediaAttribute extends Attribute {
    private static final Logger log = LoggerFactory.getLogger(MediaAttribute.class);
    private MultipartFile importFile = null;

    protected String defaultMediaFolder = null;
    protected boolean useMediaArchive = true;
    private Multimedia cachedMultimediaObj = null; // Cacher bildet

    protected String filter = null;

    private static MultimediaAO multimediaAO;

    @Override
    public void setConfig(Element config, Map<String, String> model) throws InvalidTemplateException {
        super.setConfig(config, model);
        if (config != null) {
            this.defaultMediaFolder = config.getAttribute("mediafolder");
            String useMediaArchive = config.getAttribute("usemediaarchive");
            if ("false".equals(useMediaArchive)) {
                this.useMediaArchive = false;
            }
        }
    }

    public String getProperty(String prop) {
        String returnValue = value;

        String property = prop.toLowerCase();
        if (isKnownProperty(property)) {
            try {
                Multimedia mm = getMultimedia();
                if(mm == null){
                    returnValue = "";
                } else{
                    switch (property) {
                        case AttributeProperty.HTML:
                            returnValue = MultimediaTagCreator.mm2HtmlTag(mm, null, -1, -1, null);
                            break;
                        case AttributeProperty.WIDTH:
                            returnValue = String.valueOf(mm.getWidth());
                            break;
                        case AttributeProperty.HEIGHT:
                            returnValue = String.valueOf(mm.getHeight());
                            break;
                        case AttributeProperty.AUTHOR:
                            returnValue = mm.getAuthor();
                            break;
                        case AttributeProperty.NAME:
                            returnValue = mm.getName();
                            break;
                        case AttributeProperty.ALTNAME:
                            returnValue = mm.getAltname();
                            break;
                        case AttributeProperty.DESCRIPTION:
                            returnValue = mm.getDescription();
                            break;
                        case AttributeProperty.PARENTID:
                            returnValue = String.valueOf(mm.getParentId());
                            break;
                        case AttributeProperty.LATITUDE:
                            returnValue = Double.toString(mm.getGpsLatitudeAsDouble());
                            break;
                        case AttributeProperty.LONGITUDE:
                            returnValue = Double.toString(mm.getGpsLongitudeAsDouble());
                            break;
                        case AttributeProperty.MIMETYPE:
                            returnValue = mm.getMimeType().getType();
                            break;
                        case AttributeProperty.URL:
                            returnValue = mm.getUrl();
                            break;
                        case AttributeProperty.SIZE:
                            int size = mm.getSize();
                            if (size > 0) {
                                returnValue = FormatHelper.formatSize(size);
                            } else {
                                returnValue = "";
                            }
                    }
                }
            } catch (Exception e) {
                log.error("Error getting attributevalue", e);
            }
        } else {
            returnValue = value;
        }
        return returnValue;
    }

    private boolean isKnownProperty(String property) {
        return AttributeProperty.HTML.equals(property)
                || AttributeProperty.URL.equals(property)
                || AttributeProperty.WIDTH.equals(property)
                || AttributeProperty.HEIGHT.equals(property)
                || AttributeProperty.NAME.equals(property)
                || AttributeProperty.ALTNAME.equals(property)
                || AttributeProperty.AUTHOR.equals(property)
                || AttributeProperty.DESCRIPTION.equals(property)
                || AttributeProperty.LATITUDE.equals(property)
                || AttributeProperty.LONGITUDE.equals(property)
                || AttributeProperty.PARENTID.equals(property)
                || AttributeProperty.MIMETYPE.equals(property)
                || AttributeProperty.SIZE.equals(property);
    }

    public MultipartFile getImportFile() {
        return importFile;
    }

    public void setImportFile(MultipartFile importFile) {
        this.importFile = importFile;
    }

    public Multimedia getMultimedia() {
        if (multimediaAO == null) {
            multimediaAO = RootContext.getInstance().getBean(MultimediaAO.class);
        }
        int id;
        if (cachedMultimediaObj == null && isNotBlank(value)) {
            try {
                id = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                log.error("Error parsing " + value, e);
                return null;
            }
            cachedMultimediaObj = multimediaAO.getMultimedia(id);
        }
        return cachedMultimediaObj;
    }

    public String getFilter() {
        return filter;
    }

    public PersistAttributeBehaviour getSaveBehaviour() {
        return new PersistMediaAttributeBehaviour();
    }

    public UpdateAttributeFromRequestBehaviour getUpdateFromRequestBehaviour() {
        return new UpdateMediaAttributeFromRequestBehaviour();
    }

    public String getRenderer() {
        return "media";
    }

    public String getDefaultMediaFolder() {
        return defaultMediaFolder;
    }

    public boolean getUseMediaArchive() {
        return useMediaArchive;
    }

}
