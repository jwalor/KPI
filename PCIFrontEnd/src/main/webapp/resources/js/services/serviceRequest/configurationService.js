'use strict';
angular.module('configurationService',[]).factory('ConfigurationService', ['$http','$q', function($http,$q) {
	
	var server	= 'http://localhost:8080';
//	var server	= 'http://192.168.1.33:8080';
	var dataFactory = {};
	
	dataFactory.get = function (source,action,id) {
		return $http.get(server+source+action+id).then(function(response){
			return  response.data;
		});	
	};
	
		
	dataFactory.sentRequestPost = function (source,action,request) {
		return $http.post(server+source+action, request).then(function(response){
			return  response.data;
		});
	};	
	
	return dataFactory;
}]);