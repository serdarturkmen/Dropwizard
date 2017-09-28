myApp.controller('ConnectionsCtrl', ["$scope", "$location", "ConnectionSummary",
    function ($scope, $location, ConnectionSummary) {
        d3.select("svg").remove();
        $scope.hello = "hello files"

        $scope.searchFiles = function (code) {
            $scope.connections = ConnectionSummary.findByCode({code: code});

        }

    }]);
