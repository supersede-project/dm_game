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

app.controllerProvider.register('depcheck_editor', function($scope, $http, $location) {
	
    $scope.getRequirements = function() {
        $http.get('supersede-dm-app/ahprp/requirement')
        .success(function(data) {
        	var source = [];
        	for(var i = 0; i < data.length; i++) {
        		source.push( { icon: "supersede-dm-app/depcheck/img/requirement-small.png", label: data[i].name, expanded: true } );
        	}
            $('#jqxTree').jqxTree({ source: source, width: '100%', height: '100%'});
            $('#jqxTree').jqxTree('selectItem', null);
        });
    };
    
    $scope.getRequirements();
});

$(document).ready(function () {
	$('#mainSplitter').jqxSplitter( {
		width: 850, height: 600, panels: [{ size: 400, min: 100 }, {min: 200, size: 400}] });
	$('#jqxExpander').jqxExpander({ showArrow: false, toggleMode: 'none', width: '300px', height: '400px'});
	$("#jqxRightWidget").jqxPanel({ width: 300, height: 400});
	$("#jqxradiobutton1").jqxRadioButton({ width: 220, height: 25 });
    $("#jqxradiobutton2").jqxRadioButton({ width: 220, height: 25 });
    // bind to change event.
    $("#jqxradiobutton1").bind('change', function (event) {
        var checked = event.args.checked;
//        alert('jqxradiobutton1 checked: ' + checked);
    });
    $("#jqxradiobutton2").bind('change', function (event) {
        var checked = event.args.checked;
//        alert('jqxradiobutton2 checked: ' + checked);
    });
});
