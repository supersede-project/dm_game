/*
   (C) Copyright 2015-2018 The SUPERSEDE Project Consortium

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

var app = angular.module('w5app');

app.controllerProvider.register('game_details', function($scope, $http, $location) {

    var gameId = $location.search().gameId;
    var requirements = [];

    $scope.gameRequirements = {};
    $scope.solution = {};

    $http.get('supersede-dm-app/garp/game/gamerequirements?gameId=' + gameId)
    .success(function(data) {
        requirements = data;

        for (var i = 0; i < requirements.length; i++) {
            var currentRequirement = requirements[i];
            $scope.gameRequirements[currentRequirement.requirementId] = currentRequirement;
        }
    }).error(function(err){
        alert(err.message);
    });

    $http.get('supersede-dm-app/garp/game/solution?gameId=' + gameId)
    .success(function(data) {
        $scope.solution = data;
    }).error(function(err){
        alert(err.message);
    });

    $scope.getRequirement = function(requirementId) {
        return $scope.gameRequirements[requirementId];
    }

    $scope.closeGame = function() {
        $http.post('supersede-dm-app/garp/game/closegame?gameId=' + gameId)
    .success(function(data) {
        alert('Game successfully closed!');
        $location.url('supersede-dm-app/garp/home');
    }).error(function(err){
        alert(err.message);
    });
    }
});