package com.jeremieferreira.feature.asset;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jeremieferreira.feature.common.Constant.ObjectState;

public interface AssetRepository extends JpaRepository<Asset, Long> {
	
    @Query("SELECT DISTINCT a FROM Asset a " +
 	       "LEFT JOIN a.tags t " +
 	       "WHERE a.state = :state " +
 	       "AND (LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
 	       "OR LOWER(a.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
 	       "OR LOWER(t.label) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
 	       "AND (:categoryId IS NULL OR a.category.id = :categoryId) ")
 Page<Asset> searchActiveAssetsByNameDescriptionOrTags(@Param("searchTerm") String searchTerm,
          											  	@Param("categoryId") Long categoryId,
          											  	@Param("state") ObjectState state,
          											  	Pageable pageable);
    
    @Query("SELECT DISTINCT a FROM Asset a " +
 	       "LEFT JOIN a.tags t " +
 	       "WHERE a.state = :state " +
 	       "AND (LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
 	       "OR LOWER(a.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
 	       "OR LOWER(t.label) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
 	       "AND (:categoryId IS NULL OR a.category.id = :categoryId) " +
 	       "AND (t.id IN :tagIds)")
 Page<Asset> searchActiveAssetsByNameDescriptionAndTags(@Param("searchTerm") String searchTerm,
			  											@Param("categoryId") Long categoryId,
			  											@Param("tagIds") List<Long> tagIds,
			  											@Param("state") ObjectState state,
			  											Pageable pageable);
    
    Page<Asset> findDistinctByTags_LabelAndState(String label, ObjectState state, Pageable pageable);
}
