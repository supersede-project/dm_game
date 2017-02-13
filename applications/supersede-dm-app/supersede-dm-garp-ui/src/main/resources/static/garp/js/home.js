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

app.controllerProvider.register('display_games', function($scope, $http, $location) {

    function compareGamesByDate(a, b) {
        if (a.date < b.date) {
            return 1;
        }
        if (a.date > b.date) {
            return -1;
        }

        return 0;
    }

    $scope.getActiveGames = function() {
        $http.get('supersede-dm-app/garp/game/activegames')
        .success(function(data) {
            console.log(data);
            var source = {
                datatype: "json",
                datafields: [
                    { name: 'owner' },
                    { name: 'date' },
                    { name: 'status' },
                    { name: 'id' }
                ],
                localdata: data.sort(compareGamesByDate)
            };
            var cellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties) {
                var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
                r = r.concat('<jqx-link-button jqx-width="110" jqx-height="25"><a href="#/supersede-dm-app/garp/gameplay?id=');
                r = r.concat(value + '">Open Game</a></jqx-link-button></div>');
                return r;
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            $("#activeGames").jqxGrid( {
                width: '100%',
                autoheight: true,
                pageable: true,
                altrows: true,
                source: dataAdapter,
                rowsheight: 32,
                columns: [
                  { text: 'Owner', datafield: 'owner', width: '25%', align: 'center', cellsalign: 'center' },
                  { text: 'Date', datafield: 'date', width: '25%%', align: 'center', cellsalign: 'center' },
                  { text: 'Status', datafield: 'status', width: '25%', align: 'center', cellsalign: 'center' },
                  { text: '', datafield: 'id', width: '25%', align: 'center', cellsalign: 'center', cellsrenderer: cellsrenderer }
                ]
            });
        });
    };

    $scope.getOwnedGames = function() {
        $http.get('supersede-dm-app/garp/game/ownedgames')
        .success(function(data) {
            console.log(data);
            var source = {
                datatype: "json",
                datafields: [
                    { name: 'owner' },
                    { name: 'date' },
                    { name: 'status' },
                    { name: 'id' }
                ],
                localdata: data.sort(compareGamesByDate)
            };
            var cellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties) {
                var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
                r = r.concat('<jqx-link-button jqx-width="110" jqx-height="25"><a href="#/supersede-dm-app/garp/results');
                r = r.concat(value + '">Open Game</a></jqx-link-button></div>');
                return r;
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            $("#ownedGames").jqxGrid({
                width: '100%',
                autoheight: true,
                pageable: true,
                altrows: true,
                source: dataAdapter,
                rowsheight: 32,
                columns: [
                  { text: 'Owner', datafield: 'owner', width: '25%', align: 'center', cellsalign: 'center' },
                  { text: 'Date', datafield: 'date', width: '25%', align: 'center', cellsalign: 'center' },
                  { text: 'Status', datafield: 'status', width: '25%', align: 'center', cellsalign: 'center' },
                  { text: '', datafield: 'id', width: '25%', align: 'center', cellsalign: 'center', cellsrenderer: cellsrenderer }
                ]
            });
        });
    };

    $scope.createNew = function() {
        $location.url('supersede-dm-app/garp/create_game');
    };

    $scope.getActiveGames();
    $scope.getOwnedGames();
});
