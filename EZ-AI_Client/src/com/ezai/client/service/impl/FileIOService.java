package com.ezai.client.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ezai.client.service.IFileIOService;

public class FileIOService implements IFileIOService {

	private static IFileIOService instance = null;
	private FileIOService() {}
	public static IFileIOService getInstance() {
		if (instance == null) {
			instance = new FileIOService();
		}
		return instance;
	}
	
	@Override
	public Map<String, String> getCSVContent(String filename) throws IOException {
		Map<String, String> content = new HashMap<String, String>();
		File file = new File(filename);
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	String attr = line.split(",")[0];
		    	String value = line.split(",")[1];
		    	content.put(attr, value);
		    }
		    br.close();
		}
		return content;
	}

	@Override
	public void writeSCVAttribute(String filename, String attribute, String value) throws FileNotFoundException, IOException {
		File file = new File(filename);
		String newContent = "";
		//	Get the new file content
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    boolean found = false;
		    while ((line = br.readLine()) != null) {
		    	String attr = line.split(",")[0];
		    	if (attr.equalsIgnoreCase(attribute)) {
		    		newContent += attribute + "," + value + "\n";
		    		found = true;
		    	}
		    	else {
		    		newContent += line + "\n";
		    	}
		    }
		    if (!found) {
	    		newContent += attribute + "," + value + "\n";
		    }
		    br.close();
		}
		newContent = newContent.substring(0, newContent.length()-1);
		
		//	Write the new content to the file
		FileWriter fileWriter = new FileWriter(filename);
		fileWriter.write(newContent);
		fileWriter.close();
	}

}
