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
    
    $http.get('game-requirements-gamification/user?profile=OPINION_PROVIDER')
	.success(function(data) {
		for(var i = 0; i < data.length; i++)
		{
			$scope.players.push(data[i]);
		}
	});
    
    $http.get('game-requirements-gamification/requirement')
	.success(function(data) {
		for(var i = 0; i < data.length; i++)
		{
			$scope.requirements.push(data[i]);
		}
	});
    
    $http.get('game-requirements-gamification/criteria')
	.success(function(data) {
		for(var i = 0; i < data.length; i++)
		{
			$scope.criterias.push(data[i]);
		}
	});

    $http.get('game-requirements-gamification/requirementchoice')
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
			url: "game-requirements-gamification/game",
	        data: $scope.game,
	        method: 'POST',
	        params: {criteriaValues : $scope.choices}
	    }).success(function(data){
	        $scope.game = {players : [], requirements: [], criterias: [], title: "Decision Making Process " + $scope.now()};
	    	$scope.choices = {};
	    	$scope.currentPage = 'page1';
	    	$location.url('game-requirements-gamification/game_page').search('gameId', data);
	    }).error(function(err){
	    	console.log(err);
	    });
	};
});