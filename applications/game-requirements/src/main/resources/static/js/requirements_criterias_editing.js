var app = angular.module('w5app');

app.controllerProvider.register('requirements_criterias_editing', function($scope, $http) {
		
	$scope.valutationCriterias = [];
	$scope.requirements = [];
	$scope.criteriaName = undefined;
	$scope.criteriaDescription = undefined;
	$scope.requirementName = undefined;
	$scope.requirementDescription = undefined;
	
	$scope.editCriteria = undefined;
	$scope.editCriteriaName = undefined;
	$scope.editCriteriaDescription = undefined;
	
	$scope.editRequirement = undefined;
	$scope.editRequirementName = undefined;
	$scope.editRequirementDescription = undefined;
	
	getCriterias = function () {
	 $http.get('game-requirements-gamification/criteria')
		.success(function(data) {
			for(var i = 0; i < data.length; i++)
			{
				$scope.valutationCriterias.push(data[i]);
			}
		});
	};
	
	getRequirements = function () {
		$http.get('game-requirements-gamification/requirement')
		.success(function(data) {
			for(var i = 0; i < data.length; i++)
			{
				$scope.requirements.push(data[i]);
			}
		});	
	};
	
	getCriterias();
	getRequirements();	 
	 
	// EDIT PART ##################################################################################
	$scope.editFunCriteria = function (criteriaId) {	
		$scope.editCriteria = undefined;
		$scope.editCriteriaName = undefined;
		$scope.editCriteriaDescription = undefined;
		
		$http.get('game-requirements-gamification/criteria/' + criteriaId)
		.success(function(data) {
			$scope.editCriteria = data;
		});	
	};
	
	$scope.saveEditedCriteria = function () {
		$http.put('game-requirements-gamification/criteria/edit/' + $scope.editCriteria.criteriaId + '/name/' + $scope.editCriteriaName + '/description/' + $scope.editCriteriaDescription)
		.success(function(data) {
			$scope.valutationCriterias = [];
			getCriterias();
		});
	};
	
	
	$scope.editFunRequirement = function (requirementId) {	
		$scope.editRequirement = undefined;
		$scope.editRequirementName = undefined;
		$scope.editRequirementDescription = undefined;
		
		$http.get('game-requirements-gamification/requirement/' + requirementId)
		.success(function(data) {
			$scope.editRequirement = data;
		});	
	};
	
	$scope.saveEditedRequirement = function () {
		$http.put('game-requirements-gamification/requirement/edit/' + $scope.editRequirement.requirementId + '/name/' + $scope.editRequirementName + '/description/' + $scope.editRequirementDescription)
		.success(function(data) {
			$scope.requirements = [];
			getRequirements();
		});
	};
	
	// ##################################################################################
	
	
	// NEW PART ##################################################################################	
	$scope.createCriteria = function () {
		 $http.put('game-requirements-gamification/criteria/create/' + $scope.criteriaName + '/description/' + $scope.criteriaDescription)
			.success(function(data) {
				$scope.valutationCriterias = [];
				getCriterias();
				$scope.criteriaName = undefined;
				$scope.criteriaDescription = undefined;
			});
    };
    
    $scope.createRequirement = function () {
		 $http.put('game-requirements-gamification/requirement/create/' + $scope.requirementName + '/description/' + $scope.requirementDescription)
			.success(function(data) {
				$scope.requirements = [];
				getRequirements();
				$scope.requirementName = undefined;
				$scope.requirementDescription = undefined;
			});
   };
   
   // ##################################################################################
   
   // DELETE PART ##################################################################################
   $scope.deleteCriteria = function (criteriaId) {
		 $http.put('game-requirements-gamification/criteria/delete/' + criteriaId)
			.success(function(data) {
				if(data == true){
					$scope.valutationCriterias = [];
					getCriterias();
				}
			});
  };
  
  $scope.deleteRequirement = function (requirementId) {
		 $http.put('game-requirements-gamification/requirement/delete/' + requirementId)
			.success(function(data) {
				if(data == true){
					$scope.requirements = [];
					getRequirements();
				}
			});
};
  
  //##################################################################################
});