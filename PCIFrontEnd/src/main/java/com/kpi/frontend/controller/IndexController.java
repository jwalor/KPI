package com.kpi.frontend.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.kpi.frontend.security.TokenServlet;

@Controller
@RequestMapping("/")
public class IndexController {

	private static final Log log = LogFactory.getLog(IndexController.class);
	
	@RequestMapping(value="/pages-login.html" ,method = RequestMethod.GET)
	public String getIndexPage( Model model) {
		model.addAttribute("project","ARRIBA ALIANZA");		
		return "login";
	}
	
	
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value="/page-main.html" ,method = RequestMethod.POST)
    public String getPageMain( HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
		String authHeader = request.getHeader("Authorization");
		log.info("TOKEN PUBLICO :" + authHeader);
		log.info("Actualizar segun el token que la peticion fue por este metodo con url : /page-main.html");
			
		return "home";
    }
	
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value="/warning.html" ,method = RequestMethod.GET)
    public String getPageWarning( HttpServletRequest request,HttpServletResponse response, Model model) throws IOException {
		
		model.addAttribute("message","SESSION EXPIRED");	
		log.info("Actualizar según el token que la peticion fue por este metodo con url : /page-main.html");
	
		
		return "warning";
    }
	 
	
}