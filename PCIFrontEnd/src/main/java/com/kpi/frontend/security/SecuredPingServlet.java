package com.kpi.frontend.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

@WebServlet(name="secured-ping-servlet", urlPatterns = { "/secured/token" })
public class SecuredPingServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(SecuredPingServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (log.isTraceEnabled())
			log.trace(String.format("doGet(%s, %s)", req, resp));
		resp.setContentType("text/plain");
		resp.getWriter().write("All good. You're viewing this because you're authorized to.");
		
		
	}
	

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException, IllegalStateException{
		PrintWriter out = resp.getWriter();  
		resp.setContentType("application/json");  
		JSONObject obj = new JSONObject();
		
		obj.put("TOKEN", 1);// DEBE SER UNA CONSTANTE QUE ENVIE UN OK AL LADO CLIENTE
		out.print(obj);
	}
}
