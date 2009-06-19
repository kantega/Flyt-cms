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

String.prototype.trim = function(){
    return this.replace(/^\s*|\s*$/g,"");
}



var validationErrors = new Array;

var errorTimerId = -1;

function ValidationError(fld, msg) {
   this.fld = fld;
   this.msg = msg;
}

function updateErrorStatus(isOk, fld, msg)
{
   var fieldTitle = "Feltet";
   if (fld.title && fld.title != "") fieldTitle = fld.title;

   msg = fieldTitle + " " + msg;

   if (!isOk) {
      validationErrors[validationErrors.length] = new ValidationError(fld, msg);
      return false;
   }

   return isOk;
}


function showValidationErrors() {

   if (validationErrors.length == 0) return true;

   var str = "<B>Vennligst fyll ut følgende felt riktig:</B><UL>\n";

   for (var i=0; i < validationErrors.length; i++) {
      str += '<LI>' + validationErrors[i].msg + '</LI>';
   }

   str += "</UL>";

   var errorMessage = document.getElementById("errorMessage");
   if (errorMessage) {
       errorMessage.innerHTML = str;
       var obj = document.getElementById("errorMessageArea");
       obj.style.display = 'block';
       obj.scrollIntoView(true);
   }

   return false;
}


function validateRegex(regex, fld, msg)
{
   if (!fld) {
      return true;
   }
   var value = "" + fld.value.trim();
   var regObj = new RegExp(regex);

   var isOk = true;

   if (!regObj.test(value)) {
      isOk = false;
   }

   updateErrorStatus(isOk, fld, msg);

   return isOk;
}


function validateNumber(fld, isMandatory, minDigits, maxDigits)
{
   if (!fld) {
      return true;
   }

   var val = "" + fld.value.trim();
   if (arguments.length >= 3) {
      if (val != "" && val.length < minDigits) {
         return updateErrorStatus(false, fld, "må inneholde minst " + minDigits + " siffer");
      }
   }

   if (arguments.length >= 4) {
      if (val != "" && val.length > maxDigits) {
         return updateErrorStatus(false, fld, "må inneholde maks " + maxDigits + " siffer");
      }
   }

   if ((val == "") && (!isMandatory)) {
      return updateErrorStatus(true, fld, "");
   }

   var regex = "^\\-?[\\d]{1,}$";

   return (validateRegex(regex, fld, "kan inneholde kun tall"));
}



function validateEmail(fld, isMandatory)
{
   var msg = "må inneholde en gyldig epostadresse";

   if (!fld) {
      return true;
   }

   if ((fld.value.trim() == "") && (!isMandatory)) {
      return updateErrorStatus(true, fld, msg);
   }

   var regex = "^[\\w-_\.]*[\\w-_\.]\@[\\w]\.+[\\w]+[\\w]$";

   return (validateRegex(regex, fld, msg));
}


function validateChar(fld, isMandatory)
{
   var msg =  "kan ikke være blank";
   var isOk = true;

   if (!fld) {
      return true;
   }

   if (isMandatory && fld.value.trim() == "") {
      isOk = false;
   }

   updateErrorStatus(isOk, fld, msg);

   return (isOk);
}


function validatePassword(fld)
{
   var msg =  "må være minst 6 tegn";
   var isOk = true;

   if (!fld) {
      return true;
   }

   var val = "" + fld.value.trim();
   if (val.length < 5) {
      isOk = false;
   }

   updateErrorStatus(isOk, fld, msg);

   return (isOk);
}

function validateList(fld, isMandatory)
{
   var msg =  "må velges";
   var isOk = true;

   if (!fld) {
      return true;
   }

   if (isMandatory && (fld.options.length == 0 || fld.options[fld.selectedIndex].value == "")) {
      isOk = false;
   }

   updateErrorStatus(isOk, fld, msg);

   return (isOk);
}


