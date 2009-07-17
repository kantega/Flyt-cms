<%@ page import="no.kantega.publishing.common.util.MultimediaHelper" %>
<%@ page import="no.kantega.publishing.common.data.Multimedia" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
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
<kantega:section id="title">
    <kantega:label key="aksess.edit.title"/>
</kantega:section>

<kantega:section id="content">
    <script language="Javascript" type="text/javascript">
        var hasSubmitted = false;

        function startImageCrop() {
            $('#MediaObject img').Jcrop({
                onChange: updateCoords,
                onSelect: updateCoords
            });
            $('#CropInfo').show();
        }

        function updateCoords(c)
        {
            $('#cropx').val(c.x);
            $('#cropy').val(c.y);
            $('#x2').val(c.x2);
            $('#y2').val(c.y2);
            $('#cropwidth').val(c.w);
            $('#cropheight').val(c.h);
            if (c.h == 0 && c.w == 0) {
                $('#CropInfo').html('<kantega:label key="aksess.multimedia.crop.info"/>');
            } else {
                $('#CropInfo').html('<kantega:label key="aksess.multimedia.crop.size"/>: <strong>' + c.w + "</strong> x <strong>" + c.h + "</strong>");
            }
        };


        function saveForm() {
            if ("${media.id}" != "-1" && document.myform.elements['name'].value == "") {
                alert('<kantega:label key="aksess.multimedia.name.missing"/>');
                return;
            }

            if (${altNameRequired} && document.myform.elements['altname'].value == "") {
            alert('<kantega:label key="aksess.multimedia.altname.missing"/>');
            return;
        }

            if (${descriptionRequired} && document.myform.elements['description'].value == "") {
            alert('<kantega:label key="aksess.multimedia.description.missing"/>');
            return;
        }

            if (!hasSubmitted) {
                hasSubmitted = true;
                document.myform.submit();
            }
        }

        $(document).ready(function(){
            document.myform.elements['name'].focus();
        });
    </script>

    <div id="CropInfo" class="info" style="display:none;">
        <kantega:label key="aksess.multimedia.crop.info"/>
    </div>
    <div id="MediaObject">
        <%=MultimediaHelper.mm2HtmlTag((Multimedia)request.getAttribute("media"))%>
    </div>
</kantega:section>

<kantega:section id="sidebar">
    <form name="myform" action="EditMultimedia.action" method="post" enctype="multipart/form-data">
        <input type="hidden" name="id" value="${media.id}">
        <input type="hidden" id="cropx" name="cropx" value="-1">
        <input type="hidden" id="cropy" name="cropy" value="-1">
        <input type="hidden" id="cropwidth" name="cropwidth" value="-1">
        <input type="hidden" id="cropheight" name="cropheight" value="-1">


        <div class="fieldset">
            <fieldset>
                <legend><kantega:label key="aksess.multimedia.medianame"/></legend>
                <input type="text" class="fullWidth" name="name" value="<c:out value="${media.name}"/>" maxlength="255">
            </fieldset>
        </div>
        <div class="fieldset">
            <fieldset>
                <legend><kantega:label key="aksess.multimedia.author"/></legend>
                <input type="text" class="fullWidth" name="altname" value="<c:out value="${media.altname}"/>" maxlength="255">
            </fieldset>
        </div>
        <div class="fieldset">
            <fieldset>
                <legend><kantega:label key="aksess.multimedia.author"/></legend>
                <input type="text" class="fullWidth" name="author" value="<c:out value="${media.author}"/>" maxlength="255">
            </fieldset>
        </div>

        <c:if test="showDimension">
            <div class="fieldset">
                <fieldset>
                    <legend><kantega:label key="aksess.multimedia.size"/></legend>
                    <label for="width"><kantega:label key="aksess.multimedia.width"/></label> <input type="text" size="5" id="width" name="width" value="<c:if test="${media.width > 0}">${media.width}</c:if>">
                    <label for="height"><kantega:label key="aksess.multimedia.height"/></label> <input type="text" size="5" id="height" name="height" value="<c:if test="${media.height > 0}">${media.height}</c:if>">
                    <c:if test="${showDimensionInfo}">
                        <div class="info"><kantega:label key="aksess.multimedia.sizeinfo"/></div>
                    </c:if>
                </fieldset>
            </div>
        </c:if>

        <div class="fieldset">
            <fieldset>
                <legend><kantega:label key="aksess.multimedia.usage"/></legend>
                <textarea name="usage" rows="4" cols="20" class="fullWidth" wrap="soft"><c:out value="${media.usage}"/></textarea>
            </fieldset>
        </div>
        <div class="fieldset">
            <fieldset>
                <legend><kantega:label key="aksess.multimedia.description"/></legend>
                <textarea name="description" rows="4" cols="20" class="fullWidth" wrap="soft"><c:out value="${media.description}"/></textarea>
            </fieldset>
        </div>

        <c:if test="${not empty usages}">
            <div class="fieldset">
                <fieldset>
                    <legend><kantega:label key="aksess.multimedia.pages.using"/></legend>
                    <ul id="MultimediaPagesUsing">
                        <c:forEach items="${usages}" var="page">
                            <li>
                                <a href="${page.url}" target="_new">${page.title}</a>
                            </li>
                        </c:forEach>
                    </ul>
                </fieldset>
            </div>
        </c:if>
    </form>
</kantega:section>
<%@ include file="../layout/multimediaLayout.jsp" %>