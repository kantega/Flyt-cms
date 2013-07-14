/*
 * Copyright 2011 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.multimedia;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.exception.InvalidImageFormatException;
import no.kantega.publishing.multimedia.metadata.MultimediaMetadataExtractor;

import java.io.IOException;
import java.util.List;


public class DefaultMultimediaUploadHandler implements MultimediaUploadHandler {
    private ImageEditor imageEditor;
    private List<MultimediaMetadataExtractor> multimediaMetadataExtractors;

    public Multimedia updateMultimediaWithData(Multimedia multimedia, byte[] data, String filename, boolean preserveImageSize) throws InvalidImageFormatException {
        if (filename.length() > 255) {
            filename = filename.substring(filename.length() - 255, filename.length());
        }

        multimedia.setData(data);
        multimedia.setFilename(filename);
        updateMediaNameIfEmpty(multimedia, filename);

        for (MultimediaMetadataExtractor extractor : multimediaMetadataExtractors) {
            if (extractor.supportsMimeType(multimedia.getMimeType().getType())) {
                multimedia = extractor.extractMetadata(multimedia);
            }
        }
        if (!preserveImageSize) {
            multimedia = resizeMultimedia(multimedia);
        }

        return multimedia;
    }

    private void updateMediaNameIfEmpty(Multimedia multimedia, String filename) {
        if (multimedia.getName() == null || multimedia.getName().length() == 0) {
            multimedia.setName(removeFileExtension(filename));
        }
    }

    private String removeFileExtension(String filename) {
        String name;
        if (filename.contains(".")) {
            name = filename.substring(0, filename.lastIndexOf('.'));
        } else {
            name = filename;
        }
        return name;
    }

    private Multimedia resizeMultimedia(Multimedia multimedia) throws InvalidImageFormatException {
        if (multimedia.getMimeType().getType().contains("image") && (Aksess.getMaxMediaWidth() > 0 || Aksess.getMaxMediaHeight() > 0)) {
            if (imageIsLargerThanMaxWidthOrHeight(multimedia)) {
                try {
                    multimedia = imageEditor.resizeMultimedia(multimedia, Aksess.getMaxMediaWidth(), Aksess.getMaxMediaHeight());
                } catch (IOException e) {
                    throw new SystemException("IOException", e);
                }
            }
        }
        return multimedia;
    }

    private boolean imageIsLargerThanMaxWidthOrHeight(Multimedia multimedia) {
        return multimedia.getWidth() > Aksess.getMaxMediaWidth() ||  multimedia.getHeight() > Aksess.getMaxMediaHeight();
    }

    public void setImageEditor(ImageEditor imageEditor) {
        this.imageEditor = imageEditor;
    }

    public void setMultimediaMetadataExtractors(List<MultimediaMetadataExtractor> multimediaMetadataExtractors) {
        this.multimediaMetadataExtractors = multimediaMetadataExtractors;
    }
}
