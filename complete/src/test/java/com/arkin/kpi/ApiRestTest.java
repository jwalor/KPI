package com.arkin.kpi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 * @author jalor
 *
 */

public class ApiRestTest {
	
	 private 	HttpClient client ;
	 private	String 		url = "http://localhost:8080/process/streaming/2/1333";
	 private final String USER_AGENT = "Mozilla/5.0";
	 
	 //@Before
	 public void settingUp() throws Exception {
		 client = HttpClientBuilder.create().build();
		
	 }
	
	
     @Test
 	 public void callMethod2() throws Exception {
    	  
    	
          String userName = "[77admin88888888888888888]";//"User clientId=23421. Some more text clientId=33432. This clientNum=100";//"[admin]";
          Pattern pattern = Pattern.compile("([a-zA-Z]+)([0-9]*)");
          Matcher matcher = pattern.matcher(userName);
          if (matcher.find())
          {
              System.out.println(" ::::::::::::: " + matcher.group());
          }
      }
      
      
	 @Test
	 public void callMethod1() throws Exception {
		 
		 HttpPost post = new HttpPost(url);
		 
		 post.addHeader("User-Agent", USER_AGENT);
		 
		 post.setHeader("Accept",
	             "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		 post.setHeader("Accept-Language", "en-US,en;q=0.5");
		
		 post.setHeader("Connection", "keep-alive");
		 post.setHeader("Referer", "https://accounts.google.com/ServiceLoginAuth");
		 post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		 
		 ///////////////////////////////////////////////////
		 
		 
		 HttpResponse response = client.execute(post);

		 ///////////////////////////////////////////////////
		 
		 System.out.println("Response Code : "
		                 + response.getStatusLine().getStatusCode());
		 
		 BufferedReader rd = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));

		 StringBuffer result = new StringBuffer();
		 String line = "";
		 
		 while ((line = rd.readLine()) != null) {
			result.append(line);
		 }
		 
	 }
	 
	 
}
