/* WIDGETS */
//1
var widgetJsonMenu = {
	"idContainer": "contentPage",
	"homeUrl": "file:///D:/templates_web/template_indicadores/dbEjecutivo.html",
	"currentPage": "Dashboard Compensacion y Liquidacion"
};
//2
var widgetJsonTitulo = {
	"idContainer": "contentPage",
	"titleMenu": "Dashboard Compensacion y Liquidacion"
};

//3
var jsonDataIdWFiltros = {
	"idContainer": "contentPage",
	"responsibleClass": "col-md-12",
	"htmlTitle": "Filtros",
	"iconTitle":"icon-plus",
	"idContent": "idWFiltros"
};

//4
var jsonDataIdWContResumen = {
	"idContainer": "contentPage",
	"responsibleClass": "col-md-4",
	"htmlTitle": "Resumen Compensacion y Liquidacion",
	"iconTitle":"icon-plus",
	"idContent": "idWContResumen"
};

//5
var jsonDataIdWContResumenList = {
	"idContainer": "contentPage",
	"responsibleClass": "col-md-8",
	"htmlTitle": "Lista Compensacion y Liquidacion",
	"iconTitle":"icon-plus",
	"idContent": "idWContResumenList",
	"overflow":"auto",
	"overflowHeight":"200px"
};

var jsonDataIdWContPieComparatives = {
	"idContainer": "contentPage",
	"responsibleClass": "col-md-6",
	"htmlTitle": "Comparativas Compensacion y Liquidacion",
	"iconTitle":"icon-plus",
	"idContent": "idWContPieComparatives"
};

var jsonDataIdWContBarComparatives = {
	"idContainer": "contentPage",
	"responsibleClass": "col-md-6",
	"htmlTitle": "Comparativas Compensacion y Liquidacion",
	"iconTitle":"icon-plus",
	"idContent": "idWContBarComparatives"
};

var jsonDataIdWContLineEvolution = {
	"idContainer": "contentPage",
	"responsibleClass": "col-md-12",
	"htmlTitle": "Evolucion Compensacion y Liquidacion",
	"iconTitle":"icon-plus",
	"idContent": "idWContLineEvolution"
};


var jsonDataIdWContBarVerticalComparatives = {
	"idContainer": "contentPage",
	"responsibleClass": "col-md-6",
	"htmlTitle": "Comparativas Compensacion y Liquidacion",
	"iconTitle":"icon-plus",
	"idContent": "idWContBarVerticalComparatives"
};

var jsonDataIdWContDonutComparative = {
	"idContainer": "contentPage",
	"responsibleClass": "col-md-6",
	"htmlTitle": "Evolucion Compensacion y Liquidacion",
	"iconTitle":"icon-plus",
	"idContent": "idWContDonutComparative"
};


/* DATASOURCES */
		
var widgetJsonCombo1 = {
	"idContainer": "idWFiltros",
	"idComponent": "idWContResumenCombo1",
	"resultset": [
		["01", "Valor 01"],
		["02", "Valor 02"]
	]
};
		
var widgetJsonCombo2 = {
	"idContainer": "idWFiltros",
	"idComponent": "idWContResumenCombo2",
	"resultset": [
		["03", "Valor 03"],
		["04", "Valor 04"]
	]
};


var widgetJsonResumen = {
	"idContainer": "idWContResumen",
	"classHeigh": "h180px",
	"color": "red",
	"estado":"CRITICAL",
	"up": "15",
	"warning": "10",
	"critical": "5"
};

var resultset_list_table = {
	"resultset": [
		["label-success", "icon-plus","Liquidacion de Rueda y deuda - Proceso Correcto 1"],
		["label-success", "icon-plus","Liquidacion de Rueda y deuda - Proceso Correcto 2"],
		["label-success", "icon-plus","Liquidacion de Rueda y deuda - Proceso Correcto 3"],
		["label-warning"  , "icon-warning-sign","Liquidacion de Rueda y deuda - Proceso Correcto 4"],
		["label-warning"  , "icon-warning-sign","Liquidacion de Rueda y deuda - Proceso Correcto 5"],
		["label-warning"  , "icon-warning-sign","Liquidacion de Rueda y deuda - Proceso Correcto 6"]
	],
	"metadata": [{
		"colIndex": 0,
		"colType": "String",
		"colName": "class"
	}, {
		"colIndex": 1,
		"colType": "String",
		"colName": "icon"
	}, {
		"colIndex": 2,
		"colType": "html",
		"colName": "text"
	}]
};

