package com.kpi.frontend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.auth0.jwt.JWTVerifier;
import com.kpi.frontend.configuration.ConnectionSettings;



@WebFilter (filterName= "jwt-filter", urlPatterns = { "/items/*","/page-main.html","/secured/*","/WEB-INF/views/*" },
initParams={@WebInitParam(name="LOGIN_URI",value="/login.html"),
		@WebInitParam(name="PAGE_MAIN_URI",value="/page-main.html"),		
		@WebInitParam(name="VERIFY_URI",value="/secured/token"),
		@WebInitParam(name="SECURITY",value="true")}
		)
@Component
public class KPIFrontFilter implements Filter {

	private static final Log log = LogFactory.getLog(KPIFrontFilter.class);
	private String	urlPageMain;
	private String	urlVerfifyToken;
	private static final long	TOKEN_TIMEOUT_INMILISECOND	=	20000;
	
	
	@Autowired
	private ConnectionSettings connectionSettings;

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		HttpServletResponse response = (HttpServletResponse) res;
		HttpServletRequest request = (HttpServletRequest) req;
		
		String strUri = request.getServletPath();
		
		String token = getToken(request , response, chain ,strUri);
	
    	request.setCharacterEncoding("UTF-8");
    	response.setContentType("text/html; charset=UTF-8");
 		response.setCharacterEncoding("UTF-8");
 		/* don't allow caching */ 
 		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
 		response.setHeader("Pragma", "No-cache");
 		// to allow framing of this content only by this site
 		response.setHeader( "X-FRAME-OPTIONS", "SAMEORIGIN" );
 		response.setDateHeader("Expires", 0);

 		response.setHeader("Access-Control-Allow-Origin", "*");
 		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
 		response.setHeader("Access-Control-Max-Age", "3600");
 		response.setHeader("Access-Control-Allow-Headers", "Authorization");
 		
		log.info("request type-----------------"+request.getMethod());
		log.info("URL :"+request.getServletPath());
		try {
		
		
			 // Do something with decoded information like UserId or others claims
	    	 final Claims claims = Jwts.parser().setSigningKey("alorkey")
	    	            .parseClaimsJws(token).getBody();
	 	
	    	
	    	 
	    	 /*
	    	 
	    	 if (StringUtils.isNotBlank(strUri) && ( strUri.equals(urlVerfifyToken) || strUri.equals(urlPageMain) ) ) {
	    		 
	    		 String authHeader = request.getHeader("Authorization");
	    		 response.setHeader("Authorization", authHeader);
	    		 
	    		 Boolean	 refreshToken = new Boolean(request.getParameter("refreshToken"));
	    		 JSONObject obj = new JSONObject();
	    		 if ( refreshToken){	    			 		     		     			   
		     		 obj.put("REFRESH_TOKEN", refreshToken);
		     		 
	    		 }else {
	    			 obj.put("REFRESH_TOKEN", false);
	    		 }	 
	    		 
	    		 obj.put("TOKEN", token);	
	     		 PrintWriter out = response.getWriter();
	     		 out.print(obj);
	     		 
	     		 //response.sendRedirect(request.getContextPath() + urlPageMain );
	 		}*/
	    	 
	    	 // redirect  to next servlet required...
	    	 chain.doFilter(req, res);
    		 
	 		
	        
	    } catch (ExpiredJwtException e) {
	    	
	    	 if (StringUtils.isNotBlank(strUri) && (strUri.equals(urlVerfifyToken)) ) {
	    		 
	    		 long nowMillis = System.currentTimeMillis();
	    		 long expMillis	= nowMillis	+	TOKEN_TIMEOUT_INMILISECOND;
	    		 JwtBuilder builder = Jwts.builder().setSubject("Joe").
							setIssuedAt(new Date(nowMillis)).						
							signWith(SignatureAlgorithm.HS512, "alorkey");
	    		 
	    		 /*if (expMillis > 0){
						builder.setExpiration(new Date(expMillis));
					}*/
				
	    		 
	    		token	= builder.compact();		
	    		PrintWriter out = response.getWriter();
	    		
	    		JSONObject obj = new JSONObject();
	    		obj.put("TOKEN", token);
	    		obj.put("REFRESH_TOKEN", true);
	    		out.print(obj);
	    		
	    		//response.sendRedirect(request.getContextPath() + urlPageMain );
	    		
	    	 }else{
	    	
		    	response.sendRedirect(request.getContextPath() + "/warning.html" );
		    	log.warn("Unauthorized: Access_token is expired");
		    	//throw new ServletException("Unauthorized: Access_token is expired", e);
	    	 }
	    }
	    catch (final SignatureException e) {
	        throw new ServletException("Unauthorized: Token validation failed.", e);
	        // registrar fecha,ip de intento de acceso no autorizado.
	    }

		
	}

	public void init(FilterConfig filterConfig) {
		
		urlPageMain = filterConfig.getInitParameter("PAGE_MAIN_URI");		
		urlVerfifyToken = filterConfig.getInitParameter("VERIFY_URI");
		
		ServletContext servletContext = filterConfig.getServletContext();
		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);

		AutowireCapableBeanFactory autowireCapableBeanFactory = webApplicationContext.getAutowireCapableBeanFactory();

		/**
		 *  Mecanismo de inyeccion manual debido a que Filter no pertenece al contexto Spring , sino del Servlet
		 */
		ConnectionSettings connectionSettings  = new ConnectionSettings();
		autowireCapableBeanFactory.autowireBean(connectionSettings);

	}


	@SuppressWarnings("unused")
	private String getToken(HttpServletRequest httpRequest , HttpServletResponse response , FilterChain chain , String strUri) throws ServletException, IOException {
		
		String token = null;
	    Boolean refreshToken = false;
	    
		 if (StringUtils.isNotBlank(strUri) && (strUri.equals(urlPageMain) || strUri.equals(urlVerfifyToken)) ) {
			
			 token =  httpRequest.getParameter("Bearer");			
			 
			if ( token == null ) {
				throw new ServletException("Unauthorized:	Missing or Invalid Authorization header.");
			}
			
 		}else {
 			
 			final String  authHeader = httpRequest.getHeader("Authorization");
 			token = authHeader.substring(7); // The part after "Bearer "
 			
 			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
 				throw new ServletException("Unauthorized:	Missing or Invalid Authorization header.");
 			}
 		}	
		

		return token;
	}

	public void destroy() {}

}