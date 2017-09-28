var myApp = angular.module('myApp', ['ngRoute', 'ngResource', 'ConnectionSummaryService', 'FileUploadService']);

myApp.config(function ($routeProvider) {
    'use strict';

    $routeProvider.when('/', {
        templateUrl: '/app/upload/upload.html',
        controller: 'UploadCtrl'
    }).when('/files', {
        templateUrl: '/app/files/files.html',
        controller: 'FileCtrl'
    }).when('/graph', {
        templateUrl: '/app/graph/graph.html',
        controller: 'GraphCtrl'
    }).when('/connections', {
        templateUrl: '/app/connections/connection.html',
        controller: 'ConnectionsCtrl'
    }).otherwise({
        redirectTo: '/'
    });
});
