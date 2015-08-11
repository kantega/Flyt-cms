package no.kantega.openaksess.rest.resources;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.openaksess.rest.transferObject.ContentQueryTransferObject;
import no.kantega.openaksess.rest.transferObject.ContentTransferObject;
import no.kantega.openaksess.rest.domain.Fault;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.SortOrder;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.content.api.ContentIdHelper;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kristian Myrhaug
 * @since 2015-08-11
 */
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
        ContentQuery test = new ContentQuery();
        ContentManagementService cms = new ContentManagementService(request);
        List<Content> contentList = cms.getContentList(contentQuery.getQuery(), -1, new SortOrder(ContentProperty.TITLE));
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
            return new ContentTransferObject(content, request);
        } catch (ContentNotFoundException e) {
            throw new Fault(404, "Content not found");
        } catch (no.kantega.commons.exception.NotAuthorizedException e) {
            throw new Fault(401, "Unauthorized");
        }
    }

    @GET
    @Path("/{id}")
    public ContentTransferObject getByIdentifier(@PathParam("id") Integer id){
        ContentManagementService cms = new ContentManagementService(request);

        ContentIdentifier cid = new ContentIdentifier();
        cid.setAssociationId(id);

        try{
            Content content = cms.getContent(cid);
            if(content != null){
                return new ContentTransferObject(content, request);
            }
            throw new Fault(404, "Content not found");
        } catch (NotAuthorizedException e) {
            throw new Fault(401, "Not authorized");
        }
    }

    private List<ContentTransferObject> convertToTransferObject(List<Content> contentList){
        List<ContentTransferObject> contentTransferObjectList = new ArrayList<>(contentList.size());
        for(Content content : contentList){
            contentTransferObjectList.add(new ContentTransferObject(content, request));
        }
        return contentTransferObjectList;
    }

}
