package no.kantega.publishing.security;

import no.kantega.commons.util.Base64;
import no.kantega.publishing.common.Aksess;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CookieRememberMeHandler implements RememberMeHandler {
    public HttpServletResponse setRememberMe(HttpServletResponse response, String username, String domain)
            throws Exception {
        String key = Aksess.getConfiguration().getString("security.login.rememberme.key");

        String messageDigestHex = getMessageDigestHex(username, key, domain);

        String s = username + ":" + domain + ":" + messageDigestHex;
        String cookieValue = Base64.encode(s.getBytes());

        Cookie rememberMeCookie = new Cookie("no.kantega.openaksess.remember", cookieValue);
        // cookie expires after two weeks as default
        int maxAge = Aksess.getConfiguration().getInt("security.login.rememberme.period", 1209600);
        rememberMeCookie.setMaxAge(maxAge);
        response.addCookie(rememberMeCookie);
        return response;
    }

    public String[] hasRememberMe(HttpServletRequest request) throws Exception {
        Cookie cookies[] = request.getCookies();
        Cookie rememberMeCookie = null;
        for (Cookie c : cookies) {
            if (c.getName().equals("no.kantega.openaksess.remember")) {
                rememberMeCookie = c;
                break;
            }
        }
        if (rememberMeCookie == null) {
            return null;
        }

        String s = new String(Base64.decode(rememberMeCookie.getValue()));
        Pattern pattern = Pattern.compile("(\\w*):(\\w*):(\\w*)");
        Matcher matcher = pattern.matcher(s);
        if (!matcher.matches()) {
            return null;
        }

        String username = matcher.group(1);
        String domain = matcher.group(2);
        String digestFromCookie = matcher.group(3);
        String key = Aksess.getConfiguration().getString("security.login.rememberme.key");

        String myDigest = getMessageDigestHex(username, key, domain);

        boolean valid = digestFromCookie.equals(myDigest);
        if (valid) {
            return new String[]{username, domain};
        } else {
            return null;
        }
    }

    private String getMessageDigestHex(String username, String key, String domain) throws NoSuchAlgorithmException {
        String s = username + ":" + domain + ":" + key;
        byte bytes[] = s.getBytes();

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(bytes);
        byte digest[] = messageDigest.digest();

        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}
