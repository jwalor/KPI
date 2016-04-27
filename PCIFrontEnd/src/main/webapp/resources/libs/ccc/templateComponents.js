
function createSelect(jsonData){
	
	var varMyContent = document.getElementById(jsonData["idContainer"]); 
    var myResultSet = jQuery.isEmptyObject(jsonData["resultset"])?"":jsonData["resultset"];
	console.log("myResultSet: "+myResultSet);
	
	var selectList = document.createElement("select");
	selectList.id = jsonData["idComponent"];

	for(var i = 0; i < myResultSet.length; i++) {
		var optionElement = document.createElement("option");
		optionElement.value = myResultSet[i][0];
		optionElement.text = myResultSet[i][1];
		selectList.appendChild(optionElement);
	}
	
	varMyContent.appendChild(selectList);
}

