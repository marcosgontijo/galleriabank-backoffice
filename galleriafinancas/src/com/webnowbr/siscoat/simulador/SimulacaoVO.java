package com.webnowbr.siscoat.simulador;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.formula.functions.FinanceLib;

import com.webnowbr.siscoat.common.DateUtil;

public class SimulacaoVO {

	// parametros
	private BigDecimal tarifaIOFDiario;
	private BigDecimal tarifaIOFAdicional;
	private BigDecimal seguroMIP;
	private BigDecimal seguroDFI;
	private Date dataSimulacao;
	private String tipoPessoa;
	private String tipoCalculo;
	private boolean naoCalcularDFI;
	private boolean naoCalcularMIP;
	private boolean mostrarIPCA;

	// valores
	private BigDecimal valorCredito;
	private BigDecimal valorCreditoLiberado;
	private BigDecimal taxaJuros;
	private BigDecimal taxaJurosAoAno;
	private BigInteger carencia;
	private BigInteger qtdParcelas;
	private BigDecimal valorImovel;
	private BigDecimal custoEmissaoValor;
	private BigDecimal valorSeguroDFI = BigDecimal.ZERO;
	private BigDecimal valorSeguroMIP = BigDecimal.ZERO;

	// totais
	private BigDecimal valorTotalIOF;
	private BigDecimal valorTotalIOFAdicional;
	
	private BigDecimal cetAoMes;
	private BigDecimal cetAoAno;
	private BigDecimal ltv;

	private List<SimulacaoDetalheVO> parcelas = new ArrayList<SimulacaoDetalheVO>();

	public void calcular() {
		if (this.tipoCalculo.equals("Price")) {
			calcularPrice();
		} else if (this.tipoCalculo.equals("SAC")) {
			calcularSac();
		} else if (this.tipoCalculo.equals("Americano")) {
			calcularAmericano();
		} else if (this.tipoCalculo.equals("Envelope")) {
			calcularEnvelope();
		}
	}

