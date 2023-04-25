package com.webnowbr.siscoat.relatorio.mb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import com.webnowbr.siscoat.cobranca.db.model.CcbProcessosJudiciais;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.op.DashboardDao;
import com.webnowbr.siscoat.cobranca.db.op.RelatorioSemestralDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.relatorio.vo.DataCalculoDebentures;
import com.webnowbr.siscoat.relatorio.vo.DebenturesRelatorio;
import com.webnowbr.siscoat.relatorio.vo.RelatorioSemestre;
import com.webnowbr.siscoat.relatorio.vo.SaquesDebentures;
import com.webnowbr.siscoat.seguro.vo.SeguroTabelaVO;

/** ManagedBean. */
@ManagedBean(name = "relatorioSemestralMB")
@SessionScoped
public class RelatorioSemestralMB {

	private List<RelatorioSemestre> listRelatorioReceber;
	private List<RelatorioSemestre> listRelatorioPagar;
	private String tipoPesquisaRelatorio;
	
	private BigDecimal valorAntigo;
	private BigDecimal taxaMensal;
	private Date dataInicio;
	private Date dataFim;
	private boolean pesquisaEnvelope = false;

	private Date dataCalculo;
	private BigDecimal valorPresenteParcela;
	
	private Date dataInicio2;
	private Date dataFim2;

	private List<DataCalculoDebentures> listaDatasCalculo = new ArrayList<DataCalculoDebentures>();
	private List<DebenturesRelatorio> listaDebentures = new ArrayList<DebenturesRelatorio>();
	private List<DebenturesRelatorio> listaDebenturesMensal = new ArrayList<DebenturesRelatorio>();
	private List<DebenturesRelatorio> listaDebenturesNaoMensal = new ArrayList<DebenturesRelatorio>();
	
	private int maiorQtdSaque = 0;

	public void geraRelatorio() {

		RelatorioSemestralDao rDao = new RelatorioSemestralDao();
		if (CommonsUtil.mesmoValor(this.getTipoPesquisaRelatorio(), "Receber")) {
			listRelatorioReceber = rDao.listaRelatorioReceber();
		} else if (CommonsUtil.mesmoValor(this.getTipoPesquisaRelatorio(), "Pagar")) {
			listRelatorioPagar = rDao.listaRelatorioPagar();
		} else if (CommonsUtil.mesmoValor(this.getTipoPesquisaRelatorio(), "Todos")) {
			listRelatorioPagar = rDao.listaRelatorioPagar();
			listRelatorioReceber = rDao.listaRelatorioReceber();
		}
	}
	
	public String clearFields(){
		this.tipoPesquisaRelatorio = "";
		this.listRelatorioReceber = new ArrayList<RelatorioSemestre>(0);
		this.listRelatorioPagar = new ArrayList<RelatorioSemestre>(0);
		return "/Relatorios/Pagamentos/RelatorioSemestral.xhtml";
	}
	
	
	public String clearFieldsDebentures(){
		
		return "/Relatorios/Pagamentos/RelatorioDebentures.xhtml";
	}
	
	

