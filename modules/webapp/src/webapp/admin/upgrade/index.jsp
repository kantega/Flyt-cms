
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
  <head><title>Upgrade Aksess</title></head>
  <body>

  <h1>Select version to upgrade</h1>
  <ul>
      <li><a href="upgrade-512x-5127.jsp">Oppgrader fra 5.12.0-6 til 5.12.7</a></li>
      <li><a href="upgrade-513x-514x.jsp">Oppgrader fra 5.13.X til 5.14.0 - databaseendringer</a></li>
      <li><a href="../tools/generatemultimediausage.jsp">Oppgrader fra 5.13.X til 5.14.0 - generer bildebruk (må gjøres etter alle andre databaseendringer)</a></li>
      <li><a href="upgrade-514x-515x.jsp">Oppgrader fra 5.14.X til 5.15.0 - databaseendringer</a></li>
      <li><a href="upgrade-60x-61x.jsp">Oppgrader fra 6.0.X til 6.1.X - databasechanges new forms module</a></li>
      <li><a href="upgrade-61x-62x.jsp">Upgrade from 6.1.X to 6.2.X - databasechanges</a></li>
      <li><a href="upgrade-62x-70x.jsp">Upgrade from 6.2.X to 7.0.X - databasechanges</a></li>
      <li><a href="multimediafolderandfilecount.jsp">Upgrade from 6.2.X to 7.0.X - multimedia files and folders count</a></li>
      <li><a href="upgrade-70x-71x.jsp">Upgrade from 7.0.X to 7.1.X - databasechanges</a></li>
  </ul>

  <h1>Export database templates to XML</h1>
  <ul>
      <li><a href="export-xmltemplates.jsp">Creates XML config from database</a> (Save as /WEB-INF/aksess-templateconfig.xml in your project)</li>
  </ul>

  </body>
</html>