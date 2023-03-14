package com.webnowbr.siscoat.contab.mb;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.microsoft.schemas.office.visio.x2012.main.CellType;
import com.webnowbr.siscoat.contab.db.dao.BalancoPatrimonialDao;
import com.webnowbr.siscoat.contab.db.model.BalancoPatrimonial;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;

import net.sf.jasperreports.engine.export.oasis.BorderStyle;
import net.sf.jasperreports.engine.export.oasis.CellStyle;

/** ManagedBean. */
@ManagedBean(name = "balancoPatrimonialMB")
@SessionScoped
public class BalancoPatrimonialMB {

	private BalancoPatrimonial objetoBalanco;
	private Date relDataContratoInicio;
	private Date relDataContratoFim;

	private String tituloPagina = "Todos";
	private List<BalancoPatrimonial> todosBalancos;
	private boolean editar;
	private boolean excluir;
	private boolean balancoPatrimonialXLSGerado;

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

	public String clearBalancoPatrimonialEditar() {
		if (!this.editar)
			objetoBalanco = new BalancoPatrimonial();
		return "/Atendimento/Cobranca/Contabilidade/BalancoPatrimonialInserir.xhtml";
	}

	public void salvarBalanco() {
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

	public String excluirBalanco() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		BalancoPatrimonialDao cDao = new BalancoPatrimonialDao();

		cDao.delete(this.objetoBalanco);

		this.todosBalancos.remove(this.objetoBalanco);

		facesContext.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Balanço Patrimonial: Balanço excluído com sucesso!", ""));

		return clearFieldsBalancoPatrimonialConsulta();
	}
	
	public void geraXLSBalancoPatrimonial() throws IOException {
		ParametrosDao pDao = new ParametrosDao();
		this.pathContrato = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomeContrato = "Relatório Debetures Emitidas.xlsx";

		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		dataHoje.set(Calendar.HOUR_OF_DAY, 0);
		dataHoje.set(Calendar.MINUTE, 0);
		dataHoje.set(Calendar.SECOND, 0);
		dataHoje.set(Calendar.MILLISECOND, 0);

		// dataHoje.add(Calendar.DAY_OF_MONTH, 1);

		String excelFileName = this.pathContrato + this.nomeContrato;// name of excel file

		String sheetName = "Balanço";// name of sheet

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetName);
		sheet.setDefaultColumnWidth(25);

		// Style para cabeçalho
		XSSFCellStyle cell_style = wb.createCellStyle();
		cell_style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cell_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		XSSFFont font = wb.createFont();
		font.setBold(true);
		cell_style.setFont(font);
		cell_style.setAlignment(HorizontalAlignment.CENTER);
		cell_style.setVerticalAlignment(VerticalAlignment.CENTER);
		cell_style.setBorderBottom(BorderStyle.THIN);
		cell_style.setBorderTop(BorderStyle.THIN);
		cell_style.setBorderRight(BorderStyle.THIN);
		cell_style.setBorderLeft(BorderStyle.THIN);
		cell_style.setWrapText(true);

