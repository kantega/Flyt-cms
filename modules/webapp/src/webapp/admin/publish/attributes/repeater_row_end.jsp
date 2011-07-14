<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
</div></div></div><div class="bottom"><div class="corner"></div></div></div>

<c:if test="${repeaterRowNo == repeater.numberOfRows - 1}">
    </div>

    <c:if test="${(repeater.numberOfRows < repeater.maxOccurs) || (repeater.maxOccurs == -1)}">
        <div class="buttonGroup">
        <a href="#" class="button" onclick="openaksess.editcontext.addRepeaterRow('${repeater.nameIncludingPath}')"><span class="add"><kantega:label key="aksess.button.add"/></span></a>
        </div>
    </c:if>


</c:if>
