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

app.controllerProvider.register('player_games', function($scope, $http, $location) {
    
    $scope.playerGames = [];
       
    $http.get('game-requirements/game', {params:{byUser: true, finished:false}})
	.success(function(data) {
		for(var i = 0; i < data.length; i++)
		{
			$scope.playerGames.push(data[i]);
		}
		
		// prepare the data
		var source =
		{
			datatype: "json",
			datafields: [
				{ name: 'title'},
				{ name: 'progress'},
				{ name: 'gameId'}
			],
			id: 'gameId',
			localdata: $scope.playerGames
		};
		var dataAdapter = new $.jqx.dataAdapter(source);
		$scope.gridSettings =
		{
			width: '100%',
			autoheight: true,
			pageable: true,
			source: dataAdapter,
			rowsheight: 31,
			columns: [
			    { text: 'Title', width: '83%', datafield: 'title' },
			    { text: 'Progress', width: '7%', datafield: 'progress', cellsRenderer: function (row, columnDataField, value) {
					var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
					r = r.concat((Math.round(value * 1000) / 1000) + "%");
					return r.concat("</div>");
					}
			    },
			    { text: '', width: '10%', datafield: 'gameId', cellsRenderer: function (row, columnDataField, value) {
					var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
					r = r.concat("<jqx-link-button jqx-width='75' jqx-height='25'><a href='#/game-requirements/player_moves?gameId=" + value + "'>Enter</a></jqx-link-button>");
					return r.concat("</div>");
					}
			    }
			]
		};
		$scope.createWidget = true;
	});
    
    
});