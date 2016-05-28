(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('equipment', {
            parent: 'entity',
            url: '/equipment',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'studiocity2App.equipment.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/equipment/equipment.html',
                    controller: 'EquipmentController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('equipment');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('equipment-detail', {
            parent: 'entity',
            url: '/equipment/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'studiocity2App.equipment.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/equipment/equipment-detail.html',
                    controller: 'EquipmentDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('equipment');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Equipment', function($stateParams, Equipment) {
                    return Equipment.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('equipment.new', {
            parent: 'equipment',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/equipment/equipment-dialog.html',
                    controller: 'EquipmentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                description: null,
                                image: null,
                                imageContentType: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('equipment', null, { reload: true });
                }, function() {
                    $state.go('equipment');
                });
            }]
        })
        .state('equipment.edit', {
            parent: 'equipment',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/equipment/equipment-dialog.html',
                    controller: 'EquipmentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Equipment', function(Equipment) {
                            return Equipment.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('equipment', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('equipment.delete', {
            parent: 'equipment',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/equipment/equipment-delete-dialog.html',
                    controller: 'EquipmentDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Equipment', function(Equipment) {
                            return Equipment.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('equipment', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
