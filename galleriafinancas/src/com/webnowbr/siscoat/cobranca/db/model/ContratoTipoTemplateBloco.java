package com.webnowbr.siscoat.cobranca.db.model;

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

@Entity
@Table(name = "contrato_tipo_template_bloco", schema = "cobranca")
public class ContratoTipoTemplateBloco {

	@Id
	@Column(name = "cttb_id_codigoTipoTemplateBloco", nullable = false)
	private Integer codigoContratoTipoTemplateBloco;
		
	@Column(name = "titm_id_codigoTipoTemplate", nullable = false)
	private Character codigoTipoTemplate;
	
	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "titm_id_codigoTipoTemplate", nullable = false, insertable = false, updatable = false)
	private ContratoTipoTemplate template; 
	
	@Column(name = "cttb_no_tagIdentificacao", length=50)
	private String tagIdentificacao;
	
	@Column(name = "cttb_no_descricao", length=50, nullable = false)
	private String descricao;
	
	/** Documentos gerados para este contrato */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "contrato_tipo_template_bloco_campo", joinColumns = { @JoinColumn(name = "cttb_id_codigoTipoTemplateBloco", referencedColumnName = "cttb_id_codigoTipoTemplateBloco") }, inverseJoinColumns = { @JoinColumn(name = "cttc_id_codigoTipoTemplateCampo", referencedColumnName = "cttc_id_codigoTipoTemplateCampo") })
	private List<ContratoTipoTemplateCampo> campos = new ArrayList<ContratoTipoTemplateCampo>(
			0);
	
	@Column(name = "cttb_id_codigoTipoTemplateBlocoPai", nullable = true)
	private Integer codigoContratoTipoTemplateBlocoPai;
	
	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "cttb_id_codigoTipoTemplateBlocoPai", nullable = true, insertable = false, updatable = false)
	private ContratoTipoTemplateBloco contratoTipoTemplateBlocoPai;
	
	/** Parâmetros do template */
	@OneToMany(mappedBy = "contratoTipoTemplateBlocoPai", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ContratoTipoTemplateBloco>  blocosFilho;
	
	@Column(name = "cttb_fl_inativo", nullable = false)
	private Boolean flagInativo;
	

	public Object getId() {		
		return getCodigoContratoTipoTemplateBloco();
	}

	/** @return the codigoContratoTipoTemplateBloco */
	public Integer getCodigoContratoTipoTemplateBloco() {
		return codigoContratoTipoTemplateBloco;
	}

	/** @param codigoContratoTipoTemplateBloco the codigoContratoTipoTemplateBloco to set */
	public void setCodigoContratoTipoTemplateBloco(
			Integer codigoContratoTipoTemplateBloco) {
		this.codigoContratoTipoTemplateBloco = codigoContratoTipoTemplateBloco;
	}

	/** @return the codigoTipoTemplate */
	public Character getCodigoTipoTemplate() {
		return codigoTipoTemplate;
	}

	/** @param codigoTipoTemplate the codigoTipoTemplate to set */
	public void setCodigoTipoTemplate(Character codigoTipoTemplate) {
		this.codigoTipoTemplate = codigoTipoTemplate;
	}

	/** @return the template */
	public ContratoTipoTemplate getTemplate() {
		return template;
	}

	/** @param template the template to set */
	public void setTemplate(ContratoTipoTemplate template) {
		this.template = template;
	}

	/** @return the tagIdentificacao */
	public String getTagIdentificacao() {
		return tagIdentificacao;
	}

	/** @param tagIdentificacao the tagIdentificacao to set */
	public void setTagIdentificacao(String tagIdentificacao) {
		this.tagIdentificacao = tagIdentificacao;
	}

	/** @return the descricao */
	public String getDescricao() {
		return descricao;
	}

	/** @param descricao the descricao to set */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/** @return the campos */
	public List<ContratoTipoTemplateCampo> getCampos() {
		return campos;
	}

	/** @param campos the campos to set */
	public void setCampos(List<ContratoTipoTemplateCampo> campos) {
		this.campos = campos;
	}

	/** @return the blocosFilho */
	public List<ContratoTipoTemplateBloco> getBlocosFilho() {
		return blocosFilho;
	}

	/** @param blocosFilho the blocosFilho to set */
	public void setBlocosFilho(List<ContratoTipoTemplateBloco> blocosFilho) {
		this.blocosFilho = blocosFilho;
	}

	/** @return the flagInativo */
	public Boolean getFlagInativo() {
		return flagInativo;
	}

	/** @param flagInativo the flagInativo to set */
	public void setFlagInativo(Boolean flagInativo) {
		this.flagInativo = flagInativo;
	}

	/** @return the codigoContratoTipoTemplateBlocoPai */
	public Integer getCodigoContratoTipoTemplateBlocoPai() {
		return codigoContratoTipoTemplateBlocoPai;
	}

	/** @param codigoContratoTipoTemplateBlocoPai the codigoContratoTipoTemplateBlocoPai to set */
	public void setCodigoContratoTipoTemplateBlocoPai(
			Integer codigoContratoTipoTemplateBlocoPai) {
		this.codigoContratoTipoTemplateBlocoPai = codigoContratoTipoTemplateBlocoPai;
	}

	/** @return the contratoTipoTemplateBlocoPai */
	public ContratoTipoTemplateBloco getContratoTipoTemplateBlocoPai() {
		return contratoTipoTemplateBlocoPai;
	}

	/** @param contratoTipoTemplateBlocoPai the contratoTipoTemplateBlocoPai to set */
	public void setContratoTipoTemplateBlocoPai(
			ContratoTipoTemplateBloco contratoTipoTemplateBlocoPai) {
		this.contratoTipoTemplateBlocoPai = contratoTipoTemplateBlocoPai;
	}

	


}
