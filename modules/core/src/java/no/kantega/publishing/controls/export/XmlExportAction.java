package no.kantega.publishing.controls.export;


import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.service.ContentManagementService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlExportAction implements Controller {

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters params = new RequestParameters(request);
        int associationId = params.getInt(AdminRequestParameters.THIS_ID);
        String view = "/WEB-INF/jsp/export/xmlexport.jsp";
        Content page = null;
        Map<String, Object> model = new HashMap<String, Object>();
        if (associationId > -1) {
            ContentManagementService cms = new ContentManagementService(request);
            ContentIdentifier cid = new ContentIdentifier();
            cid.setAssociationId(associationId);
            page = cms.getContent(cid);
            if (page != null) {
                List<Attribute> attributes = page.getAttributes(AttributeDataType.CONTENT_DATA);
                StringBuilder builder = new StringBuilder();
                for (Attribute attr : attributes) {
                    builder.append(attr.getXMLAttributeValueExporter().getAttributeValueAsXMLFragment(attr));
                }
                model.put("xml", builder.toString());
                model.put("page", page);
            }
        }

        if (page == null) {
            request.getRequestDispatcher("/404.jsp").forward(request, response);
        }

        return new ModelAndView(view, model);
    }
}
