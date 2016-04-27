function drawTableName(idTableQueryComponent,idContainer,jsonData){
    
    /*var tableSize = $("#"+idTableQueryComponent).innerWidth();*/
    var containerSize = $("#"+idContainer).innerWidth();
    
    var myContainer = document.getElementById(idContainer);
    var myTable = document.createElement('table');
    var myTHead = document.createElement('thead');
    var myTh = document.createElement('th');
    var myTBody = document.createElement('tbody');
    var myTr = document.createElement('tr');
    var myTd = document.createElement('td');
    var myThDiv = document.createElement('div');
    var myARef = document.createElement('a');
    var myResultSet = null;
    var myMetajsonData = null;

    $("#"+idContainer).empty();

    myTable.id = idTableQueryComponent;
    myTable.className = 'footable table table-striped';

	myMetajsonData = jQuery.isEmptyObject(jsonData["metadata"])?"":jsonData["metadata"];
	
    myContainer.appendChild(myTable).appendChild(myTHead).appendChild(myTr);
   
     for(var i = 0; i < myMetajsonData.length; i++){
        
        myHeaderText = document.createTextNode(myMetajsonData[i]['colName']);
        
        myContainer.lastChild.lastChild.lastChild.appendChild(myTh.cloneNode()).appendChild(myThDiv.cloneNode()).appendChild(myHeaderText);
			
        //if(i>0){ 
			if(i > 0){
				//myContainer.lastChild.lastChild.lastChild.lastChild.setAttribute('data-toggle','true');
				//myContainer.lastChild.lastChild.lastChild.lastChild.setAttribute('data-hide','tablet,phone');
			}
        /*}else{
            myContainer.lastChild.lastChild.lastChild.lastChild.lastChild.setAttribute('style','display:block;width: 240px !important;');//
        }*/
    }

    myContainer.lastChild.appendChild(myTBody);

	
	myResultSet = jQuery.isEmptyObject(jsonData["resultset"])?"":jsonData["resultset"];
	
    for(var i = 0; i < myResultSet.length; i++) {
        myContainer.lastChild.lastChild.appendChild(myTr.cloneNode());
        for(var j = 0; j < myResultSet[i].length; j++) {
            
            if(myResultSet[i][j] == null){
                myResultSet[i][j] = 0;
            }
            
			myARef.className="noLinkInTable";
			//myARef.id=myResultSet[i][j];
			
			//if(myMetajsonData[i]['colType'] != null && myMetajsonData[i]['colType'] == 'html'){
				var result = myResultSet[i][j];
				myText = document.createElement('div');
				myText.innerHTML = result;
			//	console.log("-> "+result);
			//}else{
				//myText = document.createTextNode(myResultSet[i][j]);	
			//}
			
            myContainer.lastChild.lastChild.lastChild.appendChild(myTd.cloneNode()).appendChild(myARef.cloneNode()).appendChild(myText);
        }
    }
    
    
    var myThHead = $('#'+idTableQueryComponent+' thead tr th');
    var sumaryThWidth = 0;
    var rowsCount = 0;
    myThHead.each(function(){
       var currentThTag =  $(this);
       sumaryThWidth = sumaryThWidth + currentThTag.innerWidth();
       rowsCount++;
	   
	   if(rowsCount == 1 ){
            currentThTag.attr('data-toggle','true');
	   }else if((sumaryThWidth+5) > containerSize){  
            //currentThTag.removeAttr('data-hide');
            currentThTag.attr('data-hide','phone,tablet');
            //currentThTag.removeAttr('data-toggle');
       }else{
            //currentThTag.removeAttr('data-hide');
            currentThTag.attr('data-hide','phone');
            //currentThTag.removeAttr('data-toggle');
       }
    }); 
    
    $('.footable').footable({
        breakpoints: {
            phone: 480,
            tablet: containerSize //containerSize
        }
    });
} 