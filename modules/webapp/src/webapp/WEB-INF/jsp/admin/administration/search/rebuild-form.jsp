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
    <kantega:label key="aksess.search.title"/>
</kantega:section>

<kantega:section id="content">
    <form action="RebuildIndex.action" name="searchindex" method="POST">
    <div class="fieldset">
        <fieldset>
            <legend><kantega:label key="aksess.search.title"/></legend>

            <input type="checkbox" checked="true" name="rebuild" id="rebuild"><label for="rebuild"><kantega:label key="aksess.search.rebuild.rebuild"/></label><br>
            <input type="checkbox" checked="true" name="optimize" id="optimize"><label for="optimize"><kantega:label key="aksess.search.rebuild.optimize"/></label><br>
            <input type="checkbox" checked="true" name="spelling" id="spelling"><label for="spelling"><kantega:label key="aksess.search.rebuild.spelling"/></label><br>

            <div class="buttonGroup">
                <a href="#" onclick="document.searchindex.submit()" class="button ok"><span><kantega:label key="aksess.button.start"/></span></a>
            </div>
        </fieldset>
    </div>
    </form>

</kantega:section>
<%@ include file="../../layout/administrationLayout.jsp" %>