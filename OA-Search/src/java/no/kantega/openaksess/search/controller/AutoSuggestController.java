package no.kantega.openaksess.search.controller;

import no.kantega.openaksess.search.security.AksessSearchContext;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.search.api.search.SearchQuery;
import no.kantega.search.api.search.Searcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class AutoSuggestController {
    @Autowired
    private Searcher searcher;

    @RequestMapping("/autosuggest")
    public @ResponseBody
    List<String> search(@RequestParam(value = "q") String term, @RequestParam(required = false, defaultValue = "5") Integer limit) {

        int siteId = -1;
        return searcher.suggest(new SearchQuery(new AksessSearchContext(SecuritySession.createNewUnauthenticatedInstance(), siteId), term));
    }
}
