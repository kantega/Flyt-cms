package no.kantega.publishing.jobs.alerts;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.api.attachment.ao.AttachmentAO;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.link.LinkDao;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.modules.linkcheck.check.LinkOccurrence;
import no.kantega.publishing.modules.linkcheck.check.LinkQueryGenerator;
import no.kantega.publishing.modules.linkcheck.crawl.LinkEmitter;
import no.kantega.publishing.security.SecuritySession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class UnusedAttachmentsFinder {

    @Autowired
    private AttachmentAO attachmentAO;

    @Autowired
    private LinkDao linkDao;

    @Autowired
    private LinkEmitter emitter;

    private static final Pattern attachmentPattern = Pattern.compile("(.*/attachment.ap\\?id=(?<apId>\\d+).*)|(.*/attachment/(?<prettyId>\\d+).*)");

    public List<Attachment> getUnusedAttachments() {
        List<Integer> referredAttachments = new LinkedList<>();

        linkDao.doForEachLink(
                () -> new LinkQueryGenerator.Query("select * from link where url like '%" + Aksess.VAR_WEB + "/attachment%'"),
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

    public List<Integer> getUnusedAttachmentsForContent(Integer contentId) {
        ContentManagementService cms = new ContentManagementService(SecuritySession.createNewAdminInstance());
        try {
            linkDao.deleteLinksForContentId(contentId);
            ContentIdentifier cid = ContentIdentifier.fromContentId(contentId);
            linkDao.saveLinksForContent(emitter, cms.getContent(cid, false));
            Collection<LinkOccurrence> attachmentUris = linkDao.getLinksforContentId(contentId)
                    .stream()
                    .filter(lo -> lo.getUrl().contains("/attachment"))
                    .collect(Collectors.toList());

            Collection<Integer> attachmentIds =  attachmentUris
                    .stream()
                    .map(lo -> getAttachmentId(lo.getUrl()))
                    .filter(Objects::nonNull)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());


            List<Integer> attachmentsForContent = attachmentAO.getAttachmentList(cid)
                    .stream()
                    .map(Attachment::getId)
                    .collect(Collectors.toList());

            attachmentsForContent.removeAll(attachmentIds);
            return attachmentsForContent;
        } catch (NotAuthorizedException e) {
            throw new IllegalStateException("Fuck you, I'm Admin!");
        }
    }
}
