package com.jeremieferreira.feature.file;

import org.springframework.core.io.Resource;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class AppFile {
	
	public static enum AppFileType { JPG, PNG, GLB }
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private String guid;
	
	private AppFileType type;
	
	@Transient
	private Resource file;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public AppFileType getType() {
		return type;
	}

	public void setType(AppFileType type) {
		this.type = type;
	}

	public Resource getFile() {
		return file;
	}

	public void setFile(Resource file) {
		this.file = file;
	}
	
}
