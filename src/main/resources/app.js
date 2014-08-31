'use strict';

//create module
var app = angular.module('brocounting', [ 'ngRoute', 'ngResource' ]);

app.config(['$httpProvider', function($httpProvider) {
        $httpProvider.defaults.useXDomain = true;
        delete $httpProvider.defaults.headers.common['X-Requested-With'];
    }
]);

//create module variables
var sessionhash = 'blub';

//set routes
app.config(['$routeProvider',
  function ($routeProvider) {
    $routeProvider.
      when('/', {
        templateUrl: 'app/partials/main.html',
        controller: 'MainCtrl'
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

//create controllers
app.controller('MainCtrl', [ '$http', '$scope', function ($http, $scope) {
    console.log("MainCtrl");
    
    //get sessionhash
    var sessionhash = lsGet('sessionhash');
    //go to login if not set yet
    if (sessionhash === null) {
        location.hash = '#/login';
        return;
    }
    
    
    //fill view
    //TODO
} ]);

app.controller('LoginCtrl', [ '$http', '$scope', 'Session', function ($http, $scope, Session) {
    console.log("LoginCtrl");
    
    this.credentials = {
        name: '',
        password: ''
    };
    
    this.login = function() {
        console.log(this.credentials);
        
        document.getElementsByName("submit")[0].disabled = true;
        
        var hash = Session.login({
            user_name: this.credentials.name,
            user_password: this.credentials.password
        }, function(sh) {
            document.getElementsByName("submit")[0].disabled = false;
            
            console.log(sh);
            
            lsSet("sessionhash", sh);
            
            //location.hash = '#/';
        });
        
        console.log(hash);
    };
} ]);

app.controller('TransactionCtrl', [ '$http', '$scope', '$routeParams', function ($http, $scope, $routeParams) {

} ]);

app.controller('RegistrationCtrl', [ '$http', '$scope', function ($http, $scope, $routeParams) {
    //end with history.back();
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
//all accounts
app.factory('Accounts', ['$resource',
  function ($resource){
    return $resource('service/accounts', {}, {
      get: {
          method: 'GET',
          params: {
              sessionhash: sessionhash
          },
          isArray:true
      }
    });
  }]);
//session
app.factory('Session', ['$resource',
  function ($resource){
    return $resource('http://127.0.0.1:8080/service/session/', {}, {
      login: {
          method: 'PUT',
          isArray: false
      },
      logout: {
          method: 'DELETE',
          params: {
            sessionhash: function(){return sessionhash;}
          },
          isArray: false
      }
    });
  }]);