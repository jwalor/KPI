function createChartBar(data){
	
	return pvcBarChart(data);
}

function createChartGroupBar(data){
	
	data["stacked"] = 'true';
	data["valuesMask"] = "{value} ({value.percent})";
	return pvcBarChart(data);
}

function createChartHorizontalGroupBar(data){
	
	data["orientation"] = 'horizontal';
	data["stacked"] = 'true';
	data["valuesMask"] = "{value} ({value.percent})";
	
	return pvcBarChart(data);
}


function createChartHorizontalBar(data){
	
	data["orientation"] = 'horizontal';
	data["stacked"] = 'false';
	data["valuesMask"] = "{value}";
	
	return pvcBarChart(data);
}


function createChartPie(data){
	
	data["orientation"] = 'horizontal';
	data["stacked"] = 'false';
	data["valuesMask"] = "{value}";
	
	return pvcBarChart(data);
}

function createChartPie(data){
	
	return pvcPieChart(data);
}

function createChartDonut(data){
	
	data["explodedSliceRadius"] = "0%";
	data["slice_innerRadiusEx"] = "50%";
	
	return pvcPieChart(data);
}


function createChartLine(data){

	return pvcStackedLineChart(data);
}

function createEjecutiveChartHome(idContainer, jsonData){
	
    var myResultSet = jQuery.isEmptyObject(jsonData["resultset"])?"":jsonData["resultset"];
	
	$("#"+idContainer).empty();
	var myContainer = document.getElementById(idContainer);
	
	/*ESTADO BASE*/
	
	var myContainerBase = document.createElement('div');
		myContainerBase.className = 'col-md-4 h50px';
		
	var divWidget = document.createElement('div');
		divWidget.className = 'statbox widget';
		
	var color = myResultSet[0][0];
	var divContentWidget = document.createElement('div');
		divContentWidget.className = 'statbox visual '+color;
	
	var estado = myResultSet[0][2];
		divContentWidget.innerHTML = estado;
			
	myContainerBase.appendChild(divWidget).appendChild(divContentWidget);	
		
	
	/*LISTADO*/
	
	var myContainerList = document.createElement('div');
		myContainerList.className = 'col-md-8';
		
	var ulElement = document.createElement('ul');
		ulElement.className = 'feeds clearfix';
	
	for(var i = 1; i < myResultSet.length; i++) {
		
		var li = document.createElement('li');
		var divCol1Content = document.createElement('div');
		var divContent = document.createElement('div');
		var divCol1 = document.createElement('div');
		var divLabel = document.createElement('div');
		var icon = document.createElement('i');
		var divCol2 = document.createElement('div');
		var divDesc = document.createElement('div');
		var desc = document.createElement('a');
		
		divContent.className = 'content';
		divCol1.className = 'content-col1';
		divCol2.className = 'content-col2';
		divCol1Content.className = 'col1';
			
		var label = myResultSet[i][0];
		var icono = myResultSet[i][1];
		var texto = myResultSet[i][2];
		
		var myText = document.createElement('div');
			myText.innerHTML = texto;
		
		divLabel.className = "label "+label;
		icon.className = icono;
		divDesc.className = "desc";
		
		li.appendChild(divCol1Content).appendChild(divContent);
		li.firstChild.firstChild.appendChild(divCol1).appendChild(divLabel).appendChild(icon);
		li.firstChild.firstChild.appendChild(divCol2).appendChild(divDesc).appendChild(myText);
		
		ulElement.appendChild(li);
	}
	
	myContainerList.appendChild(ulElement);
	
	/*DETALLE - VER MAS*/
	
		var hrefLink = myResultSet[0][1];
		var aElement = document.createElement('a');
		aElement.className = "more";
		aElement.setAttribute("href",hrefLink);
		aElement.innerHTML = "VER DETALLE"
		
	
	/*AGREGO MIS ELEMENTOS*/
	myContainer.appendChild(myContainerBase);
	myContainer.appendChild(myContainerList);
	myContainer.appendChild(aElement);
	
}


