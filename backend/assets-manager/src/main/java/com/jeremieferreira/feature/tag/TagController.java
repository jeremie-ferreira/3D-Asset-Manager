package com.jeremieferreira.feature.tag;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TagController {
	@Autowired
	private TagService service;
	
	@GetMapping("/tags/autocomplete")
	@ResponseBody
	public List<Tag> autocompleteTags(@RequestParam String searchTerm) {
		//return service.autocompleteTags(searchTerm);
		List<Tag> tags = service.autocompleteTags(searchTerm);
		
		System.out.println(tags);
		
		return tags;
	}
	
	@GetMapping("/tags")
	@ResponseBody
	public List<Tag> findAllById(@RequestParam List<Long> tagIds) {
		return service.findAllById(tagIds);
	}
}
