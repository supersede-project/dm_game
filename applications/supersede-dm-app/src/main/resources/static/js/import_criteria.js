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

app.controllerProvider.register('import_criteria', function($scope, $http, $location) {

    var gameCriteriaId = [];
    var availableCriteria = {};
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

    // Get the criteria that can be imported to the process
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
                autorowheight: true,
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

    // Automatically select criteria already added to the process
    function getAddedCriteria() {
        // Get the criteria already added to the process
        $http.get('supersede-dm-app/processes/criteria/list?processId=' + processId)
        .success(function (data) {
            var addedCriteria = data;

            // Get the number of already added criteria
            var criteriaRows = $("#criteria").jqxGrid("getrows").length;

            for (var i = 0; i < criteriaRows; i++) {
                var added = false;

                // Get the criterion at the given index
                var currentCriterion = $("#criteria").jqxGrid("getrowdatabyid", i);

                for (var j = 0; j < addedCriteria.length; j++) {
                    if (addedCriteria[j] === currentCriterion.criteriaId) {
                        // Select the criterion if it has already been added to the process
                        $("#criteria").jqxGrid("selectrow", i);
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    // Deselect the criterion if it has not been added yet to the process
                    $("#criteria").jqxGrid("unselectrow", i);
                }
            }
        }).error(function (err) {
            alert(err.message);
        });
    }

    // Save the selected criterion in local variables
    function defineProcessData() {
        var selectedCriteria = $("#criteria").jqxGrid("selectedrowindexes");
        for (var i = 0; i < selectedCriteria.length; i++) {
            var selectedCriterion = $("#criteria").jqxGrid('getrowdata', selectedCriteria[i]);
            gameCriteriaId.push(selectedCriterion.criteriaId);
        }
    }

    // Add the selected criteria to the process
    $scope.importCriteria = function () {
        // Save the selected criteria
        defineProcessData();

        // Perform a request to add the selected criteria to the process
        $http({
            method: 'POST',
            url: "supersede-dm-app/processes/criteria/import",
            params: { processId: processId, idlist: gameCriteriaId }
        })
        .success(function (data) {
            $scope.home();
        }).error(function (err) {
            alert("Unable to add the selected criteria to the process: " + err.message);
        });
    };

    // Go back to the process details page
    $scope.home = function() {
        $location.url('supersede-dm-app/process?processId=' + processId);
    };

    // Automatically select the criteria already added to the process
    getAvailableCriteria();
});