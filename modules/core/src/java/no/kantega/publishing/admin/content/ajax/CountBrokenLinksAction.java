package no.kantega.publishing.admin.content.ajax;

import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.ao.LinkDao;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.content.api.ContentIdHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Controller
public class CountBrokenLinksAction {

    @Autowired
    private LinkDao linkDao;
    @Autowired
    private ContentIdHelper contentIdHelper;

    @ResponseBody
    @RequestMapping(value = "/admin/publish/CountBrokenLinks.action", method = RequestMethod.GET)
    public int getBrokenLinkCount(HttpServletRequest request, @RequestParam(required = false) String url) throws ContentNotFoundException {
        if (isBlank(url)) {
            return linkDao.getAllBrokenLinks("").size();
        } else {
            ContentIdentifier cid = contentIdHelper.fromRequestAndUrl(request, url);
            return linkDao.getBrokenLinksUnderParent(cid, "").size();
        }
    }
}
