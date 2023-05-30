package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.util.Date;

import com.webnowbr.siscoat.common.DocumentosAnaliseEnum;

public class PagadorRecebedorConsulta implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8221556262702615711L;

	private long id;

	private PagadorRecebedor pessoa;
	private String tipo;
	private DocumentosAnaliseEnum tipoEnum;
	private Date dataConsulta;

	private String retornConsulta;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PagadorRecebedor getPessoa() {
		return pessoa;
	}

	public void setPessoa(PagadorRecebedor pessoa) {
		this.pessoa = pessoa;
	}

	public Date getDataConsulta() {
		return dataConsulta;
	}

	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}

	public String getRetornConsulta() {
		return retornConsulta;
	}

	public void setRetornConsulta(String retornConsulta) {
		this.retornConsulta = retornConsulta;
	}

	public DocumentosAnaliseEnum getTipoEnum() {
		return tipoEnum;
	}

	public void setTipoEnum(DocumentosAnaliseEnum tipoEnum) {
		this.tipoEnum = tipoEnum;
		this.tipo = tipoEnum.getNome();
	}
	
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
		this.tipoEnum = DocumentosAnaliseEnum.parse(tipo);
	}


}
