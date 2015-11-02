package no.kantega.openaksess.rest.representation;

import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.AssociationCategory;
import no.kantega.publishing.common.data.ContentQuery;

import javax.ws.rs.QueryParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

@XmlRootElement(name = "contentQuery")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ContentQueryTransferObject {
    private ContentQuery query;

    public ContentQueryTransferObject() {
        this.query = new ContentQuery();
    }

    @QueryParam("displayTemplate")
    public void setDisplayTemplate(Integer displayTemplate) {
        if(displayTemplate != null){
            query.setDisplayTemplate(displayTemplate);
        }
    }

    @QueryParam("contentTemplate")
    public void setContentTemplate(Integer contentTemplate) {
        if(contentTemplate != null){
            query.setContentTemplate(contentTemplate);
        }
    }

    @QueryParam("associationCategory")
    public void setAssociationCategory(Integer associationCategoryId) {
        if(associationCategoryId != null){
            query.setAssociationCategory(new AssociationCategory(associationCategoryId));
        }
    }

    @QueryParam("creator")
    public void setCreator(String creator) {
        if(creator != null){
            query.setCreator(creator);
        }
    }

    @QueryParam("parent")
    public void setParent(Integer parentId){
        if(parentId != null){
            ContentIdentifier cid = new ContentIdentifier();
            cid.setAssociationId(parentId);
            query.setAssociatedId(cid);
        }
    }

    @QueryParam("modifiedDate")
    public void setModifiedDate(Long date){
        if(date != null){
            query.setModifiedDate(new Date(date));
        }
    }

    @XmlTransient
    public ContentQuery getQuery() {
        return query;
    }

}
