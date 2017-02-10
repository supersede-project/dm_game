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
app.controllerProvider.register('reqsCtrl', function($scope, $location, $http) {
	 
    var gameId = $location.search().id;
	var criterion = $location.search().idC;
	
 	$scope.criterion = criterion;
    $scope.requirements = [];

    $scope.getRequirements = function() {
        $http.get('supersede-dm-app/garp/game/requirements?gameId=' + gameId + '&criterion=' + criterion)
        .success(function(data) {
        	for (var i = 0; i < data.length; i++) {
        	    $scope.requirements.push(data[i]);
            }
            console.log("current requirements:");
            console.log($scope.requirements);
            $("#sortable").jqxSortable();
            $("#sortable > div").jqxExpander({ theme: "summer", expanded: false, width: 200});
        }).error(function(err){
            alert(err.message);
        });
    };

    $scope.submitPrio = function() {
    	var data = $("#sortable").jqxSortable("toArray");
    	//alert('Criterion :' + criterion);
    	//alert('Requirements :' + data);

    	var rankings = {};
    	rankings[criterion] = data;
    	
    	//alert('rankings :' + rankings);
    	
    	$http({
            url: "supersede-dm-app/garp/game/submit",
            data: rankings,
            method: 'POST',
            params: {gameId : gameId}
        }).success(function(){
        	alert('Your prioritization was saved!');
            $location.url('supersede-dm-app/garp/criteria?id=' + gameId);
        }).error(function(err){
            alert(err.message);
        });
    };	
    
    $scope.retour = function() {
      $location.url('supersede-dm-app/garp/criteria?id=' + gameId);
    };	

    $scope.getRequirements();
});