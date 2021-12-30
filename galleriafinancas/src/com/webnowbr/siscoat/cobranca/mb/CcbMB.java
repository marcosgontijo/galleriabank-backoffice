package com.webnowbr.siscoat.cobranca.mb;

import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;

import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.webnowbr.siscoat.cobranca.auxiliar.NumeroPorExtenso;
import com.webnowbr.siscoat.cobranca.auxiliar.PorcentagemPorExtenso;
import com.webnowbr.siscoat.cobranca.auxiliar.ValorPorExtenso;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.vo.CcbVO;
import com.webnowbr.siscoat.common.BancosEnum;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.common.ValidaCNPJ;
import com.webnowbr.siscoat.common.ValidaCPF;

/** ManagedBean. */
@ManagedBean(name = "ccbMB")
@SessionScoped
public class CcbMB {
	private String numeroContrato;
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

	private BigDecimal valorLiquidoCredito;
	private BigDecimal valorCredito;
	private BigDecimal custoEmissao;
	private BigDecimal valorIOF;
	private BigDecimal valorDespesas;
	
	private BigDecimal taxaDeJurosMes;
	private BigDecimal taxaDeJurosAno;
	private BigDecimal cetMes;
	private BigDecimal cetAno;
	
	private String contaCorrente;
	private String agencia;
	private String numeroBanco;
	private String nomeBanco;
	private String numeroParcelasPagamento;
	private Date vencimentoPrimeiraParcelaPagamento;
	private Date vencimentoUltimaParcelaPagamento;
	private BigDecimal montantePagamento;

	private String numeroParcelasDFI;
	private Date vencimentoPrimeiraParcelaDFI;
	private Date vencimentoUltimaParcelaDFI;
	private BigDecimal montanteDFI;

	private String numeroParcelasMIP;
	private Date vencimentoPrimeiraParcelaMIP;
	private Date vencimentoUltimaParcelaMIP;
	private BigDecimal montanteMIP;

	private BigDecimal tarifaAntecipada;
	private Date dataDeEmissao;

	private String numeroImovel;
	private String cartorioImovel;
	private String cidadeImovel;
	private String ufImovel;
	
	private BigDecimal vendaLeilao;
	private String elaboradorNome;
	private String elaboradorCrea;
	private String responsavelNome;
	private String responsavelCrea;
	private BigDecimal porcentagemImovel;
	
	private String nomeTestemunha1;
	private String cpfTestemunha1;
	private String rgTestemunha1;
	
	private String nomeTestemunha2;
	private String cpfTestemunha2;
	private String rgTestemunha2;
	
	private String tipoPesquisa;
	private String tipoDownload;
	
	public UploadedFile uploadedFile;
    public String fileName;
    public String fileType;
    public int fileTypeInt;
    ByteArrayInputStream bis = null;
    
    private CcbVO participanteSelecionado = new CcbVO();
    private Set<CcbVO> listaParticipantes;
    private boolean addParticipante;
    
    private ArrayList<UploadedFile> filesList = new ArrayList<UploadedFile>();
	
    
    String tituloPagadorRecebedorDialog = "";
    
    private ContratoCobranca objetoContratoCobranca;
	ValorPorExtenso valorPorExtenso = new ValorPorExtenso();
	NumeroPorExtenso numeroPorExtenso = new NumeroPorExtenso();
	PorcentagemPorExtenso porcentagemPorExtenso = new PorcentagemPorExtenso();
	
	private List<SelectItem> listaBancosNome;
	private List<SelectItem> listaBancosCodigo;
	

	public void pesquisaParticipante() {
		this.tipoPesquisa = "Participante";
		this.updatePagadorRecebedor = ":form:ParticipantesPanel :form:Dados";
		this.participanteSelecionado = new CcbVO();
		this.participanteSelecionado.setPessoa(new PagadorRecebedor());
	}
	

	public void concluirParticipante() {
		this.getListaParticipantes().add(this.participanteSelecionado);
		this.participanteSelecionado = new CcbVO();
		this.participanteSelecionado.setPessoa(new PagadorRecebedor());
		this.addParticipante = false;
	}
	
	public void editarParticipante(CcbVO participante) {
		this.addParticipante = true;
		this.participanteSelecionado = new CcbVO();
		this.setParticipanteSelecionado(participante);
		this.removerParticipante(participante);
	}
	
	public void removerParticipante(CcbVO participante) {
		this.getListaParticipantes().remove(participante);
	}
	
	public void pesquisaBancosListaNome() {
		this.listaBancosNome = new ArrayList<>();
		for(BancosEnum banco : BancosEnum.values()) {
			SelectItem item = new SelectItem(banco.getNome());
			this.listaBancosNome.add(item);
		}
	}
	
	public void pesquisaBancosListaCodigo() {
		this.listaBancosCodigo = new ArrayList<>();
		for(BancosEnum banco : BancosEnum.values()) {
			SelectItem item = new SelectItem(banco.getCodigo());
			this.listaBancosCodigo.add(item);
		}
	}
	
	public void populateCodigosBanco() {
		for(BancosEnum banco : BancosEnum.values()) {
			if(CommonsUtil.mesmoValor(this.nomeBanco,banco.getNome())) {
				this.setNumeroBanco(banco.getCodigo());
				//PrimeFaces.current().ajax().update(":numeroBanco");
				break;
			}
		}
	}
	
