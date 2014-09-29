package no.kantega.publishing.security.action;

import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.password.PasswordValidator;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.security.login.PostResetPasswordHandler;
import no.kantega.security.api.identity.Identity;
import no.kantega.security.api.password.DefaultResetPasswordToken;
import no.kantega.security.api.password.PasswordManager;
import no.kantega.security.api.password.ResetPasswordTokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

public class ResetPasswordAction extends AbstractLoginAction {
    private static final Logger log = LoggerFactory.getLogger(ResetPasswordAction.class);

    private String resetPasswordView = null;
    private String resetPasswordErrorView = null;
    private PasswordValidator passwordValidator;
    private List<PostResetPasswordHandler> postResetPasswordHandlers;


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
        Map<String, Object> model = new HashMap<>();
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

        Map<String, Object> model = new HashMap<>();

        passwordManager.setPassword(identity, password1, password2);

        ResetPasswordTokenManager tokenManager = getResetPasswordTokenManager();
        tokenManager.deleteTokensForIdentity(identity);

        for (PostResetPasswordHandler resetPasswordHandler : postResetPasswordHandlers) {
            resetPasswordHandler.handlePostResetPassword(identity, request);
        }

        model.put("loginLayout", getLoginLayout());
        model.put("passwordChanged", true);


        return new ModelAndView(resetPasswordView, model);

    }

    public ModelAndView showPasswordForm(HttpServletRequest request) throws Exception {
        Map<String, Object> model = new HashMap<>();

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

    @Autowired(required = false)
    public void setPostResetPasswordHandlers(List<PostResetPasswordHandler> postResetPasswordHandlers){
        if(postResetPasswordHandlers == null){
            this.postResetPasswordHandlers = emptyList();
        } else {
            this.postResetPasswordHandlers = postResetPasswordHandlers;
        }
    }
}
