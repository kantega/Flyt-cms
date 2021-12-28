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

import no.kantega.publishing.api.content.ContentIdHelper;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.attribute.AttributeDataType;
import no.kantega.publishing.api.multimedia.MultimediaDao;
import no.kantega.publishing.api.multimedia.MultimediaUsageDao;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.MediaAttribute;
import no.kantega.publishing.common.data.attributes.TextAttribute;
import no.kantega.publishing.common.data.enums.ExpireAction;
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.publishing.common.util.MultimediaHelper;
import no.kantega.publishing.event.ContentEvent;
import no.kantega.publishing.event.ContentEventListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 *
 */
public class MultimediaUsageListener extends ContentEventListenerAdapter {
    private static final Logger log = LoggerFactory.getLogger(MultimediaUsageListener.class);
    private MultimediaUsageDao multimediaUsageDao;
    private MultimediaDao multimediaDao;
    @Autowired
    private ContentIdHelper contentIdHelper;

    public void contentSaved(ContentEvent event) {
        // Delete all usages for this content
        multimediaUsageDao.removeUsageForContentId(event.getContent().getId());

        // Add all contentattributes
        addAttributes(event.getContent().getId(), event.getContent().getAttributes(AttributeDataType.CONTENT_DATA));

        // Add all metadataattributes
        addAttributes(event.getContent().getId(), event.getContent().getAttributes(AttributeDataType.META_DATA));
    }

    public void contentDeleted(ContentEvent event) {
        multimediaUsageDao.removeUsageForContentId(event.getContent().getId());
    }

    public void contentExpired(ContentEvent event) {
        ExpireAction action = event.getContent().getExpireAction();

        if (action == ExpireAction.DELETE) {
            multimediaUsageDao.removeUsageForContentId(event.getContent().getId());
        }
    }

    public void contentPermanentlyDeleted(ContentIdentifier cid) {
        contentIdHelper.assureContentIdAndAssociationIdSet(cid);
        int contentId = cid.getContentId();
        multimediaUsageDao.removeUsageForContentId(contentId);

        List<Multimedia> multimedia = multimediaDao.getMultimediaWithContentId(contentId);
        for (Multimedia m : multimedia) {
            try {
                multimediaDao.deleteMultimedia(m.getId());
            } catch (ObjectInUseException e) {
                log.error("", e);
            }
        }
    }

    private void addAttributes(int contentId, List<Attribute> attributes) {
        for (Attribute attribute : attributes) {
            if (attribute instanceof MediaAttribute) {
                // Mediaattribute contains id of mediaattribute
                MediaAttribute mediaAttribute = (MediaAttribute) attribute;
                if (mediaAttribute.getValue() != null) {
                    try {
                        int multimediaId = Integer.parseInt(mediaAttribute.getValue());
                        multimediaUsageDao.addUsageForContentId(contentId, multimediaId);
                    } catch (NumberFormatException e) {
                        // Do nothing
                    }
                }
            } else if (attribute instanceof TextAttribute) {
                TextAttribute textAttribute = (TextAttribute) attribute;

                // Links to multimediaobjects have /multimedia/ or multimedia.ap in URL
                String value = textAttribute.getValue();
                if (value != null) {
                    List<Integer> ids = MultimediaHelper.getMultimediaIdsFromText(value);
                    for (Integer id : ids) {
                        multimediaUsageDao.addUsageForContentId(contentId, id);
                    }
                }
            }
        }
    }

    public void setMultimediaUsageDao(MultimediaUsageDao multimediaUsageDao) {
        this.multimediaUsageDao = multimediaUsageDao;
    }

    public void setMultimediaDao(MultimediaDao multimediaDao) {
        this.multimediaDao = multimediaDao;
    }
}
