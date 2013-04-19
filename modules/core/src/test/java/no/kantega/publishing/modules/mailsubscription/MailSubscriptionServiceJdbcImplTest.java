package no.kantega.publishing.modules.mailsubscription;

import com.google.common.base.Function;
import no.kantega.publishing.api.content.Language;
import no.kantega.publishing.api.mailsubscription.MailSubscription;
import no.kantega.publishing.api.mailsubscription.MailSubscriptionInterval;
import no.kantega.publishing.api.mailsubscription.MailSubscriptionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.collect.Lists.transform;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:spring/testContext.xml")
public class MailSubscriptionServiceJdbcImplTest {
    @Autowired
    private MailSubscriptionService mailSubscriptionService;

    @Test
    public void shouldGetSubscriptionsWithImmediate(){
        List<MailSubscription> mailSubscriptionByInterval = mailSubscriptionService.getMailSubscriptionByInterval(MailSubscriptionInterval.immediate);

        List<String> emails = transform(mailSubscriptionByInterval, mailTransformer);

        assertTrue("Result did not contain both mailz3@broadpark.no and mailz4@stinessen.com",
                emails.contains("mailz3@broadpark.no") && emails.contains("mailz4@stinessen.com"));
        for (MailSubscription mailSubscription : mailSubscriptionByInterval) {
            assertEquals("mailSubscription did not have MailSubscriptionInterval.immediate", MailSubscriptionInterval.immediate, mailSubscription.getInterval());
        }
    }

    @Test
    public void shouldGetSubscriptionsWithWeekly(){
        List<MailSubscription> mailSubscriptionByInterval = mailSubscriptionService.getMailSubscriptionByInterval(MailSubscriptionInterval.weekly);

        List<String> emails = transform(mailSubscriptionByInterval, mailTransformer);

        assertTrue("Result did not contain both mailz5@mail.com and mailz4@stinessen.com",
                emails.contains("mailz5@mail.com") && emails.contains("mailz6@email.com"));
        for (MailSubscription mailSubscription : mailSubscriptionByInterval) {
            assertEquals("mailSubscription did not have MailSubscriptionInterval.weekly", MailSubscriptionInterval.weekly, mailSubscription.getInterval());
        }
    }

    @Test
    public void shouldGetSubscriptionsWithDaily(){
        List<MailSubscription> mailSubscriptionByInterval = mailSubscriptionService.getMailSubscriptionByInterval(MailSubscriptionInterval.daily);

        List<String> emails = transform(mailSubscriptionByInterval, mailTransformer);

        assertTrue("Result did not contain both mailz@gmail.com and mailz4@stinessen.com",
                emails.contains("mailz@gmail.com") && emails.contains("mailz2@gmail.com"));
        for (MailSubscription mailSubscription : mailSubscriptionByInterval) {
            assertEquals("mailSubscription did not have MailSubscriptionInterval.immediate", MailSubscriptionInterval.daily, mailSubscription.getInterval());
        }
    }

    @Test
    public void subscriptionShouldBeAdded(){
        String email = "mail@mail.mail";
        List<MailSubscription> mailSubscriptions = mailSubscriptionService.getMailSubscriptions(email);
        assertEquals("subscription existed", 0, mailSubscriptions.size());

        MailSubscription mailSubscription = new MailSubscription();
        mailSubscription.setEmail(email);
        mailSubscription.setChannel(12);
        mailSubscription.setInterval(MailSubscriptionInterval.daily);
        mailSubscription.setLanguage(Language.DANISH);
        mailSubscription.setDocumenttype(3);
        mailSubscriptionService.addMailSubscription(mailSubscription);

        mailSubscriptions = mailSubscriptionService.getMailSubscriptions(email);
        assertEquals("subscription was not saved", 1, mailSubscriptions.size());
    }

    @Test
    public void removeMailSubscriptionByMailChannelDocumentTypeShouldDelete(){
        List<MailSubscription> mailSubscriptionByInterval = mailSubscriptionService.getMailSubscriptionByInterval(MailSubscriptionInterval.daily);
        assertTrue("mailsubscriptions was empty", mailSubscriptionByInterval.size() > 0);
        for (MailSubscription mailSubscription : mailSubscriptionByInterval) {
            mailSubscriptionService.removeMailSubscription(mailSubscription.getEmail(), mailSubscription.getChannel(), mailSubscription.getDocumenttype());
        }

        mailSubscriptionByInterval = mailSubscriptionService.getMailSubscriptionByInterval(MailSubscriptionInterval.daily);
        assertEquals("mailSubscriptions was not removed", 0, mailSubscriptionByInterval.size());
    }

    @Test
    public void removeMailSubscriptionByMail(){
        List<MailSubscription> mailSubscriptionByInterval = mailSubscriptionService.getMailSubscriptionByInterval(MailSubscriptionInterval.weekly);
        assertTrue("mailsubscriptions was empty", mailSubscriptionByInterval.size() > 0);
        for (MailSubscription mailSubscription : mailSubscriptionByInterval) {
            mailSubscriptionService.removeAllMailSubscriptions(mailSubscription.getEmail());
        }

        mailSubscriptionByInterval = mailSubscriptionService.getMailSubscriptionByInterval(MailSubscriptionInterval.weekly);
        assertEquals("mailSubscriptions was not removed", 0, mailSubscriptionByInterval.size());
    }

    @Test
    public void nonExistingEmailShouldReturnEmptyList(){
        List<MailSubscription> mailSubscriptions = mailSubscriptionService.getMailSubscriptions("nonexisting@mail.com");
        assertNotNull(mailSubscriptions);
        assertEquals("list was not null", 0, mailSubscriptions.size());
    }

    private static final Function<MailSubscription, String> mailTransformer = new Function<MailSubscription, String>() {
        @Nullable
        @Override
        public String apply(@Nullable MailSubscription input) {
            return input.getEmail();
        }
    };
}