function createListTable(idContainer, jsonData){
	
	$("#"+idContainer).empty();
	var myContainer = document.getElementById(idContainer);
	var ulElement = document.createElement('ul');
		ulElement.className = 'feeds clearfix';
	
	/*var myMetajsonData = jQuery.isEmptyObject(jsonData["metadata"])?"":jsonData["metadata"];*/
    var myResultSet = jQuery.isEmptyObject(jsonData["resultset"])?"":jsonData["resultset"];
	
	for(var i = 0; i < myResultSet.length; i++) {
		
		var li = document.createElement('li');
		var divCol1Content = document.createElement('div');
		var divContent = document.createElement('div');
		var divCol1 = document.createElement('div');
		var divLabel = document.createElement('div');
		var icon = document.createElement('i');
		var divCol2 = document.createElement('div');
		var divDesc = document.createElement('div');
		var desc = document.createElement('a');
		
		divContent.className = 'content';
		divCol1.className = 'content-col1';
		divCol2.className = 'content-col2';
		divCol1Content.className = 'col1';
			
		var label = myResultSet[i][0];
		var icono = myResultSet[i][1];
		var texto = myResultSet[i][2];
		
		var myText = document.createElement('div');
			myText.innerHTML = texto;
		
		divLabel.className = "label "+label;
		icon.className = icono;
		divDesc.className = "desc";
		
		li.appendChild(divCol1Content).appendChild(divContent);
		li.firstChild.firstChild.appendChild(divCol1).appendChild(divLabel).appendChild(icon);
		li.firstChild.firstChild.appendChild(divCol2).appendChild(divDesc).appendChild(myText);
		
		ulElement.appendChild(li);
	}
	
	myContainer.appendChild(ulElement);
	
}


function createResumen(jsonData){
	
	var idContainer = jsonData["idContainer"];
	
	$("#"+idContainer).empty();
	var myContainer = document.getElementById(idContainer);
	
	/*ESTADO BASE*/
	var varClassHeigh = jsonData["classHeigh"];//h150px
	var varColor = jsonData["color"];
	var varEstado = jsonData["estado"];
	var varUp = jsonData["up"];
	var varWarning = jsonData["warning"];
	var varCritical = jsonData["critical"];
	
	var listElementHtml = 
	'<ul>'+
		'<li>UP <i class="icon-plus fontcolor-green"></i> ( <b>'+varUp+'</b> ) </li>'+
		'<li>WARNING <i class="icon-warning-sign fontcolor-ambar"></i> ( <b>'+varWarning+'</b> )</li>'+
		'<li>CRITICAL <i class="icon-exclamation fontcolor-red"></i> ( <b>'+varCritical+'</b> ) </li>'+
	'</ul>';
	
	var myContainerBase = document.createElement('div');
		myContainerBase.className = 'col-md-12 float-none '+varClassHeigh;
		
	var divWidget = document.createElement('div');
		divWidget.className = 'statbox widget';
		
	var divContentWidget = document.createElement('div');
		divContentWidget.className = 'statbox visual float-none '+varColor;
		divContentWidget.innerHTML = varEstado;
	
	var divLista = document.createElement('div');
	divLista.className = 'col-md-12'
	divLista.innerHTML = listElementHtml;
			
	myContainerBase.appendChild(divWidget).appendChild(divContentWidget);	
	myContainerBase.firstChild.appendChild(divLista);
	
	myContainer.appendChild(myContainerBase);
}


/******************/

