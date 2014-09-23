package no.kantega.publishing.admin.content.ajax;

import no.kantega.publishing.common.ao.LinkDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CountBrokenLinksAction {

    @Autowired
    private LinkDao linkDao;

    @ResponseBody
    @RequestMapping(value = "/admin/publish/CountBrokenLinks.action", method = RequestMethod.GET)
    public int getBrokenLinkCount() {
        return linkDao.getAllBrokenLinks("").size();
    }
}
