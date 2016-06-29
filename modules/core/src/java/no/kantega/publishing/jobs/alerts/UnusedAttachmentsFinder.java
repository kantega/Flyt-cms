package no.kantega.publishing.jobs.alerts;

import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.publishing.common.ao.LinkDao;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.modules.linkcheck.check.LinkHandler;
import no.kantega.publishing.modules.linkcheck.check.LinkOccurrence;
import no.kantega.publishing.modules.linkcheck.check.LinkQueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UnusedAttachmentsFinder {

    @Autowired
    private LinkDao linkDao;

    private static final Pattern attachmentPattern = Pattern.compile("(.*/attachment.ap\\?id=(?<apId>\\d+))|(.*/attachment/(?<prettyId>\\d+).*)");

    public List<Attachment> getUnusedAttachments() {
        final List<Integer> referredAttachments = new LinkedList<>();

        linkDao.doForEachLink(
                new LinkQueryGenerator() {
                    @Override
                    public String getQuery() {
                        return "select * from link where url like '%" + Aksess.VAR_WEB + "/attachment%'";
                    }
                },
                new LinkHandler() {
                    public void handleLink(int id, String link, LinkOccurrence occurrence) {
                        String attachmentId = getAttachmentId(link);

                        if (attachmentId != null) {
                            referredAttachments.add(Integer.parseInt(attachmentId));

                        }
                    }
                });
        List<Integer> allAttachmentIds = AttachmentAO.getAllAttachmentIds();

        allAttachmentIds.removeAll(referredAttachments);

        return AttachmentAO.getAttachments(allAttachmentIds);
    }

    private String getAttachmentId(String url) {
        Matcher matcher = attachmentPattern.matcher(url);
        if(matcher.matches()) {
            String attachmentId = matcher.group("apId");
            if (attachmentId == null) {
                attachmentId = matcher.group("prettyId");
            }
            return attachmentId;
        } else {
            return null;
        }
    }
}
