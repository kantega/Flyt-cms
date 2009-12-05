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

        function updateCoords(c) {
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


        function sizeImage(dx) {
            var img = document.getElementById("Image");
            img.width = img.width + dx;
            img.height = img.height + dx;
        }

        function saveForm() {

        }

        $(document).ready(function() {
            //startImageCrop();
            $("#SmallerImage").click(function() {
                sizeImage(-10);
            });

            $("#LargerImage").click(function() {
                sizeImage(10);
            });

        });
    </script>

    <input type="button" id="SmallerImage" value="Mindre">
    <input type="button" id="LargerImage" value="Større"><br>

    <div id="CropInfo" class="info" style="display:none;">
        <kantega:label key="aksess.multimedia.crop.info"/>
    </div>
    <div id="MediaObject">
        <img id="Image" src="${media.url}" width="${media.width}" height="${media.height}">
    </div>
</kantega:section>

<kantega:section id="sidebar">
    Hva skal vises her ?
</kantega:section>
<%@ include file="../layout/multimediaLayout.jsp" %>