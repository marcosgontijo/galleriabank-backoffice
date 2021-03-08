package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.util.List;

public class ContaContabil implements Serializable {

	
	private static final long serialVersionUID = 8949787945531085947L;

	private long id;
	private String nome;
	
	private String codigoContaContabil;
	
	private ContaContabil contaContabilPai;
	
	public ContaContabil() {		
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getCodigoContaContabil() {
		return codigoContaContabil;
	}

	public void setCodigoContaContabil(String codigoContaContabil) {
		this.codigoContaContabil = codigoContaContabil;
	}

	public ContaContabil getContaContabilPai() {
		return contaContabilPai;
	}
	public void setContaContabilPai(ContaContabil contaContabilPai) {
		this.contaContabilPai = contaContabilPai;
	}
}
