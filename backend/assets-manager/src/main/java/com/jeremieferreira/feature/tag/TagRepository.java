package com.jeremieferreira.feature.tag;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<Tag, Long> {

	@Query("SELECT t FROM Tag t WHERE LOWER(t.label) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Tag> findByLabelContainingIgnoreCase(String searchTerm, PageRequest pageRequest);
	
	List<Tag> findByLabelIgnoreCase(String label);
}
