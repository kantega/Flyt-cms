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

package no.kantega.publishing.jobs.contentimport;

import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.api.runtime.ServerType;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.common.exception.ObjectLockedException;
import no.kantega.publishing.common.exception.TransactionLockException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.event.ContentImporter;
import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collections;
import java.util.List;

/**
 * Job for running alle ContentImporters
 */
public class ContentImportJob {
    private static final Logger log = LoggerFactory.getLogger(ContentImportJob.class);
    private List<ContentImporter> contentImporters;


    @Scheduled(cron = "${jobs.contentimport.trigger}")
    public void execute() {
        if (Aksess.getServerType() == ServerType.SLAVE) {
            log.info( "Job is disabled for server type slave");
            return;
        }
        try {
            SecuritySession session = SecuritySession.createNewAdminInstance();
            ContentManagementService cms = new ContentManagementService(session);

            for (ContentImporter ci : contentImporters) {
                importContentFromImporter(cms, ci);
            }
        } catch (SystemException | NotAuthorizedException | InvalidTemplateException | InvalidFileException e) {
            log.error("Error calling importer", e);
        }
    }

    private void importContentFromImporter(ContentManagementService cms, ContentImporter ci) throws NotAuthorizedException, InvalidFileException, InvalidTemplateException {
        List<Content> contentList = ci.getContentList();
        if (contentList != null) {
            log.debug( "Starter import av " + contentList.size() + " elementer");
            for (Content c : contentList) {
                ContentIdentifier cid = c.getContentIdentifier();
                cid.setVersion(-1);
                try {
                    c = cms.checkOutContent(cid);
                    // Only pages with status = PUBLISHED will be updated
                    if (c != null && c.getStatus() == ContentStatus.PUBLISHED) {
                        ci.updateContent(c);
                        cms.checkInContent(c, ContentStatus.PUBLISHED);
                    }
                } catch (ObjectLockedException e) {
                    log.error("Could not update:" + c.getTitle() + " was locked by someone else");
                } catch (TransactionLockException e) {
                    log.error("Could not update:" + c.getTitle() + " was locked by another process/server");
                }
            }
        }
    }

    @Autowired(required = false)
    public void setContentImporters(List<ContentImporter> contentImporters){
        if(contentImporters != null){
            this.contentImporters = contentImporters;
        } else {
            this.contentImporters = Collections.emptyList();
        }
    }
}
