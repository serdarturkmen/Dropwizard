myApp.controller('FileCtrl', ["$scope", "$location", "FileUpload",
    function ($scope, $location, FileUpload) {
        d3.select("svg").remove();
        $scope.hello = "hello files"

        $scope.searchFiles = function (code) {
            $scope.files = FileUpload.findByCode({code: code});
        }

        $scope.deleteFiles = function(code){
            FileUpload.remove({code:code})
        }

    }]);
