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
app.controllerProvider.register('req_edit_page', function($scope, $http, $location) {

//    var procId = $location.search().procId;
//    $scope.procId = procId;
//    $scope.activityId = $location.search().activityId;
//    $scope.reqId = $location.search().reqId;
//    console.log( $location.search() );
//    console.log( "Received params: " + $scope.procId + " - " + $scope.activityId + " - " + $scope.reqId );
    var procId = $scope.procId;
    var reqId = $scope.reqId;
    var dependencies = {};
    var properties = [];

//    var currentRequirementIndex = 0;
    var currentRequirementId;

    $scope.requirements = [];
    
    
    function getAvailableDependencies() {
        var availableDependencies = [];

        for (var i = 0; i < $scope.requirements.length; i++) {
        	if( $scope.requirements[i].requirementId != currentRequirementId ) {
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
            localdata: getAvailableDependencies()
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
        $http.get('supersede-dm-app/processes/requirements/properties?procId=' + procId + '&requirementId=' + currentRequirementId)
        .success(function (data) {
            properties = data;
            console.log("properties:");
            console.log(properties);
            fillPropertiesGrid();
        });
    }

    function loadCurrentRequirement() {
        currentRequirementId = $scope.currentRequirement.requirementId;
        fillDependenciesGrid();
        getRequirementProperties();
        $("#requirement_status").html("");
        $("#property_status").html("");
    }

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

    $scope.addProperty = function () {
        var propertyName = $("#property_name").val();
        var propertyValue = $("#property_value").val();

        if (propertyName === undefined || propertyValue === undefined || propertyName === "" || propertyValue === "") {
            $("#property_status").html("<strong>Unable to save the given property: both the key and the value must not be empty!</strong>");
        }
        else {
            $http({
                url: "supersede-dm-app/processes/requirements/property/submit",
                params: { procId: procId, requirementId: currentRequirementId, propertyName: propertyName, propertyValue: propertyValue },
                method: 'POST'
            }).success(function () {
                $("#property_status").html("<strong>Property successfully saved!</strong>");
                // Update the grid containing properties
                getRequirementProperties();
                // Clear the content of the two input fields
                $("#property_name").val("");
                $("#property_value").val("");
            }).error(function (err) {
                $("#property_status").html("<strong>Unable to save the given property!</strong>");
                console.log(err.message);
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
            $("#requirement_status").html("<strong>Unable to update the given requirement!</strong>");
            console.log(err.message);
        });
    };

    $http.get('supersede-dm-app/processes/requirements/list?procId=' + procId + '&reqId=' + reqId)
    .success(function (data) {
        $scope.requirements = data;
        console.log($scope.requirements);
        $http.get('supersede-dm-app/processes/requirements/get?procId=' + procId + '&reqId=' + reqId)
        .success(function (data) {
        	currentRequirementId = data.requirementId;
        	$scope.currentRequirementId = data.requirementId;
        	$scope.currentRequirement = data;
        	loadCurrentRequirement();
        });
    });
});

$(document).ready(function () {
//    $('#mainSplitter').jqxSplitter({ width: 850, height: 480, panels: [{ size: 300 }] });
});