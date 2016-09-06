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

app.controllerProvider.register('judge_act', function($scope, $http, $location) {
    
	$scope.judgeActId = $location.search()['judgeActId'];
    $scope.judgeAct = undefined;
	$scope.requirementsChoices = [];
	$scope.selectedRequirementsChoice = {selected:4};
	$scope.playerMoves = [];
	
	$http.get('game-requirements-gamification/judgeact/' + $scope.judgeActId)
	.success(function(data) {
		$scope.judgeAct = data;
		
		 $http.get('game-requirements-gamification/playermove/requirementsmatrixdata/' + $scope.judgeAct.requirementsMatrixData.requirementsMatrixDataId)
			.success(function(data) {
				for(var i = 0; i < data.length; i++)
				{
					$scope.playerMoves.push(data[i]);
				}
			});
	});
    
	 $http.get('game-requirements-gamification/requirementchoice')
		.success(function(data) {
			$scope.requirementsChoices.length = 0;
			for(var i = 0; i < data.length; i++)
			{
				$scope.requirementsChoices.push(data[i]);
			}
		});
	 
	 $scope.insertJudgeVote = function(judgeVote){
		 $http.put('game-requirements-gamification/judgeact/' + $scope.judgeActId + '/vote/' + judgeVote)
	    	.success(function(data) {
	    		$location.url('/game-requirements-gamification/judge_acts');
    	});
	 };	
});