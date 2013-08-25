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
<script src="<aksess:geturl url="/admin/js/jquery-ui-1.10.3.custom.min.js"/>"></script>
<script>
    var debug = function (msg) {
        if (typeof console != 'undefined') {
            console.log(msg);
        }
    };

    var getUrlForFile = function(file, start, number){
        var url = '<aksess:geturl url="/admin/tools/logreader/logfiles/"/>' + file + '.action?numberoflines=' + number + '&startline=' + start;
        debug(url);
        return url;
    };
    var isDownScrollDetect = function(lastScrollTop, currentScroll){
        return currentScroll > 0 && currentScroll >= lastScrollTop;
    };

    var tabheaderUl = $('#tabheaders');
    var filesByIndex = {};


    var numberOfLinesInput = $('#numberOfLines');
    var startLineInput = $('#startLine');

    var numberOfLinesToFetch = parseInt(numberOfLinesInput.val());
    var startLine = parseInt(startLineInput.val());
    var currentLine = startLine;

    $.getJSON('<aksess:geturl url="/admin/tools/logreader/logfiles.action"/>', function(data){
        for(var i = 0; i < data.length ; i++){
            var logfile = data[i];
            filesByIndex[i + 1] = logfile; // logfilecontainer.index() is 1-indexed.
            tabheaderUl.append('<li><a class="logheader" href="' + getUrlForFile(logfile, startLine, numberOfLinesToFetch)
                    + '">' + logfile + '</a><a href="<aksess:geturl url="/admin/tools/logreader/logfiles/download/"/>'
                    + logfile + '.action" class="downloadlogfile"></a></li>')
        }
        $('#logfiles').tabs({
            beforeLoad: function( event, ui ) {
                ui.ajaxSettings.dataType = 'html';
                ui.ajaxSettings.dataTypes = ['html'];
                ui.ajaxSettings.dataFilter = function(result){
                    var data = $.parseJSON(result);
                    startLine = parseInt(data.lineNumber) - parseInt(data.numberOfLinesReturned);
                    currentLine = data.lineNumber;
                    return data.lines;
                };
                ui.jqXHR.fail(function(data, errorname, error) {
                    debug(errorname + ' ' + error);
                });
            },
            activate: function( event, ui ) {
                var handler = function (e, elName) {
                    debug('onscroll ' + e + ' ' + elName)
                };
                ui.oldPanel[0]['onscroll']  = handler;
                ui.oldPanel[0]['onwheel']  = handler;
                ui.oldPanel[0]['onmousewheel']  = handler;

            }
        });
        registerScrollHandler();
    });

    var registerScrollHandler = function(){
        var lastScrollTop = 0;
        var isFetchingLines = false;
        var scrollhandler = function(){
            var tab = $('.ui-tabs-active');
            var index = tab.index();
            var filename = filesByIndex[index];
            var panel = $('#ui-tabs-' + index);


            var currentScroll = panel.scrollTop();
            var isDownScroll = isDownScrollDetect(lastScrollTop, currentScroll);
            var distanceToBottom = panel[0].scrollHeight - panel[0].clientHeight - currentScroll;
            if (!isFetchingLines && isDownScroll && distanceToBottom < 50){
                debug('downward scroll, near bottom - so loading next lines.');
                isFetchingLines = true;
                var downurl = '<aksess:geturl url="/admin/tools/logreader/logfiles/"/>' + filename + '.action?startline=' + (currentLine + numberOfLinesToFetch);
                debug(downurl);
                $.getJSON(downurl, function(data){
                    isFetchingLines = false;
                    currentLine = data.lineNumber;
                    debug('Results from server: currentLine: ' + currentLine + ' numberOfLinesInFile: ' + data.numberOfLinesInFile + ' numberOfLinesReturned:' + data.numberOfLinesReturned);
                    if (data.lines.length > 0) {
                        panel.append(data.lines);
                        panel.children().slice(0, data.numberOfLinesReturned).remove();
                    }
                })
            } else if(!isFetchingLines && !isDownScroll && currentScroll <= 50){
                debug('upward scroll, near top - so loading previous lines.');
                isFetchingLines = true;
                var upurl = '<aksess:geturl url="/admin/tools/logreader/logfiles/"/>' + filename + '.action?startline=' + (currentLine - numberOfLinesToFetch);
                debug(upurl);
                $.getJSON(upurl, function(data){
                    isFetchingLines = false;
                    currentLine = data.lineNumber;
                    debug('Results from server: currentLine: ' + currentLine + ' numberOfLinesInFile: ' + data.numberOfLinesInFile + ' numberOfLinesReturned:' + data.numberOfLinesReturned);
                    if (data.lines.length > 0) {
                        panel.prepend(data.lines);
                        var children = panel.children();
                        children.slice(-data.numberOfLinesReturned).remove();
                    }
                })
            }

            lastScrollTop = currentScroll;
        };
        var handler = function (e, elName) {
            debug('onscroll ' + e + ' ' + elName)
        };
        window.onscroll  = scrollhandler;
        window.onwheel  = scrollhandler;
        window.onmousewheel  = scrollhandler;
    };

    $('#controlls').submit(function () {
        var selected = $('.ui-tabs-selected').index();
        var panel = $($('.ui-tabs-panel')[selected - 1]);
        numberOfLinesToFetch = parseInt(numberOfLinesInput.val());
        startLine = parseInt(startLineInput.val());
        $.getJSON(getUrlForFile(filesByIndex[selected], startLine, numberOfLinesToFetch), function (data) {
            currentLine = data.lineNumber;
            panel.html(data.lines);
        });
        return false;
    });

</script>
</body>
</html>
