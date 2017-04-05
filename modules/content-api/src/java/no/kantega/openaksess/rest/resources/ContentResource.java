package no.kantega.openaksess.rest.resources;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.openaksess.rest.domain.Fault;
import no.kantega.openaksess.rest.representation.*;
import no.kantega.publishing.api.attachment.ao.AttachmentAO;
import no.kantega.publishing.api.content.ContentIdHelper;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.cache.TemplateConfigurationCache;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.SortOrder;
import no.kantega.publishing.common.data.TemplateConfiguration;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.service.SecurityService;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.stream.Collectors;

@Path("/content")
@Consumes("application/json")
@Produces("application/json")
public class ContentResource {

    private final ContentIdHelper contentIdHelper;
    private final AttachmentAO attachmentAO;
    @Context
    private HttpServletRequest request;

    @Inject
    public ContentResource(ContentIdHelper contentIdHelper, AttachmentAO attachmentAO) {
        this.contentIdHelper = contentIdHelper;
        this.attachmentAO = attachmentAO;
    }

    @GET
    public List<ContentTransferObject> get(@BeanParam ContentQueryTransferObject contentQuery){
        ContentManagementService cms = new ContentManagementService(request);
        SecuritySession ss = SecuritySession.getInstance(request);
        if(!ss.isUserInRole("admin")){
            contentQuery.setIncludeAllStatuses(false);
        }
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
            return new ContentTransferObject(content, attachmentAO.getAttachmentList(cid));
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
                contentIdHelper.assureAssociationIdSet(cid);
                return new ContentTransferObject(content, attachmentAO.getAttachmentList(cid));
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

    @GET
    @Path("/topicMaps")
    public List<TopicMapTransferObject> getTopicMap(){
        TopicMapService tms = new TopicMapService(request);
        List<TopicMap> maps = tms.getTopicMaps();
        return maps.stream().map(TopicMapTransferObject::new).collect(Collectors.toList());
    }


    @GET
    @Path("/topics")
    public List<TopicTransferObject> getTopics(){
        TopicMapService tms = new TopicMapService(request);
        List<Topic> topics = tms.getAllTopics();
        return topics.stream().map(TopicTransferObject::new).collect(Collectors.toList());
    }

    private List<ContentTransferObject> convertToTransferObject(List<Content> contentList){
        return contentList
                .stream()
                .map((content) -> new ContentTransferObject(content, attachmentAO.getAttachmentList(content.getContentIdentifier())))
                .collect(Collectors.toList());
    }

}
