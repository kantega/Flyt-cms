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

package no.kantega.publishing.modules.mailsubscription.api;

import no.kantega.commons.exception.InvalidParameterException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.data.enums.Language;
import no.kantega.publishing.common.util.database.SQLHelper;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.modules.mailsubscription.data.MailSubscription;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MailSubscriptionService {
    private static final String SOURCE = "aksess.MailSubscriptionService";

    public static void addMailSubscription(String email, int channel) throws SystemException {
        addMailSubscription(email, channel, -1, MailSubscription.DAILY, Language.NORWEGIAN_BO);
    }


    public static void addMailSubscription(String email, int channel, int documenttype) throws SystemException {
        addMailSubscription(email, channel, documenttype, MailSubscription.DAILY, Language.NORWEGIAN_BO);
    }

    public static void addMailSubscription(String email, int channel, int documenttype, int language) throws SystemException {
        addMailSubscription(email, channel, documenttype, MailSubscription.DAILY, language);
    }

    public static void addMailSubscription(String email, int channel, int documenttype, String interval, int language) throws SystemException {
        MailSubscription subscription = new MailSubscription();
        subscription.setEmail(email);
        subscription.setChannel(channel);
        subscription.setDocumenttype(documenttype);
        subscription.setLanguage(language);
        if (MailSubscription.DAILY.equals(interval)) {
            subscription.setInterval(MailSubscription.DAILY);
        } else if (MailSubscription.WEEKLY.equals(interval)) {
            subscription.setInterval(MailSubscription.WEEKLY);
        } else {
            subscription.setInterval(MailSubscription.IMMEDIATE);
        }
        addMailSubscription(subscription);
    }

    public static void addMailSubscription(MailSubscription subscription) throws SystemException {

        if (subscription.getEmail() == null || (subscription.getChannel() == -1 && subscription.getDocumenttype() == -1)) {
            throw new InvalidParameterException("email/channel", SOURCE);
        }

        String email = subscription.getEmail().toLowerCase();

        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
            // Slett abonnement dersom brukeren abonnerer allerede
            PreparedStatement st = c.prepareStatement("delete from mailsubscription where Channel = ? and DocumentType = ? and Language = ? and Email = ?");
            st.setInt(1, subscription.getChannel());
            st.setInt(2, subscription.getDocumenttype());
            st.setInt(3, subscription.getLanguage());
            st.setString(4, email);
            st.execute();

            st = c.prepareStatement("insert into mailsubscription (Channel, DocumentType, Language, Email, MailInterval) values (?, ?, ?, ?, ?)");
            st.setInt(1, subscription.getChannel());
            st.setInt(2, subscription.getDocumenttype());
            st.setInt(3, subscription.getLanguage());
            st.setString(4, email);
            st.setString(5, subscription.getInterval());
            st.execute();
        } catch (SQLException e) {
            throw new SystemException("Databasefeil", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }
    }

    /**
     * Retrieves all mail subscriptions for a given subscriber (email)
     * @param email subscriber email.
     * @return a list of all the mail subscriptions associated with the given email address.
     */
    public static List<MailSubscription> getMailSubscriptions(final String email) {
        return new NamedParameterJdbcTemplate(dbConnectionFactory.getDataSource()).query(
                "SELECT * FROM mailsubscription WHERE Email=:email",
                new HashMap<String, Object>(){{put("email", email);}},
                new RowMapper<MailSubscription>() {
                    public MailSubscription mapRow(ResultSet rs, int i) throws SQLException {
                        MailSubscription mailSubscription = new MailSubscription();
                        mailSubscription.setChannel(rs.getInt("Channel"));
                        mailSubscription.setDocumenttype(rs.getInt("DocumentType"));
                        mailSubscription.setEmail(email);
                        mailSubscription.setInterval(rs.getString("MailInterval"));
                        mailSubscription.setLanguage(rs.getInt("Language"));
                        return mailSubscription;
                    }
                });
    }

    public static void removeMailSubscription(String email, int channel, int documenttype) throws SystemException, InvalidParameterException {

        if (email == null || (channel == -1 && documenttype == -1)) {
            throw new InvalidParameterException("email", SOURCE);
        }

        email = email.toLowerCase();

        Connection c = null;
        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement st;
            if (channel == -1) {
                st = c.prepareStatement("delete from mailsubscription where Email = ? and DocumentType = ?");
                st.setString(1, email);
                st.setInt(2, documenttype);
            } else if (documenttype == -1) {
                st = c.prepareStatement("delete from mailsubscription where Email = ? and Channel = ?");
                st.setString(1, email);
                st.setInt(2, channel);
            } else {
                st = c.prepareStatement("delete from mailsubscription where Email = ? and Channel = ? and DocumentType = ?");
                st.setString(1, email);
                st.setInt(2, channel);
                st.setInt(3, documenttype);
            }

            st.execute();
        } catch (SQLException e) {
            throw new SystemException("Databasefeil", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }
    }

    public static void removeAllMailSubscriptions(String email) throws SystemException, InvalidParameterException {
        if (email == null) {
            throw new InvalidParameterException("email", SOURCE);
        }

        email = email.toLowerCase();

        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement st = c.prepareStatement("delete from mailsubscription where Email = ?");
            st.setString(1, email);
            st.execute();
        } catch (SQLException e) {
            throw new SystemException("Databasefeil", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }
    }

    public static List getAllMailSubscriptions() throws SystemException {
        List emails = new ArrayList();

        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            ResultSet rs = SQLHelper.getResultSet(c, "select distinct Email from mailsubscription order by Email");
            while(rs.next()) {
                emails.add(rs.getString(1));
            }
            rs.close();
            rs = null;
        } catch (SQLException e) {
            throw new SystemException("Databasefeil", SOURCE, e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {

            }
        }

        return emails;
    }
}
