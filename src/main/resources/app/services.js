//create module
var app = angular.module('brocountingServices', ['ngRoute', 'ngResource', 'ngStorage']);

var serviceURL = 'http://127.0.0.1:8080/service';

//account manager
//app.factory('AccountManager', function('Accounts'){
//    
//});
//account ressource
app.factory('AccountRes', ['$resource', '$localStorage',
  function ($resource, $localStorage) {
        return $resource(serviceURL+'/account', {}, {
            getOne: {
                method: 'GET',
                params: {
                    hash: function () {
                        return $localStorage.hash;
                    }
                },
                isArray: false
            },
            getAll: {
                method: 'GET',
                params: {
                    hash: function () {
                        return $localStorage.hash;
                    }
                },
                isArray: true
            },
            update: {
                method: 'PUT',
                params: {
                    hash: function () {
                        return $localStorage.hash;
                    }
                },
                isArray: false
            }
        });
  }]);

//session ressource
app.factory('SessionRes', ['$resource', '$localStorage',
  function ($resource, $localStorage) {
        return $resource(serviceURL+'/session', {}, {
            login: {
                method: 'PUT',
                isArray: false
            },
            logout: {
                method: 'DELETE',
                params: {
                    hash: function () {
                        return $localStorage.hash;
                    }
                },
                isArray: false
            },
            isValid: {
                method: 'GET',
                params: {
                    hash: function () {
                        return $localStorage.hash;
                    }
                },
                isArray: false
            }
        });
  }]);

//tag ressource
app.factory('TagRes', ['$resource', '$localStorage',
  function ($resource, $localStorage) {
        return $resource(serviceURL+'/tag', {}, {
            create: {
                method: 'PUT',
                params: {
                    hash: function () {
                        return $localStorage.hash;
                    }
                },
                isArray: false
            },
            update: {
                method: 'PUT',
                params: {
                    hash: function () {
                        return $localStorage.hash;
                    }
                },
                isArray: false
            },
            remove: {
                method: 'DELETE',
                params: {
                    hash: function () {
                        return $localStorage.hash;
                    }
                },
                isArray: true
            },
            get: {
                method: 'GET',
                params: {
                    hash: function () {
                        return $localStorage.hash;
                    }
                },
                isArray: true
            }
        });
  }]);

//user ressource
app.factory('UserRes', ['$resource', '$localStorage',
  function ($resource, $localStorage) {
        return $resource(serviceURL+'/user', {}, {
            create: {
                method: 'PUT',
                isArray: false
            },
            remove: {
                method: 'DELETE',
                params: {
                    hash: function () {
                        return $localStorage.hash;
                    }
                },
                isArray: false
            }
        });
  }]);

//

//loading all data - init
app.service('InitialLoad', ['$localStorage', '$rootScope',
    function ($localStorage, $rootScope) {
        console.log('initial load of all data');

        var hash = $localStorage.hash;

        //get Accounts


        //get Tags


        //get Transactions



}]);


//login serivce
app.factory('Login', ['$rootScope', 'SessionRes', '$location',
    function ($rootScope, SessionRes, $location) {
        $rootScope.error = false;
        var service = {
            start: function () {
                console.log('Service Login');
                console.log($rootScope.credentials);

                document.getElementsByName("submit")[0].disabled = true;

                var hash = SessionRes.login({
                        name: $rootScope.credentials.name,
                        password: $rootScope.credentials.password
                    }, function (value, responseHeaders) {
                        console.log('success');

                        $rootScope.error = false;

                        document.getElementsByName("submit")[0].disabled = false;

                        console.log(value);

                        $rootScope.$storage.hash = value.hash;

                        $location.path('/');
                    },
                    function (httpResponse) {
                        console.log('failure');

                        document.getElementsByName("submit")[0].disabled = false;

                        $rootScope.error = true;

                        console.log(httpResponse);

                        var error_msg = "Fehler mit der Internetverbindung.";
                        switch (httpResponse.status) {
                        case 200:
                            error_msg = 'Ihre Eingabe war falsch.';
                        };
                        document.getElementsByName("loginerrormessage")[0].innerHTML = error_msg;
                    });
            }
        };

        return service;
}]);