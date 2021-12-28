package no.kantega.publishing.jobs.alerts;

import no.kantega.publishing.api.attachment.ao.AttachmentAO;
import no.kantega.publishing.api.content.ContentAO;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.link.LinkDao;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.modules.linkcheck.check.LinkOccurrence;
import no.kantega.publishing.modules.linkcheck.check.LinkQueryGenerator;
import no.kantega.publishing.modules.linkcheck.crawl.LinkEmitter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
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

    @Autowired
    private ContentAO contentAO;

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

    private List<Integer> getUsedAttachmentsForContentVersion(ContentIdentifier cid) throws ContentNotFoundException {
        linkDao.deleteLinksForContentId(cid.getContentId());
        Content content = contentAO.getContent(cid, true);
        if(content == null) {
            throw new ContentNotFoundException(cid.toString());
        }
        linkDao.saveLinksForContent(emitter, content);
        Collection<LinkOccurrence> attachmentUris = linkDao.getLinksforContentId(cid.getContentId())
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
    }

    public List<Integer> getUnusedAttachmentsForContent(Integer contentId) throws ContentNotFoundException {
        ContentIdentifier cid = ContentIdentifier.fromContentId(contentId);
        Collection<Integer> attachmentIds = getUsedAttachmentsForContentVersion(cid);

        List<Integer> attachmentsForContent = attachmentAO.getAttachmentList(cid)
                .stream()
                .map(Attachment::getId)
                .collect(Collectors.toList());
        attachmentsForContent.removeAll(attachmentIds);
        return attachmentsForContent;
    }

    public Map<Integer, List<Integer>> attachmentIdsByContentVersion(Integer contentId) {
        Map<Integer, List<Integer>> mapping = new LinkedHashMap<>();
        try {

            for(Content c : contentAO.getAllContentVersions(ContentIdentifier.fromContentId(contentId))) {
                ContentIdentifier cid = ContentIdentifier.fromContentId(contentId);
                cid.setVersion(c.getVersion());
                mapping.put(c.getVersion(), getUsedAttachmentsForContentVersion(cid));
            }
        } catch (ContentNotFoundException e) { /* Ignore */}

        return mapping;
    }
}
