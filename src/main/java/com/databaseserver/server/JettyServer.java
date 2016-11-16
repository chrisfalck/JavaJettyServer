package com.databaseserver.server;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

// Native classes.
import com.databaseserver.server.helpers.ValidationUtilities;
import com.databaseserver.server.helpers.RoutingUtilities;
import com.databaseserver.server.helpers.HTMLFileServer;

public class JettyServer extends AbstractHandler {
  String correctPassword = "";

  public JettyServer(String password) {
    this.correctPassword = password;
  }

  // Set up a handler that will handle requests to the server we open in Main.java .
  public void handle(
    String target,
    Request baseRequest,
    HttpServletRequest request,
    HttpServletResponse response )
    throws IOException, ServletException
  {
    boolean requestIsForFavicon = !RoutingUtilities.faviconRequestFilter(baseRequest, request, response);
    if (requestIsForFavicon) return;

    boolean queryStringIsNotValid = !ValidationUtilities.checkForQueryString(baseRequest, request, response);
    if (queryStringIsNotValid) return;

    boolean passwordIsNotValid = !ValidationUtilities.validatePassword(baseRequest, request, response, correctPassword);
    if (passwordIsNotValid) return;
    
    boolean queryHandlersSentResponse = RoutingUtilities.handleURL(baseRequest, request, response);
    if (queryHandlersSentResponse) return;

    // Declare response encoding and types.
    response.setContentType("text/html; charset=utf-8");

    // We received an authorized request and can continue with normal operation.
    response.setStatus(HttpServletResponse.SC_OK);
    
    
    
    
    
    String preparedHTMLFile = HTMLFileServer.prepareResponseString("index.html");

    // Write back response
    response.getWriter().println(preparedHTMLFile);

    // Inform jetty that this request has now been handled.
    baseRequest.setHandled(true);
    return;
  }
}














