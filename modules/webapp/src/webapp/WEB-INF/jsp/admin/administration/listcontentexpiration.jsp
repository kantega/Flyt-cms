<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%--
  ~ Copyright 2009 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<kantega:section id="title">
    <kantega:label key="aksess.contentexpire.title"/>
</kantega:section>

<kantega:section id="content">
    <form name="myform" action="ListContentExpiration.action" method="post">
        <div class="fieldset">
            <fieldset>
                <legend><kantega:label key="aksess.contentexpire.title"/></legend>

                <div class="formElement">
                    <div class="heading"><kantega:label key="aksess.contentexpire.period"/></div>

                    <div class="inputs">
                        <div id="FromDate">
                            <label for="from_date"><kantega:label key="aksess.publishinfo.period.from"/></label>
                            <input type="text" id="from_date" name="from_date" size="10" maxlength="10" value="<admin:formatdate date="${expireFromDate}"/>">
                            <a href="#" id="chooseFromDate" class="dateselect"></a>
                        </div>
                        <script type="text/javascript">
                            Calendar.setup( { inputField  : "from_date", ifFormat : "%d.%m.%Y", button : "chooseFromDate", firstDay: 1 } );
                        </script>
                        <div id="EndDate">
                            <label for="end_date"><kantega:label key="aksess.publishinfo.period.until"/></label>
                            <input type="text" id="end_date" name="end_date" size="10" maxlength="10" value="<admin:formatdate date="${expireToDate}"/>">
                            <a href="#" id="chooseEndDate" class="dateselect"></a>
                        </div>
                        <script type="text/javascript">
                            Calendar.setup( { inputField  : "end_date", ifFormat : "%d.%m.%Y", button : "chooseEndDate", firstDay: 1 } );
                        </script>
                    </div>

                </div>

                <div class="buttonGroup">
                    <input type="submit" class="button ok" value="<kantega:label key="aksess.button.ok"/>">
                </div>

                <table>
                    <tr>
                        <th><kantega:label key="aksess.contentexpire.date"/></th>
                        <th><kantega:label key="aksess.contentexpire.page"/></th>
                    </tr>
                    <aksess:getcollection findall="true" name="pages" skipattributes="true" showexpired="true" expirefromdate="${expireFromDate}" expiretodate="${expireToDate}" orderby="expiredate" varStatus="status">
                        <tr class="tableRow${status.index mod 2}">
                            <td><aksess:getattribute name="expiredate" collection="pages"/></td>
                            <td><aksess:link collection="pages" target="_new"><aksess:getattribute name="title" collection="pages"/></aksess:link></td>
                        </tr>
                    </aksess:getcollection>
                </table>

                <div class="helpText">
                    <kantega:label key="aksess.contentexpire.help"/>
                </div>

            </fieldset>
        </div>
    </form>

</kantega:section>
<%@ include file="../layout/administrationLayout.jsp" %>
