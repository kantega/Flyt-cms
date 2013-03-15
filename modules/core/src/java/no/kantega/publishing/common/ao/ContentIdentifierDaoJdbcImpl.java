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

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentIdentifierDao;
import no.kantega.publishing.common.ao.rowmapper.ContentIdentifierRowMapper;
import no.kantega.publishing.common.data.enums.AssociationType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ContentIdentifierDaoJdbcImpl extends NamedParameterJdbcDaoSupport implements ContentIdentifierDao {

    private final ContentIdentifierRowMapper rowMapper = new ContentIdentifierRowMapper();

    @Cacheable(value = "ContentIdentifierCache")
    public ContentIdentifier getContentIdentifierBySiteIdAndAlias(int siteId, String alias) throws SystemException{
        String sql = "select associations.AssociationId, associations.SiteId, content.ContentId, content.Alias from associations, content" +
                " where content.Alias = :alias and associations.Type = :associationtype and associations.SiteId = :siteid" +
                " and content.ContentId = associations.ContentId and (associations.IsDeleted = 0 or associations.IsDeleted is null)";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("alias", getNormalisedAlias(alias));
        parameters.put("associationtype", AssociationType.DEFAULT_POSTING_FOR_SITE);
        parameters.put("siteid", siteId);

        try {
            return getNamedParameterJdbcTemplate().queryForObject(sql, parameters, rowMapper);
        }  catch (EmptyResultDataAccessException e){
            return null;
        } catch (DataAccessException e) {
            throw new SystemException(String.format("Could not find ContentIdentifier for siteId %s and alias %s", siteId, alias), "ContentIdentifierDaoJdbcImpl", e);
        }
    }

    private String getNormalisedAlias(String alias) {
        if(alias.endsWith("/")){
             return alias;
        } else {
            return alias + "/";
        }
    }

    @Cacheable("ContentIdentifierCache")
    public String getAliasBySiteIdAndAssociationId(int siteId, int associationId) throws SystemException {
        String sql = "select content.Alias from associations, content" +
                " where associations.Type = :associationtype and associations.SiteId = :siteid and associations.AssociationId = :associationid" +
                " and content.ContentId = associations.ContentId and (associations.IsDeleted = 0 or associations.IsDeleted is null)";
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("associationid", associationId);
        parameters.put("associationtype", AssociationType.DEFAULT_POSTING_FOR_SITE);
        parameters.put("siteid", siteId);

        try {
            return getNamedParameterJdbcTemplate().queryForObject(sql, parameters, String.class);
        } catch (EmptyResultDataAccessException e){
            return null;
        } catch (DataAccessException e) {
            throw new SystemException(String.format("Could not find ContentIdentifier for siteId %s and associationId %s", siteId, associationId), "ContentIdentifierDaoJdbcImpl", e);
        }
    }

    @Override
    public List<ContentIdentifier> getContentIdentifiersByAlias(String alias) {
        String sql = "select associations.AssociationId, associations.SiteId, content.ContentId, content.Alias from associations, content" +
                " where content.Alias = :alias and associations.Type = :associationtype" +
                " and content.ContentId = associations.ContentId and (associations.IsDeleted = 0 or associations.IsDeleted is null)";
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("alias", getNormalisedAlias(alias));
        parameters.put("associationtype", AssociationType.DEFAULT_POSTING_FOR_SITE);

        try {
            return getNamedParameterJdbcTemplate().query(sql, parameters, rowMapper);
        } catch (DataAccessException e) {
            throw new SystemException(String.format("Could not find ContentIdentifier for alias %s", alias), "ContentIdentifierDaoJdbcImpl", e);
        }
    }
}
