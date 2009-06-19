<%@ page import="no.kantega.publishing.common.ao.MultimediaImageMapAO"%>
<%@ page import="no.kantega.commons.configuration.Configuration"%>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ include file="../include/jsp_header.jsf" %>
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

<%
    Multimedia mm = (Multimedia)session.getAttribute("currentMultimedia");
    MultimediaImageMap mim = MultimediaImageMapAO.loadImageMap(mm.getId());

    boolean allowOverwrite = false;
    if (securitySession.isUserInRole(Aksess.getAdminRole()) || securitySession.getUser().getId().equalsIgnoreCase(mm.getModifiedBy())) {
        allowOverwrite = true;
    }

    Configuration conf = Aksess.getConfiguration();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>imagemap.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
    <meta http-equiv="Content-type" content="text/html; charset=iso-8859-1" />
</head>
<body>
<script type="text/javascript">
var boxes = new Array();
// IE er litt sær, så noen ting må gjøres spesielt for å tilpasses den.
var ie = document.all;
var currentLink = null; //den man holder på å sette inn lenke på
var start = true;
var selObj;

document.onclick = handleClickEvent;

/*
*  Tvinger lasting av sider
*/
function getRefresh() {
    var dt = new Date();
    return "" + dt.getTime();
}

// Holder oversikt over koordinatene / imagemaps
function CoordUrlMap(){
    this.startX = 0;
    this.startY = 0;
    this.stopX = 0;
    this.stopY = 0;
    this.deleted = false;
    this.url = "";
    this.altName = "";
    this.newWindow = "";
}

//gir Y-koordinaten til bildets øvre startpunkt
function getImageOffsetY(){
    var bilde = document.getElementById('bilde');
    return bilde.offsetTop;
}

// Sjeker hvilket element som det er klikket i og sender det videre hvis det er noe som skal gjøres
function handleClickEvent(e) {
    var num = boxes.length;

    selObj = ie ? event.srcElement: e.target;
    if(selObj.id == "bilde" || selObj.id == "map" + num) {
        start ? startRectangle(e) : endRectangle(e);
        start = !start;
        return;
    }
}

/**
 *   Start på nytt rektangel
 */

function startRectangle(e) {
    var num = boxes.length;
    boxes[num] = new CoordUrlMap();
    boxes[num].startX = ie ? event.clientX  + document.body.scrollLeft : e.pageX;
    boxes[num].startY = ie ? event.clientY + document.body.scrollTop - getImageOffsetY() : e.pageY - getImageOffsetY();

                // Setter opp eventhandler som bare skal være på når det tegnes
    document.onmousemove = updateRectangle;
}

/**
 *   Slutt på nytt rektangel
 */
function endRectangle(e) {
    var num = boxes.length - 1;
    document.onmousemove = null;
    boxes[num].stopX = ie ? event.clientX  + document.body.scrollLeft : e.pageX;
    boxes[num].stopY = ie ? event.clientY + document.body.scrollTop - getImageOffsetY() : e.pageY - getImageOffsetY();
				// Tester for å det er positive størelser
    if(boxes[num].startX < boxes[num].stopX && boxes[num].startY < boxes[num].stopY){
        addRow();
    }
}

function getRectangle(rowNo) {
    var rectDiv = document.getElementById("map" + rowNo);
    if (rectDiv == null) {
        // Lager ny div, setter id og legger den til i treet
        rectDiv = document.createElement('DIV');
        rectDiv.setAttribute('id', 'map' + rowNo);
        document.getElementById('maindiv').appendChild(rectDiv);
    }

    return rectDiv;
}

function removeRectangle(i) {
    document.getElementById('maindiv').removeChild(document.getElementById("map" + i));
}

function updateRectangle(e) {
    var num = boxes.length - 1;
    window.status = new Date();
    selObj = ie ? event.srcElement: e.target;
    var posX = ie ? event.clientX + document.body.scrollLeft : e.pageX;
    var posY = ie ? event.clientY + document.body.scrollTop : e.pageY;
    var posY = posY - getImageOffsetY();
    var sizeX = posX - boxes[num].startX;
    var sizeY = posY - boxes[num].startY;

                // Tester for kollisjon og for at det bare er positive størrelser.  Tegn opp rektangel i såfall
    if(!detectCollision(boxes[num].startX, boxes[num].startY, sizeX, sizeY) && sizeX > 0 && sizeY > 0) {
        boxes[num].stopX = posX;
        boxes[num].stopY = posY;

        drawRectangle(num);
    }
}

