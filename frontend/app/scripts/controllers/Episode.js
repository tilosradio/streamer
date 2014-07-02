'use strict';


angular.module('tilosApp').config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/episode/:id', {
        templateUrl: 'partials/episode.html',
        controller: 'EpisodeCtrl',
        resolve: {
            data: function ($route, $http, API_SERVER_ENDPOINT) {
                return $http.get(API_SERVER_ENDPOINT + '/api/v0/episode/' + $route.current.params.id, {cache: true});
            },
            show: function ($route, $http, API_SERVER_ENDPOINT) {
                return $http.get(API_SERVER_ENDPOINT + "/api/v0/show/" + $route.current.params.show);
            },
        }
    });
    $routeProvider.when('/episode/:show/:year/:month/:day', {
        templateUrl: 'partials/episode.html',
        controller: 'EpisodeCtrl',
        resolve: {
            data: function ($route, $http, API_SERVER_ENDPOINT) {
                return $http.get(API_SERVER_ENDPOINT + "/api/v0/show/" + $route.current.params.show + "/episode/" + $route.current.params.year + "/" + $route.current.params.month + "/" + $route.current.params.day);
            },
            show: function ($route, $http, API_SERVER_ENDPOINT) {
                return $http.get(API_SERVER_ENDPOINT + "/api/v0/show/" + $route.current.params.show);
            },
        }
    });

}]);

/*global angular*/
angular.module('tilosApp')
    .controller('EpisodeCtrl', function ($scope, data, show, $sce, Meta) {
        $scope.episode = data.data;
        $scope.episode.text.formatted = $sce.trustAsHtml(data.data.text.formatted);
        $scope.currentShow = show.data;
        var start = new Date();
        start.setTime($scope.episode.plannedFrom * 1000);
        var dateStr = start.format("yyyy.mm.dd");
        if ($scope.episode.text.title) {
            Meta.setDescription(dateStr + " - " + $scope.episode.text.title);
        } else {
            Meta.setDescription(dateStr + " - " + $scope.currentShow.name + " adás");
        }
        Meta.setTitle($scope.currentShow.name + " adásnapló");
    }
);
