package com.pradera.stream.service;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.storm.mongodb.common.PopulateCollection;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pradera.stream.model.OperationPayload;

/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings({"unused" , "rawtypes" , "unchecked"})
public class MapperUtilTest {
	
	private static final Logger LOG 				= LoggerFactory.getLogger(MapperUtilTest.class);
	
	
	private  List<Map> columnsPayload ;
	private  List<Map> columnsHeader ;
	//private  OperationPayload operationPayload;
	
	@Before
	public void loadMaps() {
		LOG.debug("at this point , Thread is loading information with hard data  to Map !");
		
		columnsPayload	 = getColumnFieldsMapp();
		
	}
	
	@Test
	public void iterarMapColumns() {
		
		Map columnField8 = columnsPayload.get(0);
		LOG.debug("Before" + columnField8.toString());
		columnField8.put("value", 5L);
		
		
		LOG.debug("After" + columnField8.toString());
	}
	
    protected List<Map> getColumnFieldsMapp() {
		
	    final Map columnField8 = new HashMap<String, Object>();
	    columnField8.put("name", "TOTAL"); 
	    columnField8.put("value", 11462L);
	    columnField8.put("type", Types.NUMERIC);

		final Map columnField9 = new HashMap<String, Object>();
		columnField9.put("name", "CODE"); 
		columnField9.put("value", 11462L);
		columnField9.put("type", Types.NUMERIC);
	 	
		final Map columnField1 = new HashMap<String, Object>();
		columnField1.put("name", "ID_HOLDER_ACCOUNT_OPERATION_PK"); 
		columnField1.put("value", 11462L);
		columnField1.put("type", Types.NUMERIC);

		final Map columnField2 = new HashMap<String, Object>();
		columnField2.put("name", "ID_MECHANISM_OPERATION_FK"); 
		columnField2.put("value", 11462L);
		columnField2.put("type", Types.NUMERIC);
		
		final Map columnField3 = new HashMap<String, Object>();
		columnField3.put("name", "ID_HOLDER_ACCOUNT_FK"); 
		columnField3.put("value", 11462L);
		columnField3.put("type", Types.NUMERIC);
		
		final Map columnField4 = new HashMap<String, Object>();
		columnField4.put("name", "OPERATION_PART"); 
		columnField4.put("value", 11462L);
		columnField4.put("type", Types.NUMERIC);
		
		final Map columnField5 = new HashMap<String, Object>();
		columnField5.put("name", "SETTLEMENT_AMOUNT"); 
		columnField5.put("value", 11462L);
		columnField5.put("type", Types.NUMERIC);
		columnField5.put("shouldCompare", Boolean.TRUE);
		
		final Map columnField6 = new HashMap<String, Object>();
		columnField6.put("name", "SETTLED_QUANTITY"); 
		columnField6.put("value", 11462L);
		columnField6.put("type", Types.NUMERIC);
		
		final Map columnField7 = new HashMap<String, Object>();
		columnField7.put("name", "LAST_MODIFY_DATE"); 
		columnField7.put("value", 11462L);
		columnField7.put("type", Types.TIMESTAMP);
		
		return new ArrayList<Map>()
		{{add(columnField8);
		  add(columnField9);
		  add(columnField1);
		  add(columnField2);
		  add(columnField3);
		  add(columnField4);
		  add(columnField5);
		  add(columnField6);
		  add(columnField7);
		}};
		
	}
}
