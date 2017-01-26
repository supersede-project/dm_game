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

var app = angular.module("w5app");

app.controllerProvider.register("display_criteria", function($scope, $http, $location) {

    var gameId = $location.search().id;

    $scope.gameId = gameId;

    $http.get("supersede-dm-app/garp/game/game?gameId=" + gameId)
    .success(function(data) {
        console.log(data);
        var source = {
            datatype: "json",
            datafields: [
                { name: 'id' },
                { name: 'owner' },
                { name : 'date' },
                { name : 'status' }
            ],
            localdata: data
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $("#game").jqxGrid({
            width: 600,
            autoheight: true,
            source: dataAdapter,
            columns: [
              { text: 'Id', datafield: 'id', width: 200 },
              { text: 'Owner', datafield: 'owner', width: 100 },
              { text: 'Date', datafield: 'date', width: 200 },
              { text: 'Status', datafield: 'status', width: 100 }
            ]
        });
    });

    $http.get("supersede-dm-app/garp/game/gamecriteria?gameId=" + gameId)
    .success(function(data) {
        console.log(data);
        var source = {
            datatype: "json",
            datafields: [
                { name: 'name' },
                { name : 'description' },
                { name: 'criteriaId' }
            ],
            localdata: data
        };
        var cellsrenderer = function (row, columnfield, value, defaulthtml, columnproperties) {
            //return '<a href="#/supersede-dm-app/garp/gameplay?id=' + gameId + "&idC=" + value + '">' + value + "</a>";
            var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
            r = r.concat('<jqx-link-button jqx-width="200" jqx-height="25"><a href="#/supersede-dm-app/garp/gameplay?id=');
            r = r.concat(gameId + "&idC=" + value + '">Define requirements priority</a></jqx-link-button></div>');
            return r;
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        $("#selectedCriteria").jqxGrid({
            width: 650,
            altrows: true,
            autoheight: true,
            source: dataAdapter,
            rowsheight: 32,
            columns: [
                { text: 'Name', datafield: 'name', width: 100 },
                { text: 'Description', datafield: 'description', width: 300 },
                { text: '', datafield: 'criteriaId', width: 250, cellsrenderer: cellsrenderer }
            ]
        });
    });

    $scope.finish = function() {
        $location.url("supersede-dm-app/garp/results" + gameId);
    };
});