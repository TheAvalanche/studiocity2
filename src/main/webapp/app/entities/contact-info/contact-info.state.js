(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('contact-info', {
            parent: 'entity',
            url: '/contact-info',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'studiocity2App.contactInfo.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/contact-info/contact-infos.html',
                    controller: 'ContactInfoController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contactInfo');
                    $translatePartialLoader.addPart('contactInfoType');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('contact-info-detail', {
            parent: 'entity',
            url: '/contact-info/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'studiocity2App.contactInfo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/contact-info/contact-info-detail.html',
                    controller: 'ContactInfoDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contactInfo');
                    $translatePartialLoader.addPart('contactInfoType');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ContactInfo', function($stateParams, ContactInfo) {
                    return ContactInfo.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('contact-info.new', {
            parent: 'contact-info',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/contact-info/contact-info-dialog.html',
                    controller: 'ContactInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                type: null,
                                value: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('contact-info', null, { reload: true });
                }, function() {
                    $state.go('contact-info');
                });
            }]
        })
        .state('contact-info.edit', {
            parent: 'contact-info',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/contact-info/contact-info-dialog.html',
                    controller: 'ContactInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ContactInfo', function(ContactInfo) {
                            return ContactInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('contact-info', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('contact-info.delete', {
            parent: 'contact-info',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/contact-info/contact-info-delete-dialog.html',
                    controller: 'ContactInfoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ContactInfo', function(ContactInfo) {
                            return ContactInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('contact-info', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
