package no.kantega.publishing.common.util;

import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.Aksess;
import no.kantega.commons.media.ImageInfo;
import no.kantega.commons.media.MimeType;
import no.kantega.commons.media.MimeTypes;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.ArrayList;

/**
 *
 */
public class MultimediaHelper {
    public static void updateMediaDimensions(Multimedia m) {
        MimeType mimeType = MimeTypes.getMimeType(m.getFilename());

        if (mimeType.getType().indexOf("image") != -1 || mimeType.getType().indexOf("flash") != -1) {
            // For images and Flash we can find the dimensions
            ImageInfo ii = new ImageInfo();
            ii.setInput(new ByteArrayInputStream(m.getData()));
            if (ii.check()) {
                m.setWidth(ii.getWidth());
                m.setHeight(ii.getHeight());
            }
        } else if (mimeType.isDimensionRequired() && (m.getWidth() <= 0 || m.getHeight() <= 0)) {
            m.setWidth(Aksess.getDefaultMediaWidth());
            m.setHeight(Aksess.getDefaultMediaHeight());
        }
    }

    public static List<Integer> getMultimediaIdsFromText(String text) {
        List<Integer> ids = new ArrayList<Integer>();
        if (text != null) {
            ids.addAll(findUsagesInText(text, "multimedia.ap?id="));
            ids.addAll(findUsagesInText(text, "/multimedia/"));
        }
        return ids;
    }


    private static List<Integer> findUsagesInText(String value, String key) {
        List<Integer> ids = new ArrayList<Integer>();

        int foundPos = value.indexOf(key);
        while (foundPos != -1) {
            value = value.substring(foundPos + key.length(), value.length());

            int endPos = 0;
            char c = value.charAt(endPos);
            while (c >= '0' && c <= '9') {
                ++endPos;
                if (endPos < value.length()) {
                    c = value.charAt(endPos);
                } else {
                    break;
                }
            }
            String id = value.substring(0, endPos);

            try {
                int multimediaId = Integer.parseInt(id);
                ids.add(multimediaId);
            } catch (NumberFormatException e) {
                // Error in URL
            }

            // Find next
            foundPos = value.indexOf(key, foundPos);
        }

        return ids;
    }

    public static void updateMultimediaFromData(Multimedia mm, byte[] data, String filename) {
        mm.setData(data);

        MimeType mimeType = MimeTypes.getMimeType(filename);
        if (mimeType.getType().indexOf("image") != -1 || mimeType.getType().indexOf("flash") != -1) {
            // Dette er et bilde eller Flash fil, finn st�rrelse
            ImageInfo ii = new ImageInfo();
            ii.setInput(new ByteArrayInputStream(mm.getData()));
            if (ii.check()) {
                mm.setWidth(ii.getWidth());
                mm.setHeight(ii.getHeight());
            }
        } else if (mimeType.isDimensionRequired() && (mm.getWidth() <= 0 || mm.getHeight() <= 0)) {
            mm.setWidth(Aksess.getDefaultMediaWidth());
            mm.setHeight(Aksess.getDefaultMediaHeight());
        }

        if (filename.length() > 255) {
            filename = filename.substring(filename.length() - 255, filename.length());
        }
        mm.setFilename(filename);
    }
}
