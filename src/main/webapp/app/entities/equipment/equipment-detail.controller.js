(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .controller('EquipmentDetailController', EquipmentDetailController);

    EquipmentDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'Equipment', 'Room'];

    function EquipmentDetailController($scope, $rootScope, $stateParams, DataUtils, entity, Equipment, Room) {
        var vm = this;

        vm.equipment = entity;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('studiocity2App:equipmentUpdate', function(event, result) {
            vm.equipment = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
