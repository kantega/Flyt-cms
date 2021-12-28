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

package no.kantega.publishing.common.service.impl;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.StringHelper;
import no.kantega.publishing.api.content.ContentIdHelper;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.path.PathEntry;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.spring.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PathWorker {

    private static final Logger log = LoggerFactory.getLogger(PathWorker.class);

    private static ContentIdHelper contentIdHelper;

    public static List<PathEntry> getPathByAssociation(Association association) throws SystemException {
        List<PathEntry> pathEntries = new ArrayList<>();

        int pathIds[] = association.getPathElementIds();
        if (pathIds == null || pathIds.length == 0) {
            return pathEntries;
        }

        List<Integer> ids = new ArrayList<>(pathIds.length);
        for (int pathId : pathIds) {
            ids.add(pathId);
        }
        return new NamedParameterJdbcTemplate(dbConnectionFactory.getDataSource()).query("select contentversion.Title, content.ContentTemplateId, associations.AssociationId from content, contentversion, associations  where content.ContentId = contentversion.ContentId and contentversion.IsActive = 1 and content.contentId = associations.contentId and associations.AssociationId in (:ids) and associations.isdeleted = 0 and not associations.type = 0 ORDER BY associations.Depth", Collections.singletonMap("ids", ids), rowMapperWithContentTemplateId);
    }

    /**
     *
     * @param cid to get PathEntries for.
     * @return PathEntries leading to the content identified by ContentIdentifier
     * @throws SystemException
     * @deprecated use PathEntryService.getPathEntriesByContentIdentifier()
     */
    @Deprecated
    public static List<PathEntry> getPathByContentId(ContentIdentifier cid) throws SystemException {
        List<PathEntry> pathEntries = new ArrayList<>();

        if (cid == null) {
            return pathEntries;
        }
        if(contentIdHelper == null){
            contentIdHelper = RootContext.getInstance().getBean(ContentIdHelper.class);
        }
        contentIdHelper.assureContentIdAndAssociationIdSet(cid);
        String path;
        try {
            path = dbConnectionFactory.getJdbcTemplate().queryForObject("select Path from associations where UniqueId = ?", String.class, cid.getAssociationId());
        } catch (DataAccessException e) {
            return pathEntries;
        }

        List<Integer> pathIds = StringHelper.getIntsAsList(path, "/");
        if (pathIds.isEmpty()) {
            return pathEntries;
        }

        pathEntries = new NamedParameterJdbcTemplate(dbConnectionFactory.getDataSource()).query("select contentversion.Title, associations.AssociationId from content, contentversion, associations  where content.ContentId = contentversion.ContentId and contentversion.IsActive = 1 and content.contentId = associations.contentId and associations.AssociationId in (:ids) ORDER BY associations.Depth", Collections.singletonMap("ids", pathIds),rowMapper);

        return pathEntries;
    }

    public static List<PathEntry> getMultimediaPath(Multimedia mm) throws SystemException {
        List<PathEntry> pathEntries = new ArrayList<>();

        int parentId = mm.getParentId();

        JdbcTemplate jdbcTemplate = dbConnectionFactory.getJdbcTemplate();
        try {
            while (parentId > 0) {
                PathEntryWithParent entry = jdbcTemplate.queryForObject("select Id, ParentId, Name from multimedia where id = ?", pathEntryWithParentRowMapper, parentId);
                parentId = entry.parentId;
                pathEntries.add(0, entry);
                if(entry.getId() == parentId) {
                    log.warn("getMultimediaPath: Id == ParentId: " + parentId);
                    break;
                }
            }
        } catch (DataAccessException e) {
            log.error("", e);
        }

        return pathEntries;
    }

    private static final RowMapper<PathEntryWithParent> pathEntryWithParentRowMapper = (rs, rowNum) -> {
        String title = rs.getString("Name");
        int id = rs.getInt("Id");
        return new PathEntryWithParent(id, title, rs.getInt("ParentId"));
    };

    private static final RowMapper<PathEntry> rowMapper = (rs, rowNum) -> {
        String title = rs.getString("Title");
        int id = rs.getInt("AssociationId");
        return new PathEntry(id, title);
    };

    private static final RowMapper<PathEntry> rowMapperWithContentTemplateId = (rs, rowNum) -> {
        String title = rs.getString("Title");
        int id = rs.getInt("AssociationId");
        int contentTemplateId = rs.getInt("ContentTemplateId");
        PathEntry entry = new PathEntry(id, title);
        entry.setContentTemplateId(contentTemplateId);
        return entry;
    };

    private static class PathEntryWithParent extends PathEntry {
        int parentId;
        public PathEntryWithParent(int id, String title, int parentId) {
            super(id, title);
            this.parentId = parentId;
        }
    }
}
