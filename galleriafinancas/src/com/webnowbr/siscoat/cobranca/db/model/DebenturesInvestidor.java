package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class DebenturesInvestidor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String numeroCautela;
	private String serie;
	private String numeroDocumento;
	
	private String quitado;
	
	private BigDecimal porcentagemDebentures;
	
	private Date dataDebentures;
	private Date dataVencimento;
	
	private int qtdeDebentures;
	
	private PagadorRecebedor recebedor;
	
	private ContratoCobranca contrato;
	
	private boolean lastrearTitulos;
	
	// Não persistido, utilizado no relatorio de Titulos Quitados.
	private BigDecimal valorFace;
	
	private BigDecimal valorDebenture;
	private BigDecimal taxa;
	private int prazo;
	private BigDecimal parcelaMensal;
	private BigDecimal parcelaFinal;
	private Date dataUltimaParcela;
	
	private BigDecimal valorUltimaParcelaPaga;
	private Date dataUltimaParcelaPaga;
	
	public DebenturesInvestidor(){
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNumeroCautela() {
		return numeroCautela;
	}

	public void setNumeroCautela(String numeroCautela) {
		this.numeroCautela = numeroCautela;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public BigDecimal getPorcentagemDebentures() {
		return porcentagemDebentures;
	}

	public void setPorcentagemDebentures(BigDecimal porcentagemDebentures) {
		this.porcentagemDebentures = porcentagemDebentures;
	}

	public Date getDataDebentures() {
		return dataDebentures;
	}

	public void setDataDebentures(Date dataDebentures) {
		this.dataDebentures = dataDebentures;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public int getQtdeDebentures() {
		return qtdeDebentures;
	}

	public void setQtdeDebentures(int qtdeDebentures) {
		this.qtdeDebentures = qtdeDebentures;
	}

	public PagadorRecebedor getRecebedor() {
		return recebedor;
	}

	public void setRecebedor(PagadorRecebedor recebedor) {
		this.recebedor = recebedor;
	}

	public ContratoCobranca getContrato() {
		return contrato;
	}

	public void setContrato(ContratoCobranca contrato) {
		this.contrato = contrato;
	}

	public boolean isLastrearTitulos() {
		return lastrearTitulos;
	}

	public void setLastrearTitulos(boolean lastrearTitulos) {
		this.lastrearTitulos = lastrearTitulos;
	}

	public BigDecimal getValorFace() {
		return valorFace;
	}

	public void setValorFace(BigDecimal valorFace) {
		this.valorFace = valorFace;
	}

	public BigDecimal getValorDebenture() {
		return valorDebenture;
	}

	public void setValorDebenture(BigDecimal valorDebenture) {
		this.valorDebenture = valorDebenture;
	}

	public BigDecimal getTaxa() {
		return taxa;
	}

	public void setTaxa(BigDecimal taxa) {
		this.taxa = taxa;
	}

	public BigDecimal getParcelaMensal() {
		return parcelaMensal;
	}

	public void setParcelaMensal(BigDecimal parcelaMensal) {
		this.parcelaMensal = parcelaMensal;
	}

	public BigDecimal getParcelaFinal() {
		return parcelaFinal;
	}

	public void setParcelaFinal(BigDecimal parcelaFinal) {
		this.parcelaFinal = parcelaFinal;
	}

	public Date getDataUltimaParcela() {
		return dataUltimaParcela;
	}

	public void setDataUltimaParcela(Date dataUltimaParcela) {
		this.dataUltimaParcela = dataUltimaParcela;
	}

	public int getPrazo() {
		return prazo;
	}

	public void setPrazo(int prazo) {
		this.prazo = prazo;
	}

	public String getQuitado() {
		return quitado;
	}

	public void setQuitado(String quitado) {
		this.quitado = quitado;
	}

	public BigDecimal getValorUltimaParcelaPaga() {
		return valorUltimaParcelaPaga;
	}

	public void setValorUltimaParcelaPaga(BigDecimal valorUltimaParcelaPaga) {
		this.valorUltimaParcelaPaga = valorUltimaParcelaPaga;
	}

	public Date getDataUltimaParcelaPaga() {
		return dataUltimaParcelaPaga;
	}

	public void setDataUltimaParcelaPaga(Date dataUltimaParcelaPaga) {
		this.dataUltimaParcelaPaga = dataUltimaParcelaPaga;
	}
}