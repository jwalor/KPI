/**
 *   jalor v1.0
 */

var DateUtil                = Java.type('com.pradera.stream.util.DateUtil');
var Constant                = Java.type('com.pradera.stream.constant.Constant');
var CustomColumn            = Java.type('com.pradera.stream.model.CustomColumn');
var Types                   = Java.type('java.sql.Types');
//var HashMap                 = Java.type("java.util.HashMap");

var ObjectType = Java.type("java.lang.Object");
var Comparable = Java.type("java.lang.Comparable");
var Serializable = Java.type("java.io.Serializable");


/*
 *                    [KPI Stream Topology]
 *                           +
 *                           v
 *                      +----+-----+
 *                      | LogicBol |
 *                      +----+-----+
 *               ^			 ^					 ^
 *               |			 |					 |
 *               + 			 +			         +	                                  
 *+------------+       +------------+            +------------+
 *| InsertBolt |	   | Nothing    |            | UpdateBolt |
 *+------------+       +------------+            +------------+
 * 
 */                  
function process(header,payload){	
	
	var t = java.lang.System.currentTimeMillis();
	
	 /** next bolt to execute **/
	/*
    	var  _actionUpdate  = Constant.UPDATE_BOLT; 
    	
    	
	 **/
	var  _actionUpsert  = Constant.UPSERT_BOLT; 
	var  _actionNothing = Constant.NOTHING;  
	var  _actionInsert  = Constant.INSERT_BOLT;
	
//	var format = 'yyyy-MM-dd HH:mm:ss';	
//	var actualDate = new java.util.Date();
	
	header.put(Constant.ACTION_RESULT , _actionNothing);
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Logic only is related to COMPARE STATES  ! when  Column.shouldCompare = Boolean.True.	
	
	var columnHeader 	        = null;
	var columnPayload 	        = null;
	
	var hadChange               = false;
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Adding Object which will be used for  updating script.
	
//	var operationPayloadUpdate  = new OperationPayload();
//	var payloadUpdate           = new HashMap();
	
//	payloadUpdate.put();
	
	payload.forEach(function(item) {
	  	
		columnPayload = payload[item];
	
		  if (columnPayload instanceof CustomColumn && columnPayload.getShouldCompare() == true ){
			 
			  header.forEach(function(itemH) {
				 var columnHeader  = header[itemH];
				  
				  if ( columnHeader instanceof CustomColumn &&
					   columnHeader.equalsField(columnPayload)	){
					  
					  if ( !columnHeader.equals(columnPayload)	){
						  
						  //columnPayload.setVal(columnHeader.getVal());
						  payload.put(item,new CustomColumn( columnPayload.getColumnName(),columnHeader.getVal(),columnPayload.getSqlType(),columnPayload.getShouldCompare() ));
						  hadChange = true;
					  }
				  }
				
			  });
		  }
	});
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	if (hadChange ){
	    
	    payload.put('UPDATE_DATE',new CustomColumn( "UPDATE_DATE", DateUtil.getSystemTimestamp() , Types.TIMESTAMP ));
		header.put(Constant.ACTION_RESULT , _actionUpsert);
	}
	
	var tFinal = java.lang.System.currentTimeMillis();
	print("tiempo: " + (tFinal-t)/1000 + " s");

return payload;

}

function compareTo(source , value){	
	if (source === undefined || value === undefined) {
	      return null;
	    }
    if (source < value) {    	
      return -1;
    } else if (source == value) {
      return 0;
    } else {
      return 1;
    }
}
