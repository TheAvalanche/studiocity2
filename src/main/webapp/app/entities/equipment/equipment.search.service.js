(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .factory('EquipmentSearch', EquipmentSearch);

    EquipmentSearch.$inject = ['$resource'];

    function EquipmentSearch($resource) {
        var resourceUrl =  'api/_search/equipment/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
