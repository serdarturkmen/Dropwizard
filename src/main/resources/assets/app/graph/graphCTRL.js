myApp.controller('GraphCtrl', ["$scope", "$location", "$http", "ConnectionSummary",
    function ($scope, $location, $http, ConnectionSummary) {

    $scope.links=[];

        $scope.getGraph = function (code) {
            d3.select("svg").remove();
            ConnectionSummary.findByCode({code: code}, function (response) {
                $scope.initData(response);
            });
        }

        $scope.initData = function (list) {
            var count=1;
            angular.forEach(list, function (value) {
                count++;
                for (i = 0; i < value.eventCount; i++) {
                    $scope.links.push({source:value.sourceIp, target:value.protocol+"-"+count, type:'resolved'});
                    $scope.links.push({source:value.protocol+"-"+count, target:value.destinationIp, type:'resolved'});
                }

                }
            )
            $scope.initGraph();

        };


        $scope.initGraph = function () {


            $scope.nodeset = {};

            // Compute the distinct nodes from the links.
            $scope.links.forEach(function (link) {
                link.source = $scope.nodeset[link.source] || ($scope.nodeset[link.source] = {name: link.source});
                link.target = $scope.nodeset[link.target] || ($scope.nodeset[link.target] = {name: link.target});
            });

            $scope.links.forEach(function (d) {
                d.straight = 1;
                $scope.links.forEach(function (d1) {
                    if ((d.source == d1.target) && (d1.source == d.target))
                        d.straight = 0;
                });
            });

            $scope.nodes = d3.values($scope.nodeset);

            $scope.nodes.forEach(function (d) {
                d.started = 0;
                $scope.links.forEach(function (d1) {
                    if (d == d1.source)
                        d.started++;
                });
            });

            var width = 1200,
                height = 600;

            var force = d3.layout.force()
                .nodes($scope.nodes)
                .links($scope.links)
                .size([width, height])
                .linkDistance(40)
                .charge(-200)
                .on("tick", tick)
                .start();

            var svg = d3.select("body").append("svg")
                .attr("width", width)
                .attr("height", height);

// Per-type markers, as they don't inherit styles.
            svg.append("defs").selectAll("marker")
                .data(["suit", "licensing", "resolved"])
                .enter().append("marker")
                .attr("id", function (d) {
                    return d;
                })
                .attr("viewBox", "0 -5 10 10")
                .attr("refX", 15)
                .attr("refY", 0)
                .attr("markerWidth", 9)
                .attr("markerHeight", 5)
                .attr("orient", "auto")
                .append("path")
                .attr("d", "M0,-5L10,0L0,5");

            var path = svg.append("g").selectAll("path")
                .data(force.links())
                .enter().append("path")
                .attr("class", function (d) {
                    return "link " + d.type;
                })
                .attr("marker-end", function (d) {
                    return "url(#" + d.type + ")";
                });

            var circle = svg.append("g").selectAll("circle")
                .data(force.nodes())
                .enter().append("circle")
                .attr("r", function (d) {
                    return 5 + d.started;
                })
                .call(force.drag);

            var text = svg.append("g").selectAll("text")
                .data(force.nodes())
                .enter().append("text")
                .attr("x", function (d) {
                    return 8 + d.started;
                })
                .attr("y", ".31em")
                .text(function (d) {
                    return d.name;
                });

// Use elliptical arc path segments to doubly-encode directionality.
            function tick() {
                path.attr("d", linkArc);
                circle.attr("transform", transform);
                text.attr("transform", transform);
            }

            function linkArc(d) {
                var targetX = d.target.x - d.target.started,
                    targetY = d.target.y - d.target.started,
                    dx = targetX - d.source.x,
                    dy = targetY - d.source.y,
                    dr = (d.straight == 0) ? Math.sqrt(dx * dx + dy * dy) : 0;
                return "M" + d.source.x + "," + d.source.y +
                    " L " + targetX + "," + targetY;
            }

            function transform(d) {
                return "translate(" + d.x + "," + d.y + ")";
            }

        };


    }]);
