var app = angular.module('w5app');

app.controllerProvider.register('leaderboard', function($scope, $http) {
	
    $scope.users = [];
    $scope.usersCount = 0;
       
    $http.get('game-requirements-gamification/user')
	.success(function(data) {
		$scope.users.length = 0;
		for(var i = 0; i < data.length; i++)
		{
			$scope.users.push(data[i]);
		}
		 $scope.usersCount = data.length;
	});
});