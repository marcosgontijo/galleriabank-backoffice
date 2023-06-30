package com.webnowbr.siscoat.simulador;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.common.ReportUtil;
import com.webnowbr.siscoat.common.SiscoatConstants;

import javassist.expr.NewArray;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;


import org.apache.poi.ss.formula.eval.*;
import org.apache.poi.ss.formula.functions.AggregateFunction;
import org.apache.poi.ss.formula.functions.NumericFunction;

/** ManagedBean. */
@ManagedBean(name = "simuladorMB")
@SessionScoped
public class SimuladorMB {
	/** Variavel. */
	private String tipoPessoa;
	private String tipoCalculo;
	private String identificacao;
	private BigDecimal valorImovel;
	private BigDecimal valorCredito;
	private BigDecimal taxaJuros;
	private BigDecimal ipcaSimulado = BigDecimal.ZERO;;
	private BigInteger parcelas;
	private BigInteger carencia;
	private boolean naoCalcularDFI;
    private boolean naoCalcularMIP;
    private boolean naoCalcularTxAdm;
    private char tipoCalculoFinal;
    private boolean mostrarIPCA;
    private boolean validar;
    private boolean simularComIPCA = false;

	private SimulacaoVO simulacao;

	public String clearFields() {
		valorImovel = null; // BigDecimal.valueOf(1000000);
		valorCredito = null; // BigDecimal.valueOf(200000);
		taxaJuros = null;
		BigDecimal.valueOf(1.49);
		parcelas = null;
		BigInteger.valueOf(24);
		carencia = BigInteger.valueOf(1);
		tipoPessoa = "PF";
		tipoCalculo = "PRICE";
		simulacao = null;
		tipoCalculoFinal= 'B';
		mostrarIPCA = true;
		validar = true;
		simularComIPCA = false;
		
		return "/Atendimento/Cobranca/Simulador/SimuladorOperacao.xhtml";
	}

