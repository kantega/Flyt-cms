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

package no.kantega.publishing.admin.security.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.model.BaseObject;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.ObjectType;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.content.api.ContentIdHelper;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.Permission;
import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.data.SecurityIdentifier;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.data.enums.NotificationPriority;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.publishing.security.realm.SecurityRealm;
import no.kantega.publishing.security.realm.SecurityRealmFactory;
import no.kantega.publishing.security.service.SecurityService;
import no.kantega.publishing.topicmaps.data.TopicMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditPermissionsAction extends AbstractController {
    public final static String PERMISSIONS_LIST = "tmpPermissionsList";
    public final static String PERMISSIONS_OBJECT = "tmpPermissionsObject";

    @Autowired
    private ContentIdHelper contentIdHelper;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request, "utf-8");

        int objectId   = param.getInt("id");
        int objectType = param.getInt("type");
        String url = param.getString("url");
        if (objectId == -1 && url != null) {
            ContentIdentifier cid = contentIdHelper.fromRequestAndUrl(request, url);
            objectId = cid.getAssociationId();
        }

        Integer objSecurityId = objectId;

        // Object user is editing
        BaseObject object = null;

        // Object with permissions same as object if not content object
        BaseObject permissionObject = null;
        List<Permission> permissions = null;

        String title = "";
        String inheritedTitle = "";

        Map model = new HashMap();

        HttpSession session = request.getSession(true);

        object = (BaseObject)session.getAttribute(PERMISSIONS_OBJECT);
        if (object != null) {
            // Found objects in session
            if (object.getObjectType() == ObjectType.CONTENT) {
                permissionObject = ((Content)object).getAssociation();
            } else {
                permissionObject = object;
            }

            title = (String)session.getAttribute("tmpPermissionsTitle");
            inheritedTitle = (String)session.getAttribute("tmpPermissionsInheritedTitle");
            permissions = (List<Permission>)session.getAttribute(PERMISSIONS_LIST);
            objSecurityId = (Integer)session.getAttribute("tmpObjSecurityId");
        }

        if ((objectId != -1) && (permissionObject == null || permissionObject.getId() != objectId)) {
            // Object not in session or wrong object in session
            if (objectType == ObjectType.CONTENT) {
                objectType = ObjectType.ASSOCIATION;
            }

            if (objectType == ObjectType.ASSOCIATION) {
                ContentManagementService aksessService = new ContentManagementService(request);
                ContentIdentifier cid = contentIdHelper.fromRequestAndUrl(request, url);
                Content content = aksessService.getContent(cid);
                title = content.getTitle();
                objSecurityId = content.getAssociation().getSecurityId();
                if (objSecurityId != objectId) {
                    ContentIdentifier inheritcid =  ContentIdentifier.fromAssociationId(objSecurityId);
                    Content inheritedFrom = aksessService.getContent(inheritcid);
                    if (inheritedFrom != null) {
                        inheritedTitle = inheritedFrom.getTitle();
                    }
                }
                object = content;
                permissionObject = content.getAssociation();
            } else if (objectType == ObjectType.MULTIMEDIA) {
                MultimediaService mediaService = new MultimediaService(request);

                Multimedia multimedia;
                if (objectId == 0) {
                    multimedia = new Multimedia();
                    multimedia.setName("Rot-katalog");
                    multimedia.setId(0);
                    multimedia.setSecurityId(0);
                } else {
                    multimedia = mediaService.getMultimedia(objectId);
                }
                title = multimedia.getName();
                objSecurityId = multimedia.getSecurityId();
                if (objSecurityId != objectId) {
                    Multimedia inheritedFrom = mediaService.getMultimedia(objSecurityId);
                    if (inheritedFrom != null) {
                        inheritedTitle = inheritedFrom.getName();
                    }
                }
                object = multimedia;
                permissionObject = object;
            } else if (objectType == ObjectType.TOPICMAP) {
                TopicMapService topicService = new TopicMapService(request);
                TopicMap topicMap = topicService.getTopicMap(objectId);
                title  = topicMap.getName();
                object = topicMap;
                permissionObject = object;
            }


            permissions = SecurityService.getPermissions(object);

            // Data is stored in session so user can add users and remove permissions before saving them
            session.setAttribute("tmpPermissionsTitle", title);
            session.setAttribute("tmpPermissionsInheritedTitle", inheritedTitle);
            session.setAttribute(PERMISSIONS_OBJECT, object);
            session.setAttribute(PERMISSIONS_LIST, permissions);
            session.setAttribute("tmpObjSecurityId", objSecurityId);
        }

        if (object == null) {
            // Object no longer in session
            return new ModelAndView("/WEB-INF/jsp/admin/generic/windowclose.jsp");
        }

        if (permissions == null) {
            permissions = new ArrayList<Permission>();
        }

        // Lookup name for users / roles
        SecurityRealm realm = SecurityRealmFactory.getInstance();

        for (Permission p : permissions) {

            SecurityIdentifier secId = p.getSecurityIdentifier();
            // Look up rolename / username
            if (secId instanceof User) {
                User tmp = realm.lookupUser(secId.getId());
                if (tmp != null) {
                    ((User) secId).setGivenName(tmp.getGivenName());
                    ((User) secId).setSurname(tmp.getSurname());
                } else {
                    ((User) secId).setGivenName(secId.getId());
                }
            } else {
                Role tmp = realm.lookupRole(secId.getId());
                if (tmp != null) {
                    ((Role) secId).setName(tmp.getName());
                } else {
                    ((Role) secId).setName(secId.getId());
                }
            }
        }

        SecuritySession securitySession = SecuritySession.getInstance(request);
        model.put("canModifyPermissions", securitySession.isAuthorized(object, Privilege.FULL_CONTROL));
        model.put("objSecurityId", objSecurityId);
        model.put("objectId", permissionObject.getId());
        model.put("permissionsList", permissions);
        model.put("title", title);
        model.put("inheritedTitle", inheritedTitle);

        // Privileges
        List privileges = new ArrayList();
        privileges.add(Privilege.VIEW_CONTENT);
        privileges.add(Privilege.UPDATE_CONTENT);
        if (object != null && object.getObjectType() == ObjectType.CONTENT) {
            privileges.add(Privilege.APPROVE_CONTENT);
        }
        privileges.add(Privilege.FULL_CONTROL);
        model.put("privileges", privileges);
        
        // Notification
        if (object != null && object.getObjectType() == ObjectType.CONTENT) {
            model.put("priorities", NotificationPriority.values());
        }

        model.put("minNotificationPrivilege", Privilege.APPROVE_CONTENT);


        return new ModelAndView("/WEB-INF/jsp/admin/security/editpermissions.jsp", model);
    }
}
