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

app.controllerProvider.register('game', function($scope, $http, $location) {
	
	$scope.Math = window.Math;
	
	var gameId = $location.search().id;
	$scope.gameId = gameId;
	
	$scope.game = undefined;
	$scope.solutions = [];
	
	$http.get('supersede-dm-app/garp/game/getGame?gameId=' + gameId)
	 .success(function(data) {
		$scope.game = data;
		alert('Game data: ' + data);
	});

	$scope.computeRank = function(){
		$http.get('supersede-dm-app/garp/game/calc?gameId=' + gameId)
		 .success(function(data) {
			 alert(data);
			$scope.solutions.length = 0;
			var count = 0;
			for(var i in data)
			{
				alert(i);
				count++;
				var row = {};
				row["requirementID"] = i[0];
				row["requirementName"] = $scope.requirementName(i[0]);
				row["value"] = i[1];
				$scope.solutions["id"] = count;
				$scope.solutions["id"].ranks.push(row);
				//$scope.solutions.push({requirement: $scope.requirementName(i), value: (Math.round(data[i] * 1000) / 1000) });
			}
			
		}).error(function(err){
            alert(err.message);
        });
	};
	
	$scope.gameEnd = function(){
		alert('Game was closed: ' + $scope.gameId);
	};
	 
	$scope.enact = function(){
		alert('Game was enacted: ' + $scope.gameId);
	};
	 
	$scope.requirementName = function(id)
	{
		for(var i = 0; i < $scope.game.requirements.length; i++)
		{
			if($scope.game.requirements[i].requirementId == id)
			{
				return $scope.game.requirements[i].name;
			}
		}
		
		return "";
	}
});
$(document).ready(function () {
    $('#jqxTabs').jqxTabs({ width: 700 });
});