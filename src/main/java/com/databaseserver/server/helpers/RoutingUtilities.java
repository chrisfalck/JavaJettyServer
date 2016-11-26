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
	
	public static JSONObject getBodyAsJSON(HttpServletRequest request) throws IOException, JSONException {
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
	
	/**
	 * For each query result (represented as a JSONObject), pull the string result out and use it to build an array of JSON.
	 * @param queryResults
	 * @return
	 */
	private static String convertJSONArrayToString(ArrayList<JSONObject> queryResults) {
			String finalResultString = "[";
			
			for (JSONObject myJsonObject : queryResults) {
				finalResultString += myJsonObject.toString() + ",";
			}
			
			int indexOfLastComma = finalResultString.lastIndexOf(",");

			finalResultString = finalResultString.substring(0, indexOfLastComma);
			finalResultString += "]";	
			
			return finalResultString;
	}
	

	private static boolean handleFrequencyQuery(Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException {
		
		JSONObject requestBody = getBodyAsJSON(request);
		String sortOrder = requestBody.getString("sortOrder");
		String fromDate = requestBody.getString("fromDate");
		String toDate = requestBody.getString("toDate");
		
		QueryExecution.openConnection();
		ArrayList<JSONObject> results = QueryExecution.queryByFrequency(fromDate.split("T")[0], toDate.split("T")[0], sortOrder);
		QueryExecution.closeConnection();
		
		sendOK(baseRequest, response, convertJSONArrayToString(results));
		return true; 
	}

	private static boolean handleSpecificQuery(Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
		try {

			JSONObject requestBody = getBodyAsJSON(request);
			String fromDate = requestBody.getString("fromDate");
			String toDate = requestBody.getString("toDate");
			
			QueryExecution.openConnection();
			ArrayList<JSONObject> results = QueryExecution.querySpecific(fromDate.split("T")[0], toDate.split("T")[0]);
			QueryExecution.closeConnection();
			
			sendOK(baseRequest, response, convertJSONArrayToString(results));
			
		} catch(Exception e) {
			System.out.println(e);
		}
		
		return true;
	}
	
	private static boolean handleInitialDateRange(Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
		try {
			QueryExecution.openConnection();
			ArrayList<JSONObject> results = QueryExecution.queryForInitialDateRange();
			QueryExecution.closeConnection();
			
			sendOK(baseRequest, response, convertJSONArrayToString(results));
		} 
		catch (Exception e) {
			System.out.println(e);
		}
		
		return true;
	}

	// Returns false if the parent server should resolve the request and true if the request is resolved by a handler. 
	public static boolean handleURL(Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException {
		String[] queryKeysSeparatePairs = request.getQueryString().split("&");
		ArrayList<String> queryKeysAndValues = new ArrayList<String>();
		
		// Parse the URL into a String where successive indices like 0 and 1 are the key and value of the query string. 
		for (String str : queryKeysSeparatePairs) {
			queryKeysAndValues.add(str.split("=")[0]);
			queryKeysAndValues.add(str.split("=")[1]);
		}
		
		// Determine the kind of URL.
		for (int i = 0; i < queryKeysAndValues.size(); i += 2) {
			if (queryKeysAndValues.get(i) == null || queryKeysAndValues.get(i + 1) == null) continue;
			if (queryKeysAndValues.get(i).equals("databaseQuery")) {
				
				System.out.println("Made it inside handler select block.");
				// Choose an appropriate handler for the type of database query. 
				String queryType = queryKeysAndValues.get(i + 1);
				if (queryType.equals("frequency")) {
					System.out.println("\nCrime frequency request.");
					return handleFrequencyQuery(baseRequest, request, response);
				} 
				else if (queryType.equals("specific")) {
					System.out.println("\nSpecific crime request.");
					return handleSpecificQuery(baseRequest, request, response);
				}
				else if (queryType.equals("initialDateRange")) {
					System.out.println("\nInitial date range request.");
					return handleInitialDateRange(baseRequest, request, response);
				}
				
			}
		}

		return false;
	}

}










