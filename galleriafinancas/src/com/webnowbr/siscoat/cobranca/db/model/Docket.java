package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.common.EstadosEnum;

public class Docket implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private long id;
	private ContratoCobranca objetoContratoCobranca; //op de referencia
	private List<PagadorRecebedor> listaPagador; //titulares pra enviar pedido
	private String estado;
	private String estadoDocketId;
	private String cidade;
	private String cidadeDocketId;
	private String usuario;
	private Date data;
	
	
	public Docket(){

	}
	
	public Docket(ContratoCobranca objetoContratoCobranca, List<PagadorRecebedor> listaPagador, String estado,
			String estadoDocketId, String cidade, String cidadeDocketId, String usuario, Date data) {
		super();
		this.objetoContratoCobranca = objetoContratoCobranca;
		this.listaPagador = listaPagador;
		this.estado = estado;
		this.estadoDocketId = estadoDocketId;
		this.cidade = cidade;
		this.cidadeDocketId = cidadeDocketId;
		this.usuario = usuario;
		this.data = data;
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ContratoCobranca getObjetoContratoCobranca() {
		return objetoContratoCobranca;
	}

	public void setObjetoContratoCobranca(ContratoCobranca objetoContratoCobranca) {
		this.objetoContratoCobranca = objetoContratoCobranca;
	}

	public List<PagadorRecebedor> getListaPagador() {
		return listaPagador;
	}

	public void setListaPagador(List<PagadorRecebedor> listaPagador) {
		this.listaPagador = listaPagador;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getEstadoDocketId() {
		return estadoDocketId;
	}

	public void setEstadoDocketId(String estadoDocketId) {
		this.estadoDocketId = estadoDocketId;
	}

	public String getCidadeDocketId() {
		return cidadeDocketId;
	}

	public void setCidadeDocketId(String cidadeDocketId) {
		this.cidadeDocketId = cidadeDocketId;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
	
}