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
            openaksess.common.debug("ImageMapAction: start box:" + x + "," + y);
            if (detectCollision(x, y, x + 10, y + 10)) {
                currentMapArea = -1;
            } else {
                var rowCount = $('#ImageMapTable tr').length;
                addBox(rowCount, x, y, x + 10, y + 10);
                currentMapArea = rowCount;
            }
        }

        function endNewBox(x, y) {
            if (currentMapArea != -1) {
                var current = $("#map" + currentMapArea);
                var pos = current.position();

                var startX = Math.floor( pos.left );
                var startY = Math.floor( pos.top );

                var endX = startX + current.width();
                var endY = startY + current.height();

                addRow(currentMapArea, startX, startY, endX, endY, 'http://', '', false);
            }
            currentMapArea = -1;

        }

        function updateBox(x, y) {
            if (currentMapArea != -1) {
                var current = $("#map" + currentMapArea);

                var pos = $('#map' + currentMapArea).position();

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
                    //x = maxX;
                }

                if (!detectCollision(startX, startY, x, y)) {
                    current.width(Math.round(x - startX));
                    current.height(Math.round(y - startY));
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
                        openaksess.common.debug("ImageMapAction: overlap with:" + id);
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
            html += '<td><input type="hidden" name="coords' + no + '" value="' + coords + '">';
            html += '<input type="hidden" name="deleted' + no + '" value="false">';
            html += '<input type="text" name="url' + no + '" id="url' + no + '" value="' + url + '" class="imageMapAreaUrl">';
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

            $("#" + id + " span.add").click(function(event) {
                selectPage(no);
            });
            $("#" + id + " span.delete").click(function(event) {
                // Delete div
                $("#map" + no).remove();
                // Empty all input fields
                $("#imagemapRow" + no + " input").val('');
                // Hide
                $("#imagemapRow" + no).hide();
            });

        }

        function addBox(no, x, y, endX, endY) {
            var w = Math.round(endX - x);
            var h = Math.round(endY - y);
            var ie = document.all;

            var pos = $('#MediaObject img').position();

            var css = "position:absolute; left:" + Math.round(x) + "px;top:" + Math.round(y) + "px;width:" + w + "px; height:" + h +"px;";
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

        function saveForm() {
            if (!hasSubmitted) {
                hasSubmitted = true;
                document.imagemapform.submit();
            }
        }

        // Open navigator to select content
        function selectPage(row){
            currentRow = row;
            openaksess.editcontext.doInsertTag = false;
            openaksess.common.modalWindow.open({title:'<kantega:label key="aksess.popup.selectcontent" escapeJavascript="true"/>', iframe:true, href: "${pageContext.request.contextPath}/admin/publish/popups/SelectContent.action",width: 400, height:450});
        }

        // Callback from navigator        
        openaksess.editcontext.insertValueAndNameIntoForm = function (id, text) {
            $("#url" + currentRow).val('/content.ap?thisId=' + id);
            $("#altTitle" + currentRow).val(text);
        };

        $(document).ready(function() {
            // Cancel button goes back to displaying image
            $("#EditMultimediaButtons .cancel").click(function (event){
                location.href = "EditMultimedia.action?id=${media.id}";
            });

            // Disable save button until something is changed
            $("#EditMultimediaButtons .save").click(function (event) {
                saveForm();
            });


            var mediaObject = $('#MediaObject');
            mediaObject.mousedown(function(event) {
                var offset = $("#MediaObject").position();
                var x = event.pageX - offset.left;
                var y = event.pageY - offset.top - $("#Top").height();
                startNewBox(x, y);
                event.preventDefault();
            });

            mediaObject.mouseup(function(event) {
                var offset = $("#MediaObject").position();
                var x = event.pageX - offset.left;
                var y = event.pageY - offset.top - $("#Top").height();
                endNewBox(x, y);
                event.preventDefault();
            });

            mediaObject.mousemove(function(event) {
                var offset = $("#MediaObject").position();
                var x = event.pageX - offset.left;
                var y = event.pageY - offset.top - $("#Top").height();
                updateBox(x, y);
                event.preventDefault();
            });

            <c:forEach var="c" items="${coordinates}" varStatus="status">
                addBox(${status.index}, ${c.startX}, ${c.startY}, ${c.stopX}, ${c.stopY});
                addRow(${status.index}, ${c.startX}, ${c.startY}, ${c.stopX}, ${c.stopY}, '${c.url}', '${c.altName}', ${c.openInNewWindow});
            </c:forEach>


        });
    </script>

    <div id="ImageMapInfo" class="ui-state-highlight">
        <kantega:label key="aksess.multimedia.imagemap.info"/>
    </div>

    <div id="MediaObject" style="position:relative">
        <img id="Image" src="${media.url}" width="${media.width}" height="${media.height}">
    </div>

    <div id="ImageMapContainer">
        <form name="imagemapform" id="ImageMapForm" action="ImageMap.action" method="post">
            <input type="hidden" name="id" value="${media.id}">
            <table id="ImageMapTable">
                <thead>
                    <tr>
                        <th class="imageMapLink"><kantega:label key="aksess.multimedia.imagemap.lenke"/></th>
                        <th class="imageMapAltTitle" width="215"><kantega:label key="aksess.multimedia.imagemap.altnavn"/></th>
                        <th class="imageMapNewWindow"><kantega:label key="aksess.multimedia.imagemap.nyttvindu"/></th>
                        <th class="imageMapDelete">&nbsp;</th>
                    </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </form>
    </div>
</kantega:section>

<%@ include file="../layout/editMultimediaLayout.jsp" %>