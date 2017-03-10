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
    var gameStatus;
    var open = 'Open';

    $scope.gameRequirements = {};
    $scope.solution = [];
    $scope.gameId = gameId;
    $scope.procId = $location.search().procId;

    $scope.enact = function(gameId,useIf){
        $http.put('supersede-dm-app/garp/game/enact?gameId=' + $scope.gameId ).success(function(data) {
        });
    };

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

    $http.get('supersede-dm-app/garp/game/game?gameId=' + gameId)
    .success(function (data) {
        gameStatus = data.status;
    }).error(function (err) {
        alert(err.message);
    });

    $scope.getRequirement = function(requirementId) {
        return $scope.gameRequirements[requirementId];
    };

    $scope.closeGame = function() {
        $http.post('supersede-dm-app/garp/game/closegame?gameId=' + gameId + "&procId=" + $scope.procId)
        .success(function(data) {
            $("#game_status").html("<strong>Game successfully closed!</strong>");
        }).error(function(err){
            $("#game_status").html("<strong>Unable to close the game!</strong>");
            console.log(err.message);
        });
    };

    $scope.openGame = function () {
        $http.post('supersede-dm-app/garp/game/opengame?gameId=' + gameId + "&procId=" + $scope.procId)
        .success(function (data) {
            $("#game_status").html("<strong>Game successfully opened!</strong>");
        }).error(function (err) {
            $("#game_status").html("<strong>Unable to open the game!</strong>");
            console.log(err.message);
        });
    };

    $scope.solutionSelected = function() {
        return $scope.solution.length !== 0;
    };

    $scope.gameOpen = function() {
        return gameStatus == open;
    };

    $scope.gameClosed = function() {
        return gameStatus != open;
    };

    $scope.home = function() {
        $location.url('supersede-dm-app/garp/home');
    };
});