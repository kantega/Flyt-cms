package no.kantega.publishing.api.mailsubscription;

import java.util.List;

public interface MailSubscriptionService {

    /**
     * @param interval - see MailSubscriptionInterval.
     * @return all subscriptions with the given interval.
     */
    List<MailSubscription> getMailSubscriptionByInterval(MailSubscriptionInterval interval);

    /**
     * Adds a new mail subscription. Must at least contain subscriber <code>email</code> and
     * <code>channel</code> or <code>documentType</code>.
     * @param mailSubscription subscription details.
     */
    void addMailSubscription(MailSubscription mailSubscription);

    /**
     * Retrieves all mail subscriptions for a given subscriber (email)
     * @param email subscriber email.
     * @return a list of all the mail subscriptions associated with the given email address.
     */
    List<MailSubscription> getMailSubscriptions(final String email);

    /**
     * Removes a subscription identified by the given parameters.
     * <code>email</code> must be set and either <code>channel</code>, <code>documentType</code> or both must be set.
     * @param email subscriber email. Required
     * @param channel channel identifier or -1 if none.
     * @param documentType document type identifier or -1 if none
     */
    void removeMailSubscription(String email, int channel, int documentType);

    /**
     * Removes all subscriptions for a subscriber identified by the given email
     * @param email subscriber email.
     */
    void removeAllMailSubscriptions(String email);

    /**
     * Gets all email addresses in the entire subscription database
     * @return list of email addresses.
     */
    List<String> getAllMailSubscriptions();
}