	public StreamedContent readXLSXFileRelatorioSemestreReceber() throws IOException {

		// String sheetName
		// =getClass().getResource("/resource/SeguroDFI.xlsx").getPath();
		XSSFWorkbook wb = new XSSFWorkbook(getClass().getResourceAsStream("/resource/TabelaVazia.xlsx"));

		XSSFSheet sheet = wb.getSheetAt(0);

		int iLinha = 0;

		XSSFRow linha = sheet.getRow(iLinha);
		if (linha == null) {
			sheet.createRow(iLinha);
			linha = sheet.getRow(iLinha);
		}

		gravaCelula(0, "N° Contrato", linha);
		gravaCelula(1, "Pagador", linha);
		gravaCelula(2, "Data de Vencimento da Parcela", linha);
		gravaCelula(3, "Valor da parcela", linha);
		gravaCelula(4, "Taxa", linha);
		gravaCelula(5, "Índice", linha);
		//gravaCelula(6, "Índice NOVO", linha);
		gravaCelula(6, "Empresa", linha);

		iLinha++;

		for (int iRelatorio = 0; iRelatorio < this.listRelatorioReceber.size(); iRelatorio++) {
			RelatorioSemestre relatorio = this.listRelatorioReceber.get(iRelatorio);

			linha = sheet.getRow(iLinha);
			if (linha == null) {
				sheet.createRow(iLinha);
				linha = sheet.getRow(iLinha);
			}

			gravaCelula(0, relatorio.getNumeroContratoRelatorio(), linha);
			gravaCelula(1, relatorio.getNomePagadorRelatorio(), linha);
			gravaCelula(2, relatorio.getDataVencimentoRelatorio(), linha);
			gravaCelula(3, relatorio.getValorContratoRelatorio(), linha);
			gravaCelula(4, relatorio.getTaxaContratoRelatorio(), linha);
			gravaCelula(5, relatorio.getIndiceContratoRelatorio(), linha);
			//gravaCelula(6, relatorio.getIndiceNovoContratoRelatorio(), linha);
			gravaCelula(6, relatorio.getEmpresaContratoRelatorio(), linha);

			iLinha++;
		}

		// FileOutputStream fileOut = new FileOutputStream("c:\\TabelaSeguroDFI.xlsx");

		ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
		// escrever tudo o que foi feito no arquivo
		wb.write(fileOut);

		// fecha a escrita de dados nessa planilha
		wb.close();

		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
				FacesContext.getCurrentInstance());

		gerador.open(String.format("Galleria Bank - ParcelasReceber %s.xlsx", ""));
		gerador.feed(new ByteArrayInputStream(fileOut.toByteArray()));
		gerador.close();

