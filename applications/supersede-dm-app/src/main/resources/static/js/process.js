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

app.controllerProvider.register('process', function($scope, $http, $location) {

	$scope.now = function() {
		return new Date().toJSON().slice(0,19).replace("T", " ");
	}

	$scope.reqNum = undefined;

	$http.get('supersede-dm-app/processes/available_activities?procId=4247').success(function(data) {
		console.log(data);
		var source = [];
		for( var i = 0; i < data.length; i++ ) {
			var item = {
					label: data[i].methodName,
					value: data[i].methodName
			};
			source.push( item );
		}
		$("#procList").jqxListBox({ source: source, width: 200, height: 250 });
	});
	
});

$(document).ready(function () {
//	$("#jqxListBox").jqxListBox({ width: 700 });

});