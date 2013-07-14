<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<!DOCTYPE html>
<html>
<head>
    <title>Read OpenAksess logs</title>
    <link rel="stylesheet" href="<aksess:geturl url="/admin/css/jquery-ui-1.8.1.custom.css"/> " type="text/css"/>
    <style>
        body {
            top: 0;
            left: 0;
            position: fixed;
            width: 98%;
            height: 95%;
        }
        #logfiles{
            height: 100%;
        }
        .ui-tabs-panel {
            height: 90%;
            overflow: scroll;
        }
        #logfiles div {
            font-family: courier,monospace;
            font-size: 8px;
        }

        .line:nth-child(even){
            background-color: #eee;
        }
        .logheader {
            font-size: 10px;
        }
        #tabheaders a.downloadlogfile {
            margin-right: 3px;
            margin-top: 3px;
            padding: 0;
            height: 16px;
            width: 16px;
            cursor: pointer;
        }

        #tabheaders a.downloadlogfile {
            background-image: url('<aksess:geturl url="/admin/bitmaps/common/icons/small/insert.png"/>');
        }

        #updateCurrentTab {
            background-image: url('<aksess:geturl url="/admin/bitmaps/common/icons/small/insert.png"/>');
        }

        .linenumber {
            font-weight: bold;;
        }

        #controlls {
            font-size: 8px;
            float: right;
        }

        #controlls input {
            width: 20px;
        }
    </style>
</head>
<body>
<div id="logfiles">
    <ul id="tabheaders">
        <form id="controlls">
            <label for="numberOfLines">Number of lines</label>
            <input id="numberOfLines" type="text" value="100">
            <label for="startLine">First line</label>
            <input id="startLine" type="text" value="0">
            <input type="submit" id="updateCurrentTab">
        </form>
    </ul>
</div>

<script src="<aksess:geturl url="/admin/js/jquery-2.0.2.min.js"/>"></script>
<script src="<aksess:geturl url="/admin/js/jquery-ui-1.8.14.custom.min.js"/>"></script><script>
    var getUrlForFile = function(file, start, number){
        return '<aksess:geturl url="/admin/tools/logreader/logfiles/"/>' + file + '.action?numberoflines=' + number + '&startline=' + start;
    };
    var tabheaderUl = $('#tabheaders');
    var filesByIndex = {};

    var numberOfLinesInput = $('#numberOfLines');
    var startLineInput = $('#startLine');

    $.getJSON('<aksess:geturl url="/admin/tools/logreader/logfiles.action"/>', function(data){
        for(var i = 0; i < data.length ; i++){
            var logfile = data[i];
            filesByIndex[i + 1] = logfile; // logfilecontainer.index() is 1-indexed.
            tabheaderUl.append('<li><a class="logheader" href="' + getUrlForFile(logfile, startLineInput.val(), numberOfLinesInput.val())
                    + '">' + logfile + '</a><a href="<aksess:geturl url="/admin/tools/logreader/logfiles/download/"/>'
                    + logfile + '.action" class="downloadlogfile"></a></li>')
        }
        $('#logfiles').tabs();


    });
    var updateContent = function () {
        var selected = $('.ui-tabs-selected').index();
        var panel = $($('.ui-tabs-panel')[selected - 1]);
        $.getJSON(getUrlForFile(filesByIndex[selected], startLineInput.val(), numberOfLinesInput.val()), function (data) {
            panel.html(data.reduce(function (previousValue, currentValue) {
                return previousValue + currentValue;
            }, ''))
        });
        return false;
    };
    $('#controlls').submit(updateContent);
</script>
</body>
</html>
