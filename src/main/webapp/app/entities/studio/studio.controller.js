(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .controller('StudioController', StudioController);

    StudioController.$inject = ['$scope', '$state', 'DataUtils', 'Studio', 'StudioSearch'];

    function StudioController ($scope, $state, DataUtils, Studio, StudioSearch) {
        var vm = this;
        
        vm.studios = [];
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
        vm.search = search;

        loadAll();

        function loadAll() {
            Studio.query(function(result) {
                vm.studios = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            StudioSearch.query({query: vm.searchQuery}, function(result) {
                vm.studios = result;
            });
        }    }
})();
