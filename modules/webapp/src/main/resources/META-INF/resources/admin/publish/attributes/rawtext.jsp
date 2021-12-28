<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page import="no.kantega.commons.util.StringHelper" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ page import="no.kantega.publishing.common.data.attributes.Attribute" %>
<%@ page import="no.kantega.publishing.common.data.enums.ContentProperty" %>

<%
  Attribute attribute = (Attribute)request.getAttribute("attribute");
  Content   content   = (Content)request.getAttribute("content");
  String    fieldName = (String)request.getAttribute("fieldName");

  String value = attribute.getValue();
  if (value == null) value = "";
  value = StringHelper.escapeQuotes(value);

  int maxLength = attribute.getMaxLength();
  int rows = (maxLength / 72) + 1;

  if (ContentProperty.TITLE.equalsIgnoreCase(attribute.getField()) && !content.isNew()) {
    value = content.getTitle();
  }
%>
<div class="inputs">
    <%
      if (maxLength <= 255) {
    %>
  <input type="text" onFocus="openaksess.editcontext.setFocusField(this)" onBlur="openaksess.editcontext.blurField(this)" name="<%=fieldName%>" id="<%=fieldName%>" value="<%=value%>" tabindex="<%=attribute.getTabIndex()%>"
    <%
        if (maxLength <  72) {
            out.write(" size=" + maxLength);
        } else {
            out.write(" class=\"fullWidth\"");
        }
        if (maxLength != -1) {
            out.write(" maxlength=" + maxLength);
        }
    %>><%
} else {
%>
  <textarea rows="<%=rows%>" cols="72" class="fullWidth" wrap="soft" onFocus="openaksess.editcontext.setFocusField(this)" onBlur="openaksess.editcontext.blurField(this)" name="<%=fieldName%>" id="<%=fieldName%>" tabindex="<%=attribute.getTabIndex()%>"><%=value%></textarea>
  <%
    }
  %>
</div>
