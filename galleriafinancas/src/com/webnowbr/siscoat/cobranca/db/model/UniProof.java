	package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class UniProof implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private long id;
	
	private String companyToken;
	private String lotId;
	private String lotItemId;
	private String lotName;
	private String lotDescription;
	private String folderId;
	private String folderName;
	private String folderDescription;
	private String serviceName;
	private String protocol;
	private String cityId;
	private String cityName;
	private Date createdAt;
	private Date updatedAt;
	private String statusName;
	private String statusDescription;
	private String statusLabel;
	private BigDecimal notaryPrice;
	private BigDecimal uniproofPrice;
	private BigDecimal finalPrice;
	
	private List<UniProofDocuments> listUniProofDocuments;
	
	public UniProof(){

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCompanyToken() {
		return companyToken;
	}

	public void setCompanyToken(String companyToken) {
		this.companyToken = companyToken;
	}

	public String getLotId() {
		return lotId;
	}

	public void setLotId(String lotId) {
		this.lotId = lotId;
	}

	public String getLotItemId() {
		return lotItemId;
	}

	public void setLotItemId(String lotItemId) {
		this.lotItemId = lotItemId;
	}

	public String getLotName() {
		return lotName;
	}

	public void setLotName(String lotName) {
		this.lotName = lotName;
	}

	public String getLotDescription() {
		return lotDescription;
	}

	public void setLotDescription(String lotDescription) {
		this.lotDescription = lotDescription;
	}

	public String getFolderId() {
		return folderId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getFolderDescription() {
		return folderDescription;
	}

	public void setFolderDescription(String folderDescription) {
		this.folderDescription = folderDescription;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}

	public String getStatusLabel() {
		return statusLabel;
	}

	public void setStatusLabel(String statusLabel) {
		this.statusLabel = statusLabel;
	}

	public BigDecimal getNotaryPrice() {
		return notaryPrice;
	}

	public void setNotaryPrice(BigDecimal notaryPrice) {
		this.notaryPrice = notaryPrice;
	}

	public BigDecimal getUniproofPrice() {
		return uniproofPrice;
	}

	public void setUniproofPrice(BigDecimal uniproofPrice) {
		this.uniproofPrice = uniproofPrice;
	}

	public List<UniProofDocuments> getListUniProofDocuments() {
		return listUniProofDocuments;
	}

	public void setListUniProofDocuments(List<UniProofDocuments> listUniProofDocuments) {
		this.listUniProofDocuments = listUniProofDocuments;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public BigDecimal getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(BigDecimal finalPrice) {
		this.finalPrice = finalPrice;
	}
}