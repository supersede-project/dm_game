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

	$scope.now = function()
	{
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
    
    $http.get('supersede-dm-app/ahprp/requirement')
	.success(function(data) {
		for(var i = 0; i < data.length; i++) {
			$scope.requirements.push(data[i]);
		    $("#jqxgrid").jqxGrid("addrow",null,
		    		{ selected: "false",
		    		 ReqID: data[i].requirementId,
		    		 ReqName: data[i].name,
		    		 ReqStatus: "Requirement" } );
//		    		 ReqDesc: data[i].description } );
		}
	});
    
    $http.get('supersede-dm-app/ahprp/criteria')
	.success(function(data) {
		for(var i = 0; i < data.length; i++)
		{
			$scope.criterias.push(data[i]);
		}
	});
    
    $scope.doPlan = function()
    {
    	var accuracy = $("#accuracyLevel").jqxSlider('value');
    	
    	$('#jqxLoader').jqxLoader('open');
    	
    	$http.get('supersede-dm-app/orchestration/plan?accuracy='+accuracy).success(
    			function(data) {
    				
    				var dock = $("#jqxDockPanel").jqxDockPanel('getInstance');
    				$('#container').jqxDraw();
    				var renderer = $('#container').jqxDraw('getInstance');
    				
    				renderer.clear();
    				
    				var top = 50;
    				var hgap = (dock.height - top) / (data.steps.length +3);
    				var maxx = dock.width;
    				
    				// Running variable, representing the bottom y of the last drawn element
    				var cury = top;
    				
    	            renderer.width = maxx;
    	            var size = renderer.getSize();
    	            var centerx = size.width / 2;
    	            var centery = size.height / 2 ;
    	            
    	            var circleElement = renderer.circle( centerx, top + 25, 25, {});
    	            renderer.attr(circleElement, { fill: 'white', stroke: 'black' });
    	            cury = top + 50;
    	            
    	            var lastElement = circleElement;
    	            
    	            var elemh = 50;
    	            
    	            for( i = 0; i < data.steps.length; i++ ) {
    	            	var ofsy = top + hgap + (i * hgap);
        	            var shapeTask = renderer.path( 
        	            		"M " + (centerx - 40) + " " + ofsy + " " +
        	            		"L " + (centerx + 40) + " " + ofsy + " " +
        	            		"L " + (centerx + 40) + " " + (ofsy + elemh) + " " +
        	            		"L " + (centerx - 40) + " " + (ofsy + elemh) + " " +
        	            		"Z" );
        	            renderer.attr( shapeTask, { fill: 'white', stroke: 'black' });
        	            renderer.text( "Step " + (i +1), centerx, ofsy, 
        	            		undefined, 50, 0, { 'class': 'largeText', fill: 'black', stroke: 'grey' }, false, 'center', 'center', 'centermiddle');
        	            for( j = 0; j < data.steps[i].activities.length; j++ ) {
            	            renderer.text( data.steps[i].activities[j].methodName, centerx, ofsy + 15, 
            	            		undefined, 50, 0, 
            	            		{ 'class': 'largeText', fill: 'black', stroke: 'grey' }, 
            	            		false, 'center', 'center', 'centermiddle');
        	            }
        	            
        	            var lineElement = renderer.path(
        	            		"M " + centerx + "," + cury + " " + 
        	            		"L " + centerx + ", " + (ofsy) + " ", { stroke: '#777777' });
        	            

        	            var step = data.steps[i];
        	            var showDetails = function(details,index) {
        	            	while( $(".jqx-expander-header").length > 1 ) {
        	            		$('#jqxNavigationBar').jqxNavigationBar('remove', 1);
        	            	}
            	            $('#jqxNavigationBar').jqxNavigationBar(
            	            		'update', '0', 'Step ' + index, '<ul><li>Method: ' + details.activities[0].methodName + '</li></ul>');
//        	            	alert(details.activities[0].methodName);
            	            $('#jqxNavigationBar').jqxNavigationBar(
            	            		'add', 'Requirements', '<ul><li>Requirement 1</li><li>Requirement 2</li></ul>');
            	            var users = '<ul><li>' + details.activities[0].options.players + '</li></ul>';
            	            $('#jqxNavigationBar').jqxNavigationBar(
            	            		'add', 'Users', users );
            	            var optString = "<ul>";
            	            optString += "<li>Gamification: " + details.activities[0].options.gamification;
            	            optString += "<li>Negotiator: " + details.activities[0].options.negotiator;
            	            optString += "</ul>";
            	            $('#jqxNavigationBar').jqxNavigationBar(
            	            		'add', 'Options', optString );
        	            }
        	            renderer.on( shapeTask, 'click', function () { 
        	            	showDetails(step,i); 
        	            });
        	            
        	            cury = ofsy + elemh;
        	            lastElement = shapeTask;
    	            }
    	            
    	            circleElement = renderer.circle( centerx, ((top + ((1 + data.steps.length) * hgap)) + 25), 25, {});
    	            renderer.attr(circleElement, { fill: 'white', stroke: 'black', 'stroke-width': '5' });
    	            
    	            renderer.path(
    	            		"M " + centerx + "," + cury + " " + 
    	            		"L " + centerx + ", " + (top + ((1 + data.steps.length) * hgap)) + " ", { stroke: '#777777' });
    	            
    	            renderer.refresh();
    	            
    	            $('#jqxLoader').jqxLoader('close');
    	            
//    	            $('#jqxNavigationBar').jqxNavigationBar('update', '0', 'Requirements', '<ul><li>Requirement 1</li><li>Requirement 2</li></ul>');
    			}
    		);
    }
    
    $scope.toggleSelection = function(array, item)
	{
	    var idx = array.indexOf(item);
	    if (idx > -1) {
	    	array.splice(idx, 1);
	    }
	    else {
	    	array.push(item);
	    }
	};
	
	$scope.toPage = function(p)
	{
		if(p == 3)
		{
			if($scope.game.players.length > 0 &&
					$scope.game.requirements.length > 1 &&
					$scope.game.criterias.length > 1)
			{
				$scope.currentPage = 'page3';
			}
		}
		else
		{
			$scope.currentPage = 'page' + p;
		}
	}
	
});

