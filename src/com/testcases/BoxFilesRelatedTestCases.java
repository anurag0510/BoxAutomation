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
public class BoxFilesRelatedTestCases {
	
	static String accessToken = "";
	private static Logger log = RunConfig.logger();

	@BeforeTest
	public void setUpEnvirnoment() throws Exception {
		log.info("Generate access token.");
		accessToken = "Bearer " + RequestGenerationUtility.getAccessToken();
		log.info("Generated access token is : " + accessToken);
	}
	
	@Skip
	@TestMethod
	public void uploadFile() throws Exception {
		String uploadFileName = BasicUtility.generateRandomString() + ".txt";
		String fileLocationOnMachine = ".\\testmile-arjuna-1.0.0-b\\testmile-arjuna\\temp\\dummyFile.txt";
		log.info("Uploading file from location : " + fileLocationOnMachine + " with name : " + uploadFileName);
		long fileId;
		if(RequestGenerationUtility.isFileExists(accessToken, uploadFileName)) {
			fileId = RequestGenerationUtility.getFileId(accessToken, uploadFileName);
			RequestGenerationUtility.deleteFile(accessToken, fileId);
		}
		fileId = RequestGenerationUtility.uploadFile(accessToken, uploadFileName, fileLocationOnMachine);
		Assertions.assertTrue("Is file uploaded successfully : ", RequestGenerationUtility.getFileName(accessToken, fileId).equals(uploadFileName));
	}
	
	@Skip
	@TestMethod
	public void deleteFile() throws Exception {
		String fileName = BasicUtility.generateRandomString() + ".txt";
		String fileLocationOnMachine = ".\\testmile-arjuna-1.0.0-b\\testmile-arjuna\\temp\\dummyFile.txt";
		long fileId;
		if(RequestGenerationUtility.isFileExists(accessToken, fileName)) {
			fileId = RequestGenerationUtility.getFileId(accessToken, fileName);
			RequestGenerationUtility.deleteFile(accessToken, fileId);
		}
		else {
			fileId = RequestGenerationUtility.uploadFile(accessToken, fileName, fileLocationOnMachine);
			RequestGenerationUtility.deleteFile(accessToken, fileId);
		}
		Assertions.assertTrue("Is file deleted successfully : ", !RequestGenerationUtility.isFilePresent(accessToken, fileId));
	}
	
	@Skip
	@TestMethod
	public void renameFile() throws Exception {
		String fileName = BasicUtility.generateRandomString() + ".txt";
		String renamedFileName = BasicUtility.generateRandomString() + ".txt";
		String fileLocationOnMachine = ".\\testmile-arjuna-1.0.0-b\\testmile-arjuna\\temp\\dummyFile.txt";
		long fileId;
		if(RequestGenerationUtility.isFileExists(accessToken, renamedFileName)) {
			fileId = RequestGenerationUtility.getFileId(accessToken, renamedFileName);
			RequestGenerationUtility.deleteFile(accessToken, fileId);
		}
		if(RequestGenerationUtility.isFileExists(accessToken, fileName)) {
			fileId = RequestGenerationUtility.getFileId(accessToken, fileName);
			RequestGenerationUtility.renameFile(accessToken, fileId, renamedFileName);
		}
		else {
			fileId = RequestGenerationUtility.uploadFile(accessToken, fileName, fileLocationOnMachine);
			RequestGenerationUtility.renameFile(accessToken, fileId, renamedFileName);
		}
		Assertions.assertTrue("Is file renamed successfully : ", RequestGenerationUtility.getFileName(accessToken, fileId).equals(renamedFileName));
	}
	
	@Skip
	@TestMethod
	public void moveFile() throws Exception {
		String fileName = BasicUtility.generateRandomString() + ".txt";
		String moveFolderName = BasicUtility.generateRandomString();
		String fileLocationOnMachine = ".\\testmile-arjuna-1.0.0-b\\testmile-arjuna\\temp\\dummyFile.txt";
		long fileId, folderId;
		if(RequestGenerationUtility.isFileExists(accessToken, fileName)) {
			fileId = RequestGenerationUtility.getFileId(accessToken, fileName);
			RequestGenerationUtility.deleteFile(accessToken, fileId);
		}
		if(RequestGenerationUtility.isFolderPresent(accessToken, moveFolderName)) {
			folderId = RequestGenerationUtility.getFolderId(accessToken, moveFolderName);
			RequestGenerationUtility.deleteFolder(accessToken, folderId);
		}
		fileId = RequestGenerationUtility.uploadFile(accessToken, fileName, fileLocationOnMachine);
		folderId = RequestGenerationUtility.createFolder(accessToken, moveFolderName);
		RequestGenerationUtility.moveFile(accessToken, folderId, fileId);
		Assertions.assertTrue("Is file moved successfully : ", RequestGenerationUtility.getFileParentId(accessToken, fileId) == folderId);
	}
	
