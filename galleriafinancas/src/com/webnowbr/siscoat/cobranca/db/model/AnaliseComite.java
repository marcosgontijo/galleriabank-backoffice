package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.simulador.GoalSeek;
import com.webnowbr.siscoat.simulador.GoalSeekFunction;
import com.webnowbr.siscoat.simulador.SimulacaoDetalheVO;
import com.webnowbr.siscoat.simulador.SimulacaoVO;

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
	
	private BigDecimal vlrParcela = BigDecimal.ZERO;
	private String tipoPessoa = "";
	private String tipoImovel = "";
	private String tipoOp = "";
	private BigDecimal vlrImovel = BigDecimal.ZERO;
	
	private ContratoCobranca contratoCobranca;
	
	public void calcularValorParcela() {
		SimulacaoVO simulador = new SimulacaoVO();	
		BigDecimal tarifaIOFDiario = BigDecimal.ZERO;
		BigDecimal tarifaIOFAdicional = SiscoatConstants.TARIFA_IOF_ADICIONAL.divide(BigDecimal.valueOf(100));
	
		simulador.setTipoPessoa(tipoPessoa);
		
		List<String> imoveis = Arrays.asList("Apartamento", "Casa", "Casa de Condomínio", "Terreno");
		
		if(!CommonsUtil.mesmoValor(tipoPessoa, "PF") &&
				imoveis.contains(tipoImovel)
				&& CommonsUtil.mesmoValor(tipoOp, "Emprestimo")) {
			tarifaIOFDiario = BigDecimal.ZERO;
			tarifaIOFAdicional = BigDecimal.ZERO;
		} else {
			if (CommonsUtil.mesmoValor(tipoPessoa, "PF")) {		
				tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PF.divide(BigDecimal.valueOf(100));		
			} else {		
				tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PJ.divide(BigDecimal.valueOf(100));		
			}
		}
		
		BigDecimal custoEmissaoValor = SiscoatConstants.CUSTO_EMISSAO_MINIMO;
		
		final BigDecimal custoEmissaoPercentual;
		custoEmissaoPercentual = SiscoatConstants.CUSTO_EMISSAO_PERCENTUAL_BRUTO_NOVO;

		if (valorComite.multiply(custoEmissaoPercentual.divide(BigDecimal.valueOf(100)))
				.compareTo(SiscoatConstants.CUSTO_EMISSAO_MINIMO) > 0) {
			custoEmissaoValor = valorComite.multiply(custoEmissaoPercentual.divide(BigDecimal.valueOf(100)));
		}
		
		simulador.setDataSimulacao(new Date());
		simulador.setTarifaIOFDiario(tarifaIOFDiario);
		simulador.setTarifaIOFAdicional(tarifaIOFAdicional);
		simulador.setSeguroMIP(SiscoatConstants.SEGURO_MIP);
		simulador.setSeguroDFI(SiscoatConstants.SEGURO_DFI);
		simulador.setValorCredito(valorComite);
		simulador.setTaxaJuros(taxaComite);
		simulador.setCarencia(BigInteger.valueOf(carenciaComite));
		simulador.setQtdParcelas(prazoMaxComite);
		simulador.setValorImovel(vlrImovel);
		simulador.setCustoEmissaoValor(custoEmissaoValor);
		simulador.setCustoEmissaoPercentual(custoEmissaoPercentual);
		simulador.setTipoCalculo("Price");
		simulador.setNaoCalcularDFI(false);
		simulador.setNaoCalcularMIP(false);
		simulador.setNaoCalcularTxAdm(false);
		if (CommonsUtil.mesmoValor("liquido", tipoValorComite)) {
			GoalSeek goalSeek = new GoalSeek(CommonsUtil.doubleValue(simulador.getValorCredito()), 
					CommonsUtil.doubleValue(simulador.getValorCredito().divide(BigDecimal.valueOf(1.5), MathContext.DECIMAL128)),
					CommonsUtil.doubleValue(simulador.getValorCredito().multiply(BigDecimal.valueOf(1.5), MathContext.DECIMAL128)));		
			GoalSeekFunction gsFunfction = new GoalSeekFunction();
			BigDecimal valorBruto = CommonsUtil.bigDecimalValue(gsFunfction.getGoalSeek(goalSeek, simulador));
			simulador.setValorCredito(valorBruto.setScale(2, RoundingMode.HALF_UP));
		} else {
			simulador.calcular();
		}
		
		for(SimulacaoDetalheVO parcela : simulador.getParcelas()) {
			if(!CommonsUtil.semValor(parcela.getAmortizacao().add(parcela.getJuros()))) {
				vlrParcela = parcela.getAmortizacao().add(parcela.getJuros());
				vlrParcela = vlrParcela.setScale(2, RoundingMode.HALF_UP);
				break;
			}		
		}
	}

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

	public BigDecimal getVlrParcela() {
		return vlrParcela;
	}

	public void setVlrParcela(BigDecimal vlrParcela) {
		this.vlrParcela = vlrParcela;
	}

	public String getTipoPessoa() {
		return tipoPessoa;
	}

	public void setTipoPessoa(String tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}

	public String getTipoImovel() {
		return tipoImovel;
	}

	public void setTipoImovel(String tipoImovel) {
		this.tipoImovel = tipoImovel;
	}

	public String getTipoOp() {
		return tipoOp;
	}

	public void setTipoOp(String tipoOp) {
		this.tipoOp = tipoOp;
	}

	public BigDecimal getVlrImovel() {
		return vlrImovel;
	}

	public void setVlrImovel(BigDecimal vlrImovel) {
		this.vlrImovel = vlrImovel;
	}
}
