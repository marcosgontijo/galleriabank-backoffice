package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class AnaliseComite implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3364649308267521304L;

	private long id;
	
	private BigDecimal taxaComite;
	private BigInteger prazoMaxComite;
	private BigDecimal valorComite;
	private Date dataComite;
	private String tipoValorComite;
	private String comentarioComite;
	private String usuarioComite;
	private String votoAnaliseComite;
	private int carenciaComite;
	
	private ContratoCobranca contratoCobranca;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BigDecimal getTaxaComite() {
		return taxaComite;
	}

	public void setTaxaComite(BigDecimal taxaComite) {
		this.taxaComite = taxaComite;
	}

	public BigInteger getPrazoMaxComite() {
		return prazoMaxComite;
	}

	public void setPrazoMaxComite(BigInteger prazoMaxComite) {
		this.prazoMaxComite = prazoMaxComite;
	}

	public BigDecimal getValorComite() {
		return valorComite;
	}

	public void setValorComite(BigDecimal valorComite) {
		this.valorComite = valorComite;
	}

	public String getTipoValorComite() {
		return tipoValorComite;
	}

	public Date getDataComite() {
		return dataComite;
	}

	public void setDataComite(Date dataComite) {
		this.dataComite = dataComite;
	}

	public void setTipoValorComite(String tipoValorComite) {
		this.tipoValorComite = tipoValorComite;
	}

	public String getComentarioComite() {
		return comentarioComite;
	}

	public void setComentarioComite(String comentarioComite) {
		this.comentarioComite = comentarioComite;
	}

	public String getUsuarioComite() {
		return usuarioComite;
	}

	public void setUsuarioComite(String usuarioComite) {
		this.usuarioComite = usuarioComite;
	}

	public ContratoCobranca getContratoCobranca() {
		return contratoCobranca;
	}

	public void setContratoCobranca(ContratoCobranca contratoCobranca) {
		this.contratoCobranca = contratoCobranca;
	}

	public String getVotoAnaliseComite() {
		return votoAnaliseComite;
	}

	public void setVotoAnaliseComite(String votoAnaliseComite) {
		this.votoAnaliseComite = votoAnaliseComite;
	}

	public int getCarenciaComite() {
		return carenciaComite;
	}

	public void setCarenciaComite(int carenciaComite) {
		this.carenciaComite = carenciaComite;
	}
	
	
}
