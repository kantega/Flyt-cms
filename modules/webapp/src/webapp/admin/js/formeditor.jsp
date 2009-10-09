<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
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

var formEditedElement = null;
var formPrevType = null;
var formEditorHTML = null;

function formDeleteElement(element) {
    $(element).remove();
}

function formEditElement(element) {
    // Show previous edited element and remove editor if visible
    formRemoveEditor();

    formEditedElement = element;

    // Move edit form to current position
    $(element).after(formEditorHTML);

    // Hide element being edited
    $(element).hide();

    $("#form_ChildNo").val($("#form_FormElements .formElement").index(element));

    fieldName = $("div.heading label", element).text();
    $("#form_FieldName").val(fieldName);

    helpText = $("div.helpText", element).html();
    if (helpText == undefined || helpText == null) helpText = "";
    $("#form_HelpText").val(helpText);

    if ($(element).hasClass("mandatory")) {
        $("#form_FieldMandatory").attr("checked", true);
    } else {
        $("#form_FieldMandatory").attr("checked", false);
    }

    if ($(element).hasClass("nobreak")) {
        $("#form_NoBreak").attr("checked", true);
    } else {
        $("#form_NoBreak").attr("checked", false);
    }

    inputClasses = $("div.inputs", element).attr("class").split(" ");
    type = "text";
    if (inputClasses.length > 1) {
        type = inputClasses[inputClasses.length - 1];
    }

    $("#form_FieldType").unbind("change");

    $("#form_FieldType").val(type);

    $("#form_FieldType").change(function() {
        type = $("#form_FieldType").val();
        for (n in formElementTypes) {
            if (formElementTypes[n].type == formPrevType) {
                formElementTypes[n].onActive(false);
            }
        }
        for (n in formElementTypes) {
            if (formElementTypes[n].type == type) {
                formElementTypes[n].onActive(true);
            }
        }
        formPrevType = type;
    });

    formPrevType = type;


    $("#form_Cancel").unbind("click");
    $("#form_Cancel").click(function() {
        formRemoveEditor();
    });

    $("#form_SaveFormElement").unbind("click");
    $("#form_SaveFormElement").click(function() {
        formSaveElement();
        $("#EditFormElement").hide();
    });

    handler = formGetElementTypeHandler(type);
    if (handler) {
        handler.onEdit(element);
        handler.onActive(true);
    }
}

function formGetElementTypeHandler(type) {
    for (n in formElementTypes) {
        if (formElementTypes[n].type == type) {
            return formElementTypes[n];
        }
    }
    return null;
}

function formRemoveEditor() {
    if (formEditedElement != null) {
        $(formEditedElement).show();
    }
    $("#EditFormElement").remove();
    formEditedElement = null;
}

function formNewElement() {
    $("#form_ChildNo").val(-1);

    fieldName = "Felt " + ($("#form_FormElements .formElement").length + 1);

    formAddOrSaveElement(fieldName, "text", "", -1);
    formBindSort();

    // Edit this element
    elm = $("#form_FormElements .formElement:last-child");
    formEditElement(elm);


    $("#form_FieldName").select();

}

function formAddInputValue(type, fieldName, value, checked) {
    html = "<div>";
    html += '<input type="' + type + '" name="ValuesCheckbox"';
    if (checked) {
        html += ' checked="checked"';
    }
    html += ">";
    html += '<input type="text" size="20" maxlength="128" name="' + fieldName + '" value="' + value + '">';
    html += '<a href="DeleteListOption" class="formOptionDelete"></a>';
    html += '</div>';
    $("#form_Values").append(html);

    $("#form_Values .formOptionDelete").unbind("click");
    $("#form_Values .formOptionDelete").click(function(event) {
        event.preventDefault();
        $(this).parent().remove();
    });
}

function formAddOrSaveElement(fieldName, type, helpText, childNo) {
    html = "";
    html += '<div class="heading"><label>' + fieldName + '</label></div>';
    html += '<div class="inputs ' + type + '">';

    handler = formGetElementTypeHandler(type);
    if (handler != null) {
        html += handler.onSave(fieldName);
    }

    html += '</div>';
    if (helpText != "") {
        html += '<div class="helpText">' + helpText + '</div>';
    }

    elementClz = "formElement";
    if ($("#form_FieldMandatory:checked").length == 1) {
        elementClz += " mandatory";
    }
    if ($("#form_NoBreak:checked").length == 1) {
        elementClz += " nobreak";
    }

    if (childNo < 0) {
        $("#form_FormElements").append('<div class="' + elementClz + '">' + html + '</div>');
        $("#form_ChildNo").val($("#form_FormElements .formElement").length-1);
        formBindHover();
    } else {
        $("#form_FormElements .formElement").eq(childNo).html(html);
        $("#form_FormElements .formElement").eq(childNo).attr("class", elementClz);
    }
}

function formSaveElement() {
    fieldName = $("#form_FieldName").val();
    type = $("#form_FieldType").val();
    helpText = $("#form_HelpText").val();

    childNo = $("#form_ChildNo").val();

    formAddOrSaveElement(fieldName, type, helpText, childNo);

    formRemoveEditor();
    formBindSort();
}


