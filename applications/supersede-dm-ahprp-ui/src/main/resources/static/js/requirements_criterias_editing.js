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
		
	$scope.valutationCriterias = [];
	$scope.criteria = {};
	
	getCriterias = function () {
		 $http.get('game-requirements/criteria')
		 	.success(function(data) {
				$scope.valutationCriterias.length = 0;
				for(var i = 0; i < data.length; i++)
				{
					$scope.valutationCriterias.push(data[i]);
				}
			});
	};
	getCriterias();
	
	$scope.getCriteria = function (criteriaId) {
		$http.get('game-requirements/criteria/' + criteriaId)
		.success(function(data) {
			$scope.criteria = data;
		});	
	};
	
	$scope.createCriteria = function () {
		$http({
			url: "game-requirements/criteria/",
			data: $scope.criteria,
			method: 'POST'
		}).success(function(data){
			$scope.criteria = {};
			getCriterias();
		}).error(function(err){
		});
    };
	
	$scope.editCriteria = function () {
		$http({
			url: "game-requirements/criteria/",
			data: $scope.criteria,
			method: 'PUT'
		}).success(function(data){
			$scope.criteria = {};
			getCriterias();
		}).error(function(err){
		});
	};
	
	$scope.deleteCriteria = function (criteriaId) {
		 $http.delete('game-requirements/criteria/' + criteriaId)
			.success(function(data) {
				if(data == true){
					$scope.valutationCriterias = [];
					getCriterias();
				}
				else
				{
					alert("Can not delete criteria");
				}
			});
	};
	
	
	
	
	$scope.requirements = [];
	$scope.requirement = {};
	
	getRequirements = function () {
		$http.get('game-requirements/requirement')
		.success(function(data) {
			$scope.requirements.length = 0;
			for(var i = 0; i < data.length; i++)
			{
				$scope.requirements.push(data[i]);
			}
		});	
	};
	 
	getRequirements();
	
	$scope.getRequirement = function (requirementId) {
		$http.get('game-requirements/requirement/' + requirementId)
		.success(function(data) {
			$scope.requirement = data;
		});	
	};
	
	$scope.createRequirement = function () {
		$http({
			url: "game-requirements/requirement/",
			data: $scope.requirement,
			method: 'POST'
		}).success(function(data){
			$scope.requirement = {};
			getRequirements();
		}).error(function(err){
		});
	};
	
	$scope.editRequirement = function () {
		$http({
			url: "game-requirements/requirement/",
			data: $scope.requirement,
			method: 'PUT'
		}).success(function(data){
			$scope.requirement = {};
			getRequirements();
		}).error(function(err){
		});
	};
	
	$scope.deleteRequirement = function (requirementId) {
		 $http.delete('game-requirements/requirement/' + requirementId)
			.success(function(data) {
				if(data == true){
					$scope.requirements = [];
					getRequirements();
				}
				else
				{
					alert("Can not delete requirement");
				}
			});
	};
  
});