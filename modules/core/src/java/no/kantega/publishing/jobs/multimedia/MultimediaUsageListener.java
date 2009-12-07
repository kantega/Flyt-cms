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

package no.kantega.publishing.jobs.multimedia;

import no.kantega.publishing.common.ao.MultimediaUsageAO;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.MediaAttribute;
import no.kantega.publishing.common.data.attributes.TextAttribute;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.data.enums.ExpireAction;
import no.kantega.publishing.common.util.MultimediaHelper;
import no.kantega.publishing.event.ContentListenerAdapter;

import java.util.List;

/**
 * User: Anders Skar, Kantega AS
 * Date: Oct 13, 2008
 * Time: 10:47:32 AM
 */
public class MultimediaUsageListener extends ContentListenerAdapter {
    public void contentSaved(Content content) {
        // Delete all usages for this content
        MultimediaUsageAO.removeUsageForContentId(content.getId());
        
        // Add all contentattributes
        addAttributes(content.getId(), content.getAttributes(AttributeDataType.CONTENT_DATA));

        // Add all metadataattributes
        addAttributes(content.getId(), content.getAttributes(AttributeDataType.META_DATA));

    }

    // TODO: Dette bør skje ved sletting
    public void contentExpired(Content content) {
        int action = content.getExpireAction();

        if(action == ExpireAction.DELETE) {
            MultimediaUsageAO.removeUsageForContentId(content.getId());
        }
    }


    private static void addAttributes(int contentId, List attributes) {
        for (int i = 0; i < attributes.size(); i++) {
            Attribute attribute = (Attribute)attributes.get(i);
            if (attribute instanceof MediaAttribute) {
                // Mediaattribute contains id of mediaattribute
                MediaAttribute mediaAttribute = (MediaAttribute)attribute;
                if (mediaAttribute.getValue() != null) {
                    try {
                        int multimediaId = Integer.parseInt(mediaAttribute.getValue());

                        MultimediaUsageAO.addUsageForContentId(contentId, multimediaId);
                    } catch (NumberFormatException e) {
                        // Do nothing
                    }
                }
            } else if (attribute instanceof TextAttribute) {
                TextAttribute textAttribute = (TextAttribute)attribute;

                // Links to multimediaobjects have /multimedia/ or multimedia.ap in URL
                String value = textAttribute.getValue();
                if (value != null) {
                    List<Integer> ids = MultimediaHelper.getMultimediaIdsFromText(value);
                    for (Integer id : ids) {
                        MultimediaUsageAO.addUsageForContentId(contentId, id);
                    }
                }
            }
        }
    }
}
