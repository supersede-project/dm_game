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

app.controllerProvider.register('import_alerts', function ($scope, $http, $location) {

    var alertsId = [];
    var availableAlerts = {};
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
                autorowheight: true,
                autoheight: true,
                source: dataAdapter,
                columnsautoresize: false,
                columnsmenu: false,
                enabletooltips: true,
                selectionmode: 'checkbox',
                editable: true,
                editmode: 'dblclick',
                groupable: true,
                columns: [
                    { text: 'App', dataField: 'applicationId', width: "5%" },
                    { text: 'Alert', dataField: 'alertId', width: "8%" },
                    { text: 'Timestamp', dataField: 'timestamp', width: "8%" },
                    { text: 'Description', dataField: 'description', width: "42%" },
                    { text: 'Classification', dataField: 'classification', width: "10%" },
                    { text: 'Accuracy', dataField: 'accuracy', width: "5%" },
                    { text: 'Pos.', dataField: 'pos', width: "5%" },
                    { text: 'Neg.', dataField: 'neg', width: "5%" },
                    { text: 'Overall.', dataField: 'overall', width: "5%" },
                    { text: '', dataField: 'id', width: "5%", cellsrenderer: cellsrenderer }
                ],
                groups: ['applicationId', 'alertId']
            });

            getAddedAlerts();
        }).error(function (err) {
            alert(err.message);
        });
    }

    function getAddedAlerts() {
        $http.get('supersede-dm-app/processes/alerts/list?processId=' + processId)
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
        for (var i = 0; i < alertsId.length; i++) {
            if (alertsId[i].id === id) {
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
        alertsId = [];

        for (var i = 0; i < selectedAlerts.length; i++) {
            var selectedAlert = $("#alerts").jqxGrid('getrowdata', selectedAlerts[i]);
            alertsId.push(selectedAlert.id);
        }
    }

    $scope.importAlerts = function () {
        defineProcessData();
        $http({
            method: 'POST',
            url: "supersede-dm-app/processes/alerts/userrequests/import",
            params: { processId: processId, userRequests: alertsId }
        })
        .success(function (data) {
            $scope.home();
        }).error(function (err) {
            alert("Unable to add the selected alerts to the process: " + err.message);
        });
    };

    $scope.home = function () {
        $location.url('supersede-dm-app/process?processId=' + processId);
    };

    getAvailableAlerts();
});