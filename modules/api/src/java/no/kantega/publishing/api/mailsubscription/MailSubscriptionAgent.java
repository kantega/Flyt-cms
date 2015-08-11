package no.kantega.publishing.api.mailsubscription;

import java.util.Date;

/**
 * Simple interface for hooking into the mail subscription system. All <code>MailSubscriptionAgent</code>s will be
 * called each time a mail subscription job is run. It is up to the implementer of this interface to decide appropriate
 * action based on the time since previous run and the mail interval for the executing job.
 * @author Kristian Lier Selnæs
 */
public interface MailSubscriptionAgent {

    /**
     * Called by the mail subscription jobs. It is up to the implementers of this method to lookup content changed
     * since previous run and locate the subscribers to send it to, based on the given interval.
     * @param previousRun timestamp.
     * @param interval Mail subscription interval
     */
    void emailNewContentSincePreviousDate(Date previousRun, MailSubscriptionInterval interval);
}
