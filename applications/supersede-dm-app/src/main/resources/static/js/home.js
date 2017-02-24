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

	$http.get('supersede-dm-app/processes/activities/list').success(function(data) {
		$scope.actNum = data.length;

		var source = {
	        localdata: data,
	        datatype: "array"
	    };
		var dataAdapter = new $.jqx.dataAdapter(source);
	    $('#activities-listbox').jqxListBox({ selectedIndex: 0,  
	    	source: dataAdapter, 
	    	displayMember: "name", 
	    	valueMember: "id", 
	    	itemHeight: 70, height: '100%', width: '100%',
	        renderer: function (index, label, value) {
	            var datarecord = data[index];
	            var action;
	            if( datarecord.state == "new" ) {
	            	action = "Start";
	            }
	            else {
	            	action = datarecord.state;
	            }
	            var table = 
	            	'<table style="min-width: 100%;">' + 
	            	'<tr><td style="width: 40px;" rowspan="2">' 
	            	+ "img" + 
	            	'</td><td>' + 
	            	datarecord.methodName + 
	            	'</td>' + 
	            	'<td style="width: 40px;" rowspan="2">' +
//	            	action + 
	            	'</td>' +
	            	'</tr><tr><td>' + 
//	            	"Created: " + datarecord.date + 
	            	'<jqx-link-button jqx-width="200" jqx-height="30"> <a ' + 
	            	'href="#/supersede-dm-app/' + datarecord.url + 
	            	'?procId=' + datarecord.processId + 
	            	'&activityId=' + datarecord.activityId + 
//	            	'&gameId=' + datarecord.properties.gameId + 
	            	'">View</a>' + 
	            	'</jqx-link-button>' + 
	            	'</td></tr></table>';
	            return table;
	        }
	    });
	});

	$http.get('supersede-dm-app/alerts/biglist').success(function(data) {
		$scope.alertsNum = data.length;
	});

	$http.get('supersede-dm-app/processes/list').success(function(data) {
		var source = {
	        localdata: data,
	        datatype: "array"
	    };
		var dataAdapter = new $.jqx.dataAdapter(source);
	    $('#listbox').jqxListBox({ selectedIndex: 0,  
	    	source: dataAdapter, 
	    	displayMember: "name", 
	    	valueMember: "id", 
	    	itemHeight: 70, height: '100%', width: '100%',
	        renderer: function (index, label, value) {
	            var datarecord = data[index];
//	            var imgurl = '../../images/' + label.toLowerCase() + '.png';
//	            var img = '<img height="50" width="40" src="' + imgurl + '"/>';
	            var action;
	            if( datarecord.state == "new" ) {
	            	action = "Start";
	            }
	            else {
	            	action = datarecord.state;
	            }
	            var table = 
	            	'<table style="min-width: 100%;">' + 
	            	'<tr><td style="width: 40px;" rowspan="2">' 
	            	+ "img" + 
	            	'</td><td>' + 
	            	datarecord.name + 
	            	" (" + 
	            	datarecord.objective + ")" + 
	            	'</td>' + 
	            	'<td style="width: 40px;" rowspan="2">' +
	            	action + 
	            	'</td>' +
	            	'</tr><tr><td>' + 
	            	"Created: " + datarecord.date + 
	            	'<jqx-link-button jqx-width="200" jqx-height="30"> <a ' + 
	            	'href="#/supersede-dm-app/process?procId=' + datarecord.id + '">View</a>' + 
	            	'</jqx-link-button>' + 
	            	'</td></tr></table>';
	            return table;
	        }
	    });
	});

	$("#btnNewProcess").on('click', function ()
			{
		$http.post('supersede-dm-app/processes/new').success(function(data) {
			$scope.alertsNum = data.length;
		});
	});
	
});

$(document).ready(function () {
//	$("#jqxListBox").jqxListBox({ width: 700 });

});