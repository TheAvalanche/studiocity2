(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .controller('EquipmentDialogController', EquipmentDialogController);

    EquipmentDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Equipment', 'Room'];

    function EquipmentDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Equipment, Room) {
        var vm = this;

        vm.equipment = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.rooms = Room.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.equipment.id !== null) {
                Equipment.update(vm.equipment, onSaveSuccess, onSaveError);
            } else {
                Equipment.save(vm.equipment, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('studiocity2App:equipmentUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setImage = function ($file, equipment) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        equipment.image = base64Data;
                        equipment.imageContentType = $file.type;
                    });
                });
            }
        };

    }
})();
