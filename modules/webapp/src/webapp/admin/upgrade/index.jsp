
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
  <head><title>Oppgradering av database for Aksess</title></head>
  <body>

  <h1>Velg versjon</h1>
  <ul>
      <li><a href="upgrade-512x-5127.jsp">Oppgrader fra 5.12.0-6 til 5.12.7</a></li>
      <li><a href="upgrade-513x-514x.jsp">Oppgrader fra 5.13.X til 5.14.0 - databaseendringer (trinn 1)</a></li>
      <li><a href="../tools/generatemultimediausage.jsp">Oppgrader fra 5.13.X til 5.14.0 - generer bildebruk (trinn 2)</a></li>
      <li><a href="upgrade-514x-515x.jsp">Oppgrader fra 5.14.X til 5.15.0 - databaseendringer</a></li>
  </ul>

  <h1>Eksport av databasemaler til XML</h1>
  <ul>
      <li><a href="export-xmltemplates.jsp">Lager XML config fra database</a> (Lagre som /WEB-INF/aksess-templateconfig.xml i prosjektet)</li>
  </ul>

  </body>
</html>