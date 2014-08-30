'use strict';

//create module
var app = angular.module('brocounting', [ 'ngRoute' ]);

//set routes
app.config(['$routeProvider',
  function ($routeProvider) {
    $routeProvider.
      when('/login', {
        templateUrl: 'partials/login.html',
        controller: 'LoginCtrl'
      }).
      when('/registration', {
        templateUrl: 'partials/registration.html',
        controller: 'RegistrationCtrl'
      })
      when('/transaction/:transactionId', {
        templateUrl: 'partials/transaction.html',
        controller: 'TransactionCtrl'
      }).
      when('/tags, {
        templateUrl: 'partials/tags.html',
        controller: 'TagsCtrl'
      }).
      when('/tag/:tagId', {
        templateUrl: 'partials/tag.html',
        controller: 'TagCtrl'
      }).
      when('/statistic', {
        templateUrl: 'partials/statistic.html',
        controller: 'StatisticCtrl'
      }).
      when('/accounts', {
        templateUrl: 'partials/accounts.html',
        controller: 'AccountsCtrl'
      }).
      when('/account/:accountId', {
        templateUrl: 'partials/account.html',
        controller: 'AccountCtrl'
      });
  }]);

//create controllers
app.controller('MainController', [ '$http', function($http) {

} ]);

app.controller('LoginController', [ '$http', '$scope', function($http, $scope) {

} ]);

app.controller('TransactionController', [ '$http', '$scope', '$routeParams', function ($http, $scope, $routeParams) {

} ]);

app.controller('RegistrationCtrl', [ '$http', '$scope', '$routeParams', function ($http, $scope, $routeParams) {

} ]);

app.controller('TagsCtrl', [ '$http', '$scope', '$routeParams', function ($http, $scope, $routeParams) {

} ]);

app.controller('TagCtrl', [ '$http', '$scope', '$routeParams', function ($http, $scope, $routeParams) {

} ]);

app.controller('StatisticCtrl', [ '$http', '$scope', '$routeParams', function ($http, $scope, $routeParams) {

} ]);

app.controller('AccountsCtrl', [ '$http', '$scope', '$routeParams', function ($http, $scope, $routeParams) {

} ]);

app.controller('AccountCtrl', [ '$http', '$scope', '$routeParams', function ($http, $scope, $routeParams) {

} ]);