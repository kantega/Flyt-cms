<%--
  ~ Copyright 2010 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<kantega:section id="head">
    <link rel="stylesheet" type="text/css" href="<kantega:expireurl url="/wro-oa/admin-formadminlayout.css"/>">
    <script type="text/javascript" src="<kantega:expireurl url="/wro-oa/admin-formadminlayout.js"/>"></script>
    <kantega:getsection id="head extras"/>
</kantega:section>

<kantega:section id="topMenu">
    <%@include file="fragments/topMenu.jsp"%>
</kantega:section>



<kantega:section id="body">
    <div id="Content"<kantega:hassection id="contentclass"> class="<kantega:getsection id="contentclass"/>"</kantega:hassection>>
        <div id="Navigation">
            <div id="Filteroptions">

            </div>
            <div class="infoslider"></div>
            <div id="Navigator"></div>
            <div id="Framesplit"></div>
        </div>


        <div id="MainPane">

            <div id="Statusbar">
                <div id="Breadcrumbs"></div>

            </div>

            <div class="infoslider"></div>

            <kantega:getsection id="content"/>


        </div>
        <div class="clearing"></div>
    </div>

</kantega:section>

<%@include file="commonLayout.jsp"%>