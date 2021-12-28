<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%--
  ~ Copyright 2009 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<kantega:section id="body">
    <script type="text/javascript">
        $(document).ready(function() {
            $('a.linkexternal').click(function() {
                window.open(this.href);
                return false;
            });
        })
    </script>
    <script type="text/javascript" src="http://konami-js.googlecode.com/svn/trunk/konami.js"></script>
    <script type="text/javascript">
        konami = new Konami()
        konami.load("http://www.youtube.com/watch?v=ZWByXDB0IKw");
    </script>

    <h1>
        <a class="linkexternal" href="http://opensource.kantega.no/aksess">Flyt CMS</a> ${aksessVersion} (<kantega:label key="aksess.systeminfo.revision.aksess"/> ${aksessRevision} / <kantega:label key="aksess.systeminfo.revisjon.webapp"/> ${webappRevision})
    </h1>
                <pre id="Licence">
  ~ Copyright 2005-2010 <a class="linkexternal" href="http://www.kantega.no">Kantega AS</a>
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     <a class="linkexternal" href="http://www.apache.org/licenses/LICENSE-2.0">http://www.apache.org/licenses/LICENSE-2.0</a>
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
                </pre>
</kantega:section>

<%@ include file="../layout/popupLayout.jsp" %>