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

app.controllerProvider.register('plan', function($scope, $http, $location) {

    $scope.now = function () {
        return new Date().toJSON().slice(0, 19).replace("T", " ");
    };

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
    }).error(function (err) {
        alert(err.message);
    });

    $http.get('supersede-dm-app/requirement')
    .success(function(data) {
        for(var i = 0; i < data.length; i++) {
            $scope.requirements.push(data[i]);
            $("#jqxgrid").jqxGrid("addrow",null,
                    { selected: "false",
                     ReqId: data[i].requirementId,
                     ReqName: data[i].name,
                     ReqStatus: "Requirement" } );
        }
    }).error(function (err) {
        alert(err.message);
    });

    $http.get('supersede-dm-app/criteria')
    .success(function(data) {
        for(var i = 0; i < data.length; i++)
        {
            $scope.criterias.push(data[i]);
        }
    }).error(function (err) {
        alert(err.message);
    });

    var showDetails = function (details, index) {
        while ($(".jqx-expander-header").length > 1) {
            $('#jqxNavigationBar').jqxNavigationBar('remove', 1);
        }

        $('#jqxNavigationBar').jqxNavigationBar(
                'update', '0', 'Step ' + index, '<ul><li>Method: ' + details.activities[0].methodName + '</li></ul>');
        $('#jqxNavigationBar').jqxNavigationBar(
                'add', 'Requirements', '<ul><li>Requirement 1</li><li>Requirement 2</li></ul>');
        var users = '<ul><li>' + details.activities[0].options.players + '</li></ul>';

        $('#jqxNavigationBar').jqxNavigationBar('add', 'Users', users);
        var optString = "<ul>";
        optString += "<li>Gamification: " + details.activities[0].options.gamification;
        optString += "<li>Negotiator: " + details.activities[0].options.negotiator;
        optString += "</ul>";
        $('#jqxNavigationBar').jqxNavigationBar('add', 'Options', optString);
    };
    
    
    $scope.doPlan = function () {
        var accuracy = $("#accuracyLevel").jqxSlider('value');

        $('#jqxLoader').jqxLoader('open');

        $http.get('supersede-dm-app/orchestration/plan?accuracy=' + accuracy).success(
                function (data) {
                	
                	var mkbox = function( x1, y2, x2, y2 ) {
                		var box = {};
                    	box.top = x1;
                    	box.left = y1;
                    	box.right = x2;
                    	box.bottom = y2;
                    	return box;
                	}
                	
                    var dock = $("#jqxDockPanel").jqxDockPanel('getInstance');
                    $('#container').jqxDraw();
                    var renderer = $('#container').jqxDraw('getInstance');

                    renderer.clear();

                    var boxes = [];
                    
                    var top = 50;
                    var hgap = (dock.height - top) / (data.steps.length + 3);
                    var maxx = dock.width;

                    // Running variable, representing the bottom y of the last drawn element
                    var cury = top;

                    renderer.width = maxx;
                    var size = renderer.getSize();
                    var centerx = size.width / 2;

                	var box = {};
                	
                	box.top = top;
                	box.left = centerx - 35;
                	box.right = centerx + 35;
                	box.bottom = top + 50;
                	boxes.push( box );
                	
//                    var circleElement = renderer.circle(centerx, top + 25, 25, {});
                    var circleElement = renderer.circle( box.left + 35, box.top + 25, 25, {});
                    renderer.attr(circleElement, { fill: 'white', stroke: 'black' });
                    cury = top + 50;

                    var lastElement = circleElement;

                    var elemh = 50;
                    
                    for (var i = 0; i < data.steps.length; i++) {
                    	
                    	var ofsy = top + hgap + (i * hgap);
                    	
                    	var box = {};
                    	
                    	box.left = centerx - 60;
                    	box.top = ofsy;
                    	box.right = centerx + 60;
                    	box.bottom = ofsy + elemh;
                    	boxes.push( box );
                    	
                        var shapeTask = renderer.path(
                                "M " + box.left + " " + box.top + " " +
                                "L " + box.right + " " + box.top + " " +
                                "L " + box.right + " " + box.bottom + " " +
                                "L " + box.left + " " + box.bottom + " " +
                                "Z");
                        renderer.attr(shapeTask, { fill: 'white', stroke: 'black' });
                        renderer.text("Step " + (i + 1), box.left, box.top,
                                undefined, 50, 0, { 'class': 'largeText', fill: 'black', stroke: 'grey' }, false, 'center', 'center', 'centermiddle');
                        for (var j = 0; j < data.steps[i].activities.length; j++) {
                            renderer.text(data.steps[i].activities[j].methodName, centerx, ofsy + 15,
                                    undefined, 50, 0,
                                    { 'class': 'largeText', fill: 'black', stroke: 'grey' },
                                    false, 'center', 'center', 'centermiddle');
                        }
                        
//                        console.log( boxes[boxes.length-2] );
                        
                        renderer.path(
                                "M " + (boxes[boxes.length-2].left + ((boxes[boxes.length-2].right - boxes[boxes.length-2].left) /2)) + "," + boxes[boxes.length-2].bottom + " " +
                                "L " + (boxes[boxes.length-1].left + ((boxes[boxes.length-1].right - boxes[boxes.length-1].left) /2)) + ", " + boxes[boxes.length-1].top + " "
                                , { stroke: '#777777' });
                        
                        var step = data.steps[i];

                        renderer.on(shapeTask, 'click', function () {
                            showDetails(step, i);
                        });

                        cury = ofsy + elemh;
                        lastElement = shapeTask;
                    }

                	var box = {};
                	
                	box.left = centerx - 25;
                	box.top = top + ((1 + data.steps.length) * hgap);
                	box.right = centerx + 25;
                	box.bottom = box.top + 50;
                	boxes.push( box );
                	
//                    circleElement = renderer.circle(centerx, ((top + ((1 + data.steps.length) * hgap)) + 25), 25, {});
                    circleElement = renderer.circle( 
                    		box.left + 25, 
                    		box.top +25, 
                    		25, {});
                    
                    renderer.attr(circleElement, { fill: 'white', stroke: 'black', 'stroke-width': '5' });

                    renderer.path(
                            "M " + (boxes[boxes.length-2].left + ((boxes[boxes.length-2].right - boxes[boxes.length-2].left) /2)) + "," + boxes[boxes.length-2].bottom + " " +
                            "L " + (boxes[boxes.length-1].left + ((boxes[boxes.length-1].right - boxes[boxes.length-1].left) /2)) + ", " + boxes[boxes.length-1].top + " "
                            , { stroke: '#777777' });

                    renderer.refresh();

                    $('#jqxLoader').jqxLoader('close');
                }
            );
    };

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

    $scope.toPage = function (p) {
        if (p == 3) {
            if ($scope.game.players.length > 0 &&
                    $scope.game.requirements.length > 1 &&
                    $scope.game.criterias.length > 1) {
                $scope.currentPage = 'page3';
            }
        }
        else {
            $scope.currentPage = 'page' + p;
        }
    };
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
            case 1:
                return "Prefer time saving over precision";
            case 2:
                return "Balance precision and time";
            case 3:
                return "Prefer precision over time saving";
            case 4:
                return "Max precision, no matter time";
            }
        }
    });
    var source =
    {
        datafields: [
            { name: 'selected', type: 'bool' },
            { name: 'ReqId', type: 'string' },
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
                  { text: 'Id', datafield: 'ReqId', width: 100 },
                  { text: 'Name', datafield: 'ReqName', width: 300 },
                  { text: 'Status', datafield: 'ReqStatus', width: 300 }
                ]
            });
});

