'use strict';

//create module
var app = angular.module('brocounting', [ 'ngRoute', 'ngStorage', 'brocountingCtrl', 'brocountingServices', 'brocountingDirectives' ]);

//CORS
app.config(['$httpProvider', function($httpProvider) {
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];
    }
]);

//run configuration
app.run(function($rootScope, $localStorage) {
  $rootScope.hello = function() {   //define here global objects, visible in $scope everywhere
    console.log('hello');
  };
  $rootScope.$storage = $localStorage;
});

//create module variables
var sessionhash = 'blub';

//set routes
app.config(['$routeProvider',
  function ($routeProvider) {
    $routeProvider.
      when('/', {
        templateUrl: 'app/partials/main.html',
        controller: 'MainCtrl',
        controllerAs: 'mc'
      }).
      when('/login', {
        templateUrl: 'app/partials/login.html',
        controller: 'LoginCtrl',
        controllerAs: 'lc'
      }).
      when('/registration', {
        templateUrl: 'app/partials/registration.html',
        controller: 'RegistrationCtrl'
      }).
      when('/transaction/:transactionId', {
        templateUrl: 'app/partials/transaction.html',
        controller: 'TransactionCtrl'
      }).
      when('/tags', {
        templateUrl: 'app/partials/tags.html',
        controller: 'TagsCtrl'
      }).
      when('/tag/:tagId', {
        templateUrl: 'app/partials/tag.html',
        controller: 'TagCtrl'
      }).
      when('/statistic', {
        templateUrl: 'app/partials/statistic.html',
        controller: 'StatisticCtrl'
      }).
      when('/accounts', {
        templateUrl: 'app/partials/accounts.html',
        controller: 'AccountsCtrl'
      }).
      when('/account/:accountId', {
        templateUrl: 'app/partials/account.html',
        controller: 'AccountCtrl'
      }).
      otherwise({
        redirectTo: '/'
      });
  }]);