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

    boolean allowOverwrite = false;
    if (securitySession.isUserInRole(Aksess.getAdminRole()) || securitySession.getUser().getId().equalsIgnoreCase(mm.getModifiedBy())) {
        allowOverwrite = true;
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>imagemanipulation.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript">
var orgWidth  = <%=mm.getWidth()%>;
var orgHeight = <%=mm.getHeight()%>;

var bilde = null;
var utsnitt = null;

var bildeX = 0;
var bildeY = 0;

var pendingCrop = false;

function sjekkInput(felt) {
    var v = parseInt(felt.value);
    if (isNaN(v) || v < 10) {
        alert("Vennligst skriv inn et tall større enn 10");
        return false;
    }
    return true;
}


function resizeImage(feltnavn, diff) {
    var felt = eval('document.myform.size' + feltnavn);

    if (!bilde) {
        return;
    }

    if (!sjekkInput(felt)) {
        return;
    }

    var v = parseInt(felt.value);
    v += diff;

    felt.value = v;

    updateImageSize(feltnavn)
}


function updateImageSize(feltnavn) {
    var felt = eval('document.myform.size' + feltnavn);

    if (!bilde) {
        return;
    }

    if (!sjekkInput(felt)) {
        return;
    }

    var v = parseInt(felt.value);
    var prosent = 0;
    var width  = 0;
    var height   = 0;

    if (feltnavn == 'prosent') {
        if (v > 100) v = 100;
        prosent = v;
        width = Math.round((orgWidth*prosent)/100);
        height  = Math.round((orgHeight*prosent)/100);
    } else if (feltnavn == 'width') {
        if (v > orgWidth) v = orgWidth;
        width = v;
        prosent = (width*100)/orgWidth;
        height  = Math.round((orgHeight*prosent)/100);
    } else if (feltnavn == 'height') {
        if (v > orgHeight) v = orgHeight;
        height = v;
        prosent = (height*100)/orgHeight;
        width = Math.round((orgWidth*prosent)/100);
    }

    felt.value = v;

    prosent = Math.round(prosent);
    width  = Math.round(width);
    height   = Math.round(height);

    document.myform.sizeprosent.value = prosent;
    document.myform.sizewidth.value = width;
    document.myform.sizeheight.value = height;

    utsnitt.style.visibility = 'hidden';
    pendingCrop = false;

    bilde.width  = width;
    bilde.height = height;
}


function mouseDown(e) {
	if (!e) var e = window.event;

    var x = 0;
    var y = 0;
	if (e.pageX || e.pageY) {
		x = e.pageX;
		y = e.pageY;
	} else if (e.clientX || e.clientY) {
		x = e.clientX + document.body.scrollLeft;
		y = e.clientY + document.body.scrollTop;
    }

    if (x < bildeX || y < bildeY || x > (bildeX + bilde.width) || y > (bildeY + bilde.height)) {
        return;
    }

    if (pendingCrop) {
        pendingCrop = false;
        return;
    }

    if (utsnitt.style.visibility == "visible") {
        utsnitt.style.width = 1;
        utsnitt.style.height = 1;
        utsnitt.style.visibility = "hidden";
        document.myform.cropx.value = -1;
        document.myform.cropy.value = -1;
    } else {
        utsnitt.style.top = y;
        utsnitt.style.left = x;
        utsnitt.style.width = 1;
        utsnitt.style.height = 1;
        utsnitt.style.visibility = "visible";

        document.myform.cropx.value = x - bildeX;
        document.myform.cropy.value = y - bildeY;

        pendingCrop = true;
    }
}


function mouseMove(e) {
    if (pendingCrop) {
    	if (!e) var e = window.event;

        var x = 0;
        var y = 0;
	    if (e.pageX || e.pageY) {
    		x = e.pageX;
	    	y = e.pageY;
    	} else if (e.clientX || e.clientY) {
	    	x = e.clientX + document.body.scrollLeft;
    		y = e.clientY + document.body.scrollTop;
        }

        // Kan ikke velge utsnitt utenfor bildet
        if (x < bildeX) x = bildeX;
        if (x > bildeX + bilde.width) x = bildeX + bilde.width;

        if (y < bildeY) y = bildeY;
        if (y > bildeY + bilde.height) y = bildeY + bilde.height;


        var w = x - parseInt(utsnitt.style.left);
        var h = y - parseInt(utsnitt.style.top);

        // Minimumsgrense på utsnitt
        if (w < 60) w = 60;
        if (h < 20) h = 20;

        utsnitt.style.width  = w;
        utsnitt.style.height = h;

        document.myform.cropwidth.value = w;
        document.myform.cropheight.value = h;

        // Vis størrelsen på utsnittet
        var info = document.getElementById("info");
        info.innerHTML = "(" + parseInt(utsnitt.style.width) + "x" + parseInt(utsnitt.style.height) + ")";
    }
}

function removeCrop(e) {
    utsnitt.style.visibility = "hidden";
}

if (window.Event) {
    document.captureEvents(Event.MOUSEDOWN);
    document.captureEvents(Event.MOUSEMOVE);
}

function initialize() {
    bilde   = document.getElementById("bilde");
    utsnitt = document.getElementById("utsnitt");

    var obj = bilde;
    while(obj) {
        bildeX += obj.offsetLeft;
        bildeY += obj.offsetTop;
        obj = obj.offsetParent;
    }

    document.onmousedown = mouseDown;
    document.onmousemove = mouseMove;
}

function saveImage() {
    if (!document.myform.overwrite[0].checked && !document.myform.overwrite[1].checked) {
        alert("Vennligst velg om du ønsker å opprette et nytt bilde eller overskrive det eksisterende!");
        return;
    }

    document.myform.submit()
}

</script>
<body class="bodyWithMargin" onLoad="initialize()">
<form name="myform" action="ImageManipulation.action" target="content" method="post">
    <table border="0" cellspacing="0" cellpadding="0" width="500">
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.multimedia.changesize"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4"></td>
        </tr>
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="4" width="380">
                    <tr>
                        <td><b><kantega:label key="aksess.multimedia.percent"/></b></td>
                        <td><b><kantega:label key="aksess.multimedia.width"/></b></td>
                        <td><b><kantega:label key="aksess.multimedia.height"/></b></td>
                        <td>&nbsp;</td>
                    </tr>
                    <tr>
                        <td><a href="Javascript:resizeImage('prosent', -10)" class="button">-</a><input type="text" size="3" name="sizeprosent" value="100" onBlur="updateImageSize('prosent')">%<a href="Javascript:resizeImage('prosent', +10)" class="button">+</a><br></td>
                        <td><a href="Javascript:resizeImage('width', -10)" class="button">-</a><input type="text" size="4" name="sizewidth" value="<%=mm.getWidth()%>" onBlur="updateImageSize('width')"><a href="Javascript:resizeImage('width', +10)" class="button">+</a><br></td>
                        <td><a href="Javascript:resizeImage('height', -10)" class="button">-</a><input type="text" size="4" name="sizeheight" value="<%=mm.getHeight()%>" onBlur="updateImageSize('height')"><a href="Javascript:resizeImage('height', +10)" class="button">+</a><br></td>
                        <td><a href="Javascript:;"><img src="../bitmaps/<%=skin%>/buttons/oppdater.gif" border="0"></a></td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.multimedia.crop"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="4"></td>
        </tr>
        <tr>
            <td>
                <div class=helpText><kantega:label key="aksess.multimedia.crop.hjelp"/></div>
            </td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="16"></td>
        </tr>
    </table>
<img id="bilde" src="../../multimedia.ap?id=<%=mm.getId()%>" width="<%=mm.getWidth()%>" height="<%=mm.getHeight()%>">

<div id="utsnitt" style="position:absolute; left: 100px; top: 100px; width:1px; height:1px; border:2px dotted; visibility:hidden;">
<div id="info"></div>
</div>
    <input type="hidden" name="id" value="<%=mm.getId()%>">
    <input type="hidden" name="cropx" value="-1">
    <input type="hidden" name="cropy" value="-1">
    <input type="hidden" name="cropwidth" value="-1">
    <input type="hidden" name="cropheight" value="-1">
    <%

    %>
    <table border="0" cellspacing="0" cellpadding="1">
        <tr>
            <td><input type="radio" name="overwrite" value="true" <%if (!allowOverwrite) out.write("disabled readonly=true");%>></td>
            <td><kantega:label key="aksess.multimedia.overwrite"/></td>
        </tr>
        <tr>
            <td><input type="radio" name="overwrite" value="false" <%if (!allowOverwrite) out.write("checked");%>></td>
            <td><kantega:label key="aksess.multimedia.createnew"/></td>
        </tr>
    </table>
    <br>
    <a href="Javascript:saveImage()"><img src="../bitmaps/<%=skin%>/buttons/lagre.gif" border="0"></a>&nbsp;&nbsp;<a href="Javascript:window.parent.location = 'multimedia.jsp?id=<%=mm.getId()%>'"><img src="../bitmaps/<%=skin%>/buttons/avbryt.gif" border="0"></a>
</form>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>
