package com.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;

import org.apache.commons.io.FileUtils;


public class BasicUtility {

	public static String getClientId() throws Exception {
		return("gtasvu4akum1q9ao6cracr6bzpvek1pt");
	}
	
	public static String getClientSecret() throws Exception {
		return("YKWcNgkCXeVhn0U9Kt0WJmdEgP7LYmuq");
	}

	public static String generateRandomString() {
		String randomString = UUID.randomUUID().toString();
		randomString = randomString.substring(0, 7);
		return randomString;
	}

	public static void copyDataToFile(String downloadedFile, String fileName) {
		File file = new File(".\\testmile-arjuna-1.0.0-b\\testmile-arjuna\\temp\\" + fileName);
		if(file.exists()) 
			file.delete();
		Writer writer = null;

		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(".\\testmile-arjuna-1.0.0-b\\testmile-arjuna\\temp\\" + fileName), "utf-8"));
		    writer.write(downloadedFile);
		} catch (IOException ex) {
		} finally {
		   try {writer.close();} catch (Exception ex) {/*ignore*/}
		}
	}

	public static boolean areFilesSame(String fileLocationOnMachine, String downloadedFileLocation) throws IOException {
		File file1 = new File(fileLocationOnMachine);
		File file2 = new File(downloadedFileLocation);
		boolean compareResult = FileUtils.contentEquals(file1, file2);
		return compareResult;
	}
}
