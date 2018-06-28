package com.Utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utilities {
	
	/**
	 * Formatiert mehrere Suchparameter für Word2Vec Demo
	 * @param searchTerms
	 * @return
	 */
	public static String[] capitalizeFirstLetter(String[] searchTerms) {
		for (String str : searchTerms) {
			if (str != null && str.length() > 0) {
				str = str.substring(0, 1).toUpperCase() + str.substring(1);
			}
		}
		return searchTerms;
	}

	/**
	 * Formatiert einen Suchparameter auf der Suche-Seite
	 * @param searchTerm
	 * @return 
	 */
	public static String capitalizeFirstLetter(String searchTerm) {
		if (searchTerm != null && searchTerm.length() > 0) {
			searchTerm = searchTerm.substring(0, 1).toUpperCase() + searchTerm.substring(1);
		}
		return searchTerm;
	}
	
	/**
	 * Liest eine Datei ein
	 * @param filename
	 * @return Dateiinhalt als String
	 */
	public static String readFile(String filename, boolean skipFirstLine) {
		String result = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			StringBuilder sb = new StringBuilder();
			if(skipFirstLine) {
				br.readLine();
			}
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			result = sb.toString();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
