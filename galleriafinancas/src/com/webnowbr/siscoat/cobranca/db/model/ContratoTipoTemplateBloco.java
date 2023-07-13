package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

public class ContratoTipoTemplateBloco implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6099658801190361222L;

	private long id;
	
	private String codigoTipoTemplate;
	
	private String codigoTipoTemplateBloco;

	private ContratoTipoTemplate template;

	private String tagIdentificacao;

	private String descricao;

	private List<ContratoTipoTemplateCampo> campos = new ArrayList<ContratoTipoTemplateCampo>(0);

	private Integer codigoContratoTipoTemplateBlocoPai;

	private ContratoTipoTemplateBloco contratoTipoTemplateBlocoPai;

	private List<ContratoTipoTemplateBloco> blocosFilho;

	private Boolean flagInativo;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCodigoTipoTemplate() {
		return codigoTipoTemplate;
	}

	public void setCodigoTipoTemplate(String codigoTipoTemplate) {
		this.codigoTipoTemplate = codigoTipoTemplate;
	}

	public String getCodigoTipoTemplateBloco() {
		return codigoTipoTemplateBloco;
	}

	public void setCodigoTipoTemplateBloco(String codigoTipoTemplateBloco) {
		this.codigoTipoTemplateBloco = codigoTipoTemplateBloco;
	}

	public ContratoTipoTemplate getTemplate() {
		return template;
	}

	public void setTemplate(ContratoTipoTemplate template) {
		this.template = template;
	}

	public String getTagIdentificacao() {
		return tagIdentificacao;
	}

	public void setTagIdentificacao(String tagIdentificacao) {
		this.tagIdentificacao = tagIdentificacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<ContratoTipoTemplateCampo> getCampos() {
		return campos;
	}

	public void setCampos(List<ContratoTipoTemplateCampo> campos) {
		this.campos = campos;
	}

	public Integer getCodigoContratoTipoTemplateBlocoPai() {
		return codigoContratoTipoTemplateBlocoPai;
	}

	public void setCodigoContratoTipoTemplateBlocoPai(Integer codigoContratoTipoTemplateBlocoPai) {
		this.codigoContratoTipoTemplateBlocoPai = codigoContratoTipoTemplateBlocoPai;
	}

	public ContratoTipoTemplateBloco getContratoTipoTemplateBlocoPai() {
		return contratoTipoTemplateBlocoPai;
	}

	public void setContratoTipoTemplateBlocoPai(ContratoTipoTemplateBloco contratoTipoTemplateBlocoPai) {
		this.contratoTipoTemplateBlocoPai = contratoTipoTemplateBlocoPai;
	}

	public List<ContratoTipoTemplateBloco> getBlocosFilho() {
		return blocosFilho;
	}

	public void setBlocosFilho(List<ContratoTipoTemplateBloco> blocosFilho) {
		this.blocosFilho = blocosFilho;
	}

	public Boolean getFlagInativo() {
		return flagInativo;
	}

	public void setFlagInativo(Boolean flagInativo) {
		this.flagInativo = flagInativo;
	}

}
