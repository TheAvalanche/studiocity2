(function() {
    'use strict';
    angular
        .module('studiocity2App')
        .factory('ContactInfo', ContactInfo);

    ContactInfo.$inject = ['$resource'];

    function ContactInfo ($resource) {
        var resourceUrl =  'api/contact-infos/:id';

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
