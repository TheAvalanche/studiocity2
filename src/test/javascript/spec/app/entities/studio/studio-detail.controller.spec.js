'use strict';

describe('Controller Tests', function() {

    describe('Studio Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockStudio, MockContactInfo, MockRoom;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockStudio = jasmine.createSpy('MockStudio');
            MockContactInfo = jasmine.createSpy('MockContactInfo');
            MockRoom = jasmine.createSpy('MockRoom');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Studio': MockStudio,
                'ContactInfo': MockContactInfo,
                'Room': MockRoom
            };
            createController = function() {
                $injector.get('$controller')("StudioDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'studiocity2App:studioUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
