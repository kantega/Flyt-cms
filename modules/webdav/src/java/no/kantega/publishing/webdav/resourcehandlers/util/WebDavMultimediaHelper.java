package no.kantega.publishing.webdav.resourcehandlers.util;

import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.Resource;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.MultimediaAO;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.exception.InvalidImageFormatException;
import no.kantega.publishing.multimedia.ImageEditor;
import no.kantega.publishing.multimedia.metadata.MultimediaMetadataExtractor;
import no.kantega.publishing.webdav.resources.AksessMediaFileResource;
import no.kantega.publishing.webdav.resources.AksessMediaFolderResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 *
 */
public class WebDavMultimediaHelper {
    private static final Logger log = LoggerFactory.getLogger(WebDavMultimediaHelper.class);
    private WebDavSecurityHelper webDavSecurityHelper;
    private ImageEditor imageEditor;
    private List<MultimediaMetadataExtractor> multimediaMetadataExtractors;


    public Resource getRootFolder() {
        Multimedia media = new Multimedia();
        media.setId(0);
        media.setName("multimedia");
        return  new AksessMediaFolderResource(media, webDavSecurityHelper, this);
    }

    public void setImageEditor(ImageEditor imageEditor) {
        this.imageEditor = imageEditor;
    }

    public void setMultimediaMetadataExtractors(List<MultimediaMetadataExtractor> multimediaMetadataExtractors) {
        this.multimediaMetadataExtractors = multimediaMetadataExtractors;
    }

    public Resource createNewFile(Multimedia parent, String fileName, InputStream inputStream, Long length) throws IOException {
        String name = fileName.substring(0, fileName.lastIndexOf('.'));

        Multimedia file = MultimediaAO.getMultimediaByParentIdAndName(parent.getId(), name);
        if (file == null) {
            // File does not exists
            file = new Multimedia();
            file.setType(MultimediaType.MEDIA);
            file.setParentId(parent.getId());
        } else {
            if (file.getType() == MultimediaType.FOLDER) {
                // Exists already as folder
                return null;
            }
        }
        // Fill data from inputstream
        file.setFilename(fileName);

        byte[] data = readBytes(inputStream, length.intValue());
        if (data == null) {
            return null;
        }
        file.setData(data);
        file.setName(name);

        for (MultimediaMetadataExtractor extractor : multimediaMetadataExtractors) {
            if (extractor.supportsMimeType(file.getMimeType().getType())) {
                file = extractor.extractMetadata(file);
            }
        }

        // Resize large images
        if (file.getMimeType().getType().indexOf("image") != -1 && (Aksess.getMaxMediaWidth() > 0 || Aksess.getMaxMediaHeight() > 0)) {
            if (file.getWidth() > Aksess.getMaxMediaWidth() ||  file.getHeight() > Aksess.getMaxMediaHeight()) {
                try {
                    file = imageEditor.resizeMultimedia(file, Aksess.getMaxMediaWidth(), Aksess.getMaxMediaHeight());
                } catch (InvalidImageFormatException e) {
                    throw new IOException();
                }
            }
        }

        // Save new file
        log.debug( "Saved new file:" + fileName + ", size:" + file.getSize());

        int id = MultimediaAO.setMultimedia(file);
        file = MultimediaAO.getMultimedia(id);

        return new AksessMediaFileResource(file, webDavSecurityHelper, this);
    }

    public CollectionResource createNewFolder(Multimedia parent, String folderName) {
        Multimedia folder = new Multimedia();
        folder.setType(MultimediaType.FOLDER);
        folder.setParentId(parent.getId());
        folder.setName(folderName);

        // Save new folder
        int id = MultimediaAO.setMultimedia(folder);
        folder = MultimediaAO.getMultimedia(id);

        return new AksessMediaFolderResource(folder, webDavSecurityHelper, this);
    }

    public Multimedia getMultimediaByPath(String path) {
        Multimedia media = null;

        int parentId = 0;

        String pathElements[] = path.split("/");
        for (int i = 0; i < pathElements.length; i++) {
            String pathElement = pathElements[i];
            if (pathElement.length() > 0) {
                if (pathElement.contains(".")) {
                    // Must remove fileextension before search
                    pathElement = pathElement.substring(0, pathElement.indexOf("."));
                }
                // Find child with name
                media = MultimediaAO.getMultimediaByParentIdAndName(parentId, pathElement);

                if (media == null) {
                    return null;
                }
                parentId = media.getId();
            }
        }

        return media;
    }

    public List<Multimedia> getMultimediaList(int parentId) {
        return MultimediaAO.getMultimediaList(parentId);
    }

    private byte[] readBytes(InputStream inputStream, int length) throws IOException {
        int bytesRead = 0;
        byte[] data = new byte[length];
        while (bytesRead < length) {
            int result = inputStream.read(data, bytesRead, length - bytesRead);
            if (result == -1) break;
            bytesRead += result;
        }
        if (bytesRead != length) {
            log.error( "read bytes != length");
            return null;
        }
        return data;
    }

    public void setWebDavSecurityHelper(WebDavSecurityHelper webDavSecurityHelper) {
        this.webDavSecurityHelper = webDavSecurityHelper;
    }
}
