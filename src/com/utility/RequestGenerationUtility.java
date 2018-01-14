package com.utility;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.IOException;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.restassured.response.Response;
import unitee.assertions.Assertions;

public class RequestGenerationUtility {
	
	static String refreshToken = "ZT6boNk4ARGAL8lENcQXSq0r24Vmm3mca8XNm4xmfSSHFm3MDDpQX3k0gv1wKNJQ";

	public static String getAccessToken() throws Exception {
		String accessToken = "Bearer ";
		String baseURL = "https://www.box.com/api/oauth2/token";
		Response response = given().param("grant_type", "refresh_token").
				param("refresh_token", refreshToken).
				param("client_id", BasicUtility.getClientId()).
				param("client_secret", BasicUtility.getClientSecret()).
				post(baseURL);
		String responseBody = response.getBody().asString();
		Assertions.assertTrue("Was response for access token request a valid schema : ", JsonSchemaUtility.validateSchema(responseBody, "AccessTokenRequestSchema"));
		System.out.println(response.getStatusCode());
		refreshToken = JsonHandlingUtility.getJsonElementFromJsonLevelOne("refresh_token" ,responseBody);
		System.out.println("Refresh Token : " + refreshToken);
		accessToken = JsonHandlingUtility.getJsonElementFromJsonLevelOne("access_token" ,responseBody);
		return accessToken;
	}

	public static boolean isFolderPresent(String accessToken, String folderName) throws Exception {
		String baseURL = "https://api.box.com/2.0/search?query=" + folderName;
		int searchCount = 0;
		Response response = given().
				header("Accept", "application/json").
				header("Content-Type", "application/json").
				header("Authorization", accessToken).
				get(baseURL);
		String responseBody = response.getBody().asString();
		Assertions.assertTrue("Was Search Request completed successfully : ", response.statusCode() == 200);
		Assertions.assertTrue("Is response  for folder query schema validation a success : ", JsonSchemaUtility.validateSchema(responseBody, "SearchFolderRequestSchema"));
		searchCount = Integer.parseInt(JsonHandlingUtility.getJsonElementFromJsonLevelOne("total_count" ,responseBody));
		if(searchCount == 0)
			return false;
		else {
			if(responseBody.contains("\"type\": \"folder\""))
				return true;
		}
		return false;
	}

	public static void deleteFolder(String accessToken, long folderid) throws Exception {
		String baseURL = "https://api.box.com/2.0/folders/" + folderid + "?recursive=true";
		Response response = given().
				header("Authorization", accessToken).
				delete(baseURL);
		System.out.println(response.getStatusCode());
		Assertions.assertTrue("Is folder deleted successfully : ", response.getStatusCode() == 204);
	}

	public static long getFolderId(String accessToken, String folderName) throws IOException, ProcessingException, Exception {
		String baseURL = "https://api.box.com/2.0/search?query=" + folderName;
		Response response = given().
				header("Accept", "application/json").
				header("Content-Type", "application/json").
				header("Authorization", accessToken).
				get(baseURL);
		String responseBody = response.getBody().asString();
		Assertions.assertTrue("Is response  for folder query schema validation a success : ", JsonSchemaUtility.validateSchema(responseBody, "SearchFolderRequestSchema"));
		long folderId = JsonHandlingUtility.getFolderIdFromJson(responseBody);
		return folderId;
	}

	public static long createFolder(String accessToken, String folderName) throws Exception {
		String baseURL = "https://api.box.com/2.0/folders";
		Response response = given().
				header("Accept", "application/json").
				header("Content-Type", "application/json").
				header("Authorization", accessToken).
				body("{\"name\":\"" + folderName + "\",\"parent\":{\"id\":\"0\"}}").
				post(baseURL);
		String responseBody = response.getBody().asString();
		Assertions.assertTrue("Is response for folder creation request is 201 : ", response.getStatusCode() == 201);
		Assertions.assertTrue("Was create folder request schema validation a success : ", JsonSchemaUtility.validateSchema(responseBody, "CreateFolderRequestSchema"));
		long folderId = Long.parseLong(JsonHandlingUtility.getJsonElementFromJsonLevelOne("id" ,response.getBody().asString()));
		return folderId;
	}

	public static Object getFolderName(String accessToken, long folderId) throws Exception {
		String baseURL = "https://api.box.com/2.0/folders/" + folderId;
		Response response = given().
				header("Authorization", accessToken).
				get(baseURL);
		Assertions.assertTrue("Is folder present : ", response.getStatusCode() == 200);
		String responseBody = response.getBody().asString();
		Assertions.assertTrue("Is getting folder details schema valid : ", JsonSchemaUtility.validateSchema(responseBody, "SearchFolderRequestSchema"));
		String folderName = JsonHandlingUtility.getJsonElementFromJsonLevelOne("name" ,responseBody);
		return folderName;
	}

