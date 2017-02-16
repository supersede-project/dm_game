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

var app = angular.module("w5app");

app.controllerProvider.register("select_solution", function($scope, $http, $location) {
    var gameId = $location.search().gameId;
    var gameRequirements = {};
    var gameStatus;
    var open = 'Open';

    $scope.currentPage = "page1";
    $scope.ranking = {};
    $scope.solutions = [];

    $http.get('supersede-dm-app/garp/game/game?gameId=' + gameId)
    .success(function (data) {
        gameStatus = data.status;

        if ($scope.canSelectSolution()) {
            $http.get("supersede-dm-app/garp/game/ranking?gameId=" + gameId)
            .success(function (data) {
                $scope.ranking = data;
                console.log("current ranking:");
                console.log($scope.ranking);
            }).error(function (err) {
                alert(err.message);
            });

            $http.get("supersede-dm-app/garp/game/gamerequirements?gameId=" + gameId)
            .success(function (data) {

                for (var i = 0; i < data.length; i++) {
                    var requirementId = data[i].requirementId;
                    gameRequirements[requirementId] = data[i];
                }

                getSolutions();
            }).error(function (err) {
                alert(err.message);
            });
        }
    }).error(function (err) {
        alert(err.message);
    });

    function setCurrentPage(page) {
        $scope.currentPage = 'page' + page;
    }

    function getSolutions() {
        $http.get("supersede-dm-app/garp/game/calc?gameId=" + gameId)
        .success(function(data) {
            $scope.solutions = data;
        }).error(function(err){
            alert(err.message);
        });
    }

    $scope.getRequirement = function(requirementId) {
        return gameRequirements[requirementId];
    };

    $scope.showSolutions = function () {
        setCurrentPage(2);
    };

    $scope.selectSolution = function() {
        var solutionIndex = $("#select_solution").val();
        var selectedSolution = $scope.solutions[solutionIndex - 1];

        $http({
            url: "supersede-dm-app/garp/game/solution",
            data: selectedSolution,
            method: 'POST',
            params: {gameId : gameId}
        }).success(function(){
            $("#selected_solution").html("<strong>Solution successfully saved!</strong>");
        }).error(function(err){
            $("#selected_solution").html("<strong>Unable to save the solution!</strong>");
            console.log(err.message);
        });
    };

    $scope.emptyRanking = function() {
        return Object.keys($scope.ranking).length === 0;
    };

    $scope.canSelectSolution = function () {
        return gameStatus == open;
    };

    $scope.home = function() {
        $location.url('supersede-dm-app/garp/home');
    };
});