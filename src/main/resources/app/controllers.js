//create module
var app = angular.module('brocountingCtrl', ['ngRoute', 'ngResource', 'ngStorage']);

//create constants:
//app.value('clientId', 'a12345654321x');

app.controller('MainCtrl', ['$http', '$scope', '$rootScope', '$location', 'SessionRes', 'InitialLoad', 
    function ($http, $scope, $rootScope, $location, SessionRes, InitialLoad) {
        console.log("MainCtrl");
        //delete $scope.$storage.hash;
        //get hash
        var hash = $scope.$storage.hash;
        //go to login if not set yet
        if (hash === null || hash === undefined) {
            $location.path('/login');
            return;
        }

        var onError = function () {

            //TODO: show hint
            alert("Sie müssen sich einloggen!");

            $location.path('/login');
        };

        //check hash
        SessionRes.isValid({}, function (value, responseHeaders) {
                console.log('success');

                console.log(value);

                if (value.response === undefined || value.response === false)
                    onError();
                else {
                    InitialLoad.start();
                }
            },
            function (httpResponse) {
                console.log('failure');

                onError();
            });

        /*
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
    */
}]);

app.controller('LoginCtrl', ['$rootScope', 'Login',
    function ($rootScope, Login) {
        console.log("LoginCtrl");

        $rootScope.credentials = {
            name: '',
            password: ''
        };

        this.login = Login.start;
}]);

app.controller('TransactionCtrl', ['$http', '$scope', '$routeParams', '$localStorage', 'TransactionRes', '$location',
    function ($http, $scope, $routeParams, $localStorage, TransactionRes, $location) {
        var transactionId = $routeParams.transactionId;
        $scope.transaction = {};
        
        //find transaction
        var i = 0;

        while (i < $localStorage.allTransactions.length) {
            if ($localStorage.allTransactions[i].id === transactionId) {
                $scope.transaction = $localStorage.allTransactions[i];
                break;
            }
            i = i+1;
        }
        
        this.save = function() {
            console.log("save transaction");
            
            if ($scope.transaction.id != null && $scope.transaction.id != undefined)
            {
                TransactionRes.create({transaction: $scope.transaction}, function (value, responseHeaders) {
                    console.log('success');

                    console.log(value);

                    if (value.response === undefined || value.response === false)
                        alert(responseHeaders);
                    else {
                        $location.path('/');
                    }
                },
                function (httpResponse) {
                    console.log('failure');

                    alert(httpResponse);
                });
            }
            else
            {
                TransactionRes.update({transaction: $scope.transaction}, function (value, responseHeaders) {
                    console.log('success');

                    console.log(value);

                    if (value.response === undefined || value.response === false)
                        alert(responseHeaders);
                    else {
                        $location.path('/');
                    }
                },
                function (httpResponse) {
                    console.log('failure');

                    alert(httpResponse);
                });
            }
        };
        
        if (this.transaction !== null && this.transaction !== undefined)
            this.header = "Transaktion "+this.transaction;
        else
            this.header = "Neue Transaktion";
        
        this.tagClicked = function(tag) {
            //only remove tag
            $scope.transaction.tags.pop(tag);
        };
        
        this.newTag = function() {
            //TODO
        };
}]);

app.controller('RegistrationCtrl', ['$http', '$scope',
    function ($http, $scope, $routeParams) {
        //end with history.back();
}]);

app.controller('TagsCtrl', ['$http', '$scope',
    function ($http, $scope) {

}]);

app.controller('TagCtrl', ['$http', '$scope', '$routeParams',
    function ($http, $scope, $routeParams) {

}]);

app.controller('StatisticCtrl', ['$http', '$scope',
    function ($http, $scope) {

}]);

app.controller('AccountsCtrl', ['$http', '$scope',
    function ($http, $scope) {

}]);

app.controller('AccountCtrl', ['$http', '$scope', '$routeParams',
    function ($http, $scope, $routeParams) {

}]);