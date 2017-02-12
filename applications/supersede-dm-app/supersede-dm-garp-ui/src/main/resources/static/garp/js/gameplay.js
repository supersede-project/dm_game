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
app.controllerProvider.register('reqsCtrl', function($scope, $location, $http) {

    $scope.currentCriterion = {};
    $scope.requirements = [];
    $scope.lastCriterion = false;

    var gameId = $location.search().id;
    var currentCriterionIndex = 0;
    var rankings = {};
    var criteria = [];

    var getCurrentCriterion = function() {
        $http.get("supersede-dm-app/garp/game/gamecriteria?gameId=" + gameId)
        .success(function(data) {
            criteria = data;
            console.log("criteria:");
            console.log(criteria);
            $scope.currentCriterion = criteria[currentCriterionIndex];
            console.log("current criterion:");
            console.log($scope.currentCriterion);
            getRequirements();
        }).error(function(err){
            alert(err.message);
        });
    };

    var getRequirements = function() {
        console.log("current criterion:");
        console.log($scope.currentCriterion);
        $http.get('supersede-dm-app/garp/game/requirements?gameId=' + gameId + '&criterion=' + $scope.currentCriterion.criteriaId)
        .success(function(data) {
            $scope.requirements = data;
            console.log("current requirements:");
            console.log($scope.requirements);
            $("#sortable").jqxSortable();
        }).error(function(err){
            alert(err.message);
        });
    };

    $scope.saveCurrentOrdering = function() {
        var data = $("#sortable").jqxSortable("toArray");
        rankings[$scope.currentCriterion.criteriaId] = data;
        console.log("current rankings:");
        console.log(rankings);

        if ($scope.lastCriterion === false) {
            currentCriterionIndex++;
            console.log("current index:");
            console.log(currentCriterionIndex);

            if (currentCriterionIndex == criteria.length - 1) {
                $scope.lastCriterion = true;
                console.log("last criterion is true");
            }
        }

        getCurrentCriterion();
    };

    $scope.submitPriorities = function() {
        $http({
            url: "supersede-dm-app/garp/game/submit",
            data: rankings,
            method: 'POST',
            params: {gameId : gameId}
        }).success(function(){
            alert('Your prioritization was saved!');
            $location.url('supersede-dm-app/garp/home');
        }).error(function(err){
            alert(err.message);
        });
    };

    getCurrentCriterion();
});