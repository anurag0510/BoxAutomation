package com.testcases;

import static io.restassured.RestAssured.given;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.examples.Utils;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonSchemaFactoryBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.specification.ProxySpecification;
import io.restassured.specification.RequestSpecification;
import unitee.assertions.Assertions;

public class ABC {

	public static void main(String arg[]) throws Exception {
		 System.out.println( "Starting Json Validation." );
		 ABC app = new ABC();
		 String jsonData = readFile(".\\testmile-arjuna-1.0.0-b\\testmile-arjuna\\data\\sources\\Schemas\\Search.json", StandardCharsets.UTF_8);
		 String jsonSchema = readFile(".\\testmile-arjuna-1.0.0-b\\testmile-arjuna\\data\\sources\\Schemas\\SearchSchema.json" ,StandardCharsets.UTF_8);
		 app.validate(jsonData, jsonSchema);
	}
	
	public boolean validate(String jsonData, String jsonSchema) {
	    ProcessingReport report = null;
	    boolean result = false;
	    try {
	        System.out.println("Applying schema: @<@<"+jsonSchema+">@>@ to data: #<#<"+jsonData+">#>#");
	        JsonNode schemaNode = JsonLoader.fromString(jsonSchema);
	        JsonNode data = JsonLoader.fromString(jsonData);         
	        JsonSchemaFactory factory = JsonSchemaFactory.byDefault(); 
	        JsonSchema schema = factory.getJsonSchema(schemaNode);
	        report = schema.validate(data);
	    } catch (JsonParseException jpex) {
	        System.out.println("Error. Something went wrong trying to parse json data: #<#<"+jsonData+
	                ">#># or json schema: @<@<"+jsonSchema+">@>@. Are the double quotes included? "+jpex.getMessage());
	    } catch (ProcessingException pex) {  
	        System.out.println("Error. Something went wrong trying to process json data: #<#<"+jsonData+
	                ">#># with json schema: @<@<"+jsonSchema+">@>@ "+pex.getMessage());
	    } catch (IOException e) {
	        System.out.println("Error. Something went wrong trying to read json data: #<#<"+jsonData+
	                ">#># or json schema: @<@<"+jsonSchema+">@>@");
	    }
	    if (report != null) {
	        Iterator<ProcessingMessage> iter = report.iterator();
	        while (iter.hasNext()) {
	            ProcessingMessage pm = iter.next();
	            System.out.println("Processing Message: "+pm.getMessage());
	        }
	        result = report.isSuccess();
	    }
	    System.out.println(" Result=" +result);
	    return result;
	}
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
	
}
