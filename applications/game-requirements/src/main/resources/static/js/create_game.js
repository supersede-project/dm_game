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

app.controllerProvider.register('create_game', function($scope, $http, $location) {

	$scope.now = function()
	{
		return new Date().toJSON().slice(0,19).replace("T", " ");
	}
	
    $scope.players = [];
    $scope.requirements = [];
    $scope.criterias = [];

    $scope.currentPlayer = undefined;
    $scope.currentRequirement= undefined;
    $scope.currentCriteria = undefined;
    
    $scope.game = {players : [], requirements: [], criterias: [], title: "Decision Making Process " + $scope.now()};
    
    $scope.currentPage = 'page1';
    
    $scope.requirementsChoices = [];
    
    $scope.choices = {};
    
    $http.get('game-requirements/user?profile=OPINION_PROVIDER_GAMIFICATION')
	.success(function(data) {
		for(var i = 0; i < data.length; i++)
		{
			$scope.players.push(data[i]);
		}
	});
    
    $http.get('game-requirements/requirement')
	.success(function(data) {
		for(var i = 0; i < data.length; i++)
		{
			$scope.requirements.push(data[i]);
		}
	});
    
    $http.get('game-requirements/criteria')
	.success(function(data) {
		for(var i = 0; i < data.length; i++)
		{
			$scope.criterias.push(data[i]);
		}
	});

    $http.get('game-requirements/requirementchoice')
	.success(function(data) {
		$scope.requirementsChoices.length = 0;
		for(var i = 0; i < data.length; i++)
		{
			$scope.requirementsChoices.push(data[i]);
		}
	});
    
    $scope.toggleSelection = function(array, item)
	{
	    var idx = array.indexOf(item);
	    if (idx > -1) {
	    	array.splice(idx, 1);
	    }
	    else {
	    	array.push(item);
	    }
	};
	
	$scope.toPage = function(p)
	{
		if(p == 3)
		{
			if($scope.game.players.length > 0 &&
					$scope.game.requirements.length > 1 &&
					$scope.game.criterias.length > 1)
			{
				$scope.currentPage = 'page3';
			}
		}
		else
		{
			$scope.currentPage = 'page' + p;
		}
	}
	
	$scope.createGame = function()
	{
		$http({
			url: "game-requirements/game",
	        data: $scope.game,
	        method: 'POST',
	        params: {criteriaValues : $scope.choices}
	    }).success(function(data){
	        $scope.game = {players : [], requirements: [], criterias: [], title: "Decision Making Process " + $scope.now()};
	    	$scope.choices = {};
	    	$scope.currentPage = 'page1';
	    	$location.url('game-requirements/game_page').search('gameId', data);
	    }).error(function(err){
	    	console.log(err);
	    });
	};
});