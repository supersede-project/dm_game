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

app.controllerProvider.register('games', function($scope, $http, $location) {
	
	$scope.Math = window.Math;
	
	$scope.open_games = [];
	$scope.closed_games = [];

	$scope.pagination = { 'open' : {}, 'closed' : {}};
	$scope.pagination.open.totalPages = 1;
	$scope.pagination.open.length = 0;
    $scope.pagination.open.itemsPerPage = 5;
    $scope.pagination.open.currentPage = 0;

	$scope.pagination.closed.totalPages = 1;
	$scope.pagination.closed.length = 0;
    $scope.pagination.closed.itemsPerPage = 5;
    $scope.pagination.closed.currentPage = 0;
	
	$http.get('game-requirements/game')
	.success(function(data) {
		for(var i = 0; i < data.length; i++)
		{
			if(data[i].finished)
			{
				$scope.closed_games.push(data[i]);
			}
			else
			{
				$scope.open_games.push(data[i]);
			}
		}

		$scope.pagination.open.length = $scope.open_games.length;
		$scope.pagination.open.totalPages = Math.max(1, Math.ceil($scope.pagination.open.length / $scope.pagination.open.itemsPerPage));
		
		$scope.pagination.closed.length = $scope.closed_games.length;
		$scope.pagination.closed.totalPages = Math.max(1, Math.ceil($scope.pagination.closed.length / $scope.pagination.closed.itemsPerPage));
	});
	
    
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

    $scope.goGame = function (gameId) {
    	$location.path('/game-requirements/game_page').search('gameId', gameId);
	};
	

    $scope.goCreateGame = function (gameId) {
    	$location.path('/game-requirements/create_game');
	};
});