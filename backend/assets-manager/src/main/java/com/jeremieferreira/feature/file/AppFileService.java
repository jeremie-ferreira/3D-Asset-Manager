package com.jeremieferreira.feature.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jeremieferreira.feature.util.ApplicationError;

@Service
public class AppFileService {

	@Autowired
	private AppFileRepository repository;

	@Value("${files.location}")
	private String filesLocation;

	public AppFile save(AppFile appFile) {
		return repository.save(appFile);
	}
	
	public AppFile saveAndFlush(AppFile appFile) {
		return repository.saveAndFlush(appFile);
	}
	
	public AppFile get(Long id) {
		return repository.findById(id).get();
	}
	
	public void delete(AppFile appFile) {
		repository.delete(appFile);
	}
	
	public FileSystemResource downloadFile(String filepath) {
		FileSystemResource file = new FileSystemResource(filesLocation + filepath);
		return file;
	}
	
	public AppFile downloadFile(Long id) {
		AppFile appFile = get(id);
		appFile.setFile(new FileSystemResource(filesLocation + appFile.getGuid()));
		return appFile;
	}
	
	public AppFile uploadFile(MultipartFile file) throws ApplicationError {
		
		try {
			if (file.isEmpty()) {
				throw new ApplicationError("Failed to store empty file.");
			}
			
			// get file extension
			int dotIndex = file.getOriginalFilename().lastIndexOf('.');
			String extension = dotIndex == -1 ? "" : file.getOriginalFilename().substring(dotIndex); 
			
			// set file name and guid
			AppFile appFile = new AppFile();
			appFile.setName(file.getOriginalFilename());
			appFile.setGuid(UUID.randomUUID().toString() + extension);
			

			Path destinationFile = Paths.get(filesLocation).resolve(Paths.get(appFile.getGuid().toString())).normalize().toAbsolutePath();

			if (!destinationFile.getParent().equals(Paths.get(filesLocation).toAbsolutePath())) {
				delete(appFile);
				throw new ApplicationError("Cannot store file outside current directory.");
			}
			
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
				return save(appFile);
			}
		} catch (IOException e) {
			throw new ApplicationError("Failed to store file.", e);
		}
	}
}
