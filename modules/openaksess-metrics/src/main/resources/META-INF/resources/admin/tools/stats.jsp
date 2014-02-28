<%@ page contentType="text/html;charset=UTF-8" %>

<html>

<head>
<title>Metrics Charts</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery-2.0.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery.flot.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery.flot.time.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery.flot.pie.js"></script>
</head>

<body>
<p>
    Time between updates:
    <input id="updateInterval" type="text" style="text-align: right; width:5em" value="">
    milliseconds
</p>
<p>
    Number of updates simultaneous in one view:
    <input id="totalPoints" type="text" style="text-align: right; width:5em" value="">
</p>

<h3>Memory</h3>
<div id="memory" style="width:700px;height:300px"></div>
<h3>Memory usage</h3>
<div id="memoryusage" style="width:400px;height:200px"></div>

<h3>Heap</h3>
<div id="heap" style="width:700px;height:300px"></div>
<h3>Heap usage</h3>
<div id="heapusage" style="width:400px;height:200px"></div>

<h3>Database connections</h3>
<div id="db" style="width:700px;height:300px"></div>

<h3>Active requests</h3>
<div id="placeholder" style="width:700px;height:300px"></div>

<h3>Responses</h3>
<div id="responses" style="width:700px;height:300px"></div>


