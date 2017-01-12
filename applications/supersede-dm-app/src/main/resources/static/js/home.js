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
		for(var i = 0; i < data.length; i++)
		{
			$scope.requirements.push(data[i]);
		}
	});
    
    $http.get('supersede-dm-app/ahprp/criteria')
	.success(function(data) {
		for(var i = 0; i < data.length; i++)
		{
			$scope.criterias.push(data[i]);
		}
	});

    $http.get('supersede-dm-app/ahprp/requirementchoice')
	.success(function(data) {
		$scope.requirementsChoices.length = 0;
		for(var i = 0; i < data.length; i++)
		{
			$scope.requirementsChoices.push(data[i]);
		}
	});
    
    $scope.doPlan = function()
    {
    	$http.get('supersede-dm-app/orchestration/plan').success(
    			function(data) {
    				
    				var dock = $("#jqxDockPanel").jqxDockPanel('getInstance');
    				
    				var maxx = dock.width;
    				
    				$('#container').jqxDraw();
    	            var renderer = $('#container').jqxDraw('getInstance');
    	            renderer.sidth = maxx;
    	            var size = renderer.getSize();
    	            // create a circle
    	            // params: cx, cy, radius, params
    	            var centerx = size.width / 2;
    	            var centery = size.height / 2 ;
    	            var circleElement = renderer.circle( centerx, 40, 25, {});
    	            renderer.attr(circleElement, { fill: 'white', stroke: 'black' });
    	            
    	            for( i = 0; i < data.steps.length; i++ ) {
    	            	var ofsy = 80 + (i * 80);
        	            var circleElement = renderer.circle( centerx, ofsy + 25, 25, {});
        	            renderer.attr(circleElement, { fill: 'white', stroke: 'black' });
        	            renderer.text( "Step " + i, centerx, ofsy, 
        	            		undefined, 50, 0, { 'class': 'largeText', fill: 'black', stroke: 'grey' }, false, 'center', 'center', 'centermiddle');
        	            for( j = 0; j < data.steps[i].activities.length; j++ ) {
            	            renderer.text( data.steps[i].activities[j].methodName, centerx, ofsy + 15, 
            	            		undefined, 50, 0, 
            	            		{ 'class': 'largeText', fill: 'black', stroke: 'grey' }, 
            	            		false, 'center', 'center', 'centermiddle');
        	            }
    	            }
    	            
    	            circleElement = renderer.circle( centerx, 360, 25, {});
    	            renderer.attr(circleElement, { fill: 'white', stroke: 'black', 'stroke-width': '5' });
    	            
//    	            renderer.attr(circleElement, { fill: 'lightblue', stroke: 'darkblue' });
    	            // draw a rectangle around the entire area
    	            // params: x, y, width, height, attributes
    	            var borderElement = renderer.rect(0, 0, size.width, size.height, { stroke: '#777777', fill: 'transparent' });
    	            // draw a path
    	            // params: line command, attributes
//    	            var pathElement = renderer.path("M 100,100 L 150, 130 C 130,130 180,190 150,150", { stroke: '#777777', fill: 'red' });
    	            // draw an area
    	            // params: line command, attributes
//    	            var areaElement = renderer.path("M 300,300 L 350, 330 C 330,330 380,390 350,350 Z", { stroke: '#777777', fill: 'yellow' });
    	            // draw a line
    	            // params: x1, y1, x2, y2, attributes
//    	            renderer.line(600, 100, 600, 200, { stroke: 'blue', 'stroke-width': 6 });
//    	            renderer.line(550, 50, 650, 90, { stroke: 'green', 'stroke-width': 6 });
    	            // draw text
    	            // params: text, x, y, width, height, angle, params, clip, halign, valign, rotateAround
//    	            renderer.text("Drawing shapes", 50, 20, undefined, undefined, 0, { 'class': 'largeText', fill: 'yellow', stroke: 'orange' }, false, 'center', 'center', 'centermiddle');
//    	            renderer.text("This is rotated text", 600, 300, undefined, undefined, 45, { 'class': 'smallText' }, false, 'center', 'center', 'centermiddle');
    	            // add an event handler to the circle element
    	            
    	            var circleUp = renderer.circle(50, 450, 30, { fill: '#DEDEDE', stroke: '#777777' });
    	            var pathUp = renderer.path("M30 460 L 70 460 L50 430 Z", { fill: 'transparent', stroke: '#777777', 'stroke-width': 1 });
    	            var circleDown = renderer.circle(120, 450, 30, { fill: '#DEDEDE', stroke: '#777777' });
    	            var pathDown = renderer.path("M100 440 L 140 440 L120 470 Z", { fill: 'transparent', stroke: '#777777', 'stroke-width': 1 });
    	            renderer.text("Click these buttons:", 20, 390);
    	            var moveCircle = function (moveUp) {
    	                var circleY = parseInt(renderer.getAttr(circleElement, 'cy'));
    	                renderer.attr(circleElement, { cy: circleY + (moveUp ? -10 : 10) });
    	            }
    	            renderer.on(circleUp, 'click', function () { moveCircle(true); });
    	            renderer.on(pathUp, 'click', function () { moveCircle(true); });
    	            renderer.on(circleDown, 'click', function () { moveCircle(false); });
    	            renderer.on(pathDown, 'click', function () { moveCircle(false); });
    	            renderer.refresh();
    	            
    	            $('#jqxNavigationBar').jqxNavigationBar('update', '0', 'Requirements', '<ul><li>Requirement 1</li><li>Requirement 2</li></ul>');
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
	
	$scope.createGame = function()
	{
		$http({
			url: "supersede-dm-app/ahprp/game",
	        data: $scope.game,
	        method: 'POST',
	        params: {criteriaValues : $scope.choices}
	    }).success(function(data){
	        $scope.game = {players : [], requirements: [], criterias: [], title: "Decision Making Process " + $scope.now()};
	    	$scope.choices = {};
	    	$scope.currentPage = 'page1';
	    	$location.url('supersede-dm-app/ahprp/game_page').search('gameId', data);
	    }).error(function(err){
	    	console.log(err);
	    });
	};
});

$(document).ready(function () {
    $("#jqxDockPanel").jqxDockPanel({ width: 700, height: 500});
    $('#jqxDockPanel').jqxDockPanel('render');
    $("#jqxNavigationBar").jqxNavigationBar({ width: 200, height: 430, expandMode: 'singleFitHeight'});
});