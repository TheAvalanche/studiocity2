(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .controller('RoomDetailController', RoomDetailController);

    RoomDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'Room', 'Studio', 'Equipment'];

    function RoomDetailController($scope, $rootScope, $stateParams, DataUtils, entity, Room, Studio, Equipment) {
        var vm = this;

        vm.room = entity;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('studiocity2App:roomUpdate', function(event, result) {
            vm.room = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
