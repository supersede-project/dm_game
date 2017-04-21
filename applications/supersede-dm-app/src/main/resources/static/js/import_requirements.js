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

    var gameRequirementsId = [];
    var availableRequirements = {};
    var processId = $location.search().processId;

    // Get the details of the process
    $http.get("supersede-dm-app/processes/details?processId=" + processId)
    .success(function (data) {
        var processes = [];
        processes.push(data);
        var source = {
            datatype: "json",
            datafields: [
                { name: "name" },
                { name: "objective" },
                { name: "status" },
                { name: "phaseName" }
            ],
            localdata: processes
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $("#process_details").jqxGrid({
            width: '100%',
            autoheight: true,
            autorowheight: true,
            source: dataAdapter,
            columns: [
                { text: 'Name', datafield: 'name', width: '25%' },
                { text: 'Objective', datafield: 'objective', width: '25%' },
                { text: 'Status', datafield: 'status', width: '25%' },
                { text: 'Phase', datafield: 'phaseName', width: '25%' }
            ]
        });
    }).error(function (err) {
        alert(err.message);
    });

    // Get the requirements that can be imported to the process
    function getAvailableRequirements() {
        // Get all the requirements that are not enacted (status != 3)
        // and that do not belong to any process (processId == -1)
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
                autorowheight: true,
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

    // Automatically select requirements already added to the process
    function getAddedRequirements() {
        // Get the requirements already added to the process
        $http.get('supersede-dm-app/processes/requirements/list?processId=' + processId)
        .success(function (data) {
            var addedRequirements = data;

            // Get the number of already added requirements
            var requirementsRows = $("#requirements").jqxGrid("getrows").length;

            for (var i = 0; i < requirementsRows; i++) {
                var added = false;

                // Get the requirement at the given index
                var currentRequirement = $("#requirements").jqxGrid("getrowdatabyid", i);

                for (var j = 0; j < addedRequirements.length; j++) {
                    if (addedRequirements[j].requirementId === currentRequirement.requirementId) {
                        // Select the requirement if it has been already added to the process
                        $("#requirements").jqxGrid("selectrow", i);
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    // Deselect the requirement if it has not been added yet to the process
                    $("#requirements").jqxGrid("unselectrow", i);
                }
            }
        }).error(function (err) {
            alert(err.message);
        });
    }

    // Save the selected requirements in local variables
    function defineProcessData() {
        var selectedRequirements = $("#requirements").jqxGrid("selectedrowindexes");

        for (var i = 0; i < selectedRequirements.length; i++) {
            var selectedRequirement = $("#requirements").jqxGrid('getrowdata', selectedRequirements[i]);
            gameRequirementsId.push(selectedRequirement.requirementId);
        }
    }

    // Add the selected requirements to the process
    $scope.importRequirements = function () {
        // Save the selected requirements
        defineProcessData();

        // Perform a request to add the selected requirements to the process
        $http({
            method: 'POST',
            url: "supersede-dm-app/processes/requirements/import",
            params: { processId: processId, requirementsId: gameRequirementsId }
        })
        .success(function(data) {
            $scope.home();
        }).error(function (err) {
            alert("Unable to add the selected requirements to the process: " + err.message);
        });
    };

    // Go back to the process details page
    $scope.home = function() {
        $location.url('supersede-dm-app/process?processId=' + processId);
    };

    // Automatically select the requirements already added to the process
    getAvailableRequirements();
});