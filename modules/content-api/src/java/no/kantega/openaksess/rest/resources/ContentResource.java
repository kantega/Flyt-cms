package no.kantega.openaksess.rest.resources;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.openaksess.rest.domain.Fault;
import no.kantega.openaksess.rest.representation.ContentQueryTransferObject;
import no.kantega.openaksess.rest.representation.ContentTemplateConfigurationTransferObject;
import no.kantega.openaksess.rest.representation.ContentTemplateTransferObject;
import no.kantega.openaksess.rest.representation.ContentTransferObject;
import no.kantega.publishing.api.content.ContentIdHelper;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.cache.TemplateConfigurationCache;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/content")
@Consumes("application/json")
@Produces("application/json")
public class ContentResource {

    private ContentIdHelper contentIdHelper;
    @Context
    private HttpServletRequest request;

    @Inject
    public ContentResource(ContentIdHelper contentIdHelper) {
        this.contentIdHelper = contentIdHelper;
    }

    @GET
    public List<ContentTransferObject> get(@BeanParam ContentQueryTransferObject contentQuery){
        ContentManagementService cms = new ContentManagementService(request);
        ContentQuery query = contentQuery.getQuery();
        query.setSortOrder(new SortOrder(ContentProperty.TITLE));
        List<Content> contentList = cms.getContentList(query);
        if(!contentList.isEmpty()){
            return convertToTransferObject(contentList);
        }

        throw new Fault(404, "Content not found");
    }

    @GET
    @Path("/current")
    public ContentTransferObject getByReferrer() {
        String url = request.getHeader("referer");
        try{
            ContentIdentifier cid = contentIdHelper.fromUrl(url);
            ContentManagementService cms = new ContentManagementService(request);
            Content content = cms.getContent(cid);
            return new ContentTransferObject(content);
        } catch (ContentNotFoundException e) {
            throw new Fault(404, "Content not found", null, false, false);
        } catch (no.kantega.commons.exception.NotAuthorizedException e) {
            throw new Fault(401, "Unauthorized");
        }
    }

    @GET
    @Path("/{id}")
    public ContentTransferObject getByIdentifier(@PathParam("id") Integer id){
        ContentManagementService cms = new ContentManagementService(request);

        ContentIdentifier cid = ContentIdentifier.fromAssociationId(id);

        try{
            Content content = cms.getContent(cid);
            if(content != null){
                return new ContentTransferObject(content);
            }
            throw new Fault(404, "Content not found", null, false, false);
        } catch (NotAuthorizedException e) {
            throw new Fault(401, "Not authorized");
        }
    }

    @GET
    @Path("/templatesConfig")
    public ContentTemplateConfigurationTransferObject getAllTemplates(){
        TemplateConfigurationCache instance = TemplateConfigurationCache.getInstance();
        TemplateConfiguration templateConfiguration = instance.getTemplateConfiguration();

        return new ContentTemplateConfigurationTransferObject(templateConfiguration);
    }

    private List<ContentTransferObject> convertToTransferObject(List<Content> contentList){
        return contentList
                .stream()
                .map(ContentTransferObject::new)
                .collect(Collectors.toList());
    }

}
