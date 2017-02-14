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

app.controllerProvider.register('results', function($scope, $http, $location) {
    var gameId = $location.search().gameId;
    var results = [];

    function getResults() {
        $http.get("supersede-dm-app/garp/game/calc?gameId=" + gameId)
        .success(function(data) {
            results = data;
            console.log("results:");
            console.log(results);
        }).error(function(err){
            alert(err.message);
        });
    }

    getResults();
});