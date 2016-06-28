package no.kantega.publishing.admin.administration.action;

import no.kantega.publishing.jobs.alerts.UnusedAttachmentsFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin/administration/unusedAttachments")
public class UnusedAttachmentsAction {

    @Autowired
    private UnusedAttachmentsFinder unusedAttachmentsFinder;

    @RequestMapping(method = RequestMethod.GET)
    public String viewForm(Model model){
        model.addAttribute("unusedAttachments", unusedAttachmentsFinder.getUnusedAttachments());
        return "/WEB-INF/jsp/admin/administration/unusedAttachments.jsp";
    }


}
