package com.jeremieferreira.feature.tag;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.jeremieferreira.feature.asset.Asset;

@Service
public class TagService {
	
	@Autowired
	private TagRepository repository;
	
	public List<Tag> autocompleteTags(String searchTerm) {
		return repository.findByLabelContainingIgnoreCase(searchTerm, PageRequest.of(0, 6));
	}
	
	public Asset checkTags(Asset asset) {
		for (Tag tag: asset.getTags()) {
			if (tag.getId() == null) {
				List<Tag> foundTags = repository.findByLabelIgnoreCase(tag.getLabel());
				if (foundTags.size() > 0) {
					tag.setId(foundTags.get(0).getId());
					tag.setLabel(foundTags.get(0).getLabel());
				}
			}
		}
		
		return asset;
	}
	
	public List<Tag> findAllById(List<Long> ids) {
		return repository.findAllById(ids);
	}
}
