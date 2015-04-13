package no.kantega.publishing.admin.administration.action;

import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.LinkDao;
import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ResetLinkChecker {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private LinkDao linkDao;

    @RequestMapping(value = "/admin/administration/ResetLinkChecker.action", method = RequestMethod.GET)
    public String view(){
        return "/WEB-INF/jsp/admin/administration/resetLinkChecker.jsp";
    }

    @RequestMapping(value = "/admin/administration/ResetLinkChecker.action", method = RequestMethod.POST)
    public ResponseEntity doResetLinkChecker(HttpServletRequest request){
        SecuritySession securitySession = SecuritySession.getInstance(request);
        if(securitySession.isUserInRole(Aksess.getAdminRole())){
            log.info(securitySession.getUser().getName() + " triggered linkchecker reset");
            linkDao.deleteAllLinks();
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

    }
}
