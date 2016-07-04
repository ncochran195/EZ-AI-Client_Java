package com.ezai.client.service;

import java.io.IOException;
import java.util.Map;

public interface IFileIOService {

	/**
	 * This method returns the mapping of the first column of CSV data to the second column of CSV data in all rows until an empty row.
	 * This is used to read all of the data for a configuration file.
	 * @param filename the name of the CSV file
	 * @return the mapping between the first and second rows
	 */
	public Map<String, String> getCSVContent(String filename) throws IOException;
	
	/**
	 * This method writes into the second column of a CST file with the first column's value equal to attribute, in the CSV vile found at filename,
	 * or appends the attribute and value to the end of the CSV file if the attribute is not found.
	 * This is used to write an attribute into or update a configuration file.
	 * @param filename the filename of the CSV file
	 * @param attribute the name of the first column to match
	 * @param value the value to write into the second column
	 */
	public void writeSCVAttribute(String filename, String attribute, String value) throws IOException;
}
