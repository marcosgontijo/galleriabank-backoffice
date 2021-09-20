package com.webnowbr.siscoat.cobranca.mb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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


	private String nomeConjugeEmitente;
	private String nacionalidadeConjugeEmitente;
	private String profissaoConjugeEmitente;
	private String numeroRgConjugeEmitente;
	private String ufConjugeEmitente;
	private String cpfConjugeEmitente;
	private String logradouroConjugeEmitente;
	private String numeroConjugeEmitente;
	private String complementoConjugeEmitente;
	private String cidadeConjugeEmitente;
	private String cepConjugeEmitente;
	private String emailConjugeEmitente;


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

	private String nomeConjugeInterveniente;
	private String nacionalidadeConjugeInterveniente;
	private String profissaoConjugeInterveniente;
	private String numeroRgConjugeInterveniente;
	private String ufConjugeInterveniente;
	private String cpfConjugeInterveniente;
	private String logradouroConjugeInterveniente;
	private String numeroConjugeInterveniente;
	private String complementoConjugeInterveniente;
	private String cidadeConjugeInterveniente;
	private String cepConjugeInterveniente;

//--------------------------------------------------------------------

	private PagadorRecebedor selectedPagadorGenerico;
	private PagadorRecebedor selectedPagador;
	private PagadorRecebedor emitenteSelecionado;
	private PagadorRecebedor intervenienteSelecionado;
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

	private String tipoPesquisa;
	private String tipoDownload;
	
	ValorPorExtenso valorPorExtenso = new ValorPorExtenso();
	NumeroPorExtenso numeroPorExtenso = new NumeroPorExtenso();


	String updatePagadorRecebedor = ":form";
	public void populateEndereço() {
		if (this.tipoPesquisa == "Emitente") {
			if(this.getCidadeEmitente() == null) {
				this.setCidadeEmitente(getCidadeConjugeEmitente());
			}
			if(this.getUfEmitente() == null) {
				this.setUfEmitente(this.getUfConjugeEmitente());
			}
			if(this.getLogradouroEmitente() == null) {
				this.setLogradouroEmitente(this.getLogradouroConjugeEmitente());
			}
			if(this.getNumeroEmitente() == null) {
				this.setNumeroEmitente(this.getNumeroConjugeEmitente());
			}
			if(this.getComplementoEmitente() == null) {
				this.setComplementoEmitente(this.getComplementoConjugeEmitente());
			}
			if(this.getCepEmitente() == null) {
				this.setCepEmitente(this.getCepConjugeEmitente());
			}
			if(this.getCidadeConjugeEmitente() == null) {
				this.setCidadeEmitente(getCidadeEmitente());
			}
			if(this.getUfConjugeEmitente() == null) {
				this.setUfConjugeEmitente(this.getUfEmitente());
			}
			if(this.getLogradouroConjugeEmitente() == null) {
				this.setLogradouroConjugeEmitente(this.getLogradouroEmitente());
			}
			if(this.getNumeroConjugeEmitente() == null) {
				this.setNumeroConjugeEmitente(this.getNumeroEmitente());
			}
			if(this.getComplementoConjugeEmitente() == null) {
				this.setComplementoConjugeEmitente(this.getComplementoEmitente());
			}
			if(this.getCepConjugeEmitente() == null) {
				this.setCepConjugeEmitente(this.getCepEmitente());
			}
		} else if (this.tipoPesquisa == "Interveniente") {
			if(this.getCidadeInterveniente() == null) {
				this.setCidadeInterveniente(getCidadeConjugeInterveniente());
			}
			if(this.getUfInterveniente() == null) {
				this.setUfInterveniente(this.getUfConjugeInterveniente());
			}
			if(this.getLogradouroInterveniente() == null) {
				this.setLogradouroInterveniente(this.getLogradouroConjugeInterveniente());
			}
			if(this.getNumeroInterveniente() == null) {
				this.setNumeroInterveniente(this.getNumeroConjugeInterveniente());
			}
			if(this.getComplementoInterveniente() == null) {
				this.setComplementoInterveniente(this.getComplementoConjugeInterveniente());
			}
			if(this.getCepInterveniente() == null) {
				this.setCepInterveniente(this.getCepConjugeInterveniente());
			}
			if(this.getCidadeConjugeInterveniente() == null) {
				this.setCidadeInterveniente(getCidadeInterveniente());
			}
			if(this.getUfConjugeInterveniente() == null) {
				this.setUfConjugeInterveniente(this.getUfInterveniente());
			}
			if(this.getLogradouroConjugeInterveniente() == null) {
				this.setLogradouroConjugeInterveniente(this.getLogradouroInterveniente());
			}
			if(this.getNumeroConjugeInterveniente() == null) {
				this.setNumeroConjugeInterveniente(this.getNumeroInterveniente());
			}
			if(this.getComplementoConjugeInterveniente() == null) {
				this.setComplementoConjugeInterveniente(this.getComplementoInterveniente());
			}
			if(this.getCepConjugeInterveniente() == null) {
				this.setCepConjugeInterveniente(this.getCepInterveniente());
			}
		}
	}

	public void pesquisaEmitente() {
		this.tipoPesquisa = "Emitente";
		this.updatePagadorRecebedor = ":form:emitentePanel";
		this.emitenteSelecionado = new PagadorRecebedor();
	}

	public void pesquisaInterveniente() {
		this.tipoPesquisa = "Interveniente";
		this.updatePagadorRecebedor = ":form:intervenientePanel";
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
		Calendar c = Calendar.getInstance();
		c.setTime(this.vencimentoPrimeiraParcelaPagamento);
		c.add(Calendar.MONTH, parcelas);
		this.setVencimentoUltimaParcelaPagamento(c.getTime());
	}

	public void populateSelectedPagadorRecebedor() {
		if (this.tipoPesquisa == "Emitente") {
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
				this.setProfissaoConjugeEmitente(this.emitenteSelecionado.getCargoConjuge());
				this.setNumeroRgConjugeEmitente(this.emitenteSelecionado.getRgConjuge());
				this.setUfConjugeEmitente(this.emitenteSelecionado.getEstadoConjuge());
				this.setCpfConjugeEmitente(this.emitenteSelecionado.getCpfConjuge());
				this.setLogradouroEmitente(this.emitenteSelecionado.getEnderecoConjuge());
				this.setComplementoConjugeEmitente(this.emitenteSelecionado.getComplementoConjuge());
				this.setCidadeConjugeEmitente(this.emitenteSelecionado.getCidadeConjuge());
				this.setCepConjugeEmitente(this.emitenteSelecionado.getCepConjuge());
				this.setEmailConjugeEmitente(this.emitenteSelecionado.getEmailConjuge());
				this.populateEndereço();
			} else {
				this.setNomeConjugeEmitente(null);
				this.setProfissaoConjugeEmitente(null);
				this.setNumeroRgConjugeEmitente(null);
				this.setUfConjugeEmitente(null);
				this.setCpfConjugeEmitente(null);
				this.setLogradouroEmitente(null);
				this.setComplementoConjugeEmitente(null);
				this.setCidadeConjugeEmitente(null);
				this.setCepConjugeEmitente(null);
				this.setEmailConjugeEmitente(null);
			}
		}

		else if (this.tipoPesquisa == "Interveniente") {
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
				this.setProfissaoConjugeInterveniente(this.intervenienteSelecionado.getCargoConjuge());
				this.setNumeroRgConjugeInterveniente(this.intervenienteSelecionado.getRgConjuge());
				this.setUfConjugeInterveniente(this.intervenienteSelecionado.getEstadoConjuge());
				this.setCpfConjugeInterveniente(this.intervenienteSelecionado.getCpfConjuge());
				this.setLogradouroInterveniente(this.intervenienteSelecionado.getEnderecoConjuge());
				this.setComplementoConjugeInterveniente(this.intervenienteSelecionado.getComplementoConjuge());
				this.setCidadeConjugeInterveniente(this.intervenienteSelecionado.getCidadeConjuge());
				this.setCepConjugeInterveniente(this.intervenienteSelecionado.getCepConjuge());
				this.populateEndereço();
			} else {
				this.setNomeConjugeInterveniente(null);
				this.setProfissaoConjugeInterveniente(null);
				this.setNumeroRgConjugeInterveniente(null);
				this.setUfConjugeInterveniente(null);
				this.setCpfConjugeInterveniente(null);
				this.setLogradouroInterveniente(null);
				this.setComplementoConjugeInterveniente(null);
				this.setCidadeConjugeInterveniente(null);
				this.setCepConjugeInterveniente(null);
				this.populateEndereço();
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
	
	public String trocaValoresNumeroExtensoXWPF(String text, XWPFRun r, String valorEscrito, BigDecimal valorSobrescrever) {
		if (text != null && text.contains("Extenso" + valorEscrito)) {
			numeroPorExtenso.setNumber(valorSobrescrever);
			text = text.replace("Extenso" + valorEscrito, numeroPorExtenso.toString());
			r.setText(text, 0);
		}
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

	@SuppressWarnings("resource")
	public StreamedContent readXWPFile() throws IOException {

	    try
	    {
	    	String tipoDownload = this.getTipoDownload();
	    	
	    	XWPFDocument document = new XWPFDocument();
	    	
	    	if (CommonsUtil.mesmoValor(tipoDownload,"CCB")) {
	    		document = new XWPFDocument(getClass().getResourceAsStream("/resource/CCB-EMITENTE_GALLERIA.docx"));
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"Anuente")) {
	    		document = new XWPFDocument(getClass().getResourceAsStream("/resource/CCB-EMITENTE_ANUENTE_GALLERIA.docx"));
	    	} else if(CommonsUtil.mesmoValor(tipoDownload,"AF")) {
	    		document = new XWPFDocument(getClass().getResourceAsStream("/resource/AF_GALLERIA.docx"));
	    	} else {
	    		return null;
	    	}


			ByteArrayOutputStream  out = new ByteArrayOutputStream ();
			
			for (XWPFParagraph p : document.getParagraphs()) {
			    List<XWPFRun> runs = p.getRuns();
			    if (runs != null) {
			        for (XWPFRun r : runs) {
			            String text = r.getText(0);
			            
						if(this.getNomeConjugeEmitente() != null) {
							text = trocaValoresXWPF(text, r, "FiducianteConjugue","Rua logradouroConjugeEmitente, nº numeroConjugeEmitente, Qd. XX - Lote XX, Cond. Residencial XXXXXX, Bairro - cidadeConjugeEmitente - ufConjugeEmitente -  \r"
									+ "CEP cepConjugeEmitente \r"
									+ "Email: emailConjugeEmitente");
							text = trocaValoresXWPF(text, r, "ConjugeDados","nacionalidadeConjugeEmitente, profissaoConjugeEmitente, estadoCivilEmitente (nomeEmitente , cpfEmitente), portador(a) da Cédula de Identidade RG nº numeroRgConjugeEmitente SSP/ufConjugeEmitente, inscrito(a) no CPF/MF sob o nº cpfConjugeEmitente, residente e domiciliado à logradouroConjugeEmitente, nº numeroConjugeEmitente, complementoConjugeEmitente, cidadeConjugeEmitente/ufConjugeEmitente, CEP cepConjugeEmitente;"); 	
							text = trocaValoresXWPF(text, r, "nomeConjugeEmitente", this.nomeConjugeEmitente);	 
							text = trocaValoresXWPF(text, r, "cpfConjugeEmitente", this.cpfConjugeEmitente);
							text = trocaValoresXWPF(text, r, "nacionalidadeConjugeEmitente",this.nacionalidadeConjugeEmitente);
							text = trocaValoresXWPF(text, r, "profissaoConjugeEmitente",this.profissaoConjugeEmitente);
							text = trocaValoresXWPF(text, r, "numeroRgConjugeEmitente", this.numeroRgConjugeEmitente);
							text = trocaValoresXWPF(text, r, "ufConjugeEmitente", this.ufConjugeEmitente);
							text = trocaValoresXWPF(text, r, "cpfConjugeEmitente", this.cpfConjugeEmitente);
							text = trocaValoresXWPF(text, r, "logradouroConjugeEmitente", this.logradouroConjugeEmitente);
							text = trocaValoresXWPF(text, r, "numeroConjugeEmitente", this.numeroConjugeEmitente);
							text = trocaValoresXWPF(text, r, "complementoConjugeEmitente", this.complementoConjugeEmitente);
							text = trocaValoresXWPF(text, r, "cidadeConjugeEmitente", this.cidadeConjugeEmitente);
							text = trocaValoresXWPF(text, r, "cepConjugeEmitente", this.cepConjugeEmitente);
							text = trocaValoresXWPF(text, r, "emailConjugeEmitente", this.emailConjugeEmitente);
						} else {
							text = trocaValoresXWPF(text, r, "nomeConjugeEmitente,", "");
							text = trocaValoresXWPF(text, r, "nomeConjugeEmitente", "");		 
							text = trocaValoresXWPF(text, r, "(nomeConjugeEmitente,","");
							text = trocaValoresXWPF(text, r, "cpfConjugeEmitente)","");
							text = trocaValoresXWPF(text, r, "ConjugeDados","");
							text = trocaValoresXWPF(text, r, "FiducianteConjugue","");
							
						}
						if(this.getNomeConjugeInterveniente() != null) {
							text = trocaValoresXWPF(text, r, "ConjugeIntervenienteDados","nacionalidadeConjugeInterveniente, profissaoConjugeInterveniente, estadoCivilInterveniente (nomeInterveniente , cpfInterveniente), portador(a) da Cédula de Identidade RG nº numeroRgConjugeInterveniente SSP/ufConjugeInterveniente, inscrito(a) no CPF/MF sob o nº cpfConjugeInterveniente, residente e domiciliado à logradouroConjugeInterveniente, nº numeroConjugeInterveniente, complementoConjugeInterveniente, cidadeConjugeInterveniente/ufConjugeInterveniente, CEP cepConjugeInterveniente;"); 	
							text = trocaValoresXWPF(text, r, "nomeConjugeInterveniente", this.nomeConjugeInterveniente);	 
							text = trocaValoresXWPF(text, r, "cpfConjugeInterveniente", this.cpfConjugeInterveniente);
							text = trocaValoresXWPF(text, r, "nacionalidadeConjugeInterveniente",this.nacionalidadeConjugeInterveniente);
							text = trocaValoresXWPF(text, r, "profissaoConjugeInterveniente",this.profissaoConjugeInterveniente);
							text = trocaValoresXWPF(text, r, "numeroRgConjugeInterveniente", this.numeroRgConjugeInterveniente);
							text = trocaValoresXWPF(text, r, "ufConjugeInterveniente", this.ufConjugeInterveniente);
							text = trocaValoresXWPF(text, r, "cpfConjugeInterveniente", this.cpfConjugeInterveniente);
							text = trocaValoresXWPF(text, r, "logradouroConjugeInterveniente", this.logradouroConjugeInterveniente);
							text = trocaValoresXWPF(text, r, "numeroConjugeInterveniente", this.numeroConjugeInterveniente);
							text = trocaValoresXWPF(text, r, "complementoConjugeInterveniente", this.complementoConjugeInterveniente);
							text = trocaValoresXWPF(text, r, "cidadeConjugeInterveniente", this.cidadeConjugeInterveniente);
							text = trocaValoresXWPF(text, r, "cepConjugeInterveniente", this.cepConjugeInterveniente);
						} else {
							text = trocaValoresXWPF(text, r, "nomeConjugeInterveniente", "");	 
							text = trocaValoresXWPF(text, r, "(nomeConjugeInterveniente,","");
							text = trocaValoresXWPF(text, r, "cpfConjugeInterveniente)","");
							text = trocaValoresXWPF(text, r, "ConjugeIntervenienteDados","");
						}
						text = trocaValoresXWPF(text, r, "estadoCivilEmitente", this.estadoCivilEmitente);	 
						text = trocaValoresXWPF(text, r, "nomeEmitente", this.nomeEmitente);	 
						text = trocaValoresXWPF(text, r, "cpfEmitente", this.cpfEmitente);
						text = trocaValoresXWPF(text, r, "nacionalidadeEmitente",this.nacionalidadeEmitente);
						text = trocaValoresXWPF(text, r, "profissaoEmitente",this.profissaoEmitente);
						text = trocaValoresXWPF(text, r, "numeroRgEmitente", this.numeroRgEmitente);
						text = trocaValoresXWPF(text, r, "ufEmitente", this.ufEmitente);
						text = trocaValoresXWPF(text, r, "cpfEmitente", this.cpfEmitente);
						text = trocaValoresXWPF(text, r, "logradouroEmitente", this.logradouroEmitente);
						text = trocaValoresXWPF(text, r, "numeroEmitente", this.numeroEmitente);
						text = trocaValoresXWPF(text, r, "complementoEmitente", this.complementoEmitente);
						text = trocaValoresXWPF(text, r, "cidadeEmitente", this.cidadeEmitente);
						text = trocaValoresXWPF(text, r, "cepEmitente", this.cepEmitente);
						text = trocaValoresXWPF(text, r, "emailEmitente", this.emailEmitente);
						
						text = trocaValoresXWPF(text, r, "nomeInterveniente", this.nomeInterveniente);
						text = trocaValoresXWPF(text, r, "estadoCivilInterveniente", this.estadoCivilInterveniente);	 
						text = trocaValoresXWPF(text, r, "cpfInterveniente", this.cpfInterveniente);
						text = trocaValoresXWPF(text, r, "nacionalidadeInterveniente",this.nacionalidadeInterveniente);
						text = trocaValoresXWPF(text, r, "profissaoInterveniente",this.profissaoInterveniente);
						text = trocaValoresXWPF(text, r, "numeroRgInterveniente", this.numeroRgInterveniente);
						text = trocaValoresXWPF(text, r, "ufInterveniente", this.ufInterveniente);
						text = trocaValoresXWPF(text, r, "cpfInterveniente", this.cpfInterveniente);
						text = trocaValoresXWPF(text, r, "logradouroInterveniente", this.logradouroInterveniente);
						text = trocaValoresXWPF(text, r, "numeroInterveniente", this.numeroInterveniente);
						text = trocaValoresXWPF(text, r, "complementoInterveniente", this.complementoInterveniente);
						text = trocaValoresXWPF(text, r, "cidadeInterveniente", this.cidadeInterveniente);
						text = trocaValoresXWPF(text, r, "cepInterveniente", this.cepInterveniente);
						
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
						text = trocaValoresNumeroExtensoXWPF(text, r, "TarifaAntecipada", this.tarifaAntecipada);

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
								text = trocaValoresXWPF(text, r, "nomeInterveniente", this.nomeInterveniente);	 
								if (this.getNomeConjugeEmitente() != null) {
									text = trocaValoresXWPF(text, r, "nomeConjugeEmitente", this.nomeConjugeEmitente);
									text = trocaValoresXWPF(text, r, "____________________________________c", "____________________________________");
									text = trocaValoresXWPF(text, r, "CONJUGEEMITENTE", "CONJUGE");
								} else {
									text = trocaValoresXWPF(text, r, "____________________________________c", "");
									text = trocaValoresXWPF(text, r, "CONJUGEEMITENTE", "");
									text = trocaValoresXWPF(text, r, "nomeConjugeEmitente", "");
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
			
			gerador.open(String.format("teste %s.docx", ""));
			gerador.feed(new ByteArrayInputStream(out.toByteArray()));
			gerador.close();
	    }
	    catch ( Throwable e )
	    {
	        e.printStackTrace();
	    }
	    
        this.intervenienteSelecionado = new PagadorRecebedor();
		this.emitenteSelecionado = new PagadorRecebedor();
		this.selectedPagador = new PagadorRecebedor();
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

	public String clearFieldsEmitirCcb() {
		loadLovs();
		this.intervenienteSelecionado = new PagadorRecebedor();
		this.emitenteSelecionado = new PagadorRecebedor();
		this.selectedPagador = new PagadorRecebedor();
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

	public String getNacionalidadeConjugeEmitente() {
		return nacionalidadeConjugeEmitente;
	}

	public void setNacionalidadeConjugeEmitente(String nacionalidadeConjugeEmitente) {
		this.nacionalidadeConjugeEmitente = nacionalidadeConjugeEmitente;
	}

	public String getProfissaoConjugeEmitente() {
		return profissaoConjugeEmitente;
	}

	public void setProfissaoConjugeEmitente(String profissaoConjugeEmitente) {
		this.profissaoConjugeEmitente = profissaoConjugeEmitente;
	}

	public String getNumeroRgConjugeEmitente() {
		return numeroRgConjugeEmitente;
	}

	public void setNumeroRgConjugeEmitente(String numeroRgConjugeEmitente) {
		this.numeroRgConjugeEmitente = numeroRgConjugeEmitente;
	}

	public String getUfConjugeEmitente() {
		return ufConjugeEmitente;
	}

	public void setUfConjugeEmitente(String ufConjugeEmitente) {
		this.ufConjugeEmitente = ufConjugeEmitente;
	}

	public String getCpfConjugeEmitente() {
		return cpfConjugeEmitente;
	}

	public void setCpfConjugeEmitente(String cpfConjugeEmitente) {
		this.cpfConjugeEmitente = cpfConjugeEmitente;
	}

	public String getLogradouroConjugeEmitente() {
		return logradouroConjugeEmitente;
	}

	public void setLogradouroConjugeEmitente(String logradouroConjugeEmitente) {
		this.logradouroConjugeEmitente = logradouroConjugeEmitente;
	}

	public String getNumeroConjugeEmitente() {
		return numeroConjugeEmitente;
	}

	public void setNumeroConjugeEmitente(String numeroConjugeEmitente) {
		this.numeroConjugeEmitente = numeroConjugeEmitente;
	}

	public String getComplementoConjugeEmitente() {
		return complementoConjugeEmitente;
	}

	public void setComplementoConjugeEmitente(String complementoConjugeEmitente) {
		this.complementoConjugeEmitente = complementoConjugeEmitente;
	}

	public String getCidadeConjugeEmitente() {
		return cidadeConjugeEmitente;
	}

	public void setCidadeConjugeEmitente(String cidadeConjugeEmitente) {
		this.cidadeConjugeEmitente = cidadeConjugeEmitente;
	}

	public String getCepConjugeEmitente() {
		return cepConjugeEmitente;
	}

	public void setCepConjugeEmitente(String cepConjugeEmitente) {
		this.cepConjugeEmitente = cepConjugeEmitente;
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

	public String getNacionalidadeConjugeInterveniente() {
		return nacionalidadeConjugeInterveniente;
	}

	public void setNacionalidadeConjugeInterveniente(String nacionalidadeConjugeInterveniente) {
		this.nacionalidadeConjugeInterveniente = nacionalidadeConjugeInterveniente;
	}

	public String getProfissaoConjugeInterveniente() {
		return profissaoConjugeInterveniente;
	}

	public void setProfissaoConjugeInterveniente(String profissaoConjugeInterveniente) {
		this.profissaoConjugeInterveniente = profissaoConjugeInterveniente;
	}

	public String getNumeroRgConjugeInterveniente() {
		return numeroRgConjugeInterveniente;
	}

	public void setNumeroRgConjugeInterveniente(String numeroRgConjugeInterveniente) {
		this.numeroRgConjugeInterveniente = numeroRgConjugeInterveniente;
	}

	public String getUfConjugeInterveniente() {
		return ufConjugeInterveniente;
	}

	public void setUfConjugeInterveniente(String ufConjugeInterveniente) {
		this.ufConjugeInterveniente = ufConjugeInterveniente;
	}

	public String getCpfConjugeInterveniente() {
		return cpfConjugeInterveniente;
	}

	public void setCpfConjugeInterveniente(String cpfConjugeInterveniente) {
		this.cpfConjugeInterveniente = cpfConjugeInterveniente;
	}

	public String getLogradouroConjugeInterveniente() {
		return logradouroConjugeInterveniente;
	}

	public void setLogradouroConjugeInterveniente(String logradouroConjugeInterveniente) {
		this.logradouroConjugeInterveniente = logradouroConjugeInterveniente;
	}

	public String getNumeroConjugeInterveniente() {
		return numeroConjugeInterveniente;
	}

	public void setNumeroConjugeInterveniente(String numeroConjugeInterveniente) {
		this.numeroConjugeInterveniente = numeroConjugeInterveniente;
	}

	public String getComplementoConjugeInterveniente() {
		return complementoConjugeInterveniente;
	}

	public void setComplementoConjugeInterveniente(String complementoConjugeInterveniente) {
		this.complementoConjugeInterveniente = complementoConjugeInterveniente;
	}

	public String getCidadeConjugeInterveniente() {
		return cidadeConjugeInterveniente;
	}

	public void setCidadeConjugeInterveniente(String cidadeConjugeInterveniente) {
		this.cidadeConjugeInterveniente = cidadeConjugeInterveniente;
	}

	public String getCepConjugeInterveniente() {
		return cepConjugeInterveniente;
	}

	public void setCepConjugeInterveniente(String cepConjugeInterveniente) {
		this.cepConjugeInterveniente = cepConjugeInterveniente;
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

	public String getEmailConjugeEmitente() {
		return emailConjugeEmitente;
	}

	public void setEmailConjugeEmitente(String emailConjugeEmitente) {
		this.emailConjugeEmitente = emailConjugeEmitente;
	}
	

}
