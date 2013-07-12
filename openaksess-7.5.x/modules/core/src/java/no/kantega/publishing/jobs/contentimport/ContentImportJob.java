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
import no.kantega.commons.log.Log;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.ServerType;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.common.exception.ObjectLockedException;
import no.kantega.publishing.common.exception.TransactionLockException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.event.ContentImporter;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.spring.RootContext;
import org.springframework.context.ApplicationContext;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Author: Kristian Lier Selnæs, Kantega AS
 * Date: May 23, 2007
 * Time: 11:12:15 AM
 */
public class ContentImportJob {

    private static final String SOURCE = "aksess.ContentImportJob";


    public void execute() {
        if (Aksess.getServerType() == ServerType.SLAVE) {
            Log.info(SOURCE, "Job is disabled for server type slave", null, null);
            return;
        }
        try {
            SecuritySession session = SecuritySession.createNewAdminInstance();
            ContentManagementService cms = new ContentManagementService(session);

            ApplicationContext context = RootContext.getInstance();
            Map importers = context.getBeansOfType(ContentImporter.class);

            if (importers != null) {
                Iterator iter = importers.values().iterator();
                while (iter.hasNext()) {
                    ContentImporter ci = (ContentImporter) iter.next();
                    if (ci != null) {
                        importContentFromImporter(cms, ci);
                    }
                }
            }
        } catch (SystemException e) {
            Log.error(SOURCE, e, null, null);
        } catch (InvalidFileException e) {
            Log.error(SOURCE, e, null, null);
        } catch (InvalidTemplateException e) {
            Log.error(SOURCE, e, null, null);
        } catch (NotAuthorizedException e) {
            Log.error(SOURCE, e, null, null);
        }
    }

    private void importContentFromImporter(ContentManagementService cms, ContentImporter ci) throws NotAuthorizedException, InvalidFileException, InvalidTemplateException {
        List contentList = ci.getContentList();
        if (contentList != null) {
            Log.debug(SOURCE, "Starter import av " + contentList.size() + " elementer", null, null);
            for (int i = 0; i < contentList.size(); i++) {
                Content c = (Content) contentList.get(i);
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
                    Log.error(SOURCE, "Could not update:" + c.getTitle() + " was locked by someone else", null, null);
                } catch (TransactionLockException e) {
                    Log.error(SOURCE, "Could not update:" + c.getTitle() + " was locked by another process/server", null, null);
                }
            }
        }
    }
}
