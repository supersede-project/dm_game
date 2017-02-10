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

app.controllerProvider.register('requirements', function($scope, $http, $location) {

	$scope.now = function() {
		return new Date().toJSON().slice(0,19).replace("T", " ");
	}
	
    $scope.players = [];
    $scope.requirements = [];
    $scope.criterias = [];
    
    $scope.currentPlayer = undefined;
    $scope.currentRequirement= undefined;
    $scope.currentCriteria = undefined;
    
    $scope.game = {players : [], requirements: [], criterias: [], title: "Decision Making Process " + $scope.now()};
    
    $scope.currentPage = 'page1';
    
    $scope.requirementsChoices = [];
    
    $scope.choices = {};
    
    $http.get('supersede-dm-app/user?profile=OPINION_PROVIDER')
	.success(function(data) {
		for(var i = 0; i < data.length; i++)
		{
			$scope.players.push(data[i]);
		}
	});
    
    $http.get('supersede-dm-app/requirement').success(function(data) {
		for (var i = 0; i < data.length; i++) {
			$scope.requirements.push(data[i]);
		    $("#jqxListBox").jqxListBox('addItem', 
		    		{ label: data[i].name } );
		}
	});
    
    $http.get('supersede-dm-app/alerts/biglist').success(function(data) {
    	
//    	console.log( data );
    	
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
        $("#jqxgrid").jqxGrid(
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
    
	$scope.toPage = function(page) {
		alert(page);
		$scope.currentPage = page;
	}
});

$(document).ready(function () {
        $("#jqxListBox").jqxListBox({ width: 700 });
});