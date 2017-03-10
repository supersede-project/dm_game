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

var http;
var loadProcesses;

app.controllerProvider.register('home', function ($scope, $rootScope, $http, $location) {

    http = $http;
    $scope.procNum = undefined;

    $http.get('/supersede-dm-app/user/current')
    .success(function (data) {
        $scope.user = data;
    });

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

    // Get alerts
    $http.get('supersede-dm-app/alerts/biglist').success(function (data) {
        $scope.alertsNum = data.length;

        $http.get('supersede-dm-app/alerts/biglist').success(function (data) {
            console.log("Alerts:");
            console.log(data);

            var source =
            {
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
            $("#gridAlerts").jqxGrid(
            {
                width: "100%",
                source: dataAdapter,
                //            pageable: true,
                //            columnsResize: true,
                //            altRows: true,
                groupable: true,
                //            ready: function () {
                //                $("#treeGrid").jqxTreeGrid('expandRow', "0");
                //            },
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
    //              { text: 'Features.', dataField: 'features', width: 120 }
                ],
            groups: ['applicationId', 'alertId']
            });

            $("#expAlerts").jqxExpander({ width: '100%', expanded: false });
        });
    });


    // Get requirements
    $http.get('supersede-dm-app/requirement?procFx=Eq&procId=-1').success(function(data) {
        $scope.reqNum = data.length;

        var source = {
            localdata: data,
            datatype: "array"
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $('#requirementslist').jqxListBox({
            source: dataAdapter,
            width: '100%',
            renderer: function (index, label, value) {
                var datarecord = data[index];

                if (datarecord === undefined) {
                    return "";
                }

                return datarecord.name + " - " + datarecord.description;
            }
        });

        $("#expRequirements").jqxExpander({ width: '100%', expanded: false });
    });

    // Get activities
    $http.get('supersede-dm-app/processes/activities/list').success(function(data) {
        $scope.actNum = data.length;

        var source = {
            localdata: data,
            datatype: "array"
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $('#activities-listbox').jqxListBox({ selectedIndex: 0,
            source: dataAdapter,
            displayMember: "name",
            valueMember: "id",
            itemHeight: 70, width: '100%',
            renderer: function (index, label, value) {
                var datarecord = data[index];
                var action;
                if( datarecord.state == "new" ) {
                    action = "Start";
                }
                else {
                    action = datarecord.state;
                }
                var table =
                    '<table style="min-width: 100%;">' +
                    '<tr><td style="width: 40px;">' + "img" + '</td><td>' +
                    '<a href="#/supersede-dm-app/' + datarecord.url +
                    '?procId=' + datarecord.processId +
                    '&activityId=' + datarecord.activityId +
                    '">' + datarecord.methodName + '</a>' +
                    '</td></tr></table>';
                console.log(table);
                return table;
            }
        });

        $("#expActivities").jqxExpander({ width: '100%', expanded: false });
    });


    $scope.loadProcesses = function () {
        $http.get('supersede-dm-app/processes/list').success(function (data) {
            //            $('#listbox').jqxListBox('clear');
            console.log("procNum:");
            console.log(data.length);
            $scope.procNum = data.length;
            var source = {
                localdata: data,
                datatype: "array"
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            $('#listbox').jqxListBox({
                selectedIndex: 0,
                source: dataAdapter,
                displayMember: "name",
                valueMember: "id",
                itemHeight: 70, width: '100%',
                renderer: function (index, label, value) {
                    var datarecord = data[index];
                    //                    var imgurl = '../../images/' + label.toLowerCase() + '.png';
                    //                    var img = '<img height="50" width="40" src="' + imgurl + '"/>';

                    if (datarecord === undefined) {
                        return "";
                    }

                    var action;
                    if (datarecord.state == "new") {
                        action = "Start";
                    }
                    else {
                        action = datarecord.state;
                    }
                    var table =
                        '<table style="min-width: 100%;">' +
                        '<tr><td style="width: 40px;">' + action + '</td><td>' +
                        datarecord.name + " (" + datarecord.objective + ")" + '</td>' +
                        '<td style="width: 40px;">' +
                        '<jqx-link-button jqx-width="200" jqx-height="30"> <a ' +
                        'href="#/supersede-dm-app/process?procId=' + datarecord.id + '">View</a></jqx-link-button>' +
                        '<jqx-link-button style="margin-left: 10px")"><a href="javascript:" onclick="closeProcess(\'' + datarecord.id + '\');">Close</a></jqx-button>' +
                        '<jqx-link-button style="margin-left: 10px")"><a href="javascript:" onclick="deleteProcess(\'' + datarecord.id + '\');">Delete</a></jqx-button>' +
                        '</td>' +
                        '</tr><tr><td>' +
                        "Created: " + datarecord.date +
                        '</td></tr></table>';
                    return table;
                }
            });

            $("#expProcesses").jqxExpander({ width: '100%', expanded: false });
        });
    };

    loadProcesses = $scope.loadProcesses;

    $scope.createNewProcess = function() {
        $http.post('supersede-dm-app/processes/new').success(function(data) {
            loadProcesses();
        });
    };

    loadProcesses();
});

function closeProcess(procId) {
    console.log("closing process " + procId);
    http.post('supersede-dm-app/processes/close?procId=' + procId).success(function (data) {
        loadProcesses();
    });
}

function deleteProcess(procId) {
    console.log("deleting process " + procId);
    http.post('supersede-dm-app/processes/delete?procId=' + procId).success(function (data) {
        loadProcesses();
    });
}