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

<kantega:section id="editbuttons">
    <span class="barButton"><input type="submit" class="save" value="<kantega:label key="aksess.button.save"/>"></span>
    <span class="barButton"><input type="submit" class="cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
</kantega:section>

<kantega:section id="content">
    <script language="Javascript" type="text/javascript">
        var hasSubmitted = false;

        var currentMapArea = -1;
        var currentRow = -1;

        function startNewBox(x, y) {
            debug("start box:" + x + "," + y);
            if (detectCollision(x, y, x + 10, y + 10)) {
                currentMapArea = -1;
            } else {
                var rowCount = $('#ImageMapTable tr').length;
                addBox(rowCount, x, y, x + 10, y + 10);
                currentMapArea = rowCount;
            }
        }

        function endNewBox(x, y, ref) {
            if (currentMapArea != -1) {
                var current = $("#map" + currentMapArea);
                var pos = current.position();
                var startX = pos.left - ref.offsetLeft;
                var startY = pos.top - ref.offsetTop;

                var endX = startX + current.width();
                var endY = startY + current.height();

                addRow(currentMapArea, startX, startY, endX, endY, 'http://', '', false);
            }
            currentMapArea = -1;

        }

        function updateBox(x, y) {
            if (currentMapArea != -1) {
                var current = $("#map" + currentMapArea);
                var pos = current.position();
                var startX = pos.left;
                var startY = pos.top;
                if (x < startX + 10) {
                    x = startX + 10;
                }
                if (y < startY + 10) {
                    y = startY + 10;
                }

                // Make sure user does not drag outside right edge of image
                var ref = $("#MediaObject img");
                var maxX = ref.position().left + ref.width();
                if (x > maxX) {
                    x = maxX;
                }

                if (!detectCollision(startX, startY, x, y)) {
                    current.width(x - startX);
                    current.height(y - startY);
                }                
            }
        }

        function detectCollision(startX, startY, stopX, stopY) {
            var overlap = false;
            $("#MediaObject div").each(function() {
                var id = $(this).attr('id');
                if (id != "map" + currentMapArea) {
                    var pos = $(this).position();
                    var boxStartX = pos.left;
                    var boxStartY = pos.top;
                    var boxStopX = boxStartX + $(this).width();
                    var boxStopY = boxStartY + $(this).height();
                    if ((startX < boxStopX && stopX > boxStartX) && (startY < boxStopY && stopY > boxStartY)) {
                        debug("overlap with:" + id);
                        overlap = true;
                    }                    
                }
            });

            return overlap;
        }


        function addRow(no, startX, startY, endX, endY, url, altTitle, newWindow) {
            var id = 'imagemapRow' + no;
            var html = '<tr class="imagemap' + no + '" id="' + id + '">';
            var coords = startX + ',' + startY + ',' + endX + ',' + endY;
            html += '<input type="hidden" name="coords' + no + '" value="' + coords + '">';
            html += '<input type="hidden" name="deleted' + no + '" value="false">';
            html += '<td><input type="text" name="url' + no + '" id="url' + no + '" value="' + url + '" class="imageMapAreaUrl">';
            html += '<a href="#"><span class="add"><kantega:label key="aksess.button.choose"/></span></a></td>';
            html += '<td><input type="text" name="altTitle' + no + '" id="altTitle' + no + '" value="' + altTitle + '" class="imageMapAreaAltTitle"></td>';
            html += '<td><input type="checkbox" value="true" name="newWindow' + no + '"';
            if (newWindow) {
                html += ' checked="checked"';
            }
            html += '></td>';
            html += '<td><a href="#"><span class="delete"><kantega:label key="aksess.button.delete"/></span></a></td>';
            html += "</tr>";
            $("#ImageMapTable").append(html);

            $("#" + id + " span.add").click(function() {
                selectPage(no);
            });
            $("#" + id + " span.delete").click(function() {
                // Delete div
                $("#map" + no).remove();
                // Empty all input fields
                $("#imagemapRow" + no + " input").val('');
                // Hide
                $("#imagemapRow" + no).hide();
            });

        }

        function addBox(no, x, y, endX, endY) {
            var w = endX - x;
            var h = endY - y;
            var ie = document.all;
            var css = "position:absolute; left:" + x + "px;top:" + y + "px;width:" + w + "px; height:" + h +"px;";
            css += ie ? 'filter: alpha(opacity=50);' : 'opacity:0.50;';
            var id = "map" + no;
            $('#MediaObject').append('<div id="' + id + '" style="' + css + '"></div>');
            var box = $('#' + id);
            box.addClass('imagemap');
            box.addClass('imagemap' + no);
            box.click(function() {
                $("#url" + no).focus();
            });

        }

        // Åpner navigator for å velge url til innhold i aksess
        function selectPage(row){
            currentRow = row;
            var target = window.open("../popups/selectcontent.jsp" , "contentWindow", "toolbar=no,width=280,height=450,resizable=yes,scrollbars=yes");
            target.focus();
        }


        // Callback fra navigator.jsp som setter inn riktig url
        // TODO: Fix this
        function insertIdAndValueIntoForm(id, text) {
            $("#url" + currentRow).val('/content.ap?thisId=' + id);
            $("#altTitle" + currentRow).val(text);
        }

        function saveForm() {
            if (!document.imagemapform.overwrite[0].checked && !document.imagemapform.overwrite[1].checked) {
                alert("<kantega:label key="aksess.multimedia.crop.overwriteorcreatenew"/>");
                return;
            }
            if (!hasSubmitted) {
                hasSubmitted = true;
                document.imagemapform.submit();
            }
        }


        $(document).ready(function() {
            // Cancel button goes back to displaying image
            $("#EditMultimediaButtons .cancel").click(function (){
                location.href = "EditMultimedia.action?id=${media.id}";
            });

            // Disable save button until something is changed
            $("#EditMultimediaButtons .save").click(function () {
                saveForm();
            });


            $('#MediaObject').mousedown(function(event) {
                var x = event.pageX;
                var y = event.pageY;
                startNewBox(x, y);
                event.preventDefault();
            });

            $('#MediaObject').mouseup(function(event) {
                var x = event.pageX;
                var y = event.pageY;
                endNewBox(x, y, this);
                event.preventDefault();
            });

            $('#MediaObject').mousemove(function(event) {
                var x = event.pageX;
                var y = event.pageY;
                updateBox(x, y);
                event.preventDefault();
            });

            var refPos = $('#MediaObject').position();
            <c:forEach var="c" items="${coordinates}" varStatus="status">
                addBox(${status.index}, refPos.left + ${c.startX}, refPos.top + ${c.startY}, refPos.left + ${c.stopX}, refPos.top + ${c.stopY});
                addRow(${status.index}, ${c.startX}, ${c.startY}, ${c.stopX}, ${c.stopY}, '${c.url}', '${c.altName}', ${c.openInNewWindow});
            </c:forEach>


        });
    </script>

    <div id="ImageMapInfo" class="ui-state-highlight">
        <kantega:label key="aksess.multimedia.imagemap.info"/>
    </div>

    <div id="MediaObject">
        <img id="Image" src="${media.url}" width="${media.width}" height="${media.height}">
    </div>

    <div id="ImageMapContainer">
        <form name="imagemapform" id="ImageMapForm" action="ImageMap.action" method="post">
            <input type="hidden" name="id" value="${media.id}">

            <table id="ImageMapTable">
                <thead>
                        <th class="imageMapLink"><kantega:label key="aksess.multimedia.imagemap.lenke"/></th>
                        <th class="imageMapAltTitle" width="215"><kantega:label key="aksess.multimedia.imagemap.altnavn"/></th>
                        <th class="imageMapNewWindow"><kantega:label key="aksess.multimedia.imagemap.nyttvindu"/></th>
                        <th class="imageMapDelete">&nbsp;</th>
                </thead>
                <tbody>
                </tbody>
            </table>

            <input type="radio" id="overwrite" name="overwrite" value="true" <c:if test="${!allowOverwrite}">disabled</c:if>>
            <label for="overwrite"><kantega:label key="aksess.multimedia.overwrite"/></label><br>

            <input type="radio" id="createnew" name="overwrite" value="false" <c:if test="${!allowOverwrite}">checked="checked"</c:if>>
            <label for="createnew"><kantega:label key="aksess.multimedia.createnew"/></label>
        </form>
    </div>
</kantega:section>

<%@ include file="../layout/multimediaLayout.jsp" %>