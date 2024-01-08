package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class StarkBankTax implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private long id;
	
    public BigDecimal amount;
    public String description;
    public String line;
    public String barCode;
    public String scheduled;
    public String tags;
    public String status;
    public Date created;
    public String pathComprovante;
    public String nomeComprovante;
    public String motivoFalha;
    
	public StarkBankTax(){

	}
	
    public StarkBankTax(long id, BigDecimal amount, String tags, String description, String scheduled,
            String line, String barCode, String status, Date created, String pathComprovante, String nomeComprovante) {
		this.id = id;
		this.description = description;
		this.line = line;
		this.barCode = barCode;
		this.scheduled = scheduled;
		this.tags = tags;
		this.amount = amount;
		this.status = status;
		this.created = created;
		this.pathComprovante = pathComprovante;
		this.nomeComprovante = nomeComprovante;
	}
    
    public StarkBankTax(long id, BigDecimal amount, String tags, String description, String scheduled,
            String line, String barCode, String status, Date created, String motivoFalha) {
		this.id = id;
		this.description = description;
		this.line = line;
		this.barCode = barCode;
		this.scheduled = scheduled;
		this.tags = tags;
		this.amount = amount;
		this.status = status;
		this.created = created;
		this.motivoFalha = motivoFalha;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getScheduled() {
		return scheduled;
	}

	public void setScheduled(String scheduled) {
		this.scheduled = scheduled;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getPathComprovante() {
		return pathComprovante;
	}

	public void setPathComprovante(String pathComprovante) {
		this.pathComprovante = pathComprovante;
	}

	public String getNomeComprovante() {
		return nomeComprovante;
	}

	public void setNomeComprovante(String nomeComprovante) {
		this.nomeComprovante = nomeComprovante;
	}

	public String getMotivoFalha() {
		return motivoFalha;
	}

	public void setMotivoFalha(String motivoFalha) {
		this.motivoFalha = motivoFalha;
	}
}