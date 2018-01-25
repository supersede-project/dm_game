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

app.controllerProvider
		.register(
				'process',
				function($scope, $http, $location) {

					var processId = $location.search().processId;

					// Get the users of the process
					$http
							.get(
									'supersede-dm-app/processes/users/list/detailed?processId='
											+ processId)
							.success(
									function(data) {
										var source = {
											datatype : "json",
											datafields : [ {
												name : 'userId'
											}, {
												name : 'name'
											}, {
												name : 'email'
											} ],
											localdata : data
										};
										var dataAdapter = new $.jqx.dataAdapter(
												source);
										$("#users")
												.jqxGrid(
														{
															width : '100%',
															altrows : true,
															autoheight : true,
															pageable : true,
															source : dataAdapter,
															/** AG1263 */
															showstatusbar : true,
															renderstatusbar : function(
																	statusbar) {
																// appends
																// buttons to
																// the status
																// bar.
																var container = $("<div style='overflow: hidden; position: relative; margin: 5px;'></div>");
																var exportButton = $("<button>Add User</button>");
																container
																		.append(exportButton);
																var requiredRow = "<strong> You must specify at least 1 user </strong>";
																var datainformations = $(
																		"#users")
																		.jqxGrid(
																				"getdatainformation");
																var rowscounts = datainformations.rowscount;
																if (rowscounts == 0) {
																	container
																			.append(requiredRow);
																	$scope.usersWarning = "[You must select at least 1 user]"
																}
																statusbar
																		.append(container);
																exportButton
																		.jqxButton({
																			width : 80,
																			height : 25
																		});
																// add new row.
																exportButton
																		.click(function(
																				event) {
																			$location
																					.url("supersede-dm-app/import_users?processId="
																							+ processId);
																			$scope
																					.$apply();
																		});
															},
															/** end AG1263 */
															columns : [
																	{
																		text : 'Id',
																		datafield : 'userId',
																		width : '20%'
																	},
																	{
																		text : 'Name',
																		datafield : 'name',
																		width : '40%'
																	},
																	{
																		text : 'Email',
																		datafield : 'email',
																		width : '40%'
																	} ]
														});
									}).error(function(err) {
								alert(err.message);
							});

					// Get the criteria of the process
					$http
							.get(
									"supersede-dm-app/processes/criteria/list/detailed?processId="
											+ processId)
							.success(
									function(data) {
										var source = {
											datatype : "json",
											datafields : [ {
												name : 'criterionId'
											}, {
												name : 'name'
											}, {
												name : 'description'
											} ],
											localdata : data
										};
										var dataAdapter = new $.jqx.dataAdapter(
												source);
										$("#criteria")
												.jqxGrid(
														{
															width : '100%',
															altrows : true,
															autoheight : true,
															autorowheight : true,
															pageable : true,
															source : dataAdapter,
															/** AG1263 */
															showstatusbar : true,
															renderstatusbar : function(
																	statusbar) {
																// appends
																// buttons to
																// the status
																// bar.
																var container = $("<div style='overflow: hidden; position: relative; margin: 5px;'></div>");
																var exportButton = $("<button>Add Criteria</button>");
																container
																		.append(exportButton);
																var requiredRow = "<strong> You must specify at least 1 criteria </strong>";
																var datainformations = $(
																		"#criteria")
																		.jqxGrid(
																				"getdatainformation");
																var rowscounts = datainformations.rowscount;
																if (rowscounts == 0) {
																	container
																			.append(requiredRow);
																	$scope.criteriaWarning = "[You must select at least 1 criteria]";
																}
																statusbar
																		.append(container);
																exportButton
																		.jqxButton({
																			width : 100,
																			height : 25
																		});
																// add new row.
																exportButton
																		.click(function(
																				event) {
																			$location
																					.url("supersede-dm-app/import_criteria?processId="
																							+ processId);
																			$scope
																					.$apply();
																		});
															},
															/** end AG1263 */
															columns : [
																	{
																		text : 'Id',
																		datafield : 'criterionId',
																		width : '15%'
																	},
																	{
																		text : 'Name',
																		datafield : 'name',
																		width : '25%'
																	},
																	{
																		text : 'Description',
																		datafield : 'description',
																		width : '60%'
																	} ]
														});
									}).error(function(err) {
								alert(err.message);
							});

					// Get the requirements of the process
					$http
							.get(
									"supersede-dm-app/processes/requirements/list?processId="
											+ processId)
							.success(
									function(data) {
										var source = {
											datatype : "json",
											datafields : [ {
												name : 'requirementId'
											}, {
												name : 'name'
											}, {
												name : 'description'
											} ],
											localdata : data
										};

										var linkrenderer = function(row,
												column, value) {
											var httpIndex = value
													.indexOf('http://');
											if (httpIndex != -1) {
												var text = value.substring(0,
														httpIndex);
												var link = value
														.substring(httpIndex);
												return "<div style='overflow-y:auto; word-wrap:break-word; white-space:normal; scroll:auto;'>"
														+ text
														+ " - <a href='"
														+ link
														+ "'> Link to JIRA </a></div>";
											}

											return value;
										}

										var dataAdapter = new $.jqx.dataAdapter(
												source);
										$("#requirements")
												.jqxGrid(
														{
															width : '100%',
															altrows : true,
															autoheight : true,
															autorowheight : true,
															sortable : true,
															pageable : true,
															source : dataAdapter,
															/** AG1263 */
															showstatusbar : true,
															renderstatusbar : function(
																	statusbar) {
																// appends
																// buttons to
																// the status
																// bar.
																var container = $("<div style='overflow: hidden; position: relative; margin: 5px;'></div>");
																var requiredRow = "<strong> It is not recommended to start a process with less than 2 requirements </strong>";
																var datainformations = $(
																		"#requirements")
																		.jqxGrid(
																				"getdatainformation");
																var rowscounts = datainformations.rowscount;
																if (rowscounts < 2) {
																	container
																			.append(requiredRow);
																	$scope.requirementWarning = "[You must select at least 2 requirements]";
																}
																statusbar
																		.append(container);
															},
															/** end AG1263 */
															columns : [
																	{
																		text : 'Id',
																		datafield : 'requirementId',
																		width : '15%'
																	},
																	{
																		text : 'Name',
																		datafield : 'name',
																		width : '25%'
																	},
																	{
																		text : 'Description',
																		datafield : 'description',
																		width : '60%',
																		cellsrenderer : linkrenderer
																	}, ]
														});
									}).error(function(err) {
								alert(err.message);
							});

					// GOT FROM dm-garp-ui --> home.js
					$http
							.get(
									'supersede-dm-app/garp/game/games?roleName=Supervisor&processId='
											+ processId)
							.success(
									function(data) {
										var source = {
											datatype : "json",
											datafields : [ {
												name : 'name'
											}, {
												name : 'date'
											}, {
												name : 'status'
											}, {
												name : 'id'
											} ],
											localdata : data
										};
										var cellsrenderer = function(row,
												columnfield, value,
												defaulthtml, columnproperties) {
											var r = '<div class="jqx-grid-cell-left-align" style="margin-top: 4px; margin-bottom: 4px;">';
											r = r
													.concat('<jqx-button jqx-width="110" jqx-height="25" style="margin-left: 10px; margin-right: 10px;" ');
											r = r
													.concat('ng-click="gameDetails('
															+ value
															+ ')">Details</jqx-button></div>');
											return r;
										};
										var dataAdapter = new $.jqx.dataAdapter(
												source);
										$("#supervisorGames")
												.jqxGrid(
														{
															width : '100%',
															autoheight : true,
															pageable : true,
															altrows : true,
															source : dataAdapter,
															rowsheight : 32,
															/** AG1263 */
															showstatusbar : true,
															renderstatusbar : function(
																	statusbar) {
																// appends
																// buttons to
																// the status
																// bar.
																var container = $("<div style='overflow: hidden; position: relative; margin: 5px;'></div>");
																var exportButton = $("<button>New Game</button>");
																container
																		.append(exportButton);
																var requiredRow = "<strong> No games available</strong>";
																var datainformations = $(
																		"#supervisorGames")
																		.jqxGrid(
																				"getdatainformation");
																var rowscounts = datainformations.rowscount;
																if (rowscounts == 0) {
																	container
																			.append(requiredRow);
																	$scope.gamesWarning = "[No games available]";
																}
																statusbar
																		.append(container);
																exportButton
																		.jqxButton({
																			width : 100,
																			height : 25
																		});
																// add new row.
																exportButton
																		.click(function(
																				event) {
																			$location
																					.url("supersede-dm-app/garp/create_game?processId="
																							+ processId);
																			$scope
																					.$apply();
																		});
															},
															/** end AG1263 */
															columns : [
																	{
																		text : 'Name',
																		datafield : 'name',
																		width : '50%',
																		align : 'center',
																		cellsalign : 'center'
																	},
																	{
																		text : 'Date',
																		datafield : 'date',
																		width : '25%',
																		align : 'center',
																		cellsalign : 'center'
																	},
																	{
																		text : 'Status',
																		datafield : 'status',
																		width : '10%',
																		align : 'center',
																		cellsalign : 'center'
																	},
																	{
																		text : '',
																		datafield : 'id',
																		width : '15%',
																		align : 'center',
																		cellsalign : 'center',
																		cellsrenderer : cellsrenderer
																	} ]
														// /supersede-dm-app/garp/create_game?processId=5580
														});
									}).error(function(err) {
								alert(err.message);
							});

					// Get the activities of the process
					function loadActivities() {
						$http
								.get(
										'supersede-dm-app/processes/available_activities?processId='
												+ processId)
								.success(
										function(data) {
											$("#actionsList").jqxListBox(
													'clear');
											$("#actionsList")
													.jqxListBox(
															{
																source : data,
																width : 700,
																height : 250,
																renderer : function(
																		index,
																		label,
																		value) {
																	var datarecord = data[index];
																	if (datarecord === undefined) {
																		return "";
																	}
																	console
																			.log(datarecord);
																	var imgurl = 'supersede-dm-app/img/process.png';
																	var img = '<img height="50" width="50" src="'
																			+ imgurl
																			+ '"/>';
																	var table = '<table style="min-width: 130px">'
																			+ '<tr><td style="width: 40px">'
																			+ img
																			+ '</td><td>'
																			+ datarecord.methodName
																			+ '</td></tr></table>';
																	return table;
																}
															});
										}).error(function(err) {
									alert(err.message);
								});
					}

					// Update the details of the process
					function updateProcessDetails(process) {
						// The jqxGrid requires an array: create one and add the
						// process to it
						processes = [];
						processes.push(process);
						$scope.processName = process.name;
						var source = {
							datatype : "json",
							datafields : [
							// { name: "name" },
							{
								name : "objective"
							}, {
								name : "status"
							}, {
								name : "phaseName"
							} ],
							localdata : processes
						};
						var dataAdapter = new $.jqx.dataAdapter(source);
						$("#process_details").jqxGrid({
							width : '75%',
							autoheight : true,
							source : dataAdapter,
							columns : [
							// { text: 'Name', datafield: 'name', width: '25%'
							// },
							{
								text : 'Objective',
								datafield : 'objective',
								width : '25%'
							}, {
								text : 'Status',
								datafield : 'status',
								width : '25%'
							}, {
								text : 'Phase',
								datafield : 'phaseName',
								width : '50%'
							} ]
						});
					}

					// Get the details of the process
					$http.get(
							'supersede-dm-app/processes/details?processId='
									+ processId).success(function(data) {
						updateProcessDetails(data);
					}).error(function(err) {
						alert(err.message);
					});

					loadActivities();

					$("#btnPrevPhase").jqxButton({
						width : 60,
						height : 250
					});

					// Go to the previous phase of the process
					$("#btnPrevPhase").on(
							'click',
							function() {
								$http.get(
										'supersede-dm-app/processes/prev?processId='
												+ processId).success(
										function(data) {
											updateProcessDetails(data);
											loadActivities();
										}).error(function(error) {
									alert(error.message);
								});
							});

					$("#btnNextPhase").jqxButton({
						width : 60,
						height : 250
					});

					// Go to the next phase of the process
					$("#btnNextPhase").on(
							'click',
							function() {
								$http.get(
										'supersede-dm-app/processes/next?processId='
												+ processId).success(
										function(data) {
											updateProcessDetails(data);
											loadActivities();
										}).error(function(error) {
									alert(error.message);
								});
							});

					// When an action is selected, go to the corresponding page
					$('#actionsList').on(
							'select',
							function(event) {
								var args = event.args;
								var item = $('#actionsList').jqxListBox(
										'getItem', args.index);
								if (item !== null) {
									$location.url("supersede-dm-app/"
											+ item.originalItem.entryUrl
											+ "?processId=" + processId);
									$scope.$apply();
								}
							});

					// Go back to the DM game home
					$scope.home = function() {
						$location.url("supersede-dm-app/home");
					}
				});

$('.col-expander-users').click(function() {
	$('.col-content-users').slideToggle('slow');
});
$('.col-expander-criteria').click(function() {
	$('.col-content-criteria').slideToggle('slow');
});
$('.col-expander-requirements').click(function() {
	$('.col-content-requirements').slideToggle('slow');
});
$('.col-expander-games').click(function() {
	$('.col-content-games').slideToggle('slow');
});
