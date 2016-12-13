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

app.controllerProvider.register('criterias_leaderboard', function($scope, $http) {
	
	$scope.valutationCriterias = [];
    $scope.criteriaUsers = []; 
    $scope.selectedCriteria = undefined;
    
    $http.get('supersede-dm-app/ahprp/criteria')
	.success(function(data) {
		$scope.valutationCriterias.length = 0;
		for(var i = 0; i < data.length; i++)
		{
			$scope.valutationCriterias.push(data[i]);
		}
	});
    	
    $scope.selectedCriteriaChanged = function(){   	
    	$http.get('supersede-dm-app/user/criteria/' + $scope.selectedCriteria.criteriaId)
		.success(function(data) {
			$scope.criteriaUsers.length = 0;
			for(var i = 0; i < data.length; i++)
			{
				$scope.criteriaUsers.push(data[i]);
			}
		});
    }
});