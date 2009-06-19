/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.commons.media;

import java.util.Vector;

public class MimeTypes {
    private static MimeType DEFAULT_MIMETYPE = new MimeType("bin", "application/octet-stream", "Ukjent filtype");
    static Vector mimetypes = new Vector();
    static {
        // Audio
        mimetypes.add(new MimeType("mid", "audio/x-midi", "MIDI fil"));
        mimetypes.add(new MimeType("wav", "audio/x-wav", "WAV fil"));
        mimetypes.add(new MimeType("ram", "audio/x-pn-realaudio", "RealAudio fil"));
        mimetypes.add(new MimeType("rpm", "audio/x-pn-realaudio-plugin", "RealAudio fil"));
        mimetypes.add(new MimeType("rm", "audio/x-pn-realaudio", "RealAudio fil"));
        mimetypes.add(new MimeType("mp3", "audio/x-mpeg", "MPEG audio"));

        // Div
        mimetypes.add(new MimeType("zip", "application/zip", "ZIP komprimert fil"));
        mimetypes.add(new MimeType("pdf", "application/pdf", "Adobe Acrobat"));
        mimetypes.add(new MimeType("rtf", "application/rtf", "Rik tekst"));

        // Images
        mimetypes.add(new MimeType("bmp", "image/bmp", "BMP bilde", true));
        mimetypes.add(new MimeType("jpe", "image/jpeg", "JPG bilde"));
        mimetypes.add(new MimeType("jpeg", "image/jpeg", "JPG bilde"));
        mimetypes.add(new MimeType("jpg", "image/jpeg", "JPG bilde"));
        mimetypes.add(new MimeType("gif", "image/gif", "GIF bilde"));
        mimetypes.add(new MimeType("png", "image/png", "PNG bilde"));
        mimetypes.add(new MimeType("psd", "image/psd", "PhotoShop bilde"));
        mimetypes.add(new MimeType("tif", "image/tiff", "TIFF bilde", true));
        mimetypes.add(new MimeType("tiff", "image/tiff", "TIFF bilde", true));

        // Video
        mimetypes.add(new MimeType("wmv", "video/x-ms-wmv", "Microsoft Windows Media File"));
        mimetypes.add(new MimeType("avi", "video/x-msvideo", "Microsoft Video"));
        mimetypes.add(new MimeType("mpg", "video/mpeg", "MPEG video"));
        mimetypes.add(new MimeType("mpe", "video/mpeg", "MPEG video"));
        mimetypes.add(new MimeType("mpeg", "video/mpeg", "MPEG video"));
        mimetypes.add(new MimeType("mp4", "video/mpeg", "MPEG video"));
        mimetypes.add(new MimeType("mov", "video/quicktime", "Quicktime"));
        mimetypes.add(new MimeType("qt", "video/quicktime", "Quicktime"));
        mimetypes.add(new MimeType("flv", "video/x-flv", "Flash video"));

        // Flash
        mimetypes.add(new MimeType("swf", "application/x-shockwave-flash", "Flash fil"));
        mimetypes.add(new MimeType("swt", "application/x-shockwave-flash", "Flash fil"));

        // Office
        mimetypes.add(new MimeType("doc", "application/msword", "MS Word dokument"));
        mimetypes.add(new MimeType("ppt", "application/vnd.ms-powerpoint", "MS Powerpoint presentasjon"));
        mimetypes.add(new MimeType("xls", "application/vnd.ms-excel" ,"MS Excel regneark"));
        mimetypes.add(new MimeType("mpp", "vnd.ms-project", "MS Project"));
        mimetypes.add(new MimeType("mpc", "vnd.ms-project", "MS Project"));
        mimetypes.add(new MimeType("mpt", "vnd.ms-project", "MS Project"));
        mimetypes.add(new MimeType("mpx", "vnd.ms-project", "MS Project"));
        mimetypes.add(new MimeType("mpw", "vnd.ms-project", "MS Project"));

        // Office 2007
        mimetypes.add(new MimeType("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "MS Word dokument openxml format"));
        mimetypes.add(new MimeType("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "MS Excel regneark openxml format"));
        mimetypes.add(new MimeType("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation", "MS Powerpoint presentasjon openxml format"));

    }


    public static MimeType getMimeType(String filename) {
        if (filename == null || filename.length() == 0) {
            return DEFAULT_MIMETYPE;
        }

        int inx = filename.lastIndexOf('.');
        if (inx != -1) {
            filename = filename.substring(inx + 1, filename.length());
        }

        String fileext = filename.toLowerCase();
        for (int i = 0; i < mimetypes.size(); i++) {
            MimeType m = (MimeType)mimetypes.get(i);
            if (m.getFileExtension().equals(fileext)) {
                return m;
            }
        }

        return DEFAULT_MIMETYPE;
    }
}
