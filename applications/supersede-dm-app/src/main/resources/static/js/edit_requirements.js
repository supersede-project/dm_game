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

    // Get all the requirements that can be set as dependencies of the current requirement
    function getAvailableDependencies() {
        var availableDependencies = [];

        for (var i = 0; i < $scope.requirements.length; i++) {
            // A requirement can't depend on itself
            if (i != currentRequirementIndex) {
                availableDependencies.push($scope.requirements[i]);
            }
        }

        return availableDependencies;
    }

    // Fill the jqxGrid containing the requirements that can be set as dependencies of
    // the current requirement
    function fillDependenciesGrid() {
        var source = {
            datatype: "json",
            datafields: [
                { name: 'requirementId' },
                { name: 'name' },
                { name: 'description' }
            ],
            localdata: getAvailableDependencies(currentRequirementIndex)
        };

        var dataAdapter = new $.jqx.dataAdapter(source);

        // Deselect requirements set as dependencies of the previous requirement, because
        // the same jqxGrid is used for all requirements.
        $("#dependencies").jqxGrid('clearselection');
        $("#dependencies").jqxGrid('refresh');

        $("#dependencies").jqxGrid({
            width: '100%',
            selectionmode: 'checkbox',
            altrows: true,
            autoheight: true,
            autorowheight: true,
            pageable: true,
            source: dataAdapter,
            columns: [
                { text: 'Id', datafield: 'requirementId', width: '15%' },
                { text: 'Name', datafield: 'name', width: '25%' },
                { text: 'Description', datafield: 'description' }
            ]
        });
    }

    // Get the properties of the current requirement
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

    // Save the dependencies of the current requirement in a local variable.
    // The dependencies for all requirements will be sent to the server only when the
    // dependencies for all the requirements have been specified.
    function saveDependencies() {
        var selectedRequirements = $("#dependencies").jqxGrid("selectedrowindexes");
        dependencies[currentRequirementId] = [];

        for (var i = 0; i < selectedRequirements.length; i++) {
            var dependencyId = $("#dependencies").jqxGrid("getrowdata", selectedRequirements[i]).requirementId;
            dependencies[parseInt(currentRequirementId)].push(parseInt(dependencyId));
        }
    }

    // Get the properties of the current requirement
    function getRequirementProperties() {
        $http.get('supersede-dm-app/processes/requirements/properties?processId=' + processId + '&requirementId=' + currentRequirementId)
        .success(function (data) {
            properties = data;
            // Fill the corresponding jqxGrid
            fillPropertiesGrid();
        }).error(function (err) {
            alert(err.message);
        });
    }

    // Load information about the current requirement
    function loadCurrentRequirement() {
        $scope.currentRequirement = $scope.requirements[currentRequirementIndex];
        currentRequirementId = $scope.currentRequirement.requirementId;

        // Force the content change of the textarea containing the requirement description
        $("#current_requirement_description").val($scope.currentRequirement.description);
        // XXX: introducing ng-model binding makes the following code useless
        //$("#current_requirement_name".val($scope.currentRequirement.name));

        fillDependenciesGrid();
        getRequirementProperties();
        $("#requirement_status").html("");
        $("#property_status").html("");
    }

    // Go to the page for the next requirement
    $scope.goToNextRequirement = function () {
        saveDependencies();
    };

    // Submit the dependencies for all the requirements
    $scope.submitDependencies = function () {
        // Save the dependencies of the last requirement
        saveDependencies();
        // Submit the dependencies
        $http({
            url: "supersede-dm-app/processes/requirements/dependencies/submit",
            params: { processId: processId, requirementId: currentRequirementId, dependencies: dependencies[currentRequirementId] },
            method: 'POST'
        }).success(function () {
            if ($scope.lastRequirement()) {
                // Go back to the home page of the process after the last requirement
                $location.url('supersede-dm-app/process?processId=' + processId);
            }
            else {
                // Go to the next requirement
                currentRequirementIndex++;
                loadCurrentRequirement();
            }
        }).error(function (err) {
            alert(err.message);
        });
    };

    // Tell if there are no requirements in the current process
    $scope.emptyRequirements = function () {
        return $scope.requirements.length === 0;
    };

    // Tell if the current requirement is the last one
    $scope.lastRequirement = function () {
        if (currentRequirementIndex == $scope.requirements.length - 1) {
            return true;
        }
        // TODO: why is this needed?
        else if ($scope.requirements.length == 1 && currentRequirementIndex === 0) {
            return true;
        }
        else {
            return false;
        }
    };

    // Add a property to the current requirement
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

    // Submit updates to the current requirement
    $scope.updateRequirement = function () {
        
        //update local requirement before submitting it
    	//XXX: no need to update name thanks to ng-model
    	//$scope.currentRequirement.name = $("#current_requirement_name").val();
		$scope.currentRequirement.description = $("#current_requirement_description").val();

        $http({
            url: "supersede-dm-app/requirement",
            data: $scope.currentRequirement,
            method: 'PUT'
        }).success(function () {
            $("#requirement_status").html("<strong>Requirement successfully updated!</strong>");
        }).error(function (err) {
            $("#requirement_status").html("<strong>Unable to update the given requirement: " + err.message + "</strong>");
        });
    };

    // Get details about the current process
    $http.get('supersede-dm-app/processes/details?processId=' + processId)
    .success(function (data) {
        $scope.processName = data.name;
    }).error(function (err) {
        alert(err.message);
    });

    // Get the list of requirements of the current process
    $http.get('supersede-dm-app/processes/requirements/list?processId=' + processId)
    .success(function (data) {
        $scope.requirements = data;
        loadCurrentRequirement();
    }).error(function (err) {
        alert(err.message);
    });
});