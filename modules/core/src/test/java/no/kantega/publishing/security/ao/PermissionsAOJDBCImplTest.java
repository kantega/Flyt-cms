package no.kantega.publishing.security.ao;

import no.kantega.publishing.api.services.security.PermissionAO;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.security.data.Permission;
import no.kantega.publishing.security.data.enums.NotificationPriority;
import no.kantega.publishing.security.data.enums.RoleType;
import org.apache.commons.collections.Predicate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.select;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= "classpath*:spring/testContext.xml")
public class PermissionsAOJDBCImplTest {

    @Autowired
    private PermissionAO permissionAO;

    @Test
    public void shouldGetPermissionsFromDb(){
        Content object = new Content();
        object.setId(123);
        Association association = new Association();
        association.setSecurityId(123);
        association.setId(123);

        object.setAssociations(Collections.singletonList(association));
        List<Permission> permissions = permissionAO.getPermissions(object);
        assertEquals(2, permissions.size());
        assertEquals(1, select(permissions, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                Permission p = (Permission) object;
                return p.getPrivilege() == 1
                        && p.getNotificationPriority() == null
                        && p.getSecurityIdentifier().getType().equals(RoleType.ROLE)
                        && p.getSecurityIdentifier().getId().equals("innholdsprodusent")
                        && p.getObjectSecurityId() == 123
                        && p.getObjectType() == 3;
            }
        }).size());

        assertEquals(1, select(permissions, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                Permission p = (Permission) object;
                return p.getPrivilege() == 3
                        && p.getNotificationPriority() == NotificationPriority.PRIORITY1
                        && p.getSecurityIdentifier().getType().equals(RoleType.USER)
                        && p.getSecurityIdentifier().getId().equals("ZFG")
                        && p.getObjectSecurityId() == 123
                        && p.getObjectType() == 1;
            }
        }).size());

    }
}
