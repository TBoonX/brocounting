//create module
var app = angular.module('brocountingDirectives', ['ngStorage']);

app.directive('tagIcon', ['$localStorage', function($localStorage) {
    return {
        restrict: 'E',
        transclude: true,
        scope: {
            name: '@tagName'
        },
        template: '<img ng-src="{{tagurl}}" />',
        controller: function($scope, $localStorage) {
            $scope.tagurl = '';
            
            var tags = $localStorage.tags;
            
            angular.forEach(tags, function(value, key) {
                var tag = value;
                
                if (tag.name == $scope.name)
                    $scope.tagurl = 'ressources/images/'+tag.icon;
            });
        }
    }
}]);