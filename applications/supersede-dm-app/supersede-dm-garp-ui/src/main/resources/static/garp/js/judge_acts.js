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

	$scope.selectedCriteria = undefined;
	$scope.criterias = [];
	
	$scope.selectedGame = undefined;
	$scope.games = [];
		
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
		$http.get('supersede-dm-app/ahprp/judgeact', {params: { criteriaId: $scope.selectedCriteria, gameId: $scope.selectedGame, gameNotFinished: true }}).success(function(data)
		{
			$scope.closed_acts.length = 0;
			$scope.open_acts.length = 0;
			
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
			
			// prepare the open data
			var sourceOpen =
			{
				datatype: "json",
				datafields: [
					{ name: 'criteriaName', map: 'requirementsMatrixData>criteria>name'},
					{ name: 'firstRequirementName', map: 'requirementsMatrixData>rowRequirement>name'},
					{ name: 'judgeActId'},
					{ name: 'secondRequirementName', map: 'requirementsMatrixData>columnRequirement>name'},
					{ name: 'judgeActId2', map: 'judgeActId'}
				],
				id: 'judgeActId',
				localdata: $scope.open_acts
			};
			var dataAdapterOpen = new $.jqx.dataAdapter(sourceOpen);
			$scope.openSettings =
			{
				width: '100%',
				autoheight: true,
				pageable: true,
				source: dataAdapterOpen,
				rowsheight: 39,
				columns: [
					{ text: 'Comparison Criteria', width: '30%', datafield: 'criteriaName' },
					{ text: 'First Requirement', width: '16%', datafield: 'firstRequirementName' },
					{ text: 'Select your vote', width: '31%', datafield: 'judgeActId', cellsRenderer: function (row, columnDataField, value) {
						var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
						r = r + "<div align='center'>" +
								"<div style='display: inline-block; margin-left: 2px; margin-right: 2px;' ng-repeat='requirementsChoice in requirementsChoices' align='center'>" + 
								"<div align='center'>" +
								"<label>{{requirementsChoice.label}}</label>" + 
								"</div>" +
								"<div align=center>" +
								"<input title='{{requirementsChoice.description}}' type='radio' ng-click='setVote(requirementsChoice.value, " + value + ")' ng-value='{{requirementsChoice.value}}' name='" + value +"'>" + 
								"</div>" + 
								"</div>" + 
								"</div>";
						return r.concat("</div>");
						}
				    },
					{ text: 'Second Requirement', width: '16%', datafield: 'secondRequirementName' },
					{ text: '', width: '7%', datafield: 'judgeActId2', cellsRenderer: function (row, columnDataField, value) {
						var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
						r = r.concat("<jqx-link-button jqx-width='55' jqx-height='25'><a href='#/supersede-dm-app/ahprp/judge_act?judgeActId=" + value + "'>Decide</a></jqx-link-button>");
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
					{ name: 'criteriaName', map: 'requirementsMatrixData>criteria>name'},
					{ name: 'firstRequirementName', map: 'requirementsMatrixData>rowRequirement>name'},
					{ name: 'requirementVote', map: 'requirementsMatrixData>value'},
					{ name: 'secondRequirementName', map: 'requirementsMatrixData>columnRequirement>name'}
				],
				id: 'judgeActId',
				localdata: $scope.closed_acts
			};
			var dataAdapterClosed = new $.jqx.dataAdapter(sourceClosed);
			$scope.closedSettings =
			{
				width: '100%',
				autoheight: true,
				pageable: true,
				source: dataAdapterClosed,
				rowsheight: 31,
				columns: [
					{ text: 'Comparison Criteria', width: '40%', datafield: 'criteriaName' },
					{ text: 'First Requirement', width: '25%', datafield: 'firstRequirementName' },
					{ text: 'Your vote', width: '10%', datafield: 'requirementVote' },
					{ text: 'Second Requirement', width: '25%', datafield: 'secondRequirementName' }
				]
			};
			$scope.createWidgetClosed = true;
			
			$('#jqxTabs').jqxTabs({ width: '100%' });
			$('#unorderedList').css('visibility', 'visible');
		});
	};
	
	getActs();
		
	$http.get('supersede-dm-app/ahprp/requirementchoice').success(function(data)
	{
		$scope.requirementsChoices.length = 0;
		for(var i = 0; i < data.length; i++)
		{
			$scope.requirementsChoices.push(data[i]);
		}
	});
	
	$scope.setVote = function(judgeVote, judgeActId){
		$http.put('supersede-dm-app/ahprp/judgeact/' + judgeActId + '/vote/' + judgeVote).success(function(data)
		{
			getActs();
		});
	};
	

	$scope.changeCriteria = function() {
		getActs();
	}
	
	$scope.changeGame = function() {
		getActs();
	}
});