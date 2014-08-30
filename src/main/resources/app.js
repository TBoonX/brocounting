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
      when('/transaction/:transactionId', {
        templateUrl: 'partials/transaction.html',
        controller: 'TransactionCtrl'
      });
  }]);

//create controllers
app.controller('MainController', [ '$http', function($http) {

} ]);

app.controller('LoginController', [ '$http', '$scope', function($http, $scope) {

} ]);

app.controller('TransactionController', [ '$http', '$scope', '$routeParams', function ($http, $scope, $routeParams) {

} ]);