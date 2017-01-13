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

    $scope.getOwnedGames = function() {
        $http.get('supersede-dm-app/garp/game/ownedgames')
        .success(function(data) {
            console.log(data);
            var source = {
                datatype: "json",
                datafields: [
                    { name: 'id' },
                    { name: 'owner' },
                    { name : 'date' },
                    { name : 'status' }
                ],
                localdata: data
            };
            var cellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties) {
                return '<a href="#/supersede-dm-app/garp/results' + value + '">' + value + "</a>";
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            $("#ownedGames").jqxGrid({
                width: 500,
                autoheight: true,
                source: dataAdapter,
                columns: [
                  { text: 'Id', datafield: 'id', width: 200, cellsrenderer: cellsrenderer },
                  { text: 'Owner', datafield: 'owner', width: 100 },
                  { text: 'Date', datafield: 'date', width: 100 },
                  { text: 'Status', datafield: 'status', width: 100 }
                ]
            });
        });
    };

    $scope.getActiveGames = function() {
        $http.get('supersede-dm-app/garp/game/activegames')
        .success(function(data) {
            console.log(data);
            var source = {
                datatype: "json",
                datafields: [
                    { name: 'id' },
                    { name: 'owner' },
                    { name : 'date' },
                    { name : 'status' }
                ],
                localdata: data
            };
            var cellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties) {
                return '<a href="#/supersede-dm-app/garp/criteria?id=' + value + '">' + value + "</a>";
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            $("#activeGames").jqxGrid( {
                width: 500,
                autoheight: true,
                source: dataAdapter,
                columns: [
                  { text: 'Id', datafield: 'id', width: 200, cellsrenderer: cellsrenderer },
                  { text: 'Owner', datafield: 'owner', width: 100 },
                  { text: 'Date', datafield: 'date', width: 100 },
                  { text: 'Status', datafield: 'status', width: 100 }
                ]
            });
        });
    };

    $scope.createGame = function() {
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
            alert(err.message);
        });
    };

    $scope.createNew = function() {
        $http({
            url: "supersede-dm-app/garp/game/newrandom",
            method: 'GET'
        }).success(function(data, status){
            console.log(data);
            $scope.getOwnedGames();
            $scope.getActiveGames();
        }).error(function(err){
            alert(err.message);
        });
    };

    $scope.getOwnedGames();
    $scope.getActiveGames();
});