$(document).ready(function () {
    $("#jqxDockPanel").jqxDockPanel({ width: 700, height: 500});
    $('#jqxDockPanel').jqxDockPanel('render');
    $("#jqxNavigationBar").jqxNavigationBar({ width: 200, height: 430, expandMode: 'singleFitHeight'});
    $("#jqxDateTimeSelector").jqxDateTimeInput({ formatString: 'F' });
    $(document).ready(function () {
        $("#navBar1").jqxNavBar({
//            height: 40, selectedItem: 0
        });
    });
    $('#accuracyLevel').jqxSlider({ 
    	tooltip: true, 
    	mode: 'fixed',
    	max: 4,
    	tooltipFormatFunction: function(value){
    		switch (value) {
            case 0:
            	return "No precision; maximum speed";
            	break;
            case 1:
            	return "Prefer time saving over precision";
            	break;
            case 2:
            	return "Balance precision and time";
            	break;
            case 3:
            	return "Prefer precision over time saving";
            	break;
            case 4:
            	return "Max precision, no matter time";
            	break;
    		}
    	}
    });
    var source =
    {
        datafields: [
            { name: 'selected', type: 'bool' },
            { name: 'ReqID', type: 'string' },
            { name: 'ReqName', type: 'string' },
            { name: 'ReqStatus', type: 'string' }
        ],
    };
    var dataAdapter = new $.jqx.dataAdapter(source, {
        downloadComplete: function (data, status, xhr) { },
        loadComplete: function (data) { },
        loadError: function (xhr, status, error) { }
    });
    $("#jqxgrid").jqxGrid(
            {
                width: 750,
                source: dataAdapter,                
                pageable: true,
                autoheight: true,
                sortable: true,
                altrows: true,
                enabletooltips: true,
                editable: true,
                selectionmode: 'multiplecellsadvanced',
                columns: [
                  { text: 'Select', columntype: 'checkbox', datafield: 'selected', width: 50 },
                  { text: 'ID', datafield: 'ReqID', width: 100 },
                  { text: 'Name', datafield: 'ReqName', width: 300 },
                  { text: 'Status', datafield: 'ReqStatus', width: 300 }
//                  { text: 'Description', datafield: 'ReqDesc', width: 300 }
                ]
            });
    
//    $("#jqxgrid").jqxGrid("addrow",null,
//    		{ selected: "true",
//    		 ReqID: 'R01',
//    		 ReqName: "ReqName" ,
//    		 ReqDesc: "Description" } );
});

