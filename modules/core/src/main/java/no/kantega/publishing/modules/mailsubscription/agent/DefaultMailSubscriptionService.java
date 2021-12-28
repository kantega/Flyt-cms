package no.kantega.publishing.modules.mailsubscription.agent;

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.common.util.MultimediaHelper;
import no.kantega.publishing.modules.mailsender.MailSender;
import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultMailSubscriptionService implements MailSubscriptionDeliveryService{
    private static final Logger log = LoggerFactory.getLogger(DefaultMailSubscriptionService.class);

    public void sendEmail(String recipient, List<Content> subscriberContent, Site site) throws  ConfigurationException, SystemException {
        Configuration config = Aksess.getConfiguration();
        // Send email to this user
        Map<String, Object> param = new HashMap<>();

        String baseurl = Aksess.getBaseUrl();

        // Parameters may be given site specific using "mail.alias..." or global for all sites "mail..."
        String alias = ".";

        if (site != null && !site.getAlias().equals("/")) {
            alias = site.getAlias();
            alias = alias.replace('/', '.');
            if(!site.getHostnames().isEmpty()){
                baseurl = site.getHostnames().get(0);
            }
        }

        String from = getMailFrom(config, alias);
        String subject = getMailSubject(subscriberContent, config, alias);

        if (subscriberContent.size() > 0) {
            param.put("contentlist", subscriberContent);
            param.put("baseurl",baseurl);

            try {
                List<MimeBodyPart> bodyParts = createBodyParts(config, alias, param);
                MailSender.send(from, recipient, subject, bodyParts.toArray(new MimeBodyPart[bodyParts.size()]));
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    private String getMailSubject(List<Content> subscriberContent, Configuration config, String alias) throws ConfigurationException {
        String subject = config.getString("mail" + alias + "subscription.subject", null);
        if (subject == null) {
            subject = config.getString("mail.subscription.subject", "Nyhetsbrev");
        }

        if (subscriberContent.size() == 1) {
            subject = subject + ": " + subscriberContent.get(0).getTitle();
        }
        return subject;
    }

    private String getMailFrom(Configuration config, String alias) throws ConfigurationException {
        String from = config.getString("mail" + alias + "from");
        if (from == null) {
            from = config.getString("mail.from");
            if (from == null) {
                throw new ConfigurationException("mail.from");
            }
        }
        return from;
    }


    private List<MimeBodyPart> createBodyParts(Configuration config, String alias, Map<String, Object> param) throws ConfigurationException, MessagingException {
        List<MimeBodyPart> bodyParts = new ArrayList<>();

        String template = config.getString("mail" + alias + "subscription.template", null);
        if (template == null) {
            template = config.getString("mail.subscription.template", "maillist.vm");
        }

        String bodyText = MailSender.createStringFromVelocityTemplate(template, param);
        List<Integer> multimediaIds = MultimediaHelper.getMultimediaIdsFromText(bodyText);
        boolean inlineImages = config.getBoolean("mail.subscription.inlineimages", true);
        bodyParts.add(createTextBodyPart(bodyText, inlineImages));

        if (inlineImages) {
            for (Integer mediaId : multimediaIds) {
                MimeBodyPart bodyPart = getMultimediaAsBodyPart(mediaId);
                if (bodyPart != null) {
                    bodyParts.add(bodyPart);
                }
            }
        }

        return bodyParts;
    }

    private MimeBodyPart createTextBodyPart(String bodyText, boolean inlineImages) {
        if (inlineImages) {
            bodyText = MultimediaHelper.replaceMultimediaUrlsWithCid(bodyText);
        }
        return MailSender.createMimeBodyPartFromStringMessage(bodyText);
    }

    private MimeBodyPart getMultimediaAsBodyPart(Integer mediaId) throws MessagingException {
        MultimediaService multimediaService = new MultimediaService(SecuritySession.createNewAdminInstance());

        Multimedia multimedia = multimediaService.getMultimedia(mediaId);
        if (multimedia != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            multimediaService.streamMultimediaData(multimedia.getId(), new InputStreamHandler(bos));
            MimeBodyPart bodyPart = MailSender.createMimeBodyPartFromData(bos.toByteArray(), multimedia.getMimeType().getType(), multimedia.getFilename());
            bodyPart.addHeader("Content-ID", "<image" + multimedia.getId() + ">");
            return bodyPart;
        } else {
            return null;
        }
    }
}
