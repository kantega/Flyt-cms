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

<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin"%>
<%@ taglib prefix="formadmin" uri="http://www.kantega.no/aksess/tags/formadmin" %>

<formadmin:printformadminnavigator root="${navigatorContent}"/>

<div id="NavigatorState" style="display: none;">
    <div class="currentInstance">${currentInstance}</div>
    <div class="currentState">${currentState}</div>
    <div class="openFolders">${openInstances}</div>
</div>