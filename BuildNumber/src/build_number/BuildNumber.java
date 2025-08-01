/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package build_number;

import java.io.*;

/**
 * Object that gets build number and version of current application.
 * @author vakol
 *
 */
public class BuildNumber {
	
	/**
	 * Get build number.
	 */
	public static String getBuildNumber() {
		
		String text = "unknown";
		try {
			InputStream in = BuildNumber.class.getResourceAsStream("build_number.txt"); 
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			text = reader.readLine();
			reader.close();
		}
		catch (IOException e) {
		}
		
		return text;
	}
	
	/**
	 * Get version
	 */
	public static String getVersion() {
		
		String text = "unknown";
		try {
			InputStream in = BuildNumber.class.getResourceAsStream("version.txt"); 
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			text = reader.readLine();
			reader.close();
		}
		catch (IOException e) {
		}
		
		return text;
	}
}
