package com.webnowbr.siscoat.cobranca.mb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.primefaces.model.charts.optionconfig.tooltip.Tooltip;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.op.CartorioDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;

@ManagedBean(name="dadosEmpresaMB")
@SessionScoped
public class DadosEmpresaMB {
	
	private String empresa;	
	
	private int totalContratosConsultar;
	private int prazoContrato;
	private BigDecimal valorUltimaPareclaPaga;
	private BigDecimal volumeCarteira;
	private BigDecimal somaContratos180;
	private BigDecimal somaContratos240;
	private BigDecimal porcentagem180;
	private BigDecimal porcentagem240;
	private int qtdDeparcelasVencidas;
	private BigDecimal inadimplencia30Soma;
	private BigDecimal inadimplencia60Soma;
	private BigDecimal inadimplencia90Soma;
	
	private BigDecimal prazoMax;
	private BigDecimal prazoMedio;
	private BigDecimal prazoMin;
	
	private BigDecimal taxaMax;
	private BigDecimal taxaMedia;
	private BigDecimal taxaMin;
	
	private BigDecimal taxaMaxIPCA;
	private BigDecimal taxaMediaIPCA;
	private BigDecimal taxaMinIPCA;
	
	private BigDecimal ltvMax;
	private BigDecimal ltvMedio;
	private BigDecimal ltvMin;
	
	private BigDecimal inadimplencia30Porcentagem;
	private BigDecimal inadimplencia60Porcentagem;
	private BigDecimal inadimplencia90Porcentagem;
	
	private Collection<ContratoCobranca> contratosInadimplencia30;
	private Collection<ContratoCobranca> contratosInadimplencia60;
	private Collection<ContratoCobranca> contratosInadimplencia90;
	private Collection<ContratoCobranca> contratoPrazoMin;
	
	private Collection<ContratoCobranca> contratosGrafico;
	
	private BigDecimal totalAVencer;
	
	private BarChartModel stackedGroupBarModel;
	
	private Collection<ContratoCobranca> contratos;
	
	public void clearDados() {
		this.somaContratos240 = BigDecimal.ZERO;
		this.volumeCarteira = BigDecimal.ZERO;
		this.somaContratos180 = BigDecimal.ZERO;
		this.valorUltimaPareclaPaga = BigDecimal.ZERO;
		this.qtdDeparcelasVencidas = 0;
		this.inadimplencia30Soma = BigDecimal.ZERO;
		this.inadimplencia60Soma = BigDecimal.ZERO;
		this.inadimplencia90Soma = BigDecimal.ZERO;
		this.inadimplencia30Porcentagem = BigDecimal.ZERO;
		this.inadimplencia60Porcentagem = BigDecimal.ZERO;
		this.inadimplencia90Porcentagem = BigDecimal.ZERO;
		
		this.contratosInadimplencia30 = new ArrayList<ContratoCobranca>();
		this.contratosInadimplencia60 = new ArrayList<ContratoCobranca>();
		this.contratosInadimplencia90 = new ArrayList<ContratoCobranca>();
		this.contratoPrazoMin = new ArrayList<ContratoCobranca>();
		
		this.prazoMax = BigDecimal.ZERO;
		this.prazoMedio = BigDecimal.ZERO;
		this.prazoMin =  BigDecimal.valueOf(0);
		
		this.taxaMax = BigDecimal.ZERO;
		this.taxaMedia = BigDecimal.ZERO;
		this.taxaMin =  BigDecimal.valueOf(0);
		
		this.taxaMaxIPCA = BigDecimal.ZERO;
		this.taxaMediaIPCA = BigDecimal.ZERO;
		this.taxaMinIPCA = BigDecimal.valueOf(0);
		
		this.ltvMax = BigDecimal.ZERO;
		this.ltvMedio = BigDecimal.ZERO;
		this.ltvMin = BigDecimal.valueOf(0);
		
		this.totalContratosConsultar = 0;
		
		this.totalAVencer = BigDecimal.ZERO;
		this.porcentagem240 = BigDecimal.ZERO;
		this.porcentagem180 = BigDecimal.ZERO;
		
		stackedGroupBarModel = new BarChartModel();
	}
	
	public String clearFieldsDadosEmpresa() {
		clearDados();
		empresa = "";
		return "/Atendimento/Cobranca/DadosEmpresas.xhtml";
	}

