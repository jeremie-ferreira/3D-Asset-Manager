package com.jeremieferreira.feature.user;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.jeremieferreira.feature.file.AppFile;
import com.jeremieferreira.feature.user.AppUser.Role;
import com.jeremieferreira.feature.util.ApplicationError;
import com.jeremieferreira.feature.util.ApplicationError.Code;

@Controller
public class UserController  {
	
	@Autowired
	private UserService service;
	
	@GetMapping(value="users", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<AppUser> getAll() {
		return service.getAll();
	}
	
	@GetMapping(value="roles", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Role> getRoles() {
		return Arrays.asList(Role.values());
	}
	
	@PostMapping(value="user", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public AppUser save(@RequestBody AppUser user) throws ApplicationError {
		if (user.getId() != null && SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
			throw new ApplicationError(Code.UNAUTHORIZED_OPERATION);
		}
		if (user.getId() == null && (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser") || user.getRole() == null)) {
			user.setRole(Role.ROLE_USER);
		}
		return service.create(user);
	}
	
	@PutMapping(value="user/renew-password", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public AppUser updatePassword(@RequestBody AppUser user) throws ApplicationError {
		return service.updatePassword(user.getEmail());
	}
	
	@PutMapping(value="user/validate", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public AppUser validate(@RequestBody AppUser user) throws ApplicationError {
		return service.validate(user);
	}
		
	@PutMapping(value="user", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public AppUser update(@RequestBody AppUser user) throws ApplicationError {
		return service.update(user);
	}
	
	@GetMapping(value="user/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public AppUser findById(@PathVariable Long id) {
		return service.findById(id);
	}
	
	@GetMapping(value="user/current", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public AppUser getConnectedUser() {
		return service.getConnectedUser();
	}
	
	@PostMapping(value="user/picture", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public void uploadUserPicture(@RequestParam MultipartFile file) {
		try {
			service.uploadPicture(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@GetMapping(value="user/picture")
	public ResponseEntity<Resource> getConnectedUserPicture() {
		AppFile appFile = service.getConnectedUserPicture();

		HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + appFile.getName());
        return ResponseEntity.ok().headers(headers).body(appFile.getFile());
	}
	
	@DeleteMapping(value="user/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public void delete(@PathVariable("id") Long id) {
		service.delete(id);
	}
	
}