	public void calcularPrice() {

		valorTotalIOF = BigDecimal.ZERO;
		valorTotalIOFAdicional = calcularIOFAdicional();

		parcelas = new ArrayList<SimulacaoDetalheVO>();

		BigDecimal saldoDevedorAnterior = this.valorCredito;
		BigDecimal saldoDevedorCarencia = getSaldoDevedorCarencia();

		BigDecimal parcelaPGTO = BigDecimal
				.valueOf(FinanceLib.pmt(this.taxaJuros.divide(BigDecimal.valueOf(100)).doubleValue(), // taxa
						this.qtdParcelas.subtract(carencia).intValue(), // prazo
						saldoDevedorCarencia.negate().doubleValue(), // valor credito - VP
						Double.valueOf("0"), // VF
						false // pagamento no inico
				));

		SimulacaoDetalheVO parcelaCalculo = new SimulacaoDetalheVO(this.valorCredito);
		parcelas.add(parcelaCalculo);

		// seguros

		if (!naoCalcularDFI) {
			valorSeguroDFI = this.valorImovel.multiply(this.seguroDFI.divide(BigDecimal.valueOf(100)));
		}

		BigDecimal valorSeguroDFIParcela = BigDecimal.ZERO;
		BigDecimal valorSeguroMIPParcela = BigDecimal.ZERO;

		for (int i = 1; i <= this.qtdParcelas.intValue(); i++) {
			parcelaCalculo = new SimulacaoDetalheVO();

			BigDecimal juros = BigDecimal
					.valueOf(FinanceLib.fv(this.taxaJuros.divide(BigDecimal.valueOf(100)).doubleValue(), // taxa
							1, // prazo
							0, // parcela
							saldoDevedorAnterior.negate().doubleValue(), // valor presente
							false));
			juros = juros.subtract(saldoDevedorAnterior);

			parcelaCalculo.setNumeroParcela(BigInteger.valueOf(i));

			if (!naoCalcularMIP) {
				valorSeguroMIP = saldoDevedorAnterior.multiply(this.seguroMIP.divide(BigDecimal.valueOf(100)));
			}

			valorSeguroMIPParcela = valorSeguroMIPParcela.add(valorSeguroMIP);

			valorSeguroDFIParcela = valorSeguroDFIParcela.add(valorSeguroDFI);

			BigDecimal parcelaAmortizacao = BigDecimal.ZERO;

			if ((this.carencia.compareTo(BigInteger.valueOf(i)) >= 0)) {
				parcelaCalculo.setValorParcela(BigDecimal.ZERO);
				parcelaCalculo.setJuros(BigDecimal.ZERO);
				parcelaCalculo.setAmortizacao(BigDecimal.ZERO);
				parcelaCalculo.setValorParcela(BigDecimal.ZERO);
				parcelaCalculo.setSeguroDFI(BigDecimal.ZERO);
				parcelaCalculo.setSeguroMIP(BigDecimal.ZERO);
			} else {

				if (saldoDevedorAnterior.compareTo(BigDecimal.ZERO) <= 0) {
					parcelaCalculo
							.setValorParcela(BigDecimal.ZERO.add(valorSeguroDFIParcela).add(valorSeguroMIPParcela));
				} else {
					parcelaCalculo.setValorParcela(parcelaPGTO.add(valorSeguroDFIParcela).add(valorSeguroMIPParcela));
					parcelaAmortizacao = parcelaPGTO;
				}
				parcelaCalculo.setJuros(juros);

				BigDecimal coeficienteAmortizacao = (parcelaAmortizacao.subtract(parcelaCalculo.getJuros()))
						.divide(saldoDevedorCarencia, MathContext.DECIMAL128);
//				parcelaCalculo.setAmortizacao(this.valorCredito.multiply(coeficienteAmortizacao));

				parcelaCalculo.setAmortizacao(parcelaAmortizacao.subtract(parcelaCalculo.getJuros()));

				parcelaCalculo.setSeguroDFI(valorSeguroDFIParcela);
				parcelaCalculo.setSeguroMIP(valorSeguroMIPParcela);

				calcularIOF(parcelaCalculo, i);
//				parcelaCalculo.setValorIOFAdicional(valorIOFAdicional);

				valorTotalIOF = valorTotalIOF.add(parcelaCalculo.getValorIOF());
//				valorTotalIOFAdicional = valorTotalIOFAdicional.add(parcelaCalculo.getValorIOFAdicional());

				valorSeguroDFIParcela = BigDecimal.ZERO;
				valorSeguroMIPParcela = BigDecimal.ZERO;
			}

			BigDecimal saldo = saldoDevedorAnterior.add(juros).subtract(parcelaAmortizacao);
			if (saldo.compareTo(BigDecimal.ZERO) == -1)
				saldo = BigDecimal.ZERO;
			
			parcelaCalculo.setSaldoDevedorInicial(saldo.setScale(2, RoundingMode.HALF_EVEN));

			saldoDevedorAnterior = saldo;

			parcelas.add(parcelaCalculo);
		}
	}

	private void calcularIOF(SimulacaoDetalheVO parcelaCalculo, int i) {

		long diasVencimento = (long) DateUtil.getDaysBetweenDates(dataSimulacao,
				DateUtil.adicionarPeriodo(dataSimulacao, i, Calendar.MONTH));

		if (diasVencimento > 365) {
			diasVencimento = 365l;
		}
		BigDecimal valorOParcelaIOF = BigDecimal.ZERO;
		
		if( this.tipoCalculo.equals("Americano") && parcelaCalculo.getAmortizacao().compareTo(BigDecimal.ZERO)==1) {
			valorOParcelaIOF = (this.valorCredito
					.multiply(tarifaIOFDiario.multiply(BigDecimal.valueOf(diasVencimento)))
					).setScale(2, RoundingMode.HALF_EVEN);
		}else {
			 valorOParcelaIOF = (parcelaCalculo.getAmortizacao()
						.multiply(tarifaIOFDiario.multiply(BigDecimal.valueOf(diasVencimento)))
						).setScale(2, RoundingMode.HALF_EVEN);			 
		}
		
//				long diasVencimento = i * 30l;
		boolean calcularIOF = true;
//		if ("PF".equals(tipoPessoa)) {
//			if ((valorTotalIOF.add(valorOParcelaIOF))
//					.compareTo(valorCredito.multiply(BigDecimal.valueOf(0.03d))) == 1) {
//				calcularIOF = false;
//			}
//		} else {
//			if ((valorTotalIOF.add(valorOParcelaIOF))
//					.compareTo(valorCredito.multiply(BigDecimal.valueOf(0.015d))) == 1) {
//				calcularIOF = false;
//			}
//		}

		if (calcularIOF) {
			parcelaCalculo.setValorIOF(valorOParcelaIOF);
		} else {
			parcelaCalculo.setValorIOF(BigDecimal.ZERO);
		}
	}