var resultset_list_table2 = {
	"resultset": [
		["label-success", "icon-plus","aaaaaa"],
		["label-success", "icon-plus","ccccccccc"],
		["label-warning"  , "icon-warning-sign","ddddddd"],
		["label-warning"  , "icon-warning-sign","ffffffff"]
	],
	"metadata": [{
		"colIndex": 0,
		"colType": "String",
		"colName": "class"
	}, {
		"colIndex": 1,
		"colType": "String",
		"colName": "icon"
	}, {
		"colIndex": 2,
		"colType": "html",
		"colName": "text"
	}]
};

var resultset_metadata_pieComparatives = {
	"resultset": [
		["Liquidado", 72],
		["Falta Valores",32],
		["Falta Fondos", 200],
		["Falta Fondos y Valores", 500]
	],
	"metadata": [{
		"colIndex": 0,
		"colType": "String",
		"colName": "Operaciones"
	}, {
		"colIndex": 1,
		"colType": "Numeric",
		"colName": "Quantity"
	}]
};

var resultset_metadata_pieComparatives2 = {
	"resultset": [
		["Liquidado", 100],
		["Falta Valores",100],
		["Falta Fondos y Valores", 100]
	],
	"metadata": [{
		"colIndex": 0,
		"colType": "String",
		"colName": "Operaciones"
	}, {
		"colIndex": 1,
		"colType": "Numeric",
		"colName": "Quantity"
	}]
};

var resultset_metadata_barComparative = {
	"resultset": [
		[ "DEPOSITADO", "Fase 3", 30],
		[ "PENDIENTE",  "Fase 3", 60],
		[ "DEPOSITADO", "Fase 2", 27],
		[ "PENDIENTE",  "Fase 2", 32],
		[ "DEPOSITADO", "Fase 1", 72],
		[ "PENDIENTE",  "Fase 1", 50]
	],
	"metadata": [{
		"colIndex": 0,
		"colType": "String",
		"colName": "Estado"
	}, {
		"colIndex": 1,
		"colType": "String",
		"colName": "Fase"
	}, {
		"colIndex": 2,
		"colType": "Numeric",
		"colName": "Cantidad"
	}]
};

var resultset_metadata_lineEvolution = {
			"resultset": [
				["fase 1", "09:00", 72],
				["fase 1", "10:00", 82],
				["fase 1", "11:00", 100],
				["fase 1", "12:00", 150],
				["fase 1", "13:00", 150],
				["fase 1", "14:00", 200],
				["fase 1", "15:00", 300],
				//            
				["fase 2", "09:00", 27],
				["fase 2", "10:00", 32],
				["fase 2", "11:00", 50],
				["fase 2", "12:00", 60],
				["fase 2", "13:00", 60],
				["fase 2", "14:00", 60],
				["fase 2", "15:00", 400],
				//            
				["fase 3", "09:00", 17],
				["fase 3", "10:00", 72],
				["fase 3", "11:00", 74],
				["fase 3", "12:00", 90],
				["fase 3", "13:00", 90],
				["fase 3", "14:00", 120],
				["fase 3", "15:00", 600]
			],
			"metadata": [{
				"colIndex": 0,
				"colType": "String",
				"colName": "Fase"
			}, {
				"colIndex": 1,
				"colType": "Date",
				"colName": "Hora"
			}, {
				"colIndex": 2,
				"colType": "Numeric",
				"colName": "Cantidad"
			}]
		};

	var resultset_metadata_table = {
		"resultset": [
			[ "DEPOSITADO", "<i class='icon-certificate'></i> tabla icono", 30],
			[ "PENDIENTE",  "<i class='icon-certificate'></i>", 60],
			[ "DEPOSITADO", "<b>Textos con negrita negrita</b>", 27],
			[ "PENDIENTE",  "<h4>h4</h4>", 32],
			[ "DEPOSITADO", "Fase 1", 72]
		],
		"metadata": [{
			"colIndex": 0,
			"colType": "String",
			"colName": "Estado"
		}, {
			"colIndex": 1,
			"colType": "html",
			"colName": "Fase"
		}, {
			"colIndex": 2,
			"colType": "Numeric",
			"colName": "Cantidad"
		}]
	};