(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .controller('EquipmentController', EquipmentController);

    EquipmentController.$inject = ['$scope', '$state', 'DataUtils', 'Equipment', 'EquipmentSearch'];

    function EquipmentController ($scope, $state, DataUtils, Equipment, EquipmentSearch) {
        var vm = this;
        
        vm.equipment = [];
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
        vm.search = search;

        loadAll();

        function loadAll() {
            Equipment.query(function(result) {
                vm.equipment = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            EquipmentSearch.query({query: vm.searchQuery}, function(result) {
                vm.equipment = result;
            });
        }    }
})();
