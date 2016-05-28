'use strict';

describe('Controller Tests', function() {

    describe('Room Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockRoom, MockStudio, MockEquipment;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockRoom = jasmine.createSpy('MockRoom');
            MockStudio = jasmine.createSpy('MockStudio');
            MockEquipment = jasmine.createSpy('MockEquipment');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Room': MockRoom,
                'Studio': MockStudio,
                'Equipment': MockEquipment
            };
            createController = function() {
                $injector.get('$controller')("RoomDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'studiocity2App:roomUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
