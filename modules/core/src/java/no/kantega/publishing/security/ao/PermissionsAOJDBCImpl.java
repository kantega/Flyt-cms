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

package no.kantega.publishing.security.ao;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.model.BaseObject;
import no.kantega.publishing.api.services.security.PermissionAO;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.AssociationAO;
import no.kantega.publishing.common.ao.MultimediaAO;
import no.kantega.publishing.common.data.enums.ObjectType;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.security.data.*;
import no.kantega.publishing.security.data.enums.NotificationPriority;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.publishing.security.data.enums.RoleType;
import no.kantega.publishing.spring.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PermissionsAOJDBCImpl extends NamedParameterJdbcDaoSupport implements PermissionAO {
    private static final Logger log = LoggerFactory.getLogger(PermissionsAOJDBCImpl.class);

    private static MultimediaAO multimediaAO;

    @Override
    @CacheEvict(value = "permissionCache", key = "#object.securityId")
    public void setPermissions(BaseObject object, List<Permission> permissions) throws SystemException {
        if (multimediaAO == null) {
            multimediaAO = RootContext.getInstance().getBean(MultimediaAO.class);
        }

        int securityId = object.getSecurityId();

        try (Connection c = dbConnectionFactory.getConnection()){
            PreparedStatement st;

            log.debug( "Setting permissions for id: {}, securityId: {}", object.getId(), securityId);

            boolean setPermissionsFromParent = false;
            if (permissions != null && permissions.size() == 0) {
                setPermissionsFromParent = true;
            }

            if (object.getId() != -1 && (securityId != object.getId()) && setPermissionsFromParent) {
                // Brukeren har opprettet nye rettigheter, men ikke valgt noen: gjør ingenting
                return;
            }

            if (securityId == object.getId()) {
                // Slett gamle rettigheter
                st = c.prepareStatement("delete from objectpermissions where ObjectSecurityId = ? and ObjectType = ?");
                st.setInt(1, securityId);
                st.setInt(2, object.getObjectType());
                st.execute();
            }
            st = c.prepareStatement("insert into objectpermissions values(?,?,?,?,?,?)");

            // Sett inn nye rettigheter
            if (permissions != null) {
                for (Permission permission : permissions) {
                    SecurityIdentifier sid = permission.getSecurityIdentifier();
                    st.setInt(1, object.getId());
                    st.setInt(2, object.getObjectType());
                    st.setInt(3, permission.getPrivilege());
                    st.setString(4, sid.getType());
                    st.setString(5, sid.getId());
                    st.setInt(6, permission.getNotificationPriority() != null ? permission.getNotificationPriority().getNotificationPriorityAsInt() : 0);
                    st.execute();
                }
            } else {
                // Legger inn default rettigheter, alle har tilgang til alt
                st.setInt(1, object.getId());
                st.setInt(2, object.getObjectType());
                st.setInt(3, Privilege.FULL_CONTROL);
                st.setString(4, RoleType.ROLE);
                st.setString(5, Aksess.getEveryoneRole());
                st.setInt(6, NotificationPriority.PRIORITY1.getNotificationPriorityAsInt());
                st.execute();
            }

            if (object.getId() != -1) {
                if (securityId != object.getId()) {
                    // Setter nye rettigheter for disse sidene
                    if (object.getObjectType() == ObjectType.ASSOCIATION) {
                        AssociationAO.setSecurityId(c, object, false);
                    } else if (object.getObjectType() == ObjectType.MULTIMEDIA) {
                        multimediaAO.setSecurityId(c, object, false);
                    }
                } else if (setPermissionsFromParent) {
                    if (object.getObjectType() == ObjectType.ASSOCIATION) {
                        AssociationAO.setSecurityId(c, object, true);
                    } else if (object.getObjectType() == ObjectType.MULTIMEDIA) {
                        multimediaAO.setSecurityId(c, object, true);
                    }
                }
            }
        } catch (SQLException e) {
            throw new SystemException("SQL feil", e);
        }
    }

    @Override
    public List<ObjectPermissionsOverview> getPermissionsOverview(int objectType) throws SystemException {
        List<ObjectPermissionsOverview> overview = new ArrayList<>();

        try (Connection c = dbConnectionFactory.getConnection()) {
            PreparedStatement st;

            if (objectType == ObjectType.MULTIMEDIA) {
                st = c.prepareStatement("select multimedia.Name as Name, objectpermissions.* from multimedia, objectpermissions where multimedia.Id = objectpermissions.ObjectSecurityId and objectpermissions.ObjectType = ? order by Name, ObjectSecurityId, Role");
            } else if (objectType == ObjectType.TOPICMAP) {
                st = c.prepareStatement("select tmmaps.Name as Name, objectpermissions.* from tmmaps, objectpermissions where tmmaps.Id = objectpermissions.ObjectSecurityId and objectpermissions.ObjectType = ? order by Name, ObjectSecurityId, Role");
            } else {
                st = c.prepareStatement("select contentversion.Title as Name, objectpermissions.* from content, contentversion, objectpermissions, associations where (contentversion.ContentId = content.ContentId) and (contentversion.IsActive = 1) and (content.contentId = associations.ContentId) and (associations.UniqueId = objectpermissions.ObjectSecurityId) and objectpermissions.ObjectType = ? order by Name, ObjectSecurityId, Role");
            }
            st.setInt(1, objectType);
            ResultSet rs = st.executeQuery();

            List<Permission> permissions = new ArrayList<>();
            int prev = -1;
            while(rs.next()) {
                String name = rs.getString("Name");
                int id = rs.getInt("ObjectSecurityId");
                if (id != prev) {
                    ObjectPermissionsOverview opo = new ObjectPermissionsOverview();
                    opo.setName(name);
                    permissions = new ArrayList<>();
                    opo.setPermissions(permissions);
                    overview.add(opo);
                    prev = id;
                }
                Permission p = new Permission();
                p.setPrivilege(rs.getInt("Privilege"));
                SecurityIdentifier sid;
                String type = rs.getString("RoleType");
                if(type.equals(RoleType.USER)) {
                    sid = new User();
                } else {
                    sid = new Role();
                }
                sid.setId(rs.getString("Role"));
                p.setSecurityIdentifier(sid);
                if (p.getPrivilege() >= Privilege.APPROVE_CONTENT) {
                    NotificationPriority priority = NotificationPriority.getNotificationPriorityAsEnum(rs.getInt("NotificationPriority"));
                    p.setNotificationPriority(priority);
                }
                permissions.add(p);
            }

            return overview;
        } catch (SQLException e) {
            throw new SystemException("SQL feil", e);
        }

    }

    @Override
    @Cacheable(value = "permissionCache", key = "#object.securityId")
    public List<Permission> getPermissions(BaseObject object) {
        return getJdbcTemplate().query("SELECT ObjectSecurityId, ObjectType, Privilege, RoleType, Role, NotificationPriority FROM objectpermissions where ObjectSecurityId = ?",
                rowMapper, object.getSecurityId());
    }

    public final RowMapper<Permission> rowMapper = new RowMapper<Permission>() {
        @Override
        public Permission mapRow(ResultSet rs, int rowNum) throws SQLException {
            Permission permission = new Permission();

            permission.setObjectSecurityId(rs.getInt("ObjectSecurityId"));
            permission.setObjectType(rs.getInt("ObjectType"));
            permission.setPrivilege(rs.getInt("Privilege"));
            String roleType = rs.getString("RoleType");
            SecurityIdentifier identifier = roleType.equalsIgnoreCase(RoleType.USER) ? new User() : new Role();
            identifier.setId(rs.getString("Role"));
            permission.setSecurityIdentifier(identifier);
            if (permission.getPrivilege() >= Privilege.APPROVE_CONTENT) {
                NotificationPriority priority = NotificationPriority.getNotificationPriorityAsEnum(rs.getInt("NotificationPriority"));
                permission.setNotificationPriority(priority);
            }
            return permission;
        }
    };

}
