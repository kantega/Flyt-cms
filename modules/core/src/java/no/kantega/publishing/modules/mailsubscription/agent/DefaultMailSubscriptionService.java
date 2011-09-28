package no.kantega.publishing.modules.mailsubscription.agent;

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Site;
import no.kantega.publishing.modules.mailsender.MailSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultMailSubscriptionService implements MailSubscriptionDeliveryService{

    public void sendEmail(String recipient, List<Content> subscriberContent, Site site)throws  ConfigurationException, SystemException {
        Configuration config = Aksess.getConfiguration();
        // Send email to this user
        Map<String, Object> param = new HashMap<String, Object>();

        String baseurl = Aksess.getBaseUrl();
        // Parameters may be given site specific using "mail.alias..." or global for all sites "mail..."
        String alias = ".";

        if (site != null && !site.getAlias().equals("/")) {
            alias = site.getAlias();
            alias = alias.replace('/', '.');
            baseurl = site.getDefaultBaseUrl();
        }
        String from = config.getString("mail" + alias + "from");
        if (from == null) {
            from = config.getString("mail.from");
            if (from == null) {
                throw new ConfigurationException("mail.from", this.getClass().getName());
            }
        }

        String subject = config.getString("mail" + alias + "subscription.subject", null);
        if (subject == null) {
            subject = config.getString("mail.subscription.subject", "Nyhetsbrev");
        }

        if (subscriberContent.size() == 1) {
            subject = subject + ":" + subscriberContent.get(0).getTitle();
        }

        String template = config.getString("mail" + alias + "subscription.template", null);
        if (template == null) {
            template = config.getString("mail.subscription.template", "maillist.vm");
        }


        if (subscriberContent.size() > 0) {
            param.put("contentlist", subscriberContent);
            param.put("baseurl",baseurl);

            try {
                MailSender.send(from, recipient, subject, template, param);
            } catch (Exception e) {
                Log.error(this.getClass().getName(), e, null, null);
            }
        }
    }
}
