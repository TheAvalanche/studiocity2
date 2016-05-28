(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .controller('ContactInfoController', ContactInfoController);

    ContactInfoController.$inject = ['$scope', '$state', 'ContactInfo', 'ContactInfoSearch'];

    function ContactInfoController ($scope, $state, ContactInfo, ContactInfoSearch) {
        var vm = this;
        
        vm.contactInfos = [];
        vm.search = search;

        loadAll();

        function loadAll() {
            ContactInfo.query(function(result) {
                vm.contactInfos = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            ContactInfoSearch.query({query: vm.searchQuery}, function(result) {
                vm.contactInfos = result;
            });
        }    }
})();
