angular.module('FileUploadService', [ 'ngResource' ]).factory('FileUpload', [ "$resource", function($resource) {
    return $resource("/api/files/:pfd", {
        fId : '@fId'
    }, {
        query : {
            method : 'GET',
            url : '/api/files',
            isArray : true
        },
        findByCode : {
            method : 'GET',
            url : '/api/files/searchFile/:code',
            isArray : true
        },
        remove : {
            method : 'DELETE',
            url : '/api/files/:code',
            isArray : false
        }
    });
} ]);

angular.module('ConnectionSummaryService', [ 'ngResource' ]).factory('ConnectionSummary', [ "$resource", function($resource) {
    return $resource("/api/connectionSummary/:cId", {
        cId : '@cId'
    }, {
        summary : {
            method : 'GET',
            url : '/api/connectionSummary/summary',
            isArray : true
        },
        findByCode : {
            method : 'GET',
            url : '/api/connectionSummary/:code',
            isArray : true
        }
    });
} ]);