		// iterating r number of rows
		// cria CABEÇALHO
		int countLine = 0;
		XSSFRow row = sheet.createRow(countLine);
		XSSFCell cell;
		cell = row.createCell(0);
		cell.setCellValue("BALANÇO PATRIMONIAL");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("Data");
		cell.setCellStyle(cell_style);
		cell = row.createCell(2);
		cell.setCellValue("TOTAL ATIVO CIRCULANTE");
		cell.setCellStyle(cell_style);
		cell = row.createCell(3);
		cell.setCellValue("	CAIXA E EQUIVALENTES DE CAIXA");
		cell.setCellStyle(cell_style);
		cell = row.createCell(4);
		cell.setCellValue("		CAIXA");
		cell.setCellStyle(cell_style);
		cell = row.createCell(5);
		cell.setCellValue("		BANCOS");
		cell.setCellStyle(cell_style);
		cell = row.createCell(6);
		cell.setCellValue("		APLICAÇÃO FINANCEIRA");
		cell.setCellStyle(cell_style);
		cell = row.createCell(7);
		cell.setCellValue("		OPERAÇÕES PAGAS E A RECEBER DO FIDC");
		cell.setCellStyle(cell_style);
		cell = row.createCell(8);
		cell.setCellValue("		APLICAÇÃO FUNDO ITAÚ SOBERANO");
		cell.setCellStyle(cell_style);
		cell = row.createCell(9);
		cell.setCellValue("		PROVISÃO DEVEDORES DUVIDOSOS");
		cell.setCellStyle(cell_style);
		cell = row.createCell(10);
		cell.setCellValue("		SALDO CONTA COBRANÇA FIDC");
		cell.setCellStyle(cell_style);
		cell = row.createCell(11);
		cell.setCellValue("		DEPÓSITO BACEN PARA SCD");
		cell.setCellStyle(cell_style);
		cell = row.createCell(12);
		cell.setCellValue("	");
		cell.setCellStyle(cell_style);
		cell = row.createCell(13);
		cell.setCellValue(" VALORES REALIZÁVEIS A CURTO PRAZO");
		cell.setCellStyle(cell_style);
		cell = row.createCell(14);
		cell.setCellValue("		DIREITOS CREDITORIOS");
		cell.setCellStyle(cell_style);
		cell = row.createCell(15);
		cell.setCellValue("		TRIBUTOS A COMPENSAR");
		cell.setCellStyle(cell_style);
		cell = row.createCell(16);
		cell.setCellValue("		ADIANTAMENTOS");
		cell.setCellStyle(cell_style);
		cell = row.createCell(17);
		cell.setCellValue("		OUTROS CRÉDITOS A IDENTIFICAR");
		cell.setCellStyle(cell_style);
		cell = row.createCell(18);
		cell.setCellValue("		ESTOQUE (Imóveis)");
		cell.setCellStyle(cell_style);
		cell = row.createCell(19);
		cell.setCellValue("VALORES REALIZÁVEIS A LONGO PRAZO");
		cell.setCellStyle(cell_style);
		cell = row.createCell(20);
		cell.setCellValue("		DEPÓSITOS JUDICIAIS");
		cell.setCellStyle(cell_style);
		cell = row.createCell(21);
		cell.setCellValue("		INVEST. OPER. ANTIGAS");
		cell.setCellStyle(cell_style);
		cell = row.createCell(22);
		cell.setCellValue("ATIVO NÃO CIRCULANTE");
		cell.setCellStyle(cell_style);
		cell = row.createCell(23);
		cell.setCellValue("	INVESTIMENTOS");
		cell.setCellStyle(cell_style);
		cell = row.createCell(24);
		cell.setCellValue("		INVESTIMENTOS");
		cell.setCellStyle(cell_style);
		cell = row.createCell(25);
		cell.setCellValue("	IMOBILIZADOS");
		cell.setCellStyle(cell_style);
		cell = row.createCell(26);
		cell.setCellValue("		BENS (Imóveis e Informática)");
		cell.setCellStyle(cell_style);
		cell = row.createCell(27);
		cell.setCellValue("TOTAL DO ATIVO");
		cell.setCellStyle(cell_style);


		// cria estilo para dados em geral
		cell_style = wb.createCellStyle();
		cell_style.setAlignment(HorizontalAlignment.CENTER);
		cell_style.setVerticalAlignment(VerticalAlignment.CENTER);
		cell_style.setBorderBottom(BorderStyle.THIN);
		cell_style.setBorderTop(BorderStyle.THIN);
		cell_style.setBorderRight(BorderStyle.THIN);
		cell_style.setBorderLeft(BorderStyle.THIN);
		cell_style.setWrapText(true);

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

		for (BalancoPatrimonial record : this.todosBalancos) {
			countLine++;
			row = sheet.createRow(countLine);

			// Cabeçalho - Balanço Patrimonial
			cell = row.createCell(8);
			cell.setCellStyle(dateStyle);
			cell.setCellType(CellType.String("Balanço Patrimonial"));
			
			// Cabeçalho - Data
			cell = row.createCell(1);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getAaaaMM());
			
