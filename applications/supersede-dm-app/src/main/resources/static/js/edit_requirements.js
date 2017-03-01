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
app.controllerProvider.register('edit_requirements', function($scope, $http, $location) {

    var procId = $location.search().procId;
    var dependencies = {};

    $scope.requirements = [];
    $scope.currentRequirementIndex = 0;
    var currentRequirementId;

    function getAvailableDependencies() {
        var availableDependencies = [];

        for (var i = 0; i < $scope.requirements.length; i++) {
            if (i != $scope.currentRequirementIndex) {
                availableDependencies.push($scope.requirements[i]);
            }
        }

        return availableDependencies;
    }

    function fillGrid() {
        var availableRequirements = {
            datatype: "json",
            datafields: [
                { name: 'requirementId' },
                { name: 'name' },
                { name: 'description' }
            ],
            localdata: getAvailableDependencies($scope.currentRequirementIndex)
        };

        var dataAdapter = new $.jqx.dataAdapter(availableRequirements);

        $("#dependencies").jqxGrid('clearselection');
        $("#dependencies").jqxGrid('refresh');

        $("#dependencies").jqxGrid({
            width: '100%',
            selectionmode: 'checkbox',
            altrows: true,
            autoheight: true,
            pageable: true,
            source: dataAdapter,
            columns: [
                { text: 'Id', datafield: 'requirementId', width: '15%' },
                { text: 'Name', datafield: 'name', width: '25%' },
                { text: 'Description', datafield: 'description' }
            ]
        });
    }

    function saveDependencies() {
        var selectedRequirements = $("#dependencies").jqxGrid("selectedrowindexes");
        dependencies[currentRequirementId] = [];

        for (var i = 0; i < selectedRequirements.length; i++) {
            var dependencyId = $("#dependencies").jqxGrid("getrowdata", selectedRequirements[i]).requirementId;
            dependencies[parseInt(currentRequirementId)].push(parseInt(dependencyId));
        }
    }

    $scope.goToNextRequirement = function () {
        saveDependencies();
        $scope.currentRequirementIndex++;
        currentRequirementId = $scope.requirements[$scope.currentRequirementIndex].requirementId;
        fillGrid();
    };

    $scope.submitDependencies = function () {
        saveDependencies();
        console.log("Submit dependencies");
        console.log(dependencies);
        $http({
            url: "supersede-dm-app/processes/requirements/dependencies/submit",
            data: dependencies,
            params: {procId: procId},
            method: 'POST'
        }).success(function () {
            $("#submitted").html("<strong>Dependencies successfully saved!</strong>");
        }).error(function (err) {
            $("#submitted").html("<strong>Unable to save the dependencies!</strong>");
            console.log(err.message);
        });
    };

    $scope.emptyRequirements = function () {
        return $scope.requirements.length === 0;
    };

    $scope.lastRequirement = function () {
        if ($scope.currentRequirementIndex == $scope.requirements.length - 1) {
            return true;
        }
        else if ($scope.requirements.length == 1 && $scope.currentRequirementIndex === 0) {
            return true;
        }
        else {
            return false;
        }
    };

    $scope.addProperty = function () {
        var propertyName = $("#property_name").val();
        var propertyValue = $("#property_value").val();

        if (propertyName === null || propertyValue == null || propertyName === "" || propertyValue === "") {
            $("#property_status").html("<strong>Unable to save the given property: both the key and the value must not be empty!</strong>");
        }
        else {
            $http({
                url: "supersede-dm-app/processes/requirements/property/submit",
                params: { procId: procId, requirementId: currentRequirementId, propertyName: propertyName, propertyValue: propertyValue },
                method: 'POST'
            }).success(function () {
                $("#property_status").html("<strong>Property successfully saved!</strong>");
            }).error(function (err) {
                $("#property_status").html("<strong>Unable to save the given property!</strong>");
                console.log(err.message);
            });
        }
    };

    $http.get('supersede-dm-app/processes/requirements/list?procId=' + procId)
    .success(function (data) {
        $scope.requirements = data;
        currentRequirementId = $scope.requirements[$scope.currentRequirementIndex].requirementId;
        fillGrid();
    });
});