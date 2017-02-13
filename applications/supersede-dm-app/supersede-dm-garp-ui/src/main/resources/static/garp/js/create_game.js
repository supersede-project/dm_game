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

    $scope.gameRequirementsId = [];
    $scope.gameCriteriaId = [];
    $scope.gameOpinionProvidersId = [];
    $scope.gameNegotiatorsId = [];

    var availableRequirements = {};
    var availableCriteria = {};
    var availablePlayers = {};

    var gameRequirements = [];
    $scope.gameCriteria = [];
    var gameOpinionProviders = [];
    var gameNegotiators = [];

    $scope.to_page = function(page) {
        $scope.currentPage = 'page' + page;
        console.log("current page: ");
        console.log($scope.currentPage);

        if (page == "2") {
            defineGameData();
        }
        else if (page == "3") {
            showGameRequirements();
            showGameCriteria();
            showGameOpinionProviders();
            showGameNegotiators();
        }
    };

    function getAvailableRequirements() {
        $http.get('supersede-dm-app/requirement')
        .success(function(data) {
            availableRequirements = {
                datatype: "json",
                datafields: [
                    { name: 'requirementId' },
                    { name: 'name' },
                    { name : 'description' }
                ],
                localdata: data
            };
            console.log("available requirements:");
            console.log(availableRequirements);
            var dataAdapter = new $.jqx.dataAdapter(availableRequirements);
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
    }

    function getAvailableCriteria() {
        $http.get('supersede-dm-app/criteria')
        .success(function(data) {
            availableCriteria = {
                datatype: "json",
                datafields: [
                    { name: 'criteriaId' },
                    { name: 'name' },
                    { name : 'description' }
                ],
                localdata: data
            };
            var dataAdapter = new $.jqx.dataAdapter(availableCriteria);
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
    }

    function getAvailablePlayers() {
        $http.get('supersede-dm-app/user?profile=OPINION_PROVIDER')
        .success(function(data) {
            availablePlayers = {
                datatype: "json",
                datafields: [
                    { name: 'userId' },
                    { name: 'name' },
                    { name : 'email' }
                ],
                localdata: data
            };
            var dataAdapter1 = new $.jqx.dataAdapter(availablePlayers);
            $("#opinion_providers").jqxGrid({
                width: 750,
                selectionmode: 'checkbox',
                altrows: true,
                autoheight: true,
                source: dataAdapter1,
                columns: [
                    { text: 'Id', datafield: 'userId', width: 100 },
                    { text: 'Name', datafield: 'name', width: 300 },
                    { text: 'Email', datafield: 'email' }
                ]
            });
            var dataAdapter2 = new $.jqx.dataAdapter(availablePlayers);
            $("#negotiators").jqxGrid({
                width: 750,
                selectionmode: 'checkbox',
                altrows: true,
                autoheight: true,
                source: dataAdapter2,
                columns: [
                    { text: 'Id', datafield: 'userId', width: 100 },
                    { text: 'Name', datafield: 'name', width: 300 },
                    { text: 'Email', datafield: 'email' }
                ]
            });
        });
    }

    function defineGameData() {
        gameRequirements = {
            datatype: "json",
            datafields: [
                { name: 'requirementId' },
                { name: 'name' },
                { name : 'description' }
            ],
            localdata: []
        };

        var i;
        var selectedRequirements = $("#requirements").jqxGrid("selectedrowindexes");

        for (i = 0; i < selectedRequirements.length; i++) {
            var selectedRequirement = $("#requirements").jqxGrid('getrowdata', selectedRequirements[i]);
            gameRequirements.localdata.push(selectedRequirement);
            $scope.gameRequirementsId.push(selectedRequirement.requirementId);
        }

        $scope.gameCriteria = {
            datatype: "json",
            datafields: [
                { name: 'criteriaId' },
                { name: 'name' },
                { name : 'description' }
            ],
            localdata: []
        };

        var selectedCriteria = $("#criteria").jqxGrid("selectedrowindexes");
        for (i = 0; i < selectedCriteria.length; i++) {
            var selectedCriterion = $("#criteria").jqxGrid('getrowdata', selectedCriteria[i]);
            $scope.gameCriteria.localdata.push(selectedCriterion);
            $scope.gameCriteriaId.push(selectedCriterion.criteriaId);
        }

        gameOpinionProviders = {
            datatype: "json",
            datafields: [
                { name: 'userId' },
                { name: 'name' },
                { name : 'email' }
            ],
            localdata: []
        };

        var selectedOpinionProviders = $("#opinion_providers").jqxGrid("selectedrowindexes");
        for (i = 0; i < selectedOpinionProviders.length; i++) {
            var selectedOpinionProvider = $("#opinion_providers").jqxGrid('getrowdata', selectedOpinionProviders[i]);
            gameOpinionProviders.localdata.push(selectedOpinionProvider);
            $scope.gameOpinionProvidersId.push(selectedOpinionProvider.userId);
        }

        gameNegotiators = {
            datatype: "json",
            datafields: [
                { name: 'userId' },
                { name: 'name' },
                { name : 'email' }
            ],
            localdata: []
        };

        var selectedNegotiators = $("#negotiators").jqxGrid("selectedrowindexes");
        for (i = 0; i < selectedNegotiators.length; i++) {
            var selectedNegotiator = $("#negotiators").jqxGrid('getrowdata', selectedNegotiators[i]);
            gameNegotiators.localdata.push(selectedNegotiator);
            $scope.gameNegotiatorsId.push(selectedNegotiator.userId);
        }
    }

    $scope.create_game = function () {
        var gameCriteriaWeights = {};
        var i;

        for (i = 0; i < $scope.gameCriteria.length; i++) {
            var criterionValue = $("#criterion" + $scope.gameCriteria[i] + " > div").jqxSlider('value');
            gameCriteriaWeights[$scope.gameCriteria[i]] = criterionValue;
        }

        console.log("gameCriteriaWeights:");
        console.log(gameCriteriaWeights);

        $http({
            method: 'POST',
            url: "supersede-dm-app/garp/game/newgame",
            data: gameCriteriaWeights,
            params: {gameRequirements: $scope.gameRequirementsId, gameOpinionProviders: $scope.gameOpinionProvidersId,
                gameNegotiators: $scope.gameNegotiatorsId}
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

    function showGameRequirements() {
        console.log("game requirements:");
        console.log(gameRequirements);
        var dataAdapter = new $.jqx.dataAdapter(gameRequirements);
        $("#game_requirements").jqxGrid({
            width: 750,
            altrows: true,
            autoheight: true,
            source: dataAdapter,
            columns: [
                { text: 'Id', datafield: 'requirementId', width: 100 },
                { text: 'Name', datafield: 'name', width: 300 },
                { text: 'Description', datafield: 'description' }
            ]
        });
    }

    function showGameCriteria() {
        console.log("game criteria: ");
        console.log($scope.gameCriteria);
        var dataAdapter = new $.jqx.dataAdapter($scope.gameCriteria);
        $("#game_criteria").jqxGrid({
            width: 750,
            altrows: true,
            autoheight: true,
            source: dataAdapter,
            columns: [
                { text: 'Id', datafield: 'criteriaId', width: 100 },
                { text: 'Name', datafield: 'name', width: 300 },
                { text: 'Description', datafield: 'description' }
            ]
        });
    }

    function showGameOpinionProviders() {
        var dataAdapter = new $.jqx.dataAdapter(gameOpinionProviders);
        $("#game_opinion_providers").jqxGrid({
            width: 750,
            altrows: true,
            autoheight: true,
            source: dataAdapter,
            columns: [
                { text: 'Id', datafield: 'userId', width: 100 },
                { text: 'Name', datafield: 'name', width: 300 },
                { text: 'Email', datafield: 'email' }
            ]
        });
    }

    function showGameNegotiators() {
        var dataAdapter = new $.jqx.dataAdapter(gameNegotiators);
        $("#game_negotiators").jqxGrid({
            width: 750,
            altrows: true,
            autoheight: true,
            source: dataAdapter,
            columns: [
                { text: 'Id', datafield: 'userId', width: 100 },
                { text: 'Name', datafield: 'name', width: 300 },
                { text: 'Email', datafield: 'email' }
            ]
        });
    }

    getAvailableRequirements();
    getAvailableCriteria();
    getAvailablePlayers();
});