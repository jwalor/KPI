'use strict';//debo borrar la dependencia d abajo?
angular.module("loginService", ['configurationService','LocalStorageModule']).factory("LoginFactory" ,['ConfigurationService','localStorageService',
                                                                                  function(ConfigurationService, localStorageService)
	{  
	    var loginFactory = {};
		
	    loginFactory.validateUser = function(source,action,request) {
			return	 ConfigurationService.sentRequestPost(source,action,request).then(function(response){
					// alert(" alor "+ response.TOKEN);
					 localStorageService.set('todos', 'alorcillo');
					 localStorageService.set('TOKEN', response.TOKEN);
					 console.log(response);		 
				  },function(){
					  console.log('ERROR'); 
				  });
			  
	    };
	    
	    return loginFactory;
	    
	}]);