(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .controller('StudioDetailController', StudioDetailController);

    StudioDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'Studio', 'ContactInfo', 'Room'];

    function StudioDetailController($scope, $rootScope, $stateParams, DataUtils, entity, Studio, ContactInfo, Room) {
        var vm = this;

        vm.studio = entity;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('studiocity2App:studioUpdate', function(event, result) {
            vm.studio = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
