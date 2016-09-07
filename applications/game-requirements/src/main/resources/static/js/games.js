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
	
	$scope.open_games = [];
	$scope.closed_games = [];

	$http.get('game-requirements/game').
	success(function(data) 
	{
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
		
		// prepare the open data
		var sourceOpen =
		{
			datatype: "json",
			datafields: [
				{ name: 'title'},
				{ name: 'creator', map: 'creator>name'},
				{ name: 'startTime'},
				{ name: 'progress'},
				{ name: 'gameId'}
			],
			id: 'gameId',
			localdata: $scope.open_games
		};
		var dataAdapterOpen = new $.jqx.dataAdapter(sourceOpen);
		$scope.openSettings =
		{
			width: '100%',
			height: 500,
			pageable: true,
			autorowheight: true,
			source: dataAdapterOpen,
			columns: [
			    { text: 'Title', width: '40%', datafield: 'title' },
			    { text: 'Created By', width: '25%', datafield: 'creator' },
			    { text: 'Creation Date', width: '18%', datafield: 'startTime' },
			    { text: 'Progress', width: '7%', datafield: 'progress', cellsRenderer: function (row, columnDataField, value) {
					var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
					r = r.concat((Math.round(value * 100) / 100) + "%");
					return r.concat("</div>");
					}
			    },
			    { text: '', width: '10%', datafield: 'gameId', cellsRenderer: function (row, columnDataField, value) {
					var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
					r = r.concat("<jqx-link-button jqx-width='75' jqx-height='25'><a href='#/game-requirements/game_page?gameId=" + value + "'>Detail</a></jqx-link-button>");
					return r.concat("</div>");
					}
			    }
			]
		};
		$scope.createWidgetOpen = true;
		
		// prepare the closed data
		var sourceClosed =
		{
			datatype: "json",
			datafields: [
				{ name: 'title'},
				{ name: 'creator', map: 'creator>name'},
				{ name: 'startTime'},
				{ name: 'progress'},
				{ name: 'gameId'}
			],
			id: 'gameId',
			localdata: $scope.closed_games
		};
		var dataAdapterClosed = new $.jqx.dataAdapter(sourceClosed);
		$scope.closedSettings =
		{
			width: '100%',
			height: 500,
			pageable: true,
			autorowheight: true,
			source: dataAdapterClosed,
			columns: [
			    { text: 'Title', width: '40%', datafield: 'title' },
			    { text: 'Created By', width: '25%', datafield: 'creator' },
			    { text: 'Creation Date', width: '18%', datafield: 'startTime' },
			    { text: 'Progress', width: '7%', datafield: 'progress', cellsRenderer: function (row, columnDataField, value) {
					var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
					r = r.concat((Math.round(value * 100) / 100) + "%");
					return r.concat("</div>");
					}
			    },
			    { text: '', width: '10%', datafield: 'gameId', cellsRenderer: function (row, columnDataField, value) {
					var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
					r = r.concat("<jqx-link-button jqx-width='75' jqx-height='25'><a href='#/game-requirements/game_page?gameId=" + value + "'>Detail</a></jqx-link-button>");
					return r.concat("</div>");
					}
			    }
			]
		};
		$scope.createWidgetClosed = true;
		
		$('#jqxTabs').jqxTabs({ height: 555, width: '100%' });
	    var index = 0;
	    $('#jqxTabs').on('tabclick', function (event) {
	        if (event.args.item == $('#unorderedList').find('li').length - 1) {
	            //go to create game page
	        	$location.path('/game-requirements/create_game');
	        }
	    });
	    $('#unorderedList').css('visibility', 'visible');
	 }).error(function (data, status) {
		 alert(status);
	 });
	
    $scope.goGame = function (gameId) {
    	$location.path('/game-requirements/game_page').search('gameId', gameId);
	};
	
});