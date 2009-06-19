<%@ page import="no.kantega.publishing.common.ao.ContentAO" %>
<%@ page import="no.kantega.publishing.common.ao.ContentHandler" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ page import="no.kantega.publishing.event.ContentListener" %>
<%@ page import="no.kantega.publishing.jobs.multimedia.MultimediaUsageListener" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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

<html>
  <head><title>Generate multimedia usage</title></head>
  <body>
  <h1>Please wait</h1>
  <%
        ContentAO.forAllContentObjects(new ContentHandler() {
            public void handleContent(Content content) {
                ContentListener listener = new MultimediaUsageListener();
                listener.contentSaved(content);
            }
        }, new ContentAO.ContentHandlerStopper() {

            public boolean isStopRequested() {
                return false;
            }
        });
  %>
  <h1>Generated usage for all documents</h1>
  </body>
</html>