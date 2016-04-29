var app = angular.module('w5app');

app.controllerProvider.register('player_moves', function($scope, $http, $location, $interval,  $rootScope) {
    
	$scope.Math = window.Math;
	
	$scope.selectedGame = $location.search()['gameId'];
	
	$scope.requirementsChoices = [];
	
	$scope.open_moves = [];
	$scope.closed_moves = [];

	$scope.pagination = { 'open' : {}, 'closed' : {}};
	$scope.pagination.open.totalPages = 1;
	$scope.pagination.open.length = 0;
    $scope.pagination.open.itemsPerPage = 5;
    $scope.pagination.open.currentPage = 0;

	$scope.pagination.closed.totalPages = 1;
	$scope.pagination.closed.length = 0;
    $scope.pagination.closed.itemsPerPage = 5;
    $scope.pagination.closed.currentPage = 0;
    
    $scope.selectedCriteria = undefined;
    $scope.criterias = [];
    
    //$scope.selectedGame = undefined;
    $scope.games = [];
    
    reset = function(){
    	$scope.open_moves.length = 0;
		$scope.closed_moves.length = 0;
    	
    	$scope.pagination.open.totalPages = 1;
    	$scope.pagination.open.length = 0;
        $scope.pagination.open.itemsPerPage = 5;
        $scope.pagination.open.currentPage = 0;

    	$scope.pagination.closed.totalPages = 1;
    	$scope.pagination.closed.length = 0;
        $scope.pagination.closed.itemsPerPage = 5;
        $scope.pagination.closed.currentPage = 0;
    }
    
    criteriasContains = function(criteria)
    {
    	for(var i = 0; i < $scope.criterias.length; i++)
    	{
    		if($scope.criterias[i].criteriaId == criteria.criteriaId)
    		{
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    gamesContains = function(game)
    {
    	for(var i = 0; i < $scope.games.length; i++)
    	{
    		if($scope.games[i].gameId == game.gameId)
    		{
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    getActions = function() {
    	$http.get('game-requirements/playermove', {params: { criteriaId: $scope.selectedCriteria, gameId: $scope.selectedGame, gameNotFinished: true }})
    	.success(function(data) {
    		reset();
    		
    		for(var i = 0; i < data.length; i++)
    		{
    			if(data[i].played)
    			{
    				$scope.closed_moves.push(data[i]);
    			}
    			else
    			{
    				$scope.open_moves.push(data[i]);
    			}
    		}
    		
    		$scope.pagination.open.length = $scope.open_moves.length;
    		$scope.pagination.open.totalPages = Math.max(1, Math.ceil($scope.pagination.open.length / $scope.pagination.open.itemsPerPage));
    		
    		$scope.pagination.closed.length = $scope.closed_moves.length;
    		$scope.pagination.closed.totalPages = Math.max(1, Math.ceil($scope.pagination.closed.length / $scope.pagination.closed.itemsPerPage));
    	
    		if(!$scope.selectedCriteria)
    		{
    			$scope.criterias.length = 0;
    			for(var i = 0; i < data.length; i++)
        		{
    				if(!criteriasContains(data[i].requirementsMatrixData.criteria))
    				{
    					$scope.criterias.push(data[i].requirementsMatrixData.criteria);
    				}
        		}
    		}
    		
    		if(!$scope.selectedGame)
    		{
    			$scope.games.length = 0;
    			for(var i = 0; i < data.length; i++)
        		{
    				if(!gamesContains(data[i].requirementsMatrixData.game))
    				{
    					$scope.games.push(data[i].requirementsMatrixData.game);
    				}
        		}
    		}
    	});
    };
	
    getActions();
    
    $scope.range = function (oc) {
        var ret = [];
        var showPages = Math.min(5, oc.totalPages);
        
        var start = Math.max(1, oc.currentPage - Math.floor(showPages / 2));
        var end = Math.min(oc.totalPages, oc.currentPage + Math.ceil(showPages / 2))
        
        if(start == 1)
        {
        	end = start + showPages - 1;
        }
        if(end == oc.totalPages)
        {
        	start = end - showPages + 1;
        }
        
        for (var i = start; i < start + showPages; i++) {
            ret.push(i);
        }
        
        return ret;
    };
    
    $scope.prevPage = function (oc) {
        if (oc.currentPage > 0) {
        	oc.currentPage--;
        }
    };
    
    $scope.nextPage = function (oc) {
        if (oc.currentPage < oc.totalPages - 1) {
        	oc.currentPage++;
        }
    };
    
    $scope.setPage = function (oc) {
    	oc.currentPage = this.n -1;
    };
    
	$http.get('game-requirements/requirementchoice')
		.success(function(data) {
			$scope.requirementsChoices.length = 0;
			for(var i = 0; i < data.length; i++)
			{
				$scope.requirementsChoices.push(data[i]);
			}
		});
	 
	$scope.setVote = function(playerVote, playerMoveId){
		$http.put('game-requirements/playermove/' + playerMoveId + '/vote/' + playerVote)
			.success(function(data) {
				for(var i = 0; i < $scope.open_moves.length; i++)
				{
					if($scope.open_moves[i].playerMoveId == playerMoveId)
					{
						$scope.open_moves[i].played = true;
						$scope.open_moves[i].value = playerVote;
						
						$scope.closed_moves.push($scope.open_moves[i]);
						$scope.open_moves.splice(i, 1);
						
						$scope.pagination.open.length = $scope.open_moves.length;
						$scope.pagination.open.totalPages = Math.max(1, Math.ceil($scope.pagination.open.length / $scope.pagination.open.itemsPerPage));
						
						if($scope.pagination.open.currentPage >= $scope.pagination.open.totalPages)
						{
							$scope.pagination.open.currentPage = Math.max(1, $scope.pagination.open.totalPages -1);
						}
						
						$scope.pagination.closed.length = $scope.closed_moves.length;
						$scope.pagination.closed.totalPages = Math.max(1, Math.ceil($scope.pagination.closed.length / $scope.pagination.closed.itemsPerPage));	
					
						break;
					}
				}
    	});
	 };
	 
	 $scope.changeCriteria = function() {
		 getActions();
	 }
    
	 $scope.changeGame = function() {
		 getActions();
	 }
	 
	$scope.openMove = function(playerMoveId){
		$http.put('game-requirements/playermove/open/' + playerMoveId)
			.success(function(data) {
				getActions();
		});
	};
	
	// polling methods (every second)
	
	$scope.loggedUser = $rootScope.user;
	$scope.user = undefined;
	
	$scope.game = undefined;
	
	$scope.gamePlayerPoints = undefined;
	
	$scope.agreementIndex = undefined;
	
	var update;
	
	update = $interval(function() {
		$http.get('game-requirements/game/' + $scope.selectedGame)
		.success(function(data) {
			$scope.game = data;
		});
		
		$http.get('game-requirements/user/' + $scope.loggedUser.userId)
		.success(function(data) {
			$scope.user = data;
		});
		
		$http.get('game-requirements/gameplayerpoint/game/' + $scope.selectedGame)
		.success(function(data) {
			$scope.gamePlayerPoints = data;
		});
		
		$http.get('game-requirements/gameplayerpoint/agreementindex/' + $scope.selectedGame)
		.success(function(data) {
			$scope.agreementIndex = data;
		});
		
    	}, 1000);
	
	
	 // stops the interval
    $scope.stop = function() {
      $interval.cancel(update);
    };
    
    // stops the interval when the scope is destroyed,
    // this usually happens when a route is changed and 
    // the ItemsController $scope gets destroyed. The
    // destruction of the ItemsController scope does not
    // guarantee the stopping of any intervals, you must
    // be responsible of stopping it when the scope is
    // is destroyed.
    $scope.$on('$destroy', function() {
      $scope.stop();
    });
	
});