function pvcBarChart(data){
	
	varCanvas 		= jQuery.isEmptyObject(data["canvas"])?"defaultCanvas":data["canvas"];
	varWidth 		= jQuery.isEmptyObject(data["width"])?400:data["width"];
	varHeight 		= jQuery.isEmptyObject(data["height"])?400:data["height"];
	varColors 		= jQuery.isEmptyObject(data["colors"])?["#e25856", "#f0ad4e", "#94b86e"]:data["colors"];
	
	varSelectable 	= jQuery.isEmptyObject(data["selectable"])?false:Boolean.parse(data["selectable"]);
	varHoverable 	= jQuery.isEmptyObject(data["hoverable"])?false:Boolean.parse(data["hoverable"]);
	varAnimate 		= jQuery.isEmptyObject(data["animate"])?true:Boolean.parse(data["animate"]);
	varLegend 		= jQuery.isEmptyObject(data["legend"])?true:Boolean.parse(data["legend"]);
	
	varPlotFrameVisible = jQuery.isEmptyObject(data["plotFrameVisible"])?false:Boolean.parse(data["plotFrameVisible"]);
	varOrientation 	= jQuery.isEmptyObject(data["orientation"])?'vertical':data["orientation"];
	varStacked 		= jQuery.isEmptyObject(data["stacked"])?false:Boolean.parse(data["stacked"]);
	varValuesMask		= jQuery.isEmptyObject(data["valuesMask"])?"{value}":data["valuesMask"]; 
	
	
	return new pvc.BarChart({
		
			canvas: varCanvas,
			width:  varWidth,
			height: varHeight, 
			animate:    varAnimate,
			
			// Main Plot
			orientation: varOrientation,
			stacked: varStacked,
			valuesVisible: true,
			valuesLabelStyle: 'inside',
			valuesOverflow: 'show',
			valuesMask: varValuesMask,
			
			
			plotFrameVisible: varPlotFrameVisible, 
			selectable: varSelectable,
			hoverable:  varHoverable,
			legend:     varLegend,
			barSizeMax: 20,
			colors: varColors,
			orthoAxisOffset: 0.01,
			
			// Cartesian axes
			axisGrid:   true,
			axisOffset: 0,
			axisGrid_strokeStyle: '#F7F8F9',
			axisLabel_font: 'normal 9px "Open Sans"',
			baseAxisTicks: true,
			axisRule_strokeStyle: '#DDDDDD'

		});

}

function pvcPieChart(data){
	
	varCanvas 		= jQuery.isEmptyObject(data["canvas"])?"defaultCanvas":data["canvas"];
	varWidth 		= jQuery.isEmptyObject(data["width"])?400:data["width"];
	varHeight 		= jQuery.isEmptyObject(data["height"])?400:data["height"];
	varColors 		= jQuery.isEmptyObject(data["colors"])?["#e25856", "#f0ad4e", "#94b86e"]:data["colors"];
	
	varSelectable 	= jQuery.isEmptyObject(data["selectable"])?false:Boolean.parse(data["selectable"]);
	varHoverable 	= jQuery.isEmptyObject(data["hoverable"])?false:Boolean.parse(data["hoverable"]);
	varAnimate 		= jQuery.isEmptyObject(data["animate"])?true:Boolean.parse(data["animate"]);
	
	varLegend 		= jQuery.isEmptyObject(data["legend"])?true:Boolean.parse(data["legend"]);
	varPlotFrameVisible = jQuery.isEmptyObject(data["plotFrameVisible"])?false:Boolean.parse(data["plotFrameVisible"]);
	varValuesMask		= jQuery.isEmptyObject(data["valuesMask"])?"{value} ({value.percent})":data["valuesMask"]; 
	
	varSlice_strokeStyle		= jQuery.isEmptyObject(data["slice_strokeStyle"])?"white":data["slice_strokeStyle"]; 
	varExplodedSliceRadius		= jQuery.isEmptyObject(data["explodedSliceRadius"])?"0%":data["explodedSliceRadius"]; 
	varSlice_innerRadiusEx		= jQuery.isEmptyObject(data["slice_innerRadiusEx"])?"0%":data["slice_innerRadiusEx"]; 
	
	
	return new pvc.PieChart({
		
			canvas: varCanvas,
			width:  varWidth,
			height: varHeight, 
			animate:    varAnimate,
			
			// Main Plot
			valuesVisible: true,
			valuesLabelStyle: 'inside',
			valuesOverflow: 'show',
			valuesMask: varValuesMask,
			valuesFont: 'normal 11px "Open Sans"',
			valuesAnchor : 'outer',
			valuesOptimizeLegibility: true,
			slice_strokeStyle: 'white',
			
			plotFrameVisible: varPlotFrameVisible, 
			selectable: varSelectable,
			hoverable:  varHoverable,
			legend:     varLegend,
			barSizeMax: 20,
			colors: varColors,
			orthoAxisOffset: 0.01,
			
			//legend
			legendShape: 'circle',
			legendPosition: 'right',
			legendAlign: 'middle',

			//donut
			slice_strokeStyle: varSlice_strokeStyle,
			explodedSliceRadius: varExplodedSliceRadius,
			slice_innerRadiusEx: varSlice_innerRadiusEx,
			
		});

}


