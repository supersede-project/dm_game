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
	
	$scope.gameId = $location.search()['gameId'];
	$scope.game = undefined;
	$scope.ahpResult = [];
	
	$http.get('supersede-dm-assetmgr/game/' + $scope.gameId).success(function(data) {
		$scope.game = data;
	});

	$scope.computeAHP = function(gameId){
		$http.get('supersede-dm-assetmgr/ahp/' + gameId).success(function(data) {
			$scope.ahpResult.length = 0;;
			
			for(var i in data)
			{
				
				$scope.ahpResult.push({requirement: $scope.requirementName(i), value: (Math.round(data[i] * 1000) / 1000) });
			}
			
			// prepare the open data
			var sourceChart =
			{
				datatype: "json",
				datafields: [
					{ name: 'requirement'},
					{ name: 'value'}
				],
				id: 'requirement',
				localdata: $scope.ahpResult
			};
			var dataAdapterChart = new $.jqx.dataAdapter(sourceChart);
			
			$scope.chartSettings = {
				title: "",
				description: "",
				showLegend: true,
				enableAnimations: true,
				padding: { left: 20, top: 5, right: 20, bottom: 5 },
				titlePadding: { left: 90, top: 0, right: 0, bottom: 10 },
				source: dataAdapterChart,
				xAxis:
				{
					dataField: 'requirement',
					gridLines: { visible: true },
					flip: false
				},
				valueAxis:
				{
					flip: true,
					labels: {
						visible: true,
						formatFunction: function (value) {
							return value;
						}
					}
				},
				colorScheme: 'scheme01',
				seriesGroups:
				[
					{
						type: 'column',
						orientation: 'horizontal',
						columnsGapPercent: 50,
						toolTipFormatSettings: { thousandsSeparator: ',' },
						series: [
							{ dataField: 'value', displayText: 'Value' , showLabels: true}
						]
					}
				]
			};
			
			$scope.createChart = true;
		});
	};
	
	$scope.gameEnd = function(gameId){
		$http.put('supersede-dm-assetmgr/game/end/' + gameId).success(function(data) {
			$scope.game.finished = true;
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
		a.href = 'supersede-dm-assetmgr/game/' + $scope.gameId + '/exportGameData'; 
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
		a.href = 'supersede-dm-assetmgr/game/' + $scope.gameId + '/exportGameResults'; 
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

	 $http.get('supersede-dm-assetmgr/requirementchoice').success(function(data) {
		$scope.requirementsChoices.length = 0;
		for(var i = 0; i < data.length; i++)
		{
			$scope.requirementsChoices.push(data[i]);
		}
	});
});