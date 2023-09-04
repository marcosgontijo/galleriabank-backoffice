package com.webnowbr.siscoat.cobranca.mb;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.webnowbr.siscoat.cobranca.auxiliar.NumeroPorExtenso;
import com.webnowbr.siscoat.cobranca.auxiliar.ValorPorExtenso;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaParcelasInvestidor;
import com.webnowbr.siscoat.cobranca.db.model.DebenturesInvestidor;
import com.webnowbr.siscoat.cobranca.db.model.InvestidorInformeRendimentos;
import com.webnowbr.siscoat.cobranca.db.model.OperacoesIndividualizado;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaParcelasInvestidorDao;
import com.webnowbr.siscoat.cobranca.db.op.DebenturesInvestidorDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.irpf.vo.IrpfContrato;
import com.webnowbr.siscoat.cobranca.vo.ContratoCobrancaResumoVO;
import com.webnowbr.siscoat.cobranca.vo.DashboardInvestidorResumoVO;
import com.webnowbr.siscoat.cobranca.vo.ExtratoVO;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.common.Util;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

@ManagedBean(name = "investidorMB")
@SessionScoped
public class InvestidorMB {

	private int qtdeContratosTotal;
//	private int parcelasAbertasTotal;
//	private BigDecimal valorReceberTotal;
//	private BigDecimal valorRecebidoTotal;
//	private BigDecimal valorInvestidoTotal;

	private List<DashboardInvestidorResumoVO> investidorResumos;
	private String situacaoSelecionada = null;
	private boolean existeContratoAtivo = false;
	private boolean existeContratoQuitado = false;

	private List<DashboardInvestidorResumoVO> investidorResumosTela;
	private List<ContratoCobranca> contratos;
	private List<ContratoCobrancaResumoVO> contratosVO;
	private List<ContratoCobrancaResumoVO> contratosVOTela;
	private List<ExtratoVO> extrato;
	private List<Long> idContratosQuitado;
	private ContratoCobranca selectedContrato;
	private List<ContratoCobrancaParcelasInvestidor> listContratoCobrancaParcelasInvestidorSelecionado;
	private ContratoCobrancaDetalhes selectedContratoCobrancaDetalhes;

	private long idInvestidor;

//	private BigDecimal valorInvestidor;

	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;

	private User usuarioLogado;

	Collection<FileUploaded> files = new ArrayList<FileUploaded>();
	FileUploaded selectedFile = new FileUploaded();
	List<FileUploaded> deletefiles = new ArrayList<FileUploaded>();
	StreamedContent downloadFile;
	StreamedContent downloadAllFiles;

	/***
	 * INICIO ATRIBUTOS RECIBO
	 */
	private boolean debenturePDFGerado;
	private boolean termoSecuritizacaoPDFGerado;
	private boolean anexo1PDFGerado;
	private boolean bsPDFGerado;
	private boolean valoresLiquidosInvestidoresPDFGerado;
	private boolean irRetidoInvestidoresPDFGerado;
	private String pathPDF;
	private String nomePDF;
	private String termoSecuritizacaoPDFStr;
	private String anexoPDFStr;
	private String bsPDFStr;
	private StreamedContent termoSecuritizacaoPDF;
	private StreamedContent anexoPDF;
	private StreamedContent bsPDF;
	private StreamedContent file;

	private boolean titulosQuitadosPDFGerado;
	private String titulosQuitadosPDFStr;
	private StreamedContent titulosQuitadosPDF;

	private boolean operacoesIndividualizadoPDFGerado;
	private String operacoesIndividualizadoPDFStr;
	private StreamedContent operacoesIndividualizadoPDF;

	private BigDecimal totalAnexoI;
	private BigDecimal valorPorcentagemAnexoI;

	private boolean filtraContrato;
	private String filtroNumeroContrato;

	private int posicaoInvestidorNoContrato;

	private List<DebenturesInvestidor> listDebenturesInvestidor;
	private DebenturesInvestidor debenturesInvestidor;

	private boolean updateMode;
	private boolean deleteMode;

	private BigDecimal totalValorFace;

	private List<OperacoesIndividualizado> listOperacoesIndividualizado;
	private BigDecimal totalBruto;
	private BigDecimal totalLiquido;
	private BigDecimal totalDesagio;
	private long prazoMedioTotal;

	/***
	 * FIM ATRIBUTOS RECIBO
	 */

	/** Objeto selecionado na LoV - Pagador. */
	private PagadorRecebedor selectedPagador;

	/** Lista dos Pagadores utilizada pela LOV. */
	private List<PagadorRecebedor> listPagadores;

	/** Nome do Pagador selecionado pela LoV. */
	private String nomePagador;

	/** Id Objeto selecionado na LoV - Pagador. */
	private long idPagador;

	/** Objeto selecionado na LoV - Contrato. */
	private ContratoCobranca selectedContratoLov;

	/** Lista dos Contratos utilizada pela LOV. */
	private List<ContratoCobranca> listContratos;

	/** Nome do Contrato selecionado pela LoV. */
	private String numeroContrato;

	/** Id Objeto selecionado na LoV - Pagador. */
	private long idContrato;

	private Date dataInicio;
	private Date dataFim;

	BigDecimal totalIRRetido;
	BigDecimal totalJurosLiquido;
	BigDecimal valorInvestidorAnoAtual;
	BigDecimal valorInvestidorAnoAnterior;
	String anoBase;

	List<ContratoCobrancaParcelasInvestidor> parcelasInvestidor;
	List<ContratoCobrancaParcelasInvestidor> parcelasInvestidorCorrespondente;
	List<ContratoCobrancaParcelasInvestidor> parcelasInvestidorSA;
	List<ContratoCobrancaParcelasInvestidor> parcelasInvestidorEnvelope;

	List<InvestidorInformeRendimentos> investidorInformeRendimentos;

	String labelAnoBase = "";
	String labelAnoAnterior = "";

	private boolean imprimirHeaderFooter;

	private boolean debenturesEmitidasXLSGerado;
	private String pathContrato;
	private String nomeContrato;
	private StreamedContent fileXLS;

	private DualListModel<PagadorRecebedor> dualListModelRecebedores;
	private List<PagadorRecebedor> listRecebedoresSeleciodados;
	private List<PagadorRecebedor> listRecebedores;

	private String filtroDebenturesTipoDocumento;
	private String filtroDebenturesDocumento;
	private String filtroDebenturesStatus;

	private String filtroDebenturesPorValor = "Todos";
	private String filtroDebenturesTipoData = "Todos";

	private BigDecimal filtroValorFaceInicial = null;
	private BigDecimal filtroValorFaceFinal = null;

	private String filtroDebenturesTipoFiltro;

	public InvestidorMB() {

	}

	public String clearFieldsInformeRendimentosOLD() {
		this.totalIRRetido = BigDecimal.ZERO;
		this.totalJurosLiquido = BigDecimal.ZERO;
		this.valorInvestidorAnoAtual = BigDecimal.ZERO;
		this.valorInvestidorAnoAnterior = BigDecimal.ZERO;

		this.dataInicio = gerarDataHoje();
		this.dataFim = gerarDataHoje();

		this.parcelasInvestidor = new ArrayList<ContratoCobrancaParcelasInvestidor>();

		PagadorRecebedorDao prDao = new PagadorRecebedorDao();
		this.listPagadores = prDao.findAll();
		clearPagador();

		this.irRetidoInvestidoresPDFGerado = false;
		this.pathPDF = "";
		this.nomePDF = "";
		this.file = null;

		this.anoBase = "";

		return "/Atendimento/Cobranca/InformeRendimentos.xhtml";
	}

	public String clearFieldsInformeRendimentos() {
		this.dataInicio = gerarDataHoje();
		this.dataFim = gerarDataHoje();

		this.valorInvestidorAnoAtual = BigDecimal.ZERO;
		this.valorInvestidorAnoAnterior = BigDecimal.ZERO;

		this.parcelasInvestidor = new ArrayList<ContratoCobrancaParcelasInvestidor>();

		this.investidorInformeRendimentos = new ArrayList<InvestidorInformeRendimentos>();

		PagadorRecebedorDao prDao = new PagadorRecebedorDao();
		this.listPagadores = prDao.findAll();
		clearPagador();

		this.irRetidoInvestidoresPDFGerado = false;
		this.pathPDF = "";
		this.nomePDF = "";
		this.file = null;

		this.anoBase = "";

		this.labelAnoBase = "Valor em";
		this.labelAnoAnterior = "Valor em";

		this.posicaoInvestidorNoContrato = 0;

		this.imprimirHeaderFooter = true;

		return "/Atendimento/Cobranca/InformeRendimentos.xhtml";
	}

	public StreamedContent processInformeRendimentosPortalInvestidor(String anoBaseParametro) {
		this.anoBase = anoBaseParametro;
		processInformeRendimentos();

		this.imprimirHeaderFooter = true;

		if (this.investidorInformeRendimentos.size() > 0) {
			return geraPDFInformeRendimentos();
		}
		return null;
	}

