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

    function getAddedUsers() {
        $http.get('supersede-dm-app/processes/users/list?processId=' + processId)
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
            gameOpinionProvidersId.push(selectedOpinionProvider.userId);
        }
    }

    $scope.importUsers = function () {
        defineProcessData();
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

    $scope.home = function () {
        $location.url('supersede-dm-app/process?processId=' + processId);
    };

    getAvailablePlayers();
});