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

app.controllerProvider.register('show_ranking', function($scope, $http, $location) {

    var requirements = {};
    var processId = $location.search().processId;

    $scope.rankings = {};

    $scope.getRequirement = function (requirementId) {
        return requirements[requirementId];
    };

    $http.get('supersede-dm-app/processes/details?processId=' + processId)
    .success(function (data) {
        $scope.processName = data.name;
    }).error(function (err) {
        alert(err.message);
    });
	
    $http.get('supersede-dm-app/processes/rankings/list?processId=' + processId)
    .success(function (data) {
        $scope.rankings = data;
    }).error(function (err) {
        alert(err.message);
    });

    $http.get('supersede-dm-app/processes/requirements/list?processId=' + processId)
    .success(function (data) {
        for (var i = 0; i < data.length; i++) {
            var requirementId = data[i].requirementId;
            requirements[requirementId] = data[i];
        }
    }).error(function (err) {
        alert(err.message);
    });

	$scope.empty = function () {
	    return $scope.rankings.length === 0;
	};

	$scope.back = function () {
	    $location.url('supersede-dm-app/process?processId=' + processId);
	};
});