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

app.controllerProvider.register('display_criteria', function($scope, $http, $location) {

	var gameId = $location.search().id;
	
	$scope.gameId = gameId;
    $scope.getSelectedCriteria = function() {
        $http.get('supersede-dm-app/garp/game/gamecriteria?gameId=' + gameId)
        .success(function(data) {
            console.log(data);
            //alert('Data:' + data);
            var data2 = {};
            for (var m = 0; m < data.length; m++) {
                var row = {};
                row["id"] = data[m];
                data2[m] = row;
            }
            
            var source = {
                datatype: "array",
                datafields: [
                    { name: 'id' }
                ],
                localdata: data2
            };
            var cellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties) {
                return '<a href="http://localhost/#/supersede-dm-app/garp/gameplayTest?id=' + gameId + '&idC=' + value + '">' + value + "</a>";
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            $("#selectedCriteria").jqxGrid({
                width: 500,
                autoheight: true,
                source: dataAdapter,
                columns: [
                  { text: 'Criterion Name', datafield: 'id', width: 500, cellsrenderer: cellsrenderer }
                ]
            });
        });
    };
    
    $scope.finish = function() {
        $location.url('supersede-dm-app/garp/home');
      };

    $scope.getSelectedCriteria();
});