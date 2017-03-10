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
	
	$scope.procId = $location.search().procId;
	
    function compareGamesByDate(a, b) {
        if (a.date < b.date) {
            return 1;
        }
        if (a.date > b.date) {
            return -1;
        }

        return 0;
    }

    function getGamesAsSupervisor() {
        $http.get('supersede-dm-app/garp/game/games?roleName=Supervisor&procId=' + $scope.procId)
        .success(function(data) {
            console.log(data);
            var source = {
                datatype: "json",
                datafields: [
                    { name: 'name'},
                    { name: 'date' },
                    { name: 'status' },
                    { name: 'id' }
                ],
                localdata: data.sort(compareGamesByDate)
            };
            var cellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties) {
                var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
                r = r.concat('<jqx-button jqx-width="110" jqx-height="25" style="margin-left: 10px; margin-right: 10px;" ');
                r = r.concat('ng-click="gameDetails(' + value + ')">Details</jqx-button></div>');
                return r;
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            $("#supervisorGames").jqxGrid({
                width: '100%',
                autoheight: true,
                pageable: true,
                altrows: true,
                source: dataAdapter,
                rowsheight: 32,
                columns: [
                    { text: 'Name', datafield: 'name', width: '50%', align: 'center', cellsalign: 'center' },
                    { text: 'Date', datafield: 'date', width: '25%', align: 'center', cellsalign: 'center' },
                    { text: 'Status', datafield: 'status', width: '10%', align: 'center', cellsalign: 'center' },
                    { text: '', datafield: 'id', width: '15%', align: 'center', cellsalign: 'center', cellsrenderer: cellsrenderer }
                ]
            });
        });
    }

    $scope.createNew = function() {
        $location.url('supersede-dm-app/garp/create_game').search('procId',$scope.procId);
    };

    $scope.gameDetails = function(gameId) {
        $location.url('supersede-dm-app/garp/game_details').search('gameId', gameId).search('procId',$scope.procId);
    };

    getGamesAsSupervisor();
});
