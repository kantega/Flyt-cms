/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var validationErrors = new Array;

var errorTimerId = -1;

function ValidationError(fld, msg) {
   this.fld = fld;
   this.msg = msg;
}

function updateErrorStatus(isOk, fld, showError, msg)
{
   var fieldTitle = "Feltet";
   if (fld.title && fld.title != "") fieldTitle = fld.title;

   msg = fieldTitle + " " + msg;

   if (!isOk) {
      if (showError) {
         showErrorPopup(fld, msg);
         if (errorTimerId != -1)
            clearTimeout(errorTimerId);
         errorTimerId = window.setTimeout("hideErrorPopup()", 4000);
      } else {
         validationErrors[validationErrors.length] = new ValidationError(fld, msg);
      }

      return false;
   } else {
      hideErrorPopup();
   }

   return isOk;
}


function showErrorPopup(obj, msg) {
    var x = obj.clientWidth + 5;
    var y = 25;

    while(obj) {
        x += obj.offsetLeft;
        y += obj.offsetTop;
        obj = obj.offsetParent;
    }

    var layer = document.getElementById("errorIndicator");
    layer.innerHTML = msg;
    layer.style.visibility = "visible";

    layer.style.left = x;
    layer.style.top = y;
}


function hideErrorPopup()
{
    var layer = document.getElementById("errorIndicator");
    if ((layer) && (layer.style.visibility != "hidden")) {
        layer.style.visibility = "hidden";
    }
}


function showValidationErrors()
{
   if (validationErrors.length == 0) return true;

   var str = "<B>Vennligst fyll ut følgende felt riktig:</B><UL>\n";

   for (var i=0; i < validationErrors.length; i++) {
      str += '<LI>' + validationErrors[i].msg + '&nbsp;[<A href="Javascript:validationErrors[' + i + '].fld.focus()">Vis meg</A>]</LI>';
   }

   str += "</UL>";

   var errorMessage = document.getElementById("errorMessage");
   if (errorMessage) {
      errorMessage.innerHTML = str;
      var obj = document.getElementById("errorMessageArea");
      obj.style.display = 'block';
   }

   return false;
}


function validateRegex(regex, fld, showError, msg)
{
   if (!fld) {
      return true;
   }
   var value = "" + fld.value;
   var regObj = new RegExp(regex);

   var isOk = true;

   if (!regObj.test(value)) {
      isOk = false;
   }

   updateErrorStatus(isOk, fld, showError, msg);

   return isOk;
}


function validateNumber(fld, isMandatory, showError)
{
   var msg = "kan inneholde kun tall";

   if (!fld) {
      return true;
   }

   if ((fld.value == "") && (!isMandatory)) {
      return updateErrorStatus(true, fld, showError, msg);
   }

   var regex = "^\\-?[\\d]{1,}$";

   return (validateRegex(regex, fld, showError, msg));
}



function validateEmail(fld, isMandatory, showError)
{
   var msg = "må inneholde en gyldig epostadresse";

   if (!fld) {
      return true;
   }

   if ((fld.value == "") && (!isMandatory)) {
      return updateErrorStatus(true, fld, showError, msg);
   }

   var regex = "^[\\w-_\.]*[\\w-_\.]\@[\\w]\.+[\\w]+[\\w]$";

   return (validateRegex(regex, fld, showError, msg));
}


function validateChar(fld, isMandatory, showError)
{
   var msg =  "kan ikke være blank";
   var isOk = true;

   if (!fld) {
      return true;
   }

   if (isMandatory && fld.value == "") {
      isOk = false;
   }

   updateErrorStatus(isOk, fld, showError, msg);

   return (isOk);
}


function validatePassword(fld, showError)
{
   var msg =  "må være minst 6 tegn";
   var isOk = true;

   if (!fld) {
      return true;
   }

   var val = "" + fld.value;
   if (val.length < 5) {
      isOk = false;
   }

   updateErrorStatus(isOk, fld, showError, msg);

   return (isOk);
}

