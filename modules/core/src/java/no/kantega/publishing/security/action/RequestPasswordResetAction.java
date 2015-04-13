package no.kantega.publishing.security.action;

import no.kantega.commons.util.URLHelper;
import no.kantega.publishing.modules.mailsender.MailSender;
import no.kantega.security.api.common.SystemException;
import no.kantega.security.api.identity.Identity;
import no.kantega.security.api.password.ResetPasswordToken;
import no.kantega.security.api.password.ResetPasswordTokenManager;
import no.kantega.security.api.profile.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.apache.commons.lang3.StringUtils.isBlank;


public class RequestPasswordResetAction extends AbstractLoginAction  {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private String requestResetPasswordView = null;
    private String mailSubject;
    private String mailFrom;
    private String mailTemplate;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Identity identity = getIdentityFromRequest(request);
        if (identity != null) {
            return sendPasswordRequest(request);
        } else {
            return new ModelAndView(requestResetPasswordView, singletonMap("loginLayout", getLoginLayout()));
        }
    }

    private ModelAndView sendPasswordRequest(HttpServletRequest request) throws SystemException {
        Identity identity = getIdentityFromRequest(request);

        Profile userProfile = getProfileManager().getProfileForUser(identity);

        Map<String, Object> model = new HashMap<>();
        model.put("loginLayout", getLoginLayout());

        if (userProfile == null) {
            log.warn("Could not find userprofile for " + identity.getUserId());
            model.put("error", "aksess.resetpassword.no-profile");
            return new ModelAndView(requestResetPasswordView, model);
        } else if (userProfile.getEmail() == null || !userProfile.getEmail().contains("@")) {
            log.warn("Userprofile {} did not have any email",  identity.getUserId());
            model.put("error", "aksess.resetpassword.no-email");
            return new ModelAndView(requestResetPasswordView, model);
        }

        log.info("Generating password reset token for user " + userProfile.getIdentity().getUserId());

        Date expireDate = getExpireDate();

        ResetPasswordTokenManager tokenManager = getResetPasswordTokenManager();
        ResetPasswordToken token = tokenManager.generateResetPasswordToken(identity, expireDate);

        Map<String, Object> mailParam = new HashMap<>();
        String url = URLHelper.getServerURL(request) + request.getContextPath()
            + "/ResetPassword.action?token=" + token.getToken()
            + "&amp;username=" + identity.getUserId()
            + "&amp;domain=" + identity.getDomain();
        mailParam.put("url", url);
        mailParam.put("expireDate", expireDate);
        mailParam.put("profile", userProfile);

        if (isBlank(mailFrom)) {
            mailFrom = configuration.getString("mail.from");
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
