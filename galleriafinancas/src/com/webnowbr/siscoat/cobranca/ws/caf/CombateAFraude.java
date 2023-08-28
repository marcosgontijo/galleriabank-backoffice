package com.webnowbr.siscoat.cobranca.ws.caf;

import java.util.Date;
import java.util.List;

public class CombateAFraude {
	
	private long id;
	private String templateId;	
	private String cpf;
	private String type;
	private String uuid;
	private String requestId;
	private String status;
	private String obs;
	private String retorno;
	private Date date;
	private List<CombateAFraudeFiles> cafFiles;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getObs() {
		return obs;
	}
	public void setObs(String obs) {
		this.obs = obs;
	}
	public String getRetorno() {
		return retorno;
	}
	public void setRetorno(String retorno) {
		this.retorno = retorno;
	}
	public List<CombateAFraudeFiles> getCafFiles() {
		return cafFiles;
	}
	public void setCafFiles(List<CombateAFraudeFiles> cafFiles) {
		this.cafFiles = cafFiles;
	}
}
