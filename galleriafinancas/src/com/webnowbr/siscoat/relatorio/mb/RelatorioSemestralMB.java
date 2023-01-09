package com.webnowbr.siscoat.relatorio.mb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.cobranca.db.op.DashboardDao;
import com.webnowbr.siscoat.cobranca.db.op.RelatorioSemestralDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.relatorio.vo.RelatorioSemestre;
import com.webnowbr.siscoat.seguro.vo.SeguroTabelaVO;

/** ManagedBean. */
@ManagedBean(name = "relatorioSemestralMB")
@SessionScoped
public class RelatorioSemestralMB {

	private List<RelatorioSemestre> listRelatorioReceber;
	private List<RelatorioSemestre> listRelatorioPagar;
	private String tipoPesquisaRelatorio;

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
		String valueStr = sdf.format(value.getTime());
		linha.getCell(celula).setCellValue(valueStr);
	}

	private void gravaCelula(Integer celula, int value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		linha.getCell(celula).setCellValue(value);
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

}
