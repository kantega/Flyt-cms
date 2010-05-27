package no.kantega.publishing.webdav.resources;

import com.bradmcevoy.http.*;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.bradmcevoy.http.exceptions.BadRequestException;

import java.io.OutputStream;
import java.io.IOException;
import java.util.Map;

import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.ao.MultimediaAO;
import no.kantega.publishing.common.util.InputStreamHandler;

/**
 *
 */
public class AksessMediaFileResource extends AbstractAksessMultimediaResource implements GetableResource {
    public AksessMediaFileResource(Multimedia media) {
        super(media);
    }

    @Override
    public String getName() {
        String filename = media.getFilename();
        if (filename.contains(".")) {
            String fileext = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
            return media.getName() + "." + fileext;
        } else {
            return media.getName();
        }
    }

    @Override
    public void sendContent(OutputStream outputStream, Range range, Map<String, String> stringStringMap, String s) throws IOException, NotAuthorizedException, BadRequestException {
        MultimediaAO.streamMultimediaData(media.getId(), new InputStreamHandler(outputStream));
    }

    @Override
    public Long getMaxAgeSeconds(Auth auth) {
        return new Long(60*60*24);
    }

    @Override
    public String getContentType(String s) {
        return media.getMimeType().getType();
    }

    @Override
    public Long getContentLength() {
        return new Long(media.getSize());
    }
}
