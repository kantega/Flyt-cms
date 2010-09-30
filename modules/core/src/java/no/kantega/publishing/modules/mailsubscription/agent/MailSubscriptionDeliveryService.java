package no.kantega.publishing.modules.mailsubscription.agent;

import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Site;

import java.util.List;
import java.util.Map;/*
Created by jordyr, 29.sep.2010

*/

public interface MailSubscriptionDeliveryService {
    public void sendEmail(String recipient, List<Content> content, Site site)throws  ConfigurationException ;
}
