package no.kantega.publishing.admin.mypage.plugins;

import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.User;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class UserInfoAction  implements Controller {

    private String view;
    private String gravatarUrl;
    private String defaultIconUrl;


    /**
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<>();

        SecuritySession securitySession =  SecuritySession.getInstance(request);
        User user = securitySession.getUser();
        model.put("currentUser", user);
        if (user != null && user.getEmail() != null && user.getEmail().length() > 0) {
            String hash = new MD5Util().md5Hex(user.getEmail());
            String url = gravatarUrl + hash + ".jpg?d=" + defaultIconUrl;
            model.put("currentUserImageUrl", url);
        }

        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }

    public void setGravatarUrl(String gravatarUrl) {
        this.gravatarUrl = gravatarUrl;
    }

    public void setDefaultIconUrl(String defaultIconUrl) {
        this.defaultIconUrl = defaultIconUrl;
    }

    class MD5Util {
        public String hex(byte[] array) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        }

        public String md5Hex (String message) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                return hex (md.digest(message.getBytes("CP1252")));
            } catch (NoSuchAlgorithmException e) {
            } catch (UnsupportedEncodingException e) {
            }
            return null;
        }

    }
}