	public static boolean doesFolderExist(String accessToken, long folderId) throws IOException, ProcessingException, Exception {
		String baseURL = "https://api.box.com/2.0/folders/" + folderId;
		Response response = given().
				header("Authorization", accessToken).
				get(baseURL);
		if (response.statusCode() == 200)
			return true;
		else {
			if(response.statusCode() == 404) {
				String responseBody = response.getBody().asString();
				Assertions.assertTrue("Is folder absence schema valid : ", JsonSchemaUtility.validateSchema(responseBody, "FolderNotFoundRequestSchema"));
				return false;
			}
		}
		return true;
	}

	public static void renameFolder(String accessToken, long folderId, String renamedFolderName) throws Exception {
		String baseURL = "https://api.box.com/2.0/folders/" + folderId;
		Response response = given().
				header("Authorization", accessToken).
				body("{\"name\":\""+ renamedFolderName +"\",\"tags\":[]}").
				put(baseURL);
		Assertions.assertTrue("Was request success and Is Folder renamed successfully : ", response.getStatusCode() == 200 && JsonHandlingUtility.getJsonElementFromJsonLevelOne("name" ,response.getBody().asString()).equals(renamedFolderName));
		String responseBody = response.getBody().asString();
		Assertions.assertTrue("Is response schema for update folder valid : ", JsonSchemaUtility.validateSchema(responseBody, "UpdateFolderRequestSchema"));
	}

	public static void moveFolder(String accessToken, long folderId, long moveFolderId) throws Exception {
		String baseURL = "https://api.box.com/2.0/folders/" + folderId;
		Response response = given().
				header("Authorization", accessToken).
				body("{\"parent\":{\"id\":\"" + moveFolderId + "\"},\"tags\":[]}").
				put(baseURL);
		Assertions.assertTrue("Was request success : ", response.getStatusCode() == 200 );
		String responseBody = response.getBody().asString();
		Assertions.assertTrue("Is response schema for update folder valid : ", JsonSchemaUtility.validateSchema(responseBody, "UpdateFolderRequestSchema"));
	}

	public static long getFolderParentId(String accessToken, long folderId) throws Exception {
		String baseURL = "https://api.box.com/2.0/folders/" + folderId;
		Response response = given().
				header("Authorization", accessToken).
				get(baseURL);
		Assertions.assertTrue("Is folder present : ", response.getStatusCode() == 200);
		String responseBody = response.getBody().asString();
		Assertions.assertTrue("Is response schema valid : ", JsonSchemaUtility.validateSchema(responseBody, "SearchFolderRequestSchema"));
		JsonObject getJsonObjectFromJsonElement = JsonHandlingUtility.getJsonObjectFromJsonElement("path_collection", response.getBody().asString());
		JsonArray getJsonArrayFromJsonElement = JsonHandlingUtility.getJsonArrayFromJsonElement("entries", getJsonObjectFromJsonElement);
		long getParentIdFromJsonArray = JsonHandlingUtility.getJsonElementFromJsonArray("id", getJsonArrayFromJsonElement, 1);
		return getParentIdFromJsonArray;
	}

	public static String getFolderDescription(String accessToken, long folderId) throws Exception {
		String baseURL = "https://api.box.com/2.0/folders/" + folderId;
		Response response = given().
				header("Authorization", accessToken).
				get(baseURL);
		Assertions.assertTrue("Is folder present : ", response.getStatusCode() == 200);
		String responseBody = response.getBody().asString();
		Assertions.assertTrue("Is schema valid : ", JsonSchemaUtility.validateSchema(responseBody, "SearchFolderRequestSchema"));
		String folderDescription = JsonHandlingUtility.getJsonElementFromJsonLevelOne("description" ,responseBody);
		return folderDescription;
	}

	public static void updateFolderDescriptionTo(String accessToken, long folderId, String updatedFolderDescription) throws Exception {
		String baseURL = "https://api.box.com/2.0/folders/" + folderId;
		Response response = given().
				header("Authorization", accessToken).
				body("{\"description\":\"" + updatedFolderDescription + "\",\"tags\":[]}").
				put(baseURL);
		Assertions.assertTrue("Was request success : ", response.getStatusCode() == 200 );
		String responseBody = response.getBody().asString();
		Assertions.assertTrue("Is schema valid : ", JsonSchemaUtility.validateSchema(responseBody, "SearchFolderRequestSchema"));
	}

