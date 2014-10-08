/*
 * Copyright (c) 2014, Tim Verbelen
 * Internet Based Communication Networks and Services research group (IBCN),
 * Department of Information Technology (INTEC), Ghent University - iMinds.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of Ghent University - iMinds, nor the names of its 
 *      contributors may be used to endorse or promote products derived from 
 *      this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package org.example.servlet;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.example.api.Greeting;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class GreetingServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3650972146962409988L;
	private BundleContext context;
	
	public GreetingServlet(BundleContext context){
		this.context = context;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Greeting greeting = null;
		
		String name = req.getParameter("name");
		String type = req.getParameter("type");
		
		if(name==null)
			name = "";
		
		// fetch correct greeting service to call
		try {
			Collection<ServiceReference<Greeting>> refs = context.getServiceReferences(Greeting.class, "(greeting.type="+type+")");
			if(!refs.isEmpty()){
				greeting = context.getService(refs.iterator().next());
			}
		}catch(InvalidSyntaxException e){}

		// build response
		String response = "";
		
		// correct output
		if(name.equals("")){
			response = html.replace("${greeting}", "Greet the server ... please insert your name");
		} else if(greeting==null){
			response = html.replace("${greeting}", "No greeting service available...");
		} else {
			response = html.replace("${greeting}", greeting.greet(name));
		}
		
		// reset parameter values to previous ones
		response = response.replace("${name}", name);
		if(type!=null && type.equals("bye")){
			response = response.replace("${type_bye}", "checked");
			response = response.replace("${type_hi}", "");
		} else {
			response = response.replace("${type_hi}", "checked");
			response = response.replace("${type_bye}", "");
		}
		resp.getWriter().write(response);
	}
	
	private final String html = 
			"<html>"+
			"<head>"+
			"<title>Example Greeting Project</title>"+
			"</head>"+

			"<body>" +

			"<h2>${greeting}</h2>"+
			"<form name=\"input\" action=\"greet\" method=\"get\">" +
			"Insert your name: <input type=\"text\" name=\"name\" value=\"${name}\">"+
			"<br>"+
			"<input type=\"radio\" name=\"type\" value=\"hi\" ${type_hi}>Say hello<br>"+
			"<input type=\"radio\" name=\"type\" value=\"bye\" ${type_bye}>Say goodbye<br>"+
			"<input type=\"submit\" value=\"Submit\">" +
			"</form>" +

			"</body>"+
			"</html>";
}
