var app = angular.module('w5app');

app.controllerProvider.register('player_games', function($scope, $http, $location) {
    
	$scope.Math = window.Math;
	
    $scope.playerGames = [];
       
    $http.get('game-requirements-gamification/game', {params:{byUser: true, finished:false}})
	.success(function(data) {
		for(var i = 0; i < data.length; i++)
		{
			$scope.playerGames.push(data[i]);
		}
	});
});