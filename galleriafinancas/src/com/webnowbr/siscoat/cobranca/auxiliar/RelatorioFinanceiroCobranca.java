package com.webnowbr.siscoat.cobranca.auxiliar;

import java.math.BigDecimal;
import java.util.Date;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;

public class RelatorioFinanceiroCobranca {
	
	public String numeroContrato;
	public Date dataContrato;
	public String nomeResponsavel;
	public String nomePagador;
	public String nomeRecebedor;
	
	public String parcela;
	public String parcelaCCB;
	public Date dataVencimento;
	public Date dataVencimentoAtual;
	public BigDecimal valor;
	public BigDecimal valorRetencao;
	public BigDecimal valorComissao;
	private BigDecimal vlrRepasse;
	private BigDecimal vlrJurosParcela;	
	private BigDecimal vlrAmortizacaoParcela;
	public Date dataUltimoPagamento;
	private BigDecimal vlrTotalPago;
	
	public String ccb;
	public String ccbParcela;
	
	private BigDecimal valorCCB;
	
	private boolean cartorio = false;
	
	public ContratoCobranca contratoCobranca;
	
	public Date dataPagamento;
	
	private boolean parcelaPaga;
	private String parcelaPagaStr;	
	
	private BigDecimal vlrParcelaAtualizada;
	private BigDecimal vlrParcela;
	private BigDecimal acrescimo;
	
	private PagadorRecebedor recebedor1;
	private PagadorRecebedor recebedor2;
	private PagadorRecebedor recebedor3;
	private PagadorRecebedor recebedor4;
	private PagadorRecebedor recebedor5;
	private PagadorRecebedor recebedor6;
	private PagadorRecebedor recebedor7;
	private PagadorRecebedor recebedor8;
	private PagadorRecebedor recebedor9;
	private PagadorRecebedor recebedor10;
	
	private BigDecimal vlrRecebedor1;
	private BigDecimal vlrRecebedor2;
	private BigDecimal vlrRecebedor3;
	private BigDecimal vlrRecebedor4;
	private BigDecimal vlrRecebedor5;
	private BigDecimal vlrRecebedor6;
	private BigDecimal vlrRecebedor7;
	private BigDecimal vlrRecebedor8;
	private BigDecimal vlrRecebedor9;
	private BigDecimal vlrRecebedor10;	
	
	private String qtdeAtrasos;
	private String qtdeBaixasParciais;
	
	private long idParcela;
	
	public RelatorioFinanceiroCobranca() {
		
	}
	
	public RelatorioFinanceiroCobranca(ContratoCobranca contratoCobranca) {
		this.contratoCobranca = contratoCobranca;
		}
	
	public RelatorioFinanceiroCobranca(String qtdeAtrasos) {
		this.qtdeAtrasos = qtdeAtrasos;
		}
	
	public RelatorioFinanceiroCobranca(String numeroContrato, Date dataContrato, String nomeResponsavel, String nomePagador, String nomeRecebedor, String parcela,
										Date dataVencimento, BigDecimal valor, ContratoCobranca contratoCobranca, BigDecimal valorRetencao, BigDecimal valorComissao, boolean parcelaPaga, Date dataVencimentoAtual, BigDecimal vlrRepasse) {
		this.numeroContrato = numeroContrato;
		this.dataContrato = dataContrato;
		this.nomeResponsavel = nomeResponsavel;
		this.nomePagador = nomePagador;
		this.nomeRecebedor = nomeRecebedor;
		this.parcela = parcela;
		this.dataVencimento = dataVencimento;
		this.valor = valor;
		this.contratoCobranca = contratoCobranca;
		this.valorRetencao = valorRetencao;
		this.valorComissao = valorComissao;
		this.parcelaPaga = parcelaPaga;
		this.dataVencimentoAtual = dataVencimentoAtual;
		this.vlrRepasse = vlrRepasse;		
	}
	
