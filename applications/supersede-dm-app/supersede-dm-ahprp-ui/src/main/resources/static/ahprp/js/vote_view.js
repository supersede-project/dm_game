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

app.controllerProvider.register('vote_view', function($scope, $http, $location) {
    
    $scope.playerMoveId = $location.search()['playerMoveId'];
    $scope.playerMove = undefined;
    $scope.requirementsChoices = [];
    $scope.selectedRequirementsChoice = {selected:4};
    
    $http.get('supersede-dm-app/ahprp/playermove/' + $scope.playerMoveId)
    .success(function(data) {
        $scope.playerMove = data;
    });
    
     $http.get('supersede-dm-app/requirementchoice')
        .success(function(data) {
            $scope.requirementsChoices.length = 0;
            for(var i = 0; i < data.length; i++)
            {
                $scope.requirementsChoices.push(data[i]);
            }
        });
     
     $scope.insertPlayerVote = function(){
         $http.put('supersede-dm-app/ahprp/playermove/' + $scope.playerMoveId + '/vote/' + $scope.selectedRequirementsChoice.selected)
            .success(function(data) {
                $location.url('/supersede-dm-app/ahprp/player_moves?gameId=' + data);
        });
     };
});