package no.kantega.openaksess.contentApi.controller;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.openaksess.contentApi.transferObject.ContentQueryTransferObject;
import no.kantega.openaksess.contentApi.transferObject.ContentTransferObject;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.SortOrder;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.content.api.ContentIdHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cms")
public class ContentController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ContentIdHelper contentIdHelper;

    @RequestMapping(value = "/current")
    public @ResponseBody ResponseEntity<ContentTransferObject> getCurrentContent(HttpServletRequest request) {
        String url = request.getHeader("referer");
        try {
            ContentIdentifier contentIdentifier = contentIdHelper.fromUrl(url);
            ContentManagementService cms = new ContentManagementService(request);
            Content content = cms.getContent(contentIdentifier);
            return new ResponseEntity<>(new ContentTransferObject(content, request), HttpStatus.OK);
        } catch (ContentNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NotAuthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<List<ContentTransferObject>> getContentWithContentQuery(HttpServletRequest request, ContentQueryTransferObject queryTransferObject){
        ContentManagementService cms = new ContentManagementService(request);
        List<Content> contentList = cms.getContentList(queryTransferObject.getQuery(), -1, new SortOrder(ContentProperty.TITLE));
        if(!contentList.isEmpty()){
            return new ResponseEntity<>(convertToTransferObject(contentList, request), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<ContentTransferObject> getContentByAssociationId(HttpServletRequest request, @PathVariable("identifier") Integer associationId){
        ContentManagementService cms = new ContentManagementService(request);

        return getContentByIdHelper(cms, associationId, request);
    }

    private List<ContentTransferObject> convertToTransferObject(List<Content> contentList, HttpServletRequest request){
        List<ContentTransferObject> contentTransferObjectList = new ArrayList<>(contentList.size());
        for(Content content : contentList){
            contentTransferObjectList.add(new ContentTransferObject(content, request));
        }
        return contentTransferObjectList;
    }

    private ResponseEntity<ContentTransferObject> getContentByIdHelper(ContentManagementService cms, int associationId, HttpServletRequest request){
        ContentIdentifier cid = new ContentIdentifier();
        cid.setAssociationId(associationId);
        try {
            Content content = cms.getContent(cid);
            if(content != null) {
                return new ResponseEntity<>(new ContentTransferObject(content, request), HttpStatus.OK);
            }
        } catch (NotAuthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
