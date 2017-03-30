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

    // Dependencies

    function getAvailableDependencies() {
        var availableDependencies = [];

        for (var i = 0; i < requirements.length; i++) {
            if (i != currentRequirementIndex) {
                availableDependencies.push(requirements[i]);
            }
        }

        return availableDependencies;
    }

    function getRequirementDependencies() {
        $http.get('supersede-dm-app/processes/requirements/dependencies/list?processId=' + processId + "&requirementId=" + currentRequirementId)
        .success(function (data) {
            var currentDependencies = data;
            var dependenciesRows = $("#dependencies").jqxGrid("getrows").length;

            if (currentDependencies === undefined)
            {
                return;
            }

            for (var i = 0; i < dependenciesRows; i++) {
                var added = false;
                var currentDependency = $("#dependencies").jqxGrid("getrowdatabyid", i);

                for (var j = 0; j < currentDependencies.length; j++) {
                    if (currentDependencies[j] === currentDependency.requirementId) {
                        $("#dependencies").jqxGrid("selectrow", i);
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    $("#dependencies").jqxGrid("unselectrow", i);
                }
            }
        }).error(function (err) {
            alert(err.message);
        });
    }

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
            pageable: true,
            source: dataAdapter,
            columns: [
                { text: 'Id', datafield: 'requirementId', width: '15%' },
                { text: 'Name', datafield: 'name', width: '25%' },
                { text: 'Description', datafield: 'description' }
            ]
        });

        getRequirementDependencies();
    }

    function saveDependencies() {
        var selectedRequirements = $("#dependencies").jqxGrid("selectedrowindexes");
        dependencies[currentRequirementId] = [];

        for (var i = 0; i < selectedRequirements.length; i++) {
            var dependencyId = $("#dependencies").jqxGrid("getrowdata", selectedRequirements[i]).requirementId;
            dependencies[parseInt(currentRequirementId)].push(parseInt(dependencyId));
        }
    }

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

    // Properties

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

    // Requirements

    function loadCurrentRequirement() {
        $http.get('supersede-dm-app/requirement/' + currentRequirementId)
        .success(function (data) {
            $scope.currentRequirement = requirements[currentRequirementIndex];

            // Force the content change of the textarea containing the requirement description
            // TODO: check why it is not automatically updated
            $("#current_requirement_description").val($scope.currentRequirement.description);

            fillDependenciesGrid();
            getCurrentRequirementProperties();
            $("#submitted").html("");
            $("#requirement_status").html("");
            $("#property_status").html("");
        }).error(function (err) {
            alert(err.message);
        });
    }

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
            loadRequirements();
        }).error(function (err) {
            $("#requirement_status").html("<strong>Unable to update the given requirement: " + err.message + "</strong>");
        });
    };

    $scope.newRequirement = function () {
        var reqName = prompt("Requirement name", "");
        if (typeof reqName === 'undefined' || reqName === null) {
            return;
        }
        $http({
            url: "supersede-dm-app/processes/requirements/new?processId=" + processId + "&name=" + reqName,
            method: 'POST'
        }).success(function (data) {
            requirements.push(data);
            loadRequirements();
        }).error(function (err) {
            alert(err.message);
        });
    };

    $scope.deleteRequirement = function () {
        var selectedRequirement = $('#requirements-listbox').jqxListBox('getSelectedItem');
        var requirementId = selectedRequirement.originalItem.requirementId;

        $http.post('supersede-dm-app/processes/requirements/delete?requirementId=' + requirementId)
        .success(function (data) {
            loadRequirements();
        }).error(function (err) {
            alert(err.message);
        });
    };

    function getCurrentRequirementIndex() {
        for (var i = 0; i < requirements.length; i++) {
            if (currentRequirementId === requirements[i].requirementId) {
                return i;
            }
        }
    }

    function loadRequirements() {
        $http.get('supersede-dm-app/processes/requirements/list?processId=' + processId)
        .success(function (data) {
            $("#requirements-listbox").jqxListBox('clear');
            requirements = data;
            $('#mainSplitter').jqxSplitter({ width: '100%', height: '1500px', panels: [{ size: 300 }] });

            var source = {
                localdata: data,
                datatype: "array"
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            $('#requirements-listbox').jqxListBox({
                selectedIndex: 0,
                source: dataAdapter,
                displayMember: "name",
                valueMember: "id",
                width: '100%',
                renderer: function (index, label, value) {
                    return data[index].name;
                }
            });
            $('#requirements-listbox').on('select', function (event) {
                var args = event.args;
                var item = $('#requirements-listbox').jqxListBox('getItem', args.index);

                if (item !== null) {
                    currentRequirementId = item.originalItem.requirementId;
                    currentRequirementIndex = getCurrentRequirementIndex();
                    $scope.$apply();
                    loadCurrentRequirement();
                }
            });

            currentRequirementId = requirements[currentRequirementIndex].requirementId;
            loadCurrentRequirement();
        }).error(function (err) {
            alert(err.message);
        });
    }

    // Process

    $http.get('supersede-dm-app/processes/details?processId=' + processId)
    .success(function (data) {
        $scope.processName = data.name;
    }).error(function (err) {
        alert(err.message);
    });

    loadRequirements();
});