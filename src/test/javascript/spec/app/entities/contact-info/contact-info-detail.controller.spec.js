'use strict';

describe('Controller Tests', function() {

    describe('ContactInfo Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockContactInfo, MockStudio;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockContactInfo = jasmine.createSpy('MockContactInfo');
            MockStudio = jasmine.createSpy('MockStudio');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'ContactInfo': MockContactInfo,
                'Studio': MockStudio
            };
            createController = function() {
                $injector.get('$controller')("ContactInfoDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'studiocity2App:contactInfoUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
