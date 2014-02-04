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

<kantega:section id="editbuttons">
    <span class="barButton"><input type="submit" class="save" value="<kantega:label key="aksess.button.savecrop"/>"></span>
    <span class="barButton"><input type="submit" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
</kantega:section>

<kantega:section id="content">
    <script language="Javascript" type="text/javascript">
        var hasSubmitted = false;

        function updateCoords(c) {
            $('#cropx').val(c.x);
            $('#cropy').val(c.y);
            $('#x2').val(c.x2);
            $('#y2').val(c.y2);
            $('#cropwidth').val(c.w);
            $('#cropheight').val(c.h);
            if (c.h == 0 || c.w == 0) {
                $('#CropInfo').html('<kantega:label key="aksess.multimedia.crop.info"/>');
                enableSaveButton(false);
            } else {
                var aspectRatio = c.w / c.h;                
                var aspectRatioRounded = Math.round(aspectRatio * Math.pow(10,2)) / Math.pow(10,2);
                $('#CropInfo').html('<kantega:label key="aksess.multimedia.crop.size"/>: <strong>' + aspectRatioRounded + "</strong> &nbsp; | &nbsp; " + c.w + " x " + c.h);
                enableSaveButton(true);
            }
        };

        function saveForm() {
            if (!document.cropform.overwrite[0].checked && !document.cropform.overwrite[1].checked) {
                alert("<kantega:label key="aksess.multimedia.crop.overwriteorcreatenew"/>");                
                return;
            }
            if (!hasSubmitted) {
                hasSubmitted = true;
                document.cropform.submit();
            }
        }

        // Show
        function enableSaveButton(active) {
            if (!active) {
                $("#EditMultimediaButtons .save").addClass("disabled");
                $("#EditMultimediaButtons .save").attr("disabled", "disabled");
            } else {
                $("#EditMultimediaButtons .save").removeClass("disabled");
                $("#EditMultimediaButtons .save").removeAttr("disabled");
            }

        }

        $(document).ready(function() {
            // Cancel button goes back to displaying image
            $("#EditMultimediaButtons .cancel").click(function (){
                location.href = "EditMultimedia.action?id=${media.id}";
            });

            // Disable save button until something is changed
            enableSaveButton(false);
            $("#EditMultimediaButtons .save").click(function () {
                saveForm();
            });

            // Enable JQuery crop
            $('#MediaObject img').Jcrop({
                boxWidth: 600,
                boxHeight: 400,
                onChange: updateCoords,
                onSelect: updateCoords
            });

            // Show crop info
            $('#CropInfo').show();
        });
    </script>

    <div id="CropInfo" class="ui-state-highlight" style="display:none;">
        <kantega:label key="aksess.multimedia.crop.info"/>
    </div>
    <div id="MediaObject">
        <img id="Image" src="${media.url}" width="${media.width}" height="${media.height}">
    </div>

    <div id="CropOptions">
        <form name="cropform" id="CropForm" action="ImageCrop.action" method="post">
            <input type="hidden" name="id" value="${media.id}">
            <input type="hidden" id="cropx" name="cropx" value="-1">
            <input type="hidden" id="cropy" name="cropy" value="-1">
            <input type="hidden" id="cropwidth" name="cropwidth" value="-1">
            <input type="hidden" id="cropheight" name="cropheight" value="-1">

            <input type="radio" id="overwrite" name="overwrite" value="true" <c:if test="${!allowOverwrite}">disabled</c:if>>
            <label for="overwrite"><kantega:label key="aksess.multimedia.overwrite"/></label><br>

            <input type="radio" id="createnew" name="overwrite" value="false" <c:if test="${!allowOverwrite}">checked="checked"</c:if>>
            <label for="createnew"><kantega:label key="aksess.multimedia.createnew"/></label>
        </form>
    </div>
</kantega:section>

<%@ include file="../layout/editMultimediaLayout.jsp" %>