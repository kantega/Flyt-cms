package no.kantega.publishing.security.action;

import no.kantega.publishing.common.Aksess;
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
    private String resetPasswordView = null;
    private String resetPasswordErrorView = null;
    private int minPasswordLength = 6;



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

        if (isValidPassword(password1, password2)) {
            ModelAndView modelAndView = showPasswordForm(request);
            modelAndView.getModel().put("error", "aksess.resetpassword.passwordmissing");
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

        model.put("loginLayout", getLoginLayout());
        model.put("minPasswordLength", minPasswordLength);
        model.put("passwordChanged", true);


        return new ModelAndView(resetPasswordView, model);

    }

    private boolean isValidPassword(String password1, String password2) {
        return password1 == null || password1.length() < minPasswordLength || (!password1.equals(password2));
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
        model.put("minPasswordLength", minPasswordLength);

        return new ModelAndView(resetPasswordView, model);
    }

    public void setResetPasswordView(String resetPasswordView) {
        this.resetPasswordView = resetPasswordView;
    }

    public void setResetPasswordErrorView(String resetPasswordErrorView) {
        this.resetPasswordErrorView = resetPasswordErrorView;
    }

    public void setMinPasswordLength(int minPasswordLength) {
        this.minPasswordLength = minPasswordLength;
    }
}
