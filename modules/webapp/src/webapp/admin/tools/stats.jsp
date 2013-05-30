<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>

<head>
    <title>Metrics Charts</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/nv.d3.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/d3.v2.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/nv.d3.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/aksess/js/jquery-1.5.2.min.js"></script>
    <script type="text/javascript">
        var memoryValues = [];
        var heapValues = [];
        var requestValues = [];
        var dbValues = [];

        $(document).ready(function(){
            $.getJSON(
                    '${pageContext.request.contextPath}/admin/metrics',
                    function(data) {
                        var d = new Date();
                        var memory = data.jvm.memory;
                        var db = data["no.kantega.publishing.common.util.database.dbConnectionFactory"];
                        var filter = data["com.yammer.metrics.web.WebappMetricsFilter"];
                        var requests = filter.activeRequests.count;

                        requestValues = [{key : 'Active requests',
                            values : [{ x:d, y:requests }],
                            color : '#ff7f0e'
                        }];
                        updateLineGraph(requestValues, '#chart5 svg', 'Requests');
                        memoryValues = [
                            {
                                key : 'TotalInit',
                                values : [ { y:memory.totalInit, x:d } ] ,
                                color : '#ff7f0e'
                            },
                            {
                                key : "TotalUsed",
                                values : [ { y:memory.totalUsed, x:d }  ] ,
                                color : '#2ca02c'
                            },
                            {
                                key : "TotalMax",
                                values : [ { y:memory.totalMax, x:d }  ] ,
                                color : '#629FCA'
                            },
                            {
                                key : "TotalCommitted",
                                values : [  { y:memory.totalCommitted, x:d }  ],
                                color : '#B3C4DB'
                            }
                        ];
                        updateLineGraph(memoryValues, '#chart1 svg', 'Memory');
                        heapValues = [
                            {
                                key : 'HeapInit',
                                values : [ { y:memory.heapInit, x:d } ] ,
                                color : '#ff7f0e'
                            },
                            {
                                key : "HeapUsed",
                                values : [ { y:memory.heapUsed, x:d }  ] ,
                                color : '#2ca02c'
                            },
                            {
                                key : "HeapMax",
                                values : [ { y:memory.heapMax, x:d }  ] ,
                                color : '#629FCA'
                            },
                            {
                                key : "HeapCommitted",
                                values : [  { y:memory.heapCommitted, x:d }  ],
                                color : '#B3C4DB'
                            }
                        ];
                        updateLineGraph(heapValues, '#chart2 svg', 'Heap');

                        dbValues = [
                            {
                                key : 'Max',
                                values : [ { y:db["max-connections"].value, x:d } ] ,
                                color : '#ff7f0e'
                            },
                            {
                                key : "Idle",
                                values : [ { y:db["idle-connections"].value, x:d }  ] ,
                                color : '#2ca02c'
                            },
                            {
                                key : "Open",
                                values : [ { y:db["open-connections"].value, x:d }  ] ,
                                color : '#629FCA'
                            }
                        ];
                        updateLineGraph(dbValues, '#chart6 svg', 'Connections');
                    });
        });

        window.setInterval(function(){
            $.getJSON(
                    '${pageContext.request.contextPath}/admin/metrics',
                    function(data) {
                        var memory = data.jvm.memory;
                        var d = new Date();
                        memoryValues[0].values.push({ y:memory.totalInit, x:d });
                        memoryValues[1].values.push({ y:memory.totalUsed, x:d });
                        memoryValues[2].values.push({ y:memory.totalMax, x:d });
                        memoryValues[3].values.push({ y:memory.totalCommitted, x:d });

                        updateLineGraph(memoryValues, '#chart1 svg', 'Memory');

                        heapValues[0].values.push({ y:memory.heapInit, x:d });
                        heapValues[1].values.push({ y:memory.heapUsed, x:d });
                        heapValues[2].values.push({ y:memory.heapMax, x:d });
                        heapValues[3].values.push({ y:memory.heapCommitted, x:d });

                        updateLineGraph(heapValues, '#chart2 svg', 'Heap');

                        updateCakeGraph([{
                            key: "Heap",
                            values: [
                                { "label": "Heap used", "value" : memory.heap_usage } ,
                                { "label": "Heap available", "value" : 1 - memory.heap_usage }
                            ]}], '#chart4 svg');

                        updateCakeGraph([{
                            key: "Non Heap Usage",
                            values: [
                                { "label": "Memory used", "value" : memory.non_heap_usage },
                                { "label": "Memory available", "value" : 1 - memory.non_heap_usage }
                            ]}], '#chart3 svg' );

                        var filter = data["com.yammer.metrics.web.WebappMetricsFilter"];
                        var requests = filter.activeRequests.count;
                        requestValues[0].values.push({ y:requests, x:d });
                        updateLineGraph(requestValues, '#chart5 svg', 'Requests');

                        var db = data["no.kantega.publishing.common.util.database.dbConnectionFactory"];
                        dbValues[0].values.push({ y:db["max-connections"].value, x:d });
                        dbValues[1].values.push({ y:db["idle-connections"].value, x:d });
                        dbValues[2].values.push({ y:db["open-connections"].value, x:d });
                        updateLineGraph(dbValues, '#chart6 svg', 'Connections');
                    });
        }, 2500);

        function updateLineGraph(items, chartId, yAxis){
            nv.addGraph(function() {
                var chart = nv.models.lineChart();
                chart.xAxis
                        .axisLabel('Time')
                        .tickFormat(function(d){ return getClockTime(d)});
                chart.yAxis
                        .axisLabel(yAxis)
                        .tickFormat(d3.format(',f'));
                d3.select(chartId)
                        .datum(items)
                        .transition().duration(500)
                        .call(chart);

                nv.utils.windowResize(chart.update);
                return chart;
            });
        }
        function updateCakeGraph(items, chartId){
            nv.addGraph(function() {
                var chart = nv.models.pieChart()
                        .x(function(d) { return d.label })
                        .y(function(d) { return d.value })
                        .showLabels(true);
                d3.select(chartId)
                        .datum(items)
                        .transition().duration(1200)
                        .call(chart);
                return chart;
            });
        }
        function getClockTime(now){
            var date   = new Date(now);
            var hour   = date.getHours();
            var minute = date.getMinutes();
            var second = date.getSeconds();
            if (hour   < 10) { hour   = "0" + hour;   }
            if (minute < 10) { minute = "0" + minute; }
            if (second < 10) { second = "0" + second; }
            return hour + ':' + minute + ':' + second;
        }

        function updateTestGraph(items) {
            nv.addGraph(function() {
                var chart = nv.models.stackedAreaChart()
                        .x(function(d) { return d[0] })
                        .y(function(d) { return d[1] })
                        .clipEdge(false);
                chart.xAxis
                        .showMaxMin(false)
                        .tickFormat(function(d) {
                            return getClockTime(d)
                        });
                chart.yAxis
                        .axisLabel('Memory')
                        .tickFormat(d3.format(',f'));
                d3.select('#chart2 svg')
                        .datum(items)
                        .transition().duration(500).call(chart);
                nv.utils.windowResize(chart.update);
                return chart;
            });
        }

    </script>
    <style>
        .chart svg {
            height: 250px;
        }
    </style>
</head>

<body>
    <div class="chart" id="chart1">
        <h3>Memory</h3>
        <svg></svg>
    </div>
    <div class="chart" id="chart2">
        <h3>Heap</h3>
        <svg></svg>
    </div>
    <div class="chart" id="chart3">
        <h3>Memory usage</h3>
        <svg class="cake"></svg>
    </div>
    <div class="chart" id="chart4">
        <h3>Heap usage</h3>
        <svg class="cake"></svg>
    </div>
    <div class="chart" id="chart5">
        <h3>Active requests</h3>
        <svg></svg>
    </div>
    <div class="chart" id="chart6">
        <h3>Database connections</h3>
        <svg></svg>
    </div>
</body>

</html>