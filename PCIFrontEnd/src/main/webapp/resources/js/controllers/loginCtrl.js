'use strict';
var demo = angular.module('loginApp',['loginService','configurationService','ngRoute','LocalStorageModule']);

demo.config(['localStorageServiceProvider', function(localStorageServiceProvider){
	  localStorageServiceProvider.setPrefix('ls');
	}]);

	demo.config(function($httpProvider){
		
		$httpProvider.interceptors.push(['$q', '$location','localStorageService', function ($q, $location, localStorageService) {
			   return {
			       'request': function (config) {
			           config.headers = config.headers || {};
			           //alert('ahora est� llevando el token ');
			           var token = localStorageService.get('TOKEN');
			           if (token) {
			        	   
			        	   //alert('ahora est� llevando el token ' + token);
			               config.headers.Authorization = 'Bearer ' + token ;
			           }
			           config.headers.Authorization = 'Bearer ' + token;
			           return config;
			       },
			       'responseError': function (response) {
			           if (response.status === 401 || response.status === 403) {
			               $location.path('/signin');
			           }
			           return $q.reject(response);
			       },
			       response: function(res) {
			    	   	  //alert('regresando del login');
			    	      return res;
			       }
			   };
			}]);
	});
demo.config(function($routeProvider) 
	{   
		$routeProvider
		.when("/main", {			
			templateUrl : "page-main.html",
			controller  : "appCtrl"
		})
		.when("/error", {
			templateUrl : "error.html",
			controller 	: "loginCtrl"
		});
	});

demo.controller('loginCtrl', ['$scope','LoginFactory','$location','localStorageService','$window',
                                       function($scope, LoginFactory, $location, localStorageService, $window){
	
	
	$scope.validateLogin = function()
		{   
		
		// alert($scope.user.name + ' ' +  $scope.user.password );			
		 //$window.location.href='page-main.html';
		 var source = '/KPIFrontEnd';
		 var action	= '/login';
		 
		 $scope.request = {};	
		 $scope.request.user = $scope.user.name; 
		 $scope.request.password = $scope.user.password; 
		 
		 localStorageService.clearAll();
		// $scope.msgVisibility  = false;
		 LoginFactory.validateUser(source,action,$scope.request).then(function(response){
			
			 var token = localStorageService.get('TOKEN');
			 alert(token);
			 
			 if (token) {
				 //$location.path('/main'); 
				 var form = document.createElement("form");
			     form.id = "gpsForm";
			     form.method = "POST";// /GPSFront-1.0.0
			     form.action = "/KPIFrontEnd/page-main.html";			   
			     var input = document.createElement('input');
			     input.type = 'hidden';
			     input.name = 'Bearer';
			     input.value = token; //'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJKb2UiLCJpYXQiOjE0NjA0Nzk2ODN9.vVau4WRzeJnnAglr9dM9s91aCo6Ijh8iNeMuZPtYSfQz-nO_gNNJBLsMzDou4A4gofhzpgtnYFE_JzI1A1CQpQ';
			     var inputRefresh = document.createElement('input');
			     inputRefresh.type = 'hidden';
			     inputRefresh.name = 'refreshToken';
			     inputRefresh.value = false; 
			     
			     form.appendChild(input);
			     form.appendChild(inputRefresh);
			     document.body.appendChild(form);
			     form.submit();   
			     $('#gpsForm').remove();
				 
				 //$window.location.href='page-main.html';
			 }else{
				 //$scope.msgVisibility  = true;
				 //$scope.msjLogin="Las credenciales son incorrectas. Verifique";
			 }	 
		  },function(){
			  console.log('ERROR'); 
		  });
		
		 
	} ;   
}]);

