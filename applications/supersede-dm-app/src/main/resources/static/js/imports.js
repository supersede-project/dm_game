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

    $scope.processId = $location.search().processId;
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
        // Get all the requirements that are not enacted
        $http.get('supersede-dm-app/requirement?statusFx=Neq&status=3&procFx=Eq&processId=-1')
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

            getAddedRequirements();
        }).error(function (err) {
            alert(err.message);
        });
    }

    function getAddedRequirements() {
        $http.get('supersede-dm-app/processes/requirements/list?processId=' + $scope.processId)
        .success(function (data) {
            var addedRequirements = data;
            var requirementsRows = $("#requirements").jqxGrid("getrows").length;

            for (var i = 0; i < requirementsRows; i++) {
                var added = false;
                var currentRequirement = $("#requirements").jqxGrid("getrowdatabyid", i);

                for (var j = 0; j < addedRequirements.length; j++) {
                    if (addedRequirements[j].requirementId === currentRequirement.requirementId) {
                        $("#requirements").jqxGrid("selectrow", i);
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    $("#requirements").jqxGrid("unselectrow", i);
                }
            }
        }).error(function (err) {
            alert(err.message);
        });
    }

    function defineProcessData() {
        var selectedRequirements = $("#requirements").jqxGrid("selectedrowindexes");

        for (var i = 0; i < selectedRequirements.length; i++) {
            var selectedRequirement = $("#requirements").jqxGrid('getrowdata', selectedRequirements[i]);
            gameRequirements.localdata.push(selectedRequirement);
            $scope.gameRequirementsId.push(selectedRequirement.requirementId);
        }
    }

    $scope.importRequirements = function () {
        defineProcessData();
        $http({
            method: 'POST',
            url: "supersede-dm-app/processes/requirements/import",
            params: { processId: $scope.processId, requirementsId: $scope.gameRequirementsId }
        })
        .success(function(data) {
            $scope.home();
        }).error(function (err) {
            $("#imported").html("<strong>Unable to add the selected requirements to the process: " + err.message + "</strong>");
        });
    };

    $scope.home = function() {
        $location.url('supersede-dm-app/process?processId=' + $scope.processId);
    };

    getAvailableRequirements();
});

app.controllerProvider.register('import_users', function($scope, $http, $location) {

    $scope.processId = $location.search().processId;
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

            getAddedUsers();
        }).error(function (err) {
            alert(err.message);
        });
    }

    function getAddedUsers() {
        $http.get('supersede-dm-app/processes/users/list?processId=' + $scope.processId)
        .success(function (data) {
            var addedUsers = data;
            var usersRows = $("#users").jqxGrid("getrows").length;

            for (var i = 0; i < usersRows; i++) {
                var added = false;
                var currentUser = $("#users").jqxGrid("getrowdatabyid", i);

                for (var j = 0; j < addedUsers.length; j++) {
                    if (addedUsers[j] === currentUser.userId) {
                        $("#users").jqxGrid("selectrow", i);
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    $("#users").jqxGrid("unselectrow", i);
                }
            }
        }).error(function (err) {
            alert(err.message);
        });
    }

    function defineProcessData() {
        var selectedOpinionProviders = $("#users").jqxGrid("selectedrowindexes");
        for (var i = 0; i < selectedOpinionProviders.length; i++) {
            var selectedOpinionProvider = $("#users").jqxGrid('getrowdata', selectedOpinionProviders[i]);
            $scope.gameOpinionProviders.localdata.push(selectedOpinionProvider);
            $scope.gameOpinionProvidersId.push(selectedOpinionProvider.userId);
        }
    }

    $scope.importUsers = function () {
        defineProcessData();
        $http({
            method: 'POST',
            url: "supersede-dm-app/processes/users/import",
            params: { processId: $scope.processId, idlist: $scope.gameOpinionProvidersId }
        })
        .success(function (data) {
            $scope.home();
        }).error(function (err) {
            $("#imported").html("<strong>Unable to add the selected users to the process: " + err.message + "</strong>");
        });
    };

    $scope.home = function() {
        $location.url('supersede-dm-app/process?processId=' + $scope.processId);
    };

    getAvailablePlayers();
});

app.controllerProvider.register('import_criteria', function($scope, $http, $location) {

    $scope.processId = $location.search().processId;
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

            getAddedCriteria();
        }).error(function (err) {
            alert(err.message);
        });
    }

    function getAddedCriteria() {
        $http.get('supersede-dm-app/processes/criteria/list?processId=' + $scope.processId)
        .success(function (data) {
            var addedCriteria = data;
            var criteriaRows = $("#criteria").jqxGrid("getrows").length;

            for (var i = 0; i < criteriaRows; i++) {
                var added = false;
                var currentCriterion = $("#criteria").jqxGrid("getrowdatabyid", i);

                for (var j = 0; j < addedCriteria.length; j++) {
                    if (addedCriteria[j] === currentCriterion.criteriaId) {
                        $("#criteria").jqxGrid("selectrow", i);
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    $("#criteria").jqxGrid("unselectrow", i);
                }
            }
        }).error(function (err) {
            alert(err.message);
        });
    }

    function defineProcessData() {
        var selectedCriteria = $("#criteria").jqxGrid("selectedrowindexes");
        for (var i = 0; i < selectedCriteria.length; i++) {
            var selectedCriterion = $("#criteria").jqxGrid('getrowdata', selectedCriteria[i]);
            $scope.gameCriteria.localdata.push(selectedCriterion);
            $scope.gameCriteriaId.push(selectedCriterion.criteriaId);
        }
    }

    $scope.importCriteria = function () {
        defineProcessData();
        $http({
            method: 'POST',
            url: "supersede-dm-app/processes/criteria/import",
            params: { processId: $scope.processId, idlist: $scope.gameCriteriaId }
        })
        .success(function (data) {
            $scope.home();
        }).error(function (err) {
            $("#imported").html("<strong>Unable to add the selected criteria to the process: " + err.message + "</strong>");
        });
    };

    $scope.home = function() {
        $location.url('supersede-dm-app/process?processId=' + $scope.processId);
    };

    getAvailableCriteria();
});

