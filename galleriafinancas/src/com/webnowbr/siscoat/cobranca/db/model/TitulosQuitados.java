package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class TitulosQuitados implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private long id;
	private Date dataCalculo;
	private Date dataAtualizacao;
	private BigDecimal txJuros;
	private BigDecimal multa;
	private BigDecimal honorarios;
	private String identificacaoCalculo;
	private String descricao;
	private boolean imprimeTaxas;
	
	private PagadorRecebedor recebedor;
	
	private List<CalculosDetalhes> listCalculoDetalhes;
	
	public TitulosQuitados(){

	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the dataCalculo
	 */
	public Date getDataCalculo() {
		return dataCalculo;
	}

	/**
	 * @param dataCalculo the dataCalculo to set
	 */
	public void setDataCalculo(Date dataCalculo) {
		this.dataCalculo = dataCalculo;
	}

	/**
	 * @return the dataAtualizacao
	 */
	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	/**
	 * @param dataAtualizacao the dataAtualizacao to set
	 */
	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	/**
	 * @return the txJuros
	 */
	public BigDecimal getTxJuros() {
		return txJuros;
	}

	/**
	 * @param txJuros the txJuros to set
	 */
	public void setTxJuros(BigDecimal txJuros) {
		this.txJuros = txJuros;
	}

	/**
	 * @return the multa
	 */
	public BigDecimal getMulta() {
		return multa;
	}

	/**
	 * @param multa the multa to set
	 */
	public void setMulta(BigDecimal multa) {
		this.multa = multa;
	}

	/**
	 * @return the identificacaoCalculo
	 */
	public String getIdentificacaoCalculo() {
		return identificacaoCalculo;
	}

	/**
	 * @param identificacaoCalculo the identificacaoCalculo to set
	 */
	public void setIdentificacaoCalculo(String identificacaoCalculo) {
		this.identificacaoCalculo = identificacaoCalculo;
	}

	/**
	 * @return the descricao
	 */
	public String getDescricao() {
		return descricao;
	}

	/**
	 * @param descricao the descricao to set
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * @return the listCalculoDetalhes
	 */
	public List<CalculosDetalhes> getListCalculoDetalhes() {
		return listCalculoDetalhes;
	}

	/**
	 * @param listCalculoDetalhes the listCalculoDetalhes to set
	 */
	public void setListCalculoDetalhes(List<CalculosDetalhes> listCalculoDetalhes) {
		this.listCalculoDetalhes = listCalculoDetalhes;
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
	 * @return the imprimeTaxas
	 */
	public boolean isImprimeTaxas() {
		return imprimeTaxas;
	}

	/**
	 * @param imprimeTaxas the imprimeTaxas to set
	 */
	public void setImprimeTaxas(boolean imprimeTaxas) {
		this.imprimeTaxas = imprimeTaxas;
	}

	/**
	 * @return the honorarios
	 */
	public BigDecimal getHonorarios() {
		return honorarios;
	}

	/**
	 * @param honorarios the honorarios to set
	 */
	public void setHonorarios(BigDecimal honorarios) {
		this.honorarios = honorarios;
	}
}