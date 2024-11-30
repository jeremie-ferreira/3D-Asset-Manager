package com.jeremieferreira.feature.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jeremieferreira.feature.user.AppUser.Role;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long>  {
	
	public AppUser getById(Long id);
	
	public Optional<AppUser> findOneByEmail(String email);
	
	public List<AppUser> findAll();

	public AppUser getByPhoneNumber(String phoneNumber);

	//public Utilisateur getByEmail(String email);

	public List<AppUser> getByRole(Role role);
}
