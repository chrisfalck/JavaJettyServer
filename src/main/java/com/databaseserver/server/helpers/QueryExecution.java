package com.databaseserver.server.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.text.Normalizer.Form;
import java.util.*;

import javax.print.attribute.ResolutionSyntax;

import org.json.JSONObject;

public class QueryExecution {
    // Oracle stuff.
    static Connection connection;
    static Statement statement;
    public QueryExecution() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    // Always call this to establish a connection to the Oracle database.
    public static void openConnection() {
        try {
            connection = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:xe", "system", "admin");
            statement = connection.createStatement();
        } catch(Exception e) {
            System.out.println(e);
        }
    }
    
    public static ArrayList<JSONObject> executeGeneralQuery(JSONObject requestBody) {
    	try {

    		JSONObject returnValues = (JSONObject)requestBody.get("returnValues");
    		
    		String query = requestBody.getString("query");
    		System.out.println(query);
    		
    		ResultSet rs = statement.executeQuery(query);
    		
    		ArrayList<JSONObject> queryResults = new ArrayList<JSONObject>();
            while(rs.next()) {
            	JSONObject currentResult = new JSONObject();
            	
            	// For each value on the returnValues JSONObject, pull the correct value and type of value 
            	// from the current database row and add the final JSONObject to the query results. 
            	Iterator keys = returnValues.keys();
            	while(keys.hasNext()) {
            		String resultName = keys.next().toString();
            		String resultType = returnValues.getString(resultName);
            		if (resultType.equals("int")) 	 currentResult.append(resultName, rs.getInt(resultName));
            		if (resultType.equals("string")) currentResult.append(resultName, rs.getString(resultName));
            		if (resultType.equals("date"))	 currentResult.append(resultName, rs.getDate(resultName));
            	}

            	queryResults.add(currentResult);
            }
            
            return queryResults;
    	}
    	catch (Exception e) {
    		System.err.println(e.toString() + "\n" + e.getStackTrace());
    	}

    	return null;
    }
    

    // Always call this when done to close the connection to the database.
    public static void closeConnection() {
        try {
            connection.close();
        } catch(Exception e) {
            System.out.println(e);
        }
    }

}
