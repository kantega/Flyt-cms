<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

<table class="fullWidth">
    <thead>
    <tr>
        <th></th>
        <th class="number"><kantega:label key="aksess.contentstatistics.count"/></th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td><kantega:label key="aksess.contentstatistics.numberofpages"/></td>
        <td class="number"><c:out value="${contentCount}"/></td>
    </tr>
    <tr>
        <td><kantega:label key="aksess.contentstatistics.numberoflinks"/></td>
        <td class="number"><c:out value="${linkCount}"/></td>
    </tr>
    <tr>
        <td><kantega:label key="aksess.contentstatistics.numberofmedia"/></td>
        <td class="number"><c:out value="${multimediaCount}"/></td>
    </tr>
    <tr>
        <td><kantega:label key="aksess.contentstatistics.numberofcontentproducers"/></td>
        <td class="number"><c:out value="${contentProducerCount}"/></td>
    </tr>
    </tbody>
</table>