	public void populateNomesBanco() {
		for(BancosEnum banco : BancosEnum.values()) {
			if(CommonsUtil.mesmoValor(this.numeroBanco,banco.getCodigo())) {
				this.setNomeBanco(banco.getNome());
				//PrimeFaces.current().ajax().update(":nomeBanco");
				break;
			}
		}
	}
	
	
	public void complementaBancoDados(){
		if (this.numeroBanco == null) {
			
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
		if(this.numeroParcelasPagamento != null) {
			this.setNumeroParcelasDFI(this.getNumeroParcelasPagamento());
			this.setNumeroParcelasMIP(this.getNumeroParcelasPagamento());
		} if(this.vencimentoPrimeiraParcelaPagamento != null) {
			this.setVencimentoPrimeiraParcelaDFI(this.getVencimentoPrimeiraParcelaPagamento());
			this.setVencimentoPrimeiraParcelaMIP(this.getVencimentoPrimeiraParcelaPagamento());
		} if(this.vencimentoUltimaParcelaPagamento != null) {
			this.setVencimentoUltimaParcelaDFI(this.getVencimentoUltimaParcelaPagamento());
			this.setVencimentoUltimaParcelaMIP(this.getVencimentoUltimaParcelaPagamento());
		}
	}
	
	public void calculaDatavencimentoFinal() {
		Integer parcelas = CommonsUtil.integerValue(getNumeroParcelasPagamento());	
		parcelas -= 1;
		Calendar c = Calendar.getInstance();
		c.setTime(this.vencimentoPrimeiraParcelaPagamento);
		c.add(Calendar.MONTH, parcelas);
		this.setVencimentoUltimaParcelaPagamento(c.getTime());
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
		this.objetoContratoCobranca = getContratoById(this.objetoContratoCobranca.getId());
		this.selectedPagadorGenerico = this.objetoContratoCobranca.getPagador();
		this.setTipoPesquisa("Emitente"); 
		populateSelectedPagadorRecebedor();
		
		
		return "/Atendimento/Cobranca/Ccb.xhtml";
	}
	
	public ContratoCobranca getContratoById(long idContrato) {
		ContratoCobranca contrato = new ContratoCobranca();
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
				
		contrato = cDao.findById(idContrato);
		
		return contrato;
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
				this.setNomeConjugeEmitente(this.emitenteSelecionado.getNomeConjuge());
				this.setCpfConjugeEmitente(this.emitenteSelecionado.getCpfConjuge());
			} else {
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
					this.setNomeConjugeInterveniente(this.intervenienteSelecionado.getNomeConjuge());
					this.setCpfConjugeInterveniente(this.intervenienteSelecionado.getCpfConjuge());
				} else {
					this.setNomeConjugeInterveniente(null);
					this.setCpfConjugeInterveniente(null);
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
					this.setNomeConjugeTerceiroG(this.terceiroGSelecionado.getNomeConjuge());
					this.setCpfConjugeTerceiroG(this.terceiroGSelecionado.getCpfConjuge());
				} else {
					this.setNomeConjugeTerceiroG(null);
					this.setCpfConjugeTerceiroG(null);
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
					this.setNomeConjugeAvalista(this.avalistaSelecionado.getNomeConjuge());
					this.setCpfConjugeAvalista(this.avalistaSelecionado.getCpfConjuge());
				} else {
					this.setNomeConjugeAvalista(null);
					this.setCpfConjugeAvalista(null);
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
			this.setNomeTestemunha1(this.testemunha1Selecionado.getNome());
			this.setCpfTestemunha1(this.testemunha1Selecionado.getCpf());
			this.setRgTestemunha1(this.testemunha1Selecionado.getRg());
		}
		
		else if (CommonsUtil.mesmoValor(this.tipoPesquisa ,"Testemunha2")) {
			this.testemunha2Selecionado = (this.selectedPagadorGenerico);
			this.setNomeTestemunha2(this.testemunha2Selecionado.getNome());
			this.setCpfTestemunha2(this.testemunha2Selecionado.getCpf());
			this.setRgTestemunha2(this.testemunha2Selecionado.getRg());
		}
		else if (CommonsUtil.mesmoValor(this.tipoPesquisa , "Participante")) {
			this.participanteSelecionado.setPessoa(this.selectedPagadorGenerico);
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
			valorPorExtenso.setNumber(valorSobrescrever);
			text = text.replace("Extenso" + valorEscrito , valorPorExtenso.toString());
			r.setText(text, 0);
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
	

	public StreamedContent geraCcbDinamica() throws IOException{
		try {
			XWPFDocument document = new XWPFDocument();
			
			
			XWPFHeaderFooterPolicy headerFooterPolicy = document.getHeaderFooterPolicy();
			  if (headerFooterPolicy == null) headerFooterPolicy = document.createHeaderFooterPolicy();

			XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			XWPFParagraph paragraphHeader = header.createParagraph();
			paragraphHeader.setAlignment(ParagraphAlignment.RIGHT);
			XWPFRun runHeader = paragraphHeader.createRun();  
			runHeader.setText("VIA NEGOCIÁVEL");
			runHeader.setFontSize(12);
			runHeader.setColor("0000ff");
			runHeader.setBold(true);
			
			XWPFParagraph paragraph = document.createParagraph();
			XWPFRun run = paragraph.createRun();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			run.setText("CÉDULA DE CRÉDITO BANCÁRIO");	
			run.addBreak();
			
			run.setText("Nº XXXXXX");	
			run.setFontSize(14);
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			run.addBreak();
			
			paragraph = document.createParagraph();		
			run = paragraph.createRun();
			run.setText("1.	 Partes:");
			run.setFontSize(11);
			run.setBold(true);
			run.addBreak();
			
			paragraph = document.createParagraph();
			run = paragraph.createRun();
			run.setText("I – CREDOR: BMP MONEY PLUS SOCIEDADE DE CRÉDITO DIRETO S.A.");
			run.setBold(true);
			run = paragraph.createRun();
			run.setText(", instituição financeira, inscrita no CNPJ/MF sob nº 34.337.707/0001-00, com sede na Av. Paulista, 1765, 1º Andar, CEP 01311-200, São Paulo, SP, neste ato, representada na forma do seu Estatuto Social; ");
			run.addBreak();
			
			for(CcbVO participante : this.listaParticipantes) {
				paragraph = document.createParagraph();
				run = paragraph.createRun();
				run.setText("IX – " + participante.getTipoParticipante() + ":");
				if(!participante.isEmpresa()) {
					run.setText(run.getText(0) + participante.getPessoa().getNome() + ",");
				}
				run.setBold(true);
			}
			
			
			for (XWPFParagraph p : document.getParagraphs()) {
			    List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);
			            adicionarEnter(text, r);
			    	}
			    }
			}
			
			
			ByteArrayOutputStream  out = new ByteArrayOutputStream ();

			document.write(out); 
			document.close();
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
					FacesContext.getCurrentInstance());