	private BigDecimal calcularIOFAdicional() {
		BigDecimal valorIOFAdicional = valorCredito.multiply(tarifaIOFAdicional);
		// .divide(BigDecimal.valueOf(qtdParcelas.subtract(carencia).doubleValue()),
		// MathContext.DECIMAL128);
		return valorIOFAdicional;
	}

	public void calcularSac() {

		valorTotalIOF = BigDecimal.ZERO;
		valorTotalIOFAdicional = calcularIOFAdicional();

		parcelas = new ArrayList<SimulacaoDetalheVO>();

		BigDecimal saldoDevedorAnterior = this.valorCredito;
		BigDecimal saldoDevedorCarencia = getSaldoDevedorCarencia();

		SimulacaoDetalheVO parcelaCalculo = new SimulacaoDetalheVO(this.valorCredito);
		parcelas.add(parcelaCalculo);

		// seguros

		if (!naoCalcularDFI) {
			valorSeguroDFI = this.valorImovel.multiply(this.seguroDFI.divide(BigDecimal.valueOf(100)));
		}

		BigDecimal valorSeguroDFIParcela = BigDecimal.ZERO;
		BigDecimal valorSeguroMIPParcela = BigDecimal.ZERO;

		// fixo
		BigDecimal amortizacao = saldoDevedorCarencia.divide(
				BigDecimal.valueOf((this.qtdParcelas.subtract(this.carencia).longValue())), MathContext.DECIMAL128);

		for (int i = 1; i <= this.qtdParcelas.intValue(); i++) {
			parcelaCalculo = new SimulacaoDetalheVO();

			BigDecimal juros = saldoDevedorAnterior.multiply(this.taxaJuros.divide(BigDecimal.valueOf(100)));

			parcelaCalculo.setNumeroParcela(BigInteger.valueOf(i));

			if (!naoCalcularMIP) {
				valorSeguroMIP = saldoDevedorAnterior.multiply(this.seguroMIP.divide(BigDecimal.valueOf(100)));
			}

			valorSeguroMIPParcela = valorSeguroMIPParcela.add(valorSeguroMIP);

			valorSeguroDFIParcela = valorSeguroDFIParcela.add(valorSeguroDFI);

			if ((this.carencia.compareTo(BigInteger.valueOf(i)) >= 0)) {
				parcelaCalculo.setValorParcela(BigDecimal.ZERO);
				parcelaCalculo.setJuros(BigDecimal.ZERO);
				parcelaCalculo.setAmortizacao(BigDecimal.ZERO);
				parcelaCalculo.setValorParcela(BigDecimal.ZERO);
				parcelaCalculo.setSeguroDFI(BigDecimal.ZERO);
				parcelaCalculo.setSeguroMIP(BigDecimal.ZERO);
			} else {

				if (saldoDevedorAnterior.compareTo(BigDecimal.ZERO) <= 0) {
					parcelaCalculo
							.setValorParcela(BigDecimal.ZERO.add(valorSeguroDFIParcela).add(valorSeguroMIPParcela));
				} else {
					parcelaCalculo.setValorParcela(
							juros.add(amortizacao).add(valorSeguroDFIParcela).add(valorSeguroMIPParcela));
				}

				parcelaCalculo.setJuros(juros);
				parcelaCalculo.setAmortizacao(amortizacao);

				parcelaCalculo.setSeguroDFI(valorSeguroDFIParcela);
				parcelaCalculo.setSeguroMIP(valorSeguroMIPParcela);

				calcularIOF(parcelaCalculo, i);
//				parcelaCalculo.setValorIOFAdicional(valorIOFAdicional);

				valorTotalIOF = valorTotalIOF.add(parcelaCalculo.getValorIOF());
//				valorTotalIOFAdicional = valorTotalIOFAdicional.add(parcelaCalculo.getValorIOFAdicional());

				valorSeguroDFIParcela = BigDecimal.ZERO;
				valorSeguroMIPParcela = BigDecimal.ZERO;
			}

			BigDecimal saldo = BigDecimal.ZERO;

			if ((this.carencia.compareTo(BigInteger.valueOf(i)) >= 0)) {
				saldo = saldoDevedorAnterior.add(juros);
			} else {
				saldo = saldoDevedorAnterior.subtract(amortizacao);
				if (saldo.compareTo(BigDecimal.ZERO) == -1)
					saldo = BigDecimal.ZERO;
			}
			parcelaCalculo.setSaldoDevedorInicial(saldo.setScale(2, RoundingMode.HALF_EVEN));

			saldoDevedorAnterior = saldo;

			parcelas.add(parcelaCalculo);
		}
	}

