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
import org.json.JSONObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
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
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.webnowbr.siscoat.cobranca.auxiliar.NumeroPorExtenso;
import com.webnowbr.siscoat.cobranca.auxiliar.PorcentagemPorExtenso;
import com.webnowbr.siscoat.cobranca.auxiliar.ValorPorExtenso;
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
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.common.BancosEnum;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.common.ValidaCNPJ;
import com.webnowbr.siscoat.common.ValidaCPF;
import com.webnowbr.siscoat.simulador.SimulacaoDetalheVO;
import com.webnowbr.siscoat.simulador.SimulacaoVO;
import com.webnowbr.siscoat.simulador.SimuladorMB;

/** ManagedBean. */
@ManagedBean(name = "ccbMB")
@SessionScoped
public class CcbMB {
	private String numeroContrato;
	private String numeroCcb;
	
	private String nomeEmitente;
	private String classeEmitente;
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

//--------------------------------------------------------------------
	private String nomeInterveniente;
	private String nacionalidadeInterveniente;
	private String profissaoInterveniente;
	private String estadoCivilInterveniente;
	private String numeroRgInterveniente;
	private String ufInterveniente;
	private String cpfInterveniente;
	private String logradouroInterveniente;
	private String numeroInterveniente;
	private String complementoInterveniente;
	private String cidadeInterveniente;
	private String cepInterveniente;
	private boolean addInterveniente;
	private boolean empresaInterveniente;
	private String paiInterveniente;
	private String maeInterveniente;
	
	private String razaoSocialInterveniente;
	private String tipoEmpresaInterveniente;
	private String cnpjInterveniente;
	private String municipioEmpresaInterveniente;
	private String estadoEmpresaInterveniente;
	private String ruaEmpresaInterveniente;
	private String numeroEmpresaInterveniente;
	private String salaEmpresaInterveniente;
	private String bairroEmpresaInterveniente;
	private String cepEmpresaInterveniente;
	
	private String emailInterveniente;
	private String regimeCasamentoInterveniente;
	private boolean femininoInterveniente;

	private String nomeConjugeInterveniente;
	private String cpfConjugeInterveniente;
	
//--------------------------------------------------------------------
	private String nomeTerceiroG;
	private String nacionalidadeTerceiroG;
	private String profissaoTerceiroG;
	private String estadoCivilTerceiroG;
	private String numeroRgTerceiroG;
	private String ufTerceiroG;
	private String cpfTerceiroG;
	private String logradouroTerceiroG;
	private String numeroTerceiroG;
	private String complementoTerceiroG;
	private String cidadeTerceiroG;
	private String cepTerceiroG;
	private boolean addTerceiro;
	private boolean empresaTerceiroG;
	private String paiTerceiroG;
	private String maeTerceiroG;
	
	
	private String razaoSocialTerceiroG;
	private String tipoEmpresaTerceiroG;
	private String cnpjTerceiroG;
	private String municipioEmpresaTerceiroG;
	private String estadoEmpresaTerceiroG;
	private String ruaEmpresaTerceiroG;
	private String numeroEmpresaTerceiroG;
	private String salaEmpresaTerceiroG;
	private String bairroEmpresaTerceiroG;
	private String cepEmpresaTerceiroG;
	
	private String emailTerceiroG;
	private String regimeCasamentoTerceiroG;
	private boolean femininoTerceiroG;
	
	private String nomeConjugeTerceiroG;
	private String cpfConjugeTerceiroG;
//--------------------------------------------------------------------
	
	private String nomeAvalista;
	private String nacionalidadeAvalista;
	private String profissaoAvalista;
	private String estadoCivilAvalista;
	private String numeroRgAvalista;
	private String ufAvalista;
	private String cpfAvalista;
	private String logradouroAvalista;
	private String numeroAvalista;
	private String complementoAvalista;
	private String cidadeAvalista;
	private String cepAvalista;
	private boolean addAvalista;
	private boolean empresaAvalista;
	private String paiAvalista;
	private String maeAvalista;
	
	private String razaoSocialAvalista;
	private String tipoEmpresaAvalista;
	private String cnpjAvalista;
	private String municipioEmpresaAvalista;
	private String estadoEmpresaAvalista;
	private String ruaEmpresaAvalista;
	private String numeroEmpresaAvalista;
	private String salaEmpresaAvalista;
	private String bairroEmpresaAvalista;
	private String cepEmpresaAvalista;
	
	private String emailAvalista;
	private String regimeCasamentoAvalista;
	private boolean femininoAvalista;
	
	private String nomeConjugeAvalista;
	private String cpfConjugeAvalista;

//--------------------------------------------------------------------

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
    
    private boolean fiducianteGerado = false;
    private boolean devedorGerado = false;
    private boolean modeloAntigo = false;
    
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
	
	public void removerSegurado(Segurado segurado) {
		this.objetoCcb.getListSegurados().remove(segurado);		
	}
	
	public void concluirSegurado() {
		this.tituloPagadorRecebedorDialog = "";
		this.updatePagadorRecebedor = "";
		this.seguradoSelecionado.setPosicao(this.objetoCcb.getListSegurados().size() + 1);
		this.objetoCcb.getListSegurados().add(this.seguradoSelecionado);
		this.seguradoSelecionado = new Segurado();
		this.seguradoSelecionado.setPessoa(new PagadorRecebedor());
		this.addSegurador= false;
	}
	
