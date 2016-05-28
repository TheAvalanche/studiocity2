'use strict';

describe('Controller Tests', function() {

    describe('Equipment Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockEquipment, MockRoom;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockEquipment = jasmine.createSpy('MockEquipment');
            MockRoom = jasmine.createSpy('MockRoom');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Equipment': MockEquipment,
                'Room': MockRoom
            };
            createController = function() {
                $injector.get('$controller')("EquipmentDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'studiocity2App:equipmentUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
