package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class CcbParticipantes implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long id;

	private PagadorRecebedor pessoa;
	private String tipoParticipante;
	private String tipoOriginal;
	private String nacionalidade;	
	private boolean fiduciante;
	private boolean feminino;
	private boolean empresa;	
	
	private String tipoEmpresa;
	private String municipioEmpresa;
	private String salaEmpresa;
	private Set<CcbParticipantes> socios;
	
	
	public CcbParticipantes() {
		this.pessoa = new PagadorRecebedor();
		this.nacionalidade = "brasileiro";
		this.socios	= new HashSet<>();
		this.fiduciante = false;
		this.feminino = false;
		this.empresa = false;
	}
	
	
	public PagadorRecebedor getPessoa() {
		return pessoa;
	}
	public void setPessoa(PagadorRecebedor pessoa) {
		this.pessoa = pessoa;
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
	public String getTipoEmpresa() {
		return tipoEmpresa;
	}
	public void setTipoEmpresa(String tipoEmpresa) {
		this.tipoEmpresa = tipoEmpresa;
	}
	public String getMunicipioEmpresa() {
		return municipioEmpresa;
	}
	public void setMunicipioEmpresa(String municipioEmpresa) {
		this.municipioEmpresa = municipioEmpresa;
	}
	public String getSalaEmpresa() {
		return salaEmpresa;
	}
	public void setSalaEmpresa(String salaEmpresa) {
		this.salaEmpresa = salaEmpresa;
	}
	public String getTipoParticipante() {
		return tipoParticipante;
	}
	public void setTipoParticipante(String tipoParticipante) {
		this.tipoParticipante = tipoParticipante;
	}
	public Set<CcbParticipantes> getSocios() {
		return socios;
	}
	public void setSocios(Set<CcbParticipantes> socios) {
		this.socios = socios;
	}
	public String getTipoOriginal() {
		return tipoOriginal;
	}
	public void setTipoOriginal(String tipoOriginal) {
		this.tipoOriginal = tipoOriginal;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
}
