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
    var gameId = $location.search().id;
    $scope.requirements = [];

    $scope.getGameRequirements = function() {
        $http.get('supersede-dm-app/garp/game/gamerequirements?gameId=' + gameId)
        .success(function(data) {
            getCriteriaRequirements(data);
        }).error(function(err){
            alert(err.message);
        });
    };

    var getRequirement = function(gameRequirements, criterionId, i) {
        var requirementId = gameRequirements[criterionId][i];
        $http.get('supersede-dm-app/garp/game/requirement?requirementId=' + requirementId)
        .success(function(data) {
            console.log("found requirement:");
            console.log(data);
            for (var i = 0; i < $scope.requirements.length; i++) {
                if ($scope.requirements[i].id == criterionId) {
                    $scope.requirements[i].requirements.push(data);
                }
            }
        }).error(function(err){
            alert(err.message);
        });
    };

    var getCriteriaRequirements = function(gameRequirements) {
        $scope.requirements = [];
        for (var criterionId in gameRequirements) {
            var criterion = {};
            criterion.id = criterionId;
            criterion.requirements = [];
            $scope.requirements.push(criterion);
            for (var i = 0; i < gameRequirements[criterionId].length; i++) {
                getRequirement(gameRequirements, criterionId, i);
            }
        }
        console.log("current requirements:");
        console.log($scope.requirements);
    };

    $scope.getGameRequirements();
    console.log("current requirements:");
    console.log($scope.requirements);
});
$(document).ready(function () {
    $('#jqxTabs').jqxTabs({ width: 700 });
    $(".sortable").jqxSortable();
    $(".jqxexpander").jqxExpander({ theme: "summer", expanded: false, width: 200});
});