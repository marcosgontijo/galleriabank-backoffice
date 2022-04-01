package com.webnowbr.siscoat.cobranca.mb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
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
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.json.JSONObject;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.primefaces.model.charts.optionconfig.tooltip.Tooltip;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.webnowbr.siscoat.auxiliar.BigDecimalConverter;
import com.webnowbr.siscoat.auxiliar.EnviaEmail;
import com.webnowbr.siscoat.cobranca.auxiliar.RelatorioFinanceiroCobranca;
import com.webnowbr.siscoat.cobranca.db.model.AnaliseComite;
import com.webnowbr.siscoat.cobranca.db.model.ContasPagar;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhesObservacoes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhesParcial;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaFavorecidos;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaObservacoes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaParcelasInvestidor;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaStatus;
import com.webnowbr.siscoat.cobranca.db.model.FilaInvestidores;
import com.webnowbr.siscoat.cobranca.db.model.GruposFavorecidos;
import com.webnowbr.siscoat.cobranca.db.model.GruposPagadores;
import com.webnowbr.siscoat.cobranca.db.model.IPCA;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedorAdicionais;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedorSocio;
import com.webnowbr.siscoat.cobranca.db.model.PesquisaObservacoes;
import com.webnowbr.siscoat.cobranca.db.model.Responsavel;
import com.webnowbr.siscoat.cobranca.db.model.Segurado;
import com.webnowbr.siscoat.cobranca.db.op.ContasPagarDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaParcelasInvestidorDao;
import com.webnowbr.siscoat.cobranca.db.op.DashboardDao;
import com.webnowbr.siscoat.cobranca.db.op.FilaInvestidoresDao;
import com.webnowbr.siscoat.cobranca.db.op.GruposFavorecidosDao;
import com.webnowbr.siscoat.cobranca.db.op.GruposPagadoresDao;
import com.webnowbr.siscoat.cobranca.db.op.IPCADao;
import com.webnowbr.siscoat.cobranca.db.op.ImovelCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.ResponsavelDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.GeracaoBoletoMB;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.common.ValidaCNPJ;
import com.webnowbr.siscoat.common.ValidaCPF;
import com.webnowbr.siscoat.db.dao.DAOException;
import com.webnowbr.siscoat.db.dao.DBConnectionException;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;
import com.webnowbr.siscoat.seguro.vo.SeguroTabelaVO;
import com.webnowbr.siscoat.simulador.SimulacaoDetalheVO;
import com.webnowbr.siscoat.simulador.SimulacaoVO;
import com.webnowbr.siscoat.simulador.SimuladorMB;

/** ManagedBean. */
@ManagedBean(name = "contratoCobrancaMB")
@SessionScoped
public class ContratoCobrancaMB {
	/** Controle dos dados da Paginação. */
	private LazyDataModel<ContratoCobranca> lazyModel;
	private LazyDataModel<Responsavel> responsaveisLazy;
	/** Variavel. */
	private ContratoCobranca objetoContratoCobranca;
	private boolean updateMode = false;
	private boolean deleteMode = false;
	private boolean crmMode = false;
	private boolean baixarMode = false;
	private String tituloPainel = null;
	private String origemTelaBaixar;
	private String empresa;
	
	private String tipoParametroConsultaContrato;
	private String parametroConsultaContrato;
	
	private String tituloTelaConsultaPreStatus;

	private Date dataHoje;
	
	
	private String tipoContratoCobrancaFinanceiroDia;

	private boolean contratoGerado = false;
	
	private boolean controleWhatsAppAgAssintura = false;
	private boolean controleWhatsAppAssinado = false;
	private boolean controleWhatsAppPajuLaudoRecebido = false;
	private boolean controleWhatsAppPreAprovado = false;
	private boolean controleWhatsAppComite = false;
	
	/************************************************************
	 * Objetos para antecipacao de parcela
	 ************************************************************/
	private int idAntecipacaoInvestidor;

	/************************************************************
	 * Objetos para Reparcelamento
	 ************************************************************/
	private BigInteger numeroParcelaReparcelamento;
	private BigDecimal saldoDevedorReparcelamento;
	private BigInteger carenciaReparcelamento;
	private Date dataParcela;

	/************************************************************
	 * Objetos utilizados pelas LoVs
	 ***********************************************************/

	/** Objeto selecionado na LoV - Pagador. */
	private GruposFavorecidos selectedGrupoFavorecido;

	/** Lista dos Pagadores utilizada pela LOV. */
	private List<GruposFavorecidos> listGrupoFavorecido;

	/** Nome do Pagador selecionado pela LoV. */
	private String nomeGrupoFavorecido;

	/** Id Objeto selecionado na LoV - Pagador. */
	private long idGrupoFavorecido;

	/** Objeto selecionado na LoV - Pagador. */
	private PagadorRecebedor selectedPagador;
	
	/** Objeto selecionado no popup do pesquisa. */
	private PagadorRecebedor selectedPagadorGenerico;
	
	
	String tipoPesquisaPagadorRecebedor = "";
	Segurado seguradoSelecionado;
	PagadorRecebedorSocio socioSelecionado;
	PagadorRecebedorAdicionais pagadorSecundarioSelecionado;
	String updatePagadorRecebedor = "";
	String updateResponsavel = "";
	String tituloPagadorRecebedorDialog = "";
	AnaliseComite objetoAnaliseComite;
	
	ContasPagar contasPagarSelecionada;
	
	private boolean addSegurador;
	private boolean addSocio;
	private boolean addPagador;
	
	private boolean addContasPagar;

	/** Lista dos Pagadores utilizada pela LOV. */
	private List<PagadorRecebedor> listPagadores;

	/** Nome do Pagador selecionado pela LoV. */
	private String nomePagador;

	/** Id Objeto selecionado na LoV - Pagador. */
	private long idPagador;

	/** Objeto selecionado na LoV - Pagador. */
	private GruposPagadores selectedGrupoPagadores;

	/** Lista dos Pagadores utilizada pela LOV. */
	private List<GruposPagadores> listGrupoPagadores;

	/** Nome do Pagador selecionado pela LoV. */
	private String nomeGrupoPagador;

	/** Id Objeto selecionado na LoV - Pagador. */
	private long idGrupoPagador;

	/** Nome do Pagador selecionado pela LoV. */
	private boolean grupoPagadores;

	/** Objeto selecionado na LoV - Recebedor. */
	private PagadorRecebedor selectedRecebedor;

	/** Objeto selecionado na LoV - Recebedor. */
	private PagadorRecebedor selectedRecebedor2;

	/** Objeto selecionado na LoV - Recebedor. */
	private PagadorRecebedor selectedRecebedor3;

	/** Objeto selecionado na LoV - Recebedor. */
	private PagadorRecebedor selectedRecebedor4;

	/** Objeto selecionado na LoV - Recebedor. */
	private PagadorRecebedor selectedRecebedor5;

	/** Objeto selecionado na LoV - Recebedor. */
	private PagadorRecebedor selectedRecebedor6;

	/** Objeto selecionado na LoV - Recebedor. */
	private PagadorRecebedor selectedRecebedor7;

	/** Objeto selecionado na LoV - Recebedor. */
	private PagadorRecebedor selectedRecebedor8;

	/** Objeto selecionado na LoV - Recebedor. */
	private PagadorRecebedor selectedRecebedor9;

	/** Objeto selecionado na LoV - Recebedor. */
	private PagadorRecebedor selectedRecebedor10;
	
	/** Lista dos Recebedores utilizada pela LOV. */
	private List<PagadorRecebedor> listRecebedores;

	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedor;

	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedor;

	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedor2;

	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedor2;

	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedor3;

	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedor3;

	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedor4;

	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedor4;

	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedor5;

	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedor5;

	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedor6;

	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedor6;

	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedor7;

	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedor7;

	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedor8;

	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedor8;

	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedor9;

	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedor9;

	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedor10;

	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedor10;

	private PagadorRecebedor selectedRecebedorFinal1;
	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedorFinal1;
	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedorFinal1;

	private PagadorRecebedor selectedRecebedorFinal2;
	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedorFinal2;
	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedorFinal2;

	private PagadorRecebedor selectedRecebedorFinal3;
	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedorFinal3;
	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedorFinal3;

	private PagadorRecebedor selectedRecebedorFinal4;
	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedorFinal4;
	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedorFinal4;

	private PagadorRecebedor selectedRecebedorFinal5;
	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedorFinal5;
	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedorFinal5;

	private PagadorRecebedor selectedRecebedorFinal6;
	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedorFinal6;
	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedorFinal6;

	private PagadorRecebedor selectedRecebedorFinal7;
	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedorFinal7;
	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedorFinal7;

	private PagadorRecebedor selectedRecebedorFinal8;
	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedorFinal8;
	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedorFinal8;

	private PagadorRecebedor selectedRecebedorFinal9;
	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedorFinal9;
	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedorFinal9;

	private PagadorRecebedor selectedRecebedorFinal10;
	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeRecebedorFinal10;
	/** Id Objeto selecionado na LoV - Recebedor. */
	private long idRecebedorFinal10;

	/** Objeto selecionado na LoV - Responsavel. */
	private Responsavel selectedResponsavel;

	/** Lista dos Recebedores utilizada pela LOV. */
	private List<Responsavel> listResponsaveis;

	/** Nome do Recebedor selecionado pela LoV. */
	private String nomeResponsavel;

	/** Id Objeto selecionado na LoV - Responsavel. */
	private long idResponsavel;

	/** Objeto selecionado na LoV - Imovel. */
	private ImovelCobranca selectedImovel;

	/** Lista dos Recebedores utilizada pela LOV. */
	private List<ImovelCobranca> listImoveis;

	/** Nome do Imovel selecionado pela LoV. */
	private String nomeImovel;

	/** Id Objeto selecionado na LoV - Imovel. */
	private long idImovel;

	private String qtdeParcelas;

	private String vlrTotal;
	private String vlrParcelas;
	private String vlrParcelaAtualizada;
	private BigDecimal vlrRecebido;

	private BigDecimal vlrParcelaNew;

	private BigDecimal vlrParcelaAtualizadaNew;

	private BigDecimal vlrRepasse;

	private BigDecimal vlrRetencao;

	private BigDecimal vlrComissao;

	private BigDecimal vlrRepasseFinal;

	private BigDecimal vlrRetencaoFinal;

	private BigDecimal vlrParcelaFinal;

	private BigDecimal vlrComissaoFinal;
	private ContratoCobrancaDetalhesParcial contratoCobrancaDetalhesParcialTemp;

	/** Atributos relatórios */
	private Date relDataContratoInicioAtraso;
	private Date relDataContratoFimAtraso;
	
	private Date relDataContratoInicio;
	private Date relDataContratoFim;
	private List<RelatorioFinanceiroCobranca> relObjetoContratoCobranca;
	private RelatorioFinanceiroCobranca relSelectedObjetoContratoCobranca;
	private ContratoCobrancaDetalhes selectedContratoCobrancaDetalhes;

	private boolean relIsRelAtraso = false;
	private boolean relIsCompleto = true;
	private boolean relIsParcial = false;
	private String relParcial = "";
	private String filtrarDataVencimento = "Atualizada";

	private boolean tipoPessoaIsFisica = false;
	private boolean tipoPessoaIsFisicaCC = false;

	private String nomeFavorecido = null;
	private String cpfcnpjFavorecido = null;

	private StreamedContent file;
	private StreamedContent fileBoleto;

	private StreamedContent fileRecibo;

	private SimulacaoVO simuladorParcelas;

	private String pathContrato;
	private String nomeContrato;

	private List<ContratoCobrancaFavorecidos> listContratoCobrancaFavorecidos;

	private Date dataPagamentoFinalTmp;

	private List<ContratoCobrancaDetalhes> selectedListContratoCobrancaDetalhes;

	private List<ContratoCobrancaParcelasInvestidor> selectedListContratoCobrancaParcelasInvestidor;
	private List<ContratoCobrancaParcelasInvestidor> selectedListContratoCobrancaParcelasInvestidor2;
	private List<ContratoCobrancaParcelasInvestidor> selectedListContratoCobrancaParcelasInvestidor3;
	private List<ContratoCobrancaParcelasInvestidor> selectedListContratoCobrancaParcelasInvestidor4;
	private List<ContratoCobrancaParcelasInvestidor> selectedListContratoCobrancaParcelasInvestidor5;
	private List<ContratoCobrancaParcelasInvestidor> selectedListContratoCobrancaParcelasInvestidor6;
	private List<ContratoCobrancaParcelasInvestidor> selectedListContratoCobrancaParcelasInvestidor7;
	private List<ContratoCobrancaParcelasInvestidor> selectedListContratoCobrancaParcelasInvestidor8;
	private List<ContratoCobrancaParcelasInvestidor> selectedListContratoCobrancaParcelasInvestidor9;
	private List<ContratoCobrancaParcelasInvestidor> selectedListContratoCobrancaParcelasInvestidor10;

	private ContratoCobrancaDetalhes bpContratoCobrancaDetalhes;

	private ContratoCobrancaParcelasInvestidor baixaContratoCobrancaParcelasInvestidor;

	private boolean renderRecebedor2;
	private boolean renderRecebedor3;
	private boolean renderRecebedor4;
	private boolean renderRecebedor5;
	private boolean renderRecebedor6;
	private boolean renderRecebedor7;
	private boolean renderRecebedor8;
	private boolean renderRecebedor9;
	private boolean renderRecebedor10;

	private boolean renderRecebedorFinais;
	private boolean renderRecebedorFinal2;
	private boolean renderRecebedorFinal3;
	private boolean renderRecebedorFinal4;
	private boolean renderRecebedorFinal5;
	private boolean renderRecebedorFinal6;
	private boolean renderRecebedorFinal7;
	private boolean renderRecebedorFinal8;
	private boolean renderRecebedorFinal9;
	private boolean renderRecebedorFinal10;

	private Date dataPagamentoInvestidor;

	private String numContrato;

	private boolean tipoFiltros;

	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;

	@ManagedProperty(value = "#{crmmb}")
	protected CRMMB crmmb;

	private String exibeSomenteFavorecidosFiltrados;

	private String filtroRelBaixado;

	private Collection<ContratoCobranca> contratosPendentes;
	private Collection<ContratoCobranca> contratos;

	private Date rowEditNewDate;
	private boolean grupoFavorecidos = true;

	private String observacao;

	private Date dataPromessaPagamento;

	/***
	 * Campos utilizados na composicao do pre-cadastro
	 */
	private String codigoResponsavel;
	@ManagedProperty(value = "#{pagadorRecebedorMB}")
	protected PagadorRecebedorMB pagadorRecebedorMB;
	@ManagedProperty(value = "#{imovelCobrancaMB}")
	protected ImovelCobrancaMB imovelCobrancaMB;
	@ManagedProperty(value = "#{investidorMB}")
	protected InvestidorMB investidorMB;

	private ImovelCobranca objetoImovelCobranca;
	private PagadorRecebedor objetoPagadorRecebedor;
	private PagadorRecebedorSocio objetoSocio;

	private boolean geraBoletoInclusaoContrato;

	private Date dataVencimentoBoleto;
	private BigDecimal valorBoleto;
	private String valorBoletoStr;

	private Date dataObservacao;

	private String observacaoContrato;

	private ContratoCobrancaObservacoes contratoCobrancaObservacoes;
	private List<ContratoCobrancaObservacoes> listContratoCobrancaObservacoes;

	private boolean reciboGerado;

	private String pathRecibo;
	private String nomeRecibo;

	private boolean hasBaixaParcial;

	private boolean txZero;
	private boolean preContratoCustom;

	private String pesquisaObservacao;
	private List<PesquisaObservacoes> listPesquisaObservacoes;

	private Date dtReparcela;
	private BigDecimal vlrReparcela;
	private String qtdeReparcela;
	private String totalQtedParcelas;
	private BigDecimal totalVlrParcelas;

	private boolean splitBoletoIugu;

	private List<PagadorRecebedor> cedentesIugu;
	private long cedenteSelecionado;

	private ContratoCobrancaDetalhes contratoCobrancaDetalhes;

	private List<ContratoCobrancaDetalhesObservacoes> listContratoCobrancaDetalhesObservacoes = new ArrayList<ContratoCobrancaDetalhesObservacoes>();
	private String observacaoContratoDetalhes;
	private ContratoCobrancaDetalhesObservacoes contratoCobrancaDetalhesObservacoes;

	public IuguMB iuguMb = new IuguMB();

	private List<FilaInvestidores> listFilaInvestidores;
	private FilaInvestidores selectedInvestidor;
	private FilaInvestidores objetoFilaInvestidores;

	private ContratoCobranca contratoCobrancaCheckList;

	private BigDecimal vlrRepasseNew;
	private BigDecimal vlrRetencaoNew;
	private BigDecimal vlrComissaoNew;

	private BigDecimal vlrRepasseFinalNew;
	private BigDecimal vlrRetencaoFinalNew;
	private BigDecimal vlrComissaoFinalNew;

	private DualListModel<PagadorRecebedor> dualListModelRecebedores;
	private List<PagadorRecebedor> listRecebedoresSeleciodados;

	List<ContratoCobrancaParcelasInvestidor> selectedParcelasInvestidorCorrespondente;
	List<ContratoCobrancaParcelasInvestidor> selectedParcelasInvestidorSA;
	List<ContratoCobrancaParcelasInvestidor> selectedParcelasInvestidorEnvelope;

	List<ContratoCobranca> contratoCobrancaFinanceiroDia;
	
	private List<ContratoCobranca> selectedContratoCobrancaFinanceiroDia;

	ContratoCobrancaParcelasInvestidor antecipacao;
	ContratoCobrancaDetalhes amortizacao;
	
	private int indexStepsStatusContrato;
	
	private Date dataPrevistaVistoria;
	private BigDecimal valorBoletoPreContrato;
	private BigDecimal taxaPreAprovada;
	private BigInteger prazoMaxPreAprovado;
	private BigDecimal valorMercadoImovel;
	private BigDecimal valorVendaForçadaImóvel;
	private String comentarioJuridico;
	
	private Date dataInicio;
    private Date dataFim;
    private Collection<ContratoCobranca> listaContratos = new ArrayList<ContratoCobranca>();
 	private List<Responsavel> listResponsavel;

	
	
	private Boolean addPagadorPreContrato;
	
	public void alterarEmpresa() {
		FacesContext context = FacesContext.getCurrentInstance();

		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();		
		contratoCobrancaDao.merge(this.objetoContratoCobranca);
		
		context.addMessage(null,
			new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Contrato " + this.objetoContratoCobranca.getNumeroContrato() + " - Alteração da empresa salva com sucesso!!!",""));
	}

	/**
	 * 
	 * Construtor.
	 */
	public ContratoCobrancaMB() {

		objetoContratoCobranca = new ContratoCobranca();

		lazyModel = new LazyDataModel<ContratoCobranca>() {

			/** Serial. */
			private static final long serialVersionUID = 1L;

			@Override
			public List<ContratoCobranca> load(final int first, final int pageSize, final String sortField,
					final SortOrder sortOrder, final Map<String, Object> filters) {

				/*
				 * Busca usuário logado para restringir retorno dos dados.
				 */

				filters.put("status", "Aprovado");
				if (loginBean != null) {
					User usuarioLogado = new User();
					UserDao u = new UserDao();
					usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

					if (usuarioLogado != null) {
						if (!usuarioLogado.isAdministrador()) {
							filters.put("contratoRestritoAdm", "false");
						}
					}
				}

				ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

				setRowCount(contratoCobrancaDao.count(filters));
				return contratoCobrancaDao.findByFilter(first, pageSize, sortField, sortOrder.toString(), filters);
			}
		};

		// TODO FILTER
		responsaveisLazy = new LazyDataModel<Responsavel>() {

			/** Serial. */
			private static final long serialVersionUID = 1L;

			@Override
			public List<Responsavel> load(final int first, final int pageSize, final String sortField,
					final SortOrder sortOrder, final Map<String, Object> filters) {

				ResponsavelDao responsavelDao = new ResponsavelDao();

				setRowCount(responsavelDao.count(filters));
				return responsavelDao.findByFilter(first, pageSize, sortField, sortOrder.toString(), filters);
			}
		};

		// INICIO - Tratamento para Pré-Contrato
		this.objetoContratoCobranca = new ContratoCobranca();
		this.objetoContratoCobranca.setDataContrato(new Date());
		loadLovs();
		clearSelectedLovs();
		this.contratoGerado = false;
		this.qtdeParcelas = null;
		clearSelectedRecebedores();
		this.seguradoSelecionado = new Segurado();
		this.seguradoSelecionado.setPessoa(new PagadorRecebedor());
		this.pagadorSecundarioSelecionado = new PagadorRecebedorAdicionais();
		this.pagadorSecundarioSelecionado.setPessoa(new PagadorRecebedor());
		this.socioSelecionado = new PagadorRecebedorSocio();
		this.socioSelecionado.setPessoa(new PagadorRecebedor());
		this.tituloPagadorRecebedorDialog = "";
		this.addSegurador = false;
		this.addSocio = false;
		this.addPagador = false;
		
		this.contasPagarSelecionada = new ContasPagar();
		this.contasPagarSelecionada.setPagadorRecebedor(new PagadorRecebedor());
		this.contasPagarSelecionada.setResponsavel(new Responsavel());
		
		this.addContasPagar = false;
		this.vlrParcelaFinal = null;
		this.vlrRepasse = null;
		this.vlrRepasseFinal = null;
		this.vlrRetencao = null;
		this.vlrRetencaoFinal = null;
		this.vlrComissao = null;
		this.vlrComissaoFinal = null;
		this.objetoContratoCobranca.setGeraParcelaFinal(false);
		this.objetoContratoCobranca.setNumeroContrato(null);
		this.codigoResponsavel = null;
		files = new ArrayList<FileUploaded>();
		filesInterno = new ArrayList<FileUploaded>();

		this.hasBaixaParcial = false;

		this.objetoImovelCobranca = new ImovelCobranca();
		this.objetoPagadorRecebedor = new PagadorRecebedor();
		this.tipoPessoaIsFisica = true;
		// FIM - Tratamento para Pré-Contrato
	}

	public String consultarContratoCobranca() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.contratos = new ArrayList<ContratoCobranca>();

		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.contratos = contratoCobrancaDao.consultaContratosAprovados(null);
				} else {
					this.contratos = contratoCobrancaDao
							.consultaContratosAprovados(usuarioLogado.getCodigoResponsavel());
				}
			}
		}
		
		

		return "/Atendimento/Cobranca/ContratoCobrancaConsultar.xhtml";
	}

	public String consultarContratoCobrancaGalleria() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.contratos = new ArrayList<ContratoCobranca>();

		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.contratos = contratoCobrancaDao.consultaContratosAprovadosGalleria(null);
				} else {
					this.contratos = contratoCobrancaDao
							.consultaContratosAprovadosGalleria(usuarioLogado.getCodigoResponsavel());
				}
			}
		}

		return "/Atendimento/Cobranca/ContratoCobrancaDebenturistasConsultar.xhtml";
	}

	/**
	 * USADO NA TELA DE FILA DE INVESTIDORES
	 * 
	 * @return
	 */
	public String clearFieldFilaInvestidores() {
		this.listFilaInvestidores = new ArrayList<FilaInvestidores>();
		this.objetoFilaInvestidores = new FilaInvestidores();

		FilaInvestidoresDao fDao = new FilaInvestidoresDao();
		this.listFilaInvestidores = fDao.findAll();

		clearRecebedor();
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listRecebedores = pagadorRecebedorDao.findAll();

		this.selectedInvestidor = new FilaInvestidores();

		return "/Atendimento/Cobranca/FilaInvestidores.xhtml";
	}

	public String clearFieldADDFilaInvestidores() {
		this.objetoFilaInvestidores = new FilaInvestidores();
		this.objetoFilaInvestidores.setDataInsercao(gerarDataHoje());

		clearRecebedor();

		this.selectedInvestidor = new FilaInvestidores();

		return "/Atendimento/Cobranca/FilaInvestidoresInserir.xhtml";
	}

	public String clearFieldEDITFilaInvestidores() {

		this.idRecebedor = this.objetoFilaInvestidores.getInvestidor().getId();
		this.nomeRecebedor = this.objetoFilaInvestidores.getInvestidor().getNome();

		this.selectedRecebedor = this.objetoFilaInvestidores.getInvestidor();

		return "/Atendimento/Cobranca/FilaInvestidoresInserir.xhtml";
	}

	public String addInvestidorNaFila() {
		FacesContext context = FacesContext.getCurrentInstance();
		FilaInvestidoresDao fDao = new FilaInvestidoresDao();

		if (this.selectedRecebedor.getId() > 0) {
			this.objetoFilaInvestidores.setInvestidor(this.selectedRecebedor);

			if (this.objetoFilaInvestidores.getId() > 0) {
				fDao.merge(this.objetoFilaInvestidores);
			} else {
				fDao.create(this.objetoFilaInvestidores);
			}

			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Contrato Cobrança - Fila de Investidores: Investidor inserido com sucesso! (Investidor: "
									+ this.selectedRecebedor.getNome() + ")",
							""));

			return clearFieldFilaInvestidores();
		} else {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Contrato Cobrança - Fila de Investidores: Para inserir um investidor na fila é necessária a pré-seleção do mesmo!",
					""));

			return "";
		}
	}

	public String deleteInvestidorFila() {
		FacesContext context = FacesContext.getCurrentInstance();

		FilaInvestidoresDao fDao = new FilaInvestidoresDao();
		fDao.delete(this.objetoFilaInvestidores);

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Contrato Cobrança - Fila de Investidores: Investidor excluído com sucesso!", ""));

		return clearFieldFilaInvestidores();
	}

	/**
	 * FIM - USADO NA TELA DE FILA DE INVESTIDORES
	 * 
	 * @return
	 */

	public void geraBoletoDaParcela(ContratoCobranca contrato, String parcela, long idParcela, Date vencimento,
			BigDecimal valor, boolean splitBoletoIugu, long cedente) {
		this.iuguMb.geraBoletoDaParcela(contrato, parcela, idParcela, vencimento, valor, splitBoletoIugu, cedente);

		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.objetoContratoCobranca = contratoCobrancaDao.findById(this.objetoContratoCobranca.getId());
	}

	/**
	 * FIM - USADO NA TELA DE FILA DE INVESTIDORES
	 * 
	 * @return
	 */

	public void geraBoletoDaParcelaGalleria(ContratoCobranca contrato, String parcela, long idParcela, Date vencimento,
			BigDecimal valor, boolean splitBoletoIugu, long cedente) {
		this.iuguMb.geraBoletoDaParcela(contrato, parcela, idParcela, vencimento, valor, splitBoletoIugu, cedente);

		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.objetoContratoCobranca = contratoCobrancaDao.findById(this.objetoContratoCobranca.getId());
	}
	
	public void emitirCCB() {
		CcbMB ccbMb = new CcbMB();
		ccbMb.clearFieldsEmitirCcb();
		this.objetoContratoCobranca = getContratoById(this.objetoContratoCobranca.getId());
		this.objetoImovelCobranca = this.objetoContratoCobranca.getImovel();
		this.objetoPagadorRecebedor = this.objetoContratoCobranca.getPagador();
		ccbMb.setSelectedPagadorGenerico(this.objetoPagadorRecebedor);
		ccbMb.setTipoPesquisa("Emitente"); 
		ccbMb.EmitirCcbPreContrato();
	}
	
	public void geraBoletoLaudoPAJU(ContratoCobranca contrato, Date vencimento, String valorBoletoStr) {
		ContratoCobrancaDao cDao = new ContratoCobrancaDao(); 
		
		this.objetoContratoCobranca = cDao.findById(contrato.getId());
		
		// carrega pagador 
		if (this.objetoContratoCobranca.getPagador().getEndereco().equals("") || this.objetoContratoCobranca.getPagador().getCep().equals("")) {
			this.objetoContratoCobranca.getPagador().setEndereco(this.objetoContratoCobranca.getImovel().getEndereco());
			this.objetoContratoCobranca.getPagador().setCep(this.objetoContratoCobranca.getImovel().getCep());
		}
		this.iuguMb.setSelectedRecebedor(this.objetoContratoCobranca.getPagador());
		
		this.iuguMb.setDataVencimento(vencimento);

		// Armazena na Fatura para dar match no retorno do gatilho do splitter
		this.iuguMb.setIdContrato(String.valueOf(this.objetoContratoCobranca.getId()));
		this.iuguMb.setIdParcela("1");
		
		BigDecimal valorItem = new BigDecimal(valorBoletoStr.replace(".", "").replace(",", "."));
		this.iuguMb.setValorItem(valorItem);
		this.objetoContratoCobranca.setValorBoletoPreContrato(valorItem);
		
		this.iuguMb.setContratoCobranca(this.objetoContratoCobranca);		
				
		this.iuguMb.geraCobrancaSimplesContrato("bd88479c57011124c25638b26572e453", 34);
	}
	
	public void calcularValorLaudoPaju() {
		this.objetoContratoCobranca.setValorLaudoPajuFaltante(this.objetoContratoCobranca.getValorLaudoPajuTotal().subtract(this.objetoContratoCobranca.getValorLaudoPajuPago()));
	}

	/******************************************************************
	 * ******************************************************** CODIFICAÇÃO DO
	 * TRATAMENTO DE OBSERVAÇÕES - INICIO
	 * 
	 * ********************************************************
	 */

	/**
	 * @param observacaoContrato the observacaoContrato to set
	 */
	public void setObservacaoContrato(String observacaoContrato) {
		this.objetoContratoCobranca = objetoContratoCobranca;

		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		this.dataObservacao = dataHoje.getTime();

		this.observacao = null;

		this.listContratoCobrancaObservacoes = new ArrayList<ContratoCobrancaObservacoes>();

		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

		this.listContratoCobrancaObservacoes = contratoCobrancaDao
				.listaObservacoesOrdenadas(this.objetoContratoCobranca.getId());
	}

	public String clearFieldsAlteraParcelasInvestidor() {

		this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		this.selectedContratoCobrancaDetalhes = new ContratoCobrancaDetalhes();

		this.numContrato = null;

		this.dtReparcela = null;
		this.vlrReparcela = null;
		this.qtdeReparcela = null;
		this.totalQtedParcelas = null;
		this.totalVlrParcelas = null;

		return "/Atendimento/Cobranca/AlteraParcelasInvestidor.xhtml";
	}

	public String clearFieldsReParcelar() {

		this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		this.selectedContratoCobrancaDetalhes = new ContratoCobrancaDetalhes();

		this.numContrato = null;

		this.dtReparcela = null;
		this.vlrReparcela = null;
		this.qtdeReparcela = null;
		this.totalQtedParcelas = null;
		this.totalVlrParcelas = null;

		return "/Atendimento/Cobranca/ContratoCobrancaReParcelar.xhtml";
	}

	public void geraRelReParcela() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();

		/* Se filtro somente por numero do contrato */
		this.relObjetoContratoCobranca = contratoCobrancaDao.relatorioRegerarParcela(CommonsUtil.strZero(this.numContrato, 5));

		int totalQtedParcelas = 0;
		this.totalVlrParcelas = BigDecimal.ZERO;

		for (RelatorioFinanceiroCobranca rfc : this.relObjetoContratoCobranca) {
			this.objetoContratoCobranca = rfc.getContratoCobranca();
			// verifica se tem baixa parcial e se a parcela é diferente de paga
			for (ContratoCobrancaDetalhes ccd : rfc.getContratoCobranca().getListContratoCobrancaDetalhes()) {
				if (rfc.getIdParcela() == ccd.getId()) {
					if (CommonsUtil.mesmoValor(ccd.getNumeroParcela(), "0")
							|| ccd.isAmortizacao())
						continue;
					if (ccd.getListContratoCobrancaDetalhesParcial().size() == 0 && !ccd.isParcelaPaga()) {
						totalQtedParcelas = totalQtedParcelas + 1;
						this.totalVlrParcelas = totalVlrParcelas.add(ccd.getVlrParcela());
					}
				}
			}
		}

		this.totalQtedParcelas = String.valueOf(totalQtedParcelas);

		this.relSelectedObjetoContratoCobranca = new RelatorioFinanceiroCobranca();

		if (this.relObjetoContratoCobranca.size() == 0) {
			this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		}
	}
	

	public void onRowEdit(RowEditEvent event) {
		ContratoCobrancaDetalhesDao cDao = new ContratoCobrancaDetalhesDao();
		ContratoCobrancaDetalhes contratoCobrancaDetalhesTmp = new ContratoCobrancaDetalhes();
		contratoCobrancaDetalhesTmp = (ContratoCobrancaDetalhes) event.getObject();

		cDao.merge(contratoCobrancaDetalhesTmp);

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Contrato Cobrança: Coluna(s) salva(s) com sucesso!", ""));
	}

	public void onRowEditInvestidor(RowEditEvent event) {
		ContratoCobrancaParcelasInvestidorDao cDao = new ContratoCobrancaParcelasInvestidorDao();
		ContratoCobrancaParcelasInvestidor ContratoCobrancaParcelasInvestidorTmp = new ContratoCobrancaParcelasInvestidor();
		ContratoCobrancaParcelasInvestidorTmp = (ContratoCobrancaParcelasInvestidor) event.getObject();

		cDao.merge(ContratoCobrancaParcelasInvestidorTmp);

		// Seta que o investidor teve alterações nas parcelas
		// Na próxima edição de contrato não deve-se gerar parcelas de investidor
		// novamente
		if (this.objetoContratoCobranca.getRecebedor() != null) {
			if (this.objetoContratoCobranca.getRecebedor().getId() == ContratoCobrancaParcelasInvestidorTmp
					.getInvestidor().getId()) {
				this.objetoContratoCobranca.setParcelasAlteradas1(true);
			}
		}

		if (this.objetoContratoCobranca.getRecebedor2() != null) {
			if (this.objetoContratoCobranca.getRecebedor2().getId() == ContratoCobrancaParcelasInvestidorTmp
					.getInvestidor().getId()) {
				this.objetoContratoCobranca.setParcelasAlteradas2(true);
			}
		}

		if (this.objetoContratoCobranca.getRecebedor3() != null) {
			if (this.objetoContratoCobranca.getRecebedor3().getId() == ContratoCobrancaParcelasInvestidorTmp
					.getInvestidor().getId()) {
				this.objetoContratoCobranca.setParcelasAlteradas3(true);
			}
		}

		if (this.objetoContratoCobranca.getRecebedor4() != null) {
			if (this.objetoContratoCobranca.getRecebedor4().getId() == ContratoCobrancaParcelasInvestidorTmp
					.getInvestidor().getId()) {
				this.objetoContratoCobranca.setParcelasAlteradas4(true);
			}
		}

		if (this.objetoContratoCobranca.getRecebedor5() != null) {
			if (this.objetoContratoCobranca.getRecebedor5().getId() == ContratoCobrancaParcelasInvestidorTmp
					.getInvestidor().getId()) {
				this.objetoContratoCobranca.setParcelasAlteradas5(true);
			}
		}

		if (this.objetoContratoCobranca.getRecebedor6() != null) {
			if (this.objetoContratoCobranca.getRecebedor6().getId() == ContratoCobrancaParcelasInvestidorTmp
					.getInvestidor().getId()) {
				this.objetoContratoCobranca.setParcelasAlteradas6(true);
			}
		}

		if (this.objetoContratoCobranca.getRecebedor7() != null) {
			if (this.objetoContratoCobranca.getRecebedor7().getId() == ContratoCobrancaParcelasInvestidorTmp
					.getInvestidor().getId()) {
				this.objetoContratoCobranca.setParcelasAlteradas7(true);
			}
		}

		if (this.objetoContratoCobranca.getRecebedor8() != null) {
			if (this.objetoContratoCobranca.getRecebedor8().getId() == ContratoCobrancaParcelasInvestidorTmp
					.getInvestidor().getId()) {
				this.objetoContratoCobranca.setParcelasAlteradas8(true);
			}
		}

		if (this.objetoContratoCobranca.getRecebedor9() != null) {
			if (this.objetoContratoCobranca.getRecebedor9().getId() == ContratoCobrancaParcelasInvestidorTmp
					.getInvestidor().getId()) {
				this.objetoContratoCobranca.setParcelasAlteradas9(true);
			}
		}

		if (this.objetoContratoCobranca.getRecebedor10() != null) {
			if (this.objetoContratoCobranca.getRecebedor10().getId() == ContratoCobrancaParcelasInvestidorTmp
					.getInvestidor().getId()) {
				this.objetoContratoCobranca.setParcelasAlteradas10(true);
			}
		}

		ContratoCobrancaDao contratoDao = new ContratoCobrancaDao();
		contratoDao.merge(this.objetoContratoCobranca);

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Parcelas do Investidor: Registro salvo com sucesso!", ""));
	}

	public void onRowCancelInvestidor(RowEditEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Parcelas do Investidor: Edição cancelada!", ""));
	}

	public void onRowCancelEditParcelaBaixa(RowEditEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Contrato Cobrança: Edição cancelada!", ""));
	}

	public void onRowEditParcelaBaixa(RowEditEvent event) {
		ContratoCobrancaDetalhesDao cDao = new ContratoCobrancaDetalhesDao();
		ContratoCobrancaDetalhes contratoCobrancaDetalhesTmp = new ContratoCobrancaDetalhes();
		contratoCobrancaDetalhesTmp = (ContratoCobrancaDetalhes) event.getObject();

		cDao.merge(contratoCobrancaDetalhesTmp);

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Contrato Cobrança: Coluna(s) salva(s) com sucesso!", ""));
	}

	public void onRowCancel(RowEditEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Contrato Cobrança: Edição cancelada!", ""));
	}

	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}

	public void excluirObservacao() {
		List<ContratoCobrancaObservacoes> listObservacoes = new ArrayList<ContratoCobrancaObservacoes>();
		for (int i = 0; i < this.objetoContratoCobranca.getListContratoCobrancaObservacoes().size(); i++) {
			if (this.objetoContratoCobranca.getListContratoCobrancaObservacoes().get(i)
					.getId() != this.contratoCobrancaObservacoes.getId()) {
				listObservacoes.add(this.objetoContratoCobranca.getListContratoCobrancaObservacoes().get(i));
			}
		}

		this.objetoContratoCobranca.setListContratoCobrancaObservacoes(listObservacoes);

		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		contratoCobrancaDao.merge(this.objetoContratoCobranca);
		contratoCobrancaDao.limpaObservacoesNaoUsadas();
	}

	public void excluirObservacaoDetalhes() {
		List<ContratoCobrancaDetalhesObservacoes> listObservacoes = new ArrayList<ContratoCobrancaDetalhesObservacoes>();
		for (int i = 0; i < this.contratoCobrancaDetalhes.getListContratoCobrancaDetalhesObservacoes().size(); i++) {
			if (this.contratoCobrancaDetalhes.getListContratoCobrancaDetalhesObservacoes().get(i)
					.getId() != this.contratoCobrancaDetalhesObservacoes.getId()) {
				listObservacoes.add(this.contratoCobrancaDetalhes.getListContratoCobrancaDetalhesObservacoes().get(i));
			}
		}

		this.contratoCobrancaDetalhes.setListContratoCobrancaDetalhesObservacoes(listObservacoes);

		ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
		contratoCobrancaDetalhesDao.merge(this.contratoCobrancaDetalhes);
		contratoCobrancaDetalhesDao.limpaObservacoesNaoUsadas();
	}

	public void addObservacao() {
		ContratoCobrancaObservacoes contratoCobrancaObservacoes = new ContratoCobrancaObservacoes();
		contratoCobrancaObservacoes.setData(this.dataObservacao);
		contratoCobrancaObservacoes.setObservacao(this.observacao);

		if (this.loginBean != null) {
			contratoCobrancaObservacoes.setUsuario(loginBean.getUsername());
		}

		this.objetoContratoCobranca.getListContratoCobrancaObservacoes().add(contratoCobrancaObservacoes);

		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		contratoCobrancaDao.merge(this.objetoContratoCobranca);
	}

	public void addObservacaoDetalhes() {
		ContratoCobrancaDetalhesObservacoes contratoCobrancaDetalhesObservacoes = new ContratoCobrancaDetalhesObservacoes();
		contratoCobrancaDetalhesObservacoes.setData(this.dataObservacao);
		contratoCobrancaDetalhesObservacoes.setObservacao(this.observacao);

		if (this.loginBean != null) {
			contratoCobrancaDetalhesObservacoes.setUsuario(loginBean.getUsername());
		}

		this.contratoCobrancaDetalhes.getListContratoCobrancaDetalhesObservacoes()
				.add(contratoCobrancaDetalhesObservacoes);

		ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
		contratoCobrancaDetalhesDao.merge(this.contratoCobrancaDetalhes);
	}

	public void cancelObservacaoDetalhes() {
		ContratoCobrancaDetalhesObservacoes contratoCobrancaDetalhesObservacoes = new ContratoCobrancaDetalhesObservacoes();
		contratoCobrancaDetalhesObservacoes.setData(this.dataObservacao);
		contratoCobrancaDetalhesObservacoes.setObservacao(this.observacao);
		this.objetoContratoCobranca = null;
		this.contratoCobrancaDetalhes = null;
	}

	public String clearObservacoes() {
		this.pesquisaObservacao = null;
		this.listPesquisaObservacoes = new ArrayList<PesquisaObservacoes>();

		return "/Atendimento/Cobranca/ContratoCobrancaPesquisaObservacoes.xhtml";
	}

	public void pesquisarObservacoes() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.listPesquisaObservacoes = contratoCobrancaDao.pesquisaObservacoes(this.pesquisaObservacao);
	}

	public void addPromessaDePagamentoDetalhes() {
		ContratoCobrancaDetalhesDao cDao = new ContratoCobrancaDetalhesDao();
		this.contratoCobrancaDetalhes.setPromessaPagamento(this.dataPromessaPagamento);

		cDao.merge(this.contratoCobrancaDetalhes);

		this.dataPromessaPagamento = null;

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Contrato Cobrança: Data Promessa de Pagamento salva com sucesso!", ""));
	}

	/**
	 * @return the listContratoCobrancaObservacoes
	 */
	public List<ContratoCobrancaObservacoes> getListContratoCobrancaObservacoes() {
		return listContratoCobrancaObservacoes;
	}

	/**
	 * @return the contratoCobrancaDetalhesParcialTemp
	 */
	public ContratoCobrancaDetalhesParcial getContratoCobrancaDetalhesParcialTemp() {
		return contratoCobrancaDetalhesParcialTemp;
	}

	/**
	 * @param contratoCobrancaDetalhesParcialTemp the
	 *                                            contratoCobrancaDetalhesParcialTemp
	 *                                            to set
	 */
	public void setContratoCobrancaDetalhesParcialTemp(
			ContratoCobrancaDetalhesParcial contratoCobrancaDetalhesParcialTemp) {
		this.contratoCobrancaDetalhesParcialTemp = contratoCobrancaDetalhesParcialTemp;
	}

	/**
	 * @param listContratoCobrancaObservacoes the listContratoCobrancaObservacoes to
	 *                                        set
	 */
	public void setListContratoCobrancaObservacoes(List<ContratoCobrancaObservacoes> listContratoCobrancaObservacoes) {
		this.listContratoCobrancaObservacoes = listContratoCobrancaObservacoes;
	}

	/**
	 * @return the dataObservacao
	 */
	public Date getDataObservacao() {
		return dataObservacao;
	}

	/**
	 * @param dataObservacao the dataObservacao to set
	 */
	public void setDataObservacao(Date dataObservacao) {
		this.dataObservacao = dataObservacao;
	}

	/**
	 * @return the observacaoContrato
	 */
	public String getObservacaoContrato() {
		return observacaoContrato;
	}

	/**
	 * @return the contratoCobrancaObservacoes
	 */
	public ContratoCobrancaObservacoes getContratoCobrancaObservacoes() {
		return contratoCobrancaObservacoes;
	}

	/**
	 * @param contratoCobrancaObservacoes the contratoCobrancaObservacoes to set
	 */
	public void setContratoCobrancaObservacoes(ContratoCobrancaObservacoes contratoCobrancaObservacoes) {
		this.contratoCobrancaObservacoes = contratoCobrancaObservacoes;
	}

	/******************************************************************
	 * ******************************************************** CODIFICAÇÃO DO
	 * TRATAMENTO DE OBSERVAÇÕES - FIM
	 * 
	 * ********************************************************
	 */

	public String clearPreContratoCustomizado() {
		// INICIO - Tratamento para Pré-Contrato
		this.objetoContratoCobranca = new ContratoCobranca();
		this.objetoContratoCobranca.setDataContrato(new Date());
		loadLovs();
		clearSelectedLovs();
		this.contratoGerado = false;
		this.qtdeParcelas = null;
		clearSelectedRecebedores();
		this.seguradoSelecionado = new Segurado();
		this.seguradoSelecionado.setPessoa(new PagadorRecebedor());		
		this.socioSelecionado = new PagadorRecebedorSocio();
		this.socioSelecionado.setPessoa(new PagadorRecebedor());	
		this.pagadorSecundarioSelecionado = new PagadorRecebedorAdicionais();
		this.pagadorSecundarioSelecionado.setPessoa(new PagadorRecebedor());
		this.addSegurador= false;
		this.addSocio = false;
		this.addPagador = false;
		
		this.contasPagarSelecionada = new ContasPagar();
		this.contasPagarSelecionada.setPagadorRecebedor(new PagadorRecebedor());
		this.contasPagarSelecionada.setResponsavel(new Responsavel());
		
		this.vlrParcelaFinal = null;
		this.vlrRepasse = null;
		this.vlrRepasseFinal = null;
		this.vlrRetencao = null;
		this.vlrRetencaoFinal = null;
		this.vlrComissao = null;
		this.vlrComissaoFinal = null;
		this.objetoContratoCobranca.setGeraParcelaFinal(false);
		this.objetoContratoCobranca.setNumeroContrato(geraNumeroContrato());
		this.codigoResponsavel = null;
		files = new ArrayList<FileUploaded>();
		filesInterno = new ArrayList<FileUploaded>();

		this.objetoImovelCobranca = new ImovelCobranca();
		this.objetoPagadorRecebedor = new PagadorRecebedor();
		this.objetoPagadorRecebedor.setEstado("SP");
		clearRecebedor();
		this.tipoPessoaIsFisica = true;
		this.tipoPessoaIsFisicaCC = false;
		this.tituloPainel = "Adicionar";
		this.objetoContratoCobranca.setStatus("Ag. Análise");
		this.objetoContratoCobranca.setAgAssinatura(true);
		this.objetoContratoCobranca.setAgRegistro(true);

		this.qtdeParcelas = null;
		// FIM - Tratamento para Pré-Contrato

		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				this.codigoResponsavel = usuarioLogado.getCodigoResponsavel();
			}
		}

		ParametrosDao pDao = new ParametrosDao();
		this.objetoContratoCobranca
				.setTxJuros(pDao.findByFilter("nome", "COBRANCA_REC_TX_JUROS").get(0).getValorBigDecimal());
		this.objetoContratoCobranca
				.setTxMulta(pDao.findByFilter("nome", "COBRANCA_REC_MULTA").get(0).getValorBigDecimal());

		this.objetoContratoCobranca.setStatusContrato("Em Análise");

		return "/Atendimento/Cobranca/ContratoCobrancaPreCustomizadoInserir.xhtml";
	}

	public String clearPreContrato() {
		// INICIO - Tratamento para Pré-Contrato
		this.objetoContratoCobranca = new ContratoCobranca();
		this.objetoContratoCobranca.setDataContrato(new Date());
		loadLovs();
		clearSelectedLovs();
		this.contratoGerado = false;
		this.qtdeParcelas = null;
		clearSelectedRecebedores();
		this.seguradoSelecionado = new Segurado();
		this.seguradoSelecionado.setPessoa(new PagadorRecebedor());
		this.socioSelecionado = new PagadorRecebedorSocio();
		this.socioSelecionado.setPessoa(new PagadorRecebedor());
		this.pagadorSecundarioSelecionado = new PagadorRecebedorAdicionais();
		this.pagadorSecundarioSelecionado.setPessoa(new PagadorRecebedor());
		this.addSegurador= false;
		this.addSocio = false;
		this.addPagador = false;
		
		this.contasPagarSelecionada = new ContasPagar();
		this.contasPagarSelecionada.setPagadorRecebedor(new PagadorRecebedor());
		this.contasPagarSelecionada.setResponsavel(new Responsavel());
		
		this.vlrParcelaFinal = null;
		this.vlrRepasse = null;
		this.vlrRepasseFinal = null;
		this.vlrRetencao = null;
		this.vlrRetencaoFinal = null;
		this.vlrComissao = null;
		this.vlrComissaoFinal = null;
		this.objetoContratoCobranca.setGeraParcelaFinal(false);
		this.objetoContratoCobranca.setNumeroContrato(geraNumeroContrato());
		this.codigoResponsavel = null;
		files = new ArrayList<FileUploaded>();
		filesInterno = new ArrayList<FileUploaded>();

		this.objetoImovelCobranca = new ImovelCobranca();
		this.objetoPagadorRecebedor = new PagadorRecebedor();
		this.objetoPagadorRecebedor.setEstado("SP");
		clearRecebedor();
		this.tipoPessoaIsFisica = true;
		this.tipoPessoaIsFisicaCC = false;
		this.tituloPainel = "Adicionar";
		this.objetoContratoCobranca.setStatus("Ag. Análise");
		this.objetoContratoCobranca.setAgAssinatura(true);
		this.objetoContratoCobranca.setAgRegistro(true);
		
		

		this.qtdeParcelas = null;
		// FIM - Tratamento para Pré-Contrato

		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				this.codigoResponsavel = usuarioLogado.getCodigoResponsavel();
			}
		}

		ParametrosDao pDao = new ParametrosDao();
		this.objetoContratoCobranca
				.setTxJuros(pDao.findByFilter("nome", "COBRANCA_REC_TX_JUROS").get(0).getValorBigDecimal());
		this.objetoContratoCobranca
				.setTxMulta(pDao.findByFilter("nome", "COBRANCA_REC_MULTA").get(0).getValorBigDecimal());

		this.objetoContratoCobranca.setStatusContrato("Em Análise");

		return "/Atendimento/Cobranca/ContratoCobrancaInserirPendente.xhtml";
	}
	
	

	/******
	 * método para envio de emails
	 */
	public void enviaEmailCriacaoPreContrato() {
		Locale locale = new Locale("pt", "BR");
		SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MM/yyyy HH:mm", locale);
		Date dataHoje = gerarDataHoje();

		String mensagemHtmlTeste = "<html>\n" + "<head>\n" + "<meta charset=\"UTF-8\">\n"
				+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n"
				+ "<link href='https://fonts.googleapis.com/css2?family=Open+Sans:wght@400;700&display=swap' rel='stylesheet'>\n"
				+ "</head>\n" + "<body>\n"
				+ "<div bgcolor='#f9f7f7' marginwidth='0' marginheight='0' style='background-color:#f9f7f7'>\n"
				+ "<div class='adM'> </div>\n"
				+ "<div style='background-color:#fff;margin-top:0px;margin-right:auto;margin-bottom:0px;margin-left:auto;width:650px!important;color:#fff;font-size:30px'>\n"
				+ "<div class='adM'> </div>\n" + "<div align='center'>\n" + "<div class='adM'> </div>\n"
				+ "<table width='100%' border='0' cellspacing='0' cellpadding='0'>\n" + "<tbody>\n" + "<tr>\n"
				+ "<td style='background-color:#f0f0f0;height:75px; padding: 15px;' align='center'> <img src='http://siscoatimagens.galleriabank.com.br/logo-galleria.png' height='65' width='300'> </td>\n"
				+ "</tr>\n" + "</tbody>\n" + "</table>\n" + "<br>\n" + "<table>\n" + "<tbody>\n" + "<tr>\n"
				+ "<td width='20'> </td>\n" + "<td>\n" + "<table>\n" + "<tbody>\n" + "<tr>\n"
				+ "<td style='font-family:Arial,sans-serif;color:#bb7e17;font-size:20px'>Olá\n"
				+ "<span style='font-weight:bold'>" + this.objetoContratoCobranca.getResponsavel().getNome()
				+ "</span>, </td>\n" + "</tr>\n" + "<tr>\n"
				+ "<td style='font-family:Arial,sans-serif;color:#58585a;font-size:14px;line-height:20px;padding-top:7px'> O pré-contrato <b>"
				+ this.objetoContratoCobranca.getNumeroContrato() + "</b> acaba de ser criado. </td>\n" + "</tr>\n"
				+ "</tbody>\n" + "</table>\n"
				+ "<div style='height:1px;background-color:#e8e8ed;margin-top:10px;margin-bottom:25px'> </div>\n"
				+ "<table width='100%' style='border-left:3px solid #bb7e17'>\n" + "<tbody>\n" + "<tr>\n"
				+ "<td style='font-family:Arial,sans-serif;color:#58585a;font-size:14px;padding-left:18px;line-height:16px'> <span style='font-size:10px;'>DATA DA CRIAÇÃO\n"
				+ "</span>\n" + "<br><b>" + sdfDataRelComHoras.format(dataHoje) + "\n" + "</b> </td>\n" + "</tr>\n"
				+ "<tr>\n"
				+ "<td style='font-family:Arial,sans-serif;color:#58585a;font-size:14px;padding-left:18px;line-height:16px'> <span style='font-size:10px;'>DADOS CONTRATO\n"
				+ "</span>\n";

		mensagemHtmlTeste = mensagemHtmlTeste + "<br>Responsável: <b>"
				+ this.objetoContratoCobranca.getResponsavel().getNome() + "</b>\n";

		mensagemHtmlTeste = mensagemHtmlTeste + "<br>Pagador: <b>" + this.objetoContratoCobranca.getPagador().getNome()
				+ "</b>\n";

		mensagemHtmlTeste = mensagemHtmlTeste + " </td> </tr> </tbody> " + " </table>" + " </td>"
				+ " <td width='20'> </td>" + " </tr>" + " </tbod>" + " </table>" + " <br>"
				+ " <table width='100%' border='0' cellspacing='0' cellpadding='0'>" + " <tbody>"
				+ " <tr style='background-color:#f0f0f0;height:61px;font-family:Arial,sans-serif;font-size:10px;color:#fff'>"
				+ " <td style='color:#16243f;padding-left:20px; font-size: 12px;'> © Todos direitos reservados. </td>"
				+ " <td style='text-align: right;padding-right: 20px;'><a style='color:#16243f;font-size: 12px; text-decoration: none;' href='http://sistema.galleriabank.com.br/' target='_blank'>Galleria Bank</a> </td>"
				+ " </tr>" + " </tbody>" + " </table>" + " <div class='yj6qo'></div>" + " <div class='adL'> </div>"
				+ " </div>" + " <div class='adL'> </div>" + " <div class='adL'> </div>" + " </div>" + " </body>"
				+ " </html>";

		try {
			ResponsavelDao rDao = new ResponsavelDao();
			EnviaEmail eec = new EnviaEmail();
			// eec.enviarEmailHtmlResponsavelAdms(rDao.getGuardaChuvaCompletoResponsavelString(this.objetoContratoCobranca.getResponsavel().getId()),
			eec.enviarEmailHtmlResponsavelAdms(this.objetoContratoCobranca.getResponsavel().getEmail(),
					"[siscoat] Criação do contrato " + this.objetoContratoCobranca.getNumeroContrato(),
					mensagemHtmlTeste);

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	/******
	 * método para envio de emails
	 */
	public void enviaEmailAtualizacaoPreContrato() {
		Locale locale = new Locale("pt", "BR");
		SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MM/yyyy HH:mm", locale);
		Date dataHoje = gerarDataHoje();

		String mensagemHtmlTeste = "<html>\n" + "<head>\n" + "<meta charset=\"UTF-8\">\n"
				+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n"
				+ "<link href='https://fonts.googleapis.com/css2?family=Open+Sans:wght@400;700&display=swap' rel='stylesheet'>\n"
				+ "</head>\n" + "<body>\n"
				+ "<div bgcolor='#f9f7f7' marginwidth='0' marginheight='0' style=' :#f9f7f7'>\n"
				+ "<div class='adM'> </div>\n"
				+ "<div style='background-color:#fff;margin-top:0px;margin-right:auto;margin-bottom:0px;margin-left:auto;width:650px!important;color:#fff;font-size:30px'>\n"
				+ "<div class='adM'> </div>\n" + "<div align='center'>\n" + "<div class='adM'> </div>\n"
				+ "<table width='100%' border='0' cellspacing='0' cellpadding='0'>\n" + "<tbody>\n" + "<tr>\n"
				+ "<td style='background-color:#f0f0f0;height:75px; padding: 15px;' align='center'> <img src='http://siscoatimagens.galleriabank.com.br/logo-galleria.png' height='65' width='300'> </td>\n"
				+ "</tr>\n" + "</tbody>\n" + "</table>\n" + "<br>\n" + "<table>\n" + "<tbody>\n" + "<tr>\n"
				+ "<td width='20'> </td>\n" + "<td>\n" + "<table>\n" + "<tbody>\n" + "<tr>\n"
				+ "<td style='font-family:Arial,sans-serif;color:#bb7e17;font-size:20px'>Olá\n"
				+ "<span style='font-weight:bold'>" + this.objetoContratoCobranca.getResponsavel().getNome()
				+ "</span>, </td>\n" + "</tr>\n" + "<tr>\n"
				+ "<td style='font-family:Arial,sans-serif;color:#58585a;font-size:14px;line-height:20px;padding-top:7px'> O contrato <b>"
				+ this.objetoContratoCobranca.getNumeroContrato() + "</b> (Pagador: <b>"
				+ this.objetoContratoCobranca.getPagador().getNome() + "</b>) teve atualização. </td>\n" + "</tr>\n"
				+ "</tbody>\n" + "</table>\n"
				+ "<div style='height:1px;background-color:#e8e8ed;margin-top:10px;margin-bottom:25px'> </div>\n"
				+ "<table width='100%' style='border-left:3px solid #bb7e17'>\n" + "<tbody>\n" + "<tr>\n"
				+ "<td style='font-family:Arial,sans-serif;color:#58585a;font-size:14px;padding-left:18px;line-height:16px'> <span style='font-size:10px;'>DATA DA ATUALIZAÇÃO\n"
				+ "</span>\n" + "<br><b>" + sdfDataRelComHoras.format(dataHoje) + "\n" + "</b> </td>\n"
				+ "</tr> </tbody> </table>";

		mensagemHtmlTeste = mensagemHtmlTeste + "<table width='100%' style='border-left:3px solid #bb7e17'>" + "<tbody>"
				+ "<tr>";

		// "Em Analise"
		if (this.objetoContratoCobranca.isInicioAnalise()
		// && this.objetoContratoCobranca.getCadastroAprovadoValor().equals("Aprovado")
		/*
		 * && (this.objetoContratoCobranca.getMatriculaAprovadaValor().equals("") ||
		 * this.objetoContratoCobranca.getMatriculaAprovadaValor() == null)
		 */) {

			mensagemHtmlTeste = mensagemHtmlTeste + " <td> "
					+ "<img src='http://siscoatimagens.galleriabank.com.br/EmAnaliseOk.png' height='57' width='120'>"
					+ "</td>";
		} else {
			mensagemHtmlTeste = mensagemHtmlTeste + " <td> "
					+ "<img src='http://siscoatimagens.galleriabank.com.br/EmAnaliseNOk.png' height='57' width='120'>"
					+ "</td>";
		}

		// "Em Analise"
		if (this.objetoContratoCobranca.getCadastroAprovadoValor() != null) {
			if (this.objetoContratoCobranca.getCadastroAprovadoValor().equals("Aprovado")) {
				mensagemHtmlTeste = mensagemHtmlTeste + " <td> "
						+ "<img src='http://siscoatimagens.galleriabank.com.br/PreCadastroAprovadoOk.png' height='57' width='120'>"
						+ "</td>";
			} else {
				mensagemHtmlTeste = mensagemHtmlTeste + " <td> "
						+ "<img src='http://siscoatimagens.galleriabank.com.br/PreCadastroAprovadoNOk.png' height='57' width='120'>"
						+ "</td>";
			}
		} else {
			mensagemHtmlTeste = mensagemHtmlTeste + " <td> "
					+ "<img src='http://siscoatimagens.galleriabank.com.br/PreCadastroAprovadoNOk.png' height='57' width='120'>"
					+ "</td>";
		}

		// "Análise Reprovada"
		if (this.objetoContratoCobranca.getCadastroAprovadoValor() != null) {
			if (this.objetoContratoCobranca.getCadastroAprovadoValor().equals("Reprovado")) {
				mensagemHtmlTeste = mensagemHtmlTeste + " <td> "
						+ "<img src='http://siscoatimagens.galleriabank.com.br/PreCadastroReprovadoOk.png' height='57' width='120'>"
						+ "</td>";
			}
		}

		// "Ag. Pagto. Laudo"
		if (// this.objetoContratoCobranca.isInicioAnalise() &&
		this.objetoContratoCobranca.getCadastroAprovadoValor() != null
				&& this.objetoContratoCobranca.getMatriculaAprovadaValor() != null) {
			if (// this.objetoContratoCobranca.isInicioAnalise() &&
			this.objetoContratoCobranca.getCadastroAprovadoValor().equals("Aprovado")
					&& this.objetoContratoCobranca.getMatriculaAprovadaValor().equals("Aprovado")
					&& this.objetoContratoCobranca.isPagtoLaudoConfirmada()) {

				mensagemHtmlTeste = mensagemHtmlTeste + " <td> "
						+ "<img src='http://siscoatimagens.galleriabank.com.br/AgPagtoBoletoOk.png' height='57' width='120'>"
						+ "</td>";
			} else {
				mensagemHtmlTeste = mensagemHtmlTeste + " <td> "
						+ "<img src='http://siscoatimagens.galleriabank.com.br/AgPagtoBoletoNOk.png' height='57' width='120'>"
						+ "</td>";
			}
		} else {
			mensagemHtmlTeste = mensagemHtmlTeste + " <td> "
					+ "<img src='http://siscoatimagens.galleriabank.com.br/AgPagtoBoletoNOk.png' height='57' width='120'>"
					+ "</td>";
		}

		// "Ag. PAJU e Laudo"
		if (/*
			 * this.objetoContratoCobranca.isInicioAnalise() &&
			 * this.objetoContratoCobranca.getCadastroAprovadoValor().equals("Aprovado") &&
			 * this.objetoContratoCobranca.getMatriculaAprovadaValor().equals("Aprovado") &&
			 */
		// this.objetoContratoCobranca.isPagtoLaudoConfirmada()
		this.objetoContratoCobranca.isLaudoRecebido() || this.objetoContratoCobranca.isPajurFavoravel()) {

			mensagemHtmlTeste = mensagemHtmlTeste + " <td> "
					+ "<img src='http://siscoatimagens.galleriabank.com.br/AgPAJULaudoOk.png' height='57' width='120'>"
					+ "</td>";
		} else {
			mensagemHtmlTeste = mensagemHtmlTeste + " <td> "
					+ "<img src='http://siscoatimagens.galleriabank.com.br/AgPAJULaudoNOk.png' height='57' width='120'>"
					+ "</td>";
		}

		// "Ag. DOC"
		if (/*
			 * this.objetoContratoCobranca.isInicioAnalise() &&
			 * this.objetoContratoCobranca.getCadastroAprovadoValor().equals("Aprovado") &&
			 * this.objetoContratoCobranca.getMatriculaAprovadaValor().equals("Aprovado") &&
			 * this.objetoContratoCobranca.isPagtoLaudoConfirmada() &&
			 */
		// this.objetoContratoCobranca.isLaudoRecebido() &&
		// this.objetoContratoCobranca.isPajurFavoravel() &&
		this.objetoContratoCobranca.isDocumentosCompletos()) {

			mensagemHtmlTeste = mensagemHtmlTeste + " <td> "
					+ "<img src='http://siscoatimagens.galleriabank.com.br/AgDOCOk.png' height='57' width='120'>"
					+ "</td>";
		} else {
			mensagemHtmlTeste = mensagemHtmlTeste + " <td> "
					+ "<img src='http://siscoatimagens.galleriabank.com.br/AgDOCNOk.png' height='57' width='120'>"
					+ "</td>";
		}

		mensagemHtmlTeste = mensagemHtmlTeste + " </tr> <tr>";

		// "Ag. CCB"
		if (/*
			 * this.objetoContratoCobranca.isInicioAnalise() &&
			 * this.objetoContratoCobranca.getCadastroAprovadoValor().equals("Aprovado") &&
			 * this.objetoContratoCobranca.getMatriculaAprovadaValor().equals("Aprovado") &&
			 * this.objetoContratoCobranca.isPagtoLaudoConfirmada() &&
			 * this.objetoContratoCobranca.isLaudoRecebido() &&
			 * this.objetoContratoCobranca.isPajurFavoravel() &&
			 * this.objetoContratoCobranca.isDocumentosCompletos() &&
			 */
		this.objetoContratoCobranca.isCcbPronta()) {

			mensagemHtmlTeste = mensagemHtmlTeste + " <td> "
					+ "<img src='http://siscoatimagens.galleriabank.com.br/AgCCBOk.png' height='57' width='120'>"
					+ "</td>";
		} else {
			mensagemHtmlTeste = mensagemHtmlTeste + " <td> "
					+ "<img src='http://siscoatimagens.galleriabank.com.br/AgCCBNOk.png' height='57' width='120'>"
					+ "</td>";
		}

		// "Ag. Assinatura"
		if (this.objetoContratoCobranca.getCadastroAprovadoValor() != null
				&& this.objetoContratoCobranca.getMatriculaAprovadaValor() != null) {
			if (this.objetoContratoCobranca.isInicioAnalise()
					&& this.objetoContratoCobranca.getCadastroAprovadoValor().equals("Aprovado")
					&& this.objetoContratoCobranca.getMatriculaAprovadaValor().equals("Aprovado")
					&& this.objetoContratoCobranca.isPagtoLaudoConfirmada()
					&& this.objetoContratoCobranca.isLaudoRecebido() && this.objetoContratoCobranca.isPajurFavoravel()
					&& this.objetoContratoCobranca.isDocumentosCompletos()
					&& this.objetoContratoCobranca.isCcbPronta()) {

				mensagemHtmlTeste = mensagemHtmlTeste + " <td> "
						+ "<img src='http://siscoatimagens.galleriabank.com.br/AgAssinaturaOk.png' height='57' width='120'>"
						+ "</td>";
			} else {
				mensagemHtmlTeste = mensagemHtmlTeste + " <td> "
						+ "<img src='http://siscoatimagens.galleriabank.com.br/AgAssinaturaNOk.png' height='57' width='120'>"
						+ "</td>";
			}
		} else {
			mensagemHtmlTeste = mensagemHtmlTeste + " <td> "
					+ "<img src='http://siscoatimagens.galleriabank.com.br/AgAssinaturaNOk.png' height='57' width='120'>"
					+ "</td>";
		}

		// "Ag. Registro"
		if (this.objetoContratoCobranca.getCadastroAprovadoValor() != null
				&& this.objetoContratoCobranca.getMatriculaAprovadaValor() != null) {
			if (this.objetoContratoCobranca.isInicioAnalise()
					&& this.objetoContratoCobranca.getCadastroAprovadoValor().equals("Aprovado")
					&& this.objetoContratoCobranca.getMatriculaAprovadaValor().equals("Aprovado")
					&& this.objetoContratoCobranca.isPagtoLaudoConfirmada()
					&& this.objetoContratoCobranca.isLaudoRecebido() && this.objetoContratoCobranca.isPajurFavoravel()
					&& this.objetoContratoCobranca.isDocumentosCompletos()
					&& this.objetoContratoCobranca.isCcbPronta()) {

				mensagemHtmlTeste = mensagemHtmlTeste + " <td> "
						+ "<img src='http://siscoatimagens.galleriabank.com.br/AgRegistroOk.png' height='57' width='120'>"
						+ "</td>";
			} else {
				mensagemHtmlTeste = mensagemHtmlTeste + " <td> "
						+ "<img src='http://siscoatimagens.galleriabank.com.br/AgRegistroNOk.png' height='57' width='120'>"
						+ "</td>";
			}
		} else {
			mensagemHtmlTeste = mensagemHtmlTeste + " <td> "
					+ "<img src='http://siscoatimagens.galleriabank.com.br/AgRegistroNOk.png' height='57' width='120'>"
					+ "</td>";
		}

		// "Contrato Aprovado"
		/*
		 * if (this.objetoContratoCobranca.getStatus().equals("Aprovado")) {
		 * mensagemHtmlTeste = mensagemHtmlTeste + " <td> " +
		 * "<img src='http://siscoatimagens.galleriabank.com.br/ContratoAprovadoOk.png' height='57' width='120'>"
		 * + "</td>"; } else { mensagemHtmlTeste = mensagemHtmlTeste + " <td> " +
		 * "<img src='http://siscoatimagens.galleriabank.com.br/ContratoAprovadoNOk.png' height='57' width='120'>"
		 * + "</td>"; }
		 */
		/*
		 * String status = "";
		 * 
		 * if (this.objetoContratoCobranca.getStatusContrato() == null) { status =
		 * "Não informado"; } else { if
		 * (this.objetoContratoCobranca.getStatusContrato().equals("")) { status =
		 * "Não informado"; } else { status =
		 * this.objetoContratoCobranca.getStatusContrato(); } }
		 * 
		 * mensagemHtmlTeste = mensagemHtmlTeste + "<br>Status: <b>" + status +
		 * "</b>\n";
		 */
		mensagemHtmlTeste = mensagemHtmlTeste + " </tr> ";

		mensagemHtmlTeste = mensagemHtmlTeste + " </tbody> " + " </table>" + " </td>" + " <td width='20'> </td>"
				+ " </tr>" + " </tbod>" + " </table>" + " <br>"
				+ " <table width='100%' border='0' cellspacing='0' cellpadding='0'>" + " <tbody>"
				+ " <tr style='background-color:#f0f0f0;height:61px;font-family:Arial,sans-serif;font-size:10px;color:#fff'>"
				+ " <td style='color:#16243f;padding-left:20px; font-size: 12px;'> © Todos direitos reservados. </td>"
				+ " <td style='text-align: right;padding-right: 20px;'><a style='color:#16243f;font-size: 12px; text-decoration: none;' href='http://sistema.galleriabank.com.br/' target='_blank'>Galleria Bank</a> </td>"
				+ " </tr>" + " </tbody>" + " </table>" + " <div class='yj6qo'></div>" + " <div class='adL'> </div>"
				+ " </div>" + " <div class='adL'> </div>" + " <div class='adL'> </div>" + " </div>" + " </body>"
				+ " </html>";

		try {
			ResponsavelDao rDao = new ResponsavelDao();
			EnviaEmail eec = new EnviaEmail();
			eec.enviarEmailHtmlResponsavelAdms(this.objetoContratoCobranca.getResponsavel().getEmail(),
					"[siscoat] Atualização do contrato " + this.objetoContratoCobranca.getNumeroContrato(),
					mensagemHtmlTeste);

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	/**
	 * SERVICO PARA PEGAR O ENDEREÇO AUTOMATICAMENTE
	 * 
	 * vianet.com.br
	 */
	public void getEnderecoByViaNet() {
		try {
			String inputCep = this.objetoPagadorRecebedor.getCep().replace("-", "");
			FacesContext context = FacesContext.getCurrentInstance();

			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("http://viacep.com.br/ws/" + inputCep + "/json/");

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);

			String erro = "";
			JSONObject myResponse = null;

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				this.objetoPagadorRecebedor.setEndereco("");
				this.objetoPagadorRecebedor.setBairro("");
				this.objetoPagadorRecebedor.setCidade("");
				this.objetoPagadorRecebedor.setEstado("");
			} else {
				myResponse = getJsonSucesso(myURLConnection.getInputStream());

				this.objetoPagadorRecebedor.setEndereco(myResponse.get("logradouro").toString());
				this.objetoPagadorRecebedor.setBairro(myResponse.get("bairro").toString());
				this.objetoPagadorRecebedor.setCidade(myResponse.get("localidade").toString());
				this.objetoPagadorRecebedor.setEstado(myResponse.get("uf").toString());
			}
			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getEnderecoByViaNetPagador(PagadorRecebedor pagador) {
		try {
			String inputCep = pagador.getCep().replace("-", "");
			FacesContext context = FacesContext.getCurrentInstance();

			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("http://viacep.com.br/ws/" + inputCep + "/json/");

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);

			String erro = "";
			JSONObject myResponse = null;

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				pagador.setEndereco("");
				pagador.setBairro("");
				pagador.setCidade("");
				pagador.setEstado("");
			} else {
				myResponse = getJsonSucesso(myURLConnection.getInputStream());

				pagador.setEndereco(myResponse.get("logradouro").toString());
				pagador.setBairro(myResponse.get("bairro").toString());
				pagador.setCidade(myResponse.get("localidade").toString());
				pagador.setEstado(myResponse.get("uf").toString());
			}
			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getEnderecoByViaNetImovelCobranca() {
		try {
			String inputCep = this.objetoImovelCobranca.getCep().replace("-", "");
			FacesContext context = FacesContext.getCurrentInstance();

			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("http://viacep.com.br/ws/" + inputCep + "/json/");

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);

			String erro = "";
			JSONObject myResponse = null;

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				this.objetoImovelCobranca.setEndereco("");
				this.objetoImovelCobranca.setBairro("");
				this.objetoImovelCobranca.setCidade("");
				this.objetoImovelCobranca.setEstado("");
			} else {
				myResponse = getJsonSucesso(myURLConnection.getInputStream());

				this.objetoImovelCobranca.setEndereco(myResponse.get("logradouro").toString());
				this.objetoImovelCobranca.setBairro(myResponse.get("bairro").toString());
				this.objetoImovelCobranca.setCidade(myResponse.get("localidade").toString());
				this.objetoImovelCobranca.setEstado(myResponse.get("uf").toString());
			}
			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getLinkMaps() {
		String linkImovel = "";
		linkImovel = this.objetoImovelCobranca.getEndereco() + "," + this.objetoImovelCobranca.getCidade() + this.objetoImovelCobranca.getEstado();
		linkImovel = linkImovel.replaceAll(" ", "+");
		linkImovel = linkImovel.replaceAll("," , "%2C");
		this.objetoImovelCobranca.setLinkGMaps("https://www.google.com/maps/search/?api=1&query="+linkImovel);
	}

	public void getEnderecoByViaNetConjuge() {
		try {
			String inputCep = this.objetoPagadorRecebedor.getCep().replace("-", "");
			FacesContext context = FacesContext.getCurrentInstance();

			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("http://viacep.com.br/ws/" + inputCep + "/json/");

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);

			String erro = "";
			JSONObject myResponse = null;

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				this.objetoPagadorRecebedor.setEndereco("");
				this.objetoPagadorRecebedor.setBairro("");
				this.objetoPagadorRecebedor.setCidade("");
				this.objetoPagadorRecebedor.setEstado("");
			} else {
				myResponse = getJsonSucesso(myURLConnection.getInputStream());

				this.objetoPagadorRecebedor.setEnderecoConjuge(myResponse.get("logradouro").toString());
				this.objetoPagadorRecebedor.setBairroConjuge(myResponse.get("bairro").toString());
				this.objetoPagadorRecebedor.setCidadeConjuge(myResponse.get("localidade").toString());
				this.objetoPagadorRecebedor.setEstadoConjuge(myResponse.get("uf").toString());
			}
			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getEnderecoByViaNetCoobrigado() {
		try {
			String inputCep = this.objetoPagadorRecebedor.getCep().replace("-", "");
			FacesContext context = FacesContext.getCurrentInstance();

			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("http://viacep.com.br/ws/" + inputCep + "/json/");

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);

			String erro = "";
			JSONObject myResponse = null;

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				this.objetoPagadorRecebedor.setEndereco("");
				this.objetoPagadorRecebedor.setBairro("");
				this.objetoPagadorRecebedor.setCidade("");
				this.objetoPagadorRecebedor.setEstado("");
			} else {
				myResponse = getJsonSucesso(myURLConnection.getInputStream());

				this.objetoPagadorRecebedor.setEnderecoCoobrigado(myResponse.get("logradouro").toString());
				this.objetoPagadorRecebedor.setBairroCoobrigado(myResponse.get("bairro").toString());
				this.objetoPagadorRecebedor.setCidadeCoobrigado(myResponse.get("localidade").toString());
				this.objetoPagadorRecebedor.setEstadoCoobrigado(myResponse.get("uf").toString());
			}
			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getEnderecoByViaNetCoobrigadoCasado() {
		try {
			String inputCep = this.objetoPagadorRecebedor.getCep().replace("-", "");
			FacesContext context = FacesContext.getCurrentInstance();

			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("http://viacep.com.br/ws/" + inputCep + "/json/");

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);

			String erro = "";
			JSONObject myResponse = null;

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				this.objetoPagadorRecebedor.setEndereco("");
				this.objetoPagadorRecebedor.setBairro("");
				this.objetoPagadorRecebedor.setCidade("");
				this.objetoPagadorRecebedor.setEstado("");
			} else {
				myResponse = getJsonSucesso(myURLConnection.getInputStream());

				this.objetoPagadorRecebedor.setEnderecoCoobrigadoCasado(myResponse.get("logradouro").toString());
				this.objetoPagadorRecebedor.setBairroCoobrigadoCasado(myResponse.get("bairro").toString());
				this.objetoPagadorRecebedor.setCidadeCoobrigadoCasado(myResponse.get("localidade").toString());
				this.objetoPagadorRecebedor.setEstadoCoobrigadoCasado(myResponse.get("uf").toString());
			}
			myURLConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 
	 * PARSE DO RETORNO SUCESSO
	 * 
	 * @param inputStream
	 * @return
	 */
	public JSONObject getJsonSucesso(InputStream inputStream) {
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// READ JSON response and print
			JSONObject myResponse = new JSONObject(response.toString());

			return myResponse;

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void populaReferenciaBancariaCPF() {
		FacesContext context = FacesContext.getCurrentInstance();
		boolean validaCPF = ValidaCPF.isCPFOnly(this.objetoPagadorRecebedor.getCpf());

		if (!validaCPF) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Pré-Contrato: O CPF inserido é inválido ou está incorreto!", ""));
		} else {
			this.objetoPagadorRecebedor.setCpfCC(this.objetoPagadorRecebedor.getCpf());
			this.objetoPagadorRecebedor.setNomeCC(this.objetoPagadorRecebedor.getNome());
		}
	}

	public void populaReferenciaBancariaCNPJ() {
		FacesContext context = FacesContext.getCurrentInstance();
		boolean validaCNPJ = ValidaCNPJ.isCNPJOnly(this.objetoPagadorRecebedor.getCnpj());

		if (!validaCNPJ) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Pré-Contrato: O CNPJ inserido é inválido ou está incorreto!", ""));
		} else {
			this.objetoPagadorRecebedor.setCnpjCC(this.objetoPagadorRecebedor.getCnpj());
			this.objetoPagadorRecebedor.setNomeCC(this.objetoPagadorRecebedor.getNome());
		}
	}
	
	public void populaReferenciaBancariaConjuge() {
		this.objetoPagadorRecebedor.setCpfCCConjuge(this.objetoPagadorRecebedor.getCpfConjuge());
		this.objetoPagadorRecebedor.setNomeCCConjuge(this.objetoPagadorRecebedor.getNomeConjuge());
	}
	
	public void populaReferenciaBancariaCoobrigado() {
		FacesContext context = FacesContext.getCurrentInstance();
		boolean validaCPF = ValidaCPF.isCPFOnly(this.objetoPagadorRecebedor.getCpfCoobrigado());

		if (!validaCPF) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Pré-Contrato: O CPF inserido é inválido ou está incorreto!", ""));
		} else {
			this.objetoPagadorRecebedor.setCpfCCCoobrigado(this.objetoPagadorRecebedor.getCpfCoobrigado());
			this.objetoPagadorRecebedor.setNomeCCCoobrigado(this.objetoPagadorRecebedor.getNomeCoobrigado());
		}
	}
	
	public void populaReferenciaBancariaCoobrigadoCasado() {
		FacesContext context = FacesContext.getCurrentInstance();
		boolean validaCPF = ValidaCPF.isCPFOnly(this.objetoPagadorRecebedor.getCpfCoobrigadoCasado());

		if (!validaCPF) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Pré-Contrato: O CPF inserido é inválido ou está incorreto!", ""));
		} else {
			this.objetoPagadorRecebedor.setCpfCCCoobrigadoCasado(this.objetoPagadorRecebedor.getCpfCoobrigadoCasado());
			this.objetoPagadorRecebedor.setNomeCCCoobrigadoCasado(this.objetoPagadorRecebedor.getNomeCoobrigadoCasado());
		}
	}
	
	
	public void populaReferenciaBancariaCPFPagador(PagadorRecebedor pagador) {
		FacesContext context = FacesContext.getCurrentInstance();
		boolean validaCPF = ValidaCPF.isCPFOnly(pagador.getCpf());

		if (!validaCPF) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Pré-Contrato: O CPF inserido é inválido ou está incorreto!", ""));
		} else {
			pagador.setCpfCC(pagador.getCpf());
			pagador.setNomeCC(pagador.getNome());
		}
	}

	/**
	 * 
	 * @param origem os valores são publico ou aprovado
	 * @return
	 */
	public String addPreContratoLeadSite() {
		ResponsavelDao responsavelDao = new ResponsavelDao();
		FacesContext context = FacesContext.getCurrentInstance();
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

		// verifica se o responsavel informado existe
		if (responsavelDao.findByFilter("codigo", this.codigoResponsavel).size() > 0) {
			Responsavel responsavel = responsavelDao.findByFilter("codigo", this.codigoResponsavel).get(0);

			this.objetoContratoCobranca.setResponsavel(responsavel);
			
			if( CommonsUtil.mesmoValor(responsavel.getId(), CommonsUtil.longValue("46") ) ) {
				this.objetoContratoCobranca.setContratoLead(true);
			} else if(CommonsUtil.mesmoValor(this.objetoContratoCobranca.isContratoLead(), null)) {
				this.objetoContratoCobranca.setContratoLead(true);
			}

			PagadorRecebedor pagadorRecebedor = null;
			PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();

			if (this.objetoPagadorRecebedor.getId() <= 0) {
				List<PagadorRecebedor> pagadorRecebedorBD = new ArrayList<PagadorRecebedor>();
				boolean registraPagador = false;
				Long idPagador = (long) 0;

				if (this.objetoPagadorRecebedor.getCpf() != null) {
					pagadorRecebedorBD = pagadorRecebedorDao.findByFilter("cpf", this.objetoPagadorRecebedor.getCpf());
					if (pagadorRecebedorBD.size() > 0) {
						pagadorRecebedor = pagadorRecebedorBD.get(0);
					} else {
						pagadorRecebedor = this.objetoPagadorRecebedor;
						registraPagador = true;
					}
				}

				if (this.objetoPagadorRecebedor.getCnpj() != null) {
					pagadorRecebedorBD = pagadorRecebedorDao.findByFilter("cnpj",
							this.objetoPagadorRecebedor.getCnpj());
					if (pagadorRecebedorBD.size() > 0) {
						pagadorRecebedor = pagadorRecebedorBD.get(0);
					} else {
						pagadorRecebedor = this.objetoPagadorRecebedor;
						registraPagador = true;
					}
				}

				registraPagador = true;

				if (pagadorRecebedor == null) {
					pagadorRecebedor = this.objetoPagadorRecebedor;
				}

				if (this.objetoPagadorRecebedor.getSite() != null && this.objetoPagadorRecebedor.getSite().equals("")) {
					if (!this.objetoPagadorRecebedor.getSite().contains("http")) {
						this.objetoPagadorRecebedor
								.setSite("HTTP://" + this.objetoPagadorRecebedor.getSite().toLowerCase());
					}
				}

				if (registraPagador) {
					idPagador = pagadorRecebedorDao.create(pagadorRecebedor);
					pagadorRecebedor = pagadorRecebedorDao.findById(idPagador);
				}
			} else {
				pagadorRecebedorDao.merge(this.objetoPagadorRecebedor);
				pagadorRecebedor = this.objetoPagadorRecebedor;
			}

			// INSERE AQUI

			ImovelCobrancaDao imovelCobrancaDao = new ImovelCobrancaDao();
			ImovelCobranca imovelCobranca = new ImovelCobranca();

			// if (imovelCobrancaDao.findByFilter("numeroMatricula",
			// this.objetoImovelCobranca.getNumeroMatricula()).size() > 0) {
			// imovelCobranca = imovelCobrancaDao.findByFilter("numeroMatricula",
			// this.objetoImovelCobranca.getNumeroMatricula()).get(0);
			// } else {
			long idIm = imovelCobrancaDao.create(this.objetoImovelCobranca);
			imovelCobranca = imovelCobrancaDao.findById(idIm);
			// }

			this.objetoContratoCobranca.setPagador(pagadorRecebedor);
			this.objetoContratoCobranca.setImovel(imovelCobranca);

			this.objetoContratoCobranca.setRecebedor(null);

			if (this.qtdeParcelas != null && !this.qtdeParcelas.equals("")) {
				this.objetoContratoCobranca.setQtdeParcelas(Integer.valueOf(this.qtdeParcelas));
			}

			BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();

			if (this.objetoContratoCobranca.getVlrParcela() != null) {
				this.objetoContratoCobranca.setVlrParcelaStr(
						bigDecimalConverter.getAsString(null, null, this.objetoContratoCobranca.getVlrParcela()));
			}

			updateCheckList();

			this.objetoContratoCobranca.setStatusLead("Novo Lead");

			contratoCobrancaDao.create(this.objetoContratoCobranca);

			enviaEmailCriacaoPreContrato();

			if (context != null) {
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Contrato Cobrança: Pré-Contrato adicionado com sucesso! (Contrato: "
										+ this.objetoContratoCobranca.getNumeroContrato() + ")!",
								""));

				return geraConsultaContratosPendentes();
			} else {
				return "";
			}
		} else {
			if (context != null) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Contrato Cobrança: Erro de validação: O código do responsável digitado não foi encontrado ("
								+ this.codigoResponsavel + ")!",
						""));
			}

			return null;
		}
	}

	/**
	 * 
	 * @param origem os valores são publico ou aprovado
	 * @return
	 */
	public String addPreContrato() {
		ResponsavelDao responsavelDao = new ResponsavelDao();
		FacesContext context = FacesContext.getCurrentInstance();
		String msgRetorno = null;
		boolean erroValidacaoLov = false;
		boolean erroValidacaoBaixa = false;
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

		try {
			// verifica se o responsavel informado existe
			if (responsavelDao.findByFilter("codigo", this.codigoResponsavel).size() > 0) {
				Responsavel responsavel = responsavelDao.findByFilter("codigo", this.codigoResponsavel).get(0);

				this.objetoContratoCobranca.setResponsavel(responsavel);
				
				if( CommonsUtil.mesmoValor(responsavel.getId(), CommonsUtil.longValue("46") ) ) {
					this.objetoContratoCobranca.setContratoLead(true);
				} else if(CommonsUtil.mesmoValor(this.objetoContratoCobranca.isContratoLead(), null)) {
					this.objetoContratoCobranca.setContratoLead(false);
				}

				PagadorRecebedor pagadorRecebedor = null;
				PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();

				if (this.objetoPagadorRecebedor.getId() <= 0) {
					List<PagadorRecebedor> pagadorRecebedorBD = new ArrayList<PagadorRecebedor>();
					boolean registraPagador = false;
					Long idPagador = (long) 0;

					if (this.objetoPagadorRecebedor.getCpf() != null) {
						pagadorRecebedorBD = pagadorRecebedorDao.findByFilter("cpf",
								this.objetoPagadorRecebedor.getCpf());
						if (pagadorRecebedorBD.size() > 0) {
							pagadorRecebedor = pagadorRecebedorBD.get(0);
						} else {
							pagadorRecebedor = this.objetoPagadorRecebedor;
							registraPagador = true;
						}
					}

					if (this.objetoPagadorRecebedor.getCnpj() != null) {
						pagadorRecebedorBD = pagadorRecebedorDao.findByFilter("cnpj",
								this.objetoPagadorRecebedor.getCnpj());
						if (pagadorRecebedorBD.size() > 0) {
							pagadorRecebedor = pagadorRecebedorBD.get(0);
						} else {
							pagadorRecebedor = this.objetoPagadorRecebedor;
							registraPagador = true;
						}
					}

					registraPagador = true;

					if (pagadorRecebedor == null) {
						pagadorRecebedor = this.objetoPagadorRecebedor;
					}

					if (this.objetoPagadorRecebedor.getSite() != null
							&& this.objetoPagadorRecebedor.getSite().equals("")) {
						if (!this.objetoPagadorRecebedor.getSite().contains("http")) {
							this.objetoPagadorRecebedor
									.setSite("HTTP://" + this.objetoPagadorRecebedor.getSite().toLowerCase());
						}
					}

					if (registraPagador) {
						idPagador = pagadorRecebedorDao.create(pagadorRecebedor);
						pagadorRecebedor = pagadorRecebedorDao.findById(idPagador);
					}
				} else {
					pagadorRecebedorDao.merge(this.objetoPagadorRecebedor);
					pagadorRecebedor = this.objetoPagadorRecebedor;
				}

				if (this.objetoContratoCobranca.getQuantoPrecisa().compareTo(BigDecimal.valueOf(2000000)) == 1) {
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Contrato Cobrança: Erro de validação: Valor Acima do limite atual de R$2.000.000,00. !",
							""));

					return "";
				}

				// VALIDA IMOVEL
				String imovelValido = null;
				String matriculaLimpa = CommonsUtil.somenteNumeros(this.objetoImovelCobranca.getNumeroMatricula());
				String cepLimpo = CommonsUtil
						.somenteNumeros(this.objetoImovelCobranca.getCep().replace(".", "").replace("-", ""));

				imovelValido = contratoCobrancaDao.validaImovelNovoContrato(matriculaLimpa, cepLimpo);

				// valida imovel
				if (imovelValido == null) {
					ImovelCobrancaDao imovelCobrancaDao = new ImovelCobrancaDao();
					ImovelCobranca imovelCobranca = new ImovelCobranca();

					// if (imovelCobrancaDao.findByFilter("numeroMatricula",
					// this.objetoImovelCobranca.getNumeroMatricula()).size() > 0) {
					// imovelCobranca = imovelCobrancaDao.findByFilter("numeroMatricula",
					// this.objetoImovelCobranca.getNumeroMatricula()).get(0);
					// } else {
					long idIm = imovelCobrancaDao.create(this.objetoImovelCobranca);
					imovelCobranca = imovelCobrancaDao.findById(idIm);
					// }

					this.objetoContratoCobranca.setPagador(pagadorRecebedor);
					this.objetoContratoCobranca.setImovel(imovelCobranca);

					this.objetoContratoCobranca.setRecebedor(null);

					if (this.qtdeParcelas != null && !this.qtdeParcelas.equals("")) {
						this.objetoContratoCobranca.setQtdeParcelas(Integer.valueOf(this.qtdeParcelas));
					}

					BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();

					if (this.objetoContratoCobranca.getVlrParcela() != null) {
						this.objetoContratoCobranca.setVlrParcelaStr(bigDecimalConverter.getAsString(null, null,
								this.objetoContratoCobranca.getVlrParcela()));
					}

					updateCheckList();

					this.objetoContratoCobranca.setStatusLead("Completo");
					this.objetoContratoCobranca.setInicioAnalise(false);

					contratoCobrancaDao.create(this.objetoContratoCobranca);

					// enviaEmailCriacaoPreContrato();
					if (!this.objetoContratoCobranca.isAgRegistro()) {
						/*
						 * this.vlrRepasse = this.vlrRepasseNew; this.vlrRetencao = this.vlrRetencaoNew;
						 * this.vlrComissao = this.vlrComissaoNew;
						 * 
						 * this.vlrRepasseFinal = this.vlrRepasseFinalNew; this.vlrRetencaoFinal =
						 * this.vlrRetencaoFinalNew; this.vlrComissaoFinal = this.vlrComissaoFinalNew;
						 * 
						 * if (!this.validarProcentagensSeguro()) { context.addMessage(null, new
						 * FacesMessage(FacesMessage.SEVERITY_ERROR,
						 * "A soma das porcentagens dos segurados não é 100%", "")); erroValidacaoLov =
						 * true; }
						 * 
						 * if (this.selectedImovel == null) { context.addMessage(null, new
						 * FacesMessage(FacesMessage.SEVERITY_ERROR,
						 * "Contrato Cobrança: Erro de validação: é obrigatória a seleção do Imóvel.",
						 * "")); erroValidacaoLov = true; } else { imovelCobrancaDao = new
						 * ImovelCobrancaDao();
						 * this.objetoContratoCobranca.setImovel(imovelCobrancaDao.findById(this.
						 * selectedImovel.getId())); }
						 * 
						 * if (this.selectedResponsavel == null) { context.addMessage(null, new
						 * FacesMessage(FacesMessage.SEVERITY_ERROR,
						 * "Contrato Cobrança: Erro de validação: é obrigatória a seleção do Responsável."
						 * , "")); erroValidacaoLov = true; } else { responsavelDao = new
						 * ResponsavelDao(); this.objetoContratoCobranca
						 * .setResponsavel(responsavelDao.findById(this.selectedResponsavel.getId())); }
						 * 
						 * // se houve erro(s) na validações acima, retorna nulo e mensagem de erro if
						 * (erroValidacaoLov || erroValidacaoBaixa) { return ""; } // FIM - Validacao
						 * das LoVs
						 * 
						 * if (this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() <=
						 * 0) { // se esta editando pela primeira vez o contrato aprovado // gera
						 * remuneracao responsaveis
						 * geraContasPagarRemuneracao(this.objetoContratoCobranca);
						 * 
						 * TimeZone zone = TimeZone.getDefault(); Locale locale = new Locale("pt",
						 * "BR"); Calendar dataInicio = Calendar.getInstance(zone, locale);
						 * dataInicio.setTime(this.objetoContratoCobranca.getDataInicio());
						 * 
						 * // Adiciona parcelas de pagamento
						 * 
						 * GeracaoBoletoMB geracaoBoletoMB = new GeracaoBoletoMB();
						 * 
						 * if (this.isGeraBoletoInclusaoContrato()) { geracaoBoletoMB.geraPDFBoletos(
						 * "Boletos Bradesco - Contrato: " +
						 * this.objetoContratoCobranca.getNumeroContrato());
						 * 
						 * this.fileBoleto = geracaoBoletoMB.getFile(); } } else { // se a quantidade de
						 * parcelas for igual, atualiza os valores e refaz as datas // de vencimento if
						 * (Integer.valueOf(this.qtdeParcelas) ==
						 * this.objetoContratoCobranca.getQtdeParcelas()) { // atualiza Repasse /
						 * Retenção / Comissão caso seja diferente for (ContratoCobrancaDetalhes ccd :
						 * this.objetoContratoCobranca .getListContratoCobrancaDetalhes()) { if
						 * (!ccd.isParcelaPaga()) { if (ccd.getVlrRepasse() != this.vlrRepasse ||
						 * ccd.getVlrRetencao() != this.vlrRetencao || ccd.getVlrComissao() !=
						 * this.vlrComissao) { ccd.setVlrRepasse(this.vlrRepasse);
						 * ccd.setVlrRetencao(this.vlrRetencao); ccd.setVlrComissao(this.vlrComissao); }
						 * else { break; } } } } }
						 * 
						 * try { if (objetoContratoCobranca.getId() <= 0) {
						 * contratoCobrancaDao.create(objetoContratoCobranca); msgRetorno = "inserido";
						 * } else { if (this.objetoContratoCobranca.isGeraParcelaFinal()) { if
						 * (!this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
						 * .get(this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() -
						 * 1) .isParcelaPaga()) {
						 * this.objetoContratoCobranca.getListContratoCobrancaDetalhes().get(
						 * this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() - 1)
						 * .setVlrComissao(this.vlrComissaoFinal);
						 * this.objetoContratoCobranca.getListContratoCobrancaDetalhes().get(
						 * this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() - 1)
						 * .setVlrRepasse(this.vlrRepasseFinal);
						 * this.objetoContratoCobranca.getListContratoCobrancaDetalhes().get(
						 * this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() - 1)
						 * .setVlrRetencao(this.vlrRetencaoFinal); } }
						 * 
						 * contratoCobrancaDao.merge(objetoContratoCobranca); msgRetorno = "atualizado";
						 * }
						 * 
						 * context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						 * "Contrato Cobrança: Registro " + msgRetorno + " com sucesso!", ""));
						 * 
						 * } catch (DAOException e) {
						 * 
						 * context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						 * "Contrato Cobrança: " + e, ""));
						 * 
						 * return ""; } catch (DBConnectionException e) { context.addMessage(null, new
						 * FacesMessage(FacesMessage.SEVERITY_ERROR, "Contrato Cobrança: " + e, ""));
						 * 
						 * return ""; }
						 * 
						 * this.contratoGerado = true;
						 */
					}

					if (context != null) {
						context.addMessage(null,
								new FacesMessage(FacesMessage.SEVERITY_INFO,
										"Contrato Cobrança: Pré-Contrato adicionado com sucesso! (Contrato: "
												+ this.objetoContratoCobranca.getNumeroContrato() + ")!",
										""));

						return geraConsultaContratosPendentes();
					} else {
						return "";
					}
				} else {
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Contrato Cobrança: Erro de validação: A matrícula informada já está vinculada a um contrato no sistema.  (Contrato: "
									+ imovelValido + ")!",
							""));

					return "";
				}

			} else {
				if (context != null) {
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Contrato Cobrança: Erro de validação: O código do responsável digitado não foi encontrado ("
									+ this.codigoResponsavel + ")!",
							""));
				}

				return null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contrato Cobrança: " + e, ""));
			return null;
		}
	}

	public void updatePagadorRecebedorDados() {
		FacesContext context = FacesContext.getCurrentInstance();

		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		pagadorRecebedorDao.merge(this.objetoContratoCobranca.getPagador());

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Contrato Cobrança: Os dados do pagador foram atualizados com sucesso!", ""));
	}

	public void updateResponsavelDados() {
		FacesContext context = FacesContext.getCurrentInstance();

		ResponsavelDao responsavelDao = new ResponsavelDao();
		responsavelDao.merge(this.objetoContratoCobranca.getResponsavel());

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Contrato Cobrança: Os dados do responsável foram atualizados com sucesso!", ""));
	}

	public String editPreContrato() {
		ResponsavelDao responsavelDao = new ResponsavelDao();
		FacesContext context = FacesContext.getCurrentInstance();
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

		if (responsavelDao.findByFilter("codigo", this.codigoResponsavel).size() > 0) {
			Responsavel responsavel = responsavelDao.findByFilter("codigo", this.codigoResponsavel).get(0);

			this.objetoContratoCobranca.setResponsavel(responsavel);
			
			if( CommonsUtil.mesmoValor(responsavel.getId(), CommonsUtil.longValue("46") ) ) {
				this.objetoContratoCobranca.setContratoLead(true);
			} else if(CommonsUtil.mesmoValor(this.objetoContratoCobranca.isContratoLead(), null)) {
				this.objetoContratoCobranca.setContratoLead(false);
			}

			if (this.objetoPagadorRecebedor.getSite() != null) {
				if (!this.objetoPagadorRecebedor.getSite().contains("http")
						&& !this.objetoPagadorRecebedor.getSite().contains("HTTP")) {
					this.objetoPagadorRecebedor
							.setSite("http://" + this.objetoPagadorRecebedor.getSite().toLowerCase());
				}
			}			

			PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
			pagadorRecebedorDao.merge(this.objetoPagadorRecebedor);
			ImovelCobrancaDao imovelCobrancaDao = new ImovelCobrancaDao();
			imovelCobrancaDao.merge(this.objetoImovelCobranca);

			this.objetoContratoCobranca.setPagador(this.objetoPagadorRecebedor);
			this.objetoContratoCobranca.setImovel(this.objetoImovelCobranca);

			if (this.qtdeParcelas != null && !this.qtdeParcelas.equals("")) {
				this.objetoContratoCobranca.setQtdeParcelas(Integer.valueOf(this.qtdeParcelas));
			}

			if (this.objetoContratoCobranca.getVlrParcela() != null) {
				BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();

				this.objetoContratoCobranca.setVlrParcelaStr(
						bigDecimalConverter.getAsString(null, null, this.objetoContratoCobranca.getVlrParcela()));
			}
			
			if(this.objetoContratoCobranca.getQuantoPrecisa().compareTo(BigDecimal.valueOf(2000000)) == 1) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
						"Contrato Cobrança: Erro de validação: Valor Acima do limite atual de R$2.000.000,00. !", ""));
				
				return "";
			}

			updateCheckList();
			
			//gerando parcelas quando contrato esta em ag registro
			if (!this.objetoContratoCobranca.isAgAssinatura() && this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() <= 0 && !CommonsUtil.semValor(this.objetoContratoCobranca.getValorCCB())) {				
				geraContratoCobrancaDetalhes(contratoCobrancaDao);			
			}

			contratoCobrancaDao.merge(this.objetoContratoCobranca);

			// verifica se o contrato for aprovado, manda um tipo de email..
			// senao valida se houve alteração no checklist para envio de email.
			
			enviaEmailAtualizacaoPreContrato();

			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Contrato Cobrança: Pré-Contrato editado com sucesso! (Contrato: "
									+ this.objetoContratoCobranca.getNumeroContrato() + ")!",
							""));

			if (!this.preContratoCustom) {
				return geraConsultaContratosPendentes();
			} else {
				if (this.objetoContratoCobranca.getStatusLead().equals("Novo Lead")) {
					return geraConsultaLeads("Novo Lead");
				}
				if (this.objetoContratoCobranca.getStatusLead().equals("Em Tratamento")) {
					return geraConsultaLeads("Em Tratamento");
				}
				if (this.objetoContratoCobranca.getStatusLead().equals("Completo")) {
					return geraConsultaLeads("Completo");
				}
				if (this.objetoContratoCobranca.getStatusLead().equals("Reprovado")) {
					return geraConsultaLeads("Reprovado");
				}
				if (this.objetoContratoCobranca.getStatusLead().equals("Baixado")) {
					return geraConsultaLeads("Baixado");
				}

				return "";
			}
		} else {
			if (context != null) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Contrato Cobrança: Erro de validação: O código do responsável digitado não foi encontrado ("
								+ this.codigoResponsavel + ")!",
						""));
			}

			return null;
		}
	}

	
	public String editPreContratoPorStatus() {
		ResponsavelDao responsavelDao = new ResponsavelDao();
		FacesContext context = FacesContext.getCurrentInstance();
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

		try {				
			
			// envia WhatsApp
			notificaStatusWhatsApp(this.objetoContratoCobranca.getId());

			if (responsavelDao.findByFilter("codigo", this.codigoResponsavel).size() > 0) {
				Responsavel responsavel = responsavelDao.findByFilter("codigo", this.codigoResponsavel).get(0);

				this.objetoContratoCobranca.setResponsavel(responsavel);
				
				if( CommonsUtil.mesmoValor(responsavel.getId(), CommonsUtil.longValue("46") ) ) {
					this.objetoContratoCobranca.setContratoLead(true);
				} else if(CommonsUtil.mesmoValor(this.objetoContratoCobranca.isContratoLead(), null)) {
					this.objetoContratoCobranca.setContratoLead(false);
				}

				if (this.objetoPagadorRecebedor.getSite() != null) {
					if (!this.objetoPagadorRecebedor.getSite().contains("http")
							&& !this.objetoPagadorRecebedor.getSite().contains("HTTP")) {
						this.objetoPagadorRecebedor
								.setSite("http://" + this.objetoPagadorRecebedor.getSite().toLowerCase());
					}
				}

				PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
				pagadorRecebedorDao.merge(this.objetoPagadorRecebedor);
				ImovelCobrancaDao imovelCobrancaDao = new ImovelCobrancaDao();
				imovelCobrancaDao.merge(this.objetoImovelCobranca);

				this.objetoContratoCobranca.setPagador(objetoPagadorRecebedor);
				this.objetoContratoCobranca.setImovel(objetoImovelCobranca);

				if (this.qtdeParcelas != null && !this.qtdeParcelas.equals("")) {
					this.objetoContratoCobranca.setQtdeParcelas(Integer.valueOf(this.qtdeParcelas));
				}

				if (this.objetoContratoCobranca.getVlrParcela() != null) {
					BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();

					this.objetoContratoCobranca.setVlrParcelaStr(
							bigDecimalConverter.getAsString(null, null, this.objetoContratoCobranca.getVlrParcela()));
				}

				// gerando parcelas quando contrato esta em ag registro
				if (this.objetoContratoCobranca.getAgAssinaturaData() != null
						&& this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() <= 0
						&& !CommonsUtil.semValor(this.objetoContratoCobranca.getValorCCB())) {
					geraContratoCobrancaDetalhes(contratoCobrancaDao);
				}
				
				if (!CommonsUtil.semValor(this.objetoAnaliseComite.getVotoAnaliseComite())
						|| CommonsUtil.mesmoValor(this.objetoAnaliseComite.getVotoAnaliseComite(), "")) {
					this.objetoAnaliseComite.setDataComite(gerarDataHoje());
					this.objetoAnaliseComite.setUsuarioComite(getNomeUsuarioLogado());
					this.objetoContratoCobranca.getListaAnaliseComite().add(this.objetoAnaliseComite);
					this.objetoAnaliseComite = new AnaliseComite();
					
						this.objetoContratoCobranca.setQtdeVotosAprovadosComite(BigInteger.ZERO);
						this.objetoContratoCobranca.setQtdeVotosReprovadosComite(BigInteger.ZERO);

					if(!this.objetoContratoCobranca.getListaAnaliseComite().isEmpty()) {
						for (AnaliseComite comite : this.objetoContratoCobranca.getListaAnaliseComite()) {
							if(CommonsUtil.mesmoValor(comite.getVotoAnaliseComite(), "Aprovado")) {
								this.objetoContratoCobranca.setQtdeVotosAprovadosComite(this.objetoContratoCobranca.getQtdeVotosAprovadosComite().add(BigInteger.ONE));
								if(CommonsUtil.mesmoValor(this.objetoContratoCobranca.getQtdeVotosAprovadosComite(), BigInteger.valueOf(2))) {
									this.objetoContratoCobranca.setAprovadoComite(true);
								}
							} else if(CommonsUtil.mesmoValor(comite.getVotoAnaliseComite(), "Reprovado")) {
								this.objetoContratoCobranca.setQtdeVotosReprovadosComite(this.objetoContratoCobranca.getQtdeVotosReprovadosComite().add(BigInteger.ONE));
							} 
						}
					} 
				}

				updateCheckList();

				contratoCobrancaDao.merge(this.objetoContratoCobranca);

				// verifica se o contrato for aprovado, manda um tipo de email..
				// senao valida se houve alteração no checklist para envio de email.

				enviaEmailAtualizacaoPreContrato();	
				
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Contrato Cobrança: Pré-Contrato editado com sucesso! (Contrato: "
										+ this.objetoContratoCobranca.getNumeroContrato() + ")!",
								""));

				if (this.tituloTelaConsultaPreStatus.equals("Aguardando Análise")) {
					return geraConsultaContratosPorStatus("Aguardando Análise");
				}
				if (this.tituloTelaConsultaPreStatus.equals("Análise Reprovada")) {
					return geraConsultaContratosPorStatus("Análise Reprovada");
				}
				if (this.tituloTelaConsultaPreStatus.equals("Em Análise")) {
					return geraConsultaContratosPorStatus("Em Analise");
				}
				if (this.tituloTelaConsultaPreStatus.equals("Ag. Pagto. Laudo")) {
					return geraConsultaContratosPorStatus("Ag. Pagto. Laudo");
				}
				if (this.tituloTelaConsultaPreStatus.equals("Ag. DOC")) {
					return geraConsultaContratosPorStatus("Ag. DOC");
				}
				if (this.tituloTelaConsultaPreStatus.equals("Pré-Comite")) {
					return geraConsultaContratosPorStatus("Pré-Comite");
				}
				if (this.tituloTelaConsultaPreStatus.equals("Ag. Comite")) {
					return geraConsultaContratosPorStatus("Ag. Comite");
				}
				if (this.tituloTelaConsultaPreStatus.equals("Ag. CCB")) {
					return geraConsultaContratosPorStatus("Ag. CCB");
				}
				if (this.tituloTelaConsultaPreStatus.equals("Ag. Assinatura")) {
					return geraConsultaContratosPorStatus("Ag. Assinatura");
				}
				if (this.tituloTelaConsultaPreStatus.equals("Ag. Registro")) {
					return geraConsultaContratosPorStatus("Ag. Registro");
				}
				if (this.tituloTelaConsultaPreStatus.equals("PreContratos")) {
					return geraConsultaContratosPendentes();
				}

				return "/Atendimento/Cobranca/ContratoCobrancaConsultarPreStatus.xhtml";

			} else {
				if (context != null) {
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Contrato Cobrança: Erro de validação: O código do responsável digitado não foi encontrado ("
									+ this.codigoResponsavel + ")!",
							""));
				}

				return "";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contrato Cobrança: " + e, ""));
			return "";
		}
	}
	
	public void notificaStatusWhatsApp(long idContrato) {
		
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		ContratoCobrancaStatus statusContrato = cDao.consultaStatusContratos(idContrato);
		
		// 1 - Verifica se teve alteração de status
		// 2 - Se sim, e o valor for true envia mensagem
		
		// Mensagem AG ASSINATURA
		if (this.objetoContratoCobranca.isCcbPronta() != statusContrato.isCcbPronta()) {
			if (this.objetoContratoCobranca.isCcbPronta()) {
				TakeBlipMB takeBlipMB = new TakeBlipMB();
				takeBlipMB.sendWhatsAppMessage(this.objetoContratoCobranca.getResponsavel(),
				"contrato_pronto_para_assinatura_operacao",
				this.objetoContratoCobranca.getPagador().getNome(),
				this.objetoContratoCobranca.getNumeroContrato(), "", "");
				
				// envia para o gerente do responsável
				if (this.objetoContratoCobranca.getResponsavel().getDonoResponsavel() != null) {
					takeBlipMB = new TakeBlipMB();
					takeBlipMB.sendWhatsAppMessage(this.objetoContratoCobranca.getResponsavel().getDonoResponsavel(),
					"contrato_pronto_para_assinatura_operacao",
					this.objetoContratoCobranca.getPagador().getNome(),
					this.objetoContratoCobranca.getNumeroContrato(), "", "");
				}
			}
		}
	
		// Mensagem ASSINADO / AG REGISTRO
		if (this.objetoContratoCobranca.isAgAssinatura() != statusContrato.isAgAssinatura()) {
			if (!this.objetoContratoCobranca.isAgAssinatura()) {
				if (statusContrato.isAgRegistro()) {
					TakeBlipMB takeBlipMB = new TakeBlipMB();
					takeBlipMB.sendWhatsAppMessage(this.objetoContratoCobranca.getResponsavel(),
					"contrato_dado_entrada_cartorio",
					this.objetoContratoCobranca.getPagador().getNome(),
					this.objetoContratoCobranca.getNumeroContrato(), "", "");
					
					// envia para o gerente do responsável
					if (this.objetoContratoCobranca.getResponsavel().getDonoResponsavel() != null) {
						takeBlipMB = new TakeBlipMB();
						takeBlipMB.sendWhatsAppMessage(this.objetoContratoCobranca.getResponsavel().getDonoResponsavel(),
						"contrato_dado_entrada_cartorio",
						this.objetoContratoCobranca.getPagador().getNome(),
						this.objetoContratoCobranca.getNumeroContrato(), "", "");
					}
				}				
			}
		}
			
		// Mensagem PAJU E LAUDO RECEBIDO
		if (this.objetoContratoCobranca.isPajurFavoravel() != statusContrato.isPajuFavoravel() ||
				this.objetoContratoCobranca.isLaudoRecebido() != statusContrato.isLaudoRecebido()) {
			if (this.objetoContratoCobranca.isPajurFavoravel() && this.objetoContratoCobranca.isLaudoRecebido()) {
				TakeBlipMB takeBlipMB = new TakeBlipMB();
				takeBlipMB.sendWhatsAppMessage(this.objetoContratoCobranca.getResponsavel(),
				"contrato_recebido_laudo_paju",
				this.objetoContratoCobranca.getPagador().getNome(),
				this.objetoContratoCobranca.getNumeroContrato(), "", "");
				
				// envia para o gerente do responsável
				if (this.objetoContratoCobranca.getResponsavel().getDonoResponsavel() != null) {
					takeBlipMB = new TakeBlipMB();
					takeBlipMB.sendWhatsAppMessage(this.objetoContratoCobranca.getResponsavel().getDonoResponsavel(),
					"contrato_recebido_laudo_paju",
					this.objetoContratoCobranca.getPagador().getNome(),
					this.objetoContratoCobranca.getNumeroContrato(), "", "");
				}
			}
		}
		
		// Mensagem PRE APROVADO COMITE
		if (this.objetoContratoCobranca.isDocumentosComite() != statusContrato.isDocumentosComite()) {
			if (this.objetoContratoCobranca.isDocumentosComite()) {
				TakeBlipMB takeBlipMB = new TakeBlipMB();
				
				ResponsavelDao rDao = new ResponsavelDao();
				Responsavel rComite1 = new Responsavel();
				Responsavel rComite2 = new Responsavel();
				Responsavel rComite3 = new Responsavel();
				
				// Fabricio
				rComite1 = rDao.findById((long) 4);
				
				takeBlipMB.sendWhatsAppMessage(rComite1,
				"contrato_comite","",
				this.objetoContratoCobranca.getNumeroContrato(),
				"", "");
				
				// João
				rComite2 = rDao.findById((long) 380);

				takeBlipMB.sendWhatsAppMessage(rComite2,
				"contrato_comite","",
				this.objetoContratoCobranca.getNumeroContrato(),
				"", "");
				
				// Sandro
				rComite3 = rDao.findById((long) 2);
				
				takeBlipMB.sendWhatsAppMessage(rComite3,
				"contrato_comite","",
				this.objetoContratoCobranca.getNumeroContrato(),
				"", "");
			}			
		}
		
		// Mensagem CONTRATO PRE APROVADO
		if (!this.objetoContratoCobranca.getCadastroAprovadoValor().equals(statusContrato.getContratoPreAprovado())) {
			if (!CommonsUtil.semValor(this.objetoContratoCobranca.getCadastroAprovadoValor())) {
				if (this.objetoContratoCobranca.getCadastroAprovadoValor().equals("Aprovado") &&
						this.objetoContratoCobranca.getTaxaPreAprovada() != null &&
						this.objetoContratoCobranca.getPrazoMaxPreAprovado() != null) {
					// if (this.objetoContratoCobranca.getTaxaPreAprovada() != null &&
					// this.objetoContratoCobranca.getPrazoMaxPreAprovado() != null) {
					TakeBlipMB takeBlipMB = new TakeBlipMB();
					takeBlipMB.sendWhatsAppMessage(this.objetoContratoCobranca.getResponsavel(),
							"contrato_pre_aprovado", 
							this.objetoContratoCobranca.getPagador().getNome(),
							this.objetoContratoCobranca.getNumeroContrato(),
							this.objetoContratoCobranca.getTaxaPreAprovada().toString(),
							this.objetoContratoCobranca.getPrazoMaxPreAprovado().toString());
					// }
					
					// envia para o gerente do responsável
					if (this.objetoContratoCobranca.getResponsavel().getDonoResponsavel() != null) {
						takeBlipMB = new TakeBlipMB();
						takeBlipMB.sendWhatsAppMessage(this.objetoContratoCobranca.getResponsavel().getDonoResponsavel(),
						"contrato_pre_aprovado", 
						this.objetoContratoCobranca.getPagador().getNome(),
						this.objetoContratoCobranca.getNumeroContrato(),
						this.objetoContratoCobranca.getTaxaPreAprovada().toString(),
						this.objetoContratoCobranca.getPrazoMaxPreAprovado().toString());
					}
				}
			}
		}
	}
	
	public String cancelarEdicaoPreContrato() {
		if (!this.preContratoCustom) {
			return geraConsultaContratosPendentes();
		} else {
			if (this.objetoContratoCobranca.getStatusLead().equals("Novo Lead")) {
				return geraConsultaLeads("Novo Lead");
			}
			if (this.objetoContratoCobranca.getStatusLead().equals("Em Tratamento")) {
				return geraConsultaLeads("Em Tratamento");
			}
			if (this.objetoContratoCobranca.getStatusLead().equals("Completo")) {
				return geraConsultaLeads("Completo");
			}
			if (this.objetoContratoCobranca.getStatusLead().equals("Reprovado")) {
				return geraConsultaLeads("Reprovado");
			}
			if (this.objetoContratoCobranca.getStatusLead().equals("Baixado")) {
				return geraConsultaLeads("Baixado");
			}

			return "";
		}
	}

	public boolean validaAlteracaoCheckList() {
		boolean retorno = false;

		if (this.objetoContratoCobranca != null && this.contratoCobrancaCheckList != null) {
			if (this.objetoContratoCobranca.isInicioAnalise() != this.contratoCobrancaCheckList.isInicioAnalise()) {
				retorno = true;
			}

			if (this.objetoContratoCobranca.getCadastroAprovadoValor() != null
					&& this.contratoCobrancaCheckList.getCadastroAprovadoValor() != null) {
				if (!this.objetoContratoCobranca.getCadastroAprovadoValor()
						.equals(this.contratoCobrancaCheckList.getCadastroAprovadoValor())) {
					retorno = true;
				}
			}

			if (this.objetoContratoCobranca.getMatriculaAprovadaValor() != null
					&& this.contratoCobrancaCheckList.getMatriculaAprovadaValor() != null) {
				if (!this.objetoContratoCobranca.getMatriculaAprovadaValor()
						.equals(this.contratoCobrancaCheckList.getMatriculaAprovadaValor())) {
					retorno = true;
				}
			}

			if (this.objetoContratoCobranca.isPagtoLaudoConfirmada() != this.contratoCobrancaCheckList
					.isPagtoLaudoConfirmada()) {
				retorno = true;
			}

			if (this.objetoContratoCobranca.isLaudoRecebido() != this.contratoCobrancaCheckList.isLaudoRecebido()) {
				retorno = true;
			}

			if (this.objetoContratoCobranca.isPajurFavoravel() != this.contratoCobrancaCheckList.isPajurFavoravel()) {
				retorno = true;
			}

			if (this.objetoContratoCobranca.isDocumentosCompletos() != this.contratoCobrancaCheckList
					.isDocumentosCompletos()) {
				retorno = true;
			}

			if (this.objetoContratoCobranca.isCcbPronta() != this.contratoCobrancaCheckList.isCcbPronta()) {
				retorno = true;
			}

			if (this.objetoContratoCobranca.getStatusContrato() != null
					&& this.contratoCobrancaCheckList.getStatusContrato() != null) {
				if (!this.objetoContratoCobranca.getStatusContrato()
						.equals(this.contratoCobrancaCheckList.getStatusContrato())) {
					retorno = true;
				}
			}
		}

		return retorno;
	}

	/*******
	 * Atualiza o check list com data e o usuário que setou se false limpa usuario e
	 * data se true verificar se data e usuario nulo preeencher dados
	 * 
	 * senão nada
	 * 
	 * @return
	 */
	public void updateCheckList() {

		if (this.objetoContratoCobranca.getStatusLead() != null) {
			if (this.objetoContratoCobranca.getStatusLead().equals("Em Tratamento")) {
				Responsavel responsavel = getResponsavelUsuarioLogado();
				this.objetoContratoCobranca.setLeadCompleto(false);
				if (responsavel != null) {
					this.objetoContratoCobranca.setResponsavel(responsavel);
				}
				
			} 
		} else {
			this.objetoContratoCobranca.setStatusLead("Completo");
		}
		
		if(this.objetoContratoCobranca.getStatusLead().equals("Completo")) {
			if (this.objetoContratoCobranca.getContratoResgatadoData() == null) {
				this.objetoContratoCobranca.setContratoResgatadoData(gerarDataHoje());
				this.objetoContratoCobranca.setContratoResgatadoBaixar(true);
			}
			this.objetoContratoCobranca.setLeadCompleto(true);
			if (this.objetoContratoCobranca.getLeadCompletoData() == null) {
				this.objetoContratoCobranca.setLeadCompletoData(gerarDataHoje());
				this.objetoContratoCobranca.setLeadCompletoUsuario(getNomeUsuarioLogado());
			}
		}

		if (!this.objetoContratoCobranca.isInicioAnalise()) {
			this.objetoContratoCobranca.setInicioAnaliseData(null);
			this.objetoContratoCobranca.setInicioAnaliseUsuario(null);
			this.objetoContratoCobranca.setDataUltimaAtualizacao(this.objetoContratoCobranca.getDataContrato());

		} else {
			if (this.objetoContratoCobranca.getInicioAnaliseData() == null) {
				this.objetoContratoCobranca.setStatus("Pendente");
				this.objetoContratoCobranca.setInicioAnaliseData(gerarDataHoje());
				this.objetoContratoCobranca.setDataUltimaAtualizacao(this.objetoContratoCobranca.getInicioAnaliseData());
				this.objetoContratoCobranca.setInicioAnaliseUsuario(getNomeUsuarioLogado());
			}
		}
		
		if (!this.objetoContratoCobranca.isAnaliseReprovada()) {
			this.objetoContratoCobranca.setAnaliseReprovadaData(null);
			this.objetoContratoCobranca.setAnaliseReprovadaUsuario(null);

		} else {
			if (this.objetoContratoCobranca.getAnaliseReprovadaData() == null) {
				this.objetoContratoCobranca.setStatus("Reprovado");
				this.objetoContratoCobranca.setAnaliseReprovadaData(gerarDataHoje());
				this.objetoContratoCobranca.setDataUltimaAtualizacao(this.objetoContratoCobranca.getAnaliseReprovadaData());
				this.objetoContratoCobranca.setAnaliseReprovadaUsuario(getNomeUsuarioLogado());
			}
		}

		if (this.objetoContratoCobranca.getCadastroAprovadoValor() != null) {
			if (this.objetoContratoCobranca.getCadastroAprovadoValor().equals("")) {
				this.objetoContratoCobranca.setCadastroAprovadoData(null);
				this.objetoContratoCobranca.setCadastroAprovadoUsuario(null);
			} else {
				if (this.objetoContratoCobranca.getCadastroAprovadoValor().equals("Aprovado") || this.objetoContratoCobranca.getCadastroAprovadoValor().equals("Pendente") ) {
					if (this.objetoContratoCobranca.getCadastroAprovadoData() == null) {
						this.objetoContratoCobranca.setStatus("Pendente");
						
						this.objetoContratoCobranca.setCadastroAprovadoData(gerarDataHoje());
						this.objetoContratoCobranca.setDataUltimaAtualizacao(this.objetoContratoCobranca.getCadastroAprovadoData());
						this.objetoContratoCobranca.setCadastroAprovadoUsuario(getNomeUsuarioLogado());
					}
				} else {
					this.objetoContratoCobranca.setStatus("Reprovado");
					this.objetoContratoCobranca.setAnaliseReprovada(true);
					this.objetoContratoCobranca.setAnaliseReprovadaData(gerarDataHoje());
					this.objetoContratoCobranca.setDataUltimaAtualizacao(this.objetoContratoCobranca.getAnaliseReprovadaData());
					this.objetoContratoCobranca.setAnaliseReprovadaUsuario(getNomeUsuarioLogado());
				}
			}
		}

		if (this.objetoContratoCobranca.getMatriculaAprovadaValor() != null) {
			if (this.objetoContratoCobranca.getMatriculaAprovadaValor().equals("")) {
				this.objetoContratoCobranca.setMatriculaAprovadaData(null);
				this.objetoContratoCobranca.setMatriculaAprovadaUsuario(null);

			} else {
				if (this.objetoContratoCobranca.getMatriculaAprovadaData() == null) {
					this.objetoContratoCobranca.setStatus("Pendente");
					this.objetoContratoCobranca.setMatriculaAprovadaData(gerarDataHoje());
					this.objetoContratoCobranca.setDataUltimaAtualizacao(this.objetoContratoCobranca.getMatriculaAprovadaData());
					this.objetoContratoCobranca.setMatriculaAprovadaUsuario(getNomeUsuarioLogado());
				}
			}
		}

		if (!this.objetoContratoCobranca.isPagtoLaudoConfirmada()) {
			this.objetoContratoCobranca.setPagtoLaudoConfirmadaData(null);
			this.objetoContratoCobranca.setPagtoLaudoConfirmadaUsuario(null);

		} else {
			if (this.objetoContratoCobranca.getPagtoLaudoConfirmadaData() == null) {
				this.objetoContratoCobranca.setStatus("Pendente");
				this.objetoContratoCobranca.setPagtoLaudoConfirmadaData(gerarDataHoje());
				this.objetoContratoCobranca.setDataUltimaAtualizacao(this.objetoContratoCobranca.getPagtoLaudoConfirmadaData());
				this.objetoContratoCobranca.setPagtoLaudoConfirmadaUsuario(getNomeUsuarioLogado());
			}
		}

		if (!this.objetoContratoCobranca.isLaudoRecebido()) {
			this.objetoContratoCobranca.setLaudoRecebidoData(null);
			this.objetoContratoCobranca.setLaudoRecebidoUsuario(null);

		} else {
			if (this.objetoContratoCobranca.getLaudoRecebidoData() == null) {
				this.objetoContratoCobranca.setStatus("Pendente");
				this.objetoContratoCobranca.setLaudoRecebidoData(gerarDataHoje());
				this.objetoContratoCobranca.setDataUltimaAtualizacao(this.objetoContratoCobranca.getLaudoRecebidoData());
				this.objetoContratoCobranca.setLaudoRecebidoUsuario(getNomeUsuarioLogado());
			}
		}

		if (!this.objetoContratoCobranca.isPajurFavoravel()) {
			this.objetoContratoCobranca.setPajurFavoravelData(null);
			this.objetoContratoCobranca.setPajurFavoravelUsuario(null);

		} else {
			if (this.objetoContratoCobranca.getPajurFavoravelData() == null) {
				this.objetoContratoCobranca.setStatus("Pendente");
				this.objetoContratoCobranca.setPajurFavoravelData(gerarDataHoje());
				this.objetoContratoCobranca.setDataUltimaAtualizacao(this.objetoContratoCobranca.getPajurFavoravelData());
				this.objetoContratoCobranca.setPajurFavoravelUsuario(getNomeUsuarioLogado());
			}
		}
		
		if (!this.objetoContratoCobranca.isPreAprovadoComite()) {
			this.objetoContratoCobranca.setPreAprovadoComiteData(null);
			this.objetoContratoCobranca.setPreAprovadoComiteUsuario(null);
		} else {
			if (this.objetoContratoCobranca.getPreAprovadoComiteData() == null) {
				this.objetoContratoCobranca.setStatus("Pendente");
				this.objetoContratoCobranca.setPreAprovadoComiteData(gerarDataHoje());
				this.objetoContratoCobranca.setDataUltimaAtualizacao(this.objetoContratoCobranca.getPreAprovadoComiteData());
				this.objetoContratoCobranca.setPreAprovadoComiteUsuario(getNomeUsuarioLogado());
			}
		}
		
		if (!this.objetoContratoCobranca.isDocumentosComite()) {
			this.objetoContratoCobranca.setDocumentosComiteData(null);
			this.objetoContratoCobranca.setDocumentosComiteUsuario(null);

		} else {
			if (this.objetoContratoCobranca.getDocumentosComiteData() == null) {
				this.objetoContratoCobranca.setStatus("Pendente");
				this.objetoContratoCobranca.setDocumentosComiteData(gerarDataHoje());
				this.objetoContratoCobranca.setDataUltimaAtualizacao(this.objetoContratoCobranca.getDocumentosComiteData());
				this.objetoContratoCobranca.setDocumentosComiteUsuario(getNomeUsuarioLogado());
			}
		}
		
		if (!this.objetoContratoCobranca.isAprovadoComite()) {
			this.objetoContratoCobranca.setAprovadoComiteData(null);
			this.objetoContratoCobranca.setAprovadoComiteUsuario(null);

		} else {
			if (this.objetoContratoCobranca.getAprovadoComiteData() == null) {
				this.objetoContratoCobranca.setStatus("Pendente");
				this.objetoContratoCobranca.setAprovadoComiteData(gerarDataHoje());
				this.objetoContratoCobranca.setDataUltimaAtualizacao(this.objetoContratoCobranca.getAprovadoComiteData());
				concluirComite(this.objetoContratoCobranca);
				
				this.objetoContratoCobranca.setAprovadoComiteUsuario(getNomeUsuarioLogado());
			}
		}

		if (!this.objetoContratoCobranca.isDocumentosCompletos()) {
			this.objetoContratoCobranca.setDocumentosCompletosData(null);
			this.objetoContratoCobranca.setDocumentosCompletosUsuario(null);

		} else {
			if (this.objetoContratoCobranca.getDocumentosCompletosData() == null) {
				this.objetoContratoCobranca.setStatus("Pendente");
				this.objetoContratoCobranca.setDocumentosCompletosData(gerarDataHoje());
				this.objetoContratoCobranca.setDataUltimaAtualizacao(this.objetoContratoCobranca.getDocumentosCompletosData());
				this.objetoContratoCobranca.setDocumentosCompletosUsuario(getNomeUsuarioLogado());
			}
		}

		if (!this.objetoContratoCobranca.isCcbPronta()) {
			this.objetoContratoCobranca.setCcbProntaData(null);
			this.objetoContratoCobranca.setCcbProntaUsuario(null);

		} else {
			if (this.objetoContratoCobranca.getCcbProntaData() == null) {
				this.objetoContratoCobranca.setStatus("Pendente");
				this.objetoContratoCobranca.setCcbProntaData(gerarDataHoje());
				this.objetoContratoCobranca.setDataUltimaAtualizacao(this.objetoContratoCobranca.getCcbProntaData());
				this.objetoContratoCobranca.setCcbProntaUsuario(getNomeUsuarioLogado());
			}
		}

		if (this.objetoContratoCobranca.isAgAssinatura()) {
			this.objetoContratoCobranca.setAgAssinaturaData(null);
			this.objetoContratoCobranca.setAgAssinaturaUsuario(null);
		} else {
			if (this.objetoContratoCobranca.getAgAssinaturaData() == null) {
				this.objetoContratoCobranca.setStatus("Pendente");
				this.objetoContratoCobranca.setAgAssinaturaData(gerarDataHoje());
				this.objetoContratoCobranca.setDataUltimaAtualizacao(this.objetoContratoCobranca.getAgAssinaturaData());
				this.objetoContratoCobranca.setAgAssinaturaUsuario(getNomeUsuarioLogado());
			}
		}

		if (this.objetoContratoCobranca.isAgRegistro()) {
			this.objetoContratoCobranca.setAgRegistroData(null);
			this.objetoContratoCobranca.setAgRegistroUsuario(null);
		} else {
			if (this.objetoContratoCobranca.getAgRegistroData() == null) {
				this.objetoContratoCobranca.setStatus("Pendente");
				this.objetoContratoCobranca.setAgRegistroData(gerarDataHoje());
				this.objetoContratoCobranca.setDataUltimaAtualizacao(this.objetoContratoCobranca.getAgRegistroData());
				this.objetoContratoCobranca.setAgRegistroUsuario(getNomeUsuarioLogado());
			
				this.objetoContratoCobranca.setStatusContrato("Aprovado");				
			}
		}
		
		this.objetoContratoCobranca.setStatusContratoData(gerarDataHoje());
		this.objetoContratoCobranca.setStatusContratoUsuario(getNomeUsuarioLogado());
		this.objetoContratoCobranca.setStatus("Pendente");

		if (this.objetoContratoCobranca.getStatusContrato().equals("Baixado")) {
			this.objetoContratoCobranca.setStatus("Baixado");
			this.objetoContratoCobranca.setReprovado(false);
			this.objetoContratoCobranca.setAprovado(false);
			this.objetoContratoCobranca.setReprovadoData(gerarDataHoje());
			this.objetoContratoCobranca.setReprovadoUsuario(getNomeUsuarioLogado());
		}
		
		if (!this.objetoContratoCobranca.getStatusContrato().equals("Em Análise")) {
			if (this.objetoContratoCobranca.getStatusContrato().equals("Aprovado")) {
				this.objetoContratoCobranca.setStatus("Aprovado");
				this.objetoContratoCobranca.setAprovado(true);
				this.objetoContratoCobranca.setAprovadoData(gerarDataHoje());
				this.objetoContratoCobranca.setAprovadoUsuario(getNomeUsuarioLogado());
			} else {
				if (this.objetoContratoCobranca.getStatusContrato().equals("Reprovado")) {
					this.objetoContratoCobranca.setStatus("Reprovado");
					this.objetoContratoCobranca.setReprovado(true);
					this.objetoContratoCobranca.setReprovadoData(gerarDataHoje());
					this.objetoContratoCobranca.setReprovadoUsuario(getNomeUsuarioLogado());
				} else {
					this.objetoContratoCobranca.setAprovado(false);
					this.objetoContratoCobranca.setAprovadoData(null);
					this.objetoContratoCobranca.setAprovadoUsuario("");
				}
			}
		}
	}
	
	public String voltarContratoParaComite() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		FacesContext context = FacesContext.getCurrentInstance();
		this.objetoContratoCobranca.setAprovadoComite(false);
		this.objetoContratoCobranca.setDocumentosCompletos(false);
		this.objetoContratoCobranca.setCcbPronta(false);
		if (!objetoContratoCobranca.getListaAnaliseComite().isEmpty()) {
			for (AnaliseComite comite : objetoContratoCobranca.getListaAnaliseComite()) {
				comite.setVotoAnaliseComite("Reprovado");
			}
		}
		updateCheckList();
		contratoCobrancaDao.merge(this.objetoContratoCobranca);
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Contrato Cobrança: Pré-Contrato editado com sucesso! (Contrato: "
								+ this.objetoContratoCobranca.getNumeroContrato() + ")!",
						""));
		return geraConsultaContratosPorStatus("Ag. Comite");
	}
	
	public String voltarContratoParaDocumentosComite() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		FacesContext context = FacesContext.getCurrentInstance();
		this.objetoContratoCobranca.setDocumentosComite(false);
		updateCheckList();
		contratoCobrancaDao.merge(this.objetoContratoCobranca);
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Contrato Cobrança: Pré-Contrato editado com sucesso! (Contrato: "
								+ this.objetoContratoCobranca.getNumeroContrato() + ")!",
						""));
		return geraConsultaContratosPorStatus("Ag. Comite");
	}
	
	public String voltarContratoParaPreComite() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		FacesContext context = FacesContext.getCurrentInstance();
		this.objetoContratoCobranca.setPreAprovadoComite(false);
		updateCheckList();
		contratoCobrancaDao.merge(this.objetoContratoCobranca);
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Contrato Cobrança: Pré-Contrato editado com sucesso! (Contrato: "
								+ this.objetoContratoCobranca.getNumeroContrato() + ")!",
						""));
		return geraConsultaContratosPorStatus("Ag. Comite");
	}

	public void geraContasPagarRemuneracao(ContratoCobranca contrato) {
		ResponsavelDao rDao = new ResponsavelDao();
		Responsavel responsavel = new Responsavel();

		if (contrato.getResponsavel() != null) {
			// nivel 1 - pega responsavel contrato
			responsavel = contrato.getResponsavel();
			geraContasPagarRemuneracaoResponsavel(contrato, responsavel);

			if (responsavel.getDonoResponsavel() != null) {
				// nivel 2 - pega responsavel hierarquico
				responsavel = responsavel.getDonoResponsavel();
				geraContasPagarRemuneracaoResponsavel(contrato, responsavel);

				if (responsavel.getDonoResponsavel() != null) {
					// nivel 3 - pega responsavel hierarquico
					responsavel = responsavel.getDonoResponsavel();
					geraContasPagarRemuneracaoResponsavel(contrato, responsavel);

					if (responsavel.getDonoResponsavel() != null) {
						// nivel 4 - pega responsavel hierarquico
						responsavel = responsavel.getDonoResponsavel();
						geraContasPagarRemuneracaoResponsavel(contrato, responsavel);

						if (responsavel.getDonoResponsavel() != null) {
							// nivel 5 - pega responsavel hierarquico
							responsavel = responsavel.getDonoResponsavel();
							geraContasPagarRemuneracaoResponsavel(contrato, responsavel);

							if (responsavel.getDonoResponsavel() != null) {
								// nivel 6 - pega responsavel hierarquico
								responsavel = responsavel.getDonoResponsavel();
								geraContasPagarRemuneracaoResponsavel(contrato, responsavel);

								if (responsavel.getDonoResponsavel() != null) {
									// nivel 7 - pega responsavel hierarquico
									responsavel = responsavel.getDonoResponsavel();
									geraContasPagarRemuneracaoResponsavel(contrato, responsavel);

									if (responsavel.getDonoResponsavel() != null) {
										// nivel 8 - pega responsavel hierarquico
										responsavel = responsavel.getDonoResponsavel();
										geraContasPagarRemuneracaoResponsavel(contrato, responsavel);

										if (responsavel.getDonoResponsavel() != null) {
											// nivel 9 - pega responsavel hierarquico
											responsavel = responsavel.getDonoResponsavel();
											geraContasPagarRemuneracaoResponsavel(contrato, responsavel);

											if (responsavel.getDonoResponsavel() != null) {
												// nivel 10 - pega responsavel hierarquico
												responsavel = responsavel.getDonoResponsavel();
												geraContasPagarRemuneracaoResponsavel(contrato, responsavel);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

	}

	public void geraContasPagarRemuneracaoResponsavel(ContratoCobranca contrato, Responsavel responsavel) {
		BigDecimal valorPagar = BigDecimal.ZERO;
		BigDecimal taxaRemuneracao = BigDecimal.ZERO;

		if (responsavel.getTaxaRemuneracao() != null && contrato.getValorCCB() != null) {
			if (responsavel.getTaxaRemuneracao().compareTo(BigDecimal.ZERO) > 0) {
				taxaRemuneracao = responsavel.getTaxaRemuneracao().divide(BigDecimal.valueOf(100));
				valorPagar = contrato.getValorCCB().multiply(taxaRemuneracao);

				ContasPagarDao cDao = new ContasPagarDao();
				ContasPagar contaPagar = new ContasPagar();
				contaPagar.setTipoDespesa("E");
				contaPagar.setResponsavel(responsavel);
				contaPagar.setDataVencimento(getDataComMais15Dias(gerarDataHoje()));
				contaPagar.setNumeroDocumento(contrato.getNumeroContrato());
				contaPagar.setDescricao("Pagamento de remuneração por aprovação de contrato.");
				contaPagar.setValor(valorPagar);
				cDao.create(contaPagar);
			}
		}

	}

	public Date getDataComMais15Dias(Date dataOriginal) {
		Date dataRetorno = new Date();

		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");

		Calendar calendar = Calendar.getInstance(zone, locale);

		calendar.setTime(dataOriginal);
		calendar.add(Calendar.DAY_OF_MONTH, 15);
		// calendar.set(Calendar.DAY_OF_MONTH, 14);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		return calendar.getTime();
	}

	public User getUsuarioLogado() {
		User usuario = new User();
		if (loginBean != null) {
			List<User> usuarioLogado = new ArrayList<User>();
			UserDao u = new UserDao();

			usuarioLogado = u.findByFilter("login", loginBean.getUsername());

			if (usuarioLogado.size() > 0) {
				usuario = usuarioLogado.get(0);
			}
		}

		return usuario;
	}

	public Responsavel getResponsavelUsuarioLogado() {
		User usuario = new User();
		if (loginBean != null) {
			List<User> usuarioLogado = new ArrayList<User>();
			UserDao u = new UserDao();

			usuarioLogado = u.findByFilter("login", loginBean.getUsername());

			if (usuarioLogado.size() > 0) {
				usuario = usuarioLogado.get(0);
			}
		}

		List<Responsavel> responsavel = new ArrayList<Responsavel>();
		ResponsavelDao rDao = new ResponsavelDao();
		responsavel = rDao.findByFilter("codigo", usuario.getCodigoResponsavel());

		if (responsavel.size() > 0) {
			return responsavel.get(0);
		} else {
			return null;
		}
	}

	public String getNomeUsuarioLogado() {
		User usuario = getUsuarioLogado();

		if (usuario.getLogin() != null) {
			if (!usuario.getLogin().equals("")) {
				return usuario.getLogin();
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	public String deletePreContrato() {
		FacesContext context = FacesContext.getCurrentInstance();
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

		contratoCobrancaDao.delete(this.objetoContratoCobranca);

		deleteFiles(this.files);

		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Contrato Cobrança: Pré-Contrato excluído com sucesso! (Contrato: "
								+ this.objetoContratoCobranca.getNumeroContrato() + ")!",
						""));

		return geraConsultaContratosPendentes();
	}

	public void cancelContratoPublico() {
		this.objetoContratoCobranca = new ContratoCobranca();
		this.objetoContratoCobranca = new ContratoCobranca();

		// deleta arquivos caso tenha inserido
		this.deletefiles = (List<FileUploaded>) this.listaArquivos();
		this.deleteFile();
	}

	public void updateRender(int numero) {
		switch (numero) {
		case 2:
			if (!this.renderRecebedor2) {
				this.nomeRecebedor2 = null;
				this.idRecebedor2 = 0;
				this.selectedRecebedor2 = null;
			}
			break;
		case 3:
			if (!this.renderRecebedor3) {
				this.nomeRecebedor3 = null;
				this.idRecebedor3 = 0;
				this.selectedRecebedor3 = null;
			}
			break;
		case 4:
			if (!this.renderRecebedor4) {
				this.nomeRecebedor4 = null;
				this.idRecebedor4 = 0;
				this.selectedRecebedor4 = null;
			}
			break;
		case 5:
			if (!this.renderRecebedor5) {
				this.nomeRecebedor5 = null;
				this.idRecebedor5 = 0;
				this.selectedRecebedor5 = null;
			}
			break;
		case 6:
			if (!this.renderRecebedor6) {
				this.nomeRecebedor6 = null;
				this.idRecebedor6 = 0;
				this.selectedRecebedor6 = null;
			}
			break;
		case 7:
			if (!this.renderRecebedor7) {
				this.nomeRecebedor7 = null;
				this.idRecebedor7 = 0;
				this.selectedRecebedor7 = null;
			}
			break;
		case 8:
			if (!this.renderRecebedor8) {
				this.nomeRecebedor8 = null;
				this.idRecebedor8 = 0;
				this.selectedRecebedor8 = null;
			}
			break;
		case 9:
			if (!this.renderRecebedor9) {
				this.nomeRecebedor9 = null;
				this.idRecebedor9 = 0;
				this.selectedRecebedor9 = null;
			}
			break;
		case 10:
			if (!this.renderRecebedor10) {
				this.nomeRecebedor10 = null;
				this.idRecebedor10 = 0;
				this.selectedRecebedor10 = null;
			}
			break;
		default:
			this.renderRecebedor2 = false;
			this.renderRecebedor3 = false;
			this.renderRecebedor4 = false;
			this.renderRecebedor5 = false;
			this.renderRecebedor6 = false;
			this.renderRecebedor7 = false;
			this.renderRecebedor8 = false;
			this.renderRecebedor9 = false;
			this.renderRecebedor10 = false;
		}

	}

	public void updateRenderFinal(int numero) {
		switch (numero) {
		case 2:
			if (!this.renderRecebedorFinais) {
				this.nomeRecebedorFinal1 = null;
				this.idRecebedorFinal1 = 0;
				this.selectedRecebedorFinal1 = null;
				this.objetoContratoCobranca.setVlrFinalRecebedor1(null);

				this.nomeRecebedorFinal2 = null;
				this.idRecebedorFinal2 = 0;
				this.selectedRecebedorFinal2 = null;
				this.objetoContratoCobranca.setVlrFinalRecebedor2(null);

				this.nomeRecebedorFinal3 = null;
				this.idRecebedorFinal3 = 0;
				this.selectedRecebedorFinal3 = null;
				this.objetoContratoCobranca.setVlrFinalRecebedor3(null);

				this.nomeRecebedorFinal4 = null;
				this.idRecebedorFinal4 = 0;
				this.selectedRecebedorFinal4 = null;
				this.objetoContratoCobranca.setVlrFinalRecebedor4(null);

				this.nomeRecebedorFinal5 = null;
				this.idRecebedorFinal5 = 0;
				this.selectedRecebedorFinal5 = null;
				this.objetoContratoCobranca.setVlrFinalRecebedor5(null);

				this.nomeRecebedorFinal6 = null;
				this.idRecebedorFinal6 = 0;
				this.selectedRecebedorFinal6 = null;
				this.objetoContratoCobranca.setVlrFinalRecebedor6(null);

				this.nomeRecebedorFinal7 = null;
				this.idRecebedorFinal7 = 0;
				this.selectedRecebedorFinal7 = null;
				this.objetoContratoCobranca.setVlrFinalRecebedor7(null);

				this.nomeRecebedorFinal8 = null;
				this.idRecebedorFinal8 = 0;
				this.selectedRecebedorFinal8 = null;
				this.objetoContratoCobranca.setVlrFinalRecebedor8(null);

				this.nomeRecebedorFinal9 = null;
				this.idRecebedorFinal9 = 0;
				this.selectedRecebedorFinal9 = null;
				this.objetoContratoCobranca.setVlrFinalRecebedor9(null);

				this.nomeRecebedorFinal10 = null;
				this.idRecebedorFinal10 = 0;
				this.selectedRecebedorFinal10 = null;
				this.objetoContratoCobranca.setVlrFinalRecebedor10(null);
			}
			break;
		default:
		}

	}

	public void populateValorParcelaFinal() {
		this.vlrParcelaFinal = this.objetoContratoCobranca.getVlrInvestimento();
	}

	public void populateRetencaoParcela() {
		if (this.vlrRepasse != null && this.objetoContratoCobranca.getVlrParcela() != null) {
			this.vlrRetencao = this.objetoContratoCobranca.getVlrParcela().subtract(this.vlrRepasse);
		}
	}

	public void populateDataPagamentoFim() {
		if (Integer.valueOf(this.qtdeParcelas) > 0 && this.dataPagamentoFinalTmp != null) {
			ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
			this.objetoContratoCobranca.setDataPagamentoFim(contratoCobrancaDao
					.geraDataParcela(Integer.valueOf(this.qtdeParcelas) + 1, this.dataPagamentoFinalTmp));
		}
	}

	public void populateDataPagamentoInicio() {
		this.dataPagamentoFinalTmp = this.objetoContratoCobranca.getDataInicio();
	}

	public void populateRetencaoParcelaFinal() {
		if (this.vlrRepasseFinal != null && this.getVlrParcelaFinal() != null) {
			this.vlrRetencaoFinal = this.getVlrParcelaFinal().subtract(this.vlrRepasseFinal);
		}
	}

	public void populateTaxaAdministracao() {
		if (this.getObjetoContratoCobranca().getTxAdministracao() != null
				&& this.getObjetoContratoCobranca().getVlrInvestimento() != null) {
			this.vlrParcelaFinal = this.getObjetoContratoCobranca().getVlrInvestimento()
					.add(this.getObjetoContratoCobranca().getTxAdministracao());
		}
	}

	/************************************************************
	 * INICIO - Métodos utilizados pelas LoVs
	 ***********************************************************/
	public void loadLovs() {
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listPagadores = pagadorRecebedorDao.findAll();
		this.listRecebedores = pagadorRecebedorDao.findAll();

		ImovelCobrancaDao imovelCobrancaDao = new ImovelCobrancaDao();
		this.listImoveis = imovelCobrancaDao.findAll();

		ResponsavelDao responsavelDao = new ResponsavelDao();
		this.listResponsaveis = responsavelDao.findAll();
	}

	public final void populateSelectedGrupoFavorecido() {
		this.idGrupoFavorecido = this.selectedGrupoFavorecido.getId();
		this.nomeGrupoFavorecido = this.selectedGrupoFavorecido.getNomeGrupo();
	}

	public void clearGrupoFavorecido() {
		this.idGrupoFavorecido = 0;
		this.nomeGrupoFavorecido = null;
		this.selectedGrupoFavorecido = new GruposFavorecidos();
	}

	public final void populateSelectedPagador() {
		this.idPagador = this.selectedPagador.getId();
		this.nomePagador = this.selectedPagador.getNome();
		if (this.selectedPagador.getCpf() != null) {
			this.tipoPessoaIsFisica = true;
		} else {
			this.tipoPessoaIsFisica = false;
		}
	}

	public void clearPagador() {
		this.idPagador = 0;
		this.nomePagador = null;
		this.selectedPagador = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedorFinal1() {
		this.idRecebedorFinal1 = this.selectedRecebedorFinal1.getId();
		this.nomeRecebedorFinal1 = this.selectedRecebedorFinal1.getNome();
	}

	public void clearRecebedorFinal1() {
		this.idRecebedorFinal1 = 0;
		this.nomeRecebedorFinal1 = null;
		this.selectedRecebedorFinal1 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedorFinal2() {
		this.idRecebedorFinal2 = this.selectedRecebedorFinal2.getId();
		this.nomeRecebedorFinal2 = this.selectedRecebedorFinal2.getNome();
	}

	public void clearRecebedorFinal2() {
		this.idRecebedorFinal2 = 0;
		this.nomeRecebedorFinal2 = null;
		this.selectedRecebedorFinal2 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedorFinal3() {
		this.idRecebedorFinal3 = this.selectedRecebedorFinal3.getId();
		this.nomeRecebedorFinal3 = this.selectedRecebedorFinal3.getNome();
	}

	public void clearRecebedorFinal3() {
		this.idRecebedorFinal3 = 0;
		this.nomeRecebedorFinal3 = null;
		this.selectedRecebedorFinal3 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedorFinal4() {
		this.idRecebedorFinal4 = this.selectedRecebedorFinal4.getId();
		this.nomeRecebedorFinal4 = this.selectedRecebedorFinal4.getNome();
	}

	public void clearRecebedorFinal4() {
		this.idRecebedorFinal4 = 0;
		this.nomeRecebedorFinal4 = null;
		this.selectedRecebedorFinal4 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedorFinal5() {
		this.idRecebedorFinal5 = this.selectedRecebedorFinal5.getId();
		this.nomeRecebedorFinal5 = this.selectedRecebedorFinal5.getNome();
	}

	public void clearRecebedorFinal5() {
		this.idRecebedorFinal5 = 0;
		this.nomeRecebedorFinal5 = null;
		this.selectedRecebedorFinal5 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedorFinal6() {
		this.idRecebedorFinal6 = this.selectedRecebedorFinal6.getId();
		this.nomeRecebedorFinal6 = this.selectedRecebedorFinal6.getNome();
	}

	public void clearRecebedorFinal6() {
		this.idRecebedorFinal6 = 0;
		this.nomeRecebedorFinal6 = null;
		this.selectedRecebedorFinal6 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedorFinal7() {
		this.idRecebedorFinal7 = this.selectedRecebedorFinal7.getId();
		this.nomeRecebedorFinal7 = this.selectedRecebedorFinal7.getNome();
	}

	public void clearRecebedorFinal7() {
		this.idRecebedorFinal7 = 0;
		this.nomeRecebedorFinal7 = null;
		this.selectedRecebedorFinal7 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedorFinal8() {
		this.idRecebedorFinal8 = this.selectedRecebedorFinal8.getId();
		this.nomeRecebedorFinal8 = this.selectedRecebedorFinal8.getNome();
	}

	public void clearRecebedorFinal8() {
		this.idRecebedorFinal8 = 0;
		this.nomeRecebedorFinal8 = null;
		this.selectedRecebedorFinal8 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedorFinal9() {
		this.idRecebedorFinal9 = this.selectedRecebedorFinal9.getId();
		this.nomeRecebedorFinal9 = this.selectedRecebedorFinal9.getNome();
	}

	public void clearRecebedorFinal9() {
		this.idRecebedorFinal9 = 0;
		this.nomeRecebedorFinal9 = null;
		this.selectedRecebedorFinal9 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedorFinal10() {
		this.idRecebedorFinal10 = this.selectedRecebedorFinal10.getId();
		this.nomeRecebedorFinal10 = this.selectedRecebedorFinal10.getNome();
	}

	public void clearRecebedorFinal10() {
		this.idRecebedorFinal10 = 0;
		this.nomeRecebedorFinal10 = null;
		this.selectedRecebedorFinal10 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedor() {
		this.idRecebedor = this.selectedRecebedor.getId();
		this.nomeRecebedor = this.selectedRecebedor.getNome();
	}

	public final void populateSelectedRecebedorPreContrato() {
		this.idRecebedor = this.selectedRecebedor.getId();
		this.nomeRecebedor = this.selectedRecebedor.getNome();

		this.objetoPagadorRecebedor = this.selectedRecebedor;

		if (this.objetoPagadorRecebedor.getCnpj() != null && !this.objetoPagadorRecebedor.getCnpj().equals("")) {
			this.tipoPessoaIsFisica = false;
		} else {
			this.tipoPessoaIsFisica = true;
		}
	}

	public void clearRecebedor() {
		this.idRecebedor = 0;
		this.nomeRecebedor = null;
		this.selectedRecebedor = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedor2() {
		this.idRecebedor2 = this.selectedRecebedor2.getId();
		this.nomeRecebedor2 = this.selectedRecebedor2.getNome();
	}

	public void clearRecebedor2() {
		this.idRecebedor2 = 0;
		this.nomeRecebedor2 = null;
		this.selectedRecebedor2 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedor3() {
		this.idRecebedor3 = this.selectedRecebedor3.getId();
		this.nomeRecebedor3 = this.selectedRecebedor3.getNome();
	}

	public void clearRecebedor3() {
		this.idRecebedor3 = 0;
		this.nomeRecebedor3 = null;
		this.selectedRecebedor3 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedor4() {
		this.idRecebedor4 = this.selectedRecebedor4.getId();
		this.nomeRecebedor4 = this.selectedRecebedor4.getNome();
	}

	public void clearRecebedor4() {
		this.idRecebedor4 = 0;
		this.nomeRecebedor4 = null;
		this.selectedRecebedor4 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedor5() {
		this.idRecebedor5 = this.selectedRecebedor5.getId();
		this.nomeRecebedor5 = this.selectedRecebedor5.getNome();
	}

	public void clearRecebedor5() {
		this.idRecebedor5 = 0;
		this.nomeRecebedor5 = null;
		this.selectedRecebedor5 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedor6() {
		this.idRecebedor6 = this.selectedRecebedor6.getId();
		this.nomeRecebedor6 = this.selectedRecebedor6.getNome();
	}

	public void clearRecebedor6() {
		this.idRecebedor6 = 0;
		this.nomeRecebedor6 = null;
		this.selectedRecebedor6 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedor7() {
		this.idRecebedor7 = this.selectedRecebedor7.getId();
		this.nomeRecebedor7 = this.selectedRecebedor7.getNome();
	}

	public void clearRecebedor7() {
		this.idRecebedor7 = 0;
		this.nomeRecebedor7 = null;
		this.selectedRecebedor7 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedor8() {
		this.idRecebedor8 = this.selectedRecebedor8.getId();
		this.nomeRecebedor8 = this.selectedRecebedor8.getNome();
	}

	public void clearRecebedor8() {
		this.idRecebedor8 = 0;
		this.nomeRecebedor8 = null;
		this.selectedRecebedor8 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedor9() {
		this.idRecebedor9 = this.selectedRecebedor9.getId();
		this.nomeRecebedor9 = this.selectedRecebedor9.getNome();
	}

	public void clearRecebedor9() {
		this.idRecebedor9 = 0;
		this.nomeRecebedor9 = null;
		this.selectedRecebedor9 = new PagadorRecebedor();
	}

	public final void populateSelectedRecebedor10() {
		this.idRecebedor10 = this.selectedRecebedor10.getId();
		this.nomeRecebedor10 = this.selectedRecebedor10.getNome();
	}

	public void clearRecebedor10() {
		this.idRecebedor10 = 0;
		this.nomeRecebedor10 = null;
		this.selectedRecebedor10 = new PagadorRecebedor();
	}

	public final void populateSelectedResponsavel() {
		this.idResponsavel = this.selectedResponsavel.getId();
		this.nomeResponsavel = this.selectedResponsavel.getNome();
	}

	public void clearResponsavel() {
		this.idResponsavel = 0;
		this.nomeResponsavel = null;
		this.selectedResponsavel = new Responsavel();
	}

	public final void populateSelectedImovel() {
		this.idImovel = this.selectedImovel.getId();
		this.nomeImovel = this.selectedImovel.getNome();
	}

	public void clearImovel() {
		this.idImovel = 0;
		this.nomeImovel = null;
		this.selectedImovel = new ImovelCobranca();
	}

	public void gerarParcelaFinal() {
		if (this.objetoContratoCobranca.isGeraParcelaFinal()) {
			if (this.vlrParcelaFinal == null || this.vlrParcelaFinal == BigDecimal.ZERO) {
				this.vlrParcelaFinal = this.objetoContratoCobranca.getVlrInvestimento();
			}
		} else {
			this.vlrParcelaFinal = null;
			this.vlrRepasseFinal = null;
			this.vlrRetencaoFinal = null;

			clearRecebedorFinal1();
			clearRecebedorFinal2();
			clearRecebedorFinal3();
			clearRecebedorFinal4();
			clearRecebedorFinal5();
			clearRecebedorFinal6();
			clearRecebedorFinal7();
			clearRecebedorFinal8();
			clearRecebedorFinal9();
			clearRecebedorFinal10();

			this.objetoContratoCobranca.setVlrFinalRecebedor1(null);
			this.objetoContratoCobranca.setVlrFinalRecebedor2(null);
			this.objetoContratoCobranca.setVlrFinalRecebedor3(null);
			this.objetoContratoCobranca.setVlrFinalRecebedor4(null);
			this.objetoContratoCobranca.setVlrFinalRecebedor5(null);
			this.objetoContratoCobranca.setVlrFinalRecebedor6(null);
			this.objetoContratoCobranca.setVlrFinalRecebedor7(null);
			this.objetoContratoCobranca.setVlrFinalRecebedor8(null);
			this.objetoContratoCobranca.setVlrFinalRecebedor9(null);
			this.objetoContratoCobranca.setVlrFinalRecebedor10(null);
		}
	}

	/************************************************************
	 * FIM - Métodos utilizados pelas LoVs
	 ***********************************************************/

	public void loadRetencaoRepasse() {
		// recupera campo retenção e repasse
		if (this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() > 0) {
			if (this.objetoContratoCobranca.isGeraParcelaFinal()) {
				if (this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() >= 2) {
					this.vlrRepasse = this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
							.get(this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() - 2)
							.getVlrRepasse();

					this.vlrRetencao = this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
							.get(this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() - 2)
							.getVlrRetencao();
				}

				this.vlrParcelaFinal = this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
						.get(this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() - 1).getVlrParcela();

				this.vlrRepasseFinal = this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
						.get(this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() - 1).getVlrRepasse();

				this.vlrRetencaoFinal = this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
						.get(this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() - 1).getVlrParcela();

				this.vlrComissaoFinal = this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
						.get(this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() - 1).getVlrComissao();
			} else {
				if (this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() >= 2) {
					this.vlrRepasse = this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
							.get(this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() - 2)
							.getVlrRepasse();
					this.vlrRetencao = this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
							.get(this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() - 2)
							.getVlrRetencao();
				} else {
					this.vlrRepasse = this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
							.get(this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() - 1)
							.getVlrRepasse();
					this.vlrRetencao = this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
							.get(this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() - 1)
							.getVlrRetencao();
				}
			}

			this.vlrComissao = this.objetoContratoCobranca.getListContratoCobrancaDetalhes().get(0).getVlrComissao();
		} else {
			this.vlrParcelaFinal = this.objetoContratoCobranca.getVlrParcelaFinal();
		}
	}

	public String clearFields() {
		objetoContratoCobranca = new ContratoCobranca();
		this.tituloPainel = "Adicionar";

		this.objetoContratoCobranca.setDataContrato(new Date());

		loadLovs();

		clearSelectedLovs();

		this.contratoGerado = false;

		this.qtdeParcelas = null;
		
		filesInterno = new ArrayList<FileUploaded>();
		this.files = new ArrayList<FileUploaded>();

		clearSelectedRecebedores();
		this.seguradoSelecionado = new Segurado();
		this.seguradoSelecionado.setPessoa(new PagadorRecebedor());
		this.socioSelecionado = new PagadorRecebedorSocio();
		this.socioSelecionado.setPessoa(new PagadorRecebedor());
		this.pagadorSecundarioSelecionado = new PagadorRecebedorAdicionais();
		this.pagadorSecundarioSelecionado.setPessoa(new PagadorRecebedor());
		this.addSegurador= false;
		this.addSocio= false;
		this.addPagador = false;
		
		this.contasPagarSelecionada = new ContasPagar();
		this.contasPagarSelecionada.setPagadorRecebedor(new PagadorRecebedor());
		this.contasPagarSelecionada.setResponsavel(new Responsavel());
		
		this.vlrParcelaFinal = null;
		this.vlrRepasse = null;
		this.vlrRepasseFinal = null;
		this.vlrRetencao = null;
		this.vlrRetencaoFinal = null;

		this.vlrComissao = null;
		this.vlrComissaoFinal = null;

		this.objetoContratoCobranca.setGeraParcelaFinal(false);

		this.objetoContratoCobranca.setNumeroContrato(geraNumeroContrato());

		this.objetoContratoCobranca.setStatus("Aprovado");

		this.objetoContratoCobranca.setEmpresa("GALLERIA FINANÇAS SECURITIZADORA S.A.");

		this.geraBoletoInclusaoContrato = false;
		this.fileBoleto = null;

		ParametrosDao pDao = new ParametrosDao();
		this.objetoContratoCobranca
				.setTxJuros(pDao.findByFilter("nome", "COBRANCA_REC_TX_JUROS").get(0).getValorBigDecimal());
		this.objetoContratoCobranca
				.setTxMulta(pDao.findByFilter("nome", "COBRANCA_REC_MULTA").get(0).getValorBigDecimal());

		return "/Atendimento/Cobranca/ContratoCobrancaInserir.xhtml";
	}

	public String clearFieldsNew() {
		objetoContratoCobranca = new ContratoCobranca();
		this.tituloPainel = "Adicionar";

		this.objetoContratoCobranca.setDataContrato(new Date());

		loadLovs();

		clearSelectedLovs();

		this.contratoGerado = false;

		this.qtdeParcelas = null;

		this.files = new ArrayList<FileUploaded>();
		filesInterno = new ArrayList<FileUploaded>();
		

		clearSelectedRecebedores();

		this.seguradoSelecionado = new Segurado();
		this.seguradoSelecionado.setPessoa(new PagadorRecebedor());
		this.socioSelecionado = new PagadorRecebedorSocio();
		this.socioSelecionado.setPessoa(new PagadorRecebedor());
		this.pagadorSecundarioSelecionado = new PagadorRecebedorAdicionais();
		this.pagadorSecundarioSelecionado.setPessoa(new PagadorRecebedor());
		this.addSegurador= false;
		this.addSocio = false;
		this.addPagador = false;
		
		this.contasPagarSelecionada = new ContasPagar();
		this.contasPagarSelecionada.setPagadorRecebedor(new PagadorRecebedor());
		this.contasPagarSelecionada.setResponsavel(new Responsavel());
		
		this.vlrParcelaFinal = null;
		this.vlrRepasse = null;
		this.vlrRepasseFinal = null;
		this.vlrRetencao = null;
		this.vlrRetencaoFinal = null;

		this.vlrComissao = null;
		this.vlrComissaoFinal = null;

		this.objetoContratoCobranca.setGeraParcelaFinal(false);

		this.objetoContratoCobranca.setNumeroContrato(geraNumeroContrato());

		this.objetoContratoCobranca.setStatus("Aprovado");		
		
		this.objetoContratoCobranca.setStatusLead("Completo");

		this.objetoContratoCobranca.setEmpresa("GALLERIA FINANÇAS SECURITIZADORA S.A.");

		this.geraBoletoInclusaoContrato = false;
		this.fileBoleto = null;

		ParametrosDao pDao = new ParametrosDao();
		this.objetoContratoCobranca
				.setTxJuros(pDao.findByFilter("nome", "COBRANCA_REC_TX_JUROS").get(0).getValorBigDecimal());
		this.objetoContratoCobranca
				.setTxMulta(pDao.findByFilter("nome", "COBRANCA_REC_MULTA").get(0).getValorBigDecimal());

		return "/Atendimento/Cobranca/ContratoCobrancaInserir.xhtml";
	}

	public void clearSelectedRecebedores() {
		this.renderRecebedor2 = false;
		this.renderRecebedor3 = false;
		this.renderRecebedor4 = false;
		this.renderRecebedor5 = false;
		this.renderRecebedor6 = false;
		this.renderRecebedor7 = false;
		this.renderRecebedor8 = false;
		this.renderRecebedor9 = false;
		this.renderRecebedor10 = false;

		this.selectedRecebedor = null;
		this.idRecebedor = 0;
		this.nomeRecebedor = null;

		this.selectedRecebedor2 = null;
		this.idRecebedor2 = 0;
		this.nomeRecebedor2 = null;

		this.selectedRecebedor3 = null;
		this.idRecebedor3 = 0;
		this.nomeRecebedor3 = null;

		this.selectedRecebedor4 = null;
		this.idRecebedor4 = 0;
		this.nomeRecebedor4 = null;

		this.selectedRecebedor5 = null;
		this.idRecebedor5 = 0;
		this.nomeRecebedor5 = null;

		this.selectedRecebedor6 = null;
		this.idRecebedor6 = 0;
		this.nomeRecebedor6 = null;

		this.selectedRecebedor7 = null;
		this.idRecebedor7 = 0;
		this.nomeRecebedor7 = null;

		this.selectedRecebedor8 = null;
		this.idRecebedor8 = 0;
		this.nomeRecebedor8 = null;

		this.selectedRecebedor9 = null;
		this.idRecebedor9 = 0;
		this.nomeRecebedor9 = null;

		this.selectedRecebedor10 = null;
		this.idRecebedor10 = 0;
		this.nomeRecebedor10 = null;

		clearRecebedorFinal1();
		clearRecebedorFinal2();
		clearRecebedorFinal3();
		clearRecebedorFinal4();
		clearRecebedorFinal5();
		clearRecebedorFinal6();
		clearRecebedorFinal7();
		clearRecebedorFinal8();
		clearRecebedorFinal9();
		clearRecebedorFinal10();
	}

	public String geraNumeroContrato() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

		int numeroUltimoContrato = Integer.valueOf(contratoCobrancaDao.ultimoNumeroContrato());

		return String.format("%05d", numeroUltimoContrato);
	}

	public String clearFieldsView() {
		this.tituloPainel = "Visualizar";

		loadLovs();

		loadSelectedLovs();

		this.contratoGerado = true;

		this.qtdeParcelas = String.valueOf(this.objetoContratoCobranca.getQtdeParcelas());

		// Verifica se há parcelas em atraso, se sim irá colorir a linha na tela
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		Calendar dataVencimentoParcela = Calendar.getInstance(zone, locale);
		dataHoje.set(Calendar.HOUR_OF_DAY, 0);
		dataHoje.set(Calendar.MINUTE, 0);
		dataHoje.set(Calendar.SECOND, 0);
		dataHoje.set(Calendar.MILLISECOND, 0);

		for (ContratoCobrancaDetalhes ccd : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			dataVencimentoParcela.setTime(ccd.getDataVencimento());
			dataHoje.set(Calendar.HOUR_OF_DAY, 0);
			dataHoje.set(Calendar.MINUTE, 0);
			dataHoje.set(Calendar.SECOND, 0);
			dataHoje.set(Calendar.MILLISECOND, 0);

			if (dataVencimentoParcela.getTime().before(dataHoje.getTime()) && !ccd.isParcelaPaga()) {
				ccd.setParcelaVencida(true);
			}

			if (dataVencimentoParcela.getTime().equals(dataHoje.getTime()) && !ccd.isParcelaPaga()) {
				ccd.setParcelaVencendo(true);
			}
		}

		loadRetencaoRepasse();

		files = new ArrayList<FileUploaded>();
		filesInterno = new ArrayList<FileUploaded>();
		files = listaArquivos();
		filesInterno = listaArquivosInterno();
		
		return "/Atendimento/Cobranca/ContratoCobrancaDetalhes.xhtml";
	}

	public String clearFieldsExcluir() {
		this.tituloPainel = "Excluir";

		loadLovs();

		loadSelectedLovs();

		this.contratoGerado = true;

		this.qtdeParcelas = String.valueOf(this.objetoContratoCobranca.getQtdeParcelas());

		loadRetencaoRepasse();

		return "/Atendimento/Cobranca/ContratoCobrancaDetalhes.xhtml";
	}

	public String clearFieldsBaixar() {
		this.tituloPainel = "Baixar Parcela";

		// loadLovs();

		// loadSelectedLovs();

		this.dataHoje = gerarDataHoje();

		loadListRecebedores();

		this.contratoGerado = true;

		this.qtdeParcelas = String.valueOf(this.objetoContratoCobranca.getQtdeParcelas());

		this.selectedListContratoCobrancaDetalhes = new ArrayList<ContratoCobrancaDetalhes>();

		this.dataPromessaPagamento = null;

		this.files = new ArrayList<FileUploaded>();
		this.files = listaArquivos();
		filesInterno = new ArrayList<FileUploaded>();
		filesInterno = listaArquivosInterno();

		this.reciboGerado = false;
		this.fileRecibo = null;
		this.pathRecibo = null;
		this.nomeRecibo = null;

		this.dataPagamentoInvestidor = gerarDataHoje();

		// Verifica se há parcelas em atraso, se sim irá colorir a linha na tela
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		Date auxDataPagamento = dataHoje.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
		String auxDataPagamentoStr = sdf.format(dataHoje.getTime());
		try {
			auxDataPagamento = sdf.parse(auxDataPagamentoStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// onRowEdit - nova data
		rowEditNewDate = auxDataPagamento;

		for (ContratoCobrancaDetalhes ccd : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			// se já houve baixa parcial, utiliza a data de vencimento atualizada
			// senão utiliza a data de vencimento antiga
			String auxDataVencimentoStr = "";
			Date auxDataVencimento = null;
			if (ccd.getDataVencimentoAtual() != null) {
				auxDataVencimentoStr = sdf.format(ccd.getDataVencimentoAtual());
				auxDataVencimento = ccd.getDataVencimentoAtual();
			} else {
				auxDataVencimentoStr = sdf.format(ccd.getDataVencimento());
				auxDataVencimento = ccd.getDataVencimento();
			}

			try {
				auxDataVencimento = sdf.parse(auxDataVencimentoStr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (auxDataVencimento.before(auxDataPagamento) && !ccd.isParcelaPaga()) {
				ccd.setParcelaVencida(true);

				// calcula coluna valor atualizado
				ContratoCobrancaUtilsMB contratoCobrancaUtilsMB;
				/*
				 * if (ccd.getVlrJuros().compareTo(BigDecimal.ZERO) == 0) {
				 * contratoCobrancaUtilsMB = new ContratoCobrancaUtilsMB( auxDataVencimento,
				 * auxDataPagamento, ccd.getVlrParcela(), BigDecimal.valueOf(1.00),
				 * ccd.getTxMulta()); } else { contratoCobrancaUtilsMB = new
				 * ContratoCobrancaUtilsMB( auxDataVencimento, auxDataPagamento,
				 * ccd.getVlrParcela(), ccd.getVlrJuros(), ccd.getTxMulta()); }
				 */

				if (ccd.getVlrJuros() != null) {
					if (BigDecimal.ZERO.compareTo(ccd.getVlrJuros()) == 0) {
						contratoCobrancaUtilsMB = new ContratoCobrancaUtilsMB(auxDataVencimento, auxDataPagamento,
								ccd.getVlrParcela(), BigDecimal.valueOf(1.00), this.objetoContratoCobranca.getTxMulta());
					} else {
						contratoCobrancaUtilsMB = new ContratoCobrancaUtilsMB(auxDataVencimento, auxDataPagamento,
								ccd.getVlrParcela(), this.objetoContratoCobranca.getTxJuros(),
								this.objetoContratoCobranca.getTxMulta());
					}
				} else {
					contratoCobrancaUtilsMB = new ContratoCobrancaUtilsMB(auxDataVencimento, auxDataPagamento,
					ccd.getVlrParcela(), this.objetoContratoCobranca.getTxJuros(),
					this.objetoContratoCobranca.getTxMulta());
				}

				if (!ccd.isParcelaPaga()) {
					if (ccd.getListContratoCobrancaDetalhesParcial().size() > 0) {
						contratoCobrancaUtilsMB.recalculaValorSemMulta();
					} else {
						contratoCobrancaUtilsMB.recalculaValor();
					}
					ccd.setVlrParcelaAtualizada(contratoCobrancaUtilsMB.getValorAtualizado());
				} else {
					ccd.setVlrParcelaAtualizada(null);
				}
			}

			if (auxDataVencimento.equals(auxDataPagamento) && !ccd.isParcelaPaga()) {
				ccd.setParcelaVencendo(true);
			}

			BigDecimal somaBaixas = BigDecimal.ZERO;
			if (ccd.isAmortizacao()) {
				somaBaixas = ccd.getVlrParcela();
			} else {
				for (ContratoCobrancaDetalhesParcial cBaixas : ccd.getListContratoCobrancaDetalhesParcial()) {
					ccd.setDataUltimoPagamento(cBaixas.getDataPagamento());
					somaBaixas = somaBaixas.add(cBaixas.getVlrRecebido());
				}
			}

			ccd.setValorTotalPagamento(somaBaixas);
		}

		loadRetencaoRepasse();

		this.selectedContratoCobrancaDetalhes = new ContratoCobrancaDetalhes();
		this.selectedContratoCobrancaDetalhes
				.setListContratoCobrancaFavorecidos(new ArrayList<ContratoCobrancaFavorecidos>());
		this.selectedContratoCobrancaDetalhes.getListContratoCobrancaFavorecidos()
				.add(new ContratoCobrancaFavorecidos());

		atualizaStatusDasParcelasInvestidor();

		// pré-seleciona o recebedor -- default galleria SA
		PagadorRecebedorDao prDao = new PagadorRecebedorDao();
		if (this.objetoContratoCobranca.getEmpresa() != null) {
			if (this.objetoContratoCobranca.getEmpresa().equals("FIDC GALLERIA")) {
				this.selectedRecebedor = prDao.findById((long) 6625);
			} else {
				this.selectedRecebedor = prDao.findById((long) 803);
			}
		}
		
		this.nomeRecebedor = this.selectedRecebedor.getNome();

		/*
		 * if (this.objetoContratoCobranca.getRecebedor2().getCpf() != null) {
		 * this.tipoPessoaIsFisica = true;
		 * 
		 * this.nomeFavorecido = this.objetoContratoCobranca.getRecebedor().getNome();
		 * this.cpfcnpjFavorecido = this.objetoContratoCobranca.getRecebedor().getCpf();
		 * } else { this.tipoPessoaIsFisica = false;
		 * 
		 * this.nomeFavorecido = this.objetoContratoCobranca.getRecebedor().getNome();
		 * this.cpfcnpjFavorecido =
		 * this.objetoContratoCobranca.getRecebedor().getCnpj(); }
		 */

		return "/Atendimento/Cobranca/ContratoCobrancaBaixar.xhtml";
	}

	public void atualizaStatusDasParcelasInvestidor() {
		for (ContratoCobrancaDetalhes ccd : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			// investidor 1
			if (this.objetoContratoCobranca.getRecebedor() != null) {
				for (ContratoCobrancaParcelasInvestidor ccpi : this.objetoContratoCobranca
						.getListContratoCobrancaParcelasInvestidor1()) {
					if (ccpi.getNumeroParcela().equals(ccd.getNumeroParcela())) {
						ccpi.setParcelaVencendo(ccd.isParcelaVencendo());
						ccpi.setParcelaVencida(ccd.isParcelaVencida());
						break;
					}
				}
			}

			// investidor 1
			if (this.objetoContratoCobranca.getRecebedor2() != null) {
				for (ContratoCobrancaParcelasInvestidor ccpi : this.objetoContratoCobranca
						.getListContratoCobrancaParcelasInvestidor2()) {
					if (ccpi.getNumeroParcela().equals(ccd.getNumeroParcela())) {
						ccpi.setParcelaVencendo(ccd.isParcelaVencendo());
						ccpi.setParcelaVencida(ccd.isParcelaVencida());
						break;
					}
				}
			}

			// investidor 1
			if (this.objetoContratoCobranca.getRecebedor3() != null) {
				for (ContratoCobrancaParcelasInvestidor ccpi : this.objetoContratoCobranca
						.getListContratoCobrancaParcelasInvestidor3()) {
					if (ccpi.getNumeroParcela().equals(ccd.getNumeroParcela())) {
						ccpi.setParcelaVencendo(ccd.isParcelaVencendo());
						ccpi.setParcelaVencida(ccd.isParcelaVencida());
						break;
					}
				}
			}

			// investidor 1
			if (this.objetoContratoCobranca.getRecebedor4() != null) {
				for (ContratoCobrancaParcelasInvestidor ccpi : this.objetoContratoCobranca
						.getListContratoCobrancaParcelasInvestidor4()) {
					if (ccpi.getNumeroParcela().equals(ccd.getNumeroParcela())) {
						ccpi.setParcelaVencendo(ccd.isParcelaVencendo());
						ccpi.setParcelaVencida(ccd.isParcelaVencida());
						break;
					}
				}
			}

			// investidor 1
			if (this.objetoContratoCobranca.getRecebedor5() != null) {
				for (ContratoCobrancaParcelasInvestidor ccpi : this.objetoContratoCobranca
						.getListContratoCobrancaParcelasInvestidor5()) {
					if (ccpi.getNumeroParcela().equals(ccd.getNumeroParcela())) {
						ccpi.setParcelaVencendo(ccd.isParcelaVencendo());
						ccpi.setParcelaVencida(ccd.isParcelaVencida());
						break;
					}
				}
			}

			// investidor 1
			if (this.objetoContratoCobranca.getRecebedor6() != null) {
				for (ContratoCobrancaParcelasInvestidor ccpi : this.objetoContratoCobranca
						.getListContratoCobrancaParcelasInvestidor6()) {
					if (ccpi.getNumeroParcela().equals(ccd.getNumeroParcela())) {
						ccpi.setParcelaVencendo(ccd.isParcelaVencendo());
						ccpi.setParcelaVencida(ccd.isParcelaVencida());
						break;
					}
				}
			}

			// investidor 1
			if (this.objetoContratoCobranca.getRecebedor7() != null) {
				for (ContratoCobrancaParcelasInvestidor ccpi : this.objetoContratoCobranca
						.getListContratoCobrancaParcelasInvestidor7()) {
					if (ccpi.getNumeroParcela().equals(ccd.getNumeroParcela())) {
						ccpi.setParcelaVencendo(ccd.isParcelaVencendo());
						ccpi.setParcelaVencida(ccd.isParcelaVencida());
						break;
					}
				}
			}

			// investidor 1
			if (this.objetoContratoCobranca.getRecebedor8() != null) {
				for (ContratoCobrancaParcelasInvestidor ccpi : this.objetoContratoCobranca
						.getListContratoCobrancaParcelasInvestidor8()) {
					if (ccpi.getNumeroParcela().equals(ccd.getNumeroParcela())) {
						ccpi.setParcelaVencendo(ccd.isParcelaVencendo());
						ccpi.setParcelaVencida(ccd.isParcelaVencida());
						break;
					}
				}
			}

			// investidor 1
			if (this.objetoContratoCobranca.getRecebedor9() != null) {
				for (ContratoCobrancaParcelasInvestidor ccpi : this.objetoContratoCobranca
						.getListContratoCobrancaParcelasInvestidor9()) {
					if (ccpi.getNumeroParcela().equals(ccd.getNumeroParcela())) {
						ccpi.setParcelaVencendo(ccd.isParcelaVencendo());
						ccpi.setParcelaVencida(ccd.isParcelaVencida());
						break;
					}
				}
			}

			// investidor 1
			if (this.objetoContratoCobranca.getRecebedor10() != null) {
				for (ContratoCobrancaParcelasInvestidor ccpi : this.objetoContratoCobranca
						.getListContratoCobrancaParcelasInvestidor10()) {
					if (ccpi.getNumeroParcela().equals(ccd.getNumeroParcela())) {
						ccpi.setParcelaVencendo(ccd.isParcelaVencendo());
						ccpi.setParcelaVencida(ccd.isParcelaVencida());
						break;
					}
				}
			}
		}
	}
	
	public StreamedContent gerarExcelParcelasInvestidor(List<ContratoCobrancaParcelasInvestidor> listaParcelas, String nome) throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook(getClass().getResourceAsStream("/resource/TabelaVazia.xlsx"));
		
		XSSFSheet sheet = wb.getSheetAt(0);
		DashboardDao dDao = new DashboardDao();
		List<ContratoCobranca> contratos = dDao.getContratosLead();
		
		XSSFRow linha = sheet.getRow(0);
		if(linha == null) {
			sheet.createRow(0);
			linha = sheet.getRow(0);
		}
		
		gravaCelula(0, "Numero Parcela", linha);
		gravaCelula(1, "Data Vencimento", linha);
		gravaCelula(2, "Valor Parcela", linha);
		gravaCelula(3, "Juros", linha);
		gravaCelula(4, "Amortização", linha);
		gravaCelula(5, "Saldo Credor", linha);
		gravaCelula(6, "IR Retido", linha);
		gravaCelula(7, "Valor Líquido", linha);
		
		int iLinha = 1;
		for (int iParcela = 0 ; iParcela < listaParcelas.size(); iParcela++) {
			ContratoCobrancaParcelasInvestidor parcela = listaParcelas.get(iParcela);
						
			linha = sheet.getRow(iLinha);
			if(linha == null) {
				sheet.createRow(iLinha);
				linha = sheet.getRow(iLinha);
			}
			
			Locale locale = new Locale("pt", "BR");  
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", locale);
			String dataStr = sdf.format(parcela.getDataVencimento());
		
			gravaCelula(0, parcela.getNumeroParcela(), linha);
			gravaCelula(1, dataStr, linha);
			if(!CommonsUtil.semValor(parcela.getParcelaMensalBaixa())) {
				gravaCelula(2, CommonsUtil.formataValorMonetario(parcela.getParcelaMensalBaixa(),"R$ "), linha);
			} else {
				gravaCelula(2, "R$ 0,00 ", linha);
			}
			
			if(!CommonsUtil.semValor(parcela.getJurosBaixa())) {
				gravaCelula(3, CommonsUtil.formataValorMonetario(parcela.getJurosBaixa(),"R$ "), linha);
			} else {
				gravaCelula(3, "R$ 0,00 ", linha);
			}
			
			if(!CommonsUtil.semValor(parcela.getAmortizacao())) {
				gravaCelula(4, CommonsUtil.formataValorMonetario(parcela.getAmortizacao(),"R$ "), linha);
			} else {
				gravaCelula(4, "R$ 0,00 ", linha);
			}
			
			if(!CommonsUtil.semValor(parcela.getSaldoCredorAtualizado())) {
				gravaCelula(5, CommonsUtil.formataValorMonetario(parcela.getSaldoCredorAtualizado(),"R$ "), linha);
			} else {
				gravaCelula(5, "R$ 0,00 ", linha);
			}
			
			if(!CommonsUtil.semValor(parcela.getIrRetido())) {
				gravaCelula(6, CommonsUtil.formataValorMonetario(parcela.getIrRetido(),"R$ "), linha);
			} else {
				gravaCelula(6, "R$ 0,00 ", linha);
			}
			
			if(!CommonsUtil.semValor(parcela.getValorLiquidoBaixa())) {
				gravaCelula(7, CommonsUtil.formataValorMonetario(parcela.getValorLiquidoBaixa(),"R$ "), linha);
			} else {
				gravaCelula(7, "R$ 0,00 ", linha);
			}
			
			iLinha++;
		}
		

		ByteArrayOutputStream  fileOut = new ByteArrayOutputStream ();
		//escrever tudo o que foi feito no arquivo
		
		wb.write(fileOut);

		//fecha a escrita de dados nessa planilha
		wb.close();
		
		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
				FacesContext.getCurrentInstance());
		
		gerador.open(String.format("Galleria Bank - Parcelas Investidor - " +  nome  + "%s.xlsx", ""));
		gerador.feed( new ByteArrayInputStream(fileOut.toByteArray()));
		gerador.close();

		return null;
    }
	
	public void viewFileInterno(String fileName) {

		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
			BufferedInputStream input = null;
			BufferedOutputStream output = null;

			ParametrosDao pDao = new ParametrosDao();
			String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
					+ this.objetoContratoCobranca.getNumeroContrato() + "/interno/" + fileName;

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
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void viewFile(String fileName) {

		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
			BufferedInputStream input = null;
			BufferedOutputStream output = null;

			ParametrosDao pDao = new ParametrosDao();
			String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
					+ this.objetoContratoCobranca.getNumeroContrato() + "/" + fileName;

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
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadListRecebedores() {
		cedentesIugu = new ArrayList<PagadorRecebedor>();
		if (this.objetoContratoCobranca.getRecebedor() != null) {
			if (!cedentesIugu.contains(this.objetoContratoCobranca.getRecebedor())) {
				cedentesIugu.add(this.objetoContratoCobranca.getRecebedor());
			}
		}
		if (this.objetoContratoCobranca.getRecebedor2() != null) {
			if (!cedentesIugu.contains(this.objetoContratoCobranca.getRecebedor2())) {
				cedentesIugu.add(this.objetoContratoCobranca.getRecebedor2());
			}
		}
		if (this.objetoContratoCobranca.getRecebedor3() != null) {
			if (!cedentesIugu.contains(this.objetoContratoCobranca.getRecebedor3())) {
				cedentesIugu.add(this.objetoContratoCobranca.getRecebedor3());
			}
		}
		if (this.objetoContratoCobranca.getRecebedor4() != null) {
			if (!cedentesIugu.contains(this.objetoContratoCobranca.getRecebedor4())) {
				cedentesIugu.add(this.objetoContratoCobranca.getRecebedor4());
			}
		}
		if (this.objetoContratoCobranca.getRecebedor5() != null) {
			if (!cedentesIugu.contains(this.objetoContratoCobranca.getRecebedor5())) {
				cedentesIugu.add(this.objetoContratoCobranca.getRecebedor5());
			}
		}
		if (this.objetoContratoCobranca.getRecebedor6() != null) {
			if (!cedentesIugu.contains(this.objetoContratoCobranca.getRecebedor6())) {
				cedentesIugu.add(this.objetoContratoCobranca.getRecebedor6());
			}
		}
		if (this.objetoContratoCobranca.getRecebedor7() != null) {
			if (!cedentesIugu.contains(this.objetoContratoCobranca.getRecebedor7())) {
				cedentesIugu.add(this.objetoContratoCobranca.getRecebedor7());
			}
		}
		if (this.objetoContratoCobranca.getRecebedor8() != null) {
			if (!cedentesIugu.contains(this.objetoContratoCobranca.getRecebedor8())) {
				cedentesIugu.add(this.objetoContratoCobranca.getRecebedor8());
			}
		}
		if (this.objetoContratoCobranca.getRecebedor9() != null) {
			if (!cedentesIugu.contains(this.objetoContratoCobranca.getRecebedor9())) {
				cedentesIugu.add(this.objetoContratoCobranca.getRecebedor9());
			}
		}
		if (this.objetoContratoCobranca.getRecebedor10() != null) {
			if (!cedentesIugu.contains(this.objetoContratoCobranca.getRecebedor10())) {
				cedentesIugu.add(this.objetoContratoCobranca.getRecebedor10());
			}
		}

		if (this.objetoContratoCobranca.getRecebedorParcelaFinal1() != null) {
			if (!cedentesIugu.contains(this.objetoContratoCobranca.getRecebedorParcelaFinal1())) {
				cedentesIugu.add(this.objetoContratoCobranca.getRecebedorParcelaFinal1());
			}
		}
		if (this.objetoContratoCobranca.getRecebedorParcelaFinal2() != null) {
			if (!cedentesIugu.contains(this.objetoContratoCobranca.getRecebedorParcelaFinal2())) {
				cedentesIugu.add(this.objetoContratoCobranca.getRecebedorParcelaFinal2());
			}
		}
		if (this.objetoContratoCobranca.getRecebedorParcelaFinal3() != null) {
			if (!cedentesIugu.contains(this.objetoContratoCobranca.getRecebedorParcelaFinal3())) {
				cedentesIugu.add(this.objetoContratoCobranca.getRecebedorParcelaFinal3());
			}
		}
		if (this.objetoContratoCobranca.getRecebedorParcelaFinal4() != null) {
			if (!cedentesIugu.contains(this.objetoContratoCobranca.getRecebedorParcelaFinal4())) {
				cedentesIugu.add(this.objetoContratoCobranca.getRecebedorParcelaFinal4());
			}
		}
		if (this.objetoContratoCobranca.getRecebedorParcelaFinal5() != null) {
			if (!cedentesIugu.contains(this.objetoContratoCobranca.getRecebedorParcelaFinal5())) {
				cedentesIugu.add(this.objetoContratoCobranca.getRecebedorParcelaFinal5());
			}
		}
		if (this.objetoContratoCobranca.getRecebedorParcelaFinal6() != null) {
			if (!cedentesIugu.contains(this.objetoContratoCobranca.getRecebedorParcelaFinal6())) {
				cedentesIugu.add(this.objetoContratoCobranca.getRecebedorParcelaFinal6());
			}
		}
		if (this.objetoContratoCobranca.getRecebedorParcelaFinal7() != null) {
			if (!cedentesIugu.contains(this.objetoContratoCobranca.getRecebedorParcelaFinal7())) {
				cedentesIugu.add(this.objetoContratoCobranca.getRecebedorParcelaFinal7());
			}
		}
		if (this.objetoContratoCobranca.getRecebedorParcelaFinal8() != null) {
			if (!cedentesIugu.contains(this.objetoContratoCobranca.getRecebedorParcelaFinal8())) {
				cedentesIugu.add(this.objetoContratoCobranca.getRecebedorParcelaFinal8());
			}
		}
		if (this.objetoContratoCobranca.getRecebedorParcelaFinal9() != null) {
			if (!cedentesIugu.contains(this.objetoContratoCobranca.getRecebedorParcelaFinal9())) {
				cedentesIugu.add(this.objetoContratoCobranca.getRecebedorParcelaFinal9());
			}
		}
		if (this.objetoContratoCobranca.getRecebedorParcelaFinal10() != null) {
			if (!cedentesIugu.contains(this.objetoContratoCobranca.getRecebedorParcelaFinal10())) {
				cedentesIugu.add(this.objetoContratoCobranca.getRecebedorParcelaFinal10());
			}
		}
	}

	public String clearFieldsBaixarSemCalculoJuros() {
		this.tituloPainel = "Baixar Parcela";

		// loadLovs();

		// loadSelectedLovs();

		this.contratoGerado = true;

		this.qtdeParcelas = String.valueOf(this.objetoContratoCobranca.getQtdeParcelas());

		this.selectedListContratoCobrancaDetalhes = new ArrayList<ContratoCobrancaDetalhes>();

		this.files = new ArrayList<FileUploaded>();
		this.files = listaArquivos();
		filesInterno = new ArrayList<FileUploaded>();
		filesInterno = listaArquivosInterno();

		this.reciboGerado = false;
		this.fileRecibo = null;
		this.pathRecibo = null;
		this.nomeRecibo = null;

		// Verifica se há parcelas em atraso, se sim irá colorir a linha na tela
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		Date auxDataPagamento = dataHoje.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
		String auxDataPagamentoStr = sdf.format(dataHoje.getTime());
		try {
			auxDataPagamento = sdf.parse(auxDataPagamentoStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// onRowEdit - nova data
		rowEditNewDate = auxDataPagamento;

		for (ContratoCobrancaDetalhes ccd : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			// se já houve baixa parcial, utiliza a data de vencimento atualizada
			// senão utiliza a data de vencimento antiga
			String auxDataVencimentoStr = "";
			Date auxDataVencimento = null;
			if (ccd.getDataVencimentoAtual() != null) {
				auxDataVencimentoStr = sdf.format(ccd.getDataVencimentoAtual());
				auxDataVencimento = ccd.getDataVencimentoAtual();
			} else {
				auxDataVencimentoStr = sdf.format(ccd.getDataVencimento());
				auxDataVencimento = ccd.getDataVencimento();
			}

			try {
				auxDataVencimento = sdf.parse(auxDataVencimentoStr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (auxDataVencimento.before(auxDataPagamento) && !ccd.isParcelaPaga()) {
				ccd.setParcelaVencida(true);
			}

			ccd.setVlrParcelaAtualizada(null);
		}

		loadRetencaoRepasse();

		this.selectedContratoCobrancaDetalhes = new ContratoCobrancaDetalhes();
		this.selectedContratoCobrancaDetalhes
				.setListContratoCobrancaFavorecidos(new ArrayList<ContratoCobrancaFavorecidos>());
		this.selectedContratoCobrancaDetalhes.getListContratoCobrancaFavorecidos()
				.add(new ContratoCobrancaFavorecidos());

		return "/Atendimento/Cobranca/ContratoCobrancaBaixar.xhtml";
	}

	public void realcularValorAtualizadoEstornoBxparcial(ContratoCobrancaDetalhes contratoCobrancaDetalhes) {
		// se já houve baixa parcial, utiliza a data de vencimento atualizada
		// senão utiliza a data de vencimento antiga
		// Verifica se há parcelas em atraso, se sim irá colorir a linha na tela
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		Date auxDataPagamento = dataHoje.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
		String auxDataPagamentoStr = sdf.format(dataHoje.getTime());
		try {
			auxDataPagamento = sdf.parse(auxDataPagamentoStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String auxDataVencimentoStr = "";
		Date auxDataVencimento = null;
		if (contratoCobrancaDetalhes.getDataVencimentoAtual() != null) {
			auxDataVencimentoStr = sdf.format(contratoCobrancaDetalhes.getDataVencimentoAtual());
			auxDataVencimento = contratoCobrancaDetalhes.getDataVencimentoAtual();
		} else {
			auxDataVencimentoStr = sdf.format(contratoCobrancaDetalhes);
			auxDataVencimento = contratoCobrancaDetalhes.getDataVencimento();
		}

		try {
			auxDataVencimento = sdf.parse(auxDataVencimentoStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (auxDataVencimento.before(auxDataPagamento) && !contratoCobrancaDetalhes.isParcelaPaga()) {
			contratoCobrancaDetalhes.setParcelaVencida(true);

			// calcula coluna valor atualizado
			ContratoCobrancaUtilsMB contratoCobrancaUtilsMB;
			contratoCobrancaUtilsMB = new ContratoCobrancaUtilsMB(auxDataVencimento, auxDataPagamento,
					contratoCobrancaDetalhes.getVlrParcela(), contratoCobrancaDetalhes.getVlrJuros(),
					contratoCobrancaDetalhes.getTxMulta());

			if (!contratoCobrancaDetalhes.isParcelaPaga()) {
				if (contratoCobrancaDetalhes.getListContratoCobrancaDetalhesParcial().size() > 0) {
					contratoCobrancaUtilsMB.recalculaValorSemMulta();
				} else {
					contratoCobrancaUtilsMB.recalculaValor();
				}
				contratoCobrancaDetalhes.setVlrParcelaAtualizada(contratoCobrancaUtilsMB.getValorAtualizado());
			} else {
				contratoCobrancaDetalhes.setVlrParcelaAtualizada(null);
			}
		}

		if (auxDataVencimento.equals(auxDataPagamento) && !contratoCobrancaDetalhes.isParcelaPaga()) {
			contratoCobrancaDetalhes.setParcelaVencendo(true);
			contratoCobrancaDetalhes.setVlrParcelaAtualizada(null);
		}
	}
	
	public void reprovarContrato() {
		FacesContext context = FacesContext.getCurrentInstance();
		this.objetoContratoCobranca = getContratoById(this.objetoContratoCobranca.getId());
		this.objetoContratoCobranca.setStatusContrato("Reprovado");
		this.objetoContratoCobranca.setReprovado(true);
		this.objetoContratoCobranca.setStatus("Reprovado");
		
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		contratoCobrancaDao.merge(this.objetoContratoCobranca);

		
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Contrato Cobrança: Pré-Contrato Reprovado com sucesso! (Contrato: "
								+ this.objetoContratoCobranca.getNumeroContrato() + ")!",
						""));
	}
	
	public String reprovarContratoConsultar(String consulta) {
		this.objetoContratoCobranca = getContratoById(this.objetoContratoCobranca.getId());
		reprovarContrato();
		
		return geraConsultaContratosPorStatus(consulta);
	}
	
	
	public void baixarPreContrato() {
		FacesContext context = FacesContext.getCurrentInstance();
		this.objetoContratoCobranca = getContratoById(this.objetoContratoCobranca.getId());
		this.objetoContratoCobranca.setStatus("Baixado");
		this.objetoContratoCobranca.setStatusContrato("Baixado");
		this.objetoContratoCobranca.setContratoResgatadoBaixar(false);
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		contratoCobrancaDao.merge(this.objetoContratoCobranca);
		
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Contrato Cobrança: Pré-Contrato baixado com sucesso! (Contrato: "
								+ this.objetoContratoCobranca.getNumeroContrato() + ")!",
						""));
	}
	

	public void recuperarContratoReprovado() {
		FacesContext context = FacesContext.getCurrentInstance();
		this.objetoContratoCobranca = getContratoById(this.objetoContratoCobranca.getId());
		this.objetoContratoCobranca.setStatusContrato("Em Análise");
		this.objetoContratoCobranca.setReprovado(false);
		this.objetoContratoCobranca.setStatus("Pendente");
		if(!CommonsUtil.semValor(this.objetoContratoCobranca.getCadastroAprovadoValor()) && CommonsUtil.mesmoValor(this.objetoContratoCobranca.getCadastroAprovadoValor(), "Reprovado")) {
			this.objetoContratoCobranca.setCadastroAprovadoValor("Aprovado");
		}
		this.objetoContratoCobranca.setAnaliseReprovada(false);
		this.objetoContratoCobranca.setAnaliseReprovadaData(null);
		this.objetoContratoCobranca.setAnaliseReprovadaUsuario(null);
		
		this.objetoContratoCobranca.setContratoResgatadoBaixar(true);
		this.objetoContratoCobranca.setContratoResgatadoData(gerarDataHoje());
		
		updateCheckList();
		
		this.objetoContratoCobranca.setContratoResgatadoBaixar(true);
		this.objetoContratoCobranca.setContratoResgatadoData(gerarDataHoje());
		this.objetoContratoCobranca.setDataUltimaAtualizacao(gerarDataHoje());
		
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		contratoCobrancaDao.merge(this.objetoContratoCobranca);
		
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Contrato Cobrança: Pré-Contrato resgatado com sucesso! (Contrato: "
								+ this.objetoContratoCobranca.getNumeroContrato() + ")!",
						""));
	}
			
	public void recuperarPreContratoBaixado() {
		FacesContext context = FacesContext.getCurrentInstance();
		this.objetoContratoCobranca = getContratoById(this.objetoContratoCobranca.getId());
		this.objetoContratoCobranca.setStatus("Pendente");
		this.objetoContratoCobranca.setStatusContrato("Em Análise");
		updateCheckList();
		this.objetoContratoCobranca.setContratoResgatadoBaixar(true);
		this.objetoContratoCobranca.setContratoResgatadoData(gerarDataHoje());
		this.objetoContratoCobranca.setDataUltimaAtualizacao(gerarDataHoje());
		
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		contratoCobrancaDao.merge(this.objetoContratoCobranca);
		
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Contrato Cobrança: Pré-Contrato resgatado com sucesso! (Contrato: "
								+ this.objetoContratoCobranca.getNumeroContrato() + ")!",
						""));
	}
	
	public void retirarPendencia() {
		FacesContext context = FacesContext.getCurrentInstance();
		this.objetoContratoCobranca = getContratoById(this.objetoContratoCobranca.getId());
		this.objetoContratoCobranca.setInicioAnalise(false);
		this.objetoContratoCobranca.setCadastroAprovadoValor("");
		updateCheckList();
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		contratoCobrancaDao.merge(this.objetoContratoCobranca);
		
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Contrato Cobrança: Pendência retirada com sucesso! (Contrato: "
								+ this.objetoContratoCobranca.getNumeroContrato() + ")!",
						""));
	}
	
	public String baixarEConsultarPreContrato() {
		this.objetoContratoCobranca = getContratoById(this.objetoContratoCobranca.getId());
		baixarPreContrato();		
		return geraConsultaContratosBaixados();
	}
	
	private BigDecimal valorPresenteParcela;

	private int numeroPresenteParcela;
	private int numeroParcelaQuitar;
	private Date dataQuitacao;
	private BigDecimal valorPresenteTotal;

	public void calcularValorPresenteParcela(){
		TimeZone zone = TimeZone.getDefault(); 
		Locale locale = new Locale("pt", "BR"); 
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		Date auxDataHoje = dataHoje.getTime();
		
		ContratoCobrancaDetalhes parcelas = this.objetoContratoCobranca.getListContratoCobrancaDetalhes().get(this.numeroPresenteParcela);
		BigDecimal juros = this.objetoContratoCobranca.getTxJurosParcelas();
		BigDecimal saldo = parcelas.getVlrJurosParcela().add(parcelas.getVlrAmortizacaoParcela());
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
	
		this.valorPresenteParcela = (saldo).divide(CommonsUtil.bigDecimalValue(divisor) , MathContext.DECIMAL128);
		this.valorPresenteParcela = this.valorPresenteParcela.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public BigDecimal calcularValorPresenteTotalContrato(ContratoCobranca contrato){
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		Date auxDataHoje = dataHoje.getTime();
		
		BigDecimal valorPresenteTotalContrato = BigDecimal.ZERO;
		BigDecimal juros = contrato.getTxJurosParcelas();
		
		for (ContratoCobrancaDetalhes parcelas : contrato.getListContratoCobrancaDetalhes()) {
			this.valorPresenteParcela = BigDecimal.ZERO;			
			BigDecimal saldo = parcelas.getVlrJurosParcela().add(parcelas.getVlrAmortizacaoParcela());
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
		
			this.valorPresenteParcela = (saldo).divide(CommonsUtil.bigDecimalValue(divisor) , MathContext.DECIMAL128);
			this.valorPresenteParcela = this.valorPresenteParcela.setScale(2, BigDecimal.ROUND_HALF_UP);
			
			valorPresenteTotalContrato = valorPresenteTotalContrato.add(this.valorPresenteParcela);
		}
		
		return valorPresenteTotalContrato;
	}
	
	public void quitarContrato(List<ContratoCobrancaDetalhes> listaParcelas) {
		ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		valorPresenteTotal = BigDecimal.ZERO;
		this.selectedListContratoCobrancaDetalhes = new ArrayList<ContratoCobrancaDetalhes>();
	
		for (ContratoCobrancaDetalhes parcelas : listaParcelas) {
			this.valorPresenteParcela = BigDecimal.ZERO;
			if(CommonsUtil.intValue(parcelas.getNumeroParcela()) >= numeroParcelaQuitar) {
				
				this.numeroPresenteParcela = CommonsUtil.intValue(parcelas.getNumeroParcela());
				calcularValorPresenteParcelaData(this.dataQuitacao, parcelas);
				parcelas.setValorTotalPagamento(this.valorPresenteParcela);
				valorPresenteTotal = valorPresenteTotal.add(valorPresenteParcela);
				parcelas.setDataUltimoPagamento(this.dataQuitacao);
				
				////////////////////////////////////////////////
				
				TimeZone zone = TimeZone.getDefault();
				Locale locale = new Locale("pt", "BR");
				
				Calendar dataPagamento = Calendar.getInstance(zone, locale);
				dataPagamento.setTime(this.dataQuitacao);

				ContratoCobrancaDetalhesParcial contratoCobrancaDetalhesParcial = new ContratoCobrancaDetalhesParcial();

				if (this.selectedRecebedor != null) {
					if (this.selectedRecebedor.getId() > 0) {
						contratoCobrancaDetalhesParcial.setRecebedor(this.selectedRecebedor);
					}
				}

				if (this.observacao != null) {
					contratoCobrancaDetalhesParcial.setObservacaoRecebedor(this.observacao);
				}
				
				contratoCobrancaDetalhesParcial.setDataVencimento(parcelas.getDataVencimento());
				contratoCobrancaDetalhesParcial.setDataVencimentoAtual(parcelas.getDataVencimentoAtual());
				contratoCobrancaDetalhesParcial.setNumeroParcela(parcelas.getNumeroParcela());
				contratoCobrancaDetalhesParcial.setDataPagamento(dataPagamento.getTime());
				contratoCobrancaDetalhesParcial.setVlrParcela(parcelas.getVlrParcela());
				contratoCobrancaDetalhesParcial.setVlrRecebido(parcelas.getValorTotalPagamento());
				contratoCobrancaDetalhesParcial.setVlrParcelaAtualizado(parcelas.getVlrParcela());
				contratoCobrancaDetalhesParcial.setSaldoAPagar(BigDecimal.ZERO);
				contratoCobrancaDetalhesParcial.setVlrParcelaAtualizado(parcelas.getVlrParcelaAtualizada());
				parcelas.getListContratoCobrancaDetalhesParcial().add(contratoCobrancaDetalhesParcial);
				parcelas.setParcelaPaga(true);
				contratoCobrancaDetalhesDao.merge(parcelas);
				this.selectedListContratoCobrancaDetalhes.add(parcelas);
			}
		}		
	}
	private List<ContratoCobrancaDetalhes> listContratoCobrancaDetalhesQuitar;
	
	public void clearQuitarContratoDialog() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		Date auxDataHoje = dataHoje.getTime();
		
		this.numeroParcelaQuitar = 0;
		this.dataQuitacao = auxDataHoje;
		
		simularQuitacaoContrato();
	}
	
	public void simularQuitacaoContrato() {
		this.valorPresenteTotal = BigDecimal.ZERO;
		this.listContratoCobrancaDetalhesQuitar = this.objetoContratoCobranca.getListContratoCobrancaDetalhes();
		
		for (ContratoCobrancaDetalhes parcelas : listContratoCobrancaDetalhesQuitar) {
			if(parcelas.isParcelaPaga()) {
				continue;
			}
			this.valorPresenteParcela = BigDecimal.ZERO;
			if(CommonsUtil.intValue(parcelas.getNumeroParcela()) >= numeroParcelaQuitar) {
				
				this.numeroPresenteParcela = CommonsUtil.intValue(parcelas.getNumeroParcela());
				calcularValorPresenteParcelaData(this.dataQuitacao, parcelas);
				valorPresenteTotal = valorPresenteTotal.add(this.valorPresenteParcela);
			}
		}
	}
	
	public void amortizarContratoValorPresente() {
		
	}
	
	public void calcularValorPresenteParcelaData(Date data, ContratoCobrancaDetalhes parcelas){
		
		
		BigDecimal juros = this.objetoContratoCobranca.getTxJurosParcelas();
		BigDecimal saldo = parcelas.getVlrJurosParcela().add(parcelas.getVlrAmortizacaoParcela());
		BigDecimal quantidadeDeMeses = BigDecimal.ONE;

		quantidadeDeMeses = BigDecimal.valueOf(DateUtil.Days360(data, parcelas.getDataVencimento()));
		
		quantidadeDeMeses = quantidadeDeMeses.divide(BigDecimal.valueOf(30), MathContext.DECIMAL128);
			
		if(quantidadeDeMeses.compareTo(BigDecimal.ZERO) == -1) { 
			quantidadeDeMeses = quantidadeDeMeses.multiply(BigDecimal.valueOf(-1)); 
		} 

		Double quantidadeDeMesesDouble = CommonsUtil.doubleValue(quantidadeDeMeses);
		
		juros = juros.divide(BigDecimal.valueOf(100));
		juros = juros.add(BigDecimal.ONE);
		
		double divisor = Math.pow(CommonsUtil.doubleValue(juros), quantidadeDeMesesDouble);
	
		this.valorPresenteParcela = (saldo).divide(CommonsUtil.bigDecimalValue(divisor) , MathContext.DECIMAL128);
		this.valorPresenteParcela = this.valorPresenteParcela.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	
	
	public List<ContratoCobrancaDetalhes> getListContratoCobrancaDetalhesQuitar() {
		return listContratoCobrancaDetalhesQuitar;
	}

	public void setListContratoCobrancaDetalhesQuitar(List<ContratoCobrancaDetalhes> listContratoCobrancaDetalhesQuitar) {
		this.listContratoCobrancaDetalhesQuitar = listContratoCobrancaDetalhesQuitar;
	}

	public BigDecimal getValorPresenteTotal() {
		return valorPresenteTotal;
	}

	public void setValorPresenteTotal(BigDecimal valorPresenteTotal) {
		this.valorPresenteTotal = valorPresenteTotal;
	}

	public Date getDataQuitacao() {
		return dataQuitacao;
	}

	public void setDataQuitacao(Date dataQuitacao) {
		this.dataQuitacao = dataQuitacao;
	}

	public int getNumeroParcelaQuitar() {
		return numeroParcelaQuitar;
	}

	public void setNumeroParcelaQuitar(int numeroParcelaQuitar) {
		this.numeroParcelaQuitar = numeroParcelaQuitar;
	}

	public BigDecimal getValorPresenteParcela() {
		return valorPresenteParcela;
	}

	public void setValorPresenteParcela(BigDecimal valorPresenteParcela) {
		this.valorPresenteParcela = valorPresenteParcela;
	}
	
	public int getNumeroPresenteParcela() {
		return numeroPresenteParcela;
	}

	public void setNumeroPresenteParcela(int numeroPresenteParcela) {
		this.numeroPresenteParcela = numeroPresenteParcela;
	}
	
	
	public String clearFieldsEditarPendentes() {
			
		this.objetoContratoCobranca = getContratoById(this.objetoContratoCobranca.getId());
		this.objetoImovelCobranca = this.objetoContratoCobranca.getImovel();
		this.objetoPagadorRecebedor = this.objetoContratoCobranca.getPagador();
		
		this.tituloPainel = "Editar";
		
		this.valorPresenteParcela = BigDecimal.ZERO;
		
		files = new ArrayList<FileUploaded>();
		files = listaArquivos();
		filesInterno = new ArrayList<FileUploaded>();
		filesInterno = listaArquivosInterno();
		
		this.objetoContratoCobranca.setContaPagarValorTotal(calcularValorTotalContasPagar()); 

		loadLovs();

		loadSelectedLovsPendentes();

		if (this.objetoContratoCobranca.getPagador() != null) {
			if (this.objetoContratoCobranca.getPagador().getCnpj() != null
					&& !this.objetoContratoCobranca.getPagador().getCnpj().equals("")) {
				this.tipoPessoaIsFisica = false;
			} else {
				this.tipoPessoaIsFisica = true;
			}
		}

		this.qtdeParcelas = String.valueOf(this.objetoContratoCobranca.getQtdeParcelas());

		if (this.objetoContratoCobranca.getResponsavel() != null) {
			this.codigoResponsavel = this.objetoContratoCobranca.getResponsavel().getCodigo();
		}
		// this.objetoContratoCobranca.setDataInicio(this.objetoContratoCobranca.getDataContrato());

		saveEstadoCheckListAtual();

		return "/Atendimento/Cobranca/ContratoCobrancaInserirPendente.xhtml";
	}
	
	public String clearFieldsDetalhesPendentes() {
		
		this.objetoContratoCobranca = getContratoById(this.objetoContratoCobranca.getId());
		this.objetoImovelCobranca = this.objetoContratoCobranca.getImovel();
		this.objetoPagadorRecebedor = this.objetoContratoCobranca.getPagador();
		
		this.tituloPainel = "Editar";
		
		this.valorPresenteParcela = BigDecimal.ZERO;
		
		files = new ArrayList<FileUploaded>();
		files = listaArquivos();
		filesInterno = new ArrayList<FileUploaded>();
		filesInterno = listaArquivosInterno();
		
		this.objetoContratoCobranca.setContaPagarValorTotal(calcularValorTotalContasPagar()); 
		
		this.tituloTelaConsultaPreStatus = "PreContratos";
		
		getIndexStepContrato();

		loadLovs();

		loadSelectedLovsPendentes();

		if (this.objetoContratoCobranca.getPagador() != null) {
			if (this.objetoContratoCobranca.getPagador().getCnpj() != null
					&& !this.objetoContratoCobranca.getPagador().getCnpj().equals("")) {
				this.tipoPessoaIsFisica = false;
			} else {
				this.tipoPessoaIsFisica = true;
			}
		}
		
		this.seguradoSelecionado = new Segurado();
		this.seguradoSelecionado.setPessoa(new PagadorRecebedor());
		this.socioSelecionado = new PagadorRecebedorSocio();
		this.socioSelecionado.setPessoa(new PagadorRecebedor());
		this.pagadorSecundarioSelecionado = new PagadorRecebedorAdicionais();
		this.pagadorSecundarioSelecionado.setPessoa(new PagadorRecebedor());
		this.addSegurador = false;
		this.addSocio = false;
		this.addPagador = false;
		
		this.objetoAnaliseComite = new AnaliseComite();
		this.objetoContratoCobranca.setQtdeVotosAprovadosComite(BigInteger.ZERO);
		this.objetoContratoCobranca.setQtdeVotosReprovadosComite(BigInteger.ZERO);
		

		this.qtdeParcelas = String.valueOf(this.objetoContratoCobranca.getQtdeParcelas());

		if (this.objetoContratoCobranca.getResponsavel() != null) {
			this.codigoResponsavel = this.objetoContratoCobranca.getResponsavel().getCodigo();
		}
		// this.objetoContratoCobranca.setDataInicio(this.objetoContratoCobranca.getDataContrato());

		saveEstadoCheckListAtual();

		return "/Atendimento/Cobranca/ContratoCobrancaDetalhesPendentePorStatus.xhtml";
	}
	
	public void clearMensagensWhatsApp() {
		this.controleWhatsAppAgAssintura = false;
		this.controleWhatsAppAssinado = false;
		this.controleWhatsAppPajuLaudoRecebido = false;
		this.controleWhatsAppPreAprovado = false;
		this.controleWhatsAppComite = false;
	}
	
	public String clearFieldsEditarPendentesAnalistas() {
		clearMensagensWhatsApp();
		this.objetoContratoCobranca = getContratoById(this.objetoContratoCobranca.getId());
		this.objetoImovelCobranca = this.objetoContratoCobranca.getImovel();
		this.objetoPagadorRecebedor = this.objetoContratoCobranca.getPagador();
		
		this.tituloPainel = "Editar";

		files = new ArrayList<FileUploaded>();
		files = listaArquivos();
		filesInterno = new ArrayList<FileUploaded>();
		filesInterno = listaArquivosInterno();
		
		this.objetoContratoCobranca.setContaPagarValorTotal(calcularValorTotalContasPagar()); 

		loadLovs();

		loadSelectedLovsPendentes();

		if (this.objetoContratoCobranca.getPagador() != null) {
			if (this.objetoContratoCobranca.getPagador().getCnpj() != null
					&& !this.objetoContratoCobranca.getPagador().getCnpj().equals("")) {
				this.tipoPessoaIsFisica = false;
			} else {
				this.tipoPessoaIsFisica = true;
			}
		}
		
		this.seguradoSelecionado = new Segurado();
		this.seguradoSelecionado.setPessoa(new PagadorRecebedor());
		this.socioSelecionado = new PagadorRecebedorSocio();
		this.socioSelecionado.setPessoa(new PagadorRecebedor());
		this.pagadorSecundarioSelecionado = new PagadorRecebedorAdicionais();
		this.pagadorSecundarioSelecionado.setPessoa(new PagadorRecebedor());
		this.addSegurador = false;
		this.addSocio = false;
		this.addPagador = false;

		this.qtdeParcelas = String.valueOf(this.objetoContratoCobranca.getQtdeParcelas());

		if (this.objetoContratoCobranca.getResponsavel() != null) {
			this.codigoResponsavel = this.objetoContratoCobranca.getResponsavel().getCodigo();
		}
		// this.objetoContratoCobranca.setDataInicio(this.objetoContratoCobranca.getDataContrato());

		saveEstadoCheckListAtual();	
		
		getIndexStepContrato();
		
		this.objetoAnaliseComite = new AnaliseComite();
		this.objetoContratoCobranca.setQtdeVotosAprovadosComite(BigInteger.ZERO);
		this.objetoContratoCobranca.setQtdeVotosReprovadosComite(BigInteger.ZERO);

		if(!this.objetoContratoCobranca.getListaAnaliseComite().isEmpty()) {
			for (AnaliseComite comite : this.objetoContratoCobranca.getListaAnaliseComite()) {
				User usuarioLogado = new User();
				UserDao u = new UserDao();
				usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);
				if(CommonsUtil.mesmoValor(comite.getVotoAnaliseComite(), "Aprovado")) {
					this.objetoContratoCobranca.setQtdeVotosAprovadosComite(this.objetoContratoCobranca.getQtdeVotosAprovadosComite().add(BigInteger.ONE));
				} else if(CommonsUtil.mesmoValor(comite.getVotoAnaliseComite(), "Reprovado")) {
					this.objetoContratoCobranca.setQtdeVotosReprovadosComite(this.objetoContratoCobranca.getQtdeVotosReprovadosComite().add(BigInteger.ONE));
				} 
				if(CommonsUtil.mesmoValor(usuarioLogado.getLogin(), comite.getUsuarioComite())) {
					this.objetoAnaliseComite = comite;
				} 
			}
		} 
		
		if(CommonsUtil.semValor(this.objetoAnaliseComite.getTaxaComite()) && !CommonsUtil.semValor(this.objetoContratoCobranca.getTaxaPreAprovada())) {
			this.objetoAnaliseComite.setTaxaComite(this.objetoContratoCobranca.getTaxaPreAprovada());
		}
		if(CommonsUtil.semValor(this.objetoAnaliseComite.getPrazoMaxComite()) && !CommonsUtil.semValor(this.objetoContratoCobranca.getPrazoMaxPreAprovado())) {
			this.objetoAnaliseComite.setPrazoMaxComite(this.objetoContratoCobranca.getPrazoMaxPreAprovado());
		}
		
		if(CommonsUtil.mesmoValor(this.tituloTelaConsultaPreStatus, "Pré-Comite")) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);
			if(usuarioLogado.isComiteConsultar()) {
				return "/Atendimento/Cobranca/ContratoCobrancaDetalhesPendentePorStatus.xhtml";
			} else {
				return "/Atendimento/Cobranca/ContratoCobrancaInserirPendentePorStatus.xhtml";
			}
		} else if (CommonsUtil.mesmoValor(this.tituloTelaConsultaPreStatus,"Ag. Comite")) {
			return "/Atendimento/Cobranca/ContratoCobrancaInserirPendentePorStatusComite.xhtml";
		} else {
			return "/Atendimento/Cobranca/ContratoCobrancaInserirPendentePorStatus.xhtml";
		}
	}
	
	public void getIndexStepContrato() {
		this.indexStepsStatusContrato = 0;
		
		if (!this.objetoContratoCobranca.isInicioAnalise()) {
			this.indexStepsStatusContrato = 0;
		} else if (this.objetoContratoCobranca.isAnaliseReprovada()) {
			this.indexStepsStatusContrato = 1;
		} else {			
			if (!this.objetoContratoCobranca.isAnaliseReprovada() && this.objetoContratoCobranca.isInicioAnalise() &&
					(this.objetoContratoCobranca.getCadastroAprovadoValor() == null || this.objetoContratoCobranca.getCadastroAprovadoValor().equals("") || this.objetoContratoCobranca.getCadastroAprovadoValor().equals("Pendente"))) {
				this.indexStepsStatusContrato = 1;
			}
			
			else if (this.objetoContratoCobranca.getCadastroAprovadoValor() != null) {
				if (!this.objetoContratoCobranca.isAnaliseReprovada() && this.objetoContratoCobranca.isInicioAnalise() && 
						this.objetoContratoCobranca.getCadastroAprovadoValor().equals("Aprovado") &&
						!this.objetoContratoCobranca.isPagtoLaudoConfirmada()) {
					this.indexStepsStatusContrato = 2;
				}
				
				else if (!this.objetoContratoCobranca.isAnaliseReprovada() && this.objetoContratoCobranca.isInicioAnalise() && 
						this.objetoContratoCobranca.getCadastroAprovadoValor().equals("Aprovado") &&
						this.objetoContratoCobranca.isPagtoLaudoConfirmada() && 
						(!this.objetoContratoCobranca.isLaudoRecebido() || !this.objetoContratoCobranca.isPajurFavoravel())) {
					this.indexStepsStatusContrato = 3;
				}
				
				else if (!this.objetoContratoCobranca.isAnaliseReprovada() && this.objetoContratoCobranca.isInicioAnalise() && 
						this.objetoContratoCobranca.getCadastroAprovadoValor().equals("Aprovado") &&
						this.objetoContratoCobranca.isPagtoLaudoConfirmada() && 
						this.objetoContratoCobranca.isLaudoRecebido() &&
						this.objetoContratoCobranca.isPajurFavoravel() &&
						!this.objetoContratoCobranca.isPreAprovadoComite() || !this.objetoContratoCobranca.isDocumentosComite()) {
					this.indexStepsStatusContrato = 4;
				}
				
				else if (!this.objetoContratoCobranca.isAnaliseReprovada() && this.objetoContratoCobranca.isInicioAnalise() && 
						this.objetoContratoCobranca.getCadastroAprovadoValor().equals("Aprovado") &&
						this.objetoContratoCobranca.isPagtoLaudoConfirmada() && 
						this.objetoContratoCobranca.isLaudoRecebido() &&
						this.objetoContratoCobranca.isPajurFavoravel() &&
						this.objetoContratoCobranca.isPreAprovadoComite() &&
						this.objetoContratoCobranca.isDocumentosComite() &&
						!this.objetoContratoCobranca.isAprovadoComite()) {
					this.indexStepsStatusContrato = 5;
				}
				
				else if (!this.objetoContratoCobranca.isAnaliseReprovada() && this.objetoContratoCobranca.isInicioAnalise() && 
						this.objetoContratoCobranca.getCadastroAprovadoValor().equals("Aprovado") &&
						this.objetoContratoCobranca.isPagtoLaudoConfirmada() && 
						this.objetoContratoCobranca.isLaudoRecebido() &&
						this.objetoContratoCobranca.isPajurFavoravel() &&
						this.objetoContratoCobranca.isAprovadoComite() &&
						!this.objetoContratoCobranca.isDocumentosCompletos()) {
					this.indexStepsStatusContrato = 6;
				}
				
				else if (!this.objetoContratoCobranca.isAnaliseReprovada() && this.objetoContratoCobranca.isInicioAnalise() && 
						this.objetoContratoCobranca.getCadastroAprovadoValor().equals("Aprovado") &&
						this.objetoContratoCobranca.isPagtoLaudoConfirmada() && 
						this.objetoContratoCobranca.isLaudoRecebido() &&
						this.objetoContratoCobranca.isPajurFavoravel() &&
						this.objetoContratoCobranca.isDocumentosCompletos() &&
						this.objetoContratoCobranca.isAprovadoComite() &&
						!this.objetoContratoCobranca.isCcbPronta()) {
					this.indexStepsStatusContrato = 7;
				}
				
				else if (!this.objetoContratoCobranca.isAnaliseReprovada() && this.objetoContratoCobranca.isInicioAnalise() && 
						this.objetoContratoCobranca.getCadastroAprovadoValor().equals("Aprovado") &&
						this.objetoContratoCobranca.isPagtoLaudoConfirmada() && 
						this.objetoContratoCobranca.isLaudoRecebido() &&
						this.objetoContratoCobranca.isPajurFavoravel() &&
						this.objetoContratoCobranca.isAprovadoComite() &&
						this.objetoContratoCobranca.isDocumentosCompletos() &&
						this.objetoContratoCobranca.isCcbPronta() &&
						this.objetoContratoCobranca.isAgAssinatura()) {
					this.indexStepsStatusContrato = 8;
				}
				
				else if (!this.objetoContratoCobranca.isAnaliseReprovada() && this.objetoContratoCobranca.isInicioAnalise() && 
						this.objetoContratoCobranca.getCadastroAprovadoValor().equals("Aprovado") &&
						this.objetoContratoCobranca.isPagtoLaudoConfirmada() && 
						this.objetoContratoCobranca.isLaudoRecebido() &&
						this.objetoContratoCobranca.isPajurFavoravel() &&
						this.objetoContratoCobranca.isAprovadoComite() &&
						this.objetoContratoCobranca.isDocumentosCompletos() &&
						this.objetoContratoCobranca.isCcbPronta() &&
						!this.objetoContratoCobranca.isAgAssinatura() &&
						this.objetoContratoCobranca.isAgRegistro()) {
					this.indexStepsStatusContrato = 9;
				}
			}
			
			//coloca todos no status a frente
			/*
			if (indexStepsStatusContrato < 9) {
				indexStepsStatusContrato = indexStepsStatusContrato + 1;	
			}	
			*/
		}
	}
	
	public ContratoCobranca getContratoById(long idContrato) {
		ContratoCobranca contrato = new ContratoCobranca();
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
				
		contrato = cDao.findById(idContrato);
		
		return contrato;
	}

	public String clearFieldsEditarPendentesSimples() {
		this.objetoContratoCobranca = getContratoById(this.objetoContratoCobranca.getId());
		this.objetoImovelCobranca = this.objetoContratoCobranca.getImovel();
		this.objetoPagadorRecebedor = this.objetoContratoCobranca.getPagador();
		this.contratos = new ArrayList<ContratoCobranca>();
		
		this.tituloPainel = "Editar";

		files = new ArrayList<FileUploaded>();
		files = listaArquivos();
		filesInterno = new ArrayList<FileUploaded>();
		filesInterno = listaArquivosInterno();

		loadLovs();

		loadSelectedLovsPendentes();

		if (this.objetoContratoCobranca.getPagador() != null) {
			if (this.objetoContratoCobranca.getPagador().getCnpj() != null
					&& !this.objetoContratoCobranca.getPagador().getCnpj().equals("")) {
				this.tipoPessoaIsFisica = false;
			} else {
				this.tipoPessoaIsFisica = true;
			}
		}
		
		this.seguradoSelecionado = new Segurado();
		this.seguradoSelecionado.setPessoa(new PagadorRecebedor());
		this.socioSelecionado = new PagadorRecebedorSocio();
		this.socioSelecionado.setPessoa(new PagadorRecebedor());
		this.pagadorSecundarioSelecionado = new PagadorRecebedorAdicionais();
		this.pagadorSecundarioSelecionado.setPessoa(new PagadorRecebedor());
		this.addSegurador = false;
		this.addSocio = false;
		this.addPagador = false;

		// this.qtdeParcelas =
		// String.valueOf(this.objetoContratoCobranca.getQtdeParcelas());

		if (this.objetoContratoCobranca.getResponsavel() != null) {
			this.codigoResponsavel = this.objetoContratoCobranca.getResponsavel().getCodigo();
		}
		// this.objetoContratoCobranca.setDataInicio(this.objetoContratoCobranca.getDataContrato());

		// saveEstadoCheckListAtual();
		
		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);
			
			if (usuarioLogado != null) {
				if (!this.objetoContratoCobranca.isInicioAnalise()) {
					return "/Atendimento/Cobranca/ContratoCobrancaPreCustomizadoInserir.xhtml";
				} else {
					return "/Atendimento/Cobranca/ContratoCobrancaPreCustomizadoDetalhes.xhtml";
				}	
			}
		}
		return null;
	}

	public void saveEstadoCheckListAtual() {
		this.contratoCobrancaCheckList = new ContratoCobranca();

		this.contratoCobrancaCheckList.setInicioAnalise(this.objetoContratoCobranca.isInicioAnalise());
		this.contratoCobrancaCheckList.setCadastroAprovadoValor(this.objetoContratoCobranca.getCadastroAprovadoValor());
		this.contratoCobrancaCheckList
				.setMatriculaAprovadaValor(this.objetoContratoCobranca.getMatriculaAprovadaValor());
		this.contratoCobrancaCheckList.setPagtoLaudoConfirmada(this.objetoContratoCobranca.isPagtoLaudoConfirmada());
		this.contratoCobrancaCheckList.setLaudoRecebido(this.objetoContratoCobranca.isLaudoRecebido());
		this.contratoCobrancaCheckList.setPajurFavoravel(this.objetoContratoCobranca.isPajurFavoravel());
		this.contratoCobrancaCheckList.setDocumentosCompletos(this.objetoContratoCobranca.isDocumentosCompletos());
		this.contratoCobrancaCheckList.setCcbPronta(this.objetoContratoCobranca.isCcbPronta());
		this.contratoCobrancaCheckList.setStatusContrato(this.objetoContratoCobranca.getStatusContrato());
	}

	public boolean hasBaixaParcial() {
		for (ContratoCobrancaDetalhes ccd : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			if (ccd.getListContratoCobrancaDetalhesParcial().size() > 0) {
				return true;
			}
		}
		return false;
	}

	public String clearFieldsEditar() {
		this.tituloPainel = "Editar";

		this.hasBaixaParcial = hasBaixaParcial();

		this.renderRecebedorFinais = false;

		if (this.objetoContratoCobranca.getRecebedorParcelaFinal1() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal2() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal3() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal4() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal5() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal6() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal7() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal8() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal9() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal10() != null) {
			this.renderRecebedorFinais = true;
		}

		files = new ArrayList<FileUploaded>();
		files = listaArquivos();
		filesInterno = new ArrayList<FileUploaded>();
		filesInterno = listaArquivosInterno();

		loadLovs();

		loadSelectedLovs();

		this.contratoGerado = true;

		this.qtdeParcelas = String.valueOf(this.objetoContratoCobranca.getQtdeParcelas());

		// Verifica se há parcelas em atraso, se sim irá colorir a linha na tela
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		Calendar dataVencimentoParcela = Calendar.getInstance(zone, locale);
		dataHoje.set(Calendar.HOUR_OF_DAY, 0);
		dataHoje.set(Calendar.MINUTE, 0);
		dataHoje.set(Calendar.SECOND, 0);
		dataHoje.set(Calendar.MILLISECOND, 0);

		for (ContratoCobrancaDetalhes ccd : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			dataVencimentoParcela.setTime(ccd.getDataVencimento());
			dataHoje.set(Calendar.HOUR_OF_DAY, 0);
			dataHoje.set(Calendar.MINUTE, 0);
			dataHoje.set(Calendar.SECOND, 0);
			dataHoje.set(Calendar.MILLISECOND, 0);

			if (dataVencimentoParcela.getTime().before(dataHoje.getTime()) && !ccd.isParcelaPaga()) {
				ccd.setParcelaVencida(true);
			}

			if (dataVencimentoParcela.getTime().equals(dataHoje.getTime()) && !ccd.isParcelaPaga()) {
				ccd.setParcelaVencendo(true);
			}
		}

		this.vlrRepasse = null;

		this.vlrRetencao = null;

		this.vlrComissao = null;

		this.vlrParcelaFinal = null;

		this.vlrRepasseFinal = null;

		this.vlrRetencaoFinal = null;

		this.vlrComissaoFinal = null;

		loadRetencaoRepasse();
		this.geraBoletoInclusaoContrato = false;
		this.fileBoleto = null;

		return "/Atendimento/Cobranca/ContratoCobrancaInserir.xhtml";
	}
	
	public void clearFieldsSegurados() {
		loadLovs();
		loadSelectedLovs();

		this.tituloPagadorRecebedorDialog = "Segurados";
		this.tipoPesquisaPagadorRecebedor = "Segurado";
		this.updatePagadorRecebedor = ":formSegurados:SeguradoresPanel";
		this.seguradoSelecionado = new Segurado();
		this.seguradoSelecionado.setPessoa(new PagadorRecebedor());

		this.contratoGerado = true;
	}
	
	public String clearFieldsDocumentoWord() {
		return "/Atendimento/Cobranca/DocumentoWord.xhtml";
	}
	
	public void geraDocumentoWord() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();

		/* Se filtro somente por numero do contrato */
		if (this.numContrato.length() == 4) {
			this.relObjetoContratoCobranca = contratoCobrancaDao.relatorioRegerarParcela("0" + this.numContrato);
		} else {
			this.relObjetoContratoCobranca = contratoCobrancaDao.relatorioRegerarParcela(this.numContrato);
		}
		
		RelatorioFinanceiroCobranca rfc =  new RelatorioFinanceiroCobranca();

		this.objetoContratoCobranca.setDataContrato(rfc.getDataContrato());
		
		this.relSelectedObjetoContratoCobranca = new RelatorioFinanceiroCobranca();

		if (this.relObjetoContratoCobranca.size() == 0) {
			this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		}
	}

	public String clearFieldsEditarReParcelar() {
		this.tituloPainel = "Editar";

		this.hasBaixaParcial = hasBaixaParcial();
		this.amortizacao = new ContratoCobrancaDetalhes();

		this.renderRecebedorFinais = false;

		if (this.objetoContratoCobranca.getRecebedorParcelaFinal1() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal2() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal3() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal4() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal5() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal6() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal7() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal8() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal9() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal10() != null) {
			this.renderRecebedorFinais = true;
		}

		files = new ArrayList<FileUploaded>();
		files = listaArquivos();
		filesInterno = new ArrayList<FileUploaded>();
		filesInterno = listaArquivosInterno();

		loadLovs();

		loadSelectedLovs();

		this.contratoGerado = true;

		this.qtdeParcelas = String.valueOf(this.objetoContratoCobranca.getQtdeParcelas());

		this.simuladorParcelas = null;

		// Verifica se há parcelas em atraso, se sim irá colorir a linha na tela
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		Calendar dataVencimentoParcela = Calendar.getInstance(zone, locale);
		dataHoje.set(Calendar.HOUR_OF_DAY, 0);
		dataHoje.set(Calendar.MINUTE, 0);
		dataHoje.set(Calendar.SECOND, 0);
		dataHoje.set(Calendar.MILLISECOND, 0);

		for (ContratoCobrancaDetalhes ccd : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			dataVencimentoParcela.setTime(ccd.getDataVencimento());
			dataHoje.set(Calendar.HOUR_OF_DAY, 0);
			dataHoje.set(Calendar.MINUTE, 0);
			dataHoje.set(Calendar.SECOND, 0);
			dataHoje.set(Calendar.MILLISECOND, 0);

			if (dataVencimentoParcela.getTime().before(dataHoje.getTime()) && !ccd.isParcelaPaga()) {
				ccd.setParcelaVencida(true);
			}

			if (dataVencimentoParcela.getTime().equals(dataHoje.getTime()) && !ccd.isParcelaPaga()) {
				ccd.setParcelaVencendo(true);
			}
		}

		this.vlrRepasse = null;

		this.vlrRetencao = null;

		this.vlrComissao = null;

		this.vlrParcelaFinal = null;

		this.vlrRepasseFinal = null;

		this.vlrRetencaoFinal = null;

		this.vlrComissaoFinal = null;

		loadRetencaoRepasse();
		this.geraBoletoInclusaoContrato = false;
		this.fileBoleto = null;

		return "/Atendimento/Cobranca/ContratoCobrancaReParcelarEditar.xhtml";
	}

	public String clearFieldsEditarAlterarParcela() {
		this.tituloPainel = "Editar";

		this.hasBaixaParcial = hasBaixaParcial();

		this.renderRecebedorFinais = false;

		if (this.objetoContratoCobranca.getRecebedorParcelaFinal1() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal2() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal3() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal4() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal5() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal6() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal7() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal8() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal9() != null
				|| this.objetoContratoCobranca.getRecebedorParcelaFinal10() != null) {
			this.renderRecebedorFinais = true;
		}

		files = new ArrayList<FileUploaded>();
		files = listaArquivos();
		filesInterno = new ArrayList<FileUploaded>();
		filesInterno = listaArquivosInterno();

		loadLovs();

		loadSelectedLovs();

		this.contratoGerado = true;

		this.qtdeParcelas = String.valueOf(this.objetoContratoCobranca.getQtdeParcelas());

		// Verifica se há parcelas em atraso, se sim irá colorir a linha na tela
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		Calendar dataVencimentoParcela = Calendar.getInstance(zone, locale);
		dataHoje.set(Calendar.HOUR_OF_DAY, 0);
		dataHoje.set(Calendar.MINUTE, 0);
		dataHoje.set(Calendar.SECOND, 0);
		dataHoje.set(Calendar.MILLISECOND, 0);

		for (ContratoCobrancaDetalhes ccd : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			dataVencimentoParcela.setTime(ccd.getDataVencimento());
			dataHoje.set(Calendar.HOUR_OF_DAY, 0);
			dataHoje.set(Calendar.MINUTE, 0);
			dataHoje.set(Calendar.SECOND, 0);
			dataHoje.set(Calendar.MILLISECOND, 0);

			if (dataVencimentoParcela.getTime().before(dataHoje.getTime()) && !ccd.isParcelaPaga()) {
				ccd.setParcelaVencida(true);
			}

			if (dataVencimentoParcela.getTime().equals(dataHoje.getTime()) && !ccd.isParcelaPaga()) {
				ccd.setParcelaVencendo(true);
			}
		}

		this.vlrRepasse = null;

		this.vlrRetencao = null;

		this.vlrComissao = null;

		this.vlrParcelaFinal = null;

		this.vlrRepasseFinal = null;

		this.vlrRetencaoFinal = null;

		this.vlrComissaoFinal = null;

		loadRetencaoRepasse();
		this.geraBoletoInclusaoContrato = false;
		this.fileBoleto = null;

		this.selectedListContratoCobrancaParcelasInvestidor = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		this.selectedListContratoCobrancaParcelasInvestidor2 = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		this.selectedListContratoCobrancaParcelasInvestidor3 = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		this.selectedListContratoCobrancaParcelasInvestidor4 = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		this.selectedListContratoCobrancaParcelasInvestidor5 = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		this.selectedListContratoCobrancaParcelasInvestidor6 = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		this.selectedListContratoCobrancaParcelasInvestidor7 = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		this.selectedListContratoCobrancaParcelasInvestidor8 = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		this.selectedListContratoCobrancaParcelasInvestidor9 = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		this.selectedListContratoCobrancaParcelasInvestidor10 = new ArrayList<ContratoCobrancaParcelasInvestidor>();

		return "/Atendimento/Cobranca/AlteraParcelasInvestidorEditar.xhtml";
	}

	public void excluirParcelasInvestidor1() {
		FacesContext context = FacesContext.getCurrentInstance();

		List<ContratoCobrancaParcelasInvestidor> listaParcelasAtualizadas = excluirParcelasInvestidor(
				this.objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor1(),
				this.selectedListContratoCobrancaParcelasInvestidor);

		if (listaParcelasAtualizadas.size() > 0) {
			this.objetoContratoCobranca.setListContratoCobrancaParcelasInvestidor1(listaParcelasAtualizadas);

			ContratoCobrancaDao cDao = new ContratoCobrancaDao();
			cDao.merge(this.objetoContratoCobranca);
		}

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Excluir Parcela(s) Investidor: Parcela(s) excluída(s) com sucesso!", ""));
	}

	public void excluirParcelasInvestidor3() {
		FacesContext context = FacesContext.getCurrentInstance();

		List<ContratoCobrancaParcelasInvestidor> listaParcelasAtualizadas = excluirParcelasInvestidor(
				this.objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor3(),
				this.selectedListContratoCobrancaParcelasInvestidor3);

		if (listaParcelasAtualizadas.size() > 0) {
			this.objetoContratoCobranca.setListContratoCobrancaParcelasInvestidor3(listaParcelasAtualizadas);

			ContratoCobrancaDao cDao = new ContratoCobrancaDao();
			cDao.merge(this.objetoContratoCobranca);
		}

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Excluir Parcela(s) Investidor: Parcela(s) excluída(s) com sucesso!", ""));
	}

	public void excluirParcelasInvestidor4() {
		FacesContext context = FacesContext.getCurrentInstance();

		List<ContratoCobrancaParcelasInvestidor> listaParcelasAtualizadas = excluirParcelasInvestidor(
				this.objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor4(),
				this.selectedListContratoCobrancaParcelasInvestidor4);

		if (listaParcelasAtualizadas.size() > 0) {
			this.objetoContratoCobranca.setListContratoCobrancaParcelasInvestidor4(listaParcelasAtualizadas);

			ContratoCobrancaDao cDao = new ContratoCobrancaDao();
			cDao.merge(this.objetoContratoCobranca);
		}

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Excluir Parcela(s) Investidor: Parcela(s) excluída(s) com sucesso!", ""));
	}

	public void excluirParcelasInvestidor5() {
		FacesContext context = FacesContext.getCurrentInstance();

		List<ContratoCobrancaParcelasInvestidor> listaParcelasAtualizadas = excluirParcelasInvestidor(
				this.objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor5(),
				this.selectedListContratoCobrancaParcelasInvestidor5);

		if (listaParcelasAtualizadas.size() > 0) {
			this.objetoContratoCobranca.setListContratoCobrancaParcelasInvestidor5(listaParcelasAtualizadas);

			ContratoCobrancaDao cDao = new ContratoCobrancaDao();
			cDao.merge(this.objetoContratoCobranca);
		}

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Excluir Parcela(s) Investidor: Parcela(s) excluída(s) com sucesso!", ""));
	}

	public void excluirParcelasInvestidor6() {
		FacesContext context = FacesContext.getCurrentInstance();

		List<ContratoCobrancaParcelasInvestidor> listaParcelasAtualizadas = excluirParcelasInvestidor(
				this.objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor6(),
				this.selectedListContratoCobrancaParcelasInvestidor6);

		if (listaParcelasAtualizadas.size() > 0) {
			this.objetoContratoCobranca.setListContratoCobrancaParcelasInvestidor6(listaParcelasAtualizadas);

			ContratoCobrancaDao cDao = new ContratoCobrancaDao();
			cDao.merge(this.objetoContratoCobranca);
		}

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Excluir Parcela(s) Investidor: Parcela(s) excluída(s) com sucesso!", ""));
	}

	public void excluirParcelasInvestidor7() {
		FacesContext context = FacesContext.getCurrentInstance();

		List<ContratoCobrancaParcelasInvestidor> listaParcelasAtualizadas = excluirParcelasInvestidor(
				this.objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor7(),
				this.selectedListContratoCobrancaParcelasInvestidor7);

		if (listaParcelasAtualizadas.size() > 0) {
			this.objetoContratoCobranca.setListContratoCobrancaParcelasInvestidor7(listaParcelasAtualizadas);

			ContratoCobrancaDao cDao = new ContratoCobrancaDao();
			cDao.merge(this.objetoContratoCobranca);
		}

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Excluir Parcela(s) Investidor: Parcela(s) excluída(s) com sucesso!", ""));
	}

	public void excluirParcelasInvestidor8() {
		FacesContext context = FacesContext.getCurrentInstance();

		List<ContratoCobrancaParcelasInvestidor> listaParcelasAtualizadas = excluirParcelasInvestidor(
				this.objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor8(),
				this.selectedListContratoCobrancaParcelasInvestidor8);

		if (listaParcelasAtualizadas.size() > 0) {
			this.objetoContratoCobranca.setListContratoCobrancaParcelasInvestidor8(listaParcelasAtualizadas);

			ContratoCobrancaDao cDao = new ContratoCobrancaDao();
			cDao.merge(this.objetoContratoCobranca);
		}

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Excluir Parcela(s) Investidor: Parcela(s) excluída(s) com sucesso!", ""));
	}

	public void excluirParcelasInvestidor9() {
		FacesContext context = FacesContext.getCurrentInstance();

		List<ContratoCobrancaParcelasInvestidor> listaParcelasAtualizadas = excluirParcelasInvestidor(
				this.objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor9(),
				this.selectedListContratoCobrancaParcelasInvestidor9);

		if (listaParcelasAtualizadas.size() > 0) {
			this.objetoContratoCobranca.setListContratoCobrancaParcelasInvestidor9(listaParcelasAtualizadas);

			ContratoCobrancaDao cDao = new ContratoCobrancaDao();
			cDao.merge(this.objetoContratoCobranca);
		}

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Excluir Parcela(s) Investidor: Parcela(s) excluída(s) com sucesso!", ""));
	}

	public void excluirParcelasInvestidor10() {
		FacesContext context = FacesContext.getCurrentInstance();

		List<ContratoCobrancaParcelasInvestidor> listaParcelasAtualizadas = excluirParcelasInvestidor(
				this.objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor10(),
				this.selectedListContratoCobrancaParcelasInvestidor10);

		if (listaParcelasAtualizadas.size() > 0) {
			this.objetoContratoCobranca.setListContratoCobrancaParcelasInvestidor10(listaParcelasAtualizadas);

			ContratoCobrancaDao cDao = new ContratoCobrancaDao();
			cDao.merge(this.objetoContratoCobranca);
		}

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Excluir Parcela(s) Investidor: Parcela(s) excluída(s) com sucesso!", ""));
	}

	public void excluirParcelasInvestidor2() {
		FacesContext context = FacesContext.getCurrentInstance();

		List<ContratoCobrancaParcelasInvestidor> listaParcelasAtualizadas = excluirParcelasInvestidor(
				this.objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor2(),
				this.selectedListContratoCobrancaParcelasInvestidor2);

		if (listaParcelasAtualizadas.size() > 0) {
			this.objetoContratoCobranca.setListContratoCobrancaParcelasInvestidor2(listaParcelasAtualizadas);

			ContratoCobrancaDao cDao = new ContratoCobrancaDao();
			cDao.merge(this.objetoContratoCobranca);
		}

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Excluir Parcela(s) Investidor: Parcela(s) excluída(s) com sucesso!", ""));
	}

	public List<ContratoCobrancaParcelasInvestidor> excluirParcelasInvestidor(
			List<ContratoCobrancaParcelasInvestidor> parcelasInvestidor,
			List<ContratoCobrancaParcelasInvestidor> parcelasSelecionadas) {
		FacesContext context = FacesContext.getCurrentInstance();

		boolean problemasParcelas = false;
		boolean parcelaExcluida = false;

		List<ContratoCobrancaParcelasInvestidor> parcelasContratoInvestidor = new ArrayList<ContratoCobrancaParcelasInvestidor>();

		// Verifica se foram selecionadas parcelas a serem excluídas
		if (parcelasSelecionadas.size() > 0) {
			// Verifica se entre as selecionadas há parcelas baixadas
			for (ContratoCobrancaParcelasInvestidor inv : parcelasSelecionadas) {
				if (inv.isBaixado()) {
					problemasParcelas = true;

					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Excluir Parcela(s) Investidor: Erro de validação: há parcelas baixadas selecionadas.",
							""));

					break;
				}
			}

			// se está tudo Ok para excluir, faz a exclusão das parcelas
			if (!problemasParcelas) {
				for (ContratoCobrancaParcelasInvestidor parcelas : parcelasInvestidor) {

					parcelaExcluida = false;

					// Cria uma lista das parcelas que continuam no contrato
					for (ContratoCobrancaParcelasInvestidor parcelasSelected : parcelasSelecionadas) {
						if (parcelasSelected.getId() == parcelas.getId()) {
							parcelaExcluida = true;
						}
					}

					if (!parcelaExcluida) {
						parcelasContratoInvestidor.add(parcelas);
					}
				}

				// Se Há lista com as parcelas que continuam no contrato do investidor
				if (parcelasContratoInvestidor.size() > 0) {
					// atualiza o número da parcela, no caso de exclusão no meio do contrato
					int numeroParcela = 0;
					for (ContratoCobrancaParcelasInvestidor p : parcelasContratoInvestidor) {
						numeroParcela = numeroParcela + 1;

						p.setNumeroParcela(String.valueOf(numeroParcela));
					}

					// retorna a lista atualizada.
					return parcelasContratoInvestidor;
				}
			}
		} else {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Excluir Parcela(s) Investidor: Erro de validação: não há parcelas selecionadas.", ""));
		}

		return parcelasContratoInvestidor;
	}

	public String clearFieldsRelFinanceiroBaixado() {
		this.filtrarDataVencimento = "Atualizada";
		this.relDataContratoInicio = null;
		this.relDataContratoFim = null;

		this.empresa = "TODAS";

		this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		this.selectedContratoCobrancaDetalhes = new ContratoCobrancaDetalhes();

		this.numContrato = null;

		this.contratoGerado = false;

		this.filtroRelBaixado = "inicio";

		this.relIsParcial = false;

		this.relParcial = "Baixa Total e Parcial";

		clearPagador();
		clearRecebedor();
		clearRecebedor2();
		clearRecebedor3();
		clearRecebedor4();
		clearRecebedor5();
		clearRecebedor6();
		clearRecebedor7();
		clearRecebedor8();
		clearRecebedor9();
		clearRecebedor10();
		clearResponsavel();
		clearGrupoFavorecido();

		this.grupoPagadores = false;
		clearGrupoPagadores();

		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listPagadores = pagadorRecebedorDao.findAll();
		this.listRecebedores = pagadorRecebedorDao.findAll();

		ResponsavelDao responsavelDao = new ResponsavelDao();
		this.listResponsaveis = responsavelDao.findAll();

		GruposPagadoresDao gruposPagadoresDao = new GruposPagadoresDao();
		this.listGrupoPagadores = gruposPagadoresDao.findAll();

		this.exibeSomenteFavorecidosFiltrados = "Todos";

		this.grupoFavorecidos = true;

		return "/Atendimento/Cobranca/ContratoCobrancaFinanceiroBaixado.xhtml";
	}
	
	public String clearFieldsRelFinanceiroBaixadoFIDC() {
		this.relDataContratoInicio = gerarDataHoje();
		this.relDataContratoFim = gerarDataHoje();

		this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		this.selectedContratoCobrancaDetalhes = new ContratoCobrancaDetalhes();
		this.contratoGerado = false;

		return "/Atendimento/Cobranca/ContratoCobrancaFinanceiroBaixadoFIDC.xhtml";
	}

	public String clearFieldsRelFinanceiroRecebedor() {
		this.relDataContratoInicio = null;
		this.relDataContratoFim = null;

		this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		this.selectedContratoCobrancaDetalhes = new ContratoCobrancaDetalhes();

		this.numContrato = null;

		this.contratoGerado = false;

		this.filtroRelBaixado = "inicio";

		clearRecebedor();

		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listRecebedores = pagadorRecebedorDao.findAll();

		return "/Atendimento/Cobranca/ContratoCobrancaFinanceiroRecebedor.xhtml";
	}
	
	public String clearFieldsRelAtrasoRecebedor() {
		this.relDataContratoInicio = null;
		this.relDataContratoFim = null;

		this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		this.selectedContratoCobrancaDetalhes = new ContratoCobrancaDetalhes();

		this.contratoGerado = false;

		this.filtroRelBaixado = "inicio";

		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listRecebedores = pagadorRecebedorDao.findAll();

		return "/Atendimento/Cobranca/ContratoCobrancaAtrasoRecebedor.xhtml";
	}

	public String backContratoCobrancaFinanceiro() {
		geraRelFinanceiro();
		return "/Atendimento/Cobranca/ContratoCobrancaFinanceiro.xhtml";
	}

	public String backContratoCobrancaQuitados() {
		return "/Atendimento/Cobranca/ContratoCobrancaConsultarQuitados.xhtml";
	}

	public String backContratoCobrancaConsultar() {
		return "/Atendimento/Cobranca/ContratoCobrancaConsultar.xhtml";
	}

	public String backContratoCobrancaFinanceiroBaixado() {
		geraRelFinanceiroBaixado();
		return "/Atendimento/Cobranca/ContratoCobrancaFinanceiroBaixado.xhtml";
	}

	/**
	 * @return the hasBaixaParcial
	 */
	public boolean isHasBaixaParcial() {
		return hasBaixaParcial;
	}

	/**
	 * @param hasBaixaParcial the hasBaixaParcial to set
	 */
	public void setHasBaixaParcial(boolean hasBaixaParcial) {
		this.hasBaixaParcial = hasBaixaParcial;
	}

	public String clearFieldsContratos(String empresa) {
		this.filtrarDataVencimento = "Atualizada";
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataInicio = Calendar.getInstance(zone, locale);
		this.relDataContratoInicio = dataInicio.getTime();
		this.relDataContratoFim = dataInicio.getTime();
		this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		this.selectedContratoCobrancaDetalhes = new ContratoCobrancaDetalhes();

		clearPagador();
		clearRecebedor();
		clearRecebedor2();
		clearRecebedor3();
		clearRecebedor4();
		clearRecebedor5();
		clearRecebedor6();
		clearRecebedor7();
		clearRecebedor8();
		clearRecebedor9();
		clearRecebedor10();
		clearResponsavel();

		this.numContrato = null;
		
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listPagadores = pagadorRecebedorDao.findAll();
		this.listRecebedores = pagadorRecebedorDao.findAll();
		this.listRecebedoresSeleciodados = new ArrayList<PagadorRecebedor>();

		this.dualListModelRecebedores = new DualListModel<PagadorRecebedor>(listRecebedores,
				listRecebedoresSeleciodados);

		ResponsavelDao responsavelDao = new ResponsavelDao();
		this.listResponsaveis = responsavelDao.findAll();

		this.contratoGerado = false;

		this.contratos = new ArrayList<ContratoCobranca>();
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		
		this.tituloPainel = "";
		
		if (empresa.equals("Todas")) {
			this.tituloPainel = "GERAL";
			this.contratos = contratoCobrancaDao.consultaContratosUltimos10(empresa);
		}
		
		if (empresa.equals("Securitizadora")) {
			this.contratos = contratoCobrancaDao.consultaContratosUltimos10(empresa);
			stackedGroupBarModel = new BarChartModel();
			this.tituloPainel = "GALLERIA FINANÇAS SECURITIZADORA S.A.";
		}
		
		if (empresa.equals("FIDC")) {
			this.contratos = contratoCobrancaDao.consultaContratosUltimos10(empresa);
			
			clearFIDC();
			stackedGroupBarModel = new BarChartModel();
		
			this.tituloPainel = "FIDC GALLERIA";
		}
		
		return "/Atendimento/Cobranca/ContratoCobrancaConsultar.xhtml";
	}
	
	public String clearFieldsContratosPerformance() {
		this.tipoParametroConsultaContrato = "numeroContrato";
		this.parametroConsultaContrato = null;
		this.empresa = "Todas";

		this.contratoGerado = false;

		this.contratos = new ArrayList<ContratoCobranca>();
		
		this.tituloPainel = ""; 
		
		return "/Atendimento/Cobranca/ContratoCobrancaConsultarPerformance.xhtml";
	}
	
	public void clearFiltersConsultaContratosPerformance() {
		this.tipoParametroConsultaContrato = "numeroContrato";
		this.parametroConsultaContrato = null;
		this.contratoGerado = false;
		this.empresa = "Todas";
		
		this.contratos = new ArrayList<ContratoCobranca>();
	}
	
	public void geraConsultaContratosPerformance() {
		
		if (this.empresa.equals("Todas")) {
			this.tituloPainel = "GERAL";
		}
		
		if (this.empresa.equals("Securitizadora")) {
			this.tituloPainel = "GALLERIA FINANÇAS SECURITIZADORA S.A.";
		}
		
		if (this.empresa.equals("FIDC")) {
			this.tituloPainel = "FIDC GALLERIA";
		}
		
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.contratos = new ArrayList<ContratoCobranca>();

		// Busca Contratos com Parcelas que vencem no dia atual
		if (this.tipoParametroConsultaContrato.equals("numeroContrato")) {
			if (this.parametroConsultaContrato.length() == 4) {
				this.parametroConsultaContrato = "0" + this.parametroConsultaContrato;
			}
		}

		this.contratos = contratoCobrancaDao.consultaContratosPerformance(this.tipoParametroConsultaContrato, this.parametroConsultaContrato, this.empresa);
	}
	
	public void changeStatusConsultaContratosPerformance() {
		this.parametroConsultaContrato = null;
	}
	
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
	
	private Collection<ContratoCobranca> contratosGraficoFidc;
	
	private BigDecimal totalAVencer;
	
	private BarChartModel stackedGroupBarModel;

	public void clearFIDC() {
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
	}
	
	public void consultaDadosFIDC() {
		clearFIDC();
		
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
		
		this.contratos = contratoCobrancaDao.consultaContratos("FIDC");	
		this.totalContratosConsultar = this.contratos.size();
		
		contratosGraficoFidc = new ArrayList<ContratoCobranca>();
		
		int qtdContratosSemIPCA = 0;
		int qtdContratosComIPCA = 0;
		
		BigDecimal ltv = BigDecimal.ZERO;
			

		for(ContratoCobranca contrato : this.contratos) {
			this.qtdDeparcelasVencidas = 0;
			for (ContratoCobrancaDetalhes ccd : contrato.getListContratoCobrancaDetalhes()) {
				dataVencimentoParcela.setTime(ccd.getDataVencimento());		
				
				if (dataVencimentoParcela.getTime().before(dataHoje.getTime()) && !ccd.isParcelaPaga()) {
					ccd.setParcelaVencida(true);
				}

				else if (dataVencimentoParcela.getTime().equals(dataHoje.getTime()) && !ccd.isParcelaPaga()) {
					ccd.setParcelaVencendo(true);
				}
				
				if (ccd.isParcelaPaga()) {
					this.valorUltimaPareclaPaga = ccd.getVlrSaldoParcela();
					this.prazoContrato = contrato.getQtdeParcelas() - CommonsUtil.intValue(ccd.getNumeroParcela());
				} else if (ccd.isParcelaVencida()) {
					if(dataVencimentoParcela.after(dataVencimentoMínima)) {
						this.qtdDeparcelasVencidas++;
					}
					this.totalAVencer = this.totalAVencer.add(ccd.getVlrJurosParcela().add(ccd.getVlrAmortizacaoParcela()));
				}  else {
					this.totalAVencer = this.totalAVencer.add(ccd.getVlrJurosParcela().add(ccd.getVlrAmortizacaoParcela()));
				}
				
				if(CommonsUtil.mesmoValor(ccd.getDataVencimento().getMonth(), dataAtual.getMonth()) && CommonsUtil.mesmoValor(ccd.getDataVencimento().getYear(), dataAtual.getYear()) && !CommonsUtil.semValor(contrato.getValorImovel())) {
					ltv = ccd.getVlrSaldoParcela().divide(contrato.getValorImovel(), MathContext.DECIMAL128);
				}
			}
			
			if(CommonsUtil.mesmoValor(this.prazoContrato, 0) || CommonsUtil.mesmoValor(this.valorUltimaPareclaPaga, BigDecimal.ZERO)) {
				this.totalContratosConsultar--;
				this.valorUltimaPareclaPaga = BigDecimal.ZERO;
			} else {
				contratosGraficoFidc.add(contrato);
				
				prazoMedio = prazoMedio.add(BigDecimal.valueOf(prazoContrato));
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
					taxaMediaIPCA = taxaMediaIPCA.add(contrato.getTxJurosParcelas());
					qtdContratosComIPCA++;
				} else {
					if (taxaMax.compareTo(contrato.getTxJurosParcelas()) == -1){
						taxaMax = contrato.getTxJurosParcelas();
					}
					if (taxaMin.compareTo(contrato.getTxJurosParcelas()) == 1){
						taxaMin = contrato.getTxJurosParcelas();
					}
					taxaMedia = taxaMedia.add(contrato.getTxJurosParcelas());
					qtdContratosSemIPCA++;
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
		this.taxaMedia = taxaMedia.divide(BigDecimal.valueOf(qtdContratosSemIPCA),  MathContext.DECIMAL128);
		this.taxaMediaIPCA = taxaMediaIPCA.divide(BigDecimal.valueOf(qtdContratosComIPCA),  MathContext.DECIMAL128);
		this.ltvMedio = ltvMedio.divide(BigDecimal.valueOf(totalContratosConsultar),  MathContext.DECIMAL128);
		
		this.ltvMedio = this.ltvMedio.multiply(BigDecimal.valueOf(100));
		this.ltvMax = this.ltvMax.multiply(BigDecimal.valueOf(100));
		this.ltvMin = this.ltvMin.multiply(BigDecimal.valueOf(100));
		
		this.prazoMedio = this.prazoMedio.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.taxaMedia = this.taxaMedia.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.taxaMediaIPCA = this.taxaMediaIPCA.setScale(2, BigDecimal.ROUND_HALF_UP);
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
	
	public StreamedContent geraRelatorioFIDC() throws IOException{
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

//			ContratoCobranca contrato = contratos.iterator().next();
			
			ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
//			ContratoCobranca contrato = contratoCobrancaDao.findByFilter("numeroContrato", "06445").get(0);
			
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
				
				for (ContratoCobranca contrato : this.contratosGraficoFidc) {
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
			
			gerador.open(String.format("Galleria Bank - Relatorio FIDC %s.xlsx", ""));
			gerador.feed( new ByteArrayInputStream(fileOut.toByteArray()));
			gerador.close();
			
			return null;
	}
	
	public void createStackedGroupBarModel() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		
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
		Calendar dataVencimentoParcela = Calendar.getInstance(zone, locale);
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
			
			for (ContratoCobranca contrato : this.contratosGraficoFidc) {
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
	

	public void geraConsultaPreContratosBaixados() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.contratosPendentes = new ArrayList<ContratoCobranca>();
		String numeroContrato = "";

		if (this.numContrato.length() == 4) {
			numeroContrato = "0" + this.numContrato;
		} else {
			numeroContrato = this.numContrato;
		}
		
		this.contratosPendentes = contratoCobrancaDao.consultaPreContratosBaixados(numeroContrato);
	}
	
	public void geraConsultaContratosReprovadosPorContrato() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.contratosPendentes = new ArrayList<ContratoCobranca>();
		String numeroContrato = "";

		if (this.numContrato.length() == 4) {
			numeroContrato = "0" + this.numContrato;
		} else {
			numeroContrato = this.numContrato;
		}
		
		this.contratosPendentes = contratoCobrancaDao.consultaContratosReprovados(numeroContrato);
	}
	
	public void geraConsultaContratos() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.contratos = new ArrayList<ContratoCobranca>();

		// Busca Contratos com Parcelas que vencem no dia atual
		String numeroContrato = "";

		if (this.numContrato.length() == 4) {
			numeroContrato = "0" + this.numContrato;
		} else {
			numeroContrato = this.numContrato;
		}

		clearRecebedor();
		clearRecebedor2();
		clearRecebedor3();
		clearRecebedor4();
		clearRecebedor5();
		clearRecebedor6();
		clearRecebedor7();
		clearRecebedor8();
		clearRecebedor9();
		clearRecebedor10();

		if (this.selectedPagador != null && this.selectedPagador.getId() > 0) {
			populateSelectedPagador();
		}
		/*
		 * if (this.selectedResponsavel != null && this.selectedResponsavel.getId() > 0)
		 * { populateSelectedResponsavel(); }
		 */

		if (this.dualListModelRecebedores.getTarget().size() > 0) {
			int countRecebedores = 0;
			for (PagadorRecebedor pessoa : this.dualListModelRecebedores.getTarget()) {
				countRecebedores = countRecebedores + 1;

				if (countRecebedores == 1) {
					this.selectedRecebedor = pessoa;
					populateSelectedRecebedor();
				}
				if (countRecebedores == 2) {
					this.selectedRecebedor2 = pessoa;
					populateSelectedRecebedor2();
				}
				if (countRecebedores == 3) {
					this.selectedRecebedor3 = pessoa;
					populateSelectedRecebedor3();
				}
				if (countRecebedores == 4) {
					this.selectedRecebedor4 = pessoa;
					populateSelectedRecebedor4();
				}
				if (countRecebedores == 5) {
					this.selectedRecebedor5 = pessoa;
					populateSelectedRecebedor5();
				}
				if (countRecebedores == 6) {
					this.selectedRecebedor6 = pessoa;
					populateSelectedRecebedor6();
				}
				if (countRecebedores == 7) {
					this.selectedRecebedor7 = pessoa;
					populateSelectedRecebedor7();
				}
				if (countRecebedores == 8) {
					this.selectedRecebedor8 = pessoa;
					populateSelectedRecebedor8();
				}
				if (countRecebedores == 9) {
					this.selectedRecebedor9 = pessoa;
					populateSelectedRecebedor9();
				}
				if (countRecebedores == 10) {
					this.selectedRecebedor10 = pessoa;
					populateSelectedRecebedor10();
				}
			}
		}

		this.contratos = contratoCobrancaDao.consultaContratosNaoGalleria(numeroContrato, this.getIdPagador(),
				this.getIdRecebedor(), this.getIdRecebedor2(), this.getIdRecebedor3(), this.getIdRecebedor4(),
				this.getIdRecebedor5(), this.getIdRecebedor6(), this.getIdRecebedor7(), this.getIdRecebedor8(),
				this.getIdRecebedor9(), this.getIdRecebedor10(), this.tituloPainel);
	}

	public void clearFiltersConsultaContratos() {
		clearPagador();
		// clearResponsavel();
		clearRecebedor();
		clearRecebedor2();
		clearRecebedor3();
		clearRecebedor4();
		clearRecebedor5();
		clearRecebedor6();
		clearRecebedor7();
		clearRecebedor8();
		clearRecebedor9();
		clearRecebedor10();

		this.listRecebedoresSeleciodados = new ArrayList<PagadorRecebedor>();

		this.dualListModelRecebedores = new DualListModel<PagadorRecebedor>(listRecebedores,
				listRecebedoresSeleciodados);
	}

	public String clearFieldsRelFinanceiro() {
		this.filtrarDataVencimento = "Atualizada";
		this.relIsRelAtraso = false;
		this.relIsCompleto = true;
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataInicio = Calendar.getInstance(zone, locale);
		this.relDataContratoInicio = dataInicio.getTime();
		this.relDataContratoFim = dataInicio.getTime();
		this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		this.selectedContratoCobrancaDetalhes = new ContratoCobrancaDetalhes();

		clearPagador();
		clearRecebedor();
		clearRecebedor2();
		clearRecebedor3();
		clearRecebedor4();
		clearRecebedor5();
		clearRecebedor6();
		clearRecebedor7();
		clearRecebedor8();
		clearRecebedor9();
		clearRecebedor10();
		clearResponsavel();
		clearGrupoFavorecido();
		clearGrupoPagadores();

		this.grupoPagadores = false;
		this.tipoFiltros = true;

		this.numContrato = null;

		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listPagadores = pagadorRecebedorDao.findAll();
		this.listRecebedores = pagadorRecebedorDao.findAll();

		ResponsavelDao responsavelDao = new ResponsavelDao();
		this.listResponsaveis = responsavelDao.findAll();

		GruposFavorecidosDao gruposFavorecidosDao = new GruposFavorecidosDao();
		this.listGrupoFavorecido = gruposFavorecidosDao.findAll();

		GruposPagadoresDao gruposPagadoresDao = new GruposPagadoresDao();
		this.listGrupoPagadores = gruposPagadoresDao.findAll();

		// this.listContratoCobrancaFavorecidos = new
		// ArrayList<ContratoCobrancaFavorecidos>();
		// this.listContratoCobrancaFavorecidos.add(new ContratoCobrancaFavorecidos());

		this.contratoGerado = false;

		this.exibeSomenteFavorecidosFiltrados = "Todos";

		this.grupoFavorecidos = true;

		return "/Atendimento/Cobranca/ContratoCobrancaFinanceiro.xhtml";
	}

	
	public String clearFieldsRelFinanceiroAtraso() {
		
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataInicio = Calendar.getInstance(zone, locale);
		this.relDataContratoInicio = dataInicio.getTime();
		this.relDataContratoFim = dataInicio.getTime();
		
		this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		this.selectedContratoCobrancaDetalhes = new ContratoCobrancaDetalhes();

		this.contratoGerado = false;

		return "/Atendimento/Cobranca/ContratoCobrancaFinanceiroAtraso.xhtml";
	}
	

	public String clearFieldsRelFinanceiroContabilidade() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataInicio = Calendar.getInstance(zone, locale);
		this.relDataContratoInicio = dataInicio.getTime();
		this.relDataContratoFim = dataInicio.getTime();
		this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();

		this.contratoGerado = false;

		return "/Atendimento/Cobranca/ContratoCobrancaFinanceiroContabilidade.xhtml";
	}

	public String clearFieldsRelFinanceiroDia(String tipoContratoCobrancaFinanceiroDia) {
		this.relDataContratoInicio = gerarDataHoje();
		this.contratoGerado = false;
		this.contratoCobrancaFinanceiroDia = new ArrayList<ContratoCobranca>();

		this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		this.selectedContratoCobrancaDetalhes = new ContratoCobrancaDetalhes();
		
		this.selectedContratoCobrancaFinanceiroDia = new ArrayList<ContratoCobranca>();
		
		this.tipoContratoCobrancaFinanceiroDia = tipoContratoCobrancaFinanceiroDia;

		return "/Atendimento/Cobranca/ContratoCobrancaFinanceiroDia.xhtml";
	}

	public void selectedRecebedores() {

	}

	public void selecaoGrupoFavorecidos() {
		if (this.grupoFavorecidos) {
			clearRecebedor();
			clearRecebedor2();
			clearRecebedor3();
			clearRecebedor4();
			clearRecebedor5();
			clearRecebedor6();
			clearRecebedor7();
			clearRecebedor8();
			clearRecebedor9();
			clearRecebedor10();
		} else {
			clearGrupoFavorecido();
		}
	}

	public void selecaoGrupoPagadores() {
		if (this.grupoPagadores) {
			clearPagador();
			this.grupoPagadores = true;
		} else {
			clearGrupoPagadores();
			this.grupoPagadores = false;
		}
	}

	public void clearGrupoPagadores() {
		this.idGrupoPagador = 0;
		this.nomeGrupoPagador = null;
		this.selectedGrupoPagadores = new GruposPagadores();
	}

	public final void populateSelectedGrupoPagador() {
		this.idGrupoPagador = this.selectedGrupoPagadores.getId();
		this.nomeGrupoPagador = this.selectedGrupoPagadores.getNomeGrupo();
	}

	public void habilitaFiltros() {
		if (this.tipoFiltros) {
			this.numContrato = null;

			TimeZone zone = TimeZone.getDefault();
			Locale locale = new Locale("pt", "BR");
			Calendar dataInicio = Calendar.getInstance(zone, locale);
			this.relDataContratoInicio = dataInicio.getTime();
			this.relDataContratoFim = dataInicio.getTime();

			this.empresa = "TODAS";
		} else {
			clearPagador();
			clearRecebedor();
			clearRecebedor2();
			clearRecebedor3();
			clearRecebedor4();
			clearRecebedor5();
			clearRecebedor6();
			clearRecebedor7();
			clearRecebedor8();
			clearRecebedor9();
			clearRecebedor10();
			clearResponsavel();
			clearGrupoFavorecido();

			this.relDataContratoInicio = null;
			this.relDataContratoFim = null;

			this.empresa = "TODAS";
		}
	}

	public void habilitaFiltrosBaixados() {
		if (this.filtroRelBaixado.equals("Contrato")) {
			this.relDataContratoInicio = null;
			this.relDataContratoFim = null;
		} else {
			TimeZone zone = TimeZone.getDefault();
			Locale locale = new Locale("pt", "BR");
			Calendar dataInicio = Calendar.getInstance(zone, locale);
			this.relDataContratoInicio = dataInicio.getTime();
			this.relDataContratoFim = dataInicio.getTime();
			this.numContrato = null;
		}
	}
	
	public void geraRelFinanceiroBaixado() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

		if (this.filtroRelBaixado.equals("Contrato")) {
			if (this.numContrato.length() == 4) {
				this.numContrato = "0" + this.numContrato;
			} else {
				this.numContrato = this.numContrato;
			}
		}

		if (this.grupoFavorecidos) {
			converteGrupoFavorecidos();
		}

		if (this.relParcial.equals("Baixa Parcial")) {
			if (this.filtroRelBaixado.equals("Contrato")) {
				this.relObjetoContratoCobranca = contratoCobrancaDao.relatorioFinanceiroBaixadoParcialContrato(
						this.numContrato, this.getIdPagador(), this.getIdRecebedor(), this.getIdRecebedor2(),
						this.getIdRecebedor3(), this.getIdRecebedor4(), this.getIdRecebedor5(), this.getIdRecebedor6(),
						this.getIdRecebedor7(), this.getIdRecebedor8(), this.getIdRecebedor9(), this.getIdRecebedor10(),
						this.getIdResponsavel());
			} else {
				TimeZone zone = TimeZone.getDefault();
				Locale locale = new Locale("pt", "BR");

				Calendar cFim = Calendar.getInstance(zone, locale);
				cFim.setTime(this.relDataContratoFim);
				cFim.add(Calendar.DATE, 1);
				this.relDataContratoFim = cFim.getTime();

				this.relObjetoContratoCobranca = contratoCobrancaDao.relatorioFinanceiroBaixadoParcialPeriodo(
						this.relDataContratoInicio, this.relDataContratoFim, this.getIdPagador(), this.getIdRecebedor(),
						this.getIdRecebedor2(), this.getIdRecebedor3(), this.getIdRecebedor4(), this.getIdRecebedor5(),
						this.getIdRecebedor6(), this.getIdRecebedor7(), this.getIdRecebedor8(), this.getIdRecebedor9(),
						this.getIdRecebedor10(), this.getIdResponsavel(), this.filtrarDataVencimento);
			}
		}

		if (this.relParcial.equals("Baixa Total")) {
			if (this.filtroRelBaixado.equals("Contrato")) {
				this.relObjetoContratoCobranca = contratoCobrancaDao.relatorioFinanceiroBaixadoContrato(
						this.numContrato, this.getIdPagador(), this.getIdRecebedor(), this.getIdRecebedor2(),
						this.getIdRecebedor3(), this.getIdRecebedor4(), this.getIdRecebedor5(), this.getIdRecebedor6(),
						this.getIdRecebedor7(), this.getIdRecebedor8(), this.getIdRecebedor9(), this.getIdRecebedor10(),
						this.getIdResponsavel());
			} else {
				this.relObjetoContratoCobranca = contratoCobrancaDao.relatorioFinanceiroBaixadoPeriodo(
						this.relDataContratoInicio, this.relDataContratoFim, this.getIdPagador(), this.getIdRecebedor(),
						this.getIdRecebedor2(), this.getIdRecebedor3(), this.getIdRecebedor4(), this.getIdRecebedor5(),
						this.getIdRecebedor6(), this.getIdRecebedor7(), this.getIdRecebedor8(), this.getIdRecebedor9(),
						this.getIdRecebedor10(), this.getIdResponsavel(), this.filtrarDataVencimento);
			}
		}

		if (this.relParcial.equals("Baixa Total e Parcial")) {
			if (this.filtroRelBaixado.equals("Contrato")) {
				this.relObjetoContratoCobranca = contratoCobrancaDao.relatorioFinanceiroBaixadoContratoTotal(
						this.numContrato, this.getIdPagador(), this.getIdRecebedor(), this.getIdRecebedor2(),
						this.getIdRecebedor3(), this.getIdRecebedor4(), this.getIdRecebedor5(), this.getIdRecebedor6(),
						this.getIdRecebedor7(), this.getIdRecebedor8(), this.getIdRecebedor9(), this.getIdRecebedor10(),
						this.getIdResponsavel());
			} else {
				this.relObjetoContratoCobranca = contratoCobrancaDao.relatorioFinanceiroBaixadoPeriodoTotal(
						this.relDataContratoInicio, this.relDataContratoFim, this.getIdPagador(), this.getIdRecebedor(),
						this.getIdRecebedor2(), this.getIdRecebedor3(), this.getIdRecebedor4(), this.getIdRecebedor5(),
						this.getIdRecebedor6(), this.getIdRecebedor7(), this.getIdRecebedor8(), this.getIdRecebedor9(),
						this.getIdRecebedor10(), this.getIdResponsavel(), this.filtrarDataVencimento);
			}
		}

		this.relSelectedObjetoContratoCobranca = new RelatorioFinanceiroCobranca();

		if (this.relObjetoContratoCobranca.size() == 0) {
			this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		}

		this.contratoGerado = false;
	}

	public void geraRelFinanceiroBaixadoFIDC() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		
		this.relObjetoContratoCobranca = contratoCobrancaDao.relatorioFinanceiroBaixadoPeriodoTotalFIDC(
				this.relDataContratoInicio, this.relDataContratoFim);

		this.relSelectedObjetoContratoCobranca = new RelatorioFinanceiroCobranca();

		if (this.relObjetoContratoCobranca.size() == 0) {
			this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		}

		this.contratoGerado = false;
	}

	public void geraRelFinanceiroRecebedor() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

		if (this.filtroRelBaixado.equals("Contrato")) {
			String numContrato = "";
			/* Se filtro somente por numero do contrato */
			if (this.numContrato.length() == 4) {
				numContrato = "0" + this.numContrato;
			} else {
				numContrato = this.numContrato;
			}

			this.relObjetoContratoCobranca = contratoCobrancaDao.relatorioFinanceiroRecebedorContrato(numContrato,
					this.getIdRecebedor());
		} else {
			TimeZone zone = TimeZone.getDefault();
			Locale locale = new Locale("pt", "BR");

			Calendar cInicio = Calendar.getInstance(zone, locale);
			cInicio.setTime(this.relDataContratoInicio);
			cInicio.add(Calendar.DATE, -1);
			this.relDataContratoInicio = cInicio.getTime();

			Calendar cFim = Calendar.getInstance(zone, locale);
			cFim.setTime(this.relDataContratoFim);
			cFim.add(Calendar.DATE, 1);
			this.relDataContratoFim = cFim.getTime();

			this.relObjetoContratoCobranca = contratoCobrancaDao.relatorioFinanceiroRecebedorPeriodo(
					this.relDataContratoInicio, this.relDataContratoFim, this.getIdRecebedor());
		}

		this.relSelectedObjetoContratoCobranca = new RelatorioFinanceiroCobranca();

		if (this.relObjetoContratoCobranca.size() == 0) {
			this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		}

		this.contratoGerado = false;
	}
	
	public void geraRelAtrasoRecebedor() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

			TimeZone zone = TimeZone.getDefault();
			Locale locale = new Locale("pt", "BR");

			Calendar cInicio = Calendar.getInstance(zone, locale);
			cInicio.setTime(this.relDataContratoInicio);
			cInicio.add(Calendar.DATE, -1);
			this.relDataContratoInicio = cInicio.getTime();

			Calendar cFim = Calendar.getInstance(zone, locale);
			cFim.setTime(this.relDataContratoFim);
			cFim.add(Calendar.DATE, 1);
			this.relDataContratoFim = cFim.getTime();

			this.relObjetoContratoCobranca = contratoCobrancaDao.relatorioFinanceiroRecebedorAtraso(
					this.relDataContratoInicio, this.relDataContratoFim);

		this.relSelectedObjetoContratoCobranca = new RelatorioFinanceiroCobranca();

		if (this.relObjetoContratoCobranca.size() == 0) {
			this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		}

		this.contratoGerado = false;
	}

	public String geraConsultaContratosPendentes() {
	//	if (this.preContratoCustom) {

		//	crmmb = new CRMMB();
		//	crmmb.geraConsultaContratosTodos();

		//	return "/Atendimento/Cobranca/ContratoCobrancaPreCustomizadoConsultar.xhtml";
		//} else {
			ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
			this.contratosPendentes = new ArrayList<ContratoCobranca>();

			if (loginBean != null) {
				User usuarioLogado = new User();
				UserDao u = new UserDao();
				usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

				if (usuarioLogado != null) {
					if (usuarioLogado.isAdministrador()) {
						this.contratosPendentes = contratoCobrancaDao.consultaContratosPendentes(null);
					} else {
						if (usuarioLogado.getListResponsavel().size() > 0) {
							this.contratosPendentes = contratoCobrancaDao.consultaContratosPendentesResponsaveis(
									usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel());
						} else {
							this.contratosPendentes = contratoCobrancaDao
									.consultaContratosPendentes(usuarioLogado.getCodigoResponsavel());
						}
					}
				}
			}
			this.contratosPendentes = populaStatus(this.contratosPendentes);

			return "/Atendimento/Cobranca/ContratoCobrancaConsultarPendentes.xhtml";
		//}
	}
	
	 public void clearFieldsRelatorioComercial(){
		   	this.dataInicio = null;
			this.dataFim = null;
			this.selectedResponsavel = new Responsavel();
			ResponsavelDao rDao = new ResponsavelDao();
			FacesContext context = FacesContext.getCurrentInstance();
			ResponsavelDao responsavelDao = new ResponsavelDao();
			
			this.updateResponsavel = ":form:relatorioComercial ";

			if (loginBean != null) {
				User usuarioLogado = new User();
				UserDao u = new UserDao();
				usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

				if (usuarioLogado != null) {
					if (usuarioLogado.isAdministrador()) {
						this.listResponsavel = rDao.findAll();
					} else {
						if (usuarioLogado.getListResponsavel().size() > 0) {
							this.listResponsavel = usuarioLogado.getListResponsavel();
						} else {
							context.addMessage(null,
								new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Lista de Responsaveis Inválida",""));
						}
						
						if(!CommonsUtil.semValor(usuarioLogado.getCodigoResponsavel())) {
							if (responsavelDao.findByFilter("codigo", usuarioLogado.getCodigoResponsavel()).size() >= 0) {
								if(!this.listResponsavel.contains(responsavelDao.findByFilter("codigo", usuarioLogado.getCodigoResponsavel()).get(0))) {
									this.listResponsavel.add(responsavelDao.findByFilter("codigo", usuarioLogado.getCodigoResponsavel()).get(0));
								}
							}
						}
					}	
				}
			}		
			this.listaContratos = new ArrayList<ContratoCobranca>();
	    }
	   
	   public StreamedContent geraRelatorioComercial() throws IOException{
		   ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		   this.listaContratos = contratoCobrancaDao.getDashboardContratosParaRelatorio(this.dataInicio, this.dataFim, this.selectedResponsavel.getCodigo(), false);
	   
		   XSSFWorkbook wb = new XSSFWorkbook(getClass().getResourceAsStream("/resource/TabelaVazia.xlsx"));
			int iLinha = 0;
			int numeroLista = this.listaContratos.size();
			
			XSSFSheet sheet = wb.getSheetAt(0);
			
			XSSFRow linha = sheet.getRow(iLinha);
			if(linha == null) {
				sheet.createRow(iLinha);
				linha = sheet.getRow(iLinha);
			}
			
			gravaCelula(0, "", linha);
			gravaCelula(1, "OP.", linha);
			gravaCelula(2, "Data", linha);
			gravaCelula(3, "Indicador", linha);
			gravaCelula(4, "Cliente", linha);
			gravaCelula(5, "Gerente", linha);
			
			iLinha = 1;
			
			for (ContratoCobranca contrato : this.listaContratos) {
				
				linha = sheet.getRow(iLinha);
				if(linha == null) {
					sheet.createRow(iLinha);
					linha = sheet.getRow(iLinha);
				}
				
				gravaCelula(0, numeroLista, linha);
				gravaCelula(1, contrato.getNumeroContrato(), linha);
				gravaCelula(2, contrato.getDataContrato(), linha);
				gravaCelula(3, contrato.getResponsavel().getNome(), linha);
				gravaCelula(4, contrato.getPagador().getNome(), linha);
				gravaCelula(5, contrato.getResponsavel().getDonoResponsavel().getNome(), linha);
				
				iLinha++;
				numeroLista--;
			}
			
			ByteArrayOutputStream  fileOut = new ByteArrayOutputStream ();
			//escrever tudo o que foi feito no arquivo
			wb.write(fileOut);

			//fecha a escrita de dados nessa planilha
			wb.close();
			
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
					FacesContext.getCurrentInstance());
			
			gerador.open(String.format("Galleria Bank - Relatorio Comercial " + selectedResponsavel.getNome() +" %s.xlsx", ""));
			gerador.feed( new ByteArrayInputStream(fileOut.toByteArray()));
			gerador.close();
			
			return null;
	   }
	   
	   public StreamedContent geraRelatorioComercialAdministrador() throws IOException {

			ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

			XSSFWorkbook wb = new XSSFWorkbook(getClass().getResourceAsStream("/resource/TabelaVazia.xlsx"));
			int iLinha = 0;
			
			XSSFSheet sheet = wb.getSheetAt(0);

			XSSFRow linha = sheet.getRow(iLinha);
			if (linha == null) {
				sheet.createRow(iLinha);
				linha = sheet.getRow(iLinha);
			}

			gravaCelula(0, "", linha);
			gravaCelula(1, "OP.", linha);
			gravaCelula(2, "Data", linha);
			gravaCelula(3, "Indicador", linha);
			gravaCelula(4, "Cliente", linha);
			gravaCelula(5, "Gerente", linha);

			iLinha = 1;

			for (Responsavel resp : listResponsavel) {
				
				this.listaContratos = contratoCobrancaDao.getDashboardContratosParaRelatorio(this.dataInicio,this.dataFim, resp.getCodigo(), true);
				int numeroLista = this.listaContratos.size();
				for (ContratoCobranca contrato : this.listaContratos) {
					linha = sheet.getRow(iLinha);
					if (linha == null) {
						sheet.createRow(iLinha);
						linha = sheet.getRow(iLinha);
					}

					gravaCelula(0, numeroLista, linha);
					gravaCelula(1, contrato.getNumeroContrato(), linha);
					gravaCelula(2, contrato.getDataContrato(), linha);
					gravaCelula(3, contrato.getResponsavel().getNome(), linha);
					gravaCelula(4, contrato.getPagador().getNome(), linha);
					gravaCelula(5, contrato.getResponsavel().getDonoResponsavel().getNome(), linha);

					iLinha++;
					numeroLista--;
				}
			}

			ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
			// escrever tudo o que foi feito no arquivo
			wb.write(fileOut);

			// fecha a escrita de dados nessa planilha
			wb.close();

			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
					FacesContext.getCurrentInstance());

			gerador.open(String.format("Galleria Bank - Relatorio Responsaveis %s.xlsx", ""));
			gerador.feed(new ByteArrayInputStream(fileOut.toByteArray()));
			gerador.close();

			return null;
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
		
		private void gravaCelula(Integer celula, Date value, XSSFRow linha) {
			if (linha.getCell(celula) == null)
				linha.createCell(celula);
			linha.getCell(celula).setCellValue(value);
		}

		private void gravaCelula(Integer celula, int value, XSSFRow linha) {
			if (linha.getCell(celula) == null)
				linha.createCell(celula);
			linha.getCell(celula).setCellValue(value);
		}
	
	public Collection<ContratoCobranca> populaStatus(Collection<ContratoCobranca> contratos) {
		// POPULA STATUS
		for (ContratoCobranca c : contratos) {
			
			if (CommonsUtil.mesmoValor(c.getStatus(), "Aprovado")) {
				c.setStatus("Aprovado");
			} else if (CommonsUtil.mesmoValor(c.getStatus(), "Reprovado")) {
				c.setStatus("Reprovado");
			} else if (CommonsUtil.mesmoValor(c.getStatus(), "Baixado")) {
				c.setStatus("Baixado");
			} else if (CommonsUtil.mesmoValor(c.getStatus(), "Desistência Cliente")) {
				c.setStatus("Reprovado");
			} else {
				
				if (!CommonsUtil.semValor(c.getStatusLead())){
					if (c.getStatusLead().equals("Novo Lead")) {
						c.setStatus("Novo Lead");
					}

					if (c.getStatusLead().equals("Em Tratamento")) {
						c.setStatus("Lead em Tratamento");
					}
					
					if (c.getStatusLead().equals("Reprovado")) {
						c.setStatus("Lead Reprovado");
					}

					if (c.getStatusLead().equals("Completo") && !c.isInicioAnalise()) {
						c.setStatus("Ag. Análise");
					}

				} else {
					c.setStatus("Não Definido");
				}
				
				if (c.isInicioAnalise()) {
					c.setStatus("Em Análise");
				}

				if (c.getCadastroAprovadoValor() != null) {
					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado")) {
						c.setStatus("Em Análise");
					}
					
					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Pendente")) {
						c.setStatus("Análise Pendente");
					}

					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado")
							&& !c.isPagtoLaudoConfirmada()) {
						c.setStatus("Ag. Pagto. Laudo");
					}

					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado")
							&& c.isPagtoLaudoConfirmada() && (!c.isLaudoRecebido() || !c.isPajurFavoravel())) {
						c.setStatus("Ag. PAJU e Laudo");
					}

					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado")
							&& c.isPagtoLaudoConfirmada() && c.isLaudoRecebido() && c.isPajurFavoravel()
							&& !c.isPreAprovadoComite()) {
						c.setStatus("Pré-Comite");
					}
					
					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado")
							&& c.isPagtoLaudoConfirmada() && c.isLaudoRecebido() && c.isPajurFavoravel()
							&& c.isPreAprovadoComite() && !c.isDocumentosComite()) {
						c.setStatus("Ag. Validação DOCs");
					}

					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado")
							&& c.isPagtoLaudoConfirmada() && c.isLaudoRecebido() && c.isPajurFavoravel()
							&& c.isPreAprovadoComite() && c.isDocumentosComite() && !c.isAprovadoComite()) {
						c.setStatus("Ag. Comite");
					}

					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado")
							&& c.isPagtoLaudoConfirmada() && c.isLaudoRecebido() && c.isPajurFavoravel()
							&& c.isPreAprovadoComite() && c.isDocumentosComite() && c.isAprovadoComite() && !c.isDocumentosCompletos()) {
						c.setStatus("Ag. DOC");
					}

					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado")
							&& c.isPagtoLaudoConfirmada() && c.isLaudoRecebido() && c.isPajurFavoravel()
							&& c.isPreAprovadoComite() && c.isDocumentosComite() && c.isAprovadoComite() && c.isDocumentosCompletos()
							&& !c.isCcbPronta()) {
						c.setStatus("Ag. CCB");
					}

					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado")
							&& c.isPagtoLaudoConfirmada() && c.isLaudoRecebido() && c.isPajurFavoravel()
							&& c.isPreAprovadoComite() && c.isDocumentosComite() && c.isAprovadoComite() && c.isDocumentosCompletos()
							&& c.isCcbPronta() && c.isAgAssinatura()) {
						c.setStatus("Ag. Assinatura");
					}

					if (c.isInicioAnalise() && c.getCadastroAprovadoValor().equals("Aprovado")
							&& c.isPagtoLaudoConfirmada() && c.isLaudoRecebido() && c.isPajurFavoravel()
							&& c.isPreAprovadoComite() && c.isDocumentosComite() && c.isAprovadoComite() && c.isDocumentosCompletos()
							&& c.isCcbPronta() && !c.isAgAssinatura() && c.isAgRegistro()) {
						c.setStatus("Ag. Registro");
					}
				}
				if (c.isAnaliseReprovada()) {
					c.setStatus("Análise Reprovada");
				}
			}
		}
		return contratos;
	}
	
	public String geraConsultaContratosPorStatus(String status) {
		this.tituloTelaConsultaPreStatus = status;
		
		clearMensagensWhatsApp();
		
		if (status.equals("Lead")) {
			this.tituloTelaConsultaPreStatus = "Novo Lead";
		}
		if (status.equals("Análise Reprovada")) {
			this.tituloTelaConsultaPreStatus = "Análise Reprovada";
		}
		if (status.equals("Aguardando Análise")) {
			this.tituloTelaConsultaPreStatus = "Aguardando Análise";
		}
		if (status.equals("Em Analise")) {
			this.tituloTelaConsultaPreStatus = "Em Análise";
		}
		if (status.equals("Analise Pendente")) {
			this.tituloTelaConsultaPreStatus = "Análise Pendente";
		}
		if (status.equals("Ag. Pagto. Laudo")) {
			this.tituloTelaConsultaPreStatus = "Ag. Pagto. Laudo";
		}
		if (status.equals("Ag. PAJU e Laudo")) {
			this.tituloTelaConsultaPreStatus = "Ag. PAJU e Laudo";
		}
		//if (status.equals("Ag. DOC e Comite")) {
		//	this.tituloTelaConsultaPreStatus = "Ag. DOC";
		//}
		if (status.equals("Pré-Comite")) {
			this.tituloTelaConsultaPreStatus = "Pré-Comite";
		}
		if (status.equals("Ag. Documentos Comite")) {
			this.tituloTelaConsultaPreStatus = "Ag. Documentos Comite";
		}
		if (status.equals("Ag. Comite")) {
			this.tituloTelaConsultaPreStatus = "Ag. Comite";
		}
		if (status.equals("Ag. DOC")) {
			this.tituloTelaConsultaPreStatus = "Ag. DOC";
		}
		if (status.equals("Ag. CCB")) {
			this.tituloTelaConsultaPreStatus = "Ag. CCB";
		}
		if (status.equals("Ag. Assinatura")) {
			this.tituloTelaConsultaPreStatus = "Ag. Assinatura";
		}
		if (status.equals("Ag. Registro")) {
			this.tituloTelaConsultaPreStatus = "Ag. Registro";
		}
				
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.contratosPendentes = new ArrayList<ContratoCobranca>();
		
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		Date auxDataHoje = dataHoje.getTime();
		
		if(!status.equals("Pré-Comite")) {
			this.contratosPendentes = contratoCobrancaDao.geraConsultaContratosCRM(null, null, status);
		} else {
			if (loginBean != null) {
				User usuarioLogado = new User();
				UserDao u = new UserDao();
				usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

				if (usuarioLogado != null) {
					if (usuarioLogado.isAdministrador() || usuarioLogado.isUserPreContratoAnalista()) {
						this.contratosPendentes = contratoCobrancaDao.geraConsultaContratosCRM(null, null, status);
					} else {
						if (usuarioLogado.getListResponsavel().size() > 0) {
							this.contratosPendentes = contratoCobrancaDao.geraConsultaContratosCRM(
									usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel(), status);
						} else {
							this.contratosPendentes = contratoCobrancaDao
									.geraConsultaContratosCRM(usuarioLogado.getCodigoResponsavel(), null, status);
						}
					}
				}
			}
		}

		for (ContratoCobranca contratos : this.contratosPendentes) {			
			contratos = getContratoById(contratos.getId());

			if (status.equals("Análise Reprovada")) {
				if (contratos.getAnaliseReprovadaData() != null && contratos.isAnaliseReprovada()) {
					if (getDifferenceDays(contratos.getAnaliseReprovadaData(), auxDataHoje) > 14) {
						if (this.objetoContratoCobranca != null) {
							this.objetoContratoCobranca = contratos;
							reprovarContrato(); 
						}
					}
				}
			}

			else if (status.equals("Aguardando Análise")) {
				if (contratos.getDataContrato() != null) {
					if (getDifferenceDays(contratos.getDataContrato(), auxDataHoje) > 45) {
						if (!contratos.isContratoResgatadoBaixar()) {
							this.objetoContratoCobranca = contratos;
							baixarPreContrato();
						} else if (getDifferenceDays(contratos.getContratoResgatadoData(), auxDataHoje) > 14) {
							this.objetoContratoCobranca = contratos;
							baixarPreContrato();
						}
					}
				}
			}

			else if (status.equals("Ag. Pagto. Laudo")) {
				if (contratos.getCadastroAprovadoData() != null) {
					if (getDifferenceDays(contratos.getCadastroAprovadoData(), auxDataHoje) > 15) {
						if (!contratos.isContratoResgatadoBaixar()) {
							this.objetoContratoCobranca = contratos;
							baixarPreContrato();
						} else if (getDifferenceDays(contratos.getContratoResgatadoData(), auxDataHoje) > 14) {
							this.objetoContratoCobranca = contratos;
							baixarPreContrato();
						}

					}
				}
			}
		}

		return "/Atendimento/Cobranca/ContratoCobrancaConsultarPreStatus.xhtml";
	}
	
	public void processaResponsaveisGeraNumeroWhatsApp() {
		ResponsavelDao r = new ResponsavelDao();
		List<Responsavel> responsaveis = r.findAll();
		
		TakeBlipMB takeBlipMB = new TakeBlipMB();
		
		for (Responsavel resp : responsaveis) {			
			takeBlipMB.getWhatsAppURL(resp);
		}
	}	

	public static long getDifferenceDays(Date d1, Date d2) {
	    long diff = d2.getTime() - d1.getTime();
	    diff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	    return diff;
	}

	public String geraConsultaLeads(String statuslead) {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.contratosPendentes = new ArrayList<ContratoCobranca>();

		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.contratosPendentes = contratoCobrancaDao.consultaLeads(null, null, statuslead);
				} else {
					if (usuarioLogado.getCodigoResponsavel() != null) {
						this.contratosPendentes = contratoCobrancaDao.consultaLeads(
								usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel(), statuslead);
					}
				}
			}
		}

		if (statuslead.equals("Novo Lead")) {
			return "/Atendimento/Cobranca/ContratoCobrancaConsultarLeads.xhtml";
		}
		if (statuslead.equals("Em Tratamento")) {
			return "/Atendimento/Cobranca/ContratoCobrancaConsultarLeadsTratamento.xhtml";
		}
		if (statuslead.equals("Completo")) {
			return "/Atendimento/Cobranca/ContratoCobrancaConsultarLeadsCompletos.xhtml";
		}
		if (statuslead.equals("Reprovado")) {
			return "/Atendimento/Cobranca/ContratoCobrancaConsultarLeadsReprovados.xhtml";
		}

		return "";
	}

	public String geraConsultaContratosCustomizados() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.contratosPendentes = new ArrayList<ContratoCobranca>();

		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.contratosPendentes = contratoCobrancaDao.consultaContratosPendentes(null);
				} else {
					if (usuarioLogado.getListResponsavel().size() > 0) {
						this.contratosPendentes = contratoCobrancaDao.consultaContratosPendentesResponsaveis(
								usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel());
					} else {
						this.contratosPendentes = contratoCobrancaDao
								.consultaContratosPendentes(usuarioLogado.getCodigoResponsavel());
					}
				}
			}
		}

		return "/Atendimento/Cobranca/ContratoCobrancaPreCustomizadoConsultar.xhtml";
	}

	public String geraConsultaContratosReprovados() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.contratosPendentes = new ArrayList<ContratoCobranca>();
		this.setNumContrato("");

		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);
			
			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.contratosPendentes = contratoCobrancaDao.consultaContratosPendentesReprovados(null);
				} else {
					if (usuarioLogado.getListResponsavel().size() > 0) {
						this.contratosPendentes = contratoCobrancaDao.consultaContratosPendentesReprovadosResponsaveis(
								usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel());
					} else {
						this.contratosPendentes = contratoCobrancaDao.consultaContratosPendentesReprovados(usuarioLogado.getCodigoResponsavel());
					}
				}
			}
		}

		return "/Atendimento/Cobranca/ContratoCobrancaConsultarReprovados.xhtml";
	}
	
	public String geraConsultaContratosBaixados() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.numContrato = "";
		this.contratosPendentes = new ArrayList<ContratoCobranca>();
		

		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
 					this.contratosPendentes = contratoCobrancaDao.consultaContratosPendentesBaixados(null);
				} else {
					if (usuarioLogado.getListResponsavel().size() > 0) {
						this.contratosPendentes = contratoCobrancaDao.consultaContratosPendentesBaixadosResponsaveis(
								usuarioLogado.getCodigoResponsavel(), usuarioLogado.getListResponsavel());
					} else {
						this.contratosPendentes = contratoCobrancaDao
								.consultaContratosPendentesBaixados(usuarioLogado.getCodigoResponsavel());
					}
				}
			}
		}

		return "/Atendimento/Cobranca/ContratoCobrancaConsultarBaixados.xhtml";
	}

	public String geraConsultaContratosQuitados() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.contratosPendentes = new ArrayList<ContratoCobranca>();

		if (loginBean != null) {
			User usuarioLogado = new User();
			UserDao u = new UserDao();
			usuarioLogado = u.findByFilter("login", loginBean.getUsername()).get(0);

			if (usuarioLogado != null) {
				if (usuarioLogado.isAdministrador()) {
					this.contratosPendentes = contratoCobrancaDao.consultaContratosPendentesQuitados(null);
				} else {
					this.contratosPendentes = contratoCobrancaDao
							.consultaContratosPendentesQuitados(usuarioLogado.getCodigoResponsavel());
				}
			}
		}

		return "/Atendimento/Cobranca/ContratoCobrancaConsultarQuitados.xhtml";
	}

	public void converteGrupoFavorecidos() {
		if (this.selectedGrupoFavorecido != null) {
			if (this.selectedGrupoFavorecido.getRecebedor1() != null) {
				this.setIdRecebedor(this.selectedGrupoFavorecido.getRecebedor1().getId());
			}
			if (this.selectedGrupoFavorecido.getRecebedor2() != null) {
				this.setIdRecebedor2(this.selectedGrupoFavorecido.getRecebedor2().getId());
			}
			if (this.selectedGrupoFavorecido.getRecebedor3() != null) {
				this.setIdRecebedor3(this.selectedGrupoFavorecido.getRecebedor3().getId());
			}
			if (this.selectedGrupoFavorecido.getRecebedor4() != null) {
				this.setIdRecebedor4(this.selectedGrupoFavorecido.getRecebedor4().getId());
			}
			if (this.selectedGrupoFavorecido.getRecebedor5() != null) {
				this.setIdRecebedor5(this.selectedGrupoFavorecido.getRecebedor5().getId());
			}
			if (this.selectedGrupoFavorecido.getRecebedor6() != null) {
				this.setIdRecebedor6(this.selectedGrupoFavorecido.getRecebedor6().getId());
			}
			if (this.selectedGrupoFavorecido.getRecebedor7() != null) {
				this.setIdRecebedor7(this.selectedGrupoFavorecido.getRecebedor7().getId());
			}
			if (this.selectedGrupoFavorecido.getRecebedor8() != null) {
				this.setIdRecebedor8(this.selectedGrupoFavorecido.getRecebedor8().getId());
			}
			if (this.selectedGrupoFavorecido.getRecebedor9() != null) {
				this.setIdRecebedor9(this.selectedGrupoFavorecido.getRecebedor9().getId());
			}
			if (this.selectedGrupoFavorecido.getRecebedor10() != null) {
				this.setIdRecebedor10(this.selectedGrupoFavorecido.getRecebedor10().getId());
			}
		}
	}
	

	/*
	 * public void alteraContratoParaCartorio() { ContratoCobrancaDao
	 * contratoCobrancaDao = new ContratoCobrancaDao();
	 * this.relObjetoContratoCobranca = new
	 * ArrayList<RelatorioFinanceiroCobranca>(); List<RelatorioFinanceiroCobranca>
	 * relObjetoContratoCobrancaAux = new ArrayList<RelatorioFinanceiroCobranca>();
	 * 
	 * contratoCobrancaDao.s }
	 */
	
	public void geraRelFinanceiroAtraso() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		List<RelatorioFinanceiroCobranca> relObjetoContratoCobrancaAux = new ArrayList<RelatorioFinanceiroCobranca>();

		// Busca Contratos com Parcelas que vencem no dia atual
		relObjetoContratoCobrancaAux = contratoCobrancaDao.relatorioControleEstoqueAtrasoFull(
				this.relDataContratoInicioAtraso,this.relDataContratoFimAtraso);
		
		for (RelatorioFinanceiroCobranca parcelas : relObjetoContratoCobrancaAux) {
			// chamada para contar parcelas em atraso
			String retornoAtrasos = contratoCobrancaDao.getParcelasAtraso(gerarDataHoje(), this.filtrarDataVencimento, parcelas.getContratoCobranca().getId());
			
			int posicaoSeparador = retornoAtrasos.indexOf("||");
			
			parcelas.setQtdeAtrasos(retornoAtrasos.substring(0, posicaoSeparador));
			
			parcelas.setQtdeBaixasParciais(retornoAtrasos.substring(posicaoSeparador + 2, retornoAtrasos.length()));
		}

		// exclui o registro, quando o pagador é a Galleria SA
		/*
		if (relObjetoContratoCobrancaAux.size() > 0) {
			for (RelatorioFinanceiroCobranca r : relObjetoContratoCobrancaAux) {
				if (r.getContratoCobranca().getPagador().getId() != 14) {
					this.relObjetoContratoCobranca.add(r);
				}
			}
		}
		*/
		if (relObjetoContratoCobrancaAux.size() > 0) {
			this.relObjetoContratoCobranca = relObjetoContratoCobrancaAux;
		}
		
		processaDadosRelFinanceiroAtrasoFull();

		this.relSelectedObjetoContratoCobranca = new RelatorioFinanceiroCobranca();

		if (this.relObjetoContratoCobranca.size() == 0) {
			this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		}

		this.contratoGerado = false;
	}
	
	public void geraRelFinanceiro() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		List<RelatorioFinanceiroCobranca> relObjetoContratoCobrancaAux = new ArrayList<RelatorioFinanceiroCobranca>();

		// Busca Contratos com Parcelas que vencem no dia atual
		if (this.tipoFiltros) {
			/* Filtra por period, parcelas em atraso / no periodo, evolvidos */
			if (this.grupoFavorecidos) {
				converteGrupoFavorecidos();
			}

			if (!relIsRelAtraso) {
				relObjetoContratoCobrancaAux = contratoCobrancaDao.relatorioControleEstoque(this.relDataContratoInicio,
						this.relDataContratoFim, this.getIdPagador(), this.getIdRecebedor(), this.getIdRecebedor2(),
						this.getIdRecebedor3(), this.getIdRecebedor4(), this.getIdRecebedor5(), this.getIdRecebedor6(),
						this.getIdRecebedor7(), this.getIdRecebedor8(), this.getIdRecebedor9(), this.getIdRecebedor10(),
						this.getIdResponsavel(), this.filtrarDataVencimento, this.grupoPagadores, this.idGrupoPagador,
						this.empresa);
			} else {
				relObjetoContratoCobrancaAux = contratoCobrancaDao.relatorioControleEstoqueAtraso(
						this.relDataContratoInicio, this.relDataContratoFim, this.getIdPagador(), this.getIdRecebedor(),
						this.getIdRecebedor2(), this.getIdRecebedor3(), this.getIdRecebedor4(), this.getIdRecebedor5(),
						this.getIdRecebedor6(), this.getIdRecebedor7(), this.getIdRecebedor8(), this.getIdRecebedor9(),
						this.getIdRecebedor10(), this.getIdResponsavel(), this.filtrarDataVencimento,
						this.grupoPagadores, this.idGrupoPagador, this.empresa);

				for (RelatorioFinanceiroCobranca parcelas : relObjetoContratoCobrancaAux) {
					// chamada para contar parcelas em atraso
					String retornoAtrasos = contratoCobrancaDao.getParcelasAtraso(gerarDataHoje(),
							this.filtrarDataVencimento, parcelas.getContratoCobranca().getId());

					int posicaoSeparador = retornoAtrasos.indexOf("||");

					parcelas.setQtdeAtrasos(retornoAtrasos.substring(0, posicaoSeparador));

					parcelas.setQtdeBaixasParciais(
							retornoAtrasos.substring(posicaoSeparador + 2, retornoAtrasos.length()));
				}
			}
		} else {
			/* Se filtro somente por numero do contrato */
			if (this.numContrato.length() == 4) {
				relObjetoContratoCobrancaAux = contratoCobrancaDao
						.relatorioControleEstoqueNumContrato("0" + this.numContrato);
			} else {
				relObjetoContratoCobrancaAux = contratoCobrancaDao
						.relatorioControleEstoqueNumContrato(this.numContrato);
			}
		}

		// exclui o registro, quando o pagador é a Galleria SA
		if (relObjetoContratoCobrancaAux.size() > 0) {
			for (RelatorioFinanceiroCobranca r : relObjetoContratoCobrancaAux) {
				if (r.getContratoCobranca().getPagador().getId() != 14) {
					this.relObjetoContratoCobranca.add(r);
				}
			}
		}

		processaDadosRelFinanceiro();

		this.relSelectedObjetoContratoCobranca = new RelatorioFinanceiroCobranca();

		if (this.relObjetoContratoCobranca.size() == 0) {
			this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		}

		this.contratoGerado = false;
	}

	public void geraRelFinanceiroContabilidade() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();

		this.relObjetoContratoCobranca = contratoCobrancaDao
				.relatorioFinanceiroContabilidade(this.relDataContratoInicio, this.relDataContratoFim);

		this.relSelectedObjetoContratoCobranca = new RelatorioFinanceiroCobranca();

		if (this.relObjetoContratoCobranca.size() == 0) {
			this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		}

		this.contratoGerado = false;
	}

	public void geraRelFinanceiroDia() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.contratoCobrancaFinanceiroDia = new ArrayList<ContratoCobranca>();

		this.contratoCobrancaFinanceiroDia = contratoCobrancaDao.relatorioFinanceiroDia(this.tipoContratoCobrancaFinanceiroDia);

		// Verifica se há parcelas em atraso, se sim irá colorir a linha na tela
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		Date auxDataPagamento = dataHoje.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
		String auxDataPagamentoStr = sdf.format(dataHoje.getTime());
		try {
			auxDataPagamento = sdf.parse(auxDataPagamentoStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// onRowEdit - nova data
		rowEditNewDate = auxDataPagamento;

		
		List<ContratoCobranca> listContratos = new ArrayList<ContratoCobranca>();
		
		for (ContratoCobranca contratos : this.contratoCobrancaFinanceiroDia) {

			int countParcelas = 0;
			BigDecimal somaAmortizacoes = BigDecimal.ZERO;
			
			SimuladorMB  simuladorMB = new SimuladorMB();
			
			if (!CommonsUtil.semValor(contratos.getValorCCB())) {
				if (contratos.getListContratoCobrancaDetalhes().size() > 0) {
					if (contratos.getPagador().getCpf() != null) {
						simuladorMB.setTipoPessoa("PF");
					} else {
						simuladorMB.setTipoPessoa("PJ");
					}
					simuladorMB.setTipoCalculo(contratos.getTipoCalculo());
					simuladorMB.setValorImovel(
							contratos.getValorCCB().multiply(BigDecimal.valueOf(3), MathContext.DECIMAL128));
					if (CommonsUtil.mesmoValor(simuladorMB.getValorImovel(), BigDecimal.ZERO)) {
						simuladorMB.setValorImovel(BigDecimal.ONE);
					}
					simuladorMB.setValorCredito(contratos.getValorCCB());
					simuladorMB.setTaxaJuros(contratos.getTxJurosParcelas());
					simuladorMB.setParcelas(BigInteger.valueOf(contratos.getListContratoCobrancaDetalhes().size() + 1));
					simuladorMB.setCarencia(BigInteger.valueOf(contratos.getMesesCarencia()));
					simuladorMB.setNaoCalcularMIP(contratos.isTemSeguroMIP());
					simuladorMB.setNaoCalcularDFI(contratos.isTemSeguroMIP());
					simuladorMB.setMostrarIPCA(true);
					simuladorMB.setTipoCalculoFinal('B');
					simuladorMB.setValidar(false);
					simuladorMB.simular();
				}
			}

			for (ContratoCobrancaDetalhes ccd : contratos.getListContratoCobrancaDetalhes()) {
				
				if (ccd.isAmortizacao()) {
					somaAmortizacoes.add(ccd.getVlrParcela());
					continue;
				}
				
				// se já houve baixa parcial, utiliza a data de vencimento atualizada
				// senão utiliza a data de vencimento antiga
				String auxDataVencimentoStr = "";
				Date auxDataVencimento = null;
				if (ccd.getDataVencimentoAtual() != null) {
					auxDataVencimentoStr = sdf.format(ccd.getDataVencimentoAtual());
					auxDataVencimento = ccd.getDataVencimentoAtual();
				} else {
					auxDataVencimentoStr = sdf.format(ccd.getDataVencimento());
					auxDataVencimento = ccd.getDataVencimento();
				}

				try {
					auxDataVencimento = sdf.parse(auxDataVencimentoStr);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (auxDataVencimento.before(auxDataPagamento) && !ccd.isParcelaPaga()) {
					ccd.setParcelaVencida(true);

					// calcula coluna valor atualizado
					ContratoCobrancaUtilsMB contratoCobrancaUtilsMB;
					/*
					 * if (ccd.getVlrJuros().compareTo(BigDecimal.ZERO) == 0) {
					 * contratoCobrancaUtilsMB = new ContratoCobrancaUtilsMB( auxDataVencimento,
					 * auxDataPagamento, ccd.getVlrParcela(), BigDecimal.valueOf(1.00),
					 * ccd.getTxMulta()); } else { contratoCobrancaUtilsMB = new
					 * ContratoCobrancaUtilsMB( auxDataVencimento, auxDataPagamento,
					 * ccd.getVlrParcela(), ccd.getVlrJuros(), ccd.getTxMulta()); }
					 */
					if (ccd.getVlrJuros().compareTo(BigDecimal.ZERO) == 0) {
						contratoCobrancaUtilsMB = new ContratoCobrancaUtilsMB(auxDataVencimento, auxDataPagamento,
								ccd.getVlrParcela(), BigDecimal.valueOf(1.00),
								this.objetoContratoCobranca.getTxMulta());
					} else {
						contratoCobrancaUtilsMB = new ContratoCobrancaUtilsMB(auxDataVencimento, auxDataPagamento,
								ccd.getVlrParcela(), this.objetoContratoCobranca.getTxJuros(),
								this.objetoContratoCobranca.getTxMulta());
					}

					if (!ccd.isParcelaPaga()) {
						if (ccd.getListContratoCobrancaDetalhesParcial().size() > 0) {
							contratoCobrancaUtilsMB.recalculaValorSemMulta();
						} else {
							contratoCobrancaUtilsMB.recalculaValor();
						}
						ccd.setVlrParcelaAtualizada(contratoCobrancaUtilsMB.getValorAtualizado());
					} else {
						ccd.setVlrParcelaAtualizada(null);
					}
				}

				if (auxDataVencimento.equals(auxDataPagamento) && !ccd.isParcelaPaga()) {
					ccd.setParcelaVencendo(true);
				}

				BigDecimal somaBaixas = BigDecimal.ZERO;

				for (ContratoCobrancaDetalhesParcial cBaixas : ccd.getListContratoCobrancaDetalhesParcial()) {
					ccd.setDataUltimoPagamento(cBaixas.getDataPagamento());
					somaBaixas = somaBaixas.add(cBaixas.getVlrRecebido());
				}
				ccd.setValorTotalPagamento(somaBaixas.add(somaAmortizacoes));

				// seta valor original da parcela
				/*
				 * countParcelas = countParcelas + 1;
				 * 
				 * if (countParcelas < contratos.getListContratoCobrancaDetalhes().size()) {
				 * ccd.setVlrParcela(contratos.getVlrParcela()); } else { if
				 * (!contratos.isGeraParcelaFinal()) {
				 * ccd.setVlrParcela(contratos.getVlrParcela()); } }
				 */

			}
			BigDecimal cet = BigDecimal.ZERO;
			
			if (!CommonsUtil.semValor(simuladorMB.getSimulacao())) {
				if (!CommonsUtil.semValor(simuladorMB.getSimulacao().getCetAoMes())) {
					cet = simuladorMB.getSimulacao().getCetAoMes();
				}
			}		
			contratos.setCetMes(cet);
			
			listContratos.add(contratos);
		}
		
		this.contratoCobrancaFinanceiroDia = listContratos;

		if (this.contratoCobrancaFinanceiroDia.size() == 0) {
			this.contratoCobrancaFinanceiroDia = new ArrayList<ContratoCobranca>();
		}

		this.contratoGerado = false;
	}

	public void geraXLSFinanceiroDia() throws IOException {
		ParametrosDao pDao = new ParametrosDao();
		this.pathContrato = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomeContrato = "Relatório Financeiro Dia.xlsx";

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
		cell.setCellValue("Parcela");
		cell.setCellStyle(cell_style);
		cell = row.createCell(16);
		cell.setCellValue("Data Vencimento");
		cell.setCellStyle(cell_style);
		cell = row.createCell(17);
		cell.setCellValue("Valor");
		cell.setCellStyle(cell_style);
		cell = row.createCell(18);
		cell.setCellValue("Data Pagto.");
		cell.setCellStyle(cell_style);
		cell = row.createCell(19);
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
		
		// verifica se tem selecionados ou se serão todos
		if (this.selectedContratoCobrancaFinanceiroDia.size() == 0) {
			this.selectedContratoCobrancaFinanceiroDia = this.contratoCobrancaFinanceiroDia;
		}
		
		for (ContratoCobranca record : this.selectedContratoCobrancaFinanceiroDia) {
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
			if (tipoContratoCobrancaFinanceiroDia.equals("PreAprovado")) {
				cell.setCellValue(record.getDataContrato());
			} else {
				cell.setCellValue(record.getDataInicio());
			}

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
			if (record.getTxJurosParcelas() != null) {
				cell.setCellValue(((BigDecimal) record.getCetMes()).doubleValue());
			} else {
				cell.setCellValue(Double.valueOf("0"));
			}

			int parcelaCount = 0;
			for (ContratoCobrancaDetalhes parcelas : record.getListContratoCobrancaDetalhes()) {
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
				if (tipoContratoCobrancaFinanceiroDia.equals("PreAprovado")) {
					cell.setCellValue(record.getDataContrato());
				} else {
					cell.setCellValue(record.getDataInicio());
				}

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
				if (record.getTxJurosParcelas() != null) {
					cell.setCellValue(((BigDecimal) record.getCetMes()).doubleValue());
				} else {
					cell.setCellValue(Double.valueOf("0"));
				}

				// Parcela
				cell = row.createCell(15);
				/*
				 * if (parcelas.isParcelaPaga()) { cell.setCellStyle(cell_style_pago_String); }
				 * else { if (parcelas.isParcelaVencida()) {
				 * cell.setCellStyle(cell_style_vencida_String); } else {
				 * cell.setCellStyle(cell_style); } }
				 */
				cell.setCellStyle(cell_style);
				cell.setCellValue(parcelas.getNumeroParcela());

				// Data Vencimento
				cell = row.createCell(16);
				/*
				 * if (parcelas.isParcelaPaga()) { cell.setCellStyle(cell_style_pago_Date); }
				 * else { if (parcelas.isParcelaVencida()) {
				 * cell.setCellStyle(cell_style_vencida_Date); } else {
				 * cell.setCellStyle(dateStyle); } }
				 */
				cell.setCellStyle(dateStyle);
				cell.setCellValue(parcelas.getDataVencimento());

				// Valor Parcela
				cell = row.createCell(17);
				/*
				 * if (parcelas.isParcelaPaga()) { cell.setCellStyle(cell_style_pago_Number); }
				 * else { if (parcelas.isParcelaVencida()) {
				 * cell.setCellStyle(cell_style_vencida_Number); } else {
				 * cell.setCellStyle(numericStyle); } }
				 */
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				if (parcelas.getVlrParcela() != null) {
					cell.setCellValue(((BigDecimal) parcelas.getVlrParcela()).doubleValue());
				} else {
					cell.setCellValue(Double.valueOf("0"));
				}

				// Data pagto
				cell = row.createCell(18);
				/*
				 * if (parcelas.isParcelaPaga()) { cell.setCellStyle(cell_style_pago_Date); }
				 * else { if (parcelas.isParcelaVencida()) {
				 * cell.setCellStyle(cell_style_vencida_Date); } else {
				 * cell.setCellStyle(dateStyle); } }
				 */
				cell.setCellStyle(dateStyle);
				cell.setCellValue(parcelas.getDataUltimoPagamento());

				// Valor Pago
				cell = row.createCell(19);
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
		
		this.contratoGerado = true;

	}
	
	
	
	public void atualizaIPCA() {
		IPCADao ipcaDao = new IPCADao();
		ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
		try {
			for (RelatorioFinanceiroCobranca relatorioFinanceiraCobranca : this.relObjetoContratoCobranca) {				
				ContratoCobrancaDetalhes parcelaIpca = contratoCobrancaDetalhesDao.findById(relatorioFinanceiraCobranca.getIdParcela());
				if (calcularIPCA(ipcaDao, contratoCobrancaDetalhesDao, parcelaIpca)) {
					relatorioFinanceiraCobranca.setValor(parcelaIpca.getVlrParcela());
					contratoCobrancaDetalhesDao.update(parcelaIpca);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean calcularIPCA(IPCADao ipcaDao,
			ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao, ContratoCobrancaDetalhes contratoCobrancaDetalhes) {

		IPCA ultimoIpca = ipcaDao.getUltimoIPCA(contratoCobrancaDetalhes.getDataVencimento());
		
		// primeira condição é para meses de mesmo ano; segunda condição é para os meses
		// jan e fev da parcela IPCA
		if (contratoCobrancaDetalhes.getDataVencimento().getMonth() - ultimoIpca.getData().getMonth() <= 2
				|| contratoCobrancaDetalhes.getDataVencimento().getMonth() - ultimoIpca.getData().getMonth() <= -10) {

			ContratoCobranca contratoCobranca = contratoCobrancaDetalhesDao.getContratoCobranca(contratoCobrancaDetalhes.getId());
			//usar o reparcelamento aqui.
			
			if (contratoCobrancaDetalhes.getIpca() == null && CommonsUtil.booleanValue(contratoCobranca.isCorrigidoIPCA())) {
				BigDecimal valorIpca = (contratoCobrancaDetalhes.getVlrSaldoParcela().add(contratoCobrancaDetalhes.getVlrAmortizacaoParcela()))
						.multiply(ultimoIpca.getTaxa().divide(BigDecimal.valueOf(100)));
				contratoCobrancaDetalhes.setVlrParcela(
						(contratoCobrancaDetalhes.getVlrParcela().add(valorIpca)).setScale(2, BigDecimal.ROUND_HALF_EVEN));
				contratoCobrancaDetalhes.setIpca(valorIpca);
				return true;
			}
		}

		return false;
	}

	public void geraRelFinanceiroUltimaParcela() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();

		this.relObjetoContratoCobranca = contratoCobrancaDao.relatorioControleEstoqueUltimaParcela();

		processaDadosRelFinanceiro();

		this.relSelectedObjetoContratoCobranca = new RelatorioFinanceiroCobranca();

		if (this.relObjetoContratoCobranca.size() == 0) {
			this.relObjetoContratoCobranca = new ArrayList<RelatorioFinanceiroCobranca>();
		}

		this.contratoGerado = false;
	}
	
	public void processaDadosRelFinanceiroAtrasoFull() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar data = Calendar.getInstance(zone, locale);
		Date auxDataPagamento = data.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
		String auxDataPagamentoStr = sdf.format(data.getTime());
		try {
			auxDataPagamento = sdf.parse(auxDataPagamentoStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (RelatorioFinanceiroCobranca relatorioFinanceiroCobranca : this.relObjetoContratoCobranca) {
			// se já houve baixa parcial, utiliza a data de vencimento atualizada
			// senão utiliza a data de vencimento antiga
			String auxDataVencimentoStr = "";
			Date auxDataVencimento = null;

			auxDataVencimentoStr = sdf.format(relatorioFinanceiroCobranca.getDataVencimentoAtual());
			try {
				auxDataVencimento = sdf.parse(auxDataVencimentoStr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ContratoCobrancaUtilsMB contratoCobrancaUtilsMB;

			contratoCobrancaUtilsMB = new ContratoCobrancaUtilsMB(auxDataVencimento, auxDataPagamento,
					relatorioFinanceiroCobranca.getValor(), relatorioFinanceiroCobranca.contratoCobranca.getTxJuros(),
					relatorioFinanceiroCobranca.contratoCobranca.getTxMulta());

			contratoCobrancaUtilsMB.recalculaValor();

			relatorioFinanceiroCobranca.setVlrParcelaAtualizada(contratoCobrancaUtilsMB.getValorAtualizado());
		}
	}

	public void processaDadosRelFinanceiro() {

		List<PagadorRecebedor> listPagadorRecebedor = new ArrayList<PagadorRecebedor>();
		List<BigDecimal> listValoresRecebedores = new ArrayList<BigDecimal>();

		/* Tratamento da opção de exibir somente o financeiro do favorecido filtrado */
		/*
		 * Se selecionado algum favorecido no filtro e houver restrição dos dados por
		 * favorecido
		 */
		if (this.exibeSomenteFavorecidosFiltrados.equals("Todos")) {
			for (RelatorioFinanceiroCobranca relatorioFinanceiroCobranca : this.relObjetoContratoCobranca) {
				listPagadorRecebedor = new ArrayList<PagadorRecebedor>();
				listValoresRecebedores = new ArrayList<BigDecimal>();

				relatorioFinanceiroCobranca.getContratoCobranca().setExibeRecebedor1(true);
				relatorioFinanceiroCobranca.getContratoCobranca().setExibeRecebedor2(true);
				relatorioFinanceiroCobranca.getContratoCobranca().setExibeRecebedor3(true);
				relatorioFinanceiroCobranca.getContratoCobranca().setExibeRecebedor4(true);
				relatorioFinanceiroCobranca.getContratoCobranca().setExibeRecebedor5(true);
				relatorioFinanceiroCobranca.getContratoCobranca().setExibeRecebedor6(true);
				relatorioFinanceiroCobranca.getContratoCobranca().setExibeRecebedor7(true);
				relatorioFinanceiroCobranca.getContratoCobranca().setExibeRecebedor8(true);
				relatorioFinanceiroCobranca.getContratoCobranca().setExibeRecebedor9(true);
				relatorioFinanceiroCobranca.getContratoCobranca().setExibeRecebedor10(true);

				if (relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor() != null) {
					listPagadorRecebedor.add(relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor());
					listValoresRecebedores.add(relatorioFinanceiroCobranca.getContratoCobranca().getVlrRecebedor());
				}

				// Contrato - Recebedor 2
				if (relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor2() != null) {
					listPagadorRecebedor.add(relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor2());
					listValoresRecebedores.add(relatorioFinanceiroCobranca.getContratoCobranca().getVlrRecebedor2());
				}

				// Contrato - Recebedor 3
				if (relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor3() != null) {
					listPagadorRecebedor.add(relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor3());
					listValoresRecebedores.add(relatorioFinanceiroCobranca.getContratoCobranca().getVlrRecebedor3());
				}

				// Contrato - Recebedor 3
				if (relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor4() != null) {
					listPagadorRecebedor.add(relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor4());
					listValoresRecebedores.add(relatorioFinanceiroCobranca.getContratoCobranca().getVlrRecebedor4());
				}

				// Contrato - Recebedor 5
				if (relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor5() != null) {
					listPagadorRecebedor.add(relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor5());
					listValoresRecebedores.add(relatorioFinanceiroCobranca.getContratoCobranca().getVlrRecebedor5());
				}

				// Contrato - Recebedor 6
				if (relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor6() != null) {
					listPagadorRecebedor.add(relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor6());
					listValoresRecebedores.add(relatorioFinanceiroCobranca.getContratoCobranca().getVlrRecebedor6());
				}

				// Contrato - Recebedor 7
				if (relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor7() != null) {
					listPagadorRecebedor.add(relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor7());
					listValoresRecebedores.add(relatorioFinanceiroCobranca.getContratoCobranca().getVlrRecebedor7());
				}

				// Contrato - Recebedor 8
				if (relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor8() != null) {
					listPagadorRecebedor.add(relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor8());
					listValoresRecebedores.add(relatorioFinanceiroCobranca.getContratoCobranca().getVlrRecebedor8());
				}

				// Contrato - Recebedor 9
				if (relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor9() != null) {
					listPagadorRecebedor.add(relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor9());
					listValoresRecebedores.add(relatorioFinanceiroCobranca.getContratoCobranca().getVlrRecebedor9());
				}

				// Contrato - Recebedor 10
				if (relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor10() != null) {
					listPagadorRecebedor.add(relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor10());
					listValoresRecebedores.add(relatorioFinanceiroCobranca.getContratoCobranca().getVlrRecebedor10());
				}

				// popula os recebedores do contrato conforme o filtro informado
				if (listPagadorRecebedor.size() > 0) {
					for (int i = 0; i < listPagadorRecebedor.size(); i++) {
						if (i == 0) {
							relatorioFinanceiroCobranca.setRecebedor1(listPagadorRecebedor.get(i));
							relatorioFinanceiroCobranca.setVlrRecebedor1(listValoresRecebedores.get(i));
						}
						if (i == 1) {
							relatorioFinanceiroCobranca.setRecebedor2(listPagadorRecebedor.get(i));
							relatorioFinanceiroCobranca.setVlrRecebedor2(listValoresRecebedores.get(i));
						}
						if (i == 2) {
							relatorioFinanceiroCobranca.setRecebedor3(listPagadorRecebedor.get(i));
							relatorioFinanceiroCobranca.setVlrRecebedor3(listValoresRecebedores.get(i));
						}
						if (i == 3) {
							relatorioFinanceiroCobranca.setRecebedor4(listPagadorRecebedor.get(i));
							relatorioFinanceiroCobranca.setVlrRecebedor4(listValoresRecebedores.get(i));
						}
						if (i == 4) {
							relatorioFinanceiroCobranca.setRecebedor5(listPagadorRecebedor.get(i));
							relatorioFinanceiroCobranca.setVlrRecebedor5(listValoresRecebedores.get(i));
						}
						if (i == 5) {
							relatorioFinanceiroCobranca.setRecebedor6(listPagadorRecebedor.get(i));
							relatorioFinanceiroCobranca.setVlrRecebedor6(listValoresRecebedores.get(i));
						}
						if (i == 6) {
							relatorioFinanceiroCobranca.setRecebedor7(listPagadorRecebedor.get(i));
							relatorioFinanceiroCobranca.setVlrRecebedor7(listValoresRecebedores.get(i));
						}
						if (i == 7) {
							relatorioFinanceiroCobranca.setRecebedor8(listPagadorRecebedor.get(i));
							relatorioFinanceiroCobranca.setVlrRecebedor8(listValoresRecebedores.get(i));
						}
						if (i == 8) {
							relatorioFinanceiroCobranca.setRecebedor9(listPagadorRecebedor.get(i));
							relatorioFinanceiroCobranca.setVlrRecebedor9(listValoresRecebedores.get(i));
						}
						if (i == 9) {
							relatorioFinanceiroCobranca.setRecebedor10(listPagadorRecebedor.get(i));
							relatorioFinanceiroCobranca.setVlrRecebedor10(listValoresRecebedores.get(i));
						}
					}
				}
			}
		}

		if (this.exibeSomenteFavorecidosFiltrados.equals("Nenhum")) {
			for (RelatorioFinanceiroCobranca relatorioFinanceiroCobranca : this.relObjetoContratoCobranca) {
				listPagadorRecebedor = new ArrayList<PagadorRecebedor>();
				listValoresRecebedores = new ArrayList<BigDecimal>();
				relatorioFinanceiroCobranca.getContratoCobranca().setExibeRecebedor1(false);
				relatorioFinanceiroCobranca.getContratoCobranca().setExibeRecebedor2(false);
				relatorioFinanceiroCobranca.getContratoCobranca().setExibeRecebedor3(false);
				relatorioFinanceiroCobranca.getContratoCobranca().setExibeRecebedor4(false);
				relatorioFinanceiroCobranca.getContratoCobranca().setExibeRecebedor5(false);
				relatorioFinanceiroCobranca.getContratoCobranca().setExibeRecebedor6(false);
				relatorioFinanceiroCobranca.getContratoCobranca().setExibeRecebedor7(false);
				relatorioFinanceiroCobranca.getContratoCobranca().setExibeRecebedor8(false);
				relatorioFinanceiroCobranca.getContratoCobranca().setExibeRecebedor9(false);
				relatorioFinanceiroCobranca.getContratoCobranca().setExibeRecebedor10(false);
			}
		}

		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar data = Calendar.getInstance(zone, locale);
		Date auxDataPagamento = data.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
		String auxDataPagamentoStr = sdf.format(data.getTime());
		try {
			auxDataPagamento = sdf.parse(auxDataPagamentoStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (RelatorioFinanceiroCobranca relatorioFinanceiroCobranca : this.relObjetoContratoCobranca) {
			// se já houve baixa parcial, utiliza a data de vencimento atualizada
			// senão utiliza a data de vencimento antiga
			String auxDataVencimentoStr = "";
			Date auxDataVencimento = null;

			if(!CommonsUtil.semValor(relatorioFinanceiroCobranca.getDataVencimentoAtual())) {
				auxDataVencimentoStr = sdf.format(relatorioFinanceiroCobranca.getDataVencimentoAtual());
			} else {
				auxDataVencimentoStr = sdf.format(relatorioFinanceiroCobranca.getDataVencimento());
			}
			try {
				auxDataVencimento = sdf.parse(auxDataVencimentoStr);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ContratoCobrancaUtilsMB contratoCobrancaUtilsMB;

			contratoCobrancaUtilsMB = new ContratoCobrancaUtilsMB(auxDataVencimento, auxDataPagamento,
					relatorioFinanceiroCobranca.getValor(), relatorioFinanceiroCobranca.contratoCobranca.getTxJuros(),
					relatorioFinanceiroCobranca.contratoCobranca.getTxMulta());

			contratoCobrancaUtilsMB.recalculaValor();

			relatorioFinanceiroCobranca.setVlrParcelaAtualizada(contratoCobrancaUtilsMB.getValorAtualizado());
		}

		if ((this.idRecebedor > 0 || this.idRecebedor2 > 0 || this.idRecebedor3 > 0 || this.idRecebedor4 > 0
				|| this.idRecebedor5 > 0 || this.idRecebedor6 > 0 || this.idRecebedor7 > 0 || this.idRecebedor8 > 0
				|| this.idRecebedor9 > 0 || this.idRecebedor10 > 0)
				&& (this.exibeSomenteFavorecidosFiltrados.equals("Somente Filtrados"))) {
			PagadorRecebedor pagadorRecebedorChecked = null;
			// Pesquisa para faezr match entre filtros de favorecidos e favorecidos do
			// contrato
			for (RelatorioFinanceiroCobranca relatorioFinanceiroCobranca : this.relObjetoContratoCobranca) {

				listPagadorRecebedor = new ArrayList<PagadorRecebedor>();
				listValoresRecebedores = new ArrayList<BigDecimal>();

				// Contrato - Recebedor 1
				if (relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor() != null) {
					// check se o filtro informado da match com algum dos recebedores do contrato
					pagadorRecebedorChecked = checkRecebedorFiltroContrato(
							relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor());

					// se deu match popula lista de recebedores auxiliar
					if (pagadorRecebedorChecked != null) {
						listPagadorRecebedor.add(pagadorRecebedorChecked);
						listValoresRecebedores.add(relatorioFinanceiroCobranca.getContratoCobranca().getVlrRecebedor());
					}

					// limpa a posição do recebedor no contrato
					relatorioFinanceiroCobranca.setRecebedor1(null);
					relatorioFinanceiroCobranca.setVlrRecebedor1(null);
				}

				// Contrato - Recebedor 2
				pagadorRecebedorChecked = null;
				if (relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor2() != null) {
					// check se o filtro informado da match com algum dos recebedores do contrato
					pagadorRecebedorChecked = checkRecebedorFiltroContrato(
							relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor2());

					// se deu match popula lista de recebedores auxiliar
					if (pagadorRecebedorChecked != null) {
						listPagadorRecebedor.add(pagadorRecebedorChecked);
						listValoresRecebedores
								.add(relatorioFinanceiroCobranca.getContratoCobranca().getVlrRecebedor2());
					}

					// limpa a posição do recebedor no contrato
					relatorioFinanceiroCobranca.setRecebedor2(null);
					relatorioFinanceiroCobranca.setVlrRecebedor2(null);
				}

				// Contrato - Recebedor 3
				pagadorRecebedorChecked = null;
				if (relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor3() != null) {
					// check se o filtro informado da match com algum dos recebedores do contrato
					pagadorRecebedorChecked = checkRecebedorFiltroContrato(
							relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor3());

					// se deu match popula lista de recebedores auxiliar
					if (pagadorRecebedorChecked != null) {
						listPagadorRecebedor.add(pagadorRecebedorChecked);
						listValoresRecebedores
								.add(relatorioFinanceiroCobranca.getContratoCobranca().getVlrRecebedor3());
					}

					// limpa a posição do recebedor no contrato
					relatorioFinanceiroCobranca.setRecebedor3(null);
					relatorioFinanceiroCobranca.setVlrRecebedor3(null);
				}

				// Contrato - Recebedor 3
				pagadorRecebedorChecked = null;
				if (relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor4() != null) {
					// check se o filtro informado da match com algum dos recebedores do contrato
					pagadorRecebedorChecked = checkRecebedorFiltroContrato(
							relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor4());

					// se deu match popula lista de recebedores auxiliar
					if (pagadorRecebedorChecked != null) {
						listPagadorRecebedor.add(pagadorRecebedorChecked);
						listValoresRecebedores
								.add(relatorioFinanceiroCobranca.getContratoCobranca().getVlrRecebedor4());
					}

					// limpa a posição do recebedor no contrato
					relatorioFinanceiroCobranca.setRecebedor4(null);
					relatorioFinanceiroCobranca.setVlrRecebedor4(null);
				}

				// Contrato - Recebedor 5
				pagadorRecebedorChecked = null;
				if (relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor5() != null) {
					// check se o filtro informado da match com algum dos recebedores do contrato
					pagadorRecebedorChecked = checkRecebedorFiltroContrato(
							relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor5());

					// se deu match popula lista de recebedores auxiliar
					if (pagadorRecebedorChecked != null) {
						listPagadorRecebedor.add(pagadorRecebedorChecked);
						listValoresRecebedores
								.add(relatorioFinanceiroCobranca.getContratoCobranca().getVlrRecebedor5());
					}

					// limpa a posição do recebedor no contrato
					relatorioFinanceiroCobranca.setRecebedor5(null);
					relatorioFinanceiroCobranca.setVlrRecebedor5(null);
				}

				// Contrato - Recebedor 6
				pagadorRecebedorChecked = null;
				if (relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor6() != null) {
					// check se o filtro informado da match com algum dos recebedores do contrato
					pagadorRecebedorChecked = checkRecebedorFiltroContrato(
							relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor6());

					// se deu match popula lista de recebedores auxiliar
					if (pagadorRecebedorChecked != null) {
						listPagadorRecebedor.add(pagadorRecebedorChecked);
						listValoresRecebedores
								.add(relatorioFinanceiroCobranca.getContratoCobranca().getVlrRecebedor6());
					}

					// limpa a posição do recebedor no contrato
					relatorioFinanceiroCobranca.setRecebedor6(null);
					relatorioFinanceiroCobranca.setVlrRecebedor6(null);
				}

				// Contrato - Recebedor 7
				pagadorRecebedorChecked = null;
				if (relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor7() != null) {
					// check se o filtro informado da match com algum dos recebedores do contrato
					pagadorRecebedorChecked = checkRecebedorFiltroContrato(
							relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor7());

					// se deu match popula lista de recebedores auxiliar
					if (pagadorRecebedorChecked != null) {
						listPagadorRecebedor.add(pagadorRecebedorChecked);
						listValoresRecebedores
								.add(relatorioFinanceiroCobranca.getContratoCobranca().getVlrRecebedor7());
					}

					// limpa a posição do recebedor no contrato
					relatorioFinanceiroCobranca.setRecebedor7(null);
					relatorioFinanceiroCobranca.setVlrRecebedor7(null);
				}

				// Contrato - Recebedor 8
				pagadorRecebedorChecked = null;
				if (relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor8() != null) {
					// check se o filtro informado da match com algum dos recebedores do contrato
					pagadorRecebedorChecked = checkRecebedorFiltroContrato(
							relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor8());

					// se deu match popula lista de recebedores auxiliar
					if (pagadorRecebedorChecked != null) {
						listPagadorRecebedor.add(pagadorRecebedorChecked);
						listValoresRecebedores
								.add(relatorioFinanceiroCobranca.getContratoCobranca().getVlrRecebedor8());
					}

					// limpa a posição do recebedor no contrato
					relatorioFinanceiroCobranca.setRecebedor8(null);
					relatorioFinanceiroCobranca.setVlrRecebedor8(null);
				}

				// Contrato - Recebedor 9
				pagadorRecebedorChecked = null;
				if (relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor9() != null) {
					// check se o filtro informado da match com algum dos recebedores do contrato
					pagadorRecebedorChecked = checkRecebedorFiltroContrato(
							relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor9());

					// se deu match popula lista de recebedores auxiliar
					if (pagadorRecebedorChecked != null) {
						listPagadorRecebedor.add(pagadorRecebedorChecked);
						listValoresRecebedores
								.add(relatorioFinanceiroCobranca.getContratoCobranca().getVlrRecebedor9());
					}

					// limpa a posição do recebedor no contrato
					relatorioFinanceiroCobranca.setRecebedor9(null);
					relatorioFinanceiroCobranca.setVlrRecebedor9(null);
				}

				// Contrato - Recebedor 10
				pagadorRecebedorChecked = null;
				if (relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor10() != null) {
					// check se o filtro informado da match com algum dos recebedores do contrato
					pagadorRecebedorChecked = checkRecebedorFiltroContrato(
							relatorioFinanceiroCobranca.getContratoCobranca().getRecebedor10());

					// se deu match popula lista de recebedores auxiliar
					if (pagadorRecebedorChecked != null) {
						listPagadorRecebedor.add(pagadorRecebedorChecked);
						listValoresRecebedores
								.add(relatorioFinanceiroCobranca.getContratoCobranca().getVlrRecebedor10());
					}

					// limpa a posição do recebedor no contrato
					relatorioFinanceiroCobranca.setRecebedor10(null);
					relatorioFinanceiroCobranca.setVlrRecebedor10(null);
				}

				// popula os recebedores do contrato conforme o filtro informado
				if (listPagadorRecebedor.size() > 0) {
					for (int i = 0; i < listPagadorRecebedor.size(); i++) {
						if (i == 0) {
							relatorioFinanceiroCobranca.setRecebedor1(listPagadorRecebedor.get(i));
							relatorioFinanceiroCobranca.setVlrRecebedor1(listValoresRecebedores.get(i));
						}
						if (i == 1) {
							relatorioFinanceiroCobranca.setRecebedor2(listPagadorRecebedor.get(i));
							relatorioFinanceiroCobranca.setVlrRecebedor2(listValoresRecebedores.get(i));
						}
						if (i == 2) {
							relatorioFinanceiroCobranca.setRecebedor3(listPagadorRecebedor.get(i));
							relatorioFinanceiroCobranca.setVlrRecebedor3(listValoresRecebedores.get(i));
						}
						if (i == 3) {
							relatorioFinanceiroCobranca.setRecebedor4(listPagadorRecebedor.get(i));
							relatorioFinanceiroCobranca.setVlrRecebedor4(listValoresRecebedores.get(i));
						}
						if (i == 4) {
							relatorioFinanceiroCobranca.setRecebedor5(listPagadorRecebedor.get(i));
							relatorioFinanceiroCobranca.setVlrRecebedor5(listValoresRecebedores.get(i));
						}
						if (i == 5) {
							relatorioFinanceiroCobranca.setRecebedor6(listPagadorRecebedor.get(i));
							relatorioFinanceiroCobranca.setVlrRecebedor6(listValoresRecebedores.get(i));
						}
						if (i == 6) {
							relatorioFinanceiroCobranca.setRecebedor7(listPagadorRecebedor.get(i));
							relatorioFinanceiroCobranca.setVlrRecebedor7(listValoresRecebedores.get(i));
						}
						if (i == 7) {
							relatorioFinanceiroCobranca.setRecebedor8(listPagadorRecebedor.get(i));
							relatorioFinanceiroCobranca.setVlrRecebedor8(listValoresRecebedores.get(i));
						}
						if (i == 8) {
							relatorioFinanceiroCobranca.setRecebedor9(listPagadorRecebedor.get(i));
							relatorioFinanceiroCobranca.setVlrRecebedor9(listValoresRecebedores.get(i));
						}
						if (i == 9) {
							relatorioFinanceiroCobranca.setRecebedor10(listPagadorRecebedor.get(i));
							relatorioFinanceiroCobranca.setVlrRecebedor10(listValoresRecebedores.get(i));
						}
					}
				}
			}
		}
	}

	public PagadorRecebedor checkRecebedorFiltroContrato(PagadorRecebedor recebedorContrato) {
		if (this.idRecebedor > 0) {
			if (recebedorContrato.getId() == this.idRecebedor) {
				return recebedorContrato;
			}
		}
		if (this.idRecebedor2 > 0) {
			if (recebedorContrato.getId() == this.idRecebedor2) {
				return recebedorContrato;
			}
		}
		if (this.idRecebedor3 > 0) {
			if (recebedorContrato.getId() == this.idRecebedor3) {
				return recebedorContrato;
			}
		}
		if (this.idRecebedor4 > 0) {
			if (recebedorContrato.getId() == this.idRecebedor4) {
				return recebedorContrato;
			}
		}
		if (this.idRecebedor5 > 0) {
			if (recebedorContrato.getId() == this.idRecebedor5) {
				return recebedorContrato;
			}
		}
		if (this.idRecebedor6 > 0) {
			if (recebedorContrato.getId() == this.idRecebedor6) {
				return recebedorContrato;
			}
		}
		if (this.idRecebedor7 > 0) {
			if (recebedorContrato.getId() == this.idRecebedor7) {
				return recebedorContrato;
			}
		}
		if (this.idRecebedor8 > 0) {
			if (recebedorContrato.getId() == this.idRecebedor8) {
				return recebedorContrato;
			}
		}
		if (this.idRecebedor9 > 0) {
			if (recebedorContrato.getId() == this.idRecebedor9) {
				return recebedorContrato;
			}
		}
		if (this.idRecebedor10 > 0) {
			if (recebedorContrato.getId() == this.idRecebedor10) {
				return recebedorContrato;
			}
		}
		return null;
	}

	public void loadSelectedLovsPendentes() {
		clearSelectedLovsPendentes();

		if (this.objetoContratoCobranca.getPagador() != null) {
			this.selectedPagador = this.objetoContratoCobranca.getPagador();
			this.nomePagador = this.objetoContratoCobranca.getPagador().getNome();
			this.idPagador = this.objetoContratoCobranca.getPagador().getId();
		}

		if (this.objetoContratoCobranca.getResponsavel() != null) {
			this.selectedResponsavel = this.objetoContratoCobranca.getResponsavel();
			this.nomeResponsavel = this.objetoContratoCobranca.getResponsavel().getNome();
			this.idResponsavel = this.objetoContratoCobranca.getResponsavel().getId();
		}

		if (this.objetoContratoCobranca.getImovel() != null) {
			this.selectedImovel = this.objetoContratoCobranca.getImovel();
			this.nomeImovel = this.objetoContratoCobranca.getImovel().getNome();
			this.idImovel = this.objetoContratoCobranca.getImovel().getId();
		}
	}

	public void clearSelectedLovsPendentes() {
		this.selectedPagador = new PagadorRecebedor();
		this.nomePagador = null;
		this.idPagador = 0;

		this.selectedResponsavel = new Responsavel();
		this.nomeResponsavel = null;
		this.idResponsavel = 0;

		this.selectedImovel = new ImovelCobranca();
		this.nomeImovel = null;
		this.idImovel = 0;
		
		this.seguradoSelecionado = new Segurado();
		this.seguradoSelecionado.setPessoa(new PagadorRecebedor());
		
		this.socioSelecionado = new PagadorRecebedorSocio();
		this.socioSelecionado.setPessoa(new PagadorRecebedor());
		
		this.pagadorSecundarioSelecionado = new PagadorRecebedorAdicionais();
		this.pagadorSecundarioSelecionado.setPessoa(new PagadorRecebedor());
		
		this.addSegurador = false;
		this.addSocio = false;
		this.addPagador = false;
	}

	public void loadSelectedLovs() {
		clearSelectedRecebedores();
		this.seguradoSelecionado = new Segurado();
		this.seguradoSelecionado.setPessoa(new PagadorRecebedor());
		this.socioSelecionado = new PagadorRecebedorSocio();
		this.socioSelecionado.setPessoa(new PagadorRecebedor());
		this.pagadorSecundarioSelecionado = new PagadorRecebedorAdicionais();
		this.pagadorSecundarioSelecionado.setPessoa(new PagadorRecebedor());
		this.addSegurador = false;
		this.addSocio = false;
		this.addPagador = false;
		
		this.contasPagarSelecionada = new ContasPagar();
		this.contasPagarSelecionada.setPagadorRecebedor(new PagadorRecebedor());
		this.contasPagarSelecionada.setResponsavel(new Responsavel());
		
		this.selectedPagador = this.objetoContratoCobranca.getPagador();
		this.nomePagador = this.objetoContratoCobranca.getPagador().getNome();
		this.idPagador = this.objetoContratoCobranca.getPagador().getId();

		this.selectedResponsavel = this.objetoContratoCobranca.getResponsavel();
		this.nomeResponsavel = this.objetoContratoCobranca.getResponsavel().getNome();
		this.idResponsavel = this.objetoContratoCobranca.getResponsavel().getId();

		this.selectedImovel = this.objetoContratoCobranca.getImovel();
		if (this.selectedImovel != null) {
			if (this.objetoContratoCobranca.getImovel().getNome() != null) {
				this.nomeImovel = this.objetoContratoCobranca.getImovel().getNome();
			} else {
				this.nomeImovel = this.objetoContratoCobranca.getImovel().getTipo();
			}
			this.idImovel = this.objetoContratoCobranca.getImovel().getId();
		}

		if (this.objetoContratoCobranca.getRecebedor() != null) {
			this.selectedRecebedor = this.objetoContratoCobranca.getRecebedor();
			this.nomeRecebedor = this.objetoContratoCobranca.getRecebedor().getNome();
			this.idRecebedor = this.objetoContratoCobranca.getRecebedor().getId();
		}

		if (this.objetoContratoCobranca.getRecebedor2() != null) {
			this.selectedRecebedor2 = this.objetoContratoCobranca.getRecebedor2();
			this.nomeRecebedor2 = this.objetoContratoCobranca.getRecebedor2().getNome();
			this.idRecebedor2 = this.objetoContratoCobranca.getRecebedor2().getId();

			this.renderRecebedor2 = true;
		}

		if (this.objetoContratoCobranca.getRecebedor3() != null) {
			this.selectedRecebedor3 = this.objetoContratoCobranca.getRecebedor3();
			this.nomeRecebedor3 = this.objetoContratoCobranca.getRecebedor3().getNome();
			this.idRecebedor3 = this.objetoContratoCobranca.getRecebedor3().getId();

			this.renderRecebedor3 = true;
		}

		if (this.objetoContratoCobranca.getRecebedor4() != null) {
			this.selectedRecebedor4 = this.objetoContratoCobranca.getRecebedor4();
			this.nomeRecebedor4 = this.objetoContratoCobranca.getRecebedor4().getNome();
			this.idRecebedor4 = this.objetoContratoCobranca.getRecebedor4().getId();

			this.renderRecebedor4 = true;
		}

		if (this.objetoContratoCobranca.getRecebedor5() != null) {
			this.selectedRecebedor5 = this.objetoContratoCobranca.getRecebedor5();
			this.nomeRecebedor5 = this.objetoContratoCobranca.getRecebedor5().getNome();
			this.idRecebedor5 = this.objetoContratoCobranca.getRecebedor5().getId();

			this.renderRecebedor5 = true;
		}

		if (this.objetoContratoCobranca.getRecebedor6() != null) {
			this.selectedRecebedor6 = this.objetoContratoCobranca.getRecebedor6();
			this.nomeRecebedor6 = this.objetoContratoCobranca.getRecebedor6().getNome();
			this.idRecebedor6 = this.objetoContratoCobranca.getRecebedor6().getId();

			this.renderRecebedor6 = true;
		}

		if (this.objetoContratoCobranca.getRecebedor7() != null) {
			this.selectedRecebedor7 = this.objetoContratoCobranca.getRecebedor7();
			this.nomeRecebedor7 = this.objetoContratoCobranca.getRecebedor7().getNome();
			this.idRecebedor7 = this.objetoContratoCobranca.getRecebedor7().getId();

			this.renderRecebedor7 = true;
		}

		if (this.objetoContratoCobranca.getRecebedor8() != null) {
			this.selectedRecebedor8 = this.objetoContratoCobranca.getRecebedor8();
			this.nomeRecebedor8 = this.objetoContratoCobranca.getRecebedor8().getNome();
			this.idRecebedor8 = this.objetoContratoCobranca.getRecebedor8().getId();

			this.renderRecebedor8 = true;
		}

		if (this.objetoContratoCobranca.getRecebedor9() != null) {
			this.selectedRecebedor9 = this.objetoContratoCobranca.getRecebedor9();
			this.nomeRecebedor9 = this.objetoContratoCobranca.getRecebedor9().getNome();
			this.idRecebedor9 = this.objetoContratoCobranca.getRecebedor9().getId();

			this.renderRecebedor9 = true;
		}

		if (this.objetoContratoCobranca.getRecebedor10() != null) {
			this.selectedRecebedor10 = this.objetoContratoCobranca.getRecebedor10();
			this.nomeRecebedor10 = this.objetoContratoCobranca.getRecebedor10().getNome();
			this.idRecebedor10 = this.objetoContratoCobranca.getRecebedor10().getId();

			this.renderRecebedor10 = true;
		}

		if (this.objetoContratoCobranca.getRecebedorParcelaFinal1() != null) {
			this.selectedRecebedorFinal1 = this.objetoContratoCobranca.getRecebedorParcelaFinal1();
			this.nomeRecebedorFinal1 = this.objetoContratoCobranca.getRecebedorParcelaFinal1().getNome();
			this.idRecebedorFinal1 = this.objetoContratoCobranca.getRecebedorParcelaFinal1().getId();
		}

		if (this.objetoContratoCobranca.getRecebedorParcelaFinal2() != null) {
			this.selectedRecebedorFinal2 = this.objetoContratoCobranca.getRecebedorParcelaFinal2();
			this.nomeRecebedorFinal2 = this.objetoContratoCobranca.getRecebedorParcelaFinal2().getNome();
			this.idRecebedorFinal2 = this.objetoContratoCobranca.getRecebedorParcelaFinal2().getId();
		}

		if (this.objetoContratoCobranca.getRecebedorParcelaFinal3() != null) {
			this.selectedRecebedorFinal3 = this.objetoContratoCobranca.getRecebedorParcelaFinal3();
			this.nomeRecebedorFinal3 = this.objetoContratoCobranca.getRecebedorParcelaFinal3().getNome();
			this.idRecebedorFinal3 = this.objetoContratoCobranca.getRecebedorParcelaFinal3().getId();
		}

		if (this.objetoContratoCobranca.getRecebedorParcelaFinal4() != null) {
			this.selectedRecebedorFinal4 = this.objetoContratoCobranca.getRecebedorParcelaFinal4();
			this.nomeRecebedorFinal4 = this.objetoContratoCobranca.getRecebedorParcelaFinal4().getNome();
			this.idRecebedorFinal4 = this.objetoContratoCobranca.getRecebedorParcelaFinal4().getId();
		}

		if (this.objetoContratoCobranca.getRecebedorParcelaFinal5() != null) {
			this.selectedRecebedorFinal5 = this.objetoContratoCobranca.getRecebedorParcelaFinal5();
			this.nomeRecebedorFinal5 = this.objetoContratoCobranca.getRecebedorParcelaFinal5().getNome();
			this.idRecebedorFinal5 = this.objetoContratoCobranca.getRecebedorParcelaFinal5().getId();
		}

		if (this.objetoContratoCobranca.getRecebedorParcelaFinal6() != null) {
			this.selectedRecebedorFinal6 = this.objetoContratoCobranca.getRecebedorParcelaFinal6();
			this.nomeRecebedorFinal6 = this.objetoContratoCobranca.getRecebedorParcelaFinal6().getNome();
			this.idRecebedorFinal6 = this.objetoContratoCobranca.getRecebedorParcelaFinal6().getId();
		}

		if (this.objetoContratoCobranca.getRecebedorParcelaFinal7() != null) {
			this.selectedRecebedorFinal7 = this.objetoContratoCobranca.getRecebedorParcelaFinal7();
			this.nomeRecebedorFinal7 = this.objetoContratoCobranca.getRecebedorParcelaFinal7().getNome();
			this.idRecebedorFinal7 = this.objetoContratoCobranca.getRecebedorParcelaFinal7().getId();
		}

		if (this.objetoContratoCobranca.getRecebedorParcelaFinal8() != null) {
			this.selectedRecebedorFinal8 = this.objetoContratoCobranca.getRecebedorParcelaFinal8();
			this.nomeRecebedorFinal8 = this.objetoContratoCobranca.getRecebedorParcelaFinal8().getNome();
			this.idRecebedorFinal8 = this.objetoContratoCobranca.getRecebedorParcelaFinal8().getId();
		}

		if (this.objetoContratoCobranca.getRecebedorParcelaFinal9() != null) {
			this.selectedRecebedorFinal9 = this.objetoContratoCobranca.getRecebedorParcelaFinal9();
			this.nomeRecebedorFinal9 = this.objetoContratoCobranca.getRecebedorParcelaFinal9().getNome();
			this.idRecebedorFinal9 = this.objetoContratoCobranca.getRecebedorParcelaFinal9().getId();
		}

		if (this.objetoContratoCobranca.getRecebedorParcelaFinal10() != null) {
			this.selectedRecebedorFinal10 = this.objetoContratoCobranca.getRecebedorParcelaFinal10();
			this.nomeRecebedorFinal10 = this.objetoContratoCobranca.getRecebedorParcelaFinal10().getNome();
			this.idRecebedorFinal10 = this.objetoContratoCobranca.getRecebedorParcelaFinal10().getId();
		}
	}

	public void clearSelectedLovs() {
		this.selectedPagador = new PagadorRecebedor();
		this.nomePagador = null;
		this.idPagador = 0;

		this.selectedResponsavel = new Responsavel();
		this.nomeResponsavel = null;
		this.idResponsavel = 0;

		this.selectedImovel = new ImovelCobranca();
		this.nomeImovel = null;
		this.idImovel = 0;

		clearRecebedor();
		clearRecebedor2();
		clearRecebedor3();
		clearRecebedor4();
		clearRecebedor5();
		clearRecebedor6();
		clearRecebedor7();
		clearRecebedor8();
		clearRecebedor9();
		clearRecebedor10();

		clearRecebedorFinal1();
		clearRecebedorFinal2();
		clearRecebedorFinal3();
		clearRecebedorFinal4();
		clearRecebedorFinal5();
		clearRecebedorFinal6();
		clearRecebedorFinal7();
		clearRecebedorFinal8();
		clearRecebedorFinal9();
		clearRecebedorFinal10();
	}

	public void reParcelarContrato() {	
		return;
	}

	public List<ContratoCobrancaParcelasInvestidor> ordenaParcleasInvstidor(
			List<ContratoCobrancaParcelasInvestidor> lista) {

		Collections.sort(lista, new Comparator<ContratoCobrancaParcelasInvestidor>() {
			@Override
			public int compare(ContratoCobrancaParcelasInvestidor one, ContratoCobrancaParcelasInvestidor other) {
				int result = one.getDataVencimento().compareTo(other.getDataVencimento());
				if (result == 0) {
					try {
						Integer oneParcela = Integer.parseInt(one.getNumeroParcela());
						Integer otherParcela = Integer.parseInt(other.getNumeroParcela());
						result = oneParcela.compareTo(otherParcela);
					} catch (Exception e) {
						result = 0;
					}
				}
				return result;
			}
		});

		return lista;
	}

	public List<ContratoCobrancaParcelasInvestidor> geraParcelasInvestidor(int investidorPosicao) {
		List<ContratoCobrancaParcelasInvestidor> parcelasInvestidor = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		ContratoCobrancaParcelasInvestidor parcelaInvestidor;
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

		Date dataParcela;
		BigDecimal taxaRemuneracao = BigDecimal.ZERO;
		
		String tipoCalculoInvestidor;
		BigDecimal vlrInvestidor;
		Integer qtdeParcelasInvestidor;

		PagadorRecebedor investidor = new PagadorRecebedor();
		boolean isEnvelope = false;

		switch (investidorPosicao) {
		case 1:
			investidor = this.objetoContratoCobranca.getRecebedor();
			taxaRemuneracao = this.objetoContratoCobranca.getTaxaRemuneracaoInvestidor1();
			isEnvelope = this.objetoContratoCobranca.isRecebedorEnvelope();
			
			tipoCalculoInvestidor = this.objetoContratoCobranca.getTipoCalculoInvestidor1();			
			vlrInvestidor = this.objetoContratoCobranca.getVlrInvestidor1();
			qtdeParcelasInvestidor = this.objetoContratoCobranca.getQtdeParcelasInvestidor1();
			break;
		case 2:
			investidor = this.objetoContratoCobranca.getRecebedor2();
			taxaRemuneracao = this.objetoContratoCobranca.getTaxaRemuneracaoInvestidor2();
			isEnvelope = this.objetoContratoCobranca.isRecebedorEnvelope2();

			tipoCalculoInvestidor = this.objetoContratoCobranca.getTipoCalculoInvestidor2();			
			vlrInvestidor = this.objetoContratoCobranca.getVlrInvestidor2();
			qtdeParcelasInvestidor = this.objetoContratoCobranca.getQtdeParcelasInvestidor2();
			break;
		case 3:
			investidor = this.objetoContratoCobranca.getRecebedor3();
			taxaRemuneracao = this.objetoContratoCobranca.getTaxaRemuneracaoInvestidor3();
			isEnvelope = this.objetoContratoCobranca.isRecebedorEnvelope3();

			tipoCalculoInvestidor = this.objetoContratoCobranca.getTipoCalculoInvestidor3();			
			vlrInvestidor = this.objetoContratoCobranca.getVlrInvestidor3();
			qtdeParcelasInvestidor = this.objetoContratoCobranca.getQtdeParcelasInvestidor3();
			break;
		case 4:
			investidor = this.objetoContratoCobranca.getRecebedor4();
			taxaRemuneracao = this.objetoContratoCobranca.getTaxaRemuneracaoInvestidor4();
			isEnvelope = this.objetoContratoCobranca.isRecebedorEnvelope4();

			tipoCalculoInvestidor = this.objetoContratoCobranca.getTipoCalculoInvestidor4();			
			vlrInvestidor = this.objetoContratoCobranca.getVlrInvestidor4();
			qtdeParcelasInvestidor = this.objetoContratoCobranca.getQtdeParcelasInvestidor4();
			break;
		case 5:
			investidor = this.objetoContratoCobranca.getRecebedor5();
			taxaRemuneracao = this.objetoContratoCobranca.getTaxaRemuneracaoInvestidor5();
			isEnvelope = this.objetoContratoCobranca.isRecebedorEnvelope5();

			tipoCalculoInvestidor = this.objetoContratoCobranca.getTipoCalculoInvestidor5();			
			vlrInvestidor = this.objetoContratoCobranca.getVlrInvestidor5();
			qtdeParcelasInvestidor = this.objetoContratoCobranca.getQtdeParcelasInvestidor5();
			break;
		case 6:
			investidor = this.objetoContratoCobranca.getRecebedor6();
			taxaRemuneracao = this.objetoContratoCobranca.getTaxaRemuneracaoInvestidor6();
			isEnvelope = this.objetoContratoCobranca.isRecebedorEnvelope6();
			
			tipoCalculoInvestidor = this.objetoContratoCobranca.getTipoCalculoInvestidor6();			
			vlrInvestidor = this.objetoContratoCobranca.getVlrInvestidor6();
			qtdeParcelasInvestidor = this.objetoContratoCobranca.getQtdeParcelasInvestidor6();
			break;
		case 7:
			investidor = this.objetoContratoCobranca.getRecebedor7();
			taxaRemuneracao = this.objetoContratoCobranca.getTaxaRemuneracaoInvestidor7();
			isEnvelope = this.objetoContratoCobranca.isRecebedorEnvelope7();
			
			tipoCalculoInvestidor = this.objetoContratoCobranca.getTipoCalculoInvestidor7();			
			vlrInvestidor = this.objetoContratoCobranca.getVlrInvestidor7();
			qtdeParcelasInvestidor = this.objetoContratoCobranca.getQtdeParcelasInvestidor7();
			break;
		case 8:
			investidor = this.objetoContratoCobranca.getRecebedor8();
			taxaRemuneracao = this.objetoContratoCobranca.getTaxaRemuneracaoInvestidor8();
			isEnvelope = this.objetoContratoCobranca.isRecebedorEnvelope8();
			
			tipoCalculoInvestidor = this.objetoContratoCobranca.getTipoCalculoInvestidor8();			
			vlrInvestidor = this.objetoContratoCobranca.getVlrInvestidor8();
			qtdeParcelasInvestidor = this.objetoContratoCobranca.getQtdeParcelasInvestidor8();
			break;
		case 9:
			investidor = this.objetoContratoCobranca.getRecebedor9();
			taxaRemuneracao = this.objetoContratoCobranca.getTaxaRemuneracaoInvestidor9();
			isEnvelope = this.objetoContratoCobranca.isRecebedorEnvelope9();
			
			tipoCalculoInvestidor = this.objetoContratoCobranca.getTipoCalculoInvestidor9();			
			vlrInvestidor = this.objetoContratoCobranca.getVlrInvestidor9();
			qtdeParcelasInvestidor = this.objetoContratoCobranca.getQtdeParcelasInvestidor9();
			break;
		case 10:
			investidor = this.objetoContratoCobranca.getRecebedor10();
			taxaRemuneracao = this.objetoContratoCobranca.getTaxaRemuneracaoInvestidor10();
			isEnvelope = this.objetoContratoCobranca.isRecebedorEnvelope10();
			
			tipoCalculoInvestidor = this.objetoContratoCobranca.getTipoCalculoInvestidor10();			
			vlrInvestidor = this.objetoContratoCobranca.getVlrInvestidor10();
			qtdeParcelasInvestidor = this.objetoContratoCobranca.getQtdeParcelasInvestidor10();
			break;
		}

		//calcular simulador 
		SimulacaoVO simulador = calcularInvestimento(investidorPosicao);
		BigDecimal totalJuros = BigDecimal.ZERO;
		if( simulador == null)
			return new ArrayList<ContratoCobrancaParcelasInvestidor>();
		
		for (SimulacaoDetalheVO parcela : simulador.getParcelas()) {
			
			if ( BigInteger.ZERO.compareTo(parcela.getNumeroParcela())==0)
				continue;
					
//		for (int i = 0; i < iQtdParcelas; i++) {
			if (isEnvelope) {
				parcelaInvestidor = new ContratoCobrancaParcelasInvestidor();

				dataParcela = contratoCobrancaDao.geraDataParcela(parcela.getNumeroParcela().intValue(), simulador.getDataSimulacao());

				parcelaInvestidor.setDataVencimento(dataParcela);
				parcelaInvestidor.setNumeroParcela(CommonsUtil.stringValue(parcela.getNumeroParcela()));
				parcelaInvestidor.setParcelaMensal(parcela.getValorParcela());
				parcelaInvestidor.setValorLiquido(parcela.getValorParcela());
				parcelaInvestidor.setBaixado(false);
				parcelaInvestidor.setInvestidor(investidor);
				parcelaInvestidor.setNumeroContrato(this.objetoContratoCobranca.getNumeroContrato());

				parcelaInvestidor.setJuros(BigDecimal.ZERO);
				parcelaInvestidor.setAmortizacao(BigDecimal.ZERO);
				parcelaInvestidor.setSaldoCredor(BigDecimal.ZERO);
				parcelaInvestidor.setSaldoCredorAtualizado(BigDecimal.ZERO);
				parcelaInvestidor.setIrRetido(BigDecimal.ZERO);

				// adiciona a lista de retorno
				parcelasInvestidor.add(parcelaInvestidor);
			} else {
				parcelaInvestidor = new ContratoCobrancaParcelasInvestidor();

				dataParcela = contratoCobrancaDao.geraDataParcela(parcela.getNumeroParcela().intValue(), simulador.getDataSimulacao());

				parcelaInvestidor.setDataVencimento(dataParcela);
				parcelaInvestidor.setNumeroParcela(CommonsUtil.stringValue(parcela.getNumeroParcela()));
				
				parcelaInvestidor.setSaldoCredor(parcela.getSaldoDevedorInicial());

				// se a taxa de remuneração for maior que zero
				if (taxaRemuneracao.compareTo(BigDecimal.ZERO) == 1) {
					if (BigDecimal.ZERO.compareTo(parcela.getSaldoDevedorInicial())==0) {
						parcelaInvestidor.setJuros(totalJuros.add(parcela.getJuros()));
						parcelaInvestidor.setParcelaMensal(parcela.getValorParcela().add(totalJuros));
						parcelaInvestidor.setCapitalizacao(parcela.getJuros());
					}else {
						parcelaInvestidor.setJuros(BigDecimal.ZERO);
						parcelaInvestidor.setParcelaMensal(BigDecimal.ZERO);
						totalJuros = totalJuros.add(parcela.getJuros());
						parcelaInvestidor.setCapitalizacao(parcela.getJuros());
					}
					parcelaInvestidor.setAmortizacao(parcela.getAmortizacao());
					parcelaInvestidor
							.setSaldoCredorAtualizado(parcela.getSaldoDevedorInicial());
				} else {
					parcelaInvestidor.setJuros(BigDecimal.ZERO);
					parcelaInvestidor.setAmortizacao(BigDecimal.ZERO);
					parcelaInvestidor.setSaldoCredorAtualizado(BigDecimal.ZERO);
					parcelaInvestidor.setCapitalizacao(BigDecimal.ZERO);
				}

				if (BigDecimal.ZERO.compareTo(parcelaInvestidor.getJurosBaixa())<0) {
					if (!this.objetoContratoCobranca.getEmpresa().equals("GALLERIA CORRESPONDENTE BANCARIO EIRELI")) {
						BigDecimal txIR = BigDecimal.ZERO;
	
						if ((parcela.getNumeroParcela().intValue()) < 6) {
							txIR = BigDecimal.valueOf(0.225);
						}else if ((parcela.getNumeroParcela().intValue()) >= 6 && (parcela.getNumeroParcela().intValue()) < 12) {
							txIR = BigDecimal.valueOf(0.2);
						}else if ((parcela.getNumeroParcela().intValue()) >= 12 && (parcela.getNumeroParcela().intValue()) < 24) {
							txIR = BigDecimal.valueOf(0.175);
						}else if ((parcela.getNumeroParcela().intValue()) >= 24) {
							txIR = BigDecimal.valueOf(0.15);
						}
	
						parcelaInvestidor.setIrRetido(parcelaInvestidor.getJurosBaixa().multiply(txIR));
	
						if (BigDecimal.ZERO.compareTo(parcelaInvestidor.getParcelaMensal()) < 0)
							parcelaInvestidor.setValorLiquido(parcelaInvestidor.getParcelaMensal().subtract(parcelaInvestidor.getIrRetido()));
						
					} else {
						parcelaInvestidor.setValorLiquido(parcela.getValorParcela());
					}
				}else {
					parcelaInvestidor.setValorLiquido(BigDecimal.ZERO);
					parcelaInvestidor.setIrRetido(BigDecimal.ZERO);
				}

				parcelaInvestidor.setBaixado(false);

				parcelaInvestidor.setInvestidor(investidor);

				parcelaInvestidor.setNumeroContrato(this.objetoContratoCobranca.getNumeroContrato());

				// adiciona a lista de retorno
				parcelasInvestidor.add(parcelaInvestidor);
			}
		}
		return parcelasInvestidor;
	}

	public boolean verificaSeGeraParcelasInvestidor(List<ContratoCobrancaParcelasInvestidor> listaParcelas,
			int posicaoInvestidor) {
		if (listaParcelas == null) {
			return true;
		} else {
			// Verifica se o investidor teve alterações nas parcelas
			// Se sim, não deve-se gerar parcelas de investidor novamente
			boolean geraParcelasInvestidor = true;

			switch (posicaoInvestidor) {
			case 1:
				if (this.objetoContratoCobranca.isParcelasAlteradas1()) {
					geraParcelasInvestidor = false;
				}
				break;
			case 2:
				if (this.objetoContratoCobranca.isParcelasAlteradas2()) {
					geraParcelasInvestidor = false;
				}
				break;
			case 3:
				if (this.objetoContratoCobranca.isParcelasAlteradas3()) {
					geraParcelasInvestidor = false;
				}
				break;
			case 4:
				if (this.objetoContratoCobranca.isParcelasAlteradas4()) {
					geraParcelasInvestidor = false;
				}
				break;
			case 5:
				if (this.objetoContratoCobranca.isParcelasAlteradas5()) {
					geraParcelasInvestidor = false;
				}
				break;
			case 6:
				if (this.objetoContratoCobranca.isParcelasAlteradas6()) {
					geraParcelasInvestidor = false;
				}
				break;
			case 7:
				if (this.objetoContratoCobranca.isParcelasAlteradas7()) {
					geraParcelasInvestidor = false;
				}
				break;
			case 8:
				if (this.objetoContratoCobranca.isParcelasAlteradas8()) {
					geraParcelasInvestidor = false;
				}
				break;
			case 9:
				if (this.objetoContratoCobranca.isParcelasAlteradas9()) {
					geraParcelasInvestidor = false;
				}
				break;
			case 10:
				if (this.objetoContratoCobranca.isParcelasAlteradas10()) {
					geraParcelasInvestidor = false;
				}
				break;
			default:
			}

			if (geraParcelasInvestidor) {
				for (ContratoCobrancaParcelasInvestidor c : listaParcelas) {
					if (c.getDataBaixa() != null) {
						return false;
					}
				}
			} else {
				return false;
			}
		}

		return true;
	}

	/***
	 * VERIFICA SE RECEBEDOR É GALLERIA
	 * 
	 * @param recebedor
	 * @return
	 */
	public boolean recebedoIsGalleria(PagadorRecebedor recebedor) {
		boolean retorno = false;

		if (recebedor.getId() == 15 || recebedor.getId() == 34 || recebedor.getId() == 14 || recebedor.getId() == 182
				|| recebedor.getId() == 417 || recebedor.getId() == 803) {
			retorno = true;
		}

		return retorno;
	}

	/****
	 * CALCULA RETENCAO, REPASSE E COMISSAO
	 * 
	 * @return
	 */
	public void calculaValoresContratoTodasParcelas(PagadorRecebedor recebedor, boolean ocultaRecebedor,
			boolean recebedorEnvelope, List<ContratoCobrancaParcelasInvestidor> contratoCobrancaParcelasInvestidor) {
		if (recebedor != null) {
			// calcula repasse
			if (ocultaRecebedor && recebedorEnvelope) {
				for (ContratoCobrancaParcelasInvestidor parcelas : contratoCobrancaParcelasInvestidor) {
					this.vlrComissaoNew = this.vlrComissaoNew.add(parcelas.getParcelaMensal());
				}
			}
			// Se galleria
			if (recebedoIsGalleria(recebedor)) {
				for (ContratoCobrancaParcelasInvestidor parcelas : contratoCobrancaParcelasInvestidor) {
					this.vlrRetencaoNew = this.vlrRetencaoNew.add(parcelas.getParcelaMensal());
				}
			} else {
				// Se não galleria
				if (!ocultaRecebedor && !recebedorEnvelope) {
					for (ContratoCobrancaParcelasInvestidor parcelas : contratoCobrancaParcelasInvestidor) {
						this.vlrRepasseNew = this.vlrRepasseNew.add(parcelas.getParcelaMensal());
					}
				}
			}
		}
	}

	/****
	 * CALCULA RETENCAO, REPASSE E COMISSAO
	 * 
	 * @return
	 */
	public void calculaValoresContratoParcelaRecebedor(PagadorRecebedor recebedor, boolean ocultaRecebedor,
			boolean recebedorEnvelope, BigDecimal valorRecebedor, BigDecimal valorRecebedorFinal) {
		if (recebedor != null) {
			// calcula repasse
			if (ocultaRecebedor || recebedorEnvelope) {
				if (valorRecebedor != null) {
					this.vlrComissaoNew = this.vlrComissaoNew.add(valorRecebedor);
				}
				if (valorRecebedorFinal != null) {
					this.vlrComissaoFinalNew = this.vlrComissaoFinalNew.add(valorRecebedorFinal);
				}
			}
			// Se galleria
			if (recebedoIsGalleria(recebedor)) {
				if (valorRecebedor != null) {
					this.vlrRetencaoNew = this.vlrRetencaoNew.add(valorRecebedor);
				}
				if (valorRecebedorFinal != null) {
					this.vlrRetencaoFinalNew = this.vlrRetencaoFinalNew.add(valorRecebedorFinal);
				}
			} else {
				// Se não galleria
				if (!ocultaRecebedor && !recebedorEnvelope) {
					if (valorRecebedor != null) {
						this.vlrRepasseNew = this.vlrRepasseNew.add(valorRecebedor);
					}
					if (valorRecebedorFinal != null) {
						this.vlrRepasseFinalNew = this.vlrRepasseFinalNew.add(valorRecebedorFinal);
					}
				}
			}
		}
	}

	/***
	 * CALCULA VALOR INVESTIDOR vlrRepasse ADM vlrRetencao REPASSE vlrComissao
	 * 
	 */
	public void calculaValoresContratoParcelaRecebedorTela() {
		this.vlrRepasseNew = BigDecimal.ZERO;
		this.vlrRetencaoNew = BigDecimal.ZERO;
		this.vlrComissaoNew = BigDecimal.ZERO;

		this.vlrRepasseFinalNew = BigDecimal.ZERO;
		this.vlrRetencaoFinalNew = BigDecimal.ZERO;
		this.vlrComissaoFinalNew = BigDecimal.ZERO;

		if (this.selectedRecebedor != null) {
			calculaValoresContratoParcelaRecebedor(this.selectedRecebedor,
					this.objetoContratoCobranca.isOcultaRecebedor(), this.objetoContratoCobranca.isRecebedorEnvelope(),
					this.objetoContratoCobranca.getVlrRecebedor(), this.objetoContratoCobranca.getVlrFinalRecebedor1());
		}

		if (this.selectedRecebedor2 != null) {
			calculaValoresContratoParcelaRecebedor(this.selectedRecebedor2,
					this.objetoContratoCobranca.isOcultaRecebedor2(),
					this.objetoContratoCobranca.isRecebedorEnvelope2(), this.objetoContratoCobranca.getVlrRecebedor2(),
					this.objetoContratoCobranca.getVlrFinalRecebedor2());
		}

		if (this.selectedRecebedor3 != null) {
			calculaValoresContratoParcelaRecebedor(this.selectedRecebedor3,
					this.objetoContratoCobranca.isOcultaRecebedor3(),
					this.objetoContratoCobranca.isRecebedorEnvelope3(), this.objetoContratoCobranca.getVlrRecebedor3(),
					this.objetoContratoCobranca.getVlrFinalRecebedor3());
		}

		if (this.selectedRecebedor4 != null) {
			calculaValoresContratoParcelaRecebedor(this.selectedRecebedor4,
					this.objetoContratoCobranca.isOcultaRecebedor4(),
					this.objetoContratoCobranca.isRecebedorEnvelope4(), this.objetoContratoCobranca.getVlrRecebedor4(),
					this.objetoContratoCobranca.getVlrFinalRecebedor4());
		}

		if (this.selectedRecebedor5 != null) {
			calculaValoresContratoParcelaRecebedor(this.selectedRecebedor5,
					this.objetoContratoCobranca.isOcultaRecebedor5(),
					this.objetoContratoCobranca.isRecebedorEnvelope5(), this.objetoContratoCobranca.getVlrRecebedor5(),
					this.objetoContratoCobranca.getVlrFinalRecebedor5());
		}

		if (this.selectedRecebedor6 != null) {
			calculaValoresContratoParcelaRecebedor(this.selectedRecebedor6,
					this.objetoContratoCobranca.isOcultaRecebedor6(),
					this.objetoContratoCobranca.isRecebedorEnvelope6(), this.objetoContratoCobranca.getVlrRecebedor6(),
					this.objetoContratoCobranca.getVlrFinalRecebedor6());
		}

		if (this.selectedRecebedor7 != null) {
			calculaValoresContratoParcelaRecebedor(this.selectedRecebedor7,
					this.objetoContratoCobranca.isOcultaRecebedor7(),
					this.objetoContratoCobranca.isRecebedorEnvelope7(), this.objetoContratoCobranca.getVlrRecebedor7(),
					this.objetoContratoCobranca.getVlrFinalRecebedor7());
		}

		if (this.selectedRecebedor8 != null) {
			calculaValoresContratoParcelaRecebedor(this.selectedRecebedor8,
					this.objetoContratoCobranca.isOcultaRecebedor8(),
					this.objetoContratoCobranca.isRecebedorEnvelope8(), this.objetoContratoCobranca.getVlrRecebedor8(),
					this.objetoContratoCobranca.getVlrFinalRecebedor8());
		}

		if (this.selectedRecebedor9 != null) {
			calculaValoresContratoParcelaRecebedor(this.selectedRecebedor9,
					this.objetoContratoCobranca.isOcultaRecebedor9(),
					this.objetoContratoCobranca.isRecebedorEnvelope9(), this.objetoContratoCobranca.getVlrRecebedor9(),
					this.objetoContratoCobranca.getVlrFinalRecebedor9());
		}

		if (this.selectedRecebedor10 != null) {
			calculaValoresContratoParcelaRecebedor(this.selectedRecebedor10,
					this.objetoContratoCobranca.isOcultaRecebedor10(),
					this.objetoContratoCobranca.isRecebedorEnvelope10(),
					this.objetoContratoCobranca.getVlrRecebedor10(),
					this.objetoContratoCobranca.getVlrFinalRecebedor10());
		}

		// seta novos valores
		this.vlrRepasse = this.vlrRepasseNew;
		this.vlrRetencao = this.vlrRetencaoNew;
		this.vlrComissao = this.vlrComissaoNew;

		this.vlrRepasseFinal = this.vlrRepasseFinalNew;
		this.vlrRetencaoFinal = this.vlrRetencaoFinalNew;
		this.vlrComissaoFinal = this.vlrComissaoFinalNew;
	}

	public String gerarContrato() {
		FacesContext context = FacesContext.getCurrentInstance();
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		String msgRetorno = null;
		boolean erroValidacaoLov = false;
		boolean erroValidacaoBaixa = false;

		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();

		this.objetoContratoCobranca.setVlrInvestimento(this.objetoContratoCobranca.getVlrParcela());

		// em caso de regerar
		/*
		 * if (this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() > 0)
		 * { for (ContratoCobrancaDetalhes cbd :
		 * this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) { if
		 * (cbd.isParcelaPaga()) { context.addMessage(null, new FacesMessage(
		 * FacesMessage.SEVERITY_ERROR,
		 * "Contrato Cobrança: Erro de validação: Já houve baixa de parcela(s) " +
		 * "para este contrato, não é possível efetuar a alteração.", ""));
		 * erroValidacaoBaixa = true; break; } } }
		 */
		BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();

//		this.objetoContratoCobranca.setVlrParcelaStr(
//				bigDecimalConverter.getAsString(null, null, this.objetoContratoCobranca.getVlrParcela()));

		// INICIO - Validacao das LoVs
		if (this.selectedPagador == null) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Contrato Cobrança: Erro de validação: é obrigatória a seleção do Pagador.", ""));
			erroValidacaoLov = true;
		} else {
			this.objetoContratoCobranca.setPagador(pagadorRecebedorDao.findById(this.selectedPagador.getId()));
		}

		if (this.selectedRecebedor == null && SiscoatConstants.PAGADOR_GALLERIA.contains(this.selectedPagador.getId())) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Contrato Cobrança: Erro de validação: é obrigatória a seleção do Recebedor.", ""));
			erroValidacaoLov = true;

			this.objetoContratoCobranca
					.setListContratoCobrancaParcelasInvestidor1(new ArrayList<ContratoCobrancaParcelasInvestidor>());
		} else {
			// Popula recebedor final
			if (!this.objetoContratoCobranca.isOcultaRecebedor()
					&& !this.objetoContratoCobranca.isRecebedorEnvelope()) {
				this.objetoContratoCobranca.setRecebedorParcelaFinal1(this.selectedRecebedor);
			}

			if( this.selectedRecebedor == null) {
				this.objetoContratoCobranca.setRecebedor(null);
				this.objetoContratoCobranca.setVlrRecebedor(null);
				this.objetoContratoCobranca.setDataInclusaoRecebedor1(null);
				this.objetoContratoCobranca
						.setListContratoCobrancaParcelasInvestidor1(new ArrayList<ContratoCobrancaParcelasInvestidor>());
				this.objetoContratoCobranca.setRecebedorParcelaFinal1(null);
			} else {
				this.objetoContratoCobranca.setRecebedor(pagadorRecebedorDao.findById(this.selectedRecebedor.getId()));
				this.objetoContratoCobranca.setDataInclusaoRecebedor1(dataHoje);
				if (verificaSeGeraParcelasInvestidor(
						this.objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor1(), 1)) {
					this.objetoContratoCobranca.setListContratoCobrancaParcelasInvestidor1(geraParcelasInvestidor(1));
				}
			}
		}

		if (this.selectedRecebedor2 == null) {
			this.objetoContratoCobranca.setRecebedor2(null);
			this.objetoContratoCobranca.setVlrRecebedor2(null);
			this.objetoContratoCobranca.setDataInclusaoRecebedor2(null);
			this.objetoContratoCobranca
					.setListContratoCobrancaParcelasInvestidor2(new ArrayList<ContratoCobrancaParcelasInvestidor>());
			this.objetoContratoCobranca.setRecebedorParcelaFinal2(null);
		} else {
			// Popula recebedor final
			if (!this.objetoContratoCobranca.isOcultaRecebedor2()
					&& !this.objetoContratoCobranca.isRecebedorEnvelope2()) {
				this.objetoContratoCobranca.setRecebedorParcelaFinal2(this.selectedRecebedor2);
			}

			this.objetoContratoCobranca.setRecebedor2(pagadorRecebedorDao.findById(this.selectedRecebedor2.getId()));
			this.objetoContratoCobranca.setDataInclusaoRecebedor2(dataHoje);
			if (verificaSeGeraParcelasInvestidor(
					this.objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor2(), 2)) {
				this.objetoContratoCobranca.setListContratoCobrancaParcelasInvestidor2(geraParcelasInvestidor(2));
			}
		}

		if (this.selectedRecebedor3 == null) {
			this.objetoContratoCobranca.setRecebedor3(null);
			this.objetoContratoCobranca.setVlrRecebedor3(null);
			this.objetoContratoCobranca.setDataInclusaoRecebedor3(null);
			this.objetoContratoCobranca
					.setListContratoCobrancaParcelasInvestidor3(new ArrayList<ContratoCobrancaParcelasInvestidor>());
			this.objetoContratoCobranca.setRecebedorParcelaFinal3(null);
		} else {
			// Popula recebedor final
			if (!this.objetoContratoCobranca.isOcultaRecebedor3()
					&& !this.objetoContratoCobranca.isRecebedorEnvelope3()) {
				this.objetoContratoCobranca.setRecebedorParcelaFinal3(this.selectedRecebedor3);
			}

			this.objetoContratoCobranca.setRecebedor3(pagadorRecebedorDao.findById(this.selectedRecebedor3.getId()));
			this.objetoContratoCobranca.setDataInclusaoRecebedor3(dataHoje);
			if (verificaSeGeraParcelasInvestidor(
					this.objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor3(), 3)) {
				this.objetoContratoCobranca.setListContratoCobrancaParcelasInvestidor3(geraParcelasInvestidor(3));
			}
		}

		if (this.selectedRecebedor4 == null) {
			this.objetoContratoCobranca.setRecebedor4(null);
			this.objetoContratoCobranca.setVlrRecebedor4(null);
			this.objetoContratoCobranca.setDataInclusaoRecebedor4(null);
			this.objetoContratoCobranca
					.setListContratoCobrancaParcelasInvestidor4(new ArrayList<ContratoCobrancaParcelasInvestidor>());
			this.objetoContratoCobranca.setRecebedorParcelaFinal4(null);
		} else {
			// Popula recebedor final
			if (!this.objetoContratoCobranca.isOcultaRecebedor4()
					&& !this.objetoContratoCobranca.isRecebedorEnvelope4()) {
				this.objetoContratoCobranca.setRecebedorParcelaFinal4(this.selectedRecebedor4);
			}

			this.objetoContratoCobranca.setRecebedor4(pagadorRecebedorDao.findById(this.selectedRecebedor4.getId()));
			this.objetoContratoCobranca.setDataInclusaoRecebedor4(dataHoje);
			if (verificaSeGeraParcelasInvestidor(
					this.objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor4(), 4)) {
				this.objetoContratoCobranca.setListContratoCobrancaParcelasInvestidor4(geraParcelasInvestidor(4));
			}
		}

		if (this.selectedRecebedor5 == null) {
			this.objetoContratoCobranca.setRecebedor5(null);
			this.objetoContratoCobranca.setVlrRecebedor5(null);
			this.objetoContratoCobranca.setDataInclusaoRecebedor5(null);
			this.objetoContratoCobranca
					.setListContratoCobrancaParcelasInvestidor5(new ArrayList<ContratoCobrancaParcelasInvestidor>());
			this.objetoContratoCobranca.setRecebedorParcelaFinal5(null);
		} else {
			// Popula recebedor final
			if (!this.objetoContratoCobranca.isOcultaRecebedor5()
					&& !this.objetoContratoCobranca.isRecebedorEnvelope5()) {
				this.objetoContratoCobranca.setRecebedorParcelaFinal5(this.selectedRecebedor5);
			}

			this.objetoContratoCobranca.setRecebedor5(pagadorRecebedorDao.findById(this.selectedRecebedor5.getId()));
			this.objetoContratoCobranca.setDataInclusaoRecebedor5(dataHoje);
			if (verificaSeGeraParcelasInvestidor(
					this.objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor5(), 5)) {
				this.objetoContratoCobranca.setListContratoCobrancaParcelasInvestidor5(geraParcelasInvestidor(5));
			}
		}

		if (this.selectedRecebedor6 == null) {
			this.objetoContratoCobranca.setRecebedor6(null);
			this.objetoContratoCobranca.setVlrRecebedor6(null);
			this.objetoContratoCobranca.setDataInclusaoRecebedor6(null);
			this.objetoContratoCobranca
					.setListContratoCobrancaParcelasInvestidor6(new ArrayList<ContratoCobrancaParcelasInvestidor>());
			this.objetoContratoCobranca.setRecebedorParcelaFinal6(null);
		} else {

			// Popula recebedor final
			if (!this.objetoContratoCobranca.isOcultaRecebedor6()
					&& !this.objetoContratoCobranca.isRecebedorEnvelope6()) {
				this.objetoContratoCobranca.setRecebedorParcelaFinal6(this.selectedRecebedor6);
			}

			this.objetoContratoCobranca.setRecebedor6(pagadorRecebedorDao.findById(this.selectedRecebedor6.getId()));
			this.objetoContratoCobranca.setDataInclusaoRecebedor6(dataHoje);
			if (verificaSeGeraParcelasInvestidor(
					this.objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor6(), 6)) {
				this.objetoContratoCobranca.setListContratoCobrancaParcelasInvestidor6(geraParcelasInvestidor(6));
			}
		}

		if (this.selectedRecebedor7 == null) {
			this.objetoContratoCobranca.setRecebedor7(null);
			this.objetoContratoCobranca.setVlrRecebedor7(null);
			this.objetoContratoCobranca.setDataInclusaoRecebedor7(null);
			this.objetoContratoCobranca
					.setListContratoCobrancaParcelasInvestidor7(new ArrayList<ContratoCobrancaParcelasInvestidor>());
			this.objetoContratoCobranca.setRecebedorParcelaFinal7(null);
		} else {
			// Popula recebedor final
			if (!this.objetoContratoCobranca.isOcultaRecebedor7()
					&& !this.objetoContratoCobranca.isRecebedorEnvelope7()) {
				this.objetoContratoCobranca.setRecebedorParcelaFinal7(this.selectedRecebedor7);
			}

			this.objetoContratoCobranca.setRecebedor7(pagadorRecebedorDao.findById(this.selectedRecebedor7.getId()));
			this.objetoContratoCobranca.setDataInclusaoRecebedor7(dataHoje);
			if (verificaSeGeraParcelasInvestidor(
					this.objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor7(), 7)) {
				this.objetoContratoCobranca.setListContratoCobrancaParcelasInvestidor7(geraParcelasInvestidor(7));
			}
		}

		if (this.selectedRecebedor8 == null) {
			this.objetoContratoCobranca.setRecebedor8(null);
			this.objetoContratoCobranca.setVlrRecebedor8(null);
			this.objetoContratoCobranca.setDataInclusaoRecebedor8(null);
			this.objetoContratoCobranca
					.setListContratoCobrancaParcelasInvestidor8(new ArrayList<ContratoCobrancaParcelasInvestidor>());
			this.objetoContratoCobranca.setRecebedorParcelaFinal8(null);
		} else {

			// Popula recebedor final
			if (!this.objetoContratoCobranca.isOcultaRecebedor8()
					&& !this.objetoContratoCobranca.isRecebedorEnvelope8()) {
				this.objetoContratoCobranca.setRecebedorParcelaFinal8(this.selectedRecebedor8);
			}

			this.objetoContratoCobranca.setRecebedor8(pagadorRecebedorDao.findById(this.selectedRecebedor8.getId()));
			this.objetoContratoCobranca.setDataInclusaoRecebedor8(dataHoje);
			if (verificaSeGeraParcelasInvestidor(
					this.objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor8(), 8)) {
				this.objetoContratoCobranca.setListContratoCobrancaParcelasInvestidor8(geraParcelasInvestidor(8));
			}
		}

		if (this.selectedRecebedor9 == null) {
			this.objetoContratoCobranca.setRecebedor9(null);
			this.objetoContratoCobranca.setVlrRecebedor9(null);
			this.objetoContratoCobranca.setDataInclusaoRecebedor9(null);
			this.objetoContratoCobranca
					.setListContratoCobrancaParcelasInvestidor9(new ArrayList<ContratoCobrancaParcelasInvestidor>());
			this.objetoContratoCobranca.setRecebedorParcelaFinal9(null);
		} else {
			// Popula recebedor final
			if (!this.objetoContratoCobranca.isOcultaRecebedor9()
					&& !this.objetoContratoCobranca.isRecebedorEnvelope9()) {
				this.objetoContratoCobranca.setRecebedorParcelaFinal9(this.selectedRecebedor9);
			}

			this.objetoContratoCobranca.setRecebedor9(pagadorRecebedorDao.findById(this.selectedRecebedor9.getId()));
			this.objetoContratoCobranca.setDataInclusaoRecebedor9(dataHoje);
			if (verificaSeGeraParcelasInvestidor(
					this.objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor9(), 9)) {
				this.objetoContratoCobranca.setListContratoCobrancaParcelasInvestidor9(geraParcelasInvestidor(9));
			}
		}

		if (this.selectedRecebedor10 == null) {
			this.objetoContratoCobranca.setRecebedor10(null);
			this.objetoContratoCobranca.setVlrRecebedor10(null);
			this.objetoContratoCobranca.setDataInclusaoRecebedor10(null);
			this.objetoContratoCobranca
					.setListContratoCobrancaParcelasInvestidor10(new ArrayList<ContratoCobrancaParcelasInvestidor>());
			this.objetoContratoCobranca.setRecebedorParcelaFinal10(null);
		} else {
			// Popula recebedor final
			if (!this.objetoContratoCobranca.isOcultaRecebedor10()
					&& !this.objetoContratoCobranca.isRecebedorEnvelope10()) {
				this.objetoContratoCobranca.setRecebedorParcelaFinal10(this.selectedRecebedor10);
			}

			this.objetoContratoCobranca.setRecebedor10(pagadorRecebedorDao.findById(this.selectedRecebedor10.getId()));
			this.objetoContratoCobranca.setDataInclusaoRecebedor10(dataHoje);
			if (verificaSeGeraParcelasInvestidor(
					this.objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor10(), 10)) {
				this.objetoContratoCobranca.setListContratoCobrancaParcelasInvestidor10(geraParcelasInvestidor(10));
			}
		}

		/***
		 * CALCULA VALOR INVESTIDOR vlrRepasse ADM vlrRetencao REPASSE vlrComissao
		 * 
		 */

		this.vlrRepasseNew = BigDecimal.ZERO;
		this.vlrRetencaoNew = BigDecimal.ZERO;
		this.vlrComissaoNew = BigDecimal.ZERO;

		this.vlrRepasseFinalNew = BigDecimal.ZERO;
		this.vlrRetencaoFinalNew = BigDecimal.ZERO;
		this.vlrComissaoFinalNew = BigDecimal.ZERO;

		if (this.selectedRecebedor != null) {
			calculaValoresContratoParcelaRecebedor(this.selectedRecebedor,
					this.objetoContratoCobranca.isOcultaRecebedor(), this.objetoContratoCobranca.isRecebedorEnvelope(),
					this.objetoContratoCobranca.getVlrRecebedor(), this.objetoContratoCobranca.getVlrFinalRecebedor1());
		}

		if (this.selectedRecebedor2 != null) {
			calculaValoresContratoParcelaRecebedor(this.selectedRecebedor2,
					this.objetoContratoCobranca.isOcultaRecebedor2(),
					this.objetoContratoCobranca.isRecebedorEnvelope2(), this.objetoContratoCobranca.getVlrRecebedor2(),
					this.objetoContratoCobranca.getVlrFinalRecebedor2());
		}

		if (this.selectedRecebedor3 != null) {
			calculaValoresContratoParcelaRecebedor(this.selectedRecebedor3,
					this.objetoContratoCobranca.isOcultaRecebedor3(),
					this.objetoContratoCobranca.isRecebedorEnvelope3(), this.objetoContratoCobranca.getVlrRecebedor3(),
					this.objetoContratoCobranca.getVlrFinalRecebedor3());
		}

		if (this.selectedRecebedor4 != null) {
			calculaValoresContratoParcelaRecebedor(this.selectedRecebedor4,
					this.objetoContratoCobranca.isOcultaRecebedor4(),
					this.objetoContratoCobranca.isRecebedorEnvelope4(), this.objetoContratoCobranca.getVlrRecebedor4(),
					this.objetoContratoCobranca.getVlrFinalRecebedor4());
		}

		if (this.selectedRecebedor5 != null) {
			calculaValoresContratoParcelaRecebedor(this.selectedRecebedor5,
					this.objetoContratoCobranca.isOcultaRecebedor5(),
					this.objetoContratoCobranca.isRecebedorEnvelope5(), this.objetoContratoCobranca.getVlrRecebedor5(),
					this.objetoContratoCobranca.getVlrFinalRecebedor5());
		}

		if (this.selectedRecebedor6 != null) {
			calculaValoresContratoParcelaRecebedor(this.selectedRecebedor6,
					this.objetoContratoCobranca.isOcultaRecebedor6(),
					this.objetoContratoCobranca.isRecebedorEnvelope6(), this.objetoContratoCobranca.getVlrRecebedor6(),
					this.objetoContratoCobranca.getVlrFinalRecebedor6());
		}

		if (this.selectedRecebedor7 != null) {
			calculaValoresContratoParcelaRecebedor(this.selectedRecebedor7,
					this.objetoContratoCobranca.isOcultaRecebedor7(),
					this.objetoContratoCobranca.isRecebedorEnvelope7(), this.objetoContratoCobranca.getVlrRecebedor7(),
					this.objetoContratoCobranca.getVlrFinalRecebedor7());
		}

		if (this.selectedRecebedor8 != null) {
			calculaValoresContratoParcelaRecebedor(this.selectedRecebedor8,
					this.objetoContratoCobranca.isOcultaRecebedor8(),
					this.objetoContratoCobranca.isRecebedorEnvelope8(), this.objetoContratoCobranca.getVlrRecebedor8(),
					this.objetoContratoCobranca.getVlrFinalRecebedor8());
		}

		if (this.selectedRecebedor9 != null) {
			calculaValoresContratoParcelaRecebedor(this.selectedRecebedor9,
					this.objetoContratoCobranca.isOcultaRecebedor9(),
					this.objetoContratoCobranca.isRecebedorEnvelope9(), this.objetoContratoCobranca.getVlrRecebedor9(),
					this.objetoContratoCobranca.getVlrFinalRecebedor9());
		}

		if (this.selectedRecebedor10 != null) {
			calculaValoresContratoParcelaRecebedor(this.selectedRecebedor10,
					this.objetoContratoCobranca.isOcultaRecebedor10(),
					this.objetoContratoCobranca.isRecebedorEnvelope10(),
					this.objetoContratoCobranca.getVlrRecebedor10(),
					this.objetoContratoCobranca.getVlrFinalRecebedor10());
		}

		// seta novos valores
		this.vlrRepasse = this.vlrRepasseNew;
		this.vlrRetencao = this.vlrRetencaoNew;
		this.vlrComissao = this.vlrComissaoNew;

		this.vlrRepasseFinal = this.vlrRepasseFinalNew;
		this.vlrRetencaoFinal = this.vlrRetencaoFinalNew;
		this.vlrComissaoFinal = this.vlrComissaoFinalNew;
		
		if (!this.validarProcentagensSeguro()) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"A soma das porcentagens dos segurados não é 100%", ""));
			erroValidacaoLov = true;
		}

		if (this.selectedImovel == null) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Contrato Cobrança: Erro de validação: é obrigatória a seleção do Imóvel.", ""));
			erroValidacaoLov = true;
		} else {
			ImovelCobrancaDao imovelCobrancaDao = new ImovelCobrancaDao();
			this.objetoContratoCobranca.setImovel(imovelCobrancaDao.findById(this.selectedImovel.getId()));
		}

		if (this.selectedResponsavel == null) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Contrato Cobrança: Erro de validação: é obrigatória a seleção do Responsável.", ""));
			erroValidacaoLov = true;
		} else {
			ResponsavelDao responsavelDao = new ResponsavelDao();
			this.objetoContratoCobranca.setResponsavel(responsavelDao.findById(this.selectedResponsavel.getId()));
		}

		// se houve erro(s) na validações acima, retorna nulo e mensagem de erro
		if (erroValidacaoLov || erroValidacaoBaixa) {
			return "";
		}
		// FIM - Validacao das LoVs

		// limpa a lista de parcelas existentes para criação do novo contrato
		/*
		 * List<ContratoCobrancaDetalhes> parcelasPagas = new
		 * ArrayList<ContratoCobrancaDetalhes>(); if (!erroValidacaoBaixa &&
		 * this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() > 0 &&
		 * this.updateMode) { ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao =
		 * new ContratoCobrancaDetalhesDao(); List<Long> idsContratoCobrancaDetalhes =
		 * new ArrayList<Long>();
		 * 
		 * for (ContratoCobrancaDetalhes c :
		 * this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) { if
		 * (c.isParcelaPaga()) { parcelasPagas.add(c); } }
		 * 
		 * for (ContratoCobrancaDetalhes c :
		 * this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
		 * idsContratoCobrancaDetalhes.add(c.getId()); }
		 * 
		 * this.objetoContratoCobranca.getListContratoCobrancaDetalhes().removeAll(this.
		 * objetoContratoCobranca.getListContratoCobrancaDetalhes());
		 * 
		 * contratoCobrancaDao.merge(this.objetoContratoCobranca);
		 * 
		 * for (Long id : idsContratoCobrancaDetalhes) {
		 * contratoCobrancaDetalhesDao.delete(contratoCobrancaDetalhesDao.findById(id));
		 * } }
		 */
		if (this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() <= 0) {
			geraContratoCobrancaDetalhes(contratoCobrancaDao);
		} else {
			// se a quantidade de parcelas for igual, atualiza os valores e refaz as datas
			// de vencimento
			if (Integer.valueOf(this.qtdeParcelas) == this.objetoContratoCobranca.getQtdeParcelas()) {				
				// atualiza Repasse / Retenção / Comissão caso seja diferente
				for (ContratoCobrancaDetalhes ccd : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
					if (!ccd.isParcelaPaga()) {
						if (ccd.getVlrRepasse() != this.vlrRepasse || ccd.getVlrRetencao() != this.vlrRetencao
								|| ccd.getVlrComissao() != this.vlrComissao) {
							ccd.setVlrRepasse(this.vlrRepasse);
							ccd.setVlrRetencao(this.vlrRetencao);
							ccd.setVlrComissao(this.vlrComissao);
						} else {
							break;
						}
					}
				}
			}
		}

		try {
			if (objetoContratoCobranca.getId() <= 0) {
				contratoCobrancaDao.create(objetoContratoCobranca);
				msgRetorno = "inserido";
			} else {
				if (this.objetoContratoCobranca.isGeraParcelaFinal()) {
					if (this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() > 0) {
						if (!this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
								.get(this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() - 1)
								.isParcelaPaga()) {
							this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
									.get(this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() - 1)
									.setVlrComissao(this.vlrComissaoFinal);
							this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
									.get(this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() - 1)
									.setVlrRepasse(this.vlrRepasseFinal);
							this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
									.get(this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() - 1)
									.setVlrRetencao(this.vlrRetencaoFinal);
						}
					}
				}

				contratoCobrancaDao.merge(objetoContratoCobranca);
				msgRetorno = "atualizado";
			}

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Contrato Cobrança: Registro " + msgRetorno + " com sucesso!", ""));

		} catch (DAOException e) {

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contrato Cobrança: " + e, ""));

			return "";
		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contrato Cobrança: " + e, ""));

			return "";
		}

		this.contratoGerado = true;

		return "/Atendimento/Cobranca/ContratoCobrancaDetalhes.xhtml";
	}

	private void geraContratoCobrancaDetalhes(ContratoCobrancaDao contratoCobrancaDao) {
		// se esta editando pela primeira vez o contrato aprovado
		// gera remuneracao responsaveis
		geraContasPagarRemuneracao(this.objetoContratoCobranca);

		// processa parcelas
		/*
		 * todo calculo de juros composto BigDecimal valorParcela = BigDecimal.ZERO;
		 * valorParcela =
		 * valorParcela.add(this.objetoContratoCobranca.getVlrInvestimento());
		 * valorParcela =
		 * valorParcela.add(this.objetoContratoCobranca.getTxAdministracao());
		 * valorParcela =
		 * valorParcela.subtract(this.objetoContratoCobranca.getVlrRepasse());
		 * valorParcela = valorParcela.divide(new
		 * BigDecimal(this.objetoContratoCobranca.getQtdeParcelas()), 2,
		 * RoundingMode.CEILING);
		 */

		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataInicio = Calendar.getInstance(zone, locale);
		
		if (this.objetoContratoCobranca.getDataInicio() == null) {
			dataInicio.setTime(gerarDataHoje());
		} else {
			dataInicio.setTime(this.objetoContratoCobranca.getDataInicio());
		}

		//Date dataParcela = this.objetoContratoCobranca.getDataInicio();

		// Adiciona parcelas de pagamento
		
		 GeracaoBoletoMB geracaoBoletoMB = new GeracaoBoletoMB();
		  
		  this.fileBoleto = null;
		  
		  if (this.objetoContratoCobranca.getStatusLead() != null) {
			  if (this.objetoContratoCobranca.getStatusLead().equals("Completo")) {
				if (!SiscoatConstants.PAGADOR_GALLERIA.contains(this.selectedPagador.getId())) {
	
					SimulacaoVO simulador = calcularParcelas();
	
					BigDecimal saldoAnterior = BigDecimal.ZERO;
					for (SimulacaoDetalheVO parcela : simulador.getParcelas()) {
	
						ContratoCobrancaDetalhes contratoCobrancaDetalhes = criaContratoCobrancaDetalhe(
								contratoCobrancaDao, parcela, this.objetoContratoCobranca.getDataInicio(),
								saldoAnterior);
	
						this.objetoContratoCobranca.getListContratoCobrancaDetalhes().add(contratoCobrancaDetalhes);
						saldoAnterior = contratoCobrancaDetalhes.getVlrSaldoParcela();
	
						// gera boleto 
						
						if(this.objetoContratoCobranca.getEmpresa() != null) {
							if (this.isGeraBoletoInclusaoContrato()) {
								geracaoBoletoMB.geraBoletosBradesco("Locação", this.objetoContratoCobranca.getNumeroContrato(),
										this.objetoContratoCobranca.getPagador().getNome(),
										this.objetoContratoCobranca.getPagador().getCpf(),
										this.objetoContratoCobranca.getPagador().getCnpj(),
										this.objetoContratoCobranca.getPagador().getEndereco()
												+ this.objetoContratoCobranca.getPagador().getNumero(),
										this.objetoContratoCobranca.getPagador().getBairro(),
										this.objetoContratoCobranca.getPagador().getCep(),
										this.objetoContratoCobranca.getPagador().getCidade(),
										this.objetoContratoCobranca.getPagador().getEstado(),
										contratoCobrancaDetalhes.getDataVencimento(),
										this.objetoContratoCobranca.getVlrParcela(),
										contratoCobrancaDetalhes.getNumeroParcela());
							}
						}
					}
				}
			  }
		  }
		 


		if (this.isGeraBoletoInclusaoContrato()) {
			geracaoBoletoMB.geraPDFBoletos(
					"Boletos Bradesco - Contrato: " + this.objetoContratoCobranca.getNumeroContrato());

			this.fileBoleto = geracaoBoletoMB.getFile();
		}
	}
	

	private ContratoCobrancaDetalhes criaContratoCobrancaDetalhe(ContratoCobrancaDao contratoCobrancaDao, SimulacaoDetalheVO parcela, Date dataBaseParecela , BigDecimal saldoAnterior ) {
		ContratoCobrancaDetalhes contratoCobrancaDetalhes = new ContratoCobrancaDetalhes();

		Date dataParcela = contratoCobrancaDao.geraDataParcela(parcela.getNumeroParcela().intValue(),
				dataBaseParecela );

		contratoCobrancaDetalhes.setVlrSaldoInicial(saldoAnterior);
		contratoCobrancaDetalhes.setDataVencimento(dataParcela);
		contratoCobrancaDetalhes.setDataVencimentoAtual(dataParcela);
		contratoCobrancaDetalhes.setNumeroParcela(String.valueOf(parcela.getNumeroParcela().intValue()));
		contratoCobrancaDetalhes.setParcelaPaga(false);
		contratoCobrancaDetalhes.setVlrJurosParcela(parcela.getJuros().setScale(2, BigDecimal.ROUND_HALF_EVEN));

		contratoCobrancaDetalhes.setVlrJuros(this.objetoContratoCobranca.getTxJuros());
		contratoCobrancaDetalhes.setTxMulta(this.objetoContratoCobranca.getTxMulta());
		contratoCobrancaDetalhes.setVlrParcela(parcela.getValorParcela().setScale(2, BigDecimal.ROUND_HALF_EVEN));
		contratoCobrancaDetalhes
				.setVlrAmortizacaoParcela(parcela.getAmortizacao().setScale(2, BigDecimal.ROUND_HALF_EVEN));
		contratoCobrancaDetalhes
				.setVlrSaldoParcela(parcela.getSaldoDevedorInicial().setScale(2, BigDecimal.ROUND_HALF_EVEN));

		contratoCobrancaDetalhes.setSeguroDFI(parcela.getSeguroDFI().setScale(2, BigDecimal.ROUND_HALF_EVEN));
		contratoCobrancaDetalhes.setSeguroMIP(parcela.getSeguroMIP().setScale(2, BigDecimal.ROUND_HALF_EVEN));

//		contratoCobrancaDetalhes.setVlrRepasse(this.vlrRepasse);
//		contratoCobrancaDetalhes.setVlrRetencao(this.vlrRetencao);
//		contratoCobrancaDetalhes.setVlrComissao(this.vlrComissao);

		if (parcela.getValorParcela().compareTo(BigDecimal.ZERO) == 0) {
			contratoCobrancaDetalhes.setParcelaPaga(true);
			contratoCobrancaDetalhes.setDataPagamento(dataParcela);
			contratoCobrancaDetalhes.setVlrParcela(BigDecimal.ZERO);
		}
		
		if (DateUtil.isAfterDate(contratoCobrancaDetalhes.getDataVencimento(), DateUtil.getDataHoje()) && !contratoCobrancaDetalhes.isParcelaPaga()) {
			contratoCobrancaDetalhes.setParcelaVencida(true);
		}else 
			contratoCobrancaDetalhes.setParcelaVencida(false);

		if (DateUtil.isDataHoje(contratoCobrancaDetalhes.getDataVencimento()) && !contratoCobrancaDetalhes.isParcelaPaga()) {
			contratoCobrancaDetalhes.setParcelaVencendo(true);
		}else 
			contratoCobrancaDetalhes.setParcelaVencendo(false);
		
		return contratoCobrancaDetalhes;
		
	}

	private SimulacaoVO calcularParcelas() {
		BigDecimal tarifaIOFDiario;
		BigDecimal tarifaIOFAdicional = BigDecimal.valueOf(0.38).divide(BigDecimal.valueOf(100));

		if (this.qtdeParcelas != null) {
			this.objetoContratoCobranca.setQtdeParcelas(Integer.valueOf(this.qtdeParcelas));
		}
		
//			BigDecimal custoEmissaoValor = SiscoatConstants.CUSTO_EMISSAO_MINIMO;
//			if (this.objetoContratoCobranca.getVlrInvestimento().multiply(SiscoatConstants.CUSTO_EMISSAO_PERCENTUAL.divide(BigDecimal.valueOf(100)))
//					.compareTo(SiscoatConstants.CUSTO_EMISSAO_MINIMO) > 0) {
//				custoEmissaoValor = this.objetoContratoCobranca.getVlrInvestimento().multiply(SiscoatConstants.CUSTO_EMISSAO_PERCENTUAL.divide(BigDecimal.valueOf(100)));
//			}

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

		simulador.calcular();
		return simulador;
	}
	
	private SimulacaoVO calcularInvestimento(int numeroInvestidor){
		BigDecimal tarifaIOFDiario;
		BigDecimal tarifaIOFAdicional = BigDecimal.valueOf(0.38).divide(BigDecimal.valueOf(100));

//			BigDecimal custoEmissaoValor = SiscoatConstants.CUSTO_EMISSAO_MINIMO;
//			if (this.objetoContratoCobranca.getVlrInvestimento().multiply(SiscoatConstants.CUSTO_EMISSAO_PERCENTUAL.divide(BigDecimal.valueOf(100)))
//					.compareTo(SiscoatConstants.CUSTO_EMISSAO_MINIMO) > 0) {
//				custoEmissaoValor = this.objetoContratoCobranca.getVlrInvestimento().multiply(SiscoatConstants.CUSTO_EMISSAO_PERCENTUAL.divide(BigDecimal.valueOf(100)));
//			}
		boolean isEnvelope = false;
		
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
		
		if (numeroInvestidor == 1) {
			simulador.setValorCredito(this.objetoContratoCobranca.getVlrInvestidor1());
			simulador.setTaxaJuros(this.objetoContratoCobranca.getTaxaRemuneracaoInvestidor1());
			simulador.setQtdParcelas(BigInteger.valueOf(CommonsUtil.intValue(this.objetoContratoCobranca.getQtdeParcelasInvestidor1())));
			simulador.setTipoCalculo(this.objetoContratoCobranca.getTipoCalculoInvestidor1());
			isEnvelope = this.objetoContratoCobranca.isRecebedorEnvelope();
			simulador.setCarencia(BigInteger.valueOf( this.objetoContratoCobranca.getCarenciaInvestidor1()));
			simulador.setDataSimulacao(this.objetoContratoCobranca.getDataInicioInvestidor1());
		} else if (numeroInvestidor == 2) {
			simulador.setValorCredito(this.objetoContratoCobranca.getVlrInvestidor2());
			simulador.setTaxaJuros(this.objetoContratoCobranca.getTaxaRemuneracaoInvestidor2());
			simulador.setQtdParcelas(BigInteger.valueOf(CommonsUtil.intValue(this.objetoContratoCobranca.getQtdeParcelasInvestidor2())));
			simulador.setTipoCalculo(this.objetoContratoCobranca.getTipoCalculoInvestidor2());
			isEnvelope = this.objetoContratoCobranca.isRecebedorEnvelope2();
			simulador.setCarencia(BigInteger.valueOf( this.objetoContratoCobranca.getCarenciaInvestidor2()));
			simulador.setDataSimulacao(this.objetoContratoCobranca.getDataInicioInvestidor2());
		} else if (numeroInvestidor == 3) {
			simulador.setValorCredito(this.objetoContratoCobranca.getVlrInvestidor3());
			simulador.setTaxaJuros(this.objetoContratoCobranca.getTaxaRemuneracaoInvestidor3());
			simulador.setQtdParcelas(BigInteger.valueOf(CommonsUtil.intValue(this.objetoContratoCobranca.getQtdeParcelasInvestidor3())));
			simulador.setTipoCalculo(this.objetoContratoCobranca.getTipoCalculoInvestidor3());
			isEnvelope = this.objetoContratoCobranca.isRecebedorEnvelope3();
			simulador.setCarencia(BigInteger.valueOf( this.objetoContratoCobranca.getCarenciaInvestidor3()));
			simulador.setDataSimulacao(this.objetoContratoCobranca.getDataInicioInvestidor3());
		} else if (numeroInvestidor == 4) {
			simulador.setValorCredito(this.objetoContratoCobranca.getVlrInvestidor4());
			simulador.setTaxaJuros(this.objetoContratoCobranca.getTaxaRemuneracaoInvestidor4());
			simulador.setQtdParcelas(BigInteger.valueOf(CommonsUtil.intValue(this.objetoContratoCobranca.getQtdeParcelasInvestidor4())));
			simulador.setTipoCalculo(this.objetoContratoCobranca.getTipoCalculoInvestidor4());
			isEnvelope = this.objetoContratoCobranca.isRecebedorEnvelope4();
			simulador.setCarencia(BigInteger.valueOf( this.objetoContratoCobranca.getCarenciaInvestidor4()));
			simulador.setDataSimulacao(this.objetoContratoCobranca.getDataInicioInvestidor4());
		} else if (numeroInvestidor == 5) {
			simulador.setValorCredito(this.objetoContratoCobranca.getVlrInvestidor5());
			simulador.setTaxaJuros(this.objetoContratoCobranca.getTaxaRemuneracaoInvestidor5());
			simulador.setQtdParcelas(BigInteger.valueOf(CommonsUtil.intValue(this.objetoContratoCobranca.getQtdeParcelasInvestidor5())));
			simulador.setTipoCalculo(this.objetoContratoCobranca.getTipoCalculoInvestidor5());
			isEnvelope = this.objetoContratoCobranca.isRecebedorEnvelope5();
			simulador.setCarencia(BigInteger.valueOf( this.objetoContratoCobranca.getCarenciaInvestidor5()));
			simulador.setDataSimulacao(this.objetoContratoCobranca.getDataInicioInvestidor5());
		} else if (numeroInvestidor == 6) {
			simulador.setValorCredito(this.objetoContratoCobranca.getVlrInvestidor6());
			simulador.setTaxaJuros(this.objetoContratoCobranca.getTaxaRemuneracaoInvestidor6());
			simulador.setQtdParcelas(BigInteger.valueOf(CommonsUtil.intValue(this.objetoContratoCobranca.getQtdeParcelasInvestidor6())));
			simulador.setTipoCalculo(this.objetoContratoCobranca.getTipoCalculoInvestidor6());
			isEnvelope = this.objetoContratoCobranca.isRecebedorEnvelope6();
			simulador.setCarencia(BigInteger.valueOf( this.objetoContratoCobranca.getCarenciaInvestidor6()));
			simulador.setDataSimulacao(this.objetoContratoCobranca.getDataInicioInvestidor6());
		} else if (numeroInvestidor == 7) {
			simulador.setValorCredito(this.objetoContratoCobranca.getVlrInvestidor7());
			simulador.setTaxaJuros(this.objetoContratoCobranca.getTaxaRemuneracaoInvestidor7());
			simulador.setQtdParcelas(BigInteger.valueOf(CommonsUtil.intValue(this.objetoContratoCobranca.getQtdeParcelasInvestidor7())));
			simulador.setTipoCalculo(this.objetoContratoCobranca.getTipoCalculoInvestidor7());
			isEnvelope = this.objetoContratoCobranca.isRecebedorEnvelope7();
			simulador.setCarencia(BigInteger.valueOf( this.objetoContratoCobranca.getCarenciaInvestidor7()));
			simulador.setDataSimulacao(this.objetoContratoCobranca.getDataInicioInvestidor7());
		} else if (numeroInvestidor == 8) {
			simulador.setValorCredito(this.objetoContratoCobranca.getVlrInvestidor8());
			simulador.setTaxaJuros(this.objetoContratoCobranca.getTaxaRemuneracaoInvestidor8());
			simulador.setQtdParcelas(BigInteger.valueOf(CommonsUtil.intValue(this.objetoContratoCobranca.getQtdeParcelasInvestidor8())));
			simulador.setTipoCalculo(this.objetoContratoCobranca.getTipoCalculoInvestidor8());
			isEnvelope = this.objetoContratoCobranca.isRecebedorEnvelope8();
			simulador.setCarencia(BigInteger.valueOf( this.objetoContratoCobranca.getCarenciaInvestidor8()));
			simulador.setDataSimulacao(this.objetoContratoCobranca.getDataInicioInvestidor8());
		} else if (numeroInvestidor == 9) {
			simulador.setValorCredito(this.objetoContratoCobranca.getVlrInvestidor9());
			simulador.setTaxaJuros(this.objetoContratoCobranca.getTaxaRemuneracaoInvestidor9());
			simulador.setQtdParcelas(BigInteger.valueOf(CommonsUtil.intValue(this.objetoContratoCobranca.getQtdeParcelasInvestidor9())));
			simulador.setTipoCalculo(this.objetoContratoCobranca.getTipoCalculoInvestidor9());
			isEnvelope = this.objetoContratoCobranca.isRecebedorEnvelope9();
			simulador.setCarencia(BigInteger.valueOf( this.objetoContratoCobranca.getCarenciaInvestidor9()));
			simulador.setDataSimulacao(this.objetoContratoCobranca.getDataInicioInvestidor9());
		} else if (numeroInvestidor == 10) {
			simulador.setValorCredito(this.objetoContratoCobranca.getVlrInvestidor10());
			simulador.setTaxaJuros(this.objetoContratoCobranca.getTaxaRemuneracaoInvestidor10());
			simulador.setQtdParcelas(BigInteger.valueOf(CommonsUtil.intValue(this.objetoContratoCobranca.getQtdeParcelasInvestidor10())));
			simulador.setTipoCalculo(this.objetoContratoCobranca.getTipoCalculoInvestidor10());
			isEnvelope = this.objetoContratoCobranca.isRecebedorEnvelope10();
			simulador.setCarencia(BigInteger.valueOf( this.objetoContratoCobranca.getCarenciaInvestidor10()));
			simulador.setDataSimulacao(this.objetoContratoCobranca.getDataInicioInvestidor10());
		}
		
		if( simulador.getQtdParcelas().compareTo(BigInteger.ZERO)==0) {
			return null;
		}
		
		if (isEnvelope) {
			simulador.setTipoCalculo("Envelope");
		}
		
		//simulador.setDataSimulacao(DateUtil.getDataHoje());
		simulador.setTarifaIOFDiario(tarifaIOFDiario);
		simulador.setTarifaIOFAdicional(tarifaIOFAdicional);
		simulador.setSeguroMIP(BigDecimal.ZERO);
		simulador.setSeguroDFI(BigDecimal.ZERO);
		// valores		
		simulador.setValorImovel(this.objetoContratoCobranca.getValorImovel());
//			simulador.setCustoEmissaoValor(custoEmissaoValor);		
		simulador.setNaoCalcularDFI(true);
		simulador.setNaoCalcularMIP(true);
		simulador.calcular();
		return simulador;
	}

	private SimulacaoVO calcularReParcelamento() {
		BigDecimal tarifaIOFDiario;
		BigDecimal tarifaIOFAdicional = SiscoatConstants.TARIFA_IOF_ADICIONAL.divide(BigDecimal.valueOf(100));

//			BigDecimal custoEmissaoValor = SiscoatConstants.CUSTO_EMISSAO_MINIMO;
//			if (this.objetoContratoCobranca.getVlrInvestimento().multiply(SiscoatConstants.CUSTO_EMISSAO_PERCENTUAL.divide(BigDecimal.valueOf(100)))
//					.compareTo(SiscoatConstants.CUSTO_EMISSAO_MINIMO) > 0) {
//				custoEmissaoValor = this.objetoContratoCobranca.getVlrInvestimento().multiply(SiscoatConstants.CUSTO_EMISSAO_PERCENTUAL.divide(BigDecimal.valueOf(100)));
//			}

		SimulacaoVO simulador = new SimulacaoVO();

		if ( CommonsUtil.intValue(this.qtdeParcelas) >= this.objetoContratoCobranca.getQtdeParcelas() &&
				this.objetoContratoCobranca.isGeraParcelaFinal()
				&& !CommonsUtil.semValor(this.objetoContratoCobranca.getVlrParcelaFinal())) {
			//this.objetoContratoCobranca.setQtdeParcelas(this.objetoContratoCobranca.getQtdeParcelas() + 1);
			this.setQtdeParcelas(CommonsUtil.stringValue(this.objetoContratoCobranca.getQtdeParcelas()));
			this.objetoContratoCobranca.setGeraParcelaFinal(false);
		}

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
		simulador.setValorCredito(this.saldoDevedorReparcelamento);
		simulador.setTaxaJuros(this.objetoContratoCobranca.getTxJurosParcelas());
		simulador.setCarencia(this.carenciaReparcelamento);
		if (this.numeroParcelaReparcelamento.compareTo(BigInteger.ZERO) == 1) {
			simulador.setQtdParcelas(BigInteger.valueOf(this.objetoContratoCobranca.getQtdeParcelas())
					.subtract(this.numeroParcelaReparcelamento.subtract(BigInteger.ONE)));
		} else {
			simulador.setQtdParcelas(BigInteger.valueOf(this.objetoContratoCobranca.getQtdeParcelas()));
		}

		simulador.setValorImovel(this.objetoContratoCobranca.getValorImovel());
//			simulador.setCustoEmissaoValor(custoEmissaoValor);
		simulador.setTipoCalculo(this.objetoContratoCobranca.getTipoCalculo());
		simulador.setNaoCalcularDFI(
				!(this.objetoContratoCobranca.isTemSeguroDFI() && this.objetoContratoCobranca.isTemSeguro()));
		simulador.setNaoCalcularMIP(
				!(this.objetoContratoCobranca.isTemSeguroMIP() && this.objetoContratoCobranca.isTemSeguro()));

		simulador.calcular();
		if (this.numeroParcelaReparcelamento.compareTo(BigInteger.ZERO) != 0) {
			simulador.getParcelas().remove(0);
			for (SimulacaoDetalheVO parcela : simulador.getParcelas()) {
				parcela.setNumeroParcela(
						parcela.getNumeroParcela().add(numeroParcelaReparcelamento).subtract(BigInteger.ONE));
			}
		}
		return simulador;

	}

	public void mostrarParcela() {
		try {
			this.simuladorParcelas = calcularParcelas();
		} catch (Exception e) {
		}
	}
	
	public void mostrarInvestimento(int numeroInvestidor) {
		try {
			this.simuladorParcelas = calcularInvestimento(numeroInvestidor);
		} catch (Exception e) {
		}
	}

	public void mostrarReParcelamento() {
		try {			
			this.objetoContratoCobranca.setQtdeParcelas(CommonsUtil.intValue(this.qtdeParcelas));
			this.simuladorParcelas = calcularReParcelamento();
		} catch (Exception e) {
		}
	}

	public void concluirReparcelamento() {
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		IPCADao ipcaDao = new IPCADao();
		ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
		
		BigInteger ultimaParcela = BigInteger.ZERO;
		boolean geraDataVencimento = this.numeroParcelaReparcelamento.compareTo(BigInteger.ZERO)==0;
		
		Date dataVencimentoNova = null;
		if ( geraDataVencimento) {
			dataVencimentoNova = this.objetoContratoCobranca.getDataInicio();
		}else {
			dataVencimentoNova = this.dataParcela;
		}
			
		
		for (SimulacaoDetalheVO parcela : this.simuladorParcelas.getParcelas()) {
			boolean encontrouParcela = false;
			BigDecimal saldoAnterior = BigDecimal.ZERO;
			
			for (ContratoCobrancaDetalhes detalhe : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
				
				
				if (CommonsUtil.mesmoValor(parcela.getNumeroParcela().toString(), detalhe.getNumeroParcela())) {
					
					Date dataParcela =null;
					
					if (!detalhe.isAmortizacao())
						dataParcela = contratoCobrancaDao
								.geraDataParcela((CommonsUtil.intValue(parcela.getNumeroParcela())
										- this.numeroParcelaReparcelamento.intValue()), dataVencimentoNova);
					
					if ( detalhe.isParcelaPaga()) {
						
						if ( CommonsUtil.mesmoValor(BigInteger.ZERO, this.numeroParcelaReparcelamento)) {
							detalhe.setDataVencimento(dataParcela);
							detalhe.setVlrSaldoInicial(saldoAnterior);
							detalhe.setVlrSaldoParcela(parcela.getSaldoDevedorInicial());
						}
						
						encontrouParcela = true;
						if ( CommonsUtil.mesmoValor(BigDecimal.ZERO, parcela.getValorParcela()))
							break;		
						
						if ( CommonsUtil.mesmoValor(BigDecimal.ZERO, detalhe.getVlrParcela()))
							detalhe.setParcelaPaga(false);
					}
					
					
					if (detalhe.getDataVencimentoAtual().compareTo(detalhe.getDataVencimento()) < 1) {
						detalhe.setDataVencimentoAtual(dataParcela);
					}
					
					detalhe.setDataVencimento(dataParcela);
					
					detalhe.setVlrSaldoInicial(saldoAnterior);
					detalhe.setVlrSaldoParcela(
							parcela.getSaldoDevedorInicial().setScale(2, BigDecimal.ROUND_HALF_EVEN));
					detalhe.setVlrParcela(parcela.getValorParcela().setScale(2, BigDecimal.ROUND_HALF_EVEN));
					detalhe.setVlrJurosParcela(parcela.getJuros().setScale(2, BigDecimal.ROUND_HALF_EVEN));
					detalhe.setVlrAmortizacaoParcela(parcela.getAmortizacao().setScale(2, BigDecimal.ROUND_HALF_EVEN));
					detalhe.setSeguroDFI(parcela.getSeguroDFI());
					detalhe.setSeguroMIP(parcela.getSeguroMIP());
					if (parcela.getValorParcela().compareTo(BigDecimal.ZERO) == 0) {
						detalhe.setParcelaPaga(true);
						detalhe.setDataPagamento(detalhe.getDataVencimento());
						detalhe.setVlrParcela(BigDecimal.ZERO);
					}
					
					if (DateUtil.isAfterDate(detalhe.getDataVencimento(), DateUtil.getDataHoje()) && !detalhe.isParcelaPaga()) {
						detalhe.setParcelaVencida(true);
					}else 
						detalhe.setParcelaVencida(false);

					if (DateUtil.isDataHoje(detalhe.getDataVencimento()) && !detalhe.isParcelaPaga()) {
						detalhe.setParcelaVencendo(true);
					}else 
						detalhe.setParcelaVencendo(false);
					
					if (!CommonsUtil.semValor(detalhe.getIpca()) ) {
						detalhe.setIpca(null);
						calcularIPCA(ipcaDao, contratoCobrancaDetalhesDao,detalhe);						
					}
					
					encontrouParcela = true;
					break;
				}
				saldoAnterior = detalhe.getVlrSaldoParcela();
			}
			if (!encontrouParcela) {
				this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
						.add(criaContratoCobrancaDetalhe(contratoCobrancaDao, parcela, dataVencimentoNova, saldoAnterior));
			}

			ultimaParcela = parcela.getNumeroParcela();
		}
		
		// valida se tem parcela para se retirada, tem que ser ao contrario o for
		for (Integer iDetalhe = this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size()
				- 1; iDetalhe >= 0; iDetalhe--) {
			ContratoCobrancaDetalhes detalhe = this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
					.get(iDetalhe);
			if (!CommonsUtil.mesmoValor(detalhe.getNumeroParcela(), "Armotização") && !detalhe.isParcelaPaga()) {
				if (CommonsUtil.intValue(detalhe.getNumeroParcela()) > ultimaParcela.intValue()) {
					this.objetoContratoCobranca.getListContratoCobrancaDetalhes().remove(detalhe);
				} 
			}
		}
		
		
		
		
		
		
	}

	public void reparcelarPelaUltimaParcelaValidada() {
		this.simuladorParcelas = new SimulacaoVO();
		
		
		for (int iDetalhe = 0; iDetalhe < this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
				.size(); iDetalhe++) {
			ContratoCobrancaDetalhes detalhe = this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
					.get(iDetalhe);
			if (!detalhe.isParcelaPaga()) {
				if (CommonsUtil.mesmoValor(iDetalhe, 0)) {
					this.setNumeroParcelaReparcelamento(
							BigInteger.valueOf(CommonsUtil.intValue(detalhe.getNumeroParcela())));
					this.setDataParcela(detalhe.getDataVencimento());
					this.setCarenciaReparcelamento(BigInteger.valueOf(objetoContratoCobranca.getMesesCarencia()));
					if (detalhe.getVlrSaldoParcela() != null) {
						this.setSaldoDevedorReparcelamento(detalhe.getVlrSaldoParcela());
					} else {
						this.setSaldoDevedorReparcelamento(this.objetoContratoCobranca.getValorCCB());
					}
				}				
				break;
			} else {
				this.setSaldoDevedorReparcelamento(detalhe.getVlrSaldoParcela());
				if (!detalhe.isAmortizacao()) {
					
					ContratoCobrancaDetalhes detalheProximo = this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
							.get(iDetalhe+1);
					if (!detalheProximo.isAmortizacao()) {
						this.setDataParcela(detalheProximo.getDataVencimento());
						this.setNumeroParcelaReparcelamento(
								BigInteger.valueOf(CommonsUtil.intValue(detalheProximo.getNumeroParcela())));
					}
				}
				this.setCarenciaReparcelamento(BigInteger.ZERO);
			}
		}
	}
	
	public void concluirReparcelamentoAutomatico() {
		FacesContext context = FacesContext.getCurrentInstance();
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		try {
			contratoCobrancaDao.merge(this.objetoContratoCobranca);
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Contrato Cobrança: As parcelas foram re-geradas com sucesso!!!", ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contrato Cobrança: " + e, ""));
		}
	}
	
	public List<String> contaPagarDescricaoLista(){
		List<String> listaNome = new ArrayList<>();
		listaNome.add("Cartório");
		listaNome.add("Honorário");
		listaNome.add("IPTU");
		listaNome.add("Condomínio");
		listaNome.add("Processo");
		listaNome.add("IQ");
		listaNome.add("Outros");
		return listaNome.stream().collect(Collectors.toList());
	}
	
	public void pesquisaSegurado() {
		this.tituloPagadorRecebedorDialog = "Segurados";
		this.tipoPesquisaPagadorRecebedor = "Segurado";
		this.updatePagadorRecebedor = ":form:SeguradoresPanel";
		this.seguradoSelecionado = new Segurado();
		this.seguradoSelecionado.setPessoa(new PagadorRecebedor());
	}
	
	public void pesquisaSeguradoConsulta() {
		this.tituloPagadorRecebedorDialog = "Segurados";
		this.tipoPesquisaPagadorRecebedor = "Segurado";
		this.updatePagadorRecebedor = ":formSegurados:SeguradoresPanel";
		this.seguradoSelecionado = new Segurado();
		this.seguradoSelecionado.setPessoa(new PagadorRecebedor());
	}
	
	public void pesquisaPagador() {
			this.tituloPagadorRecebedorDialog = "Pagadores";
			this.tipoPesquisaPagadorRecebedor = "Pagador";
			this.updatePagadorRecebedor = ":form:OutrosPagadores";
			this.pagadorSecundarioSelecionado = new PagadorRecebedorAdicionais();
			this.pagadorSecundarioSelecionado.setPessoa(new PagadorRecebedor());
	}
	
	public void pesquisaSocio() {
		
		this.tituloPagadorRecebedorDialog = "Sócios";
		this.tipoPesquisaPagadorRecebedor = "Socio";
		this.updatePagadorRecebedor = ":form:SociosPanel";
		this.socioSelecionado = new PagadorRecebedorSocio();
		this.socioSelecionado.setPessoa(new PagadorRecebedor());
	}
	
	public void populateSelectedPagadorRecebedor() {
		if ( CommonsUtil.mesmoValor("Segurado", tipoPesquisaPagadorRecebedor)) {
			this.seguradoSelecionado.setPessoa(this.selectedPagadorGenerico);
		} else if ( CommonsUtil.mesmoValor("Socio", tipoPesquisaPagadorRecebedor)) {
			this.socioSelecionado.setPessoa(this.selectedPagadorGenerico);
		} else if ( CommonsUtil.mesmoValor("Pagador", tipoPesquisaPagadorRecebedor)) {
			this.pagadorSecundarioSelecionado.setPessoa(this.selectedPagadorGenerico);
		}
	}
	
	public void concluirSegurado() {
		this.tituloPagadorRecebedorDialog = "";
		this.tipoPesquisaPagadorRecebedor = "";
		this.updatePagadorRecebedor = "";
		this.seguradoSelecionado.setContratoCobranca(this.objetoContratoCobranca);
		this.objetoContratoCobranca.getListSegurados().add(this.seguradoSelecionado);
		this.seguradoSelecionado = new Segurado();
		this.seguradoSelecionado.setPessoa(new PagadorRecebedor());
		this.addSegurador= false;
	}
	
	public void concluirPagador() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			this.pagadorSecundarioSelecionado.setContratoCobranca(this.objetoContratoCobranca);
			this.pagadorSecundarioSelecionado.setNomeParticipanteCheckList(this.pagadorSecundarioSelecionado.getPessoa().getNome());
			this.pagadorSecundarioSelecionado.getPessoa().setNomeCC(this.pagadorSecundarioSelecionado.getPessoa().getNome());
			this.pagadorSecundarioSelecionado.getPessoa().setCpfCC(this.pagadorSecundarioSelecionado.getPessoa().getCpf());
			this.objetoContratoCobranca.getListaPagadores().add(this.pagadorSecundarioSelecionado);
			criarPagadorRecebedorNoSistema(this.pagadorSecundarioSelecionado.getPessoa());
			this.pagadorSecundarioSelecionado = new PagadorRecebedorAdicionais();
			this.pagadorSecundarioSelecionado.setPessoa(new PagadorRecebedor());
			this.addPagador = false;
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contrato Cobrança: " + e, ""));
		}
	} 
	
	public void concluirSocio() {
		this.socioSelecionado.setContratoCobranca(this.objetoContratoCobranca);
		this.socioSelecionado.setNomeParticipanteCheckList(this.socioSelecionado.getPessoa().getNome());
		this.socioSelecionado.getPessoa().setNomeCC(this.socioSelecionado.getPessoa().getNome());
		this.socioSelecionado.getPessoa().setCpfCC(this.socioSelecionado.getPessoa().getCpf());
		this.objetoContratoCobranca.getListSocios().add(this.socioSelecionado);
		criarPagadorRecebedorNoSistema(this.socioSelecionado.getPessoa());
		this.socioSelecionado = new PagadorRecebedorSocio();
		this.socioSelecionado.setPessoa(new PagadorRecebedor());
		this.addSocio = false;
	}
	
	public void concluirConta() {
		this.contasPagarSelecionada.setContrato(this.objetoContratoCobranca);
		this.contasPagarSelecionada.setNumeroDocumento(this.objetoContratoCobranca.getNumeroContrato());
		this.contasPagarSelecionada.setPagadorRecebedor(this.objetoPagadorRecebedor);
		this.contasPagarSelecionada.setTipoDespesa("C");
		this.contasPagarSelecionada.setResponsavel(this.objetoContratoCobranca.getResponsavel());
		if(!CommonsUtil.semValor(this.contasPagarSelecionada.getValor())) {
			if(!CommonsUtil.semValor(this.objetoContratoCobranca.getContaPagarValorTotal())) {
				this.objetoContratoCobranca.getContaPagarValorTotal().add(this.contasPagarSelecionada.getValor());
			} else {
				this.objetoContratoCobranca.setContaPagarValorTotal(this.contasPagarSelecionada.getValor());
			}
		}
		if(this.contasPagarSelecionada.isContaPaga() && CommonsUtil.semValor(this.contasPagarSelecionada.getDataPagamento())) {
			this.contasPagarSelecionada.setDataPagamento(gerarDataHoje());
		}	
		this.objetoContratoCobranca.getListContasPagar().add(this.contasPagarSelecionada);
		this.contasPagarSelecionada = new ContasPagar();
		this.addContasPagar = false;
	}

	public void concluirComite(ContratoCobranca contrato) {
		BigDecimal maiorTaxaAprovada = BigDecimal.ZERO;
		BigInteger menorPrazoAprovado = BigInteger.valueOf(999999999);
		BigDecimal menorValorAprovado = BigDecimal.valueOf(999999999);
		String menorValorAprovadoTipo = "";
		String comentarioComiteFinal = "";
		
		for (AnaliseComite comite : contrato.getListaAnaliseComite()) {
			if(comite.getTaxaComite().compareTo(maiorTaxaAprovada) >= 0) {
				maiorTaxaAprovada = comite.getTaxaComite();
			}
			if(comite.getPrazoMaxComite().compareTo(menorPrazoAprovado) <= 0) {
				menorPrazoAprovado = comite.getPrazoMaxComite();
			}
			if(comite.getValorComite().compareTo(menorValorAprovado) <= 0) {
				menorValorAprovado = comite.getValorComite();
				menorValorAprovadoTipo = comite.getTipoValorComite();
			}
			comentarioComiteFinal += comite.getUsuarioComite() + ": " + comite.getComentarioComite() + "  //  ";
		}
		contrato.setTaxaAprovada(maiorTaxaAprovada);
		contrato.setPrazoMaxAprovado(menorPrazoAprovado);
		contrato.setValorAprovadoComite(menorValorAprovado);
		contrato.setTipoValorComite(menorValorAprovadoTipo);
		contrato.setComentarioComite(comentarioComiteFinal);
	}
	
	public void editarSocio(PagadorRecebedorSocio socio) {
		this.addSocio = true;
		this.socioSelecionado = new PagadorRecebedorSocio();
		this.setSocioSelecionado(socio);
		this.removerSocio(socio);
	}
	
	public void editarPagador(PagadorRecebedorAdicionais pagador) {
		this.addPagador = true;
		this.pagadorSecundarioSelecionado = new PagadorRecebedorAdicionais();
		this.setPagadorSecundarioSelecionado(pagador);
		this.removerPagador(pagador);
	}
	
	public void editarConta(ContasPagar conta) {
		this.addContasPagar = true;
		this.contasPagarSelecionada = new ContasPagar();
		this.setContasPagarSelecionada(conta);
		this.removerConta(conta);
	}
	
	public void removerSegurado(Segurado segurado) {
		this.objetoContratoCobranca.getListSegurados().remove(segurado);		
	}
	
	public void removerPagador(PagadorRecebedorAdicionais pagador) {
		this.objetoContratoCobranca.getListaPagadores().remove(pagador);		
	}
	
	public void removerSocio(PagadorRecebedorSocio socio) {
		this.objetoContratoCobranca.getListSocios().remove(socio);
	}
	
	public void removerConta(ContasPagar conta) {
		this.objetoContratoCobranca.getListContasPagar().remove(conta);
	}
	
	public void criarPagadorRecebedorNoSistema(PagadorRecebedor pagador) {
		PagadorRecebedor pagadorRecebedor = null;
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();

		if (pagador.getId() <= 0) {
			List<PagadorRecebedor> pagadorRecebedorBD = new ArrayList<PagadorRecebedor>();
			boolean registraPagador = false;
			Long idPagador = (long) 0;

			if (pagador.getCpf() != null) {
				boolean validaCPF = ValidaCPF.isCPF(pagador.getCpf());
				if(validaCPF) {
					pagadorRecebedorBD = pagadorRecebedorDao.findByFilter("cpf", pagador.getCpf());
					if (pagadorRecebedorBD.size() > 0) {
						pagadorRecebedor = pagadorRecebedorBD.get(0);
					} else {
						pagadorRecebedor = pagador;
						registraPagador = true;
					}
				}
			}
			
			if (pagador.getCnpj() != null) {
				boolean validaCNPJ = ValidaCNPJ.isCNPJ(pagador.getCnpj());
				if(validaCNPJ) {
					pagadorRecebedorBD = pagadorRecebedorDao.findByFilter("cnpj", pagador.getCnpj());
					if (pagadorRecebedorBD.size() > 0) {
						pagadorRecebedor = pagadorRecebedorBD.get(0);
					} else {
						pagadorRecebedor = pagador;
						registraPagador = true;
					}
				}
			}

			registraPagador = true;


			if (pagadorRecebedor == null) {
				pagadorRecebedor = pagador;
			}

			if (pagador.getSite() != null && pagador.getSite().equals("")) {
				if (!pagador.getSite().contains("http")) {
					pagador
							.setSite("HTTP://" + pagador.getSite().toLowerCase());
				}
			}

			if (registraPagador) {
				idPagador = pagadorRecebedorDao.create(pagadorRecebedor);
				pagadorRecebedor = pagadorRecebedorDao.findById(idPagador);
			}
		} else {
			pagadorRecebedorDao.merge(pagador);
			pagadorRecebedor = pagador;
		}
	}

	private boolean validarProcentagensSeguro() {
		if (!this.objetoContratoCobranca.isTemSeguro())
			return true;
		
		BigDecimal totalPorcentagem = BigDecimal.ZERO;	
		for (Segurado seguro : this.getListSegurado()) {
			totalPorcentagem = totalPorcentagem.add(seguro.getPorcentagemSegurador());			
		} 
		
		if (totalPorcentagem.compareTo(BigDecimal.valueOf(100)) != 0){
			return false;
		} else {
			return true;
		}
	}
	
	public void editarSeguradosConsulta() {
		FacesContext context = FacesContext.getCurrentInstance();
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();
		String msgRetorno = null;

		this.tituloPagadorRecebedorDialog = "";
		this.tipoPesquisaPagadorRecebedor = "";
		this.updatePagadorRecebedor = "";
		this.seguradoSelecionado = new Segurado();
		this.seguradoSelecionado.setPessoa(new PagadorRecebedor());
		
		if (!this.validarProcentagensSeguro()) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"A soma das porcentagens dos segurados não é 100%", ""));
		} else {
			contratoCobrancaDao.merge(objetoContratoCobranca);
			msgRetorno = "atualizado";
		
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
			"Contrato Cobrança: Registro " + msgRetorno + " com sucesso!", ""));
		}		
	}
	
	public void cancelarSeguradosConsulta() {
		this.tituloPagadorRecebedorDialog = "";
		this.tipoPesquisaPagadorRecebedor = "";
		this.updatePagadorRecebedor = "";
		this.seguradoSelecionado = new Segurado();
		this.seguradoSelecionado.setPessoa(new PagadorRecebedor());
	}
	
	private BigDecimal calcularValorTotalContasPagar() {
		BigDecimal valorTotalContasPagarNovo = BigDecimal.ZERO;
		for (ContasPagar conta : this.objetoContratoCobranca.getListContasPagar())
			if(!CommonsUtil.semValor(conta.getValor())) {
				valorTotalContasPagarNovo = valorTotalContasPagarNovo.add(conta.getValor());
		}
		
		return valorTotalContasPagarNovo;
	}
	
	public void clearPagadorRecebedor() {
		if (CommonsUtil.mesmoValor("Segurado", tipoPesquisaPagadorRecebedor)) {
			this.seguradoSelecionado.setPessoa(null);
			this.selectedPagador = new PagadorRecebedor();
		} else if (CommonsUtil.mesmoValor("Socio", tipoPesquisaPagadorRecebedor)) {
			this.socioSelecionado.setPessoa(null);
			this.selectedPagador = new PagadorRecebedor();
		}
	}
//	public void mostrarParcela() {
//		try {
//			this.simuladorParcelas = calcularParcelas();
//		} catch (Exception e) {}
//	}
//	
	public void atualizaVlrPago() {
		this.listContratoCobrancaFavorecidos.get(0)
				.setVlrRecebido(this.selectedContratoCobrancaDetalhes.getVlrParcelaAtualizada());
	}

	public String loadParcelaBaixar(ContratoCobrancaDetalhes selectedContratoCobrancaDetalhes) {

		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar data = Calendar.getInstance(zone, locale);

		this.selectedContratoCobrancaDetalhes = selectedContratoCobrancaDetalhes;

		// seta a data da baixa para a data corrente
		this.selectedContratoCobrancaDetalhes.setDataPagamento(data.getTime());

		// set o valor a ser depositado como valor pago na parcela
		this.selectedContratoCobrancaDetalhes
				.setVlrParcelaAtualizada(this.selectedContratoCobrancaDetalhes.getVlrRepasse());

		if (this.selectedContratoCobrancaDetalhes.getListContratoCobrancaFavorecidos().size() == 0) {
			// tem lista de favorecido pensando em baixa pra mais de um favorecido
			ContratoCobrancaFavorecidos contratoCobrancaFavorecidos = new ContratoCobrancaFavorecidos();
			contratoCobrancaFavorecidos.setNome(this.objetoContratoCobranca.getRecebedor().getNome());
			if (this.objetoContratoCobranca.getRecebedor().getCpf() != null) {
				contratoCobrancaFavorecidos.setCpf(this.objetoContratoCobranca.getRecebedor().getCpf());
				this.setTipoPessoaIsFisica(true);
			} else {
				if (this.objetoContratoCobranca.getRecebedor().getCnpj() != null) {
					contratoCobrancaFavorecidos.setCnpj(this.objetoContratoCobranca.getRecebedor().getCnpj());
					this.setTipoPessoaIsFisica(false);
				}
			}

			List<ContratoCobrancaFavorecidos> listContratoCobrancaFavorecidos = new ArrayList<ContratoCobrancaFavorecidos>();
			listContratoCobrancaFavorecidos.add(contratoCobrancaFavorecidos);

			this.selectedContratoCobrancaDetalhes.setListContratoCobrancaFavorecidos(listContratoCobrancaFavorecidos);

			// se não há saldo copia o valor da parcela, que é referente ao total da parcela
			if (this.selectedContratoCobrancaDetalhes.getVlrSaldoParcela() == null) {
				this.selectedContratoCobrancaDetalhes
						.setVlrSaldoParcela(this.selectedContratoCobrancaDetalhes.getVlrParcela());
			}
		}

		return "ContratoCobrancaBaixarProcessar.xhtml";
	}

	public String estornarBaixaParcela() {
		FacesContext context = FacesContext.getCurrentInstance();

		ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dtHoje = Calendar.getInstance(zone, locale);

		/*
		 * // verifica se a parcela ja teve uma baixa if
		 * (this.selectedContratoCobrancaDetalhes.isParcelaPaga()) { // se sim, verifica
		 * se o valor digitado é diferente do valor da parcela (isso significa correção
		 * de operação) if
		 * (this.selectedContratoCobrancaDetalhes.getVlrParcelaAtualizada() !=
		 * this.selectedContratoCobrancaDetalhes.getVlrRepasse()) { // se sim, corrigi o
		 * valor pago e seta a parcela como não baixada
		 * this.selectedContratoCobrancaDetalhes.setVlrSaldoParcela(this.
		 * selectedContratoCobrancaDetalhes.getVlrRepasse().subtract(this.
		 * selectedContratoCobrancaDetalhes.getListContratoCobrancaFavorecidos().get(0).
		 * getVlrRecebido()));
		 * this.selectedContratoCobrancaDetalhes.setParcelaPaga(false); } } else { // se
		 * parcela não baixada // verifica se o valor a ser baixado é o Total da parcela
		 * ou o total do saldo da parcela //TODO baixa parcial //if
		 * (this.selectedContratoCobrancaDetalhes.getVlrParcelaAtualizada().equals(this.
		 * selectedContratoCobrancaDetalhes.getVlrParcela()) //||
		 * this.selectedContratoCobrancaDetalhes.getVlrParcelaAtualizada().equals(this.
		 * selectedContratoCobrancaDetalhes.getVlrSaldoParcela())) { if
		 * (this.selectedContratoCobrancaDetalhes.getListContratoCobrancaFavorecidos().
		 * get(0).getVlrRecebido().equals(this.selectedContratoCobrancaDetalhes.
		 * getVlrParcela())) { // se sim, seta a parcela como baixada e zera os saldos
		 * this.selectedContratoCobrancaDetalhes.setParcelaPaga(true);
		 * this.selectedContratoCobrancaDetalhes.setVlrSaldoParcela(BigDecimal.ZERO);
		 * this.selectedContratoCobrancaDetalhes.setVlrParcelaAtualizada(this.
		 * selectedContratoCobrancaDetalhes.getVlrParcela()); } else { // se não,
		 * calcula o saldo que ficará na parcela e deixa ela em aberto
		 * this.selectedContratoCobrancaDetalhes.setVlrSaldoParcela(this.
		 * selectedContratoCobrancaDetalhes.getVlrParcela().subtract(this.
		 * selectedContratoCobrancaDetalhes.getListContratoCobrancaFavorecidos().get(0).
		 * getVlrRecebido())); } }
		 * 
		 * //this.selectedContratoCobrancaDetalhes.setListContratoCobrancaFavorecidos(
		 * this.listContratoCobrancaFavorecidos);
		 */
		if (this.selectedListContratoCobrancaDetalhes.size() > 0) {

			for (ContratoCobrancaDetalhes c : this.selectedListContratoCobrancaDetalhes) {
				c.setParcelaPaga(false);
				c.setVlrSaldoParcela(c.getVlrParcela());
				c.setVlrParcelaAtualizada(c.getVlrParcelaAtualizada());
				c.setDataPagamento(null);

				if (c.getDataVencimentoAtual().before(dtHoje.getTime()) && !c.isParcelaPaga()) {
					c.setParcelaVencida(true);
				}

				if (c.getDataVencimentoAtual().equals(dtHoje.getTime()) && !c.isParcelaPaga()) {
					c.setParcelaVencendo(true);
				}

				contratoCobrancaDetalhesDao.merge(c);
			}

			this.selectedListContratoCobrancaDetalhes = new ArrayList<ContratoCobrancaDetalhes>();

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Contrato Cobrança: Parcela(s) estornada(s) com sucesso!", ""));

			// limpa campos
			/*
			 * this.relIsRelAtraso = false; this.relIsCompleto = true; Calendar dataInicio =
			 * Calendar.getInstance(zone, locale); this.relDataContratoInicio =
			 * dataInicio.getTime(); this.relDataContratoFim = dataInicio.getTime();
			 * this.relObjetoContratoCobranca = new
			 * ArrayList<RelatorioFinanceiroCobranca>();
			 * this.selectedContratoCobrancaDetalhes = new ContratoCobrancaDetalhes();
			 * 
			 * clearPagador(); clearRecebedor(); clearRecebedor2(); clearResponsavel();
			 * 
			 * this.contratoGerado = false;
			 */

			return "/Atendimento/Cobranca/ContratoCobrancaBaixar.xhtml";
		} else {
			return "";
		}
	}

	/* BAIXA PARCIAL */
	public void setBpContratoCobrancaDetalhesCustom(ContratoCobrancaDetalhes bpContratoCobrancaDetalhes) {
		// zerando variáveis
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		dataHoje.set(Calendar.HOUR_OF_DAY, 0);
		dataHoje.set(Calendar.MINUTE, 0);
		dataHoje.set(Calendar.SECOND, 0);
		dataHoje.set(Calendar.MILLISECOND, 0);

		// nova data
		this.rowEditNewDate = dataHoje.getTime();
		this.vlrRecebido = null;

		this.bpContratoCobrancaDetalhes = bpContratoCobrancaDetalhes;

		this.vlrParcelaNew = this.bpContratoCobrancaDetalhes.getVlrParcela();

		this.observacao = null;

		/***
		 * inicio - uso na dialog de geração de boletos
		 */
		this.dataVencimentoBoleto = dataHoje.getTime();

		this.valorBoleto = BigDecimal.ZERO;

		if (this.bpContratoCobrancaDetalhes != null) {
			if (this.bpContratoCobrancaDetalhes.getVlrParcelaAtualizada() != null) {
				if (this.bpContratoCobrancaDetalhes.getVlrParcelaAtualizada().compareTo(BigDecimal.ZERO) == 0) {
					valorBoleto = this.bpContratoCobrancaDetalhes.getVlrParcela();
				} else {
					valorBoleto = this.bpContratoCobrancaDetalhes.getVlrParcelaAtualizada();
				}
			} else {
				valorBoleto = this.bpContratoCobrancaDetalhes.getVlrParcela();
			}
		}
		/***
		 * fim - uso na dialog de geração de boletos
		 */

		if (this.bpContratoCobrancaDetalhes.getVlrParcelaAtualizada() == null) {
			this.bpContratoCobrancaDetalhes.setVlrParcelaAtualizada(this.bpContratoCobrancaDetalhes.getVlrParcela());
			this.vlrParcelaAtualizadaNew = this.bpContratoCobrancaDetalhes.getVlrParcela();
		} else {
			this.vlrParcelaAtualizadaNew = this.bpContratoCobrancaDetalhes.getVlrParcelaAtualizada();
		}
	}

	/* BAIXA PARCIAL */
	/* refaz o valor atualizado de acordo com a data selecionada na baixa parcial */

	public void verificaTaxaZero() {
		if (!this.txZero) {
			this.vlrParcelaAtualizadaNew = this.bpContratoCobrancaDetalhes.getVlrParcela();
		} else {
			calculaNovaData(this.rowEditNewDate);
		}
	}

	public void atualizaValorBaixaParcelada(SelectEvent event) {
		Date dateSelected = (Date) event.getObject();
		this.rowEditNewDate = dateSelected;
		calculaNovaData(dateSelected);
		calcularValorPresenteParcelaData(this.rowEditNewDate, this.bpContratoCobrancaDetalhes);
		this.setVlrRecebido(this.valorPresenteParcela);
	}
	
	public void atualizaValorBaixaPresente() {
		calculaNovaData(rowEditNewDate);
		calcularValorPresenteParcelaData(this.rowEditNewDate, this.bpContratoCobrancaDetalhes);
		this.setVlrRecebido(this.valorPresenteParcela);
	}

	public void calculaNovaData(Date novaData) {
		if (this.txZero) {
			if (novaData.after(this.bpContratoCobrancaDetalhes.getDataVencimentoAtual())) {
				ContratoCobrancaUtilsMB contratoCobrancaUtilsMB;

				BigDecimal juros = BigDecimal.ZERO;
				BigDecimal multa = BigDecimal.ZERO;

				if (this.bpContratoCobrancaDetalhes.getVlrJuros().compareTo(BigDecimal.ZERO) == 0) {
					juros = this.objetoContratoCobranca.getTxJuros();
				} else {
					juros = this.bpContratoCobrancaDetalhes.getVlrJuros();
				}

				if (this.bpContratoCobrancaDetalhes.getTxMulta().compareTo(BigDecimal.ZERO) == 0) {
					multa = this.objetoContratoCobranca.getTxMulta();
				} else {
					multa = this.bpContratoCobrancaDetalhes.getTxMulta();
				}

				contratoCobrancaUtilsMB = new ContratoCobrancaUtilsMB(
						this.bpContratoCobrancaDetalhes.getDataVencimentoAtual(), novaData,
						this.bpContratoCobrancaDetalhes.getVlrParcela(), juros, multa);

				if (this.bpContratoCobrancaDetalhes.getListContratoCobrancaDetalhesParcial().size() > 0) {
					contratoCobrancaUtilsMB.recalculaValorSemMulta();
				} else {
					contratoCobrancaUtilsMB.recalculaValor();
				}

				// se recalculo retornou vazio
				if (contratoCobrancaUtilsMB.getValorAtualizado() != null) {
					this.vlrParcelaAtualizadaNew = contratoCobrancaUtilsMB.getValorAtualizado();
				} else {
					this.vlrParcelaAtualizadaNew = this.bpContratoCobrancaDetalhes.getVlrParcela();
				}
			} else {
				this.vlrParcelaAtualizadaNew = this.bpContratoCobrancaDetalhes.getVlrParcela();
			}
		} else {
			this.vlrParcelaAtualizadaNew = this.bpContratoCobrancaDetalhes.getVlrParcela();
		}

		this.vlrRecebido = this.vlrParcelaAtualizadaNew;
	}

	/* GERAÇÃO DE BOLETO */
	/* refaz o valor atualizado de acordo com a data selecionada na baixa parcial */
	public void atualizaValorBaixaParceladaBoleto(SelectEvent event) {
		Date dateSelected = (Date) event.getObject();

		if (dateSelected.after(this.bpContratoCobrancaDetalhes.getDataVencimentoAtual())) {
			ContratoCobrancaUtilsMB contratoCobrancaUtilsMB;

			BigDecimal juros = BigDecimal.ZERO;
			BigDecimal multa = BigDecimal.ZERO;

			if (this.bpContratoCobrancaDetalhes.getVlrJuros().compareTo(BigDecimal.ZERO) == 0) { 
				juros = this.objetoContratoCobranca.getTxJuros();
			} else {
				juros = this.bpContratoCobrancaDetalhes.getVlrJuros();
			}

			if (this.bpContratoCobrancaDetalhes.getTxMulta().compareTo(BigDecimal.ZERO) == 0) {
				multa = this.objetoContratoCobranca.getTxMulta();
			} else {
				multa = this.bpContratoCobrancaDetalhes.getTxMulta();
			}

			contratoCobrancaUtilsMB = new ContratoCobrancaUtilsMB(
					this.bpContratoCobrancaDetalhes.getDataVencimentoAtual(), dateSelected,
					this.bpContratoCobrancaDetalhes.getVlrParcela(), juros, multa);

			if (this.bpContratoCobrancaDetalhes.getListContratoCobrancaDetalhesParcial().size() > 0) {
				contratoCobrancaUtilsMB.recalculaValorSemMulta();
			} else {
				contratoCobrancaUtilsMB.recalculaValor();
			}

			// se recalculo retornou vazio
			if (contratoCobrancaUtilsMB.getValorAtualizado() != null) {
				this.valorBoleto = contratoCobrancaUtilsMB.getValorAtualizado();
			} else {
				this.valorBoleto = this.bpContratoCobrancaDetalhes.getVlrParcela();
			}
		} else {
			this.valorBoleto = this.bpContratoCobrancaDetalhes.getVlrParcela();
		}
	}

	/* ESTORNO BAIXA PARCIAL */
	public void estornoBaixaParcial(ContratoCobrancaDetalhesParcial contratoCobrancaDetalhesParcial) {

		// seta a parcela para paga = FALSE
		this.bpContratoCobrancaDetalhes.setParcelaPaga(false);

		// se só possui uma baixa parcial:
		// remove da lista de pagamentos
		// e volta a data e valor originais
		if (this.bpContratoCobrancaDetalhes.getListContratoCobrancaDetalhesParcial().size() == 1) {
			this.bpContratoCobrancaDetalhes.setDataVencimentoAtual(this.bpContratoCobrancaDetalhes.getDataVencimento());
			this.bpContratoCobrancaDetalhes.setVlrParcela(this.bpContratoCobrancaDetalhes.getVlrParcela());
			this.bpContratoCobrancaDetalhes.setDataUltimoPagamento(null);
			this.bpContratoCobrancaDetalhes.setValorTotalPagamento(null);
			
			
			// remove baixa da lista de parcelas
			this.bpContratoCobrancaDetalhes.getListContratoCobrancaDetalhesParcial()
					.remove(contratoCobrancaDetalhesParcial);
		} else {
			// Se tem mais de uma baixa parcial
			if (this.bpContratoCobrancaDetalhes.getListContratoCobrancaDetalhesParcial().size() > 1) {
				// verificase baixa em questão é a última feita
				// se sim, deverá atualizar, além do valor, a data de vencimento
				if (this.bpContratoCobrancaDetalhes.getListContratoCobrancaDetalhesParcial()
						.get(this.bpContratoCobrancaDetalhes.getListContratoCobrancaDetalhesParcial().size() - 1)
						.getId() == contratoCobrancaDetalhesParcial.getId()) {
					// atualiza data de vencimento
					this.bpContratoCobrancaDetalhes.setDataVencimentoAtual(this.bpContratoCobrancaDetalhes
							.getListContratoCobrancaDetalhesParcial()
							.get(this.bpContratoCobrancaDetalhes.getListContratoCobrancaDetalhesParcial().size() - 2)
							.getDataVencimentoAtual());
					// atualiza valor
					this.bpContratoCobrancaDetalhes.setVlrParcela(this.bpContratoCobrancaDetalhes.getVlrParcela()
							.add(contratoCobrancaDetalhesParcial.getVlrRecebido()));
					// remove baixa da lista de parcelas
					this.bpContratoCobrancaDetalhes.getListContratoCobrancaDetalhesParcial()
							.remove(contratoCobrancaDetalhesParcial);
				} else {
					// se a baixa em questão não for a única e nem a última da lista
					// atualiza apenas o valor
					this.bpContratoCobrancaDetalhes.setVlrParcela(this.bpContratoCobrancaDetalhes.getVlrParcela()
							.add(contratoCobrancaDetalhesParcial.getVlrRecebido()));
					// remove baixa da lista de parcelas
					this.bpContratoCobrancaDetalhes.getListContratoCobrancaDetalhesParcial()
							.remove(contratoCobrancaDetalhesParcial);
				}
			}
		}
		ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();
		contratoCobrancaDetalhesDao.merge(this.bpContratoCobrancaDetalhes);

		realcularValorAtualizadoEstornoBxparcial(this.bpContratoCobrancaDetalhes);
		// clearFieldsBaixar();
	}

	/* BAIXA PARCELA PARA O PORTAL INVESTIDOR */
	public void baixarParcelaInvestidor() {
		ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();

		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		dataHoje.set(Calendar.HOUR_OF_DAY, 0);
		dataHoje.set(Calendar.MINUTE, 0);
		dataHoje.set(Calendar.SECOND, 0);
		dataHoje.set(Calendar.MILLISECOND, 0);

		this.bpContratoCobrancaDetalhes.setBaixadoParaInvestidor(true);

		contratoCobrancaDetalhesDao.merge(bpContratoCobrancaDetalhes);
	}

	/* CANCELAR BAIXA PARCELA PARA O PORTAL INVESTIDOR */
	public void cancelarBaixaParcelaDoInvestidor() {
		ContratoCobrancaParcelasInvestidorDao contratoCobrancaParcelasInvestidorDao = new ContratoCobrancaParcelasInvestidorDao();

		this.baixaContratoCobrancaParcelasInvestidor.setValorBaixado(null);

		this.baixaContratoCobrancaParcelasInvestidor.setDataBaixa(null);

		this.baixaContratoCobrancaParcelasInvestidor.setBaixado(false);

		contratoCobrancaParcelasInvestidorDao.merge(this.baixaContratoCobrancaParcelasInvestidor);
	}

	/* BAIXA PARCELA PARA O PORTAL INVESTIDOR */
	public void baixarParcelaDoInvestidor() {
		ContratoCobrancaParcelasInvestidorDao contratoCobrancaParcelasInvestidorDao = new ContratoCobrancaParcelasInvestidorDao();

		this.baixaContratoCobrancaParcelasInvestidor.setDataBaixa(getDataComHorasZeradas(this.dataPagamentoInvestidor));

		this.baixaContratoCobrancaParcelasInvestidor.setBaixado(true);

		contratoCobrancaParcelasInvestidorDao.merge(this.baixaContratoCobrancaParcelasInvestidor);
	}

	/* BAIXA PARCELA PARA O PORTAL INVESTIDOR EM LOTE */
	public void baixarParcelaDoInvestidorLote() {
		ContratoCobrancaParcelasInvestidorDao contratoCobrancaParcelasInvestidorDao = new ContratoCobrancaParcelasInvestidorDao();

		for (ContratoCobrancaParcelasInvestidor parcelaCorrespondente : this.selectedParcelasInvestidorCorrespondente) {
			if (!parcelaCorrespondente.isBaixado()) {
				parcelaCorrespondente.setDataBaixa(getDataComHorasZeradas(this.dataPagamentoInvestidor));

				parcelaCorrespondente.setBaixado(true);

				parcelaCorrespondente.setValorBaixado(parcelaCorrespondente.getValorLiquido());

				contratoCobrancaParcelasInvestidorDao.merge(parcelaCorrespondente);
			}
		}

		for (ContratoCobrancaParcelasInvestidor parcelaSA : this.selectedParcelasInvestidorSA) {
			if (!parcelaSA.isBaixado()) {
				parcelaSA.setDataBaixa(getDataComHorasZeradas(this.dataPagamentoInvestidor));

				parcelaSA.setBaixado(true);

				parcelaSA.setValorBaixado(parcelaSA.getValorLiquido());

				contratoCobrancaParcelasInvestidorDao.merge(parcelaSA);
			}
		}

		for (ContratoCobrancaParcelasInvestidor parcelaEnvelope : this.selectedParcelasInvestidorEnvelope) {
			if (!parcelaEnvelope.isBaixado()) {
				parcelaEnvelope.setDataBaixa(getDataComHorasZeradas(this.dataPagamentoInvestidor));

				parcelaEnvelope.setBaixado(true);

				parcelaEnvelope.setValorBaixado(parcelaEnvelope.getValorLiquido());

				contratoCobrancaParcelasInvestidorDao.merge(parcelaEnvelope);
			}
		}

		// Limpa Seleção
		this.selectedParcelasInvestidorCorrespondente = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		this.selectedParcelasInvestidorSA = new ArrayList<ContratoCobrancaParcelasInvestidor>();
		this.selectedParcelasInvestidorEnvelope = new ArrayList<ContratoCobrancaParcelasInvestidor>();
	}

	public Date getDataComHorasZeradas(Date dataAtual) {
		Calendar c = Calendar.getInstance();
		c.setTime(dataAtual);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		return c.getTime();
	}

	/* BAIXA PARCIAL */
	public void baixarParcelaParcial() {
		ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();

		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);
		dataHoje.set(Calendar.HOUR_OF_DAY, 0);
		dataHoje.set(Calendar.MINUTE, 0);
		dataHoje.set(Calendar.SECOND, 0);
		dataHoje.set(Calendar.MILLISECOND, 0);
		Calendar dataVencimentoParcela = Calendar.getInstance(zone, locale);
		dataVencimentoParcela.set(Calendar.HOUR_OF_DAY, 0);
		dataVencimentoParcela.set(Calendar.MINUTE, 0);
		dataVencimentoParcela.set(Calendar.SECOND, 0);
		dataVencimentoParcela.set(Calendar.MILLISECOND, 0);

		Calendar dataPagamento = Calendar.getInstance(zone, locale);
		dataPagamento.setTime(this.rowEditNewDate);

		Calendar dataPagamentoHoras = Calendar.getInstance(zone, locale);

		this.bpContratoCobrancaDetalhes.setVlrParcelaAtualizada(this.vlrParcelaAtualizadaNew);

		ContratoCobrancaDetalhesParcial contratoCobrancaDetalhesParcial = new ContratoCobrancaDetalhesParcial();

		if (this.selectedRecebedor != null) {
			if (this.selectedRecebedor.getId() > 0) {
				contratoCobrancaDetalhesParcial.setRecebedor(this.selectedRecebedor);
			}
		}

		if (this.observacao != null) {
			contratoCobrancaDetalhesParcial.setObservacaoRecebedor(this.observacao);
		}
		
		//calcula valor presente
		calcularValorPresenteParcelaData(rowEditNewDate, bpContratoCobrancaDetalhes);

		// se valor recebido é igual ao valor atualizado
		if (this.vlrRecebido.intValue() != 0) {
			// se valor recebido é igual ou maior
			if (this.vlrRecebido.compareTo(bpContratoCobrancaDetalhes.getVlrParcelaAtualizada()) == 0
					|| this.vlrRecebido.compareTo(bpContratoCobrancaDetalhes.getVlrParcelaAtualizada()) == 1) {

				// atualiza data de vencimento para a data atual se a data de vencimento for
				// menor que a data de hoje
				// if
				// (this.bpContratoCobrancaDetalhes.getDataVencimentoAtual().before(this.rowEditNewDate))
				// {
				bpContratoCobrancaDetalhes.setDataVencimentoAtual(this.rowEditNewDate);
				// }

				contratoCobrancaDetalhesParcial.setDataVencimento(this.bpContratoCobrancaDetalhes.getDataVencimento());
				contratoCobrancaDetalhesParcial
						.setDataVencimentoAtual(this.bpContratoCobrancaDetalhes.getDataVencimentoAtual());

				contratoCobrancaDetalhesParcial.setNumeroParcela(this.bpContratoCobrancaDetalhes.getNumeroParcela());
				contratoCobrancaDetalhesParcial.setDataPagamento(dataPagamento.getTime());
				contratoCobrancaDetalhesParcial
						.setVlrParcela(this.bpContratoCobrancaDetalhes.getVlrParcelaAtualizada());

				if (this.vlrRecebido.compareTo(bpContratoCobrancaDetalhes.getVlrParcelaAtualizada()) == 0) {
					contratoCobrancaDetalhesParcial
							.setVlrRecebido(this.bpContratoCobrancaDetalhes.getVlrParcelaAtualizada());
				} else {
					contratoCobrancaDetalhesParcial.setVlrRecebido(this.vlrRecebido);
				}
				contratoCobrancaDetalhesParcial
						.setVlrParcelaAtualizado(this.bpContratoCobrancaDetalhes.getVlrParcelaAtualizada());

				contratoCobrancaDetalhesParcial.setSaldoAPagar(BigDecimal.ZERO);

				bpContratoCobrancaDetalhes.getListContratoCobrancaDetalhesParcial()
						.add(contratoCobrancaDetalhesParcial);

				bpContratoCobrancaDetalhes.setParcelaPaga(true);

				// compoem o valor da parcela de acordo com o historico de baixas
				BigDecimal valorParcelaAtual = BigDecimal.ZERO;

				for (ContratoCobrancaDetalhesParcial ccdp : bpContratoCobrancaDetalhes
						.getListContratoCobrancaDetalhesParcial()) {
					valorParcelaAtual = valorParcelaAtual.add(ccdp.getVlrRecebido());
				}

				bpContratoCobrancaDetalhes.setVlrParcela(valorParcelaAtual);
				bpContratoCobrancaDetalhes.setVlrParcelaAtualizada(BigDecimal.ZERO);
				// bpContratoCobrancaDetalhes.setVlrSaldoParcela(BigDecimal.ZERO);
				
				// verifica se pagamento é igual ao valor presente
			} else if(this.vlrRecebido.compareTo(this.valorPresenteParcela) == 0){
				
				// atualiza data de vencimento para a data atual se a data de vencimento for
				// menor que a data de hoje
				// if
				// (this.bpContratoCobrancaDetalhes.getDataVencimentoAtual().before(this.rowEditNewDate))
				// {
				bpContratoCobrancaDetalhes.setDataVencimentoAtual(this.rowEditNewDate);
				// }
				contratoCobrancaDetalhesParcial.setDataVencimento(this.bpContratoCobrancaDetalhes.getDataVencimento());
				contratoCobrancaDetalhesParcial.setDataVencimentoAtual(this.bpContratoCobrancaDetalhes.getDataVencimentoAtual());
				contratoCobrancaDetalhesParcial.setNumeroParcela(this.bpContratoCobrancaDetalhes.getNumeroParcela());
				contratoCobrancaDetalhesParcial.setDataPagamento(dataPagamento.getTime());
				contratoCobrancaDetalhesParcial.setVlrParcela(valorPresenteParcela);
				contratoCobrancaDetalhesParcial.setVlrRecebido(valorPresenteParcela);
				contratoCobrancaDetalhesParcial.setVlrParcelaAtualizado(valorPresenteParcela);
				contratoCobrancaDetalhesParcial.setSaldoAPagar(BigDecimal.ZERO);
				bpContratoCobrancaDetalhes.getListContratoCobrancaDetalhesParcial().add(contratoCobrancaDetalhesParcial);
				bpContratoCobrancaDetalhes.setParcelaPaga(true);
				
				//valor da parcela continua o mesmo
				
				bpContratoCobrancaDetalhes.setVlrParcelaAtualizada(BigDecimal.ZERO);
			} else {
				// atualiza data de vencimento para a data atual se a data de vencimento for
				// menor que a data de hoje
				if (this.bpContratoCobrancaDetalhes.getDataVencimentoAtual().before(this.rowEditNewDate)) {
					bpContratoCobrancaDetalhes.setDataVencimentoAtual(this.rowEditNewDate);
				}

				contratoCobrancaDetalhesParcial.setDataVencimento(this.bpContratoCobrancaDetalhes.getDataVencimento());
				contratoCobrancaDetalhesParcial
						.setDataVencimentoAtual(this.bpContratoCobrancaDetalhes.getDataVencimentoAtual());

				// historico de baixa parcial
				contratoCobrancaDetalhesParcial.setNumeroParcela(this.bpContratoCobrancaDetalhes.getNumeroParcela());
				contratoCobrancaDetalhesParcial.setDataPagamento(dataPagamento.getTime());
				contratoCobrancaDetalhesParcial
						.setVlrParcela(bpContratoCobrancaDetalhes.getVlrParcelaAtualizada().subtract(this.vlrRecebido));
				contratoCobrancaDetalhesParcial.setVlrRecebido(this.vlrRecebido);

				contratoCobrancaDetalhesParcial
						.setVlrParcelaAtualizado(this.bpContratoCobrancaDetalhes.getVlrParcelaAtualizada());
				contratoCobrancaDetalhesParcial.setSaldoAPagar(
						bpContratoCobrancaDetalhes.getVlrParcelaAtualizada().subtract(this.vlrRecebido));

				bpContratoCobrancaDetalhes.getListContratoCobrancaDetalhesParcial()
						.add(contratoCobrancaDetalhesParcial);

				// se o valor recebido for menor que o da parcela
				bpContratoCobrancaDetalhes
						.setVlrParcela(bpContratoCobrancaDetalhes.getVlrParcelaAtualizada().subtract(this.vlrRecebido));
				bpContratoCobrancaDetalhes.setVlrParcelaAtualizada(null);
			}
			this.vlrRecebido = BigDecimal.ZERO;
		} else {
			// se o valor recebido é zero estorna todas as baixas parciais
			// atualiza data de vencimento para a data atual se a data de vencimento for
			// menor que a data de hoje
			/*
			 * if (this.bpContratoCobrancaDetalhes.getDataVencimentoAtual().before(this.
			 * rowEditNewDate)) {
			 * bpContratoCobrancaDetalhes.setDataVencimentoAtual(this.rowEditNewDate); }
			 * 
			 * contratoCobrancaDetalhesParcial.setDataVencimento(this.
			 * bpContratoCobrancaDetalhes.getDataVencimento());
			 * contratoCobrancaDetalhesParcial.setDataVencimentoAtual(this.
			 * bpContratoCobrancaDetalhes.getDataVencimentoAtual());
			 * 
			 * // historico de baixa parcial
			 * contratoCobrancaDetalhesParcial.setNumeroParcela(this.
			 * bpContratoCobrancaDetalhes.getNumeroParcela());
			 * contratoCobrancaDetalhesParcial.setDataPagamento(dataPagamentoHoras.getTime()
			 * ); contratoCobrancaDetalhesParcial.setVlrParcela(this.vlrParcelaNew);
			 * contratoCobrancaDetalhesParcial.setVlrRecebido(this.vlrRecebido);
			 * 
			 * bpContratoCobrancaDetalhes.getListContratoCobrancaDetalhesParcial().add(
			 * contratoCobrancaDetalhesParcial);
			 */
			bpContratoCobrancaDetalhes.setDataVencimentoAtual(bpContratoCobrancaDetalhes.getDataVencimento());
			bpContratoCobrancaDetalhes.setVlrParcelaAtualizada(null);
			bpContratoCobrancaDetalhes.setVlrParcela(this.vlrParcelaNew);
		}

		contratoCobrancaDetalhesDao.merge(bpContratoCobrancaDetalhes);
		

		
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

		this.objetoContratoCobranca = contratoCobrancaDao.findById(this.objetoContratoCobranca.getId());
		
		// ATUALIZA STATUS PARCELAS
		for (ContratoCobrancaDetalhes ccd : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			
			if (ccd.isAmortizacao()) {
				ccd.setValorTotalPagamento(ccd.getVlrParcela());
				continue;
			}
			
			dataVencimentoParcela.setTime(ccd.getDataVencimentoAtual());

			if (dataVencimentoParcela.getTime().before(dataHoje.getTime()) && !ccd.isParcelaPaga()) {
				ccd.setParcelaVencida(true);

				// calcula coluna valor atualizado
				ContratoCobrancaUtilsMB contratoCobrancaUtilsMB;
				if (ccd.getVlrParcelaAtualizada() != null) {
					if (ccd.getVlrParcelaAtualizada().compareTo(BigDecimal.ZERO) == 1) {
						contratoCobrancaUtilsMB = new ContratoCobrancaUtilsMB(dataVencimentoParcela.getTime(),
								dataHoje.getTime(), ccd.getVlrParcela(), ccd.getVlrParcelaAtualizada(),
								ccd.getVlrJuros(), ccd.getTxMulta());
					} else {
						contratoCobrancaUtilsMB = new ContratoCobrancaUtilsMB(dataVencimentoParcela.getTime(),
								dataHoje.getTime(), ccd.getVlrParcela(), ccd.getVlrJuros(), ccd.getTxMulta());
					}
				} else {
					contratoCobrancaUtilsMB = new ContratoCobrancaUtilsMB(dataVencimentoParcela.getTime(),
							dataHoje.getTime(), ccd.getVlrParcela(), ccd.getVlrJuros(), ccd.getTxMulta());
				}

				if (!ccd.isParcelaPaga()) {
					if (ccd.getListContratoCobrancaDetalhesParcial().size() > 0) {
						contratoCobrancaUtilsMB.recalculaValorSemMulta();
					} else {
						contratoCobrancaUtilsMB.recalculaValor();
					}

					ccd.setVlrParcelaAtualizada(contratoCobrancaUtilsMB.getValorAtualizado());
				} else {
					ccd.setVlrParcelaAtualizada(null);
				}
			}

			if (dataVencimentoParcela.getTime().equals(dataHoje.getTime()) && !ccd.isParcelaPaga()) {
				ccd.setParcelaVencendo(true);
			}

			BigDecimal somaBaixas = BigDecimal.ZERO;

			for (ContratoCobrancaDetalhesParcial cBaixas : ccd.getListContratoCobrancaDetalhesParcial()) {
				ccd.setDataUltimoPagamento(cBaixas.getDataPagamento());
				somaBaixas = somaBaixas.add(cBaixas.getVlrRecebido());
			}

			ccd.setValorTotalPagamento(somaBaixas);
		}

		/*
		 * //se valor atualizado == valor parcela //valor atualizado recebe null
		 * 
		 * // verifica se a parcela ja teve uma baixa if (((ContratoCobrancaDetalhes)
		 * event.getObject()).isParcelaPaga()) { ((ContratoCobrancaDetalhes)
		 * event.getObject()).setVlrParcelaAtualizada(((ContratoCobrancaDetalhes)
		 * event.getObject()).getVlrParcela());
		 * 
		 * if (((ContratoCobrancaDetalhes)
		 * event.getObject()).getVlrRecebido().compareTo(((ContratoCobrancaDetalhes)
		 * event.getObject()).getVlrParcelaAtualizada()) == 1 ) {
		 * context.addMessage(null, new FacesMessage( FacesMessage.SEVERITY_WARN,
		 * "Contrato Cobrança: O valor recebido é maior do que o valor atualizado!",
		 * "")); ((ContratoCobrancaDetalhes) event.getObject()).setVlrRecebido(null); }
		 * else { if (((ContratoCobrancaDetalhes)
		 * event.getObject()).getVlrRecebido().compareTo(((ContratoCobrancaDetalhes)
		 * event.getObject()).getVlrParcelaAtualizada()) == 0 ) { // se sim, seta a
		 * parcela como baixada e zera os saldos ((ContratoCobrancaDetalhes)
		 * event.getObject()).setParcelaPaga(true); ((ContratoCobrancaDetalhes)
		 * event.getObject()).setVlrSaldoParcela(BigDecimal.ZERO);
		 * ((ContratoCobrancaDetalhes)
		 * event.getObject()).setVlrRecebido(BigDecimal.ZERO);
		 * 
		 * contratoCobrancaDetalhesDao.merge(((ContratoCobrancaDetalhes)
		 * event.getObject()));
		 * 
		 * context.addMessage(null, new FacesMessage( FacesMessage.SEVERITY_INFO,
		 * "Contrato Cobrança: Parcela(s) atualizada(s) com sucesso!", "")); } else { //
		 * se não, calcula o saldo que ficará na parcela e deixa ela em aberto //
		 * recebido maior que valor atualizado if (((ContratoCobrancaDetalhes)
		 * event.getObject()).getVlrRecebido().compareTo(((ContratoCobrancaDetalhes)
		 * event.getObject()).getVlrParcelaAtualizada()) == 1 ) {
		 * context.addMessage(null, new FacesMessage( FacesMessage.SEVERITY_WARN,
		 * "Contrato Cobrança: O valor recebido é maior do que o valor atualizado!",
		 * "")); ((ContratoCobrancaDetalhes) event.getObject()).setVlrRecebido(null); }
		 * else { ((ContratoCobrancaDetalhes)
		 * event.getObject()).setVlrParcelaAtualizada(((ContratoCobrancaDetalhes)
		 * event.getObject()).getVlrParcelaAtualizada().subtract(((
		 * ContratoCobrancaDetalhes) event.getObject()).getVlrRecebido()));
		 * ((ContratoCobrancaDetalhes)
		 * event.getObject()).setDataVencimento(rowEditNewDate);
		 * 
		 * ((ContratoCobrancaDetalhes) event.getObject()).setParcelaPaga(false);
		 * 
		 * contratoCobrancaDetalhesDao.merge(((ContratoCobrancaDetalhes)
		 * event.getObject()));
		 * 
		 * context.addMessage(null, new FacesMessage( FacesMessage.SEVERITY_INFO,
		 * "Contrato Cobrança: Parcela(s) atualizada(s) com sucesso!", "")); } } } }
		 * else { // se parcela não baixada // verifica se o valor a ser baixado é o
		 * Total da parcela ou o total do saldo da parcela //TODO baixa parcial //if
		 * (((ContratoCobrancaDetalhes)
		 * event.getObject()).getVlrParcelaAtualizada().equals(((
		 * ContratoCobrancaDetalhes) event.getObject()).getVlrParcela()) //||
		 * ((ContratoCobrancaDetalhes)
		 * event.getObject()).getVlrParcelaAtualizada().equals(((
		 * ContratoCobrancaDetalhes) event.getObject()).getVlrSaldoParcela())) { if
		 * (((ContratoCobrancaDetalhes) event.getObject()).getVlrRecebido().intValue()
		 * == 0) { ((ContratoCobrancaDetalhes)
		 * event.getObject()).setVlrParcelaAtualizada(((ContratoCobrancaDetalhes)
		 * event.getObject()).getVlrParcela()); ((ContratoCobrancaDetalhes)
		 * event.getObject()).setDataVencimento(rowEditNewDate);
		 * ((ContratoCobrancaDetalhes) event.getObject()).setVlrRecebido(null);
		 * 
		 * contratoCobrancaDetalhesDao.merge(((ContratoCobrancaDetalhes)
		 * event.getObject()));
		 * 
		 * context.addMessage(null, new FacesMessage( FacesMessage.SEVERITY_INFO,
		 * "Contrato Cobrança: Parcela(s) atualizada(s) com sucesso!", "")); } else { if
		 * (((ContratoCobrancaDetalhes) event.getObject()).getVlrParcelaAtualizada() ==
		 * null) { ((ContratoCobrancaDetalhes)
		 * event.getObject()).setVlrParcelaAtualizada(((ContratoCobrancaDetalhes)
		 * event.getObject()).getVlrParcela()); }
		 * 
		 * if (((ContratoCobrancaDetalhes)
		 * event.getObject()).getVlrRecebido().compareTo(((ContratoCobrancaDetalhes)
		 * event.getObject()).getVlrParcelaAtualizada()) == 0 ) { // se sim, seta a
		 * parcela como baixada e zera os saldos ((ContratoCobrancaDetalhes)
		 * event.getObject()).setParcelaPaga(true); ((ContratoCobrancaDetalhes)
		 * event.getObject()).setVlrSaldoParcela(BigDecimal.ZERO);
		 * ((ContratoCobrancaDetalhes)
		 * event.getObject()).setVlrRecebido(BigDecimal.ZERO);
		 * 
		 * contratoCobrancaDetalhesDao.merge(((ContratoCobrancaDetalhes)
		 * event.getObject()));
		 * 
		 * context.addMessage(null, new FacesMessage( FacesMessage.SEVERITY_INFO,
		 * "Contrato Cobrança: Parcela(s) atualizada(s) com sucesso!", "")); } else { //
		 * se não, calcula o saldo que ficará na parcela e deixa ela em aberto //
		 * recebido maior que valor atualizado if (((ContratoCobrancaDetalhes)
		 * event.getObject()).getVlrRecebido().compareTo(((ContratoCobrancaDetalhes)
		 * event.getObject()).getVlrParcelaAtualizada()) == 1 ) {
		 * context.addMessage(null, new FacesMessage( FacesMessage.SEVERITY_WARN,
		 * "Contrato Cobrança: O valor recebido é maior do que o valor atualizado!",
		 * "")); ((ContratoCobrancaDetalhes) event.getObject()).setVlrRecebido(null); }
		 * else { ((ContratoCobrancaDetalhes)
		 * event.getObject()).setVlrParcelaAtualizada(((ContratoCobrancaDetalhes)
		 * event.getObject()).getVlrParcelaAtualizada().subtract(((
		 * ContratoCobrancaDetalhes) event.getObject()).getVlrRecebido()));
		 * ((ContratoCobrancaDetalhes)
		 * event.getObject()).setDataVencimento(rowEditNewDate);
		 * //((ContratoCobrancaDetalhes)
		 * event.getObject()).setDataVencimento(data.getTime());
		 * 
		 * contratoCobrancaDetalhesDao.merge(((ContratoCobrancaDetalhes)
		 * event.getObject()));
		 * 
		 * context.addMessage(null, new FacesMessage( FacesMessage.SEVERITY_INFO,
		 * "Contrato Cobrança: Parcela(s) atualizada(s) com sucesso!", "")); } } } }
		 */
	}

	// baixar parcela geral
	public String baixarParcela() {
		FacesContext context = FacesContext.getCurrentInstance();

		ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();

		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar data = Calendar.getInstance(zone, locale);
		/*
		 * // verifica se a parcela ja teve uma baixa if
		 * (this.selectedContratoCobrancaDetalhes.isParcelaPaga()) { // se sim, verifica
		 * se o valor digitado é diferente do valor da parcela (isso significa correção
		 * de operação) if
		 * (this.selectedContratoCobrancaDetalhes.getVlrParcelaAtualizada() !=
		 * this.selectedContratoCobrancaDetalhes.getVlrRepasse()) { // se sim, corrigi o
		 * valor pago e seta a parcela como não baixada
		 * this.selectedContratoCobrancaDetalhes.setVlrSaldoParcela(this.
		 * selectedContratoCobrancaDetalhes.getVlrRepasse().subtract(this.
		 * selectedContratoCobrancaDetalhes.getListContratoCobrancaFavorecidos().get(0).
		 * getVlrRecebido()));
		 * this.selectedContratoCobrancaDetalhes.setParcelaPaga(false); } } else { // se
		 * parcela não baixada // verifica se o valor a ser baixado é o Total da parcela
		 * ou o total do saldo da parcela //TODO baixa parcial //if
		 * (this.selectedContratoCobrancaDetalhes.getVlrParcelaAtualizada().equals(this.
		 * selectedContratoCobrancaDetalhes.getVlrParcela()) //||
		 * this.selectedContratoCobrancaDetalhes.getVlrParcelaAtualizada().equals(this.
		 * selectedContratoCobrancaDetalhes.getVlrSaldoParcela())) { if
		 * (this.selectedContratoCobrancaDetalhes.getListContratoCobrancaFavorecidos().
		 * get(0).getVlrRecebido().equals(this.selectedContratoCobrancaDetalhes.
		 * getVlrParcela())) { // se sim, seta a parcela como baixada e zera os saldos
		 * this.selectedContratoCobrancaDetalhes.setParcelaPaga(true);
		 * this.selectedContratoCobrancaDetalhes.setVlrSaldoParcela(BigDecimal.ZERO);
		 * this.selectedContratoCobrancaDetalhes.setVlrParcelaAtualizada(this.
		 * selectedContratoCobrancaDetalhes.getVlrParcela()); } else { // se não,
		 * calcula o saldo que ficará na parcela e deixa ela em aberto
		 * this.selectedContratoCobrancaDetalhes.setVlrSaldoParcela(this.
		 * selectedContratoCobrancaDetalhes.getVlrParcela().subtract(this.
		 * selectedContratoCobrancaDetalhes.getListContratoCobrancaFavorecidos().get(0).
		 * getVlrRecebido())); } }
		 * 
		 * //this.selectedContratoCobrancaDetalhes.setListContratoCobrancaFavorecidos(
		 * this.listContratoCobrancaFavorecidos);
		 */

		if (this.selectedListContratoCobrancaDetalhes != null) {
			if (this.selectedListContratoCobrancaDetalhes.size() > 0) {
				for (ContratoCobrancaDetalhes c : this.selectedListContratoCobrancaDetalhes) {
					c.setParcelaPaga(true);
					c.setVlrSaldoParcela(BigDecimal.ZERO);
					c.setVlrParcelaAtualizada(c.getVlrParcelaAtualizada());
					c.setDataPagamento(data.getTime());

					contratoCobrancaDetalhesDao.merge(c);
				}

				this.selectedListContratoCobrancaDetalhes = new ArrayList<ContratoCobrancaDetalhes>();

				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Contrato Cobrança: Parcela(s) atualizada(s) com sucesso!", ""));

				// limpa campos
				/*
				 * this.relIsRelAtraso = false; this.relIsCompleto = true; Calendar dataInicio =
				 * Calendar.getInstance(zone, locale); this.relDataContratoInicio =
				 * dataInicio.getTime(); this.relDataContratoFim = dataInicio.getTime();
				 * this.relObjetoContratoCobranca = new
				 * ArrayList<RelatorioFinanceiroCobranca>();
				 * this.selectedContratoCobrancaDetalhes = new ContratoCobrancaDetalhes();
				 * 
				 * clearPagador(); clearRecebedor(); clearRecebedor2(); clearResponsavel();
				 * 
				 * this.contratoGerado = false;
				 */

				return "/Atendimento/Cobranca/ContratoCobrancaBaixar.xhtml";
			} else {
				return "";
			}
		} else {
			return "";
		}
	}
	/*
	 * public void onRowCancel(RowEditEvent event) {
	 * 
	 * }
	 */

	public String excluir() {
		FacesContext context = FacesContext.getCurrentInstance();
		ContratoCobrancaDao contratoCobrancaDao = new ContratoCobrancaDao();

		try {
			contratoCobrancaDao.delete(objetoContratoCobranca);

			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Contrato Cobrança: Registro excluído com sucesso!", ""));

		} catch (DBConnectionException e) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contrato Cobrança: " + e, ""));

			return "";
		}

		return clearFieldsContratosPerformance();
	}

	public void anteciparCreditoInvestidor(int iInvestidor) {
		this.idAntecipacaoInvestidor = iInvestidor;
		this.antecipacao = new ContratoCobrancaParcelasInvestidor();
	}
	
	public void incluirAmortizacaoSaldo() {
		ContratoCobrancaDetalhes parcelaAnterior = null;

		for (ContratoCobrancaDetalhes ContratoCobrancaDetalhes : objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
			if (parcelaAnterior == null || ContratoCobrancaDetalhes.getDataVencimento()
					.compareTo(amortizacao.getDataVencimento()) < 1) {
				parcelaAnterior = ContratoCobrancaDetalhes;

			} else {

				break;
			}
		}
		
		amortizacao.setVlrSaldoParcela(parcelaAnterior.getVlrSaldoParcela().subtract(amortizacao.getVlrAmortizacaoParcela()));
		amortizacao.setVlrJurosParcela(BigDecimal.ZERO);
		amortizacao.setSeguroDFI(BigDecimal.ZERO);
		amortizacao.setSeguroMIP(BigDecimal.ZERO);
		amortizacao.setVlrParcela(amortizacao.getVlrAmortizacaoParcela());
		amortizacao.setNumeroParcela("Amortização");
		amortizacao.setParcelaPaga(true);		
		amortizacao.setDataPagamento(amortizacao.getDataVencimento());
		//amortizacao.setVlrRecebido(amortizacao.getVlrParcela());
		amortizacao.setValorTotalPagamento(amortizacao.getVlrParcela());
		objetoContratoCobranca.getListContratoCobrancaDetalhes().add(amortizacao);

		
		
	}
	public String incluirAntecipacao() {

		List<ContratoCobrancaParcelasInvestidor> listaCobrancaParcelas = null;
		PagadorRecebedor investidor = null;
		BigDecimal taxaRemuneracao = BigDecimal.ZERO;
		BigDecimal saldo = BigDecimal.ZERO;
		boolean isEnvelope = false;
		BigDecimal saldoAtualizado = BigDecimal.ZERO;
		BigDecimal parcelaMensal = BigDecimal.ZERO;

		if (idAntecipacaoInvestidor == 1) {
			listaCobrancaParcelas = objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor1();
			investidor = objetoContratoCobranca.getRecebedor();
			taxaRemuneracao = objetoContratoCobranca.getTaxaRemuneracaoInvestidor1();
			saldo = objetoContratoCobranca.getVlrRecebedor();
			isEnvelope = objetoContratoCobranca.isRecebedorEnvelope();
			parcelaMensal = this.objetoContratoCobranca.getVlrRecebedor();
		}else if (idAntecipacaoInvestidor == 2) {
			listaCobrancaParcelas = objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor2();
			investidor = objetoContratoCobranca.getRecebedor2();
			taxaRemuneracao = objetoContratoCobranca.getTaxaRemuneracaoInvestidor2();
			saldo = objetoContratoCobranca.getVlrRecebedor2();
			isEnvelope = objetoContratoCobranca.isRecebedorEnvelope2();
			parcelaMensal = this.objetoContratoCobranca.getVlrRecebedor2();
		}else if (idAntecipacaoInvestidor == 3) {
			listaCobrancaParcelas = objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor3();
			investidor = objetoContratoCobranca.getRecebedor3();
			taxaRemuneracao = objetoContratoCobranca.getTaxaRemuneracaoInvestidor3();
			saldo = objetoContratoCobranca.getVlrRecebedor3();
			isEnvelope = objetoContratoCobranca.isRecebedorEnvelope3();
			parcelaMensal = this.objetoContratoCobranca.getVlrRecebedor3();
		}else if (idAntecipacaoInvestidor == 4) {
			listaCobrancaParcelas = objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor4();
			investidor = objetoContratoCobranca.getRecebedor4();
			taxaRemuneracao = objetoContratoCobranca.getTaxaRemuneracaoInvestidor4();
			saldo = objetoContratoCobranca.getVlrRecebedor4();
			isEnvelope = objetoContratoCobranca.isRecebedorEnvelope4();
			parcelaMensal = this.objetoContratoCobranca.getVlrRecebedor4();
		}else if (idAntecipacaoInvestidor == 5) {
			listaCobrancaParcelas = objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor5();
			investidor = objetoContratoCobranca.getRecebedor5();
			taxaRemuneracao = objetoContratoCobranca.getTaxaRemuneracaoInvestidor5();
			saldo = objetoContratoCobranca.getVlrRecebedor5();
			isEnvelope = objetoContratoCobranca.isRecebedorEnvelope5();
			parcelaMensal = this.objetoContratoCobranca.getVlrRecebedor5();
		}else if (idAntecipacaoInvestidor == 6) {
			listaCobrancaParcelas = objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor6();
			investidor = objetoContratoCobranca.getRecebedor6();
			taxaRemuneracao = objetoContratoCobranca.getTaxaRemuneracaoInvestidor6();
			saldo = objetoContratoCobranca.getVlrRecebedor6();
			isEnvelope = objetoContratoCobranca.isRecebedorEnvelope6();
			parcelaMensal = this.objetoContratoCobranca.getVlrRecebedor6();
		}else if (idAntecipacaoInvestidor == 7) {
			listaCobrancaParcelas = objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor7();
			investidor = objetoContratoCobranca.getRecebedor7();
			taxaRemuneracao = objetoContratoCobranca.getTaxaRemuneracaoInvestidor7();
			saldo = objetoContratoCobranca.getVlrRecebedor7();
			isEnvelope = objetoContratoCobranca.isRecebedorEnvelope7();
			parcelaMensal = this.objetoContratoCobranca.getVlrRecebedor7();
		}else if (idAntecipacaoInvestidor == 8) {
			listaCobrancaParcelas = objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor8();
			investidor = objetoContratoCobranca.getRecebedor8();
			taxaRemuneracao = objetoContratoCobranca.getTaxaRemuneracaoInvestidor8();
			saldo = objetoContratoCobranca.getVlrRecebedor8();
			isEnvelope = objetoContratoCobranca.isRecebedorEnvelope8();
			parcelaMensal = this.objetoContratoCobranca.getVlrRecebedor8();
		}else if (idAntecipacaoInvestidor == 9) {
			listaCobrancaParcelas = objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor9();
			investidor = objetoContratoCobranca.getRecebedor9();
			taxaRemuneracao = objetoContratoCobranca.getTaxaRemuneracaoInvestidor9();
			saldo = objetoContratoCobranca.getVlrRecebedor9();
			isEnvelope = objetoContratoCobranca.isRecebedorEnvelope9();
			parcelaMensal = this.objetoContratoCobranca.getVlrRecebedor9();
		}else if (idAntecipacaoInvestidor == 10) {
			listaCobrancaParcelas = objetoContratoCobranca.getListContratoCobrancaParcelasInvestidor10();
			investidor = objetoContratoCobranca.getRecebedor10();
			taxaRemuneracao = objetoContratoCobranca.getTaxaRemuneracaoInvestidor10();
			saldo = objetoContratoCobranca.getVlrRecebedor10();
			isEnvelope = objetoContratoCobranca.isRecebedorEnvelope10();
			parcelaMensal = this.objetoContratoCobranca.getVlrRecebedor10();
		}

		if (listaCobrancaParcelas == null)
			return null;

		ContratoCobrancaParcelasInvestidor parcelaAnterior = null;

		for (ContratoCobrancaParcelasInvestidor contratoCobrancaParcelasInvestidor : listaCobrancaParcelas) {
			if (parcelaAnterior == null || contratoCobrancaParcelasInvestidor.getDataVencimento()
					.compareTo(antecipacao.getDataVencimento()) < 1) {
				parcelaAnterior = contratoCobrancaParcelasInvestidor;

			} else {

				break;
			}
		}

		antecipacao.setAmortizacao(antecipacao.getParcelaMensal());
		antecipacao.setValorLiquido(antecipacao.getParcelaMensal());
		antecipacao.setSaldoCredor(saldo);
		antecipacao.setSaldoCredorAtualizado(
				parcelaAnterior.getSaldoCredorAtualizado().subtract(antecipacao.getParcelaMensal()));

		antecipacao.setJuros(BigDecimal.ZERO);
		antecipacao.setIrRetido(BigDecimal.ZERO);
		antecipacao.setBaixado(true);
		antecipacao.setDataBaixa(new Date());
		antecipacao.setEnvelope(false);
		antecipacao.setInvestidor(investidor);
		antecipacao.setNumeroParcela("Antecipação");

		listaCobrancaParcelas.add(antecipacao);

		saldoAtualizado = antecipacao.getSaldoCredorAtualizado();

		taxaRemuneracao = taxaRemuneracao.divide(BigDecimal.valueOf(100));

		Date dataParcela = this.objetoContratoCobranca.getDataInicio();

		for (int i = 0; i < listaCobrancaParcelas.size(); i++) {
			ContratoCobrancaParcelasInvestidor parcelaInvestidor = listaCobrancaParcelas.get(i);

			if (parcelaInvestidor.isBaixado()
					|| parcelaInvestidor.getDataVencimento().compareTo(antecipacao.getDataVencimento()) < 0) {
				continue;
			}

			if (isEnvelope) {

				parcelaInvestidor = new ContratoCobrancaParcelasInvestidor();

				parcelaInvestidor.setParcelaMensal(parcelaMensal);
				parcelaInvestidor.setValorLiquido(parcelaMensal);

				parcelaInvestidor.setJuros(BigDecimal.ZERO);
				parcelaInvestidor.setAmortizacao(BigDecimal.ZERO);
				parcelaInvestidor.setSaldoCredor(BigDecimal.ZERO);
				parcelaInvestidor.setSaldoCredorAtualizado(BigDecimal.ZERO);
				parcelaInvestidor.setIrRetido(BigDecimal.ZERO);

			} else {
				parcelaInvestidor.setParcelaMensal(parcelaMensal);
				parcelaInvestidor.setSaldoCredor(saldo);

				// se a taxa de remuneração for maior que zero
				if (taxaRemuneracao.compareTo(BigDecimal.ZERO) == 1) {
					parcelaInvestidor.setJuros(saldoAtualizado.multiply(taxaRemuneracao));
					parcelaInvestidor.setAmortizacao(parcelaMensal.subtract(parcelaInvestidor.getJuros()));
					parcelaInvestidor
							.setSaldoCredorAtualizado(saldoAtualizado.subtract(parcelaInvestidor.getAmortizacao()));
				} else {
					parcelaInvestidor.setJuros(BigDecimal.ZERO);
					parcelaInvestidor.setAmortizacao(BigDecimal.ZERO);
					parcelaInvestidor.setSaldoCredorAtualizado(BigDecimal.ZERO);
				}

				saldoAtualizado = parcelaInvestidor.getSaldoCredorAtualizado();
				if (!this.objetoContratoCobranca.getEmpresa().equals("GALLERIA CORRESPONDENTE BANCARIO EIRELI")) {
					BigDecimal txIR = BigDecimal.ZERO;

					if ((i + 1) <= 6) {
						txIR = BigDecimal.valueOf(0.225);
					}

					if ((i + 1) > 6 && (i + 1) <= 12) {
						txIR = BigDecimal.valueOf(0.2);
					}

					if ((i + 1) > 12 && (i + 1) <= 18) {
						txIR = BigDecimal.valueOf(0.175);
					}

					if ((i + 1) > 18) {
						txIR = BigDecimal.valueOf(0.15);
					}

					parcelaInvestidor.setIrRetido(parcelaInvestidor.getJuros().multiply(txIR));

					parcelaInvestidor.setValorLiquido(parcelaMensal.subtract(parcelaInvestidor.getIrRetido()));
				} else {
					parcelaInvestidor.setValorLiquido(parcelaMensal);
				}
			}
		}

		ContratoCobrancaDao contratoDao = new ContratoCobrancaDao();
		contratoDao.merge(this.objetoContratoCobranca);

		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Antecipacao do Investidor: Registro salvo com sucesso!", ""));

		return null;
	}

	public String imprimeContrato() {

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.
		 * VGpT0_nF_h4
		 */

		Document doc = null;
		OutputStream os = null;
		try {
			/*
			 * Fonts Utilizadas no PDF
			 */
			Font titulo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			Font subtitulo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 10, Font.BOLD);

			TimeZone zone = TimeZone.getDefault();
			Locale locale = new Locale("pt", "BR");

			/*
			 * Formatadores de Data/Hora
			 */
			SimpleDateFormat sdfDataContrato = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss", locale);
			SimpleDateFormat sdfDataPagamento = new SimpleDateFormat("dd/MMM/yyyy", locale);
			SimpleDateFormat sdfDataArquivo = new SimpleDateFormat("dd-MMM-yyyy", locale);

			/*
			 * DAOs
			 */
			ParametrosDao pDao = new ParametrosDao();

			/*
			 * Instancia Calendário
			 */
			Calendar date = Calendar.getInstance(zone, locale);

			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */
			doc = new Document(PageSize.A4, 10, 10, 10, 10);
			this.pathContrato = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
			this.nomeContrato = sdfDataArquivo.format(date.getTime()) + " Contrato "
					+ this.objetoContratoCobranca.getNumeroContrato() + " - "
					+ this.objetoContratoCobranca.getPagador().getNome() + ".pdf";
			os = new FileOutputStream(this.pathContrato + this.nomeContrato);

			// Associa a stream de saída ao
			PdfWriter.getInstance(doc, os);

			// Abre o documento
			doc.open();

			Paragraph p1 = new Paragraph("CONTRATO DE COBRANÇA - Nº " + this.objetoContratoCobranca.getNumeroContrato(),
					titulo);
			p1.setAlignment(Element.ALIGN_CENTER);
			p1.setSpacingAfter(10);
			doc.add(p1);

			p1 = new Paragraph(
					"DATA DO CONTRATO - " + sdfDataContrato.format(this.objetoContratoCobranca.getDataContrato()));
			p1.setAlignment(Element.ALIGN_LEFT);
			doc.add(p1);

			p1 = new Paragraph("ENVOLVIDOS", subtituloIdent);
			p1.setAlignment(Element.ALIGN_LEFT);
			p1.setSpacingBefore(10);
			doc.add(p1);

			p1 = new Paragraph("PAGADOR - " + this.objetoContratoCobranca.getPagador().getNome(), subtitulo);
			p1.setAlignment(Element.ALIGN_LEFT);
			doc.add(p1);

			p1 = new Paragraph("RESPONSÁVEL - " + this.objetoContratoCobranca.getResponsavel().getNome(), subtitulo);
			p1.setAlignment(Element.ALIGN_LEFT);
			doc.add(p1);

			/* Dados Recebedor */
			p1 = new Paragraph("FAVORECIDO", subtitulo);
			p1.setAlignment(Element.ALIGN_LEFT);
			doc.add(p1);

			if (this.objetoContratoCobranca.getRecebedor().getConta() != null
					&& this.objetoContratoCobranca.getRecebedor().getAgencia() != null
					&& this.objetoContratoCobranca.getRecebedor().getBanco() != null) {
				if (!this.objetoContratoCobranca.getRecebedor().getConta().equals("")
						&& !this.objetoContratoCobranca.getRecebedor().getBanco().equals("")
						&& !this.objetoContratoCobranca.getRecebedor().getAgencia().equals("")) {

					p1 = new Paragraph("BANCO - " + this.objetoContratoCobranca.getRecebedor().getBanco());
					p1.setAlignment(Element.ALIGN_LEFT);
					doc.add(p1);

					p1 = new Paragraph("AG. " + this.objetoContratoCobranca.getRecebedor().getAgencia() + " C/C "
							+ this.objetoContratoCobranca.getRecebedor().getConta());
					p1.setAlignment(Element.ALIGN_LEFT);
					doc.add(p1);

					if (this.objetoContratoCobranca.getVlrRecebedor() != null) {
						p1 = new Paragraph("VALOR - R$ "
								+ this.objetoContratoCobranca.getVlrRecebedor().toString().replace(".", ","));
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					} else {
						p1 = new Paragraph("VALOR - R$ 0,00");
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}
			}

			if (this.objetoContratoCobranca.getRecebedor().getNomeCC() != null) {
				if (!this.objetoContratoCobranca.getRecebedor().getNomeCC().equals("")) {
					p1 = new Paragraph("FAVORECIDO - " + this.objetoContratoCobranca.getRecebedor().getNomeCC());
					p1.setAlignment(Element.ALIGN_LEFT);
					doc.add(p1);
				}
			}

			if (this.objetoContratoCobranca.getRecebedor().getCpfCC() != null) {
				if (!this.objetoContratoCobranca.getRecebedor().getCpfCC().equals("")) {
					p1 = new Paragraph("CPF - " + this.objetoContratoCobranca.getRecebedor().getCpfCC());
					p1.setAlignment(Element.ALIGN_LEFT);
					doc.add(p1);
				}
			}

			if (this.objetoContratoCobranca.getRecebedor().getCnpjCC() != null) {
				if (!this.objetoContratoCobranca.getRecebedor().getCnpjCC().equals("")) {
					p1 = new Paragraph("CNPJ - " + this.objetoContratoCobranca.getRecebedor().getCnpj());
					p1.setAlignment(Element.ALIGN_LEFT);
					doc.add(p1);
				}
			}

			if (this.objetoContratoCobranca.getRecebedor2() != null) {

				p1 = new Paragraph("FAVORECIDO 2 ", subtitulo);
				p1.setAlignment(Element.ALIGN_LEFT);
				doc.add(p1);

				if (this.objetoContratoCobranca.getRecebedor2().getConta() != null
						&& this.objetoContratoCobranca.getRecebedor2().getAgencia() != null
						&& this.objetoContratoCobranca.getRecebedor2().getBanco() != null) {
					if (!this.objetoContratoCobranca.getRecebedor2().getConta().equals("")
							&& !this.objetoContratoCobranca.getRecebedor2().getBanco().equals("")
							&& !this.objetoContratoCobranca.getRecebedor2().getAgencia().equals("")) {

						p1 = new Paragraph("BANCO - " + this.objetoContratoCobranca.getRecebedor2().getBanco());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);

						p1 = new Paragraph("AG. " + this.objetoContratoCobranca.getRecebedor2().getAgencia() + " C/C "
								+ this.objetoContratoCobranca.getRecebedor2().getConta());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);

						if (this.objetoContratoCobranca.getVlrRecebedor2() != null) {
							p1 = new Paragraph("VALOR - R$ "
									+ this.objetoContratoCobranca.getVlrRecebedor2().toString().replace(".", ","));
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						} else {
							p1 = new Paragraph("VALOR - R$ 0,00");
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}
				}

				if (this.objetoContratoCobranca.getRecebedor2().getNomeCC() != null) {
					if (!this.objetoContratoCobranca.getRecebedor2().getNomeCC().equals("")) {
						p1 = new Paragraph("FAVORECIDO - " + this.objetoContratoCobranca.getRecebedor2().getNomeCC());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}

				if (this.objetoContratoCobranca.getRecebedor2().getCpfCC() != null) {
					if (!this.objetoContratoCobranca.getRecebedor2().getCpfCC().equals("")) {
						p1 = new Paragraph("CPF - " + this.objetoContratoCobranca.getRecebedor2().getCpfCC());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}

				if (this.objetoContratoCobranca.getRecebedor2().getCnpj() != null) {
					if (!this.objetoContratoCobranca.getRecebedor2().getCnpj().equals("")) {
						p1 = new Paragraph("CNPJ - " + this.objetoContratoCobranca.getRecebedor2().getCnpj());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}
			}

			if (this.objetoContratoCobranca.getRecebedor3() != null) {

				p1 = new Paragraph("FAVORECIDO 3 ", subtitulo);
				p1.setAlignment(Element.ALIGN_LEFT);
				doc.add(p1);

				if (this.objetoContratoCobranca.getRecebedor3().getConta() != null
						&& this.objetoContratoCobranca.getRecebedor3().getAgencia() != null
						&& this.objetoContratoCobranca.getRecebedor3().getBanco() != null) {
					if (!this.objetoContratoCobranca.getRecebedor3().getConta().equals("")
							&& !this.objetoContratoCobranca.getRecebedor3().getBanco().equals("")
							&& !this.objetoContratoCobranca.getRecebedor3().getAgencia().equals("")) {

						p1 = new Paragraph("BANCO - " + this.objetoContratoCobranca.getRecebedor3().getBanco());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);

						p1 = new Paragraph("AG. " + this.objetoContratoCobranca.getRecebedor3().getAgencia() + " C/C "
								+ this.objetoContratoCobranca.getRecebedor3().getConta());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);

						if (this.objetoContratoCobranca.getVlrRecebedor3() != null) {
							p1 = new Paragraph("VALOR - R$ "
									+ this.objetoContratoCobranca.getVlrRecebedor3().toString().replace(".", ","));
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						} else {
							p1 = new Paragraph("VALOR - R$ 0,00");
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}
				}

				if (this.objetoContratoCobranca.getRecebedor3().getNomeCC() != null) {
					if (!this.objetoContratoCobranca.getRecebedor3().getNomeCC().equals("")) {
						p1 = new Paragraph("FAVORECIDO - " + this.objetoContratoCobranca.getRecebedor3().getNomeCC());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}

				if (this.objetoContratoCobranca.getRecebedor3().getCpfCC() != null) {
					if (!this.objetoContratoCobranca.getRecebedor3().getCpfCC().equals("")) {
						p1 = new Paragraph("CPF - " + this.objetoContratoCobranca.getRecebedor3().getCpfCC());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}

				if (this.objetoContratoCobranca.getRecebedor3().getCnpj() != null) {
					if (!this.objetoContratoCobranca.getRecebedor3().getCnpj().equals("")) {
						p1 = new Paragraph("CNPJ - " + this.objetoContratoCobranca.getRecebedor3().getCnpj());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}
			}

			if (this.objetoContratoCobranca.getRecebedor4() != null) {

				p1 = new Paragraph("FAVORECIDO 4 ", subtitulo);
				p1.setAlignment(Element.ALIGN_LEFT);
				doc.add(p1);

				if (this.objetoContratoCobranca.getRecebedor4().getConta() != null
						&& this.objetoContratoCobranca.getRecebedor4().getAgencia() != null
						&& this.objetoContratoCobranca.getRecebedor4().getBanco() != null) {
					if (!this.objetoContratoCobranca.getRecebedor4().getConta().equals("")
							&& !this.objetoContratoCobranca.getRecebedor4().getBanco().equals("")
							&& !this.objetoContratoCobranca.getRecebedor4().getAgencia().equals("")) {

						p1 = new Paragraph("BANCO - " + this.objetoContratoCobranca.getRecebedor4().getBanco());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);

						p1 = new Paragraph("AG. " + this.objetoContratoCobranca.getRecebedor4().getAgencia() + " C/C "
								+ this.objetoContratoCobranca.getRecebedor4().getConta());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);

						if (this.objetoContratoCobranca.getVlrRecebedor4() != null) {
							p1 = new Paragraph("VALOR - R$ "
									+ this.objetoContratoCobranca.getVlrRecebedor4().toString().replace(".", ","));
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						} else {
							p1 = new Paragraph("VALOR - R$ 0,00");
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}
				}

				if (this.objetoContratoCobranca.getRecebedor4().getNomeCC() != null) {
					if (!this.objetoContratoCobranca.getRecebedor4().getNomeCC().equals("")) {
						p1 = new Paragraph("FAVORECIDO - " + this.objetoContratoCobranca.getRecebedor4().getNomeCC());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}

				if (this.objetoContratoCobranca.getRecebedor4().getCpfCC() != null) {
					if (!this.objetoContratoCobranca.getRecebedor4().getCpfCC().equals("")) {
						p1 = new Paragraph("CPF - " + this.objetoContratoCobranca.getRecebedor4().getCpfCC());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}

				if (this.objetoContratoCobranca.getRecebedor4().getCnpj() != null) {
					if (!this.objetoContratoCobranca.getRecebedor4().getCnpj().equals("")) {
						p1 = new Paragraph("CNPJ - " + this.objetoContratoCobranca.getRecebedor4().getCnpj());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}
			}

			if (this.objetoContratoCobranca.getRecebedor5() != null) {

				p1 = new Paragraph("FAVORECIDO 5 ", subtitulo);
				p1.setAlignment(Element.ALIGN_LEFT);
				doc.add(p1);

				if (this.objetoContratoCobranca.getRecebedor5().getConta() != null
						&& this.objetoContratoCobranca.getRecebedor5().getAgencia() != null
						&& this.objetoContratoCobranca.getRecebedor5().getBanco() != null) {
					if (!this.objetoContratoCobranca.getRecebedor5().getConta().equals("")
							&& !this.objetoContratoCobranca.getRecebedor5().getBanco().equals("")
							&& !this.objetoContratoCobranca.getRecebedor5().getAgencia().equals("")) {

						p1 = new Paragraph("BANCO - " + this.objetoContratoCobranca.getRecebedor5().getBanco());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);

						p1 = new Paragraph("AG. " + this.objetoContratoCobranca.getRecebedor5().getAgencia() + " C/C "
								+ this.objetoContratoCobranca.getRecebedor5().getConta());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);

						if (this.objetoContratoCobranca.getVlrRecebedor5() != null) {
							p1 = new Paragraph("VALOR - R$ "
									+ this.objetoContratoCobranca.getVlrRecebedor5().toString().replace(".", ","));
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						} else {
							p1 = new Paragraph("VALOR - R$ 0,00");
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}
				}

				if (this.objetoContratoCobranca.getRecebedor5().getNomeCC() != null) {
					if (!this.objetoContratoCobranca.getRecebedor5().getNomeCC().equals("")) {
						p1 = new Paragraph("FAVORECIDO - " + this.objetoContratoCobranca.getRecebedor5().getNomeCC());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}

				if (this.objetoContratoCobranca.getRecebedor5().getCpfCC() != null) {
					if (!this.objetoContratoCobranca.getRecebedor5().getCpfCC().equals("")) {
						p1 = new Paragraph("CPF - " + this.objetoContratoCobranca.getRecebedor5().getCpfCC());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}

				if (this.objetoContratoCobranca.getRecebedor5().getCnpj() != null) {
					if (!this.objetoContratoCobranca.getRecebedor5().getCnpj().equals("")) {
						p1 = new Paragraph("CNPJ - " + this.objetoContratoCobranca.getRecebedor5().getCnpj());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}
			}

			if (this.objetoContratoCobranca.getRecebedor6() != null) {

				p1 = new Paragraph("FAVORECIDO 6 ", subtitulo);
				p1.setAlignment(Element.ALIGN_LEFT);
				doc.add(p1);

				if (this.objetoContratoCobranca.getRecebedor6().getConta() != null
						&& this.objetoContratoCobranca.getRecebedor6().getAgencia() != null
						&& this.objetoContratoCobranca.getRecebedor6().getBanco() != null) {
					if (!this.objetoContratoCobranca.getRecebedor6().getConta().equals("")
							&& !this.objetoContratoCobranca.getRecebedor6().getBanco().equals("")
							&& !this.objetoContratoCobranca.getRecebedor6().getAgencia().equals("")) {

						p1 = new Paragraph("BANCO - " + this.objetoContratoCobranca.getRecebedor6().getBanco());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);

						p1 = new Paragraph("AG. " + this.objetoContratoCobranca.getRecebedor6().getAgencia() + " C/C "
								+ this.objetoContratoCobranca.getRecebedor6().getConta());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);

						if (this.objetoContratoCobranca.getVlrRecebedor6() != null) {
							p1 = new Paragraph("VALOR - R$ "
									+ this.objetoContratoCobranca.getVlrRecebedor6().toString().replace(".", ","));
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						} else {
							p1 = new Paragraph("VALOR - R$ 0,00");
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}
				}

				if (this.objetoContratoCobranca.getRecebedor6().getNomeCC() != null) {
					if (!this.objetoContratoCobranca.getRecebedor6().getNomeCC().equals("")) {
						p1 = new Paragraph("FAVORECIDO - " + this.objetoContratoCobranca.getRecebedor6().getNomeCC());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}

				if (this.objetoContratoCobranca.getRecebedor6().getCpfCC() != null) {
					if (!this.objetoContratoCobranca.getRecebedor6().getCpfCC().equals("")) {
						p1 = new Paragraph("CPF - " + this.objetoContratoCobranca.getRecebedor6().getCpfCC());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}

				if (this.objetoContratoCobranca.getRecebedor6().getCnpj() != null) {
					if (!this.objetoContratoCobranca.getRecebedor6().getCnpj().equals("")) {
						p1 = new Paragraph("CNPJ - " + this.objetoContratoCobranca.getRecebedor6().getCnpj());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}
			}

			if (this.objetoContratoCobranca.getRecebedor7() != null) {

				p1 = new Paragraph("FAVORECIDO 7 ", subtitulo);
				p1.setAlignment(Element.ALIGN_LEFT);
				doc.add(p1);

				if (this.objetoContratoCobranca.getRecebedor7().getConta() != null
						&& this.objetoContratoCobranca.getRecebedor7().getAgencia() != null
						&& this.objetoContratoCobranca.getRecebedor7().getBanco() != null) {
					if (!this.objetoContratoCobranca.getRecebedor7().getConta().equals("")
							&& !this.objetoContratoCobranca.getRecebedor7().getBanco().equals("")
							&& !this.objetoContratoCobranca.getRecebedor7().getAgencia().equals("")) {

						p1 = new Paragraph("BANCO - " + this.objetoContratoCobranca.getRecebedor7().getBanco());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);

						p1 = new Paragraph("AG. " + this.objetoContratoCobranca.getRecebedor7().getAgencia() + " C/C "
								+ this.objetoContratoCobranca.getRecebedor7().getConta());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);

						if (this.objetoContratoCobranca.getVlrRecebedor6() != null) {
							p1 = new Paragraph("VALOR - R$ "
									+ this.objetoContratoCobranca.getVlrRecebedor7().toString().replace(".", ","));
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						} else {
							p1 = new Paragraph("VALOR - R$ 0,00");
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}
				}

				if (this.objetoContratoCobranca.getRecebedor7().getNomeCC() != null) {
					if (!this.objetoContratoCobranca.getRecebedor7().getNomeCC().equals("")) {
						p1 = new Paragraph("FAVORECIDO - " + this.objetoContratoCobranca.getRecebedor7().getNomeCC());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}

				if (this.objetoContratoCobranca.getRecebedor7().getCpfCC() != null) {
					if (!this.objetoContratoCobranca.getRecebedor7().getCpfCC().equals("")) {
						p1 = new Paragraph("CPF - " + this.objetoContratoCobranca.getRecebedor7().getCpfCC());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}

				if (this.objetoContratoCobranca.getRecebedor7().getCnpj() != null) {
					if (!this.objetoContratoCobranca.getRecebedor7().getCnpj().equals("")) {
						p1 = new Paragraph("CNPJ - " + this.objetoContratoCobranca.getRecebedor7().getCnpj());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}
			}

			if (this.objetoContratoCobranca.getRecebedor8() != null) {

				p1 = new Paragraph("FAVORECIDO 8 ", subtitulo);
				p1.setAlignment(Element.ALIGN_LEFT);
				doc.add(p1);

				if (this.objetoContratoCobranca.getRecebedor8().getConta() != null
						&& this.objetoContratoCobranca.getRecebedor8().getAgencia() != null
						&& this.objetoContratoCobranca.getRecebedor8().getBanco() != null) {
					if (!this.objetoContratoCobranca.getRecebedor8().getConta().equals("")
							&& !this.objetoContratoCobranca.getRecebedor8().getBanco().equals("")
							&& !this.objetoContratoCobranca.getRecebedor8().getAgencia().equals("")) {

						p1 = new Paragraph("BANCO - " + this.objetoContratoCobranca.getRecebedor8().getBanco());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);

						p1 = new Paragraph("AG. " + this.objetoContratoCobranca.getRecebedor8().getAgencia() + " C/C "
								+ this.objetoContratoCobranca.getRecebedor8().getConta());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);

						if (this.objetoContratoCobranca.getVlrRecebedor8() != null) {
							p1 = new Paragraph("VALOR - R$ "
									+ this.objetoContratoCobranca.getVlrRecebedor8().toString().replace(".", ","));
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						} else {
							p1 = new Paragraph("VALOR - R$ 0,00");
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}
				}

				if (this.objetoContratoCobranca.getRecebedor8().getNomeCC() != null) {
					if (!this.objetoContratoCobranca.getRecebedor8().getNomeCC().equals("")) {
						p1 = new Paragraph("FAVORECIDO - " + this.objetoContratoCobranca.getRecebedor8().getNomeCC());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}

				if (this.objetoContratoCobranca.getRecebedor8().getCpfCC() != null) {
					if (!this.objetoContratoCobranca.getRecebedor8().getCpfCC().equals("")) {
						p1 = new Paragraph("CPF - " + this.objetoContratoCobranca.getRecebedor8().getCpfCC());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}

				if (this.objetoContratoCobranca.getRecebedor8().getCnpj() != null) {
					if (!this.objetoContratoCobranca.getRecebedor8().getCnpj().equals("")) {
						p1 = new Paragraph("CNPJ - " + this.objetoContratoCobranca.getRecebedor8().getCnpj());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}
			}

			if (this.objetoContratoCobranca.getRecebedor9() != null) {

				p1 = new Paragraph("FAVORECIDO 9 ", subtitulo);
				p1.setAlignment(Element.ALIGN_LEFT);
				doc.add(p1);

				if (this.objetoContratoCobranca.getRecebedor9().getConta() != null
						&& this.objetoContratoCobranca.getRecebedor9().getAgencia() != null
						&& this.objetoContratoCobranca.getRecebedor9().getBanco() != null) {
					if (!this.objetoContratoCobranca.getRecebedor9().getConta().equals("")
							&& !this.objetoContratoCobranca.getRecebedor9().getBanco().equals("")
							&& !this.objetoContratoCobranca.getRecebedor9().getAgencia().equals("")) {

						p1 = new Paragraph("BANCO - " + this.objetoContratoCobranca.getRecebedor9().getBanco());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);

						p1 = new Paragraph("AG. " + this.objetoContratoCobranca.getRecebedor9().getAgencia() + " C/C "
								+ this.objetoContratoCobranca.getRecebedor9().getConta());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);

						if (this.objetoContratoCobranca.getVlrRecebedor9() != null) {
							p1 = new Paragraph("VALOR - R$ "
									+ this.objetoContratoCobranca.getVlrRecebedor9().toString().replace(".", ","));
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						} else {
							p1 = new Paragraph("VALOR - R$ 0,00");
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}
				}

				if (this.objetoContratoCobranca.getRecebedor9().getNomeCC() != null) {
					if (!this.objetoContratoCobranca.getRecebedor9().getNomeCC().equals("")) {
						p1 = new Paragraph("FAVORECIDO - " + this.objetoContratoCobranca.getRecebedor9().getNomeCC());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}

				if (this.objetoContratoCobranca.getRecebedor9().getCpfCC() != null) {
					if (!this.objetoContratoCobranca.getRecebedor9().getCpfCC().equals("")) {
						p1 = new Paragraph("CPF - " + this.objetoContratoCobranca.getRecebedor9().getCpfCC());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}

				if (this.objetoContratoCobranca.getRecebedor9().getCnpj() != null) {
					if (!this.objetoContratoCobranca.getRecebedor9().getCnpj().equals("")) {
						p1 = new Paragraph("CNPJ - " + this.objetoContratoCobranca.getRecebedor9().getCnpj());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}
			}

			if (this.objetoContratoCobranca.getRecebedor10() != null) {

				p1 = new Paragraph("FAVORECIDO 10 ", subtitulo);
				p1.setAlignment(Element.ALIGN_LEFT);
				doc.add(p1);

				if (this.objetoContratoCobranca.getRecebedor10().getConta() != null
						&& this.objetoContratoCobranca.getRecebedor10().getAgencia() != null
						&& this.objetoContratoCobranca.getRecebedor10().getBanco() != null) {
					if (!this.objetoContratoCobranca.getRecebedor10().getConta().equals("")
							&& !this.objetoContratoCobranca.getRecebedor10().getBanco().equals("")
							&& !this.objetoContratoCobranca.getRecebedor10().getAgencia().equals("")) {

						p1 = new Paragraph("BANCO - " + this.objetoContratoCobranca.getRecebedor10().getBanco());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);

						p1 = new Paragraph("AG. " + this.objetoContratoCobranca.getRecebedor10().getAgencia() + " C/C "
								+ this.objetoContratoCobranca.getRecebedor10().getConta());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);

						if (this.objetoContratoCobranca.getVlrRecebedor10() != null) {
							p1 = new Paragraph("VALOR - R$ "
									+ this.objetoContratoCobranca.getVlrRecebedor10().toString().replace(".", ","));
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						} else {
							p1 = new Paragraph("VALOR - R$ 0,00");
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}
				}

				if (this.objetoContratoCobranca.getRecebedor10().getNomeCC() != null) {
					if (!this.objetoContratoCobranca.getRecebedor10().getNomeCC().equals("")) {
						p1 = new Paragraph("FAVORECIDO - " + this.objetoContratoCobranca.getRecebedor10().getNomeCC());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}

				if (this.objetoContratoCobranca.getRecebedor10().getCpfCC() != null) {
					if (!this.objetoContratoCobranca.getRecebedor10().getCpfCC().equals("")) {
						p1 = new Paragraph("CPF - " + this.objetoContratoCobranca.getRecebedor10().getCpfCC());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}

				if (this.objetoContratoCobranca.getRecebedor10().getCnpj() != null) {
					if (!this.objetoContratoCobranca.getRecebedor10().getCnpj().equals("")) {
						p1 = new Paragraph("CNPJ - " + this.objetoContratoCobranca.getRecebedor10().getCnpj());
						p1.setAlignment(Element.ALIGN_LEFT);
						doc.add(p1);
					}
				}
			}

			p1 = new Paragraph("DADOS DO CONTRATO", subtituloIdent);
			p1.setSpacingBefore(10);
			p1.setAlignment(Element.ALIGN_LEFT);
			doc.add(p1);

			p1 = new Paragraph("IMÓVEL - " + this.objetoContratoCobranca.getImovel().getNome());
			p1.setAlignment(Element.ALIGN_LEFT);
			doc.add(p1);

			p1 = new Paragraph("VALOR DA ÚLTIMA NP - R$ "
					+ this.objetoContratoCobranca.getVlrInvestimento().toString().replace(".", ","));
			p1.setAlignment(Element.ALIGN_LEFT);
			doc.add(p1);

			p1 = new Paragraph("TAXA DE ADMINISTRAÇÃO - R$ "
					+ this.objetoContratoCobranca.getTxAdministracao().toString().replace(".", ","));
			p1.setAlignment(Element.ALIGN_LEFT);
			doc.add(p1);

			p1 = new Paragraph(
					"TAXA DE JUROS - " + this.objetoContratoCobranca.getTxJuros().toString().replace(".", ",") + " %");
			p1.setAlignment(Element.ALIGN_LEFT);
			doc.add(p1);

			p1 = new Paragraph("FORMA DE PAGAMENTO", subtituloIdent);
			p1.setSpacingBefore(10);
			p1.setAlignment(Element.ALIGN_LEFT);
			doc.add(p1);

			p1 = new Paragraph("DATA INICIO - " + sdfDataPagamento.format(this.objetoContratoCobranca.getDataInicio()));
			p1.setAlignment(Element.ALIGN_LEFT);
			doc.add(p1);

			p1 = new Paragraph("VALOR DA PARCELA - R$ "
					+ this.objetoContratoCobranca.getVlrParcela().toString().replace(".", ","));
			p1.setAlignment(Element.ALIGN_LEFT);
			doc.add(p1);

			p1 = new Paragraph("VALOR DO INVESTIDOR - R$ " + this.getVlrRepasse().toString().replace(".", ","));
			p1.setAlignment(Element.ALIGN_LEFT);
			doc.add(p1);

			p1 = new Paragraph("VALOR DA ADMINISTRAÇÃO - R$ " + this.getVlrRetencao().toString().replace(".", ","));
			p1.setAlignment(Element.ALIGN_LEFT);
			doc.add(p1);

			if (this.getVlrComissao() != null) {
				p1 = new Paragraph("VALOR DO REPASSE - R$ " + this.getVlrComissao().toString().replace(".", ","));
				p1.setAlignment(Element.ALIGN_LEFT);
				doc.add(p1);
			}

			if (this.objetoContratoCobranca.isGeraParcelaFinal()) {
				p1 = new Paragraph("FORMA DE PAGAMENTO - PARCELA FINAL", subtituloIdent);
				p1.setSpacingBefore(10);
				p1.setAlignment(Element.ALIGN_LEFT);
				doc.add(p1);

				p1 = new Paragraph(
						"VALOR DA PARCELA - R$ " + this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
								.get(this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() - 1)
								.getVlrParcela().toString().replace(".", ","));
				p1.setAlignment(Element.ALIGN_LEFT);
				doc.add(p1);

				p1 = new Paragraph(
						"VALOR DO INVESTIDOR - R$ " + this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
								.get(this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() - 1)
								.getVlrRepasse().toString().replace(".", ","));
				p1.setAlignment(Element.ALIGN_LEFT);
				doc.add(p1);

				p1 = new Paragraph(
						"VALOR DA ADMINISTRAÇÃO - R$ " + this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
								.get(this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() - 1)
								.getVlrRetencao().toString().replace(".", ","));
				p1.setAlignment(Element.ALIGN_LEFT);
				doc.add(p1);

				if (this.getVlrComissao() != null) {
					p1 = new Paragraph(
							"VALOR DO REPASSE - R$ " + this.objetoContratoCobranca.getListContratoCobrancaDetalhes()
									.get(this.objetoContratoCobranca.getListContratoCobrancaDetalhes().size() - 1)
									.getVlrComissao().toString().replace(".", ","));
					p1.setAlignment(Element.ALIGN_LEFT);
					doc.add(p1);
				}

				if (this.objetoContratoCobranca.getRecebedorParcelaFinal1() != null) {

					p1 = new Paragraph("RECEBEDOR PARCELA FINAL ", subtitulo);
					p1.setAlignment(Element.ALIGN_LEFT);
					p1.setSpacingBefore(10);
					doc.add(p1);

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal1().getConta() != null
							&& this.objetoContratoCobranca.getRecebedorParcelaFinal1().getAgencia() != null
							&& this.objetoContratoCobranca.getRecebedorParcelaFinal1().getBanco() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal1().getConta().equals("")
								&& !this.objetoContratoCobranca.getRecebedorParcelaFinal1().getBanco().equals("")
								&& !this.objetoContratoCobranca.getRecebedorParcelaFinal1().getAgencia().equals("")) {

							p1 = new Paragraph(
									"BANCO - " + this.objetoContratoCobranca.getRecebedorParcelaFinal1().getBanco());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);

							p1 = new Paragraph("AG. "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal1().getAgencia() + " C/C "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal1().getConta());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);

							if (this.objetoContratoCobranca.getRecebedorParcelaFinal1() != null) {
								p1 = new Paragraph("VALOR - R$ " + this.objetoContratoCobranca.getVlrFinalRecebedor1()
										.toString().replace(".", ","));
								p1.setAlignment(Element.ALIGN_LEFT);
								doc.add(p1);
							} else {
								p1 = new Paragraph("VALOR - R$ 0,00");
								p1.setAlignment(Element.ALIGN_LEFT);
								doc.add(p1);
							}
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal1().getNomeCC() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal1().getNomeCC().equals("")) {
							p1 = new Paragraph("FAVORECIDO - "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal1().getNomeCC());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal1().getCpfCC() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal1().getCpfCC().equals("")) {
							p1 = new Paragraph(
									"CPF - " + this.objetoContratoCobranca.getRecebedorParcelaFinal1().getCpfCC());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal1().getCnpj() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal1().getCnpj().equals("")) {
							p1 = new Paragraph(
									"CNPJ - " + this.objetoContratoCobranca.getRecebedorParcelaFinal1().getCnpj());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}
				}

				if (this.objetoContratoCobranca.getRecebedorParcelaFinal2() != null) {

					p1 = new Paragraph("RECEBEDOR PARCELA FINAL ", subtitulo);
					p1.setAlignment(Element.ALIGN_LEFT);
					p1.setSpacingBefore(10);
					doc.add(p1);

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal2().getConta() != null
							&& this.objetoContratoCobranca.getRecebedorParcelaFinal2().getAgencia() != null
							&& this.objetoContratoCobranca.getRecebedorParcelaFinal2().getBanco() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal2().getConta().equals("")
								&& !this.objetoContratoCobranca.getRecebedorParcelaFinal2().getBanco().equals("")
								&& !this.objetoContratoCobranca.getRecebedorParcelaFinal2().getAgencia().equals("")) {

							p1 = new Paragraph(
									"BANCO - " + this.objetoContratoCobranca.getRecebedorParcelaFinal2().getBanco());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);

							p1 = new Paragraph("AG. "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal2().getAgencia() + " C/C "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal2().getConta());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);

							if (this.objetoContratoCobranca.getRecebedorParcelaFinal2() != null) {
								p1 = new Paragraph("VALOR - R$ " + this.objetoContratoCobranca.getVlrFinalRecebedor2()
										.toString().replace(".", ","));
								p1.setAlignment(Element.ALIGN_LEFT);
								doc.add(p1);
							} else {
								p1 = new Paragraph("VALOR - R$ 0,00");
								p1.setAlignment(Element.ALIGN_LEFT);
								doc.add(p1);
							}
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal2().getNomeCC() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal2().getNomeCC().equals("")) {
							p1 = new Paragraph("FAVORECIDO - "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal2().getNomeCC());
							p1.setAlignment(Element.ALIGN_LEFT);
							p1.setSpacingBefore(10);
							doc.add(p1);
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal2().getCpfCC() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal2().getCpfCC().equals("")) {
							p1 = new Paragraph(
									"CPF - " + this.objetoContratoCobranca.getRecebedorParcelaFinal2().getCpfCC());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal2().getCnpj() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal2().getCnpj().equals("")) {
							p1 = new Paragraph(
									"CNPJ - " + this.objetoContratoCobranca.getRecebedorParcelaFinal2().getCnpj());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}
				}

				if (this.objetoContratoCobranca.getRecebedorParcelaFinal3() != null) {

					p1 = new Paragraph("RECEBEDOR PARCELA FINAL ", subtitulo);
					p1.setAlignment(Element.ALIGN_LEFT);
					p1.setSpacingBefore(10);
					doc.add(p1);

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal3().getConta() != null
							&& this.objetoContratoCobranca.getRecebedorParcelaFinal3().getAgencia() != null
							&& this.objetoContratoCobranca.getRecebedorParcelaFinal3().getBanco() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal3().getConta().equals("")
								&& !this.objetoContratoCobranca.getRecebedorParcelaFinal3().getBanco().equals("")
								&& !this.objetoContratoCobranca.getRecebedorParcelaFinal3().getAgencia().equals("")) {

							p1 = new Paragraph(
									"BANCO - " + this.objetoContratoCobranca.getRecebedorParcelaFinal3().getBanco());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);

							p1 = new Paragraph("AG. "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal3().getAgencia() + " C/C "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal3().getConta());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);

							if (this.objetoContratoCobranca.getRecebedorParcelaFinal3() != null) {
								p1 = new Paragraph("VALOR - R$ " + this.objetoContratoCobranca.getVlrFinalRecebedor3()
										.toString().replace(".", ","));
								p1.setAlignment(Element.ALIGN_LEFT);
								doc.add(p1);
							} else {
								p1 = new Paragraph("VALOR - R$ 0,00");
								p1.setAlignment(Element.ALIGN_LEFT);
								doc.add(p1);
							}
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal3().getNomeCC() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal3().getNomeCC().equals("")) {
							p1 = new Paragraph("FAVORECIDO - "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal3().getNomeCC());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal3().getCpfCC() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal3().getCpfCC().equals("")) {
							p1 = new Paragraph(
									"CPF - " + this.objetoContratoCobranca.getRecebedorParcelaFinal3().getCpfCC());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal3().getCnpj() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal3().getCnpj().equals("")) {
							p1 = new Paragraph(
									"CNPJ - " + this.objetoContratoCobranca.getRecebedorParcelaFinal3().getCnpj());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}
				}

				if (this.objetoContratoCobranca.getRecebedorParcelaFinal4() != null) {

					p1 = new Paragraph("RECEBEDOR PARCELA FINAL ", subtitulo);
					p1.setAlignment(Element.ALIGN_LEFT);
					p1.setSpacingBefore(10);
					doc.add(p1);

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal4().getConta() != null
							&& this.objetoContratoCobranca.getRecebedorParcelaFinal4().getAgencia() != null
							&& this.objetoContratoCobranca.getRecebedorParcelaFinal4().getBanco() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal4().getConta().equals("")
								&& !this.objetoContratoCobranca.getRecebedorParcelaFinal4().getBanco().equals("")
								&& !this.objetoContratoCobranca.getRecebedorParcelaFinal4().getAgencia().equals("")) {

							p1 = new Paragraph(
									"BANCO - " + this.objetoContratoCobranca.getRecebedorParcelaFinal4().getBanco());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);

							p1 = new Paragraph("AG. "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal4().getAgencia() + " C/C "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal4().getConta());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);

							if (this.objetoContratoCobranca.getRecebedorParcelaFinal4() != null) {
								p1 = new Paragraph("VALOR - R$ " + this.objetoContratoCobranca.getVlrFinalRecebedor4()
										.toString().replace(".", ","));
								p1.setAlignment(Element.ALIGN_LEFT);
								doc.add(p1);
							} else {
								p1 = new Paragraph("VALOR - R$ 0,00");
								p1.setAlignment(Element.ALIGN_LEFT);
								doc.add(p1);
							}
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal4().getNomeCC() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal4().getNomeCC().equals("")) {
							p1 = new Paragraph("FAVORECIDO - "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal4().getNomeCC());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal4().getCpfCC() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal4().getCpfCC().equals("")) {
							p1 = new Paragraph(
									"CPF - " + this.objetoContratoCobranca.getRecebedorParcelaFinal4().getCpfCC());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal4().getCnpj() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal4().getCnpj().equals("")) {
							p1 = new Paragraph(
									"CNPJ - " + this.objetoContratoCobranca.getRecebedorParcelaFinal4().getCnpj());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}
				}

				if (this.objetoContratoCobranca.getRecebedorParcelaFinal5() != null) {

					p1 = new Paragraph("RECEBEDOR PARCELA FINAL ", subtitulo);
					p1.setAlignment(Element.ALIGN_LEFT);
					p1.setSpacingBefore(10);
					doc.add(p1);

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal5().getConta() != null
							&& this.objetoContratoCobranca.getRecebedorParcelaFinal5().getAgencia() != null
							&& this.objetoContratoCobranca.getRecebedorParcelaFinal5().getBanco() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal5().getConta().equals("")
								&& !this.objetoContratoCobranca.getRecebedorParcelaFinal5().getBanco().equals("")
								&& !this.objetoContratoCobranca.getRecebedorParcelaFinal5().getAgencia().equals("")) {

							p1 = new Paragraph(
									"BANCO - " + this.objetoContratoCobranca.getRecebedorParcelaFinal5().getBanco());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);

							p1 = new Paragraph("AG. "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal5().getAgencia() + " C/C "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal5().getConta());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);

							if (this.objetoContratoCobranca.getRecebedorParcelaFinal5() != null) {
								p1 = new Paragraph("VALOR - R$ " + this.objetoContratoCobranca.getVlrFinalRecebedor5()
										.toString().replace(".", ","));
								p1.setAlignment(Element.ALIGN_LEFT);
								doc.add(p1);
							} else {
								p1 = new Paragraph("VALOR - R$ 0,00");
								p1.setAlignment(Element.ALIGN_LEFT);
								doc.add(p1);
							}
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal5().getNomeCC() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal5().getNomeCC().equals("")) {
							p1 = new Paragraph("FAVORECIDO - "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal5().getNomeCC());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal5().getCpfCC() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal5().getCpfCC().equals("")) {
							p1 = new Paragraph(
									"CPF - " + this.objetoContratoCobranca.getRecebedorParcelaFinal5().getCpfCC());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal5().getCnpj() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal5().getCnpj().equals("")) {
							p1 = new Paragraph(
									"CNPJ - " + this.objetoContratoCobranca.getRecebedorParcelaFinal5().getCnpj());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}
				}

				if (this.objetoContratoCobranca.getRecebedorParcelaFinal6() != null) {

					p1 = new Paragraph("RECEBEDOR PARCELA FINAL ", subtitulo);
					p1.setAlignment(Element.ALIGN_LEFT);
					p1.setSpacingBefore(10);
					doc.add(p1);

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal6().getConta() != null
							&& this.objetoContratoCobranca.getRecebedorParcelaFinal6().getAgencia() != null
							&& this.objetoContratoCobranca.getRecebedorParcelaFinal6().getBanco() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal6().getConta().equals("")
								&& !this.objetoContratoCobranca.getRecebedorParcelaFinal6().getBanco().equals("")
								&& !this.objetoContratoCobranca.getRecebedorParcelaFinal6().getAgencia().equals("")) {

							p1 = new Paragraph(
									"BANCO - " + this.objetoContratoCobranca.getRecebedorParcelaFinal6().getBanco());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);

							p1 = new Paragraph("AG. "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal6().getAgencia() + " C/C "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal6().getConta());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);

							if (this.objetoContratoCobranca.getRecebedorParcelaFinal6() != null) {
								p1 = new Paragraph("VALOR - R$ " + this.objetoContratoCobranca.getVlrFinalRecebedor6()
										.toString().replace(".", ","));
								p1.setAlignment(Element.ALIGN_LEFT);
								doc.add(p1);
							} else {
								p1 = new Paragraph("VALOR - R$ 0,00");
								p1.setAlignment(Element.ALIGN_LEFT);
								doc.add(p1);
							}
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal6().getNomeCC() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal6().getNomeCC().equals("")) {
							p1 = new Paragraph("FAVORECIDO - "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal6().getNomeCC());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal6().getCpfCC() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal6().getCpfCC().equals("")) {
							p1 = new Paragraph(
									"CPF - " + this.objetoContratoCobranca.getRecebedorParcelaFinal6().getCpfCC());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal6().getCnpj() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal6().getCnpj().equals("")) {
							p1 = new Paragraph(
									"CNPJ - " + this.objetoContratoCobranca.getRecebedorParcelaFinal6().getCnpj());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}
				}

				if (this.objetoContratoCobranca.getRecebedorParcelaFinal7() != null) {

					p1 = new Paragraph("RECEBEDOR PARCELA FINAL ", subtitulo);
					p1.setAlignment(Element.ALIGN_LEFT);
					p1.setSpacingBefore(10);
					doc.add(p1);

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal7().getConta() != null
							&& this.objetoContratoCobranca.getRecebedorParcelaFinal7().getAgencia() != null
							&& this.objetoContratoCobranca.getRecebedorParcelaFinal7().getBanco() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal7().getConta().equals("")
								&& !this.objetoContratoCobranca.getRecebedorParcelaFinal7().getBanco().equals("")
								&& !this.objetoContratoCobranca.getRecebedorParcelaFinal7().getAgencia().equals("")) {

							p1 = new Paragraph(
									"BANCO - " + this.objetoContratoCobranca.getRecebedorParcelaFinal7().getBanco());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);

							p1 = new Paragraph("AG. "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal7().getAgencia() + " C/C "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal7().getConta());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);

							if (this.objetoContratoCobranca.getRecebedorParcelaFinal7() != null) {
								p1 = new Paragraph("VALOR - R$ " + this.objetoContratoCobranca.getVlrFinalRecebedor7()
										.toString().replace(".", ","));
								p1.setAlignment(Element.ALIGN_LEFT);
								doc.add(p1);
							} else {
								p1 = new Paragraph("VALOR - R$ 0,00");
								p1.setAlignment(Element.ALIGN_LEFT);
								doc.add(p1);
							}
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal7().getNomeCC() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal7().getNomeCC().equals("")) {
							p1 = new Paragraph("FAVORECIDO - "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal7().getNomeCC());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal7().getCpfCC() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal7().getCpfCC().equals("")) {
							p1 = new Paragraph(
									"CPF - " + this.objetoContratoCobranca.getRecebedorParcelaFinal7().getCpfCC());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal7().getCnpj() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal7().getCnpj().equals("")) {
							p1 = new Paragraph(
									"CNPJ - " + this.objetoContratoCobranca.getRecebedorParcelaFinal7().getCnpj());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}
				}

				if (this.objetoContratoCobranca.getRecebedorParcelaFinal8() != null) {

					p1 = new Paragraph("RECEBEDOR PARCELA FINAL ", subtitulo);
					p1.setAlignment(Element.ALIGN_LEFT);
					p1.setSpacingBefore(10);
					doc.add(p1);

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal8().getConta() != null
							&& this.objetoContratoCobranca.getRecebedorParcelaFinal8().getAgencia() != null
							&& this.objetoContratoCobranca.getRecebedorParcelaFinal8().getBanco() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal8().getConta().equals("")
								&& !this.objetoContratoCobranca.getRecebedorParcelaFinal8().getBanco().equals("")
								&& !this.objetoContratoCobranca.getRecebedorParcelaFinal8().getAgencia().equals("")) {

							p1 = new Paragraph(
									"BANCO - " + this.objetoContratoCobranca.getRecebedorParcelaFinal8().getBanco());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);

							p1 = new Paragraph("AG. "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal8().getAgencia() + " C/C "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal8().getConta());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);

							if (this.objetoContratoCobranca.getRecebedorParcelaFinal8() != null) {
								p1 = new Paragraph("VALOR - R$ " + this.objetoContratoCobranca.getVlrFinalRecebedor8()
										.toString().replace(".", ","));
								p1.setAlignment(Element.ALIGN_LEFT);
								doc.add(p1);
							} else {
								p1 = new Paragraph("VALOR - R$ 0,00");
								p1.setAlignment(Element.ALIGN_LEFT);
								doc.add(p1);
							}
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal8().getNomeCC() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal8().getNomeCC().equals("")) {
							p1 = new Paragraph("FAVORECIDO - "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal8().getNomeCC());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal8().getCpfCC() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal8().getCpfCC().equals("")) {
							p1 = new Paragraph(
									"CPF - " + this.objetoContratoCobranca.getRecebedorParcelaFinal8().getCpfCC());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal8().getCnpj() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal8().getCnpj().equals("")) {
							p1 = new Paragraph(
									"CNPJ - " + this.objetoContratoCobranca.getRecebedorParcelaFinal8().getCnpj());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}
				}

				if (this.objetoContratoCobranca.getRecebedorParcelaFinal9() != null) {

					p1 = new Paragraph("RECEBEDOR PARCELA FINAL ", subtitulo);
					p1.setAlignment(Element.ALIGN_LEFT);
					p1.setSpacingBefore(10);
					doc.add(p1);

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal9().getConta() != null
							&& this.objetoContratoCobranca.getRecebedorParcelaFinal9().getAgencia() != null
							&& this.objetoContratoCobranca.getRecebedorParcelaFinal9().getBanco() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal9().getConta().equals("")
								&& !this.objetoContratoCobranca.getRecebedorParcelaFinal9().getBanco().equals("")
								&& !this.objetoContratoCobranca.getRecebedorParcelaFinal9().getAgencia().equals("")) {

							p1 = new Paragraph(
									"BANCO - " + this.objetoContratoCobranca.getRecebedorParcelaFinal9().getBanco());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);

							p1 = new Paragraph("AG. "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal9().getAgencia() + " C/C "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal9().getConta());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);

							if (this.objetoContratoCobranca.getRecebedorParcelaFinal9() != null) {
								p1 = new Paragraph("VALOR - R$ " + this.objetoContratoCobranca.getVlrFinalRecebedor9()
										.toString().replace(".", ","));
								p1.setAlignment(Element.ALIGN_LEFT);
								doc.add(p1);
							} else {
								p1 = new Paragraph("VALOR - R$ 0,00");
								p1.setAlignment(Element.ALIGN_LEFT);
								doc.add(p1);
							}
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal9().getNomeCC() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal9().getNomeCC().equals("")) {
							p1 = new Paragraph("FAVORECIDO - "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal9().getNomeCC());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal9().getCpfCC() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal9().getCpfCC().equals("")) {
							p1 = new Paragraph(
									"CPF - " + this.objetoContratoCobranca.getRecebedorParcelaFinal9().getCpfCC());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal9().getCnpj() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal9().getCnpj().equals("")) {
							p1 = new Paragraph(
									"CNPJ - " + this.objetoContratoCobranca.getRecebedorParcelaFinal9().getCnpj());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}
				}

				if (this.objetoContratoCobranca.getRecebedorParcelaFinal10() != null) {

					p1 = new Paragraph("RECEBEDOR PARCELA FINAL ", subtitulo);
					p1.setAlignment(Element.ALIGN_LEFT);
					p1.setSpacingBefore(10);
					doc.add(p1);

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal10().getConta() != null
							&& this.objetoContratoCobranca.getRecebedorParcelaFinal10().getAgencia() != null
							&& this.objetoContratoCobranca.getRecebedorParcelaFinal10().getBanco() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal10().getConta().equals("")
								&& !this.objetoContratoCobranca.getRecebedorParcelaFinal10().getBanco().equals("")
								&& !this.objetoContratoCobranca.getRecebedorParcelaFinal10().getAgencia().equals("")) {

							p1 = new Paragraph(
									"BANCO - " + this.objetoContratoCobranca.getRecebedorParcelaFinal10().getBanco());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);

							p1 = new Paragraph("AG. "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal10().getAgencia() + " C/C "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal10().getConta());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);

							if (this.objetoContratoCobranca.getRecebedorParcelaFinal10() != null) {
								p1 = new Paragraph("VALOR - R$ " + this.objetoContratoCobranca.getVlrFinalRecebedor10()
										.toString().replace(".", ","));
								p1.setAlignment(Element.ALIGN_LEFT);
								doc.add(p1);
							} else {
								p1 = new Paragraph("VALOR - R$ 0,00");
								p1.setAlignment(Element.ALIGN_LEFT);
								doc.add(p1);
							}
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal10().getNomeCC() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal10().getNomeCC().equals("")) {
							p1 = new Paragraph("FAVORECIDO - "
									+ this.objetoContratoCobranca.getRecebedorParcelaFinal10().getNomeCC());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal10().getCpfCC() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal10().getCpfCC().equals("")) {
							p1 = new Paragraph(
									"CPF - " + this.objetoContratoCobranca.getRecebedorParcelaFinal10().getCpfCC());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}

					if (this.objetoContratoCobranca.getRecebedorParcelaFinal10().getCnpj() != null) {
						if (!this.objetoContratoCobranca.getRecebedorParcelaFinal10().getCnpj().equals("")) {
							p1 = new Paragraph(
									"CNPJ - " + this.objetoContratoCobranca.getRecebedorParcelaFinal10().getCnpj());
							p1.setAlignment(Element.ALIGN_LEFT);
							doc.add(p1);
						}
					}
				}
			}

			if (this.objetoContratoCobranca.getAcao() != null && this.objetoContratoCobranca.getAcao() != "") {
				p1 = new Paragraph("AÇÃO", subtituloIdent);
				p1.setSpacingBefore(10);
				p1.setAlignment(Element.ALIGN_LEFT);
				doc.add(p1);

				p1 = new Paragraph(this.objetoContratoCobranca.getAcao());
				p1.setAlignment(Element.ALIGN_LEFT);
				doc.add(p1);
			}

			if (this.objetoContratoCobranca.getObservacao() != null
					&& this.objetoContratoCobranca.getObservacao() != "") {
				p1 = new Paragraph("OBSERVAÇÃO", subtituloIdent);
				p1.setSpacingBefore(10);
				p1.setAlignment(Element.ALIGN_LEFT);
				doc.add(p1);

				p1 = new Paragraph(this.objetoContratoCobranca.getObservacao());
				p1.setAlignment(Element.ALIGN_LEFT);
				doc.add(p1);
			}

			if (this.objetoContratoCobranca.getObservacao2() != null
					&& this.objetoContratoCobranca.getObservacao2() != "") {
				p1 = new Paragraph(this.objetoContratoCobranca.getObservacao2());
				p1.setAlignment(Element.ALIGN_LEFT);
				doc.add(p1);
			}

			p1 = new Paragraph("DETALHAMENTO - FORMA DE PAGAMENTO", subtituloIdent);
			p1.setAlignment(Element.ALIGN_LEFT);
			p1.setSpacingBefore(10);
			doc.add(p1);

			PdfPTable table = new PdfPTable(new float[] { 0.10f, 0.22f, 0.16f, 0.16f, 0.22f, 0.16f, 0.10f });
			table.setWidthPercentage(100.0f);

			PdfPCell cell1 = new PdfPCell(new Phrase(" "));
			cell1.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell1);

			PdfPCell cell2 = new PdfPCell(new Phrase("VENCIMENTO"));
			cell2.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell2);

			PdfPCell cell3 = new PdfPCell(new Phrase("VALOR"));
			cell3.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell3);

			PdfPCell cell4 = new PdfPCell(new Phrase("INVESTIDOR"));
			cell4.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell4);

			PdfPCell cell5 = new PdfPCell(new Phrase("ADMINISTRAÇÃO"));
			cell5.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell5);

			PdfPCell cell7 = new PdfPCell(new Phrase("REPASSE"));
			cell7.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell7);

			PdfPCell cell6 = new PdfPCell(new Phrase("PAGO?"));
			cell6.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell6);

			for (ContratoCobrancaDetalhes ccd : this.objetoContratoCobranca.getListContratoCobrancaDetalhes()) {
				cell1 = new PdfPCell(new Phrase(ccd.getNumeroParcela()));
				cell1.setBorder(Rectangle.NO_BORDER);
				table.addCell(cell1);

				cell2 = new PdfPCell(new Phrase(sdfDataPagamento.format(ccd.getDataVencimento())));
				cell2.setBorder(Rectangle.NO_BORDER);
				table.addCell(cell2);

				cell3 = new PdfPCell(new Phrase("R$ " + ccd.getVlrParcela().toString().replace(".", ",")));
				cell3.setBorder(Rectangle.NO_BORDER);
				table.addCell(cell3);

				cell4 = new PdfPCell(new Phrase("R$" + ccd.getVlrRepasse().toString().replace(".", ",")));
				cell4.setBorder(Rectangle.NO_BORDER);
				table.addCell(cell4);

				cell5 = new PdfPCell(new Phrase("R$" + ccd.getVlrRetencao().toString().replace(".", ",")));
				cell5.setBorder(Rectangle.NO_BORDER);
				table.addCell(cell5);

				if (ccd.getVlrComissao() != null) {
					cell7 = new PdfPCell(new Phrase("R$" + ccd.getVlrComissao().toString().replace(".", ",")));
				} else {
					cell7 = new PdfPCell(new Phrase(" "));
				}
				cell7.setBorder(Rectangle.NO_BORDER);
				table.addCell(cell7);

				if (ccd.isParcelaPaga()) {
					cell6 = new PdfPCell(new Phrase("SIM"));
					cell6.setBorder(Rectangle.NO_BORDER);
					table.addCell(cell6);
				} else {
					cell6 = new PdfPCell(new Phrase("NÃO"));
					cell6.setBorder(Rectangle.NO_BORDER);
					table.addCell(cell6);
				}
			}

			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Contrato de Cobrança: Este contrato está aberto por algum outro programa, por favor, feche-o e tente novamente! (Contrato: "
							+ this.objetoContratoCobranca.getNumeroContrato() + ")" + e,
					""));
			return "/Atendimento/Cobranca/ContratoCobrancaConsultar.xhtml";
		} catch (Exception e) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Contrato de Cobrança: Ocorreu um problema ao gerar o contrato! (Contrato: "
									+ this.objetoContratoCobranca.getNumeroContrato() + ")" + e,
							""));

			return "/Atendimento/Cobranca/ContratoCobrancaConsultar.xhtml";
		} finally {
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

			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Contrato de Cobrança: Contrato gerado com sucesso! (Contrato: "
									+ this.objetoContratoCobranca.getNumeroContrato() + ")",
							""));
		}

		return "/Atendimento/Cobranca/ContratoCobrancaSucesso.xhtml";
	}
	
	public void geraPDFFinanceiroAtraso() {
		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.
		 * VGpT0_nF_h4
		 */

		Document doc = null;
		OutputStream os = null;
		try {
			/*
			 * Fonts Utilizadas no PDF
			 */
			Font header = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getDefault();
			Locale locale = new Locale("pt", "BR");

			Calendar date = Calendar.getInstance(zone, locale);

			SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MMM/yyyy", locale);
			SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss", locale);

			ParametrosDao pDao = new ParametrosDao();

			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */
			doc = new Document(PageSize.A4.rotate(), 10, 10, 10, 10);
			this.pathContrato = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
			this.nomeContrato = "Relatório Financeiro Cobrança.pdf";
			os = new FileOutputStream(this.pathContrato + this.nomeContrato);

			// Associa a stream de saída ao
			PdfWriter.getInstance(doc, os);

			// Abre o documento
			doc.open();

			Paragraph p1 = new Paragraph("RELATÓRIO FINANCEIRO DE COBRANÇA");
			if (this.tipoFiltros) {
				p1 = new Paragraph("RELATÓRIO FINANCEIRO DE COBRANÇA - " + sdfDataRel.format(this.relDataContratoInicio)
						+ " a " + sdfDataRel.format(this.relDataContratoFim), header);
			} else {
				if (relObjetoContratoCobranca.size() > 0) {
					p1 = new Paragraph("RELATÓRIO FINANCEIRO DE COBRANÇA - CONTRATO "
							+ relObjetoContratoCobranca.get(0).getNumeroContrato(), header);
				}
			}

			p1.setAlignment(Element.ALIGN_CENTER);
			p1.setSpacingAfter(10);
			doc.add(p1);

			PdfPTable table = new PdfPTable(
					new float[] { 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f });
			table.setWidthPercentage(100.0f);

			PdfPCell cell1 = new PdfPCell(new Phrase("CONTRATO", titulo));
			cell1.setBorderColor(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			cell1.setGrayFill(0.9f);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(10f);
			table.addCell(cell1);

			PdfPCell cell2 = new PdfPCell(new Phrase("DATA", titulo));
			cell2.setBorderColor(BaseColor.BLACK);
			cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell2.setUseBorderPadding(true);
			cell2.setGrayFill(0.9f);
			cell2.setPaddingTop(2f);
			cell2.setPaddingBottom(2f);
			table.addCell(cell2);

			PdfPCell cell3 = new PdfPCell(new Phrase("RESPONSÁVEL", titulo));
			cell3.setBorderColor(BaseColor.BLACK);
			cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell3.setUseBorderPadding(true);
			cell3.setGrayFill(0.9f);
			cell3.setPaddingTop(2f);
			cell3.setPaddingBottom(2f);
			table.addCell(cell3);

			PdfPCell cell4 = new PdfPCell(new Phrase("PAGADOR", titulo));
			cell4.setBorderColor(BaseColor.BLACK);
			cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell4.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell4.setUseBorderPadding(true);
			cell4.setGrayFill(0.9f);
			cell4.setPaddingTop(2f);
			cell4.setPaddingBottom(2f);
			table.addCell(cell4);

			PdfPCell cell6 = new PdfPCell(new Phrase("PARCELA", titulo));
			cell6.setBorderColor(BaseColor.BLACK);
			cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell6.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell6.setUseBorderPadding(true);
			cell6.setGrayFill(0.9f);
			cell6.setPaddingTop(2f);
			cell6.setPaddingBottom(2f);
			table.addCell(cell6);

			PdfPCell cell7 = new PdfPCell(new Phrase("VENCIMENTO", titulo));
			cell7.setBorderColor(BaseColor.BLACK);
			cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell7.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell7.setUseBorderPadding(true);
			cell7.setGrayFill(0.9f);
			cell7.setPaddingTop(2f);
			cell7.setPaddingBottom(2f);
			table.addCell(cell7);
			
			PdfPCell cell8 = new PdfPCell(new Phrase("VENCIMENTO ATUALIZADO", titulo));
			cell8.setBorderColor(BaseColor.BLACK);
			cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell8.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell8.setUseBorderPadding(true);
			cell8.setGrayFill(0.9f);
			cell8.setPaddingTop(2f);
			cell8.setPaddingBottom(2f);
			table.addCell(cell8);

			PdfPCell cell9 = new PdfPCell(new Phrase("VALOR", titulo));
			cell9.setBorderColor(BaseColor.BLACK);
			cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell9.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell9.setUseBorderPadding(true);
			cell9.setGrayFill(0.9f);
			cell9.setPaddingTop(2f);
			cell9.setPaddingBottom(2f);
			table.addCell(cell9);

			PdfPCell cell99 = new PdfPCell(new Phrase("VALOR ATUAL.", titulo));
			cell99.setBorderColor(BaseColor.BLACK);
			cell99.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell99.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell99.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell99.setUseBorderPadding(true);
			cell99.setGrayFill(0.9f);
			cell99.setPaddingTop(2f);
			cell99.setPaddingBottom(2f);
			table.addCell(cell99);

			PdfPCell cell10 = new PdfPCell(new Phrase("PARCELAS EM ABERTO", titulo));
			cell10.setBorderColor(BaseColor.BLACK);
			cell10.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell10.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell10.setUseBorderPadding(true);
			cell10.setGrayFill(0.9f);
			cell10.setPaddingTop(2f);
			cell10.setPaddingBottom(2f);
			table.addCell(cell10);

			PdfPCell cell11 = new PdfPCell(new Phrase("COM BAIXAS PARCIAIS", titulo));
			cell11.setBorderColor(BaseColor.BLACK);
			cell11.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell11.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell11.setUseBorderPadding(true);
			cell11.setGrayFill(0.9f);
			cell11.setPaddingTop(2f);
			cell11.setPaddingBottom(2f);
			table.addCell(cell11);

			BigDecimal totalVencido = BigDecimal.ZERO;
			BigDecimal totalVencidoAtualizado = BigDecimal.ZERO;
			BigDecimal totalRetencao = BigDecimal.ZERO;
			BigDecimal totalComissao = BigDecimal.ZERO;

			for (RelatorioFinanceiroCobranca r : this.relObjetoContratoCobranca) {
				cell1 = new PdfPCell(new Phrase(r.getNumeroContrato()));
				cell1.setBorderColor(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(5f);
				table.addCell(cell1);

				cell2 = new PdfPCell(new Phrase(sdfDataRel.format(r.getDataContrato())));
				cell2.setBorderColor(BaseColor.BLACK);
				cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell2.setPaddingTop(5f);
				cell2.setPaddingBottom(5f);
				table.addCell(cell2);

				cell3 = new PdfPCell(new Phrase(r.getNomeResponsavel()));
				cell3.setBorderColor(BaseColor.BLACK);
				cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell3.setPaddingTop(5f);
				cell3.setPaddingBottom(5f);
				table.addCell(cell3);

				cell4 = new PdfPCell(new Phrase(r.getNomePagador()));
				cell4.setBorderColor(BaseColor.BLACK);
				cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell4.setPaddingTop(5f);
				cell4.setPaddingBottom(5f);
				table.addCell(cell4);

				cell6 = new PdfPCell(new Phrase(r.getParcela()));
				cell6.setBorderColor(BaseColor.BLACK);
				cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell6.setPaddingTop(5f);
				cell6.setPaddingBottom(5f);
				table.addCell(cell6);

				cell7 = new PdfPCell(new Phrase(sdfDataRel.format(r.getDataVencimento())));
				cell7.setBorderColor(BaseColor.BLACK);
				cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setPaddingTop(5f);
				cell7.setPaddingBottom(5f);
				table.addCell(cell7);
				
				cell8 = new PdfPCell(new Phrase(sdfDataRel.format(r.getDataVencimentoAtual())));
				cell8.setBorderColor(BaseColor.BLACK);
				cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setPaddingTop(5f);
				cell8.setPaddingBottom(5f);
				table.addCell(cell8);
				
				cell9 = new PdfPCell(new Phrase("R$ " + r.getValor().toString().replace(".", ",")));
				cell9.setBorderColor(BaseColor.BLACK);
				cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell9.setPaddingTop(5f);
				cell9.setPaddingBottom(5f);
				table.addCell(cell9);

				if (r.getVlrParcelaAtualizada() != null) {
					cell99 = new PdfPCell(new Phrase("R$ " + r.getVlrParcelaAtualizada().toString().replace(".", ",")));
				} else {
					cell99 = new PdfPCell(new Phrase("--"));
				}

				cell99.setBorderColor(BaseColor.BLACK);
				cell99.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell99.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell99.setPaddingTop(5f);
				cell99.setPaddingBottom(5f);
				table.addCell(cell99);

				cell10 = new PdfPCell(new Phrase(r.getQtdeAtrasos()));
				cell10.setBorderColor(BaseColor.BLACK);
				cell10.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell10.setPaddingTop(5f);
				cell10.setPaddingBottom(5f);
				table.addCell(cell10);

				cell11 = new PdfPCell(new Phrase(r.getQtdeBaixasParciais()));
				cell11.setBorderColor(BaseColor.BLACK);
				cell11.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell11.setPaddingTop(5f);
				cell11.setPaddingBottom(5f);
				table.addCell(cell11);

				totalVencido = totalVencido.add(r.getValor());

				if (r.getVlrParcelaAtualizada() != null) {
					totalVencidoAtualizado = totalVencidoAtualizado.add(r.getVlrParcelaAtualizada());
				}

			}

			cell1 = new PdfPCell(new Phrase(""));
			cell1.setBorder(Rectangle.NO_BORDER);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell1);

			cell2 = new PdfPCell(new Phrase(""));
			cell2.setBorder(Rectangle.NO_BORDER);
			cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell2);

			cell3 = new PdfPCell(new Phrase(""));
			cell3.setBorder(Rectangle.NO_BORDER);
			cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell3);

			cell4 = new PdfPCell(new Phrase(""));
			cell4.setBorder(Rectangle.NO_BORDER);
			cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell4);

			cell6 = new PdfPCell(new Phrase(""));
			cell6.setBorder(Rectangle.NO_BORDER);
			cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell6);
			
			cell7 = new PdfPCell(new Phrase(""));
			cell7.setBorder(Rectangle.NO_BORDER);
			cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell7);

			cell8 = new PdfPCell(new Phrase("Total", header));
			cell8.setBorder(Rectangle.NO_BORDER);
			cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell8.setBorderWidthBottom(1f);
			cell8.setPaddingTop(5f);
			cell8.setPaddingBottom(5f);
			table.addCell(cell8);

			cell9 = new PdfPCell(new Phrase("R$ " + totalVencido.toString().replace(".", ","), header));
			cell9.setBorder(Rectangle.NO_BORDER);
			cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell9.setBorderWidthBottom(1f);
			cell9.setPaddingTop(5f);
			cell9.setPaddingBottom(5f);
			table.addCell(cell9);

			cell99 = new PdfPCell(new Phrase("R$ " + totalVencidoAtualizado.toString().replace(".", ","), header));
			cell99.setBorder(Rectangle.NO_BORDER);
			cell99.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell99.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell99.setBorderWidthBottom(1f);
			cell99.setPaddingTop(5f);
			cell99.setPaddingBottom(5f);
			table.addCell(cell99);

			cell10 = new PdfPCell(new Phrase(""));
			cell10.setBorder(Rectangle.NO_BORDER);
			cell10.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell10);
			
			cell11 = new PdfPCell(new Phrase(""));
			cell11.setBorder(Rectangle.NO_BORDER);
			cell11.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell11);

			doc.add(table);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Contrato de Cobrança: Este contrato está aberto por algum outro programa, por favor, feche-o e tente novamente! (Contrato: "
							+ this.objetoContratoCobranca.getNumeroContrato() + ")" + e,
					""));
		} catch (Exception e) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Contrato de Cobrança: Ocorreu um problema ao gerar o contrato! (Contrato: "
									+ this.objetoContratoCobranca.getNumeroContrato() + ")" + e,
							""));
		} finally {
			this.contratoGerado = true;

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
	
	public void imprimeRelatorioFinanceiro() {
		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.
		 * VGpT0_nF_h4
		 */

		Document doc = null;
		OutputStream os = null;
		try {
			/*
			 * Fonts Utilizadas no PDF
			 */
			Font header = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getDefault();
			Locale locale = new Locale("pt", "BR");

			Calendar date = Calendar.getInstance(zone, locale);

			SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MMM/yyyy", locale);
			SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss", locale);

			ParametrosDao pDao = new ParametrosDao();

			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */
			doc = new Document(PageSize.A4.rotate(), 10, 10, 10, 10);
			this.pathContrato = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
			this.nomeContrato = "Relatório Financeiro Cobrança.pdf";
			os = new FileOutputStream(this.pathContrato + this.nomeContrato);

			// Associa a stream de saída ao
			PdfWriter.getInstance(doc, os);

			// Abre o documento
			doc.open();

			Paragraph p1 = new Paragraph("RELATÓRIO FINANCEIRO DE COBRANÇA");
			if (this.tipoFiltros) {
				p1 = new Paragraph("RELATÓRIO FINANCEIRO DE COBRANÇA - " + sdfDataRel.format(this.relDataContratoInicio)
						+ " a " + sdfDataRel.format(this.relDataContratoFim), header);
			} else {
				if (relObjetoContratoCobranca.size() > 0) {
					p1 = new Paragraph("RELATÓRIO FINANCEIRO DE COBRANÇA - CONTRATO "
							+ relObjetoContratoCobranca.get(0).getNumeroContrato(), header);
				}
			}

			p1.setAlignment(Element.ALIGN_CENTER);
			p1.setSpacingAfter(10);
			doc.add(p1);

			PdfPTable table = new PdfPTable(
					new float[] { 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f });
			table.setWidthPercentage(100.0f);

			PdfPCell cell1 = new PdfPCell(new Phrase("CONTRATO", titulo));
			cell1.setBorderColor(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			cell1.setGrayFill(0.9f);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(10f);
			table.addCell(cell1);

			PdfPCell cell2 = new PdfPCell(new Phrase("DATA", titulo));
			cell2.setBorderColor(BaseColor.BLACK);
			cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell2.setUseBorderPadding(true);
			cell2.setGrayFill(0.9f);
			cell2.setPaddingTop(2f);
			cell2.setPaddingBottom(2f);
			table.addCell(cell2);

			PdfPCell cell3 = new PdfPCell(new Phrase("RESPONSÁVEL", titulo));
			cell3.setBorderColor(BaseColor.BLACK);
			cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell3.setUseBorderPadding(true);
			cell3.setGrayFill(0.9f);
			cell3.setPaddingTop(2f);
			cell3.setPaddingBottom(2f);
			table.addCell(cell3);

			PdfPCell cell4 = new PdfPCell(new Phrase("PAGADOR", titulo));
			cell4.setBorderColor(BaseColor.BLACK);
			cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell4.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell4.setUseBorderPadding(true);
			cell4.setGrayFill(0.9f);
			cell4.setPaddingTop(2f);
			cell4.setPaddingBottom(2f);
			table.addCell(cell4);

			PdfPCell cell6 = new PdfPCell(new Phrase("PARCELA", titulo));
			cell6.setBorderColor(BaseColor.BLACK);
			cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell6.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell6.setUseBorderPadding(true);
			cell6.setGrayFill(0.9f);
			cell6.setPaddingTop(2f);
			cell6.setPaddingBottom(2f);
			table.addCell(cell6);

			PdfPCell cell7 = new PdfPCell(new Phrase("VENCIMENTO", titulo));
			cell7.setBorderColor(BaseColor.BLACK);
			cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell7.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell7.setUseBorderPadding(true);
			cell7.setGrayFill(0.9f);
			cell7.setPaddingTop(2f);
			cell7.setPaddingBottom(2f);
			table.addCell(cell7);

			PdfPCell cell8 = new PdfPCell(new Phrase("VALOR", titulo));
			cell8.setBorderColor(BaseColor.BLACK);
			cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell8.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell8.setUseBorderPadding(true);
			cell8.setGrayFill(0.9f);
			cell8.setPaddingTop(2f);
			cell8.setPaddingBottom(2f);
			table.addCell(cell8);

			PdfPCell cell99 = new PdfPCell(new Phrase("VALOR ATUAL.", titulo));
			cell99.setBorderColor(BaseColor.BLACK);
			cell99.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell99.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell99.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell99.setUseBorderPadding(true);
			cell99.setGrayFill(0.9f);
			cell99.setPaddingTop(2f);
			cell99.setPaddingBottom(2f);
			table.addCell(cell99);

			PdfPCell cell9 = new PdfPCell(new Phrase("INVESTIDOR", titulo));
			cell9.setBorderColor(BaseColor.BLACK);
			cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell9.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell9.setUseBorderPadding(true);
			cell9.setGrayFill(0.9f);
			cell9.setPaddingTop(2f);
			cell9.setPaddingBottom(2f);
			table.addCell(cell9);

			PdfPCell cell10 = new PdfPCell(new Phrase("ADMINISTRAÇÃO", titulo));
			cell10.setBorderColor(BaseColor.BLACK);
			cell10.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell10.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell10.setUseBorderPadding(true);
			cell10.setGrayFill(0.9f);
			cell10.setPaddingTop(2f);
			cell10.setPaddingBottom(2f);
			table.addCell(cell10);

			BigDecimal totalVencido = BigDecimal.ZERO;
			BigDecimal totalVencidoAtualizado = BigDecimal.ZERO;
			BigDecimal totalRetencao = BigDecimal.ZERO;
			BigDecimal totalComissao = BigDecimal.ZERO;

			for (RelatorioFinanceiroCobranca r : this.relObjetoContratoCobranca) {
				cell1 = new PdfPCell(new Phrase(r.getNumeroContrato()));
				cell1.setBorderColor(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(5f);
				table.addCell(cell1);

				cell2 = new PdfPCell(new Phrase(sdfDataRel.format(r.getDataContrato())));
				cell1.setBorderColor(BaseColor.BLACK);
				cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell2.setPaddingTop(5f);
				cell2.setPaddingBottom(5f);
				table.addCell(cell2);

				cell3 = new PdfPCell(new Phrase(r.getNomeResponsavel()));
				cell1.setBorderColor(BaseColor.BLACK);
				cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell3.setPaddingTop(5f);
				cell3.setPaddingBottom(5f);
				table.addCell(cell3);

				cell4 = new PdfPCell(new Phrase(r.getNomePagador()));
				cell1.setBorderColor(BaseColor.BLACK);
				cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell4.setPaddingTop(5f);
				cell4.setPaddingBottom(5f);
				table.addCell(cell4);

				cell6 = new PdfPCell(new Phrase(r.getParcela()));
				cell1.setBorderColor(BaseColor.BLACK);
				cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell6.setPaddingTop(5f);
				cell6.setPaddingBottom(5f);
				table.addCell(cell6);

				cell7 = new PdfPCell(new Phrase(sdfDataRel.format(r.getDataVencimento())));
				cell1.setBorderColor(BaseColor.BLACK);
				cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setPaddingTop(5f);
				cell7.setPaddingBottom(5f);
				table.addCell(cell7);

				cell8 = new PdfPCell(new Phrase("R$ " + r.getValor().toString().replace(".", ",")));
				cell1.setBorderColor(BaseColor.BLACK);
				cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setPaddingTop(5f);
				cell8.setPaddingBottom(5f);
				table.addCell(cell8);

				if (r.getVlrParcelaAtualizada() != null) {
					cell99 = new PdfPCell(new Phrase("R$ " + r.getVlrParcelaAtualizada().toString().replace(".", ",")));
				} else {
					cell99 = new PdfPCell(new Phrase("--"));
				}

				cell1.setBorderColor(BaseColor.BLACK);
				cell99.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell99.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell99.setPaddingTop(5f);
				cell99.setPaddingBottom(5f);
				table.addCell(cell99);

				cell9 = new PdfPCell(new Phrase("R$ " + r.getVlrRepasse().toString().replace(".", ",")));
				cell1.setBorderColor(BaseColor.BLACK);
				cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell9.setPaddingTop(5f);
				cell9.setPaddingBottom(5f);
				table.addCell(cell9);

				if (r.getValorComissao() != null) {
					cell10 = new PdfPCell(new Phrase("R$ " + r.getValorRetencao().toString().replace(".", ",")));
					totalComissao = totalComissao.add(r.getValorComissao());
				} else {
					cell10 = new PdfPCell(new Phrase("R$ 0,00"));
				}

				cell1.setBorderColor(BaseColor.BLACK);
				cell10.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell10.setPaddingTop(5f);
				cell10.setPaddingBottom(5f);
				table.addCell(cell10);

				totalVencido = totalVencido.add(r.getValor());

				if (r.getVlrParcelaAtualizada() != null) {
					totalVencidoAtualizado = totalVencidoAtualizado.add(r.getVlrParcelaAtualizada());
				}

				totalRetencao = totalRetencao.add(r.getValorRetencao());

				if (this.relIsCompleto) {
					if (r.getContratoCobranca().getAcao() != null) {
						if (!r.getContratoCobranca().getAcao().equals("")) {
							cell1 = new PdfPCell(
									new Phrase("AÇÃO - " + r.getContratoCobranca().getAcao(), subtituloIdent));
							cell1.setBorderColor(BaseColor.BLACK);
							cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell1.setPaddingTop(5f);
							cell1.setPaddingBottom(5f);
							cell1.setColspan(10);
							table.addCell(cell1);
						}
					}

					if (r.getContratoCobranca().getRecebedor().getConta() != null
							&& r.getContratoCobranca().getRecebedor().getAgencia() != null
							&& r.getContratoCobranca().getRecebedor().getBanco() != null) {
						if (!r.getContratoCobranca().getRecebedor().getConta().equals("")
								&& !r.getContratoCobranca().getRecebedor().getBanco().equals("")
								&& !r.getContratoCobranca().getRecebedor().getAgencia().equals("")) {

							if (r.getContratoCobranca().getVlrRecebedor() != null) {
								cell1 = new PdfPCell(new Phrase("BANCO - "
										+ r.getContratoCobranca().getRecebedor().getBanco() + " AG. "
										+ r.getContratoCobranca().getRecebedor().getAgencia() + " C/C "
										+ r.getContratoCobranca().getRecebedor().getConta() + " VALOR - R$ "
										+ r.getContratoCobranca().getVlrRecebedor().toString().replace(".", ",")));
							} else {
								cell1 = new PdfPCell(
										new Phrase("BANCO - " + r.getContratoCobranca().getRecebedor().getBanco()
												+ " AG. " + r.getContratoCobranca().getRecebedor().getAgencia()
												+ " C/C " + r.getContratoCobranca().getRecebedor().getConta()));
							}
							cell1.setBorderColor(BaseColor.BLACK);
							cell1.disableBorderSide(2);// (1-Top,2-Bottom,3-left and 4-Right border)
							cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell1.setPaddingTop(5f);
							cell1.setPaddingBottom(5f);
							cell1.setColspan(10);
							table.addCell(cell1);
						}
					}

					if (r.getContratoCobranca().getRecebedor().getNomeCC() != null) {
						if (!r.getContratoCobranca().getRecebedor().getNomeCC().equals("")) {
							cell1 = new PdfPCell(
									new Phrase("FAVORECIDO - " + r.getContratoCobranca().getRecebedor().getNomeCC()));
							cell1.setBorderColor(BaseColor.BLACK);
							cell1.disableBorderSide(2);// (1-Top,2-Bottom,3-left and 4-Right border)
							cell1.disableBorderSide(1);
							cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

							cell1.setPaddingTop(5f);
							cell1.setPaddingBottom(5f);
							cell1.setColspan(10);
							table.addCell(cell1);
						}
					}

					String texto = null;
					if (r.getContratoCobranca().getRecebedor().getCpfCC() != null) {
						if (!r.getContratoCobranca().getRecebedor().getCpfCC().equals("")) {
							texto = "CPF - " + r.getContratoCobranca().getRecebedor().getCpfCC();
						}
					}

					if (r.getContratoCobranca().getRecebedor().getCnpjCC() != null) {
						if (!r.getContratoCobranca().getRecebedor().getCnpjCC().equals("")) {
							texto = "CNPJ - " + r.getContratoCobranca().getRecebedor().getCnpj();

						}
					}

					if (texto != null) {
						cell1 = new PdfPCell(new Phrase(texto));
						cell1.setBorderColor(BaseColor.BLACK);
						cell1.disableBorderSide(1);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

						cell1.setPaddingTop(5f);
						cell1.setPaddingBottom(5f);
						cell1.setColspan(10);
						table.addCell(cell1);
					}

					if (r.getContratoCobranca().getRecebedor2() != null) {

						if (r.getContratoCobranca().getRecebedor2().getConta() != null
								&& r.getContratoCobranca().getRecebedor2().getAgencia() != null
								&& r.getContratoCobranca().getRecebedor2().getBanco() != null) {
							if (!r.getContratoCobranca().getRecebedor2().getConta().equals("")
									&& !r.getContratoCobranca().getRecebedor2().getBanco().equals("")
									&& !r.getContratoCobranca().getRecebedor2().getAgencia().equals("")) {

								if (r.getContratoCobranca().getVlrRecebedor2() != null) {
									cell1 = new PdfPCell(new Phrase("BANCO - "
											+ r.getContratoCobranca().getRecebedor2().getBanco() + " AG. "
											+ r.getContratoCobranca().getRecebedor2().getAgencia() + " C/C "
											+ r.getContratoCobranca().getRecebedor2().getConta() + " VALOR - R$ "
											+ r.getContratoCobranca().getVlrRecebedor2().toString().replace(".", ",")));
								} else {
									cell1 = new PdfPCell(
											new Phrase("BANCO - " + r.getContratoCobranca().getRecebedor2().getBanco()
													+ " AG. " + r.getContratoCobranca().getRecebedor2().getAgencia()
													+ " C/C " + r.getContratoCobranca().getRecebedor2().getConta()));
								}

								cell1.setBorderColor(BaseColor.BLACK);
								cell1.disableBorderSide(2);// (1-Top,2-Bottom,3-left and 4-Right border)
								cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
								cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

								cell1.setPaddingTop(5f);
								cell1.setPaddingBottom(5f);
								cell1.setColspan(10);
								table.addCell(cell1);
							}
						}

						if (r.getContratoCobranca().getRecebedor2().getNomeCC() != null) {
							if (!r.getContratoCobranca().getRecebedor2().getNomeCC().equals("")) {
								cell1 = new PdfPCell(new Phrase(
										"FAVORECIDO - " + r.getContratoCobranca().getRecebedor2().getNomeCC()));
								cell1.setBorderColor(BaseColor.BLACK);
								cell1.disableBorderSide(2);// (1-Top,2-Bottom,3-left and 4-Right border)
								cell1.disableBorderSide(1);
								cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
								cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

								cell1.setPaddingTop(5f);
								cell1.setPaddingBottom(5f);
								cell1.setColspan(10);
								table.addCell(cell1);
							}
						}

						texto = null;
						if (r.getContratoCobranca().getRecebedor2().getCpfCC() != null) {
							if (!r.getContratoCobranca().getRecebedor2().getCpfCC().equals("")) {
								texto = "CPF - " + r.getContratoCobranca().getRecebedor2().getCpfCC();
							}
						}

						if (r.getContratoCobranca().getRecebedor2().getCnpjCC() != null) {
							if (!r.getContratoCobranca().getRecebedor2().getCnpjCC().equals("")) {
								texto = "CNPJ - " + r.getContratoCobranca().getRecebedor2().getCnpj();

							}
						}

						if (texto != null) {
							cell1 = new PdfPCell(new Phrase(texto));
							cell1.setBorderColor(BaseColor.BLACK);
							cell1.disableBorderSide(1);
							cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

							cell1.setPaddingTop(5f);
							cell1.setPaddingBottom(5f);
							cell1.setColspan(10);
							table.addCell(cell1);
						}
					}

					if (r.getContratoCobranca().getRecebedor3() != null) {

						if (r.getContratoCobranca().getRecebedor3().getConta() != null
								&& r.getContratoCobranca().getRecebedor3().getAgencia() != null
								&& r.getContratoCobranca().getRecebedor3().getBanco() != null) {
							if (!r.getContratoCobranca().getRecebedor3().getConta().equals("")
									&& !r.getContratoCobranca().getRecebedor3().getBanco().equals("")
									&& !r.getContratoCobranca().getRecebedor3().getAgencia().equals("")) {

								if (r.getContratoCobranca().getVlrRecebedor3() != null) {
									cell1 = new PdfPCell(new Phrase("BANCO - "
											+ r.getContratoCobranca().getRecebedor3().getBanco() + " AG. "
											+ r.getContratoCobranca().getRecebedor3().getAgencia() + " C/C "
											+ r.getContratoCobranca().getRecebedor3().getConta() + " VALOR - R$ "
											+ r.getContratoCobranca().getVlrRecebedor3().toString().replace(".", ",")));
								} else {
									cell1 = new PdfPCell(
											new Phrase("BANCO - " + r.getContratoCobranca().getRecebedor3().getBanco()
													+ " AG. " + r.getContratoCobranca().getRecebedor3().getAgencia()
													+ " C/C " + r.getContratoCobranca().getRecebedor3().getConta()));
								}

								cell1.setBorderColor(BaseColor.BLACK);
								cell1.disableBorderSide(2);// (1-Top,2-Bottom,3-left and 4-Right border)
								cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
								cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

								cell1.setPaddingTop(5f);
								cell1.setPaddingBottom(5f);
								cell1.setColspan(10);
								table.addCell(cell1);
							}
						}

						if (r.getContratoCobranca().getRecebedor3().getNomeCC() != null) {
							if (!r.getContratoCobranca().getRecebedor3().getNomeCC().equals("")) {
								cell1 = new PdfPCell(new Phrase(
										"FAVORECIDO - " + r.getContratoCobranca().getRecebedor3().getNomeCC()));
								cell1.setBorderColor(BaseColor.BLACK);
								cell1.disableBorderSide(2);// (1-Top,2-Bottom,3-left and 4-Right border)
								cell1.disableBorderSide(1);
								cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
								cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

								cell1.setPaddingTop(5f);
								cell1.setPaddingBottom(5f);
								cell1.setColspan(10);
								table.addCell(cell1);
							}
						}

						texto = null;
						if (r.getContratoCobranca().getRecebedor3().getCpfCC() != null) {
							if (!r.getContratoCobranca().getRecebedor3().getCpfCC().equals("")) {
								texto = "CPF - " + r.getContratoCobranca().getRecebedor3().getCpfCC();
							}
						}

						if (r.getContratoCobranca().getRecebedor3().getCnpjCC() != null) {
							if (!r.getContratoCobranca().getRecebedor3().getCnpjCC().equals("")) {
								texto = "CNPJ - " + r.getContratoCobranca().getRecebedor3().getCnpj();

							}
						}

						if (texto != null) {
							cell1 = new PdfPCell(new Phrase(texto));
							cell1.setBorderColor(BaseColor.BLACK);
							cell1.disableBorderSide(1);
							cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

							cell1.setPaddingTop(5f);
							cell1.setPaddingBottom(5f);
							cell1.setColspan(10);
							table.addCell(cell1);
						}
					}

					if (r.getContratoCobranca().getRecebedor4() != null) {

						if (r.getContratoCobranca().getRecebedor4().getConta() != null
								&& r.getContratoCobranca().getRecebedor4().getAgencia() != null
								&& r.getContratoCobranca().getRecebedor4().getBanco() != null) {
							if (!r.getContratoCobranca().getRecebedor4().getConta().equals("")
									&& !r.getContratoCobranca().getRecebedor4().getBanco().equals("")
									&& !r.getContratoCobranca().getRecebedor4().getAgencia().equals("")) {

								if (r.getContratoCobranca().getVlrRecebedor4() != null) {
									cell1 = new PdfPCell(new Phrase("BANCO - "
											+ r.getContratoCobranca().getRecebedor4().getBanco() + " AG. "
											+ r.getContratoCobranca().getRecebedor4().getAgencia() + " C/C "
											+ r.getContratoCobranca().getRecebedor4().getConta() + " VALOR - R$ "
											+ r.getContratoCobranca().getVlrRecebedor4().toString().replace(".", ",")));
								} else {
									cell1 = new PdfPCell(
											new Phrase("BANCO - " + r.getContratoCobranca().getRecebedor4().getBanco()
													+ " AG. " + r.getContratoCobranca().getRecebedor4().getAgencia()
													+ " C/C " + r.getContratoCobranca().getRecebedor4().getConta()));
								}

								cell1.setBorderColor(BaseColor.BLACK);
								cell1.disableBorderSide(2);// (1-Top,2-Bottom,3-left and 4-Right border)
								cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
								cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

								cell1.setPaddingTop(5f);
								cell1.setPaddingBottom(5f);
								cell1.setColspan(10);
								table.addCell(cell1);
							}
						}

						if (r.getContratoCobranca().getRecebedor4().getNomeCC() != null) {
							if (!r.getContratoCobranca().getRecebedor4().getNomeCC().equals("")) {
								cell1 = new PdfPCell(new Phrase(
										"FAVORECIDO - " + r.getContratoCobranca().getRecebedor4().getNomeCC()));
								cell1.setBorderColor(BaseColor.BLACK);
								cell1.disableBorderSide(2);// (1-Top,2-Bottom,3-left and 4-Right border)
								cell1.disableBorderSide(1);
								cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
								cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

								cell1.setPaddingTop(5f);
								cell1.setPaddingBottom(5f);
								cell1.setColspan(10);
								table.addCell(cell1);
							}
						}

						texto = null;
						if (r.getContratoCobranca().getRecebedor4().getCpfCC() != null) {
							if (!r.getContratoCobranca().getRecebedor4().getCpfCC().equals("")) {
								texto = "CPF - " + r.getContratoCobranca().getRecebedor4().getCpfCC();
							}
						}

						if (r.getContratoCobranca().getRecebedor4().getCnpjCC() != null) {
							if (!r.getContratoCobranca().getRecebedor4().getCnpjCC().equals("")) {
								texto = "CNPJ - " + r.getContratoCobranca().getRecebedor4().getCnpj();

							}
						}

						if (texto != null) {
							cell1 = new PdfPCell(new Phrase(texto));
							cell1.setBorderColor(BaseColor.BLACK);
							cell1.disableBorderSide(1);
							cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

							cell1.setPaddingTop(5f);
							cell1.setPaddingBottom(5f);
							cell1.setColspan(10);
							table.addCell(cell1);
						}
					}

					if (r.getContratoCobranca().getRecebedor5() != null) {

						if (r.getContratoCobranca().getRecebedor5().getConta() != null
								&& r.getContratoCobranca().getRecebedor5().getAgencia() != null
								&& r.getContratoCobranca().getRecebedor5().getBanco() != null) {
							if (!r.getContratoCobranca().getRecebedor5().getConta().equals("")
									&& !r.getContratoCobranca().getRecebedor5().getBanco().equals("")
									&& !r.getContratoCobranca().getRecebedor5().getAgencia().equals("")) {

								if (r.getContratoCobranca().getVlrRecebedor5() != null) {
									cell1 = new PdfPCell(new Phrase("BANCO - "
											+ r.getContratoCobranca().getRecebedor5().getBanco() + " AG. "
											+ r.getContratoCobranca().getRecebedor5().getAgencia() + " C/C "
											+ r.getContratoCobranca().getRecebedor5().getConta() + " VALOR - R$ "
											+ r.getContratoCobranca().getVlrRecebedor5().toString().replace(".", ",")));
								} else {
									cell1 = new PdfPCell(
											new Phrase("BANCO - " + r.getContratoCobranca().getRecebedor5().getBanco()
													+ " AG. " + r.getContratoCobranca().getRecebedor5().getAgencia()
													+ " C/C " + r.getContratoCobranca().getRecebedor5().getConta()));
								}

								cell1.setBorderColor(BaseColor.BLACK);
								cell1.disableBorderSide(2);// (1-Top,2-Bottom,3-left and 4-Right border)
								cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
								cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

								cell1.setPaddingTop(5f);
								cell1.setPaddingBottom(5f);
								cell1.setColspan(10);
								table.addCell(cell1);
							}
						}

						if (r.getContratoCobranca().getRecebedor5().getNomeCC() != null) {
							if (!r.getContratoCobranca().getRecebedor5().getNomeCC().equals("")) {
								cell1 = new PdfPCell(new Phrase(
										"FAVORECIDO - " + r.getContratoCobranca().getRecebedor5().getNomeCC()));
								cell1.setBorderColor(BaseColor.BLACK);
								cell1.disableBorderSide(2);// (1-Top,2-Bottom,3-left and 4-Right border)
								cell1.disableBorderSide(1);
								cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
								cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

								cell1.setPaddingTop(5f);
								cell1.setPaddingBottom(5f);
								cell1.setColspan(10);
								table.addCell(cell1);
							}
						}

						texto = null;
						if (r.getContratoCobranca().getRecebedor5().getCpfCC() != null) {
							if (!r.getContratoCobranca().getRecebedor5().getCpfCC().equals("")) {
								texto = "CPF - " + r.getContratoCobranca().getRecebedor5().getCpfCC();
							}
						}

						if (r.getContratoCobranca().getRecebedor5().getCnpjCC() != null) {
							if (!r.getContratoCobranca().getRecebedor5().getCnpjCC().equals("")) {
								texto = "CNPJ - " + r.getContratoCobranca().getRecebedor5().getCnpj();

							}
						}

						if (texto != null) {
							cell1 = new PdfPCell(new Phrase(texto));
							cell1.setBorderColor(BaseColor.BLACK);
							cell1.disableBorderSide(1);
							cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

							cell1.setPaddingTop(5f);
							cell1.setPaddingBottom(5f);
							cell1.setColspan(10);
							table.addCell(cell1);
						}
					}

					if (r.getContratoCobranca().getRecebedor6() != null) {

						if (r.getContratoCobranca().getRecebedor6().getConta() != null
								&& r.getContratoCobranca().getRecebedor6().getAgencia() != null
								&& r.getContratoCobranca().getRecebedor6().getBanco() != null) {
							if (!r.getContratoCobranca().getRecebedor6().getConta().equals("")
									&& !r.getContratoCobranca().getRecebedor6().getBanco().equals("")
									&& !r.getContratoCobranca().getRecebedor6().getAgencia().equals("")) {

								if (r.getContratoCobranca().getVlrRecebedor6() != null) {
									cell1 = new PdfPCell(new Phrase("BANCO - "
											+ r.getContratoCobranca().getRecebedor6().getBanco() + " AG. "
											+ r.getContratoCobranca().getRecebedor6().getAgencia() + " C/C "
											+ r.getContratoCobranca().getRecebedor6().getConta() + " VALOR - R$ "
											+ r.getContratoCobranca().getVlrRecebedor6().toString().replace(".", ",")));
								} else {
									cell1 = new PdfPCell(
											new Phrase("BANCO - " + r.getContratoCobranca().getRecebedor6().getBanco()
													+ " AG. " + r.getContratoCobranca().getRecebedor6().getAgencia()
													+ " C/C " + r.getContratoCobranca().getRecebedor6().getConta()));
								}

								cell1.setBorderColor(BaseColor.BLACK);
								cell1.disableBorderSide(2);// (1-Top,2-Bottom,3-left and 4-Right border)
								cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
								cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

								cell1.setPaddingTop(5f);
								cell1.setPaddingBottom(5f);
								cell1.setColspan(10);
								table.addCell(cell1);
							}
						}

						if (r.getContratoCobranca().getRecebedor6().getNomeCC() != null) {
							if (!r.getContratoCobranca().getRecebedor6().getNomeCC().equals("")) {
								cell1 = new PdfPCell(new Phrase(
										"FAVORECIDO - " + r.getContratoCobranca().getRecebedor6().getNomeCC()));
								cell1.setBorderColor(BaseColor.BLACK);
								cell1.disableBorderSide(2);// (1-Top,2-Bottom,3-left and 4-Right border)
								cell1.disableBorderSide(1);
								cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
								cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

								cell1.setPaddingTop(5f);
								cell1.setPaddingBottom(5f);
								cell1.setColspan(10);
								table.addCell(cell1);
							}
						}

						texto = null;
						if (r.getContratoCobranca().getRecebedor6().getCpfCC() != null) {
							if (!r.getContratoCobranca().getRecebedor6().getCpfCC().equals("")) {
								texto = "CPF - " + r.getContratoCobranca().getRecebedor6().getCpfCC();
							}
						}

						if (r.getContratoCobranca().getRecebedor6().getCnpjCC() != null) {
							if (!r.getContratoCobranca().getRecebedor6().getCnpjCC().equals("")) {
								texto = "CNPJ - " + r.getContratoCobranca().getRecebedor6().getCnpj();

							}
						}

						if (texto != null) {
							cell1 = new PdfPCell(new Phrase(texto));
							cell1.setBorderColor(BaseColor.BLACK);
							cell1.disableBorderSide(1);
							cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

							cell1.setPaddingTop(5f);
							cell1.setPaddingBottom(5f);
							cell1.setColspan(10);
							table.addCell(cell1);
						}
					}

					if (r.getContratoCobranca().getRecebedor7() != null) {

						if (r.getContratoCobranca().getRecebedor7().getConta() != null
								&& r.getContratoCobranca().getRecebedor7().getAgencia() != null
								&& r.getContratoCobranca().getRecebedor7().getBanco() != null) {
							if (!r.getContratoCobranca().getRecebedor7().getConta().equals("")
									&& !r.getContratoCobranca().getRecebedor7().getBanco().equals("")
									&& !r.getContratoCobranca().getRecebedor7().getAgencia().equals("")) {

								if (r.getContratoCobranca().getVlrRecebedor7() != null) {
									cell1 = new PdfPCell(new Phrase("BANCO - "
											+ r.getContratoCobranca().getRecebedor7().getBanco() + " AG. "
											+ r.getContratoCobranca().getRecebedor7().getAgencia() + " C/C "
											+ r.getContratoCobranca().getRecebedor7().getConta() + " VALOR - R$ "
											+ r.getContratoCobranca().getVlrRecebedor7().toString().replace(".", ",")));
								} else {
									cell1 = new PdfPCell(
											new Phrase("BANCO - " + r.getContratoCobranca().getRecebedor7().getBanco()
													+ " AG. " + r.getContratoCobranca().getRecebedor7().getAgencia()
													+ " C/C " + r.getContratoCobranca().getRecebedor7().getConta()));
								}

								cell1.setBorderColor(BaseColor.BLACK);
								cell1.disableBorderSide(2);// (1-Top,2-Bottom,3-left and 4-Right border)
								cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
								cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

								cell1.setPaddingTop(5f);
								cell1.setPaddingBottom(5f);
								cell1.setColspan(10);
								table.addCell(cell1);
							}
						}

						if (r.getContratoCobranca().getRecebedor7().getNomeCC() != null) {
							if (!r.getContratoCobranca().getRecebedor7().getNomeCC().equals("")) {
								cell1 = new PdfPCell(new Phrase(
										"FAVORECIDO - " + r.getContratoCobranca().getRecebedor7().getNomeCC()));
								cell1.setBorderColor(BaseColor.BLACK);
								cell1.disableBorderSide(2);// (1-Top,2-Bottom,3-left and 4-Right border)
								cell1.disableBorderSide(1);
								cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
								cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

								cell1.setPaddingTop(5f);
								cell1.setPaddingBottom(5f);
								cell1.setColspan(10);
								table.addCell(cell1);
							}
						}

						texto = null;
						if (r.getContratoCobranca().getRecebedor7().getCpfCC() != null) {
							if (!r.getContratoCobranca().getRecebedor7().getCpfCC().equals("")) {
								texto = "CPF - " + r.getContratoCobranca().getRecebedor7().getCpfCC();
							}
						}

						if (r.getContratoCobranca().getRecebedor7().getCnpjCC() != null) {
							if (!r.getContratoCobranca().getRecebedor7().getCnpjCC().equals("")) {
								texto = "CNPJ - " + r.getContratoCobranca().getRecebedor7().getCnpj();

							}
						}

						if (texto != null) {
							cell1 = new PdfPCell(new Phrase(texto));
							cell1.setBorderColor(BaseColor.BLACK);
							cell1.disableBorderSide(1);
							cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

							cell1.setPaddingTop(5f);
							cell1.setPaddingBottom(5f);
							cell1.setColspan(10);
							table.addCell(cell1);
						}
					}

					if (r.getContratoCobranca().getRecebedor8() != null) {

						if (r.getContratoCobranca().getRecebedor8().getConta() != null
								&& r.getContratoCobranca().getRecebedor8().getAgencia() != null
								&& r.getContratoCobranca().getRecebedor8().getBanco() != null) {
							if (!r.getContratoCobranca().getRecebedor8().getConta().equals("")
									&& !r.getContratoCobranca().getRecebedor8().getBanco().equals("")
									&& !r.getContratoCobranca().getRecebedor8().getAgencia().equals("")) {

								if (r.getContratoCobranca().getVlrRecebedor8() != null) {
									cell1 = new PdfPCell(new Phrase("BANCO - "
											+ r.getContratoCobranca().getRecebedor8().getBanco() + " AG. "
											+ r.getContratoCobranca().getRecebedor8().getAgencia() + " C/C "
											+ r.getContratoCobranca().getRecebedor8().getConta() + " VALOR - R$ "
											+ r.getContratoCobranca().getVlrRecebedor8().toString().replace(".", ",")));
								} else {
									cell1 = new PdfPCell(
											new Phrase("BANCO - " + r.getContratoCobranca().getRecebedor8().getBanco()
													+ " AG. " + r.getContratoCobranca().getRecebedor8().getAgencia()
													+ " C/C " + r.getContratoCobranca().getRecebedor8().getConta()));
								}

								cell1.setBorderColor(BaseColor.BLACK);
								cell1.disableBorderSide(2);// (1-Top,2-Bottom,3-left and 4-Right border)
								cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
								cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

								cell1.setPaddingTop(5f);
								cell1.setPaddingBottom(5f);
								cell1.setColspan(10);
								table.addCell(cell1);
							}
						}

						if (r.getContratoCobranca().getRecebedor8().getNomeCC() != null) {
							if (!r.getContratoCobranca().getRecebedor8().getNomeCC().equals("")) {
								cell1 = new PdfPCell(new Phrase(
										"FAVORECIDO - " + r.getContratoCobranca().getRecebedor8().getNomeCC()));
								cell1.setBorderColor(BaseColor.BLACK);
								cell1.disableBorderSide(2);// (1-Top,2-Bottom,3-left and 4-Right border)
								cell1.disableBorderSide(1);
								cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
								cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

								cell1.setPaddingTop(5f);
								cell1.setPaddingBottom(5f);
								cell1.setColspan(10);
								table.addCell(cell1);
							}
						}

						texto = null;
						if (r.getContratoCobranca().getRecebedor8().getCpfCC() != null) {
							if (!r.getContratoCobranca().getRecebedor8().getCpfCC().equals("")) {
								texto = "CPF - " + r.getContratoCobranca().getRecebedor8().getCpfCC();
							}
						}

						if (r.getContratoCobranca().getRecebedor8().getCnpjCC() != null) {
							if (!r.getContratoCobranca().getRecebedor8().getCnpjCC().equals("")) {
								texto = "CNPJ - " + r.getContratoCobranca().getRecebedor8().getCnpj();

							}
						}

						if (texto != null) {
							cell1 = new PdfPCell(new Phrase(texto));
							cell1.setBorderColor(BaseColor.BLACK);
							cell1.disableBorderSide(1);
							cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

							cell1.setPaddingTop(5f);
							cell1.setPaddingBottom(5f);
							cell1.setColspan(10);
							table.addCell(cell1);
						}
					}

					if (r.getContratoCobranca().getRecebedor9() != null) {

						if (r.getContratoCobranca().getRecebedor9().getConta() != null
								&& r.getContratoCobranca().getRecebedor9().getAgencia() != null
								&& r.getContratoCobranca().getRecebedor9().getBanco() != null) {
							if (!r.getContratoCobranca().getRecebedor9().getConta().equals("")
									&& !r.getContratoCobranca().getRecebedor9().getBanco().equals("")
									&& !r.getContratoCobranca().getRecebedor9().getAgencia().equals("")) {

								if (r.getContratoCobranca().getVlrRecebedor9() != null) {
									cell1 = new PdfPCell(new Phrase("BANCO - "
											+ r.getContratoCobranca().getRecebedor9().getBanco() + " AG. "
											+ r.getContratoCobranca().getRecebedor9().getAgencia() + " C/C "
											+ r.getContratoCobranca().getRecebedor9().getConta() + " VALOR - R$ "
											+ r.getContratoCobranca().getVlrRecebedor9().toString().replace(".", ",")));
								} else {
									cell1 = new PdfPCell(
											new Phrase("BANCO - " + r.getContratoCobranca().getRecebedor9().getBanco()
													+ " AG. " + r.getContratoCobranca().getRecebedor9().getAgencia()
													+ " C/C " + r.getContratoCobranca().getRecebedor9().getConta()));
								}

								cell1.setBorderColor(BaseColor.BLACK);
								cell1.disableBorderSide(2);// (1-Top,2-Bottom,3-left and 4-Right border)
								cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
								cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

								cell1.setPaddingTop(5f);
								cell1.setPaddingBottom(5f);
								cell1.setColspan(10);
								table.addCell(cell1);
							}
						}

						if (r.getContratoCobranca().getRecebedor9().getNomeCC() != null) {
							if (!r.getContratoCobranca().getRecebedor9().getNomeCC().equals("")) {
								cell1 = new PdfPCell(new Phrase(
										"FAVORECIDO - " + r.getContratoCobranca().getRecebedor9().getNomeCC()));
								cell1.setBorderColor(BaseColor.BLACK);
								cell1.disableBorderSide(2);// (1-Top,2-Bottom,3-left and 4-Right border)
								cell1.disableBorderSide(1);
								cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
								cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

								cell1.setPaddingTop(5f);
								cell1.setPaddingBottom(5f);
								cell1.setColspan(10);
								table.addCell(cell1);

							}
						}

						texto = null;
						if (r.getContratoCobranca().getRecebedor9().getCpfCC() != null) {
							if (!r.getContratoCobranca().getRecebedor9().getCpfCC().equals("")) {
								texto = "CPF - " + r.getContratoCobranca().getRecebedor9().getCpfCC();
							}
						}

						if (r.getContratoCobranca().getRecebedor9().getCnpjCC() != null) {
							if (!r.getContratoCobranca().getRecebedor9().getCnpjCC().equals("")) {
								texto = "CNPJ - " + r.getContratoCobranca().getRecebedor9().getCnpj();

							}
						}

						if (texto != null) {
							cell1 = new PdfPCell(new Phrase(texto));
							cell1.setBorderColor(BaseColor.BLACK);
							cell1.disableBorderSide(1);
							cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

							cell1.setPaddingTop(5f);
							cell1.setPaddingBottom(5f);
							cell1.setColspan(10);
							table.addCell(cell1);
						}
					}

					if (r.getContratoCobranca().getRecebedor10() != null) {

						if (r.getContratoCobranca().getRecebedor10().getConta() != null
								&& r.getContratoCobranca().getRecebedor10().getAgencia() != null
								&& r.getContratoCobranca().getRecebedor10().getBanco() != null) {
							if (!r.getContratoCobranca().getRecebedor10().getConta().equals("")
									&& !r.getContratoCobranca().getRecebedor10().getBanco().equals("")
									&& !r.getContratoCobranca().getRecebedor10().getAgencia().equals("")) {

								if (r.getContratoCobranca().getVlrRecebedor10() != null) {
									cell1 = new PdfPCell(
											new Phrase("BANCO - " + r.getContratoCobranca().getRecebedor10().getBanco()
													+ " AG. " + r.getContratoCobranca().getRecebedor10().getAgencia()
													+ " C/C " + r.getContratoCobranca().getRecebedor10().getConta()
													+ " VALOR - R$ " + r.getContratoCobranca().getVlrRecebedor10()
															.toString().replace(".", ",")));
								} else {
									cell1 = new PdfPCell(
											new Phrase("BANCO - " + r.getContratoCobranca().getRecebedor10().getBanco()
													+ " AG. " + r.getContratoCobranca().getRecebedor10().getAgencia()
													+ " C/C " + r.getContratoCobranca().getRecebedor10().getConta()));
								}

								cell1.setBorderColor(BaseColor.BLACK);
								cell1.disableBorderSide(2);// (1-Top,2-Bottom,3-left and 4-Right border)
								cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
								cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

								cell1.setPaddingTop(5f);
								cell1.setPaddingBottom(5f);
								cell1.setColspan(10);
								table.addCell(cell1);
							}
						}

						if (r.getContratoCobranca().getRecebedor10().getNomeCC() != null) {
							if (!r.getContratoCobranca().getRecebedor10().getNomeCC().equals("")) {
								cell1 = new PdfPCell(new Phrase(
										"FAVORECIDO - " + r.getContratoCobranca().getRecebedor10().getNomeCC()));
								cell1.setBorderColor(BaseColor.BLACK);
								cell1.disableBorderSide(2);// (1-Top,2-Bottom,3-left and 4-Right border)
								cell1.disableBorderSide(1);
								cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
								cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

								cell1.setPaddingTop(5f);
								cell1.setPaddingBottom(5f);
								cell1.setColspan(10);
								table.addCell(cell1);

							}
						}

						texto = null;
						if (r.getContratoCobranca().getRecebedor10().getCpfCC() != null) {
							if (!r.getContratoCobranca().getRecebedor10().getCpfCC().equals("")) {
								texto = "CPF - " + r.getContratoCobranca().getRecebedor10().getCpfCC();
							}
						}

						if (r.getContratoCobranca().getRecebedor10().getCnpjCC() != null) {
							if (!r.getContratoCobranca().getRecebedor10().getCnpjCC().equals("")) {
								texto = "CNPJ - " + r.getContratoCobranca().getRecebedor10().getCnpj();

							}
						}

						if (texto != null) {
							cell1 = new PdfPCell(new Phrase(texto));
							cell1.setBorderColor(BaseColor.BLACK);
							cell1.disableBorderSide(1);
							cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
							cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

							cell1.setPaddingTop(5f);
							cell1.setPaddingBottom(5f);
							cell1.setColspan(10);
							table.addCell(cell1);
						}
					}

					cell1 = new PdfPCell(new Phrase(" "));
					cell1.setBorderColorTop(BaseColor.BLACK);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBorderWidthBottom(1f);
					cell1.setPaddingTop(5f);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(10);
					table.addCell(cell1);
				} else {
					cell1 = new PdfPCell(new Phrase(" "));
					cell1.setBorder(Rectangle.NO_BORDER);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setBorderWidthBottom(1f);
					cell1.setPaddingTop(5f);
					cell1.setPaddingBottom(5f);
					cell1.setColspan(10);
					table.addCell(cell1);
				}

				if (!this.tipoFiltros) {
					break;
				}
			}

			cell1 = new PdfPCell(new Phrase(""));
			cell1.setBorder(Rectangle.NO_BORDER);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell1);

			cell2 = new PdfPCell(new Phrase(""));
			cell2.setBorder(Rectangle.NO_BORDER);
			cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell2);

			cell3 = new PdfPCell(new Phrase(""));
			cell3.setBorder(Rectangle.NO_BORDER);
			cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell3);

			cell4 = new PdfPCell(new Phrase(""));
			cell4.setBorder(Rectangle.NO_BORDER);
			cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell4);

			cell6 = new PdfPCell(new Phrase(""));
			cell6.setBorder(Rectangle.NO_BORDER);
			cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell6);

			cell7 = new PdfPCell(new Phrase("Total", header));
			cell7.setBorder(Rectangle.NO_BORDER);
			cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell7.setBorderWidthBottom(1f);
			cell7.setPaddingTop(5f);
			cell7.setPaddingBottom(5f);
			table.addCell(cell7);

			cell8 = new PdfPCell(new Phrase("R$ " + totalVencido.toString().replace(".", ","), header));
			cell8.setBorder(Rectangle.NO_BORDER);
			cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell8.setBorderWidthBottom(1f);
			cell8.setPaddingTop(5f);
			cell8.setPaddingBottom(5f);
			table.addCell(cell8);

			cell99 = new PdfPCell(new Phrase("R$ " + totalVencidoAtualizado.toString().replace(".", ","), header));
			cell99.setBorder(Rectangle.NO_BORDER);
			cell99.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell99.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell99.setBorderWidthBottom(1f);
			cell99.setPaddingTop(5f);
			cell99.setPaddingBottom(5f);
			table.addCell(cell99);

			cell9 = new PdfPCell(new Phrase("R$ " + totalRetencao.toString().replace(".", ","), header));
			cell9.setBorder(Rectangle.NO_BORDER);
			cell9.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell9.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell9.setBorderWidthBottom(1f);
			cell9.setPaddingTop(5f);
			cell9.setPaddingBottom(5f);
			table.addCell(cell9);

			if (totalComissao != BigDecimal.ZERO) {
				cell10 = new PdfPCell(new Phrase("R$ " + totalComissao.toString().replace(".", ","), header));
			} else {
				cell10 = new PdfPCell(new Phrase("R$ 0,00", header));
			}

			cell10.setBorder(Rectangle.NO_BORDER);
			cell10.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell10.setBorderWidthBottom(1f);
			cell10.setPaddingTop(5f);
			cell10.setPaddingBottom(5f);
			table.addCell(cell10);

			doc.add(table);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Contrato de Cobrança: Este contrato está aberto por algum outro programa, por favor, feche-o e tente novamente! (Contrato: "
							+ this.objetoContratoCobranca.getNumeroContrato() + ")" + e,
					""));
		} catch (Exception e) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Contrato de Cobrança: Ocorreu um problema ao gerar o contrato! (Contrato: "
									+ this.objetoContratoCobranca.getNumeroContrato() + ")" + e,
							""));
		} finally {
			this.contratoGerado = true;

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

	public void imprimeRelatorioFinanceiroContabilidadeXLS() throws IOException {
		this.pathContrato = "";
		this.nomeContrato = "";
		this.file = null;
		this.contratoGerado = false;

		ParametrosDao pDao = new ParametrosDao();
		this.pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString();
		this.nomeContrato = "Relatório Financeiro - Contador.xlsx";

		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		dataHoje.set(Calendar.HOUR_OF_DAY, 0);
		dataHoje.set(Calendar.MINUTE, 0);
		dataHoje.set(Calendar.SECOND, 0);
		dataHoje.set(Calendar.MILLISECOND, 0);

		// dataHoje.add(Calendar.DAY_OF_MONTH, 1);

		String excelFileName = this.pathContrato + this.nomeContrato;// name of excel file

		SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd-MM-yyyy", locale);

		String sheetName = sdfDataRel.format(this.getRelDataContratoInicio()) + " a "
				+ sdfDataRel.format(this.getRelDataContratoFim());// name of sheet

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
		cell.setCellValue("Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("Pagador");
		cell.setCellStyle(cell_style);
		cell = row.createCell(2);
		cell.setCellValue("Parcela");
		cell.setCellStyle(cell_style);
		cell = row.createCell(3);
		cell.setCellValue("Vencimento Original");
		cell.setCellStyle(cell_style);
		cell = row.createCell(4);
		cell.setCellValue("Valor");
		cell.setCellStyle(cell_style);
		cell = row.createCell(5);
		cell.setCellValue("Data Pagamento");
		cell.setCellStyle(cell_style);
		cell = row.createCell(6);
		cell.setCellValue("Valor Pago");
		cell.setCellStyle(cell_style);
		cell = row.createCell(7);
		cell.setCellValue("Valor Juros");
		cell.setCellStyle(cell_style);
		cell = row.createCell(8);
		cell.setCellValue("Valor Amortização");
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

		for (RelatorioFinanceiroCobranca record : this.relObjetoContratoCobranca) {
			countLine++;
			row = sheet.createRow(countLine);

			// Contrato
			cell = row.createCell(0);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getNumeroContrato());

			// Pagador
			cell = row.createCell(1);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getNomePagador());

			// Parcela
			cell = row.createCell(2);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getParcela());

			// Vencimento Original
			cell = row.createCell(3);
			cell.setCellStyle(dateStyle);

			if (record.getDataVencimento() != null) {
				cell.setCellValue(record.getDataVencimento());
			} else {
				cell.setCellValue("");
			}

			// Valor
			cell = row.createCell(4);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);

			if (record.getVlrParcela() != null) {
				cell.setCellValue(((BigDecimal) record.getVlrParcela()).doubleValue());
			} else {
				cell.setCellValue(((BigDecimal) BigDecimal.ZERO).doubleValue());
			}

			// Data Pagamento
			cell = row.createCell(5);
			cell.setCellStyle(dateStyle);

			if (record.getDataUltimoPagamento() != null) {
				cell.setCellValue(record.getDataUltimoPagamento());
			} else {
				cell.setCellValue("");
			}

			// Valor Pago
			cell = row.createCell(6);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);

			if (record.getVlrTotalPago() != null) {
				cell.setCellValue(((BigDecimal) record.getVlrTotalPago()).doubleValue());
			} else {
				cell.setCellValue(((BigDecimal) BigDecimal.ZERO).doubleValue());
			}

			// Valor Juros
			cell = row.createCell(7);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);

			if (record.getVlrJurosParcela() != null) {
				cell.setCellValue(((BigDecimal) record.getVlrJurosParcela()).doubleValue());
			} else {
				cell.setCellValue(((BigDecimal) BigDecimal.ZERO).doubleValue());
			}

			// Valor Amortizacao
			cell = row.createCell(8);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);

			if (record.getVlrAmortizacaoParcela() != null) {
				cell.setCellValue(((BigDecimal) record.getVlrAmortizacaoParcela()).doubleValue());
			} else {
				cell.setCellValue(((BigDecimal) BigDecimal.ZERO).doubleValue());
			}
		}

		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		// write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();

		this.contratoGerado = true;
	}

	public void imprimeRelatorioFinanceiroContabilidade() {
		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.
		 * VGpT0_nF_h4
		 */

		Document doc = null;
		OutputStream os = null;
		try {
			/*
			 * Fonts Utilizadas no PDF
			 */
			Font header = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getDefault();
			Locale locale = new Locale("pt", "BR");

			Calendar date = Calendar.getInstance(zone, locale);

			SimpleDateFormat sdfDataRel = new SimpleDateFormat("dd/MMM/yyyy", locale);
			SimpleDateFormat sdfDataRelComHoras = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss", locale);

			ParametrosDao pDao = new ParametrosDao();

			/*
			 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
			 */
			doc = new Document(PageSize.A4.rotate(), 10, 10, 10, 10);
			this.pathContrato = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
			this.nomeContrato = "Relatório Financeiro Cobrança.pdf";
			os = new FileOutputStream(this.pathContrato + this.nomeContrato);

			// Associa a stream de saída ao
			PdfWriter.getInstance(doc, os);

			// Abre o documento
			doc.open();

			Paragraph p1 = new Paragraph("RELATÓRIO FINANCEIRO CONTABILIDADE");

			p1 = new Paragraph("RELATÓRIO FINANCEIRO DE COBRANÇA - " + sdfDataRel.format(this.relDataContratoInicio)
					+ " a " + sdfDataRel.format(this.relDataContratoFim), header);

			p1.setAlignment(Element.ALIGN_CENTER);
			p1.setSpacingAfter(10);
			doc.add(p1);

			PdfPTable table = new PdfPTable(
					new float[] { 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f });
			table.setWidthPercentage(100.0f);

			PdfPCell cell1 = new PdfPCell(new Phrase("CONTRATO", titulo));
			cell1.setBorderColor(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			cell1.setGrayFill(0.9f);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(10f);
			table.addCell(cell1);

			PdfPCell cell2 = new PdfPCell(new Phrase("PAGADOR", titulo));
			cell2.setBorderColor(BaseColor.BLACK);
			cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell2.setUseBorderPadding(true);
			cell2.setGrayFill(0.9f);
			cell2.setPaddingTop(2f);
			cell2.setPaddingBottom(2f);
			table.addCell(cell2);

			PdfPCell cell3 = new PdfPCell(new Phrase("PARCELA", titulo));
			cell3.setBorderColor(BaseColor.BLACK);
			cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell3.setUseBorderPadding(true);
			cell3.setGrayFill(0.9f);
			cell3.setPaddingTop(2f);
			cell3.setPaddingBottom(2f);
			table.addCell(cell3);

			PdfPCell cell4 = new PdfPCell(new Phrase("DATA VENCIMENTO", titulo));
			cell4.setBorderColor(BaseColor.BLACK);
			cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell4.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell4.setUseBorderPadding(true);
			cell4.setGrayFill(0.9f);
			cell4.setPaddingTop(2f);
			cell4.setPaddingBottom(2f);
			table.addCell(cell4);

			PdfPCell cell6 = new PdfPCell(new Phrase("VALOR", titulo));
			cell6.setBorderColor(BaseColor.BLACK);
			cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell6.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell6.setUseBorderPadding(true);
			cell6.setGrayFill(0.9f);
			cell6.setPaddingTop(2f);
			cell6.setPaddingBottom(2f);
			table.addCell(cell6);

			PdfPCell cell7 = new PdfPCell(new Phrase("DATA PAGAMENTO", titulo));
			cell7.setBorderColor(BaseColor.BLACK);
			cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell7.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell7.setUseBorderPadding(true);
			cell7.setGrayFill(0.9f);
			cell7.setPaddingTop(2f);
			cell7.setPaddingBottom(2f);
			table.addCell(cell7);

			PdfPCell cell8 = new PdfPCell(new Phrase("VALOR PAGO", titulo));
			cell8.setBorderColor(BaseColor.BLACK);
			cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell8.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell8.setUseBorderPadding(true);
			cell8.setGrayFill(0.9f);
			cell8.setPaddingTop(2f);
			cell8.setPaddingBottom(2f);
			table.addCell(cell8);

			PdfPCell cell99 = new PdfPCell(new Phrase("VALOR JUROS", titulo));
			cell99.setBorderColor(BaseColor.BLACK);
			cell99.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell99.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell99.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell99.setUseBorderPadding(true);
			cell99.setGrayFill(0.9f);
			cell99.setPaddingTop(2f);
			cell99.setPaddingBottom(2f);
			table.addCell(cell99);

			PdfPCell cell10 = new PdfPCell(new Phrase("VALOR AMORTIZAÇÃO", titulo));
			cell10.setBorderColor(BaseColor.BLACK);
			cell10.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell10.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell10.setUseBorderPadding(true);
			cell10.setGrayFill(0.9f);
			cell10.setPaddingTop(2f);
			cell10.setPaddingBottom(2f);
			table.addCell(cell10);

			BigDecimal totalParcela = BigDecimal.ZERO;
			BigDecimal totalPago = BigDecimal.ZERO;
			BigDecimal totalJuros = BigDecimal.ZERO;
			BigDecimal totalAmortizacao = BigDecimal.ZERO;

			for (RelatorioFinanceiroCobranca r : this.relObjetoContratoCobranca) {
				cell1 = new PdfPCell(new Phrase(r.getNumeroContrato()));
				cell1.setBorderColor(BaseColor.BLACK);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(5f);
				table.addCell(cell1);

				cell2 = new PdfPCell(new Phrase(r.getNomePagador()));
				cell2.setBorderColor(BaseColor.BLACK);
				cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell2.setPaddingTop(5f);
				cell2.setPaddingBottom(5f);
				table.addCell(cell2);

				cell3 = new PdfPCell(new Phrase(r.getParcela()));
				cell3.setBorderColor(BaseColor.BLACK);
				cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell3.setPaddingTop(5f);
				cell3.setPaddingBottom(5f);
				table.addCell(cell3);

				cell4 = new PdfPCell(new Phrase(sdfDataRel.format(r.getDataVencimento())));
				cell4.setBorderColor(BaseColor.BLACK);
				cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell4.setPaddingTop(5f);
				cell4.setPaddingBottom(5f);
				table.addCell(cell4);

				if (r.getVlrParcela() != null) {
					if (r.getVlrParcela() != BigDecimal.ZERO) {
						cell6 = new PdfPCell(new Phrase("R$ " + r.getVlrParcela().toString().replace(".", ",")));
						totalParcela = totalParcela.add(r.getVlrParcela());
					} else {
						cell6 = new PdfPCell(new Phrase("R$ 0,00"));
					}
				}
				cell6.setBorderColor(BaseColor.BLACK);
				cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell6.setPaddingTop(5f);
				cell6.setPaddingBottom(5f);
				table.addCell(cell6);

				if (r.getDataUltimoPagamento() != null) {
					cell7 = new PdfPCell(new Phrase(sdfDataRel.format(r.getDataUltimoPagamento())));
				} else {
					cell7 = new PdfPCell(new Phrase(""));
				}

				cell7.setBorderColor(BaseColor.BLACK);
				cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell7.setPaddingTop(5f);
				cell7.setPaddingBottom(5f);
				table.addCell(cell7);

				if (r.getVlrTotalPago() != null) {
					if (r.getVlrTotalPago() != BigDecimal.ZERO) {
						cell8 = new PdfPCell(new Phrase("R$ " + r.getVlrTotalPago().toString().replace(".", ",")));
						totalPago = totalPago.add(r.getVlrTotalPago());
					} else {
						cell8 = new PdfPCell(new Phrase("R$ 0,00"));
					}
				} else {
					cell8 = new PdfPCell(new Phrase("R$ 0,00"));
				}

				cell8.setBorderColor(BaseColor.BLACK);
				cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell8.setPaddingTop(5f);
				cell8.setPaddingBottom(5f);
				table.addCell(cell8);

				if (r.getVlrJurosParcela() != null) {
					if (r.getVlrJurosParcela() != BigDecimal.ZERO) {
						cell99 = new PdfPCell(new Phrase("R$ " + r.getVlrJurosParcela().toString().replace(".", ",")));
						totalJuros = totalJuros.add(r.getVlrJurosParcela());
					} else {
						cell99 = new PdfPCell(new Phrase("R$ 0,00"));
					}
				} else {
					cell99 = new PdfPCell(new Phrase("R$ 0,00"));
				}

				cell99.setBorderColor(BaseColor.BLACK);
				cell99.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell99.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell99.setPaddingTop(5f);
				cell99.setPaddingBottom(5f);
				table.addCell(cell99);

				if (r.getVlrAmortizacaoParcela() != null) {
					if (r.getVlrAmortizacaoParcela() != BigDecimal.ZERO) {
						cell10 = new PdfPCell(
								new Phrase("R$ " + r.getVlrAmortizacaoParcela().toString().replace(".", ",")));
						totalAmortizacao = totalAmortizacao.add(r.getVlrAmortizacaoParcela());
					} else {
						cell10 = new PdfPCell(new Phrase("R$ 0,00"));
					}
				} else {
					cell10 = new PdfPCell(new Phrase("R$ 0,00"));
				}

				cell10.setBorderColor(BaseColor.BLACK);
				cell10.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell10.setPaddingTop(5f);
				cell10.setPaddingBottom(5f);
				table.addCell(cell10);
			}

			cell1 = new PdfPCell(new Phrase(""));
			cell1.setBorder(Rectangle.NO_BORDER);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell1);

			cell2 = new PdfPCell(new Phrase(""));
			cell2.setBorder(Rectangle.NO_BORDER);
			cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell2);

			cell3 = new PdfPCell(new Phrase(""));
			cell3.setBorder(Rectangle.NO_BORDER);
			cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell3);

			cell4 = new PdfPCell(new Phrase("Total"));
			cell4.setBorder(Rectangle.NO_BORDER);
			cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell4);

			if (totalParcela != null) {
				if (totalParcela != BigDecimal.ZERO) {
					cell6 = new PdfPCell(new Phrase("R$ " + totalParcela.toString().replace(".", ","), header));
				} else {
					cell6 = new PdfPCell(new Phrase("R$ 0,00", header));
				}
			} else {
				cell6 = new PdfPCell(new Phrase("R$ 0,00"));
			}
			cell6.setBorder(Rectangle.NO_BORDER);
			cell6.setBorderWidthBottom(1f);
			cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell6);

			cell7 = new PdfPCell(new Phrase(""));
			cell7.setBorder(Rectangle.NO_BORDER);
			cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell7.setBorderWidthBottom(1f);
			cell7.setPaddingTop(5f);
			cell7.setPaddingBottom(5f);
			table.addCell(cell7);

			if (totalPago != null) {
				if (totalPago != BigDecimal.ZERO) {
					cell8 = new PdfPCell(new Phrase("R$ " + totalPago.toString().replace(".", ","), header));
				} else {
					cell8 = new PdfPCell(new Phrase("R$ 0,00", header));
				}
			} else {
				cell8 = new PdfPCell(new Phrase("R$ 0,00"));
			}

			cell8.setBorder(Rectangle.NO_BORDER);
			cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell8.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell8.setBorderWidthBottom(1f);
			cell8.setPaddingTop(5f);
			cell8.setPaddingBottom(5f);
			table.addCell(cell8);

			if (totalJuros != null) {
				if (totalJuros != BigDecimal.ZERO) {
					cell99 = new PdfPCell(new Phrase("R$ " + totalJuros.toString().replace(".", ","), header));
				} else {
					cell99 = new PdfPCell(new Phrase("R$ 0,00", header));
				}
			} else {
				cell99 = new PdfPCell(new Phrase("R$ 0,00"));
			}

			cell99.setBorder(Rectangle.NO_BORDER);
			cell99.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell99.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell99.setBorderWidthBottom(1f);
			cell99.setPaddingTop(5f);
			cell99.setPaddingBottom(5f);
			table.addCell(cell99);

			if (totalAmortizacao != null) {
				if (totalAmortizacao != BigDecimal.ZERO) {
					cell10 = new PdfPCell(new Phrase("R$ " + totalAmortizacao.toString().replace(".", ","), header));
				} else {
					cell10 = new PdfPCell(new Phrase("R$ 0,00", header));
				}
			} else {
				cell10 = new PdfPCell(new Phrase("R$ 0,00"));
			}

			cell10.setBorder(Rectangle.NO_BORDER);
			cell10.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell10.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell10.setBorderWidthBottom(1f);
			cell10.setPaddingTop(5f);
			cell10.setPaddingBottom(5f);
			table.addCell(cell10);

			doc.add(table);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Contrato de Cobrança: Este contrato está aberto por algum outro programa, por favor, feche-o e tente novamente! (Contrato: "
							+ this.objetoContratoCobranca.getNumeroContrato() + ")" + e,
					""));
		} catch (Exception e) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Contrato de Cobrança: Ocorreu um problema ao gerar o contrato! (Contrato: "
									+ this.objetoContratoCobranca.getNumeroContrato() + ")" + e,
							""));
		} finally {
			this.contratoGerado = true;

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
	
	public void writeXWPFileBaixado() throws IOException {
		String wordFileName = "Relatório Financeiro Baixado Cobranças.docx";// name of excel file

		String sheetName = "Resultado";// name of sheet

		XWPFDocument document = new XWPFDocument();
		XWPFTable sheet = document.createTable();
		sheet.setWidth(25);
		
		FileOutputStream fileOut = new FileOutputStream(wordFileName);

		// write this workbook to an Outputstream.
		document.write(fileOut);

	}

	public void writeXLSXFileBaixado() throws IOException {
		ParametrosDao pDao = new ParametrosDao();
		this.pathContrato = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomeContrato = "Relatório Financeiro Baixado Cobranças.xlsx";

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
		cell.setCellValue("Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("Data Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(2);
		cell.setCellValue("Responsável");
		cell.setCellStyle(cell_style);
		cell = row.createCell(3);
		cell.setCellValue("Pagador");
		cell.setCellStyle(cell_style);
		cell = row.createCell(4);
		cell.setCellValue("Parcela");
		cell.setCellStyle(cell_style);
		cell = row.createCell(5);
		cell.setCellValue("Vencimento Original");
		cell.setCellStyle(cell_style);
		cell = row.createCell(6);
		cell.setCellValue("Vencimento Atualizado");
		cell.setCellStyle(cell_style);
		cell = row.createCell(7);
		cell.setCellValue("Valor");
		cell.setCellStyle(cell_style);
		cell = row.createCell(8);
		cell.setCellValue("Valor Original");
		cell.setCellStyle(cell_style);
		cell = row.createCell(9);
		cell.setCellValue("Acréscimo");
		cell.setCellStyle(cell_style);
		cell = row.createCell(10);
		cell.setCellValue("Liquidação");
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

		for (RelatorioFinanceiroCobranca record : this.relObjetoContratoCobranca) {
			countLine++;
			row = sheet.createRow(countLine);

			// Contrato
			cell = row.createCell(0);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getNumeroContrato());

			// Data do Contrato
			cell = row.createCell(1);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getDataContrato());

			// Responsavel
			cell = row.createCell(2);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getContratoCobranca().getResponsavel().getNome());

			// Pagador
			cell = row.createCell(3);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getContratoCobranca().getPagador().getNome());

			// Parcela
			cell = row.createCell(4);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getParcela());

			// Vencimento
			cell = row.createCell(5);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getDataVencimento());

			// Vencimento Atualizado
			cell = row.createCell(6);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getDataVencimentoAtual());

			// Valor
			cell = row.createCell(7);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(((BigDecimal) record.getValor()).doubleValue());

			// Valor Parcela
			cell = row.createCell(8);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(((BigDecimal) record.getVlrParcela()).doubleValue());

			// Acréscimo
			cell = row.createCell(9);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(((BigDecimal) record.getAcrescimo()).doubleValue());

			// Liquidação
			cell = row.createCell(10);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getParcelaPagaStr());
		}

		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		// write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();

		this.contratoGerado = true;
	}

	public void writeXLSXFile() throws IOException {
		ParametrosDao pDao = new ParametrosDao();
		this.pathContrato = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomeContrato = "Relatório Financeiro Cobrança.xlsx";

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
		cell.setCellValue("Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("Data Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(2);
		cell.setCellValue("Responsável");
		cell.setCellStyle(cell_style);
		cell = row.createCell(3);
		cell.setCellValue("Pagador");
		cell.setCellStyle(cell_style);
		cell = row.createCell(4);
		cell.setCellValue("Parcela");
		cell.setCellStyle(cell_style);
		cell = row.createCell(5);
		cell.setCellValue("Vencimento");
		cell.setCellStyle(cell_style);
		cell = row.createCell(6);
		cell.setCellValue("Valor");
		cell.setCellStyle(cell_style);
		cell = row.createCell(7);
		cell.setCellValue("Valor Atualizado");
		cell.setCellStyle(cell_style);
		cell = row.createCell(8);
		cell.setCellValue("Status");
		cell.setCellStyle(cell_style);

		if (this.isRelIsRelAtraso()) {
			cell = row.createCell(50);
			cell.setCellValue("Parcelas em Aberto");
			cell.setCellStyle(cell_style);
			cell = row.createCell(50);
			cell.setCellValue("Com Baixas Parciais (# Parcela)");
			cell.setCellStyle(cell_style);
		}

		if (this.exibeSomenteFavorecidosFiltrados.equals("Todos")) {
			cell = row.createCell(9);
			cell.setCellValue("Favorecido");
			cell.setCellStyle(cell_style);
			cell = row.createCell(10);
			cell.setCellValue("Valor");
			cell.setCellStyle(cell_style);
			cell = row.createCell(11);
			cell.setCellValue("Favorecido 2");
			cell.setCellStyle(cell_style);
			cell = row.createCell(12);
			cell.setCellValue("Valor");
			cell.setCellStyle(cell_style);
			cell = row.createCell(13);
			cell.setCellValue("Favorecido 3");
			cell.setCellStyle(cell_style);
			cell = row.createCell(14);
			cell.setCellValue("Valor");
			cell.setCellStyle(cell_style);
			cell = row.createCell(15);
			cell.setCellValue("Favorecido 4");
			cell.setCellStyle(cell_style);
			cell = row.createCell(16);
			cell.setCellValue("Valor");
			cell.setCellStyle(cell_style);
			cell = row.createCell(17);
			cell.setCellValue("Favorecido 5");
			cell.setCellStyle(cell_style);
			cell = row.createCell(18);
			cell.setCellValue("Valor");
			cell.setCellStyle(cell_style);
			cell = row.createCell(19);
			cell.setCellValue("Favorecido 6");
			cell.setCellStyle(cell_style);
			cell = row.createCell(20);
			cell.setCellValue("Valor");
			cell.setCellStyle(cell_style);
			cell = row.createCell(21);
			cell.setCellValue("Favorecido 7");
			cell.setCellStyle(cell_style);
			cell = row.createCell(22);
			cell.setCellValue("Valor");
			cell.setCellStyle(cell_style);
			cell = row.createCell(23);
			cell.setCellValue("Favorecido 8");
			cell.setCellStyle(cell_style);
			cell = row.createCell(24);
			cell.setCellValue("Valor");
			cell.setCellStyle(cell_style);
			cell = row.createCell(25);
			cell.setCellValue("Favorecido 9");
			cell.setCellStyle(cell_style);
			cell = row.createCell(26);
			cell.setCellValue("Valor");
			cell.setCellStyle(cell_style);
			cell = row.createCell(27);
			cell.setCellValue("Favorecido 10");
			cell.setCellStyle(cell_style);
			cell = row.createCell(28);
			cell.setCellValue("Valor");
			cell.setCellStyle(cell_style);
		}

		if (this.exibeSomenteFavorecidosFiltrados.equals("Somente Filtrados")) {
			if (this.idRecebedor > 0) {
				cell = row.createCell(9);
				cell.setCellValue("Favorecido");
				cell.setCellStyle(cell_style);
				cell = row.createCell(10);
				cell.setCellValue("Valor");
				cell.setCellStyle(cell_style);
			}
			if (this.idRecebedor2 > 0) {
				cell = row.createCell(11);
				cell.setCellValue("Favorecido 2");
				cell.setCellStyle(cell_style);
				cell = row.createCell(12);
				cell.setCellValue("Valor");
				cell.setCellStyle(cell_style);
			}
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

		for (RelatorioFinanceiroCobranca record : this.relObjetoContratoCobranca) {
			countLine++;
			row = sheet.createRow(countLine);

			// Contrato
			cell = row.createCell(0);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getNumeroContrato());

			// Data do Contrato
			cell = row.createCell(1);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getDataContrato());

			// Responsavel
			cell = row.createCell(2);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getContratoCobranca().getResponsavel().getNome());

			// Pagador
			cell = row.createCell(3);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getContratoCobranca().getPagador().getNome());

			// Parcela
			cell = row.createCell(4);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getParcela());

			// Vencimento
			cell = row.createCell(5);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getDataVencimento());

			// Valor
			cell = row.createCell(6);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(((BigDecimal) record.valor).doubleValue());

			// Valor Atualizado
			cell = row.createCell(7);
			if (record.getVlrParcelaAtualizada() != null) {
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(((BigDecimal) record.getVlrParcelaAtualizada()).doubleValue());
			} else {
				cell.setCellStyle(cell_style);
				cell.setCellValue("--");
			}

			// status
			cell = row.createCell(8);

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

			if (record.isParcelaPaga()) {
				cell.setCellStyle(cell_style_pago);
				cell.setCellValue("Pago");
			} else {
				ContratoCobrancaDetalhesDao ccdDao = new ContratoCobrancaDetalhesDao();
				ContratoCobrancaDetalhes ccd = ccdDao.findById(record.getIdParcela());

				Calendar dataParcela = Calendar.getInstance(zone, locale);
				if(!CommonsUtil.semValor(ccd.getDataVencimentoAtual())){
					dataParcela.setTime(ccd.getDataVencimentoAtual());
				} else {
					dataParcela.setTime(ccd.getDataVencimento());
				}
				dataHoje.set(Calendar.HOUR_OF_DAY, 0);
				dataHoje.set(Calendar.MINUTE, 0);
				dataHoje.set(Calendar.SECOND, 0);
				dataHoje.set(Calendar.MILLISECOND, 0);

				if (dataParcela.before(dataHoje)) {
					cell.setCellStyle(cell_style_atraso);
					cell.setCellValue("Em atraso");
				} else {
					if (ccd.getListContratoCobrancaDetalhesParcial().size() > 0) {
						cell.setCellStyle(cell_style_bx_parcial);
						cell.setCellValue("Baixado parcialmente");
					} else {
						cell.setCellStyle(cell_style_aberto);
						cell.setCellValue("Em aberto");
					}
				}
			}

			if (this.exibeSomenteFavorecidosFiltrados.equals("Todos")) {
				// FAvorecido 1
				cell = row.createCell(9);
				cell.setCellStyle(cell_style);

				// row.setHeightInPoints((5*sheet.getDefaultRowHeightInPoints()));

				if (record.getContratoCobranca().getRecebedor() != null) {
					String dadosRecebedor = "";
					dadosRecebedor = record.getContratoCobranca().getRecebedor().getNomeCC();
					cell.setCellValue(dadosRecebedor);
				}

				// Valor
				cell = row.createCell(10);
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				if (record.getContratoCobranca().getVlrRecebedor() != null) {
					cell.setCellValue(((BigDecimal) record.getContratoCobranca().getVlrRecebedor()).doubleValue());
				}

				// FAvorecido 2
				cell = row.createCell(11);
				cell.setCellStyle(cell_style);
				if (record.getContratoCobranca().getRecebedor2() != null) {
					String dadosRecebedor = "";
					dadosRecebedor = record.getContratoCobranca().getRecebedor2().getNomeCC();
					cell.setCellValue(dadosRecebedor);
				}

				// Valor
				cell = row.createCell(12);
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				if (record.getContratoCobranca().getVlrRecebedor2() != null) {
					cell.setCellValue(((BigDecimal) record.getContratoCobranca().getVlrRecebedor2()).doubleValue());
				}

				// FAvorecido 3
				cell = row.createCell(13);
				cell.setCellStyle(cell_style);
				if (record.getContratoCobranca().getRecebedor3() != null) {
					String dadosRecebedor = "";
					dadosRecebedor = record.getContratoCobranca().getRecebedor3().getNomeCC();
					cell.setCellValue(dadosRecebedor);
				}

				// Valor
				cell = row.createCell(14);
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				if (record.getContratoCobranca().getVlrRecebedor3() != null) {
					cell.setCellValue(((BigDecimal) record.getContratoCobranca().getVlrRecebedor3()).doubleValue());
				}

				// FAvorecido 4
				cell = row.createCell(15);
				cell.setCellStyle(cell_style);
				if (record.getContratoCobranca().getRecebedor4() != null) {
					String dadosRecebedor = "";
					dadosRecebedor = record.getContratoCobranca().getRecebedor4().getNomeCC();
					cell.setCellValue(dadosRecebedor);
				}

				// Valor
				cell = row.createCell(16);
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				if (record.getContratoCobranca().getVlrRecebedor4() != null) {
					cell.setCellValue(((BigDecimal) record.getContratoCobranca().getVlrRecebedor4()).doubleValue());
				}

				// FAvorecido 5
				cell = row.createCell(17);
				cell.setCellStyle(cell_style);
				if (record.getContratoCobranca().getRecebedor5() != null) {
					String dadosRecebedor = "";
					dadosRecebedor = record.getContratoCobranca().getRecebedor5().getNomeCC();
					cell.setCellValue(dadosRecebedor);
				}

				// Valor
				cell = row.createCell(18);
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				if (record.getContratoCobranca().getVlrRecebedor5() != null) {
					cell.setCellValue(((BigDecimal) record.getContratoCobranca().getVlrRecebedor5()).doubleValue());
				}

				// FAvorecido 6
				cell = row.createCell(19);
				cell.setCellStyle(cell_style);
				if (record.getContratoCobranca().getRecebedor6() != null) {
					String dadosRecebedor = "";
					dadosRecebedor = record.getContratoCobranca().getRecebedor6().getNomeCC();
					cell.setCellValue(dadosRecebedor);
				}

				// Valor
				cell = row.createCell(20);
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				if (record.getContratoCobranca().getVlrRecebedor6() != null) {
					cell.setCellValue(((BigDecimal) record.getContratoCobranca().getVlrRecebedor6()).doubleValue());
				}

				// FAvorecido 7
				cell = row.createCell(21);
				cell.setCellStyle(cell_style);
				if (record.getContratoCobranca().getRecebedor7() != null) {
					String dadosRecebedor = "";
					dadosRecebedor = record.getContratoCobranca().getRecebedor7().getNomeCC();
					cell.setCellValue(dadosRecebedor);
				}

				// Valor
				cell = row.createCell(22);
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				if (record.getContratoCobranca().getVlrRecebedor7() != null) {
					cell.setCellValue(((BigDecimal) record.getContratoCobranca().getVlrRecebedor7()).doubleValue());
				}

				// FAvorecido 8
				cell = row.createCell(23);
				cell.setCellStyle(cell_style);
				if (record.getContratoCobranca().getRecebedor8() != null) {
					String dadosRecebedor = "";
					dadosRecebedor = record.getContratoCobranca().getRecebedor8().getNomeCC();
					cell.setCellValue(dadosRecebedor);
				}

				// Valor
				cell = row.createCell(24);
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				if (record.getContratoCobranca().getVlrRecebedor8() != null) {
					cell.setCellValue(((BigDecimal) record.getContratoCobranca().getVlrRecebedor8()).doubleValue());
				}

				// FAvorecido 9
				cell = row.createCell(25);
				cell.setCellStyle(cell_style);
				if (record.getContratoCobranca().getRecebedor9() != null) {
					String dadosRecebedor = "";
					dadosRecebedor = record.getContratoCobranca().getRecebedor9().getNomeCC();
					cell.setCellValue(dadosRecebedor);
				}

				// Valor
				cell = row.createCell(26);
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				if (record.getContratoCobranca().getVlrRecebedor9() != null) {
					cell.setCellValue(((BigDecimal) record.getContratoCobranca().getVlrRecebedor9()).doubleValue());
				}

				// FAvorecido 10
				cell = row.createCell(27);
				cell.setCellStyle(cell_style);
				if (record.getContratoCobranca().getRecebedor10() != null) {
					String dadosRecebedor = "";
					dadosRecebedor = record.getContratoCobranca().getRecebedor10().getNomeCC();
					cell.setCellValue(dadosRecebedor);
				}

				// Valor
				cell = row.createCell(28);
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				if (record.getContratoCobranca().getVlrRecebedor10() != null) {
					cell.setCellValue(((BigDecimal) record.getContratoCobranca().getVlrRecebedor10()).doubleValue());
				}
			}

			if (this.exibeSomenteFavorecidosFiltrados.equals("Somente Filtrados")) {
				boolean usouFavorecido1 = false;
				// trata favorecido 1 FILTRADO
				if (this.idRecebedor > 0) {
					if (record.getContratoCobranca().getRecebedor() != null) {
						if (record.getContratoCobranca().getRecebedor().getId() == this.idRecebedor) {
							// FAvorecido 1
							cell = row.createCell(9);
							cell.setCellStyle(cell_style);
							if (record.getContratoCobranca().getRecebedor() != null) {
								String dadosRecebedor = "";
								dadosRecebedor = record.getContratoCobranca().getRecebedor().getNomeCC();
								cell.setCellValue(dadosRecebedor);
							}

							// Valor
							cell = row.createCell(10);
							cell.setCellStyle(numericStyle);
							cell.setCellType(CellType.NUMERIC);
							if ((BigDecimal) record.getContratoCobranca().getVlrRecebedor() != null) {
								cell.setCellValue(
										((BigDecimal) record.getContratoCobranca().getVlrRecebedor()).doubleValue());
							} else {
								cell.setCellValue(BigDecimal.ZERO.doubleValue());
							}

							usouFavorecido1 = true;
						}
					}
					if (record.getContratoCobranca().getRecebedor2() != null) {
						if (record.getContratoCobranca().getRecebedor2().getId() == this.idRecebedor) {
							// FAvorecido 1
							cell = row.createCell(9);
							cell.setCellStyle(cell_style);
							if (record.getContratoCobranca().getRecebedor2() != null) {
								String dadosRecebedor = "";
								dadosRecebedor = record.getContratoCobranca().getRecebedor2().getNomeCC();
								cell.setCellValue(dadosRecebedor);
							}

							// Valor
							cell = row.createCell(10);
							cell.setCellStyle(numericStyle);
							cell.setCellType(CellType.NUMERIC);
							if ((BigDecimal) record.getContratoCobranca().getVlrRecebedor2() != null) {
								cell.setCellValue(
										((BigDecimal) record.getContratoCobranca().getVlrRecebedor2()).doubleValue());
							} else {
								cell.setCellValue(BigDecimal.ZERO.doubleValue());
							}

							usouFavorecido1 = true;
						}
					}
					if (record.getContratoCobranca().getRecebedor3() != null) {
						if (record.getContratoCobranca().getRecebedor3().getId() == this.idRecebedor) {
							// FAvorecido 1
							cell = row.createCell(9);
							cell.setCellStyle(cell_style);
							if (record.getContratoCobranca().getRecebedor3() != null) {
								String dadosRecebedor = "";
								dadosRecebedor = record.getContratoCobranca().getRecebedor3().getNomeCC();
								cell.setCellValue(dadosRecebedor);
							}

							// Valor
							cell = row.createCell(10);
							cell.setCellStyle(numericStyle);
							cell.setCellType(CellType.NUMERIC);
							if ((BigDecimal) record.getContratoCobranca().getVlrRecebedor3() != null) {
								cell.setCellValue(
										((BigDecimal) record.getContratoCobranca().getVlrRecebedor3()).doubleValue());
							} else {
								cell.setCellValue(BigDecimal.ZERO.doubleValue());
							}

							usouFavorecido1 = true;
						}
					}
					if (record.getContratoCobranca().getRecebedor4() != null) {
						if (record.getContratoCobranca().getRecebedor4().getId() == this.idRecebedor) {
							// FAvorecido 1
							cell = row.createCell(9);
							cell.setCellStyle(cell_style);
							if (record.getContratoCobranca().getRecebedor4() != null) {
								String dadosRecebedor = "";
								dadosRecebedor = record.getContratoCobranca().getRecebedor4().getNomeCC();
								cell.setCellValue(dadosRecebedor);
							}

							// Valor
							cell = row.createCell(10);
							cell.setCellStyle(numericStyle);
							cell.setCellType(CellType.NUMERIC);
							if ((BigDecimal) record.getContratoCobranca().getVlrRecebedor4() != null) {
								cell.setCellValue(
										((BigDecimal) record.getContratoCobranca().getVlrRecebedor4()).doubleValue());
							} else {
								cell.setCellValue(BigDecimal.ZERO.doubleValue());
							}

							usouFavorecido1 = true;
						}
					}
					if (record.getContratoCobranca().getRecebedor5() != null) {
						if (record.getContratoCobranca().getRecebedor5().getId() == this.idRecebedor) {
							// FAvorecido 1
							cell = row.createCell(9);
							cell.setCellStyle(cell_style);
							if (record.getContratoCobranca().getRecebedor5() != null) {
								String dadosRecebedor = "";
								dadosRecebedor = record.getContratoCobranca().getRecebedor5().getNomeCC();
								cell.setCellValue(dadosRecebedor);
							}

							// Valor
							cell = row.createCell(10);
							cell.setCellStyle(numericStyle);
							cell.setCellType(CellType.NUMERIC);
							if ((BigDecimal) record.getContratoCobranca().getVlrRecebedor5() != null) {
								cell.setCellValue(
										((BigDecimal) record.getContratoCobranca().getVlrRecebedor5()).doubleValue());
							} else {
								cell.setCellValue(BigDecimal.ZERO.doubleValue());
							}

							usouFavorecido1 = true;
						}
					}
					if (record.getContratoCobranca().getRecebedor6() != null) {
						if (record.getContratoCobranca().getRecebedor6().getId() == this.idRecebedor) {
							// FAvorecido 1
							cell = row.createCell(9);
							cell.setCellStyle(cell_style);
							if (record.getContratoCobranca().getRecebedor6() != null) {
								String dadosRecebedor = "";
								dadosRecebedor = record.getContratoCobranca().getRecebedor6().getNomeCC();
								cell.setCellValue(dadosRecebedor);
							}

							// Valor
							cell = row.createCell(10);
							cell.setCellStyle(numericStyle);
							cell.setCellType(CellType.NUMERIC);
							if ((BigDecimal) record.getContratoCobranca().getVlrRecebedor6() != null) {
								cell.setCellValue(
										((BigDecimal) record.getContratoCobranca().getVlrRecebedor6()).doubleValue());
							} else {
								cell.setCellValue(BigDecimal.ZERO.doubleValue());
							}

							usouFavorecido1 = true;
						}
					}
					if (record.getContratoCobranca().getRecebedor7() != null) {
						if (record.getContratoCobranca().getRecebedor7().getId() == this.idRecebedor) {
							// FAvorecido 1
							cell = row.createCell(9);
							cell.setCellStyle(cell_style);
							if (record.getContratoCobranca().getRecebedor7() != null) {
								String dadosRecebedor = "";
								dadosRecebedor = record.getContratoCobranca().getRecebedor7().getNomeCC();
								cell.setCellValue(dadosRecebedor);
							}

							// Valor
							cell = row.createCell(10);
							cell.setCellStyle(numericStyle);
							cell.setCellType(CellType.NUMERIC);
							if ((BigDecimal) record.getContratoCobranca().getVlrRecebedor7() != null) {
								cell.setCellValue(
										((BigDecimal) record.getContratoCobranca().getVlrRecebedor7()).doubleValue());
							} else {
								cell.setCellValue(BigDecimal.ZERO.doubleValue());
							}

							usouFavorecido1 = true;
						}
					}
					if (record.getContratoCobranca().getRecebedor8() != null) {
						if (record.getContratoCobranca().getRecebedor8().getId() == this.idRecebedor) {
							// FAvorecido 1
							cell = row.createCell(9);
							cell.setCellStyle(cell_style);
							if (record.getContratoCobranca().getRecebedor8() != null) {
								String dadosRecebedor = "";
								dadosRecebedor = record.getContratoCobranca().getRecebedor8().getNomeCC();
								cell.setCellValue(dadosRecebedor);
							}

							// Valor
							cell = row.createCell(10);
							cell.setCellStyle(numericStyle);
							cell.setCellType(CellType.NUMERIC);
							if ((BigDecimal) record.getContratoCobranca().getVlrRecebedor8() != null) {
								cell.setCellValue(
										((BigDecimal) record.getContratoCobranca().getVlrRecebedor8()).doubleValue());
							} else {
								cell.setCellValue(BigDecimal.ZERO.doubleValue());
							}

							usouFavorecido1 = true;
						}
					}
					if (record.getContratoCobranca().getRecebedor9() != null) {
						if (record.getContratoCobranca().getRecebedor9().getId() == this.idRecebedor) {
							// FAvorecido 1
							cell = row.createCell(9);
							cell.setCellStyle(cell_style);
							if (record.getContratoCobranca().getRecebedor9() != null) {
								String dadosRecebedor = "";
								dadosRecebedor = record.getContratoCobranca().getRecebedor9().getNomeCC();
								cell.setCellValue(dadosRecebedor);
							}

							// Valor
							cell = row.createCell(10);
							cell.setCellStyle(numericStyle);
							cell.setCellType(CellType.NUMERIC);
							if ((BigDecimal) record.getContratoCobranca().getVlrRecebedor9() != null) {
								cell.setCellValue(
										((BigDecimal) record.getContratoCobranca().getVlrRecebedor9()).doubleValue());
							} else {
								cell.setCellValue(BigDecimal.ZERO.doubleValue());
							}

							usouFavorecido1 = true;
						}
					}
					if (record.getContratoCobranca().getRecebedor10() != null) {
						if (record.getContratoCobranca().getRecebedor10().getId() == this.idRecebedor) {
							// FAvorecido 1
							cell = row.createCell(9);
							cell.setCellStyle(cell_style);
							if (record.getContratoCobranca().getRecebedor10() != null) {
								String dadosRecebedor = "";
								dadosRecebedor = record.getContratoCobranca().getRecebedor10().getNomeCC();
								cell.setCellValue(dadosRecebedor);
							}

							// Valor
							cell = row.createCell(10);
							cell.setCellStyle(numericStyle);
							cell.setCellType(CellType.NUMERIC);
							if ((BigDecimal) record.getContratoCobranca().getVlrRecebedor10() != null) {
								cell.setCellValue(
										((BigDecimal) record.getContratoCobranca().getVlrRecebedor10()).doubleValue());
							} else {
								cell.setCellValue(BigDecimal.ZERO.doubleValue());
							}

							usouFavorecido1 = true;
						}
					}
				}
				// trata favorecido 2 FILTRADO
				if (this.idRecebedor2 > 0) {
					// controle de locação de células
					int idxColumnFavorecido2 = 0;
					int idxColumnValor2 = 0;

					if (usouFavorecido1) {
						idxColumnFavorecido2 = 10;
						idxColumnValor2 = 11;
					} else {
						idxColumnFavorecido2 = 8;
						idxColumnValor2 = 9;
					}

					boolean usouFavorecido2 = false;

					if (record.getContratoCobranca().getRecebedor() != null) {
						if (record.getContratoCobranca().getRecebedor().getId() == this.idRecebedor2) {
							// FAvorecido 1
							cell = row.createCell(idxColumnFavorecido2);
							cell.setCellStyle(cell_style);
							if (record.getContratoCobranca().getRecebedor() != null) {
								String dadosRecebedor = "";
								dadosRecebedor = record.getContratoCobranca().getRecebedor().getNomeCC();
								cell.setCellValue(dadosRecebedor);
							}

							// Valor
							cell = row.createCell(idxColumnValor2);
							cell.setCellStyle(numericStyle);
							cell.setCellType(CellType.NUMERIC);
							cell.setCellValue(
									((BigDecimal) record.getContratoCobranca().getVlrRecebedor()).doubleValue());

							usouFavorecido2 = true;
						}
					}
					if (record.getContratoCobranca().getRecebedor2() != null) {
						if (record.getContratoCobranca().getRecebedor2().getId() == this.idRecebedor2) {
							// FAvorecido 1
							cell = row.createCell(idxColumnFavorecido2);
							cell.setCellStyle(cell_style);
							if (record.getContratoCobranca().getRecebedor2() != null) {
								String dadosRecebedor = "";
								dadosRecebedor = record.getContratoCobranca().getRecebedor2().getNomeCC();
								cell.setCellValue(dadosRecebedor);
							}

							// Valor
							cell = row.createCell(idxColumnValor2);
							cell.setCellStyle(numericStyle);
							cell.setCellType(CellType.NUMERIC);
							cell.setCellValue(
									((BigDecimal) record.getContratoCobranca().getVlrRecebedor2()).doubleValue());

							usouFavorecido2 = true;
						}
					}
					if (record.getContratoCobranca().getRecebedor3() != null) {
						if (record.getContratoCobranca().getRecebedor3().getId() == this.idRecebedor2) {
							// FAvorecido 1
							cell = row.createCell(idxColumnFavorecido2);
							cell.setCellStyle(cell_style);
							if (record.getContratoCobranca().getRecebedor3() != null) {
								String dadosRecebedor = "";
								dadosRecebedor = record.getContratoCobranca().getRecebedor3().getNomeCC();
								cell.setCellValue(dadosRecebedor);
							}

							// Valor
							cell = row.createCell(idxColumnValor2);
							cell.setCellStyle(numericStyle);
							cell.setCellType(CellType.NUMERIC);
							cell.setCellValue(
									((BigDecimal) record.getContratoCobranca().getVlrRecebedor3()).doubleValue());

							usouFavorecido2 = true;
						}
					}
					if (record.getContratoCobranca().getRecebedor4() != null) {
						if (record.getContratoCobranca().getRecebedor4().getId() == this.idRecebedor2) {
							// FAvorecido 1
							cell = row.createCell(idxColumnFavorecido2);
							cell.setCellStyle(cell_style);
							if (record.getContratoCobranca().getRecebedor4() != null) {
								String dadosRecebedor = "";
								dadosRecebedor = record.getContratoCobranca().getRecebedor4().getNomeCC();
								cell.setCellValue(dadosRecebedor);
							}

							// Valor
							cell = row.createCell(idxColumnValor2);
							cell.setCellStyle(numericStyle);
							cell.setCellType(CellType.NUMERIC);
							cell.setCellValue(
									((BigDecimal) record.getContratoCobranca().getVlrRecebedor4()).doubleValue());

							usouFavorecido2 = true;
						}
					}
					if (record.getContratoCobranca().getRecebedor5() != null) {
						if (record.getContratoCobranca().getRecebedor5().getId() == this.idRecebedor2) {
							// FAvorecido 1
							cell = row.createCell(idxColumnFavorecido2);
							cell.setCellStyle(cell_style);
							if (record.getContratoCobranca().getRecebedor5() != null) {
								String dadosRecebedor = "";
								dadosRecebedor = record.getContratoCobranca().getRecebedor5().getNomeCC();
								cell.setCellValue(dadosRecebedor);
							}

							// Valor
							cell = row.createCell(idxColumnValor2);
							cell.setCellStyle(numericStyle);
							cell.setCellType(CellType.NUMERIC);
							cell.setCellValue(
									((BigDecimal) record.getContratoCobranca().getVlrRecebedor5()).doubleValue());

							usouFavorecido2 = true;
						}
					}
					if (record.getContratoCobranca().getRecebedor6() != null) {
						if (record.getContratoCobranca().getRecebedor6().getId() == this.idRecebedor2) {
							// FAvorecido 1
							cell = row.createCell(idxColumnFavorecido2);
							cell.setCellStyle(cell_style);
							if (record.getContratoCobranca().getRecebedor6() != null) {
								String dadosRecebedor = "";
								dadosRecebedor = record.getContratoCobranca().getRecebedor6().getNomeCC();
								cell.setCellValue(dadosRecebedor);
							}

							// Valor
							cell = row.createCell(idxColumnValor2);
							cell.setCellStyle(numericStyle);
							cell.setCellType(CellType.NUMERIC);
							cell.setCellValue(
									((BigDecimal) record.getContratoCobranca().getVlrRecebedor6()).doubleValue());

							usouFavorecido2 = true;
						}
					}
					if (record.getContratoCobranca().getRecebedor7() != null) {
						if (record.getContratoCobranca().getRecebedor7().getId() == this.idRecebedor2) {
							// FAvorecido 1
							cell = row.createCell(idxColumnFavorecido2);
							cell.setCellStyle(cell_style);
							if (record.getContratoCobranca().getRecebedor7() != null) {
								String dadosRecebedor = "";
								dadosRecebedor = record.getContratoCobranca().getRecebedor7().getNomeCC();
								cell.setCellValue(dadosRecebedor);
							}

							// Valor
							cell = row.createCell(idxColumnValor2);
							cell.setCellStyle(numericStyle);
							cell.setCellType(CellType.NUMERIC);
							cell.setCellValue(
									((BigDecimal) record.getContratoCobranca().getVlrRecebedor7()).doubleValue());

							usouFavorecido2 = true;
						}
					}
					if (record.getContratoCobranca().getRecebedor8() != null) {
						if (record.getContratoCobranca().getRecebedor8().getId() == this.idRecebedor2) {
							// FAvorecido 1
							cell = row.createCell(idxColumnFavorecido2);
							cell.setCellStyle(cell_style);
							if (record.getContratoCobranca().getRecebedor8() != null) {
								String dadosRecebedor = "";
								dadosRecebedor = record.getContratoCobranca().getRecebedor8().getNomeCC();
								cell.setCellValue(dadosRecebedor);
							}

							// Valor
							cell = row.createCell(idxColumnValor2);
							cell.setCellStyle(numericStyle);
							cell.setCellType(CellType.NUMERIC);
							cell.setCellValue(
									((BigDecimal) record.getContratoCobranca().getVlrRecebedor8()).doubleValue());

							usouFavorecido2 = true;
						}
					}
					if (record.getContratoCobranca().getRecebedor9() != null) {
						if (record.getContratoCobranca().getRecebedor9().getId() == this.idRecebedor2) {
							// FAvorecido 1
							cell = row.createCell(idxColumnFavorecido2);
							cell.setCellStyle(cell_style);
							if (record.getContratoCobranca().getRecebedor9() != null) {
								String dadosRecebedor = "";
								dadosRecebedor = record.getContratoCobranca().getRecebedor9().getNomeCC();
								cell.setCellValue(dadosRecebedor);
							}

							// Valor
							cell = row.createCell(idxColumnValor2);
							cell.setCellStyle(numericStyle);
							cell.setCellType(CellType.NUMERIC);
							cell.setCellValue(
									((BigDecimal) record.getContratoCobranca().getVlrRecebedor9()).doubleValue());

							usouFavorecido2 = true;
						}
					}
					if (record.getContratoCobranca().getRecebedor10() != null) {
						if (record.getContratoCobranca().getRecebedor10().getId() == this.idRecebedor2) {
							// FAvorecido 1
							cell = row.createCell(idxColumnFavorecido2);
							cell.setCellStyle(cell_style);
							if (record.getContratoCobranca().getRecebedor10() != null) {
								String dadosRecebedor = "";
								dadosRecebedor = record.getContratoCobranca().getRecebedor10().getNomeCC();
								cell.setCellValue(dadosRecebedor);
							}

							// Valor
							cell = row.createCell(idxColumnValor2);
							cell.setCellStyle(numericStyle);
							cell.setCellType(CellType.NUMERIC);
							cell.setCellValue(
									((BigDecimal) record.getContratoCobranca().getVlrRecebedor10()).doubleValue());

							usouFavorecido2 = true;
						}
					}

					// cria estilo da coluna mesmo se não tiver valor
					if (!usouFavorecido2) {
						cell = row.createCell(idxColumnFavorecido2);
						cell.setCellStyle(cell_style);

						cell = row.createCell(idxColumnValor2);
						cell.setCellStyle(numericStyle);
						cell.setCellType(CellType.NUMERIC);
					}
					if (idxColumnFavorecido2 == 7 && idxColumnValor2 == 8) {
						cell = row.createCell(11);
						cell.setCellStyle(cell_style);

						cell = row.createCell(12);
						cell.setCellStyle(numericStyle);
						cell.setCellType(CellType.NUMERIC);
					}
				}
			}
		}

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

		this.contratoGerado = true;
	}
	
	public void gerarXLSFinanceiroBaixadoFIDC() throws IOException {
		ParametrosDao pDao = new ParametrosDao();
		this.pathContrato = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomeContrato = "Relatório Financeiro Baixado FIDC.xlsx";

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
		cell.setCellValue("Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("Responsável");
		cell.setCellStyle(cell_style);
		cell = row.createCell(2);
		cell.setCellValue("Pagador");
		cell.setCellStyle(cell_style);
		cell = row.createCell(3);
		cell.setCellValue("CCB");
		cell.setCellStyle(cell_style);
		cell = row.createCell(4);
		cell.setCellValue("Valor CCB");
		cell.setCellStyle(cell_style);
		cell = row.createCell(5);
		cell.setCellValue("Parcela");
		cell.setCellStyle(cell_style);
		cell = row.createCell(6);
		cell.setCellValue("Valor Parcela");
		cell.setCellStyle(cell_style);
		cell = row.createCell(7);
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

		for (RelatorioFinanceiroCobranca record : this.relObjetoContratoCobranca) {
			countLine++;
			row = sheet.createRow(countLine);

			// Contrato
			cell = row.createCell(0);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getNumeroContrato());

			// Responsável
			cell = row.createCell(1);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getNomeResponsavel());
			
			// Pagador
			cell = row.createCell(2);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getNomePagador());
						
			// CCB
			cell = row.createCell(3);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getParcelaCCB());
			
			// Valor CCB
			cell = row.createCell(4);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(((BigDecimal) record.getValorCCB()).doubleValue());
			
			// Parcela
			cell = row.createCell(5);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getParcela());
			
			// Valor Parcela
			cell = row.createCell(6);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(((BigDecimal) record.getVlrParcela()).doubleValue());
						
			// Valor Pago
			cell = row.createCell(7);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(((BigDecimal) record.getVlrTotalPago()).doubleValue());			
		}

		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		// write this workbook to an Outputstream.
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();

		this.contratoGerado = true;
	}
	
	public void geraXLSFinanceiroAtraso() throws IOException {
		ParametrosDao pDao = new ParametrosDao();
		this.pathContrato = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
		this.nomeContrato = "Relatório Financeiro Cobrança.xlsx";

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
		cell.setCellValue("Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(1);
		cell.setCellValue("Data Contrato");
		cell.setCellStyle(cell_style);
		cell = row.createCell(2);
		cell.setCellValue("Responsável");
		cell.setCellStyle(cell_style);
		cell = row.createCell(3);
		cell.setCellValue("Pagador");
		cell.setCellStyle(cell_style);
		cell = row.createCell(4);
		cell.setCellValue("Parcela");
		cell.setCellStyle(cell_style);
		cell = row.createCell(5);
		cell.setCellValue("Vencimento");
		cell.setCellStyle(cell_style);
		cell = row.createCell(6);
		cell.setCellValue("Valor");
		cell.setCellStyle(cell_style);
		cell = row.createCell(7);
		cell.setCellValue("Valor Atualizado");
		cell.setCellStyle(cell_style);
		cell = row.createCell(8);
		cell.setCellValue("Status");
		cell.setCellStyle(cell_style);
		cell = row.createCell(9);
		cell.setCellValue("Parcelas em Aberto");
		cell.setCellStyle(cell_style);
		cell = row.createCell(10);
		cell.setCellValue("Com Baixas Parciais (# Parcela)");
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

		for (RelatorioFinanceiroCobranca record : this.relObjetoContratoCobranca) {
			countLine++;
			row = sheet.createRow(countLine);

			// Contrato
			cell = row.createCell(0);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getNumeroContrato());

			// Data do Contrato
			cell = row.createCell(1);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getDataContrato());

			// Responsavel
			cell = row.createCell(2);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getContratoCobranca().getResponsavel().getNome());

			// Pagador
			cell = row.createCell(3);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getContratoCobranca().getPagador().getNome());

			// Parcela
			cell = row.createCell(4);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getParcela());

			// Vencimento
			cell = row.createCell(5);
			cell.setCellStyle(dateStyle);
			cell.setCellValue(record.getDataVencimento());

			// Valor
			cell = row.createCell(6);
			cell.setCellStyle(numericStyle);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(((BigDecimal) record.valor).doubleValue());

			// Valor Atualizado
			cell = row.createCell(7);
			if (record.getVlrParcelaAtualizada() != null) {
				cell.setCellStyle(numericStyle);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(((BigDecimal) record.getVlrParcelaAtualizada()).doubleValue());
			} else {
				cell.setCellStyle(cell_style);
				cell.setCellValue("--");
			}

			// status
			cell = row.createCell(8);

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

			if (record.isParcelaPaga()) {
				cell.setCellStyle(cell_style_pago);
				cell.setCellValue("Pago");
			} else {
				ContratoCobrancaDetalhesDao ccdDao = new ContratoCobrancaDetalhesDao();
				ContratoCobrancaDetalhes ccd = ccdDao.findById(record.getIdParcela());

				Calendar dataParcela = Calendar.getInstance(zone, locale);
				dataParcela.setTime(ccd.getDataVencimentoAtual());
				dataHoje.set(Calendar.HOUR_OF_DAY, 0);
				dataHoje.set(Calendar.MINUTE, 0);
				dataHoje.set(Calendar.SECOND, 0);
				dataHoje.set(Calendar.MILLISECOND, 0);

				if (dataParcela.before(dataHoje)) {
					cell.setCellStyle(cell_style_atraso);
					cell.setCellValue("Em atraso");
				} else {
					if (ccd.getListContratoCobrancaDetalhesParcial().size() > 0) {
						cell.setCellStyle(cell_style_bx_parcial);
						cell.setCellValue("Baixado parcialmente");
					} else {
						cell.setCellStyle(cell_style_aberto);
						cell.setCellValue("Em aberto");
					}
				}
			}
			
			cell = row.createCell(9);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getQtdeAtrasos());
			
			cell = row.createCell(10);
			cell.setCellStyle(cell_style);
			cell.setCellValue(record.getQtdeBaixasParciais());
		}

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

		this.contratoGerado = true;
	}

	public void geraRecibo(ContratoCobrancaDetalhesParcial bpContratoDetalhes) {
		FacesContext context = FacesContext.getCurrentInstance();

		Document doc = null;
		OutputStream os = null;

		if (bpContratoDetalhes.getId() > 0) {
			try {
				/*
				 * Fonts Utilizadas no PDF
				 */
				Font titulo = new Font(FontFamily.HELVETICA, 20, Font.BOLD);
				Font subtitulo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
				Font subtituloIdent = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
				Font destaque = new Font(FontFamily.HELVETICA, 10, Font.BOLD);

				TimeZone zone = TimeZone.getDefault();
				Locale locale = new Locale("pt", "BR");

				/*
				 * Formatadores de Data/Hora
				 */
				SimpleDateFormat sdfDataHora = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss", locale);
				SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss", locale);
				SimpleDateFormat sdfDataArquivo = new SimpleDateFormat("dd-MMM-yyyy", locale);
				SimpleDateFormat sdfData = new SimpleDateFormat("dd/MMM/yyyy", locale);
				SimpleDateFormat sdfDia = new SimpleDateFormat("dd", locale);
				SimpleDateFormat sdfMesExtenso = new SimpleDateFormat("MMMM", locale);
				SimpleDateFormat sdfAno = new SimpleDateFormat("yyyy", locale);

				/*
				 * Instancia Calendário
				 */
				Calendar date = Calendar.getInstance(zone, locale);
				ParametrosDao pDao = new ParametrosDao();

				/*
				 * Configuração inicial do PDF - Cria o documento tamanho A4, margens de 2,54cm
				 */
				doc = new Document(PageSize.A4, 10, 10, 10, 10);
				this.pathRecibo = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
				this.nomeRecibo = sdfDataArquivo.format(date.getTime()) + "_Recibo_Contrato_"
						+ this.objetoContratoCobranca.getNumeroContrato() + "_Parcela_"
						+ bpContratoDetalhes.getNumeroParcela() + ".pdf";
				// this.objetoContratoCobranca.getNumeroContrato() + " - Parcela: " +
				// this.bpContratoCobrancaDetalhes.getNumeroParcela()
				os = new FileOutputStream(this.pathRecibo + this.nomeRecibo);

				// Associa a stream de saída ao
				PdfWriter.getInstance(doc, os);

				// Abre o documento
				doc.open();

				Paragraph p1;

				PdfPTable table = new PdfPTable(1);
				table.setWidthPercentage(100.0f);

				Paragraph p2;
				p2 = new Paragraph("RECIBO DE PAGAMENTO", titulo);

				PdfPCell cell1;
				cell1 = new PdfPCell(p2);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setPaddingTop(20f);
				cell1.disableBorderSide(2);
				table.addCell(cell1);

				p2 = new Paragraph("emitido em " + sdfData.format(date.getTime()), subtituloIdent);

				PdfPCell cell20;
				cell20 = new PdfPCell(p2);
				cell20.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell20.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell20.setPaddingBottom(20f);
				cell20.disableBorderSide(1);
				table.addCell(cell20);

				p1 = new Paragraph("Número do Contrato: " + this.objetoContratoCobranca.getNumeroContrato(), subtitulo);
				p1.setAlignment(Element.ALIGN_LEFT);

				PdfPCell cell2;
				cell2 = new PdfPCell(p1);
				cell2.setPaddingTop(20f);
				cell2.setPaddingLeft(20f);
				cell2.disableBorderSide(2);

				table.addCell(cell2);

				int numParcela = 0;
				if (bpContratoDetalhes.getNumeroParcela() != null) {
					numParcela = Integer.valueOf(bpContratoDetalhes.getNumeroParcela());
				}

				p1 = new Paragraph("Parcela: " + String.format("%02d", numParcela), subtitulo);
				p1.setAlignment(Element.ALIGN_LEFT);

				PdfPCell cell3;
				cell3 = new PdfPCell(p1);
				cell3.disableBorderSide(1);
				cell3.disableBorderSide(2);
				cell3.setPaddingTop(10f);
				cell3.setPaddingLeft(20f);

				table.addCell(cell3);

				p1 = new Paragraph("Data Parcela: " + sdfData.format(bpContratoDetalhes.getDataVencimento()),
						subtitulo);

				PdfPCell cell4;
				cell4 = new PdfPCell(p1);
				cell4.disableBorderSide(1);
				cell4.disableBorderSide(2);
				cell4.setPaddingTop(10f);
				cell4.setPaddingLeft(20f);
				table.addCell(cell4);

				p1 = new Paragraph("Valor da Parcela: R$ "
						+ this.objetoContratoCobranca.getVlrParcela().toString().replace(".", ","), subtitulo);

				PdfPCell cell8;
				cell8 = new PdfPCell(p1);
				cell8.disableBorderSide(1);
				cell8.disableBorderSide(2);
				cell8.setPaddingTop(10f);
				cell8.setPaddingLeft(20f);
				table.addCell(cell8);

				if (bpContratoDetalhes.getVlrParcelaAtualizado() != null) {
					p1 = new Paragraph(
							"Valor da Parcela Atualizada: R$ "
									+ bpContratoDetalhes.getVlrParcelaAtualizado().toString().replace(".", ","),
							subtitulo);
					PdfPCell cell5;
					cell5 = new PdfPCell(p1);
					cell5.disableBorderSide(1);
					cell5.disableBorderSide(2);
					cell5.setPaddingTop(10f);
					cell5.setPaddingLeft(20f);
					table.addCell(cell5);
				}

				p1 = new Paragraph("Data do Pagamento: " + sdfData.format(bpContratoDetalhes.getDataPagamento()),
						subtitulo);

				PdfPCell cell21;
				cell21 = new PdfPCell(p1);
				cell21.disableBorderSide(1);
				cell21.disableBorderSide(2);
				cell21.setPaddingTop(10f);
				cell21.setPaddingLeft(20f);
				table.addCell(cell21);

				p1 = new Paragraph(
						"Valor Recebido: R$ " + bpContratoDetalhes.getVlrRecebido().toString().replace(".", ","),
						subtitulo);

				PdfPCell cell6;
				cell6 = new PdfPCell(p1);
				cell6.disableBorderSide(1);
				cell6.disableBorderSide(2);
				cell6.setPaddingTop(10f);
				cell6.setPaddingLeft(20f);
				table.addCell(cell6);

				if (bpContratoDetalhes.getSaldoAPagar() != null) {
					p1 = new Paragraph(
							"Saldo a Pagar: R$ " + bpContratoDetalhes.getSaldoAPagar().toString().replace(".", ","),
							subtitulo);

					PdfPCell cell9;
					cell9 = new PdfPCell(p1);
					cell9.disableBorderSide(1);
					cell9.disableBorderSide(2);
					cell9.setPaddingTop(10f);
					cell9.setPaddingLeft(20f);
					table.addCell(cell9);
				}

				if (bpContratoDetalhes.getRecebedor() != null) {
					p1 = new Paragraph("Forma de Pagamento: " + bpContratoDetalhes.getRecebedor().getNome(), subtitulo);
					PdfPCell cell7;
					cell7 = new PdfPCell(p1);
					cell7.disableBorderSide(1);
					cell7.disableBorderSide(2);
					cell7.setPaddingTop(10f);
					cell7.setPaddingLeft(20f);
					table.addCell(cell7);
				}

				if (bpContratoDetalhes.getObservacaoRecebedor() != null
						&& !bpContratoDetalhes.getObservacaoRecebedor().equals("")) {
					p1 = new Paragraph("Observação: " + bpContratoDetalhes.getObservacaoRecebedor(), subtitulo);
					PdfPCell cell22;
					cell22 = new PdfPCell(p1);
					cell22.disableBorderSide(1);
					cell22.disableBorderSide(2);
					cell22.setPaddingTop(10f);
					cell22.setPaddingLeft(20f);
					table.addCell(cell22);
				}

				p1 = new Paragraph(" ", subtitulo);
				PdfPCell cell23;
				cell23 = new PdfPCell(p1);
				cell23.disableBorderSide(1);
				cell23.setPaddingLeft(20f);
				table.addCell(cell23);

				doc.add(table);

				/*
				 * PdfPCell cell1 = new PdfPCell(new Phrase("CONTRATO", titulo));
				 * cell1.setBorderColor(BaseColor.BLACK);
				 * cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				 * cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				 * cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
				 * cell1.setUseBorderPadding(true); cell1.setGrayFill(0.9f);
				 * cell1.setPaddingTop(10f); cell1.setPaddingBottom(10f); table.addCell(cell1);
				 * Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO" , titulo);
				 * p1.setAlignment(Element.ALIGN_CENTER); p1.setSpacingAfter(10); doc.add(p1);
				 */

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Contrato Cobrança: Este contrato está aberto por algum outro programa, por favor, feche-o e tente novamente! (Contrato: "
								+ this.objetoContratoCobranca.getNumeroContrato() + " - Parcela: "
								+ this.bpContratoCobrancaDetalhes.getNumeroParcela() + ")" + e,
						""));
			} catch (Exception e) {
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"Contrato Cobrança: Ocorreu um problema ao gerar o recibo! (Contrato: "
										+ this.objetoContratoCobranca.getNumeroContrato() + " - Parcela: "
										+ this.bpContratoCobrancaDetalhes.getNumeroParcela() + ")" + e,
								""));
			} finally {
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

			this.reciboGerado = true;

			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Contrato Cobrança: Recibo gerado com sucesso! (Contrato: "
									+ this.objetoContratoCobranca.getNumeroContrato() + " - Parcela: "
									+ this.bpContratoCobrancaDetalhes.getNumeroParcela() + ")",
							""));
		}
	}

	/**
	 * @return the fileRecibo
	 */
	public StreamedContent getFileRecibo() {
		String caminho = this.pathRecibo + this.nomeRecibo;
		String arquivo = this.nomeRecibo;
		FileInputStream stream = null;

		this.reciboGerado = false;
		try {
			stream = new FileInputStream(caminho);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fileRecibo = new DefaultStreamedContent(stream, caminho, arquivo);

		return fileRecibo;
	}

	/**
	 * @param fileRecibo the fileRecibo to set
	 */
	public void setFileRecibo(StreamedContent fileRecibo) {
		this.fileRecibo = fileRecibo;
	}

	/**
	 * @return the reciboGerado
	 */
	public boolean isReciboGerado() {
		return reciboGerado;
	}

	/**
	 * @param reciboGerado the reciboGerado to set
	 */
	public void setReciboGerado(boolean reciboGerado) {
		this.reciboGerado = reciboGerado;
	}

	/**
	 * @return the lazyModel
	 */
	public LazyDataModel<ContratoCobranca> getLazyModel() {
		return lazyModel;
	}

	/**
	 * @param lazyModel the lazyModel to set
	 */
	public void setLazyModel(LazyDataModel<ContratoCobranca> lazyModel) {
		this.lazyModel = lazyModel;
	}

	/**
	 * @return the objetoContratoCobranca
	 */
	public ContratoCobranca getObjetoContratoCobranca() {
		return objetoContratoCobranca;
	}

	/**
	 * @param objetoContratoCobranca the objetoContratoCobranca to set
	 */
	public void setObjetoContratoCobranca(ContratoCobranca objetoContratoCobranca) {
		this.objetoContratoCobranca = objetoContratoCobranca;
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
		if (updateMode) {
			this.tituloPainel = "Editar";
		} else {
			this.tituloPainel = "Visualizar";
		}
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
		if (deleteMode) {
			this.tituloPainel = "Excluir";
		} else if (this.updateMode) {
			this.tituloPainel = "Editar";
		} else if (this.baixarMode) {
			this.tituloPainel = "Baixar";
		} else {
			this.tituloPainel = "Visualizar";
		}	
		this.deleteMode = deleteMode;
	}
	

	public boolean isBaixarMode() {
		return baixarMode;
	}

	public void setBaixarMode(boolean baixarMode) {
		if (baixarMode) {
			this.tituloPainel = "Baixar";
		} else if (this.updateMode) {
			this.tituloPainel = "Editar";
		} else if (this.deleteMode) {
			this.tituloPainel = "Excluir";
		} else {
			this.tituloPainel = "Visualizar";
		}	
		
		this.baixarMode = baixarMode;
	}

	/**
	 * @return the tituloPainel
	 */
	public String getTituloPainel() {
		return tituloPainel;
	}

	/**
	 * @param tituloPainel the tituloPainel to set
	 */
	public void setTituloPainel(String tituloPainel) {
		this.tituloPainel = tituloPainel;
	}

	/**
	 * @return the selectedPagador
	 */
	public PagadorRecebedor getSelectedPagador() {
		return selectedPagador;
	}

	/**
	 * @param selectedPagador the selectedPagador to set
	 */
	public void setSelectedPagador(PagadorRecebedor selectedPagador) {
		this.selectedPagador = selectedPagador;
	}
	
	public PagadorRecebedor getSelectedPagadorGenerico() {
		return selectedPagadorGenerico;
	}

	public void setSelectedPagadorGenerico(PagadorRecebedor selectedPagadorGenerico) {
		this.selectedPagadorGenerico = selectedPagadorGenerico;
	}

	/**
	 * @return the listPagadores
	 */
	public List<PagadorRecebedor> getListPagadores() {
		return listPagadores;
	}

	/**
	 * @param listPagadores the listPagadores to set
	 */
	public void setListPagadores(List<PagadorRecebedor> listPagadores) {
		this.listPagadores = listPagadores;
	}

	/**
	 * @return the nomePagador
	 */
	public String getNomePagador() {
		return nomePagador;
	}

	/**
	 * @param nomePagador the nomePagador to set
	 */
	public void setNomePagador(String nomePagador) {
		this.nomePagador = nomePagador;
	}

	/**
	 * @return the idPagador
	 */
	public long getIdPagador() {
		return idPagador;
	}

	/**
	 * @param idPagador the idPagador to set
	 */
	public void setIdPagador(long idPagador) {
		this.idPagador = idPagador;
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
	 * @return the ListResposavel listRecebedorPagador
	 */
	public List<PagadorRecebedor> getListRecebedorPagador() {
		return listPagadores;
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
	 * @return the selectedResponsavel
	 */
	public Responsavel getSelectedResponsavel() {
		return selectedResponsavel;
	}

	/**
	 * @param selectedResponsavel the selectedResponsavel to set
	 */
	public void setSelectedResponsavel(Responsavel selectedResponsavel) {
		this.selectedResponsavel = selectedResponsavel;
	}

	/**
	 * @return the listResponsaveis
	 */
	public List<Responsavel> getListResponsaveis() {
		return listResponsaveis;
	}

	/**
	 * @param listResponsaveis the listResponsaveis to set
	 */
	public void setListResponsaveis(List<Responsavel> listResponsaveis) {
		this.listResponsaveis = listResponsaveis;
	}

	/**
	 * @return the nomeResponsavel
	 */
	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	/**
	 * @param nomeResponsavel the nomeResponsavel to set
	 */
	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}

	/**
	 * @return the idResponsavel
	 */
	public long getIdResponsavel() {
		return idResponsavel;
	}

	/**
	 * @param idResponsavel the idResponsavel to set
	 */
	public void setIdResponsavel(long idResponsavel) {
		this.idResponsavel = idResponsavel;
	}

	/**
	 * @return the selectedImovel
	 */
	public ImovelCobranca getSelectedImovel() {
		return selectedImovel;
	}

	/**
	 * @param selectedImovel the selectedImovel to set
	 */
	public void setSelectedImovel(ImovelCobranca selectedImovel) {
		this.selectedImovel = selectedImovel;
	}

	/**
	 * @return the listImoveis
	 */
	public List<ImovelCobranca> getListImoveis() {
		return listImoveis;
	}

	/**
	 * @param listImoveis the listImoveis to set
	 */
	public void setListImoveis(List<ImovelCobranca> listImoveis) {
		this.listImoveis = listImoveis;
	}

	/**
	 * @return the nomeImovel
	 */
	public String getNomeImovel() {
		return nomeImovel;
	}

	/**
	 * @param nomeImovel the nomeImovel to set
	 */
	public void setNomeImovel(String nomeImovel) {
		this.nomeImovel = nomeImovel;
	}

	/**
	 * @return the idImovel
	 */
	public long getIdImovel() {
		return idImovel;
	}

	/**
	 * @param idImovel the idImovel to set
	 */
	public void setIdImovel(long idImovel) {
		this.idImovel = idImovel;
	}

	/**
	 * @return the contratoGerado
	 */
	public boolean isContratoGerado() {
		return contratoGerado;
	}

	/**
	 * @param contratoGerado the contratoGerado to set
	 */
	public void setContratoGerado(boolean contratoGerado) {
		this.contratoGerado = contratoGerado;
	}

	/**
	 * @return the qtdeParcelas
	 */
	public String getQtdeParcelas() {
		return qtdeParcelas;
	}

	/**
	 * @param qtdeParcelas the qtdeParcelas to set
	 */
	public void setQtdeParcelas(String qtdeParcelas) {
		this.qtdeParcelas = qtdeParcelas;
	}

	/**
	 * @return the relDataContratoInicio
	 */
	public Date getRelDataContratoInicio() {
		return relDataContratoInicio;
	}

	/**
	 * @param relDataContratoInicio the relDataContratoInicio to set
	 */
	public void setRelDataContratoInicio(Date relDataContratoInicio) {
		this.relDataContratoInicio = relDataContratoInicio;
	}

	/**
	 * @return the relDataContratoFim
	 */
	public Date getRelDataContratoFim() {
		return relDataContratoFim;
	}

	/**
	 * @param relDataContratoFim the relDataContratoFim to set
	 */
	public void setRelDataContratoFim(Date relDataContratoFim) {
		this.relDataContratoFim = relDataContratoFim;
	}

	/**
	 * @return the relObjetoContratoCobranca
	 */
	public List<RelatorioFinanceiroCobranca> getRelObjetoContratoCobranca() {
		return relObjetoContratoCobranca;
	}

	/**
	 * @param relObjetoContratoCobranca the relObjetoContratoCobranca to set
	 */
	public void setRelObjetoContratoCobranca(List<RelatorioFinanceiroCobranca> relObjetoContratoCobranca) {
		this.relObjetoContratoCobranca = relObjetoContratoCobranca;
	}

	/**
	 * @return the relSelectedObjetoContratoCobranca
	 */
	public RelatorioFinanceiroCobranca getRelSelectedObjetoContratoCobranca() {
		return relSelectedObjetoContratoCobranca;
	}

	/**
	 * @param relSelectedObjetoContratoCobranca the
	 *                                          relSelectedObjetoContratoCobranca to
	 *                                          set
	 */
	public void setRelSelectedObjetoContratoCobranca(RelatorioFinanceiroCobranca relSelectedObjetoContratoCobranca) {
		this.relSelectedObjetoContratoCobranca = relSelectedObjetoContratoCobranca;
	}

	/**
	 * @return the origemTelaBaixar
	 */
	public String getOrigemTelaBaixar() {
		return origemTelaBaixar;
	}

	/**
	 * @param origemTelaBaixar the origemTelaBaixar to set
	 */
	public void setOrigemTelaBaixar(String origemTelaBaixar) {
		this.origemTelaBaixar = origemTelaBaixar;
	}

	/**
	 * @return the relIsRelAtraso
	 */
	public boolean isRelIsRelAtraso() {
		return relIsRelAtraso;
	}

	/**
	 * @param relIsRelAtraso the relIsRelAtraso to set
	 */
	public void setRelIsRelAtraso(boolean relIsRelAtraso) {
		this.relIsRelAtraso = relIsRelAtraso;
	}

	/**
	 * @return the selectedContratoCobrancaDetalhes
	 */
	public ContratoCobrancaDetalhes getSelectedContratoCobrancaDetalhes() {
		return selectedContratoCobrancaDetalhes;
	}

	/**
	 * @param selectedContratoCobrancaDetalhes the selectedContratoCobrancaDetalhes
	 *                                         to set
	 */
	public void setSelectedContratoCobrancaDetalhes(ContratoCobrancaDetalhes selectedContratoCobrancaDetalhes) {
		this.selectedContratoCobrancaDetalhes = selectedContratoCobrancaDetalhes;
	}

	/**
	 * @return the listContratoCobrancaFavorecidos
	 */
	public List<ContratoCobrancaFavorecidos> getListContratoCobrancaFavorecidos() {
		return listContratoCobrancaFavorecidos;
	}

	/**
	 * @param listContratoCobrancaFavorecidos the listContratoCobrancaFavorecidos to
	 *                                        set
	 */
	public void setListContratoCobrancaFavorecidos(List<ContratoCobrancaFavorecidos> listContratoCobrancaFavorecidos) {
		this.listContratoCobrancaFavorecidos = listContratoCobrancaFavorecidos;
	}

	/**
	 * @return the tipoPessoaIsFisica
	 */
	public boolean isTipoPessoaIsFisica() {
		return tipoPessoaIsFisica;
	}

	/**
	 * @param tipoPessoaIsFisica the tipoPessoaIsFisica to set
	 */
	public void setTipoPessoaIsFisica(boolean tipoPessoaIsFisica) {
		this.tipoPessoaIsFisica = tipoPessoaIsFisica;
	}

	public boolean validaCPF(FacesContext facesContext, UIComponent uiComponent, Object object) {
		return ValidaCPF.isCPF(object.toString());
	}

	public boolean validaCPFPopulaCC(FacesContext facesContext, UIComponent uiComponent, Object object) {

		boolean retorno = ValidaCPF.isCPF(object.toString());

		if (retorno) {
			this.objetoPagadorRecebedor.setCpfCC(object.toString());
		}

		return retorno;
	}

	public boolean validaCNPJ(FacesContext facesContext, UIComponent uiComponent, Object object) {
		return ValidaCNPJ.isCNPJ(object.toString());
	}

	public void selectedTipoPessoa() {
		if (this.tipoPessoaIsFisica) {
			this.listContratoCobrancaFavorecidos.get(0).setCnpj(null);
			this.listContratoCobrancaFavorecidos.get(0).setNome(null);
		} else {
			this.listContratoCobrancaFavorecidos.get(0).setCpf(null);
			this.listContratoCobrancaFavorecidos.get(0).setNome(null);
		}
	}

	public void selectedTipoPessoaPublico() {
		if (this.tipoPessoaIsFisica) {
			this.objetoPagadorRecebedor.setCnpj(null);
			this.objetoPagadorRecebedor.setNome(null);

			this.objetoPagadorRecebedor.setCnpjCC(null);
			this.objetoPagadorRecebedor.setNomeCC(null);
		} else {
			this.objetoPagadorRecebedor.setCpf(null);
			this.objetoPagadorRecebedor.setRg(null);
			this.objetoPagadorRecebedor.setNome(null);

			this.objetoPagadorRecebedor.setCpfCC(null);
			this.objetoPagadorRecebedor.setNomeCC(null);
		}

		this.tipoPessoaIsFisicaCC = this.tipoPessoaIsFisica;
	}

	public void selectedTipoPessoaCC() {
		if (this.tipoPessoaIsFisica) {
			this.objetoPagadorRecebedor.setCnpjCC(null);
			this.objetoPagadorRecebedor.setNomeCC(null);
		} else {
			this.objetoPagadorRecebedor.setCpfCC(null);
			this.objetoPagadorRecebedor.setNomeCC(null);
		}
	}

	/**
	 * @return the vlrRepasse
	 */
	public BigDecimal getVlrRepasse() {
		return vlrRepasse;
	}

	/**
	 * @param vlrRepasse the vlrRepasse to set
	 */
	public void setVlrRepasse(BigDecimal vlrRepasse) {
		this.vlrRepasse = vlrRepasse;
	}

	/**
	 * @return the vlrRetencao
	 */
	public BigDecimal getVlrRetencao() {
		return vlrRetencao;
	}

	/**
	 * @param vlrRetencao the vlrRetencao to set
	 */
	public void setVlrRetencao(BigDecimal vlrRetencao) {
		this.vlrRetencao = vlrRetencao;
	}

	/**
	 * @return the vlrRepasseFinal
	 */
	public BigDecimal getVlrRepasseFinal() {
		return vlrRepasseFinal;
	}

	/**
	 * @param vlrRepasseFinal the vlrRepasseFinal to set
	 */
	public void setVlrRepasseFinal(BigDecimal vlrRepasseFinal) {
		this.vlrRepasseFinal = vlrRepasseFinal;
	}

	/**
	 * @return the vlrRetencaoFinal
	 */
	public BigDecimal getVlrRetencaoFinal() {
		return vlrRetencaoFinal;
	}

	/**
	 * @param vlrRetencaoFinal the vlrRetencaoFinal to set
	 */
	public void setVlrRetencaoFinal(BigDecimal vlrRetencaoFinal) {
		this.vlrRetencaoFinal = vlrRetencaoFinal;
	}

	/**
	 * @return the vlrParcelaFinal
	 */
	public BigDecimal getVlrParcelaFinal() {
		return vlrParcelaFinal;
	}

	/**
	 * @param vlrParcelaFinal the vlrParcelaFinal to set
	 */
	public void setVlrParcelaFinal(BigDecimal vlrParcelaFinal) {
		this.vlrParcelaFinal = vlrParcelaFinal;
	}

	/**
	 * @return the file
	 */
	public StreamedContent getFile() {
		String caminho = this.pathContrato + this.nomeContrato;
		String arquivo = this.nomeContrato;
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

	/**
	 * @param file the file to set
	 */
	public void setFile(StreamedContent file) {
		this.file = file;
	}

	/**
	 * @return the vlrComissao
	 */
	public BigDecimal getVlrComissao() {
		return vlrComissao;
	}

	/**
	 * @param vlrComissao the vlrComissao to set
	 */
	public void setVlrComissao(BigDecimal vlrComissao) {
		this.vlrComissao = vlrComissao;
	}

	/**
	 * @return the vlrComissaoFinal
	 */
	public BigDecimal getVlrComissaoFinal() {
		return vlrComissaoFinal;
	}

	/**
	 * @param vlrComissaoFinal the vlrComissaoFinal to set
	 */
	public void setVlrComissaoFinal(BigDecimal vlrComissaoFinal) {
		this.vlrComissaoFinal = vlrComissaoFinal;
	}

	/**
	 * @return the selectedListContratoCobrancaDetalhes
	 */
	public List<ContratoCobrancaDetalhes> getSelectedListContratoCobrancaDetalhes() {
		return selectedListContratoCobrancaDetalhes;
	}

	/**
	 * @param selectedListContratoCobrancaDetalhes the
	 *                                             selectedListContratoCobrancaDetalhes
	 *                                             to set
	 */
	public void setSelectedListContratoCobrancaDetalhes(
			List<ContratoCobrancaDetalhes> selectedListContratoCobrancaDetalhes) {
		this.selectedListContratoCobrancaDetalhes = selectedListContratoCobrancaDetalhes;
	}

	/**
	 * @return the renderRecebedor2
	 */
	public boolean isRenderRecebedor2() {
		return renderRecebedor2;
	}

	/**
	 * @param renderRecebedor2 the renderRecebedor2 to set
	 */
	public void setRenderRecebedor2(boolean renderRecebedor2) {
		this.renderRecebedor2 = renderRecebedor2;
	}

	/**
	 * @return the renderRecebedor3
	 */
	public boolean isRenderRecebedor3() {
		return renderRecebedor3;
	}

	/**
	 * @param renderRecebedor3 the renderRecebedor3 to set
	 */
	public void setRenderRecebedor3(boolean renderRecebedor3) {
		this.renderRecebedor3 = renderRecebedor3;
	}

	/**
	 * @return the renderRecebedor4
	 */
	public boolean isRenderRecebedor4() {
		return renderRecebedor4;
	}

	/**
	 * @param renderRecebedor4 the renderRecebedor4 to set
	 */
	public void setRenderRecebedor4(boolean renderRecebedor4) {
		this.renderRecebedor4 = renderRecebedor4;
	}

	/**
	 * @return the renderRecebedor5
	 */
	public boolean isRenderRecebedor5() {
		return renderRecebedor5;
	}

	/**
	 * @param renderRecebedor5 the renderRecebedor5 to set
	 */
	public void setRenderRecebedor5(boolean renderRecebedor5) {
		this.renderRecebedor5 = renderRecebedor5;
	}

	/**
	 * @return the renderRecebedor6
	 */
	public boolean isRenderRecebedor6() {
		return renderRecebedor6;
	}

	/**
	 * @param renderRecebedor6 the renderRecebedor6 to set
	 */
	public void setRenderRecebedor6(boolean renderRecebedor6) {
		this.renderRecebedor6 = renderRecebedor6;
	}

	/**
	 * @return the renderRecebedor7
	 */
	public boolean isRenderRecebedor7() {
		return renderRecebedor7;
	}

	/**
	 * @param renderRecebedor7 the renderRecebedor7 to set
	 */
	public void setRenderRecebedor7(boolean renderRecebedor7) {
		this.renderRecebedor7 = renderRecebedor7;
	}

	/**
	 * @return the renderRecebedor8
	 */
	public boolean isRenderRecebedor8() {
		return renderRecebedor8;
	}

	/**
	 * @param renderRecebedor8 the renderRecebedor8 to set
	 */
	public void setRenderRecebedor8(boolean renderRecebedor8) {
		this.renderRecebedor8 = renderRecebedor8;
	}

	/**
	 * @return the renderRecebedor9
	 */
	public boolean isRenderRecebedor9() {
		return renderRecebedor9;
	}

	/**
	 * @param renderRecebedor9 the renderRecebedor9 to set
	 */
	public void setRenderRecebedor9(boolean renderRecebedor9) {
		this.renderRecebedor9 = renderRecebedor9;
	}

	/**
	 * @return the renderRecebedor10
	 */
	public boolean isRenderRecebedor10() {
		return renderRecebedor10;
	}

	/**
	 * @param renderRecebedor10 the renderRecebedor10 to set
	 */
	public void setRenderRecebedor10(boolean renderRecebedor10) {
		this.renderRecebedor10 = renderRecebedor10;
	}

	/**
	 * @return the nomeRecebedor2
	 */
	public String getNomeRecebedor2() {
		return nomeRecebedor2;
	}

	/**
	 * @param nomeRecebedor2 the nomeRecebedor2 to set
	 */
	public void setNomeRecebedor2(String nomeRecebedor2) {
		this.nomeRecebedor2 = nomeRecebedor2;
	}

	/**
	 * @return the idRecebedor2
	 */
	public long getIdRecebedor2() {
		return idRecebedor2;
	}

	/**
	 * @param idRecebedor2 the idRecebedor2 to set
	 */
	public void setIdRecebedor2(long idRecebedor2) {
		this.idRecebedor2 = idRecebedor2;
	}

	/**
	 * @return the nomeRecebedor3
	 */
	public String getNomeRecebedor3() {
		return nomeRecebedor3;
	}

	/**
	 * @param nomeRecebedor3 the nomeRecebedor3 to set
	 */
	public void setNomeRecebedor3(String nomeRecebedor3) {
		this.nomeRecebedor3 = nomeRecebedor3;
	}

	/**
	 * @return the idRecebedor3
	 */
	public long getIdRecebedor3() {
		return idRecebedor3;
	}

	/**
	 * @param idRecebedor3 the idRecebedor3 to set
	 */
	public void setIdRecebedor3(long idRecebedor3) {
		this.idRecebedor3 = idRecebedor3;
	}

	/**
	 * @return the nomeRecebedor4
	 */
	public String getNomeRecebedor4() {
		return nomeRecebedor4;
	}

	/**
	 * @param nomeRecebedor4 the nomeRecebedor4 to set
	 */
	public void setNomeRecebedor4(String nomeRecebedor4) {
		this.nomeRecebedor4 = nomeRecebedor4;
	}

	/**
	 * @return the idRecebedor4
	 */
	public long getIdRecebedor4() {
		return idRecebedor4;
	}

	/**
	 * @param idRecebedor4 the idRecebedor4 to set
	 */
	public void setIdRecebedor4(long idRecebedor4) {
		this.idRecebedor4 = idRecebedor4;
	}

	/**
	 * @return the nomeRecebedor5
	 */
	public String getNomeRecebedor5() {
		return nomeRecebedor5;
	}

	/**
	 * @param nomeRecebedor5 the nomeRecebedor5 to set
	 */
	public void setNomeRecebedor5(String nomeRecebedor5) {
		this.nomeRecebedor5 = nomeRecebedor5;
	}

	/**
	 * @return the idRecebedor5
	 */
	public long getIdRecebedor5() {
		return idRecebedor5;
	}

	/**
	 * @param idRecebedor5 the idRecebedor5 to set
	 */
	public void setIdRecebedor5(long idRecebedor5) {
		this.idRecebedor5 = idRecebedor5;
	}

	/**
	 * @return the nomeRecebedor6
	 */
	public String getNomeRecebedor6() {
		return nomeRecebedor6;
	}

	/**
	 * @param nomeRecebedor6 the nomeRecebedor6 to set
	 */
	public void setNomeRecebedor6(String nomeRecebedor6) {
		this.nomeRecebedor6 = nomeRecebedor6;
	}

	/**
	 * @return the idRecebedor6
	 */
	public long getIdRecebedor6() {
		return idRecebedor6;
	}

	/**
	 * @param idRecebedor6 the idRecebedor6 to set
	 */
	public void setIdRecebedor6(long idRecebedor6) {
		this.idRecebedor6 = idRecebedor6;
	}

	/**
	 * @return the nomeRecebedor7
	 */
	public String getNomeRecebedor7() {
		return nomeRecebedor7;
	}

	/**
	 * @param nomeRecebedor7 the nomeRecebedor7 to set
	 */
	public void setNomeRecebedor7(String nomeRecebedor7) {
		this.nomeRecebedor7 = nomeRecebedor7;
	}

	/**
	 * @return the idRecebedor7
	 */
	public long getIdRecebedor7() {
		return idRecebedor7;
	}

	/**
	 * @param idRecebedor7 the idRecebedor7 to set
	 */
	public void setIdRecebedor7(long idRecebedor7) {
		this.idRecebedor7 = idRecebedor7;
	}

	/**
	 * @return the nomeRecebedor8
	 */
	public String getNomeRecebedor8() {
		return nomeRecebedor8;
	}

	/**
	 * @param nomeRecebedor8 the nomeRecebedor8 to set
	 */
	public void setNomeRecebedor8(String nomeRecebedor8) {
		this.nomeRecebedor8 = nomeRecebedor8;
	}

	/**
	 * @return the idRecebedor8
	 */
	public long getIdRecebedor8() {
		return idRecebedor8;
	}

	/**
	 * @param idRecebedor8 the idRecebedor8 to set
	 */
	public void setIdRecebedor8(long idRecebedor8) {
		this.idRecebedor8 = idRecebedor8;
	}

	/**
	 * @return the nomeRecebedor9
	 */
	public String getNomeRecebedor9() {
		return nomeRecebedor9;
	}

	/**
	 * @param nomeRecebedor9 the nomeRecebedor9 to set
	 */
	public void setNomeRecebedor9(String nomeRecebedor9) {
		this.nomeRecebedor9 = nomeRecebedor9;
	}

	/**
	 * @return the idRecebedor9
	 */
	public long getIdRecebedor9() {
		return idRecebedor9;
	}

	/**
	 * @param idRecebedor9 the idRecebedor9 to set
	 */
	public void setIdRecebedor9(long idRecebedor9) {
		this.idRecebedor9 = idRecebedor9;
	}

	/**
	 * @return the nomeRecebedor10
	 */
	public String getNomeRecebedor10() {
		return nomeRecebedor10;
	}

	/**
	 * @param nomeRecebedor10 the nomeRecebedor10 to set
	 */
	public void setNomeRecebedor10(String nomeRecebedor10) {
		this.nomeRecebedor10 = nomeRecebedor10;
	}

	/**
	 * @return the idRecebedor10
	 */
	public long getIdRecebedor10() {
		return idRecebedor10;
	}

	/**
	 * @param idRecebedor10 the idRecebedor10 to set
	 */
	public void setIdRecebedor10(long idRecebedor10) {
		this.idRecebedor10 = idRecebedor10;
	}

	/**
	 * @return the nomeFavorecido
	 */
	public String getNomeFavorecido() {
		return nomeFavorecido;
	}

	/**
	 * @param nomeFavorecido the nomeFavorecido to set
	 */
	public void setNomeFavorecido(String nomeFavorecido) {
		this.nomeFavorecido = nomeFavorecido;
	}

	/**
	 * @return the nomeContrato
	 */
	public String getNomeContrato() {
		return nomeContrato;
	}

	/**
	 * @param nomeContrato the nomeContrato to set
	 */
	public void setNomeContrato(String nomeContrato) {
		this.nomeContrato = nomeContrato;
	}

	/**
	 * @return the selectedRecebedor2
	 */
	public PagadorRecebedor getSelectedRecebedor2() {
		return selectedRecebedor2;
	}

	/**
	 * @param selectedRecebedor2 the selectedRecebedor2 to set
	 */
	public void setSelectedRecebedor2(PagadorRecebedor selectedRecebedor2) {
		this.selectedRecebedor2 = selectedRecebedor2;
	}

	/**
	 * @return the selectedRecebedor3
	 */
	public PagadorRecebedor getSelectedRecebedor3() {
		return selectedRecebedor3;
	}

	/**
	 * @param selectedRecebedor3 the selectedRecebedor3 to set
	 */
	public void setSelectedRecebedor3(PagadorRecebedor selectedRecebedor3) {
		this.selectedRecebedor3 = selectedRecebedor3;
	}

	/**
	 * @return the selectedRecebedor4
	 */
	public PagadorRecebedor getSelectedRecebedor4() {
		return selectedRecebedor4;
	}

	/**
	 * @param selectedRecebedor4 the selectedRecebedor4 to set
	 */
	public void setSelectedRecebedor4(PagadorRecebedor selectedRecebedor4) {
		this.selectedRecebedor4 = selectedRecebedor4;
	}

	/**
	 * @return the selectedRecebedor5
	 */
	public PagadorRecebedor getSelectedRecebedor5() {
		return selectedRecebedor5;
	}

	/**
	 * @param selectedRecebedor5 the selectedRecebedor5 to set
	 */
	public void setSelectedRecebedor5(PagadorRecebedor selectedRecebedor5) {
		this.selectedRecebedor5 = selectedRecebedor5;
	}

	/**
	 * @return the selectedRecebedor6
	 */
	public PagadorRecebedor getSelectedRecebedor6() {
		return selectedRecebedor6;
	}

	/**
	 * @param selectedRecebedor6 the selectedRecebedor6 to set
	 */
	public void setSelectedRecebedor6(PagadorRecebedor selectedRecebedor6) {
		this.selectedRecebedor6 = selectedRecebedor6;
	}

	/**
	 * @return the selectedRecebedor7
	 */
	public PagadorRecebedor getSelectedRecebedor7() {
		return selectedRecebedor7;
	}

	/**
	 * @param selectedRecebedor7 the selectedRecebedor7 to set
	 */
	public void setSelectedRecebedor7(PagadorRecebedor selectedRecebedor7) {
		this.selectedRecebedor7 = selectedRecebedor7;
	}

	/**
	 * @return the selectedRecebedor8
	 */
	public PagadorRecebedor getSelectedRecebedor8() {
		return selectedRecebedor8;
	}

	/**
	 * @param selectedRecebedor8 the selectedRecebedor8 to set
	 */
	public void setSelectedRecebedor8(PagadorRecebedor selectedRecebedor8) {
		this.selectedRecebedor8 = selectedRecebedor8;
	}

	/**
	 * @return the selectedRecebedor9
	 */
	public PagadorRecebedor getSelectedRecebedor9() {
		return selectedRecebedor9;
	}

	/**
	 * @param selectedRecebedor9 the selectedRecebedor9 to set
	 */
	public void setSelectedRecebedor9(PagadorRecebedor selectedRecebedor9) {
		this.selectedRecebedor9 = selectedRecebedor9;
	}

	/**
	 * @return the selectedRecebedor10
	 */
	public PagadorRecebedor getSelectedRecebedor10() {
		return selectedRecebedor10;
	}

	/**
	 * @param selectedRecebedor10 the selectedRecebedor10 to set
	 */
	public void setSelectedRecebedor10(PagadorRecebedor selectedRecebedor10) {
		this.selectedRecebedor10 = selectedRecebedor10;
	}

	/**
	 * @return the relIsCompleto
	 */
	public boolean isRelIsCompleto() {
		return relIsCompleto;
	}

	/**
	 * @param relIsCompleto the relIsCompleto to set
	 */
	public void setRelIsCompleto(boolean relIsCompleto) {
		this.relIsCompleto = relIsCompleto;
	}

	/**
	 * @return the numContrato
	 */
	public String getNumContrato() {
		return numContrato;
	}

	/**
	 * @param numContrato the numContrato to set
	 */
	public void setNumContrato(String numContrato) {
		this.numContrato = numContrato;
	}

	/**
	 * @return the tipoFiltros
	 */
	public boolean isTipoFiltros() {
		return tipoFiltros;
	}

	/**
	 * @param tipoFiltros the tipoFiltros to set
	 */
	public void setTipoFiltros(boolean tipoFiltros) {
		this.tipoFiltros = tipoFiltros;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	/**
	 * @return the exibeSomenteFavorecidosFiltrados
	 */
	public String getExibeSomenteFavorecidosFiltrados() {
		return exibeSomenteFavorecidosFiltrados;
	}

	/**
	 * @param exibeSomenteFavorecidosFiltrados the exibeSomenteFavorecidosFiltrados
	 *                                         to set
	 */
	public void setExibeSomenteFavorecidosFiltrados(String exibeSomenteFavorecidosFiltrados) {
		this.exibeSomenteFavorecidosFiltrados = exibeSomenteFavorecidosFiltrados;
	}

	/**
	 * @return the filtroRelBaixado
	 */
	public String getFiltroRelBaixado() {
		return filtroRelBaixado;
	}

	/**
	 * @param filtroRelBaixado the filtroRelBaixado to set
	 */
	public void setFiltroRelBaixado(String filtroRelBaixado) {
		this.filtroRelBaixado = filtroRelBaixado;
	}

	/**
	 * @return the contratosPendentes
	 */
	public Collection<ContratoCobranca> getContratosPendentes() {
		return contratosPendentes;
	}

	/**
	 * @param contratosPendentes the contratosPendentes to set
	 */
	public void setContratosPendentes(Collection<ContratoCobranca> contratosPendentes) {
		this.contratosPendentes = contratosPendentes;
	}

	/**
	 * @return the codigoResponsavel
	 */
	public String getCodigoResponsavel() {
		return codigoResponsavel;
	}

	/**
	 * @param codigoResponsavel the codigoResponsavel to set
	 */
	public void setCodigoResponsavel(String codigoResponsavel) {
		this.codigoResponsavel = codigoResponsavel;
	}

	/**
	 * @return the pagadorRecebedorMB
	 */
	public PagadorRecebedorMB getPagadorRecebedorMB() {
		return pagadorRecebedorMB;
	}

	/**
	 * @param pagadorRecebedorMB the pagadorRecebedorMB to set
	 */
	public void setPagadorRecebedorMB(PagadorRecebedorMB pagadorRecebedorMB) {
		this.pagadorRecebedorMB = pagadorRecebedorMB;
	}

	/**
	 * @return the imovelCobrancaMB
	 */
	public ImovelCobrancaMB getImovelCobrancaMB() {
		return imovelCobrancaMB;
	}

	/**
	 * @param imovelCobrancaMB the imovelCobrancaMB to set
	 */
	public void setImovelCobrancaMB(ImovelCobrancaMB imovelCobrancaMB) {
		this.imovelCobrancaMB = imovelCobrancaMB;
	}

	/**
	 * @return the objetoImovelCobranca
	 */
	public ImovelCobranca getObjetoImovelCobranca() {
		return objetoImovelCobranca;
	}

	/**
	 * @param objetoImovelCobranca the objetoImovelCobranca to set
	 */
	public void setObjetoImovelCobranca(ImovelCobranca objetoImovelCobranca) {
		this.objetoImovelCobranca = objetoImovelCobranca;
	}

	/**
	 * @return the objetoPagadorRecebedor
	 */
	public PagadorRecebedor getObjetoPagadorRecebedor() {
		return objetoPagadorRecebedor;
	}

	/**
	 * @param objetoPagadorRecebedor the objetoPagadorRecebedor to set
	 */
	public void setObjetoPagadorRecebedor(PagadorRecebedor objetoPagadorRecebedor) {
		this.objetoPagadorRecebedor = objetoPagadorRecebedor;
	}

	/**
	 * @return the vlrTotal
	 */
	public String getVlrTotal() {
		return vlrTotal;
	}

	/**
	 * @param vlrTotal the vlrTotal to set
	 */
	public void setVlrTotal(String vlrTotal) {
		this.vlrTotal = vlrTotal;
	}

	/**
	 * @return the vlrParcelas
	 */
	public String getVlrParcelas() {
		return vlrParcelas;
	}

	/**
	 * @param vlrParcelas the vlrParcelas to set
	 */
	public void setVlrParcelas(String vlrParcelas) {
		this.vlrParcelas = vlrParcelas;
	}

	/**
	 * @return the grupoFavorecidos
	 */
	public boolean isGrupoFavorecidos() {
		return grupoFavorecidos;
	}

	/**
	 * @param grupoFavorecidos the grupoFavorecidos to set
	 */
	public void setGrupoFavorecidos(boolean grupoFavorecidos) {
		this.grupoFavorecidos = grupoFavorecidos;
	}

	/**
	 * @return the rowEditNewDate
	 */
	public Date getRowEditNewDate() {
		return rowEditNewDate;
	}

	/**
	 * @param rowEditNewDate the rowEditNewDate to set
	 */
	public void setRowEditNewDate(Date rowEditNewDate) {
		this.rowEditNewDate = rowEditNewDate;
	}

	/**
	 * @return the bpContratoCobrancaDetalhes
	 */
	public ContratoCobrancaDetalhes getBpContratoCobrancaDetalhes() {
		return bpContratoCobrancaDetalhes;
	}

	/**
	 * @param bpContratoCobrancaDetalhes the bpContratoCobrancaDetalhes to set
	 */
	public void setBpContratoCobrancaDetalhes(ContratoCobrancaDetalhes bpContratoCobrancaDetalhes) {
		this.bpContratoCobrancaDetalhes = bpContratoCobrancaDetalhes;
	}

	/**
	 * @return the vlrParcelaAtualizada
	 */
	public String getVlrParcelaAtualizada() {
		return vlrParcelaAtualizada;
	}

	/**
	 * @param vlrParcelaAtualizada the vlrParcelaAtualizada to set
	 */
	public void setVlrParcelaAtualizada(String vlrParcelaAtualizada) {
		this.vlrParcelaAtualizada = vlrParcelaAtualizada;
	}

	/**
	 * @return the vlrRecebido
	 */
	public BigDecimal getVlrRecebido() {
		return vlrRecebido;
	}

	/**
	 * @param vlrRecebido the vlrRecebido to set
	 */
	public void setVlrRecebido(BigDecimal vlrRecebido) {
		this.vlrRecebido = vlrRecebido;
	}

	/**
	 * @return the vlrParcelaNew
	 */
	public BigDecimal getVlrParcelaNew() {
		return vlrParcelaNew;
	}

	/**
	 * @param vlrParcelaNew the vlrParcelaNew to set
	 */
	public void setVlrParcelaNew(BigDecimal vlrParcelaNew) {
		this.vlrParcelaNew = vlrParcelaNew;
	}

	/**
	 * @return the vlrParcelaAtualizadaNew
	 */
	public BigDecimal getVlrParcelaAtualizadaNew() {
		return vlrParcelaAtualizadaNew;
	}

	/**
	 * @param vlrParcelaAtualizadaNew the vlrParcelaAtualizadaNew to set
	 */
	public void setVlrParcelaAtualizadaNew(BigDecimal vlrParcelaAtualizadaNew) {
		this.vlrParcelaAtualizadaNew = vlrParcelaAtualizadaNew;
	}

	/**
	 * @return the selectedGrupoFavorecido
	 */
	public GruposFavorecidos getSelectedGrupoFavorecido() {
		return selectedGrupoFavorecido;
	}

	/**
	 * @param selectedGrupoFavorecido the selectedGrupoFavorecido to set
	 */
	public void setSelectedGrupoFavorecido(GruposFavorecidos selectedGrupoFavorecido) {
		this.selectedGrupoFavorecido = selectedGrupoFavorecido;
	}

	/**
	 * @return the listGrupoFavorecido
	 */
	public List<GruposFavorecidos> getListGrupoFavorecido() {
		return listGrupoFavorecido;
	}

	/**
	 * @param listGrupoFavorecido the listGrupoFavorecido to set
	 */
	public void setListGrupoFavorecido(List<GruposFavorecidos> listGrupoFavorecido) {
		this.listGrupoFavorecido = listGrupoFavorecido;
	}

	/**
	 * @return the nomeGrupoFavorecido
	 */
	public String getNomeGrupoFavorecido() {
		return nomeGrupoFavorecido;
	}

	/**
	 * @param nomeGrupoFavorecido the nomeGrupoFavorecido to set
	 */
	public void setNomeGrupoFavorecido(String nomeGrupoFavorecido) {
		this.nomeGrupoFavorecido = nomeGrupoFavorecido;
	}

	/**
	 * @return the idGrupoFavorecido
	 */
	public long getIdGrupoFavorecido() {
		return idGrupoFavorecido;
	}

	/**
	 * @param idGrupoFavorecido the idGrupoFavorecido to set
	 */
	public void setIdGrupoFavorecido(long idGrupoFavorecido) {
		this.idGrupoFavorecido = idGrupoFavorecido;
	}

	/**
	 * @return the relIsParcial
	 */
	public boolean isRelIsParcial() {
		return relIsParcial;
	}

	/**
	 * @param relIsParcial the relIsParcial to set
	 */
	public void setRelIsParcial(boolean relIsParcial) {
		this.relIsParcial = relIsParcial;
	}

	/**
	 * @return the relParcial
	 */
	public String getRelParcial() {
		return relParcial;
	}

	/**
	 * @param relParcial the relParcial to set
	 */
	public void setRelParcial(String relParcial) {
		this.relParcial = relParcial;
	}

	/**
	 * @return the geraBoletoInclusaoContrato
	 */
	public boolean isGeraBoletoInclusaoContrato() {
		return geraBoletoInclusaoContrato;
	}

	/**
	 * @param geraBoletoInclusaoContrato the geraBoletoInclusaoContrato to set
	 */
	public void setGeraBoletoInclusaoContrato(boolean geraBoletoInclusaoContrato) {
		this.geraBoletoInclusaoContrato = geraBoletoInclusaoContrato;
	}

	/**
	 * @return the fileBoleto
	 */
	public StreamedContent getFileBoleto() {
		return fileBoleto;
	}

	/**
	 * @param fileBoleto the fileBoleto to set
	 */
	public void setFileBoleto(StreamedContent fileBoleto) {
		this.fileBoleto = fileBoleto;
	}

	/**
	 * @return the dataVencimentoBoleto
	 */
	public Date getDataVencimentoBoleto() {
		if (dataVencimentoBoleto == null) {			
			this.dataVencimentoBoleto = getDataHoje();
		}
		return dataVencimentoBoleto;
	}

	/**
	 * @param dataVencimentoBoleto the dataVencimentoBoleto to set
	 */
	public void setDataVencimentoBoleto(Date dataVencimentoBoleto) {
		this.dataVencimentoBoleto = dataVencimentoBoleto;
	}

	/**
	 * @return the valorBoleto
	 */
	public BigDecimal getValorBoleto() {
		return valorBoleto;
	}

	/**
	 * @param valorBoleto the valorBoleto to set
	 */
	public void setValorBoleto(BigDecimal valorBoleto) {
		this.valorBoleto = valorBoleto;
	}

	/**
	 * @return the observacao
	 */
	public String getObservacao() {
		return observacao;
	}

	/**
	 * @return the filtrarDataVencimento
	 */
	public String getFiltrarDataVencimento() {
		return filtrarDataVencimento;
	}

	/**
	 * @param filtrarDataVencimento the filtrarDataVencimento to set
	 */
	public void setFiltrarDataVencimento(String filtrarDataVencimento) {
		this.filtrarDataVencimento = filtrarDataVencimento;
	}

	/**
	 * @param observacao the observacao to set
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	/**
	 * @return the txZero
	 */
	public boolean isTxZero() {
		return txZero;
	}

	/**
	 * @param txZero the txZero to set
	 */
	public void setTxZero(boolean txZero) {
		this.txZero = txZero;
	}

	/***************************************
	 * Tratamento de Upload de Arquivos
	 **************************************/

	// Lista os arquivos contidos no diretório
	Collection<FileUploaded> files = new ArrayList<FileUploaded>();
	// armazena arquivo selecionado a ser excluido
	FileUploaded selectedFile = new FileUploaded();
	List<FileUploaded> deletefiles = new ArrayList<FileUploaded>();
	StreamedContent downloadFile;
	StreamedContent downloadAllFiles;
	
	
	Collection<FileUploaded> filesInterno = new ArrayList<FileUploaded>();
	
	
	List<FileUploaded> deletefilesInterno = new ArrayList<FileUploaded>();
	
	StreamedContent downloadAllFilesInterno;

	/***
	 * handler de upload do arquivo
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void handleFileUpload(FileUploadEvent event) throws IOException {
		// recupera local onde será gravado o arquivo
		ParametrosDao pDao = new ParametrosDao();
		String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
				+ this.objetoContratoCobranca.getNumeroContrato() + "/";

		// cria o diretório, caso não exista
		File diretorio = new File(pathContrato);
		if (!diretorio.isDirectory()) {
			diretorio.mkdir();
		}

		// cria o arquivo
		byte[] conteudo = event.getFile().getContents();
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(pathContrato + event.getFile().getFileName());
			fos.write(conteudo);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		}

		// atualiza lista de arquivos contidos no diretório
		files = listaArquivos();
	}
	
	public void handleFileInternoUpload(FileUploadEvent event) throws IOException {
		// recupera local onde será gravado o arquivo
		ParametrosDao pDao = new ParametrosDao();
		String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
				+ this.objetoContratoCobranca.getNumeroContrato() + "//interno/";

		// cria o diretório, caso não exista
		File diretorio = new File(pathContrato);
		if (!diretorio.isDirectory()) {
			diretorio.mkdir();
		}

		// cria o arquivo
		byte[] conteudo = event.getFile().getContents();
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(pathContrato + event.getFile().getFileName());
			fos.write(conteudo);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		}

		// atualiza lista de arquivos contidos no diretório
		filesInterno = listaArquivosInterno();
	}

	/**
	 * deleta o arquivo selecionado na tela
	 */
	public void deleteFile() {
		for (FileUploaded f : deletefiles) {
			f.getFile().delete();
		}

		deletefiles = new ArrayList<FileUploaded>();
		files = listaArquivos();
	}
	
	public void deleteFileInterno() {
		for (FileUploaded f : deletefilesInterno) {
			f.getFile().delete();
		}

		deletefilesInterno = new ArrayList<FileUploaded>();
		filesInterno = listaArquivos();
	}

	public void deleteFiles(Collection<FileUploaded> lista) {
		for (FileUploaded f : lista) {
			f.getFile().delete();
		}
	}
	
	public void deleteFilesInterno(Collection<FileUploaded> lista) {
		for (FileUploaded f : lista) {
			f.getFile().delete();
		}
	}

	/***
	 * Lista ois arquivos contidos no diretório
	 * 
	 * @return
	 */
	public Collection<FileUploaded> listaArquivos() {
		// DateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");
		ParametrosDao pDao = new ParametrosDao();
		String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
				+ this.objetoContratoCobranca.getNumeroContrato() + "/";
		File diretorio = new File(pathContrato);
		File arqs[] = diretorio.listFiles();
		Collection<FileUploaded> lista = new ArrayList<FileUploaded>();
		if (arqs != null) {
			for (int i = 0; i < arqs.length; i++) {
				File arquivo = arqs[i];

				// String nome = arquivo.getName();
				// String dt_ateracao = formatData.format(new Date(arquivo.lastModified()));
				
				if(!arquivo.getName().contains("interno")) {
					lista.add(new FileUploaded(arquivo.getName(), arquivo, pathContrato));
				}
			}
		}
		return lista;
	}
	
	public Collection<FileUploaded> listaArquivosInterno() {
		// DateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");
		ParametrosDao pDao = new ParametrosDao();
		String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
				+ this.objetoContratoCobranca.getNumeroContrato() + "//interno/";
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

	/**
	 * Método para fazer download de todos os arquivos do diretório do contrato
	 * 
	 * @return
	 */
	public StreamedContent getDownloadAllFiles() {
		try {
			// recupera path do contrato
			ParametrosDao pDao = new ParametrosDao();
			String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString();

			// cria objetos para ZIP
			ZipOutputStream zip = null;
			FileOutputStream fileWriter = null;

			// cria arquivo ZIP
			fileWriter = new FileOutputStream(
					pathContrato + "Documentos_" + this.objetoContratoCobranca.getNumeroContrato() + ".zip");
			zip = new ZipOutputStream(fileWriter);

			// Percorre arquivos selecionados e adiciona ao ZIP
			for (FileUploaded f : deletefiles) {
				addFileToZip("", f.getFile().getAbsolutePath(), zip);
			}

			// Fecha o ZIP
			zip.flush();
			zip.close();

			// Recupera ZIP gerado para fazer download
			FileInputStream stream = new FileInputStream(
					pathContrato + "Documentos_" + this.objetoContratoCobranca.getNumeroContrato() + ".zip");
			downloadAllFiles = new DefaultStreamedContent(stream, pathContrato,
					"Documentos_" + this.objetoContratoCobranca.getNumeroContrato() + ".zip");

		} catch (Exception e) {
			System.out.println(e);
		}

		return this.downloadAllFiles;
	}
	
	public StreamedContent getDownloadAllFilesInterno() {
		try {
			// recupera path do contrato
			ParametrosDao pDao = new ParametrosDao();
			String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString()
					+ this.objetoContratoCobranca.getNumeroContrato() + "//interno/";
			// cria objetos para ZIP
			ZipOutputStream zip = null;
			FileOutputStream fileWriter = null;

			// cria arquivo ZIP
			fileWriter = new FileOutputStream(
					pathContrato + "Documentos_" + this.objetoContratoCobranca.getNumeroContrato() + ".zip");
			zip = new ZipOutputStream(fileWriter);

			// Percorre arquivos selecionados e adiciona ao ZIP
			for (FileUploaded f : deletefilesInterno) {
				addFileToZip("", f.getFile().getAbsolutePath(), zip);
			}

			// Fecha o ZIP
			zip.flush();
			zip.close();

			// Recupera ZIP gerado para fazer download
			FileInputStream stream = new FileInputStream(
					pathContrato + "Documentos_" + this.objetoContratoCobranca.getNumeroContrato() + ".zip");
			downloadAllFiles = new DefaultStreamedContent(stream, pathContrato,
					"Documentos_" + this.objetoContratoCobranca.getNumeroContrato() + ".zip");

		} catch (Exception e) {
			System.out.println(e);
		}

		return this.downloadAllFilesInterno;
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

	/**
	 * @return the files
	 */
	public Collection<FileUploaded> getFiles() {
		return files;
	}

	/**
	 * @param files the files to set
	 */
	public void setFiles(Collection<FileUploaded> files) {
		this.files = files;
	}

	/**
	 * @return the selectedFile
	 */
	public FileUploaded getSelectedFile() {
		return selectedFile;
	}

	/**
	 * @param selectedFile the selectedFile to set
	 */
	public void setSelectedFile(FileUploaded selectedFile) {
		this.selectedFile = selectedFile;
	}

	/**
	 * @return the deletefiles
	 */
	public List<FileUploaded> getDeletefiles() {
		return deletefiles;
	}

	/**
	 * @param deletefiles the deletefiles to set
	 */
	public void setDeletefiles(List<FileUploaded> deletefiles) {
		this.deletefiles = deletefiles;
	}

	/**
	 * @param downloadFile the downloadFile to set
	 */
	public void setDownloadFile(StreamedContent downloadFile) {
		this.downloadFile = downloadFile;
	}
	
	public Collection<FileUploaded> getFilesInterno() {
		return filesInterno;
	}

	public void setFilesInterno(Collection<FileUploaded> filesInterno) {
		this.filesInterno = filesInterno;
	}

	public List<FileUploaded> getDeletefilesInterno() {
		return deletefilesInterno;
	}

	public void setDeletefilesInterno(List<FileUploaded> deletefilesInterno) {
		this.deletefilesInterno = deletefilesInterno;
	}

	public void setDownloadAllFilesInterno(StreamedContent downloadAllFilesInterno) {
		this.downloadAllFilesInterno = downloadAllFilesInterno;
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

	/**
	 * @return the selectedRecebedorFinal1
	 */
	public PagadorRecebedor getSelectedRecebedorFinal1() {
		return selectedRecebedorFinal1;
	}

	/**
	 * @param selectedRecebedorFinal1 the selectedRecebedorFinal1 to set
	 */
	public void setSelectedRecebedorFinal1(PagadorRecebedor selectedRecebedorFinal1) {
		this.selectedRecebedorFinal1 = selectedRecebedorFinal1;
	}

	/**
	 * @return the nomeRecebedorFinal1
	 */
	public String getNomeRecebedorFinal1() {
		return nomeRecebedorFinal1;
	}

	/**
	 * @param nomeRecebedorFinal1 the nomeRecebedorFinal1 to set
	 */
	public void setNomeRecebedorFinal1(String nomeRecebedorFinal1) {
		this.nomeRecebedorFinal1 = nomeRecebedorFinal1;
	}

	/**
	 * @return the idRecebedorFinal1
	 */
	public long getIdRecebedorFinal1() {
		return idRecebedorFinal1;
	}

	/**
	 * @param idRecebedorFinal1 the idRecebedorFinal1 to set
	 */
	public void setIdRecebedorFinal1(long idRecebedorFinal1) {
		this.idRecebedorFinal1 = idRecebedorFinal1;
	}

	/**
	 * @return the selectedRecebedorFinal2
	 */
	public PagadorRecebedor getSelectedRecebedorFinal2() {
		return selectedRecebedorFinal2;
	}

	/**
	 * @param selectedRecebedorFinal2 the selectedRecebedorFinal2 to set
	 */
	public void setSelectedRecebedorFinal2(PagadorRecebedor selectedRecebedorFinal2) {
		this.selectedRecebedorFinal2 = selectedRecebedorFinal2;
	}

	/**
	 * @return the nomeRecebedorFinal2
	 */
	public String getNomeRecebedorFinal2() {
		return nomeRecebedorFinal2;
	}

	/**
	 * @param nomeRecebedorFinal2 the nomeRecebedorFinal2 to set
	 */
	public void setNomeRecebedorFinal2(String nomeRecebedorFinal2) {
		this.nomeRecebedorFinal2 = nomeRecebedorFinal2;
	}

	/**
	 * @return the idRecebedorFinal2
	 */
	public long getIdRecebedorFinal2() {
		return idRecebedorFinal2;
	}

	/**
	 * @param idRecebedorFinal2 the idRecebedorFinal2 to set
	 */
	public void setIdRecebedorFinal2(long idRecebedorFinal2) {
		this.idRecebedorFinal2 = idRecebedorFinal2;
	}

	/**
	 * @return the selectedRecebedorFinal3
	 */
	public PagadorRecebedor getSelectedRecebedorFinal3() {
		return selectedRecebedorFinal3;
	}

	/**
	 * @param selectedRecebedorFinal3 the selectedRecebedorFinal3 to set
	 */
	public void setSelectedRecebedorFinal3(PagadorRecebedor selectedRecebedorFinal3) {
		this.selectedRecebedorFinal3 = selectedRecebedorFinal3;
	}

	/**
	 * @return the nomeRecebedorFinal3
	 */
	public String getNomeRecebedorFinal3() {
		return nomeRecebedorFinal3;
	}

	/**
	 * @param nomeRecebedorFinal3 the nomeRecebedorFinal3 to set
	 */
	public void setNomeRecebedorFinal3(String nomeRecebedorFinal3) {
		this.nomeRecebedorFinal3 = nomeRecebedorFinal3;
	}

	/**
	 * @return the idRecebedorFinal3
	 */
	public long getIdRecebedorFinal3() {
		return idRecebedorFinal3;
	}

	/**
	 * @param idRecebedorFinal3 the idRecebedorFinal3 to set
	 */
	public void setIdRecebedorFinal3(long idRecebedorFinal3) {
		this.idRecebedorFinal3 = idRecebedorFinal3;
	}

	/**
	 * @return the selectedRecebedorFinal4
	 */
	public PagadorRecebedor getSelectedRecebedorFinal4() {
		return selectedRecebedorFinal4;
	}

	/**
	 * @param selectedRecebedorFinal4 the selectedRecebedorFinal4 to set
	 */
	public void setSelectedRecebedorFinal4(PagadorRecebedor selectedRecebedorFinal4) {
		this.selectedRecebedorFinal4 = selectedRecebedorFinal4;
	}

	/**
	 * @return the nomeRecebedorFinal4
	 */
	public String getNomeRecebedorFinal4() {
		return nomeRecebedorFinal4;
	}

	/**
	 * @param nomeRecebedorFinal4 the nomeRecebedorFinal4 to set
	 */
	public void setNomeRecebedorFinal4(String nomeRecebedorFinal4) {
		this.nomeRecebedorFinal4 = nomeRecebedorFinal4;
	}

	/**
	 * @return the idRecebedorFinal4
	 */
	public long getIdRecebedorFinal4() {
		return idRecebedorFinal4;
	}

	/**
	 * @param idRecebedorFinal4 the idRecebedorFinal4 to set
	 */
	public void setIdRecebedorFinal4(long idRecebedorFinal4) {
		this.idRecebedorFinal4 = idRecebedorFinal4;
	}

	/**
	 * @return the selectedRecebedorFinal5
	 */
	public PagadorRecebedor getSelectedRecebedorFinal5() {
		return selectedRecebedorFinal5;
	}

	/**
	 * @param selectedRecebedorFinal5 the selectedRecebedorFinal5 to set
	 */
	public void setSelectedRecebedorFinal5(PagadorRecebedor selectedRecebedorFinal5) {
		this.selectedRecebedorFinal5 = selectedRecebedorFinal5;
	}

	/**
	 * @return the nomeRecebedorFinal5
	 */
	public String getNomeRecebedorFinal5() {
		return nomeRecebedorFinal5;
	}

	/**
	 * @param nomeRecebedorFinal5 the nomeRecebedorFinal5 to set
	 */
	public void setNomeRecebedorFinal5(String nomeRecebedorFinal5) {
		this.nomeRecebedorFinal5 = nomeRecebedorFinal5;
	}

	/**
	 * @return the idRecebedorFinal5
	 */
	public long getIdRecebedorFinal5() {
		return idRecebedorFinal5;
	}

	/**
	 * @param idRecebedorFinal5 the idRecebedorFinal5 to set
	 */
	public void setIdRecebedorFinal5(long idRecebedorFinal5) {
		this.idRecebedorFinal5 = idRecebedorFinal5;
	}

	/**
	 * @return the renderRecebedorFinais
	 */
	public boolean isRenderRecebedorFinais() {
		return renderRecebedorFinais;
	}

	/**
	 * @param renderRecebedorFinais the renderRecebedorFinais to set
	 */
	public void setRenderRecebedorFinais(boolean renderRecebedorFinais) {
		this.renderRecebedorFinais = renderRecebedorFinais;
	}

	/**
	 * @return the renderRecebedorFinal2
	 */
	public boolean isRenderRecebedorFinal2() {
		return renderRecebedorFinal2;
	}

	/**
	 * @param renderRecebedorFinal2 the renderRecebedorFinal2 to set
	 */
	public void setRenderRecebedorFinal2(boolean renderRecebedorFinal2) {
		this.renderRecebedorFinal2 = renderRecebedorFinal2;
	}

	/**
	 * @return the renderRecebedorFinal3
	 */
	public boolean isRenderRecebedorFinal3() {
		return renderRecebedorFinal3;
	}

	/**
	 * @param renderRecebedorFinal3 the renderRecebedorFinal3 to set
	 */
	public void setRenderRecebedorFinal3(boolean renderRecebedorFinal3) {
		this.renderRecebedorFinal3 = renderRecebedorFinal3;
	}

	/**
	 * @return the renderRecebedorFinal4
	 */
	public boolean isRenderRecebedorFinal4() {
		return renderRecebedorFinal4;
	}

	/**
	 * @param renderRecebedorFinal4 the renderRecebedorFinal4 to set
	 */
	public void setRenderRecebedorFinal4(boolean renderRecebedorFinal4) {
		this.renderRecebedorFinal4 = renderRecebedorFinal4;
	}

	/**
	 * @return the renderRecebedorFinal5
	 */
	public boolean isRenderRecebedorFinal5() {
		return renderRecebedorFinal5;
	}

	/**
	 * @param renderRecebedorFinal5 the renderRecebedorFinal5 to set
	 */
	public void setRenderRecebedorFinal5(boolean renderRecebedorFinal5) {
		this.renderRecebedorFinal5 = renderRecebedorFinal5;
	}

	public String getPesquisaObservacao() {
		return pesquisaObservacao;
	}

	public void setPesquisaObservacao(String pesquisaObservacao) {
		this.pesquisaObservacao = pesquisaObservacao;
	}

	public List<PesquisaObservacoes> getListPesquisaObservacoes() {
		return listPesquisaObservacoes;
	}

	public void setListPesquisaObservacoes(List<PesquisaObservacoes> listPesquisaObservacoes) {
		this.listPesquisaObservacoes = listPesquisaObservacoes;
	}

	/**
	 * @return the dtReparcela
	 */
	public Date getDtReparcela() {
		return dtReparcela;
	}

	/**
	 * @param dtReparcela the dtReparcela to set
	 */
	public void setDtReparcela(Date dtReparcela) {
		this.dtReparcela = dtReparcela;
	}

	/**
	 * @return the vlrReparcela
	 */
	public BigDecimal getVlrReparcela() {
		return vlrReparcela;
	}

	/**
	 * @param vlrReparcela the vlrReparcela to set
	 */
	public void setVlrReparcela(BigDecimal vlrReparcela) {
		this.vlrReparcela = vlrReparcela;
	}

	/**
	 * @return the qtdeReparcela
	 */
	public String getQtdeReparcela() {
		return qtdeReparcela;
	}

	/**
	 * @param qtdeReparcela the qtdeReparcela to set
	 */
	public void setQtdeReparcela(String qtdeReparcela) {
		this.qtdeReparcela = qtdeReparcela;
	}

	/**
	 * @return the totalQtedParcelas
	 */
	public String getTotalQtedParcelas() {
		return totalQtedParcelas;
	}

	/**
	 * @param totalQtedParcelas the totalQtedParcelas to set
	 */
	public void setTotalQtedParcelas(String totalQtedParcelas) {
		this.totalQtedParcelas = totalQtedParcelas;
	}

	/**
	 * @return the totalVlrParcelas
	 */
	public BigDecimal getTotalVlrParcelas() {
		return totalVlrParcelas;
	}

	/**
	 * @param totalVlrParcelas the totalVlrParcelas to set
	 */
	public void setTotalVlrParcelas(BigDecimal totalVlrParcelas) {
		this.totalVlrParcelas = totalVlrParcelas;
	}

	/**
	 * @return the splitBoletoIugu
	 */
	public boolean isSplitBoletoIugu() {
		return splitBoletoIugu;
	}

	/**
	 * @param splitBoletoIugu the splitBoletoIugu to set
	 */
	public void setSplitBoletoIugu(boolean splitBoletoIugu) {
		this.splitBoletoIugu = splitBoletoIugu;
	}

	/**
	 * @return the cedenteIugu
	 */
	public List<PagadorRecebedor> getCedentesIugu() {
		return cedentesIugu;
	}

	/**
	 * @param cedenteIugu the cedenteIugu to set
	 */
	public void setCedenteIugu(List<PagadorRecebedor> cedentesIugu) {
		this.cedentesIugu = cedentesIugu;
	}

	/**
	 * @return the cedenteSelecionado
	 */
	public long getCedenteSelecionado() {
		return cedenteSelecionado;
	}

	/**
	 * @param cedenteSelecionado the cedenteSelecionado to set
	 */
	public void setCedenteSelecionado(long cedenteSelecionado) {
		this.cedenteSelecionado = cedenteSelecionado;
	}

	/**
	 * @return the observacaoContratoDetalhes
	 */
	public String getObservacaoContratoDetalhes() {
		return observacaoContratoDetalhes;
	}

	/**
	 * @param observacaoContratoDetalhes the observacaoContratoDetalhes to set
	 */
	public void setObservacaoContratoDetalhes(String observacaoContratoDetalhes) {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		this.dataObservacao = dataHoje.getTime();

		this.observacao = null;

		this.listContratoCobrancaDetalhesObservacoes = new ArrayList<ContratoCobrancaDetalhesObservacoes>();

		ContratoCobrancaDetalhesDao contratoCobrancaDetalhesDao = new ContratoCobrancaDetalhesDao();

		this.listContratoCobrancaDetalhesObservacoes = contratoCobrancaDetalhesDao
				.listaObservacoesOrdenadas(this.contratoCobrancaDetalhes.getId());
	}

	/**
	 * @return the listContratoCobrancaDetalhesObservacoes
	 */
	public List<ContratoCobrancaDetalhesObservacoes> getListContratoCobrancaDetalhesObservacoes() {
		return listContratoCobrancaDetalhesObservacoes;
	}

	/**
	 * @param listContratoCobrancaDetalhesObservacoes the
	 *                                                listContratoCobrancaDetalhesObservacoes
	 *                                                to set
	 */
	public void setListContratoCobrancaDetalhesObservacoes(
			List<ContratoCobrancaDetalhesObservacoes> listContratoCobrancaDetalhesObservacoes) {
		this.listContratoCobrancaDetalhesObservacoes = listContratoCobrancaDetalhesObservacoes;
	}

	/**
	 * @return the contratoCobrancaDetalhes
	 */
	public ContratoCobrancaDetalhes getContratoCobrancaDetalhes() {
		return contratoCobrancaDetalhes;
	}

	/**
	 * @param contratoCobrancaDetalhes the contratoCobrancaDetalhes to set
	 */
	public void setContratoCobrancaDetalhes(ContratoCobrancaDetalhes contratoCobrancaDetalhes) {
		this.contratoCobrancaDetalhes = contratoCobrancaDetalhes;
	}

	/**
	 * @return the contratoCobrancaDetalhesObservacoes
	 */
	public ContratoCobrancaDetalhesObservacoes getContratoCobrancaDetalhesObservacoes() {
		return contratoCobrancaDetalhesObservacoes;
	}

	/**
	 * @param contratoCobrancaDetalhesObservacoes the
	 *                                            contratoCobrancaDetalhesObservacoes
	 *                                            to set
	 */
	public void setContratoCobrancaDetalhesObservacoes(
			ContratoCobrancaDetalhesObservacoes contratoCobrancaDetalhesObservacoes) {
		this.contratoCobrancaDetalhesObservacoes = contratoCobrancaDetalhesObservacoes;
	}

	public GruposPagadores getSelectedGrupoPagadores() {
		return selectedGrupoPagadores;
	}

	public void setSelectedGrupoPagadores(GruposPagadores selectedGrupoPagadores) {
		this.selectedGrupoPagadores = selectedGrupoPagadores;
	}

	public String getNomeGrupoPagador() {
		return nomeGrupoPagador;
	}

	public void setNomeGrupoPagador(String nomeGrupoPagador) {
		this.nomeGrupoPagador = nomeGrupoPagador;
	}

	public long getIdGrupoPagador() {
		return idGrupoPagador;
	}

	public void setIdGrupoPagador(long idGrupoPagador) {
		this.idGrupoPagador = idGrupoPagador;
	}

	public boolean isGrupoPagadores() {
		return grupoPagadores;
	}

	public void setGrupoPagadores(boolean grupoPagadores) {
		this.grupoPagadores = grupoPagadores;
	}

	public List<GruposPagadores> getListGrupoPagadores() {
		return listGrupoPagadores;
	}

	public void setListGrupoPagadores(List<GruposPagadores> listGrupoPagadores) {
		this.listGrupoPagadores = listGrupoPagadores;
	}

	public Date getDataPromessaPagamento() {
		return dataPromessaPagamento;
	}

	public void setDataPromessaPagamento(Date dataPromessaPagamento) {
		this.dataPromessaPagamento = dataPromessaPagamento;
	}

	public LazyDataModel<Responsavel> getResponsaveisLazy() {
		return responsaveisLazy;
	}

	public void setResponsaveisLazy(LazyDataModel<Responsavel> responsaveisLazy) {
		this.responsaveisLazy = responsaveisLazy;
	}

	public List<FilaInvestidores> getListFilaInvestidores() {
		return listFilaInvestidores;
	}

	public void setListFilaInvestidores(List<FilaInvestidores> listFilaInvestidores) {
		this.listFilaInvestidores = listFilaInvestidores;
	}

	public FilaInvestidores getSelectedInvestidor() {
		return selectedInvestidor;
	}

	public void setSelectedInvestidor(FilaInvestidores selectedInvestidor) {
		this.selectedInvestidor = selectedInvestidor;
	}

	public FilaInvestidores getObjetoFilaInvestidores() {
		return objetoFilaInvestidores;
	}

	public void setObjetoFilaInvestidores(FilaInvestidores objetoFilaInvestidores) {
		this.objetoFilaInvestidores = objetoFilaInvestidores;
	}

	public PagadorRecebedor getSelectedRecebedorFinal6() {
		return selectedRecebedorFinal6;
	}

	public void setSelectedRecebedorFinal6(PagadorRecebedor selectedRecebedorFinal6) {
		this.selectedRecebedorFinal6 = selectedRecebedorFinal6;
	}

	public String getNomeRecebedorFinal6() {
		return nomeRecebedorFinal6;
	}

	public void setNomeRecebedorFinal6(String nomeRecebedorFinal6) {
		this.nomeRecebedorFinal6 = nomeRecebedorFinal6;
	}

	public long getIdRecebedorFinal6() {
		return idRecebedorFinal6;
	}

	public void setIdRecebedorFinal6(long idRecebedorFinal6) {
		this.idRecebedorFinal6 = idRecebedorFinal6;
	}

	public PagadorRecebedor getSelectedRecebedorFinal7() {
		return selectedRecebedorFinal7;
	}

	public void setSelectedRecebedorFinal7(PagadorRecebedor selectedRecebedorFinal7) {
		this.selectedRecebedorFinal7 = selectedRecebedorFinal7;
	}

	public String getNomeRecebedorFinal7() {
		return nomeRecebedorFinal7;
	}

	public void setNomeRecebedorFinal7(String nomeRecebedorFinal7) {
		this.nomeRecebedorFinal7 = nomeRecebedorFinal7;
	}

	public long getIdRecebedorFinal7() {
		return idRecebedorFinal7;
	}

	public void setIdRecebedorFinal7(long idRecebedorFinal7) {
		this.idRecebedorFinal7 = idRecebedorFinal7;
	}

	public PagadorRecebedor getSelectedRecebedorFinal8() {
		return selectedRecebedorFinal8;
	}

	public void setSelectedRecebedorFinal8(PagadorRecebedor selectedRecebedorFinal8) {
		this.selectedRecebedorFinal8 = selectedRecebedorFinal8;
	}

	public String getNomeRecebedorFinal8() {
		return nomeRecebedorFinal8;
	}

	public void setNomeRecebedorFinal8(String nomeRecebedorFinal8) {
		this.nomeRecebedorFinal8 = nomeRecebedorFinal8;
	}

	public long getIdRecebedorFinal8() {
		return idRecebedorFinal8;
	}

	public void setIdRecebedorFinal8(long idRecebedorFinal8) {
		this.idRecebedorFinal8 = idRecebedorFinal8;
	}

	public PagadorRecebedor getSelectedRecebedorFinal9() {
		return selectedRecebedorFinal9;
	}

	public void setSelectedRecebedorFinal9(PagadorRecebedor selectedRecebedorFinal9) {
		this.selectedRecebedorFinal9 = selectedRecebedorFinal9;
	}

	public String getNomeRecebedorFinal9() {
		return nomeRecebedorFinal9;
	}

	public void setNomeRecebedorFinal9(String nomeRecebedorFinal9) {
		this.nomeRecebedorFinal9 = nomeRecebedorFinal9;
	}

	public long getIdRecebedorFinal9() {
		return idRecebedorFinal9;
	}

	public void setIdRecebedorFinal9(long idRecebedorFinal9) {
		this.idRecebedorFinal9 = idRecebedorFinal9;
	}

	public PagadorRecebedor getSelectedRecebedorFinal10() {
		return selectedRecebedorFinal10;
	}

	public void setSelectedRecebedorFinal10(PagadorRecebedor selectedRecebedorFinal10) {
		this.selectedRecebedorFinal10 = selectedRecebedorFinal10;
	}

	public String getNomeRecebedorFinal10() {
		return nomeRecebedorFinal10;
	}

	public void setNomeRecebedorFinal10(String nomeRecebedorFinal10) {
		this.nomeRecebedorFinal10 = nomeRecebedorFinal10;
	}

	public long getIdRecebedorFinal10() {
		return idRecebedorFinal10;
	}

	public void setIdRecebedorFinal10(long idRecebedorFinal10) {
		this.idRecebedorFinal10 = idRecebedorFinal10;
	}

	public ContratoCobrancaParcelasInvestidor getBaixaContratoCobrancaParcelasInvestidor() {
		return baixaContratoCobrancaParcelasInvestidor;
	}

	public void setBaixaContratoCobrancaParcelasInvestidor(
			ContratoCobrancaParcelasInvestidor baixaContratoCobrancaParcelasInvestidor) {
		this.baixaContratoCobrancaParcelasInvestidor = baixaContratoCobrancaParcelasInvestidor;
	}

	public Date getDataPagamentoInvestidor() {
		return dataPagamentoInvestidor;
	}

	public void setDataPagamentoInvestidor(Date dataPagamentoInvestidor) {
		this.dataPagamentoInvestidor = dataPagamentoInvestidor;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public Date getDataHoje() {
		return dataHoje;
	}

	public void setDataHoje(Date dataHoje) {
		this.dataHoje = dataHoje;
	}

	public List<ContratoCobrancaParcelasInvestidor> getSelectedListContratoCobrancaParcelasInvestidor() {
		return selectedListContratoCobrancaParcelasInvestidor;
	}

	public void setSelectedListContratoCobrancaParcelasInvestidor(
			List<ContratoCobrancaParcelasInvestidor> selectedListContratoCobrancaParcelasInvestidor) {
		this.selectedListContratoCobrancaParcelasInvestidor = selectedListContratoCobrancaParcelasInvestidor;
	}

	public List<ContratoCobrancaParcelasInvestidor> getSelectedListContratoCobrancaParcelasInvestidor2() {
		return selectedListContratoCobrancaParcelasInvestidor2;
	}

	public void setSelectedListContratoCobrancaParcelasInvestidor2(
			List<ContratoCobrancaParcelasInvestidor> selectedListContratoCobrancaParcelasInvestidor2) {
		this.selectedListContratoCobrancaParcelasInvestidor2 = selectedListContratoCobrancaParcelasInvestidor2;
	}

	public List<ContratoCobrancaParcelasInvestidor> getSelectedListContratoCobrancaParcelasInvestidor3() {
		return selectedListContratoCobrancaParcelasInvestidor3;
	}

	public void setSelectedListContratoCobrancaParcelasInvestidor3(
			List<ContratoCobrancaParcelasInvestidor> selectedListContratoCobrancaParcelasInvestidor3) {
		this.selectedListContratoCobrancaParcelasInvestidor3 = selectedListContratoCobrancaParcelasInvestidor3;
	}

	public List<ContratoCobrancaParcelasInvestidor> getSelectedListContratoCobrancaParcelasInvestidor4() {
		return selectedListContratoCobrancaParcelasInvestidor4;
	}

	public void setSelectedListContratoCobrancaParcelasInvestidor4(
			List<ContratoCobrancaParcelasInvestidor> selectedListContratoCobrancaParcelasInvestidor4) {
		this.selectedListContratoCobrancaParcelasInvestidor4 = selectedListContratoCobrancaParcelasInvestidor4;
	}

	public List<ContratoCobrancaParcelasInvestidor> getSelectedListContratoCobrancaParcelasInvestidor5() {
		return selectedListContratoCobrancaParcelasInvestidor5;
	}

	public void setSelectedListContratoCobrancaParcelasInvestidor5(
			List<ContratoCobrancaParcelasInvestidor> selectedListContratoCobrancaParcelasInvestidor5) {
		this.selectedListContratoCobrancaParcelasInvestidor5 = selectedListContratoCobrancaParcelasInvestidor5;
	}

	public List<ContratoCobrancaParcelasInvestidor> getSelectedListContratoCobrancaParcelasInvestidor6() {
		return selectedListContratoCobrancaParcelasInvestidor6;
	}

	public void setSelectedListContratoCobrancaParcelasInvestidor6(
			List<ContratoCobrancaParcelasInvestidor> selectedListContratoCobrancaParcelasInvestidor6) {
		this.selectedListContratoCobrancaParcelasInvestidor6 = selectedListContratoCobrancaParcelasInvestidor6;
	}

	public List<ContratoCobrancaParcelasInvestidor> getSelectedListContratoCobrancaParcelasInvestidor7() {
		return selectedListContratoCobrancaParcelasInvestidor7;
	}

	public void setSelectedListContratoCobrancaParcelasInvestidor7(
			List<ContratoCobrancaParcelasInvestidor> selectedListContratoCobrancaParcelasInvestidor7) {
		this.selectedListContratoCobrancaParcelasInvestidor7 = selectedListContratoCobrancaParcelasInvestidor7;
	}

	public List<ContratoCobrancaParcelasInvestidor> getSelectedListContratoCobrancaParcelasInvestidor8() {
		return selectedListContratoCobrancaParcelasInvestidor8;
	}

	public void setSelectedListContratoCobrancaParcelasInvestidor8(
			List<ContratoCobrancaParcelasInvestidor> selectedListContratoCobrancaParcelasInvestidor8) {
		this.selectedListContratoCobrancaParcelasInvestidor8 = selectedListContratoCobrancaParcelasInvestidor8;
	}

	public List<ContratoCobrancaParcelasInvestidor> getSelectedListContratoCobrancaParcelasInvestidor9() {
		return selectedListContratoCobrancaParcelasInvestidor9;
	}

	public void setSelectedListContratoCobrancaParcelasInvestidor9(
			List<ContratoCobrancaParcelasInvestidor> selectedListContratoCobrancaParcelasInvestidor9) {
		this.selectedListContratoCobrancaParcelasInvestidor9 = selectedListContratoCobrancaParcelasInvestidor9;
	}

	public List<ContratoCobrancaParcelasInvestidor> getSelectedListContratoCobrancaParcelasInvestidor10() {
		return selectedListContratoCobrancaParcelasInvestidor10;
	}

	public void setSelectedListContratoCobrancaParcelasInvestidor10(
			List<ContratoCobrancaParcelasInvestidor> selectedListContratoCobrancaParcelasInvestidor10) {
		this.selectedListContratoCobrancaParcelasInvestidor10 = selectedListContratoCobrancaParcelasInvestidor10;
	}

	public boolean isTipoPessoaIsFisicaCC() {
		return tipoPessoaIsFisicaCC;
	}

	public void setTipoPessoaIsFisicaCC(boolean tipoPessoaIsFisicaCC) {
		this.tipoPessoaIsFisicaCC = tipoPessoaIsFisicaCC;
	}

	public Collection<ContratoCobranca> getContratos() {
		return contratos;
	}

	public void setContratos(Collection<ContratoCobranca> contratos) {
		this.contratos = contratos;
	}

	public boolean isCrmMode() {
		return crmMode;
	}

	public void setCrmMode(boolean crmMode) {
		this.crmMode = crmMode;
	}

	public boolean isPreContratoCustom() {
		return preContratoCustom;
	}

	public void setPreContratoCustom(boolean preContratoCustom) {
		this.preContratoCustom = preContratoCustom;
	}

	public InvestidorMB getInvestidorMB() {
		return investidorMB;
	}

	public void setInvestidorMB(InvestidorMB investidorMB) {
		this.investidorMB = investidorMB;
	}

	public BigDecimal getVlrRepasseNew() {
		return vlrRepasseNew;
	}

	public void setVlrRepasseNew(BigDecimal vlrRepasseNew) {
		this.vlrRepasseNew = vlrRepasseNew;
	}

	public BigDecimal getVlrRetencaoNew() {
		return vlrRetencaoNew;
	}

	public void setVlrRetencaoNew(BigDecimal vlrRetencaoNew) {
		this.vlrRetencaoNew = vlrRetencaoNew;
	}

	public BigDecimal getVlrComissaoNew() {
		return vlrComissaoNew;
	}

	public void setVlrComissaoNew(BigDecimal vlrComissaoNew) {

		this.vlrComissaoNew = vlrComissaoNew;
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

	public List<ContratoCobrancaParcelasInvestidor> getSelectedParcelasInvestidorCorrespondente() {
		return selectedParcelasInvestidorCorrespondente;
	}

	public void setSelectedParcelasInvestidorCorrespondente(
			List<ContratoCobrancaParcelasInvestidor> selectedParcelasInvestidorCorrespondente) {
		this.selectedParcelasInvestidorCorrespondente = selectedParcelasInvestidorCorrespondente;
	}

	public List<ContratoCobrancaParcelasInvestidor> getSelectedParcelasInvestidorSA() {
		return selectedParcelasInvestidorSA;
	}

	public void setSelectedParcelasInvestidorSA(List<ContratoCobrancaParcelasInvestidor> selectedParcelasInvestidorSA) {
		this.selectedParcelasInvestidorSA = selectedParcelasInvestidorSA;
	}

	public List<ContratoCobrancaParcelasInvestidor> getSelectedParcelasInvestidorEnvelope() {
		return selectedParcelasInvestidorEnvelope;
	}

	public void setSelectedParcelasInvestidorEnvelope(
			List<ContratoCobrancaParcelasInvestidor> selectedParcelasInvestidorEnvelope) {
		this.selectedParcelasInvestidorEnvelope = selectedParcelasInvestidorEnvelope;
	}

	public List<ContratoCobranca> getContratoCobrancaFinanceiroDia() {
		return contratoCobrancaFinanceiroDia;
	}

	public void setContratoCobrancaFinanceiroDia(List<ContratoCobranca> contratoCobrancaFinanceiroDia) {
		this.contratoCobrancaFinanceiroDia = contratoCobrancaFinanceiroDia;
	}

	public CRMMB getCrmmb() {
		return crmmb;
	}

	public void setCrmmb(CRMMB crmmb) {
		this.crmmb = crmmb;
	}

	public int getIdAntecipacaoInvestidor() {
		return idAntecipacaoInvestidor;
	}

	public void setIdAntecipacaoInvestidor(int idAntecipacaoInvestidor) {
		this.idAntecipacaoInvestidor = idAntecipacaoInvestidor;
	}

	public ContratoCobrancaParcelasInvestidor getAntecipacao() {
		return antecipacao;
	}

	public void setAntecipacao(ContratoCobrancaParcelasInvestidor antecipacao) {
		this.antecipacao = antecipacao;
	}

	public SimulacaoVO getSimuladorParcelas() {
		return simuladorParcelas;
	}

	public void setSimuladorParcelas(SimulacaoVO simuladorParcelas) {
		this.simuladorParcelas = simuladorParcelas;
	}

	public BigInteger getNumeroParcelaReparcelamento() {
		return numeroParcelaReparcelamento;
	}

	public void setNumeroParcelaReparcelamento(BigInteger numeroParcelaReparcelamento) {
		this.numeroParcelaReparcelamento = numeroParcelaReparcelamento;
	}

	public Date getDataParcela() {
		return dataParcela;
	}

	public void setDataParcela(Date dataParcela) {
		this.dataParcela = dataParcela;
	}

	public BigDecimal getSaldoDevedorReparcelamento() {
		return saldoDevedorReparcelamento;
	}

	public void setSaldoDevedorReparcelamento(BigDecimal saldoDevedorReparcelamento) {
		this.saldoDevedorReparcelamento = saldoDevedorReparcelamento;
	}

	public BigInteger getCarenciaReparcelamento() {
		return carenciaReparcelamento;
	}

	public void setCarenciaReparcelamento(BigInteger carenciaReparcelamento) {
		this.carenciaReparcelamento = carenciaReparcelamento;
	}

	public ContratoCobrancaDetalhes getAmortizacao() {
		return amortizacao;
	}

	public void setAmortizacao(ContratoCobrancaDetalhes amortizacao) {
		this.amortizacao = amortizacao;
	}

	public Set<Segurado> getListSegurado() {
		return this.objetoContratoCobranca.getListSegurados();
	}

	public void setListSegurado(Set<Segurado> listSegurado) {
		this.objetoContratoCobranca.setListSegurados(listSegurado);
	}

	public String getTipoPesquisaPagadorRecebedor() {
		return tipoPesquisaPagadorRecebedor;
	}

	public void setTipoPesquisaPagadorRecebedor(String tipoPesquisaPagadorRecebedor) {
		this.tipoPesquisaPagadorRecebedor = tipoPesquisaPagadorRecebedor;
	}

	public Segurado getSeguradoSelecionado() {
		return seguradoSelecionado;
	}

	public void setSeguradoSelecionado(Segurado seguradoSelecionado) {
		this.seguradoSelecionado = seguradoSelecionado;
	}

	public PagadorRecebedorSocio getSocioSelecionado() {
		return socioSelecionado;
	}

	public void setSocioSelecionado(PagadorRecebedorSocio socioSelecionado) {
		this.socioSelecionado = socioSelecionado;
	}

	public boolean isAddSegurador() {
		return addSegurador;
	}

	public void setAddSegurador(boolean addSegurador) {
		this.addSegurador = addSegurador;
	}

	public boolean isAddSocio() {
		return addSocio;
	}

	public void setAddSocio(boolean addSocio) {
		this.addSocio = addSocio;
	}
	
	public String getUpdatePagadorRecebedor() {
		return updatePagadorRecebedor;
	}

	public void setUpdatePagadorRecebedor(String updatePagadorRecebedor) {
		this.updatePagadorRecebedor = updatePagadorRecebedor;
	}

	public Date getRelDataContratoInicioAtraso() {
		return relDataContratoInicioAtraso;
	}

	public String getUpdateResponsavel() {
		return updateResponsavel;
	}

	public void setUpdateResponsavel(String updateResponsavel) {
		this.updateResponsavel = updateResponsavel;
	}

	public void setRelDataContratoInicioAtraso(Date relDataContratoInicioAtraso) {
		this.relDataContratoInicioAtraso = relDataContratoInicioAtraso;
	}

	public Date getRelDataContratoFimAtraso() {
		return relDataContratoFimAtraso;
	}

	public void setRelDataContratoFimAtraso(Date relDataContratoFimAtraso) {
		this.relDataContratoFimAtraso = relDataContratoFimAtraso;
	}

	public String getTituloTelaConsultaPreStatus() {
		return tituloTelaConsultaPreStatus;
	}

	public void setTituloTelaConsultaPreStatus(String tituloTelaConsultaPreStatus) {
		this.tituloTelaConsultaPreStatus = tituloTelaConsultaPreStatus;
	}

	public int getIndexStepsStatusContrato() {
		return indexStepsStatusContrato;
	}

	public void setIndexStepsStatusContrato(int indexStepsStatusContrato) {
		this.indexStepsStatusContrato = indexStepsStatusContrato;
	}

	public Date getDataPrevistaVistoria() {
		return dataPrevistaVistoria;
	}

	public void setDataPrevistaVistoria(Date dataPrevistaVistoria) {
		this.dataPrevistaVistoria = dataPrevistaVistoria;
	}

	public BigDecimal getValorBoletoPreContrato() {
		return valorBoletoPreContrato;
	}

	public void setValorBoletoPreContrato(BigDecimal valorBoletoPreContrato) {
		this.valorBoletoPreContrato = valorBoletoPreContrato;
	}

	public BigDecimal getTaxaPreAprovada() {
		return taxaPreAprovada;
	}

	public void setTaxaPreAprovada(BigDecimal taxaPreAprovada) {
		this.taxaPreAprovada = taxaPreAprovada;
	}

	public BigDecimal getValorMercadoImovel() {
		return valorMercadoImovel;
	}

	public void setValorMercadoImovel(BigDecimal valorMercadoImovel) {
		this.valorMercadoImovel = valorMercadoImovel;
	}

	public BigDecimal getValorVendaForçadaImóvel() {
		return valorVendaForçadaImóvel;
	}

	public void setValorVendaForçadaImóvel(BigDecimal valorVendaForçadaImóvel) {
		this.valorVendaForçadaImóvel = valorVendaForçadaImóvel;
	}

	public String getComentarioJuridico() {
		return comentarioJuridico;
	}

	public void setComentarioJuridico(String comentarioJuridico) {
		this.comentarioJuridico = comentarioJuridico;
	}

	public void setAddPagadorPreContrato(Boolean addPagadorPreContrato) {
		this.addPagadorPreContrato = addPagadorPreContrato;
	}

	public Boolean getAddPagadorPreContrato() {
		return addPagadorPreContrato;
	}

	public BigInteger getPrazoMaxPreAprovado() {
		return prazoMaxPreAprovado;
	}

	public void setPrazoMaxPreAprovado(BigInteger prazoMaxPreAprovado) {
		this.prazoMaxPreAprovado = prazoMaxPreAprovado;
	}

	public String getValorBoletoStr() {
		return valorBoletoStr;
	}

	public void setValorBoletoStr(String valorBoletoStr) {
		this.valorBoletoStr = valorBoletoStr;
	}

	public String getTituloPagadorRecebedorDialog() {
		return tituloPagadorRecebedorDialog;
	}

	public void setTituloPagadorRecebedorDialog(String tituloPagadorRecebedorDialog) {
		this.tituloPagadorRecebedorDialog = tituloPagadorRecebedorDialog;
	}

	public List<ContratoCobranca> getSelectedContratoCobrancaFinanceiroDia() {
		return selectedContratoCobrancaFinanceiroDia;
	}

	public void setSelectedContratoCobrancaFinanceiroDia(List<ContratoCobranca> selectedContratoCobrancaFinanceiroDia) {
		this.selectedContratoCobrancaFinanceiroDia = selectedContratoCobrancaFinanceiroDia;
	}

	public String getTipoContratoCobrancaFinanceiroDia() {
		return tipoContratoCobrancaFinanceiroDia;
	}

	public void setTipoContratoCobrancaFinanceiroDia(String tipoContratoCobrancaFinanceiroDia) {
		this.tipoContratoCobrancaFinanceiroDia = tipoContratoCobrancaFinanceiroDia;
	}

	public int getTotalContratosConsultar() {
		return totalContratosConsultar;
	}

	public void setTotalContratosConsultar(int totalContratosConsultar) {
		this.totalContratosConsultar = totalContratosConsultar;
	}

	public BigDecimal getVolumeCarteira() {
		return volumeCarteira;
	}

	public void setVolumeCarteira(BigDecimal volumeCarteira) {
		this.volumeCarteira = volumeCarteira;
	}

	public BigDecimal getValorUltimaPareclaPaga() {
		return valorUltimaPareclaPaga;
	}

	public void setValorUltimaPareclaPaga(BigDecimal valorUltimaPareclaPaga) {
		this.valorUltimaPareclaPaga = valorUltimaPareclaPaga;
	}

	public BigDecimal getSomaContratos180() {
		return somaContratos180;
	}

	public void setSomaContratos180(BigDecimal somaContratos180) {
		this.somaContratos180 = somaContratos180;
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

	public PagadorRecebedorAdicionais getPagadorSecundarioSelecionado() {
		return pagadorSecundarioSelecionado;
	}

	public void setPagadorSecundarioSelecionado(PagadorRecebedorAdicionais pagadorSecundarioSelecionado) {
		this.pagadorSecundarioSelecionado = pagadorSecundarioSelecionado;
	}

	public boolean isAddPagador() {
		return addPagador;
	}

	public void setAddPagador(boolean addPagador) {
		this.addPagador = addPagador;
	}

	public ContasPagar getContasPagarSelecionada() {
		return contasPagarSelecionada;
	}

	public void setContasPagarSelecionada(ContasPagar contasPagarSelecionada) {
		this.contasPagarSelecionada = contasPagarSelecionada;
	}

	public boolean isAddContasPagar() {
		return addContasPagar;
	}

	public void setAddContasPagar(boolean addContasPagar) {
		this.addContasPagar = addContasPagar;
	}

	public AnaliseComite getObjetoAnaliseComite() {
		return objetoAnaliseComite;
	}

	public void setObjetoAnaliseComite(AnaliseComite objetoAnaliseComite) {
		this.objetoAnaliseComite = objetoAnaliseComite;
	}

	public String getParametroConsultaContrato() {
		return parametroConsultaContrato;
	}

	public void setParametroConsultaContrato(String parametroConsultaContrato) {
		this.parametroConsultaContrato = parametroConsultaContrato;
	}

	public String getTipoParametroConsultaContrato() {
		return tipoParametroConsultaContrato;
	}

	public void setTipoParametroConsultaContrato(String tipoParametroConsultaContrato) {
		this.tipoParametroConsultaContrato = tipoParametroConsultaContrato;
	}

	public boolean isControleWhatsAppAgAssintura() {
		return controleWhatsAppAgAssintura;
	}

	public void setControleWhatsAppAgAssintura(boolean controleWhatsAppAgAssintura) {
		this.controleWhatsAppAgAssintura = controleWhatsAppAgAssintura;
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

	public BigDecimal getTotalAVencer() {
		return totalAVencer;
	}

	public void setTotalAVencer(BigDecimal totalAVencer) {
		this.totalAVencer = totalAVencer;
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

	public Collection<ContratoCobranca> getListaContratos() {
		return listaContratos;
	}

	public void setListaContratos(Collection<ContratoCobranca> listaContratos) {
		this.listaContratos = listaContratos;
	}

	public List<Responsavel> getListResponsavel() {
		return listResponsavel;
	}

	public void setListResponsavel(List<Responsavel> listResponsavel) {
		this.listResponsavel = listResponsavel;
	}

	public BarChartModel getStackedGroupBarModel() {
		return stackedGroupBarModel;
	}

	public void setStackedGroupBarModel(BarChartModel stackedGroupBarModel) {
		this.stackedGroupBarModel = stackedGroupBarModel;
	}

	public Collection<ContratoCobranca> getContratoPrazoMin() {
		return contratoPrazoMin;
	}

	public void setContratoPrazoMin(Collection<ContratoCobranca> contratoPrazoMin) {
		this.contratoPrazoMin = contratoPrazoMin;
	}
	
}
