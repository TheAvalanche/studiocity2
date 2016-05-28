(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .factory('RoomSearch', RoomSearch);

    RoomSearch.$inject = ['$resource'];

    function RoomSearch($resource) {
        var resourceUrl =  'api/_search/rooms/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
