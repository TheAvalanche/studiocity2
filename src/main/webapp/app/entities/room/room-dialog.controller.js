(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .controller('RoomDialogController', RoomDialogController);

    RoomDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Room', 'Studio', 'Equipment'];

    function RoomDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Room, Studio, Equipment) {
        var vm = this;

        vm.room = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.studios = Studio.query();
        vm.equipment = Equipment.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.room.id !== null) {
                Room.update(vm.room, onSaveSuccess, onSaveError);
            } else {
                Room.save(vm.room, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('studiocity2App:roomUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setImage = function ($file, room) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        room.image = base64Data;
                        room.imageContentType = $file.type;
                    });
                });
            }
        };

    }
})();
