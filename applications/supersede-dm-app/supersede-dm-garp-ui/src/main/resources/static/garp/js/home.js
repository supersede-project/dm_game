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
        $http.get('supersede-dm-app/garp/game/games?roleName=Supervisor')
        .success(function(data) {
            console.log(data);
            var source = {
                datatype: "json",
                datafields: [
                    { name: 'name'},
                    { name: 'owner' },
                    { name: 'date' },
                    { name: 'status' },
                    { name: 'id' }
                ],
                localdata: data.sort(compareGamesByDate)
            };
            var cellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties) {
                var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
                r = r.concat('<jqx-button jqx-width="110" jqx-height="25" style="margin-left: 10px; margin-right: 10px;" ');
                r = r.concat('ng-click="closeGame(' + value + ')">Close Game</jqx-button></div>');
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
                    { text: 'Name', datafield: 'name', width: '30%', align: 'center', cellsalign: 'center' },
                    { text: 'Owner', datafield: 'owner', width: '20%', align: 'center', cellsalign: 'center' },
                    { text: 'Date', datafield: 'date', width: '20%', align: 'center', cellsalign: 'center' },
                    { text: 'Status', datafield: 'status', width: '10%', align: 'center', cellsalign: 'center' },
                    { text: '', datafield: 'id', width: '20%', align: 'center', cellsalign: 'center', cellsrenderer: cellsrenderer }
                ]
            });
        });
    }

    function getGamesAsNegotiator() {
        $http.get('supersede-dm-app/garp/game/games?roleName=Negotiator')
        .success(function(data) {
            console.log(data);
            var source = {
                datatype: "json",
                datafields: [
                    { name: 'name'},
                    { name: 'owner' },
                    { name: 'date' },
                    { name: 'status' },
                    { name: 'id' }
                ],
                localdata: data.sort(compareGamesByDate)
            };
            var cellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties) {
                var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
                r = r.concat('<jqx-button jqx-width="110" jqx-height="25" style="margin-left: 10px; margin-right: 10px;" ');
                r = r.concat('ng-click="selectSolution(' + value + ')">Select Solution</jqx-button></div>');
                r = r.concat();
                return r;
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            $("#negotiatorGames").jqxGrid( {
                width: '100%',
                autoheight: true,
                pageable: true,
                altrows: true,
                source: dataAdapter,
                rowsheight: 32,
                columns: [
                    { text: 'Name', datafield: 'name', width: '30%', align: 'center', cellsalign: 'center' },
                    { text: 'Owner', datafield: 'owner', width: '20%', align: 'center', cellsalign: 'center' },
                    { text: 'Date', datafield: 'date', width: '20%', align: 'center', cellsalign: 'center' },
                    { text: 'Status', datafield: 'status', width: '10%', align: 'center', cellsalign: 'center' },
                    { text: '', datafield: 'id', width: '20%', align: 'center', cellsalign: 'center', cellsrenderer: cellsrenderer }
                ]
            });
        });
    }

    function getGamesAsOpinionProvider() {
        $http.get('supersede-dm-app/garp/game/games?roleName=OpinionProvider')
        .success(function(data) {
            console.log(data);
            var source = {
                datatype: "json",
                datafields: [
                    { name: 'name'},
                    { name: 'owner' },
                    { name: 'date' },
                    { name: 'status' },
                    { name: 'id' }
                ],
                localdata: data.sort(compareGamesByDate)
            };
            var cellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties) {
                var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
                r = r.concat('<jqx-button jqx-width="110" jqx-height="25" style="margin-left: 10px; margin-right: 10px;" ');
                r = r.concat('ng-click="vote(' + value + ')">Vote</jqx-button></div>');
                r = r.concat();
                return r;
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            $("#opinionProviderGames").jqxGrid( {
                width: '100%',
                autoheight: true,
                pageable: true,
                altrows: true,
                source: dataAdapter,
                rowsheight: 32,
                columns: [
                    { text: 'Name', datafield: 'name', width: '30%', align: 'center', cellsalign: 'center' },
                    { text: 'Owner', datafield: 'owner', width: '20%', align: 'center', cellsalign: 'center' },
                    { text: 'Date', datafield: 'date', width: '20%', align: 'center', cellsalign: 'center' },
                    { text: 'Status', datafield: 'status', width: '10%', align: 'center', cellsalign: 'center' },
                    { text: '', datafield: 'id', width: '20%', align: 'center', cellsalign: 'center', cellsrenderer: cellsrenderer }                ]
            });
        });
    }

    $scope.createNew = function() {
        $location.url('supersede-dm-app/garp/create_game');
    };

    $scope.closeGame = function(gameId) {
        $location.url('supersede-dm-app/garp/close_game').search('gameId', gameId);
    };

    $scope.selectSolution = function(gameId) {
        $location.url('supersede-dm-app/garp/select_solution').search('gameId', gameId);
    };

    $scope.vote = function(gameId) {
        $location.url('supersede-dm-app/garp/vote').search('id', gameId);
    };

    getGamesAsSupervisor();
    getGamesAsNegotiator();
    getGamesAsOpinionProvider();
});
