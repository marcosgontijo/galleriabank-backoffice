package com.webnowbr.siscoat.contab.mb;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
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

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.contab.db.dao.BalancoPatrimonialDao;
import com.webnowbr.siscoat.contab.db.model.BalancoPatrimonial;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.relatorio.vo.RelatorioBalanco;


/** ManagedBean. */
@ManagedBean(name = "balancoPatrimonialMB")
@SessionScoped
public class BalancoPatrimonialMB {

	private BalancoPatrimonial objetoBalanco;
	private BalancoPatrimonial ultimoBalanco;
	private Date relDataContratoInicio;
	private Date relDataContratoFim;

	private String tituloPagina = "Todos";
	private List<BalancoPatrimonial> todosBalancos;
	private boolean editar;
	private boolean excluir;
	private boolean balancoPatrimonialXLSGerado;
	private String pathBalanco;
	private String nomeBalanco;
	
	private List<RelatorioBalanco> relatorioBalancoPagar = new ArrayList<RelatorioBalanco>();
	private List<RelatorioBalanco> relatorioBalancoReceber = new ArrayList<RelatorioBalanco>();

	

	public String clearFieldsBalancoPatrimonialConsulta() {

		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataInicio = Calendar.getInstance(zone, locale);
		this.relDataContratoInicio = dataInicio.getTime();
		this.relDataContratoFim = dataInicio.getTime();
		BalancoPatrimonialDao balancopatrimonialDao = new BalancoPatrimonialDao();
		this.todosBalancos = balancopatrimonialDao.consultaBalancoPatrimonial();
		return "/Atendimento/Cobranca/Contabilidade/BalancoPatrimonialConsulta.xhtml";
	}
	public String clearBalancoPatrimonialNew() {
		objetoBalanco = new BalancoPatrimonial();
		this.objetoBalanco.setAaaamm(null);
		this.objetoBalanco.setSaldoCaixaOmie(CommonsUtil.bigDecimalValue(0));
		this.objetoBalanco.setSaldoBancos(CommonsUtil.bigDecimalValue(0));
		this.objetoBalanco.setSaldoAplFin(CommonsUtil.bigDecimalValue(0));
		this.objetoBalanco.setDepositoBacenScd(CommonsUtil.bigDecimalValue(0));
		this.objetoBalanco.setDireitosCreditorios(CommonsUtil.bigDecimalValue(0));
		this.objetoBalanco.setProvisaoLiquidAntecipada(CommonsUtil.bigDecimalValue(0));
		this.objetoBalanco.setDepositosjudiciais(CommonsUtil.bigDecimalValue(0));
		this.objetoBalanco.setInvestOperantigas(CommonsUtil.bigDecimalValue(0));
		this.objetoBalanco.setCapitalSocial(CommonsUtil.bigDecimalValue(0));
		this.objetoBalanco.setRecursosDebentures(CommonsUtil.bigDecimalValue(0));
		
		return "/Atendimento/Cobranca/Contabilidade/BalancoPatrimonialInserir.xhtml";
	}
	