<script type="text/javascript">
    var updateInterval = 1000;
    $("#updateInterval").val(updateInterval).change(function () {
        var v = $(this).val();
        if (v && !isNaN(+v)) {
            updateInterval = +v;
            if (updateInterval < 200) {
                updateInterval = 200;
            } else if (updateInterval > 8000) {
                updateInterval = 8000;
            }
            $(this).val("" + updateInterval);
        }
    });
    var g_data = null;
    var requestValues = [];

    var memoryMax = [];
    var memoryUsed = [];
    var memoryInit = [];
    var memoryCommitted = [];

    var heapMax = [];
    var heapUsed = [];
    var heapInit = [];
    var heapCommitted = [];

    var maxConnections = [];
    var idleConnections = [];
    var openConnections = [];

    var heapUsage = [];
    var memoryUsage = [];

    var responseValues = [];

    var totalPoints = 500;
    $("#totalPoints").val(totalPoints).change(function () {
        var v = $(this).val();
        if (v && !isNaN(+v)) {
            totalPoints = +v;
            if (totalPoints < 100) {
                totalPoints = 100;
            } else if (totalPoints > 5000) {
                totalPoints = 5000;
            }
            $(this).val("" + totalPoints);
        }
    });
    function activeRequests(requests) {
        var d = new Date();
        if (requestValues.length > totalPoints)
            requestValues = requestValues.slice(0, requestValues.length - totalPoints);
        requestValues.push([d.getTime(), requests]);
        plot.setData([requestValues]);
        plot.draw();
        plot.setupGrid();
    }
    function memoryValues(memory) {
        var d = new Date();
        if (memoryMax.length > totalPoints){
            memoryMax = memoryMax.slice(0, memoryMax.length - totalPoints);
            memoryUsed = memoryUsed.slice(0, memoryUsed.length - totalPoints);
            memoryInit = memoryInit.slice(0, memoryInit.length - totalPoints);
            memoryCommitted = memoryCommitted.slice(0, memoryCommitted.length - totalPoints);
        }

        memoryMax.push([d.getTime(), memory.totalMax]);
        memoryUsed.push([d.getTime(), memory.totalUsed]);
        memoryInit.push([d.getTime(), memory.totalInit]);
        memoryCommitted.push([d.getTime(), memory.totalCommitted]);

        plot2.setData([
            {data:memoryMax, label:"Max"},
            {data:memoryInit, label:"Init"},
            {data:memoryCommitted, label:"Committed"},
            {data:memoryUsed, label:"Used"}
        ]);
        plot2.draw();
        plot2.setupGrid();
    }
    function heapValues(memory) {
        var d = new Date();
        if (heapMax.length > totalPoints) {
            heapMax = heapMax.slice(0, heapMax.length - totalPoints);
            heapUsed = heapUsed.slice(0, heapUsed.length - totalPoints);
            heapInit = heapInit.slice(0, heapInit.length - totalPoints);
            heapCommitted = heapCommitted.slice(0, heapCommitted.length - totalPoints);
        }

        heapMax.push([d.getTime(), memory.heapMax]);
        heapUsed.push([d.getTime(), memory.heapUsed]);
        heapInit.push([d.getTime(), memory.heapInit]);
        heapCommitted.push([d.getTime(), memory.heapCommitted]);

        plot3.setData([
            {data:heapMax, label:"Max"},
            {data:heapInit, label:"Init"},
            {data:heapCommitted, label:"Committed"},
            {data:heapUsed, label:"Used"}
        ]);
        plot3.draw();
        plot3.setupGrid();
    }
    function dbValues(db) {
        var d = new Date();
        if (maxConnections.length > totalPoints) {
            maxConnections = maxConnections.slice(0, maxConnections.length - totalPoints);
            idleConnections = idleConnections.slice(0, idleConnections.length - totalPoints);
            openConnections = openConnections.slice(0, openConnections.length - totalPoints);
        }

        maxConnections.push([d.getTime(), db['max-connections'].value]);
        idleConnections.push([d.getTime(), db['idle-connections'].value]);
        openConnections.push([d.getTime(), db['open-connections'].value]);

        plot4.setData([
            {data:maxConnections, label:"Max"},
            {data:idleConnections, label:"Idle"},
            {data:openConnections, label:"Open"}
        ]);
        plot4.draw();
        plot4.setupGrid();
    }
    function heapUsageValues(memory) {
        heapUsage = [
            { label:"Heap used", data: memory.heap_usage },
            { label:"Heap available", data: 1-memory.heap_usage }
        ];
        plot5.setData(heapUsage);
        plot5.draw();
    }
    function memoryUsageValues(memory) {
        memoryUsage = [
            { label:"Memory used", data: memory.non_heap_usage },
            { label:"Memory available", data: 1-memory.non_heap_usage }
        ];
        plot6.setData(memoryUsage);
        plot6.draw();
    }
    function responseCodes(webAppMetrics) {
        responseValues = [
            { label:"Bad requests", data:webAppMetrics['responseCodes.badRequest'].count},
            { label:"Created", data:webAppMetrics['responseCodes.created'].count },
            { label:"No content", data:webAppMetrics['responseCodes.noContent'].count },
            { label:"Not found", data:webAppMetrics['responseCodes.notFound'].count },
            { label:"Ok", data:webAppMetrics['responseCodes.ok'].count },
            { label:"Server error", data:webAppMetrics['responseCodes.serverError'].count },
            { label:"Other", data:webAppMetrics['responseCodes.other'].count }
        ];
        plot7.setData(responseValues);
        plot7.draw();
    }
    function update() {
        $.getJSON(
                '${pageContext.request.contextPath}/admin/metrics',
                function(data) {
                    g_data = data;

                    var memory = {
                        totalMax : data.gauges["total.max"].value,
                        totalUsed : data.gauges["total.used"].value,
                        totalInit : data.gauges["total.init"].value,
                        totalCommitted : data.gauges["total.committed"].value,
                        heapMax : data.gauges["heap.max"].value,
                        heapUsed : data.gauges["heap.used"].value,
                        heapInit : data.gauges["heap.init"].value,
                        heapCommitted : data.gauges["heap.committed"].value,
                        heap_usage : data.gauges["heap.usage"].value,
                        non_heap_usage : data.gauges["non-heap.usage"].value
                    }

                    var db = {
                        "max-connections": data.gauges["no.kantega.publishing.common.util.database.dbConnectionFactory.max-connections"],
                        "idle-connections": data.gauges["no.kantega.publishing.common.util.database.dbConnectionFactory.idle-connections"],
                        "open-connections": data.gauges["no.kantega.publishing.common.util.database.dbConnectionFactory.open-connections"]
                    }

                    var webAppMetrics = {
                        'responseCodes.badRequest': data.meters['com.codahale.metrics.servlet.AbstractInstrumentedFilter.responseCodes.badRequest'],
                        'responseCodes.created': data.meters['com.codahale.metrics.servlet.AbstractInstrumentedFilter.responseCodes.created'],
                        'responseCodes.noContent': data.meters['com.codahale.metrics.servlet.AbstractInstrumentedFilter.responseCodes.noContent'],
                        'responseCodes.notFound': data.meters['com.codahale.metrics.servlet.AbstractInstrumentedFilter.responseCodes.notFound'],
                        'responseCodes.ok': data.meters['com.codahale.metrics.servlet.AbstractInstrumentedFilter.responseCodes.ok'],
                        'responseCodes.other': data.meters['com.codahale.metrics.servlet.AbstractInstrumentedFilter.responseCodes.other'],
                        'responseCodes.serverError': data.meters['com.codahale.metrics.servlet.AbstractInstrumentedFilter.responseCodes.serverError']
                    }
                    var requests = data.counters["com.codahale.metrics.servlet.AbstractInstrumentedFilter.activeRequests"].count;

                    activeRequests(requests);
                    dbValues(db);
                    memoryValues(memory);
                    heapValues(memory);
                    heapUsageValues(memory);
                    memoryUsageValues(memory);
                    responseCodes(webAppMetrics);
                    setTimeout(update, updateInterval);
                });
    }

    function labelFormatter(label, series) {
        return "<div style='font-size:10pt; text-align:center; padding:1px; color:white;'>" + label + "<br/>" + Math.round(series.percent) + "%</div>";
    }

    var plot = $.plot("#placeholder", [ {data: requestValues, label: "Active Requests"} ],{
        series: {
            shadowSize: 0	// Drawing is faster without shadows
        },
        yaxis: {
            min: 0,
            max: 5
        },
        xaxis: {
            mode: "time"
        }
    });
    var plot2 = $.plot("#memory", [
        {data: memoryMax, label: "Max"},
        {data: memoryInit, label: "Init"},
        {data: memoryCommitted, label: "Committed"},
        {data: memoryUsed, label: "Used"}
    ],{
        series: {
            shadowSize: 0	// Drawing is faster without shadows
        },
        yaxis: {
            min: 0,
            max: 850000000
        },
        xaxis: {
            mode: "time"
        }
    });
    var plot3 = $.plot("#heap", [
        {data: heapMax, label: "Max"},
        {data: heapInit, label: "Init"},
        {data: heapCommitted, label: "Committed"},
        {data: heapUsed, label: "Used"}
    ],{
        series: {
            shadowSize: 0	// Drawing is faster without shadows
        },
        yaxis: {
            min: 0,
            max: 500000000
        },
        xaxis: {
            mode: "time"
        }
    });
    var plot4 = $.plot("#db", [
        {data: maxConnections, label: "Max"},
        {data: idleConnections, label: "Idle"},
        {data: openConnections, label: "Open"}
    ],{
        series: {
            shadowSize: 0	// Drawing is faster without shadows
        },
        yaxis: {
            min: 0,
            max: 55
        },
        xaxis: {
            mode: "time"
        }
    });

    var plot5 = $.plot('#heapusage', [ { label: "Heap used", data: 50},
            { label:"Heap available", data: 50 } ], {
        series: {
            pie: {
                show: true,
                radius: 1,
                tilt: 0.5,
                label: {
                    show: true,
                    radius: 1,
                    formatter: labelFormatter,
                    background: {
                        opacity: 0.8
                    }
                },
                combine: {
                    color: '#999',
                    threshold: 0.1
                }
            }
        },
        legend: {
            show: false
        }
    });
    var plot6 = $.plot('#memoryusage', [ { label: "Memory used", data: 50},
        { label:"Memory available", data: 50 } ], {
        series: {
            pie: {
                show: true,
                radius: 1,
                tilt: 0.5,
                label: {
                    show: true,
                    radius: 1,
                    formatter: labelFormatter,
                    background: {
                        opacity: 0.8
                    }
                },
                combine: {
                    color: '#999',
                    threshold: 0.1
                }
            }
        },
        legend: {
            show: false
        }
    });
    var plot7 = $.plot('#responses', [
        { label: "Bad requests", data: 50},
        { label: "Created", data: 50 },
        { label: "No content", data: 50 },
        { label: "Not found", data: 50 },
        { label: "Ok", data: 50 },
        { label: "Server error", data: 50 },
        { label: "Other", data: 50 }
    ], {
        series: {
            pie: {
                show: true,
                radius: 1,
                tilt: 0.5,
                label: {
                    show: true,
                    radius: 1,
                    formatter: labelFormatter,
                    background: {
                        opacity: 0.8
                    }
                },
                combine: {
                    color: '#999',
                    threshold: 0.1
                }
            }
        },
        legend: {
            show: true
        }
    });
    update();
</script>

</body>

</html>