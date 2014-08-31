//create module
var app = angular.module('brocountingCtrl', [ 'ngRoute', 'ngResource', 'ngStorage' ]);

//create constants:
//app.value('clientId', 'a12345654321x');

app.controller('MainCtrl', [ '$http', '$scope', '$rootScope', '$location', function ($http, $scope, $rootScope, $location) {
    console.log("MainCtrl");
    //delete $scope.$storage.sessionhash;
    //get sessionhash
    var sessionhash = $scope.$storage.sessionhash;
    //go to login if not set yet
    if (sessionhash === null || sessionhash === undefined) {
        $location.path('/login');
        return;
    }
    
    
    //fill view
    this.transactions = [];
    
    //test
    this.transactions = [
        {
            id: 'nr1',
            agent: 'kurt',
            account: 'giro',
            amount: 12.95,
            date: new Date(),
            note: 'Rewe',
            tag: '1'
        },
        {
            id: 'nr2',
            agent: 'kurt',
            account: 'giro',
            amount: 20.0,
            date: new Date(),
            note: 'test',
            tag: '2'
        }
    ];
} ]);

app.controller('LoginCtrl', [ '$rootScope', 'Login', function ($rootScope, Login) {
    console.log("LoginCtrl");
    
    $rootScope.credentials = {
        name: '',
        password: ''
    };
    
    this.login = Login.login;
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