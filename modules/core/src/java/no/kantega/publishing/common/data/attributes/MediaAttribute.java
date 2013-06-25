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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Element;

import java.util.Map;

import static org.apache.commons.lang.StringUtils.isNotBlank;

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

    public String getProperty(String property) {
        String returnValue = value;
        if (value == null || value.length() == 0) {
            return "";
        }

        if (AttributeProperty.HTML.equalsIgnoreCase(property)
                || AttributeProperty.URL.equalsIgnoreCase(property)
                || AttributeProperty.WIDTH.equalsIgnoreCase(property)
                || AttributeProperty.HEIGHT.equalsIgnoreCase(property)
                || AttributeProperty.NAME.equalsIgnoreCase(property)
                || AttributeProperty.ALTNAME.equalsIgnoreCase(property)
                || AttributeProperty.AUTHOR.equalsIgnoreCase(property)
                || AttributeProperty.DESCRIPTION.equalsIgnoreCase(property)
                || AttributeProperty.LATITUDE.equalsIgnoreCase(property)
                || AttributeProperty.LONGITUDE.equalsIgnoreCase(property)
                || AttributeProperty.PARENTID.equalsIgnoreCase(property)
                || AttributeProperty.MIMETYPE.equalsIgnoreCase(property)
                || AttributeProperty.SIZE.equalsIgnoreCase(property)) {
            try {
                Multimedia mm = getMultimedia();
                if(mm == null){
                    return "";
                } else if (AttributeProperty.HTML.equalsIgnoreCase(property)) {
                    return MultimediaTagCreator.mm2HtmlTag(mm, null, -1, -1, null);
                } else if (AttributeProperty.WIDTH.equalsIgnoreCase(property)) {
                    return String.valueOf(mm.getWidth());
                } else if (AttributeProperty.HEIGHT.equalsIgnoreCase(property)) {
                    return String.valueOf(mm.getHeight());
                } else if (AttributeProperty.AUTHOR.equalsIgnoreCase(property)) {
                    return mm.getAuthor();
                } else if (AttributeProperty.NAME.equalsIgnoreCase(property)) {
                    return mm.getName();
                } else if (AttributeProperty.ALTNAME.equalsIgnoreCase(property)) {
                    return mm.getAltname();
                } else if (AttributeProperty.DESCRIPTION.equalsIgnoreCase(property)) {
                    return mm.getDescription();
                } else if (AttributeProperty.PARENTID.equalsIgnoreCase(property)) {
                    return String.valueOf(mm.getParentId());
                } else if (AttributeProperty.LATITUDE.equalsIgnoreCase(property)) {
                    return Double.toString(mm.getGpsLatitudeAsDouble());
                } else if (AttributeProperty.LONGITUDE.equalsIgnoreCase(property)) {
                    return Double.toString(mm.getGpsLongitudeAsDouble());
                } else if (AttributeProperty.MIMETYPE.equalsIgnoreCase(property)) {
                    return mm.getMimeType().getType();
                } else if (AttributeProperty.URL.equalsIgnoreCase(property)){
                    return mm.getUrl();
                } else if (AttributeProperty.SIZE.equalsIgnoreCase(property)) {
                    int size = mm.getSize();
                    if (size > 0) {
                        return FormatHelper.formatSize(size);
                    } else {
                        return "";
                    }
                }
            } catch (Exception e) {
                log.error("Error getting attributevalue", e);
            }
        }
        return returnValue;
    }

    public MultipartFile getImportFile() {
        return importFile;
    }

    public void setImportFile(MultipartFile importFile) {
        this.importFile = importFile;
    }

    public Multimedia getMultimedia() {
        int id;
        if (cachedMultimediaObj == null && isNotBlank(value)) {
            try {
                id = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                log.error("Error parsing " + value, e);
                return null;
            }
            cachedMultimediaObj = MultimediaAO.getMultimedia(id);
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