function validateList(fld, isMandatory, showError)
{
   var msg =  "kan ikke være blank";
   var isOk = true;

   if (!fld) {
      return true;
   }

   if (isMandatory && (fld.options.length == 0 || fld.options[fld.selectedIndex].value == "")) {
      isOk = false;
   }

   updateErrorStatus(isOk, fld, showError, msg);

   return (isOk);
}


function stringToDate(dateStr) {
   if (dateStr == "") {
      return null;
   }

   var day, month, year;

   if ((dateStr.indexOf(".") != -1) || (dateStr.indexOf("/") != -1) || (dateStr.indexOf("-") != -1)) {
      dateStr = dateStr.split("/").join(".");
      dateStr = dateStr.split("-").join(".");
      dateElements = dateStr.split(".");
      day = dateElements[0];
      month = dateElements[1];
      if (dateElements.length > 2) {
         year = parseInt(dateElements[2], 10);
      } else {
         year = new Date().getFullYear();
      }
   } else {
      day = dateStr.substring(0,2);
      month = dateStr.substring(2,4);
      if (dateStr.length > 4) {
         year = parseInt(dateStr.substring(4,dateStr.length), 10);
      } else {
         year = new Date().getFullYear();
      }
   }

   if (year < 100) {
      var tmp2 = "" + new Date().getFullYear();
      var century = parseInt(tmp2.substring(0, 2), 10) * 100;
      if (year < 50)
         year += century;
      else
         year += century - 100;
   }
   if ((day < 1) || (day > 31)) {
      return null;
   }
   if ((month < 1) || (month > 12)) {
      return null;
   }

   return new Date(year, month - 1, day);
}


function validateDate(fld, isMandatory, showError)
{
   var msg = "må inneholde en korrekt dato (dd.mm.åååå)";

   if (!fld) {
      return true;
   }

   if ((fld.value == "") && (!isMandatory)) {
      return updateErrorStatus(true, fld, showError, msg);
   } else if ((fld.value == "dd.mm.åååå") && (!isMandatory)) {
      return updateErrorStatus(true, fld, showError, msg);
   }

   var tmp = "" + fld.value;
   var dateElements;
   var day, month, year;
   if ((tmp.indexOf(".") != -1) || (tmp.indexOf("/") != -1) || (tmp.indexOf("-") != -1)) {
      tmp = tmp.split("/").join(".");
      tmp = tmp.split("-").join(".");
      dateElements = tmp.split(".");
      day = dateElements[0];
      month = dateElements[1];
      if (dateElements.length > 2) {
         year = parseInt(dateElements[2], 10);
      } else {
         year = new Date().getFullYear();
      }

   } else {
      day = tmp.substring(0,2);
      month = tmp.substring(2,4);
      if (tmp.length > 4) {
         year = parseInt(tmp.substring(4,tmp.length), 10);
      } else {
         year = new Date().getFullYear();
      }
   }

   if (year < 100) {
      var tmp = "" + new Date().getFullYear();
      var century = parseInt(tmp.substring(0, 2), 10) * 100;
      if (year < 50)
        year += century;
      else
        year += century - 100;
   }

   var str = day + "." + month + "." + year;

   var regObj = new RegExp("^[\\d]{1,2}.[\\d]{1,2}.[\\d]{4,4}$");

   if (!regObj.test(str)) {
      return updateErrorStatus(false, fld, showError, msg);
   }

   if ((day < 1) || (day > 31)) {
      return updateErrorStatus(false, fld, showError, msg + " Dag må være mellom 1 og 31.");
   }

   if ((month < 1) || (month > 12)) {
      return updateErrorStatus(false, fld, showError, msg + "  Måned må være mellom 1 og 12.");
   }

   var chkDt = new Date(year, month - 1, day);
   var chkDays = chkDt.getDate();

   if (day != chkDays) {
      return updateErrorStatus(false, fld, showError, msg + "  Måneden du oppgav har kun " + (day-chkDays) +  " dager.");
   }

   fld.value = str;

   return updateErrorStatus(true, fld, showError, msg);
}

