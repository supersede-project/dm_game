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

app.controllerProvider.register('create_game', function($scope, $http, $location) {

    $scope.currentPage = "page1";

    $scope.to_page = function(page) {
        $scope.currentPage = 'page' + page;
        console.log("current page: ");
        console.log($scope.currentPage);
    };

    $http.get('supersede-dm-app/requirement')
    .success(function(data) {
        var source = {
            datatype: "json",
            datafields: [
                { name: 'requirementId' },
                { name: 'name' },
                { name : 'description' }
            ],
            localdata: data
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $("#requirements").jqxGrid({
            width: 750,
            selectionmode: 'checkbox',
            altrows: true,
            autoheight: true,
            source: dataAdapter,
            columns: [
                { text: 'Id', datafield: 'requirementId', width: 100 },
                { text: 'Name', datafield: 'name', width: 300 },
                { text: 'Description', datafield: 'description' }
            ]
        });
    });

    $http.get('supersede-dm-app/criteria')
    .success(function(data) {
        var source = {
            datatype: "json",
            datafields: [
                { name: 'criteriaId' },
                { name: 'name' },
                { name : 'description' }
            ],
            localdata: data
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $("#criteria").jqxGrid({
            width: 750,
            selectionmode: 'checkbox',
            altrows: true,
            autoheight: true,
            source: dataAdapter,
            columns: [
                { text: 'Id', datafield: 'criteriaId', width: 100 },
                { text: 'Name', datafield: 'name', width: 300 },
                { text: 'Description', datafield: 'description' }
            ]
        });
    });

    $http.get('supersede-dm-app/user?profile=OPINION_PROVIDER')
    .success(function(data) {
        var source = {
            datatype: "json",
            datafields: [
                { name: 'userId' },
                { name: 'name' },
                { name : 'email' }
            ],
            localdata: data
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $("#players").jqxGrid({
            width: 750,
            selectionmode: 'checkbox',
            altrows: true,
            autoheight: true,
            source: dataAdapter,
            columns: [
                { text: 'Id', datafield: 'userId', width: 100 },
                { text: 'Name', datafield: 'name', width: 300 },
                { text: 'Email', datafield: 'email' }
            ]
        });
    });

    $scope.create_game = function () {
        var selectedRequirements = $("#requirements").jqxGrid("selectedrowindexes");
        var gameRequirements = [];
        var i;
        for (i = 0; i < selectedRequirements.length; i++) {
            var selectedRequirement = $("#requirements").jqxGrid('getrowdata', selectedRequirements[i]).requirementId;
            gameRequirements.push(selectedRequirement);
        }

        var selectedCriteria = $("#criteria").jqxGrid("selectedrowindexes");
        var gameCriteria = [];
        for (i = 0; i < selectedCriteria.length; i++) {
            var selectedCriterion = $("#criteria").jqxGrid('getrowdata', selectedCriteria[i]).criteriaId;
            gameCriteria.push(selectedCriterion);
        }

        var selectedPlayers = $("#players").jqxGrid("selectedrowindexes");
        var gamePlayers = [];
        for (i = 0; i < selectedPlayers.length; i++) {
            var selectedPlayer = $("#players").jqxGrid('getrowdata', selectedPlayers[i]).userId;
            gamePlayers.push(selectedPlayer);
        }

        var gameCriteriaWeights = {};
        for (i = 0; i < gameCriteria.length; i++) {
            gameCriteriaWeights[gameCriteria[i]] = 1.0;
        }

        console.log("gameCriteriaWeights:");
        console.log(gameCriteriaWeights);

        $http({
            method: 'POST',
            url: "supersede-dm-app/garp/game/newgame",
            data: gameCriteriaWeights,
            params: {gameRequirements: gameRequirements, gamePlayers: gamePlayers}
        })
        .success(function(data) {
            console.log("success sending data:");
            console.log(data);
        }).error(function(err, data){
            console.log(err);
            console.log(data);
        });

        $location.url('supersede-dm-app/garp/home');
    };
});