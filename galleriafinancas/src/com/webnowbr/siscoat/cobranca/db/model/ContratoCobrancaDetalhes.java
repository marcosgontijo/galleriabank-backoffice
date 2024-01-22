package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.simulador.SimulacaoDetalheVO;

public class ContratoCobrancaDetalhes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String numeroParcela;
	private Date dataVencimento;
	private Date dataVencimentoAtual;
	private Date dataPagamento;
	private Date promessaPagamento;
	private BigDecimal vlrParcela;
	private BigDecimal vlrBoletoKobana;
	private BigDecimal vlrJuros;
	private BigDecimal txMulta;
	private BigDecimal vlrParcelaAtualizada;
	private boolean parcelaPaga;
	private boolean parcelaVencida;
	private boolean parcelaVencendo;
	private BigDecimal vlrSaldoInicial;
	private BigDecimal vlrSaldoParcela;
	private BigDecimal vlrParcelaOriginal;
	
	private String origemBaixa;

	private BigDecimal valorJurosSemIPCA;
	private BigDecimal valorAmortizacaoSemIPCA;
	
	private BigDecimal vlrRepasse;
	private BigDecimal vlrRetencao;
	
	private BigDecimal vlrComissao;
	private BigDecimal vlrRecebido;
	
	//com qual ipca esta atualizado a parcela
	private IPCA ipcaAtualizou;
	//valor do ipca atualizado
	private BigDecimal ipca;
	
	private BigDecimal vlrJurosParcela;
	private BigDecimal vlrAmortizacaoParcela;
	
	private List<ContratoCobrancaFavorecidos> listContratoCobrancaFavorecidos;
	
	private List<ContratoCobrancaDetalhesParcial> listContratoCobrancaDetalhesParcial;
	
	private String idFaturaIugu;
	
	private boolean geraSplitterIugu;
	private boolean fezTransferenciaIugu;
		
	private PagadorRecebedor cedenteIugu;
	
	private String secureURLIugu;
	
	private List<ContratoCobrancaDetalhesObservacoes> listContratoCobrancaDetalhesObservacoes;
	
	private boolean baixadoParaInvestidor;
	
	private Date dataUltimoPagamento;
	private BigDecimal valorTotalPagamento;
	
	private BigDecimal seguroDFI;
	private BigDecimal seguroMIP;
	
	private BigDecimal taxaAdm;
	
	private String urlBoletoKonana;
	private String statusBoletoKonana;
	
	private boolean pagoParcial = false;
	
	/**
	 * Não persistido
	 */
	private long idContrato;
	
	private ContratoCobranca contrato;
	
	private int qtdParcelasVencidas;
	
	public ContratoCobrancaDetalhes(){
		this.listContratoCobrancaFavorecidos = new ArrayList<ContratoCobrancaFavorecidos>();
		this.listContratoCobrancaDetalhesParcial = new ArrayList<ContratoCobrancaDetalhesParcial>();
	}
	
	
	
	public ContratoCobrancaDetalhes(ContratoCobrancaDetalhes parcela) {
		this.listContratoCobrancaFavorecidos = new ArrayList<ContratoCobrancaFavorecidos>();
		this.listContratoCobrancaDetalhesParcial = new ArrayList<ContratoCobrancaDetalhesParcial>();
		this.parcelaPaga = parcela.isParcelaPaga();
		this.numeroParcela = parcela.getNumeroParcela();
		this.dataVencimento = parcela.getDataVencimento();
		this.vlrParcela = parcela.vlrParcela;
		this.vlrJurosParcela = parcela.getVlrJurosParcela();
		this.vlrAmortizacaoParcela = parcela.getVlrAmortizacaoParcela();
		this.seguroDFI = parcela.getSeguroDFI();
		this.seguroMIP = parcela.getSeguroMIP();
		this.taxaAdm = parcela.getTaxaAdm();
		this.vlrSaldoInicial = parcela.getVlrSaldoInicial();
		this.dataUltimoPagamento = parcela.getDataUltimoPagamento();
		this.valorTotalPagamento = parcela.getValorTotalPagamento();
		this.vlrSaldoParcela = parcela.getVlrSaldoParcela();
	}



	public boolean isAmortizacao() {
		return CommonsUtil.mesmoValor("Amortização", this.getNumeroParcela());
	}
	
	public boolean isAcertoSaldo() {
		return CommonsUtil.mesmoValor("Acerto Saldo", this.getNumeroParcela());
	}
	
	public void atualizaDadosComParcelaSimulador(SimulacaoDetalheVO parcela) {
		setVlrParcela(parcela.getValorParcela().setScale(2, BigDecimal.ROUND_HALF_EVEN));
		setVlrJurosParcela(parcela.getJuros().setScale(2, BigDecimal.ROUND_HALF_EVEN));
		setVlrAmortizacaoParcela(parcela.getAmortizacao().setScale(2, BigDecimal.ROUND_HALF_EVEN));
		setSeguroDFI(parcela.getSeguroDFI());
		setSeguroMIP(parcela.getSeguroMIP());
		setTaxaAdm(parcela.getTxAdm());
		if (parcela.getValorParcela().compareTo(BigDecimal.ZERO) == 0) {
			setParcelaPaga(true);
			setOrigemBaixa("concluirReparcelamento");
			setDataPagamento(getDataVencimento());
			setVlrParcela(BigDecimal.ZERO);
		}
		
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
	 * @return the numeroParcela
	 */
	public String getNumeroParcela() {
		return numeroParcela;
	}

	/**
	 * @param numeroParcela the numeroParcela to set
	 */
	public void setNumeroParcela(String numeroParcela) {
		this.numeroParcela = numeroParcela;
	}

	/**
	 * @return the dataVencimento
	 */
	public Date getDataVencimento() {
		return dataVencimento;
	}

	/**
	 * @param dataVencimento the dataVencimento to set
	 */
	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	/**
	 * @return the dataPagamento
	 */
	public Date getDataPagamento() {
		return dataPagamento;
	}

	/**
	 * @param dataPagamento the dataPagamento to set
	 */
	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	/**
	 * @return the vlrParcela
	 */
	public BigDecimal getVlrParcela() {
		return vlrParcela;
	}

	/**
	 * @param vlrParcela the vlrParcela to set
	 */
	public void setVlrParcela(BigDecimal vlrParcela) {
		this.vlrParcela = vlrParcela;
	}

	/**
	 * @return the vlrJuros
	 */
	public BigDecimal getVlrJuros() {
		return vlrJuros;
	}

	/**
	 * @param vlrJuros the vlrJuros to set
	 */
	public void setVlrJuros(BigDecimal vlrJuros) {
		this.vlrJuros = vlrJuros;
	}

	/**
	 * @return the vlrParcelaAtualizada
	 */
	public BigDecimal getVlrParcelaAtualizada() {
		return vlrParcelaAtualizada;
	}

	/**
	 * @param vlrParcelaAtualizada the vlrParcelaAtualizada to set
	 */
	public void setVlrParcelaAtualizada(BigDecimal vlrParcelaAtualizada) {
		this.vlrParcelaAtualizada = vlrParcelaAtualizada;
	}

	/**
	 * @return the parcelaPaga
	 */
	public boolean isParcelaPaga() {
		return parcelaPaga;
	}

	/**
	 * @param parcelaPaga the parcelaPaga to set
	 */
	public void setParcelaPaga(boolean parcelaPaga) {
		this.parcelaPaga = parcelaPaga;
	}

	/**
	 * @return the parcelaVencida
	 */
	public boolean isParcelaVencida() {
		return parcelaVencida;
	}

	/**
	 * @param parcelaVencida the parcelaVencida to set
	 */
	public void setParcelaVencida(boolean parcelaVencida) {
		this.parcelaVencida = parcelaVencida;
	}

	/**
	 * @return the parcelaVencendo
	 */
	public boolean isParcelaVencendo() {
		return parcelaVencendo;
	}

	/**
	 * @param parcelaVencendo the parcelaVencendo to set
	 */
	public void setParcelaVencendo(boolean parcelaVencendo) {
		this.parcelaVencendo = parcelaVencendo;
	}

	/**
	 * @return the vlrSaldoParcela
	 */
	public BigDecimal getVlrSaldoParcela() {
		return vlrSaldoParcela;
	}

	/**
	 * @param vlrSaldoParcela the vlrSaldoParcela to set
	 */
	public void setVlrSaldoParcela(BigDecimal vlrSaldoParcela) {
		this.vlrSaldoParcela = vlrSaldoParcela;
	}

	public BigDecimal getVlrSaldoInicial() {
		return vlrSaldoInicial;
	}

	public void setVlrSaldoInicial(BigDecimal vlrSaldoInicial) {
		this.vlrSaldoInicial = vlrSaldoInicial;
	}

	/**
	 * @return the listContratoCobrancaFavorecidos
	 */
	public List<ContratoCobrancaFavorecidos> getListContratoCobrancaFavorecidos() {
		return listContratoCobrancaFavorecidos;
	}

	/**
	 * @param listContratoCobrancaFavorecidos the listContratoCobrancaFavorecidos to set
	 */
	public void setListContratoCobrancaFavorecidos(
			List<ContratoCobrancaFavorecidos> listContratoCobrancaFavorecidos) {
		this.listContratoCobrancaFavorecidos = listContratoCobrancaFavorecidos;
	}

	/**
	 * @return the vlrRepasse
	 */
	public BigDecimal getVlrRepasse() {
		return vlrRepasse;
	}

	/**
	 * @param vlrRepasse the vlrRepasse to set
	 */
	public void setVlrRepasse(BigDecimal vlrRepasse) {
		this.vlrRepasse = vlrRepasse;
	}

	/**
	 * @return the vlrRetencao
	 */
	public BigDecimal getVlrRetencao() {
		return vlrRetencao;
	}

	/**
	 * @param vlrRetencao the vlrRetencao to set
	 */
	public void setVlrRetencao(BigDecimal vlrRetencao) {
		this.vlrRetencao = vlrRetencao;
	}

	/**
	 * @return the vlrComissao
	 */
	public BigDecimal getVlrComissao() {
		return vlrComissao;
	}

	/**
	 * @param vlrComissao the vlrComissao to set
	 */
	public void setVlrComissao(BigDecimal vlrComissao) {
		this.vlrComissao = vlrComissao;
	}

	/**
	 * @return the vlrRecebido
	 */
	public BigDecimal getVlrRecebido() {
		return vlrRecebido;
	}

	/**
	 * @param vlrRecebido the vlrRecebido to set
	 */
	public void setVlrRecebido(BigDecimal vlrRecebido) {
		this.vlrRecebido = vlrRecebido;
	}

	/**
	 * @return the listContratoCobrancaDetalhesParcial
	 */
	public List<ContratoCobrancaDetalhesParcial> getListContratoCobrancaDetalhesParcial() {
		return listContratoCobrancaDetalhesParcial;
	}

	/**
	 * @param listContratoCobrancaDetalhesParcial the listContratoCobrancaDetalhesParcial to set
	 */
	public void setListContratoCobrancaDetalhesParcial(
			List<ContratoCobrancaDetalhesParcial> listContratoCobrancaDetalhesParcial) {
		this.listContratoCobrancaDetalhesParcial = listContratoCobrancaDetalhesParcial;
	}

	/**
	 * @return the dataVencimentoAtual
	 */
	public Date getDataVencimentoAtual() {
		return dataVencimentoAtual;
	}

	/**
	 * @param dataVencimentoAtual the dataVencimentoAtual to set
	 */
	public void setDataVencimentoAtual(Date dataVencimentoAtual) {
		this.dataVencimentoAtual = dataVencimentoAtual;
	}

	/**
	 * @return the idFaturaIugu
	 */
	public String getIdFaturaIugu() {
		return idFaturaIugu;
	}

	/**
	 * @param idFaturaIugu the idFaturaIugu to set
	 */
	public void setIdFaturaIugu(String idFaturaIugu) {
		this.idFaturaIugu = idFaturaIugu;
	}

	/**
	 * @return the geraSplitterIugu
	 */
	public boolean isGeraSplitterIugu() {
		return geraSplitterIugu;
	}

	/**
	 * @param geraSplitterIugu the geraSplitterIugu to set
	 */
	public void setGeraSplitterIugu(boolean geraSplitterIugu) {
		this.geraSplitterIugu = geraSplitterIugu;
	}

	/**
	 * @return the fezTransferenciaIugu
	 */
	public boolean isFezTransferenciaIugu() {
		return fezTransferenciaIugu;
	}

	/**
	 * @param fezTransferenciaIugu the fezTransferenciaIugu to set
	 */
	public void setFezTransferenciaIugu(boolean fezTransferenciaIugu) {
		this.fezTransferenciaIugu = fezTransferenciaIugu;
	}

	/**
	 * @return the cedenteIugu
	 */
	public PagadorRecebedor getCedenteIugu() {
		return cedenteIugu;
	}

	/**
	 * @param cedenteIugu the cedenteIugu to set
	 */
	public void setCedenteIugu(PagadorRecebedor cedenteIugu) {
		this.cedenteIugu = cedenteIugu;
	}

	/**
	 * @return the secureURLIugu
	 */
	public String getSecureURLIugu() {
		return secureURLIugu;
	}

	/**
	 * @param secureURLIugu the secureURLIugu to set
	 */
	public void setSecureURLIugu(String secureURLIugu) {
		this.secureURLIugu = secureURLIugu;
	}

	/**
	 * @return the listContratoCobrancaDetalhesObservacoes
	 */
	public List<ContratoCobrancaDetalhesObservacoes> getListContratoCobrancaDetalhesObservacoes() {
		return listContratoCobrancaDetalhesObservacoes;
	}

	/**
	 * @param listContratoCobrancaDetalhesObservacoes the listContratoCobrancaDetalhesObservacoes to set
	 */
	public void setListContratoCobrancaDetalhesObservacoes(
			List<ContratoCobrancaDetalhesObservacoes> listContratoCobrancaDetalhesObservacoes) {
		this.listContratoCobrancaDetalhesObservacoes = listContratoCobrancaDetalhesObservacoes;
	}

	public Date getPromessaPagamento() {
		return promessaPagamento;
	}

	public void setPromessaPagamento(Date promessaPagamento) {
		this.promessaPagamento = promessaPagamento;
	}

	public boolean isBaixadoParaInvestidor() {
		return baixadoParaInvestidor;
	}

	public void setBaixadoParaInvestidor(boolean baixadoParaInvestidor) {
		this.baixadoParaInvestidor = baixadoParaInvestidor;
	}

	public BigDecimal getTxMulta() {
		return txMulta;
	}

	public void setTxMulta(BigDecimal txMulta) {
		this.txMulta = txMulta;
	}

	public BigDecimal getVlrJurosParcela() {
		return vlrJurosParcela;
	}

	public void setVlrJurosParcela(BigDecimal vlrJurosParcela) {
		this.vlrJurosParcela = vlrJurosParcela;
	}

	public BigDecimal getVlrAmortizacaoParcela() {
		return vlrAmortizacaoParcela;
	}

	public void setVlrAmortizacaoParcela(BigDecimal vlrAmortizacaoParcela) {
		this.vlrAmortizacaoParcela = vlrAmortizacaoParcela;
	}

	public Date getDataUltimoPagamento() {
		return dataUltimoPagamento;
	}

	public void setDataUltimoPagamento(Date dataUltimoPagamento) {
		this.dataUltimoPagamento = dataUltimoPagamento;
	}

	public BigDecimal getValorTotalPagamento() {
		return valorTotalPagamento;
	}

	public void setValorTotalPagamento(BigDecimal valorTotalPagamento) {
		this.valorTotalPagamento = valorTotalPagamento;
	}

	public IPCA getIpcaAtualizou() {
		return ipcaAtualizou;
	}

	public void setIpcaAtualizou(IPCA ipcaAtualizou) {
		this.ipcaAtualizou = ipcaAtualizou;
	}

	public BigDecimal getIpca() {
		return ipca;
	}

	public void setIpca(BigDecimal ipca) {
		this.ipca = ipca;
	}

	public long getIdContrato() {
		return idContrato;
	}

	public void setIdContrato(long idContrato) {
		this.idContrato = idContrato;
	}

	public BigDecimal getSeguroDFI() {
		return seguroDFI;
	}

	public void setSeguroDFI(BigDecimal seguroDFI) {
		this.seguroDFI = seguroDFI;
	}

	public BigDecimal getSeguroMIP() {
		return seguroMIP;
	}

	public void setSeguroMIP(BigDecimal seguroMIP) {
		this.seguroMIP = seguroMIP;
	}

	public BigDecimal getVlrParcelaOriginal() {
		return vlrParcelaOriginal;
	}

	public void setVlrParcelaOriginal(BigDecimal vlrParcelaOriginal) {
		this.vlrParcelaOriginal = vlrParcelaOriginal;
	}

	public BigDecimal getValorJurosSemIPCA() {
		return valorJurosSemIPCA;
	}

	public void setValorJurosSemIPCA(BigDecimal valorJurosSemIPCA) {
		this.valorJurosSemIPCA = valorJurosSemIPCA;
	}

	public BigDecimal getValorAmortizacaoSemIPCA() {
		return valorAmortizacaoSemIPCA;
	}

	public void setValorAmortizacaoSemIPCA(BigDecimal valorAmortizacaoSemIPCA) {
		this.valorAmortizacaoSemIPCA = valorAmortizacaoSemIPCA;
	}

	public String getUrlBoletoKonana() {
		return urlBoletoKonana;
	}

	public void setUrlBoletoKonana(String urlBoletoKonana) {
		this.urlBoletoKonana = urlBoletoKonana;
	}

	public String getStatusBoletoKonana() {
		return statusBoletoKonana;
	}

	public void setStatusBoletoKonana(String statusBoletoKonana) {
		this.statusBoletoKonana = statusBoletoKonana;
	}
	
	public BigDecimal getTaxaAdm() {
		return taxaAdm;
	}
	
	public void setTaxaAdm(BigDecimal taxaAdm) {
		this.taxaAdm = taxaAdm;
	}

	public ContratoCobranca getContrato() {
		return contrato;
	}

	public void setContrato(ContratoCobranca contrato) {
		this.contrato = contrato;
	}

	public int getQtdParcelasVencidas() {
		return qtdParcelasVencidas;
	}

	public void setQtdParcelasVencidas(int qtdParcelasVencidas) {
		this.qtdParcelasVencidas = qtdParcelasVencidas;
	}

	public boolean isPagoParcial() {
		return pagoParcial;
	}

	public void setPagoParcial(boolean pagoParcial) {
		this.pagoParcial = pagoParcial;
	}

	public String getOrigemBaixa() {
		return origemBaixa;
	}

	public void setOrigemBaixa(String origemBaixa) {
		this.origemBaixa = origemBaixa;
	}

	public BigDecimal getVlrBoletoKobana() {
		return vlrBoletoKobana;
	}

	public void setVlrBoletoKobana(BigDecimal vlrBoletoKobana) {
		this.vlrBoletoKobana = vlrBoletoKobana;
	}	
}
