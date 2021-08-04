package com.webnowbr.siscoat.simulador;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

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
	private BigInteger parcelas;
	private BigInteger carencia;

	

	private SimulacaoVO simulacao;

	public String clearFields() {
		valorImovel = null; // BigDecimal.valueOf(1000000);
		valorCredito = null; // BigDecimal.valueOf(200000);
		taxaJuros = null;
		BigDecimal.valueOf(1.49);
		parcelas = null;
		BigInteger.valueOf(24);
		carencia = BigInteger.valueOf(2);
		tipoPessoa = "PF";
		tipoCalculo = "PRICE";
		simulacao = null;
		return "/Atendimento/Cobranca/Simulador/SimuladorOperacao.xhtml";
	}

	public String simular() {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		this.simulacao = new SimulacaoVO();

		Map<String, String> validacao = new HashMap<String, String>();
		if (CommonsUtil.mesmoValor(this.tipoCalculo, "Americanno")) {
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
				facesContext.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, mensagem.getKey(), mensagem.getValue()));
			}
			return null;
		}

		BigDecimal custoEmissaoValor = SiscoatConstants.CUSTO_EMISSAO_MINIMO;
		if (this.valorCredito.multiply(SiscoatConstants.CUSTO_EMISSAO_PERCENTUAL.divide(BigDecimal.valueOf(100)))
				.compareTo(SiscoatConstants.CUSTO_EMISSAO_MINIMO) > 0) {
			custoEmissaoValor = this.valorCredito.multiply(SiscoatConstants.CUSTO_EMISSAO_PERCENTUAL.divide(BigDecimal.valueOf(100)));
		}

		BigDecimal tarifaIOFDiario;
		BigDecimal tarifaIOFAdicional = BigDecimal.valueOf(0.38).divide(BigDecimal.valueOf(100));

		if ("PF".equals(tipoPessoa)) {
			tarifaIOFDiario = BigDecimal.valueOf(0.0082).divide(BigDecimal.valueOf(100));
		} else {
			tarifaIOFDiario = BigDecimal.valueOf(0.0041).divide(BigDecimal.valueOf(100));
		}

		SimulacaoVO simulador = new SimulacaoVO();
		simulador.setDataSimulacao(DateUtil.getDataHoje());
		simulador.setTarifaIOFDiario(tarifaIOFDiario);
		simulador.setTarifaIOFAdicional(tarifaIOFAdicional);
		simulador.setSeguroMIP(SiscoatConstants.SEGURO_MIP);
		simulador.setSeguroDFI(SiscoatConstants.SEGURO_DFI);
		// valores
		simulador.setValorCredito(this.valorCredito);
		simulador.setTaxaJuros(this.taxaJuros);
		simulador.setCarencia(this.carencia);
		simulador.setQtdParcelas(this.parcelas);
		simulador.setValorImovel(this.valorImovel);
		simulador.setCustoEmissaoValor(custoEmissaoValor);
		simulador.setTipoCalculo(tipoCalculo);
		simulador.calcular();

		BigDecimal fator = simulador.getIOFTotal().divide(simulador.getValorCredito(), MathContext.DECIMAL128);
		fator = BigDecimal.ONE.subtract(fator);
		BigDecimal valorBruto = (simulador.getValorCredito().add(custoEmissaoValor)).divide(fator,
				MathContext.DECIMAL128);

		SimulacaoVO simuladorLiquido = new SimulacaoVO();
		simuladorLiquido.setDataSimulacao(DateUtil.getDataHoje());
		simuladorLiquido.setTarifaIOFDiario(tarifaIOFDiario);
		simuladorLiquido.setTarifaIOFAdicional(tarifaIOFAdicional);
		simuladorLiquido.setSeguroMIP(SiscoatConstants.SEGURO_MIP);
		simuladorLiquido.setSeguroDFI(SiscoatConstants.SEGURO_DFI);
		// valores
		simuladorLiquido.setValorCreditoLiberado(simulador.getValorCredito());
		simuladorLiquido.setValorCredito(valorBruto);
		simuladorLiquido.setTaxaJuros(this.taxaJuros);
		simuladorLiquido.setCarencia(this.carencia);
		simuladorLiquido.setQtdParcelas(this.parcelas);
		simuladorLiquido.setValorImovel(this.valorImovel);
		simuladorLiquido.setCustoEmissaoValor(custoEmissaoValor);
		simuladorLiquido.setTipoCalculo(tipoCalculo);
		simuladorLiquido.calcular();

		this.simulacao = simuladorLiquido;
		this.simulacao.setTipoCalculo(tipoCalculo);
		this.simulacao.setTipoPessoa(tipoPessoa);
		return null;
	}

	public StreamedContent download() throws JRException, IOException {

		if (!CommonsUtil.semValor(this.simulacao.getParcelas())) {

			JasperPrint jp = null;

			jp = geraPDFSimulacao();

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

		JasperReport rptSimulacao = ReportUtil.getRelatorio("SimulacaoCredito");
		JasperReport rptSimulacaoDetalhe = ReportUtil.getRelatorio("SimulacaoCreditoParcelas");
		InputStream logoStream = getClass().getResourceAsStream("/resource/GalleriaBank.png");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("SUBREPORT_DETALHE", rptSimulacaoDetalhe);

		parameters.put("IMAGEMLOGO", IOUtils.toByteArray(logoStream));
		parameters.put("REPORT_LOCALE", new Locale("pt", "BR"));

		List<SimulacaoVO> list = new ArrayList<SimulacaoVO>();
		list.add(simulacao);

		final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);

		return JasperFillManager.fillReport(rptSimulacao, parameters, dataSource);

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

}
