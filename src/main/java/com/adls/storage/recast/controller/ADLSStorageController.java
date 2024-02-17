package com.adls.storage.recast.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;

@RestController
@RequestMapping("/api")
public class ADLSStorageController {

	@PostMapping("/upload")
	public String uploadFile(@RequestParam("file") MultipartFile file) {
		try {
			/*
			 * Account Name, Account Key: https://stackoverflow.com/questions/6985921/where-can-i-find-my-azure-account-name-and-account-key
			 * 
			 */
			String conStr = "AccountName=adlstestforrecast;"
					+ "AccountKey=sSnvggsAA4lj6KF3lirHfFG46BAU7b6qs7DAy9OsiOTraYoUEh2PU9cP9obO6xn/z8LZRmISVgV0+ASt4XQZ5g==;"
					+ "EndpointSuffix=core.windows.net;" + "DefaultEndpointsProtocol=https;";

			BlobContainerClient container = new BlobContainerClientBuilder().connectionString(conStr)
					.containerName("recast").buildClient();

			BlobClient blob = container.getBlobClient(file.getOriginalFilename());

			blob.upload(file.getInputStream(), file.getSize(), true);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "ok";
	}
	
	@GetMapping("/uploadFileFromResource")
	public String uploadFileFromResource() {
		try {
			String conStr = "AccountName=adlstestforrecast;"
					+ "AccountKey=sSnvggsAA4lj6KF3lirHfFG46BAU7b6qs7DAy9OsiOTraYoUEh2PU9cP9obO6xn/z8LZRmISVgV0+ASt4XQZ5g==;"
					+ "EndpointSuffix=core.windows.net;" + "DefaultEndpointsProtocol=https;";

			BlobContainerClient container = new BlobContainerClientBuilder().connectionString(conStr)
					.containerName("recast").buildClient();
			
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("Book1.xlsx").getFile());
			
			MultipartFile multipart = convertToMultipart(file);

			BlobClient blob = container.getBlobClient(multipart.getOriginalFilename());

			blob.upload(multipart.getInputStream(), multipart.getSize(), true);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "ok";
	}
	
	private static MultipartFile convertToMultipart(File file) {
		MultipartFile multipartFile = null;
		
		try {
			FileItem fileItem = new DiskFileItem(
					"mainFile", 
					Files.probeContentType(file.toPath()), 
					false, 
					file.getName(), 
					(int) file.length(), 
					file.getParentFile());

			try {
			    InputStream input = new FileInputStream(file);
			    OutputStream os = fileItem.getOutputStream();
			    IOUtils.copy(input, os);
			    // Or faster..
			    // IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
			} catch (IOException ex) {
			    // do something.
			}

			multipartFile = new CommonsMultipartFile(fileItem);
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return multipartFile;
	}

}
