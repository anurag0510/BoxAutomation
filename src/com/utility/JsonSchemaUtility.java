package com.utility;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

public class JsonSchemaUtility {

	public static boolean validateSchema(String responseBody, String schemaDetails) throws IOException, ProcessingException {
		String jsonSchema = readFile(schemaDetails, StandardCharsets.UTF_8);
		ProcessingReport report = null;
	    boolean result = false;
	    JsonNode schemaNode = JsonLoader.fromString(jsonSchema);
        JsonNode data = JsonLoader.fromString(responseBody);         
        JsonSchemaFactory factory = JsonSchemaFactory.byDefault(); 
        JsonSchema schema = factory.getJsonSchema(schemaNode);
        report = schema.validate(data);
        if (report != null) {
	        Iterator<ProcessingMessage> iter = report.iterator();
	        while (iter.hasNext()) {
	            ProcessingMessage pm = iter.next();
	            System.out.println("Processing Message: "+pm.getMessage());
	        }
	        result = report.isSuccess();
	    }
		return result;
	}

	private static String readFile(String schemaDetails, Charset encoding) throws IOException {
		String path = ".\\testmile-arjuna-1.0.0-b\\testmile-arjuna\\data\\sources\\Schemas\\" + schemaDetails + ".json";
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		  return new String(encoded, encoding);
	}
	
}
