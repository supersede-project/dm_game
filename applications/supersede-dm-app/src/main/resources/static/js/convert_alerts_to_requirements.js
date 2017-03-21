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

app.controllerProvider.register('convert_alerts_to_requirements', function ($scope, $http, $location) {

    var processId = $location.search().processId;

    $scope.userRequests = [];

    $scope.convertToRequirement = function (alertId) {
        $http.put('supersede-dm-app/processes/alerts/convert?alertId=' + alertId + '&processId=' + processId)
        .success(function (data) {
            $("#converted" + alertId).html("<strong>Alert " + alertId + " successfully converted to a requirement</strong>");
        }).error(function(err) {
            $("#converted" + alertId).html("<strong>Unable to convert alert " + alertId +
                " to a requirement: " + err.message + "</strong>");
        });
    };

    $scope.discard = function (alertId) {
        $http.put('supersede-dm-app/alerts/discard?alertId=' + alertId)
        .success(function (data) {
            $("#converted" + alertId).html("<strong>Alert " + alertId + " successfully discarded</strong>");
        }).error(function (err) {
            $("#converted" + alertId).html("<strong>Unable to discard alert " + alertId + ": " + err.message + "</strong>");
        });
    };

    function getUserRequests(alertId) {
        $http.get('supersede-dm-app/alerts/userrequests?alertId=' + alertId)
        .success(function (data) {
            for (var i = 0; i < data.length; i++) {
                $scope.userRequests.push(data[i]);
            }
        }).error(function (err) {
            alert("Unable to retrieve user requests for alert " + alertId + ": " + err.message);
        });
    }

    function getAvailableAlerts() {
        $http.get('supersede-dm-app/alerts/biglist').success(function (data) {
            var alerts = data;
            console.log("Alerts:");
            console.log(alerts);

            for (var i = 0; i < alerts.length; i++) {
                getUserRequests(alerts[i].id);
            }
        }).error(function (err) {
            alert("Unable to retrieve alerts: " + err.message);
        });
    }

    getAvailableAlerts();
});