	public String clearBalancoPatrimonialEditar() {
		BalancoPatrimonialDao balancopatrimonialDao = new BalancoPatrimonialDao();
		
		if (!this.editar) {
			objetoBalanco = new BalancoPatrimonial();
			ultimoBalanco = balancopatrimonialDao.consultaUltimoBalanco();
		
		this.objetoBalanco.setAaaamm(gerarDataHoje());
		
		//VALOR DEFAULT NO CÓDIGO
		this.objetoBalanco.setDepositoBacenScd(CommonsUtil.bigDecimalValue(1016095.04));
		
		//VALOR DEFAULT NO DAO - VALOR DO SISTEMA
		this.objetoBalanco.setProvisaoLiquidAntecipada(balancopatrimonialDao.consultaContasPagar());
		this.objetoBalanco.setCustoPonderado(balancopatrimonialDao.somaParcelaX());
		
		// VALOR DEFAULT ÚLTIMO BALANÇO
		if (!CommonsUtil.semValor(ultimoBalanco)) {
			this.objetoBalanco.setDepositosjudiciais(ultimoBalanco.getDepositosjudiciais());
			this.objetoBalanco.setInvestOperantigas(ultimoBalanco.getInvestOperantigas());
			this.objetoBalanco.setCapitalSocial(ultimoBalanco.getCapitalSocial());
		}	
	}
		return "/Atendimento/Cobranca/Contabilidade/BalancoPatrimonialInserir.xhtml";
	}
	
	
	public void geraBalanco () {
		BalancoPatrimonialDao balancopatrimonialDao = new BalancoPatrimonialDao();
		if (!this.editar) {
			objetoBalanco = new BalancoPatrimonial();
			ultimoBalanco = balancopatrimonialDao.consultaUltimoBalanco();
		
		this.objetoBalanco.setAaaamm(gerarDataHoje());
		
		//VALOR DEFAULT NO CÓDIGO
		this.objetoBalanco.setDepositoBacenScd(CommonsUtil.bigDecimalValue(1016095.04));
		
		//VALOR DEFAULT NO DAO - VALOR DO SISTEMA

		this.objetoBalanco.setProvisaoLiquidAntecipada(balancopatrimonialDao.consultaContasPagar());
		this.objetoBalanco.setCustoPonderado(balancopatrimonialDao.somaParcelaX());
		this.relatorioBalancoPagar = balancopatrimonialDao.listaRelatorioPagarBalanco();
		
		// VALOR DEFAULT ÚLTIMO BALANÇO
		if (!CommonsUtil.semValor(ultimoBalanco)) {
			this.objetoBalanco.setDepositosjudiciais(ultimoBalanco.getDepositosjudiciais());
			this.objetoBalanco.setInvestOperantigas(ultimoBalanco.getInvestOperantigas());
			this.objetoBalanco.setCapitalSocial(ultimoBalanco.getCapitalSocial());
		}
		
		this.objetoBalanco.saldoCaixaOmie(); // VALOR CAIXAS
		this.objetoBalanco.calcularVariaveisReceber(null, relatorioBalancoReceber); //VALO DIREITOS CREDITORIOS
		this.objetoBalanco.calcularCustoPonderado(relatorioBalancoPagar); //VALOR RECURSOS DEBENTURES
		
		
		}
	}

	public BalancoPatrimonial getUltimoBalanco() {
		return ultimoBalanco;
	}

	public void setUltimoBalanco(BalancoPatrimonial ultimoBalanco) {
		this.ultimoBalanco = ultimoBalanco;
	}

	public String salvarBalanco() {
		FacesContext context = FacesContext.getCurrentInstance();
		BalancoPatrimonialDao balancoPatrimonialDao = new BalancoPatrimonialDao();
		try {
			balancoPatrimonialDao.merge(this.objetoBalanco);

			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Balanço Inserido com sucesso!!", ""));
			clearFieldsBalancoPatrimonialConsulta();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro: " + e, ""));
		}
		return clearFieldsBalancoPatrimonialConsulta();
	}

	public String editarBalanco() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		BalancoPatrimonialDao cDao = new BalancoPatrimonialDao();
	

		if (this.objetoBalanco.getId() > 0) {
			cDao.merge(this.objetoBalanco);
		}

		facesContext.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Balanço Patrimonial: Balanço alterado com sucesso!", ""));

