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

app.controllerProvider.register('import_requirements', function($scope, $http, $location) {

	$scope.procId = $location.search().procId;
	
    $scope.gameRequirementsId = [];

    var availableRequirements = {};

    var gameRequirements = {
        datatype: "json",
        datafields: [
            { name: 'requirementId' },
            { name: 'name' },
            { name: 'description' }
        ],
        localdata: []
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

    function defineGameData() {

        gameName = $("#game_name").val();

        var i;
        var selectedRequirements = $("#requirements").jqxGrid("selectedrowindexes");

        for (i = 0; i < selectedRequirements.length; i++) {
            var selectedRequirement = $("#requirements").jqxGrid('getrowdata', selectedRequirements[i]);
            gameRequirements.localdata.push(selectedRequirement);
            $scope.gameRequirementsId.push(selectedRequirement.requirementId);
        }

    }

    $scope.done = function () {
    	defineGameData();
    	console.log($scope.gameRequirementsId);
        $http({
            method: 'POST',
            url: "supersede-dm-app/processes/requirements/import",
//            data: weightsId,
            params: { procId: $scope.procId, requirementsId: $scope.gameRequirementsId }
        })
        .success(function(data) {
        	$location.url('supersede-dm-app/process?procId='+$scope.procId);
//            $("#game_created").html("<strong>Game successfully created!</strong>");
        }).error(function(err, data){
//            $("#game_created").html("<strong>Unable to create the game!</strong>");
            console.log(err);
            console.log(data);
        });
    };

    $scope.home = function() {
        $location.url('supersede-dm-app/home');
    };

    getAvailableRequirements();
//    getAvailableCriteria();
//    getAvailablePlayers();
});

app.controllerProvider.register('import_users', function($scope, $http, $location) {

	$scope.procId = $location.search().procId;
	
	console.log( $scope.procId );
	
    $scope.gameOpinionProvidersId = [];

    var availablePlayers = {};

    $scope.gameOpinionProviders = {
        datatype: "json",
        datafields: [
            { name: 'userId' },
            { name: 'name' },
            { name: 'email' }
        ],
        localdata: []
    };

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
            $("#users").jqxGrid({
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
        });
    }
    
    function defineGameData() {

        var i;
        
        var selectedOpinionProviders = $("#users").jqxGrid("selectedrowindexes");
        for (i = 0; i < selectedOpinionProviders.length; i++) {
            var selectedOpinionProvider = $("#users").jqxGrid('getrowdata', selectedOpinionProviders[i]);
            $scope.gameOpinionProviders.localdata.push(selectedOpinionProvider);
            $scope.gameOpinionProvidersId.push(selectedOpinionProvider.userId);
        }
        
        console.log("selected opinion providers:");
        console.log($scope.gameOpinionProvidersId);
//        console.log($scope.gameOpinionProviders.localdata);
        

    }
    
    $scope.done = function () {
        console.log( $scope.procId );
    	defineGameData();
        $http({
            method: 'POST',
            url: "supersede-dm-app/processes/users/import",
            params: { procId: $scope.procId, idlist: $scope.gameOpinionProvidersId }
        })
        .success(function(data) {
//            $("#game_created").html("<strong>Game successfully created!</strong>");
        }).error(function(err, data){
//            $("#game_created").html("<strong>Unable to create the game!</strong>");
            console.log(err);
            console.log(data);
        });
    };

    $scope.home = function() {
        $location.url('supersede-dm-app/process?procId=' + $scope.procId );
    };

//    getAvailableRequirements();
//    getAvailableCriteria();
    getAvailablePlayers();
});

app.controllerProvider.register('import_criteria', function($scope, $http, $location) {

	$scope.procId = $location.search().procId;
	
    $scope.gameCriteriaId = [];

    var availableCriteria = {};

    $scope.gameCriteria = {
        datatype: "json",
        datafields: [
            { name: 'criteriaId' },
            { name: 'name' },
            { name: 'description' }
        ],
        localdata: []
    };

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

    function defineGameData() {

        gameName = $("#game_name").val();

        var i;
        
        var selectedCriteria = $("#criteria").jqxGrid("selectedrowindexes");
        for (i = 0; i < selectedCriteria.length; i++) {
            var selectedCriterion = $("#criteria").jqxGrid('getrowdata', selectedCriteria[i]);
            $scope.gameCriteria.localdata.push(selectedCriterion);
            $scope.gameCriteriaId.push(selectedCriterion.criteriaId);
        }

    }
    
    $scope.done = function () {
    	defineGameData();
        $http({
            method: 'POST',
            url: "supersede-dm-app/processes/criteria/import",
            params: { procId: $scope.procId, idlist: $scope.gameCriteriaId }
        })
        .success(function(data) {
//            $("#game_created").html("<strong>Game successfully created!</strong>");
        }).error(function(err, data){
//            $("#game_created").html("<strong>Unable to create the game!</strong>");
            console.log(err);
            console.log(data);
        });
    };

    $scope.home = function() {
        $location.url('supersede-dm-app/home');
    };

    getAvailableCriteria();
});

