var app = angular.module('w5app');

app.controllerProvider.register('game', function($scope, $http, $location) {
	
	$scope.Math = window.Math;
	
	$scope.gameId = $location.search()['gameId'];
	$scope.game = undefined;
	$scope.ahpResult = undefined;
	
	$http.get('game-requirements-gamification/game/' + $scope.gameId)
	.success(function(data) {
		$scope.game = data;
	});

	$scope.computeAHP = function(gameId){
		$http.get('game-requirements-gamification/ahp/' + gameId)
			.success(function(data) {
				$scope.ahpResult = data;
		});
	};
	
	$scope.gameEnd = function(gameId){
		$http.put('game-requirements-gamification/game/end/' + gameId)
			.success(function(data) {
				
			});
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
	
	$scope.exportGameData = function(){
		var a = document.createElement("a");
		a.href = 'game-requirements-gamification/game/' + $scope.gameId + '/exportGameData'; 
		a.target = '_blank';
		
		var clickEvent = new MouseEvent("click", {
		    "view": window,
		    "bubbles": true,
		    "cancelable": false
		});
		
		a.dispatchEvent(clickEvent);
	};
	
	$scope.exportGameResults = function(){
		var a = document.createElement("a");
		a.href = 'game-requirements-gamification/game/' + $scope.gameId + '/exportGameResults'; 
		a.target = '_blank';

		var clickEvent = new MouseEvent("click", {
		    "view": window,
		    "bubbles": true,
		    "cancelable": false
		});
		
		a.dispatchEvent(clickEvent);
	};
	
	// for the criterias test
	 $scope.choices = {};
	 $scope.requirementsChoices = [];

	 $http.get('game-requirements-gamification/requirementchoice')
		.success(function(data) {
			$scope.requirementsChoices.length = 0;
			for(var i = 0; i < data.length; i++)
			{
				$scope.requirementsChoices.push(data[i]);
			}
		});
});