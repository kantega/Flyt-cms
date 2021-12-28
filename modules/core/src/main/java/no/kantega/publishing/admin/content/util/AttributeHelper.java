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

package no.kantega.publishing.admin.content.util;

import no.kantega.publishing.api.attachment.ao.AttachmentAO;
import no.kantega.publishing.api.content.ContentAO;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.multimedia.MultimediaAO;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Multimedia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static no.kantega.publishing.common.Aksess.VAR_WEB;

public class AttributeHelper {
    private static final Pattern ILLEGAL_PATTERN = Pattern.compile("[^a-zA-Z0-9\\$]");


    public static String getInputFieldName(String name) {
        name = name.replace(".", "_dot_");
        return "attributeValue_" + ILLEGAL_PATTERN.matcher(name).replaceAll("_");
    }

    public static String getInputContainerName(String name) {
        name = name.replace(".", "_dot_");
        return "contentAttribute_" + ILLEGAL_PATTERN.matcher(name).replaceAll("_");

    }

    private static final Pattern multimediaPattern = Pattern.compile("(<@WEB@>/multimedia.ap\\?id=(\\d+))");
    private static final Pattern attachmentsPattern = Pattern.compile("(<@WEB@>/attachment.ap\\?id=(\\d+))");
    private static final Pattern contentThisIdPattern = Pattern.compile("(<@WEB@>/content.ap\\?thisId=(\\d+))");
    private static final Pattern contentContentIdPattern = Pattern.compile("(<@WEB@>/content.ap\\?contentId=(\\d+))&(amp;)?contextId=\\$contextId\\$");

    public static String replaceApUrls(String value, ContentAO contentAO, AttachmentAO attachmentAO, MultimediaAO multimediaAO) {
        if(value == null) return null;
        String result = attachments(value, attachmentAO);
        result = multimedia(result, multimediaAO);
        result = contentThis(result, contentAO);
        result = contentId(result, contentAO);
        return result;
    }


    private static String attachments(String value, AttachmentAO attachmentAo) {
        Matcher matcher = attachmentsPattern.matcher(value);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String id = matcher.group(2);
            Attachment attachment = attachmentAo.getAttachment(Integer.parseInt(id));
            if (attachment != null) {
                matcher.appendReplacement(buffer, VAR_WEB + attachment.getPath());
            } else {
                matcher.appendReplacement(buffer, matcher.group(1));
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String multimedia(String value, MultimediaAO multimediaAO) {
        Matcher matcher = multimediaPattern.matcher(value);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String id = matcher.group(2);
            Multimedia multimedia = multimediaAO.getMultimedia(Integer.parseInt(id));
            if (multimedia != null) {
                matcher.appendReplacement(buffer, VAR_WEB + multimedia.getPath());
            } else {
                matcher.appendReplacement(buffer, matcher.group(1));
            }

        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String contentThis(String value, ContentAO cms) {
        Matcher matcher = contentThisIdPattern.matcher(value);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String id = matcher.group(2);
            Content content = cms.getContent(ContentIdentifier.fromAssociationId(Integer.parseInt(id)), true);
            if (content != null) {
                matcher.appendReplacement(buffer, VAR_WEB + content.getPath());
            } else {
                matcher.appendReplacement(buffer, matcher.group(1));
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String contentId(String value, ContentAO cms) {
        Matcher matcher = contentContentIdPattern.matcher(value);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String id = matcher.group(2);
            Content content = cms.getContent(ContentIdentifier.fromContentId(Integer.parseInt(id)), true);
            if (content != null) {
                matcher.appendReplacement(buffer, VAR_WEB + content.getPath());
            } else {
                matcher.appendReplacement(buffer, matcher.group(1));
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
