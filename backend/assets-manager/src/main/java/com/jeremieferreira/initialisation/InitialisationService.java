package com.jeremieferreira.initialisation;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jeremieferreira.feature.category.Category;
import com.jeremieferreira.feature.category.CategoryRepository;
import com.jeremieferreira.feature.user.AppUser;
import com.jeremieferreira.feature.user.AppUser.Role;
import com.jeremieferreira.feature.user.UserRepository;

@Service
public class InitialisationService {

	public static final Logger logger = LoggerFactory.getLogger(InitialisationService.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	public void init() {
		this.createAdminUser();
		this.createCategories();
	}
	
	public boolean createAdminUser() {
		Optional<AppUser> userFound = userRepository.findOneByEmail("admin@assetsmanager.com");
				
		if (userFound == null || !userFound.isPresent()) {
			logger.info("----- Initialise admin users ----- ");
			AppUser user = new AppUser();
			user.setPassword(new BCryptPasswordEncoder().encode("MYQ@oIrRjG6O5i944ErI%csN"));
			user.setEmail("admin@assetsmanager.com");
			user.setLastname("Admin");
			user.setFirstname("Assets Manager");
			user.setRole(Role.ROLE_SUPERADMIN);
			userRepository.save(user);
		}
		return true;
	}
	
	public boolean createCategories() {
		if (categoryRepository.findAll().size() == 0) {
			logger.info("----- Initialise categories ----- ");
			categoryRepository.save(new Category("Animals & Pets"));
			categoryRepository.save(new Category("Architecture"));
			categoryRepository.save(new Category("Art & Abstract"));
			categoryRepository.save(new Category("Cars & Vehicles"));
			categoryRepository.save(new Category("Characters & Creatures"));
			categoryRepository.save(new Category("Cultural Heritage & History"));
			categoryRepository.save(new Category("Electronics & Gadgets"));
			categoryRepository.save(new Category("Fashion & Style"));
			categoryRepository.save(new Category("Food & Drink"));
			categoryRepository.save(new Category("Furniture & Home"));
			categoryRepository.save(new Category("Music"));
			categoryRepository.save(new Category("Nature & Plants"));
			categoryRepository.save(new Category("News & Politics"));
			categoryRepository.save(new Category("People"));
			categoryRepository.save(new Category("Places & Travel"));
			categoryRepository.save(new Category("Science & Technology"));
			categoryRepository.save(new Category("Sports & Fitness"));
			categoryRepository.save(new Category("Weapons & Military"));
		}
		return true;
	}

}