		return null;

	}

	public StreamedContent readXLSXFileRelatorioSemestrePagar() throws IOException {

		// String sheetName
		// =getClass().getResource("/resource/SeguroDFI.xlsx").getPath();
		XSSFWorkbook wb = new XSSFWorkbook(getClass().getResourceAsStream("/resource/TabelaVazia.xlsx"));

		XSSFSheet sheet = wb.getSheetAt(0);

		int iLinha = 0;

		XSSFRow linha = sheet.getRow(iLinha);
		if (linha == null) {
			sheet.createRow(iLinha);
			linha = sheet.getRow(iLinha);
		}

		gravaCelula(0, "N° Contrato", linha);
		gravaCelula(1, "Pagador", linha);
		gravaCelula(2, "Data de Vencimento da Parcela", linha);
		gravaCelula(3, "Valor da parcela", linha);
		gravaCelula(4, "Amortização", linha);
		gravaCelula(5, "Capitalização", linha);
		gravaCelula(6, "Taxa Contrato", linha);
		gravaCelula(7, "Taxa Investidor", linha);
		gravaCelula(8, "Índice", linha);
		gravaCelula(9, "Empresa", linha);
		gravaCelula(10, "Tipo Pagador", linha);

		iLinha++;

		for (int iRelatorio = 0; iRelatorio < this.listRelatorioPagar.size(); iRelatorio++) {
			RelatorioSemestre relatorio = this.listRelatorioPagar.get(iRelatorio);

			linha = sheet.getRow(iLinha);
			if (linha == null) {
				sheet.createRow(iLinha);
				linha = sheet.getRow(iLinha);
			}

			gravaCelula(0, relatorio.getNumeroContratoRelatorio(), linha);
			gravaCelula(1, relatorio.getNomePagadorRelatorio(), linha);
			gravaCelula(2, relatorio.getDataVencimentoRelatorio(), linha);
			gravaCelula(3, relatorio.getValorContratoRelatorio(), linha);
			gravaCelula(4, relatorio.getValorAmortizacao(), linha);
			gravaCelula(5, relatorio.getValorCapitalizacao(), linha);
			gravaCelula(6, relatorio.getTaxaContratoRelatorio(), linha);
			gravaCelula(7, relatorio.getTaxaInvestidor(), linha);
			gravaCelula(8, relatorio.getIndiceContratoRelatorio(), linha);
			gravaCelula(9, relatorio.getEmpresaContratoRelatorio(), linha);
			gravaCelula(10, relatorio.getTipoPagadorRelatorio(), linha);

			iLinha++;
		}

		// FileOutputStream fileOut = new FileOutputStream("c:\\TabelaSeguroDFI.xlsx");

		ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
		// escrever tudo o que foi feito no arquivo
		wb.write(fileOut);

		// fecha a escrita de dados nessa planilha
		wb.close();

		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
				FacesContext.getCurrentInstance());

		gerador.open(String.format("Galleria Bank - ParcelasPagar %s.xlsx", ""));
		gerador.feed(new ByteArrayInputStream(fileOut.toByteArray()));
		gerador.close();

		return null;

	}

	private void gravaCelula(Integer celula, String value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		linha.getCell(celula).setCellValue(value);
	}

	private void gravaCelula(Integer celula, BigDecimal value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		if (value != null) {
			linha.getCell(celula).setCellValue(value.doubleValue());
		}
	}

	private void gravaCelula(Integer celula, Date value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		Locale locale = new Locale("pt", "BR");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", locale);
		String valueStr = "";
		if(!CommonsUtil.semValor(value)) {
			valueStr = sdf.format(value.getTime());
		}
		linha.getCell(celula).setCellValue(valueStr);
	}

	private void gravaCelula(Integer celula, int value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		linha.getCell(celula).setCellValue(value);
	}

	
	public void consultarDebentures() {
		RelatorioSemestralDao rsDao = new RelatorioSemestralDao();
		listaDebentures.clear();
		listaDebenturesNaoMensal.clear();
		listaDebenturesMensal.clear();
		
		listaDebenturesNaoMensal.addAll(rsDao.getDebenturesNaoMensal(dataInicio2, dataFim2, pesquisaEnvelope, listaDatasCalculo));
		listaDebenturesMensal.addAll(rsDao.getDebenturesMensal(dataInicio2, dataFim2, pesquisaEnvelope, listaDatasCalculo));
	}
	
	public void calcularDebentures() {
		for(DebenturesRelatorio debenturista : listaDebenturesNaoMensal) {
			
			BigDecimal juros = debenturista.getTaxaMensal();
			juros = juros.divide(BigDecimal.valueOf(100));
			juros = juros.add(BigDecimal.ONE);	
			BigDecimal potencia = BigDecimal.ONE.divide(BigDecimal.valueOf(30), MathContext.DECIMAL128);
			double jurosDiario = Math.pow(CommonsUtil.doubleValue(juros), CommonsUtil.doubleValue(potencia));
			debenturista.setTaxaDiaria(CommonsUtil.bigDecimalValue(jurosDiario).subtract(BigDecimal.ONE));
			
			if(maiorQtdSaque < debenturista.getSaques().size()) {
				maiorQtdSaque = debenturista.getSaques().size();
			}
			
			for(DataCalculoDebentures data : debenturista.getCalculos()) {
				data.setValor(calcularValorPresenteDiario(
						debenturista.getValorFace(), jurosDiario, 
						debenturista.getDataInicio(), data.getDataCalculo()));
			}
			
			for(SaquesDebentures saque : debenturista.getSaques()) {
				for(DataCalculoDebentures dataSaques : saque.getCalculos()) {	
					//jurosDiario = debenturista.getTaxaDiaria().doubleValue();
					dataSaques.setValor(calcularValorPresenteDiario(
							saque.getValorSaque(), jurosDiario, 
							saque.getDataSaque(), dataSaques.getDataCalculo()).negate());
				}
			}
			
			for(DataCalculoDebentures data2 : debenturista.getCalculos()) {
				BigDecimal totalSaque = BigDecimal.ZERO;
				for(SaquesDebentures saque : debenturista.getSaques()) {
					for(DataCalculoDebentures dataSaque : saque.getCalculos()) {	
						if(CommonsUtil.mesmoValor(data2.getDataCalculo(), dataSaque.getDataCalculo())) {
							totalSaque = totalSaque.add(dataSaque.getValor());
						}
					}
				}
				DataCalculoDebentures dataTotal = new DataCalculoDebentures();
				dataTotal.setDataCalculo(data2.getDataCalculo());
				dataTotal.setValor(data2.getValor().add(totalSaque));
				debenturista.getTotal().add(dataTotal);
			}
		}
		
		for(DebenturesRelatorio debenturista : listaDebenturesMensal) {
			
			BigDecimal juros = debenturista.getTaxaMensal();
			juros = juros.divide(BigDecimal.valueOf(100));
			juros = juros.add(BigDecimal.ONE);	
			BigDecimal potencia = BigDecimal.ONE.divide(BigDecimal.valueOf(30), MathContext.DECIMAL128);
			double jurosDiario = Math.pow(CommonsUtil.doubleValue(juros), CommonsUtil.doubleValue(potencia));
			debenturista.setTaxaDiaria(CommonsUtil.bigDecimalValue(jurosDiario).subtract(BigDecimal.ONE));	
			
			if(maiorQtdSaque < debenturista.getSaques().size()) {
				maiorQtdSaque = debenturista.getSaques().size();
			}

			for(SaquesDebentures saque : debenturista.getSaques()) {
				for(DataCalculoDebentures dataSaques : saque.getCalculos()) {	
					dataSaques.setValor(calcularValorPresenteDiario(
					saque.getValorSaque(), jurosDiario, 
					saque.getDataSaque(), dataSaques.getDataCalculo()));
				}
			}
			
			for(DataCalculoDebentures data2 : debenturista.getCalculos()) {
				BigDecimal totalSaque = BigDecimal.ZERO;
				for(SaquesDebentures saque : debenturista.getSaques()) {
					for(DataCalculoDebentures dataSaque : saque.getCalculos()) {	
						if(CommonsUtil.mesmoValor(data2.getDataCalculo(), dataSaque.getDataCalculo())) {
							totalSaque = totalSaque.add(dataSaque.getValor());
						}
					}
				}
				DataCalculoDebentures dataTotal = new DataCalculoDebentures();
				dataTotal.setDataCalculo(data2.getDataCalculo());
				dataTotal.setValor(data2.getValor().add(totalSaque));
				debenturista.getTotal().add(dataTotal);
			}
		}
		
		listaDebentures.addAll(listaDebenturesNaoMensal);
		listaDebentures.addAll(listaDebenturesMensal);
	}
	
	public StreamedContent readXLSXFile() throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook(getClass().getResourceAsStream("/resource/TabelaVazia.xlsx"));
		XSSFSheet sheet = wb.getSheetAt(0);
		DashboardDao dDao = new DashboardDao();
		
		XSSFRow linha = sheet.getRow(0);
		if(linha == null) {
			sheet.createRow(0);
			linha = sheet.getRow(0);
		}
		
		int colunasSaque = maiorQtdSaque * (2 + listaDatasCalculo.size());  
		
		gravaCelula(0, "Investidor", linha);
		gravaCelula(1, "CPF/CNPJ", linha);
		gravaCelula(2, "Agencia/Conta", linha);
		gravaCelula(3, "Contrato", linha);
		gravaCelula(4, "Garantido", linha);	
		gravaCelula(5, "Data Vencimento", linha);
		gravaCelula(6, "Taxa Remuneração", linha);
		gravaCelula(7, "Recebe Juros Mensal?", linha);
		gravaCelula(8, "Valor Bruto da Parcela", linha);
		gravaCelula(9, "Valor Líquido a Receber", linha);
		gravaCelula(10, "Taxa Diária", linha);
		gravaCelula(11, "Valor Face", linha);
		gravaCelula(12, "Data Inicio", linha);
		//gravaCelula(13, "Valor Em:", linha);
		int icoluna = 12;
		for(int j = 0; j < listaDatasCalculo.size();) {
			j++; 
			icoluna++;
			gravaCelula(icoluna, "Valor Em:", linha);
		}
			
		while(icoluna < colunasSaque + 12 + listaDatasCalculo.size()) {
			icoluna++;
			gravaCelula(icoluna, "Valor Saque", linha);
			icoluna++;
			gravaCelula(icoluna, "Data Saque", linha);
			for(int j = 0; j < listaDatasCalculo.size();) {
				j++; 
				icoluna++;
				gravaCelula(icoluna, "Valor Em:", linha);
			}
		}
		for(int j = 0; j < listaDatasCalculo.size(); j++) {
			//j++; 
			icoluna++;
			gravaCelula(icoluna, "Saldo em " +  CommonsUtil.formataData(listaDatasCalculo.get(j).getDataCalculo()), linha);
		}
		///////acabou cabeçalho, começa sub cabeçalho
		int iLinha = 1;	
		linha = sheet.getRow(iLinha);
		if(linha == null) {
			sheet.createRow(iLinha);
			linha = sheet.getRow(iLinha);
		}
		icoluna = 12;
		gravaCelula(6, "Mensal", linha);
		gravaCelula(11, "TOTAL_FACE", linha);
		
		for(int j = 0; j < listaDatasCalculo.size(); j++) {
			//j++; 
			icoluna++;
			gravaCelula(icoluna, CommonsUtil.formataData(listaDatasCalculo.get(j).getDataCalculo()), linha);
		}
			
		while(icoluna < colunasSaque + 12 + listaDatasCalculo.size()) {
			icoluna++;
			gravaCelula(icoluna, "", linha);
			icoluna++;
			gravaCelula(icoluna, "", linha);
			for(int j = 0; j < listaDatasCalculo.size(); j++) {
				//j++; 
				icoluna++;
				gravaCelula(icoluna, CommonsUtil.formataData(listaDatasCalculo.get(j).getDataCalculo()), linha);
			}
		}
		
		int inicioColunaTotalSaldo = icoluna;
		for(int j = 0; j < listaDatasCalculo.size();) {
			j++; 
			icoluna++;
			gravaCelula(icoluna, "TOTAL_SALDO", linha);
		}
		
		///////acabou sub cabeçalho, começa os debenturistas	
		BigDecimal totalFace = BigDecimal.ZERO;
		iLinha = 2;	
		for (DebenturesRelatorio debenture : listaDebentures) {	
			icoluna = 12;
			linha = sheet.getRow(iLinha);
			if(linha == null) {
				sheet.createRow(iLinha);
				linha = sheet.getRow(iLinha);
			}
			gravaCelula(0, debenture.getNome(), linha);
			gravaCelula(1, debenture.getCpfCnpj(), linha);
			gravaCelula(2, debenture.getAgenciaConta(), linha);
			gravaCelula(3, debenture.getNumerocontrato(), linha);
			gravaCelula(4, debenture.getGarantido(), linha);	
			gravaCelula(5, debenture.getDataVencimento(), linha);
			gravaCelula(6, debenture.getTaxaMensal(), linha);
			gravaCelula(7, debenture.getRecebeMensal(), linha);			
			gravaCelula(8, debenture.getValorBruto(), linha);
			gravaCelula(9, debenture.getValorLiquido(), linha);
			gravaCelula(10, debenture.getTaxaDiaria(), linha);
			gravaCelula(11, debenture.getValorFace(), linha);
			gravaCelula(12, debenture.getDataInicio(), linha);		
			icoluna = 12;
			for(int j = 0; j < listaDatasCalculo.size();j++) {
				//j++; 
				icoluna++;
				gravaCelula(icoluna, CommonsUtil.formataValorMonetario(debenture.getCalculos().get(j).getValor()), linha);
			}
			int aux = 0;
			while(icoluna < (colunasSaque + 12 + listaDatasCalculo.size())) {
				icoluna++;
				if(debenture.getSaques().size() > aux) {
					gravaCelula(icoluna, CommonsUtil.formataValorMonetario(debenture.getSaques().get(aux).getValorSaque()), linha);
				}
				icoluna++;
				if(debenture.getSaques().size() > aux) {
					gravaCelula(icoluna, CommonsUtil.formataData(debenture.getSaques().get(aux).getDataSaque()), linha);
				}
				for(int j = 0; j < listaDatasCalculo.size();j++) {
					//j++; 
					icoluna++;
					if(debenture.getSaques().size() > aux) {
						gravaCelula(icoluna, 
								CommonsUtil.formataValorMonetario(debenture.getSaques().get(aux).getCalculos().get(j).getValor()),
								linha);
					}
				}
				aux++;
			}
			for(int j = 0; j < listaDatasCalculo.size();j++) {
				//j++; 
				icoluna++;
				gravaCelula(icoluna, 
						CommonsUtil.formataValorMonetario(debenture.getTotal().get(j).getValor()),
						linha);
				listaDatasCalculo.get(j).setValor(listaDatasCalculo.get(j).getValor().add(debenture.getTotal().get(j).getValor()));
			}
			
			totalFace = totalFace.add(debenture.getValorFace());
			
			iLinha++;
		}
		
		gravaCelula(11, CommonsUtil.formataValorMonetario(totalFace), sheet.getRow(1));
		
		icoluna = inicioColunaTotalSaldo;
		for(int j = 0; j < listaDatasCalculo.size(); j++) {
			icoluna++;
			gravaCelula(icoluna, CommonsUtil.formataValorMonetario(listaDatasCalculo.get(j).getValor()), sheet.getRow(1));
		}
		
		ByteArrayOutputStream  fileOut = new ByteArrayOutputStream ();
		//escrever tudo o que foi feito no arquivo
		
		wb.write(fileOut);

		//fecha a escrita de dados nessa planilha
		wb.close();
		
		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
				FacesContext.getCurrentInstance());
		
		gerador.open(String.format("Galleria Bank - Debentures %s.xlsx", ""));
		gerador.feed( new ByteArrayInputStream(fileOut.toByteArray()));
		gerador.close();

		return null;		
	}
	
	public StreamedContent consultarCalcularBaixar() throws IOException {
		consultarDebentures();
		calcularDebentures();
		return readXLSXFile();
	}
	
	public void removeData(DataCalculoDebentures data) {
		this.listaDatasCalculo.remove(data);
	}
	
	public void addValorData() {
		this.listaDatasCalculo.add(new DataCalculoDebentures(dataCalculo));
		dataCalculo = null;
	}
	
	public void calcularValorPresenteParcela() { //caculo de valor presente com mutiply no final. Versão simplificada do dario
		BigDecimal juros = taxaMensal;
		BigDecimal saldo = valorAntigo;
		BigDecimal quantidadeDeMeses = BigDecimal.ONE;

		quantidadeDeMeses = BigDecimal.valueOf(DateUtil.Days360(dataInicio, dataFim));	
		quantidadeDeMeses = quantidadeDeMeses.divide(BigDecimal.valueOf(30), MathContext.DECIMAL128);
		Double quantidadeDeMesesDouble = CommonsUtil.doubleValue(quantidadeDeMeses);
		
		juros = juros.divide(BigDecimal.valueOf(100));
		juros = juros.add(BigDecimal.ONE);	
		double divisor = Math.pow(CommonsUtil.doubleValue(juros), quantidadeDeMesesDouble);
	
		this.valorPresenteParcela = (saldo).multiply(CommonsUtil.bigDecimalValue(divisor) , MathContext.DECIMAL128);
		this.valorPresenteParcela = this.valorPresenteParcela.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public void calcularValorPresenteDiario() {
		BigDecimal juros = taxaMensal;
		BigDecimal saldo = valorAntigo;
		BigDecimal quantidadeDeMeses = BigDecimal.ONE;
		
		quantidadeDeMeses = BigDecimal.valueOf(DateUtil.Days360(dataInicio, dataFim));	
		Double quantidadeDeMesesDouble = CommonsUtil.doubleValue(quantidadeDeMeses);
		
		juros = juros.divide(BigDecimal.valueOf(100));
		juros = juros.add(BigDecimal.ONE);	
		BigDecimal potencia = BigDecimal.ONE.divide(BigDecimal.valueOf(30), MathContext.DECIMAL128);
		double jurosDiario = Math.pow(CommonsUtil.doubleValue(juros), CommonsUtil.doubleValue(potencia));
		double jurosTotal = Math.pow(jurosDiario, quantidadeDeMesesDouble);
	
		this.valorPresenteParcela = (saldo).multiply(CommonsUtil.bigDecimalValue(jurosTotal), MathContext.DECIMAL128);
		this.valorPresenteParcela = this.valorPresenteParcela.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public BigDecimal calcularValorPresenteDiario(BigDecimal valor, double jurosDiario, Date dataInicio, Date dataFim) {
		//BigDecimal juros = taxaMensal;
		BigDecimal saldo = valor;
		BigDecimal quantidadeDeMeses = BigDecimal.ONE;
		if(dataFim.before(dataInicio)) {
			return BigDecimal.ZERO;
		}
		quantidadeDeMeses = BigDecimal.valueOf(DateUtil.Days360(dataInicio, dataFim));	
		Double quantidadeDeMesesDouble = CommonsUtil.doubleValue(quantidadeDeMeses);
		
		//juros = juros.divide(BigDecimal.valueOf(100));
		//juros = juros.add(BigDecimal.ONE);	
		//BigDecimal potencia = BigDecimal.ONE.divide(BigDecimal.valueOf(30), MathContext.DECIMAL128);
		//double jurosDiario = Math.pow(CommonsUtil.doubleValue(juros), CommonsUtil.doubleValue(potencia));
		double jurosTotal = Math.pow(jurosDiario, quantidadeDeMesesDouble);
	
		BigDecimal valorPresente = BigDecimal.ZERO;
		valorPresente = (saldo).multiply(CommonsUtil.bigDecimalValue(jurosTotal), MathContext.DECIMAL128);
		valorPresente = valorPresente.setScale(2, BigDecimal.ROUND_HALF_UP);
		return valorPresente;
	}
	
	
	
	public List<RelatorioSemestre> getListRelatorioReceber() {
		return listRelatorioReceber;
	}

	public void setListRelatorioReceber(List<RelatorioSemestre> listRelatorioReceber) {
		this.listRelatorioReceber = listRelatorioReceber;
	}

	public List<RelatorioSemestre> getListRelatorioPagar() {
		return listRelatorioPagar;
	}

	public void setListRelatorioPagar(List<RelatorioSemestre> listRelatorioPagar) {
		this.listRelatorioPagar = listRelatorioPagar;
	}

	public String getTipoPesquisaRelatorio() {
		return tipoPesquisaRelatorio;
	}

	public void setTipoPesquisaRelatorio(String tipoPesquisaRelatorio) {
		this.tipoPesquisaRelatorio = tipoPesquisaRelatorio;
	}
	
	////////////////////

	public BigDecimal getValorAntigo() {
		return valorAntigo;
	}

	public void setValorAntigo(BigDecimal valorAntigo) {
		this.valorAntigo = valorAntigo;
	}

	public BigDecimal getTaxaMensal() {
		return taxaMensal;
	}

	public void setTaxaMensal(BigDecimal taxaMensal) {
		this.taxaMensal = taxaMensal;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public BigDecimal getValorPresenteParcela() {
		return valorPresenteParcela;
	}

	public void setValorPresenteParcela(BigDecimal valorPresenteParcela) {
		this.valorPresenteParcela = valorPresenteParcela;
	}

	public Date getDataCalculo() {
		return dataCalculo;
	}

	public void setDataCalculo(Date dataCalculo) {
		this.dataCalculo = dataCalculo;
	}

	public List<DataCalculoDebentures> getListaDatasCalculo() {
		return listaDatasCalculo;
	}

	public void setListaDatasCalculo(List<DataCalculoDebentures> listaDatasCalculo) {
		this.listaDatasCalculo = listaDatasCalculo;
	}

	public Date getDataInicio2() {
		return dataInicio2;
	}

	public void setDataInicio2(Date dataInicio2) {
		this.dataInicio2 = dataInicio2;
	}

	public Date getDataFim2() {
		return dataFim2;
	}

	public void setDataFim2(Date dataFim2) {
		this.dataFim2 = dataFim2;
	}

	public boolean isPesquisaEnvelope() {
		return pesquisaEnvelope;
	}

	public void setPesquisaEnvelope(boolean pesquisaEnvelope) {
		this.pesquisaEnvelope = pesquisaEnvelope;
	}
	
	
}
