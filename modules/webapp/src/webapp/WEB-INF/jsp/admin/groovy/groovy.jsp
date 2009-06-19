<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

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

<script type="text/javascript">
        window.onload = function() {
            document.getElementById("groovycode").focus();
        }
        function handleKey(e, t) {
                if(e.ctrlKey && e.keyCode == 13) {
                    document.getElementById("groovyform").submit();
                    killEvent(e);
                }
            }
    </script>

    <h1>Groovy console</h1>

    <p>
        Enter some Groovy code:
    </p>
    <form method="post" id="groovyform">
        <textarea style="width:800px;height:200px" id="groovycode" name="code" onkeydown="handleKey(event, this)">${code}</textarea>
        <br/>
        <input type="submit" value="Run!"/>
    </form>


    <c:if test="${returnValue != null}">
        Resultat: ${returnValue}
    </c:if>


    <c:if test="${out != null}">
    <div>
        Output: <br/>
        <textarea style="width:800px;height:200px" wrap="off">${out}</textarea>
    </div>
    </c:if>

    <c:if test="${variables != null}">
    <table>
        <c:forEach items="${variables}" var="entry">
        <tr>
            <td style="font-family: monospace">${entry.key}</td>
            <td>${entry.value}</td>
        </tr>
        </c:forEach>
    </table>
    </c:if>
