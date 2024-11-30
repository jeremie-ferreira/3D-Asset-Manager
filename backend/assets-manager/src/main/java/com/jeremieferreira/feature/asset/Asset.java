package com.jeremieferreira.feature.asset;

import java.util.ArrayList;
import java.util.List;

import com.jeremieferreira.feature.category.Category;
import com.jeremieferreira.feature.common.Constant.ObjectState;
import com.jeremieferreira.feature.file.AppFile;
import com.jeremieferreira.feature.tag.Tag;
import com.jeremieferreira.feature.user.AppUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Asset {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private ObjectState state = ObjectState.ACTIVE;
	
	private String name;
	
	@Column(length = 4000)
	private String description;
	
	@OneToMany
	private List<AppFile> additionalFiles = new ArrayList<>();
	
	@ManyToOne
	private AppFile mainFile;
	
	@ManyToOne
	private AppFile thumb;

	@ManyToOne
	private AppUser author;
	
	@ManyToOne
	private Category category;
	
	@ManyToMany(cascade = CascadeType.MERGE)
	private List<Tag> tags;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ObjectState getState() {
		return state;
	}

	public void setState(ObjectState state) {
		this.state = state;
	}

	public List<AppFile> getAdditionalFiles() {
		return additionalFiles;
	}

	public void setAdditionalFiles(List<AppFile> additionalFiles) {
		this.additionalFiles = additionalFiles;
	}

	public AppFile getMainFile() {
		return mainFile;
	}

	public void setMainFile(AppFile mainFile) {
		this.mainFile = mainFile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AppFile getThumb() {
		return thumb;
	}

	public void setThumb(AppFile thumb) {
		this.thumb = thumb;
	}

	public AppUser getAuthor() {
		return author;
	}

	public void setAuthor(AppUser author) {
		this.author = author;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
}
