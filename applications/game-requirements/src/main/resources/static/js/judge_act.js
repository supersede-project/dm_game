var app = angular.module('w5app');

app.controllerProvider.register('judge_act', function($scope, $http, $location) {
    
	$scope.judgeActId = $location.search()['judgeActId'];
    $scope.judgeAct = undefined;
	$scope.requirementsChoices = [];
	$scope.selectedRequirementsChoice = {selected:4};
	$scope.playerMoves = [];
	
	$http.get('game-requirements/judgeact/' + $scope.judgeActId)
	.success(function(data) {
		$scope.judgeAct = data;
		
		 $http.get('game-requirements/playermove/requirementsmatrixdata/' + $scope.judgeAct.requirementsMatrixData.requirementsMatrixDataId)
			.success(function(data) {
				for(var i = 0; i < data.length; i++)
				{
					$scope.playerMoves.push(data[i]);
				}
			});
	});
    
	 $http.get('game-requirements/requirementchoice')
		.success(function(data) {
			$scope.requirementsChoices.length = 0;
			for(var i = 0; i < data.length; i++)
			{
				$scope.requirementsChoices.push(data[i]);
			}
		});
	 
	 $scope.insertJudgeVote = function(judgeVote){
		 $http.put('game-requirements/judgeact/' + $scope.judgeActId + '/vote/' + judgeVote)
	    	.success(function(data) {
	    		$location.url('/game-requirements/judge_acts');
    	});
	 };	
});