<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%--
~ URL: admin/metrics
--%>
<html>

<head>
    <title>Metrics Charts</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/nv.d3.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/d3.v2.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/nv.d3.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/aksess/js/jquery-1.5.2.min.js"></script>
    <script type="text/javascript">
        var memoryValues = [];
        var lineMemoryValues = [];

        $(document).ready(function(){
            $.getJSON(
                    '${pageContext.request.contextPath}/admin/metrics',
                    function(data) {
                        var memory = data.jvm.memory;
                        var items = {
                                'totalInit': memory.totalInit,
                                'totalUsed' : memory.totalUsed,
                                'totalMax' : memory.totalMax,
                                'totalCommitted' : memory.totalCommitted
                            };
//                        var listItems = [];
//                        $.each(items, function(key, val) {
//                            listItems.push('<li id="' + key + '">' + val + '</li>');
//                        });
//                        $('<ul/>', {
//                            'class': 'my-new-list',
//                            html: listItems.join('')
//                        }).appendTo('body');

                        var d = new Date();
                        lineMemoryValues = [
                            {
                                key : 'TotalInit',
                                values : [ { y:items.totalInit, x:d } ] ,
                                color : '#ff7f0e'
                            },
                            {
                                key : "TotalUsed",
                                values : [ { y:items.totalUsed, x:d }  ] ,
                                color : '#2ca02c'
                            },
                            {
                                key : "TotalMax",
                                values : [ { y:items.totalMax, x:d }  ] ,
                                color : '#629FCA'
                            },
                            {
                                key : "TotalCommitted",
                                values : [  { y:items.totalCommitted, x:d }  ],
                                color : '#B3C4DB'
                            }
                        ];
                        updateLineGraph(lineMemoryValues);
                        memoryValues = [
                            {
                                "key" : "TotalInit",
                                "values" : [ [d, items.totalInit]  ]
                            },
                            {
                                "key" : "TotalUsed",
                                "values" : [ [d,items.totalUsed]  ]
                            },
                            {
                                "key" : "TotalMax",
                                "values" : [ [d,items.totalMax]  ]
                            },
                            {
                                "key" : "TotalCommitted",
                                "values" : [ [d, items.totalCommitted ] ]
                            }
                        ];
//                        updateMemoryGraph(memoryValues);
                    });
        });
        window.setInterval(function(){
            $.getJSON(
                    '${pageContext.request.contextPath}/admin/metrics',
                    function(data) {
                        var memory = data.jvm.memory;
                        var d = new Date();
                        lineMemoryValues[0].values.push({ y:memory.totalInit, x:d });
                        lineMemoryValues[1].values.push({ y:memory.totalUsed, x:d });
                        lineMemoryValues[2].values.push({ y:memory.totalMax, x:d });
                        lineMemoryValues[3].values.push({ y:memory.totalCommitted, x:d });

                        updateLineGraph(lineMemoryValues);

                        memoryValues[0].values.push([d, memory.totalInit]);
                        memoryValues[1].values.push([d, memory.totalUsed]);
                        memoryValues[2].values.push([d, memory.totalMax]);
                        memoryValues[3].values.push([d, memory.totalCommitted]);
//                    updateMemoryGraph(memoryValues)
                    });
        }, 2000);         // Change to 5000

        function updateLineGraph(items){
            nv.addGraph(function() {
                var chart = nv.models.lineChart();

                chart.xAxis
                        .axisLabel('Time')
                        .tickFormat(function(d){ return getClockTime(d)});

                chart.yAxis
                        .axisLabel('Memory')
                        .tickFormat(d3.format(',f'));

                d3.select('#chart1 svg')
                        .datum(items)
                        .transition().duration(500)
                        .call(chart);

                nv.utils.windowResize(chart.update);

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
            var timeString = hour +
                    ':' +
                    minute +
                    ':' +
                    second
            return timeString;
        }

        function updateMemoryGraph(items) {
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
            height: 400px;
        }
    </style>
</head>

<body>
    <div class="chart" id="chart1">
        <svg></svg>
    </div>
    <div class="chart" id="chart2">
        <svg></svg>
    </div>

</body>

</html>