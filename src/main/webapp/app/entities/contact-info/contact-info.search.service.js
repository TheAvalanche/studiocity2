(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .factory('ContactInfoSearch', ContactInfoSearch);

    ContactInfoSearch.$inject = ['$resource'];

    function ContactInfoSearch($resource) {
        var resourceUrl =  'api/_search/contact-infos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
