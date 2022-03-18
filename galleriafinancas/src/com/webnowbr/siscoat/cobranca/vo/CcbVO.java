package com.webnowbr.siscoat.cobranca.vo;

import java.util.HashSet;
import java.util.Set;

import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedorSocio;

public class CcbVO {
	private String numeroContrato;
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
	private Set<CcbVO> socios;
	
	
	public CcbVO() {
		this.pessoa = new PagadorRecebedor();
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
	public String getNumeroContrato() {
		return numeroContrato;
	}
	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
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
	public Set<CcbVO> getSocios() {
		return socios;
	}
	public void setSocios(Set<CcbVO> socios) {
		this.socios = socios;
	}
	public String getTipoOriginal() {
		return tipoOriginal;
	}
	public void setTipoOriginal(String tipoOriginal) {
		this.tipoOriginal = tipoOriginal;
	}
}
