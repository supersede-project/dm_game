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

    var procId = $location.search().procId;

    $http({
        method: 'GET',
        url: "supersede-dm-app/processes/users/list/detailed",
        params: { procId: procId }
    }).success(function(data) {
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

    $http({
        method: 'GET',
        url: "supersede-dm-app/processes/criteria/list/detailed",
        params: { procId: procId }
    }).success(function(data) {
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

    $http({
        method: 'GET',
        url: "supersede-dm-app/processes/requirements/list",
        params: { procId: procId }
    }).success(function(data){
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

    $http({
        method: 'GET',
        url: "supersede-dm-app/processes/status",
        params: { procId: procId },
        headers: {
            'Content-Type': undefined
          }
    }).success(function(data){
        $scope.processStatus = data;
    }).error(function (err) {
        alert(err.message);
    });

    function loadActivities() {
        $http.get('supersede-dm-app/processes/available_activities?procId=' + procId)
        .success(function (data) {
            console.log("Loading activities:");
            console.log(data);
            $("#procList").jqxListBox('clear');
            $("#procList").jqxListBox({
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
                        img + '</td><td>' +
                        '<a href="#/supersede-dm-app/' + datarecord.entryUrl + '?procId=' + procId + '">' + datarecord.methodName +
                        '</a></td></tr></table>';
                    return table;
                }
            });
        }).error(function (err) {
            alert(err.message);
        });
    }

    loadActivities();

    $("#btnPrevPhase").jqxButton({ width: 60, height: 250 });
    $("#btnPrevPhase").on('click', function() {
        $http({
            method: 'GET',
            url: "supersede-dm-app/processes/requirements/prev",
            params: { procId: procId }
        }).success(function (data) {
        	$scope.processStatus = data;
            loadActivities();
        }).error(function (err) {
            alert(err.message);
        });
    } );
    $("#btnNextPhase").jqxButton({ width: 60, height: 250  });
    $("#btnNextPhase").on('click', function() {
        $http({
            method: 'GET',
            url: "supersede-dm-app/processes/requirements/next",
            params: { procId: procId }
        }).success(function (data) {
        	$scope.processStatus = data;
            loadActivities();
        }).error(function (err) {
            alert(err.message);
        });
    } );
});