	public void pesquisaSegurado() {
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listPagadores = pagadorRecebedorDao.findAll();		
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
		fiducianteGerado = false;
		devedorGerado = false;
		modeloAntigo = false;
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
					this.objetoCcb.setContaCorrente(participanteSelecionado.getPessoa().getConta());
				}
			}
			
			if(CommonsUtil.semValor(this.objetoCcb.getAgencia())) {
				if(!CommonsUtil.semValor(participanteSelecionado.getPessoa().getAgencia())) {
					this.objetoCcb.setAgencia(participanteSelecionado.getPessoa().getAgencia());
				}
			}
			
			if(!CommonsUtil.semValor(participanteSelecionado.getPessoa().getBanco())) {			
				String[] banco = participanteSelecionado.getPessoa().getBanco().split(Pattern.quote("|"));
				if (CommonsUtil.semValor(this.objetoCcb.getNomeBanco())) {
					if (banco.length > 0) {
						this.objetoCcb.setNomeBanco(CommonsUtil.trimNull(banco[1]));
					}
				}
				if (CommonsUtil.semValor(this.objetoCcb.getNumeroBanco())) {
					if (banco.length > 1) {
						this.objetoCcb.setNumeroBanco(CommonsUtil.trimNull(banco[0]));
					}
				}	
			}
			
			if(CommonsUtil.semValor(this.objetoCcb.getTitularConta())) {
				if(!CommonsUtil.semValor(participanteSelecionado.getPessoa().getNomeCC())) {
					this.objetoCcb.setTitularConta(participanteSelecionado.getPessoa().getNomeCC());
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
	
	public void pesquisaContratoCobranca() {
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		this.listaContratosConsultar = cDao.consultaContratosCCBs();
	}
	
	public void populateSelectedContratoCobranca() {
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		ContratoCobranca contrato = new ContratoCobranca();
		contrato = cDao.findById(this.getObjetoContratoCobranca().getId());
		if(CommonsUtil.semValor(this.objetoCcb.getObjetoContratoCobranca())){
			this.objetoCcb.setObjetoContratoCobranca(contrato);
			this.objetoCcb.setNumeroOperacao(contrato.getNumeroContrato());
		}
		
		this.objetoCcb.setVlrImovel(contrato.getValorMercadoImovel());
		this.objetoCcb.setVendaLeilao(contrato.getValorVendaForcadaImovel());
		
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
	}
	
	public void clearContratoCobranca() {
		this.objetoContratoCobranca = new ContratoCobranca();
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
		
		if(isTemCustasCartorarias()) {
			if(!CommonsUtil.semValor(this.objetoCcb.getCustasCartorariasValor()))
				total = total.add(this.objetoCcb.getCustasCartorariasValor());
		}
		if(isTemCertidaoDeCasamento()) {
			if(!CommonsUtil.semValor(this.objetoCcb.getCertidaoDeCasamentoValor()))
				total = total.add(this.objetoCcb.getCertidaoDeCasamentoValor());
		}
		if(isTemLaudoDeAvaliacao()) {
			if(!CommonsUtil.semValor(this.objetoCcb.getLaudoDeAvaliacaoValor()))
				total = total.add(this.objetoCcb.getLaudoDeAvaliacaoValor());
		}
		if(isTemIntermediacao()) {
			if(!CommonsUtil.semValor(this.objetoCcb.getIntermediacaoValor()))
				total = total.add(this.objetoCcb.getIntermediacaoValor());
		}
		if(isTemIptuEmAtraso()) {
			if(!CommonsUtil.semValor(this.objetoCcb.getIptuEmAtrasoValor()))
				total = total.add(this.objetoCcb.getIptuEmAtrasoValor());
		}
		if(isTemCondominioEmAtraso()) {
			if(!CommonsUtil.semValor(this.objetoCcb.getCondominioEmAtrasoValor()))
				total = total.add(this.objetoCcb.getCondominioEmAtrasoValor());
		}
		if(isTemIq()) {
			if(!CommonsUtil.semValor(this.objetoCcb.getIqValor()))
				total = total.add(this.objetoCcb.getIqValor());
		}
		if(isTemCCBValor()) {
			if(!CommonsUtil.semValor(this.objetoCcb.getCCBValor()))
				total = total.add(this.objetoCcb.getCCBValor());
		}
		if(isTemItbi()) {
			if(!CommonsUtil.semValor(this.objetoCcb.getItbiValor()))
				total = total.add(this.objetoCcb.getItbiValor());
		}
		if(!this.objetoCcb.getProcessosJucidiais().isEmpty()) {
			for(CcbProcessosJudiciais processo : this.objetoCcb.getProcessosJucidiais()) {
				if(!CommonsUtil.semValor(processo.getValor()))
					total = total.add(processo.getValor());
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
		populateSelectedContratoCobranca();
		
		objetoCcb.setValorCredito(objetoContratoCobranca.getValorAprovadoComite());
		objetoCcb.setTaxaDeJurosMes(objetoContratoCobranca.getTaxaAprovada());
		objetoCcb.setPrazo(objetoContratoCobranca.getPrazoMaxAprovado().toString());
		
		if(!CommonsUtil.semValor(objetoContratoCobranca.getValorLaudoPajuFaltante())) {
			this.temLaudoDeAvaliacao = true;
			objetoCcb.setLaudoDeAvaliacaoValor(objetoContratoCobranca.getValorLaudoPajuFaltante());
		}
		
		if(CommonsUtil.mesmoValor(objetoContratoCobranca.getCobrarComissaoCliente(), "Sim")) {
			this.temIntermediacao = true;
			objetoCcb.setIntermediacaoBanco(objetoContratoCobranca.getResponsavel().getBanco());
			objetoCcb.setIntermediacaoAgencia(objetoContratoCobranca.getResponsavel().getAgencia());
			objetoCcb.setIntermediacaoCC(objetoContratoCobranca.getResponsavel().getConta());
			objetoCcb.setIntermediacaoCNPJ(objetoContratoCobranca.getResponsavel().getCpfCnpjCC());
			objetoCcb.setIntermediacaoNome(objetoContratoCobranca.getResponsavel().getNomeCC());
			objetoCcb.setIntermediacaoPix(objetoContratoCobranca.getResponsavel().getPix());
		}
		
		calculaPorcentagemImovel();
		
		if (objetoCcb.getId() <= 0) {
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
		}
		
		//luana
		pesquisaTestemunha1();
		selectedPagadorGenerico = ccbDao.ConsultaTestemunha((long) 11960);
		populateSelectedPagadorRecebedor();
		
		//anna flavia
		pesquisaTestemunha2();
		selectedPagadorGenerico = ccbDao.ConsultaTestemunha((long) 25929);
		populateSelectedPagadorRecebedor();
		
		//clearFieldsInserirCcb();
		return "/Atendimento/Cobranca/Ccb.xhtml";
	}
	
	public ContratoCobranca getContratoById(long idContrato) {
		ContratoCobranca contrato = new ContratoCobranca();
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
		
		if(this.addInterveniente == true) {
			if(!CommonsUtil.semValor(this.intervenienteSelecionado.getCpf())) {
				this.setNomeInterveniente(this.intervenienteSelecionado.getNome());
				this.setUfInterveniente(this.intervenienteSelecionado.getEstado());
				this.setLogradouroInterveniente(this.intervenienteSelecionado.getEndereco());
				this.setCepInterveniente(this.intervenienteSelecionado.getCep());
				this.setCidadeInterveniente(this.intervenienteSelecionado.getCidade());
				this.setNumeroInterveniente(this.intervenienteSelecionado.getNumero());
				this.setEmailInterveniente(this.intervenienteSelecionado.getEmail());
				this.setProfissaoInterveniente(this.intervenienteSelecionado.getAtividade());
				this.setEstadoCivilInterveniente(this.intervenienteSelecionado.getEstadocivil());
				this.setNumeroRgInterveniente(this.intervenienteSelecionado.getRg());	
				this.setCpfInterveniente(this.intervenienteSelecionado.getCpf());
				this.setComplementoInterveniente(this.intervenienteSelecionado.getComplemento());
				this.setLogradouroInterveniente(this.intervenienteSelecionado.getEndereco());
				this.setPaiInterveniente(this.intervenienteSelecionado.getNomePai());
				this.setMaeInterveniente(this.intervenienteSelecionado.getNomeMae());
				
				if(!CommonsUtil.semValor(this.intervenienteSelecionado.getSexo())) {
					if(this.intervenienteSelecionado.getSexo() == "MASCULINO") {
						this.femininoInterveniente = false;
					} else if(this.intervenienteSelecionado.getSexo() == "FEMININO") {
						this.femininoInterveniente = true;
					} else {
						this.femininoInterveniente = false;
					}
				} else if(this.femininoInterveniente = true) {
					this.intervenienteSelecionado.setSexo("FEMININO");
				} else {
					this.intervenienteSelecionado.setSexo("MASCULINO");
				}
				
				if (this.intervenienteSelecionado.getNomeConjuge() != null) {
					this.setRegimeCasamentoInterveniente(this.intervenienteSelecionado.getRegimeCasamento());
					this.setNomeConjugeInterveniente(this.intervenienteSelecionado.getNomeConjuge());
					this.setCpfConjugeInterveniente(this.intervenienteSelecionado.getCpfConjuge());
				} else {
					this.setNomeConjugeInterveniente(null);
					this.setCpfConjugeInterveniente(null);
					this.setRegimeCasamentoInterveniente(null);
				}
			} else {
				this.setEmpresaInterveniente(true);
				this.setCnpjInterveniente(this.intervenienteSelecionado.getCnpj());
				this.setRazaoSocialInterveniente(this.intervenienteSelecionado.getNome());
				this.setEstadoEmpresaInterveniente(this.intervenienteSelecionado.getEstado());
				this.setRuaEmpresaInterveniente(this.intervenienteSelecionado.getEndereco());
				this.setCepEmpresaInterveniente(this.intervenienteSelecionado.getCep());
				this.setMunicipioEmpresaInterveniente(this.intervenienteSelecionado.getCidade());
				this.setNumeroEmpresaInterveniente(this.intervenienteSelecionado.getNumero());
			}
		}
		
		if(this.addTerceiro == true) {
			if(!CommonsUtil.semValor(this.terceiroGSelecionado.getCpf())) {
				this.setNomeTerceiroG(this.terceiroGSelecionado.getNome());
				this.setUfTerceiroG(this.terceiroGSelecionado.getEstado());
				this.setLogradouroTerceiroG(this.terceiroGSelecionado.getEndereco());
				this.setCepTerceiroG(this.terceiroGSelecionado.getCep());
				this.setCidadeTerceiroG(this.terceiroGSelecionado.getCidade());
				this.setNumeroTerceiroG(this.terceiroGSelecionado.getNumero());
				this.setEmailTerceiroG(this.terceiroGSelecionado.getEmail());
				this.setProfissaoTerceiroG(this.terceiroGSelecionado.getAtividade());
				this.setEstadoCivilTerceiroG(this.terceiroGSelecionado.getEstadocivil());
				this.setNumeroRgTerceiroG(this.terceiroGSelecionado.getRg());	
				this.setCpfTerceiroG(this.terceiroGSelecionado.getCpf());
				this.setComplementoTerceiroG(this.terceiroGSelecionado.getComplemento());
				this.setLogradouroTerceiroG(this.terceiroGSelecionado.getEndereco());
				this.setPaiTerceiroG(this.terceiroGSelecionado.getNomePai());
				this.setMaeTerceiroG(this.terceiroGSelecionado.getNomeMae());
				
				if(!CommonsUtil.semValor(this.terceiroGSelecionado.getSexo())) {
					if(this.terceiroGSelecionado.getSexo() == "MASCULINO") {
						this.femininoTerceiroG = false;
					} else if(this.terceiroGSelecionado.getSexo() == "FEMININO") {
						this.femininoTerceiroG = true;
					} else {
						this.femininoTerceiroG = false;
					}
				} else if(this.femininoTerceiroG = true) {
					this.terceiroGSelecionado.setSexo("FEMININO");
				} else {
					this.terceiroGSelecionado.setSexo("MASCULINO");
				}
				
				if (this.terceiroGSelecionado.getNomeConjuge() != null) {
					this.setRegimeCasamentoTerceiroG(this.terceiroGSelecionado.getRegimeCasamento());
					this.setNomeConjugeTerceiroG(this.terceiroGSelecionado.getNomeConjuge());
					this.setCpfConjugeTerceiroG(this.terceiroGSelecionado.getCpfConjuge());
				} else {
					this.setNomeConjugeTerceiroG(null);
					this.setCpfConjugeTerceiroG(null);
					this.setRegimeCasamentoTerceiroG(null);
				}
			} else {
				this.setEmpresaTerceiroG(true);
				this.setCnpjTerceiroG(this.terceiroGSelecionado.getCnpj());
				this.setRazaoSocialTerceiroG(this.terceiroGSelecionado.getNome());
				this.setEstadoEmpresaTerceiroG(this.terceiroGSelecionado.getEstado());
				this.setRuaEmpresaTerceiroG(this.terceiroGSelecionado.getEndereco());
				this.setCepEmpresaTerceiroG(this.terceiroGSelecionado.getCep());
				this.setMunicipioEmpresaTerceiroG(this.terceiroGSelecionado.getCidade());
				this.setNumeroEmpresaTerceiroG(this.terceiroGSelecionado.getNumero());
			}
		}
		
		if(this.addAvalista == true) {
			if(!CommonsUtil.semValor(this.avalistaSelecionado.getCpf())) {
				this.setNomeAvalista(this.avalistaSelecionado.getNome());
				this.setUfAvalista(this.avalistaSelecionado.getEstado());
				this.setLogradouroAvalista(this.avalistaSelecionado.getEndereco());
				this.setCepAvalista(this.avalistaSelecionado.getCep());
				this.setCidadeAvalista(this.avalistaSelecionado.getCidade());
				this.setNumeroAvalista(this.avalistaSelecionado.getNumero());
				this.setEmailAvalista(this.avalistaSelecionado.getEmail());
				this.setProfissaoAvalista(this.avalistaSelecionado.getAtividade());
				this.setEstadoCivilAvalista(this.avalistaSelecionado.getEstadocivil());
				this.setNumeroRgAvalista(this.avalistaSelecionado.getRg());	
				this.setCpfAvalista(this.avalistaSelecionado.getCpf());
				this.setComplementoAvalista(this.avalistaSelecionado.getComplemento());
				this.setLogradouroAvalista(this.avalistaSelecionado.getEndereco());
				this.setPaiAvalista(this.avalistaSelecionado.getNomePai());
				this.setMaeAvalista(this.avalistaSelecionado.getNomeMae());
				
				if(!CommonsUtil.semValor(this.avalistaSelecionado.getSexo())) {
					if(this.avalistaSelecionado.getSexo() == "MASCULINO") {
						this.femininoAvalista = false;
					} else if(this.avalistaSelecionado.getSexo() == "FEMININO") {
						this.femininoAvalista = true;
					} else {
						this.femininoAvalista = false;
					}
				} else if(this.femininoAvalista = true) {
					this.avalistaSelecionado.setSexo("FEMININO");
				} else {
					this.avalistaSelecionado.setSexo("MASCULINO");
				}
				
				if (this.avalistaSelecionado.getNomeConjuge() != null) {
					this.setRegimeCasamentoAvalista(this.avalistaSelecionado.getRegimeCasamento());
					this.setNomeConjugeAvalista(this.avalistaSelecionado.getNomeConjuge());
					this.setCpfConjugeAvalista(this.avalistaSelecionado.getCpfConjuge());
				} else {
					this.setNomeConjugeAvalista(null);
					this.setCpfConjugeAvalista(null);
					this.setRegimeCasamentoAvalista(null);
				}
			} else {
				this.setEmpresaAvalista(true);
				this.setCnpjAvalista(this.avalistaSelecionado.getCnpj());
				this.setRazaoSocialAvalista(this.avalistaSelecionado.getNome());
				this.setEstadoEmpresaAvalista(this.avalistaSelecionado.getEstado());
				this.setRuaEmpresaAvalista(this.avalistaSelecionado.getEndereco());
				this.setCepEmpresaAvalista(this.avalistaSelecionado.getCep());
				this.setMunicipioEmpresaAvalista(this.avalistaSelecionado.getCidade());
				this.setNumeroEmpresaAvalista(this.avalistaSelecionado.getNumero());
			}
		}
	}
	
	public void populateSelectedPagadorRecebedor() {
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

		else if (CommonsUtil.mesmoValor(this.tipoPesquisa , "Interveniente")) {
			this.intervenienteSelecionado = (this.selectedPagadorGenerico);
			this.setNomeInterveniente(this.intervenienteSelecionado.getNome());
			this.setProfissaoInterveniente(this.intervenienteSelecionado.getAtividade());
			this.setEstadoCivilInterveniente(this.intervenienteSelecionado.getEstadocivil());
			this.setNumeroRgInterveniente(this.intervenienteSelecionado.getRg());
			this.setUfInterveniente(this.intervenienteSelecionado.getEstado());
			this.setCpfInterveniente(this.intervenienteSelecionado.getCpf());
			this.setLogradouroInterveniente(this.intervenienteSelecionado.getEndereco());
			this.setNumeroInterveniente(this.intervenienteSelecionado.getNumero());
			this.setComplementoInterveniente(this.intervenienteSelecionado.getComplemento());
			this.setCidadeInterveniente(this.intervenienteSelecionado.getCidade());
			this.setCepInterveniente(this.intervenienteSelecionado.getCep());
			this.setEmailInterveniente(this.intervenienteSelecionado.getEmail());
			if(this.intervenienteSelecionado.getSexo() != null) {
				if(this.intervenienteSelecionado.getSexo() == "MASCULINO") {
					this.femininoInterveniente = false;
				} else if(this.intervenienteSelecionado.getSexo() == "FEMININO") {
					this.femininoInterveniente = true;
				} else {
					this.femininoInterveniente = false;
				}
			}
			if (this.intervenienteSelecionado.getNomeConjuge() != null) {
				this.setNomeConjugeInterveniente(this.intervenienteSelecionado.getNomeConjuge());
				this.setCpfConjugeInterveniente(this.intervenienteSelecionado.getCpfConjuge());
			} else {
				this.setNomeConjugeInterveniente(null);
				this.setCpfConjugeInterveniente(null);
			}
		}
		
		else if (CommonsUtil.mesmoValor(this.tipoPesquisa , "TerceiroG")) {
			this.terceiroGSelecionado = (this.selectedPagadorGenerico);
			this.setNomeTerceiroG(this.terceiroGSelecionado.getNome());
			this.setProfissaoTerceiroG(this.terceiroGSelecionado.getAtividade());
			this.setEstadoCivilTerceiroG(this.terceiroGSelecionado.getEstadocivil());
			this.setNumeroRgTerceiroG(this.terceiroGSelecionado.getRg());
			this.setUfTerceiroG(this.terceiroGSelecionado.getEstado());
			this.setCpfTerceiroG(this.terceiroGSelecionado.getCpf());
			this.setLogradouroTerceiroG(this.terceiroGSelecionado.getEndereco());
			this.setNumeroTerceiroG(this.terceiroGSelecionado.getNumero());
			this.setComplementoTerceiroG(this.terceiroGSelecionado.getComplemento());
			this.setCidadeTerceiroG(this.terceiroGSelecionado.getCidade());
			this.setCepTerceiroG(this.terceiroGSelecionado.getCep());
			this.setEmailTerceiroG(this.terceiroGSelecionado.getEmail());
			if(this.terceiroGSelecionado.getSexo() != null) {
				if(this.terceiroGSelecionado.getSexo() == "MASCULINO") {
					this.femininoTerceiroG = false;
				} else if(this.terceiroGSelecionado.getSexo() == "FEMININO") {
					this.femininoTerceiroG = true;
				} else {
					this.femininoTerceiroG = false;
				}
			}
			if (this.terceiroGSelecionado.getNomeConjuge() != null) {
				this.setNomeConjugeTerceiroG(this.terceiroGSelecionado.getNomeConjuge());
				this.setCpfConjugeTerceiroG(this.terceiroGSelecionado.getCpfConjuge());
			} else {
				this.setNomeConjugeTerceiroG(null);
				this.setCpfConjugeTerceiroG(null);
			}
		}
		
		else if (CommonsUtil.mesmoValor(this.tipoPesquisa ,"Avalista")) {
			this.avalistaSelecionado = (this.selectedPagadorGenerico);
			this.setNomeAvalista(this.avalistaSelecionado.getNome());
			this.setProfissaoAvalista(this.avalistaSelecionado.getAtividade());
			this.setEstadoCivilAvalista(this.avalistaSelecionado.getEstadocivil());
			this.setNumeroRgAvalista(this.avalistaSelecionado.getRg());
			this.setUfAvalista(this.avalistaSelecionado.getEstado());
			this.setCpfAvalista(this.avalistaSelecionado.getCpf());
			this.setLogradouroAvalista(this.avalistaSelecionado.getEndereco());
			this.setNumeroAvalista(this.avalistaSelecionado.getNumero());
			this.setComplementoAvalista(this.avalistaSelecionado.getComplemento());
			this.setCidadeAvalista(this.avalistaSelecionado.getCidade());
			this.setCepAvalista(this.avalistaSelecionado.getCep());
			this.setEmailAvalista(this.avalistaSelecionado.getEmail());
			if(this.avalistaSelecionado.getSexo() != null) {
				if(this.avalistaSelecionado.getSexo() == "MASCULINO") {
					this.femininoAvalista = false;
				} else if(this.avalistaSelecionado.getSexo() == "FEMININO") {
					this.femininoAvalista = true;
				} else {
					this.femininoAvalista = false;
				}
			}
			if (this.avalistaSelecionado.getNomeConjuge() != null) {
				this.setNomeConjugeAvalista(this.avalistaSelecionado.getNomeConjuge());
				this.setCpfConjugeAvalista(this.avalistaSelecionado.getCpfConjuge());
			} else {
				this.setNomeConjugeAvalista(null);
				this.setCpfConjugeAvalista(null);
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
		
		criarConjugeNoSistema(pagadorRecebedor);
	}
	
	public void criarConjugeNoSistema(PagadorRecebedor pagador) {
		if(CommonsUtil.semValor(pagador.getEstadocivil())){
			return;
		}
		if(!CommonsUtil.mesmoValor(pagador.getEstadocivil(), "CASADO")){
			return;
		}
		
		PagadorRecebedor conjuge = null;
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();

		List<PagadorRecebedor> pagadorRecebedorBD = new ArrayList<PagadorRecebedor>();
		boolean registraPagador = false;
		Long idPagador = (long) 0;

		if (!CommonsUtil.semValor(pagador.getCpfConjuge())) {
			boolean validaCPF = ValidaCPF.isCPF(pagador.getCpfConjuge());
			if(validaCPF) {
				pagadorRecebedorBD = pagadorRecebedorDao.findByFilter("cpf", pagador.getCpfConjuge());
				if (pagadorRecebedorBD.size() > 0) {
					conjuge = pagadorRecebedorBD.get(0);
				} else {
					conjuge = new PagadorRecebedor();
					registraPagador = true;
				}
			} else {
				return;
			}
		} else {
			return;
		}
		
		conjuge.setEstadocivil(pagador.getEstadocivil());
		conjuge.setRegimeCasamento(pagador.getRegimeCasamento());
		conjuge.setRegistroPactoAntenupcial(pagador.getRegistroPactoAntenupcial());
		conjuge.setLivroPactoAntenupcial(pagador.getLivroPactoAntenupcial());
		conjuge.setFolhasPactoAntenupcial(pagador.getFolhasPactoAntenupcial());
		conjuge.setDataPactoAntenupcial(pagador.getDataPactoAntenupcial());
		
		conjuge.setNome(pagador.getNomeConjuge());
		conjuge.setCpf(pagador.getCpfConjuge());
		conjuge.setAtividade(pagador.getCargoConjuge());
		conjuge.setRg(pagador.getRgConjuge());
		conjuge.setSexo(pagador.getSexoConjuge());
		conjuge.setTelResidencial(pagador.getTelResidencialConjuge());
		conjuge.setTelCelular(pagador.getTelCelularConjuge());
		conjuge.setDtNascimento(pagador.getDtNascimentoConjuge());
		conjuge.setIdade(pagador.getIdadeConjuge());
		conjuge.setNomeMae(pagador.getNomeMaeConjuge());
		conjuge.setNomePai(pagador.getNomePaiConjuge());
		conjuge.setEndereco(pagador.getEnderecoConjuge());
		conjuge.setBairro(pagador.getBairroConjuge());
		conjuge.setComplemento(pagador.getComplementoConjuge());
		conjuge.setCidade(pagador.getCidadeConjuge());
		conjuge.setEstado(pagador.getEstadoConjuge());
		conjuge.setCep(pagador.getCepConjuge());
		conjuge.setEmail(pagador.getEmailConjuge());
		conjuge.setBanco(pagador.getBancoConjuge());
		conjuge.setAgencia(pagador.getAgenciaConjuge());
		conjuge.setConta(pagador.getContaConjuge());
		conjuge.setNomeCC(pagador.getNomeCCConjuge());
		conjuge.setCpfCC(pagador.getCpfCCConjuge());
		
		conjuge.setNomeConjuge(pagador.getNome());
		conjuge.setCpfConjuge(pagador.getCpf());
		conjuge.setCargoConjuge(pagador.getAtividade());
		conjuge.setRgConjuge(pagador.getRg());
		conjuge.setSexoConjuge(pagador.getSexo());
		conjuge.setTelResidencialConjuge(pagador.getTelResidencial());
		conjuge.setTelCelularConjuge(pagador.getTelCelular());
		conjuge.setDtNascimentoConjuge(pagador.getDtNascimento());
		conjuge.setIdadeConjuge(pagador.getIdade());
		conjuge.setNomeMaeConjuge(pagador.getNomeMae());
		conjuge.setNomePaiConjuge(pagador.getNomePai());
		conjuge.setEnderecoConjuge(pagador.getEndereco());
		conjuge.setBairroConjuge(pagador.getBairro());
		conjuge.setComplementoConjuge(pagador.getComplemento());
		conjuge.setCidadeConjuge(pagador.getCidade());
		conjuge.setEstadoConjuge(pagador.getEstado());
		conjuge.setCepConjuge(pagador.getCep());
		conjuge.setEmailConjuge(pagador.getEmail());
		conjuge.setBancoConjuge(pagador.getBanco());
		conjuge.setAgenciaConjuge(pagador.getAgencia());
		conjuge.setContaConjuge(pagador.getConta());
		conjuge.setNomeCCConjuge(pagador.getNomeCC());
		conjuge.setCpfCCConjuge(pagador.getCpfCC());
		
		if (registraPagador) {
			idPagador = pagadorRecebedorDao.create(conjuge);
			conjuge = pagadorRecebedorDao.findById(idPagador);
			System.out.println("ConjugeCriado");
		} else {
			pagadorRecebedorDao.merge(conjuge);
		}
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
			
			if (this.objetoCcb.getId() > 0) {
				ccbDao.merge(this.objetoCcb);
				System.out.println("CCB Merge ID: " + objetoCcb.getId() + " / "  + objetoCcb.getNumeroCcb() + " / "
						+ objetoCcb.getNumeroOperacao() + " / " + objetoCcb.getNomeEmitente());
			} else {
				ccbDao.create(this.objetoCcb);
				System.out.println("CCB Create ID: " + objetoCcb.getId() + " / "  + objetoCcb.getNumeroCcb() + " / "
						+ objetoCcb.getNumeroOperacao() + " / " + objetoCcb.getNomeEmitente());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "CCB: " + e.getCause(), ""));
		} finally {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Percelas Geradas com sucesso", ""));	
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
			if(CommonsUtil.mesmoValor(valorSobrescrever,BigDecimal.ZERO)) {
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
	
	public void criarTexto(XWPFParagraph paragraph, XWPFDocument document, XWPFRun run) {
		paragraph = document.createParagraph();
		run = paragraph.createRun();
	}

	static String cTAbstractNumBulletXML = "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"0\">"
			+ "<w:multiLevelType w:val=\"hybridMultilevel\"/>"
			+ "<w:lvl w:ilvl=\"0\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1)\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"720\" w:hanging=\"360\"/></w:pPr><w:rPr><w:sz w:val=\"24\"/></w:rPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"1\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"1440\" w:hanging=\"360\"/></w:pPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"2\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2.%3\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"2160\" w:hanging=\"360\"/></w:pPr></w:lvl>"
			+ "</w:abstractNum>";
	
	static String cTAbstractNumBulletXML_NoLeft = "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"1\">"
			+ "<w:multiLevelType w:val=\"hybridMultilevel\"/>"
			+ "<w:lvl w:ilvl=\"0\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1)\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"360\" w:hanging=\"360\"/></w:pPr><w:rPr><w:sz w:val=\"24\"/></w:rPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"1\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"720\" w:hanging=\"360\"/></w:pPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"2\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2.%3\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"1440\" w:hanging=\"360\"/></w:pPr></w:lvl>"
			+ "</w:abstractNum>";
	
	static String cTAbstractNumBulletXML_NoLeft_NoHanging_bold = "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"2\">"
			+ "<w:multiLevelType w:val=\"hybridMultilevel\"/>"
			+ "<w:lvl w:ilvl=\"0\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1)\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"0\" w:hanging=\"0\"/></w:pPr><w:rPr><w:b w:val=\"true\"/><w:sz w:val=\"24\"/></w:rPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"1\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"720\" w:hanging=\"0\"/></w:pPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"2\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2.%3\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"1440\" w:hanging=\"0\"/></w:pPr></w:lvl>"
			+ "</w:abstractNum>";
	
	static String cTAbstractNumBulletXML_NoLeft_NoHanging_bold2 = "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"3\">"
			+ "<w:multiLevelType w:val=\"hybridMultilevel\"/>"
			+ "<w:lvl w:ilvl=\"0\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1)\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"0\" w:hanging=\"0\"/></w:pPr><w:rPr><w:b w:val=\"true\"/><w:sz w:val=\"24\"/></w:rPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"1\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"720\" w:hanging=\"0\"/></w:pPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"2\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2.%3\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"1440\" w:hanging=\"0\"/></w:pPr></w:lvl>"
			+ "</w:abstractNum>";
	
	static String cTAbstractNumBulletXML_NoHanging_bold = "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"4\">"
			+ "<w:multiLevelType w:val=\"hybridMultilevel\"/>"
			+ "<w:lvl w:ilvl=\"0\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1)\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"360\" w:hanging=\"0\"/></w:pPr><w:rPr><w:b w:val=\"true\"/><w:sz w:val=\"24\"/></w:rPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"1\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"1440\" w:hanging=\"360\"/></w:pPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"2\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2.%3\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"2160\" w:hanging=\"360\"/></w:pPr></w:lvl>"
			+ "</w:abstractNum>";
	
	static String cTAbstractNumBulletXML_bold = "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"5\">"
			+ "<w:multiLevelType w:val=\"hybridMultilevel\"/>"
			+ "<w:lvl w:ilvl=\"0\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1)\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"720\" w:hanging=\"360\"/></w:pPr><w:rPr><w:b w:val=\"true\"/><w:sz w:val=\"24\"/></w:rPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"1\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"1440\" w:hanging=\"360\"/></w:pPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"2\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2.%3\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"2160\" w:hanging=\"360\"/></w:pPr></w:lvl>"
			+ "</w:abstractNum>";
	
	static String cTAbstractNumBulletXML_bold_Roman = "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"6\">"
			+ "<w:multiLevelType w:val=\"hybridMultilevel\"/>"
			+ "<w:lvl w:ilvl=\"0\"><w:start w:val=\"1\"/><w:numFmt w:val=\"upperRoman\"/><w:lvlText w:val=\"%1-\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"1080\" w:hanging=\"720\"/></w:pPr><w:rPr><w:b w:val=\"true\"/><w:i w:val=\"true\"/><w:sz w:val=\"24\"/></w:rPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"1\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"upperRoman\"/><w:lvlText w:val=\"%1-.%2\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"2160\" w:hanging=\"720\"/></w:pPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"2\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"upperRoman\"/><w:lvlText w:val=\"%1-.%2.%3\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"2880\" w:hanging=\"720\"/></w:pPr></w:lvl>"
			+ "</w:abstractNum>";
	
	static String cTAbstractNumBulletXML_bold2 = "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"7\">"
			+ "<w:multiLevelType w:val=\"hybridMultilevel\"/>"
			+ "<w:lvl w:ilvl=\"0\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1)\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"720\" w:hanging=\"360\"/></w:pPr><w:rPr><w:b w:val=\"true\"/><w:sz w:val=\"24\"/></w:rPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"1\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"1440\" w:hanging=\"360\"/></w:pPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"2\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"lowerLetter\"/><w:lvlText w:val=\"%1).%2.%3\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"2160\" w:hanging=\"360\"/></w:pPr></w:lvl>"
			+ "</w:abstractNum>";
	
	static String cTAbstractNumBulletXML_bold_Roman_NoLeft_NoHanging= "<w:abstractNum xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" w:abstractNumId=\"8\">"
			+ "<w:multiLevelType w:val=\"hybridMultilevel\"/>"
			+ "<w:lvl w:ilvl=\"0\"><w:start w:val=\"1\"/><w:numFmt w:val=\"upperRoman\"/><w:lvlText w:val=\"%1\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"0\" w:hanging=\"0\"/></w:pPr><w:rPr><w:b w:val=\"true\"/><w:sz w:val=\"24\"/></w:rPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"1\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"upperRoman\"/><w:lvlText w:val=\"%1.%2\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"720\" w:hanging=\"720\"/></w:pPr></w:lvl>"
			+ "<w:lvl w:ilvl=\"2\" w:tentative=\"1\"><w:start w:val=\"1\"/><w:numFmt w:val=\"upperRoman\"/><w:lvlText w:val=\"%1.%2.%3\"/><w:lvlJc w:val=\"left\"/><w:pPr><w:ind w:left=\"1440\" w:hanging=\"720\"/></w:pPr></w:lvl>"
			+ "</w:abstractNum>";
	
	public StreamedContent geraCcbDinamica() throws IOException {
		try {
			CcbDao ccbDao = new CcbDao();
			XWPFDocument document = new XWPFDocument();
			XWPFHeaderFooterPolicy headerFooterPolicy = document.getHeaderFooterPolicy();
			if (headerFooterPolicy == null)
				headerFooterPolicy = document.createHeaderFooterPolicy();

			XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			XWPFParagraph paragraphHeader = header.createParagraph();
			paragraphHeader.setAlignment(ParagraphAlignment.LEFT);
			XWPFRun runHeader = paragraphHeader.createRun();
			runHeader.addPicture(getClass().getResourceAsStream("/resource/BMP MoneyPlus.png"), 6, "BMP MoneyPlus",
					Units.toEMU(130), Units.toEMU(72));
			runHeader.addTab();
			runHeader.addTab();
			runHeader.addTab();
			runHeader.addTab();
			runHeader.addTab();
			runHeader.addTab();
			runHeader.addTab();
			runHeader.setText("VIA NEGOCIÁVEL");
			runHeader.setFontSize(12);
			runHeader.setColor("0000ff");
			runHeader.setBold(true);

			XWPFRun run;

			XWPFParagraph paragraph = document.createParagraph();
			run = paragraph.createRun();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run.setText("CÉDULA DE CRÉDITO BANCÁRIO");
			XWPFRun run2 = paragraph.createRun();
			XWPFRun run3 = paragraph.createRun();
			XWPFRun run4 = paragraph.createRun();
			run.addCarriageReturn();

			run.setText("Nº " + this.objetoCcb.getNumeroCcb());
			run.setFontSize(14);
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			run.addCarriageReturn();

			fazParagrafoSimples(document, paragraph, run, "1.  Partes:", true);

			geraParagrafoComposto(document, paragraph, run, run2,
					"I – CREDOR: BMP SOCIEDADE DE CRÉDITO DIRETO S.A.",
					", instituição financeira, inscrita no CNPJ/MF sob nº 34.337.707/0001-00,"
						+ " com sede na Av. Paulista, 1765, 1º Andar, CEP 01311-200, São Paulo, SP,"
						+ " neste ato, representada na forma do seu Estatuto Social; ",
					true, false);

			int iParticipante = 2;
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				paragraph.setSpacingBetween(1);

				run = paragraph.createRun();
				run.setFontSize(12);
				run.setText(RomanNumerals(iParticipante) + " – " + participante.getTipoParticipante() + ":");
				run.setText(" " + participante.getPessoa().getNome() + ", ");
				run.setBold(true);

				run2 = paragraph.createRun();
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
						XWPFRun runSocios = paragraph.createRun();
						runSocios.setFontSize(12);
						runSocios.setText(" " + sociosParticipante.getPessoa().getNome() + ", ");
						runSocios.setBold(true);
						XWPFRun runSociosNome = paragraph.createRun();
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
				}
				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "TERCEIRO GARANTIDOR")) {
					this.objetoCcb.setTerceiroGarantidor(true);
				}

				iParticipante++;
			}

			fazParagrafoSimples(document, paragraph, run, "Considerando que: ", false);
			
			if(this.objetoCcb.isTerceiroGarantidor()) {
				fazParagrafoSimples(document, paragraph, run,
						"a)	O EMITENTE e o TERCEIRO(S) GARANTIDOR(ES) declara(m) e garante(m) "
						+ "que está(ão) devidamente autorizado a firmar a presente Cédula de "
						+ "Crédito Bancário (“CCB”), e assumir todas as obrigações aqui pactuadas"
						+ " e cumprir todos os seus termos e condições até quitação final de todas"
						+ " as obrigações aqui estabelecidas, uma vez que as obrigações pecuniárias"
						+ " assumidas nesta CCB são compatíveis com a capacidade econômico-financeira "
						+ "do EMITENTE para honrá-las;",
						false, ParagraphAlignment.BOTH);
			} else {
				fazParagrafoSimples(document, paragraph, run,
						"a) O EMITENTE declara e garante que está devidamente "
								+ "autorizado a firmar a presente Cédula de Crédito Bancário (“CCB”),"
								+ " e assumir todas as obrigações aqui pactuadas e cumprir todos os "
								+ "seus termos e condições até quitação final de todas as obrigações aqui "
								+ "estabelecidas, uma vez que as obrigações pecuniárias assumidas "
								+ "nesta CCB são compatíveis com a capacidade econômico-financeira do"
								+ " EMITENTE para honrá-las;",
						false, ParagraphAlignment.BOTH);
			}

			fazParagrafoSimples(document, paragraph, run, "b) O EMITENTE declara e garante que cumpre o disposto na"
					+ " legislação referente à Política Nacional de Meio Ambiente"
					+ " e não aplicará os recursos decorrentes desta CCB no financiamento "
					+ "de qualquer atividade ou projeto que caracterize crime contra o"
					+ " meio ambiente, que cause poluição e/ou que prejudique o ordenamento"
					+ " urbano e o patrimônio cultural, obrigando-se a respeitar integralmente"
					+ " as normas contidas nas Leis nº 9.605/98 e nº 9.985/2000 e demais"
					+ " regras complementares; e ainda que não utilizará os recursos no "
					+ "desenvolvimento de suas atividades comerciais e vinculadas ao seu objeto"
					+ " social, formas nocivas ou de exploração de trabalho forçado e/ou mão" + " de obra infantil.",
					false, ParagraphAlignment.BOTH);

			fazParagrafoSimples(document, paragraph, run,
					"Em garantia do integral cumprimento de todas as obrigações,"
							+ " principais e acessórias, assumidas pelo EMITENTE, as Partes"
							+ " resolvem celebrar a presente Cédula de Crédito Bancário, a qual"
							+ " se regerá pelas seguintes cláusulas e condições: ",
					false, ParagraphAlignment.BOTH);

			fazParagrafoSimples(document, paragraph, run, "2.	DAS CARACTERÍSTICAS DA OPERAÇÃO DE CRÉDITO", true);

			valorPorExtenso.setNumber(this.objetoCcb.getValorCredito());
			geraParagrafoComposto(document, paragraph, run, run2, "2.1. Valor do Crédito: ",
					CommonsUtil.formataValorMonetario(this.objetoCcb.getValorCredito(), "R$ ") + " (" + valorPorExtenso.toString() + ");",
					true, false);

			valorPorExtenso.setNumber(this.objetoCcb.getCustoEmissao());
			geraParagrafoComposto(document, paragraph, run, run2, "2.1.1. Custo de Emissão: ",
					CommonsUtil.formataValorMonetario(this.objetoCcb.getCustoEmissao(), "R$ ") + " (" + valorPorExtenso.toString()
							+ "), e será pago pelo EMITENTE na data"
							+ " de emissão desta CCB, sendo o mesmo deduzido no ato da liberação do recurso"
							+ " que entrará a crédito na Conta Corrente descrita no item 2.5 desta CCB, e"
							+ " será devido por conta da guarda, manutenção e atualização de dados cadastrais,"
							+ " bem como permanente e contínua geração de dados relativos ao cumprimento dos"
							+ " direitos e obrigações decorrentes deste instrumento;",
					true, false);

			valorPorExtenso.setNumber(this.objetoCcb.getValorIOF());
			geraParagrafoComposto(document, paragraph, run, run2,
					"2.1.2. Valor do Imposto sobre Operações Financeiras (IOF): ",
					CommonsUtil.formataValorMonetario(this.objetoCcb.getValorIOF(), "R$ ") + " (" + valorPorExtenso.toString()
							+ "), conforme apurado na Planilha"
							+ " de Cálculo (Anexo I), calculado nos termos da legislação vigente"
							+ " na data de ocorrência do fato gerador, tendo como base de cálculo"
							+ " o Valor do Crédito mencionado no item 2.1;",
					true, false);

			valorPorExtenso.setNumber(this.objetoCcb.getValorDespesas());
			geraParagrafoComposto(document, paragraph, run, run2,
					"2.1.3. Valor destinado ao pagamento de despesas acessórias (devidas a terceiros): ",
					CommonsUtil.formataValorMonetario(this.objetoCcb.getValorDespesas(), "R$ ") + " (" + valorPorExtenso.toString() + ") conforme anexo II;", true,
					false);
			
			geraParagrafoComposto(document, paragraph, run, run2,
					"2.1.3.1 ", "Os valores mencionados no ANEXO II expressam estimativas,"
							+ " sendo que caso haja necessidade de complementação para quitação,"
							+ " o(a) EMITENTE autoriza desde já e independentemente de notificação,"
							+ " que seja realizado o desconto destes valores do montante líquido a"
							+ " ser liberado, bem como, caso os valores sejam menores no momento da"
							+ " quitação dos débitos, o CREDOR irá realizar o depósito da diferença"
							+ " na Conta indicada no item 2.5.", true, false);

			valorPorExtenso.setNumber(this.objetoCcb.getValorLiquidoCredito());
			geraParagrafoComposto(document, paragraph, run, run2, "2.1.4. Valor Líquido do Crédito: ",
					"O valor líquido do crédito concedido é de "
							+ CommonsUtil.formataValorMonetario(this.objetoCcb.getValorLiquidoCredito(), "R$ ") + "" + " ("
							+ valorPorExtenso.toString() + "), após o desconto do Custo de Emissão,"
							+ " IOF e Despesas Acessórias desta CCB;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "2.1.5.",
					" O EMITENTE está ciente e concorda que é de sua responsabilidade"
							+ " o pagamento dos valores indicados nos itens supramencionados, bem "
							+ "como os relativos aos tributos e demais despesas que incidam ou venham"
							+ " a incidir sobre a operação, inclusive as que façam necessária para o "
							+ "registro da garantia real perante a circunscrição imobiliária competente.",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "2.1.6.",
					" O EMITENTE concorda que o valor relativo ao IOF será incorporado à"
							+ " sua dívida confessada, sendo pago nos mesmos termos do parcelamento"
							+ " do saldo devedor em aberto.",
					true, false);

			fazParagrafoSimplesSemReturn(document, paragraph, run, "2.2.	Encargos Financeiros:", true);

			geraParagrafoCompostoSemReturn(document, paragraph, run, run2, "(X) Pré-fixado,",
					" calculado com base no ano de 365 dias;", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "(X) Pós-fixado: ",
					"atualização dos valores pela variação mensal do Índice Nacional "
							+ "de Preços ao Consumidor Amplo – IPCA/IBGE, apurado a partir da data"
							+ " de emissão até a efetiva quitação da CCB, sendo esta atualização "
							+ "condição essencial do presente negócio, que o saldo devedor e o valor"
							+ " de cada uma das parcelas serão atualizados monetária e mensalmente, de"
							+ " acordo com o índice de atualização referido;",
					true, false);

			fazParagrafoSimplesSemReturn(document, paragraph, run, "2.3. Taxa de Juros Efetiva: ", true);

			geraParagrafoCompostoSemReturn(document, paragraph, run, run2, "Mês: ",
					CommonsUtil.formataValorTaxa(this.objetoCcb.getTaxaDeJurosMes()) + "%", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "Ano: ",
					CommonsUtil.formataValorTaxa(this.objetoCcb.getTaxaDeJurosAno()) + "%", true, false);

			fazParagrafoSimplesSemReturn(document, paragraph, run, "2.4. Custo Efetivo Total (“CET”):", true);

			geraParagrafoCompostoSemReturn(document, paragraph, run, run2, "Mês: ", CommonsUtil.formataValorTaxa(this.objetoCcb.getCetMes()) + "%",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "Ano: ", CommonsUtil.formataValorTaxa(this.objetoCcb.getCetAno()) + "%",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "2.5. Forma de Liberação do Crédito: ",
					"O CREDOR realizará o crédito na Conta Corrente nº " + this.objetoCcb.getContaCorrente() + "," + " Agência nº "
							+ this.objetoCcb.getAgencia() + ", BANCO " + this.objetoCcb.getNumeroBanco() + " – " + this.objetoCcb.getNomeBanco() + ", em até 5 (cinco)"
							+ " dias úteis após o cumprimento das condições precedentes estabelecidas "
							+ "na cláusula 4.4 abaixo;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "2.6. Forma de pagamento: ",
					"O EMITENTE realizará o pagamento, nos termos do Anexo "
							+ "I desta CCB, em conta corrente do CREDOR ou a quem este indicar; ",
					true, false);

			numeroPorExtenso.setNumber(CommonsUtil.bigDecimalValue(this.objetoCcb.getNumeroParcelasPagamento()));
			valorPorExtenso.setNumber(this.objetoCcb.getMontantePagamento());
			geraParagrafoComposto(document, paragraph, run, run2, "2.7. Fluxo de Pagamento (Juros e Amortização): ",
					this.objetoCcb.getNumeroParcelasPagamento() + " (" + numeroPorExtenso.toString() + ")"
							+ " parcelas mensais, sendo a 1ª parcela com vencimento em "
							+ CommonsUtil.formataData(this.objetoCcb.getVencimentoPrimeiraParcelaPagamento(), "dd/MM/yyyy")
							+ " e a última com vencimento " + "em "
							+ CommonsUtil.formataData(this.objetoCcb.getVencimentoUltimaParcelaPagamento(), "dd/MM/yyyy")
							+ ", corrigidas pela variação"
							+ " mensal do IPCA/IBGE, totalizando, na data de emissão desta CCB," + " o montante de "
							+ CommonsUtil.formataValorMonetario(this.objetoCcb.getMontantePagamento(), "R$ ") + " ("
							+ valorPorExtenso.toString() + "), conforme ANEXO I;",
					true, false);

			numeroPorExtenso.setNumber(CommonsUtil.bigDecimalValue(this.objetoCcb.getNumeroParcelasMIP()));
			valorPorExtenso.setNumber(this.objetoCcb.getMontanteMIP());
			geraParagrafoComposto(document, paragraph, run, run2,
					"2.7.1. Valor e Fluxo de Pagamento do Seguro de Morte e Invalidez Permanente (MIP): ",
					this.objetoCcb.getNumeroParcelasMIP() + " (" + numeroPorExtenso.toString() + ") parcelas mensais,"
							+ " sendo a 1ª parcela com vencimento em "
							+ CommonsUtil.formataData(this.objetoCcb.getVencimentoPrimeiraParcelaMIP(), "dd/MM/yyyy") + " "
							+ "e a última com vencimento em "
							+ CommonsUtil.formataData(this.objetoCcb.getVencimentoUltimaParcelaMIP(), "dd/MM/yyyy") + ", corrigidas"
							+ " pela variação mensal do IPCA/IBGE, totalizando, na data de emissão "
							+ "desta CCB, o montante de " + CommonsUtil.formataValorMonetario(this.objetoCcb.getMontanteMIP(), "R$ ") + " ("
							+ valorPorExtenso.toString() + "), conforme ANEXO I. ",
					true, false);

			numeroPorExtenso.setNumber(CommonsUtil.bigDecimalValue(this.objetoCcb.getNumeroParcelasDFI()));
			valorPorExtenso.setNumber(this.objetoCcb.getMontanteDFI());
			geraParagrafoComposto(document, paragraph, run, run2,
					"2.7.2. Valor e Fluxo de Pagamento do Seguro de Danos Físicos ao Imóvel (DFI): ",
					this.objetoCcb.getNumeroParcelasDFI() + " (" + numeroPorExtenso.toString() + ") parcelas"
							+ " mensais, sendo a 1ª parcela com vencimento em "
							+ CommonsUtil.formataData(this.objetoCcb.getVencimentoPrimeiraParcelaDFI(), "dd/MM/yyyy") + " "
							+ "e a última com vencimento em "
							+ CommonsUtil.formataData(this.objetoCcb.getVencimentoUltimaParcelaDFI(), "dd/MM/yyyy") + ", corrigidas pela"
							+ " variação mensal do IPCA/IBGE, totalizando, na data de emissão desta CCB,"
							+ " o montante de " + CommonsUtil.formataValorMonetario(this.objetoCcb.getMontanteDFI(), "R$ ") + " ("
							+ valorPorExtenso.toString() + "), conforme ANEXO I.",
					true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2,
					"2.7.3. Tarifa mensal de administração do contrato: ","Será cobrado"
							+ " mensalmente o valor de R$ 25,00 (vinte e cinco reais)"
							+ " a título de tarifa para administração do contrato.",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "2.8. ",
					"A atualização pela variação mensal do Índice Nacional"
							+ " de Preços ao Consumidor Amplo – IPCA/IBGE será devida"
							+ " desde o momento da emissão desta CCB, independentemente "
							+ "da data ajustada para o pagamento da 1ª parcela.",
					true, false);

			porcentagemPorExtenso.setNumber(this.objetoCcb.getTarifaAntecipada());
			String tarifaAntecipadastr = porcentagemPorExtenso.toString();
			if(CommonsUtil.semValor(this.objetoCcb.getTarifaAntecipada())) {
				tarifaAntecipadastr = "Zero";
			}
			geraParagrafoComposto(document, paragraph, run, run2, "2.9. Tarifa de Liquidação Antecipada: ",
					CommonsUtil.formataValorTaxa(this.objetoCcb.getTarifaAntecipada()) + "% (" + tarifaAntecipadastr + " por cento);",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "2.10. Data de Emissão: ",
					CommonsUtil.formataData(this.objetoCcb.getDataDeEmissao(), "dd/MM/yyyy") + ";", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "2.11. Data de Vencimento: ",
					CommonsUtil.formataData(this.objetoCcb.getVencimentoUltimaParcelaPagamento(), "dd/MM/yyyy") + ";", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "2.12. Praça de Pagamento: ", "São Paulo/SP.", true,
					false);

			fazParagrafoSimples(document, paragraph, run, "3. DAS GARANTIAS", true);

			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("3.1. ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			if(this.objetoCcb.isTerceiroGarantidor()) {
				run2.setText("Em garantia do fiel, integral e pontual cumprimento "
						+ "de todas as obrigações assumidas na presente CCB, o(s) TERCEIRO(S)"
						+ " GARANTIDOR(ES) aliena(m) fiduciariamente ao CREDOR o(s) bem(ens) "
						+ "imóvel(eis), de sua propriedade, bem(ns) com a(s) seguinte(s) "
						+ "descrição(ões):");
			} else {
				run2.setText("Em garantia do fiel, integral e pontual "
						+ "cumprimento de todas as obrigações assumidas na presente CCB,"
						+ " o EMITENTE aliena fiduciariamente ao CREDOR o(s) bem(ens)"
						+ " imóvel(eis), de sua propriedade, bem(ns) com a(s) seguinte(s) "
						+ "descrição(ões):");
			}
			run2.setBold(false);

			int iImagem = 0;
			for (UploadedFile imagem : filesList) {
				run3 = paragraph.createRun();
				run3.addCarriageReturn();
				this.populateFiles(iImagem);
				run3.addPicture(this.getBis(), fileTypeInt, fileName.toLowerCase(), Units.toEMU(400), Units.toEMU(300));
				run3.addCarriageReturn();
				iImagem++;
			}

			run4 = paragraph.createRun();
			run4.setFontSize(12);
			run4.removeCarriageReturn();
			run4.setText("Objeto da matrícula nº " + this.objetoCcb.getNumeroImovel() + " "
					+ "(“Bem Imóvel” ou “Imóvel”), registrada perante o " + this.objetoCcb.getCartorioImovel()
					+ " Cartório de Registro de Imóveis da " + "Comarca de " + this.objetoCcb.getCidadeImovel() + " – " + this.objetoCcb.getUfImovel()
					+ " (“RGI”), nos termos" + " e condições anuídos pelas Partes no Instrumento Particular "
					+ "de Alienação Fiduciária Bem Imóvel (“Termo de Garantia”), o "
					+ "qual faz parte desta CCB como parte acessória e inseparável.");
			run4.addCarriageReturn();

			geraParagrafoComposto(document, paragraph, run, run2, "3.2. ",
					"Se solteiro(a), viúvo(a), divorciado(a) ou separado(a) "
							+ "judicialmente, declara, sob responsabilidade civil e criminal, "
							+ "que o imóvel aqui objetivado não foi adquirido na constância de "
							+ "união estável prevista na Lei nº 9.278, de 10/05/96 e no Código Civil, "
							+ "razão pela qual é seu único e exclusivo proprietário.",
					true, false);

			fazParagrafoSimples(document, paragraph, run, "3.3.	Seguros:", true);

			geraParagrafoComposto(document, paragraph, run, run2, "3.3.1. ",
					"O CREDOR Fica autorizado neste ato a contratar em nome do(s) EMITENTE, os seguros para "
							+ "cobertura dos riscos de morte e invalidez permanente e de danos físicos ao(s) Imóvel(is) descrito(s) "
							+ "na cláusula 3 acima, cujos prêmios deverão ser pagos mensalmente. O CREDOR, ou quem vier a substituí-lo, "
							+ "será nomeado beneficiário das respectivas apólices/certificados de seguro, e receberá o capital segurado"
							+ " ou indenização em caso de sinistro para utilização dos valores daí decorrentes na liquidação total"
							+ " ou parcial das obrigações de pagamento oriundas do presente instrumento. O valor do prêmio dos"
							+ " referidos seguros será reajustado conforme definido em apólice e poderá ser revisto e alterado"
							+ " desde o início da contratação, ou seja, na elaboração da proposta de empréstimo ou financiamento,"
							+ " até a liquidação integral da CCB, de acordo com as regras estabelecidas na respectiva"
							+ " apólice de seguros que são estipuladas pela companhia seguradora. ",
					true, false);
			
			if(this.objetoCcb.isTerceiroGarantidor()) {
				geraParagrafoComposto(document, paragraph, run, run2, "3.3.1.1. ",
						"Assim, declaram-se cientes o EMITENTE e o(s) TERCEIRO(S) "
						+ "GARANTIDOR(ES) que qualquer alteração nas condições "
						+ "inicialmente informadas para a contratação, tais como,"
						+ " mas não se limitando, por exemplo, a(s) idade(s) do(s) "
						+ "proponente(s), poderá refletir em modificação no prêmio dos"
						+ " seguros a serem contratados para a devida formalização deste"
						+ " empréstimo com garantia imobiliária. ",
						true, false);
			} else {
				geraParagrafoComposto(document, paragraph, run, run2, "3.3.1.1. ",
						"Assim, declara-se ciente o EMITENTE que qualquer alteração"
								+ " nas condições inicialmente informadas para a contratação,"
								+ " tais como, mas não se limitando, por exemplo, a(s) idade(s)"
								+ " do(s) proponente(s), poderá refletir em modificação no prêmio"
								+ " dos seguros a serem contratados para a devida formalização deste"
								+ " empréstimo com garantia imobiliária.",
						true, false);
			}

			geraParagrafoComposto(document, paragraph, run, run2, "3.3.1.2. ",
					"Declara ainda o EMITENTE e o(s) TERCEIROS(S) GARANTIDOR(ES) que:", true, false,
					ParagraphAlignment.LEFT);

			CTNumbering cTNumbering = CTNumbering.Factory.parse(cTAbstractNumBulletXML);
			CTAbstractNum cTAbstractNum = cTNumbering.getAbstractNumArray(0);

			// CTAbstractNum cTAbstractNum = getAbstractNumber(STNumberFormat.LOWER_LETTER);
			XWPFAbstractNum abstractNum = new XWPFAbstractNum(cTAbstractNum);
			XWPFNumbering numbering = document.createNumbering();
			BigInteger abstractNumID = numbering.addAbstractNum(abstractNum);
			BigInteger numID = numbering.addNum(abstractNumID);

			geraParagrafoBulletList(document, paragraph, run, numID,
					"tem(têm) ciência e concorda(m) integralmente com os termos das condições gerais "
							+ "ora apresentadas com relação ao Seguro de pessoa com cobertura de Morte e "
							+ "Invalidez Permanente por Acidente (MIP) e ao Seguro de danos com cobertura de "
							+ "Danos Físicos ao Imóvel (DFI), tendo pleno conhecimento de todas as suas "
							+ "coberturas e riscos excluídos ",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID,
					"os próprios EMITENTE ou seus beneficiários, herdeiros ou sucessores, deverão "
							+ "comunicar ao CREDOR e a Seguradora, imediatamente e por escrito, a ocorrência "
							+ "de qualquer sinistro, bem como, qualquer evento suscetível de agravar "
							+ "consideravelmente o risco coberto, sob pena de perder o direito à indenização se "
							+ "for provado que silenciou de má-fé;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID,
					"autoriza(m), desde já, de forma expressa, irrevogável e inequívoca, que a "
							+ "Seguradora realize o levantamento de informações médicas em hospitais, clínicas "
							+ "e/ou consultórios, bem como, que solicite a realização de perícia médica quando	necessária.",
					false);

			geraParagrafoComposto(document, paragraph, run, run2, "3.3.2. ",
					"Se, em decorrência de sinistro, " + "a Seguradora por qualquer motivo "
							+ "desembolsar indenização em valor " + "insuficiente a quitação do saldo"
							+ " devedor do empréstimo objeto deste " + "instrumento, ficará(ão) o EMITENTE ou seu(s)"
							+ " herdeiro(s) e/ou sucessor(es) obrigado(s) a efetiva"
							+ " liquidação do saldo devedor remanescente perante o CREDOR.",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "3.3.3. ",
					"Na hipótese da cláusula acima, no caso de não liquidação do"
							+ " saldo remanescente pelos DEVEDOR(ES), seus herdeiros e"
							+ " sucessores a qualquer título, sobre estes incidirá os encargos"
							+ " moratórios previstos na cláusula 6, bem como a respectiva "
							+ "execução da garantia pelo CREDOR ou quem vier a substituí-lo.",
					true, false);

			fazParagrafoSimples(document, paragraph, run, "4. DA CONCESSÃO DO CRÉDITO", true);

			geraParagrafoComposto(document, paragraph, run, run2, "4.1. ",
					"O EMITENTE pagará por esta CCB ao CREDOR ou a quem este "
							+ "vier a indicar, em moeda corrente nacional, o Valor do "
							+ "Crédito acrescido de encargos, conforme expressamente "
							+ "indicado na cláusula 2 acima, calculados desde a data da "
							+ "emissão desta CCB pelo EMITENTE até a data do seu respectivo "
							+ "pagamento integral ao CREDOR, acrescidos, quando aplicáveis,"
							+ " dos encargos moratórios, conforme disposto na presente CCB; ",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "4.2. ",
					"O EMITENTE tem expresso conhecimento de que os juros"
							+ " ajustados para o empréstimo a que se refere à presente"
							+ " CCB são calculados, sempre e invariavelmente, de forma"
							+ " diária e capitalizada, conforme permitido pela legislação" + " aplicável; ",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "4.3. ",
					"O EMITENTE declara que tomou conhecimento do cálculo do CET"
							+ " indicado no item 2.4 acima, previamente à operação de "
							+ "empréstimo contratada por meio da presente CCB, através "
							+ "de planilha de cálculo que lhe foi apresentada pelo CREDOR; ",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "4.4. ",
					"O EMITENTE concorda que a Liberação do Crédito "
							+ "prevista na cláusula 2.5 está condicionada ao cumprimento"
							+ " das seguintes condições precedentes, de forma cumulativa"
							+ " e satisfatória para o CREDOR:",
					true, false);

			CTNumbering cTNumbering2 = CTNumbering.Factory.parse(cTAbstractNumBulletXML_NoLeft);
			CTAbstractNum cTAbstractNum2 = cTNumbering2.getAbstractNumArray(0);
			XWPFAbstractNum abstractNum2 = new XWPFAbstractNum(cTAbstractNum2);
			XWPFNumbering numbering2 = document.createNumbering();
			BigInteger abstractNumID2 = numbering2.addAbstractNum(abstractNum2);
			BigInteger numID2 = numbering2.addNum(abstractNumID2);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Entrega de todas as vias da CCB e Instrumento Particular"
							+ " de Alienação Fiduciária de Bem(ns) Imóvel(eis) em Garantia e "
							+ "Outras Avenças, devidamente assinadas pelas Partes com todas as "
							+ "firmas reconhecidas ou mediante assinatura eletrônica compatível"
							+ " com os padrões do ICP-BRASIL;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Entrega da matrícula atualizada com o registro da alienação "
							+ "fiduciária do imóvel descrito na cláusula 3 dessa CCB" + " em favor do CREDOR.",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"No caso de haver processo judicial em andamento,"
							+ " a ser quitado na forma do ANEXO II da presente CCB,"
							+ " concorda o EMITENTE que a liberação do crédito estará"
							+ " condicionada à comprovação do protocolo do acordo "
							+ "assinado pelas partes litigantes nos autos, o qual deve"
							+ " conter obrigatoriamente a menção à quitação e o pedido" + " extinção do processo.",
					false);
			
			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Emissão da Certidão Negativa de Débitos – CND Municipal atualizada, em que não"
					+ " conste débitos de Imposto Predial e Territorial Urbano – IPTU.",
					false);
			
			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Emissão da Certidão Negativa de Débitos – CND dos débitos condominiais.",
					false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.4.1. ",
					"Caso haja parcelamento judicial ou administrativo vigente para pagamento dos débitos de IPTU "
					+ "ou condomínio que torne possível a emissão da CND, pelo fato da existência da dívida ainda"
					+ " representar risco à garantia, é condição necessária à Liberação do Crédito que toda a dívida"
					+ " seja quitada.",
					true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.4.2. ",
					"Caso existam débitos municipais de IPTU ou condomínio, parcelados ou não, ajuizados ou não,"
					+ " o(a) EMITENTE autoriza o desconto destes valores para quitação das dívidas nos termos do"
					+ " ANEXO II, caso em que se compromete a encaminhar ao CREDOR as respectivas guias para pagamento.",
					true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.4.3. ",
					"Caso a Certidão de Débitos seja positiva, a exclusivo critério do CREDOR a operação "
					+ "poderá ser cancelada, devendo o EMITENTE reembolsar os valores gastos até o registro"
					+ " da garantia.",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "4.5. ",
					"O(A) EMITENTE concorda que, caso as condições precedentes "
							+ "acima não sejam cumpridas no prazo de até 30 (trinta) "
							+ "dias corridos contados da emissão da CCB, o referido título"
							+ " poderá, a critério do CREDOR, ser considerado cancelado,"
							+ " deixando de surtir efeitos, obrigações, direitos e deveres "
							+ "às Partes, devendo o(a) EMITENTE reembolsar todos os gastos "
							+ "despendidos pelo CREDOR. ",
					true, false);

			fazParagrafoSimples(document, paragraph, run, "5. DA FORMA DE PAGAMENTO E PRAZO", true);

			geraParagrafoComposto(document, paragraph, run, run2, "5.1. Depósito em Conta Corrente: ",
					"Fica o EMITENTE instruído pelo CREDOR, em caráter irrevogável e irretratável,"
							+ " a depositar em conta corrente nos termos da cláusula 2.6 acima,"
							+ " de titularidade do CREDOR ou a quem este vier a indicar (“Conta Corrente”),"
							+ " os valores relativos às parcelas da CCB indicadas no ANEXO I, "
							+ "acrescidas dos respectivos encargos, inclusive debitar os valores"
							+ " correspondentes a mora, IOF, tarifas e demais despesas aqui previstas.",
					true, false);

			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("5.2. ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("O(s) EMITENTE(S) declara(m)-se ciente(s) de que o pagamento "
					+ "das parcelas mensais e os encargos, conforme valores e prazos "
					+ "estabelecidos no ANEXO I dessa CCB, ");
			run2.setBold(false);

			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("não estão vinculados à data de liberação do Valor Líquido do Crédito");
			run.setBold(true);

			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.removeCarriageReturn();
			run2.setText(", devendo tais encargos serem pagos a partir da data ajustada" + " no item ");

			run = paragraph.createRun();
			run.setFontSize(12);
			run.removeCarriageReturn();
			run.setText("2.7");
			run.setBold(true);

			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.removeCarriageReturn();
			run2.setText(
					", sob pena de incidência de atualização monetária, juros e multa, de acordo com o quanto disposto na cláusula 6.");
			run2.addCarriageReturn();

			/*
			 * XWPFRun run5 = paragraph.createRun(); run5.setFontSize(12);
			 * run5.removeCarriageReturn(); run5.setText("2.7"); run5.addCarriageReturn();
			 * 
			 * XWPFRun run6 = paragraph.createRun(); run6.setFontSize(12);
			 * run6.removeCarriageReturn(); run6.
			 * setText(", sob pena de incidência de atualização monetária, juros e multa, de acordo com o quanto disposto na cláusula 6."
			 * ); run6.addCarriageReturn();
			 */

			geraParagrafoComposto(document, paragraph, run, run2, "5.3. ",
					"Na hipótese de haver parcelas mensais vencidas e não pagas na "
							+ "data de liberação do Valor Líquido do Crédito, o(s) DEVEDOR(ES),"
							+ " desde já, autoriza(m) o CREDOR a descontar desse valor,"
							+ " descrito na cláusula 2.1.4, eventual montante devido em"
							+ " razão do não pagamento das parcelas mensais ajustadas"
							+ " conforme ANEXO I, incluindo encargos moratórios conforme"
							+ " previsto na Cláusula 6 dessa CCB.",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "5.4. ",
					"Os pagamentos devidos ao CREDOR, previstos na presente CCB,"
							+ " serão efetuados via boleto bancário a ser encaminhado ao"
							+ " endereço físico ou eletrônico do EMITENTE constante do item"
							+ " II da cláusula 1. Fica estabelecido que a falta de recebimento "
							+ "do aviso de cobrança ou boleto bancário não exime o EMITENTE de "
							+ "efetuar os pagamentos previstos nesta CCB, nem constitui justificativa"
							+ " para atraso em sua liquidação ou isenção de penalidades moratórias,"
							+ " cabendo ao EMITENTE entrar em contato com o CREDOR, ou quem o substituir,"
							+ " em tempo hábil, visando à obtenção de boleto para pagamento.",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "5.5. ",
					"Em razão do acordado nesta cédula quanto ao valor, prestações,"
							+ " parcelas, reajustes e atualizações, o pagamento de qualquer"
							+ " prestação atualizada de maneira diversa da estabelecida nesta CCB,"
							+ " inclusive perante terceiros autorizados a recebê-las,"
							+ " não implicará na quitação do respectivo débito ou " + "repactuação da dívida.",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "5.6. ",
					"Qualquer diferença verificada entre os créditos"
							+ " efetivados na conta corrente do CREDOR e a sistemática"
							+ " de cálculos dos valores estabelecidos nesta CCB,"
							+ " deverá ser imediatamente liquidada pelo EMITENTE no "
							+ "prazo máximo de 48 (quarenta e oito) horas, contadas"
							+ " do aviso que o CREDOR lhe dirigir neste sentido,"
							+ " caso em que, não realizado o pagamento após esse " + "prazo, estará em mora.",
					true, false);

			fazParagrafoSimples(document, paragraph, run, "6. DO ATRASO NO PAGAMENTO E ENCARGOS MORATÓRIOS", true);

			geraParagrafoComposto(document, paragraph, run, run2, "6.1. ",
					"Na hipótese de inadimplemento ou mora, o EMITENTE estará "
							+ "obrigado a pagar ao CREDOR ou a quem este indicar, cumulativamente,"
							+ " além da quantia correspondente à dívida em aberto, os seguintes " + "encargos: ",
					true, false);

			cTNumbering2 = CTNumbering.Factory.parse(cTAbstractNumBulletXML_NoLeft_NoHanging_bold);
			cTAbstractNum2 = cTNumbering2.getAbstractNumArray(0);
			abstractNum2 = new XWPFAbstractNum(cTAbstractNum2);
			numbering2 = document.createNumbering();
			abstractNumID2 = numbering2.addAbstractNum(abstractNum2);
			numID2 = numbering2.addNum(abstractNumID2);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Juros remuneratórios nos mesmos percentuais das taxas "
							+ "contratadas nessa CCB, calculados a partir do vencimento "
							+ "da(s) parcela(s) em aberto até a data do efetivo pagamento;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Juros de mora à razão de 1% a.m. (um por cento ao mês), "
							+ "calculados a partir do vencimento da(s) parcela(s) em aberto"
							+ " até a data do efetivo pagamento;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Multa contratual, de natureza não compensatória, de 2% (dois por cento)"
							+ " incidente sobre o montante atualizado (juros remuneratórios e juros de mora)"
							+ " total do débito apurado e não pago;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Na hipótese do CREDOR vir a ser compelido a recorrer"
							+ " a meios administrativos ou judiciais para receber o seu crédito,"
							+ " as despesas de cobrança, estas limitadas a 20% (vinte por cento)"
							+ " sobre o valor do saldo devedor e, havendo procedimento judicial, "
							+ "custas processuais e honorários advocatícios, estes fixados judicialmente.",
					false);

			fazParagrafoSimples(document, paragraph, run, "7. DO VENCIMENTO ANTECIPADO", true);

			geraParagrafoComposto(document, paragraph, run, run2, "7.1. ",
					"Além das demais hipóteses estabelecidas em lei e nesta CCB,"
							+ " a dívida aqui contraída pelo EMITENTE, a partir do primeiro "
							+ "dia útil da liberação do Valor do Crédito, reputar-se-á "
							+ "antecipadamente vencida, facultando-se ao credor da CCB exigir "
							+ "a imediata e integral satisfação de seu crédito, independentemente "
							+ "de aviso ou notificação judicial ou extrajudicial de qualquer espécie,"
							+ " na ocorrência de qualquer das hipóteses previstas nos artigos 333 e "
							+ "1.425 do Código Civil Brasileiro e, ainda, nas seguintes hipóteses:",
					true, false);

			cTNumbering2 = CTNumbering.Factory.parse(cTAbstractNumBulletXML_NoLeft_NoHanging_bold2);
			cTAbstractNum2 = cTNumbering2.getAbstractNumArray(0);
			abstractNum2 = new XWPFAbstractNum(cTAbstractNum2);
			numbering2 = document.createNumbering();
			abstractNumID2 = numbering.addAbstractNum(abstractNum2);
			numID2 = numbering2.addNum(abstractNumID2);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se ocorrer inadimplemento de qualquer obrigação assumida pelo EMITENTE, "
							+ "em consonância com as cláusulas e condições aqui estabelecidas, "
							+ "principalmente no que tange ao pagamento das parcelas devidas "
							+ "em decorrências do empréstimo a ele concedido por força da " + "presente CCB;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2, "Se ocorrer inadimplemento "
					+ "de qualquer obrigação assumida pelo EMITENTE, e/ou quaisquer sociedades"
					+ " direta ou indiretamente ligadas, coligadas, controladoras ou controladas"
					+ " pelo EMITENTE (doravante denominadas “AFILIADAS”), inclusive no exterior,"
					+ " de suas obrigações decorrentes de outros contratos, empréstimos ou descontos"
					+ " celebrados com o CREDOR e/ou quaisquer sociedades, direta ou indiretamente,"
					+ " ligadas, coligadas, controladoras ou controladas pelo credor da CCB ou seu "
					+ "cessionário, e/ou com terceiros, e/ou rescisão ou declaração de vencimento "
					+ "antecipado dos respectivos documentos, por culpa do EMITENTE e/ou de quaisquer " + "AFILIADAS;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se for protestado qualquer "
							+ "título de responsabilidade do EMITENTE em razão do inadimplemento de obrigação "
							+ "cujo valor individual ou em conjunto seja igual ou superior a R$ 100.000,00 "
							+ "(cem mil reais), sem que a justificativa para tal medida tenha sido apresentada"
							+ " ao credor da CCB, no prazo que lhe tiver sido solicitada ou, sendo ou tendo sido"
							+ " apresentada a justificativa, se esta não for considerada satisfatória pelo CREDOR,"
							+ " ressalvado o protesto tirado por erro ou má-fé do respectivo portador;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se O EMITENTE for inscrito no "
							+ "Cadastro de Emitentes de Cheques sem Fundo – CCF, ou, ainda, constem informações "
							+ "negativas a seu respeito no Sistema de Informações de Crédito do Banco Central,"
							+ " que, a critério do credor da CCB, possa afetar a sua capacidade de cumprir as "
							+ "obrigações assumidas na presente CCB ou no Termo de Garantia;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se o EMITENTE e/ou quaisquer AFILIADAS,"
							+ " inclusive no exterior, tornarem-se insolventes, requerer(em) ou tiver(em), falência, "
							+ "insolvência civil, recuperação judicial ou extrajudicial requerida ou decretada, sofrer "
							+ "intervenções, regime de administração especial temporária, ou liquidação judicial ou"
							+ " extrajudicial;",
					false);
			if(this.objetoCcb.isTerceiroGarantidor()) {
				geraParagrafoBulletList(document, paragraph, run, numID2, "Se for comprovada a falsidade de qualquer"
						+ " declaração, informação ou documento que houver sido, respectivamente, firmada, prestada ou "
						+ "entregue pelo EMITENTE e TERCEIRO(S) GARANTIDOR(ES), ao CREDOR;", false);
			} else {
				geraParagrafoBulletList(document, paragraph, run, numID2, "Se for comprovada a falsidade de qualquer"
						+ " declaração, informação ou documento que houver sido, respectivamente, firmada, prestada ou"
						+ " entregue pelo EMITENTE, ao CREDOR;", false);
			}

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se o EMITENTE sofrer qualquer (quaisquer) "
							+ "medida(s) judicial(ais) ou extrajudicial(ais) que por qualquer forma, possa(m) afetar "
							+ "negativamente os créditos do empréstimo e/ou as garantias conferidas ao credor da CCB;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se as garantias fidejussórias, "
							+ "ora e/ou que venham a ser eventualmente convencionadas, por qualquer fato atinente"
							+ " ao seu objeto ou prestador se tornar inábeis, impróprias, ou insuficientes para "
							+ "assegurar o pagamento da dívida, e desde que não sejam substituídas, ou complementadas,"
							+ " quando solicitada por escrito pelo CREDOR ou a quem este vier a indicar;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se, sem o expresso consentimento "
							+ "do credor da CCB ocorrer a transferência a terceiros dos direitos e obrigações do"
							+ " EMITENTE previstos nesta CCB e no Termo de Garantia;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se, sem o expresso consentimento do "
							+ "credor da CCB ocorrer alienação, cessão, doação ou transferência, por qualquer meio, "
							+ "de bens, ativos ou direitos de propriedade do EMITENTE e/ou de quaisquer AFILIADAS, "
							+ "quando aplicável que, no entendimento do credor, possam levar ao descumprimento das "
							+ "obrigações previstas na presente CCB;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se, sem o expresso consentimento do"
							+ " credor da CCB, o EMITENTE, quando aplicável, tiver total ou parcialmente, o seu"
							+ " controle acionário, direto ou indireto, cedido, transferido ou por qualquer outra"
							+ " forma alienado ou modificado;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se ocorrer mudança ou alteração do"
							+ " objeto social do EMITENTE, quando aplicável, de forma a alterar as atividades"
							+ " principais ou a agregar às suas atividades novos negócios que possam representar "
							+ "desvios em relação às atividades atualmente desenvolvidas;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se, sem o expresso consentimento do"
							+ " credor da CCB, o EMITENTE sofrer, durante a vigência desta CCB, qualquer operação"
							+ " de transformação, incorporação, fusão ou cisão;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID2,
					"Se a garantia real objeto do"
							+ " Instrumento Particular de Alienação Fiduciária de Bem Imóvel não for efetivamente"
							+ " registrada junto ao RGI no prazo de até 30(trinta) dias corridos a contar da"
							+ " emissão desta CCB; e",
					false);
			if(this.objetoCcb.isTerceiroGarantidor()) {
				geraParagrafoBulletList(document, paragraph, run, numID2,
						"o)	Se o Bem Imóvel objeto da garantia à presente CCB apresentar quaisquer características,"
						+ " ônus ou gravame ou caso ocorra qualquer ato ou omissão por parte de EMITENTE e/ou TERCEIRO(S)"
						+ " GARANTIDOR(ES), que impeça a efetiva constituição da garantia regulada nos termos Instrumento"
						+ " Particular de Alienação Fiduciária de Bem Imóvel.",
						false);
			} else {
				geraParagrafoBulletList(document, paragraph, run, numID2,
						"Se o Bem Imóvel objeto" + " da garantia à presente CCB apresentar quaisquer características, ônus "
								+ "ou gravame ou caso ocorra qualquer ato ou omissão por parte do EMITENTE,"
								+ " que impeça a efetiva constituição da garantia regulada nos termos Instrumento"
								+ " Particular de Alienação Fiduciária de Bem Imóvel.",
						false);
			}

			geraParagrafoComposto(document, paragraph, run, run2, "7.2. ",
					"No caso de falta de pagamento"
							+ " de qualquer parcela(s) na(s) data(s) de seu(s) respectivo(s) vencimento(s),"
							+ " o CREDOR poderá, por mera liberdade e sem que tal situação caracterize novação"
							+ " ou alteração das condições estabelecidas nesta CCB – optar pela cobrança somente"
							+ " da(s) parcela(s) devida(s) em aberto, comprometendo-se o EMITENTE,"
							+ " em contrapartida, a liquidá-la(s) imediatamente quando instado(s) para tal,"
							+ " sob pena de ultimar-se o vencimento antecipado de toda a dívida; ",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "7.2.1. ",
					"Declarado o vencimento "
							+ "antecipado de toda a dívida, o credor da CCB apresentará ao EMITENTE notificação "
							+ "contendo o saldo devedor final, incluindo principal, juros, encargos, despesas e "
							+ "tributos, a ser pago pelo EMITENTE no dia útil imediatamente subsequente ao "
							+ "recebimento de referida notificação, sob pena de ser considerado em mora, "
							+ "independentemente de qualquer aviso ou notificação judicial ou extrajudicial;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "7.2.2. ", "Na declaração de vencimento "
					+ "antecipado da dívida pelo credor da CCB, além do valor apurado nos termos do item 7.2.1 acima,"
					+ " serão acrescidos os encargos previstos na cláusula 6 às parcelas vencidas. ", true, false);

			fazParagrafoSimples(document, paragraph, run, "8. LIQUIDAÇÃO ANTECIPADA", true);

			geraParagrafoComposto(document, paragraph, run, run2, "8.1. ", "O EMITENTE poderá liquidar"
					+ " antecipadamente, total ou parcialmente, suas obrigações decorrentes desta CCB, "
					+ "desde que previamente acordado, de modo satisfatório ao credor da CCB e ao EMITENTE,"
					+ " as condições de tal liquidação antecipada. Para tanto, o EMITENTE deverá encaminhar"
					+ " ao credor da CCB, solicitação por escrito, com antecedência mínima de 10 (dez) dias úteis;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "8.1.1. ",
					"Se indicada a Tarifa de "
							+ "Liquidação Antecipada no item 2.10 acima, o EMITENTE, desde já, se obriga a pagar "
							+ "ao CREDOR, na data da liquidação, a Tarifa de Liquidação Antecipada sobre o valor"
							+ " efetivamente pago antecipadamente, a título de indenização pelos custos relacionados"
							+ " com a quebra de captação de recursos;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "8.1.2. ", "Nas situações em que as despesas "
					+ "associadas à contratação realizada por meio desta CCB forem também objeto de financiamento "
					+ "ou empréstimo, essas despesas integrarão igualmente a operação para apuração do valor "
					+ "presente para fins de amortização, total ou parcial, da dívida ainda em aberto;", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "8.1.3. ", "Caso haja saldo devedor a ser"
					+ " pago acrescentar-se-ão, às prestações em atraso, e as penalidades previstas neste instrumento,"
					+ " bem como os juros remuneratórios calculados pro rata die e quaisquer outras despesas de "
					+ "responsabilidade do EMITENTE nos termos desta CCB;", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "8.1.4. ",
					"Sempre que for necessário,"
							+ " a apuração do saldo devedor do EMITENTE será realizada pelo CREDOR mediante planilha "
							+ "de cálculo, que constituirá documento integrante e inseparável da presente CCB. ",
					true, false);

			fazParagrafoSimples(document, paragraph, run, "9.	DECLARAÇÕES", true);

			geraParagrafoComposto(document, paragraph, run, run2, "9.1. ",
					"As Partes signatárias, cada uma por si, declaram e garantem que: ", true, false);

			cTNumbering = CTNumbering.Factory.parse(cTAbstractNumBulletXML_NoHanging_bold);
			cTAbstractNum = cTNumbering.getAbstractNumArray(0);
			abstractNum = new XWPFAbstractNum(cTAbstractNum);
			numbering = document.createNumbering();
			abstractNumID = numbering.addAbstractNum(abstractNum);
			numID = numbering.addNum(abstractNumID);

			geraParagrafoBulletListComposta(document, paragraph, run, run2,
					"Possui plena capacidade e "
							+ "legitimidade para celebrar a presente CCB, realizar todas as operações e cumprir todas "
							+ "as obrigações aqui assumidas",
					", bem como dos instrumentos de garantia, tendo tomado todas as medidas"
							+ " de natureza societária e outras eventualmente necessárias para autorizar "
							+ "a sua celebração, implementação e cumprimento de todas as obrigações " + "constituídas;",
					true, false, numID, UnderlinePatterns.SINGLE);

			geraParagrafoBulletList(document, paragraph, run, numID, "A celebração desta CCB e do Termo de Garantia,"
					+ " e o cumprimento das obrigações de cada uma das Partes: (a) não violam qualquer disposição contida"
					+ " nos seus documentos societários; (b) não violam qualquer lei, regulamento, decisão judicial, "
					+ "administrativa ou arbitral, aos quais a respectiva Parte esteja vinculada; (c) não exigem qualquer"
					+ " consentimento, ação ou autorização, prévia ou posterior, de terceiros;", false);

			geraParagrafoBulletList(document, paragraph, run, numID, "Esta CCB e o Termo de Garantia são validamente "
					+ "celebrados e constituem obrigação legal, válida, vinculante e exequível contra cada uma das Partes,"
					+ " de acordo com os seus termos;", false);

			geraParagrafoBulletList(document, paragraph, run, numID, "Cada Parte está apta a cumprir as obrigações ora"
					+ " previstas nesta CCB e nos instrumentos de garantia, e agirá em relação aos mesmos de boa-fé e com"
					+ " lealdade;", false);

			geraParagrafoBulletList(document, paragraph, run, numID, "Nenhuma Parte depende economicamente da outra;",
					false);

			geraParagrafoBulletList(document, paragraph, run, numID, "Nenhuma das Partes se encontra em estado de"
					+ " necessidade ou sob coação para celebrar esta CCB e/ou quaisquer contratos e compromissos a "
					+ "ela relacionados e acessórios ", false);

			geraParagrafoBulletList(document, paragraph, run, numID, "As discussões sobre o objeto contratual, "
					+ "crédito, encargos incidentes e obrigações acessórias, oriundos desta CCB e dos instrumentos"
					+ " de garantia, foram feitas, conduzidas e implementadas por livre iniciativa das Partes;", false);

			geraParagrafoBulletList(document, paragraph, run, numID,
					"O CREDOR e EMITENTE, são pessoas devidamente estruturadas,"
							+ " qualificadas e capacitadas para entender a estrutura financeira e jurídica objeto desta CCB, e estão "
							+ "acostumadas a celebrar, em seus respectivos campos de atuação, títulos e instrumentos de garantia semelhantes"
							+ " aos previstos nesta CCB, não havendo entre as Partes qualquer relação de hipossuficiência ou ainda natureza de"
							+ " consumo na relação aqui tratada.",
					false);
			
			geraParagrafoBulletList(document, paragraph, run, numID,
					"EMITENTE(S), TERCEIRO(S) GARANTIDOR(ES), AVALISTA(S) e ANUENTE(S) declaram expressamente,"
					+ " sob pena de responsabilidade civil e criminal, que não possuem nenhum negócio jurídico"
					+ " pactuado entre si ou com terceiros que tenha relação com emissão desta CCB ou com a garantia"
					+ " oferecida, estando cientes de que nada poderá ser oponível ao credor com a finalidade de "
					+ "prejudicar os pagamentos ou a execução da garantia.",
					false);

			fazParagrafoSimples(document, paragraph, run, "10.	DAS DISPOSIÇÕES FINAIS", true);

			geraParagrafoComposto(document, paragraph, run, run2, "10.1. Tolerância: ",
					"A tolerância não implica perdão, renúncia, novação ou alteração da dívida ou das condições aqui previstas e o pagamento do principal, mesmo sem ressalvas, não será considerado ou presumido a quitação dos encargos. Dessa forma, as Partes acordam que qualquer prática diversa da aqui pactuada, mesmo que reiterada, não poderá ser interpretada como novação;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.1.1 Declarações Específicas: ",
					"O EMITENTE declara que: "
							+ "(i) está ciente que o surto do novo coronavírus (COVID-19), reconhecido oficialmente como pandemia pela "
							+ "Organização Mundial de Saúde (OMS), é anterior à celebração desta CCB e que a pandemia não apresenta "
							+ "caráter de imprevisibilidade, extraordinariedade ou superveniência no presente momento, (ii) reconhece "
							+ "que tais eventos não configuram caso fortuito ou de força maior, conforme definição do artigo 393 do Código"
							+ " Civil, e (iii) compromete-se a honrar as obrigações assumidas nos termos desta CCB; ",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2,
					"10.2. Comunicação aos Serviços de Proteção ao Crédito: ",
					"Na hipótese de ocorrer descumprimento de qualquer obrigação ou atraso no pagamento, o CREDOR ou a quem este"
							+ " vier a indicar poderá comunicar o fato a qualquer serviço de proteção ao crédito, como Serasa Experian ou"
							+ " qualquer outro órgão encarregado de cadastrar atraso nos pagamentos e o descumprimento de obrigações "
							+ "contratuais, informando o nome do EMITENTE.",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.3. Reforço de Garantias: ", "O CREDOR poderá, "
					+ "a qualquer tempo, exigir reforço de garantias, ficando estipulado o prazo de 5 (cinco) dias úteis "
					+ "contados da data de sua solicitação, pelo CREDOR, por carta sob protocolo ou registro postal, para "
					+ "que o EMITENTE providencie o respectivo reforço, sob pena do imediato vencimento da presente CCB, "
					+ "independentemente de interpelação judicial ou notificação judicial ou extrajudicial;", true,
					false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.4. Alteração da CCB: ",
					"A presente CCB somente poderá "
							+ "ser alterada mediante aditivo próprio devidamente assinado pelas Partes; ",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2,
					"10.5. Comunicação ao Sistema de Informação de Créditos " + "(“SCR”): ",
					"O CREDOR, neste ato, comunica ao EMITENTE que a presente operação de empréstimo, será "
							+ "registrada no SCR gerido pelo Banco Central do Brasil (“BACEN”), que tem por finalidade subsidiar"
							+ " o BACEN para fins de supervisão de risco de crédito a que estão expostas as instituições"
							+ " financeiras e ainda intercambiar informações entre as instituições financeiras; ",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.5.1 ",
					"O EMITENTE poderá ter acesso aos dados "
							+ "constantes em seu SCR, por meio de central de atendimento ao público do BACEN;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.5.2 ",
					"Em caso de discordância quanto às informações"
							+ " do SCR, bem como pedidos de correções, o EMITENTE deverá entrar em contato com a Ouvidoria do CREDOR,"
							+ " nos termos do item 10.11 abaixo;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.5.3 ",
					"O EMITENTE autoriza o CREDOR ou a quem este "
							+ "indicar, a qualquer tempo: a: (i) efetuar consultas ao Sistema de Informações de Crédito – SCR – do Banco"
							+ " Central do Brasil (“SCR”), nos termos da Resolução nº 3.658, do Conselho Monetário Nacional, de 17.12.2008,"
							+ " conforme alterada e os serviços de proteção ao crédito SPC, Serasa e outras em que o CREDOR seja "
							+ "cadastrado; (ii) fornecer ao Banco Central do Brasil informações sobre esta CCB, para integrar o SCR; "
							+ "e (iii) proceder conforme disposições que advierem de novas exigências feitas pelo Banco Central do Brasil"
							+ " ou autoridades. ",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.6. Efeitos do CCB: ",
					"As Partes convencionam que as "
							+ "obrigações pecuniárias estipuladas na presente CCB passam a vigorar a partir de sua respectiva emissão;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.7. ", "Se qualquer item ou cláusula desta CCB "
					+ "vier a ser considerado ilegal, inexequível ou, por qualquer motivo, ineficaz, todos os demais itens"
					+ " e cláusulas continuarão em vigor, plenamente válidos e eficazes. As Partes, desde já, se comprometem"
					+ " a negociar, no menor prazo possível, item ou cláusula que, conforme o caso, venha a substituir o item"
					+ " ou cláusula ilegal, inexequível ou ineficaz. Nessa negociação, deverá ser considerado o objetivo das "
					+ "Partes na data de assinatura dessa CCB, bem como o contexto no qual o item ou cláusula ilegal, inexequível"
					+ " ou ineficaz foi inserido.", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.8. Irrevogabilidade e Irretratabilidade: ",
					"A presente CCB é firmada em caráter irrevogável e irretratável, obrigando as Partes, seus "
							+ "herdeiros e/ou sucessores; ",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.9. Base de Dados: ",
					"O EMITENTE declara e concorda "
							+ "expressamente que ao firmar a presente CCB passará a fazer parte integrante da base de clientes do CREDOR,"
							+ " ou a quem este vier a indicar, autorizando, assim através das informações cadastrais que o CREDOR, ou a "
							+ "quem este vier a indicar, possui a respeito dele o oferecimento de produtos e/ou serviços;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.10. Ouvidoria: ",
					"O EMITENTE declara ter ciência de que o "
							+ "CREDOR disponibiliza um canal de Ouvidoria para que sejam feitas sugestões e/ou reclamações através do telefone"
							+ " (11) 3810-9333;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.11. Legislação: ", "Aplica-se a presente CCB, as"
					+ " disposições da Lei 10.931, de 02 de agosto de 2004, e posteriores alterações (“Lei 10.931”), declarando"
					+ " o EMITENTE ter conhecimento que a presente CCB é um título executivo extrajudicial e representa dívida "
					+ "em dinheiro, certa, líquida e exigível, seja pela soma nela indicada, seja pelo saldo devedor "
					+ "demonstrado em planilha de cálculo ou nos extratos de Conta Corrente, a serem emitidos consoante "
					+ "o que preceitua a aludida Lei 10.931;", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.11.1  ", "O EMITENTE declara ter ciência que: (i)"
					+ " o CREDOR integra o Sistema Financeiro Nacional, submetendo-se à disciplina e regras pelo Conselho"
					+ " Monetário Nacional e Banco Central do Brasil; e (ii) as taxas de juros cobradas nas operações "
					+ "financeiras realizadas pelo CREDOR, incluindo a presente CCB, não estão submetidas ao limite de"
					+ " 12% (doze por cento) ao ano, como já decidiu o Supremo Tribunal Federal, sendo legítima a "
					+ "cobrança de juros e encargos superiores a esse percentual;", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.11.2 ", "A tolerância, por uma das partes, "
					+ "quanto a alguma demora, atraso ou omissão da outra parte no cumprimento das obrigações ajustadas"
					+ " neste instrumento, ou a não aplicação, na ocasião oportuna, das penalidades previstas "
					+ "será considerada mera liberalidade, não se configurando como precedente ou novação "
					+ "contratual", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.11.3 ", "Se vier a tornar impossível"
					+ " a aplicação das regras previstas nesta Cédula, seja por força de eventual caráter cogente"
					+ " de imperativos legais que venham a ser baixados, seja em decorrência de ausência de consenso"
					+ " entre as Partes, considerar-se-á rescindida esta CCB e, em consequência, a dívida dela"
					+ " oriunda se considerará antecipadamente vencida, da mesma forma e com os mesmos efeitos "
					+ "previstos, efetivando-se a cobrança de juros “pro-rata temporis”; ", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.12. Cessão ou Endosso: ", "O CREDOR fica "
					+ "expressamente autorizado a qualquer tempo, a seu exclusivo critério e independentemente da "
					+ "prévia anuência do EMITENTE, a ceder a terceiros os direitos de crédito que detém em razão desta CCB,"
					+ " bem como a transferi-la a terceiros mediante endosso da “via negociável”, sendo certo que "
					+ "a cessão ou o endosso não caracterizarão violação do sigilo bancário em relação ao EMITENTE."
					+ " Ocorrendo a cessão ou o endosso, o cessionário/endossatário desta CCB assumirá automaticamente"
					+ " a qualidade de credor desta CCB, passando a ser titular de todos os direitos e obrigações dela "
					+ "decorrentes; ", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.12.1 ", "Após o endosso pelo CREDOR desta CCB,"
					+ " o EMITENTE desde já, reconhece a validade da emissão e do endosso desta CCB de forma física ou eletrônica, "
					+ "o que é feito com base no art. 889, §3º, do Código Civil. ", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.12.2",
					" Na hipótese de transferência da presente CCB,"
							+ " o seu novo titular ficará automaticamente sub-rogado em todos os direitos e garantias que "
							+ "cabiam ao CREDOR original, independentemente de qualquer formalidade, passando a ter acesso "
							+ "livre e direto a todas as informações relacionadas à operação bancária e respectivas garantias,"
							+ " a exemplo de duplicatas e/ou direitos creditórios e/ou quaisquer outras garantias eventualmente "
							+ "constituídas, reconhecendo o EMITENTE que o novo titular da CCB possui o inequívoco direito de "
							+ "acompanhar detidamente todo o andamento da operação bancária, motivo pelo qual, da mesma forma,"
							+ " estará automaticamente sub-rogado a consultar as informações consolidadas em seu nome, no SCR, "
							+ "SERASA – Centralização de Serviços os Bancos S.A. e quaisquer  outros órgãos, entidades ou empresas,"
							+ " julgados pertinentes pelo CREDOR, permanecendo válida a presente autorização durante todo o tempo"
							+ " em que subsistir em aberto e não liquidadas as obrigações decorrentes da presente CCB. ",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.12.3 ",
					"A cessão dos direitos sempre compreenderá"
							+ " os acessórios, títulos, instrumentos que os representam e anexos. De tal forma, ao formalizar a cessão "
							+ "dos direitos de crédito, por meio de Contrato de Cessão, o CREDOR estará cedendo, automaticamente,"
							+ " todos os direitos, privilégios, preferências, prerrogativas, garantias e ações, legal e contratualmente"
							+ " previstas, que sejam inerentes ao direito de crédito cedido, inclusive: (i) o direito de receber "
							+ "integralmente o seu valor, acrescido dos juros, das multas, da atualização monetária e/ou demais encargos"
							+ " remuneratórios e/ou moratórios; (ii) o direito de ação e o de protesto em face do respectivo EMITENTE,"
							+ " para exigir o cumprimento da obrigação de pagamento, ou visando resguardar qualquer direito; (iii)"
							+ " as garantias eventualmente existentes, sejam reais ou pessoais; e (iv) o direito de declarar o direito "
							+ "de crédito vencido antecipadamente, nas hipóteses contratadas com o EMITENTE e naquelas previstas na"
							+ " legislação aplicável;",
					true, false);
			if(this.objetoCcb.isTerceiroGarantidor()) {
				geraParagrafoComposto(document, paragraph, run, run2, "10.12.4 ", "O EMITENTE e/ou TERCEIRO(S) GARANTIDOR(ES),"
						+ " está(ão) integralmente ciente(s) e de acordo com o seguinte: (i) qualquer litígio ou questionamento,"
						+ " judicial ou extrajudicial, que possa vir a ser ajuizado, deverá ser ajuizado, àquele portador"
						+ " endossatário da CCB na data do ajuizamento do litígio ou questionamento; e (ii) o ajuizamento "
						+ "de qualquer ação, judicial ou extrajudicial, pelo EMITENTE e/ou TERCEIRO(S) GARANTIDOR(ES), "
						+ "contra o CREDOR, após o mesmo ter endossado esta CCB para terceiro, o EMITENTE e/ou TERCEIRO(S)"
						+ " GARANTIDOR(ES), estará(ão) sujeito(s) ao pagamento de indenização por perdas e danos, e "
						+ "ressarcimento de todo e quaisquer custos e despesas que o CREDOR venha a incorrer "
						+ "(incluindo honorários advocatícios) para defesa de seus direitos no respectivo litígio;", true, false);
			} else {
				geraParagrafoComposto(document, paragraph, run, run2, "10.12.4 ", "O EMITENTE, está integralmente ciente(s)"
						+ " e de acordo com o seguinte: (i) qualquer litígio ou questionamento, judicial ou extrajudicial, que possa "
						+ "vir a ser ajuizado, deverá ser ajuizado, àquele portador endossatário da CCB na data do ajuizamento do "
						+ "litígio ou questionamento; e (ii) o ajuizamento de qualquer ação, judicial ou extrajudicial, pelo EMITENTE,"
						+ " contra o CREDOR, após o mesmo ter endossado esta CCB para terceiro, o EMITENTE, estará sujeito ao "
						+ "pagamento de indenização por perdas e danos, e ressarcimento de todo e quaisquer custos e despesas"
						+ " que o CREDOR venha a incorrer (incluindo honorários advocatícios) para defesa de seus direitos no "
						+ "respectivo litígio;", true, false);
			}

			geraParagrafoComposto(document, paragraph, run, run2, "10.13. Emissão de Certificados de CCB: ",
					"O CREDOR, "
							+ "ou a quem este vier a indicar, poderá emitir certificados de CCB com lastro no presente título, podendo"
							+ " negociá-los livremente no mercado;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.13.1 ",
					"Caso haja a emissão do certificado referido "
							+ "no item 10.13, a presente CCB ficará custodiada em instituição financeira autorizada, a qual passará a "
							+ "proceder às cobranças dos valores devidos, junto ao EMITENTE;",
					true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.13.2 ", "O EMITENTE desde já se declara de acordo"
					+ " com a emissão do certificado referido no item 10.13, obrigando-se a atender às solicitações da instituição"
					+ " custodiante, bem como, aceitam a cessão de crédito, independentemente de qualquer aviso "
					+ "ou formalidade;", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.14.	Foro: ", "Ajustam as Partes que será sempre"
					+ " competente para conhecer e dirimir qualquer questão oriunda ou decorrente da presente CCB, o foro"
					+ " da comarca de São Paulo capital com a exclusão de qualquer outro, por mais privilegiado que seja,"
					+ " reservando-se o credor da CCB o direito de optar, a seu exclusivo critério, pelo foro da sede"
					+ " do EMITENTE ou, ainda, pelo foro da situação dos bens dados em garantia;", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.15. ", "Em caso de contratação eletrônica, "
					+ "as Partes ratificam que admitem como válido, para fins de comprovação de autoria e integridade,"
					+ " a assinatura e informações constantes no presente documento, as quais foram capturadas de forma"
					+ " eletrônica e utilizadas nesta Cédula, constituindo título executivo extrajudicial nos termos "
					+ "do artigo 28 da Lei nº 10.931 2004 e para todos os fins de direito, ainda que seja estabelecida "
					+ "com assinatura eletrônica ou certificação fora dos padrões ICP-BRASIL, conforme disposto pelo art."
					+ " 10 da Medida Provisória nº 2.200/2001.", true, false);

			geraParagrafoComposto(document, paragraph, run, run2, "10.16. ", "A presente CCB é emitida e firmada "
					+ "em 2 (duas) vias, constando na 1ª via a expressão “Via Negociável” e nas demais, a expressão "
					+ "“Via Não Negociável”. ", true, false);

			fazParagrafoSimples(document, paragraph, run,
					"São Paulo, SP, " + this.objetoCcb.getDataDeEmissao().getDate() + " de "
							+ CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase() + " de "
							+ (this.objetoCcb.getDataDeEmissao().getYear() + 1900) + ".",
					false);

			fazParagrafoSimples(document, paragraph, run,
					"(O final desta página foi intencionalmente deixado em branco)", false, ParagraphAlignment.CENTER);

			paragraph = document.createParagraph();
			paragraph.setPageBreak(true);

			fazParagrafoSimples(document, paragraph, run, "(Segue a página de assinaturas)", false,
					ParagraphAlignment.CENTER);

			paragraph = document.createParagraph();
			paragraph.setPageBreak(true);
			
			paragraph = document.createParagraph();	
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("(Página de assinaturas da Cédula de Crédito "
					+ "Bancário nº " + this.objetoCcb.getNumeroCcb() + ", emitida por "+ this.objetoCcb.getNomeEmitente() +", CPF/MF nº "+ this.objetoCcb.getCpfEmitente() +", em favor de "
					+ "BMP SOCIEDADE DE CRÉDITO DIRETO S.A., CNPJ/ MF sob nº 34.337.707/0001-00,"
					+ " em "+ CommonsUtil.formataData(this.objetoCcb.getDataDeEmissao(), "dd/MM/yyyy" )+".)");
			run.setBold(false);
			run.setItalic(true);
			run.addCarriageReturn();

			XWPFTable table = document.createTable();

			paragraph = document.createParagraph();
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			paragraph.setAlignment(ParagraphAlignment.LEFT);

			table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(6000));
			table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(2500));

			// create first row
			XWPFTableRow tableRow1 = table.getRow(0);

			tableRow1.getCell(0).setParagraph(paragraph);
			run = tableRow1.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("____________________________________   ");
			run.setBold(false);
			run.addBreak();

			run2 = tableRow1.getCell(0).getParagraphArray(0).createRun();
			run2.setFontSize(12);
			run2.setText("BMP SOCIEDADE DE CRÉDITO DIRETO S.A");
			run2.setBold(true);
			run2.addBreak();

			run4 = tableRow1.getCell(0).getParagraphArray(0).createRun();
			run4.setFontSize(12);
			run4.setText("CREDOR");
			run4.setBold(false);

			tableRow1.addNewTableCell();

			tableRow1.getCell(1).setParagraph(paragraph);

			run = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("____________________________________ ");
			run.setBold(false);
			run.addBreak();

			run2 = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run2.setFontSize(12);
			run2.setText(this.objetoCcb.getNomeEmitente());
			run2.setBold(true);
			run2.addBreak();

			run3 = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run3.setFontSize(12);
			run3.setText(" ");
			run3.setBold(true);
			run3.addBreak();

			run4 = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run4.setFontSize(12);
			run4.setText("EMITENTE");
			run4.setBold(false);

			XWPFTableRow tableRow2 = table.createRow();

			if (this.objetoCcb.getListaParticipantes().size() > 1) {
				tableRow2.getCell(0).setParagraph(paragraph);
				tableRow2.getCell(1).setParagraph(paragraph);
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
				run4 = tableRow2.getCell(0).getParagraphArray(0).createRun();
				run4.setFontSize(12);
				run4.addBreak();
				run4.setText("Testemunhas");
				run4.setBold(false);
				run4.addBreak();
				run4.setText("____________________________________");

				run4 = tableRow2.getCell(1).getParagraphArray(0).createRun();
				run4.setFontSize(12);
				for (int i = 0; i <= qtdePessoasEsquerdo; i++) {
					run4.addBreak();
					run4.addBreak();
					run4.addBreak();
				}
				run4.setText("____________________________________   ");
				run4.setBold(false);

			} else {
				tableRow2.getCell(0).setParagraph(paragraph);
				run = tableRow2.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.addBreak();
				run.setText("Testemunhas");
				run.setBold(false);
				run.addBreak();
				run.setText("____________________________________");

				tableRow2.getCell(1).setParagraph(paragraph);
				run = tableRow2.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.addBreak();
				run.addBreak();
				run.setText("____________________________________   ");
				run.setBold(false);
			}
			
			XWPFTableRow tableRow3 = table.createRow();
			tableRow3.getCell(0).setParagraph(paragraph);
			run = tableRow3.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("Nome:  " + this.objetoCcb.getNomeTestemunha1());
			run.setBold(false);

			tableRow3.getCell(1).setParagraph(paragraph);
			run = tableRow3.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("Nome:  " + this.objetoCcb.getNomeTestemunha2());
			run.setBold(false);

			XWPFTableRow tableRow4 = table.createRow();
			tableRow4.getCell(0).setParagraph(paragraph);
			run = tableRow4.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("RG:  " + this.objetoCcb.getRgTestemunha1());
			run.setBold(false);

			tableRow4.getCell(1).setParagraph(paragraph);
			run = tableRow4.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("RG:  " + this.objetoCcb.getRgTestemunha2());
			run.setBold(false);

			XWPFTableRow tableRow5 = table.createRow();
			tableRow5.getCell(0).setParagraph(paragraph);
			run = tableRow5.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("CPF:  " + this.objetoCcb.getCpfTestemunha1());
			run.setBold(false);

			tableRow5.getCell(1).setParagraph(paragraph);
			run = tableRow5.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("CPF:  " + this.objetoCcb.getCpfTestemunha2());
			run.setBold(false);

			CTTblPr tblpro = table.getCTTbl().getTblPr();

			CTTblBorders borders = tblpro.addNewTblBorders();
			borders.addNewBottom().setVal(STBorder.NONE);
			borders.addNewLeft().setVal(STBorder.NONE);
			borders.addNewRight().setVal(STBorder.NONE);
			borders.addNewTop().setVal(STBorder.NONE);
			// also inner borders
			borders.addNewInsideH().setVal(STBorder.NONE);
			borders.addNewInsideV().setVal(STBorder.NONE);

			paragraph = document.createParagraph();
			paragraph.setPageBreak(true);

			paragraph = document.createParagraph();
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("ANEXO I");
			run.addCarriageReturn();
			run.setText("CÉDULA DE CRÉDITO BANCÁRIO Nº " + this.objetoCcb.getNumeroCcb());
			run.addCarriageReturn();
			run.setText("PLANILHA DE CÁLCULO");
			run.setBold(true);

			XWPFFooter footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);

			paragraph = footer.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.RIGHT);
			run = paragraph.createRun();
			run.setFontSize(10);
			run.setColor("0000ff");
			run.setBold(true);
			run.setText("pág. ");

			run = paragraph.createRun();
			run.setFontSize(10);
			run.setColor("0000ff");
			run.setBold(true);
			run.getCTR().addNewFldChar()
					.setFldCharType(org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType.BEGIN);

			run = paragraph.createRun();
			run.setFontSize(10);
			run.setColor("0000ff");
			run.setBold(true);
			run.getCTR().addNewInstrText().setStringValue("PAGE \\* MERGEFORMAT");

			run = paragraph.createRun();
			run.setFontSize(10);
			run.setColor("0000ff");
			run.setBold(true);
			run.getCTR().addNewFldChar()
					.setFldCharType(org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType.END);

			/*
			 * for (XWPFParagraph p : document.getParagraphs()) { List<XWPFRun> runs =
			 * p.getRuns(); if (runs != null) { for (XWPFRun r : runs) { String text =
			 * r.getText(0); adicionarEnter(text, r); } } }
			 */

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			document.write(out);
			document.close();
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
					FacesContext.getCurrentInstance());
			
		
			String nomeSemvirgula = this.objetoCcb.getNomeEmitente();
			if(nomeSemvirgula.contains(",")) {
				nomeSemvirgula = nomeSemvirgula.replace(",", "");
		    }
			gerador.open(String.format("Galleria Bank - Modelo_CCB %s.docx", ""));
			gerador.feed(new ByteArrayInputStream(out.toByteArray()));
			gerador.close();

			criarCcbNosistema();	
		
		} catch (JDBCException jdbce) {
		    jdbce.getSQLException().getNextException().printStackTrace();
		} catch (Exception e) {
			e.getCause().printStackTrace();
		} 

		return null;
	}

	public StreamedContent geraAFDinamica() throws IOException {
		try {
			XWPFDocument document = new XWPFDocument();				
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			XWPFRun run;

			XWPFParagraph paragraph = document.createParagraph();
			run = paragraph.createRun();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run.setText("INSTRUMENTO PARTICULAR DE ALIENAÇÃO FIDUCIÁRIA DE BEM(NS) IMÓVEL(EIS) EM GARANTIA E OUTRAS AVENÇAS");
			XWPFRun run2 = paragraph.createRun();
			XWPFRun run3 = paragraph.createRun();
			XWPFRun run4 = paragraph.createRun();			
			
			run.setFontSize(12);
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			run.addCarriageReturn();
			run.addCarriageReturn();
			
			fazParagrafoSimples(document, paragraph, run, "Pelo presente instrumento particular firmado"
					+ " nos termos do artigo 38 da Lei nº 9.514/1997, com a redação que lhe foi dada "
					+ "pelo artigo 53 da Lei nº 11.076/2004, as Partes: ", false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "De um lado, na qualidade de outorgante(s) ", "FIDUCIANTE(s),", false, true);
			
			int iParticipante = 1;
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {
				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")) {
					if(CommonsUtil.semValor(this.objetoCcb.getNomeEmitente())) {
						this.objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
					}
					
					if(CommonsUtil.semValor(this.objetoCcb.getCpfEmitente())) {
						if(!CommonsUtil.semValor(participante.getPessoa().getCpf())) {
							this.objetoCcb.setCpfEmitente(participante.getPessoa().getCpf());
						} else {
							this.objetoCcb.setCpfEmitente(participante.getPessoa().getCnpj());
						}
					}
					
					if(participante.isFiduciante()) {
						classeEmitente = "FIDUCIANTE";
					} else {
						classeEmitente = "DEVEDOR";
					}
				}
				
				if(participante.isFiduciante()) {
					participante.setTipoParticipante("FIDUCIANTE");
				
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				paragraph.setSpacingBetween(1);

				run = paragraph.createRun();
				run.setFontSize(12);
				run.setText(iParticipante + ")");
				run.addTab();
				run.setText(" " + participante.getPessoa().getNome() + ", ");
				run.setBold(true);

				run2 = paragraph.createRun();
				if (!participante.isEmpresa()) {
					geraParagrafoPF(run2, participante);
					run2.addCarriageReturn();

				} else {
					run2.setFontSize(12);
					PagadorRecebedor pessoa = participante.getPessoa();

					String socios = "";
					if (participante.getSocios().size() > 1) {
						socios = "pelos seus sócios, ";
					} else {
						if (participante.getSocios().iterator().next().isFeminino()) {
							socios = "pela sua única sócia, ";
						} else {
							socios = "pelo seu único sócio, ";
						}
					}

					run2.setText(participante.getTipoEmpresa() + ", devidamente inscrito no CNPJ sob n° "
							+ pessoa.getCnpj() + ", com sede em " + pessoa.getEndereco() + ", " + "n° "
							+ pessoa.getNumero() + ", Sala " + participante.getSalaEmpresa() + ", " + pessoa.getBairro()
							+ ", " + pessoa.getCidade() + " - " + pessoa.getEstado() + ", CEP " + pessoa.getCep()
							+ "; neste ato representada " + socios);

					for (CcbParticipantes sociosParticipante : participante.getSocios()) {
						XWPFRun runSocios = paragraph.createRun();
						runSocios.setFontSize(12);
						runSocios.setText(" " + sociosParticipante.getPessoa().getNome() + ", ");
						runSocios.setBold(true);
						XWPFRun runSociosNome = paragraph.createRun();
						geraParagrafoPF(runSociosNome, sociosParticipante);
						runSociosNome.addCarriageReturn();
					}
				}

				iParticipante++;
				} else {
					participante.setTipoParticipante("DEVEDOR");
				}
			}
			
			fazParagrafoSimples(document, paragraph, run, "De outro lado, na qualidade de outorgada fiduciária, ", false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(iParticipante + ")");
			run.addTab();
			run.setText("BMP SOCIEDADE DE CRÉDITO DIRETO S.A., ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("instituição financeira, inscrita no CNPJ/MF sob"
					+ " nº 34.337.707/0001-00, com sede na Av. Paulista,"
					+ " 1765, 1º Andar, CEP 01311-200, São Paulo, SP, neste ato,"
					+ " representada na forma do seu Estatuto Social (“");
			run2.setBold(false);
			
			iParticipante++;
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIÁRIA");
			run.setUnderline(UnderlinePatterns.SINGLE); 
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("”, e quando em conjunto com o ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIANTE(S), ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("doravante denominadas “");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText( "PARTES");
			run.setUnderline(UnderlinePatterns.SINGLE); 
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("” e, isoladamente, “");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("PARTE");
			run.setUnderline(UnderlinePatterns.SINGLE); 
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("”).");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {
				if(!participante.isFiduciante()) {
	
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				paragraph.setSpacingBetween(1);

				run = paragraph.createRun();
				run.setFontSize(12);
				run.setText(iParticipante + ")");
				run.addTab();
				run.setText("DEVEDOR: " + participante.getPessoa().getNome() + ", ");
				run.setBold(true);

				run2 = paragraph.createRun();
				if (!participante.isEmpresa()) {
					geraParagrafoPF(run2, participante);
					run2.addCarriageReturn();

				} else {
					run2.setFontSize(12);
					PagadorRecebedor pessoa = participante.getPessoa();

					String socios = "";
					if (participante.getSocios().size() > 1) {
						socios = "pelos seus sócios, ";
					} else {
						if (participante.getSocios().iterator().next().isFeminino()) {
							socios = "pela sua única sócia, ";
						} else {
							socios = "pelo seu único sócio, ";
						}
					}

					run2.setText(participante.getTipoEmpresa() + ", devidamente inscrito no CNPJ sob n° "
							+ pessoa.getCnpj() + ", com sede em " + pessoa.getEndereco() + ", " + "n° "
							+ pessoa.getNumero() + ", Sala " + participante.getSalaEmpresa() + ", " + pessoa.getBairro()
							+ ", " + pessoa.getCidade() + " - " + pessoa.getEstado() + ", CEP " + pessoa.getCep()
							+ "; neste ato representada " + socios);

					for (CcbParticipantes sociosParticipante : participante.getSocios()) {
						XWPFRun runSocios = paragraph.createRun();
						runSocios.setFontSize(12);
						runSocios.setText(" " + sociosParticipante.getPessoa().getNome() + ", ");
						runSocios.setBold(true);
						XWPFRun runSociosNome = paragraph.createRun();
						geraParagrafoPF(runSociosNome, sociosParticipante);
						runSociosNome.addCarriageReturn();
					}
				}
				
				iParticipante++;
				} 
			}
			
			fazParagrafoSimples(document, paragraph, run, "CONSIDERANDO QUE: ", true);
			
			CTNumbering cTNumbering = CTNumbering.Factory.parse(cTAbstractNumBulletXML_bold);
			CTAbstractNum cTAbstractNum = cTNumbering.getAbstractNumArray(0);

			// CTAbstractNum cTAbstractNum = getAbstractNumber(STNumberFormat.LOWER_LETTER);
			XWPFAbstractNum abstractNum = new XWPFAbstractNum(cTAbstractNum);
			XWPFNumbering numbering = document.createNumbering();
			BigInteger abstractNumID = numbering.addAbstractNum(abstractNum);
			BigInteger numID = numbering.addNum(abstractNumID);
			
			//criarParagrafo(document, paragraph, ParagraphAlignment.BOTH, numID);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Em ");
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(CommonsUtil.formataData(this.objetoCcb.getDataDeEmissao(), "dd/MM/yyyy"));
			run2.setBold(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(" o ");
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("FIDUCIANTE " + this.objetoCcb.getNomeEmitente() );
			run2.setBold(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(" emitiu a(s) Cédula(s) de Crédito Bancário nº ");
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(this.objetoCcb.getNumeroCcb() + " ");
			run2.setBold(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("em favor da ");
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("FIDUCIÁRIA");
			run2.setBold(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(", com as características previstas na Cláusula 3ª abaixo "
					+ "(“CCB(s)”), passando a ser devedora da totalidade do valor principal, juros "
					+ "remuneratórios e encargos, presentes e futuros, principais e acessórios "
					+ "decorrentes do referido título (“Obrigações Garantidas”);");
			run.setBold(false);
			run.addCarriageReturn();
			
			geraParagrafoBulletListComposta(document, paragraph, run, run2, "As obrigações, pecuniárias ou não,"
					+ " previstas na(s) CCB(s) são garantidas pela alienação fiduciária de Imóvel(eis) descrito"
					+ " abaixo bem como registrado(s) perante o "+ this.objetoCcb.getCartorioImovel() +" Cartório de Registro de Imóveis da "
					+ "Comarca de "+ this.objetoCcb.getCidadeImovel() +" – "+ this.objetoCcb.getUfImovel() +" “RGI”, de propriedade do(s) ", "FIDUCIANTE(S).", false, true, numID, UnderlinePatterns.NONE);
			

			geraParagrafoBulletList(document, paragraph, run, numID , "Nos termos da(s) CCB(s), o protocolo da garantia "
					+ "de Alienação Fiduciária junto ao RGI é condição precedente ao seu desembolso devendo o "
					+ "registro ser concluído no prazo de até 30(trinta) dias contados da emissão da CCB sob pena"
					+ " de vencimento antecipado do referido título;", false);
			
			geraParagrafoBulletList(document, paragraph, run, numID , "A presente garantia de Alienação Fiduciária é celebrada"
					+ " sem prejuízo das outras garantias constituídas ou que venham a ser constituídas em favor da(s) CCB(s);", false);
			
			geraParagrafoBulletList(document, paragraph, run, numID , "As Partes dispuseram de tempo e condições adequadas para "
					+ "a avaliação e discussão de todas as cláusulas desta Alienação Fiduciária (abaixo definido), "
					+ "cuja celebração, execução e extinção são pautadas pelos princípios da igualdade, probidade,"
					+ " lealdade e boa-fé.", false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Resolvem, na melhor forma de direito, celebrar o presente ");
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Instrumento Particular de Alienação Fiduciária de Bens Imóveis em Garantia e Outras Avenças ");
			run2.setBold(false);
			run2.setItalic(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("(“");
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Contrato de Alienação Fiduciária");
			run2.setBold(false);
			run2.setItalic(false);
			run2.setUnderline(UnderlinePatterns.SINGLE); 
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("”), que se regerá pelas cláusulas a seguir redigidas e demais disposições,"
					+ " contratuais e legais, aplicáveis. ");
			run.setBold(false);
			run.addCarriageReturn();
			
			fazParagrafoSimples(document, paragraph, run, "CLÁUSULA PRIMEIRA – DO OBJETO", true);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("1.1 ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Em garantia do cumprimento das Obrigações Garantidas, "
					+ "nesta data representadas pela(s) CCB nº ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(this.objetoCcb.getNumeroCcb() + " ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("melhor descritas na clausula 2ª abaixo, o(s) ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIANTE(S) ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("aliena(m) fiduciariamente, em favor da ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIÁRIA, ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("o(s) Imóvel(eis) de sua titularidade e de seguinte descrição: ");
			run2.setBold(false);
			
			int iImagem = 0;
			for (UploadedFile imagem : filesList) {
				run3 = paragraph.createRun();
				run3.addCarriageReturn();
				this.populateFiles(iImagem);
				run3.addPicture(this.getBis(), fileTypeInt, fileName.toLowerCase(), Units.toEMU(400), Units.toEMU(300));
				run3.addCarriageReturn();
				iImagem++;
			}
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Objeto da matrícula nº "+ this.objetoCcb.getNumeroImovel() +" (“Bem Imóvel” ou “Imóvel”), "
					+ "registrada perante o "+ this.objetoCcb.getCartorioImovel() +" Cartório de Registro de Imóveis da "
					+ "Comarca de "+ this.objetoCcb.getCidadeImovel() +" – "+ this.objetoCcb.getUfImovel() +" (");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("“RGI”");
			run.setBold(false);
			run.setItalic(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("(“Bem(ns) Imóvel(eis) ou Imóvel(eis)”) bem conforme identificado no ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Anexo I ");
			run.setBold(true);
			run.setItalic(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("ao presente (“");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Alienação Fiduciária");
			run.setUnderline(UnderlinePatterns.SINGLE); 
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("”). ");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "1.2 ", "Se solteiro(a), viúvo(a), divorciado(a)"
					+ " ou separado(a) judicialmente, declara, sob responsabilidade civil e criminal, que o imóvel "
					+ "aqui objetivado não foi adquirido na constância de união estável prevista na Lei nº 9.278,"
					+ " de 10/05/96 e no Código Civil, razão pela qual é seu único e exclusivo proprietário.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "1.3 ", "O(s) FIDUCIANTE(S), declara(m), "
					+ "sob as penas da lei, que não está(ão) vinculado(s) como empregador(es) ao INSS - Instituto"
					+ " Nacional do Seguro Social, bem como não ser(em) produtor(es) rural(is), não estando, assim,"
					+ " incurso(s) nas restrições da legislação pertinente, dispensando a apresentação de Certidão "
					+ "Negativa de Débitos – CND. Todavia, na hipótese de ser(em) contribuinte(s) desse órgão,"
					+ " declara(m) ciente(s) e responsável(eis) pela apresentação da CND-INSS ao Cartório de "
					+ "Registro de Imóveis.", true, false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("1.4 ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("A transferência da propriedade fiduciária do(s) Imóvel(eis), pelo(s) ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIANTE(S) ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("à ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIÁRIA ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("se opera com o registro desta Alienação Fiduciária no "
					+ "competente Cartório de Registro de Imóveis indicado na "
					+ "descrição acima e subsistirá, durante seu prazo de vigência,"
					+ " até o cumprimento válido e eficaz da totalidade das"
					+ " Obrigações Garantidas. ");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "1.5 ", "Obriga(m)-se o(s) FIDUCIANTE(S),"
					+ " seus herdeiros e sucessores a qualquer título das Partes a providenciar o "
					+ "registro do presente instrumento, com a constituição da Alienação Fiduciária "
					+ "aqui prevista, e averbação da CCB na matrícula do Imóvel objeto da garantia,"
					+ " no prazo de 30 (trinta) dias a contar de sua assinatura, sob pena deste"
					+ " CONTRATO ser considerado automaticamente resolvido, independentemente"
					+ " de qualquer notificação prévia ou outra formalidade, hipótese em que "
					+ "não serão devidas quaisquer indenizações ao(s) EMITENTE(S). Nesta hipótese,"
					+ " o(s) EMITENTE(S) deverá(ão) ressarcir o CREDOR das despesas de custo de"
					+ " emissão da CCB e outras despesas decorrentes desta no prazo máximo de "
					+ "48 (quarenta e oito) horas contadas da data em que for(em) notificado(s) "
					+ "para tanto, sob pena de sofrer(em) execução específica.", true, false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("1.6 ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Em ocorrendo a cessão, endosso ou qualquer outra forma de transferência"
					+ " da(s) CCB(s) e/ou dos créditos dela oriundos à terceiros(“");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Sucessores");
			run.setBold(false);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("”), referidos Sucessores passarão a ser os legítimos titulares e beneficiários"
					+ " da presente Alienação Fiduciária, de forma que toda menção à ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIÁRIA ");
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.NONE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("deverá ser interpretada como referindo-se aos Sucessores"
					+ " (efetivos titulares dos créditos, conforme constante do SNA da CETIP)"
					+ " e sendo certo, ainda, que todas as disposições do presente contrato"
					+ " serão mantidas.");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			fazParagrafoSimples(document, paragraph, run, "CLÁUSULA SEGUNDA – DOS REQUISITOS DO ARTIGO 24º DA LEI 9514/1997", true);
			
			geraParagrafoComposto(document, paragraph, run, run2, "2.1 ", "As Partes declaram, para os fins do artigo 24 da Lei nº 9.514/1997, "
					+ "que as Obrigações Garantidas apresentam as exatas características principais indicadas na abaixo: ", true, false);
			
			
			cTNumbering = CTNumbering.Factory.parse(cTAbstractNumBulletXML_bold_Roman);
			cTAbstractNum = cTNumbering.getAbstractNumArray(0);

			// CTAbstractNum cTAbstractNum = getAbstractNumber(STNumberFormat.LOWER_LETTER);
			abstractNum = new XWPFAbstractNum(cTAbstractNum);
			numbering = document.createNumbering();
			abstractNumID = numbering.addAbstractNum(abstractNum);
			numID = numbering.addNum(abstractNumID);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Obrigação Garantida:");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" Cédula de Crédito Bancário nº " + this.objetoCcb.getNumeroCcb() + " ");
			run2.setBold(false);
			run2.setItalic(true);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Valor do Principal da Dívida:");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" a soma do saldo devedor das Obrigações Garantidas,"
					+ " na data do leilão, nele incluídos os juros convencionais, "
					+ "as penalidades e os demais encargos contratuais conforme"
					+ " termos da clausula 5.7 deste instrumento; ");
			run2.setBold(false);
			run2.setItalic(true);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Valor do Crédito:");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			valorPorExtenso.setNumber(this.objetoCcb.getValorCredito()); 
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" "+ CommonsUtil.formataValorMonetario(this.objetoCcb.getValorCredito(), "R$ ") + " ("+ valorPorExtenso.toString() +");");
			run2.setBold(false);
			run2.setItalic(true);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Condições de Pagamento:");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			numeroPorExtenso.setNumber(CommonsUtil.bigDecimalValue(this.objetoCcb.getNumeroParcelasPagamento()));
			valorPorExtenso.setNumber(this.objetoCcb.getMontantePagamento()); 
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" "+ this.objetoCcb.getNumeroParcelasPagamento() +" ("+ numeroPorExtenso.toString() +") parcelas,"
					+ " sendo a 1ª. parcela com vencimento em "+ CommonsUtil.formataData(this.objetoCcb.getVencimentoPrimeiraParcelaPagamento(), "dd/MM/yyyy")  +""
					+ " e a última parcela com vencimento em "+ CommonsUtil.formataData(this.objetoCcb.getVencimentoUltimaParcelaPagamento(), "dd/MM/yyyy")  +","
					+ " totalizando o montante de "+ CommonsUtil.formataValorMonetario(this.objetoCcb.getMontantePagamento(), "R$ ") +" ("+ valorPorExtenso.toString() +");");
			run2.setBold(false);
			run2.setItalic(true);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Encargos Financeiros:");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			run.addCarriageReturn();

			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("(X) ");
			run2.setBold(false);
			run2.setItalic(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Pré-fixado");
			run.setBold(true);
			run.setItalic(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(", calculado com base no ano de 365 dias;");
			run2.setBold(false);
			run2.setItalic(true);
			run2.addCarriageReturn();
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("(X) ");
			run2.setBold(false);
			run2.setItalic(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Pós-fixado");
			run.setBold(true);
			run.setItalic(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(": atualização dos valores pela variação mensal do Índice "
					+ "Nacional de Preços ao Consumidor Amplo – IPCA/IBGE, apurado "
					+ "a partir da data de emissão até a efetiva quitação da CCB;");
			run2.setBold(false);
			run2.setItalic(true);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Taxa de Juros Efetiva: ");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			run.addCarriageReturn();
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Mes: ");
			run2.setBold(true);
			run2.setItalic(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(CommonsUtil.formataValorTaxa(this.objetoCcb.getTaxaDeJurosMes()) + "%");
			run.setBold(false);
			run.setItalic(true);
			run.addTab();
			run.setUnderline(UnderlinePatterns.NONE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Ano: ");
			run2.setBold(true);
			run2.setItalic(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(CommonsUtil.formataValorTaxa(this.objetoCcb.getTaxaDeJurosAno()) + "%");
			run.setBold(false);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.NONE);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Custo Efetivo Total (“CET”)");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			run.addCarriageReturn();
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Mes: ");
			run2.setBold(true);
			run2.setItalic(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(CommonsUtil.formataValorTaxa(this.objetoCcb.getCetMes()) + "%");
			run.setBold(false);
			run.setItalic(true);
			run.addTab();
			run.setUnderline(UnderlinePatterns.NONE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Ano: ");
			run2.setBold(true);
			run2.setItalic(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(CommonsUtil.formataValorTaxa(this.objetoCcb.getCetAno()) + "%");
			run.setBold(false);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.NONE);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Data de Emissão:");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" "+ CommonsUtil.formataData(this.objetoCcb.getDataDeEmissao(), "dd/MM/yyyy") +";");
			run2.setBold(false);
			run2.setItalic(true);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Data de Vencimento:");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" "+CommonsUtil.formataData(this.objetoCcb.getVencimentoUltimaParcelaPagamento(), "dd/MM/yyyy")+"." );
			run2.setBold(false);
			run2.setItalic(true);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Clausula de Constituição da Propriedade Fiduciária:");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" vide clausula 1.1 deste instrumento;");
			run2.setBold(false);
			run2.setItalic(true);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Clausula assegurando o Fiduciante – enquanto adimplente - ao uso do Bem(ns) Imóvel(eis):");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" vide clausula 3.9. deste instrumento;");
			run2.setBold(false);
			run2.setItalic(true);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Indicação, para efeito de venda em público leilão, do valor do imóvel -");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" vide clausula 6.1 deste instrumento e");
			run2.setBold(false);
			run2.setItalic(true);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Cláusula dispondo sobre os procedimentos de que trata o art. 27 da Lei 9514/97:");
			run.setBold(true);
			run.setItalic(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" vide clausula 5ª deste instrumento");
			run2.setBold(false);
			run2.setItalic(true);
			run2.addCarriageReturn();
			
			fazParagrafoSimples(document, paragraph, run, "CLÁUSULA TERCEIRA – DAS CARACTERÍSTICAS DA GARANTIA FIDUCIÁRIA", true);
			
			geraParagrafoComposto(document, paragraph, run, run2, "3.1. ", "Inicialmente as Partes fazem constar que a"
					+ " presente garantia é constituída nos termos da Lei 9514/97 e suas atualizações e que, com base "
					+ "na autorização constante no parágrafo primeiro do artigo 22 da referida lei, não é firmada no "
					+ "âmbito de operação de financiamento imobiliário operado pelo SFI. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "3.2. ", "As Partes anuem e o(s) FIDUCIANTE(s) "
					+ "ratificam que, entende-se por Obrigações Garantidas a totalidade da(s) cédula(s) de crédito"
					+ " bancário que contenham a presente garantia fiduciária constituída em garantia"
					+ " (“Garantia Fiduciária”).", true, false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("3.3. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Ficará a cargo do FIDUCIANTE(S) realizar o "
					+ "registro da Alienação Fiduciária do Imóvel(eis) na(s) respectiva(s) matrícula(s) do(s)"
					+ " Imóvel(eis) perante o Cartório de Registro de Imóveis competente nos prazos estabelecidos "
					+ "entre as Partes ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setBold(false);
			run.setText("sendo tal descumprimento considerado como hipótese de vencimento antecipado das Obrigações Garantidas.");
			run.setUnderline(UnderlinePatterns.SINGLE);
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("3.4. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("As Partes desde já se obrigam a disponibilizar, "
					+ "apresentar documentos e praticar os atos que vierem a ser"
					+ " necessários para formalizar o registro da Alienação Fiduciária"
					+ " (“Obrigações para Registro”) e, nesse sentido ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setBold(false);
			run.setText("declaram anuência de que qualquer ação ou omissão realizada no sentindo "
					+ "de prejudicar a efetiva constituição da Garantia Fiduciária será considerada "
					+ "também como hipótese de vencimento antecipado das Obrigações Garantidas.");
			run.setUnderline(UnderlinePatterns.SINGLE);
			run.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "3.5. ", "A presente Garantia Fiduciária "
					+ "compreende a propriedade fiduciária do Imóvel(eis) e todas as acessões, "
					+ "melhorias e benfeitorias existentes. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "3.6. ", "O(s) FIDUCIANTE(S) se obriga(m) "
					+ "a manter o Imóvel(eis) ora alienado fiduciariamente nos termos deste instrumento,"
					+ " em perfeito estado de segurança e utilização, além de realizar todas as obras,"
					+ " reparos e benfeitorias necessárias. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "3.7. ", "Mediante o registro da presente"
					+ " Alienação Fiduciária na(s) matrícula(s) do(s) Imóvel(eis), estará constituída a"
					+ " propriedade fiduciária sobre o(s) Imóvel(eis) em nome do FIDUCIÁRIA, efetivando-se"
					+ " o desdobramento da posse e tornando-se o(s) FIDUCIANTE(S) possuidor(es) direto(s)"
					+ " com direito à utilização do(s) Imóvel(eis) e a FIDUCIÁRIA, ou os Sucessores,"
					+ " conforme o caso, possuidores indiretos do(s) Imóvel(eis).", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "3.8. ", "A posse direta de que fica "
					+ "investida o(s) FIDUCIANTE(S) manter-se-ão até o adimplemento total das Obrigações "
					+ "Garantidas e enquanto estas permanecerem adimplidas, obrigando-se o(s) FIDUCIANTE(S)"
					+ " a manter, conservar e guardar o(s) Imóvel(eis), pagar pontualmente todos os tributos,"
					+ " taxas e quaisquer outras contribuições ou encargos que incidam ou venham "
					+ "a incidir sobre estes ou que sejam inerentes à Garantia Fiduciária..", true, false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("3.9. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Para fins de atendimento ao inciso V do artigo 24º da Lei 9.514/97,"
					+ " as Partes anuem que é assegurado ao(s) FIDUCIANTE(S) titular do(s) Imóvel(eis),"
					+ " enquanto adimplente(s), a livre utilização, por sua conta e risco do(s) Imóvel(eis). ");
			run2.setUnderline(UnderlinePatterns.SINGLE);
			run2.setBold(true);
			run2.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "3.10. ", "Caso o(s) FIDUCIANTE(S) não pague(m)"
					+ " pontualmente todos os tributos, despesas e encargos relativos ao(s) Imóvel(eis), a FIDUCIÁRIA,"
					+ " ou os Sucessores, conforme o caso, poderão, a seu critério, pagar tais tributos,"
					+ " despesas e encargos e solicitar o correspondente reembolso, que deverá ser feito dentro "
					+ "de 15 (quinze) dias de solicitação neste sentido, sob pena de, sobre o valor em atraso, "
					+ "incidirem juros moratórios de 1% (um por cento) ao mês, ou fração de mês em atraso, mais "
					+ "correção monetária de acordo com o IPCA/IBGE, tudo calculado desde a data de vencimento "
					+ "até a data do respectivo pagamento, além de multa não compensatória de 2% (dois por cento)"
					+ " sobre o valor em atraso.  ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "3.11. ", "A FIDUCIÁRIA, ou os Sucessores, "
					+ "conforme o caso, reservam-se ao direito de, a qualquer tempo, com periodicidade não"
					+ " inferior à trimestral e mediante aviso com 5 (cinco) dias de antecedência, exigir "
					+ "comprovantes de pagamento dos referidos encargos fiscais e/ou tributários, ou de quaisquer "
					+ "outras contribuições, ou ainda, conforme o caso, a comprovação de questionamentos"
					+ " administrativo e/ou judicial referentes a valores eventualmente não pagos, relacionados"
					+ " com os tributos incidentes. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "3.12. ", "O(s) FIDUCIANTE(S) titular(es)"
					+ " do(s) Imóvel(eis) declara(m) e informa(m) que o(s) Bem(ns) Imóvel(eis) outorgado(s) "
					+ "em garantia não é(são) nem faz(em) parte de bem de família de maneira que ratificam que,"
					+ " caso em algum momento da vigência das Obrigações Garantidas tal condição venha a ser "
					+ "contestada, servirá a presente clausula como RENÚNCIA aos benefícios de tal natureza. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "3.13. ", "O(s) FIDUCIANTE(S) titular(es) do(s) "
					+ "Imóvel(eis) também declaram que o(s) Bem(ns) Imóvel(eis) não conta(m) com usufruto em nome "
					+ "de terceiros se responsabilizando pelas penas impostas, inclusive indenizatórias, aos"
					+ " que declaram condições que não contemplam a realidade dos fatos.", true, false);
			
			fazParagrafoSimples(document, paragraph, run, "CLÁUSULA QUARTA – DA CONSTITUIÇÃO DA MORA E DO INADIMPLEMENTO – "
					+ "PROCEDIMENTOS DO ARTIGO 26º DA LEI 9514/1997", true);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.1. ", "Nos termos do artigo 26 da Lei nº 9.514/1997, "
					+ "vencida e não paga, no todo ou em parte as Obrigações Garantidas, consolidar-se-á, a propriedade do(s) "
					+ "Imóvel(eis) em nome da FIDUCIÁRIA, observadas as disposições a seguir. ", true, false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("4.2. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Para fins do parágrafo 3º mesmo artigo, as Partes convencionam que, ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("decorrido o prazo de 15(quinze) dias corridos da data de vencimento"
					+ " parcial ou total de qualquer dos títulos representativos das Obrigações"
					+ " Garantidas (“Prazo de Carência”),");
			run.setUnderline(UnderlinePatterns.SINGLE);
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" a FIDUCIÁRIA, ou os eventuais sucessores, conforme o caso, "
					+ "poderá, a seu critério, iniciar o procedimento de excussão da presente"
					+ " Garantia Fiduciária através da intimação do(s) FIDUCIANTE(S) nos "
					+ "termos do artigo 26, § 1º da Lei nº 9.514/1997.");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.3. ", "O simples pagamento "
					+ "do principal ou de parte dos valores atrasados, sem encargos pactuados, "
					+ "não exonerará o(s) FIDUCIANTE(S) OU DEVEDOR, da responsabilidade de "
					+ "liquidar(em) tais obrigações, continuando em mora para todos os efeitos "
					+ "legais, contratuais e da excussão iniciada;", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.4. ", "O procedimento de "
					+ "intimação para pagamento obedecerá aos seguintes requisitos:", true, false);
			
			cTNumbering = CTNumbering.Factory.parse(cTAbstractNumBulletXML_bold2);
			cTAbstractNum = cTNumbering.getAbstractNumArray(0);
			// CTAbstractNum cTAbstractNum = getAbstractNumber(STNumberFormat.LOWER_LETTER);
			abstractNum = new XWPFAbstractNum(cTAbstractNum);
			numbering = document.createNumbering();
			abstractNumID = numbering.addAbstractNum(abstractNum);
			numID = numbering.addNum(abstractNumID);
			
			geraParagrafoBulletList(document, paragraph, run, numID, "A intimação será requerida pela FIDUCIÁRIA, "
					+ "ou por seu sucessor conforme o caso, ao Oficial do Serviço de Registro de Imóveis competente,"
					+ " indicando o valor total das obrigações garantidas decorrentes da(s) CCB(s) vencidas e não pagas;", false);
			
			geraParagrafoBulletList(document, paragraph, run, numID, "A intimação far-se-á pessoalmente ao(s) FIDUCIANTE(S)"
					+ " e será realizada pelo Oficial do Serviço de Registro de Imóveis da circunscrição imobiliária onde "
					+ "se localizar o Imóvel(eis), podendo, a critério do Oficial, vir a ser realizada por seu preposto ou"
					+ " por meio do Serviço de Registro de Títulos e Documentos da respectiva comarca da situação do Imóvel(eis),"
					+ " ou, a critério da FIDUCIÁRIA por meio do Serviço de Registro de Títulos e Documentos  do domicílio de"
					+ " quem deva recebê-la, ou, ainda, pelo correio, com aviso de recebimento a ser firmado pelo(s) FIDUCIANTE(S),"
					+ " ou por quem deva receber a intimação;", false);
			
			geraParagrafoBulletList(document, paragraph, run, numID, "Quando se tratar de pessoa jurídica, a intimação será feita"
					+ " ao(s) representantes ou a procuradores regularmente constituídos pelo(s) FIDUCIANTE(S);", false);
			
			geraParagrafoBulletList(document, paragraph, run, numID, "Nos termos da Lei nº 13.465/2017, quando, por duas vezes,"
					+ " o Oficial de Registro de Imóveis ou de Registro de Títulos e Documentos ou o serventuário por eles"
					+ " credenciado ou o Oficial Registro de Títulos e Documentos  do domicilio do(s) FIDUCIANTE(S) "
					+ "houver procurado o(s) FIDUCIANTE(S) titular(es) do(s) Imóvel(eis) em seu domicílio ou residência "
					+ "sem o encontrar, deverá, havendo suspeita motivada de ocultação, intimar qualquer pessoa da família "
					+ "ou, em sua falta, qualquer vizinho de que, no dia útil imediato, retornará ao imóvel, a fim de efetuar"
					+ " a intimação, na hora que designar, aplicando-se subsidiariamente o disposto nos arts. 252, 253 e 254 "
					+ "da Lei no 13.105, de 16 de março de 2015 (Código de Processo Civil);", false);
			
			geraParagrafoBulletList(document, paragraph, run, numID, "Nos condomínios edilícios ou outras espécies de "
					+ "conjuntos imobiliários com controle de acesso, a intimação poderá ser feita ao funcionário da"
					+ " portaria responsável pelo recebimento de correspondência; e", false);
			
			geraParagrafoBulletList(document, paragraph, run, numID, "Quando o(s) FIDUCIANTE(ES), ou seu representante"
					+ " legal ou procurador encontrar-se em local ignorado, incerto ou inacessível, o fato será "
					+ "certificado pelo serventuário encarregado da diligência e informado ao oficial de Registro "
					+ "de Imóveis, que, à vista da certidão, promoverá a intimação por edital publicado durante 3 "
					+ "(três) dias, pelo menos, em um dos jornais de maior circulação local ou noutro de comarca "
					+ "de fácil acesso, se no local não houver imprensa diária, contado o prazo para purgação da"
					+ " mora da data da última publicação do edital;", false);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Os FIDUCIANTES constituem-se bastantes procuradores, uns dos outros, "
					+ "outorgando-se mutuamente poderes gerais, podendo qualquer um deles receber citações,"
					+ " intimações, comunicações, notificações, acordar, negociar, quitar, dar e receber,"
					+ " em nome um do outro, encarregando-se de dar ciência à outra parte de quaisquer"
					+ " obrigações decorrentes da CCB e da presente garantia");
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(", não podendo qualquer um deles alegar desconhecimento do que a outra parte"
					+ " fez e/ou realizou em relação ao presente instrumento e em especial receber "
					+ "todas as intimações decorrentes da Lei 9514/97, promovidas dor Cartório de Registro "
					+ "de Imóveis ou outro autorizado em lei, sem exceção.”;");
			run2.setBold(true);
			run2.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.5. ", "Purgada a mora, perante o"
					+ " Cartório de Registro de Imóvel(eis) competente, a presente Alienação Fiduciária"
					+ " se restabelecerá, caso ainda exista(m) Obrigações Garantidas. Nesta hipótese, "
					+ "nos 3 (três) dias seguintes, o Oficial entregará à FIDUCIÁRIA, ou aos Sucessores,"
					+ " conforme o caso, as importâncias recebidas, deduzidas as despesas de cobrança e"
					+ " de intimação.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.6. ", "O não pagamento, pelo(s) FIDUCIANTE(S)"
					+ " de qualquer valor devido pelas Obrigações Garantidas vencidas e não pagas, depois de"
					+ " devidamente comunicada nos termos da intimação tratada acima, bastará para a configuração"
					+ " da não purgação da mora. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.7. ", "Não havendo purgação da mora, "
					+ "o Oficial do Cartório de Registro de Imóvel(eis) certificará o fato e promoverá a "
					+ "averbação, na matrícula do(s) Imóvel(eis), da consolidação da propriedade do(s) "
					+ "Imóvel(eis) em nome da FIDUCIÁRIA, cabendo a esta, apresentar o comprovante de"
					+ " recolhimento do respectivo Imposto sobre Transmissão de Bens Imóveis – ITBI e,"
					+ " se for o caso, do laudêmio.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.7.1 ", "O(s) FIDUCIANTE(s) pode(rão),"
					+ " com a anuência da FIDUCIÁRIA, dar seu direito eventual ao imóvel em pagamento da dívida,"
					+ " dispensados os procedimentos previstos no art. 27º da Lei 9.514/1997.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.7.2 ", "Até a data da averbação"
					+ " da consolidação da propriedade fiduciária, é assegurado ao(s) FIDUCIANTE(S) ou DEVEDOR,"
					+ " quando aplicável, pagar as parcelas da dívida vencidas e as despesas de que trata o"
					+ " inciso II do § 3o do art. 27, hipótese em que convalescerá o contrato de Alienação"
					+ " Fiduciária.", true, false);
			
			fazParagrafoSimples(document, paragraph, run, "CLÁUSULA QUINTA – DOS LEILÕES PÚBLICOS EXTRAJUDICIAIS E PROCEDIMENTOS DO ARTIGO 27º DA LEI 9514/97 ", true);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.1. ", "Consolidada a propriedade do(s)"
					+ " Imóvel(eis) em nome da FIDUCIÁRIA, esta promoverá os públicos leilões, extrajudicialmente,"
					+ " para alienação em questão, no prazo de 30 (trinta) dias contados do registro da referida"
					+ " consolidação. ", true, false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("5.2. ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Se no primeiro público leilão, o maior "
					+ "lance oferecido for inferior ao ");
			run2.setBold(false);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Valor do Imóvel");
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" leiloado, conforme definição na clausula 6ª abaixo, será realizado o segundo leilão,"
					+ " nos 15 (quinze) dias seguintes.");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("5.3. ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("No segundo leilão, será aceito o maior lance oferecido, desde que igual ou superior ao ");
			run2.setBold(false);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Valor da Dívida");
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(", das despesas, dos prêmios de seguro, dos encargos legais, inclusive tributos,"
					+ " e das contribuições condominiais. ");
			run2.setBold(false);
			run2.addCarriageReturn();			
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.4. ", "Os leilões públicos extrajudiciais"
					+ " (primeiro e segundo) serão anunciados em edital único, resumido,"
					+ " por três vezes em jornal de ampla circulação na Comarca da situação do(s)"
					+ " Imóvel(eis) ou em outro de comarca de fácil acesso, se, no local do(s) Imóvel(eis) "
					+ "não houver imprensa com circulação diária; ", true, false);
			
			geraParagrafoCompostoSemReturn(document, paragraph, run, run2, "5.4.1. ", "Diante do acima exposto obriga-se o"
					+ " FIDUCIANTE a manter seus dados de notificação atualizados de forma que, caso não o faça,"
					+ " as notificações serão endereçadas aos seguintes endereços abaixo:", true, false);
			
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {
				if(participante.isFiduciante()) {
					if(!fiducianteGerado) {
						paragraph = document.createParagraph();
						paragraph.setAlignment(ParagraphAlignment.BOTH);
						paragraph.setSpacingBefore(0);
						paragraph.setSpacingAfter(0);
						paragraph.setSpacingBetween(1);

						run = paragraph.createRun();
						run.setFontSize(12);
						run.addCarriageReturn();
						run.setText("Pelo FIDUCIANTE:");
						run.setBold(false);
						run.setUnderline(UnderlinePatterns.SINGLE);
						fiducianteGerado = true;
					}
		
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				paragraph.setSpacingBetween(1);

				run = paragraph.createRun();
				run.addCarriageReturn();
				run.setFontSize(12);
				run.setText(participante.getPessoa().getNome());
				run.setBold(true);
				run.addCarriageReturn();
				
				run2 = paragraph.createRun();
				run2.setFontSize(12);
				run2.setText(participante.getPessoa().getEndereco() + ", n° " + participante.getPessoa().getNumero() + ", " + participante.getPessoa().getCidade() + " - " + participante.getPessoa().getEstado());
				run2.setBold(false);
				run2.addCarriageReturn();
				
				run2 = paragraph.createRun();
				run2.setFontSize(12);
				run2.setText("CEP " + participante.getPessoa().getCep());
				run2.setBold(false);
				run2.addCarriageReturn();
				
				run2 = paragraph.createRun();
				run2.setFontSize(12);
				run2.setText("E-mail: " + participante.getPessoa().getEmail());
				run2.setBold(false);
				
				} else {
					if(!devedorGerado) {
						paragraph = document.createParagraph();
						paragraph.setAlignment(ParagraphAlignment.BOTH);
						paragraph.setSpacingBefore(0);
						paragraph.setSpacingAfter(0);
						paragraph.setSpacingBetween(1);

						run = paragraph.createRun();
						run.setFontSize(12);
						run.addCarriageReturn();
						run.setText("Pelo DEVEDOR:");
						run.setBold(false);
						run.setUnderline(UnderlinePatterns.SINGLE);
						devedorGerado = true;
					}
		
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				paragraph.setSpacingBetween(1);

				run = paragraph.createRun();
				run.addCarriageReturn();
				run.setFontSize(12);
				run.setText(participante.getPessoa().getNome());
				run.setBold(true);
				run.addCarriageReturn();
				
				run2 = paragraph.createRun();
				run2.setFontSize(12);
				run2.setText(participante.getPessoa().getEndereco() + ", n° " + participante.getPessoa().getNumero() + ", " + participante.getPessoa().getCidade() + " - " + participante.getPessoa().getEstado());
				run2.setBold(false);
				run2.addCarriageReturn();
				
				run2 = paragraph.createRun();
				run2.setFontSize(12);
				run2.setText("CEP " + participante.getPessoa().getCep());
				run2.setBold(false);
				run2.addCarriageReturn();
				
				run2 = paragraph.createRun();
				run2.setFontSize(12);
				run2.setText("E-mail: " + participante.getPessoa().getEmail());
				run2.setBold(false);
				}
			}
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);

			run = paragraph.createRun();
			run.addCarriageReturn();
			run.setFontSize(12);
			run.setText("Pela FIDUCIÁRIA:");
			run.setBold(false);
			run.setUnderline(UnderlinePatterns.SINGLE);
			fiducianteGerado = true;
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);

			run = paragraph.createRun();
			run.addCarriageReturn();
			run.setFontSize(12);
			run.setText("BMP SOCIEDADE DE CRÉDITO DIRETO S.A. ");
			run.setBold(true);
			run.addCarriageReturn();
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Av. Paulista, 1765, 1º Andar, CEP 01311-200, São Paulo, SP ");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("E-mail: ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("cb@moneyp.com.br");
			run.setBold(false);
			run.setColor("0000ff");
			run.setUnderline(UnderlinePatterns.SINGLE);
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);

			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("5.5. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Após a averbação da consolidação da propriedade fiduciária no "
					+ "patrimônio da FIDUCIÁRIA e até a data da realização do segundo leilão,"
					+ " é assegurado aos FIDUCIANTE(S) o direito de preferência para adquirir"
					+ " o(s) Imóvel(eis) por preço correspondente ao ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Valor da Dívida");
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(", somado aos encargos, dos prêmios de seguro, dos encargos legais,"
					+ " inclusive tributos, e das contribuições condominiais, aos valores "
					+ "correspondentes ao imposto sobre transmissão inter vivos e ao laudêmio,"
					+ " se for o caso, pagos para efeito de consolidação da propriedade "
					+ "fiduciária no patrimônio da FIDUCIÁRIA, e às ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Despesas ");
			run.setBold(true);

			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("inerentes ao procedimento de cobrança e leilão, incumbindo,"
					+ " também, ao(s) FIDUCIANTE(S) o pagamento dos encargos tributários"
					+ " e despesas exigíveis para a nova aquisição do(s) Imóvel(eis), de "
					+ "que trata este parágrafo, inclusive custas, impostos e emolumentos. ");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.6. ", "Para os fins do disposto na cláusula 5.5."
					+ " deste instrumento, as datas, horários e locais dos leilões serão comunicados ao devedor "
					+ "mediante correspondência dirigida aos endereços constantes do contrato, inclusive ao endereço"
					+ " eletrônico.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.7. ", "Para os fins do disposto no artigo 27º"
					+ " da Lei 9.514/1997, entende-se por: ", true, false);
			
			cTNumbering = CTNumbering.Factory.parse(cTAbstractNumBulletXML_bold_Roman_NoLeft_NoHanging);
			cTAbstractNum = cTNumbering.getAbstractNumArray(0);
			// CTAbstractNum cTAbstractNum = getAbstractNumber(STNumberFormat.LOWER_LETTER);
			abstractNum = new XWPFAbstractNum(cTAbstractNum);
			numbering = document.createNumbering();
			abstractNumID = numbering.addAbstractNum(abstractNum);
			numID = numbering.addNum(abstractNumID);
			
			geraParagrafoBulletListComposta(document, paragraph, run, run2, "– Valor da Dívida: ", "a soma do saldo devedor das"
					+ " operações representativas das Obrigações Garantidas, na data do leilão, nele incluídos os juros "
					+ "convencionais, as penalidades e os demais encargos contratuais; ", true, false, numID, UnderlinePatterns.NONE);
			
			geraParagrafoBulletListComposta(document, paragraph, run, run2, "– Despesas: ", "a soma das importâncias correspondentes aos"
					+ " encargos e custas de intimação, e as necessárias à realização do público leilão, nestas compreendidas"
					+ " as relativas aos anúncios, publicações de editais, à comissão do leiloeiro, avaliações e perícias,"
					+ " Imposto sob transmissão recolhido para fins de consolidação da propriedade bem como, adicionalmente,"
					+ " honorários advocatícios extrajudiciais no importe de 20%(vinte por cento) sob o Valor da Dívida"
					+ " relacionados aos procedimentos de cobrança.  ", true, false, numID, UnderlinePatterns.NONE);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);

			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("5.8. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Nos ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("cinco dias que se seguirem à venda do(s) Imóvel(eis)");
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" no leilão, a FIDUCIÁRIA entregará ao(s) FIDUCIANTE(S) a importância que sobejar, "
					+ "considerando-se nela compreendido o valor da indenização de benfeitorias, depois de deduzidos"
					+ " o Valor da Dívida e das Despesas e encargos aplicáveis, fato esse que importará em recíproca "
					+ "quitação, não se aplicando o disposto na parte final do art. 516 do Código Civil.");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);

			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("5.9. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Se, no segundo leilão, o maior lance oferecido não for igual ou superior ao ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Valor da Dívida");
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" somado às Despesas e dos Encargos, considerar-se-á extinta a dívida "
					+ "e exonerada a FIDUCIÁRIA da obrigação de entregar ao(s) FIDUCIANTE(S) o sobejo"
					+ " retratado na clausula acima. ");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);

			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("5.9.1. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Na hipótese dessa clausula, a FIDUCIÁRIA, ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("no prazo de cinco dias a contar da data do segundo leilão");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(", dará ao(s) FIDUCIANTE(S) quitação da dívida, mediante termo próprio.");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.10. ", "Se o(s) Imóvel (eis) estiver(em) locado(s),"
					+ " a locação poderá ser denunciada com o prazo de 30(trinta) dias para desocupação, salvo se"
					+ " tiver havido aquiescência por escrito da FIDUCIÁRIA, devendo a denúncia ser realizada no "
					+ "prazo de 90(noventa) dias a contar da data da consolidação da propriedade na FIDUCIÁRIA, "
					+ "devendo essa condição constar expressamente em cláusula contratual específica, destacando-se"
					+ " das demais por sua apresentação gráfica.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.11. ", "A critério do CREDOR,"
					+ " poderá ser realizada a alteração de propriedade do imóvel no contrato de aluguel,"
					+ " mediante aditivo próprio que independerá de notificação ou anuência do DEVEDOR, "
					+ "caso em que os alugueis serão devidos ao CREDOR desde a consolidação.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.12. ", "Responde o(s) FIDUCIANTE(S)"
					+ " pelo pagamento dos impostos, taxas, contribuições condominiais e quaisquer outros "
					+ "encargos que recaiam ou venham a recair sobre o(s) Imóvel(eis), cuja posse tenha sido "
					+ "transferida para a FIDUCIÁRIA, até a data em que a FIDUCIÁRIA vier "
					+ "a ser imitida na posse.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.13. ", "A cessão de quaisquer das Obrigações Garantidas"
					+ " implicará a transferência, ao cessionário, de todos os direitos e obrigações inerentes à propriedade"
					+ " fiduciária em garantia. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.14. ", "O(s) FIDUCIANTE(S), com anuência expressa da "
					+ "FIDUCIÁRIA, poderá transmitir os direitos de que seja titular sobre o(s) Imóvel(eis) objeto da "
					+ "alienação fiduciária em garantia, assumindo o adquirente as respectivas obrigações. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.15. ", "O(s) FIDUCIANTE(S) deverá(ão) desocupar"
					+ " o imóvel até a data da realização do primeiro público leilão, deixando-o livre e desimpedido de "
					+ "pessoas e coisas. ", true, false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);

			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("5.16. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Não ocorrendo a desocupação do(s) Imóvel(eis), no prazo e forma ajustados, a FIDUCIÁRIA,"
					+ " ou seus Sucessores, inclusive o adquirente do Imóvel(eis) em leilão ou posteriormente, "
					+ "poderá requerer a reintegração de sua posse cumulada com cobrança do valor da Taxa de Ocupação"
					+ " desde a data da consolidação (observado o limite máximo mensal ou por fração de 1% acima estabelecido)"
					+ " e demais despesas previstas neste Instrumento de Alienação, sendo concedida, liminarmente, a ordem "
					+ "judicial de desocupação no prazo máximo de 60 (sessenta) dias, desde que comprovada, mediante certidão"
					+ " da matrícula do Imóvel(eis) a consolidação da plena propriedade em nome da ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIÁRIA");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(", ou do registro do contrato celebrado em decorrência do leilão, "
					+ "conforme quem seja o autor da ação de reintegração de posse. ");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.17. ", "O fiador ou terceiro"
					+ " interessado que pagar a dívida ficará sub-rogado, de pleno direito, no crédito e na propriedade "
					+ "fiduciária. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.18. ", "Na hipótese de insolvência do(s)"
					+ " FIDUCIANTE(S) fica assegurada à FIDUCIÁRIA a restituição do(s) Imóvel(eis) alienado(s) "
					+ "fiduciariamente, na forma da legislação pertinente.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.19. ", "Aplicam-se à propriedade fiduciária regida"
					+ " por este instrumento, no que couber, as disposições dos arts. 647 e 648 do Código Civil.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.20. ", "Na hipótese de desapropriação, total ou parcial,"
					+ " do(s) Imóvel(eis), a FIDUCIÁRIA, como proprietária, ainda que em caráter resolúvel, será o único e exclusivo"
					+ " beneficiário da justa e prévia indenização paga pelo poder expropriante.", true, false);
			
			fazParagrafoSimples(document, paragraph, run, "CLÁUSULA SEXTA – DO VALOR DE VENDA DO(S) IMÓVEL(EIS) PARA FINS DE LEILÃO ", true);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);

			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("6.1. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("As Partes convencionam que o valor de venda total do(s) Imóvel(eis) para fins de leilão, é de  ");
			run2.setBold(false);
			
			valorPorExtenso.setNumber(this.objetoCcb.getVendaLeilao());
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(CommonsUtil.formataValorMonetario( this.objetoCcb.getVendaLeilao(), "R$ ") + " ("+ valorPorExtenso.toString() +"),");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("conforme Laudo de Avaliação (anexo) elaborado por ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(this.objetoCcb.getElaboradorNome() +" - CREA "+ this.objetoCcb.getElaboradorCrea());
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" e responsável ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(this.objetoCcb.getResponsavelNome() +" - CREA "+ this.objetoCcb.getResponsavelCrea() +", ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("o qual deverá ser devidamente atualizado pelo IGP-M/FGV, desde a data base do"
					+ " Laudo até a data de realização de cada leilão (“Valor de Venda do Imóvel(eis) em "
					+ "Leilão” ou “Valor do Imóvel(eis)”).  (novo)");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);

			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("6.2. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Até o pagamento integral da(s) CCB(s), a qualquer momento e "
					+ "independentemente do devido cumprimento das demais obrigações da ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIANTE "+ this.objetoCcb.getNomeEmitente() +" ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("contratadas no âmbito da CCB, o valor do Imóvel(eis) deverá ser equivalente a, pelo menos, ");
			run2.setBold(false);
			
			porcentagemPorExtenso.setNumber(this.objetoCcb.getPorcentagemImovel());
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(CommonsUtil.formataValorTaxa(this.objetoCcb.getPorcentagemImovel()) +"% ("+ porcentagemPorExtenso.toString() +" por cento) ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("do saldo devedor da CCB, acrescido dos juros remuneratórios e, conforme o caso, encargos moratórios (“Razão Mínima”).");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);

			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("6.3. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Na hipótese de a Razão Mínima não ser observada, a qualquer momento, ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("o(s) FIDUCIANTE(S) ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("deverá(ão), no prazo de até 10 (dez) dias contados do recebimento de comunicação nesse sentido, oferecer à ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIÁRIA ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("outra garantia que este considere aceitável,"
					+ " a seu exclusivo critério, para reforço das garantias nos termos da(s) CCBs. ");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);

			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("6.4. ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Nos termos do parágrafo único do artigo 24º da Lei 9.514/1997 "
					+ "atualizado pela Lei nº 13.465/2017, anuem as Partes que, ");
			run2.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("caso o Valor do Imóvel indicado na clausula 6.1 seja inferior"
					+ " ao utilizado pelo órgão competente como base de cálculo para a apuração"
					+ " do imposto sobre transmissão inter vivos, exigível por força da "
					+ "consolidação da propriedade em nome do credor fiduciário, o Valor "
					+ "Mínimo de Venda do Imóvel(eis) em Leilão deverá automaticamente "
					+ "corresponder ao valor de tal apuração.");
			run2.setBold(false);
			run2.setUnderline(UnderlinePatterns.SINGLE);
			run2.addCarriageReturn();
			
			fazParagrafoSimples(document, paragraph, run, "CLAUSULA SÉTIMA - DAS DISPOSIÇÕES GERAIS", true);
			
			geraParagrafoComposto(document, paragraph, run, run2, "7.1 ", "A tolerância por qualquer das "
					+ "Partes quanto a alguma demora, atraso ou omissão das outras no cumprimento das "
					+ "obrigações ajustadas nesta Alienação Fiduciária, ou a não aplicação, na ocasião "
					+ "oportuna, das cominações aqui constantes, não acarretará o cancelamento das penalidades,"
					+ " nem dos poderes ora conferidos, podendo ser aplicadas aquelas e exercidos estes,"
					+ " a qualquer tempo, caso permaneçam as causas. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "7.1.1 ", "O disposto no item 7.1, acima,"
					+ " prevalecerá ainda que a tolerância ou a não aplicação das cominações ocorra repetidas vezes,"
					+ " consecutiva ou alternadamente. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "7.2 ", "A ocorrência de uma ou mais hipóteses referidas"
					+ " acima não implicará novação ou modificação de quaisquer disposições	desta Alienação Fiduciária,"
					+ " as quais permanecerão íntegras e em pleno vigor, como se nenhum favor houvesse ocorrido.", true, false);
						
			geraParagrafoComposto(document, paragraph, run, run2, "7.3 ", "As obrigações constituídas por "
					+ "esta Alienação Fiduciária são extensivas e obrigatórias aos cessionários,"
					+ " promissários-cessionários, herdeiros e sucessores a qualquer título"
					+ " das Partes.  ", true, false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);

			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("7.4 ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Na hipótese de desapropriação total ou parcial do Imóvel(eis), a ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIÁRIA");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(", ou os Sucessores, conforme o caso, como proprietários do Imóvel(eis),"
					+ " ainda que em caráter fiduciário, serão os únicos e exclusivos beneficiários"
					+ " da justa e prévia indenização paga pelo poder expropriante, até o limite do"
					+ " saldo devedor das Obrigações Garantidas à época, sendo tais valores"
					+ " amortizados das Obrigações Garantidas.");
			run2.setBold(false);
			run2.addCarriageReturn();

			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);

			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("7.4.1 ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Se, no dia de seu recebimento pela ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIÁRIA");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(", ou pelos Sucessores, conforme o caso, a proporção da indenização conforme item 7.4, acima, for: ");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			cTNumbering = CTNumbering.Factory.parse(cTAbstractNumBulletXML);
			cTAbstractNum = cTNumbering.getAbstractNumArray(0);
			// CTAbstractNum cTAbstractNum = getAbstractNumber(STNumberFormat.LOWER_LETTER);
			abstractNum = new XWPFAbstractNum(cTAbstractNum);
			numbering = document.createNumbering();
			abstractNumID = numbering.addAbstractNum(abstractNum);
			numID = numbering.addNum(abstractNumID);
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Superior ao saldo devedor das Obrigações Garantidas à época,"
					+ " a importância que sobejar será entregue aos ");
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("FIDUCIANTE(S)");
			run2.setBold(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("; ou");
			run.setBold(false);
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Inferior ao saldo devedor das Obrigações Garantidas à época, a ");
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("FIDUCIÁRIA");
			run2.setBold(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(", ou os Sucessores, conforme o caso,"
					+ " ficarão exonerados da obrigação de restituição de qualquer quantia, a que título for, em favor dos ");
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("FIDUCIANTE(S)");
			run2.setBold(true);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(", pela integral liquidação das Obrigações Garantidas.");
			run.setBold(false);
			run.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "7.5 ", "As Partes autorizam e determinam,"
					+ " desde já, que o Sr. Oficial do Serviço de Registro de Imóveis competente proceda,"
					+ " total ou parcialmente, a todos os assentamentos, registros e averbações necessários"
					+ " decorrentes da presente Alienação Fiduciária, isentando-os de qualquer responsabilidade "
					+ "pelo devido cumprimento do disposto neste instrumento.", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "7.6 ", "Fica desde logo estipulado que"
					+ " a presente Alienação Fiduciária revoga e substitui todo e qualquer entendimento havido"
					+ " entre as Partes anteriormente a esta data sobre o mesmo objeto. ", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "7.7 ", "Todas as comunicações entre as Partes "
					+ "serão consideradas válidas quando enviadas nos endereços constantes da cláusula 5.4.1 desta"
					+ " Alienação Fiduciária, observado, inclusive o disposto no item 4.4. alínea “g”, ou em outros"
					+ " que venham a indicar, por escrito, no curso desta relação. Cada Parte deverá comunicar "
					+ "imediatamente a outra sobre a mudança de seu endereço.", true, false);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("7.8 ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Neste ato e como condição de celebração do presente instrumento, o(s) ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIANTE(S)");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" nomeia(m) a ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIÁRIA ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("de forma irrevogável e irretratável, para representá-lo(s) na celebração de"
					+ " escrituras de registro da presente que eventualmente se façam necessárias por"
					+ " exigência do competente Oficial de Registro de Imóveis, podendo este descrever "
					+ "e caracterizar o(s) Imóvel(eis), suas benfeitorias, perímetro e confrontantes, "
					+ "bem como cumprir alterar todo e qualquer outro item que se faça necessário, desde"
					+ " que mantidas as condições comerciais ora pactuadas, podendo inclusive substabelecer,"
					+ " com reservas os poderes ora conferidos. Ainda, o(s) ");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("FIDUCIANTE(S)");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" compromete-se neste ato a fornecer toda a documentação necessária para tanto. ");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("7.9 ");
			run.setBold(true);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Os “");
			run2.setBold(false);
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Considerandos");
			run.setBold(false);
			
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("” e os Anexos constituem partes integrantes e inseparáveis da"
					+ " presente Alienação Fiduciária, e serão considerados meios válidos"
					+ " e eficazes para fins de interpretação das Cláusulas deste. ");
			run2.setBold(false);
			
			fazParagrafoSimples(document, paragraph, run, "CLÁUSULA OITAVA – DA LEI DE REGÊNCIA E DO FORO DE ELEIÇÃO ", true);
			
			geraParagrafoComposto(document, paragraph, run, run2, "8.1 ", "A presente Alienação Fiduciária é regida,"
					+ " material e processualmente, pelas leis da República Federativa do Brasil e faz parte"
					+ " acessória da(s) CCB(s).", true, false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "8.2 ", "Todo litígio ou controvérsia originário"
					+ " ou decorrente desta Alienação Fiduciária e dos demais Documentos da Operação será submetido"
					+ " ao Foro da Comarca de São Paulo, Estado de São Paulo, único competente para conhecer e "
					+ "dirimir quaisquer questões ou litígios, com renúncia expressa a qualquer outro, por mais"
					+ " privilegiado que seja ou venha a ser. ", true, false);
			
			fazParagrafoSimples(document, paragraph, run, "E, por estarem assim, justas e contratadas, as Partes assinam"
					+ " a presente Alienação Fiduciária em 2 (duas) vias, de igual teor e forma, na presença das 2 (duas)"
					+ " testemunhas abaixo identificadas. ", false);
			
			fazParagrafoSimples(document, paragraph, run,
					"São Paulo, SP, " + this.objetoCcb.getDataDeEmissao().getDate() + " de "
							+ CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase() + " de "
							+ (this.objetoCcb.getDataDeEmissao().getYear() + 1900) + ".",
					false);
			
			paragraph = document.createParagraph();		
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(10);
			run.setText("(O final desta página foi intencionalmente deixado em branco)");
			run.setBold(false);
			run.setItalic(true);
			run.addCarriageReturn();
			run.setText("(Segue a página de assinaturas)");

			paragraph = document.createParagraph();
			paragraph.setPageBreak(true);
			
			paragraph = document.createParagraph();	
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("(Página de assinaturas do INSTRUMENTO PARTICULAR DE ALIENAÇÃO FIDUCIÁRIA DE BEM(NS) IMÓVEL(EIS) EM GARANTIA E OUTRAS AVENÇAS "
					+ "nº " + this.objetoCcb.getNumeroCcb() + ", emitida por "+ this.objetoCcb.getNomeEmitente() +", CPF/MF nº "+ this.objetoCcb.getCpfEmitente() +", em favor de "
					+ "BMP SOCIEDADE DE CRÉDITO DIRETO S.A., CNPJ/ MF sob nº 34.337.707/0001-00,"
					+ " em "+ CommonsUtil.formataData(this.objetoCcb.getDataDeEmissao(), "dd/MM/yyyy" )+".)");
			run.setBold(false);
			run.setItalic(true);
			run.addCarriageReturn();

			XWPFTable table = document.createTable();

			paragraph = document.createParagraph();
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			paragraph.setAlignment(ParagraphAlignment.LEFT);

			table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(6000));
			table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(2500));

			// create first row
			XWPFTableRow tableRow1 = table.getRow(0);

			tableRow1.getCell(0).setParagraph(paragraph);
			run = tableRow1.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("__________________________________");
			run.setBold(false);
			run.addBreak();

			run2 = tableRow1.getCell(0).getParagraphArray(0).createRun();
			run2.setFontSize(12);
			run2.setText("BMP SOCIEDADE DE CRÉDITO DIRETO S.A");
			run2.setBold(true);
			run2.addBreak();

			run4 = tableRow1.getCell(0).getParagraphArray(0).createRun();
			run4.setFontSize(12);
			run4.setText("FIDUCIÁRIA");
			run4.setBold(false);

			tableRow1.addNewTableCell();

			tableRow1.getCell(1).setParagraph(paragraph);

			run = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("__________________________________");
			run.setBold(false);
			run.addBreak();

			run2 = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run2.setFontSize(12);
			run2.setText(this.objetoCcb.getNomeEmitente());
			run2.setBold(true);
			run2.addBreak();

			run3 = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run3.setFontSize(12);
			run3.setText(" ");
			run3.setBold(true);
			run3.addBreak();

			run4 = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run4.setFontSize(12);
			run4.setText("EMITENTE");
			run4.setBold(false);

			XWPFTableRow tableRow2 = table.createRow();

			if (this.objetoCcb.getListaParticipantes().size() > 1) {
				tableRow2.getCell(0).setParagraph(paragraph);
				tableRow2.getCell(1).setParagraph(paragraph);
				int qtdePessoasEsquerdo = 0;
				for (int iPartTab = 0; iPartTab < this.objetoCcb.getListaParticipantes().size(); iPartTab++) {

					CcbParticipantes participante = this.objetoCcb.getListaParticipantes().get(iPartTab);
					if (iPartTab != 0) {
						if (iPartTab % 2 != 0) {

							run = tableRow2.getCell(0).getParagraphArray(0).createRun();
							run.addBreak();
							run.setFontSize(12);
							run.setText("__________________________________");
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
							run.setText("__________________________________");
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
				run4 = tableRow2.getCell(0).getParagraphArray(0).createRun();
				run4.setFontSize(12);
				run4.addBreak();
				run4.setText("Testemunhas");
				run4.setBold(false);
				run4.addBreak();
				run4.setText("__________________________________");

				run4 = tableRow2.getCell(1).getParagraphArray(0).createRun();
				run4.setFontSize(12);
				for (int i = 0; i <= qtdePessoasEsquerdo; i++) {
					run4.addBreak();
					run4.addBreak();
					run4.addBreak();
				}
				run4.setText("__________________________________");
				run4.setBold(false);

			} else {
				tableRow2.getCell(0).setParagraph(paragraph);
				run = tableRow2.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.addBreak();
				run.setText("Testemunhas");
				run.setBold(false);
				run.addBreak();
				run.setText("__________________________________ ");

				tableRow2.getCell(1).setParagraph(paragraph);
				run = tableRow2.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.addBreak();
				run.addBreak();
				run.setText("__________________________________ ");
				run.setBold(false);
			}

			// create third row
			XWPFTableRow tableRow3 = table.createRow();
			tableRow3.getCell(0).setParagraph(paragraph);
			run = tableRow3.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("Nome:  " + this.objetoCcb.getNomeTestemunha1());
			run.setBold(false);

			tableRow3.getCell(1).setParagraph(paragraph);
			run = tableRow3.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("Nome:  " + this.objetoCcb.getNomeTestemunha2());
			run.setBold(false);

			XWPFTableRow tableRow4 = table.createRow();
			tableRow4.getCell(0).setParagraph(paragraph);
			run = tableRow4.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("RG:  " + this.objetoCcb.getRgTestemunha1());
			run.setBold(false);

			tableRow4.getCell(1).setParagraph(paragraph);
			run = tableRow4.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("RG:  " + this.objetoCcb.getRgTestemunha2());
			run.setBold(false);

			XWPFTableRow tableRow5 = table.createRow();
			tableRow5.getCell(0).setParagraph(paragraph);
			run = tableRow5.getCell(0).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("CPF:  " + this.objetoCcb.getCpfTestemunha1());
			run.setBold(false);

			tableRow5.getCell(1).setParagraph(paragraph);
			run = tableRow5.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setText("CPF:  " + this.objetoCcb.getCpfTestemunha2());
			run.setBold(false);

			CTTblPr tblpro = table.getCTTbl().getTblPr();

			CTTblBorders borders = tblpro.addNewTblBorders();
			borders.addNewBottom().setVal(STBorder.NONE);
			borders.addNewLeft().setVal(STBorder.NONE);
			borders.addNewRight().setVal(STBorder.NONE);
			borders.addNewTop().setVal(STBorder.NONE);
			// also inner borders
			borders.addNewInsideH().setVal(STBorder.NONE);
			borders.addNewInsideV().setVal(STBorder.NONE);
			
			
			
			fiducianteGerado = false;
			devedorGerado = false;
			
			
			
			XWPFHeaderFooterPolicy headerFooterPolicy = document.getHeaderFooterPolicy();
			if (headerFooterPolicy == null) {
				headerFooterPolicy = document.createHeaderFooterPolicy();
			}
			
			XWPFFooter footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);

			paragraph = footer.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);

			run = paragraph.createRun();
			run.setFontSize(10);
			run.setBold(true);
			run.getCTR().addNewFldChar()
					.setFldCharType(org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType.BEGIN);

			run = paragraph.createRun();
			run.setFontSize(10);
			run.setBold(true);
			run.getCTR().addNewInstrText().setStringValue("PAGE \\* MERGEFORMAT");
			
			run = paragraph.createRun();
			run.setFontSize(10);
			run.setBold(true);
			run.getCTR().addNewFldChar().setFldCharType(org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType.END);
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			document.write(out);
			document.close();
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
					FacesContext.getCurrentInstance());
			String nomeSemvirgula = this.objetoCcb.getNomeEmitente();
			if(nomeSemvirgula.contains(",")) {
				nomeSemvirgula = nomeSemvirgula.replace(",", "");
		    }
			gerador.open(String.format("Galleria Bank - Modelo_AF %s.docx", ""));
			gerador.feed(new ByteArrayInputStream(out.toByteArray()));
			gerador.close();
			
			criarCcbNosistema();
			
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public StreamedContent geraNCDinamica() throws IOException{
			try {
				XWPFDocument document = new XWPFDocument();	
				
				XWPFHeaderFooterPolicy headerFooterPolicy = document.getHeaderFooterPolicy();
				if (headerFooterPolicy == null)
					headerFooterPolicy = document.createHeaderFooterPolicy();

				XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);
				XWPFParagraph paragraphHeader = header.createParagraph();
				paragraphHeader.setAlignment(ParagraphAlignment.LEFT);
				XWPFRun runHeader = paragraphHeader.createRun();
				runHeader.addPicture(getClass().getResourceAsStream("/resource/BMP MoneyPlus.png"), 6, "BMP MoneyPlus",
						Units.toEMU(130), Units.toEMU(72));

				XWPFRun run;
				XWPFParagraph paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				run = paragraph.createRun();
				run.setText("São Paulo, SP, " + this.objetoCcb.getDataDeEmissao().getDate() + " de "
								+ CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase() + " de "
								+ (this.objetoCcb.getDataDeEmissao().getYear() + 1900) + ".");
				run.setFontSize(10);
				run.setBold(false);
				run.addCarriageReturn();
				XWPFRun run2 = paragraph.createRun();
				XWPFRun run3 = paragraph.createRun();
				XWPFRun run4 = paragraph.createRun();
				
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				run = paragraph.createRun();
				run.setText("À");
				run.setFontSize(10);
				run.setBold(false);
				
				for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {
					if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "EMITENTE")) {
						if(CommonsUtil.semValor(this.objetoCcb.getNomeEmitente())) {
							this.objetoCcb.setNomeEmitente(participante.getPessoa().getNome());
						}
						if(CommonsUtil.semValor(this.objetoCcb.getLogradouroEmitente())) {
							if(!CommonsUtil.semValor(participante.getPessoa().getEndereco())) {
								this.objetoCcb.setLogradouroEmitente(participante.getPessoa().getEndereco());
							}
						}
						if(CommonsUtil.semValor(this.objetoCcb.getNumeroEmitente())) {
							if(!CommonsUtil.semValor(participante.getPessoa().getNumero())) {
								this.objetoCcb.setNumeroEmitente(participante.getPessoa().getNumero());
							}
						}
						if(CommonsUtil.semValor(this.objetoCcb.getComplementoEmitente())) {
							if(!CommonsUtil.semValor(participante.getPessoa().getComplemento())) {
								this.objetoCcb.setComplementoEmitente(participante.getPessoa().getComplemento());
							}
						}
						if(CommonsUtil.semValor(this.objetoCcb.getCidadeEmitente())) {
							if(!CommonsUtil.semValor(participante.getPessoa().getCidade())) {
								this.objetoCcb.setCidadeEmitente(participante.getPessoa().getCidade());
							}
						}
						if(CommonsUtil.semValor(this.objetoCcb.getUfEmitente())) {
							if(!CommonsUtil.semValor(participante.getPessoa().getEstado())) {
								this.objetoCcb.setUfEmitente(participante.getPessoa().getEstado());
							}
						}
						if(CommonsUtil.semValor(this.objetoCcb.getCepEmitente())) {
							if(!CommonsUtil.semValor(participante.getPessoa().getCep())) {
								this.objetoCcb.setCepEmitente(participante.getPessoa().getCep());
							}
						}						
					}
				}
				
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				run = paragraph.createRun();
				run.setText(this.objetoCcb.getNomeEmitente());
				run.setFontSize(10);
				run.setBold(true);
				run.addCarriageReturn();
				
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				run = paragraph.createRun();
				run.setText(this.objetoCcb.getLogradouroEmitente() +", nº "+ this.objetoCcb.getNumeroEmitente() +", "+ this.objetoCcb.getComplementoEmitente());
				run.setFontSize(10);
				run.setBold(false);
				
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				run = paragraph.createRun();
				run.setText(this.objetoCcb.getCidadeEmitente() +" – " + this.objetoCcb.getUfEmitente());
				run.setFontSize(10);
				run.setBold(false);
				
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				run = paragraph.createRun();
				run.setText("CEP "+ this.objetoCcb.getCepEmitente() +";");
				run.setFontSize(10);
				run.setBold(false);
				run.addCarriageReturn();
				
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				run = paragraph.createRun();
				run.setText("REF.: Contrato de CCI nº " + this.objetoCcb.getNumeroCcb());
				run.setFontSize(10);
				run.setBold(true);
				run.addCarriageReturn();
				run.addCarriageReturn();
				
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				run = paragraph.createRun();
				run.setText("Prezado(s) Cliente(s) ");
				run.setFontSize(10);
				run.setBold(false);
				run.addCarriageReturn();
				
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				run = paragraph.createRun();
				run.setText("Pela presente, levamos ao seu conhecimento que, nesta data,"
						+ " o GALLERIA FINANÇAS SECURITIZADORA S.A., inscrito no CNPJ/MF "
						+ "sob nº 34.425.347/0001-06, adquiriu da BMP SOCIEDADE "
						+ "DE CRÉDITO DIRETO S.A. os direitos de crédito, decorrentes da(s) "
						+ "Cédula(s) de Crédito Imobiliário (“CCI”) em referência, celebrado por"
						+ " V. Sa(s), dos vencimentos a partir de ");
				run.setFontSize(10);
				run.setBold(false);
				
				run2 = paragraph.createRun();
				run2.setText(this.objetoCcb.getDataDeEmissao().getDate()+"");
				run2.setFontSize(10);
				run2.setBold(true);
				run2.setUnderline(UnderlinePatterns.SINGLE);
				
				run = paragraph.createRun();
				run.setText(" de ");
				run.setFontSize(10);
				run.setBold(true);
				
				run2 = paragraph.createRun();
				run2.setText(CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase());
				run2.setFontSize(10);
				run2.setBold(true);
				run2.setUnderline(UnderlinePatterns.SINGLE);
				
				run = paragraph.createRun();
				run.setText(" de ");
				run.setFontSize(10);
				run.setBold(true);
				
				run2 = paragraph.createRun();
				run2.setText( (this.objetoCcb.getDataDeEmissao().getYear() + 1900)  + ",");
				run2.setFontSize(10);
				run2.setBold(true);
				run2.setUnderline(UnderlinePatterns.SINGLE);
				
				run = paragraph.createRun();
				run.setText(" (inclusive).");
				run.setFontSize(10);
				run.setBold(false);
				run.addCarriageReturn();
				
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				run = paragraph.createRun();
				run.setText("Assim, em face da operação contratada, fica(m) V. "
						+ "Sa(s) notificadas que a partir de "+ this.objetoCcb.getDataDeEmissao().getDate() +" de "+
						CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase() +" de "+ (this.objetoCcb.getDataDeEmissao().getYear() + 1900) +","
						+ " (inclusive), o pagamento das parcelas referentes a(s) CCI ");
				run.setFontSize(10);
				run.setBold(false);
								
				run2 = paragraph.createRun();
				run2.setText("Nº " + this.objetoCcb.getNumeroCcb());
				run2.setFontSize(10);
				run2.setBold(true);
				run = paragraph.createRun();
				run.setText(" deverão ser efetuados diretamente ao GALLERIA FINANÇAS SECURITIZADORA S.A.,"
						+ " na conta de nº 300793-6, mantida na agência nº 1515-6, Banco 001 - Banco do Brasil S.A.,"
						+ " ou à sua ordem.");
				run.setFontSize(10);
				run.setBold(false);
				
				
				run.addCarriageReturn();
				
				paragraph = document.createParagraph();
				run = paragraph.createRun();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				run.setText("Qualquer alteração dos procedimentos acima descritos dependerá"
						+ " de prévia e expressa autorização do BMP SOCIEDADE DE CRÉDITO DIRETO S.A. ");
				run.setFontSize(10);
				run.setBold(false);
				run.addCarriageReturn();
				
				paragraph = document.createParagraph();
				run = paragraph.createRun();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				run.setText("Atenciosamente, ");
				run.setFontSize(10);
				run.setBold(false);
				run.addCarriageReturn();
				run.addCarriageReturn();
				run.addCarriageReturn();
				
				paragraph = document.createParagraph();
				run = paragraph.createRun();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				run.setText("___________________________________________________________");
				run.setFontSize(9);
				run.setBold(false);
				
				paragraph = document.createParagraph();
				run = paragraph.createRun();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				run.setText("BMP SOCIEDADE DE CRÉDITO DIRETO S.A");
				run.setFontSize(11);
				run.setBold(true);
				
				paragraph = document.createParagraph();
				run = paragraph.createRun();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				run.setText("CEDENTE");
				run.setFontSize(9);
				run.setBold(false);
				run.addCarriageReturn();
				run.addCarriageReturn();
				
				paragraph = document.createParagraph();
				run = paragraph.createRun();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				run.setText("Ciente: ");
				run.setFontSize(9);
				run.setBold(false);
				run.addCarriageReturn();
				run.addCarriageReturn();
				
				paragraph = document.createParagraph();
				run = paragraph.createRun();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				run.setText("___________________________________________________________");
				run.setFontSize(9);
				run.setBold(false);
				
				paragraph = document.createParagraph();
				run = paragraph.createRun();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				run.setText(this.objetoCcb.getNomeEmitente());
				run.setFontSize(11);
				run.setBold(true);
				run.addCarriageReturn();
				
				paragraph = document.createParagraph();
				run = paragraph.createRun();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				run.setText("EMITENTE");
				run.setFontSize(9);
				run.setBold(false);
				
				headerFooterPolicy = document.getHeaderFooterPolicy();
				if (headerFooterPolicy == null) {
					headerFooterPolicy = document.createHeaderFooterPolicy();
				}
				
				XWPFFooter footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);

				paragraph = footer.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.CENTER);

				run = paragraph.createRun();
				run.setFontSize(10);
				run.setBold(true);
				run.getCTR().addNewFldChar()
						.setFldCharType(org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType.BEGIN);

				run = paragraph.createRun();
				run.setFontSize(10);
				run.setBold(true);
				run.getCTR().addNewInstrText().setStringValue("PAGE \\* MERGEFORMAT");
				
				run = paragraph.createRun();
				run.setFontSize(10);
				run.setBold(true);
				run.getCTR().addNewFldChar().setFldCharType(org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType.END);
				
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				document.write(out);
				document.close();
				final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
						FacesContext.getCurrentInstance());
				String nomeSemvirgula = this.objetoCcb.getNomeEmitente();
				if(nomeSemvirgula.contains(",")) {
					nomeSemvirgula = nomeSemvirgula.replace(",", "");
			    }
				gerador.open(String.format("Galleria Bank - Modelo_NC %s.docx", ""));
				gerador.feed(new ByteArrayInputStream(out.toByteArray()));
				gerador.close();
				
				criarCcbNosistema();
				
			} catch (Throwable e) {
				e.printStackTrace();
			}
			

			return null;
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
			run.setText("São Paulo/SP, " + this.objetoCcb.getDataDeEmissao().getDate() + " de "
							+ CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase() + " de "
							+ (this.objetoCcb.getDataDeEmissao().getYear() + 1900) + ".");
			run.setFontSize(11);
			run.setBold(false);
			run.addCarriageReturn();
			XWPFRun run2 = paragraph.createRun();
			XWPFRun run3 = paragraph.createRun();
			XWPFRun run4 = paragraph.createRun();
			
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
			run.setText("BMP Sociedade de Crédito Direto S.A.");
			run.setFontSize(11);
			run.setBold(true);
			run.addCarriageReturn();
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText("Autorizamos a efetivação de transferência, através da TED, no valor de ");
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
			run.setText("Nome: " + this.objetoCcb.getNomeEmitente());
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
					+ this.objetoCcb.getCartorioImovel() + " RI de " 
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
			run.setText("NOME/RAZÃO SOCIAL: " + this.objetoCcb.getNomeEmitente());
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
			
			ContratoCobrancaDao cDao = new ContratoCobrancaDao();
			if(!CommonsUtil.semValor(this.objetoContratoCobranca)) {
				this.objetoContratoCobranca = cDao.findById(this.objetoCcb.getObjetoContratoCobranca().getId());
				
				this.objetoContratoCobranca.setValorCartaSplit(this.objetoCcb.getValorLiquidoCredito());
				this.objetoContratoCobranca.setNomeBancarioCartaSplit(this.objetoCcb.getNomeEmitente());
				this.objetoContratoCobranca.setCpfCnpjBancarioCartaSplit(this.objetoCcb.getCpfEmitente());
				this.objetoContratoCobranca.setBancoBancarioCartaSplit(this.objetoCcb.getNomeBanco());
				this.objetoContratoCobranca.setAgenciaBancarioCartaSplit(this.objetoCcb.getAgencia());
				this.objetoContratoCobranca.setContaBancarioCartaSplit(this.objetoCcb.getContaCorrente());		
				
				cDao.merge(this.objetoContratoCobranca);
				this.objetoCcb.setObjetoContratoCobranca(objetoContratoCobranca);	
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
			CcbDao ccbDao = new CcbDao();
			XWPFDocument document = new XWPFDocument();
			XWPFHeaderFooterPolicy headerFooterPolicy = document.getHeaderFooterPolicy();
			if (headerFooterPolicy == null)
				headerFooterPolicy = document.createHeaderFooterPolicy();

			XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			XWPFParagraph paragraphHeader = header.createParagraph();
			paragraphHeader.setAlignment(ParagraphAlignment.LEFT);
			XWPFRun runHeader = paragraphHeader.createRun();
			runHeader.addPicture(getClass().getResourceAsStream("/resource/BMP MoneyPlus.png"), 6, "BMP MoneyPlus",
					Units.toEMU(130), Units.toEMU(72));

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
			XWPFRun run3 = paragraph.createRun();
			XWPFRun run4 = paragraph.createRun();
			
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
					+ " da Galleria Correspondente Bancário Eireli, CNPJ 34.787.885/0001-32, Banco do Brasil"
					+ " – Ag: 1515-6 C/C: 131094-1, que, na condição de Correspondente Bancário da BMP,"
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
			tableRow.getCell(0).setColor("8880F4");
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
			tableRow.getCell(1).setColor("8880F4");
			run = tableRow.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setBold(true);
			run.setText("Forma de Pagamento");
			run.setColor("ffffff");	
			
			tableRow.getCell(2).setParagraph(paragraph);
			tableRow.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));
			tableRow.getCell(2).setColor("8880F4");
			tableRow.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			run = tableRow.getCell(2).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setBold(true);
			run.setColor("ffffff");
			run.setText("Valor");
			
			ContratoCobrancaDao cDao = new ContratoCobrancaDao();
			if(!CommonsUtil.semValor(objetoContratoCobranca)) {
				this.objetoContratoCobranca = cDao.findById(this.objetoCcb.getObjetoContratoCobranca().getId());
				this.objetoPagadorRecebedor = objetoContratoCobranca.getPagador();
			}
			
			if(this.temCustasCartorarias) {
				XWPFTableRow tableRow1 = table.createRow();
				
				tableRow1.getCell(0).setParagraph(paragraph);
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setColor("000000");
				run.setText("Custas Cartorárias");

				tableRow1.getCell(1).setParagraph(paragraph);
				tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));
				run = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setText("Boleto");
				run.setColor("000000");	
				
				tableRow1.getCell(2).setParagraph(paragraph);
				tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				tableRow1.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));
				run = tableRow1.getCell(2).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setColor("000000");
				run.setText(CommonsUtil.formataValorMonetario(this.objetoCcb.getCustasCartorariasValor(), "R$ "));
				
				if(!this.objetoCcb.isCustasInseridaContrato()) {
					if(!CommonsUtil.semValor(objetoContratoCobranca)) {
						ContasPagar custasCartorarias = new ContasPagar();				
						custasCartorarias.setDescricao("Cartório");
						custasCartorarias.setValor(this.objetoCcb.getCustasCartorariasValor());
						custasCartorarias.setFormaTransferencia("Boleto");
						/////////////////////////////////////////////////////////////////////////////////////
						custasCartorarias.setContrato(this.objetoContratoCobranca);
						custasCartorarias.setNumeroDocumento(this.objetoContratoCobranca.getNumeroContrato());
						custasCartorarias.setPagadorRecebedor(this.objetoPagadorRecebedor);
						custasCartorarias.setTipoDespesa("C");
						custasCartorarias.setResponsavel(this.objetoContratoCobranca.getResponsavel());
						this.objetoContratoCobranca.getListContasPagar().add(custasCartorarias);
						this.objetoCcb.setCustasInseridaContrato(true);
					}
				}
				
				
			}
			
			if(this.temCertidaoDeCasamento) {
				XWPFTableRow tableRow1 = table.createRow();
				
				tableRow1.getCell(0).setParagraph(paragraph);
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setColor("000000");
				run.setText("Certidão de Casamento");

				tableRow1.getCell(1).setParagraph(paragraph);
				tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));

				run = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setText("Boleto");
				run.setColor("000000");	
				
				tableRow1.getCell(2).setParagraph(paragraph);
				tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				tableRow1.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));

				run = tableRow1.getCell(2).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setColor("000000");
				run.setText(CommonsUtil.formataValorMonetario(this.objetoCcb.getCertidaoDeCasamentoValor(), "R$ "));
				if(!this.objetoCcb.isCertidaoInseridaContrato()) {
					if(!CommonsUtil.semValor(objetoContratoCobranca)) {
						ContasPagar certidaoDeCasamento = new ContasPagar();				
						certidaoDeCasamento.setDescricao("Certidão de Casamento");
						certidaoDeCasamento.setValor(this.objetoCcb.getCertidaoDeCasamentoValor());
						certidaoDeCasamento.setFormaTransferencia("Boleto");
						/////////////////////////////////////////////////////////////////////////////////////
						certidaoDeCasamento.setContrato(this.objetoContratoCobranca);
						certidaoDeCasamento.setNumeroDocumento(this.objetoContratoCobranca.getNumeroContrato());
						certidaoDeCasamento.setPagadorRecebedor(this.objetoPagadorRecebedor);
						certidaoDeCasamento.setTipoDespesa("C");
						certidaoDeCasamento.setResponsavel(this.objetoContratoCobranca.getResponsavel());
						this.objetoContratoCobranca.getListContasPagar().add(certidaoDeCasamento);
						this.objetoCcb.setCertidaoInseridaContrato(true);
					}
				}
			}
			
			if(this.temLaudoDeAvaliacao) {
				XWPFTableRow tableRow1 = table.createRow();
				
				tableRow1.getCell(0).setParagraph(paragraph);
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setColor("000000");
				run.setText("Laudo De Avaliação");

				tableRow1.getCell(1).setParagraph(paragraph);
				tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));

				run = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setText("PIX");
				run.setColor("000000");	
				
				tableRow1.getCell(2).setParagraph(paragraph);
				tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				tableRow1.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));

				run = tableRow1.getCell(2).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setColor("000000");
				run.setText(CommonsUtil.formataValorMonetario(this.objetoCcb.getLaudoDeAvaliacaoValor(), "R$ "));
				if(!this.objetoCcb.isLaudoInseridoContrato()) {
					if(!CommonsUtil.semValor(objetoContratoCobranca)) {
						ContasPagar laudoDeAvaliacao = new ContasPagar();				
						laudoDeAvaliacao.setDescricao("Laudo De Avaliação");
						laudoDeAvaliacao.setValor(this.objetoCcb.getLaudoDeAvaliacaoValor());
						laudoDeAvaliacao.setFormaTransferencia("Boleto");
						/////////////////////////////////////////////////////////////////////////////////////
						laudoDeAvaliacao.setContrato(this.objetoContratoCobranca);
						laudoDeAvaliacao.setNumeroDocumento(this.objetoContratoCobranca.getNumeroContrato());
						laudoDeAvaliacao.setPagadorRecebedor(this.objetoPagadorRecebedor);
						laudoDeAvaliacao.setTipoDespesa("C");
						laudoDeAvaliacao.setResponsavel(this.objetoContratoCobranca.getResponsavel());
						this.objetoContratoCobranca.getListContasPagar().add(laudoDeAvaliacao);
						this.objetoCcb.setLaudoInseridoContrato(true);
					}
				}
			}
			
			if(this.temIntermediacao) {
				XWPFTableRow tableRow1 = table.createRow();
				
				tableRow1.getCell(0).setParagraph(paragraph);
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setColor("000000");
				run.setText("Transferência");

				tableRow1.getCell(1).setParagraph(paragraph);
				tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));

				run = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setText("Ted no "+ this.objetoCcb.getIntermediacaoBanco() +" AG: "+ this.objetoCcb.getIntermediacaoAgencia()
					+" C/C: "+ this.objetoCcb.getIntermediacaoCC() +" Chave Pix: "+ this.objetoCcb.getIntermediacaoPix()
					+ this.objetoCcb.getIntermediacaoNome() +" CNPJ: "+ this.objetoCcb.getIntermediacaoCNPJ() );
				run.setColor("000000");	
				
				tableRow1.getCell(2).setParagraph(paragraph);
				tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				tableRow1.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));

				run = tableRow1.getCell(2).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setColor("000000");
				run.setText(CommonsUtil.formataValorMonetario(this.objetoCcb.getIntermediacaoValor(), "R$ "));
				if(!this.objetoCcb.isIntermediacaoInseridoContrato()) {
					if(!CommonsUtil.semValor(objetoContratoCobranca)) {
						ContasPagar intermediacao = new ContasPagar();				
						intermediacao.setDescricao("Transferência");
						intermediacao.setValor(this.objetoCcb.getIntermediacaoValor());
						intermediacao.setFormaTransferencia("TED");
						intermediacao.setBancoTed(this.objetoCcb.getIntermediacaoBanco());
						intermediacao.setAgenciaTed(this.objetoCcb.getIntermediacaoAgencia());
						intermediacao.setContaTed(this.objetoCcb.getContaCorrente());
						intermediacao.setNomeTed(this.objetoCcb.getIntermediacaoNome());
						intermediacao.setCpfTed(this.objetoCcb.getIntermediacaoCNPJ());	
						
						this.objetoContratoCobranca.setNomeBancarioContaPagar(this.objetoCcb.getIntermediacaoNome());
						this.objetoContratoCobranca.setCpfCnpjBancarioContaPagar(this.objetoCcb.getIntermediacaoCNPJ());
						this.objetoContratoCobranca.setBancoBancarioContaPagar(this.objetoCcb.getIntermediacaoBanco());
						this.objetoContratoCobranca.setAgenciaBancarioContaPagar(this.objetoCcb.getIntermediacaoAgencia());
						this.objetoContratoCobranca.setContaBancarioContaPagar(this.objetoCcb.getContaCorrente());		
						
						/////////////////////////////////////////////////////////////////////////////////////
						intermediacao.setContrato(this.objetoContratoCobranca);
						intermediacao.setNumeroDocumento(this.objetoContratoCobranca.getNumeroContrato());
						intermediacao.setPagadorRecebedor(this.objetoPagadorRecebedor);
						intermediacao.setTipoDespesa("C");
						intermediacao.setResponsavel(this.objetoContratoCobranca.getResponsavel());
						this.objetoContratoCobranca.getListContasPagar().add(intermediacao);
						this.objetoCcb.setIntermediacaoInseridoContrato(true);
					}
				}
			}
			
			if(this.temCCBValor) {
				XWPFTableRow tableRow1 = table.createRow();
				
				tableRow1.getCell(0).setParagraph(paragraph);
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setColor("000000");
				run.setText("Crédito CCI/CCB");

				tableRow1.getCell(1).setParagraph(paragraph);
				tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));

				run = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setText("Ted no "+ this.objetoCcb.getCCBBanco() +" AG: "+ this.objetoCcb.getCCBAgencia()
					+" C/C: "+ this.objetoCcb.getCCBCC() +" Chave Pix: "+ this.objetoCcb.getCCBPix()
					+ this.objetoCcb.getCCBNome() +" CNPJ: "+ this.objetoCcb.getCCBCNPJ() );
				run.setColor("000000");	
				
				tableRow1.getCell(2).setParagraph(paragraph);
				tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				tableRow1.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));

				run = tableRow1.getCell(2).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setColor("000000");
				run.setText(CommonsUtil.formataValorMonetario(this.objetoCcb.getCCBValor(), "R$ "));
				if(!this.objetoCcb.isCCBInseridoContrato()) {
					if(!CommonsUtil.semValor(objetoContratoCobranca)) {
						ContasPagar CCB = new ContasPagar();				
						CCB.setDescricao("Crédito CCB/CCI");
						CCB.setValor(this.objetoCcb.getCCBValor());
						CCB.setFormaTransferencia("TED");
						CCB.setBancoTed(this.objetoCcb.getCCBBanco());
						CCB.setAgenciaTed(this.objetoCcb.getCCBAgencia());
						CCB.setContaTed(this.objetoCcb.getCCBCC());
						CCB.setNomeTed(this.objetoCcb.getCCBNome());
						CCB.setCpfTed(this.objetoCcb.getCCBCNPJ());	
						
						this.objetoContratoCobranca.setNomeBancarioContaPagar(this.objetoCcb.getCCBNome());
						this.objetoContratoCobranca.setCpfCnpjBancarioContaPagar(this.objetoCcb.getCCBCNPJ());
						this.objetoContratoCobranca.setBancoBancarioContaPagar(this.objetoCcb.getCCBBanco());
						this.objetoContratoCobranca.setAgenciaBancarioContaPagar(this.objetoCcb.getCCBAgencia());
						this.objetoContratoCobranca.setContaBancarioContaPagar(this.objetoCcb.getContaCorrente());		
						
						/////////////////////////////////////////////////////////////////////////////////////
						CCB.setContrato(this.objetoContratoCobranca);
						CCB.setNumeroDocumento(this.objetoContratoCobranca.getNumeroContrato());
						CCB.setPagadorRecebedor(this.objetoPagadorRecebedor);
						CCB.setTipoDespesa("C");
						CCB.setResponsavel(this.objetoContratoCobranca.getResponsavel());
						this.objetoContratoCobranca.getListContasPagar().add(CCB);
						this.objetoCcb.setCCBInseridoContrato(true);
					}
				}
			}
			
			if (this.temProcessosJucidiais) {
				for (CcbProcessosJudiciais processo : this.objetoCcb.getProcessosJucidiais()) {
					XWPFTableRow tableRow1 = table.createRow();
					tableRow1.getCell(0).setParagraph(paragraph);
					tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

					run = tableRow1.getCell(0).getParagraphArray(0).createRun();
					run.setFontSize(12);
					run.setColor("000000");
					run.setText("Processo N° " + processo.getNumero());

					tableRow1.getCell(1).setParagraph(paragraph);
					tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
					tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));

					run = tableRow1.getCell(1).getParagraphArray(0).createRun();
					run.setFontSize(12);
					run.setText("Boleto");
					run.setColor("000000");

					tableRow1.getCell(2).setParagraph(paragraph);
					tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
					tableRow1.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));

					run = tableRow1.getCell(2).getParagraphArray(0).createRun();
					run.setFontSize(12);
					run.setColor("000000");
					run.setText(CommonsUtil.formataValorMonetario(processo.getValor()));
					if(!processo.isProcessoInseridoContrato()) {
						if(!CommonsUtil.semValor(objetoContratoCobranca)) {
							ContasPagar contaProcesso = new ContasPagar();				
							contaProcesso.setDescricao("Processo");
							contaProcesso.setValor(processo.getValor());
							contaProcesso.setFormaTransferencia("Boleto");
							/////////////////////////////////////////////////////////////////////////////////////
							contaProcesso.setContrato(this.objetoContratoCobranca);
							contaProcesso.setNumeroDocumento(this.objetoContratoCobranca.getNumeroContrato());
							contaProcesso.setPagadorRecebedor(this.objetoPagadorRecebedor);
							contaProcesso.setTipoDespesa("C");
							contaProcesso.setResponsavel(this.objetoContratoCobranca.getResponsavel());
							this.objetoContratoCobranca.getListContasPagar().add(contaProcesso);
							processo.setProcessoInseridoContrato(true);
						}
					}
				}
			}
			
			if(this.temIptuEmAtraso) {
				XWPFTableRow tableRow1 = table.createRow();
				
				tableRow1.getCell(0).setParagraph(paragraph);
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setColor("000000");
				run.setText("IPTU em Atraso");

				tableRow1.getCell(1).setParagraph(paragraph);
				tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));

				run = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setText("Boleto");
				run.setColor("000000");	
				
				tableRow1.getCell(2).setParagraph(paragraph);
				tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				tableRow1.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));

				run = tableRow1.getCell(2).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setColor("000000");
				run.setText(CommonsUtil.formataValorMonetario(this.objetoCcb.getIptuEmAtrasoValor(), "R$ "));
				if(!this.objetoCcb.isIptuInseridoContrato()) {
					if(!CommonsUtil.semValor(objetoContratoCobranca)) {
						ContasPagar iptuAtraso = new ContasPagar();				
						iptuAtraso.setDescricao("IPTU");
						iptuAtraso.setValor(this.objetoCcb.getIptuEmAtrasoValor());
						iptuAtraso.setFormaTransferencia("Boleto");
						/////////////////////////////////////////////////////////////////////////////////////
						iptuAtraso.setContrato(this.objetoContratoCobranca);
						iptuAtraso.setNumeroDocumento(this.objetoContratoCobranca.getNumeroContrato());
						iptuAtraso.setPagadorRecebedor(this.objetoPagadorRecebedor);
						iptuAtraso.setTipoDespesa("C");
						iptuAtraso.setResponsavel(this.objetoContratoCobranca.getResponsavel());
						this.objetoContratoCobranca.getListContasPagar().add(iptuAtraso);
						this.objetoCcb.setIptuInseridoContrato(true);
					}
				}
			}
			
			if(this.temCondominioEmAtraso) {
				XWPFTableRow tableRow1 = table.createRow();
				
				tableRow1.getCell(0).setParagraph(paragraph);
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setColor("000000");
				run.setText("Condomínio em Atraso");

				tableRow1.getCell(1).setParagraph(paragraph);
				tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));

				run = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setText("Boleto");
				run.setColor("000000");	
				
				tableRow1.getCell(2).setParagraph(paragraph);
				tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				tableRow1.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));

				run = tableRow1.getCell(2).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setColor("000000");
				run.setText(CommonsUtil.formataValorMonetario(this.objetoCcb.getCondominioEmAtrasoValor(), "R$ "));
				if(!this.objetoCcb.isCondominioInseridoContrato()) {
					if(!CommonsUtil.semValor(objetoContratoCobranca)) {
						ContasPagar comdominioEmAtraso = new ContasPagar();				
						comdominioEmAtraso.setDescricao("Condomínio");
						comdominioEmAtraso.setValor(this.objetoCcb.getCondominioEmAtrasoValor());
						comdominioEmAtraso.setFormaTransferencia("Boleto");
						/////////////////////////////////////////////////////////////////////////////////////
						comdominioEmAtraso.setContrato(this.objetoContratoCobranca);
						comdominioEmAtraso.setNumeroDocumento(this.objetoContratoCobranca.getNumeroContrato());
						comdominioEmAtraso.setPagadorRecebedor(this.objetoPagadorRecebedor);
						comdominioEmAtraso.setTipoDespesa("C");
						comdominioEmAtraso.setResponsavel(this.objetoContratoCobranca.getResponsavel());
						this.objetoContratoCobranca.getListContasPagar().add(comdominioEmAtraso);
						this.objetoCcb.setCondominioInseridoContrato(true);
					}
				}
			}
			
			if(this.temIq) {
				XWPFTableRow tableRow1 = table.createRow();
				
				tableRow1.getCell(0).setParagraph(paragraph);
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setColor("000000");
				run.setText("IQ");

				tableRow1.getCell(1).setParagraph(paragraph);
				tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));

				run = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setText("Boleto");
				run.setColor("000000");	
				
				tableRow1.getCell(2).setParagraph(paragraph);
				tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				tableRow1.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));

				run = tableRow1.getCell(2).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setColor("000000");
				run.setText(CommonsUtil.formataValorMonetario(this.objetoCcb.getIqValor(), "R$ "));
				if(!this.objetoCcb.isIqInseridoContrato()) {
					if(!CommonsUtil.semValor(objetoContratoCobranca)) {
						ContasPagar iq = new ContasPagar();				
						iq.setDescricao("IQ");
						iq.setValor(this.objetoCcb.getIqValor());
						iq.setFormaTransferencia("Boleto");
						/////////////////////////////////////////////////////////////////////////////////////
						iq.setContrato(this.objetoContratoCobranca);
						iq.setNumeroDocumento(this.objetoContratoCobranca.getNumeroContrato());
						iq.setPagadorRecebedor(this.objetoPagadorRecebedor);
						iq.setTipoDespesa("C");
						iq.setResponsavel(this.objetoContratoCobranca.getResponsavel());
						this.objetoContratoCobranca.getListContasPagar().add(iq);
						this.objetoCcb.setIqInseridoContrato(true);
					}
				}
			}
			
			if(this.temItbi) {
				XWPFTableRow tableRow1 = table.createRow();
				
				tableRow1.getCell(0).setParagraph(paragraph);
				tableRow1.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setColor("000000");
				run.setText("ITBI");

				tableRow1.getCell(1).setParagraph(paragraph);
				tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));

				run = tableRow1.getCell(1).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setText("Boleto");
				run.setColor("000000");	
				
				tableRow1.getCell(2).setParagraph(paragraph);
				tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
				tableRow1.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));

				run = tableRow1.getCell(2).getParagraphArray(0).createRun();
				run.setFontSize(12);
				run.setColor("000000");
				run.setText(CommonsUtil.formataValorMonetario(this.objetoCcb.getItbiValor(), "R$ "));
				if(!this.objetoCcb.isItbiInseridoContrato()) {
					if(!CommonsUtil.semValor(objetoContratoCobranca)) {
						ContasPagar itbi = new ContasPagar();				
						itbi.setDescricao("ITBI");
						itbi.setValor(this.objetoCcb.getItbiValor());
						itbi.setFormaTransferencia("Boleto");
						/////////////////////////////////////////////////////////////////////////////////////
						itbi.setContrato(this.objetoContratoCobranca);
						itbi.setNumeroDocumento(this.objetoContratoCobranca.getNumeroContrato());
						itbi.setPagadorRecebedor(this.objetoPagadorRecebedor);
						itbi.setTipoDespesa("C");
						itbi.setResponsavel(this.objetoContratoCobranca.getResponsavel());
						this.objetoContratoCobranca.getListContasPagar().add(itbi);
						this.objetoCcb.setIqInseridoContrato(true);
					}
				}
			}
			
			XWPFTableRow tableRow1 = table.createRow();		

			tableRow1.getCell(1).setParagraph(paragraph);
			tableRow1.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			tableRow1.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));		
			tableRow1.getCell(1).setColor("8880F4");

			run = tableRow1.getCell(1).getParagraphArray(0).createRun();
			run.setFontSize(12);
			run.setBold(true);
			run.setText("Total");
			run.setColor("ffffff");	
			
			tableRow1.getCell(2).setParagraph(paragraph);
			tableRow1.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
			tableRow1.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(CommonsUtil.longValue(2800) ));
			tableRow1.getCell(2).setColor("8880F4");

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
			run2.setFontSize(12);
			run2.setText("" + this.objetoCcb.getNomeEmitente());
			run2.setBold(true);
			run2.addCarriageReturn();
			run2.setText(documento + this.objetoCcb.getCpfEmitente());
			
			if(!CommonsUtil.semValor(objetoContratoCobranca)) {
				objetoContratoCobranca.setContaPagarValorTotal(this.objetoCcb.getValorDespesas());
				cDao.merge(this.objetoContratoCobranca);
				this.objetoCcb.setObjetoContratoCobranca(objetoContratoCobranca);		
			}
			
			/*
			 * for (XWPFParagraph p : document.getParagraphs()) { List<XWPFRun> runs =
			 * p.getRuns(); if (runs != null) { for (XWPFRun r : runs) { String text =
			 * r.getText(0); adicionarEnter(text, r); } } }
			 */

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
			e.getCause().printStackTrace();
		} 

		return null;
	}
	
	public StreamedContent geraCciDinamica() throws IOException {
		try {
			CcbDao ccbDao = new CcbDao();
			XWPFDocument document = new XWPFDocument();
			XWPFHeaderFooterPolicy headerFooterPolicy = document.getHeaderFooterPolicy();
			if (headerFooterPolicy == null)
				headerFooterPolicy = document.createHeaderFooterPolicy();


			XWPFRun run;
			XWPFParagraph paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			
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
			XWPFRun run2 = paragraph.createRun();
			XWPFRun run3 = paragraph.createRun();
			XWPFRun run4 = paragraph.createRun();
			
			/*
			 * <w:tblPr>
			 *	<w:tblW w:w="0" w:type="auto"/>
			 *  <w:tblCellMar>
			 *  	<w:left w:w="70" w:type="dxa"/>
			 *  	<w:right w:w="70" w:type="dxa"/>
			 *  </w:tblCellMar>
			 *  <w:tblLook w:val="04A0" w:firstRow="1" w:lastRow="0" w:firstColumn="1" w:lastColumn="0" w:noHBand="0" w:noVBand="1"/>
			 * </w:tblPr>
			 * <w:tblGrid> 
			 *   	<w:gridCol w:w="4389"/>
			 * 		<w:gridCol w:w="5240"/> 
			 * </w:tblGrid>
			 */
			
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
			
//			table.setWidth((int) (6.1 * 1440));
//			table.getCTTbl().getTblPr().getTblW().unsetType();
			setTableAlign(table, ParagraphAlignment.CENTER);

//			table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(6000));
//			table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(2500));
			
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
	
	public void setTableAlign(XWPFTable table,ParagraphAlignment align) {
	    CTTblPr tblPr = table.getCTTbl().getTblPr();
	    CTJc jc = (tblPr.isSetJc() ? tblPr.getJc() : tblPr.addNewJc());
	    STJc.Enum en = STJc.Enum.forInt(align.getValue());
	    jc.setVal(en);
	}
	
	public void criarParagrafo(XWPFDocument document, XWPFParagraph paragraph, ParagraphAlignment alinhamento ){
		paragraph = document.createParagraph();
		paragraph.setAlignment(alinhamento);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		paragraph.setSpacingBetween(1);
	}
	
	public void criarParagrafo(XWPFDocument document, XWPFParagraph paragraph, ParagraphAlignment alinhamento, BigInteger numID ){
		paragraph = document.createParagraph();
		paragraph.setNumID(numID);
		paragraph.setAlignment(alinhamento);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		paragraph.setSpacingBetween(1);
	}
	
	public void criarRun(XWPFParagraph paragraph, XWPFRun run, String texto, boolean bold, boolean tab ) {
		run = paragraph.createRun();
		run.setFontSize(12);
		run.setText(texto);
		if(tab) {
			run.addTab();
		}
		run.setBold(bold);
	}
	
	public void criarRun(XWPFParagraph paragraph, XWPFRun run, String texto, boolean bold, UnderlinePatterns underline ) {
		run = paragraph.createRun();
		run.setFontSize(12);
		run.setText(texto);
		run.setBold(bold);
		run.setUnderline(underline);
	}
	
	public void criarRun(XWPFParagraph paragraph, XWPFRun run, String texto, String texto2, boolean bold, boolean tab ) {
		run = paragraph.createRun();
		run.setFontSize(12);
		run.setText(texto);
		if(tab) {
			run.addTab();
		}
		run.setText(texto2);
		run.setBold(bold);
	}
	
	private void geraParagrafoPF(XWPFRun run2, CcbParticipantes participante){
		run2.setFontSize(12);
		String filho;
		if(participante.isFeminino()) {
			filho = "filha";
		} else {
			filho = "filho";
		}
		String nacionalidade = null;
		String estadoCivilStr = "";
		String conjugeStr = "";
		
		PagadorRecebedor pessoa = participante.getPessoa();

		if (CommonsUtil.mesmoValor(participante.getNacionalidade(), "brasileiro")) {
			if (participante.isFeminino() == true) {
				nacionalidade = "brasileira";
			} else {
				nacionalidade = participante.getNacionalidade();
			}
		} else {
			nacionalidade = participante.getNacionalidade();
		}

		if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "CASADO")) {
			if (participante.isFeminino() == true) {
				estadoCivilStr = "casada";
			} else {
				estadoCivilStr = "casado";
			}		
			conjugeStr = ", sob o regime de comunhão " + pessoa.getRegimeCasamento() + ", na vigência da lei 6.515/77 (" + 
					pessoa.getNomeConjuge() + " " + pessoa.getCpfConjuge() + "), conforme pacto antenupcial registrado no "+
					pessoa.getRegistroPactoAntenupcial() + ", sob livro " + pessoa.getLivroPactoAntenupcial() + ", folhas " + 
					pessoa.getFolhasPactoAntenupcial() + ", datada de " + CommonsUtil.formataData(pessoa.getDataPactoAntenupcial()) ;
					;
		} else {
			if (participante.isFeminino()) {
				if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "SOLTEIRO")) {
					estadoCivilStr = "solteira";
				} else if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "VIÚVO")) {
					estadoCivilStr = "viúva";
				} else if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "DIVORCIADO")) {
					estadoCivilStr = "divorciada";
				} else if (CommonsUtil.mesmoValor(pessoa.getEstadocivil(), "SEPARADO")) {
					estadoCivilStr = "separada";
				} 
			} else {
				estadoCivilStr = pessoa.getEstadocivil().toLowerCase();
			}

			if(participante.isUniaoEstavel()) {
				estadoCivilStr = estadoCivilStr + " convivente em união estável";
			} else {
				estadoCivilStr = estadoCivilStr + " não convivente em união estável";
			}
		}
		
		run2.setText( filho + " de " + pessoa.getNomeMae() + " e " + pessoa.getNomePai() + ", "
				+ nacionalidade + ", "+ pessoa.getAtividade() + ", "+ estadoCivilStr 
				+ conjugeStr + ","
				+ " portador(a) da Cédula de Identidade RG nº "+ pessoa.getRg() + "SSP/"+ pessoa.getEstado() +","
				+ " inscrito(a) no CPF/MF sob o nº "+ pessoa.getCpf() +", endereço eletrônico: "+ pessoa.getEmail() +","
				+ " residente e domiciliado à "+ pessoa.getEndereco() +", nº "+ pessoa.getNumero() +", "
				+ pessoa.getComplemento() + ", "+ pessoa.getBairro() + ", " 
				+ pessoa.getCidade()+"/"+pessoa.getEstado()+", CEP "+ pessoa.getCep()+"; ");
	}
	
	public void fazParagrafoSimples(XWPFDocument document, XWPFParagraph paragraph, XWPFRun run, String texto, boolean bold) {
		paragraph = document.createParagraph();		
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		paragraph.setSpacingBetween(1);
		run = paragraph.createRun();
		run.setFontSize(12);
		run.setText(texto);
		run.setBold(bold);
		run.addCarriageReturn();
	}
	
	public void fazParagrafoSimplesSemReturn(XWPFDocument document, XWPFParagraph paragraph, XWPFRun run, String texto, boolean bold) {
		paragraph = document.createParagraph();		
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		paragraph.setSpacingBetween(1);
		run = paragraph.createRun();
		run.setFontSize(12);
		run.setText(texto);
		run.setBold(bold);
	}
	
	public void fazParagrafoSimples(XWPFDocument document, XWPFParagraph paragraph, XWPFRun run, String texto, boolean bold, ParagraphAlignment alinhamento) {
		paragraph = document.createParagraph();	
		paragraph.setAlignment(alinhamento);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		paragraph.setSpacingBetween(1);
		run = paragraph.createRun();
		run.setFontSize(12);
		run.setText(texto);
		run.setBold(bold);
		run.addCarriageReturn();
	}
	
	public void geraParagrafoComposto(XWPFDocument document, XWPFParagraph paragraph, XWPFRun run, XWPFRun run2, String texto, 
			String texto2, boolean bold, boolean bold2) {
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.BOTH);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		paragraph.setSpacingBetween(1);
		run = paragraph.createRun();
		run.setFontSize(12);
		run.setText(texto);
		run.setBold(bold);
		run2 = paragraph.createRun();
		run2.setFontSize(12);
		run2.setText(texto2);
		run2.setBold(bold2);
		run2.addCarriageReturn();
	}
	
	public void geraParagrafoCompostoSemReturn(XWPFDocument document, XWPFParagraph paragraph, XWPFRun run, XWPFRun run2, String texto, 
			String texto2, boolean bold, boolean bold2) {
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.BOTH);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		paragraph.setSpacingBetween(1);
		run = paragraph.createRun();
		run.setFontSize(12);
		run.setText(texto);
		run.setBold(bold);
		run2 = paragraph.createRun();
		run2.setFontSize(12);
		run2.setText(texto2);
		run2.setBold(bold2);
	}
	
	public void geraParagrafoComposto(XWPFDocument document, XWPFParagraph paragraph, XWPFRun run, XWPFRun run2, String texto, 
			String texto2, boolean bold, boolean bold2, ParagraphAlignment alinhamento) {
		paragraph = document.createParagraph();
		paragraph.setAlignment(alinhamento);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		paragraph.setSpacingBetween(1);
		run = paragraph.createRun();
		run.setFontSize(12);
		run.setText(texto);
		run.setBold(bold);
		run2 = paragraph.createRun();
		run2.setFontSize(12);
		run2.setText(texto2);
		run2.setBold(bold2);
		run2.addCarriageReturn();
	}
	
	public void geraParagrafoBulletList(XWPFDocument document, XWPFParagraph paragraph, XWPFRun run, BigInteger numID, String texto, boolean bold) {
		paragraph = document.createParagraph();
		paragraph.setNumID(numID);
		paragraph.setSpacingBetween(1);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		paragraph.setAlignment(ParagraphAlignment.BOTH);
		run = paragraph.createRun();
		run.setFontSize(12);
		run.setText(texto);
		run.setBold(bold);
		run.addCarriageReturn();
	}
	
	public void geraParagrafoBulletListSemReturn(XWPFDocument document, XWPFParagraph paragraph, XWPFRun run, BigInteger numID, String texto, boolean bold) {
		paragraph = document.createParagraph();
		paragraph.setNumID(numID);
		paragraph.setSpacingBetween(1);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		paragraph.setAlignment(ParagraphAlignment.BOTH);
		run = paragraph.createRun();
		run.setFontSize(12);
		run.setText(texto);
		run.setBold(bold);
	}
	
	public void geraParagrafoBulletListComposta(XWPFDocument document, XWPFParagraph paragraph, XWPFRun run, XWPFRun run2, String texto, 
			String texto2, boolean bold, boolean bold2, BigInteger numID, UnderlinePatterns underline) {
		paragraph = document.createParagraph();
		paragraph.setNumID(numID);
		paragraph.setAlignment(ParagraphAlignment.BOTH);
		paragraph.setSpacingBefore(0);
		paragraph.setSpacingAfter(0);
		paragraph.setSpacingBetween(1);
		run = paragraph.createRun();
		run.setFontSize(12);
		run.setText(texto);
		run.setBold(bold);
		run.setUnderline(underline);
		run2 = paragraph.createRun();
		run2.setFontSize(12);
		run2.setText(texto2);
		run2.setBold(bold2);
		run2.addCarriageReturn();
	}
	
	public StreamedContent geraCci() throws IOException{
		try {
			XWPFDocument document;	
			XWPFRun run;
			XWPFRun run2;
			XWPFRun run3;
			XWPFRun run4;
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "TERCEIRO GARANTIDOR")) {
					this.objetoCcb.setTerceiroGarantidor(true);
					participante.setTipoParticipante("DEVEDOR FIDUCIANTE");
				}
			}
			if(this.objetoCcb.isTerceiroGarantidor()) {
				document = new XWPFDocument(getClass().getResourceAsStream("/resource/CciTg.docx"));
			} else {
				document = new XWPFDocument(getClass().getResourceAsStream("/resource/Cci.docx"));
				/*
				 * document.getDocument().getBody().removeTbl(0);
				 * //document.getDocument().getBody().removeTbl(1); int i = 0; for(XWPFParagraph
				 * p : document.getParagraphs()) { document.getDocument().getBody().removeP(i);
				 * }
				 * 
				 * for(XWPFTable p : document.getTables()) {
				 * document.getDocument().getBody().removeTbl(i); }
				 */
				
			}		
			CTFonts fonts = CTFonts.Factory.newInstance();
			fonts.setHAnsi("Calibri");
			fonts.setAscii("Calibri");
			fonts.setEastAsia("Calibri");
			fonts.setCs("Calibri");
			document.getStyles().setDefaultFonts(fonts);
			document.getStyle().getDocDefaults().getRPrDefault().getRPr().setRFonts(fonts);
			
			
		
			int indexSegurados = 40;
			
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
			paragraph.setAlignment(ParagraphAlignment.LEFT);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			int iParticipante = 0;
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {										
				run = tableRow1.getCell(0).getParagraphArray(0).createRun();	
				run.setFontSize(12);
				run.setText(alphabet[iParticipante] + ") ");
				run.setBold(true);
				run2 = tableRow1.getCell(0).getParagraphArray(0).createRun();
				run2.setText(" " + participante.getPessoa().getNome() + ", ");
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
						runSocios.setText(" " + sociosParticipante.getPessoa().getNome() + ", ");
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
			            
			            text = trocaValoresXWPF(text, r, "nomeEmitente", this.objetoCcb.getNomeEmitente());	 		
			            text = trocaValoresXWPF(text, r, "porcentagemImovel", CommonsUtil.formataValorMonetarioCci(this.objetoCcb.getPorcentagemImovel(), ""));	 		
			            text = trocaValoresTaxaExtensoXWPF(text, r, "PorcentagemImovel", this.objetoCcb.getPorcentagemImovel());
						text = trocaValoresXWPF(text, r, "emissaoDia", this.objetoCcb.getDataDeEmissao().getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(this.objetoCcb.getDataDeEmissao()).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (this.objetoCcb.getDataDeEmissao().getYear() + 1900));		
			        }
			    }
			}	
						
			BigDecimal taxaAdm = SiscoatConstants.TAXA_ADM.multiply(BigDecimal.valueOf( Long.parseLong(this.objetoCcb.getPrazo()) - Long.parseLong(this.objetoCcb.getNumeroParcelasPagamento()) + 1));
			BigDecimal totalPrimeiraParcela = BigDecimal.ZERO;
			totalPrimeiraParcela = this.objetoCcb.getValorMipParcela();
			totalPrimeiraParcela = totalPrimeiraParcela.add(this.objetoCcb.getValorDfiParcela());
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
								text = trocaValoresXWPF(text, r, "nomeEmitente", this.objetoCcb.getNomeEmitente());	 		
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
			
			calcularSimulador();
			

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
			XWPFRun run3;
			XWPFRun run4;
			for (CcbParticipantes participante : this.objetoCcb.getListaParticipantes()) {				
				if (CommonsUtil.mesmoValor(participante.getTipoParticipante(), "TERCEIRO GARANTIDOR")) {
					this.objetoCcb.setTerceiroGarantidor(true);
					participante.setTipoParticipante("DEVEDOR FIDUCIANTE");
				}
			}
			
			document = new XWPFDocument(getClass().getResourceAsStream("/resource/AquisicaoCCI.docx"));
				
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
				
				if(CommonsUtil.mesmoValor(participante.getTipoOriginal(), "TERCEIRO GARANTIDOR")) {
					participante.setTipoParticipante("Vendedor");
				
					run = tableRowAux.getCell(0).getParagraphArray(0).createRun();	
					run.setFontSize(12);
					run.setText(participante.getTipoParticipante() + " " + (iParticipante + 1));
					run.setBold(true);
					run2 = tableRowAux.getCell(0).getParagraphArray(0).createRun();
					run2.setText(" " + participante.getPessoa().getNome() + ", ");
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
							runSocios.setText(" " + sociosParticipante.getPessoa().getNome() + ", ");
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
					run2.setText(" " + participante.getPessoa().getNome() + ", ");
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
							runSocios.setText(" " + sociosParticipante.getPessoa().getNome() + ", ");
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
			            
			            text = trocaValoresXWPF(text, r, "nomeEmitente", this.objetoCcb.getNomeEmitente());	 		
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
			totalPrimeiraParcela = this.objetoCcb.getValorMipParcela();
			totalPrimeiraParcela = totalPrimeiraParcela.add(this.objetoCcb.getValorDfiParcela());
			totalPrimeiraParcela = totalPrimeiraParcela.add(this.objetoCcb.getValorParcela());
			totalPrimeiraParcela = totalPrimeiraParcela.add(taxaAdm);
			
			BigDecimal despesas = this.objetoCcb.getValorDespesas();
			despesas = despesas.subtract(this.objetoCcb.getCustasCartorariasValor());
			despesas = despesas.subtract(this.objetoCcb.getItbiValor());
			
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
								
								text = trocaValoresXWPF(text, r, "nomeEmitente", this.objetoCcb.getNomeEmitente());	 		
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
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getValorParcela(), "R$ "));
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
			XWPFRun run3;
			XWPFRun run4;
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
				
				if(CommonsUtil.mesmoValor(participante.getTipoOriginal(), "TERCEIRO GARANTIDOR")) {
					participante.setTipoParticipante("Vendedor");
				
					run = tableRowAux.getCell(0).getParagraphArray(0).createRun();	
					run.setFontSize(12);
					run.setText(participante.getTipoParticipante() + " " + (iParticipante + 1));
					run.setBold(true);
					run2 = tableRowAux.getCell(0).getParagraphArray(0).createRun();
					run2.setText(" " + participante.getPessoa().getNome() + ", ");
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
							runSocios.setText(" " + sociosParticipante.getPessoa().getNome() + ", ");
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
					run2.setText(" " + participante.getPessoa().getNome() + ", ");
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
							runSocios.setText(" " + sociosParticipante.getPessoa().getNome() + ", ");
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
			            
			            text = trocaValoresXWPF(text, r, "nomeEmitente", this.objetoCcb.getNomeEmitente());	 		
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
				tableRow1.createCell();
				tableRow1.getCell(7).getCTTc().addNewTcPr();				
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
				table.getRow(indexSegurados).getCell(7).getCTTc().getTcPr().setHMerge(hMerge1);
				
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
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);	
				border.getBottom().setColor("808080");
				border.getTop().setColor("808080");
				border = tableRow1.getCell(7).getCTTc().addNewTcPr().addNewTcBorders();	
				border.addNewRight().setVal(STBorder.TRIPLE);
				border.addNewBottom().setVal(STBorder.SINGLE);
				border.addNewTop().setVal(STBorder.SINGLE);
				border.addNewLeft().setVal(STBorder.SINGLE);	
				border.getRight().setColor("808080");
				border.getBottom().setColor("808080");
				border.getTop().setColor("808080");
				border.getLeft().setColor("808080");
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
			despesas = despesas.subtract(this.objetoCcb.getCustasCartorariasValor());
			despesas = despesas.subtract(this.objetoCcb.getItbiValor());
			
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
								
								text = trocaValoresXWPF(text, r, "nomeEmitente", this.objetoCcb.getNomeEmitente());	 		
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
				run.setText(CommonsUtil.formataValorMonetarioCci(p.getValorParcela(), "R$ "));
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
						text = trocaValoresXWPF(text, r, "nomeEmitente", this.objetoCcb.getNomeEmitente());				
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
										
		    for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {}
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
			gerador.open(String.format("Galleria Bank - CESSAO %s.docx", ""));
			gerador.feed(new ByteArrayInputStream(out.toByteArray()));
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
					            
								text = trocaValoresXWPF(text, r, "nomeEmitente", this.objetoCcb.getNomeEmitente());	
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
			gerador.open(String.format("Galleria Bank - Instrumento Emissão CCI BMP %s.docx", ""));
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
						text = trocaValoresXWPF(text, r, "nomeEmitente", this.objetoCcb.getNomeEmitente());	
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

	public void setTableAlignment(XWPFTable table, STJc.Enum justification) {
	    CTTblPr tblPr = table.getCTTbl().getTblPr();
	    CTJc jc = (tblPr.isSetJc() ? tblPr.getJc() : tblPr.addNewJc());
	    jc.setVal(justification);
	}
	
	@SuppressWarnings("resource")
	public StreamedContent readXWPFile() throws IOException {
		//FacesContext context = FacesContext.getCurrentInstance();
		
	    try {
	    	String tipoDownload = this.getTipoDownload();
	    	
	    	XWPFDocument document = new XWPFDocument();
	    
	    	
	    	if (CommonsUtil.mesmoValor(tipoDownload,"CCB")) {
	    		if(CommonsUtil.mesmoValor(this.addTerceiro, true)) {
	    			document = new XWPFDocument(getClass().getResourceAsStream("/resource/TG.docx"));
	    		} else {
	    			document = new XWPFDocument(getClass().getResourceAsStream("/resource/CCB.docx"));
	    		}
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"AF")) {
	    		document = new XWPFDocument(getClass().getResourceAsStream("/resource/AF.docx"));
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"NC")) {
	    		document = new XWPFDocument(getClass().getResourceAsStream("/resource/NC.docx"));
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"Excel")) {
	    		this.readXLSXFile();
	    		return null;
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"teste")){
	    		this.geraCcbDinamica();
	    		return null;
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"CCBnova")){
	    		clearDocumentosNovos();
	    		return geraCcbDinamica();
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"AFnova")){
	    		clearDocumentosNovos();
	    		return geraAFDinamica();
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"NCnova")){
	    		clearDocumentosNovos();
	    		return geraNCDinamica();
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
		    	return geraCciFinanciamento();
		    } else {
	    		
	    	}
	    	
	    	
	    	
	    	
	    	//BufferedImage picture = ImageIO.read(getClass().getResourceAsStream("/resource/GalleriaBank.png")); 
	    	//RenderedImage picture = ImageIO.read(getClass().getResourceAsStream("/resource/GalleriaBank.png")); 
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
	    	
	    	populatePagadores();
	    	
			criarPagadorRecebedorNoSistema(this.emitenteSelecionado);
			
			if (this.addInterveniente == true) {
				criarPagadorRecebedorNoSistema(this.intervenienteSelecionado);
			}
			
			if (this.addTerceiro == true) {
				criarPagadorRecebedorNoSistema(this.terceiroGSelecionado);
			}
			
			if (this.addAvalista == true) {
				criarPagadorRecebedorNoSistema(this.avalistaSelecionado);
			}

			
			for (XWPFParagraph p : document.getParagraphs()) {}
			
			for (XWPFTable tbl : document.getTables()) {}
			
			
			ByteArrayOutputStream  out = new ByteArrayOutputStream ();

			document.write(out); 
			document.close();
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
			
			if (CommonsUtil.mesmoValor(tipoDownload,"CCB")) {
				gerador.open(String.format("Galleria Bank - Modelo_CCB %s.docx", ""));
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"AF")) {
	    		gerador.open(String.format("Galleria Bank - Modelo_AF %s.docx", ""));
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"NC")) {
	    		gerador.open(String.format("Galleria Bank - Modelo_NC %s.docx", ""));
	    	} else {
	    		gerador.open(String.format("teste %s.docx", ""));	    	
	    	}

			
			gerador.feed(new ByteArrayInputStream(out.toByteArray()));
			gerador.close();
			
	    } catch (Exception e) {
			//context.addMessage(null,
			//		new FacesMessage(FacesMessage.SEVERITY_ERROR,
			//				"Contrato de Cobrança: Ocorreu um problema ao gerar o documento!  " + e + ";" + e.getCause(),
			//				""));
	    }  
	    return null;
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

	private void gravaCelula(Integer celula, Double value, XSSFRow linha) {
		if (linha.getCell(celula) == null)
			linha.createCell(celula);
		linha.getCell(celula).setCellValue(value);
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
	
	public void verificaModeloAntigo() {
		if (CommonsUtil.mesmoValor(tipoDownload,"CCB")) {
			this.setModeloAntigo(true);
    	} else if(CommonsUtil.mesmoValor(tipoDownload,"AF")) {
    		this.setModeloAntigo(true);
    	} else if(CommonsUtil.mesmoValor(tipoDownload,"NC")) {
    		this.setModeloAntigo(true);
    	} else if(CommonsUtil.mesmoValor(tipoDownload,"Excel")) {
    		this.setModeloAntigo(false);
    	} else if(CommonsUtil.mesmoValor(tipoDownload,"teste")){
    		this.setModeloAntigo(false);
    	} else if(CommonsUtil.mesmoValor(tipoDownload,"CCBnova")){
    		this.setModeloAntigo(false);
    	} else if(CommonsUtil.mesmoValor(tipoDownload,"AFnova")){
    		this.setModeloAntigo(false);
    	} else if(CommonsUtil.mesmoValor(tipoDownload,"NCnova")){
    		this.setModeloAntigo(false);
    	} else {
    		this.setModeloAntigo(false);
    	}
	}

	public StreamedContent readXLSXFile() throws IOException {

		// String sheetName
		// =getClass().getResource("/resource/SeguroDFI.xlsx").getPath();
		XSSFWorkbook wb = new XSSFWorkbook(getClass().getResourceAsStream("/resource/Excel.xlsx"));

		XSSFSheet sheet = wb.getSheetAt(0);

		int iLinha = 0;
		XSSFRow linha = sheet.getRow(iLinha);
		
		
		linha.getCell(0).setCellValue(this.getNomeEmitente());
	

		iLinha++;

		ByteArrayOutputStream fileOut = new ByteArrayOutputStream();

		// escrever tudo o que foi feito no arquivo
		wb.write(fileOut);

		// fecha a escrita de dados nessa planilha
		wb.close();

		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
				FacesContext.getCurrentInstance());

		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");

		gerador.open(String.format("Galleria Bank - SeguradoTabelaMIP %s.xlsx", ""));
		gerador.feed(new ByteArrayInputStream(fileOut.toByteArray()));
		gerador.close();

		return null;
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
		if (this.objetoCcb.getValorCredito() != null && this.objetoCcb.getCustoEmissao() != null && this.objetoCcb.getValorIOF() != null && this.objetoCcb.getValorDespesas() != null)
			this.objetoCcb.setValorLiquidoCredito(
					((this.objetoCcb.getValorCredito().subtract(this.objetoCcb.getCustoEmissao())).subtract(this.objetoCcb.getValorIOF()))
							.subtract(this.objetoCcb.getValorDespesas()));
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
		custoEmissaoPercentual = SiscoatConstants.CUSTO_EMISSAO_PERCENTUAL_BRUTO;

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
		simulador.setTipoCalculo(this.objetoCcb.getSistemaAmortizacao());
		simulador.setNaoCalcularDFI(false);
		simulador.setNaoCalcularMIP(false);
		simulador.setNaoCalcularTxAdm(false);
		simulador.calcular();
		
		BigDecimal jurosAoAno = BigDecimal.ZERO;
		jurosAoAno = BigDecimal.ONE.add((simulador.getTaxaJuros().divide(BigDecimal.valueOf(100), MathContext.DECIMAL128)));
		jurosAoAno = CommonsUtil.bigDecimalValue(Math.pow(CommonsUtil.doubleValue(jurosAoAno), 12));
		jurosAoAno = jurosAoAno.subtract(BigDecimal.ONE);
		jurosAoAno = jurosAoAno.multiply(BigDecimal.valueOf(100), MathContext.DECIMAL128);
		jurosAoAno = jurosAoAno.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.simulador.setTaxaJurosAoAno(jurosAoAno);
		
		simulador.setValorCreditoLiberado(simulador.getValorCredito());
		
		if (simulador.getCustoEmissaoValor() != null) {
			simulador.setValorCreditoLiberado(simulador.getValorCreditoLiberado().subtract(simulador.getCustoEmissaoValor()));
		} 
		
		if (simulador.getIOFTotal() != null) {
			simulador.setValorCreditoLiberado(simulador.getValorCreditoLiberado().subtract(simulador.getIOFTotal()));
		}
		
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
		calculaValorLiquidoCredito();
		calculaPorcentagemImovel();
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Percelas Geradas com sucesso", ""));	
	}
	
	public Date getDataHoje() {
		TimeZone zone = TimeZone.getDefault(); 
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHojeCalendar = Calendar.getInstance(zone, locale);
		Date dataHoje = dataHojeCalendar.getTime();
		
		return dataHoje;
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
		this.seguradoSelecionado = new Segurado();
		this.seguradoSelecionado.setPessoa(new PagadorRecebedor());
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
		ContratoCobrancaDao ccDao = new ContratoCobrancaDao();
		loadLovs();	
		clearDespesas();
		this.seguradoSelecionado = new Segurado();
		this.seguradoSelecionado.setPessoa(new PagadorRecebedor());
		this.addSegurador = false;
		this.objetoCcb = new CcbContrato();
		this.objetoCcb.setListaParticipantes(new ArrayList<CcbParticipantes>());
		this.participanteSelecionado = new CcbParticipantes();
		this.participanteSelecionado.setPessoa(new PagadorRecebedor());
		//this.socioSelecionado = new CcbParticipantes();
		//this.socioSelecionado.setPessoa(new PagadorRecebedor());
		this.intervenienteSelecionado = new PagadorRecebedor();
		this.emitenteSelecionado = new PagadorRecebedor();
		this.selectedPagador = new PagadorRecebedor();
		this.objetoCcb.setDataDeEmissao(getDataHoje());
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

	//--------------------------------------------------------------------
		this.nomeInterveniente = null;
		this.nacionalidadeInterveniente = "brasileiro";
		this.profissaoInterveniente = null;
		this.estadoCivilInterveniente = null;
		this.numeroRgInterveniente = null;
		this.ufInterveniente = null;
		this.cpfInterveniente = null;
		this.logradouroInterveniente = null;
		this.numeroInterveniente = null;
		this.complementoInterveniente = null;
		this.cidadeInterveniente = null;
		this.cepInterveniente = null;
		this.addInterveniente = false;
		this.empresaInterveniente = false;
		this.paiInterveniente = null;
		this.maeInterveniente = null;
		
		this.razaoSocialInterveniente = null;
		this.tipoEmpresaInterveniente = null;
		this.cnpjInterveniente = null;
		this.municipioEmpresaInterveniente = null;
		this.estadoEmpresaInterveniente = null;
		this.ruaEmpresaInterveniente = null;
		this.numeroEmpresaInterveniente = null;
		this.salaEmpresaInterveniente = null;
		this.bairroEmpresaInterveniente = null;
		this.cepEmpresaInterveniente = null;
		
		this.emailInterveniente = null;
		this.regimeCasamentoInterveniente = null;
		this.femininoInterveniente = false;

		this.nomeConjugeInterveniente = null;
		this.cpfConjugeInterveniente = null;
		
	//--------------------------------------------------------------------
		this.nomeTerceiroG = null;
		this.nacionalidadeTerceiroG = "brasileiro";
		this.profissaoTerceiroG = null;
		this.estadoCivilTerceiroG = null;
		this.numeroRgTerceiroG = null;
		this.ufTerceiroG = null;
		this.cpfTerceiroG = null;
		this.logradouroTerceiroG = null;
		this.numeroTerceiroG = null;
		this.complementoTerceiroG = null;
		this.cidadeTerceiroG = null;
		this.cepTerceiroG = null;
		this.addTerceiro = false;
		this.empresaTerceiroG = false;
		this.paiTerceiroG = null;
		this.maeTerceiroG = null;
		
		
		this.razaoSocialTerceiroG = null;
		this.tipoEmpresaTerceiroG = null;
		this.cnpjTerceiroG = null;
		this.municipioEmpresaTerceiroG = null;
		this.estadoEmpresaTerceiroG = null;
		this.ruaEmpresaTerceiroG = null;
		this.numeroEmpresaTerceiroG = null;
		this.salaEmpresaTerceiroG = null;
		this.bairroEmpresaTerceiroG = null;
		this.cepEmpresaTerceiroG = null;
		
		this.emailTerceiroG = null;
		this.regimeCasamentoTerceiroG = null;
		this.femininoTerceiroG = false;
		
		this.nomeConjugeTerceiroG = null;
		this.cpfConjugeTerceiroG = null;
	//--------------------------------------------------------------------
		
		this.nomeAvalista = null;
		this.nacionalidadeAvalista = "brasileiro";
		this.profissaoAvalista = null;
		this.estadoCivilAvalista = null;
		this.numeroRgAvalista = null;
		this.ufAvalista = null;
		this.cpfAvalista = null;
		this.logradouroAvalista = null;
		this.numeroAvalista = null;
		this.complementoAvalista = null;
		this.cidadeAvalista = null;
		this.cepAvalista = null;
		this.addAvalista = false;
		this.empresaAvalista = false;
		this.paiAvalista = null;
		this.maeAvalista = null;
		
		this.razaoSocialAvalista = null;
		this.tipoEmpresaAvalista = null;
		this.cnpjAvalista = null;
		this.municipioEmpresaAvalista = null;
		this.estadoEmpresaAvalista = null;
		this.ruaEmpresaAvalista = null;
		this.numeroEmpresaAvalista = null;
		this.salaEmpresaAvalista = null;
		this.bairroEmpresaAvalista = null;
		this.cepEmpresaAvalista = null;
		
		this.emailAvalista = null;
		this.regimeCasamentoAvalista = null;
		this.femininoAvalista = false;
		
		this.nomeConjugeAvalista = null;
		this.cpfConjugeAvalista = null;

	//--------------------------------------------------------------------

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
		
		return "/Atendimento/Cobranca/Ccb.xhtml";
	}
	
	public void loadLovs() {
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listPagadores = pagadorRecebedorDao.findAll();
		
		this.filesList = new ArrayList<UploadedFile>();
	}
	
	public void getEnderecoByViaNet() {
		try {
			String inputCep = this.participanteSelecionado.getPessoa().getCep().replace("-", "");
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
				
			} else {
				myResponse = getJsonSucesso(myURLConnection.getInputStream());

				this.participanteSelecionado.getPessoa().setEndereco(myResponse.get("logradouro").toString());
				this.participanteSelecionado.getPessoa().setBairro(myResponse.get("bairro").toString());
				this.participanteSelecionado.getPessoa().setCidade(myResponse.get("localidade").toString());
				this.participanteSelecionado.getPessoa().setEstado(myResponse.get("uf").toString());
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
				
			} else {
				myResponse = getJsonSucesso(myURLConnection.getInputStream());

				this.socioSelecionado.getPessoa().setEndereco(myResponse.get("logradouro").toString());
				this.socioSelecionado.getPessoa().setBairro(myResponse.get("bairro").toString());
				this.socioSelecionado.getPessoa().setCidade(myResponse.get("localidade").toString());
				this.socioSelecionado.getPessoa().setEstado(myResponse.get("uf").toString());
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

			} else {
				myResponse = getJsonSucesso(myURLConnection.getInputStream());
				this.objetoCcb.setLogradouroRuaImovel(myResponse.get("logradouro").toString());
				this.objetoCcb.setBairroImovel(myResponse.get("bairro").toString());
				this.objetoCcb.setCidadeImovel(myResponse.get("localidade").toString());
				this.objetoCcb.setUfImovel(myResponse.get("uf").toString());
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String RomanNumerals(int Int) {
		LinkedHashMap<String, Integer> roman_numerals = new LinkedHashMap<String, Integer>();
		roman_numerals.put("M", 1000);
		roman_numerals.put("CM", 900);
		roman_numerals.put("D", 500);
		roman_numerals.put("CD", 400);
		roman_numerals.put("C", 100);
		roman_numerals.put("XC", 90);
		roman_numerals.put("L", 50);
		roman_numerals.put("XL", 40);
		roman_numerals.put("X", 10);
		roman_numerals.put("IX", 9);
		roman_numerals.put("V", 5);
		roman_numerals.put("IV", 4);
		roman_numerals.put("I", 1);
		String res = "";
		for (Map.Entry<String, Integer> entry : roman_numerals.entrySet()) {
			int matches = Int / entry.getValue();
			res += repeat(entry.getKey(), matches);
			Int = Int % entry.getValue();
		}
		return res;
	}
	
	char[] alphabet = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};

	public static String repeat(String s, int n) {
			if (s == null) {
				return null;
			}
			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i < n; i++) {
				sb.append(s);
			}
			return sb.toString();
		}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
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

	public String getNomeInterveniente() {
		return nomeInterveniente;
	}

	public void setNomeInterveniente(String nomeInterveniente) {
		this.nomeInterveniente = nomeInterveniente;
	}

	public String getNacionalidadeInterveniente() {
		return nacionalidadeInterveniente;
	}

	public void setNacionalidadeInterveniente(String nacionalidadeInterveniente) {
		this.nacionalidadeInterveniente = nacionalidadeInterveniente;
	}

	public String getProfissaoInterveniente() {
		return profissaoInterveniente;
	}

	public void setProfissaoInterveniente(String profissaoInterveniente) {
		this.profissaoInterveniente = profissaoInterveniente;
	}

	public String getEstadoCivilInterveniente() {
		return estadoCivilInterveniente;
	}

	public void setEstadoCivilInterveniente(String estadoCivilInterveniente) {
		this.estadoCivilInterveniente = estadoCivilInterveniente;
	}

	public String getNumeroRgInterveniente() {
		return numeroRgInterveniente;
	}

	public void setNumeroRgInterveniente(String numeroRgInterveniente) {
		this.numeroRgInterveniente = numeroRgInterveniente;
	}

	public String getUfInterveniente() {
		return ufInterveniente;
	}

	public void setUfInterveniente(String ufInterveniente) {
		this.ufInterveniente = ufInterveniente;
	}

	public String getCpfInterveniente() {
		return cpfInterveniente;
	}

	public void setCpfInterveniente(String cpfInterveniente) {
		this.cpfInterveniente = cpfInterveniente;
	}

	public String getLogradouroInterveniente() {
		return logradouroInterveniente;
	}

	public void setLogradouroInterveniente(String logradouroInterveniente) {
		this.logradouroInterveniente = logradouroInterveniente;
	}

	public String getNumeroInterveniente() {
		return numeroInterveniente;
	}

	public void setNumeroInterveniente(String numeroInterveniente) {
		this.numeroInterveniente = numeroInterveniente;
	}

	public String getComplementoInterveniente() {
		return complementoInterveniente;
	}

	public void setComplementoInterveniente(String complementoInterveniente) {
		this.complementoInterveniente = complementoInterveniente;
	}

	public String getCidadeInterveniente() {
		return cidadeInterveniente;
	}

	public void setCidadeInterveniente(String cidadeInterveniente) {
		this.cidadeInterveniente = cidadeInterveniente;
	}

	public String getCepInterveniente() {
		return cepInterveniente;
	}

	public void setCepInterveniente(String cepInterveniente) {
		this.cepInterveniente = cepInterveniente;
	}

	public String getNomeConjugeInterveniente() {
		return nomeConjugeInterveniente;
	}

	public void setNomeConjugeInterveniente(String nomeConjugeInterveniente) {
		this.nomeConjugeInterveniente = nomeConjugeInterveniente;
	}

	public String getCpfConjugeInterveniente() {
		return cpfConjugeInterveniente;
	}

	public void setCpfConjugeInterveniente(String cpfConjugeInterveniente) {
		this.cpfConjugeInterveniente = cpfConjugeInterveniente;
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
	
	public boolean isAddAvalista() {
		return addAvalista;
	}

	public void setAddAvalista(boolean addAvalista) {
		this.addAvalista = addAvalista;
	}

	public boolean isAddInterveniente() {
		return addInterveniente;
	}

	public void setAddInterveniente(boolean addInterveniente) {
		this.addInterveniente = addInterveniente;
	}

	public boolean isAddTerceiro() {
		return addTerceiro;
	}

	public void setAddTerceiro(boolean addTerceiro) {
		this.addTerceiro = addTerceiro;
	}

	public String getNomeTerceiroG() {
		return nomeTerceiroG;
	}

	public void setNomeTerceiroG(String nomeTerceiroG) {
		this.nomeTerceiroG = nomeTerceiroG;
	}

	public String getNacionalidadeTerceiroG() {
		return nacionalidadeTerceiroG;
	}

	public void setNacionalidadeTerceiroG(String nacionalidadeTerceiroG) {
		this.nacionalidadeTerceiroG = nacionalidadeTerceiroG;
	}

	public String getProfissaoTerceiroG() {
		return profissaoTerceiroG;
	}

	public void setProfissaoTerceiroG(String profissaoTerceiroG) {
		this.profissaoTerceiroG = profissaoTerceiroG;
	}

	public String getEstadoCivilTerceiroG() {
		return estadoCivilTerceiroG;
	}

	public void setEstadoCivilTerceiroG(String estadoCivilTerceiroG) {
		this.estadoCivilTerceiroG = estadoCivilTerceiroG;
	}

	public String getNumeroRgTerceiroG() {
		return numeroRgTerceiroG;
	}

	public void setNumeroRgTerceiroG(String numeroRgTerceiroG) {
		this.numeroRgTerceiroG = numeroRgTerceiroG;
	}

	public String getUfTerceiroG() {
		return ufTerceiroG;
	}

	public void setUfTerceiroG(String ufTerceiroG) {
		this.ufTerceiroG = ufTerceiroG;
	}

	public String getCpfTerceiroG() {
		return cpfTerceiroG;
	}

	public void setCpfTerceiroG(String cpfTerceiroG) {
		this.cpfTerceiroG = cpfTerceiroG;
	}

	public String getLogradouroTerceiroG() {
		return logradouroTerceiroG;
	}

	public void setLogradouroTerceiroG(String logradouroTerceiroG) {
		this.logradouroTerceiroG = logradouroTerceiroG;
	}

	public String getNumeroTerceiroG() {
		return numeroTerceiroG;
	}

	public void setNumeroTerceiroG(String numeroTerceiroG) {
		this.numeroTerceiroG = numeroTerceiroG;
	}

	public String getComplementoTerceiroG() {
		return complementoTerceiroG;
	}

	public void setComplementoTerceiroG(String complementoTerceiroG) {
		this.complementoTerceiroG = complementoTerceiroG;
	}

	public String getCidadeTerceiroG() {
		return cidadeTerceiroG;
	}

	public void setCidadeTerceiroG(String cidadeTerceiroG) {
		this.cidadeTerceiroG = cidadeTerceiroG;
	}

	public String getCepTerceiroG() {
		return cepTerceiroG;
	}

	public void setCepTerceiroG(String cepTerceiroG) {
		this.cepTerceiroG = cepTerceiroG;
	}

	public String getNomeConjugeTerceiroG() {
		return nomeConjugeTerceiroG;
	}

	public void setNomeConjugeTerceiroG(String nomeConjugeTerceiroG) {
		this.nomeConjugeTerceiroG = nomeConjugeTerceiroG;
	}

	public String getCpfConjugeTerceiroG() {
		return cpfConjugeTerceiroG;
	}

	public void setCpfConjugeTerceiroG(String cpfConjugeTerceiroG) {
		this.cpfConjugeTerceiroG = cpfConjugeTerceiroG;
	}

	public String getNomeAvalista() {
		return nomeAvalista;
	}

	public void setNomeAvalista(String nomeAvalista) {
		this.nomeAvalista = nomeAvalista;
	}

	public String getNacionalidadeAvalista() {
		return nacionalidadeAvalista;
	}

	public void setNacionalidadeAvalista(String nacionalidadeAvalista) {
		this.nacionalidadeAvalista = nacionalidadeAvalista;
	}

	public String getProfissaoAvalista() {
		return profissaoAvalista;
	}

	public void setProfissaoAvalista(String profissaoAvalista) {
		this.profissaoAvalista = profissaoAvalista;
	}

	public String getEstadoCivilAvalista() {
		return estadoCivilAvalista;
	}

	public void setEstadoCivilAvalista(String estadoCivilAvalista) {
		this.estadoCivilAvalista = estadoCivilAvalista;
	}

	public String getNumeroRgAvalista() {
		return numeroRgAvalista;
	}

	public void setNumeroRgAvalista(String numeroRgAvalista) {
		this.numeroRgAvalista = numeroRgAvalista;
	}

	public String getUfAvalista() {
		return ufAvalista;
	}

	public void setUfAvalista(String ufAvalista) {
		this.ufAvalista = ufAvalista;
	}

	public String getCpfAvalista() {
		return cpfAvalista;
	}

	public void setCpfAvalista(String cpfAvalista) {
		this.cpfAvalista = cpfAvalista;
	}

	public String getLogradouroAvalista() {
		return logradouroAvalista;
	}

	public void setLogradouroAvalista(String logradouroAvalista) {
		this.logradouroAvalista = logradouroAvalista;
	}

	public String getNumeroAvalista() {
		return numeroAvalista;
	}

	public void setNumeroAvalista(String numeroAvalista) {
		this.numeroAvalista = numeroAvalista;
	}

	public String getComplementoAvalista() {
		return complementoAvalista;
	}

	public void setComplementoAvalista(String complementoAvalista) {
		this.complementoAvalista = complementoAvalista;
	}

	public String getCidadeAvalista() {
		return cidadeAvalista;
	}

	public void setCidadeAvalista(String cidadeAvalista) {
		this.cidadeAvalista = cidadeAvalista;
	}

	public String getCepAvalista() {
		return cepAvalista;
	}

	public void setCepAvalista(String cepAvalista) {
		this.cepAvalista = cepAvalista;
	}

	public String getNomeConjugeAvalista() {
		return nomeConjugeAvalista;
	}

	public void setNomeConjugeAvalista(String nomeConjugeAvalista) {
		this.nomeConjugeAvalista = nomeConjugeAvalista;
	}

	public String getCpfConjugeAvalista() {
		return cpfConjugeAvalista;
	}

	public void setCpfConjugeAvalista(String cpfConjugeAvalista) {
		this.cpfConjugeAvalista = cpfConjugeAvalista;
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

	public String getEmailInterveniente() {
		return emailInterveniente;
	}

	public void setEmailInterveniente(String emailInterveniente) {
		this.emailInterveniente = emailInterveniente;
	}

	public String getRegimeCasamentoInterveniente() {
		return regimeCasamentoInterveniente;
	}

	public void setRegimeCasamentoInterveniente(String regimeCasamentoInterveniente) {
		this.regimeCasamentoInterveniente = regimeCasamentoInterveniente;
	}

	public boolean isFemininoInterveniente() {
		return femininoInterveniente;
	}

	public void setFemininoInterveniente(boolean femininoInterveniente) {
		this.femininoInterveniente = femininoInterveniente;
	}

	public String getEmailTerceiroG() {
		return emailTerceiroG;
	}

	public void setEmailTerceiroG(String emailTerceiroG) {
		this.emailTerceiroG = emailTerceiroG;
	}

	public String getRegimeCasamentoTerceiroG() {
		return regimeCasamentoTerceiroG;
	}

	public void setRegimeCasamentoTerceiroG(String regimeCasamentoTerceiroG) {
		this.regimeCasamentoTerceiroG = regimeCasamentoTerceiroG;
	}

	public boolean isFemininoTerceiroG() {
		return femininoTerceiroG;
	}

	public void setFemininoTerceiroG(boolean femininoTerceiroG) {
		this.femininoTerceiroG = femininoTerceiroG;
	}

	public String getEmailAvalista() {
		return emailAvalista;
	}

	public void setEmailAvalista(String emailAvalista) {
		this.emailAvalista = emailAvalista;
	}

	public String getRegimeCasamentoAvalista() {
		return regimeCasamentoAvalista;
	}

	public void setRegimeCasamentoAvalista(String regimeCasamentoAvalista) {
		this.regimeCasamentoAvalista = regimeCasamentoAvalista;
	}

	public boolean isFemininoAvalista() {
		return femininoAvalista;
	}

	public void setFemininoAvalista(boolean femininoAvalista) {

		this.femininoAvalista = femininoAvalista;
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

	public boolean isEmpresaInterveniente() {
		return empresaInterveniente;
	}

	public void setEmpresaInterveniente(boolean empresaInterveniente) {
		this.empresaInterveniente = empresaInterveniente;
	}

	public String getRazaoSocialInterveniente() {
		return razaoSocialInterveniente;
	}

	public void setRazaoSocialInterveniente(String razaoSocialInterveniente) {
		this.razaoSocialInterveniente = razaoSocialInterveniente;
	}

	public String getTipoEmpresaInterveniente() {
		return tipoEmpresaInterveniente;
	}

	public void setTipoEmpresaInterveniente(String tipoEmpresaInterveniente) {
		this.tipoEmpresaInterveniente = tipoEmpresaInterveniente;
	}

	public String getCnpjInterveniente() {
		return cnpjInterveniente;
	}

	public void setCnpjInterveniente(String cnpjInterveniente) {
		this.cnpjInterveniente = cnpjInterveniente;
	}

	public String getMunicipioEmpresaInterveniente() {
		return municipioEmpresaInterveniente;
	}

	public void setMunicipioEmpresaInterveniente(String municipioEmpresaInterveniente) {
		this.municipioEmpresaInterveniente = municipioEmpresaInterveniente;
	}

	public String getEstadoEmpresaInterveniente() {
		return estadoEmpresaInterveniente;
	}

	public void setEstadoEmpresaInterveniente(String estadoEmpresaInterveniente) {
		this.estadoEmpresaInterveniente = estadoEmpresaInterveniente;
	}

	public String getRuaEmpresaInterveniente() {
		return ruaEmpresaInterveniente;
	}

	public void setRuaEmpresaInterveniente(String ruaEmpresaInterveniente) {
		this.ruaEmpresaInterveniente = ruaEmpresaInterveniente;
	}

	public String getNumeroEmpresaInterveniente() {
		return numeroEmpresaInterveniente;
	}

	public void setNumeroEmpresaInterveniente(String numeroEmpresaInterveniente) {
		this.numeroEmpresaInterveniente = numeroEmpresaInterveniente;
	}

	public String getSalaEmpresaInterveniente() {
		return salaEmpresaInterveniente;
	}

	public void setSalaEmpresaInterveniente(String salaEmpresaInterveniente) {
		this.salaEmpresaInterveniente = salaEmpresaInterveniente;
	}

	public String getBairroEmpresaInterveniente() {
		return bairroEmpresaInterveniente;
	}

	public void setBairroEmpresaInterveniente(String bairroEmpresaInterveniente) {
		this.bairroEmpresaInterveniente = bairroEmpresaInterveniente;
	}

	public String getCepEmpresaInterveniente() {
		return cepEmpresaInterveniente;
	}

	public void setCepEmpresaInterveniente(String cepEmpresaInterveniente) {
		this.cepEmpresaInterveniente = cepEmpresaInterveniente;
	}

	public boolean isEmpresaTerceiroG() {
		return empresaTerceiroG;
	}

	public void setEmpresaTerceiroG(boolean empresaTerceiroG) {
		this.empresaTerceiroG = empresaTerceiroG;
	}

	public String getRazaoSocialTerceiroG() {
		return razaoSocialTerceiroG;
	}

	public void setRazaoSocialTerceiroG(String razaoSocialTerceiroG) {
		this.razaoSocialTerceiroG = razaoSocialTerceiroG;
	}

	public String getTipoEmpresaTerceiroG() {
		return tipoEmpresaTerceiroG;
	}

	public void setTipoEmpresaTerceiroG(String tipoEmpresaTerceiroG) {
		this.tipoEmpresaTerceiroG = tipoEmpresaTerceiroG;
	}

	public String getCnpjTerceiroG() {
		return cnpjTerceiroG;
	}

	public void setCnpjTerceiroG(String cnpjTerceiroG) {
		this.cnpjTerceiroG = cnpjTerceiroG;
	}

	public String getMunicipioEmpresaTerceiroG() {
		return municipioEmpresaTerceiroG;
	}

	public void setMunicipioEmpresaTerceiroG(String municipioEmpresaTerceiroG) {
		this.municipioEmpresaTerceiroG = municipioEmpresaTerceiroG;
	}

	public String getEstadoEmpresaTerceiroG() {
		return estadoEmpresaTerceiroG;
	}

	public void setEstadoEmpresaTerceiroG(String estadoEmpresaTerceiroG) {
		this.estadoEmpresaTerceiroG = estadoEmpresaTerceiroG;
	}

	public String getRuaEmpresaTerceiroG() {
		return ruaEmpresaTerceiroG;
	}

	public void setRuaEmpresaTerceiroG(String ruaEmpresaTerceiroG) {
		this.ruaEmpresaTerceiroG = ruaEmpresaTerceiroG;
	}

	public String getNumeroEmpresaTerceiroG() {
		return numeroEmpresaTerceiroG;
	}

	public void setNumeroEmpresaTerceiroG(String numeroEmpresaTerceiroG) {
		this.numeroEmpresaTerceiroG = numeroEmpresaTerceiroG;
	}

	public String getSalaEmpresaTerceiroG() {
		return salaEmpresaTerceiroG;
	}

	public void setSalaEmpresaTerceiroG(String salaEmpresaTerceiroG) {
		this.salaEmpresaTerceiroG = salaEmpresaTerceiroG;
	}

	public String getBairroEmpresaTerceiroG() {
		return bairroEmpresaTerceiroG;
	}

	public void setBairroEmpresaTerceiroG(String bairroEmpresaTerceiroG) {
		this.bairroEmpresaTerceiroG = bairroEmpresaTerceiroG;
	}

	public String getCepEmpresaTerceiroG() {
		return cepEmpresaTerceiroG;
	}

	public void setCepEmpresaTerceiroG(String cepEmpresaTerceiroG) {
		this.cepEmpresaTerceiroG = cepEmpresaTerceiroG;
	}

	public boolean isEmpresaAvalista() {
		return empresaAvalista;
	}

	public void setEmpresaAvalista(boolean empresaAvalista) {
		this.empresaAvalista = empresaAvalista;
	}

	public String getRazaoSocialAvalista() {
		return razaoSocialAvalista;
	}

	public void setRazaoSocialAvalista(String razaoSocialAvalista) {
		this.razaoSocialAvalista = razaoSocialAvalista;
	}

	public String getTipoEmpresaAvalista() {
		return tipoEmpresaAvalista;
	}

	public void setTipoEmpresaAvalista(String tipoEmpresaAvalista) {
		this.tipoEmpresaAvalista = tipoEmpresaAvalista;
	}

	public String getCnpjAvalista() {
		return cnpjAvalista;
	}

	public void setCnpjAvalista(String cnpjAvalista) {
		this.cnpjAvalista = cnpjAvalista;
	}

	public String getMunicipioEmpresaAvalista() {
		return municipioEmpresaAvalista;
	}

	public void setMunicipioEmpresaAvalista(String municipioEmpresaAvalista) {
		this.municipioEmpresaAvalista = municipioEmpresaAvalista;
	}

	public String getEstadoEmpresaAvalista() {
		return estadoEmpresaAvalista;
	}

	public void setEstadoEmpresaAvalista(String estadoEmpresaAvalista) {
		this.estadoEmpresaAvalista = estadoEmpresaAvalista;
	}

	public String getRuaEmpresaAvalista() {
		return ruaEmpresaAvalista;
	}

	public void setRuaEmpresaAvalista(String ruaEmpresaAvalista) {
		this.ruaEmpresaAvalista = ruaEmpresaAvalista;
	}

	public String getNumeroEmpresaAvalista() {
		return numeroEmpresaAvalista;
	}

	public void setNumeroEmpresaAvalista(String numeroEmpresaAvalista) {
		this.numeroEmpresaAvalista = numeroEmpresaAvalista;
	}

	public String getSalaEmpresaAvalista() {
		return salaEmpresaAvalista;
	}

	public void setSalaEmpresaAvalista(String salaEmpresaAvalista) {
		this.salaEmpresaAvalista = salaEmpresaAvalista;
	}

	public String getBairroEmpresaAvalista() {
		return bairroEmpresaAvalista;
	}

	public void setBairroEmpresaAvalista(String bairroEmpresaAvalista) {
		this.bairroEmpresaAvalista = bairroEmpresaAvalista;
	}

	public String getCepEmpresaAvalista() {
		return cepEmpresaAvalista;
	}

	public void setCepEmpresaAvalista(String cepEmpresaAvalista) {
		this.cepEmpresaAvalista = cepEmpresaAvalista;
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

	public String getPaiInterveniente() {
		return paiInterveniente;
	}

	public void setPaiInterveniente(String paiInterveniente) {
		this.paiInterveniente = paiInterveniente;
	}

	public String getMaeInterveniente() {
		return maeInterveniente;
	}

	public void setMaeInterveniente(String maeInterveniente) {
		this.maeInterveniente = maeInterveniente;
	}

	public String getPaiTerceiroG() {
		return paiTerceiroG;
	}

	public void setPaiTerceiroG(String paiTerceiroG) {
		this.paiTerceiroG = paiTerceiroG;
	}

	public String getMaeTerceiroG() {
		return maeTerceiroG;
	}

	public void setMaeTerceiroG(String maeTerceiroG) {
		this.maeTerceiroG = maeTerceiroG;
	}

	public String getPaiAvalista() {
		return paiAvalista;
	}

	public void setPaiAvalista(String paiAvalista) {
		this.paiAvalista = paiAvalista;
	}

	public String getMaeAvalista() {
		return maeAvalista;
	}

	public void setMaeAvalista(String maeAvalista) {
		this.maeAvalista = maeAvalista;
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

	public boolean isModeloAntigo() {
		return modeloAntigo;
	}

	public void setModeloAntigo(boolean modeloAntigo) {
		this.modeloAntigo = modeloAntigo;
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
	
	
}
