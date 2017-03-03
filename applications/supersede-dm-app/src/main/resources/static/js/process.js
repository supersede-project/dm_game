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
    $scope.userCount = undefined;

    $http({
        method: 'GET',
        url: "supersede-dm-app/processes/users/list",
        params: { procId: procId }
    }).success(function(data){
        $scope.userCount = data.length;
    });

    $http({
        method: 'GET',
        url: "supersede-dm-app/processes/criteria/list",
        params: { procId: procId }
    }).success(function(data){
        $scope.criteriaCount = data.length;
    });

    $http({
        method: 'GET',
        url: "supersede-dm-app/processes/requirements/count",
        params: { procId: procId }
    }).success(function(data){
        $scope.requirementsCount = data;
    });

    $http({
        method: 'GET',
        url: "supersede-dm-app/processes/requirements/stablestatus",
        params: { procId: procId },
        headers: {
            'Content-Type': undefined
          }
    }).success(function(data){
        $scope.processStatus = data;
    });

    function loadActivities() {
        console.log("Loading activities");
        $http.get('supersede-dm-app/processes/available_activities?procId=' + procId).success(function (data) {
            console.log("Activities:");
            console.log(data);
            $("#procList").jqxListBox({
                source: data, width: 700, height: 250,
                renderer: function (index, label, value) {
                    var datarecord = data[index];
                    var imgurl = 'supersede-dm-app/img/process.png';
                    var img = '<img height="50" width="50" src="' + imgurl + '"/>';
                    var table =
                        '<table style="min-width: 130px;">' +
                        '<tr><td style="width: 40px;" rowspan="2">' +
                        img + '</td><td>' +
                        datarecord.methodName +
                        '</td></tr><tr><td>' +
                        '<jqx-link-button jqx-width="200" jqx-height="30"> <a ' +
                        'href="#/supersede-dm-app/' + datarecord.entryUrl + '?procId=' + procId + '">Open</a>' +
                        '</jqx-link-button>' +
                        '</td></tr>' +
                        '</table>';
                    return table;
                }
            });
        });
    }

    loadActivities();

//    $http.get('supersede-dm-app/processes/requirements/statusmap?procId=' + $scope.procId ).success(function(data) {
//        console.log(data);
//        $('#barGauge').jqxBarGauge({colorScheme: "scheme02", width: 400, height: 400,
//            values: [102, 115, 130, 137], max: 150,
//            labels: {
//                formatFunction: function (value) {
//                    var realVal = parseInt(value);
//                    if( realVal == 0 ) return "Unconfirmed";
//                    if( realVal == 1 ) return "Editable";
//                    if( realVal == 2 ) return "Confirmed";
//                    if( realVal == 3 ) return "Enacted";
//                    if( realVal == 4 ) return "Discarded";
//                    return "Other";
//                },
//                font: { size: 12 },
//                indent: 10
//            }
//        });
//    });

//    $scope.next = function() {
//        $http({
//            method: 'GET',
//            url: "supersede-dm-app/processes/requirements/next",
//            params: { procId: $scope.procId }
//        }).success(function(data){
//            $scope.loadActivities();
//        });
//    };

    $("#btnPrevPhase").jqxButton({ width: 60, height: 250 });
    $("#btnNextPhase").jqxButton({ width: 60, height: 250  });
    $("#btnNextPhase").on('click', function() {
        $http({
            method: 'GET',
            url: "supersede-dm-app/processes/requirements/next",
            params: { procId: procId }
        }).success(function (data) {
            loadActivities();
        }).error(function (error) {
            console.log(error);
        });
    } );
});

$(document).ready(function () {
});