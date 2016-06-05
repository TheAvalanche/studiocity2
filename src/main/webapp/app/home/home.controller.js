(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService', '$state', 'Studio', 'ParseLinks'];

    function HomeController ($scope, Principal, LoginService, $state, Studio, ParseLinks) {
        var vm = this;

        vm.studios = [];
        vm.page = 0;
        vm.loadPage = loadPage;

        loadAll();

        function loadAll () {
            Studio.query({
                page: vm.page,
                size: 20,
                sort: sort()
            }, onSuccess, onError);

            function sort() {
                return ['id'];
            }

            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                for (var i = 0; i < data.length; i++) {
                    vm.studios.push(data[i]);
                }
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function loadPage(page) {
            vm.page = page;
            loadAll();
        }
    }
})();
