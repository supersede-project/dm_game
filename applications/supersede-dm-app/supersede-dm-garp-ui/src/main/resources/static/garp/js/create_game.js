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
        console.log(data);
        var source = {
            datatype: "json",
            datafields: [
                { name: 'requirementId' },
                { name: 'name' },
                { name : 'description' }
            ],
            localdata: data
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $("#requirements").jqxGrid({
            width: 500,
            autoheight: true,
            source: dataAdapter,
            columns: [
              { text: 'Id', datafield: 'requirementId', width: 100 },
              { text: 'Name', datafield: 'owner', width: 100 },
              { text: 'Description', datafield: 'date', width: 300 }
            ]
        });
    });
});