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
	
	$scope.processId = $location.search().processId;
	
    $scope.currentPage = "page1";
    $scope.gameRequirementsId = [];
    $scope.gameCriteriaId = [];
    $scope.gameOpinionProvidersId = [];

    $scope.now = function()
    {
        return new Date().toJSON().slice(0,19).replace("T", " ");
    };

    var availableRequirements = {};
    var availableCriteria = {};
    var availablePlayers = {};
    var weights = {};
    var weightsId = {};
    var gameName;
    var gameNegotiator;

    $scope.game = {title: "Decision Making Process " + $scope.now()};
    
    weightsId.players = {};
    weightsId.criteria = {};

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
            { name: 'sourceId' },
            { name: 'name' },
            { name: 'description' }
        ],
        localdata: []
    };

    $scope.gameOpinionProviders = {
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

    weights.players = {
        datatype: "json",
        datafields: [
            { name: 'userId' },
            { name: 'name' },
            { name: 'email' },
            { name: 'sourceId'},
            { name: 'weight' }
        ],
        localdata: []
    };

    weights.criteria = {
        datatype: "json",
        datafields: [
            { name: 'sourceId' },
            { name: 'name' },
            { name: 'description' },
            { name: 'weight' }
        ],
        localdata: []
    };

    function setCurrentPage(page) {
        $scope.currentPage = 'page' + page;
    }

    function getAvailableRequirements() {
        $http.get('supersede-dm-app/processes/requirements/list?processId=' + $scope.processId )
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
                autorowheight: true,
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
        $http.get('supersede-dm-app/processes/criteria/list/detailed?processId=' + $scope.processId)
        .success(function(data) {
            availableCriteria = {
                datatype: "json",
                datafields: [
                    { name: 'sourceId' },
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
                autorowheight: true,
                pageable: true,
                source: dataAdapter,
                columns: [
                    { text: 'Id', datafield: 'sourceId', width: '15%' },
                    { text: 'Name', datafield: 'name', width: '25%' },
                    { text: 'Description', datafield: 'description' }
                ]
            });
        }).error(function (err) {
            alert(err.message);
        });
    }

    function getAvailablePlayers() {
        $http.get('supersede-dm-app/processes/users/list/detailed?processId=' + $scope.processId)
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
            $("#negotiator").jqxGrid({
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
        }).error(function (err) {
            alert(err.message);
        });
    }

    function defineGameData() {

        gameName = $("#game_name").val();
        var i;

        // clear requirements
        gameRequirements.localdata = [];
        $scope.gameRequirementsId = [];

        // fill requirements
        var selectedRequirements = $("#requirements").jqxGrid("selectedrowindexes");

        if (selectedRequirements.length < 2) {
            alert('You must select at least two requirements');
            return false;
        }

        for (i = 0; i < selectedRequirements.length; i++) {
            var selectedRequirement = $("#requirements").jqxGrid('getrowdata', selectedRequirements[i]);
            gameRequirements.localdata.push(selectedRequirement);
            $scope.gameRequirementsId.push(selectedRequirement.requirementId);
        }

        // clear criteria
        $scope.gameCriteria.localdata = [];
        $scope.gameCriteriaId = [];

        // fill criteria
        var selectedCriteria = $("#criteria").jqxGrid("selectedrowindexes");

        if (selectedCriteria.length === 0) {
            alert('You must select at least one criterion');
            return false;
        }

        for (i = 0; i < selectedCriteria.length; i++) {
            var selectedCriterion = $("#criteria").jqxGrid('getrowdata', selectedCriteria[i]);
            $scope.gameCriteria.localdata.push(selectedCriterion);
            $scope.gameCriteriaId.push(selectedCriterion.sourceId);
        }

        // clear negotiator
        gameNegotiators.localdata = [];
        gameNegotiator = undefined;

        // fill negotiator
        var selectedNegotiators = $("#negotiator").jqxGrid("selectedrowindexes");

        if (selectedNegotiators.length !== 1) {
            alert("You must select exactly one negotiator");
            return false;
        }
        else {
            gameNegotiator = $("#negotiator").jqxGrid('getrowdata', selectedNegotiators[0]);
            gameNegotiators.localdata.push(gameNegotiator);
        }

        // clear opinion providers
        $scope.gameOpinionProviders.localdata = [];
        $scope.gameOpinionProvidersId = [];

        // fill opinion providers
        var selectedOpinionProviders = $("#opinion_providers").jqxGrid("selectedrowindexes");

        if (selectedOpinionProviders.length === 0) {
            alert('You must select at least one opinion provider');
            return false;
        }

        for (i = 0; i < selectedOpinionProviders.length; i++) {
            var selectedOpinionProvider = $("#opinion_providers").jqxGrid('getrowdata', selectedOpinionProviders[i]);
            $scope.gameOpinionProviders.localdata.push(selectedOpinionProvider);
            $scope.gameOpinionProvidersId.push(selectedOpinionProvider.userId);
        }

        return true;
    }

    function setPlayersWeights() {
        for (var i = 0; i < $scope.gameCriteria.localdata.length; i++) {
            var currentCriterion = $scope.gameCriteria.localdata[i];
            weightsId.players[currentCriterion.sourceId] = {};

            for (var j = 0; j < $scope.gameOpinionProviders.localdata.length; j++) {
                var currentOpinionProvider = $scope.gameOpinionProviders.localdata[j];
                var opinionProviderValue = $("#criterion" + currentCriterion.sourceId + "player" + currentOpinionProvider.userId + " > div").jqxSlider('value');
                weights.players.localdata.push(currentOpinionProvider);
                weights.players.localdata[i].sourceId = currentCriterion.sourceId;
                weights.players.localdata[i].weight = opinionProviderValue;
                weightsId.players[currentCriterion.sourceId][currentOpinionProvider.userId] = opinionProviderValue;
            }
        }
    }

    function setCriteriaWeights() {
        for (var i = 0; i < $scope.gameCriteria.localdata.length; i++) {
            var currentCriterion = $scope.gameCriteria.localdata[i];
            var criterionValue = $("#criterion" + currentCriterion.sourceId + " > div").jqxSlider('value');
            weights.criteria.localdata.push(currentCriterion);
            weights.criteria.localdata[i].weight = criterionValue;
            weightsId.criteria[currentCriterion.sourceId] = criterionValue;
        }
    }

    function showGameRequirements() {
        var dataAdapter = new $.jqx.dataAdapter(gameRequirements);
        $("#game_requirements").jqxGrid({
            width: '100%',
            altrows: true,
            autoheight: true,
            autorowheight: true,
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
        var dataAdapter = new $.jqx.dataAdapter(weights.criteria);
        $("#game_criteria").jqxGrid({
            width: '100%',
            altrows: true,
            autoheight: true,
            autorowheight: true,
            pageable: true,
            source: dataAdapter,
            columns: [
                { text: 'Id', datafield: 'sourceId', width: '15%' },
                { text: 'Name', datafield: 'name', width: '20%' },
                { text: 'Description', datafield: 'description', widht: '55%' },
                { text: 'Weight', datafield: 'weight', width: '20%' }
            ]
        });
    }

    function showGameNegotiator() {
        var dataAdapter = new $.jqx.dataAdapter(gameNegotiators);
        $("#game_negotiator").jqxGrid({
            width: '100%',
            altrows: true,
            autoheight: true,
            source: dataAdapter,
            columns: [
                { text: 'Id', datafield: 'userId', width: '20%' },
                { text: 'Name', datafield: 'name', width: '40%' },
                { text: 'Email', datafield: 'email', width: '40%' }
            ]
        });
    }

    function showGameOpinionProviders() {
        var dataAdapter = new $.jqx.dataAdapter($scope.gameOpinionProviders);
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

    $scope.definePlayersWeights = function() {
        if (defineGameData()) {
            setCurrentPage(2);
        }
    };

    $scope.defineCriteriaWeights = function () {
        setPlayersWeights();
        setCurrentPage(3);
    };

    $scope.showSummary = function() {
        setCriteriaWeights();
        setCurrentPage(4);
        showGameRequirements();
        showGameCriteria();
        showGameOpinionProviders();
        showGameNegotiator();
    };

    $scope.create_game = function () {
        $http({
            method: 'POST',
            url: "supersede-dm-app/garp/game/newgame",
            data: weightsId,
            params: {
            	name: gameName, 
            	gameRequirements: $scope.gameRequirementsId, 
            	gameOpinionProviders: $scope.gameOpinionProvidersId,
                gameNegotiator: gameNegotiator.userId,
                processId: $scope.processId }
        })
        .success(function(data) {
            $("#game_created").html("<strong>Game successfully created!</strong>");
            $location.url('supersede-dm-app/garp/home').search('processId',$scope.processId);
        }).error(function(err) {
            $("#game_created").html("<strong>Unable to create the game: " + err.message + "</strong>");
        });
    };

    $scope.home = function() {
        $location.url('supersede-dm-app/garp/home').search('processId',$scope.processId);
    };

    getAvailableRequirements();
    getAvailableCriteria();
    getAvailablePlayers();
});