package com.webnowbr.siscoat.cobranca.mb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
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
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaBRLLiquidacao;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.simulador.SimulacaoDetalheVO;
import com.webnowbr.siscoat.simulador.SimulacaoVO;


@ManagedBean(name = "brlTrustMB")
@SessionScoped
public class BRLTrustMB {
	
	private boolean jsonGerado;
	private String pathJSON;
	private String nomeJSON;	
	private StreamedContent file;	
	
	private String pathXLS;
	private String nomeXLS;
	private boolean xlsGerado;
	private StreamedContent xlsFile;	
	
	List<ContratoCobranca> contratos = new ArrayList<ContratoCobranca>();
	private List<ContratoCobranca> selectedContratos = new ArrayList<ContratoCobranca>();
	private List<ContratoCobranca> selectedContratosXLS = new ArrayList<ContratoCobranca>();
	private List<ContratoCobrancaBRLLiquidacao> selectedJsonLiquidacao = new ArrayList<ContratoCobrancaBRLLiquidacao>();
	ContratoCobranca objetoContratoCobranca = new ContratoCobranca();
	
	private String numContrato;
	private String cedenteCessao;
	private Date dataAquisicao;
	private Date dataBaixaInicial;
	private Date dataBaixaFinal;
	
	private boolean usaTaxaJurosDiferenciada;
	private BigDecimal txJurosCessao;
	
	private BigDecimal valorTotalFaceCessao;
	private BigDecimal valorTotalAquisicaoCessao;
	
	private BigDecimal somatoriaValorePresenteContratos;
	
	private BigDecimal taxaDesconto;
	
	private Date dataValorPresente;
	
	private BigDecimal valorTotalRecebidoLiquidacao;
	private BigDecimal valorTotalJurosAmortizacaoLiquidacao;
	private int qtdSelecionadoLiquidacao;
	
	private BigDecimal valorTotalLiquidacao;
	private int qtdeLiquidados;
	
	List<ContratoCobrancaBRLLiquidacao> parcelasLiquidacao = new ArrayList<ContratoCobrancaBRLLiquidacao>();
	ContratoCobrancaBRLLiquidacao parcelaLiquidacao = new ContratoCobrancaBRLLiquidacao();
	
	public UploadedFile uploadedFile;

	public String clearFieldsBRLJson() {			
		this.numContrato = "";
		this.cedenteCessao = "";		
		this.contratos = new ArrayList<ContratoCobranca>();
		this.dataAquisicao = new Date();
		
		this.usaTaxaJurosDiferenciada = false;
		this.txJurosCessao = BigDecimal.ZERO;
		
		this.jsonGerado = false;
		
		this.valorTotalFaceCessao = BigDecimal.ZERO;
		this.valorTotalAquisicaoCessao = BigDecimal.ZERO;
		
		return "/Atendimento/Cobranca/ContratoCobrancaConsultarBRLJson.xhtml";
	}
	
	public String clearFieldsBRLJsonSemIPCA() {			
		this.numContrato = "";
		this.cedenteCessao = "";		
		this.contratos = new ArrayList<ContratoCobranca>();
		this.dataAquisicao = new Date();
		
		this.usaTaxaJurosDiferenciada = false;
		this.txJurosCessao = BigDecimal.ZERO;
		
		this.jsonGerado = false;
		
		this.valorTotalFaceCessao = BigDecimal.ZERO;
		this.valorTotalAquisicaoCessao = BigDecimal.ZERO;
		
		return "/Manutencao/ContratoCobrancaConsultarBRLJsonSemIPCA.xhtml";
	}
	
	public void pesquisaContratosJSONCessao() {
		// cedenteBRLCessao;
		// dataAquisicaoCessao;
		
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		
		this.contratos = new ArrayList<ContratoCobranca>();
		this.contratos = cDao.consultaContratosJSONCessao();
	}
	