function validateCheckbox(fld, isMandatory) {
   var msg =  "må velges";
   var isOk = true;
   var isChecked = false;

   if (!fld) {
      return true;
   }

   if (!fld.length) {
       if (fld.checked) isChecked = true;
   } else {
       for (var i = 0; i < fld.length; i++) {
          if (fld[i].checked) {
             isChecked = true;
             break;
          }
       }
   }

   if (isMandatory && !isChecked) {
      isOk = false;
   }

   updateErrorStatus(isOk, fld[0], msg);

   return isOk;
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

function validateDate(fld, isMandatory)
{
   var msg = "må inneholde en korrekt dato (dd.mm.åååå)";

   if (!fld) {
      return true;
   }

   if ((fld.value.trim() == "") && (!isMandatory)) {
      return updateErrorStatus(true, fld, msg);
   } else if ((fld.value == "dd.mm.åååå") && (!isMandatory)) {
      return updateErrorStatus(true, fld, msg);
   }

   var tmp = "" + fld.value.trim();
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
      var tmp2 = "" + new Date().getFullYear();
      var century = parseInt(tmp2.substring(0, 2), 10) * 100;
      if (year < 50)
        year += century;
      else
        year += century - 100;
   }

   var str = day + "." + month + "." + year;

   var regObj = new RegExp("^[\\d]{1,2}.[\\d]{1,2}.[\\d]{4,4}$");

   if (!regObj.test(str)) {
      return updateErrorStatus(false, fld, msg);
   }

   if ((day < 1) || (day > 31)) {
      return updateErrorStatus(false, fld, msg + " Dag må være mellom 1 og 31.");
   }

   if ((month < 1) || (month > 12)) {
      return updateErrorStatus(false, fld, msg + "  Måned må være mellom 1 og 12.");
   }

   var chkDt = new Date(year, month - 1, day);
   var chkDays = chkDt.getDate();

   if (day != chkDays) {
      return updateErrorStatus(false, fld, msg + "  Måneden du oppga har kun " + (day-chkDays) +  " dager.");
   }

   fld.value = str;

   return updateErrorStatus(true, fld, msg);
}

function validateTime(fld, isMandatory)
{
    if (!fld) {
       return true;
    }

    if ((fld.value.trim() == "") && (!isMandatory)) {
       return updateErrorStatus(true, fld, msg);
    }

    var hours, min;
    var startInx, endInx;

    var msg = "Oppgi et korrekt tidspunkt i formatet TT:MM.  Timer skilles fra minutter med : (kolon)."
    if (fld.length < 3) {
       return updateErrorStatus(false, fld, msg);
    }

    var time = fld.value.trim();

    startInx = 0;
    endInx = time.indexOf(':', 0);
    if (endInx == -1) {
       return updateErrorStatus(false, fld, msg);
    }

    if ((time.charAt(startInx) == '0') && ((endInx - startInx) > 1))
       startInx++;

    hours = parseInt("" + time.substring(startInx, endInx));
    if (isNaN(hours)) {
       return updateErrorStatus(false, fld, msg);
    }

    if ((time.charAt(endInx + 1) == '0') && ((endInx + 1 - time.length) > 1))
        startInx++;

    min = parseInt("" + time.substring(endInx + 1, time.length));
    if (isNaN(min)) {
        return updateErrorStatus(false, fld, msg);
    }

    if (min < 0 || min > 59 || hours < 0 || hours > 23) {
        return updateErrorStatus(false, fld, msg);
    }
    return updateErrorStatus(true, fld, msg);
}

function validatorGetXmlHttp() {
    var xmlhttp=false;

    try {
        xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
    } catch (e) {
        try {
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        } catch (E) {
            xmlhttp = false;
        }
    }

    if (!xmlhttp && typeof XMLHttpRequest!='undefined') {
        xmlhttp = new XMLHttpRequest();
    }
    return xmlhttp;
}


function validateRemote(url) {
    var xmlhttp = validatorGetXmlHttp();
    xmlhttp.open("POST",  url, true);
    xmlhttp.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
    xmlhttp.onreadystatechange=function() {
        if (xmlhttp.readyState==4) {
            var xml = xmlhttp.responseXML;
            var feil = xml.getElementsByTagName("error");
            if (feil) {
                for (var i = 0; i < feil.length; i++) {
                    var f = feil[i];
                    var msg = f.firstChild.nodeValue;
                    validationErrors[validationErrors.length] = new ValidationError(null, msg);
                }
            }
            showValidationErrors();
        }
    }

    var param = "";
    for(var i = 1; i < arguments.length; i++) {
        var fld = arguments[i];
        if (i > 1) {
            param += "&";
        }
        param += fld.name + "=" + escape(fld.value);
    }

    xmlhttp.send(param);
}
