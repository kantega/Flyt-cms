<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>

<head>
<title>Metrics Charts</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery-2.0.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery.flot.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery.flot.time.js"></script>
<script type="text/javascript">
    <%--window.setInterval(function(){--%>
        <%--$.getJSON(--%>
                <%--'${pageContext.request.contextPath}/admin/metrics',--%>
                <%--function(data) {--%>
                    <%--var memory = data.jvm.memory;--%>
                    <%--var d = new Date();--%>
//                    memoryValues[0].values.push({ y:memory.totalInit, x:d });
//                    memoryValues[1].values.push({ y:memory.totalUsed, x:d });
//                    memoryValues[2].values.push({ y:memory.totalMax, x:d });
//                    if(memoryValues[3].values.push({ y:memory.totalCommitted, x:d }) > 20) {
//                        $.each(memoryValues, function(){
//                            this['values'].shift()
//                        });
//                    }

//                    heapValues[0].values.push({ y:memory.heapInit, x:d });
//                    heapValues[1].values.push({ y:memory.heapUsed, x:d });
//                    heapValues[2].values.push({ y:memory.heapMax, x:d });
//                    if(heapValues[3].values.push({ y:memory.heapCommitted, x:d }) > 20) {
//                        $.each(heapValues, function(){
//                            this['values'].shift()
//                        });
//                    }

//                    updateCakeGraph([{
//                        key: "Heap",
//                        values: [
//                            { "label": "Heap used", "value" : memory.heap_usage } ,
//                            { "label": "Heap available", "value" : 1 - memory.heap_usage }
//                        ]}], '#chart4 svg');
//
//                    updateCakeGraph([{
//                        key: "Non Heap Usage",
//                        values: [
//                            { "label": "Memory used", "value" : memory.non_heap_usage },
//                            { "label": "Memory available", "value" : 1 - memory.non_heap_usage }
//                        ]}], '#chart3 svg' );

//                    var webAppMetrics = data["com.yammer.metrics.web.WebappMetricsFilter"];
//                    var requests = webAppMetrics.activeRequests.count;
//                    if(requestValues[0].values.push([d, requests ]) > 20) {
//                        requestValues[0].values.shift();
//                    }
//
//                    var db = data["no.kantega.publishing.common.util.database.dbConnectionFactory"];
//                    dbValues[0].values.push({ y:db["max-connections"].value, x:d });
//                    dbValues[1].values.push({ y:db["idle-connections"].value, x:d });
//                    if(dbValues[2].values.push({ y:db["open-connections"].value, x:d }) > 20) {
//                        $.each(dbValues,function(){
//                            this['values'].shift()
//                        });
//                    }
//
//                    responseValues[0].values.push({ y:webAppMetrics['responseCodes.badRequest'].count, x:d });
//                    responseValues[1].values.push({ y:webAppMetrics['responseCodes.created'].count, x:d });
//                    responseValues[2].values.push({ y:webAppMetrics['responseCodes.noContent'].count, x:d });
//                    responseValues[3].values.push({ y:webAppMetrics['responseCodes.notFound'].count, x:d });
//                    responseValues[4].values.push({ y:webAppMetrics['responseCodes.ok'].count, x:d });
//                    responseValues[5].values.push({ y:webAppMetrics['responseCodes.other'].count, x:d });
//                    if(responseValues[6].values.push({ y:webAppMetrics['responseCodes.serverError'].count, x:d }) > 20) {
//                        $.each(responseValues, function(){
//                            this['values'].shift()
//                        });
//                    }
//                });
//    }, 5000);
//    $(function() {
//        // We use an inline data source in the example, usually data would
//        // be fetched from a server
//        var data = [],
//                totalPoints = 300;
//
//        function getRandomData() {
//            if (data.length > 0)
//                data = data.slice(1);
//            // Do a random walk
//            while (data.length < totalPoints) {
//                var prev = data.length > 0 ? data[data.length - 1] : 50,
//                        y = prev + Math.random() * 10 - 5;
//                if (y < 0) {
//                    y = 0;
//                } else if (y > 100) {
//                    y = 100;
//                }
//                data.push(y);
//            }
//            // Zip the generated y values with the x values
//            var res = [];
//            for (var i = 0; i < data.length; ++i) {
//                res.push([i, data[i]])
//            }
//            return res;
//        }
//        var updateInterval = 30;
//        var plot = $.plot("#demo", [ getRandomData() ], {
//            series: {
//                shadowSize: 0	// Drawing is faster without shadows
//            },
//            yaxis: {
//                min: 0,
//                max: 100
//            },
//            xaxis: {
//                show: true
//            }
//        });
//        function update() {
//            plot.setData([getRandomData()]);
//            // Since the axes don't change, we don't need to call plot.setupGrid()
//            plot.draw();
//            setTimeout(update, updateInterval);
//        }
//        update();
//
//    });
</script>
</head>

<body>
<%--<div id="demo" style="width:600px;height:300px"></div>--%>
<h3>Memory</h3>
<div id="memory" style="width:600px;height:300px"></div>
<h3>Active requests</h3>
<div id="placeholder" style="width:600px;height:300px"></div>
<script type="text/javascript">
    var updateInterval = 1000;
    var memoryValues = [];
    var heapValues = [];
    var dbValues = [];
    var responseValues = [];

    var requestValues = [];

    var data = [],
            totalPoints = 200;

    function update() {
        $.getJSON(
                '${pageContext.request.contextPath}/admin/metrics',
                function(data) {
                    var webAppMetrics = data["com.yammer.metrics.web.WebappMetricsFilter"];
                    var requests = webAppMetrics.activeRequests.count;
                    var d = new Date();
                    if(requestValues.length == totalPoints)
                        requestValues = requestValues.slice(1);
                    requestValues.push([d.getTime(), requests]);
                    plot.setData([requestValues]);
                    plot.draw();
                    plot.setupGrid();
                    setTimeout(update, updateInterval);
                });
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
        {data: memoryValues, label: "Max"},
        {data: memoryValues, label: "Init"},
        {data: memoryValues, label: "Committed"},
        {data: memoryValues, label: "User"}
    ],{
        series: {
            shadowSize: 0	// Drawing is faster without shadows
        },
        yaxis: {
            min: 0,
            max: 5
        },
        xaxis: {
            show: true,
            mode: "time",
            max: 200
        }
    });

    update();
</script>

</body>

</html>