	public void calcularAmericano() {

		valorTotalIOF = BigDecimal.ZERO;
		valorTotalIOFAdicional = calcularIOFAdicional();

		parcelas = new ArrayList<SimulacaoDetalheVO>();
		BigDecimal saldoDevedorCarencia = getSaldoDevedorCarencia();
		BigDecimal saldoDevedorAnterior = this.valorCredito;

		SimulacaoDetalheVO parcelaCalculo = new SimulacaoDetalheVO(this.valorCredito);
		parcelas.add(parcelaCalculo);

		// seguros

		if (!naoCalcularDFI) {
			valorSeguroDFI = this.valorImovel.multiply(this.seguroDFI.divide(BigDecimal.valueOf(100)));
		}

		BigDecimal valorSeguroDFIParcela = BigDecimal.ZERO;
		BigDecimal valorSeguroMIPParcela = BigDecimal.ZERO;

		// fixo
		BigDecimal amortizacao = BigDecimal.ZERO;

		for (int i = 1; i <= this.qtdParcelas.intValue(); i++) {
			parcelaCalculo = new SimulacaoDetalheVO();

			BigDecimal juros = saldoDevedorAnterior.multiply(this.taxaJuros.divide(BigDecimal.valueOf(100)));

			parcelaCalculo.setNumeroParcela(BigInteger.valueOf(i));

			if (!naoCalcularMIP) {
				valorSeguroMIP = saldoDevedorAnterior.multiply(this.seguroMIP.divide(BigDecimal.valueOf(100)));
			}

			valorSeguroMIPParcela = valorSeguroMIPParcela.add(valorSeguroMIP);

			valorSeguroDFIParcela = valorSeguroDFIParcela.add(valorSeguroDFI);

			if ((this.carencia.compareTo(BigInteger.valueOf(i)) >= 0)) {
				parcelaCalculo.setValorParcela(BigDecimal.ZERO);
				parcelaCalculo.setJuros(BigDecimal.ZERO);
				parcelaCalculo.setAmortizacao(BigDecimal.ZERO);
				parcelaCalculo.setValorParcela(BigDecimal.ZERO);
				parcelaCalculo.setSeguroDFI(BigDecimal.ZERO);
				parcelaCalculo.setSeguroMIP(BigDecimal.ZERO);
			} else {

				if (saldoDevedorAnterior.compareTo(BigDecimal.ZERO) <= 0) {
					parcelaCalculo
							.setValorParcela(BigDecimal.ZERO.add(valorSeguroDFIParcela).add(valorSeguroMIPParcela));
				} else if (this.qtdParcelas.compareTo(BigInteger.valueOf((long) i)) == 0) {
					parcelaCalculo.setValorParcela(
							saldoDevedorCarencia.add(juros.add(valorSeguroDFIParcela).add(valorSeguroMIPParcela)));
					amortizacao = saldoDevedorCarencia;
				} else {
					parcelaCalculo.setValorParcela(juros.add(valorSeguroDFIParcela).add(valorSeguroMIPParcela));
				}

				parcelaCalculo.setJuros(juros);
				parcelaCalculo.setAmortizacao(amortizacao);

				parcelaCalculo.setSeguroDFI(valorSeguroDFIParcela);
				parcelaCalculo.setSeguroMIP(valorSeguroMIPParcela);

				calcularIOF(parcelaCalculo, i);
//				parcelaCalculo.setValorIOFAdicional(valorIOFAdicional);

				valorTotalIOF = valorTotalIOF.add(parcelaCalculo.getValorIOF());
//				valorTotalIOFAdicional = valorTotalIOFAdicional.add(parcelaCalculo.getValorIOFAdicional());

				valorSeguroDFIParcela = BigDecimal.ZERO;
				valorSeguroMIPParcela = BigDecimal.ZERO;
			}

			BigDecimal saldo = BigDecimal.ZERO;

			if ((this.carencia.compareTo(BigInteger.valueOf(i)) >= 0)) {
				saldo = saldoDevedorAnterior.add(juros);
			} else {
				saldo = saldoDevedorAnterior.subtract(amortizacao);
				if (saldo.compareTo(BigDecimal.ZERO) == -1)
					saldo = BigDecimal.ZERO;
			}
			parcelaCalculo.setSaldoDevedorInicial(saldo.setScale(2, RoundingMode.HALF_EVEN));

			saldoDevedorAnterior = saldo;

			parcelas.add(parcelaCalculo);
		}
	}

