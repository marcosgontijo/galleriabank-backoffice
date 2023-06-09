package com.webnowbr.siscoat.cobranca.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "contrato_tipo_template_campo", schema = "cobranca")
public class ContratoTipoTemplateCampo {

	@Id
	@Column(name = "cttc_id_codigoTipoTemplateCampo", nullable = false)
	private Integer codigoTipoTemplateCampo;
	
	@Column(name = "cttc_no_tag", length=50, nullable = false)
	private String tag;
	
	@Column(name = "cttc_de_expressao", length=1000, nullable = false)
	private String expressao;
	
	public Object getId() {
		return getCodigoTipoTemplateCampo();
	}


	/** @return the codigoTipoTemplateCampo */
	public Integer getCodigoTipoTemplateCampo() {
		return codigoTipoTemplateCampo;
	}


	/** @param codigoTipoTemplateCampo the codigoTipoTemplateCampo to set */
	public void setCodigoTipoTemplateCampo(Integer codigoTipoTemplateCampo) {
		this.codigoTipoTemplateCampo = codigoTipoTemplateCampo;
	}


	/** @return the tag */
	public String getTag() {
		return tag;
	}


	/** @param tag the tag to set */
	public void setTag(String tag) {
		this.tag = tag;
	}


	/** @return the expressao */
	public String getExpressao() {
		return expressao;
	}


	/** @param expressao the expressao to set */
	public void setExpressao(String expressao) {
		this.expressao = expressao;
	}

	
	


}
