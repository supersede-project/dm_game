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

app.controllerProvider.register('game_details', function($scope, $http, $location) {

    var gameId = $location.search().gameId;
    var processId = $location.search().processId;
    var requirements = [];
    var gameStatus;
    var open = 'Open';

    $scope.gameRequirements = {};
    $scope.solution = [];

    function getNegotiator() {
        $http.get('supersede-dm-app/garp/game/negotiators?gameId=' + gameId)
        .success(function (data) {
            var negotiator = {
                datatype: "json",
                datafields: [
                    { name: 'userId' },
                    { name: 'name' },
                    { name: 'email' }
                ],
                localdata: data
            };
            var dataAdapter = new $.jqx.dataAdapter(negotiator);
            $("#negotiator").jqxGrid({
                width: '100%',
                altrows: true,
                autoheight: true,
                pageable: true,
                source: dataAdapter,
                columns: [
                    { text: 'Id', datafield: 'userId', width: '20%' },
                    { text: 'Name', datafield: 'name', width: '40%' },
                    { text: 'Email', datafield: 'email' }
                ]
            });
        }).error(function (err) {
            alert(err.message);
        });
    }

    function getOpinionProviders() {
        $http.get('supersede-dm-app/garp/game/opinionproviders?gameId=' + gameId)
        .success(function (data) {
            var opinionProviders = {
                datatype: "json",
                datafields: [
                    { name: 'userId' },
                    { name: 'name' },
                    { name: 'email' }
                ],
                localdata: data
            };
            var dataAdapter = new $.jqx.dataAdapter(opinionProviders);
            $("#opinion_providers").jqxGrid({
                width: '100%',
                altrows: true,
                autoheight: true,
                pageable: true,
                source: dataAdapter,
                columns: [
                    { text: 'Id', datafield: 'userId', width: '20%' },
                    { text: 'Name', datafield: 'name', width: '40%' },
                    { text: 'Email', datafield: 'email' }
                ]
            });
        }).error(function (err) {
            alert(err.message);
        });
    }

    $scope.saveRankings = function () {
        $http.put('supersede-dm-app/garp/game/rankings/save?processId=' + processId + "&gameId=" + gameId + "&name=GA-Default")
        .success(function (data) {
            $location.url('supersede-dm-app/process').search('processId', processId);
        }).error(function (err) {
            alert(err.message);
        });
    };

    $scope.getRequirement = function (requirementId) {
        return $scope.gameRequirements[requirementId];
    };

    $scope.closeGame = function () {
        $http.post('supersede-dm-app/garp/game/closegame?gameId=' + gameId + "&processId=" + processId)
        .success(function (data) {
            $("#game_status").html("<strong>Game successfully closed!</strong>");
        }).error(function (err) {
            $("#game_status").html("<strong>Unable to close the game: " + err.message + "</strong>");
        });
    };

    $scope.openGame = function () {
        $http.post('supersede-dm-app/garp/game/opengame?gameId=' + gameId + "&processId=" + processId)
        .success(function (data) {
            $("#game_status").html("<strong>Game successfully opened!</strong>");
        }).error(function (err) {
            $("#game_status").html("<strong>Unable to open the game: " + err.message + "</strong>");
        });
    };

    $scope.solutionSelected = function () {
        return $scope.solution.length !== 0;
    };

    $scope.gameOpen = function () {
        return gameStatus == open;
    };

    $scope.gameClosed = function () {
        return gameStatus != open;
    };

    $scope.home = function () {
        $location.url('supersede-dm-app/garp/home?processId=' + processId);
    };

    function getGameInfo() {
        $http.get('supersede-dm-app/garp/game/game?gameId=' + gameId)
        .success(function (data) {
            $scope.game = data;
            gameStatus = data.status;
        }).error(function (err) {
            alert(err.message);
        });
    }

    function getGameRequirements() {
        $http.get('supersede-dm-app/garp/game/gamerequirements?gameId=' + gameId)
        .success(function (data) {
            requirements = data;

            for (var i = 0; i < requirements.length; i++) {
                var currentRequirement = requirements[i];
                $scope.gameRequirements[currentRequirement.requirementId] = currentRequirement;
            }
        }).error(function (err) {
            alert(err.message);
        });
    }

    function getSolution() {
        $http.get('supersede-dm-app/garp/game/solution?gameId=' + gameId)
        .success(function (data) {
            $scope.solution = data;
        }).error(function (err) {
            alert(err.message);
        });
    }

    getGameInfo();
    getNegotiator();
    getOpinionProviders();
    getGameRequirements();
    getSolution();
});