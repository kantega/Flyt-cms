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

import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.core.RowMapper;

public class JdbcNotesDao  extends JdbcDaoSupport implements NotesDao {
    private NotesRowMapper rowMapper = new NotesRowMapper();

    @SuppressWarnings("unchecked")
    public List<Note> getNotesByContentId(int contentId) {
        return getJdbcTemplate().query("SELECT * FROM notes WHERE ContentId = ? ORDER BY CreatedDate DESC", new Object[] {contentId}, rowMapper);
    }

    public void addNote(Note note) {
        getJdbcTemplate().update("INSERT INTO notes (Author, NoteText, CreatedDate, ContentId) VALUES (?, ?, ?, ?)", new Object[] {note.getAuthor(), note.getText(), note.getDate(), note.getContentId()});
    }

    public void removeNote(int noteId) {
        getJdbcTemplate().update("DELETE FROM notes WHERE NoteId=?", new Object[] {noteId});
    }

    private class NotesRowMapper implements RowMapper {
        public Object mapRow(ResultSet rs, int i) throws SQLException {
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