	public static long copyFolder(String accessToken, long folderId, long copyfolderId) throws Exception {
		String baseURL = "https://api.box.com/2.0/folders/" + folderId + "/copy";
		Response response = given().
				header("Authorization", accessToken).
				body("{\"parent\":{\"id\":\""+ copyfolderId +"\"}}").
				post(baseURL);
		Assertions.assertTrue("Was copy folder request success : ", response.statusCode() == 201);
		String responseBody = response.getBody().asString();
		Assertions.assertTrue("Is schema valid : ", JsonSchemaUtility.validateSchema(responseBody, "SearchFolderRequestSchema"));
		long generatedFolderId = Long.parseLong(JsonHandlingUtility.getJsonElementFromJsonLevelOne("id" ,responseBody));
		return generatedFolderId;
	}

	public static long copyFolderWithChangedName(String accessToken, long folderId, long copyfolderId, String updatedName) throws Exception {
		String baseURL = "https://api.box.com/2.0/folders/" + folderId + "/copy";
		Response response = given().
				header("Authorization", accessToken).
				body("{\"parent\":{\"id\":\"" + copyfolderId + "\"},\"name\":\"" + updatedName + "\"}").
				post(baseURL);
		Assertions.assertTrue("Was copy folder request success : ", response.statusCode() == 201);
		String responseBody = response.getBody().asString();
		Assertions.assertTrue("Is schema valid : ", JsonSchemaUtility.validateSchema(responseBody, "SearchFolderRequestSchema"));
		long generatedFolderId = Long.parseLong(JsonHandlingUtility.getJsonElementFromJsonLevelOne("id" ,responseBody));
		return generatedFolderId;
	}

	public static boolean isFileExists(String accessToken, String uploadFileName) throws Exception {
		String baseURL = "https://api.box.com/2.0/search?query=" + uploadFileName;
		int searchCount = 0;
		Response response = given().
				header("Accept", "application/json").
				header("Content-Type", "application/json").
				header("Authorization", accessToken).
				get(baseURL);
		String responseBody = response.getBody().asString();
		Assertions.assertTrue("Was Search Request completed successfully : ", response.statusCode() == 200);
		searchCount = Integer.parseInt(JsonHandlingUtility.getJsonElementFromJsonLevelOne("total_count" ,responseBody));
		if(searchCount == 0)
			return false;
		else {
			if(responseBody.contains("\"type\": \"file\""))
				return true;
		}
		return false;
	}

	public static long getFileId(String accessToken, String uploadFileName) {
		String baseURL = "https://api.box.com/2.0/search?query=" + uploadFileName;
		Response response = given().
				header("Accept", "application/json").
				header("Content-Type", "application/json").
				header("Authorization", accessToken).
				get(baseURL);
		String responseBody = response.getBody().asString();
		long folderId = JsonHandlingUtility.getFolderIdFromJson(responseBody);
		return folderId;
	}

	public static boolean deleteFile(String accessToken, long fileId) throws Exception {
		String baseURL = "https://api.box.com/2.0/files/" + fileId;
		Response response = given().
				header("Accept", "application/json").
				header("Content-Type", "application/json").
				header("Authorization", accessToken).
				delete(baseURL);
		Assertions.assertTrue("Is file deleted successfully : ", response.getStatusCode() == 204);
		return true;
	}

	public static long uploadFile(String accessToken, String uploadFileName, String fileLocationOnMachine) throws Exception {
		String baseURL = "https://upload.box.com/api/2.0/files/content";
		Response response = given().
				header("Authorization", accessToken).
				multiPart("attributes", "{\"name\": \"" + uploadFileName + "\",\"parent\": {\"id\" : \"0\"}}").
				multiPart(new File(fileLocationOnMachine)).
				post(baseURL);
		Assertions.assertTrue("Was file upload request a success : ", response.getStatusCode() == 201);
		long fileId;
		JsonObject jobj = new Gson().fromJson(response.getBody().asString(), JsonObject.class);
		JsonArray getJsonArrayFromJsonElement = JsonHandlingUtility.getJsonArrayFromJsonElement("entries", jobj);
		fileId = JsonHandlingUtility.getJsonElementFromJsonArray("id", getJsonArrayFromJsonElement, 0);
		return(fileId);
	}

	public static String getFileName(String accessToken, long fileId) throws Exception {
		String baseURL = "https://api.box.com/2.0/files/" + fileId;
		Response response = given().
				header("Authorization", accessToken).
				get(baseURL);
		Assertions.assertTrue("Was search file request success : ", response.getStatusCode() == 200);
		String responseBody = response.getBody().asString();
		String fileName = refreshToken = JsonHandlingUtility.getJsonElementFromJsonLevelOne("name" ,responseBody);
		return fileName;
	}