// Tegner opp rektangel
function drawRectangle(num) {
    var sizeX = boxes[num].stopX - boxes[num].startX;
    var sizeY = boxes[num].stopY - boxes[num].startY;

    top.window.status = new Date();
                // Flytter rektangelet ned til å stå over der bildet er plassert
    var startY = boxes[num].startY + getImageOffsetY();
    var stopY  = boxes[num].stopY + getImageOffsetY();

    var text = 'width:' + sizeX + 'px;';
    text += 'height:' + sizeY + 'px;';
    text += 'position:absolute;left:' + boxes[num].startX + 'px;top:' + startY + 'px;';

    // Setter gjennomsiktigheten og farge
    text += ie ? 'filter: alpha(opacity=50);' : 'opacity:0.50';

    var rect = getRectangle(num);
    rect.style.cssText = text;
    rect.className = "imagemap" + (num%10);
}

// Tester for om markeringer overlapper
function detectCollision(startX, startY, sizeX, sizeY) {
    for(var i = 0; i < boxes.length-1; i++){
        if(!boxes[i].deleted) {
            if ((startX < boxes[i].stopX && startX + sizeX > boxes[i].startX) && (startY < boxes[i].stopY && startY + sizeY > boxes[i].startY)) {
                return true;
            }
        }
    }
    return false;
}

function addRow() {
    var num = boxes.length - 1;

    var elementsTable = document.getElementById("elementsTable");

                // Legg til rad på slutten av tabellen
    var row = elementsTable.insertRow(-1);
    row.setAttribute('class', 'imagemap' + (num%10));
    row.setAttribute('id', 'row' + num);
    row.className = "imagemap" + (num%10);

                // Koordinater
    var tdKoord = document.createElement("TD");
    row.insertBefore(tdKoord, null);

    var koord = document.createElement('INPUT');
    koord.setAttribute('id', 'value' + num);
    koord.setAttribute('name', 'coords' + num);
    koord.setAttribute('type', 'text');
    koord.setAttribute('readonly', 'true');
    koord.setAttribute('style', 'width:100;');
    koord.setAttribute('value',  boxes[num].startX + ',' + boxes[num].startY + ',' + boxes[num].stopX + ',' + boxes[num].stopY);
    tdKoord.appendChild(koord);

                // Lenke
    var tdLenke = document.createElement("TD");
    row.insertBefore(tdLenke, null);

    var lenke = document.createElement('INPUT');
    lenke.setAttribute('id', 'link' + num);
    lenke.setAttribute('name', 'link'+num);
    lenke.setAttribute('type','text');
    lenke.setAttribute('style', 'width:185;');
    var url = boxes[num].url;
    if (url == "") {
        url = '<kantega:label key="aksess.multimedia.imagemap.insertlink"/>';
    }
    lenke.setAttribute('value', url);
    tdLenke.appendChild(lenke);

                // Lenke
    var tdLenkeVelg = document.createElement("TD");
    row.insertBefore(tdLenkeVelg, null);

    var a = document.createElement('A');
    a.innerHTML = '<img src="../bitmaps/common/buttons/mini_velg.gif" border=0>';
    a.href = "javascript:insertContentLink(" + num + ")";
    tdLenkeVelg.appendChild(a);


    var a2 = document.createElement('A');
    a2.setAttribute('class', 'button');
    a2.innerHTML = '<kantega:label key="aksess.multimedia.imagemap.choose"/>';
    a2.href = "javascript:insertContentLink(" + num + ")";
    tdLenkeVelg.appendChild(a2);

                // Alternativt navn
    var tdAltName = document.createElement("TD");
    row.insertBefore(tdAltName, null);

    var altName = document.createElement('INPUT');
    altName.setAttribute('id', 'altname' + num);
    altName.setAttribute('name','altname' + num);
    altName.setAttribute('style', 'width:125;');
    altName.setAttribute('type', 'text');
    altName.setAttribute('value', boxes[num].altName);
    tdAltName.appendChild(altName);

                // Target
    var tdNewWin = document.createElement("TD");
    row.insertBefore(tdNewWin, null);

    var newWin = document.createElement('INPUT');
    newWin.setAttribute('id', 'nyttvindu' + num);
    newWin.setAttribute('name','nyttvindu' + num);
    newWin.setAttribute('type', 'checkbox');
    newWin.setAttribute('class', 'imagemap' + (num%10));
    if (boxes[num].newWindow) {
        newWin.setAttribute('checked', 'true');
    }
    tdNewWin.appendChild(newWin);

                // Slett
    var tdSlett = document.createElement("TD");
    row.insertBefore(tdSlett, null);

    var aSlett = document.createElement('A');
    aSlett.innerHTML = '<img src="../bitmaps/common/buttons/mini_slett.gif" border=0>';
    aSlett.href = "javascript:deleteRow(" + num + ")";
    tdSlett.appendChild(aSlett);


    var a2Slett = document.createElement('A');
    a2Slett.setAttribute('class', 'button');
    a2Slett.innerHTML = '<kantega:label key="aksess.multimedia.imagemap.delete"/>';
    a2Slett.href = "javascript:deleteRow(" + num + ")";
    tdSlett.appendChild(a2Slett);

}

