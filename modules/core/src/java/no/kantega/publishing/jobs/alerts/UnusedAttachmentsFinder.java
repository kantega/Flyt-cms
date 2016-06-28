package no.kantega.publishing.jobs.alerts;

import no.kantega.publishing.api.attachment.ao.AttachmentAO;
import no.kantega.publishing.api.link.LinkDao;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Attachment;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;

public class UnusedAttachmentsFinder {

    @Autowired
    private AttachmentAO attachmentAO;

    @Autowired
    private LinkDao linkDao;

    private static final Pattern attachmentPattern = Pattern.compile("(.*/attachment.ap?id=(<apId>\\d+))|(.*/attachment/(<prettyId>\\d+).*)");

    public List<Attachment> getUnusedAttachments() {
        List<Integer> referredAttachments = new LinkedList<>();

        linkDao.doForEachLink(
                () -> "select * from link where url like '%" + Aksess.VAR_WEB + "/attachment%'",
                (linkId, url, occurrence) -> {
                    String attachmentId = getAttachmentId(url);

                    if(nonNull(attachmentId)) {
                        referredAttachments.add(Integer.parseInt(attachmentId));

                    }
                });
        List<Integer> allAttachmentIds = attachmentAO.getAllAttachmentIds();

        allAttachmentIds.removeAll(referredAttachments);

        return attachmentAO.getAttachments(allAttachmentIds);
    }

    private String getAttachmentId(String url) {
        Matcher matcher = attachmentPattern.matcher(url);
        String attachmentId = matcher.group("apId");
        if(attachmentId == null) {
            attachmentId = matcher.group("prettyId");
        }
        return attachmentId;
    }
}
