package no.kantega.publishing.webdav;

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
public class AksessMediaFileResource extends AbstractAksessMultimediaResource implements GetableResource, PropFindableResource {
    private Multimedia file;

    public AksessMediaFileResource(Multimedia media) {
        super(media);
    }

    @Override
    public void sendContent(OutputStream outputStream, Range range, Map<String, String> stringStringMap, String s) throws IOException, NotAuthorizedException, BadRequestException {
        MultimediaAO.streamMultimediaData(file.getId(), new InputStreamHandler(outputStream));
    }

    @Override
    public Long getMaxAgeSeconds(Auth auth) {
        return new Long(60*60*24);
    }

    @Override
    public String getContentType(String s) {
        return file.getMimeType().getType();
    }

    @Override
    public Long getContentLength() {
        return new Long(file.getSize());
    }
}
