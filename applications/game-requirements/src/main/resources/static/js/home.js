var app = angular.module('w5app');

app.controllerProvider.register('home', function($scope, $http, $rootScope) {
	
	$scope.loggedUser = $rootScope.user;
	$scope.user = undefined;
	
	$http.get('game-requirements/user/' + $scope.loggedUser.userId)
	.success(function(data) {
		$scope.user = data;
	});
	
	$scope.hasRole = function(role){
		if($scope.user)
		{
			for(var i = 0; i < $scope.user.profiles.length; i++)
			{
				if($scope.user.profiles[i].name == role)
				{
					return true;
				}
			}
		}
		
		return false;
	}

});