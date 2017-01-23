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

app.controllerProvider.register('create_game', function($scope, $http, $location) {

    $http.get('supersede-dm-app/requirement')
    .success(function(data) {
        for (var i = 0; i < data.length; i++) {
            data[i].selected = false;
        }
        var source = {
            datatype: "json",
            datafields: [
                { name: 'selected' },
                { name: 'requirementId' },
                { name: 'name' },
                { name : 'description' }
            ],
            localdata: data
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $("#requirements").jqxGrid({
            width: 750,
            editable: true,
            selectionmode: 'none',
            autoheight: true,
            source: dataAdapter,
            columns: [
                { text: '', datafield: 'selected', columntype: 'checkbox', width: 50 },
                { text: 'Id', editable: 'false', datafield: 'requirementId', width: 100 },
                { text: 'Name', editable: 'false', datafield: 'name', width: 300 },
                { text: 'Description', editable: 'false', datafield: 'description', width: 300 }
            ]
        });
        $("#requirements").bind('cellendedit', function (event) {
            if (event.args.value) {
                $("#requirements").jqxGrid('selectrow', event.args.rowindex);
            }
            else {
                $("#requirements").jqxGrid('unselectrow', event.args.rowindex);
            }
        });
    });

    $http.get('supersede-dm-app/criteria')
    .success(function(data) {
        for (var i = 0; i < data.length; i++) {
            data[i].selected = false;
        }
        var source = {
            datatype: "json",
            datafields: [
                { name: 'selected' },
                { name: 'criteriaId' },
                { name: 'name' },
                { name : 'description' }
            ],
            localdata: data
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $("#criteria").jqxGrid({
            width: 750,
            editable: true,
            selectionmode: 'none',
            autoheight: true,
            source: dataAdapter,
            columns: [
                { text: '', datafield: 'selected', columntype: 'checkbox', width: 50 },
                { text: 'Id', editable: 'false', datafield: 'criteriaId', width: 100 },
                { text: 'Name', editable: 'false', datafield: 'name', width: 300 },
                { text: 'Description', editable: 'false', datafield: 'description', width: 300 }
            ]
        });
        $("#criteria").bind('cellendedit', function (event) {
            if (event.args.value) {
                $("#criteria").jqxGrid('selectrow', event.args.rowindex);
            }
            else {
                $("#criteria").jqxGrid('unselectrow', event.args.rowindex);
            }
        });
    });

    $http.get('supersede-dm-app/user?profile=OPINION_PROVIDER')
    .success(function(data) {
        for (var i = 0; i < data.length; i++) {
            data[i].selected = false;
        }
        var source = {
            datatype: "json",
            datafields: [
                { name: 'selected' },
                { name: 'userId' },
                { name: 'name' },
                { name : 'email' }
            ],
            localdata: data
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $("#players").jqxGrid({
            width: 750,
            editable: true,
            selectionmode: 'none',
            autoheight: true,
            source: dataAdapter,
            columns: [
                { text: '', datafield: 'selected', columntype: 'checkbox', width: 50 },
                { text: 'Id', editable: 'false', datafield: 'userId', width: 100 },
                { text: 'Name', editable: 'false', datafield: 'name', width: 300 },
                { text: 'Email', editable: 'false', datafield: 'email', width: 300 }
            ]
        });
        $("#players").bind('cellendedit', function (event) {
            if (event.args.value) {
                $("#players").jqxGrid('selectrow', event.args.rowindex);
            }
            else {
                $("#players").jqxGrid('unselectrow', event.args.rowindex);
            }
        });
    });

    $scope.create_game = function () {

    };
});