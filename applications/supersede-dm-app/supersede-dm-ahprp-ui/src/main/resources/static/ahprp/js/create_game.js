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
	
    $scope.now = function()
    {
        return new Date().toJSON().slice(0,19).replace("T", " ");
    };

    var availableRequirements = {};
    var availableCriteria = {};
    var availablePlayers = {};
    var gameName;
    var gameNegotiator;
    
    $scope.players = [];
    $scope.requirements = [];
    $scope.criterias = [];

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

    $scope.currentPlayer = undefined;
    $scope.currentRequirement= undefined;
    $scope.currentCriteria = undefined;

    $scope.game = {players : [], requirements: [], criterias: [], title: "Decision Making Process " + $scope.now(), negotiator: {} };

    $scope.currentPage = 'page1';

    $scope.requirementsChoices = [];

    $scope.choices = {};

//    $http.get('supersede-dm-app/user?profile=OPINION_PROVIDER')
//    .success(function(data) {
//        for (var i = 0; i < data.length; i++)
//        {
//            $scope.players.push(data[i]);
//        }
//    });
    
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
            $scope.game.requirements.push(selectedRequirement);
        }

        // clear criteria
        $scope.gameCriteria.localdata = [];
        $scope.gameCriteriaId = [];
        $scope.game.criterias = [];
        $scope.choices = {};

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
            var criterion = {};
            criterion.criteriaId = selectedCriterion.sourceId;
            criterion.name = selectedCriterion.name;
            $scope.game.criterias.push(criterion);
//            $scope.choices.push( selectedCriterion.sourceId );
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
            $scope.game.players.push(selectedOpinionProvider);
        }

        return true;
    }


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


    $http.get('supersede-dm-app/requirement')
    .success(function(data) {
//        for (var i = 0; i < data.length; i++)
//        {
//            $scope.requirements.push(data[i]);
//        }

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

//    $http.get('supersede-dm-app/criteria')
    $http.get('supersede-dm-app/processes/criteria/list/detailed?processId=' + $scope.processId)
    .success(function(data) {
//        for (var i = 0; i < data.length; i++)
//        {
//            $scope.criterias.push(data[i]);
//        }

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
    
    });

    $http.get('supersede-dm-app/requirementchoice')
    .success(function(data) {
        $scope.requirementsChoices.length = 0;
        for (var i = 0; i < data.length; i++)
        {
            $scope.requirementsChoices.push(data[i]);
        }
    });

    $scope.toggleSelection = function(array, item)
    {
        var idx = array.indexOf(item);
        if (idx > -1) {
            array.splice(idx, 1);
        }
        else {
            array.push(item);
        }
    };

    $scope.toPage = function(p)
    {
        if (p == 3)
        {
        	if( defineGameData() ) {
//            if ($scope.game.players.length > 0 &&
//                    $scope.game.requirements.length > 1 &&
//                    $scope.game.criterias.length > 1)
//            {
                $scope.currentPage = 'page3';
            }
        }
        else
        {
            $scope.currentPage = 'page' + p;
        }
    };

    $scope.createGame = function()
    {
    	console.log( $scope );
    	for (var key in $scope.choices ) {
    		console.log( key );
    		for( var subkey in $scope.choices[key] ) {
//        	for (var j = 0; j < $scope.choices[i].length; j++) {
        		console.log( subkey + " => " + $scope.choices[key][subkey] );
        	}
    	}
    	console.log( JSON.stringify( $scope.choices ) );
//    	return;
        $http({
            url: "supersede-dm-app/ahprp/game",
            data: $scope.game,
            method: 'POST',
            params: {criteriaValues : $scope.choices, processId: $scope.processId }
        }).success(function(data){
            $scope.game = {
            		players : [], 
            		requirements: [], 
            		criterias: [], 
            		title: "Decision Making Process " + $scope.now()
            	};
            $scope.choices = {};
            $scope.currentPage = 'page1';
            $location.url('supersede-dm-app/ahprp/game_page').search('gameId', data).search( 'processId',$scope.processId);
        }).error(function(err){
            alert(err.message);
        });
    };
});