	public void consultaDadosEmpresa() {
		clearDados();
		
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		Calendar dataVencimentoParcela = Calendar.getInstance(zone, locale);
		dataHoje.set(Calendar.HOUR_OF_DAY, 0);
		dataHoje.set(Calendar.MINUTE, 0);
		dataHoje.set(Calendar.SECOND, 0);
		dataHoje.set(Calendar.MILLISECOND, 0);
		Date dataAtual = dataHoje.getTime();
		Calendar dataVencimentoMínima = new GregorianCalendar(2021,9,31);	
		
		this.prazoMin =  BigDecimal.valueOf(100);
		
		this.taxaMin =  BigDecimal.valueOf(100);
		
		this.taxaMinIPCA = BigDecimal.valueOf(100);

		this.ltvMin = BigDecimal.valueOf(100);
		
		this.contratos = new ArrayList<ContratoCobranca>();
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		
		this.contratos = contratoCobrancaDao.consultaContratos(empresa);
		this.totalContratosConsultar = this.contratos.size();
		
		contratosGrafico = new ArrayList<ContratoCobranca>();

		BigDecimal somaPeso = BigDecimal.ZERO;
		BigDecimal ltv = BigDecimal.ZERO;
			

		for(ContratoCobranca contrato : this.contratos) {
			BigDecimal valorVencido = BigDecimal.ZERO;
			this.qtdDeparcelasVencidas = 0;
			for (ContratoCobrancaDetalhes ccd : contrato.getListContratoCobrancaDetalhes()) {
				dataVencimentoParcela.setTime(ccd.getDataVencimento());		
				
				if (dataVencimentoParcela.getTime().before(dataHoje.getTime()) && !ccd.isParcelaPaga()) {
					ccd.setParcelaVencida(true);
				}

				else if (dataVencimentoParcela.getTime().equals(dataHoje.getTime()) && !ccd.isParcelaPaga()) {
					ccd.setParcelaVencendo(true);
				}
				
				BigDecimal valorParcela = BigDecimal.ZERO;
				
				if(!CommonsUtil.semValor(ccd.getVlrJurosParcela())){
					valorParcela = valorParcela.add(ccd.getVlrJurosParcela());
				}
				
				if(!CommonsUtil.semValor(ccd.getVlrAmortizacaoParcela())){
					valorParcela = valorParcela.add(ccd.getVlrAmortizacaoParcela());
				}
				
				if(!CommonsUtil.semValor(ccd.getVlrParcela()) && CommonsUtil.semValor(valorParcela)){
					valorParcela = valorParcela.add(ccd.getVlrParcela());
				}
				
				if (ccd.isParcelaPaga()) {
					this.valorUltimaPareclaPaga = ccd.getVlrSaldoParcela();
					if(!CommonsUtil.mesmoValor(ccd.getNumeroParcela(), "Amortização") && !CommonsUtil.mesmoValor(ccd.getNumeroParcela(), "Acerto Saldo")) {
						this.prazoContrato = contrato.getQtdeParcelas() - CommonsUtil.intValue(ccd.getNumeroParcela());
					}
				} else if (ccd.isParcelaVencida()) {
					if(dataVencimentoParcela.after(dataVencimentoMínima)) {
						this.qtdDeparcelasVencidas++;
						valorVencido = valorVencido.add(ccd.getVlrParcela());
					}
				
					this.totalAVencer = this.totalAVencer.add(valorParcela);
				}  else {
					this.totalAVencer = this.totalAVencer.add(valorParcela);
				}
				
				if(CommonsUtil.mesmoValor(ccd.getDataVencimento().getMonth(), dataAtual.getMonth()) && CommonsUtil.mesmoValor(ccd.getDataVencimento().getYear(), dataAtual.getYear()) && !CommonsUtil.semValor(contrato.getValorImovel())) {
					ltv = ccd.getVlrSaldoParcela().divide(contrato.getValorImovel(), MathContext.DECIMAL128);
				}
			}
			
			CartorioDao dao = new CartorioDao();
			contrato.qtdParcelasAtraso = qtdDeparcelasVencidas;
			contrato.somaParcelasAtraso = valorVencido;
			contrato.ultimoCartorio = dao.consultaUltimoCartorio(contrato);
			
			if(CommonsUtil.mesmoValor(this.prazoContrato, 0) || CommonsUtil.mesmoValor(this.valorUltimaPareclaPaga, BigDecimal.ZERO)) {
				this.totalContratosConsultar--;
				this.valorUltimaPareclaPaga = BigDecimal.ZERO;
			} else {
				contratosGrafico.add(contrato);
				if(!CommonsUtil.semValor(contrato.getValorCCB())) {
					BigDecimal peso = BigDecimal.ZERO;
					BigDecimal valor = BigDecimal.ZERO;
					peso = contrato.getValorCCB().divide(BigDecimal.valueOf(100000), MathContext.DECIMAL128);
					somaPeso = somaPeso.add(peso);
					valor = BigDecimal.valueOf(prazoContrato * peso.doubleValue());
					prazoMedio = prazoMedio.add(valor);
				}
				
				ltvMedio = ltvMedio.add(ltv);
				
				if (prazoMax.compareTo(BigDecimal.valueOf(prazoContrato)) == -1){
					prazoMax = BigDecimal.valueOf(prazoContrato);
				}
				
				if (prazoMin.compareTo(BigDecimal.valueOf(prazoContrato)) == 1){
					prazoMin = BigDecimal.valueOf(prazoContrato);
					contratoPrazoMin.clear();
					contratoPrazoMin.add(contrato);
				} else if (prazoMin.compareTo(BigDecimal.valueOf(prazoContrato)) == 0){
					contratoPrazoMin.add(contrato);
				}
				
				if(contrato.isCorrigidoIPCA()) {
					if (taxaMaxIPCA.compareTo(contrato.getTxJurosParcelas()) == -1){
						taxaMaxIPCA = contrato.getTxJurosParcelas();
					}
					if (taxaMinIPCA.compareTo(contrato.getTxJurosParcelas()) == 1){
						taxaMinIPCA = contrato.getTxJurosParcelas();
					}					
				} else {
					if (taxaMax.compareTo(contrato.getTxJurosParcelas()) == -1){
						taxaMax = contrato.getTxJurosParcelas();
					}
					if (taxaMin.compareTo(contrato.getTxJurosParcelas()) == 1){
						taxaMin = contrato.getTxJurosParcelas();
					}					
				}
				
				if (ltvMax.compareTo(ltv) == -1){
					ltvMax = ltv;
				}
				if (ltvMin.compareTo(ltv) == 1){
					ltvMin = ltv;
				}
			}
			
			if(this.qtdDeparcelasVencidas == 1) {
				this.inadimplencia30Soma = this.inadimplencia30Soma.add(valorUltimaPareclaPaga);
				this.contratosInadimplencia30.add(contrato);
			} else if(this.qtdDeparcelasVencidas == 2) {
				this.inadimplencia60Soma = this.inadimplencia60Soma.add(valorUltimaPareclaPaga);
				this.contratosInadimplencia60.add(contrato);
			} else if(this.qtdDeparcelasVencidas >= 3) {
				this.inadimplencia90Soma = this.inadimplencia90Soma.add(valorUltimaPareclaPaga);
				this.contratosInadimplencia90.add(contrato);
			}
			
			this.volumeCarteira = this.volumeCarteira.add(valorUltimaPareclaPaga);
			
			if(this.prazoContrato <= 180) {
				this.somaContratos180 = this.somaContratos180.add(this.valorUltimaPareclaPaga);
			} else if(this.prazoContrato > 180) {
				this.somaContratos240 = this.somaContratos240.add(this.valorUltimaPareclaPaga);
			}
		}
		
		this.prazoMedio = prazoMedio.divide(BigDecimal.valueOf(totalContratosConsultar),  MathContext.DECIMAL128);
		this.taxaMedia = CalcularMediaCcbSemIPCA(contratos);
		this.taxaMediaIPCA = CalcularMediaCcbComIPCA(contratos);
		this.ltvMedio = ltvMedio.divide(BigDecimal.valueOf(totalContratosConsultar),  MathContext.DECIMAL128);
		
		this.ltvMedio = this.ltvMedio.multiply(BigDecimal.valueOf(100));
		this.ltvMax = this.ltvMax.multiply(BigDecimal.valueOf(100));
		this.ltvMin = this.ltvMin.multiply(BigDecimal.valueOf(100));
		
		this.prazoMedio = this.prazoMedio.divide(somaPeso, MathContext.DECIMAL128);
		this.prazoMedio = this.prazoMedio.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.ltvMedio = this.ltvMedio.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.ltvMax = this.ltvMax.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.ltvMin = this.ltvMin.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		this.inadimplencia30Porcentagem = this.inadimplencia30Soma.divide(this.volumeCarteira,  MathContext.DECIMAL128);
		this.inadimplencia30Porcentagem = this.inadimplencia30Porcentagem.multiply(BigDecimal.valueOf(100));
		this.inadimplencia30Porcentagem = this.inadimplencia30Porcentagem.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		this.inadimplencia60Porcentagem = this.inadimplencia60Soma.divide(this.volumeCarteira,  MathContext.DECIMAL128);
		this.inadimplencia60Porcentagem = this.inadimplencia60Porcentagem.multiply(BigDecimal.valueOf(100));
		this.inadimplencia60Porcentagem = this.inadimplencia60Porcentagem.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		this.inadimplencia90Porcentagem = this.inadimplencia90Soma.divide(this.volumeCarteira,  MathContext.DECIMAL128);
		this.inadimplencia90Porcentagem = this.inadimplencia90Porcentagem.multiply(BigDecimal.valueOf(100));
		this.inadimplencia90Porcentagem = this.inadimplencia90Porcentagem.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		this.porcentagem180 = this.somaContratos180.divide(this.volumeCarteira,  MathContext.DECIMAL128);
		this.porcentagem180 = this.porcentagem180.multiply(BigDecimal.valueOf(100));
		this.porcentagem180 = this.porcentagem180.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		this.porcentagem240 = this.somaContratos240.divide(this.volumeCarteira,  MathContext.DECIMAL128);
		this.porcentagem240 = this.porcentagem240.multiply(BigDecimal.valueOf(100));
		this.porcentagem240 = this.porcentagem240.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		prazoMax = prazoMax.add(BigDecimal.ONE);
		
	//	createStackedGroupBarModel();
	}
	
