(function() {
    'use strict';

    angular
        .module('studiocity2App')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('room', {
            parent: 'entity',
            url: '/room',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'studiocity2App.room.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/room/rooms.html',
                    controller: 'RoomController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('room');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('room-detail', {
            parent: 'entity',
            url: '/room/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'studiocity2App.room.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/room/room-detail.html',
                    controller: 'RoomDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('room');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Room', function($stateParams, Room) {
                    return Room.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('room.new', {
            parent: 'room',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/room/room-dialog.html',
                    controller: 'RoomDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                index: null,
                                description: null,
                                image: null,
                                imageContentType: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('room', null, { reload: true });
                }, function() {
                    $state.go('room');
                });
            }]
        })
        .state('room.edit', {
            parent: 'room',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/room/room-dialog.html',
                    controller: 'RoomDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Room', function(Room) {
                            return Room.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('room', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('room.delete', {
            parent: 'room',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/room/room-delete-dialog.html',
                    controller: 'RoomDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Room', function(Room) {
                            return Room.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('room', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
