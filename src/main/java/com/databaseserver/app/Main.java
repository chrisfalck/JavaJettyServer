package com.databaseserver.app;

import org.eclipse.jetty.server.Server;
import java.util.Scanner;
import com.databaseserver.server.JettyServer;

public class Main {
	public static void main(String[] args) throws Exception {
		String passwordToUse = "toast";/*System.getenv("databasesPassword");*/
		if (passwordToUse == null) 
		{
			System.out.println("Enter a server password:");
			Scanner scanner = new Scanner(System.in);
			passwordToUse = scanner.next();
			scanner.close();
		}

		// Create a new server on port 8080.
		Server server = new Server(9090);

		// Pass our JettyServer handler as this server's handler.
		server.setHandler(new JettyServer(passwordToUse));

		// Start the server that has been configured.
		server.start();
		server.join();
	}
}