app.controllerProvider.register('imports', function($scope, $http, $location) {

    $scope.currentPage = "page1";
    $scope.gameRequirementsId = [];
    $scope.gameCriteriaId = [];
    $scope.gameOpinionProvidersId = [];
    $scope.gameNegotiatorsId = [];

    var availableRequirements = {};
    var availableCriteria = {};
    var availablePlayers = {};
    var weights = {};
    var weightsId = {};
    var gameName;

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
            { name: 'criteriaId' },
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
            { name: 'criterionId'},
            { name: 'weight' }
        ],
        localdata: []
    };

    weights.criteria = {
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
            $scope.gameOpinionProviders.localdata.push(selectedOpinionProvider);
            $scope.gameOpinionProvidersId.push(selectedOpinionProvider.userId);
        }

        console.log("selected opinion providers:");
        console.log($scope.gameOpinionProviders.localdata);

        var selectedNegotiators = $("#negotiators").jqxGrid("selectedrowindexes");
        for (i = 0; i < selectedNegotiators.length; i++) {
            var selectedNegotiator = $("#negotiators").jqxGrid('getrowdata', selectedNegotiators[i]);
            gameNegotiators.localdata.push(selectedNegotiator);
            $scope.gameNegotiatorsId.push(selectedNegotiator.userId);
        }
    }

    function setPlayersWeights() {
        console.log("Set player weights");
        for (var i = 0; i < $scope.gameCriteria.localdata.length; i++) {
            var currentCriterion = $scope.gameCriteria.localdata[i];
            weightsId.players[currentCriterion.criteriaId] = {};
            console.log(weightsId);
            for (var j = 0; j < $scope.gameOpinionProviders.localdata.length; j++) {
                var currentOpinionProvider = $scope.gameOpinionProviders.localdata[j];
                var opinionProviderValue = $("#criterion" + currentCriterion.criteriaId + "player" + currentOpinionProvider.userId + " > div").jqxSlider('value');
                weights.players.localdata.push(currentOpinionProvider);
                weights.players.localdata[i].criterionId = currentCriterion.criteriaId;
                weights.players.localdata[i].weight = opinionProviderValue;
                weightsId.players[currentCriterion.criteriaId][currentOpinionProvider.userId] = opinionProviderValue;
                console.log(weightsId);
            }
        }
    }

    function setCriteriaWeights() {
        console.log("Set criteria weights");
        for (var i = 0; i < $scope.gameCriteria.localdata.length; i++) {
            var currentCriterion = $scope.gameCriteria.localdata[i];
            var criterionValue = $("#criterion" + currentCriterion.criteriaId + " > div").jqxSlider('value');
            console.log(currentCriterion.criteriaId + " = " + criterionValue);
            console.log("criterion value:");
            console.log(criterionValue);
            weights.criteria.localdata.push(currentCriterion);
            weights.criteria.localdata[i].weight = criterionValue;
            weightsId.criteria[currentCriterion.criteriaId] = criterionValue;
            console.log(weightsId);
        }
    }

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
        var dataAdapter = new $.jqx.dataAdapter(weights.criteria);
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

    $scope.definePlayersWeights = function() {
        defineGameData();
        setCurrentPage(2);
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
        showGameNegotiators();
    };

    $scope.create_game = function () {
        console.log("sending weights:");
        console.log(weightsId);
        $http({
            method: 'POST',
            url: "supersede-dm-app/garp/game/newgame",
            data: weightsId,
            params: {name: gameName, gameRequirements: $scope.gameRequirementsId, gameOpinionProviders: $scope.gameOpinionProvidersId,
                gameNegotiators: $scope.gameNegotiatorsId}
        })
        .success(function(data) {
            $("#game_created").html("<strong>Game successfully created!</strong>");
        }).error(function(err, data){
            $("#game_created").html("<strong>Unable to create the game!</strong>");
            console.log(err);
            console.log(data);
        });
    };

    $scope.home = function() {
        $location.url('supersede-dm-app/garp/home');
    };

    getAvailableRequirements();
    getAvailableCriteria();
    getAvailablePlayers();
});