	public void pesquisaContratosCessao() {
		FacesContext context = FacesContext.getCurrentInstance();
		
		if (!this.cedenteCessao.equals("")) {
			if (this.numContrato.length() == 4) {
				this.numContrato = "0" + this.numContrato;
			} 
			
			ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
			this.contratos = contratoCobrancaDao.consultaContratosBRLCessao(this.numContrato);
			
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"BRL JSON: Pesquisa efetuada com sucesso!",
							""));	
		} else {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"BRL JSON: Informe o cedente para a consulta dos contratos!",
							""));
		}
	}
	
	public String clearFieldsBRLJsonLiquidacao() {			
		this.numContrato = "";
		this.cedenteCessao = "";		
		this.contratos = new ArrayList<ContratoCobranca>();
		this.dataAquisicao = new Date();
		this.selectedJsonLiquidacao = new ArrayList<ContratoCobrancaBRLLiquidacao>();
		
		this.dataBaixaInicial = gerarDataOntem();
		this.dataBaixaFinal = gerarDataOntem();
		
		this.parcelasLiquidacao = new ArrayList<ContratoCobrancaBRLLiquidacao>();
		this.parcelaLiquidacao = new ContratoCobrancaBRLLiquidacao();
		
		this.jsonGerado = false;
		
		valorTotalRecebidoLiquidacao = BigDecimal.ZERO;
		valorTotalJurosAmortizacaoLiquidacao = BigDecimal.ZERO;
		qtdSelecionadoLiquidacao = 0;
		
		return "/Atendimento/Cobranca/ContratoCobrancaConsultarBRLJsonLiquidacao.xhtml";
	}
	
	public String clearFieldsBRLJsonLiquidacaoMigracao() {			
		this.numContrato = "";
		this.cedenteCessao = "";		
		this.contratos = new ArrayList<ContratoCobranca>();
		this.selectedContratos = new ArrayList<ContratoCobranca>();
		
		this.dataAquisicao = new Date();
		
		this.dataValorPresente = DateUtil.gerarDataHoje();
		
		this.dataBaixaInicial = gerarDataOntem();
		this.dataBaixaFinal = gerarDataOntem();
		
		this.parcelasLiquidacao = new ArrayList<ContratoCobrancaBRLLiquidacao>();
		this.parcelaLiquidacao = new ContratoCobrancaBRLLiquidacao();
		
		this.somatoriaValorePresenteContratos = BigDecimal.ZERO;
		
		this.jsonGerado = false;
		
		return "/Atendimento/Cobranca/ContratoCobrancaConsultarBRLJsonMigracao.xhtml";
	}
	
    public void rowSelected() {
    	this.somatoriaValorePresenteContratos = calculaValorPresenteTotalContrato();
    }
 
    public void rowUnSelected() {
    	this.somatoriaValorePresenteContratos = calculaValorPresenteTotalContrato();
    }
    
    private BigDecimal calculaValorPresenteTotalContrato() {
    	BigDecimal total = BigDecimal.ZERO;
    	
    	for (ContratoCobranca contrato : this.selectedContratos) {
    		total = total.add(contrato.getSomatoriaValorPresente());
    	}
    	
    	return total;
    }
    
	public void calculaValorTotalLiquidacao() {
    	BigDecimal totalRecebido = BigDecimal.ZERO;
    	BigDecimal totalJurosAmortizacao = BigDecimal.ZERO;
    	for (ContratoCobrancaBRLLiquidacao contrato : selectedJsonLiquidacao) {
    		totalRecebido = totalRecebido.add(contrato.getVlrRecebido());
    		totalJurosAmortizacao = totalJurosAmortizacao.add(contrato.getVlrJurosParcela());
    		totalJurosAmortizacao = totalJurosAmortizacao.add(contrato.getVlrAmortizacaoParcela());
    	}
    	valorTotalRecebidoLiquidacao = totalRecebido;
    	valorTotalJurosAmortizacaoLiquidacao = totalJurosAmortizacao;
    	qtdSelecionadoLiquidacao = selectedJsonLiquidacao.size();
    }
    
	public void pesquisaContratosLiquidacao() {
		FacesContext context = FacesContext.getCurrentInstance();
		
		if (this.numContrato.length() == 4) {
			this.numContrato = "0" + this.numContrato;
		} 
		
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.parcelasLiquidacao = contratoCobrancaDao.consultaContratosBRLLiquidacao(this.dataBaixaInicial, this.dataBaixaFinal, this.cedenteCessao);
		
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"BRL JSON: Pesquisa efetuada com sucesso!",
						""));	
	}
	
	public boolean contratoQuitado(ContratoCobranca contrato) {
		boolean contratoQuitado = true;
		
		for (ContratoCobrancaDetalhes parcela : contrato.getListContratoCobrancaDetalhes()) {
			if (!parcela.isParcelaPaga()) {
				contratoQuitado = false;
				break;
			}
		}
		
		return contratoQuitado;
	}
	
	public void pesquisaContratosLiquidacaoMigracao() {
		FacesContext context = FacesContext.getCurrentInstance();
		
		if (this.numContrato.length() == 4) {
			this.numContrato = "0" + this.numContrato;
		} 
		
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		List<ContratoCobranca> contratosBD = new ArrayList<ContratoCobranca>();
		this.contratos = new ArrayList<ContratoCobranca>();
		
		contratosBD = contratoCobrancaDao.consultaContratosBRLLiquidacaoMigracao(this.cedenteCessao);
		
		BigDecimal somatoriaValorPresente = BigDecimal.ZERO;

		for (ContratoCobranca contrato : contratosBD) {
			somatoriaValorPresente = BigDecimal.ZERO;
			boolean contratoQuitado = contratoQuitado(contrato);
			
			if (!contratoQuitado) {
				int parcelasVencidas = consideraContratoJSONMigracao(contrato);
				int parcelasAVencer = 0;
				
				if (parcelasVencidas <= 1) {
					for (ContratoCobrancaDetalhes parcela : contrato.getListContratoCobrancaDetalhes()) {
							if (parcela.getDataVencimento().after(this.dataValorPresente) && !parcela.isParcelaPaga()) {
								somatoriaValorPresente = somatoriaValorPresente.add(calcularValorPresenteParcelaComIPCAGambiarra(parcela.getId(), contrato.getTxJurosParcelas(), this.dataValorPresente));
								parcelasAVencer = parcelasAVencer +1 ;
							}
					}
					
					contrato.setParcelasVencidas(parcelasVencidas);
					
					contrato.setSomatoriaValorPresente(somatoriaValorPresente);
					contrato.setParcelasAVencer(parcelasAVencer);
					
					this.contratos.add(contrato);
				}
			}
		}

		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"BRL JSON: Pesquisa efetuada com sucesso!",
						""));	
	}
	
	public void processaRelatorioFIDCMigracao() {
		this.xlsGerado = false;

		this.selectedContratosXLS = this.selectedContratos;
		
		// GERA XLS
		if (this.selectedContratos.size() > 0) {
			try {
				geraXLSFinanceiroDia();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void geraXLSFinanceiroDia() throws IOException {
		ParametrosDao pDao = new ParametrosDao();
		this.pathXLS = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomeXLS = "Relatório Financeiro Migração FIDC.xlsx";

		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		dataHoje.set(Calendar.HOUR_OF_DAY, 0);
		dataHoje.set(Calendar.MINUTE, 0);
		dataHoje.set(Calendar.SECOND, 0);
		dataHoje.set(Calendar.MILLISECOND, 0);

		// dataHoje.add(Calendar.DAY_OF_MONTH, 1);

		String excelFileName = this.pathXLS + this.nomeXLS;// name of excel file

		String sheetName = "Resultado";// name of sheet

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

		// Style para cabeçalho
		XSSFCellStyle cell_style_pago_String = wb.createCellStyle();
		cell_style_pago_String.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		cell_style_pago_String.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cell_style_pago_String.setFont(font);
		cell_style_pago_String.setAlignment(HorizontalAlignment.CENTER);
		cell_style_pago_String.setVerticalAlignment(VerticalAlignment.CENTER);
		cell_style_pago_String.setBorderBottom(BorderStyle.THIN);
		cell_style_pago_String.setBorderTop(BorderStyle.THIN);
		cell_style_pago_String.setBorderRight(BorderStyle.THIN);
		cell_style_pago_String.setBorderLeft(BorderStyle.THIN);
		cell_style_pago_String.setWrapText(true);

		// Style para cabeçalho
		XSSFCellStyle cell_style_pago_Date = wb.createCellStyle();
		cell_style_pago_Date.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		cell_style_pago_Date.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cell_style_pago_Date.setFont(font);
		cell_style_pago_Date.setAlignment(HorizontalAlignment.CENTER);
		cell_style_pago_Date.setVerticalAlignment(VerticalAlignment.CENTER);
		cell_style_pago_Date.setBorderBottom(BorderStyle.THIN);
		cell_style_pago_Date.setBorderTop(BorderStyle.THIN);
		cell_style_pago_Date.setBorderRight(BorderStyle.THIN);
		cell_style_pago_Date.setBorderLeft(BorderStyle.THIN);
		cell_style_pago_Date.setWrapText(true);
		cell_style_pago_Date.setDataFormat((short) BuiltinFormats.getBuiltinFormat("m/d/yy"));

		// Style para cabeçalho
		XSSFCellStyle cell_style_pago_Number = wb.createCellStyle();
		cell_style_pago_Number.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		cell_style_pago_Number.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cell_style_pago_Number.setFont(font);
		cell_style_pago_Number.setAlignment(HorizontalAlignment.CENTER);
		cell_style_pago_Number.setVerticalAlignment(VerticalAlignment.CENTER);
		cell_style_pago_Number.setBorderBottom(BorderStyle.THIN);
		cell_style_pago_Number.setBorderTop(BorderStyle.THIN);
		cell_style_pago_Number.setBorderRight(BorderStyle.THIN);
		cell_style_pago_Number.setBorderLeft(BorderStyle.THIN);
		cell_style_pago_Number.setWrapText(true);
		CreationHelper chNumber = wb.getCreationHelper();
		cell_style_pago_Number.setDataFormat(
				chNumber.createDataFormat().getFormat("_(R$* #,##0.00_);_(R$* (#,##0.00);_(R$* \"-\"??_);_(@_)"));

		// Style para cabeçalho
		XSSFCellStyle cell_style_vencida_String = wb.createCellStyle();
		cell_style_vencida_String.setFillForegroundColor(IndexedColors.RED.getIndex());
		cell_style_vencida_String.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cell_style_vencida_String.setFont(font);
		cell_style_vencida_String.setAlignment(HorizontalAlignment.CENTER);
		cell_style_vencida_String.setVerticalAlignment(VerticalAlignment.CENTER);
		cell_style_vencida_String.setBorderBottom(BorderStyle.THIN);
		cell_style_vencida_String.setBorderTop(BorderStyle.THIN);
		cell_style_vencida_String.setBorderRight(BorderStyle.THIN);
		cell_style_vencida_String.setBorderLeft(BorderStyle.THIN);
		cell_style_vencida_String.setWrapText(true);

		// Style para cabeçalho
		XSSFCellStyle cell_style_vencida_Date = wb.createCellStyle();
		cell_style_vencida_Date.setFillForegroundColor(IndexedColors.RED.getIndex());
		cell_style_vencida_Date.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cell_style_vencida_Date.setFont(font);
		cell_style_vencida_Date.setAlignment(HorizontalAlignment.CENTER);
		cell_style_vencida_Date.setVerticalAlignment(VerticalAlignment.CENTER);
		cell_style_vencida_Date.setBorderBottom(BorderStyle.THIN);
		cell_style_vencida_Date.setBorderTop(BorderStyle.THIN);
		cell_style_vencida_Date.setBorderRight(BorderStyle.THIN);
		cell_style_vencida_Date.setBorderLeft(BorderStyle.THIN);
		cell_style_vencida_Date.setWrapText(true);
		cell_style_vencida_Date.setDataFormat((short) BuiltinFormats.getBuiltinFormat("m/d/yy"));

		// Style para cabeçalho
		XSSFCellStyle cell_style_vencida_Number = wb.createCellStyle();
		cell_style_vencida_Number.setFillForegroundColor(IndexedColors.RED.getIndex());
		cell_style_vencida_Number.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cell_style_vencida_Number.setFont(font);
		cell_style_vencida_Number.setAlignment(HorizontalAlignment.CENTER);
		cell_style_vencida_Number.setVerticalAlignment(VerticalAlignment.CENTER);
		cell_style_vencida_Number.setBorderBottom(BorderStyle.THIN);
		cell_style_vencida_Number.setBorderTop(BorderStyle.THIN);
		cell_style_vencida_Number.setBorderRight(BorderStyle.THIN);
		cell_style_vencida_Number.setBorderLeft(BorderStyle.THIN);
		cell_style_vencida_Number.setWrapText(true);
		chNumber = wb.getCreationHelper();
		cell_style_vencida_Number.setDataFormat(
				chNumber.createDataFormat().getFormat("_(R$* #,##0.00_);_(R$* (#,##0.00);_(R$* \"-\"??_);_(@_)"));

		// iterating r number of rows
		// cria CABEÇALHO
		int countLine = 0;
		XSSFRow row = sheet.createRow(countLine);
		XSSFCell cell;
		cell = row.createCell(0);
		cell.setCellValue("Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("Data Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(2);
		cell.setCellValue("Pagador");
		cell.setCellStyle(cell_style);
		cell = row.createCell(3);
		cell.setCellValue("CPF/CNPJ");
		cell.setCellStyle(cell_style);
		cell = row.createCell(4);
		cell.setCellValue("Dt. Nascimento");
		cell.setCellStyle(cell_style);
		cell = row.createCell(5);
		cell.setCellValue("Endereço");
		cell.setCellStyle(cell_style);
		cell = row.createCell(6);
		cell.setCellValue("Nome Cônjuge");
		cell.setCellStyle(cell_style);
		cell = row.createCell(7);
		cell.setCellValue("CPF Cônjuge");
		cell.setCellStyle(cell_style);
		cell = row.createCell(8);
		cell.setCellValue("Região Imóvel");
		cell.setCellStyle(cell_style);
		cell = row.createCell(9);
		cell.setCellValue("Tipo Imóvel");
		cell.setCellStyle(cell_style);
		cell = row.createCell(10);
		cell.setCellValue("Valor Imóvel");
		cell.setCellStyle(cell_style);
		cell = row.createCell(11);
		cell.setCellValue("Valor CCB");
		cell.setCellStyle(cell_style);
		cell = row.createCell(12);
		cell.setCellValue("Taxa de Juros (%)");
		cell.setCellStyle(cell_style);
		cell = row.createCell(13);
		cell.setCellValue("Tipo de Juros");
		cell.setCellStyle(cell_style);
		cell = row.createCell(14);
		cell.setCellValue("CET");
		cell.setCellStyle(cell_style);
		cell = row.createCell(15);
		cell.setCellValue("Qtde. Parcelas Aberto");
		cell.setCellStyle(cell_style);
		cell = row.createCell(16);
		cell.setCellValue("Saldo Devedor");
		cell.setCellStyle(cell_style);
		cell = row.createCell(17);
		cell.setCellValue("IF (Termo Cessão)");
		cell.setCellStyle(cell_style);
		cell = row.createCell(18);
		cell.setCellValue("Matrícula");
		cell.setCellStyle(cell_style);
		cell = row.createCell(19);
		cell.setCellValue("Cartório");
		cell.setCellStyle(cell_style);
		cell = row.createCell(20);
		cell.setCellValue("Endereço");
		cell.setCellStyle(cell_style);
		cell = row.createCell(21);
		cell.setCellValue("Parcela");
		cell.setCellStyle(cell_style);
		cell = row.createCell(22);
		cell.setCellValue("Data Vencimento");
		cell.setCellStyle(cell_style);
		cell = row.createCell(23);
		cell.setCellValue("Amortização");
		cell.setCellStyle(cell_style);
		cell = row.createCell(24);
		cell.setCellValue("Juros");
		cell.setCellStyle(cell_style);
		cell = row.createCell(25);
		cell.setCellValue("Taxa Adm");
		cell.setCellStyle(cell_style);
		cell = row.createCell(26);
		cell.setCellValue("Valor");
		cell.setCellStyle(cell_style);
		cell = row.createCell(27);
		cell.setCellValue("Data Pagto.");
		cell.setCellStyle(cell_style);
		cell = row.createCell(28);
		cell.setCellValue("Valor Pago");
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

		// cria estilo especifico para coluna type numérico
		CellStyle numberStyle = wb.createCellStyle();
		numberStyle.setAlignment(HorizontalAlignment.CENTER);
		numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		numberStyle.setBorderBottom(BorderStyle.THIN);
		numberStyle.setBorderTop(BorderStyle.THIN);
		numberStyle.setBorderRight(BorderStyle.THIN);
		numberStyle.setBorderLeft(BorderStyle.THIN);
		numberStyle.setWrapText(true);

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

		int linhaInicioContrato = 0;
		
		for (ContratoCobranca record : this.selectedContratosXLS) {
			countLine++;
			linhaInicioContrato = countLine;
			row = sheet.createRow(countLine);

			// Contrato
			cell = row.createCell(0);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getNumeroContrato());

			// Data do Contrato
			cell = row.createCell(1);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getDataInicio());

			// Pagador
			cell = row.createCell(2);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getPagador().getNome());

			// CPF CNPJ
			cell = row.createCell(3);
			cell.setCellStyle(cell_style);
			if (record.getPagador().getCpf() != null && !record.getPagador().getCpf().equals("")) {
				cell.setCellValue(record.getPagador().getCpf());
			} else {
				cell.setCellValue(record.getPagador().getCnpj());
			}
			
			// Data NAscimento Pagador
			cell = row.createCell(4);
			cell.setCellStyle(dateStyle);
			if (record.getPagador().getDtNascimento() != null) {
				cell.setCellValue(record.getPagador().getDtNascimento());
			}
						
			// Endereço pagador
			cell = row.createCell(5);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getPagador().getEndereco() + ", " + record.getPagador().getNumero() + " - " + record.getPagador().getCidade() + " / " + record.getPagador().getEstado() + " (CEP: " + record.getPagador().getCep() + ")");
			
			// Nome Conjuge
			cell = row.createCell(6);
			cell.setCellStyle(cell_style);
			if (record.getPagador().getNomeConjuge() != null) {
				cell.setCellValue(record.getPagador().getNomeConjuge());
			}
			
			// CPF Conjuge
			cell = row.createCell(7);
			cell.setCellStyle(cell_style);
			if (record.getPagador().getCpfConjuge() != null) {
				cell.setCellValue(record.getPagador().getCpfConjuge());
			}
			
			// Região Imóvel
			cell = row.createCell(8);
			cell.setCellStyle(cell_style);					
			if (record.getImovel().getCidade() != null && record.getImovel().getEstado() != null) {
				cell.setCellValue(record.getImovel().getCidade() + "/" + record.getImovel().getEstado());
			}
			
			// Tipo Imovel
			cell = row.createCell(9);
			cell.setCellStyle(cell_style);
			if (record.getTipoImovel() != null) {
				cell.setCellValue(record.getTipoImovel());
			}
		
			//Valor Imovel
			cell = row.createCell(10);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getValorImovel() != null) {
				cell.setCellValue(((BigDecimal) record.getValorImovel()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0"));
			}

			// Valor CCB
			cell = row.createCell(11);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getValorCCB() != null) {
				cell.setCellValue(((BigDecimal) record.getValorCCB()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0"));
			}

			// Taxa Juros
			cell = row.createCell(12);
			cell.setCellStyle(numberStyle);
			if (record.getTxJurosParcelas() != null) {
				cell.setCellValue(((BigDecimal) record.getTxJurosParcelas()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0"));
			}
			
			// Tipo Juros
			cell = row.createCell(13);
			cell.setCellStyle(cell_style);
			if (record.isCorrigidoIPCA()) {
				cell.setCellValue("Pós-Fixado");
			} else {
				cell.setCellValue("Pré-Fixado");
			}

			// CET
			cell = row.createCell(14);
			cell.setCellStyle(numberStyle);
			if (!CommonsUtil.semValor(record.getCetMes())) {
				cell.setCellValue((record.getCetMes()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0"));
			}
			
			// qtde parcelas aberto			
			cell = row.createCell(15);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getParcelasAVencer()
					);
			
			// somatoria valor presente
			cell = row.createCell(16);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getSomatoriaValorPresente() != null) {
				cell.setCellValue(((BigDecimal) record.getSomatoriaValorPresente()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0"));
			}
			
			// IF (Termo Cessão)
			cell = row.createCell(17);
			cell.setCellStyle(cell_style);
			if (record.isTemSeguro()) {
				cell.setCellValue(record.getTermoCessao());
			}

			// Matricula
			cell = row.createCell(18);
			cell.setCellStyle(cell_style);
			if (record.getImovel() != null && !record.getImovel().getNumeroMatricula().equals("")) {
				cell.setCellValue(record.getImovel().getNumeroMatricula());
			}
			
			// cartorio
			cell = row.createCell(19);
			cell.setCellStyle(cell_style);
			if (record.getImovel() != null && !record.getImovel().getCartorio().equals("")) {
				cell.setCellValue(record.getImovel().getCartorio());
			}
			
			// endereço
			cell = row.createCell(20);
			cell.setCellStyle(cell_style);
			if (record.getImovel() != null && !record.getImovel().getEndereco().equals("") //&& !record.getImovel().getComplemento().equals("")
					 && !record.getImovel().getBairro().equals("") && !record.getImovel().getCidade().equals("") && !record.getImovel().getEstado().equals("")
					 && !record.getImovel().getCep().equals("")) {
				cell.setCellValue(CommonsUtil.stringValueVazio(record.getImovel().getEndereco()) + " - " //
						+ CommonsUtil.stringValueVazio(record.getImovel().getComplemento() //
								+ CommonsUtil.stringValueVazio(record.getImovel().getBairro()) + " - " + //
								CommonsUtil.stringValueVazio(record.getImovel().getCidade()) + "/" + //
								CommonsUtil.stringValueVazio(record.getImovel().getEstado()) + " - " + //
								CommonsUtil.stringValueVazio(record.getImovel().getCep())));
			}
			

			int parcelaCount = 0;
			for (ContratoCobrancaDetalhes parcelas : record.getListContratoCobrancaDetalhes()) {
				if (parcelas.getDataVencimento().after(this.dataValorPresente) && !parcelas.isParcelaPaga()) {
					if (parcelaCount > 0) {
						countLine++;
						row = sheet.createRow(countLine);
					}
					parcelaCount = parcelaCount + 1;
	
					// Contrato
					cell = row.createCell(0);
					cell.setCellStyle(cell_style);
					cell.setCellValue(record.getNumeroContrato());
	
					// Data do Contrato
					cell = row.createCell(1);
					cell.setCellStyle(dateStyle);
					cell.setCellValue(record.getDataInicio());
	
					// Pagador
					cell = row.createCell(2);
					cell.setCellStyle(cell_style);
					cell.setCellValue(record.getPagador().getNome());
	
					// CPF CNPJ
					cell = row.createCell(3);
					cell.setCellStyle(cell_style);
					if (record.getPagador().getCpf() != null && !record.getPagador().getCpf().equals("")) {
						cell.setCellValue(record.getPagador().getCpf());
					} else {
						cell.setCellValue(record.getPagador().getCnpj());
					}
					
					// Data NAscimento Pagador
					cell = row.createCell(4);
					cell.setCellStyle(dateStyle);
					if (record.getPagador().getDtNascimento() != null) {
						cell.setCellValue(record.getPagador().getDtNascimento());
					}
								
					// Endereço pagador
					cell = row.createCell(5);
					cell.setCellStyle(cell_style);
					cell.setCellValue(record.getPagador().getEndereco() + ", " + record.getPagador().getNumero() + " - " + record.getPagador().getCidade() + " / " + record.getPagador().getEstado() + " (CEP: " + record.getPagador().getCep() + ")");
					
					// Nome Conjuge
					cell = row.createCell(6);
					cell.setCellStyle(cell_style);
					if (record.getPagador().getNomeConjuge() != null) {
						cell.setCellValue(record.getPagador().getNomeConjuge());
					}
					
					// CPF Conjuge
					cell = row.createCell(7);
					cell.setCellStyle(cell_style);
					if (record.getPagador().getCpfConjuge() != null) {
						cell.setCellValue(record.getPagador().getCpfConjuge());
					}
					
					// Região Imóvel
					cell = row.createCell(8);
					cell.setCellStyle(cell_style);					
					if (record.getImovel().getCidade() != null && record.getImovel().getEstado() != null) {
						cell.setCellValue(record.getImovel().getCidade() + "/" + record.getImovel().getEstado());
					}
					
					// Tipo Imovel
					cell = row.createCell(9);
					cell.setCellStyle(cell_style);
					if (record.getTipoImovel() != null) {
						cell.setCellValue(record.getTipoImovel());
					}
					
					//Valor Imovel
					cell = row.createCell(10);
					cell.setCellStyle(numericStyle);
					cell.setCellType(CellType.NUMERIC);
					if (record.getValorImovel() != null) {
						cell.setCellValue(((BigDecimal) record.getValorImovel()).doubleValue());
					} else {
						cell.setCellValue(Double.valueOf("0"));
					}
	
					// Valor CCB
					cell = row.createCell(11);
					cell.setCellStyle(numericStyle);
					cell.setCellType(CellType.NUMERIC);
					if (record.getValorCCB() != null) {
						cell.setCellValue(((BigDecimal) record.getValorCCB()).doubleValue());
					} else {
						cell.setCellValue(Double.valueOf("0"));
					}
	
					// Taxa Juros
					cell = row.createCell(12);
					cell.setCellStyle(numberStyle);
					if (record.getTxJurosParcelas() != null) {
						cell.setCellValue(((BigDecimal) record.getTxJurosParcelas()).doubleValue());
					} else {
						cell.setCellValue(Double.valueOf("0"));
					}
					
					// Tipo Juros
					cell = row.createCell(13);
					cell.setCellStyle(cell_style);
					if (record.isCorrigidoIPCA()) {
						cell.setCellValue("Pós-Fixado");
					} else {
						cell.setCellValue("Pré-Fixado");
					}
					
					// CET
					cell = row.createCell(14);
					cell.setCellStyle(numberStyle);
					if (!CommonsUtil.semValor(record.getCetMes())) {
						cell.setCellValue(((BigDecimal) record.getCetMes()).doubleValue());
					} else {
						cell.setCellValue(Double.valueOf("0"));
					}
	
					// Parcela
					cell = row.createCell(21);
					/*
					 * if (parcelas.isParcelaPaga()) { cell.setCellStyle(cell_style_pago_String); }
					 * else { if (parcelas.isParcelaVencida()) {
					 * cell.setCellStyle(cell_style_vencida_String); } else {
					 * cell.setCellStyle(cell_style); } }
					 */
					cell.setCellStyle(cell_style);
					cell.setCellValue(parcelas.getNumeroParcela());
	
					// Data Vencimento
					cell = row.createCell(22);
					/*
					 * if (parcelas.isParcelaPaga()) { cell.setCellStyle(cell_style_pago_Date); }
					 * else { if (parcelas.isParcelaVencida()) {
					 * cell.setCellStyle(cell_style_vencida_Date); } else {
					 * cell.setCellStyle(dateStyle); } }
					 */
					cell.setCellStyle(dateStyle);
					cell.setCellValue(parcelas.getDataVencimento());
	
					// Amortização
					cell = row.createCell(23);
					cell.setCellStyle(numericStyle);
					cell.setCellType(CellType.NUMERIC);
					if (parcelas.getVlrAmortizacaoParcela() != null) {
						cell.setCellValue(((BigDecimal) parcelas.getVlrAmortizacaoParcela()).doubleValue());					
					} else {
						cell.setCellValue(Double.valueOf("0"));
					}
					
					// Taxa Juros
					cell = row.createCell(24);
					cell.setCellStyle(numericStyle);
					cell.setCellType(CellType.NUMERIC);
					if (parcelas.getVlrJurosParcela() != null) {
						cell.setCellValue(((BigDecimal) parcelas.getVlrJurosParcela()).doubleValue());					
					} else {
						cell.setCellValue(Double.valueOf("0"));
					}
					
					// Taxa Adm
					cell = row.createCell(25);
					cell.setCellStyle(numericStyle);
					cell.setCellType(CellType.NUMERIC);
					if (parcelas.getTaxaAdm() != null) {
						cell.setCellValue(((BigDecimal) parcelas.getTaxaAdm()).doubleValue());					
					} else {
						cell.setCellValue(Double.valueOf("0"));
					}
					
					// Valor Parcela
					cell = row.createCell(26);
					/*
					 * if (parcelas.isParcelaPaga()) { cell.setCellStyle(cell_style_pago_Number); }
					 * else { if (parcelas.isParcelaVencida()) {
					 * cell.setCellStyle(cell_style_vencida_Number); } else {
					 * cell.setCellStyle(numericStyle); } }
					 */
					cell.setCellStyle(numericStyle);
					cell.setCellType(CellType.NUMERIC);
					if (parcelas.getVlrParcela() != null) {
						BigDecimal seguros = BigDecimal.ZERO;
						if (parcelas.getSeguroDFI() != null) {
							seguros = seguros.add(parcelas.getSeguroDFI());
						}
						if (parcelas.getSeguroMIP() != null) {
							seguros = seguros.add(parcelas.getSeguroMIP());
						}
						
						if (seguros.compareTo(BigDecimal.ZERO) == 1) {
							cell.setCellValue(((BigDecimal) parcelas.getVlrParcela().subtract(seguros)).doubleValue());
						} else {
							cell.setCellValue(((BigDecimal) parcelas.getVlrParcela()).doubleValue());
						}						
					} else {
						cell.setCellValue(Double.valueOf("0"));
					}
	
					// Data pagto
					cell = row.createCell(27);
					/*
					 * if (parcelas.isParcelaPaga()) { cell.setCellStyle(cell_style_pago_Date); }
					 * else { if (parcelas.isParcelaVencida()) {
					 * cell.setCellStyle(cell_style_vencida_Date); } else {
					 * cell.setCellStyle(dateStyle); } }
					 */
					cell.setCellStyle(dateStyle);
					cell.setCellValue(parcelas.getDataUltimoPagamento());
	
					// Valor Pago
					cell = row.createCell(28);
					/*
					 * if (parcelas.isParcelaPaga()) { cell.setCellStyle(cell_style_pago_Number); }
					 * else { if (parcelas.isParcelaVencida()) {
					 * cell.setCellStyle(cell_style_vencida_Number); } else {
					 * cell.setCellStyle(numericStyle); } }
					 */
					cell.setCellStyle(numericStyle);
					cell.setCellType(CellType.NUMERIC);
					if (parcelas.getValorTotalPagamento() != null) {
						cell.setCellValue(((BigDecimal) parcelas.getValorTotalPagamento()).doubleValue());
					} else {
						cell.setCellValue(Double.valueOf("0"));
					}
				}
			}

			if ((countLine - linhaInicioContrato) > 1) {
				sheet.addMergedRegion(new CellRangeAddress(linhaInicioContrato, countLine, 0, 0));
				sheet.addMergedRegion(new CellRangeAddress(linhaInicioContrato, countLine, 1, 1));
				sheet.addMergedRegion(new CellRangeAddress(linhaInicioContrato, countLine, 2, 2));
				sheet.addMergedRegion(new CellRangeAddress(linhaInicioContrato, countLine, 3, 3));
				sheet.addMergedRegion(new CellRangeAddress(linhaInicioContrato, countLine, 4, 4));
				sheet.addMergedRegion(new CellRangeAddress(linhaInicioContrato, countLine, 5, 5));
				sheet.addMergedRegion(new CellRangeAddress(linhaInicioContrato, countLine, 6, 6));
				sheet.addMergedRegion(new CellRangeAddress(linhaInicioContrato, countLine, 7, 7));
				sheet.addMergedRegion(new CellRangeAddress(linhaInicioContrato, countLine, 8, 8));
				sheet.addMergedRegion(new CellRangeAddress(linhaInicioContrato, countLine, 9, 9));
				sheet.addMergedRegion(new CellRangeAddress(linhaInicioContrato, countLine, 10, 10));
				sheet.addMergedRegion(new CellRangeAddress(linhaInicioContrato, countLine, 11, 11));
				sheet.addMergedRegion(new CellRangeAddress(linhaInicioContrato, countLine, 12, 12));
				sheet.addMergedRegion(new CellRangeAddress(linhaInicioContrato, countLine, 13, 13));
				sheet.addMergedRegion(new CellRangeAddress(linhaInicioContrato, countLine, 14, 14));
				sheet.addMergedRegion(new CellRangeAddress(linhaInicioContrato, countLine, 15, 15));
				sheet.addMergedRegion(new CellRangeAddress(linhaInicioContrato, countLine, 16, 16));
				sheet.addMergedRegion(new CellRangeAddress(linhaInicioContrato, countLine, 17, 17));
				sheet.addMergedRegion(new CellRangeAddress(linhaInicioContrato, countLine, 18, 18));
				sheet.addMergedRegion(new CellRangeAddress(linhaInicioContrato, countLine, 19, 19));
				sheet.addMergedRegion(new CellRangeAddress(linhaInicioContrato, countLine, 20, 20));
			}

			// pula 1 linha
			countLine++;
			linhaInicioContrato = countLine;
			row = sheet.createRow(countLine);
			cell = row.createCell(0);
			cell.setCellStyle(cell_style);
			cell = row.createCell(1);
			cell.setCellStyle(cell_style);
			cell = row.createCell(2);
			cell.setCellStyle(cell_style);
			cell = row.createCell(3);
			cell.setCellStyle(cell_style);
			cell = row.createCell(4);
			cell.setCellStyle(cell_style);
			cell = row.createCell(5);
			cell.setCellStyle(cell_style);
			cell = row.createCell(6);
			cell.setCellStyle(cell_style);
			cell = row.createCell(7);
			cell.setCellStyle(cell_style);
			cell = row.createCell(8);
			cell.setCellStyle(cell_style);
			cell = row.createCell(9);
			cell.setCellStyle(cell_style);
			cell = row.createCell(10);
			cell.setCellStyle(cell_style);
			cell = row.createCell(11);
			cell.setCellStyle(cell_style);
			cell = row.createCell(12);
			cell.setCellStyle(cell_style);
			cell = row.createCell(13);
			cell.setCellStyle(cell_style);
			cell = row.createCell(14);
			cell.setCellStyle(cell_style);
			cell = row.createCell(15);
			cell.setCellStyle(cell_style);
			cell = row.createCell(16);
			cell.setCellStyle(cell_style);
			cell = row.createCell(17);
			cell.setCellStyle(cell_style);
			cell = row.createCell(18);
			cell.setCellStyle(cell_style);
			cell = row.createCell(19);
			cell.setCellStyle(cell_style);
			cell = row.createCell(20);
			cell.setCellStyle(cell_style);
			cell = row.createCell(21);
			cell.setCellStyle(cell_style);
			cell = row.createCell(22);
			cell.setCellStyle(cell_style);
			cell = row.createCell(23);
			cell.setCellStyle(cell_style);
			cell = row.createCell(24);
			cell.setCellStyle(cell_style);
			cell = row.createCell(25);
			cell.setCellStyle(cell_style);
			cell = row.createCell(26);
			cell.setCellStyle(cell_style);
			cell = row.createCell(27);
			cell.setCellStyle(cell_style);
			cell = row.createCell(28);
			cell.setCellStyle(cell_style);
			
			// Style para cabeçalho
			XSSFCellStyle cell_style_pago = wb.createCellStyle();
			cell_style_pago = wb.createCellStyle();
			cell_style_pago.setAlignment(HorizontalAlignment.CENTER);
			cell_style_pago.setVerticalAlignment(VerticalAlignment.CENTER);
			cell_style_pago.setBorderBottom(BorderStyle.THIN);
			cell_style_pago.setBorderTop(BorderStyle.THIN);
			cell_style_pago.setBorderRight(BorderStyle.THIN);
			cell_style_pago.setBorderLeft(BorderStyle.THIN);
			cell_style_pago.setWrapText(true);
			cell_style_pago.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
			cell_style_pago.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			XSSFCellStyle cell_style_aberto = wb.createCellStyle();
			cell_style_aberto = wb.createCellStyle();
			cell_style_aberto.setAlignment(HorizontalAlignment.CENTER);
			cell_style_aberto.setVerticalAlignment(VerticalAlignment.CENTER);
			cell_style_aberto.setBorderBottom(BorderStyle.THIN);
			cell_style_aberto.setBorderTop(BorderStyle.THIN);
			cell_style_aberto.setBorderRight(BorderStyle.THIN);
			cell_style_aberto.setBorderLeft(BorderStyle.THIN);
			cell_style_aberto.setWrapText(true);
			cell_style_aberto.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
			cell_style_aberto.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			XSSFCellStyle cell_style_atraso = wb.createCellStyle();
			cell_style_atraso = wb.createCellStyle();
			cell_style_atraso.setAlignment(HorizontalAlignment.CENTER);
			cell_style_atraso.setVerticalAlignment(VerticalAlignment.CENTER);
			cell_style_atraso.setBorderBottom(BorderStyle.THIN);
			cell_style_atraso.setBorderTop(BorderStyle.THIN);
			cell_style_atraso.setBorderRight(BorderStyle.THIN);
			cell_style_atraso.setBorderLeft(BorderStyle.THIN);
			cell_style_atraso.setWrapText(true);
			cell_style_atraso.setFillForegroundColor(IndexedColors.RED.getIndex());
			cell_style_atraso.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			XSSFCellStyle cell_style_bx_parcial = wb.createCellStyle();
			cell_style_bx_parcial = wb.createCellStyle();
			cell_style_bx_parcial.setAlignment(HorizontalAlignment.CENTER);
			cell_style_bx_parcial.setVerticalAlignment(VerticalAlignment.CENTER);
			cell_style_bx_parcial.setBorderBottom(BorderStyle.THIN);
			cell_style_bx_parcial.setBorderTop(BorderStyle.THIN);
			cell_style_bx_parcial.setBorderRight(BorderStyle.THIN);
			cell_style_bx_parcial.setBorderLeft(BorderStyle.THIN);
			cell_style_bx_parcial.setWrapText(true);
			cell_style_bx_parcial.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
			cell_style_bx_parcial.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			/*
			 * if (record.isParcelaPaga()) { cell.setCellStyle(cell_style_pago);
			 * cell.setCellValue("Pago"); } else { ContratoCobrancaDetalhesDao ccdDao = new
			 * ContratoCobrancaDetalhesDao(); ContratoCobrancaDetalhes ccd =
			 * ccdDao.findById(record.getIdParcela());
			 * 
			 * Calendar dataParcela = Calendar.getInstance(zone, locale);
			 * dataParcela.setTime(ccd.getDataVencimentoAtual());
			 * dataHoje.set(Calendar.HOUR_OF_DAY, 0); dataHoje.set(Calendar.MINUTE, 0);
			 * dataHoje.set(Calendar.SECOND, 0); dataHoje.set(Calendar.MILLISECOND, 0);
			 * 
			 * if (dataParcela.before(dataHoje)) { cell.setCellStyle(cell_style_atraso);
			 * cell.setCellValue("Em atraso"); } else { if
			 * (ccd.getListContratoCobrancaDetalhesParcial().size() > 0) {
			 * cell.setCellStyle(cell_style_bx_parcial);
			 * cell.setCellValue("Baixado parcialmente"); } else {
			 * cell.setCellStyle(cell_style_aberto); cell.setCellValue("Em aberto"); } } }
			 */
		}

		downloadJson(wb);
		
		// Resize columns to fit data
		// TODO MIGRACAO POI
		/*
		 * int noOfColumns = sheet.getRow(0).getLastCellNum(); for (int i = 0; i <
		 * noOfColumns; i++) { sheet.autoSizeColumn(i); }
		 */
		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		// write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();
		
		this.xlsGerado = true;

	}
		
	public int consideraContratoJSONMigracao(ContratoCobranca contrato) {
		int parcelasVencidas = 0;
		Date dataHoje = DateUtil.gerarDataHoje();
		
		for (ContratoCobrancaDetalhes parcela : contrato.getListContratoCobrancaDetalhes()) {
			// verifica parcelas vencidas, se maior que 1 não mostra contrato
			if (parcela.getDataVencimento().before(dataHoje) && !parcela.isParcelaPaga()) {
				parcelasVencidas = parcelasVencidas + 1;
				
				if (parcelasVencidas > 1) {
					break;
				}
			}
		}
		
		return parcelasVencidas;
	}
	
	public void atualizaValorParcelaSemIPCA() {
		SimulacaoVO simulacaoVO = calcularParcelas();
		ContratoCobrancaDetalhesDao cDetalhesDao = new ContratoCobrancaDetalhesDao();
		
		for (ContratoCobrancaDetalhes parcela : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			BigDecimal valorFace = BigDecimal.ZERO;
			BigDecimal valorJuros = BigDecimal.ZERO;
			BigDecimal valorAmortizacao = BigDecimal.ZERO;
			String numeroParcelaStr = parcela.getNumeroParcela();
			
			for (SimulacaoDetalheVO parcelasSimulacao : simulacaoVO.getParcelas()) {
				valorFace = BigDecimal.ZERO;
				valorJuros = BigDecimal.ZERO;
				valorAmortizacao = BigDecimal.ZERO;
				
				if (numeroParcelaStr.equals(parcelasSimulacao.getNumeroParcela().toString())) {
					valorJuros = parcelasSimulacao.getJuros();
					valorAmortizacao = parcelasSimulacao.getAmortizacao();
					break; 
				}
			}

			parcela.setValorJurosSemIPCA(valorJuros);
			parcela.setValorAmortizacaoSemIPCA(valorAmortizacao);
			
			cDetalhesDao.merge(parcela);
		}
	}
	
	public void atualizaContratoDadosCessaoBRL() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.objetoContratoCobranca.setCedenteBRLCessao(this.cedenteCessao);
		this.objetoContratoCobranca.setCessionario("Galleria Home Equity FIDC");
		this.objetoContratoCobranca.setDataAquisicaoCessao(this.dataAquisicao);
		
		this.objetoContratoCobranca.setValorCessao(somatoriaValorePresenteContratos);
		this.objetoContratoCobranca.setValorAgilCessao(somatoriaValorePresenteContratos);
		
		
		
		if (this.usaTaxaJurosDiferenciada) {
			this.objetoContratoCobranca.setTxJurosCessao(this.txJurosCessao);
		} else {
			this.objetoContratoCobranca.setTxJurosCessao(this.objetoContratoCobranca.getTxJurosParcelas());
		}
		
		contratoCobrancaDao.merge(this.objetoContratoCobranca);
	}
	
	public void atualizaContratoValorAquisicaoCessaoBRL(BigDecimal valorTotalAquisicaoCessao) {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		
		this.objetoContratoCobranca.setValorCessao(valorTotalAquisicaoCessao);
		
		if (this.objetoContratoCobranca.getValorCCB() != null) {			
			this.objetoContratoCobranca.setValorAgilCessao(valorTotalAquisicaoCessao.subtract(this.objetoContratoCobranca.getValorCCB()));
		}
		
		contratoCobrancaDao.merge(this.objetoContratoCobranca);
	}
	
	public static int mesesEntre(Calendar inicial , Calendar fim ){  
		int qtdMesesIni = (inicial.get(Calendar.YEAR) * 12) + inicial.get(Calendar.MONTH);
		int qtdMesesFim = (fim.get(Calendar.YEAR) * 12) + fim.get(Calendar.MONTH);
		return qtdMesesFim - qtdMesesIni;
	}
	
	public Calendar getDateCalendar(Date data) {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");

		Calendar calendar = Calendar.getInstance(zone, locale);

		calendar.setTime(data);
		
		return calendar;
	}
	
	public void geraJSONCessaoSemIPCA() {
		/***
		 * TODO SE CEDENTE DIFERENTE, GERAR ARQUIVOS DIFERENTES 
		 */		
		
		FacesContext context = FacesContext.getCurrentInstance();
		String patternyyyyMMdd = "yyyyMMdd";
		SimpleDateFormat simpleDateFormatyyyyMMdd = new SimpleDateFormat(patternyyyyMMdd);
		
		String patternyyyyMMddComTraco = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormatyyyyMMddComTraco = new SimpleDateFormat(patternyyyyMMddComTraco);
		
		String identificadorCessao = simpleDateFormatyyyyMMdd.format(DateUtil.gerarDataHoje()) + this.objetoContratoCobranca.getNumeroContrato();
		
		ParametrosDao pDao = new ParametrosDao();
		this.pathJSON = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomeJSON = this.objetoContratoCobranca.getNumeroContrato() + ".json";
		
		JSONObject jsonSchema = new JSONObject();
		jsonSchema.put("$schema", "https://schemas.brltrust.com.br/json/fidc/v1.2/cessao.schema.json");
		
		JSONObject jsonFundo = new JSONObject();
		jsonFundo.put("identificacao", Long.valueOf("37294759000134"));
		jsonFundo.put("nome", "FIDC GALLERIA");		
		jsonSchema.put("fundo", jsonFundo);
		
		JSONObject jsonCessao = new JSONObject();
		
		JSONObject jsonOriginador = new JSONObject();
		jsonOriginador.put("codigo", "34425347000106");
		jsonOriginador.put("nome", "GALLERIA FINANCAS SEC");		
		jsonCessao.put("originador", jsonOriginador);
		
		jsonCessao.put("identificadorCessao", identificadorCessao);
		
		JSONArray jsonRecebiveis = new JSONArray();
		
		/**
		 * verifica se tem parcela zero
		 */
		int countCarencia = 0;
		boolean temParcelaZeo = false;

		for (ContratoCobrancaDetalhes parcela : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			if (parcela.getNumeroParcela().equals("0")) {
				temParcelaZeo = true;
			}
		}
		
		if (temParcelaZeo) {
			countCarencia = this.objetoContratoCobranca.getMesesCarencia() + 1;
		} else {
			countCarencia = this.objetoContratoCobranca.getMesesCarencia();
		}
		/**
		 * FIM
		 */
		
		int countParcelas = 0;
		
		ContratoCobrancaMB contratoCobranca = new ContratoCobrancaMB();		
		
		atualizaContratoDadosCessaoBRL();
		
		/***
		 * INICIO - GET VALOR FACE SEM IPCA
		 */
		
		atualizaValorParcelaSemIPCA();
		
		/***
		 * FIM - GET VALOR FACE SEM IPCA
		 */
		
		/***
		 * CALCULA VALOR PRESENTE CONTRATO
		 */
		//BigDecimal valorTotalPresenteContrato = contratoCobranca.calcularValorPresenteTotalContrato(this.objetoContratoCobranca);
		BigDecimal valorTotalPresenteContrato = BigDecimal.ZERO;
		BigDecimal taxaJurosCessao = BigDecimal.ZERO;
				
		if (this.objetoContratoCobranca != null) {
			if (this.objetoContratoCobranca.getTxJurosCessao() != null) {
				taxaJurosCessao = this.objetoContratoCobranca.getTxJurosCessao();
			} else {
				taxaJurosCessao = this.objetoContratoCobranca.getTxJurosParcelas();
			}
		} else {
			taxaJurosCessao = this.objetoContratoCobranca.getTxJurosParcelas();
		}
		
		Date datahoje = DateUtil.gerarDataHoje();
		for (ContratoCobrancaDetalhes parcela : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			if (parcela.getDataVencimento().after(datahoje)) {
				BigDecimal valorPresenteParcela = calcularValorPresenteParcela(parcela.getId(), taxaJurosCessao, this.objetoContratoCobranca.getDataAquisicaoCessao());
				valorTotalPresenteContrato = valorTotalPresenteContrato.add(valorPresenteParcela);
			}
		}
		
		valorTotalPresenteContrato = valorTotalPresenteContrato.divide(this.objetoContratoCobranca.getValorImovel(), 4, RoundingMode.HALF_DOWN);
		valorTotalPresenteContrato = valorTotalPresenteContrato.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_DOWN);
		
		/***
		 * FIM - CALCULA VALOR PRESENTE CONTRATO
		 */
		
		/**
		 * INICIO - CALCULA MESES CARENCIA
		 */
		int mesesCarencia = 0;
		Date dataHoje = DateUtil.gerarDataHoje();
		for (ContratoCobrancaDetalhes parcela : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			if (!parcela.isParcelaPaga()) {
				mesesCarencia = mesesEntre(getDateCalendar(dataHoje),getDateCalendar(parcela.getDataVencimento()));
				break;
			}			
		}
		
		if (mesesCarencia > 0) {
			mesesCarencia = mesesCarencia - 1;			
		}
		/**
		 * FIM - CALCULA MESES CARENCIA
		 */
		
		this.valorTotalFaceCessao = BigDecimal.ZERO;
		this.valorTotalAquisicaoCessao = BigDecimal.ZERO;
		
		for (ContratoCobrancaDetalhes parcela : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) { 
			countParcelas = countParcelas + 1;
			if (countParcelas > countCarencia) {
				if (parcela.getDataVencimento().after(this.dataAquisicao)) {
					JSONObject jsonRecebivel = new JSONObject();
					
					String numeroParcela = "";
					
					if (parcela.getNumeroParcela().length() == 1) {
						numeroParcela = "00" + parcela.getNumeroParcela();
					} else if (parcela.getNumeroParcela().length() == 2) {
						numeroParcela = "0" + parcela.getNumeroParcela();
					} else {
						numeroParcela = parcela.getNumeroParcela();
					}
					
					jsonRecebivel.put("numeroControle", this.objetoContratoCobranca.getNumeroContratoSeguro() + "-" + numeroParcela);
					jsonRecebivel.put("coobrigacao", false);
					jsonRecebivel.put("ocorrencia", 1);
					jsonRecebivel.put("tipo", 73);
					jsonRecebivel.put("documento", this.objetoContratoCobranca.getNumeroContratoSeguro());
					jsonRecebivel.put("termoCessao", this.objetoContratoCobranca.getTermoCessao());
					
					JSONObject jsonSacado = new JSONObject();
					
					JSONObject jsonPessoa = new JSONObject();
					if (this.objetoContratoCobranca.getPagador().getCpf() != null && !this.objetoContratoCobranca.getPagador().getCpf().equals("")) {
						jsonPessoa.put("tipo", "PF");
						jsonPessoa.put("identificacao", Long.valueOf(getStringSemCaracteres(this.objetoContratoCobranca.getPagador().getCpf())));				
					} else {
						jsonPessoa.put("tipo", "PJ");
						jsonPessoa.put("identificacao", Long.valueOf(getStringSemCaracteres(this.objetoContratoCobranca.getPagador().getCnpj())));
					}
					jsonPessoa.put("nome", this.objetoContratoCobranca.getPagador().getNome());
					jsonSacado.put("pessoa", jsonPessoa);
					
					JSONObject jsonEndereco = new JSONObject();
					jsonEndereco.put("cep", Long.valueOf(getStringSemCaracteres(this.objetoContratoCobranca.getPagador().getCep())));
					jsonEndereco.put("logradouro", this.objetoContratoCobranca.getPagador().getEndereco());
					jsonEndereco.put("numero", this.objetoContratoCobranca.getPagador().getNumero());
					jsonEndereco.put("complemento", this.objetoContratoCobranca.getPagador().getComplemento());
					jsonEndereco.put("bairro", this.objetoContratoCobranca.getPagador().getBairro());
					jsonEndereco.put("municipio", this.objetoContratoCobranca.getPagador().getCidade());
					jsonEndereco.put("uf", this.objetoContratoCobranca.getPagador().getEstado());
					jsonSacado.put("endereco", jsonEndereco);
			
					jsonRecebivel.put("sacado", jsonSacado);
					
					JSONObject jsonCedente = new JSONObject();
					jsonCedente.put("tipo", "PJ");
					
					if (this.cedenteCessao.equals("BMP Money Plus SCD S.A.")) {
						jsonCedente.put("identificacao", Long.valueOf("34337707000100"));
						jsonCedente.put("nome", "BMP Money Plus SCD S.A.");		
					} else {
						jsonCedente.put("identificacao", Long.valueOf("34425347000106"));
						jsonCedente.put("nome", "Galleria Finanças Securitizadora S.A.");	
					}
					jsonRecebivel.put("cedente", jsonCedente);
					
					jsonRecebivel.put("aquisicao", simpleDateFormatyyyyMMddComTraco.format(this.dataAquisicao));
					jsonRecebivel.put("emissao", simpleDateFormatyyyyMMddComTraco.format(this.objetoContratoCobranca.getDataInicio()));
					jsonRecebivel.put("vencimento", simpleDateFormatyyyyMMddComTraco.format(parcela.getDataVencimento()));
					JSONObject jsonValores = new JSONObject();
					
					BigDecimal valorTotalFaceCessaoCalc = BigDecimal.ZERO;
					
					//valorTotalFaceCessaoCalc = parcela.getValorAmortizacaoSemIPCA().add(parcela.getValorJurosSemIPCA()).setScale(2, RoundingMode.HALF_EVEN);
					valorTotalFaceCessaoCalc = parcela.getVlrParcela().subtract(parcela.getSeguroDFI()).subtract(parcela.getSeguroMIP()).subtract(parcela.getTaxaAdm());
					
					this.valorTotalFaceCessao = this.valorTotalFaceCessao.add(valorTotalFaceCessaoCalc);
					jsonValores.put("face", valorTotalFaceCessaoCalc);
					
					BigDecimal valorTotalAquisicaoCessaoCalc = BigDecimal.ZERO;
					
					if (this.usaTaxaJurosDiferenciada) {
						valorTotalAquisicaoCessaoCalc = calcularValorPresenteParcela(parcela.getId(), this.txJurosCessao, this.dataAquisicao);
						jsonValores.put("aquisicao", valorTotalAquisicaoCessaoCalc);
					} else {
						valorTotalAquisicaoCessaoCalc = calcularValorPresenteParcela(parcela.getId(), this.objetoContratoCobranca.getTxJurosParcelas(), this.dataAquisicao);
						jsonValores.put("aquisicao", valorTotalAquisicaoCessaoCalc);
					}					

					this.valorTotalAquisicaoCessao = this.valorTotalAquisicaoCessao.add(valorTotalAquisicaoCessaoCalc);
					
					jsonRecebivel.put("valores", jsonValores);
					
					JSONObject jsonDados = new JSONObject();
					
					if (this.objetoContratoCobranca.isCorrigidoIPCA() || this.objetoContratoCobranca.isCorrigidoNovoIPCA()) {
						jsonDados.put("indice", "IPCA");
					} else {
						jsonDados.put("indice", "Pré-Fixado");						
					}
					
					jsonDados.put("sistemaAmortizacao", this.objetoContratoCobranca.getTipoCalculo());
					jsonDados.put("valorDaGarantia", this.objetoContratoCobranca.getValorImovel());
					jsonDados.put("tipo", this.objetoContratoCobranca.getTipoImovel());					
					jsonDados.put("LTV", valorTotalPresenteContrato);					
					jsonDados.put("empresa", this.objetoContratoCobranca.getEmpresaImovel());
					jsonDados.put("contemSeguroMIPeDFI", "SIM");
					jsonDados.put("valorEmprestimo", this.objetoContratoCobranca.getValorCCB());
					jsonDados.put("garantiaAtual", this.objetoContratoCobranca.getImovel().getNome());
					
					
					jsonDados.put("taxaCessao", this.objetoContratoCobranca.getTxJurosParcelas());
					jsonDados.put("taxaJuros", this.objetoContratoCobranca.getTxJurosParcelas());
					jsonDados.put("numeroDeParcelas", this.objetoContratoCobranca.getQtdeParcelas());
					jsonDados.put("mesesDeCarencia", mesesCarencia);
																			
					jsonRecebivel.put("dados", jsonDados);		
					
					jsonRecebiveis.put(jsonRecebivel);
				}
			}
		} 		
		
		jsonCessao.put("recebiveis", jsonRecebiveis);
		
		jsonSchema.put("cessao", jsonCessao);

//		FileOutputStream fileStream;
		try {
			downloadJson(jsonSchema);
			
//			fileStream = new FileOutputStream(new File(this.pathJSON + this.nomeJSON));
//			OutputStreamWriter file;
//			file = new OutputStreamWriter(fileStream, "UTF-8");
//			
//            file.write(jsonSchema.toString());
//            file.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.jsonGerado = true;
		
		atualizaContratoValorAquisicaoCessaoBRL(this.valorTotalAquisicaoCessao);
		
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Geração JSON BRL Cessão: JSON gerado com sucesso!",
						""));	
	}
	
	public void geraJSONCessao() {
		/***
		 * TODO SE CEDENTE DIFERENTE, GERAR ARQUIVOS DIFERENTES 
		 */		
		
		FacesContext context = FacesContext.getCurrentInstance();
		String patternyyyyMMdd = "yyyyMMdd";
		SimpleDateFormat simpleDateFormatyyyyMMdd = new SimpleDateFormat(patternyyyyMMdd);
		
		String patternyyyyMMddComTraco = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormatyyyyMMddComTraco = new SimpleDateFormat(patternyyyyMMddComTraco);
		
		String identificadorCessao = simpleDateFormatyyyyMMdd.format(DateUtil.gerarDataHoje()) + this.objetoContratoCobranca.getNumeroContrato();
		
		ParametrosDao pDao = new ParametrosDao();
		this.pathJSON = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomeJSON = this.objetoContratoCobranca.getNumeroContrato() + ".json";
		
		JSONObject jsonSchema = new JSONObject();
		jsonSchema.put("$schema", "https://schemas.brltrust.com.br/json/fidc/v1.2/cessao.schema.json");
		
		JSONObject jsonFundo = new JSONObject();
		jsonFundo.put("identificacao", Long.valueOf("37294759000134"));
		jsonFundo.put("nome", "FIDC GALLERIA");		
		jsonSchema.put("fundo", jsonFundo);
		
		JSONObject jsonCessao = new JSONObject();
		
		JSONObject jsonOriginador = new JSONObject();
		jsonOriginador.put("codigo", "34425347000106");
		jsonOriginador.put("nome", "GALLERIA FINANCAS SEC");		
		jsonCessao.put("originador", jsonOriginador);
		
		jsonCessao.put("identificadorCessao", identificadorCessao);
		
		JSONArray jsonRecebiveis = new JSONArray();
		
		/**
		 * verifica se tem parcela zero
		 */
		int countCarencia = 0;
		boolean temParcelaZeo = false;

		for (ContratoCobrancaDetalhes parcela : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			if (parcela.getNumeroParcela().equals("0")) {
				temParcelaZeo = true;
			}
		}
		
		if (temParcelaZeo) {
			countCarencia = this.objetoContratoCobranca.getMesesCarencia() + 1;
		} else {
			countCarencia = this.objetoContratoCobranca.getMesesCarencia();
		}
		/**
		 * FIM
		 */
		
		int countParcelas = 0;
		
		ContratoCobrancaMB contratoCobranca = new ContratoCobrancaMB();		
		
		atualizaContratoDadosCessaoBRL();
		
		/***
		 * INICIO - GET VALOR FACE SEM IPCA
		 */
		
		atualizaValorParcelaSemIPCA();
		
		/***
		 * FIM - GET VALOR FACE SEM IPCA
		 */
		
		/***
		 * CALCULA VALOR PRESENTE CONTRATO
		 */
		//BigDecimal valorTotalPresenteContrato = contratoCobranca.calcularValorPresenteTotalContrato(this.objetoContratoCobranca);
		BigDecimal valorTotalPresenteContrato = BigDecimal.ZERO;
		BigDecimal taxaJurosCessao = BigDecimal.ZERO;
				
		if (this.objetoContratoCobranca != null) {
			if (this.objetoContratoCobranca.getTxJurosCessao() != null) {
				taxaJurosCessao = this.objetoContratoCobranca.getTxJurosCessao();
			} else {
				taxaJurosCessao = this.objetoContratoCobranca.getTxJurosParcelas();
			}
		} else {
			taxaJurosCessao = this.objetoContratoCobranca.getTxJurosParcelas();
		}
		
		Date datahoje = DateUtil.gerarDataHoje();
		for (ContratoCobrancaDetalhes parcela : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			if (parcela.getDataVencimento().after(datahoje)) {
				BigDecimal valorPresenteParcela = calcularValorPresenteParcela(parcela.getId(), taxaJurosCessao, this.objetoContratoCobranca.getDataAquisicaoCessao());
				valorTotalPresenteContrato = valorTotalPresenteContrato.add(valorPresenteParcela);
			}
		}
		
		valorTotalPresenteContrato = valorTotalPresenteContrato.divide(this.objetoContratoCobranca.getValorImovel(), 4, RoundingMode.HALF_DOWN);
		valorTotalPresenteContrato = valorTotalPresenteContrato.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_DOWN);
		
		/***
		 * FIM - CALCULA VALOR PRESENTE CONTRATO
		 */
		
		/**
		 * INICIO - CALCULA MESES CARENCIA
		 */
		int mesesCarencia = 0;
		Date dataHoje = DateUtil.gerarDataHoje();
		for (ContratoCobrancaDetalhes parcela : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			if (!parcela.isParcelaPaga()) {
				mesesCarencia = mesesEntre(getDateCalendar(dataHoje),getDateCalendar(parcela.getDataVencimento()));
				break;
			}			
		}
		
		if (mesesCarencia > 0) {
			mesesCarencia = mesesCarencia - 1;			
		}
		/**
		 * FIM - CALCULA MESES CARENCIA
		 */
		
		this.valorTotalFaceCessao = BigDecimal.ZERO;
		this.valorTotalAquisicaoCessao = BigDecimal.ZERO;
		
		for (ContratoCobrancaDetalhes parcela : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) { 
			countParcelas = countParcelas + 1;
			if (countParcelas > countCarencia) {
				if (parcela.getDataVencimento().after(this.dataAquisicao)) {
					JSONObject jsonRecebivel = new JSONObject();
					
					String numeroParcela = "";
					
					if (parcela.getNumeroParcela().length() == 1) {
						numeroParcela = "00" + parcela.getNumeroParcela();
					} else if (parcela.getNumeroParcela().length() == 2) {
						numeroParcela = "0" + parcela.getNumeroParcela();
					} else {
						numeroParcela = parcela.getNumeroParcela();
					}
					
					jsonRecebivel.put("numeroControle", this.objetoContratoCobranca.getNumeroContratoSeguro() + "-" + numeroParcela);
					jsonRecebivel.put("coobrigacao", false);
					jsonRecebivel.put("ocorrencia", 1);
					jsonRecebivel.put("tipo", 73);
					jsonRecebivel.put("documento", this.objetoContratoCobranca.getNumeroContratoSeguro());
					jsonRecebivel.put("termoCessao", this.objetoContratoCobranca.getTermoCessao());
					
					JSONObject jsonSacado = new JSONObject();
					
					JSONObject jsonPessoa = new JSONObject();
					if (this.objetoContratoCobranca.getPagador().getCpf() != null && !this.objetoContratoCobranca.getPagador().getCpf().equals("")) {
						jsonPessoa.put("tipo", "PF");
						jsonPessoa.put("identificacao", Long.valueOf(getStringSemCaracteres(this.objetoContratoCobranca.getPagador().getCpf())));				
					} else {
						jsonPessoa.put("tipo", "PJ");
						jsonPessoa.put("identificacao", Long.valueOf(getStringSemCaracteres(this.objetoContratoCobranca.getPagador().getCnpj())));
					}
					jsonPessoa.put("nome", this.objetoContratoCobranca.getPagador().getNome());
					jsonSacado.put("pessoa", jsonPessoa);
					
					JSONObject jsonEndereco = new JSONObject();
					jsonEndereco.put("cep", Long.valueOf(getStringSemCaracteres(this.objetoContratoCobranca.getPagador().getCep())));
					jsonEndereco.put("logradouro", this.objetoContratoCobranca.getPagador().getEndereco());
					jsonEndereco.put("numero", this.objetoContratoCobranca.getPagador().getNumero());
					jsonEndereco.put("complemento", this.objetoContratoCobranca.getPagador().getComplemento());
					jsonEndereco.put("bairro", this.objetoContratoCobranca.getPagador().getBairro());
					jsonEndereco.put("municipio", this.objetoContratoCobranca.getPagador().getCidade());
					jsonEndereco.put("uf", this.objetoContratoCobranca.getPagador().getEstado());
					jsonSacado.put("endereco", jsonEndereco);
			
					jsonRecebivel.put("sacado", jsonSacado);
					
					JSONObject jsonCedente = new JSONObject();
					jsonCedente.put("tipo", "PJ");
					
					if (this.cedenteCessao.equals("BMP Money Plus SCD S.A.")) {
						jsonCedente.put("identificacao", Long.valueOf("34337707000100"));
						jsonCedente.put("nome", "BMP Money Plus SCD S.A.");		
					} else {
						jsonCedente.put("identificacao", Long.valueOf("34425347000106"));
						jsonCedente.put("nome", "Galleria Finanças Securitizadora S.A.");	
					}
					jsonRecebivel.put("cedente", jsonCedente);
					
					jsonRecebivel.put("aquisicao", simpleDateFormatyyyyMMddComTraco.format(this.dataAquisicao));
					jsonRecebivel.put("emissao", simpleDateFormatyyyyMMddComTraco.format(this.objetoContratoCobranca.getDataInicio()));
					jsonRecebivel.put("vencimento", simpleDateFormatyyyyMMddComTraco.format(parcela.getDataVencimento()));
					JSONObject jsonValores = new JSONObject();
					
					BigDecimal valorTotalFaceCessaoCalc = BigDecimal.ZERO;
					
					//valorTotalFaceCessaoCalc = parcela.getValorAmortizacaoSemIPCA().add(parcela.getValorJurosSemIPCA()).setScale(2, RoundingMode.HALF_EVEN);
					//valorTotalFaceCessaoCalc = parcela.getVlrParcela().subtract(parcela.getSeguroDFI()).subtract(parcela.getSeguroMIP()).subtract(parcela.getTaxaAdm());
					if (parcela.getVlrAmortizacaoParcela() != null && parcela.getVlrJurosParcela() != null) {
						valorTotalFaceCessaoCalc = parcela.getVlrAmortizacaoParcela().add(parcela.getVlrJurosParcela()).setScale(2, RoundingMode.HALF_EVEN);
						jsonValores.put("face", valorTotalFaceCessaoCalc);
					}
					
					this.valorTotalFaceCessao = this.valorTotalFaceCessao.add(valorTotalFaceCessaoCalc);
					jsonValores.put("face", valorTotalFaceCessaoCalc);
					
					BigDecimal valorTotalAquisicaoCessaoCalc = BigDecimal.ZERO;		
					
					if (this.objetoContratoCobranca != null) {
						if (this.objetoContratoCobranca.getTxJurosCessao() != null) {
							valorTotalAquisicaoCessaoCalc = calcularValorPresenteParcelaComIPCA(parcela.getId(), this.objetoContratoCobranca.getTxJurosCessao(), this.objetoContratoCobranca.getDataAquisicaoCessao());
							jsonValores.put("aquisicao", valorTotalAquisicaoCessaoCalc);
						} else {
							valorTotalAquisicaoCessaoCalc = calcularValorPresenteParcelaComIPCA(parcela.getId(), this.objetoContratoCobranca.getTxJurosParcelas(), this.objetoContratoCobranca.getDataAquisicaoCessao());
							jsonValores.put("aquisicao", valorTotalAquisicaoCessaoCalc);
						}
					} 

					this.valorTotalAquisicaoCessao = this.valorTotalAquisicaoCessao.add(valorTotalAquisicaoCessaoCalc);
					
					jsonRecebivel.put("valores", jsonValores);
					
					JSONObject jsonDados = new JSONObject();
					
					if (this.objetoContratoCobranca.isCorrigidoIPCA() || this.objetoContratoCobranca.isCorrigidoNovoIPCA()) {
						jsonDados.put("indice", "IPCA");
					} else {
						jsonDados.put("indice", "Pré-Fixado");						
					}
					
					jsonDados.put("sistemaAmortizacao", this.objetoContratoCobranca.getTipoCalculo());
					jsonDados.put("valorDaGarantia", this.objetoContratoCobranca.getValorImovel());
					jsonDados.put("tipo", this.objetoContratoCobranca.getTipoImovel());					
					jsonDados.put("LTV", valorTotalPresenteContrato);					
					jsonDados.put("empresa", this.objetoContratoCobranca.getEmpresaImovel());
					jsonDados.put("contemSeguroMIPeDFI", "SIM");
					jsonDados.put("valorEmprestimo", this.objetoContratoCobranca.getValorCCB());
					jsonDados.put("garantiaAtual", this.objetoContratoCobranca.getImovel().getNome());
					
					
					jsonDados.put("taxaCessao", this.objetoContratoCobranca.getTxJurosParcelas());
					jsonDados.put("taxaJuros", this.objetoContratoCobranca.getTxJurosParcelas());
					jsonDados.put("numeroDeParcelas", this.objetoContratoCobranca.getQtdeParcelas());
					jsonDados.put("mesesDeCarencia", mesesCarencia);
																			
					jsonRecebivel.put("dados", jsonDados);		
					
					jsonRecebiveis.put(jsonRecebivel);
				}
			}
		} 		
		
		jsonCessao.put("recebiveis", jsonRecebiveis);
		
		jsonSchema.put("cessao", jsonCessao);

		try {
			downloadJson(jsonSchema);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.jsonGerado = true;
		
		atualizaContratoValorAquisicaoCessaoBRL(this.valorTotalAquisicaoCessao);
		
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Geração JSON BRL Cessão: JSON gerado com sucesso!",
						""));	
	}
	
	public void geraJSONLiquidacaoMigracao() {
		/***
		 * TODO SE CEDENTE DIFERENTE, GERAR ARQUIVOS DIFERENTES 
		 */		
		
		FacesContext context = FacesContext.getCurrentInstance();
		
		this.jsonGerado = true;
		String contratosErros = null;
		
		String patternyyyyMMdd = "yyyyMMdd";
		SimpleDateFormat simpleDateFormatyyyyMMdd = new SimpleDateFormat(patternyyyyMMdd);
		
		String patternyyyyMMddComTraco = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormatyyyyMMddComTraco = new SimpleDateFormat(patternyyyyMMddComTraco);
		
		String identificadorCessao = simpleDateFormatyyyyMMdd.format(DateUtil.gerarDataHoje()) + "_LIQ";
		
		ParametrosDao pDao = new ParametrosDao();
		this.pathJSON = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomeJSON = "JSON_BRL_Trust_Migracao_" + identificadorCessao + ".json";
		
		JSONObject jsonSchema = new JSONObject();
		jsonSchema.put("$schema", "https://schemas.brltrust.com.br/json/fidc/v1.2/cessao.schema.json");
		
		JSONObject jsonFundo = new JSONObject();
		jsonFundo.put("identificacao", Long.valueOf("37294759000134"));
		jsonFundo.put("nome", "FIDC GALLERIA");		
		jsonSchema.put("fundo", jsonFundo);
		
		JSONObject jsonCessao = new JSONObject();
		
		JSONObject jsonOriginador = new JSONObject();
		jsonOriginador.put("codigo", "34425347000106");
		jsonOriginador.put("nome", "GALLERIA FINANCAS SEC");		
		jsonCessao.put("originador", jsonOriginador);
		
		jsonCessao.put("identificadorCessao", identificadorCessao);
		
		JSONArray jsonRecebiveis = new JSONArray();
		
		for (ContratoCobranca contrato : this.selectedContratos) {
			for (ContratoCobrancaDetalhes parcela : contrato.getListContratoCobrancaDetalhes()) {
				if (parcela.getDataVencimento().after(this.dataValorPresente) && !parcela.isParcelaPaga()) {
					JSONObject jsonRecebivel = new JSONObject();
					
					String numeroParcela = "";
					
					if (!contrato.getNumeroContrato().equals("01306")) {
						if (parcela.getNumeroParcela().length() == 1) {
							numeroParcela = "00" + parcela.getNumeroParcela();
						} else if (parcela.getNumeroParcela().length() == 2) {
							numeroParcela = "0" + parcela.getNumeroParcela();
						} else {
							numeroParcela = parcela.getNumeroParcela();
						}
					} else {
						int numeroParcelaInt = Integer.valueOf(parcela.getNumeroParcela()) - 6;
						
						numeroParcela = String.valueOf(numeroParcelaInt);
						
						if (numeroParcela.length() == 1) {
							numeroParcela = "00" + numeroParcela;
						} else if (numeroParcela.length() == 2) {
							numeroParcela = "0" + numeroParcela;
						} 
					}
					
					jsonRecebivel.put("numeroControle", contrato.getNumeroContratoSeguro() + "-" + numeroParcela);
					jsonRecebivel.put("coobrigacao", false);
					jsonRecebivel.put("ocorrencia", 95);
					jsonRecebivel.put("tipo", 73);
					jsonRecebivel.put("documento", contrato.getNumeroContratoSeguro());
					jsonRecebivel.put("termoCessao", contrato.getTermoCessao());
										
					JSONObject jsonSacado = new JSONObject();
					
					JSONObject jsonPessoa = new JSONObject();
					if (contrato.getPagador().getCpf() != null && !contrato.getPagador().getCpf().equals("")) {
						jsonPessoa.put("tipo", "PF");
						jsonPessoa.put("identificacao", Long.valueOf(getStringSemCaracteres(contrato.getPagador().getCpf())));				
					} else {
						jsonPessoa.put("tipo", "PJ");
						jsonPessoa.put("identificacao", Long.valueOf(getStringSemCaracteres(contrato.getPagador().getCnpj())));
					}
					jsonPessoa.put("nome", contrato.getPagador().getNome());
					jsonSacado.put("pessoa", jsonPessoa);
					
					JSONObject jsonEndereco = new JSONObject();
					if (contrato.getPagador().getCep() != null && !contrato.getPagador().getCep().equals("")) {
						jsonEndereco.put("cep", Long.valueOf(getStringSemCaracteres(contrato.getPagador().getCep())));
					} else {
						System.out.println(contrato.getPagador().getNome() + "Contrato: " + contrato.getNumeroContrato());
						jsonEndereco.put("cep","");
					}
					
					jsonEndereco.put("logradouro", contrato.getPagador().getEndereco());
					jsonEndereco.put("numero", contrato.getPagador().getNumero());
					jsonEndereco.put("complemento", contrato.getPagador().getComplemento());
					jsonEndereco.put("bairro", contrato.getPagador().getBairro());
					jsonEndereco.put("municipio", contrato.getPagador().getCidade());
					jsonEndereco.put("uf", contrato.getPagador().getEstado());
					jsonSacado.put("endereco", jsonEndereco);
			
					jsonRecebivel.put("sacado", jsonSacado);
					
					JSONObject jsonCedente = new JSONObject();
					jsonCedente.put("tipo", "PJ");
					
					if (contrato.getCedenteBRLCessao().equals("BMP Money Plus SCD S.A.")) {
						jsonCedente.put("identificacao", Long.valueOf("34337707000100"));
						jsonCedente.put("nome", "BMP Money Plus SCD S.A.");		
					} else {
						jsonCedente.put("identificacao", Long.valueOf("34425347000106"));
						jsonCedente.put("nome", "Galleria Finanças Securitizadora S.A.");	
					}
					jsonRecebivel.put("cedente", jsonCedente);
					
					jsonRecebivel.put("aquisicao", simpleDateFormatyyyyMMddComTraco.format(contrato.getDataAquisicaoCessao()));
					jsonRecebivel.put("emissao", simpleDateFormatyyyyMMddComTraco.format(contrato.getDataInicio()));
					jsonRecebivel.put("vencimento", simpleDateFormatyyyyMMddComTraco.format(parcela.getDataVencimento()));
					jsonRecebivel.put("liquidacao", simpleDateFormatyyyyMMddComTraco.format(this.dataValorPresente));
					JSONObject jsonValores = new JSONObject();
					
					if (parcela.getVlrAmortizacaoParcela() != null && parcela.getVlrJurosParcela() != null) {
						jsonValores.put("face", parcela.getVlrAmortizacaoParcela().add(parcela.getVlrJurosParcela()).setScale(2, RoundingMode.HALF_EVEN));
					} else {
						this.jsonGerado = false;
						
						if (contratosErros == null) {
							contratosErros = contrato.getNumeroContrato();
						} else {
							contratosErros = contratosErros + " / " + contrato.getNumeroContrato();
						}
					}
					
					//System.out.println("contrato: " + contrato.getNumeroContrato());
					//System.out.println("cessao: " + contrato.getTxJurosCessao());
					//System.out.println("juros parcela: " + contrato.getTxJurosParcelas());
					
					if (contrato != null) {
						if (contrato.getTxJurosCessao() != null) {
							jsonValores.put("aquisicao", calcularValorPresenteParcelaComIPCA(parcela.getId(), contrato.getTxJurosCessao(), contrato.getDataAquisicaoCessao()));
						} else {
							jsonValores.put("aquisicao", calcularValorPresenteParcelaComIPCA(parcela.getId(), contrato.getTxJurosParcelas(), contrato.getDataAquisicaoCessao()));
						}
					} 
					
					//jsonValores.put("liquidacao", parcela.getVlrRecebido());
					jsonValores.put("liquidacao", calcularValorPresenteParcelaComIPCAGambiarra(parcela.getId(), contrato.getTxJurosParcelas(), this.dataValorPresente));
					
					jsonRecebivel.put("valores", jsonValores);
					
					JSONObject jsonDados = new JSONObject();
					jsonDados.put("indice", "IPCA");	
					
					if (contrato.getTxJurosParcelas() != null) {
						jsonDados.put("taxaJuros", contrato.getTxJurosParcelas());
					}

					JSONObject jsonCessionario = new JSONObject();
					
					JSONObject jsonCessionarioPessoa = new JSONObject();
					
					jsonCessionarioPessoa.put("tipo", "PJ");
					jsonCessionarioPessoa.put("identificacao", "04.200.649/0001-071");
					jsonCessionarioPessoa.put("nome", "COMPANHIA PROVÍNCIA DE SECURITIZAÇÃO");
					
					jsonCessionario.put("pessoa", jsonCessionarioPessoa);
					jsonCessionario.put("conta", "16182008");
					
					jsonDados.put("cessionario", jsonCessionario);
					
					jsonRecebivel.put("dados", jsonDados);		
					
					jsonRecebiveis.put(jsonRecebivel);
				}
			}
		}
		
		jsonCessao.put("recebiveis", jsonRecebiveis);
		
		jsonSchema.put("cessao", jsonCessao);

//		FileOutputStream fileStream;
		try {

			downloadJson(jsonSchema);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (this.jsonGerado) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Geração JSON BRL Liquidação: JSON gerado com sucesso!",
							""));	
		} else {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Geração JSON BRL Liquidação: Este(s) contrato(s) precisa(m) do processo de gerar Cessão novamente: " + contratosErros,
							""));	
		}
	}
	
	
	private void downloadJson(XSSFWorkbook xls) throws IOException {
		
		
		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
				FacesContext.getCurrentInstance());
		String nomeSemvirgula = this.nomeXLS;
		if(nomeSemvirgula.contains(",")) {
			nomeSemvirgula = nomeSemvirgula.replace(",", "");
	    }
		String nomeArquivoDownload = nomeSemvirgula;
		gerador.open(nomeArquivoDownload);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		 xls.write(out);

		 
		gerador.feed(new ByteArrayInputStream(out.toByteArray()));
		gerador.close();
	}
	
	
	private void downloadJson(JSONObject jsonSchema) throws IOException {
		
		
		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
				FacesContext.getCurrentInstance());
		String nomeSemvirgula =this.nomeJSON;
		if(nomeSemvirgula.contains(",")) {
			nomeSemvirgula = nomeSemvirgula.replace(",", "");
	    }
		String nomeArquivoDownload = nomeSemvirgula;
		gerador.open(nomeArquivoDownload);

		gerador.feed(new ByteArrayInputStream(jsonSchema.toString().getBytes()));
		gerador.close();
	}
	
	
	public void geraJSONLiquidacao() {
		/***
		 * TODO SE CEDENTE DIFERENTE, GERAR ARQUIVOS DIFERENTES 
		 */		
		
		FacesContext context = FacesContext.getCurrentInstance();
		
		this.valorTotalLiquidacao = BigDecimal.ZERO;
		this.qtdeLiquidados = 0;
		
		this.jsonGerado = true;
		String contratosErros = null;
		
		String patternyyyyMMdd = "yyyyMMdd";
		SimpleDateFormat simpleDateFormatyyyyMMdd = new SimpleDateFormat(patternyyyyMMdd);
		
		String patternyyyyMMddComTraco = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormatyyyyMMddComTraco = new SimpleDateFormat(patternyyyyMMddComTraco);
		
		String identificadorCessao = simpleDateFormatyyyyMMdd.format(DateUtil.gerarDataHoje()) + "_LIQ";
		
		ParametrosDao pDao = new ParametrosDao();
		this.pathJSON = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		
		JSONObject jsonSchema = new JSONObject();
		jsonSchema.put("$schema", "https://schemas.brltrust.com.br/json/fidc/v1.2/cessao.schema.json");
		
		JSONObject jsonFundo = new JSONObject();
		jsonFundo.put("identificacao", Long.valueOf("37294759000134"));
		jsonFundo.put("nome", "FIDC GALLERIA");		
		jsonSchema.put("fundo", jsonFundo);
		
		JSONObject jsonCessao = new JSONObject();
		
		JSONObject jsonOriginador = new JSONObject();
		jsonOriginador.put("codigo", "34425347000106");
		jsonOriginador.put("nome", "GALLERIA FINANCAS SEC");		
		jsonCessao.put("originador", jsonOriginador);
		
		jsonCessao.put("identificadorCessao", identificadorCessao);
		
		JSONArray jsonRecebiveis = new JSONArray();
		
		if(this.selectedJsonLiquidacao.size() == 0) {
			this.selectedJsonLiquidacao = parcelasLiquidacao;
		}
		
		for (ContratoCobrancaBRLLiquidacao parcela : this.selectedJsonLiquidacao) {
			JSONObject jsonRecebivel = new JSONObject();
			
			String numeroParcela = "";
			
			if (parcela.getNumeroParcela().length() == 1) {
				numeroParcela = "00" + parcela.getNumeroParcela();
			} else if (parcela.getNumeroParcela().length() == 2) {
				numeroParcela = "0" + parcela.getNumeroParcela();
			} else {
				numeroParcela = parcela.getNumeroParcela();
			}
			
			jsonRecebivel.put("numeroControle", parcela.getContrato().getNumeroContratoSeguro() + "-" + numeroParcela);
			jsonRecebivel.put("coobrigacao", false);
			jsonRecebivel.put("ocorrencia", 77);
			jsonRecebivel.put("tipo", 73);
			jsonRecebivel.put("documento", parcela.getContrato().getNumeroContratoSeguro());
			jsonRecebivel.put("termoCessao", parcela.getContrato().getTermoCessao());
			
			JSONObject jsonSacado = new JSONObject();
			
			JSONObject jsonPessoa = new JSONObject();
			if (parcela.getContrato().getPagador().getCpf() != null && !parcela.getContrato().getPagador().getCpf().equals("")) {
				jsonPessoa.put("tipo", "PF");
				jsonPessoa.put("identificacao", Long.valueOf(getStringSemCaracteres(parcela.getContrato().getPagador().getCpf())));				
			} else {
				jsonPessoa.put("tipo", "PJ");
				jsonPessoa.put("identificacao", Long.valueOf(getStringSemCaracteres(parcela.getContrato().getPagador().getCnpj())));
			}
			jsonPessoa.put("nome", parcela.getContrato().getPagador().getNome());
			jsonSacado.put("pessoa", jsonPessoa);
			
			JSONObject jsonEndereco = new JSONObject();
			if (!CommonsUtil.semValor(parcela.getContrato().getPagador().getCep())) {
				jsonEndereco.put("cep", Long.valueOf(getStringSemCaracteres(parcela.getContrato().getPagador().getCep())));
			} else {
				jsonEndereco.put("cep", Long.valueOf(00000000));
			}
			jsonEndereco.put("logradouro", parcela.getContrato().getPagador().getEndereco());
			jsonEndereco.put("numero", parcela.getContrato().getPagador().getNumero());
			jsonEndereco.put("complemento", parcela.getContrato().getPagador().getComplemento());
			jsonEndereco.put("bairro", parcela.getContrato().getPagador().getBairro());
			jsonEndereco.put("municipio", parcela.getContrato().getPagador().getCidade());
			jsonEndereco.put("uf", parcela.getContrato().getPagador().getEstado());
			jsonSacado.put("endereco", jsonEndereco);
	
			jsonRecebivel.put("sacado", jsonSacado);
			
			JSONObject jsonCedente = new JSONObject();
			jsonCedente.put("tipo", "PJ");
			
			if (parcela.getContrato().getCedenteBRLCessao().equals("BMP Money Plus SCD S.A.")) {
				jsonCedente.put("identificacao", Long.valueOf("34337707000100"));
				jsonCedente.put("nome", "BMP Money Plus SCD S.A.");		
			} else {
				jsonCedente.put("identificacao", Long.valueOf("34425347000106"));
				jsonCedente.put("nome", "Galleria Finanças Securitizadora S.A.");	
			}
			jsonRecebivel.put("cedente", jsonCedente);
			
			jsonRecebivel.put("aquisicao", simpleDateFormatyyyyMMddComTraco.format(parcela.getContrato().getDataAquisicaoCessao()));
			jsonRecebivel.put("emissao", simpleDateFormatyyyyMMddComTraco.format(parcela.getContrato().getDataInicio()));
			jsonRecebivel.put("vencimento", simpleDateFormatyyyyMMddComTraco.format(parcela.getDataVencimento()));
			jsonRecebivel.put("liquidacao", simpleDateFormatyyyyMMddComTraco.format(parcela.getDataVencimento()));
			JSONObject jsonValores = new JSONObject();
			
			if (parcela.getVlrAmortizacaoSemIPCA() != null && parcela.getVlrJurosSemIPCA() != null) {
				jsonValores.put("face", parcela.getVlrAmortizacaoSemIPCA().add(parcela.getVlrJurosSemIPCA()).setScale(2, RoundingMode.HALF_EVEN));
			} else {
				this.jsonGerado = false;
				
				if (contratosErros == null) {
					contratosErros = parcela.getContrato().getNumeroContrato();
				} else {
					contratosErros = contratosErros + " / " + parcela.getContrato().getNumeroContrato();
				}
			}
			
			//System.out.println("contrato: " + parcela.getContrato().getNumeroContrato());
			//System.out.println("cessao: " + parcela.getContrato().getTxJurosCessao());
			//System.out.println("juros parcela: " + parcela.getContrato().getTxJurosParcelas());
			
			if (parcela.getContrato() != null) {
				if (parcela.getContrato().getTxJurosCessao() != null) {
					jsonValores.put("aquisicao", calcularValorPresenteParcela(parcela.getId(), parcela.getContrato().getTxJurosCessao(), parcela.getContrato().getDataAquisicaoCessao()));
				} else {
					jsonValores.put("aquisicao", calcularValorPresenteParcela(parcela.getId(), parcela.getContrato().getTxJurosParcelas(), parcela.getContrato().getDataAquisicaoCessao()));
				}
			} else {
				jsonValores.put("aquisicao", calcularValorPresenteParcela(parcela.getId(), parcela.getContrato().getTxJurosParcelas(), parcela.getContrato().getDataAquisicaoCessao()));
			}
			
			BigDecimal valorParcelaOriginal = parcela.getVlrJurosParcela().add(parcela.getVlrAmortizacaoParcela());
			
			if(DateUtil.getDifferenceDays(parcela.getDataPagamento(), parcela.getDataVencimento()) >= 30) {
				valorParcelaOriginal = parcela.getVlrRecebido();
			}
			
			/*if (parcela.getDataPagamento().before(parcela.getDataVencimento()) &&
					parcela.getVlrRecebido().compareTo(valorParcelaOriginal) < 0) {
				jsonValores.put("liquidacao", parcela.getVlrRecebido());
				
				this.valorTotalLiquidacao = this.valorTotalLiquidacao.add(parcela.getVlrRecebido());
			} */
			
			jsonValores.put("liquidacao", valorParcelaOriginal); 
			this.valorTotalLiquidacao = this.valorTotalLiquidacao.add(valorParcelaOriginal);
			
			this.qtdeLiquidados = this.qtdeLiquidados + 1;
			
			jsonRecebivel.put("valores", jsonValores);
			
			JSONObject jsonDados = new JSONObject();
			jsonDados.put("indice", "IPCA");			
			jsonRecebivel.put("dados", jsonDados);		
			
			jsonRecebiveis.put(jsonRecebivel);
		}
		
		jsonCessao.put("recebiveis", jsonRecebiveis);
		
		jsonSchema.put("cessao", jsonCessao);
		
		this.nomeJSON = "JSON_BRL_Trust_Liquidacao_" + identificadorCessao + "_QtdeLiquidados_" + this.qtdeLiquidados + "_ValorTotal_" + this.valorTotalLiquidacao + ".json";

//		FileOutputStream fileStream;
		try {

			downloadJson(jsonSchema);
			
//			fileStream = new FileOutputStream(new File(this.pathJSON + this.nomeJSON));
//			OutputStreamWriter file;
//			file = new OutputStreamWriter(fileStream, "UTF-8");
//			
//            file.write(jsonSchema.toString());
//            file.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (this.jsonGerado) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Geração JSON BRL Liquidação: JSON gerado com sucesso!",
							""));	
		} else {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Geração JSON BRL Liquidação: Este(s) contrato(s) precisa(m) do processo de gerar Cessão novamente: " + contratosErros,
							""));	
		}
	}
	
	private SimulacaoVO calcularParcelas() {
		BigDecimal tarifaIOFDiario;
		BigDecimal tarifaIOFAdicional = BigDecimal.valueOf(0.38).divide(BigDecimal.valueOf(100));

		SimulacaoVO simulador = new SimulacaoVO();

		if (this.objetoContratoCobranca.getPagador().getCpf() != null) {
			if ( DateUtil.isAfterDate(this.objetoContratoCobranca.getDataInicio(), SiscoatConstants.TROCA_IOF ) ) {
				tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PF_ANTIGA.divide(BigDecimal.valueOf(100));
			}else {
				tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PF.divide(BigDecimal.valueOf(100));
			}
			simulador.setTipoPessoa("PF");
		} else {
			if ( DateUtil.isAfterDate(this.objetoContratoCobranca.getDataInicio(), SiscoatConstants.TROCA_IOF ) ) {
				tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PJ_ANTIGA.divide(BigDecimal.valueOf(100));
			}else {
				tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PJ.divide(BigDecimal.valueOf(100));
			}
			simulador.setTipoPessoa("PJ");
		}

		simulador.setDataSimulacao(DateUtil.getDataHoje());
		simulador.setTarifaIOFDiario(tarifaIOFDiario);
		simulador.setTarifaIOFAdicional(tarifaIOFAdicional);
		simulador.setSeguroMIP(SiscoatConstants.SEGURO_MIP);
		simulador.setSeguroDFI(SiscoatConstants.SEGURO_DFI);
		// valores
		simulador.setValorCredito(this.objetoContratoCobranca.getValorCCB());
		simulador.setTaxaJuros(this.objetoContratoCobranca.getTxJurosParcelas());
		simulador.setCarencia(BigInteger.valueOf(this.objetoContratoCobranca.getMesesCarencia()));
		simulador.setQtdParcelas(BigInteger.valueOf(this.objetoContratoCobranca.getQtdeParcelas()));
		simulador.setValorImovel(this.objetoContratoCobranca.getValorImovel());
//			simulador.setCustoEmissaoValor(custoEmissaoValor);
		simulador.setTipoCalculo(this.objetoContratoCobranca.getTipoCalculo());
		simulador.setNaoCalcularDFI(
				!(this.objetoContratoCobranca.isTemSeguroDFI() && this.objetoContratoCobranca.isTemSeguro()));
		simulador.setNaoCalcularMIP(
				!(this.objetoContratoCobranca.isTemSeguroMIP() && this.objetoContratoCobranca.isTemSeguro()));
		simulador.setNaoCalcularTxAdm(!this.objetoContratoCobranca.isTemTxAdm());

		simulador.calcular();
		return simulador;
	}
	
	public void geraJSONLiquidacaoParcela() {
		/***
		 * TODO SE CEDENTE DIFERENTE, GERAR ARQUIVOS DIFERENTES 
		 */		
		
		FacesContext context = FacesContext.getCurrentInstance();
		String patternyyyyMMdd = "yyyyMMdd";
		SimpleDateFormat simpleDateFormatyyyyMMdd = new SimpleDateFormat(patternyyyyMMdd);
		
		String patternyyyyMMddComTraco = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormatyyyyMMddComTraco = new SimpleDateFormat(patternyyyyMMddComTraco);
		
		String numeroParcela = "";
		
		if (this.parcelaLiquidacao.getNumeroParcela().length() == 1) {
			numeroParcela = "00" + this.parcelaLiquidacao.getNumeroParcela();
		} else if (this.parcelaLiquidacao.getNumeroParcela().length() == 2) {
			numeroParcela = "0" + this.parcelaLiquidacao.getNumeroParcela();
		} else {
			numeroParcela = this.parcelaLiquidacao.getNumeroParcela();
		}
		
		String identificadorCessao = simpleDateFormatyyyyMMdd.format(DateUtil.gerarDataHoje()) + numeroParcela + "_LIQ_";
		
		ParametrosDao pDao = new ParametrosDao();
		this.pathJSON = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomeJSON = "JSON_BRL_Trust_Liquidacao_" + identificadorCessao + ".json";
		
		JSONObject jsonSchema = new JSONObject();
		jsonSchema.put("$schema", "https://schemas.brltrust.com.br/json/fidc/v1.2/cessao.schema.json");
		
		JSONObject jsonFundo = new JSONObject();
		jsonFundo.put("identificacao", Long.valueOf("37294759000134"));
		jsonFundo.put("nome", "FIDC GALLERIA");		
		jsonSchema.put("fundo", jsonFundo);
		
		JSONObject jsonCessao = new JSONObject();
		
		JSONObject jsonOriginador = new JSONObject();
		jsonOriginador.put("codigo", "34425347000106");
		jsonOriginador.put("nome", "GALLERIA FINANCAS SEC");		
		jsonCessao.put("originador", jsonOriginador);
		
		jsonCessao.put("identificadorCessao", identificadorCessao);
		
		JSONArray jsonRecebiveis = new JSONArray();
		
		JSONObject jsonRecebivel = new JSONObject();
		
		jsonRecebivel.put("numeroControle", this.parcelaLiquidacao.getContrato().getNumeroContratoSeguro() + "-" + numeroParcela);
		jsonRecebivel.put("coobrigacao", false);
		jsonRecebivel.put("ocorrencia", 77);
		jsonRecebivel.put("tipo", 73 ); // total
		//jsonRecebivel.put("tipo", 73); parcial 14
		jsonRecebivel.put("documento", this.parcelaLiquidacao.getContrato().getNumeroContratoSeguro());
		jsonRecebivel.put("termoCessao", this.parcelaLiquidacao.getContrato().getTermoCessao());
		
		JSONObject jsonSacado = new JSONObject();
		
		JSONObject jsonPessoa = new JSONObject();
		if (this.parcelaLiquidacao.getContrato().getPagador().getCpf() != null && !this.parcelaLiquidacao.getContrato().getPagador().getCpf().equals("")) {
			jsonPessoa.put("tipo", "PF");
			jsonPessoa.put("identificacao", Long.valueOf(getStringSemCaracteres(this.parcelaLiquidacao.getContrato().getPagador().getCpf())));				
		} else {
			jsonPessoa.put("tipo", "PJ");
			jsonPessoa.put("identificacao", Long.valueOf(getStringSemCaracteres(this.parcelaLiquidacao.getContrato().getPagador().getCnpj())));
		}
		jsonPessoa.put("nome", this.parcelaLiquidacao.getContrato().getPagador().getNome());
		jsonSacado.put("pessoa", jsonPessoa);
		
		JSONObject jsonEndereco = new JSONObject();
		jsonEndereco.put("cep", Long.valueOf(getStringSemCaracteres(this.parcelaLiquidacao.getContrato().getPagador().getCep())));
		jsonEndereco.put("logradouro", this.parcelaLiquidacao.getContrato().getPagador().getEndereco());
		jsonEndereco.put("numero", this.parcelaLiquidacao.getContrato().getPagador().getNumero());
		jsonEndereco.put("complemento", this.parcelaLiquidacao.getContrato().getPagador().getComplemento());
		jsonEndereco.put("bairro", this.parcelaLiquidacao.getContrato().getPagador().getBairro());
		jsonEndereco.put("municipio", this.parcelaLiquidacao.getContrato().getPagador().getCidade());
		jsonEndereco.put("uf", this.parcelaLiquidacao.getContrato().getPagador().getEstado());
		jsonSacado.put("endereco", jsonEndereco);

		jsonRecebivel.put("sacado", jsonSacado);
		
		JSONObject jsonCedente = new JSONObject();
		jsonCedente.put("tipo", "PJ");
		
		if (this.parcelaLiquidacao.getContrato().getCedenteBRLCessao().equals("BMP Money Plus SCD S.A.")) {
			jsonCedente.put("identificacao", Long.valueOf("34337707000100"));
			jsonCedente.put("nome", "BMP Money Plus SCD S.A.");		
		} else {
			jsonCedente.put("identificacao", Long.valueOf("34425347000106"));
			jsonCedente.put("nome", "Galleria Finanças Securitizadora S.A.");	
		}
		jsonRecebivel.put("cedente", jsonCedente);
		
		jsonRecebivel.put("aquisicao", simpleDateFormatyyyyMMddComTraco.format(this.parcelaLiquidacao.getContrato().getDataAquisicaoCessao()));
		jsonRecebivel.put("emissao", simpleDateFormatyyyyMMddComTraco.format(this.parcelaLiquidacao.getContrato().getDataInicio()));
		jsonRecebivel.put("vencimento", simpleDateFormatyyyyMMddComTraco.format(this.parcelaLiquidacao.getDataVencimento()));
		jsonRecebivel.put("liquidacao", simpleDateFormatyyyyMMddComTraco.format(this.parcelaLiquidacao.getDataVencimento()));
		JSONObject jsonValores = new JSONObject();
		jsonValores.put("face", this.parcelaLiquidacao.getVlrAmortizacaoParcela().add(this.parcelaLiquidacao.getVlrJurosParcela()));
		
		if (parcelaLiquidacao.getContrato() != null) {
			if (parcelaLiquidacao.getContrato().getTxJurosCessao() != null) {
				jsonValores.put("aquisicao", calcularValorPresenteParcela(parcelaLiquidacao.getId(), parcelaLiquidacao.getContrato().getTxJurosCessao(), parcelaLiquidacao.getContrato().getDataAquisicaoCessao()));
			} else {
				jsonValores.put("aquisicao", calcularValorPresenteParcela(parcelaLiquidacao.getId(), parcelaLiquidacao.getContrato().getTxJurosParcelas(), parcelaLiquidacao.getContrato().getDataAquisicaoCessao()));
			}
		} else {
			jsonValores.put("aquisicao", calcularValorPresenteParcela(parcelaLiquidacao.getId(), parcelaLiquidacao.getContrato().getTxJurosParcelas(), parcelaLiquidacao.getContrato().getDataAquisicaoCessao()));
		}
		
		BigDecimal valorParcelaOriginal = this.parcelaLiquidacao.getVlrJurosParcela().add(this.parcelaLiquidacao.getVlrAmortizacaoParcela());
		
		if (this.parcelaLiquidacao.getDataPagamento().before(this.parcelaLiquidacao.getDataVencimento()) &&
				this.parcelaLiquidacao.getVlrRecebido().compareTo(valorParcelaOriginal) < 0) {
			jsonValores.put("liquidacao", this.parcelaLiquidacao.getVlrRecebido());
		} else {	
			jsonValores.put("liquidacao", valorParcelaOriginal); 
		} 	
		
		jsonRecebivel.put("valores", jsonValores);
		
		JSONObject jsonDados = new JSONObject();
		jsonDados.put("indice", "IPCA");			
		jsonRecebivel.put("dados", jsonDados);		
		
		jsonRecebiveis.put(jsonRecebivel);
		
		jsonCessao.put("recebiveis", jsonRecebiveis);
		
		jsonSchema.put("cessao", jsonCessao);

//		FileOutputStream fileStream;
		try {

			downloadJson(jsonSchema);
			
//			fileStream = new FileOutputStream(new File(this.pathJSON + this.nomeJSON));
//			OutputStreamWriter file;
//			file = new OutputStreamWriter(fileStream, "UTF-8");
//			
//            file.write(jsonSchema.toString());
//            file.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.jsonGerado = true;
		
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Geração JSON BRL Liquidação: JSON gerado com sucesso!",
						""));	
	}
	
	public BigDecimal calcularValorPresenteParcelaData(Date data, ContratoCobrancaDetalhes parcelas, BigDecimal juros){

		BigDecimal valorPresenteParcela = BigDecimal.ZERO;
		
		BigDecimal saldo = BigDecimal.ZERO;
		if (parcelas.getVlrJurosParcela() != null && parcelas.getVlrAmortizacaoParcela() != null) {
			saldo = parcelas.getVlrJurosParcela().add(parcelas.getVlrAmortizacaoParcela());
		} else {
			if (parcelas.getVlrJurosParcela() != null) {
				saldo = parcelas.getVlrJurosParcela();
			} 
			if (parcelas.getVlrAmortizacaoParcela() != null) {
				saldo = parcelas.getVlrAmortizacaoParcela();
			}
		}

		BigDecimal quantidadeDeMeses = BigDecimal.ONE;

		quantidadeDeMeses = BigDecimal.valueOf(DateUtil.Days360(data, parcelas.getDataVencimento()));
		
		quantidadeDeMeses = quantidadeDeMeses.divide(BigDecimal.valueOf(30), MathContext.DECIMAL128);
		
		/*
		if(quantidadeDeMeses.compareTo(BigDecimal.ZERO) == -1) { 
			quantidadeDeMeses = quantidadeDeMeses.multiply(BigDecimal.valueOf(-1)); 
		} 
		*/

		Double quantidadeDeMesesDouble = CommonsUtil.doubleValue(quantidadeDeMeses);
		
		juros = juros.divide(BigDecimal.valueOf(100));
		juros = juros.add(BigDecimal.ONE);
		
		double divisor = Math.pow(CommonsUtil.doubleValue(juros), quantidadeDeMesesDouble);
	
		valorPresenteParcela = (saldo).divide(CommonsUtil.bigDecimalValue(divisor) , MathContext.DECIMAL128);
		valorPresenteParcela = valorPresenteParcela.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		return valorPresenteParcela;
	}
	
	public BigDecimal calcularValorPresenteParcela(Long idParcela, BigDecimal txJuros, Date dataAquisicao){
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		//Date auxDataHoje = dataHoje.getTime();
		Date auxDataHoje = dataAquisicao;
		BigDecimal valorPresenteParcela;
		
		ContratoCobrancaDetalhesDao cDao = new ContratoCobrancaDetalhesDao();		
		ContratoCobrancaDetalhes parcelas = cDao.findById(idParcela);
		
		BigDecimal juros = txJuros;
		BigDecimal saldo = BigDecimal.ZERO;
		
		if (parcelas.getValorJurosSemIPCA() != null && parcelas.getValorAmortizacaoSemIPCA() != null) {
			saldo = parcelas.getValorJurosSemIPCA().add(parcelas.getValorAmortizacaoSemIPCA());
		}
		
		BigDecimal quantidadeDeMeses = BigDecimal.ONE;

		quantidadeDeMeses = BigDecimal.valueOf(DateUtil.Days360(auxDataHoje, parcelas.getDataVencimento()));
		
		quantidadeDeMeses = quantidadeDeMeses.divide(BigDecimal.valueOf(30), MathContext.DECIMAL128);
			
		if(quantidadeDeMeses.compareTo(BigDecimal.ZERO) == -1) { 
			quantidadeDeMeses = quantidadeDeMeses.multiply(BigDecimal.valueOf(-1)); 
		} 

		Double quantidadeDeMesesDouble = CommonsUtil.doubleValue(quantidadeDeMeses); 
		
		juros = juros.divide(BigDecimal.valueOf(100));
		juros = juros.add(BigDecimal.ONE);
		
		double divisor = Math.pow(CommonsUtil.doubleValue(juros), quantidadeDeMesesDouble);
	
		valorPresenteParcela = (saldo).divide(CommonsUtil.bigDecimalValue(divisor) , MathContext.DECIMAL128);
		valorPresenteParcela = valorPresenteParcela.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		return valorPresenteParcela;
	}
	
	public BigDecimal calcularValorPresenteParcelaComIPCA(Long idParcela, BigDecimal txJuros, Date dataAquisicao){
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		//Date auxDataHoje = dataHoje.getTime();
		Date auxDataHoje = dataAquisicao;
		BigDecimal valorPresenteParcela;
		
		ContratoCobrancaDetalhesDao cDao = new ContratoCobrancaDetalhesDao();		
		ContratoCobrancaDetalhes parcelas = cDao.findById(idParcela);
		
		BigDecimal juros = txJuros;
		BigDecimal saldo = BigDecimal.ZERO;
		
		if (parcelas.getVlrJurosParcela() != null && parcelas.getVlrAmortizacaoParcela() != null) {
			saldo = parcelas.getVlrJurosParcela().add(parcelas.getVlrAmortizacaoParcela());
		}
		
		BigDecimal quantidadeDeMeses = BigDecimal.ONE;

		quantidadeDeMeses = BigDecimal.valueOf(DateUtil.Days360(auxDataHoje, parcelas.getDataVencimento()));
		
		quantidadeDeMeses = quantidadeDeMeses.divide(BigDecimal.valueOf(30), MathContext.DECIMAL128);
			
		if(quantidadeDeMeses.compareTo(BigDecimal.ZERO) == -1) { 
			quantidadeDeMeses = quantidadeDeMeses.multiply(BigDecimal.valueOf(-1)); 
		} 

		Double quantidadeDeMesesDouble = CommonsUtil.doubleValue(quantidadeDeMeses); 
		
		juros = juros.divide(BigDecimal.valueOf(100));
		juros = juros.add(BigDecimal.ONE);
		
		double divisor = Math.pow(CommonsUtil.doubleValue(juros), quantidadeDeMesesDouble);
	
		valorPresenteParcela = (saldo).divide(CommonsUtil.bigDecimalValue(divisor) , MathContext.DECIMAL128);
		valorPresenteParcela = valorPresenteParcela.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		return valorPresenteParcela;
	}
	
	public BigDecimal calcularValorPresenteParcelaComIPCAGambiarra(Long idParcela, BigDecimal txJuros, Date dataAquisicao){
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		//Date auxDataHoje = dataHoje.getTime();
		Date auxDataHoje = dataAquisicao;
		BigDecimal valorPresenteParcela;
		
		ContratoCobrancaDetalhesDao cDao = new ContratoCobrancaDetalhesDao();		
		ContratoCobrancaDetalhes parcelas = cDao.findById(idParcela);
		
		BigDecimal juros = txJuros;
		BigDecimal saldo = BigDecimal.ZERO;
		
		if (parcelas.getVlrJurosParcela() != null && parcelas.getVlrAmortizacaoParcela() != null) {
			saldo = parcelas.getVlrJurosParcela().add(parcelas.getVlrAmortizacaoParcela());
		}
		
		BigDecimal quantidadeDeMeses = BigDecimal.ONE;

		quantidadeDeMeses = BigDecimal.valueOf(DateUtil.Days360(auxDataHoje, parcelas.getDataVencimento()));
		
		quantidadeDeMeses = quantidadeDeMeses.divide(BigDecimal.valueOf(30), MathContext.DECIMAL128);
			
		if(quantidadeDeMeses.compareTo(BigDecimal.ZERO) == -1) { 
			quantidadeDeMeses = quantidadeDeMeses.multiply(BigDecimal.valueOf(-1)); 
		} 

		Double quantidadeDeMesesDouble = CommonsUtil.doubleValue(quantidadeDeMeses); 
		
		juros = juros.divide(BigDecimal.valueOf(100));
		juros = juros.add(BigDecimal.ONE);
		
		double divisor = Math.pow(CommonsUtil.doubleValue(juros), quantidadeDeMesesDouble);
	
		valorPresenteParcela = (saldo).divide(CommonsUtil.bigDecimalValue(divisor) , MathContext.DECIMAL128);
		valorPresenteParcela = valorPresenteParcela.subtract(valorPresenteParcela.multiply(this.taxaDesconto.divide(BigDecimal.valueOf(100), MathContext.DECIMAL128)));
		valorPresenteParcela = valorPresenteParcela.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		return valorPresenteParcela;
	}
	
	public BigDecimal calcularValorPresenteParcelaDiaUtilComIPCA(Long idParcela, BigDecimal txJuros, Date dataAquisicao){
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		//Date auxDataHoje = dataHoje.getTime();
		Date auxDataHoje = dataAquisicao;
		BigDecimal valorPresenteParcela;
		
		
		ContratoCobrancaDetalhesDao cDao = new ContratoCobrancaDetalhesDao();		
		ContratoCobrancaDetalhes parcelas = cDao.findById(idParcela);
		
		BigDecimal juros = txJuros;
		
		BigDecimal jurosAoAno = BigDecimal.ZERO;
		jurosAoAno = BigDecimal.ONE.add((txJuros.divide(BigDecimal.valueOf(100), MathContext.DECIMAL128)));
		jurosAoAno = CommonsUtil.bigDecimalValue(Math.pow(CommonsUtil.doubleValue(jurosAoAno), 12));
		jurosAoAno = jurosAoAno.subtract(BigDecimal.ONE);
		jurosAoAno = jurosAoAno.multiply(BigDecimal.valueOf(100), MathContext.DECIMAL128);
		juros = jurosAoAno;
		
		BigDecimal saldo = BigDecimal.ZERO;
		
		if (parcelas.getVlrJurosParcela() != null && parcelas.getVlrAmortizacaoParcela() != null) {
			saldo = parcelas.getVlrJurosParcela().add(parcelas.getVlrAmortizacaoParcela());
		}
		
		if(CommonsUtil.mesmoValor(parcelas.getNumeroParcela(), "12")) {
			dataHoje = null;
		}
		
		BigDecimal quantidadeDeMeses = BigDecimal.ONE;

		quantidadeDeMeses = BigDecimal.valueOf(DateUtil.getWorkingDaysBetweenTwoDates(auxDataHoje, parcelas.getDataVencimento()));
			
		if(quantidadeDeMeses.compareTo(BigDecimal.ZERO) == -1) { 
			quantidadeDeMeses = quantidadeDeMeses.multiply(BigDecimal.valueOf(-1)); 
		} 
		
		//quantidadeDeMeses = BigDecimal.valueOf(21);
		
		quantidadeDeMeses = quantidadeDeMeses.divide(BigDecimal.valueOf(252), MathContext.DECIMAL128);

		Double quantidadeDeMesesDouble = CommonsUtil.doubleValue(quantidadeDeMeses); 
		
		juros = juros.divide(BigDecimal.valueOf(100));
		juros = juros.add(BigDecimal.ONE);
	
		double divisor = Math.pow(juros.doubleValue(), (quantidadeDeMesesDouble)) ;
		
		valorPresenteParcela = (saldo).divide(CommonsUtil.bigDecimalValue(divisor) , MathContext.DECIMAL128);
		valorPresenteParcela = valorPresenteParcela.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		return valorPresenteParcela;
	}

	public String getStringSemCaracteres(String documento) {
		String retorno = "";
		
		retorno = documento.replace(".", "").replace("/", "").replace("-", "");
				
		return retorno;
	}
	
	public Date gerarDataOntem() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		dataHoje.add(Calendar.DATE, -1);
		
		return dataHoje.getTime();
	}

	public void handleFileUpload(FileUploadEvent event) {
		uploadedFile = event.getFile();
	}
	
	public void clearDialog() {
		this.uploadedFile = null;
	}
	
	public void selecionarContratosPorXLS() throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook((uploadedFile.getInputstream()));
		XSSFSheet sheet = wb.getSheetAt(0);

		int iLinha = 0;
		XSSFRow linha = sheet.getRow(iLinha);
		List<String> contratosNaoEncontrados = new ArrayList<String>();
		while (!CommonsUtil.semValor(linha)) {
			linha = sheet.getRow(iLinha);
			if (CommonsUtil.semValor(linha) || CommonsUtil.semValor(linha.getCell(0))) {
				break;
			}
			String numeroContrato = "";
			try {
				if (!CommonsUtil.semValor(linha.getCell(0).getNumericCellValue())) {
					numeroContrato = CommonsUtil.stringValue(linha.getCell(0).getNumericCellValue());
					if(numeroContrato.contains("\\."));
						numeroContrato = numeroContrato.split("\\.")[0];
				} else if (!CommonsUtil.semValor(linha.getCell(0).getStringCellValue())) {
					numeroContrato = CommonsUtil.stringValue(linha.getCell(0).getStringCellValue());
				}
			} catch (Exception e) {
				if (!CommonsUtil.semValor(linha.getCell(0).getStringCellValue())) {
					numeroContrato = CommonsUtil.stringValue(linha.getCell(0).getStringCellValue());
				}
			}
			if(!CommonsUtil.eSomenteNumero(numeroContrato)) {
				iLinha++;
				continue;
			}
			if (numeroContrato.length() == 4) {
				numeroContrato = "0" + numeroContrato;
			} 
			final String numeroFinal = numeroContrato;
			ContratoCobranca contratoSelecionado = contratos.stream()
					.filter(d -> CommonsUtil.mesmoValor(d.getNumeroContrato(), numeroFinal))
					.findFirst().orElse(null);
			if(!CommonsUtil.semValor(contratoSelecionado)) {
				selectedContratos.add(contratoSelecionado);
			} else {
				contratosNaoEncontrados.add(numeroFinal);
			}
			iLinha++;
		}
		this.somatoriaValorePresenteContratos = calculaValorPresenteTotalContrato();
		if(contratosNaoEncontrados.size() > 0) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contratos Não Encontrados: " + contratosNaoEncontrados.toString(), ""));
		}
	}
	
	public boolean isJsonGerado() {
		return jsonGerado;
	}

	public void setJsonGerado(boolean jsonGerado) {
		this.jsonGerado = jsonGerado;
	}

	public String getPathJSON() {
		return pathJSON;
	}

	public void setPathJSON(String pathJSON) {
		this.pathJSON = pathJSON;
	}

	public String getNomeJSON() {
		return nomeJSON;
	}

	public void setNomeJSON(String nomeJSON) {
		this.nomeJSON = nomeJSON;
	}

	public StreamedContent getFile() {
		String caminho =  this.pathJSON + this.nomeJSON;        
		String arquivo = this.nomeJSON;
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

	public String getNumContrato() {
		return numContrato;
	}

	public void setNumContrato(String numContrato) {
		this.numContrato = numContrato;
	}

	public String getCedenteCessao() {
		return cedenteCessao;
	}

	public void setCedenteCessao(String cedenteCessao) {
		this.cedenteCessao = cedenteCessao;
	}

	public Date getDataAquisicao() {
		return dataAquisicao;
	}

	public void setDataAquisicao(Date dataAquisicao) {
		this.dataAquisicao = dataAquisicao;
	}

	public List<ContratoCobranca> getContratos() {
		return contratos;
	}

	public void setContratos(List<ContratoCobranca> contratos) {
		this.contratos = contratos;
	}

	public ContratoCobranca getObjetoContratoCobranca() {
		return objetoContratoCobranca;
	}

	public void setObjetoContratoCobranca(ContratoCobranca objetoContratoCobranca) {
		this.objetoContratoCobranca = objetoContratoCobranca;
	}

	public List<ContratoCobrancaBRLLiquidacao> getParcelasLiquidacao() {
		return parcelasLiquidacao;
	}

	public void setParcelasLiquidacao(List<ContratoCobrancaBRLLiquidacao> parcelasLiquidacao) {
		this.parcelasLiquidacao = parcelasLiquidacao;
	}

	public Date getDataBaixaInicial() {
		return dataBaixaInicial;
	}

	public void setDataBaixaInicial(Date dataBaixaInicial) {
		this.dataBaixaInicial = dataBaixaInicial;
	}

	public Date getDataBaixaFinal() {
		return dataBaixaFinal;
	}

	public void setDataBaixaFinal(Date dataBaixaFinal) {
		this.dataBaixaFinal = dataBaixaFinal;
	}

	public ContratoCobrancaBRLLiquidacao getParcelaLiquidacao() {
		return parcelaLiquidacao;
	}

	public void setParcelaLiquidacao(ContratoCobrancaBRLLiquidacao parcelaLiquidacao) {
		this.parcelaLiquidacao = parcelaLiquidacao;
	}

	public boolean isUsaTaxaJurosDiferenciada() {
		return usaTaxaJurosDiferenciada;
	}

	public void setUsaTaxaJurosDiferenciada(boolean usaTaxaJurosDiferenciada) {
		this.usaTaxaJurosDiferenciada = usaTaxaJurosDiferenciada;
	}

	public BigDecimal getTxJurosCessao() {
		return txJurosCessao;
	}

	public void setTxJurosCessao(BigDecimal txJurosCessao) {
		this.txJurosCessao = txJurosCessao;
	}

	public BigDecimal getValorTotalFaceCessao() {
		return valorTotalFaceCessao;
	}

	public void setValorTotalFaceCessao(BigDecimal valorTotalFaceCessao) {
		this.valorTotalFaceCessao = valorTotalFaceCessao;
	}

	public BigDecimal getValorTotalAquisicaoCessao() {
		return valorTotalAquisicaoCessao;
	}

	public void setValorTotalAquisicaoCessao(BigDecimal valorTotalAquisicaoCessao) {
		this.valorTotalAquisicaoCessao = valorTotalAquisicaoCessao;
	}

	public List<ContratoCobranca> getSelectedContratos() {
		return selectedContratos;
	}

	public void setSelectedContratos(List<ContratoCobranca> selectedContratos) {
		this.selectedContratos = selectedContratos;
	}

	public BigDecimal getSomatoriaValorePresenteContratos() {
		return somatoriaValorePresenteContratos;
	}

	public void setSomatoriaValorePresenteContratos(BigDecimal somatoriaValorePresenteContratos) {
		this.somatoriaValorePresenteContratos = somatoriaValorePresenteContratos;
	}

	public String getPathXLS() {
		return pathXLS;
	}

	public void setPathXLS(String pathXLS) {
		this.pathXLS = pathXLS;
	}

	public String getNomeXLS() {
		return nomeXLS;
	}

	public void setNomeXLS(String nomeXLS) {
		this.nomeXLS = nomeXLS;
	}

	public boolean isXlsGerado() {
		return xlsGerado;
	}

	public void setXlsGerado(boolean xlsGerado) {
		this.xlsGerado = xlsGerado;
	}

	public StreamedContent getXlsFile() {
		String caminho =  this.pathXLS + this.nomeXLS;        
		String arquivo = this.nomeXLS;
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(caminho);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
		xlsFile = new DefaultStreamedContent(stream, caminho, arquivo); 

		return xlsFile;  
	}

	public void setXlsFile(StreamedContent xlsFile) {
		this.xlsFile = xlsFile;
	}

	public List<ContratoCobranca> getSelectedContratosXLS() {
		return selectedContratosXLS;
	}

	public void setSelectedContratosXLS(List<ContratoCobranca> selectedContratosXLS) {
		this.selectedContratosXLS = selectedContratosXLS;
	}

	public Date getDataValorPresente() {
		return dataValorPresente;
	}

	public void setDataValorPresente(Date dataValorPresente) {
		this.dataValorPresente = dataValorPresente;
	}

	public BigDecimal getTaxaDesconto() {
		return taxaDesconto;
	}

	public void setTaxaDesconto(BigDecimal taxaDesconto) {
		this.taxaDesconto = taxaDesconto;
	}

	public List<ContratoCobrancaBRLLiquidacao> getSelectedJsonLiquidacao() {
		return selectedJsonLiquidacao;
	}

	public void setSelectedJsonLiquidacao(List<ContratoCobrancaBRLLiquidacao> selectedJsonLiquidacao) {
		this.selectedJsonLiquidacao = selectedJsonLiquidacao;
	}

	public BigDecimal getValorTotalLiquidacao() {
		return valorTotalLiquidacao;
	}

	public void setValorTotalLiquidacao(BigDecimal valorTotalLiquidacao) {
		this.valorTotalLiquidacao = valorTotalLiquidacao;
	}

	public int getQtdeLiquidados() {
		return qtdeLiquidados;
	}

	public void setQtdeLiquidados(int qtdeLiquidados) {
		this.qtdeLiquidados = qtdeLiquidados;
	}

	public BigDecimal getValorTotalRecebidoLiquidacao() {
		return valorTotalRecebidoLiquidacao;
	}

	public void setValorTotalRecebidoLiquidacao(BigDecimal valorTotalRecebidoLiquidacao) {
		this.valorTotalRecebidoLiquidacao = valorTotalRecebidoLiquidacao;
	}

	public BigDecimal getValorTotalJurosAmortizacaoLiquidacao() {
		return valorTotalJurosAmortizacaoLiquidacao;
	}

	public void setValorTotalJurosAmortizacaoLiquidacao(BigDecimal valorTotalJurosAmortizacaoLiquidacao) {
		this.valorTotalJurosAmortizacaoLiquidacao = valorTotalJurosAmortizacaoLiquidacao;
	}

	public int getQtdSelecionadoLiquidacao() {
		return qtdSelecionadoLiquidacao;
	}

	public void setQtdSelecionadoLiquidacao(int qtdSelecionadoLiquidacao) {
		this.qtdSelecionadoLiquidacao = qtdSelecionadoLiquidacao;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

}