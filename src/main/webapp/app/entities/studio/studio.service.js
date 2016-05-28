(function() {
    'use strict';
    angular
        .module('studiocity2App')
        .factory('Studio', Studio);

    Studio.$inject = ['$resource'];

    function Studio ($resource) {
        var resourceUrl =  'api/studios/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
