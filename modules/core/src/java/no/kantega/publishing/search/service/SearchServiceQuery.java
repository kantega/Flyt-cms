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

package no.kantega.publishing.search.service;

import no.kantega.commons.log.Log;
import no.kantega.publishing.search.SearchField;
import no.kantega.search.index.Fields;
import no.kantega.search.query.hitcount.HitCountQuery;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Date: Jan 28, 2009
 * Time: 8:55:45 AM
 *
 * @author Tarje Killingberg
 */
public class SearchServiceQuery {

    private static final String SOURCE = SearchServiceQuery.class.getName();

    public static final String PARAM_SEARCH_PHRASE = "q";
    public static final String PARAM_DOCUMENT_TYPE = Fields.DOCUMENT_TYPE_ID;
    public static final String PARAM_CONTENT_TEMPLATE = Fields.CONTENT_TEMPLATE_ID;
    public static final String PARAM_EXCLUDED_CONTENT_TEMPLATE = "Excluded" + Fields.CONTENT_TEMPLATE_ID;
    public static final String PARAM_DOCTYPE = Fields.DOCTYPE;
    public static final String PARAM_CONTENT_PARENT = Fields.CONTENT_PARENTS;
    public static final String PARAM_EXCLUDED_CONTENT_PARENT = "Excluded" + Fields.CONTENT_PARENTS;
    public static final String PARAM_LAST_MODIFIED_FROM = Fields.LAST_MODIFIED + "_fra";
    public static final String PARAM_LAST_MODIFIED_TO = Fields.LAST_MODIFIED + "_til";
    public static final String PARAM_SITE_ID = Fields.SITE_ID;
    public static final String PARAM_LANGUAGE = Fields.LANGUAGE;
    public static final String PARAM_THIS_ID = "thisId";
    
    public static final String METAPARAM_PAGE = "page";
    public static final String METAPARAM_HITS_PER_PAGE = "hpp";
    public static final String METAPARAM_ORDERBY = "orderby";
    public static final String METAPARAM_SORTORDER = "sortorder";

    private static int defaultPage = 0;
    private static int defaultHitsPerPage = 10;
    private static DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");

    private HttpServletRequest request;
    private Map<String, String> searchParams;
    private Map<String, String> metaParams;
    private List<HitCountQuery> hitCountQueries = new ArrayList<HitCountQuery>();
    private List<SearchField> customSearchFields;
    private List<String> paramNames;
    private boolean allowEmptySearchPhrase = false;
    private boolean shouldGetContentDocument = true;

    {
        paramNames = new ArrayList<String>();
        paramNames.add(PARAM_SEARCH_PHRASE);
        paramNames.add(PARAM_DOCUMENT_TYPE);
        paramNames.add(PARAM_CONTENT_TEMPLATE);
        paramNames.add(PARAM_EXCLUDED_CONTENT_TEMPLATE);
        paramNames.add(PARAM_DOCTYPE);
        paramNames.add(PARAM_CONTENT_PARENT);
        paramNames.add(PARAM_EXCLUDED_CONTENT_PARENT);
        paramNames.add(PARAM_LAST_MODIFIED_FROM);
        paramNames.add(PARAM_LAST_MODIFIED_TO);
        paramNames.add(PARAM_SITE_ID);
        paramNames.add(PARAM_LANGUAGE);
        paramNames.add(PARAM_THIS_ID);
    }


    public SearchServiceQuery(HttpServletRequest request) {
        this(request, new ArrayList<SearchField>());
    }

    public SearchServiceQuery(HttpServletRequest request, List<SearchField> customSearchFields) {
        this.request = request;
        this.customSearchFields = customSearchFields;
        if (customSearchFields != null) {
            for (SearchField field : customSearchFields) {
                paramNames.add(field.getFieldname());
            }
        }
        searchParams = new HashMap<String, String>();
        for (String paramName : paramNames) {
            String paramValue = getString(request.getParameter(paramName), null);
            if (paramValue != null) {
                searchParams.put(paramName, paramValue);
            }
        }
        metaParams = new HashMap<String, String>();
        metaParams.put(METAPARAM_PAGE, request.getParameter(METAPARAM_PAGE));
        metaParams.put(METAPARAM_HITS_PER_PAGE, request.getParameter(METAPARAM_HITS_PER_PAGE));
        metaParams.put(METAPARAM_ORDERBY, request.getParameter(METAPARAM_ORDERBY));
        metaParams.put(METAPARAM_SORTORDER, request.getParameter(METAPARAM_SORTORDER));
    }

    /**
     * Returnerer en liste med alle parameternavn som skal være med i søket.
     *
     * @return en liste med alle parameternavn som skal være med i søket
     */
    public List<String> getParamNames() {
        return paramNames;
    }