	public String simular() {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		this.simulacao = new SimulacaoVO();
		if(validar) {
			Map<String, String> validacao = new HashMap<String, String>();
			if (CommonsUtil.mesmoValor(this.tipoCalculo, "Americano")) {
				if (this.parcelas.compareTo(BigInteger.valueOf(36)) == 1) {
					validacao.put("Prazo excedido !!", "O prazo máximo é de 36 meses para o tipo de cálculo americano");
				}
				if (this.taxaJuros.compareTo(BigDecimal.valueOf(1.99)) < 0) {
					validacao.put("Juros inválido !!", "A menor taxa de juros é de 1.99% para o tipo de cálculo americano");
				}
			} else {
				if (this.parcelas.compareTo(BigInteger.valueOf(240)) == 1) {
					validacao.put("Prazo excedido !!", "O prazo máximo é de 240 meses");
				}
				if (this.taxaJuros.compareTo(BigDecimal.valueOf(0.99)) < 0) {
					validacao.put("Juros inválido !!", "A menor taxa de juros é de 0.99%");
				}
			}
			if (this.valorCredito.compareTo(BigDecimal.ZERO) == 0) {
				validacao.put("Valor Empréstimo !!", "O valor de empréstimo não foi informado");
			}
			if (this.valorImovel.compareTo(BigDecimal.ZERO) == 0) {
				validacao.put("Valor Imóvel !!", "O valor de imóvel não foi informado");
			}
			if (this.valorCredito.compareTo(BigDecimal.ZERO) == 1 && this.valorImovel.compareTo(BigDecimal.ZERO) == 1
					&& this.valorCredito.compareTo(valorImovel) == 1) {
				validacao.put("Valores inválidos!!", "O imóvel deve ter valor maior que o empréstimo");
			}
			if (!CommonsUtil.semValor(validacao)) {
				for (Map.Entry<String, String> mensagem : validacao.entrySet()) {
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem.getKey(), mensagem.getValue()));
				}
				return null;
			}
		}

		BigDecimal custoEmissaoValor = SiscoatConstants.CUSTO_EMISSAO_MINIMO;
		
		final BigDecimal custoEmissaoPercentual;
		
		custoEmissaoPercentual = SiscoatConstants.CUSTO_EMISSAO_PERCENTUAL_BRUTO;
		
		if (this.valorCredito.multiply(custoEmissaoPercentual.divide(BigDecimal.valueOf(100)))
				.compareTo(SiscoatConstants.CUSTO_EMISSAO_MINIMO) > 0) {
			custoEmissaoValor = this.valorCredito.multiply(custoEmissaoPercentual.divide(BigDecimal.valueOf(100)));
		}
		
		BigDecimal tarifaIOFDiario;
		BigDecimal tarifaIOFAdicional = SiscoatConstants.TARIFA_IOF_ADICIONAL.divide(BigDecimal.valueOf(100));

		if ("PF".equals(tipoPessoa)) {
			tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PF.divide(BigDecimal.valueOf(100));
		} else {
			tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PJ.divide(BigDecimal.valueOf(100));
		}

	
		
		SimulacaoVO simulador = new SimulacaoVO();
		simulador.setDataSimulacao(DateUtil.getDataHoje());
		simulador.setTarifaIOFDiario(tarifaIOFDiario);
		simulador.setTarifaIOFAdicional(tarifaIOFAdicional);
		simulador.setSeguroMIP(SiscoatConstants.SEGURO_MIP);
		simulador.setSeguroDFI(SiscoatConstants.SEGURO_DFI);
		simulador.setTipoPessoa(tipoPessoa);
		// valores

		simulador.setTaxaJuros(this.taxaJuros);
		simulador.setValorCredito(this.valorCredito);
		simulador.setCarencia(this.carencia);
		simulador.setQtdParcelas(this.parcelas);
		simulador.setValorImovel(this.valorImovel);
		simulador.setCustoEmissaoValor(custoEmissaoValor);
		simulador.setCustoEmissaoPercentual(custoEmissaoPercentual);
		simulador.setTipoCalculo(tipoCalculo);
		simulador.setNaoCalcularDFI(this.naoCalcularDFI);
		simulador.setNaoCalcularMIP(this.isNaoCalcularMIP());
		simulador.setNaoCalcularTxAdm(this.naoCalcularTxAdm);
		simulador.setSimularComIPCA(this.simularComIPCA);
		simulador.setIpcaSimulado(this.ipcaSimulado);
		if (CommonsUtil.mesmoValor('L', tipoCalculoFinal)) {
			GoalSeek goalSeek = new GoalSeek(CommonsUtil.doubleValue(valorCredito), 
					CommonsUtil.doubleValue(valorCredito.divide(BigDecimal.valueOf(1.5), MathContext.DECIMAL128)),
					CommonsUtil.doubleValue(valorCredito.multiply(BigDecimal.valueOf(1.5), MathContext.DECIMAL128)));		
			GoalSeekFunction gsFunfction = new GoalSeekFunction();
			BigDecimal valorBruto = CommonsUtil.bigDecimalValue(gsFunfction.getGoalSeek(goalSeek, simulador));
			simulador.setValorCredito(valorBruto.setScale(2, RoundingMode.HALF_UP));
		} else {
			simulador.calcular();
		}
		simulador.calcularValorLiberado();
		
		this.simulacao = simulador;
		this.simulacao.setMostrarIPCA(mostrarIPCA);
		this.simulacao.setTipoCalculo(tipoCalculo);
		this.simulacao.setTipoPessoa(tipoPessoa);
		
		BigDecimal jurosAoAno = BigDecimal.ZERO;
		jurosAoAno = BigDecimal.ONE.add((simulacao.getTaxaJuros().divide(BigDecimal.valueOf(100), MathContext.DECIMAL128)));
		jurosAoAno = CommonsUtil.bigDecimalValue(Math.pow(CommonsUtil.doubleValue(jurosAoAno), 12));
		jurosAoAno = jurosAoAno.subtract(BigDecimal.ONE);
		jurosAoAno = jurosAoAno.multiply(BigDecimal.valueOf(100), MathContext.DECIMAL128);
		jurosAoAno = jurosAoAno.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.simulacao.setTaxaJurosAoAno(jurosAoAno);
		
		if (simulacao.getParcelas().size() > 0) {
			BigDecimal cet = BigDecimal.ZERO;
			BigDecimal cetAno = BigDecimal.ZERO;
			double cetDouble = 0.0;
			
			double[] cash_flows = new double[simulacao.getQtdParcelas().intValue() + 1];
			
			cash_flows[0] = simulacao.getValorCreditoLiberado().negate().doubleValue();
			
			for (int i = 1; i <= simulacao.getQtdParcelas().intValue(); i++) {
				BigDecimal calc_value = simulacao.getParcelas().get(i).getAmortizacao().add(simulacao.getParcelas().get(i).getJuros());
				cash_flows[i] = calc_value.doubleValue();
			}
			
			
			int maxGuess = 500;
			cetDouble = irr(cash_flows, maxGuess);
			
			if (CommonsUtil.mesmoValor(CommonsUtil.stringValue(cetDouble), "NaN")) {
				cetDouble = 0;
			} 
			
			cetDouble = cetDouble * 100; 
			cet = CommonsUtil.bigDecimalValue(cetDouble);	
			cetAno = BigDecimal.ONE.add((cet.divide(BigDecimal.valueOf(100), MathContext.DECIMAL128)));
			cetAno = CommonsUtil.bigDecimalValue(Math.pow(CommonsUtil.doubleValue(cetAno), 12));
			cetAno = cetAno.subtract(BigDecimal.ONE);
			cetAno = cetAno.multiply(BigDecimal.valueOf(100), MathContext.DECIMAL128);
			
			cetAno = cetAno.setScale(2, BigDecimal.ROUND_HALF_UP);
			cet = cet.setScale(2, BigDecimal.ROUND_HALF_UP);
			
			this.simulacao.setCetAoAno(cetAno);
			this.simulacao.setCetAoMes(cet);
		}
		
		BigDecimal ltv = this.simulacao.getValorCredito().divide(this.simulacao.getValorImovel(),MathContext.DECIMAL128);
		ltv = ltv.multiply(BigDecimal.valueOf(100));
		ltv = ltv.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.simulacao.setLtv(ltv);
		return null;
	}

	public StreamedContent download(boolean isNovo) throws JRException, IOException {	
		
		if (!CommonsUtil.semValor(this.simulacao.getParcelas())) {

			JasperPrint jp = null;
			
			if(simularComIPCA) {
				jp = geraPDFSimulacaoComExemploIPCA();
			} else if(isNovo) {
				jp = geraPDFSimulacaoNOVO();
			} else {
				jp = geraPDFSimulacao();
			}

			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
					FacesContext.getCurrentInstance());

			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
			if (CommonsUtil.semValor(identificacao))
				gerador.open("Galleria Bank - simulação.pdf");
			else
				gerador.open(String.format("Galleria Bank - simulação %s.pdf", this.identificacao));
			gerador.feed(jp);
			gerador.close();

		}
		return null;
	}

	public JasperPrint geraPDFSimulacao() throws JRException, IOException {

		final ReportUtil ReportUtil = new ReportUtil();

		JasperReport rptSimulacao = ReportUtil.getRelatorio("SimulacaoCreditoNovo");
		JasperReport rptSimulacaoDetalhe = ReportUtil.getRelatorio("SimulacaoCreditoParcelasNovo");
		InputStream logoStream = getClass().getResourceAsStream("/resource/GalleriaBank.png");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("SUBREPORT_DETALHE", rptSimulacaoDetalhe);

		parameters.put("IMAGEMLOGO", IOUtils.toByteArray(logoStream));
		parameters.put("REPORT_LOCALE", new Locale("pt", "BR"));
		parameters.put("MOSTRARIPCA", this.mostrarIPCA);

		List<SimulacaoVO> list = new ArrayList<SimulacaoVO>();
		list.add(simulacao);

		final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);

		return JasperFillManager.fillReport(rptSimulacao, parameters, dataSource);

	}
	
	// Método Criado Apenas para teste
	public JasperPrint geraPDFSimulacaoNOVO() throws JRException, IOException {

		final ReportUtil ReportUtil = new ReportUtil();

		JasperReport rptSimulacao = ReportUtil.getRelatorio("SimulacaoCreditoNovo");
		JasperReport rptSimulacaoDetalhe = ReportUtil.getRelatorio("SimuladorparcelasTeste");
		InputStream logoStream = getClass().getResourceAsStream("/resource/GalleriaBank.png");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("SUBREPORT_DETALHE", rptSimulacaoDetalhe);

		parameters.put("IMAGEMLOGO", IOUtils.toByteArray(logoStream));
		parameters.put("REPORT_LOCALE", new Locale("pt", "BR"));
		parameters.put("MOSTRARIPCA", this.mostrarIPCA);

		List<SimulacaoVO> list = new ArrayList<SimulacaoVO>();
		list.add(simulacao);

		final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);

		return JasperFillManager.fillReport(rptSimulacao, parameters, dataSource);
	}

	// Método Criado Apenas para teste
	public JasperPrint geraPDFSimulacaoComExemploIPCA() throws JRException, IOException {

		final ReportUtil ReportUtil = new ReportUtil();

		JasperReport rptSimulacao = ReportUtil.getRelatorio("SimulacaoCreditoExemploIPCA");
		JasperReport rptSimulacaoDetalhe = ReportUtil.getRelatorio("SimuladorParcelasExemploIPCA");
		InputStream logoStream = getClass().getResourceAsStream("/resource/GalleriaBank.png");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("SUBREPORT_DETALHE", rptSimulacaoDetalhe);

		parameters.put("IMAGEMLOGO", IOUtils.toByteArray(logoStream));
		parameters.put("REPORT_LOCALE", new Locale("pt", "BR"));
		parameters.put("MOSTRARIPCA", this.mostrarIPCA);

		List<SimulacaoVO> list = new ArrayList<SimulacaoVO>();
		list.add(simulacao);

		final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);

		return JasperFillManager.fillReport(rptSimulacao, parameters, dataSource);
	}
	
