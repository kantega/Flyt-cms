<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
    <title>...</title>
    <script type="text/javascript" src="${pageContext.request.contextPath}/aksess/js/jquery-1.4.2.min.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            $('a.linkexternal').click(function() {
                window.open(this.href);
                return false;
            });
        })
    </script>
</head>
<body>
 
                <div class="content">
                        <a class="linkexternal" href="http://opensource.kantega.no/aksess">OpenAksess</a> ${aksessVersion} (<kantega:label key="aksess.systeminfo.revisjon.aksess"/> ${aksessRevision} / <kantega:label key="aksess.systeminfo.revisjon.webapp"/> ${webappRevision})
                <pre>
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
                </div>
    


</body>
</html>


