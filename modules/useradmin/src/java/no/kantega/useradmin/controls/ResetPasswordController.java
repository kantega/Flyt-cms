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

package no.kantega.useradmin.controls;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.password.PasswordValidator;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.modules.mailsender.MailSender;
import no.kantega.security.api.identity.DefaultIdentity;
import no.kantega.security.api.password.PasswordManager;
import no.kantega.security.api.profile.Profile;
import no.kantega.useradmin.model.ProfileManagementConfiguration;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class ResetPasswordController extends AbstractUserAdminController {

    private PasswordValidator passwordValidator;

    private SecureRandom random = new SecureRandom();

    public ModelAndView doHandleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);

        String domain = param.getString("domain");
        String id = param.getString("userId");

        DefaultIdentity ident = new DefaultIdentity();
        ident.setUserId(id);
        ident.setDomain(domain);

        Profile user = getProfileConfiguration(domain).getProfileManager().getProfileForUser(ident);

        String password1 = param.getString("password1");
        String password2 = param.getString("password2");

        Map<String, Object> model = new HashMap<>();
        model.put("domain", domain);

        Configuration aksessConf = Aksess.getConfiguration();

        String mailtemplate = aksessConf.getString("security.passwordmail.template");

        if(mailtemplate != null && getProfileConfiguration(domain).getPasswordManager() != null) {

            if( isBlank(user.getEmail()) ) {
                model.put("noemail", Boolean.TRUE);
            } else {

                model.put("maildefault", aksessConf.getBoolean("security.passwordmail.default", false));

                Velocity.init();

                File confDir = new File(Configuration.getApplicationDirectory());
                File mailDir = new File(confDir, "mail");

                File temp = new File(mailDir, mailtemplate);

                VelocityContext context = new VelocityContext();

                context.put("editor", aksessConf.getString("mail.editor"));
                StringBuilder name = new StringBuilder();
                name.append(user.getGivenName());
                if(user.getSurname() != null && !user.getSurname().trim().equals("")) {
                    name.append(" ").append(user.getSurname());
                }
                context.put("userId", id);
                context.put("name", name.toString());

                StringWriter sw = new StringWriter();
                Velocity.evaluate(context, sw, mailtemplate, new InputStreamReader(new FileInputStream(temp), aksessConf.getString("velocity.templateencoding", "iso-8859-1")));

                model.put("mailtemplate", sw.toString());

                model.put("mailto", user.getEmail());
                {
                    String from = aksessConf.getString("security.passwordmail.from");
                    from = from == null ? aksessConf.getString("mail.editor") : from;
                    model.put("mailfrom", from);
                }
                {
                    String subject = aksessConf.getString("security.passwordmail.subject");
                    subject = subject == null ? "Nytt passord" : subject;
                    model.put("mailsubject", subject);
                }
            }


        }

        if (password1 != null) {

            // We're sending email
            if(request.getParameter("mailsubmit") != null) {

                String from = param.getString("from");
                String subject = param.getString("subject");
                String message = param.getString("message");

                VelocityContext context = new VelocityContext();

                BigInteger number = new BigInteger(40, random);

                String generatedPassword = number.toString(Character.MAX_RADIX);

                context.put("password", generatedPassword);

                StringWriter sw = new StringWriter();
                Velocity.evaluate(context, sw, "message", new StringReader(message));

                MailSender.send(from, user.getEmail(), subject, sw.toString());

                ProfileManagementConfiguration config = getProfileConfiguration(domain);
                PasswordManager passwordManager = config.getPasswordManager();
                if (passwordManager != null) {
                    passwordManager.setPassword(ident, generatedPassword, generatedPassword);
                    model.put("userId", id);
                    model.put("message", "useradmin.password.saved");
                    return new ModelAndView(new RedirectView("../profile/search"), model);
                }

            } else {
                // Just set the password
                ValidationErrors errors = passwordValidator.isValidPassword(password1, password2);
                if (errors.getLength() > 0) {
                    model.put("errors", errors);
                } else {
                    ProfileManagementConfiguration config = getProfileConfiguration(domain);
                    PasswordManager passwordManager = config.getPasswordManager();
                    if (passwordManager != null) {
                        passwordManager.setPassword(ident, password1, password2);
                        model.put("userId", id);
                        model.put("message", "useradmin.password.saved");
                        return new ModelAndView(new RedirectView("../profile/search"), model);
                    }
                }
            }
        }
        model.put("userId", id);

        return new ModelAndView("password/reset", model);
    }

    public void setPasswordValidator(PasswordValidator passwordValidator) {
        this.passwordValidator = passwordValidator;
    }
}
