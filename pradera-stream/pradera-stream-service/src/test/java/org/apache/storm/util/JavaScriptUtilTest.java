package org.apache.storm.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.storm.jdbc.common.Column;
import org.junit.Before;
import org.junit.Test;

import com.pradera.stream.constant.Constant;
import com.pradera.stream.model.CustomColumn;
import com.pradera.stream.util.JavaScriptUtil;


/**
 * @author jalor
 *
 */
@SuppressWarnings({"unused" , "rawtypes" , "unchecked"})
public class JavaScriptUtilTest {

	static ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
	
	private String pathRoot = JavaScriptUtilTest.scriptsPath();
	
	private  Map columnsPayload ;
	private  Map columnsHeader ;
	
	@Before
	public void loadMaps() {
		
		columnsPayload	 = getColumnFieldsPayload();
		columnsHeader	 = getColumnFieldsHeader();
	}
	
	public static String scriptsPath() {
		String rootProjectPath;
		try {
			rootProjectPath = new File(new File(".").getCanonicalPath()).getPath();
			return rootProjectPath + "/src/test/resources/scripts";
		} catch (IOException e) {
			return "";
		}
	}

	
	@Test
	public void executeJS()  {
		
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(this.pathRoot+"/settlement/settlementLogicBolt.js"));
			String function = new String(encoded, StandardCharsets.UTF_8);
		
			System.out.println("Before" + columnsPayload.toString());
			Object result	= JavaScriptUtil.executeJS(Constant.JAVASCRIPT_FUNCTION_NAME_DEFAULT,function,columnsHeader,columnsPayload );
			System.out.println("After Payload " + columnsPayload.toString());
			System.out.println("After Header " + columnsHeader.toString());
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	 protected Map getColumnFieldsPayload() {
			
		    final Map payload = new HashMap<String, Object>();
		    Column column1 = new CustomColumn("TOTAL",1L,Types.NUMERIC);
		    payload.put("TOTAL", column1); 
		    
		    Column column2 = new CustomColumn("CODE",1L,Types.NUMERIC);
		    payload.put("CODE", column2);
		    
		    Column column3 = new CustomColumn("ID_HOLDER_ACCOUNT_OPERATION_PK",11462L,Types.NUMERIC);
		    payload.put("ID_HOLDER_ACCOUNT_OPERATION_PK", column3);
		    
		    Column column4 = new CustomColumn("ID_MECHANISM_OPERATION_FK",11462L,Types.NUMERIC);
		    payload.put("ID_MECHANISM_OPERATION_FK", column4);
		    
		    Column column5 = new CustomColumn("ID_HOLDER_ACCOUNT_FK",11462L,Types.NUMERIC);
		    payload.put("ID_HOLDER_ACCOUNT_FK", column5);
		    
		    Column column6 = new CustomColumn("OPERATION_PART",11462L,Types.NUMERIC);
		    payload.put("OPERATION_PART", column6);
		    
		    Column column7 = new CustomColumn("SETTLEMENT_AMOUNT",11462L,Types.NUMERIC);
		    ((CustomColumn)column7).setShouldCompare(Boolean.TRUE);
		    payload.put("SETTLEMENT_AMOUNT", column7);
		    
		    Column column8 = new CustomColumn("SETTLED_QUANTITY",11462L,Types.NUMERIC);
		    payload.put("SETTLED_QUANTITY", column8);
		    
		    Column column9 = new CustomColumn("LAST_MODIFY_DATE",new Date(),Types.TIMESTAMP);
		    payload.put("LAST_MODIFY_DATE", column9);
		    
		   return payload;
			
		}
	 	
	 protected Map getColumnFieldsHeader() {
			
		    final Map payload = new HashMap<String, Object>();
		    Column column1 = new CustomColumn("TOTAL",1L,Types.NUMERIC);
		    payload.put("TOTAL", column1); 
		    
		    Column column2 = new CustomColumn("CODE",1L,Types.NUMERIC);
		    payload.put("CODE", column2);
		    
		    Column column3 = new CustomColumn("ID_HOLDER_ACCOUNT_OPERATION_PK",11462L,Types.NUMERIC);
		    payload.put("ID_HOLDER_ACCOUNT_OPERATION_PK", column3);
		    
		    Column column4 = new CustomColumn("ID_MECHANISM_OPERATION_FK",11462L,Types.NUMERIC);
		    payload.put("ID_MECHANISM_OPERATION_FK", column4);
		    
		    Column column5 = new CustomColumn("ID_HOLDER_ACCOUNT_FK",11462L,Types.NUMERIC);
		    payload.put("ID_HOLDER_ACCOUNT_FK", column5);
		    
		    Column column6 = new CustomColumn("OPERATION_PART",11462L,Types.NUMERIC);
		    payload.put("OPERATION_PART", column6);
		    
		    Column column7 = new CustomColumn("SETTLEMENT_AMOUNT",12462L,Types.NUMERIC);
		    ((CustomColumn)column7).setShouldCompare(Boolean.TRUE);
		    payload.put("SETTLEMENT_AMOUNT", column7);
		    
		    Column column8 = new CustomColumn("SETTLED_QUANTITY",11462L,Types.NUMERIC);
		    payload.put("SETTLED_QUANTITY", column8);
		    
		    Column column9 = new CustomColumn("LAST_MODIFY_DATE",new Date(),Types.TIMESTAMP);
		    payload.put("LAST_MODIFY_DATE", column9);
		    
		   return payload;
			
		}
}

/**
**			
*/