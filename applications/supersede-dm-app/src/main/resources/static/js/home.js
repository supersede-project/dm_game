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

app.controllerProvider.register('home', function($scope, $http, $location) {

	$scope.now = function() {
		return new Date().toJSON().slice(0,19).replace("T", " ");
	}
	
    $scope.reqNum = undefined;
    
    $http.get('supersede-dm-app/requirement').success(function(data) {
    	$scope.reqNum = data.length;
	});
    
    $http.get('supersede-dm-app/alerts/biglist').success(function(data) {
    	$scope.alertsNum = data.length;
    });
    
    $("#btnNewProcess").on('click', function ()
    {
    	$http.get('supersede-dm-app/processes/new').success(function(data) {
        $scope.alertsNum = data.length;
        });
   	});
});

$(document).ready(function () {
//        $("#jqxListBox").jqxListBox({ width: 700 });
});