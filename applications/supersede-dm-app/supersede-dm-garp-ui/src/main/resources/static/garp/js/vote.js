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
app.controllerProvider.register('vote', function($scope, $location, $http) {

    $scope.currentCriterion = {};
    $scope.requirements = [];
    $scope.lastCriterion = false;
    $scope.solution = [];

    $scope.procId = $location.search().procId;
    $scope.activityId = $location.search().activityId;
    
    var gameId = $location.search().id;
    
    var loadPage = function(gameId) {
    	
    	console.log("Building page");
    	
    	var gameStatus;
    	var gameRequirements = {};
    	var currentCriterionIndex = 0;
    	var rankings = {};
    	var criteria = [];
    	var closed = 'Closed';
    	var userRanking = {};
    	
        $http.get('supersede-dm-app/garp/game/game?gameId=' + gameId)
        .success(function (data) {
            gameStatus = data.status;

            $http.get('supersede-dm-app/garp/game/solution?gameId=' + gameId)
            .success(function (data) {
                $scope.solution = data;

                $http.get('supersede-dm-app/garp/game/userranking?gameId=' + gameId)
                .success(function (data) {
                    userRanking = data;

                    if ($scope.canVote()) {
                        getCurrentCriterion();
                    }
                }).error(function (err) {
                    alert(err.message);
                });
            }).error(function (err) {
                alert(err.message);
            });
        }).error(function (err) {
            alert(err.message);
        });

        var getCurrentCriterion = function() {
            $http.get("supersede-dm-app/garp/game/gamecriteria?gameId=" + gameId)
            .success(function(data) {
                criteria = data;
                console.log("criteria:");
                console.log(criteria);
                $scope.currentCriterion = criteria[currentCriterionIndex];

                if (criteria.length == 1) {
                    $scope.lastCriterion = true;
                }

                getRequirements();
            }).error(function(err){
                alert(err.message);
            });
        };

        var getRequirements = function() {
            $http.get('supersede-dm-app/garp/game/requirements?gameId=' + gameId + '&criterion=' + $scope.currentCriterion.criteriaId)
            .success(function(data) {
                $scope.requirements = data;

                for (var i = 0; i < data.length; i++) {
                    var requirementId = data[i].requirementId;
                    gameRequirements[requirementId] = data[i];
                }

                $("#sortable").jqxSortable();
            }).error(function(err){
                alert(err.message);
            });
        };

        $scope.getRequirement = function (requirementId) {
            return gameRequirements[requirementId];
        };

        function saveRanking() {
            var data = $("#sortable").jqxSortable("toArray");
            rankings[$scope.currentCriterion.criteriaId] = data;

            if ($scope.lastCriterion === false) {
                currentCriterionIndex++;

                if (currentCriterionIndex == criteria.length - 1) {
                    $scope.lastCriterion = true;
                }
            }
        }

        $scope.saveCurrentOrdering = function() {
            saveRanking();
            getCurrentCriterion();
        };

        $scope.submitPriorities = function() {
            saveRanking();
            console.log("saving rankings:");
            console.log(rankings);
            $http({
                url: "supersede-dm-app/garp/game/submit",
                data: rankings,
                method: 'POST',
                params: {gameId : gameId}
            }).success(function(){
                $("#voted").html("<strong>Rankings successfully saved!</strong>");
            }).error(function(err){
                $("#voted").html("<strong>Unable to save the rankings!</strong>");
                console.log(err.message);
            });
        };

        $scope.solutionSelected = function() {
            return $scope.solution.length !== 0;
        };

        $scope.alreadyVoted = function () {
            return Object.keys(userRanking).length !== 0;
        };

        $scope.gameClosed = function () {
            return gameStatus == closed;
        };

        $scope.gameOpen = function () {
            return gameStatus != closed;
        };

        $scope.canVote = function() {
            if ($scope.gameClosed()) {
                return false;
            }
            else if ($scope.alreadyVoted()) {
                if ($scope.solutionSelected()) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return true;
            }
        };

        $scope.home = function () {
            $location.url('supersede-dm-app/garp/home');
        };
    	
    };
    
    if( !(typeof gameId === 'undefined') ) {
        console.log( "Loading page directly" );
    	loadPage(gameId);
    }
    else 
    {
        console.log( "Getting remote mapping" );
    	$http({
            method: 'GET',
            url: "supersede-dm-app/garp/game/id",
            params: { procId: $scope.procId, activityId: $scope.activityId },
            headers: {
                'Content-Type': undefined
              }
        }).success(function(data){
            console.log( "Received: " + data );
//            $scope.selectedGame = data;
            loadPage(data);
    	});
    }
    
});