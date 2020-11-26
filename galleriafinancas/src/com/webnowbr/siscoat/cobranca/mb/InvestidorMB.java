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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.webnowbr.siscoat.cobranca.auxiliar.NumeroPorExtenso;
import com.webnowbr.siscoat.cobranca.auxiliar.ValorPorExtenso;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaParcelasInvestidor;
import com.webnowbr.siscoat.cobranca.db.model.DebenturesInvestidor;
import com.webnowbr.siscoat.cobranca.db.model.OperacoesIndividualizado;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.TransferenciasObservacoesIUGU;
import com.webnowbr.siscoat.cobranca.db.model.directd.Socio;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaParcelasInvestidorDao;
import com.webnowbr.siscoat.cobranca.db.op.DebenturesInvestidorDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.mb.ContratoCobrancaMB.FileUploaded;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

@ManagedBean(name = "investidorMB")
@SessionScoped
public class InvestidorMB {
	
	private int qtdeContratos;
	private int parcelasAbertas;
	private BigDecimal valorReceber;
	private BigDecimal valorRecebido;
	private BigDecimal valorInvestido;	
	private List<ContratoCobranca> contratos;
	private ContratoCobranca selectedContrato;
	private ContratoCobrancaDetalhes selectedContratoCobrancaDetalhes;
	
	private long idInvestidor;
	
	private BigDecimal valorInvestidor;
	
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
	
	public InvestidorMB() {
		
	}
	
