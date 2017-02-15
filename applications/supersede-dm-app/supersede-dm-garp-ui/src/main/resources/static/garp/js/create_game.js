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
    var gameCriteriaWeightsId = {};
    var gameName;

    var gameRequirements = {
        datatype: "json",
        datafields: [
            { name: 'requirementId' },
            { name: 'name' },
            { name: 'description' }
        ],
        localdata: []
    };

    $scope.gameCriteria = {
        datatype: "json",
        datafields: [
            { name: 'criteriaId' },
            { name: 'name' },
            { name: 'description' }
        ],
        localdata: []
    };

    var gameOpinionProviders = {
        datatype: "json",
        datafields: [
            { name: 'userId' },
            { name: 'name' },
            { name: 'email' }
        ],
        localdata: []
    };

    var gameNegotiators = {
        datatype: "json",
        datafields: [
            { name: 'userId' },
            { name: 'name' },
            { name: 'email' }
        ],
        localdata: []
    };

    var gameCriteriaWeights = {
        datatype: "json",
        datafields: [
            { name: 'criteriaId' },
            { name: 'name' },
            { name: 'description' },
            { name: 'weight' }
        ],
        localdata: []
    };

    function setCurrentPage(page) {
        $scope.currentPage = 'page' + page;
    }

    $scope.defineWeights = function() {
        defineGameData();
        setCurrentPage(2);
    };

    $scope.showSummary = function() {
        defineCriteriaWeights();
        setCurrentPage(3);
        showGameRequirements();
        showGameCriteria();
        showGameOpinionProviders();
        showGameNegotiators();
    };

    function getAvailableRequirements() {
        $http.get('supersede-dm-app/requirement')
        .success(function(data) {
            availableRequirements = {
                datatype: "json",
                datafields: [
                    { name: 'requirementId' },
                    { name: 'name' },
                    { name: 'description' }
                ],
                localdata: data
            };
            var dataAdapter = new $.jqx.dataAdapter(availableRequirements);
            $("#requirements").jqxGrid({
                width: '100%',
                selectionmode: 'checkbox',
                altrows: true,
                autoheight: true,
                pageable: true,
                source: dataAdapter,
                columns: [
                    { text: 'Id', datafield: 'requirementId', width: '15%' },
                    { text: 'Name', datafield: 'name', width: '25%' },
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
                    { name: 'description' }
                ],
                localdata: data
            };
            var dataAdapter = new $.jqx.dataAdapter(availableCriteria);
            $("#criteria").jqxGrid({
                width: '100%',
                selectionmode: 'checkbox',
                altrows: true,
                autoheight: true,
                pageable: true,
                source: dataAdapter,
                columns: [
                    { text: 'Id', datafield: 'criteriaId', width: '15%' },
                    { text: 'Name', datafield: 'name', width: '25%' },
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
                    { name: 'email' }
                ],
                localdata: data
            };
            var dataAdapter1 = new $.jqx.dataAdapter(availablePlayers);
            $("#opinion_providers").jqxGrid({
                width: '100%',
                selectionmode: 'checkbox',
                altrows: true,
                autoheight: true,
                pageable: true,
                source: dataAdapter1,
                columns: [
                    { text: 'Id', datafield: 'userId', width: '20%' },
                    { text: 'Name', datafield: 'name', width: '40%' },
                    { text: 'Email', datafield: 'email' }
                ]
            });
            var dataAdapter2 = new $.jqx.dataAdapter(availablePlayers);
            $("#negotiators").jqxGrid({
                width: '100%',
                selectionmode: 'checkbox',
                altrows: true,
                autoheight: true,
                pageable: true,
                source: dataAdapter2,
                columns: [
                    { text: 'Id', datafield: 'userId', width: '20%' },
                    { text: 'Name', datafield: 'name', width: '40%' },
                    { text: 'Email', datafield: 'email' }
                ]
            });
        });
    }

    function defineGameData() {

        gameName = $("#game_name").val();

        var i;
        var selectedRequirements = $("#requirements").jqxGrid("selectedrowindexes");

        for (i = 0; i < selectedRequirements.length; i++) {
            var selectedRequirement = $("#requirements").jqxGrid('getrowdata', selectedRequirements[i]);
            gameRequirements.localdata.push(selectedRequirement);
            $scope.gameRequirementsId.push(selectedRequirement.requirementId);
        }

        var selectedCriteria = $("#criteria").jqxGrid("selectedrowindexes");
        for (i = 0; i < selectedCriteria.length; i++) {
            var selectedCriterion = $("#criteria").jqxGrid('getrowdata', selectedCriteria[i]);
            $scope.gameCriteria.localdata.push(selectedCriterion);
            $scope.gameCriteriaId.push(selectedCriterion.criteriaId);
        }

        var selectedOpinionProviders = $("#opinion_providers").jqxGrid("selectedrowindexes");
        for (i = 0; i < selectedOpinionProviders.length; i++) {
            var selectedOpinionProvider = $("#opinion_providers").jqxGrid('getrowdata', selectedOpinionProviders[i]);
            gameOpinionProviders.localdata.push(selectedOpinionProvider);
            $scope.gameOpinionProvidersId.push(selectedOpinionProvider.userId);
        }

        var selectedNegotiators = $("#negotiators").jqxGrid("selectedrowindexes");
        for (i = 0; i < selectedNegotiators.length; i++) {
            var selectedNegotiator = $("#negotiators").jqxGrid('getrowdata', selectedNegotiators[i]);
            gameNegotiators.localdata.push(selectedNegotiator);
            $scope.gameNegotiatorsId.push(selectedNegotiator.userId);
        }
    }

    function defineCriteriaWeights() {
        var i;

        for (i = 0; i < $scope.gameCriteria.localdata.length; i++) {
            var currentCriterion = $scope.gameCriteria.localdata[i];
            var criterionValue = $("#criterion" + currentCriterion.criteriaId + " > div").jqxSlider('value');
            gameCriteriaWeights.localdata.push(currentCriterion);
            gameCriteriaWeights.localdata[i].weight = criterionValue;
            gameCriteriaWeightsId[currentCriterion.criteriaId] = criterionValue;
        }
    }

    $scope.create_game = function () {
        $http({
            method: 'POST',
            url: "supersede-dm-app/garp/game/newgame",
            data: gameCriteriaWeightsId,
            params: {name: gameName, gameRequirements: $scope.gameRequirementsId, gameOpinionProviders: $scope.gameOpinionProvidersId,
                gameNegotiators: $scope.gameNegotiatorsId}
        })
        .success(function(data) {
            console.log("Game created successfully");
        }).error(function(err, data){
            console.log(err);
            console.log(data);
        });

        $location.url('supersede-dm-app/garp/home');
    };

    function showGameRequirements() {
        var dataAdapter = new $.jqx.dataAdapter(gameRequirements);
        $("#game_requirements").jqxGrid({
            width: '100%',
            altrows: true,
            autoheight: true,
            pageable: true,
            source: dataAdapter,
            columns: [
                { text: 'Id', datafield: 'requirementId', width: '15%' },
                { text: 'Name', datafield: 'name', width: '25%' },
                { text: 'Description', datafield: 'description', width: '60%' }
            ]
        });
    }

    function showGameCriteria() {
        var dataAdapter = new $.jqx.dataAdapter(gameCriteriaWeights);
        $("#game_criteria").jqxGrid({
            width: '100%',
            altrows: true,
            autoheight: true,
            pageable: true,
            source: dataAdapter,
            columns: [
                { text: 'Id', datafield: 'criteriaId', width: '15%' },
                { text: 'Name', datafield: 'name', width: '20%' },
                { text: 'Description', datafield: 'description', widht: '55%' },
                { text: 'Weight', datafield: 'weight', width: '20%' }
            ]
        });
    }

    function showGameOpinionProviders() {
        var dataAdapter = new $.jqx.dataAdapter(gameOpinionProviders);
        $("#game_opinion_providers").jqxGrid({
            width: '100%',
            altrows: true,
            autoheight: true,
            pageable: true,
            source: dataAdapter,
            columns: [
                { text: 'Id', datafield: 'userId', width: '20%' },
                { text: 'Name', datafield: 'name', width: '40%' },
                { text: 'Email', datafield: 'email', width: '40%' }
            ]
        });
    }

    function showGameNegotiators() {
        var dataAdapter = new $.jqx.dataAdapter(gameNegotiators);
        $("#game_negotiators").jqxGrid({
            width: 750,
            altrows: true,
            autoheight: true,
            pageable: true,
            source: dataAdapter,
            columns: [
                { text: 'Id', datafield: 'userId', width: '20%' },
                { text: 'Name', datafield: 'name', width: '40%' },
                { text: 'Email', datafield: 'email', width: '40%' }
            ]
        });
    }

    getAvailableRequirements();
    getAvailableCriteria();
    getAvailablePlayers();
});