package no.kantega.publishing.api.mail;

import javax.mail.internet.MimeBodyPart;
import java.io.File;
import java.util.Map;

/**
 * Interface for sending mail
 */
public interface Mailsender {

        /**
         * Sends a mail message. The message body is created by using a simple template mechanism. See MailTextReader.
         *
         * @param from           Sender's email address.
         * @param to             Recipient's email address.
         * @param subject        Subject text for the email.
         * @param contentFile    Template file.
         * @param replaceStrings Strings to insert into template.
         */
        void send(String from, String to, String subject, String contentFile, String replaceStrings[]);

        /**
         * Sends a mail message. The message body is created by using a Velocity template.
         *
         * @param from        Sender's email address.
         * @param to          Recipient's email address.
         * @param subject     Subject text for the email.
         * @param contentFile Velocity template file.
         * @param parameters  Parameters used by Velocity to merge values into the template.
         */
        void send(String from, String to, String subject, String contentFile, Map<String, Object> parameters);

        /**
         * Sends a mail message with a simple string as the message body.
         *
         * @param from    Sender's email address.
         * @param to      Recipient's email address.
         * @param subject Subject text for the email.
         * @param content The message body.
         */
        void send(String from, String to, String subject, String content);

        /**
         * Sends a mail message. The content must be provided as MimeBodyPart objects.
         *
         * @param from      Sender's email address.
         * @param to        Recipient's email address.
         * @param subject   Subject text for the email.
         * @param bodyParts The body parts to insert into the message.
         */
        void send(String from, String to, String subject, MimeBodyPart[] bodyParts) ;

        /**
         * Helper method to create a string from a Velocity template.
         *
         * @param templateFile The name of the template file to use.
         * @param parameters   The values to merge into the template.
         * @return The result of the merge.
         */
        String createStringFromVelocityTemplate(String templateFile, Map<String, Object> parameters) ;

        /**
         * Helper method to create a MimeBodyPart from a string.
         *
         * @param content The string to insert into the MimeBodyPart.
         * @return The resulting MimeBodyPart.
         */
        MimeBodyPart createMimeBodyPartFromStringMessage(String content);


        /**
         * Helper method to create a MimeBodyPart from a binary file.
         *
         * @param pathToFile The complete path to the file - including file name.
         * @param contentType The Mime content type of the file.
         * @param fileName   The name of the file - as it will appear for the mail recipient.
         * @return The resulting MimeBodyPart.
         */
        MimeBodyPart createMimeBodyPartFromBinaryFile(final String pathToFile, final String contentType, String fileName);

        /**
         * Helper method to create a MimeBodyPart from a binary file.
         *
         * @param data Data
         * @param contentType The Mime content type of the file.
         * @param fileName   The name of the file - as it will appear for the mail recipient.
         * @return The resulting MimeBodyPart.
         */
        MimeBodyPart createMimeBodyPartFromData(byte[] data, final String contentType, String fileName);

        /**
         * Helper method to create a MimeBodyPart from a binary file.
         *
         * @param file     The file.
         * @param contentType The Mime content type of the file.
         * @param fileName The name of the file - as it will appear for the mail recipient.
         * @return The resulting MimeBodyPart.
         */
        MimeBodyPart createMimeBodyPartFromBinaryFile(final File file, final String contentType, String fileName);
}
