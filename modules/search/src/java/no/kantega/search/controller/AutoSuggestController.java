package no.kantega.search.controller;

import no.kantega.search.core.Searcher;
import no.kantega.search.query.CompletionQuery;
import no.kantega.search.result.Suggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@Controller
public class AutoSuggestController {

    private Searcher searcher;

    @RequestMapping("/autosuggest")
    public @ResponseBody String search(@RequestParam(value = "q") String term, @RequestParam(required = false, defaultValue = "5") Integer limit) throws UnsupportedEncodingException {
        List<Suggestion> suggestionList = suggest(decodeTerm(term), limit);
        return printSuggestions(suggestionList);
    }

    private List<Suggestion> suggest(String term, int limit) {
        List<Suggestion> suggestions = new ArrayList<Suggestion>();
        if (isNotBlank(term)) {
            CompletionQuery query = new CompletionQuery();
            query.setText(term);
            query.setMax(limit);
            suggestions = searcher.suggest(query);
        }
        return suggestions;
    }

    private String printSuggestions(List<Suggestion> suggestionList) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        for (Suggestion suggestion : suggestionList) {
            builder.append(suggestion.getPhrase());
            builder.append('\n');
        }
      return builder.toString();
    }

    private String decodeTerm(String term) throws UnsupportedEncodingException {
        if (term != null) {
            term = URLDecoder.decode(term, "UTF-8");
            term = term.toLowerCase();
        }
        return term;
    }

    @Autowired
    public void setSearcher(Searcher searcher) {
        this.searcher = searcher;
    }
}
