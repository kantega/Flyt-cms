package no.kantega.publishing.client;

import no.kantega.publishing.common.data.ImageResizeParameters;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.exception.InvalidImageFormatException;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.multimedia.ImageEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class MultimediaRequestHandlerHelper {
    private static final Logger log = LoggerFactory.getLogger(MultimediaRequestHandlerHelper.class);

    @Autowired
    private ImageEditor imageEditor;

    @Cacheable(value = "ImageCache", key = "#calculatedKey")
    public byte[] getResizedMultimediaBytes(String calculatedKey, Multimedia mm, ImageResizeParameters resizeParams, MultimediaService mediaService) throws IOException, InvalidImageFormatException {
        log.debug("Resizing image with key: {}", calculatedKey);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mediaService.streamMultimediaData(mm.getId(), new InputStreamHandler(bos));
        Multimedia resized = new Multimedia();
        resized.setType(mm.getType());
        resized.setFilename(mm.getFilename());
        resized.setData(bos.toByteArray());

        resized = imageEditor.resizeMultimedia(resized, resizeParams);

        return resized.getData();
    }
}
