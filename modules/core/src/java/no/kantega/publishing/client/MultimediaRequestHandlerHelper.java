package no.kantega.publishing.client;

import no.kantega.commons.log.Log;
import no.kantega.publishing.common.data.ImageResizeParameters;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.exception.InvalidImageFormatException;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.multimedia.ImageEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class MultimediaRequestHandlerHelper {

    @Autowired
    private ImageEditor imageEditor;

    @Cacheable(value = "ImageCache", key = "#calculatedKey")
    public byte[] getResizedMultimediaBytes(String calculatedKey, Multimedia mm, ImageResizeParameters resizeParams, MultimediaService mediaService) throws IOException, InvalidImageFormatException {
        Log.debug("MultimediaRequestHandlerHelper", "Resizing image with key: " + calculatedKey);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mediaService.streamMultimediaData(mm.getId(), new InputStreamHandler(bos));
        mm.setData(bos.toByteArray());

        mm = imageEditor.resizeMultimedia(mm, resizeParams);

        return mm.getData();
    }
}
