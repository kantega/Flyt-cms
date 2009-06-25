<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
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

function checkDate(date)
{
   var day, month, year;
   var startInx, endInx;
   var newDate = "";

   if (date.length < 8) {
      alert("<kantega:label key="aksess.js.advarsel.dato.feilformat"/>");
      return -1;
   }

   if (date.indexOf('/', 0) != -1) {
      for (var i = 0; i < date.length; i++) {
         if (date.charAt(i) == "/") {
            newDate += "."
         } else {
            newDate += date.charAt(i);
         }
      }
      date = newDate;
   }

   startInx = 0;
   endInx = date.indexOf('.', 0);
   if (endInx == -1) {
      alert("<kantega:label key="aksess.js.advarsel.dato.skilletegn"/>");
      return -1;
   }

   if ((date.charAt(startInx) == '0') && ((endInx - startInx) > 1)) {
      startInx++;
   }

   day = parseInt(date.substring(startInx, endInx));
   if (isNaN(day)) {
      alert("<kantega:label key="aksess.js.advarsel.dato.feildag"/>");
      return -1;
   }

   startInx = endInx + 1;
   endInx = date.indexOf('.', startInx);
   if (endInx == -1) {
      alert("<kantega:label key="aksess.js.advarsel.dato.feilformat"/>");
      return -1;
   }

   if ((date.charAt(startInx) == '0') && ((endInx - startInx) > 1)) {
      startInx++
   }

   month = parseInt(date.substring(startInx, endInx));
   if (isNaN(month)) {
      alert("<kantega:label key="aksess.js.advarsel.dato.feilmaned"/>");
      return -1;
   } // month improperly specified

   year = parseInt(date.substring(endInx + 1, date.length));
   if (isNaN(year)) {
      alert("<kantega:label key="aksess.js.advarsel.dato.feilar"/>");
      return -1;
   } // year improperly specified

   if ((day < 1) || (day > 31)) {
      alert("<kantega:label key="aksess.js.advarsel.dato.feildagtall"/>");
      return -1;
   }

   if ((month < 1) || (month > 12)) {
      alert("<kantega:label key="aksess.js.advarsel.dato.feilmanedtall"/>");
      return -1;
   }

   if (year < 1000 ) {
      alert("<kantega:label key="aksess.js.advarsel.dato.feilartall"/>");
      return -1;
   }

   var chkDt = new Date(year, month - 1, day);
   var chkDays = chkDt.getDate();

   if (day != chkDays) {
     alert("<kantega:label key="aksess.js.advarsel.dato.feilskuddarmaned"/>");
     return -1;
   }

   return 0;
}

function checkTime(time)
{
   var hours, min;
   var startInx, endInx;

   if (time.length < 3) {
      return -1;
   }

   startInx = 0;
   endInx = time.indexOf(':', 0);
   if (endInx == -1) {
      alert("<kantega:label key="aksess.js.advarsel.dato.feiltidsformat.kolon"/>");
      return -1;
   }

   if ((time.charAt(startInx) == '0') && ((endInx - startInx) > 1))
      startInx++;

   hours = parseInt("" + time.substring(startInx, endInx));
   if (isNaN(hours)) {
      alert("<kantega:label key="aksess.js.advarsel.dato.feiltidsformat"/>");
      return -1;
   }

   if ((time.charAt(endInx + 1) == '0') && ((endInx + 1 - time.length) > 1))
      startInx++;

   min = parseInt("" + time.substring(endInx + 1, time.length));
   if (isNaN(min)) {
      alert("Oppgi et korrekt tidspunkt i formatet TT:MM.");
      return -1;
   }

   if (min < 0) {
      alert("<kantega:label key="aksess.js.advarsel.dato.feiltidsformat.minuttermindre"/>");
      return -1;
   }

   if (min > 59) {
      alert("<kantega:label key="aksess.js.advarsel.dato.feiltidsformat.minutterstorre"/>");
      return -1;
   }

   if (hours < 0) {
      alert("<kantega:label key="aksess.js.advarsel.dato.feiltidsformat.timermindre"/>");
      return -1;
   }

   if (hours > 23) {
      alert("<kantega:label key="aksess.js.advarsel.dato.feiltidsformat.timerstorre"/>");
      return -1;
   }

   return 0;
}