	public String clearFieldsInformeRendimentos() {
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
	
	public void processInformeRendimentos() {
		FacesContext context = FacesContext.getCurrentInstance();
		ContratoCobrancaParcelasInvestidorDao cDao = new ContratoCobrancaParcelasInvestidorDao();
		
		if (this.selectedPagador == null || this.selectedPagador.getId() <= 0) {			
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Informe de rendimentos: É obrigatória a seleção do investidor!", ""));
		} else {
			DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			
			Date dataInicioAnterior = null;
			Date dataFimAnterior = null;
			
			try {			
				if (this.anoBase.equals("2018")) {
					this.dataInicio = format.parse("01/01/2018");
					this.dataFim = format.parse("31/12/2018");
					
					dataInicioAnterior = format.parse("01/01/2017");
					dataFimAnterior = format.parse("31/12/2017");
				}
				
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
			
			this.parcelasInvestidor = cDao.getParcelasPorDataInvestidorBaixadas(this.dataInicio, this.dataFim, this.selectedPagador.getId());
			
			this.totalIRRetido = this.getTotalIRRetidoInvestidor(this.selectedPagador.getId());
			BigDecimal totalJuros = this.getTotalJurosInvestidor(this.selectedPagador.getId());
			this.totalJurosLiquido = totalJuros.subtract(totalIRRetido);
			
			this.valorInvestidorAnoAtual = cDao.getParcelasPorDataInvestidorIR(this.dataInicio, this.dataFim, this.selectedPagador.getId()); 
			
			if (dataInicioAnterior != null && dataFimAnterior != null) {
				this.valorInvestidorAnoAnterior = cDao.getParcelasPorDataInvestidorIR(dataInicioAnterior, dataFimAnterior, this.selectedPagador.getId());
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
		
		return "/Atendimento/Cobranca/InvestidorValorLiquido.xhtml";
	}
	
	public void gerarRelatorioValorLiquido() {
		
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
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "Investidores: Não há registros para os filtros informados!", ""));
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
		this.parcelasInvestidor = cDao.getParcelasPorDataInvestidorBaixadas(this.dataInicio, this.dataFim, this.selectedPagador.getId());
		
		if (this.parcelasInvestidor.size() == 0) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "Investidores: Não há registros para os filtros informados!", ""));
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
				totalLiquido = totalLiquido.add(parcelas.getValorLiquido());
			}
		}
		
		return totalLiquido;
	}
	
	public BigDecimal getTotalLiquidoInvestidorSA(long idInvestidor) {
		BigDecimal totalLiquido = BigDecimal.ZERO;
		
		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorSA) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				totalLiquido = totalLiquido.add(parcelas.getValorLiquido());
			}
		}
		
		return totalLiquido;
	}
	
	public BigDecimal getTotalLiquidoInvestidorCorrespondente(long idInvestidor) {
		BigDecimal totalLiquido = BigDecimal.ZERO;
		
		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorCorrespondente) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				totalLiquido = totalLiquido.add(parcelas.getValorLiquido());
			}
		}
		
		return totalLiquido;
	}
	
	public BigDecimal getTotalLiquidoInvestidorEnvelope(long idInvestidor) {
		BigDecimal totalLiquido = BigDecimal.ZERO;
		
		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorEnvelope) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				totalLiquido = totalLiquido.add(parcelas.getValorLiquido());
			}
		}
		
		return totalLiquido;
	}
	
	public BigDecimal getTotalLiquidoTodosInvestidores() {
		BigDecimal totalLiquido = BigDecimal.ZERO;
		
		if (this.parcelasInvestidor != null) { 
			for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidor) {
				if (parcelas.getInvestidor().getId() != 14 && 
						parcelas.getInvestidor().getId() != 15 &&
								parcelas.getInvestidor().getId() != 34) {
					totalLiquido = totalLiquido.add(parcelas.getValorLiquido());
				}			
			}
		}
		
		return totalLiquido;
	}
	
	public BigDecimal getTotalLiquidoTodosInvestidoresCorrespondente() {
		BigDecimal totalLiquido = BigDecimal.ZERO;
		
		if (this.parcelasInvestidorCorrespondente != null) { 
			for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorCorrespondente) {
				if (parcelas.getInvestidor().getId() != 14 && 
						parcelas.getInvestidor().getId() != 15 &&
								parcelas.getInvestidor().getId() != 34) {
					totalLiquido = totalLiquido.add(parcelas.getValorLiquido());
				}			
			}
		}
		
		return totalLiquido;
	}
	
	public BigDecimal getTotalLiquidoTodosInvestidoresSA() {
		BigDecimal totalLiquido = BigDecimal.ZERO;
		
		if (this.parcelasInvestidorSA != null) { 
			for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorSA) {
				if (parcelas.getInvestidor().getId() != 14 && 
						parcelas.getInvestidor().getId() != 15 &&
								parcelas.getInvestidor().getId() != 34) {
					totalLiquido = totalLiquido.add(parcelas.getValorLiquido());
				}			
			}
		}
		
		return totalLiquido;
	}
	
	public BigDecimal getTotalLiquidoTodosInvestidoresEnvelope() {
		BigDecimal totalLiquido = BigDecimal.ZERO;
		
		if (this.parcelasInvestidorEnvelope != null) { 
			for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorEnvelope) {
				if (parcelas.getInvestidor().getId() != 14 && 
						parcelas.getInvestidor().getId() != 15 &&
								parcelas.getInvestidor().getId() != 34) {
					totalLiquido = totalLiquido.add(parcelas.getValorLiquido());
				}			
			}
		}
		
		return totalLiquido;
	}
	
	
	public BigDecimal getTotalParcelaInvestidor(long idInvestidor) {
		BigDecimal totalParcela= BigDecimal.ZERO;
		
		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidor) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				totalParcela = totalParcela.add(parcelas.getParcelaMensal());
			}
		}
		
		return totalParcela;
	}
	
	public BigDecimal getTotalParcelaInvestidorSA(long idInvestidor) {
		BigDecimal totalParcela= BigDecimal.ZERO;
		
		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorSA) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				totalParcela = totalParcela.add(parcelas.getParcelaMensal());
			}
		}
		
		return totalParcela;
	}
	
	public BigDecimal getTotalParcelaInvestidorCorrespondente(long idInvestidor) {
		BigDecimal totalParcela= BigDecimal.ZERO;
		
		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorCorrespondente) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				totalParcela = totalParcela.add(parcelas.getParcelaMensal());
			}
		}
		
		return totalParcela;
	}
	
	public BigDecimal getTotalParcelaInvestidorEnvelope(long idInvestidor) {
		BigDecimal totalParcela= BigDecimal.ZERO;
		
		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorEnvelope) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				totalParcela = totalParcela.add(parcelas.getParcelaMensal());
			}
		}
		
		return totalParcela;
	}
	
	public BigDecimal getTotalParcelaTodosInvestidores() {
		BigDecimal totalParcela = BigDecimal.ZERO;
		
		if (this.parcelasInvestidor != null) { 
			for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidor) {
				if (parcelas.getInvestidor().getId() != 14 && 
						parcelas.getInvestidor().getId() != 15 &&
								parcelas.getInvestidor().getId() != 34) {
					totalParcela = totalParcela.add(parcelas.getParcelaMensal());
				}			
			}
		}
		
		return totalParcela;
	}
	
	public BigDecimal getTotalParcelaTodosInvestidoresEnvelope() {
		BigDecimal totalParcela = BigDecimal.ZERO;
		
		if (this.parcelasInvestidorEnvelope != null) { 
			for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidorEnvelope) {
				if (parcelas.getInvestidor().getId() != 14 && 
						parcelas.getInvestidor().getId() != 15 &&
								parcelas.getInvestidor().getId() != 34) {
					totalParcela = totalParcela.add(parcelas.getParcelaMensal());
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
					totalIRRetido = totalIRRetido.add(parcelas.getIrRetido());	
				}
				
			}
		}
		
		return totalIRRetido;
	}
	
	public BigDecimal getTotalJurosInvestidor(long idInvestidor) {
		BigDecimal totalJuros = BigDecimal.ZERO;
		
		for (ContratoCobrancaParcelasInvestidor parcelas : this.parcelasInvestidor) {
			if (parcelas.getInvestidor().getId() == idInvestidor) {
				if (parcelas.getJuros() != null) {
					totalJuros = totalJuros.add(parcelas.getJuros());	
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
		this.qtdeContratos = 0;
		this.parcelasAbertas = 0;
		this.valorReceber = BigDecimal.ZERO;
		this.valorRecebido = BigDecimal.ZERO;
		this.valorInvestidor = BigDecimal.ZERO;
		this.valorInvestido = BigDecimal.ZERO;	
		this.idInvestidor = 0;
		this.files = new ArrayList<FileUploaded>();
		this.posicaoInvestidorNoContrato = 0;
		
		this.usuarioLogado = new User();
		
		this.contratos = new ArrayList<ContratoCobranca>();
		this.selectedContratoCobrancaDetalhes = new ContratoCobrancaDetalhes();
	}
	
	public void getContratosInvestidor() {
		clearFields();
		this.usuarioLogado = getUsuarioLogado();
		
		if (usuarioLogado != null) {
			// Busca o cadastro do Recebedor pelo ID do Usuário Logado
			PagadorRecebedorDao prDao = new PagadorRecebedorDao();
			PagadorRecebedor pr = new PagadorRecebedor();
			pr = prDao.getRecebedorByUsuarioInvestidor(usuarioLogado.getId());
			
			if (pr.getId() > 0) {
				this.idInvestidor = pr.getId();
				
				// get contratos por investidor
				ContratoCobrancaDao contratoDao = new ContratoCobrancaDao();
				this.contratos = contratoDao.getContratosPorInvestidor(this.idInvestidor);
				
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
	
	// calcula quantidade de parcelas abertas
	public void getCardsDashboards() {
		this.qtdeContratos = this.contratos.size();
		
		this.valorInvestido = BigDecimal.ZERO;
		BigDecimal valorInvestidoContrato = BigDecimal.ZERO;
		
		for (ContratoCobranca c : this.contratos) {
			// busca o valor do investidor no contrato
			getInformacoesDoInvestidorNoContrato(c);			
			
			//buscaPosicaoInvestidorNoContrato(c);
			
			// retorna a posicao de investidores que não são envelope
			buscaPosicaoInvestidorNoContratoSemEnvelope(c);
			
			valorInvestidoContrato = BigDecimal.ZERO;
			
			// busca valor investido nos contratos
			if (this.posicaoInvestidorNoContrato == 1) {
				if (!c.isRecebedorEnvelope()) {
					valorInvestidoContrato = BigDecimal.ZERO;
					for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor1()) {
						if (cd.isBaixado()) {
							valorInvestidoContrato = cd.getSaldoCredorAtualizado();
						} else {
							break;
						}
					}
					
					if (valorInvestidoContrato.compareTo(BigDecimal.ZERO) == 0) {
						if (c.getListContratoCobrancaParcelasInvestidor1().size() > 0) {
							valorInvestidoContrato = c.getListContratoCobrancaParcelasInvestidor1().get(0).getSaldoCredorAtualizado();
						}
					}
				}			
			}
			
			if (this.posicaoInvestidorNoContrato == 2) {
				if (!c.isRecebedorEnvelope2()) {
					valorInvestidoContrato = BigDecimal.ZERO;
					for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor2()) {
						if (cd.isBaixado()) {
							valorInvestidoContrato = cd.getSaldoCredorAtualizado();
						} else {
							break;
						}
					}
					
					if (valorInvestidoContrato.compareTo(BigDecimal.ZERO) == 0) {
						if (c.getListContratoCobrancaParcelasInvestidor2().size() > 0) {
							valorInvestidoContrato = c.getListContratoCobrancaParcelasInvestidor2().get(0).getSaldoCredorAtualizado();
						}
					}
				}			
			}
			
			if (this.posicaoInvestidorNoContrato == 3) {
				if (!c.isRecebedorEnvelope3()) {
					valorInvestidoContrato = BigDecimal.ZERO;
					for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor3()) {
						if (cd.isBaixado()) {
							valorInvestidoContrato = cd.getSaldoCredorAtualizado();
						} else {
							break;
						}
					}
					
					if (valorInvestidoContrato.compareTo(BigDecimal.ZERO) == 0) {
						if (c.getListContratoCobrancaParcelasInvestidor3().size() > 0) {
							valorInvestidoContrato = c.getListContratoCobrancaParcelasInvestidor3().get(0).getSaldoCredorAtualizado();
						}
					}
				}			
			}
			
			if (this.posicaoInvestidorNoContrato == 4) {
				if (!c.isRecebedorEnvelope4()) {
					valorInvestidoContrato = BigDecimal.ZERO;
					for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor4()) {
						if (cd.isBaixado()) {
							valorInvestidoContrato = cd.getSaldoCredorAtualizado();
						} else {
							break;
						}
					}
					
					if (valorInvestidoContrato.compareTo(BigDecimal.ZERO) == 0) {
						if (c.getListContratoCobrancaParcelasInvestidor4().size() > 0) {
							valorInvestidoContrato = c.getListContratoCobrancaParcelasInvestidor4().get(0).getSaldoCredorAtualizado();
						}
					}
				}			
			}
			
			if (this.posicaoInvestidorNoContrato == 5) {
				if (!c.isRecebedorEnvelope5()) {
					valorInvestidoContrato = BigDecimal.ZERO;
					for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor5()) {
						if (cd.isBaixado()) {
							valorInvestidoContrato = cd.getSaldoCredorAtualizado();
						} else {
							break;
						}
					}
					
					if (valorInvestidoContrato.compareTo(BigDecimal.ZERO) == 0) {
						if (c.getListContratoCobrancaParcelasInvestidor5().size() > 0) {
							valorInvestidoContrato = c.getListContratoCobrancaParcelasInvestidor5().get(0).getSaldoCredorAtualizado();
						}
					}
				}			
			}
			
			if (this.posicaoInvestidorNoContrato == 6) {
				if (!c.isRecebedorEnvelope6()) {
					valorInvestidoContrato = BigDecimal.ZERO;
					for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor6()) {
						if (cd.isBaixado()) {
							valorInvestidoContrato = cd.getSaldoCredorAtualizado();
						} else {
							break;
						}
					}
					
					if (valorInvestidoContrato.compareTo(BigDecimal.ZERO) == 0) {
						if (c.getListContratoCobrancaParcelasInvestidor6().size() > 0) {
							valorInvestidoContrato = c.getListContratoCobrancaParcelasInvestidor6().get(0).getSaldoCredorAtualizado();
						}
					}
				}			
			}
			
			if (this.posicaoInvestidorNoContrato == 7) {
				if (!c.isRecebedorEnvelope7()) {
					valorInvestidoContrato = BigDecimal.ZERO;
					for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor7()) {
						if (cd.isBaixado()) {
							valorInvestidoContrato = cd.getSaldoCredorAtualizado();
						} else {
							break;
						}
					}
					
					if (valorInvestidoContrato.compareTo(BigDecimal.ZERO) == 0) {
						if (c.getListContratoCobrancaParcelasInvestidor7().size() > 0) {
							valorInvestidoContrato = c.getListContratoCobrancaParcelasInvestidor7().get(0).getSaldoCredorAtualizado();
						}
					}
				}			
			}
			
			if (this.posicaoInvestidorNoContrato == 8) {
				if (!c.isRecebedorEnvelope8()) {
					valorInvestidoContrato = BigDecimal.ZERO;
					for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor8()) {
						if (cd.isBaixado()) {
							valorInvestidoContrato = cd.getSaldoCredorAtualizado();
						} else {
							break;
						}
					}
					
					if (valorInvestidoContrato.compareTo(BigDecimal.ZERO) == 0) {
						if (c.getListContratoCobrancaParcelasInvestidor8().size() > 0) {
							valorInvestidoContrato = c.getListContratoCobrancaParcelasInvestidor8().get(0).getSaldoCredorAtualizado();
						}
					}
				}			
			}
			
			if (this.posicaoInvestidorNoContrato == 9) {
				if (!c.isRecebedorEnvelope9()) {
					valorInvestidoContrato = BigDecimal.ZERO;
					for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor9()) {
						if (cd.isBaixado()) {
							valorInvestidoContrato = cd.getSaldoCredorAtualizado();
						} else {
							break;
						}
					}
					
					if (valorInvestidoContrato.compareTo(BigDecimal.ZERO) == 0) {
						if (c.getListContratoCobrancaParcelasInvestidor9().size() > 0) {
							valorInvestidoContrato = c.getListContratoCobrancaParcelasInvestidor9().get(0).getSaldoCredorAtualizado();
						}
					}
				}			
			}
			
			if (this.posicaoInvestidorNoContrato == 10) {
				if (!c.isRecebedorEnvelope10()) {
					valorInvestidoContrato = BigDecimal.ZERO;
					for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor10()) {
						if (cd.isBaixado()) {
							valorInvestidoContrato = cd.getSaldoCredorAtualizado();
						} else {
							break;
						}
					}
					
					if (valorInvestidoContrato.compareTo(BigDecimal.ZERO) == 0) {
						if (c.getListContratoCobrancaParcelasInvestidor10().size() > 0) {
							valorInvestidoContrato = c.getListContratoCobrancaParcelasInvestidor10().get(0).getSaldoCredorAtualizado();
						}
					}
				}			
			}
			
			// Atribui o valor investido no contrato em questão a variavel global			
			this.valorInvestido = this.valorInvestido.add(valorInvestidoContrato);
			
			// busca valor investido, sendo o saldo credor da primeira parcela não paga
			//valorInvestidoContrato = valorInvestidoContrato.add(getValorInvestidoNoContrato(c));
			/*
			if (this.posicaoInvestidorNoContrato == 1) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor1()) {
					if (cd.isBaixado()) { 		
						valorInvestidoContrato = BigDecimal.ZERO;
						valorInvestidoContrato = valorInvestidoContrato.add(cd.getSaldoCredorAtualizado());
					}
				}				
			}
				
			if (this.posicaoInvestidorNoContrato == 2) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor2()) {
					if (cd.isBaixado()) { 				
						valorInvestidoContrato = BigDecimal.ZERO;
						valorInvestidoContrato = valorInvestidoContrato.add(cd.getSaldoCredorAtualizado());
					}
				}
			}

			if (this.posicaoInvestidorNoContrato == 3) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor3()) {
					if (cd.isBaixado()) { 		
						valorInvestidoContrato = BigDecimal.ZERO;
						valorInvestidoContrato = valorInvestidoContrato.add(cd.getSaldoCredorAtualizado());
					}
				}
			}
				
			if (this.posicaoInvestidorNoContrato == 4) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor4()) {
					if (cd.isBaixado()) { 		
						valorInvestidoContrato = BigDecimal.ZERO;
						valorInvestidoContrato = valorInvestidoContrato.add(cd.getSaldoCredorAtualizado());
					}
				}
			}
				
			if (this.posicaoInvestidorNoContrato == 5) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor5()) {
					if (cd.isBaixado()) { 		
						valorInvestidoContrato = BigDecimal.ZERO;
						valorInvestidoContrato = valorInvestidoContrato.add(cd.getSaldoCredorAtualizado());
					}
				}
			}
				
			if (this.posicaoInvestidorNoContrato == 6) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor6()) {
					if (cd.isBaixado()) { 				
						valorInvestidoContrato = BigDecimal.ZERO;
						valorInvestidoContrato = valorInvestidoContrato.add(cd.getSaldoCredorAtualizado());
					}
				}
			}		
			
			if (this.posicaoInvestidorNoContrato == 7) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor7()) {
					if (cd.isBaixado()) { 				
						valorInvestidoContrato = BigDecimal.ZERO;
						valorInvestidoContrato = valorInvestidoContrato.add(cd.getSaldoCredorAtualizado());
					}
				}
			}
				
			if (this.posicaoInvestidorNoContrato == 8) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor8()) {
					if (cd.isBaixado()) { 		
						valorInvestidoContrato = BigDecimal.ZERO;
						valorInvestidoContrato = valorInvestidoContrato.add(cd.getSaldoCredorAtualizado());
					}
				}
			}	
			
			if (this.posicaoInvestidorNoContrato == 9) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor9()) {
					if (cd.isBaixado()) { 		
						valorInvestidoContrato = BigDecimal.ZERO;
						valorInvestidoContrato = valorInvestidoContrato.add(cd.getSaldoCredorAtualizado());
					}
				}
			}
				
			if (this.posicaoInvestidorNoContrato == 10) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor10()) {
					if (cd.isBaixado()) { 		
						valorInvestidoContrato = BigDecimal.ZERO;
						valorInvestidoContrato = valorInvestidoContrato.add(cd.getSaldoCredorAtualizado());
					}
				}
			}	
			*/
			
			// Atribui o valor investido no contrato em questão a variavel global			
			//this.valorInvestido = this.valorInvestido.add(valorInvestidoContrato);
				
			// busca valores a receber e pagos
			if (this.posicaoInvestidorNoContrato == 1) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor1()) {
					if (!cd.isBaixado()) { 					
						// se parcela paga em aberto, soma qtde de parcelas e valor em aberto
						this.parcelasAbertas = this.parcelasAbertas + 1;
						
						// soma valor a receber
						this.valorReceber = valorReceber.add(cd.getValorLiquido());
					} else {
						// se parcela paga soma o valor recebido pelo investidor
						// soma valor a receber
						this.valorRecebido = valorRecebido.add(cd.getValorLiquido());
					}					
				}
			}
			
			if (this.posicaoInvestidorNoContrato == 2) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor2()) {
					if (!cd.isBaixado()) { 					
						// se parcela paga em aberto, soma qtde de parcelas e valor em aberto
						this.parcelasAbertas = this.parcelasAbertas + 1;
						
						// soma valor a receber
						this.valorReceber = valorReceber.add(cd.getValorLiquido());
					} else {
						// se parcela paga soma o valor recebido pelo investidor
						// soma valor a receber
						this.valorRecebido = valorRecebido.add(cd.getValorLiquido());
					}					
				}
			}
			
			if (this.posicaoInvestidorNoContrato == 3) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor3()) {
					if (!cd.isBaixado()) { 					
						// se parcela paga em aberto, soma qtde de parcelas e valor em aberto
						this.parcelasAbertas = this.parcelasAbertas + 1;
						
						// soma valor a receber
						this.valorReceber = valorReceber.add(cd.getValorLiquido());
					} else {
						// se parcela paga soma o valor recebido pelo investidor
						// soma valor a receber
						this.valorRecebido = valorRecebido.add(cd.getValorLiquido());
					}					
				}
			}
			
			if (this.posicaoInvestidorNoContrato == 4) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor4()) {
					if (!cd.isBaixado()) { 					
						// se parcela paga em aberto, soma qtde de parcelas e valor em aberto
						this.parcelasAbertas = this.parcelasAbertas + 1;
						
						// soma valor a receber
						this.valorReceber = valorReceber.add(cd.getValorLiquido());
					} else {
						// se parcela paga soma o valor recebido pelo investidor
						// soma valor a receber
						this.valorRecebido = valorRecebido.add(cd.getValorLiquido());
					}					
				}
			}
			
			if (this.posicaoInvestidorNoContrato == 5) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor5()) {
					if (!cd.isBaixado()) { 					
						// se parcela paga em aberto, soma qtde de parcelas e valor em aberto
						this.parcelasAbertas = this.parcelasAbertas + 1;
						
						// soma valor a receber
						this.valorReceber = valorReceber.add(cd.getValorLiquido());
					} else {
						// se parcela paga soma o valor recebido pelo investidor
						// soma valor a receber
						this.valorRecebido = valorRecebido.add(cd.getValorLiquido());
					}					
				}
			}
			
			if (this.posicaoInvestidorNoContrato == 6) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor6()) {
					if (!cd.isBaixado()) { 					
						// se parcela paga em aberto, soma qtde de parcelas e valor em aberto
						this.parcelasAbertas = this.parcelasAbertas + 1;
						
						// soma valor a receber
						this.valorReceber = valorReceber.add(cd.getValorLiquido());
					} else {
						// se parcela paga soma o valor recebido pelo investidor
						// soma valor a receber
						this.valorRecebido = valorRecebido.add(cd.getValorLiquido());
					}					
				}
			}
			
			if (this.posicaoInvestidorNoContrato == 7) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor7()) {
					if (!cd.isBaixado()) { 					
						// se parcela paga em aberto, soma qtde de parcelas e valor em aberto
						this.parcelasAbertas = this.parcelasAbertas + 1;
						
						// soma valor a receber
						this.valorReceber = valorReceber.add(cd.getValorLiquido());
					} else {
						// se parcela paga soma o valor recebido pelo investidor
						// soma valor a receber
						this.valorRecebido = valorRecebido.add(cd.getValorLiquido());
					}					
				}
			}
			
			if (this.posicaoInvestidorNoContrato == 8) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor8()) {
					if (!cd.isBaixado()) { 					
						// se parcela paga em aberto, soma qtde de parcelas e valor em aberto
						this.parcelasAbertas = this.parcelasAbertas + 1;
						
						// soma valor a receber
						this.valorReceber = valorReceber.add(cd.getValorLiquido());
					} else {
						// se parcela paga soma o valor recebido pelo investidor
						// soma valor a receber
						this.valorRecebido = valorRecebido.add(cd.getValorLiquido());
					}					
				}
			}
			
			if (this.posicaoInvestidorNoContrato == 9) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor9()) {
					if (!cd.isBaixado()) { 					
						// se parcela paga em aberto, soma qtde de parcelas e valor em aberto
						this.parcelasAbertas = this.parcelasAbertas + 1;
						
						// soma valor a receber
						this.valorReceber = valorReceber.add(cd.getValorLiquido());
					} else {
						// se parcela paga soma o valor recebido pelo investidor
						// soma valor a receber
						this.valorRecebido = valorRecebido.add(cd.getValorLiquido());
					}					
				}
			}
			
			if (this.posicaoInvestidorNoContrato == 10) {
				for (ContratoCobrancaParcelasInvestidor cd : c.getListContratoCobrancaParcelasInvestidor10()) {
					if (!cd.isBaixado()) { 					
						// se parcela paga em aberto, soma qtde de parcelas e valor em aberto
						this.parcelasAbertas = this.parcelasAbertas + 1;
						
						// soma valor a receber
						this.valorReceber = valorReceber.add(cd.getValorLiquido());
					} else {
						// se parcela paga soma o valor recebido pelo investidor
						// soma valor a receber
						this.valorRecebido = valorRecebido.add(cd.getValorLiquido());
					}					
				}
			}
		}
	}

	/***
	 * Lista ois arquivos contidos no diretório
	 * @return
	 */
	public Collection<FileUploaded> listaArquivos(ContratoCobranca contrato) {
		//DateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");
		ParametrosDao pDao = new ParametrosDao(); 
		String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString() + contrato.getNumeroContrato() + "/";
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
	
	public void getInformacoesDoInvestidorNoContrato(ContratoCobranca contrato) {		
		this.selectedContrato = contrato;
		
		// get valor do investidor no contrato
		if (contrato.getRecebedor() != null) {
			if (contrato.getRecebedor().getId() == this.idInvestidor) {
				this.valorInvestidor = contrato.getVlrRecebedor();
				
				if (contrato.isRecebedorEnvelope()) {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(this.selectedContrato.getListContratoCobrancaParcelasInvestidor1());
				} else {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(this.selectedContrato.getListContratoCobrancaParcelasInvestidor1());	
				}
			}			
		}
		
		if (contrato.getRecebedor2() != null) {
			if (contrato.getRecebedor2().getId() == this.idInvestidor) {
				this.valorInvestidor = contrato.getVlrRecebedor2();

				if (contrato.isRecebedorEnvelope2()) {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(this.selectedContrato.getListContratoCobrancaParcelasInvestidor2());
				} else {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(this.selectedContrato.getListContratoCobrancaParcelasInvestidor2());	
				}
			}			
		}
		
		if (contrato.getRecebedor3() != null) {
			if (contrato.getRecebedor3().getId() == this.idInvestidor) {
				this.valorInvestidor = contrato.getVlrRecebedor3();

				if (contrato.isRecebedorEnvelope3()) {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(this.selectedContrato.getListContratoCobrancaParcelasInvestidor3());
				} else {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(this.selectedContrato.getListContratoCobrancaParcelasInvestidor3());	
				}
			}			
		}
		
		if (contrato.getRecebedor4() != null) {
			if (contrato.getRecebedor4().getId() == this.idInvestidor) {
				this.valorInvestidor = contrato.getVlrRecebedor4();

				if (contrato.isRecebedorEnvelope4()) {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(this.selectedContrato.getListContratoCobrancaParcelasInvestidor4());
				} else {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(this.selectedContrato.getListContratoCobrancaParcelasInvestidor4());	
				}
			}			
		}
		
		if (contrato.getRecebedor5() != null) {
			if (contrato.getRecebedor5().getId() == this.idInvestidor) {
				this.valorInvestidor = contrato.getVlrRecebedor5();

				if (contrato.isRecebedorEnvelope5()) {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(this.selectedContrato.getListContratoCobrancaParcelasInvestidor5());
				} else {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(this.selectedContrato.getListContratoCobrancaParcelasInvestidor5());	
				}
			}			
		}
		
		if (contrato.getRecebedor6() != null) {
			if (contrato.getRecebedor6().getId() == this.idInvestidor) {
				this.valorInvestidor = contrato.getVlrRecebedor6();

				if (contrato.isRecebedorEnvelope6()) {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(this.selectedContrato.getListContratoCobrancaParcelasInvestidor6());
				} else {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(this.selectedContrato.getListContratoCobrancaParcelasInvestidor6());	
				}
			}			
		}
		
		if (contrato.getRecebedor7() != null) {
			if (contrato.getRecebedor7().getId() == this.idInvestidor) {
				this.valorInvestidor = contrato.getVlrRecebedor7();

				if (contrato.isRecebedorEnvelope7()) {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(this.selectedContrato.getListContratoCobrancaParcelasInvestidor7());
				} else {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(this.selectedContrato.getListContratoCobrancaParcelasInvestidor7());	
				}
			}		
		}
		
		if (contrato.getRecebedor8() != null) {
			if (contrato.getRecebedor8().getId() == this.idInvestidor) {
				this.valorInvestidor = contrato.getVlrRecebedor8();

				if (contrato.isRecebedorEnvelope8()) {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(this.selectedContrato.getListContratoCobrancaParcelasInvestidor8());
				} else {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(this.selectedContrato.getListContratoCobrancaParcelasInvestidor8());	
				}
			}			
		}

		if (contrato.getRecebedor9() != null) {
			if (contrato.getRecebedor9().getId() == this.idInvestidor) {
				this.valorInvestidor = contrato.getVlrRecebedor9();

				if (contrato.isRecebedorEnvelope9()) {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(this.selectedContrato.getListContratoCobrancaParcelasInvestidor9());
				} else {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(this.selectedContrato.getListContratoCobrancaParcelasInvestidor9());	
				}
			}			
		}
		
		
		if (contrato.getRecebedor10() != null) {
			if (contrato.getRecebedor10().getId() == this.idInvestidor) {
				this.valorInvestidor = contrato.getVlrRecebedor10();

				if (contrato.isRecebedorEnvelope10()) {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionadoEnvelope(this.selectedContrato.getListContratoCobrancaParcelasInvestidor10());
				} else {
					this.selectedContrato.setListContratoCobrancaParcelasInvestidorSelecionado(this.selectedContrato.getListContratoCobrancaParcelasInvestidor10());	
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
	 * @return
	 */
	public StreamedContent getDownloadFile() {    
		if (this.selectedFile != null) {
			FileInputStream stream;
			try {
				stream = new FileInputStream(this.selectedFile.getFile().getAbsolutePath());
				downloadFile = new DefaultStreamedContent(stream, this.selectedFile.getPath(), this.selectedFile.getFile().getName());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("Cobrança - Download de Arquivos - Arquivo Não Encontrado");
			}			
		}	
		return this.downloadFile;
	}

	/***
	 * Exemplo de Zip de um Diretório inteiro
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
	 * @param path
	 * @param srcFile
	 * @param zip
	 * @throws Exception
	 */
	static private void addFileToZip(String path, String srcFile, ZipOutputStream zip)
			throws Exception {

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
	 * @param path
	 * @param srcFolder
	 * @param zip
	 * @throws Exception
	 */
	static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip)
			throws Exception {
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
    			String pathContrato = pDao.findByFilter("nome", "COBRANCA_DOCUMENTOS").get(0).getValorString() + contrato.getNumeroContrato() + "/" + fileName;
    			
    			/*
    			   	  'docx'  => 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
				  'xlsx'  => 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
				  'word'  => 'application/msword',
				  'xls'   => 'application/excel',
				  'pdf'   => 'application/pdf'
				  'psd'   => 'application/x-photoshop'
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
	        
	        response.setContentLength((int)arquivo.length());

	        response.setHeader("Content-disposition", "inline; filename=" +arquivo.getName());
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
	
	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();  
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}
	
	public void habilitaFiltrosContrato() {
		if (!this.updateMode) {
			if (this.filtraContrato) {
				clearContrato();
			} else {
				this.filtroNumeroContrato = "";
			}
			
			this.selectedContratoLov =  new ContratoCobranca();
		}
	}
	
	public final void loadContratos() {
		this.listContratos = new ArrayList<ContratoCobranca>();
		
		ContratoCobrancaDao contratoDao = new ContratoCobrancaDao();
		
		this.listContratos = contratoDao.findByFilter("status", "Aprovado");
	}
	
	
	public final void loadContratosByNumero(String numeroContrato) {
		List<ContratoCobranca> listContratosTmp = new ArrayList<ContratoCobranca>();
		
		ContratoCobrancaDao contratoDao = new ContratoCobrancaDao();
		
		Map<String,Object> filtros = new HashMap<String,Object>();
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
		this.operacoesIndividualizadoPDFGerado  =false;
		this.operacoesIndividualizadoPDFStr = ""; 
		
		//this.debenturesInvestidor = new DebenturesInvestidor();
		
		//this.totalValorFace = BigDecimal.ZERO.setScale(2);
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
			dataFimParcelas = contrato.getListContratoCobrancaDetalhes().get(contrato.getListContratoCobrancaDetalhes().size() - 1).getDataVencimento();
		}
		
		long prazoMedio = calculaDiasParcelas(dataInicioParcelas, dataFimParcelas);
		
		return prazoMedio ;
	}
	
	public long calculaDiasParcelas(Date dataInicioParcelas, Date dataFimParcelas) {
		long qtdeDias = Days.daysBetween(new DateTime(dataInicioParcelas), new DateTime(dataFimParcelas)).getDays();

		return qtdeDias ;
	}
	
	
	public void geraPDFInformeRendimentos() {
		/*
		this.transferenciasObservacoesIUGU = new TransferenciasObservacoesIUGU();
		this.transferenciasObservacoesIUGU.setId(1);
		this.transferenciasObservacoesIUGU.setIdTransferencia("jdsfhdsfhjskfhjhslafdshf");
		this.transferenciasObservacoesIUGU.setObservacao("asdklfhjksdhfjd dsjfhjhdsfjashgdfj ");

		this.valorItem = new BigDecimal("30000.00");
		 */
		DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");

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
			Font normal12 = new Font(FontFamily.HELVETICA, 12);
			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font tituloBranco = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloBranco.setColor(BaseColor.WHITE);
			Font normal = new Font(FontFamily.HELVETICA, 10);
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
			this.pathPDF = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();

			os = new FileOutputStream(this.pathPDF + this.nomePDF);  	

			// Associa a stream de saída ao 
			PdfWriter.getInstance(doc, os);

			// Abre o documento
			doc.open();     			
			/*
			Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			p1.setAlignment(Element.ALIGN_CENTER);
			p1.setSpacingAfter(10);
			doc.add(p1);  	
			 */
			PdfPTable table = new PdfPTable(new float[] { 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f});
			table.setWidthPercentage(100.0f); 

			PdfPCell cell1 = new PdfPCell(new Phrase("Informe de Rendimentos", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Período: 01/01/" + this.anoBase + " a 31/12/" + this.anoBase, titulo));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(6);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Nome: ", titulo));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.selectedPagador.getNome(), header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(5);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("CPF: ", titulo));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(this.selectedPagador.getCpf(), header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(5);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Investimentos", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Razão Social", titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("CNPJ", titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Valor em 31/12/" + String.valueOf(Integer.valueOf(this.anoBase) - 1), titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Valor em 31/12/" + this.anoBase, titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("IR Retido", titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Juros Líquidos", titulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.GRAY);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.GRAY);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Galleria Finanças Securitizadora S.A.", normal));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("34.425.347/0001-06", normal));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("R$ " + df.format(this.valorInvestidorAnoAnterior), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("R$ " + df.format(this.valorInvestidorAnoAtual), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("R$ " + df.format(this.totalIRRetido), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("R$ " + df.format(this.totalJurosLiquido), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.GRAY);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.GRAY);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.GRAY);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
		
			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Informe de Rendimento: Este contrato está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Informe de Rendimento: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.irRetidoInvestidoresPDFGerado = true;

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
	
	public void gerarOperacoesIndividualizadoPDF() {
		DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.VGpT0_nF_h4
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
			doc = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);
			this.operacoesIndividualizadoPDFStr = "OperacoesIndividualizado.pdf";
			this.pathPDF = pDao.findByFilter("nome", "RECIBOS_IUGU").get(0).getValorString();

			os = new FileOutputStream(this.pathPDF + this.operacoesIndividualizadoPDFStr);  	

			// Associa a stream de saída ao 
			PdfWriter writer = PdfWriter.getInstance(doc, os);
			
			// adiciona cabeçalho e rodapé
			PDFCabecalhoRodape event = new PDFCabecalhoRodape();
			writer.setPageEvent(event);

			// Abre o documento
			doc.open();     			
			/*
			Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			p1.setAlignment(Element.ALIGN_CENTER);
			p1.setSpacingAfter(10);
			doc.add(p1);  	
			 */
			PdfPTable table = new PdfPTable(new float[] { 0.10f, 0.10f, 0.18f, 0.18f, 0.18f, 0.18f, 0.18f, 0.18f, 0.18f });
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
			cell1.setColspan(9);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Relatório de Operações - Individualizado", normal10));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(9);
			cell1.setPaddingBottom(5);
			cell1.setPaddingLeft(5);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.dataInicio) + " a " + sdfDataRel.format(this.dataFim), header10));
			cell1.setBorder(0);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(9);
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
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.GRAY);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cell1.setUseBorderPadding(true);
			table.addCell(cell1);
			
			for (OperacoesIndividualizado oi : this.listOperacoesIndividualizado) {
				cell1 = new PdfPCell(new Phrase(oi.getContrato().getNumeroContrato() , normal8));
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
				
				cell1 = new PdfPCell(new Phrase("R$ " + df.format(oi.getDesagio()),normal8));
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
				
				cell1 = new PdfPCell(new Phrase("R$ " + df.format(oi.getValorLiquido()),normal8));
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
				
				cell1 = new PdfPCell(new Phrase("R$ " + df.format(oi.getValorLiquido()),normal8));
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
			
			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Detalhamento Novas Operações: Este documento está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Detalhamento Novas Operações: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.operacoesIndividualizadoPDFGerado = true;

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
	
	public final String clearFieldsTitulosQuitados() {
		this.dataInicio = gerarDataHoje(); 
		this.dataFim = gerarDataHoje();
		
		clearTitulosQuitadosPDFParams();	

		return "/Atendimento/Cobranca/TitulosQuitadosConsultar.xhtml";
	}
	
	public void clearTitulosQuitadosPDFParams() {
		this.titulosQuitadosPDF = null;
		this.titulosQuitadosPDFGerado  =false;
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
		DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.VGpT0_nF_h4
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
			doc = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);
			this.titulosQuitadosPDFStr = "TitulosQuitados.pdf";
			this.pathPDF = pDao.findByFilter("nome", "RECIBOS_IUGU").get(0).getValorString();

			os = new FileOutputStream(this.pathPDF + this.titulosQuitadosPDFStr);  	

			// Associa a stream de saída ao 
			PdfWriter writer = PdfWriter.getInstance(doc, os);
			
			// adiciona cabeçalho e rodapé
			PDFCabecalhoRodape event = new PDFCabecalhoRodape();
			writer.setPageEvent(event);

			// Abre o documento
			doc.open();     			
			/*
			Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			p1.setAlignment(Element.ALIGN_CENTER);
			p1.setSpacingAfter(10);
			doc.add(p1);  	
			 */
			PdfPTable table = new PdfPTable(new float[] { 0.10f, 0.10f, 0.18f, 0.18f, 0.18f, 0.18f, 0.18f, 0.18f, 0.18f, 0.18f, 0.18f });
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
			
			cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.dataInicio) + " a " + sdfDataRel.format(this.dataFim), header10));
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
				cell1 = new PdfPCell(new Phrase(db.getNumeroCautela() , normal8));
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
				
				cell1 = new PdfPCell(new Phrase("R$ " + df.format(db.getValorFace()),normal8));
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

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Debêntures Emitidas: Este documento está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Debêntures Emitidas: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.titulosQuitadosPDFGerado = true;

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
		this.listPagadores = prDao.findAll();
		
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
				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Seleção de Contrato: O contrato informado não foi localizado, verifique e tente novamente!", ""));
				
			} else {
				if (this.selectedContratoLov.getId() <= 0) {
					validaContrato = false;
					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Seleção de Contrato: O contrato informado não foi localizado, verifique e tente novamente!", ""));
				}
			}
		}
		
		// valida se o pagador foi selecionado
		if (this.selectedPagador == null) {
			validaContrato = false;
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Seleção de Pagador: O pagador não foi selecionado, verifique e tente novamente!", ""));
			
		} else {
			if (this.selectedPagador.getId() <= 0) {
				validaContrato = false;
				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Seleção de Pagador:  O pagador não foi selecionado, verifique e tente novamente!", ""));
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
				this.debenturesInvestidor.setContrato(this.selectedContratoLov);
			}
			
			if (this.debenturesInvestidor.getId() <= 0) {
				diDao.create(this.debenturesInvestidor);
				
				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, "Debêntures: O registro foi salvo com sucesso!", ""));
			} else {
				diDao.merge(this.debenturesInvestidor);
				
				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, "Debêntures: O registro foi salvo com sucesso!", ""));
			}
			
			this.listDebenturesInvestidor = diDao.findAll();
			
			return "/Atendimento/Cobranca/DebenturesConsultar.xhtml";
		}

		return "/Atendimento/Cobranca/DebenturesInserir.xhtml";
	}
	
	public void geraBS() {
		DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.VGpT0_nF_h4
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
			Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			p1.setAlignment(Element.ALIGN_CENTER);
			p1.setSpacingAfter(10);
			doc.add(p1);  	
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
			
			cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.debenturesInvestidor.getDataDebentures()), normal10));
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
			
			BigDecimal valorDebenture = new BigDecimal(this.debenturesInvestidor.getQtdeDebentures()).multiply(new BigDecimal(1000));
			
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
			pa.add(new Chunk(df.format(valorDebenture) + " na série 1/" + this.debenturesInvestidor.getSerie() + ".", normal10));
			
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
					endereco = this.debenturesInvestidor.getRecebedor().getEndereco() + ", " + this.debenturesInvestidor.getRecebedor().getNumero();
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
			
			cell1 = new PdfPCell(new Phrase("Preço Unitário de Subscrição Em " + sdfDataRel.format(this.debenturesInvestidor.getDataDebentures()), normal10));
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
			
			cell1 = new PdfPCell(new Phrase("Valor Total Integralizado Em " + sdfDataRel.format(this.debenturesInvestidor.getDataDebentures()), normal10));
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
			
			cell1 = new PdfPCell(new Phrase("1/" + this.debenturesInvestidor.getSerie() + " - " + this.debenturesInvestidor.getQtdeDebentures(), normal10));
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
			
			cell1 = new PdfPCell(new Phrase("1/" + this.debenturesInvestidor.getSerie() + " - " + this.debenturesInvestidor.getQtdeDebentures(), normal10));
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

			cell1 = new PdfPCell(new Phrase("Agência: " + this.debenturesInvestidor.getRecebedor().getAgencia() + " / CC: " + this.debenturesInvestidor.getRecebedor().getConta(), normal10));
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
			
			cell1 = new PdfPCell(new Phrase("A Galleria Finanças Securitizadora S.A certifica que recebeu a referida integralização no valor acima.", normal10));
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
			cell1.setPaddingRight(20f);	
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);	
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(2);
			cell1.setPaddingLeft(10);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("__________________________________________", normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(2);
			cell1.setPaddingLeft(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("João Augusto Magatti Alves", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);	
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(2);
			cell1.setPaddingLeft(10);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Fabricio Figueiredo", header10));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(2);
			cell1.setPaddingLeft(5);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Diretor Administrativo-Financeiro", normal10));
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
			cell1.setPaddingLeft(10);
			cell1.setPaddingBottom(10);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Diretor Administrativo-Financeiro", normal10));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setColspan(2);
			cell1.setPaddingLeft(5);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);
			cell1.setPaddingBottom(10);
			table.addCell(cell1);
			
			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "BS: Este documento está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "BS: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.bsPDFGerado = true;

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
	
	public void geraAnexo1() {
		DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.VGpT0_nF_h4
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
			Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			p1.setAlignment(Element.ALIGN_CENTER);
			p1.setSpacingAfter(10);
			doc.add(p1);  	
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
			
			cell1 = new PdfPCell(new Phrase("DO TERMO DE SECURITIZAÇÃO DA CAUTELA Nº " + this.debenturesInvestidor.getNumeroCautela(), header12));
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
				
				if (i == 20 || //borda inferior primeira página 
					i == sizeList || //borda inferior última página 
					i == 44 || //borda inferior segunda página
					i == 66) { //borda inferior terceira página (24 registros por página)
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
				
				if (i == 20 || //borda inferior primeira página 
						i == sizeList || //borda inferior última página 
						i == 44 || //borda inferior segunda página
						i == 66) { //borda inferior terceira página (24 registros por página)
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.GRAY);
				}
				
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.selectedContratoLov.getListContratoCobrancaDetalhes().get(i).getDataVencimento()), normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.GRAY);	
				
				if (i == 20 || //borda inferior primeira página 
						i == sizeList || //borda inferior última página 
						i == 44 || //borda inferior segunda página
						i == 66) { //borda inferior terceira página (24 registros por página)
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
				
				if (i == 20 || //borda inferior primeira página 
						i == sizeList || //borda inferior última página 
						i == 44 || //borda inferior segunda página
						i == 66) { //borda inferior terceira página (24 registros por página)
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
				
				if (i == 20 || //borda inferior primeira página 
						i == sizeList || //borda inferior última página 
						i == 44 || //borda inferior segunda página
						i == 66) { //borda inferior terceira página (24 registros por página)
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
				
				if (i == 20 || //borda inferior primeira página 
						i == sizeList || //borda inferior última página 
						i == 44 || //borda inferior segunda página
						i == 66) { //borda inferior terceira página (24 registros por página)
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
				
				if (i == 20 || //borda inferior primeira página 
						i == sizeList || //borda inferior última página 
						i == 44 || //borda inferior segunda página
						i == 66) { //borda inferior terceira página (24 registros por página)
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.GRAY);
				}
				
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);
				
				cell1 = new PdfPCell(new Phrase("R$ " + df.format(this.selectedContratoLov.getListContratoCobrancaDetalhes().get(i).getVlrParcela()), normal8));
				cell1.setBorder(0);
				cell1.setBorderWidthLeft(1);
				cell1.setBorderColorLeft(BaseColor.GRAY);
				cell1.setBorderWidthRight(1);
				cell1.setBorderColorRight(BaseColor.GRAY);	
				cell1.setBorderWidthTop(1);
				cell1.setBorderColorTop(BaseColor.GRAY);
				
				if (i == 20 || //borda inferior primeira página 
						i == sizeList || //borda inferior última página 
						i == 44 || //borda inferior segunda página
						i == 66) { //borda inferior terceira página (24 registros por página)
					cell1.setBorderWidthBottom(1);
					cell1.setBorderColorBottom(BaseColor.GRAY);
				}
				
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setUseBorderPadding(true);
				table.addCell(cell1);
				
				this.totalAnexoI = this.totalAnexoI.add(this.selectedContratoLov.getListContratoCobrancaDetalhes().get(i).getVlrParcela());
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
			
			//LINHA PORCENTAGEM
			if (this.debenturesInvestidor.getPorcentagemDebentures().compareTo(new BigDecimal((String) "100")) == -1) {
				BigDecimal ONE_HUNDRED = new BigDecimal(100);
				this.valorPorcentagemAnexoI = this.totalAnexoI.multiply(this.debenturesInvestidor.getPorcentagemDebentures()).divide(ONE_HUNDRED);
				
				cell1 = new PdfPCell(new Phrase("As debêntures acima têm como garantia o lastro correspondente a " + this.debenturesInvestidor.getPorcentagemDebentures() + "% dos Títulos acima.", normal8));
				cell1.setBorder(0);
				cell1.setPaddingTop(20f);
				cell1.setPaddingRight(20f);	
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setColspan(8);
				table.addCell(cell1);
			}
			
			cell1 = new PdfPCell(new Phrase("Galleria Finanças Securitizadora S.A.", header10));
			cell1.setBorder(0);
			cell1.setPaddingTop(40f);
			cell1.setPaddingRight(20f);	
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(8);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("______________________________________________", normal));
			cell1.setBorder(0);
			cell1.setPaddingRight(20f);	
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(40f);
			cell1.setColspan(8);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("João Augusto Magatti Alves", header10));
			cell1.setBorder(0);
			cell1.setPaddingRight(20f);	
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(8);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Diretor Administrativo-Financeiro", header10));
			cell1.setBorder(0);
			cell1.setPaddingRight(20f);	
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(8);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("______________________________________________", normal));
			cell1.setBorder(0);
			cell1.setPaddingRight(20f);	
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(40f);
			cell1.setColspan(8);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Fabricio Figueiredo", header10));
			cell1.setBorder(0);
			cell1.setPaddingRight(20f);	
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(8);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Diretor Administrativo-Financeiro", header10));
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
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Anexo I: Este documento está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Anexo I: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.anexo1PDFGerado = true;

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
	
	public void geraTermoSecuritizacao() {
		DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.VGpT0_nF_h4
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
			Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			p1.setAlignment(Element.ALIGN_CENTER);
			p1.setSpacingAfter(10);
			doc.add(p1);  	
			 */
			PdfPTable table = new PdfPTable(new float[] { 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f });
			table.setWidthPercentage(100.0f); 
			
			PdfPCell cell1 = new PdfPCell(new Phrase("TERMO DE SECURITIZAÇÃO DE RECEBÍVEIS EMPRESARIAIS MERCANTIS & INDUSTRIAIS", header));
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
			pa.add(new Chunk(", pessoa jurídica de direito privado, na forma de sociedade anônima de capital fechado, com sede e foro no município de Campinas, no endereço Avenida Doutor José Bonifácio Coutinho N, CEP 13091-611, inscrita no Cadastro Nacional de Pessoa Jurídica do Ministério da Fazenda sob n.o 34.425.347/0001-06, devidamente constituída e registrada na Junta Comercial do Estado de São Paulo, sob n° ED003063-6/000, neste ato representada, na forma de seu Estatuto Social, por seus Diretores Administrativo-Financeiro Joao Augusto Magatti Alves, Brasileiro, Casado(a), Empresário, residente e domiciliado na Cidade de Campinas, no endereço Avenida Doutor José Bonifácio Coutinho Nogueira, portador da Carteira de Identidade n° 50.630.711-6 e inscrito no CPF n° 436.821.448-03 e Fabricio Figueiredo, Brasileiro, Casado(a), Empresário, residente e domiciliado na Cidade de Sorocaba, no endereço Rua Elza Batista de Souza, portador da Carteira de Identidade n° 22.569.228-4 e inscrito no CPF n° 266.752.318-04, doravante denominada simplesmente ", normal));
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
			pa.add(new Chunk(", com fulcro no Princípio da Livre Iniciativa e no da Licitude, aliados à aplicação por analogia da Lei 9514/97, art. 8°, firma o presente ", normal));
			pa.add(new Chunk("Termo de Securitização de Recebíveis", header));
			pa.add(new Chunk(", doravante designado simplesmente ", normal));
			pa.add(new Chunk("TERMO", header));
			pa.add(new Chunk(", para aglutinar os créditos empresariais mercantis & industriais adquiridos e relacionados no(s) ANEXO(s), lastreando e vinculando tais créditos ao CERTIFICADO	DE RECEBÍVEIS, representado pelas Debêntures de sua 2ª emissão, especialmente à SÉRIE " + this.debenturesInvestidor.getSerie() + ", cujas características acham-se discriminadas na ", normal));
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
			
			cell1 = new PdfPCell(new Phrase("CLÁUSULA 2ª - DOS CRÉDITOS: ORIGEM, DATA DA CONSTITUIÇÃO E CARACTERÍSTICAS:", header));
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
			pa.add(new Chunk(" a partir de negócios de Cessão de Créditos realizados com diversos originadores (CEDENTES), com o objetivo de serem eles (os créditos) securitizados, os quais são oriundos de transações mercantis, imobiliárias e industriais a prazo, cujos dados dos respectivos títulos estão discriminados no(s) ANEXO(s) integrante(s) deste TERMO;", normal));
			
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
			
			cell1 = new PdfPCell(new Phrase("CLÁUSULA 3ª - DO VALOR NOMINAL DOS CRÉDITOS E DOS PAGAMENTOS DA SECURITIZADORA:", header));
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
				pa.add(new Chunk("O valor nominal e total dos créditos que lastreiam a presente emissão deve ser de R$ " + df.format(this.valorPorcentagemAnexoI) + " (" + valorPorExtenso.toString() + "). O valor decorrerá do resultado da somatória dos títulos elencados no ANEXO I e seus derivados. Tais créditos serão pagos pelos devedores diretamente à SECURITIZADORA , que, por sua vez, se responsabiliza perante o ", normal));
				pa.add(new Chunk("DEBENTURISTA", header));
				pa.add(new Chunk(" da série " + this.debenturesInvestidor.getSerie() + " da 2ª EMISSÃO ", normal));
				pa.add(new Chunk(this.selectedPagador.getNome(), header));
				pa.add(new Chunk(", através da liquidação dos títulos adquiridos e ocorridos quando do seu vencimento. À medida que os créditos elencados no ANEXO I atingirem as respectivas datas de vencimentos, novos títulos serão adquiridos com os valores havidos da liquidação dos primeiros, compondo estes novos a lista dos ANEXOS subsequentes, o mesmo se dando à medida que os últimos foram liquidados e dos valores da liquidação destes sirvam para adquirir novos títulos dando sequência até a liquidação das DEBÊNTURES das séries " + this.debenturesInvestidor.getSerie() + " da 1ª Emissão, todos integrantes deste TERMO, a ponto de não permitir que o total de créditos dos ANEXOS seja inferior ao valor das DEBÊNURES . Parágrafo único: Os anexos que relacionam os títulos servirão de lastro da presente emissão.", normal));
			} else {
				pa = new Paragraph();
				pa.add(new Chunk("A presente emissão não possui títulos lastreados. Os créditos serão pagos pela ", normal));
				pa.add(new Chunk("SECURITIZADORA", header));
				pa.add(new Chunk(", que, por sua vez, se responsabiliza perante o ", normal));
				pa.add(new Chunk("DEBENTURISTA", header));
				pa.add(new Chunk(" da série " + this.debenturesInvestidor.getSerie() + " da 2a EMISSÃO " + sdfDataRel.format(this.debenturesInvestidor.getDataDebentures()) + " (", normal));
				pa.add(new Chunk(this.selectedPagador.getNome(), header));
				pa.add(new Chunk("), através da liquidação dos títulos adquiridos e ocorridos quando do seu vencimento.", normal));
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
			
			cell1 = new PdfPCell(new Phrase("CLÁUSULA 4ª - DAS CARACTERÍSTICAS DA EMISSÃO DOS CERTIFICADOS DE RECEBÍVEIS", header));
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
			
			cell1 = new PdfPCell(new Phrase("A emissão privada das Debêntures, que consubstanciam o CERTIFICADO DE RECEBÍVEIS, observa as condições e características adiante descritas:", normal));
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
			pa.add(new Chunk("A(s) série(s) da(s) debênture(s) adquirida(s) são da 2ª Emissão Particular de Debêntures da ", normal));
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
			
			BigDecimal totalDebentures = new BigDecimal(1000).multiply(new BigDecimal(this.debenturesInvestidor.getQtdeDebentures()));
			valorPorExtenso.setNumber(totalDebentures);
			
			pa = new Paragraph();
			pa.add(new Chunk("f) ", header));
			pa.add(new Chunk("São adquiridas pelo ", normal));
			pa.add(new Chunk("DEBENTURISTA", header));
			pa.add(new Chunk(" " + this.debenturesInvestidor.getQtdeDebentures() + " (" + numeroPorExtenso.toString() + ") Debêntures da Série " + this.debenturesInvestidor.getSerie() + ", consubstanciando o CERTIFICADO DE RECEBÍVEIS, com valor nominal unitário de R$ 1.000,00 (um mil reais), totalizando R$ " + df.format(totalDebentures) + " (" + valorPorExtenso.toString() + ").", normal));
			
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
			pa.add(new Chunk("as debêntures adquiridas possuem prazo de vencimento pré-fixado, sendo que para as Debêntures de Série " + this.debenturesInvestidor.getSerie() + ", foi determinado o vencimento de " + sdfDataRel.format(this.debenturesInvestidor.getDataVencimento()) + ".", normal));
			
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
			pa.add(new Chunk("foi efetuada particularmente, sem intermediação de qualquer instituição ou agente do mercado, de acordo com as determinações inseridas no Parecer CVM/SJU/No 005-19.02.86, inexistindo reservas antecipadas, lotes mínimos ou máximos", header));
			pa.add(new Chunk(". A sua emissão foi autorizada pela Assembléia Geral Extraordinária dos Acionistas da SECURITIZADORA, datada de 12/08/2019, e sua emissão foi efetuada através de Escritura Pública de 2ª Emissão de Debêntures Subordinadas lavrada na Junta Comercial do Estado de São Paulo sob o n° ED003063-6/000", normal));
			
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
			pa.add(new Chunk("O valor nominal de subscrição das Debêntures está descrito no Quadro constante da CLÁUSULA 4ª e está lastreado pelo total dos créditos constante do ANEXO I. A integralização é à vista, em moeda corrente nacional, no ato da subscrição, sendo os valores integralizados pagos através de crédito em conta corrente bancária da ", normal));
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
			pa.add(new Chunk("Ocorrendo a mora por parte do(a,s) sacado(a,s) de qualquer título, ora securitizado, este responderá perante a SECURITIZADORA e, em solidariedade a ele(a,s), os devedores co-responsáveis: seu(s) avalista(s), endossante(s) e/ou demais coobrigados solidários, que serão obrigados a pagar o valor principal com os respectivos encargos, de conformidade com o estabelecido nos Contratos de Compromisso de Cessão de Créditos e outras avenças, celebrado entre a SECURITIZADORA e os originadores dos títulos negociados;", normal));
			
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
			pa.add(new Chunk("Parágrafo primeiro - RESPONSABILIDADE PELO(S) CRÉDITO(S) ADQUIRIDOS(S): muito embora as Debêntures terem sido emitidas pela SECURITIZADORA, lastreadas, oportunamente, nos títulos de crédito por ela avaliados, escolhidos e, por fim adquiridos, o risco do	negócio firmado e da garantia em que ele é lastreado, será suportado exclusivamente pelo DEBENTURISTA.", normal));
			
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
			pa.add(new Chunk("Parágrafo segundo – todas as despesas decorrentes de cobranças extrajudiciais e/ou judiciais para cumprimento das obrigações do(s) devedor(s) e seus corresponsáveis, bem como execução das respectivas garantias serão de responsabilidade do DEBENTURISTA, o qual também terá o direito em receber as receitas decorrentes dos encargos contratuais de acordo com o que firmado nos Contratos de Compromisso de Cessão de Créditos e outras avenças celebrado entre a SECURITIZADORA e os originadores dos títulos negociados.", normal));
			
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
			pa.add(new Chunk("Fica eleito o foro da comarca de Campinas/SP para dirimir quaisquer questões oriundas do presente ", normal));
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
			pa.add(new Chunk(" subscreve este documento para que possa,	então, ter por securitizados os créditos cedidos e constantes do(s) ANEXO(s) integrante(s) deste, bem como para tê-los como lastro do CERTIFICADO DE RECEBÍVEIS, representados por Debêntures acima referidas.", normal));
			
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
			
			cell1 = new PdfPCell(new Phrase("João Augusto Magatti Alves", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);	
			cell1.setPaddingRight(20f);	
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Diretor Administrativo-Financeiro", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);	
			cell1.setPaddingRight(20f);	
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
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
			cell1.setPaddingTop(40f);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Fabricio Figueiredo", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);	
			cell1.setPaddingRight(20f);	
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(6);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Diretor Administrativo-Financeiro", header));
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
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Termo de Securitização: Este documento está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Termo de Securitização: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.termoSecuritizacaoPDFGerado = true;

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

	public void geraDebenture() {
		DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.VGpT0_nF_h4
		 */ 		

		Document doc = null;
		OutputStream os = null;
		
		NumeroPorExtenso numeroPorExtenso = new NumeroPorExtenso();

		try {
			Font header = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			Font headerFullBold = new Font(FontFamily.HELVETICA, 14, Font.BOLD);
			Font headerFull = new Font(FontFamily.HELVETICA, 14);
			Font headerFullUnderline = new Font(FontFamily.HELVETICA, 14, Font.BOLD|Font.UNDERLINE);
			
			Font title = new Font(FontFamily.HELVETICA, 12);
			Font titleBold = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
			
			Font headerFullRed = new Font(FontFamily.HELVETICA, 16,Font.NORMAL, BaseColor.RED);
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
			Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			p1.setAlignment(Element.ALIGN_CENTER);
			p1.setSpacingAfter(10);
			doc.add(p1);  	
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
			pa.add(new Chunk(" 05/08/2019, com seus atos constitutivos arquivados na Junta Comercial do Estado do São Paulo em 02 de Setembro de 2019, sob o nº ED003063-6/000.", normal));
						
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
			pa.add(new Chunk(" A Sociedade tem por Objeto a aquisição e securitização de recebíveis comerciais e industriais. ", normal));
			
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
			
			cell1 = new PdfPCell(new Phrase(String.valueOf(this.debenturesInvestidor.getQtdeDebentures()), headerFullRedBold));
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
			
			BigDecimal valorDebenture = new BigDecimal(this.debenturesInvestidor.getQtdeDebentures()).multiply(new BigDecimal(1000));
			
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
			
			BigDecimal totalDebentures = new BigDecimal(1000).multiply(new BigDecimal(this.debenturesInvestidor.getQtdeDebentures()));
			ValorPorExtenso valorPorExtenso = new ValorPorExtenso(totalDebentures);
			
			cell1 = new PdfPCell(new Phrase("Esta cautela representa " + numeroPorExtenso.toString() + " debêntures, não conversíveis em ações, da 2ª (segunda) emissão privada, série " + this.debenturesInvestidor.getSerie() + ", no valor nominal unitário de R$ 1.000,00 (Um mil reais), totalizando R$ " + df.format(totalDebentures) + ", e demais características especificadas no instrumento particular de escritura da primeira emissão privada de debêntures simples da Galleria Finanças Securitizadora S.A., ficando disponível cópia autenticada na sede desta companhia. Confere a " + this.selectedPagador.getNome() + ", " + documento + ", os direitos que a lei e a escritura de emissão lhes asseguram.", header));
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
			
			cell1 = new PdfPCell(new Phrase("Campinas (SP), " + dia.format(this.debenturesInvestidor.getDataDebentures()) + " de " + mes.format(this.debenturesInvestidor.getDataDebentures()) + " de " + ano.format(this.debenturesInvestidor.getDataDebentures()), headerFull));
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
			
			cell1 = new PdfPCell(new Phrase("_______________________________________", normal));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(10f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(80f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("_______________________________________", normal));
			cell1.setBorder(0);
			cell1.setPaddingLeft(10f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(80f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Galleria Finanças Securitizadora S.A.", titleBold));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(10f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Galleria Finanças Securitizadora S.A.", titleBold));
			cell1.setBorder(0);
			cell1.setPaddingLeft(10f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Fabricio Figueiredo", title));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(10f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("João Augusto Magatti Alves", title));
			cell1.setBorder(0);
			cell1.setPaddingLeft(10f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(3);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Diretor Comercial", title));
			cell1.setBorder(0);
			cell1.setPaddingLeft(20f);
			cell1.setPaddingRight(10f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Diretor Administrativo", title));
			cell1.setBorder(0);
			cell1.setPaddingLeft(10f);
			cell1.setPaddingRight(20f);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Debênture: Este documento está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Debênture: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.debenturePDFGerado = true;

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
	
	public void geraPDFValorLiquidoInvestidores() {
		DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.VGpT0_nF_h4
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
			Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			p1.setAlignment(Element.ALIGN_CENTER);
			p1.setSpacingAfter(10);
			doc.add(p1);  	
			 */
			PdfPTable table = new PdfPTable(new float[] { 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f });
			table.setWidthPercentage(100.0f); 
			
			PdfPCell cell1 = new PdfPCell(new Phrase("GALLERIA FINANÇAS - Investidores - Valores Líquidos a Receber - " + sdfDataRel.format(this.dataInicio) + " a " +  sdfDataRel.format(this.dataFim), header));
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
						
						cell1 = new PdfPCell(new Phrase(df.format(getTotalParcelaInvestidorSA(investidorTemp.getId())), normal));
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
						
						cell1 = new PdfPCell(new Phrase(df.format(getTotalLiquidoInvestidorSA(investidorTemp.getId())), normal));
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
										
					cell1 = new PdfPCell(new Phrase(investidorTemp.getNome() , header));
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
						cell1 = new PdfPCell(new Phrase("CPF " + investidorTemp.getCpf() + " | Banco " + investidorTemp.getBanco() + " | AG." + investidorTemp.getAgencia() + " C/C " + investidorTemp.getConta(), titulo));
					} else {
						cell1 = new PdfPCell(new Phrase("CNPJ " + investidorTemp.getCnpj() + " | Banco " + investidorTemp.getBanco() + " | AG." + investidorTemp.getAgencia() + " C/C " + investidorTemp.getConta(), titulo));
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
					
					cell1 = new PdfPCell(new Phrase(df.format(getTotalParcelaInvestidorSA(investidorTemp.getId())), normal));
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
					
					cell1 = new PdfPCell(new Phrase(df.format(getTotalLiquidoInvestidorSA(investidorTemp.getId())), normal));
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
				
				cell1 = new PdfPCell(new Phrase("Valor Líquido Total: R$ " + df.format(getTotalLiquidoTodosInvestidoresSA()), header));
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
				cell1 = new PdfPCell(new Phrase("GALLERIA CORRESPONDENTE - Investidores - Valores Líquidos a Receber - " + sdfDataRel.format(this.dataInicio) + " a " +  sdfDataRel.format(this.dataFim), header));
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
						
						cell1 = new PdfPCell(new Phrase(df.format(getTotalParcelaInvestidorCorrespondente(investidorTemp.getId())), normal));
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
						
						cell1 = new PdfPCell(new Phrase(df.format(getTotalLiquidoInvestidorCorrespondente(investidorTemp.getId())), normal));
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
										
					cell1 = new PdfPCell(new Phrase(investidorTemp.getNome() , header));
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
					
					cell1 = new PdfPCell(new Phrase("CPF " + investidorTemp.getCpf() + " | Banco " + investidorTemp.getBanco() + " | AG." + investidorTemp.getAgencia() + " C/C " + investidorTemp.getConta(), titulo));
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
					
					cell1 = new PdfPCell(new Phrase(df.format(getTotalParcelaInvestidorCorrespondente(investidorTemp.getId())), normal));
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
					
					cell1 = new PdfPCell(new Phrase(df.format(getTotalLiquidoInvestidorCorrespondente(investidorTemp.getId())), normal));
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
				
				cell1 = new PdfPCell(new Phrase("Valor Líquido Total: R$ " + df.format(getTotalLiquidoTodosInvestidoresCorrespondente()), header));
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
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Investidores: Este documento está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Investidores: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.valoresLiquidosInvestidoresPDFGerado = true;

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
	
	public void geraPDFValorLiquidoInvestidoresEnvelope() {
		DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.VGpT0_nF_h4
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
			Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			p1.setAlignment(Element.ALIGN_CENTER);
			p1.setSpacingAfter(10);
			doc.add(p1);  	
			 */
			PdfPTable table = new PdfPTable(new float[] { 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f });
			table.setWidthPercentage(100.0f); 
			
			PdfPCell cell1 = new PdfPCell(new Phrase("Investidores - Valores Líquidos a Receber - Envelope - " + sdfDataRel.format(this.dataInicio) + " a " +  sdfDataRel.format(this.dataFim), header));
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
						
						cell1 = new PdfPCell(new Phrase(df.format(getTotalParcelaInvestidorEnvelope(investidorTemp.getId())), normal));
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
						
						cell1 = new PdfPCell(new Phrase(df.format(getTotalLiquidoInvestidorEnvelope(investidorTemp.getId())), normal));
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
										
					cell1 = new PdfPCell(new Phrase(investidorTemp.getNome() , header));
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
						cell1 = new PdfPCell(new Phrase("CPF " + investidorTemp.getCpf() + " | Banco " + investidorTemp.getBanco() + " | AG." + investidorTemp.getAgencia() + " C/C " + investidorTemp.getConta(), titulo));
					} else {
						cell1 = new PdfPCell(new Phrase("CNPJ " + investidorTemp.getCnpj() + " | Banco " + investidorTemp.getBanco() + " | AG." + investidorTemp.getAgencia() + " C/C " + investidorTemp.getConta(), titulo));
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
					
					cell1 = new PdfPCell(new Phrase(df.format(getTotalParcelaInvestidorEnvelope(investidorTemp.getId())), normal));
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
					
					cell1 = new PdfPCell(new Phrase(df.format(getTotalLiquidoInvestidorEnvelope(investidorTemp.getId())), normal));
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
				
				cell1 = new PdfPCell(new Phrase("Valor Líquido Total: R$ " + df.format(getTotalLiquidoTodosInvestidoresEnvelope()), header));
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
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Investidores: Este documento está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Investidores: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.valoresLiquidosInvestidoresPDFGerado = true;

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
	
	public void geraPDFIRRetidoInvestidores() {
		DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.VGpT0_nF_h4
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
			this.nomePDF = "Investidores - IR Retido.pdf";
			this.pathPDF = pDao.findByFilter("nome", "RECIBOS_IUGU").get(0).getValorString();

			os = new FileOutputStream(this.pathPDF + this.nomePDF);  	

			// Associa a stream de saída ao 
			PdfWriter.getInstance(doc, os);

			// Abre o documento
			doc.open();     			
			/*
			Paragraph p1 = new Paragraph("RECIBO DE PAGAMENTO - " + favorecido, titulo);
			p1.setAlignment(Element.ALIGN_CENTER);
			p1.setSpacingAfter(10);
			doc.add(p1);  	
			 */
			PdfPTable table = new PdfPTable(new float[] { 0.16f, 0.16f, 0.16f, 0.16f, 0.16f, 0.16f });
			table.setWidthPercentage(100.0f); 
			
			PdfPCell cell1 = new PdfPCell(new Phrase("Investidores - IR Retido - " + sdfDataRel.format(this.dataInicio) + " a " +  sdfDataRel.format(this.dataFim), header));
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
										
					cell1 = new PdfPCell(new Phrase(investidorTemp.getNome() + " | CPF " + investidorTemp.getCpf(), header));
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
				
				if (parcelas.getJuros() == null || parcelas.getJuros().toString().equals("0.00")) {
					cell1 = new PdfPCell(new Phrase("0,00", normal));
				} else {
					cell1 = new PdfPCell(new Phrase(df.format(parcelas.getJuros()), normal));
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

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Investidores: Este documento está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Investidores: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.irRetidoInvestidoresPDFGerado = true;

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
		return qtdeContratos;
	}

	public void setQtdeContratos(int qtdeContratos) {
		this.qtdeContratos = qtdeContratos;
	}

	public int getParcelasAbertas() {
		return parcelasAbertas;
	}

	public void setParcelasAbertas(int parcelasAbertas) {
		this.parcelasAbertas = parcelasAbertas;
	}

	public BigDecimal getValorReceber() {
		return valorReceber;
	}

	public void setValorReceber(BigDecimal valorReceber) {
		this.valorReceber = valorReceber;
	}

	public BigDecimal getValorRecebido() {
		return valorRecebido;
	}

	public void setValorRecebido(BigDecimal valorRecebido) {
		this.valorRecebido = valorRecebido;
	}

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

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public BigDecimal getValorInvestidor() {
		return valorInvestidor;
	}

	public void setValorInvestidor(BigDecimal valorInvestidor) {
		this.valorInvestidor = valorInvestidor;
	}

	public ContratoCobrancaDetalhes getSelectedContratoCobrancaDetalhes() {
		return selectedContratoCobrancaDetalhes;
	}

	public void setSelectedContratoCobrancaDetalhes(ContratoCobrancaDetalhes selectedContratoCobrancaDetalhes) {
		this.selectedContratoCobrancaDetalhes = selectedContratoCobrancaDetalhes;
	}

	public void setUsuarioLogado(User usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}

	public BigDecimal getValorInvestido() {
		return valorInvestido;
	}

	public void setValorInvestido(BigDecimal valorInvestido) {
		this.valorInvestido = valorInvestido;
	}

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

	public StreamedContent getDownloadAllFiles() {
		return downloadAllFiles;
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
		String caminho =  this.pathPDF + this.termoSecuritizacaoPDFStr;        
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
		String caminho =  this.pathPDF + this.anexoPDFStr;        
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
		String caminho =  this.pathPDF + this.bsPDFStr;        
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
		String caminho =  this.pathPDF + this.titulosQuitadosPDFStr;        
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
		String caminho =  this.pathPDF + this.operacoesIndividualizadoPDFStr;        
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
}
