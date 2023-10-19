package com.webnowbr.siscoat.cobranca.mb;

import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;

import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFAbstractNum;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.hibernate.JDBCException;
import org.hibernate.TransientObjectException;
import org.json.JSONObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumbering;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGrid;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.webnowbr.siscoat.cobranca.auxiliar.NumeroPorExtenso;
import com.webnowbr.siscoat.cobranca.auxiliar.PorcentagemPorExtenso;
import com.webnowbr.siscoat.cobranca.auxiliar.ValorPorExtenso;
import com.webnowbr.siscoat.cobranca.db.model.Averbacao;
import com.webnowbr.siscoat.cobranca.db.model.CcbContrato;
import com.webnowbr.siscoat.cobranca.db.model.CcbParticipantes;
import com.webnowbr.siscoat.cobranca.db.model.CcbProcessosJudiciais;
import com.webnowbr.siscoat.cobranca.db.model.ContasPagar;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.Segurado;
import com.webnowbr.siscoat.cobranca.db.op.CcbDao;
import com.webnowbr.siscoat.cobranca.db.op.CcbParticipantesDao;
import com.webnowbr.siscoat.cobranca.db.op.CcbProcessosJudiciaisDao;
import com.webnowbr.siscoat.cobranca.db.op.ContasPagarDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.RegistroImovelTabelaDao;
import com.webnowbr.siscoat.cobranca.db.op.SeguradoDAO;
import com.webnowbr.siscoat.common.BancosEnum;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.common.ValidaCNPJ;
import com.webnowbr.siscoat.common.ValidaCPF;
import com.webnowbr.siscoat.db.dao.DAOException;
import com.webnowbr.siscoat.simulador.GoalSeek;
import com.webnowbr.siscoat.simulador.GoalSeekFunction;
import com.webnowbr.siscoat.simulador.SimulacaoDetalheVO;
import com.webnowbr.siscoat.simulador.SimulacaoVO;
import com.webnowbr.siscoat.simulador.SimuladorMB;

/** ManagedBean. */
@ManagedBean(name = "ccbMB")
@SessionScoped
@SuppressWarnings("deprecation")
public class CcbMB {
	private String numeroContrato;
	private String numeroCcb;
	
	private String nomeEmitente;
	private String nacionalidadeEmitente;
	private String profissaoEmitente;
	private String estadoCivilEmitente;
	private String numeroRgEmitente;
	private String ufEmitente;
	private String cpfEmitente;
	private String logradouroEmitente;
	private String numeroEmitente;
	private String complementoEmitente;
	private String cidadeEmitente;
	private String cepEmitente;
	private String emailEmitente;
	private String regimeCasamentoEmitente;
	private boolean fiduciante;
	private boolean femininoEmitente;
	private boolean empresaEmitente;
	private String paiEmitente;
	private String maeEmitente;
	
	private String razaoSocialEmitente;
	private String tipoEmpresaEmitente;
	private String cnpjEmitente;
	private String municipioEmpresaEmitente;
	private String estadoEmpresaEmitente;
	private String ruaEmpresaEmitente;
	private String numeroEmpresaEmitente;
	private String salaEmpresaEmitente;
	private String bairroEmpresaEmitente;
	private String cepEmpresaEmitente;

	private String nomeConjugeEmitente;	
	private String cpfConjugeEmitente;	

	private boolean addTerceiro;

	private PagadorRecebedor selectedPagadorGenerico;
	private PagadorRecebedor selectedPagador;
	private PagadorRecebedor objetoPagadorRecebedor;
	private PagadorRecebedor emitenteSelecionado = new PagadorRecebedor();
	private PagadorRecebedor intervenienteSelecionado;
	private PagadorRecebedor terceiroGSelecionado;
	private PagadorRecebedor avalistaSelecionado;
	private PagadorRecebedor testemunha1Selecionado;
	private PagadorRecebedor testemunha2Selecionado;
	private List<PagadorRecebedor> listPagadores;
	
	private String tipoPesquisa;
	private String tipoDownload;
	
	public UploadedFile uploadedFile;
    public String fileName;
    public String fileType;
    public int fileTypeInt;
    ByteArrayInputStream bis = null;
    
    private CcbParticipantes participanteSelecionado = new CcbParticipantes();
    
    private boolean addParticipante;
    
    private CcbParticipantes socioSelecionado = new CcbParticipantes();
    private boolean addSocio;
    
    private boolean temCustasCartorarias = false;
    private boolean temCertidaoDeCasamento = false;
    private boolean temLaudoDeAvaliacao = false;
    private boolean temIntermediacao = false;
    private boolean temCCBValor = false;
    private boolean temProcessosJucidiais = false;
    private boolean temIptuEmAtraso = false;
    private boolean temCondominioEmAtraso = false;
    private boolean temIq = false;
    private boolean temItbi = false;
	private boolean addSegurador;
	
	private boolean mostrarDadosOcultos;
	private Segurado seguradoSelecionado;
    
    private BigDecimal valorProcesso = BigDecimal.ZERO;
    
    private String numeroProcesso = "";
    
    private ContasPagar despesaSelecionada;
    
    private CcbProcessosJudiciais processoSelecionado;
    
    private String carencia = "";
    
    private String aviso = " a ";
    
    private CcbContrato objetoCcb = new CcbContrato();
    
    private List<CcbContrato> listaCcbs = new ArrayList<CcbContrato>();
    
    private ArrayList<UploadedFile> filesList = new ArrayList<UploadedFile>();
    
    String tituloPagadorRecebedorDialog = "";
    
    private ContratoCobranca objetoContratoCobranca;
   
    private List<ContratoCobranca> listaContratosConsultar = new ArrayList<ContratoCobranca>();
    
	ValorPorExtenso valorPorExtenso = new ValorPorExtenso();
	NumeroPorExtenso numeroPorExtenso = new NumeroPorExtenso();
	PorcentagemPorExtenso porcentagemPorExtenso = new PorcentagemPorExtenso();
	
	SimulacaoVO simulador = new SimulacaoVO();
	
	private char[] alphabet = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	
	public void removerSegurado(Segurado segurado) {
		this.objetoCcb.getListSegurados().remove(segurado);		
		if(!CommonsUtil.semValor(this.objetoCcb.getObjetoContratoCobranca())) {
			if(this.objetoCcb.getObjetoContratoCobranca().getListSegurados().contains(segurado)) {
				this.objetoCcb.getObjetoContratoCobranca().getListSegurados().remove(segurado);
			}
		}
	}
	
	public void concluirSegurado() {
		this.tituloPagadorRecebedorDialog = "";
		this.updatePagadorRecebedor = "";
		this.seguradoSelecionado.setPosicao(this.objetoCcb.getListSegurados().size() + 1);
		if(!CommonsUtil.semValor(this.objetoCcb.getObjetoContratoCobranca())) {
			if(!this.objetoCcb.getObjetoContratoCobranca().getListSegurados().contains(this.seguradoSelecionado)) {		
				this.seguradoSelecionado.setContratoCobranca(this.objetoContratoCobranca);
				this.objetoCcb.getObjetoContratoCobranca().getListSegurados().add(seguradoSelecionado);
			}
		}
		this.objetoCcb.getListSegurados().add(this.seguradoSelecionado);
		this.addSegurador= false;
	}
	
	public void pesquisaSegurado() {
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listPagadores = pagadorRecebedorDao.getPagadoresRecebedores();		
		this.tipoPesquisa = "Segurado";
		this.tituloPagadorRecebedorDialog = "Segurados";
		this.updatePagadorRecebedor = " :form:SeguradoresPanel ";
		this.seguradoSelecionado = new Segurado();
		this.seguradoSelecionado.setPessoa(new PagadorRecebedor());
	}
		
