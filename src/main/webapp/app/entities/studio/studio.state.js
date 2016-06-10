(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('studio', {
            parent: 'entity',
            url: '/studio',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'studiocity2App.studio.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/studio/studios.html',
                    controller: 'StudioController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('studio');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('studio-detail', {
            parent: 'entity',
            url: '/studio/{id}',
            data: {
                pageTitle: 'studiocity2App.studio.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/studio/studio-detail.html',
                    controller: 'StudioDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('studio');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Studio', function($stateParams, Studio) {
                    return Studio.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('studio.new', {
            parent: 'studio',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/studio/studio-dialog.html',
                    controller: 'StudioDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                description: null,
                                city: null,
                                street: null,
                                house: null,
                                index: null,
                                image: null,
                                imageContentType: null,
                                pricePerHour: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('studio', null, { reload: true });
                }, function() {
                    $state.go('studio');
                });
            }]
        })
        .state('studio.edit', {
            parent: 'studio',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/studio/studio-dialog.html',
                    controller: 'StudioDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Studio', function(Studio) {
                            return Studio.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('studio', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('studio.delete', {
            parent: 'studio',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/studio/studio-delete-dialog.html',
                    controller: 'StudioDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Studio', function(Studio) {
                            return Studio.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('studio', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
