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

import java.util.List;

import static java.util.Arrays.asList;

public class MimeTypes {
	private static final MimeType DEFAULT_MIMETYPE = new MimeType("bin", "application/octet-stream", "Ukjent filtype");
	static final List<MimeType> mimetypes = asList(
            // Images
            new MimeType("bmp", "image/bmp", "BMP bilde"),
            new MimeType("jpe", "image/jpeg", "JPG bilde"),
            new MimeType("jpeg", "image/jpeg", "JPG bilde"),
            new MimeType("jpg", "image/jpeg", "JPG bilde"),
            new MimeType("gif", "image/gif", "GIF bilde"),
            new MimeType("png", "image/png", "PNG bilde"),
            new MimeType("tif", "image/tiff", "TIFF bilde"),
            new MimeType("tiff", "image/tiff", "TIFF bilde"),
            new MimeType("svg", "image/svg+xml", "SVG bilde"),

	        // Audio
            new MimeType("mid", "audio/x-midi", "MIDI fil"),
            new MimeType("wav", "audio/x-wav", "WAV fil"),
            new MimeType("mp3", "audio/x-mpeg", "MPEG audio"),
            new MimeType("m4a", "audio/x-mpeg", "MPEG audio 4"),

            // Div
            new MimeType("zip", "application/zip", "ZIP komprimert fil"),
            new MimeType("pdf", "application/pdf", "PDF"),
            new MimeType("rtf", "application/rtf", "Rik tekst"),
            new MimeType("eps", "application/postscript", "Encapsulated postscript"),

            //Adobe
            new MimeType("ai", "application/postscript", "Adobe illustrator fil"),
            new MimeType("psd", "image/psd", "Photoshop bilde"),

            // Video
            new MimeType("wmv", "video/x-ms-wmv", "Microsoft Windows Media File"),
            new MimeType("avi", "video/x-msvideo", "Microsoft Video"),
            new MimeType("mpg", "video/mpeg", "MPEG video"),
            new MimeType("mpe", "video/mpeg", "MPEG video"),
            new MimeType("mpeg", "video/mpeg", "MPEG video"),
            new MimeType("mp4", "video/mpeg", "MPEG video"),
            new MimeType("m4a", "video/mpeg", "MPEG video"),
            new MimeType("mp4v", "video/mpeg", "MPEG video"),
            new MimeType("3gp", "video/mpeg", "MPEG video"),
            new MimeType("3g2", "video/mpeg", "MPEG video"),

            new MimeType("mov", "video/quicktime", "Quicktime"),
            new MimeType("qt", "video/quicktime", "Quicktime"),
            new MimeType("flv", "video/x-flv", "Flash video"),

            // Flash
            new MimeType("swf", "application/x-shockwave-flash", "Flash"),
            new MimeType("swt", "application/x-shockwave-flash", "Flash"),

            // Office
            new MimeType("doc", "application/msword", "MS Word"),
            new MimeType("ppt", "application/vnd.ms-powerpoint", "MS Powerpoint"),
            new MimeType("xls", "application/vnd.ms-excel", "MS Excel"),
            new MimeType("mpp", "vnd.ms-project", "MS Project"),
            new MimeType("mpc", "vnd.ms-project", "MS Project"),
            new MimeType("mpt", "vnd.ms-project", "MS Project"),
            new MimeType("mpx", "vnd.ms-project", "MS Project"),
            new MimeType("mpw", "vnd.ms-project", "MS Project"),

            // Office 2007
            new MimeType("docx", "application/vnd.openxmlformats-officedocument.processingml.document", "MS Word dokument openxml format"),
            new MimeType("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "MS Excel regneark openxml format"),
            new MimeType("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation", "MS Powerpoint presentasjon openxml format"),

            // Open Office
            new MimeType("odt", "application/vnd.oasis.opendocument.text", "Open Office"),
            new MimeType("ott", "application/vnd.oasis.opendocument.text-template", "Open Office template"),
            new MimeType("ods", "application/vnd.oasis.opendocument.spreadsheet", "Open Office Spreadsheet"),
            new MimeType("ots", "application/vnd.oasis.opendocument.spreadsheet-template", "Open Office Spreadsheet template"),
            new MimeType("odp", "application/vnd.oasis.opendocument.presentation", "Open Office Presentation"),
            new MimeType("otp", "application/vnd.oasis.opendocument.presentation-template", "Open Office Presentation template"));

	public static MimeType getMimeType(final String filename) {
		if (filename == null || filename.length() == 0) {
			return DEFAULT_MIMETYPE;
		}

		int inx = filename.lastIndexOf('.');
		final String extension = (inx != -1) ? filename.substring(inx + 1, filename.length()).toLowerCase() : DEFAULT_MIMETYPE.getFileExtension();

		for (MimeType mimetype : mimetypes) {
			if (mimetype.getFileExtension().equals(extension)) {
				return mimetype;
			}
		}

		return new MimeType(extension, "unknown", "uknown");
	}
}
