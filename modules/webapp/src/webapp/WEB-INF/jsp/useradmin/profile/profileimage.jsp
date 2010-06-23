<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="kangega" uri="http://www.kantega.no/aksess/tags/commons" %>
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

<kantega:section id="head">

</kantega:section>

<kantega:section id="innhold">

    <%@ include file="/admin/include/infobox.jsp" %>

    <form action="profileimage" method="post" enctype="multipart/form-data">
        <fieldset>

            <legend><kangega:label key="useradmin.profileimage.legend"/> for ${name}</legend>

            <c:if test="${not empty profileImage}">
                <p class="profileImage">
                    <img src="${profileImage.url}&amp;height=${profileImage.height}&amp;width=${profileImage.width}" alt="${profileImage.name}"/>
                </p>
                <p><input name="delete" type="submit" value="<kangega:label key="useradmin.profileimage.delete"/>" onclick="return confirm('<kantega:label key="useradmin.profileimage.delete.confirm"/>')"></p>
            </c:if>
            <p>
                <label><kangega:label key="useradmin.profileimage.image"/></label>
                <input type="file" name="profileImage">
            </p>
            <p>
                <input type="hidden" name="domain" value="${domain}">
                <input type="hidden" name="userId" value="${userId}">
                <input type="submit" value="<kangega:label key="useradmin.profileimage.upload"/>">
            </p>
        </fieldset>

    </form>


</kantega:section>

<%@ include file="../include/design/standard.jsp" %>