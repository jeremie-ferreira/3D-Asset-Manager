package com.jeremieferreira.feature.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CategoryController {
	
	@Autowired
	private CategoryService service;
	
	@GetMapping("/categories")
	@ResponseBody
	public List<Category> getAll() {
		return service.findAll();
	}
	
	@GetMapping("/category/{id}")
	@ResponseBody
	public Category getById(@PathVariable Long id) {
		return service.findById(id);
	}
}