	@Skip
	@TestMethod
	public void copyFile() throws Exception {
		String fileName = BasicUtility.generateRandomString() + ".txt";
		String copyFolderName = BasicUtility.generateRandomString();
		String fileLocationOnMachine = ".\\testmile-arjuna-1.0.0-b\\testmile-arjuna\\temp\\dummyFile.txt";
		long fileId, copiedFileId, folderId;
		if(RequestGenerationUtility.isFileExists(accessToken, fileName)) {
			fileId = RequestGenerationUtility.getFileId(accessToken, fileName);
			RequestGenerationUtility.deleteFile(accessToken, fileId);
		}
		if(RequestGenerationUtility.isFolderPresent(accessToken, copyFolderName)) {
			folderId = RequestGenerationUtility.getFolderId(accessToken, copyFolderName);
			RequestGenerationUtility.deleteFolder(accessToken, folderId);
		}
		fileId = RequestGenerationUtility.uploadFile(accessToken, fileName, fileLocationOnMachine);
		folderId = RequestGenerationUtility.createFolder(accessToken, copyFolderName);
		copiedFileId = RequestGenerationUtility.copyFile(accessToken, fileId, folderId);
		Assertions.assertTrue("Is file copied successfully : ", RequestGenerationUtility.getFileParentId(accessToken, copiedFileId) == folderId);
	}
	
	@Skip
	@TestMethod
	public void copyFileWithUpdatedName() throws Exception {
		String fileName = BasicUtility.generateRandomString() + ".txt";
		String updatedFileName = BasicUtility.generateRandomString() + ".txt";
		String copyFolderName = BasicUtility.generateRandomString();
		String fileLocationOnMachine = ".\\testmile-arjuna-1.0.0-b\\testmile-arjuna\\temp\\dummyFile.txt";
		long fileId, copiedFileId, folderId;
		if(RequestGenerationUtility.isFileExists(accessToken, fileName)) {
			fileId = RequestGenerationUtility.getFileId(accessToken, fileName);
			RequestGenerationUtility.deleteFile(accessToken, fileId);
		}
		if(RequestGenerationUtility.isFolderPresent(accessToken, copyFolderName)) {
			folderId = RequestGenerationUtility.getFolderId(accessToken, copyFolderName);
			RequestGenerationUtility.deleteFolder(accessToken, folderId);
		}
		fileId = RequestGenerationUtility.uploadFile(accessToken, fileName, fileLocationOnMachine);
		folderId = RequestGenerationUtility.createFolder(accessToken, copyFolderName);
		copiedFileId = RequestGenerationUtility.copyFileWithChangedName(accessToken, fileId, folderId, updatedFileName);
		Assertions.assertTrue("Is file copied successfully : ", RequestGenerationUtility.getFileParentId(accessToken, copiedFileId) == folderId && RequestGenerationUtility.getFileName(accessToken, copiedFileId).equals(updatedFileName));
	}
	
	@Skip
	@TestMethod
	public void updateFileDescription() throws Exception {
		String fileName = BasicUtility.generateRandomString() + ".txt";
		String fileLocationOnMachine = ".\\testmile-arjuna-1.0.0-b\\testmile-arjuna\\temp\\dummyFile.txt";
		String updatedFileDescription = BasicUtility.generateRandomString();
		long fileId;
		if(RequestGenerationUtility.isFileExists(accessToken, fileName)) {
			fileId = RequestGenerationUtility.getFileId(accessToken, fileName);
			RequestGenerationUtility.deleteFile(accessToken, fileId);
		}
		fileId = RequestGenerationUtility.uploadFile(accessToken, fileName, fileLocationOnMachine);
		String fileCurrentDescription = RequestGenerationUtility.getFileDescription(accessToken, fileId);
		RequestGenerationUtility.updateFileDescriptionTo(accessToken, fileId, updatedFileDescription);
		Assertions.assertTrue("Is file description updated : ", !(RequestGenerationUtility.getFileDescription(accessToken, fileId).equals(fileCurrentDescription)) && (RequestGenerationUtility.getFileDescription(accessToken, fileId).equals(updatedFileDescription)));
	}
	
	@Skip
	@TestMethod
	public void downloadFile() throws Exception {
		String fileName = BasicUtility.generateRandomString() + ".txt";
		String fileLocationOnMachine = ".\\testmile-arjuna-1.0.0-b\\testmile-arjuna\\temp\\dummyFile.txt";
		long fileId;
		if(RequestGenerationUtility.isFileExists(accessToken, fileName)) {
			fileId = RequestGenerationUtility.getFileId(accessToken, fileName);
			RequestGenerationUtility.deleteFile(accessToken, fileId);
		}
		fileId = RequestGenerationUtility.uploadFile(accessToken, fileName, fileLocationOnMachine);
		String downloadedFile = RequestGenerationUtility.downloadFile(accessToken, fileId);
		BasicUtility.copyDataToFile(downloadedFile, fileName);
		String downloadedFileLocation = ".\\testmile-arjuna-1.0.0-b\\testmile-arjuna\\temp\\" + fileName;
		Assertions.assertTrue("Is downloaded file correct : ", BasicUtility.areFilesSame(fileLocationOnMachine, downloadedFileLocation));
	}
	
}
