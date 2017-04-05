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

    var dependencies = {};
    var properties = [];

    var currentRequirementIndex = 0;
    var currentRequirementId;
    var processId = $location.search().processId;

    $scope.requirements = [];

    function getAvailableDependencies() {
        var availableDependencies = [];

        for (var i = 0; i < $scope.requirements.length; i++) {
            if (i != currentRequirementIndex) {
                availableDependencies.push($scope.requirements[i]);
            }
        }

        return availableDependencies;
    }

    function fillDependenciesGrid() {
        var availableRequirements = {
            datatype: "json",
            datafields: [
                { name: 'requirementId' },
                { name: 'name' },
                { name: 'description' }
            ],
            localdata: getAvailableDependencies(currentRequirementIndex)
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

    function fillPropertiesGrid() {
        var requirementProperties = {
            datatype: "json",
            datafields: [
                { name: 'propertyName' },
                { name: 'propertyValue' }
            ],
            localdata: properties
        };

        var dataAdapter = new $.jqx.dataAdapter(requirementProperties);

        $("#properties").jqxGrid({
            width: '100%',
            altrows: true,
            autoheight: true,
            pageable: true,
            source: dataAdapter,
            columns: [
                { text: 'Name', datafield: 'propertyName', width: '50%' },
                { text: 'Value', datafield: 'propertyValue', width: '50%' }
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

    function getRequirementProperties() {
        $http.get('supersede-dm-app/processes/requirements/properties?processId=' + processId + '&requirementId=' + currentRequirementId)
        .success(function (data) {
            properties = data;
            fillPropertiesGrid();
        }).error(function (err) {
            alert(err.message);
        });
    }

    function loadCurrentRequirement() {
        $scope.currentRequirement = $scope.requirements[currentRequirementIndex];
        currentRequirementId = $scope.currentRequirement.requirementId;
        fillDependenciesGrid();
        getRequirementProperties();
        $("#requirement_status").html("");
        $("#property_status").html("");
    }

    $scope.goToNextRequirement = function () {
        saveDependencies();
    };

    $scope.submitDependencies = function () {
        saveDependencies();
        $http({
            url: "supersede-dm-app/processes/requirements/dependencies/submit",
            params: { processId: processId, requirementId: currentRequirementId, dependencies: dependencies[currentRequirementId] },
            method: 'POST'
        }).success(function () {
            if (lastRequirement()) {
                $location.url('supersede-dm-app/process?processId=' + processId);
            }
            else {
                currentRequirementIndex++;
                loadCurrentRequirement();
            }
        }).error(function (err) {
            alert(err.message);
        });
    };

    $scope.emptyRequirements = function () {
        return $scope.requirements.length === 0;
    };

    $scope.lastRequirement = function () {
        if (currentRequirementIndex == $scope.requirements.length - 1) {
            return true;
        }
        else if ($scope.requirements.length == 1 && currentRequirementIndex === 0) {
            return true;
        }
        else {
            return false;
        }
    };

    $scope.addProperty = function () {
        var propertyName = $("#property_name").val();
        var propertyValue = $("#property_value").val();

        if (propertyName === undefined || propertyValue === undefined || propertyName === "" || propertyValue === "") {
            $("#property_status").html("<strong>Unable to save the given property: both the key and the value must not be empty!</strong>");
        }
        else {
            $http({
                url: "supersede-dm-app/processes/requirements/property/submit",
                params: { processId: processId, requirementId: currentRequirementId, propertyName: propertyName, propertyValue: propertyValue },
                method: 'POST'
            }).success(function () {
                $("#property_status").html("<strong>Property successfully saved!</strong>");

                // Update the grid containing properties
                getRequirementProperties();

                // Clear the content of the two input fields
                $("#property_name").val("");
                $("#property_value").val("");
            }).error(function (err) {
                $("#property_status").html("<strong>Unable to save the given property: " + err.message + "</strong>");
            });
        }
    };

    $scope.updateRequirement = function () {
        var requirement = {};
        requirement.requirementId = currentRequirementId;
        requirement.name = $("#current_requirement_name").val();
        requirement.description = $("#current_requirement_description").val();

        $http({
            url: "supersede-dm-app/requirement",
            data: requirement,
            method: 'PUT'
        }).success(function () {
            $("#requirement_status").html("<strong>Requirement successfully updated!</strong>");
        }).error(function (err) {
            $("#requirement_status").html("<strong>Unable to update the given requirement: " + err.message + "</strong>");
        });
    };

    $http.get('supersede-dm-app/processes/details?processId=' + processId)
    .success(function (data) {
        $scope.processName = data.name;
    }).error(function (err) {
        alert(err.message);
    });

    $http.get('supersede-dm-app/processes/requirements/list?processId=' + processId)
    .success(function (data) {
        $scope.requirements = data;
        loadCurrentRequirement();
    }).error(function (err) {
        alert(err.message);
    });
});