package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class CartoriosProtesto implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String Cidade;
	private String Nome;
	private String Endereco;
	private String Observacao;
	private String Telefone;
	private String CodigoCartorio;
	private String CodigoCidade;
	private Date DataAtualizacao;
	private int NumProtestos;
	
	private List<Protesto> protesto;

	
	public CartoriosProtesto() {
		
	}


	public String getCidade() {
		return Cidade;
	}


	public void setCidade(String cidade) {
		Cidade = cidade;
	}


	public String getNome() {
		return Nome;
	}


	public void setNome(String nome) {
		Nome = nome;
	}


	public String getEndereco() {
		return Endereco;
	}


	public void setEndereco(String endereco) {
		Endereco = endereco;
	}


	public String getObservacao() {
		return Observacao;
	}


	public void setObservacao(String observacao) {
		Observacao = observacao;
	}


	public String getTelefone() {
		return Telefone;
	}


	public void setTelefone(String telefone) {
		Telefone = telefone;
	}


	public String getCodigoCartorio() {
		return CodigoCartorio;
	}


	public void setCodigoCartorio(String codigoCartorio) {
		CodigoCartorio = codigoCartorio;
	}


	public String getCodigoCidade() {
		return CodigoCidade;
	}


	public void setCodigoCidade(String codigoCidade) {
		CodigoCidade = codigoCidade;
	}


	public Date getDataAtualizacao() {
		return DataAtualizacao;
	}


	public void setDataAtualizacao(Date dataAtualizacao) {
		DataAtualizacao = dataAtualizacao;
	}


	public int getNumProtestos() {
		return NumProtestos;
	}


	public void setNumProtestos(int numProtestos) {
		NumProtestos = numProtestos;
	}


	public List<Protesto> getProtesto() {
		return protesto;
	}


	public void setProtesto(List<Protesto> protesto) {
		this.protesto = protesto;
	}	
}