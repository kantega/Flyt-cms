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

function aksessFormValidate() {
    var formOk = true;
    $(".formElement").each(function() {
        $(this).removeClass("error");
        var formFieldOk = true;
        // Check mandatory fields
        if ($(this).hasClass("mandatory")) {
            if ($("input[type=text]", this).val() == "") {
                formFieldOk = false;
            }
            if ($("textarea", this).val() == "") {
                formFieldOk = false;
            }
            if ($("input:checkbox", this).length > 0) {
                if ($("input:checkbox:checked", this).length == 0) {
                    formFieldOk = false;
                }
            }
            if ($("input:radio", this).length > 0) {
                if ($("input:radio:checked", this).length == 0) {
                    formFieldOk = false;
                }
            }
        }
        // Check email fields
        var email = $("input.email", this).val();
        if (email && email != "") {
            if (email.indexOf("@") == -1) {
                formFieldOk = false;
            }
        }
        // Check number fields
        var number = $("input.number", this).val();
        if (number && number != "") {
            var n = parseInt(number, 10);
            if (isNaN(n)) {
                formFieldOk = false;
            }
        }
        // Check

        if (!formFieldOk) {
            formOk = false;
            $(this).addClass("error");
        }
    }
    );

    if (formOk) {
        $("#form_Error").hide();
    } else {
        $("#form_Error").show();
        var form_Error = document.getElementById("form_Error");
        if (form_Error && form_Error.scrollIntoView) {
            form_Error.scrollIntoView();
        }
    }

    return formOk;
}