	public static boolean isFilePresent(String accessToken, long fileId) {
		String baseURL = "https://api.box.com/2.0/files/" + fileId;
		Response response = given().
				header("Authorization", accessToken).
				get(baseURL);
		if(response.getStatusCode() == 200)
			return true;
		return false;
	}

	public static void renameFile(String accessToken, long fileId, String renamedFileName) throws Exception {
		String baseURL = "https://api.box.com/2.0/files/" + fileId;
		Response response = given().
				header("Accept", "application/json").
				header("Content-Type", "application/json").
				header("Authorization", accessToken).
				body("{\"name\":\"" + renamedFileName + "\",\"tags\":[]}").
				put(baseURL);
		Assertions.assertTrue("Was rename request a success : ", response.getStatusCode() == 200);
	}

	public static void moveFile(String accessToken, long folderId, long fileId) throws Exception {
		String baseURL = "https://api.box.com/2.0/files/" + fileId;
		Response response = given().
				header("Accept", "application/json").
				header("Content-Type", "application/json").
				header("Authorization", accessToken).
				body("{\"parent\":{\"id\":\"" + folderId + "\"},\"tags\":[]}").
				put(baseURL);
		Assertions.assertTrue("Was move request a success : ", response.getStatusCode() == 200);
	}

	public static long getFileParentId(String accessToken, long fileId) {
		String baseURL = "https://api.box.com/2.0/files/" + fileId;
		Response response = given().
				header("Accept", "application/json").
				header("Content-Type", "application/json").
				header("Authorization", accessToken).
				get(baseURL);
		JsonObject getJsonObjectFromJsonElement = JsonHandlingUtility.getJsonObjectFromJsonElement("path_collection", response.getBody().asString());
		JsonArray getJsonArrayFromJsonElement = JsonHandlingUtility.getJsonArrayFromJsonElement("entries", getJsonObjectFromJsonElement);
		long getParentIdFromJsonArray = JsonHandlingUtility.getJsonElementFromJsonArray("id", getJsonArrayFromJsonElement, 1);
		return getParentIdFromJsonArray;
	}

	public static long copyFile(String accessToken, long fileId, long folderId) throws Exception {
		String baseURL = "https://api.box.com/2.0/files/" + fileId + "/copy";
		Response response = given().
				header("Authorization", accessToken).
				header("Accept", "application/json").
				header("Content-Type", "application/json").
				body("{\"parent\":{\"id\":\""+ folderId +"\"}}").
				post(baseURL);
		Assertions.assertTrue("Was copy file request success : ", response.statusCode() == 201);
		long generatedFolderId = Long.parseLong(JsonHandlingUtility.getJsonElementFromJsonLevelOne("id" ,response.getBody().asString()));
		return generatedFolderId;
	}
	
	public static long copyFileWithChangedName(String accessToken, long fileId, long copyfolderId, String updatedName) throws Exception {
		String baseURL = "https://api.box.com/2.0/files/" + fileId + "/copy";
		Response response = given().
				header("Authorization", accessToken).
				body("{\"parent\":{\"id\":\"" + copyfolderId + "\"},\"name\":\"" + updatedName + "\"}").
				post(baseURL);
		Assertions.assertTrue("Was copy folder request success : ", response.statusCode() == 201);
		long generatedFolderId = Long.parseLong(JsonHandlingUtility.getJsonElementFromJsonLevelOne("id" ,response.getBody().asString()));
		return generatedFolderId;
	}

	public static String getFileDescription(String accessToken, long fileId) throws Exception {
		String baseURL = "https://api.box.com/2.0/files/" + fileId;
		Response response = given().
				header("Authorization", accessToken).
				get(baseURL);
		Assertions.assertTrue("Is file present : ", response.getStatusCode() == 200);
		String fileDescription = JsonHandlingUtility.getJsonElementFromJsonLevelOne("description" ,response.getBody().asString());
		return fileDescription;
	}

	public static void updateFileDescriptionTo(String accessToken, long fileId, String updatedFileDescription) throws Exception {
		String baseURL = "https://api.box.com/2.0/files/" + fileId;
		Response response = given().
				header("Authorization", accessToken).
				body("{\"description\":\"" + updatedFileDescription + "\",\"tags\":[]}").
				put(baseURL);
		Assertions.assertTrue("Was request success : ", response.getStatusCode() == 200 );
	}

