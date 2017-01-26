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

app.controllerProvider.register('judge_acts', function($scope, $http, $interval) {

    $scope.Math = Math;
    $scope.requirementsChoices = [];

    $scope.open_acts = [];
    $scope.closed_acts = [];

    $scope.selectedCriteria = undefined;
    $scope.criterias = [];

    $scope.selectedGame = undefined;
    $scope.games = [];

    var criteriasContains = function(criteria)
    {
        for (var i = 0; i < $scope.criterias.length; i++)
        {
            if ($scope.criterias[i].criteriaId == criteria.criteriaId)
            {
                return true;
            }
        }

        return false;
    };

    var gamesContains = function(game)
    {
        for (var i = 0; i < $scope.games.length; i++)
        {
            if ($scope.games[i].gameId == game.gameId)
            {
                return true;
            }
        }

        return false;
    };

    var getActs = function() {
        $http.get('supersede-dm-app/ahprp/judgeact',
            {params: { criteriaId: $scope.selectedCriteria, gameId: $scope.selectedGame, gameNotFinished: true }}).success(function(data)
        {
            $scope.closed_acts.length = 0;
            $scope.open_acts.length = 0;

            for (var i = 0; i < data.length; i++)
            {
                if (data[i].voted)
                {
                    $scope.closed_acts.push(data[i]);
                }
                else
                {
                    $scope.open_acts.push(data[i]);
                }
            }

            if (!$scope.selectedCriteria)
            {
                $scope.criterias.length = 0;

                for (var j = 0; j < data.length; j++)
                {
                    if (!criteriasContains(data[j].requirementsMatrixData.criteria))
                    {
                        $scope.criterias.push(data[j].requirementsMatrixData.criteria);
                    }
                }
            }

            if (!$scope.selectedGame)
            {
                $scope.games.length = 0;

                for (var k = 0; k < data.length; k++)
                {
                    if (!gamesContains(data[k].requirementsMatrixData.game))
                    {
                        $scope.games.push(data[k].requirementsMatrixData.game);
                    }
                }
            }

            // prepare the open data
            var sourceOpen =
            {
                datatype: "json",
                datafields: [
                    { name: 'criteriaName', map: 'requirementsMatrixData>criteria>name'},
                    { name: 'firstRequirementName', map: 'requirementsMatrixData>rowRequirement>name'},
                    { name: 'judgeActId'},
                    { name: 'secondRequirementName', map: 'requirementsMatrixData>columnRequirement>name'},
                    { name: 'judgeActId2', map: 'judgeActId'}
                ],
                id: 'judgeActId',
                localdata: $scope.open_acts
            };
            var dataAdapterOpen = new $.jqx.dataAdapter(sourceOpen);
            $scope.openSettings =
            {
                width: '100%',
                autoheight: true,
                pageable: true,
                source: dataAdapterOpen,
                rowsheight: 39,
                columns: [
                    { text: 'Comparison Criteria', width: '30%', datafield: 'criteriaName' },
                    { text: 'First Requirement', width: '16%', datafield: 'firstRequirementName' },
                    { text: 'Select your vote', width: '31%', datafield: 'judgeActId', cellsRenderer: function (row, columnDataField, value) {
                        var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
                        r = r + "<div align='center'>" +
                                "<div style='display: inline-block; margin-left: 2px; margin-right: 2px;'" +
                                " ng-repeat='requirementsChoice in requirementsChoices' align='center'>" +
                                "<div align='center'>" +
                                "<label>{{requirementsChoice.label}}</label>" +
                                "</div>" +
                                "<div align=center>" +
                                "<input title='{{requirementsChoice.description}}' type='radio' ng-click='setVote(requirementsChoice.value, " +
                                value + ")' ng-value='{{requirementsChoice.value}}' name='" + value +"'>" +
                                "</div>" +
                                "</div>" +
                                "</div>";
                        return r.concat("</div>");
                        }
                    },
                    { text: 'Second Requirement', width: '16%', datafield: 'secondRequirementName' },
                    { text: '', width: '7%', datafield: 'judgeActId2', cellsRenderer: function (row, columnDataField, value) {
                        var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
                        r = r.concat("<jqx-link-button jqx-width='55' jqx-height='25'><a href='#/supersede-dm-app/ahprp/judge_act?judgeActId=" +
                        value + "'>Decide</a></jqx-link-button>");
                        return r.concat("</div>");
                        }
                    }
                ]
            };

            $scope.createWidgetOpen = true;

            // prepare the closed data
            var sourceClosed =
            {
                datatype: "json",
                datafields: [
                    { name: 'criteriaName', map: 'requirementsMatrixData>criteria>name'},
                    { name: 'firstRequirementName', map: 'requirementsMatrixData>rowRequirement>name'},
                    { name: 'requirementVote', map: 'requirementsMatrixData>value'},
                    { name: 'secondRequirementName', map: 'requirementsMatrixData>columnRequirement>name'}
                ],
                id: 'judgeActId',
                localdata: $scope.closed_acts
            };
            var dataAdapterClosed = new $.jqx.dataAdapter(sourceClosed);
            $scope.closedSettings =
            {
                width: '100%',
                autoheight: true,
                pageable: true,
                source: dataAdapterClosed,
                rowsheight: 31,
                columns: [
                    { text: 'Comparison Criteria', width: '40%', datafield: 'criteriaName' },
                    { text: 'First Requirement', width: '25%', datafield: 'firstRequirementName' },
                    { text: 'Your vote', width: '10%', datafield: 'requirementVote' },
                    { text: 'Second Requirement', width: '25%', datafield: 'secondRequirementName' }
                ]
            };

            $scope.createWidgetClosed = true;

            $('#jqxTabs').jqxTabs({ width: '100%' });
            $('#unorderedList').css('visibility', 'visible');
        });
    };

    getActs();

    $http.get('supersede-dm-app/requirementchoice').success(function(data)
    {
        $scope.requirementsChoices.length = 0;
        for (var i = 0; i < data.length; i++)
        {
            $scope.requirementsChoices.push(data[i]);
        }
    });

    $scope.setVote = function(judgeVote, judgeActId){
        $http.put('supersede-dm-app/ahprp/judgeact/' + judgeActId + '/vote/' + judgeVote).success(function(data)
        {
            getActs();
        });
    };


    $scope.changeCriteria = function() {
        getActs();
    };

    $scope.changeGame = function() {
        getActs();
    };

    // ##################################################################################
    // polling methods (every second)

    $scope.user = undefined;

    $scope.game = undefined;

    $scope.gamePlayerPoints = undefined;

    $scope.agreementIndex = undefined;

    // there are the variables for the different sets of points
    $scope.movesPoints = 0;
    $scope.gameProgressPoints = 0;
    $scope.positionInVotingPoints = 0;
    $scope.gameStatusPoints = 0;
    $scope.gameCompleted = false;

    var getGameData = function()
    {
        $http.get('supersede-dm-app/ahprp/game/' + $scope.selectedGame).success(function(data) {
            $scope.game = data;

            if (data.playerProgress < 100){
                $scope.gameStatusPoints = -20;
                $scope.gameCompleted = false;
            }
            else
            {
                $scope.gameStatusPoints = 0;
                $scope.gameCompleted = true;
            }

            $scope.gameProgressPoints = Math.floor((data.playerProgress / 10));
            $scope.movesPoints = data.movesDone;
        });

        $http.get('supersede-dm-app/user/current')
        .success(function(data) {
            $scope.user = data;
        });

        $http.get('supersede-dm-app/ahprp/gameplayerpoint/game/' + $scope.selectedGame)
        .success(function(data) {
            $scope.gamePlayerPoints = data;

            $scope.agreementIndex = data.agreementIndex / 20.000;

            if (data.positionInVoting == 1){
                $scope.positionInVotingPoints = 5;
            }
            else if (data.positionInVoting == 2){
                $scope.positionInVotingPoints = 3;
            }
            else if (data.positionInVoting == 3){
                $scope.positionInVotingPoints = 2;
            }
        });
    };

    getGameData();

    var update = $interval(function() {
        getGameData();
        }, 5000);

     // stops the interval
    $scope.stop = function() {
        $interval.cancel(update);
    };

    // stops the interval when the scope is destroyed,
    // this usually happens when a route is changed and
    // the ItemsController $scope gets destroyed. The
    // destruction of the ItemsController scope does not
    // guarantee the stopping of any intervals, you must
    // be responsible of stopping it when the scope is
    // is destroyed.
    $scope.$on('$destroy', function() {
        $scope.stop();
    });

    // ##################################################################################

    $scope.hashCode = function(str) {
        var hash = 0;

        for (var i = 0; i < str.length; i++) {
            hash = str.charCodeAt(i) + ((hash << 5) - hash);
        }

        return hash;
    };

    $scope.intToARGB = function(i) {
        var hex = ((i>>24)&0xFF).toString(16) +
                ((i>>16)&0xFF).toString(16) +
                ((i>>8)&0xFF).toString(16) +
                (i&0xFF).toString(16);

        // Sometimes the string returned will be too short so we
        // add zeros to pad it out, which later get removed if
        // the length is greater than six.
        hex += '000000';
        return hex.substring(0, 6);
    };

    $scope.criteriaColor = function(data) {
        return $scope.intToARGB($scope.hashCode(data));
    };
});