package com.jeremieferreira.feature.asset;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jeremieferreira.feature.common.Constant.ObjectState;
import com.jeremieferreira.feature.file.AppFile;
import com.jeremieferreira.feature.file.AppFileService;
import com.jeremieferreira.feature.tag.TagService;
import com.jeremieferreira.feature.user.UserService;
import com.jeremieferreira.feature.util.ApplicationError;

@Service
public class AssetService {

	@Autowired
	private AssetRepository repository;
	
	@Autowired
	private UserService appUserService;
	
	@Autowired
	AppFileService appFileService;
	
	@Autowired
	TagService tagService;
    
	public Asset save(Asset asset) {
		asset.setAuthor(appUserService.getConnectedUser());
		asset = tagService.checkTags(asset);
		return repository.save(asset);
	}
	
	public List<Asset> findAll() {
		return repository.findAll();
	}
	
	public Asset findById(Long id) {
		return repository.findById(id).get();
	}

	public Asset saveFile(Long id, MultipartFile file) throws ApplicationError {
		Asset asset = new Asset();
		
		if (id != null) {
			asset = findById(id);
		}

		AppFile appFile = appFileService.uploadFile(file);
		asset.getAdditionalFiles().add(appFile);
		
		return save(asset);
	}
		
	public Asset uploadThumb(Long id, MultipartFile file) throws ApplicationError {
		Asset asset = findById(id);
		AppFile appFile = appFileService.uploadFile(file);
		asset.setThumb(appFile);
		return save(asset);
	}

	public Page<Asset> searchActiveAssetsByNameDescriptionOrTags(String searchTerm, List<Long> tagIds, Long categoryId, Pageable pageable) {
		if (tagIds == null) {
			return repository.searchActiveAssetsByNameDescriptionOrTags(searchTerm, categoryId, ObjectState.ACTIVE, pageable);
		}
		return repository.searchActiveAssetsByNameDescriptionAndTags(searchTerm, categoryId, tagIds, ObjectState.ACTIVE, pageable);
	}
	
	public Page<Asset> findDistinctByTagLabel(String tag, Pageable pageable) {
		return repository.findDistinctByTags_LabelAndState(tag, ObjectState.ACTIVE, pageable);
	}
}