	public RelatorioFinanceiroCobranca(String numeroContrato, Date dataContrato, String nomeResponsavel, String nomePagador, String nomeRecebedor, String parcela,
			Date dataVencimento, BigDecimal valor, ContratoCobranca contratoCobranca, BigDecimal valorRetencao, BigDecimal valorComissao, boolean parcelaPaga, Date dataVencimentoAtual, long idParcela, BigDecimal vlrRepasse, String ccb) {
		this.numeroContrato = numeroContrato;
		this.dataContrato = dataContrato;
		this.nomeResponsavel = nomeResponsavel;
		this.nomePagador = nomePagador;
		this.nomeRecebedor = nomeRecebedor;
		this.parcela = parcela;
		this.dataVencimento = dataVencimento;
		this.valor = valor;
		this.contratoCobranca = contratoCobranca;
		this.valorRetencao = valorRetencao;
		this.valorComissao = valorComissao;
		this.parcelaPaga = parcelaPaga;
		this.dataVencimentoAtual = dataVencimentoAtual;
		this.idParcela = idParcela;
		this.vlrRepasse = vlrRepasse;
		this.ccb = ccb;	
		}
	
	public RelatorioFinanceiroCobranca(String numeroContrato, Date dataContrato, String nomeResponsavel, String nomePagador, String nomeRecebedor, String parcela,
			Date dataVencimento, BigDecimal valor, ContratoCobranca contratoCobranca, BigDecimal valorRetencao, BigDecimal valorComissao, boolean parcelaPaga, Date dataVencimentoAtual, long idParcela, BigDecimal vlrRepasse) {
		this.numeroContrato = numeroContrato;
		this.dataContrato = dataContrato;
		this.nomeResponsavel = nomeResponsavel;
		this.nomePagador = nomePagador;
		this.nomeRecebedor = nomeRecebedor;
		this.parcela = parcela;
		this.dataVencimento = dataVencimento;
		this.valor = valor;
		this.contratoCobranca = contratoCobranca;
		this.valorRetencao = valorRetencao;
		this.valorComissao = valorComissao;
		this.parcelaPaga = parcelaPaga;
		this.dataVencimentoAtual = dataVencimentoAtual;
		this.idParcela = idParcela;
		this.vlrRepasse = vlrRepasse;
		}
	
	public RelatorioFinanceiroCobranca(String numeroContrato, Date dataContrato, String nomeResponsavel, String nomePagador, String nomeRecebedor, String parcela,
			Date dataVencimento, BigDecimal valor, ContratoCobranca contratoCobranca, BigDecimal valorRetencao, BigDecimal valorComissao, Date dataPagamento, boolean parcelaPaga, Date dataVencimentoAtual, BigDecimal vlrRepasse) {
		this.numeroContrato = numeroContrato;
		this.dataContrato = dataContrato;
		this.nomeResponsavel = nomeResponsavel;
		this.nomePagador = nomePagador;
		this.nomeRecebedor = nomeRecebedor;
		this.parcela = parcela;
		this.dataVencimento = dataVencimento;
		this.valor = valor;
		this.contratoCobranca = contratoCobranca;
		this.valorRetencao = valorRetencao;
		this.valorComissao = valorComissao;
		this.dataPagamento = dataPagamento;
		this.parcelaPaga = parcelaPaga;
		this.dataVencimentoAtual = dataVencimentoAtual;
		this.vlrRepasse = vlrRepasse;	
	}
	
	public RelatorioFinanceiroCobranca(String numeroContrato, String parcela,
			Date dataPagamento, BigDecimal vlrRecebedor1, PagadorRecebedor recebedor1, ContratoCobranca contratoCobranca) {
		this.numeroContrato = numeroContrato;
		this.parcela = parcela;
		this.dataVencimento = dataPagamento;
		this.dataPagamento = dataPagamento;
		this.vlrRecebedor1 = vlrRecebedor1;
		this.recebedor1 = recebedor1;
		this.contratoCobranca = contratoCobranca;
	}
	
