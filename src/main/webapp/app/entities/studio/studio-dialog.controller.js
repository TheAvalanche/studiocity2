(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .controller('StudioDialogController', StudioDialogController);

    StudioDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Studio', 'ContactInfo', 'Room'];

    function StudioDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Studio, ContactInfo, Room) {
        var vm = this;

        vm.studio = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.contactinfos = ContactInfo.query();
        vm.rooms = Room.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.studio.id !== null) {
                Studio.update(vm.studio, onSaveSuccess, onSaveError);
            } else {
                Studio.save(vm.studio, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('studiocity2App:studioUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setImage = function ($file, studio) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        studio.image = base64Data;
                        studio.imageContentType = $file.type;
                    });
                });
            }
        };

    }
})();
