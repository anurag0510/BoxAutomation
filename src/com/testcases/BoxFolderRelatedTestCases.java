package com.testcases;

import org.apache.log4j.Logger;

import com.utility.BasicUtility;
import com.utility.RequestGenerationUtility;

import arjunasdk.config.RunConfig;
import unitee.annotations.BeforeTest;
import unitee.annotations.Skip;
import unitee.annotations.TestClass;
import unitee.annotations.TestMethod;
import unitee.assertions.Assertions;

@TestClass
public class BoxFolderRelatedTestCases {
	
	static String accessToken = "";
	private static Logger log = RunConfig.logger();

	@BeforeTest
	public void setUpEnvirnoment() throws Exception {
		log.info("Generate access token.");
		accessToken = "Bearer " + RequestGenerationUtility.getAccessToken();
		log.info("Generated access token is : " + accessToken);
	}
	
	@TestMethod
	public void testCreateFolder() throws Exception {
		log.info("!!!!!!!!!!!!!!!Starting folder creation test!!!!!!!!!!!!!!!!!");
		String folderName = BasicUtility.generateRandomString();
		log.info("Folder with name to be created :" + folderName);
		if(RequestGenerationUtility.isFolderPresent(accessToken, folderName)) {
			long folderId = RequestGenerationUtility.getFolderId(accessToken, folderName);
			RequestGenerationUtility.deleteFolder(accessToken, folderId);
		}
		long folderId = RequestGenerationUtility.createFolder(accessToken, folderName);
		Assertions.assertTrue("Is folder created successfully : ", RequestGenerationUtility.getFolderName(accessToken, folderId).equals(folderName));
	}
	
	@TestMethod
	public void testDeleteFolder() throws Exception {
		log.info("!!!!!!!!!!!!!!!Starting folder deletion test!!!!!!!!!!!!!!!!!");
		String folderName = BasicUtility.generateRandomString();
		long folderId;
		log.info("Folder with name to be deleted :" + folderName);
		if(RequestGenerationUtility.isFolderPresent(accessToken, folderName)) {
			folderId = RequestGenerationUtility.getFolderId(accessToken, folderName);
			RequestGenerationUtility.deleteFolder(accessToken, folderId);
		}
		else {
			folderId = RequestGenerationUtility.createFolder(accessToken, folderName);
			RequestGenerationUtility.deleteFolder(accessToken, folderId);
		}
		Assertions.assertTrue("Is folder deleted successfully : ", !RequestGenerationUtility.doesFolderExist(accessToken, folderId));
	}
	
	@TestMethod
	public void renameFolder() throws Exception {
		log.info("!!!!!!!!!!!!!!!Starting folder rename test!!!!!!!!!!!!!!!!!");
		String folderName = BasicUtility.generateRandomString();
		String renamedFolderName = BasicUtility.generateRandomString();
		log.info("folder name : " + folderName + "to be renamed to : " + renamedFolderName);
		long folderId;
		if(RequestGenerationUtility.isFolderPresent(accessToken, folderName)) {
			folderId = RequestGenerationUtility.getFolderId(accessToken, folderName);
			RequestGenerationUtility.deleteFolder(accessToken, folderId);
		}
		if(RequestGenerationUtility.isFolderPresent(accessToken, renamedFolderName)) {
			folderId = RequestGenerationUtility.getFolderId(accessToken, renamedFolderName);
			RequestGenerationUtility.deleteFolder(accessToken, folderId);
		}
		folderId = RequestGenerationUtility.createFolder(accessToken, folderName);
		RequestGenerationUtility.renameFolder(accessToken, folderId, renamedFolderName);
		Assertions.assertTrue("Is folder renamed successfully : ", RequestGenerationUtility.getFolderName(accessToken, folderId).equals(renamedFolderName));
	}
	
