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

        #tabheaders a.downloadlogfile, #updateCurrentTab {
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
            <input id="startLine" type="text" value="-1">
            <input type="submit" id="updateCurrentTab">
        </form>
    </ul>
</div>

<script src="<aksess:geturl url="/admin/js/jquery-2.0.2.min.js"/>"></script>
<script src="<aksess:geturl url="/admin/js/jquery-ui-1.8.14.custom.min.js"/>"></script><script>
    var getUrlForFile = function(file, start, number){
        return '<aksess:geturl url="/admin/tools/logreader/logfiles/"/>' + file + '.action?numberoflines=' + number + '&startline=' + start;
    };
    var isDownScrollDetect = function(lastScrollTop, currentScroll){
        return currentScroll < lastScrollTop;
    };

    var tabheaderUl = $('#tabheaders');
    var filesByIndex = {};

    var numberOfLinesInput = $('#numberOfLines');
    var startLineInput = $('#startLine');
    var currentLine = startLineInput;

    $.getJSON('<aksess:geturl url="/admin/tools/logreader/logfiles.action"/>', function(data){
        for(var i = 0; i < data.length ; i++){
            var logfile = data[i];
            filesByIndex[i + 1] = logfile; // logfilecontainer.index() is 1-indexed.
            tabheaderUl.append('<li><a class="logheader" href="' + getUrlForFile(logfile, startLineInput.val(), numberOfLinesInput.val())
                    + '">' + logfile + '</a><a href="<aksess:geturl url="/admin/tools/logreader/logfiles/download/"/>'
                    + logfile + '.action" class="downloadlogfile"></a></li>')
        }
        $('#logfiles').tabs({
            ajaxOptions: {
                dataFilter: function(result){
                    var data = $.parseJSON(result);
                    return data.lines;
                }
            }
        });
        registerScrollHandler();
    });

    var updateContent = function () {
        var selected = $('.ui-tabs-selected').index();
        var panel = $($('.ui-tabs-panel')[selected - 1]);
        $.getJSON(getUrlForFile(filesByIndex[selected], startLineInput.val(), numberOfLinesInput.val()), function (data) {
            currentLine = data.lineNumber;
            panel.html(data.lines.reduce(function (previousValue, currentValue) {
                return previousValue + currentValue;
            }, ''))
        });
        return false;
    };

    var registerScrollHandler = function(){
        var lastScrollTop = 0;
        var isFetchingLines = false;
        $('.ui-tabs-panel').scroll(function(){
            var panel = $(this);
            var filename = filesByIndex[panel.index()];

            var currentScroll = panel.scrollTop();
            var isDownScroll = isDownScrollDetect(lastScrollTop, currentScroll);
            if (!isFetchingLines && isDownScroll && (panel[0].scrollTopMax - 20) <= currentScroll){
                // downward scroll, near bottom - so loading next lines.
                $.getJSON('<aksess:geturl url="/admin/tools/logreader/logfiles/"/>'+ filename + '.action?startline=' + (currentLine + numberOfLinesInput), function(data){
                    isFetchingLines = false;
                    currentLine = data.lineNumber;
                    if (data.lines.length > 0) {
                        panel.append(data.lines);
                        panel.children().slice(0, numberOfLinesInput).remove();
                    }
                })
            } else if(!isFetchingLines && !isDownScroll && currentScroll <= 20){
                // upward scroll, near top - so loading previous lines.
                $.getJSON('<aksess:geturl url="/admin/tools/logreader/logfiles/"/>'+ filename + '.action?startline=' + (currentLine - numberOfLinesInput), function(data){
                    isFetchingLines = false;
                    currentLine = data.lineNumber;
                    if (data.lines.length > 0) {
                        panel.prepend(data.lines);
                        var children = panel.children();
                        var childrenLength = children.length;
                        children.slice(childrenLength - numberOfLinesInput, childrenLength)
                    }
                })
            }

            lastScrollTop = currentScroll;
        });
    };
    $('#controlls').submit(updateContent);
</script>
</body>
</html>
