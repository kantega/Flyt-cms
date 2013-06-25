/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.api.taglibs.util;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.spring.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * Author: Kristian Lier Selnæs, Kantega
 * Date: 19.apr.2007
 * Time: 14:21:29
 */
public class PollTag  extends TagSupport {
    private static final Logger log = LoggerFactory.getLogger(PollTag.class);

    private JdbcTemplate jdbcTemplate;
    private Content containingPage;
    /**
     * Den publiserte pollens contentId
     */
    private int pollid = -1;
    private String sessionVarName = "aksess_poll_";


    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        HttpSession session = pageContext.getSession();

        try {
            ContentIdentifier cid =  ContentIdentifier.fromContentId(pollid);
            containingPage = new ContentManagementService(request).getContent(cid);
        } catch (SystemException e) {
            log.error("Error getting content", e);
            throw new JspException(e);
        } catch (NotAuthorizedException e) {
            log.error("User not authorized", e);
            throw new JspException(e);
        }

        jdbcTemplate = new JdbcTemplate((DataSource) RootContext.getInstance().getBean("aksessDataSource"));

        createOrUpdatePoll();

        if("post".equalsIgnoreCase(request.getMethod()) && request.getParameter("pollsubmit_" + pollid) != null) {
            if(!hasVoted(session)){
                registerResult(request);
                session.setAttribute(sessionVarName + pollid, ""+ pollid);
            }
            fetchResults(request);
            request.setAttribute("visresultat", Boolean.TRUE);
        }
        else{
            Boolean visresultat = Boolean.FALSE;
            //Hvis brukeren allerede har stemt
            //eller har klikket på link for å vise resultat
            if(hasVoted(session) || (request.getParameter("visresultat") != null && request.getParameter("visresultat").equals("true")) ) {
                fetchResults(request);
                visresultat = Boolean.TRUE;
            }
            request.setAttribute("visresultat", visresultat);
        }


        return SKIP_BODY;
    }



    /**
     * Legger til alternativer som ikke finnes i denne pollen.
     *
     * Ved første request til pollen vil det ikke finnes noen alternativer, alle blir da opprettet.
     *
     * Hvis brukeren har lagt til et eller flere alternativer til en eksisterende poll vil disse bli lagt til ved
     * første request til pollen etter tilleggelse.
     */
    private void createOrUpdatePoll(){
        int altnum = 1;
        String alternativ = containingPage.getAttributeValue("alt" + altnum);
        while(alternativ != null && alternativ.length() > 0){
            if(!alternativeExists("alt" + altnum, pollid)){
                jdbcTemplate.update("INSERT INTO poll(Alternative, Votes, ContentId) VALUES(?,0,?)", new Object[]{"alt" + altnum, pollid});
            }
            altnum++;
            alternativ = containingPage.getAttributeValue("alt" + altnum);
        }
    }

    /**
     * Sjekker om et gitt alternativ eksisterer for denne pollen.
     *
     * @param alt
     * @param contentId
     * @return
     */
    private boolean alternativeExists(String alt, int contentId) {
        try{
            int numRows = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM poll WHERE Alternative=? AND ContentId=?", new Object[]{alt, pollid});
            return numRows > 0;
        } catch(IncorrectResultSizeDataAccessException e){//Query'et gir ingen treff
            return false;
        }
    }

    /**
     * Legger inn resultatet i basen
     *
     * @param request
     */
    private void registerResult(HttpServletRequest request) {
        RequestParameters param = new RequestParameters(request);
        String alternativ = param.getString("alternativ");
        if(alternativ != null){
            jdbcTemplate.update("UPDATE poll SET Votes=Votes+1 WHERE Alternative=? AND ContentId=?", new Object[]{alternativ, pollid});
        }
    }

    /**
     * Legger resultatene i modellen
     *
     * @param request
     */
    private void fetchResults(HttpServletRequest request) {
        List alternativer = jdbcTemplate.query("SELECT * FROM poll WHERE ContentId=? ORDER BY Alternative", new Object[]{pollid}, new RowMapper(){
            public Object mapRow(ResultSet rs, int i) throws SQLException {
                return new Integer(rs.getInt("Votes"));
            }
        });
        Iterator i = alternativer.iterator();
        int altnum = 1;
        while(i.hasNext()){
            request.setAttribute("poll_alt" + altnum, i.next());
            altnum++;
        }

        try {
            int numVotes = jdbcTemplate.queryForInt("SELECT SUM(Votes) FROM poll WHERE ContentId=?", new Object[]{pollid});
            request.setAttribute("num_votes", new Integer(numVotes));
        } catch (IncorrectResultSizeDataAccessException e) {
            //Skal ikke skje siden alle alternativer er default 0
            request.setAttribute("num_votes", new Integer(0));
        }
    }

    /**
     * Sjekker om brukeren allerede har stemt
     *
     * @param session
     * @return
     */
    private boolean hasVoted(HttpSession session) {
        return (""+pollid).equals(session.getAttribute(sessionVarName + pollid));
    }


    /**
     *
     * @param pollid
     */
    public void setPollid(int pollid) {
        this.pollid = pollid;
    }
}
