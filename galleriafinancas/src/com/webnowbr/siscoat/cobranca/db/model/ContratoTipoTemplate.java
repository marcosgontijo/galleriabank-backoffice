package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.util.List;

public class ContratoTipoTemplate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4833210242196264707L;

	private long id;

	private String codigoTipoTemplate;
	
	private String descricao;

	/** Prefixo dos nomes dos arquivos gerados a partir deste template */
	private String prefixoNomeArquivo;

	/** ordem em que os tipos são apresentados */
	private Integer ordem;

	/** Parâmetros do template */
	private List<ContratoTipoTemplateBloco> blocos;

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

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getPrefixoNomeArquivo() {
		return prefixoNomeArquivo;
	}

	public void setPrefixoNomeArquivo(String prefixoNomeArquivo) {
		this.prefixoNomeArquivo = prefixoNomeArquivo;
	}

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public List<ContratoTipoTemplateBloco> getBlocos() {
		return blocos;
	}

	public void setBlocos(List<ContratoTipoTemplateBloco> blocos) {
		this.blocos = blocos;
	}

}
