package no.kantega.publishing.webdav.resources;

import com.bradmcevoy.http.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.ao.MultimediaAO;

/**
 *
 */
public class AksessMediaFolderResource extends AbstractAksessMultimediaResource implements CollectionResource, PostableResource {

    public AksessMediaFolderResource(Multimedia media) {
        super(media);
    }    

    public Resource child(String s) {
        return null;
    }

    public List<? extends Resource> getChildren() {
        List<Resource> children = new ArrayList<Resource>();

        List<Multimedia> multimedia = MultimediaAO.getMultimediaList(media.getId());
        for (Multimedia m : multimedia) {
            if (m.getType() == MultimediaType.FOLDER) {
                children.add(new AksessMediaFolderResource(m));
            } else {
                children.add(new AksessMediaFileResource(m));
            }

        }
        return children;
    }

    public String processForm(Map<String, String> parameters, Map<String, FileItem> files) throws BadRequestException, NotAuthorizedException {
        Collection<FileItem> fileList = files.values();
        for (FileItem file : fileList) {
             Log.debug(this.getClass().getName(), "Uploaded file: " + file.getName());
        }
        
        return null;
    }

    public void sendContent(OutputStream outputStream, Range range, Map<String, String> stringStringMap, String s) throws IOException, NotAuthorizedException, BadRequestException {

    }

    public Long getMaxAgeSeconds(Auth auth) {
        return null;
    }

    public String getContentType(String s) {
        return null;
    }

    public Long getContentLength() {
        return (long) 0;
    }
}
