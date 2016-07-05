package no.kantega.publishing.jobs.alerts;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.publishing.common.ao.LinkDao;
import no.kantega.publishing.common.data.Attachment;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.modules.linkcheck.check.LinkHandler;
import no.kantega.publishing.modules.linkcheck.check.LinkOccurrence;
import no.kantega.publishing.modules.linkcheck.check.LinkQueryGenerator;
import no.kantega.publishing.modules.linkcheck.crawl.LinkEmitter;
import no.kantega.publishing.security.SecuritySession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;


public class UnusedAttachmentsFinder {

    @Autowired
    private LinkDao linkDao;

    @Autowired
    private LinkEmitter emitter;

    private static final Pattern attachmentPattern = Pattern.compile("(.*/attachment.ap\\?id=(?<apId>\\d+).*)|(.*/attachment/(?<prettyId>\\d+).*)");

    public List<Attachment> getUnusedAttachments() {
        final List<Integer> referredAttachments = new LinkedList<>();

        linkDao.doForEachLink(
                new LinkQueryGenerator() {
                    @Override
                    public Query getQuery() {
                        return new Query("select * from link where url like '%" + Aksess.VAR_WEB + "/attachment%'");
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

    private List<Integer> getUsedAttachmentsForContentVersion(ContentIdentifier cid) throws ContentNotFoundException {
        ContentManagementService cms = new ContentManagementService(SecuritySession.createNewAdminInstance());
        try {
            linkDao.deleteLinksForContentId(cid.getContentId());
            Content content = cms.getContent(cid, false);
            if(content == null) {
                throw new ContentNotFoundException(cid.toString());
            }
            linkDao.saveLinksForContent(emitter, content);
            Collection<LinkOccurrence> attachmentUris = filter(linkDao.getLinksforContentId(cid.getContentId()), new Predicate<LinkOccurrence>() {
                @Override
                public boolean apply(LinkOccurrence lo) {
                    return lo.getUrl().contains("/attachment");
                }
            });
            Collection<Integer> attachmentIds = transform(attachmentUris, new Function<LinkOccurrence, Integer>() {
                @Override
                public Integer apply(LinkOccurrence input) {
                    String attachmentId = getAttachmentId(input.getUrl());
                    if (attachmentId == null) {
                        return null;
                    } else {
                        return Integer.parseInt(attachmentId);
                    }
                }
            });

            return new ArrayList<>(attachmentIds);
        } catch (NotAuthorizedException e) {
            throw new IllegalStateException("Fuck you, I'm Admin!");
        }
    }

    public List<Integer> getUnusedAttachmentsForContent(Integer contentId) throws ContentNotFoundException {
        ContentIdentifier cid = ContentIdentifier.fromContentId(contentId);
        Collection<Integer> attachmentIds = getUsedAttachmentsForContentVersion(cid);

        List<Integer> attachmentsForContent = new ArrayList<>(transform(AttachmentAO.getAttachmentList(cid), new Function<Attachment, Integer>() {
            @Override
            public Integer apply(Attachment input) {
                return input.getId();
            }
        }));
        attachmentsForContent.removeAll(attachmentIds);
        return attachmentsForContent;
    }

    public Map<Integer, List<Integer>> attachmentIdsByContentVersion(Integer contentId) {
        Map<Integer, List<Integer>> mapping = new LinkedHashMap<>();
        try {
            ContentManagementService cms = new ContentManagementService(SecuritySession.createNewAdminInstance());

            for(Content c : cms.getAllContentVersions(ContentIdentifier.fromContentId(contentId))) {
                ContentIdentifier cid = ContentIdentifier.fromContentId(contentId);
                cid.setVersion(c.getVersion());
                mapping.put(c.getVersion(), getUsedAttachmentsForContentVersion(cid));
            }
        } catch (ContentNotFoundException e) { /* Ignore */}

        return mapping;
    }
}