app.controllerProvider.register('import_alerts', function ($scope, $http, $location) {

    $scope.processId = $location.search().processId;
    $scope.alertsId = [];

    var availableAlerts = {};

    $scope.alerts = {
        datatype: "json",
        datafields: [
            { name: 'id' },
            { name: 'applicationId' },
            { name: 'timestamp' }
        ],
        localdata: []
    };

    function getAvailableAlerts() {
        $http.get('supersede-dm-app/alerts/biglist')
        .success(function (data) {
            availableAlerts = {
                datatype: "json",
                localdata: data,
                datafields: [
                    { name: 'applicationId', map: 'applicationId' },
                    { name: 'alertId' },
                    { name: 'timestamp' },
                    { name: 'description' },
                    { name: 'classification' },
                    { name: 'accuracy' },
                    { name: 'pos' },
                    { name: 'neg' },
                    { name: 'overall' },
                    { name: 'id' }
                ],
            };
            var cellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties) {
                return '<div class="jqx-grid-cell-left-align"><jqx-button ng-click="deleteAlert(' + "'" + value + "'" +
                    ')">Delete</jqx-button></div>';
            };
            var dataAdapter = new $.jqx.dataAdapter(availableAlerts);
            $("#alerts").jqxGrid({
                width: "100%",
                source: dataAdapter,
                selectionmode: 'checkbox',
                editable: true,
                editmode: 'dblclick',
                groupable: true,
                columns: [
                    { text: 'App', dataField: 'applicationId', width: "15%" },
                    { text: 'Alert', dataField: 'alertId', width: "10%" },
                    { text: 'Timestamp', dataField: 'timestamp', width: "10%" },
                    { text: 'Description', dataField: 'description', width: "20%" },
                    { text: 'Classification', dataField: 'classification', width: "15%" },
                    { text: 'Accuracy', dataField: 'accuracy', width: "5%" },
                    { text: 'Pos.', dataField: 'pos', width: "5%" },
                    { text: 'Neg.', dataField: 'neg', width: "5%" },
                    { text: 'Overall.', dataField: 'overall', width: "5%" },
                    { text: '', dataField: 'id', width: "10%", cellsrenderer: cellsrenderer }
                ],
                groups: ['applicationId', 'alertId']
            });

            getAddedAlerts();
        }).error(function (err) {
            alert(err.message);
        });
    }

    function getAddedAlerts() {
        $http.get('supersede-dm-app/processes/alerts/list?processId=' + $scope.processId)
        .success(function (data) {
            var addedAlerts = data;
            var alertsRows = $("#alerts").jqxGrid("getrows").length;

            for (var i = 0; i < alertsRows; i++) {
                var added = false;
                var currentAlert = $("#alerts").jqxGrid("getrowdatabyid", i);

                for (var j = 0; j < addedAlerts.length; j++) {
                    if (addedAlerts[j].id === currentAlert.alertId) {
                        $("#alerts").jqxGrid("selectrow", i);
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    $("#alerts").jqxGrid("unselectrow", i);
                }
            }
        }).error(function (err) {
            alert(err.message);
        });
    }

    $scope.deleteAlert = function(id) {
        for (var i = 0; i < availableAlerts.localdata.length; i++) {
            if (availableAlerts.localdata[i].id === id) {
                discardAlert(id);
            }
        }
    };

    function discardAlert(id) {
        $http.put('supersede-dm-app/alerts/userrequests/discard?id=' + id)
        .success(function () {
            getAvailableAlerts();
        }).error(function (err) {
            alert(err.message);
        });
    }

    function defineProcessData() {
        // TODO: check how to avoid having the same alert added multiple times if alerts are grouped by columns
        var selectedAlerts = $("#alerts").jqxGrid("selectedrowindexes");
        $scope.alerts.localdata = [];
        $scope.alertsId = [];

        for (var i = 0; i < selectedAlerts.length; i++) {
            var selectedAlert = $("#alerts").jqxGrid('getrowdata', selectedAlerts[i]);
            $scope.alerts.localdata.push(selectedAlert);
            $scope.alertsId.push(selectedAlert.id);
        }
    }

    $scope.importAlerts = function () {
        defineProcessData();
        $http({
            method: 'POST',
            url: "supersede-dm-app/processes/alerts/userrequests/import",
            params: { processId: $scope.processId, userRequests: $scope.alertsId }
        })
        .success(function (data) {
            $scope.home();
        }).error(function (err) {
            $("#imported").html("<strong>Unable to add the selected alerts to the process: " + err.message + "</strong>");
        });
    };

    $scope.home = function () {
        $location.url('supersede-dm-app/process?processId=' + $scope.processId);
    };

    getAvailableAlerts();
});