	public RelatorioFinanceiroCobranca(String numeroContrato, Date dataContrato, String nomeResponsavel, String nomePagador, String nomeRecebedor, String parcela,
			Date dataVencimento, BigDecimal valor, ContratoCobranca contratoCobranca, BigDecimal valorRetencao, BigDecimal valorComissao, boolean parcelaPaga, Date dataVencimentoAtual, BigDecimal vlrRepasse, BigDecimal vlrParcela, BigDecimal acrescimo, String parcelaPagaStr) {
		this.numeroContrato = numeroContrato;
		this.dataContrato = dataContrato;
		this.nomeResponsavel = nomeResponsavel;
		this.nomePagador = nomePagador;
		this.nomeRecebedor = nomeRecebedor;
		this.parcela = parcela;
		this.dataVencimento = dataVencimento;
		this.valor = valor;
		this.contratoCobranca = contratoCobranca;
		this.valorRetencao = valorRetencao;
		this.valorComissao = valorComissao;
		this.parcelaPaga = parcelaPaga;
		this.dataVencimentoAtual = dataVencimentoAtual;
		this.vlrRepasse = vlrRepasse;		
		this.vlrParcela = vlrParcela;
		this.acrescimo = acrescimo;
		this.parcelaPagaStr = parcelaPagaStr;
	}
	
	public RelatorioFinanceiroCobranca(String numeroContrato, Date dataContrato, String nomeResponsavel, String nomePagador, String nomeRecebedor, String parcela,
			Date dataVencimento, BigDecimal valor, ContratoCobranca contratoCobranca, BigDecimal valorRetencao, BigDecimal valorComissao, boolean parcelaPaga, Date dataVencimentoAtual, BigDecimal vlrRepasse, BigDecimal vlrParcela, BigDecimal acrescimo) {
		this.numeroContrato = numeroContrato;
		this.dataContrato = dataContrato;
		this.nomeResponsavel = nomeResponsavel;
		this.nomePagador = nomePagador;
		this.nomeRecebedor = nomeRecebedor;
		this.parcela = parcela;
		this.dataVencimento = dataVencimento;
		this.valor = valor;
		this.contratoCobranca = contratoCobranca;
		this.valorRetencao = valorRetencao;
		this.valorComissao = valorComissao;
		this.parcelaPaga = parcelaPaga;
		this.dataVencimentoAtual = dataVencimentoAtual;
		this.vlrRepasse = vlrRepasse;		
		this.vlrParcela = vlrParcela;
		this.acrescimo = acrescimo;
	}
	
	public RelatorioFinanceiroCobranca(String numeroContrato, String nomePagador, String parcela, Date dataVencimento, BigDecimal vlrParcela, Date dataUltimoPagamento, BigDecimal vlrTotalPago,
			BigDecimal vlrJurosParcela, BigDecimal vlrAmortizacaoParcela) {
		this.numeroContrato = numeroContrato;
		this.nomePagador = nomePagador;
		this.dataVencimento = dataVencimento;
		this.vlrParcela = vlrParcela;
		this.dataUltimoPagamento = dataUltimoPagamento;
		this.vlrTotalPago = vlrTotalPago;
		this.vlrJurosParcela = vlrJurosParcela;
		this.vlrAmortizacaoParcela = vlrAmortizacaoParcela;
		this.parcela = parcela;
	}
	
