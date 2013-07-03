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

package no.kantega.publishing.jobs.cleanup;

import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.enums.ServerType;
import no.kantega.publishing.common.exception.ObjectInUseException;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.content.api.ContentAO;
import no.kantega.publishing.event.ContentListenerUtil;
import no.kantega.publishing.eventlog.Event;
import no.kantega.publishing.eventlog.EventLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DatabaseCleanupJob {
    private static final Logger log = LoggerFactory.getLogger(DatabaseCleanupJob.class);

    @Autowired
    private EventLog eventLog;

    @Autowired
    private ContentAO contentAO;

    @Scheduled(cron = "${dbcleanupjob.cron:0 0 4 * * ?}")
    public void cleanDatabase() {

        if (Aksess.getServerType() == ServerType.SLAVE) {
            log.info( "Job is disabled for server type slave");
            return;
        } else {
            log.info("Starting database cleanup job");
        }

        try (Connection c = dbConnectionFactory.getConnection()){
            deleteAttachmentsWithNoContent(c);
            deleteOrphantMultimedia(c);
            deleteOldTrafficLogEntries(c);
            deleteOldTransactionLocks(c);
            updateNumberOfViews(c);
            deleteOldEventLogEntries(c);
            deleteOldSearhlogEntries(c);
            deleteOldItems(c);
            removeDeletedAssociations(c);
            deletePagesWithNoAssociations(c);
            deleteTopicContentMappingsForDeletedContent(c);
            removeLinksFromLinkChecker(c);
            deleteOldXmlCacheEntries(c);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void deleteAttachmentsWithNoContent(Connection c) throws SQLException {
        Calendar cal = new GregorianCalendar();

        cal.add(Calendar.DATE, -1);
        PreparedStatement st = c.prepareStatement("delete from attachments where ContentId = -1 AND LastModified < ?");
        st.setTimestamp(1, new java.sql.Timestamp(cal.getTime().getTime()));
        st.execute();
    }

    private void deleteOrphantMultimedia(Connection c) throws SQLException {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DATE, -1);

        PreparedStatement st = c.prepareStatement("delete from multimedia where ContentId = -1 AND ParentId = -1 AND LastModified < ?");
        st.setTimestamp(1, new java.sql.Timestamp(cal.getTime().getTime()));
        st.execute();
    }

    private void deleteOldTrafficLogEntries(Connection c) throws SQLException {
        Calendar cal;
        cal = new GregorianCalendar();
        cal.add(Calendar.MONTH, -Aksess.getTrafficLogMaxAge());

        log.info( "Deleting trafficlog older than " + Aksess.getTrafficLogMaxAge() + " months");

        PreparedStatement st = c.prepareStatement("delete from trafficlog where Time < ?");
        st.setTimestamp(1, new java.sql.Timestamp(cal.getTime().getTime()));
        st.execute();
    }

    private void deleteOldTransactionLocks(Connection c) throws SQLException {
        Calendar cal;
        cal = new GregorianCalendar();
        cal.add(Calendar.DATE, -7);
        log.info( "Deleting transactionlocks older than 7 days");

        PreparedStatement st = c.prepareStatement("delete from transactionlocks where TransactionTime < ?");
        st.setTimestamp(1, new java.sql.Timestamp(cal.getTime().getTime()));
        st.execute();
    }

    private void updateNumberOfViews(Connection c) throws SQLException {
        log.info( "Updating number of views based on trafficlog");

        PreparedStatement st = c.prepareStatement("update associations set NumberOfViews = (select count(*) from trafficlog where trafficlog.ContentId = associations.ContentId and trafficlog.SiteId = associations.SiteId and trafficlog.IsSpider=0)");
        st.execute();
    }

    private void deleteOldEventLogEntries(Connection c) throws SQLException {
        Calendar cal;
        cal = new GregorianCalendar();
        cal.add(Calendar.MONTH, -Aksess.getEventLogMaxAge());

        log.info( "Deleting event log entries older than " + Aksess.getEventLogMaxAge() + " months");

        PreparedStatement st = c.prepareStatement("delete from eventlog where Time < ?");
        st.setTimestamp(1, new java.sql.Timestamp(cal.getTime().getTime()));
        st.execute();
    }

    private void deleteOldSearhlogEntries(Connection c) throws SQLException {
        Calendar cal;
        cal = new GregorianCalendar();
        cal.add(Calendar.MONTH, -1);

        log.info( "Deleting search log entries older than 1 month");

        PreparedStatement st = c.prepareStatement("delete from searchlog where Time < ?");
        st.setTimestamp(1, new java.sql.Timestamp(cal.getTime().getTime()));
        st.execute();
    }

    private void deleteOldItems(Connection c) throws SQLException {
        Calendar cal;
        cal = new GregorianCalendar();
        cal.add(Calendar.MONTH, -Aksess.getDeletedItemsMaxAge());

        log.info( "Deleting deleted items older than " + Aksess.getDeletedItemsMaxAge() + " months");

        PreparedStatement st = c.prepareStatement("delete from deleteditems where DeletedDate < ?");
        st.setTimestamp(1, new java.sql.Timestamp(cal.getTime().getTime()));
        st.execute();
    }

    private void removeDeletedAssociations(Connection c) throws SQLException {
        PreparedStatement st = c.prepareStatement("delete from associations where IsDeleted = 1 and DeletedItemsId not in (select Id from deleteditems)");
        st.execute();
    }

    private void deletePagesWithNoAssociations(Connection c) throws SQLException, ObjectInUseException {
        PreparedStatement st = c.prepareStatement("select ContentId from content where ContentId not in (select ContentId from associations)");
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            ContentIdentifier cid =  ContentIdentifier.fromContentId(rs.getInt("ContentId"));
            String title = SQLHelper.getString(c, "select title from contentversion where contentId = " + cid.getContentId() + " and IsActive = 1", "title");
            log.info( "Deleting page " + title + " because it has been in the trash can for over 1 month");
            eventLog.log("System", null, Event.DELETE_CONTENT_TRASH, title, null);

            ContentListenerUtil.getContentNotifier().contentPermanentlyDeleted(cid);
            contentAO.deleteContent(cid);
        }
    }

    private void deleteTopicContentMappingsForDeletedContent(Connection c) throws SQLException {
        PreparedStatement st = c.prepareStatement("delete from ct2topic where ContentId not in (select ContentId from content)");
        st.execute();
    }

    private void removeLinksFromLinkChecker(Connection c) throws SQLException {
        PreparedStatement st = c.prepareStatement("delete from link where Id not in (select distinct LinkId from linkoccurrence)");
        st.executeUpdate();
    }

    private void deleteOldXmlCacheEntries(Connection c) throws SQLException {
        Calendar cal;
        cal = new GregorianCalendar();
        cal.add(Calendar.MONTH, - Aksess.getXmlCacheMaxAge());

        log.info( "Deleting Xmlcache entries older than " + Aksess.getXmlCacheMaxAge() + " months");

        PreparedStatement st = c.prepareStatement("delete from xmlcache where LastUpdated < ?");
        st.setTimestamp(1, new java.sql.Timestamp(cal.getTime().getTime()));
        st.execute();

    }

}

