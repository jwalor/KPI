package com.kpi.frontend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.kpi.frontend.configuration.ConnectionSettings;


@WebServlet(name="ping-servlet", urlPatterns = { "/login" })
public class TokenServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(TokenServlet.class);
	private static final long	TOKEN_TIMEOUT_INMILISECOND	=	10000;

	@Autowired
	private ConnectionSettings connectionSettings;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (log.isTraceEnabled())
			log.trace(String.format("doGet(%s, %s)", req, resp));
		resp.setContentType("text/plain");
		resp.getWriter().write("All good. You don't need authorization to view this");
	}


	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException, IllegalStateException{
		if (log.isTraceEnabled())
			log.trace(String.format("doGet(%s, %s)", req, resp));
		
		HttpServletResponse response = (HttpServletResponse) resp;
		HttpServletRequest request = (HttpServletRequest) req;
		
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		
		long nowMillis = System.currentTimeMillis();
		long expMillis	= nowMillis	+	TOKEN_TIMEOUT_INMILISECOND;
		
		JSONObject obj = new JSONObject();
		
		String token = null;
		JwtBuilder builder = null;
		
		final String authHeader = request.getHeader("Authorization");

						
			/**
			 *  getting claims from bd or properties! userID , get from parameter request!
			 */
			 builder = Jwts.builder().setSubject("Joe").
								setIssuedAt(new Date(nowMillis)).						
								signWith(SignatureAlgorithm.HS512, "alorkey");
			
		
		/*
		if (expMillis > 0){
			builder.setExpiration(new Date(expMillis));
		}*/
		
		token	= builder.compact();		
		PrintWriter out = resp.getWriter();

		obj.put("TOKEN", token);
		out.print(obj);
		
		
				
	}
}
