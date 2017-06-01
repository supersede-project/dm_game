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

app.controllerProvider.register('process', function($scope, $http, $location) {

    var processId = $location.search().processId;

    // Get the users of the process
    $http.get('supersede-dm-app/processes/users/list/detailed?processId=' + processId)
    .success(function(data) {
        var source = {
            datatype: "json",
            datafields: [
                { name: 'userId' },
                { name: 'name' },
                { name: 'email' }
            ],
            localdata: data
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $("#users").jqxGrid({
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
    }).error(function (err) {
        alert(err.message);
    });

    // Get the criteria of the process
    $http.get("supersede-dm-app/processes/criteria/list/detailed?processId=" + processId)
    .success(function(data) {
        var source = {
            datatype: "json",
            datafields: [
                { name: 'criterionId' },
                { name: 'name' },
                { name: 'description' }
            ],
            localdata: data
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $("#criteria").jqxGrid({
            width: '100%',
            altrows: true,
            autoheight: true,
            autorowheight: true,
            pageable: true,
            source: dataAdapter,
            columns: [
                { text: 'Id', datafield: 'criterionId', width: '15%' },
                { text: 'Name', datafield: 'name', width: '25%' },
                { text: 'Description', datafield: 'description', width: '60%' }
            ]
        });
    }).error(function (err) {
        alert(err.message);
    });

    // Get the requirements of the process
    $http.get("supersede-dm-app/processes/requirements/list?processId=" + processId)
    .success(function(data){
        var source = {
            datatype: "json",
            datafields: [
                { name: 'requirementId' },
                { name: 'name' },
                { name: 'description' }
            ],
            localdata: data
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $("#requirements").jqxGrid({
            width: '100%',
            altrows: true,
            autoheight: true,
            autorowheight: true,
            pageable: true,
            source: dataAdapter,
            columns: [
                { text: 'Id', datafield: 'requirementId', width: '15%' },
                { text: 'Name', datafield: 'name', width: '25%' },
                { text: 'Description', datafield: 'description', width: '60%' }
            ]
        });
    }).error(function (err) {
        alert(err.message);
    });

    // Get the activities of the process
    function loadActivities() {
        $http.get('supersede-dm-app/processes/available_activities?processId=' + processId)
        .success(function (data) {
            $("#actionsList").jqxListBox('clear');
            $("#actionsList").jqxListBox({
                source: data,
                width: 700,
                height: 250,
                renderer: function (index, label, value) {
                    var datarecord = data[index];
                    if (datarecord === undefined)
                    {
                        return "";
                    }
                    var imgurl = 'supersede-dm-app/img/process.png';
                    var img = '<img height="50" width="50" src="' + imgurl + '"/>';
                    var table =
                        '<table style="min-width: 130px">' +
                        '<tr><td style="width: 40px">' +
                        img + '</td><td>' + datarecord.methodName +
                        '</td></tr></table>';
                    return table;
                }
            });
        }).error(function (err) {
            alert(err.message);
        });
    }

    // Update the details of the process
    function updateProcessDetails(process) {
        // The jqxGrid requires an array: create one and add the process to it
        processes = [];
        processes.push(process);
        $scope.processName = process.name;
        var source = {
            datatype: "json",
            datafields: [
//                { name: "name" },
                { name: "objective" },
                { name: "status" },
                { name: "phaseName" }
            ],
            localdata: processes
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $("#process_details").jqxGrid({
            width: '75%',
            autoheight: true,
            source: dataAdapter,
            columns: [
//                { text: 'Name', datafield: 'name', width: '25%' },
                { text: 'Objective', datafield: 'objective', width: '25%' },
                { text: 'Status', datafield: 'status', width: '25%' },
                { text: 'Phase', datafield: 'phaseName', width: '50%' }
            ]
        });
    }

    // Get the details of the process
    $http.get('supersede-dm-app/processes/details?processId=' + processId)
    .success(function (data) {
        updateProcessDetails(data);
    }).error(function (err) {
        alert(err.message);
    });

    loadActivities();

    $("#btnPrevPhase").jqxButton({ width: 60, height: 250 });

    // Go to the previous phase of the process
    $("#btnPrevPhase").on('click', function () {
        $http.get('supersede-dm-app/processes/prev?processId=' + processId)
        .success(function (data) {
            updateProcessDetails(data);
            loadActivities();
        }).error(function (error) {
            alert(error.message);
        });
    });

    $("#btnNextPhase").jqxButton({ width: 60, height: 250 });

    // Go to the next phase of the process
    $("#btnNextPhase").on('click', function() {
        $http.get('supersede-dm-app/processes/next?processId=' + processId)
        .success(function (data) {
            updateProcessDetails(data);
            loadActivities();
        }).error(function (error) {
            alert(error.message);
        });
    });

    // When an action is selected, go to the corresponding page
    $('#actionsList').on('select', function (event) {
        var args = event.args;
        var item = $('#actionsList').jqxListBox('getItem', args.index);
        if (item !== null) {
            $location.url("supersede-dm-app/" + item.originalItem.entryUrl + "?processId=" + processId);
            $scope.$apply();
        }
    });

    // Go back to the DM game home
    $scope.home = function () {
        $location.url("supersede-dm-app/home");
    }
});