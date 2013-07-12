package no.kantega.publishing.modules.mailsubscription.agent;

import no.kantega.commons.exception.ConfigurationException;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.data.Content;

import java.util.List;


public interface MailSubscriptionDeliveryService {
    public void sendEmail(String recipient, List<Content> content, Site site)throws  ConfigurationException ;
}
