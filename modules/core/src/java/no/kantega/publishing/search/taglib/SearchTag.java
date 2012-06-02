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

package no.kantega.publishing.search.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.HashMap;
import java.util.Map;

public class SearchTag extends TagSupport {
    private static String SOURCE = "SearchTag";

    private String query = null;
    private int parent = 0;
    private int lang = -1;
    private int notlang = -1;
    private String queryref = null;
    private String excludequeryref = null;
    private String view = null;

    private String requiredwords = null;
    private String prohibitedwords = null;
    private String phrase = null;
    private int siteId = -1;
    private String suggestionbase;
    private int numhitsforsuggestion = 3;
    private String doctypes = "";

    public final static String HITS = "TagSupport.HITS";
    private int hitsPerPage = 10;


    // Not in use, only for backwards compatibility
    private String index;

    private String sortfield;

    private boolean sortreversed;

    public int doStartTag() throws JspException {

        Map map = new HashMap();

/*
        try {
            String queryString = "";
            String url = "";

            ApplicationContext applicationContext = RootContext.getInstance();

            Analyzer analyzer = ((AnalyzerFactory)applicationContext.getBean("analyzerFactory")).createInstance();

            IndexSearcherManager manager = (IndexSearcherManager) applicationContext.getBean("indexSearcherManager");
            IndexReaderManager readerManager = (IndexReaderManager) applicationContext.getBean("indexReaderManager");

            DocumentProviderSelector selector = (DocumentProviderSelector) applicationContext.getBean("documentProviderSelector");

            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
            BooleanQuery bq = new BooleanQuery();
            Query q;
            StringBuffer words = new StringBuffer();
            if (queryref != null) {
                q = (Query) request.getAttribute(queryref);
                bq.add(q, BooleanClause.Occur.MUST);
            } else {
                if (query == null) {
                    query = request.getParameter("q");
                }

                if (phrase == null) {
                    phrase = request.getParameter("phrase");
                }

                if (requiredwords == null) {
                    requiredwords = request.getParameter("allWords");
                }

                if (prohibitedwords == null) {
                    prohibitedwords = request.getParameter("notQ");
                }

                if(parent == 0) {
                    try {
                        parent = Integer.parseInt(request.getParameter("parent"));
                    } catch (NumberFormatException e) {
                        parent = 0;
                    }
                }

                if(query != null && query.length() > 0) {
                    QueryParser qp = new QueryParser(Fields.CONTENT, analyzer);
                    qp.setDefaultOperator(operator);
                    q = qp.parse(query);
                    bq.add(q, BooleanClause.Occur.MUST);
                    Query q2 = new QueryParser(Fields.TM_TOPICS, analyzer).parse(query);
                    bq.add(q2, BooleanClause.Occur.SHOULD);
                    Query q3 = new QueryParser(Fields.TITLE, analyzer).parse(query);
                    bq.add(q3, BooleanClause.Occur.SHOULD);
                    Query q4 = new QueryParser(Fields.ALIAS, analyzer).parse(query);
                    bq.add(q4, BooleanClause.Occur.SHOULD);
                    Query q5 = new QueryParser(Fields.KEYWORDS, analyzer).parse(query);
                    bq.add(q5, BooleanClause.Occur.SHOULD);
                    Query q6 = new QueryParser(Fields.ALT_TITLE, analyzer).parse(query);
                    bq.add(q6, BooleanClause.Occur.SHOULD);

                    map.put("q", query);
                    words.append(query);
                    queryString += "&q=" + URLEncoder.encode(query, "iso-8859-1");
                }

                if(doctypes != null && !"".equals(doctypes.trim())) {
                    String[] types = doctypes.split(",");
                    BooleanQuery or = new BooleanQuery();
                    for (String type : types) {
                        if (!type.trim().equals("")) {
                            or.add(new TermQuery(new Term(Fields.DOCTYPE, type)), BooleanClause.Occur.SHOULD);
                        }
                    }
                    if(or.getClauses().length >0) {
                        bq.add(or, BooleanClause.Occur.MUST);
                    }
                }
                if (parent != 0) {
                    BooleanQuery or = new BooleanQuery();
                    bq.add(new TermQuery(new Term(Fields.CONTENT_PARENTS, Integer.toString(parent))), BooleanClause.Occur.MUST);
                    queryString += "&parent=" + parent;
                    map.put("parent", parent);
                }

                if (lang >= 0) {
                    bq.add(new TermQuery(new Term(Fields.LANGUAGE, Integer.toString(lang))), BooleanClause.Occur.MUST);
                    queryString += "&parent=" + parent;
                }

                if (notlang >= 0) {
                    bq.add(new TermQuery(new Term(Fields.LANGUAGE, Integer.toString(notlang))), BooleanClause.Occur.MUST_NOT);
                }

                if (requiredwords != null && requiredwords.length() > 1) {
                    StringTokenizer st = new StringTokenizer(requiredwords, " ");
                    while (st.hasMoreTokens()) {
                        bq.add(new QueryParser(Fields.CONTENT, analyzer).parse(st.nextToken()), BooleanClause.Occur.MUST);
                    }
                    queryString += "&allWords=" + URLEncoder.encode(requiredwords, "iso-8859-1");
                    map.put("allWords", requiredwords);
                    words.append(" ").append(requiredwords);
                }

                if (phrase != null && phrase.length() > 1) {
                    bq.add(new QueryParser(Fields.CONTENT, analyzer).parse("\"" + phrase +"\""), BooleanClause.Occur.MUST);
                    queryString += "&phrase=" + URLEncoder.encode(phrase, "iso-8859-1");
                    map.put("phrase", phrase);
                    words.append(" ").append(phrase);
                }

                if (prohibitedwords != null && prohibitedwords.length() > 1) {
                    StringTokenizer st = new StringTokenizer(prohibitedwords, " \"\'");
                    while (st.hasMoreTokens()) {
                        bq.add(new QueryParser(Fields.CONTENT, analyzer).parse(st.nextToken()), BooleanClause.Occur.MUST_NOT);
                    }
                    queryString += "&notQ=" + URLEncoder.encode(prohibitedwords, "iso-8859-1");
                    map.put("notQ", prohibitedwords);
                }

                Content content = (Content)request.getAttribute("aksess_this");
                if (content != null) {
                    if (siteId == -1) {
                        siteId = content.getAssociation().getSiteId();
                    }
                    url = content.getUrl();
                }

                if (siteId == -1) {
                    Site site = SiteCache.getSiteByHostname(request.getServerName());
                    if (site != null) {
                        siteId = site.getId();
                    }
                }

                if(siteId != -1) {
                    bq.add(new TermQuery(new Term(Fields.SITE_ID, Integer.toString(siteId))), BooleanClause.Occur.MUST);
                }
            }
            if (excludequeryref != null) {
                Query exludeQuery =(Query) request.getAttribute(excludequeryref);
                if (exludeQuery != null) {
                    bq.add(exludeQuery, BooleanClause.Occur.MUST_NOT);
                }
            }

            bq.add(new TermQuery(new Term(Fields.CONTENT_STATUS, Integer.toString(ContentStatus.PUBLISHED))), BooleanClause.Occur.MUST);
            bq.add(new TermQuery(new Term(Fields.CONTENT_VISIBILITY_STATUS, Integer.toString(ContentVisibilityStatus.ACTIVE))), BooleanClause.Occur.MUST);
            map.put("queryString", queryString);

            Log.debug("SearchTag", "Query:" + bq.toString(), null, null);

            if (url.length() > 0 && url.indexOf("?") == -1) {
                url += "?" + queryString.substring(1, queryString.length());
            } else {
                url += queryString;
            }


            IndexSearcher searcher = null;

            int startIndex = 0;
            int endIndex = 0;

            searcher = manager.getSearcher("aksess");

            Sort sort = sortfield != null ? new Sort(sortfield, sortreversed) : Sort.RELEVANCE;
            Hits hits = searcher.search(bq, sort);

            if (Aksess.isSearchLogEnabled() && words.toString().length() > 0) {
                // Register number of hits for this query
                try {
                    SearchAO.registerSearch(words.toString(), bq.toString(), siteId, hits.length());
                } catch (Exception e) {
                    Log.error(SOURCE, e, null, null);
                }
            }

            map.put("numhits", hits.length());

            if(hits.length() < numhitsforsuggestion && suggestionbase != null) {
                Directory spellDirectory = readerManager.getReader("spelling").directory();
                final SpellChecker spellChecker = new SpellChecker(spellDirectory);

                final IndexReader reader = searcher.getIndexReader();

                final List suggestionList = new ArrayList();
                QueryParser suggestingParser = new QueryParser(Fields.CONTENT, analyzer) {

                    protected Query getFieldQuery(String field, String text) throws org.apache.lucene.queryParser.ParseException {
                        if(Fields.CONTENT.equals(field))  {
                            try {
                                if(reader.docFreq(new Term(field, text)) < 5) {
                                    String[] suggestions = spellChecker.suggestSimilar(text, 10, reader, Fields.CONTENT_UNSTEMMED, true);
                                    if(suggestions.length > 0) {
                                        suggestionList.add(suggestions[0]);
                                        return new TermQuery(new Term(field, "<i>" +suggestions[0] +"</i>"));
                                    }
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        return super.getFieldQuery(field, text);
                    }
                };

                Query suggestion = suggestingParser.parse(suggestionbase);
                if(suggestionList.size()> 0) {
                    String s = suggestion.toString(Fields.CONTENT).replaceAll("\\+", "");
                    map.put("suggestionHtml", s);
                    map.put("suggestion", s.replaceAll("<i>", "").replaceAll("</i>", ""));
                }
            }

            List indices = new ArrayList();

            List searchPageUrls = new ArrayList();
            int pageNo = 1;
            for (int i = 0; i < hits.length(); i += hitsPerPage) {
                indices.add(i);
                // Legger inn liste med URL til de ulike sidene kun dersom det finnes flere sider
                if (hits.length() > hitsPerPage) {
                    searchPageUrls.add(url + "&idx=" + i);
                }
            }

            map.put("searchPageUrls", searchPageUrls);

            try {
                startIndex = Integer.parseInt(pageContext.getRequest().getParameter("idx"));
            } catch (Exception e) {
                startIndex = 0;
            }
            if (startIndex < 0 || startIndex >= hits.length()) {
                startIndex = 0;
            }

            map.put("startIndex", startIndex);

            if (startIndex > 0) {
                int prevIndex = (startIndex - hitsPerPage >= 0) ? startIndex - hitsPerPage : 0;
                map.put("prevIndex", prevIndex);
                map.put("prevSearchPageUrl", url + "&idx=" + prevIndex);
            }

            endIndex = startIndex + hitsPerPage - 1;

            if (endIndex >= hits.length() - 1) {
                endIndex = hits.length() - 1;
            } else {
                map.put("nextIndex", startIndex + hitsPerPage);
                map.put("nextSearchPageUrl", url + "&idx=" + (startIndex + hitsPerPage));
            }

            map.put("endIndex", endIndex);

            map.put("firstHit", startIndex + 1);
            map.put("lastHit", endIndex + 1);

            map.put("searchHits", buildHitList(bq, analyzer, request, hits, startIndex, endIndex, selector));
            map.put("hits", hits); // Kompabilitet med eldre versjoner

            map.put("indices", indices);

            Iterator i = map.keySet().iterator();
            while (i.hasNext()) {
                String k = (String) i.next();
                pageContext.getRequest().setAttribute(k, map.get(k));
            }

            if (view != null && view.length() > 0) {
                pageContext.include(view);
                return SKIP_BODY;
            }


        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);
            throw new JspException(e);
        }*/

        return SKIP_BODY;
    }

/*
    private List buildHitList(Query q, Analyzer analyzer, HttpServletRequest request, Hits hits, int startIndex, int endIndex, DocumentProviderSelector selector) throws SystemException, IOException {
        List<SearchHit> searchHits = new ArrayList<SearchHit>();

        AksessSearchHitContext context = new AksessSearchHitContext();
        context.setSecuritySession(SecuritySession.getInstance(request));
        context.setSiteId(siteId);
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setQuery(q);
        queryInfo.setAnalyzer(analyzer);
        context.setQueryInfo(queryInfo);

        for (int i = startIndex; i <= endIndex; i++) {
            Document doc = hits.doc(i);

            String docType = doc.get(Fields.DOCTYPE);
            DocumentProvider provider = selector.selectByDocumentType(docType);


            SearchHit sHit = provider.createSearchHit();
            if (sHit instanceof AksessSearchHit) {
                AksessSearchHit searchHit = (AksessSearchHit)sHit;
                searchHit.setDocument(doc);
                searchHit.setTitle(doc.get(Fields.TITLE));
                searchHit.setSummary(doc.get(Fields.SUMMARY));

                try {
                    searchHit.setLastModified(DateTools.stringToDate(doc.get(Fields.LAST_MODIFIED)));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                provider.processSearchHit(sHit, context, doc);
                searchHits.add(sHit);
            } catch (Exception e) {
                request.setAttribute("excludedHits", Boolean.TRUE);
            }

        }

        return searchHits;
    }*/


