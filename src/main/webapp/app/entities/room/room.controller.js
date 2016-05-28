(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .controller('RoomController', RoomController);

    RoomController.$inject = ['$scope', '$state', 'DataUtils', 'Room', 'RoomSearch'];

    function RoomController ($scope, $state, DataUtils, Room, RoomSearch) {
        var vm = this;
        
        vm.rooms = [];
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
        vm.search = search;

        loadAll();

        function loadAll() {
            Room.query(function(result) {
                vm.rooms = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            RoomSearch.query({query: vm.searchQuery}, function(result) {
                vm.rooms = result;
            });
        }    }
})();
