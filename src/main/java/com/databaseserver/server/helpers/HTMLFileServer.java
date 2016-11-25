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
		
		// Path separator dependent. 
		String systemDependentPathToStaticFiles = "/home/ryan/git/JavaJettyServer/static_files/" + fileName;
		
		// Path separator independent. 
		// Rebuild the string with the system's path separator. 
		String systemIndependentPathToStaticFiles = ""; 
		String[] pathPieces = systemDependentPathToStaticFiles.split("/");
		for (String filePathPiece : pathPieces) 
		{
			systemIndependentPathToStaticFiles += filePathPiece + File.separator;
		}

		// Turn the path string into a URI that can be passed to the Files class. 
		Path filePath = Paths.get(systemIndependentPathToStaticFiles);
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