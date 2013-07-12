package no.kantega.publishing.security.action;

import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.log.Log;
import no.kantega.commons.password.PasswordValidator;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.security.login.PostResetPasswordHandler;
import no.kantega.security.api.identity.Identity;
import no.kantega.security.api.password.DefaultResetPasswordToken;
import no.kantega.security.api.password.PasswordManager;
import no.kantega.security.api.password.ResetPasswordTokenManager;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class ResetPasswordAction extends AbstractLoginAction {
    private static final String SOURCE = "aksess.ResetPassordAction";

    private String resetPasswordView = null;
    private String resetPasswordErrorView = null;
    private PasswordValidator passwordValidator;



    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!Aksess.isSecurityAllowPasswordReset()) {
            throw new Exception("Password reset not allowed");
        }

        String token = request.getParameter("token");
        Identity identity = getIdentityFromRequest(request);
        if (token == null || identity == null) {
            return showErrorForm("aksess.resetpassword.missingparameter");
        }

        ResetPasswordTokenManager tokenManager = getResetPasswordTokenManager();
        DefaultResetPasswordToken tok = new DefaultResetPasswordToken();
        tok.setToken(token);

        boolean validToken = tokenManager.verifyPasswordToken(identity, tok);
        if (!validToken) {
            return showErrorForm("aksess.resetpassword.invalidtoken");
        }

        if (request.getParameter("password1") != null) {
            return resetPassword(request);
        } else {
            return showPasswordForm(request);
        }
    }


    private ModelAndView showErrorForm(String error) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("loginLayout", getLoginLayout());
        model.put("error", error);
        return new ModelAndView(resetPasswordErrorView, model);
    }

    public ModelAndView resetPassword(HttpServletRequest request) throws Exception {
        String password1 = request.getParameter("password1");
        String password2 = request.getParameter("password2");

        ValidationErrors errors = passwordValidator.isValidPassword(password1, password2);
        if (errors.getLength() > 0) {
            ModelAndView modelAndView = showPasswordForm(request);
            Map model = modelAndView.getModel();
            model.put("passwordErrors", errors);
            return modelAndView;
        }

        Identity identity = getIdentityFromRequest(request);

        PasswordManager passwordManager = getPasswordManager(identity.getDomain());
        if (passwordManager == null) {
            return showErrorForm("aksess.resetpassword.nopasswordmanager");
        }

        Map<String, Object> model = new HashMap<String, Object>();

        passwordManager.setPassword(identity, password1, password2);

        ResetPasswordTokenManager tokenManager = getResetPasswordTokenManager();
        tokenManager.deleteTokensForIdentity(identity);

        Configuration c = Aksess.getConfiguration();
        String postResetPasswordHandler = c.getString("security.login.postresetpasswordhandler");
        if (postResetPasswordHandler != null && !postResetPasswordHandler.isEmpty()) {
            try {
                PostResetPasswordHandler resetHandler = (PostResetPasswordHandler)Class.forName(postResetPasswordHandler).newInstance();
                resetHandler.handlePostResetPassword(identity, request);
            } catch (Exception e) {
                Log.error(SOURCE, e, null, null);
            }
        }

        model.put("loginLayout", getLoginLayout());
        model.put("passwordChanged", true);


        return new ModelAndView(resetPasswordView, model);

    }

    public ModelAndView showPasswordForm(HttpServletRequest request) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        String token = request.getParameter("token");
        String userid = request.getParameter("username");
        String domain = request.getParameter("domain");
        model.put("loginLayout", getLoginLayout());
        model.put("token", token);
        model.put("username", userid);
        model.put("domain", domain);

        return new ModelAndView(resetPasswordView, model);
    }

    public void setResetPasswordView(String resetPasswordView) {
        this.resetPasswordView = resetPasswordView;
    }

    public void setResetPasswordErrorView(String resetPasswordErrorView) {
        this.resetPasswordErrorView = resetPasswordErrorView;
    }

    public void setPasswordValidator(PasswordValidator passwordValidator) {
        this.passwordValidator = passwordValidator;
    }
}