package no.kantega.publishing.admin.orgunit.ajax;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class NavigatorAction implements Controller {
    private String view;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<>();
        String openFolders = request.getParameter("openFolders");

        Set<String> openUnits = new TreeSet<String>();
        model.put("openFolders", openFolders);
        if (openFolders != null) {
            String[] tmpOpenUnits = openFolders.split(",");
            openUnits.addAll(Arrays.asList(tmpOpenUnits));
        }
        model.put("openUnits", openUnits);


        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }
}
