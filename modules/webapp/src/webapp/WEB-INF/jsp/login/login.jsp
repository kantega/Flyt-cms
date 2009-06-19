<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1"%>
<%@ taglib uri="http://www.kantega.no/aksess/tags/aksess" prefix="aksess" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
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

<%
    request.setAttribute("aksess_locale", Aksess.getDefaultAdminLocale());
%>
<%@ page import="no.kantega.publishing.common.Aksess"%>
<html>
<head>
<title>Aksess - Login</title>
<script language="JavaScript">
<!--
function init()
{
   if (window.self != window.top) {
      window.open(".", "_top");
   }
   document.brukernavn.brukernavn.focus()
}

function check_brukernavn(feilmelding)
{
   // Check if values are filled out
   if (document.brukernavn.brukernavn.value.length < 1) {
      alert(feilmelding);
      document.brukernavn.brukernavn.focus();
      return false;
   }

   document.passord.passord.focus();

   return false;
}


function check_passord()
{
   if (document.brukernavn.brukernavn.value.length < 1) {
      alert('<kantega:label key="aksess.login.empty"/>');
      document.brukernavn.brukernavn.focus();
      return false;
   }

   if (document.passord.j_password.value.length < 1) {
      alert('<kantega:label key="aksess.login.empty"/>');
      document.passord.j_password.focus();
      return false;
   }

   document.passord.j_username.value = document.brukernavn.brukernavn.value;

   return true;
}


function login()
{
   if (check_passord()) {
      document.passord.submit();
   }
}

function capsLockDetect(e) {
  var capsLock = false;

   if(!e) {
      e = window.event;
   }

   if( !e ) {
      return;
   }

   var keyPressed = 0;
   if(e.which) {
      keyPressed = e.which;
   } else if(e.keyCode) {
      keyPressed = e.keyCode;
   } else if(e.charCode) {
      keyPressed = e.charCode
   }

   var shiftPressed = false;
   if(e.shiftKey) {
      shiftPressed = e.shiftKey;
   } else if( e.modifiers ) {
      if(e.modifiers & 4) {
         shiftPressed = true;
      }
   }

   if(keyPressed > 64 && keyPressed < 91 && !shiftPressed) {
      capsLock = true;
   } else if(keyPressed > 96 && keyPressed < 123 && shiftPressed) {
      capsLock = true;
   }

   document.getElementById('capslock').style.display = capsLock ? 'block' : 'none';
}

function showCopyright() {
   window.open("<aksess:geturl/>/login/about.jsp", "aboutWindow", "dependent,toolbar=no,width=230,height=50,resizable=yes");
}
//-->
</script>
<link rel="stylesheet" type="text/css" href="<aksess:geturl/>/login/login.css">
</head>
<body onLoad="init()" style="margin:0; padding:0">
    <map name="logo">
        <area type="rect" coords="300, 0, 500, 43" href="Javascript:showCopyright()">
    </map>

    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td background="<aksess:geturl/>/login/bitmaps/top_logo_spacer.gif" align="right"><img src="<aksess:geturl/>/login/bitmaps/top_logo.gif" width="500" height="43" usemap="#logo" border="0"></td>
        </tr>
        <tr>
            <td background="<aksess:geturl/>/login/bitmaps/top_nav_spacer.gif" align="right"><img src="<aksess:geturl/>/login/bitmaps/top_nav_login.gif" width="67" height="26"></td>
        </tr>
        <tr>
            <td><img src="<aksess:geturl/>/login/bitmaps/blank.gif" width="1" height="75"></td>
        </tr>
        <tr>
            <td align="center">
                <table border="0" cellspacing="0" cellpadding="0" width="215">
                    <tr>
                        <td width="1" rowspan="3" class="frame"><img src="<aksess:geturl/>/login/bitmaps/blank.gif" width="1" height="1"></td>
                        <td width="210" class="frame"><img src="<aksess:geturl/>/login/bitmaps/blank.gif" width="1" height="1"></td>
                        <td width="1" rowspan="3" class="frame"><img src="<aksess:geturl/>/login/bitmaps/blank.gif" width="1" heigth="1"></td>
                        <td width="2" rowspan="3" class="shadow" valign="top"><img src="<aksess:geturl/>/login/bitmaps/corner.gif" width="2" heigth="2"></td>
                     </tr>
                     <tr>
                        <td class="box">
                            <table border="0" cellspacing="2" cellpadding="2" align="center" width="210">
                                <tr>
                                    <td class="inpHeading"><strong><kantega:label key="aksess.login.username"/></strong></td>
                                </tr>
                                <tr>

                                    <form method="post" name="brukernavn" onSubmit="return check_brukernavn()">
                                        <td><input type="text" name="brukernavn" size="25" maxlength="40" onkeypress="capsLockDetect(arguments[0]);" value="<c:out value="${username}"/>"></td>
                                    </form>
                                </tr>
                                <form method="post" action="<%=response.encodeURL(Aksess.getContextPath() + "/Login.action")%>" name="passord" onSubmit="return check_passord()">
                                <tr>
                                    <input type="hidden" name="j_domain" value="<%=Aksess.getDefaultSecurityDomain()%>">
                                    <input type="hidden" name="j_username" value="<c:out value="${username}"/>">
                                    <input type="hidden" name="redirect" value="<c:out value="${redirect}"/>">
                                    <td class="inpHeading"><strong><kantega:label key="aksess.login.password"/></strong></td>
                                </tr>
                                <tr>
                                    <td><input type="password" name="j_password" size="25" maxlength="40" onkeypress="capsLockDetect(arguments[0]);"></td>
                                </tr>
                                <tr>
                                    <td><a href="Javascript:login()"><img src="<aksess:geturl/>/login/bitmaps/logginn.gif" border="0"></a></td>
                                </tr>
                                <c:if test="${loginfailed}">
                                    <tr>
                                        <td><strong><kantega:label key="aksess.login.loginfailed"/></strong></td>
                                    </tr>
                                </c:if>
                                <c:if test="${blockedUser}">
                                    <tr>
                                        <td><strong><kantega:label key="aksess.login.blockeduser"/></strong></td>
                                    </tr>
                                </c:if>
                                <c:if test="${blockedIP}">
                                    <tr>
                                        <td><strong><kantega:label key="aksess.login.blockedip"/></strong></td>
                                    </tr>
                                </c:if>

                                <tr>
                                    <td id="capslock" style="display:none;"><kantega:label key="aksess.login.caps"/></td>
                                </tr>
                                <tr>
                                    <td>
                                    <noscript>
                                        <p><strong>Javascript must be enabled to login</strong></p>
                                    </noscript>
                                    </td>
                                </tr>

                                </form>
                            </table>
                        </td>
                     </tr>
                    <tr>
                        <td class="frame"><img src="<aksess:geturl/>/login/bitmaps/blank.gif" width="1" height="1"></td>
                     </tr>
                     <tr>
                        <td colspan="4" class="shadow"><img src="<aksess:geturl/>/login/bitmaps/corner.gif" width="2" height="2"></td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>