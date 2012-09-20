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

import java.util.ArrayList;
import java.util.List;

public class MimeTypes {
    private static MimeType DEFAULT_MIMETYPE = new MimeType("bin", "application/octet-stream", "Ukjent filtype");
    static List<MimeType> mimetypes = new ArrayList<MimeType>();
    static {
        // Audio
        mimetypes.add(new MimeType("mid", "audio/x-midi", "MIDI fil"));
        mimetypes.add(new MimeType("wav", "audio/x-wav", "WAV fil"));
        mimetypes.add(new MimeType("mp3", "audio/x-mpeg", "MPEG audio"));
        mimetypes.add(new MimeType("m4a", "audio/x-mpeg", "MPEG audio 4"));

        // Div
        mimetypes.add(new MimeType("zip", "application/zip", "ZIP komprimert fil"));
        mimetypes.add(new MimeType("pdf", "application/pdf", "PDF"));
        mimetypes.add(new MimeType("rtf", "application/rtf", "Rik tekst"));

        // Images
        mimetypes.add(new MimeType("bmp", "image/bmp", "BMP bilde"));
        mimetypes.add(new MimeType("jpe", "image/jpeg", "JPG bilde"));
        mimetypes.add(new MimeType("jpeg", "image/jpeg", "JPG bilde"));
        mimetypes.add(new MimeType("jpg", "image/jpeg", "JPG bilde"));
        mimetypes.add(new MimeType("gif", "image/gif", "GIF bilde"));
        mimetypes.add(new MimeType("png", "image/png", "PNG bilde"));
        mimetypes.add(new MimeType("psd", "image/psd", "PhotoShop bilde"));
        mimetypes.add(new MimeType("tif", "image/tiff", "TIFF bilde"));
        mimetypes.add(new MimeType("tiff", "image/tiff", "TIFF bilde"));
        mimetypes.add(new MimeType("svg", "image/svg+xml", "SVG bilde"));

        // Video
        mimetypes.add(new MimeType("wmv", "video/x-ms-wmv", "Microsoft Windows Media File"));
        mimetypes.add(new MimeType("avi", "video/x-msvideo", "Microsoft Video"));
        mimetypes.add(new MimeType("mpg", "video/mpeg", "MPEG video"));
        mimetypes.add(new MimeType("mpe", "video/mpeg", "MPEG video"));
        mimetypes.add(new MimeType("mpeg", "video/mpeg", "MPEG video"));
        mimetypes.add(new MimeType("mp4", "video/mpeg", "MPEG video"));
        mimetypes.add(new MimeType("m4a", "video/mpeg", "MPEG video"));
        mimetypes.add(new MimeType("mp4v", "video/mpeg", "MPEG video"));
        mimetypes.add(new MimeType("3gp", "video/mpeg", "MPEG video"));
        mimetypes.add(new MimeType("3g2", "video/mpeg", "MPEG video"));
        
        mimetypes.add(new MimeType("mov", "video/quicktime", "Quicktime"));
        mimetypes.add(new MimeType("qt", "video/quicktime", "Quicktime"));
        mimetypes.add(new MimeType("flv", "video/x-flv", "Flash video"));

        // Flash
        mimetypes.add(new MimeType("swf", "application/x-shockwave-flash", "Flash"));
        mimetypes.add(new MimeType("swt", "application/x-shockwave-flash", "Flash"));

        // Office
        mimetypes.add(new MimeType("doc", "application/msword", "MS Word"));
        mimetypes.add(new MimeType("ppt", "application/vnd.ms-powerpoint", "MS Powerpoint"));
        mimetypes.add(new MimeType("xls", "application/vnd.ms-excel" ,"MS Excel"));
        mimetypes.add(new MimeType("mpp", "vnd.ms-project", "MS Project"));
        mimetypes.add(new MimeType("mpc", "vnd.ms-project", "MS Project"));
        mimetypes.add(new MimeType("mpt", "vnd.ms-project", "MS Project"));
        mimetypes.add(new MimeType("mpx", "vnd.ms-project", "MS Project"));
        mimetypes.add(new MimeType("mpw", "vnd.ms-project", "MS Project"));

        // Office 2007
        mimetypes.add(new MimeType("docx", "application/vnd.openxmlformats-officedocument.processingml.document", "MS Word dokument openxml format"));
        mimetypes.add(new MimeType("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "MS Excel regneark openxml format"));
        mimetypes.add(new MimeType("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation", "MS Powerpoint presentasjon openxml format"));

        // Open Office
        mimetypes.add(new MimeType("odt", "application/vnd.oasis.opendocument.text", "Open Office"));
        mimetypes.add(new MimeType("ott", "application/vnd.oasis.opendocument.text-template", "Open Office template"));
        mimetypes.add(new MimeType("ods", "application/vnd.oasis.opendocument.spreadsheet", "Open Office Spreadsheet"));
        mimetypes.add(new MimeType("ots", "application/vnd.oasis.opendocument.spreadsheet-template", "Open Office Spreadsheet template"));
        mimetypes.add(new MimeType("odp", "application/vnd.oasis.opendocument.presentation", "Open Office Presentation"));
        mimetypes.add(new MimeType("otp", "application/vnd.oasis.opendocument.presentation-template", "Open Office Presentation template"));
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
        for (MimeType mimetype : mimetypes) {
            if (mimetype.getFileExtension().equals(fileext)) {
                return mimetype;
            }
        }

        return DEFAULT_MIMETYPE;
    }
}
