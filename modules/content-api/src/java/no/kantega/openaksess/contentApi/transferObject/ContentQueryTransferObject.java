package no.kantega.openaksess.contentApi.transferObject;

import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.AssociationCategory;
import no.kantega.publishing.common.data.ContentQuery;

public class ContentQueryTransferObject {
    private ContentQuery query;

    public ContentQueryTransferObject() {
        this.query = new ContentQuery();
    }

    public void setDisplayTemplate(int displayTemplate) {
        query.setDisplayTemplate(displayTemplate);
    }

    public void setContentTemplate(int contentTemplate) {
        query.setContentTemplate(contentTemplate);
    }

    public void setAssociationCategory(int associationCategoryId) {
        query.setAssociationCategory(new AssociationCategory(associationCategoryId));
    }

    public void setCreator(String creator) {
        query.setCreator(creator);
    }

    public void setSql(String sql){
        query.setSql(sql);
    }

    public void setParent(int parentId){
        ContentIdentifier cid = new ContentIdentifier();
        cid.setAssociationId(parentId);
        query.setAssociatedId(cid);
    }

    public ContentQuery getQuery() {
        return query;
    }

}