	@TestMethod
	public void moveFolder() throws Exception {
		log.info("!!!!!!!!!!!!!!!Starting folder move test!!!!!!!!!!!!!!!!!");
		String folderName = BasicUtility.generateRandomString();
		String moveFolderName = BasicUtility.generateRandomString();
		log.info("move folder : " + folderName + "to folder : " + moveFolderName);
		long folderId, moveFolderId;
		if(RequestGenerationUtility.isFolderPresent(accessToken, folderName)) {
			folderId = RequestGenerationUtility.getFolderId(accessToken, folderName);
			RequestGenerationUtility.deleteFolder(accessToken, folderId);
		}
		if(RequestGenerationUtility.isFolderPresent(accessToken, moveFolderName)) {
			moveFolderId = RequestGenerationUtility.getFolderId(accessToken, moveFolderName);
			RequestGenerationUtility.deleteFolder(accessToken, moveFolderId);
		}
		folderId = RequestGenerationUtility.createFolder(accessToken, folderName);
		moveFolderId = RequestGenerationUtility.createFolder(accessToken, moveFolderName);
		RequestGenerationUtility.moveFolder(accessToken, folderId, moveFolderId);
		Assertions.assertTrue("Is folder moved successfully : ", RequestGenerationUtility.getFolderParentId(accessToken, folderId) == moveFolderId);
	}
	
	@TestMethod
	public void updateFolderDescription() throws Exception {
		log.info("!!!!!!!!!!!!!!!Starting change folder description test!!!!!!!!!!!!!!!!!");
		String folderName = BasicUtility.generateRandomString();
		String updatedFolderDescription = BasicUtility.generateRandomString();
		log.info("changing folder : " + folderName + "description to : " + updatedFolderDescription);
		long folderId;
		if(RequestGenerationUtility.isFolderPresent(accessToken, folderName)) {
			folderId = RequestGenerationUtility.getFolderId(accessToken, folderName);
			RequestGenerationUtility.deleteFolder(accessToken, folderId);
		}
		folderId = RequestGenerationUtility.createFolder(accessToken, folderName);
		String folderCurrentDescription = RequestGenerationUtility.getFolderDescription(accessToken, folderId);
		RequestGenerationUtility.updateFolderDescriptionTo(accessToken, folderId, updatedFolderDescription);
		Assertions.assertTrue("Is folder description updated : ", !(RequestGenerationUtility.getFolderDescription(accessToken, folderId).equals(folderCurrentDescription)) && (RequestGenerationUtility.getFolderDescription(accessToken, folderId).equals(updatedFolderDescription)));
	}
	
	@TestMethod
	public void testCopyFolder() throws Exception {
		log.info("!!!!!!!!!!!!!!!Starting copy folder test!!!!!!!!!!!!!!!!!");
		String folderName = BasicUtility.generateRandomString();
		String copyLocationFolderName = BasicUtility.generateRandomString();
		log.info("copy folder : " + folderName + " to folder : " + copyLocationFolderName);
		long folderId, copyfolderId, copiedFolderId;
		if(RequestGenerationUtility.isFolderPresent(accessToken, folderName)) {
			folderId = RequestGenerationUtility.getFolderId(accessToken, folderName);
			RequestGenerationUtility.deleteFolder(accessToken, folderId);
		}
		if(RequestGenerationUtility.isFolderPresent(accessToken, copyLocationFolderName)) {
			copyfolderId = RequestGenerationUtility.getFolderId(accessToken, copyLocationFolderName);
			RequestGenerationUtility.deleteFolder(accessToken, copyfolderId);
		}
		folderId = RequestGenerationUtility.createFolder(accessToken, folderName);
		copyfolderId = RequestGenerationUtility.createFolder(accessToken, copyLocationFolderName);
		copiedFolderId = RequestGenerationUtility.copyFolder(accessToken, folderId, copyfolderId);
		Assertions.assertTrue("Is folder successfully copied : ", RequestGenerationUtility.getFolderParentId(accessToken, copiedFolderId) == copyfolderId);
	}
	
