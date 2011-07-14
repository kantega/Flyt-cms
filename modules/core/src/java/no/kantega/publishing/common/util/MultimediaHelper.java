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
}
