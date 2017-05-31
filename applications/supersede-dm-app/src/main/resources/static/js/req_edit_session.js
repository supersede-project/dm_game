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
app.controllerProvider.register('req_edit_session', function($scope, $http, $location) {

    var dependencies = {};
    var properties = [];
    var requirements = [];
    var currentRequirementIndex = 0;
    var currentRequirementId;
    var processId = $location.search().processId;

    //////// Dependencies

    // Set the list of requirements that can be set as dependencies of the currently selected one
    function getAvailableDependencies() {
        var availableDependencies = [];

        for (var i = 0; i < requirements.length; i++) {
            if (i != currentRequirementIndex) {
                availableDependencies.push(requirements[i]);
            }
        }

        return availableDependencies;
    }

    // Automatically select the dependencies of the current requirement in the jqxGrid
    function getRequirementDependencies() {
        $http.get('supersede-dm-app/processes/requirements/dependencies/list?processId=' + processId + "&requirementId=" + currentRequirementId)
        .success(function (data) {
            var currentDependencies = data;
            // Get the number of dependencies present in the jqxGrid
            var dependenciesRows = $("#dependencies").jqxGrid("getrows").length;

            if (currentDependencies === undefined)
            {
                return;
            }

            for (var i = 0; i < dependenciesRows; i++) {
                var added = false;
                // Get the dependency at the given index
                var currentDependency = $("#dependencies").jqxGrid("getrowdatabyid", i);

                for (var j = 0; j < currentDependencies.length; j++) {
                    if (currentDependencies[j] === currentDependency.requirementId) {
                        // Automatically select the dependency at the given index if already added
                        $("#dependencies").jqxGrid("selectrow", i);
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    // Deselect the dependency at the given index if not already added
                    $("#dependencies").jqxGrid("unselectrow", i);
                }
            }
        }).error(function (err) {
            alert(err.message);
            // Refresh the list of requirements
            loadRequirements();
        });
    }

    // Fill the jqxGrid for the dependencies of the current requirement
    function fillDependenciesGrid() {
        var availableRequirements = {
            datatype: "json",
            datafields: [
                { name: 'requirementId' },
                { name: 'name' },
                { name: 'description' }
            ],
            localdata: getAvailableDependencies()
        };

        var dataAdapter = new $.jqx.dataAdapter(availableRequirements);

        $("#dependencies").jqxGrid('clearselection');
        $("#dependencies").jqxGrid('refresh');

        $("#dependencies").jqxGrid({
            width: '96%',
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

        // Automatically select the dependencies of the current requirement in the jqxGrid
        getRequirementDependencies();
    }

    // Save the selected dependencies in a local variable
    function saveDependencies() {
        var selectedRequirements = $("#dependencies").jqxGrid("selectedrowindexes");
        dependencies[currentRequirementId] = [];

        for (var i = 0; i < selectedRequirements.length; i++) {
            var dependencyId = $("#dependencies").jqxGrid("getrowdata", selectedRequirements[i]).requirementId;
            dependencies[parseInt(currentRequirementId)].push(parseInt(dependencyId));
        }
    }

    // Perform a request to submit the saved dependencies for the current requirement
    $scope.submitDependencies = function () {
        saveDependencies();
        $http({
            url: "supersede-dm-app/processes/requirements/dependencies/submit",
            params: { processId: processId, requirementId: currentRequirementId,
                dependencies: dependencies[parseInt(currentRequirementId)]
            },
            method: 'POST'
        }).success(function () {
            $("#submitted").html("<strong>Dependencies successfully saved!</strong>");
            fillDependenciesGrid();
        }).error(function (err) {
            $("#submitted").html("<strong>Unable to save the dependencies: " + err.message + "</strong>");
        });
    };

    //////// Properties

    // Fill the jqxGrid containing the properties of the selected requirement
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
            width: '96%',
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

    // Get the properties of the selected requirement
    function getCurrentRequirementProperties() {
        $http.get('supersede-dm-app/processes/requirements/properties?processId=' + processId +
            '&requirementId=' + currentRequirementId)
        .success(function (data) {
            properties = data;
            fillPropertiesGrid();
        }).error(function (err) {
            alert(err.message);
        });
    }

    // Add a property to the selected requirement according to the input fields
    $scope.addProperty = function () {
        var propertyName = $("#property_name").val();
        var propertyValue = $("#property_value").val();

        if (propertyName === undefined || propertyValue === undefined || propertyName === "" || propertyValue === "") {
            $("#property_status").html("<strong>Unable to save the given property: both the key and the value must not be empty!</strong>");
        }
        else {
            $http({
                url: "supersede-dm-app/processes/requirements/property/submit",
                params: {
                    processId: processId, requirementId: currentRequirementId,
                    propertyName: propertyName, propertyValue: propertyValue
                },
                method: 'POST'
            }).success(function () {
                $("#property_status").html("<strong>Property successfully saved!</strong>");

                // Update the grid containing properties
                getCurrentRequirementProperties();

                // Clear the content of the two input fields
                $("#property_name").val("");
                $("#property_value").val("");
            }).error(function (err) {
                $("#property_status").html("<strong>Unable to save the given property: " + err.message + "</strong>");
            });
        }
    };

    //////// Requirements

    // Get the details of the current requirement
    function loadCurrentRequirement() {
        $http.get('supersede-dm-app/requirement/' + currentRequirementId)
        .success(function (data) {
            $scope.currentRequirement = requirements[currentRequirementIndex];

            // Force the content change of the textarea containing the requirement description
            // TODO: check why it is not automatically updated
            $("#current_requirement_description").val($scope.currentRequirement.description);

            // Update the content of the jqxGrid containing the requirement dependencies
            fillDependenciesGrid();

            // Update the content of the jqxGrid containing the requirement properties
            getCurrentRequirementProperties();

            // Clear content of paragraphs used to show messages
            $("#submitted").html("");
            $("#requirement_status").html("");
            $("#property_status").html("");
        }).error(function (err) {
            alert(err.message);
            loadRequirements();
        });
    }

    // Update the name and the description of the selected requirement
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
            // Update the list of requirements of the current process
            loadRequirements();
        }).error(function (err) {
            $("#requirement_status").html("<strong>Unable to update the given requirement: " + err.message + "</strong>");
        });
    };

    // Create a new requirement asking for a name
    $scope.newRequirement = function () {
        var reqName = prompt("Requirement name", "");
        if (typeof reqName === 'undefined' || reqName === null) {
            return;
        }
        $http({
            url: "supersede-dm-app/processes/requirements/new?processId=" + processId + "&name=" + reqName,
            method: 'POST'
        }).success(function (data) {
            // Update the list of requirements of the current process
            loadRequirements();
        }).error(function (err) {
            alert(err.message);
            loadRequirements();
        });
    };

    // Delete the selected requirement
    $scope.deleteRequirement = function () {
        var selectedRequirement = $('#requirements-listbox').jqxListBox('getSelectedItem');
        var requirementId = selectedRequirement.originalItem.requirementId;

        $http.post('supersede-dm-app/processes/requirements/delete?requirementId=' + requirementId)
        .success(function (data) {
            // Update the list of requirements of the current process
            loadRequirements();
        }).error(function (err) {
            alert(err.message);
            // Update the list of requirements of the current process
            loadRequirements();
        });
    };

    // Get the array index of the currently selected requirement
    function getCurrentRequirementIndex() {
        for (var i = 0; i < requirements.length; i++) {
            if (currentRequirementId === requirements[i].requirementId) {
                return i;
            }
        }
    }

    // Get the list of requirements in the current process
    function loadRequirements() {
        $http.get('supersede-dm-app/processes/requirements/list?processId=' + processId)
        .success(function (data) {
            // Clear the list of requirements
            $("#requirements-listbox").jqxListBox('clear');
            requirements = data;
            $('#mainSplitter').jqxSplitter({ width: '100%', height: '1500px', panels: [{ size: 300 }] });

            var source = {
                localdata: data,
                datatype: "array"
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            $('#requirements-listbox').jqxListBox({
                // Automatically select the first one
                selectedIndex: 0,
                source: dataAdapter,
                // Show only the requirement name
                displayMember: "name",
                valueMember: "id",
                width: '100%',
                renderer: function (index, label, value) {
                    return requirements[index].name;
                }
            });

            currentRequirementIndex = 0;
            currentRequirementId = requirements[0].requirementId;
            // Show the details of the currently selected requirement
            loadCurrentRequirement();
        }).error(function (err) {
            alert(err.message);
        });
    }

    // Update requirement details when a new requirement is selected
    $('#requirements-listbox').on('select', function (event) {
        var args = event.args;
        var item = $('#requirements-listbox').jqxListBox('getItem', args.index);

        if (item !== null) {
            // Change the currently selected requirement
            currentRequirementId = item.originalItem.requirementId;
            currentRequirementIndex = getCurrentRequirementIndex();
            // Load the requirement details
            loadCurrentRequirement();
        }
    });

    //////// Process

    // Get the details of the current process
    $http.get('supersede-dm-app/processes/details?processId=' + processId)
    .success(function (data) {
        $scope.processName = data.name;
    }).error(function (err) {
        alert(err.message);
    });

    loadRequirements();
});