function formBindSort() {
    $('#form_FormElements').sortable(
    {
        opacity: 	0.8,
        axis:		'vertically',
        revert:		true
    });
}

function formSave() {
    formRemoveEditor();

    // Remove style set by mozilla
    $("#form_FormElements .formElement").removeAttr("style");

    // Remove disabled attribute
    $("#form_FormElements input").removeAttr("disabled");
    $("#form_FormElements textarea").removeAttr("disabled");
    $("#form_FormElements select").removeAttr("disabled");

    // Remove attributes set by JQuery
    $("#form_FormElements div").removeAttr("unselectable");
    $("#form_FormElements div").removeAttr("isdraggable");
    $("#form_FormElements div").removeAttr("sizset");
    $("#form_FormElements div").removeAttr("animationhandler");
    $("#form_FormElements div").removeAttr("ui-sortable");

    // Remove editor buttons
    $("#form_FormElements .formElementButtons").remove();

    // Get HTML
    html = $("#form_FormElements").html();
    $("#form_Value").val(html);

    // Add disabled attribute to prevent form elements being saved
    $("#form_FormElements input").attr("disabled", true);

}

function formBindHover() {
    $("#form_FormElements .formElement").unbind("hover");

    $("#form_FormElements .formElement").hover(
            function () {
                if ($(".formElementButtons", this).length == 0) {
                    $(".heading", this).append('<span class="formElementButtons"><span class="edit"><kantega:label key="aksess.button.endre"/></span><span class="delete"><kantega:label key="aksess.button.fjern"/></span></span>');
                    $("#form_FormElements .formElementButtons .edit").unbind("click");
                    $("#form_FormElements .formElementButtons .edit").click(function() {
                        formEditElement($(this).parent().parent().parent());
                    });

                    $("#form_FormElements .formElementButtons .delete").unbind("click");
                    $("#form_FormElements .formElementButtons .delete").click(function() {
                        formDeleteElement($(this).parent().parent().parent());
                    });
                }
            },
            function () {
                $(".formElementButtons", this).remove();
            }
            );
}

var formNextId = 0;
function formGetUniqueId(prefix) {
    var e = document.getElementById(prefix + formNextId);
    while (e) {
        formNextId++;
        e = document.getElementById(prefix + formNextId);
    }
    formNextId++;
    return prefix + formNextId;
}

function formDeleteSubmissions(formId) {
    if (confirm('<kantega:label key="aksess.formeditor.deleteformdata.confirm"/>')) {
        $.post("DeleteFormSubmissions.action", { formId: formId });
    }
}

function formInitElements() {
    $("#form_FormElements input").attr("disabled", true);
    for (n in formElementTypes) {
        $("#form_FieldType").append('<option value="' + formElementTypes[n].type + '">' + formElementTypes[n].name + '</option>');
    }
}

$(document).ready(function() {
    formBindSort();
    formBindHover();
    formInitElements();

    formEditorHTML = $("#form_PlaceHolder").html();
    $("#form_PlaceHolder").html("");

    $("#form_NewElement a").click(function(event) {
        event.preventDefault();
        formNewElement();
    });
});


function FormElementType(name, type) {
    this.name = name;
    this.type = type;
    this.onEdit = function(element) {
    }

    this.onSave = function(fieldName) {
        return '<input type="text" name="' + fieldName + '" disabled>';
    }

    this.onActive = function(isSelected) {
    }
}

var formElementTypes = new Array();

// Text type
var formElementText = new FormElementType("<kantega:label key="aksess.formeditor.type.text"/>", "text");
formElementText.onEdit = function(element) {
    var size = $("div.inputs input", element).attr("size");
    if (size != 0) {
        $("#form_Length").val(size);
    } else {
        $("#form_Length").val("");
    }

    var clz = $("div.inputs input", element).attr("class");
    $("#form_Validator").val(clz);

}
formElementText.onSave = function (fieldName) {
    var len = $("#form_Length").val();
    if (len != "") {
        len = parseInt(len, 10);
    }
    var validator = $("#form_Validator").val();

    var html = '<input type="text" name="' + fieldName + '" disabled';
    if (!isNaN(len) && len > 0) {
        html += ' size="' + len + '"';
    }
    if (validator != '') {
        html += ' class="' + validator + '"';
    }
    html += '>';
    return html;
}
formElementText.onActive = function (isSelected) {
    if (isSelected) {
        $(".form_params_text").show();
    } else {
        $(".form_params_text").hide();
    }
}
formElementTypes[formElementTypes.length] = formElementText;

// Textarea
var formElementTextArea = new FormElementType("<kantega:label key="aksess.formeditor.type.textarea"/>", "textarea");
formElementTextArea.onEdit = function(element) {
    var size = $("div.inputs textarea", element).attr("rows");
    if (size != 0) {
        $("#form_Rows").val(size);
    } else {
        $("#form_Rows").val("3");
    }
}

