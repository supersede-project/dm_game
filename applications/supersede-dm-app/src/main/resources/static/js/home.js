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

var http = undefined;
var loadProcesses = undefined;

app.controllerProvider.register('home', function($scope, $http, $location) {

	http = $http;
	
	$scope.now = function() {
		return new Date().toJSON().slice(0,19).replace("T", " ");
	}

	$scope.reqNum = undefined;

    $http.get('supersede-dm-app/alerts/biglist').success(function(data) {
    	
    	console.log( data );
    	
        var source =
        {
            datatype: "json",
            localdata: data,
            datafields: [
                { name: 'applicationID', map: 'applicationID' },
                { name: 'alertID' },
                { name: 'id' },
                { name: 'timestamp' },
                { name: 'description' },
                { name: 'classification' },
                { name: 'accuracy' },
                { name: 'pos' },
                { name: 'neg' },
                { name: 'overall' },
            ],
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $("#gridAlerts").jqxGrid(
        {
            width: 900,
            source: dataAdapter,
//            pageable: true,
//            columnsResize: true,
//            altRows: true,
            groupable: true,
//            ready: function () {
//                $("#treeGrid").jqxTreeGrid('expandRow', "0");
//            },
            columns: [
              { text: 'App', dataField: 'applicationID', width: 50 },
              { text: 'Alert', dataField: 'alertID', width: 50 },
              { text: 'ID', dataField: 'id', width: 50 },
              { text: 'Timestamp', dataField: 'timestamp', width: 100 },
              { text: 'Description', dataField: 'description', minWidth: 100, width: 200 },
              { text: 'Classification', dataField: 'classification', minWidth: 100, width: 150 },
              { text: 'Accuracy', dataField: 'accuracy', width: 50 },
              { text: 'Pos.', dataField: 'pos', width: 58 },
              { text: 'Neg.', dataField: 'neg', width: 58 },
              { text: 'Overall.', dataField: 'overall', width: 50 }
//              { text: 'Features.', dataField: 'features', width: 120 }
            ]
        ,groups: ['applicationID', 'alertID']
        });
    });
    
	$http.get('supersede-dm-app/requirement?procFx=Eq&procId=-1').success(function(data) {
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
	    	itemHeight: 70, width: '100%',
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
	
	$scope.loadProcesses = function() {
		$http.get('supersede-dm-app/processes/list').success(function(data) {
//			$('#listbox').jqxListBox('clear');
			$scope.procNum = data.length;
			var source = {
		        localdata: data,
		        datatype: "array"
		    };
			var dataAdapter = new $.jqx.dataAdapter(source);
		    $('#listbox').jqxListBox({ selectedIndex: 0,  
		    	source: dataAdapter, 
		    	displayMember: "name", 
		    	valueMember: "id", 
		    	itemHeight: 70, width: '100%',
		        renderer: function (index, label, value) {
		            var datarecord = data[index];
//		            var imgurl = '../../images/' + label.toLowerCase() + '.png';
//		            var img = '<img height="50" width="40" src="' + imgurl + '"/>';
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
		            	+ action + 
		            	'</td><td>' + 
		            	datarecord.name + 
		            	" (" + 
		            	datarecord.objective + ")" + 
		            	'</td>' + 
		            	'<td style="width: 40px;" rowspan="2">' +
		            	'<jqx-link-button jqx-width="200" jqx-height="30"> <a ' + 
		            	'href="#/supersede-dm-app/process?procId=' + datarecord.id + '">View</a></jqx-link-button>' + 
//		            	'<jqx-button style="margin-left: 10px" ng-click="closeProcess(\'' + datarecord.id + '\')">Close</jqx-button>' +
		            	'<jqx-link-button style="margin-left: 10px")"><a href="javascript:" onclick="closeProcess(\'' + datarecord.id + '\');">Close</a></jqx-button>' +
		            	'</td>' +
		            	'</tr><tr><td>' + 
		            	"Created: " + datarecord.date + 
		            	'</td></tr></table>';
		            return table;
		        }
		    });
		});
	}
	
	$scope.closeProcess = function(procId) {
		console.log( "deleting process " + procId );
		$http.post('supersede-dm-app/processes/close?procId=' + procId).success(function(data) {
			$scope.loadProcesses();
		});
	};
	
	$scope.loadProcesses();

	$("#btnNewProcess").on('click', function () {
		$http.post('supersede-dm-app/processes/new').success(function(data) {
			$scope.loadProcesses();
		});
	});
	
	loadProcesses = $scope.loadProcesses;
	
});

var closeProcess = function(procId) {
	console.log( "deleting process " + procId );
	http.post('supersede-dm-app/processes/close?procId=' + procId).success(function(data) {
		loadProcesses();
	});
};

$(document).ready(function () {
	$("#jqxExpander").jqxExpander({ width: '100%', expanded: false });
	$("#expRequirements").jqxExpander({ width: '100%', expanded: false });
	$("#expProcesses").jqxExpander({ width: '100%', expanded: false });
	$("#expActivities").jqxExpander({ width: '100%', expanded: false });
});