	private BigDecimal getSaldoDevedorCarencia() {
		BigDecimal saldoDevedorCarencia = BigDecimal
				.valueOf(FinanceLib.fv(this.taxaJuros.divide(BigDecimal.valueOf(100)).doubleValue(),
						this.carencia.intValue(), 0, this.valorCredito.negate().doubleValue(), false));
		return saldoDevedorCarencia;
	}
	
	public void calcularEnvelope() {

		parcelas = new ArrayList<SimulacaoDetalheVO>();

		BigDecimal parcelaPGTO = this.valorCredito.divide(new BigDecimal(this.qtdParcelas.subtract(this.carencia)), MathContext.DECIMAL128);

		SimulacaoDetalheVO parcelaCalculo = new SimulacaoDetalheVO(this.valorCredito);

		for (int i = 1; i <= this.qtdParcelas.intValue(); i++) {

			parcelaCalculo = new SimulacaoDetalheVO();
			parcelaCalculo.setNumeroParcela(BigInteger.valueOf(i));
			parcelaCalculo.setValorParcela(parcelaPGTO);
			parcelaCalculo.setJuros(BigDecimal.ZERO);
			parcelaCalculo.setAmortizacao(BigDecimal.ZERO);
			if ((this.carencia.compareTo(BigInteger.valueOf(i)) >= 0)) {
				parcelaCalculo.setValorParcela(BigDecimal.ZERO);
			} else {
				parcelaCalculo.setValorParcela(parcelaPGTO);
			}
			parcelaCalculo.setSeguroDFI(BigDecimal.ZERO);
			parcelaCalculo.setSeguroMIP(BigDecimal.ZERO);

			parcelas.add(parcelaCalculo);
		}
	}

	public BigDecimal getIOFTotal() {
		return this.valorTotalIOF.add(this.valorTotalIOFAdicional);
	}

	public BigDecimal getTarifaIOFDiario() {
		return tarifaIOFDiario;
	}

	public void setTarifaIOFDiario(BigDecimal tarifaIOFDiario) {
		this.tarifaIOFDiario = tarifaIOFDiario;
	}

	public BigDecimal getTarifaIOFAdicional() {
		return tarifaIOFAdicional;
	}

	public void setTarifaIOFAdicional(BigDecimal tarifaIOFAdicional) {
		this.tarifaIOFAdicional = tarifaIOFAdicional;
	}

	public BigDecimal getSeguroMIP() {
		return seguroMIP;
	}

	public void setSeguroMIP(BigDecimal seguroMIP) {
		this.seguroMIP = seguroMIP;
	}

	public BigDecimal getSeguroDFI() {
		return seguroDFI;
	}

	public void setSeguroDFI(BigDecimal seguroDFI) {
		this.seguroDFI = seguroDFI;
	}

