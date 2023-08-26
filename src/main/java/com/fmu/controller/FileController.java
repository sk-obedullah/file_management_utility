package com.fmu.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fmu.entity.FileEntity;
import com.fmu.repository.FileRepository;

@RestController
@RequestMapping("/api/fmu")
public class FileController {

	@Autowired
	FileRepository fileRepository;

	private String localDir = "D:\\LocalStorage\\File_management_utility\\storage\\";

	@GetMapping("/test")
	public String testEndpoint() {
		return "test sucess ::fileController";
	}

	@PostMapping("/upload")
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
		try {
			String fileName = StringUtils.cleanPath(file.getOriginalFilename());
			Path filePath = Paths.get(localDir + fileName);
			try {
				Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
				return ResponseEntity.internalServerError().body("Error While Saving the file to Storage");
			}
			FileEntity fileEnt = new FileEntity();
			fileEnt.setFileName(fileName);
			FileEntity save = fileRepository.save(fileEnt);
			if (save == null) {
				return ResponseEntity.internalServerError().body("Error While Uploading the file to Storage");

			}
			return ResponseEntity.ok("File Uploaded Successfully");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Error While Uploading the file to Storage");
		}

	}

	@PostMapping("/download/{id}")
	public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
		Optional<FileEntity> findById = fileRepository.findById(id);
		if (findById.isPresent()) {
			FileEntity fileEntity = findById.get();
			Path filePath = Paths.get(localDir + fileEntity.getFileName());
			Resource resource;
			try {
				resource = new UrlResource(filePath.toUri());
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		}
		return ResponseEntity.notFound().build();
	}
}
