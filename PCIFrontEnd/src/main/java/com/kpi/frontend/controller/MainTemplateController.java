package com.kpi.frontend.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/pages")
public class MainTemplateController {
	
	private static final Log log = LogFactory.getLog(IndexController.class);

    @RequestMapping(value="/configuration/person/searchPerson.html" ,method = RequestMethod.GET)
	public String getPagePerson( Model model) {
		return "configuration/person/searchPerson";
	}
    
    
}
