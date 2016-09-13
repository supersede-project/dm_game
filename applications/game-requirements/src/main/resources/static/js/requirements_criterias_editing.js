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

app.controllerProvider.register('requirements_criterias_editing', function($scope, $http) {
	
	$scope.criterias = [];
	
	drawTabs = function() {
		console.log("Drawing");
		$('#jqxTabs').jqxTabs({ height: 555, width: '100%' });
		$('#jqxTabs').on('tabclick', function (event) {
	        if (event.args.item == $('#unorderedList').find('li').length - 2) {
	            //create criteria
	        	createCriteria();
	        }
	        else if (event.args.item == $('#unorderedList').find('li').length - 1) {
	            //create requirement
	        	createRequirement();
	        } 
	    });
	    $('#unorderedList').css('visibility', 'visible');
	}
	
	getCriterias = function() {
		 $http.get('game-requirements/criteria')
		 	.success(function(data) {
				$scope.criterias.length = 0;
				for(var i = 0; i < data.length; i++)
				{
					$scope.criterias.push(data[i]);
				}
				
				setupCriterias();
		 	});
	};
	
	setupCriterias = function() {
		//prepare criteria data
		var sourceCriteria =
		{
			datatype: "json",
			datafields: [
				{ name: 'name'},
				{ name: 'description'},
				{ name: 'criteriaId'}
			],
			id: 'criteriaId',
			localdata: $scope.criterias
		};
		var dataAdapterCriteria = new $.jqx.dataAdapter(sourceCriteria);
		$scope.criteriaSettings =
		{
			width: '100%',
			height: 500,
			pageable: true,
			editable: true,
			autorowheight: true,
			editmode: 'selectedrow',
			source: dataAdapterCriteria,
			columns: [
				{ text: 'Name', width: '24%', datafield: 'name' },
				{ text: 'Description', width: '65%', datafield: 'description' },
				{ text: '', width: '11%', height: '31px',editable: false, datafield: 'criteriaId', cellsRenderer: function (row, columnDataField, value) {
						var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
						r = r.concat("<jqx-button onclick='editCriteria(" + row + ");'>Save</jqx-button> ");
						r = r.concat("<jqx-button onclick='deleteCriteria(" + row + ");'>Delete</jqx-button>");
						return r.concat("</div>");
					}
				}
			]
		};
		$scope.createWidgetCriteria = true;
		console.log("criteria");
		if($scope.createWidgetRequirement)
		{
			console.log("criteria drawing");
			drawTabs();
		}
	};
		
	getCriterias();
	
	createCriteria = function() {
		var criteria = {name: "", description: ""};
		$http({
			url: "game-requirements/criteria/",
			data: criteria,
			method: 'POST'
		}).success(function(data, status, headers, config){
			var l = headers('Location');
			criteria.criteriaId = parseInt(l.substring(l.lastIndexOf("/") + 1));
			$('#criteriaGrid').jqxGrid('addrow', criteria.criteriaId, criteria);
		}).error(function(err){
		});
    };
	
	editCriteria = function(row) {
		$('#criteriaGrid').jqxGrid('endrowedit', row, false);
		var rowData = $('#criteriaGrid').jqxGrid('getrowdata', row);
		$http({
			url: "game-requirements/criteria/",
			data: rowData,
			method: 'PUT'
		}).success(function(data){
		}).error(function(err){
		});
	};
	
	deleteCriteria = function(row) {
		var rowData = $('#criteriaGrid').jqxGrid('getrowdata', row);
		$http.delete('game-requirements/criteria/' + rowData.criteriaId).success(function(data) {
			if(data == true){
				$('#criteriaGrid').jqxGrid('deleterow', $('#criteriaGrid').jqxGrid('getrowid', row));
			}
			else
			{
				alert("Can not delete criteria");
			}
		});
	};
	
	
	
	
	$scope.requirements = [];
	
	getRequirements = function () {
		$http.get('game-requirements/requirement')
		.success(function(data) {
			$scope.requirements.length = 0;
			for(var i = 0; i < data.length; i++)
			{
				$scope.requirements.push(data[i]);
			}
			
			setupRequirements();
		});	
	};
	 
	setupRequirements = function() {
		//prepare requirement data
		var sourceRequirement =
		{
			datatype: "json",
			datafields: [
				{ name: 'name'},
				{ name: 'description'},
				{ name: 'requirementId'}
			],
			id: 'requirementId',
			localdata: $scope.requirements
		};
		var dataAdapterRequirement = new $.jqx.dataAdapter(sourceRequirement);
		$scope.requirementSettings =
		{
			width: '100%',
			height: 500,
			pageable: true,
			editable: true,
			autorowheight: true,
			editmode: 'selectedrow',
			source: dataAdapterRequirement,
			columns: [
			    { text: 'Name', width: '24%', datafield: 'name' },
			    { text: 'Description', width: '65%', datafield: 'description' },
			    { text: '', width: '11%', editable: false, datafield: 'requirementId', cellsRenderer: function (row, columnDataField, value) {
			    	var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
					r = r.concat("<jqx-button onclick='editRequirement(" + row + ");'>Save</jqx-button> ");
					r = r.concat("<jqx-button onclick='deleteRequirement(" + row + ");'>Delete</jqx-button>");
					return r.concat("</div>");
					}
			    }
			]
		};
		$scope.createWidgetRequirement = true;
		console.log("requirement");
		if($scope.createWidgetCriteria)
		{
			console.log("requirement drawing");
			drawTabs();
		}
	};
	
	getRequirements();
	
	createRequirement = function() {
		var requirement = {name: "", description: ""};
		$http({
			url: "game-requirements/requirement/",
			data: requirement,
			method: 'POST'
		}).success(function(data, status, headers, config){
			var l = headers('Location');
			requirement.requirementId = parseInt(l.substring(l.lastIndexOf("/") + 1));
			$('#requirementGrid').jqxGrid('addrow', requirement.requirementId, requirement);
		}).error(function(err){
		});
	};
	
	editRequirement = function(row) {
		$('#requirementGrid').jqxGrid('endrowedit', row, false);
		var rowData = $('#requirementGrid').jqxGrid('getrowdata', row);
		$http({
			url: "game-requirements/requirement/",
			data: rowData,
			method: 'PUT'
		}).success(function(data){
		}).error(function(err){
		});
	};
	
	deleteRequirement = function(row) {
		var rowData = $('#requirementGrid').jqxGrid('getrowdata', row);
		$http.delete('game-requirements/requirement/' + rowData.requirementId).success(function(data) {
			if(data == true){
				$('#requirementGrid').jqxGrid('deleterow', $('#requirementGrid').jqxGrid('getrowid', row));
			}
			else
			{
				alert("Can not delete requirement");
			}
		});
	};
  
});