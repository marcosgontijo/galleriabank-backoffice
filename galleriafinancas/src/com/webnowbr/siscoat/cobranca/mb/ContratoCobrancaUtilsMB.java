package com.webnowbr.siscoat.cobranca.mb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
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
import org.joda.time.Days;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sun.xml.internal.ws.resources.BindingApiMessages;
import com.webnowbr.siscoat.cobranca.auxiliar.RelatorioFinanceiroCobranca;
import com.webnowbr.siscoat.cobranca.auxiliar.UtilsMB;
import com.webnowbr.siscoat.cobranca.db.model.Calculos;
import com.webnowbr.siscoat.cobranca.db.model.CalculosDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.CalculoDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;

/** ManagedBean. */
@ManagedBean(name = "contratoCobrancaUtilsMB")
@SessionScoped
public class ContratoCobrancaUtilsMB {
	private Date dtVencimento;
	private Date dtPagamento;

	private BigDecimal valorParcela;

	private BigDecimal txJuros;

	private BigDecimal multa;

	private BigDecimal valorAtualizado;

	private long qtdeDias;

	/** Objeto selecionado na LoV - Recebedor. */
	private PagadorRecebedor selectedRecebedor;

	/** Lista dos Recebedores utilizada pela LOV. */
	private List<PagadorRecebedor> listRecebedores;

	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedor;	

	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedor;	

	private boolean contratoGerado;

	public List<Calculos> listCalculos;
	public Calculos selectedCalculo;
	public Calculos objetoCalculo;
	private CalculosDetalhes calculosDetalhes;
	private List<CalculosDetalhes> listTemp;


	/* CALCULO PARCELAS */
	private Date dataVencimento;
	private Date dataPagamento;
	private BigDecimal vlrParcela;
	private BigDecimal txJurosPacela;
	private BigDecimal multaParcela;
	private BigDecimal totalParcela;
	private BigDecimal honorarios;
	private int numeroParcela;
	private String observacaoParcela;
	
	private BigDecimal sumTxJurosPacela;
	private BigDecimal sumMultaParcela;
	private BigDecimal sumVlrParcela;
	private BigDecimal sumHonorariosParcela;
	private BigDecimal sumTotalParcelas;
	
	private String sumTxJurosPacelaStr;
	private String sumMultaParcelaStr;
	private String sumVlrParcelaStr;
	private String sumHonorariosParcelaStr;
	private String sumTotalParcelasStr;
	
	private boolean calculoPDFGerado;
	private String pathPDF;
	private String nomePDF;

	private StreamedContent file;

	public boolean updateParcelaMode;

	public boolean updateMode;
	public boolean deleteMode;
	
	public Date dataAtualizacaoBkp;

	public ContratoCobrancaUtilsMB() {

	}
	public ContratoCobrancaUtilsMB(Date dtVencimento, Date dtPagamento, BigDecimal valorParcela, BigDecimal txJuros, BigDecimal multa){
		this.dtVencimento = dtVencimento;
		this.dtPagamento = dtPagamento;
		this.valorParcela = valorParcela;
		this.txJuros = txJuros;
		this.multa = multa;

		if (dtVencimento.after(dtPagamento)) {
			this.qtdeDias = 0;
		} else {
			this.qtdeDias = qtdeDiasCalculado(dtVencimento, dtPagamento);
		}

		// carrega parametros multa e tx juros, se não informado pelo contrato
		if (this.txJuros == null && this.multa == null) {
			loadParametros();
		}
	}

	public ContratoCobrancaUtilsMB(Date dtVencimento, Date dtPagamento, BigDecimal valorParcela, BigDecimal valorAtualizado, BigDecimal txJuros, BigDecimal multa){
		this.dtVencimento = dtVencimento;
		this.dtPagamento = dtPagamento;
		this.valorParcela = valorAtualizado;
		this.txJuros = txJuros;
		this.multa = multa;

		if (dtVencimento.after(dtPagamento)) {
			this.qtdeDias = 0;
		} else {
			Days.daysBetween(new DateTime(dtVencimento), new DateTime(dtPagamento)).getDays();
		}

		// carrega parametros multa e tx juros, se não informado pelo contrato
		if (this.txJuros == null && this.multa == null) {
			loadParametros();
		}
	}

	public long qtdeDiasCalculado(Date dtVencimento, Date dtPagamento) {
		long qtdeDias = Days.daysBetween(new DateTime(dtVencimento), new DateTime(dtPagamento)).getDays();

		return qtdeDias ;
	}

	public void atualizaValorRecalculado(BigDecimal valorParcela) {
		this.valorParcela = valorParcela;

		recalculaValor();
	}
	
	public void clearSumsParcelas() {
		this.sumTxJurosPacela = new BigDecimal("0.00");
		this.sumMultaParcela = new BigDecimal("0.00");
		this.sumVlrParcela = new BigDecimal("0.00");
		this.sumHonorariosParcela = new BigDecimal("0.00");
		this.sumTotalParcelas = new BigDecimal("0.00");
		
		this.sumTxJurosPacelaStr = "R$ 0,00";
		this.sumMultaParcelaStr = "R$ 0,00";
		this.sumVlrParcelaStr = "R$ 0,00";
		this.sumHonorariosParcelaStr = "R$ 0,00";
		this.sumTotalParcelasStr = "R$ 0,00";
	}
	
	public void sumsParcelas() {
		clearSumsParcelas();
		
		for (CalculosDetalhes c : this.objetoCalculo.getListCalculoDetalhes()) {
			if (c.getVlrTxJuros() != null) {
				this.sumTxJurosPacela = this.sumTxJurosPacela.add(c.getVlrTxJuros());
			}
			
			if (c.getVlrMulta() != null) {
				this.sumMultaParcela = this.sumMultaParcela.add(c.getVlrMulta());
			}

			if (c.getVlrParcela() != null) {
				this.sumVlrParcela = this.sumVlrParcela.add(c.getVlrParcela());
			}

			if (c.getVlrHonorarios() != null) {
				this.sumHonorariosParcela = this.sumHonorariosParcela.add(c.getVlrHonorarios());
			}

			if (c.getTotal() != null) {
				this.sumTotalParcelas = this.sumTotalParcelas.add(c.getTotal());
			}

			this.sumTxJurosPacelaStr = UtilsMB.getBigdecimalAsString(this.sumTxJurosPacela);;
			this.sumMultaParcelaStr = UtilsMB.getBigdecimalAsString(this.sumMultaParcela);;
			this.sumVlrParcelaStr = UtilsMB.getBigdecimalAsString(this.sumVlrParcela);;
			this.sumHonorariosParcelaStr = UtilsMB.getBigdecimalAsString(this.sumHonorariosParcela);;
			this.sumTotalParcelasStr = UtilsMB.getBigdecimalAsString(this.sumTotalParcelas);;
		}
	}

	/*
	 * Limpa campos da tela de recalculo de valor.
	 */
	public String clearFieldsCalculo() {		

		this.contratoGerado = false;

		loadRecebedores();
		clearRecebedor();

		loadParametros();

		this.dtVencimento = null;
		this.dtPagamento = null;

		this.valorParcela = null;		
		this.valorAtualizado = null;

		this.qtdeDias = 0;

		loadCalculos();

		this.deleteMode = false;
		this.updateParcelaMode = false;

		this.calculoPDFGerado = false;
		this.pathPDF = "";
		this.nomePDF = "";
		this.file = null;
		
		clearRecebedor();

		return "/Atendimento/Cobranca/ContratoCobrancaCalculo.xhtml";
	}

	public String clearFieldsEditCalculo() {
		loadRecebedores();

		if (this.objetoCalculo.getRecebedor() != null) {
			this.idRecebedor = this.objetoCalculo.getRecebedor() .getId();
			this.nomeRecebedor = this.objetoCalculo.getRecebedor() .getNome();
			this.selectedRecebedor = this.objetoCalculo.getRecebedor();
		}

		sumsParcelas();
		clearCalculoDetalhes();

		this.calculoPDFGerado = false;
		this.pathPDF = "";
		this.nomePDF = "";
		this.file = null;
		
		this.dataAtualizacaoBkp = this.objetoCalculo.getDataAtualizacao();

		return "/Atendimento/Cobranca/ContratoCobrancaCalculoInserir.xhtml";
	}

	public void clearFieldsEditParcelaCalculo() {
		this.dataVencimento = this.calculosDetalhes.getDataVencimento();
		this.dataPagamento = this.calculosDetalhes.getDataPagamento();
		this.multaParcela = this.calculosDetalhes.getMulta();
		this.txJurosPacela = this.calculosDetalhes.getTxJuros();
		this.totalParcela = this.calculosDetalhes.getTotal();
		this.vlrParcela = this.calculosDetalhes.getVlrParcela();
		this.numeroParcela = this.calculosDetalhes.getNumeroParcela();
		this.honorarios = this.calculosDetalhes.getHonorarios();
		this.observacaoParcela = this.calculosDetalhes.getObservacao();
	}

	public void loadCalculos() {
		CalculoDao calculoDao = new CalculoDao();
		this.listCalculos = calculoDao.findAll();
	}

	public void clearCalculoDetalhes() {
		this.calculosDetalhes = new CalculosDetalhes();

		this.dataVencimento = null;
		this.dataPagamento = this.objetoCalculo.getDataAtualizacao();
		this.vlrParcela = null;
		this.txJurosPacela = this.objetoCalculo.getTxJuros();
		this.multaParcela = this.objetoCalculo.getMulta();
		this.honorarios = this.objetoCalculo.getHonorarios();
		this.totalParcela = new BigDecimal("0.00");
		
		this.observacaoParcela = "";
		
		this.numeroParcela = 0;
	}
	
	public void atualizaTaxas() {
		this.txJurosPacela = this.objetoCalculo.getTxJuros();
		this.multaParcela = this.objetoCalculo.getMulta();
		this.honorarios = this.objetoCalculo.getHonorarios();
		this.dataPagamento = this.objetoCalculo.getDataAtualizacao();
		
		atualizaDataDeAtualizacao();
		
		this.dataAtualizacaoBkp = this.objetoCalculo.getDataAtualizacao();
	}

