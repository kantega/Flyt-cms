package no.kantega.publishing.security.action;

import no.kantega.commons.exception.ConfigurationException;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.modules.mailsender.MailSender;
import no.kantega.security.api.common.SystemException;
import no.kantega.security.api.identity.Identity;
import no.kantega.security.api.password.ResetPasswordToken;
import no.kantega.security.api.password.ResetPasswordTokenManager;
import no.kantega.security.api.profile.Profile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class RequestPasswordResetAction extends AbstractLoginAction  {
    private String requestResetPasswordView = null;
    private String mailSubject;
    private String mailFrom;
    private String mailTemplate;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Identity identity = getIdentityFromRequest(request);
        if (identity != null) {
            return sendPasswordRequest(request, response);
        } else {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("loginLayout", getLoginLayout());
            return new ModelAndView(requestResetPasswordView, model);
        }
    }

    private ModelAndView sendPasswordRequest(HttpServletRequest request, HttpServletResponse response) throws SystemException, ConfigurationException {
        Identity identity = getIdentityFromRequest(request);

        Profile userProfile = getProfileManager().getProfileForUser(identity);

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("loginLayout", getLoginLayout());
        
        if (userProfile == null) {
            model.put("error", "aksess.resetpassword.no-profile");
            return new ModelAndView(requestResetPasswordView, model);
        } else if (userProfile.getEmail() == null || userProfile.getEmail().indexOf("@") == -1) {
            model.put("error", "aksess.resetpassword.no-email");
            return new ModelAndView(requestResetPasswordView, model);
        }

        Date expireDate = getExpireDate();

        ResetPasswordTokenManager tokenManager = getResetPasswordTokenManager();
        ResetPasswordToken token = tokenManager.generateResetPasswordToken(identity, expireDate);

        Map<String, Object> mailParam = new HashMap<String, Object>();
        String url = Aksess.getApplicationUrl();
        url += "/ResetPassword.action";
        url += "?token=" + token.getToken();
        url += "&amp;username=" + identity.getUserId();
        url += "&amp;domain=" + identity.getDomain();
        mailParam.put("url", url);
        mailParam.put("expireDate", expireDate);
        mailParam.put("profile", userProfile);

        if (mailFrom == null || mailFrom.length() == 0) {
            mailFrom = Aksess.getConfiguration().getString("mail.from");
        }

        try {
            MailSender.send(mailFrom, userProfile.getEmail(), mailSubject, mailTemplate, mailParam);
        } catch (Exception e) {
            model.put("error", "aksess.resetpassword.email-failed");
            return new ModelAndView(requestResetPasswordView, model);
        }

        model.put("requestSent", true);
        return new ModelAndView(requestResetPasswordView, model);
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public void setRequestResetPasswordView(String requestResetPasswordView) {
        this.requestResetPasswordView = requestResetPasswordView;
    }

    public void setMailTemplate(String mailTemplate) {
        this.mailTemplate = mailTemplate;
    }

    public Date getExpireDate() {
        long now = new Date().getTime();
        long h24 = now + 1000*60*60*24; // 24h


        return new Date(h24);
    }
}