	    	gerador.open(String.format("testeAaaaaaa %s.docx", ""));	    	
			gerador.feed(new ByteArrayInputStream(out.toByteArray()));
			gerador.close();
		} catch ( Throwable e ) {
	        e.printStackTrace();
	    }
	    
	    return null;
	}
	
	@SuppressWarnings("resource")
	public StreamedContent readXWPFile() throws IOException {

	    try
	    {
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
			
			
			/*text = trocaValoresXWPF(text, r, "FiducianteConjugue","Rua logradouroConjugeEmitente, nº numeroConjugeEmitente, Qd. XX - Lote XX, Cond. Residencial XXXXXX, Bairro - cidadeConjugeEmitente - ufConjugeEmitente -  \n"
									+ "CEP cepConjugeEmitente \n"
									+ "Email: emailConjugeEmitente");
							text = trocaValoresXWPF(text, r, "ConjugeDados","nacionalidadeConjugeEmitente, profissaoConjugeEmitente, estadoCivilEmitente (nomeEmitente , cpfEmitente), portador(a) da Cédula de Identidade RG nº numeroRgConjugeEmitente SSP/ufConjugeEmitente, inscrito(a) no CPF/MF sob o nº cpfConjugeEmitente, residente e domiciliado à logradouroConjugeEmitente, nº numeroConjugeEmitente, complementoConjugeEmitente, cidadeConjugeEmitente/ufConjugeEmitente, CEP cepConjugeEmitente;");*/
			/*
			 * text = trocaValoresXWPF(text, r,
			 * "ConjugeIntervenienteDados","nacionalidadeConjugeInterveniente, profissaoConjugeInterveniente, estadoCivilInterveniente (nomeInterveniente , cpfInterveniente), portador(a) da Cédula de Identidade RG nº numeroRgConjugeInterveniente SSP/ufConjugeInterveniente, inscrito(a) no CPF/MF sob o nº cpfConjugeInterveniente, residente e domiciliado à logradouroConjugeInterveniente, nº numeroConjugeInterveniente, complementoConjugeInterveniente, cidadeConjugeInterveniente/ufConjugeInterveniente, CEP cepConjugeInterveniente;"
			 * );
			 */ 
			
			for (XWPFParagraph p : document.getParagraphs()) {
			    List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);


						if (CommonsUtil.mesmoValor(tipoDownload, "CCB")) {
							if (this.addTerceiro == true) {
								text = trocaValoresXWPF(text, r, "criaTerceiroG",
										"\nIX – TERCEIRO GARANTIDOR: nomeEmpresaTerceiroG dadosEmpresaTerceiroG nomeTerceiroG, filhoTerceiroG de maeTerceiroG e paiTerceiroG, nacionalidadeTerceiroG, profissaoTerceiroG, estadoCivilTerceiroG regimeCasamentoTerceiroG nomeConjugeTerceiroG cpfConjugeTerceiroG, portador(a) da Cédula de Identidade RG nº numeroRgTerceiroG SSP/ufTerceiroG, inscrito(a) no CPF/MF sob o nº cpfTerceiroG, endereço eletônico: emailTerceiroG, residente e domiciliado à logradouroTerceiroG, nº numeroTerceiroG,  complementoTerceiroG, cidadeTerceiroG/ufTerceiroG, CEP cepTerceiroG; \n");
							} else {
								text = trocaValoresXWPF(text, r, "criaTerceiroG", "");
							}
							if (this.addInterveniente == true) {
								text = trocaValoresXWPF(text, r, "criaInterveniente",
										"\nIX – INTERVENIENTE ANUENTE: nomeEmpresaInterveniente dadosEmpresaInterveniente nomeInterveniente, nacionalidadeInterveniente, filhoInterveniente de maeInterveniente e paiInterveniente, profissaoInterveniente, estadoCivilInterveniente regimeCasamentoInterveniente nomeConjugeInterveniente cpfConjugeInterveniente, portador(a) da Cédula de Identidade RG nº numeroRgInterveniente SSP/ufInterveniente, inscrito(a) no CPF/MF sob o nº cpfInterveniente, endereço eletônico: emailInterveniente, residente e domiciliado à logradouroInterveniente, nº numeroInterveniente,  complementoInterveniente, cidadeInterveniente/ufInterveniente, CEP cepInterveniente; \n");
							} else {
								text = trocaValoresXWPF(text, r, "criaInterveniente", "");
							}
							if (this.addAvalista == true) {
								text = trocaValoresXWPF(text, r, "criaAvalista",
										"\nIX – AVALISTA: nomeEmpresaAvalista dadosEmpresaAvalista nomeAvalista, filhoAvalista de maeAvalista e paiAvalista, nacionalidadeAvalista, profissaoAvalista, estadoCivilAvalista regimeCasamentoAvalista nomeConjugeAvalista cpfConjugeAvalista,portador(a) da Cédula de Identidade RG nº numeroRgAvalista SSP/ufAvalista, inscrito(a) no CPF/MF sob o nº cpfAvalista, endereço eletônico: emailAvalista, residente e domiciliado à logradouroAvalista, nº numeroAvalista,  complementoAvalista, cidadeAvalista/ufAvalista, CEP cepAvalista; \n");
							} else {
								text = trocaValoresXWPF(text, r, "criaAvalista", "");
							}		
						} else if(CommonsUtil.mesmoValor(tipoDownload,"AF")) {
							if (this.addTerceiro == true) {
								text = trocaValoresXWPF(text, r, "criaTerceiroG",
										"nomeEmpresaTerceiroG dadosEmpresaTerceiroG nomeTerceiroG, filhoTerceiroG de maeTerceiroG e paiTerceiroG, nacionalidadeTerceiroG, profissaoTerceiroG, estadoCivilTerceiroG regimeCasamentoTerceiroG nomeConjugeTerceiroG cpfConjugeTerceiroG, portador(a) da Cédula de Identidade RG nº numeroRgTerceiroG SSP/ufTerceiroG, inscrito(a) no CPF/MF sob o nº cpfTerceiroG, endereço eletônico: emailTerceiroG, residente e domiciliado à logradouroTerceiroG, nº numeroTerceiroG,  complementoTerceiroG, cidadeTerceiroG/ufTerceiroG, CEP cepTerceiroG;");
							} else {
								text = trocaValoresXWPF(text, r, "criaTerceiroG", "");
							}
							if(CommonsUtil.mesmoValor(fiduciante, true)) {
								text = trocaValoresXWPF(text, r, "criaFiduciante", "nomeEmpresaEmitente dadosEmpresaEmitente nomeEmitente, filhoEmitente de maeEmitente e paiEmitente, nacionalidadeEmitente, profissaoEmitente, estadoCivilEmitente regimeCasamentoEmitente nomeConjugeEmitente cpfConjugeEmitente, portador(a) da Cédula de Identidade RG nº numeroRgEmitente SSP/ufEmitente, inscrito(a) no CPF/MF sob o nº cpfEmitente, endereço eletônico: emailEmitente, residente e domiciliado à logradouroEmitente, nº numeroEmitente, complementoEmitente, cidadeEmitente/ufEmitente, CEP cepEmitente;");
								text = trocaValoresXWPF(text, r, "criaDevedor", "");
								
								text = trocaValoresXWPF(text, r, "fiducianteEmitente", "\n"
										+ "nomeEmitente \n"
										+ "logradouroEmitente, nº numeroEmitente,  cidadeEmitente - ufEmitente \n"
										+ "CEP cepEmitente \n"
										+ "Email: emailEmitente \n");
								
								text = trocaValoresXWPF(text, r, "devedorEmitente","");
								
							} else {
								text = trocaValoresXWPF(text, r, "criaDevedor", "\n X) DEVEDOR: nomeEmpresaEmitente dadosEmpresaEmitente nomeEmitente, filhoEmitente de maeEmitente e paiEmitente, nacionalidadeEmitente, profissaoEmitente, estadoCivilEmitente regimeCasamentoEmitente nomeConjugeEmitente cpfConjugeEmitente, portador(a) da Cédula de Identidade RG nº numeroRgEmitente SSP/ufEmitente, inscrito(a) no CPF/MF sob o nº cpfEmitente, endereço eletônico: emailEmitente, residente e domiciliado à logradouroEmitente, nº numeroEmitente, complementoEmitente, cidadeEmitente/ufEmitente, CEP cepEmitente;");
								text = trocaValoresXWPF(text, r, "criaFiduciante", "");
								text = trocaValoresXWPF(text, r, "fiducianteEmitente", "");
								text = trocaValoresXWPF(text, r, "devedorEmitente", "\n \n"
										+ "Pelo DEVEDOR: \n" 
										+ "nomeEmitente \n"
										+ "logradouroEmitente, nº numeroEmitente,  cidadeEmitente - ufEmitente - \n"
										+ "CEP cepEmitente \n"
										+ "Email: emailEmitente \n");
							}
							
							
							if (this.addInterveniente == true) {
								text = trocaValoresXWPF(text, r, "criaInterveniente",
										"\nX) – INTERVENIENTE ANUENTE: nomeEmpresaInterveniente dadosEmpresaInterveniente nomeInterveniente, nacionalidadeInterveniente, filhoInterveniente de maeInterveniente e paiInterveniente, profissaoInterveniente, estadoCivilInterveniente regimeCasamentoInterveniente nomeConjugeInterveniente cpfConjugeInterveniente, portador(a) da Cédula de Identidade RG nº numeroRgInterveniente SSP/ufInterveniente, inscrito(a) no CPF/MF sob o nº cpfInterveniente, endereço eletônico: emailInterveniente, residente e domiciliado à logradouroInterveniente, nº numeroInterveniente,  complementoInterveniente, cidadeInterveniente/ufInterveniente, CEP cepInterveniente; \n");
							} else {
								text = trocaValoresXWPF(text, r, "criaInterveniente", "");
							}
							if (this.addAvalista == true) {
								text = trocaValoresXWPF(text, r, "criaAvalista",
										" nomeEmpresaAvalista dadosEmpresaAvalista nomeAvalista, filhoAvalista de maeAvalista e paiAvalista, nacionalidadeAvalista, profissaoAvalista, estadoCivilAvalista regimeCasamentoAvalista nomeConjugeAvalista cpfConjugeAvalista,portador(a) da Cédula de Identidade RG nº numeroRgAvalista SSP/ufAvalista, inscrito(a) no CPF/MF sob o nº cpfAvalista, endereço eletônico: emailAvalista, residente e domiciliado à logradouroAvalista, nº numeroAvalista,  complementoAvalista, cidadeAvalista/ufAvalista, CEP cepAvalista;");
							} else {
								text = trocaValoresXWPF(text, r, "criaAvalista", "");
							}	
						}
						
						if(this.empresaEmitente) {
							text = trocaValoresXWPF(text, r, "nomeEmpresaEmitente", this.razaoSocialEmitente + ",");
							text = trocaValoresXWPF(text, r, "dadosEmpresaEmitente", "tipoEmpresaEmitente, inscrita no CNPJ/MF sob o nº cnpjEmitente, com sede e foro no município de municipioEmpresaEmitente no Estado de estadoEmpresaEmitente, na ruaEmpresaEmitente, nº numeroEmpresaEmitente, Sala salaEmpresaEmitente, Bairro: bairroEmpresaEmitente, CEP cepEmpresaEmitente, neste ato representada na forma do seu contrato social socioEmitente,");
							text = trocaValoresXWPF(text, r, "tipoEmpresaEmitente", this.tipoEmpresaEmitente);
							text = trocaValoresXWPF(text, r, "cnpjEmitente", this.cnpjEmitente);
							text = trocaValoresXWPF(text, r, "municipioEmpresaEmitente", this.municipioEmpresaEmitente);
							text = trocaValoresXWPF(text, r, "estadoEmpresaEmitente", this.estadoEmpresaEmitente);
							text = trocaValoresXWPF(text, r, "ruaEmpresaEmitente", this.ruaEmpresaEmitente);
							text = trocaValoresXWPF(text, r, "numeroEmpresaEmitente", this.numeroEmpresaEmitente);
							text = trocaValoresXWPF(text, r, "salaEmpresaEmitente", this.salaEmpresaEmitente);
							text = trocaValoresXWPF(text, r, "bairroEmpresaEmitente", this.bairroEmpresaEmitente);
							text = trocaValoresXWPF(text, r, "cepEmpresaEmitente", this.cepEmpresaEmitente);
							if(this.femininoEmitente) {
								text = trocaValoresXWPF(text, r, "socioEmitente", "pela sua única sósia");
							} else {
								text = trocaValoresXWPF(text, r, "socioEmitente", "pelo seu único sósio");
							}
						} else {
								text = trocaValoresXWPF(text, r, "nomeEmpresaEmitente", "");
								text = trocaValoresXWPF(text, r, "dadosEmpresaEmitente", "");
						}
			            
						if(this.getNomeConjugeEmitente() != null) { 	
							text = trocaValoresXWPF(text, r, "nomeConjugeEmitente", " (" + this.nomeConjugeEmitente + ",");	 
							text = trocaValoresXWPF(text, r, "cpfConjugeEmitente", " " + this.cpfConjugeEmitente + ")");
							text = trocaValoresXWPF(text, r, "regimeCasamentoEmitente", "sob o regime " + this.regimeCasamentoEmitente);
						} else {
							text = trocaValoresXWPF(text, r, "cpfConjugeEmitente", "");
							text = trocaValoresXWPF(text, r, "nomeConjugeEmitente", "");		 
							text = trocaValoresXWPF(text, r, "FiducianteConjugue","");
							text = trocaValoresXWPF(text, r, "regimeCasamentoEmitente", "");
						}
						text = trocaValoresXWPF(text, r, "estadoCivilEmitente", verificaEstadoCivil(this.femininoEmitente,this.estadoCivilEmitente).toLowerCase());	 
						text = trocaValoresXWPF(text, r, "nomeEmitente", this.nomeEmitente);	 
						text = trocaValoresXWPF(text, r, "cpfEmitente", this.cpfEmitente);
						text = trocaValoresXWPF(text, r, "nacionalidadeEmitente",verificaNacionalidade(this.femininoEmitente, this.nacionalidadeEmitente));
						text = trocaValoresXWPF(text, r, "profissaoEmitente",this.profissaoEmitente.toLowerCase());
						text = trocaValoresXWPF(text, r, "numeroRgEmitente", this.numeroRgEmitente);
						text = trocaValoresXWPF(text, r, "ufEmitente", this.ufEmitente);
						text = trocaValoresXWPF(text, r, "cpfEmitente", this.cpfEmitente);
						text = trocaValoresXWPF(text, r, "logradouroEmitente", this.logradouroEmitente);
						text = trocaValoresXWPF(text, r, "numeroEmitente", this.numeroEmitente);
						text = trocaValoresXWPF(text, r, "complementoEmitente", this.complementoEmitente);
						text = trocaValoresXWPF(text, r, "cidadeEmitente", this.cidadeEmitente);
						text = trocaValoresXWPF(text, r, "cepEmitente", this.cepEmitente);
						text = trocaValoresXWPF(text, r, "emailEmitente", this.emailEmitente);
						if(this.femininoEmitente) {
							text = trocaValoresXWPF(text, r, "filhoEmitente", "filha");
						} else {
							text = trocaValoresXWPF(text, r, "filhoEmitente", "filho");
						}
						text = trocaValoresXWPF(text, r, "paiEmitente", this.paiEmitente);
						text = trocaValoresXWPF(text, r, "maeEmitente", this.maeEmitente);

						
						if (this.addInterveniente == true) {
							text = trocaValoresXWPF(text, r,"fiducianteInterveniente", "\n"
									+ "Pelo ANUENTE: \n"

									+ "nomeInterveniente \n"

									+ "logradouroInterveniente, nº numeroInterveniente,  cidadeInterveniente - ufInterveniente - \n"

									+ "CEP cepInterveniente \n"

									+ "Email: emailInterveniente \n");
							
							if (this.getNomeConjugeInterveniente() != null) {
								text = trocaValoresXWPF(text, r, "nomeConjugeInterveniente",
										" (" + this.nomeConjugeInterveniente + ",");
								text = trocaValoresXWPF(text, r, "cpfConjugeInterveniente",
										" " + this.cpfConjugeInterveniente + ")");
								text = trocaValoresXWPF(text, r, "regimeCasamentoInterveniente",
										"sob o regime " + this.regimeCasamentoInterveniente);
							} else {
								text = trocaValoresXWPF(text, r, "cpfConjugeInterveniente", "");
								text = trocaValoresXWPF(text, r, "nomeConjugeInterveniente", "");
								text = trocaValoresXWPF(text, r, "FiducianteConjugue", "");
								text = trocaValoresXWPF(text, r, "regimeCasamentoInterveniente", "");					
							}
							
							if(this.empresaInterveniente) {
								text = trocaValoresXWPF(text, r, "nomeEmpresaInterveniente", this.razaoSocialInterveniente + ",");
								text = trocaValoresXWPF(text, r, "dadosEmpresaInterveniente", "tipoEmpresaInterveniente, inscrita no CNPJ/MF sob o nº cnpjInterveniente, com sede e foro no município de municipioEmpresaInterveniente no Estado de estadoEmpresaInterveniente, na ruaEmpresaInterveniente, nº numeroEmpresaInterveniente, Sala salaEmpresaInterveniente, Bairro: bairroEmpresaInterveniente, CEP cepEmpresaInterveniente, neste ato representada na forma do seu contrato social socioInterveniente,");
								text = trocaValoresXWPF(text, r, "tipoEmpresaInterveniente", this.tipoEmpresaInterveniente);
								text = trocaValoresXWPF(text, r, "cnpjInterveniente", this.cnpjInterveniente);
								text = trocaValoresXWPF(text, r, "municipioEmpresaInterveniente", this.municipioEmpresaInterveniente);
								text = trocaValoresXWPF(text, r, "estadoEmpresaInterveniente", this.estadoEmpresaInterveniente);
								text = trocaValoresXWPF(text, r, "ruaEmpresaInterveniente", this.ruaEmpresaInterveniente);
								text = trocaValoresXWPF(text, r, "numeroEmpresaInterveniente", this.numeroEmpresaInterveniente);
								text = trocaValoresXWPF(text, r, "salaEmpresaInterveniente", this.salaEmpresaInterveniente);
								text = trocaValoresXWPF(text, r, "bairroEmpresaInterveniente", this.bairroEmpresaInterveniente);
								text = trocaValoresXWPF(text, r, "cepEmpresaInterveniente", this.cepEmpresaInterveniente);
								if(this.femininoInterveniente) {
									text = trocaValoresXWPF(text, r, "socioInterveniente", "pela sua única sósia");
								} else {
									text = trocaValoresXWPF(text, r, "socioInterveniente", "pelo seu único sósio");
								}
							} else {
									text = trocaValoresXWPF(text, r, "nomeEmpresaInterveniente", "");
									text = trocaValoresXWPF(text, r, "dadosEmpresaInterveniente", "");
							}
							
							text = trocaValoresXWPF(text, r, "estadoCivilInterveniente", verificaEstadoCivil(this.femininoInterveniente, this.estadoCivilInterveniente).toLowerCase());
							text = trocaValoresXWPF(text, r, "nomeInterveniente", this.nomeInterveniente);
							text = trocaValoresXWPF(text, r, "cpfInterveniente", this.cpfInterveniente);
							text = trocaValoresXWPF(text, r, "nacionalidadeInterveniente", verificaNacionalidade(this.femininoInterveniente, this.nacionalidadeInterveniente));
							text = trocaValoresXWPF(text, r, "profissaoInterveniente", this.profissaoInterveniente.toLowerCase());
							text = trocaValoresXWPF(text, r, "numeroRgInterveniente", this.numeroRgInterveniente);
							text = trocaValoresXWPF(text, r, "ufInterveniente", this.ufInterveniente);
							text = trocaValoresXWPF(text, r, "cpfInterveniente", this.cpfInterveniente);
							text = trocaValoresXWPF(text, r, "logradouroInterveniente", this.logradouroInterveniente);
							text = trocaValoresXWPF(text, r, "numeroInterveniente", this.numeroInterveniente);
							text = trocaValoresXWPF(text, r, "complementoInterveniente", this.complementoInterveniente);
							text = trocaValoresXWPF(text, r, "cidadeInterveniente", this.cidadeInterveniente);
							text = trocaValoresXWPF(text, r, "cepInterveniente", this.cepInterveniente);
							text = trocaValoresXWPF(text, r, "emailInterveniente", this.emailInterveniente);
							if(this.femininoInterveniente) {
								text = trocaValoresXWPF(text, r, "filhoInterveniente", "filha");
							} else {
								text = trocaValoresXWPF(text, r, "filhoInterveniente", "filho");
							}
							text = trocaValoresXWPF(text, r, "paiInterveniente", this.paiInterveniente);
							text = trocaValoresXWPF(text, r, "maeInterveniente", this.maeInterveniente);

						} else {
							text = trocaValoresXWPF(text, r, "fiducianteInterveniente","");
						}
						if (this.addAvalista == true) {
							text = trocaValoresXWPF(text, r, "fiducianteAvalista","\n"
									+"nomeAvalista	 \n"

									+ "logradouroAvalista, nº numeroAvalista,  cidadeAvalista - ufAvalista - \n"

									+ "CEP cepAvalista \n"

									+ "Email: emailAvalista \n");
							
							if (this.getNomeConjugeAvalista() != null) {
								text = trocaValoresXWPF(text, r, "nomeConjugeAvalista",
										" (" + this.nomeConjugeAvalista + ",");
								text = trocaValoresXWPF(text, r, "cpfConjugeAvalista",
										" " + this.cpfConjugeAvalista + ")");
								text = trocaValoresXWPF(text, r, "regimeCasamentoAvalista",
										"sob o regime " + this.regimeCasamentoAvalista);
							} else {
								text = trocaValoresXWPF(text, r, "cpfConjugeAvalista", "");
								text = trocaValoresXWPF(text, r, "nomeConjugeAvalista", "");
								text = trocaValoresXWPF(text, r, "FiducianteConjugue", "");
								text = trocaValoresXWPF(text, r, "regimeCasamentoAvalista", "");
							}
							
							if(this.empresaAvalista) {
								text = trocaValoresXWPF(text, r, "nomeEmpresaAvalista", this.razaoSocialAvalista + ",");
								text = trocaValoresXWPF(text, r, "dadosEmpresaAvalista", "tipoEmpresaAvalista, inscrita no CNPJ/MF sob o nº cnpjAvalista, com sede e foro no município de municipioEmpresaAvalista no Estado de estadoEmpresaAvalista, na ruaEmpresaAvalista, nº numeroEmpresaAvalista, Sala salaEmpresaAvalista, Bairro: bairroEmpresaAvalista, CEP cepEmpresaAvalista, neste ato representada na forma do seu contrato social socioAvalista,");
								text = trocaValoresXWPF(text, r, "tipoEmpresaAvalista", this.tipoEmpresaAvalista);
								text = trocaValoresXWPF(text, r, "cnpjAvalista", this.cnpjAvalista);
								text = trocaValoresXWPF(text, r, "municipioEmpresaAvalista", this.municipioEmpresaAvalista);
								text = trocaValoresXWPF(text, r, "estadoEmpresaAvalista", this.estadoEmpresaAvalista);
								text = trocaValoresXWPF(text, r, "ruaEmpresaAvalista", this.ruaEmpresaAvalista);
								text = trocaValoresXWPF(text, r, "numeroEmpresaAvalista", this.numeroEmpresaAvalista);
								text = trocaValoresXWPF(text, r, "salaEmpresaAvalista", this.salaEmpresaAvalista);
								text = trocaValoresXWPF(text, r, "bairroEmpresaAvalista", this.bairroEmpresaAvalista);
								text = trocaValoresXWPF(text, r, "cepEmpresaAvalista", this.cepEmpresaAvalista);
								if(this.femininoAvalista) {
									text = trocaValoresXWPF(text, r, "socioAvalista", "pela sua única sósia");
								} else {
									text = trocaValoresXWPF(text, r, "socioAvalista", "pelo seu único sósio");
								}
							} else {
									text = trocaValoresXWPF(text, r, "nomeEmpresaAvalista", "");
									text = trocaValoresXWPF(text, r, "dadosEmpresaAvalista", "");
							}
							
							text = trocaValoresXWPF(text, r, "estadoCivilAvalista", verificaEstadoCivil(this.femininoAvalista, this.estadoCivilAvalista).toLowerCase());
							text = trocaValoresXWPF(text, r, "nomeAvalista", this.nomeAvalista);
							text = trocaValoresXWPF(text, r, "cpfAvalista", this.cpfAvalista);
							text = trocaValoresXWPF(text, r, "nacionalidadeAvalista", verificaNacionalidade(this.femininoAvalista, this.nacionalidadeAvalista));
							text = trocaValoresXWPF(text, r, "profissaoAvalista", this.profissaoAvalista.toLowerCase());
							text = trocaValoresXWPF(text, r, "numeroRgAvalista", this.numeroRgAvalista);
							text = trocaValoresXWPF(text, r, "ufAvalista", this.ufAvalista);
							text = trocaValoresXWPF(text, r, "cpfAvalista", this.cpfAvalista);
							text = trocaValoresXWPF(text, r, "logradouroAvalista", this.logradouroAvalista);
							text = trocaValoresXWPF(text, r, "numeroAvalista", this.numeroAvalista);
							text = trocaValoresXWPF(text, r, "complementoAvalista", this.complementoAvalista);
							text = trocaValoresXWPF(text, r, "cidadeAvalista", this.cidadeAvalista);
							text = trocaValoresXWPF(text, r, "cepAvalista", this.cepAvalista);
							text = trocaValoresXWPF(text, r, "emailAvalista", this.emailAvalista);
							if(this.femininoAvalista) {
								text = trocaValoresXWPF(text, r, "filhoAvalista", "filha");
							} else {
								text = trocaValoresXWPF(text, r, "filhoAvalista", "filho");
							}
							text = trocaValoresXWPF(text, r, "paiAvalista", this.paiAvalista);
							text = trocaValoresXWPF(text, r, "maeAvalista", this.maeAvalista);
						} else {
							text = trocaValoresXWPF(text, r, "fiducianteAvalista","");
						}
						
						if (this.addTerceiro == true) {
							text = trocaValoresXWPF(text, r, "fiducianteTerceiroG","\n"
									+"nomeTerceiroG \n"

									+ "logradouroTerceiroG, nº numeroTerceiroG,  cidadeTerceiroG - ufTerceiroG - \n"

									+ "CEP cepTerceiroG \n"

									+ "Email: emailTerceiroG \n");
							
							
							if (this.getNomeConjugeTerceiroG() != null) {
								text = trocaValoresXWPF(text, r, "nomeConjugeTerceiroG",
										" (" + this.nomeConjugeTerceiroG + ",");
								text = trocaValoresXWPF(text, r, "cpfConjugeTerceiroG",
										" " + this.cpfConjugeTerceiroG + ")");
								text = trocaValoresXWPF(text, r, "regimeCasamentoTerceiroG",
										"sob o regime " + this.regimeCasamentoTerceiroG);
								
								
								
							} else {
								text = trocaValoresXWPF(text, r, "cpfConjugeTerceiroG", "");
								text = trocaValoresXWPF(text, r, "nomeConjugeTerceiroG", "");
								text = trocaValoresXWPF(text, r, "FiducianteConjugue", "");
								text = trocaValoresXWPF(text, r, "regimeCasamentoTerceiroG", "");
							}
							
							if(this.empresaTerceiroG) {
								text = trocaValoresXWPF(text, r, "nomeEmpresaTerceiroG", this.razaoSocialTerceiroG + ",");
								text = trocaValoresXWPF(text, r, "dadosEmpresaTerceiroG", "tipoEmpresaTerceiroG, inscrita no CNPJ/MF sob o nº cnpjTerceiroG, com sede e foro no município de municipioEmpresaTerceiroG no Estado de estadoEmpresaTerceiroG, na ruaEmpresaTerceiroG, nº numeroEmpresaTerceiroG, Sala salaEmpresaTerceiroG, Bairro: bairroEmpresaTerceiroG, CEP cepEmpresaTerceiroG, neste ato representada na forma do seu contrato social socioTerceiroG,");
								text = trocaValoresXWPF(text, r, "tipoEmpresaTerceiroG", this.tipoEmpresaTerceiroG);
								text = trocaValoresXWPF(text, r, "cnpjTerceiroG", this.cnpjTerceiroG);
								text = trocaValoresXWPF(text, r, "municipioEmpresaTerceiroG", this.municipioEmpresaTerceiroG);
								text = trocaValoresXWPF(text, r, "estadoEmpresaTerceiroG", this.estadoEmpresaTerceiroG);
								text = trocaValoresXWPF(text, r, "ruaEmpresaTerceiroG", this.ruaEmpresaTerceiroG);
								text = trocaValoresXWPF(text, r, "numeroEmpresaTerceiroG", this.numeroEmpresaTerceiroG);
								text = trocaValoresXWPF(text, r, "salaEmpresaTerceiroG", this.salaEmpresaTerceiroG);
								text = trocaValoresXWPF(text, r, "bairroEmpresaTerceiroG", this.bairroEmpresaTerceiroG);
								text = trocaValoresXWPF(text, r, "cepEmpresaTerceiroG", this.cepEmpresaTerceiroG);
								if(this.femininoTerceiroG) {
									text = trocaValoresXWPF(text, r, "socioTerceiroG", "pela sua única sósia");
								} else {
									text = trocaValoresXWPF(text, r, "socioTerceiroG", "pelo seu único sósio");
								}
							} else {
									text = trocaValoresXWPF(text, r, "nomeEmpresaTerceiroG", "");
									text = trocaValoresXWPF(text, r, "dadosEmpresaTerceiroG", "");
							}
							
							text = trocaValoresXWPF(text, r, "estadoCivilTerceiroG",
									verificaEstadoCivil(this.femininoTerceiroG, this.estadoCivilTerceiroG)
											.toLowerCase());
							text = trocaValoresXWPF(text, r, "nomeTerceiroG", this.nomeTerceiroG);
							text = trocaValoresXWPF(text, r, "cpfTerceiroG", this.cpfTerceiroG);
							text = trocaValoresXWPF(text, r, "nacionalidadeTerceiroG", verificaNacionalidade(this.femininoTerceiroG, this.nacionalidadeTerceiroG));
							text = trocaValoresXWPF(text, r, "profissaoTerceiroG",
									this.profissaoTerceiroG.toLowerCase());
							text = trocaValoresXWPF(text, r, "numeroRgTerceiroG", this.numeroRgTerceiroG);
							text = trocaValoresXWPF(text, r, "ufTerceiroG", this.ufTerceiroG);
							text = trocaValoresXWPF(text, r, "cpfTerceiroG", this.cpfTerceiroG);
							text = trocaValoresXWPF(text, r, "logradouroTerceiroG", this.logradouroTerceiroG);
							text = trocaValoresXWPF(text, r, "numeroTerceiroG", this.numeroTerceiroG);
							text = trocaValoresXWPF(text, r, "complementoTerceiroG", this.complementoTerceiroG);
							text = trocaValoresXWPF(text, r, "cidadeTerceiroG", this.cidadeTerceiroG);
							text = trocaValoresXWPF(text, r, "cepTerceiroG", this.cepTerceiroG);
							text = trocaValoresXWPF(text, r, "emailTerceiroG", this.emailTerceiroG);
	
							if(this.femininoTerceiroG) {
								text = trocaValoresXWPF(text, r, "filhoTerceiroG", "filha");
							} else {
								text = trocaValoresXWPF(text, r, "filhoTerceiroG", "filho");
							}
							text = trocaValoresXWPF(text, r, "paiTerceiroG", this.paiTerceiroG);
							text = trocaValoresXWPF(text, r, "maeTerceiroG", this.maeTerceiroG);
						} else {
							text = trocaValoresXWPF(text, r, "fiducianteTerceiroG","");
						}
						
						text = trocaValoresXWPF(text, r, "valorCredito", this.valorCredito, "R$ ");
						text = trocaValoresXWPF(text, r, "custoEmissao", this.custoEmissao, "R$ ");
						text = trocaValoresXWPF(text, r, "valorIOF", this.valorIOF, "R$ ");
						text = trocaValoresXWPF(text, r, "valorDespesas", this.valorDespesas, "R$ ");
						text = trocaValoresXWPF(text, r, "valorLiquidoCredito", this.valorLiquidoCredito, "R$ ");
						text = trocaValoresXWPF(text, r, "taxaDeJurosMes", this.taxaDeJurosMes );
						text = trocaValoresXWPF(text, r, "taxaDeJurosAno", this.taxaDeJurosAno);
						
						text = trocaValoresXWPF(text, r, "cetMes", this.cetMes);
						text = trocaValoresXWPF(text, r, "cetAno", this.cetAno);
						text = trocaValoresXWPF(text, r, "contaCorrente", this.contaCorrente);
						text = trocaValoresXWPF(text, r, "agencia", this.agencia);
						text = trocaValoresXWPF(text, r, "numeroBanco", this.numeroBanco);
						text = trocaValoresXWPF(text, r, "nomeBanco", this.nomeBanco);
						
						text = trocaValoresXWPF(text, r, "numeroParcelasPagamento", this.numeroParcelasPagamento);
						text = trocaValoresXWPF(text, r, "vencimentoPrimeiraParcelaPagamento", this.vencimentoPrimeiraParcelaPagamento);
						text = trocaValoresXWPF(text, r, "vencimentoUltimaParcelaPagamento", this.vencimentoUltimaParcelaPagamento);
						text = trocaValoresXWPF(text, r, "montantePagamento", this.montantePagamento, "R$ ");
						
						text = trocaValoresXWPF(text, r, "numeroParcelasMIP", this.numeroParcelasMIP);
						text = trocaValoresXWPF(text, r, "vencimentoPrimeiraParcelaMIP", this.vencimentoPrimeiraParcelaMIP);
						text = trocaValoresXWPF(text, r, "vencimentoUltimaParcelaMIP", this.vencimentoUltimaParcelaMIP);
						text = trocaValoresXWPF(text, r, "montanteMIP", this.montanteMIP, "R$ ");
						
						text = trocaValoresXWPF(text, r, "numeroParcelasDFI", this.numeroParcelasDFI);
						text = trocaValoresXWPF(text, r, "vencimentoPrimeiraParcelaDFI", this.vencimentoPrimeiraParcelaDFI);
						text = trocaValoresXWPF(text, r, "vencimentoUltimaParcelaDFI", this.vencimentoUltimaParcelaDFI);
						text = trocaValoresXWPF(text, r, "montanteDFI", this.montanteDFI, "R$ ");
						
						text = trocaValoresXWPF(text, r, "tarifaAntecipada", this.tarifaAntecipada);
						text = trocaValoresXWPF(text, r, "dataDeEmissao", this.dataDeEmissao);
								
						text = trocaValoresXWPF(text, r, "numeroImovel", this.numeroImovel);
						text = trocaValoresXWPF(text, r, "cartorioImovel", this.cartorioImovel);
						text = trocaValoresXWPF(text, r, "cidadeImovel", this.cidadeImovel);
						text = trocaValoresXWPF(text, r, "ufImovel", this.ufImovel);
						
						text = trocaValoresXWPF(text, r, "emissaoDia", this.dataDeEmissao.getDate());
						text = trocaValoresXWPF(text, r, "emissaoMes", CommonsUtil.formataMesExtenso(dataDeEmissao).toLowerCase());
						text = trocaValoresXWPF(text, r, "emissaoAno", (this.dataDeEmissao.getYear() + 1900));
						
						text = trocaValoresXWPF(text, r, "vencimentoPrimeiraParcelaDia", this.vencimentoPrimeiraParcelaPagamento.getDate());
						text = trocaValoresXWPF(text, r, "vencimentoPrimeiraParcelaMes", CommonsUtil.formataMesExtenso(vencimentoPrimeiraParcelaPagamento).toLowerCase());
						text = trocaValoresXWPF(text, r, "vencimentoPrimeiraParcelaAno", (this.vencimentoPrimeiraParcelaPagamento.getYear() + 1900));
						
						text = trocaValoresXWPF(text, r, "vendaLeilao", this.vendaLeilao, "R$ ");
						text = trocaValoresXWPF(text, r, "elaboradorNome", this.elaboradorNome);
						text = trocaValoresXWPF(text, r, "elaboradorCrea", this.elaboradorCrea);
						text = trocaValoresXWPF(text, r, "responsavelNome", this.responsavelNome);
						text = trocaValoresXWPF(text, r, "responsavelCrea", this.responsavelCrea);
						text = trocaValoresXWPF(text, r, "porcentagemImovel", this.porcentagemImovel);
						
						text = trocaValoresDinheiroExtensoXWPF(text, r, "VendaLeilao", this.vendaLeilao);
						text = trocaValoresTaxaExtensoXWPF(text, r, "PorcentagemImovel", this.porcentagemImovel);	
						text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorCredito", this.valorCredito);
						text = trocaValoresDinheiroExtensoXWPF(text, r, "CustoEmissao", this.custoEmissao);
						text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorIOF", this.valorIOF);
						text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorDespesas", this.valorDespesas);
						text = trocaValoresDinheiroExtensoXWPF(text, r, "ValorLiquidoCredito", this.valorLiquidoCredito);						
						text = trocaValoresNumeroExtensoXWPF(text, r, "NumeroParcelasPagamento", this.numeroParcelasPagamento);						
						text = trocaValoresDinheiroExtensoXWPF(text, r, "MontantePagamento", this.montantePagamento);						
						text = trocaValoresNumeroExtensoXWPF(text, r, "NumeroParcelasMIP", this.numeroParcelasMIP);						
						text = trocaValoresDinheiroExtensoXWPF(text, r, "MontanteMIP", this.montanteMIP);						
						text = trocaValoresNumeroExtensoXWPF(text, r, "NumeroParcelasDFI", this.numeroParcelasDFI);						
						text = trocaValoresDinheiroExtensoXWPF(text, r, "MontanteDFI", this.montanteDFI);
						text = trocaValoresTaxaExtensoXWPF(text, r, "TarifaAntecipada", this.tarifaAntecipada);
								 
						if (text != null && text.contains("ImagemImovel") && filesList.size() > 0) {
							r.addBreak();
							this.populateFiles(0);
							r.addPicture(this.getBis(), fileTypeInt, fileName.toLowerCase(), Units.toEMU(400), Units.toEMU(300));
							r.addBreak();
						} 				
						text = trocaValoresXWPF(text, r, "ImagemImovel", "");
						
						adicionarEnter(text, r);
			        }
			    }
			}
			
			for (XWPFTable tbl : document.getTables()) {
				for (XWPFTableRow row : tbl.getRows()) {
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
								String text = r.getText(0);
								
								text = trocaValoresXWPF(text, r, "nomeEmitente", this.nomeEmitente);	 		
								text = trocaValoresXWPF(text, r, "nomeTestemunha1", this.nomeTestemunha1);
								text = trocaValoresXWPF(text, r, "cpfTestemunha1", this.cpfTestemunha1);
								text = trocaValoresXWPF(text, r, "rgTestemunha1", this.rgTestemunha1);
								
								text = trocaValoresXWPF(text, r, "nomeTestemunha2", this.nomeTestemunha2);
								text = trocaValoresXWPF(text, r, "cpfTestemunha2", this.cpfTestemunha2);
								text = trocaValoresXWPF(text, r, "rgTestemunha2", this.rgTestemunha2);
								
								if (CommonsUtil.mesmoValor(this.fiduciante, true)) {
									text = trocaValoresXWPF(text, r, "classeEmitente", "");
								} else {
									text = trocaValoresXWPF(text, r, "classeEmitente", "DEVEDOR");
								}
								
								if (this.addInterveniente == true) {
									text = trocaValoresXWPF(text, r, "nomeInterveniente", this.nomeInterveniente);
									text = trocaValoresXWPF(text, r, "_i", "________________________________________");

								} else {
									text = trocaValoresXWPF(text, r, "nomeInterveniente", "");
									text = trocaValoresXWPF(text, r, "INTERVENIENTE", "");
									text = trocaValoresXWPF(text, r, "ANUENTE", "");
									text = trocaValoresXWPF(text, r, "_i", "");
								}
								if (this.addTerceiro == true) {
									text = trocaValoresXWPF(text, r, "nomeTerceiroG", this.nomeTerceiroG);
									text = trocaValoresXWPF(text, r, "_t", "_________________________________________");
								} else {
									text = trocaValoresXWPF(text, r, "nomeTerceiroG", "");
									text = trocaValoresXWPF(text, r, "TERCEIRO", "");
									text = trocaValoresXWPF(text, r, "GARANTIDOR", "");
									text = trocaValoresXWPF(text, r, "_t", "");
								}
								if (this.addAvalista == true) {
									text = trocaValoresXWPF(text, r, "nomeAvalista", this.nomeAvalista);
									text = trocaValoresXWPF(text, r, "_a", "_________________________________________");

								} else {
									text = trocaValoresXWPF(text, r, "nomeAvalista", "");
									text = trocaValoresXWPF(text, r, "AVALISTA", "");
									text = trocaValoresXWPF(text, r, "_a", "");
								}
							}
						}
					}
				}
			}
			
			
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
	    }
	    catch ( Throwable e )
	    {
	        e.printStackTrace();
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
		this.participanteSelecionado = new CcbVO();
		this.emitenteSelecionado = new PagadorRecebedor();
		this.selectedPagador = new PagadorRecebedor();
		this.intervenienteSelecionado = new PagadorRecebedor();
		this.terceiroGSelecionado = new PagadorRecebedor();
		this.avalistaSelecionado = new PagadorRecebedor();
	}

	public void calculaValorLiquidoCredito() {
		if (this.valorCredito != null && custoEmissao != null && this.valorIOF != null && this.valorDespesas != null)
			this.setValorLiquidoCredito(
					((this.getValorCredito().subtract(this.getCustoEmissao())).subtract(this.getValorIOF()))
							.subtract(this.getValorDespesas()));
	}
	
	public void calculaPorcentagemImovel() {
		if (this.valorCredito != null && vendaLeilao != null) {
				this.setPorcentagemImovel(((this.vendaLeilao.divide(this.valorCredito, MathContext.DECIMAL128)).multiply(BigDecimal.valueOf(100))).setScale(2, BigDecimal.ROUND_HALF_UP));
						
		}

	}
	
	public Date getDataHoje() {
		TimeZone zone = TimeZone.getDefault(); 
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHojeCalendar = Calendar.getInstance(zone, locale);
		Date dataHoje = dataHojeCalendar.getTime();
		
		return dataHoje;
	}
	
	public String clearFieldsEmitirCcb() {
		loadLovs();	
		this.listaParticipantes = new HashSet<>();
		this.participanteSelecionado = new CcbVO();
		this.participanteSelecionado.setPessoa(new PagadorRecebedor());
		this.intervenienteSelecionado = new PagadorRecebedor();
		this.emitenteSelecionado = new PagadorRecebedor();
		this.selectedPagador = new PagadorRecebedor();
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

		this.valorLiquidoCredito = null;
		this.valorCredito = null;
		this.custoEmissao = null;
		this.valorIOF = null;
		this.valorDespesas = null;
		
		this.taxaDeJurosMes = null;
		this.taxaDeJurosAno = null;
		this.cetMes = null;
		this.cetAno = null;
		
		this.contaCorrente = null;
		this.agencia = null;
		this.numeroBanco = null;
		this.nomeBanco = null;
		this.numeroParcelasPagamento = null;
		this.vencimentoPrimeiraParcelaPagamento = null;
		this.vencimentoUltimaParcelaPagamento = null;
		this.montantePagamento = null;

		this.numeroParcelasDFI = null;
		this.vencimentoPrimeiraParcelaDFI = null;
		this.vencimentoUltimaParcelaDFI = null;
		this.montanteDFI = null;

		this.numeroParcelasMIP = null;
		this.vencimentoPrimeiraParcelaMIP = null;
		this.vencimentoUltimaParcelaMIP = null;
		this.montanteMIP = null;

		this.tarifaAntecipada = BigDecimal.ZERO;
		this.dataDeEmissao =  getDataHoje();

		this.numeroImovel = null;
		this.cartorioImovel = null;
		this.cidadeImovel = null;
		this.ufImovel = null;
		
		this.vendaLeilao = null;
		this.elaboradorNome = null;
		this.elaboradorCrea = null;
		this.responsavelNome = null;
		this.responsavelCrea = null;
		this.porcentagemImovel = null;
		
		this.nomeTestemunha1 = null;
		this.cpfTestemunha1 = null;
		this.rgTestemunha1 = null;
		
		this.nomeTestemunha2 = null;
		this.cpfTestemunha2 = null;
		this.rgTestemunha2 = null;
		
		this.uploadedFile = null;
	    this.fileName = null;
	    this.fileType = null;
	    this.fileTypeInt = 0;
	    
	    pesquisaBancosListaNome();
	    pesquisaBancosListaCodigo();
	    clearPagadorRecebedor();
		
		return "/Atendimento/Cobranca/Ccb.xhtml";
	}

	public void loadLovs() {
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listPagadores = pagadorRecebedorDao.findAll();
	}

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

	public BigDecimal getValorLiquidoCredito() {
		return valorLiquidoCredito;
	}

	public void setValorLiquidoCredito(BigDecimal valorLiquidoCredito) {
		this.valorLiquidoCredito = valorLiquidoCredito;
	}

	public BigDecimal getValorCredito() {
		return valorCredito;
	}

	public void setValorCredito(BigDecimal valorCredito) {
		this.valorCredito = valorCredito;
	}

	public BigDecimal getCustoEmissao() {
		return custoEmissao;
	}

	public void setCustoEmissao(BigDecimal custoEmissao) {
		this.custoEmissao = custoEmissao;
	}

	public BigDecimal getValorIOF() {
		return valorIOF;
	}

	public void setValorIOF(BigDecimal valorIOF) {
		this.valorIOF = valorIOF;
	}

	public BigDecimal getValorDespesas() {
		return valorDespesas;
	}

	public void setValorDespesas(BigDecimal valorDespesas) {
		this.valorDespesas = valorDespesas;
	}

	public BigDecimal getTaxaDeJurosMes() {
		return taxaDeJurosMes;
	}

	public void setTaxaDeJurosMes(BigDecimal taxaDeJurosMes) {
		this.taxaDeJurosMes = taxaDeJurosMes;
	}

	public BigDecimal getTaxaDeJurosAno() {
		return taxaDeJurosAno;
	}

	public void setTaxaDeJurosAno(BigDecimal taxaDeJurosAno) {
		this.taxaDeJurosAno = taxaDeJurosAno;
	}

	public BigDecimal getCetMes() {
		return cetMes;
	}

	public void setCetMes(BigDecimal cetMes) {
		this.cetMes = cetMes;
	}

	public BigDecimal getCetAno() {
		return cetAno;
	}

	public void setCetAno(BigDecimal cetAno) {
		this.cetAno = cetAno;
	}

	public String getContaCorrente() {
		return contaCorrente;
	}

	public void setContaCorrente(String contaCorrente) {
		this.contaCorrente = contaCorrente;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getNumeroBanco() {
		return numeroBanco;
	}

	public void setNumeroBanco(String numeroBanco) {
		this.numeroBanco = numeroBanco;
	}

	public String getNomeBanco() {
		return nomeBanco;
	}

	public void setNomeBanco(String nomeBanco) {
		this.nomeBanco = nomeBanco;
	}

	public String getNumeroParcelasPagamento() {
		return numeroParcelasPagamento;
	}

	public void setNumeroParcelasPagamento(String numeroParcelasPagamento) {
		this.numeroParcelasPagamento = numeroParcelasPagamento;
	}

	public Date getVencimentoPrimeiraParcelaPagamento() {
		return vencimentoPrimeiraParcelaPagamento;
	}

	public void setVencimentoPrimeiraParcelaPagamento(Date vencimentoPrimeiraParcelaPagamento) {
		this.vencimentoPrimeiraParcelaPagamento = vencimentoPrimeiraParcelaPagamento;
	}

	public Date getVencimentoUltimaParcelaPagamento() {
		return vencimentoUltimaParcelaPagamento;
	}

	public void setVencimentoUltimaParcelaPagamento(Date vencimentoUltimaParcelaPagamento) {
		this.vencimentoUltimaParcelaPagamento = vencimentoUltimaParcelaPagamento;
	}

	public BigDecimal getMontantePagamento() {
		return montantePagamento;
	}

	public void setMontantePagamento(BigDecimal montantePagamento) {
		this.montantePagamento = montantePagamento;
	}

	public String getNumeroParcelasDFI() {
		return numeroParcelasDFI;
	}

	public void setNumeroParcelasDFI(String numeroParcelasDFI) {
		this.numeroParcelasDFI = numeroParcelasDFI;
	}

	public Date getVencimentoPrimeiraParcelaDFI() {
		return vencimentoPrimeiraParcelaDFI;
	}

	public void setVencimentoPrimeiraParcelaDFI(Date vencimentoPrimeiraParcelaDFI) {
		this.vencimentoPrimeiraParcelaDFI = vencimentoPrimeiraParcelaDFI;
	}

	public Date getVencimentoUltimaParcelaDFI() {
		return vencimentoUltimaParcelaDFI;
	}

	public void setVencimentoUltimaParcelaDFI(Date vencimentoUltimaParcelaDFI) {
		this.vencimentoUltimaParcelaDFI = vencimentoUltimaParcelaDFI;
	}

	public BigDecimal getMontanteDFI() {
		return montanteDFI;
	}

	public void setMontanteDFI(BigDecimal montanteDFI) {
		this.montanteDFI = montanteDFI;
	}

	public String getNumeroParcelasMIP() {
		return numeroParcelasMIP;
	}

	public void setNumeroParcelasMIP(String numeroParcelasMIP) {
		this.numeroParcelasMIP = numeroParcelasMIP;
	}

	public Date getVencimentoPrimeiraParcelaMIP() {
		return vencimentoPrimeiraParcelaMIP;
	}

	public void setVencimentoPrimeiraParcelaMIP(Date vencimentoPrimeiraParcelaMIP) {
		this.vencimentoPrimeiraParcelaMIP = vencimentoPrimeiraParcelaMIP;
	}

	public Date getVencimentoUltimaParcelaMIP() {
		return vencimentoUltimaParcelaMIP;
	}

	public void setVencimentoUltimaParcelaMIP(Date vencimentoUltimaParcelaMIP) {
		this.vencimentoUltimaParcelaMIP = vencimentoUltimaParcelaMIP;
	}

	public BigDecimal getMontanteMIP() {
		return montanteMIP;
	}

	public void setMontanteMIP(BigDecimal montanteMIP) {
		this.montanteMIP = montanteMIP;
	}

	public BigDecimal getTarifaAntecipada() {
		return tarifaAntecipada;
	}

	public void setTarifaAntecipada(BigDecimal tarifaAntecipada) {
		this.tarifaAntecipada = tarifaAntecipada;
	}

	public Date getDataDeEmissao() {
		return dataDeEmissao;
	}

	public void setDataDeEmissao(Date dataDeEmissao) {
		this.dataDeEmissao = dataDeEmissao;
	}

	public String getNumeroImovel() {
		return numeroImovel;
	}

	public void setNumeroImovel(String numeroImovel) {
		this.numeroImovel = numeroImovel;
	}

	public String getCartorioImovel() {
		return cartorioImovel;
	}

	public void setCartorioImovel(String cartorioImovel) {
		this.cartorioImovel = cartorioImovel;
	}

	public String getCidadeImovel() {
		return cidadeImovel;
	}

	public void setCidadeImovel(String cidadeImovel) {
		this.cidadeImovel = cidadeImovel;
	}

	public String getUfImovel() {
		return ufImovel;
	}

	public void setUfImovel(String ufImovel) {
		this.ufImovel = ufImovel;
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

	public BigDecimal getVendaLeilao() {
		return vendaLeilao;
	}

	public void setVendaLeilao(BigDecimal vendaLeilao) {
		this.vendaLeilao = vendaLeilao;
	}

	public String getElaboradorNome() {
		return elaboradorNome;
	}

	public void setElaboradorNome(String elaboradorNome) {
		this.elaboradorNome = elaboradorNome;
	}

	public String getElaboradorCrea() {
		return elaboradorCrea;
	}

	public void setElaboradorCrea(String elaboradorCrea) {
		this.elaboradorCrea = elaboradorCrea;
	}

	public String getResponsavelNome() {
		return responsavelNome;
	}

	public void setResponsavelNome(String responsavelNome) {
		this.responsavelNome = responsavelNome;
	}

	public String getResponsavelCrea() {
		return responsavelCrea;
	}

	public void setResponsavelCrea(String responsavelCrea) {
		this.responsavelCrea = responsavelCrea;
	}

	public BigDecimal getPorcentagemImovel() {
		return porcentagemImovel;
	}

	public void setPorcentagemImovel(BigDecimal porcentagemImovel) {
		this.porcentagemImovel = porcentagemImovel;
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

	public String getNomeTestemunha1() {
		return nomeTestemunha1;
	}

	public void setNomeTestemunha1(String nomeTestemunha1) {
		this.nomeTestemunha1 = nomeTestemunha1;
	}

	public String getCpfTestemunha1() {
		return cpfTestemunha1;
	}

	public void setCpfTestemunha1(String cpfTestemunha1) {
		this.cpfTestemunha1 = cpfTestemunha1;
	}

	public String getRgTestemunha1() {
		return rgTestemunha1;
	}

	public void setRgTestemunha1(String rgTestemunha1) {
		this.rgTestemunha1 = rgTestemunha1;
	}

	public String getNomeTestemunha2() {
		return nomeTestemunha2;
	}

	public void setNomeTestemunha2(String nomeTestemunha2) {
		this.nomeTestemunha2 = nomeTestemunha2;
	}

	public String getCpfTestemunha2() {
		return cpfTestemunha2;
	}

	public void setCpfTestemunha2(String cpfTestemunha2) {
		this.cpfTestemunha2 = cpfTestemunha2;
	}

	public String getRgTestemunha2() {
		return rgTestemunha2;
	}

	public void setRgTestemunha2(String rgTestemunha2) {
		this.rgTestemunha2 = rgTestemunha2;
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

	public List<SelectItem> getListaBancosNome() {
		return listaBancosNome;
	}

	public void setListaBancosNome(List<SelectItem> listaBancosNome) {
		this.listaBancosNome = listaBancosNome;
	}

	public List<SelectItem> getListaBancosCodigo() {
		return listaBancosCodigo;
	}

	public void setListaBancosCodigo(List<SelectItem> listaBancosCodigo) {
		this.listaBancosCodigo = listaBancosCodigo;
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

	public Set<CcbVO> getListaParticipantes() {
		return listaParticipantes;
	}

	public void setListaParticipantes(Set<CcbVO> listaParticipantes) {
		this.listaParticipantes = listaParticipantes;
	}

	public ArrayList<UploadedFile> getFilesList() {
		return filesList;
	}

	public void setFilesList(ArrayList<UploadedFile> filesList) {
		this.filesList = filesList;
	}

	public CcbVO getParticipanteSelecionado() {
		return participanteSelecionado;
	}

	public void setParticipanteSelecionado(CcbVO participanteSelecionado) {
		this.participanteSelecionado = participanteSelecionado;
	}

	public boolean isAddParticipante() {
		return addParticipante;
	}
	
	public void setAddParticipante(boolean addParticipante) {
		this.addParticipante = addParticipante;
	}
}
