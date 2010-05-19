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
    <script type="text/javascript">
        $(function() {
            $("#from_date").datepicker();
            $("#end_date").datepicker();
        });
    </script>

    <form name="myform" action="ListContentExpiration.action" method="post">
        <div class="fieldset">
            <fieldset>
                <h1><kantega:label key="aksess.contentexpire.title"/></h1>

                <div class="formElement">
                    <div class="heading"><kantega:label key="aksess.contentexpire.period"/></div>

                    <div class="inputs">
                        <table class="noborder">
                            <tr>
                                <td><label for="from_date"><kantega:label key="aksess.publishinfo.period.from"/></label></td>
                                <td><input type="text" id="from_date" name="from_date" size="10" maxlength="10" value="<admin:formatdate date="${expireFromDate}"/>"></td>
                            </tr>
                            <tr>
                                <td><label for="end_date"><kantega:label key="aksess.publishinfo.period.until"/></label></td>
                                <td><input type="text" id="end_date" name="end_date" size="10" maxlength="10" value="<admin:formatdate date="${expireToDate}"/>"></td>
                            </tr>
                        </table>
                    </div>
                </div>
                <div class="buttonGroup">
                    <span class="button"><input type="submit" class="ok" value="<kantega:label key="aksess.button.ok"/>"></span>
                </div>
                <aksess:ifcollectionnotempty findall="true" name="pages" skipattributes="true" showexpired="true" expirefromdate="${expireFromDate}" expiretodate="${expireToDate}" orderby="expiredate">
                    <table style="margin-top:20px;" class="fullWidth">
                        <tr>
                            <th class="date"><kantega:label key="aksess.contentexpire.date"/></th>
                            <th><kantega:label key="aksess.contentexpire.page"/></th>
                        </tr>
                        <aksess:getcollection name="pages" varStatus="status">
                            <tr class="tableRow${status.index mod 2}">
                                <td><aksess:getattribute name="expiredate" collection="pages"/></td>
                                <td><aksess:link collection="pages" target="_new"><aksess:getattribute name="title" collection="pages"/></aksess:link></td>
                            </tr>
                        </aksess:getcollection>
                    </table>
                </aksess:ifcollectionnotempty>
                <div class="ui-state-highlight">
                    <kantega:label key="aksess.contentexpire.help"/>
                </div>
            </fieldset>
        </div>
    </form>

</kantega:section>
<%@ include file="../layout/administrationLayout.jsp" %>
