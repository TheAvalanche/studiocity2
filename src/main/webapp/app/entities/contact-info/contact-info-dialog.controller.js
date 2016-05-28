(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .controller('ContactInfoDialogController', ContactInfoDialogController);

    ContactInfoDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ContactInfo', 'Studio'];

    function ContactInfoDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ContactInfo, Studio) {
        var vm = this;

        vm.contactInfo = entity;
        vm.clear = clear;
        vm.save = save;
        vm.studios = Studio.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.contactInfo.id !== null) {
                ContactInfo.update(vm.contactInfo, onSaveSuccess, onSaveError);
            } else {
                ContactInfo.save(vm.contactInfo, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('studiocity2App:contactInfoUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
