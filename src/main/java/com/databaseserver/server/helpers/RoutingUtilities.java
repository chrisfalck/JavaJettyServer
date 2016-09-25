package com.databaseserver.server.helpers;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

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

}
