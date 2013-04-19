package no.kantega.publishing.api.mailsubscription;

import java.util.List;

public interface MailSubscriptionService {
    /**
     * @param interval - see MailSubscriptionInterval.
     * @return all subscriptions with the given interval.
     */
    public List<MailSubscription> getMailSubscriptionByInterval(MailSubscriptionInterval interval);

    public void addMailSubscription(MailSubscription mailSubscription);

    /**
     * Retrieves all mail subscriptions for a given subscriber (email)
     * @param email subscriber email.
     * @return a list of all the mail subscriptions associated with the given email address.
     */
    public List<MailSubscription> getMailSubscriptions(final String email);

    public void removeMailSubscription(String email, int channel, int documenttype);

    public void removeAllMailSubscriptions(String email);

    public List<MailSubscription> getAllMailSubscriptions();
}
