package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.util.Date;

public class UniProofDocuments implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private long id;
		
	private String idDocuments;
	private int currentVersion;
	private String name;
	private String extension;
	private int pages;
	private String typeId;
	private String typeName;
	private String typeLabel;
	private Date createdAt;
	private Date attachedAt;
	
	public UniProofDocuments(){

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIdDocuments() {
		return idDocuments;
	}

	public void setIdDocuments(String idDocuments) {
		this.idDocuments = idDocuments;
	}

	public int getCurrentVersion() {
		return currentVersion;
	}

	public void setCurrentVersion(int currentVersion) {
		this.currentVersion = currentVersion;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeLabel() {
		return typeLabel;
	}

	public void setTypeLabel(String typeLabel) {
		this.typeLabel = typeLabel;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getAttachedAt() {
		return attachedAt;
	}

	public void setAttachedAt(Date attachedAt) {
		this.attachedAt = attachedAt;
	}
}