package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OperacaoContratoIUGU implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;

	private PagadorRecebedor recebedor;
	private BigDecimal vlrRecebedor;
	private BigDecimal saldoRecebedor;
	private String observacao;
		
	public OperacaoContratoIUGU(){
	}
	
	public OperacaoContratoIUGU(PagadorRecebedor recebedor, BigDecimal vlrRecebedor, BigDecimal saldoRecebedor){
		this.recebedor = recebedor;
		this.vlrRecebedor = vlrRecebedor;
		this.saldoRecebedor = saldoRecebedor;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the recebedor
	 */
	public PagadorRecebedor getRecebedor() {
		return recebedor;
	}

	/**
	 * @param recebedor the recebedor to set
	 */
	public void setRecebedor(PagadorRecebedor recebedor) {
		this.recebedor = recebedor;
	}

	/**
	 * @return the vlrRecebedor
	 */
	public BigDecimal getVlrRecebedor() {
		return vlrRecebedor;
	}

	/**
	 * @param vlrRecebedor the vlrRecebedor to set
	 */
	public void setVlrRecebedor(BigDecimal vlrRecebedor) {
		this.vlrRecebedor = vlrRecebedor;
	}

	/**
	 * @return the saldoRecebedor
	 */
	public BigDecimal getSaldoRecebedor() {
		return saldoRecebedor;
	}

	/**
	 * @param saldoRecebedor the saldoRecebedor to set
	 */
	public void setSaldoRecebedor(BigDecimal saldoRecebedor) {
		this.saldoRecebedor = saldoRecebedor;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the observacao
	 */
	public String getObservacao() {
		return observacao;
	}

	/**
	 * @param observacao the observacao to set
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}	
}