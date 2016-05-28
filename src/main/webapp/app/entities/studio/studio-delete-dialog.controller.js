(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .controller('StudioDeleteController',StudioDeleteController);

    StudioDeleteController.$inject = ['$uibModalInstance', 'entity', 'Studio'];

    function StudioDeleteController($uibModalInstance, entity, Studio) {
        var vm = this;

        vm.studio = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Studio.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
