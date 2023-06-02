package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class StarkBankBoleto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private long id;
	
    public BigDecimal amount;
    public String taxId;
    public String description;
    public String line;
    public String barCode;
    public String scheduled;
    public String tags;
    public String status;
    public Integer fee;
    public Date created;

	public StarkBankBoleto(){

	}
	
    public StarkBankBoleto(long id, BigDecimal amount, String taxId, String tags, String description, String scheduled,
            String line, String barCode, Integer fee, String status, Date created) {
		this.id = id;
		this.taxId = taxId;
		this.description = description;
		this.line = line;
		this.barCode = barCode;
		this.scheduled = scheduled;
		this.tags = tags;
		this.amount = amount;
		this.status = status;
		this.fee = fee;
		this.created = created;
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

	public String getTaxId() {
		return taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
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

	public Integer getFee() {
		return fee;
	}

	public void setFee(Integer fee) {
		this.fee = fee;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
}