	public void processInformeRendimentos() {
		ContratoCobrancaParcelasInvestidorDao cDao = new ContratoCobrancaParcelasInvestidorDao();
		FacesContext context = FacesContext.getCurrentInstance();

		this.investidorInformeRendimentos = new ArrayList<InvestidorInformeRendimentos>();
		this.posicaoInvestidorNoContrato = 0;

		if (this.selectedPagador == null || this.selectedPagador.getId() <= 0) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Informe de rendimentos: É obrigatória a seleção do investidor!", ""));
		} else {
			DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

			Date dataInicioAnterior = null;
			Date dataFimAnterior = null;

			try {
				if (this.anoBase.equals("2019")) {
					this.dataInicio = format.parse("31/12/2018");
					this.dataFim = format.parse("31/12/2019");

					dataInicioAnterior = format.parse("31/12/2017");
					dataFimAnterior = format.parse("31/12/2018");

					this.labelAnoBase = "Valor em 31/12/2019";
					this.labelAnoAnterior = "Valor em 31/12/2018";
				}

				if (this.anoBase.equals("2020")) {
					this.dataInicio = format.parse("31/12/2019");
					this.dataFim = format.parse("31/12/2020");

					dataInicioAnterior = format.parse("31/12/2018");
					dataFimAnterior = format.parse("31/12/2019");

					this.labelAnoBase = "Valor em 31/12/2020";
					this.labelAnoAnterior = "Valor em 31/12/2019";
				}

				if (this.anoBase.equals("2021")) {
					this.dataInicio = format.parse("31/12/2020");
					this.dataFim = format.parse("31/12/2021");

					dataInicioAnterior = format.parse("31/12/2019");
					dataFimAnterior = format.parse("31/12/2020");

					this.labelAnoBase = "Valor em 31/12/2021";
					this.labelAnoAnterior = "Valor em 31/12/2020";
				}

				if (this.anoBase.equals("2022")) {
					this.dataInicio = format.parse("31/12/2021");
					this.dataFim = format.parse("31/12/2022");

					dataInicioAnterior = format.parse("31/12/2020");
					dataFimAnterior = format.parse("31/12/2021");

					this.labelAnoBase = "Valor em 31/12/2022";
					this.labelAnoAnterior = "Valor em 31/12/2021";
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/****
			 * BUSCA O SALDO DA ÚLTIMA PARCELA PAGA, IR E JUROS (ano base atual)
			 */
			this.parcelasInvestidor = cDao.getParcelasPorDataInvestidorBaixadasInforme(this.dataInicio, this.dataFim,
					this.selectedPagador.getId());

			InvestidorInformeRendimentos informeRendimentos = new InvestidorInformeRendimentos();
			String numeroContrato = null;
			String numeroParcela = "0";
			BigDecimal irRetidoTotalContrato = BigDecimal.ZERO;
			BigDecimal jurosTotalContrato = BigDecimal.ZERO;
			BigDecimal saldoTotalContrato = BigDecimal.ZERO;

			ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
			List<String> contratos = this.parcelasInvestidor.stream().map(p -> p.getNumeroContrato()).distinct()
					.collect(Collectors.toList());

			for (String numeroContratoIRPF : contratos) {
				List<ContratoCobrancaParcelasInvestidor> parcelasContrato = this.parcelasInvestidor.stream()
						.filter(p -> p.getNumeroContrato().equals(numeroContratoIRPF))
						.sorted((e1, e2) -> e1.getDataBaixa().compareTo(e2.getDataBaixa()))
						.collect(Collectors.toList());

				ContratoCobrancaParcelasInvestidor utltimaParcela = parcelasContrato.stream()
						.reduce((first, second) -> second).orElse(null);

				ContratoCobranca contrato = contratoCobrancaDao.findById(utltimaParcela.getIdContrato());

				IrpfContrato irpfContrato = BuscaDadosIRPF(contrato, utltimaParcela);

				boolean parcelaFimContrato = false;
				if (irpfContrato != null && irpfContrato.getQtdeParcelasInvestidor() != null
						&& irpfContrato.getCarenciaInvestidor() != null)
					parcelaFimContrato = (irpfContrato.getQtdeParcelasInvestidor()
							- irpfContrato.getCarenciaInvestidor()) == 1;

				irRetidoTotalContrato = parcelasContrato.stream().map(p -> CommonsUtil.bigDecimalValue(p.getIrRetido()))
						.reduce(BigDecimal.ZERO, BigDecimal::add);
				jurosTotalContrato = parcelasContrato.stream().map(p -> (
						  BigDecimal.ZERO.compareTo(CommonsUtil.bigDecimalValue(p.getJuros()))== 0 &&
						  BigDecimal.ZERO.compareTo(CommonsUtil.bigDecimalValue(p.getCapitalizacao())) == -1)?p.getCapitalizacao():p.getJuros()   )
						.reduce(BigDecimal.ZERO, BigDecimal::add);

				// pegar sempre o saldo da última parcela baixada
				if (!parcelaFimContrato
						|| (parcelaFimContrato && (Integer.valueOf(utltimaParcela.getNumeroParcela()) == irpfContrato
								.getQtdeParcelasInvestidor())
						|| CommonsUtil.mesmoValor(BigDecimal.ZERO, utltimaParcela.getSaldoCredorAtualizado())
						|| !CommonsUtil.mesmoValor(BigDecimal.ZERO, utltimaParcela.getParcelaMensal())))
					saldoTotalContrato = utltimaParcela.getSaldoCredorAtualizado();
				else
					saldoTotalContrato = irpfContrato.getVlrInvestidor();

				// Armazena dados do contrato anterior
				informeRendimentos = new InvestidorInformeRendimentos();
				informeRendimentos.setNumeroContrato(numeroContratoIRPF);
				informeRendimentos.setIrRetido(irRetidoTotalContrato);
				informeRendimentos.setJuros(jurosTotalContrato);
				informeRendimentos.setSaldoAnoAtual(saldoTotalContrato);
				informeRendimentos.setIndice(this.investidorInformeRendimentos.size() + 1);

				informeRendimentos.setSaldoAnoAnterior(getSaldoInvestidorAnoAnterior(dataInicioAnterior,
						dataFimAnterior, cDao, contratoCobrancaDao, numeroContratoIRPF));
				informeRendimentos.setEmpresa("Galleria Finanças Securitizadora S.A");
				informeRendimentos.setCnpj("34.425.347/0001-06");

				this.investidorInformeRendimentos.add(informeRendimentos);

				numeroParcela = utltimaParcela.getNumeroParcela();
				//numeroParcela = Integer.valueOf(utltimaParcela.getNumeroParcela());

				// for (ContratoCobrancaParcelasInvestidor parcela : parcelasContrato) {
				//
				// ContratoCobranca contrato =
				// contratoCobrancaDao.findById(parcela.getIdContrato());
				//
				// IrpfContrato irpfContrato = BuscaDadosIRPF(contrato, parcela);
				//
				// boolean parcelaFimContrato = false;
				// if (irpfContrato != null)
				// parcelaFimContrato = (irpfContrato.getQtdeParcelasInvestidor()
				// - irpfContrato.getCarenciaInvestidor()) == 1;
				//
				// /*******
				// * VERIFICA SE A DATA DE ENTRADA DO INVESTIDOR NO CONTRATO FOI NO ANO BASE
				// */
				// if (verificarAnoBaseInvestidor(this.selectedPagador, contrato,
				// this.dataInicio, this.dataFim)) {
				// // Corrige valores nulos de valores
				// if (parcela.getIrRetido() == null) {
				// parcela.setIrRetido(BigDecimal.ZERO);
				// }
				//
				// if (parcela.getJuros() == null) {
				// parcela.setJuros(BigDecimal.ZERO);
				// }
				//
				// if (parcela.getSaldoCredorAtualizado() == null) {
				// parcela.setSaldoCredorAtualizado(BigDecimal.ZERO);
				// }
				//
				// if (numeroContrato == null) {
				// numeroContrato = parcela.getNumeroContrato();
				// //irRetidoTotalContrato = parcela.getIrRetido();
				// jurosTotalContrato = parcela.getJuros();
				//
				// saldoTotalContrato = parcela.getSaldoCredorAtualizado();
				// numeroParcela = Integer.valueOf(parcela.getNumeroParcela());
				// } else {
				// if (numeroContrato.equals(parcela.getNumeroContrato())) {
				//
				//// irRetidoTotalContrato = irRetidoTotalContrato.add(parcela.getIrRetido());
				// jurosTotalContrato = jurosTotalContrato.add(parcela.getJuros());
				//
				// // pegar sempre o saldo da última parcela baixada
				// if (!parcelaFimContrato
				// && Integer.valueOf(parcela.getNumeroParcela()) > numeroParcela) {
				// numeroParcela = Integer.valueOf(parcela.getNumeroParcela());
				// saldoTotalContrato = parcela.getSaldoCredorAtualizado();
				// }
				// } else {
				// // Armazena dados do contrato anterior
				// informeRendimentos = new InvestidorInformeRendimentos();
				// informeRendimentos.setNumeroContrato(numeroContrato);
				// informeRendimentos.setIrRetido(irRetidoTotalContrato);
				// informeRendimentos.setJuros(jurosTotalContrato);
				// informeRendimentos.setSaldoAnoAtual(saldoTotalContrato);
				// informeRendimentos.setIndice(this.investidorInformeRendimentos.size() + 1);
				//
				// informeRendimentos.setSaldoAnoAnterior(getSaldoInvestidorAnoAnterior(dataInicioAnterior,
				// dataFimAnterior, cDao, contratoCobrancaDao, numeroContrato));
				// informeRendimentos.setEmpresa("Galleria Finanças Securitizadora S.A");
				// informeRendimentos.setCnpj("34.425.347/0001-06");
				//
				// this.investidorInformeRendimentos.add(informeRendimentos);
				//
				// // Coleta dados do contrato atual
				// numeroContrato = parcela.getNumeroContrato();
				// irRetidoTotalContrato = parcela.getIrRetido();
				// jurosTotalContrato = parcela.getJuros();
				// if (!parcelaFimContrato || (parcelaFimContrato
				// && Integer.valueOf(parcela.getNumeroParcela()) == irpfContrato
				// .getQtdeParcelasInvestidor()))
				// saldoTotalContrato = parcela.getSaldoCredorAtualizado();
				// else
				// saldoTotalContrato = irpfContrato.getVlrInvestidor();
				//
				// numeroParcela = Integer.valueOf(parcela.getNumeroParcela());
				// }
				// }
				// }
				// }
				// }
				//
				// if (numeroContrato != null) {
				// // Armazena dados do último contrato
				// informeRendimentos = new InvestidorInformeRendimentos();
				// informeRendimentos.setNumeroContrato(numeroContrato);
				// informeRendimentos.setIrRetido(irRetidoTotalContrato);
				// informeRendimentos.setJuros(jurosTotalContrato);
				// informeRendimentos.setSaldoAnoAtual(saldoTotalContrato);
				// informeRendimentos.setIndice(this.investidorInformeRendimentos.size() + 1);
				// informeRendimentos.setSaldoAnoAnterior(getSaldoInvestidorAnoAnterior(dataInicioAnterior,
				// dataFimAnterior, cDao, contratoCobrancaDao, numeroContrato));
				// informeRendimentos.setEmpresa("Galleria Finanças Securitizadora S.A");
				// informeRendimentos.setCnpj("34.425.347/0001-06");
				//
				// this.investidorInformeRendimentos.add(informeRendimentos);
				// }
			}
				/****
				 * PEGA CONTRATOS GERADOS NO ANO BASE E QUE NÃO TIVERAM BAIXA
				 */
				Date dataInicioContrato = null;
				Date dataFimContrato = null;

				try {
					dataInicioContrato = format.parse("01/01/" + this.anoBase);
					dataFimContrato = format.parse("31/12/" + this.anoBase);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ContratoCobrancaDao contratoDao = new ContratoCobrancaDao();
				List<ContratoCobranca> listContratos = contratoDao.getContratosPorInvestidorInformeRendimentos(
						this.selectedPagador.getId(), dataInicioContrato, dataFimContrato);
				
				final List<String> contratosNoRelatorio =   investidorInformeRendimentos.stream().map(ir -> ir.getNumeroContrato()).collect(Collectors.toList());
										
				List<ContratoCobranca> listContratosFilter =  listContratos.stream().filter(c -> !contratosNoRelatorio.contains( c.getNumeroContrato())).collect(Collectors.toList()); 
				
//				// VERIFICAR SE O CONTRATO JÁ ENCONTRA-SE LISTADO NO ANO BASE
				// SE SIM, É PQ JÁ HOUVE BAIXA
				for (ContratoCobranca contrato2 : listContratosFilter) {
					boolean consideraContrato = true;

//					for (InvestidorInformeRendimentos informe : this.investidorInformeRendimentos) {
//						if (contrato.getNumeroContrato().equals(informe.getNumeroContrato())) {
//							consideraContrato = false;
//						}
//					}

					// SE CONSIDERA O CONTRATO,
//					if (consideraContrato) {
						if (verificarAnoBaseInvestidor(this.selectedPagador, contrato2, this.dataInicio,
								this.dataFim)) {
							informeRendimentos = new InvestidorInformeRendimentos();
							informeRendimentos.setNumeroContrato(contrato2.getNumeroContrato());
							informeRendimentos.setIrRetido(BigDecimal.ZERO);
							informeRendimentos.setJuros(BigDecimal.ZERO);
							informeRendimentos.setSaldoAnoAtual(
									buscaValorDebentureInvestidorNoContrato(contrato2, this.selectedPagador.getId()));
							informeRendimentos.setIndice(this.investidorInformeRendimentos.size() + 1);
							informeRendimentos.setSaldoAnoAnterior(BigDecimal.ZERO);
							informeRendimentos.setEmpresa("Galleria Finanças Securitizadora S.A");
							informeRendimentos.setCnpj("34.425.347/0001-06");

							this.investidorInformeRendimentos.add(informeRendimentos);
						}
//					}
				}

				/****
				 * PEGA CONTRATOS EM ATRASO ANOS ANTERIORES, PEGA A ÚLTIMA BAIXA E USA O SALDO
				 * CREDOR COMO VALOR DOS ANOS ANTERIORES
				 */

				int anoInicioContratos = Integer.valueOf(this.anoBase) - 15;
				int anoFimContratos = Integer.valueOf(this.anoBase) - 1;
				try {
					dataInicioContrato = format.parse("01/01/" + String.valueOf(anoInicioContratos));
					dataFimContrato = format.parse("31/12/" + String.valueOf(anoFimContratos));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				listContratos = new ArrayList<ContratoCobranca>();
				listContratos = contratoDao.getContratosPorInvestidorInformeRendimentos(this.selectedPagador.getId(),
						dataInicioContrato, dataFimContrato);

				final List<String> contratosNoRelatorioNovo = investidorInformeRendimentos.stream()
						.map(ir -> ir.getNumeroContrato()).collect(Collectors.toList());

				listContratosFilter = listContratos
						.stream().filter(c -> !contratosNoRelatorioNovo.contains( c.getNumeroContrato())).collect(Collectors.toList()); 
				

				// VERIFICAR SE O CONTRATO JÁ ENCONTRA-SE LISTADO NO ANO BASE
				// SE SIM, É PQ JÁ HOUVE BAIXA ou Foi criado no ano base
				for (ContratoCobranca contratoAnt : listContratosFilter) {

//					boolean consideraContrato = true;
//
//					for (InvestidorInformeRendimentos informe : this.investidorInformeRendimentos) {
//						if (contratoAnt.getNumeroContrato().equals(informe.getNumeroContrato())) {
//							consideraContrato = false;
//						}
//					}

					// SE CONSIDERA O CONTRATO,
//					if (consideraContrato) {
						BigDecimal saldoContrato = buscaUltimoValorPago(contratoAnt, this.selectedPagador.getId());

						if (saldoContrato.compareTo(BigDecimal.ZERO) > 0) {
							informeRendimentos = new InvestidorInformeRendimentos();
							informeRendimentos.setNumeroContrato(contratoAnt.getNumeroContrato());
							informeRendimentos.setIrRetido(BigDecimal.ZERO);
							informeRendimentos.setJuros(BigDecimal.ZERO);
							informeRendimentos.setSaldoAnoAtual(saldoContrato);
							informeRendimentos.setIndice(this.investidorInformeRendimentos.size() + 1);
							informeRendimentos.setSaldoAnoAnterior(saldoContrato);
							informeRendimentos.setEmpresa("Galleria Finanças Securitizadora S.A");
							informeRendimentos.setCnpj("34.425.347/0001-06");

							this.investidorInformeRendimentos.add(informeRendimentos);
						}
//					}
				}

				// FINALIZA PROCESSO DE CONSTRUÇÃO DO INFORME
				if (this.investidorInformeRendimentos.size() == 0) {
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Investidores: Não há registros para os filtros informados!", ""));
				} else {
					this.totalIRRetido = BigDecimal.ZERO;
					this.totalJurosLiquido = BigDecimal.ZERO;
					this.valorInvestidorAnoAtual = BigDecimal.ZERO;
					this.valorInvestidorAnoAnterior = BigDecimal.ZERO;

					/***
					 * CORRIGIR VALORES NULOS NA LISTA
					 */
					for (InvestidorInformeRendimentos informe : this.investidorInformeRendimentos) {
						if (informe.getIrRetido() == null) {
							informe.setIrRetido(BigDecimal.ZERO);
						}

						if (informe.getJuros() == null) {
							informe.setJuros(BigDecimal.ZERO);
						}

						if (informe.getSaldoAnoAtual() == null) {
							informe.setSaldoAnoAtual(BigDecimal.ZERO);
						}

						if (informe.getSaldoAnoAnterior() == null) {
							informe.setSaldoAnoAnterior(BigDecimal.ZERO);
						}
					}

					/***
					 * FAZ SOMATÓRIA FINAL
					 */
					for (InvestidorInformeRendimentos informe : this.investidorInformeRendimentos) {
						this.totalIRRetido = this.totalIRRetido.add(informe.getIrRetido());
						this.totalJurosLiquido = this.totalJurosLiquido.add(informe.getJuros());
						this.valorInvestidorAnoAtual = this.valorInvestidorAnoAtual.add(informe.getSaldoAnoAtual());
						this.valorInvestidorAnoAnterior = this.valorInvestidorAnoAnterior
								.add(informe.getSaldoAnoAnterior());
					}
				}
				/*
				 * this.valorInvestidorAnoAtual =
				 * cDao.getParcelasPorDataInvestidorIR(this.dataInicio, this.dataFim,
				 * this.selectedPagador.getId());
				 * 
				 * if (dataInicioAnterior != null && dataFimAnterior != null) {
				 * this.valorInvestidorAnoAnterior =
				 * cDao.getParcelasPorDataInvestidorIR(dataInicioAnterior, dataFimAnterior,
				 * this.selectedPagador.getId()); } else { this.valorInvestidorAnoAnterior =
				 * BigDecimal.ZERO; }
				 */

			}
		}
	//}
	
	private IrpfContrato BuscaDadosIRPF(ContratoCobranca contrato, ContratoCobrancaParcelasInvestidor parcela) {
		IrpfContrato irpfContrato = null;
		if (contrato.getRecebedor() != null && contrato.getRecebedor().equals(parcela.getInvestidor())) {
			irpfContrato = new IrpfContrato(contrato.getTipoCalculoInvestidor1(), contrato.getVlrInvestidor1(),
					contrato.getQtdeParcelasInvestidor1(), contrato.getCarenciaInvestidor1(),
					contrato.getDataInicioInvestidor1());
		} else if (contrato.getRecebedor2() != null
				&& contrato.getRecebedor2().equals(parcela.getInvestidor())) {
			irpfContrato = new IrpfContrato(contrato.getTipoCalculoInvestidor2(), contrato.getVlrInvestidor2(),
					contrato.getQtdeParcelasInvestidor2(), contrato.getCarenciaInvestidor2(),
					contrato.getDataInicioInvestidor2());
		} else if (contrato.getRecebedor3() != null
				&& contrato.getRecebedor3().equals(parcela.getInvestidor())) {
			irpfContrato = new IrpfContrato(contrato.getTipoCalculoInvestidor3(), contrato.getVlrInvestidor3(),
					contrato.getQtdeParcelasInvestidor3(), contrato.getCarenciaInvestidor3(),
					contrato.getDataInicioInvestidor3());
		} else if (contrato.getRecebedor4() != null
				&& contrato.getRecebedor4().equals(parcela.getInvestidor())) {
			irpfContrato = new IrpfContrato(contrato.getTipoCalculoInvestidor4(), contrato.getVlrInvestidor4(),
					contrato.getQtdeParcelasInvestidor4(), contrato.getCarenciaInvestidor4(),
					contrato.getDataInicioInvestidor4());
		} else if (contrato.getRecebedor5() != null
				&& contrato.getRecebedor5().equals(parcela.getInvestidor())) {
			irpfContrato = new IrpfContrato(contrato.getTipoCalculoInvestidor5(), contrato.getVlrInvestidor5(),
					contrato.getQtdeParcelasInvestidor5(), contrato.getCarenciaInvestidor5(),
					contrato.getDataInicioInvestidor5());
		} else if (contrato.getRecebedor6() != null
				&& contrato.getRecebedor6().equals(parcela.getInvestidor())) {
			irpfContrato = new IrpfContrato(contrato.getTipoCalculoInvestidor6(), contrato.getVlrInvestidor6(),
					contrato.getQtdeParcelasInvestidor6(), contrato.getCarenciaInvestidor6(),
					contrato.getDataInicioInvestidor6());
		} else if (contrato.getRecebedor7() != null
				&& contrato.getRecebedor7().equals(parcela.getInvestidor())) {
			irpfContrato = new IrpfContrato(contrato.getTipoCalculoInvestidor7(), contrato.getVlrInvestidor7(),
					contrato.getQtdeParcelasInvestidor7(), contrato.getCarenciaInvestidor7(),
					contrato.getDataInicioInvestidor7());
		} else if (contrato.getRecebedor8() != null
				&& contrato.getRecebedor8().equals(parcela.getInvestidor())) {
			irpfContrato = new IrpfContrato(contrato.getTipoCalculoInvestidor8(), contrato.getVlrInvestidor8(),
					contrato.getQtdeParcelasInvestidor8(), contrato.getCarenciaInvestidor8(),
					contrato.getDataInicioInvestidor8());
		} else if (contrato.getRecebedor9() != null
				&& contrato.getRecebedor9().equals(parcela.getInvestidor())) {
			irpfContrato = new IrpfContrato(contrato.getTipoCalculoInvestidor9(), contrato.getVlrInvestidor9(),
					contrato.getQtdeParcelasInvestidor9(), contrato.getCarenciaInvestidor9(),
					contrato.getDataInicioInvestidor9());
		} else if (contrato.getRecebedor10() != null
				&& contrato.getRecebedor10().equals(parcela.getInvestidor())) {
			irpfContrato = new IrpfContrato(contrato.getTipoCalculoInvestidor10(), contrato.getVlrInvestidor10(),
					contrato.getQtdeParcelasInvestidor10(), contrato.getCarenciaInvestidor10(),
					contrato.getDataInicioInvestidor10());
		}

		return irpfContrato;
	}
	

	public boolean verificarAnoBaseInvestidor(PagadorRecebedor investidor, ContratoCobranca contrato, Date dataInicio,
			Date dataFim) {
		boolean retorno = false;

		Date dataEntradaInvestidor = null;

		// acha o investidorm no contrato
		if (contrato.getRecebedor() != null) {
			if (investidor.getId() == contrato.getRecebedor().getId()) {
				// pega as parcelas dele no contrato
				for (ContratoCobrancaParcelasInvestidor parcela : contrato
						.getListContratoCobrancaParcelasInvestidor1()) {
					dataEntradaInvestidor = parcela.getDataVencimento();

					Calendar c = Calendar.getInstance();
					c.setTime(parcela.getDataVencimento());
					c.add(Calendar.MONTH, -1);
					dataEntradaInvestidor = c.getTime();

					break;
				}
			}
		}

		if (contrato.getRecebedor2() != null) {
			if (investidor.getId() == contrato.getRecebedor2().getId()) {
				// pega as parcelas dele no contrato
				for (ContratoCobrancaParcelasInvestidor parcela : contrato
						.getListContratoCobrancaParcelasInvestidor2()) {
					dataEntradaInvestidor = parcela.getDataVencimento();

					Calendar c = Calendar.getInstance();
					c.setTime(parcela.getDataVencimento());
					c.add(Calendar.MONTH, -1);
					dataEntradaInvestidor = c.getTime();

					break;
				}
			}
		}

		if (contrato.getRecebedor3() != null) {
			if (investidor.getId() == contrato.getRecebedor3().getId()) {
				// pega as parcelas dele no contrato
				for (ContratoCobrancaParcelasInvestidor parcela : contrato
						.getListContratoCobrancaParcelasInvestidor3()) {
					dataEntradaInvestidor = parcela.getDataVencimento();

					Calendar c = Calendar.getInstance();
					c.setTime(parcela.getDataVencimento());
					c.add(Calendar.MONTH, -1);
					dataEntradaInvestidor = c.getTime();

					break;
				}
			}
		}

		if (contrato.getRecebedor4() != null) {
			if (investidor.getId() == contrato.getRecebedor4().getId()) {
				// pega as parcelas dele no contrato
				for (ContratoCobrancaParcelasInvestidor parcela : contrato
						.getListContratoCobrancaParcelasInvestidor4()) {
					dataEntradaInvestidor = parcela.getDataVencimento();

					Calendar c = Calendar.getInstance();
					c.setTime(parcela.getDataVencimento());
					c.add(Calendar.MONTH, -1);
					dataEntradaInvestidor = c.getTime();

					break;
				}
			}
		}

		if (contrato.getRecebedor5() != null) {
			if (investidor.getId() == contrato.getRecebedor5().getId()) {
				// pega as parcelas dele no contrato
				for (ContratoCobrancaParcelasInvestidor parcela : contrato
						.getListContratoCobrancaParcelasInvestidor5()) {
					dataEntradaInvestidor = parcela.getDataVencimento();

					Calendar c = Calendar.getInstance();
					c.setTime(parcela.getDataVencimento());
					c.add(Calendar.MONTH, -1);
					dataEntradaInvestidor = c.getTime();

					break;
				}
			}
		}

		if (contrato.getRecebedor6() != null) {
			if (investidor.getId() == contrato.getRecebedor6().getId()) {
				// pega as parcelas dele no contrato
				for (ContratoCobrancaParcelasInvestidor parcela : contrato
						.getListContratoCobrancaParcelasInvestidor6()) {
					dataEntradaInvestidor = parcela.getDataVencimento();

					Calendar c = Calendar.getInstance();
					c.setTime(parcela.getDataVencimento());
					c.add(Calendar.MONTH, -1);
					dataEntradaInvestidor = c.getTime();

					break;
				}
			}
		}

		if (contrato.getRecebedor7() != null) {
			if (investidor.getId() == contrato.getRecebedor7().getId()) {
				// pega as parcelas dele no contrato
				for (ContratoCobrancaParcelasInvestidor parcela : contrato
						.getListContratoCobrancaParcelasInvestidor7()) {
					dataEntradaInvestidor = parcela.getDataVencimento();

					Calendar c = Calendar.getInstance();
					c.setTime(parcela.getDataVencimento());
					c.add(Calendar.MONTH, -1);
					dataEntradaInvestidor = c.getTime();

					break;
				}
			}
		}

		if (contrato.getRecebedor8() != null) {
			if (investidor.getId() == contrato.getRecebedor8().getId()) {
				// pega as parcelas dele no contrato
				for (ContratoCobrancaParcelasInvestidor parcela : contrato
						.getListContratoCobrancaParcelasInvestidor8()) {
					dataEntradaInvestidor = parcela.getDataVencimento();

					Calendar c = Calendar.getInstance();
					c.setTime(parcela.getDataVencimento());
					c.add(Calendar.MONTH, -1);
					dataEntradaInvestidor = c.getTime();

					break;
				}
			}
		}

		if (contrato.getRecebedor9() != null) {
			if (investidor.getId() == contrato.getRecebedor9().getId()) {
				// pega as parcelas dele no contrato
				for (ContratoCobrancaParcelasInvestidor parcela : contrato
						.getListContratoCobrancaParcelasInvestidor9()) {
					dataEntradaInvestidor = parcela.getDataVencimento();

					Calendar c = Calendar.getInstance();
					c.setTime(parcela.getDataVencimento());
					c.add(Calendar.MONTH, -1);
					dataEntradaInvestidor = c.getTime();

					break;
				}
			}
		}

		if (contrato.getRecebedor10() != null) {
			if (investidor.getId() == contrato.getRecebedor10().getId()) {
				// pega as parcelas dele no contrato
				for (ContratoCobrancaParcelasInvestidor parcela : contrato
						.getListContratoCobrancaParcelasInvestidor10()) {
					dataEntradaInvestidor = parcela.getDataVencimento();

					Calendar c = Calendar.getInstance();
					c.setTime(parcela.getDataVencimento());
					c.add(Calendar.MONTH, -1);
					dataEntradaInvestidor = c.getTime();

					break;
				}
			}
		}
		// verifica se a data de entrada do investidor está no ano base
		if (dataEntradaInvestidor != null) {
			// if ((dataEntradaInvestidor.compareTo(dataInicio) == 0 ||
			// dataEntradaInvestidor.compareTo(dataInicio) > 0) &&
			// (dataEntradaInvestidor.compareTo(dataFim) == 0 ||
			// dataEntradaInvestidor.compareTo(dataFim) < 0)) {
			if ((dataEntradaInvestidor.compareTo(dataFim) == 0 || dataEntradaInvestidor.compareTo(dataFim) < 0)) {
				retorno = true;
			}
		}

		return retorno;
	}

	/***
	 * CAPTURA O SALDO DO CONTRATO NO ANO BASE ANTERIOR
	 * 
	 * @param dataInicioAnoAnterior
	 * @param dataFimAnoAnterior
	 * @param cDaoAtual
	 * @param numeroContrato
	 * @return
	 */
	public BigDecimal getSaldoInvestidorAnoAnterior(Date dataInicioAnoAnterior, Date dataFimAnoAnterior,
			ContratoCobrancaParcelasInvestidorDao cDaoAtual, ContratoCobrancaDao contratoCobrancaDao, String numeroContrato) {
		BigDecimal saldoAnterior = BigDecimal.ZERO;

		/***
		 * TODOS CONTRATOS QUE TIVERAM BAIXA NO ANNO ANTERIOR
		 */
		List<ContratoCobrancaParcelasInvestidor> parcelasInvestidorAnoAnterior = cDaoAtual
				.getParcelasPorDataInvestidorBaixadasInforme(dataInicioAnoAnterior, dataFimAnoAnterior,
						this.selectedPagador.getId());
		int numeroParcela = 0;

		for (ContratoCobrancaParcelasInvestidor parcela : parcelasInvestidorAnoAnterior) {
			ContratoCobranca contrato = contratoCobrancaDao.findById( parcela.getIdContrato());
			
			if (verificarAnoBaseInvestidor(this.selectedPagador, contrato, dataInicioAnoAnterior,
					dataFimAnoAnterior)) {
				if (numeroContrato.equals(parcela.getNumeroContrato())) {
					if (numeroParcela == 0) {
						saldoAnterior = parcela.getSaldoCredorAtualizado();
						numeroParcela = Integer.valueOf(parcela.getNumeroParcela());
					} else {
						if (Integer.valueOf(parcela.getNumeroParcela()) > numeroParcela) {
							saldoAnterior = parcela.getSaldoCredorAtualizado();
							numeroParcela = Integer.valueOf(parcela.getNumeroParcela());
						}
					}
				}
			}
		}

		if (numeroContrato.equals("01287")) {
			saldoAnterior = new BigDecimal(177000.00).setScale(2);
		}

		/****
		 * TODOS CONTRATOS GERADOS NO ANTERIOR E QUE NÃO TIVERAM BAIXA
		 */
		/*
		 * ContratoCobrancaDao contratoDao = new ContratoCobrancaDao();
		 * List<ContratoCobranca> listContratos =
		 * contratoDao.getContratosPorInvestidorInformeRendimentos(
		 * this.selectedPagador.getId(), dataInicioAnoAnterior, dataFimAnoAnterior);
		 * 
		 * // VERIFICAR SE O CONTRATO JÁ ENCONTRA-SE LISTADO NO ANO BASE // SE SIM, É PQ
		 * JÁ HOUVE BAIXA for (ContratoCobranca contrato : listContratos) { boolean
		 * consideraContrato = true;
		 * 
		 * for (InvestidorInformeRendimentos informe :
		 * this.investidorInformeRendimentos) { if
		 * (contrato.getNumeroContrato().equals(informe.getNumeroContrato())) {
		 * consideraContrato = false; } }
		 * 
		 * // SE CONSIDERA O CONTRATO, if (consideraContrato) { if
		 * (verificarAnoBaseInvestidor(this.selectedPagador, contrato.getId(),
		 * this.dataInicio, this.dataFim)) { saldoAnterior =
		 * buscaValorFinalInvestidorNoContrato(contrato, this.selectedPagador.getId());
		 * } } }
		 */

		return saldoAnterior;
	}

	public void processInformeRendimentosOLD() {
		FacesContext context = FacesContext.getCurrentInstance();
		ContratoCobrancaParcelasInvestidorDao cDao = new ContratoCobrancaParcelasInvestidorDao();

		if (this.selectedPagador == null || this.selectedPagador.getId() <= 0) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Informe de rendimentos: É obrigatória a seleção do investidor!", ""));
		} else {
			DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

			Date dataInicioAnterior = null;
			Date dataFimAnterior = null;

			try {
				if (this.anoBase.equals("2019")) {
					this.dataInicio = format.parse("01/01/2019");
					this.dataFim = format.parse("31/12/2019");

					dataInicioAnterior = format.parse("01/01/2018");
					dataFimAnterior = format.parse("31/12/2018");
				}

				if (this.anoBase.equals("2020")) {
					this.dataInicio = format.parse("01/01/2020");
					this.dataFim = format.parse("31/12/2020");

					dataInicioAnterior = format.parse("01/01/2019");
					dataFimAnterior = format.parse("31/12/2019");
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this.parcelasInvestidor = cDao.getParcelasPorDataInvestidorBaixadas(this.dataInicio, this.dataFim,
					this.selectedPagador.getId());

			this.totalIRRetido = this.getTotalIRRetidoInvestidor(this.selectedPagador.getId());
			BigDecimal totalJuros = this.getTotalJurosInvestidor(this.selectedPagador.getId());
			this.totalJurosLiquido = totalJuros.subtract(totalIRRetido);

			this.valorInvestidorAnoAtual = cDao.getParcelasPorDataInvestidorIR(this.dataInicio, this.dataFim,
					this.selectedPagador.getId());

			if (dataInicioAnterior != null && dataFimAnterior != null) {
				this.valorInvestidorAnoAnterior = cDao.getParcelasPorDataInvestidorIR(dataInicioAnterior,
						dataFimAnterior, this.selectedPagador.getId());
			} else {
				this.valorInvestidorAnoAnterior = BigDecimal.ZERO;
			}
		}
	}

	public String clearFieldsValorLiquido() {
		this.dataInicio = gerarDataHoje();
		this.dataFim = gerarDataHoje();

		this.parcelasInvestidor = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		this.parcelasInvestidorEnvelope = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		this.parcelasInvestidorSA = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		this.parcelasInvestidorCorrespondente = new ArrayList<ContratoCobrancaParcelasInvestidor>();

		this.valoresLiquidosInvestidoresPDFGerado = false;
		this.pathPDF = "";
		this.nomePDF = "";
		this.file = null;

		this.valoresLiquidosInvestidoresPDFGerado = false;

		return "/Atendimento/Cobranca/InvestidorValorLiquido.xhtml";
	}

	public String clearFieldsValorLiquidoRelatorio() {
		this.dataInicio = gerarDataHoje();
		this.dataFim = gerarDataHoje();

		this.parcelasInvestidor = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		this.parcelasInvestidorEnvelope = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		this.parcelasInvestidorSA = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		this.parcelasInvestidorCorrespondente = new ArrayList<ContratoCobrancaParcelasInvestidor>();

		this.valoresLiquidosInvestidoresPDFGerado = false;
		this.pathPDF = "";
		this.nomePDF = "";
		this.file = null;

		this.valoresLiquidosInvestidoresPDFGerado = false;

		return "/Atendimento/Cobranca/InvestidorValorLiquidoRelatorio.xhtml";
	}

	public void gerarRelatorioValorLiquido() {

		this.valoresLiquidosInvestidoresPDFGerado = false;
		this.parcelasInvestidorEnvelope = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		this.parcelasInvestidorSA = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		this.parcelasInvestidorCorrespondente = new ArrayList<ContratoCobrancaParcelasInvestidor>();

		ContratoCobrancaParcelasInvestidorDao cDao = new ContratoCobrancaParcelasInvestidorDao();

		List<ContratoCobrancaParcelasInvestidor> parcelasInvestidorAux = new ArrayList<ContratoCobrancaParcelasInvestidor>();

		parcelasInvestidorAux = cDao.getParcelasPorDataInvestidor(this.dataInicio, this.dataFim);

		for (ContratoCobrancaParcelasInvestidor p : parcelasInvestidorAux) {
			if (p.isEnvelope()) {
				this.parcelasInvestidorEnvelope.add(p);
			} else {
				if (p.getEmpresa() != null) {
					if (p.getEmpresa().equals("GALLERIA CORRESPONDENTE BANCARIO EIRELI")) {
						this.parcelasInvestidorCorrespondente.add(p);
					} else {
						this.parcelasInvestidorSA.add(p);
					}
				} else {
					this.parcelasInvestidorSA.add(p);
				}
			}
		}

		if (this.parcelasInvestidor.size() == 0 && this.parcelasInvestidorEnvelope.size() == 0) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Investidores: Não há registros para os filtros informados!", ""));
		}
	}

	public void gerarRelatorioValorLiquidoCustom() {

		this.valoresLiquidosInvestidoresPDFGerado = false;
		this.parcelasInvestidorEnvelope = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		this.parcelasInvestidorSA = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		this.parcelasInvestidorCorrespondente = new ArrayList<ContratoCobrancaParcelasInvestidor>();

		ContratoCobrancaParcelasInvestidorDao cDao = new ContratoCobrancaParcelasInvestidorDao();

		List<ContratoCobrancaParcelasInvestidor> parcelasInvestidorAux = new ArrayList<ContratoCobrancaParcelasInvestidor>();

		parcelasInvestidorAux = cDao.getParcelasPorDataInvestidor(this.dataInicio, this.dataFim);

		for (ContratoCobrancaParcelasInvestidor p : parcelasInvestidorAux) {
			if (p.isEnvelope()) {
				this.parcelasInvestidorEnvelope.add(p);
			} else {
				if (p.getEmpresa() != null) {
					if (p.getEmpresa().equals("GALLERIA CORRESPONDENTE BANCARIO EIRELI")) {
						this.parcelasInvestidorCorrespondente.add(p);
					} else {
						this.parcelasInvestidorSA.add(p);
					}
				} else {
					this.parcelasInvestidorSA.add(p);
				}
			}
		}

		if (this.parcelasInvestidor.size() == 0 && this.parcelasInvestidorEnvelope.size() == 0) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Investidores: Não há registros para os filtros informados!", ""));
		}
	}

	public String clearFieldsIRRetido() {
		this.dataInicio = gerarDataHoje();
		this.dataFim = gerarDataHoje();

		this.parcelasInvestidor = new ArrayList<ContratoCobrancaParcelasInvestidor>();

		PagadorRecebedorDao prDao = new PagadorRecebedorDao();
		this.listPagadores = prDao.findAll();
		clearPagador();

		this.irRetidoInvestidoresPDFGerado = false;
		this.pathPDF = "";
		this.nomePDF = "";
		this.file = null;

		return "/Atendimento/Cobranca/InvestidorIRRetido.xhtml";
	}

	public void gerarRelatorioIRRetido() {

		ContratoCobrancaParcelasInvestidorDao cDao = new ContratoCobrancaParcelasInvestidorDao();

		// busca apenas parcelas baixadas
		this.parcelasInvestidor = cDao.getParcelasPorDataInvestidorBaixadas(this.dataInicio, this.dataFim,
				this.selectedPagador.getId());

		if (this.parcelasInvestidor.size() == 0) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Investidores: Não há registros para os filtros informados!", ""));
		} else {
			for (ContratoCobrancaParcelasInvestidor parcela : this.parcelasInvestidor) {
				if (parcela.getIrRetido() == null) {
					parcela.setIrRetido(BigDecimal.ZERO);
				}
			}
		}
	}

	public BigDecimal getTotalLiquidoInvestidor(long idInvestidor) {
		BigDecimal totalLiquido = BigDecimal.ZERO;

		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidor) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				if (parcelas.getValorLiquido() != null) {
					totalLiquido = totalLiquido.add(parcelas.getValorLiquido());
				}
			}
		}

		return totalLiquido;
	}

	public BigDecimal getTotalLiquidoInvestidorSA(long idInvestidor) {
		BigDecimal totalLiquido = BigDecimal.ZERO;

		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorSA) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				if (parcelas.getValorLiquido() != null && parcelas.getValorLiquido().compareTo(BigDecimal.ZERO) == 1) {
					if (parcelas.getValorLiquidoBaixa() != null) {
						totalLiquido = totalLiquido.add(parcelas.getValorLiquidoBaixa());
					}
				}
			}
		}

		return totalLiquido.setScale(2);
	}

	public BigDecimal getTotalLiquidoInvestidorBaixa(long idInvestidor) {
		BigDecimal totalLiquido = BigDecimal.ZERO;

		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorSA) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				if (parcelas.getValorLiquidoBaixa() != null) {
					totalLiquido = totalLiquido.add(parcelas.getValorLiquidoBaixa());
				}
			}
		}

		return totalLiquido.setScale(2);
	}

	public BigDecimal getTotalParcelaInvestidorBaixa(long idInvestidor) {
		BigDecimal totalParcela = BigDecimal.ZERO;

		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorSA) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				if (parcelas.getParcelaMensalBaixa() != null) {
					totalParcela = totalParcela.add(parcelas.getParcelaMensalBaixa());
				}
			}
		}

		return totalParcela;
	}

	public BigDecimal getTotalFaceInvestidorBaixa(long idInvestidor) {
		BigDecimal totalFace = BigDecimal.ZERO;

		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorSA) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				if (parcelas.getValorFace() != null) {
					totalFace = totalFace.add(parcelas.getValorFace());
				}
			}
		}

		return totalFace.setScale(2);
	}

	public BigDecimal getTotalSaldoCredorInvestidorBaixa(long idInvestidor) {
		BigDecimal totalSaldoCredor = BigDecimal.ZERO;

		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorSA) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				if (parcelas.getSaldoCredorAtualizado() != null) {
					totalSaldoCredor = totalSaldoCredor.add(parcelas.getSaldoCredorAtualizado());
				}
			}
		}

		return totalSaldoCredor.setScale(2);
	}

	public BigDecimal getTotalFaceInvestidorBaixaCorrespondente(long idInvestidor) {
		BigDecimal totalFace = BigDecimal.ZERO;

		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorCorrespondente) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				if (parcelas.getValorFace() != null) {
					totalFace = totalFace.add(parcelas.getValorFace());
				}
			}
		}

		return totalFace.setScale(2);
	}

	public BigDecimal getTotalSaldoCredorInvestidorBaixaCorrespondente(long idInvestidor) {
		BigDecimal totalSaldoCredor = BigDecimal.ZERO;

		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorCorrespondente) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				if (parcelas.getSaldoCredorAtualizado() != null) {
					totalSaldoCredor = totalSaldoCredor.add(parcelas.getSaldoCredorAtualizado());
				}
			}
		}

		return totalSaldoCredor.setScale(2);
	}

	public BigDecimal getTotalFaceInvestidorBaixaEnvelope(long idInvestidor) {
		BigDecimal totalFace = BigDecimal.ZERO;

		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorEnvelope) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				if (parcelas.getValorFace() != null) {
					totalFace = totalFace.add(parcelas.getValorFace());
				}
			}
		}

		return totalFace.setScale(2);
	}

	public BigDecimal getTotalSaldoCredorInvestidorBaixaEnvelope(long idInvestidor) {
		BigDecimal totalSaldoCredor = BigDecimal.ZERO;

		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorEnvelope) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				if (parcelas.getSaldoCredorAtualizado() != null) {
					totalSaldoCredor = totalSaldoCredor.add(parcelas.getSaldoCredorAtualizado());
				}
			}
		}

		return totalSaldoCredor.setScale(2);
	}

	public BigDecimal getTotalLiquidoInvestidorCorrespondente(long idInvestidor) {
		BigDecimal totalLiquido = BigDecimal.ZERO;

		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorCorrespondente) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				if (parcelas.getValorLiquido() != null && parcelas.getValorLiquido().compareTo(BigDecimal.ZERO) == 1) {
					if (parcelas.getValorLiquidoBaixa() != null) {
						totalLiquido = totalLiquido.add(parcelas.getValorLiquidoBaixa());
					}
				}
			}
		}

		return totalLiquido.setScale(2);
	}

	public BigDecimal getTotalLiquidoInvestidorEnvelope(long idInvestidor) {
		BigDecimal totalLiquido = BigDecimal.ZERO;

		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorEnvelope) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				if (parcelas.getValorLiquido() != null && parcelas.getValorLiquido().compareTo(BigDecimal.ZERO) == 1) {
					if (parcelas.getValorLiquidoBaixa() != null) {
						totalLiquido = totalLiquido.add(parcelas.getValorLiquidoBaixa());
					}
				}
			}
		}

		return totalLiquido.setScale(2);
	}

	public BigDecimal getTotalLiquidoTodosInvestidores() {
		BigDecimal totalLiquido = BigDecimal.ZERO;

		if (this.parcelasInvestidor != null) {
			for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidor) {
				if (parcelas.getInvestidor().getId() != 14 && parcelas.getInvestidor().getId() != 15
						&& parcelas.getInvestidor().getId() != 34) {
					if (parcelas.getValorLiquido() != null) {
						totalLiquido = totalLiquido.add(parcelas.getValorLiquido());
					}
				}
			}
		}

		return totalLiquido;
	}

	public BigDecimal getTotalLiquidoTodosInvestidoresCorrespondente() {
		BigDecimal totalLiquido = BigDecimal.ZERO;

		if (this.parcelasInvestidorCorrespondente != null) {
			for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorCorrespondente) {
				if (parcelas.getInvestidor().getId() != 14 && parcelas.getInvestidor().getId() != 15
						&& parcelas.getInvestidor().getId() != 34) {
					if (parcelas.getValorLiquido() != null) {
						totalLiquido = totalLiquido.add(parcelas.getValorLiquido());
					}
				}
			}
		}

		return totalLiquido;
	}

	public BigDecimal getTotalLiquidoTodosInvestidoresSA() {
		BigDecimal totalLiquido = BigDecimal.ZERO;

		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorSA) {
			totalLiquido = totalLiquido.add(parcelas.getValorLiquidoBaixa());
		}

		return totalLiquido;
	}

	public BigDecimal getTotalLiquidoTodosInvestidoresSAOld() {
		BigDecimal totalLiquido = BigDecimal.ZERO;

		if (this.parcelasInvestidorSA != null) {
			for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorSA) {
				if (parcelas.getInvestidor().getId() != 14 && parcelas.getInvestidor().getId() != 15
						&& parcelas.getInvestidor().getId() != 34) {
					if (parcelas.getValorLiquido() != null) {
						totalLiquido = totalLiquido.add(parcelas.getValorLiquido());
					}
				}
			}
		}

		return totalLiquido;
	}

	public BigDecimal getTotalLiquidoTodosInvestidoresEnvelope() {
		BigDecimal totalLiquido = BigDecimal.ZERO;

		if (this.parcelasInvestidorEnvelope != null) {
			for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorEnvelope) {
				if (parcelas.getInvestidor().getId() != 14 && parcelas.getInvestidor().getId() != 15
						&& parcelas.getInvestidor().getId() != 34) {
					if (parcelas.getValorLiquido() != null) {
						totalLiquido = totalLiquido.add(parcelas.getValorLiquido());
					}
				}
			}
		}

		return totalLiquido;
	}

	public BigDecimal getTotalParcelaInvestidor(long idInvestidor) {
		BigDecimal totalParcela = BigDecimal.ZERO;

		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidor) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				if (parcelas.getParcelaMensalBaixa() != null) {
					totalParcela = totalParcela.add(parcelas.getParcelaMensalBaixa());
				}
			}
		}

		return totalParcela;
	}

	public BigDecimal getTotalParcelaInvestidorSA(long idInvestidor) {
		BigDecimal totalParcela = BigDecimal.ZERO;

		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorSA) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				if (parcelas.getParcelaMensal() != null) {
					totalParcela = totalParcela.add(parcelas.getParcelaMensal());
				}
			}
		}

		return totalParcela;
	}

	public BigDecimal getTotalParcelaInvestidorCorrespondente(long idInvestidor) {
		BigDecimal totalParcela = BigDecimal.ZERO;

		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorCorrespondente) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				if (parcelas.getParcelaMensal() != null) {
					totalParcela = totalParcela.add(parcelas.getParcelaMensal());
				}
			}
		}

		return totalParcela;
	}

	public BigDecimal getTotalParcelaInvestidorEnvelope(long idInvestidor) {
		BigDecimal totalParcela = BigDecimal.ZERO;

		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorEnvelope) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				if (parcelas.getParcelaMensal() != null) {
					if (parcelas.getParcelaMensal() != null) {
						totalParcela = totalParcela.add(parcelas.getParcelaMensal());
					}
				}
			}
		}

		return totalParcela;
	}

	public BigDecimal getTotalParcelaTodosInvestidores() {
		BigDecimal totalParcela = BigDecimal.ZERO;

		if (this.parcelasInvestidor != null) {
			for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidor) {
				if (parcelas.getInvestidor().getId() != 14 && parcelas.getInvestidor().getId() != 15
						&& parcelas.getInvestidor().getId() != 34) {
					if (parcelas.getParcelaMensal() != null) {
						totalParcela = totalParcela.add(parcelas.getParcelaMensal());
					}
				}
			}
		}

		return totalParcela;
	}

	public BigDecimal getTotalParcelaTodosInvestidoresEnvelope() {
		BigDecimal totalParcela = BigDecimal.ZERO;

		if (this.parcelasInvestidorEnvelope != null) {
			for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorEnvelope) {
				if (parcelas.getInvestidor().getId() != 14 && parcelas.getInvestidor().getId() != 15
						&& parcelas.getInvestidor().getId() != 34) {
					if (parcelas.getParcelaMensal() != null) {
						totalParcela = totalParcela.add(parcelas.getParcelaMensal());
					}
				}
			}
		}

		return totalParcela;
	}

	public BigDecimal getTotalIRRetidoInvestidor(long idInvestidor) {
		BigDecimal totalIRRetido = BigDecimal.ZERO;

		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidor) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				if (parcelas.getIrRetido() != null) {
					if (parcelas.getIrRetido() != null) {
						totalIRRetido = totalIRRetido.add(parcelas.getIrRetido());
					}
				}

			}
		}

		return totalIRRetido;
	}

	public BigDecimal getTotalJurosInvestidor(long idInvestidor) {
		BigDecimal totalJuros = BigDecimal.ZERO;

		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidor) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				if (parcelas.getJurosBaixa() != null) {
					if (parcelas.getJurosBaixa() != null) {
						totalJuros = totalJuros.add(parcelas.getJurosBaixa());
					}
				}

			}
		}

		return totalJuros;
	}

	public BigDecimal getTotalAmortizacaoInvestidor(long idInvestidor) {
		BigDecimal totalAmortizacao = BigDecimal.ZERO;

		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidor) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				if (parcelas.getAmortizacao() != null) {
					totalAmortizacao = totalAmortizacao.add(parcelas.getAmortizacao());
				}

			}
		}

		return totalAmortizacao;
	}

	public void clearFields() {
		this.qtdeContratosTotal = 0;
//		this.parcelasAbertasTotal = 0;
//		this.valorReceberTotal = BigDecimal.ZERO;
//		this.valorRecebidoTotal = BigDecimal.ZERO;
//		this.valorInvestidor = BigDecimal.ZERO;
//		this.valorInvestidoTotal = BigDecimal.ZERO;
		this.idInvestidor = 0;
		this.files = new ArrayList<FileUploaded>();
		this.posicaoInvestidorNoContrato = 0;
		this.situacaoSelecionada = null;
		this.existeContratoAtivo = false;
		this.existeContratoQuitado = false;

		this.investidorResumosTela = new ArrayList<DashboardInvestidorResumoVO>(0);
		this.contratosVOTela = new ArrayList<ContratoCobrancaResumoVO>(0);

		this.usuarioLogado = new User();

		this.contratos = new ArrayList<ContratoCobranca>();
		this.investidorResumos = new ArrayList<DashboardInvestidorResumoVO>(0);
		this.contratosVO = new ArrayList<ContratoCobrancaResumoVO>(0);
		this.extrato = new ArrayList<ExtratoVO>(0);

		this.selectedContratoCobrancaDetalhes = new ContratoCobrancaDetalhes();
	}

	public String getCssStatus(String status) {
		if (SiscoatConstants.CONTRATO_ATIVO.equals(status)) {
			return "dashboard-status-ativo";
		} else {
			return "dashboard-status-quitado";
		}
	}

	public String getLabelSituacaoResumo() {
		if (SiscoatConstants.CONTRATO_ATIVO.equals(this.situacaoSelecionada)) {
			return "Ativos";
		} else {
			return "Quitados";
		}
	}

	public void getContratosInvestidor() {
		clearFields();
		this.usuarioLogado = getUsuarioLogado();
		if (this.situacaoSelecionada == null) {
			getAtualizaDados();
		}
	}

	public void getAtualizaDados() {
		if (usuarioLogado != null) {
			// Busca o cadastro do Recebedor pelo ID do Usuário Logado
			PagadorRecebedorDao prDao = new PagadorRecebedorDao();
			PagadorRecebedor pr = new PagadorRecebedor();
			pr = prDao.getRecebedorByUsuarioInvestidor(usuarioLogado.getId());

			if (pr.getId() > 0) {
				this.idInvestidor = pr.getId();

				this.selectedPagador = pr;

				// get contratos por investidor
				ContratoCobrancaDao contratoDao = new ContratoCobrancaDao();
				this.contratos = contratoDao.getContratosPorInvestidor(this.idInvestidor);
				this.idContratosQuitado = contratoDao.consultaIdContratosQuitadosInvestidor(this.idInvestidor);
				getCardsDashboards();
			}
		}
	}

	// busca a posicao do investidor no contrato
	public void buscaPosicaoInvestidorNoContratoSemEnvelope(ContratoCobranca contrato) {
		this.posicaoInvestidorNoContrato = 0;

		if (contrato.getRecebedor() != null) {
			if (contrato.getRecebedor().getId() == this.idInvestidor) {
				if (!contrato.isRecebedorEnvelope()) {
					this.posicaoInvestidorNoContrato = 1;
				}
			}
		}

		if (contrato.getRecebedor2() != null) {
			if (contrato.getRecebedor2().getId() == this.idInvestidor) {
				if (!contrato.isRecebedorEnvelope2()) {
					this.posicaoInvestidorNoContrato = 2;
				}
			}
		}

		if (contrato.getRecebedor3() != null) {
			if (contrato.getRecebedor3().getId() == this.idInvestidor) {
				if (!contrato.isRecebedorEnvelope3()) {
					this.posicaoInvestidorNoContrato = 3;
				}
			}
		}

		if (contrato.getRecebedor4() != null) {
			if (contrato.getRecebedor4().getId() == this.idInvestidor) {
				if (!contrato.isRecebedorEnvelope4()) {
					this.posicaoInvestidorNoContrato = 4;
				}
			}
		}

		if (contrato.getRecebedor5() != null) {
			if (contrato.getRecebedor5().getId() == this.idInvestidor) {
				if (!contrato.isRecebedorEnvelope5()) {
					this.posicaoInvestidorNoContrato = 5;
				}
			}
		}

		if (contrato.getRecebedor6() != null) {
			if (contrato.getRecebedor6().getId() == this.idInvestidor) {
				if (!contrato.isRecebedorEnvelope6()) {
					this.posicaoInvestidorNoContrato = 6;
				}
			}
		}

		if (contrato.getRecebedor7() != null) {
			if (contrato.getRecebedor7().getId() == this.idInvestidor) {
				if (!contrato.isRecebedorEnvelope7()) {
					this.posicaoInvestidorNoContrato = 7;
				}
			}
		}

		if (contrato.getRecebedor8() != null) {
			if (contrato.getRecebedor8().getId() == this.idInvestidor) {
				if (!contrato.isRecebedorEnvelope8()) {
					this.posicaoInvestidorNoContrato = 8;
				}
			}
		}

		if (contrato.getRecebedor9() != null) {
			if (contrato.getRecebedor9().getId() == this.idInvestidor) {
				if (!contrato.isRecebedorEnvelope9()) {
					this.posicaoInvestidorNoContrato = 9;
				}
			}
		}

		if (contrato.getRecebedor10() != null) {
			if (contrato.getRecebedor10().getId() == this.idInvestidor) {
				if (!contrato.isRecebedorEnvelope10()) {
					this.posicaoInvestidorNoContrato = 10;
				}
			}
		}
	}

	// busca a posicao do investidor no contrato
	public void buscaPosicaoInvestidorNoContrato(ContratoCobranca contrato) {
		this.posicaoInvestidorNoContrato = 0;

		if (contrato.getRecebedor() != null) {
			if (contrato.getRecebedor().getId() == this.idInvestidor) {
				this.posicaoInvestidorNoContrato = 1;
			}
		}

		if (contrato.getRecebedor2() != null) {
			if (contrato.getRecebedor2().getId() == this.idInvestidor) {
				this.posicaoInvestidorNoContrato = 2;
			}
		}

		if (contrato.getRecebedor3() != null) {
			if (contrato.getRecebedor3().getId() == this.idInvestidor) {
				this.posicaoInvestidorNoContrato = 3;
			}
		}

		if (contrato.getRecebedor4() != null) {
			if (contrato.getRecebedor4().getId() == this.idInvestidor) {
				this.posicaoInvestidorNoContrato = 4;
			}
		}

		if (contrato.getRecebedor5() != null) {
			if (contrato.getRecebedor5().getId() == this.idInvestidor) {
				this.posicaoInvestidorNoContrato = 5;
			}
		}

		if (contrato.getRecebedor6() != null) {
			if (contrato.getRecebedor6().getId() == this.idInvestidor) {
				this.posicaoInvestidorNoContrato = 6;
			}
		}

		if (contrato.getRecebedor7() != null) {
			if (contrato.getRecebedor7().getId() == this.idInvestidor) {
				this.posicaoInvestidorNoContrato = 7;
			}
		}

		if (contrato.getRecebedor8() != null) {
			if (contrato.getRecebedor8().getId() == this.idInvestidor) {
				this.posicaoInvestidorNoContrato = 8;
			}
		}

		if (contrato.getRecebedor9() != null) {
			if (contrato.getRecebedor9().getId() == this.idInvestidor) {
				this.posicaoInvestidorNoContrato = 9;
			}
		}

		if (contrato.getRecebedor10() != null) {
			if (contrato.getRecebedor10().getId() == this.idInvestidor) {
				this.posicaoInvestidorNoContrato = 10;
			}
		}
	}

	// busca o valor final do investidor no contrato
	public BigDecimal buscaUltimoValorPago(ContratoCobranca contrato, long idInvestidor) {
		BigDecimal valorFinal = null;

		if (contrato.getRecebedor() != null) {
			if (contrato.getRecebedor().getId() == idInvestidor && !contrato.isRecebedorEnvelope()) {
				// pega a última parcela baixa
				for (ContratoCobrancaParcelasInvestidor parcelasInvestidor : contrato
						.getListContratoCobrancaParcelasInvestidor1()) {
					if (parcelasInvestidor.isBaixado()) {
						valorFinal = parcelasInvestidor.getSaldoCredorAtualizado();
					}
				}

				// se não houve baixa, pega a primeira parcela
				if (valorFinal == null) {
					if (contrato.getListContratoCobrancaParcelasInvestidor1().size() > 0) {
						valorFinal = contrato.getListContratoCobrancaParcelasInvestidor1().get(0).getSaldoCredor();
					}
				}
			}
		}

		if (contrato.getRecebedor2() != null) {
			if (contrato.getRecebedor2().getId() == idInvestidor && !contrato.isRecebedorEnvelope2()) {
				for (ContratoCobrancaParcelasInvestidor parcelasInvestidor : contrato
						.getListContratoCobrancaParcelasInvestidor2()) {
					if (parcelasInvestidor.isBaixado()) {
						valorFinal = parcelasInvestidor.getSaldoCredorAtualizado();
					}
				}

				// se não houve baixa, pega a primeira parcela
				if (valorFinal == null) {
					if (contrato.getListContratoCobrancaParcelasInvestidor2().size() > 0) {
						valorFinal = contrato.getListContratoCobrancaParcelasInvestidor2().get(0).getSaldoCredor();
					}
				}
			}
		}

		if (contrato.getRecebedor3() != null) {
			if (contrato.getRecebedor3().getId() == idInvestidor && !contrato.isRecebedorEnvelope3()) {
				for (ContratoCobrancaParcelasInvestidor parcelasInvestidor : contrato
						.getListContratoCobrancaParcelasInvestidor3()) {
					if (parcelasInvestidor.isBaixado()) {
						valorFinal = parcelasInvestidor.getSaldoCredorAtualizado();
					}
				}

				// se não houve baixa, pega a primeira parcela
				if (valorFinal == null) {
					if (contrato.getListContratoCobrancaParcelasInvestidor3().size() > 0) {
						valorFinal = contrato.getListContratoCobrancaParcelasInvestidor3().get(0).getSaldoCredor();
					}
				}
			}
		}

		if (contrato.getRecebedor4() != null) {
			if (contrato.getRecebedor4().getId() == idInvestidor && !contrato.isRecebedorEnvelope4()) {
				for (ContratoCobrancaParcelasInvestidor parcelasInvestidor : contrato
						.getListContratoCobrancaParcelasInvestidor4()) {
					if (parcelasInvestidor.isBaixado()) {
						valorFinal = parcelasInvestidor.getSaldoCredorAtualizado();
					}
				}

				// se não houve baixa, pega a primeira parcela
				if (valorFinal == null) {
					if (contrato.getListContratoCobrancaParcelasInvestidor4().size() > 0) {
						valorFinal = contrato.getListContratoCobrancaParcelasInvestidor4().get(0).getSaldoCredor();
					}
				}
			}
		}

		if (contrato.getRecebedor5() != null) {
			if (contrato.getRecebedor5().getId() == idInvestidor && !contrato.isRecebedorEnvelope5()) {
				for (ContratoCobrancaParcelasInvestidor parcelasInvestidor : contrato
						.getListContratoCobrancaParcelasInvestidor5()) {
					if (parcelasInvestidor.isBaixado()) {
						valorFinal = parcelasInvestidor.getSaldoCredorAtualizado();
					}
				}

				// se não houve baixa, pega a primeira parcela
				if (valorFinal == null) {
					if (contrato.getListContratoCobrancaParcelasInvestidor5().size() > 0) {
						valorFinal = contrato.getListContratoCobrancaParcelasInvestidor5().get(0).getSaldoCredor();
					}
				}
			}
		}

		if (contrato.getRecebedor6() != null) {
			if (contrato.getRecebedor6().getId() == idInvestidor && !contrato.isRecebedorEnvelope6()) {
				for (ContratoCobrancaParcelasInvestidor parcelasInvestidor : contrato
						.getListContratoCobrancaParcelasInvestidor6()) {
					if (parcelasInvestidor.isBaixado()) {
						valorFinal = parcelasInvestidor.getSaldoCredorAtualizado();
					}
				}

				// se não houve baixa, pega a primeira parcela
				if (valorFinal == null) {
					if (contrato.getListContratoCobrancaParcelasInvestidor6().size() > 0) {
						valorFinal = contrato.getListContratoCobrancaParcelasInvestidor6().get(0).getSaldoCredor();
					}
				}
			}
		}

		if (contrato.getRecebedor7() != null) {
			if (contrato.getRecebedor7().getId() == idInvestidor && !contrato.isRecebedorEnvelope7()) {
				for (ContratoCobrancaParcelasInvestidor parcelasInvestidor : contrato
						.getListContratoCobrancaParcelasInvestidor7()) {
					if (parcelasInvestidor.isBaixado()) {
						valorFinal = parcelasInvestidor.getSaldoCredorAtualizado();
					}
				}

				// se não houve baixa, pega a primeira parcela
				if (valorFinal == null) {
					if (contrato.getListContratoCobrancaParcelasInvestidor7().size() > 0) {
						valorFinal = contrato.getListContratoCobrancaParcelasInvestidor7().get(0).getSaldoCredor();
					}
				}
			}
		}

		if (contrato.getRecebedor8() != null) {
			if (contrato.getRecebedor8().getId() == idInvestidor && !contrato.isRecebedorEnvelope8()) {
				for (ContratoCobrancaParcelasInvestidor parcelasInvestidor : contrato
						.getListContratoCobrancaParcelasInvestidor8()) {
					if (parcelasInvestidor.isBaixado()) {
						valorFinal = parcelasInvestidor.getSaldoCredorAtualizado();
					}
				}

				// se não houve baixa, pega a primeira parcela
				if (valorFinal == null) {
					if (contrato.getListContratoCobrancaParcelasInvestidor8().size() > 0) {
						valorFinal = contrato.getListContratoCobrancaParcelasInvestidor8().get(0).getSaldoCredor();
					}
				}
			}
		}

		if (contrato.getRecebedor9() != null) {
			if (contrato.getRecebedor9().getId() == idInvestidor && !contrato.isRecebedorEnvelope9()) {
				for (ContratoCobrancaParcelasInvestidor parcelasInvestidor : contrato
						.getListContratoCobrancaParcelasInvestidor9()) {
					if (parcelasInvestidor.isBaixado()) {
						valorFinal = parcelasInvestidor.getSaldoCredorAtualizado();
					}
				}

				// se não houve baixa, pega a primeira parcela
				if (valorFinal == null) {
					if (contrato.getListContratoCobrancaParcelasInvestidor9().size() > 0) {
						valorFinal = contrato.getListContratoCobrancaParcelasInvestidor9().get(0).getSaldoCredor();
					}
				}
			}
		}

		if (contrato.getRecebedor10() != null) {
			if (contrato.getRecebedor10().getId() == idInvestidor && !contrato.isRecebedorEnvelope10()) {
				for (ContratoCobrancaParcelasInvestidor parcelasInvestidor : contrato
						.getListContratoCobrancaParcelasInvestidor10()) {
					if (parcelasInvestidor.isBaixado()) {
						valorFinal = parcelasInvestidor.getSaldoCredorAtualizado();
					}
				}

				// se não houve baixa, pega a primeira parcela
				if (valorFinal == null) {
					if (contrato.getListContratoCobrancaParcelasInvestidor10().size() > 0) {
						valorFinal = contrato.getListContratoCobrancaParcelasInvestidor10().get(0).getSaldoCredor();
					}
				}
			}
		}

		return valorFinal;
	}

	// busca o valor final do investidor no contrato
	public BigDecimal buscaValorDebentureInvestidorNoContrato(ContratoCobranca contrato, long idInvestidor) {
		BigDecimal valorFinal = BigDecimal.ZERO;

		if (contrato.getRecebedor() != null) {
			if (contrato.getRecebedor().getId() == idInvestidor) {
				valorFinal = contrato.getVlrInvestidor1();
			}
		}

		if (contrato.getRecebedor2() != null) {
			if (contrato.getRecebedor2().getId() == idInvestidor) {
				valorFinal = contrato.getVlrInvestidor2();
			}
		}

		if (contrato.getRecebedor3() != null) {
			if (contrato.getRecebedor3().getId() == idInvestidor) {
				valorFinal = contrato.getVlrInvestidor3();
			}
		}

		if (contrato.getRecebedor4() != null) {
			if (contrato.getRecebedor4().getId() == idInvestidor) {
				valorFinal = contrato.getVlrInvestidor4();
			}
		}

		if (contrato.getRecebedor5() != null) {
			if (contrato.getRecebedor5().getId() == idInvestidor) {
				valorFinal = contrato.getVlrInvestidor5();
			}
		}

		if (contrato.getRecebedor6() != null) {
			if (contrato.getRecebedor6().getId() == idInvestidor) {
				valorFinal = contrato.getVlrInvestidor6();
			}
		}

		if (contrato.getRecebedor7() != null) {
			if (contrato.getRecebedor7().getId() == idInvestidor) {
				valorFinal = contrato.getVlrInvestidor7();
			}
		}

		if (contrato.getRecebedor8() != null) {
			if (contrato.getRecebedor8().getId() == idInvestidor) {
				valorFinal = contrato.getVlrInvestidor8();
			}
		}

		if (contrato.getRecebedor9() != null) {
			if (contrato.getRecebedor9().getId() == idInvestidor) {
				valorFinal = contrato.getVlrInvestidor9();
			}
		}

		if (contrato.getRecebedor10() != null) {
			if (contrato.getRecebedor10().getId() == idInvestidor) {
				valorFinal = contrato.getVlrInvestidor10();
			}
		}

		return valorFinal;
	}

	// busca o valor final do investidor no contrato
	public BigDecimal buscaValorFinalInvestidorNoContrato(ContratoCobranca contrato, long idInvestidor) {
		BigDecimal valorFinal = BigDecimal.ZERO;

		if (contrato.getRecebedor() != null) {
			if (contrato.getRecebedor().getId() == idInvestidor) {
				valorFinal = contrato.getVlrFinalRecebedor1();
			}
		}

		if (contrato.getRecebedor2() != null) {
			if (contrato.getRecebedor2().getId() == idInvestidor) {
				valorFinal = contrato.getVlrFinalRecebedor2();
			}
		}

		if (contrato.getRecebedor3() != null) {
			if (contrato.getRecebedor3().getId() == idInvestidor) {
				valorFinal = contrato.getVlrFinalRecebedor3();
			}
		}

		if (contrato.getRecebedor4() != null) {
			if (contrato.getRecebedor4().getId() == idInvestidor) {
				valorFinal = contrato.getVlrFinalRecebedor4();
			}
		}

		if (contrato.getRecebedor5() != null) {
			if (contrato.getRecebedor5().getId() == idInvestidor) {
				valorFinal = contrato.getVlrFinalRecebedor5();
			}
		}

		if (contrato.getRecebedor6() != null) {
			if (contrato.getRecebedor6().getId() == idInvestidor) {
				valorFinal = contrato.getVlrFinalRecebedor6();
			}
		}

		if (contrato.getRecebedor7() != null) {
			if (contrato.getRecebedor7().getId() == idInvestidor) {
				valorFinal = contrato.getVlrFinalRecebedor7();
			}
		}

		if (contrato.getRecebedor8() != null) {
			if (contrato.getRecebedor8().getId() == idInvestidor) {
				valorFinal = contrato.getVlrFinalRecebedor8();
			}
		}

		if (contrato.getRecebedor9() != null) {
			if (contrato.getRecebedor9().getId() == idInvestidor) {
				valorFinal = contrato.getVlrFinalRecebedor9();
			}
		}

		if (contrato.getRecebedor10() != null) {
			if (contrato.getRecebedor10().getId() == idInvestidor) {
				valorFinal = contrato.getVlrFinalRecebedor10();
			}
		}

		return valorFinal;
	}

	private void buscaValorInvestidoContratos(ContratoCobranca c, ContratoCobrancaResumoVO contratoVo) {
		if (!c.isRecebedorEnvelope()) {
			contratoVo.setValorInvestido(BigDecimal.ZERO);

			List<ContratoCobrancaParcelasInvestidor> listParcelasInvestidor;
			listParcelasInvestidor = buscaListaParcelasInvestidor(c);

			for (ContratoCobrancaParcelasInvestidor cd : listParcelasInvestidor) {
				if (cd.isBaixado()) {
					// valorInvestidoContrato = cd.getSaldoCredorAtualizado();
					contratoVo.setValorInvestido(cd.getSaldoCredorAtualizado());
				} else {
					break;
				}
			}

			if (contratoVo.getValorInvestido().compareTo(BigDecimal.ZERO) == 0) {
				// valorInvestidoContrato = c.getVlrFinalRecebedor1();
				boolean semValor = false;
				switch (this.posicaoInvestidorNoContrato) {
				case 1:
					if (CommonsUtil.semValor(c.getVlrInvestidor1())) {
						if (listParcelasInvestidor.size() > 0) {
							if (listParcelasInvestidor.get(0).getSaldoCredor() != null) {
								contratoVo.setValorInvestido(
										Util.zeroIsNull(listParcelasInvestidor.get(0).getSaldoCredor()));
							} else {
								semValor = true;
							}
						} else {
							semValor = true;
						}

						if (semValor) {
							contratoVo.setValorInvestido(Util.zeroIsNull(c.getVlrFinalRecebedor1()));
						}
					} else {
						contratoVo.setValorInvestido(Util.zeroIsNull(c.getVlrInvestidor1()));
					}
					break;
				case 2:
					semValor = false;
					if (CommonsUtil.semValor(c.getVlrInvestidor2())) {
						if (listParcelasInvestidor.size() > 0) {
							if (listParcelasInvestidor.get(0).getSaldoCredor() != null) {
								contratoVo.setValorInvestido(
										Util.zeroIsNull(listParcelasInvestidor.get(0).getSaldoCredor()));
							} else {
								semValor = true;
							}
						} else {
							semValor = true;
						}

						if (semValor) {
							contratoVo.setValorInvestido(Util.zeroIsNull(c.getVlrFinalRecebedor2()));
						}
					} else {
						contratoVo.setValorInvestido(Util.zeroIsNull(c.getVlrInvestidor2()));
					}
					break;
				case 3:
					semValor = false;
					if (CommonsUtil.semValor(c.getVlrInvestidor3())) {
						if (listParcelasInvestidor.size() > 0) {
							if (listParcelasInvestidor.get(0).getSaldoCredor() != null) {
								contratoVo.setValorInvestido(
										Util.zeroIsNull(listParcelasInvestidor.get(0).getSaldoCredor()));
							} else {
								semValor = true;
							}
						} else {
							semValor = true;
						}

						if (semValor) {
							contratoVo.setValorInvestido(Util.zeroIsNull(c.getVlrFinalRecebedor3()));
						}
					} else {
						contratoVo.setValorInvestido(Util.zeroIsNull(c.getVlrInvestidor3()));
					}
					break;
				case 4:
					semValor = false;
					if (CommonsUtil.semValor(c.getVlrInvestidor4())) {
						if (listParcelasInvestidor.size() > 0) {
							if (listParcelasInvestidor.get(0).getSaldoCredor() != null) {
								contratoVo.setValorInvestido(
										Util.zeroIsNull(listParcelasInvestidor.get(0).getSaldoCredor()));
							} else {
								semValor = true;
							}
						} else {
							semValor = true;
						}

						if (semValor) {
							contratoVo.setValorInvestido(Util.zeroIsNull(c.getVlrFinalRecebedor4()));
						}
					} else {
						contratoVo.setValorInvestido(Util.zeroIsNull(c.getVlrInvestidor4()));
					}
					break;
				case 5:
					semValor = false;
					if (CommonsUtil.semValor(c.getVlrInvestidor5())) {
						if (listParcelasInvestidor.size() > 0) {
							if (listParcelasInvestidor.get(0).getSaldoCredor() != null) {
								contratoVo.setValorInvestido(
										Util.zeroIsNull(listParcelasInvestidor.get(0).getSaldoCredor()));
							} else {
								semValor = true;
							}
						} else {
							semValor = true;
						}

						if (semValor) {
							contratoVo.setValorInvestido(Util.zeroIsNull(c.getVlrFinalRecebedor5()));
						}
					} else {
						contratoVo.setValorInvestido(Util.zeroIsNull(c.getVlrInvestidor5()));
					}
					break;
				case 6:
					semValor = false;
					if (CommonsUtil.semValor(c.getVlrInvestidor6())) {
						if (listParcelasInvestidor.size() > 0) {
							if (listParcelasInvestidor.get(0).getSaldoCredor() != null) {
								contratoVo.setValorInvestido(
										Util.zeroIsNull(listParcelasInvestidor.get(0).getSaldoCredor()));
							} else {
								semValor = true;
							}
						} else {
							semValor = true;
						}

						if (semValor) {
							contratoVo.setValorInvestido(Util.zeroIsNull(c.getVlrFinalRecebedor6()));
						}
					} else {
						contratoVo.setValorInvestido(Util.zeroIsNull(c.getVlrInvestidor6()));
					}
					break;
				case 7:
					semValor = false;
					if (CommonsUtil.semValor(c.getVlrInvestidor7())) {
						if (listParcelasInvestidor.size() > 0) {
							if (listParcelasInvestidor.get(0).getSaldoCredor() != null) {
								contratoVo.setValorInvestido(
										Util.zeroIsNull(listParcelasInvestidor.get(0).getSaldoCredor()));
							} else {
								semValor = true;
							}
						} else {
							semValor = true;
						}

						if (semValor) {
							contratoVo.setValorInvestido(Util.zeroIsNull(c.getVlrFinalRecebedor7()));
						}
					} else {
						contratoVo.setValorInvestido(Util.zeroIsNull(c.getVlrInvestidor7()));
					}
					break;
				case 8:
					semValor = false;
					if (CommonsUtil.semValor(c.getVlrInvestidor8())) {
						if (listParcelasInvestidor.size() > 0) {
							if (listParcelasInvestidor.get(0).getSaldoCredor() != null) {
								contratoVo.setValorInvestido(
										Util.zeroIsNull(listParcelasInvestidor.get(0).getSaldoCredor()));
							} else {
								semValor = true;
							}
						} else {
							semValor = true;
						}

						if (semValor) {
							contratoVo.setValorInvestido(Util.zeroIsNull(c.getVlrFinalRecebedor8()));
						}
					} else {
						contratoVo.setValorInvestido(Util.zeroIsNull(c.getVlrInvestidor8()));
					}
					break;
				case 9:
					semValor = false;
					if (CommonsUtil.semValor(c.getVlrInvestidor9())) {
						if (listParcelasInvestidor.size() > 0) {
							if (listParcelasInvestidor.get(0).getSaldoCredor() != null) {
								contratoVo.setValorInvestido(
										Util.zeroIsNull(listParcelasInvestidor.get(0).getSaldoCredor()));
							} else {
								semValor = true;
							}
						} else {
							semValor = true;
						}

						if (semValor) {
							contratoVo.setValorInvestido(Util.zeroIsNull(c.getVlrFinalRecebedor9()));
						}
					} else {
						contratoVo.setValorInvestido(Util.zeroIsNull(c.getVlrInvestidor9()));
					}
					break;
				case 10:
					semValor = false;
					if (CommonsUtil.semValor(c.getVlrInvestidor10())) {
						if (listParcelasInvestidor.size() > 0) {
							if (listParcelasInvestidor.get(0).getSaldoCredor() != null) {
								contratoVo.setValorInvestido(
										Util.zeroIsNull(listParcelasInvestidor.get(0).getSaldoCredor()));
							} else {
								semValor = true;
							}
						} else {
							semValor = true;
						}

						if (semValor) {
							contratoVo.setValorInvestido(Util.zeroIsNull(c.getVlrFinalRecebedor10()));
						}
					} else {
						contratoVo.setValorInvestido(Util.zeroIsNull(c.getVlrInvestidor10()));
					}
					break;
				}
			}

			Date dataInvestimento = c.getDataContrato();
			String pagador = "";
			switch (this.posicaoInvestidorNoContrato) {
			case 1:
				if (c.getDataInclusaoRecebedor1() != null)
					dataInvestimento = c.getDataInclusaoRecebedor1();
				pagador = c.getRecebedor().getNome();
				break;
			case 2:
				if (c.getDataInclusaoRecebedor2() != null)
					dataInvestimento = c.getDataInclusaoRecebedor2();
				pagador = c.getRecebedor2().getNome();
				break;
			case 3:
				if (c.getDataInclusaoRecebedor3() != null)
					dataInvestimento = c.getDataInclusaoRecebedor3();
				pagador = c.getRecebedor3().getNome();
				break;
			case 4:
				if (c.getDataInclusaoRecebedor4() != null)
					dataInvestimento = c.getDataInclusaoRecebedor4();
				pagador = c.getRecebedor4().getNome();
				break;
			case 5:
				if (c.getDataInclusaoRecebedor5() != null)
					dataInvestimento = c.getDataInclusaoRecebedor5();
				pagador = c.getRecebedor5().getNome();
				break;
			case 6:
				if (c.getDataInclusaoRecebedor6() != null)
					dataInvestimento = c.getDataInclusaoRecebedor6();
				pagador = c.getRecebedor6().getNome();
				break;
			case 7:
				if (c.getDataInclusaoRecebedor7() != null)
					dataInvestimento = c.getDataInclusaoRecebedor7();
				pagador = c.getRecebedor7().getNome();
				break;
			case 8:
				if (c.getDataInclusaoRecebedor8() != null)
					dataInvestimento = c.getDataInclusaoRecebedor8();
				pagador = c.getRecebedor8().getNome();
				break;
			case 9:
				if (c.getDataInclusaoRecebedor9() != null)
					dataInvestimento = c.getDataInclusaoRecebedor9();
				pagador = c.getRecebedor9().getNome();
				break;
			case 10:
				if (c.getDataInclusaoRecebedor10() != null)
					dataInvestimento = c.getDataInclusaoRecebedor10();
				pagador = c.getRecebedor10().getNome();
				break;
			}

			if (contratoVo.getValorInvestido().compareTo(BigDecimal.ZERO) > 0) {
				ExtratoVO extrato = new ExtratoVO();
				extrato.setIdContratoCobranca(c.getId());
				extrato.setNumeroContrato(c.getNumeroContrato());
				extrato.setDataMovimento(dataInvestimento);
				extrato.setDebitoCredito('D');
				extrato.setValor(contratoVo.getValorInvestido());
				extrato.setTipoLancamento('P');
				extrato.setPagador(pagador);
				this.extrato.add(extrato);
			}
		}
	}

	private List<ContratoCobrancaParcelasInvestidor> buscaListaParcelasInvestidor(ContratoCobranca c) {
		List<ContratoCobrancaParcelasInvestidor> listParcelasInvestidor;
		switch (this.posicaoInvestidorNoContrato) {
		case 1:
			listParcelasInvestidor = c.getListContratoCobrancaParcelasInvestidor1();
			break;
		case 2:
			listParcelasInvestidor = c.getListContratoCobrancaParcelasInvestidor2();
			break;
		case 3:
			listParcelasInvestidor = c.getListContratoCobrancaParcelasInvestidor3();
			break;
		case 4:
			listParcelasInvestidor = c.getListContratoCobrancaParcelasInvestidor4();
			break;
		case 5:
			listParcelasInvestidor = c.getListContratoCobrancaParcelasInvestidor5();
			break;
		case 6:
			listParcelasInvestidor = c.getListContratoCobrancaParcelasInvestidor6();
			break;
		case 7:
			listParcelasInvestidor = c.getListContratoCobrancaParcelasInvestidor7();
			break;
		case 8:
			listParcelasInvestidor = c.getListContratoCobrancaParcelasInvestidor8();
			break;
		case 9:
			listParcelasInvestidor = c.getListContratoCobrancaParcelasInvestidor9();
			break;
		case 10:
			listParcelasInvestidor = c.getListContratoCobrancaParcelasInvestidor10();
			break;
		default:
			listParcelasInvestidor = new ArrayList<ContratoCobrancaParcelasInvestidor>(0);
		}
		return listParcelasInvestidor;
	}

	private void buscaValorPagoContrato(ContratoCobranca c, ContratoCobrancaResumoVO contratoVo) {
		if (!c.isRecebedorEnvelope()) {
			List<ContratoCobrancaParcelasInvestidor> listParcelasInvestidor;
			listParcelasInvestidor = buscaListaParcelasInvestidor(c);

			String contratoQuitato = "-1";

			for (ContratoCobrancaParcelasInvestidor cd : listParcelasInvestidor) {
				if (!cd.isBaixado()) {
					// se parcela paga em aberto, soma qtde de parcelas e valor em aberto
					contratoVo.adicionaParcelaAberta();
					contratoVo.addValorReceber(cd.getValorLiquido());
					if (contratoQuitato.equals("-1"))
						contratoQuitato = cd.getNumeroParcela();
				} else {

					// se parcela paga soma o valor recebido pelo investidor
					// soma valor a receber
					BigDecimal valorRecebido = BigDecimal.ZERO;
//					if (cd.getSaldoCredorAtualizado().compareTo( BigDecimal.ZERO) == 0) {
//						valorRecebido = cd.getValorLiquido().subtract(contratoVo.getValorInvestido());						
//					} else {
					if (!CommonsUtil.semValor(cd.getAmortizacao()) && !CommonsUtil.semValor(cd.getValorLiquido())) {
						valorRecebido = cd.getValorLiquido().subtract(cd.getAmortizacao());
					}

//					}					
					contratoVo.addValorRecebido(valorRecebido);

					if (contratoVo.getValorRecebido().compareTo(BigDecimal.ZERO) > 0) {
						ExtratoVO extrato = new ExtratoVO();
						extrato.setIdContratoCobranca(c.getId());
						extrato.setNumeroContrato(c.getNumeroContrato());
						extrato.setDataMovimento(cd.getDataBaixa());
						extrato.setDebitoCredito('C');
						extrato.setTipoLancamento('J');
						extrato.setValor(valorRecebido);
						if (cd.getPagador() != null) {
							extrato.setPagador(cd.getPagador().getNome());
						} else {
							extrato.setPagador(cd.getInvestidor().getNome());
						}
						this.extrato.add(extrato);
					}

					if (!CommonsUtil.semValor(cd.getAmortizacao())) {
						if (cd.getAmortizacao().compareTo(BigDecimal.ZERO) > 0) {
							ExtratoVO extrato = new ExtratoVO();
							extrato.setIdContratoCobranca(c.getId());
							extrato.setNumeroContrato(c.getNumeroContrato());
							extrato.setDataMovimento(cd.getDataBaixa());
							extrato.setDebitoCredito('C');
							if (cd.getSaldoCredorAtualizado().compareTo(BigDecimal.ZERO) == 0) {
								extrato.setTipoLancamento('Q');
							} else {
								extrato.setTipoLancamento('A');
							}
							extrato.setValor(cd.getAmortizacao());
							if (cd.getPagador() != null) {
								extrato.setPagador(cd.getPagador().getNome());
							} else {
								extrato.setPagador(cd.getInvestidor().getNome());
							}
							this.extrato.add(extrato);
						}
					}
				}
			}
			if (contratoQuitato.equals("-1")) {
				contratoVo.setSituacao(SiscoatConstants.CONTRATO_QUITADO);
			}

		}
	}

	// calcula quantidade de parcelas abertas
	public void getCardsDashboards() {

		investidorResumos = new ArrayList<DashboardInvestidorResumoVO>(0);

		this.qtdeContratosTotal = this.contratos.size();
//		this.valorInvestidoTotal = BigDecimal.ZERO;
		// BigDecimal valorInvestidoContrato = BigDecimal.ZERO;

		this.contratosVO = new ArrayList<ContratoCobrancaResumoVO>(0);
		this.extrato = new ArrayList<ExtratoVO>(0);

		for (ContratoCobranca c : this.contratos) {

			ContratoCobrancaResumoVO contratoVo = new ContratoCobrancaResumoVO();
			contratoVo.setId(c.getId());
			contratoVo.setNumeroContrato(c.getNumeroContrato());
			contratoVo.setDataContrato(c.getDataContrato());
			contratoVo.setPagador(c.getPagador().getNome());
			contratoVo.setSituacao(SiscoatConstants.CONTRATO_ATIVO);
			contratoVo.setParcelasAbertas(BigInteger.ZERO);
			contratoVo.setValorReceber(BigDecimal.ZERO);
			contratoVo.setValorRecebido(BigDecimal.ZERO);
			contratoVo.setValorInvestido(BigDecimal.ZERO);

			// busca o valor do investidor no contrato
			getInformacoesDoInvestidorNoContrato(c, contratoVo);

			// retorna a posicao de investidores que não são envelope
			buscaPosicaoInvestidorNoContratoSemEnvelope(c);

			// busca valor investido nos contratos
			buscaValorInvestidoContratos(c, contratoVo);

			// Atribui o valor investido no contrato em questão a variavel global
//			this.valorInvestidoTotal = this.valorInvestidoTotal.add(contratoVo.getValorInvestido());

			// Atribui o valor investido no contrato em questão a variavel global
			// this.valorInvestido = this.valorInvestido.add(valorInvestidoContrato);
//			int parcelasAbertasContrato = 0;
//			BigDecimal valorReceberTotalContrato = BigDecimal.ZERO;
//			BigDecimal valorRecebidoTotalContrato = BigDecimal.ZERO;

			// busca valores a receber e pagos
			buscaValorPagoContrato(c, contratoVo);

			// acerta o valor recebido retirando o total do valor investido ????
			// contratoVo.acertaValorRecebido();

			this.contratosVO.add(contratoVo);

			DashboardInvestidorResumoVO investidorResumo = null;

			for (DashboardInvestidorResumoVO dashboardInvestidorResumo : investidorResumos) {
				if (dashboardInvestidorResumo.getSituacaoContrato().equals(contratoVo.getSituacao())) {
					investidorResumo = dashboardInvestidorResumo;
					break;
				}
			}

			if (investidorResumo == null) {
				investidorResumo = new DashboardInvestidorResumoVO();
				investidorResumo.setSituacaoContrato(contratoVo.getSituacao());
				investidorResumo.setQtdeContratos(BigInteger.ZERO);
				investidorResumo.setParcelasAbertas(BigInteger.ZERO);
				investidorResumo.setValorInvestido(BigDecimal.ZERO);
				investidorResumo.setValorRecebido(BigDecimal.ZERO);
				investidorResumo.setValorReceber(BigDecimal.ZERO);
				investidorResumos.add(investidorResumo);

				if (SiscoatConstants.CONTRATO_ATIVO.equals(contratoVo.getSituacao())) {
					existeContratoAtivo = true;
				}
				if (SiscoatConstants.CONTRATO_QUITADO.equals(contratoVo.getSituacao())) {
					existeContratoQuitado = true;
				}

			}

			investidorResumo.addValorInvestido(contratoVo.getValorInvestido());
			investidorResumo.adicionaQtdeContratos();
			investidorResumo.addParcelasAbertas(contratoVo.getParcelasAbertas());
			investidorResumo.addValorReceber(contratoVo.getValorReceber());
			investidorResumo.addValorRecebido(contratoVo.getValorRecebido());

		}

		Collections.sort(this.contratosVO, new Comparator<ContratoCobrancaResumoVO>() {
			@Override
			public int compare(ContratoCobrancaResumoVO one, ContratoCobrancaResumoVO other) {
				return one.getSituacao().compareTo(other.getSituacao());
			}
		});

		Collections.sort(this.investidorResumos, new Comparator<DashboardInvestidorResumoVO>() {
			@Override
			public int compare(DashboardInvestidorResumoVO one, DashboardInvestidorResumoVO other) {
				return one.getSituacaoContrato().compareTo(other.getSituacaoContrato());
			}
		});

		if (!this.investidorResumos.isEmpty()) {
			situacaoSelecionada = this.investidorResumos.get(0).getSituacaoContrato();
			filtraDados();
		} else {
			investidorResumosTela = new ArrayList<DashboardInvestidorResumoVO>(0);
		}

		Collections.sort(this.extrato, new Comparator<ExtratoVO>() {
			@Override
			public int compare(ExtratoVO one, ExtratoVO other) {
				return other.getDataMovimento().compareTo(one.getDataMovimento());
			}
		});

	}

	public List<ExtratoVO> getExtratoUltimosLancamentos() {
		List<ExtratoVO> result = new ArrayList<ExtratoVO>(0);

		int iExtrato = 0;
		for (ExtratoVO extratoVO : this.extrato) {
			result.add(extratoVO);
			iExtrato++;
			if (iExtrato == 5) {
				break;
			}
		}

		return result;
	}

	public void filtrarSituacaoAtivos() {
		situacaoSelecionada = SiscoatConstants.CONTRATO_ATIVO;
		filtraDados();
	}

	public void filtrarSituacaoQuitados() {
		situacaoSelecionada = SiscoatConstants.CONTRATO_QUITADO;
		filtraDados();
	}

	public void filtraDados() {
		investidorResumosTela = new ArrayList<DashboardInvestidorResumoVO>(0);
		contratosVOTela = new ArrayList<ContratoCobrancaResumoVO>(0);
		for (DashboardInvestidorResumoVO resumo : investidorResumos) {
			if (situacaoSelecionada.equals(resumo.getSituacaoContrato())) {
				investidorResumosTela.add(resumo);
				break;
			}
		}
		for (ContratoCobrancaResumoVO contratoVO : contratosVO) {
			if (situacaoSelecionada.equals(contratoVO.getSituacao())) {
				contratosVOTela.add(contratoVO);
			}
		}
	}

	public StreamedContent downloadInformeRendimentosPortalInvestidor(String anoBase) {

		return processInformeRendimentosPortalInvestidor(anoBase);
	}

	/***
	 * Lista ois arquivos contidos no diretório
	 * 
	 * @return
	 */
	public Collection<FileUploaded> listaArquivos(ContratoCobranca contrato) {
		// DateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");
		ParametrosDao pDao = new ParametrosDao();
		String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
				+ contrato.getNumeroContrato() + "/";
		File diretorio = new File(pathContrato);
		File arqs[] = diretorio.listFiles();
		Collection<FileUploaded> lista = new ArrayList<FileUploaded>();
		if (arqs != null) {
			for (int i = 0; i < arqs.length; i++) {
				File arquivo = arqs[i];

				lista.add(new FileUploaded(arquivo.getName(), arquivo, pathContrato));
			}
		}
		return lista;
	}

	public void getFilesDoInvestidorNoContrato(ContratoCobranca contrato) {
		// get files do contrato
		this.files = new ArrayList<FileUploaded>();
		this.files = listaArquivos(contrato);
	}

	public String getInformacoesDoInvestidorNoContrato(ContratoCobrancaResumoVO contrato) {
		// get files do contrato
		this.selectedContrato.setNumeroContrato(contrato.getNumeroContrato());

		this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
				contrato.getListContratoCobrancaParcelasInvestidorSelecionadoEnvelope());

		this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(
				contrato.getListContratoCobrancaParcelasInvestidorSelecionado());

		return null;
	}

	public void getInformacoesDoInvestidorNoContrato(ContratoCobranca contrato, ContratoCobrancaResumoVO contratoVO) {
		this.selectedContrato = contrato;

		// get valor do investidor no contrato
		if (contrato.getRecebedor() != null) {
			if (contrato.getRecebedor().getId() == this.idInvestidor) {
				if (contratoVO != null)
					contratoVO.setValorInvestidor(contrato.getVlrRecebedor());

				if (contrato.isRecebedorEnvelope()) {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
							this.selectedContrato.getListContratoCobrancaParcelasInvestidor1());
					if (contratoVO != null)
						contratoVO.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
								this.selectedContrato.getListContratoCobrancaParcelasInvestidor1());

				} else {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(
							this.selectedContrato.getListContratoCobrancaParcelasInvestidor1());
					if (contratoVO != null)
						contratoVO.setListContratoCobrancaParcelasInvestidorSelecionado(
								this.selectedContrato.getListContratoCobrancaParcelasInvestidor1());
				}
			}
		}

		if (contrato.getRecebedor2() != null) {
			if (contrato.getRecebedor2().getId() == this.idInvestidor) {
				if (contratoVO != null)
					contratoVO.setValorInvestidor(contrato.getVlrRecebedor2());

				if (contrato.isRecebedorEnvelope2()) {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
							this.selectedContrato.getListContratoCobrancaParcelasInvestidor2());
					if (contratoVO != null)
						contratoVO.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
								this.selectedContrato.getListContratoCobrancaParcelasInvestidor2());
				} else {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(
							this.selectedContrato.getListContratoCobrancaParcelasInvestidor2());
					if (contratoVO != null)
						contratoVO.setListContratoCobrancaParcelasInvestidorSelecionado(
								this.selectedContrato.getListContratoCobrancaParcelasInvestidor2());
				}
			}
		}

		if (contrato.getRecebedor3() != null) {
			if (contrato.getRecebedor3().getId() == this.idInvestidor) {
				if (contratoVO != null)
					contratoVO.setValorInvestidor(contrato.getVlrRecebedor3());

				if (contrato.isRecebedorEnvelope3()) {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
							this.selectedContrato.getListContratoCobrancaParcelasInvestidor3());
					if (contratoVO != null)
						contratoVO.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
								this.selectedContrato.getListContratoCobrancaParcelasInvestidor3());
				} else {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(
							this.selectedContrato.getListContratoCobrancaParcelasInvestidor3());
					if (contratoVO != null)
						contratoVO.setListContratoCobrancaParcelasInvestidorSelecionado(
								this.selectedContrato.getListContratoCobrancaParcelasInvestidor3());
				}
			}
		}

		if (contrato.getRecebedor4() != null) {
			if (contrato.getRecebedor4().getId() == this.idInvestidor) {
				if (contratoVO != null)
					contratoVO.setValorInvestidor(contrato.getVlrRecebedor4());

				if (contrato.isRecebedorEnvelope4()) {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
							this.selectedContrato.getListContratoCobrancaParcelasInvestidor4());
					if (contratoVO != null)
						contratoVO.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
								this.selectedContrato.getListContratoCobrancaParcelasInvestidor4());
				} else {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(
							this.selectedContrato.getListContratoCobrancaParcelasInvestidor4());
					if (contratoVO != null)
						contratoVO.setListContratoCobrancaParcelasInvestidorSelecionado(
								this.selectedContrato.getListContratoCobrancaParcelasInvestidor4());
				}
			}
		}

		if (contrato.getRecebedor5() != null) {
			if (contrato.getRecebedor5().getId() == this.idInvestidor) {
				if (contratoVO != null)
					contratoVO.setValorInvestidor(contrato.getVlrRecebedor5());

				if (contrato.isRecebedorEnvelope5()) {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
							this.selectedContrato.getListContratoCobrancaParcelasInvestidor5());
					if (contratoVO != null)
						contratoVO.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
								this.selectedContrato.getListContratoCobrancaParcelasInvestidor5());
				} else {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(
							this.selectedContrato.getListContratoCobrancaParcelasInvestidor5());
					if (contratoVO != null)
						contratoVO.setListContratoCobrancaParcelasInvestidorSelecionado(
								this.selectedContrato.getListContratoCobrancaParcelasInvestidor5());
				}
			}
		}

		if (contrato.getRecebedor6() != null) {
			if (contrato.getRecebedor6().getId() == this.idInvestidor) {
				if (contratoVO != null)
					contratoVO.setValorInvestidor(contrato.getVlrRecebedor6());

				if (contrato.isRecebedorEnvelope6()) {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
							this.selectedContrato.getListContratoCobrancaParcelasInvestidor6());
					if (contratoVO != null)
						contratoVO.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
								this.selectedContrato.getListContratoCobrancaParcelasInvestidor6());
				} else {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(
							this.selectedContrato.getListContratoCobrancaParcelasInvestidor6());
					if (contratoVO != null)
						contratoVO.setListContratoCobrancaParcelasInvestidorSelecionado(
								this.selectedContrato.getListContratoCobrancaParcelasInvestidor6());
				}
			}
		}

		if (contrato.getRecebedor7() != null) {
			if (contrato.getRecebedor7().getId() == this.idInvestidor) {
				if (contratoVO != null)
					contratoVO.setValorInvestidor(contrato.getVlrRecebedor7());

				if (contrato.isRecebedorEnvelope7()) {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
							this.selectedContrato.getListContratoCobrancaParcelasInvestidor7());
					if (contratoVO != null)
						contratoVO.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
								this.selectedContrato.getListContratoCobrancaParcelasInvestidor7());
				} else {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(
							this.selectedContrato.getListContratoCobrancaParcelasInvestidor7());
					if (contratoVO != null)
						contratoVO.setListContratoCobrancaParcelasInvestidorSelecionado(
								this.selectedContrato.getListContratoCobrancaParcelasInvestidor7());
				}
			}
		}

		if (contrato.getRecebedor8() != null) {
			if (contrato.getRecebedor8().getId() == this.idInvestidor) {
				if (contratoVO != null)
					contratoVO.setValorInvestidor(contrato.getVlrRecebedor8());

				if (contrato.isRecebedorEnvelope8()) {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
							this.selectedContrato.getListContratoCobrancaParcelasInvestidor8());
					if (contratoVO != null)
						contratoVO.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
								this.selectedContrato.getListContratoCobrancaParcelasInvestidor8());
				} else {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(
							this.selectedContrato.getListContratoCobrancaParcelasInvestidor8());
					if (contratoVO != null)
						contratoVO.setListContratoCobrancaParcelasInvestidorSelecionado(
								this.selectedContrato.getListContratoCobrancaParcelasInvestidor8());
				}
			}
		}

		if (contrato.getRecebedor9() != null) {
			if (contrato.getRecebedor9().getId() == this.idInvestidor) {
				if (contratoVO != null)
					contratoVO.setValorInvestidor(contrato.getVlrRecebedor9());

				if (contrato.isRecebedorEnvelope9()) {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
							this.selectedContrato.getListContratoCobrancaParcelasInvestidor9());
					if (contratoVO != null)
						contratoVO.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
								this.selectedContrato.getListContratoCobrancaParcelasInvestidor9());
				} else {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(
							this.selectedContrato.getListContratoCobrancaParcelasInvestidor9());
					if (contratoVO != null)
						contratoVO.setListContratoCobrancaParcelasInvestidorSelecionado(
								this.selectedContrato.getListContratoCobrancaParcelasInvestidor9());
				}
			}
		}

		if (contrato.getRecebedor10() != null) {
			if (contrato.getRecebedor10().getId() == this.idInvestidor) {
				if (contratoVO != null)
					contratoVO.setValorInvestidor(contrato.getVlrRecebedor10());

				if (contrato.isRecebedorEnvelope10()) {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
							this.selectedContrato.getListContratoCobrancaParcelasInvestidor10());
					if (contratoVO != null)
						contratoVO.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(
								this.selectedContrato.getListContratoCobrancaParcelasInvestidor10());
				} else {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(
							this.selectedContrato.getListContratoCobrancaParcelasInvestidor10());
					if (contratoVO != null)
						contratoVO.setListContratoCobrancaParcelasInvestidorSelecionado(
								this.selectedContrato.getListContratoCobrancaParcelasInvestidor10());
				}
			}
		}
	}

	public BigDecimal getValorInvestidoNoContrato(ContratoCobranca contrato) {
		BigDecimal valor = BigDecimal.ZERO;

		if (contrato.getRecebedorParcelaFinal1() != null) {
			if (contrato.getRecebedorParcelaFinal1().getId() == this.idInvestidor) {
				valor = contrato.getVlrFinalRecebedor1();
			}
		}

		if (contrato.getRecebedorParcelaFinal2() != null) {
			if (contrato.getRecebedorParcelaFinal2().getId() == this.idInvestidor) {
				valor = contrato.getVlrFinalRecebedor2();
			}
		}

		if (contrato.getRecebedorParcelaFinal3() != null) {
			if (contrato.getRecebedorParcelaFinal3().getId() == this.idInvestidor) {
				valor = contrato.getVlrFinalRecebedor3();
			}
		}

		if (contrato.getRecebedorParcelaFinal4() != null) {
			if (contrato.getRecebedorParcelaFinal4().getId() == this.idInvestidor) {
				valor = contrato.getVlrFinalRecebedor4();
			}
		}

		if (contrato.getRecebedorParcelaFinal5() != null) {
			if (contrato.getRecebedorParcelaFinal5().getId() == this.idInvestidor) {
				valor = contrato.getVlrFinalRecebedor5();
			}
		}

		return valor;
	}

	public String goToAlteraSenhaInvestidor() {

		return "./AlteraSenhaInvestidor.xhtml";
	}

	public User getUsuarioLogado() {
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			return usuarioLogado;
		} else {
			return null;
		}
	}

	/***
	 * Faz download de um único arquivo - linha do DataTable
	 * 
	 * @return
	 */
	public StreamedContent getDownloadFile() {
		if (this.selectedFile != null) {
			FileInputStream stream;
			try {
				stream = new FileInputStream(this.selectedFile.getFile().getAbsolutePath());
				downloadFile = new DefaultStreamedContent(stream, this.selectedFile.getPath(),
						this.selectedFile.getFile().getName());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("Cobrança - Download de Arquivos - Arquivo Não Encontrado");
			}
		}
		return this.downloadFile;
	}

	/***
	 * Exemplo de Zip de um Diretório inteiro
	 * 
	 * @param srcFolder
	 * @param destZipFile
	 * @throws Exception
	 */
	static public void zipFolder(String srcFolder, String destZipFile) throws Exception {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;

		fileWriter = new FileOutputStream(destZipFile);
		zip = new ZipOutputStream(fileWriter);

		addFolderToZip("", srcFolder, zip);
		zip.flush();
		zip.close();
	}

	/***
	 * Exemplo de adicionar arquivos a um zip existente
	 * 
	 * @param path
	 * @param srcFile
	 * @param zip
	 * @throws Exception
	 */
	static private void addFileToZip(String path, String srcFile, ZipOutputStream zip) throws Exception {

		File folder = new File(srcFile);
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
		} else {
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(folder.getName()));
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
		}
	}

	/**
	 * Exemplo de adicionar uam pasta a um zip existente
	 * 
	 * @param path
	 * @param srcFolder
	 * @param zip
	 * @throws Exception
	 */
	static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception {
		File folder = new File(srcFolder);

		for (String fileName : folder.list()) {
			if (path.equals("")) {
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
			} else {
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
			}
		}
	}

	public void viewFile(String fileName, ContratoCobranca contrato) {

		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
			BufferedInputStream input = null;
			BufferedOutputStream output = null;

			ParametrosDao pDao = new ParametrosDao();
			String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
					+ contrato.getNumeroContrato() + "/" + fileName;

			/*
			 * 'docx' =>
			 * 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
			 * 'xlsx' =>
			 * 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 'word'
			 * => 'application/msword', 'xls' => 'application/excel', 'pdf' =>
			 * 'application/pdf' 'psd' => 'application/x-photoshop'
			 */
			String mineFile = "";

			if (fileName.contains(".jpg") || fileName.contains(".JPG")) {
				mineFile = "image-jpg";
			}

			if (fileName.contains(".jpeg") || fileName.contains(".jpeg")) {
				mineFile = "image-jpeg";
			}

			if (fileName.contains(".png") || fileName.contains(".PNG")) {
				mineFile = "image-png";
			}

			if (fileName.contains(".pdf") || fileName.contains(".PDF")) {
				mineFile = "application/pdf";
			}

			File arquivo = new File(pathContrato);

			input = new BufferedInputStream(new FileInputStream(arquivo), 10240);

			response.reset();
			// lire un fichier pdf
			response.setHeader("Content-type", mineFile);

			response.setContentLength((int) arquivo.length());

			response.setHeader("Content-disposition", "inline; filename=" + arquivo.getName());
			output = new BufferedOutputStream(response.getOutputStream(), 10240);

			// Write file contents to response.
			byte[] buffer = new byte[10240];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}

			// Finalize task.
			output.flush();
			output.close();
			facesContext.responseComplete();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}

	public void habilitaFiltrosContrato() {
		loadContratos();

		if (!this.updateMode) {
			if (this.filtraContrato) {
				clearContrato();
			} else {
				this.filtroNumeroContrato = "";
			}

			this.selectedContratoLov = new ContratoCobranca();
		}
	}

	public final void loadContratos() {
		this.listContratos = new ArrayList<ContratoCobranca>();

		ContratoCobrancaDao contratoDao = new ContratoCobrancaDao();

		this.listContratos = contratoDao.consultaContratosAprovados();
	}

	public final void loadContratosByNumero(String numeroContrato) {
		List<ContratoCobranca> listContratosTmp = new ArrayList<ContratoCobranca>();

		ContratoCobrancaDao contratoDao = new ContratoCobrancaDao();

		Map<String, Object> filtros = new HashMap<String, Object>();
		filtros.put("status", "Aprovado");
		filtros.put("numeroContrato", numeroContrato);

		listContratosTmp = contratoDao.findByFilter(filtros);

		if (listContratosTmp.size() > 0) {
			this.selectedContratoLov = listContratosTmp.get(0);
		}
	}

	public final String clearFieldsOperacoesIndividualizado() {
		this.dataInicio = gerarDataHoje();
		this.dataFim = gerarDataHoje();

		clearOperacoesIndividualizadoPDFParams();

		return "/Atendimento/Cobranca/OperacoesIndividualizadoConsultar.xhtml";
	}

	public void clearOperacoesIndividualizadoPDFParams() {
		this.operacoesIndividualizadoPDF = null;
		this.operacoesIndividualizadoPDFGerado = false;
		this.operacoesIndividualizadoPDFStr = "";

		// this.debenturesInvestidor = new DebenturesInvestidor();

		// this.totalValorFace = BigDecimal.ZERO.setScale(2);
		this.totalBruto = BigDecimal.ZERO.setScale(2);
		this.totalLiquido = BigDecimal.ZERO.setScale(2);
		this.totalDesagio = BigDecimal.ZERO.setScale(2);
		this.prazoMedioTotal = 0;

		this.contratos = new ArrayList<ContratoCobranca>();
		this.listOperacoesIndividualizado = new ArrayList<OperacoesIndividualizado>();
	}

	public void consultaOperacoesIndividualizado() {
		clearOperacoesIndividualizadoPDFParams();

		ContratoCobrancaDao ccDao = new ContratoCobrancaDao();
		this.contratos = ccDao.getContratoPorDataContrato(this.dataInicio, this.dataFim);

		OperacoesIndividualizado oi = new OperacoesIndividualizado();

		for (ContratoCobranca cc : this.contratos) {
			oi = new OperacoesIndividualizado();

			oi.setContrato(cc);
			oi.setCedente("Money Plus Sociedade");
			oi.setPrazoMedio(calculaPrazoMedio(cc));
			oi.setValorBruto(getValorBrutoParcelaContrato(cc));

			if (cc.getValorCCB() == null) {
				oi.setValorLiquido(BigDecimal.ZERO);
			} else {
				oi.setValorLiquido(cc.getValorCCB());
			}

			oi.setDesagio(oi.getValorBruto().subtract(oi.getValorLiquido()));
			this.listOperacoesIndividualizado.add(oi);

			// calcula totais
			this.totalBruto = this.totalBruto.add(oi.getValorBruto());
			this.totalLiquido = this.totalLiquido.add(oi.getValorLiquido());
			this.totalDesagio = this.totalDesagio.add(oi.getDesagio());
			this.prazoMedioTotal = this.prazoMedioTotal + oi.getPrazoMedio();
		}

		if (this.prazoMedioTotal > 0) {
			if (this.contratos.size() > 0) {
				this.prazoMedioTotal = this.prazoMedioTotal / this.contratos.size();
			}
		}
	}

	public BigDecimal getValorBrutoParcelaContrato(ContratoCobranca contrato) {
		// soma o valor de todas as parcelas como valor bruto
		BigDecimal valorBruto = BigDecimal.ZERO;

		if (contrato.getListContratoCobrancaDetalhes().size() > 0) {
			for (ContratoCobrancaDetalhes ccd : contrato.getListContratoCobrancaDetalhes()) {
				valorBruto = valorBruto.add(ccd.getVlrParcela());
			}
		}

		return valorBruto;
	}

	public long calculaPrazoMedio(ContratoCobranca contrato) {
		// pega a data da primeira parcela e data da última parcela,
		// calcula a quantidade de dias neste período

		Date dataInicioParcelas = gerarDataHoje();
		Date dataFimParcelas = gerarDataHoje();

		if (contrato.getListContratoCobrancaDetalhes().size() > 0) {
			dataInicioParcelas = contrato.getListContratoCobrancaDetalhes().get(0).getDataVencimento();
			dataFimParcelas = contrato.getListContratoCobrancaDetalhes()
					.get(contrato.getListContratoCobrancaDetalhes().size() - 1).getDataVencimento();
		}

		long prazoMedio = calculaDiasParcelas(dataInicioParcelas, dataFimParcelas);

		return prazoMedio;
	}

	public long calculaDiasParcelas(Date dataInicioParcelas, Date dataFimParcelas) {
		long qtdeDias = Days.daysBetween(new DateTime(dataInicioParcelas), new DateTime(dataFimParcelas)).getDays();

		return qtdeDias;
	}

	public void geraXLSDebenturesEmitidas() throws IOException {
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

		// iterating r number of rows
		// cria CABEÇALHO
		int countLine = 0;
		XSSFRow row = sheet.createRow(countLine);
		XSSFCell cell;
		cell = row.createCell(0);
		cell.setCellValue("Emissão");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("Debenturista");
		cell.setCellStyle(cell_style);
		cell = row.createCell(2);
		cell.setCellValue("Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(3);
		cell.setCellValue("Valor Debenture");
		cell.setCellStyle(cell_style);
		cell = row.createCell(4);
		cell.setCellValue("Taxa");
		cell.setCellStyle(cell_style);
		cell = row.createCell(5);
		cell.setCellValue("Prazo");
		cell.setCellStyle(cell_style);
		cell = row.createCell(6);
		cell.setCellValue("Vlr. Parcela Mensal");
		cell.setCellStyle(cell_style);
		cell = row.createCell(7);
		cell.setCellValue("Vlr. Parcela Final");
		cell.setCellStyle(cell_style);
		cell = row.createCell(8);
		cell.setCellValue("Data Última Parcela");
		cell.setCellStyle(cell_style);
		cell = row.createCell(9);
		cell.setCellValue("Quitado");
		cell.setCellStyle(cell_style);

		cell = row.createCell(10);
		cell.setCellValue("Data ultima Pacela Paga");
		cell.setCellStyle(cell_style);

		cell = row.createCell(11);
		cell.setCellValue("Valor ultima Parcela Paga");
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

		for (DebenturesInvestidor record : this.listDebenturesInvestidor) {
			countLine++;
			row = sheet.createRow(countLine);

			// Emissao
			cell = row.createCell(0);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getDataDebentures());

			// Debenturista
			cell = row.createCell(1);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getRecebedor().getNome());

			// Contrato
			cell = row.createCell(2);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getContrato().getNumeroContrato());

			// Valor Debenture
			cell = row.createCell(3);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getValorDebenture() != null) {
				cell.setCellValue(((BigDecimal) record.getValorDebenture()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Taxa
			cell = row.createCell(4);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getTaxa() != null) {
				cell.setCellValue(((BigDecimal) record.getTaxa()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Prazo
			cell = row.createCell(5);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getPrazo());

			// Parcela Mensal
			cell = row.createCell(6);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getParcelaMensal() != null) {
				cell.setCellValue(((BigDecimal) record.getParcelaMensal()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Parcela Final
			cell = row.createCell(7);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getParcelaFinal() != null) {
				cell.setCellValue(((BigDecimal) record.getParcelaFinal()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Data Ultima PArcela
			cell = row.createCell(8);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getDataUltimaParcela());

			// Quitado
			cell = row.createCell(9);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getQuitado());

			// Data Ultima Parcela paga
			cell = row.createCell(10);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getDataUltimaParcelaPaga());

			// Valor Ultima Parcela paga
			cell = row.createCell(11);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getValorUltimaParcelaPaga() != null) {
				cell.setCellValue(((BigDecimal) record.getValorUltimaParcelaPaga()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

		}

		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		// write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();

		this.debenturesEmitidasXLSGerado = true;
	}

	public void geraXLSValorLiquidoInvestidores() throws IOException {
		ParametrosDao pDao = new ParametrosDao();
		this.pathPDF = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomePDF = "Relatorio Valor Liquido - Debetures Emitidas.xlsx";

		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		dataHoje.set(Calendar.HOUR_OF_DAY, 0);
		dataHoje.set(Calendar.MINUTE, 0);
		dataHoje.set(Calendar.SECOND, 0);
		dataHoje.set(Calendar.MILLISECOND, 0);

		// dataHoje.add(Calendar.DAY_OF_MONTH, 1);

		String excelFileName = this.pathPDF + this.nomePDF;// name of excel file

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

		// iterating r number of rows
		// cria CABEÇALHO
		int countLine = 0;
		XSSFRow row = sheet.createRow(countLine);
		XSSFCell cell;
		cell = row.createCell(0);
		cell.setCellValue("Investidor");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("CPF/CNPJ");
		cell.setCellStyle(cell_style);
		cell = row.createCell(2);
		cell.setCellValue("Agência / Conta");
		cell.setCellStyle(cell_style);
		cell = row.createCell(3);
		cell.setCellValue("Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(4);
		cell.setCellValue("Pagador");
		cell.setCellStyle(cell_style);
		cell = row.createCell(5);
		cell.setCellValue("Garantido");
		cell.setCellStyle(cell_style);
		cell = row.createCell(6);
		cell.setCellValue("Data Vencimento");
		cell.setCellStyle(cell_style);
		cell = row.createCell(7);
		cell.setCellValue("Em dia");
		cell.setCellStyle(cell_style);
		cell = row.createCell(8);
		cell.setCellValue("Valor Bruto da Parcela");
		cell.setCellStyle(cell_style);
		cell = row.createCell(9);
		cell.setCellValue("Valor Líquido a Receber");
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

		for (ContratoCobrancaParcelasInvestidor record : this.parcelasInvestidorSA) {
			countLine++;
			row = sheet.createRow(countLine);

			// Investidor
			cell = row.createCell(0);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getInvestidor().getNome());

			// CPF CNPJ
			cell = row.createCell(1);
			cell.setCellStyle(cell_style);

			if (record.getInvestidor().getCpf() != null) {
				cell.setCellValue(record.getInvestidor().getCpf());
			} else {
				cell.setCellValue(record.getInvestidor().getCnpj());
			}

			// Agencia Conta
			cell = row.createCell(2);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getInvestidor().getAgencia() + " | " + record.getInvestidor().getConta());

			// Contrato
			cell = row.createCell(3);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getNumeroContrato());

			// Pagador
			cell = row.createCell(4);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getPagador().getNome());

			// Garantido
			cell = row.createCell(5);
			cell.setCellStyle(cell_style);

			if (record.isInvestidorGarantido()) {
				cell.setCellValue("Sim");
			} else {
				cell.setCellValue("Não");
			}

			// Data Vencimento
			cell = row.createCell(6);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getDataVencimento());

			// Em dia
			cell = row.createCell(7);
			cell.setCellStyle(cell_style);

			if (!record.isParcelaContratoVencida()) {
				cell.setCellValue("Sim");
			} else {
				cell.setCellValue("Não");
			}

			// Valor Bruto Parcela
			cell = row.createCell(8);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getParcelaMensalBaixa() != null) {
				cell.setCellValue(((BigDecimal) record.getParcelaMensalBaixa()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Valor Líquido a Receber
			cell = row.createCell(9);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getValorLiquidoBaixa() != null) {
				cell.setCellValue(((BigDecimal) record.getValorLiquidoBaixa()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}
		}

		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		// write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();

		this.valoresLiquidosInvestidoresPDFGerado = true;
	}

	public void geraXLSValorLiquidoInvestidoresRelatorio() throws IOException {
		ParametrosDao pDao = new ParametrosDao();
		this.pathPDF = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomePDF = "Relatorio Valor Liquido - Debetures Emitidas.xlsx";

		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		dataHoje.set(Calendar.HOUR_OF_DAY, 0);
		dataHoje.set(Calendar.MINUTE, 0);
		dataHoje.set(Calendar.SECOND, 0);
		dataHoje.set(Calendar.MILLISECOND, 0);

		// dataHoje.add(Calendar.DAY_OF_MONTH, 1);

		String excelFileName = this.pathPDF + this.nomePDF;// name of excel file

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

		// iterating r number of rows
		// cria CABEÇALHO
		int countLine = 0;
		XSSFRow row = sheet.createRow(countLine);
		XSSFCell cell;
		cell = row.createCell(0);
		cell.setCellValue("Investidor");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("CPF/CNPJ");
		cell.setCellStyle(cell_style);
		cell = row.createCell(2);
		cell.setCellValue("Agência / Conta");
		cell.setCellStyle(cell_style);
		cell = row.createCell(3);
		cell.setCellValue("Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(4);
		cell.setCellValue("Pagador");
		cell.setCellStyle(cell_style);
		cell = row.createCell(5);
		cell.setCellValue("Garantido");
		cell.setCellStyle(cell_style);
		cell = row.createCell(6);
		cell.setCellValue("Data Vencimento");
		cell.setCellStyle(cell_style);
		cell = row.createCell(7);
		cell.setCellValue("Em dia");
		cell.setCellStyle(cell_style);
		cell = row.createCell(8);
		cell.setCellValue("Taxa Remuneração");
		cell.setCellStyle(cell_style);
		cell = row.createCell(9);
		cell.setCellValue("Antecipação?");
		cell.setCellStyle(cell_style);
		cell = row.createCell(10);
		cell.setCellValue("Recebe Juros Mensal?");
		cell.setCellStyle(cell_style);
		cell = row.createCell(11);
		cell.setCellValue("Valor Bruto da Parcela");
		cell.setCellStyle(cell_style);
		cell = row.createCell(12);
		cell.setCellValue("Valor Líquido a Receber");
		cell.setCellStyle(cell_style);
		cell = row.createCell(13);
		cell.setCellValue("Valor Face");
		cell.setCellStyle(cell_style);
		cell = row.createCell(14);
		cell.setCellValue("Saldo Credor");
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

		for (ContratoCobrancaParcelasInvestidor record : this.parcelasInvestidorSA) {
			countLine++;
			row = sheet.createRow(countLine);

			// Investidor
			cell = row.createCell(0);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getInvestidor().getNome());

			// CPF CNPJ
			cell = row.createCell(1);
			cell.setCellStyle(cell_style);

			if (record.getInvestidor().getCpf() != null) {
				cell.setCellValue(record.getInvestidor().getCpf());
			} else {
				cell.setCellValue(record.getInvestidor().getCnpj());
			}

			// Agencia Conta
			cell = row.createCell(2);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getInvestidor().getAgencia() + " | " + record.getInvestidor().getConta());

			// Contrato
			cell = row.createCell(3);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getNumeroContrato());

			// Pagador
			cell = row.createCell(4);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getPagador().getNome());

			// Garantido
			cell = row.createCell(5);
			cell.setCellStyle(cell_style);

			if (record.isInvestidorGarantido()) {
				cell.setCellValue("Sim");
			} else {
				cell.setCellValue("Não");
			}

			// Data Vencimento
			cell = row.createCell(6);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getDataVencimento());

			// Em dia
			cell = row.createCell(7);
			cell.setCellStyle(cell_style);

			if (!record.isParcelaContratoVencida()) {
				cell.setCellValue("Sim");
			} else {
				cell.setCellValue("Não");
			}

			// Taxa Remuneração
			cell = row.createCell(8);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getTxRemuneracao() != null) {
				cell.setCellValue(((BigDecimal) record.getTxRemuneracao()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Antecipação?
			cell = row.createCell(9);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getTipoParcela());

			// Recebe Juros Mensal?
			cell = row.createCell(10);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getRecebeJurosMensal());

			// Valor Bruto Parcela
			cell = row.createCell(11);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getParcelaMensalBaixa() != null) {
				cell.setCellValue(((BigDecimal) record.getParcelaMensalBaixa()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Valor Líquido a Receber
			cell = row.createCell(12);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getValorLiquidoBaixa() != null) {
				cell.setCellValue(((BigDecimal) record.getValorLiquidoBaixa()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Valor Face
			cell = row.createCell(13);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getValorFace() != null) {
				cell.setCellValue(((BigDecimal) record.getValorFace()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Saldo Credor
			cell = row.createCell(14);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getSaldoCredorAtualizado() != null) {
				cell.setCellValue(((BigDecimal) record.getSaldoCredorAtualizado()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}
		}

		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		// write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();

		this.valoresLiquidosInvestidoresPDFGerado = true;
	}

	public void geraXLSValorLiquidoCorrespondente() throws IOException {
		ParametrosDao pDao = new ParametrosDao();
		this.pathPDF = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomePDF = "Relatorio Pagto Valor Liquido Correspondente - Debetures Emitidas.xlsx";

		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		dataHoje.set(Calendar.HOUR_OF_DAY, 0);
		dataHoje.set(Calendar.MINUTE, 0);
		dataHoje.set(Calendar.SECOND, 0);
		dataHoje.set(Calendar.MILLISECOND, 0);

		// dataHoje.add(Calendar.DAY_OF_MONTH, 1);

		String excelFileName = this.pathPDF + this.nomePDF;// name of excel file

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

		// iterating r number of rows
		// cria CABEÇALHO
		int countLine = 0;
		XSSFRow row = sheet.createRow(countLine);
		XSSFCell cell;
		cell = row.createCell(0);
		cell.setCellValue("Investidor");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("CPF/CNPJ");
		cell.setCellStyle(cell_style);
		cell = row.createCell(2);
		cell.setCellValue("Agência / Conta");
		cell.setCellStyle(cell_style);
		cell = row.createCell(3);
		cell.setCellValue("Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(4);
		cell.setCellValue("Pagador");
		cell.setCellStyle(cell_style);
		cell = row.createCell(5);
		cell.setCellValue("Garantido");
		cell.setCellStyle(cell_style);
		cell = row.createCell(6);
		cell.setCellValue("Data Vencimento");
		cell.setCellStyle(cell_style);
		cell = row.createCell(7);
		cell.setCellValue("Em dia");
		cell.setCellStyle(cell_style);
		cell = row.createCell(8);
		cell.setCellValue("Valor Bruto da Parcela");
		cell.setCellStyle(cell_style);
		cell = row.createCell(9);
		cell.setCellValue("Valor Líquido a Receber");
		cell.setCellStyle(cell_style);
		cell = row.createCell(10);
		cell.setCellValue("Valor Face");
		cell.setCellStyle(cell_style);
		cell = row.createCell(11);
		cell.setCellValue("Saldo Credor");
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

		for (ContratoCobrancaParcelasInvestidor record : this.parcelasInvestidorCorrespondente) {
			countLine++;
			row = sheet.createRow(countLine);

			// Investidor
			cell = row.createCell(0);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getInvestidor().getNome());

			// CPF CNPJ
			cell = row.createCell(1);
			cell.setCellStyle(cell_style);

			if (record.getInvestidor().getCpf() != null) {
				cell.setCellValue(record.getInvestidor().getCpf());
			} else {
				cell.setCellValue(record.getInvestidor().getCnpj());
			}

			// Agencia Conta
			cell = row.createCell(2);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getInvestidor().getAgencia() + " | " + record.getInvestidor().getConta());

			// Contrato
			cell = row.createCell(3);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getNumeroContrato());

			// Pagador
			cell = row.createCell(4);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getPagador().getNome());

			// Garantido
			cell = row.createCell(5);
			cell.setCellStyle(cell_style);

			if (record.isInvestidorGarantido()) {
				cell.setCellValue("Sim");
			} else {
				cell.setCellValue("Não");
			}

			// Data Vencimento
			cell = row.createCell(6);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getDataVencimento());

			// Em dia
			cell = row.createCell(7);
			cell.setCellStyle(cell_style);

			if (!record.isParcelaContratoVencida()) {
				cell.setCellValue("Sim");
			} else {
				cell.setCellValue("Não");
			}

			// Valor Bruto Parcela
			cell = row.createCell(8);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getParcelaMensalBaixa() != null) {
				cell.setCellValue(((BigDecimal) record.getParcelaMensalBaixa()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Valor Líquido a Receber
			cell = row.createCell(9);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getValorLiquidoBaixa() != null) {
				cell.setCellValue(((BigDecimal) record.getValorLiquidoBaixa()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Valor Face
			cell = row.createCell(10);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getValorFace() != null) {
				cell.setCellValue(((BigDecimal) record.getValorFace()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Saldo Credor
			cell = row.createCell(11);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getSaldoCredorAtualizado() != null) {
				cell.setCellValue(((BigDecimal) record.getSaldoCredorAtualizado()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}
		}

		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		// write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();

		this.valoresLiquidosInvestidoresPDFGerado = true;
	}

	public void geraXLSValorLiquidoEnvelopeRelatorio() throws IOException {
		ParametrosDao pDao = new ParametrosDao();
		this.pathPDF = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomePDF = "Relatorio Pagto Valor Liquido Envelope - Debetures Emitidas.xlsx";

		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		dataHoje.set(Calendar.HOUR_OF_DAY, 0);
		dataHoje.set(Calendar.MINUTE, 0);
		dataHoje.set(Calendar.SECOND, 0);
		dataHoje.set(Calendar.MILLISECOND, 0);

		// dataHoje.add(Calendar.DAY_OF_MONTH, 1);

		String excelFileName = this.pathPDF + this.nomePDF;// name of excel file

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

		// iterating r number of rows
		// cria CABEÇALHO
		int countLine = 0;
		XSSFRow row = sheet.createRow(countLine);
		XSSFCell cell;
		cell = row.createCell(0);
		cell.setCellValue("Investidor");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("CPF/CNPJ");
		cell.setCellStyle(cell_style);
		cell = row.createCell(2);
		cell.setCellValue("Agência / Conta");
		cell.setCellStyle(cell_style);
		cell = row.createCell(3);
		cell.setCellValue("Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(4);
		cell.setCellValue("Pagador");
		cell.setCellStyle(cell_style);
		cell = row.createCell(5);
		cell.setCellValue("Garantido");
		cell.setCellStyle(cell_style);
		cell = row.createCell(6);
		cell.setCellValue("Data Vencimento");
		cell.setCellStyle(cell_style);
		cell = row.createCell(7);
		cell.setCellValue("Em dia");
		cell.setCellStyle(cell_style);
		cell = row.createCell(8);
		cell.setCellValue("Valor Bruto da Parcela");
		cell.setCellStyle(cell_style);
		cell = row.createCell(9);
		cell.setCellValue("Valor Líquido a Receber");
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

		for (ContratoCobrancaParcelasInvestidor record : this.parcelasInvestidorEnvelope) {
			countLine++;
			row = sheet.createRow(countLine);

			// Investidor
			cell = row.createCell(0);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getInvestidor().getNome());

			// CPF CNPJ
			cell = row.createCell(1);
			cell.setCellStyle(cell_style);

			if (record.getInvestidor().getCpf() != null) {
				cell.setCellValue(record.getInvestidor().getCpf());
			} else {
				cell.setCellValue(record.getInvestidor().getCnpj());
			}

			// Agencia Conta
			cell = row.createCell(2);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getInvestidor().getAgencia() + " | " + record.getInvestidor().getConta());

			// Contrato
			cell = row.createCell(3);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getNumeroContrato());

			// Pagador
			cell = row.createCell(4);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getPagador().getNome());

			// Garantido
			cell = row.createCell(5);
			cell.setCellStyle(cell_style);

			if (record.isInvestidorGarantido()) {
				cell.setCellValue("Sim");
			} else {
				cell.setCellValue("Não");
			}

			// Data Vencimento
			cell = row.createCell(6);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getDataVencimento());

			// Em dia
			cell = row.createCell(7);
			cell.setCellStyle(cell_style);

			if (!record.isParcelaContratoVencida()) {
				cell.setCellValue("Sim");
			} else {
				cell.setCellValue("Não");
			}

			// Valor Bruto Parcela
			cell = row.createCell(8);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getParcelaMensalBaixa() != null) {
				cell.setCellValue(((BigDecimal) record.getParcelaMensalBaixa()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Valor Líquido a Receber
			cell = row.createCell(9);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getValorLiquidoBaixa() != null) {
				cell.setCellValue(((BigDecimal) record.getValorLiquidoBaixa()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}
		}

		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		// write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();

		this.valoresLiquidosInvestidoresPDFGerado = true;
	}

	public void geraXLSValorLiquidoEnvelope() throws IOException {
		ParametrosDao pDao = new ParametrosDao();
		this.pathPDF = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomePDF = "Relatorio Pagto Valor Liquido Envelope - Debetures Emitidas.xlsx";

		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		dataHoje.set(Calendar.HOUR_OF_DAY, 0);
		dataHoje.set(Calendar.MINUTE, 0);
		dataHoje.set(Calendar.SECOND, 0);
		dataHoje.set(Calendar.MILLISECOND, 0);

		// dataHoje.add(Calendar.DAY_OF_MONTH, 1);

		String excelFileName = this.pathPDF + this.nomePDF;// name of excel file

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

		// iterating r number of rows
		// cria CABEÇALHO
		int countLine = 0;
		XSSFRow row = sheet.createRow(countLine);
		XSSFCell cell;
		cell = row.createCell(0);
		cell.setCellValue("Investidor");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("CPF/CNPJ");
		cell.setCellStyle(cell_style);
		cell = row.createCell(2);
		cell.setCellValue("Agência / Conta");
		cell.setCellStyle(cell_style);
		cell = row.createCell(3);
		cell.setCellValue("Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(4);
		cell.setCellValue("Pagador");
		cell.setCellStyle(cell_style);
		cell = row.createCell(5);
		cell.setCellValue("Garantido");
		cell.setCellStyle(cell_style);
		cell = row.createCell(6);
		cell.setCellValue("Data Vencimento");
		cell.setCellStyle(cell_style);
		cell = row.createCell(7);
		cell.setCellValue("Em dia");
		cell.setCellStyle(cell_style);
		cell = row.createCell(8);
		cell.setCellValue("Valor Bruto da Parcela");
		cell.setCellStyle(cell_style);
		cell = row.createCell(9);
		cell.setCellValue("Valor Líquido a Receber");
		cell.setCellStyle(cell_style);
		cell = row.createCell(10);
		cell.setCellValue("Valor Face");
		cell.setCellStyle(cell_style);
		cell = row.createCell(11);
		cell.setCellValue("Saldo Credor");
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

		for (ContratoCobrancaParcelasInvestidor record : this.parcelasInvestidorEnvelope) {
			countLine++;
			row = sheet.createRow(countLine);

			// Investidor
			cell = row.createCell(0);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getInvestidor().getNome());

			// CPF CNPJ
			cell = row.createCell(1);
			cell.setCellStyle(cell_style);

			if (record.getInvestidor().getCpf() != null) {
				cell.setCellValue(record.getInvestidor().getCpf());
			} else {
				cell.setCellValue(record.getInvestidor().getCnpj());
			}

			// Agencia Conta
			cell = row.createCell(2);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getInvestidor().getAgencia() + " | " + record.getInvestidor().getConta());

			// Contrato
			cell = row.createCell(3);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getNumeroContrato());

			// Pagador
			cell = row.createCell(4);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getPagador().getNome());

			// Garantido
			cell = row.createCell(5);
			cell.setCellStyle(cell_style);

			if (record.isInvestidorGarantido()) {
				cell.setCellValue("Sim");
			} else {
				cell.setCellValue("Não");
			}

			// Data Vencimento
			cell = row.createCell(6);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getDataVencimento());

			// Em dia
			cell = row.createCell(7);
			cell.setCellStyle(cell_style);

			if (!record.isParcelaContratoVencida()) {
				cell.setCellValue("Sim");
			} else {
				cell.setCellValue("Não");
			}

			// Valor Bruto Parcela
			cell = row.createCell(8);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getParcelaMensalBaixa() != null) {
				cell.setCellValue(((BigDecimal) record.getParcelaMensalBaixa()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Valor Líquido a Receber
			cell = row.createCell(9);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getValorLiquidoBaixa() != null) {
				cell.setCellValue(((BigDecimal) record.getValorLiquidoBaixa()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Valor Face
			cell = row.createCell(10);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getValorFace() != null) {
				cell.setCellValue(((BigDecimal) record.getValorFace()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Saldo Credor
			cell = row.createCell(11);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getSaldoCredorAtualizado() != null) {
				cell.setCellValue(((BigDecimal) record.getSaldoCredorAtualizado()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}
		}

		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		// write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();

		this.valoresLiquidosInvestidoresPDFGerado = true;
	}

	public void geraXLSRelatorioDebenturesEmitidas() throws IOException {
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

		// iterating r number of rows
		// cria CABEÇALHO
		int countLine = 0;
		XSSFRow row = sheet.createRow(countLine);
		XSSFCell cell;
		cell = row.createCell(0);
		cell.setCellValue("Debenturista");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(2);
		cell.setCellValue("Emissão");
		cell.setCellStyle(cell_style);
		cell = row.createCell(3);
		cell.setCellValue("Prazo");
		cell.setCellStyle(cell_style);
		cell = row.createCell(4);
		cell.setCellValue("Valor Face");
		cell.setCellStyle(cell_style);
		cell = row.createCell(5);
		cell.setCellValue("Taxa Remuneração");
		cell.setCellStyle(cell_style);
		cell = row.createCell(6);
		cell.setCellValue("Carência");
		cell.setCellStyle(cell_style);
		cell = row.createCell(7);
		cell.setCellValue("Mensal");
		cell.setCellStyle(cell_style);
		cell = row.createCell(8);
		cell.setCellValue("Valor Parcela Mensal");
		cell.setCellStyle(cell_style);
		cell = row.createCell(9);
		cell.setCellValue("Valor Líquido");
		cell.setCellStyle(cell_style);
		cell = row.createCell(10);
		cell.setCellValue("Data Última Parcela");
		cell.setCellStyle(cell_style);
		cell = row.createCell(11);
		cell.setCellValue("Valor Última Parcela");
		cell.setCellStyle(cell_style);
		cell = row.createCell(12);
		cell.setCellValue("Quitado");
		cell.setCellStyle(cell_style);
		cell = row.createCell(13);
		cell.setCellValue("Data Quitação");
		cell.setCellStyle(cell_style);
		cell = row.createCell(14);
		cell.setCellValue("Tipo de Cálculo");
		cell.setCellStyle(cell_style);
		cell = row.createCell(15);
		cell.setCellValue("Garantido");
		cell.setCellStyle(cell_style);
		cell = row.createCell(16);
		cell.setCellValue("Saldo Credor");
		cell.setCellStyle(cell_style);
		cell = row.createCell(17);
		cell.setCellValue("Número Parcela");
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

		for (DebenturesInvestidor record : this.listDebenturesInvestidor) {
			countLine++;
			row = sheet.createRow(countLine);

			// Debenturista
			cell = row.createCell(0);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getRecebedor().getNome());

			// Contrato
			cell = row.createCell(1);
			cell.setCellStyle(cell_style);
			if (record.getContrato() != null) {
				cell.setCellValue(record.getContrato().getNumeroContrato());
			}

			// Emissao
			cell = row.createCell(2);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getDataDebentures());

			// Prazo
			cell = row.createCell(3);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getPrazo());

			// Valor Face
			cell = row.createCell(4);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getValorFace() != null) {
				cell.setCellValue(((BigDecimal) record.getValorFace()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Taxa
			cell = row.createCell(5);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getTaxa() != null) {
				cell.setCellValue(((BigDecimal) record.getTaxa()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Meses Carência
			cell = row.createCell(6);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getMesesCarencia());

			// Mensal
			cell = row.createCell(7);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getPagamentoMensal());

			// Valor Parcela Mensal
			cell = row.createCell(8);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getParcelaMensal() != null) {
				cell.setCellValue(record.getParcelaMensal().doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Valor Liquido
			cell = row.createCell(9);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getValorLiquido() != null) {
				cell.setCellValue(record.getValorLiquido().doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Data Última Parcela
			cell = row.createCell(10);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getDataUltimaParcela());

			// Valor Última Parcela
			cell = row.createCell(11);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			if (record.getParcelaFinal() != null) {
				cell.setCellValue(record.getParcelaFinal().doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0.00"));
			}

			// Quitado
			cell = row.createCell(12);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getQuitado());

			// Data Quitação
			cell = row.createCell(13);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getDataQuitacao());

			// Tipo de Cálculo
			cell = row.createCell(14);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getTipoCalculo());

			// Garantido
			cell = row.createCell(15);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getGarantido());

			if (record.getParcelaInvestidor() != null) {
				if (record.getParcelaInvestidor().getId() > 0) {
					// Saldo Credor
					cell = row.createCell(16);
					cell.setCellStyle(numericStyle);
					cell.setCellType(CellType.NUMERIC);
					if (record.getParcelaFinal() != null) {
						cell.setCellValue(record.getParcelaInvestidor().getSaldoCredorAtualizado().doubleValue());
					} else {
						cell.setCellValue(Double.valueOf("0.00"));
					}

					// Número Parcela
					cell = row.createCell(17);
					cell.setCellStyle(numericStyle);
					cell.setCellType(CellType.NUMERIC);
					cell.setCellValue(record.getParcelaInvestidor().getNumeroParcela());
				}
			}

		}

		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		// write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();

		this.debenturesEmitidasXLSGerado = true;
	}

	public StreamedContent geraPDFInformeRendimentos() {
		/*
		 * this.transferenciasObservacoesIUGU = new TransferenciasObservacoesIUGU();
		 * this.transferenciasObservacoesIUGU.setId(1);
		 * this.transferenciasObservacoesIUGU.setIdTransferencia(
		 * "jdsfhdsfhjskfhjhslafdshf"); this.transferenciasObservacoesIUGU.
		 * setObservacao("asdklfhjksdhfjd dsjfhjhdsfjashgdfj ");
		 * 
		 * this.valorItem = new BigDecimal("30000.00");
		 */
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("#,##0.00");
		Locale.setDefault(new Locale("pt", "BR"));

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.
		 * VGpT0_nF_h4
		 */

		Document doc = null;
		OutputStream os = null;
		// ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			/*
			 * Fonts Utilizadas no PDF
			 */
			Font headerBig = new Font(FontFamily.HELVETICA, 14, Font.BOLD);
			Font header = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			Font normal12 = new Font(FontFamily.HELVETICA, 12);
			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font tituloBranco = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloBranco.setColor(BaseColor.WHITE);
			Font normal = new Font(FontFamily.HELVETICA, 10);
			Font normalSmall = new Font(FontFamily.HELVETICA, 8);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getDefault();
			Locale locale = new Locale("pt", "BR");
			Calendar date = Calendar.getInstance(zone, locale);
			SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MMM/yyyy", locale);
			SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", locale);

			ParametrosDao pDao = new ParametrosDao();

			doc = new Document(PageSize.A4, 10, 10, 10, 10);
			this.nomePDF = "Informe de rendimentos - " + this.selectedPagador.getNome() + ".pdf";
			this.pathPDF = pDao.findByFilter("nome", "ARQUIVOS_PDF").get(0).getValorString();

			os = new FileOutputStream(this.pathPDF + this.nomePDF);

			// Associa a stream de saída ao
			PdfWriter writer = PdfWriter.getInstance(doc, os);

			// Abre o documento
			doc.open();
			/*
			 * Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			 * p1.setAlignment(Element.ALIGN_CENTER); p1.setSpacingAfter(10); doc.add(p1);
			 */
			PdfPTable table = new PdfPTable(new float[] { 0.2f, 0.3f, 0.3f, 0.2f, 0.2f, 0.2f, 0.2f });
			table.setWidthPercentage(100.0f);

			PdfPCell cell1;

			// adiciona cabeçalho e rodapé
			if (this.imprimirHeaderFooter) {
				PDFCabecalhoRodapeInformeRendimentos event = new PDFCabecalhoRodapeInformeRendimentos();
				writer.setPageEvent(event);

				BufferedImage buff = ImageIO.read(getClass().getResourceAsStream("/resource/logo-galleria-ok.png"));
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ImageIO.write(buff, "png", bos);
				Image img = Image.getInstance(bos.toByteArray());

				img.setAlignment(Element.ALIGN_CENTER);
				img.scaleAbsolute(190, 45);
				cell1 = new PdfPCell(img);
				cell1.setBorder(0);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setColspan(3);
				cell1.setRowspan(3);
				cell1.setPaddingBottom(15f);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(
						"Informe de Rendimentos Financeiros de " + String.valueOf(Integer.valueOf(this.anoBase) + 1),
						titulo));
				cell1.setBorder(0);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(13f);
				cell1.setColspan(4);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("Ano Calendário - " + this.anoBase, titulo));
				cell1.setBorder(0);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(4);
				table.addCell(cell1);

				if (this.selectedPagador.getCpf() != null && !this.selectedPagador.getCpf().equals("")) {
					cell1 = new PdfPCell(new Phrase("Imposto de Renda Pessoa Física", titulo));
				} else {
					cell1 = new PdfPCell(new Phrase("Imposto de Renda Pessoa Jurídica", titulo));
				}
				cell1.setBorder(0);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(15f);
				cell1.setColspan(4);
				table.addCell(cell1);

			} else {

				cell1 = new PdfPCell(new Phrase(
						"Informe de Rendimentos Financeiros de " + String.valueOf(Integer.valueOf(this.anoBase) + 1),
						titulo));
				cell1.setBorder(0);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(100f);
				cell1.setPaddingBottom(13f);
				cell1.setColspan(7);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("Ano Calendário - " + this.anoBase, titulo));
				cell1.setBorder(0);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(7);
				table.addCell(cell1);

				if (this.selectedPagador.getCpf() != null && !this.selectedPagador.getCpf().equals("")) {
					cell1 = new PdfPCell(new Phrase("Imposto de Renda Pessoa Física", titulo));
				} else {
					cell1 = new PdfPCell(new Phrase("Imposto de Renda Pessoa Jurídica", titulo));
				}
				cell1.setBorder(0);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(15f);
				cell1.setColspan(7);
				table.addCell(cell1);

			}
			/*
			 * cell1 = new PdfPCell(new Phrase("Período: 01/01/" + this.anoBase +
			 * " a 31/12/" + this.anoBase, titulo)); cell1.setBorder(0);
			 * cell1.setPaddingLeft(8f); cell1.setBackgroundColor(BaseColor.WHITE);
			 * cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			 * cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 * cell1.setUseBorderPadding(true); cell1.setPaddingTop(5f);
			 * cell1.setPaddingBottom(15f); cell1.setColspan(7); table.addCell(cell1);
			 */

			cell1 = new PdfPCell(new Phrase("          ", normal12));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(7);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Identificação da Fonte Pagadora (IFP)", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(8f);
			cell1.setPaddingBottom(9f);
			cell1.setColspan(7);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Razão Social  ", titulo));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(4);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("CNPJ ", titulo));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(3);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Galleria Finanças Securitizadora S.A.", normal12));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(30f);
			cell1.setColspan(4);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("34.425.347/0001-06", normal12));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(30f);
			cell1.setColspan(3);
			table.addCell(cell1);

			if (this.selectedPagador.getCpf() != null && !this.selectedPagador.getCpf().equals("")) {
				cell1 = new PdfPCell(new Phrase("Pessoa Física Beneficiária dos Rendimentos", header));
			} else {
				cell1 = new PdfPCell(new Phrase("Pessoa Jurídica Beneficiária dos Rendimentos", header));
			}

			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(8f);
			cell1.setPaddingBottom(9f);
			cell1.setColspan(7);
			table.addCell(cell1);

			if (this.selectedPagador.getCpf() != null && !this.selectedPagador.getCpf().equals("")) {
				cell1 = new PdfPCell(new Phrase("Nome ", titulo));
			} else {
				cell1 = new PdfPCell(new Phrase("Razão Social ", titulo));
			}
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(4);
			table.addCell(cell1);

			if (this.selectedPagador.getCpf() != null && !this.selectedPagador.getCpf().equals("")) {
				cell1 = new PdfPCell(new Phrase("CPF ", titulo));
			} else {
				cell1 = new PdfPCell(new Phrase("CNPJ ", titulo));
			}
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setColspan(3);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(this.selectedPagador.getNome(), normal12));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(30f);
			cell1.setColspan(4);
			table.addCell(cell1);

			if (this.selectedPagador.getCpf() != null && !this.selectedPagador.getCpf().equals("")) {
				cell1 = new PdfPCell(new Phrase(this.selectedPagador.getCpf(), normal12));
			} else {
				cell1 = new PdfPCell(new Phrase(this.selectedPagador.getCnpj(), normal12));
			}
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(30f);
			cell1.setColspan(3);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Rendimentos Sujeitos a Tributação Exclusiva", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(8f);
			cell1.setPaddingBottom(9f);
			cell1.setColspan(7);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Contrato", titulo));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Especificação", titulo));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(
					new Phrase("Valor em 31/12/" + String.valueOf(Integer.valueOf(this.anoBase) - 1), titulo));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Valor em 31/12/" + String.valueOf(Integer.valueOf(this.anoBase)), titulo));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("I.R. Retido na Fonte", titulo));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Juros", titulo));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			for (InvestidorInformeRendimentos informe : this.investidorInformeRendimentos) {

				cell1 = new PdfPCell(new Phrase(informe.getNumeroContrato(), normalSmall));
				cell1.setBorder(0);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("Aplicações em Debêntures Simples", normalSmall));
				cell1.setBorder(0);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);

				if (informe.getSaldoAnoAnterior().compareTo(BigDecimal.ZERO) != 0) {
					cell1 = new PdfPCell(new Phrase("R$ " + df.format(informe.getSaldoAnoAnterior()), normalSmall));
				} else {
					cell1 = new PdfPCell(new Phrase("R$ 0,00", normalSmall));
				}

				cell1.setBorder(0);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				if (informe.getSaldoAnoAtual().compareTo(BigDecimal.ZERO) != 0) {
					cell1 = new PdfPCell(new Phrase("R$ " + df.format(informe.getSaldoAnoAtual()), normalSmall));
				} else {
					cell1 = new PdfPCell(new Phrase("R$ 0,00", normalSmall));
				}

				cell1.setBorder(0);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				if (informe.getIrRetido().compareTo(BigDecimal.ZERO) != 0) {
					cell1 = new PdfPCell(new Phrase("R$ " + df.format(informe.getIrRetido()), normalSmall));
				} else {
					cell1 = new PdfPCell(new Phrase("R$ 0,00", normalSmall));
				}

				cell1.setBorder(0);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				if (informe.getJuros().compareTo(BigDecimal.ZERO) != 0) {
					cell1 = new PdfPCell(new Phrase("R$ " + df.format(informe.getJuros()), normalSmall));
				} else {
					cell1 = new PdfPCell(new Phrase("R$ 0,00", normalSmall));
				}

				cell1.setBorder(0);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
			}

			if (this.investidorInformeRendimentos.size() > 0) {
				cell1 = new PdfPCell(new Phrase(" ", normalSmall));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(10f);
				cell1.setPaddingBottom(10f);
				cell1.setColspan(7);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(" ", normalSmall));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(10f);
				cell1.setPaddingBottom(10f);
				cell1.setColspan(2);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("Totais", titulo));
				cell1.setBorder(0);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(10f);
				cell1.setPaddingBottom(10f);
				cell1.setColspan(1);
				table.addCell(cell1);

				if (valorInvestidorAnoAnterior.compareTo(BigDecimal.ZERO) != 0) {
					cell1 = new PdfPCell(new Phrase("R$ " + df.format(valorInvestidorAnoAnterior), normalSmall));
				} else {
					cell1 = new PdfPCell(new Phrase("R$ 0,00", normalSmall));
				}

				cell1.setBorder(0);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				if (valorInvestidorAnoAtual.compareTo(BigDecimal.ZERO) != 0) {
					cell1 = new PdfPCell(new Phrase("R$ " + df.format(valorInvestidorAnoAtual), normalSmall));
				} else {
					cell1 = new PdfPCell(new Phrase("R$ 0,00", normalSmall));
				}

				cell1.setBorder(0);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				if (totalIRRetido.compareTo(BigDecimal.ZERO) != 0) {
					cell1 = new PdfPCell(new Phrase("R$ " + df.format(totalIRRetido), normalSmall));
				} else {
					cell1 = new PdfPCell(new Phrase("R$ 0,00", normalSmall));
				}

				cell1.setBorder(0);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				if (totalJurosLiquido.compareTo(BigDecimal.ZERO) != 0) {
					cell1 = new PdfPCell(new Phrase("R$ " + df.format(totalJurosLiquido), normalSmall));
				} else {
					cell1 = new PdfPCell(new Phrase("R$ 0,00", normalSmall));
				}

				cell1.setBorder(0);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
			}

			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Informe de Rendimento: Este contrato está aberto por algum outro programa, por favor, feche-o e tente novamente!"
							+ e,
					""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Informe de Rendimento: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.irRetidoInvestidoresPDFGerado = true;

			if (doc != null) {
				// fechamento do documento
				doc.close();
			}

			if (os != null) {
				// fechamento da stream de saída
				try {
					os.close();

					// fechamento da stream de saída
					String caminho = this.pathPDF + this.nomePDF;
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

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	public void gerarOperacoesIndividualizadoPDF() {
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("#,##0.00");
		Locale.setDefault(new Locale("pt", "BR"));

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.
		 * VGpT0_nF_h4
		 */

		Document doc = null;
		OutputStream os = null;
		ByteArrayOutputStream baos = null;

		try {
			baos = new ByteArrayOutputStream();
			Font header16 = new Font(FontFamily.HELVETICA, 16, Font.BOLD);
			Font header = new Font(FontFamily.HELVETICA, 14, Font.BOLD);
			Font header12 = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			Font header10 = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font header8 = new Font(FontFamily.HELVETICA, 8, Font.BOLD);
			Font normal8 = new Font(FontFamily.HELVETICA, 8);
			Font normal10 = new Font(FontFamily.HELVETICA, 10);

			Font headerFull = new Font(FontFamily.HELVETICA, 16, Font.BOLD);

			Font headerFullRed = new Font(FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.RED);

			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font tituloSmall = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
			Font tituloBranco = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloBranco.setColor(BaseColor.WHITE);
			Font normal = new Font(FontFamily.HELVETICA, 10, Font.NORMAL);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getDefault();
			Locale locale = new Locale("pt", "BR");
			Calendar date = Calendar.getInstance(zone, locale);
			SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MM/yyyy", locale);
			SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", locale);

			SimpleDateFormat dia = new SimpleDateFormat("dd", locale);
			SimpleDateFormat mes = new SimpleDateFormat("MMMMM", locale);
			SimpleDateFormat ano = new SimpleDateFormat("yyyy", locale);

			ValorPorExtenso valorPorExtenso = new ValorPorExtenso();
			NumeroPorExtenso numeroPorExtenso = new NumeroPorExtenso();
			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */

			// esquerda / direita / top / down
			doc = new Document(PageSize.A4.rotate(), 10, 10, 10, 10);

			

			// Associa a stream de saída ao
			PdfWriter writer = PdfWriter.getInstance(doc, baos);

			// adiciona cabeçalho e rodapé
			PDFCabecalhoRodape event = new PDFCabecalhoRodape();
			writer.setPageEvent(event);

			// Abre o documento
			doc.open();
			/*
			 * Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			 * p1.setAlignment(Element.ALIGN_CENTER); p1.setSpacingAfter(10); doc.add(p1);
			 */
			PdfPTable table = new PdfPTable(new float[] { 0.10f, 0.10f, 0.10f, 0.10f, 0.10f, 0.10f, 0.10f, 0.10f, 0.10f,
					0.10f, 0.10f, 0.10f, 0.10f });
			table.setWidthPercentage(100.0f);

			PdfPCell cell1 = new PdfPCell(new Phrase("Galleria Finanças Securitizadora S.A.", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(20f);
			cell1.setColspan(13);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Relatório de Operações - Individualizado", normal10));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(13);
			cell1.setPaddingBottom(5);
			cell1.setPaddingLeft(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(
					new Phrase(sdfDataRel.format(this.dataInicio) + " a " + sdfDataRel.format(this.dataFim), header10));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(13);
			cell1.setPaddingBottom(15);
			cell1.setPaddingLeft(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Contrato", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Data Contrato", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Cedente", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Prazo Médio", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Bruto", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Deságio", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Líquido", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Pagto. Op.", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Saldo", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Cessionário", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Aquisição", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Cessão", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Ágil", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			for (OperacoesIndividualizado oi : this.listOperacoesIndividualizado) {
				cell1 = new PdfPCell(new Phrase(oi.getContrato().getNumeroContrato(), normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(oi.getContrato().getDataContrato()), normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(oi.getCedente(), normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(String.valueOf(oi.getPrazoMedio()), normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("R$ " + df.format(oi.getValorBruto()), normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("R$ " + df.format(oi.getDesagio()), normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("R$ " + df.format(oi.getValorLiquido()), normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("R$ " + df.format(oi.getValorLiquido()), normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("R$ 0,00", normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(oi.getContrato().getCessionario(), normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				if (oi.getContrato().getDataAquisicaoCessao() != null) {
					cell1 = new PdfPCell(
							new Phrase(sdfDataRel.format(oi.getContrato().getDataAquisicaoCessao()), normal8));
				} else {
					cell1 = new PdfPCell(new Phrase(" ", normal8));
				}
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				if (oi.getContrato().getValorCessao() != null) {
					cell1 = new PdfPCell(new Phrase("R$ " + df.format(oi.getContrato().getValorCessao()), normal8));
				} else {
					cell1 = new PdfPCell(new Phrase(" ", normal8));
				}
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				if (oi.getContrato().getValorAgilCessao() != null) {
					cell1 = new PdfPCell(new Phrase("R$ " + df.format(oi.getContrato().getValorAgilCessao()), normal8));
				} else {
					cell1 = new PdfPCell(new Phrase(" ", normal8));
				}
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);
			}

			cell1 = new PdfPCell(new Phrase(" ", header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);
			cell1 = new PdfPCell(new Phrase(" ", header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);
			cell1 = new PdfPCell(new Phrase(" ", header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(String.valueOf(this.prazoMedioTotal), header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("R$ " + df.format(this.totalBruto), header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("R$ " + df.format(this.totalDesagio), header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("R$ " + df.format(this.totalLiquido), header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("R$ " + df.format(this.totalLiquido), header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("R$ 0,00", header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(" ", header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(" ", header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(" ", header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(" ", header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			doc.add(table);
			doc.close();
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
					FacesContext.getCurrentInstance());
			String nomeArquivoDownload = String.format("Galleria Bank - NovaOperacaoDetalhe.pdf", "");
			gerador.open(nomeArquivoDownload);
			gerador.feed(new ByteArrayInputStream(baos.toByteArray()));
			gerador.close();

		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Detalhamento Novas Operações: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.operacoesIndividualizadoPDFGerado = true;

			
			
			}
		}
	

	public final String clearFieldsTitulosQuitados() {
		this.dataInicio = gerarDataHoje();
		this.dataFim = gerarDataHoje();

		clearTitulosQuitadosPDFParams();

		return "/Atendimento/Cobranca/TitulosQuitadosConsultar.xhtml";
	}

	public final String clearFieldsDebeturesEmitidas() {
		this.dataInicio = gerarDataHoje();
		this.dataFim = gerarDataHoje();

		clearTitulosQuitadosPDFParams();

		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listRecebedores = pagadorRecebedorDao.findAll();
		this.listRecebedoresSeleciodados = new ArrayList<PagadorRecebedor>();

		this.dualListModelRecebedores = new DualListModel<PagadorRecebedor>(listRecebedores,
				listRecebedoresSeleciodados);

		return "/Atendimento/Cobranca/DebenturesEmitidasConsultar.xhtml";
	}

	public final String clearFieldsRelatorioDebeturesEmitidas() {
		this.dataInicio = gerarDataHoje();
		this.dataFim = gerarDataHoje();

		this.filtroDebenturesDocumento = "";
		this.filtroDebenturesTipoDocumento = "Todos";
		this.filtroDebenturesStatus = "Todos";

		this.filtroDebenturesTipoFiltro = "Periodo";
		this.filtroDebenturesTipoData = "Contrato";

		this.filtroDebenturesPorValor = "Todos";
		this.filtroValorFaceInicial = null;
		this.filtroValorFaceFinal = null;

		this.filtroNumeroContrato = "";

		clearTitulosQuitadosPDFParams();
		/*
		 * PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		 * this.listRecebedores = pagadorRecebedorDao.findAll();
		 * this.listRecebedoresSeleciodados = new ArrayList<PagadorRecebedor>();
		 * 
		 * this.dualListModelRecebedores = new DualListModel<PagadorRecebedor>(
		 * listRecebedores, listRecebedoresSeleciodados);
		 */
		return "/Atendimento/Cobranca/RelatorioDebenturesEmitidas.xhtml";
	}

	public void updateFiltroDocumento() {
		if (this.filtroDebenturesTipoDocumento.equals("Todos")) {
			this.filtroDebenturesDocumento = "";
		}
	}

	public void updateTipoFiltro() {
		if (this.filtroDebenturesTipoFiltro.equals("Periodo")) {
			this.dataInicio = gerarDataHoje();
			this.dataFim = gerarDataHoje();

			this.filtroNumeroContrato = "";
		} else {
			this.filtroNumeroContrato = "";
		}
	}

	public void updateFiltroPorValor() {
		if (this.filtroDebenturesPorValor.equals("Todos")) {
			this.filtroValorFaceInicial = null;
			this.filtroValorFaceFinal = null;
		}
	}

	public void consultaDebeturesEmitidas() {
		System.out.println("Debentures Emitidas Size: Consultando...");
		clearTitulosQuitadosPDFParams();

		DebenturesInvestidorDao dbDao = new DebenturesInvestidorDao();
		listDebenturesInvestidor = dbDao.getDebenturesEmitidasPorPeriodo(this.dataInicio, this.dataFim,
				this.dualListModelRecebedores.getTarget());

		System.out.println("Debentures Emitidas Size: " + listDebenturesInvestidor.size());
	}

	public void relatorioDebeturesEmitidas() {
		System.out.println("Debentures Emitidas Size: Consultando...");
		clearTitulosQuitadosPDFParams();

		DebenturesInvestidorDao dbDao = new DebenturesInvestidorDao();
		listDebenturesInvestidor = dbDao.getRelatorioDebenturesEmitidas(this.dataInicio, this.dataFim,
				this.filtroDebenturesTipoDocumento, this.filtroDebenturesDocumento, this.filtroDebenturesStatus,
				this.filtroDebenturesPorValor, this.filtroValorFaceInicial, this.filtroValorFaceFinal,
				this.filtroNumeroContrato, this.filtroDebenturesTipoFiltro, this.filtroDebenturesTipoData);

		System.out.println("Debentures Emitidas Size: " + listDebenturesInvestidor.size());
	}

	public void clearTitulosQuitadosPDFParams() {
		this.titulosQuitadosPDF = null;
		this.titulosQuitadosPDFGerado = false;
		this.titulosQuitadosPDFStr = "";

		this.debenturesInvestidor = new DebenturesInvestidor();

		this.totalValorFace = BigDecimal.ZERO.setScale(2);
		this.listDebenturesInvestidor = new ArrayList<DebenturesInvestidor>();
	}

	public void consultaTitulosQuitados() {
		clearTitulosQuitadosPDFParams();

		DebenturesInvestidorDao dbDao = new DebenturesInvestidorDao();
		listDebenturesInvestidor = dbDao.getDebenturesPorPeriodo(this.dataInicio, this.dataFim);

		for (DebenturesInvestidor db : listDebenturesInvestidor) {
			BigDecimal valorFace = new BigDecimal(db.getQtdeDebentures()).multiply(new BigDecimal(1000.00));
			db.setValorFace(valorFace.setScale(2));

			this.totalValorFace = this.totalValorFace.add(valorFace);
		}
	}

	public void gerarTitulosQuitadosPDF() {
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("#,##0.00");
		Locale.setDefault(new Locale("pt", "BR"));

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.
		 * VGpT0_nF_h4
		 */

		Document doc = null;
		OutputStream os = null;
		ByteArrayOutputStream baos = null;

		try {
			baos = new ByteArrayOutputStream();
			
			Font header16 = new Font(FontFamily.HELVETICA, 16, Font.BOLD);
			Font header = new Font(FontFamily.HELVETICA, 14, Font.BOLD);
			Font header12 = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			Font header10 = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font header8 = new Font(FontFamily.HELVETICA, 8, Font.BOLD);
			Font normal8 = new Font(FontFamily.HELVETICA, 8);
			Font normal10 = new Font(FontFamily.HELVETICA, 10);

			Font headerFull = new Font(FontFamily.HELVETICA, 16, Font.BOLD);

			Font headerFullRed = new Font(FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.RED);

			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font tituloSmall = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
			Font tituloBranco = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloBranco.setColor(BaseColor.WHITE);
			Font normal = new Font(FontFamily.HELVETICA, 10, Font.NORMAL);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getDefault();
			Locale locale = new Locale("pt", "BR");
			Calendar date = Calendar.getInstance(zone, locale);
			SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MM/yyyy", locale);
			SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", locale);

			SimpleDateFormat dia = new SimpleDateFormat("dd", locale);
			SimpleDateFormat mes = new SimpleDateFormat("MMMMM", locale);
			SimpleDateFormat ano = new SimpleDateFormat("yyyy", locale);

			ValorPorExtenso valorPorExtenso = new ValorPorExtenso();
			NumeroPorExtenso numeroPorExtenso = new NumeroPorExtenso();

			ParametrosDao pDao = new ParametrosDao();
			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */

			// esquerda / direita / top / down
			doc = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);
			


			// Associa a stream de saída ao
			PdfWriter writer = PdfWriter.getInstance(doc, baos);

			// adiciona cabeçalho e rodapé
			PDFCabecalhoRodape event = new PDFCabecalhoRodape();
			writer.setPageEvent(event);

			// Abre o documento
			doc.open();
			/*
			 * Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			 * p1.setAlignment(Element.ALIGN_CENTER); p1.setSpacingAfter(10); doc.add(p1);
			 */
			PdfPTable table = new PdfPTable(
					new float[] { 0.10f, 0.10f, 0.18f, 0.18f, 0.18f, 0.18f, 0.18f, 0.18f, 0.18f, 0.18f, 0.18f });
			table.setWidthPercentage(100.0f);

			PdfPCell cell1 = new PdfPCell(new Phrase("Galleria Finanças Securitizadora S.A.", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(20f);
			cell1.setColspan(11);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Relatório de Debêntures Emitidas", normal10));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(11);
			cell1.setPaddingBottom(5);
			cell1.setPaddingLeft(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(
					new Phrase(sdfDataRel.format(this.dataInicio) + " a " + sdfDataRel.format(this.dataFim), header10));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(11);
			cell1.setPaddingBottom(15);
			cell1.setPaddingLeft(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Cautela", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Tipo", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Debenturista", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Emissão", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Vencimento", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Valor Face", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Juros", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Tarifas", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Multas", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Valor", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Op", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			for (DebenturesInvestidor db : this.listDebenturesInvestidor) {
				cell1 = new PdfPCell(new Phrase(db.getNumeroCautela(), normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("TED", normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(db.getRecebedor().getNome(), normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(db.getDataDebentures()), normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(db.getDataVencimento()), normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("R$ " + df.format(db.getValorFace()), normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("R$ 0,00", normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("R$ 0,00", normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("R$ 0,00", normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("R$ " + df.format(db.getValorFace()), normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				String numeroContrato = "";

				if (db.getContrato() != null) {
					numeroContrato = db.getContrato().getNumeroContrato();
				}
				cell1 = new PdfPCell(new Phrase(numeroContrato, normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.GRAY);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.GRAY);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);
			}

			cell1 = new PdfPCell(new Phrase(" ", header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);
			cell1 = new PdfPCell(new Phrase(" ", header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);
			cell1 = new PdfPCell(new Phrase(" ", header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);
			cell1 = new PdfPCell(new Phrase(" ", header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);
			cell1 = new PdfPCell(new Phrase(" ", header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("R$ " + df.format(this.totalValorFace), header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("R$ 0,00", header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("R$ 0,00", header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("R$ 0,00", header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("R$ " + df.format(this.totalValorFace), header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(" ", header8));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			doc.add(table);
			doc.close();
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
					FacesContext.getCurrentInstance());
			String nomeArquivoDownload = String.format("Galleria Bank - DebenturesEmitidas.pdf", "");
			gerador.open(nomeArquivoDownload);
			gerador.feed(new ByteArrayInputStream(baos.toByteArray()));
			gerador.close();

		}  catch (Exception e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debêntures Emitidas: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.titulosQuitadosPDFGerado = true;

			
			}
		}
	

	public String clearFieldsConsultaDebentures() {
		this.listDebenturesInvestidor = new ArrayList<DebenturesInvestidor>();
		clearFieldsAddDebentures();
		clearFieldsPDFs();

		DebenturesInvestidorDao diDao = new DebenturesInvestidorDao();
		this.listDebenturesInvestidor = diDao.findAll();

		return "/Atendimento/Cobranca/DebenturesConsultar.xhtml";
	}

	public String clearFieldsDeleteDebentures() {
		DebenturesInvestidorDao diDao = new DebenturesInvestidorDao();
		diDao.delete(this.debenturesInvestidor);

		this.listDebenturesInvestidor = diDao.findAll();

		return "/Atendimento/Cobranca/DebenturesConsultar.xhtml";
	}

	public void gerarPDFs() {
		this.selectedContratoLov = this.debenturesInvestidor.getContrato();
		this.selectedPagador = this.debenturesInvestidor.getRecebedor();

		if (this.debenturesInvestidor.isLastrearTitulos()) {
			geraAnexo1();
		} else {
			this.anexo1PDFGerado = false;
		}

		geraDebenture();
		geraTermoSecuritizacao();
		geraBS();
	}

	public String clearFieldsEditDebentures() {
		this.selectedPagador = this.debenturesInvestidor.getRecebedor();
		populateSelectedPagador();

		PagadorRecebedorDao prDao = new PagadorRecebedorDao();
// TODO		this.listPagadores = prDao.findAll();

		loadContratos();

		this.filtraContrato = true;
		clearContrato();

		if (this.debenturesInvestidor.isLastrearTitulos()) {
			this.filtroNumeroContrato = this.debenturesInvestidor.getContrato().getNumeroContrato();
		}

		clearFieldsPDFs();

		return "/Atendimento/Cobranca/DebenturesInserir.xhtml";
	}

	public String clearGeraDebenture() {
		clearContrato();
		loadContratos();
		clearFieldsAddDebentures();
		clearFieldsPDFs();
		clearPagador();
		PagadorRecebedorDao prDao = new PagadorRecebedorDao();
		this.listPagadores = prDao.findAll();

		return "/Atendimento/Cobranca/DebenturesInserir.xhtml";
	}

	public void clearFieldsAddDebentures() {
		this.debenturesInvestidor = new DebenturesInvestidor();

		this.debenturesInvestidor.setLastrearTitulos(true);
		this.debenturesInvestidor.setDataDebentures(gerarDataHoje());

		this.filtraContrato = true;
		this.filtroNumeroContrato = "";
	}

	public void clearFieldsPDFs() {
		this.debenturePDFGerado = false;
		this.termoSecuritizacaoPDFGerado = false;
		this.anexo1PDFGerado = false;
		this.bsPDFGerado = false;
		this.pathPDF = "";
		this.nomePDF = "";
		this.termoSecuritizacaoPDFStr = "";
		this.anexoPDFStr = "";
		this.bsPDFStr = "";
		this.termoSecuritizacaoPDF = null;
		this.anexoPDF = null;
		this.bsPDF = null;
		this.file = null;
	}

	public void atualizaLastreamentoTitulos() {
		if (!this.debenturesInvestidor.isLastrearTitulos()) {
			this.debenturesInvestidor.setNumeroDocumento(null);
			this.debenturesInvestidor.setPorcentagemDebentures(null);
			this.filtroNumeroContrato = null;
			clearContrato();
		}
	}

	public String inserirDebenture() {
		FacesContext context = FacesContext.getCurrentInstance();
		boolean validaContrato = true;

		// busca contrato filtrando por numero
		if (this.debenturesInvestidor.isLastrearTitulos()) {
			if (this.filtraContrato) {
				if (this.filtroNumeroContrato.length() == 4) {
					this.filtroNumeroContrato = "0" + this.filtroNumeroContrato;
				}

				loadContratosByNumero(this.filtroNumeroContrato);
			}

			// valida se o contrato foi selecionado
			if (this.selectedContratoLov == null) {
				validaContrato = false;
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Seleção de Contrato: O contrato informado não foi localizado, verifique e tente novamente!",
						""));

			} else {
				if (this.selectedContratoLov.getId() <= 0) {
					validaContrato = false;
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Seleção de Contrato: O contrato informado não foi localizado, verifique e tente novamente!",
							""));
				}
			}
		}

		// valida se o pagador foi selecionado
		if (this.selectedPagador == null) {
			validaContrato = false;
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Seleção de Pagador: O pagador não foi selecionado, verifique e tente novamente!", ""));

		} else {
			if (this.selectedPagador.getId() <= 0) {
				validaContrato = false;
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Seleção de Pagador:  O pagador não foi selecionado, verifique e tente novamente!", ""));
			}
		}

		// gera PDFs
		if (validaContrato) {
			if (this.debenturesInvestidor.isLastrearTitulos()) {
				geraAnexo1();
			} else {
				this.anexo1PDFGerado = false;
			}

			this.debenturesInvestidor.setRecebedor(this.selectedPagador);

			geraDebenture();
			geraTermoSecuritizacao();
			geraBS();

			// salva registro
			DebenturesInvestidorDao diDao = new DebenturesInvestidorDao();

			if (this.debenturesInvestidor.isLastrearTitulos()) {
				ContratoCobrancaDao cDao = new ContratoCobrancaDao();
				
				this.debenturesInvestidor.setContrato(cDao.findById(this.selectedContratoLov.getId()));
			}

			if (this.debenturesInvestidor.getId() <= 0) {
				diDao.create(this.debenturesInvestidor);

				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Debêntures: O registro foi salvo com sucesso!", ""));
			} else {
				diDao.merge(this.debenturesInvestidor);

				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Debêntures: O registro foi salvo com sucesso!", ""));
			}

			this.listDebenturesInvestidor = diDao.findAll();

			return "/Atendimento/Cobranca/DebenturesConsultar.xhtml";
		}

		return "/Atendimento/Cobranca/DebenturesInserir.xhtml";
	}

	public void geraBS() {
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("#,##0.00");
		Locale.setDefault(new Locale("pt", "BR"));

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.
		 * VGpT0_nF_h4
		 */

		Document doc = null;
		OutputStream os = null;

		try {
			Font header16 = new Font(FontFamily.HELVETICA, 16, Font.BOLD);
			Font header = new Font(FontFamily.HELVETICA, 14, Font.BOLD);
			Font header12 = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			Font header10 = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font header8 = new Font(FontFamily.HELVETICA, 8, Font.BOLD);
			Font normal8 = new Font(FontFamily.HELVETICA, 8);
			Font normal10 = new Font(FontFamily.HELVETICA, 10);

			Font headerFull = new Font(FontFamily.HELVETICA, 16, Font.BOLD);

			Font headerFullRed = new Font(FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.RED);

			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font tituloSmall = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
			Font tituloBranco = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloBranco.setColor(BaseColor.WHITE);
			Font normal = new Font(FontFamily.HELVETICA, 10, Font.NORMAL);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getDefault();
			Locale locale = new Locale("pt", "BR");
			Calendar date = Calendar.getInstance(zone, locale);
			SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MM/yyyy", locale);
			SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", locale);

			SimpleDateFormat dia = new SimpleDateFormat("dd", locale);
			SimpleDateFormat mes = new SimpleDateFormat("MMMMM", locale);
			SimpleDateFormat ano = new SimpleDateFormat("yyyy", locale);

			ValorPorExtenso valorPorExtenso = new ValorPorExtenso();
			NumeroPorExtenso numeroPorExtenso = new NumeroPorExtenso();

			ParametrosDao pDao = new ParametrosDao();
			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */

			// esquerda / direita / top / down
			doc = new Document(PageSize.A4, 30, 30, 65, 65);
			this.bsPDFStr = "BS.pdf";
			this.pathPDF = pDao.findByFilter("nome", "RECIBOS_IUGU").get(0).getValorString();

			os = new FileOutputStream(this.pathPDF + this.bsPDFStr);

			// Associa a stream de saída ao
			PdfWriter.getInstance(doc, os);

			// Abre o documento
			doc.open();
			/*
			 * Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			 * p1.setAlignment(Element.ALIGN_CENTER); p1.setSpacingAfter(10); doc.add(p1);
			 */
			PdfPTable table = new PdfPTable(new float[] { 0.18f, 0.18f, 0.18f, 0.18f });
			table.setWidthPercentage(100.0f);

			PdfPCell cell1 = new PdfPCell(new Phrase("Galleria Finanças Securitizadora S.A.", header16));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(20f);
			cell1.setColspan(8);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("BOLETIM DE SUBSCRIÇÃO DE DEBÊNTURES SIMPLES", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(8);
			cell1.setPaddingTop(10);
			cell1.setPaddingBottom(10);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Emissora", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(8);
			cell1.setPaddingTop(5);
			cell1.setPaddingLeft(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Galleria Finanças Securitizadora S.A.", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(8);
			cell1.setPaddingBottom(5);
			cell1.setPaddingLeft(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Número da Cautela", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Operação", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Data de Subscrição", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("CNPJ/MF", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(this.debenturesInvestidor.getNumeroCautela(), normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Venda", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(
					new Phrase(sdfDataRel.format(this.debenturesInvestidor.getDataDebentures()), normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("34.425.347/0001-06", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Logradouro", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(2);
			cell1.setPaddingTop(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Bairro", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(2);
			cell1.setPaddingTop(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Avenida Doutor José Bonifácio Coutinho N, 150", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(2);
			cell1.setPaddingBottom(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Jardim Madalena", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(2);
			cell1.setPaddingBottom(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("CEP", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(1);
			cell1.setPaddingTop(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Cidade", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(2);
			cell1.setPaddingTop(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("UF", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(1);
			cell1.setPaddingTop(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("13091-611", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(1);
			cell1.setPaddingBottom(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Campinas", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(2);
			cell1.setPaddingBottom(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("São Paulo", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(1);
			cell1.setPaddingBottom(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Características da Emissão", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(8);
			cell1.setPaddingTop(5);
			cell1.setPaddingLeft(5);
			table.addCell(cell1);

			BigDecimal valorDebenture = new BigDecimal(this.debenturesInvestidor.getQtdeDebentures())
					.multiply(new BigDecimal(1000));

			Paragraph pa = new Paragraph();
			pa.add(new Chunk("Emissão privada, aprovada pela 2a Assembléia Geral Extraordinária da ", normal10));
			pa.add(new Chunk("EMISSORA", header10));
			pa.add(new Chunk(", realizada em 30/10/2020. ", normal10));

			cell1 = new PdfPCell(new Phrase(pa));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(8);
			cell1.setPaddingBottom(5);
			cell1.setPaddingLeft(5);
			table.addCell(cell1);

			pa = new Paragraph();
			pa.add(new Chunk("Data da Emissão: ", header10));
			pa.add(new Chunk(sdfDataRel.format(this.debenturesInvestidor.getDataDebentures()) + ". ", normal10));

			cell1 = new PdfPCell(new Phrase(pa));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(2);
			cell1.setPaddingBottom(5);
			cell1.setPaddingLeft(5);
			cell1.setPaddingTop(15);
			table.addCell(cell1);

			pa = new Paragraph();
			pa.add(new Chunk("Data de Vencimento: ", header10));
			pa.add(new Chunk(sdfDataRel.format(this.debenturesInvestidor.getDataVencimento()), normal10));

			cell1 = new PdfPCell(new Phrase(pa));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(2);
			cell1.setPaddingBottom(5);
			cell1.setPaddingLeft(5);
			cell1.setPaddingTop(15);
			table.addCell(cell1);

			pa = new Paragraph();
			pa.add(new Chunk("Valor Total da Emissão: ", header10));
			pa.add(new Chunk(df.format(valorDebenture) + " na série 1/" + this.debenturesInvestidor.getSerie() + ".",
					normal10));

			cell1 = new PdfPCell(new Phrase(pa));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(2);
			cell1.setPaddingBottom(5);
			cell1.setPaddingLeft(5);
			table.addCell(cell1);

			pa = new Paragraph();
			pa.add(new Chunk("Espécie: ", header10));
			pa.add(new Chunk("subordinada. ", normal10));

			cell1 = new PdfPCell(new Phrase(pa));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(2);
			cell1.setPaddingBottom(5);
			cell1.setPaddingLeft(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Nome do Adquirente", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("CPF/MF", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(this.debenturesInvestidor.getRecebedor().getNome(), header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(this.debenturesInvestidor.getRecebedor().getCpf(), header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Logradouro", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(2);
			cell1.setPaddingTop(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Bairro", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(2);
			cell1.setPaddingTop(5);
			table.addCell(cell1);

			String endereco = "";
			if (this.debenturesInvestidor.getRecebedor().getNumero() != null) {
				if (!this.debenturesInvestidor.getRecebedor().getNumero().equals("")) {
					endereco = this.debenturesInvestidor.getRecebedor().getEndereco() + ", "
							+ this.debenturesInvestidor.getRecebedor().getNumero();
				}
			} else {
				endereco = this.debenturesInvestidor.getRecebedor().getEndereco();
			}

			cell1 = new PdfPCell(new Phrase(endereco, normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(2);
			cell1.setPaddingBottom(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(this.debenturesInvestidor.getRecebedor().getBairro(), normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(2);
			cell1.setPaddingBottom(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("CEP", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(1);
			cell1.setPaddingTop(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Cidade", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(2);
			cell1.setPaddingTop(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("UF", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(1);
			cell1.setPaddingTop(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(this.debenturesInvestidor.getRecebedor().getCep(), normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(1);
			cell1.setPaddingBottom(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(this.debenturesInvestidor.getRecebedor().getCidade(), normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(2);
			cell1.setPaddingBottom(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(this.debenturesInvestidor.getRecebedor().getEstado(), normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(1);
			cell1.setPaddingBottom(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Preço Unitário de Subscrição Em "
					+ sdfDataRel.format(this.debenturesInvestidor.getDataDebentures()), normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5);
			cell1.setPaddingBottom(7);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Quantidade de Debêntures Subscritas", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5);
			cell1.setPaddingBottom(7);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Quantidade de Debêntures Integralizadas", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5);
			cell1.setPaddingBottom(7);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(
					"Valor Total Integralizado Em " + sdfDataRel.format(this.debenturesInvestidor.getDataDebentures()),
					normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5);
			cell1.setPaddingBottom(7);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("R$ 1.000,00", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(
					"1/" + this.debenturesInvestidor.getSerie() + " - " + this.debenturesInvestidor.getQtdeDebentures(),
					normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(
					"1/" + this.debenturesInvestidor.getSerie() + " - " + this.debenturesInvestidor.getQtdeDebentures(),
					normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(df.format(valorDebenture), header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Forma de Pagamento", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5);
			cell1.setPaddingLeft(5);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Banco Debenturista", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5);
			cell1.setPaddingLeft(5);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Dados Bancários Debenturista", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5);
			cell1.setPaddingLeft(5);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("TED", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5);
			cell1.setPaddingLeft(5);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(this.debenturesInvestidor.getRecebedor().getBanco(), normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5);
			cell1.setPaddingLeft(5);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Agência: " + this.debenturesInvestidor.getRecebedor().getAgencia()
					+ " / CC: " + this.debenturesInvestidor.getRecebedor().getConta(), normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5);
			cell1.setPaddingLeft(5);
			cell1.setColspan(2);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(
					"A Galleria Finanças Securitizadora S.A certifica que recebeu a referida integralização no valor acima.",
					normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5);
			cell1.setPaddingBottom(40);
			cell1.setPaddingLeft(5);
			cell1.setColspan(8);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("__________________________________________", normal));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(8);
			cell1.setPaddingLeft(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Galleria Finanças Securitizadora S.A.", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(8);
			cell1.setPaddingLeft(5);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setPaddingBottom(10);
			table.addCell(cell1);

			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"BS: Este documento está aberto por algum outro programa, por favor, feche-o e tente novamente!"
							+ e,
					""));
		} catch (Exception e) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "BS: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.bsPDFGerado = true;

			if (doc != null) {
				// fechamento do documento
				doc.close();
			}
			if (os != null) {
				// fechamento da stream de saída
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void geraAnexo1() {
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("#,##0.00");
		Locale.setDefault(new Locale("pt", "BR"));

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.
		 * VGpT0_nF_h4
		 */

		Document doc = null;
		OutputStream os = null;

		try {
			Font header = new Font(FontFamily.HELVETICA, 14, Font.BOLD);
			Font header12 = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			Font header10 = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font header8 = new Font(FontFamily.HELVETICA, 8, Font.BOLD);
			Font normal8 = new Font(FontFamily.HELVETICA, 8);

			Font headerFull = new Font(FontFamily.HELVETICA, 16, Font.BOLD);

			Font headerFullRed = new Font(FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.RED);

			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font tituloSmall = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
			Font tituloBranco = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloBranco.setColor(BaseColor.WHITE);
			Font normal = new Font(FontFamily.HELVETICA, 10, Font.NORMAL);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getDefault();
			Locale locale = new Locale("pt", "BR");
			Calendar date = Calendar.getInstance(zone, locale);
			SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MM/yyyy", locale);
			SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", locale);

			SimpleDateFormat dia = new SimpleDateFormat("dd", locale);
			SimpleDateFormat mes = new SimpleDateFormat("MMMMM", locale);
			SimpleDateFormat ano = new SimpleDateFormat("yyyy", locale);

			ValorPorExtenso valorPorExtenso = new ValorPorExtenso();
			NumeroPorExtenso numeroPorExtenso = new NumeroPorExtenso();

			ParametrosDao pDao = new ParametrosDao();
			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */

			// esquerda / direita / top / down
			doc = new Document(PageSize.A4, 30, 30, 65, 65);
			this.anexoPDFStr = "AnexoI.pdf";
			this.pathPDF = pDao.findByFilter("nome", "RECIBOS_IUGU").get(0).getValorString();

			os = new FileOutputStream(this.pathPDF + this.anexoPDFStr);

			// Associa a stream de saída ao
			PdfWriter.getInstance(doc, os);

			// Abre o documento
			doc.open();
			/*
			 * Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			 * p1.setAlignment(Element.ALIGN_CENTER); p1.setSpacingAfter(10); doc.add(p1);
			 */
			PdfPTable table = new PdfPTable(new float[] { 0.06f, 0.14f, 0.12f, 0.18f, 0.18f, 0.18f, 0.18f, 0.16f });
			table.setWidthPercentage(100.0f);

			PdfPCell cell1 = new PdfPCell(new Phrase("ANEXO I", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(8);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("O PRESENTE ANEXO É PARTE INTEGRANTE E INSEPARÁVEL", header12));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(8);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(
					"DO TERMO DE SECURITIZAÇÃO DA CAUTELA Nº " + this.debenturesInvestidor.getNumeroCautela(),
					header12));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(30f);
			cell1.setColspan(8);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Tipo", header8));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Nº Doc", header8));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Vencto", header8));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Cedente", header8));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("CPF/CNPJ", header8));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Sacado", header8));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("CPF/CNPJ", header8));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Valor", header8));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);

			this.totalAnexoI = BigDecimal.ZERO;
			this.valorPorcentagemAnexoI = BigDecimal.ZERO;

			int sizeList = this.selectedContratoLov.getListContratoCobrancaDetalhes().size() - 1;

			for (int i = 0; i < this.selectedContratoLov.getListContratoCobrancaDetalhes().size(); i++) {
				cell1 = new PdfPCell(new Phrase("CCB", normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.GRAY);

				if (i == 20 || // borda inferior primeira página
						i == sizeList || // borda inferior última página
						i == 44 || // borda inferior segunda página
						i == 66) { // borda inferior terceira página (24 registros por página)
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.GRAY);
				}

				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(this.debenturesInvestidor.getNumeroDocumento(), normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.GRAY);

				if (i == 20 || // borda inferior primeira página
						i == sizeList || // borda inferior última página
						i == 44 || // borda inferior segunda página
						i == 66) { // borda inferior terceira página (24 registros por página)
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.GRAY);
				}

				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(
						sdfDataRel.format(
								this.selectedContratoLov.getListContratoCobrancaDetalhes().get(i).getDataVencimento()),
						normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.GRAY);

				if (i == 20 || // borda inferior primeira página
						i == sizeList || // borda inferior última página
						i == 44 || // borda inferior segunda página
						i == 66) { // borda inferior terceira página (24 registros por página)
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.GRAY);
				}

				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("BMP Money Plus Sociedade de Crédito Direto S.A.", normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.GRAY);

				if (i == 20 || // borda inferior primeira página
						i == sizeList || // borda inferior última página
						i == 44 || // borda inferior segunda página
						i == 66) { // borda inferior terceira página (24 registros por página)
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.GRAY);
				}

				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase("34.337.707/0001-00", normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.GRAY);

				if (i == 20 || // borda inferior primeira página
						i == sizeList || // borda inferior última página
						i == 44 || // borda inferior segunda página
						i == 66) { // borda inferior terceira página (24 registros por página)
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.GRAY);
				}

				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(this.selectedContratoLov.getPagador().getNome(), normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.GRAY);

				if (i == 20 || // borda inferior primeira página
						i == sizeList || // borda inferior última página
						i == 44 || // borda inferior segunda página
						i == 66) { // borda inferior terceira página (24 registros por página)
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.GRAY);
				}

				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				String documento = "";

				if (this.selectedContratoLov.getPagador().getCpf() != null) {
					documento = this.selectedContratoLov.getPagador().getCpf();
				} else {
					documento = this.selectedContratoLov.getPagador().getCnpj();
				}

				cell1 = new PdfPCell(new Phrase(documento, normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.GRAY);

				if (i == 20 || // borda inferior primeira página
						i == sizeList || // borda inferior última página
						i == 44 || // borda inferior segunda página
						i == 66) { // borda inferior terceira página (24 registros por página)
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.GRAY);
				}

				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(
						"R$ " + df.format(
								this.selectedContratoLov.getListContratoCobrancaDetalhes().get(i).getVlrParcela()),
						normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.GRAY);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.GRAY);

				if (i == 20 || // borda inferior primeira página
						i == sizeList || // borda inferior última página
						i == 44 || // borda inferior segunda página
						i == 66) { // borda inferior terceira página (24 registros por página)
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.GRAY);
				}

				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);

				this.totalAnexoI = this.totalAnexoI
						.add(this.selectedContratoLov.getListContratoCobrancaDetalhes().get(i).getVlrParcela());
			}

			// VALOR TOTAL
			cell1 = new PdfPCell(new Phrase("Valor total: R$ " + df.format(this.totalAnexoI), normal8));
			cell1.setBorder(0);
			cell1.setPaddingTop(5f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(8);
			table.addCell(cell1);

			// LINHA PORCENTAGEM
			if (this.debenturesInvestidor.getPorcentagemDebentures().compareTo(new BigDecimal((String) "100")) == -1) {
				BigDecimal ONE_HUNDRED = new BigDecimal(100);
				this.valorPorcentagemAnexoI = this.totalAnexoI
						.multiply(this.debenturesInvestidor.getPorcentagemDebentures()).divide(ONE_HUNDRED);

				cell1 = new PdfPCell(new Phrase(
						"As debêntures acima têm como garantia o lastro correspondente a "
								+ this.debenturesInvestidor.getPorcentagemDebentures() + "% dos Títulos acima.",
						normal8));
				cell1.setBorder(0);
				cell1.setPaddingTop(20f);
				cell1.setPaddingRight(20f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setColspan(8);
				table.addCell(cell1);
			}

			cell1 = new PdfPCell(new Phrase("______________________________________________", normal));
			cell1.setBorder(0);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(100f);
			cell1.setColspan(8);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Galleria Finanças Securitizadora S.A.", header10));
			cell1.setBorder(0);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(8);
			table.addCell(cell1);

			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Anexo I: Este documento está aberto por algum outro programa, por favor, feche-o e tente novamente!"
							+ e,
					""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Anexo I: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.anexo1PDFGerado = true;

			if (doc != null) {
				// fechamento do documento
				doc.close();
			}
			if (os != null) {
				// fechamento da stream de saída
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void geraTermoSecuritizacao() {
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("#,##0.00");
		Locale.setDefault(new Locale("pt", "BR"));

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.
		 * VGpT0_nF_h4
		 */

		Document doc = null;
		OutputStream os = null;

		try {
			Font header = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font headerFull = new Font(FontFamily.HELVETICA, 16, Font.BOLD);

			Font headerFullRed = new Font(FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.RED);

			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font tituloSmall = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
			Font tituloBranco = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloBranco.setColor(BaseColor.WHITE);
			Font normal = new Font(FontFamily.HELVETICA, 10, Font.NORMAL);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getDefault();
			Locale locale = new Locale("pt", "BR");
			Calendar date = Calendar.getInstance(zone, locale);
			SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MM/yyyy", locale);
			SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", locale);

			SimpleDateFormat dia = new SimpleDateFormat("dd", locale);
			SimpleDateFormat mes = new SimpleDateFormat("MMMMM", locale);
			SimpleDateFormat ano = new SimpleDateFormat("yyyy", locale);

			ValorPorExtenso valorPorExtenso = new ValorPorExtenso();
			NumeroPorExtenso numeroPorExtenso = new NumeroPorExtenso();

			ParametrosDao pDao = new ParametrosDao();
			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */

			// esquerda / direita / top / down
			doc = new Document(PageSize.A4, 30, 30, 100, 50);
			this.termoSecuritizacaoPDFStr = "TermoSecuritizacao.pdf";
			this.pathPDF = pDao.findByFilter("nome", "RECIBOS_IUGU").get(0).getValorString();

			os = new FileOutputStream(this.pathPDF + this.termoSecuritizacaoPDFStr);

			// Associa a stream de saída ao
			PdfWriter.getInstance(doc, os);

			// Abre o documento
			doc.open();
			/*
			 * Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			 * p1.setAlignment(Element.ALIGN_CENTER); p1.setSpacingAfter(10); doc.add(p1);
			 */
			PdfPTable table = new PdfPTable(new float[] { 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f });
			table.setWidthPercentage(100.0f);

			PdfPCell cell1 = new PdfPCell(
					new Phrase("TERMO DE SECURITIZAÇÃO DE RECEBÍVEIS EMPRESARIAIS MERCANTIS & INDUSTRIAIS", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("I) EMITENTE:", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			Paragraph pa = new Paragraph();

			pa.add(new Chunk("Galleria Finanças Securitizadora S.A.", header));
			pa.add(new Chunk(
					", pessoa jurídica de direito privado, na forma de sociedade anônima de capital fechado, com sede e foro no município de Campinas, no endereço Avenida Doutor José Bonifácio Coutinho N, CEP 13091-611, inscrita no Cadastro Nacional de Pessoa Jurídica do Ministério da Fazenda sob n.o 34.425.347/0001-06, devidamente constituída e registrada na Junta Comercial do Estado de São Paulo, sob n° ED003063-6/000, neste ato representada, na forma de seu Estatuto Social, por seus Diretores Administrativo-Financeiro Joao Augusto Magatti Alves, Brasileiro, Casado(a), Empresário, residente e domiciliado na Cidade de Campinas, no endereço Avenida Doutor José Bonifácio Coutinho Nogueira, portador da Carteira de Identidade n° 50.630.711-6 e inscrito no CPF n° 436.821.448-03 e Fabricio Figueiredo, Brasileiro, Casado(a), Empresário, residente e domiciliado na Cidade de Sorocaba, no endereço Rua Elza Batista de Souza, portador da Carteira de Identidade n° 22.569.228-4 e inscrito no CPF n° 266.752.318-04, doravante denominada simplesmente ",
					normal));
			pa.add(new Chunk("SECURITIZADORA", header));
			pa.add(new Chunk(";", normal));

			cell1 = new PdfPCell(pa);
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_TOP);
			cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("CLÁUSULA 1ª – DO OBJETO:", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setColspan(6);
			table.addCell(cell1);

			pa = new Paragraph();

			pa.add(new Chunk("A ", normal));
			pa.add(new Chunk("SECURITIZADORA ", header));
			pa.add(new Chunk(
					", com fulcro no Princípio da Livre Iniciativa e no da Licitude, aliados à aplicação por analogia da Lei 9514/97, art. 8°, firma o presente ",
					normal));
			pa.add(new Chunk("Termo de Securitização de Recebíveis", header));
			pa.add(new Chunk(", doravante designado simplesmente ", normal));
			pa.add(new Chunk("TERMO", header));
			pa.add(new Chunk(
					", para aglutinar os créditos empresariais mercantis & industriais adquiridos e relacionados no(s) ANEXO(s), lastreando e vinculando tais créditos ao CERTIFICADO	DE RECEBÍVEIS, representado pelas Debêntures de sua 2ª emissão, especialmente à SÉRIE "
							+ this.debenturesInvestidor.getSerie()
							+ ", cujas características acham-se discriminadas na ",
					normal));
			pa.add(new Chunk("CLÁUSULA 4ª", header));
			pa.add(new Chunk(" deste ", normal));
			pa.add(new Chunk("TERMO", header));
			pa.add(new Chunk(";", normal));

			cell1 = new PdfPCell(pa);
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_TOP);
			cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(
					new Phrase("CLÁUSULA 2ª - DOS CRÉDITOS: ORIGEM, DATA DA CONSTITUIÇÃO E CARACTERÍSTICAS:", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setColspan(6);
			table.addCell(cell1);

			pa = new Paragraph();
			pa.add(new Chunk("Os créditos constantes do presente TERMO foram adquiridos pela ", normal));
			pa.add(new Chunk("SECURITIZADORA", header));
			pa.add(new Chunk(
					" a partir de negócios de Cessão de Créditos realizados com diversos originadores (CEDENTES), com o objetivo de serem eles (os créditos) securitizados, os quais são oriundos de transações mercantis, imobiliárias e industriais a prazo, cujos dados dos respectivos títulos estão discriminados no(s) ANEXO(s) integrante(s) deste TERMO;",
					normal));

			cell1 = new PdfPCell(pa);
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_TOP);
			cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(
					"CLÁUSULA 3ª - DO VALOR NOMINAL DOS CRÉDITOS E DOS PAGAMENTOS DA SECURITIZADORA:", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setColspan(6);
			table.addCell(cell1);

			if (this.debenturesInvestidor.isLastrearTitulos()) {
				valorPorExtenso.setNumber(this.valorPorcentagemAnexoI);
				pa = new Paragraph();
				pa.add(new Chunk("O valor nominal e total dos créditos que lastreiam a presente emissão deve ser de R$ "
						+ df.format(this.valorPorcentagemAnexoI) + " (" + valorPorExtenso.toString()
						+ "). O valor decorrerá do resultado da somatória dos títulos elencados no ANEXO I e seus derivados. Tais créditos serão pagos pelos devedores diretamente à SECURITIZADORA , que, por sua vez, se responsabiliza perante o ",
						normal));
				pa.add(new Chunk("DEBENTURISTA", header));
				pa.add(new Chunk(" da série " + this.debenturesInvestidor.getSerie() + " da 2ª EMISSÃO ", normal));
				pa.add(new Chunk(this.selectedPagador.getNome(), header));
				pa.add(new Chunk(
						", através da liquidação dos títulos adquiridos e ocorridos quando do seu vencimento. À medida que os créditos elencados no ANEXO I atingirem as respectivas datas de vencimentos, novos títulos serão adquiridos com os valores havidos da liquidação dos primeiros, compondo estes novos a lista dos ANEXOS subsequentes, o mesmo se dando à medida que os últimos foram liquidados e dos valores da liquidação destes sirvam para adquirir novos títulos dando sequência até a liquidação das DEBÊNTURES das séries "
								+ this.debenturesInvestidor.getSerie()
								+ " da 1ª Emissão, todos integrantes deste TERMO, a ponto de não permitir que o total de créditos dos ANEXOS seja inferior ao valor das DEBÊNURES . Parágrafo único: Os anexos que relacionam os títulos servirão de lastro da presente emissão.",
						normal));
			} else {
				pa = new Paragraph();
				pa.add(new Chunk("A presente emissão não possui títulos lastreados. Os créditos serão pagos pela ",
						normal));
				pa.add(new Chunk("SECURITIZADORA", header));
				pa.add(new Chunk(", que, por sua vez, se responsabiliza perante o ", normal));
				pa.add(new Chunk("DEBENTURISTA", header));
				pa.add(new Chunk(" da série " + this.debenturesInvestidor.getSerie() + " da 2a EMISSÃO "
						+ sdfDataRel.format(this.debenturesInvestidor.getDataDebentures()) + " (", normal));
				pa.add(new Chunk(this.selectedPagador.getNome(), header));
				pa.add(new Chunk(
						"), através da liquidação dos títulos adquiridos e ocorridos quando do seu vencimento.",
						normal));
			}

			cell1 = new PdfPCell(pa);
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_TOP);
			cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(
					new Phrase("CLÁUSULA 4ª - DAS CARACTERÍSTICAS DA EMISSÃO DOS CERTIFICADOS DE RECEBÍVEIS", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("CONSUBSTANCIADOS NAS DEBÊNTURES", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(
					"A emissão privada das Debêntures, que consubstanciam o CERTIFICADO DE RECEBÍVEIS, observa as condições e características adiante descritas:",
					normal));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_TOP);
			cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("a) Número de Ordem:", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			pa = new Paragraph();
			pa.add(new Chunk(
					"A(s) série(s) da(s) debênture(s) adquirida(s) são da 2ª Emissão Particular de Debêntures da ",
					normal));
			pa.add(new Chunk("SECURITIZADORA.", header));

			cell1 = new PdfPCell(pa);
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_TOP);
			cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			pa = new Paragraph();
			pa.add(new Chunk("b) Data de Emissão: ", header));
			pa.add(new Chunk(sdfDataRel.format(this.debenturesInvestidor.getDataDebentures()), normal));

			cell1 = new PdfPCell(pa);
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			pa = new Paragraph();
			pa.add(new Chunk("c) Data da Subscrição: ", header));
			pa.add(new Chunk(sdfDataRel.format(this.debenturesInvestidor.getDataDebentures()), normal));

			cell1 = new PdfPCell(pa);
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			pa = new Paragraph();
			pa.add(new Chunk("d) Data da Integralização: ", header));
			pa.add(new Chunk(sdfDataRel.format(this.debenturesInvestidor.getDataDebentures()), normal));

			cell1 = new PdfPCell(pa);
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			pa = new Paragraph();
			pa.add(new Chunk("e) Número da Cautela: ", header));
			pa.add(new Chunk(this.debenturesInvestidor.getNumeroCautela(), normal));

			cell1 = new PdfPCell(pa);
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			numeroPorExtenso.setNumber(this.debenturesInvestidor.getQtdeDebentures());

			BigDecimal totalDebentures = new BigDecimal(1000)
					.multiply(new BigDecimal(this.debenturesInvestidor.getQtdeDebentures()));
			valorPorExtenso.setNumber(totalDebentures);

			pa = new Paragraph();
			pa.add(new Chunk("f) ", header));
			pa.add(new Chunk("São adquiridas pelo ", normal));
			pa.add(new Chunk("DEBENTURISTA", header));
			pa.add(new Chunk(" " + this.debenturesInvestidor.getQtdeDebentures() + " (" + numeroPorExtenso.toString()
					+ ") Debêntures da Série " + this.debenturesInvestidor.getSerie()
					+ ", consubstanciando o CERTIFICADO DE RECEBÍVEIS, com valor nominal unitário de R$ 1.000,00 (um mil reais), totalizando R$ "
					+ df.format(totalDebentures) + " (" + valorPorExtenso.toString() + ").", normal));

			cell1 = new PdfPCell(pa);
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			pa = new Paragraph();
			pa.add(new Chunk("g) Prazo e Data de Vencimento: ", header));
			pa.add(new Chunk(
					"as debêntures adquiridas possuem prazo de vencimento pré-fixado, sendo que para as Debêntures de Série "
							+ this.debenturesInvestidor.getSerie() + ", foi determinado o vencimento de "
							+ sdfDataRel.format(this.debenturesInvestidor.getDataVencimento()) + ".",
					normal));

			cell1 = new PdfPCell(pa);
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			pa = new Paragraph();
			pa.add(new Chunk("h) Forma de Termo: ", header));
			pa.add(new Chunk("Escritural", normal));

			cell1 = new PdfPCell(pa);
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("i) Titularidade originária: SECURITIZADORA.", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			cell1.setPaddingBottom(40f);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("CLÁUSULA 5ª - DA COLOCAÇÃO E SEU PROCEDIMENTO:", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setColspan(6);
			table.addCell(cell1);

			pa = new Paragraph();
			pa.add(new Chunk("A distribuição primária das Debêntures, constante deste TERMO, ", normal));
			pa.add(new Chunk(
					"foi efetuada particularmente, sem intermediação de qualquer instituição ou agente do mercado, de acordo com as determinações inseridas no Parecer CVM/SJU/No 005-19.02.86, inexistindo reservas antecipadas, lotes mínimos ou máximos",
					header));
			pa.add(new Chunk(
					". A sua emissão foi autorizada pela Assembléia Geral Extraordinária dos Acionistas da SECURITIZADORA, datada de 12/08/2019, e sua emissão foi efetuada através de Escritura Pública de 2ª Emissão de Debêntures Subordinadas lavrada na Junta Comercial do Estado de São Paulo sob o n° ED003063-6/000",
					normal));

			cell1 = new PdfPCell(pa);
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_TOP);
			cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("CLÁUSULA 6ª - PREÇO DE SUBSCRIÇÃO E FORMA DE INTEGRALIZAÇÃO:", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setColspan(6);
			table.addCell(cell1);

			pa = new Paragraph();
			pa.add(new Chunk(
					"O valor nominal de subscrição das Debêntures está descrito no Quadro constante da CLÁUSULA 4ª e está lastreado pelo total dos créditos constante do ANEXO I. A integralização é à vista, em moeda corrente nacional, no ato da subscrição, sendo os valores integralizados pagos através de crédito em conta corrente bancária da ",
					normal));
			pa.add(new Chunk("SECURITIZADORA", header));
			pa.add(new Chunk(";", normal));

			cell1 = new PdfPCell(pa);
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_TOP);
			cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("CLÁUSULA 7ª - DO INADIMPLEMENTO E SUAS CONSEQUÊNCIAS:", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setColspan(6);
			table.addCell(cell1);

			pa = new Paragraph();
			pa.add(new Chunk(
					"Ocorrendo a mora por parte do(a,s) sacado(a,s) de qualquer título, ora securitizado, este responderá perante a SECURITIZADORA e, em solidariedade a ele(a,s), os devedores co-responsáveis: seu(s) avalista(s), endossante(s) e/ou demais coobrigados solidários, que serão obrigados a pagar o valor principal com os respectivos encargos, de conformidade com o estabelecido nos Contratos de Compromisso de Cessão de Créditos e outras avenças, celebrado entre a SECURITIZADORA e os originadores dos títulos negociados;",
					normal));

			cell1 = new PdfPCell(pa);
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_TOP);
			cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			pa = new Paragraph();
			pa.add(new Chunk(
					"Parágrafo primeiro - RESPONSABILIDADE PELO(S) CRÉDITO(S) ADQUIRIDOS(S): muito embora as Debêntures terem sido emitidas pela SECURITIZADORA, lastreadas, oportunamente, nos títulos de crédito por ela avaliados, escolhidos e, por fim adquiridos, o risco do	negócio firmado e da garantia em que ele é lastreado, será suportado exclusivamente pelo DEBENTURISTA.",
					normal));

			cell1 = new PdfPCell(pa);
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_TOP);
			cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			pa = new Paragraph();
			pa.add(new Chunk(
					"Parágrafo segundo – todas as despesas decorrentes de cobranças extrajudiciais e/ou judiciais para cumprimento das obrigações do(s) devedor(s) e seus corresponsáveis, bem como execução das respectivas garantias serão de responsabilidade do DEBENTURISTA, o qual também terá o direito em receber as receitas decorrentes dos encargos contratuais de acordo com o que firmado nos Contratos de Compromisso de Cessão de Créditos e outras avenças celebrado entre a SECURITIZADORA e os originadores dos títulos negociados.",
					normal));

			cell1 = new PdfPCell(pa);
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_TOP);
			cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("CLÁUSULA 8ª - DO FORO:", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setColspan(6);
			table.addCell(cell1);

			pa = new Paragraph();
			pa.add(new Chunk(
					"Fica eleito o foro da comarca de Campinas/SP para dirimir quaisquer questões oriundas do presente ",
					normal));
			pa.add(new Chunk("TERMO", header));
			pa.add(new Chunk(" , com exclusão de qualquer outro, por mais privilegiado que seja.", normal));

			cell1 = new PdfPCell(pa);
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_TOP);
			cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			pa = new Paragraph();
			pa.add(new Chunk("Atendidas todas as condições legais para firmar o presente ", normal));
			pa.add(new Chunk("TERMO", header));
			pa.add(new Chunk(", a ", normal));
			pa.add(new Chunk("SECURITIZADORA", header));
			pa.add(new Chunk(
					" subscreve este documento para que possa,	então, ter por securitizados os créditos cedidos e constantes do(s) ANEXO(s) integrante(s) deste, bem como para tê-los como lastro do CERTIFICADO DE RECEBÍVEIS, representados por Debêntures acima referidas.",
					normal));

			cell1 = new PdfPCell(pa);
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_TOP);
			cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("______________________________________________", normal));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(60f);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Galleria Finanças Securitizadora S.A.", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(6);
			table.addCell(cell1);

			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Termo de Securitização: Este documento está aberto por algum outro programa, por favor, feche-o e tente novamente!"
							+ e,
					""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Termo de Securitização: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.termoSecuritizacaoPDFGerado = true;

			if (doc != null) {
				// fechamento do documento
				doc.close();
			}
			if (os != null) {
				// fechamento da stream de saída
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void geraDebenture() {
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("#,##0.00");
		Locale.setDefault(new Locale("pt", "BR"));

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.
		 * VGpT0_nF_h4
		 */

		Document doc = null;
		OutputStream os = null;

		NumeroPorExtenso numeroPorExtenso = new NumeroPorExtenso();

		try {
			Font header = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			Font headerFullBold = new Font(FontFamily.HELVETICA, 14, Font.BOLD);
			Font headerFull = new Font(FontFamily.HELVETICA, 14);
			Font headerFullUnderline = new Font(FontFamily.HELVETICA, 14, Font.BOLD | Font.UNDERLINE);

			Font title = new Font(FontFamily.HELVETICA, 12);
			Font titleBold = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

			Font headerFullRed = new Font(FontFamily.HELVETICA, 16, Font.NORMAL, BaseColor.RED);
			Font headerFullRedBold = new Font(FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.RED);

			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font tituloSmall = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
			Font tituloBranco = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloBranco.setColor(BaseColor.WHITE);
			Font normalBold = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font normal = new Font(FontFamily.HELVETICA, 10);
			Font normal12 = new Font(FontFamily.HELVETICA, 12);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getDefault();
			Locale locale = new Locale("pt", "BR");
			Calendar date = Calendar.getInstance(zone, locale);
			SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MM/yyyy", locale);
			SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", locale);

			SimpleDateFormat dia = new SimpleDateFormat("dd", locale);
			SimpleDateFormat mes = new SimpleDateFormat("MMMMM", locale);
			SimpleDateFormat ano = new SimpleDateFormat("yyyy", locale);

			ParametrosDao pDao = new ParametrosDao();
			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */

			doc = new Document(PageSize.A4, 30, 30, 30, 30);
			this.nomePDF = "Debenture.pdf";
			this.pathPDF = pDao.findByFilter("nome", "RECIBOS_IUGU").get(0).getValorString();

			os = new FileOutputStream(this.pathPDF + this.nomePDF);

			// Associa a stream de saída ao
			PdfWriter.getInstance(doc, os);

			// Abre o documento
			doc.open();
			/*
			 * Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			 * p1.setAlignment(Element.ALIGN_CENTER); p1.setSpacingAfter(10); doc.add(p1);
			 */
			PdfPTable table = new PdfPTable(new float[] { 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f });
			table.setWidthPercentage(100.0f);

			PdfPCell cell1 = new PdfPCell(new Phrase("GALLERIA FINANÇAS SECURITIZADORA S.A.", headerFullUnderline));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setPaddingTop(20f);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("CNPJ/MF: 34.425.347/0001-06", headerFullBold));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Rua Avenida Doutor José Bonifácio Coutinho, 150", normal12));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Jardim Madalena - Campinas - SP", normal12));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(6);
			table.addCell(cell1);

			Paragraph pa = new Paragraph();
			pa.add(new Chunk("Data de Constituição da Sociedade: ", normalBold));
			pa.add(new Chunk(
					" 05/08/2019, com seus atos constitutivos arquivados na Junta Comercial do Estado do São Paulo em 02 de Setembro de 2019, sob o nº ED003063-6/000.",
					normal));

			cell1 = new PdfPCell(pa);
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(10f);
			cell1.setVerticalAlignment(Element.ALIGN_TOP);
			cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(40f);
			cell1.setColspan(3);
			table.addCell(cell1);

			pa = new Paragraph();
			pa.add(new Chunk("Objeto Social: ", normalBold));
			pa.add(new Chunk(
					" A Sociedade tem por Objeto a aquisição e securitização de recebíveis comerciais e industriais. ",
					normal));

			cell1 = new PdfPCell(pa);
			cell1.setBorder(0);
			cell1.setPaddingLeft(10f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_TOP);
			cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(40f);
			cell1.setColspan(3);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Prazo de Duração da Sociedade: Indeterminado", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(5f);
			cell1.setPaddingTop(40f);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("DEBÊNTURES SIMPLES, SUBORDINADAS", headerFullRed));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(40f);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Cautela N°", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(3);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Quantidade de Debêntures", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(3);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase(this.debenturesInvestidor.getNumeroCautela(), headerFullRedBold));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(40f);
			cell1.setColspan(3);
			table.addCell(cell1);

			cell1 = new PdfPCell(
					new Phrase(String.valueOf(this.debenturesInvestidor.getQtdeDebentures()), headerFullRedBold));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(40f);
			cell1.setColspan(3);
			table.addCell(cell1);

			BigDecimal valorDebenture = new BigDecimal(this.debenturesInvestidor.getQtdeDebentures())
					.multiply(new BigDecimal(1000));

			String documento = "";

			if (this.selectedPagador != null) {
				if (this.selectedPagador.getCpf() != null) {
					documento = this.selectedPagador.getCpf();
				} else {
					documento = this.selectedPagador.getCnpj();
				}
			} else {
				if (this.selectedContratoLov.getPagador().getCpf() != null) {
					documento = this.selectedContratoLov.getPagador().getCpf();
				} else {
					documento = this.selectedContratoLov.getPagador().getCnpj();
				}
			}

			numeroPorExtenso.setNumber(this.debenturesInvestidor.getQtdeDebentures());

			BigDecimal totalDebentures = new BigDecimal(1000)
					.multiply(new BigDecimal(this.debenturesInvestidor.getQtdeDebentures()));
			ValorPorExtenso valorPorExtenso = new ValorPorExtenso(totalDebentures);

			cell1 = new PdfPCell(new Phrase("Esta cautela representa " + numeroPorExtenso.toString()
					+ " debêntures, não conversíveis em ações, da 2ª (segunda) emissão privada, série "
					+ this.debenturesInvestidor.getSerie()
					+ ", no valor nominal unitário de R$ 1.000,00 (Um mil reais), totalizando R$ "
					+ df.format(totalDebentures)
					+ ", e demais características especificadas no instrumento particular de escritura da primeira emissão privada de debêntures simples da Galleria Finanças Securitizadora S.A., ficando disponível cópia autenticada na sede desta companhia. Confere a "
					+ this.selectedPagador.getNome() + ", " + documento
					+ ", os direitos que a lei e a escritura de emissão lhes asseguram.", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(40f);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(
					new Phrase("Campinas (SP), " + dia.format(this.debenturesInvestidor.getDataDebentures()) + " de "
							+ mes.format(this.debenturesInvestidor.getDataDebentures()) + " de "
							+ ano.format(this.debenturesInvestidor.getDataDebentures()), headerFull));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(40f);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("___________________________________________________", normal));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(80f);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Galleria Finanças Securitizadora S.A.", titleBold));
			cell1.setBorder(0);
			cell1.setPaddingLeft(21f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(6);
			table.addCell(cell1);
			/*
			 * cell1 = new PdfPCell(new Phrase("Fabricio Figueiredo", title));
			 * cell1.setBorder(0); cell1.setPaddingLeft(20f); cell1.setPaddingRight(10f);
			 * cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			 * cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * cell1.setBackgroundColor(BaseColor.WHITE); cell1.setColspan(3);
			 * table.addCell(cell1);
			 * 
			 * cell1 = new PdfPCell(new Phrase("João Augusto Magatti Alves", title));
			 * cell1.setBorder(0); cell1.setPaddingLeft(10f); cell1.setPaddingRight(20f);
			 * cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			 * cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * cell1.setBackgroundColor(BaseColor.WHITE); cell1.setColspan(3);
			 * table.addCell(cell1);
			 * 
			 * cell1 = new PdfPCell(new Phrase("Diretor Comercial", title));
			 * cell1.setBorder(0); cell1.setPaddingLeft(20f); cell1.setPaddingRight(10f);
			 * cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			 * cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * cell1.setBackgroundColor(BaseColor.WHITE); cell1.setColspan(3);
			 * table.addCell(cell1);
			 * 
			 * cell1 = new PdfPCell(new Phrase("Diretor Administrativo", title));
			 * cell1.setBorder(0); cell1.setPaddingLeft(10f); cell1.setPaddingRight(20f);
			 * cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			 * cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			 * cell1.setBackgroundColor(BaseColor.WHITE); cell1.setColspan(3);
			 * table.addCell(cell1);
			 */
			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debênture: Este documento está aberto por algum outro programa, por favor, feche-o e tente novamente!"
							+ e,
					""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debênture: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.debenturePDFGerado = true;

			if (doc != null) {
				// fechamento do documento
				doc.close();
			}
			if (os != null) {
				// fechamento da stream de saída
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void geraPDFValorLiquidoInvestidores() {
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("#,##0.00");
		Locale.setDefault(new Locale("pt", "BR"));

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.
		 * VGpT0_nF_h4
		 */

		Document doc = null;
		OutputStream os = null;

		try {
			Font header = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			Font headerFull = new Font(FontFamily.HELVETICA, 16, Font.BOLD);

			Font headerFullRed = new Font(FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.RED);

			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font tituloSmall = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
			Font tituloBranco = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloBranco.setColor(BaseColor.WHITE);
			Font normal = new Font(FontFamily.HELVETICA, 10);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getDefault();
			Locale locale = new Locale("pt", "BR");
			Calendar date = Calendar.getInstance(zone, locale);
			SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MM/yyyy", locale);
			SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", locale);

			SimpleDateFormat dia = new SimpleDateFormat("dd", locale);
			SimpleDateFormat mes = new SimpleDateFormat("MMMMM", locale);
			SimpleDateFormat ano = new SimpleDateFormat("yyyy", locale);

			ParametrosDao pDao = new ParametrosDao();
			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */

			doc = new Document(PageSize.A4, 10, 10, 10, 10);
			this.nomePDF = "Investidores - Valores Líquidos a Receber.pdf";
			this.pathPDF = pDao.findByFilter("nome", "RECIBOS_IUGU").get(0).getValorString();

			os = new FileOutputStream(this.pathPDF + this.nomePDF);

			// Associa a stream de saída ao
			PdfWriter.getInstance(doc, os);

			// Abre o documento
			doc.open();
			/*
			 * Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			 * p1.setAlignment(Element.ALIGN_CENTER); p1.setSpacingAfter(10); doc.add(p1);
			 */
			PdfPTable table = new PdfPTable(new float[] { 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f });
			table.setWidthPercentage(100.0f);

			PdfPCell cell1 = new PdfPCell(new Phrase("GALLERIA FINANÇAS - Investidores - Valores Líquidos a Receber - "
					+ sdfDataRel.format(this.dataInicio) + " a " + sdfDataRel.format(this.dataFim), header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(6);
			table.addCell(cell1);

			PagadorRecebedor investidorTemp = new PagadorRecebedor();

			for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorSA) {
				// Popula investidor para fazer a quebra do relatório
				if (investidorTemp != parcelas.getInvestidor()) {
					// se for a primeira passada não terá total
					if (investidorTemp.getId() > 0) {
						cell1 = new PdfPCell(new Phrase("Total:", titulo));
						cell1.setBorder(0);
						cell1.setBorderWidthLeft(1);
						cell1.setBorderColorLeft(BaseColor.BLACK);
						cell1.setBorderWidthRight(1);
						cell1.setBorderColorRight(BaseColor.BLACK);
						cell1.setBorderWidthBottom(1);
						cell1.setBorderColorBottom(BaseColor.BLACK);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingBottom(5f);
						cell1.setColspan(4);
						table.addCell(cell1);

						cell1 = new PdfPCell(
								new Phrase(df.format(getTotalParcelaInvestidorSA(investidorTemp.getId())), normal));
						cell1.setBorder(0);
						cell1.setBorderWidthRight(1);
						cell1.setBorderColorRight(BaseColor.BLACK);
						cell1.setBorderWidthBottom(1);
						cell1.setBorderColorBottom(BaseColor.BLACK);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingBottom(5f);
						cell1.setColspan(1);
						table.addCell(cell1);

						cell1 = new PdfPCell(
								new Phrase(df.format(getTotalLiquidoInvestidorSA(investidorTemp.getId())), normal));
						cell1.setBorder(0);
						cell1.setBorderWidthRight(1);
						cell1.setBorderColorRight(BaseColor.BLACK);
						cell1.setBorderWidthBottom(1);
						cell1.setBorderColorBottom(BaseColor.BLACK);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingBottom(5f);
						cell1.setColspan(1);
						table.addCell(cell1);
					}

					investidorTemp = parcelas.getInvestidor();

					cell1 = new PdfPCell(new Phrase(investidorTemp.getNome(), header));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell1.setPaddingTop(10f);
					cell1.setPaddingBottom(10f);
					cell1.setUseBorderPadding(true);
					cell1.setColspan(6);
					table.addCell(cell1);

					if (investidorTemp.getCpf() != null && !investidorTemp.getCpf().equals("")) {
						cell1 = new PdfPCell(new Phrase(
								"CPF " + investidorTemp.getCpf() + " | Banco " + investidorTemp.getBanco() + " | AG."
										+ investidorTemp.getAgencia() + " C/C " + investidorTemp.getConta(),
								titulo));
					} else {
						cell1 = new PdfPCell(new Phrase(
								"CNPJ " + investidorTemp.getCnpj() + " | Banco " + investidorTemp.getBanco() + " | AG."
										+ investidorTemp.getAgencia() + " C/C " + investidorTemp.getConta(),
								titulo));
					}

					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell1.setPaddingTop(10f);
					cell1.setPaddingBottom(10f);
					cell1.setUseBorderPadding(true);
					cell1.setColspan(6);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase("Contrato", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase("Pagador", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(2);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase("Data de Vencimento", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase("Valor Bruto da Parcela", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase("Valor Líquido a Receber", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);
				}

				cell1 = new PdfPCell(new Phrase(parcelas.getNumeroContrato(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(parcelas.getPagador().getNome(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(parcelas.getDataVencimento()), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(df.format(parcelas.getParcelaMensal()), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(df.format(parcelas.getValorLiquido()), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
			}

			// gera a última linha de total dos registros e total geral
			if (this.parcelasInvestidorSA.size() > 0) {
				if (investidorTemp.getId() > 0) {
					cell1 = new PdfPCell(new Phrase("Total:", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(4);
					table.addCell(cell1);

					cell1 = new PdfPCell(
							new Phrase(df.format(getTotalParcelaInvestidorSA(investidorTemp.getId())), normal));
					cell1.setBorder(0);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);

					cell1 = new PdfPCell(
							new Phrase(df.format(getTotalLiquidoInvestidorSA(investidorTemp.getId())), normal));
					cell1.setBorder(0);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);
				}

				cell1 = new PdfPCell(new Phrase(
						"Valor Líquido Total: R$ " + df.format(getTotalLiquidoTodosInvestidoresSA()), header));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell1.setPaddingTop(15f);
				cell1.setPaddingBottom(15f);
				cell1.setUseBorderPadding(true);
				cell1.setColspan(6);
				table.addCell(cell1);
			}

			if (this.parcelasInvestidorCorrespondente.size() > 0) {
				cell1 = new PdfPCell(new Phrase(
						"GALLERIA CORRESPONDENTE - Investidores - Valores Líquidos a Receber - "
								+ sdfDataRel.format(this.dataInicio) + " a " + sdfDataRel.format(this.dataFim),
						header));
				cell1.setBorder(0);
				cell1.setPaddingLeft(8f);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(10f);
				cell1.setPaddingTop(20f);
				cell1.setColspan(6);
				table.addCell(cell1);
			}

			investidorTemp = new PagadorRecebedor();

			for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorCorrespondente) {
				// Popula investidor para fazer a quebra do relatório
				if (investidorTemp != parcelas.getInvestidor()) {
					// se for a primeira passada não terá total
					if (investidorTemp.getId() > 0) {
						cell1 = new PdfPCell(new Phrase("Total:", titulo));
						cell1.setBorder(0);
						cell1.setBorderWidthLeft(1);
						cell1.setBorderColorLeft(BaseColor.BLACK);
						cell1.setBorderWidthRight(1);
						cell1.setBorderColorRight(BaseColor.BLACK);
						cell1.setBorderWidthBottom(1);
						cell1.setBorderColorBottom(BaseColor.BLACK);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingBottom(5f);
						cell1.setColspan(4);
						table.addCell(cell1);

						cell1 = new PdfPCell(new Phrase(
								df.format(getTotalParcelaInvestidorCorrespondente(investidorTemp.getId())), normal));
						cell1.setBorder(0);
						cell1.setBorderWidthRight(1);
						cell1.setBorderColorRight(BaseColor.BLACK);
						cell1.setBorderWidthBottom(1);
						cell1.setBorderColorBottom(BaseColor.BLACK);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingBottom(5f);
						cell1.setColspan(1);
						table.addCell(cell1);

						cell1 = new PdfPCell(new Phrase(
								df.format(getTotalLiquidoInvestidorCorrespondente(investidorTemp.getId())), normal));
						cell1.setBorder(0);
						cell1.setBorderWidthRight(1);
						cell1.setBorderColorRight(BaseColor.BLACK);
						cell1.setBorderWidthBottom(1);
						cell1.setBorderColorBottom(BaseColor.BLACK);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingBottom(5f);
						cell1.setColspan(1);
						table.addCell(cell1);
					}

					investidorTemp = parcelas.getInvestidor();

					cell1 = new PdfPCell(new Phrase(investidorTemp.getNome(), header));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell1.setPaddingTop(10f);
					cell1.setPaddingBottom(10f);
					cell1.setUseBorderPadding(true);
					cell1.setColspan(6);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase(
							"CPF " + investidorTemp.getCpf() + " | Banco " + investidorTemp.getBanco() + " | AG."
									+ investidorTemp.getAgencia() + " C/C " + investidorTemp.getConta(),
							titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell1.setPaddingTop(10f);
					cell1.setPaddingBottom(10f);
					cell1.setUseBorderPadding(true);
					cell1.setColspan(6);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase("Contrato", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase("Pagador", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(2);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase("Data de Vencimento", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase("Valor Bruto da Parcela", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase("Valor Líquido a Receber", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);
				}

				cell1 = new PdfPCell(new Phrase(parcelas.getNumeroContrato(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(parcelas.getPagador().getNome(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(parcelas.getDataVencimento()), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(df.format(parcelas.getParcelaMensal()), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(df.format(parcelas.getValorLiquido()), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
			}

			// gera a última linha de total dos registros e total geral
			if (this.parcelasInvestidorCorrespondente.size() > 0) {
				if (investidorTemp.getId() > 0) {
					cell1 = new PdfPCell(new Phrase("Total:", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(4);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase(
							df.format(getTotalParcelaInvestidorCorrespondente(investidorTemp.getId())), normal));
					cell1.setBorder(0);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase(
							df.format(getTotalLiquidoInvestidorCorrespondente(investidorTemp.getId())), normal));
					cell1.setBorder(0);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);
				}

				cell1 = new PdfPCell(new Phrase(
						"Valor Líquido Total: R$ " + df.format(getTotalLiquidoTodosInvestidoresCorrespondente()),
						header));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell1.setPaddingTop(15f);
				cell1.setPaddingBottom(15f);
				cell1.setUseBorderPadding(true);
				cell1.setColspan(6);
				table.addCell(cell1);
			}

			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Investidores: Este documento está aberto por algum outro programa, por favor, feche-o e tente novamente!"
							+ e,
					""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Investidores: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.valoresLiquidosInvestidoresPDFGerado = true;

			if (doc != null) {
				// fechamento do documento
				doc.close();
			}
			if (os != null) {
				// fechamento da stream de saída
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void geraPDFValorLiquidoInvestidoresEnvelope() {
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("#,##0.00");
		Locale.setDefault(new Locale("pt", "BR"));

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.
		 * VGpT0_nF_h4
		 */

		Document doc = null;
		OutputStream os = null;

		try {
			Font header = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			Font headerFull = new Font(FontFamily.HELVETICA, 16, Font.BOLD);

			Font headerFullRed = new Font(FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.RED);

			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font tituloSmall = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
			Font tituloBranco = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloBranco.setColor(BaseColor.WHITE);
			Font normal = new Font(FontFamily.HELVETICA, 10);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getDefault();
			Locale locale = new Locale("pt", "BR");
			Calendar date = Calendar.getInstance(zone, locale);
			SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MM/yyyy", locale);
			SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", locale);

			SimpleDateFormat dia = new SimpleDateFormat("dd", locale);
			SimpleDateFormat mes = new SimpleDateFormat("MMMMM", locale);
			SimpleDateFormat ano = new SimpleDateFormat("yyyy", locale);

			ParametrosDao pDao = new ParametrosDao();
			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */

			doc = new Document(PageSize.A4, 10, 10, 10, 10);
			this.nomePDF = "Investidores - Valores Líquidos a Receber - Envelope.pdf";
			this.pathPDF = pDao.findByFilter("nome", "RECIBOS_IUGU").get(0).getValorString();

			os = new FileOutputStream(this.pathPDF + this.nomePDF);

			// Associa a stream de saída ao
			PdfWriter.getInstance(doc, os);

			// Abre o documento
			doc.open();
			/*
			 * Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			 * p1.setAlignment(Element.ALIGN_CENTER); p1.setSpacingAfter(10); doc.add(p1);
			 */
			PdfPTable table = new PdfPTable(new float[] { 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f });
			table.setWidthPercentage(100.0f);

			PdfPCell cell1 = new PdfPCell(new Phrase("Investidores - Valores Líquidos a Receber - Envelope - "
					+ sdfDataRel.format(this.dataInicio) + " a " + sdfDataRel.format(this.dataFim), header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(6);
			table.addCell(cell1);

			PagadorRecebedor investidorTemp = new PagadorRecebedor();

			for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorEnvelope) {
				// Popula investidor para fazer a quebra do relatório
				if (investidorTemp != parcelas.getInvestidor()) {
					// se for a primeira passada não terá total
					if (investidorTemp.getId() > 0) {
						cell1 = new PdfPCell(new Phrase("Total:", titulo));
						cell1.setBorder(0);
						cell1.setBorderWidthLeft(1);
						cell1.setBorderColorLeft(BaseColor.BLACK);
						cell1.setBorderWidthRight(1);
						cell1.setBorderColorRight(BaseColor.BLACK);
						cell1.setBorderWidthBottom(1);
						cell1.setBorderColorBottom(BaseColor.BLACK);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingBottom(5f);
						cell1.setColspan(4);
						table.addCell(cell1);

						cell1 = new PdfPCell(new Phrase(
								df.format(getTotalParcelaInvestidorEnvelope(investidorTemp.getId())), normal));
						cell1.setBorder(0);
						cell1.setBorderWidthRight(1);
						cell1.setBorderColorRight(BaseColor.BLACK);
						cell1.setBorderWidthBottom(1);
						cell1.setBorderColorBottom(BaseColor.BLACK);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingBottom(5f);
						cell1.setColspan(1);
						table.addCell(cell1);

						cell1 = new PdfPCell(new Phrase(
								df.format(getTotalLiquidoInvestidorEnvelope(investidorTemp.getId())), normal));
						cell1.setBorder(0);
						cell1.setBorderWidthRight(1);
						cell1.setBorderColorRight(BaseColor.BLACK);
						cell1.setBorderWidthBottom(1);
						cell1.setBorderColorBottom(BaseColor.BLACK);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingBottom(5f);
						cell1.setColspan(1);
						table.addCell(cell1);
					}

					investidorTemp = parcelas.getInvestidor();

					cell1 = new PdfPCell(new Phrase(investidorTemp.getNome(), header));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell1.setPaddingTop(10f);
					cell1.setPaddingBottom(10f);
					cell1.setUseBorderPadding(true);
					cell1.setColspan(6);
					table.addCell(cell1);

					if (investidorTemp.getCpf() != null && !investidorTemp.getCpf().equals("")) {
						cell1 = new PdfPCell(new Phrase(
								"CPF " + investidorTemp.getCpf() + " | Banco " + investidorTemp.getBanco() + " | AG."
										+ investidorTemp.getAgencia() + " C/C " + investidorTemp.getConta(),
								titulo));
					} else {
						cell1 = new PdfPCell(new Phrase(
								"CNPJ " + investidorTemp.getCnpj() + " | Banco " + investidorTemp.getBanco() + " | AG."
										+ investidorTemp.getAgencia() + " C/C " + investidorTemp.getConta(),
								titulo));
					}

					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell1.setPaddingTop(10f);
					cell1.setPaddingBottom(10f);
					cell1.setUseBorderPadding(true);
					cell1.setColspan(6);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase("Contrato", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase("Pagador", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(2);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase("Data de Vencimento", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase("Valor Bruto da Parcela", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase("Valor Líquido a Receber", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);
				}

				cell1 = new PdfPCell(new Phrase(parcelas.getNumeroContrato(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(parcelas.getPagador().getNome(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(2);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(parcelas.getDataVencimento()), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(df.format(parcelas.getParcelaMensal()), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(df.format(parcelas.getValorLiquido()), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
			}

			// gera a última linha de total dos registros e total geral
			if (this.parcelasInvestidorEnvelope.size() > 0) {
				if (investidorTemp.getId() > 0) {
					cell1 = new PdfPCell(new Phrase("Total:", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(4);
					table.addCell(cell1);

					cell1 = new PdfPCell(
							new Phrase(df.format(getTotalParcelaInvestidorEnvelope(investidorTemp.getId())), normal));
					cell1.setBorder(0);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);

					cell1 = new PdfPCell(
							new Phrase(df.format(getTotalLiquidoInvestidorEnvelope(investidorTemp.getId())), normal));
					cell1.setBorder(0);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);
				}

				cell1 = new PdfPCell(new Phrase(
						"Valor Líquido Total: R$ " + df.format(getTotalLiquidoTodosInvestidoresEnvelope()), header));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
				cell1.setPaddingTop(15f);
				cell1.setPaddingBottom(15f);
				cell1.setUseBorderPadding(true);
				cell1.setColspan(6);
				table.addCell(cell1);
			}

			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Investidores: Este documento está aberto por algum outro programa, por favor, feche-o e tente novamente!"
							+ e,
					""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Investidores: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.valoresLiquidosInvestidoresPDFGerado = true;

			if (doc != null) {
				// fechamento do documento
				doc.close();
			}
			if (os != null) {
				// fechamento da stream de saída
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void geraPDFIRRetidoInvestidores() {
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("#,##0.00");
		Locale.setDefault(new Locale("pt", "BR"));

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.
		 * VGpT0_nF_h4
		 */

		Document doc = null;
		OutputStream os = null;

		try {
			Font header = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			Font headerFull = new Font(FontFamily.HELVETICA, 16, Font.BOLD);

			Font headerFullRed = new Font(FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.RED);

			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font tituloSmall = new Font(FontFamily.HELVETICA, 5, Font.BOLD);
			Font tituloBranco = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloBranco.setColor(BaseColor.WHITE);
			Font normal = new Font(FontFamily.HELVETICA, 10);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getDefault();
			Locale locale = new Locale("pt", "BR");
			Calendar date = Calendar.getInstance(zone, locale);
			SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MM/yyyy", locale);
			SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", locale);

			SimpleDateFormat dia = new SimpleDateFormat("dd", locale);
			SimpleDateFormat mes = new SimpleDateFormat("MMMMM", locale);
			SimpleDateFormat ano = new SimpleDateFormat("yyyy", locale);

			ParametrosDao pDao = new ParametrosDao();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */

			doc = new Document(PageSize.A4, 10, 10, 10, 10);

			// Associa a stream de saída ao
			PdfWriter.getInstance(doc, baos);

			// Abre o documento
			doc.open();
			/*
			 * Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			 * p1.setAlignment(Element.ALIGN_CENTER); p1.setSpacingAfter(10); doc.add(p1);
			 */
			PdfPTable table = new PdfPTable(new float[] { 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f });
			table.setWidthPercentage(100.0f);

			PdfPCell cell1 = new PdfPCell(new Phrase("Investidores - IR Retido - " + sdfDataRel.format(this.dataInicio)
					+ " a " + sdfDataRel.format(this.dataFim), header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingBottom(10f);
			cell1.setColspan(6);
			table.addCell(cell1);

			PagadorRecebedor investidorTemp = new PagadorRecebedor();

			BigDecimal totalParcela = BigDecimal.ZERO;
			BigDecimal totalJuros = BigDecimal.ZERO;
			BigDecimal totalAmortizacao = BigDecimal.ZERO;
			BigDecimal totalIR = BigDecimal.ZERO;

			for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidor) {
				// Popula investidor para fazer a quebra do relatório
				if (investidorTemp != parcelas.getInvestidor()) {
					// se for a primeira passada não terá total
					if (investidorTemp.getId() > 0) {
						cell1 = new PdfPCell(new Phrase("Total:", titulo));
						cell1.setBorder(0);
						cell1.setBorderWidthLeft(1);
						cell1.setBorderColorLeft(BaseColor.BLACK);
						cell1.setBorderWidthRight(1);
						cell1.setBorderColorRight(BaseColor.BLACK);
						cell1.setBorderWidthBottom(1);
						cell1.setBorderColorBottom(BaseColor.BLACK);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingBottom(5f);
						cell1.setColspan(2);
						table.addCell(cell1);

						totalParcela = getTotalParcelaInvestidor(investidorTemp.getId());

						if (totalParcela == null || totalParcela.toString().equals("0.00")) {
							cell1 = new PdfPCell(new Phrase("0,00", normal));
						} else {
							cell1 = new PdfPCell(new Phrase(df.format(totalParcela), normal));
						}

						cell1.setBorder(0);
						cell1.setBorderWidthRight(1);
						cell1.setBorderColorRight(BaseColor.BLACK);
						cell1.setBorderWidthBottom(1);
						cell1.setBorderColorBottom(BaseColor.BLACK);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingBottom(5f);
						cell1.setColspan(1);
						table.addCell(cell1);

						totalJuros = getTotalJurosInvestidor(investidorTemp.getId());

						if (totalJuros == null || totalJuros.toString().equals("0.00")) {
							cell1 = new PdfPCell(new Phrase("0,00", normal));
						} else {
							cell1 = new PdfPCell(new Phrase(df.format(totalJuros), normal));
						}

						cell1.setBorder(0);
						cell1.setBorderWidthRight(1);
						cell1.setBorderColorRight(BaseColor.BLACK);
						cell1.setBorderWidthBottom(1);
						cell1.setBorderColorBottom(BaseColor.BLACK);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingBottom(5f);
						cell1.setColspan(1);
						table.addCell(cell1);

						totalAmortizacao = getTotalAmortizacaoInvestidor(investidorTemp.getId());

						if (totalAmortizacao == null || totalAmortizacao.toString().equals("0.00")) {
							cell1 = new PdfPCell(new Phrase("0,00", normal));
						} else {
							cell1 = new PdfPCell(new Phrase(df.format(totalAmortizacao), normal));
						}

						cell1.setBorder(0);
						cell1.setBorderWidthRight(1);
						cell1.setBorderColorRight(BaseColor.BLACK);
						cell1.setBorderWidthBottom(1);
						cell1.setBorderColorBottom(BaseColor.BLACK);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingBottom(5f);
						cell1.setColspan(1);
						table.addCell(cell1);

						totalIR = getTotalIRRetidoInvestidor(investidorTemp.getId());

						if (totalIR == null || totalIR.toString().equals("0.00")) {
							cell1 = new PdfPCell(new Phrase("0,00", normal));
						} else {
							cell1 = new PdfPCell(new Phrase(df.format(totalIR), normal));
						}

						cell1.setBorder(0);
						cell1.setBorderWidthRight(1);
						cell1.setBorderColorRight(BaseColor.BLACK);
						cell1.setBorderWidthBottom(1);
						cell1.setBorderColorBottom(BaseColor.BLACK);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingBottom(5f);
						cell1.setColspan(1);
						table.addCell(cell1);
					}

					investidorTemp = parcelas.getInvestidor();

					cell1 = new PdfPCell(
							new Phrase(investidorTemp.getNome() + " | CPF " + investidorTemp.getCpf(), header));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
					cell1.setPaddingTop(10f);
					cell1.setPaddingBottom(10f);
					cell1.setUseBorderPadding(true);
					cell1.setColspan(6);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase("Contrato", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase("Data da Baixa", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase("Valor Bruto da Parcela", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase("Juros", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase("Amortização", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);

					cell1 = new PdfPCell(new Phrase("Valor IR Retido", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthTop(1);
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBackgroundColor(BaseColor.GRAY);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);
				}

				cell1 = new PdfPCell(new Phrase(parcelas.getNumeroContrato(), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(parcelas.getDataBaixa()), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				cell1 = new PdfPCell(new Phrase(df.format(parcelas.getParcelaMensalBaixa()), normal));
				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				if (parcelas.getJurosBaixa() == null || parcelas.getJurosBaixa().toString().equals("0.00")) {
					cell1 = new PdfPCell(new Phrase("0,00", normal));
				} else {
					cell1 = new PdfPCell(new Phrase(df.format(parcelas.getJurosBaixa()), normal));
				}

				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				if (parcelas.getAmortizacao() == null || parcelas.getAmortizacao().toString().equals("0.00")) {
					cell1 = new PdfPCell(new Phrase("0,00", normal));
				} else {
					cell1 = new PdfPCell(new Phrase(df.format(parcelas.getAmortizacao()), normal));
				}

				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);

				if (parcelas.getIrRetido() == null || parcelas.getIrRetido().toString().equals("0.00")) {
					cell1 = new PdfPCell(new Phrase("0,00", normal));
				} else {
					cell1 = new PdfPCell(new Phrase(df.format(parcelas.getIrRetido()), normal));
				}

				cell1.setBorder(0);
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.BLACK);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.BLACK);
				cell1.setBorderWidthBottom(1);
				cell1.setBorderColorBottom(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingBottom(5f);
				cell1.setColspan(1);
				table.addCell(cell1);
			}

			// gera última linha do total
			if (this.parcelasInvestidor.size() > 0) {
				if (investidorTemp.getId() > 0) {
					cell1 = new PdfPCell(new Phrase("Total:", titulo));
					cell1.setBorder(0);
					cell1.setBorderWidthLeft(1);
					cell1.setBorderColorLeft(BaseColor.BLACK);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(2);
					table.addCell(cell1);

					totalParcela = getTotalParcelaInvestidor(investidorTemp.getId());

					if (totalParcela == null || totalParcela.toString().equals("0.00")) {
						cell1 = new PdfPCell(new Phrase("0,00", normal));
					} else {
						cell1 = new PdfPCell(new Phrase(df.format(totalParcela), normal));
					}

					cell1.setBorder(0);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);

					totalJuros = getTotalJurosInvestidor(investidorTemp.getId());

					if (totalJuros == null || totalJuros.toString().equals("0.00")) {
						cell1 = new PdfPCell(new Phrase("0,00", normal));
					} else {
						cell1 = new PdfPCell(new Phrase(df.format(totalJuros), normal));
					}

					cell1.setBorder(0);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);

					totalAmortizacao = getTotalAmortizacaoInvestidor(investidorTemp.getId());

					if (totalAmortizacao == null || totalAmortizacao.toString().equals("0.00")) {
						cell1 = new PdfPCell(new Phrase("0,00", normal));
					} else {
						cell1 = new PdfPCell(new Phrase(df.format(totalAmortizacao), normal));
					}

					cell1.setBorder(0);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);

					totalIR = getTotalIRRetidoInvestidor(investidorTemp.getId());

					if (totalIR == null || totalIR.toString().equals("0.00")) {
						cell1 = new PdfPCell(new Phrase("0,00", normal));
					} else {
						cell1 = new PdfPCell(new Phrase(df.format(totalIR), normal));
					}

					cell1.setBorder(0);
					cell1.setBorderWidthRight(1);
					cell1.setBorderColorRight(BaseColor.BLACK);
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(1);
					table.addCell(cell1);
				}
			}

			doc.add(table);
			doc.close();
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
					FacesContext.getCurrentInstance());
			String nomeArquivoDownload = String.format("Galleria Bank - IRretidoInvestidores.pdf", "");
			gerador.open(nomeArquivoDownload);
			gerador.feed(new ByteArrayInputStream(baos.toByteArray()));
			gerador.close();
			

		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Investidores: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.irRetidoInvestidoresPDFGerado = true;
				
			
		}
	}

	/**
	 * deleta o arquivo selecionado na tela
	 */
	public void deleteFile() {
		List<FileUploaded> arquivosDownload = (List<FileUploaded>) this.listaArquivos();

		for (FileUploaded f : arquivosDownload) {
			f.getFile().delete();
		}
	}

	/**
	 * Método para fazer download de todos os arquivos do diretório do contrato
	 * 
	 * @return
	 */
	public StreamedContent getDownloadAllFiles() {
		// limpa o diretório de PDFS
		deleteFile();

		// Pega a lista de Investidores
		PagadorRecebedorDao prDao = new PagadorRecebedorDao();
		this.listPagadores = prDao.findAll();

		// Seta ano Base
		// this.anoBase = "2020";

		// gera consulta e PDFs
		for (PagadorRecebedor investidor : this.listPagadores) {
			// Seta Investidor para gerar PDF
			this.selectedPagador = investidor;
			// Processa consulta Informe
			processInformeRendimentos();

			// Gera PDF Informe Rendimentos
			if (this.investidorInformeRendimentos.size() > 0) {
				geraPDFInformeRendimentos();
			}
		}

		// Faz processo de geração do ZIP e donwload
		List<FileUploaded> arquivosDownload = (List<FileUploaded>) this.listaArquivos();

		try {
			// recupera path do contrato
			ParametrosDao pDao = new ParametrosDao();
			String pathContrato = pDao.findByFilter("nome", "ARQUIVOS_PDF_DOWNLOAD").get(0).getValorString();

			// cria objetos para ZIP
			ZipOutputStream zip = null;
			FileOutputStream fileWriter = null;

			// cria arquivo ZIP
			fileWriter = new FileOutputStream(pathContrato + "Informe_Rendimentos_Geral.zip");
			zip = new ZipOutputStream(fileWriter);

			// Percorre arquivos selecionados e adiciona ao ZIP
			for (FileUploaded f : arquivosDownload) {
				addFileToZip("", f.getFile().getAbsolutePath(), zip);
			}

			// Fecha o ZIP
			zip.flush();
			zip.close();

			// Recupera ZIP gerado para fazer download
			FileInputStream stream = new FileInputStream(pathContrato + "Informe_Rendimentos_Geral.zip");
			downloadAllFiles = new DefaultStreamedContent(stream, pathContrato, "Informe_Rendimentos_Geral.zip");

		} catch (Exception e) {
			System.out.println(e);
		}

		this.irRetidoInvestidoresPDFGerado = false;

		return this.downloadAllFiles;
	}

	/***
	 * Lista os arquivos contidos no diretório
	 * 
	 * @return
	 */
	public Collection<FileUploaded> listaArquivos() {
		// DateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");
		ParametrosDao pDao = new ParametrosDao();
		String pathContrato = pDao.findByFilter("nome", "ARQUIVOS_PDF").get(0).getValorString() + "/";
		File diretorio = new File(pathContrato);
		File arqs[] = diretorio.listFiles();
		Collection<FileUploaded> lista = new ArrayList<FileUploaded>();
		if (arqs != null) {
			for (int i = 0; i < arqs.length; i++) {
				File arquivo = arqs[i];

				// String nome = arquivo.getName();
				// String dt_ateracao = formatData.format(new Date(arquivo.lastModified()));
				lista.add(new FileUploaded(arquivo.getName(), arquivo, pathContrato));
			}
		}
		return lista;
	}

	public final void populateSelectedPagador() {
		this.idPagador = this.selectedPagador.getId();
		this.nomePagador = this.selectedPagador.getNome();
	}

	public void clearPagador() {
		this.idPagador = 0;
		this.nomePagador = null;
		this.selectedPagador = new PagadorRecebedor();
	}

	public final void populateSelectedContrato() {
		this.idContrato = this.selectedContratoLov.getId();
		this.numeroContrato = this.selectedContratoLov.getNumeroContrato();
	}

	public void clearContrato() {
		this.idContrato = 0;
		this.numeroContrato = null;
		this.selectedContratoLov = new ContratoCobranca();
	}

	public int getQtdeContratos() {
		return qtdeContratosTotal;
	}

	public void setQtdeContratos(int qtdeContratos) {
		this.qtdeContratosTotal = qtdeContratos;
	}

//	public int getParcelasAbertas() {
//		return parcelasAbertasTotal;
//	}
//
//	public void setParcelasAbertas(int parcelasAbertas) {
//		this.parcelasAbertasTotal = parcelasAbertas;
//	}
//
//	public BigDecimal getValorReceber() {
//		return valorReceberTotal;
//	}
//
//	public void setValorReceber(BigDecimal valorReceber) {
//		this.valorReceberTotal = valorReceber;
//	}
//
//	public BigDecimal getValorRecebido() {
//		return valorRecebidoTotal;
//	}
//
//	public void setValorRecebido(BigDecimal valorRecebido) {
//		this.valorRecebidoTotal = valorRecebido;
//	}

	public List<ContratoCobranca> getContratos() {
		return contratos;
	}

	public void setContratos(List<ContratoCobranca> contratos) {
		this.contratos = contratos;
	}

	public ContratoCobranca getSelectedContrato() {
		return selectedContrato;
	}

	public void setSelectedContrato(ContratoCobranca selectedContrato) {
		this.selectedContrato = selectedContrato;
	}

	public List<ContratoCobrancaParcelasInvestidor> getListContratoCobrancaParcelasInvestidorSelecionado() {
		if (CommonsUtil.semValor(this.listContratoCobrancaParcelasInvestidorSelecionado)) {
			BigDecimal capitalizacaoAcumulada = BigDecimal.ZERO;

			for (ContratoCobrancaParcelasInvestidor parcela : this.selectedContrato
					.getListContratoCobrancaParcelasInvestidorSelecionado()) {
				if (parcela.getParcelaMensal().compareTo(BigDecimal.ZERO) == 0) {
					capitalizacaoAcumulada = capitalizacaoAcumulada
							.add(CommonsUtil.bigDecimalValue(parcela.getCapitalizacao()));
					parcela.setSaldoCredor(parcela.getSaldoCredor().add(capitalizacaoAcumulada));
				}
				this.listContratoCobrancaParcelasInvestidorSelecionado.add(parcela);

			}
			;
		}

		return this.listContratoCobrancaParcelasInvestidorSelecionado;

	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

//	public BigDecimal getValorInvestidor() {
//		return valorInvestidor;
//	}
//
//	public void setValorInvestidor(BigDecimal valorInvestidor) {
//		this.valorInvestidor = valorInvestidor;
//	}

	public ContratoCobrancaDetalhes getSelectedContratoCobrancaDetalhes() {
		return selectedContratoCobrancaDetalhes;
	}

	public void setSelectedContratoCobrancaDetalhes(ContratoCobrancaDetalhes selectedContratoCobrancaDetalhes) {
		this.selectedContratoCobrancaDetalhes = selectedContratoCobrancaDetalhes;
	}

	public void setUsuarioLogado(User usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}

//	public BigDecimal getValorInvestido() {
//		return new BigDecimal( 9999999);
//	}
//
//	public void setValorInvestido(BigDecimal valorInvestido) {
//		this.valorInvestidoTotal = valorInvestido;
//	}

	public Collection<FileUploaded> getFiles() {
		return files;
	}

	public void setFiles(Collection<FileUploaded> files) {
		this.files = files;
	}

	public FileUploaded getSelectedFile() {
		return selectedFile;
	}

	public void setSelectedFile(FileUploaded selectedFile) {
		this.selectedFile = selectedFile;
	}

	public List<FileUploaded> getDeletefiles() {
		return deletefiles;
	}

	public void setDeletefiles(List<FileUploaded> deletefiles) {
		this.deletefiles = deletefiles;
	}

	public void setDownloadAllFiles(StreamedContent downloadAllFiles) {
		this.downloadAllFiles = downloadAllFiles;
	}

	public void setDownloadFile(StreamedContent downloadFile) {
		this.downloadFile = downloadFile;
	}

	public class FileUploaded {
		private File file;
		private String name;
		private String path;

		public FileUploaded() {
		}

		public FileUploaded(String name, File file, String path) {
			this.name = name;
			this.file = file;
			this.path = path;
		}

		/**
		 * @return the file
		 */
		public File getFile() {
			return file;
		}

		/**
		 * @param file the file to set
		 */
		public void setFile(File file) {
			this.file = file;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the path
		 */
		public String getPath() {
			return path;
		}

		/**
		 * @param path the path to set
		 */
		public void setPath(String path) {
			this.path = path;
		}
	}

	public boolean isDebenturePDFGerado() {
		return debenturePDFGerado;
	}

	public void setDebenturePDFGerado(boolean debenturePDFGerado) {
		this.debenturePDFGerado = debenturePDFGerado;
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

	public StreamedContent getFile() {
		String caminho = this.pathPDF + this.nomePDF;
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

	public String getTermoSecuritizacaoPDFStr() {
		return termoSecuritizacaoPDFStr;
	}

	public void setTermoSecuritizacaoPDFStr(String termoSecuritizacaoPDFStr) {
		this.termoSecuritizacaoPDFStr = termoSecuritizacaoPDFStr;
	}

	public String getAnexoPDFStr() {
		return anexoPDFStr;
	}

	public void setAnexoPDFStr(String anexoPDFStr) {
		this.anexoPDFStr = anexoPDFStr;
	}

	public StreamedContent getTermoSecuritizacaoPDF() {
		String caminho = this.pathPDF + this.termoSecuritizacaoPDFStr;
		String arquivo = this.termoSecuritizacaoPDFStr;
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(caminho);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		termoSecuritizacaoPDF = new DefaultStreamedContent(stream, caminho, arquivo);

		return termoSecuritizacaoPDF;
	}

	public void setTermoSecuritizacaoPDF(StreamedContent termoSecuritizacaoPDF) {
		this.termoSecuritizacaoPDF = termoSecuritizacaoPDF;
	}

	public StreamedContent getAnexoPDF() {
		String caminho = this.pathPDF + this.anexoPDFStr;
		String arquivo = this.anexoPDFStr;
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(caminho);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		anexoPDF = new DefaultStreamedContent(stream, caminho, arquivo);

		return anexoPDF;
	}

	public void setAnexoPDF(StreamedContent anexoPDF) {
		this.anexoPDF = anexoPDF;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public long getIdInvestidor() {
		return idInvestidor;
	}

	public void setIdInvestidor(long idInvestidor) {
		this.idInvestidor = idInvestidor;
	}

	public PagadorRecebedor getSelectedPagador() {
		return selectedPagador;
	}

	public void setSelectedPagador(PagadorRecebedor selectedPagador) {
		this.selectedPagador = selectedPagador;
	}

	public List<PagadorRecebedor> getListPagadores() {
		return listPagadores;
	}

	public void setListPagadores(List<PagadorRecebedor> listPagadores) {
		this.listPagadores = listPagadores;
	}

	public String getNomePagador() {
		return nomePagador;
	}

	public void setNomePagador(String nomePagador) {
		this.nomePagador = nomePagador;
	}

	public long getIdPagador() {
		return idPagador;
	}

	public void setIdPagador(long idPagador) {
		this.idPagador = idPagador;
	}

	public int getPosicaoInvestidorNoContrato() {
		return posicaoInvestidorNoContrato;
	}

	public void setPosicaoInvestidorNoContrato(int posicaoInvestidorNoContrato) {
		this.posicaoInvestidorNoContrato = posicaoInvestidorNoContrato;
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

	public List<ContratoCobrancaParcelasInvestidor> getParcelasInvestidor() {
		return parcelasInvestidor;
	}

	public void setParcelasInvestidor(List<ContratoCobrancaParcelasInvestidor> parcelasInvestidor) {
		this.parcelasInvestidor = parcelasInvestidor;
	}

	public boolean isValoresLiquidosInvestidoresPDFGerado() {
		return valoresLiquidosInvestidoresPDFGerado;
	}

	public void setValoresLiquidosInvestidoresPDFGerado(boolean valoresLiquidosInvestidoresPDFGerado) {
		this.valoresLiquidosInvestidoresPDFGerado = valoresLiquidosInvestidoresPDFGerado;
	}

	public boolean isIrRetidoInvestidoresPDFGerado() {
		return irRetidoInvestidoresPDFGerado;
	}

	public void setIrRetidoInvestidoresPDFGerado(boolean irRetidoInvestidoresPDFGerado) {
		this.irRetidoInvestidoresPDFGerado = irRetidoInvestidoresPDFGerado;
	}

	public List<ContratoCobrancaParcelasInvestidor> getParcelasInvestidorEnvelope() {
		return parcelasInvestidorEnvelope;
	}

	public void setParcelasInvestidorEnvelope(List<ContratoCobrancaParcelasInvestidor> parcelasInvestidorEnvelope) {
		this.parcelasInvestidorEnvelope = parcelasInvestidorEnvelope;
	}

	public List<ContratoCobrancaParcelasInvestidor> getParcelasInvestidorCorrespondente() {
		return parcelasInvestidorCorrespondente;
	}

	public void setParcelasInvestidorCorrespondente(
			List<ContratoCobrancaParcelasInvestidor> parcelasInvestidorCorrespondente) {
		this.parcelasInvestidorCorrespondente = parcelasInvestidorCorrespondente;
	}

	public List<ContratoCobrancaParcelasInvestidor> getParcelasInvestidorSA() {
		return parcelasInvestidorSA;
	}

	public void setParcelasInvestidorSA(List<ContratoCobrancaParcelasInvestidor> parcelasInvestidorSA) {
		this.parcelasInvestidorSA = parcelasInvestidorSA;
	}

	public ContratoCobranca getSelectedContratoLov() {
		return selectedContratoLov;
	}

	public void setSelectedContratoLov(ContratoCobranca selectedContratoLov) {
		this.selectedContratoLov = selectedContratoLov;
	}

	public List<ContratoCobranca> getListContratos() {
		return listContratos;
	}

	public void setListContratos(List<ContratoCobranca> listContratos) {
		this.listContratos = listContratos;
	}

	public String getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public long getIdContrato() {
		return idContrato;
	}

	public void setIdContrato(long idContrato) {
		this.idContrato = idContrato;
	}

	public BigDecimal getTotalAnexoI() {
		return totalAnexoI;
	}

	public void setTotalAnexoI(BigDecimal totalAnexoI) {
		this.totalAnexoI = totalAnexoI;
	}

	public BigDecimal getValorPorcentagemAnexoI() {
		return valorPorcentagemAnexoI;
	}

	public void setValorPorcentagemAnexoI(BigDecimal valorPorcentagemAnexoI) {
		this.valorPorcentagemAnexoI = valorPorcentagemAnexoI;
	}

	public boolean isFiltraContrato() {
		return filtraContrato;
	}

	public void setFiltraContrato(boolean filtraContrato) {
		this.filtraContrato = filtraContrato;
	}

	public String getFiltroNumeroContrato() {
		return filtroNumeroContrato;
	}

	public void setFiltroNumeroContrato(String filtroNumeroContrato) {
		this.filtroNumeroContrato = filtroNumeroContrato;
	}

	public List<DebenturesInvestidor> getListDebenturesInvestidor() {
		return listDebenturesInvestidor;
	}

	public void setListDebenturesInvestidor(List<DebenturesInvestidor> listDebenturesInvestidor) {
		this.listDebenturesInvestidor = listDebenturesInvestidor;
	}

	public DebenturesInvestidor getDebenturesInvestidor() {
		return debenturesInvestidor;
	}

	public void setDebenturesInvestidor(DebenturesInvestidor debenturesInvestidor) {
		this.debenturesInvestidor = debenturesInvestidor;
	}

	public boolean isUpdateMode() {
		return updateMode;
	}

	public void setUpdateMode(boolean updateMode) {
		this.updateMode = updateMode;
	}

	public boolean isDeleteMode() {
		return deleteMode;
	}

	public void setDeleteMode(boolean deleteMode) {
		this.deleteMode = deleteMode;
	}

	public boolean isTermoSecuritizacaoPDFGerado() {
		return termoSecuritizacaoPDFGerado;
	}

	public void setTermoSecuritizacaoPDFGerado(boolean termoSecuritizacaoPDFGerado) {
		this.termoSecuritizacaoPDFGerado = termoSecuritizacaoPDFGerado;
	}

	public boolean isAnexo1PDFGerado() {
		return anexo1PDFGerado;
	}

	public void setAnexo1PDFGerado(boolean anexo1pdfGerado) {
		anexo1PDFGerado = anexo1pdfGerado;
	}

	public boolean isBsPDFGerado() {
		return bsPDFGerado;
	}

	public void setBsPDFGerado(boolean bsPDFGerado) {
		this.bsPDFGerado = bsPDFGerado;
	}

	public String getBsPDFStr() {
		return bsPDFStr;
	}

	public void setBsPDFStr(String bsPDFStr) {
		this.bsPDFStr = bsPDFStr;
	}

	public StreamedContent getBsPDF() {
		String caminho = this.pathPDF + this.bsPDFStr;
		String arquivo = this.bsPDFStr;
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(caminho);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bsPDF = new DefaultStreamedContent(stream, caminho, arquivo);

		return bsPDF;
	}

	public void setBsPDF(StreamedContent bsPDF) {
		this.bsPDF = bsPDF;
	}

	public boolean isTitulosQuitadosPDFGerado() {
		return titulosQuitadosPDFGerado;
	}

	public void setTitulosQuitadosPDFGerado(boolean titulosQuitadosPDFGerado) {
		this.titulosQuitadosPDFGerado = titulosQuitadosPDFGerado;
	}

	public String getTitulosQuitadosPDFStr() {
		return titulosQuitadosPDFStr;
	}

	public void setTitulosQuitadosPDFStr(String titulosQuitadosPDFStr) {
		this.titulosQuitadosPDFStr = titulosQuitadosPDFStr;
	}

	public StreamedContent getTitulosQuitadosPDF() {
		String caminho = this.pathPDF + this.titulosQuitadosPDFStr;
		String arquivo = this.titulosQuitadosPDFStr;
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(caminho);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		titulosQuitadosPDF = new DefaultStreamedContent(stream, caminho, arquivo);

		return titulosQuitadosPDF;
	}

	public void setTitulosQuitadosPDF(StreamedContent titulosQuitadosPDF) {
		this.titulosQuitadosPDF = titulosQuitadosPDF;
	}

	public BigDecimal getTotalValorFace() {
		return totalValorFace;
	}

	public void setTotalValorFace(BigDecimal totalValorFace) {
		this.totalValorFace = totalValorFace;
	}

	public boolean isOperacoesIndividualizadoPDFGerado() {
		return operacoesIndividualizadoPDFGerado;
	}

	public void setOperacoesIndividualizadoPDFGerado(boolean operacoesIndividualizadoPDFGerado) {
		this.operacoesIndividualizadoPDFGerado = operacoesIndividualizadoPDFGerado;
	}

	public String getOperacoesIndividualizadoPDFStr() {
		return operacoesIndividualizadoPDFStr;
	}

	public void setOperacoesIndividualizadoPDFStr(String operacoesIndividualizadoPDFStr) {
		this.operacoesIndividualizadoPDFStr = operacoesIndividualizadoPDFStr;
	}

	public StreamedContent getOperacoesIndividualizadoPDF() {
		String caminho = this.pathPDF + this.operacoesIndividualizadoPDFStr;
		String arquivo = this.operacoesIndividualizadoPDFStr;
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(caminho);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		operacoesIndividualizadoPDF = new DefaultStreamedContent(stream, caminho, arquivo);

		return operacoesIndividualizadoPDF;
	}

	public void setOperacoesIndividualizadoPDF(StreamedContent operacoesIndividualizadoPDF) {
		this.operacoesIndividualizadoPDF = operacoesIndividualizadoPDF;
	}

	public List<OperacoesIndividualizado> getListOperacoesIndividualizado() {
		return listOperacoesIndividualizado;
	}

	public void setListOperacoesIndividualizado(List<OperacoesIndividualizado> listOperacoesIndividualizado) {
		this.listOperacoesIndividualizado = listOperacoesIndividualizado;
	}

	public BigDecimal getTotalBruto() {
		return totalBruto;
	}

	public void setTotalBruto(BigDecimal totalBruto) {
		this.totalBruto = totalBruto;
	}

	public BigDecimal getTotalLiquido() {
		return totalLiquido;
	}

	public void setTotalLiquido(BigDecimal totalLiquido) {
		this.totalLiquido = totalLiquido;
	}

	public BigDecimal getTotalDesagio() {
		return totalDesagio;
	}

	public void setTotalDesagio(BigDecimal totalDesagio) {
		this.totalDesagio = totalDesagio;
	}

	public long getPrazoMedioTotal() {
		return prazoMedioTotal;
	}

	public void setPrazoMedioTotal(long prazoMedioTotal) {
		this.prazoMedioTotal = prazoMedioTotal;
	}

	public BigDecimal getTotalIRRetido() {
		return totalIRRetido;
	}

	public void setTotalIRRetido(BigDecimal totalIRRetido) {
		this.totalIRRetido = totalIRRetido;
	}

	public BigDecimal getTotalJurosLiquido() {
		return totalJurosLiquido;
	}

	public void setTotalJurosLiquido(BigDecimal totalJurosLiquido) {
		this.totalJurosLiquido = totalJurosLiquido;
	}

	public String getAnoBase() {
		return anoBase;
	}

	public void setAnoBase(String anoBase) {
		this.anoBase = anoBase;
	}

	public BigDecimal getValorInvestidorAnoAtual() {
		return valorInvestidorAnoAtual;
	}

	public void setValorInvestidorAnoAtual(BigDecimal valorInvestidorAnoAtual) {
		this.valorInvestidorAnoAtual = valorInvestidorAnoAtual;
	}

	public BigDecimal getValorInvestidorAnoAnterior() {
		return valorInvestidorAnoAnterior;
	}

	public void setValorInvestidorAnoAnterior(BigDecimal valorInvestidorAnoAnterior) {
		this.valorInvestidorAnoAnterior = valorInvestidorAnoAnterior;
	}

	public List<InvestidorInformeRendimentos> getInvestidorInformeRendimentos() {
		return investidorInformeRendimentos;
	}

	public void setInvestidorInformeRendimentos(List<InvestidorInformeRendimentos> investidorInformeRendimentos) {
		this.investidorInformeRendimentos = investidorInformeRendimentos;
	}

	public String getLabelAnoBase() {
		return labelAnoBase;
	}

	public void setLabelAnoBase(String labelAnoBase) {
		this.labelAnoBase = labelAnoBase;
	}

	public String getLabelAnoAnterior() {
		return labelAnoAnterior;
	}

	public void setLabelAnoAnterior(String labelAnoAnterior) {
		this.labelAnoAnterior = labelAnoAnterior;
	}

	public boolean isImprimirHeaderFooter() {
		return imprimirHeaderFooter;
	}

	public void setImprimirHeaderFooter(boolean imprimirHeaderFooter) {
		this.imprimirHeaderFooter = imprimirHeaderFooter;
	}

	public List<DashboardInvestidorResumoVO> getInvestidorResumos() {
		return investidorResumos;
	}

	public void setInvestidorResumos(List<DashboardInvestidorResumoVO> investidorResumos) {
		this.investidorResumos = investidorResumos;
	}

	public List<ExtratoVO> getExtrato() {
		return extrato;
	}

	public void setExtrato(List<ExtratoVO> extrato) {
		this.extrato = extrato;
	}

	public List<DashboardInvestidorResumoVO> getInvestidorResumosTela() {
		return investidorResumosTela;
	}

	public void setInvestidorResumosTela(List<DashboardInvestidorResumoVO> investidorResumosTela) {
		this.investidorResumosTela = investidorResumosTela;
	}

	public List<ContratoCobrancaResumoVO> getContratosVOTela() {
		return contratosVOTela;
	}

	public void setContratosVOTela(List<ContratoCobrancaResumoVO> contratosVOTela) {
		this.contratosVOTela = contratosVOTela;
	}

	public String getSituacaoSelecionada() {
		return situacaoSelecionada;
	}

	public void setSituacaoSelecionada(String situacaoSelecionada) {
		this.situacaoSelecionada = situacaoSelecionada;
	}

	public boolean isExisteContratoAtivo() {
		return existeContratoAtivo;
	}

	public void setExisteContratoAtivo(boolean existeContratoAtivo) {
		this.existeContratoAtivo = existeContratoAtivo;
	}

	public boolean isExisteContratoQuitado() {
		return existeContratoQuitado;
	}

	public void setExisteContratoQuitado(boolean existeContratoQuitado) {
		this.existeContratoQuitado = existeContratoQuitado;
	}

	public boolean isDebenturesEmitidasXLSGerado() {
		return debenturesEmitidasXLSGerado;
	}

	public void setDebenturesEmitidasXLSGerado(boolean debenturesEmitidasXLSGerado) {
		this.debenturesEmitidasXLSGerado = debenturesEmitidasXLSGerado;
	}

	public String getPathContrato() {
		return pathContrato;
	}

	public void setPathContrato(String pathContrato) {
		this.pathContrato = pathContrato;
	}

	public String getNomeContrato() {
		return nomeContrato;
	}

	public void setNomeContrato(String nomeContrato) {
		this.nomeContrato = nomeContrato;
	}

	/**
	 * @return the fileRecibo
	 */
	public StreamedContent getFileXLS() {
		String caminho = this.pathContrato + this.nomeContrato;
		String arquivo = this.nomeContrato;
		FileInputStream stream = null;

		this.debenturesEmitidasXLSGerado = false;
		try {
			stream = new FileInputStream(caminho);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fileXLS = new DefaultStreamedContent(stream, caminho, arquivo);

		return fileXLS;
	}

	public void setFileXLS(StreamedContent fileXLS) {
		this.fileXLS = fileXLS;
	}

	public DualListModel<PagadorRecebedor> getDualListModelRecebedores() {
		return dualListModelRecebedores;
	}

	public void setDualListModelRecebedores(DualListModel<PagadorRecebedor> dualListModelRecebedores) {
		this.dualListModelRecebedores = dualListModelRecebedores;
	}

	public List<PagadorRecebedor> getListRecebedoresSeleciodados() {
		return listRecebedoresSeleciodados;
	}

	public void setListRecebedoresSeleciodados(List<PagadorRecebedor> listRecebedoresSeleciodados) {
		this.listRecebedoresSeleciodados = listRecebedoresSeleciodados;
	}

	public List<PagadorRecebedor> getListRecebedores() {
		return listRecebedores;
	}

	public void setListRecebedores(List<PagadorRecebedor> listRecebedores) {
		this.listRecebedores = listRecebedores;
	}

	public String getFiltroDebenturesDocumento() {
		return filtroDebenturesDocumento;
	}

	public void setFiltroDebenturesDocumento(String filtroDebenturesDocumento) {
		this.filtroDebenturesDocumento = filtroDebenturesDocumento;
	}

	public String getFiltroDebenturesStatus() {
		return filtroDebenturesStatus;
	}

	public void setFiltroDebenturesStatus(String filtroDebenturesStatus) {
		this.filtroDebenturesStatus = filtroDebenturesStatus;
	}

	public String getFiltroDebenturesTipoDocumento() {
		return filtroDebenturesTipoDocumento;
	}

	public void setFiltroDebenturesTipoDocumento(String filtroDebenturesTipoDocumento) {
		this.filtroDebenturesTipoDocumento = filtroDebenturesTipoDocumento;
	}

	public String getFiltroDebenturesPorValor() {
		return filtroDebenturesPorValor;
	}

	public void setFiltroDebenturesPorValor(String filtroDebenturesPorValor) {
		this.filtroDebenturesPorValor = filtroDebenturesPorValor;
	}

	public BigDecimal getFiltroValorFaceInicial() {
		return filtroValorFaceInicial;
	}

	public void setFiltroValorFaceInicial(BigDecimal filtroValorFaceInicial) {
		this.filtroValorFaceInicial = filtroValorFaceInicial;
	}

	public BigDecimal getFiltroValorFaceFinal() {
		return filtroValorFaceFinal;
	}

	public void setFiltroValorFaceFinal(BigDecimal filtroValorFaceFinal) {
		this.filtroValorFaceFinal = filtroValorFaceFinal;
	}

	public String getFiltroDebenturesTipoFiltro() {
		return filtroDebenturesTipoFiltro;
	}

	public void setFiltroDebenturesTipoFiltro(String filtroDebenturesTipoFiltro) {
		this.filtroDebenturesTipoFiltro = filtroDebenturesTipoFiltro;
	}

	public String getFiltroDebenturesTipoData() {
		return filtroDebenturesTipoData;
	}

	public void setFiltroDebenturesTipoData(String filtroDebenturesTipoData) {
		this.filtroDebenturesTipoData = filtroDebenturesTipoData;
	}
}
