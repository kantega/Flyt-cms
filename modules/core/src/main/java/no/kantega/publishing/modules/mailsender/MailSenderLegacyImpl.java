package no.kantega.publishing.modules.mailsender;

import no.kantega.publishing.api.mail.Mailsender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.MimeBodyPart;
import java.io.File;
import java.util.Arrays;
import java.util.Map;

public class MailSenderLegacyImpl implements Mailsender {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void send(String from, String to, String subject, String contentFile, String[] replaceStrings) {
        try {
            MailSender.send(from, to, subject, contentFile, replaceStrings);
        } catch (Exception e) {
            log.error("Error sending from: {}, to: {}, subject: {}, contentFile: {}, replaceStrings: {}", from, to, subject, contentFile, Arrays.toString(replaceStrings));
            log.error("Exception", e);
        }
    }

    @Override
    public void send(String from, String to, String subject, String contentFile, Map<String, Object> parameters) {
        try {
            MailSender.send(from, to, subject, contentFile, parameters);
        } catch (Exception e) {
            log.error("Error sending from: {}, to: {}, subject: {}, contentFile: {}, replaceStrings: {}", from, to, subject, contentFile, parameters);

            log.error("Exception", e);
        }
    }

    @Override
    public void send(String from, String to, String subject, String content) {
        try {
            MailSender.send(from, to, subject, content);
        } catch (Exception e) {
            log.error("Error sending from: {}, to: {}, subject: {}, content: {}", from, to, subject, content);
            log.error("Exception", e);
        }
    }

    @Override
    public void send(String from, String to, String subject, MimeBodyPart[] bodyParts) {
        try {
            MailSender.send(from, to, subject, bodyParts);
        } catch (Exception e) {
            log.error("Error sending from: {}, to: {}, subject: {}, bodyParts: {}", from, to, subject, bodyParts.length);
            log.error("Exception", e);
        }
    }

    @Override
    public void send(String from, String to, String cc, String bcc, String subject, MimeBodyPart[] bodyParts) {
        try {
            MailSender.send(from, to, cc, bcc, subject, bodyParts);
        } catch (Exception e) {
            log.error("Exception", e);
        }
    }

    @Override
    public String createStringFromVelocityTemplate(String templateFile, Map<String, Object> parameters) {
        return MailSender.createStringFromVelocityTemplate(templateFile, parameters);
    }

    @Override
    public MimeBodyPart createMimeBodyPartFromStringMessage(String content) {
        return MailSender.createMimeBodyPartFromStringMessage(content);
    }

    @Override
    public MimeBodyPart createMimeBodyPartFromBinaryFile(String pathToFile, String contentType, String fileName) {
        return MailSender.createMimeBodyPartFromBinaryFile(pathToFile, contentType, fileName);
    }

    @Override
    public MimeBodyPart createMimeBodyPartFromData(byte[] data, String contentType, String fileName) {
        return MailSender.createMimeBodyPartFromData(data, contentType, fileName);
    }

    @Override
    public MimeBodyPart createMimeBodyPartFromBinaryFile(File file, String contentType, String fileName) {
        return MailSender.createMimeBodyPartFromBinaryFile(file, contentType, fileName);
    }
}
