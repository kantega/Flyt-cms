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
import no.kantega.publishing.common.ao.MultimediaUsageAO;
import no.kantega.publishing.common.ao.LinkDao;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.enums.Event;
import no.kantega.publishing.common.service.impl.EventLog;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DatabaseCleanupJob  extends QuartzJobBean {
    private static final String SOURCE = "aksess.jobs.DatabaseCleanupJob";

    @Autowired
    private LinkDao linkDao;

    protected void executeInternal(org.quartz.JobExecutionContext jobExecutionContext) throws org.quartz.JobExecutionException {
        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();

            Calendar cal = null;
            cal = new GregorianCalendar();

            // Slette vedlegg som har blitt liggende igjen
            cal.add(Calendar.DATE, -1);
            PreparedStatement st = c.prepareStatement("delete from attachments where ContentId = -1 AND LastModified < ?");
            st.setTimestamp(1, new java.sql.Timestamp(cal.getTime().getTime()));
            st.execute();

            // Slett trafikk log
            cal = new GregorianCalendar();
            cal.add(Calendar.MONTH, -Aksess.getTrafficLogMaxAge());

            Log.info(SOURCE, "Sletter trafikklog eldre enn " + Aksess.getTrafficLogMaxAge() + " mnd", null, null);

            st = c.prepareStatement("delete from trafficlog where Time < ?");
            st.setTimestamp(1, new java.sql.Timestamp(cal.getTime().getTime()));
            st.execute();

            // Oppdater antall visninger i loggen
            Log.info(SOURCE, "Oppdaterer antall visninger basert på trafikklog", null, null);

            st = c.prepareStatement("update associations set NumberOfViews = (select count(*) from trafficlog where trafficlog.ContentId = associations.ContentId and trafficlog.SiteId = associations.SiteId)");
            st.execute();


            // Slett event log
            cal = new GregorianCalendar();
            cal.add(Calendar.MONTH, -Aksess.getEventLogMaxAge());

            Log.info(SOURCE, "Sletter eventlog eldre enn " + Aksess.getEventLogMaxAge() + " mnd", null, null);

            st = c.prepareStatement("delete from eventlog where Time < ?");
            st.setTimestamp(1, new java.sql.Timestamp(cal.getTime().getTime()));
            st.execute();

            // Slett ting fra trash som er eldre enn N mnd
            cal = new GregorianCalendar();
            cal.add(Calendar.MONTH, -Aksess.getDeletedItemsMaxAge());

            Log.info(SOURCE, "Sletter deleteditems eldre enn " + Aksess.getDeletedItemsMaxAge() + " mnd", null, null);

            st = c.prepareStatement("delete from deleteditems where DeletedDate < ?");
            st.setTimestamp(1, new java.sql.Timestamp(cal.getTime().getTime()));
            st.execute();

            // Slett knytninger som ikke finnes i trash lenger
            c.prepareStatement("delete from associations where IsDeleted = 1 and DeletedItemsId not in (select Id from deleteditems)");
            st.execute();

            // Slett sider som ikke har knytninger lenger
            st = c.prepareStatement("select ContentId from content where ContentId not in (select ContentId from associations)");
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                ContentIdentifier cid = new ContentIdentifier();
                cid.setContentId(rs.getInt("ContentId"));
                String title = SQLHelper.getString(c, "select title from contentversion where contentId = " + cid.getContentId() + " and IsActive = 1", "title");
                Log.info(SOURCE, "Sletter side " + title + " fordi den har ligget i papirkurv i 1 måned", null, null);
                EventLog.log("System", null, Event.DELETE_CONTENT_TRASH, title, null);
                linkDao.deleteLinksForContentId(cid.getContentId());
                MultimediaUsageAO.removeUsageForContentId(cid.getContentId());

                ContentAO.deleteContent(cid);
            }

            // Slett lenker i lenkesjekkeren
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

    public static void main(String[] args) {
        Calendar cal = null;
        cal = new GregorianCalendar();
        System.out.println("Antall mnd:" + Aksess.getEventLogMaxAge());
        cal.add(Calendar.MONTH, -Aksess.getEventLogMaxAge());
        java.sql.Timestamp t = new java.sql.Timestamp(cal.getTime().getTime());

        System.out.println(t);
    }

}

