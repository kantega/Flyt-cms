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

package no.kantega.publishing.jobs.contentstate;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.ExpireAction;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.event.ContentEvent;
import no.kantega.publishing.event.ContentEventListenerAdapter;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.util.SecurityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DeleteIfExpiredListener extends ContentEventListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(DeleteIfExpiredListener.class);

    public void contentExpired(ContentEvent event) {
        Content content = event.getContent();
        ExpireAction action = content.getExpireAction();

        if(action == ExpireAction.DELETE) {
            try {
                log.info("Deleting content with id=" + content.getId() +"('" +content.getTitle() +"') because it has expired");

                String lastModifiedBy = content.getModifiedBy();
                ContentManagementService cms = new ContentManagementService(SecuritySession.createNewUserInstance(SecurityHelper.createApiIdentity(lastModifiedBy)));

                List<Association> associations = content.getAssociations();
                int tmpAssociations[] = new int[associations.size()];
                for (int i = 0; i < tmpAssociations.length; i++) {
                    tmpAssociations[i] = associations.get(i).getAssociationId();
                }
                boolean deleteMultiple = Aksess.getConfiguration().getBoolean("expired.content.delete.multiple", false);
                cms.deleteAssociationsById(tmpAssociations, deleteMultiple);
            } catch (SystemException e) {
                log.error("Could not delete content", e);
            }
        }
    }

}
