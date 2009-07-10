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

<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>


<kantega:section id="title">
    <kantega:label key="aksess.multimedia.title"/>
</kantega:section>

<kantega:section id="content">
    <div id="Content">
        <div id="Navigation">

            <div id="Navigator"></div>
            <div id="Framesplit">
                <div id="FramesplitDrag"></div>
            </div>
        </div>

        <div id="MainPane">
            <div id="MultimediaFolders"></div>
        </div>
    </div>

</kantega:section>


<%@include file="../layout/multimediaLayout.jsp"%>