package com.pradera.stream.util;

import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.pradera.stream.constant.Constant;

/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings("rawtypes")
public final class JavaScriptUtil {

	static ScriptEngine engine = new ScriptEngineManager().getEngineByName(Constant.ENGINE_NASHORN);

	/**
	 * @param functionName
	 * @param scriptBody
	 * @param payload
	 * @param header
	 * @throws Exception
	 */
	public static Object executeJS(String functionName, String scriptBody,  Map header,Map payload) throws Exception {
		Object result =engine.eval(scriptBody);

		Invocable invocable = (Invocable) engine;
		invocable.invokeFunction(functionName, header, payload);
		return result;
	}

}
