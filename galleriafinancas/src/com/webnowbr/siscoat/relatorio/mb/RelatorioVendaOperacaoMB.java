package com.webnowbr.siscoat.relatorio.mb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.relatorio.vo.RelatorioVendaOperacaoVO;

/** ManagedBean. */
@ManagedBean(name = "relatorioVendaOperacaoMB")
@SessionScoped
public class RelatorioVendaOperacaoMB {
	/** Variavel. */
	private BigDecimal faixaValorInicial;
	private BigDecimal faixaValorFinal;
	private BigDecimal taxaDesagio = BigDecimal.valueOf(1.25);
	private BigDecimal taxaDesagioCalculo;
	private Date dataDesagio = new Date();
	private Date dataDesagioCalculo;
	
	private Integer situacaoInvestimentos = 0;
	private Integer SituacaoParcelas = 0;

	private List<RelatorioVendaOperacaoVO> contratosVenda;
	private List<RelatorioVendaOperacaoVO> contratosVendaPesquisa;

	private String pathExcel;
	private String nomeExcel;

	public String clearFields() {
		contratosVenda = null;
		contratosVendaPesquisa = null;
		return "/Relatorios/Venda/RelatorioVendaOperacao.xhtml";
	}

	public String carregaListagem() {
		FacesContext context = FacesContext.getCurrentInstance();
		if (taxaDesagio.floatValue() <= 0) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Taxa Deságio: A taxa de desagio esta inválida!", ""));
			return "";
		}

		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.contratosVendaPesquisa = new ArrayList<RelatorioVendaOperacaoVO>(0);
		try {
			if (this.contratosVenda == null || this.contratosVenda.size() == 0 || taxaDesagio != taxaDesagioCalculo  || dataDesagio != dataDesagioCalculo) {
				this.contratosVenda = contratoCobrancaDao.geraRelatorioVendaOperacao(taxaDesagio, dataDesagio);
				taxaDesagioCalculo = taxaDesagio;
				dataDesagioCalculo = dataDesagio;
			}

			for (RelatorioVendaOperacaoVO relatorioVendaOperacaoVO : contratosVenda) {
				if (((BigDecimal.valueOf(0).compareTo(faixaValorInicial) == 0
						&& BigDecimal.valueOf(0).compareTo(faixaValorFinal) == 0)
						|| (relatorioVendaOperacaoVO.getValorVenda().compareTo(faixaValorInicial) >= 0
								&& relatorioVendaOperacaoVO.getValorVenda().compareTo(faixaValorFinal) <= 0))
						&& //
						(situacaoInvestimentos == 0 || (situacaoInvestimentos == 1
								&& relatorioVendaOperacaoVO.getPercVendido().compareTo(BigDecimal.valueOf(0)) > 0)
								|| (situacaoInvestimentos == 2 && relatorioVendaOperacaoVO.getPercVendido()
										.compareTo(BigDecimal.valueOf(0)) == 0))
						&& //
						(SituacaoParcelas == 0 || (SituacaoParcelas == 1 && relatorioVendaOperacaoVO.getSituacao())
								|| (SituacaoParcelas == 2 && !relatorioVendaOperacaoVO.getSituacao()))) {

					this.contratosVendaPesquisa.add(relatorioVendaOperacaoVO);

				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";

	}

	/** Get/Set */

	public BigDecimal getFaixaValorInicial() {
		return faixaValorInicial;
	}

	public void setFaixaValorInicial(BigDecimal faixaValorInicial) {
		this.faixaValorInicial = faixaValorInicial;
	}

	public BigDecimal getFaixaValorFinal() {
		return faixaValorFinal;
	}

	public void setFaixaValorFinal(BigDecimal faixaValorFinal) {
		this.faixaValorFinal = faixaValorFinal;
	}

	public BigDecimal getTaxaDesagio() {
		return taxaDesagio;
	}

	public void setTaxaDesagio(BigDecimal taxaDesagio) {
		this.taxaDesagio = taxaDesagio;
	}

	public Date getDataDesagio() {
		return dataDesagio;
	}

	public void setDataDesagio(Date dataDesagio) {
		this.dataDesagio = dataDesagio;
	}

	public Integer getSituacaoInvestimentos() {
		return situacaoInvestimentos;
	}

	public void setSituacaoInvestimentos(Integer situacaoInvestimentos) {
		this.situacaoInvestimentos = situacaoInvestimentos;
	}

	public Integer getSituacaoParcelas() {
		return SituacaoParcelas;
	}

	public void setSituacaoParcelas(Integer situacaoParcelas) {
		SituacaoParcelas = situacaoParcelas;
	}

	public List<RelatorioVendaOperacaoVO> getContratosVendaPesquisa() {
		return contratosVendaPesquisa;
	}

	public void setContratosVendaPesquisa(List<RelatorioVendaOperacaoVO> contratosVendaPesquisa) {
		this.contratosVendaPesquisa = contratosVendaPesquisa;
	}

	public StreamedContent getXLSXFile() throws IOException {
		ParametrosDao pDao = new ParametrosDao();
		this.pathExcel = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomeExcel = "Relatório de Venda" + DateTime.now().toString("yyyyMMddHHmm") + ".xlsx";

		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		dataHoje.set(Calendar.HOUR_OF_DAY, 0);
		dataHoje.set(Calendar.MINUTE, 0);
		dataHoje.set(Calendar.SECOND, 0);
		dataHoje.set(Calendar.MILLISECOND, 0);

		// dataHoje.add(Calendar.DAY_OF_MONTH, 1);

		String excelFileName = this.pathExcel + this.nomeExcel;// name of excel file

		String sheetName = "Resultado";// name of sheet

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetName);
		sheet.setDefaultColumnWidth(25);

		// Style para cabeçalho
		XSSFCellStyle cell_style_center = wb.createCellStyle();

		cell_style_center.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cell_style_center.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		XSSFFont font = wb.createFont();
		font.setBold(true);
		cell_style_center.setFont(font);
		cell_style_center.setAlignment(HorizontalAlignment.CENTER);
		cell_style_center.setVerticalAlignment(VerticalAlignment.CENTER);
		cell_style_center.setBorderBottom(BorderStyle.THIN);
		cell_style_center.setBorderTop(BorderStyle.THIN);
		cell_style_center.setBorderRight(BorderStyle.THIN);
		cell_style_center.setBorderLeft(BorderStyle.THIN);
		cell_style_center.setWrapText(true);

		XSSFCellStyle cell_style_left = wb.createCellStyle();
		cell_style_left.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cell_style_left.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cell_style_left.setFont(font);
		cell_style_left.setAlignment(HorizontalAlignment.CENTER);
		cell_style_left.setVerticalAlignment(VerticalAlignment.CENTER);
		cell_style_left.setBorderBottom(BorderStyle.THIN);
		cell_style_left.setBorderTop(BorderStyle.THIN);
		cell_style_left.setBorderRight(BorderStyle.THIN);
		cell_style_left.setBorderLeft(BorderStyle.THIN);
		cell_style_left.setWrapText(true);

		// iterating r number of rows
		// cria CABEÇALHO
		int countLine = 0;
		XSSFRow row = sheet.createRow(countLine);
		XSSFCell cell;
		cell = row.createCell(0);
		cell.setCellValue("Contrato");
		cell.setCellStyle(cell_style_center);
		cell = row.createCell(1);
		cell.setCellValue("Ultima Parcela");
		cell.setCellStyle(cell_style_center);
		cell = row.createCell(2);
		cell.setCellValue("Sistema");
		cell.setCellStyle(cell_style_center);
		cell = row.createCell(3);
		cell.setCellValue("Pagador");
		cell.setCellStyle(cell_style_center);
		cell = row.createCell(4);
		cell.setCellValue("Valor Parcela");
		cell.setCellStyle(cell_style_center);
		cell = row.createCell(5);
		cell.setCellValue("Valor de Venda");
		cell.setCellStyle(cell_style_center);
		cell = row.createCell(6);
		cell.setCellValue("Falta Vender");
		cell.setCellStyle(cell_style_center);
		cell = row.createCell(7);
		cell.setCellValue("Perc. Vendido");
		cell.setCellStyle(cell_style_center);
		cell = row.createCell(8);
		cell.setCellValue("Situação");
		cell.setCellStyle(cell_style_center);
		cell = row.createCell(9);
		cell.setCellValue("Valor Avaliação");
		cell.setCellStyle(cell_style_center);

		// cria estilo para dados em geral - Centralizado
		cell_style_center = wb.createCellStyle();
		cell_style_center.setAlignment(HorizontalAlignment.CENTER);
		cell_style_center.setVerticalAlignment(VerticalAlignment.CENTER);
		cell_style_center.setBorderBottom(BorderStyle.THIN);
		cell_style_center.setBorderTop(BorderStyle.THIN);
		cell_style_center.setBorderRight(BorderStyle.THIN);
		cell_style_center.setBorderLeft(BorderStyle.THIN);
		cell_style_center.setWrapText(true);

		// cria estilo para dados em geral - Esquerda
		cell_style_left = wb.createCellStyle();
		cell_style_left.setAlignment(HorizontalAlignment.LEFT);
		cell_style_left.setVerticalAlignment(VerticalAlignment.CENTER);
		cell_style_left.setBorderBottom(BorderStyle.THIN);
		cell_style_left.setBorderTop(BorderStyle.THIN);
		cell_style_left.setBorderRight(BorderStyle.THIN);
		cell_style_left.setBorderLeft(BorderStyle.THIN);
		cell_style_left.setWrapText(true);

		// cria estilo especifico para coluna type numérico
		CellStyle numericStyle = wb.createCellStyle();
		numericStyle.setAlignment(HorizontalAlignment.CENTER);
		numericStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		numericStyle.setBorderBottom(BorderStyle.THIN);
		numericStyle.setBorderTop(BorderStyle.THIN);
		numericStyle.setBorderRight(BorderStyle.THIN);
		numericStyle.setBorderLeft(BorderStyle.THIN);
		numericStyle.setWrapText(true);

		// cria a formatação para moeda
		CreationHelper ch = wb.getCreationHelper();
		numericStyle.setDataFormat(
				ch.createDataFormat().getFormat("_(R$* #,##0.00_);_(R$* (#,##0.00);_(R$* \"-\"??_);_(@_)"));

		// cria estilo especifico para coluna type Date
		CellStyle dateStyle = wb.createCellStyle();
		dateStyle.setAlignment(HorizontalAlignment.CENTER);
		dateStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		dateStyle.setBorderBottom(BorderStyle.THIN);
		dateStyle.setBorderTop(BorderStyle.THIN);
		dateStyle.setBorderRight(BorderStyle.THIN);
		dateStyle.setBorderLeft(BorderStyle.THIN);
		dateStyle.setWrapText(true);
		// cria a formatação para Date
		dateStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("m/d/yy"));

		for (RelatorioVendaOperacaoVO record : this.contratosVendaPesquisa) {
			countLine++;
			row = sheet.createRow(countLine);

			// Contrato
			cell = row.createCell(0);
			cell.setCellStyle(cell_style_center);
			cell.setCellValue(record.getNumeroContrato());

			// Última Parcela
			cell = row.createCell(1);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getUltimaParcela());

			// Sistema
			cell = row.createCell(2);
			cell.setCellStyle(cell_style_center);
			cell.setCellValue(record.getSistema());

			// Pagador
			cell = row.createCell(3);
			cell.setCellStyle(cell_style_left);
			cell.setCellValue(record.getPagador());

			// Valor da Parcela
			cell = row.createCell(4);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(((BigDecimal) record.getValorParcela()).doubleValue());

			// Valor de Venda
			cell = row.createCell(5);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(((BigDecimal) record.getValorVenda()).doubleValue());

			// Falta Vender
			cell = row.createCell(6);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(((BigDecimal) record.getFaltaVender()).doubleValue());

			// Perc. Vendido
			cell = row.createCell(7);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(((BigDecimal) record.getPercVendido()).doubleValue());

			// Situação
			cell = row.createCell(8);
			cell.setCellStyle(cell_style_center);
			cell.setCellValue(record.getSituacao() ? "Em Dia" : "Com atrasos");

			cell = row.createCell(9);
			cell.setCellStyle(cell_style_center);

		}

		// Resize columns to fit data
		// TODO MIGRACAO POI
		/*
		 * int noOfColumns = sheet.getRow(0).getLastCellNum(); for (int i = 0; i <
		 * noOfColumns; i++) { sheet.autoSizeColumn(i); }
		 */

		ByteArrayOutputStream fileOut = new ByteArrayOutputStream();

		// write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();

		ByteArrayInputStream stream;
		stream = new ByteArrayInputStream(fileOut.toByteArray());
		StreamedContent file = new DefaultStreamedContent(stream, "application/xls", this.nomeExcel);

		return file;

	}
}