formElementTextArea.onSave = function(fieldName) {
    var rows = $("#form_Rows").val();
    if (rows != "") {
        rows = parseInt(rows, 10);
    }

    if (isNaN(rows) || rows == 0) {
        rows = 3;
    }

    return '<textarea rows="' + rows + '" cols="40" name="' + fieldName + '" disabled></textarea>';
}
formElementTextArea.onActive = function (isSelected) {
    if (isSelected) {
        $(".form_params_textarea").show();
    } else {
        $(".form_params_textarea").hide();
    }
}
formElementTypes[formElementTypes.length] = formElementTextArea;

// Checkbox
var formElementCheckbox = new FormElementType("<kantega:label key="aksess.formeditor.type.checkbox"/>", "checkbox");
formElementCheckbox.onEdit = function(element) {
    $("#form_Values").html("");
    $("div.inputs input", element).each(function() {
        var fieldName = $("#form_FieldName").val();
        formAddInputValue("checkbox", fieldName, this.value, this.checked);
    });
}
formElementCheckbox.onSave = function (fieldName) {
    html = "";
    $("#form_Values div").each(function (i) {
        var val = $("input[type=text]", this).val();
        if (val != "") {
            var id = formGetUniqueId("formcb");
            html += '<div><input type="checkbox" name="' + fieldName + '" value="' + val + '" id="' + id + '" disabled';
            if ($("input[type=checkbox]", this).is(":checked")) {
                html += ' checked="checked"';
            }
            html += '> <label for="' + id + '">' + val + '</label></div>';
        }
    });
    return html;
}
formElementCheckbox.onActive = function (isSelected) {
    if (isSelected) {
        $("#form_AddElement").unbind("click");
        $("#form_AddElement").click(function(event) {
            event.preventDefault();
            var fieldName = $("#form_FieldName").val();
            formAddInputValue("checkbox", fieldName, "", false);
        });
        if ($("#form_Values input").length == 0) {
            var fieldName = $("#form_FieldName").val();
            formAddInputValue("checkbox", fieldName, "", false);
        }
        $(".form_params_list").show();
    } else {
        $(".form_params_list").hide();
    }
}
formElementTypes[formElementTypes.length] = formElementCheckbox;

// Radio button
var formElementRadio = new FormElementType("<kantega:label key="aksess.formeditor.type.radio"/>", "radio");
formElementRadio.onEdit = function(element) {
    $("#form_Values").html("");
    $("div.inputs input", element).each(function() {
        var fieldName = $("#form_FieldName").val();
        formAddInputValue("radio", fieldName, this.value, this.checked);
    });
}
formElementRadio.onSave = function (fieldName) {
    html = "";
    $("#form_Values div").each(function (i) {
        var val = $("input[type=text]", this).val();
        if (val != "") {
            var id = formGetUniqueId("formradio");
            html += '<div><input type="radio" name="' + fieldName + '" value="' + val + '" id="' + id + '" disabled';
            if ($("input[type=radio]", this).is(":checked")) {
                html += ' checked="checked"';
            }
            html += '> <label for="' + id + '">' + val + '</label></div>';
        }
    });
    return html;
}
formElementRadio.onActive = function (isSelected) {
    if (isSelected) {
        $("#form_AddElement").unbind("click");
        $("#form_AddElement").click(function(event) {
            event.preventDefault();
            var fieldName = $("#form_FieldName").val();
            formAddInputValue("radio", fieldName, "", false);
        });
        if ($("#form_Values input").length == 0) {
            var fieldName = $("#form_FieldName").val();
            formAddInputValue("radio", fieldName, "", false);
        }
        $(".form_params_list").show();
    } else {
        $(".form_params_list").hide();
    }
}
formElementTypes[formElementTypes.length] = formElementRadio;

var formElementSelect = new FormElementType("<kantega:label key="aksess.formeditor.type.select"/>", "select");
formElementSelect.onEdit = function(element) {
    $("#form_Values").html("");
    $("div.inputs input", element).each(function() {
        var fieldName = $("#form_FieldName").val();
        formAddInputValue("radio", fieldName, this.value, this.checked);
    });
}

formElementSelect.onSave = function (fieldName) {
    html = '<select name="' + fieldName + '" disabled>';
    $("#form_Values div").each(function (i) {
        val = $("input[type=text]", this).val();
        if (val != "") {
            html += '<option value="' + val + '" ';
            if ($("input[type=radio]", this).is(":checked")) {
                html += ' selected="selected"';
            }
            html += '>' + val + '</option>';
        }
    });
    html += '</select>';
    return html;
}
formElementSelect.onActive = function (isSelected) {
    if (isSelected) {
        $("#form_AddElement").unbind("click");
        $("#form_AddElement").click(function(event) {
            event.preventDefault();
            fieldName = $("#form_FieldName").val();
            formAddInputValue("radio", fieldName, "", false);
        });
        if ($("#form_Values input").length == 0) {
            fieldName = $("#form_FieldName").val();
            formAddInputValue("radio", fieldName, "", false);
        }
        $(".form_params_list").show();
    } else {
        $(".form_params_list").hide();
    }
}


formElementTypes[formElementTypes.length] = formElementSelect;