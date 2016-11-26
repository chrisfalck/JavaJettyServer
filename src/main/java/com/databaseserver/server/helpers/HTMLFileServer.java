package com.databaseserver.server.helpers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public final class HTMLFileServer 
{
	public static String prepareResponseString(String fileName) throws IOException 
	{
		// Turn the path string into a URI that can be passed to the Files class. 
		// String systemDependentPathToStaticFiles = "/home/ryan/git/JavaJettyServer/static_files/" + fileName;
		Path filePath = Paths.get("C:", "Users", "Chris", "git", "JavaJettyServer", "static_files", fileName);
		List<String> fileLines = Files.readAllLines(filePath);
		
		// Hold the final string to return.
		String preparedString = "";
		for (String str : fileLines) 
		{
			preparedString += "\n" + str;
		}
		return preparedString;
	}
}











//