		return clearFieldsBalancoPatrimonialConsulta();
	}

	public void excluirBalanco() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		BalancoPatrimonialDao cDao = new BalancoPatrimonialDao();

		cDao.delete(this.objetoBalanco);

		this.todosBalancos.remove(this.objetoBalanco);

		facesContext.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Balanço Patrimonial: Balanço excluído com sucesso!", ""));

		clearFieldsBalancoPatrimonialConsulta();
	}
	

	public void geraXLSBalancoPatrimonial() throws IOException {
		ParametrosDao pDao = new ParametrosDao();
		this.pathBalanco = pDao.findByFilter("nome", "BALANCO_PATH").get(0).getValorString();
		this.nomeBalanco = "Balanço Patrimonial.xlsx";

		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		dataHoje.set(Calendar.HOUR_OF_DAY, 0);
		dataHoje.set(Calendar.MINUTE, 0);
		dataHoje.set(Calendar.SECOND, 0);
		dataHoje.set(Calendar.MILLISECOND, 0);

		// dataHoje.add(Calendar.DAY_OF_MONTH, 1);

		String excelFileName = this.pathBalanco + this.nomeBalanco;// name of excel file

		String sheetName = "BalançoPatrimonial";// name of sheet

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetName);
//		sheet.setDefaultColumnWidth(51);
		sheet.setColumnWidth((short) (0), (short) (10 * 1100));
		sheet.setColumnWidth((short) (1), (short) (10 * 500));
		sheet.setColumnWidth((short) (2), (short) (10 * 1600));
		sheet.setColumnWidth((short) (3), (short) (10 * 500));

		
		// Style para cabeçalho
		XSSFCellStyle cell_style = wb.createCellStyle();
		cell_style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		cell_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		XSSFFont font = wb.createFont();
		font.setBold(true);
		cell_style.setFont(font);
		cell_style.setAlignment(HorizontalAlignment.LEFT);
		cell_style.setVerticalAlignment(VerticalAlignment.TOP);
		cell_style.setWrapText(true);

		/*
		 * // cria estilo para dados em geral cell_style = wb.createCellStyle();
		 * cell_style.setAlignment(HorizontalAlignment.LEFT);
		 * cell_style.setVerticalAlignment(VerticalAlignment.TOP);
		 * cell_style.setBorderBottom(BorderStyle.THIN);
		 * cell_style.setBorderTop(BorderStyle.THIN);
		 * cell_style.setBorderRight(BorderStyle.THIN);
		 * cell_style.setBorderLeft(BorderStyle.THIN); cell_style.setWrapText(true);
		 */

		// cria estilo especifico para coluna type numérico título
		CellStyle numericStyle = wb.createCellStyle();
		
		numericStyle.setAlignment(HorizontalAlignment.CENTER);
		numericStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		numericStyle.setWrapText(true);
		
		// cria estilo especifico para coluna type numérico normal
		CellStyle numericNormalStyle = wb.createCellStyle();
		numericNormalStyle.setAlignment(HorizontalAlignment.CENTER);
		numericNormalStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		numericStyle.setWrapText(true);
		
		// formatação dados geral
		XSSFCellStyle normal_style = wb.createCellStyle();
