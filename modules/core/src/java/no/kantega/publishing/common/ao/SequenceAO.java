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

package no.kantega.publishing.common.ao;

import org.xml.sax.helpers.XMLFilterImpl;
import org.apache.log4j.Logger;

import java.sql.*;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.util.database.dbConnectionFactory;

/**
 *
 */
public class SequenceAO {
    private static Logger log = Logger.getLogger(SequenceAO.class);
    private static final String SOURCE = "aksess.SequenceAO";

    public synchronized static int nextNumber(String sequence) throws SystemException {
        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();

            PreparedStatement p = c.prepareStatement("SELECT Id, CurrentNumber from numbersequence where SequenceName=?");
            p.setString(1, sequence);

            ResultSet rs = p.executeQuery();

            if(rs.next()) {
                int id = rs.getInt("Id");
                int currentNumber = rs.getInt("CurrentNumber");
                PreparedStatement p2 = c.prepareStatement("update numbersequence set CurrentNumber=?, LastModified=? WHERE Id=?");
                p2.setInt(1, currentNumber+1);
                p2.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
                p2.setInt(3, id);
                p2.executeUpdate();
                return currentNumber +1;
            } else {
                PreparedStatement p2 = c.prepareStatement("INSERT INTO numbersequence (SequenceName, CurrentNumber, LastModified) VALUES (?, 1, ?)");
                p2.setString(1, sequence);
                p2.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
                p2.executeUpdate();
                return 1;
            }
        } catch (SQLException e) {
            log.error(e);
            throw new SystemException("",SequenceAO.class.getName(), e);
        } finally {
            try {
                if (c != null) {
                    c.close();
                }
            } catch (SQLException e) {
                Log.error(SOURCE, e, null, null);
            }
        }
    }
}