	public void enviarMoneyPlus() {
		FacesContext context = FacesContext.getCurrentInstance();

		BmpDigitalCCBMB bmpMB = new BmpDigitalCCBMB();	
		boolean validacao = true;
		
		/**
		 * TRATA EMITENTE
		 */
		PagadorRecebedor eminenteDTO = null;
		for (CcbParticipantes pessoa : this.objetoCcb.getListaParticipantes()) {
			if (pessoa.getTipoParticipante().equals("EMITENTE")) {
				eminenteDTO = pessoa.getPessoa();
			}
		}
		if (eminenteDTO != null) {
			// se não existe pessoa na money plus, cria!
			if (eminenteDTO.getCodigoMoneyPlus() == null || eminenteDTO.getCodigoMoneyPlus().equals("")) {
				bmpMB.enviaEmitente(eminenteDTO, this.nacionalidadeEmitente);	
				bmpMB.enviaEndereco(eminenteDTO);
			}			
		} else {	
			validacao = false;
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"[MoneyPlus] Envia Pessoa - Emitente não encontrado", ""));
		}
		
		/**
		 * TRATA FIDUCIANTE
		 */
		PagadorRecebedor fiducianteDTO = null;		
		if (validacao) {			
			for (CcbParticipantes pessoa : objetoCcb.getListaParticipantes()) {
				if (pessoa.getTipoParticipante().equals("TERCEIRO GARANTIDOR")) {
					fiducianteDTO = pessoa.getPessoa();
				}
			}
			if (fiducianteDTO != null) {
				// se não existe pessoa na money plus, cria!
				if (fiducianteDTO.getCodigoMoneyPlus() == null || fiducianteDTO.getCodigoMoneyPlus().equals("")) {
					bmpMB.enviaEmitente(fiducianteDTO, this.nacionalidadeEmitente);	
					bmpMB.enviaEndereco(fiducianteDTO);
				}			
			} else {	
				fiducianteDTO = eminenteDTO;
			}
		}
		
		/**
		 * ENVIA PROPOSTA
		 */
		if (validacao) {			
			bmpMB.enviaProposta(eminenteDTO, fiducianteDTO, this.objetoCcb.getNumeroParcelasPagamento(), this.objetoCcb.getTaxaDeJurosMes(), this.objetoCcb.getValorIOF(), this.objetoCcb.getNumeroBanco(), 
					this.objetoCcb.getAgencia(), this.objetoCcb.getContaCorrente(), this.objetoCcb.getValorCredito(), this.objetoCcb.getNumeroCcb(), this.objetoCcb.getVencimentoPrimeiraParcelaPagamento(), this.objetoCcb.getValorParcela());
		}		
	}
	
	public void clearDocumentosNovos() {
		mostrarDadosOcultos = false;
		this.objetoCcb.setNomeEmitente(null);
		this.objetoCcb.setCpfEmitente(null);
		this.objetoCcb.setTerceiroGarantidor(false);
		this.objetoCcb.setLogradouroEmitente(null);
		this.objetoCcb.setNumeroEmitente(null);
		this.objetoCcb.setComplementoEmitente(null);
		this.objetoCcb.setCidadeEmitente(null);
		this.objetoCcb.setUfEmitente(null);
		this.objetoCcb.setCepEmitente(null); 
		
		for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {
			if(CommonsUtil.semValor(participante.getTipoOriginal())) {
				participante.setTipoOriginal(participante.getTipoParticipante());
			} else {
				participante.setTipoParticipante(participante.getTipoOriginal());
			}
		}
	}
	
	public void pesquisaParticipante() {
		this.tituloPagadorRecebedorDialog = "Participante";
		this.tipoPesquisa = "Participante";
		this.updatePagadorRecebedor = ":form:ParticipantesPanel :form:Dados";
		//this.participanteSelecionado = new CcbParticipantes();
		//this.participanteSelecionado.setPessoa(new PagadorRecebedor());
	}
	
	public void concluirParticipante() {
		CcbParticipantesDao ccbDao = new CcbParticipantesDao();
		this.participanteSelecionado.setTipoOriginal(participanteSelecionado.getTipoParticipante());

		this.objetoCcb.getListaParticipantes().add(this.participanteSelecionado);
		criarPagadorRecebedorNoSistema(this.participanteSelecionado.getPessoa());
		this.participanteSelecionado.setPessoa(this.objetoPagadorRecebedor);
		CcbParticipantesDao ccbPartDao = new CcbParticipantesDao();
		if(ccbPartDao.findByFilter("pessoa", this.participanteSelecionado.getPessoa()).size() > 0){
			this.participanteSelecionado.setId(ccbPartDao.findByFilter("pessoa", this.participanteSelecionado.getPessoa()).get(0).getId());
			ccbDao.merge(this.participanteSelecionado);
		} else {
			ccbDao.create(this.participanteSelecionado);
		}
		
		if(CommonsUtil.mesmoValor(participanteSelecionado.getTipoParticipante(), "EMITENTE")) {
			if(CommonsUtil.semValor(this.objetoCcb.getContaCorrente())) {
				if(!CommonsUtil.semValor(participanteSelecionado.getPessoa().getConta())) {
					this.objetoCcb.setContaCorrente(participanteSelecionado.getPessoa().getConta() + "-" + participanteSelecionado.getPessoa().getContaDigito());
					this.objetoCcb.setCCBCC(participanteSelecionado.getPessoa().getConta() + "-" + participanteSelecionado.getPessoa().getContaDigito());
				}
			}
			
			if(CommonsUtil.semValor(this.objetoCcb.getTipoContaBanco())) {
				if(!CommonsUtil.semValor(participanteSelecionado.getPessoa().getTipoConta())) {
					this.objetoCcb.setTipoContaBanco(participanteSelecionado.getPessoa().getTipoConta());
					this.objetoCcb.setCCBTipoConta(participanteSelecionado.getPessoa().getTipoConta());
				}
			}
			
			if(CommonsUtil.semValor(this.objetoCcb.getAgencia())) {
				if(!CommonsUtil.semValor(participanteSelecionado.getPessoa().getAgencia())) {
					this.objetoCcb.setAgencia(participanteSelecionado.getPessoa().getAgencia());
					this.objetoCcb.setCCBAgencia(participanteSelecionado.getPessoa().getAgencia());
				}
			}
			
			if(!CommonsUtil.semValor(participanteSelecionado.getPessoa().getBanco())) {		
				this.objetoCcb.setCCBBanco(participanteSelecionado.getPessoa().getBanco());
				String[] banco = participanteSelecionado.getPessoa().getBanco().split(Pattern.quote("|"));
				if (CommonsUtil.semValor(this.objetoCcb.getNomeBanco())) {
					if (!CommonsUtil.semValor(banco) && banco.length > 1) {
						this.objetoCcb.setNomeBanco(CommonsUtil.trimNull(banco[1]));
					}
				}
				if (CommonsUtil.semValor(this.objetoCcb.getNumeroBanco())) {
					if (!CommonsUtil.semValor(banco) && banco.length > 0) {
						this.objetoCcb.setNumeroBanco(CommonsUtil.trimNull(banco[0]));
					}
				}
			}
			
			if(CommonsUtil.semValor(this.objetoCcb.getTitularConta())) {
				if(!CommonsUtil.semValor(participanteSelecionado.getPessoa().getNomeCC())) {
					this.objetoCcb.setTitularConta(participanteSelecionado.getPessoa().getNomeCC());
					this.objetoCcb.setCCBNome(participanteSelecionado.getPessoa().getNomeCC());
				}
			}
			
			if(CommonsUtil.semValor(this.objetoCcb.getPixBanco())) {
				if(!CommonsUtil.semValor(participanteSelecionado.getPessoa().getPix())) {
					this.objetoCcb.setPixBanco(participanteSelecionado.getPessoa().getPix());
					this.objetoCcb.setCCBPix(participanteSelecionado.getPessoa().getPix());
				}
			}
			
			if(CommonsUtil.semValor(this.objetoCcb.getCCBCNPJ())) {
				if(!CommonsUtil.semValor(participanteSelecionado.getPessoa().getCpf())) {
					this.objetoCcb.setCCBDocumento("CPF");
					this.objetoCcb.setCCBCNPJ(participanteSelecionado.getPessoa().getCpf());
				} else if (!CommonsUtil.semValor(participanteSelecionado.getPessoa().getCnpj())) {
					this.objetoCcb.setCCBDocumento("CNPJ");
					this.objetoCcb.setCCBCNPJ(participanteSelecionado.getPessoa().getCnpj());
				}
			}
			
		}
		this.participanteSelecionado = new CcbParticipantes();
		this.participanteSelecionado.setPessoa(new PagadorRecebedor());
		this.addParticipante = false;
	}
	
	public void editarParticipante(CcbParticipantes participante) {
		this.addParticipante = true;
		this.participanteSelecionado = new CcbParticipantes();
		this.setParticipanteSelecionado(participante);
		this.removerParticipante(participante);
	}
	
	public void removerParticipante(CcbParticipantes participante) {
		this.objetoCcb.getListaParticipantes().remove(participante);
	}
	
	public void clearParticipante() {
		this.participanteSelecionado = new CcbParticipantes();
		this.participanteSelecionado.setPessoa(new PagadorRecebedor());
	}
	
	public void pesquisaSocio() {
		this.tituloPagadorRecebedorDialog = "Socio";
		this.tipoPesquisa = "Socio";
		this.updatePagadorRecebedor = ":form:SociosPanel ";
		//this.socioSelecionado = new CcbParticipantes();
		//this.socioSelecionado.setPessoa(new PagadorRecebedor());
	}
	
	public void concluirSocio() {
		CcbParticipantesDao ccbDao = new CcbParticipantesDao();
		this.getParticipanteSelecionado().getSocios().add(socioSelecionado); 
		criarPagadorRecebedorNoSistema(this.socioSelecionado.getPessoa());
		CcbParticipantesDao ccbPartDao = new CcbParticipantesDao();
		
		//colocado merge de pagRece
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		pagadorRecebedorDao.merge(this.socioSelecionado.getPessoa());
		if(ccbPartDao.findByFilter("pessoa", this.socioSelecionado.getPessoa()).size() > 0){
			this.socioSelecionado.setId(ccbPartDao.findByFilter("pessoa", this.socioSelecionado.getPessoa()).get(0).getId());
			ccbDao.merge(this.socioSelecionado);
		} else {
			ccbDao.create(this.socioSelecionado);
		}
		this.socioSelecionado = new CcbParticipantes();
		this.socioSelecionado.setPessoa(new PagadorRecebedor());
		this.addSocio = false;
	}
	
	public void editarSocio(CcbParticipantes socio) {
		this.addSocio = true;
		this.socioSelecionado = new CcbParticipantes();
		this.setSocioSelecionado(socio);
		this.removerSocio(socio);
	}
	
	public void removerSocio(CcbParticipantes socio) {
		this.getParticipanteSelecionado().getSocios().remove(socio);
	}
	
	public void clearSocio() {
		this.socioSelecionado = new CcbParticipantes();
		this.socioSelecionado.setPessoa(new PagadorRecebedor());
	}
	
	public void criarDespesa(String descricao, BigDecimal valor) {
		criarDespesa(descricao, valor, "Boleto");
	}
	
	public void criarDespesa(String descricao, BigDecimal valor, String formaTransferencia) {
		despesaSelecionada = new ContasPagar();
		despesaSelecionada.setDescricao(descricao);
		despesaSelecionada.setValor(valor);
		despesaSelecionada.setFormaTransferencia(formaTransferencia);
		addDespesa();
	}
	
	public void addDespesa() {
		despesaSelecionada.setTipoDespesa("C");
		if(!CommonsUtil.semValor(objetoCcb.getObjetoContratoCobranca())) {
			despesaSelecionada.setNumeroDocumento(objetoCcb.getObjetoContratoCobranca().getNumeroContrato());
			despesaSelecionada.setPagadorRecebedor(objetoCcb.getObjetoContratoCobranca().getPagador());
			despesaSelecionada.setResponsavel(objetoCcb.getObjetoContratoCobranca().getResponsavel());
			
			if(CommonsUtil.mesmoValor(despesaSelecionada.getDescricao(), "Crédito CCI")) {
				if(CommonsUtil.mesmoValor(despesaSelecionada.getFormaTransferencia(), "TED")) {
					if(!CommonsUtil.semValor(objetoCcb.getCCBNome())) {
						despesaSelecionada.setNomeTed(objetoCcb.getCCBNome());
					}					
					if(!CommonsUtil.semValor(objetoCcb.getCCBCNPJ())) {
						despesaSelecionada.setCpfTed(objetoCcb.getCCBCNPJ());
					}					
					if(!CommonsUtil.semValor(objetoCcb.getCCBBanco())) {
						despesaSelecionada.setBancoTed(objetoCcb.getCCBBanco());
					}					
					if(!CommonsUtil.semValor(objetoCcb.getCCBAgencia())) {
						despesaSelecionada.setAgenciaTed(objetoCcb.getCCBAgencia());
					}					
					if(!CommonsUtil.semValor(objetoCcb.getCCBCC())) {
						despesaSelecionada.setContaTed(objetoCcb.getCCBCC());
					}
					if(!CommonsUtil.semValor(objetoCcb.getCCBCC())) {
						despesaSelecionada.setContaTed(objetoCcb.getCCBCC());
					}
					
					if(!CommonsUtil.semValor(objetoCcb.getCCBDigito())) {
						despesaSelecionada.setDigitoContaTed(objetoCcb.getCCBDigito());
					}
					
					if(!CommonsUtil.semValor(despesaSelecionada.getNomeTed())) {
						objetoCcb.getObjetoContratoCobranca().setNomeBancarioContaPagar(despesaSelecionada.getNomeTed());
					}
					if(!CommonsUtil.semValor(despesaSelecionada.getCpfTed())) {
						objetoCcb.getObjetoContratoCobranca().setCpfCnpjBancarioContaPagar(despesaSelecionada.getCpfTed());
					}				
					if(!CommonsUtil.semValor(despesaSelecionada.getBancoTed())) {
						objetoCcb.getObjetoContratoCobranca().setBancoBancarioContaPagar(despesaSelecionada.getBancoTed());
					}
					if(!CommonsUtil.semValor(despesaSelecionada.getAgenciaTed())) {
						objetoCcb.getObjetoContratoCobranca().setAgenciaBancarioContaPagar(despesaSelecionada.getAgenciaTed());
					}
					if(!CommonsUtil.semValor(despesaSelecionada.getContaTed())) {
						objetoCcb.getObjetoContratoCobranca().setContaBancarioContaPagar(despesaSelecionada.getContaTed());
					}
					if(!CommonsUtil.semValor(despesaSelecionada.getDigitoContaTed())) {
						objetoCcb.getObjetoContratoCobranca().setDigitoContaBancarioContaPagar(despesaSelecionada.getDigitoContaTed());
					}
				} else if(CommonsUtil.mesmoValor(despesaSelecionada.getFormaTransferencia(), "Pix")) {
					if(!CommonsUtil.semValor(objetoCcb.getCCBPix())) {
						despesaSelecionada.setPix(objetoCcb.getCCBPix());
					}
					
					if(!CommonsUtil.semValor(despesaSelecionada.getPix())) {
						//objetoCcb.getObjetoContratoCobranca().se(despesaSelecionada.getPix());
					}
				}
			} else if(CommonsUtil.mesmoValor(despesaSelecionada.getDescricao(), "Transferência")) {
				if(CommonsUtil.mesmoValor(despesaSelecionada.getFormaTransferencia(), "TED")) {
					if(!CommonsUtil.semValor(objetoCcb.getIntermediacaoNome())) {
						despesaSelecionada.setNomeTed(objetoCcb.getIntermediacaoNome());
					}					
					if(!CommonsUtil.semValor(objetoCcb.getIntermediacaoCNPJ())) {
						despesaSelecionada.setCpfTed(objetoCcb.getIntermediacaoCNPJ());
					}					
					if(!CommonsUtil.semValor(objetoCcb.getIntermediacaoBanco())) {
						despesaSelecionada.setBancoTed(objetoCcb.getIntermediacaoBanco());
					}
					if(!CommonsUtil.semValor(objetoCcb.getIntermediacaoAgencia())) {
						despesaSelecionada.setAgenciaTed(objetoCcb.getIntermediacaoAgencia());
					}					
					if(!CommonsUtil.semValor(objetoCcb.getIntermediacaoCC())) {
						despesaSelecionada.setContaTed(objetoCcb.getIntermediacaoCC());
					}
				} else if(CommonsUtil.mesmoValor(despesaSelecionada.getFormaTransferencia(), "Pix")) {
					if(!CommonsUtil.semValor(objetoCcb.getIntermediacaoPix())) {
						despesaSelecionada.setPix(objetoCcb.getIntermediacaoPix());
					}
				}
			}
			
			if(!this.objetoCcb.getObjetoContratoCobranca().getListContasPagar().contains(this.despesaSelecionada)) {	
				despesaSelecionada.setContrato(objetoCcb.getObjetoContratoCobranca());
				//objetoCcb.getObjetoContratoCobranca().getListContasPagar().add(despesaSelecionada);
			}
		}
		
		this.objetoCcb.getDespesasAnexo2().add(despesaSelecionada);
		if(CommonsUtil.semValor(this.objetoCcb.getValorDespesas())) {
			this.objetoCcb.setValorDespesas(BigDecimal.ZERO);
		}
		calcularValorDespesa();
		ContasPagarDao contasPagarDao = new ContasPagarDao();
		contasPagarDao.create(despesaSelecionada);
		despesaSelecionada = new ContasPagar();
	}
	
	public void removeDespesa(ContasPagar conta) {
		this.objetoCcb.getDespesasAnexo2().remove(conta);
		if(!CommonsUtil.semValor(conta.getContrato())) {
			conta.setContrato(null);
		}
		if(!CommonsUtil.semValor(objetoCcb.getObjetoContratoCobranca())) {
			if(this.objetoCcb.getObjetoContratoCobranca().getListContasPagar().contains(conta)) {
				objetoCcb.getObjetoContratoCobranca().getListContasPagar().remove(conta);
			}
		}
		calcularValorDespesa();
	}
	
	public void addProcesso() {
		processoSelecionado.getContaPagar().setValor(processoSelecionado.getValorAtualizado());
		processoSelecionado.getContaPagar().setDescricao("Processo N°: " + processoSelecionado.getNumero());
		
		if(!CommonsUtil.semValor(objetoCcb.getObjetoContratoCobranca())) {
			processoSelecionado.getContaPagar().setNumeroDocumento(objetoCcb.getObjetoContratoCobranca().getNumeroContrato());
			processoSelecionado.getContaPagar().setPagadorRecebedor(objetoCcb.getObjetoContratoCobranca().getPagador());
			processoSelecionado.getContaPagar().setResponsavel(objetoCcb.getObjetoContratoCobranca().getResponsavel());
			processoSelecionado.getContaPagar().setTipoDespesa("C");
			processoSelecionado.getContaPagar().setFormaTransferencia("Boleto");
			
			if(!this.objetoCcb.getObjetoContratoCobranca().getListProcessos().contains(this.processoSelecionado)) {
				processoSelecionado.setContrato(objetoCcb.getObjetoContratoCobranca());
				//objetoCcb.getObjetoContratoCobranca().getListContasPagar().add(despesaSelecionada);
			}
		}
		this.objetoCcb.getProcessosJucidiais().add(processoSelecionado);
		calcularValorDespesa();
		ContasPagarDao contasPagarDao = new ContasPagarDao();
		contasPagarDao.create(processoSelecionado.getContaPagar());
		CcbProcessosJudiciaisDao processoDao = new CcbProcessosJudiciaisDao();
		processoDao.create(processoSelecionado);
		processoSelecionado = new CcbProcessosJudiciais();
	}
	
	public void removeProcesso(CcbProcessosJudiciais processo) {
		this.objetoCcb.getProcessosJucidiais().remove(processo);
		if(!CommonsUtil.semValor(objetoCcb.getObjetoContratoCobranca())) {
			if(this.objetoCcb.getObjetoContratoCobranca().getListProcessos().contains(processo)) {
				objetoCcb.getObjetoContratoCobranca().getListProcessos().remove(processo);
			}
		}
		calcularValorDespesa();
	}
		
	public void pesquisaContratoCobranca() {
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		this.listaContratosConsultar = cDao.consultaContratosCCBs();
	}
	
	public void populateSelectedContratoCobranca() {
		ContratoCobranca contrato = objetoContratoCobranca;
		this.objetoCcb.setObjetoContratoCobranca(contrato);
		this.objetoCcb.setNumeroOperacao(contrato.getNumeroContrato());
		
		if (CommonsUtil.mesmoValor(contrato.getAvaliacaoLaudo(), "Compass")) {
			this.objetoCcb.setElaboradorNome("Compass Avaliações Imobiliárias");
			this.objetoCcb.setElaboradorCrea("CAU A40301-6");
			this.objetoCcb.setResponsavelNome("Ana Maria F. Cooke");
			this.objetoCcb.setResponsavelCrea("CAU A40301-6");
		} else if (CommonsUtil.mesmoValor(contrato.getAvaliacaoLaudo(), "Galache")) {
			this.objetoCcb.setElaboradorNome("Galache Engenharia Ltda");
			this.objetoCcb.setElaboradorCrea("1009877");
			this.objetoCcb.setResponsavelNome("Tales R. S. Galache");
			this.objetoCcb.setResponsavelCrea("5060563873-D");
		}
		
		ImovelCobranca imovel = contrato.getImovel();
		this.objetoCcb.setCepImovel(imovel.getCep());	
		this.objetoCcb.setNumeroImovel(imovel.getNumeroMatricula());
		this.objetoCcb.setInscricaoMunicipal(imovel.getInscricaoMunicipal());
		this.objetoCcb.setCidadeImovel(imovel.getCidade());
		this.objetoCcb.setCartorioImovel(imovel.getCartorio());
		this.objetoCcb.setUfImovel(imovel.getEstado());
		String[] endereco = imovel.getEndereco().split(Pattern.quote(","));
		if(endereco.length > 0) {
			this.objetoCcb.setLogradouroRuaImovel(endereco[0]);
		}
		if(endereco.length > 1) {
			this.objetoCcb.setLogradouroNumeroImovel(CommonsUtil.removeEspacos(endereco[1]));
		}
		this.objetoCcb.setBairroImovel(imovel.getBairro());
		//listaArquivos();
		
		//Popular Campos para Simulação
		this.objetoCcb.setUsarNovoCustoEmissao(true);
		this.objetoCcb.setVlrImovel(contrato.getValorMercadoImovel());
		this.objetoCcb.setVendaLeilao(contrato.getValorVendaForcadaImovel());
		objetoCcb.setValorCredito(objetoContratoCobranca.getValorAprovadoComite());
		objetoCcb.setTaxaDeJurosMes(objetoContratoCobranca.getTaxaAprovada());
		objetoCcb.setPrazo(objetoContratoCobranca.getPrazoMaxAprovado().toString());
		objetoCcb.setSistemaAmortizacao("Price");
		this.carencia = CommonsUtil.stringValue(objetoContratoCobranca.getCarenciaComite());
		if(CommonsUtil.mesmoValor(objetoContratoCobranca.getTipoValorComite(), "liquido")) {
			objetoCcb.setTipoCalculoFinal('L');
		} else {
			objetoCcb.setTipoCalculoFinal('B');
		}
		
		//Calcular Parcelas
		calcularSimulador();
		
		//Adicionar Despesas
		for(CcbProcessosJudiciais processo : contrato.getListProcessos()) {
			if(!processo.isSelecionadoComite()) {
				continue;
			}
			
			if(!this.objetoCcb.getProcessosJucidiais().contains(processo)) {
				this.objetoCcb.getProcessosJucidiais().add(processo);
			}
		}
		
		if(!CommonsUtil.semValor(objetoContratoCobranca.getValorLaudoPajuFaltante())) {
			if(CommonsUtil.semValor(objetoCcb.getLaudoDeAvaliacaoValor())) {
				criarDespesa("Laudo", objetoContratoCobranca.getValorLaudoPajuFaltante());
				this.temLaudoDeAvaliacao = true;
				objetoCcb.setLaudoDeAvaliacaoValor(objetoContratoCobranca.getValorLaudoPajuFaltante());
			}
		}
		
		if(CommonsUtil.mesmoValor(objetoContratoCobranca.getCobrarComissaoCliente(), "Sim")) {
			if(CommonsUtil.semValor(objetoCcb.getIntermediacaoValor())) {
				BigDecimal valorTranferencia = BigDecimal.ZERO;
				BigDecimal comissao = BigDecimal.ZERO;
				if(CommonsUtil.mesmoValor(objetoContratoCobranca.getTipoCobrarComissaoCliente(), "Real")) {
					if(!CommonsUtil.semValor(objetoContratoCobranca.getComissaoClienteValorFixo())) {
						valorTranferencia = objetoContratoCobranca.getComissaoClienteValorFixo();
					}
				} else if(CommonsUtil.mesmoValor(objetoContratoCobranca.getTipoCobrarComissaoCliente(), "Porcentagem")) {
					if(!CommonsUtil.semValor(objetoContratoCobranca.getComissaoClientePorcentagem())) {
						comissao = objetoContratoCobranca.getComissaoClientePorcentagem();
						comissao = comissao.divide(BigDecimal.valueOf(100), MathContext.DECIMAL128);
					}
					
					if(CommonsUtil.mesmoValor(objetoContratoCobranca.getBrutoLiquidoCobrarComissaoCliente(), "Bruto")) {
						valorTranferencia = objetoContratoCobranca.getValorAprovadoComite().multiply(comissao);
					} else if(CommonsUtil.mesmoValor(objetoContratoCobranca.getBrutoLiquidoCobrarComissaoCliente(), "Liquido")) {
						valorTranferencia = objetoCcb.getValorLiquidoCredito().multiply(comissao);
					}
				}
				
				this.temIntermediacao = true;
				objetoCcb.setIntermediacaoValor(valorTranferencia);
				objetoCcb.setIntermediacaoBanco(objetoContratoCobranca.getResponsavel().getBanco());
				objetoCcb.setIntermediacaoAgencia(objetoContratoCobranca.getResponsavel().getAgencia());
				objetoCcb.setIntermediacaoCC(objetoContratoCobranca.getResponsavel().getConta());
				objetoCcb.setIntermediacaoCNPJ(objetoContratoCobranca.getResponsavel().getCpfCnpjCC());
				objetoCcb.setIntermediacaoNome(objetoContratoCobranca.getResponsavel().getNomeCC());
				objetoCcb.setIntermediacaoPix(objetoContratoCobranca.getResponsavel().getPix());
				objetoCcb.setIntermediacaoTipoConta(objetoContratoCobranca.getResponsavel().getTipoConta());
				
				criarDespesa("Transferência", valorTranferencia, "TED");
			}
		}
		
		if(CommonsUtil.mesmoValor(objetoContratoCobranca.getDivida(), "Sim")) {
			if(CommonsUtil.semValor(objetoCcb.getIqValor())) {
				criarDespesa("IQ", objetoContratoCobranca.getDividaValor());
				this.temIq = true;
				objetoCcb.setIqValor(objetoContratoCobranca.getDividaValor());
			}
		}
		
		if(!CommonsUtil.semValor(objetoContratoCobranca.getDividaIPTU())) {
			if(CommonsUtil.semValor(objetoCcb.getIptuEmAtrasoValor())) {
				criarDespesa("IPTU", objetoContratoCobranca.getDividaIPTU());
				this.temIptuEmAtraso = true;
				objetoCcb.setIptuEmAtrasoValor(objetoContratoCobranca.getDividaIPTU());
			}
		}
		
		if(!CommonsUtil.semValor(objetoContratoCobranca.getDividaCondominio())) {
			if(CommonsUtil.semValor(objetoCcb.getCondominioEmAtrasoValor())) {
				criarDespesa("Condomínio", objetoContratoCobranca.getDividaCondominio());
				this.temCondominioEmAtraso = true;
				objetoCcb.setCondominioEmAtrasoValor(objetoContratoCobranca.getDividaCondominio());
			}
		}
		
		if(CommonsUtil.semValor(objetoContratoCobranca.getValorTotalAverbacao())) {
			 objetoContratoCobranca.setValorTotalAverbacao(BigDecimal.ZERO);
		}
		
		if(CommonsUtil.semValor(objetoCcb.getAverbacaoValor())) {
			if(!CommonsUtil.mesmoValor(objetoCcb.getAverbacaoValor(), objetoContratoCobranca.getValorTotalAverbacao())) {
				BigDecimal averbacaoTotal = BigDecimal.ZERO;
				for(Averbacao averbacao : contrato.getListAverbacao()) {
					averbacaoTotal = averbacaoTotal.add(averbacao.getValor());
				}
				if(!CommonsUtil.semValor(averbacaoTotal)) {
					criarDespesa("Averbação", averbacaoTotal);
					objetoCcb.setAverbacaoValor(averbacaoTotal);
				}
			}
		}
		
		if(!CommonsUtil.semValor(objetoCcb.getValorCredito())) {
			if(CommonsUtil.semValor(objetoCcb.getRegistroImovelValor())) {
				RegistroImovelTabelaDao rDao = new RegistroImovelTabelaDao();
				BigDecimal valorRegistro = rDao.getValorRegistro(objetoCcb.getValorCredito());
				criarDespesa("Registro", valorRegistro);
				objetoCcb.setRegistroImovelValor(valorRegistro);
			}
		}
		
		calcularValorDespesa();
		
		this.objetoContratoCobranca = null;
	}
	
	public void clearContratoCobranca() {
		this.objetoContratoCobranca = null;
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		this.listaContratosConsultar = cDao.consultaContratosCCBs();
	}
	
	public List<String> completeBancosNome(String query) {
        String queryLowerCase = query.toLowerCase();
        List<String> bancos = new ArrayList<>();
        for(BancosEnum banco : BancosEnum.values()) {
        	String bancoStr = banco.getNome().toString();
        	bancos.add(bancoStr);
        }
        return bancos.stream().filter(t -> t.toLowerCase().contains(queryLowerCase)).collect(Collectors.toList());
	}
	
	public List<String> completeBancosCodigo(String query) {
        String queryLowerCase = query.toLowerCase();
        List<String> bancos = new ArrayList<>();
        for(BancosEnum banco : BancosEnum.values()) {
        	String bancoStr = banco.getCodigo().toString();
        	bancos.add(bancoStr);
        }
        return bancos.stream().filter(t -> t.toLowerCase().contains(queryLowerCase)).collect(Collectors.toList());
	}
	
	public void populateCodigosBanco() {
		for(BancosEnum banco : BancosEnum.values()) {
			if(CommonsUtil.mesmoValor(this.objetoCcb.getNomeBanco(), banco.getNome().toString())) {
				this.objetoCcb.setNumeroBanco(banco.getCodigo());
				break;
			}
		}
	}
	
	public void populateNomesBanco() {
		for(BancosEnum banco : BancosEnum.values()) {
			if(CommonsUtil.mesmoValor(this.objetoCcb.getNumeroBanco(),banco.getCodigo())) {
				this.objetoCcb.setNomeBanco(banco.getNome());
				//PrimeFaces.current().ajax().update(":nomeBanco");
				break;
			}
		}
	}
	
	public void populateCodigosBancoVendedor() {
		for(BancosEnum banco : BancosEnum.values()) {
			if(CommonsUtil.mesmoValor(this.objetoCcb.getNomeBancoVendedor(), banco.getNome().toString())) {
				this.objetoCcb.setNumeroBancoVendedor(banco.getCodigo());
				break;
			}
		}
	}
	
	public void populateNomesBancoVendedor() {
		for(BancosEnum banco : BancosEnum.values()) {
			if(CommonsUtil.mesmoValor(this.objetoCcb.getNumeroBancoVendedor(),banco.getCodigo())) {
				this.objetoCcb.setNomeBancoVendedor(banco.getNome());
				//PrimeFaces.current().ajax().update(":nomeBanco");
				break;
			}
		}
	}

	String updatePagadorRecebedor = ":form";

	public void pesquisaEmitente() {
		this.tituloPagadorRecebedorDialog = "Emitente";
		this.tipoPesquisa = "Emitente";
		this.updatePagadorRecebedor = ":form:emitentePanel :form:Dados";
		this.emitenteSelecionado = new PagadorRecebedor();
	}

	public void pesquisaInterveniente() {
		this.tituloPagadorRecebedorDialog = "Interveniente";
		this.tipoPesquisa = "Interveniente";
		this.updatePagadorRecebedor = ":form:intervenientePanel";
		this.intervenienteSelecionado = new PagadorRecebedor();
	}
	
	public void pesquisaTerceiroG() {
		this.tituloPagadorRecebedorDialog = "Terceiro Garantidor";
		this.tipoPesquisa = "TerceiroG";
		this.updatePagadorRecebedor = ":form:terceiroPanel";
		this.terceiroGSelecionado = new PagadorRecebedor();
	}
	
	public void pesquisaAvalista() {
		this.tituloPagadorRecebedorDialog = "Avalista";
		this.tipoPesquisa = "Avalista";
		this.updatePagadorRecebedor = ":form:avalistaPanel";
		this.avalistaSelecionado = new PagadorRecebedor();
	}
	
	public void pesquisaTestemunha1() {
		this.tituloPagadorRecebedorDialog = "Testemunha 1";
		this.tipoPesquisa = "Testemunha1";
		this.updatePagadorRecebedor = ":form:Dados";
		this.testemunha1Selecionado = new PagadorRecebedor();
	}
	
	public void pesquisaTestemunha2() {
		this.tituloPagadorRecebedorDialog = "Testemunha 2";
		this.tipoPesquisa = "Testemunha2";
		this.updatePagadorRecebedor = ":form:Dados";
		this.testemunha2Selecionado = new PagadorRecebedor();
	}
	
	public void populateParcelaSeguro() {
		if(this.objetoCcb.getNumeroParcelasPagamento() != null) {
			this.objetoCcb.setNumeroParcelasDFI(this.objetoCcb.getNumeroParcelasPagamento());
			this.objetoCcb.setNumeroParcelasMIP(this.objetoCcb.getNumeroParcelasPagamento());
		} if(this.objetoCcb.getVencimentoPrimeiraParcelaPagamento() != null) {
			this.objetoCcb.setVencimentoPrimeiraParcelaDFI(this.objetoCcb.getVencimentoPrimeiraParcelaPagamento());
			this.objetoCcb.setVencimentoPrimeiraParcelaMIP(this.objetoCcb.getVencimentoPrimeiraParcelaPagamento());
		} if(this.objetoCcb.getVencimentoUltimaParcelaPagamento() != null) {
			this.objetoCcb.setVencimentoUltimaParcelaDFI(this.objetoCcb.getVencimentoUltimaParcelaPagamento());
			this.objetoCcb.setVencimentoUltimaParcelaMIP(this.objetoCcb.getVencimentoUltimaParcelaPagamento());
		}
	}
	
	public void calculaDatavencimentoFinal() {
		Integer parcelas = CommonsUtil.integerValue(this.objetoCcb.getNumeroParcelasPagamento());	
		parcelas -= 1;
		Calendar c = Calendar.getInstance();
		c.setTime(this.objetoCcb.getVencimentoPrimeiraParcelaPagamento());
		c.add(Calendar.MONTH, parcelas);
		this.objetoCcb.setVencimentoUltimaParcelaPagamento(c.getTime());
	}
	
	public void addValorProcesso() {
		this.objetoCcb.getProcessosJucidiais().add(new CcbProcessosJudiciais(valorProcesso, numeroProcesso));
		valorProcesso = BigDecimal.ZERO;	
		numeroProcesso = "";
		calcularValorDespesa();
	}
	
	public void removeValor(CcbProcessosJudiciais processo) {
		this.objetoCcb.getProcessosJucidiais().remove(processo);
		calcularValorDespesa();
	}
	
	public void calcularValorDespesa() {
		BigDecimal total =  BigDecimal.ZERO;
		
		if(!this.objetoCcb.getDespesasAnexo2().isEmpty()) {
			for(ContasPagar despesas : this.objetoCcb.getDespesasAnexo2()) {
				if(!CommonsUtil.semValor(despesas.getValor()))
					total = total.add(despesas.getValor());
			}
		}
		
		if(!this.objetoCcb.getProcessosJucidiais().isEmpty()) {
			for(CcbProcessosJudiciais processo : this.objetoCcb.getProcessosJucidiais()) {
				if(!CommonsUtil.semValor(processo.getValorAtualizado()))
					total = total.add(processo.getValorAtualizado());
			}
		}
		this.objetoCcb.setValorDespesas(total);
	}

	public List<String> completeTextNomes(){
		List<String> listaNome = new ArrayList<>();
		listaNome.add("Galache Engenharia Ltda");
		listaNome.add("Tales R. S. Galache");
		listaNome.add("Compass Avaliações Imobiliárias");
		listaNome.add("Ana Maria F. Cooke");
		return listaNome.stream().collect(Collectors.toList());
	}
	
	public List<String> completeTextCrea(){
		List<String> listaCrea = new ArrayList<>();
		listaCrea.add("1009877");
		listaCrea.add("5060563873-D");
		listaCrea.add("CAU A40301-6");
		return listaCrea;
	}
	
	public String EmitirCcbPreContrato() {
		clearFieldsInserirCcb();
		List<CcbContrato> ccbContratoDB = new ArrayList<CcbContrato>();
		CcbDao ccbDao = new CcbDao();
		ccbContratoDB = ccbDao.findByFilter("objetoContratoCobranca", objetoContratoCobranca);

		if (ccbContratoDB.size() > 0) {
			objetoCcb = ccbContratoDB.get(0);
			this.objetoContratoCobranca = objetoCcb.getObjetoContratoCobranca();
		} else {
			this.objetoContratoCobranca = getContratoById(this.objetoContratoCobranca.getId());
		}	
		
		if (objetoCcb.getListaParticipantes().size() <= 0) {
			//procura e setta pagador	
			this.selectedPagadorGenerico = getPagadorById(this.objetoContratoCobranca.getPagador().getId());
			pesquisaParticipante();
			populateSelectedPagadorRecebedor();	
			addParticipante = true;
			
			if(participanteSelecionado.isEmpresa()) {
				objetoCcb.setTipoPessoaEmitente("PJ");
			} else {
				objetoCcb.setTipoPessoaEmitente("PF");
			}
			
			participanteSelecionado.setTipoParticipante("EMITENTE");
			
			concluirParticipante();
		}
		
		populateSelectedContratoCobranca();
		
		calculaPorcentagemImovel();
				
		//luana
		pesquisaTestemunha1();
		selectedPagadorGenerico = ccbDao.ConsultaTestemunha((long) 11960);
		populateSelectedPagadorRecebedor();
		
		//anna flavia
		pesquisaTestemunha2();
		selectedPagadorGenerico = ccbDao.ConsultaTestemunha((long) 25929);
		populateSelectedPagadorRecebedor();
		
		criarCcbNosistema();
		//clearFieldsInserirCcb();
		return "/Atendimento/Cobranca/Ccb.xhtml";
	}
	
	public ContratoCobranca getContratoById(long idContrato) {
		ContratoCobranca contrato; //= new ContratoCobranca();
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();				
		contrato = cDao.findById(idContrato);	
		return contrato;
	}
	
	public PagadorRecebedor getPagadorById(long idPagador) {
		PagadorRecebedor pagador = new PagadorRecebedor();
		PagadorRecebedorDao pDao = new PagadorRecebedorDao();				
		pagador = pDao.findById(idPagador);	
		return pagador;
	}
	
	public PagadorRecebedor getTestemunha(long idPagador) {
		PagadorRecebedor pagador = new PagadorRecebedor();
		PagadorRecebedorDao pDao = new PagadorRecebedorDao();				
		pagador = pDao.findById(idPagador);	
		return pagador;
	}
	 	
	public void populatePagadores() {
		if(!CommonsUtil.semValor(this.emitenteSelecionado.getCpf())) {
			this.setNomeEmitente(this.emitenteSelecionado.getNome());
			this.setUfEmitente(this.emitenteSelecionado.getEstado());
			this.setLogradouroEmitente(this.emitenteSelecionado.getEndereco());
			this.setCepEmitente(this.emitenteSelecionado.getCep());
			this.setCidadeEmitente(this.emitenteSelecionado.getCidade());
			this.setNumeroEmitente(this.emitenteSelecionado.getNumero());
			this.setEmailEmitente(this.emitenteSelecionado.getEmail());
			this.setProfissaoEmitente(this.emitenteSelecionado.getAtividade());
			this.setEstadoCivilEmitente(this.emitenteSelecionado.getEstadocivil());
			this.setNumeroRgEmitente(this.emitenteSelecionado.getRg());	
			this.setCpfEmitente(this.emitenteSelecionado.getCpf());
			this.setComplementoEmitente(this.emitenteSelecionado.getComplemento());
			this.setLogradouroEmitente(this.emitenteSelecionado.getEndereco());
			this.setPaiEmitente(this.emitenteSelecionado.getNomePai());
			this.setMaeEmitente(this.emitenteSelecionado.getNomeMae());
			
			if(!CommonsUtil.semValor(this.emitenteSelecionado.getSexo())) {
				if(this.emitenteSelecionado.getSexo() == "MASCULINO") {
					this.femininoEmitente = false;
				} else if(this.emitenteSelecionado.getSexo() == "FEMININO") {
					this.femininoEmitente = true;
				} else {
					this.femininoEmitente = false;
				}
			} else if(this.femininoEmitente = true) {
				this.emitenteSelecionado.setSexo("FEMININO");
			} else {
				this.emitenteSelecionado.setSexo("MASCULINO");
			}
			
			if (this.emitenteSelecionado.getNomeConjuge() != null) {
				this.setRegimeCasamentoEmitente(this.emitenteSelecionado.getRegimeCasamento());
				this.setNomeConjugeEmitente(this.emitenteSelecionado.getNomeConjuge());
				this.setCpfConjugeEmitente(this.emitenteSelecionado.getCpfConjuge());
			} else {
				this.setRegimeCasamentoEmitente(null);
				this.setNomeConjugeEmitente(null);
				this.setCpfConjugeEmitente(null);
			}
		} else {
			this.setEmpresaEmitente(true);
			this.setCnpjEmitente(this.emitenteSelecionado.getCnpj());
			this.setRazaoSocialEmitente(this.emitenteSelecionado.getNome());
			this.setEstadoEmpresaEmitente(this.emitenteSelecionado.getEstado());
			this.setRuaEmpresaEmitente(this.emitenteSelecionado.getEndereco());
			this.setCepEmpresaEmitente(this.emitenteSelecionado.getCep());
			this.setMunicipioEmpresaEmitente(this.emitenteSelecionado.getCidade());
			this.setNumeroEmpresaEmitente(this.emitenteSelecionado.getNumero());
		}
	}
	
	public void populateSelectedPagadorRecebedor() {
		PagadorRecebedorDao pDao = new PagadorRecebedorDao();
		selectedPagadorGenerico = pDao.findById(selectedPagadorGenerico.getId());
		
		if (CommonsUtil.mesmoValor(this.tipoPesquisa , "Emitente")) {
			this.emitenteSelecionado = (this.selectedPagadorGenerico);
			this.setNomeEmitente(this.emitenteSelecionado.getNome());
			this.setProfissaoEmitente(this.emitenteSelecionado.getAtividade());
			this.setEstadoCivilEmitente(this.emitenteSelecionado.getEstadocivil());
			this.setNumeroRgEmitente(this.emitenteSelecionado.getRg());
			this.setUfEmitente(this.emitenteSelecionado.getEstado());
			this.setCpfEmitente(this.emitenteSelecionado.getCpf());
			this.setLogradouroEmitente(this.emitenteSelecionado.getEndereco());
			this.setNumeroEmitente(this.emitenteSelecionado.getNumero());
			this.setComplementoEmitente(this.emitenteSelecionado.getComplemento());
			this.setCidadeEmitente(this.emitenteSelecionado.getCidade());
			this.setCepEmitente(this.emitenteSelecionado.getCep());
			this.setEmailEmitente(this.emitenteSelecionado.getEmail());
			if(this.emitenteSelecionado.getSexo() != null) {
				if(this.emitenteSelecionado.getSexo() == "MASCULINO") {
					this.femininoEmitente = false;
				} else if(this.emitenteSelecionado.getSexo() == "FEMININO") {
					this.femininoEmitente = true;
				} else {
					this.femininoEmitente = false;
				}
			}
			if (this.emitenteSelecionado.getNomeConjuge() != null) {
				this.setNomeConjugeEmitente(this.emitenteSelecionado.getNomeConjuge());
				this.setCpfConjugeEmitente(this.emitenteSelecionado.getCpfConjuge());
			} else {
				this.setNomeConjugeEmitente(null);
				this.setCpfConjugeEmitente(null);
			}
		}
		
		else if (CommonsUtil.mesmoValor(this.tipoPesquisa ,"Testemunha1")) {
			this.testemunha1Selecionado = (this.selectedPagadorGenerico);
			this.objetoCcb.setNomeTestemunha1(this.testemunha1Selecionado.getNome());
			this.objetoCcb.setCpfTestemunha1(this.testemunha1Selecionado.getCpf());
			this.objetoCcb.setRgTestemunha1(this.testemunha1Selecionado.getRg());
		}
		
		else if (CommonsUtil.mesmoValor(this.tipoPesquisa ,"Testemunha2")) {
			this.testemunha2Selecionado = (this.selectedPagadorGenerico);
			this.objetoCcb.setNomeTestemunha2(this.testemunha2Selecionado.getNome());
			this.objetoCcb.setCpfTestemunha2(this.testemunha2Selecionado.getCpf());
			this.objetoCcb.setRgTestemunha2(this.testemunha2Selecionado.getRg());
		}
		else if (CommonsUtil.mesmoValor(this.tipoPesquisa , "Participante")) {
			CcbParticipantesDao ccbPartDao = new CcbParticipantesDao();
			
			if(ccbPartDao.findByFilter("pessoa", selectedPagadorGenerico).size() > 0){
				CcbParticipantes participanteBD = ccbPartDao.findByFilter("pessoa", selectedPagadorGenerico).get(0);
				this.setParticipanteSelecionado(ccbPartDao.findById(participanteBD.getId()));
			} else {
				if(CommonsUtil.semValor(selectedPagadorGenerico.getCpf())) {
					this.participanteSelecionado.setEmpresa(true);
				}
				if(this.selectedPagadorGenerico.getSexo() != null) {
					if(this.selectedPagadorGenerico.getSexo() == "MASCULINO") {
						this.participanteSelecionado.setFeminino(false);
					} else if(this.selectedPagadorGenerico.getSexo() == "FEMININO") {
						this.participanteSelecionado.setFeminino(true);
					} else {
						this.participanteSelecionado.setFeminino(false);
					}
				}
				this.participanteSelecionado.setPessoa(this.selectedPagadorGenerico);
			}
			//pegar participante na base de dados
		} 
		else if (CommonsUtil.mesmoValor(this.tipoPesquisa , "Socio")) {
			if(this.selectedPagadorGenerico.getSexo() != null) {
				if(this.selectedPagadorGenerico.getSexo() == "MASCULINO") {
					this.socioSelecionado.setFeminino(false);
				} else if(this.selectedPagadorGenerico.getSexo() == "FEMININO") {
					this.socioSelecionado.setFeminino(true);
				} else {
					this.socioSelecionado.setFeminino(false);
				}
			}
			this.socioSelecionado.setPessoa(this.selectedPagadorGenerico);
		} 
		else if ( CommonsUtil.mesmoValor("Segurado", tipoPesquisa)) {
			this.seguradoSelecionado.setPessoa(this.selectedPagadorGenerico);
		}
	}
	
	public boolean validaCPF(FacesContext facesContext, UIComponent uiComponent, Object object) {
		return ValidaCPF.isCPF(object.toString());
	}
	
	public void populaReferenciaBancariaCPF() {
		FacesContext context = FacesContext.getCurrentInstance();
		boolean validaCPF = ValidaCPF.isCPF(this.objetoPagadorRecebedor.getCpf());

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
		boolean validaCNPJ = ValidaCNPJ.isCNPJ(this.objetoPagadorRecebedor.getCnpj());

		if (!validaCNPJ) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Pré-Contrato: O CNPJ inserido é inválido ou está incorreto!", ""));
		} else {
			this.objetoPagadorRecebedor.setCnpjCC(this.objetoPagadorRecebedor.getCnpj());
			this.objetoPagadorRecebedor.setNomeCC(this.objetoPagadorRecebedor.getNome());
		}
	}
	
	public void populateDadosEmitente() {
		for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {																	
			if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")) {
				if(CommonsUtil.semValor(this.objetoCcb.getNomeEmitente())) {
					this.objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
				}
				if(CommonsUtil.semValor(cpfEmitente)) {
					if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
						this.objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
					} else {
						this.objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
					}
				}
				if(CommonsUtil.semValor(this.objetoCcb.getTipoPessoaEmitente())) {
					if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
						this.objetoCcb.setTipoPessoaEmitente("PF");
					} else {
						this.objetoCcb.setTipoPessoaEmitente("PJ");
					}
				}
			}
		}
	}
	
	public void criarPagadorRecebedorNoSistema(PagadorRecebedor pagador) {
		PagadorRecebedor pagadorRecebedor = null;
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		
		this.objetoPagadorRecebedor = pagador;
		

		if (this.objetoPagadorRecebedor.getId() <= 0) {
			List<PagadorRecebedor> pagadorRecebedorBD = new ArrayList<PagadorRecebedor>();
			boolean registraPagador = false;
			Long idPagador = (long) 0;

			if (this.objetoPagadorRecebedor.getCpf() != null) {
				boolean validaCPF = ValidaCPF.isCPF(this.objetoPagadorRecebedor.getCpf());
				if(validaCPF) {
					pagadorRecebedorBD = pagadorRecebedorDao.findByFilter("cpf", this.objetoPagadorRecebedor.getCpf());
					if (pagadorRecebedorBD.size() > 0) {
						pagadorRecebedor = pagadorRecebedorBD.get(0);
					} else {
						populaReferenciaBancariaCPF();
						pagadorRecebedor = this.objetoPagadorRecebedor;
						registraPagador = true;
					}
				}
			}
			
			if (this.objetoPagadorRecebedor.getCnpj() != null) {
				boolean validaCNPJ = ValidaCNPJ.isCNPJ(this.objetoPagadorRecebedor.getCnpj());
				if(validaCNPJ) {
					pagadorRecebedorBD = pagadorRecebedorDao.findByFilter("cnpj", this.objetoPagadorRecebedor.getCnpj());
					if (pagadorRecebedorBD.size() > 0) {
						pagadorRecebedor = pagadorRecebedorBD.get(0);
					} else {
						populaReferenciaBancariaCNPJ();
						pagadorRecebedor = this.objetoPagadorRecebedor;
						registraPagador = true;
					}
				}
			}

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
		
		pagadorRecebedor.criarConjugeNoSistema();
	}
	
	public void criarCcbNosistema() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			CcbDao ccbDao = new CcbDao();
			
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {
				if(CommonsUtil.semValor(participante.getTipoOriginal())) {
					participante.setTipoOriginal(participante.getTipoParticipante());
				} else {
					participante.setTipoParticipante(participante.getTipoOriginal());
				}
			}
			
			ContasPagarDao cpDao = new ContasPagarDao();
			CcbProcessosJudiciaisDao pjDao = new CcbProcessosJudiciaisDao();
			for (CcbProcessosJudiciais processo : this.objetoCcb.getProcessosJucidiais()) {
				if(!CommonsUtil.semValor(processo.getContaPagar())) {
					ContasPagar conta = processo.getContaPagar();
					if(conta.getId() <= 0) {
						cpDao.create(conta);
					} else {
						cpDao.merge(conta);
					}
				}
				if(processo.getId() <= 0) {
					pjDao.create(processo);
				} else {
					pjDao.merge(processo);
				}
			}
			
			for (ContasPagar conta : this.objetoCcb.getDespesasAnexo2()) {
				if(conta.getId() <= 0) {
					cpDao.create(conta);
				} else {
					cpDao.merge(conta);
				}
			}
			
			if(!CommonsUtil.semValor(objetoCcb.getObjetoContratoCobranca())) {
				ContratoCobranca contrato = objetoCcb.getObjetoContratoCobranca();
				if(contrato.getId() > 0) {
					//if(!CommonsUtil.semValor(this.objetoCcb.getDespesasAnexo2())) {
					//	contrato.setListContasPagar(new HashSet<ContasPagar>(this.objetoCcb.getDespesasAnexo2()));
					//}
					
					if(!CommonsUtil.semValor(objetoCcb.getNumeroCcb())) {
						contrato.setNumeroContratoSeguro(objetoCcb.getNumeroCcb());
					}
					if(!CommonsUtil.semValor(objetoCcb.getDataDeEmissao())) {
						contrato.setDataInicio(objetoCcb.getDataDeEmissao());
					}
					if(!CommonsUtil.semValor(objetoCcb.getSistemaAmortizacao())) {
						contrato.setTipoCalculo(objetoCcb.getSistemaAmortizacao());
					}
					if(!CommonsUtil.semValor(objetoCcb.getPrazo())) {
						contrato.setQtdeParcelas(CommonsUtil.intValue(objetoCcb.getPrazo()));
					}
					if(!CommonsUtil.semValor(objetoCcb.getValorCredito())) {
						contrato.setValorCCB(objetoCcb.getValorCredito());
					}
					if(!CommonsUtil.semValor(objetoCcb.getTaxaDeJurosMes())) {
						contrato.setTxJurosParcelas(objetoCcb.getTaxaDeJurosMes());
					}
					if(!CommonsUtil.semValor(this.carencia)) {
						contrato.setMesesCarencia(CommonsUtil.intValue(this.carencia));
					}
					
					ContratoCobrancaDao cDao = new ContratoCobrancaDao();
					try {
						cDao.merge(contrato);
					} catch (TransientObjectException e) {
						contrato.toString();
						e.printStackTrace();
					} catch (DAOException e) {
						contrato.toString();
						e.printStackTrace();
					} 
				}
			} 
			//if(this.objetoCcb.getId() <= 0) {
				//this.objetoCcb.setId(ccbDao.idCcb());
			//}
			if (this.objetoCcb.getId() > 0) {
				ccbDao.merge(this.objetoCcb);
				System.out.println("CCB Merge ID: " + objetoCcb.getId() + " / "  + objetoCcb.getNumeroCcb() + " / "
						+ objetoCcb.getNumeroOperacao() + " / " + objetoCcb.getNomeEmitente() + " / " + this.tipoDownload);
			} else {
				ccbDao.create(this.objetoCcb);
				System.out.println("CCB Create ID: " + objetoCcb.getId() + " / "  + objetoCcb.getNumeroCcb() + " / "
						+ objetoCcb.getNumeroOperacao() + " / " + objetoCcb.getNomeEmitente() + " / " + this.tipoDownload);
			}
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "CCB: " + e.getCause(), ""));
		} finally {
			//context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Percelas Geradas com sucesso", ""));	
			if (this.objetoCcb.getId() > 0) {
				this.setAviso("CCB: Contrato salvo no sistema " + objetoCcb.getNumeroCcb() + " / "
					+ objetoCcb.getNumeroOperacao() + " / " + objetoCcb.getNomeEmitente() + " (" + objetoCcb.getId() + ")");
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "CCB: Contrato salvo no sistema", ""));
				System.out.println("CCB: Contrato salvo");
			} else {
				this.setAviso("CCB: Erro ao salver contrato no sistema " + objetoCcb.getNumeroCcb() + " / "
						+ objetoCcb.getNumeroOperacao() + " / " + objetoCcb.getNomeEmitente());
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "CCB: Erro ao salver contrato no sistema", ""));
				System.out.println("CCB: Erro ao salvar contrato");
			}		
		}
	}
	
	public String trocaValoresXWPF(String text, XWPFRun r, String valorEscrito, String valorSobrescrever) {
		if (text != null && text.contains(valorEscrito)) {
			text = text.replace(valorEscrito, valorSobrescrever);
			r.setText(text, 0);
		}
		return text;
	}
	
	public void handleFileUpload(FileUploadEvent event) {
		uploadedFile = event.getFile();
	    filesList.add(uploadedFile);
    }
	
	public void populateFiles(int index) throws IOException {
		uploadedFile = filesList.get(index);
		fileName = uploadedFile.getFileName();
	    fileType = uploadedFile.getContentType();
	    if(fileType.contains("png")) {
	    	fileTypeInt = 6;
	    	fileType = "png";
	    } else if(fileType.contains("jpeg")) {
	    	fileTypeInt = 5;
	    	fileType = "jpeg";
	    }
	    
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    if (uploadedFile != null) {
			RenderedImage picture = ImageIO.read((uploadedFile.getInputstream()));
			ImageIO.write(picture, "png", baos);
			baos.flush();
			// InputStream is = new ByteArrayInputStream(baos.toByteArray());
			baos.close();
			this.bis = new ByteArrayInputStream(baos.toByteArray());
		}
	}
	
	public void clearFiles() {
		uploadedFile = null;
	    fileName = null;
	    fileType = null;
	}
	
	public void removerArquivo(UploadedFile file) {
		this.getFilesList().remove(file);		
	}
	
	public String trocaValoresXWPF(String text, XWPFRun r, String valorEscrito, BigDecimal valorSobrescrever, String moeda) {
		if (text != null && text.contains(valorEscrito)) {
				text = text.replace(valorEscrito, CommonsUtil.formataValorMonetario(valorSobrescrever, moeda));
			r.setText(text, 0);
		}
		return text;
	}
	
	public String trocaValoresXWPFCci(String text, XWPFRun r, String valorEscrito, BigDecimal valorSobrescrever, String moeda) {
		if (text != null && text.contains(valorEscrito)) {
				text = text.replace(valorEscrito, CommonsUtil.formataValorMonetarioCci(valorSobrescrever, moeda) );
			r.setText(text, 0);
		}
		return text;
	}
	
	public String trocaValoresXWPF(String text, XWPFRun r, String valorEscrito, BigDecimal valorSobrescrever) {
		if (text != null && text.contains(valorEscrito)) {
			if(CommonsUtil.mesmoValor(valorEscrito, "tarifaAntecipada")) {
				text = text.replace(valorEscrito, CommonsUtil.formataValorMonetario(valorSobrescrever));
			} else {
				text = text.replace(valorEscrito, CommonsUtil.formataValorTaxa(valorSobrescrever));
			}
			r.setText(text, 0);
		}
		return text;
	}

	public String trocaValoresDinheiroExtensoXWPF(String text, XWPFRun r, String valorEscrito, BigDecimal valorSobrescrever) {
		if (text != null && text.contains("Extenso" + valorEscrito)) {
			if(CommonsUtil.semValor(valorSobrescrever)) {
				text = text.replace("Extenso" + valorEscrito , "Zero reais");
				r.setText(text, 0);
			} else {
				valorPorExtenso.setNumber(valorSobrescrever);
				text = text.replace("Extenso" + valorEscrito , valorPorExtenso.toString());
				r.setText(text, 0);	
			}	
		}
		return text;
	}
	
	public String trocaValoresTaxaExtensoXWPF(String text, XWPFRun r, String valorEscrito, BigDecimal valorSobrescrever) {
		if (text != null && text.contains("Extenso" + valorEscrito)) {
			if(CommonsUtil.semValor(valorSobrescrever)) {
				text = text.replace("Extenso" + valorEscrito, "Zero");
			} else {
				porcentagemPorExtenso.setNumber(valorSobrescrever);
				text = text.replace("Extenso" + valorEscrito, porcentagemPorExtenso.toString());
				
			}
		}
		r.setText(text, 0);
		return text;
	}
	
	public String trocaValoresNumeroExtensoXWPF(String text, XWPFRun r, String valorEscrito, String valorSobrescrever) {
		if (text != null && text.contains("Extenso" + valorEscrito )) {
			numeroPorExtenso.setNumber(BigDecimal.valueOf(CommonsUtil.doubleValue(valorSobrescrever)));
			text = text.replace("Extenso" + valorEscrito , numeroPorExtenso.toString());
			r.setText(text, 0);
		}
		return text;
	}
	
	public String trocaValoresXWPF(String text, XWPFRun r, String valorEscrito, Date valorSobrescrever) {
		if (text != null && text.contains(valorEscrito)) {
			text = text.replace(valorEscrito, CommonsUtil.formataData(valorSobrescrever, "dd/MM/yyyy"));
			r.setText(text, 0);
		}
		return text;
	}
	
	public String trocaValoresXWPF(String text, XWPFRun r, String valorEscrito, Integer valorSobrescrever) {
		if (text != null && text.contains(valorEscrito)) {
			text = text.replace(valorEscrito, CommonsUtil.stringValue(valorSobrescrever));
			r.setText(text, 0);
		}
		return text;
	}

	public String verificaEstadoCivil(Boolean sexo, String estadoCivil) {
		if(sexo == true) {
			if(CommonsUtil.mesmoValor(estadoCivil, "SOLTEIRO")) {
				return "SOLTEIRA";
			} else if(CommonsUtil.mesmoValor(estadoCivil, "CASADO")) {
				return "CASADA";
			} else if(CommonsUtil.mesmoValor(estadoCivil, "VIÚVO")) {
				return "VIÚVA";
			} else if(CommonsUtil.mesmoValor(estadoCivil, "DIVORCIADO")) {
				return "DIVORCIADA";
			} else {
				return estadoCivil;
			}
		} else {
			return estadoCivil;
		}
	}
	
	public String verificaNacionalidade(Boolean sexo, String nacionalidade) {
		if(sexo == true) {
			if(CommonsUtil.mesmoValor(nacionalidade, "brasileiro")) {
				return "brasileira";
			} else {
				return nacionalidade;
			}
		} else {
			return nacionalidade;
		}
	}
	
	public void adicionarEnter(String text, XWPFRun r) {
		if (text != null && text.contains("\n")) {
			String[] lines = text.split("\n");
			r.setText(lines[0], 0); // set first line into XWPFRun
			for (int i = 1; i < lines.length; i++) {
				// add break and insert new text
				r.addBreak();
				r.setText(lines[i]);
			}
		} else {
			r.setText(text, 0);
		}
	}
		
	
	public StreamedContent geraCartaSplitDinamica() throws IOException{
		try {
			XWPFDocument document = new XWPFDocument();	

			XWPFRun run;
			XWPFParagraph paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(100);
			run = paragraph.createRun();
			run.setText("Votorantim/SP, " + this.objetoCcb.getDataDeEmissao().getDate() + " de "
							+ CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase() + " de "
							+ (this.objetoCcb.getDataDeEmissao().getYear() + 1900) + ".");
			run.setFontSize(11);
			run.setBold(false);
			run.addCarriageReturn();
			XWPFRun run2 = paragraph.createRun();
			XWPFRun run3 = paragraph.createRun();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText("À");
			run.setFontSize(11);
			run.setBold(false);
			
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")) {
					if(CommonsUtil.semValor(this.objetoCcb.getNomeEmitente())) {
						this.objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
					}
					if(CommonsUtil.semValor(this.objetoCcb.getCpfEmitente())) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							this.objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
						}
					}
					if(CommonsUtil.semValor(this.objetoCcb.getCpfEmitente())) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCnpj())) {
							this.objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
						}
					}
				}
			}
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText("Galleria Sociedade de Crédito Direto S.A.");
			run.setFontSize(11);
			run.setBold(true);
			run.addCarriageReturn();
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText("Autorizamos a efetivação de transferência no valor de ");
			run.setFontSize(11);
			run.setBold(false);
			BigDecimal valorCartaSplit =   this.objetoCcb.getValorLiquidoCredito().add(this.objetoCcb.getValorDespesas());
			valorPorExtenso.setNumber(valorCartaSplit);
			run2 = paragraph.createRun();
			run2.setText(CommonsUtil.formataValorMonetario(valorCartaSplit, "R$ ") + " (" + valorPorExtenso.toString() + ")," );
			run2.setFontSize(11);
			run2.setBold(true);
			run = paragraph.createRun();
			run.setText(" conforme dados abaixo, crédito oriundo da CCI n° " + this.objetoCcb.getNumeroCcb() + ", datada de " + this.objetoCcb.getDataDeEmissao().getDate() + " de "
					+ CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase() + " de "
					+ (this.objetoCcb.getDataDeEmissao().getYear() + 1900) + ".");
			run.setFontSize(11);
			run.setBold(false);
			run.addCarriageReturn();
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(100);
			run = paragraph.createRun();
			run.setText("Contas a serem creditadas");
			run.setFontSize(11);
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText("Nome: Galleria Correspondente Bancário Eireli");
			run.setFontSize(11);
			run.setBold(false);
			run.addCarriageReturn();
			run.setText("CPF/CNPJ: 34.787.885/0001-32");
			run.addCarriageReturn();
			run.setText("Banco: ");
			run2 = paragraph.createRun();
			run2.setText("Banco do Brasil");
			run2.setFontSize(11);
			run2.setBold(true);
			run2.addCarriageReturn();
			run = paragraph.createRun();
			run.setText("Agência: 1515-6");
			run.addCarriageReturn();
			run.setText("C/C: 131094-1");
			run.addCarriageReturn();
			run.setText("Valor: ");
			run2 = paragraph.createRun();
			if(this.temCCBValor) {
				valorPorExtenso.setNumber(valorCartaSplit);
				run2.setText(CommonsUtil.formataValorMonetario(valorCartaSplit, "R$ ")  + " (" + valorPorExtenso.toString() + ") " );
			} else {
				valorPorExtenso.setNumber(this.objetoCcb.getValorDespesas());
				run2.setText(CommonsUtil.formataValorMonetario(this.objetoCcb.getValorDespesas(), "R$ ")  + " (" + valorPorExtenso.toString() + ") " );
			}
			run2.setFontSize(11);
			run2.setBold(true);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(100);
			run = paragraph.createRun();
			run.setText("Contas a serem creditadas (conta cliente no contrato Money)");
			run.setFontSize(11);
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText("Nome: " + this.objetoCcb.getNomeEmitente().toUpperCase());
			run.setFontSize(11);
			run.setBold(false);
			run.addCarriageReturn();
			run.setText("CPF/CNPJ: " + this.objetoCcb.getCpfEmitente());
			run.addCarriageReturn();
			run.setText("Banco: ");
			run2 = paragraph.createRun();
			run2.setText(this.objetoCcb.getNomeBanco() + "");
			run2.setFontSize(11);
			run2.setBold(true);
			run2.addCarriageReturn();
			run = paragraph.createRun();
			run.setText("Agência: " + this.objetoCcb.getAgencia());
			run.addCarriageReturn();
			run.setText("C/C: " + this.objetoCcb.getContaCorrente() + " Pix: " + this.objetoCcb.getPixBanco());
			run.addCarriageReturn();
			run.setText("Valor: ");
			valorPorExtenso.setNumber(this.objetoCcb.getValorLiquidoCredito());
			run2 = paragraph.createRun();
			if(this.temCCBValor) {
				run2.setText("R$ 0,00" + " (Zero reais) " );
			} else {
				run2.setText(CommonsUtil.formataValorMonetario(this.objetoCcb.getValorLiquidoCredito(), "R$ ")  + " (" + valorPorExtenso.toString() + ") " );
			}
			
			run2.setFontSize(11);
			run2.setBold(true);
			run3 = paragraph.createRun();
			run3.setText("* Credito será efetuado somente no registro da alienação Fiduciária da CCI "
					+ this.objetoCcb.getNumeroCcb() + " da matricula " 
					+ this.objetoCcb.getNumeroImovel()  + " do " 
					+ this.objetoCcb.getCartorioImovel() + "° RI de " 
					+ this.objetoCcb.getCidadeImovel()  + " - " 
					+ this.objetoCcb.getUfImovel() );
			run3.setFontSize(11);
			run3.setColor("ff0000");
			run3.setBold(true);		
			run3.addCarriageReturn();
			run3.addCarriageReturn();			
			run3.addCarriageReturn();			
			run3.addCarriageReturn();			
			run3.addCarriageReturn();			
			run3.addCarriageReturn();			
			run3.addCarriageReturn();
		
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText("_____________________________________________________________________________");
			run.setFontSize(11);
			run.setBold(false);
			run.addCarriageReturn();
			run = paragraph.createRun();
			run.setText("NOME/RAZÃO SOCIAL: " + this.objetoCcb.getNomeEmitente().toUpperCase());
			run.setFontSize(11);
			run.setBold(false);
			run.addCarriageReturn();
			run = paragraph.createRun();
			run.setText("CPF/CNPJ: " + this.objetoCcb.getCpfEmitente());
			run.setFontSize(11);
			run.setBold(false);
			run.addCarriageReturn();
			run = paragraph.createRun();
			run.setText("(EMITENTE)");
			run.setFontSize(11);
			run.setBold(false);
			run.addCarriageReturn();
			
			if(!CommonsUtil.semValor(this.objetoCcb.getObjetoContratoCobranca())) {
				ContratoCobranca contrato = this.objetoCcb.getObjetoContratoCobranca();
				//this.objetoContratoCobranca = cDao.findById(this.objetoCcb.getObjetoContratoCobranca().getId());
				
				contrato.setValorCartaSplit(this.objetoCcb.getValorLiquidoCredito());
				contrato.setNomeBancarioCartaSplit(this.objetoCcb.getNomeEmitente());
				contrato.setCpfCnpjBancarioCartaSplit(this.objetoCcb.getCpfEmitente());
				contrato.setBancoBancarioCartaSplit(this.objetoCcb.getNomeBanco());
				contrato.setAgenciaBancarioCartaSplit(this.objetoCcb.getAgencia());
				contrato.setContaBancarioCartaSplit(this.objetoCcb.getContaCorrente());		
				
				//cDao.merge(contrato);
				//this.objetoCcb.setObjetoContratoCobranca(contrato);	
			}
			
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
					FacesContext.getCurrentInstance());
			String nomeSemvirgula = this.objetoCcb.getNomeEmitente();
			if(nomeSemvirgula.contains(",")) {
				nomeSemvirgula = nomeSemvirgula.replace(",", "");
		    }
			gerador.open(String.format("Galleria Bank - Carta Split %s.docx", ""));
			gerador.feed(new ByteArrayInputStream(out.toByteArray()));
			gerador.close();
			
			criarCcbNosistema();
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		

		return null;
	}
	
	public StreamedContent geraAnexoII() throws IOException {
		try {
			XWPFDocument document = new XWPFDocument();
			XWPFHeaderFooterPolicy headerFooterPolicy = document.getHeaderFooterPolicy();
			if (headerFooterPolicy == null)
				headerFooterPolicy = document.createHeaderFooterPolicy();

			XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);
			XWPFParagraph paragraphHeader = header.createParagraph();
			paragraphHeader.setAlignment(ParagraphAlignment.CENTER);
			XWPFRun runHeader = paragraphHeader.createRun();
			runHeader.addPicture(getClass().getResourceAsStream("/resource/GalleriaBank.png"), 6, "Galleria Bank",
					Units.toEMU(130), Units.toEMU(72));

			geraPaginaContratoII(document, "8880F4", true);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			document.write(out);
			document.close();

			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
					FacesContext.getCurrentInstance());
			String nomeSemvirgula = this.objetoCcb.getNomeEmitente();
			if(nomeSemvirgula.contains(",")) {
				nomeSemvirgula = nomeSemvirgula.replace(",", "");
		    }
			gerador.open(String.format("Galleria Bank - AnexoII %s.docx", ""));
			gerador.feed(new ByteArrayInputStream(out.toByteArray()));
			gerador.close();

			criarCcbNosistema();
			
		} catch (JDBCException jdbce) {
		    jdbce.getSQLException().getNextException().printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
			e.getCause().printStackTrace();
		} 

		return null;
	}

	private void geraPaginaContratoII(XWPFDocument document, String cor, boolean gerarAssinatura) throws IOException {
		XWPFRun run;
		XWPFParagraph paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(100);
		
		String documento = "";
		
		for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {				
			if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")) {
				if(CommonsUtil.semValor(this.objetoCcb.getNomeEmitente())) {
					this.objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
				}
				
				if(CommonsUtil.semValor(this.objetoCcb.getCpfEmitente())) {
					if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
						this.objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
						documento = "CPF: ";
					} else {
						this.objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
						documento = "CNPJ: ";
					}
				}
			}
		}
		
		run = paragraph.createRun();
		run.setText("ANEXO II");
		run.setFontSize(11);
		run.setBold(true);
		XWPFRun run2 = paragraph.createRun();
		
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		
		run.addCarriageReturn();
		run.setText("CÉDULA DE CRÉDITO IMOBILIÁRIO Nº " + this.objetoCcb.getNumeroCcb());
		run.setFontSize(11);
		run.setBold(true);
		run.addCarriageReturn();
		run.setText("DESPESAS ACESSÓRIAS (DEVIDAS A TERCEIROS)");
		run.setFontSize(11);
		run.setBold(true);
		
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.BOTH);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(200);
		
		run = paragraph.createRun();
		run.setText("Para todos os fins e efeitos de direito, conforme previsto na ");
		run.setFontSize(11);
		run.setBold(false);
		
		run2 = paragraph.createRun();
		run2.setFontSize(11);
		run2.setText("cláusula 3.5 do Quadro Resumo da Cédula de Crédito Imobiliário n° " + this.objetoCcb.getNumeroCcb() 
			+ ", datada de " + CommonsUtil.formataData(this.objetoCcb.getDataDeEmissao(), "dd/MM/yyyy") );
		run2.setBold(true);
		
		run = paragraph.createRun();
		run.setText(" (CCI), autorizo o pagamento das despesas acessórias e dos "
				+ "compromissos diversos abaixo relacionados e aprovados por mim previamente no valor total de");
		run.setFontSize(11);
		run.setBold(false);			
		
		calcularValorDespesa();

		valorPorExtenso.setNumber(this.objetoCcb.getValorDespesas()); 
		run2 = paragraph.createRun();
		run2.setFontSize(11);
		run2.setText(" "+ CommonsUtil.formataValorMonetario(this.objetoCcb.getValorDespesas(), "R$ ") + " ("+ valorPorExtenso.toString() +"), ");
		run2.setBold(true);
		
		run = paragraph.createRun();
		run.setText("por meio do crédito oriundo da CCI. O montante total necessário para o pagamento"
				+ " das despesas acessórias e dos compromissos diversos será transferido para a conta"
				+ " da Galleria Correspondente Bancário Sociedade Unipessoal Ltda, CNPJ 34.787.885/0001-32, Banco do Brasil"
				+ " – Ag: 1515-6 C/C: 131094-1, que, na condição de Correspondente Bancário da Galleria Sociedade de Crédito Direto,"
				+ " será a responsável por efetuar todos os pagamentos devidamente especificados na"
				+ " tabela abaixo:");
		run.setFontSize(11);
		run.setBold(false);	
		
		XWPFTable table = document.createTable();
		table.setWidth((int) (6.1 * 1440));
		table.getCTTbl().getTblPr().getTblW().unsetType();
		setTableAlign(table, ParagraphAlignment.CENTER);

		table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(6000));
		table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(2500));
		
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		
		// create first row
		XWPFTableRow tableRow = table.getRow(0);

		tableRow.getCell(0).setParagraph(paragraph);
		tableRow.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
		tableRow.getCell(0).setColor(cor);
		//tableRow.getCell(0).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(3000) ));
		run = tableRow.getCell(0).getParagraphArray(0).createRun();
		run.setFontSize(12);
		run.setBold(true);
		run.setColor("ffffff");
		run.setText("Descrição da despesa ou do Compromisso Diverso");
		
		tableRow.addNewTableCell();
		tableRow.addNewTableCell();

		tableRow.getCell(1).setParagraph(paragraph);
		tableRow.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
		tableRow.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));
		tableRow.getCell(1).setColor(cor);
		run = tableRow.getCell(1).getParagraphArray(0).createRun();
		run.setFontSize(12);
		run.setBold(true);
		run.setText("Forma de Pagamento");
		run.setColor("ffffff");	
		
		tableRow.getCell(2).setParagraph(paragraph);
		tableRow.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));
		tableRow.getCell(2).setColor(cor);
		tableRow.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
		run = tableRow.getCell(2).getParagraphArray(0).createRun();
		run.setFontSize(12);
		run.setBold(true);
		run.setColor("ffffff");
		run.setText("Valor");
				
		for(ContasPagar despesa : objetoCcb.getDespesasAnexo2()) {
			XWPFTableRow tableRow1 = table.createRow();
			
			tableRow1.getCell(0).setParagraph(paragraph);
			tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			
			run = tableRow1.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setColor("000000");
			if(CommonsUtil.mesmoValor(despesa.getDescricao(), "Cartório")) {
				run.setText("Custas Cartorárias");
			} else if(CommonsUtil.mesmoValor(despesa.getDescricao(), "Certidão de Casamento")) {
				run.setText("Certidão de estado civil");
			} else if(CommonsUtil.mesmoValor(despesa.getDescricao(), "IPTU")) {
				run.setText("IPTU em Atraso");
			} else if(CommonsUtil.mesmoValor(despesa.getDescricao(), "Condomínio")) {
				run.setText("Condomínio em Atraso");
			} else {
				run.setText(despesa.getDescricao());
			}
			
			tableRow1.getCell(1).setParagraph(paragraph);
			tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));
			run = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			if(CommonsUtil.mesmoValor(despesa.getFormaTransferencia(), "TED")) {
				run.setText("Ted no "+ despesa.getBancoTed() +" AG: "+ despesa.getAgenciaTed()
				+" C/C: "+ despesa.getContaTed() + " Chave Pix:" + despesa.getPix() + " " + despesa.getNomeTed() 
				+" CPF/CNPJ: "+ despesa.getCpfTed()); 
			} else {
				run.setText(despesa.getFormaTransferencia());
			}
			run.setColor("000000");
			if(CommonsUtil.mesmoValor(despesa.getDescricao(), "Crédito CCI")) {
				run2 = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run2.addBreak();
				run2.addBreak();
				run2.setText("* Credito será efetuado somente no registro da alienação Fiduciária da CCI " + this.objetoCcb.getNumeroCcb() 
						+ " da matricula " + this.objetoCcb.getNumeroImovel() + " do "+ this.objetoCcb.getCartorioImovel() 
						+ "° Cartório de Registro de Imóveis de " + this.objetoCcb.getCidadeImovel() + " - " + this.objetoCcb.getUfImovel() + "* ");
				run2.setColor("FF0000");
			}
			
			tableRow1.getCell(2).setParagraph(paragraph);
			tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			tableRow1.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));
			run = tableRow1.getCell(2).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setColor("000000");
			run.setText(CommonsUtil.formataValorMonetario(despesa.getValor(), "R$ "));
		}
		
		for(CcbProcessosJudiciais processo : objetoCcb.getProcessosJucidiais()) {
			ContasPagar despesa = processo.getContaPagar();
			if( !processo.isSelecionadoComite() || CommonsUtil.semValor(despesa)) {
				continue;
			}
			
			XWPFTableRow tableRow1 = table.createRow();
			
			tableRow1.getCell(0).setParagraph(paragraph);
			tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			
			run = tableRow1.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText(despesa.getDescricao());
			run.setColor("000000");
			
			tableRow1.getCell(1).setParagraph(paragraph);
			tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));
			run = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText(despesa.getFormaTransferencia());
			run.setColor("000000");
			
			tableRow1.getCell(2).setParagraph(paragraph);
			tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			tableRow1.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));
			run = tableRow1.getCell(2).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setColor("000000");
			run.setText(CommonsUtil.formataValorMonetario(despesa.getValor(), "R$ "));
		}
		
		
		if (gerarAssinatura) {
			XWPFTableRow tableRow1 = table.createRow();

			tableRow1.getCell(1).setParagraph(paragraph);
			tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcW()
					.setW(BigInteger.valueOf(CommonsUtil.longValue(2800)));
			tableRow1.getCell(1).setColor(cor);

			run = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setBold(true);
			run.setText("Total");
			run.setColor("ffffff");

			tableRow1.getCell(2).setParagraph(paragraph);
			tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			tableRow1.getCell(2).getCTTc().addNewTcPr().addNewTcW()
					.setW(BigInteger.valueOf(CommonsUtil.longValue(2800)));
			tableRow1.getCell(2).setColor(cor);

			run = tableRow1.getCell(2).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setColor("ffffff");
			run.setBold(true);
			run.setText(CommonsUtil.formataValorMonetario(this.objetoCcb.getValorDespesas(), "R$ "));

			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);

			run = paragraph.createRun();
			run.addCarriageReturn();
			run.addCarriageReturn();
			run.addCarriageReturn();
			run.addCarriageReturn();
			run.addCarriageReturn();

			run.setText("_____________________________________________________________________________");
			run.setFontSize(11);
			run.setBold(false);
			run.addCarriageReturn();

			run2 = paragraph.createRun();
			run2.setColor("000000");
			run2.setFontSize(12);
			run2.setText("" + this.objetoCcb.getNomeEmitente().toUpperCase());
			run2.setBold(true);
			run2.addCarriageReturn();
			run2.setText(documento + this.objetoCcb.getCpfEmitente());
			
			if (!CommonsUtil.semValor(this.objetoCcb.getObjetoContratoCobranca())) {
				ContratoCobranca contrato = this.objetoCcb.getObjetoContratoCobranca();
				contrato.setContaPagarValorTotal(this.objetoCcb.getValorDespesas());
			}
		}
	}
	
	public StreamedContent geraCciDinamica() throws IOException {
		try {
			XWPFDocument document = new XWPFDocument();
			XWPFHeaderFooterPolicy headerFooterPolicy = document.getHeaderFooterPolicy();
			if (headerFooterPolicy == null)
				headerFooterPolicy = document.createHeaderFooterPolicy();


			XWPFRun run;
			XWPFParagraph paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			
			@SuppressWarnings("unused")
			String documento = "";
			
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")) {
					if(CommonsUtil.semValor(this.objetoCcb.getNomeEmitente())) {
						this.objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
					}
					
					if(CommonsUtil.semValor(cpfEmitente)) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							this.objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
							documento = "CPF: ";
						} else {
							this.objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
							documento = "CNPJ: ";
						}
					}
				}
			}
			
			run = paragraph.createRun();
			run.addBreak();
			run.addBreak();
			run.setText("INSTRUMENTO PARTICULAR DE EMPRÉSTIMO COM CONSTITUIÇÃO DE ALIENAÇÃO FIDUCIÁRIA"
					+ " EM GARANTIA COM EMISSÃO DE CÉDULA DE CRÉDITO IMOBILIÁRIO E OUTRAS AVENÇAS");
			run.setFontSize(12);
			run.setBold(true);
			run.addBreak();			
			
			XWPFTable table = document.createTable();
			CTTblPr cttblPr = table.getCTTbl().addNewTblPr();
			cttblPr.addNewTblW().setW(BigInteger.ZERO);
			cttblPr.getTblW().setType(STTblWidth.AUTO);
			cttblPr.addNewTblCellMar().addNewLeft().setW(BigInteger.valueOf(70));
			cttblPr.getTblCellMar().getLeft().setType(STTblWidth.DXA);
			cttblPr.getTblCellMar().addNewRight().setW(BigInteger.valueOf(70));
			cttblPr.getTblCellMar().getRight().setType(STTblWidth.DXA);
	
			CTTblGrid tblGrid = table.getCTTbl().addNewTblGrid();
			tblGrid.addNewGridCol().setW(BigInteger.valueOf(4389));
			tblGrid.addNewGridCol().setW(BigInteger.valueOf(5240));
			
			setTableAlign(table, ParagraphAlignment.CENTER);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			
			// create first row
			XWPFTableRow tableRow = table.getRow(0);
			tableRow.getCtRow().addNewTrPr().addNewTrHeight().setVal(BigInteger.valueOf(315));
			tableRow.getCell(0).setParagraph(paragraph);
			tableRow.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			tableRow.getCell(0).getCTTc().getTcPr().addNewTcW().setW(BigInteger.ZERO);
			tableRow.getCell(0).getCTTc().getTcPr().getTcW().setType(STTblWidth.AUTO);
			tableRow.getCell(0).getCTTc().getTcPr().addNewNoWrap();
			tableRow.getCell(0).getCTTc().getTcPr().addNewHideMark();
			tableRow.getCell(0).setColor("8880F4");
			run = tableRow.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setBold(true);
			run.setColor("ffffff");
			run.setText("Descrição da despesa ou do Compromisso Diverso");
			
			tableRow.addNewTableCell();

			tableRow.getCell(1).setParagraph(paragraph);
			tableRow.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			tableRow.getCell(1).setColor("8880F4");
			tableRow.getCell(1).getCTTc().getTcPr().addNewTcW().setW(BigInteger.ZERO);
			tableRow.getCell(1).getCTTc().getTcPr().getTcW().setType(STTblWidth.AUTO);
			tableRow.getCell(1).getCTTc().getTcPr().addNewNoWrap();
			tableRow.getCell(1).getCTTc().getTcPr().addNewHideMark();
			run = tableRow.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setBold(true);
			run.setText("Forma de Pagamento");
			run.setColor("ffffff");	
			
			XWPFTableRow tableRow1 = table.createRow();
			tableRow1.getCtRow().addNewTrPr().addNewTrHeight().setVal(BigInteger.valueOf(315));
			tableRow1.getCell(0).setParagraph(paragraph);
			tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			tableRow1.getCell(0).getCTTc().getTcPr().addNewTcW().setW(BigInteger.ZERO);
			tableRow1.getCell(0).getCTTc().getTcPr().getTcW().setType(STTblWidth.AUTO);
			tableRow1.getCell(0).getCTTc().getTcPr().addNewNoWrap();
			tableRow1.getCell(0).getCTTc().getTcPr().addNewHideMark();
			
			run = tableRow1.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setColor("000000");
			run.setText("Custas Cartorárias");

			tableRow1.getCell(1).setParagraph(paragraph);
			tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			tableRow1.getCell(1).getCTTc().getTcPr().addNewTcW().setW(BigInteger.ZERO);
			tableRow1.getCell(1).getCTTc().getTcPr().getTcW().setType(STTblWidth.AUTO);
			tableRow1.getCell(1).getCTTc().getTcPr().addNewNoWrap();
			tableRow1.getCell(1).getCTTc().getTcPr().addNewHideMark();
			run = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("Boleto");
			run.setColor("000000");	
				
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
			String nomeSemvirgula = this.objetoCcb.getNomeEmitente();
			if(nomeSemvirgula.contains(",")) {
				nomeSemvirgula = nomeSemvirgula.replace(",", "");
		    }
			gerador.open(String.format("Galleria Bank - AnexoII %s.docx", ""));
			gerador.feed(new ByteArrayInputStream(out.toByteArray()));
			gerador.close();
			criarCcbNosistema();
		} catch (JDBCException jdbce) {
		    jdbce.getSQLException().getNextException().printStackTrace();
		} catch (Throwable e) {
			e.getCause().printStackTrace();
		} 
		return null;
	}
		
	public void setTableAlignment(XWPFTable table, STJc.Enum justification) {
	    CTTblPr tblPr = table.getCTTbl().getTblPr();
	    CTJc jc = (tblPr.isSetJc() ? tblPr.getJc() : tblPr.addNewJc());
	    jc.setVal(justification);
	}
	
	public void setTableAlign(XWPFTable table,ParagraphAlignment align) {
	    CTTblPr tblPr = table.getCTTbl().getTblPr();
	    CTJc jc = (tblPr.isSetJc() ? tblPr.getJc() : tblPr.addNewJc());
	    STJc.Enum en = STJc.Enum.forInt(align.getValue());
	    jc.setVal(en);
	}
		
	private void geraParagrafoPF(XWPFRun run2, CcbParticipantes participante){
		run2.setFontSize(12);
		String filho;
		String nacionalidade = null;
		String estadoCivilStr = "";
		String conjugeStr = "";
		PagadorRecebedor pessoa = participante.getPessoa();
		
		if(participante.isFeminino()) {
			filho = "filha";
			if (CommonsUtil.mesmoValor(participante.getNacionalidade(), "brasileiro")) 
				nacionalidade = "brasileira";
			if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "CASADO")) {
				estadoCivilStr = "casada";
			} else {
				if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "SOLTEIRO"))
					estadoCivilStr = "solteira";
				else if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "VIÚVO"))
					estadoCivilStr = "viúva";
				else if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "DIVORCIADO"))
					estadoCivilStr = "divorciada";
				else if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "SEPARADO"))
					estadoCivilStr = "separada";
				else if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "SEPARADO JUDICIALMENTE"))
					estadoCivilStr = "separada judicialmente";
			}
		} else {
			filho = "filho";
			nacionalidade = participante.getNacionalidade();
			estadoCivilStr = pessoa.getEstadocivil().toLowerCase();
		}
		
		if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "CASADO")) {
			if(!CommonsUtil.semValor(pessoa.getDataCasamento())) {
				estadoCivilStr = estadoCivilStr + " em " + CommonsUtil.formataData(pessoa.getDataCasamento(), "dd/MM/yyyy");
			}
			
			if(!CommonsUtil.mesmoValor(pessoa.getRegimeCasamento(), "parcial de bens")) {
				conjugeStr = ", sob o regime " + pessoa.getRegimeCasamento() + ", na vigência da lei 6.515/77 (" + 
					pessoa.getNomeConjuge() + " " + pessoa.getCpfConjuge() + "), conforme pacto antenupcial registrado no "+
					pessoa.getRegistroPactoAntenupcial() + ", sob livro " + pessoa.getLivroPactoAntenupcial() + ", folhas " + 
					pessoa.getFolhasPactoAntenupcial() + ", datada de " + CommonsUtil.formataData(pessoa.getDataPactoAntenupcial()) ;
			} else {
				conjugeStr = ", sob o regime " + pessoa.getRegimeCasamento() + ", na vigência da lei 6.515/77 (" + 
						pessoa.getNomeConjuge() + " " + pessoa.getCpfConjuge() + ")" ;
			}
		} else {
			if(participante.isUniaoEstavel()) {
				estadoCivilStr = estadoCivilStr + " convivente em união estável";
			} else {
				estadoCivilStr = estadoCivilStr + " não convivente em união estável";
			}
		}
		
		run2.setText( filho + " de " + pessoa.getNomeMae() + " e " + pessoa.getNomePai() + ", "
				+ nacionalidade + ", "+ pessoa.getAtividade() + ", "+ estadoCivilStr 
				+ conjugeStr + ","
				+ " portador(a) da Cédula de Identidade RG nº "+ pessoa.getRg() + " " + pessoa.getOrgaoEmissorRG() + ","
				+ " inscrito(a) no CPF/MF sob o nº "+ pessoa.getCpf() +", endereço eletrônico: "+ pessoa.getEmail() +","
				+ " residente e domiciliado à "+ pessoa.getEndereco() +", nº "+ pessoa.getNumero() +", "
				+ pessoa.getComplemento() + ", "+ pessoa.getBairro() + ", " 
				+ pessoa.getCidade()+"/"+pessoa.getEstado()+", CEP "+ pessoa.getCep()+"; ");
	}
	
	public StreamedContent geraCci() throws IOException{
		try {
			XWPFDocument document;	
			XWPFRun run;
			XWPFRun run2;
			XWPFRun run3;
			List<CcbParticipantes> segurados = new ArrayList<CcbParticipantes>();
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "TERCEIRO GARANTIDOR")
						|| CommonsUtil.mesmoValor(participante.getTipoParticipante(), "DEVEDOR FIDUCIANTE") ) {
					this.objetoCcb.setTerceiroGarantidor(true);
					participante.setTipoParticipante("DEVEDOR FIDUCIANTE");
					segurados.add(participante);
				} else if(CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")){
					segurados.add(participante);
				}
			}
			if(this.objetoCcb.isTerceiroGarantidor()) {
				document = new XWPFDocument(getClass().getResourceAsStream("/resource/CciTg.docx"));
			} else {
				if ( CommonsUtil.semValor( this.objetoCcb.getProcessosJucidiais() ) )
					document = new XWPFDocument(getClass().getResourceAsStream("/resource/Cci.docx"));
				else
					document = new XWPFDocument(getClass().getResourceAsStream("/resource/CciComProcesso.docx"));
			}		
			
			String numerosProcessos = "";
			BigDecimal totalProcessos = BigDecimal.ZERO;
			if (!CommonsUtil.semValor(this.objetoCcb.getProcessosJucidiais())) {
				for (CcbProcessosJudiciais processo : this.objetoCcb.getProcessosJucidiais()) {
					if (CommonsUtil.semValor(processo.getValorAtualizado())) {
						continue;
					}
					numerosProcessos = numerosProcessos + ((!CommonsUtil.semValor(numerosProcessos)) ? ", " : "")
							+ "Nº " + CommonsUtil.stringValueVazio(processo.getNumero()) + " ";
					totalProcessos = totalProcessos.add(processo.getValorAtualizado());
				}
				numerosProcessos = numerosProcessos.trim();
			}

			
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Calibri");
			fonts.setAscii("Calibri");
			fonts.setEastAsia("Calibri");
			fonts.setCs("Calibri");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			organizaSegurados(segurados);
		
			int indexSegurados = 41;
			
			for(Segurado segurado : objetoCcb.getListSegurados()) {
				XWPFTable table = document.getTables().get(0);
				table.insertNewTableRow(indexSegurados);
				XWPFTableRow tableRow1 = table.getRow(indexSegurados);
				XWPFParagraph paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.LEFT);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				tableRow1.createCell();
				tableRow1.getCell(0).setParagraph(paragraph);
				tableRow1.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				CTTcBorders border = tableRow1.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();
				border.addNewRight().setVal(STBorder.SINGLE);
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);
				border.addNewLeft().setVal(STBorder.SINGLE);		
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setFontFamily("Calibri");
				run.setText("Nome:");
				tableRow1.createCell();////////////////////////////////////////////////////////////////////////
				tableRow1.getCell(1).setParagraph(paragraph);
				tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				border = tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcBorders();
				border.addNewRight().setVal(STBorder.SINGLE);
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);
				border.addNewLeft().setVal(STBorder.SINGLE);
				run = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setFontFamily("Calibri");
				run.setText(segurado.getPessoa().getNome());
				indexSegurados++;////////////////////////////////////////////////////////////////////////////////
				table.insertNewTableRow(indexSegurados);
				XWPFTableRow tableRow2 = table.getRow(indexSegurados);				
				tableRow2.createCell();
				tableRow2.getCell(0).setParagraph(paragraph);
				tableRow2.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow2.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				border = tableRow2.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();
				border.addNewRight().setVal(STBorder.SINGLE);
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);
				border.addNewLeft().setVal(STBorder.SINGLE);	
				run = tableRow2.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setFontFamily("Calibri");
				run.setText("Percentual:");
				tableRow2.createCell();//////////////////////////////////////////////////////////////////////////
				tableRow2.getCell(1).setParagraph(paragraph);
				tableRow2.getCell(1).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow2.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				border = tableRow2.getCell(1).getCTTc().addNewTcPr().addNewTcBorders();
				border.addNewRight().setVal(STBorder.SINGLE);
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);
				border.addNewLeft().setVal(STBorder.SINGLE);			
				run = tableRow2.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetario(segurado.getPorcentagemSegurador()) + "%");
				indexSegurados++;
			}
			
			
		
			
			XWPFTable table = document.getTables().get(0);
			XWPFTableRow tableRow1 = table.getRow(3);
			XWPFParagraph paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			int iParticipante = 0;
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {										
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();	
				run.setFontSize(12);
				run.setText(alphabet[iParticipante] + ") ");
				run.setBold(true);
				run2 = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setText(" " + participante.getPessoa().getNome().toUpperCase() + ", ");
				//run2.setFontFamily("Calibri");
				if (!participante.isEmpresa()) {
					geraParagrafoPF(run2, participante);
					run2.addCarriageReturn();
				} else {
					run2.setFontSize(12);
					PagadorRecebedor pessoa = participante.getPessoa();
					String socios = "";
					if (participante.getSocios().size() > 1) {
						socios = "pelos seus sócios, ";
					} else if(participante.getSocios().size() > 0){
						if (participante.getSocios().iterator().next().isFeminino()) {
							socios = "pela sua única sócia, ";
						} else {
							socios = "pelo seu único sócio, ";
						}
					} else {
						socios = "";
					}
					run2.setText(participante.getTipoEmpresa() + ", devidamente inscrito no CNPJ sob n° "
							+ pessoa.getCnpj() + ", com sede em " + pessoa.getEndereco() + ", " + "n° "
							+ pessoa.getNumero() + ", Sala " + participante.getSalaEmpresa() + ", " + pessoa.getBairro()
							+ ", " + pessoa.getCidade() + " - " + pessoa.getEstado() + ", CEP " + pessoa.getCep()
							+ "; neste ato representada " + socios);
					for (CcbParticipantes sociosParticipante : participante.getSocios()) {
						XWPFRun runSocios = tableRow1.getCell(0).getParagraphArray(0).createRun();
						runSocios.setFontSize(12);
						runSocios.setText(" " + sociosParticipante.getPessoa().getNome().toUpperCase() + ", ");
						runSocios.setBold(true);
						XWPFRun runSociosNome = tableRow1.getCell(0).getParagraphArray(0).createRun();
						geraParagrafoPF(runSociosNome, sociosParticipante);
						runSociosNome.addCarriageReturn();
					}
				}									
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")) {
					if(CommonsUtil.semValor(this.objetoCcb.getNomeEmitente())) {
						this.objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
					}
					if(CommonsUtil.semValor(cpfEmitente)) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							this.objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
						} else {
							this.objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
						}
					}
					if(CommonsUtil.semValor(this.objetoCcb.getTipoPessoaEmitente())) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							this.objetoCcb.setTipoPessoaEmitente("PF");
						} else {
							this.objetoCcb.setTipoPessoaEmitente("PJ");
						}
					}
					
					participante.setTipoParticipante("DEVEDOR FIDUCIANTE");
					this.objetoCcb.setTipoParticipanteEmitente("DEVEDOR FIDUCIANTE");
				}
				run3 = tableRow1.getCell(0).getParagraphArray(0).createRun();	
				run3.setFontSize(12);
				run3.setText(" (“" + participante.getTipoParticipante() + "”)");
				run3.setBold(true);
				run3.addBreak();
				iParticipante++;
			}
			
			for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);
			            
			            text = trocaValoresXWPF(text, r, "nomeEmitente", this.objetoCcb.getNomeEmitente().toUpperCase());	 		
			            text = trocaValoresXWPF(text, r, "porcentagemImovel", CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getPorcentagemImovel(), ""));	 		
			            text = trocaValoresTaxaExtensoXWPF(text, r, "PorcentagemImovel", this.objetoCcb.getPorcentagemImovel());
						text = trocaValoresXWPF(text, r, "emissaoDia", this.objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (this.objetoCcb.getDataDeEmissao().getYear() + 1900));
						
						text = trocaValoresXWPF(text, r, "numerosProcessos",numerosProcessos);
						text = trocaValoresXWPF(text, r, "totalProcessos", CommonsUtil.formataValorMonetario(totalProcessos));
						
			        }
			    }
			}	
			
			
			
			BigDecimal taxaAdm = SiscoatConstants.TAXA_ADM;
			if(!CommonsUtil.semValor(this.objetoCcb.getPrazo()) && !CommonsUtil.semValor(this.objetoCcb.getNumeroParcelasPagamento())) {
				taxaAdm = taxaAdm.multiply(BigDecimal.valueOf( Long.parseLong(CommonsUtil.somenteNumeros(this.objetoCcb.getPrazo())) - Long.parseLong(CommonsUtil.somenteNumeros(this.objetoCcb.getNumeroParcelasPagamento())) + 1));
			} 
			BigDecimal totalPrimeiraParcela = BigDecimal.ZERO;

			if (!CommonsUtil.semValor(this.objetoCcb.getValorMipParcela()))
				totalPrimeiraParcela = this.objetoCcb.getValorMipParcela();
			if (!CommonsUtil.semValor(this.objetoCcb.getValorDfiParcela()))
				totalPrimeiraParcela = totalPrimeiraParcela.add(this.objetoCcb.getValorDfiParcela());
			if (!CommonsUtil.semValor(this.objetoCcb.getValorParcela()))
				totalPrimeiraParcela = totalPrimeiraParcela.add(this.objetoCcb.getValorParcela());
			totalPrimeiraParcela = totalPrimeiraParcela.add(taxaAdm);
						
		    for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
								String text = r.getText(0);										
								
								text = trocaValoresXWPFCci(text, r, "valorCredito", this.objetoCcb.getValorCredito(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorCredito", this.objetoCcb.getValorCredito());									
								text = trocaValoresXWPFCci(text, r, "valorLiquidoCredito", this.objetoCcb.getValorLiquidoCredito(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorLiquidoCredito", this.objetoCcb.getValorLiquidoCredito());								
								text = trocaValoresXWPFCci(text, r, "custoEmissao", this.objetoCcb.getCustoEmissao(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "CustoEmissao", this.objetoCcb.getCustoEmissao());	
								text = trocaValoresXWPFCci(text, r, "valorIOF", this.objetoCcb.getValorIOF(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorIOF", this.objetoCcb.getValorIOF());	
								text = trocaValoresXWPFCci(text, r, "valorDespesas", this.objetoCcb.getValorDespesas(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorDespesas", this.objetoCcb.getValorDespesas());	
								
								text = trocaValoresXWPF(text, r, "titularConta", this.objetoCcb.getTitularConta());
								text = trocaValoresXWPF(text, r, "agencia", this.objetoCcb.getAgencia());
								text = trocaValoresXWPF(text, r, "contaCorrente", this.objetoCcb.getContaCorrente());					
								text = trocaValoresXWPF(text, r, "nomeBanco", this.objetoCcb.getNomeBanco());
								text = trocaValoresXWPF(text, r, "pixBanco", this.objetoCcb.getPixBanco());
								
								text = trocaValoresXWPF(text, r, "prazoContrato", this.objetoCcb.getPrazo());
								text = trocaValoresXWPF(text, r, "numeroParcelasPagamento", this.objetoCcb.getNumeroParcelasPagamento());
								text = trocaValoresXWPF(text, r, "vencimentoPrimeiraParcelaPagamento", this.objetoCcb.getVencimentoPrimeiraParcelaPagamento());
								text = trocaValoresXWPF(text, r, "taxaDeJurosMes", this.objetoCcb.getTaxaDeJurosMes());
								text = trocaValoresXWPF(text, r, "taxaDeJurosAno", this.objetoCcb.getTaxaDeJurosAno());
								text = trocaValoresXWPF(text, r, "cetMes", this.objetoCcb.getCetMes());
								text = trocaValoresXWPF(text, r, "cetAno", this.objetoCcb.getCetAno());
								
								text = trocaValoresXWPFCci(text, r, "totalPrimeiraParcela", totalPrimeiraParcela, "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "TotalPrimeiraParcela", totalPrimeiraParcela);	
								text = trocaValoresXWPFCci(text, r, "valorMipParcela", this.objetoCcb.getValorMipParcela(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorMipParcela", this.objetoCcb.getValorMipParcela());			
								text = trocaValoresXWPFCci(text, r, "valorDfiParcela", this.objetoCcb.getValorDfiParcela(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorDfiParcela", this.objetoCcb.getValorDfiParcela());
								text = trocaValoresXWPFCci(text, r, "valorParcela", this.objetoCcb.getValorParcela(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorParcela", this.objetoCcb.getValorParcela());		
								
								text = trocaValoresXWPF(text, r, "serieCcb", this.objetoCcb.getSerieCcb());
								text = trocaValoresXWPF(text, r, "numeroCCI", this.objetoCcb.getNumeroCcb());
								text = trocaValoresXWPF(text, r, "numeroCCB", this.objetoCcb.getNumeroCcb());
								
								text = trocaValoresXWPF(text, r, "numeroRegistroMatricula", this.objetoCcb.getNumeroRegistroMatricula());
								
								text = trocaValoresXWPF(text, r, "cartorioImovel", this.objetoCcb.getCartorioImovel());
								text = trocaValoresXWPF(text, r, "cidadeImovel", this.objetoCcb.getCidadeImovel());
								text = trocaValoresXWPF(text, r, "ufImovel", this.objetoCcb.getUfImovel());
								text = trocaValoresXWPF(text, r, "numeroImovel", this.objetoCcb.getNumeroImovel());
								text = trocaValoresXWPF(text, r, "inscricaoMunicipal", this.objetoCcb.getInscricaoMunicipal());
								text = trocaValoresXWPFCci(text, r, "vendaLeilao", this.objetoCcb.getVendaLeilao(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "VendaLeilao", this.objetoCcb.getVendaLeilao());	
								
								text = trocaValoresXWPF(text, r, "vencimentoUltimaParcelaPagamento", this.objetoCcb.getVencimentoUltimaParcelaPagamento());
								
								text = trocaValoresXWPF(text, r, "emissaoDia", this.objetoCcb.getDataDeEmissao().getDate());
								text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase());
								text = trocaValoresXWPF(text, r, "emissaoAno", (this.objetoCcb.getDataDeEmissao().getYear() + 1900));
								
								text = trocaValoresXWPF(text, r, "tipoParticipanteEmitente", this.objetoCcb.getTipoParticipanteEmitente());	 		
								text = trocaValoresXWPF(text, r, "nomeEmitente", this.objetoCcb.getNomeEmitente().toUpperCase());	 		
								text = trocaValoresXWPF(text, r, "nomeTestemunha1", this.objetoCcb.getNomeTestemunha1());
								text = trocaValoresXWPF(text, r, "cpfTestemunha1", this.objetoCcb.getCpfTestemunha1());
								text = trocaValoresXWPF(text, r, "rgTestemunha1", this.objetoCcb.getRgTestemunha1());								
								text = trocaValoresXWPF(text, r, "nomeTestemunha2", this.objetoCcb.getNomeTestemunha2());
								text = trocaValoresXWPF(text, r, "cpfTestemunha2", this.objetoCcb.getCpfTestemunha2());
								text = trocaValoresXWPF(text, r, "rgTestemunha2", this.objetoCcb.getRgTestemunha2());
						
								
								if (text != null && text.contains("sistemaAmortizacao")) {
									if(CommonsUtil.mesmoValor(this.objetoCcb.getSistemaAmortizacao(), "Price")) {
										text = trocaValoresXWPF(text, r, "sistemaAmortizacao", "Tabela Price");
									} else if(CommonsUtil.mesmoValor(this.objetoCcb.getSistemaAmortizacao(), "SAC")) {
										text = trocaValoresXWPF(text, r, "sistemaAmortizacao", "SAC - Sistema de Amortização Constante");
									} else {
										text = trocaValoresXWPF(text, r, "sistemaAmortizacao", "");
									}
								}
								
								if (text != null && text.contains("participantesCci")) {
									text = text.replace("participantesCci", "");
									r.setText(text, 0);			
								}
								
								if (text != null && text.contains("ImagemImovel") && filesList.size() > 0) {
									int iImagem = 0;
									int idImage = 50;
									for(iImagem = 0; iImagem < filesList.size(); iImagem++) {
										r.addBreak();
										this.populateFiles(iImagem);
										r.addPicture(this.getBis(), fileTypeInt, fileName.toLowerCase(), Units.toEMU(400), Units.toEMU(300));
										r.addBreak();	
									}
									for (int i = 0; i < r.getCTR().getDrawingList().size(); i++) {
										CTDrawing drawing = r.getCTR().getDrawingList().get(i);
										drawing.getInlineList().get(0).getDocPr().setId(idImage);
										idImage++;
									}
									text = trocaValoresXWPF(text, r, "ImagemImovel", "");						
									adicionarEnter(text, r);
								} else if(text != null && text.contains("ImagemImovel") && filesList.size() == 0) {
									text = trocaValoresXWPF(text, r, "ImagemImovel", "");
								}
							}
						}
					}
				}
			}
		    
		    
		    
		    XWPFTableRow tableRow2 = document.getTableArray(1).getRow(1);

		    paragraph = document.createParagraph();
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			paragraph.setAlignment(ParagraphAlignment.LEFT);
			
			if (this.objetoCcb.getListaParticipantes().size() > 1) {
				tableRow2.getCell(0).setParagraph(paragraph);
				tableRow2.getCell(1).setParagraph(paragraph);
				@SuppressWarnings("unused")
				int qtdePessoasEsquerdo = 0;
				for (int iPartTab = 0; iPartTab < this.objetoCcb.getListaParticipantes().size(); iPartTab++) {

					CcbParticipantes participante = this.objetoCcb.getListaParticipantes().get(iPartTab);
					if (iPartTab != 0) {
						if (iPartTab % 2 != 0) {

							run = tableRow2.getCell(0).getParagraphArray(0).createRun();
							run.addBreak();
							run.setFontSize(12);
							run.setText("____________________________________   ");
							run.setBold(false);
							run.addBreak();

							run2 = tableRow2.getCell(0).getParagraphArray(0).createRun();
							run2.setFontSize(12);
							run2.setText(participante.getPessoa().getNome());
							run2.setBold(true);
							run2.addBreak();

							run3 = tableRow2.getCell(0).getParagraphArray(0).createRun();
							run3.setFontSize(12);
							run3.setText(participante.getTipoParticipante());
							run3.setBold(false);
							run3.addBreak();

							qtdePessoasEsquerdo++;
						} else {
							run = tableRow2.getCell(1).getParagraphArray(0).createRun();
							run.addBreak();
							run.setFontSize(12);
							run.setText("____________________________________   ");
							run.setBold(false);
							run.addBreak();

							run2 = tableRow2.getCell(1).getParagraphArray(0).createRun();
							run2.setFontSize(12);
							run2.setText(participante.getPessoa().getNome());
							run2.setBold(true);
							run2.addBreak();

							run3 = tableRow2.getCell(1).getParagraphArray(0).createRun();
							run3.setFontSize(12);
							run3.setText(participante.getTipoParticipante());
							run3.setBold(false);
							run3.addBreak();
							qtdePessoasEsquerdo--;
						}
					}
				}
			}
			
			int indexParcela = 1;
			

			XWPFParagraph paragraph1 = document.createParagraph();
			paragraph1.setAlignment(ParagraphAlignment.CENTER);
			paragraph1.setSpacingBefore(0);
			paragraph1.setSpacingAfter(0);
			
			XWPFParagraph paragraph2 = document.createParagraph();
			paragraph2.setAlignment(ParagraphAlignment.RIGHT);
			paragraph2.setSpacingBefore(0);
			paragraph2.setSpacingAfter(0);
			
			int fontSize = 7;
			for(SimulacaoDetalheVO p : simulador.getParcelas()) {
				table = document.getTableArray(3);
				table.insertNewTableRow(indexParcela);
				tableRow1 = table.getRow(indexParcela);
				tableRow1.createCell();
				tableRow1.getCell(0).setParagraph(paragraph1);
				tableRow1.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(p.getNumeroParcela().toString());
				tableRow1.createCell();
				tableRow1.getCell(1).setParagraph(paragraph2);
				tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataData(DateUtil.adicionarPeriodo(simulador.getDataSimulacao(), p.getNumeroParcela().intValue(), Calendar.MONTH), "dd/MM/yyyy"));
				tableRow1.createCell();
				tableRow1.getCell(2).setParagraph(paragraph2);
				tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(2).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getSaldoDevedorInicial(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(3).setParagraph(paragraph2);
				tableRow1.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(3).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getAmortizacao(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(4).setParagraph(paragraph2);
				tableRow1.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(4).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getJuros(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(5).setParagraph(paragraph2);
				tableRow1.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(5).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getJuros().add(p.getAmortizacao()), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(6).setParagraph(paragraph2);
				tableRow1.getCell(6).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(6).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getTxAdm(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(7).setParagraph(paragraph2);
				tableRow1.getCell(7).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(7).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCciArredondado(p.getSeguroMIP(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(8).setParagraph(paragraph2);
				tableRow1.getCell(8).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(8).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCciArredondado(p.getSeguroDFI(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(9).setParagraph(paragraph2);
				tableRow1.getCell(9).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(9).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCciArredondado(p.getValorParcela(), "R$ ") + " + IPCA");
				indexParcela++;////////////////////////////////////////////////////////////////////////////////
			}
			
			geraPaginaContratoII(document, "9DC83E", false);

			table = document.getTableArray(2);			
			CabecalhoAnexo1(table, 0, 1, CommonsUtil.formataData(this.objetoCcb.getDataDeEmissao(), "dd/MM/yyyy"));
			CabecalhoAnexo1(table, 1, 1, CommonsUtil.formataData(this.objetoCcb.getVencimentoUltimaParcelaPagamento(), "dd/MM/yyyy"));	
			CabecalhoAnexo1(table, 2, 1, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getValorCredito(), "R$ "));
			CabecalhoAnexo1(table, 2, 4, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getTaxaDeJurosMes(),"") + "%");
			
			CabecalhoAnexo1(table, 3, 1, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getValorIOF(), "R$ "));
			CabecalhoAnexo1(table, 3, 4, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getTaxaDeJurosAno(),"") + "%");
			
			CabecalhoAnexo1(table, 4, 1, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getCustoEmissao(), "R$ "));
			CabecalhoAnexo1(table, 4, 4, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getCetMes(),"") + "%");
			CabecalhoAnexo1(table, 4, 7, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getVlrImovel(), "R$ "));
			
			CabecalhoAnexo1(table, 5, 1, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getValorDespesas(), "R$ "));
			CabecalhoAnexo1(table, 5, 4, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getCetAno(),"") + "%");
			CabecalhoAnexo1(table, 5, 7, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getMontanteMIP(), "R$ "));
			
			CabecalhoAnexo1(table, 6, 1, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getValorLiquidoCredito(), "R$ "));
			CabecalhoAnexo1(table, 6, 4, CommonsUtil.stringValue(
					CommonsUtil.formataValorInteiro(
							DateUtil.getDaysBetweenDates(this.objetoCcb.getDataDeEmissao(), this.objetoCcb.getVencimentoUltimaParcelaPagamento()))));
			CabecalhoAnexo1(table, 6, 7, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getMontanteDFI(), "R$ "));
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
			String nomeSemvirgula = this.objetoCcb.getNomeEmitente();
			if(nomeSemvirgula.contains(",")) {
				nomeSemvirgula = nomeSemvirgula.replace(",", "");
		    }
			gerador.open(String.format(nomeSemvirgula +  " - CCI - " + this.objetoCcb.getNumeroCcb() +"%s.docx", ""));
			gerador.feed(new ByteArrayInputStream(out.toByteArray()));
			gerador.close();
			criarCcbNosistema();	
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	

	private void organizaSegurados(List<CcbParticipantes> segurados) {
		if(segurados.size() <= 0) {
			return;
		}
		BigDecimal porcentagem =  BigDecimal.valueOf(100).divide(BigDecimal.valueOf(segurados.size()), MathContext.DECIMAL128).setScale(2, BigDecimal.ROUND_HALF_UP);
		if(this.objetoCcb.getListSegurados().size() == segurados.size()) {
			return;
		}
		
		SeguradoDAO seguradoDAO = new SeguradoDAO();
		this.objetoCcb.getListSegurados().clear();
		if(objetoCcb.getObjetoContratoCobranca().getListSegurados().size() == segurados.size()) {
			for(Segurado segurado : this.objetoCcb.getObjetoContratoCobranca().getListSegurados()) {
				this.objetoCcb.getListSegurados().add(segurado);
			}
		} else {
			for(Segurado segurado : this.objetoCcb.getObjetoContratoCobranca().getListSegurados()) {
				segurado.setContratoCobranca(null);
				seguradoDAO.delete(segurado);
			}
			this.objetoCcb.getObjetoContratoCobranca().getListSegurados().clear();
		
			for(CcbParticipantes participante : segurados) {
				Segurado segurado = new Segurado();
				if(!CommonsUtil.semValor(this.objetoCcb.getObjetoContratoCobranca())) {
					segurado.setPessoa(participante.getPessoa());
					segurado.setPorcentagemSegurador(porcentagem);
					segurado.setPosicao(this.objetoCcb.getListSegurados().size() + 1);
					if(!this.objetoCcb.getObjetoContratoCobranca().getListSegurados().contains(segurado)) {		
						segurado.setContratoCobranca(this.objetoCcb.getObjetoContratoCobranca());
						this.objetoCcb.getObjetoContratoCobranca().getListSegurados().add(segurado);
					}
					if(!this.objetoCcb.getListSegurados().contains(segurado)) {	
						
						seguradoDAO.create(segurado);
						this.objetoCcb.getListSegurados().add(segurado);
					}
				}
			}
		}
			
		
	}
	

	private void CabecalhoAnexo1(XWPFTable table, int r, int c, String text) {
		XWPFRun run;
		XWPFTableRow tableRow1;
		tableRow1 = table.getRow(r);
		run = tableRow1.getCell(c).getParagraphArray(0).createRun();
		run.setFontSize(8);
		run.setFontFamily("Calibri");
		run.setBold(true);
		run.setText(text);
	}
	
	public StreamedContent geraCciAquisicao() throws IOException{
		try {
			XWPFDocument document;	
			XWPFRun run;
			XWPFRun run2;
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "TERCEIRO GARANTIDOR")) {
					this.objetoCcb.setTerceiroGarantidor(true);
					participante.setTipoParticipante("DEVEDOR FIDUCIANTE");
				}
			}
			
			document = new XWPFDocument(getClass().getResourceAsStream("/resource/AquisicaoCCI_Novo.docx"));
				
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Times New Roman");
			fonts.setAscii("Times New Roman");
			fonts.setEastAsia("Times New Roman");
			fonts.setCs("Times New Roman");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			
			XWPFParagraph paragraph;
			
			XWPFTable table = document.getTables().get(0);
			setTableAlignment(table, STJc.CENTER);
			XWPFTableRow tableRow1 = table.getRow(2);
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.LEFT);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			XWPFTableRow tableRowAux = tableRow1.getCell(0).getTableArray(0).getRow(0);
			int iParticipante = 0;
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {		
				
				if(CommonsUtil.mesmoValor(participante.getTipoOriginal(), "TERCEIRO GARANTIDOR") 
						|| CommonsUtil.mesmoValor(participante.getTipoOriginal(), "Vendedor")) {
					participante.setTipoParticipante("Vendedor");
				
					run = tableRowAux.getCell(0).getParagraphArray(0).createRun();	
					run.setFontSize(12);
					run.setText(participante.getTipoParticipante() + " " + (iParticipante + 1));
					run.setBold(true);
					run2 = tableRowAux.getCell(0).getParagraphArray(0).createRun();
					run2.setText(" " + participante.getPessoa().getNome().toUpperCase() + ", ");
					//run2.setFontFamily("Times New Roman");
					if (!participante.isEmpresa()) {
						geraParagrafoPF(run2, participante);
						run2.addBreak();
					} else {
						run2.setFontSize(12);
						PagadorRecebedor pessoa = participante.getPessoa();
						String socios = "";
						if (participante.getSocios().size() > 1) {
							socios = "pelos seus sócios, ";
						} else if(participante.getSocios().size() > 0){
							if (participante.getSocios().iterator().next().isFeminino()) {
								socios = "pela sua única sócia, ";
							} else {
								socios = "pelo seu único sócio, ";
							}
						} else {
							socios = "";
						}
						run2.setText(participante.getTipoEmpresa() + ", devidamente inscrito no CNPJ sob n° "
								+ pessoa.getCnpj() + ", com sede em " + pessoa.getEndereco() + ", " + "n° "
								+ pessoa.getNumero() + ", Sala " + participante.getSalaEmpresa() + ", " + pessoa.getBairro()
								+ ", " + pessoa.getCidade() + " - " + pessoa.getEstado() + ", CEP " + pessoa.getCep()
								+ "; neste ato representada " + socios);
						for (CcbParticipantes sociosParticipante : participante.getSocios()) {
							XWPFRun runSocios = tableRowAux.getCell(0).getParagraphArray(0).createRun();
							runSocios.setFontSize(12);
							runSocios.setText(" " + sociosParticipante.getPessoa().getNome().toUpperCase() + ", ");
							runSocios.setBold(true);
							XWPFRun runSociosNome = tableRowAux.getCell(0).getParagraphArray(0).createRun();
							geraParagrafoPF(runSociosNome, sociosParticipante);
							runSociosNome.addBreak();
						}
					}									
					iParticipante++;
				}
			}
			
			tableRow1 = table.getRow(4);
			tableRowAux = tableRow1.getCell(0).getTableArray(0).getRow(0);
			iParticipante = 0;
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {		
				
				if (CommonsUtil.mesmoValor(participante.getTipoOriginal(), "EMITENTE")) {
					if(CommonsUtil.semValor(this.objetoCcb.getNomeEmitente())) {
						this.objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
					}
					if(CommonsUtil.semValor(cpfEmitente)) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							this.objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
						} else {
							this.objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
						}
					}
					participante.setTipoParticipante("Devedor");
								
					run = tableRowAux.getCell(0).getParagraphArray(0).createRun();	
					run.setFontSize(12);
					run.setText(participante.getTipoParticipante() + " " + (iParticipante + 1));
					run.setBold(true);
					run2 = tableRowAux.getCell(0).getParagraphArray(0).createRun();
					run2.setText(" " + participante.getPessoa().getNome().toUpperCase() + ", ");
					//run2.setFontFamily("Times New Roman");
					if (!participante.isEmpresa()) {
						geraParagrafoPF(run2, participante);
						run2.addBreak();
					} else {
						run2.setFontSize(12);
						PagadorRecebedor pessoa = participante.getPessoa();
						String socios = "";
						if (participante.getSocios().size() > 1) {
							socios = "pelos seus sócios, ";
						} else if(participante.getSocios().size() > 0){
							if (participante.getSocios().iterator().next().isFeminino()) {
								socios = "pela sua única sócia, ";
							} else {
								socios = "pelo seu único sócio, ";
							}
						} else {
							socios = "";
						}
						run2.setText(participante.getTipoEmpresa() + ", devidamente inscrito no CNPJ sob n° "
								+ pessoa.getCnpj() + ", com sede em " + pessoa.getEndereco() + ", " + "n° "
								+ pessoa.getNumero() + ", Sala " + participante.getSalaEmpresa() + ", " + pessoa.getBairro()
								+ ", " + pessoa.getCidade() + " - " + pessoa.getEstado() + ", CEP " + pessoa.getCep()
								+ "; neste ato representada " + socios);
						for (CcbParticipantes sociosParticipante : participante.getSocios()) {
							XWPFRun runSocios = tableRowAux.getCell(0).getParagraphArray(0).createRun();
							runSocios.setFontSize(12);
							runSocios.setText(" " + sociosParticipante.getPessoa().getNome().toUpperCase() + ", ");
							runSocios.setBold(true);
							XWPFRun runSociosNome = tableRowAux.getCell(0).getParagraphArray(0).createRun();
							geraParagrafoPF(runSociosNome, sociosParticipante);
							runSociosNome.addBreak();
						}
					}									
					iParticipante++;
				}
			}
			
			for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);
			            
			            text = trocaValoresXWPF(text, r, "nomeEmitente", this.objetoCcb.getNomeEmitente().toUpperCase());	 		
			            text = trocaValoresXWPF(text, r, "porcentagemImovel", CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getPorcentagemImovel(), ""));	 		
			            text = trocaValoresTaxaExtensoXWPF(text, r, "PorcentagemImovel", this.objetoCcb.getPorcentagemImovel());
						text = trocaValoresXWPF(text, r, "emissaoDia", this.objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (this.objetoCcb.getDataDeEmissao().getYear() + 1900));		

						text = trocaValoresXWPF(text, r, "nomeTestemunha1", this.objetoCcb.getNomeTestemunha1());
						text = trocaValoresXWPF(text, r, "cpfTestemunha1", this.objetoCcb.getCpfTestemunha1());
						text = trocaValoresXWPF(text, r, "rgTestemunha1", this.objetoCcb.getRgTestemunha1());								
						text = trocaValoresXWPF(text, r, "nomeTestemunha2", this.objetoCcb.getNomeTestemunha2());
						text = trocaValoresXWPF(text, r, "cpfTestemunha2", this.objetoCcb.getCpfTestemunha2());
						text = trocaValoresXWPF(text, r, "rgTestemunha2", this.objetoCcb.getRgTestemunha2());
						
						if(CommonsUtil.mesmoValor(text, "aaaaaaaaaaa")){
							text = trocaValoresXWPF(text, r, "aaaaaaaaaaa", "");	 	
							
							for(CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {
							
								r.setText("____________________________________________________________________");
								r.setBold(true);
								r.setFontSize(12);
								r.setFontFamily("Times New Roman");
								r.addCarriageReturn();
								r.setText(participante.getTipoParticipante().toUpperCase() + ": " + participante.getPessoa().getNome());
								r.addCarriageReturn();
								r.addCarriageReturn();
								r.addCarriageReturn();
							}
						}
			        }
			    }
			}	
		
			int indexSegurados = 47;
			
			for(Segurado segurado : objetoCcb.getListSegurados()) {
				table = document.getTables().get(0);
				table.insertNewTableRow(indexSegurados);
				tableRow1 = table.getRow(indexSegurados);
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.LEFT);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				tableRow1.createCell();
				tableRow1.getCell(0).setParagraph(paragraph);
				tableRow1.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				CTTcBorders border = tableRow1.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();	
				border.addNewRight().setVal(STBorder.SINGLE);
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);
				border.addNewLeft().setVal(STBorder.TRIPLE);	
				border.getRight().setColor("808080");
				border.getBottom().setColor("808080");
				border.getTop().setColor("808080");
				border.getLeft().setColor("808080");
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setFontFamily("Times New Roman");
				run.setText("Nome: " + segurado.getPessoa().getNome());
				tableRow1.createCell();////////////////////////////////////////////////////////////////////////
				tableRow1.getCell(1).setParagraph(paragraph);
				tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);			
				run = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setFontFamily("Times New Roman");
				run.setText("Percentual: ");
				run2 = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run2.setFontSize(12);
				run2.setFontFamily("Times New Roman");
				run2.setBold(true);
				run2.setText(CommonsUtil.formataValorMonetario(segurado.getPorcentagemSegurador()) + "%");
				tableRow1.createCell();
				tableRow1.getCell(2).getCTTc().addNewTcPr();
				CTHMerge hMerge = CTHMerge.Factory.newInstance();
				table = document.getTables().get(0);
				hMerge.setVal(STMerge.RESTART);
				table.getRow(indexSegurados).getCell(1).getCTTc().getTcPr().setHMerge(hMerge);
				CTHMerge hMerge1 = CTHMerge.Factory.newInstance();
				hMerge.setVal(STMerge.CONTINUE);
				table.getRow(indexSegurados).getCell(2).getCTTc().getTcPr().setHMerge(hMerge1);
				border = tableRow1.getCell(2).getCTTc().addNewTcPr().addNewTcBorders();
				border.addNewRight().setVal(STBorder.TRIPLE);
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);
				border.addNewLeft().setVal(STBorder.SINGLE);
				border.getRight().setColor("808080");
				border.getBottom().setColor("808080");
				border.getTop().setColor("808080");
				border.getLeft().setColor("808080");
				indexSegurados++;
			}
			// First Row
						
			BigDecimal taxaAdm = SiscoatConstants.TAXA_ADM;
			BigDecimal totalPrimeiraParcela = BigDecimal.ZERO;
			if (!CommonsUtil.semValor(this.objetoCcb.getValorMipParcela()))
				totalPrimeiraParcela = this.objetoCcb.getValorMipParcela();
			if (!CommonsUtil.semValor(this.objetoCcb.getValorDfiParcela()))
			totalPrimeiraParcela = totalPrimeiraParcela.add(this.objetoCcb.getValorDfiParcela());
			if (!CommonsUtil.semValor(this.objetoCcb.getValorParcela()))
			totalPrimeiraParcela = totalPrimeiraParcela.add(this.objetoCcb.getValorParcela());
			if (!CommonsUtil.semValor(taxaAdm))
			totalPrimeiraParcela = totalPrimeiraParcela.add(taxaAdm);
			
			BigDecimal despesas = this.objetoCcb.getValorDespesas();
			BigDecimal custasCartorarias = BigDecimal.ZERO;
			BigDecimal itbi =  BigDecimal.ZERO;
			
			if(!this.objetoCcb.getDespesasAnexo2().isEmpty()) {
				for(ContasPagar cartorioItbi : this.objetoCcb.getDespesasAnexo2()) {
					if(!CommonsUtil.mesmoValor(cartorioItbi.getDescricao(), "Cartório")
							&& !CommonsUtil.mesmoValor(cartorioItbi.getDescricao(), "ITBI")) {
						continue;
					}
					
					if(CommonsUtil.semValor(cartorioItbi.getValor())) {
						continue;
					}
					
					if(CommonsUtil.mesmoValor(cartorioItbi.getDescricao(), "Cartório")) {
						custasCartorarias = custasCartorarias.add(cartorioItbi.getValor());
					}
					if(CommonsUtil.mesmoValor(cartorioItbi.getDescricao(), "ITBI")) {
						itbi = itbi.add(cartorioItbi.getValor());
					}
					despesas = despesas.subtract(cartorioItbi.getValor());
				}
			}
			
			this.objetoCcb.setCustasCartorariasValor(custasCartorarias);
			this.objetoCcb.setItbiValor(itbi);
			
			
		    for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
								String text = r.getText(0);		 
								
								text = trocaValoresXWPFCci(text, r, "precoVendaCompra", this.objetoCcb.getPrecoVendaCompra(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "PrecoVendaCompra", this.objetoCcb.getPrecoVendaCompra());	
								
								text = trocaValoresXWPFCci(text, r, "valorCredito", this.objetoCcb.getValorCredito(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorCredito", this.objetoCcb.getValorCredito());									
								text = trocaValoresXWPFCci(text, r, "valorLiquidoCredito", this.objetoCcb.getValorLiquidoCredito(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorLiquidoCredito", this.objetoCcb.getValorLiquidoCredito());								
								text = trocaValoresXWPFCci(text, r, "custoEmissao", this.objetoCcb.getCustoEmissao(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "CustoEmissao", this.objetoCcb.getCustoEmissao());	
								text = trocaValoresXWPFCci(text, r, "valorIOF", this.objetoCcb.getValorIOF(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorIOF", this.objetoCcb.getValorIOF());	
								text = trocaValoresXWPFCci(text, r, "valorDespesas", despesas, "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorDespesas", despesas);	
								
								text = trocaValoresXWPFCci(text, r, "custasCartorariasValor", this.objetoCcb.getCustasCartorariasValor(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "CustasCartorariasValor", this.objetoCcb.getCustasCartorariasValor());
								
								text = trocaValoresXWPFCci(text, r, "itbiValor", this.objetoCcb.getItbiValor(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ItbiValor", this.objetoCcb.getItbiValor());
								
								text = trocaValoresXWPF(text, r, "titularConta", this.objetoCcb.getTitularConta());
								text = trocaValoresXWPF(text, r, "agencia", this.objetoCcb.getAgencia());
								text = trocaValoresXWPF(text, r, "contaCorrente", this.objetoCcb.getContaCorrente());					
								text = trocaValoresXWPF(text, r, "nomeBanco", this.objetoCcb.getNomeBanco());		
				
								text = trocaValoresXWPF(text, r, "prazoContrato", this.objetoCcb.getPrazo());
								text = trocaValoresXWPF(text, r, "numeroParcelasPagamento", this.objetoCcb.getNumeroParcelasPagamento());
								text = trocaValoresXWPF(text, r, "vencimentoPrimeiraParcelaPagamento", this.objetoCcb.getVencimentoPrimeiraParcelaPagamento());
								text = trocaValoresXWPF(text, r, "taxaDeJurosMes", this.objetoCcb.getTaxaDeJurosMes());
								text = trocaValoresXWPF(text, r, "taxaDeJurosAno", this.objetoCcb.getTaxaDeJurosAno());
								text = trocaValoresXWPF(text, r, "cetMes", this.objetoCcb.getCetMes());
								text = trocaValoresXWPF(text, r, "cetAno", this.objetoCcb.getCetAno());
								
								text = trocaValoresXWPFCci(text, r, "totalPrimeiraParcela", totalPrimeiraParcela, "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "TotalPrimeiraParcela", totalPrimeiraParcela);	
								text = trocaValoresXWPFCci(text, r, "valorMipParcela", this.objetoCcb.getValorMipParcela(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorMipParcela", this.objetoCcb.getValorMipParcela());			
								text = trocaValoresXWPFCci(text, r, "valorDfiParcela", this.objetoCcb.getValorDfiParcela(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorDfiParcela", this.objetoCcb.getValorDfiParcela());
								text = trocaValoresXWPFCci(text, r, "valorParcela", this.objetoCcb.getValorParcela(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorParcela", this.objetoCcb.getValorParcela());		
								
								text = trocaValoresXWPF(text, r, "numeroCCB", this.objetoCcb.getNumeroCcb());
								text = trocaValoresXWPF(text, r, "serieCcb", this.objetoCcb.getSerieCcb());
								
								text = trocaValoresXWPF(text, r, "numeroRegistroMatricula", this.objetoCcb.getNumeroRegistroMatricula());
								
								text = trocaValoresXWPF(text, r, "cartorioImovel", this.objetoCcb.getCartorioImovel());
								text = trocaValoresXWPF(text, r, "cidadeImovel", this.objetoCcb.getCidadeImovel());
								text = trocaValoresXWPF(text, r, "ufImovel", this.objetoCcb.getUfImovel());
								text = trocaValoresXWPF(text, r, "numeroImovel", this.objetoCcb.getNumeroImovel());
								text = trocaValoresXWPF(text, r, "inscricaoMunicipal", this.objetoCcb.getInscricaoMunicipal());
								text = trocaValoresXWPFCci(text, r, "vendaLeilao", this.objetoCcb.getVendaLeilao(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "VendaLeilao", this.objetoCcb.getVendaLeilao());	
								
								text = trocaValoresXWPF(text, r, "vencimentoUltimaParcelaPagamento", this.objetoCcb.getVencimentoUltimaParcelaPagamento());
								
								text = trocaValoresXWPF(text, r, "emissaoDia", this.objetoCcb.getDataDeEmissao().getDate());
								text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase());
								text = trocaValoresXWPF(text, r, "emissaoAno", (this.objetoCcb.getDataDeEmissao().getYear() + 1900));
								
								text = trocaValoresXWPF(text, r, "nomeEmitente", this.objetoCcb.getNomeEmitente().toUpperCase());	 		
								text = trocaValoresXWPF(text, r, "nomeTestemunha1", this.objetoCcb.getNomeTestemunha1());
								text = trocaValoresXWPF(text, r, "cpfTestemunha1", this.objetoCcb.getCpfTestemunha1());
								text = trocaValoresXWPF(text, r, "rgTestemunha1", this.objetoCcb.getRgTestemunha1());								
								text = trocaValoresXWPF(text, r, "nomeTestemunha2", this.objetoCcb.getNomeTestemunha2());
								text = trocaValoresXWPF(text, r, "cpfTestemunha2", this.objetoCcb.getCpfTestemunha2());
								text = trocaValoresXWPF(text, r, "rgTestemunha2", this.objetoCcb.getRgTestemunha2());
								

								text = trocaValoresXWPF(text, r, "elaboradorNome", this.objetoCcb.getElaboradorNome());								
								text = trocaValoresXWPF(text, r, "elaboradorCrea", this.objetoCcb.getElaboradorCrea());
								text = trocaValoresXWPF(text, r, "responsavelNome", this.objetoCcb.getResponsavelNome());
								text = trocaValoresXWPF(text, r, "responsavelCrea", this.objetoCcb.getResponsavelCrea());
								
								
								
								
								if (text != null && text.contains("sistemaAmortizacao")) {
									if(CommonsUtil.mesmoValor(this.objetoCcb.getSistemaAmortizacao(), "Price")) {
										text = trocaValoresXWPF(text, r, "sistemaAmortizacao", "Tabela Price");
									} else if(CommonsUtil.mesmoValor(this.objetoCcb.getSistemaAmortizacao(), "SAC")) {
										text = trocaValoresXWPF(text, r, "sistemaAmortizacao", "SAC - Sistema de Amortização Constante");
									} else {
										text = trocaValoresXWPF(text, r, "sistemaAmortizacao", "");
									}
								}
								
								if (text != null && text.contains("participantesCci")) {
									text = text.replace("participantesCci", "");
									r.setText(text, 0);			
								}
								
								if (text != null && text.contains("ImagemImovel") && filesList.size() > 0) {
									int iImagem = 0;
									for(UploadedFile imagem :  filesList) {
										r.addBreak();
										this.populateFiles(iImagem);
										r.addPicture(this.getBis(), fileTypeInt, fileName.toLowerCase(), Units.toEMU(400), Units.toEMU(300));
										r.addBreak();	
										iImagem++;
									}
									text = trocaValoresXWPF(text, r, "ImagemImovel", "");						
									adicionarEnter(text, r);
								} else if(text != null && text.contains("ImagemImovel") && filesList.size() == 0) {
									text = trocaValoresXWPF(text, r, "ImagemImovel", "");
								}
							}
						}
						
						for (XWPFTable t : cell.getTables()) {
							for (XWPFTableRow row2 : t.getRows()) {
								for (XWPFTableCell cell2 : row2.getTableCells()) {
									for (XWPFParagraph p2 : cell2.getParagraphs()) {
										for (XWPFRun r2 : p2.getRuns()) {
											String text = r2.getText(0);
											text = trocaValoresXWPF(text, r2, "cartorioImovel", this.objetoCcb.getCartorioImovel());
											text = trocaValoresXWPF(text, r2, "cidadeImovel", this.objetoCcb.getCidadeImovel());
											text = trocaValoresXWPF(text, r2, "ufImovel", this.objetoCcb.getUfImovel());		
											text = trocaValoresXWPF(text, r2, "numeroImovel", this.objetoCcb.getNumeroImovel());
											text = trocaValoresXWPF(text, r2, "inscricaoMunicipal", this.objetoCcb.getInscricaoMunicipal());
											text = trocaValoresXWPFCci(text, r2, "vendaLeilao", this.objetoCcb.getVendaLeilao(), "R$ ");
											text = trocaValoresDinheiroExtensoXWPF(text, r2, "VendaLeilao", this.objetoCcb.getVendaLeilao());	
											
										}
									}
								}
							}
						}
					}
				}
			}
		    
		    int indexParcela = 1;
			
			//calcularSimulador();

			XWPFParagraph paragraph1 = document.createParagraph();
			paragraph1.setAlignment(ParagraphAlignment.CENTER);
			paragraph1.setSpacingBefore(0);
			paragraph1.setSpacingAfter(0);
			
			XWPFParagraph paragraph2 = document.createParagraph();
			paragraph2.setAlignment(ParagraphAlignment.RIGHT);
			paragraph2.setSpacingBefore(0);
			paragraph2.setSpacingAfter(0);
			
			int fontSize = 7;
			for(SimulacaoDetalheVO p : simulador.getParcelas()) {
				table = document.getTableArray(2);
				table.insertNewTableRow(indexParcela);
				tableRow1 = table.getRow(indexParcela);
				tableRow1.createCell();
				tableRow1.getCell(0).setParagraph(paragraph1);
				tableRow1.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(p.getNumeroParcela().toString());
				tableRow1.createCell();
				tableRow1.getCell(1).setParagraph(paragraph2);
				tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataData(DateUtil.adicionarPeriodo(simulador.getDataSimulacao(), p.getNumeroParcela().intValue(), Calendar.MONTH), "dd/MM/yyyy"));
				tableRow1.createCell();
				tableRow1.getCell(2).setParagraph(paragraph2);
				tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(2).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getSaldoDevedorInicial(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(3).setParagraph(paragraph2);
				tableRow1.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(3).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getAmortizacao(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(4).setParagraph(paragraph2);
				tableRow1.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(4).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getJuros(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(5).setParagraph(paragraph2);
				tableRow1.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(5).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getJuros().add(p.getAmortizacao()), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(6).setParagraph(paragraph2);
				tableRow1.getCell(6).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(6).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getTxAdm(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(7).setParagraph(paragraph2);
				tableRow1.getCell(7).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(7).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getSeguroMIP(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(8).setParagraph(paragraph2);
				tableRow1.getCell(8).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(8).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getSeguroDFI(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(9).setParagraph(paragraph2);
				tableRow1.getCell(9).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(9).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getValorParcela(), "R$ ") + " + IPCA");
				indexParcela++;////////////////////////////////////////////////////////////////////////////////
			}
			
			table = document.getTableArray(1);			
			CabecalhoAnexo1(table, 0, 1, CommonsUtil.formataData(this.objetoCcb.getDataDeEmissao(), "dd/MM/yyyy"));
			CabecalhoAnexo1(table, 1, 1, CommonsUtil.formataData(this.objetoCcb.getVencimentoUltimaParcelaPagamento(), "dd/MM/yyyy"));	
			CabecalhoAnexo1(table, 2, 1, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getValorCredito(), "R$ "));
			CabecalhoAnexo1(table, 2, 4, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getTaxaDeJurosMes(),"") + "%");
			
			CabecalhoAnexo1(table, 3, 1, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getValorIOF(), "R$ "));
			CabecalhoAnexo1(table, 3, 4, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getTaxaDeJurosAno(),"") + "%");
			
			CabecalhoAnexo1(table, 4, 1, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getCustoEmissao(), "R$ "));
			CabecalhoAnexo1(table, 4, 4, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getCetMes(),"") + "%");
			CabecalhoAnexo1(table, 4, 7, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getVlrImovel(), "R$ "));
			
			CabecalhoAnexo1(table, 5, 1, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getValorDespesas(), "R$ "));
			CabecalhoAnexo1(table, 5, 4, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getCetAno(),"") + "%");
			CabecalhoAnexo1(table, 5, 7, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getMontanteMIP(), "R$ "));
			
			CabecalhoAnexo1(table, 6, 1, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getValorLiquidoCredito(), "R$ "));
			CabecalhoAnexo1(table, 6, 4, CommonsUtil.stringValue(
					CommonsUtil.formataValorInteiro(
							DateUtil.getDaysBetweenDates(this.objetoCcb.getDataDeEmissao(), this.objetoCcb.getVencimentoUltimaParcelaPagamento()))));
			CabecalhoAnexo1(table, 6, 7, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getMontanteDFI(), "R$ "));
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
			String nomeSemvirgula = this.objetoCcb.getNomeEmitente();
			if(nomeSemvirgula.contains(",")) {
				nomeSemvirgula = nomeSemvirgula.replace(",", "");
		    }
			gerador.open(String.format("Galleria Bank - CCI %s.docx", ""));
			gerador.feed(new ByteArrayInputStream(out.toByteArray()));
			gerador.close();
			criarCcbNosistema();	
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public StreamedContent geraCciFinanciamento() throws IOException{
		try {
			XWPFDocument document;	
			XWPFRun run;
			XWPFRun run2;
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "TERCEIRO GARANTIDOR")) {
					this.objetoCcb.setTerceiroGarantidor(true);
					participante.setTipoParticipante("DEVEDOR FIDUCIANTE");
				}
			}
			
			document = new XWPFDocument(getClass().getResourceAsStream("/resource/CCI - Financiamento (final).docx"));
				
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Times New Roman");
			fonts.setAscii("Times New Roman");
			fonts.setEastAsia("Times New Roman");
			fonts.setCs("Times New Roman");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
					
			XWPFParagraph paragraph;
			
			XWPFTable table = document.getTables().get(0);
			setTableAlignment(table, STJc.CENTER);
			XWPFTableRow tableRow1 = table.getRow(2);
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.LEFT);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			XWPFTableRow tableRowAux = tableRow1.getCell(0).getTableArray(0).getRow(0);
			int iParticipante = 0;
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {		
				
				if(CommonsUtil.mesmoValor(participante.getTipoOriginal(), "TERCEIRO GARANTIDOR") 
						|| CommonsUtil.mesmoValor(participante.getTipoOriginal(), "Vendedor")) {
					participante.setTipoParticipante("Vendedor");
				
					run = tableRowAux.getCell(0).getParagraphArray(0).createRun();	
					run.setFontSize(12);
					run.setText(participante.getTipoParticipante() + " - " + (iParticipante + 1));
					run.setBold(true);
					run2 = tableRowAux.getCell(0).getParagraphArray(0).createRun();
					run2.setText(" " + participante.getPessoa().getNome().toUpperCase() + ", ");
					//run2.setFontFamily("Times New Roman");
					if (!participante.isEmpresa()) {
						geraParagrafoPF(run2, participante);
						run2.addBreak();
					} else {
						run2.setFontSize(12);
						PagadorRecebedor pessoa = participante.getPessoa();
						String socios = "";
						if (participante.getSocios().size() > 1) {
							socios = "pelos seus sócios, ";
						} else if(participante.getSocios().size() > 0){
							if (participante.getSocios().iterator().next().isFeminino()) {
								socios = "pela sua única sócia, ";
							} else {
								socios = "pelo seu único sócio, ";
							}
						} else {
							socios = "";
						}
						run2.setText(participante.getTipoEmpresa() + ", devidamente inscrito no CNPJ sob n° "
								+ pessoa.getCnpj() + ", com sede em " + pessoa.getEndereco() + ", " + "n° "
								+ pessoa.getNumero() + ", Sala " + participante.getSalaEmpresa() + ", " + pessoa.getBairro()
								+ ", " + pessoa.getCidade() + " - " + pessoa.getEstado() + ", CEP " + pessoa.getCep()
								+ "; neste ato representada " + socios);
						for (CcbParticipantes sociosParticipante : participante.getSocios()) {
							XWPFRun runSocios = tableRowAux.getCell(0).getParagraphArray(0).createRun();
							runSocios.setFontSize(12);
							runSocios.setText(" " + sociosParticipante.getPessoa().getNome().toUpperCase() + ", ");
							runSocios.setBold(true);
							XWPFRun runSociosNome = tableRowAux.getCell(0).getParagraphArray(0).createRun();
							geraParagrafoPF(runSociosNome, sociosParticipante);
							runSociosNome.addBreak();
						}
					}									
					iParticipante++;
				}
			}
			
			tableRow1 = table.getRow(4);
			tableRowAux = tableRow1.getCell(0).getTableArray(0).getRow(0);
			iParticipante = 0;
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {		
				
				if (CommonsUtil.mesmoValor(participante.getTipoOriginal(), "EMITENTE")) {
					if(CommonsUtil.semValor(this.objetoCcb.getNomeEmitente())) {
						this.objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
					}
					if(CommonsUtil.semValor(cpfEmitente)) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							this.objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
						} else {
							this.objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
						}
					}
					participante.setTipoParticipante("Devedor");
								
					run = tableRowAux.getCell(0).getParagraphArray(0).createRun();	
					run.setFontSize(12);
					run.setText(participante.getTipoParticipante() + " " + (iParticipante + 1));
					run.setBold(true);
					run2 = tableRowAux.getCell(0).getParagraphArray(0).createRun();
					run2.setText(" " + participante.getPessoa().getNome().toUpperCase() + ", ");
					//run2.setFontFamily("Times New Roman");
					if (!participante.isEmpresa()) {
						geraParagrafoPF(run2, participante);
						run2.addBreak();
					} else {
						run2.setFontSize(12);
						PagadorRecebedor pessoa = participante.getPessoa();
						String socios = "";
						if (participante.getSocios().size() > 1) {
							socios = "pelos seus sócios, ";
						} else if(participante.getSocios().size() > 0){
							if (participante.getSocios().iterator().next().isFeminino()) {
								socios = "pela sua única sócia, ";
							} else {
								socios = "pelo seu único sócio, ";
							}
						} else {
							socios = "";
						}
						run2.setText(participante.getTipoEmpresa() + ", devidamente inscrito no CNPJ sob n° "
								+ pessoa.getCnpj() + ", com sede em " + pessoa.getEndereco() + ", " + "n° "
								+ pessoa.getNumero() + ", Sala " + participante.getSalaEmpresa() + ", " + pessoa.getBairro()
								+ ", " + pessoa.getCidade() + " - " + pessoa.getEstado() + ", CEP " + pessoa.getCep()
								+ "; neste ato representada " + socios);
						for (CcbParticipantes sociosParticipante : participante.getSocios()) {
							XWPFRun runSocios = tableRowAux.getCell(0).getParagraphArray(0).createRun();
							runSocios.setFontSize(12);
							runSocios.setText(" " + sociosParticipante.getPessoa().getNome().toUpperCase() + ", ");
							runSocios.setBold(true);
							XWPFRun runSociosNome = tableRowAux.getCell(0).getParagraphArray(0).createRun();
							geraParagrafoPF(runSociosNome, sociosParticipante);
							runSociosNome.addBreak();
						}
					}									
					iParticipante++;
				}
			}
			
			for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);
			            
			            text = trocaValoresXWPF(text, r, "nomeEmitente", this.objetoCcb.getNomeEmitente().toUpperCase());	 		
			            text = trocaValoresXWPF(text, r, "porcentagemImovel", CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getPorcentagemImovel(), ""));	 		
			            text = trocaValoresTaxaExtensoXWPF(text, r, "PorcentagemImovel", this.objetoCcb.getPorcentagemImovel());
						text = trocaValoresXWPF(text, r, "emissaoDia", this.objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (this.objetoCcb.getDataDeEmissao().getYear() + 1900));		

						text = trocaValoresXWPF(text, r, "nomeTestemunha1", this.objetoCcb.getNomeTestemunha1());
						text = trocaValoresXWPF(text, r, "cpfTestemunha1", this.objetoCcb.getCpfTestemunha1());
						text = trocaValoresXWPF(text, r, "rgTestemunha1", this.objetoCcb.getRgTestemunha1());								
						text = trocaValoresXWPF(text, r, "nomeTestemunha2", this.objetoCcb.getNomeTestemunha2());
						text = trocaValoresXWPF(text, r, "cpfTestemunha2", this.objetoCcb.getCpfTestemunha2());
						text = trocaValoresXWPF(text, r, "rgTestemunha2", this.objetoCcb.getRgTestemunha2());
						
						if(CommonsUtil.mesmoValor(text, "aaaaaaaaaaa")){
							text = trocaValoresXWPF(text, r, "aaaaaaaaaaa", "");	 	
							
							for(CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {
							
								r.setText("____________________________________________________________________");
								r.setBold(true);
								r.setFontSize(12);
								r.setFontFamily("Times New Roman");
								r.addCarriageReturn();
								r.setText(participante.getTipoParticipante().toUpperCase() + ": " + participante.getPessoa().getNome());
								r.addCarriageReturn();
								r.addCarriageReturn();
								r.addCarriageReturn();
							}
						}
			        }
			    }
			}	
		
			int indexSegurados = 62;
			
			for(Segurado segurado : objetoCcb.getListSegurados()) {
				table = document.getTables().get(0);
				table.insertNewTableRow(indexSegurados);
				tableRow1 = table.getRow(indexSegurados);
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.LEFT);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				tableRow1.createCell();
				tableRow1.getCell(0).setParagraph(paragraph);		
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setText("");
				tableRow1.getCell(0).getCTTc().addNewTcPr();				
				tableRow1.createCell();
				tableRow1.getCell(1).getCTTc().addNewTcPr();				
				tableRow1.createCell();
				tableRow1.getCell(2).getCTTc().addNewTcPr();				
				tableRow1.createCell();
				tableRow1.getCell(3).getCTTc().addNewTcPr();
				tableRow1.createCell();
				tableRow1.getCell(4).getCTTc().addNewTcPr();			
				tableRow1.createCell();
				tableRow1.getCell(5).setParagraph(paragraph);		
				run = tableRow1.getCell(5).getParagraphArray(0).createRun();
				run.setText("");
				tableRow1.getCell(5).getCTTc().addNewTcPr();				
				tableRow1.createCell();
				tableRow1.getCell(6).getCTTc().addNewTcPr();				
				//tableRow1.createCell();
				//tableRow1.getCell(7).getCTTc().addNewTcPr();				
				CTHMerge hMerge = CTHMerge.Factory.newInstance();
				table = document.getTables().get(0);
				hMerge.setVal(STMerge.RESTART);
				table.getRow(indexSegurados).getCell(0).getCTTc().getTcPr().setHMerge(hMerge);				
				CTHMerge hMerge1 = CTHMerge.Factory.newInstance();
				hMerge1.setVal(STMerge.CONTINUE);
				table.getRow(indexSegurados).getCell(1).getCTTc().getTcPr().setHMerge(hMerge1);
				table.getRow(indexSegurados).getCell(2).getCTTc().getTcPr().setHMerge(hMerge1);				
				table.getRow(indexSegurados).getCell(3).getCTTc().getTcPr().setHMerge(hMerge1);
				table.getRow(indexSegurados).getCell(4).getCTTc().getTcPr().setHMerge(hMerge1);				
				CTHMerge hMerge2 = CTHMerge.Factory.newInstance();
				table = document.getTables().get(0);
				hMerge2.setVal(STMerge.RESTART);
				table.getRow(indexSegurados).getCell(5).getCTTc().getTcPr().setHMerge(hMerge2);
				table.getRow(indexSegurados).getCell(6).getCTTc().getTcPr().setHMerge(hMerge1);
				//table.getRow(indexSegurados).getCell(7).getCTTc().getTcPr().setHMerge(hMerge1);
				
				tableRow1.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				CTTcBorders border = tableRow1.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();	
				border.addNewRight().setVal(STBorder.SINGLE);
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);
				border.addNewLeft().setVal(STBorder.TRIPLE);	
				border.getRight().setColor("808080");
				border.getBottom().setColor("808080");
				border.getTop().setColor("808080");
				border.getLeft().setColor("808080");
				run = tableRow1.getCell(0).getParagraphArray(0).getRuns().get(0);
				run.setFontSize(12);
				run.setFontFamily("Times New Roman");
				run.setText("Nome: " + segurado.getPessoa().getNome());
				
				tableRow1.getCell(5).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow1.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);	
				border = tableRow1.getCell(5).getCTTc().addNewTcPr().addNewTcBorders();
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);
				border.addNewLeft().setVal(STBorder.SINGLE);	
				border.getBottom().setColor("808080");
				border.getTop().setColor("808080");
				border.getLeft().setColor("808080");
				border = tableRow1.getCell(6).getCTTc().addNewTcPr().addNewTcBorders();
				border.addNewRight().setVal(STBorder.TRIPLE);
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);
				border.addNewLeft().setVal(STBorder.SINGLE);	
				border.getBottom().setColor("808080");
				border.getTop().setColor("808080");
				border.getRight().setColor("808080");
				border.getLeft().setColor("808080");
				/*border = tableRow1.getCell(7).getCTTc().addNewTcPr().addNewTcBorders();	
				border.addNewRight().setVal(STBorder.TRIPLE);
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);
				border.addNewLeft().setVal(STBorder.SINGLE);	
				border.getRight().setColor("808080");
				border.getBottom().setColor("808080");
				border.getTop().setColor("808080");
				border.getLeft().setColor("808080");*/
				run = tableRow1.getCell(5).getParagraphArray(0).getRuns().get(0);
				run.setFontSize(12);
				run.setFontFamily("Times New Roman");
				run.setText("Percentual: ");
				run2 = tableRow1.getCell(5).getParagraphArray(0).createRun();
				run2.setFontSize(12);
				run2.setFontFamily("Times New Roman");
				run2.setBold(true);
				run2.setText(CommonsUtil.formataValorMonetario(segurado.getPorcentagemSegurador()) + "%");
				indexSegurados++;
			}
			// First Row
						
			BigDecimal taxaAdm = SiscoatConstants.TAXA_ADM;
			BigDecimal totalPrimeiraParcela = BigDecimal.ZERO;
			totalPrimeiraParcela = this.objetoCcb.getValorMipParcela();
			totalPrimeiraParcela = totalPrimeiraParcela.add(this.objetoCcb.getValorDfiParcela());
			totalPrimeiraParcela = totalPrimeiraParcela.add(this.objetoCcb.getValorParcela());
			totalPrimeiraParcela = totalPrimeiraParcela.add(taxaAdm);
			
			BigDecimal despesas = this.objetoCcb.getValorDespesas();
			BigDecimal custasCartorarias = BigDecimal.ZERO;
			BigDecimal itbi =  BigDecimal.ZERO;
			
			if(!this.objetoCcb.getDespesasAnexo2().isEmpty()) {
				for(ContasPagar cartorioItbi : this.objetoCcb.getDespesasAnexo2()) {
					if(!CommonsUtil.mesmoValor(cartorioItbi.getDescricao(), "Cartório")
							&& !CommonsUtil.mesmoValor(cartorioItbi.getDescricao(), "ITBI")) {
						continue;
					}
					
					if(CommonsUtil.semValor(cartorioItbi.getValor())) {
						continue;
					}
					
					if(CommonsUtil.mesmoValor(cartorioItbi.getDescricao(), "Cartório")) {
						custasCartorarias = custasCartorarias.add(cartorioItbi.getValor());
					}
					if(CommonsUtil.mesmoValor(cartorioItbi.getDescricao(), "ITBI")) {
						itbi = itbi.add(cartorioItbi.getValor());
					}
					despesas = despesas.subtract(cartorioItbi.getValor());
				}
			}
			
			this.objetoCcb.setCustasCartorariasValor(custasCartorarias);
			this.objetoCcb.setItbiValor(itbi);
			
		    for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
								String text = r.getText(0);		 
								
								text = trocaValoresXWPFCci(text, r, "precoVendaCompra", this.objetoCcb.getPrecoVendaCompra(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "PrecoVendaCompra", this.objetoCcb.getPrecoVendaCompra());	
								
								text = trocaValoresXWPFCci(text, r, "valorCredito", this.objetoCcb.getValorCredito(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorCredito", this.objetoCcb.getValorCredito());									
								text = trocaValoresXWPFCci(text, r, "valorLiquidoCredito", this.objetoCcb.getValorLiquidoCredito(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorLiquidoCredito", this.objetoCcb.getValorLiquidoCredito());								
								text = trocaValoresXWPFCci(text, r, "custoEmissao", this.objetoCcb.getCustoEmissao(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "CustoEmissao", this.objetoCcb.getCustoEmissao());	
								text = trocaValoresXWPFCci(text, r, "valorIOF", this.objetoCcb.getValorIOF(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorIOF", this.objetoCcb.getValorIOF());	
								text = trocaValoresXWPFCci(text, r, "valorDespesas", despesas, "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorDespesas", despesas);	
								
								text = trocaValoresXWPFCci(text, r, "custasCartorariasValor", this.objetoCcb.getCustasCartorariasValor(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "CustasCartorariasValor", this.objetoCcb.getCustasCartorariasValor());
								
								text = trocaValoresXWPFCci(text, r, "itbiValor", this.objetoCcb.getItbiValor(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ItbiValor", this.objetoCcb.getItbiValor());
								
								text = trocaValoresXWPFCci(text, r, "recursosProprios", this.objetoCcb.getRecursosProprios(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "RecursosProprios", this.objetoCcb.getRecursosProprios());							
								text = trocaValoresXWPFCci(text, r, "recursosFinanciamento", this.objetoCcb.getRecursosFinanciamento(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "RecursosFinanciamento", this.objetoCcb.getRecursosFinanciamento());
								
								text = trocaValoresXWPF(text, r, "titularContaVendedor", this.objetoCcb.getTitularContaVendedor());
								text = trocaValoresXWPF(text, r, "agenciaVendedor", this.objetoCcb.getAgenciaVendedor());
								text = trocaValoresXWPF(text, r, "contaCorrenteVendedor", this.objetoCcb.getContaCorrenteVendedor());					
								text = trocaValoresXWPF(text, r, "nomeBancoVendedor", this.objetoCcb.getNomeBancoVendedor() + " - " + this.objetoCcb.getNumeroBancoVendedor() );		
								text = trocaValoresXWPF(text, r, "digitoBancoVendedor", this.objetoCcb.getDigitoBancoVendedor());	
								text = trocaValoresXWPF(text, r, "tipoContaBancoVendedor", this.objetoCcb.getTipoContaBancoVendedor());	
								
								text = trocaValoresXWPF(text, r, "titularConta", this.objetoCcb.getTitularConta());
								text = trocaValoresXWPF(text, r, "agencia", this.objetoCcb.getAgencia());
								text = trocaValoresXWPF(text, r, "contaCorrente", this.objetoCcb.getContaCorrente());					
								text = trocaValoresXWPF(text, r, "nomeBanco", this.objetoCcb.getNomeBanco() + " - " + this.objetoCcb.getNumeroBanco() );		
								text = trocaValoresXWPF(text, r, "digitoBanco", this.objetoCcb.getDigitoBanco());	
								text = trocaValoresXWPF(text, r, "tipoContaBanco", this.objetoCcb.getTipoContaBanco());	
				
								text = trocaValoresXWPF(text, r, "prazoContrato", this.objetoCcb.getPrazo());
								text = trocaValoresXWPF(text, r, "numeroParcelasPagamento", this.objetoCcb.getNumeroParcelasPagamento());
								text = trocaValoresXWPF(text, r, "vencimentoPrimeiraParcelaPagamento", this.objetoCcb.getVencimentoPrimeiraParcelaPagamento());
								text = trocaValoresXWPF(text, r, "taxaDeJurosMes", this.objetoCcb.getTaxaDeJurosMes());
								text = trocaValoresXWPF(text, r, "taxaDeJurosAno", this.objetoCcb.getTaxaDeJurosAno());
								text = trocaValoresXWPF(text, r, "cetMes", this.objetoCcb.getCetMes());
								text = trocaValoresXWPF(text, r, "cetAno", this.objetoCcb.getCetAno());
								
								text = trocaValoresXWPFCci(text, r, "totalPrimeiraParcela", totalPrimeiraParcela, "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "TotalPrimeiraParcela", totalPrimeiraParcela);	
								text = trocaValoresXWPFCci(text, r, "valorMipParcela", this.objetoCcb.getValorMipParcela(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorMipParcela", this.objetoCcb.getValorMipParcela());			
								text = trocaValoresXWPFCci(text, r, "valorDfiParcela", this.objetoCcb.getValorDfiParcela(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorDfiParcela", this.objetoCcb.getValorDfiParcela());
								text = trocaValoresXWPFCci(text, r, "valorParcela", this.objetoCcb.getValorParcela(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorParcela", this.objetoCcb.getValorParcela());		
								
								text = trocaValoresXWPF(text, r, "numeroCCB", this.objetoCcb.getNumeroCcb());
								text = trocaValoresXWPF(text, r, "serieCCB", this.objetoCcb.getSerieCcb());
								text = trocaValoresXWPF(text, r, "numeroRegistroMatricula", this.objetoCcb.getNumeroRegistroMatricula());
								
								text = trocaValoresXWPF(text, r, "cartorioImovel", this.objetoCcb.getCartorioImovel());
								text = trocaValoresXWPF(text, r, "cidadeImovel", this.objetoCcb.getCidadeImovel());
								text = trocaValoresXWPF(text, r, "ufImovel", this.objetoCcb.getUfImovel());
								text = trocaValoresXWPF(text, r, "numeroImovel", this.objetoCcb.getNumeroImovel());
								text = trocaValoresXWPF(text, r, "inscricaoMunicipal", this.objetoCcb.getInscricaoMunicipal());
								text = trocaValoresXWPFCci(text, r, "vendaLeilao", this.objetoCcb.getVendaLeilao(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "VendaLeilao", this.objetoCcb.getVendaLeilao());	
								
								text = trocaValoresXWPF(text, r, "vencimentoUltimaParcelaPagamento", this.objetoCcb.getVencimentoUltimaParcelaPagamento());
								
								text = trocaValoresXWPF(text, r, "emissaoDia", this.objetoCcb.getDataDeEmissao().getDate());
								text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase());
								text = trocaValoresXWPF(text, r, "emissaoAno", (this.objetoCcb.getDataDeEmissao().getYear() + 1900));
								
								text = trocaValoresXWPF(text, r, "nomeEmitente", this.objetoCcb.getNomeEmitente().toUpperCase());	 		
								text = trocaValoresXWPF(text, r, "nomeTestemunha1", this.objetoCcb.getNomeTestemunha1());
								text = trocaValoresXWPF(text, r, "cpfTestemunha1", this.objetoCcb.getCpfTestemunha1());
								text = trocaValoresXWPF(text, r, "rgTestemunha1", this.objetoCcb.getRgTestemunha1());								
								text = trocaValoresXWPF(text, r, "nomeTestemunha2", this.objetoCcb.getNomeTestemunha2());
								text = trocaValoresXWPF(text, r, "cpfTestemunha2", this.objetoCcb.getCpfTestemunha2());
								text = trocaValoresXWPF(text, r, "rgTestemunha2", this.objetoCcb.getRgTestemunha2());
								
								if (text != null && text.contains("sistemaAmortizacao")) {
									if(CommonsUtil.mesmoValor(this.objetoCcb.getSistemaAmortizacao(), "Price")) {
										text = trocaValoresXWPF(text, r, "sistemaAmortizacao", "Tabela Price");
									} else if(CommonsUtil.mesmoValor(this.objetoCcb.getSistemaAmortizacao(), "SAC")) {
										text = trocaValoresXWPF(text, r, "sistemaAmortizacao", "SAC - Sistema de Amortização Constante");
									} else {
										text = trocaValoresXWPF(text, r, "sistemaAmortizacao", "");
									}
								}
								
								if (text != null && text.contains("participantesCci")) {
									text = text.replace("participantesCci", "");
									r.setText(text, 0);			
								}
								
								if (text != null && text.contains("ImagemImovel") && filesList.size() > 0) {
									int iImagem = 0;
									for(UploadedFile imagem :  filesList) {
										r.addBreak();
										this.populateFiles(iImagem);
										r.addPicture(this.getBis(), fileTypeInt, fileName.toLowerCase(), Units.toEMU(400), Units.toEMU(300));
										r.addBreak();	
										iImagem++;
									}
									text = trocaValoresXWPF(text, r, "ImagemImovel", "");						
									adicionarEnter(text, r);
								} else if(text != null && text.contains("ImagemImovel") && filesList.size() == 0) {
									text = trocaValoresXWPF(text, r, "ImagemImovel", "");
								}
							}
						}
						
						for (XWPFTable t : cell.getTables()) {
							for (XWPFTableRow row2 : t.getRows()) {
								for (XWPFTableCell cell2 : row2.getTableCells()) {
									for (XWPFParagraph p2 : cell2.getParagraphs()) {
										for (XWPFRun r2 : p2.getRuns()) {
											String text = r2.getText(0);
											text = trocaValoresXWPF(text, r2, "cartorioImovel", this.objetoCcb.getCartorioImovel());
											text = trocaValoresXWPF(text, r2, "cidadeImovel", this.objetoCcb.getCidadeImovel());
											text = trocaValoresXWPF(text, r2, "ufImovel", this.objetoCcb.getUfImovel());		
											text = trocaValoresXWPF(text, r2, "numeroImovel", this.objetoCcb.getNumeroImovel());
											text = trocaValoresXWPF(text, r2, "inscricaoMunicipal", this.objetoCcb.getInscricaoMunicipal());
											text = trocaValoresXWPFCci(text, r2, "vendaLeilao", this.objetoCcb.getVendaLeilao(), "R$ ");
											text = trocaValoresDinheiroExtensoXWPF(text, r2, "VendaLeilao", this.objetoCcb.getVendaLeilao());	
											
										}
									}
								}
							}
						}
					}
				}
			}
		    
		    int indexParcela = 1;
			
			//calcularSimulador();

			XWPFParagraph paragraph1 = document.createParagraph();
			paragraph1.setAlignment(ParagraphAlignment.CENTER);
			paragraph1.setSpacingBefore(0);
			paragraph1.setSpacingAfter(0);
			
			XWPFParagraph paragraph2 = document.createParagraph();
			paragraph2.setAlignment(ParagraphAlignment.RIGHT);
			paragraph2.setSpacingBefore(0);
			paragraph2.setSpacingAfter(0);
			
			int fontSize = 7;
			for(SimulacaoDetalheVO p : simulador.getParcelas()) {
				table = document.getTableArray(2);
				table.insertNewTableRow(indexParcela);
				tableRow1 = table.getRow(indexParcela);
				tableRow1.createCell();
				tableRow1.getCell(0).setParagraph(paragraph1);
				tableRow1.getCell(0).getCTTc().addNewTcPr().addNewTcBorders();
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(p.getNumeroParcela().toString());
				tableRow1.createCell();
				tableRow1.getCell(1).setParagraph(paragraph2);
				tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataData(DateUtil.adicionarPeriodo(simulador.getDataSimulacao(), p.getNumeroParcela().intValue(), Calendar.MONTH), "dd/MM/yyyy"));
				tableRow1.createCell();
				tableRow1.getCell(2).setParagraph(paragraph2);
				tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(2).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getSaldoDevedorInicial(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(3).setParagraph(paragraph2);
				tableRow1.getCell(3).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(3).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getAmortizacao(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(4).setParagraph(paragraph2);
				tableRow1.getCell(4).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(4).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getJuros(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(5).setParagraph(paragraph2);
				tableRow1.getCell(5).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(5).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getJuros().add(p.getAmortizacao()), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(6).setParagraph(paragraph2);
				tableRow1.getCell(6).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(6).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getTxAdm(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(7).setParagraph(paragraph2);
				tableRow1.getCell(7).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(7).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getSeguroMIP(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(8).setParagraph(paragraph2);
				tableRow1.getCell(8).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(8).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getSeguroDFI(), "R$ "));
				tableRow1.createCell();
				tableRow1.getCell(9).setParagraph(paragraph2);
				tableRow1.getCell(9).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				run = tableRow1.getCell(9).getParagraphArray(0).createRun();
				run.setFontSize(fontSize);
				run.setFontFamily("Calibri");
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getValorParcela(), "R$ ") + " + IPCA");
				indexParcela++;////////////////////////////////////////////////////////////////////////////////
			}
			
			table = document.getTableArray(1);			
			CabecalhoAnexo1(table, 0, 1, CommonsUtil.formataData(this.objetoCcb.getDataDeEmissao(), "dd/MM/yyyy"));
			CabecalhoAnexo1(table, 1, 1, CommonsUtil.formataData(this.objetoCcb.getVencimentoUltimaParcelaPagamento(), "dd/MM/yyyy"));	
			CabecalhoAnexo1(table, 2, 1, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getValorCredito(), "R$ "));
			CabecalhoAnexo1(table, 2, 4, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getTaxaDeJurosMes(),"") + "%");
			
			CabecalhoAnexo1(table, 3, 1, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getValorIOF(), "R$ "));
			CabecalhoAnexo1(table, 3, 4, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getTaxaDeJurosAno(),"") + "%");
			
			CabecalhoAnexo1(table, 4, 1, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getCustoEmissao(), "R$ "));
			CabecalhoAnexo1(table, 4, 4, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getCetMes(),"") + "%");
			CabecalhoAnexo1(table, 4, 7, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getVlrImovel(), "R$ "));
			
			CabecalhoAnexo1(table, 5, 1, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getValorDespesas(), "R$ "));
			CabecalhoAnexo1(table, 5, 4, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getCetAno(),"") + "%");
			CabecalhoAnexo1(table, 5, 7, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getMontanteMIP(), "R$ "));
			
			CabecalhoAnexo1(table, 6, 1, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getValorLiquidoCredito(), "R$ "));
			CabecalhoAnexo1(table, 6, 4, CommonsUtil.stringValue(
					CommonsUtil.formataValorInteiro(
							DateUtil.getDaysBetweenDates(this.objetoCcb.getDataDeEmissao(), this.objetoCcb.getVencimentoUltimaParcelaPagamento()))));
			CabecalhoAnexo1(table, 6, 7, CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getMontanteDFI(), "R$ "));
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
			String nomeSemvirgula = this.objetoCcb.getNomeEmitente();
			if(nomeSemvirgula.contains(",")) {
				nomeSemvirgula = nomeSemvirgula.replace(",", "");
		    }
			gerador.open(String.format("Galleria Bank - CCI %s.docx", ""));
			gerador.feed(new ByteArrayInputStream(out.toByteArray()));
			gerador.close();
			criarCcbNosistema();	
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public StreamedContent geraCessao() throws IOException{
		try {
			XWPFDocument document;
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "TERCEIRO GARANTIDOR")) {
					this.objetoCcb.setTerceiroGarantidor(true);
				}
			}
			
			document = new XWPFDocument(getClass().getResourceAsStream("/resource/CESSAO.docx"));
					
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Calibri");
			fonts.setAscii("Calibri");
			fonts.setEastAsia("Calibri");
			fonts.setCs("Calibri");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")) {
					if(CommonsUtil.semValor(this.objetoCcb.getNomeEmitente())) {
						this.objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
					}
					if(CommonsUtil.semValor(cpfEmitente)) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							this.objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
						} else {
							this.objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
						}
					}
					participante.setTipoParticipante("DEVEDOR");
				}
			}
			
			for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);
			            
			            text = trocaValoresXWPFCci(text, r, "valorCredito", this.objetoCcb.getValorCredito(), "R$ ");
						text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorCredito", this.objetoCcb.getValorCredito());	
						text = trocaValoresXWPF(text, r, "numeroCCI", this.objetoCcb.getNumeroCcb());
						text = trocaValoresXWPF(text, r, "nomeEmitente", this.objetoCcb.getNomeEmitente().toUpperCase());				
						text = trocaValoresXWPF(text, r, "emissaoDia", this.objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (this.objetoCcb.getDataDeEmissao().getYear() + 1900));
						
						if (text != null && text.contains("ImagemImovel") && filesList.size() > 0) {
							int iImagem = 0;
							for(UploadedFile imagem :  filesList) {
								r.addBreak();
								this.populateFiles(iImagem);
								r.addPicture(this.getBis(), fileTypeInt, fileName.toLowerCase(), Units.toEMU(400), Units.toEMU(300));
								r.addBreak();	
								iImagem++;
							}
							text = trocaValoresXWPF(text, r, "ImagemImovel", "");						
							adicionarEnter(text, r);
						} else if(text != null && text.contains("ImagemImovel") && filesList.size() == 0) {
							text = trocaValoresXWPF(text, r, "ImagemImovel", "");
						}
						
						text = trocaValoresXWPF(text, r, "cartorioImovel", this.objetoCcb.getCartorioImovel());
						text = trocaValoresXWPF(text, r, "cidadeImovel", this.objetoCcb.getCidadeImovel());
						text = trocaValoresXWPF(text, r, "ufImovel", this.objetoCcb.getUfImovel());
						text = trocaValoresXWPF(text, r, "numeroImovel", this.objetoCcb.getNumeroImovel());
						
						text = trocaValoresXWPF(text, r, "numeroParcelasPagamento", this.objetoCcb.getNumeroParcelasPagamento());
						text = trocaValoresNumeroExtensoXWPF(text, r, "NumeroParcelasPagamento", this.objetoCcb.getNumeroParcelasPagamento());
						text = trocaValoresXWPF(text, r, "vencimentoPrimeiraParcelaPagamento", this.objetoCcb.getVencimentoPrimeiraParcelaPagamento());
						text = trocaValoresXWPF(text, r, "vencimentoUltimaParcelaPagamento", this.objetoCcb.getVencimentoUltimaParcelaPagamento());
						
						text = trocaValoresXWPF(text, r, "taxaDeJurosMes", this.objetoCcb.getTaxaDeJurosMes());
						text = trocaValoresXWPF(text, r, "taxaDeJurosAno", this.objetoCcb.getTaxaDeJurosAno());
						
						text = trocaValoresXWPF(text, r, "nomeTestemunha1", this.objetoCcb.getNomeTestemunha1());
						text = trocaValoresXWPF(text, r, "cpfTestemunha1", this.objetoCcb.getCpfTestemunha1());
						text = trocaValoresXWPF(text, r, "nomeTestemunha2", this.objetoCcb.getNomeTestemunha2());
						text = trocaValoresXWPF(text, r, "cpfTestemunha2", this.objetoCcb.getCpfTestemunha2());			       
					}
			    }
			}	
								
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			
			InputStream in = new ByteArrayInputStream(out.toByteArray());
					
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
			String nomeSemvirgula = this.objetoCcb.getNomeEmitente();
			if(nomeSemvirgula.contains(",")) {
				nomeSemvirgula = nomeSemvirgula.replace(",", "");
		    }
			if(SiscoatConstants.DEV && CommonsUtil.sistemaWindows()) {
				gerador.open(String.format("Galleria Bank - CESSAO %s.pdf", ""));
				gerador.feed(new ByteArrayInputStream(CommonsUtil.wordToPdf(in).toByteArray()));
			} else {
				gerador.open(String.format("Galleria Bank - CESSAO %s.docx", ""));
				gerador.feed(new ByteArrayInputStream(out.toByteArray()));
			}
			gerador.close();
			criarCcbNosistema();	
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public StreamedContent geraInstrumentoEmissaoCCI() throws IOException{
		try {
			XWPFDocument document;
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "TERCEIRO GARANTIDOR")) {
					this.objetoCcb.setTerceiroGarantidor(true);
				}
			}
			
			document = new XWPFDocument(getClass().getResourceAsStream("/resource/Instrumento_Emissao_CCI_BMP.docx"));
					
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Calibri");
			fonts.setAscii("Calibri");
			fonts.setEastAsia("Calibri");
			fonts.setCs("Calibri");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")) {
					if(CommonsUtil.semValor(this.objetoCcb.getNomeEmitente())) {
						this.objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
					}
					if(CommonsUtil.semValor(cpfEmitente)) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							this.objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
						} else {
							this.objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
						}
					}
					if(CommonsUtil.semValor(this.objetoCcb.getLogradouroEmitente())) {
						this.objetoCcb.setLogradouroEmitente(participante.getPessoa().getEndereco());
					}
					if(CommonsUtil.semValor(this.objetoCcb.getNumeroEmitente())) {
						this.objetoCcb.setNumeroEmitente(participante.getPessoa().getNumero());
					}
					if(CommonsUtil.semValor(this.objetoCcb.getComplementoEmitente())) {
						this.objetoCcb.setComplementoEmitente(participante.getPessoa().getComplemento());
					}
					if(CommonsUtil.semValor(this.objetoCcb.getCepEmitente())) {
						this.objetoCcb.setCepEmitente(participante.getPessoa().getCep());
					}
					if(CommonsUtil.semValor(this.objetoCcb.getCidadeEmitente())) {
						this.objetoCcb.setCidadeEmitente(participante.getPessoa().getCidade());
					}
					if(CommonsUtil.semValor(this.objetoCcb.getUfEmitente())) {
						this.objetoCcb.setUfEmitente(participante.getPessoa().getEstado());
					}				
				}
			}
			
			int prazoAno = CommonsUtil.intValue(this.objetoCcb.getPrazo()) / 12;
			String prazoAnoStr = CommonsUtil.stringValue(prazoAno);
			String estado = estadoPorExtenso(this.objetoCcb.getUfImovel());
						
		    for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
					            String text = r.getText(0);
					            
					            text = trocaValoresXWPF(text, r, "emissaoDia", this.objetoCcb.getDataDeEmissao().getDate());
								text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase());
								text = trocaValoresXWPF(text, r, "emissaoAno", (this.objetoCcb.getDataDeEmissao().getYear() + 1900));
								
								text = trocaValoresXWPF(text, r, "numeroCCB", this.objetoCcb.getNumeroCcb());
					            
								text = trocaValoresXWPF(text, r, "nomeEmitente", this.objetoCcb.getNomeEmitente().toUpperCase());	
								text = trocaValoresXWPF(text, r, "cpfEmitente", this.objetoCcb.getCpfEmitente());								
								text = trocaValoresXWPF(text, r, "logradouroEmitente", this.objetoCcb.getLogradouroEmitente());	
								text = trocaValoresXWPF(text, r, "numeroEmitente", this.objetoCcb.getNumeroEmitente());	
								text = trocaValoresXWPF(text, r, "complementoEmitente", this.objetoCcb.getComplementoEmitente());								
								text = trocaValoresXWPF(text, r, "cepEmitente", this.objetoCcb.getCepEmitente());
								text = trocaValoresXWPF(text, r, "cidadeEmitente", this.objetoCcb.getCidadeEmitente());			
								text = trocaValoresXWPF(text, r, "ufEmitente", this.objetoCcb.getUfEmitente());							
									
								text = trocaValoresXWPFCci(text, r, "valorCredito", this.objetoCcb.getValorCredito(), "R$ ");
								text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorCredito", this.objetoCcb.getValorCredito());								
				
								text = trocaValoresXWPF(text, r, "cartorioImovel", this.objetoCcb.getCartorioImovel());
								text = trocaValoresXWPF(text, r, "cidadeImovel", this.objetoCcb.getCidadeImovel());
								text = trocaValoresXWPF(text, r, "ufImovel", estado);
								text = trocaValoresXWPF(text, r, "estadoImovel", this.objetoCcb.getUfImovel());			
								text = trocaValoresXWPF(text, r, "numeroImovel", this.objetoCcb.getNumeroImovel());
								text = trocaValoresXWPF(text, r, "logradouroRuaImovel", this.objetoCcb.getLogradouroRuaImovel());
								text = trocaValoresXWPF(text, r, "logradouroNumeroImovel", this.objetoCcb.getLogradouroNumeroImovel());
								text = trocaValoresXWPF(text, r, "bairroImovel", this.objetoCcb.getBairroImovel());
								text = trocaValoresXWPF(text, r, "cepImovel", this.objetoCcb.getCepImovel());

								text = trocaValoresXWPF(text, r, "parcelaDia", this.objetoCcb.getVencimentoPrimeiraParcelaPagamento().getDate());
								text = trocaValoresXWPF(text, r, "parcelaMes", CommonsUtil.formataMesExtenso(this.objetoCcb.getVencimentoPrimeiraParcelaPagamento()).toLowerCase());
								text = trocaValoresXWPF(text, r, "parcelaAno", (this.objetoCcb.getVencimentoPrimeiraParcelaPagamento().getYear() + 1900));
								
								text = trocaValoresXWPF(text, r, "vencimentoDia", this.objetoCcb.getVencimentoUltimaParcelaPagamento().getDate());
								text = trocaValoresXWPF(text, r, "vencimentoMes", CommonsUtil.formataMesExtenso(this.objetoCcb.getVencimentoUltimaParcelaPagamento()).toLowerCase());
								text = trocaValoresXWPF(text, r, "vencimentoAno", (this.objetoCcb.getVencimentoUltimaParcelaPagamento().getYear() + 1900));
								
								text = trocaValoresXWPF(text, r, "prazoAno", prazoAnoStr);
								text = trocaValoresNumeroExtensoXWPF(text, r, "Prazo", prazoAnoStr);
								
								text = trocaValoresXWPF(text, r, "taxaDeJurosMes", this.objetoCcb.getTaxaDeJurosMes());
								
								text = trocaValoresXWPF(text, r, "nomeTestemunha1", this.objetoCcb.getNomeTestemunha1());
								text = trocaValoresXWPF(text, r, "cpfTestemunha1", this.objetoCcb.getCpfTestemunha1());
								text = trocaValoresXWPF(text, r, "rgTestemunha1", this.objetoCcb.getRgTestemunha1());
								
								text = trocaValoresXWPF(text, r, "nomeTestemunha2", this.objetoCcb.getNomeTestemunha2());
								text = trocaValoresXWPF(text, r, "cpfTestemunha2", this.objetoCcb.getCpfTestemunha2());		
								text = trocaValoresXWPF(text, r, "rgTestemunha2", this.objetoCcb.getRgTestemunha2());
							}
						}
					}
				}
			}
		    
		    for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);
			            
			            text = trocaValoresXWPFCci(text, r, "valorCredito", this.objetoCcb.getValorCredito(), "R$ ");
						text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorCredito", this.objetoCcb.getValorCredito());								
						text = trocaValoresXWPF(text, r, "emissaoDia", this.objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (this.objetoCcb.getDataDeEmissao().getYear() + 1900));
					}
			    }
			}
		   
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
			String nomeSemvirgula = this.objetoCcb.getNomeEmitente();
			if(nomeSemvirgula.contains(",")) {
				nomeSemvirgula = nomeSemvirgula.replace(",", "");
		    }
			String nomeArquivoDownload = String.format("Galleria Bank - Instrumento Emissão CCI BMP %s.docx", "");
			gerador.open(nomeArquivoDownload);
			gerador.feed(new ByteArrayInputStream(out.toByteArray()));
			gerador.close();
			criarCcbNosistema();	
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
		
	public StreamedContent geraEndossosEmPretoGalleria() throws IOException{
		try {
			XWPFDocument document;
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "TERCEIRO GARANTIDOR")) {
					this.objetoCcb.setTerceiroGarantidor(true);
				}
			}	
			document = new XWPFDocument(getClass().getResourceAsStream("/resource/EndossosEmPretoGalleria.docx"));			
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Calibri");
			fonts.setAscii("Calibri");
			fonts.setEastAsia("Calibri");
			fonts.setCs("Calibri");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")) {
					if(CommonsUtil.semValor(this.objetoCcb.getNomeEmitente())) {
						this.objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
					}
					if(CommonsUtil.semValor(cpfEmitente)) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							this.objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
						} else {
							this.objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
						}
					}				
				}
			}

		    for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);
			            text = trocaValoresXWPF(text, r, "emissaoData", this.objetoCcb.getDataDeEmissao());								
						text = trocaValoresXWPF(text, r, "numeroCCI", this.objetoCcb.getNumeroCcb());		            
						text = trocaValoresXWPF(text, r, "nomeEmitente", this.objetoCcb.getNomeEmitente().toUpperCase());	
						text = trocaValoresXWPF(text, r, "cpfEmitente", this.objetoCcb.getCpfEmitente()); 
					}
			    }
			}
		   
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
			String nomeSemvirgula = this.objetoCcb.getNomeEmitente();
			if(nomeSemvirgula.contains(",")) {
				nomeSemvirgula = nomeSemvirgula.replace(",", "");
		    }
			gerador.open(String.format("Galleria Bank - Endossos Em Preto %s.docx", ""));
			gerador.feed(new ByteArrayInputStream(out.toByteArray()));
			gerador.close();
			criarCcbNosistema();	
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public StreamedContent geraDeclaracaoNaoUniaoEstavel(CcbParticipantes participante) throws IOException{
		try {
			//PagadorRecebedor pagador
			XWPFDocument document;
			XWPFRun run;
			XWPFRun run2;
			
			document = new XWPFDocument(getClass().getResourceAsStream("/resource/DeclaracaoNaoUniaoEstavel.docx"));			
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Calibri");
			fonts.setAscii("Calibri");
			fonts.setEastAsia("Calibri");
			fonts.setCs("Calibri");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			run = document.getParagraphs().get(1).getRuns().get(1);
			document.getParagraphs().get(1).setAlignment(ParagraphAlignment.BOTH);
			//run.setFontSize(12);
			run.setText(participante.getPessoa().getNome().toUpperCase() + ", ");
			run.setBold(true);
			run.setCharacterSpacing(1*10);
			run2 = document.getParagraphs().get(1).insertNewRun(2);
			//run2.setFontFamily("Calibri");
			geraParagrafoPF(run2, participante);
			//run2.addCarriageReturn();

		    for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);		            
			            if(CommonsUtil.semValor(text)) {
			            	continue;
			            }				            
			            text = trocaValoresXWPF(text, r, "cidadeEmitente", (participante.getPessoa().getCidade()));    
			            text = trocaValoresXWPF(text, r, "ufEmitente", (participante.getPessoa().getEstado()));			            
			            text = trocaValoresXWPF(text, r, "emissaoDia", this.objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (this.objetoCcb.getDataDeEmissao().getYear() + 1900));						
						text = trocaValoresXWPF(text, r, "nomeEmitente", (participante.getPessoa().getNome()));    
			            text = trocaValoresXWPF(text, r, "cpfEmitente", (participante.getPessoa().getCpf()));
					}
			    }
			}
		    
		    for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
					            String text = r.getText(0);					            
					            if(CommonsUtil.semValor(text)) {
					            	continue;
					            }				         
								text = trocaValoresXWPF(text, r, "nomeTestemunha1", this.objetoCcb.getNomeTestemunha1());
								text = trocaValoresXWPF(text, r, "cpfTestemunha1", this.objetoCcb.getCpfTestemunha1());
								text = trocaValoresXWPF(text, r, "rgTestemunha1", this.objetoCcb.getRgTestemunha1());						
								text = trocaValoresXWPF(text, r, "nomeTestemunha2", this.objetoCcb.getNomeTestemunha2());
								text = trocaValoresXWPF(text, r, "cpfTestemunha2", this.objetoCcb.getCpfTestemunha2());		
								text = trocaValoresXWPF(text, r, "rgTestemunha2", this.objetoCcb.getRgTestemunha2());
							}
						}
					}
				}
			}
		   
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			String nomeSemvirgula = this.objetoCcb.getNomeEmitente();
			if(nomeSemvirgula.contains(",")) {
				nomeSemvirgula = nomeSemvirgula.replace(",", "");
		    }			
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
			gerador.open(String.format("Galleria Bank - Declaracao Nao Uniao Estavel%s.docx", ""));
			gerador.feed(new ByteArrayInputStream(out.toByteArray()));
			gerador.close();
			criarCcbNosistema();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public StreamedContent geraDeclaracaoDestinacaoRecursos(CcbParticipantes participante) throws IOException{
		try {
			//PagadorRecebedor pagador
			XWPFDocument document;
			XWPFRun run;
			XWPFRun run2;
			
			document = new XWPFDocument(getClass().getResourceAsStream("/resource/DeclaracaoDeCienciaDestinacaoDeRecurso.docx"));			
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Calibri");
			fonts.setAscii("Calibri");
			fonts.setEastAsia("Calibri");
			fonts.setCs("Calibri");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			run = document.getParagraphs().get(5).insertNewRun(0);
			document.getParagraphs().get(5).setAlignment(ParagraphAlignment.BOTH);
			//run.setFontSize(12);
			run.setText(participante.getPessoa().getNome().trim().toUpperCase() + ", ");
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.NONE);
			run.setCharacterSpacing(1*10);
			run.setFontSize(11);
			run2 = document.getParagraphs().get(5).insertNewRun(1);
			//run2.setFontFamily("Calibri");
			geraParagrafoPF(run2, participante);
			run2.setUnderline(UnderlinePatterns.NONE);
			run2.setFontSize(11);
			run2.setText(run2.getText(0).replace(';', ','));
			//run2.addCarriageReturn();

		    for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);		            
			            if(CommonsUtil.semValor(text)) {
			            	continue;
			            }			           
			            
			            text = trocaValoresXWPF(text, r, "emissaoDia", this.objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (this.objetoCcb.getDataDeEmissao().getYear() + 1900));						
						text = trocaValoresXWPF(text, r, "nomeEmitente", (participante.getPessoa().getNome()));    
			            text = trocaValoresXWPF(text, r, "numeroCCI", this.objetoCcb.getNumeroCcb());		          
			            text = trocaValoresXWPF(text, r, "cartorioImovel", this.objetoCcb.getCartorioImovel());
						text = trocaValoresXWPF(text, r, "cidadeImovel", this.objetoCcb.getCidadeImovel());
						text = trocaValoresXWPF(text, r, "ufImovel", this.objetoCcb.getUfImovel());
						text = trocaValoresXWPF(text, r, "numeroMatricula", this.objetoCcb.getNumeroRegistroMatricula());
					}
			    }
			}
		    
		    for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
					            String text = r.getText(0);					            
					            if(CommonsUtil.semValor(text)) {
					            	continue;
					            }				         
							}
						}
					}
				}
			}
		   
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
			String nomeSemvirgula = this.objetoCcb.getNomeEmitente();
			if(nomeSemvirgula.contains(",")) {
				nomeSemvirgula = nomeSemvirgula.replace(",", "");
		    }
			gerador.open(String.format("Galleria Bank - Declaracao Destinacao Recursos%s.docx", ""));
			gerador.feed(new ByteArrayInputStream(out.toByteArray()));
			gerador.close();
			criarCcbNosistema();	
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public StreamedContent geraDeclaracaoUniaoEstavel(CcbParticipantes participante) throws IOException{
		try {
			//PagadorRecebedor pagador
			XWPFDocument document;
			XWPFRun run;
			XWPFRun run2;
			
			document = new XWPFDocument(getClass().getResourceAsStream("/resource/DeclaracaoUniaoEstavel.docx"));			
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Calibri");
			fonts.setAscii("Calibri");
			fonts.setEastAsia("Calibri");
			fonts.setCs("Calibri");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			int paragraph = 4;
			run = document.getParagraphs().get(paragraph).insertNewRun(1);
			document.getParagraphs().get(paragraph).setAlignment(ParagraphAlignment.BOTH);
			//run.setFontSize(12);
			run.setText(participante.getPessoa().getNome().toUpperCase() + ", ");
			run.setBold(true);
			run.setCharacterSpacing(1*10);
			run2 = document.getParagraphs().get(paragraph).insertNewRun(2);
			run2.setFontSize(11);
			String filho;
			String nacionalidade = participante.getNacionalidade();
			String estadoCivilStr = "";
			PagadorRecebedor pessoa = participante.getPessoa();

			if (participante.isFeminino()) {
				if (CommonsUtil.mesmoValor(participante.getNacionalidade(), "brasileiro")) {
					nacionalidade = "brasileira";
				}
				filho = "filha";
				if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "SOLTEIRO")) {
					estadoCivilStr = "solteira";
				} else if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "VIÚVO")) {
					estadoCivilStr = "viúva";
				} else if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "DIVORCIADO")) {
					estadoCivilStr = "divorciada";
				} else if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "SEPARADO")) {
					estadoCivilStr = "separada";
				} else if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "SEPARADO JUDICIALMENTE")) {
					estadoCivilStr = "separada judicialmente";
				} 
			} else {
				estadoCivilStr = pessoa.getEstadocivil().toLowerCase();
				filho = "filho";
			}
			estadoCivilStr = estadoCivilStr + " convivente em união estável";
			
			PagadorRecebedorDao pagadorDao = new PagadorRecebedorDao();
			PagadorRecebedor conjuge = pagadorDao.findByFilter("cpf", participante.getPessoa().getCpfConjuge()).get(0);
			CcbParticipantesDao partDao = new CcbParticipantesDao();
			CcbParticipantes participanteConjuge = partDao.findByFilter("pessoa", conjuge).get(0);
			String filhoConjuge;
			String nacionalidadeConjuge = participanteConjuge.getNacionalidade();
			String estadoCivilStrConjuge = "";
			if (participanteConjuge.isFeminino()) {
				if (CommonsUtil.mesmoValor(participanteConjuge.getNacionalidade(), "brasileiro")) {
					nacionalidadeConjuge = "brasileira";
				}
				filhoConjuge = "filha";
				if (CommonsUtil.mesmoValor(conjuge.getEstadocivil(), "SOLTEIRO")) {
					estadoCivilStrConjuge = "solteira";
				} else if (CommonsUtil.mesmoValor(conjuge.getEstadocivil(), "VIÚVO")) {
					estadoCivilStrConjuge = "viúva";
				} else if (CommonsUtil.mesmoValor(conjuge.getEstadocivil(), "DIVORCIADO")) {
					estadoCivilStrConjuge = "divorciada";
				} else if (CommonsUtil.mesmoValor(conjuge.getEstadocivil(), "SEPARADO")) {
					estadoCivilStrConjuge = "separada";
				} else if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "SEPARADO JUDICIALMENTE")) {
					estadoCivilStrConjuge = "separada judicialmente";
				} 
			} else {
				estadoCivilStrConjuge = conjuge.getEstadocivil().toLowerCase();
				filhoConjuge = "filho";
			}
			estadoCivilStrConjuge = estadoCivilStrConjuge + " convivente em união estável";
			
			
			run2.setText( filho + " de " + pessoa.getNomeMae() + " e " + pessoa.getNomePai() + ", "
					+ nacionalidade + ", "+ pessoa.getAtividade() + ", "+ estadoCivilStr + ","
					+ " portador(a) da Cédula de Identidade RG nº "+ pessoa.getRg() + " " + pessoa.getOrgaoEmissorRG() + ","
					+ " inscrito(a) no CPF/MF sob o nº "+ pessoa.getCpf() +", endereço eletrônico: "+ pessoa.getEmail() +" e ");	
			
			run = document.getParagraphs().get(paragraph).insertNewRun(3);
			run.setFontSize(11);
			run.setText(conjuge.getNome().toUpperCase() + ", ");
			run.setBold(true);
			run.setCharacterSpacing(1*10);
			
			run2 = document.getParagraphs().get(paragraph).insertNewRun(4);
			run2.setFontSize(11);
			run2.setText( filhoConjuge + " de " + conjuge.getNomeMae() + " e " + conjuge.getNomePai() + ", "
					+ nacionalidadeConjuge + ", "+ conjuge.getAtividade() + ", "+ estadoCivilStrConjuge + ","
					+ " portador(a) da Cédula de Identidade RG nº "+ conjuge.getRg() + " " + conjuge.getOrgaoEmissorRG() + ","
					+ " inscrito(a) no CPF/MF sob o nº "+ conjuge.getCpf() +", endereço eletrônico: "+ conjuge.getEmail() 
					+ ", residentes e domiciliados à "+ pessoa.getEndereco() +", nº "+ pessoa.getNumero() +", "
					+ pessoa.getComplemento() + ", "+ pessoa.getBairro() + ", " 
					+ pessoa.getCidade()+"/"+pessoa.getEstado()+", CEP "+ pessoa.getCep()+"; ");
			
			////
		

		    for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);		            
			            if(CommonsUtil.semValor(text)) {
			            	continue;
			            }			            	            
			            
			            text = trocaValoresXWPF(text, r, "nomeEmitente", (participante.getPessoa().getNome()));    
						text = trocaValoresXWPF(text, r, "nomeConjuge", (participante.getPessoa().getNomeConjuge()));
						text = trocaValoresXWPF(text, r, "cpfEmitente", (participante.getPessoa().getCpf()));    
						text = trocaValoresXWPF(text, r, "cpfConjuge", (participante.getPessoa().getCpfConjuge()));
							            
			            text = trocaValoresXWPF(text, r, "emissaoDia", this.objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (this.objetoCcb.getDataDeEmissao().getYear() + 1900));						
			         
			            text = trocaValoresXWPF(text, r, "nomeTestemunha1", this.objetoCcb.getNomeTestemunha1());
						text = trocaValoresXWPF(text, r, "cpfTestemunha1", this.objetoCcb.getCpfTestemunha1());
						text = trocaValoresXWPF(text, r, "rgTestemunha1", this.objetoCcb.getRgTestemunha1());						
						text = trocaValoresXWPF(text, r, "nomeTestemunha2", this.objetoCcb.getNomeTestemunha2());
						text = trocaValoresXWPF(text, r, "cpfTestemunha2", this.objetoCcb.getCpfTestemunha2());		
						text = trocaValoresXWPF(text, r, "rgTestemunha2", this.objetoCcb.getRgTestemunha2());
					}
			    }
			}
		    
		    for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
					            String text = r.getText(0);					            
					            if(CommonsUtil.semValor(text)) {
					            	continue;
					            }				         
								text = trocaValoresXWPF(text, r, "nomeTestemunha1", this.objetoCcb.getNomeTestemunha1());
								text = trocaValoresXWPF(text, r, "cpfTestemunha1", this.objetoCcb.getCpfTestemunha1());
								text = trocaValoresXWPF(text, r, "rgTestemunha1", this.objetoCcb.getRgTestemunha1());						
								text = trocaValoresXWPF(text, r, "nomeTestemunha2", this.objetoCcb.getNomeTestemunha2());
								text = trocaValoresXWPF(text, r, "cpfTestemunha2", this.objetoCcb.getCpfTestemunha2());		
								text = trocaValoresXWPF(text, r, "rgTestemunha2", this.objetoCcb.getRgTestemunha2());
							}
						}
					}
				}
			}
		   
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
			String nomeSemvirgula = this.objetoCcb.getNomeEmitente();
			if(nomeSemvirgula.contains(",")) {
				nomeSemvirgula = nomeSemvirgula.replace(",", "");
		    }
			gerador.open(String.format("Galleria Bank - Declaracao Nao Uniao Estavel%s.docx", ""));
			gerador.feed(new ByteArrayInputStream(out.toByteArray()));
			gerador.close();
			criarCcbNosistema();	
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public StreamedContent geraTermoResponsabilidadeAnuenciaPaju(CcbParticipantes participante) throws IOException{
		try {
			//PagadorRecebedor pagador
			XWPFDocument document;
			XWPFRun run;
			XWPFRun run2;
			
			document = new XWPFDocument(getClass().getResourceAsStream("/resource/TermoDeResponsabilidadeAnuenciaPaju.docx"));			
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Calibri");
			fonts.setAscii("Calibri");
			fonts.setEastAsia("Calibri");
			fonts.setCs("Calibri");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			int paragraph = 2;
			run = document.getParagraphs().get(paragraph).insertNewRun(0);
			document.getParagraphs().get(paragraph).setAlignment(ParagraphAlignment.BOTH);
			//run.setFontSize(12);
			run.setText(participante.getPessoa().getNome().trim().toUpperCase() + ", ");
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.NONE);
			run.setCharacterSpacing(1*10);
			run.setFontSize(10);
			run2 = document.getParagraphs().get(paragraph).insertNewRun(1);
			run = document.getParagraphs().get(paragraph).insertNewRun(2);
			//run2.setFontFamily("Calibri");
			geraParagrafoPF(run2, participante);
			run2.setUnderline(UnderlinePatterns.NONE);
			run2.setFontSize(10);
			run2.setText(run2.getText(0).replace(';', ','));
			//run2.addCarriageReturn();

		    for (XWPFParagraph p : document.getParagraphs()) {
				List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);		            
			            if(CommonsUtil.semValor(text)) {
			            	continue;
			            }			           
			            
			            text = trocaValoresXWPF(text, r, "emissaoDia", this.objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (this.objetoCcb.getDataDeEmissao().getYear() + 1900));						
						text = trocaValoresXWPF(text, r, "nomeEmitente", (participante.getPessoa().getNome()));    
			            text = trocaValoresXWPF(text, r, "numeroCCI", this.objetoCcb.getNumeroCcb());		          
			            text = trocaValoresXWPF(text, r, "cartorioImovel", this.objetoCcb.getCartorioImovel());
						text = trocaValoresXWPF(text, r, "cidadeImovel", this.objetoCcb.getCidadeImovel());
						text = trocaValoresXWPF(text, r, "ufImovel", this.objetoCcb.getUfImovel());
						text = trocaValoresXWPF(text, r, "numeroMatricula", this.objetoCcb.getNumeroRegistroMatricula());
						
						text = trocaValoresXWPF(text, r, "nomeTestemunha1", this.objetoCcb.getNomeTestemunha1());
						text = trocaValoresXWPF(text, r, "cpfTestemunha1", this.objetoCcb.getCpfTestemunha1());
						text = trocaValoresXWPF(text, r, "rgTestemunha1", this.objetoCcb.getRgTestemunha1());						
						text = trocaValoresXWPF(text, r, "nomeTestemunha2", this.objetoCcb.getNomeTestemunha2());
						text = trocaValoresXWPF(text, r, "cpfTestemunha2", this.objetoCcb.getCpfTestemunha2());		
						text = trocaValoresXWPF(text, r, "rgTestemunha2", this.objetoCcb.getRgTestemunha2());
						
						Date pajuGerado = this.objetoCcb.getObjetoContratoCobranca().getPajurFavoravelData();
						
						text = trocaValoresXWPF(text, r, "pajuDia", pajuGerado.getDate());
						text = trocaValoresXWPF(text, r, "pajuMes", CommonsUtil.formataMesExtenso(pajuGerado).toLowerCase());
						text = trocaValoresXWPF(text, r, "pajuAno", (pajuGerado.getYear() + 1900));
					}
			    }
			}
		    
		    for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
					            String text = r.getText(0);					            
					            if(CommonsUtil.semValor(text)) {
					            	continue;
					            }				         
							}
						}
					}
				}
			}
		   
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
			String nomeSemvirgula = this.objetoCcb.getNomeEmitente();
			if(nomeSemvirgula.contains(",")) {
				nomeSemvirgula = nomeSemvirgula.replace(",", "");
		    }
			gerador.open(String.format("Galleria Bank - Termo De Responsabilidade Anuencia Paju%s.docx", ""));
			gerador.feed(new ByteArrayInputStream(out.toByteArray()));
			gerador.close();
			criarCcbNosistema();	
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public StreamedContent geraFichaPPE() throws IOException{
		try {
			InputStream in = getClass().getResourceAsStream("/resource/Ficha PPE.pdf");
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
			gerador.open(String.format("Galleria Bank - Ficha PPE %s.pdf", ""));
			gerador.feed(in);
			gerador.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public StreamedContent geraFichaPLDeFT() throws IOException{
		try {
			InputStream in = getClass().getResourceAsStream("/resource/Ficha PLD e FT.pdf");
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
			gerador.open(String.format("Galleria Bank - Ficha PPE %s.pdf", ""));
			gerador.feed(in);
			gerador.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public StreamedContent geraFichaCadastro(PagadorRecebedor pagador) throws IOException{
		//PagadorRecebedorDao pDao = new PagadorRecebedorDao();
		//PagadorRecebedor pagador = new PagadorRecebedor();
		//pagador = pDao.findById((long)27193); 
		ImpressoesPDFMB impressaoMb = new ImpressoesPDFMB();
		
		return impressaoMb.geraPdfCadastroPagadorRecebedor(pagador);
	}
	
	@SuppressWarnings("resource")
	public StreamedContent readXWPFile() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();	
	    try {
	    	String tipoDownload = this.getTipoDownload();
	    	
	    	if (CommonsUtil.mesmoValor(tipoDownload,"CCB")) {
	    		
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"AF")) {
	    		
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"NC")) {
	    		
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"Excel")) {
	    		
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"teste")){
	    		
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"CCBnova")){
	    		
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"AFnova")){
	    		
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"NCnova")){
	    		
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"CartaSplit")){
	    		clearDocumentosNovos();
	    		return geraCartaSplitDinamica();
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"AnexoII")){
	    		clearDocumentosNovos();
	    		return geraAnexoII();
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"CCI")) {
		    	return geraCci();
		    } else if(CommonsUtil.mesmoValor(tipoDownload,"Cessao")) {
		    	return geraCessao();
		    } else if(CommonsUtil.mesmoValor(tipoDownload,"InstrumentoEmissaoCCI")) {
		    	return geraInstrumentoEmissaoCCI();
		    } else if(CommonsUtil.mesmoValor(tipoDownload,"EndossosEmPretoGalleria")) {
		    	return geraEndossosEmPretoGalleria();
		    } else if(CommonsUtil.mesmoValor(tipoDownload,"AquisicaoCCI")) {
		    	return geraCciAquisicao();
		    } else if(CommonsUtil.mesmoValor(tipoDownload,"FinanciamentoCCI")) {
		    	clearDocumentosNovos();
		    	return geraCciFinanciamento();
		    } else if(CommonsUtil.mesmoValor(tipoDownload,"Ficha PPE")) {
		    	return geraFichaPPE();
		    } else if(CommonsUtil.mesmoValor(tipoDownload,"Ficha PLD e FT")) {
		    	return geraFichaPLDeFT();		    	
		    } else if(CommonsUtil.mesmoValor(tipoDownload,"DeclaracaoNaoUniaoEstavel")) {
		    	for(CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
		    		if(!participante.isEmpresa() && !participante.isUniaoEstavel()) {
		    			geraDeclaracaoNaoUniaoEstavel(participante);
		    		}
		    		return null;
		    	}
		    } else if(CommonsUtil.mesmoValor(tipoDownload,"DeclaracaoDestinacaoRecursos")) {
		    	for(CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
		    		if(!participante.isEmpresa() && !participante.isUniaoEstavel()) {
		    			geraDeclaracaoDestinacaoRecursos(participante);
		    		}
		    		return null;
		    	}
		    } else if(CommonsUtil.mesmoValor(tipoDownload,"DeclaracaoUniaoEstavel")) {
		    	for(CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
		    		if(!participante.isEmpresa() && participante.isUniaoEstavel()) {
		    			geraDeclaracaoUniaoEstavel(participante);
		    		}
		    		return null;
		    	}		    	
		    } else if(CommonsUtil.mesmoValor(tipoDownload,"TermoPaju")) {
		    	for(CcbParticipantes participante : objetoCcb.getListaParticipantes()) {
		    		geraTermoResponsabilidadeAnuenciaPaju(participante);
		    	}
		    	return null;
		    } else {
	    		
	    	}
   	
	    } catch (Exception e) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Contrato de Cobrança: Ocorreu um problema ao gerar o documento!  " + e + ";" + e.getCause(),
							""));
	    }  
	    return null;
	}
		
	public String estadoPorExtenso(String uf) {
		if(!CommonsUtil.semValor(uf)) {
			if(CommonsUtil.mesmoValor(uf, "AC")) {
				return "Acre";
			} else if(CommonsUtil.mesmoValor(uf, "AL")) {
				return "Alagoas";
			} else if(CommonsUtil.mesmoValor(uf, "AP")) {
				return "Amapá";
			} else if(CommonsUtil.mesmoValor(uf, "AM")) {
				return "Amazonas";
			} else if(CommonsUtil.mesmoValor(uf, "BA")) {
				return "Bahia";
			} else if(CommonsUtil.mesmoValor(uf, "CE")) {
				return "Ceará";
			} else if(CommonsUtil.mesmoValor(uf, "DF")) {
				return "Distrito Federal";
			} else if(CommonsUtil.mesmoValor(uf, "ES")) {
				return "Espírito Santo";
			} else if(CommonsUtil.mesmoValor(uf, "GO")) {
				return "Goiás";
			} else if(CommonsUtil.mesmoValor(uf, "MA")) {
				return "Maranhão";
			} else if(CommonsUtil.mesmoValor(uf, "MT")) {
				return "Mato Grosso";
			} else if(CommonsUtil.mesmoValor(uf, "MS")) {
				return "Mato Grosso";
			} else if(CommonsUtil.mesmoValor(uf, "MG")) {
				return "Minas Gerais";
			} else if(CommonsUtil.mesmoValor(uf, "PA")) {
				return "Pará";
			} else if(CommonsUtil.mesmoValor(uf, "PB")) {
				return "Paraíba";
			} else if(CommonsUtil.mesmoValor(uf, "PR")) {
				return "Paraná";
			} else if(CommonsUtil.mesmoValor(uf, "PE")) {
				return "Pernambuco";
			} else if(CommonsUtil.mesmoValor(uf, "PI")) {
				return "Piauí";
			} else if(CommonsUtil.mesmoValor(uf, "RJ")) {
				return "Rio de Janeiro";
			} else if(CommonsUtil.mesmoValor(uf, "RN")) {
				return "Rio Grande do Norte";
			} else if(CommonsUtil.mesmoValor(uf, "RS")) {
				return "Rio Grande do Sul";
			} else if(CommonsUtil.mesmoValor(uf, "RO")) {
				return "Rondônia";
			} else if(CommonsUtil.mesmoValor(uf, "RR")) {
				return "Roraima";
			} else if(CommonsUtil.mesmoValor(uf, "SC")) {
				return "Santa Catarina";
			} else if(CommonsUtil.mesmoValor(uf, "SP")) {
				return "São Paulo";
			} else if(CommonsUtil.mesmoValor(uf, "SE")) {
				return "Sergipe";
			} else if(CommonsUtil.mesmoValor(uf, "TO")) {
				return "Tocantins";
			} else {
				return uf;
			}
		}
		return "";
	}

	public void clearPagadorRecebedor() {
		this.participanteSelecionado = new CcbParticipantes();
		this.emitenteSelecionado = new PagadorRecebedor();
		this.selectedPagador = new PagadorRecebedor();
		this.intervenienteSelecionado = new PagadorRecebedor();
		this.terceiroGSelecionado = new PagadorRecebedor();
		this.avalistaSelecionado = new PagadorRecebedor();
	}
	
	public void clearDespesas() {
		despesaSelecionada = new ContasPagar();
		processoSelecionado = new CcbProcessosJudiciais();
		if(!CommonsUtil.semValor(this.objetoCcb.getCustasCartorariasValor())) {
			setTemCustasCartorarias(true);
		} else {
			setTemCustasCartorarias(false);
		}
		if(!CommonsUtil.semValor(this.objetoCcb.getCertidaoDeCasamentoValor())) {
			setTemCertidaoDeCasamento(true);
		} else {
			setTemCertidaoDeCasamento(false);
		}
		if(!CommonsUtil.semValor(this.objetoCcb.getLaudoDeAvaliacaoValor())) {
			setTemLaudoDeAvaliacao(true);
		} else {
			setTemLaudoDeAvaliacao(false);
		}
		if(!CommonsUtil.semValor(this.objetoCcb.getIntermediacaoValor())) {
			setTemIntermediacao(true);
		} else {
			setTemIntermediacao(false);
		}
		if(!CommonsUtil.semValor(this.objetoCcb.getIptuEmAtrasoValor())) {
			setTemIptuEmAtraso(true);
		} else {
			setTemIptuEmAtraso(false);
		}
		if(!CommonsUtil.semValor(this.objetoCcb.getCondominioEmAtrasoValor())) {
			setTemCondominioEmAtraso(true);
		} else {
			setTemCondominioEmAtraso(false);
		}
	}

	public void calculaValorLiquidoCredito() {
		BigDecimal valor = BigDecimal.ZERO;
		if(!CommonsUtil.semValor(this.objetoCcb.getValorCredito())) {
			valor = this.objetoCcb.getValorCredito();
		}
		if(!CommonsUtil.semValor(this.objetoCcb.getCustoEmissao())) {
			valor = valor.subtract(this.objetoCcb.getCustoEmissao());
		}
		if(!CommonsUtil.semValor(this.objetoCcb.getValorIOF())) {
			valor = valor.subtract(this.objetoCcb.getValorIOF());
		}
		if(!CommonsUtil.semValor(this.objetoCcb.getValorDespesas())) {
			valor = valor.subtract(this.objetoCcb.getValorDespesas());
		}
		this.objetoCcb.setValorLiquidoCredito(valor);
	}
	
	public void calculaPorcentagemImovel() {
		if (!CommonsUtil.semValor(this.objetoCcb.getValorCredito()) && this.objetoCcb.getVendaLeilao() != null) {
			this.objetoCcb.setPorcentagemImovel(((this.objetoCcb.getVendaLeilao().divide(this.objetoCcb.getValorCredito(), MathContext.DECIMAL128)).multiply(BigDecimal.valueOf(100))).setScale(2, BigDecimal.ROUND_HALF_UP));	
		}

	}
	
	public void calcularSimulador() {
		populateDadosEmitente();
		this.simulador = new SimulacaoVO();	
		BigDecimal tarifaIOFDiario = BigDecimal.ZERO;
		BigDecimal tarifaIOFAdicional = SiscoatConstants.TARIFA_IOF_ADICIONAL.divide(BigDecimal.valueOf(100));
		simulador.setTipoPessoa(this.objetoCcb.getTipoPessoaEmitente());
		
		BigDecimal custoEmissaoValor = SiscoatConstants.CUSTO_EMISSAO_MINIMO;
		
		final BigDecimal custoEmissaoPercentual;
		if (objetoCcb.isUsarNovoCustoEmissao()) {
			custoEmissaoPercentual = SiscoatConstants.CUSTO_EMISSAO_PERCENTUAL_BRUTO_NOVO;
		} else {
			custoEmissaoPercentual = SiscoatConstants.CUSTO_EMISSAO_PERCENTUAL_BRUTO;
		}

		if (objetoCcb.getValorCredito().multiply(custoEmissaoPercentual.divide(BigDecimal.valueOf(100)))
				.compareTo(SiscoatConstants.CUSTO_EMISSAO_MINIMO) > 0) {
			custoEmissaoValor = objetoCcb.getValorCredito().multiply(custoEmissaoPercentual.divide(BigDecimal.valueOf(100)));
		}
		
		if (CommonsUtil.mesmoValor(this.objetoCcb.getTipoPessoaEmitente(), "PF")) {		
			tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PF.divide(BigDecimal.valueOf(100));		
		} else {		
			tarifaIOFDiario = SiscoatConstants.TARIFA_IOF_PJ.divide(BigDecimal.valueOf(100));		
		}
		
		if(!CommonsUtil.semValor(this.objetoCcb.getPrazo()) && !CommonsUtil.semValor(carencia)) {
			this.objetoCcb.setNumeroParcelasPagamento(CommonsUtil.stringValue(Long.parseLong(this.objetoCcb.getPrazo()) - Long.parseLong(carencia)));
		}
		simulador.setDataSimulacao(this.objetoCcb.getDataDeEmissao());
		simulador.setTarifaIOFDiario(tarifaIOFDiario);
		simulador.setTarifaIOFAdicional(tarifaIOFAdicional);
		simulador.setSeguroMIP(SiscoatConstants.SEGURO_MIP);
		simulador.setSeguroDFI(SiscoatConstants.SEGURO_DFI);
		simulador.setValorCredito(this.objetoCcb.getValorCredito());
		simulador.setTaxaJuros(this.objetoCcb.getTaxaDeJurosMes());
		simulador.setCarencia(BigInteger.valueOf( Long.parseLong(this.objetoCcb.getPrazo()) - Long.parseLong(this.objetoCcb.getNumeroParcelasPagamento())));
		simulador.setQtdParcelas(BigInteger.valueOf(Long.parseLong(this.objetoCcb.getPrazo())));
		simulador.setValorImovel(this.objetoCcb.getVlrImovel());
		simulador.setCustoEmissaoValor(custoEmissaoValor);
		simulador.setCustoEmissaoPercentual(custoEmissaoPercentual);
		simulador.setTipoCalculo(this.objetoCcb.getSistemaAmortizacao());
		simulador.setNaoCalcularDFI(false);
		simulador.setNaoCalcularMIP(false);
		simulador.setNaoCalcularTxAdm(false);
		if (CommonsUtil.mesmoValor('L', this.objetoCcb.getTipoCalculoFinal())) {
			GoalSeek goalSeek = new GoalSeek(CommonsUtil.doubleValue(simulador.getValorCredito()), 
					CommonsUtil.doubleValue(simulador.getValorCredito().divide(BigDecimal.valueOf(1.5), MathContext.DECIMAL128)),
					CommonsUtil.doubleValue(simulador.getValorCredito().multiply(BigDecimal.valueOf(1.5), MathContext.DECIMAL128)));		
			GoalSeekFunction gsFunfction = new GoalSeekFunction();
			BigDecimal valorBruto = CommonsUtil.bigDecimalValue(gsFunfction.getGoalSeek(goalSeek, simulador));
			simulador.setValorCredito(valorBruto.setScale(2, RoundingMode.HALF_UP));
		} else {
			simulador.calcular();
		}
		simulador.calcularValorLiberado();
		
		BigDecimal jurosAoAno = BigDecimal.ZERO;
		jurosAoAno = BigDecimal.ONE.add((this.objetoCcb.getTaxaDeJurosMes().divide(BigDecimal.valueOf(100), MathContext.DECIMAL128)));
		jurosAoAno = CommonsUtil.bigDecimalValue(Math.pow(CommonsUtil.doubleValue(jurosAoAno), 12));
		jurosAoAno = jurosAoAno.subtract(BigDecimal.ONE);
		jurosAoAno = jurosAoAno.multiply(BigDecimal.valueOf(100), MathContext.DECIMAL128);
		jurosAoAno = jurosAoAno.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		this.simulador.setTaxaJurosAoAno(jurosAoAno);
		
		if (simulador.getParcelas().size() > 0 ) {
			BigDecimal cet = BigDecimal.ZERO;
			BigDecimal cetAno = BigDecimal.ZERO;
			double cetDouble = 0.0;
			
			double[] cash_flows = new double[simulador.getQtdParcelas().intValue() + 1];
			
			cash_flows[0] = simulador.getValorCreditoLiberado().negate().doubleValue();
			
			for (int i = 1; i <= simulador.getQtdParcelas().intValue(); i++) {
				BigDecimal calc_value = simulador.getParcelas().get(i).getAmortizacao().add(simulador.getParcelas().get(i).getJuros());
				cash_flows[i] = calc_value.doubleValue();
			}
			
			
			int maxGuess = 500;
			cetDouble = SimuladorMB.irr(cash_flows, maxGuess);
			
			if (CommonsUtil.mesmoValor(CommonsUtil.stringValue(cetDouble), "NaN")) {
				cetDouble = 0;
			} 
			
			cetDouble = cetDouble * 100; 
			cet = CommonsUtil.bigDecimalValue(cetDouble);	
			cetAno = BigDecimal.ONE.add((cet.divide(BigDecimal.valueOf(100), MathContext.DECIMAL128)));
			cetAno = CommonsUtil.bigDecimalValue(Math.pow(CommonsUtil.doubleValue(cetAno), 12));
			cetAno = cetAno.subtract(BigDecimal.ONE);
			cetAno = cetAno.multiply(BigDecimal.valueOf(100), MathContext.DECIMAL128);
			
			cetAno = cetAno.setScale(2, BigDecimal.ROUND_HALF_UP);
			cet = cet.setScale(2, BigDecimal.ROUND_HALF_UP);
			
			this.simulador.setCetAoAno(cetAno);
			this.simulador.setCetAoMes(cet);
		}
		
		int numeroUltimaParcela = simulador.getParcelas().get(simulador.getParcelas().size() - 1).getNumeroParcela().intValue();
		Date dataUltimaParcela = DateUtil.adicionarPeriodo(simulador.getDataSimulacao(), numeroUltimaParcela, Calendar.MONTH);
		
		this.objetoCcb.setVencimentoUltimaParcelaPagamento(dataUltimaParcela);
		this.objetoCcb.setVencimentoUltimaParcelaDFI(dataUltimaParcela);
		this.objetoCcb.setVencimentoUltimaParcelaMIP(dataUltimaParcela);
		this.objetoCcb.setValorIOF(simulador.getIOFTotal().setScale(2, BigDecimal.ROUND_HALF_UP));
		this.objetoCcb.setCustoEmissao(simulador.getCustoEmissaoValor().setScale(2, BigDecimal.ROUND_HALF_UP));
		this.objetoCcb.setTaxaDeJurosAno(simulador.getTaxaJurosAoAno());
		this.objetoCcb.setCetMes(simulador.getCetAoMes());
		this.objetoCcb.setCetAno(simulador.getCetAoAno());
		
		BigDecimal montante = BigDecimal.ZERO;
		BigDecimal montanteDfi = BigDecimal.ZERO;
		BigDecimal montanteMip = BigDecimal.ZERO;
		
		BigDecimal vlrPrimeiraParcela = BigDecimal.ZERO;
		BigDecimal vlrPrimeiraDfi = BigDecimal.ZERO;
		BigDecimal vlrPrimeiraMip = BigDecimal.ZERO;
		
		int numeroPrimeiraParcela = 0;
		Date dataPrimeiraParcela;
		
		for(SimulacaoDetalheVO parcela : simulador.getParcelas()) {
			if(!CommonsUtil.semValor(parcela.getAmortizacao().add(parcela.getJuros()))) {
				if(CommonsUtil.semValor(vlrPrimeiraParcela)) {
					vlrPrimeiraParcela = parcela.getAmortizacao().add(parcela.getJuros());
					numeroPrimeiraParcela = parcela.getNumeroParcela().intValue();
				}
			}		
			if(!CommonsUtil.semValor(parcela.getSeguroDFI())) {
				if(CommonsUtil.semValor(vlrPrimeiraDfi)) {
					vlrPrimeiraDfi = parcela.getSeguroDFI();
				}
			}			
			if(!CommonsUtil.semValor(parcela.getSeguroDFI())) {
				if(CommonsUtil.semValor(vlrPrimeiraMip)) {
					vlrPrimeiraMip = parcela.getSeguroMIP();
				}
			}

			montante = montante.add(parcela.getAmortizacao().add(parcela.getJuros()));
			montanteDfi = montanteDfi.add(parcela.getSeguroDFI());
			montanteMip = montanteMip.add(parcela.getSeguroMIP());
		}
		
		dataPrimeiraParcela = DateUtil.adicionarPeriodo(simulador.getDataSimulacao(), numeroPrimeiraParcela, Calendar.MONTH);
		this.objetoCcb.setVencimentoPrimeiraParcelaPagamento(dataPrimeiraParcela);
		this.objetoCcb.setVencimentoPrimeiraParcelaDFI(dataPrimeiraParcela);
		this.objetoCcb.setVencimentoPrimeiraParcelaMIP(dataPrimeiraParcela);
		
		this.objetoCcb.setMontanteMIP(montanteMip.setScale(2, BigDecimal.ROUND_HALF_UP));
		this.objetoCcb.setMontanteDFI(montanteDfi.setScale(2, BigDecimal.ROUND_HALF_UP));
		this.objetoCcb.setMontantePagamento(montante.setScale(2, BigDecimal.ROUND_HALF_UP));
		
		this.objetoCcb.setValorMipParcela(vlrPrimeiraMip.setScale(2, BigDecimal.ROUND_HALF_UP));
		this.objetoCcb.setValorDfiParcela(vlrPrimeiraDfi.setScale(2, BigDecimal.ROUND_HALF_UP));
		this.objetoCcb.setValorParcela(vlrPrimeiraParcela.setScale(2, BigDecimal.ROUND_HALF_UP));
		
		populateParcelaSeguro();
		if(CommonsUtil.mesmoValor(this.objetoCcb.getTipoCalculoFinal(),'L')) {
			this.objetoCcb.setValorCredito(simulador.getValorCredito().setScale(2, BigDecimal.ROUND_HALF_UP));
			this.objetoCcb.setTipoCalculoFinal('B');
		} 
		calculaValorLiquidoCredito();
		
		calculaPorcentagemImovel();
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Percelas Geradas com sucesso", ""));	
	}
		
	public String clearFieldsConsultarCcb() {
		CcbDao ccbDao = new CcbDao();
		listaCcbs = ccbDao.ConsultaCCBs();
		
		return "/Atendimento/Cobranca/CcbConsultar.xhtml";
	}
	
	public String clearFieldsEditarCcb() {
		loadLovs();	
		clearPagadorRecebedor();
		clearDespesas();
		this.simulador = new SimulacaoVO();
		//this.seguradoSelecionado = new Segurado();
		//this.seguradoSelecionado.setPessoa(new PagadorRecebedor());
		this.addSegurador = false;
		CcbDao ccbDao = new CcbDao();
		this.objetoCcb = ccbDao.findById(objetoCcb.getId());
		
		if(!CommonsUtil.semValor(objetoCcb.getPrazo()) && !CommonsUtil.semValor(objetoCcb.getNumeroParcelasPagamento())){
			this.carencia = CommonsUtil.stringValue(CommonsUtil.integerValue(objetoCcb.getPrazo())
					- CommonsUtil.integerValue(objetoCcb.getNumeroParcelasPagamento()));
		}
		
		clearAnexoII();
		mostrarDadosOcultos = false;
		aviso = "";
		return "/Atendimento/Cobranca/Ccb.xhtml";
		
	}
	
	public void clearAnexoII(){
		if(!CommonsUtil.semValor(this.objetoCcb.getCertidaoDeCasamentoValor())) {
			this.temCertidaoDeCasamento = true;
		}
		
		if(!CommonsUtil.semValor(this.objetoCcb.getCustasCartorariasValor())) {
			this.temCustasCartorarias = true;
		}
		
		if(!CommonsUtil.semValor(this.objetoCcb.getCertidaoDeCasamentoValor())) {
			this.temCertidaoDeCasamento = true;
		}
		
		if(!CommonsUtil.semValor(this.objetoCcb.getLaudoDeAvaliacaoValor())) {
			this.temLaudoDeAvaliacao = true;
		}
		
		if(!CommonsUtil.semValor(this.objetoCcb.getIntermediacaoValor())) {
			this.temIntermediacao = true;
		}

		if(!CommonsUtil.semValor(this.objetoCcb.getProcessosJucidiais().size())) {
			this.temProcessosJucidiais = true;
		}
		
		if(!CommonsUtil.semValor(this.objetoCcb.getIptuEmAtrasoValor())) {
			this.temIptuEmAtraso = true;
		}
		
		if(!CommonsUtil.semValor(this.objetoCcb.getCondominioEmAtrasoValor())) {
			this.temCondominioEmAtraso = true;
		}
		
		if(!CommonsUtil.semValor(this.objetoCcb.getIqValor())) {
			this.temIq = true;
		}
		
		if(!CommonsUtil.semValor(this.objetoCcb.getCCBValor())) {
			this.temCCBValor = true;
		}
		
		if(!CommonsUtil.semValor(this.objetoCcb.getItbiValor())) {
			this.temItbi = true;
		}
	}
	
	public String clearFieldsInserirCcb() {
		loadLovs();	
		clearDespesas();
		this.addSegurador = false;
		this.objetoCcb = new CcbContrato();
		this.objetoCcb.setListaParticipantes(new ArrayList<CcbParticipantes>());
		this.participanteSelecionado = new CcbParticipantes();
		this.participanteSelecionado.setPessoa(new PagadorRecebedor());
		this.intervenienteSelecionado = new PagadorRecebedor();
		this.emitenteSelecionado = new PagadorRecebedor();
		this.selectedPagador = new PagadorRecebedor();
		this.objetoCcb.setDataDeEmissao(DateUtil.gerarDataHoje());
		this.numeroContrato = null;
		this.nomeEmitente = null;
		this.nacionalidadeEmitente = "brasileiro";
		this.profissaoEmitente = null;
		this.estadoCivilEmitente = null;
		this.numeroRgEmitente = null;
		this.ufEmitente = null;
		this.cpfEmitente = null;
		this.logradouroEmitente = null;
		this.numeroEmitente = null;
		this.complementoEmitente = null;
		this.cidadeEmitente = null;
		this.cepEmitente = null;
		this.emailEmitente = null;
		this.regimeCasamentoEmitente = null;
		this.fiduciante = false;
		this.femininoEmitente = false;
		this.empresaEmitente = false;
		this.paiEmitente = null;
		this.maeEmitente = null;
		
		this.razaoSocialEmitente = null;
		this.tipoEmpresaEmitente = null;
		this.cnpjEmitente = null;
		this.municipioEmpresaEmitente = null;
		this.estadoEmpresaEmitente = null;
		this.ruaEmpresaEmitente = null;
		this.numeroEmpresaEmitente = null;
		this.salaEmpresaEmitente = null;
		this.bairroEmpresaEmitente = null;
		this.cepEmpresaEmitente = null;

		this.addTerceiro = false;
		this.selectedPagadorGenerico = null;
		this.selectedPagador = null;
		this.emitenteSelecionado = null;
		this.intervenienteSelecionado = null;
		this.terceiroGSelecionado = null;
		this.avalistaSelecionado = null;
		this.testemunha1Selecionado = null;
		this.testemunha2Selecionado = null;
		
		this.uploadedFile = null;
	    this.fileName = null;
	    this.fileType = null;
	    this.fileTypeInt = 0;
	    
	    mostrarDadosOcultos = false;
	    
	    aviso = "";
	    
	    this.simulador = new SimulacaoVO();
	    
	    clearPagadorRecebedor();
	    
	    CcbDao ccbDao = new CcbDao();
	    int serie = CommonsUtil.intValue(ccbDao.ultimaSerieCCB()) + 1;
	    objetoCcb.setSerieCcb(CommonsUtil.stringValue(serie));
		
		return "/Atendimento/Cobranca/Ccb.xhtml";
	}
	
	public void loadLovs() {
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listPagadores = pagadorRecebedorDao.getPagadoresRecebedores();
		
		filesList = new ArrayList<UploadedFile>();
		/*for(FileUploaded file : listaArquivos()) {
			//filesList.add((UploadedFile) file.file);
	    }*/
	}
	
	public void getEnderecoByViaNet() {
		try {
			String inputCep = this.participanteSelecionado.getPessoa().getCep().replace("-", "");
			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("http://viacep.com.br/ws/" + inputCep + "/json/");

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);

			JSONObject myResponse = null;

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				
			} else {
				myResponse = getJsonSucesso(myURLConnection.getInputStream());
				if(myResponse.has("logradouro")) {
					this.participanteSelecionado.getPessoa().setEndereco(myResponse.get("logradouro").toString());
				}
				if(myResponse.has("bairro")) {
					this.participanteSelecionado.getPessoa().setBairro(myResponse.get("bairro").toString());
				}				
				if(myResponse.has("localidade")) {
					this.participanteSelecionado.getPessoa().setCidade(myResponse.get("localidade").toString());
				}		
				if(myResponse.has("uf")) {
					this.participanteSelecionado.getPessoa().setEstado(myResponse.get("uf").toString());
				}
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
	
	public void getEnderecoByViaNetSocio() {
		try {
			String inputCep = this.socioSelecionado.getPessoa().getCep().replace("-", "");

			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("http://viacep.com.br/ws/" + inputCep + "/json/");

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);

			JSONObject myResponse = null;

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {
				
			} else {
				myResponse = getJsonSucesso(myURLConnection.getInputStream());
				if(myResponse.has("logradouro")) {
					this.socioSelecionado.getPessoa().setEndereco(myResponse.get("logradouro").toString());
				}
				if(myResponse.has("bairro")) {
					this.socioSelecionado.getPessoa().setBairro(myResponse.get("bairro").toString());
				}				
				if(myResponse.has("localidade")) {
					this.socioSelecionado.getPessoa().setCidade(myResponse.get("localidade").toString());
				}			
				if(myResponse.has("uf")) {
					this.socioSelecionado.getPessoa().setEstado(myResponse.get("uf").toString());
				}				
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
			String inputCep = this.objetoCcb.getCepImovel().replace("-", "");
			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("http://viacep.com.br/ws/" + inputCep + "/json/");

			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);
			
			JSONObject myResponse = null;

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {

			} else {
				myResponse = getJsonSucesso(myURLConnection.getInputStream());
				if(myResponse.has("logradouro")) {
					this.objetoCcb.setLogradouroRuaImovel(myResponse.get("logradouro").toString());
				}
				if(myResponse.has("bairro")) {
					this.objetoCcb.setBairroImovel(myResponse.get("bairro").toString());
				}				
				if(myResponse.has("localidade")) {
					this.objetoCcb.setCidadeImovel(myResponse.get("localidade").toString());
				}				
				if(myResponse.has("uf")) {
					this.objetoCcb.setUfImovel(myResponse.get("uf").toString());
				}
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
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return null;
	}
	
	/////////////////////////////////////			
	public String getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

	public String getNomeEmitente() {
		return nomeEmitente;
	}
	
	public void setNomeEmitente(String nomeEmitente) {
		this.nomeEmitente = nomeEmitente;
	}

	public String getNacionalidadeEmitente() {
		return nacionalidadeEmitente;
	}

	public void setNacionalidadeEmitente(String nacionalidadeEmitente) {
		this.nacionalidadeEmitente = nacionalidadeEmitente;
	}

	public String getProfissaoEmitente() {
		return profissaoEmitente;
	}

	public void setProfissaoEmitente(String profissaoEmitente) {
		this.profissaoEmitente = profissaoEmitente;
	}

	public String getEstadoCivilEmitente() {
		return estadoCivilEmitente;
	}

	public void setEstadoCivilEmitente(String estadoCivilEmitente) {
		this.estadoCivilEmitente = estadoCivilEmitente;
	}

	public String getNomeConjugeEmitente() {
		return nomeConjugeEmitente;
	}

	public void setNomeConjugeEmitente(String nomeConjugeEmitente) {
		this.nomeConjugeEmitente = nomeConjugeEmitente;
	}

	public PagadorRecebedor getSelectedPagadorGenerico() {
		return selectedPagadorGenerico;
	}

	public void setSelectedPagadorGenerico(PagadorRecebedor selectedPagadorGenerico) {
		this.selectedPagadorGenerico = selectedPagadorGenerico;
	}

	public PagadorRecebedor getSelectedPagador() {
		return selectedPagador;
	}

	public void setSelectedPagador(PagadorRecebedor selectedPagador) {
		this.selectedPagador = selectedPagador;
	}

	public PagadorRecebedor getEmitenteSelecionado() {
		return emitenteSelecionado;
	}

	public void setEmitenteSelecionado(PagadorRecebedor emitenteSelecionado) {
		this.emitenteSelecionado = emitenteSelecionado;
	}

	public List<PagadorRecebedor> getListPagadores() {
		return listPagadores;
	}

	public void setListPagadores(List<PagadorRecebedor> listPagadores) {
		this.listPagadores = listPagadores;
	}

	public List<PagadorRecebedor> getListRecebedorPagador() {
		return listPagadores;
	}

	public String getUpdatePagadorRecebedor() {
		return updatePagadorRecebedor;
	}

	public void setUpdatePagadorRecebedor(String updatePagadorRecebedor) {
		this.updatePagadorRecebedor = updatePagadorRecebedor;
	}

	public String getNumeroRgEmitente() {
		return numeroRgEmitente;
	}

	public void setNumeroRgEmitente(String numeroRgEmitente) {
		this.numeroRgEmitente = numeroRgEmitente;
	}

	public String getUfEmitente() {
		return ufEmitente;
	}

	public void setUfEmitente(String ufEmitente) {
		this.ufEmitente = ufEmitente;
	}

	public String getCpfEmitente() {
		return cpfEmitente;
	}

	public void setCpfEmitente(String cpfEmitente) {
		this.cpfEmitente = cpfEmitente;
	}

	public String getLogradouroEmitente() {
		return logradouroEmitente;
	}

	public void setLogradouroEmitente(String logradouroEmitente) {
		this.logradouroEmitente = logradouroEmitente;
	}

	public String getNumeroEmitente() {
		return numeroEmitente;
	}

	public void setNumeroEmitente(String numeroEmitente) {
		this.numeroEmitente = numeroEmitente;
	}

	public String getComplementoEmitente() {
		return complementoEmitente;
	}

	public void setComplementoEmitente(String complementoEmitente) {
		this.complementoEmitente = complementoEmitente;
	}

	public String getCidadeEmitente() {
		return cidadeEmitente;
	}

	public void setCidadeEmitente(String cidadeEmitente) {
		this.cidadeEmitente = cidadeEmitente;
	}

	public String getCepEmitente() {
		return cepEmitente;
	}

	public void setCepEmitente(String cepEmitente) {
		this.cepEmitente = cepEmitente;
	}

	public String getCpfConjugeEmitente() {
		return cpfConjugeEmitente;
	}

	public void setCpfConjugeEmitente(String cpfConjugeEmitente) {
		this.cpfConjugeEmitente = cpfConjugeEmitente;
	}
	
	public PagadorRecebedor getIntervenienteSelecionado() {
		return intervenienteSelecionado;
	}

	public void setIntervenienteSelecionado(PagadorRecebedor intervenienteSelecionado) {
		this.intervenienteSelecionado = intervenienteSelecionado;
	}

	public String getTipoPesquisa() {
		return tipoPesquisa;
	}

	public void setTipoPesquisa(String tipoPesquisa) {
		this.tipoPesquisa = tipoPesquisa;
	}

	public String getTipoDownload() {
		return tipoDownload;
	}

	public void setTipoDownload(String tipoDownload) {
		this.tipoDownload = tipoDownload;
	}

	public String getEmailEmitente() {
		return emailEmitente;
	}

	public void setEmailEmitente(String emailEmitente) {
		this.emailEmitente = emailEmitente;
	}

	public String getRegimeCasamentoEmitente() {
		return regimeCasamentoEmitente;
	}

	public void setRegimeCasamentoEmitente(String regimeCasamentoEmitente) {
		this.regimeCasamentoEmitente = regimeCasamentoEmitente;
	}
	

	public boolean isAddTerceiro() {
		return addTerceiro;
	}

	public void setAddTerceiro(boolean addTerceiro) {
		this.addTerceiro = addTerceiro;
	}

	public boolean isFiduciante() {
		return fiduciante;
	}

	public void setFiduciante(boolean fiduciante) {
		this.fiduciante = fiduciante;
	}

	public boolean isFemininoEmitente() {
		return femininoEmitente;
	}

	public void setFemininoEmitente(boolean femininoEmitente) {
		this.femininoEmitente = femininoEmitente;
	}

	public PagadorRecebedor getTerceiroGSelecionado() {
		return terceiroGSelecionado;
	}

	public void setTerceiroGSelecionado(PagadorRecebedor terceiroGSelecionado) {
		this.terceiroGSelecionado = terceiroGSelecionado;
	}

	public PagadorRecebedor getAvalistaSelecionado() {
		return avalistaSelecionado;
	}

	public void setAvalistaSelecionado(PagadorRecebedor avalistaSelecionado) {
		this.avalistaSelecionado = avalistaSelecionado;
	}

	public PagadorRecebedor getTestemunha1Selecionado() {
		return testemunha1Selecionado;
	}

	public void setTestemunha1Selecionado(PagadorRecebedor testemunha1Selecionado) {
		this.testemunha1Selecionado = testemunha1Selecionado;
	}

	public PagadorRecebedor getTestemunha2Selecionado() {
		return testemunha2Selecionado;
	}

	public void setTestemunha2Selecionado(PagadorRecebedor testemunha2Selecionado) {
		this.testemunha2Selecionado = testemunha2Selecionado;
	}

	public boolean isEmpresaEmitente() {
		return empresaEmitente;
	}

	public void setEmpresaEmitente(boolean empresaEmitente) {
		this.empresaEmitente = empresaEmitente;
	}

	public String getRazaoSocialEmitente() {
		return razaoSocialEmitente;
	}

	public void setRazaoSocialEmitente(String razaoSocialEmitente) {
		this.razaoSocialEmitente = razaoSocialEmitente;
	}

	public String getTipoEmpresaEmitente() {
		return tipoEmpresaEmitente;
	}

	public void setTipoEmpresaEmitente(String tipoEmpresaEmitente) {
		this.tipoEmpresaEmitente = tipoEmpresaEmitente;
	}

	public String getCnpjEmitente() {
		return cnpjEmitente;
	}

	public void setCnpjEmitente(String cnpjEmitente) {
		this.cnpjEmitente = cnpjEmitente;
	}

	public String getMunicipioEmpresaEmitente() {
		return municipioEmpresaEmitente;
	}

	public void setMunicipioEmpresaEmitente(String municipioEmpresaEmitente) {
		this.municipioEmpresaEmitente = municipioEmpresaEmitente;
	}

	public String getEstadoEmpresaEmitente() {
		return estadoEmpresaEmitente;
	}

	public void setEstadoEmpresaEmitente(String estadoEmpresaEmitente) {
		this.estadoEmpresaEmitente = estadoEmpresaEmitente;
	}

	public String getRuaEmpresaEmitente() {
		return ruaEmpresaEmitente;
	}

	public void setRuaEmpresaEmitente(String ruaEmpresaEmitente) {
		this.ruaEmpresaEmitente = ruaEmpresaEmitente;
	}

	public String getNumeroEmpresaEmitente() {
		return numeroEmpresaEmitente;
	}

	public void setNumeroEmpresaEmitente(String numeroEmpresaEmitente) {
		this.numeroEmpresaEmitente = numeroEmpresaEmitente;
	}

	public String getSalaEmpresaEmitente() {
		return salaEmpresaEmitente;
	}

	public void setSalaEmpresaEmitente(String salaEmpresaEmitente) {
		this.salaEmpresaEmitente = salaEmpresaEmitente;
	}

	public String getBairroEmpresaEmitente() {
		return bairroEmpresaEmitente;
	}

	public void setBairroEmpresaEmitente(String bairroEmpresaEmitente) {
		this.bairroEmpresaEmitente = bairroEmpresaEmitente;
	}

	public String getCepEmpresaEmitente() {
		return cepEmpresaEmitente;
	}

	public void setCepEmpresaEmitente(String cepEmpresaEmitente) {
		this.cepEmpresaEmitente = cepEmpresaEmitente;
	}

	

	public String getPaiEmitente() {
		return paiEmitente;
	}

	public void setPaiEmitente(String paiEmitente) {
		this.paiEmitente = paiEmitente;
	}

	public String getMaeEmitente() {
		return maeEmitente;
	}

	public void setMaeEmitente(String maeEmitente) {
		this.maeEmitente = maeEmitente;
	}

	

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public int getFileTypeInt() {
		return fileTypeInt;
	}

	public void setFileTypeInt(int fileTypeInt) {
		this.fileTypeInt = fileTypeInt;
	}

	public String getTituloPagadorRecebedorDialog() {
		return tituloPagadorRecebedorDialog;
	}

	public void setTituloPagadorRecebedorDialog(String tituloPagadorRecebedorDialog) {
		this.tituloPagadorRecebedorDialog = tituloPagadorRecebedorDialog;
	}

	public ContratoCobranca getObjetoContratoCobranca() {
		return objetoContratoCobranca;
	}

	public void setObjetoContratoCobranca(ContratoCobranca objetoContratoCobranca) {
		this.objetoContratoCobranca = objetoContratoCobranca;
	}

	public ByteArrayInputStream getBis() {
		return bis;
	}

	public void setBis(ByteArrayInputStream bis) {
		this.bis = bis;
	}

	public ArrayList<UploadedFile> getFilesList() {
		return filesList;
	}

	public void setFilesList(ArrayList<UploadedFile> filesList) {
		this.filesList = filesList;
	}

	public CcbParticipantes getParticipanteSelecionado() {
		return participanteSelecionado;
	}

	public void setParticipanteSelecionado(CcbParticipantes participanteSelecionado) {
		this.participanteSelecionado = participanteSelecionado;
	}

	public boolean isAddParticipante() {
		return addParticipante;
	}
	
	public void setAddParticipante(boolean addParticipante) {
		this.addParticipante = addParticipante;
	}

	public CcbParticipantes getSocioSelecionado() {
		return socioSelecionado;
	}

	public void setSocioSelecionado(CcbParticipantes socioSelecionado) {
		this.socioSelecionado = socioSelecionado;
	}

	public boolean isAddSocio() {
		return addSocio;
	}

	public void setAddSocio(boolean addSocio) {
		this.addSocio = addSocio;
	}

	public CcbContrato getObjetoCcb() {
		return objetoCcb;
	}

	public void setObjetoCcb(CcbContrato objetoCcb) {
		this.objetoCcb = objetoCcb;
	}

	public List<CcbContrato> getListaCcbs() {
		return listaCcbs;
	}

	public void setListaCcbs(List<CcbContrato> listaCcbs) {
		this.listaCcbs = listaCcbs;
	}

	public String getNumeroCcb() {
		return numeroCcb;
	}

	public void setNumeroCcb(String numeroCcb) {

		this.numeroCcb = numeroCcb;
	}
	

	public List<ContratoCobranca> getListaContratosConsultar() {
		return listaContratosConsultar;
	}

	
	public void setListaContratosConsultar(List<ContratoCobranca> listaContratosConsultar) {
		this.listaContratosConsultar = listaContratosConsultar;
	}

	public boolean isTemCustasCartorarias() {
		return temCustasCartorarias;
	}

	public void setTemCustasCartorarias(boolean temCustasCartorarias) {
		this.temCustasCartorarias = temCustasCartorarias;
	}

	public boolean isTemCertidaoDeCasamento() {
		return temCertidaoDeCasamento;
	}

	public void setTemCertidaoDeCasamento(boolean temCertidaoDeCasamento) {
		this.temCertidaoDeCasamento = temCertidaoDeCasamento;
	}

	public boolean isTemLaudoDeAvaliacao() {
		return temLaudoDeAvaliacao;
	}

	public void setTemLaudoDeAvaliacao(boolean temLaudoDeAvaliacao) {
		this.temLaudoDeAvaliacao = temLaudoDeAvaliacao;
	}

	public boolean isTemIntermediacao() {
		return temIntermediacao;
	}

	public void setTemIntermediacao(boolean temIntermediacao) {
		this.temIntermediacao = temIntermediacao;
	}

	public boolean isTemProcessosJucidiais() {
		return temProcessosJucidiais;
	}

	public void setTemProcessosJucidiais(boolean temProcessosJucidiais) {
		this.temProcessosJucidiais = temProcessosJucidiais;
	}

	public boolean isTemIptuEmAtraso() {
		return temIptuEmAtraso;
	}

	public void setTemIptuEmAtraso(boolean temIptuEmAtraso) {
		this.temIptuEmAtraso = temIptuEmAtraso;
	}

	public boolean isTemCondominioEmAtraso() {
		return temCondominioEmAtraso;
	}

	public void setTemCondominioEmAtraso(boolean temCondominioEmAtraso) {
		this.temCondominioEmAtraso = temCondominioEmAtraso;
	}

	public BigDecimal getValorProcesso() {
		return valorProcesso;
	}

	public void setValorProcesso(BigDecimal valorProcesso) {
		this.valorProcesso = valorProcesso;
	}

	public boolean isTemIq() {
		return temIq;
	}

	public void setTemIq(boolean temIq) {
		this.temIq = temIq;
	}

	public boolean isAddSegurador() {
		return addSegurador;
	}

	public void setAddSegurador(boolean addSegurador) {
		this.addSegurador = addSegurador;
	}

	public Segurado getSeguradoSelecionado() {
		return seguradoSelecionado;
	}

	public void setSeguradoSelecionado(Segurado seguradoSelecionado) {
		this.seguradoSelecionado = seguradoSelecionado;
	}

	
	public String getNumeroProcesso() {
		return numeroProcesso;
	}


	public void setNumeroProcesso(String numeroProcesso) {
		this.numeroProcesso = numeroProcesso;
	}

	public boolean isTemCCBValor() {
		return temCCBValor;
	}

	public void setTemCCBValor(boolean temCCBValor) {
		this.temCCBValor = temCCBValor;
	}

	public boolean isTemItbi() {
		return temItbi;
	}

	public void setTemItbi(boolean temItbi) {
		this.temItbi = temItbi;
	}

	public boolean isMostrarDadosOcultos() {
		return mostrarDadosOcultos;
	}

	public void setMostrarDadosOcultos(boolean mostrarDadosOcultos) {
		this.mostrarDadosOcultos = mostrarDadosOcultos;
	}

	public String getCarencia() {
		return carencia;
	}

	public void setCarencia(String carencia) {
		this.carencia = carencia;
	}

	public String getAviso() {
		return aviso;
	}

	public void setAviso(String aviso) {
		this.aviso = aviso;
	}

	public ContasPagar getDespesaSelecionada() {
		return despesaSelecionada;
	}

	public void setDespesaSelecionada(ContasPagar despesaSelecionada) {
		this.despesaSelecionada = despesaSelecionada;
	}

	public CcbProcessosJudiciais getProcessoSelecionado() {
		return processoSelecionado;
	}

	public void setProcessoSelecionado(CcbProcessosJudiciais processoSelecionado) {
		this.processoSelecionado = processoSelecionado;
	}	
}
