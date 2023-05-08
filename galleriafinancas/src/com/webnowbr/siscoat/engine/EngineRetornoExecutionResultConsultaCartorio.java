package com.webnowbr.siscoat.engine;

import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

public class EngineRetornoExecutionResultConsultaCartorio {

	@SerializedName("codigo_cidade")
	String codigo_cidade;
	@SerializedName("codigo_cartorio")
	String codigo_cartorio;
	@SerializedName("nome")
	String nome;
	@SerializedName("telefone")
	String telefone;
	@SerializedName("endereco")
	String endereco;
	@SerializedName("uf")
	String uf;
	@SerializedName("cidade")
	String cidade;
	@SerializedName("protestos")
	int protestos;
	@SerializedName("valor_protestado")	
	BigDecimal valor_protestado;
	
	public String getCodigo_cidade() {
		return codigo_cidade;
	}
	public void setCodigo_cidade(String codigo_cidade) {
		this.codigo_cidade = codigo_cidade;
	}
	public String getCodigo_cartorio() {
		return codigo_cartorio;
	}
	public void setCodigo_cartorio(String codigo_cartorio) {
		this.codigo_cartorio = codigo_cartorio;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	public String getEndereco() {
		return endereco;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public String getUf() {
		return uf;
	}
	public void setUf(String uf) {
		this.uf = uf;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public int getProtestos() {
		return protestos;
	}
	public void setProtestos(int protestos) {
		this.protestos = protestos;
	}
	public BigDecimal getValor_protestado() {
		return valor_protestado;
	}
	public void setValor_protestado(BigDecimal valor_protestado) {
		this.valor_protestado = valor_protestado;
	}

	
	
}
