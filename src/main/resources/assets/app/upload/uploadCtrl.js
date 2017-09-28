myApp.controller('UploadCtrl', ["$scope", "$location", 'fileUpload',
    function ($scope, $location, fileUpload) {
        d3.select("svg").remove();
        $scope.uploadFile = function (type) {
            var file = $scope.myFile;
            var uploadCode = $scope.uploadCode;
            console.log('uploadCode is ' + uploadCode);
            var uploadUrl = "/api/files/upload";
            fileUpload.uploadFileToUrl(file, uploadCode, uploadUrl, type);
        };

    }]);

myApp.service('fileUpload', ['$http', function ($http) {
    this.uploadFileToUrl = function (file, uploadCode, uploadUrl, type) {
        var data = new FormData();
        data.append('file', file);
        data.append('uploadCode', uploadCode);
        if (type == 'post') {
            $http.post(uploadUrl, data, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            })
                .success(function () {
                })
                .error(function () {
                });
        } else {
            $http.patch(uploadUrl, data, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            })
                .success(function () {
                })
                .error(function () {
                });
        }

    }
}]);

myApp.directive('fileModel', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;
            element.bind('change', function () {
                scope.$apply(function () {
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}]);