	public String addCalculo() {
		this.objetoCalculo = new Calculos();

		this.objetoCalculo.setDataCalculo(gerarDataHoje());
		this.objetoCalculo.setDataAtualizacao(gerarDataHoje());
		this.objetoCalculo.setTxJuros(new BigDecimal("10.00"));
		this.objetoCalculo.setMulta(new BigDecimal("2.00"));
		this.objetoCalculo.setImprimeTaxas(true);

		this.objetoCalculo.setListCalculoDetalhes(new ArrayList<CalculosDetalhes>());
		
		loadRecebedores();
		clearRecebedor();
		clearCalculoDetalhes();
		clearSumsParcelas();

		this.calculoPDFGerado = false;
		this.pathPDF = "";
		this.nomePDF = "";
		this.file = null;
		
		this.dataAtualizacaoBkp = this.objetoCalculo.getDataAtualizacao();

		return "/Atendimento/Cobranca/ContratoCobrancaCalculoInserir.xhtml";
	}

	public void addParcela() {
		FacesContext context = FacesContext.getCurrentInstance();

		if (this.vlrParcela == null || this.dataVencimento == null) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Cálculo: Erro de validação: Para inserir a parcela é necessário preencher a data de vencimento e o valor da parcela!", ""));			
		} else {
			if (this.dataPagamento == null) {
				this.dataPagamento = this.objetoCalculo.getDataAtualizacao();
			}

			if (this.objetoCalculo.getListCalculoDetalhes().size() > 0) {
				this.calculosDetalhes.setNumeroParcela(this.objetoCalculo.getListCalculoDetalhes().size() + 1);
			} else {
				this.calculosDetalhes.setNumeroParcela(1);
			}		

			this.calculosDetalhes.setDataVencimento(this.dataVencimento);
			this.calculosDetalhes.setDataPagamento(this.dataPagamento);
			this.calculosDetalhes.setMulta(this.multaParcela);
			this.calculosDetalhes.setTxJuros(this.txJurosPacela);
			this.calculosDetalhes.setTotal(this.totalParcela);
			this.calculosDetalhes.setVlrParcela(this.vlrParcela);
			this.calculosDetalhes.setHonorarios(this.honorarios);
			this.calculosDetalhes.setObservacao(this.observacaoParcela);
			

			if (this.vlrParcela.compareTo(BigDecimal.ZERO) == -1) {				
				zeraParcelas();
			} else {
				calculaValorTotalParcela();
			}

			this.objetoCalculo.getListCalculoDetalhes().add(this.calculosDetalhes);
			
			sumsParcelas();

			clearCalculoDetalhes();
		}
	}
	
	public void zeraParcelas() {
		this.calculosDetalhes.setTxJuros(new BigDecimal("0.00"));
		this.calculosDetalhes.setVlrTxJuros(new BigDecimal("0.00"));
		this.calculosDetalhes.setHonorarios(new BigDecimal("0.00"));
		this.calculosDetalhes.setVlrHonorarios(new BigDecimal("0.00"));
		this.calculosDetalhes.setMulta(new BigDecimal("0.00"));
		this.calculosDetalhes.setVlrMulta(new BigDecimal("0.00"));
		this.calculosDetalhes.setTotal(this.calculosDetalhes.getVlrParcela());
	}

	public void editParcela() {

		for (CalculosDetalhes cd : this.objetoCalculo.getListCalculoDetalhes()) {
			if (cd.getNumeroParcela() == this.numeroParcela) {
						
				this.calculosDetalhes.setDataVencimento(this.dataVencimento);
				this.calculosDetalhes.setDataPagamento(this.dataPagamento);
				this.calculosDetalhes.setMulta(this.multaParcela);
				this.calculosDetalhes.setTxJuros(this.txJurosPacela);
				this.calculosDetalhes.setTotal(this.totalParcela);
				this.calculosDetalhes.setVlrParcela(this.vlrParcela);	
				this.calculosDetalhes.setHonorarios(this.honorarios);
				this.calculosDetalhes.setObservacao(this.observacaoParcela);
				
				if (this.vlrParcela.compareTo(BigDecimal.ZERO) == -1) {				
					zeraParcelas();
				} else {
					calculaValorTotalParcela();
				}			
			}
		}

		clearCalculoDetalhes();
				
		sumsParcelas();
		
		this.updateParcelaMode = false;
	}
	
	public void atualizaDataDeAtualizacao() {
		for (CalculosDetalhes cd : this.objetoCalculo.getListCalculoDetalhes()) {
			if (compareDate(cd.getDataPagamento(), this.dataAtualizacaoBkp)) {
				this.calculosDetalhes = new CalculosDetalhes();
				
				loadCalculoDetalhesFor(cd);
				
				calculaValorTotalParcela();
				
				cd.setDataVencimento(this.dataVencimento);
				cd.setDataPagamento(this.dataPagamento);
				cd.setMulta(this.multaParcela);
				cd.setTxJuros(this.txJurosPacela);
				cd.setVlrParcela(this.vlrParcela);	
				cd.setHonorarios(this.honorarios);
				cd.setObservacao(this.observacaoParcela);
				
				cd.setTotal(this.calculosDetalhes.getTotal());
				cd.setVlrHonorarios(this.calculosDetalhes.getVlrHonorarios());
				cd.setVlrMulta(this.calculosDetalhes.getVlrMulta());
				cd.setVlrTxJuros(this.calculosDetalhes.getVlrTxJuros());		
			}
		}
		
		sumsParcelas();
		
		clearCalculoDetalhes();		
	}
	
	public boolean compareDate(Date data1, Date data2) {
		
		TimeZone zone = TimeZone.getTimeZone("GMT-03:00");  
		Locale locale = new Locale("pt", "BR"); 

		Calendar date = Calendar.getInstance(zone, locale);  

		SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MMM/yyyy", locale);;

		if (sdfDataRel.format(data1).equals(sdfDataRel.format(data2))) {
			return true;
		} else {
			return false;
		}
	}
	
	public void loadCalculoDetalhesFor(CalculosDetalhes calculosDetalhesParam) {
		this.dataVencimento = calculosDetalhesParam.getDataVencimento();
		this.dataPagamento = this.objetoCalculo.getDataAtualizacao();
		this.multaParcela = calculosDetalhesParam.getMulta();
		this.txJurosPacela = calculosDetalhesParam.getTxJuros();
		this.totalParcela = calculosDetalhesParam.getTotal();
		this.vlrParcela = calculosDetalhesParam.getVlrParcela();
		this.numeroParcela = calculosDetalhesParam.getNumeroParcela();
		this.honorarios = calculosDetalhesParam.getHonorarios();
		this.observacaoParcela = calculosDetalhesParam.getObservacao();
	}

	public String editCalculo() {
		this.objetoCalculo.setDataAtualizacao(gerarDataHoje());

		return "/Atendimento/Cobranca/ContratoCobrancaCalculoInserir.xhtml";
	}

	public String saveCalculo() {
		CalculoDao calculoDao = new CalculoDao();
		FacesContext context = FacesContext.getCurrentInstance();

		if (this.selectedRecebedor.getId() > 0) {
			this.objetoCalculo.setRecebedor(this.selectedRecebedor);
		}		

		String msgRetorno = "";

		if (this.objetoCalculo.getId() <= 0) {
			calculoDao.create(this.objetoCalculo);
			msgRetorno = "criado";
		} else {
			calculoDao.merge(this.objetoCalculo);
			msgRetorno = "atualizado";
		}

		loadCalculos();

		context.addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_INFO, "Contrato Cobrança: Cálculo "
						+ msgRetorno + " com sucesso!"
						, ""));

		return "/Atendimento/Cobranca/ContratoCobrancaCalculo.xhtml";
	}
	
	public void saveCalculoPreXLS() {
		saveCalculo();
		impressaoXLS();
	}
	
	public void saveCalculoPrePDF() {
		saveCalculo();
		impressaoPDF();
	}
	
	public void impressaoXLS() {
		ParametrosDao pDao = new ParametrosDao(); 
		this.pathPDF = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomePDF = "CÁLCULO - " + this.objetoCalculo.getIdentificacaoCalculo() + ".xls";  					

		TimeZone zone = TimeZone.getTimeZone("GMT-03:00");  
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		dataHoje.set(Calendar.HOUR_OF_DAY, 0);  
		dataHoje.set(Calendar.MINUTE, 0);  
		dataHoje.set(Calendar.SECOND, 0);  
		dataHoje.set(Calendar.MILLISECOND, 0);

		String excelFileName = this.pathPDF + this.nomePDF;//name of excel file

		String sheetName = "Resultado";//name of sheet

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetName);

		// define largura das colunas pelo indice da coluna
		sheet.setColumnWidth(0, 4500);
		sheet.setColumnWidth(1, 4500);
		sheet.setColumnWidth(2, 4500);
		sheet.setColumnWidth(3, 4500);
		sheet.setColumnWidth(4, 4500);
		sheet.setColumnWidth(5, 4500);
		sheet.setColumnWidth(6, 4500);
		sheet.setColumnWidth(7, 4500);		
		sheet.setColumnWidth(8, 9000);

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
		cell_style.setShrinkToFit(true);
		
		XSSFCellStyle cell_style_total = wb.createCellStyle();
		cell_style_total.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cell_style_total.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		font.setBold(true);
		cell_style_total.setFont(font);
		cell_style_total.setAlignment(HorizontalAlignment.CENTER);
		cell_style_total.setVerticalAlignment(VerticalAlignment.CENTER);
		cell_style_total.setBorderBottom(BorderStyle.THIN);
		cell_style_total.setBorderTop(BorderStyle.THIN);
		cell_style_total.setBorderRight(BorderStyle.THIN);
		cell_style_total.setBorderLeft(BorderStyle.THIN);
		cell_style_total.setWrapText(true);  	
		cell_style_total.setShrinkToFit(true);

		// cria estilo para dados em geral
		XSSFCellStyle cell_styleSimple = wb.createCellStyle();
		cell_styleSimple.setAlignment(HorizontalAlignment.CENTER);
		cell_styleSimple.setVerticalAlignment(VerticalAlignment.CENTER);
		cell_styleSimple.setBorderBottom(BorderStyle.THIN);
		cell_styleSimple.setBorderTop(BorderStyle.THIN);
		cell_styleSimple.setBorderRight(BorderStyle.THIN);
		cell_styleSimple.setBorderLeft(BorderStyle.THIN);
		cell_styleSimple.setWrapText(true);  
		
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
		dateStyle.setDataFormat((short)BuiltinFormats.getBuiltinFormat("m/d/yy"));
		
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
		numericStyle.setDataFormat(ch.createDataFormat().getFormat("_(R$* #,##0.00_);_(R$* (#,##0.00);_(R$* \"-\"??_);_(@_)"));
		
		CellStyle numericStyleTotal = wb.createCellStyle();
		numericStyleTotal.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		numericStyleTotal.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		numericStyleTotal.setAlignment(HorizontalAlignment.CENTER);
		numericStyleTotal.setVerticalAlignment(VerticalAlignment.CENTER);
		numericStyleTotal.setBorderBottom(BorderStyle.THIN);
		numericStyleTotal.setBorderTop(BorderStyle.THIN);
		numericStyleTotal.setBorderRight(BorderStyle.THIN);
		numericStyleTotal.setBorderLeft(BorderStyle.THIN);
		numericStyleTotal.setWrapText(true);
		font.setBold(true);
		numericStyleTotal.setFont(font);
		// cria a formatação para moeda             			
		numericStyleTotal.setDataFormat(ch.createDataFormat().getFormat("_(R$* #,##0.00_);_(R$* (#,##0.00);_(R$* \"-\"??_);_(@_)"));


		int countLine = 0;
		XSSFRow row = sheet.createRow(countLine);
		XSSFCell cell;   				
		
		cell = row.createCell(0);
		cell.setCellValue("IDENTIFICAÇÃO");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("PAGADOR");
		cell.setCellStyle(cell_style);
		
		countLine ++;
		row = sheet.createRow(countLine);	
		
		cell = row.createCell(0);
		cell.setCellStyle(cell_styleSimple);
		cell.setCellValue(this.objetoCalculo.getIdentificacaoCalculo());
		
		cell = row.createCell(1);
		cell.setCellStyle(cell_styleSimple);
		if (this.objetoCalculo.getRecebedor() != null) {
			cell.setCellValue(this.objetoCalculo.getRecebedor().getNome());
		}
		
		countLine ++;
		row = sheet.createRow(countLine);	
		
		cell = row.createCell(0);
		cell.setCellValue("DATA CÁLCULO");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("DATA ATUALIZAÇÃO");
		cell.setCellStyle(cell_style);
		
		countLine ++;
		row = sheet.createRow(countLine);	
		
		cell = row.createCell(0);
		cell.setCellStyle(dateStyle);
		cell.setCellValue(this.objetoCalculo.getDataCalculo());
		
		cell = row.createCell(1);
		cell.setCellStyle(dateStyle);
		if (this.objetoCalculo.getRecebedor() != null) {
			cell.setCellValue(this.objetoCalculo.getDataAtualizacao());
		}
		
		countLine ++;
		row = sheet.createRow(countLine);	
		
		cell = row.createCell(0);
		cell.setCellValue("TX JUROS (%)");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("MULTA (%)");
		cell.setCellStyle(cell_style);
		
		countLine ++;
		row = sheet.createRow(countLine);	
		
		cell = row.createCell(0);	
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		cell.setCellValue(((BigDecimal) this.objetoCalculo.getTxJuros()).doubleValue());
		
		cell = row.createCell(1);	
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		cell.setCellValue(((BigDecimal) this.objetoCalculo.getMulta()).doubleValue());
		
		countLine ++;
		row = sheet.createRow(countLine);	
		
		cell = row.createCell(0);
		cell.setCellValue("HONORÁRIOS (%)");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("OBSERVAÇÃO");
		cell.setCellStyle(cell_style);
		
		countLine ++;
		row = sheet.createRow(countLine);	
		
		cell = row.createCell(0);	
		cell.setCellStyle(numericStyle);
		cell.setCellType(CellType.NUMERIC);
		cell.setCellValue(((BigDecimal) this.objetoCalculo.getHonorarios()).doubleValue());
		
		cell = row.createCell(1);
		cell.setCellStyle(cell_styleSimple);
		cell.setCellValue(this.objetoCalculo.getDescricao());
		
		//iterating r number of rows
		// cria CABEÇALHO
		countLine ++;
		countLine ++;
		row = sheet.createRow(countLine);		
		cell = row.createCell(0);
		cell.setCellValue("PARCELA");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("DATA VENCIMENTO");
		cell.setCellStyle(cell_style);
		cell = row.createCell(2);
		cell.setCellValue("DATA ATUALIZAÇÃO");
		cell.setCellStyle(cell_style);
		cell = row.createCell(3);
		cell.setCellValue("VALOR");
		cell.setCellStyle(cell_style);
		
		if (this.objetoCalculo.isImprimeTaxas()) {
			cell = row.createCell(4);
			cell.setCellValue("JUROS");
			cell.setCellStyle(cell_style);
			cell = row.createCell(5);
			cell.setCellValue("MULTA");
			cell.setCellStyle(cell_style);
			cell = row.createCell(6);
			cell.setCellValue("HONORÁRIOS");
			cell.setCellStyle(cell_style);	
			cell = row.createCell(7);
			cell.setCellValue("TOTAL");
			cell.setCellStyle(cell_style);
			cell = row.createCell(8);
			cell.setCellValue("OBSERVAÇÃO");
			cell.setCellStyle(cell_style);
		} else {
			cell = row.createCell(4);
			cell.setCellValue("TOTAL");
			cell.setCellStyle(cell_style);
			cell = row.createCell(5);
			cell.setCellValue("OBSERVAÇÃO");
			cell.setCellStyle(cell_style);			
		}

		// cria estilo para dados em geral
		cell_style = wb.createCellStyle();
		cell_style.setAlignment(HorizontalAlignment.CENTER);
		cell_style.setVerticalAlignment(VerticalAlignment.CENTER);
		cell_style.setBorderBottom(BorderStyle.THIN);
		cell_style.setBorderTop(BorderStyle.THIN);
		cell_style.setBorderRight(BorderStyle.THIN);
		cell_style.setBorderLeft(BorderStyle.THIN);
		cell_style.setWrapText(true);  
		
		BigDecimal totalValorParcela = new BigDecimal("0.00");
		BigDecimal totalValorTotal = new BigDecimal("0.00");
		BigDecimal totalVlrTxJuros = new BigDecimal("0.00");
		BigDecimal totalVlrMulta = new BigDecimal("0.00");
		BigDecimal totalVlrHonorarios = new BigDecimal("0.00");

		if (this.objetoCalculo.getListCalculoDetalhes().size() > 0) {
			for (CalculosDetalhes list : this.objetoCalculo.getListCalculoDetalhes()) {
				countLine ++;
				row = sheet.createRow(countLine);

				//getNumeroParcela
				cell = row.createCell(0);
				cell.setCellStyle(cell_style);
				cell.setCellValue(list.getNumeroParcela());	

				//getDataVencimento
				cell = row.createCell(1);
				cell.setCellStyle(dateStyle);
				cell.setCellValue(list.getDataVencimento());

				//getDataPagamento
				cell = row.createCell(2);
				cell.setCellStyle(dateStyle);
				cell.setCellValue(list.getDataPagamento());

				//doubleValue
				cell = row.createCell(3);	
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(((BigDecimal) list.getVlrParcela()).doubleValue());
				
				// calc total
				totalValorParcela = totalValorParcela.add(list.getVlrParcela());
				totalValorTotal = totalValorTotal.add(list.getTotal());
				
				if (list.getTxJuros() != null) {
					totalVlrTxJuros = totalVlrTxJuros.add(list.getVlrTxJuros());
				}
				
				if (list.getVlrMulta() != null) {
					totalVlrMulta = totalVlrMulta.add(list.getVlrMulta());
				}
				
				if (list.getVlrHonorarios() != null) {
					totalVlrHonorarios = totalVlrHonorarios.add(list.getVlrHonorarios());
				}		

				if (this.objetoCalculo.isImprimeTaxas()) {
					//doubleValue
					cell = row.createCell(4);
					cell.setCellStyle(numericStyle);
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(((BigDecimal) list.getVlrTxJuros()).doubleValue());	

					//getVlrMulta
					cell = row.createCell(5);
					cell.setCellStyle(numericStyle);
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(((BigDecimal) list.getVlrMulta()).doubleValue());	

					//doubleValue
					cell = row.createCell(6);
					cell.setCellStyle(numericStyle);
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(((BigDecimal) list.getVlrHonorarios()).doubleValue());	
					
					//getTotal
					cell = row.createCell(7);
					cell.setCellStyle(numericStyle);
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(((BigDecimal) list.getTotal()).doubleValue());	
					
					//getObservacao
					cell = row.createCell(8);
					cell.setCellStyle(cell_style);
					cell.setCellValue(list.getObservacao());
				} else {
					//getTotal
					cell = row.createCell(4);
					cell.setCellStyle(numericStyle);
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(((BigDecimal) list.getTotal()).doubleValue());
					
					//getObservacao
					cell = row.createCell(5);
					cell.setCellStyle(cell_style);
					cell.setCellValue(list.getObservacao());
				}
			
			}
			
			// print total
			countLine ++;
			row = sheet.createRow(countLine);		
			cell = row.createCell(2);
			cell.setCellValue("TOTAL");
			cell.setCellStyle(cell_style_total);
				
			cell = row.createCell(3);
			cell.setCellStyle(numericStyleTotal);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(((BigDecimal) totalValorParcela).doubleValue());	
			
			if (this.objetoCalculo.isImprimeTaxas()) {
				cell = row.createCell(4);
				cell.setCellStyle(numericStyleTotal);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(((BigDecimal) totalVlrTxJuros).doubleValue());	
		
				cell = row.createCell(5);
				cell.setCellStyle(numericStyleTotal);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(((BigDecimal) totalVlrMulta).doubleValue());	
			
				cell = row.createCell(6);
				cell.setCellStyle(numericStyleTotal);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(((BigDecimal) totalVlrHonorarios).doubleValue());	
					
				cell = row.createCell(7);
				cell.setCellStyle(numericStyleTotal);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(((BigDecimal) totalValorTotal).doubleValue());	
			} else {
				cell = row.createCell(4);
				cell.setCellStyle(numericStyleTotal);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(((BigDecimal) totalValorTotal).doubleValue());	
			}
		}

		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(excelFileName);
			//write this workbook to an Outputstream.
			wb.write(fileOut);
			fileOut.flush();
			fileOut.close();
			
			this.calculoPDFGerado = true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.calculoPDFGerado = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.calculoPDFGerado = false;
		}
	}	
	
	public final void excluirParcela() {		
		boolean next = false;
		for (CalculosDetalhes c : this.objetoCalculo.getListCalculoDetalhes()) {
			if (!next) {
				if (c.getId() == this.calculosDetalhes.getId()) {
					next = true;
				}
			} else {
				c.setNumeroParcela(c.getNumeroParcela() - 1);
			}
		}
		
		this.objetoCalculo.getListCalculoDetalhes().remove(this.calculosDetalhes);
	}

	public void calculaValorTotalParcela() {
		
		BigDecimal valorHonorario = null;
		if (this.honorarios != null) {
			if (this.vlrParcela != null) {
				//calcula Multa
				double honorariosCalc = (this.honorarios.doubleValue() / 100);
				valorHonorario = this.vlrParcela;
				valorHonorario = (valorHonorario.add(this.vlrParcela.multiply(BigDecimal.valueOf(honorariosCalc))).subtract(this.vlrParcela));
				this.calculosDetalhes.setVlrHonorarios(valorHonorario.setScale(2, BigDecimal.ROUND_UP));
			}
		}		
		
		if (this.dataVencimento != null) {
			this.qtdeDias = qtdeDiasCalculado(this.dataVencimento, this.dataPagamento); 

			if (this.qtdeDias < 0) { 
				this.qtdeDias = -this.qtdeDias;
			}

			if (this.qtdeDias > 0) {
				if (!this.txJurosPacela.equals(BigDecimal.ZERO)) {
					//calcula Indice da Taxa de Juros		
					double percentual = ((this.txJurosPacela.doubleValue() / 100) / 30) * this.qtdeDias;
					//Calcula valor Atualizado apenas com o Juros
					this.totalParcela = this.vlrParcela.add(this.vlrParcela.multiply(BigDecimal.valueOf(percentual)));
					
					this.calculosDetalhes.setVlrTxJuros(this.totalParcela.subtract(this.vlrParcela).setScale(2, BigDecimal.ROUND_UP));									
				} else {
					this.totalParcela = this.vlrParcela;
				}

				if (!this.multaParcela.equals(BigDecimal.ZERO)) {
					//calcula Multa
					double multa = (this.multaParcela.doubleValue() / 100);
					BigDecimal valorMulta = this.vlrParcela;
					valorMulta = (valorMulta.add(this.vlrParcela.multiply(BigDecimal.valueOf(multa))).subtract(this.vlrParcela));		
					
					this.calculosDetalhes.setVlrMulta(valorMulta.setScale(2, BigDecimal.ROUND_UP));
					
					//Calcula valor Atualizado com a multa
					this.totalParcela = this.totalParcela.add(valorMulta);		
				}

				if (valorHonorario != null) {
					this.totalParcela = this.totalParcela.add(valorHonorario);
				}
			} else {
				this.totalParcela = this.vlrParcela;
			}
		} else {
			this.totalParcela = this.vlrParcela;

			if (valorHonorario != null) {
				this.totalParcela = this.totalParcela.add(valorHonorario);
			}
		}

		//Seta para apenas 2 casas decimais
		this.totalParcela = this.totalParcela.setScale(2, RoundingMode.HALF_EVEN);
		
		this.calculosDetalhes.setTotal(this.totalParcela);
		
		// valida valores nulos
		if (this.calculosDetalhes.getVlrTxJuros() == null) {
			this.calculosDetalhes.setVlrTxJuros(new BigDecimal("0.00"));
		}
		if (this.calculosDetalhes.getVlrMulta() == null) {
			this.calculosDetalhes.setVlrMulta(new BigDecimal("0.00"));		
		}
		if (this.calculosDetalhes.getVlrHonorarios() == null) {
			this.calculosDetalhes.setVlrMulta(new BigDecimal("0.00"));
		}
	}

	public String deleteCalculo() {
		CalculoDao calculoDao = new CalculoDao();
		FacesContext context = FacesContext.getCurrentInstance();

		String msgRetorno = "";

		calculoDao.delete(this.objetoCalculo);
		msgRetorno = "excluído";

		loadCalculos();

		context.addMessage(null, new FacesMessage(
				FacesMessage.SEVERITY_INFO, "Contrato Cobrança: Cálculo "
						+ msgRetorno + " com sucesso!"
						, ""));

		return "/Atendimento/Cobranca/ContratoCobrancaCalculo.xhtml";
	}

	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getTimeZone("GMT-03:00");  
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}

	/*
	 * Método responsável pelo recalculo do valor
	 */
	public void recalculaValor() {
		if (this.dtVencimento != null) {
			this.qtdeDias = qtdeDiasCalculado(this.dtVencimento, this.dtPagamento); 

			if (this.qtdeDias > 0) {
				if (!this.txJuros.equals(BigDecimal.ZERO)) {
					//calcula Indice da Taxa de Juros		
					double percentual = ((this.txJuros.doubleValue() / 100) / 30) * this.qtdeDias;
					//Calcula valor Atualizado apenas com o Juros
					this.valorAtualizado = this.valorParcela.add(this.valorParcela.multiply(BigDecimal.valueOf(percentual)));
				} else {
					this.valorAtualizado = this.valorParcela;
				}

				if (!this.multa.equals(BigDecimal.ZERO)) {
					//calcula Multa
					double multa = (this.multa.doubleValue() / 100);
					BigDecimal valorMulta = this.valorParcela;
					valorMulta = (valorMulta.add(this.valorParcela.multiply(BigDecimal.valueOf(multa))).subtract(this.valorParcela));		

					//Calcula valor Atualizado com a multa
					this.valorAtualizado = this.valorAtualizado.add(valorMulta);		
				}

				//Seta para apenas 2 casas decimais
				this.valorAtualizado = this.valorAtualizado.setScale(2, RoundingMode.HALF_EVEN);
			} else {
				this.valorAtualizado = null;
			}
		} else {
			this.valorAtualizado = this.valorParcela;
		}
	}	

	/*
	 * Método responsável pelo recalculo do valor
	 */
	public void recalculaValorSemMulta() {
		this.qtdeDias = qtdeDiasCalculado(this.dtVencimento, this.dtPagamento); 

		if (this.qtdeDias > 0) {
			if (!this.txJuros.equals(BigDecimal.ZERO)) {
				//calcula Indice da Taxa de Juros		
				double percentual = ((this.txJuros.doubleValue() / 100) / 30) * this.qtdeDias;
				//Calcula valor Atualizado apenas com o Juros
				this.valorAtualizado = this.valorParcela.add(this.valorParcela.multiply(BigDecimal.valueOf(percentual)));
			} else {
				this.valorAtualizado = this.valorParcela;
			}

			//Seta para apenas 2 casas decimais
			this.valorAtualizado = this.valorAtualizado.setScale(2, RoundingMode.HALF_EVEN);
		} else {
			this.valorAtualizado = null;
		}
	}	

	/*
	 * Limpa campos da tela de recalculo de valor.
	 */
	public String clearFieldsRecalculaValor() {		

		this.contratoGerado = false;

		loadRecebedores();
		clearRecebedor();

		loadParametros();

		this.dtVencimento = null;
		this.dtPagamento = null;

		this.valorParcela = null;		
		this.valorAtualizado = null;

		this.qtdeDias = 0;

		return "/Atendimento/Cobranca/ContratoCobrancaRecalculo.xhtml";
	}


	/*
	 * Le os parametros do BD
	 */
	public void loadParametros() {
		ParametrosDao pDao = new ParametrosDao(); 
		this.txJuros = pDao.findByFilter("nome","COBRANCA_REC_TX_JUROS").get(0).getValorBigDecimal();
		this.multa = pDao.findByFilter("nome","COBRANCA_REC_MULTA").get(0).getValorBigDecimal();
	}

	/*
	 * Le os recebedores do BD
	 */
	public final void loadRecebedores() {
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listRecebedores = pagadorRecebedorDao.findAll();
	}

	/*
	 * Método que seta o recebedor selecionado
	 */
	public final void populateSelectedRecebedor() {
		if (this.selectedRecebedor != null) {
			this.idRecebedor = this.selectedRecebedor.getId();
			this.nomeRecebedor = this.selectedRecebedor.getNome();
			this.objetoCalculo.setRecebedor(this.selectedRecebedor);
		}
	}

	/*
	 * Método que limpa a seleção de um recebedor
	 */
	public void clearRecebedor() {
		this.idRecebedor = 0;
		this.nomeRecebedor = null;
		this.selectedRecebedor = new PagadorRecebedor();
	}

	/*
	 * GETs e SETs
	 */

	/**
	 * @return the dtVencimento
	 */
	public Date getDtVencimento() {
		return dtVencimento;
	}

	/**
	 * @param dtVencimento the dtVencimento to set
	 */
	public void setDtVencimento(Date dtVencimento) {	
		if (dtVencimento != null) {
			Locale locale = new Locale("pt", "BR");  
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
			String auxDtPagamentoStr = sdf.format(dtVencimento.getTime());
			try {
				dtVencimento = sdf.parse(auxDtPagamentoStr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		this.dtVencimento = dtVencimento;
	}

	/**
	 * @return the dtPagamento
	 */
	public Date getDtPagamento() {
		return dtPagamento;
	}

	/**
	 * @param dtPagamento the dtPagamento to set
	 */
	public void setDtPagamento(Date dtPagamento) {
		Locale locale = new Locale("pt", "BR");  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
		String auxDtPagamentoStr = sdf.format(dtPagamento.getTime());
		try {
			dtPagamento = sdf.parse(auxDtPagamentoStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.dtPagamento = dtPagamento;
	}


	public void impressaoPDF(){	
		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.VGpT0_nF_h4
		 */ 		

		Document doc = null;
		OutputStream os = null;
		try {
			/*
			 *  Fonts Utilizadas no PDF
			 */
			Font header = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);	    	
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getTimeZone("GMT-03:00");  
			Locale locale = new Locale("pt", "BR"); 

			Calendar date = Calendar.getInstance(zone, locale);  

			SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MMM/yyyy", locale);
			SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss", locale);
			
			DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");

			ParametrosDao pDao = new ParametrosDao(); 

			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */
			doc = new Document(PageSize.A4.rotate(), 10, 10, 10, 10);
			this.pathPDF = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
			this.nomePDF = "Cálculo - " + this.objetoCalculo.getIdentificacaoCalculo() + ".pdf";
			os = new FileOutputStream(this.pathPDF + this.nomePDF);  	

			// Associa a stream de saída ao 
			PdfWriter.getInstance(doc, os);

			// Abre o documento
			doc.open();      

			Paragraph p1 = new Paragraph("CÁLCULO - " + this.objetoCalculo.getIdentificacaoCalculo(), titulo);
			p1.setAlignment(Element.ALIGN_CENTER);
			p1.setSpacingAfter(10);
			doc.add(p1);   
			/*
			if (this.tipoFiltros) {
				p1 = new Paragraph("RELATÓRIO FINANCEIRO DE COBRANÇA - " + sdfDataRel.format(this.relDataContratoInicio) + " a " + sdfDataRel.format(this.relDataContratoFim), header);
			} else {
				if (relObjetoContratoCobranca.size() > 0) {
					p1 = new Paragraph("RELATÓRIO FINANCEIRO DE COBRANÇA - CONTRATO " + relObjetoContratoCobranca.get(0).getNumeroContrato(), header);
				}
			}
			 */

			PdfPTable table = new PdfPTable(new float[] { 0.16f, 0.16f});
			table.setWidthPercentage(100.0f); 

			PdfPCell cell1 = new PdfPCell(new Phrase("IDENTIFICAÇÃO", titulo));
			cell1.setBorderColor(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			cell1.setGrayFill(0.9f);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(10f);
			table.addCell(cell1);

			PdfPCell cell7 = new PdfPCell(new Phrase("PAGADOR", titulo));
			cell7.setBorderColor(BaseColor.BLACK);
			cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell7.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell7.setUseBorderPadding(true);
			cell7.setGrayFill(0.9f);
			cell7.setPaddingTop(2f);
			cell7.setPaddingBottom(2f);
			table.addCell(cell7);

			cell1 = new PdfPCell(new Phrase(this.objetoCalculo.getIdentificacaoCalculo()));
			cell1.setBorderColor(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			table.addCell(cell1);

			if (this.objetoCalculo.getRecebedor() != null) {
				cell7 = new PdfPCell(new Phrase(this.objetoCalculo.getRecebedor().getNome()));
			} else {
				cell7 = new PdfPCell(new Phrase(""));
			}
			cell7.setBorderColor(BaseColor.BLACK);
			cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell7.setPaddingTop(5f);
			cell7.setPaddingBottom(5f);
			table.addCell(cell7);  

			PdfPCell cell2 = new PdfPCell(new Phrase("DATA CÁLCULO ", titulo));
			cell2.setBorderColor(BaseColor.BLACK);
			cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell2.setUseBorderPadding(true);
			cell2.setGrayFill(0.9f);
			cell2.setPaddingTop(2f);
			cell2.setPaddingBottom(2f);
			table.addCell(cell2);

			PdfPCell cell3 = new PdfPCell(new Phrase("DATA ATUALIZAÇÃO", titulo));
			cell3.setBorderColor(BaseColor.BLACK);
			cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell3.setUseBorderPadding(true);
			cell3.setGrayFill(0.9f);
			cell3.setPaddingTop(2f);
			cell3.setPaddingBottom(2f);
			table.addCell(cell3);

			cell2 = new PdfPCell(new Phrase(sdfDataRel.format(this.objetoCalculo.getDataCalculo())));
			cell2.setBorderColor(BaseColor.BLACK);
			cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell2.setPaddingTop(5f);
			cell2.setPaddingBottom(5f);
			table.addCell(cell2);

			cell3 = new PdfPCell(new Phrase(sdfDataRel.format(this.objetoCalculo.getDataAtualizacao())));
			cell3.setBorderColor(BaseColor.BLACK);
			cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell3.setPaddingTop(5f);
			cell3.setPaddingBottom(5f);
			table.addCell(cell3);
			
			PdfPCell cell4;
			PdfPCell cell6;
			PdfPCell cell8;
			
			if (this.objetoCalculo.isImprimeTaxas()) {
				cell4 = new PdfPCell(new Phrase("TX JUROS (%)", titulo));
				cell4.setBorderColor(BaseColor.BLACK);
				cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell4.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell4.setUseBorderPadding(true);
				cell4.setGrayFill(0.9f);
				cell4.setPaddingTop(2f);
				cell4.setPaddingBottom(2f);
				table.addCell(cell4);
	
				cell6 = new PdfPCell(new Phrase("MULTA (%)", titulo));
				cell6.setBorderColor(BaseColor.BLACK);
				cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell6.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell6.setUseBorderPadding(true);
				cell6.setGrayFill(0.9f);
				cell6.setPaddingTop(2f);
				cell6.setPaddingBottom(2f);
				table.addCell(cell6);
	
				if (this.objetoCalculo.getTxJuros() == null) {
					cell4 = new PdfPCell(new Phrase("0,00 %"));
				} else {
					cell4 = new PdfPCell(new Phrase(this.objetoCalculo.getTxJuros().toString()+ " % "));
				}
				
				cell4.setBorderColor(BaseColor.BLACK);
				cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell4.setPaddingTop(5f);
				cell4.setPaddingBottom(5f);
				table.addCell(cell4);  
	
				if (this.objetoCalculo.getTxJuros() == null) {
					cell6 = new PdfPCell(new Phrase("0,00 %"));
				} else {
					cell6 = new PdfPCell(new Phrase(this.objetoCalculo.getMulta()+ " % "));
				}
				cell6.setBorderColor(BaseColor.BLACK);
				cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell6.setPaddingTop(5f);
				cell6.setPaddingBottom(5f);
				table.addCell(cell6);  
				
				cell7 = new PdfPCell(new Phrase("HONORÁRIOS (%)", titulo));
				cell7.setBorderColor(BaseColor.BLACK);
				cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell7.setUseBorderPadding(true);
				cell7.setGrayFill(0.9f);
				cell7.setPaddingTop(2f);
				cell7.setPaddingBottom(2f);
				table.addCell(cell7);
				
				cell8 = new PdfPCell(new Phrase("OBSERVAÇÃO", titulo));
				cell8.setBorderColor(BaseColor.BLACK);
				cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell8.setUseBorderPadding(true);
				cell8.setGrayFill(0.9f);
				cell8.setPaddingTop(2f);
				cell8.setPaddingBottom(2f);
				table.addCell(cell8);
				
				
				if (this.objetoCalculo.getHonorarios() == null) {
					cell7 = new PdfPCell(new Phrase("0,00 %"));
				} else {
					cell7 = new PdfPCell(new Phrase(this.objetoCalculo.getHonorarios()+ " % "));
				}
				cell7.setBorderColor(BaseColor.BLACK);
				cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setPaddingTop(5f);
				cell7.setPaddingBottom(5f);
				table.addCell(cell7);  
				
				cell8 = new PdfPCell(new Phrase(this.objetoCalculo.getDescricao()));
				cell8.setBorderColor(BaseColor.BLACK);
				cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setPaddingTop(5f);
				cell8.setPaddingBottom(5f);
				table.addCell(cell8);  
			} else {
				cell8 = new PdfPCell(new Phrase("OBSERVAÇÃO", titulo));
				cell8.setBorderColor(BaseColor.BLACK);
				cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell8.setUseBorderPadding(true);
				cell8.setGrayFill(0.9f);
				cell8.setPaddingTop(2f);
				cell8.setColspan(2);
				cell8.setPaddingBottom(2f);
				
				table.addCell(cell8);
				
				cell8 = new PdfPCell(new Phrase(this.objetoCalculo.getDescricao()));
				cell8.setBorderColor(BaseColor.BLACK);
				cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setPaddingTop(5f);
				cell8.setPaddingBottom(5f);
				cell8.setColspan(2);
				table.addCell(cell8);  
			}

			doc.add(table);

			p1 = new Paragraph("PARCELAS", titulo);
			p1.setAlignment(Element.ALIGN_CENTER);
			p1.setSpacingAfter(20);
			p1.setSpacingBefore(20);
			doc.add(p1); 

			BigDecimal totalValorParcela = new BigDecimal("0.00");
			BigDecimal totalValorTotal = new BigDecimal("0.00");
			BigDecimal totalVlrTxJuros = new BigDecimal("0.00");
			BigDecimal totalVlrMulta = new BigDecimal("0.00");
			BigDecimal totalVlrHonorarios = new BigDecimal("0.00");
			
			if (this.objetoCalculo.isImprimeTaxas()) {
				PdfPTable tableParcelas = new PdfPTable(new float[] { 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f});
				tableParcelas.setWidthPercentage(100.0f); 

				cell1 = new PdfPCell(new Phrase("PARCELA", titulo));
				cell1.setBorderColor(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell1.setUseBorderPadding(true);
				cell1.setGrayFill(0.9f);
				cell1.setPaddingTop(10f);
				cell1.setPaddingBottom(10f);
				tableParcelas.addCell(cell1);
				
				cell3 = new PdfPCell(new Phrase("DATA VENCIMENTO", titulo));
				cell3.setBorderColor(BaseColor.BLACK);
				cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell3.setUseBorderPadding(true);
				cell3.setGrayFill(0.9f);
				cell3.setPaddingTop(10f);
				cell3.setPaddingBottom(10f);
				tableParcelas.addCell(cell3);
				

				cell2 = new PdfPCell(new Phrase("DATA ATUALIZAÇÃO", titulo));
				cell2.setBorderColor(BaseColor.BLACK);
				cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell2.setUseBorderPadding(true);
				cell2.setGrayFill(0.9f);
				cell2.setPaddingTop(10f);
				cell2.setPaddingBottom(10f);
				tableParcelas.addCell(cell2);
				
				cell8 = new PdfPCell(new Phrase("VALOR", titulo));
				cell8.setBorderColor(BaseColor.BLACK);
				cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell8.setUseBorderPadding(true);
				cell8.setGrayFill(0.9f);
				cell8.setPaddingTop(10f);
				cell8.setPaddingBottom(10f);
				tableParcelas.addCell(cell8);				

				cell4 = new PdfPCell(new Phrase("JUROS", titulo));
				cell4.setBorderColor(BaseColor.BLACK);
				cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell4.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell4.setUseBorderPadding(true);
				cell4.setGrayFill(0.9f);
				cell4.setPaddingTop(10f);
				cell4.setPaddingBottom(10f);
				tableParcelas.addCell(cell4);

				cell6 = new PdfPCell(new Phrase("MULTA", titulo));
				cell6.setBorderColor(BaseColor.BLACK);
				cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell6.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell6.setUseBorderPadding(true);
				cell6.setGrayFill(0.9f);
				cell6.setPaddingTop(10f);
				cell6.setPaddingBottom(10f);
				tableParcelas.addCell(cell6);

				cell7 = new PdfPCell(new Phrase("HONORÁRIOS", titulo));
				cell7.setBorderColor(BaseColor.BLACK);
				cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell7.setUseBorderPadding(true);
				cell7.setGrayFill(0.9f);
				cell7.setPaddingTop(10f);
				cell7.setPaddingBottom(10f);
				tableParcelas.addCell(cell7);

				PdfPCell cell9 = new PdfPCell(new Phrase("TOTAL", titulo));
				cell9.setBorderColor(BaseColor.BLACK);
				cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell9.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell9.setUseBorderPadding(true);
				cell9.setGrayFill(0.9f);
				cell9.setPaddingTop(10f);
				cell9.setPaddingBottom(10f);
				tableParcelas.addCell(cell9);
				
				
				PdfPCell cell5 = new PdfPCell(new Phrase("OBSERVAÇÃO", titulo));
				cell5.setBorderColor(BaseColor.BLACK);
				cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell5.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell5.setUseBorderPadding(true);
				cell5.setGrayFill(0.9f);
				cell5.setPaddingTop(10f);
				cell5.setPaddingBottom(10f);
				tableParcelas.addCell(cell5);

				for (CalculosDetalhes cd : this.objetoCalculo.getListCalculoDetalhes()) {
					totalValorParcela = totalValorParcela.add(cd.getVlrParcela());
					totalValorTotal = totalValorTotal.add(cd.getTotal());
					
					if (cd.getTxJuros() != null) {
						totalVlrTxJuros = totalVlrTxJuros.add(cd.getVlrTxJuros());
					}
					
					if (cd.getVlrMulta() != null) {
						totalVlrMulta = totalVlrMulta.add(cd.getVlrMulta());
					}
					
					if (cd.getVlrHonorarios() != null) {
						totalVlrHonorarios = totalVlrHonorarios.add(cd.getVlrHonorarios());
					}							

					cell1 = new PdfPCell(new Phrase(String.valueOf(cd.getNumeroParcela())));
					cell1.setBorderColor(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setPaddingTop(5f);
					cell1.setPaddingBottom(5f);
					tableParcelas.addCell(cell1);
					
					cell3 = new PdfPCell(new Phrase(sdfDataRel.format(cd.getDataVencimento())));
					cell3.setBorderColor(BaseColor.BLACK);
					cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell3.setPaddingTop(5f);
					cell3.setPaddingBottom(5f);
					tableParcelas.addCell(cell3);
					
					cell2 = new PdfPCell(new Phrase(sdfDataRel.format(cd.getDataPagamento())));
					cell2.setBorderColor(BaseColor.BLACK);
					cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell2.setPaddingTop(5f);
					cell2.setPaddingBottom(5f);
					tableParcelas.addCell(cell2);
					
					cell8 = new PdfPCell(new Phrase("R$ " + df.format(cd.getVlrParcela())));
					cell8.setBorderColor(BaseColor.BLACK);
					cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell8.setPaddingTop(5f);
					cell8.setPaddingBottom(5f);
					tableParcelas.addCell(cell8); 							

					if (cd.getVlrTxJuros() == null) {
						cell4 = new PdfPCell(new Phrase(""));
					} else {
						if (cd.getVlrTxJuros().toString().equals("0.00")) {
							cell4 = new PdfPCell(new Phrase("R$ 0,00"));
						} else {
							cell4 = new PdfPCell(new Phrase("R$ " + df.format(cd.getVlrTxJuros())));
						}						
					}
					
					cell4.setBorderColor(BaseColor.BLACK);
					cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell4.setPaddingTop(5f);
					cell4.setPaddingBottom(5f);
					tableParcelas.addCell(cell4);

					if (cd.getVlrMulta() == null) {
						cell6 = new PdfPCell(new Phrase(""));
					} else {
						if (cd.getVlrMulta().toString().equals("0.00")) {
							cell6 = new PdfPCell(new Phrase("R$ 0,00"));
						} else {
							cell6 = new PdfPCell(new Phrase("R$ " + df.format(cd.getVlrMulta())));
						}
					}
					
					cell6.setBorderColor(BaseColor.BLACK);
					cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell6.setPaddingTop(5f);
					cell6.setPaddingBottom(5f);
					tableParcelas.addCell(cell6);

					if (cd.getVlrHonorarios() == null) {
						cell7 = new PdfPCell(new Phrase(""));
					} else {
						if (cd.getVlrHonorarios().toString().equals("0.00")) {
							cell7 = new PdfPCell(new Phrase("R$ 0,00"));
						} else {
							cell7 = new PdfPCell(new Phrase("R$ " + df.format(cd.getVlrHonorarios())));
						}
					}
					
					cell7.setBorderColor(BaseColor.BLACK);
					cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell7.setPaddingTop(5f);
					cell7.setPaddingBottom(5f);
					tableParcelas.addCell(cell7); 

					if (cd.getTotal() == null) {
						cell9 = new PdfPCell(new Phrase("R$ 0,00"));
					} else {
						if (cd.getTotal().toString().equals("0.00")) {
							cell9 = new PdfPCell(new Phrase("R$ 0,00"));
						} else {
							cell9 = new PdfPCell(new Phrase("R$ " + df.format(cd.getTotal())));
						}
					}
					cell9.setBorderColor(BaseColor.BLACK);
					cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell9.setPaddingTop(5f);
					cell9.setPaddingBottom(5f);
					tableParcelas.addCell(cell9); 					
					
					cell5 = new PdfPCell(new Phrase(String.valueOf(cd.getObservacao())));
					cell5.setBorderColor(BaseColor.BLACK);
					cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell5.setPaddingTop(5f);
					cell5.setPaddingBottom(5f);
					tableParcelas.addCell(cell5);
				}	

				if (this.objetoCalculo.getListCalculoDetalhes().size() > 0) {
					cell1 = new PdfPCell(new Phrase(""));
					cell1.setBorderColor(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setPaddingTop(5f);
					cell1.setPaddingBottom(5f);
					tableParcelas.addCell(cell1);

					cell2 = new PdfPCell(new Phrase(""));
					cell2.setBorderColor(BaseColor.BLACK);
					cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell2.setPaddingTop(5f);
					cell2.setPaddingBottom(5f);
					tableParcelas.addCell(cell2);

					
					cell7 = new PdfPCell(new Phrase("TOTAL", titulo));
					cell7.setBorderColor(BaseColor.BLACK);
					cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell7.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell7.setUseBorderPadding(true);
					cell7.setGrayFill(0.9f);
					cell7.setPaddingTop(10f);
					cell7.setPaddingBottom(10f);
					tableParcelas.addCell(cell7);
					
					if (totalValorParcela.toString().equals("0.00")) {
						cell8 = new PdfPCell(new Phrase("R$ 0,00", titulo));
					} else {
						cell8 = new PdfPCell(new Phrase("R$ " + df.format(totalValorParcela), titulo));
					}
					cell8.setBorderColor(BaseColor.BLACK);
					cell8.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell8.setGrayFill(0.9f);
					cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell8.setPaddingTop(5f);
					cell8.setPaddingBottom(5f);
					tableParcelas.addCell(cell8); 					
					
					if (totalVlrTxJuros.toString().equals("0.00")) {
						cell3 = new PdfPCell(new Phrase("R$ 0,00", titulo));
					} else {
						cell3 = new PdfPCell(new Phrase("R$ " + df.format(totalVlrTxJuros), titulo));
					}
					cell3.setBorderColor(BaseColor.BLACK);
					cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell3.setGrayFill(0.9f);
					cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell3.setPaddingTop(5f);
					cell3.setPaddingBottom(5f);
					tableParcelas.addCell(cell3); 
					
					if (totalVlrMulta.toString().equals("0.00")) {
						cell4 = new PdfPCell(new Phrase("R$ 0,00", titulo));
					} else {
						cell4 = new PdfPCell(new Phrase("R$ " + df.format(totalVlrMulta), titulo));
					}
					cell4.setBorderColor(BaseColor.BLACK);
					cell4.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell4.setGrayFill(0.9f);
					cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell4.setPaddingTop(5f);
					cell4.setPaddingBottom(5f);
					tableParcelas.addCell(cell4); 
					
					if (totalVlrHonorarios.toString().equals("0.00")) {
						cell6 = new PdfPCell(new Phrase("R$ 0,00", titulo));
					} else {
						cell6 = new PdfPCell(new Phrase("R$ " + df.format(totalVlrHonorarios), titulo));
					}
					cell6.setBorderColor(BaseColor.BLACK);
					cell6.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell6.setGrayFill(0.9f);
					cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell6.setPaddingTop(5f);
					cell6.setPaddingBottom(5f);
					tableParcelas.addCell(cell6); 					

					if (totalValorTotal.toString().equals("0.00")) {
						cell9 = new PdfPCell(new Phrase("R$ 0,00", titulo));
					} else {
						cell9 = new PdfPCell(new Phrase("R$ " + df.format(totalValorTotal), titulo));
					}
					cell9.setBorderColor(BaseColor.BLACK);
					cell9.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell9.setGrayFill(0.9f);
					cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell9.setPaddingTop(5f);
					cell9.setPaddingBottom(5f);
					tableParcelas.addCell(cell9); 
					
					cell5 = new PdfPCell(new Phrase(""));
					cell5.setBorderColor(BaseColor.BLACK);
					cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell5.setPaddingTop(5f);
					cell5.setPaddingBottom(5f);
					tableParcelas.addCell(cell5);
				}

				doc.add(tableParcelas);
			} else {
				PdfPTable tableParcelas = new PdfPTable(new float[] { 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f});
				tableParcelas.setWidthPercentage(100.0f); 

				cell1 = new PdfPCell(new Phrase("PARCELA", titulo));
				cell1.setBorderColor(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell1.setUseBorderPadding(true);
				cell1.setGrayFill(0.9f);
				cell1.setPaddingTop(10f);
				cell1.setPaddingBottom(10f);
				tableParcelas.addCell(cell1);				

				cell3 = new PdfPCell(new Phrase("DATA VENCIMENTO", titulo));
				cell3.setBorderColor(BaseColor.BLACK);
				cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell3.setUseBorderPadding(true);
				cell3.setGrayFill(0.9f);
				cell3.setPaddingTop(10f);
				cell3.setPaddingBottom(10f);
				tableParcelas.addCell(cell3);				

				cell2 = new PdfPCell(new Phrase("DATA ATUALIZAÇÃO", titulo));
				cell2.setBorderColor(BaseColor.BLACK);
				cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell2.setUseBorderPadding(true);
				cell2.setGrayFill(0.9f);
				cell2.setPaddingTop(10f);
				cell2.setPaddingBottom(10f);
				tableParcelas.addCell(cell2);

				cell8 = new PdfPCell(new Phrase("VALOR", titulo));
				cell8.setBorderColor(BaseColor.BLACK);
				cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell8.setUseBorderPadding(true);
				cell8.setGrayFill(0.9f);
				cell8.setPaddingTop(10f);
				cell8.setPaddingBottom(10f);
				tableParcelas.addCell(cell8);

				PdfPCell cell9 = new PdfPCell(new Phrase("TOTAL", titulo));
				cell9.setBorderColor(BaseColor.BLACK);
				cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell9.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell9.setUseBorderPadding(true);
				cell9.setGrayFill(0.9f);
				cell9.setPaddingTop(10f);
				cell9.setPaddingBottom(10f);
				tableParcelas.addCell(cell9);				
				
				cell6 = new PdfPCell(new Phrase("OBSERVAÇÃO", titulo));
				cell6.setBorderColor(BaseColor.BLACK);
				cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell6.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell6.setUseBorderPadding(true);
				cell6.setGrayFill(0.9f);
				cell6.setPaddingTop(10f);
				cell6.setPaddingBottom(10f);
				tableParcelas.addCell(cell6);				

				for (CalculosDetalhes cd : this.objetoCalculo.getListCalculoDetalhes()) {					
					totalValorParcela = totalValorParcela.add(cd.getVlrParcela());
					totalValorTotal = totalValorTotal.add(cd.getTotal());

					cell1 = new PdfPCell(new Phrase(String.valueOf(cd.getNumeroParcela())));
					cell1.setBorderColor(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setPaddingTop(5f);
					cell1.setPaddingBottom(5f);
					tableParcelas.addCell(cell1);					

					cell3 = new PdfPCell(new Phrase(sdfDataRel.format(cd.getDataVencimento())));
					cell3.setBorderColor(BaseColor.BLACK);
					cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell3.setPaddingTop(5f);
					cell3.setPaddingBottom(5f);
					tableParcelas.addCell(cell3);					

					cell2 = new PdfPCell(new Phrase(sdfDataRel.format(cd.getDataPagamento())));
					cell2.setBorderColor(BaseColor.BLACK);
					cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell2.setPaddingTop(5f);
					cell2.setPaddingBottom(5f);
					tableParcelas.addCell(cell2);

					if (cd.getVlrParcela().toString().equals("0.00")) {
						cell8 = new PdfPCell(new Phrase("R$ 0,00"));
					} else {
						cell8 = new PdfPCell(new Phrase("R$ " + df.format(cd.getVlrParcela())));
					}
					cell8.setBorderColor(BaseColor.BLACK);
					cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell8.setPaddingTop(5f);
					cell8.setPaddingBottom(5f);
					tableParcelas.addCell(cell8); 

					if (cd.getTotal() == null) {
						cell9 = new PdfPCell(new Phrase("R$ 0,00"));
					} else {
						if (cd.getTotal().toString().equals("0.00")) {
							cell9 = new PdfPCell(new Phrase("R$ 0,00"));
						} else {
							cell9 = new PdfPCell(new Phrase("R$ " + df.format(cd.getTotal())));
						}
					}
					cell9.setBorderColor(BaseColor.BLACK);
					cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell9.setPaddingTop(5f);
					cell9.setPaddingBottom(5f);
					tableParcelas.addCell(cell9); 
										
					cell6 = new PdfPCell(new Phrase(String.valueOf(cd.getObservacao())));
					cell6.setBorderColor(BaseColor.BLACK);
					cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell6.setPaddingTop(5f);
					cell6.setPaddingBottom(5f);
					tableParcelas.addCell(cell6);					
				}


				if (this.objetoCalculo.getListCalculoDetalhes().size() > 0) {
					cell1 = new PdfPCell(new Phrase(""));
					cell1.setBorderColor(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setPaddingTop(5f);
					cell1.setPaddingBottom(5f);
					tableParcelas.addCell(cell1);

					cell2 = new PdfPCell(new Phrase(""));
					cell2.setBorderColor(BaseColor.BLACK);
					cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell2.setPaddingTop(5f);
					cell2.setPaddingBottom(5f);
					tableParcelas.addCell(cell2);
					
					cell7 = new PdfPCell(new Phrase("TOTAL", titulo));
					cell7.setBorderColor(BaseColor.BLACK);
					cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell7.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell7.setUseBorderPadding(true);
					cell7.setGrayFill(0.9f);
					cell7.setPaddingTop(10f);
					cell7.setPaddingBottom(10f);
					tableParcelas.addCell(cell7);

					if (totalValorParcela.toString().equals("0.00")) {
						cell8 = new PdfPCell(new Phrase("R$ 0,00", titulo));
					} else {
						cell8 = new PdfPCell(new Phrase("R$ " + df.format(totalValorParcela), titulo));
					}
					cell8.setBorderColor(BaseColor.BLACK);
					cell8.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell8.setGrayFill(0.9f);
					cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell8.setPaddingTop(5f);
					cell8.setPaddingBottom(5f);
					tableParcelas.addCell(cell8); 

					if (totalValorTotal.toString().equals("0.00")) {
						cell9 = new PdfPCell(new Phrase("R$ 0,00", titulo));
					} else {
						cell9 = new PdfPCell(new Phrase("R$ " + df.format(totalValorTotal), titulo));
					}
					cell9.setBorderColor(BaseColor.BLACK);
					cell9.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell9.setGrayFill(0.9f);
					cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell9.setPaddingTop(5f);
					cell9.setPaddingBottom(5f);
					tableParcelas.addCell(cell9); 
										
					cell3 = new PdfPCell(new Phrase(""));
					cell3.setBorderColor(BaseColor.BLACK);
					cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell3.setPaddingTop(5f);
					cell3.setPaddingBottom(5f);
					tableParcelas.addCell(cell3);
				}

				doc.add(tableParcelas);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Cálculo: Este contrato está aberto por algum outro programa, por favor, feche-o e tente novamente! (Identificação: "
							+ this.objetoCalculo.getIdentificacaoCalculo() + ")" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Cálculo: Ocorreu um problema ao gerar o PDF! (Identificação: "
							+ this.objetoCalculo.getIdentificacaoCalculo() + ")" + e, ""));
		} finally {
			this.calculoPDFGerado = true;

			if (doc != null) {
				//fechamento do documento
				doc.close();
			}
			if (os != null) {
				//fechamento da stream de saída
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}	

	/**
	 * @return the valorParcela
	 */
	public BigDecimal getValorParcela() {
		return valorParcela;
	}

	/**
	 * @param valorParcela the valorParcela to set
	 */
	public void setValorParcela(BigDecimal valorParcela) {
		this.valorParcela = valorParcela;
	}

	/**
	 * @return the txJuros
	 */
	public BigDecimal getTxJuros() {
		return txJuros;
	}

	/**
	 * @param txJuros the txJuros to set
	 */
	public void setTxJuros(BigDecimal txJuros) {
		this.txJuros = txJuros;
	}

	/**
	 * @return the multa
	 */
	public BigDecimal getMulta() {
		return multa;
	}

	/**
	 * @param multa the multa to set
	 */
	public void setMulta(BigDecimal multa) {
		this.multa = multa;
	}

	/**
	 * @return the valorAtualizado
	 */
	public BigDecimal getValorAtualizado() {
		return valorAtualizado;
	}

	/**
	 * @param valorAtualizado the valorAtualizado to set
	 */
	public void setValorAtualizado(BigDecimal valorAtualizado) {
		this.valorAtualizado = valorAtualizado;
	}

	/**
	 * @return the selectedRecebedor
	 */
	public PagadorRecebedor getSelectedRecebedor() {
		return selectedRecebedor;
	}

	/**
	 * @param selectedRecebedor the selectedRecebedor to set
	 */
	public void setSelectedRecebedor(PagadorRecebedor selectedRecebedor) {
		this.selectedRecebedor = selectedRecebedor;
	}

	/**
	 * @return the listRecebedores
	 */
	public List<PagadorRecebedor> getListRecebedores() {
		return listRecebedores;
	}

	/**
	 * @param listRecebedores the listRecebedores to set
	 */
	public void setListRecebedores(List<PagadorRecebedor> listRecebedores) {
		this.listRecebedores = listRecebedores;
	}

	/**
	 * @return the nomeRecebedor
	 */
	public String getNomeRecebedor() {
		return nomeRecebedor;
	}

	/**
	 * @param nomeRecebedor the nomeRecebedor to set
	 */
	public void setNomeRecebedor(String nomeRecebedor) {
		this.nomeRecebedor = nomeRecebedor;
	}

	/**
	 * @return the idRecebedor
	 */
	public long getIdRecebedor() {
		return idRecebedor;
	}

	/**
	 * @param idRecebedor the idRecebedor to set
	 */
	public void setIdRecebedor(long idRecebedor) {
		this.idRecebedor = idRecebedor;
	}

	/**
	 * @return the contratoGerado
	 */
	public boolean isContratoGerado() {
		return contratoGerado;
	}

	/**
	 * @return the selectedCalculo
	 */
	public Calculos getSelectedCalculo() {
		return selectedCalculo;
	}
	/**
	 * @param selectedCalculo the selectedCalculo to set
	 */
	public void setSelectedCalculo(Calculos selectedCalculo) {
		this.selectedCalculo = selectedCalculo;
	}
	/**
	 * @return the listCalculos
	 */
	public List<Calculos> getListCalculos() {
		return listCalculos;
	}
	/**
	 * @param listCalculos the listCalculos to set
	 */
	public void setListCalculos(List<Calculos> listCalculos) {
		this.listCalculos = listCalculos;
	}
	/**
	 * @return the objetoCalculo
	 */
	public Calculos getObjetoCalculo() {
		return objetoCalculo;
	}
	/**
	 * @param objetoCalculo the objetoCalculo to set
	 */
	public void setObjetoCalculo(Calculos objetoCalculo) {
		this.objetoCalculo = objetoCalculo;
	}
	/**
	 * @return the listTemp
	 */
	public List<CalculosDetalhes> getListTemp() {
		return listTemp;
	}
	/**
	 * @param listTemp the listTemp to set
	 */
	public void setListTemp(List<CalculosDetalhes> listTemp) {
		this.listTemp = listTemp;
	}
	/**
	 * @return the calculosDetalhes
	 */
	public CalculosDetalhes getCalculosDetalhes() {
		return calculosDetalhes;
	}
	/**
	 * @param calculosDetalhes the calculosDetalhes to set
	 */
	public void setCalculosDetalhes(CalculosDetalhes calculosDetalhes) {
		this.calculosDetalhes = calculosDetalhes;
	}
	/**
	 * @return the dataVencimento
	 */
	public Date getDataVencimento() {
		return dataVencimento;
	}
	/**
	 * @param dataVencimento the dataVencimento to set
	 */
	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}
	/**
	 * @return the dataPagamento
	 */
	public Date getDataPagamento() {
		return dataPagamento;
	}
	/**
	 * @param dataPagamento the dataPagamento to set
	 */
	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}
	/**
	 * @return the vlrParcela
	 */
	public BigDecimal getVlrParcela() {
		return vlrParcela;
	}
	/**
	 * @param vlrParcela the vlrParcela to set
	 */
	public void setVlrParcela(BigDecimal vlrParcela) {
		this.vlrParcela = vlrParcela;
	}
	/**
	 * @return the txJurosPacela
	 */
	public BigDecimal getTxJurosPacela() {
		return txJurosPacela;
	}
	/**
	 * @param txJurosPacela the txJurosPacela to set
	 */
	public void setTxJurosPacela(BigDecimal txJurosPacela) {
		this.txJurosPacela = txJurosPacela;
	}
	/**
	 * @return the multaParcela
	 */
	public BigDecimal getMultaParcela() {
		return multaParcela;
	}
	/**
	 * @param multaParcela the multaParcela to set
	 */
	public void setMultaParcela(BigDecimal multaParcela) {
		this.multaParcela = multaParcela;
	}
	/**
	 * @return the totalParcela
	 */
	public BigDecimal getTotalParcela() {
		return totalParcela;
	}
	/**
	 * @param totalParcela the totalParcela to set
	 */
	public void setTotalParcela(BigDecimal totalParcela) {
		this.totalParcela = totalParcela;
	}
	/**
	 * @return the updateMode
	 */
	public boolean isUpdateMode() {
		return updateMode;
	}
	/**
	 * @param updateMode the updateMode to set
	 */
	public void setUpdateMode(boolean updateMode) {
		this.updateMode = updateMode;
	}
	/**
	 * @return the deleteMode
	 */
	public boolean isDeleteMode() {
		return deleteMode;
	}
	/**
	 * @param deleteMode the deleteMode to set
	 */
	public void setDeleteMode(boolean deleteMode) {
		this.deleteMode = deleteMode;
	}
	public boolean isUpdateParcelaMode() {
		return updateParcelaMode;
	}
	public void setUpdateParcelaMode(boolean updateParcelaMode) {
		this.updateParcelaMode = updateParcelaMode;
	}
	public int getNumeroParcela() {
		return numeroParcela;
	}
	public void setNumeroParcela(int numeroParcela) {
		this.numeroParcela = numeroParcela;
	}
	/**
	 * @return the honorarios
	 */
	public BigDecimal getHonorarios() {
		return honorarios;
	}
	/**
	 * @param honorarios the honorarios to set
	 */
	public void setHonorarios(BigDecimal honorarios) {
		this.honorarios = honorarios;
	}
	public boolean isCalculoPDFGerado() {
		return calculoPDFGerado;
	}
	public void setCalculoPDFGerado(boolean calculoPDFGerado) {
		this.calculoPDFGerado = calculoPDFGerado;
	}
	public String getPathPDF() {
		return pathPDF;
	}
	public void setPathPDF(String pathPDF) {
		this.pathPDF = pathPDF;
	}
	public String getNomePDF() {
		return nomePDF;
	}
	public void setNomePDF(String nomePDF) {
		this.nomePDF = nomePDF;
	}
	/**
	 * @return the file
	 */
	public StreamedContent getFile() {
		String caminho =  this.pathPDF + this.nomePDF;        
		String arquivo = this.nomePDF;
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(caminho);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
		file = new DefaultStreamedContent(stream, caminho, arquivo); 

		return file;  
	}
	public void setFile(StreamedContent file) {
		this.file = file;
	}
	/**
	 * @return the observacaoParcela
	 */
	public String getObservacaoParcela() {
		return observacaoParcela;
	}
	/**
	 * @param observacaoParcela the observacaoParcela to set
	 */
	public void setObservacaoParcela(String observacaoParcela) {
		this.observacaoParcela = observacaoParcela;
	}
	/**
	 * @return the sumTxJurosPacela
	 */
	public BigDecimal getSumTxJurosPacela() {
		return sumTxJurosPacela;
	}
	/**
	 * @param sumTxJurosPacela the sumTxJurosPacela to set
	 */
	public void setSumTxJurosPacela(BigDecimal sumTxJurosPacela) {
		this.sumTxJurosPacela = sumTxJurosPacela;
	}
	/**
	 * @return the sumMultaParcela
	 */
	public BigDecimal getSumMultaParcela() {
		return sumMultaParcela;
	}
	/**
	 * @param sumMultaParcela the sumMultaParcela to set
	 */
	public void setSumMultaParcela(BigDecimal sumMultaParcela) {
		this.sumMultaParcela = sumMultaParcela;
	}
	/**
	 * @return the sumVlrParcela
	 */
	public BigDecimal getSumVlrParcela() {
		return sumVlrParcela;
	}
	/**
	 * @param sumVlrParcela the sumVlrParcela to set
	 */
	public void setSumVlrParcela(BigDecimal sumVlrParcela) {
		this.sumVlrParcela = sumVlrParcela;
	}
	/**
	 * @return the sumHonorariosParcela
	 */
	public BigDecimal getSumHonorariosParcela() {
		return sumHonorariosParcela;
	}
	/**
	 * @param sumHonorariosParcela the sumHonorariosParcela to set
	 */
	public void setSumHonorariosParcela(BigDecimal sumHonorariosParcela) {
		this.sumHonorariosParcela = sumHonorariosParcela;
	}
	/**
	 * @return the sumTotalParcelas
	 */
	public BigDecimal getSumTotalParcelas() {
		return sumTotalParcelas;
	}
	/**
	 * @param sumTotalParcelas the sumTotalParcelas to set
	 */
	public void setSumTotalParcelas(BigDecimal sumTotalParcelas) {
		this.sumTotalParcelas = sumTotalParcelas;
	}
	/**
	 * @return the sumTxJurosPacelaStr
	 */
	public String getSumTxJurosPacelaStr() {
		return sumTxJurosPacelaStr;
	}
	/**
	 * @param sumTxJurosPacelaStr the sumTxJurosPacelaStr to set
	 */
	public void setSumTxJurosPacelaStr(String sumTxJurosPacelaStr) {
		this.sumTxJurosPacelaStr = sumTxJurosPacelaStr;
	}
	/**
	 * @return the sumMultaParcelaStr
	 */
	public String getSumMultaParcelaStr() {
		return sumMultaParcelaStr;
	}
	/**
	 * @param sumMultaParcelaStr the sumMultaParcelaStr to set
	 */
	public void setSumMultaParcelaStr(String sumMultaParcelaStr) {
		this.sumMultaParcelaStr = sumMultaParcelaStr;
	}
	/**
	 * @return the sumVlrParcelaStr
	 */
	public String getSumVlrParcelaStr() {
		return sumVlrParcelaStr;
	}
	/**
	 * @param sumVlrParcelaStr the sumVlrParcelaStr to set
	 */
	public void setSumVlrParcelaStr(String sumVlrParcelaStr) {
		this.sumVlrParcelaStr = sumVlrParcelaStr;
	}
	/**
	 * @return the sumHonorariosParcelaStr
	 */
	public String getSumHonorariosParcelaStr() {
		return sumHonorariosParcelaStr;
	}
	/**
	 * @param sumHonorariosParcelaStr the sumHonorariosParcelaStr to set
	 */
	public void setSumHonorariosParcelaStr(String sumHonorariosParcelaStr) {
		this.sumHonorariosParcelaStr = sumHonorariosParcelaStr;
	}
	/**
	 * @return the sumTotalParcelasStr
	 */
	public String getSumTotalParcelasStr() {
		return sumTotalParcelasStr;
	}
	/**
	 * @param sumTotalParcelasStr the sumTotalParcelasStr to set
	 */
	public void setSumTotalParcelasStr(String sumTotalParcelasStr) {
		this.sumTotalParcelasStr = sumTotalParcelasStr;
	}
	/**
	 * @return the dataAtualizacaoBkp
	 */
	public Date getDataAtualizacaoBkp() {
		return dataAtualizacaoBkp;
	}
	/**
	 * @param dataAtualizacaoBkp the dataAtualizacaoBkp to set
	 */
	public void setDataAtualizacaoBkp(Date dataAtualizacaoBkp) {
		this.dataAtualizacaoBkp = dataAtualizacaoBkp;
	}	
}