	public StreamedContent geraRelatorio() throws IOException{
		XSSFWorkbook wb = new XSSFWorkbook(getClass().getResourceAsStream("/resource/TabelaVazia.xlsx"));
		int iLinha = 0;
		int numeroLista = 1;
		
	        TimeZone zone = TimeZone.getDefault();
			Locale locale = new Locale("pt", "BR");
			Calendar dataHoje = Calendar.getInstance(zone, locale);
			dataHoje.set(Calendar.HOUR_OF_DAY, 0);
			dataHoje.set(Calendar.MINUTE, 0);
			dataHoje.set(Calendar.SECOND, 0);
			dataHoje.set(Calendar.MILLISECOND, 0);
			Date dataAtual = dataHoje.getTime();
		
		XSSFSheet sheet = wb.getSheetAt(0);
		
		XSSFRow linha = sheet.getRow(iLinha);
		if(linha == null) {
			sheet.createRow(iLinha);
			linha = sheet.getRow(iLinha);
		}
		
		gravaCelula(0, "Período", linha);
		gravaCelula(1, "Saldo Inicial", linha);
		gravaCelula(2, "Juros", linha);
		gravaCelula(3, "Amortização", linha);
		gravaCelula(4, "Valor Parcela", linha);
		gravaCelula(5, "Amortização Total", linha);

		BigDecimal saldoAnterior = BigDecimal.ZERO;
		BigDecimal saldoInicial = BigDecimal.ZERO;
		BigDecimal juros = BigDecimal.ZERO;
		BigDecimal amortizacao = BigDecimal.ZERO;
		BigDecimal valorParcela = BigDecimal.ZERO;
		BigDecimal amortizacaoTotal = BigDecimal.ZERO;
		
		int mesHoje = dataAtual.getMonth();
		int anoHoje = dataAtual.getYear();
		
		int mesOntem = mesHoje;
		mesOntem--;
		int anoOntem = anoHoje;
		
		if (mesOntem < 0) {
			mesOntem = 11;
			anoOntem--;
		}
		
		iLinha = 1;
		
		int i = 0;
		for (i = 0; i <= prazoMax.intValue() ; i++) {
			juros = BigDecimal.ZERO;
			amortizacao = BigDecimal.ZERO;
			valorParcela = BigDecimal.ZERO;
			
			for (ContratoCobranca contrato : this.contratosGrafico) {
				for (ContratoCobrancaDetalhes ccd : contrato.getListContratoCobrancaDetalhes()) {
					int mesVencimento = ccd.getDataVencimento().getMonth();
					int anoVencimetno = ccd.getDataVencimento().getYear();
					
					if(i==0) {
						if (CommonsUtil.mesmoValor(mesVencimento, mesOntem) && CommonsUtil.mesmoValor(anoVencimetno, anoOntem)) {
							saldoAnterior = ccd.getVlrSaldoParcela();
						}
					}
					
					if (CommonsUtil.mesmoValor(mesVencimento, mesHoje) && CommonsUtil.mesmoValor(anoVencimetno, anoHoje)) {
						if (i == 0) {
							if (!CommonsUtil.semValor(ccd.getVlrSaldoInicial())) {
								saldoInicial = saldoInicial.add(ccd.getVlrSaldoInicial());
							} else {
								saldoInicial = saldoInicial.add(saldoAnterior);
							}
						}
						juros = juros.add(ccd.getVlrJurosParcela());
						amortizacao = amortizacao.add(ccd.getVlrAmortizacaoParcela());
						amortizacaoTotal = amortizacaoTotal.add(ccd.getVlrAmortizacaoParcela());
						valorParcela = valorParcela.add(ccd.getVlrAmortizacaoParcela().add(ccd.getVlrJurosParcela()));
					}
				}
			}
			
			if(i == 0) {
				linha = sheet.getRow(iLinha);
				if(linha == null) {
					sheet.createRow(iLinha);
					linha = sheet.getRow(iLinha);
				}
				gravaCelula(0, 0, linha);
				gravaCelula(1, saldoInicial, linha);
				gravaCelula(2, BigDecimal.ZERO, linha);
				gravaCelula(3, BigDecimal.ZERO, linha);
				gravaCelula(4, BigDecimal.ZERO, linha);
				gravaCelula(5, BigDecimal.ZERO, linha);

				iLinha++;
			} 
			
			BigDecimal saldoInicial2 = saldoInicial.subtract(amortizacaoTotal);
			
			linha = sheet.getRow(iLinha);
			if(linha == null) {
				sheet.createRow(iLinha);
				linha = sheet.getRow(iLinha);
			}
			
			gravaCelula(0, numeroLista, linha);
			gravaCelula(1, saldoInicial2, linha);
			gravaCelula(2, juros, linha);
			gravaCelula(3, amortizacao, linha);
			gravaCelula(4, valorParcela, linha);
			gravaCelula(5, amortizacaoTotal, linha);

			iLinha++;
			
			numeroLista++;
			mesHoje++;
			if (mesHoje == 12) {
				mesHoje = 0;
				anoHoje++;
			}
		}       
					
		ByteArrayOutputStream  fileOut = new ByteArrayOutputStream ();
		//escrever tudo o que foi feito no arquivo
		wb.write(fileOut);

		//fecha a escrita de dados nessa planilha
		wb.close();
		
		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
				FacesContext.getCurrentInstance());
		
