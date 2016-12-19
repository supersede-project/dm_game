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
    var requirements = {};

    $scope.getGameRequirements = function() {
        $http.get('supersede-dm-app/garp/game/gamerequirements?gameId=' + gameId)
        .success(function(data) {
            getCriteriaRequirements(data);
        }).error(function(err){
            alert(err.message);
        });
    };

    var getRequirement = function(gameRequirements, criterion, i) {
        requirementId = gameRequirements[criterion][i];
        $http.get('supersede-dm-app/garp/game/requirement?requirementId=' + requirementId)
        .success(function(data) {
            console.log("found requirement:");
            console.log(data);
            requirements[criterion].push(data);
        }).error(function(err){
            alert(err.message);
        });
    };

    var getCriteriaRequirements = function(gameRequirements) {
        for (var criterion in gameRequirements) {
            requirements[criterion] = [];
            for (var i = 0; i < gameRequirements[criterion].length; i++) {
                getRequirement(gameRequirements, criterion, i);
            }
        }
        console.log("current requirements:");
        console.log(requirements);
    };

    $scope.getGameRequirements();
    console.log("current requirements:");
    console.log(requirements);
});
$(document).ready(function () {
    $('#jqxTabs').jqxTabs({ width: 700 });
    $(".sortable").jqxSortable();
    $(".jqxexpander").jqxExpander({ theme: "summer", expanded: false, width: 200});
});