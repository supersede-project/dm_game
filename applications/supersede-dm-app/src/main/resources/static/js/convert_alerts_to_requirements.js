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

    $scope.procId = $location.search().procId;

    $scope.convertToRequirement = function (alertId) {
        console.log("Alert " + alertId + " successfully converted to a requirement");
        $("#converted" + alertId).html("<strong>Alert " + alertId + " successfully converted to a requirement</strong>");
    };

    $scope.discard = function (alertId) {
        console.log("Alert " + alertId + " successfully discarded");
        $("#converted" + alertId).html("<strong>Alert " + alertId + " successfully discarded</strong>");
    };

    function getAvailableAlerts() {
        $http.get('supersede-dm-app/alerts/biglist').success(function (data) {
            $scope.alerts = data;
            console.log("Alerts:");
            console.log($scope.alerts);
        });
    }

    getAvailableAlerts();
});