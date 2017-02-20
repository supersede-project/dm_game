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

	$scope.procId = $location.search().procId;
	
	$scope.now = function() {
		return new Date().toJSON().slice(0,19).replace("T", " ");
	}

	$scope.userCount = undefined;
	
	$http({
        method: 'GET',
        url: "supersede-dm-app/processes/users/list",
        params: { procId: $scope.procId }
    }).success(function(data){
		$scope.userCount = data.length;
	});
    
	$http({
        method: 'GET',
        url: "supersede-dm-app/processes/criteria/list",
        params: { procId: $scope.procId }
    }).success(function(data){
		$scope.criteriaCount = data.length;
	});
    
	$http.get('supersede-dm-app/processes/available_activities?procId=' + $scope.procId ).success(function(data) {
		console.log(data);
//		var source = [];
//		for( var i = 0; i < data.length; i++ ) {
//			var item = {
//					label: data[i].methodName,
//					value: data[i].methodName
//			};
//			source.push( item );
//		}
		$("#procList").jqxListBox({ source: data, width: 400, height: 250,
			renderer: function (index, label, value) {
				var datarecord = data[index];
				var imgurl = 'supersede-dm-app/img/process.png';
				var img = '<img height="50" width="50" src="' + imgurl + '"/>';
				var table = 
					'<table style="min-width: 130px;">' +
					'<tr><td style="width: 40px;" rowspan="2">' + 
					img + '</td><td>' + 
					datarecord.methodName + 
					'</td></tr><tr><td>' + 
					'<jqx-link-button jqx-width="200" jqx-height="30"> <a ' + 
	            	'href="#/supersede-dm-app/' + datarecord.entryUrl + '?procId=' + $scope.procId + '">Open</a>' + 
	            	'</jqx-link-button>' + 
	            	'</td></tr>' + 
					'</table>';
				return table;
			}
		});
	});

});

$(document).ready(function () {
//	$("#jqxListBox").jqxListBox({ width: 700 });

});