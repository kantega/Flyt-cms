<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%--
  ~ Copyright 2009 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~  http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License
  --%>

<kantega:section id="head">
    <link rel="stylesheet" type="text/css" href="<%=Aksess.getContextPath()%>/admin/css/multimedia.css">
    <script type="text/javascript" src="<%=Aksess.getContextPath()%>/admin/js/multimedia.jjs"></script>
</kantega:section>

<kantega:section id="topMenu">
    <%@include file="fragments/topMenu.jsp"%>
</kantega:section>

<kantega:section id="modesMenu">

</kantega:section>

<kantega:section id="toolsMenu">

</kantega:section>

<kantega:section id="body">
    <kantega:getsection id="content"/>
</kantega:section>


<%@include file="commonLayout.jsp"%>