package com.webnowbr.siscoat.cobranca.model.docket;

import java.util.Date;

import javax.persistence.Transient;

import com.google.gson.annotations.SerializedName;

public class DocketDocumentoCampo {

	@SerializedName("nomeCompleto")
	private String nomeCompleto;

	@SerializedName("cpf")
	private String cpf;

	@SerializedName("nomeMae")
	private String nomeMae;

	@SerializedName("rg")
	private String rg;

	@SerializedName("dataNascimento")
	private Date dataNascimento;

	@SerializedName("cidade")
	private String cidade;

	@SerializedName("estado")
	private String estado;
	
	@Transient
	private String estadoNome;

	@SerializedName("cnpj")
	private String cnpj;

	@SerializedName("razaoSocial")
	private String razaoSocial;

	public String getNomeCompleto() {
		return nomeCompleto;
	}

	public void setNomeCompleto(String nomeCompleto) {
		this.nomeCompleto = nomeCompleto;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getEstadoNome() {
		return estadoNome;
	}

	public void setEstadoNome(String estadoNome) {
		this.estadoNome = estadoNome;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}
	
	
}
