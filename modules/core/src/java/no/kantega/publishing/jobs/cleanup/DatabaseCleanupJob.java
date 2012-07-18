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

import no.kantega.commons.log.Log;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.enums.Event;
import no.kantega.publishing.common.data.enums.ServerType;
import no.kantega.publishing.common.service.impl.EventLog;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.event.ContentListenerUtil;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DatabaseCleanupJob  extends QuartzJobBean {
    private static final String SOURCE = "aksess.jobs.DatabaseCleanupJob";

    protected void executeInternal(org.quartz.JobExecutionContext jobExecutionContext) throws org.quartz.JobExecutionException {

        if (Aksess.getServerType() == ServerType.SLAVE) {
            Log.info(SOURCE, "Job is disabled for server type slave", null, null);
            return;
        }
        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();

            Calendar cal = null;
            cal = new GregorianCalendar();

            // Delete attachments with no connections to content
            cal.add(Calendar.DATE, -1);
            PreparedStatement st = c.prepareStatement("delete from attachments where ContentId = -1 AND LastModified < ?");
            st.setTimestamp(1, new java.sql.Timestamp(cal.getTime().getTime()));
            st.execute();

            // Delete multimedia with no connections to content and no parent id
            st = c.prepareStatement("delete from multimedia where ContentId = -1 AND ParentId = -1 AND LastModified < ?");
            st.setTimestamp(1, new java.sql.Timestamp(cal.getTime().getTime()));
            st.execute();


            // Delete traffic log
            cal = new GregorianCalendar();
            cal.add(Calendar.MONTH, -Aksess.getTrafficLogMaxAge());

            Log.info(SOURCE, "Deleting trafficlog older than " + Aksess.getTrafficLogMaxAge() + " months");

            st = c.prepareStatement("delete from trafficlog where Time < ?");
            st.setTimestamp(1, new java.sql.Timestamp(cal.getTime().getTime()));
            st.execute();

            cal = new GregorianCalendar();
            cal.add(Calendar.DATE, -7);
            Log.info(SOURCE, "Deleting transactionlocks older than 7 days");

            st = c.prepareStatement("delete from transactionlocks where TransactionTime < ?");
            st.setTimestamp(1, new java.sql.Timestamp(cal.getTime().getTime()));
            st.execute();


            // Update number of views based on trafficlog
            Log.info(SOURCE, "Updating number of views based on trafficlog");

            st = c.prepareStatement("update associations set NumberOfViews = (select count(*) from trafficlog where trafficlog.ContentId = associations.ContentId and trafficlog.SiteId = associations.SiteId) and trafficlog.IsSpider=0");
            st.execute();


            // Delete old entries from event log
            cal = new GregorianCalendar();
            cal.add(Calendar.MONTH, -Aksess.getEventLogMaxAge());

            Log.info(SOURCE, "Deleting event log entries older than " + Aksess.getEventLogMaxAge() + " months");

            st = c.prepareStatement("delete from eventlog where Time < ?");
            st.setTimestamp(1, new java.sql.Timestamp(cal.getTime().getTime()));
            st.execute();

            // Delete entries from searchlog older than 1 month
            cal = new GregorianCalendar();
            cal.add(Calendar.MONTH, -1);

            Log.info(SOURCE, "Deleting search log entries older than 1 month");

            st = c.prepareStatement("delete from searchlog where Time < ?");
            st.setTimestamp(1, new java.sql.Timestamp(cal.getTime().getTime()));
            st.execute();

            // Delete old items from trash
            cal = new GregorianCalendar();
            cal.add(Calendar.MONTH, -Aksess.getDeletedItemsMaxAge());

            Log.info(SOURCE, "Deleting deleted items older than " + Aksess.getDeletedItemsMaxAge() + " months");

            st = c.prepareStatement("delete from deleteditems where DeletedDate < ?");
            st.setTimestamp(1, new java.sql.Timestamp(cal.getTime().getTime()));
            st.execute();

            // Delete associations that have been removed from trash
            st = c.prepareStatement("delete from associations where IsDeleted = 1 and DeletedItemsId not in (select Id from deleteditems)");
            st.execute();

            // Delete pages with no associations
            st = c.prepareStatement("select ContentId from content where ContentId not in (select ContentId from associations)");
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                ContentIdentifier cid = new ContentIdentifier();
                cid.setContentId(rs.getInt("ContentId"));
                String title = SQLHelper.getString(c, "select title from contentversion where contentId = " + cid.getContentId() + " and IsActive = 1", "title");
                Log.info(SOURCE, "Deleting page " + title + " because it has been in the trash can for over 1 month");
                EventLog.log("System", null, Event.DELETE_CONTENT_TRASH, title, null);

                ContentListenerUtil.getContentNotifier().contentPermanentlyDeleted(cid);
                ContentAO.deleteContent(cid);
            }

            // Delete mappings between topics and content for deleted content
            st = c.prepareStatement("delete from ct2topic where ContentId not in (select ContentId from content)");
            st.execute();

            // Remove links from linkchecker
            st = c.prepareStatement("delete from link where Id not in (select distinct LinkId from linkoccurrence)");
            st.executeUpdate();

        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);
        } finally {
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    Log.error(SOURCE, e, null, null);
                }
            }
        }
    }
}

