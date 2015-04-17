package no.kantega.publishing.admin.content.ajax;

import no.kantega.publishing.common.cache.AssociationCategoryCache;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.AssociationCategory;
import no.kantega.publishing.common.service.ContentManagementService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Called with ajax when the user has reordered subpages in the Organize sub pages view.
 */
public class ReorderSubpagesAction implements Controller {


    /**
     * The controller expects the data to be sent as request parameters on the following format:
     * ?associationCategory[categoryId]=[association1],[association2],[association3]&associationCategory[categoryId]=[association1],[association2]
     *
     * Example:
     * ?associationCategory1=39,54,3465&associationCategory4=545,274,23423,673,324
     *
     * @see Controller#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse) 
     */
    @SuppressWarnings("unchecked")
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
