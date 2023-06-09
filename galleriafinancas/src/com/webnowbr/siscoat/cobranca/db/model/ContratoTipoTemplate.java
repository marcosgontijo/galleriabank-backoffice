package com.webnowbr.siscoat.cobranca.db.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "contrato_tipo_template", schema = "cobranca")
public class ContratoTipoTemplate {
	
	@Id
	@Column(name = "titm_id_codigoTipoTemplate", nullable = false)
	private Character codigoTipoTemplate;
	
	@Column(name = "titm_de_descricaoTemplate", length = 100, nullable = false)
	private String descricao;
	
	/** Prefixo dos nomes dos arquivos gerados a partir deste template */
	@Column(name = "titm_no_prefixoNomeArquivo", length = 20, nullable = false)
	private String prefixoNomeArquivo;
	
	/** ordem em que os tipos são apresentados */
	@Column(name = "titm_nu_ordem", nullable = false)
	private Integer ordem;
	
	/** Parâmetros do template */
	@OneToMany(mappedBy = "template", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ContratoTipoTemplateBloco> blocos;

	
	public Object getId() {
		return getCodigoTipoTemplate();
	}

	/** @return the codigoTipoTemplate */
	public Character getCodigoTipoTemplate() {
		return codigoTipoTemplate;
	}

	/** @param codigoTipoTemplate the codigoTipoTemplate to set */
	public void setCodigoTipoTemplate(Character codigoTipoTemplate) {
		this.codigoTipoTemplate = codigoTipoTemplate;
	}

	/** @return the descricao */
	public String getDescricao() {
		return descricao;
	}

	/** @param descricao the descricao to set */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/** @return the prefixoNomeArquivo */
	public String getPrefixoNomeArquivo() {
		return prefixoNomeArquivo;
	}

	/** @param prefixoNomeArquivo the prefixoNomeArquivo to set */
	public void setPrefixoNomeArquivo(String prefixoNomeArquivo) {
		this.prefixoNomeArquivo = prefixoNomeArquivo;
	}

	/** @return the ordem */
	public Integer getOrdem() {
		return ordem;
	}

	/** @param ordem the ordem to set */
	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	/** @return the blocos */
	public List<ContratoTipoTemplateBloco> getBlocos() {
		return blocos;
	}

	/** @param blocos the blocos to set */
	public void setBlocos(List<ContratoTipoTemplateBloco> blocos) {
		this.blocos = blocos;
	}



}
