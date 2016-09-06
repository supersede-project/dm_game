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

app.controllerProvider.register('judge_acts', function($scope, $http) {
    
	$scope.requirementsChoices = [];
    
	$scope.open_acts = [];
	$scope.closed_acts = [];

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
    
    $scope.selectedGame = undefined;
    $scope.games = [];
    
    reset = function(){
    	$scope.open_acts.length = 0;
		$scope.closed_acts.length = 0;
    	
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
    
    getActs = function() {
	    $http.get('game-requirements-gamification/judgeact', {params: { criteriaId: $scope.selectedCriteria, gameId: $scope.selectedGame, gameNotFinished: true }})
		.success(function(data) {
			reset();
			
			for(var i = 0; i < data.length; i++)
			{
				if(data[i].voted)
				{
					$scope.closed_acts.push(data[i]);
				}
				else
				{
					$scope.open_acts.push(data[i]);
				}
			}
			
			$scope.pagination.open.length = $scope.open_acts.length;
			$scope.pagination.open.totalPages = Math.max(1, Math.ceil($scope.pagination.open.length / $scope.pagination.open.itemsPerPage));
			
			$scope.pagination.closed.length = $scope.closed_acts.length;
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
    
    getActs();
    
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
    
	$http.get('game-requirements-gamification/requirementchoice')
		.success(function(data) {
			$scope.requirementsChoices.length = 0;
			for(var i = 0; i < data.length; i++)
			{
				$scope.requirementsChoices.push(data[i]);
			}
		});
    
	$scope.setVote = function(judgeVote, judgeActId){
		$http.put('game-requirements-gamification/judgeact/' + judgeActId + '/vote/' + judgeVote)
	    	.success(function(data) {
	    		for(var i = 0; i < $scope.open_acts.length; i++)
				{
					if($scope.open_acts[i].judgeActId == judgeActId)
					{
						$scope.open_acts[i].voted = true;
						$scope.closed_acts.push($scope.open_acts[i]);
						$scope.open_acts.splice(i, 1);
						
						$scope.pagination.open.length = $scope.open_acts.length;
						$scope.pagination.open.totalPages = Math.max(1, Math.ceil($scope.pagination.open.length / $scope.pagination.open.itemsPerPage));
						
						if($scope.pagination.open.currentPage >= $scope.pagination.open.totalPages)
						{
							$scope.pagination.open.currentPage = Math.max(1, $scope.pagination.open.totalPages -1);
						}
						
						$scope.pagination.closed.length = $scope.closed_acts.length;
						$scope.pagination.closed.totalPages = Math.max(1, Math.ceil($scope.pagination.closed.length / $scope.pagination.closed.itemsPerPage));	
					
						break;
					}
				}
    	});
	 };
	 

	 $scope.changeCriteria = function() {
		 getActs();
	 }
    
	 $scope.changeGame = function() {
		 getActs();
	 }
});