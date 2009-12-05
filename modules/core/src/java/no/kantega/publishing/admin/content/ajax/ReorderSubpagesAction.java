package no.kantega.publishing.admin.content.ajax;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.AssociationCategory;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.cache.AssociationCategoryCache;


public class ReorderSubpagesAction implements Controller {


    
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters != null && parameters.size() > 0) {
            for (String param : parameters.keySet()) {
                if (param.startsWith("associationCategory")) {
                    int categoryId = Integer.parseInt(param.substring("associationCategory".length(), param.length()));
                    AssociationCategory category = AssociationCategoryCache.getAssociationCategoryById(categoryId);
                    String associationIds = parameters.get(param)[0];

                    List<Association> associations = new ArrayList<Association>();
                    StringTokenizer tokens = new StringTokenizer(associationIds, ",");
                    int i = 0;
                    while (tokens.hasMoreTokens()) {
                        i++;
                        String tmp = tokens.nextToken();
                        int uniqueId = Integer.parseInt(tmp);
                        Association association = new Association();
                        association.setCategory(category);
                        association.setId(uniqueId);
                        association.setPriority(i);
                        associations.add(association);
                    }
                    new ContentManagementService(request).setAssociationsPriority(associations);

                }
            }
        }

        return null;
    }
}
