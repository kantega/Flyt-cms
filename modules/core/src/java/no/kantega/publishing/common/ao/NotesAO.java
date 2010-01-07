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

import no.kantega.publishing.common.data.Note;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.sqlsearch.SearchTerm;
import no.kantega.commons.log.Log;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotesAO {

    private final static String SOURCE = "aksess.NotesAO" ;

    public static Note[] getNotesByContentId(int contentId) throws SystemException {

        List notes = new ArrayList();

        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement p = c.prepareStatement("SELECT * FROM notes WHERE ContentId = ? ORDER BY CreatedDate DESC");
            p.setInt(1, contentId);
            ResultSet rs = p.executeQuery();
            while(rs.next()) {
                notes.add(getNoteFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new SystemException("SQL Exception while getting notes", SOURCE, e);
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    Log.error(SOURCE, e, null, null);
                }
            }
        }
        return (Note[]) notes.toArray(new Note[0]);
    }


    public static int addNote(Note note) throws SystemException {
        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement p = c.prepareStatement("INSERT INTO notes (Author, NoteText, CreatedDate, ContentId) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            p.setString(1, note.getAuthor());
            p.setString(2, note.getText());
            p.setTimestamp(3, new java.sql.Timestamp(note.getDate().getTime()));
            p.setInt(4, note.getContentId());

            p.executeUpdate();

            ResultSet rs = p.getGeneratedKeys();
            if(rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SystemException("Could get the generated key", SOURCE, null);
            }

        } catch (SQLException e) {
          throw new SystemException("SQL exception while adding note", SOURCE, e);
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    Log.error(SOURCE, e, null, null);
                }
            }
        }
    }

    private static Note getNoteFromResultSet(ResultSet rs) throws SQLException {
        Note note = new Note();
        note.setNoteId(rs.getInt("NoteId"));
        note.setAuthor(rs.getString("Author"));
        note.setText(rs.getString("NoteText"));
        note.setDate(rs.getTimestamp("CreatedDate"));
        note.setContentId(rs.getInt("ContentId"));
        return note;
    }

    public static void removeNote(int nid) throws SystemException {
        Connection c = null;

        try {
            c = dbConnectionFactory.getConnection();
            PreparedStatement p = c.prepareStatement("DELETE FROM notes WHERE NoteId=?");
            p.setInt(1, nid);
            p.executeUpdate();
        } catch (SQLException e) {
            throw new SystemException("Could remove note", SOURCE, null);
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    Log.error(SOURCE, e, null, null);
                }
            }
        }
    }
}