// Sletter valgte element og tilhørende felter. Kan utvides til å også endre elementet
function deleteRow(rowId) {
    if(confirm("Ønsker du å slette dette området fra bildekartet?")){
        var elementsTable = document.getElementById("elementsTable");
        for (var i = 0; i < elementsTable.rows.length; i++) {
            var row = elementsTable.rows[i];
            if (row.id == "row" + rowId) {
                elementsTable.deleteRow(i);
                break;
            }
        }

        removeRectangle(rowId);

                    // Marker som slettet. Brukes i stede for å fjerne fra listen for at navnene i feltene
        // skal stemme overens med indexer i tabellen
        boxes[rowId].deleted = true;
    }
}

// Åpner navigator for å velge url til innhold i aksess
function insertContentLink(linkNo){
    currentLink = linkNo;
    var target = window.open("../popups/selectcontent.jsp?refresh=" + getRefresh(), "contentWindow", "toolbar=no,width=280,height=450,resizable=yes,scrollbars=yes");
    target.focus();
}


// Callback fra navigator.jsp som setter inn riktig url
function insertIdAndValueIntoForm(id, text) {
    var linkid = 'link' + currentLink;
    var currentlink = document.getElementById(linkid);
    currentlink.setAttribute('value', '/content.ap?thisId=' + id);
}


function saveImageMap() {
<%
    if (conf.getBoolean("imagemap.askbeforeoverwrite", false)) {
%>
    if (!document.myform.overwrite[0].checked && !document.myform.overwrite[1].checked) {
        alert("Vennligst velg om du ønsker å lagre bildekartet på det eksisterende bildet eller å lagre det på et nytt bilde!");
        return;
    }
<%
    }
%>
    document.myform.submit();
}
</script>

<table border="0" cellspacing="0" cellpadding="0" width="680">
    <tr>
        <td class="tableHeading"><b><kantega:label key="aksess.multimedia.imagemap"/></b></td>
    </tr>
    <tr>
        <td><img src="../bitmaps/blank.gif" width="2" height="4" alt=""></td>
    </tr>
    <tr>
        <td>
            <div class=helpText><kantega:label key="aksess.multimedia.imagemap.hjelp"/></div>
        </td>
    </tr>
    <tr>
        <td><img src="../bitmaps/blank.gif" width="2" height="4"></td>
    </tr>
</table>
<div id="maindiv">
    <img id="bilde" style="border:0" usemap="#imagemap" src="../../multimedia.ap?id=<%=mm.getId()%>" width="<%=mm.getWidth()%>" height="<%=mm.getHeight()%>" alt="">
    <br>
    <form id="myform" name="myform" method="post" action="ImageMap.action" target="content">
        <input type="hidden" name="id" value="<%=mm.getId()%>">
        <table border="0" cellspacing="0" cellpadding="2" width="680" id="elementsTable">
            <tr>
                <td width="110" class="tableHeading"><kantega:label key="aksess.multimedia.imagemap.koordinater"/></td>
                <td width="195" class="tableHeading"><kantega:label key="aksess.multimedia.imagemap.lenke"/></td>
                <td width="70" class="tableHeading">&nbsp;</td>
                <td width="135" class="tableHeading"><kantega:label key="aksess.multimedia.imagemap.altnavn"/></td>
                <td width="100" class="tableHeading"><kantega:label key="aksess.multimedia.imagemap.nyttvindu"/></td>
                <td width="70" class="tableHeading">&nbsp;</td>
            </tr>
        </table>
        <br>
        <%
            if (conf.getBoolean("imagemap.askbeforeoverwrite", false)) {
        %>
        <table border="0" cellspacing="0" cellpadding="1">
            <tr>
                <td><input type="radio" name="overwrite" value="true" <%if (!allowOverwrite) out.write("disabled readonly=true");%>></td>
                <td><kantega:label key="aksess.multimedia.imagemap.overwrite"/></td>
            </tr>
            <tr>
                <td><input type="radio" name="overwrite" value="false" <%if (!allowOverwrite) out.write("checked");%>></td>
                <td><kantega:label key="aksess.multimedia.imagemap.createnew"/></td>
            </tr>
        </table>
        <br>
        <%
            }
        %>
        <a href="Javascript:saveImageMap()"><img src="../bitmaps/<%=skin%>/buttons/lagre.gif" border="0"></a>&nbsp;&nbsp;<a href="Javascript:window.parent.location = 'multimedia.jsp?id=<%=mm.getId()%>'"><img src="../bitmaps/<%=skin%>/buttons/avbryt.gif" border="0"></a>
    </form>

</div>
<script type="text/javascript">
    <%= mim.generateJavascript() %>
</script>

</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>