    /**
     * Legger til en søkeparameter til dette Query'et. Hvis en parameter med dette navnet allerede finnes, blir dette
     * overskrevet.
     *
     * @param name navnet til parameteren
     * @param value verdien til parameteren
     */
    public void putSearchParam(String name, String value) {
        searchParams.put(name, value);
    }

    /**
     * Returnerer parameteren med det gitte navnet.
     *
     * @param name navnet på parameteren
     * @return parameteren med det gitte navnet
     */
    public String getStringParam(String name) {
        return getString(searchParams.get(name), null);
    }

    /**
     * Returnerer parameteren med det gitte navnet.
     *
     * @param name navnet på parameteren
     * @return parameteren med det gitte navnet
     */
    public Integer getIntegerParam(String name) {
        return getInteger(searchParams.get(name), null);
    }

    /**
     * Returnerer parameteren med det gitte navnet.
     *
     * @param name navnet på parameteren
     * @return parameteren med det gitte navnet
     */
    public String getDateParamAsString(String name) {
        Date retVal = getDate(searchParams.get(name), dateFormat, null);
        return retVal != null ? dateFormat.format(retVal) : null;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * Convenience method for getStringParam(PARAM_SEARCH_PHRASE)
     *
     * @return det samme som om getStringParam(String) ble kalt med SearchServiceQuery.PARAM_SEARCH_PHRASE som parameter
     */
    public String getSearchPhrase() {
        return getStringParam(PARAM_SEARCH_PHRASE);
    }

    /**
     * Adds a metaparameter to this query. Overwrites any existing parameter
     * with the same name.
     *
     * @param name the parameter's name
     * @param value the parameter's value
     */
    public void putMetaParam(String name, String value) {
        metaParams.put(name, value);
    }

    public int getPage() {
        return getPositiveInteger(metaParams.get(METAPARAM_PAGE), defaultPage);
    }

    public int getHitsPerPage() {
        return getStrictlyPositiveInteger(metaParams.get(METAPARAM_HITS_PER_PAGE), defaultHitsPerPage);
    }

    public String getOrderBy() {
        String orderby = metaParams.get(METAPARAM_ORDERBY);
        if ("title".equalsIgnoreCase(orderby)) {
            orderby = Fields.TITLE_UNANALYZED;
        } else if ("modified".equalsIgnoreCase(orderby)) {
            orderby = Fields.LAST_MODIFIED;
        }
        return orderby;
    }

    public boolean isSortReverse() {
        String sortorder = metaParams.get(METAPARAM_SORTORDER);
        return null != sortorder && ("desc".equalsIgnoreCase(sortorder) || "descending".equalsIgnoreCase(sortorder));
    }

    public int getFromIndex() {
        return getPage() * getHitsPerPage();
    }

    public int getToIndex() {
        return getFromIndex() + getHitsPerPage();
    }

    public void addHitCountQuery(HitCountQuery hitCountQuery) {
        hitCountQueries.add(hitCountQuery);
    }

    public List<HitCountQuery> getHitCountQueries() {
        return hitCountQueries;
    }

    public List<SearchField> getCustomSearchFields() {
        return customSearchFields;
    }

    protected static String getString(String s, String defaultValue) {
        return emptyOrWhitespace(s) ? defaultValue : s;
    }
    
    protected static Integer getStrictlyPositiveInteger(String s, Integer defaultValue) {
        Integer retVal = getInteger(s, null);
        return retVal == null || retVal <= 0 ? defaultValue : retVal;
    }

    protected static Integer getPositiveInteger(String s, Integer defaultValue) {
        Integer retVal = getInteger(s, null);
        return retVal == null || retVal < 0 ? defaultValue : retVal;
    }

    protected static Integer getInteger(String s, Integer defaultValue) {
        Integer retVal = defaultValue;
        if (!emptyOrWhitespace(s)) {
            try {
                retVal = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                Log.error(SOURCE, e, null, null);
            }
        }
        return retVal;
    }

    protected static Date getDate(String s, DateFormat dateFormat, Date defaultValue) {
        Date retVal = defaultValue;
        if (!emptyOrWhitespace(s)) {
            try {
                retVal = dateFormat.parse(s);
            } catch (ParseException e) {
                Log.error(SOURCE, e, null, null);
            }
        }
        return retVal;
    }

    protected static boolean emptyOrWhitespace(String s) {
        return s == null || s.trim().equals("");
    }

    public void setAllowEmptySearchPhrase(boolean allowEmptySearchPhrase) {
        this.allowEmptySearchPhrase = allowEmptySearchPhrase;
    }

    public boolean isAllowEmptySearchPhrase() {
        return allowEmptySearchPhrase;
    }

    public boolean isShouldGetContentDocument() {
        return shouldGetContentDocument;
    }

    public void setShouldGetContentDocument(boolean shouldGetContentDocument) {
        this.shouldGetContentDocument = shouldGetContentDocument;
    }
}
