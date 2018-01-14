package com.utility;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonHandlingUtility {

	public static String getJsonElementFromJsonLevelOne(String jsonElementIdentity, String jsonString) {
		JsonObject jobj = new Gson().fromJson(jsonString, JsonObject.class);
		return (jobj.get(jsonElementIdentity).getAsString());
	}

	public static long getFolderIdFromJson(String jsonString) {
		JsonObject jobj = new Gson().fromJson(jsonString, JsonObject.class);
		JsonArray properties = jobj.getAsJsonArray("entries");
		JsonObject jobj1 = properties.get(0).getAsJsonObject();
		long folderId = jobj1.get("id").getAsLong();
		return folderId;
	}

	public static JsonArray getJsonArrayFromJsonElement(String jsonElement, JsonObject jsonObject) {
		JsonArray properties = jsonObject.getAsJsonArray(jsonElement);
		return properties;
	}

	public static long getJsonElementFromJsonArray(String elementId, JsonArray jsonArray, int arrayItem) {
		JsonObject jobj = jsonArray.get(arrayItem).getAsJsonObject();
		long folderId = jobj.get(elementId).getAsLong();
		return folderId;
	}
	
	public static JsonObject getJsonObjectFromJsonElement(String jsonElementIdentity, String jsonString) {
		JsonObject jobj = new Gson().fromJson(jsonString, JsonObject.class);
		return (jobj.get(jsonElementIdentity).getAsJsonObject());
	}

	public static boolean isPropertyPresent(String responseBody, String metaDataKey) {
		JsonObject jobj = new Gson().fromJson(responseBody, JsonObject.class);
		if(jobj.has(metaDataKey))
			return true;
		return false;
	}
}