    public int doEndTag() throws JspException {
        query = null;
        parent = 0;
       // operator = QueryParser.AND_OPERATOR;
        lang = -1;
        notlang = -1;
        queryref = null;
        excludequeryref = null;
        view = null;

        requiredwords = null;
        prohibitedwords = null;
        phrase = null;
        siteId = -1;

        return EVAL_PAGE;
    }


    public void setQuery(String query) {
        this.query = query;
    }

    public void setView(String view) {
        this.view = view;
    }

    public void setQueryref(String queryref) {
        this.queryref = queryref;
    }

    public void setExcludequeryref(String excludequeryref) {
        this.excludequeryref = excludequeryref;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }


    public void setLang(int lang) {
        this.lang = lang;
    }

    public void setNotlang(int notlang) {
        this.notlang = notlang;
    }

    public void setOperator(int operator) {
        if (operator != 1 && operator != 0) {
            throw new IllegalArgumentException(operator + " is not a legal default operator");
        }
       // this.operator = operator == 1 ? QueryParser.Operator.AND : QueryParser.Operator.OR;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public void setRequiredwords(String requiredwords) {
        if (requiredwords != null) {
            requiredwords = requiredwords.toLowerCase();
        }
        this.requiredwords = requiredwords;
    }

    public void setProhibitedwords(String prohibitedwords) {
        if (prohibitedwords != null) {
            prohibitedwords = prohibitedwords.toLowerCase();
        }
        this.prohibitedwords = prohibitedwords;
    }

    public void setPhrase(String phrase) {
        if (phrase != null) {
            phrase = phrase.toLowerCase();
        }
        this.phrase = phrase;
    }

    public void setSiteid(int siteid) {
        this.siteId = siteid;
    }

    public void setHitsperpage(String hitsPerPage) {
        this.hitsPerPage = Integer.parseInt(hitsPerPage, 10);
    }


    public void setSortreversed(boolean sortreversed) throws JspException {
        this.sortreversed = sortreversed;
    }

    public void setSortfield(String sortfield) throws JspException {
        this.sortfield = sortfield;
        if("".equals(this.sortfield)) {
            this.sortfield = null;
        }
    }

    public void setSuggestionbase(String suggestionbase) {
        this.suggestionbase = suggestionbase;
    }

    public void setNumhitsforsuggestion(int numhitsforsuggestion) {
        this.numhitsforsuggestion = numhitsforsuggestion;
    }

    public void setDoctypes(String doctypes) {
        if (doctypes == null || doctypes.trim().equals("")) {
            this.doctypes = null;
        } else {
            this.doctypes = doctypes;
        }
    }
}
