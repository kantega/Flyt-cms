package no.kantega.useradmin.controls;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import no.kantega.useradmin.model.RoleManagementConfiguration;
import no.kantega.useradmin.model.ProfileManagementConfiguration;
import no.kantega.security.api.impl.dbuser.password.DbUserPasswordManager;
import no.kantega.security.api.impl.dbuser.password.MD5Crypt;
import no.kantega.security.api.impl.dbuser.profile.DbUserProfileManager;
import no.kantega.security.api.impl.dbuser.profile.DbUserProfileUpdateManager;
import no.kantega.security.api.impl.dbuser.role.DbUserRoleManager;
import no.kantega.security.api.impl.dbuser.role.DbUserRoleUpdateManager;
import no.kantega.security.api.role.DefaultRoleId;
import no.kantega.security.api.identity.DefaultIdentity;
import no.kantega.publishing.test.database.HSQLDBDatabaseCreator;
import no.kantega.publishing.common.Aksess;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CreateInitialUserControllerTest {
    String domain = "mydomain";

    CreateInitialUserController controller;
    DbUserProfileManager profileManager;
    DbUserRoleManager roleManager;

    @Before
    public void setupDoHandleRequest() {
        DataSource ds = new HSQLDBDatabaseCreator("aksess", getClass().getClassLoader().getResourceAsStream("aksess-useradmin-db.sql")).createDatabase();

        // Setup profile management config
        ProfileManagementConfiguration profileConfig = new ProfileManagementConfiguration();
        profileConfig.setDomain(domain);

        // Password manager
        DbUserPasswordManager passwordManager = new DbUserPasswordManager();
        passwordManager.setDomain(domain);
        passwordManager.setDataSource(ds);

        passwordManager.setPasswordCrypt(new MD5Crypt());
        profileConfig.setPasswordManager(passwordManager);

        // Profile manager
        profileManager = new DbUserProfileManager();
        profileManager.setDataSource(ds);
        profileManager.setDomain(domain);
        profileConfig.setProfileManager(profileManager);

        // Profile update manager
        DbUserProfileUpdateManager profileUpdateManager = new DbUserProfileUpdateManager();
        profileUpdateManager.setDataSource(ds);
        profileUpdateManager.setDomain(domain);
        profileConfig.setProfileUpdateManager(profileUpdateManager);

        List<ProfileManagementConfiguration> profileConfigs = new ArrayList<ProfileManagementConfiguration>();
        profileConfigs.add(profileConfig);


        // Setup role management config
        RoleManagementConfiguration roleConfig = new RoleManagementConfiguration();
        roleConfig.setDomain(domain);

        // Role manager
        roleManager = new DbUserRoleManager();
        roleManager.setDomain(domain);
        roleManager.setDataSource(ds);
        roleConfig.setRoleManager(roleManager);

        // Role update manager
        DbUserRoleUpdateManager roleUpdateManager = new DbUserRoleUpdateManager();
        roleUpdateManager.setDomain(domain);
        roleUpdateManager.setDataSource(ds);
        roleConfig.setRoleUpdateManager(roleUpdateManager);

        List<RoleManagementConfiguration> roleConfigs = new ArrayList<RoleManagementConfiguration>();
        roleConfigs.add(roleConfig);


        // Setup controller
        controller = new CreateInitialUserController();

        controller.setDefaultDomain(domain);
        controller.setProfileConfiguration(profileConfigs);
        controller.setRoleConfiguration(roleConfigs);

    }

    @Test
    public void testDoHandleRequest() throws Exception {
        String userId = "admin";

        // When request is get
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");

        ModelAndView modelAndView = controller.handleRequestInternal(request, null);

        // Then view should be form page
        assertEquals(CreateInitialUserController.FORM_VIEW, modelAndView.getViewName());

        // When database is empty
        DefaultRoleId roleId = new DefaultRoleId();
        roleId.setDomain(domain);
        roleId.setId(Aksess.getAdminRole());

        // The role should not exists
        assertNull(roleManager.getRoleById(roleId));

        // User should not exists
        DefaultIdentity identity = new DefaultIdentity();
        identity.setDomain(domain);
        identity.setUserId(userId);

        assertNull(profileManager.getProfileForUser(identity));

        // When request is POST and username + password is specified
        request.setParameter("username", userId);
        request.setParameter("password", "password");
        request.setParameter("password2", "password");
        request.setMethod("POST");

        // And IP address is remote
        request.setRemoteAddr("212.200.200.200");

        modelAndView = controller.handleRequestInternal(request, null);

        // Then view should be Not authorized page
        assertEquals(CreateInitialUserController.NOT_AUTH_VIEW, modelAndView.getViewName());


        // When IP address is local
        request.setRemoteAddr("127.0.0.1");
        modelAndView = controller.handleRequestInternal(request, null);

        // Then view should be confirmation page
        assertEquals(CreateInitialUserController.CONFIRM_VIEW, modelAndView.getViewName());

        // And user should have been created
        assertNotNull(profileManager.getProfileForUser(identity));

        // And the role should be created
        assertNotNull(roleManager.getRoleById(roleId));

        // When roles exists
        modelAndView = controller.handleRequestInternal(request, null);

        // The view should tell user roles exists
        assertEquals(CreateInitialUserController.EXISTS_VIEW, modelAndView.getViewName());
    }
}
