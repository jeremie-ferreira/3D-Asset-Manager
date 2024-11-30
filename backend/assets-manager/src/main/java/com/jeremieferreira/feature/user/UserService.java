package com.jeremieferreira.feature.user;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jeremieferreira.feature.common.Constant.ObjectState;
import com.jeremieferreira.feature.file.AppFile;
import com.jeremieferreira.feature.file.AppFileService;
import com.jeremieferreira.feature.user.AppUser.Role;
import com.jeremieferreira.feature.util.ApplicationError;
import com.jeremieferreira.feature.util.ApplicationError.Code;


@Service
public class UserService implements UserDetailsService {
	
	private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();
	
	@Autowired
	private UserRepository repository;
	@Autowired
	private AppFileService appFileService;

	public AppUser save(AppUser user) {
		return repository.save(user);
	}
	
	public AppUser findById(Long id) {
		return repository.findById(id).get();
	}
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return repository.findOneByEmail(username).get();
	}
	
	public AppUser getConnectedUser() {
		return repository.findOneByEmail((String)SecurityContextHolder.getContext().getAuthentication().getName()).get();
	}
	
	public AppFile getConnectedUserPicture() {
		AppUser user = getConnectedUser();
		return appFileService.downloadFile(user.getProfilePicture().getId());
	}

	public AppUser create(AppUser user) throws ApplicationError {
		if (user.getId() == null && getByEmail(user.getEmail()) != null) {
			throw new ApplicationError(Code.USER_ALREADY_EXISTS, user);
		}
		
		user.setPassword(ENCODER.encode(RandomStringUtils.random(16, true, true)));
		user = repository.save(user);
		
		user.setObjectState(ObjectState.INACTIVE);
		user.setUrlToken(this.generateToken(user));
		
		repository.save(user);
		
		return user;
	}
	
	private String generateToken(AppUser user) {
		return String.format("%s-%s", user.getId().toString(), UUID.randomUUID().toString());
	}
	
	public AppUser validate(AppUser user) throws ApplicationError {
		AppUser db = findById(Long.valueOf(user.getUrlToken().split("-")[0]));
		if (db.getUrlToken() == null || !user.getUrlToken().equals(db.getUrlToken())) {
			throw new ApplicationError(Code.UNAUTHORIZED_OPERATION);
		}
		
		db.setObjectState(ObjectState.ACTIVE);
		db.setPassword(ENCODER.encode(user.getPassword()));
		db.setUrlToken(null);
		return repository.save(db);
	}
	
	public AppUser updatePassword(String email) throws ApplicationError {
		AppUser user = this.getByEmail(email);
		user.setUrlToken(this.generateToken(user));
		repository.save(user);
		return user;
	}

	public AppUser update(AppUser user) throws ApplicationError {
		AppUser connectedUser = this.getConnectedUser();
		AppUser originalUser = this.getByEmail(user.getEmail());

		if (originalUser == null) {
			throw new ApplicationError(Code.USER_NOT_FOUND);
		}

		if (!hasRoles(connectedUser, Role.ROLE_ADMIN, Role.ROLE_SUPERADMIN)) {
			if(!connectedUser.getId().equals(user.getId())) {
				throw new ApplicationError(Code.UNAUTHORIZED_OPERATION);
			}
			if (!ENCODER.matches(user.getPassword(), originalUser.getPassword())) {
				throw new ApplicationError(Code.PASSWORDS_DO_NOT_MATCH);
			}
		}

		user.setPassword(originalUser.getPassword());
		return save(user);
	}

	public List<AppUser> getByRole(Role role) {
		return repository.getByRole(role);
	}
	
	public AppUser getByEmail(String email) {
		return repository.findOneByEmail(email).get();
	}
	
	public AppUser getByPhoneNumber(String phoneNumber) {
		return repository.getByPhoneNumber(phoneNumber);
	}
	
	public boolean hasRoles(Authentication authentication, String... roles) {
		List<String> roleList = Arrays.asList(roles);
		for (GrantedAuthority authority : authentication.getAuthorities()) {
			if (roleList.contains(authority.getAuthority())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasRoles(AppUser user, Role... roles) {
		List<String> roleList = Arrays.asList(roles).stream().map(p -> p.name()).collect(Collectors.toList());
		for (GrantedAuthority authority : user.getAuthorities()) {
			if (roleList.contains(authority.getAuthority())) {
				return true;
			}
		}
		return false;
	}

	public List<AppUser> getAll() {
		return repository.findAll();
	}

	public void delete(Long id) {
		AppUser user = this.findById(id);
		user.setObjectState(ObjectState.DELETED);
	}

	public void uploadPicture(MultipartFile file) throws ApplicationError {
		AppUser user = getConnectedUser();
		AppFile appFile = appFileService.uploadFile(file);
		
		user.setProfilePicture(appFile);
		repository.save(user);
	}	
}
