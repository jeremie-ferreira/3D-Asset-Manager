package com.jeremieferreira.feature.asset;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.jeremieferreira.feature.util.ApplicationError;

@Controller
public class AssetController {

	@Autowired
	private AssetService service;
	
	@GetMapping(value="assets", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Asset> findAll() {
		return service.findAll();
	}

	@GetMapping(value="assets/search", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Page<Asset> searchActiveAssetsByNameDescriptionOrTags(@RequestParam String search, @RequestParam(required = false) List<Long> tagIds, @RequestParam(required = false) Long categoryId, Pageable pageable) {
		return service.searchActiveAssetsByNameDescriptionOrTags(search, tagIds, categoryId, pageable);
	}
	
	@GetMapping(value="assets/search-by-tag", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Page<Asset> findDistinctByTagLabel(@RequestParam String tag, Pageable pageable) {
		return service.findDistinctByTagLabel(tag, pageable);
	}
	
	@GetMapping(value="asset/{id}", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Asset findById(@PathVariable Long id) {
		return service.findById(id);
	}
		
	@PostMapping(value="asset", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Asset update(@RequestBody Asset asset) throws ApplicationError {
		return service.save(asset);
	}
	
	
	@PostMapping(value="asset/file", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Asset saveFile(@RequestParam MultipartFile file) throws ApplicationError {

		Asset asset = service.saveFile(null, file);
		
		return asset;
	}
	
	
	@PutMapping(value="asset/{id}/file", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Asset addAssetFile(@PathVariable Long id, @RequestParam MultipartFile file) throws ApplicationError {

		Asset asset = service.saveFile(id, file);
		
		return asset;
	}
	
	@PutMapping(value="asset/{id}/thumb", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Asset setThumb(@PathVariable Long id, @RequestParam MultipartFile file) throws ApplicationError {
		
		Asset asset = service.uploadThumb(id, file);
		
		return asset;
	}
	
}
