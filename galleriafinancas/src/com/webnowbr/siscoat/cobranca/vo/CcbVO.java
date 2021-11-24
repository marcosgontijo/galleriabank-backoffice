package com.webnowbr.siscoat.cobranca.vo;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;

public class CcbVO {
	private String numeroContrato;
	private PagadorRecebedor pessoa;
	private String tipoPessoa;
	private String nacionalidade;	
	private boolean fiduciante;
	private boolean feminino;
	private boolean empresa;	
	private String razaoSocial;
	private String tipoEmpresa;
	private String cnpj;
	private String municipioEmpresa;
	private String estadoEmpresa;
	private String ruaEmpresa;
	private String numeroEmpresa;
	private String salaEmpresa;
	private String bairroEmpresa;
	private String cepEmpresa;

	
	
	public PagadorRecebedor getPessoa() {
		return pessoa;
	}
	public void setPessoa(PagadorRecebedor pessoa) {
		this.pessoa = pessoa;
	}
	public String getNumeroContrato() {
		return numeroContrato;
	}
	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}
	public String getTipoPessoa() {
		return tipoPessoa;
	}
	public void setTipoPessoa(String tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}
	
	public String getNacionalidade() {
		return nacionalidade;
	}
	public void setNacionalidade(String nacionalidade) {
		this.nacionalidade = nacionalidade;
	}
	public boolean isFiduciante() {
		return fiduciante;
	}
	public void setFiduciante(boolean fiduciante) {
		this.fiduciante = fiduciante;
	}
	public boolean isFeminino() {
		return feminino;
	}
	public void setFeminino(boolean feminino) {
		this.feminino = feminino;
	}
	public boolean isEmpresa() {
		return empresa;
	}
	public void setEmpresa(boolean empresa) {
		this.empresa = empresa;
	}
	public String getRazaoSocial() {
		return razaoSocial;
	}
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}
	public String getTipoEmpresa() {
		return tipoEmpresa;
	}
	public void setTipoEmpresa(String tipoEmpresa) {
		this.tipoEmpresa = tipoEmpresa;
	}
	public String getCnpj() {
		return cnpj;
	}
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	public String getMunicipioEmpresa() {
		return municipioEmpresa;
	}
	public void setMunicipioEmpresa(String municipioEmpresa) {
		this.municipioEmpresa = municipioEmpresa;
	}
	public String getEstadoEmpresa() {
		return estadoEmpresa;
	}
	public void setEstadoEmpresa(String estadoEmpresa) {
		this.estadoEmpresa = estadoEmpresa;
	}
	public String getRuaEmpresa() {
		return ruaEmpresa;
	}
	public void setRuaEmpresa(String ruaEmpresa) {
		this.ruaEmpresa = ruaEmpresa;
	}
	public String getNumeroEmpresa() {
		return numeroEmpresa;
	}
	public void setNumeroEmpresa(String numeroEmpresa) {
		this.numeroEmpresa = numeroEmpresa;
	}
	public String getSalaEmpresa() {
		return salaEmpresa;
	}
	public void setSalaEmpresa(String salaEmpresa) {
		this.salaEmpresa = salaEmpresa;
	}
	public String getBairroEmpresa() {
		return bairroEmpresa;
	}
	public void setBairroEmpresa(String bairroEmpresa) {
		this.bairroEmpresa = bairroEmpresa;
	}
	public String getCepEmpresa() {
		return cepEmpresa;
	}
	public void setCepEmpresa(String cepEmpresa) {
		this.cepEmpresa = cepEmpresa;
	}
	
	
}
