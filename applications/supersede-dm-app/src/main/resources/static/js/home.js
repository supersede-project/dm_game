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

app.controllerProvider.register('home', function ($scope, $rootScope, $http, $location) {

    // Get the alerts received from WP2
    $http.get('supersede-dm-app/alerts/biglist')
    .success(function (data) {
        $scope.alertsNum = data.length;

        $http.get('supersede-dm-app/alerts/biglist')
        .success(function (data) {
            var source = {
                datatype: "json",
                localdata: data,
                datafields: [
                    { name: 'applicationId', map: 'applicationId' },
                    { name: 'alertId' },
                    { name: 'id' },
                    { name: 'timestamp' },
                    { name: 'description' },
                    { name: 'classification' },
                    { name: 'accuracy' },
                    { name: 'pos' },
                    { name: 'neg' },
                    { name: 'overall' },
                ],
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            $("#alerts").jqxGrid({
                width: "100%",
                source: dataAdapter,
                groupable: true,
                columns: [
                  { text: 'App', dataField: 'applicationId', width: "15%" },
                  { text: 'Alert', dataField: 'alertId', width: "10%" },
                  { text: 'Id', dataField: 'id', width: "10%" },
                  { text: 'Timestamp', dataField: 'timestamp', width: "10%" },
                  { text: 'Description', dataField: 'description', width: "20%" },
                  { text: 'Classification', dataField: 'classification', width: "15%" },
                  { text: 'Accuracy', dataField: 'accuracy', width: "5%" },
                  { text: 'Pos.', dataField: 'pos', width: "5%" },
                  { text: 'Neg.', dataField: 'neg', width: "5%" },
                  { text: 'Overall.', dataField: 'overall', width: "5%" }
                ],
            groups: ['applicationId', 'alertId']
            });

            $("#expAlerts").jqxExpander({ width: '100%', expanded: false });
        }).error(function (err) {
            alert(err.message);
        });
    }).error(function (err) {
        alert(err.message);
    });

    // Get requirements that are not enacted a not assigned to any process
    $http.get('supersede-dm-app/requirement?statusFx=Neq&status=3&procFx=Eq&processId=-1')
    .success(function (data) {
        $scope.reqNum = data.length;
        var source = {
            datatype: "json",
            datafields: [
                { name: 'name' },
                { name: 'description' }
            ],
            localdata: data
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $('#requirements').jqxGrid({
            source: dataAdapter,
            width: '100%',
            autoheight: true,
            autorowheight: true,
            pageable: true,
            altrows: true,
            columns: [
                { text: 'Name', datafield: 'name', width: '30%' },
                { text: 'Description', datafield: 'description', width: '70%' }
            ]
        });

        $("#expRequirements").jqxExpander({ width: '100%', expanded: false });
    }).error(function (err) {
        alert(err.message);
    });

    // Get the available activities
    $http.get('supersede-dm-app/processes/activities/list')
    .success(function (data) {
        $scope.actNum = data.length;

        var source = {
            datatype: "json",
            datafields: [
                { name: "description" },
                { name: "url" },
                { name: "processId" },
                { name: "activityId"}
            ],
            localdata: data,
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        var cellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties) {
            var activity = dataAdapter.records[row];
            var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
            r = r.concat('<jqx-button jqx-height="25" ng-click="openActivity(\'' + activity.url + '\', ' +
                activity.processId + ', ' + activity.activityId + ')" style="margin-left: 10px">Open Activity</jqx-button></div>');
            return r;
        };
        $('#activities').jqxGrid({
            width: '100%',
            altrows: true,
            autoheight: true,
            rowsheight: 32,
            pageable: true,
            source: dataAdapter,
            columns: [
                { text: 'Description', datafield: 'description', width: '80%' },
                { text: '', datafield: 'url', width: '20%', cellsrenderer: cellsrenderer }
            ]
        });

        $("#expActivities").jqxExpander({ width: '100%', expanded: false });
    }).error(function (err) {
        alert(err.message);
    });

    function loadProcesses() {
        $http.get('supersede-dm-app/processes/list')
        .success(function (data) {
            $scope.procNum = data.length;
            var source = {
                datatype: "json",
                datafields: [
                    { name: 'name' },
                    { name: 'objective' },
                    { name: 'state' },
                    { name: 'date' },
                    { name: 'id' }
                ],
                localdata: data
            };
            var cellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties) {
                var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
                r = r.concat('<jqx-button jqx-height="25" ng-click="viewProcess(' + value + ')" style="margin-left: 10px">View</jqx-button>');
                r = r.concat('<jqx-button jqx-height="25" ng-click="closeProcess(' + value + ')" style="margin-left: 10px">Close</jqx-button>');
                r = r.concat('<jqx-button jqx-height="25" ng-click="deleteProcess(' + value + ')" style="margin-left: 10px">Delete</jqx-button></div>');
                return r;
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            $('#processes').jqxGrid({
                width: '100%',
                autoheight: true,
                pageable: true,
                altrows: true,
                source: dataAdapter,
                rowsheight: 32,
                columns: [
                    { text: 'Name', datafield: 'name', width: '20%' },
                    { text: 'Objective', datafield: 'objective', width: '20%' },
                    { text: 'State', datafield: 'state', width: '20%' },
                    { text: 'Date', datafield: 'date', width: '20%' },
                    { text: '', datafield: 'id', cellsrenderer: cellsrenderer, width: '20%' }
                ]
            });

            $("#expProcesses").jqxExpander({ width: '100%', expanded: false });
        }).error(function (err) {
            alert(err.message);
        });
    }

    $scope.hasRole = function (role) {
        if ($rootScope.user && $rootScope.user.authorities) {
            for (var i = 0; i < $rootScope.user.authorities.length; i++) {
                if ($rootScope.user.authorities[i].authority == "ROLE_" + role) {
                    return true;
                }
            }
        }

        return false;
    };

    $scope.createNewProcess = function () {
    	var name = prompt( "Enter process name", "");
        if (name !== null) {
    		$http.post('supersede-dm-app/processes/new?name=' + name).success(function(data) {
    			loadProcesses();
    		});
    	}
    };

    $scope.viewProcess = function (processId) {
        $location.url('supersede-dm-app/process?processId=' + processId);
    };

    $scope.closeProcess = function (processId) {
        $http.post('supersede-dm-app/processes/close?processId=' + processId)
        .success(function (data) {
            loadProcesses();
        }).error(function (err) {
            alert(err.message);
        });
    };

    $scope.deleteProcess = function (processId) {
        $http.post('supersede-dm-app/processes/delete?processId=' + processId)
        .success(function (data) {
            loadProcesses();
        }).error(function (err) {
            alert(err.message);
        });
    };

    $scope.editRequirements = function () {
        $location.url("supersede-dm-app/ahprp/requirements_criterias_editing");
    };

    $scope.openActivity = function (url, processId, activityId) {
        $location.url("supersede-dm-app/" + url).search('processId', processId).search('activityId', activityId);
    };

    loadProcesses();
});