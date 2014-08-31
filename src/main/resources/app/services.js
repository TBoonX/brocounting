//create module
var app = angular.module('brocountingServices', [ 'ngRoute', 'ngResource', 'ngStorage' ]);

//account manager
//app.factory('AccountManager', function('Accounts'){
//    
//});
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
app.factory('Session', ['$resource', '$localStorage',
  function ($resource){
    return $resource('http://127.0.0.1:8080/service/session/', {}, {
      login: {
          method: 'PUT',
          isArray: false
      },
      logout: {
          method: 'DELETE',
          params: {
            sessionhash: function(){return $localStorage.sessionhash;}
          },
          isArray: false
      }
    });
  }]);


//login serivce
app.service('Login', ['$rootScope', 'Session', '$location', function ($rootScope, Session, $location) {
    $rootScope.error = false;
    var service = {
        login: function() {
            console.log('Service Login');
            console.log($rootScope.credentials);

            document.getElementsByName("submit")[0].disabled = true;

            var hash = Session.login({
                user_name: $rootScope.credentials.name,
                user_password: $rootScope.credentials.password
            }, function(sh) {
                console.log('success');

                $rootScope.error = false;

                document.getElementsByName("submit")[0].disabled = false;

                console.log(sh);

                $rootScope.$storage.sessionhash = sh.response;

                $location.path('/');
            },
            function(unknown) {
                console.log('failure');

                document.getElementsByName("submit")[0].disabled = false;

                $rootScope.error = true;

                console.log(unknown);

                var error_msg = "Fehler mit der Internetverbindung.";
                switch (unknown.status) {
                    case 200:   error_msg = 'Ihre Eingabe war falsch.';
                };
                document.getElementsByName("loginerrormessage")[0].innerHTML = error_msg;
            });
        }
    };
    
    return service;
}]);