		String nomeArquivoDownload = String.format("Galleria Bank - Relatorio FIDC %s.xlsx", "");
		gerador.open(nomeArquivoDownload);
		gerador.feed( new ByteArrayInputStream(fileOut.toByteArray()));
		gerador.close();
		
		return null;
	}

	private BigDecimal CalcularMediaCcbSemIPCA(Collection<ContratoCobranca> lista) {		
		BigDecimal soma = BigDecimal.ZERO;
		BigDecimal somaPeso = BigDecimal.ZERO;
		for (ContratoCobranca coco : lista) {
			if(coco.isCorrigidoIPCA()) {
				continue;
			}
			BigDecimal peso = BigDecimal.ZERO;
			BigDecimal valor = BigDecimal.ZERO;
			BigDecimal taxa = coco.getTxJurosParcelas();
			BigDecimal valorContrato = coco.getValorCCB();
			if(!CommonsUtil.semValor(valorContrato) && !CommonsUtil.semValor(taxa)) {
				peso = valorContrato.divide(BigDecimal.valueOf(100000), MathContext.DECIMAL128);
				somaPeso = somaPeso.add(peso);
				valor = taxa.multiply(peso);
				soma = soma.add(valor);
			}
		}
		if(!CommonsUtil.semValor(somaPeso)) {
			BigDecimal media = soma.divide(somaPeso, MathContext.DECIMAL128);
			media = media.setScale(2, BigDecimal.ROUND_HALF_UP);
			return media;
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	private BigDecimal CalcularMediaCcbComIPCA(Collection<ContratoCobranca> lista) {		
		BigDecimal soma = BigDecimal.ZERO;
		BigDecimal somaPeso = BigDecimal.ZERO;
		for (ContratoCobranca coco : lista) {
			if(!coco.isCorrigidoIPCA()) {
				continue;
			}
			BigDecimal peso = BigDecimal.ZERO;
			BigDecimal valor = BigDecimal.ZERO;
			BigDecimal taxa = coco.getTxJurosParcelas();
			BigDecimal valorContrato = coco.getValorCCB();
			if(!CommonsUtil.semValor(valorContrato) && !CommonsUtil.semValor(taxa)) {
				peso = valorContrato.divide(BigDecimal.valueOf(100000), MathContext.DECIMAL128);
				somaPeso = somaPeso.add(peso);
				valor = taxa.multiply(peso);
				soma = soma.add(valor);
			}
		}
		if(!CommonsUtil.semValor(somaPeso)) {
			BigDecimal media = soma.divide(somaPeso, MathContext.DECIMAL128);
			media = media.setScale(2, BigDecimal.ROUND_HALF_UP);
			return media;
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	private BigDecimal CalcularPrazoMedio(Collection<ContratoCobranca> lista) {		
		BigDecimal soma = BigDecimal.ZERO;
		BigDecimal somaPeso = BigDecimal.ZERO;
		for (ContratoCobranca coco : lista) {
			if(coco.isCorrigidoIPCA()) {
				continue;
			}
			BigDecimal peso = BigDecimal.ZERO;
			BigDecimal valor = BigDecimal.ZERO;
			BigDecimal taxa = coco.getTxJurosParcelas();
			BigDecimal valorContrato = coco.getValorCCB();
			if(!CommonsUtil.semValor(valorContrato) && !CommonsUtil.semValor(taxa)) {
				peso = valorContrato.divide(BigDecimal.valueOf(100000), MathContext.DECIMAL128);
				somaPeso = somaPeso.add(peso);
				valor = taxa.multiply(peso);
				soma = soma.add(valor);
			}
		}
		if(!CommonsUtil.semValor(somaPeso)) {
			BigDecimal media = soma.divide(somaPeso, MathContext.DECIMAL128);
			media = media.setScale(2, BigDecimal.ROUND_HALF_UP);
			return media;
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	private void gravaCelula(Integer celula, BigDecimal value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		linha.getCell(celula).setCellValue(value.doubleValue());
	}
   
	private void gravaCelula(Integer celula, String value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		linha.getCell(celula).setCellValue(value);
	}
	
	private void gravaCelula(Integer celula, int value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		linha.getCell(celula).setCellValue(value);
	}

	public void createStackedGroupBarModel() {
		
        stackedGroupBarModel = new BarChartModel();
        ChartData data = new ChartData();
        BarChartDataSet barDataSet = new BarChartDataSet();
        
        barDataSet.setLabel("Carteira");
        barDataSet.setBackgroundColor("rgb(69, 138, 17)");
        barDataSet.setStack("Stack 0");
        List<Number> dataVal = new ArrayList<>();
        
        BarChartDataSet barDataSet2 = new BarChartDataSet();
        barDataSet2.setLabel("Amortização");
        barDataSet2.setBackgroundColor("rgb(63, 171, 236)");
        barDataSet2.setStack("Stack 0");
        List<Number> dataVal2 = new ArrayList<>();
        
        BarChartDataSet barDataSet3 = new BarChartDataSet();
       	barDataSet3.setLabel("Parcela");
        barDataSet3.setBackgroundColor("rgb(167, 06, 206)");
        barDataSet3.setStack("Stack 1");
        List<Number> dataVal3 = new ArrayList<>();
        
        List<String> labels = new ArrayList<>();
        
        TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		dataHoje.set(Calendar.HOUR_OF_DAY, 0);
		dataHoje.set(Calendar.MINUTE, 0);
		dataHoje.set(Calendar.SECOND, 0);
		dataHoje.set(Calendar.MILLISECOND, 0);
		Date dataAtual = dataHoje.getTime();
		
		BigDecimal totalAmortizado = BigDecimal.ZERO;
		BigDecimal volumeCarteiraGrafico = BigDecimal.ZERO;
		BigDecimal saldoAnterior = BigDecimal.ZERO;
		
		String numeroParcela = "";
		
		int mesHoje = dataAtual.getMonth();
		int anoHoje = dataAtual.getYear();
		
		int mesOntem = mesHoje;
		mesOntem--;
		int anoOntem = anoHoje;
		
		if (mesOntem < 0) {
			mesOntem = 11;
			anoOntem--;
		}
		
//		ContratoCobranca contrato = contratos.iterator().next();
		
		int i = 0;
		for (i = 0; i <= prazoMax.intValue(); i++) {
		//	volumeCarteiraGrafico = BigDecimal.ZERO;
		//	totalAmortizado = BigDecimal.ZERO;
			
			for (ContratoCobranca contrato : this.contratosGrafico) {
				for (ContratoCobrancaDetalhes ccd : contrato.getListContratoCobrancaDetalhes()) {
					int mesVencimento = ccd.getDataVencimento().getMonth();
					int anoVencimetno = ccd.getDataVencimento().getYear();
					
					if(i==0) {
						if (CommonsUtil.mesmoValor(mesVencimento, mesOntem) && CommonsUtil.mesmoValor(anoVencimetno, anoOntem)) {
							if(null == ccd.getVlrSaldoInicial()) {
								saldoAnterior = ccd.getVlrSaldoParcela();
							}
						}
					}
					
					if (CommonsUtil.mesmoValor(mesVencimento, mesHoje) && CommonsUtil.mesmoValor(anoVencimetno, anoHoje)) {
						numeroParcela = ccd.getNumeroParcela();
						if(i==0) {
							if (!CommonsUtil.semValor(ccd.getVlrSaldoInicial())) {
								volumeCarteiraGrafico = volumeCarteiraGrafico.add(ccd.getVlrSaldoInicial());
							} else {
								volumeCarteiraGrafico = volumeCarteiraGrafico.add(saldoAnterior);
							}
						}
						totalAmortizado = totalAmortizado.add(ccd.getVlrAmortizacaoParcela());
					}
				}
			}
				
			if(i == 0) {
				dataVal.add(volumeCarteiraGrafico);
				dataVal2.add(BigDecimal.ZERO);
				labels.add(CommonsUtil.stringValue(0));
			}
			
			BigDecimal volumeCarteiraGrafico2 = volumeCarteiraGrafico.subtract(totalAmortizado);
	//		BigDecimal volumeCarteiraGrafico2 = volumeCarteiraGrafico;
			int j = i;
			j++;
			dataVal.add(volumeCarteiraGrafico2);
			dataVal2.add(totalAmortizado);
			dataVal3.add(CommonsUtil.intValue(numeroParcela));
			labels.add(CommonsUtil.stringValue(j));
			
			mesHoje++;
			if (mesHoje == 12) {
				mesHoje = 0;
				anoHoje++;
			}
		}       
        
        barDataSet.setData(dataVal);
        barDataSet2.setData(dataVal2);
        barDataSet3.setData(dataVal3);
        
        
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setStacked(true);
        linearAxes.setOffset(true);
        cScales.addXAxesData(linearAxes);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Bar Chart - Stacked Group");
        options.setTitle(title);

        Tooltip tooltip = new Tooltip();
        tooltip.setMode("index");
        tooltip.setIntersect(false);
        options.setTooltip(tooltip);

        stackedGroupBarModel.setOptions(options);

        data.addChartDataSet(barDataSet);
        data.addChartDataSet(barDataSet2);
        data.setLabels(labels);

        stackedGroupBarModel.setData(data);
    }

	
	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public int getTotalContratosConsultar() {
		return totalContratosConsultar;
	}

	public void setTotalContratosConsultar(int totalContratosConsultar) {
		this.totalContratosConsultar = totalContratosConsultar;
	}

	public int getPrazoContrato() {
		return prazoContrato;
	}

	public void setPrazoContrato(int prazoContrato) {
		this.prazoContrato = prazoContrato;
	}

	public BigDecimal getValorUltimaPareclaPaga() {
		return valorUltimaPareclaPaga;
	}

	public void setValorUltimaPareclaPaga(BigDecimal valorUltimaPareclaPaga) {
		this.valorUltimaPareclaPaga = valorUltimaPareclaPaga;
	}

	public BigDecimal getVolumeCarteira() {
		return volumeCarteira;
	}

	public void setVolumeCarteira(BigDecimal volumeCarteira) {
		this.volumeCarteira = volumeCarteira;
	}

	public BigDecimal getSomaContratos180() {
		return somaContratos180;
	}

	public void setSomaContratos180(BigDecimal somaContratos180) {
		this.somaContratos180 = somaContratos180;
	}

	public BigDecimal getSomaContratos240() {
		return somaContratos240;
	}

	public void setSomaContratos240(BigDecimal somaContratos240) {
		this.somaContratos240 = somaContratos240;
	}

	public BigDecimal getPorcentagem180() {
		return porcentagem180;
	}

	public void setPorcentagem180(BigDecimal porcentagem180) {
		this.porcentagem180 = porcentagem180;
	}

	public BigDecimal getPorcentagem240() {
		return porcentagem240;
	}

	public void setPorcentagem240(BigDecimal porcentagem240) {
		this.porcentagem240 = porcentagem240;
	}

	public int getQtdDeparcelasVencidas() {
		return qtdDeparcelasVencidas;
	}

	public void setQtdDeparcelasVencidas(int qtdDeparcelasVencidas) {
		this.qtdDeparcelasVencidas = qtdDeparcelasVencidas;
	}

	public BigDecimal getInadimplencia30Soma() {
		return inadimplencia30Soma;
	}

	public void setInadimplencia30Soma(BigDecimal inadimplencia30Soma) {
		this.inadimplencia30Soma = inadimplencia30Soma;
	}

	public BigDecimal getInadimplencia60Soma() {
		return inadimplencia60Soma;
	}

	public void setInadimplencia60Soma(BigDecimal inadimplencia60Soma) {
		this.inadimplencia60Soma = inadimplencia60Soma;
	}

	public BigDecimal getInadimplencia90Soma() {
		return inadimplencia90Soma;
	}

	public void setInadimplencia90Soma(BigDecimal inadimplencia90Soma) {
		this.inadimplencia90Soma = inadimplencia90Soma;
	}

	public BigDecimal getPrazoMax() {
		return prazoMax;
	}

	public void setPrazoMax(BigDecimal prazoMax) {
		this.prazoMax = prazoMax;
	}

	public BigDecimal getPrazoMedio() {
		return prazoMedio;
	}

	public void setPrazoMedio(BigDecimal prazoMedio) {
		this.prazoMedio = prazoMedio;
	}

	public BigDecimal getPrazoMin() {
		return prazoMin;
	}

	public void setPrazoMin(BigDecimal prazoMin) {
		this.prazoMin = prazoMin;
	}

	public BigDecimal getTaxaMax() {
		return taxaMax;
	}

	public void setTaxaMax(BigDecimal taxaMax) {
		this.taxaMax = taxaMax;
	}

	public BigDecimal getTaxaMedia() {
		return taxaMedia;
	}

	public void setTaxaMedia(BigDecimal taxaMedia) {
		this.taxaMedia = taxaMedia;
	}

	public BigDecimal getTaxaMin() {
		return taxaMin;
	}

	public void setTaxaMin(BigDecimal taxaMin) {
		this.taxaMin = taxaMin;
	}

	public BigDecimal getTaxaMaxIPCA() {
		return taxaMaxIPCA;
	}

	public void setTaxaMaxIPCA(BigDecimal taxaMaxIPCA) {
		this.taxaMaxIPCA = taxaMaxIPCA;
	}

	public BigDecimal getTaxaMediaIPCA() {
		return taxaMediaIPCA;
	}

	public void setTaxaMediaIPCA(BigDecimal taxaMediaIPCA) {
		this.taxaMediaIPCA = taxaMediaIPCA;
	}

	public BigDecimal getTaxaMinIPCA() {
		return taxaMinIPCA;
	}

	public void setTaxaMinIPCA(BigDecimal taxaMinIPCA) {
		this.taxaMinIPCA = taxaMinIPCA;
	}

	public BigDecimal getLtvMax() {
		return ltvMax;
	}

	public void setLtvMax(BigDecimal ltvMax) {
		this.ltvMax = ltvMax;
	}

	public BigDecimal getLtvMedio() {
		return ltvMedio;
	}

	public void setLtvMedio(BigDecimal ltvMedio) {
		this.ltvMedio = ltvMedio;
	}

	public BigDecimal getLtvMin() {
		return ltvMin;
	}

	public void setLtvMin(BigDecimal ltvMin) {
		this.ltvMin = ltvMin;
	}

	public BigDecimal getInadimplencia30Porcentagem() {
		return inadimplencia30Porcentagem;
	}

	public void setInadimplencia30Porcentagem(BigDecimal inadimplencia30Porcentagem) {
		this.inadimplencia30Porcentagem = inadimplencia30Porcentagem;
	}

	public BigDecimal getInadimplencia60Porcentagem() {
		return inadimplencia60Porcentagem;
	}

	public void setInadimplencia60Porcentagem(BigDecimal inadimplencia60Porcentagem) {
		this.inadimplencia60Porcentagem = inadimplencia60Porcentagem;
	}

	public BigDecimal getInadimplencia90Porcentagem() {
		return inadimplencia90Porcentagem;
	}

	public void setInadimplencia90Porcentagem(BigDecimal inadimplencia90Porcentagem) {
		this.inadimplencia90Porcentagem = inadimplencia90Porcentagem;
	}

	public Collection<ContratoCobranca> getContratosInadimplencia30() {
		return contratosInadimplencia30;
	}

	public void setContratosInadimplencia30(Collection<ContratoCobranca> contratosInadimplencia30) {
		this.contratosInadimplencia30 = contratosInadimplencia30;
	}

	public Collection<ContratoCobranca> getContratosInadimplencia60() {
		return contratosInadimplencia60;
	}

	public void setContratosInadimplencia60(Collection<ContratoCobranca> contratosInadimplencia60) {
		this.contratosInadimplencia60 = contratosInadimplencia60;
	}

	public Collection<ContratoCobranca> getContratosInadimplencia90() {
		return contratosInadimplencia90;
	}

	public void setContratosInadimplencia90(Collection<ContratoCobranca> contratosInadimplencia90) {
		this.contratosInadimplencia90 = contratosInadimplencia90;
	}

	public Collection<ContratoCobranca> getContratoPrazoMin() {
		return contratoPrazoMin;
	}

	public void setContratoPrazoMin(Collection<ContratoCobranca> contratoPrazoMin) {
		this.contratoPrazoMin = contratoPrazoMin;
	}

	public Collection<ContratoCobranca> getContratosGrafico() {
		return contratosGrafico;
	}

	public void setContratosGrafico(Collection<ContratoCobranca> contratosGrafico) {
		this.contratosGrafico = contratosGrafico;
	}

	public BigDecimal getTotalAVencer() {
		return totalAVencer;
	}

	public void setTotalAVencer(BigDecimal totalAVencer) {
		this.totalAVencer = totalAVencer;
	}

	public BarChartModel getStackedGroupBarModel() {
		return stackedGroupBarModel;
	}

	public void setStackedGroupBarModel(BarChartModel stackedGroupBarModel) {
		this.stackedGroupBarModel = stackedGroupBarModel;
	}

	public Collection<ContratoCobranca> getContratos() {
		return contratos;
	}

	public void setContratos(Collection<ContratoCobranca> contratos) {
		this.contratos = contratos;
	}
	
}