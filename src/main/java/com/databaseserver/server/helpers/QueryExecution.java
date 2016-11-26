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
            System.out.println("Oracle database connection instantiated.");
        } catch(Exception e) {
            System.out.println(e);
        }
    }
    
    public static ArrayList<JSONObject> querySpecific(String fromDate, String toDate) {
    	
    	try {
    		ResultSet rs = statement.executeQuery("select * from Incident, CrimeTime where General_Offense_Number = Offense_Number and Crime_Date > To_Date('" + fromDate + "', 'YYYY-MM-DD') and Crime_Date < To_Date('" + toDate + "', 'YYYY-MM-DD')");
    		ArrayList<JSONObject> queryResults = new ArrayList<JSONObject>();
    		while(rs.next()) {
    			JSONObject currentResult = new JSONObject();
    			currentResult.append("general_offense_number", rs.getInt("general_offense_number"));
    			currentResult.append("event_code", rs.getInt("event_Code"));
    			currentResult.append("addr", rs.getString("addr"));
    			currentResult.append("crime_date", rs.getDate("crime_date"));
    			currentResult.append("time", rs.getString("time"));
    			queryResults.add(currentResult);
    		}
    		
    		return queryResults;
    	} catch(Exception e) {
    		System.out.println(e);
    		System.out.println(e.getStackTrace());
    	}
    	
    	return null;
    }
    
    public static ArrayList<JSONObject> queryForInitialDateRange() {
    	try {
    		ResultSet resultSetMinDate = statement.executeQuery("select min(Crime_Date) from CrimeTime");

    		JSONObject dates = new JSONObject();

    		if (resultSetMinDate.next()) {
    			dates.append("fromDate", resultSetMinDate.getString(1));
    		} 
    		else {
    			System.out.println("No min date.");
    		}
    		
    		ResultSet resultSetMaxDate = statement.executeQuery("select max(Crime_Date) from CrimeTime");
    		
    		if (resultSetMaxDate.next()) {
    			dates.append("toDate", resultSetMaxDate.getString(1));
    		} 
    		else {
    			System.out.println("No max date.");
    		}
    		
    		ArrayList<JSONObject> returnArray = new ArrayList<JSONObject>();
    		returnArray.add(dates);
    		
    		return returnArray; 
		} 
    	catch (Exception e) {
    		System.err.println(e.toString() + "\n" + e.getStackTrace());
		}
    	
    	return null;
    };
    
    public static ArrayList<JSONObject> queryByFrequency(String fromDate, String toDate, String sortOrder) {
    	
        try {
            String order = "";
            if(sortOrder.equals("Most Frequent First")) {
                order = "desc";
            } else {
                order = "asc";
            }
            String select = "event_code, event_clearance_description, group_name, count(event_code) as num";
            String tables = "Incident, Type, CrimeTime";
            String conditions = "event_code = event_clearance_code and general_offense_number = offense_number and Crime_Date > To_Date('" + fromDate + "', 'YYYY-MM-DD') and Crime_Date < To_Date('" + toDate + "', 'YYYY-MM-DD')";
            String groupBy = "event_code, event_clearance_description, group_name";

            ResultSet rs = statement.executeQuery("select " + select +
            		" from " + tables + 
            		" where " + conditions + 
            		" group by " + groupBy + " order by count(event_code) " + order);

            ArrayList<JSONObject> queryResults = new ArrayList<JSONObject>();
            while(rs.next()) {
            	JSONObject currentResult = new JSONObject();
            	currentResult.append("event_code", rs.getInt("event_code"));
            	currentResult.append("event_clearance_description", rs.getString("event_clearance_description"));
            	currentResult.append("group_name", rs.getString("group_name"));
            	currentResult.append("num", rs.getInt("num"));
            	queryResults.add(currentResult);
            }
            
            return queryResults;
        } catch(Exception e) {
            System.out.println(e);
            System.out.println(e.getStackTrace());
        }
        
        return null;
    }
    
    public static String executeQuery() {

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

    public static String getData() {
        return ("Hello World");
    }
}
