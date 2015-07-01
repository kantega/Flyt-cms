package no.kantega.publishing.security;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.configuration.SystemConfiguration;
import no.kantega.publishing.api.security.RememberMeHandler;
import no.kantega.publishing.common.Aksess;
import no.kantega.security.api.identity.DefaultIdentity;
import no.kantega.security.api.identity.Identity;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CookieRememberMeHandler implements RememberMeHandler, InitializingBean {

    @Autowired private SystemConfiguration configuration;

    private String cookieName;
    private int cookieMaxAge;
    private String hashKey;
    private String algorithm;
    private boolean useSsl;
    private String contextPath;
    private final String invalidCookieValue = "invalid";
    private int TWO_WEEKS = 1209600;
    private Pattern COOKIE_PATTERN = Pattern.compile("([^:]*):([^:]*):([^:]*)");


    public void afterPropertiesSet() {
        cookieName = configuration.getString("security.login.rememberme.cookieName", "no.kantega.openaksess.remember");
        cookieMaxAge = configuration.getInt("security.login.rememberme.period", TWO_WEEKS);
        hashKey = configuration.getString("security.login.rememberme.key");
        algorithm = configuration.getString("security.login.rememberme.algorithm", "SHA-256");
        useSsl = configuration.getBoolean("security.login.usessl", false);
        contextPath = Aksess.getContextPath();
    }

    @Override
    public void rememberUser(HttpServletResponse response, String username, String domain) {

        String messageDigestHex = getMessageDigestHex(username, domain, hashKey);

        String s = username + ":" + domain + ":" + messageDigestHex;
        String cookieValue = Base64.getEncoder().encodeToString((s.getBytes()));

        Cookie rememberMeCookie = createRememberMeCookie(cookieValue, cookieMaxAge);

        response.addCookie(rememberMeCookie);
    }


    @Override
    public Identity getRememberedIdentity(HttpServletRequest request) {

        Cookie rememberMeCookie = getRememberMeCookie(request.getCookies());

        if (rememberMeCookie == null) {
            return null;
        }

        String s = new String(Base64.getDecoder().decode(rememberMeCookie.getValue().getBytes()));
        Matcher matcher = COOKIE_PATTERN.matcher(s);
        if (!matcher.matches()) {
            return null;
        }

        String username = matcher.group(1);
        String domain = matcher.group(2);
        String digestFromCookie = matcher.group(3);

        String myDigest = getMessageDigestHex(username, domain, hashKey);

        boolean valid = digestFromCookie.equals(myDigest);
        if (valid) {
            DefaultIdentity identity = new DefaultIdentity();
            identity.setUserId(username);
            identity.setDomain(domain);
            return identity;
        } else {
            return null;
        }
    }


    @Override
    public void forgetUser(HttpServletRequest request, HttpServletResponse response) {
        Cookie rememberMeCookie = getRememberMeCookie(request.getCookies());
        if (rememberMeCookie != null) {
            // Create a new, identical cookie instead of modifying the existing one.
            // Some browsers will not identify the original and changed cookie as the same,
            // and will thus not remove/modify it if we use the incoming cookie.
            // Set an invalid cookie value as well as setting Max-Age since some browsers
            // refuse to remove the cookie even when the Max-Age is set to 0.
            response.addCookie(createRememberMeCookie(invalidCookieValue, 0));
        }
    }


    /**
     * Extracts the remember-me-cookie from the complete array of cookies.
     * @param cookies all user cookies
     * @return remember-me-cookie or null if not found.
     */
    private Cookie getRememberMeCookie(Cookie[] cookies) {
        Cookie rememberMeCookie = null;
        if (cookies == null) {
            return null;
        }

        for (Cookie c : cookies) {
            if (cookieName.equals(c.getName()) && !invalidCookieValue.equals(c.getValue())) {
                rememberMeCookie = c;
                break;
            }
        }
        if (rememberMeCookie == null) {
            return null;
        }
        return rememberMeCookie;
    }

    /**
     * Builds a SHA-256 hash of the username, domain, and key
     * @param username userId
     * @param domain user security domain
     * @param key arbitrary key, set by the config parameter 'security.login.rememberme.key'
     * @return sha256hash(username:domain:key)
     */
    private String getMessageDigestHex(String username, String domain, String key)  {
        String s = username + ":" + domain + ":" + key;
        byte bytes[] = s.getBytes();

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new SystemException("Unable to create Remember me cookie hash using " + algorithm, e);
        }
        messageDigest.update(bytes);
        byte digest[] = messageDigest.digest();

        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }



    private Cookie createRememberMeCookie(String cookieValue, int maxAge) {
        Cookie rememberMeCookie = new Cookie(cookieName, cookieValue);
        rememberMeCookie.setMaxAge(maxAge);
        rememberMeCookie.setPath(contextPath);
        if (useSsl) {
            rememberMeCookie.setSecure(true);
        }
        return rememberMeCookie;
    }

}
