package com.webnowbr.siscoat.cobranca.mb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.IntegerConverter;
import javax.xml.crypto.Data;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.hamcrest.core.Is;
import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.auxiliar.BigDecimalConverter;
import com.webnowbr.siscoat.cobranca.auxiliar.NumeroPorExtenso;
import com.webnowbr.siscoat.cobranca.auxiliar.PorcentagemPorExtenso;
import com.webnowbr.siscoat.cobranca.auxiliar.ValorPorExtenso;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;

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
	private boolean femininoEmitente = false;

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
	
	private String emailInterveniente;
	private String regimeCasamentoInterveniente;
	private boolean femininoInterveniente = false;

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
	
	private String emailTerceiroG;
	private String regimeCasamentoTerceiroG;
	private boolean femininoTerceiroG = false;
	
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
	
	private String emailAvalista;
	private String regimeCasamentoAvalista;
	private boolean femininoAvalista = false;
	
	private String nomeConjugeAvalista;
	private String cpfConjugeAvalista;

//--------------------------------------------------------------------

	private PagadorRecebedor selectedPagadorGenerico;
	private PagadorRecebedor selectedPagador;
	private PagadorRecebedor emitenteSelecionado;
	private PagadorRecebedor intervenienteSelecionado;
	private PagadorRecebedor terceiroGSelecionado;
	private PagadorRecebedor avalistaSelecionado;
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
	
	ValorPorExtenso valorPorExtenso = new ValorPorExtenso();
	NumeroPorExtenso numeroPorExtenso = new NumeroPorExtenso();
	PorcentagemPorExtenso porcentagemPorExtenso = new PorcentagemPorExtenso();

	String updatePagadorRecebedor = ":form";

	public void pesquisaEmitente() {
		this.tipoPesquisa = "Emitente";
		this.updatePagadorRecebedor = ":form:emitentePanel";
		this.emitenteSelecionado = new PagadorRecebedor();
	}

	public void pesquisaInterveniente() {
		this.tipoPesquisa = "Interveniente";
		this.updatePagadorRecebedor = ":form:intervenientePanel";
		this.intervenienteSelecionado = new PagadorRecebedor();
		this.emitenteSelecionado = new PagadorRecebedor();
	}
	
	public void pesquisaTerceiroG() {
		this.tipoPesquisa = "TerceiroG";
		this.updatePagadorRecebedor = ":form:terceiroPanel";
		this.terceiroGSelecionado = new PagadorRecebedor();
		this.emitenteSelecionado = new PagadorRecebedor();
	}
	
	public void pesquisaAvalista() {
		this.tipoPesquisa = "Avalista";
		this.updatePagadorRecebedor = ":form:avalistaPanel";
		this.avalistaSelecionado = new PagadorRecebedor();
		this.emitenteSelecionado = new PagadorRecebedor();
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
			if (this.avalistaSelecionado.getNomeConjuge() != null) {
				this.setNomeConjugeAvalista(this.avalistaSelecionado.getNomeConjuge());
				this.setCpfConjugeAvalista(this.avalistaSelecionado.getCpfConjuge());
			} else {
				this.setNomeConjugeAvalista(null);
				this.setCpfConjugeAvalista(null);
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
	    	} else {
	    		return null;
	    	}

	    	
			ByteArrayOutputStream  out = new ByteArrayOutputStream ();
			
			/*text = trocaValoresXWPF(text, r, "FiducianteConjugue","Rua logradouroConjugeEmitente, nº numeroConjugeEmitente, Qd. XX - Lote XX, Cond. Residencial XXXXXX, Bairro - cidadeConjugeEmitente - ufConjugeEmitente -  \r"
									+ "CEP cepConjugeEmitente \r"
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
							if (this.addInterveniente == true) {
								text = trocaValoresXWPF(text, r, "criaInterveniente",
										"\r IX – INTERVENIENTE ANUENTE: nomeInterveniente, nacionalidadeInterveniente, profissaoInterveniente, estadoCivilInterveniente regimeCasamentoInterveniente nomeConjugeInterveniente, cpfConjugeInterveniente,portador(a) da Cédula de Identidade RG nº numeroRgInterveniente SSP/ufInterveniente, inscrito(a) no CPF/MF sob o nº cpfInterveniente, residente e domiciliado à logradouroInterveniente, nº numeroInterveniente,  complementoInterveniente, cidadeInterveniente/ufInterveniente, CEP cepInterveniente; \r");
							} else {
								text = trocaValoresXWPF(text, r, "criaInterveniente", "");
							}
							if (this.addTerceiro == true) {
								text = trocaValoresXWPF(text, r, "criaTerceiroG",
										"\r IX – TERCEIRO GARANTIDOR: nomeTerceiroG, nacionalidadeTerceiroG, profissaoTerceiroG, estadoCivilTerceiroG regimeCasamentoTerceiroG nomeConjugeTerceiroG, cpfConjugeTerceiroG,portador(a) da Cédula de Identidade RG nº numeroRgTerceiroG SSP/ufTerceiroG, inscrito(a) no CPF/MF sob o nº cpfTerceiroG, residente e domiciliado à logradouroTerceiroG, nº numeroTerceiroG,  complementoTerceiroG, cidadeTerceiroG/ufTerceiroG, CEP cepTerceiroG; \r");
							} else {
								text = trocaValoresXWPF(text, r, "criaTerceiroG", "");
							}
							if (this.addAvalista == true) {
								text = trocaValoresXWPF(text, r, "criaAvalista",
										"\r IX – AVALISTA: nomeAvalista, nacionalidadeAvalista, profissaoAvalista, estadoCivilAvalista regimeCasamentoAvalista nomeConjugeAvalista, cpfConjugeAvalista,portador(a) da Cédula de Identidade RG nº numeroRgAvalista SSP/ufAvalista, inscrito(a) no CPF/MF sob o nº cpfAvalista, residente e domiciliado à logradouroAvalista, nº numeroAvalista,  complementoAvalista, cidadeAvalista/ufAvalista, CEP cepAvalista; \r");
							} else {
								text = trocaValoresXWPF(text, r, "criaAvalista", "");
							}
						} else if(CommonsUtil.mesmoValor(tipoDownload,"AF")) {
							if (this.addTerceiro == true) {
								text = trocaValoresXWPF(text, r, "criaTerceiroG",
										" nomeTerceiroG, nacionalidadeTerceiroG, profissaoTerceiroG, estadoCivilTerceiroG regimeCasamentoTerceiroG nomeConjugeTerceiroG cpfConjugeTerceiroG, portador(a) da Cédula de Identidade RG nº numeroRgTerceiroG SSP/ufTerceiroG, inscrito(a) no CPF/MF sob o nº cpfTerceiroG, endereço eletônico: emailTerceiroG, residente e domiciliado à logradouroTerceiroG, nº numeroTerceiroG,  complementoTerceiroG, cidadeTerceiroG/ufTerceiroG, CEP cepTerceiroG;");
							} else {
								text = trocaValoresXWPF(text, r, "criaTerceiroG", "");
							}
							if (this.addInterveniente == true) {
								text = trocaValoresXWPF(text, r, "criaInterveniente",
										"\r IX – INTERVENIENTE ANUENTE: nomeInterveniente, nacionalidadeInterveniente, profissaoInterveniente, estadoCivilInterveniente regimeCasamentoInterveniente nomeConjugeInterveniente, cpfConjugeInterveniente,portador(a) da Cédula de Identidade RG nº numeroRgInterveniente SSP/ufInterveniente, inscrito(a) no CPF/MF sob o nº cpfInterveniente, endereço eletônico: emailInterveniente, residente e domiciliado à logradouroInterveniente, nº numeroInterveniente,  complementoInterveniente, cidadeInterveniente/ufInterveniente, CEP cepInterveniente; \r");
							} else {
								text = trocaValoresXWPF(text, r, "criaInterveniente", "");
							}
							if (this.addAvalista == true) {
								text = trocaValoresXWPF(text, r, "criaAvalista",
										"\r IX – AVALISTA: nomeAvalista, nacionalidadeAvalista, profissaoAvalista, estadoCivilAvalista regimeCasamentoAvalista nomeConjugeAvalista, cpfConjugeAvalista,portador(a) da Cédula de Identidade RG nº numeroRgAvalista SSP/ufAvalista, inscrito(a) no CPF/MF sob o nº cpfAvalista, endereço eletônico: emailAvalista, residente e domiciliado à logradouroAvalista, nº numeroAvalista,  complementoAvalista, cidadeAvalista/ufAvalista, CEP cepAvalista; \r");
							} else {
								text = trocaValoresXWPF(text, r, "criaAvalista", "");
							}
							if(CommonsUtil.mesmoValor(fiduciante, true)) {
								text = trocaValoresXWPF(text, r, "criaFiduciante", "nomeEmitente, nacionalidadeEmitente, profissaoEmitente, estadoCivilEmitente regimeCasamentoEmitente nomeConjugeEmitente, cpfConjugeEmitente, portador(a) da Cédula de Identidade RG nº numeroRgEmitente SSP/ufEmitente, inscrito(a) no CPF/MF sob o nº cpfEmitente, endereço eletônico: emailEmitente, residente e domiciliado à logradouroEmitente, nº numeroEmitente, complementoEmitente, cidadeEmitente/ufEmitente, CEP cepEmitente;");
								text = trocaValoresXWPF(text, r, "criaDevedor", "");
								text = trocaValoresXWPF(text, r, "fiducianteEmitente", "nomeEmitente \r"
										+ "Rua logradouroEmitente, nº numeroEmitente,  cidadeEmitente - ufEmitente - \r"
										+ "CEP cepEmitente \r" 
										+ "Email: emailEmitente");
								text = trocaValoresXWPF(text, r, "devedorEmitente","");
								
							} else {
								text = trocaValoresXWPF(text, r, "criaDevedor", "DEVEDOR: nomeEmitente, nacionalidadeEmitente, profissaoEmitente, estadoCivilEmitente regimeCasamentoEmitente nomeConjugeEmitente, cpfConjugeEmitente, portador(a) da Cédula de Identidade RG nº numeroRgEmitente SSP/ufEmitente, inscrito(a) no CPF/MF sob o nº cpfEmitente, endereço eletônico: emailEmitente, residente e domiciliado à logradouroEmitente, nº numeroEmitente, complementoEmitente, cidadeEmitente/ufEmitente, CEP cepEmitente;");
								text = trocaValoresXWPF(text, r, "criaFiduciante", "");
								text = trocaValoresXWPF(text, r, "fiducianteEmitente", "");
								text = trocaValoresXWPF(text, r, "devedorEmitente", "Pelo DEVEDOR: \r" + "nomeEmitente \r"
										+ "Rua logradouroEmitente, nº numeroEmitente,  cidadeEmitente - ufEmitente - \r"
										+ "CEP cepEmitente \r"
										+ "Email: emailEmitente");
							}
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
						text = trocaValoresXWPF(text, r, "nacionalidadeEmitente",this.nacionalidadeEmitente);
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
						
						if (this.addInterveniente == true) {
							text = trocaValoresXWPF(text, r, "fiducianteInterveniente","nomeInterveniente	 \r"
									+ "Rua logradouroInterveniente, nº numeroInterveniente,  cidadeInterveniente - ufInterveniente - \r"
									+ "CEP cepInterveniente \r"
									+ "Email: emailInterveniente ");
							
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
							text = trocaValoresXWPF(text, r, "estadoCivilInterveniente", verificaEstadoCivil(this.femininoInterveniente, this.estadoCivilInterveniente).toLowerCase());
							text = trocaValoresXWPF(text, r, "nomeInterveniente", this.nomeInterveniente);
							text = trocaValoresXWPF(text, r, "cpfInterveniente", this.cpfInterveniente);
							text = trocaValoresXWPF(text, r, "nacionalidadeInterveniente", this.nacionalidadeInterveniente);
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
						} else {
							text = trocaValoresXWPF(text, r, "fiducianteInterveniente","");
						}
						if (this.addAvalista == true) {
							text = trocaValoresXWPF(text, r, "fiducianteAvalista","nomeAvalista	 \r"
									+ "Rua logradouroAvalista, nº numeroAvalista,  cidadeAvalista - ufAvalista - \r"
									+ "CEP cepAvalista \r"
									+ "Email: emailAvalista ");
							
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
							text = trocaValoresXWPF(text, r, "estadoCivilAvalista",
									verificaEstadoCivil(this.femininoAvalista, this.estadoCivilAvalista).toLowerCase());
							text = trocaValoresXWPF(text, r, "nomeAvalista", this.nomeAvalista);
							text = trocaValoresXWPF(text, r, "cpfAvalista", this.cpfAvalista);
							text = trocaValoresXWPF(text, r, "nacionalidadeAvalista", this.nacionalidadeAvalista);
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
						} else {
							text = trocaValoresXWPF(text, r, "fiducianteAvalista","");
						}
						
						if (this.addTerceiro == true) {
							text = trocaValoresXWPF(text, r, "fiducianteTerceiroG","nomeTerceiroG	 \r"
									+ "Rua logradouroTerceiroG, nº numeroTerceiroG,  cidadeTerceiroG - ufTerceiroG - \r"
									+ "CEP cepTerceiroG \r"
									+ "Email: emailTerceiroG");
							
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
							text = trocaValoresXWPF(text, r, "estadoCivilTerceiroG",
									verificaEstadoCivil(this.femininoTerceiroG, this.estadoCivilTerceiroG)
											.toLowerCase());
							text = trocaValoresXWPF(text, r, "nomeTerceiroG", this.nomeTerceiroG);
							text = trocaValoresXWPF(text, r, "cpfTerceiroG", this.cpfTerceiroG);
							text = trocaValoresXWPF(text, r, "nacionalidadeTerceiroG", this.nacionalidadeTerceiroG);
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

			document.write(out); 
			document.close();
			final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(
					FacesContext.getCurrentInstance());
			
			if (CommonsUtil.mesmoValor(tipoDownload,"CCB")) {
				gerador.open(String.format("Galleria Bank - Modelo_CCB %s.docx", ""));
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"AF")) {
	    		gerador.open(String.format("Galleria Bank - Modelo_AF %s.docx", ""));
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

	public void clearPagadorRecebedor() {

		this.emitenteSelecionado = new PagadorRecebedor();
		this.selectedPagador = new PagadorRecebedor();
	}

	public void calculaValorLiquidoCredito() {
		if (this.valorCredito != null && custoEmissao != null && this.valorIOF != null && this.valorDespesas != null)
			this.setValorLiquidoCredito(
					((this.getValorCredito().subtract(this.getCustoEmissao())).subtract(this.getValorIOF()))
							.subtract(this.getValorDespesas()));
	}
	
	public void calculaPorcentagemImovel() {
		if (this.valorCredito != null && vendaLeilao != null) {
				this.setPorcentagemImovel((this.vendaLeilao.divide(this.valorCredito)).multiply(BigDecimal.valueOf(100)));
		}

	}

	public String clearFieldsEmitirCcb() {
		loadLovs();
		this.intervenienteSelecionado = new PagadorRecebedor();
		this.emitenteSelecionado = new PagadorRecebedor();
		this.selectedPagador = new PagadorRecebedor();
		
		this.setNomeEmitente(null);
		this.setProfissaoEmitente(null);
		this.setEstadoCivilEmitente(null);
		this.setNumeroRgEmitente(null);
		this.setUfEmitente(null);
		this.setCpfEmitente(null);
		this.setLogradouroEmitente(null);
		this.setNumeroEmitente(null);
		this.setComplementoEmitente(null);
		this.setCidadeEmitente(null);
		this.setCepEmitente(null);
		this.setEmailEmitente(null);
		if (this.getNomeConjugeEmitente() != null) {
			this.setNomeConjugeEmitente(null);
			this.setCpfConjugeEmitente(null);
		}
		this.setNomeInterveniente(null);
		this.setProfissaoInterveniente(null);
		this.setEstadoCivilInterveniente(null);
		this.setNumeroRgInterveniente(null);
		this.setUfInterveniente(null);
		this.setCpfInterveniente(null);
		this.setLogradouroInterveniente(null);
		this.setNumeroInterveniente(null);
		this.setComplementoInterveniente(null);
		this.setCidadeInterveniente(null);
		this.setCepInterveniente(null);
		if (this.getNomeConjugeInterveniente() != null) {
			this.setNomeConjugeInterveniente(null);	
			this.setCpfConjugeInterveniente(null);
		}
		 valorLiquidoCredito = null;
		 valorCredito = null;
		 custoEmissao = null;
		 valorIOF = null;
		 valorDespesas = null;	
		 taxaDeJurosMes = null;
		 taxaDeJurosAno = null;
		 cetMes = null;
		 cetAno = null;
		 contaCorrente = null;
		 agencia = null;
		 numeroBanco = null;
		 nomeBanco = null;
		 numeroParcelasPagamento = null;
		 vencimentoPrimeiraParcelaPagamento = null;
		 vencimentoUltimaParcelaPagamento = null;
		 montantePagamento = null;
		 numeroParcelasDFI = null;
		 vencimentoPrimeiraParcelaDFI = null;
		 vencimentoUltimaParcelaDFI = null;
		 montanteDFI = null;
		 numeroParcelasMIP = null;
		 vencimentoPrimeiraParcelaMIP = null;
		 vencimentoUltimaParcelaMIP = null;
		 montanteMIP = null;
		 tarifaAntecipada = null;
		 dataDeEmissao = null;
		 numeroImovel = null;
		 cartorioImovel = null;
		 cidadeImovel = null;
		 ufImovel = null;
		 
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
	
	
}