//		normal_style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
//		normal_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		CellStyle normalStyle = wb.createCellStyle();
		normalStyle.setAlignment(HorizontalAlignment.LEFT);
		normalStyle.setVerticalAlignment(VerticalAlignment.TOP);
		normalStyle.setWrapText(true);
				
		// cria a formatação para moeda
		CreationHelper ch = wb.getCreationHelper();
		numericStyle.setDataFormat(
				ch.createDataFormat().getFormat("_(R$* #,##0.00_);_(R$* (#,##0.00);_(R$* \"-\"??_);_(@_)"));
		// cria a formatação para moeda
		CreationHelper dh = wb.getCreationHelper();
		numericNormalStyle.setDataFormat(
				dh.createDataFormat().getFormat("_(R$* #,##0.00_);_(R$* (#,##0.00);_(R$* \"-\"??_);_(@_)"));

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


		// Montar excel

		int countLine = 0;
		BalancoPatrimonial record = this.objetoBalanco;
		countLine++;
		XSSFRow row1 = sheet.createRow(countLine);
		XSSFCell cell;
		cell = row1.createCell(0);
		cell.setCellValue("BALANÇO PATRIMONIAL");
		cell.setCellStyle(cell_style);
		
		cell = row1.createCell(1);
		cell.setCellStyle(dateStyle);
		cell.setCellValue(record.getAaaaMM());
		
		countLine++;
		XSSFRow row2 = sheet.createRow(countLine);
		cell = row2.createCell(0);
		cell.setCellValue("TOTAL ATIVO CIRCULANTE");
		cell.setCellStyle(cell_style);
		
		cell = row2.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getTotalAtivoCirculante() != null) {
			cell.setCellValue(((BigDecimal) record.getTotalAtivoCirculante()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++;
		XSSFRow row3 = sheet.createRow(countLine);
		cell = row3.createCell(0);
		cell.setCellValue("     CAIXA E EQUIVALENTES DE CAIXA");
		cell.setCellStyle(cell_style);
		
		cell = row3.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getTotalCaixa() != null) {
			cell.setCellValue(((BigDecimal) record.getTotalCaixa()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++;
		XSSFRow row4 = sheet.createRow(countLine);
		cell = row4.createCell(0);
		cell.setCellValue("          CAIXA");
		cell.setCellStyle(normal_style);
		
		cell = row4.createCell(1);
		cell.setCellStyle(numericNormalStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getTotalCaixa() != null) {
			cell.setCellValue(((BigDecimal) record.getSaldoCaixa()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++;
		XSSFRow row5 = sheet.createRow(countLine);
		cell = row5.createCell(0);
		cell.setCellValue("          BANCOS");
		cell.setCellStyle(normal_style);
		
		cell = row5.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getSaldoBancos() != null) {
			cell.setCellValue(((BigDecimal) record.getSaldoBancos()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++;
		XSSFRow row6 = sheet.createRow(countLine);
		cell = row6.createCell(0);
		cell.setCellValue("          APLICAÇÃO FINANCEIRA");
		cell.setCellStyle(normal_style);
		
		cell = row6.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getSaldoAplFin() != null) {
			cell.setCellValue(((BigDecimal) record.getSaldoAplFin()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++;
		XSSFRow row7 = sheet.createRow(countLine);
		cell = row7.createCell(0);
		cell.setCellValue("          OPERAÇÕES PAGAS E A RECEBER DO FIDC");
		cell.setCellStyle(normal_style);
		
		cell = row7.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getOpPagasReceberFidc() != null) {
			cell.setCellValue(((BigDecimal) record.getOpPagasReceberFidc()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++;
		XSSFRow row8 = sheet.createRow(countLine);
		cell = row8.createCell(0);
		cell.setCellValue("          APLICAÇÃO FUNDO ITAÚ SOBERANO");
		cell.setCellStyle(normal_style);
		
		cell = row8.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getApItauSoberano() != null) {
			cell.setCellValue(((BigDecimal) record.getApItauSoberano()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++;
		XSSFRow row9 = sheet.createRow(countLine);
		cell = row9.createCell(0);
		cell.setCellValue("          PROVISÃO DEVEDORES DUVIDOSOS");
		cell.setCellStyle(normal_style);
		
		cell = row9.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getProvisaoDevedoresDuvidosos() != null) {
			cell.setCellValue(((BigDecimal) record.getProvisaoDevedoresDuvidosos()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++;
		XSSFRow row10 = sheet.createRow(countLine);
		cell = row10.createCell(0);
		cell.setCellValue("          SALDO CONTA COBRANÇA FIDC");
		cell.setCellStyle(normal_style);
		
		cell = row10.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getSaldoCobrancaFidc() != null) {
			cell.setCellValue(((BigDecimal) record.getSaldoCobrancaFidc()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++;
		XSSFRow row11 = sheet.createRow(countLine);
		cell = row11.createCell(0);
		cell.setCellValue("          DEPÓSITO BACEN PARA SCD");
		cell.setCellStyle(normal_style);
		
		cell = row11.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getDepositoBacenScd() != null) {
			cell.setCellValue(((BigDecimal) record.getDepositoBacenScd()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++; XSSFRow row12 = sheet.createRow(countLine); cell =
		row12.createCell(0); cell.setCellValue("	");
		 
		
		
		countLine++;
		XSSFRow row13 = sheet.createRow(countLine);
		cell = row13.createCell(0);
		cell.setCellValue("     VALORES REALIZÁVEIS A CURTO PRAZO");
		cell.setCellStyle(cell_style);
		
		cell = row13.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getTotalRealizavelCurtoPrazo() != null) {
			cell.setCellValue(((BigDecimal) record.getTotalRealizavelCurtoPrazo()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++;
		XSSFRow row14 = sheet.createRow(countLine);
		cell = row14.createCell(0);
		cell.setCellValue("          DIREITOS CREDITORIOS");
		cell.setCellStyle(normal_style);
		
		cell = row14.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getDireitosCreditorios() != null) {
			cell.setCellValue(((BigDecimal) record.getDireitosCreditorios()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++;
		XSSFRow row15 = sheet.createRow(countLine);
		cell = row15.createCell(0);
		cell.setCellValue("          TRIBUTOS A COMPENSAR");
		cell.setCellStyle(normal_style);
		
		cell = row15.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getTributosCompensar() != null) {
			cell.setCellValue(((BigDecimal) record.getTributosCompensar()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++;
		XSSFRow row16 = sheet.createRow(countLine);
		cell = row16.createCell(0);
		cell.setCellValue("          ADIANTAMENTOS");
		cell.setCellStyle(normal_style);
		
		cell = row16.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getAdiantamentos() != null) {
			cell.setCellValue(((BigDecimal) record.getAdiantamentos()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++;
		XSSFRow row17 = sheet.createRow(countLine);
		cell = row17.createCell(0);
		cell.setCellValue("          OUTROS CRÉDITOS A IDENTIFICAR");
		cell.setCellStyle(normal_style);
		
		cell = row17.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getOutrosCreditos() != null) {
			cell.setCellValue(((BigDecimal) record.getOutrosCreditos()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++;
		XSSFRow row18 = sheet.createRow(countLine);
		cell = row18.createCell(0);
		cell.setCellValue("          ESTOQUE (Imóveis)");
		cell.setCellStyle(normal_style);
		
		cell = row18.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getEstoque() != null) {
			cell.setCellValue(((BigDecimal) record.getEstoque()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++; XSSFRow row19 = sheet.createRow(countLine); 
		cell = row19.createCell(0); cell.setCellValue("	");
				
		countLine++;
		XSSFRow row20 = sheet.createRow(countLine);
		cell = row20.createCell(0);
		cell.setCellValue("VALORES REALIZÁVEIS A LONGO PRAZO");
		cell.setCellStyle(cell_style);
		
		cell = row20.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getTotalRealizavelLongoPrazo() != null) {
			cell.setCellValue(((BigDecimal) record.getTotalRealizavelLongoPrazo()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++;
		XSSFRow row21 = sheet.createRow(countLine);
		cell = row21.createCell(0);
		cell.setCellValue("          DEPÓSITOS JUDICIAIS");
		cell.setCellStyle(normal_style);
		
		cell = row21.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getDepositosjudiciais() != null) {
			cell.setCellValue(((BigDecimal) record.getDepositosjudiciais()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++;
		XSSFRow row22 = sheet.createRow(countLine);
		cell = row22.createCell(0);
		cell.setCellValue("          INVEST. OPER. ANTIGAS");
		cell.setCellStyle(normal_style);
		
		cell = row22.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getInvestOperantigas() != null) {
			cell.setCellValue(((BigDecimal) record.getInvestOperantigas()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++; XSSFRow row23 = sheet.createRow(countLine); 
		cell = row23.createCell(0); cell.setCellValue("	");
		
		countLine++;
		XSSFRow row24 = sheet.createRow(countLine);
		cell = row24.createCell(0);
		cell.setCellValue("ATIVO NÃO CIRCULANTE");
		cell.setCellStyle(cell_style);
		
		cell = row24.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getTotalAtivoNaoCirculante() != null) {
			cell.setCellValue(((BigDecimal) record.getTotalAtivoNaoCirculante()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++;
		XSSFRow row25 = sheet.createRow(countLine);
		cell = row25.createCell(0);
		cell.setCellValue("     INVESTIMENTOS");
		cell.setCellStyle(cell_style);
		
		cell = row25.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getTotalInvestimentos() != null) {
			cell.setCellValue(((BigDecimal) record.getTotalInvestimentos()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++;
		XSSFRow row26 = sheet.createRow(countLine);
		cell = row26.createCell(0);
		cell.setCellValue("          INVESTIMENTOS");
		cell.setCellStyle(normal_style);
		
		cell = row26.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getInvestimentos() != null) {
			cell.setCellValue(((BigDecimal) record.getInvestimentos()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++;
		XSSFRow row27 = sheet.createRow(countLine);
		cell = row27.createCell(0);
		cell.setCellValue("     IMOBILIZADO");
		cell.setCellStyle(cell_style);
		
		cell = row27.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getTotalImobilizados() != null) {
			cell.setCellValue(((BigDecimal) record.getTotalImobilizados()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++;
		XSSFRow row28 = sheet.createRow(countLine);
		cell = row28.createCell(0);
		cell.setCellValue("          BENS (Imóveis e Informática)");
		cell.setCellStyle(normal_style);
		
		cell = row28.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getBensImobilizados() != null) {
			cell.setCellValue(((BigDecimal) record.getBensImobilizados()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		countLine++;
		XSSFRow row29 = sheet.createRow(countLine);
		cell = row29.createCell(0);
		cell.setCellValue("TOTAL DO ATIVO");
		cell.setCellStyle(cell_style);
		
		cell = row29.createCell(1);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getTotalAtivos() != null) {
			cell.setCellValue(((BigDecimal) record.getTotalAtivos()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		//PASSIVO
		
		cell = row2.createCell(2);
		cell.setCellValue("TOTAL DO PASSIVO CIRCULANTE");
		cell.setCellStyle(cell_style);
		
		cell = row2.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getTotalPassivoCirculante() != null) {
			cell.setCellValue(((BigDecimal) record.getTotalPassivoCirculante()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		cell = row3.createCell(2);
		cell.setCellValue("     VALORES EXIGÍVEIS A CURTO PRAZO");
		cell.setCellStyle(cell_style);
		
		cell = row3.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getTotalExigivelCurtoPrazo() != null) {
			cell.setCellValue(((BigDecimal) record.getTotalExigivelCurtoPrazo()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		cell = row4.createCell(2);
		cell.setCellValue("          CONTA CORRENTE CLIENTES");
		cell.setCellStyle(normal_style);
		
		cell = row4.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getContaCorrenteClientes() != null) {
			cell.setCellValue(((BigDecimal) record.getContaCorrenteClientes()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		cell = row5.createCell(2);
		cell.setCellValue("          FORNECEDORES-CONSÓRCIO");
		cell.setCellStyle(normal_style);
		
		cell = row5.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getFornecedoresConsorcio() != null) {
			cell.setCellValue(((BigDecimal) record.getFornecedoresConsorcio()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}

		cell = row6.createCell(2);
		cell.setCellValue("          OBRIGAÇÕES TRIBUTÁRIAS");
		cell.setCellStyle(normal_style);
		
		cell = row6.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getObrigacoesTributarias() != null) {
			cell.setCellValue(((BigDecimal) record.getObrigacoesTributarias()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}

		cell = row7.createCell(2);
		cell.setCellValue("          OBRIGAÇÕES SOCIAIS ESTATUTÁRIAS");
		cell.setCellStyle(normal_style);
		
		cell = row7.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getObrigacoesSociaisEstatutarias() != null) {
			cell.setCellValue(((BigDecimal) record.getObrigacoesSociaisEstatutarias()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		cell = row8.createCell(2);
		cell.setCellValue("          RECURSOS DE DEBENTURES");
		cell.setCellStyle(normal_style);
		
		cell = row8.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getRecursosDebentures() != null) {
			cell.setCellValue(((BigDecimal) record.getRecursosDebentures()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}

		cell = row9.createCell(2);
		cell.setCellValue("          RECURSOS DO FIDC");
		cell.setCellStyle(normal_style);
		
		cell = row9.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getRecursosFidc() != null) {
			cell.setCellValue(((BigDecimal) record.getRecursosFidc()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}

		cell = row10.createCell(2);
		cell.setCellValue("          RECURSOS DO CRI");
		cell.setCellStyle(normal_style);
		
		cell = row10.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getRecursosCri() != null) {
			cell.setCellValue(((BigDecimal) record.getRecursosCri()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}

		cell = row12.createCell(2);
		cell.setCellValue("     PROVISÃO PARA LIQUIDAÇÃO ANTECIPADA");
		cell.setCellStyle(cell_style);
		
		cell = row12.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getProvisaoLiquidAntecipada() != null) {
			cell.setCellValue(((BigDecimal) record.getProvisaoLiquidAntecipada()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		cell = row13.createCell(2);
		cell.setCellValue("TOTAL PASSIVO EXIGÍVEL A LONGO PRAZO");
		cell.setCellStyle(cell_style);
		
		cell = row13.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getTotalExigivelLongoPrazo() != null) {
			cell.setCellValue(((BigDecimal) record.getTotalExigivelLongoPrazo()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}

		cell = row14.createCell(2);
		cell.setCellValue("VALORES EXIGÍVEIS A LONGO PRAZO");
		cell.setCellStyle(normal_style);
		
		cell = row14.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getValorExigivelLongoPrazo() != null) {
			cell.setCellValue(((BigDecimal) record.getValorExigivelLongoPrazo()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}

		cell = row16.createCell(2);
		cell.setCellValue("TOTAL PATRIMÔNIO LÍQUIDO");
		cell.setCellStyle(cell_style);
		
		cell = row16.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getTotalPatrimonioLiquido() != null) {
			cell.setCellValue(((BigDecimal) record.getTotalPatrimonioLiquido()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		cell = row17.createCell(2);
		cell.setCellValue("     TOTAL CAPITAL SOCIAL");
		cell.setCellStyle(cell_style);
		
		cell = row17.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getTotalCapitalSocial() != null) {
			cell.setCellValue(((BigDecimal) record.getTotalCapitalSocial()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		cell = row18.createCell(2);
		cell.setCellValue("          CAPITAL SOCIAL");
		cell.setCellStyle(normal_style);
		
		cell = row18.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getCapitalSocial() != null) {
			cell.setCellValue(((BigDecimal) record.getCapitalSocial()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}

		cell = row20.createCell(2);
		cell.setCellValue("     TOTAL LUCROS ACUMULADOS ATÉ SEMESTRE ANTERIOR");
		cell.setCellStyle(cell_style);
		
		cell = row20.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getTotalAcumuladosSemestreAnterior() != null) {
			cell.setCellValue(((BigDecimal) record.getTotalAcumuladosSemestreAnterior()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}

		cell = row21.createCell(2);
		cell.setCellValue("          LUCROS ACUMULADOS ATÉ ANO ANTERIOR");
		cell.setCellStyle(normal_style);
		
		cell = row21.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getLucrosAcumuladosAnoAnterior() != null) {
			cell.setCellValue(((BigDecimal) record.getLucrosAcumuladosAnoAnterior()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}

		cell = row22.createCell(2);
		cell.setCellValue("          (-)DISTRIBUIÇÃO LUCROS DO 2º SEMESTRE PAGO NO 1º SEMESTRE");
		cell.setCellStyle(normal_style);
		
		cell = row22.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getDistribuicao2Pago1() != null) {
			cell.setCellValue(((BigDecimal) record.getDistribuicao2Pago1()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}

		cell = row23.createCell(2);
		cell.setCellValue("          LUCRO 1º SEMESTRE DO ANO ANTERIOR");
		cell.setCellStyle(normal_style);
		
		cell = row23.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getLucroSemestreAnterior() != null) {
			cell.setCellValue(((BigDecimal) record.getLucroSemestreAnterior()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}

		cell = row24.createCell(2);
		cell.setCellValue("          (-)AUMENTO DE CAPITAL SOCIAL");
		cell.setCellStyle(normal_style);
		
		cell = row24.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getAumentoCapitalSocial() != null) {
			cell.setCellValue(((BigDecimal) record.getAumentoCapitalSocial()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		cell = row25.createCell(2);
		cell.setCellValue("          (-)DISTRIBUIÇÃO LUCROS DO 1º SEMESTRE PAGO NO 2º SEMESTRE");
		cell.setCellStyle(normal_style);
		
		cell = row25.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getDistribuicao1Pago2() != null) {
			cell.setCellValue(((BigDecimal) record.getDistribuicao1Pago2()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}

		cell = row26.createCell(2);
		cell.setCellValue("	          ÚLTIMO LUCRO");
		cell.setCellStyle(normal_style);
		
		cell = row26.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getLucroAnterior() != null) {
			cell.setCellValue(((BigDecimal) record.getLucroAnterior()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}
		
		cell = row29.createCell(2);
		cell.setCellValue("TOTAL DO PASSIVO");
		cell.setCellStyle(cell_style);
		
		cell = row29.createCell(3);
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		if (record.getTotalPassivo() != null) {
			cell.setCellValue(((BigDecimal) record.getTotalPassivo()).doubleValue());
		} else {
			cell.setCellValue(Double.valueOf("0.00"));
		}

		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		// write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();

		this.balancoPatrimonialXLSGerado = true;
	}
	
	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}

	public BalancoPatrimonialMB() {

		objetoBalanco = new BalancoPatrimonial();
	}

	public BalancoPatrimonial getObjetoBalanco() {
		return objetoBalanco;
	}

	public void setObjetoBalanco(BalancoPatrimonial objetoBalanco) {
		this.objetoBalanco = objetoBalanco;
	}

	public Date getRelDataContratoInicio() {
		return relDataContratoInicio;
	}

	public void setRelDataContratoInicio(Date relDataContratoInicio) {
		this.relDataContratoInicio = relDataContratoInicio;
	}

	public Date getRelDataContratoFim() {
		return relDataContratoFim;
	}

	public void setRelDataContratoFim(Date relDataContratoFim) {
		this.relDataContratoFim = relDataContratoFim;
	}

	public String getTituloPagina() {
		return tituloPagina;
	}

	public void setTituloPagina(String tituloPagina) {
		this.tituloPagina = tituloPagina;
	}

	public List<BalancoPatrimonial> getTodosBalancos() {
		return todosBalancos;
	}

	public void setTodosBalancos(List<BalancoPatrimonial> todosBalancos) {
		this.todosBalancos = todosBalancos;
	}

	public boolean isEditar() {
		return editar;
	}

	public void setEditar(boolean editar) {
		this.editar = editar;
	}

	public boolean isExcluir() {
		return excluir;
	}

	public void setExcluir(boolean excluir) {
		this.excluir = excluir;
	}

	public boolean isBalancoPatrimonialXLSGerado() {
		return balancoPatrimonialXLSGerado;
	}

	public void setBalancoPatrimonialXLSGerado(boolean balancoPatrimonialXLSGerado) {
		this.balancoPatrimonialXLSGerado = balancoPatrimonialXLSGerado;
	}

	public String getPathBalanco() {
		return pathBalanco;
	}

	public void setPathBalanco(String pathBalanco) {
		this.pathBalanco = pathBalanco;
	}

	public String getNomeBalanco() {
		return nomeBalanco;
	}

	public void setNomeBalanco(String nomeBalanco) {
		this.nomeBalanco = nomeBalanco;
	}

}