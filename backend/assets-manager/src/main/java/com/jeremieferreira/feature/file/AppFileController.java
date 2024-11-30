package com.jeremieferreira.feature.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.jeremieferreira.feature.util.ApplicationError;

@Controller
public class AppFileController {

	@Autowired
	private AppFileService service;
	
	@PostMapping(value="file", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public AppFile uploadFile(@RequestParam MultipartFile file) throws ApplicationError {
		return service.uploadFile(file);
	}
	
	@GetMapping("file/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
		try {
			AppFile appFile = service.downloadFile(id);

            if (!appFile.getFile().exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + appFile.getName());

            return ResponseEntity.ok().headers(headers).body(appFile.getFile());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
    }
	
	@GetMapping("appfile/{id}")
	@ResponseBody
    public AppFile getAppFile(@PathVariable Long id) {
		return service.get(id);
    }
}