	public static String downloadFile(String accessToken, long fileId) throws Exception {
		String baseURL = "https://api.box.com/2.0/files/" + fileId + "/content";
		Response response = given().
				header("Authorization", accessToken).
				header("Accept", "application/json").
				header("Content-Type", "application/json").
				get(baseURL);
		Assertions.assertTrue("Was download request a success : ", response.getStatusCode() == 200);
		return (response.getBody().asString());
	}

	public static void createMetaDataForFolder(String accessToken, long folderId, String metaDataKey,
			String metaDataValue) throws Exception {
		String baseURL = "https://api.box.com/2.0/folders/" + folderId + "/metadata/global/properties";
		Response response = given().
				header("Authorization", accessToken).
				header("Accept", "application/json").
				header("Content-Type", "application/json").
				body("{\"" + metaDataKey + "\":\"" + metaDataValue + "\"}").
				post(baseURL);
		Assertions.assertTrue("Was create metadata request successfull for folder : ", response.getStatusCode() == 201);
	}

	public static boolean areKeyValuePairPresentInFolderMetaData(String accessToken, long folderId, String metaDataKey,
			String metaDataValue) throws Exception {
		String baseURL = "https://api.box.com/2.0/folders/" + folderId + "/metadata/global/properties";
		Response response = given().
				header("Authorization", accessToken).
				header("Accept", "application/json").
				header("Content-Type", "application/json").
				get(baseURL);
		String responseBody = response.getBody().asString();
		if(JsonHandlingUtility.isPropertyPresent(responseBody, metaDataKey))
			return (JsonHandlingUtility.getJsonElementFromJsonLevelOne(metaDataKey, responseBody).equals(metaDataValue));
		return false;
	}

	public static void deleteMetaDataForFolder(String accessToken, long folderId) throws Exception {
		String baseURL = "https://api.box.com/2.0/folders/" + folderId + "/metadata/global/properties";
		Response response = given().
				header("Authorization", accessToken).
				header("Accept", "application/json").
				header("Content-Type", "application/json").
				delete(baseURL);
		Assertions.assertTrue("Was create metadata request successfull for folder : ", response.getStatusCode() == 204);
	}

	public static void updateMetaDataForFolderByAddField(String accessToken, long folderId, String metaDataKey,
			String metaDataValue) throws Exception {
		String baseURL = "https://api.box.com/2.0/folders/" + folderId + "/metadata/global/properties";
		Response response = given().
				header("Authorization", accessToken).
				header("Accept", "application/json").
				header("Content-Type", "application/json-patch+json").
				body("[{\"op\": \"add\", \"path\":\"/" + metaDataKey + "\", \"value\": \"" + metaDataValue + "\" }]").
				put(baseURL);
		Assertions.assertTrue("Was update folder metadata add property request a success : ", response.getStatusCode() == 200);
	}

	public static void updateMetaDataForFolderByReplaceField(String accessToken, long folderId, String metaDataKey1,
			String metaDataValue2) throws Exception {
		String baseURL = "https://api.box.com/2.0/folders/" + folderId + "/metadata/global/properties";
		Response response = given().
				header("Authorization", accessToken).
				header("Accept", "application/json").
				header("Content-Type", "application/json-patch+json").
				body("[{\"op\": \"replace\", \"path\":\"/" + metaDataKey1 + "\", \"value\": \"" + metaDataValue2 + "\" }]").
				put(baseURL);
		Assertions.assertTrue("Was update folder metadata replace property request a success : ", response.getStatusCode() == 200);
	}

	public static void updateMetaDataForFolderByRemoveField(String accessToken, long folderId, String metaDataKey) throws Exception {
		String baseURL = "https://api.box.com/2.0/folders/" + folderId + "/metadata/global/properties";
		Response response = given().
				header("Authorization", accessToken).
				header("Accept", "application/json").
				header("Content-Type", "application/json-patch+json").
				body("[{\"op\": \"remove\", \"path\":\"/" + metaDataKey + "\"}]").
				put(baseURL);
		Assertions.assertTrue("Was update folder metadata remove property request a success : ", response.getStatusCode() == 200);
	}

	public static void updateMetaDataForFolderByTestField(String accessToken, long folderId, String metaDataKey,
			String metaDataValue) throws Exception {
		String baseURL = "https://api.box.com/2.0/folders/" + folderId + "/metadata/global/properties";
		Response response = given().
				header("Authorization", accessToken).
				header("Accept", "application/json").
				header("Content-Type", "application/json-patch+json").
				body("[{\"op\": \"test\", \"path\":\"/" + metaDataKey + "\", \"value\": \"" + metaDataValue + "\" }]").
				put(baseURL);
		Assertions.assertTrue("Was update folder metadata test property request a success : ", response.getStatusCode() == 200);
		
	}

}
