package no.kantega.publishing.modules.mailsubscription;

import no.kantega.commons.exception.InvalidParameterException;
import no.kantega.publishing.api.mailsubscription.MailSubscription;
import no.kantega.publishing.api.mailsubscription.MailSubscriptionInterval;
import no.kantega.publishing.api.mailsubscription.MailSubscriptionService;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MailSubscriptionServiceJdbcImpl extends NamedParameterJdbcDaoSupport implements MailSubscriptionService {

    @Override
    public List<MailSubscription> getMailSubscriptionByInterval(MailSubscriptionInterval interval) {
        return getNamedParameterJdbcTemplate().query("SELECT * FROM mailsubscription WHERE MailInterval = :mailinterval",
                Collections.singletonMap("mailinterval", interval.name()), rowMapper);
    }

    @Override
    public void addMailSubscription(MailSubscription subscription) {
        if (subscription.getEmail() == null || (subscription.getChannel() == -1 && subscription.getDocumenttype() == -1)) {
            throw new InvalidParameterException("email/channel", "MailSubscriptionService");
        }

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("channel", subscription.getChannel());
        paramMap.put("documenttype", subscription.getDocumenttype());
        paramMap.put("language", subscription.getLanguage());
        paramMap.put("email", subscription.getEmail().toLowerCase());
        getNamedParameterJdbcTemplate().update("delete from mailsubscription where Channel = :channel and DocumentType = :documenttype and Language = :language and Email = :email", paramMap);
        paramMap.put("interval", subscription.getInterval().name());
        getNamedParameterJdbcTemplate().update("insert into mailsubscription (Channel, DocumentType, Language, Email, MailInterval) values (:channel, :documenttype, :language, :email, :interval)", paramMap);
    }

    @Override
    public List<MailSubscription> getMailSubscriptions(final String email) {
        return getNamedParameterJdbcTemplate().query("SELECT * FROM mailsubscription WHERE Email=:email", Collections.singletonMap("email", email), rowMapper);
    }

    @Override
    public void removeMailSubscription(String email, int channel, int documenttype) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("channel", channel);
        params.put("documenttype", documenttype);
        getNamedParameterJdbcTemplate().update("delete from mailsubscription where Email = :email and Channel = :channel and DocumentType = :documenttype", params);
    }

    @Override
    public void removeAllMailSubscriptions(String email) {
        getNamedParameterJdbcTemplate().update("delete from mailsubscription where Email = :email", Collections.singletonMap("email", email));
    }

    @Override
    public List<MailSubscription> getAllMailSubscriptions() {
        return getJdbcTemplate().query("select distinct Email from mailsubscription order by Email", rowMapper);
    }

    private RowMapper<MailSubscription> rowMapper  = new RowMapper<MailSubscription>() {
        public MailSubscription mapRow(ResultSet rs, int i) throws SQLException {
            MailSubscription mailSubscription = new MailSubscription();
            mailSubscription.setChannel(rs.getInt("Channel"));
            mailSubscription.setDocumenttype(rs.getInt("DocumentType"));
            mailSubscription.setEmail(rs.getString("Email"));
            mailSubscription.setInterval(MailSubscriptionInterval.valueOf(rs.getString("MailInterval")));
            mailSubscription.setLanguage(rs.getInt("Language"));
            return mailSubscription;
        }
    };
}
