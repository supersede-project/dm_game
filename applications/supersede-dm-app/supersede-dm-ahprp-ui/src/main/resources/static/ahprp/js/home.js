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

app.controllerProvider.register('home', function($scope, $http, $rootScope, $location) {
	
	$scope.procId = $location.search().procId;
	
	$scope.dmactions = function(gameId) {
        $location.url('#/supersede-dm-app/ahprp/games').search('procId', procId);
    };
    
    $http.get('/supersede-dm-app/user/current')
    .success(function(data) {
        $scope.user = data;
    });

    $scope.hasRole = function(role){
        if ($rootScope.user && $rootScope.user.authorities)
        {
            for (var i = 0; i < $rootScope.user.authorities.length; i++)
            {
                if ($rootScope.user.authorities[i].authority == "ROLE_" + role)
                {
                    return true;
                }
            }
        }
        return false;
    };
});