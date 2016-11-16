package com.databaseserver.server.helpers;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;

public final class ValidationUtilities
{
  private ValidationUtilities() {}

  // Check for the existence of a query string and then validate it.
  // Return true if the query string exists and has at least one key value pair.
  public static boolean checkForQueryString(Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException
  {
    // Error if we have no query string.
    String queryString = request.getQueryString();
    if (queryString == null)
    {
      RoutingUtilities.sendUnauthorized(baseRequest, response);
      return false;
    }
    return validateQueryString(queryString, baseRequest, response);
  }
  public static boolean validateQueryString(String queryString, Request baseRequest, HttpServletResponse response) throws IOException
  {
    String[] queryKeysAndValues = queryString.split("=");
    if (queryKeysAndValues.length < 2)
    {
      RoutingUtilities.sendUnauthorized(baseRequest, response);
      return false;
    }
    return true;
  }

  // Check for password validity and return true if valid.
  public static boolean validatePassword(Request baseRequest, HttpServletRequest request, HttpServletResponse response, String correctPassword) throws IOException
  {
	String[] queryKeysSeparatePairs = request.getQueryString().split("&");
    String[] queryKeysAndValues = request.getQueryString().split("=");
    // Check query string for correct password.
    for (int i = 0; i < queryKeysAndValues.length - 1; i += 2)
    {
      if (queryKeysAndValues[i].equals("password") && queryKeysAndValues[i + 1] != null && queryKeysAndValues[i + 1].equals(correctPassword))
      {
        return true;
      }
    }

    System.out.println("Password incorrect: " + queryKeysAndValues[0] + queryKeysAndValues[1]);
    // If we made it here, we didn't find a valid password.
    RoutingUtilities.sendUnauthorized(baseRequest, response);
    return false;
  }

}