// 	Função TIR(excel) tirada de https://apache.googlesource.com/poi/+/887af17af3cc2ef8733f9a1990bd99fdeabf789a/src/java/org/apache/poi/ss/formula/functions/Irr.java
	
	public static double irr(double[] income, int maxGuess) {
        return irr(income, 0.1d, maxGuess);
    }

    public static double irr(double[] values, double guess, int maxGuess) {
        int maxIterationCount = maxGuess;
        double absoluteAccuracy = 1E-7;
        double x0 = guess;
        double x1;
        int i = 0;
        while (i < maxIterationCount) {
            // the value of the function (NPV) and its derivate can be calculated in the same loop
            double fValue = 0;
            double fDerivative = 0;
            for (int k = 0; k < values.length; k++) {
                fValue += values[k] / Math.pow(1.0 + x0, k);
                fDerivative += -k * values[k] / Math.pow(1.0 + x0, k + 1);
            }
            // the essense of the Newton-Raphson Method
            x1 = x0 - fValue/fDerivative;
            if (Math.abs(x1 - x0) <= absoluteAccuracy) {
                return x1;
            }
            x0 = x1;
            ++i;
        }
        // maximum number of iterations is exceeded
        return Double.NaN;
    }

	public BigDecimal getValorImovel() {
		return valorImovel;
	}

	public void setValorImovel(BigDecimal valorImovel) {
		this.valorImovel = valorImovel;
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

	public BigInteger getParcelas() {
		return parcelas;
	}

	public void setParcelas(BigInteger parcelas) {
		this.parcelas = parcelas;
	}

	public BigInteger getCarencia() {
		return carencia;
	}

	public void setCarencia(BigInteger carencia) {
		this.carencia = carencia;
	}

	public String getTipoPessoa() {
		return tipoPessoa;
	}

	public void setTipoPessoa(String tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}

	public SimulacaoVO getSimulacao() {
		return simulacao;
	}

	public void setSimulacao(SimulacaoVO simulacao) {
		this.simulacao = simulacao;
	}

	public String getTipoCalculo() {
		return tipoCalculo;
	}

	public void setTipoCalculo(String tipoCalculo) {
		this.tipoCalculo = tipoCalculo;
	}

	public String getIdentificacao() {
		return identificacao;
	}

	public void setIdentificacao(String identificacao) {
		this.identificacao = identificacao;
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

	public char getTipoCalculoFinal() {
		return tipoCalculoFinal;
	}

	public void setTipoCalculoFinal(char tipoCalculoFinal) {
		this.tipoCalculoFinal = tipoCalculoFinal;
	}

	public boolean isMostrarIPCA() {
		return mostrarIPCA;
	}

	public void setMostrarIPCA(boolean mostrarIPCA) {
		this.mostrarIPCA = mostrarIPCA;
	}

	public boolean isValidar() {
		return validar;
	}

	public void setValidar(boolean validar) {
		this.validar = validar;
	}

	public boolean isNaoCalcularTxAdm() {
		return naoCalcularTxAdm;
	}

	public void setNaoCalcularTxAdm(boolean naoCalcularTxAdm) {
		this.naoCalcularTxAdm = naoCalcularTxAdm;
	}

	public BigDecimal getIpcaSimulado() {
		return ipcaSimulado;
	}

	public void setIpcaSimulado(BigDecimal ipcaSimulado) {
		this.ipcaSimulado = ipcaSimulado;
	}

	public boolean isSimularComIPCA() {
		return simularComIPCA;
	}

	public void setSimularComIPCA(boolean simularComIPCA) {
		this.simularComIPCA = simularComIPCA;
	}
}


