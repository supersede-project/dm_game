var app = angular.module('w5app');

app.controllerProvider.register('criterias_leaderboard', function($scope, $http) {
	
	$scope.valutationCriterias = [];
    $scope.criteriaUsers = []; 
    $scope.selectedCriteria = undefined;
    
    $http.get('game-requirements-gamification/criteria')
	.success(function(data) {
		$scope.valutationCriterias.length = 0;
		for(var i = 0; i < data.length; i++)
		{
			$scope.valutationCriterias.push(data[i]);
		}
	});
    	
    $scope.selectedCriteriaChanged = function(){   	
    	$http.get('game-requirements-gamification/user/criteria/' + $scope.selectedCriteria.criteriaId)
		.success(function(data) {
			$scope.criteriaUsers.length = 0;
			for(var i = 0; i < data.length; i++)
			{
				$scope.criteriaUsers.push(data[i]);
			}
		});
    }
});