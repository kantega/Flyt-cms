package no.kantega.publishing.api.taglibs.media;

import no.kantega.commons.util.StringHelper;
import no.kantega.publishing.common.MultimediaComparator;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaProperty;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.service.MultimediaService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.LoopTagSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static no.kantega.publishing.api.ContentUtil.tryGetFromPageContext;

public class GetMediaCollectionTag  extends LoopTagSupport {
    private int folder = -1;
    private Iterator i;
    private String medialist;
    private boolean imagesonly = false;
    private String orderby = MultimediaProperty.PUBLISH_DATE;
    private boolean descending = false;

    protected Object next() throws JspTagException {
        if(i != null) {
            return i.next();
        }
        return null;
    }

    protected boolean hasNext() throws JspTagException {
        if(i == null){
            return false;
        }
        return i.hasNext();
    }

    protected void prepare() throws JspTagException {
        MultimediaService mediaService = new MultimediaService((HttpServletRequest)pageContext.getRequest());

        List<Multimedia> images = new ArrayList<>();
        if (folder != -1) {
            // Get objects from a folder
            List<Multimedia> objects = mediaService.getMultimediaList(folder);
            for (Multimedia obj : objects) {
                if (obj.getType() == MultimediaType.MEDIA) {
                    if (!imagesonly || obj.getMimeType().getType().contains("image")) {
                        images.add(obj);
                    }
                }
            }
        } else if (medialist != null && medialist.length() > 0) {
            // Get objects from a list
            String ids = null;
            if (medialist.charAt(0) >= '0' && medialist.charAt(0) <= '9') {
                // Interpret as a list with ids (numbers)
                ids = medialist;
            } else {
                // Interpret as attribute name that contains list with ids
                Content content = tryGetFromPageContext(pageContext);
                if (content != null) {
                    ids = content.getAttributeValue(medialist);
                }
            }

            if (ids != null && ids.length() > 0) {
                int mediaIds[] = StringHelper.getInts(ids, ",");
                for (int mediaId : mediaIds) {
                    Multimedia m = mediaService.getMultimedia(mediaId);
                    if (m != null && m.getType() == MultimediaType.MEDIA) {
                        if (!imagesonly || m.getMimeType().getType().contains("image")) {
                            images.add(m);
                        }
                    }
                }
            }
        }

        MultimediaComparator comparator = new MultimediaComparator(orderby, descending);
        Collections.sort(images, comparator);
        i = images.iterator();

        folder = -1;
        medialist = null;
        imagesonly = false;
        orderby = MultimediaProperty.PUBLISH_DATE;
        descending = false;
    }

    public void setFolder(int folder) {
        this.folder = folder;
    }

    public void setMedialist(String medialist) {
        this.medialist = medialist;
    }

    public void setImagesonly(boolean imagesonly) {
        this.imagesonly = imagesonly;
    }

    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
    }
}
