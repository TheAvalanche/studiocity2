(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .controller('ContactInfoDetailController', ContactInfoDetailController);

    ContactInfoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'ContactInfo', 'Studio'];

    function ContactInfoDetailController($scope, $rootScope, $stateParams, entity, ContactInfo, Studio) {
        var vm = this;

        vm.contactInfo = entity;

        var unsubscribe = $rootScope.$on('studiocity2App:contactInfoUpdate', function(event, result) {
            vm.contactInfo = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