	public Date getDataSimulacao() {
		return dataSimulacao;
	}

	public void setDataSimulacao(Date dataSimulacao) {
		this.dataSimulacao = dataSimulacao;
	}

	public String getTipoPessoa() {
		return tipoPessoa;
	}

	public void setTipoPessoa(String tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}

	public String getTipoCalculo() {
		return tipoCalculo;
	}

	public void setTipoCalculo(String tipoCalculo) {
		this.tipoCalculo = tipoCalculo;
	}

	public boolean isNaoCalcularDFI() {
		return naoCalcularDFI;
	}

	public void setNaoCalcularDFI(boolean naoCalcularDFI) {
		this.naoCalcularDFI = naoCalcularDFI;
	}

	public boolean isNaoCalcularMIP() {
		return naoCalcularMIP;
	}

	public void setNaoCalcularMIP(boolean naoCalcularMIP) {
		this.naoCalcularMIP = naoCalcularMIP;
	}

	public boolean isMostrarIPCA() {
		return mostrarIPCA;
	}

	public void setMostrarIPCA(boolean mostrarIPCA) {
		this.mostrarIPCA = mostrarIPCA;
	}

	public BigDecimal getValorCredito() {
		return valorCredito;
	}

	public void setValorCredito(BigDecimal valorCredito) {
		this.valorCredito = valorCredito;
	}

	public BigDecimal getTaxaJuros() {
		return taxaJuros;
	}

	public void setTaxaJuros(BigDecimal taxaJuros) {
		this.taxaJuros = taxaJuros;
	}

	public BigInteger getCarencia() {
		return carencia;
	}

	public void setCarencia(BigInteger carencia) {
		this.carencia = carencia;
	}

	public BigInteger getQtdParcelas() {
		return qtdParcelas;
	}

	public void setQtdParcelas(BigInteger qtdParcelas) {
		this.qtdParcelas = qtdParcelas;
	}

	public BigDecimal getValorImovel() {
		return valorImovel;
	}

	public void setValorImovel(BigDecimal valorImovel) {
		this.valorImovel = valorImovel;
	}

	public BigDecimal getCustoEmissaoValor() {
		return custoEmissaoValor;
	}

	public void setCustoEmissaoValor(BigDecimal custoEmissaoValor) {
		this.custoEmissaoValor = custoEmissaoValor;
	}

	public BigDecimal getValorTotalIOF() {
		return valorTotalIOF;
	}

	public void setValorTotalIOF(BigDecimal valorTotalIOF) {
		this.valorTotalIOF = valorTotalIOF;
	}

	public BigDecimal getValorTotalIOFAdicional() {
		return valorTotalIOFAdicional;
	}

	public void setValorTotalIOFAdicional(BigDecimal valorTotalIOFAdicional) {
		this.valorTotalIOFAdicional = valorTotalIOFAdicional;
	}

	public List<SimulacaoDetalheVO> getParcelas() {
		return parcelas;
	}

	public void setParcelas(List<SimulacaoDetalheVO> parcelas) {
		this.parcelas = parcelas;
	}

	public BigDecimal getValorCreditoLiberado() {
		return valorCreditoLiberado;
	}

	public void setValorCreditoLiberado(BigDecimal valorCreditoLiberado) {
		this.valorCreditoLiberado = valorCreditoLiberado;
	}

	public BigDecimal getTaxaJurosAoAno() {
		return taxaJurosAoAno;
	}

	public void setTaxaJurosAoAno(BigDecimal taxaJurosAoAno) {
		this.taxaJurosAoAno = taxaJurosAoAno;
	}

	public BigDecimal getCetAoMes() {
		return cetAoMes;
	}

	public void setCetAoMes(BigDecimal cetAoMes) {
		this.cetAoMes = cetAoMes;
	}

	public BigDecimal getCetAoAno() {
		return cetAoAno;
	}

	public void setCetAoAno(BigDecimal cetAoAno) {
		this.cetAoAno = cetAoAno;
	}

	public BigDecimal getLtv() {
		return ltv;
	}

	public void setLtv(BigDecimal ltv) {
		this.ltv = ltv;
	}
		
}
