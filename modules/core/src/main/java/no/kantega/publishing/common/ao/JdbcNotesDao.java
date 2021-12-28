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

import no.kantega.publishing.api.notes.NotesDao;
import no.kantega.publishing.common.data.Note;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcNotesDao extends JdbcDaoSupport implements NotesDao {
    private NotesRowMapper rowMapper = new NotesRowMapper();

    public List<Note> getNotesByContentId(int contentId) {
        return getJdbcTemplate().query("SELECT * FROM notes WHERE ContentId = ? ORDER BY CreatedDate DESC", new Object[] {contentId}, rowMapper);
    }

    public void addNote(Note note) {
        getJdbcTemplate().update("INSERT INTO notes (Author, NoteText, CreatedDate, ContentId) VALUES (?, ?, ?, ?)", note.getAuthor(), note.getText(), note.getDate(), note.getContentId());
    }

    public void removeNote(int noteId) {
        getJdbcTemplate().update("DELETE FROM notes WHERE NoteId=?", noteId);
    }

    private class NotesRowMapper implements RowMapper<Note> {
        public Note mapRow(ResultSet rs, int i) throws SQLException {
            Note note = new Note();
            note.setNoteId(rs.getInt("NoteId"));
            note.setAuthor(rs.getString("Author"));
            note.setText(rs.getString("NoteText"));
            note.setDate(rs.getTimestamp("CreatedDate"));
            note.setContentId(rs.getInt("ContentId"));
            return note;
        }
    }
}
