package com.pradera.stream.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.jdbc.common.Column;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

import com.pradera.stream.model.CustomColumn;


/**
 * 
 * @author jalor
 *
 */
public class VelocityUtils {
	
	private static final Log LOG = LogFactory.getLog(VelocityUtils.class);

	public static final String PAYLOAD = "payload";
	
	public static final String VELOCITY_CONTEXT = "vcontext";
	
	public static final String FILE_TEMP = "tmp";
	
	static RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();

	
	/**
	 * @param vmBody
	 * @param data
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String loadTemplateVM(Object template, Map data )throws Exception {
		if(template instanceof String ){
			 return loadTemplateSimpleVM((String) template,  data) ;
		}
		if(template instanceof List ){
			return loadTemplateMultipleVM((List<Map>) template,  data) ;
		}
		return null;
	}
	
	/**
	 * @param vmBody
	 * @param data
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("rawtypes")
	public static String loadTemplateSimpleVM(String vmBody, Map data ) throws Exception {
		try {
			VelocityEngine ve = new VelocityEngine();
			ve.init();
			StringReader reader = new StringReader(vmBody);
			SimpleNode node = runtimeServices.parse(reader, "Any Template name");

			Template template = new Template();
			template.setRuntimeServices(runtimeServices);
			template.setData(node);
			template.initDocument();

			VelocityContext context = new VelocityContext();
			context.put("StringUtils", StringUtils.class);
			context.put("ObjectUtils", ObjectUtils.class);
			context.put("NumberUtils", NumberUtils.class);
			context.put("DateUtil", DateUtil.class);
			context.put("Column", CustomColumn.class);
			context.put("payload", data);
			StringWriter sw = new StringWriter();
			template.merge(context, sw);
			return sw.toString();
		}
		catch (Exception e) {
			throw e;
		}
	}
	
	
	/**
	 * @param vmBody
	 * @param data
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String loadTemplateMultipleVM(List<Map> templates, Map data) {
		try {
			VelocityEngine ve = new VelocityEngine();

		    ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
		    ve.setProperty(Velocity.RESOURCE_LOADER, "string");
		    ve.addProperty("string.resource.loader.class", StringResourceLoader.class.getName());
		    ve.addProperty("string.resource.loader.repository.static", "false");
//		    ve.setProperty(RuntimeConstants.EVENTHANDLER_INCLUDE, IncludeRelativePath.class.getName());

		    ve.init();
			
			StringResourceRepository repo = (StringResourceRepository) ve.getApplicationAttribute(StringResourceLoader.REPOSITORY_NAME_DEFAULT);
			LOG.debug("::::::repo: "+repo+ "  Velocity: "+ve);
			for (ListIterator<Map> ite = templates.listIterator(); ite.hasNext();) {
				Map mapTemplate = ite.next();
				if(!MapUtils.isEmpty(mapTemplate) && !Objects.isNull(repo)){
					repo.putStringResource(Objects.toString(mapTemplate.get("name")), Objects.toString(mapTemplate.get("body")));
				}
			}			
			
			Template template = ve.getTemplate("mainTemplate.vm");
		

			VelocityContext context = new VelocityContext();

			context.put("payload", data);
			context.put("StringUtils", StringUtils.class);
			context.put("NumberUtils", NumberUtils.class);
			StringWriter sw = new StringWriter();
			template.merge(context, sw);
			return sw.toString();
		}
		catch (Exception e) {
			throw e;
		}
	}
}