	@TestMethod
	public void copyFolderWithUpdatedName() throws Exception {
		log.info("!!!!!!!!!!!!!!!Starting copy folder with different name test!!!!!!!!!!!!!!!!!");
		String folderName = BasicUtility.generateRandomString();
		String copyLocationFolderName = BasicUtility.generateRandomString();
		String changedFolderName = BasicUtility.generateRandomString();
		log.info("copy folder : " + folderName + " to folder : " + copyLocationFolderName + " with name : " + changedFolderName);
		long folderId, copyfolderId, copiedFolderId;
		if(RequestGenerationUtility.isFolderPresent(accessToken, folderName)) {
			folderId = RequestGenerationUtility.getFolderId(accessToken, folderName);
			RequestGenerationUtility.deleteFolder(accessToken, folderId);
		}
		if(RequestGenerationUtility.isFolderPresent(accessToken, copyLocationFolderName)) {
			copyfolderId = RequestGenerationUtility.getFolderId(accessToken, copyLocationFolderName);
			RequestGenerationUtility.deleteFolder(accessToken, copyfolderId);
		}
		folderId = RequestGenerationUtility.createFolder(accessToken, folderName);
		copyfolderId = RequestGenerationUtility.createFolder(accessToken, copyLocationFolderName);
		copiedFolderId = RequestGenerationUtility.copyFolderWithChangedName(accessToken, folderId, copyfolderId, changedFolderName);
		Assertions.assertTrue("Is folder successfully copied with changed name : ", RequestGenerationUtility.getFolderParentId(accessToken, copiedFolderId) == copyfolderId && RequestGenerationUtility.getFolderName(accessToken, copiedFolderId).equals(changedFolderName));
	}
	
	@TestMethod
	public void createMetaDataForFolder() throws Exception {
		String folderName = BasicUtility.generateRandomString();
		String metaDataKey = BasicUtility.generateRandomString();
		String metaDataValue = BasicUtility.generateRandomString();
		long folderId;
		if(RequestGenerationUtility.isFolderPresent(accessToken, folderName)) {
			folderId = RequestGenerationUtility.getFolderId(accessToken, folderName);
			RequestGenerationUtility.deleteFolder(accessToken, folderId);
		}
		folderId = RequestGenerationUtility.createFolder(accessToken, folderName);
		RequestGenerationUtility.createMetaDataForFolder(accessToken, folderId, metaDataKey, metaDataValue);
		Assertions.assertTrue("Was metadata created successfully for folder : ", RequestGenerationUtility.areKeyValuePairPresentInFolderMetaData(accessToken, folderId, metaDataKey, metaDataValue));
	}
	
	@TestMethod
	public void deleteMetaDataForFolder() throws Exception {
		String folderName = BasicUtility.generateRandomString();
		String metaDataKey = BasicUtility.generateRandomString();
		String metaDataValue = BasicUtility.generateRandomString();
		long folderId;
		if(RequestGenerationUtility.isFolderPresent(accessToken, folderName)) {
			folderId = RequestGenerationUtility.getFolderId(accessToken, folderName);
			RequestGenerationUtility.deleteFolder(accessToken, folderId);
		}
		folderId = RequestGenerationUtility.createFolder(accessToken, folderName);
		RequestGenerationUtility.createMetaDataForFolder(accessToken, folderId, metaDataKey, metaDataValue);
		Assertions.assertTrue("Was metadata created successfully for folder : ", RequestGenerationUtility.areKeyValuePairPresentInFolderMetaData(accessToken, folderId, metaDataKey, metaDataValue));
		RequestGenerationUtility.deleteMetaDataForFolder(accessToken, folderId);
		Assertions.assertTrue("Was metadata deleted successfully for folder : ", !RequestGenerationUtility.areKeyValuePairPresentInFolderMetaData(accessToken, folderId, metaDataKey, metaDataValue));
	}
	
	@TestMethod
	public void updateMetaDataForFolderByAddField() throws Exception {
		String folderName = BasicUtility.generateRandomString();
		String metaDataKey1 = BasicUtility.generateRandomString();
		String metaDataKey2 = BasicUtility.generateRandomString();
		String metaDataValue1 = BasicUtility.generateRandomString();
		String metaDataValue2 = BasicUtility.generateRandomString();
		long folderId;
		if(RequestGenerationUtility.isFolderPresent(accessToken, folderName)) {
			folderId = RequestGenerationUtility.getFolderId(accessToken, folderName);
			RequestGenerationUtility.deleteFolder(accessToken, folderId);
		}
		folderId = RequestGenerationUtility.createFolder(accessToken, folderName);
		RequestGenerationUtility.createMetaDataForFolder(accessToken, folderId, metaDataKey1, metaDataValue1);
		RequestGenerationUtility.updateMetaDataForFolderByAddField(accessToken, folderId, metaDataKey2, metaDataValue2);
		Assertions.assertTrue("Was metadata created successfully for folder : ", RequestGenerationUtility.areKeyValuePairPresentInFolderMetaData(accessToken, folderId, metaDataKey2, metaDataValue2));
	}
	
