package no.kantega.openaksess.contentApi.controller;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.SortOrder;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.content.api.ContentIdHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/content-api")
public class ContentController {

    @Autowired
    private ContentIdHelper contentIdHelper;

    @RequestMapping(value = "/current")
    public @ResponseBody ResponseEntity<Content> getCurrentContent(HttpServletRequest request, @RequestParam("url") String url) throws ContentNotFoundException, NotAuthorizedException {
        ContentIdentifier contentIdentifier = contentIdHelper.fromUrl(url);
        ContentManagementService cms = new ContentManagementService(request);
        Content content = cms.getContent(contentIdentifier);

        return new ResponseEntity<>(content, HttpStatus.OK);
    }

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<List<Content>> getContentWithContentQuery(HttpServletRequest request, ContentQueryTransferObject queryTransferObject){
        ContentManagementService cms = new ContentManagementService(request);
        List<Content> contentList = cms.getContentList(queryTransferObject.getQuery(), -1, new SortOrder(ContentProperty.TITLE));
        if(!contentList.isEmpty()){
            return new ResponseEntity<>(contentList, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/query", method = RequestMethod.GET, params = "associationId")
    public @ResponseBody ResponseEntity<Content> getContentByAssociationId(HttpServletRequest request, @RequestParam("associationId") Integer associationId){
        ContentManagementService cms = new ContentManagementService(request);

        return getContentByIdHelper(cms, associationId);
    }

    private ResponseEntity<Content> getContentByIdHelper(ContentManagementService cms, int associationId){
        ContentIdentifier cid = new ContentIdentifier();
        cid.setAssociationId(associationId);
        try {
            Content content = cms.getContent(cid);
            if(content != null) {
                return new ResponseEntity<>(content, HttpStatus.OK);
            }
        } catch (NotAuthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
