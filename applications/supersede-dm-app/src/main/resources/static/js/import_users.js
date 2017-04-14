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

app.controllerProvider.register('import_users', function ($scope, $http, $location) {

    var gameOpinionProvidersId = [];
    var availablePlayers = {};
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

    // Get the users that can be imported to the process
    function getAvailablePlayers() {
        $http.get('supersede-dm-app/user?profile=OPINION_PROVIDER')
        .success(function (data) {
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

    // Automatically select users already added to the process
    function getAddedUsers() {
        // Get the users already added to the process
        $http.get('supersede-dm-app/processes/users/list?processId=' + processId)
        .success(function (data) {
            var addedUsers = data;

            // Get the number of already added users
            var usersRows = $("#users").jqxGrid("getrows").length;

            for (var i = 0; i < usersRows; i++) {
                var added = false;

                // Get the user at the given index
                var currentUser = $("#users").jqxGrid("getrowdatabyid", i);

                for (var j = 0; j < addedUsers.length; j++) {
                    if (addedUsers[j] === currentUser.userId) {
                        // Select the user if it has been already added to the process
                        $("#users").jqxGrid("selectrow", i);
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    // Deselect the user if it has not been added yet to the process
                    $("#users").jqxGrid("unselectrow", i);
                }
            }
        }).error(function (err) {
            alert(err.message);
        });
    }

    // Save the selected user in local variables
    function defineProcessData() {
        var selectedOpinionProviders = $("#users").jqxGrid("selectedrowindexes");
        for (var i = 0; i < selectedOpinionProviders.length; i++) {
            var selectedOpinionProvider = $("#users").jqxGrid('getrowdata', selectedOpinionProviders[i]);
            gameOpinionProvidersId.push(selectedOpinionProvider.userId);
        }
    }

    // Add the selected users to the process
    $scope.importUsers = function () {
        // Save the selected users
        defineProcessData();

        // Perform a request to add the selected users to the process
        $http({
            method: 'POST',
            url: "supersede-dm-app/processes/users/import",
            params: { processId: processId, idlist: gameOpinionProvidersId }
        })
        .success(function (data) {
            $scope.home();
        }).error(function (err) {
            alert("Unable to add the selected users to the process: " + err.message);
        });
    };

    // Go back to the process details page
    $scope.home = function () {
        $location.url('supersede-dm-app/process?processId=' + processId);
    };

    // Automatically select the users already added to the process
    getAvailablePlayers();
});