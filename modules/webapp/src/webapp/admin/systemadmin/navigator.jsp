<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>navigator.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript">
function gotoObject(url, title) {
    window.parent.content.location = "systemadmin.jsp?url=" + url + "&title=" + title;
}
</script>
<body class="bodyWithMargin">
<table border="0">
    <tr>
        <td><img src="../bitmaps/common/navigator/nav_open.gif" width=7 height=7 hspace=2 vspace=2></td>
        <td nobr><a href="#" class="navNormal"><kantega:label key="aksess.systemadmin.navigator.systemadmin"/></a></td>
    </tr>
    <%
        if (Aksess.isTopicMapsEnabled()) {
    %>
    <tr>
        <td></td>
        <td>
            <table border="0">
                <tr>
                    <td><img src="../bitmaps/blank.gif" width=7 height=7 hspace=2 vspace=2></td>
                    <td nobr><a href="Javascript:gotoObject('topicmaps/admin/ListTopicMaps.action', 'topicmaps.admin')" class="navNormal"><kantega:label key="aksess.systemadmin.navigator.emnekart"/></a></td>
                </tr>
            </table>
        </td>
    </tr>
    <%
        }
    %>

    <tr>
        <td></td>
        <td>
            <table border="0">
                <tr>
                    <td><img src="../bitmaps/common/navigator/nav_open.gif" width=7 height=7 hspace=2 vspace=2></td>
                    <td nobr><a href="#" class="navNormal"><kantega:label key="aksess.systemadmin.navigator.nettstedogmaler"/></a></td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        <table border="0">
                            <tr>
                                <td><img src="../bitmaps/blank.gif" width=11 height=11 hspace=0 vspace=0></td>
                                <td nobr><a href="Javascript:gotoObject('sites/ListSites.action', 'sites')" class="navNormal"><kantega:label key="aksess.systemadmin.navigator.nettstedogdomener"/></a></td>
                            </tr>
                            <tr>
                                <td><img src="../bitmaps/blank.gif" width=11 height=11 hspace=0 vspace=0></td>
                                <td nobr><a href="Javascript:gotoObject('templates/ReloadTemplateConfiguration.action', 'templateconfig')" class="navNormal"><kantega:label key="aksess.templateconfig.title"/></a></td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td></td>
        <td>
            <table border="0">
                <tr>
                    <td><img src="../bitmaps/common/navigator/nav_open.gif" width=7 height=7 hspace=2 vspace=2></td>
                    <td nobr><a href="#" class="navNormal"><kantega:label key="aksess.systemadmin.navigator.sikkerhet"/></a></td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        <table border="0">
                            <tr>
                                <td><img src="../bitmaps/blank.gif" width=11 height=11 hspace=0 vspace=0></td>
                                <td nobr><a href="Javascript:gotoObject('useradmin/profile/', 'useradmin.profile')" class="navNormal"><kantega:label key="aksess.useradmin.profile.title"/></a></td>
                            </tr>
                            <tr>
                                <td><img src="../bitmaps/blank.gif" width=11 height=11 hspace=0 vspace=0></td>
                                <td nobr><a href="Javascript:gotoObject('useradmin/role/', 'useradmin.role')" class="navNormal"><kantega:label key="aksess.useradmin.role.title"/></a></td>
                            </tr>
                            <tr>
                                <td><img src="../bitmaps/blank.gif" width=11 height=11 hspace=0 vspace=0></td>
                                <td nobr><a href="Javascript:gotoObject('security/', 'security')" class="navNormal"><kantega:label key="aksess.viewpermissions.title"/></a></td>
                            </tr>
                        <%
                            if (Aksess.isEventLogEnabled()) {
                        %>
                            <tr>
                                <td><img src="../bitmaps/blank.gif" width=11 height=11 hspace=0 vspace=0></td>
                                <td nobr><a href="Javascript:gotoObject('eventlog/SearchEventLog.action', 'eventlog')" class="navNormal"><kantega:label key="aksess.eventlog.title"/></a></td>
                            </tr>
                        <%
                            }
                           %><tr>
                                <td><img src="../bitmaps/blank.gif" width=11 height=11 hspace=0 vspace=0></td>
                                <td nobr><a href="Javascript:gotoObject('locks/ListContentLocks.action', 'locks')"  class="navNormal"><kantega:label key="aksess.locks.title"/></a></td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td></td>
        <td>
            <table border="0">
                <tr>
                    <td><img src="../bitmaps/common/navigator/nav_open.gif" width=7 height=7 hspace=2 vspace=2></td>
                    <td nobr><a href="#" class="navNormal"><kantega:label key="aksess.systemadmin.navigator.oversiktogstat"/></a></td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        <table border="0">
                            <tr>
                                <td><img src="../bitmaps/blank.gif" width=11 height=11 hspace=0 vspace=0></td>
                                <td nobr><a href="Javascript:gotoObject('statistics/', 'statistics')" class="navNormal"><kantega:label key="aksess.statistics.title"/></a></td>
                            </tr>
                            <% if (Aksess.isSearchLogEnabled()) {
                            %>
                            <tr>
                                <td><img src="../bitmaps/blank.gif" width=11 height=11 hspace=0 vspace=0></td>
                                <td nobr><a href="Javascript:gotoObject('searchlog/ViewSearchLog.action', 'searchlog')" class="navNormal"><kantega:label key="aksess.searchlog.title"/></a></td>
                            </tr>
                            <%
                                }
                            %>
                            <tr>
                                <td><img src="../bitmaps/blank.gif" width=11 height=11 hspace=0 vspace=0></td>
                                <td nobr><a href="Javascript:gotoObject('aliases/', 'aliases')" class="navNormal"><kantega:label key="aksess.aliases.title"/></a></td>
                            </tr>
                            <tr>
                                <td><img src="../bitmaps/blank.gif" width=11 height=11 hspace=0 vspace=0></td>
                                <td nobr><a href="Javascript:gotoObject('contentexpiration/ListContentExpiration.action', 'contentexpire')" class="navNormal"><kantega:label key="aksess.contentexpire.title"/></a></td>
                            </tr>
                            <tr>
                                <td><img src="../bitmaps/blank.gif" width=11 height=11 hspace=0 vspace=0></td>
                                <td nobr><a href="Javascript:gotoObject('userchanges/ListUserChanges.action', 'userchanges')" class="navNormal"><kantega:label key="aksess.userchanges.title"/></a></td>
                            </tr>
                            <tr>
                                <td><img src="../bitmaps/blank.gif" width=11 height=11 hspace=0 vspace=0></td>
                                <td nobr><a href="Javascript:gotoObject('mailsubscription/ViewMailSubscribers.action', 'mailsubscription')" class="navNormal"><kantega:label key="aksess.mailsubscription.title"/></a></td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>

    <tr>
        <td></td>
        <td>
            <table border="0">
                <tr>
                    <td><img src="../bitmaps/common/navigator/nav_open.gif" width=7 height=7 hspace=2 vspace=2></td>
                    <td nobr><a href="#" class="navNormal"><kantega:label key="aksess.systemadmin.navigator.søk"/></a></td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        <table border="0">
                            <tr>
                                <td><img src="../bitmaps/blank.gif" width=11 height=11 hspace=0 vspace=0></td>
                                <td nobr><a href="Javascript:gotoObject('search/RebuildIndex.action', 'search')" class="navNormal"><kantega:label key="aksess.systemadmin.navigator.gjenopprettindeks"/></a></td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <% if(Aksess.isLinkCheckerEnabled()) { %>
    <tr>
        <td></td>
        <td>
            <table border="0">
                <tr>
                    <td><img src="../bitmaps/common/navigator/nav_open.gif" width=7 height=7 hspace=2 vspace=2></td>
                    <td nobr><a href="#" class="navNormal"><kantega:label key="aksess.systemadmin.navigator.lenkesjekker"/></a></td>
                </tr>
                <tr>
                    <td></td>
                    <td>
                        <table border="0">
                            <tr>
                                <td><img src="../bitmaps/blank.gif" width=11 height=11 hspace=0 vspace=0></td>
                                <td nobr><a href="Javascript:gotoObject('linkcheck/', 'linkcheck')" class="navNormal"><kantega:label key="aksess.systemadmin.navigator.visbruknelenker"/></a></td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <% } %>
</table>

</body>
</html>

<%@ include file="../include/jsp_footer.jsf" %>