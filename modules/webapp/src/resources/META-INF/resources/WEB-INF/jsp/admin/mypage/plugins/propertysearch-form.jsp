<%@ page import="no.kantega.commons.util.LocaleLabels" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page import="no.kantega.publishing.common.data.attributes.*" %>
<%@ page import="no.kantega.publishing.common.data.enums.ContentProperty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
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
<script type="text/javascript">
    $("#PropertySearchSubmit").click(function () {
        var f = document.myform;
        var parent = f.parent.value;
        var lastmodified = f.lastmodified.value;
        var doctype = f.doctype.value;
        var ownerperson = f.ownerperson.value;
        var owner = f.owner.value;
        var sort = f.sort.value;
        $.post("${pageContext.request.contextPath}/admin/mypage/plugins/PropertySearch.action", {parent:parent, lastmodified:lastmodified, doctype:doctype, ownerperson:ownerperson, owner:owner, sort:sort}, function(html) {
            $("#PropertySearchResults").html(html);
            $("#PropertySearchResults .sortable").dataTable({
                "bFilter": false,
                "bSort":false,
                "bLengthChange":false,
                "iDisplayLength":25,
                "oLanguage": {
                    "sInfo": "<kantega:label key="aksess.datatable.footer"/>"
                }
            });
        }, "html");
    });

    var PropertySearch = function() {
        function loaded() {

        }

    };

</script>
<form name="myform" action="" method="post">
    <div class="contentAttribute">
        <div class="heading"><kantega:label key="aksess.contentproperty.parent"/></div>
        <%
            ContentidAttribute parent = new ContentidAttribute();
            parent.setName(LocaleLabels.getLabel("aksess.contentproperty.parent", Aksess.getDefaultAdminLocale()));
            parent.setValue("");
            request.setAttribute("attribute", parent);
            request.setAttribute("fieldName", "parent");
            pageContext.include("/admin/publish/attributes/" + parent.getRenderer() +".jsp");
        %>
    </div>
    <div class="contentAttribute">
        <div class="heading"><kantega:label key="aksess.contentproperty.lastmodified"/></div>
        <%
            DateAttribute changed = new DateAttribute();
            changed.setName(LocaleLabels.getLabel("aksess.contentproperty.lastmodified", Aksess.getDefaultAdminLocale()));
            changed.setValue("");
            request.setAttribute("attribute", changed);
            request.setAttribute("fieldName", "lastmodified");
            pageContext.include("/admin/publish/attributes/" + changed.getRenderer() +".jsp");
        %>
    </div>
    <div class="contentAttribute">
        <div class="heading"><kantega:label key="aksess.contentproperty.doctype"/></div>
        <%
            DocumenttypeAttribute doctype = new DocumenttypeAttribute();
            doctype.setName(LocaleLabels.getLabel("aksess.contentproperty.doctype", Aksess.getDefaultAdminLocale()));
            doctype.setValue("");
            request.setAttribute("attribute", doctype);
            request.setAttribute("fieldName", "doctype");
            pageContext.include("/admin/publish/attributes/" + doctype.getRenderer() +".jsp");
        %>
    </div>
    <div class="contentAttribute">
        <div class="heading"><kantega:label key="aksess.contentproperty.ownerperson"/></div>
        <%
            UserAttribute user = new UserAttribute();
            user.setName(LocaleLabels.getLabel("aksess.contentproperty.ownerperson", Aksess.getDefaultAdminLocale()));
            user.setValue("");
            request.setAttribute("attribute", user);
            request.setAttribute("fieldName", "ownerperson");
            pageContext.include("/admin/publish/attributes/" + user.getRenderer() + ".jsp");
        %>
    </div>
    <div class="contentAttribute">
        <div class="heading"><kantega:label key="aksess.contentproperty.owner"/></div>
        <%
            OrgunitAttribute orgunit = new OrgunitAttribute();
            orgunit.setName(LocaleLabels.getLabel("aksess.contentproperty.owner", Aksess.getDefaultAdminLocale()));
            orgunit.setValue("");
            request.setAttribute("attribute", orgunit);
            request.setAttribute("fieldName", "owner");
            pageContext.include("/admin/publish/attributes/" + orgunit.getRenderer() + ".jsp");
        %>
    </div>
    <div class="contentAttribute">
        <div class="heading"><kantega:label key="aksess.propertysearch.sort"/></div>
        <div class="inputs">
            <select name="sort">
                <option value="<%=ContentProperty.TITLE%>"><kantega:label key="aksess.propertysearch.sort.title"/></option>
                <option value="<%=ContentProperty.LAST_MODIFIED%> desc"><kantega:label key="aksess.propertysearch.sort.lastmodified.desc"/></option>
                <option value="<%=ContentProperty.LAST_MODIFIED%>"><kantega:label key="aksess.propertysearch.sort.lastmodified"/></option>
                <option value="<%=ContentProperty.NUMBER_OF_VIEWS%> desc"><kantega:label key="aksess.propertysearch.sort.numberofviews.desc"/></option>
                <option value="<%=ContentProperty.NUMBER_OF_VIEWS%>"><kantega:label key="aksess.propertysearch.sort.numberofviews"/></option>
            </select>
        </div>
    </div>

    <div class="buttonGroup">
        <span class="button"><input id="PropertySearchSubmit" type="button" class="search" value="<kantega:label key="aksess.button.search"/>"></span>
    </div>

    <div id="PropertySearchResults" style="padding-top:25px">

    </div>
</form>