			// Total Ativo Circulante
			cell = row.createCell(2);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getTotalAtivoCirculante() != null) {
				cell.setCellValue(((BigDecimal) record.getTotalAtivoCirculante()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}
				

			// Total Caixa e Equivalente de Caixa
			cell = row.createCell(3);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getTotalCaixa() != null) {
				cell.setCellValue(((BigDecimal) record.getTotalCaixa()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Caixa
			cell = row.createCell(4);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getTotalCaixa() != null) {
				cell.setCellValue(((BigDecimal) record.getSaldoCaixa()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Bancos
			cell = row.createCell(5);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getSaldoBancos() != null) {
				cell.setCellValue(((BigDecimal) record.getSaldoBancos()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}
			// Aplicação Finananceira
			cell = row.createCell(6);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getSaldoAplFin() != null) {
				cell.setCellValue(((BigDecimal) record.getSaldoAplFin()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Operações pagas e a receber do FIDC
			cell = row.createCell(7);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getOpPagasReceberFidc() != null) {
				cell.setCellValue(((BigDecimal) record.getOpPagasReceberFidc()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Aplicação Fundo Itaú Soberano
			cell = row.createCell(8);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getApItauSoberano() != null) {
				cell.setCellValue(((BigDecimal) record.getApItauSoberano()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Provisão devedores duvidosos
			cell = row.createCell(9);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getProvisaoDevedoresDuvidosos() != null) {
				cell.setCellValue(((BigDecimal) record.getProvisaoDevedoresDuvidosos()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Saldo Conta Cobrança FIDC
			cell = row.createCell(10);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getSaldoCobrancaFidc() != null) {
				cell.setCellValue(((BigDecimal) record.getSaldoCobrancaFidc()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Deposito Bacen para SCD
			cell = row.createCell(11);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getDepositoBacenScd() != null) {
				cell.setCellValue(((BigDecimal) record.getDepositoBacenScd()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Total Valores Realizaveis a Curto Prazo
			cell = row.createCell(13);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getTotalRealizavelCurtoPrazo() != null) {
				cell.setCellValue(((BigDecimal) record.getTotalRealizavelCurtoPrazo()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Direitos Creditorios
			cell = row.createCell(14);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getDireitosCreditorios() != null) {
				cell.setCellValue(((BigDecimal) record.getDireitosCreditorios()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}
			
			// Tributos a Compensar
			cell = row.createCell(15);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getTributosCompensar() != null) {
				cell.setCellValue(((BigDecimal) record.getTributosCompensar()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}
			
			// Adiantamentos
			cell = row.createCell(16);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getAdiantamentos() != null) {
				cell.setCellValue(((BigDecimal) record.getAdiantamentos()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}
			
			// Outros Creditos a Identificar
			cell = row.createCell(17);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getOutrosCreditos() != null) {
				cell.setCellValue(((BigDecimal) record.getOutrosCreditos()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}
			
			// Estoque
			cell = row.createCell(18);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getEstoque() != null) {
				cell.setCellValue(((BigDecimal) record.getEstoque()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}
			
			// Total Valores realizaveis a Longo Prazo
			cell = row.createCell(19);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getTotalRealizavelLongoPrazo() != null) {
				cell.setCellValue(((BigDecimal) record.getTotalRealizavelLongoPrazo()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}
			
			// Depositos Judiciais
			cell = row.createCell(20);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getDepositosjudiciais() != null) {
				cell.setCellValue(((BigDecimal) record.getDepositosjudiciais()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}
			
			// Invest Oper Antigas
			cell = row.createCell(21);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getInvestOperantigas() != null) {
				cell.setCellValue(((BigDecimal) record.getInvestOperantigas()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}
			
			// Total Ativo Não Circulante
			cell = row.createCell(22);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getTotalAtivoNaoCirculante() != null) {
				cell.setCellValue(((BigDecimal) record.getTotalAtivoNaoCirculante()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}
			
			// Total Investimentos
			cell = row.createCell(23);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getTotalInvestimentos() != null) {
				cell.setCellValue(((BigDecimal) record.getTotalInvestimentos()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}
			
			// Investimentos
			cell = row.createCell(24);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getInvestimentos() != null) {
				cell.setCellValue(((BigDecimal) record.getInvestimentos()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}
			
			// Total Imobilizados
			cell = row.createCell(25);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getTotalImobilizados() != null) {
				cell.setCellValue(((BigDecimal) record.getTotalImobilizados()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}
			
			// Bens
			cell = row.createCell(26);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getBensImobilizados() != null) {
				cell.setCellValue(((BigDecimal) record.getBensImobilizados()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}
			
			// Total do Ativo
			cell = row.createCell(27);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getTotalAtivos() != null) {
				cell.setCellValue(((BigDecimal) record.getTotalAtivos()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

		}

		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		// write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();

		this.balancoPatrimonialXLSGerado = true;
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

}