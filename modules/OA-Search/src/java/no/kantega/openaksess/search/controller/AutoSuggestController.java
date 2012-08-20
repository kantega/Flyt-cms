package no.kantega.openaksess.search.controller;

import no.kantega.openaksess.search.query.AksessSearchContextCreator;
import no.kantega.search.api.search.SearchQuery;
import no.kantega.search.api.search.Searcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class AutoSuggestController {
    @Autowired
    private Searcher searcher;

    @Autowired
    private AksessSearchContextCreator aksessSearchContextCreator;

    @RequestMapping("/autosuggest")
    public @ResponseBody
    List<String> search(HttpServletRequest request, @RequestParam(value = "q") String term, @RequestParam(required = false, defaultValue = "5") Integer limit) {

        SearchQuery query = new SearchQuery(aksessSearchContextCreator.getSearchContext(request), term);
        query.setResultsPerPage(limit);
        return searcher.suggest(query);
    }
}
