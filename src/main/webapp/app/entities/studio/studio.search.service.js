(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .factory('StudioSearch', StudioSearch);

    StudioSearch.$inject = ['$resource'];

    function StudioSearch($resource) {
        var resourceUrl =  'api/_search/studios/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
