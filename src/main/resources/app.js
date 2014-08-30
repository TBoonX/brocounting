'use strict';

//create module
var app = angular.module('brocounting', [ 'ngRoute', 'ngResource' ]);

//set routes
app.config(['$routeProvider',
  function ($routeProvider) {
    $routeProvider.
      when('/login', {
        templateUrl: 'app/partials/login.html',
        controller: 'LoginCtrl'
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
      });
  }]);

//create controllers
app.controller('MainCtrl', [ '$http', function ($http) {
    console.log("MainCtrl");
} ]);

app.controller('LoginCtrl', [ '$http', '$scope', function ($http, $scope) {
    console.log("LoginCtrl");
} ]);

app.controller('TransactionCtrl', [ '$http', '$scope', '$routeParams', function ($http, $scope, $routeParams) {

} ]);

app.controller('RegistrationCtrl', [ '$http', '$scope', function ($http, $scope, $routeParams) {

} ]);

app.controller('TagsCtrl', [ '$http', '$scope', function ($http, $scope) {

} ]);

app.controller('TagCtrl', [ '$http', '$scope', '$routeParams', function ($http, $scope, $routeParams) {

} ]);

app.controller('StatisticCtrl', [ '$http', '$scope', function ($http, $scope) {

} ]);

app.controller('AccountsCtrl', [ '$http', '$scope', function ($http, $scope) {

} ]);

app.controller('AccountCtrl', [ '$http', '$scope', '$routeParams', function ($http, $scope, $routeParams) {

} ]);


//set services
