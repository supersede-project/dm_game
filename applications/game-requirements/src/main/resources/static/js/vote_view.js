var app = angular.module('w5app');

app.controllerProvider.register('vote_view', function($scope, $http, $location) {
    
    $scope.playerMoveId = $location.search()['playerMoveId'];
    $scope.playerMove = undefined;
    $scope.requirementsChoices = [];
    $scope.selectedRequirementsChoice = {selected:4};
    
    $http.get('game-requirements-gamification/playermove/' + $scope.playerMoveId)
    .success(function(data) {
        $scope.playerMove = data;
    });
    
     $http.get('game-requirements-gamification/requirementchoice')
        .success(function(data) {
            $scope.requirementsChoices.length = 0;
            for(var i = 0; i < data.length; i++)
            {
                $scope.requirementsChoices.push(data[i]);
            }
        });
     
     $scope.insertPlayerVote = function(playerVote){
         $http.put('game-requirements-gamification/playermove/' + $scope.playerMoveId + '/vote/' + playerVote)
            .success(function(data) {
                $location.url('/game-requirements-gamification/player_moves?gameId=' + data);
        });
     };
});