
function createWidgetTitlePage(jsonData){
	
	var varMyContent = document.getElementById(jsonData["idContainer"]); 
	var titleMenu = jsonData["titleMenu"];
		
	var divContent = document.createElement('div');
		divContent.className = "col-md-12";
		
	var divSubContent = document.createElement('div');
	
	var h3Element = document.createElement('h3');
		h3Element.innerHTML = titleMenu;
		
		
	divContent.appendChild(divSubContent);
	divContent.firstChild.appendChild(h3Element);
	
	varMyContent.appendChild(divContent);
}

function createWidgetMenuHome(jsonData,backgroundClass){
	
	var varMyContent = document.getElementById(jsonData["idContainer"]); 
    var myResultSet = jQuery.isEmptyObject(jsonData["menuHome"])?"":jsonData["menuHome"];
	var backgroundClass = jQuery.isEmptyObject(backgroundClass)?"":backgroundClass;
	console.log("myResultSet: "+myResultSet);
	
	for(var i = 0; i < myResultSet.length; i++) {
		
		var varResponsiveClass = myResultSet[i][0];
		var pageUrl		 = myResultSet[i][1];
		var iconMenu	 = myResultSet[i][2];
		var titleMenu	 = myResultSet[i][3];
		
		currentPage = jsonData["currentPage"]; 
		
		var divContent = document.createElement('div');
			divContent.className = varResponsiveClass;
			
		var aElement = document.createElement('a');
			aElement.className = "btn btn-icon input-block-level"+backgroundClass;
			aElement.setAttribute("href",pageUrl);
		
		var iElement = document.createElement('i');
			iElement.className = iconMenu;
			
		var divElementTitle= document.createElement('div');
			divElementTitle.innerHTML = titleMenu;
			
		divContent.appendChild(aElement);
		divContent.firstChild.appendChild(iElement);
		divContent.firstChild.appendChild(divElementTitle);
		
		varMyContent.appendChild(divContent);
	}
	
}

function createWidgetBase(jsonData){
	
	var varMyContent = document.getElementById(jsonData["idContainer"]); 
	
	var varResponsiveClass = jsonData["responsibleClass"];
	var varhtmlTitle = "<h4>"+jsonData["htmlTitle"]+"<h4>"; 
	var varIconTitle = jQuery.isEmptyObject(jsonData["iconTitle"])?"":"<i class='"+jsonData["iconTitle"]+"'></i>";	
	var varIdContent = jsonData["idContent"]; 
	var varOverflow = jQuery.isEmptyObject(jsonData["overflow"])?"":jsonData["overflow"]; 
	var varOverflowH = jsonData["overflowHeight"]; 
	
	var tituloWidget = varIconTitle+" "+varhtmlTitle;
	
	var divContent = document.createElement('div');
		divContent.className = varResponsiveClass;
	
	var divWidgetBox = document.createElement('div');
		divWidgetBox.className = "widget box";
	
	var divWidgetHeader = document.createElement('div');
		divWidgetHeader.className = "widget-header";
		divWidgetHeader.innerHTML = tituloWidget;
	/*
		<div id="idTableListExample" class="scroller" style="overflow: auto; width: auto; height: 200px;"
	*/
	var divWidgetContentBase = document.createElement('div');
		divWidgetContentBase.className = "col-md-12";
		divWidgetContentBase.setAttribute("id",varIdContent+"Base");
		
	var divWidgetContent = document.createElement('div');
		divWidgetContent.className = "widget-content scroller";
		divWidgetContent.setAttribute("id",varIdContent);
		if(varOverflow != ""){
			divWidgetContent.setAttribute("style","overflow: "+varOverflow+"; width: auto; height: "+varOverflowH);
		}
	
	divContent.appendChild(divWidgetBox).appendChild(divWidgetHeader);
	divContent.firstChild.appendChild(divWidgetContentBase);
	divContent.firstChild.appendChild(divWidgetContent);
	
	varMyContent.appendChild(divContent);
	
}
function createWidgetPanelMenun(jsonData){
	
	varMyContent = document.getElementById(jsonData["idContainer"]); 
	
	currentPage = jsonData["currentPage"]; 
	homeUrl		= jsonData["homeUrl"]; 
	
	var divContent = document.createElement('div');
		divContent.className = "crumbs outMenu";
	
	var ulElement = document.createElement('ul');
		ulElement.className = "breadcrumb";
		ulElement.setAttribute("id","breadcrumbs");
	
	var liElementDashboard = document.createElement('li');
		liElementDashboard.innerHTML = "<i class='icon-home'></i><a>Dashboard</a>";
		
	var liElementHome= document.createElement('li');
		liElementHome.className = "current";
		liElementHome.innerHTML = "<a title='' href='"+homeUrl+"'>Home</a>";
		
	var liElementCurrent= document.createElement('li');
		liElementCurrent.className = "current";
		liElementCurrent.innerHTML = "<a title='' href='#'>"+currentPage+"</a>";

	divContent.appendChild(ulElement);
	divContent.firstChild.appendChild(liElementDashboard);
	divContent.firstChild.appendChild(liElementHome);
	divContent.firstChild.appendChild(liElementCurrent);
	
	varMyContent.appendChild(divContent);
}

