package com.databaseserver.server.helpers;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.json.HTTPTokener;
import org.json.JSONException;
import org.json.JSONObject;

public final class RoutingUtilities
{
	private RoutingUtilities() {}

	// Convenience method for sending an internal server error message.
	public static void sendServerError(Request baseRequest, HttpServletResponse response) throws IOException
	{
		System.out.println("Indicating server error.");
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		response.getWriter().println("<h1>Internal Server Error</h1>");
		baseRequest.setHandled(true);
		return;
	}
	
	public static void sendOK(Request baseRequest, HttpServletResponse response, String optionalData) throws IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		if (optionalData != null) response.getWriter().println(optionalData);
		baseRequest.setHandled(true);
		return;
	}

	// Convenience method for sending a not authorized message.
	public static void sendUnauthorized(Request baseRequest, HttpServletResponse response) throws IOException
	{
		System.out.println("Denying server access.");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().println("<h1>Not Authorized</h1>");
		baseRequest.setHandled(true);
		return;
	}

	// If the request is for a favicon, ignore it and continue.
	// Returns true if the request was not for a favicon.
	public static boolean faviconRequestFilter(Request baseRequest, HttpServletRequest request, HttpServletResponse response)
	{
		// Skip favicon request.
		String requestURI = request.getRequestURI();
		if (requestURI != null && requestURI.length() > 0 && requestURI.contains("favicon")) {
			System.out.println("Skipping favicon request.");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			baseRequest.setHandled(true);
			return false;
		}
		return true;
	}
	
	private static JSONObject getBodyAsJSON(HttpServletRequest request) throws IOException, JSONException {
		// Read from request
		StringBuilder buffer = new StringBuilder();
		BufferedReader reader = request.getReader();
		String line;
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		String data = buffer.toString();	
		
		return new JSONObject(data);
	}

	private static boolean handleFrequencyQuery(Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException {
		
		JSONObject requestBody = getBodyAsJSON(request);
		String sortOrder = requestBody.getString("sortOrder");
		String fromDate = requestBody.getString("fromDate");
		String toDate = requestBody.getString("toDate");
		
		QueryExecution.openConnection();
		ArrayList<JSONObject> results = QueryExecution.queryByFrequency(fromDate.split("T")[0], toDate.split("T")[0], sortOrder);
		QueryExecution.closeConnection();
		
		String finalResultString = "[";
		
		for (JSONObject myJsonObject : results) {
			finalResultString += myJsonObject.toString() + ",";
		}
		
		System.out.println(finalResultString);
		
		int indexOfLastComma = finalResultString.lastIndexOf(",");
		
		String noLastComma = finalResultString.substring(0, indexOfLastComma);
		
		noLastComma += "]";
		
		System.out.println("Frequency request with params:\n" + sortOrder + "\n" + fromDate + "\n" + toDate + "\n");
		sendOK(baseRequest, response, noLastComma);
		return true; 
	}

	private static boolean handleSpecificQuery(Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
		try {
			System.out.println("check");
			// Read from request
			StringBuilder buffer = new StringBuilder();
			BufferedReader reader = request.getReader();
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			String data = buffer.toString();	
			
			JSONObject requestBody = new JSONObject(data);
			String fromDate = requestBody.getString("fromDate");
			String toDate = requestBody.getString("toDate");
			
			QueryExecution.openConnection();
			ArrayList<JSONObject> results = QueryExecution.querySpecific(fromDate.split("T")[0], toDate.split("T")[0]);
			QueryExecution.closeConnection();
			
			String finalResultString = "[";
			
			for (JSONObject myJsonObject : results) {
				finalResultString += myJsonObject.toString() + ",";
			}
			
			System.out.println(finalResultString);
			
			int indexOfLastComma = finalResultString.lastIndexOf(",");
			
			String noLastComma = finalResultString.substring(0, indexOfLastComma);
			
			noLastComma += "]";
			
			sendOK(baseRequest, response, noLastComma);
			
		} catch(Exception e) {
			System.out.println(e);
		}
		
		return true;
	}

	public static boolean handleURL(Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException {
		String[] queryKeysSeparatePairs = request.getQueryString().split("&");
		ArrayList<String> queryKeysAndValues = new ArrayList<String>();
		for (String str : queryKeysSeparatePairs) {
			queryKeysAndValues.add(str.split("=")[0]);
			queryKeysAndValues.add(str.split("=")[1]);
		}
		for (int i = 0; i < queryKeysAndValues.size(); i += 2) {
			if (queryKeysAndValues.get(i) == null || queryKeysAndValues.get(i + 1) == null) continue;
			if (queryKeysAndValues.get(i).equals("databaseQuery")) {
				if (queryKeysAndValues.get(i + 1).equals("frequency")) {
					return handleFrequencyQuery(baseRequest, request, response);
				} else if (queryKeysAndValues.get(i + 1).equals("specific")) {
					return handleSpecificQuery(baseRequest, request, response);
				}
			}
		}

		return false;
	}

}










