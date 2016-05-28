(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .controller('ContactInfoDeleteController',ContactInfoDeleteController);

    ContactInfoDeleteController.$inject = ['$uibModalInstance', 'entity', 'ContactInfo'];

    function ContactInfoDeleteController($uibModalInstance, entity, ContactInfo) {
        var vm = this;

        vm.contactInfo = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ContactInfo.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
