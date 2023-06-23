package com.webnowbr.siscoat.relatorio.mb;

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
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.relatorio.vo.RelatorioInadimplencia;

@ManagedBean(name="relatorioInadimplenciaMB")
@SessionScoped
public class RelatorioInadimplenciaMB {
	
	private List<RelatorioInadimplencia> listRelatorioInadimplencia;
	
	public void clearDados() {
		listRelatorioInadimplencia = new ArrayList<RelatorioInadimplencia>();
	}
	
	public String clearFieldsInadimplencia() {
		clearDados();
		return "/Relatorios/Pagamentos/RelatorioInadimplencia.xhtml";
	}

	public void consultarInadimplencia() {
		clearDados();
		
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		Calendar dataVencimentoParcela = Calendar.getInstance(zone, locale);
		dataHoje.set(Calendar.HOUR_OF_DAY, 0);
		dataHoje.set(Calendar.MINUTE, 0);
		dataHoje.set(Calendar.SECOND, 0);
		dataHoje.set(Calendar.MILLISECOND, 0);
		Calendar dataVencimentoMínima = new GregorianCalendar(2021,9,31);	
		
		List<ContratoCobranca> listContratos = new ArrayList<ContratoCobranca>();
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		listContratos = contratoCobrancaDao.consultaInadimplencia();
		
		for(ContratoCobranca contrato : listContratos) {
			int qtdDeparcelasVencidas = 0;
			RelatorioInadimplencia relIna = new RelatorioInadimplencia();
			if(CommonsUtil.mesmoValor(contrato.getNumeroContrato(), "09017")){
				String oi;
				oi = "asda";
				oi = oi + "asdsa";
			}
			for (ContratoCobrancaDetalhes ccd : contrato.getListContratoCobrancaDetalhes()) {
				if(CommonsUtil.intValue(CommonsUtil.somenteNumeros(ccd.getNumeroParcela())) <= contrato.getMesesCarencia()) {
					continue;
				}
				dataVencimentoParcela.setTime(ccd.getDataVencimento());		
				if(dataVencimentoParcela.before(dataVencimentoMínima)) {
					continue;
				}
				
				if (dataVencimentoParcela.getTime().before(dataHoje.getTime()) && !ccd.isParcelaPaga()) {
					ccd.setParcelaVencida(true);
					qtdDeparcelasVencidas++;
					relIna.parcelasAtraso.add(ccd);
				} else if (ccd.isParcelaPaga()) {
					relIna.parcelasPagas.add(ccd);
				} 
			}
			
			if(qtdDeparcelasVencidas >= 2) {
				relIna.contrato = contrato;
				relIna.populateCampos();
				listRelatorioInadimplencia.add(relIna);
			}
		}
	}
	
	public StreamedContent geraRelatorio() throws IOException{
		XSSFWorkbook wb = new XSSFWorkbook(getClass().getResourceAsStream("/resource/TabelaVazia.xlsx"));
		int iLinha = 0;
		XSSFSheet sheet = wb.getSheetAt(0);
		XSSFRow linha = sheet.getRow(iLinha);
		if(linha == null) {
			sheet.createRow(iLinha);
			linha = sheet.getRow(iLinha);
		}
		
		gravaCelula(0, "N° Contrato", linha);
		gravaCelula(1, "Pagador", linha);
		gravaCelula(2, "1° Atraso", linha);
		gravaCelula(3, "Valor da Ccb", linha);
		gravaCelula(4, "Valor da Garantia", linha);
		gravaCelula(5, "Parcelas Pagas", linha);
		gravaCelula(6, "Está em Cartório?", linha);
		gravaCelula(7, "Empresa", linha);
		
		iLinha = 1;
		for(RelatorioInadimplencia relIna : listRelatorioInadimplencia) {
			linha = sheet.getRow(iLinha);
			if(linha == null) {
				sheet.createRow(iLinha);
				linha = sheet.getRow(iLinha);
			}
			
			gravaCelula(0, relIna.numeroContrato, linha);
			gravaCelula(1, relIna.nomePagador, linha);
			gravaCelula(2, relIna.primeiroAtraso, linha);
			gravaCelula(3, relIna.valorCcb, linha);
			gravaCelula(4, relIna.valorGarantia, linha);
			gravaCelula(5, relIna.qtdParcelasPagas, linha);
			gravaCelula(6, relIna.estaEmCartorio, linha);
			gravaCelula(7, relIna.empresa, linha);
			iLinha++;
		}
		
		ByteArrayOutputStream  fileOut = new ByteArrayOutputStream ();
		//escrever tudo o que foi feito no arquivo
		wb.write(fileOut);

		//fecha a escrita de dados nessa planilha
		wb.close();
		
		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
				FacesContext.getCurrentInstance());
		
		gerador.open(String.format("Galleria Bank - Relatorio FIDC %s.xlsx", ""));
		gerador.feed( new ByteArrayInputStream(fileOut.toByteArray()));
		gerador.close();
		
		return null;
	}
	
	private void gravaCelula(Integer celula, BigDecimal value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		if(CommonsUtil.semValor(value))
			linha.getCell(celula).setCellValue((double) 0);
		else
			linha.getCell(celula).setCellValue(value.doubleValue());
	}
   
	private void gravaCelula(Integer celula, String value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		linha.getCell(celula).setCellValue(value);
	}
	
	private void gravaCelula(Integer celula, Date value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		linha.getCell(celula).setCellValue(CommonsUtil.formataData(value, "dd/MM/yyyy"));
	}
	
	private void gravaCelula(Integer celula, int value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		linha.getCell(celula).setCellValue(value);
	}
	
	private void gravaCelula(Integer celula, boolean value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		if(value)
			linha.getCell(celula).setCellValue("Sim");
		else
			linha.getCell(celula).setCellValue("Não");
	}

	public List<RelatorioInadimplencia> getListRelatorioInadimplencia() {
		return listRelatorioInadimplencia;
	}
	public void setListRelatorioInadimplencia(List<RelatorioInadimplencia> listRelatorioInadimplencia) {
		this.listRelatorioInadimplencia = listRelatorioInadimplencia;
	}	
}