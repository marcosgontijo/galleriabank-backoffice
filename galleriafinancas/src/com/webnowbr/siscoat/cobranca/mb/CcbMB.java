package com.webnowbr.siscoat.cobranca.mb;

import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import org.apache.poi.xwpf.usermodel.XWPFAbstractNum;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLevelText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumFmt;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumbering;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;
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
    private ArrayList<CcbVO> listaParticipantes;
    private boolean addParticipante;
    
    private ArrayList<UploadedFile> filesList = new ArrayList<UploadedFile>();
    
    String tituloPagadorRecebedorDialog = "";
    
    private ContratoCobranca objetoContratoCobranca;
	ValorPorExtenso valorPorExtenso = new ValorPorExtenso();
	NumeroPorExtenso numeroPorExtenso = new NumeroPorExtenso();
	PorcentagemPorExtenso porcentagemPorExtenso = new PorcentagemPorExtenso();
	
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
			if(CommonsUtil.mesmoValor(this.nomeBanco, banco.getNome().toString())) {
				this.setNumeroBanco(banco.getCodigo());
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
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run.setText("CÉDULA DE CRÉDITO BANCÁRIO");	
			run.addCarriageReturn();
			
			
			run.setText("Nº XXXXXX");	
			run.setFontSize(14);
			run.setBold(true);
			run.setUnderline(UnderlinePatterns.SINGLE);
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();		
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("1.	 Partes:");
			run.setBold(true);
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setText("I – CREDOR: BMP MONEY PLUS SOCIEDADE DE CRÉDITO DIRETO S.A.");
			run.setBold(true);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText(", instituição financeira, inscrita no CNPJ/MF sob nº 34.337.707/0001-00,"
					+ " com sede na Av. Paulista, 1765, 1º Andar, CEP 01311-200, São Paulo, SP,"
					+ " neste ato, representada na forma do seu Estatuto Social; ");
			run.addCarriageReturn();
			
			int iParticipante = 2;
			for(CcbVO participante : this.listaParticipantes) {
				paragraph = document.createParagraph();
				paragraph.setAlignment(ParagraphAlignment.BOTH);
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				paragraph.setSpacingBetween(1);
				
				run = paragraph.createRun();
				run.setFontSize(12);
				run.setText(RomanNumerals(iParticipante) + " – " + participante.getTipoParticipante() + ":");
				if(!participante.isEmpresa()) {
					run.setText(" " + participante.getPessoa().getNome() + ", ");
				}
				run.setBold(true);
				XWPFRun run2 = paragraph.createRun();
				run2.setFontSize(12);
				String filho;
				if(participante.isFeminino()) {
					filho = "filha";
				} else {
					filho = "filho";
				}
				String nacionalidade;

				if (participante.isFeminino() == true) {
					if (CommonsUtil.mesmoValor(participante.getNacionalidade(), "brasileiro")) {
						nacionalidade = "brasileira";
					} else {
						nacionalidade = participante.getNacionalidade();
					}
				} else {
					nacionalidade = participante.getNacionalidade();
				}
				String conjuge ="";
				
				PagadorRecebedor pessoa = participante.getPessoa();
				run2.setText( filho + " de " + pessoa.getNomeMae() + " e " + pessoa.getNomePai() + ", "
						+ nacionalidade + ", "+ pessoa.getAtividade() + ", "+ pessoa.getEstadocivil() 
						+ "regimeCasamentoEmitente nomeConjugeEmitente cpfConjugeEmitente,"
						+ " portador(a) da Cédula de Identidade RG nº "+ pessoa.getRg() + "SSP/"+ pessoa.getEstado() +","
						+ " inscrito(a) no CPF/MF sob o nº "+ pessoa.getCpf() +", endereço eletrônico: "+ pessoa.getEmail() +","
						+ " residente e domiciliado à "+ pessoa.getEndereco() +", nº "+ pessoa.getNumero() +", "
						+ pessoa.getComplemento()+", "+ pessoa.getCidade()+"/"+pessoa.getEstado()+", CEP"+ pessoa.getCep()+"; ");
				
				run2.addCarriageReturn();
				
				iParticipante++;
			}
			
			paragraph = document.createParagraph();	
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Considerando que: ");
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();	
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("a) O EMITENTE declara e garante que está devidamente "
					+ "autorizado a firmar a presente Cédula de Crédito Bancário (“CCB”),"
					+ " e assumir todas as obrigações aqui pactuadas e cumprir todos os "
					+ "seus termos e condições até quitação final de todas as obrigações aqui "
					+ "estabelecidas, uma vez que as obrigações pecuniárias assumidas "
					+ "nesta CCB são compatíveis com a capacidade econômico-financeira do"
					+ " EMITENTE para honrá-las;");
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();	
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("b) O EMITENTE declara e garante que cumpre o disposto na"
					+ " legislação referente à Política Nacional de Meio Ambiente"
					+ " e não aplicará os recursos decorrentes desta CCB no financiamento "
					+ "de qualquer atividade ou projeto que caracterize crime contra o"
					+ " meio ambiente, que cause poluição e/ou que prejudique o ordenamento"
					+ " urbano e o patrimônio cultural, obrigando-se a respeitar integralmente"
					+ " as normas contidas nas Leis nº 9.605/98 e nº 9.985/2000 e demais"
					+ " regras complementares; e ainda que não utilizará os recursos no "
					+ "desenvolvimento de suas atividades comerciais e vinculadas ao seu objeto"
					+ " social, formas nocivas ou de exploração de trabalho forçado e/ou mão"
					+ " de obra infantil.");
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();		
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Em garantia do integral cumprimento de todas as obrigações,"
					+ " principais e acessórias, assumidas pelo EMITENTE, as Partes"
					+ " resolvem celebrar a presente Cédula de Crédito Bancário, a qual"
					+ " se regerá pelas seguintes cláusulas e condições: ");
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();		
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.	DAS CARACTERÍSTICAS DA OPERAÇÃO DE CRÉDITO");
			run.setBold(true);
			run.addCarriageReturn();
			
			
			paragraph = document.createParagraph();		
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.1.	Valor do Crédito: ");
			run.setBold(true);
			XWPFRun run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("valorCredito (ExtensoValorCredito);");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.1.1.	Custo de Emissão: ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("custoEmissao (ExtensoCustoEmissao), e será pago pelo EMITENTE na data"
					+ " de emissão desta CCB, sendo o mesmo deduzido no ato da liberação do recurso"
					+ " que entrará a crédito na Conta Corrente descrita no item 2.5 desta CCB, e"
					+ " será devido por conta da guarda, manutenção e atualização de dados cadastrais,"
					+ " bem como permanente e contínua geração de dados relativos ao cumprimento dos"
					+ " direitos e obrigações decorrentes deste instrumento;");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.1.2.	Valor do Imposto sobre Operações Financeiras (IOF): ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("valorIOF (ExtensoValorIOF), conforme apurado na Planilha"
					+ " de Cálculo (Anexo I), calculado nos termos da legislação vigente"
					+ " na data de ocorrência do fato gerador, tendo como base de cálculo"
					+ " o Valor do Crédito mencionado no item 2.1;");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.1.3.	Valor destinado ao pagamento de despesas acessórias "
					+ "(devidas a terceiros): ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("valorDespesas (ExtensoValorDespesas);");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.1.4.	Valor Líquido do Crédito: ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("O valor líquido do crédito concedido é de valorLiquidoCredito"
					+ " (ExtensoValorLiquidoCredito), após o desconto do Custo de Emissão,"
					+ " IOF e Despesas Acessórias desta CCB;");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.1.5.");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" O EMITENTE está ciente e concorda que é de sua responsabilidade"
					+ " o pagamento dos valores indicados nos itens supramencionados, bem "
					+ "como os relativos aos tributos e demais despesas que incidam ou venham"
					+ " a incidir sobre a operação, inclusive as que façam necessária para o "
					+ "registro da garantia real perante a circunscrição imobiliária competente.");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.1.6.");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" O EMITENTE concorda que o valor relativo ao IOF será incorporado à"
					+ " sua dívida confessada, sendo pago nos mesmos termos do parcelamento"
					+ " do saldo devedor em aberto.");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.2.	Encargos Financeiros:");
			run.setBold(true);
			run.addCarriageReturn();
			run.setText("(X) Pré-fixado,");
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText(" calculado com base no ano de 365 dias;");
			run2.addCarriageReturn();
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("(X) Pós-fixado: ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("atualização dos valores pela variação mensal do Índice Nacional "
					+ "de Preços ao Consumidor Amplo – IPCA/IBGE, apurado a partir da data"
					+ " de emissão até a efetiva quitação da CCB, sendo esta atualização "
					+ "condição essencial do presente negócio, que o saldo devedor e o valor"
					+ " de cada uma das parcelas serão atualizados monetária e mensalmente, de"
					+ " acordo com o índice de atualização referido;");
			run2.addCarriageReturn();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
	
			paragraph = document.createParagraph();
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.3. 	Taxa de Juros Efetiva: ");
			run.setBold(true);
			run.addCarriageReturn();
			run.setText("Mês: ");
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("taxaDeJurosMes%");
			run2.addCarriageReturn();
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Ano: ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("taxaDeJurosAno%");
			run2.addCarriageReturn();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			
			paragraph = document.createParagraph();
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.4. 	Custo Efetivo Total (“CET”):");
			run.setBold(true);
			run.addCarriageReturn();
			run.setText("Mês: ");
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("cetMes%");
			run2.addCarriageReturn();
			
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Ano: ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("cetAno%");
			run2.addCarriageReturn();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.5. Forma de Liberação do Crédito: ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("O CREDOR realizará o crédito na Conta Corrente nº contaCorrente,"
					+ " Agência nº agencia, BANCO numeroBanco – nomeBanco, em até 5 (cinco)"
					+ " dias úteis após o cumprimento das condições precedentes estabelecidas "
					+ "na cláusula 4.4 abaixo;");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.6. Forma de pagamento: ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("O EMITENTE realizará o pagamento, nos termos do Anexo "
					+ "I desta CCB, em conta corrente do CREDOR ou a quem este indicar; ");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.7. Fluxo de Pagamento (Juros e Amortização): ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("numeroParcelasPagamento (ExtensoNumeroParcelasPagamento)"
					+ " parcelas mensais, sendo a 1ª parcela com vencimento em "
					+ "vencimentoPrimeiraParcelaPagamento e a última com vencimento "
					+ "em vencimentoUltimaParcelaPagamento, corrigidas pela variação"
					+ " mensal do IPCA/IBGE, totalizando, na data de emissão desta CCB,"
					+ " o montante de montantePagamento (ExtensoMontantePagamento), conforme ANEXO I;");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.7.1. Valor e Fluxo de Pagamento do Seguro de Morte e Invalidez Permanente (MIP): ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("numeroParcelasMIP (ExtensoNumeroParcelasMIP) parcelas mensais,"
					+ " sendo a 1ª parcela com vencimento em vencimentoPrimeiraParcelaMIP "
					+ "e a última com vencimento em vencimentoUltimaParcelaMIP, corrigidas"
					+ " pela variação mensal do IPCA/IBGE, totalizando, na data de emissão "
					+ "desta CCB, o montante de montanteMIP (ExtensoMontanteMIP), conforme ANEXO I. ");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.7.2. Valor e Fluxo de Pagamento do Seguro de Danos Físicos ao Imóvel (DFI): ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("numeroParcelasDFI (ExtensoNumeroParcelasDFI) parcelas"
					+ " mensais, sendo a 1ª parcela com vencimento em vencimentoPrimeiraParcelaDFI "
					+ "e a última com vencimento em vencimentoUltimaParcelaDFI, corrigidas pela"
					+ " variação mensal do IPCA/IBGE, totalizando, na data de emissão desta CCB,"
					+ " o montante de montanteDFI (ExtensoMontanteDFI), conforme ANEXO I.");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.8. ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("A atualização pela variação mensal do Índice Nacional"
					+ " de Preços ao Consumidor Amplo – IPCA/IBGE será devida"
					+ " desde o momento da emissão desta CCB, independentemente "
					+ "da data ajustada para o pagamento da 1ª parcela.");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.8.1. ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("O valor da atualização IPCA/IBGE apurado a cada mês, "
					+ "desde a emissão da CCB até a última parcela, será incorporado"
					+ " ao saldo devedor, resultando em um reajuste em todas as parcelas.");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.9. ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("O(s) EMITENTE(S) poderá(ão) verificar as datas de divulgação dos indicadores no sítio eletrônico ");
			run2.setBold(false);
			XWPFRun run3 = paragraph.createRun();
			run3.setFontSize(12);
			run3.setText("www.ibge.gov.br/calendario-indicadores-novoportal");
			
			XWPFRun run4 = paragraph.createRun();
			run4.setFontSize(12);
			run4.removeCarriageReturn();
			run4.setText(", ou em outro que vier a substituí-lo.");
			
			run4.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.10. Tarifa de Liquidação Antecipada: ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("tarifaAntecipada% (ExtensoTarifaAntecipada por cento);");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.11. Data de Emissão: ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("dataDeEmissao;");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.12. Data de Vencimento: ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("vencimentoUltimaParcelaPagamento;");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("2.13. Praça de Pagamento: ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("São Paulo/SP.");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();		
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("3.	DAS GARANTIAS");
			run.setBold(true);
			run.addCarriageReturn();
			
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
			run2.setText("Em garantia do fiel, integral e pontual "
					+ "cumprimento de todas as obrigações assumidas na presente CCB,"
					+ " o EMITENTE aliena fiduciariamente ao CREDOR o(s) bem(ens)"
					+ " imóvel(eis), de sua propriedade, bem(ns) com a(s) seguinte(s) "
					+ "descrição(ões):");
			run2.setBold(false);
			
			int iImagem = 0;
			for(UploadedFile imagem :  filesList) {
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
			run4.setText(", objeto da matrícula nº numeroImovel "
					+ "(“Bem Imóvel” ou “Imóvel”), registrada perante o "
					+ "cartorioImovel Cartório de Registro de Imóveis da "
					+ "Comarca de cidadeImovel – ufImovel  (“RGI”), nos termos"
					+ " e condições anuídos pelas Partes no Instrumento Particular "
					+ "de Alienação Fiduciária Bem Imóvel (“Termo de Garantia”), o "
					+ "qual faz parte desta CCB como parte acessória e inseparável.");
			run4.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("3.2. ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Se solteiro(a), viúvo(a), divorciado(a) ou separado(a) "
					+ "judicialmente, declara, sob responsabilidade civil e criminal, "
					+ "que o imóvel aqui objetivado não foi adquirido na constância de "
					+ "união estável prevista na Lei nº 9.278, de 10/05/96 e no Código Civil, "
					+ "razão pela qual é seu único e exclusivo proprietário.");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();		
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("3.3.	Seguros:");
			run.setBold(true);
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("3.3.1. ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("O CREDOR Fica autorizado neste ato a contratar em nome do(s) EMITENTE, os seguros para "
					+ "cobertura dos riscos de morte e invalidez permanente e de danos físicos ao(s) Imóvel(is) descrito(s) "
					+ "na cláusula 3 acima, cujos prêmios deverão ser pagos mensalmente. O CREDOR, ou quem vier a substituí-lo, "
					+ "será nomeado beneficiário das respectivas apólices/certificados de seguro, e receberá o capital segurado"
					+ " ou indenização em caso de sinistro para utilização dos valores daí decorrentes na liquidação total"
					+ " ou parcial das obrigações de pagamento oriundas do presente instrumento. O valor do prêmio dos"
					+ " referidos seguros será reajustado conforme definido em apólice e poderá ser revisto e alterado"
					+ " desde o início da contratação, ou seja, na elaboração da proposta de empréstimo ou financiamento,"
					+ " até a liquidação integral da CCB, de acordo com as regras estabelecidas na respectiva"
					+ " apólice de seguros que são estipuladas pela companhia seguradora. ");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setSpacingBetween(1);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("3.3.1.1. ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Assim, declara-se ciente o EMITENTE que qualquer alteração"
					+ " nas condições inicialmente informadas para a contratação,"
					+ " tais como, mas não se limitando, por exemplo, a(s) idade(s)"
					+ " do(s) proponente(s), poderá refletir em modificação no prêmio"
					+ " dos seguros a serem contratados para a devida formalização deste"
					+ " empréstimo com garantia imobiliária.");
			run2.setBold(false);
			run2.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.LEFT);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("3.3.1.2. ");
			run.setBold(true);
			run2 = paragraph.createRun();
			run2.setFontSize(12);
			run2.setText("Declara ainda o EMITENTE e o(s) TERCEIROS(S) GARANTIDOR(ES) que:");
			run2.setBold(false);
			
			CTNumbering cTNumbering = CTNumbering.Factory.parse(cTAbstractNumBulletXML);
			CTAbstractNum cTAbstractNum = cTNumbering.getAbstractNumArray(0);
			
		//	CTAbstractNum cTAbstractNum = getAbstractNumber(STNumberFormat.LOWER_LETTER);
			XWPFAbstractNum abstractNum = new XWPFAbstractNum(cTAbstractNum);
			XWPFNumbering numbering = document.createNumbering();
			BigInteger abstractNumID = numbering.addAbstractNum(abstractNum);
			BigInteger numID = numbering.addNum(abstractNumID);
			
			paragraph = document.createParagraph();	
			paragraph.setNumID(numID);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("tem(têm) ciência e concorda(m) integralmente com os termos das condições gerais "
					+ "ora apresentadas com relação ao Seguro de pessoa com cobertura de Morte e "
					+ "Invalidez Permanente por Acidente (MIP) e ao Seguro de danos com cobertura de "
					+ "Danos Físicos ao Imóvel (DFI), tendo pleno conhecimento de todas as suas "
					+ "coberturas e riscos excluídos ");
			run.setBold(false);
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("os próprios EMITENTE ou seus beneficiários, herdeiros ou sucessores, deverão "
					+ "comunicar ao CREDOR e a Seguradora, imediatamente e por escrito, a ocorrência "
					+ "de qualquer sinistro, bem como, qualquer evento suscetível de agravar "
					+ "consideravelmente o risco coberto, sob pena de perder o direito à indenização se "
					+ "for provado que silenciou de má-fé;");
			run.setBold(false);
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();		
			paragraph.setNumID(numID);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setAlignment(ParagraphAlignment.BOTH);	
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("autoriza(m), desde já, de forma expressa, irrevogável e inequívoca, que a "
					+ "Seguradora realize o levantamento de informações médicas em hospitais, clínicas "
					+ "e/ou consultórios, bem como, que solicite a realização de perícia médica quando	necessária.");
			run.setBold(false);
			run.addCarriageReturn();

			
			geraParagrafoComposto(document, paragraph, run, run2, "3.3.2.     ", 
					 "Se, em decorrência de sinistro, "
					 + "a Seguradora por qualquer motivo "
					 + "desembolsar indenização em valor "
					 + "insuficiente a quitação do saldo"
					 + " devedor do empréstimo objeto deste "
					 + "instrumento, ficará(ão) o EMITENTE ou seu(s)"
					 + " herdeiro(s) e/ou sucessor(es) obrigado(s) a efetiva"
					 + " liquidação do saldo devedor remanescente perante o CREDOR.", 
					 true,  false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "3.3.3.      ", 
					 "Na hipótese da cláusula acima, no caso de não liquidação do"
					 + " saldo remanescente pelos DEVEDOR(ES), seus herdeiros e"
					 + " sucessores a qualquer título, sobre estes incidirá os encargos"
					 + " moratórios previstos na cláusula 6, bem como a respectiva "
					 + "execução da garantia pelo CREDOR ou quem vier a substituí-lo.", 
					 true,  false);
			
			fazParagrafoSimples(document, paragraph, run, "4. DA CONCESSÃO DO CRÉDITO",  true);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.1.     ", 
					 "O EMITENTE pagará por esta CCB ao CREDOR ou a quem este "
					 + "vier a indicar, em moeda corrente nacional, o Valor do "
					 + "Crédito acrescido de encargos, conforme expressamente "
					 + "indicado na cláusula 2 acima, calculados desde a data da "
					 + "emissão desta CCB pelo EMITENTE até a data do seu respectivo "
					 + "pagamento integral ao CREDOR, acrescidos, quando aplicáveis,"
					 + " dos encargos moratórios, conforme disposto na presente CCB; ", 
					 true,  false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.2.   ", 
					 "O EMITENTE tem expresso conhecimento de que os juros"
					 + " ajustados para o empréstimo a que se refere à presente"
					 + " CCB são calculados, sempre e invariavelmente, de forma"
					 + " diária e capitalizada, conforme permitido pela legislação"
					 + " aplicável; ", 
					 true,  false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.3.      ", 
					 "O EMITENTE declara que tomou conhecimento do cálculo do CET"
					 + " indicado no item 2.4 acima, previamente à operação de "
					 + "empréstimo contratada por meio da presente CCB, através "
					 + "de planilha de cálculo que lhe foi apresentada pelo CREDOR; ", 
					 true,  false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.4.    ", 
					 "O EMITENTE concorda que a Liberação do Crédito "
					 + "prevista na cláusula 2.5 está condicionada ao cumprimento"
					 + " das seguintes condições precedentes, de forma cumulativa"
					 + " e satisfatória para o CREDOR:", 
					 true,  false);
			
			
			CTNumbering cTNumbering2 = CTNumbering.Factory.parse(cTAbstractNumBulletXML_NoLeft);
			CTAbstractNum cTAbstractNum2 = cTNumbering2.getAbstractNumArray(0);
			XWPFAbstractNum abstractNum2 = new XWPFAbstractNum(cTAbstractNum2);
			XWPFNumbering numbering2 = document.createNumbering();
			BigInteger abstractNumID2 = numbering2.addAbstractNum(abstractNum2);
			BigInteger numID2 = numbering2.addNum(abstractNumID2);

			paragraph = document.createParagraph();
			paragraph.setNumID(numID2);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Entrega de todas as vias da CCB e Instrumento Particular"
					+ " de Alienação Fiduciária de Bem(ns) Imóvel(eis) em Garantia e "
					+ "Outras Avenças, devidamente assinadas pelas Partes com todas as "
					+ "firmas reconhecidas ou mediante assinatura eletrônica compatível"
					+ " com os padrões do ICP-BRASIL;");
			run.setBold(false);
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID2);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Entrega do protocolo do registro da alienação"
					+ " fiduciária em favor do CREDOR na matrícula do"
					+ " imóvel descrito na cláusula 3 dessa CCB.");
			run.setBold(false);
			run.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "4.5.   ", 
					 "As Partes anuem que, caso as condições precedentes acima"
					 + " não sejam cumpridas no prazo de até 30 (trinta) dias "
					 + "corridos contados da emissão da CCB, o referido título"
					 + " poderá será considerado cancelado deixando de surtir efeitos,"
					 + " obrigações, direitos e deveres às Partes. ", 
					 true,  false);
			
			fazParagrafoSimples(document, paragraph, run, "5. DA FORMA DE PAGAMENTO E PRAZO",  true);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.1.	Depósito em Conta Corrente: ", 
					 "Fica o EMITENTE instruído pelo CREDOR, em caráter irrevogável e irretratável,"
					 + " a depositar em conta corrente nos termos da cláusula 2.6 acima,"
					 + " de titularidade do CREDOR ou a quem este vier a indicar (“Conta Corrente”),"
					 + " os valores relativos às parcelas da CCB indicadas no ANEXO I, "
					 + "acrescidas dos respectivos encargos, inclusive debitar os valores"
					 + " correspondentes a mora, IOF, tarifas e demais despesas aqui previstas.", 
					 true,  false);
			
			
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
			
			run3 = paragraph.createRun();
			run3.setFontSize(12);
			run3.setText("não estão vinculados à data de liberação do Valor Líquido do Crédito");
			run3.setBold(true);	
			
			run4 = paragraph.createRun();
			run4.setFontSize(12);
			run4.removeCarriageReturn();
			run4.setText(", devendo tais encargos serem pagos a partir da data ajustada"
					+ " no item ");
			run4.addCarriageReturn();
			
			
			XWPFRun run5 = paragraph.createRun();
			run5.setFontSize(12);
			run5.removeCarriageReturn();
			run5.setText("2.7");
			run5.addCarriageReturn();
			
			XWPFRun run6 = paragraph.createRun();
			run6.setFontSize(12);
			run6.removeCarriageReturn();
			run6.setText(", sob pena de incidência de atualização monetária, juros e multa, de acordo com o quanto disposto na cláusula 6.");
			run6.addCarriageReturn();
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.3.	", 
					 "Na hipótese de haver parcelas mensais vencidas e não pagas na "
					 + "data de liberação do Valor Líquido do Crédito, o(s) DEVEDOR(ES),"
					 + " desde já, autoriza(m) o CREDOR a descontar desse valor,"
					 + " descrito na cláusula 2.1.4, eventual montante devido em"
					 + " razão do não pagamento das parcelas mensais ajustadas"
					 + " conforme ANEXO I, incluindo encargos moratórios conforme"
					 + " previsto na Cláusula 6 dessa CCB.", 
					 true,  false);
			
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
					 true,  false);
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.5.	", 
					 "Em razão do acordado nesta cédula quanto ao valor, prestações,"
					 + " parcelas, reajustes e atualizações, o pagamento de qualquer"
					 + " prestação atualizada de maneira diversa da estabelecida nesta CCB,"
					 + " inclusive perante terceiros autorizados a recebê-las,"
					 + " não implicará na quitação do respectivo débito ou "
					 + "repactuação da dívida.", 
					 true,  false);
			
			
			geraParagrafoComposto(document, paragraph, run, run2, "5.6.", 
					 "Qualquer diferença verificada entre os créditos"
					 + " efetivados na conta corrente do CREDOR e a sistemática"
					 + " de cálculos dos valores estabelecidos nesta CCB,"
					 + " deverá ser imediatamente liquidada pelo EMITENTE no "
					 + "prazo máximo de 48 (quarenta e oito) horas, contadas"
					 + " do aviso que o CREDOR lhe dirigir neste sentido,"
					 + " caso em que, não realizado o pagamento após esse "
					 + "prazo, estará em mora.", 
					 true,  false);
			
			fazParagrafoSimples(document, paragraph, run, "6. DO ATRASO NO PAGAMENTO E ENCARGOS MORATÓRIOS", true);
			
			geraParagrafoComposto(document, paragraph, run, run2, "6.1.    ", 
					 "Na hipótese de inadimplemento ou mora, o EMITENTE estará "
					 + "obrigado a pagar ao CREDOR ou a quem este indicar, cumulativamente,"
					 + " além da quantia correspondente à dívida em aberto, os seguintes "
					 + "encargos: ", 
					 true,  false);
			
			
			 cTNumbering2 = CTNumbering.Factory.parse(cTAbstractNumBulletXML_NoLeft_NoHanging_bold);
			 cTAbstractNum2 = cTNumbering2.getAbstractNumArray(0);
			 abstractNum2 = new XWPFAbstractNum(cTAbstractNum2);
			 numbering2 = document.createNumbering();
			 abstractNumID2 = numbering2.addAbstractNum(abstractNum2);
			 numID2 = numbering2.addNum(abstractNumID2);

			paragraph = document.createParagraph();
			paragraph.setNumID(numID2);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Juros remuneratórios nos mesmos percentuais das taxas "
					+ "contratadas nessa CCB, calculados a partir do vencimento "
					+ "da(s) parcela(s) em aberto até a data do efetivo pagamento;");
			run.setBold(false);
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID2);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Juros de mora à razão de 1% a.m. (um por cento ao mês), "
					+ "calculados a partir do vencimento da(s) parcela(s) em aberto"
					+ " até a data do efetivo pagamento;");
			run.setBold(false);
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID2);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Multa contratual, de natureza não compensatória, de 2% (dois por cento)"
					+ " incidente sobre o montante atualizado (juros remuneratórios e juros de mora)"
					+ " total do débito apurado e não pago;");
			run.setBold(false);
			run.addCarriageReturn();
			
			paragraph = document.createParagraph();
			paragraph.setNumID(numID2);
			paragraph.setSpacingBetween(1);
			paragraph.setSpacingBefore(0);
			paragraph.setSpacingAfter(0);
			paragraph.setAlignment(ParagraphAlignment.BOTH);
			run = paragraph.createRun();
			run.setFontSize(12);
			run.setText("Na hipótese do CREDOR vir a ser compelido a recorrer"
					+ " a meios administrativos ou judiciais para receber o seu crédito,"
					+ " as despesas de cobrança, estas limitadas a 20% (vinte por cento)"
					+ " sobre o valor do saldo devedor e, havendo procedimento judicial, "
					+ "custas processuais e honorários advocatícios, estes fixados judicialmente.");
			run.setBold(false);
			run.addCarriageReturn();
			
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
					 true,  false);
			
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
					+ "em decorrências do empréstimo a ele concedido por força da "
					+ "presente CCB;",
					false);
			
			 geraParagrafoBulletList(document, paragraph, run, numID2, "Se ocorrer inadimplemento "
			 		+ "de qualquer obrigação assumida pelo EMITENTE, e/ou quaisquer sociedades"
			 		+ " direta ou indiretamente ligadas, coligadas, controladoras ou controladas"
			 		+ " pelo EMITENTE (doravante denominadas “AFILIADAS”), inclusive no exterior,"
			 		+ " de suas obrigações decorrentes de outros contratos, empréstimos ou descontos"
			 		+ " celebrados com o CREDOR e/ou quaisquer sociedades, direta ou indiretamente,"
			 		+ " ligadas, coligadas, controladoras ou controladas pelo credor da CCB ou seu "
			 		+ "cessionário, e/ou com terceiros, e/ou rescisão ou declaração de vencimento "
			 		+ "antecipado dos respectivos documentos, por culpa do EMITENTE e/ou de quaisquer "
			 		+ "AFILIADAS;", false);
	
			 geraParagrafoBulletList(document, paragraph, run, numID2, "Se for protestado qualquer "
			 		+ "título de responsabilidade do EMITENTE em razão do inadimplemento de obrigação "
			 		+ "cujo valor individual ou em conjunto seja igual ou superior a R$ 100.000,00 "
			 		+ "(cem mil reais), sem que a justificativa para tal medida tenha sido apresentada"
			 		+ " ao credor da CCB, no prazo que lhe tiver sido solicitada ou, sendo ou tendo sido"
			 		+ " apresentada a justificativa, se esta não for considerada satisfatória pelo CREDOR,"
			 		+ " ressalvado o protesto tirado por erro ou má-fé do respectivo portador;", false);

			 geraParagrafoBulletList(document, paragraph, run, numID2, "Se O EMITENTE for inscrito no "
			 		+ "Cadastro de Emitentes de Cheques sem Fundo – CCF, ou, ainda, constem informações "
			 		+ "negativas a seu respeito no Sistema de Informações de Crédito do Banco Central,"
			 		+ " que, a critério do credor da CCB, possa afetar a sua capacidade de cumprir as "
			 		+ "obrigações assumidas na presente CCB ou no Termo de Garantia;", false);
			
			 geraParagrafoBulletList(document, paragraph, run, numID2, "Se o EMITENTE e/ou quaisquer AFILIADAS,"
			 		+ " inclusive no exterior, tornarem-se insolventes, requerer(em) ou tiver(em), falência, "
			 		+ "insolvência civil, recuperação judicial ou extrajudicial requerida ou decretada, sofrer "
			 		+ "intervenções, regime de administração especial temporária, ou liquidação judicial ou"
			 		+ " extrajudicial;", false);
			 
			 geraParagrafoBulletList(document, paragraph, run, numID2, "Se for comprovada a falsidade de qualquer"
			 		+ " declaração, informação ou documento que houver sido, respectivamente, firmada, prestada ou"
			 		+ " entregue pelo EMITENTE, ao CREDOR;", false);
			 
			 geraParagrafoBulletList(document, paragraph, run, numID2, "Se o EMITENTE sofrer qualquer (quaisquer) "
			 		+ "medida(s) judicial(ais) ou extrajudicial(ais) que por qualquer forma, possa(m) afetar "
			 		+ "negativamente os créditos do empréstimo e/ou as garantias conferidas ao credor da CCB;", false);
			 
			 geraParagrafoBulletList(document, paragraph, run, numID2, "Se as garantias fidejussórias, "
			 		+ "ora e/ou que venham a ser eventualmente convencionadas, por qualquer fato atinente"
			 		+ " ao seu objeto ou prestador se tornar inábeis, impróprias, ou insuficientes para "
			 		+ "assegurar o pagamento da dívida, e desde que não sejam substituídas, ou complementadas,"
			 		+ " quando solicitada por escrito pelo CREDOR ou a quem este vier a indicar;", false);
			 
			 geraParagrafoBulletList(document, paragraph, run, numID2, "Se, sem o expresso consentimento "
			 		+ "do credor da CCB ocorrer a transferência a terceiros dos direitos e obrigações do"
			 		+ " EMITENTE previstos nesta CCB e no Termo de Garantia;", false);
			 
			 geraParagrafoBulletList(document, paragraph, run, numID2, "Se, sem o expresso consentimento do "
			 		+ "credor da CCB ocorrer alienação, cessão, doação ou transferência, por qualquer meio, "
			 		+ "de bens, ativos ou direitos de propriedade do EMITENTE e/ou de quaisquer AFILIADAS, "
			 		+ "quando aplicável que, no entendimento do credor, possam levar ao descumprimento das "
			 		+ "obrigações previstas na presente CCB;", false);
			 
			 geraParagrafoBulletList(document, paragraph, run, numID2, "Se, sem o expresso consentimento do"
			 		+ " credor da CCB, o EMITENTE, quando aplicável, tiver total ou parcialmente, o seu"
			 		+ " controle acionário, direto ou indireto, cedido, transferido ou por qualquer outra"
			 		+ " forma alienado ou modificado;", false);
			 
			 geraParagrafoBulletList(document, paragraph, run, numID2, "Se ocorrer mudança ou alteração do"
			 		+ " objeto social do EMITENTE, quando aplicável, de forma a alterar as atividades"
			 		+ " principais ou a agregar às suas atividades novos negócios que possam representar "
			 		+ "desvios em relação às atividades atualmente desenvolvidas;", false);
			 
			 geraParagrafoBulletList(document, paragraph, run, numID2, "Se, sem o expresso consentimento do"
			 		+ " credor da CCB, o EMITENTE sofrer, durante a vigência desta CCB, qualquer operação"
			 		+ " de transformação, incorporação, fusão ou cisão;", false);
			 
			 geraParagrafoBulletList(document, paragraph, run, numID2, "Se a garantia real objeto do"
			 		+ " Instrumento Particular de Alienação Fiduciária de Bem Imóvel não for efetivamente"
			 		+ " registrada junto ao RGI no prazo de até 30(trinta) dias corridos a contar da"
			 		+ " emissão desta CCB; e", false);
			 
			 geraParagrafoBulletList(document, paragraph, run, numID2, "Se o Bem Imóvel objeto"
			 		+ " da garantia à presente CCB apresentar quaisquer características, ônus "
			 		+ "ou gravame ou caso ocorra qualquer ato ou omissão por parte do EMITENTE,"
			 		+ " que impeça a efetiva constituição da garantia regulada nos termos Instrumento"
			 		+ " Particular de Alienação Fiduciária de Bem Imóvel.", false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "7.2. ",  "No caso de falta de pagamento"
			 		+ " de qualquer parcela(s) na(s) data(s) de seu(s) respectivo(s) vencimento(s),"
			 		+ " o CREDOR poderá, por mera liberdade e sem que tal situação caracterize novação"
			 		+ " ou alteração das condições estabelecidas nesta CCB – optar pela cobrança somente"
			 		+ " da(s) parcela(s) devida(s) em aberto, comprometendo-se o EMITENTE,"
			 		+ " em contrapartida, a liquidá-la(s) imediatamente quando instado(s) para tal,"
			 		+ " sob pena de ultimar-se o vencimento antecipado de toda a dívida; ", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "7.2.1. ",  "Declarado o vencimento "
			 		+ "antecipado de toda a dívida, o credor da CCB apresentará ao EMITENTE notificação "
			 		+ "contendo o saldo devedor final, incluindo principal, juros, encargos, despesas e "
			 		+ "tributos, a ser pago pelo EMITENTE no dia útil imediatamente subsequente ao "
			 		+ "recebimento de referida notificação, sob pena de ser considerado em mora, "
			 		+ "independentemente de qualquer aviso ou notificação judicial ou extrajudicial;", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "7.2.2. ",  "Na declaração de vencimento "
			 		+ "antecipado da dívida pelo credor da CCB, além do valor apurado nos termos do item 7.2.1 acima,"
			 		+ " serão acrescidos os encargos previstos na cláusula 6 às parcelas vencidas. ", true,  false);
			 
			 fazParagrafoSimples(document, paragraph, run, "8. LIQUIDAÇÃO ANTECIPADA", true);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "8.1. ",  "O EMITENTE poderá liquidar"
			 		+ " antecipadamente, total ou parcialmente, suas obrigações decorrentes desta CCB, "
			 		+ "desde que previamente acordado, de modo satisfatório ao credor da CCB e ao EMITENTE,"
			 		+ " as condições de tal liquidação antecipada. Para tanto, o EMITENTE deverá encaminhar"
			 		+ " ao credor da CCB, solicitação por escrito, com antecedência mínima de 10 (dez) dias úteis;", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "8.1.1. ",  "Se indicada a Tarifa de "
			 		+ "Liquidação Antecipada no item 2.10 acima, o EMITENTE, desde já, se obriga a pagar "
			 		+ "ao CREDOR, na data da liquidação, a Tarifa de Liquidação Antecipada sobre o valor"
			 		+ " efetivamente pago antecipadamente, a título de indenização pelos custos relacionados"
			 		+ " com a quebra de captação de recursos;", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "8.1.2. ",  "Nas situações em que as despesas "
			 		+ "associadas à contratação realizada por meio desta CCB forem também objeto de financiamento "
			 		+ "ou empréstimo, essas despesas integrarão igualmente a operação para apuração do valor "
			 		+ "presente para fins de amortização, total ou parcial, da dívida ainda em aberto;", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "8.1.3. ",  "Caso haja saldo devedor a ser"
			 		+ " pago acrescentar-se-ão, às prestações em atraso, e as penalidades previstas neste instrumento,"
			 		+ " bem como os juros remuneratórios calculados pro rata die e quaisquer outras despesas de "
			 		+ "responsabilidade do EMITENTE nos termos desta CCB;", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "8.1.4. ",  "Sempre que for necessário,"
			 		+ " a apuração do saldo devedor do EMITENTE será realizada pelo CREDOR mediante planilha "
			 		+ "de cálculo, que constituirá documento integrante e inseparável da presente CCB. ", true,  false);
			 
			 fazParagrafoSimples(document, paragraph, run, "9.	DECLARAÇÕES", true);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "9.1. ",  "As Partes signatárias, cada uma por si, declaram e garantem que: ", true,  false);
			 
			 cTNumbering = CTNumbering.Factory.parse(cTAbstractNumBulletXML_NoHanging_bold);
			 cTAbstractNum = cTNumbering.getAbstractNumArray(0);
			 abstractNum = new XWPFAbstractNum(cTAbstractNum);
			 numbering = document.createNumbering();
			 abstractNumID = numbering.addAbstractNum(abstractNum);
			 numID = numbering.addNum(abstractNumID);
			 
			 geraParagrafoBulletListComposta( document,  paragraph,  run,  run2,  "Possui plena capacidade e "
			 		+ "legitimidade para celebrar a presente CCB, realizar todas as operações e cumprir todas "
			 		+ "as obrigações aqui assumidas", 
						 ", bem como dos instrumentos de garantia, tendo tomado todas as medidas"
						 + " de natureza societária e outras eventualmente necessárias para autorizar "
						 + "a sua celebração, implementação e cumprimento de todas as obrigações "
						 + "constituídas;",  true,  false,  numID, UnderlinePatterns.DASH);
			 
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
			 
			 geraParagrafoBulletList(document, paragraph, run, numID, "Nenhuma Parte depende economicamente da outra;", false);
			 
			 geraParagrafoBulletList(document, paragraph, run, numID, "Nenhuma das Partes se encontra em estado de"
			 		+ " necessidade ou sob coação para celebrar esta CCB e/ou quaisquer contratos e compromissos a "
			 		+ "ela relacionados e acessórios ", false);
			 
			 geraParagrafoBulletList(document, paragraph, run, numID, "As discussões sobre o objeto contratual, "
			 		+ "crédito, encargos incidentes e obrigações acessórias, oriundos desta CCB e dos instrumentos"
			 		+ " de garantia, foram feitas, conduzidas e implementadas por livre iniciativa das Partes;", false);
			 
			 geraParagrafoBulletList(document, paragraph, run, numID, "O CREDOR e EMITENTE, são pessoas devidamente estruturadas,"
			 		+ " qualificadas e capacitadas para entender a estrutura financeira e jurídica objeto desta CCB, e estão "
			 		+ "acostumadas a celebrar, em seus respectivos campos de atuação, títulos e instrumentos de garantia semelhantes"
			 		+ " aos previstos nesta CCB, não havendo entre as Partes qualquer relação de hipossuficiência ou ainda natureza de"
			 		+ " consumo na relação aqui tratada.", false);
			 
			 fazParagrafoSimples(document, paragraph, run, "10.	DAS DISPOSIÇÕES FINAIS", true);

			 geraParagrafoComposto(document, paragraph, run, run2, "10.1. Tolerância:",  "A tolerância não implica perdão, renúncia, novação ou alteração da dívida ou das condições aqui previstas e o pagamento do principal, mesmo sem ressalvas, não será considerado ou presumido a quitação dos encargos. Dessa forma, as Partes acordam que qualquer prática diversa da aqui pactuada, mesmo que reiterada, não poderá ser interpretada como novação;", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.1.1 Declarações Específicas: ",  "O EMITENTE declara que: "
			 		+ "(i) está ciente que o surto do novo coronavírus (COVID-19), reconhecido oficialmente como pandemia pela "
			 		+ "Organização Mundial de Saúde (OMS), é anterior à celebração desta CCB e que a pandemia não apresenta "
			 		+ "caráter de imprevisibilidade, extraordinariedade ou superveniência no presente momento, (ii) reconhece "
			 		+ "que tais eventos não configuram caso fortuito ou de força maior, conforme definição do artigo 393 do Código"
			 		+ " Civil, e (iii) compromete-se a honrar as obrigações assumidas nos termos desta CCB; ", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.2. Comunicação aos Serviços de Proteção ao Crédito: ", 
					 "Na hipótese de ocorrer descumprimento de qualquer obrigação ou atraso no pagamento, o CREDOR ou a quem este"
					 + " vier a indicar poderá comunicar o fato a qualquer serviço de proteção ao crédito, como Serasa Experian ou"
					 + " qualquer outro órgão encarregado de cadastrar atraso nos pagamentos e o descumprimento de obrigações "
					 + "contratuais, informando o nome do EMITENTE.", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.3. Reforço de Garantias: ",  "O CREDOR poderá, "
			 		+ "a qualquer tempo, exigir reforço de garantias, ficando estipulado o prazo de 5 (cinco) dias úteis "
			 		+ "contados da data de sua solicitação, pelo CREDOR, por carta sob protocolo ou registro postal, para "
			 		+ "que o EMITENTE providencie o respectivo reforço, sob pena do imediato vencimento da presente CCB, "
			 		+ "independentemente de interpelação judicial ou notificação judicial ou extrajudicial;", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.4. Alteração da CCB: ",  "A presente CCB somente poderá "
			 		+ "ser alterada mediante aditivo próprio devidamente assinado pelas Partes; ", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.5. Comunicação ao Sistema de Informação de Créditos "
			 		+ "(“SCR”): ",  "O CREDOR, neste ato, comunica ao EMITENTE que a presente operação de empréstimo, será "
			 				+ "registrada no SCR gerido pelo Banco Central do Brasil (“BACEN”), que tem por finalidade subsidiar"
			 				+ " o BACEN para fins de supervisão de risco de crédito a que estão expostas as instituições"
			 				+ " financeiras e ainda intercambiar informações entre as instituições financeiras; ", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.5.1 ",  "O EMITENTE poderá ter acesso aos dados "
			 		+ "constantes em seu SCR, por meio de central de atendimento ao público do BACEN;", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.5.2 ",  "Em caso de discordância quanto às informações"
			 		+ " do SCR, bem como pedidos de correções, o EMITENTE deverá entrar em contato com a Ouvidoria do CREDOR,"
			 		+ " nos termos do item 10.11 abaixo;", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.5.3 ",  "O EMITENTE autoriza o CREDOR ou a quem este "
			 		+ "indicar, a qualquer tempo: a: (i) efetuar consultas ao Sistema de Informações de Crédito – SCR – do Banco"
			 		+ " Central do Brasil (“SCR”), nos termos da Resolução nº 3.658, do Conselho Monetário Nacional, de 17.12.2008,"
			 		+ " conforme alterada e os serviços de proteção ao crédito SPC, Serasa e outras em que o CREDOR seja "
			 		+ "cadastrado; (ii) fornecer ao Banco Central do Brasil informações sobre esta CCB, para integrar o SCR; "
			 		+ "e (iii) proceder conforme disposições que advierem de novas exigências feitas pelo Banco Central do Brasil"
			 		+ " ou autoridades. ", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.6. Efeitos do CCB: ",  "As Partes convencionam que as "
			 		+ "obrigações pecuniárias estipuladas na presente CCB passam a vigorar a partir de sua respectiva emissão;", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.7. ",  "Se qualquer item ou cláusula desta CCB "
			 		+ "vier a ser considerado ilegal, inexequível ou, por qualquer motivo, ineficaz, todos os demais itens"
			 		+ " e cláusulas continuarão em vigor, plenamente válidos e eficazes. As Partes, desde já, se comprometem"
			 		+ " a negociar, no menor prazo possível, item ou cláusula que, conforme o caso, venha a substituir o item"
			 		+ " ou cláusula ilegal, inexequível ou ineficaz. Nessa negociação, deverá ser considerado o objetivo das "
			 		+ "Partes na data de assinatura dessa CCB, bem como o contexto no qual o item ou cláusula ilegal, inexequível"
			 		+ " ou ineficaz foi inserido.", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.8. Irrevogabilidade e Irretratabilidade: ",
					 "A presente CCB é firmada em caráter irrevogável e irretratável, obrigando as Partes, seus "
					 + "herdeiros e/ou sucessores; ", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.9. Base de Dados: ",  "O EMITENTE declara e concorda "
			 		+ "expressamente que ao firmar a presente CCB passará a fazer parte integrante da base de clientes do CREDOR,"
			 		+ " ou a quem este vier a indicar, autorizando, assim através das informações cadastrais que o CREDOR, ou a "
			 		+ "quem este vier a indicar, possui a respeito dele o oferecimento de produtos e/ou serviços;", true,  false);
			 
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.10. Ouvidoria: ",  "O EMITENTE declara ter ciência de que o "
			 		+ "CREDOR disponibiliza um canal de Ouvidoria para que sejam feitas sugestões e/ou reclamações através do telefone"
			 		+ " (11) 3810-9333;", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.11. Legislação: ",  "Aplica-se a presente CCB, as"
			 		+ " disposições da Lei 10.931, de 02 de agosto de 2004, e posteriores alterações (“Lei 10.931”), declarando"
			 		+ " o EMITENTE ter conhecimento que a presente CCB é um título executivo extrajudicial e representa dívida "
			 		+ "em dinheiro, certa, líquida e exigível, seja pela soma nela indicada, seja pelo saldo devedor "
			 		+ "demonstrado em planilha de cálculo ou nos extratos de Conta Corrente, a serem emitidos consoante "
			 		+ "o que preceitua a aludida Lei 10.931;", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.11.1  ",  "O EMITENTE declara ter ciência que: (i)"
			 		+ " o CREDOR integra o Sistema Financeiro Nacional, submetendo-se à disciplina e regras pelo Conselho"
			 		+ " Monetário Nacional e Banco Central do Brasil; e (ii) as taxas de juros cobradas nas operações "
			 		+ "financeiras realizadas pelo CREDOR, incluindo a presente CCB, não estão submetidas ao limite de"
			 		+ " 12% (doze por cento) ao ano, como já decidiu o Supremo Tribunal Federal, sendo legítima a "
			 		+ "cobrança de juros e encargos superiores a esse percentual;", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.11.2	 ",  "A tolerância, por uma das partes, "
			 		+ "quanto a alguma demora, atraso ou omissão da outra parte no cumprimento das obrigações ajustadas"
			 		+ " neste instrumento, ou a não aplicação, na ocasião oportuna, das penalidades previstas "
			 		+ "será considerada mera liberalidade, não se configurando como precedente ou novação "
			 		+ "contratual", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.11.3 ",  "Se vier a tornar impossível"
			 		+ " a aplicação das regras previstas nesta Cédula, seja por força de eventual caráter cogente"
			 		+ " de imperativos legais que venham a ser baixados, seja em decorrência de ausência de consenso"
			 		+ " entre as Partes, considerar-se-á rescindida esta CCB e, em consequência, a dívida dela"
			 		+ " oriunda se considerará antecipadamente vencida, da mesma forma e com os mesmos efeitos "
			 		+ "previstos, efetivando-se a cobrança de juros “pro-rata temporis”; ", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.12. Cessão ou Endosso: ",  "O CREDOR fica "
			 		+ "expressamente autorizado a qualquer tempo, a seu exclusivo critério e independentemente da "
			 		+ "prévia anuência do EMITENTE, a ceder a terceiros os direitos de crédito que detém em razão desta CCB,"
			 		+ " bem como a transferi-la a terceiros mediante endosso da “via negociável”, sendo certo que "
			 		+ "a cessão ou o endosso não caracterizarão violação do sigilo bancário em relação ao EMITENTE."
			 		+ " Ocorrendo a cessão ou o endosso, o cessionário/endossatário desta CCB assumirá automaticamente"
			 		+ " a qualidade de credor desta CCB, passando a ser titular de todos os direitos e obrigações dela "
			 		+ "decorrentes; ", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.12.1	 ",  "Após o endosso pelo CREDOR desta CCB,"
			 		+ " o EMITENTE desde já, reconhece a validade da emissão e do endosso desta CCB de forma física ou eletrônica, "
			 		+ "o que é feito com base no art. 889, §3º, do Código Civil. ", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.12.2	",  "Na hipótese de transferência da presente CCB,"
			 		+ " o seu novo titular ficará automaticamente sub-rogado em todos os direitos e garantias que "
			 		+ "cabiam ao CREDOR original, independentemente de qualquer formalidade, passando a ter acesso "
			 		+ "livre e direto a todas as informações relacionadas à operação bancária e respectivas garantias,"
			 		+ " a exemplo de duplicatas e/ou direitos creditórios e/ou quaisquer outras garantias eventualmente "
			 		+ "constituídas, reconhecendo o EMITENTE que o novo titular da CCB possui o inequívoco direito de "
			 		+ "acompanhar detidamente todo o andamento da operação bancária, motivo pelo qual, da mesma forma,"
			 		+ " estará automaticamente sub-rogado a consultar as informações consolidadas em seu nome, no SCR, "
			 		+ "SERASA – Centralização de Serviços os Bancos S.A. e quaisquer  outros órgãos, entidades ou empresas,"
			 		+ " julgados pertinentes pelo CREDOR, permanecendo válida a presente autorização durante todo o tempo"
			 		+ " em que subsistir em aberto e não liquidadas as obrigações decorrentes da presente CCB. ", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.12.3	 ",  "A cessão dos direitos sempre compreenderá"
			 		+ " os acessórios, títulos, instrumentos que os representam e anexos. De tal forma, ao formalizar a cessão "
			 		+ "dos direitos de crédito, por meio de Contrato de Cessão, o CREDOR estará cedendo, automaticamente,"
			 		+ " todos os direitos, privilégios, preferências, prerrogativas, garantias e ações, legal e contratualmente"
			 		+ " previstas, que sejam inerentes ao direito de crédito cedido, inclusive: (i) o direito de receber "
			 		+ "integralmente o seu valor, acrescido dos juros, das multas, da atualização monetária e/ou demais encargos"
			 		+ " remuneratórios e/ou moratórios; (ii) o direito de ação e o de protesto em face do respectivo EMITENTE,"
			 		+ " para exigir o cumprimento da obrigação de pagamento, ou visando resguardar qualquer direito; (iii)"
			 		+ " as garantias eventualmente existentes, sejam reais ou pessoais; e (iv) o direito de declarar o direito "
			 		+ "de crédito vencido antecipadamente, nas hipóteses contratadas com o EMITENTE e naquelas previstas na"
			 		+ " legislação aplicável;", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.12.4 ",  "O EMITENTE, está integralmente ciente(s)"
			 		+ " e de acordo com o seguinte: (i) qualquer litígio ou questionamento, judicial ou extrajudicial, que possa "
			 		+ "vir a ser ajuizado, deverá ser ajuizado, àquele portador endossatário da CCB na data do ajuizamento do "
			 		+ "litígio ou questionamento; e (ii) o ajuizamento de qualquer ação, judicial ou extrajudicial, pelo EMITENTE,"
			 		+ " contra o CREDOR, após o mesmo ter endossado esta CCB para terceiro, o EMITENTE, estará sujeito ao "
			 		+ "pagamento de indenização por perdas e danos, e ressarcimento de todo e quaisquer custos e despesas"
			 		+ " que o CREDOR venha a incorrer (incluindo honorários advocatícios) para defesa de seus direitos no "
			 		+ "respectivo litígio;", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.13. Emissão de Certificados de CCB: ",  "O CREDOR, "
			 		+ "ou a quem este vier a indicar, poderá emitir certificados de CCB com lastro no presente título, podendo"
			 		+ " negociá-los livremente no mercado;", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.13.1 ",  "Caso haja a emissão do certificado referido "
			 		+ "no item 10.13, a presente CCB ficará custodiada em instituição financeira autorizada, a qual passará a "
			 		+ "proceder às cobranças dos valores devidos, junto ao EMITENTE;", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.13.2 ",  "O EMITENTE desde já se declara de acordo"
			 		+ " com a emissão do certificado referido no item 10.13, obrigando-se a atender às solicitações da instituição"
			 		+ " custodiante, bem como, aceitam a cessão de crédito, independentemente de qualquer aviso "
			 		+ "ou formalidade;", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.14.	Foro: ",  "Ajustam as Partes que será sempre"
			 		+ " competente para conhecer e dirimir qualquer questão oriunda ou decorrente da presente CCB, o foro"
			 		+ " da comarca de São Paulo capital com a exclusão de qualquer outro, por mais privilegiado que seja,"
			 		+ " reservando-se o credor da CCB o direito de optar, a seu exclusivo critério, pelo foro da sede"
			 		+ " do EMITENTE ou, ainda, pelo foro da situação dos bens dados em garantia;", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.15. ",  "Em caso de contratação eletrônica, "
			 		+ "as Partes ratificam que admitem como válido, para fins de comprovação de autoria e integridade,"
			 		+ " a assinatura e informações constantes no presente documento, as quais foram capturadas de forma"
			 		+ " eletrônica e utilizadas nesta Cédula, constituindo título executivo extrajudicial nos termos "
			 		+ "do artigo 28 da Lei nº 10.931 2004 e para todos os fins de direito, ainda que seja estabelecida "
			 		+ "com assinatura eletrônica ou certificação fora dos padrões ICP-BRASIL, conforme disposto pelo art."
			 		+ " 10 da Medida Provisória nº 2.200/2001.", true,  false);
			 
			 geraParagrafoComposto(document, paragraph, run, run2, "10.16. ",  "A presente CCB é emitida e firmada "
			 		+ "em 2 (duas) vias, constando na 1ª via a expressão “Via Negociável” e nas demais, a expressão "
			 		+ "“Via Não Negociável”. ", true,  false);

			 fazParagrafoSimples(document, paragraph, run, "São Paulo, SP, emissaoDia de emissaoMes de emissaoAno.", false);
			 
			 	paragraph = document.createParagraph();		
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				paragraph.setAlignment(ParagraphAlignment.CENTER);
				paragraph.setSpacingBetween(1);
				run = paragraph.createRun();
				run.setFontSize(12);
				run.setText("(O final desta página foi intencionalmente deixado em branco)");
				run.setBold(false);
				
				paragraph = document.createParagraph();		
				paragraph.setPageBreak(true);
				
				 paragraph = document.createParagraph();		
				paragraph.setSpacingBefore(0);
				paragraph.setSpacingAfter(0);
				paragraph.setSpacingBetween(1);
				paragraph.setAlignment(ParagraphAlignment.CENTER);
				run = paragraph.createRun();
				run.setFontSize(12);
				run.setText("(Segue a página de assinaturas)");
					run.setBold(false);
					run.addCarriageReturn();
					
					paragraph = document.createParagraph();		
					paragraph.setPageBreak(true);
			 
					paragraph = document.createParagraph();		
					paragraph.setSpacingBefore(0);
					paragraph.setSpacingAfter(0);
					paragraph.setSpacingBetween(1);
					paragraph.setAlignment(ParagraphAlignment.BOTH);
					run = paragraph.createRun();
					run.setFontSize(12);
					run.setText("(Página de assinaturas da Cédula de Crédito Bancário nº XXXXXX, emitida por nomeEmitente, CPF/MF nº cpfEmitente, em favor de BMP MONEY PLUS SOCIEDADE DE CRÉDITO DIRETO S.A., CNPJ/ MF sob nº 34.337.707/0001-00, em dataDeEmissao).");
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
					run2.setText("BMP MONEY PLUS SOCIEDADE DE CRÉDITO DIRETO S.A");
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
					run2.setText("NOME EMITENTE");
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
					
					if (listaParticipantes.size() > 1) {
						tableRow2.getCell(0).setParagraph(paragraph);
						tableRow2.getCell(1).setParagraph(paragraph);
						int QtdePessoasEsquerdo = 0;
						for (int iPartTab = 0; iPartTab < listaParticipantes.size(); iPartTab++) {
							
							CcbVO participante = this.listaParticipantes.get(iPartTab);
							if (iPartTab != 0) {
								if (iPartTab % 2 != 0) {
									
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
									
									QtdePessoasEsquerdo++;
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
						for(int i = 0; i < QtdePessoasEsquerdo; i++) {
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
					

						
				      //create third row
				     XWPFTableRow tableRow3 = table.createRow();
				     tableRow3.getCell(0).setParagraph(paragraph);
						run = tableRow3.getCell(0).getParagraphArray(0).createRun();
						run.setFontSize(12);
						run.setText("Nome:  nomeTestemunha1");
						run.setBold(false);
						
						tableRow3.getCell(1).setParagraph(paragraph);
						run = tableRow3.getCell(1).getParagraphArray(0).createRun();
						run.setFontSize(12);
						run.setText("Nome:  nomeTestemunha2");
						run.setBold(false);

						
						XWPFTableRow tableRow4 = table.createRow();
						tableRow4.getCell(0).setParagraph(paragraph);
							run = tableRow4.getCell(0).getParagraphArray(0).createRun();
							run.setFontSize(12);
							run.setText("RG:  rgTestemunha1");
							run.setBold(false);
							
							tableRow4.getCell(1).setParagraph(paragraph);
							run = tableRow4.getCell(1).getParagraphArray(0).createRun();
							run.setFontSize(12);
							run.setText("RG:  rgTestemunha2");
							run.setBold(false);

					
							XWPFTableRow tableRow5 = table.createRow();
							tableRow5.getCell(0).setParagraph(paragraph);
								run = tableRow5.getCell(0).getParagraphArray(0).createRun();
								run.setFontSize(12);
								run.setText("CPF:  cpfTestemunha1");
								run.setBold(false);
								
								tableRow5.getCell(1).setParagraph(paragraph);
								run = tableRow5.getCell(1).getParagraphArray(0).createRun();
								run.setFontSize(12);
								run.setText("CPF:  cpfTestemunha2");
								run.setBold(false);
								
								CTTblPr tblpro = table.getCTTbl().getTblPr();

								CTTblBorders borders = tblpro.addNewTblBorders();
								borders.addNewBottom().setVal(STBorder.NONE); 
								borders.addNewLeft().setVal(STBorder.NONE);
								borders.addNewRight().setVal(STBorder.NONE);
								borders.addNewTop().setVal(STBorder.NONE);
								//also inner borders
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
									run.setText("CÉDULA DE CRÉDITO BANCÁRIO Nº XXXXXX");
									run.addCarriageReturn();
									run.setText("PLANILHA DE CÁLCULO");
										run.setBold(false);
										
										
						
			/*for (XWPFParagraph p : document.getParagraphs()) {
			    List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {  	
			    	for (XWPFRun r : runs) {
			            String text = r.getText(0);
			            adicionarEnter(text, r);
			    	}
			    }
			}*/

			
			
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
	
	@SuppressWarnings("resource")
	public StreamedContent readXWPFile() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		
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
										"\nIII – TERCEIRO GARANTIDOR: nomeEmpresaTerceiroG dadosEmpresaTerceiroG nomeTerceiroG, filhoTerceiroG de maeTerceiroG e paiTerceiroG, nacionalidadeTerceiroG, profissaoTerceiroG, estadoCivilTerceiroG regimeCasamentoTerceiroG nomeConjugeTerceiroG cpfConjugeTerceiroG, portador(a) da Cédula de Identidade RG nº numeroRgTerceiroG SSP/ufTerceiroG, inscrito(a) no CPF/MF sob o nº cpfTerceiroG, endereço eletrônico: emailTerceiroG, residente e domiciliado à logradouroTerceiroG, nº numeroTerceiroG,  complementoTerceiroG, cidadeTerceiroG/ufTerceiroG, CEP cepTerceiroG; \n");
							} else {
								text = trocaValoresXWPF(text, r, "criaTerceiroG", "");
							}
							if (this.addInterveniente == true) {
								text = trocaValoresXWPF(text, r, "criaInterveniente",
										"\nIX – INTERVENIENTE ANUENTE: nomeEmpresaInterveniente dadosEmpresaInterveniente nomeInterveniente, nacionalidadeInterveniente, filhoInterveniente de maeInterveniente e paiInterveniente, profissaoInterveniente, estadoCivilInterveniente regimeCasamentoInterveniente nomeConjugeInterveniente cpfConjugeInterveniente, portador(a) da Cédula de Identidade RG nº numeroRgInterveniente SSP/ufInterveniente, inscrito(a) no CPF/MF sob o nº cpfInterveniente, endereço eletrônico: emailInterveniente, residente e domiciliado à logradouroInterveniente, nº numeroInterveniente,  complementoInterveniente, cidadeInterveniente/ufInterveniente, CEP cepInterveniente; \n");
							} else {
								text = trocaValoresXWPF(text, r, "criaInterveniente", "");
							}
							if (this.addAvalista == true) {
								text = trocaValoresXWPF(text, r, "criaAvalista",
										"\nIX – AVALISTA: nomeEmpresaAvalista dadosEmpresaAvalista nomeAvalista, filhoAvalista de maeAvalista e paiAvalista, nacionalidadeAvalista, profissaoAvalista, estadoCivilAvalista regimeCasamentoAvalista nomeConjugeAvalista cpfConjugeAvalista,portador(a) da Cédula de Identidade RG nº numeroRgAvalista SSP/ufAvalista, inscrito(a) no CPF/MF sob o nº cpfAvalista, endereço eletrônico: emailAvalista, residente e domiciliado à logradouroAvalista, nº numeroAvalista,  complementoAvalista, cidadeAvalista/ufAvalista, CEP cepAvalista; \n");
							} else {
								text = trocaValoresXWPF(text, r, "criaAvalista", "");
							}		
						} else if(CommonsUtil.mesmoValor(tipoDownload,"AF")) {
							if (this.addTerceiro == true) {
								text = trocaValoresXWPF(text, r, "criaTerceiroG",
										"nomeEmpresaTerceiroG dadosEmpresaTerceiroG nomeTerceiroG, filhoTerceiroG de maeTerceiroG e paiTerceiroG, nacionalidadeTerceiroG, profissaoTerceiroG, estadoCivilTerceiroG regimeCasamentoTerceiroG nomeConjugeTerceiroG cpfConjugeTerceiroG, portador(a) da Cédula de Identidade RG nº numeroRgTerceiroG SSP/ufTerceiroG, inscrito(a) no CPF/MF sob o nº cpfTerceiroG, endereço eletrônico: emailTerceiroG, residente e domiciliado à logradouroTerceiroG, nº numeroTerceiroG,  complementoTerceiroG, cidadeTerceiroG/ufTerceiroG, CEP cepTerceiroG;");
							} else {
								text = trocaValoresXWPF(text, r, "criaTerceiroG", "");
							}
							if(CommonsUtil.mesmoValor(fiduciante, true)) {
								text = trocaValoresXWPF(text, r, "criaFiduciante", "nomeEmpresaEmitente dadosEmpresaEmitente nomeEmitente, filhoEmitente de maeEmitente e paiEmitente, nacionalidadeEmitente, profissaoEmitente, estadoCivilEmitente regimeCasamentoEmitente nomeConjugeEmitente cpfConjugeEmitente, portador(a) da Cédula de Identidade RG nº numeroRgEmitente SSP/ufEmitente, inscrito(a) no CPF/MF sob o nº cpfEmitente, endereço eletrônico: emailEmitente, residente e domiciliado à logradouroEmitente, nº numeroEmitente, complementoEmitente, cidadeEmitente/ufEmitente, CEP cepEmitente;");
								text = trocaValoresXWPF(text, r, "criaDevedor", "");
								
								text = trocaValoresXWPF(text, r, "fiducianteEmitente", "\n"
										+ "nomeEmitente \n"
										+ "logradouroEmitente, nº numeroEmitente,  cidadeEmitente - ufEmitente \n"
										+ "CEP cepEmitente \n"
										+ "E-mail: emailEmitente \n");
								
								text = trocaValoresXWPF(text, r, "devedorEmitente","");
								
							} else {
								text = trocaValoresXWPF(text, r, "criaDevedor", "\n X) DEVEDOR: nomeEmpresaEmitente dadosEmpresaEmitente nomeEmitente, filhoEmitente de maeEmitente e paiEmitente, nacionalidadeEmitente, profissaoEmitente, estadoCivilEmitente regimeCasamentoEmitente nomeConjugeEmitente cpfConjugeEmitente, portador(a) da Cédula de Identidade RG nº numeroRgEmitente SSP/ufEmitente, inscrito(a) no CPF/MF sob o nº cpfEmitente, endereço eletrônico: emailEmitente, residente e domiciliado à logradouroEmitente, nº numeroEmitente, complementoEmitente, cidadeEmitente/ufEmitente, CEP cepEmitente;");
								text = trocaValoresXWPF(text, r, "criaFiduciante", "");
								text = trocaValoresXWPF(text, r, "fiducianteEmitente", "");
								text = trocaValoresXWPF(text, r, "devedorEmitente", "\n \n"
										+ "Pelo DEVEDOR: \n" 
										+ "nomeEmitente \n"
										+ "logradouroEmitente, nº numeroEmitente,  cidadeEmitente - ufEmitente - \n"
										+ "CEP cepEmitente \n"
										+ "E-mail: emailEmitente \n");
							}
							
							
							if (this.addInterveniente == true) {
								text = trocaValoresXWPF(text, r, "criaInterveniente",
										"\nX) – INTERVENIENTE ANUENTE: nomeEmpresaInterveniente dadosEmpresaInterveniente nomeInterveniente, nacionalidadeInterveniente, filhoInterveniente de maeInterveniente e paiInterveniente, profissaoInterveniente, estadoCivilInterveniente regimeCasamentoInterveniente nomeConjugeInterveniente cpfConjugeInterveniente, portador(a) da Cédula de Identidade RG nº numeroRgInterveniente SSP/ufInterveniente, inscrito(a) no CPF/MF sob o nº cpfInterveniente, endereço eletrônico: emailInterveniente, residente e domiciliado à logradouroInterveniente, nº numeroInterveniente,  complementoInterveniente, cidadeInterveniente/ufInterveniente, CEP cepInterveniente; \n");
							} else {
								text = trocaValoresXWPF(text, r, "criaInterveniente", "");
							}
							if (this.addAvalista == true) {
								text = trocaValoresXWPF(text, r, "criaAvalista",
										" nomeEmpresaAvalista dadosEmpresaAvalista nomeAvalista, filhoAvalista de maeAvalista e paiAvalista, nacionalidadeAvalista, profissaoAvalista, estadoCivilAvalista regimeCasamentoAvalista nomeConjugeAvalista cpfConjugeAvalista,portador(a) da Cédula de Identidade RG nº numeroRgAvalista SSP/ufAvalista, inscrito(a) no CPF/MF sob o nº cpfAvalista, endereço eletrônico: emailAvalista, residente e domiciliado à logradouroAvalista, nº numeroAvalista,  complementoAvalista, cidadeAvalista/ufAvalista, CEP cepAvalista;");
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
								text = trocaValoresXWPF(text, r, "socioEmitente", "pela sua única sócia");
							} else {
								text = trocaValoresXWPF(text, r, "socioEmitente", "pelo seu único sócio");
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

									+ "E-mail: emailInterveniente \n");
							
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
									text = trocaValoresXWPF(text, r, "socioInterveniente", "pela sua única sócia");
								} else {
									text = trocaValoresXWPF(text, r, "socioInterveniente", "pelo seu único sócio");
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
									+ "nomeAvalista	 \n"

									+ "logradouroAvalista, nº numeroAvalista,  cidadeAvalista - ufAvalista - \n"

									+ "CEP cepAvalista \n"

									+ "E-mail: emailAvalista \n");
							
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
									text = trocaValoresXWPF(text, r, "socioAvalista", "pela sua única sócia");
								} else {
									text = trocaValoresXWPF(text, r, "socioAvalista", "pelo seu único sócio");
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

									+ "E-mail: emailTerceiroG \n");
							
							
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
									text = trocaValoresXWPF(text, r, "socioTerceiroG", "pela sua única sócia");
								} else {
									text = trocaValoresXWPF(text, r, "socioTerceiroG", "pelo seu único sócio");
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
					

						if (text != null && text.contains("xtenso" + "PorcentagemImovel")) {
							if(CommonsUtil.mesmoValor(this.porcentagemImovel,BigDecimal.ZERO)) {
								text = text.replace("xtenso" + "PorcentagemImovel", "Zero");
							} else {
								porcentagemPorExtenso.setNumber(this.porcentagemImovel);
								text = text.replace("xtenso" + "PorcentagemImovel", porcentagemPorExtenso.toString());
							}
							r.setText(text, 0);
						}
						
						
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
							int iImagem = 0;
							for(UploadedFile imagem :  filesList) {
								r.addBreak();
								this.populateFiles(iImagem);
								r.addPicture(this.getBis(), fileTypeInt, fileName.toLowerCase(), Units.toEMU(400), Units.toEMU(300));
								r.addBreak();	
								iImagem++;
							}
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
			
	    } catch (Exception e) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Contrato de Cobrança: Ocorreu um problema ao gerar o documento!  " + e,
							""));
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
		this.listaParticipantes = new ArrayList<>();
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
	    
	    clearPagadorRecebedor();
		
		return "/Atendimento/Cobranca/Ccb.xhtml";
	}

	public void loadLovs() {
		PagadorRecebedorDao pagadorRecebedorDao = new PagadorRecebedorDao();
		this.listPagadores = pagadorRecebedorDao.findAll();
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
	    for(Map.Entry<String, Integer> entry : roman_numerals.entrySet()){
	      int matches = Int/entry.getValue();
	      res += repeat(entry.getKey(), matches);
	      Int = Int % entry.getValue();
	    }
	    return res;
	  }
	  public static String repeat(String s, int n) {
	    if(s == null) {
	        return null;
	    }
	    final StringBuilder sb = new StringBuilder();
	    for(int i = 0; i < n; i++) {
	        sb.append(s);
	    }
	    return sb.toString();
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

	public ArrayList<CcbVO> getListaParticipantes() {
		return listaParticipantes;
	}

	public void setListaParticipantes(ArrayList<CcbVO> listaParticipantes) {
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