	@TestMethod
	public void updateMetaDataForFolderByReplaceField() throws Exception {
		String folderName = BasicUtility.generateRandomString();
		String metaDataKey1 = BasicUtility.generateRandomString();
		String metaDataValue1 = BasicUtility.generateRandomString();
		String metaDataValue2 = BasicUtility.generateRandomString();
		long folderId;
		if(RequestGenerationUtility.isFolderPresent(accessToken, folderName)) {
			folderId = RequestGenerationUtility.getFolderId(accessToken, folderName);
			RequestGenerationUtility.deleteFolder(accessToken, folderId);
		}
		folderId = RequestGenerationUtility.createFolder(accessToken, folderName);
		RequestGenerationUtility.createMetaDataForFolder(accessToken, folderId, metaDataKey1, metaDataValue1);
		RequestGenerationUtility.updateMetaDataForFolderByReplaceField(accessToken, folderId, metaDataKey1, metaDataValue2);
		Assertions.assertTrue("Was metadata value replaced for key successfully for folder : ", RequestGenerationUtility.areKeyValuePairPresentInFolderMetaData(accessToken, folderId, metaDataKey1, metaDataValue2) && !RequestGenerationUtility.areKeyValuePairPresentInFolderMetaData(accessToken, folderId, metaDataKey1, metaDataValue1));
	}
	
	@TestMethod
	public void updateMetaDataForFolderByRemoveField() throws Exception {
		String folderName = BasicUtility.generateRandomString();
		String metaDataKey = BasicUtility.generateRandomString();
		String metaDataValue = BasicUtility.generateRandomString();
		long folderId;
		if(RequestGenerationUtility.isFolderPresent(accessToken, folderName)) {
			folderId = RequestGenerationUtility.getFolderId(accessToken, folderName);
			RequestGenerationUtility.deleteFolder(accessToken, folderId);
		}
		folderId = RequestGenerationUtility.createFolder(accessToken, folderName);
		RequestGenerationUtility.createMetaDataForFolder(accessToken, folderId, metaDataKey, metaDataValue);
		boolean isMetaDataGenerated = RequestGenerationUtility.areKeyValuePairPresentInFolderMetaData(accessToken, folderId, metaDataKey, metaDataValue);
		RequestGenerationUtility.updateMetaDataForFolderByRemoveField(accessToken, folderId, metaDataKey);
		Assertions.assertTrue("Is specific metadata removed successfully : ", isMetaDataGenerated != RequestGenerationUtility.areKeyValuePairPresentInFolderMetaData(accessToken, folderId, metaDataKey, metaDataValue));
	}
	
	@TestMethod
	public void updateMetaDataForFolderByTestField() throws Exception {
		String folderName = BasicUtility.generateRandomString();
		String metaDataKey = BasicUtility.generateRandomString();
		String metaDataValue = BasicUtility.generateRandomString();
		long folderId;
		if(RequestGenerationUtility.isFolderPresent(accessToken, folderName)) {
			folderId = RequestGenerationUtility.getFolderId(accessToken, folderName);
			RequestGenerationUtility.deleteFolder(accessToken, folderId);
		}
		folderId = RequestGenerationUtility.createFolder(accessToken, folderName);
		RequestGenerationUtility.createMetaDataForFolder(accessToken, folderId, metaDataKey, metaDataValue);
		RequestGenerationUtility.updateMetaDataForFolderByTestField(accessToken, folderId, metaDataKey, metaDataValue);
		Assertions.assertTrue("Is data key and value pair tested successfully : ", RequestGenerationUtility.areKeyValuePairPresentInFolderMetaData(accessToken, folderId, metaDataKey, metaDataValue));
	}
	
}