	/**
	 * @return the numeroContrato
	 */
	public String getNumeroContrato() {
		return numeroContrato;
	}
	/**
	 * @param numeroContrato the numeroContrato to set
	 */
	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}
	/**
	 * @return the dataContrato
	 */
	public Date getDataContrato() {
		return dataContrato;
	}
	/**
	 * @param dataContrato the dataContrato to set
	 */
	public void setDataContrato(Date dataContrato) {
		this.dataContrato = dataContrato;
	}
	/**
	 * @return the nomeResponsavel
	 */
	public String getNomeResponsavel() {
		return nomeResponsavel;
	}
	/**
	 * @param nomeResponsavel the nomeResponsavel to set
	 */
	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}
	/**
	 * @return the nomePagador
	 */
	public String getNomePagador() {
		return nomePagador;
	}
	/**
	 * @param nomePagador the nomePagador to set
	 */
	public void setNomePagador(String nomePagador) {
		this.nomePagador = nomePagador;
	}
	/**
	 * @return the nomeRecebedor
	 */
	public String getNomeRecebedor() {
		return nomeRecebedor;
	}
	/**
	 * @param nomeRecebedor the nomeRecebedor to set
	 */
	public void setNomeRecebedor(String nomeRecebedor) {
		this.nomeRecebedor = nomeRecebedor;
	}
	/**
	 * @return the parcela
	 */
	public String getParcela() {
		return parcela;
	}
	/**
	 * @param parcela the parcela to set
	 */
	public void setParcela(String parcela) {
		this.parcela = parcela;
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
	 * @return the valor
	 */
	public BigDecimal getValor() {
		return valor;
	}
	/**
	 * @param valor the valor to set
	 */
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	/**
	 * @return the contratoCobranca
	 */
	public ContratoCobranca getContratoCobranca() {
		return contratoCobranca;
	}

	/**
	 * @param contratoCobranca the contratoCobranca to set
	 */
	public void setContratoCobranca(ContratoCobranca contratoCobranca) {
		this.contratoCobranca = contratoCobranca;
	}

	/**
	 * @return the valorRetencao
	 */
	public BigDecimal getValorRetencao() {
		return valorRetencao;
	}

	/**
	 * @param valorRetencao the valorRetencao to set
	 */
	public void setValorRetencao(BigDecimal valorRetencao) {
		this.valorRetencao = valorRetencao;
	}

	/**
	 * @return the valorComissao
	 */
	public BigDecimal getValorComissao() {
		return valorComissao;
	}

	/**
	 * @param valorComissao the valorComissao to set
	 */
	public void setValorComissao(BigDecimal valorComissao) {
		this.valorComissao = valorComissao;
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
	 * @return the recebedor1
	 */
	public PagadorRecebedor getRecebedor1() {
		return recebedor1;
	}

	/**
	 * @param recebedor1 the recebedor1 to set
	 */
	public void setRecebedor1(PagadorRecebedor recebedor1) {
		this.recebedor1 = recebedor1;
	}

	/**
	 * @return the recebedor2
	 */
	public PagadorRecebedor getRecebedor2() {
		return recebedor2;
	}

	/**
	 * @param recebedor2 the recebedor2 to set
	 */
	public void setRecebedor2(PagadorRecebedor recebedor2) {
		this.recebedor2 = recebedor2;
	}

	/**
	 * @return the recebedor3
	 */
	public PagadorRecebedor getRecebedor3() {
		return recebedor3;
	}

	/**
	 * @param recebedor3 the recebedor3 to set
	 */
	public void setRecebedor3(PagadorRecebedor recebedor3) {
		this.recebedor3 = recebedor3;
	}

	/**
	 * @return the recebedor4
	 */
	public PagadorRecebedor getRecebedor4() {
		return recebedor4;
	}

	/**
	 * @param recebedor4 the recebedor4 to set
	 */
	public void setRecebedor4(PagadorRecebedor recebedor4) {
		this.recebedor4 = recebedor4;
	}

	/**
	 * @return the recebedor5
	 */
	public PagadorRecebedor getRecebedor5() {
		return recebedor5;
	}

	/**
	 * @param recebedor5 the recebedor5 to set
	 */
	public void setRecebedor5(PagadorRecebedor recebedor5) {
		this.recebedor5 = recebedor5;
	}

	/**
	 * @return the recebedor6
	 */
	public PagadorRecebedor getRecebedor6() {
		return recebedor6;
	}

	/**
	 * @param recebedor6 the recebedor6 to set
	 */
	public void setRecebedor6(PagadorRecebedor recebedor6) {
		this.recebedor6 = recebedor6;
	}

	/**
	 * @return the recebedor7
	 */
	public PagadorRecebedor getRecebedor7() {
		return recebedor7;
	}

	/**
	 * @param recebedor7 the recebedor7 to set
	 */
	public void setRecebedor7(PagadorRecebedor recebedor7) {
		this.recebedor7 = recebedor7;
	}

	/**
	 * @return the recebedor8
	 */
	public PagadorRecebedor getRecebedor8() {
		return recebedor8;
	}

	/**
	 * @param recebedor8 the recebedor8 to set
	 */
	public void setRecebedor8(PagadorRecebedor recebedor8) {
		this.recebedor8 = recebedor8;
	}

	/**
	 * @return the recebedor9
	 */
	public PagadorRecebedor getRecebedor9() {
		return recebedor9;
	}

	/**
	 * @param recebedor9 the recebedor9 to set
	 */
	public void setRecebedor9(PagadorRecebedor recebedor9) {
		this.recebedor9 = recebedor9;
	}

	/**
	 * @return the recebedor10
	 */
	public PagadorRecebedor getRecebedor10() {
		return recebedor10;
	}

	/**
	 * @param recebedor10 the recebedor10 to set
	 */
	public void setRecebedor10(PagadorRecebedor recebedor10) {
		this.recebedor10 = recebedor10;
	}

	/**
	 * @return the vlrRecebedor1
	 */
	public BigDecimal getVlrRecebedor1() {
		return vlrRecebedor1;
	}

	/**
	 * @param vlrRecebedor1 the vlrRecebedor1 to set
	 */
	public void setVlrRecebedor1(BigDecimal vlrRecebedor1) {
		this.vlrRecebedor1 = vlrRecebedor1;
	}

	/**
	 * @return the vlrRecebedor2
	 */
	public BigDecimal getVlrRecebedor2() {
		return vlrRecebedor2;
	}

	/**
	 * @param vlrRecebedor2 the vlrRecebedor2 to set
	 */
	public void setVlrRecebedor2(BigDecimal vlrRecebedor2) {
		this.vlrRecebedor2 = vlrRecebedor2;
	}

	/**
	 * @return the vlrRecebedor3
	 */
	public BigDecimal getVlrRecebedor3() {
		return vlrRecebedor3;
	}

	/**
	 * @param vlrRecebedor3 the vlrRecebedor3 to set
	 */
	public void setVlrRecebedor3(BigDecimal vlrRecebedor3) {
		this.vlrRecebedor3 = vlrRecebedor3;
	}

	/**
	 * @return the vlrRecebedor4
	 */
	public BigDecimal getVlrRecebedor4() {
		return vlrRecebedor4;
	}

	/**
	 * @param vlrRecebedor4 the vlrRecebedor4 to set
	 */
	public void setVlrRecebedor4(BigDecimal vlrRecebedor4) {
		this.vlrRecebedor4 = vlrRecebedor4;
	}

	/**
	 * @return the vlrRecebedor5
	 */
	public BigDecimal getVlrRecebedor5() {
		return vlrRecebedor5;
	}

	/**
	 * @param vlrRecebedor5 the vlrRecebedor5 to set
	 */
	public void setVlrRecebedor5(BigDecimal vlrRecebedor5) {
		this.vlrRecebedor5 = vlrRecebedor5;
	}

	/**
	 * @return the vlrRecebedor6
	 */
	public BigDecimal getVlrRecebedor6() {
		return vlrRecebedor6;
	}

	/**
	 * @param vlrRecebedor6 the vlrRecebedor6 to set
	 */
	public void setVlrRecebedor6(BigDecimal vlrRecebedor6) {
		this.vlrRecebedor6 = vlrRecebedor6;
	}

	/**
	 * @return the vlrRecebedor7
	 */
	public BigDecimal getVlrRecebedor7() {
		return vlrRecebedor7;
	}

	/**
	 * @param vlrRecebedor7 the vlrRecebedor7 to set
	 */
	public void setVlrRecebedor7(BigDecimal vlrRecebedor7) {
		this.vlrRecebedor7 = vlrRecebedor7;
	}

	/**
	 * @return the vlrRecebedor8
	 */
	public BigDecimal getVlrRecebedor8() {
		return vlrRecebedor8;
	}

	/**
	 * @param vlrRecebedor8 the vlrRecebedor8 to set
	 */
	public void setVlrRecebedor8(BigDecimal vlrRecebedor8) {
		this.vlrRecebedor8 = vlrRecebedor8;
	}

	/**
	 * @return the vlrRecebedor9
	 */
	public BigDecimal getVlrRecebedor9() {
		return vlrRecebedor9;
	}

	/**
	 * @param vlrRecebedor9 the vlrRecebedor9 to set
	 */
	public void setVlrRecebedor9(BigDecimal vlrRecebedor9) {
		this.vlrRecebedor9 = vlrRecebedor9;
	}

	/**
	 * @return the vlrRecebedor10
	 */
	public BigDecimal getVlrRecebedor10() {
		return vlrRecebedor10;
	}

	/**
	 * @param vlrRecebedor10 the vlrRecebedor10 to set
	 */
	public void setVlrRecebedor10(BigDecimal vlrRecebedor10) {
		this.vlrRecebedor10 = vlrRecebedor10;
	}

	public long getIdParcela() {
		return idParcela;
	}

	public void setIdParcela(long idParcela) {
		this.idParcela = idParcela;
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

	public BigDecimal getVlrParcela() {
		return vlrParcela;
	}

	public void setVlrParcela(BigDecimal vlrParcela) {
		this.vlrParcela = vlrParcela;
	}

	public BigDecimal getAcrescimo() {
		return acrescimo;
	}

	public void setAcrescimo(BigDecimal acrescimo) {
		this.acrescimo = acrescimo;
	}

	/**
	 * @return the parcelaPagaStr
	 */
	public String getParcelaPagaStr() {
		return parcelaPagaStr;
	}

	/**
	 * @param parcelaPagaStr the parcelaPagaStr to set
	 */
	public void setParcelaPagaStr(String parcelaPagaStr) {
		this.parcelaPagaStr = parcelaPagaStr;
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

	public BigDecimal getVlrTotalPago() {
		return vlrTotalPago;
	}

	public void setVlrTotalPago(BigDecimal vlrTotalPago) {
		this.vlrTotalPago = vlrTotalPago;
	}

	public String getQtdeAtrasos() {
		return qtdeAtrasos;
	}

	public void setQtdeAtrasos(String qtdeAtrasos) {
		this.qtdeAtrasos = qtdeAtrasos;
	}

	public String getQtdeBaixasParciais() {
		return qtdeBaixasParciais;
	}

	public void setQtdeBaixasParciais(String qtdeBaixasParciais) {
		this.qtdeBaixasParciais = qtdeBaixasParciais;
	}

	public boolean isCartorio() {
		return cartorio;
	}

	public void setCartorio(boolean cartorio) {
		this.cartorio = cartorio;
	}

	public BigDecimal getValorCCB() {
		return valorCCB;
	}

	public void setValorCCB(BigDecimal valorCCB) {
		this.valorCCB = valorCCB;
	}

	public String getParcelaCCB() {
		return parcelaCCB;
	}

	public void setParcelaCCB(String parcelaCCB) {
		this.parcelaCCB = parcelaCCB;
	}

	public String getCcb() {
		return ccb;
	}

	public void setCcb(String ccb) {
		this.ccb = ccb;
	}

	public String getCcbParcela() {
		return ccbParcela;
	}

	public void setCcbParcela(String ccbParcela) {
		this.ccbParcela = ccbParcela;
	}
}
