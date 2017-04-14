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
        $http.get('supersede-dm-app/processes/criteria/list?processId=' + processId)
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
            gameCriteriaId.push(selectedCriterion.criteriaId);
        }
    }

    $scope.importCriteria = function () {
        defineProcessData();
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

    $scope.home = function() {
        $location.url('supersede-dm-app/process?processId=' + processId);
    };

    getAvailableCriteria();
});