function pvcStackedLineChart(data){
	
	varCanvas 		= jQuery.isEmptyObject(data["canvas"])?"defaultCanvas":data["canvas"];
	varWidth 		= jQuery.isEmptyObject(data["width"])?400:data["width"];
	varHeight 		= jQuery.isEmptyObject(data["height"])?400:data["height"];
	varCrosstabMode = jQuery.isEmptyObject(data["crosstabMode"])?false:Boolean.parse(data["crosstabMode"]);
	varColors 		= jQuery.isEmptyObject(data["colors"])?["#e25856", "#f0ad4e", "#94b86e"]:data["colors"];
	varSelectable 	= jQuery.isEmptyObject(data["selectable"])?false:Boolean.parse(data["selectable"]);
	varHoverable 	= jQuery.isEmptyObject(data["hoverable"])?false:Boolean.parse(data["hoverable"]);
	varAnimate 		= jQuery.isEmptyObject(data["animate"])?true:Boolean.parse(data["animate"]);
	varLegend 		= jQuery.isEmptyObject(data["legend"])?true:Boolean.parse(data["legend"]);
	varPlotFrameVisible = jQuery.isEmptyObject(data["plotFrameVisible"])?false:Boolean.parse(data["plotFrameVisible"]);
	varDimesionsCategory = jQuery.isEmptyObject(data["category"])?"":data["category"];
	//"{valueType: Date, isDiscrete: true } " <- para acortar la fecha
	
	console.log("createChartLine");
	return new pvc.StackedLineChart({
		
			canvas: varCanvas,
			width:  varWidth,
			height: varHeight, 

			// Data
			dimensions: {
				// Category is a Date, but discrete
				category: varDimesionsCategory
			},

			// Main plot
			// Main Plot
			valuesVisible: true,
			valuesLabelStyle: 'inside',
			valuesOverflow: 'show',
			
			dotsVisible: true,
			dot_shapeSize: 5,
			line_interpolate: 'monotone',
			area_interpolate: 'monotone',

			// Cartesian axes
			axisGrid:   true,
			axisOffset: 0,
			axisGrid_strokeStyle: '#F7F8F9',
			axisLabel_font: 'normal 9px "Open Sans"',
			baseAxisTicks: true,
			axisRule_strokeStyle: '#DDDDDD',
			plotFrameVisible: varPlotFrameVisible,
			
			// Panels
			legend: true,
			legendFont: 'normal 11px "Open Sans"',

			// Chart/Interaction
			animate:    varAnimate,
			selectable: varSelectable,
			hoverable:  varHoverable,

			// Color axes
			colors: varColors
		});

}

var falsy = /^(?:f(?:alse)?|no?|0+)$/i;
Boolean.parse = function(val) { 
    return !falsy.test(val) && !!val;
};
