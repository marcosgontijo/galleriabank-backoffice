package com.webnowbr.siscoat.cobranca.mb;

import java.io.BufferedReader;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
import org.primefaces.util.CalendarUtils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.webnowbr.siscoat.cobranca.auxiliar.RelatorioFinanceiroCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhesObservacoes;
import com.webnowbr.siscoat.cobranca.db.model.FaturaIUGU;
import com.webnowbr.siscoat.cobranca.db.model.OperacaoContratoIUGU;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.SaldoIUGU;
import com.webnowbr.siscoat.cobranca.db.model.SaqueIUGU;
import com.webnowbr.siscoat.cobranca.db.model.SubContaIUGU;
import com.webnowbr.siscoat.cobranca.db.model.TransferenciasIUGU;
import com.webnowbr.siscoat.cobranca.db.model.TransferenciasObservacoesIUGU;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.TransferenciasObservacoesIUGUDao;
import com.webnowbr.siscoat.cobranca.mb.ContratoCobrancaMB.FileUploaded;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.BcMsgRetorno;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ResumoDaOperacao;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ResumoDoCliente;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ResumoDoClienteTraduzido;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ResumoDoVencimento;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ResumoModalidade;
import com.webnowbr.siscoat.cobranca.model.bmpdigital.ScrResult;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.DateUtil;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

@ManagedBean(name = "bmpDigitalMB")
@SessionScoped
public class BmpDigitalMB {

	/****
	 * 
		
	{
	  "auth": {
	    "Usuario": "joao@galleriafinancas.com.br",
	    "Senha": "Scr!2021",
	    "CodigoParametro": "GALLERIA_SCR",
	    "Chave": "eb11110f-9f0e-4a16-83d7-6229c949da4a"
	  },
	
	
	•	URL de Produção
	o	Integração
	https://bmpdigital.moneyp.com.br/api/BMPDigital/ <- concatenando o serviço
	o	Swagger
			https://bmpteste.moneyp.com.br/swagger/ui/index
	o	Dashboard
		https://bmpdigital.moneyp.com.br/
		Para acessarem podem utilizar o mesmo login e senha da integração
		Caso necessitem de outros usuários podem solicitar que liberamos

	 */

	
	/***
	 * INICIO ATRIBUTOS RECIBO
	 */
	
	private String documento;
	
	private TransferenciasObservacoesIUGU transferenciasObservacoesIUGU;
	private boolean pdfGerado;
	private String pathPDF;
	private String nomePDF;
	private StreamedContent file;
	/***
	 * FIM ATRIBUTOS RECIBO
	 */



	/****
	 * 
	 * COMPOEM O JSON UTILIZADO NA TRANSFERENCIA DE VALORES SUBCONTAS IUGU
	 * 
	 * @return
	 */
	
	public String clearFields() {
		this.documento = "";
		this.pathPDF = "";
		this.nomePDF = "";
		this.file = null;
		this.pdfGerado = false;
		
		return "/Atendimento/Cobranca/ConsultaSCR.xhtml";
	}
	public String composeJSONPayload() {
		String json = "";
		
		Date dataHoje = DateUtil.getDataHoje();
		//Date dataReferencia = DateUtil.adicionarMes(dataHoje, -2);
		Date dataReferencia = DateUtil.adicionarDias(dataHoje, -60);
		
		int mesReferenciaInt = dataReferencia.getMonth();
		mesReferenciaInt ++;
		String mesReferencia = CommonsUtil.stringValue(mesReferenciaInt);
		
		int anoReferenciaInt = dataReferencia.getYear();
		anoReferenciaInt += 1900;
		String anoReferencia = CommonsUtil.stringValue(anoReferenciaInt);
		
		//calendar.get(Calendar.MONTH) retorna 1 mês antes porque começa em 0
		// Subtraimos 1 porque a consulta é de 45 dias pra tras apenas

		json = "{\"auth\":{\"Usuario\":\"JOAO@GALLERIAFINANCAS.COM.BR\",\"Senha\":\"Scr!2021\",\"CodigoParametro\":\"GALLERIA_SCR\",\"Chave\":\"eb11110f-9f0e-4a16-83d7-6229c949da4a\"}, " +
			    "\"consulta\":{\"Documento\":\"" + this.documento + "\",\"DataBaseMes\":\"" + mesReferencia + "\",\"DataBaseAno\":\"" + anoReferencia + "\"}}";
		
		return json;
	}
	
	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}
	
	// retorna a string do objeto JSON, mesmo em caso de nulos
	public String getStringJSON(JSONObject objetoJSON, String chave) {
		if (!objetoJSON.isNull(chave)) {
			return objetoJSON.getString(chave);
		} else {
			return "";
		}
	}
	
	// retorna a string do objeto JSON, mesmo em caso de nulos
	public boolean getObjectJSON(JSONObject objetoJSON, String chave) {
		if (objetoJSON.has(chave)) {
			if (!objetoJSON.isNull(chave)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * EFETUA TRANSFERENCIA ENTRE CONTAS
	 */
	public void consultaSCR() {
		try {		
			FacesContext context = FacesContext.getCurrentInstance();
			
			System.out.println(" INICIO DO PROCESSO ");
			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://bmpdigital.moneyp.com.br/api/BMPDigital/ConsultaSCR");

			String dados = composeJSONPayload();			

			JSONObject jsonObj = new JSONObject(dados);
			byte[] postDataBytes = jsonObj.toString().getBytes();

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			myURLConnection.setDoOutput(true);
			myURLConnection.getOutputStream().write(postDataBytes);

			String erro = "";
			JSONObject myResponse = null;

			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
				System.out.println("Erro ao Consultar o SCR BMP Digital - " + myURLConnection.getResponseCode());
			} else {				
				myResponse = getJsonSucesso(myURLConnection.getInputStream());
				
				ScrResult scResult = new ScrResult();
				
				if (!myResponse.getBoolean("Erro")) {
					scResult.setErro(myResponse.getBoolean("Erro"));
					scResult.setMensagemOperador(myResponse.getString("MensagemOperador"));
					scResult.setPeriodo(myResponse.getString("Periodo"));
				
					if (getObjectJSON(myResponse, "ResumoDoCliente")) {
						
						JSONObject resumoDoClienteJSON = myResponse.getJSONObject("ResumoDoCliente");
						
						ResumoDoCliente resumoDoCliente = new ResumoDoCliente();				
				
						resumoDoCliente.setCnpjDaIfSolicitante(getStringJSON(resumoDoClienteJSON, "CnpjDaIfSolicitante"));
						resumoDoCliente.setCodigoDoCliente(getStringJSON(resumoDoClienteJSON, "CodigoDoCliente"));
						resumoDoCliente.setCoobrigacaoAssumida(BigDecimal.valueOf(resumoDoClienteJSON.getLong("CoobrigacaoAssumida")));
						resumoDoCliente.setCoobrigacaoAssumidaSpecified(resumoDoClienteJSON.getBoolean("CoobrigacaoAssumidaSpecified"));
						resumoDoCliente.setCoobrigacaoRecebida(BigDecimal.valueOf(resumoDoClienteJSON.getLong("CoobrigacaoRecebida")));
						resumoDoCliente.setCoobrigacaoRecebidaSpecified(resumoDoClienteJSON.getBoolean("CoobrigacaoRecebidaSpecified"));
						resumoDoCliente.setDataBaseConsultada(getStringJSON(resumoDoClienteJSON, "DataBaseConsultada"));
						resumoDoCliente.setDataInicioRelacionamento(getStringJSON(resumoDoClienteJSON, "DataInicioRelacionamento"));
						resumoDoCliente.setPercentualDocumentosProcessados(getStringJSON(resumoDoClienteJSON, "PercentualDocumentosProcessados"));
						resumoDoCliente.setPercentualVolumeProcessado(getStringJSON(resumoDoClienteJSON, "PercentualVolumeProcessado"));
						resumoDoCliente.setQuantidadeDeInstituicoes(resumoDoClienteJSON.getInt("QuantidadeDeInstituicoes"));
						resumoDoCliente.setQuantidadeDeOperacoes(resumoDoClienteJSON.getInt("QuantidadeDeOperacoes"));
						resumoDoCliente.setQuantidadeOperacoesDiscordancia(resumoDoClienteJSON.getInt("QuantidadeOperacoesSubJudice"));
						resumoDoCliente.setQuantidadeOperacoesSubJudice(resumoDoClienteJSON.getInt("QuantidadeOperacoesSubJudice"));
						resumoDoCliente.setResponsabilidadeTotalDiscordancia(BigDecimal.valueOf(resumoDoClienteJSON.getLong("CoobrigacaoRecebida")));
						resumoDoCliente.setResponsabilidadeTotalDiscordanciaSpecified(resumoDoClienteJSON.getBoolean("ResponsabilidadeTotalDiscordanciaSpecified"));
						resumoDoCliente.setResponsabilidadeTotalSubJudice(BigDecimal.valueOf(resumoDoClienteJSON.getLong("ResponsabilidadeTotalSubJudice")));
						resumoDoCliente.setResponsabilidadeTotalSubJudiceSpecified(resumoDoClienteJSON.getBoolean("ResponsabilidadeTotalSubJudiceSpecified"));
						resumoDoCliente.setRiscoIndiretoVendor(BigDecimal.valueOf(resumoDoClienteJSON.getLong("RiscoIndiretoVendor")));
						resumoDoCliente.setRiscoIndiretoVendorSpecified(resumoDoClienteJSON.getBoolean("RiscoIndiretoVendorSpecified"));
						resumoDoCliente.setTipoDoCliente(getStringJSON(resumoDoClienteJSON, "TipoDoCliente"));
		
						if (getObjectJSON(resumoDoClienteJSON, "ListaDeMensagensDeValidacao")) {
						
							JSONArray mensagensDeValidacao = resumoDoClienteJSON.getJSONArray("ListaDeMensagensDeValidacao");
		
							List<BcMsgRetorno> listBcMsgRetorno = new ArrayList<BcMsgRetorno>();
							
							for (int i = 0; i < mensagensDeValidacao.length(); i++) {
								BcMsgRetorno bcMsgRetorno = new BcMsgRetorno();
		
								JSONObject obj = mensagensDeValidacao.getJSONObject(i);
								
								bcMsgRetorno.setCodigo(getStringJSON(obj, "Codigo"));
								bcMsgRetorno.setMensagem(getStringJSON(obj, "Mensagem"));
								
								listBcMsgRetorno.add(bcMsgRetorno);
							}
							
							if (listBcMsgRetorno.size() > 0) {
								resumoDoCliente.setListaDeMensagensDeValidacao(listBcMsgRetorno);
							}
						}
						
						if (getObjectJSON(resumoDoClienteJSON, "ListaDeResumoDasOperacoes")) {
							
							JSONArray resumoDasOperacoes = resumoDoClienteJSON.getJSONArray("ListaDeResumoDasOperacoes");
		
							List<ResumoDaOperacao> listResumoDaOperacao = new ArrayList<ResumoDaOperacao>();
							
							for (int i = 0; i < resumoDasOperacoes.length(); i++) {
								ResumoDaOperacao resumoDaOperacao = new ResumoDaOperacao();
		
								JSONObject objResumoDasOperacoes = resumoDasOperacoes.getJSONObject(i);
														
								resumoDaOperacao.setModalidade(getStringJSON(objResumoDasOperacoes, "Modalidade"));
								resumoDaOperacao.setVariacaoCambial(getStringJSON(objResumoDasOperacoes, "VariacaoCambial"));
								
								if (getObjectJSON(objResumoDasOperacoes, "ListaDeVencimentos")) {
									
									JSONArray vencimentos = objResumoDasOperacoes.getJSONArray("ListaDeVencimentos");
									
									List<ResumoDoVencimento> listResumoDoVencimento = new ArrayList<ResumoDoVencimento>();
									
									for (int j = 0; j < vencimentos.length(); j++) {
										ResumoDoVencimento resumoDoVencimento = new ResumoDoVencimento();
				
										JSONObject objVencimentos = vencimentos.getJSONObject(j);
										
										resumoDoVencimento.setCodigoVencimento(getStringJSON(objVencimentos, "CodigoVencimento"));
										resumoDoVencimento.setValorVencimento(BigDecimal.valueOf(objVencimentos.getLong("ValorVencimento")));
										resumoDoVencimento.setValorVencimentoSpecified(objVencimentos.getBoolean("ValorVencimentoSpecified"));
										
										listResumoDoVencimento.add(resumoDoVencimento);
									}
									
									if (listResumoDoVencimento.size() > 0) {
										resumoDaOperacao.setListaDeVencimentos(listResumoDoVencimento);
									}
								}
								
								listResumoDaOperacao.add(resumoDaOperacao);
							}
							
							if (listResumoDaOperacao.size() > 0) {
								resumoDoCliente.setListaDeResumoDasOperacoes(listResumoDaOperacao);
							}
						}
	
						scResult.setResumoDoCliente(resumoDoCliente);
					}
					
					if (getObjectJSON(myResponse, "ResumoDoClienteTraduzido")) {
						
						JSONObject resumoDoClienteTraduzidoJSON = myResponse.getJSONObject("ResumoDoClienteTraduzido");
						
						ResumoDoClienteTraduzido resumoDoClienteTraduzido = new ResumoDoClienteTraduzido();
						
						resumoDoClienteTraduzido.setPercDocumentosProcessados(getStringJSON(resumoDoClienteTraduzidoJSON, "PercDocumentosProcessados"));
						resumoDoClienteTraduzido.setDtInicioRelacionamento(getStringJSON(resumoDoClienteTraduzidoJSON, "DtInicioRelacionamento"));
						resumoDoClienteTraduzido.setQtdeInstituicoes(resumoDoClienteTraduzidoJSON.getInt("QtdeInstituicoes"));
						resumoDoClienteTraduzido.setQtdeOperacoes(resumoDoClienteTraduzidoJSON.getInt("QtdeOperacoes"));
						resumoDoClienteTraduzido.setQtdeOperacoesDiscordancia(resumoDoClienteTraduzidoJSON.getInt("QtdeOperacoesDiscordancia"));
						resumoDoClienteTraduzido.setVlrOperacoesDiscordancia(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("VlrOperacoesDiscordancia")));
						resumoDoClienteTraduzido.setQtdeOperacoesSobJudice(resumoDoClienteTraduzidoJSON.getInt("QtdeOperacoesSobJudice"));
						resumoDoClienteTraduzido.setVlrOperacoesSobJudice(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("VlrOperacoesSobJudice")));
						resumoDoClienteTraduzido.setCarteiraVencer(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("CarteiraVencer")));
						resumoDoClienteTraduzido.setCarteiraVencerAte30diasVencidosAte14dias(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("CarteiraVencerAte30diasVencidosAte14dias")));
						resumoDoClienteTraduzido.setCarteiraVencer31a60dias(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("CarteiraVencer31a60dias")));
						resumoDoClienteTraduzido.setCarteiraVencer61a90dias(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("CarteiraVencer61a90dias")));
						resumoDoClienteTraduzido.setCarteiraVencer91a180dias(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("CarteiraVencer91a180dias")));
						resumoDoClienteTraduzido.setCarteiraVencer181a360dias(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("CarteiraVencer181a360dias")));
						resumoDoClienteTraduzido.setCarteiraVencerAcima360dias(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("CarteiraVencerAcima360dias")));
						resumoDoClienteTraduzido.setCarteiraVencerPrazoIndeterminado(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("CarteiraVencerPrazoIndeterminado")));
						resumoDoClienteTraduzido.setCarteiraVencido(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("CarteiraVencido")));
						resumoDoClienteTraduzido.setCarteiraVencido15a30dias(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("CarteiraVencido15a30dias")));
						resumoDoClienteTraduzido.setCarteiraVencido31a60dias(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("CarteiraVencido31a60dias")));
						resumoDoClienteTraduzido.setCarteiraVencido61a90dias(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("CarteiraVencido61a90dias")));
						resumoDoClienteTraduzido.setCarteiraVencido91a180dias(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("CarteiraVencido91a180dias")));
						resumoDoClienteTraduzido.setCarteiraVencido181a360dias(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("CarteiraVencido181a360dias")));
						resumoDoClienteTraduzido.setCarteiraVencidoAcima360dias(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("CarteiraVencidoAcima360dias")));
						resumoDoClienteTraduzido.setPrejuizo(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("Prejuizo")));
						resumoDoClienteTraduzido.setPrejuizoAte12meses(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("PrejuizoAte12meses")));
						resumoDoClienteTraduzido.setPrejuizoAcima12meses(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("PrejuizoAcima12meses")));
						resumoDoClienteTraduzido.setCarteiradeCredito(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("CarteiradeCredito")));
						resumoDoClienteTraduzido.setRepasses(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("Repasses")));
						resumoDoClienteTraduzido.setCoobrigacoes(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("Coobrigacoes")));
						resumoDoClienteTraduzido.setResponsabilidadeTotal(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("ResponsabilidadeTotal")));
						resumoDoClienteTraduzido.setCreditosaLiberar(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("CreditosaLiberar")));
						resumoDoClienteTraduzido.setLimitesdeCredito(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("LimitesdeCredito")));
						resumoDoClienteTraduzido.setLimitesdeCreditoAte360dias(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("LimitesdeCreditoAte360dias")));
						resumoDoClienteTraduzido.setLimitesdeCreditoAcima360dias(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("LimitesdeCreditoAcima360dias")));
						resumoDoClienteTraduzido.setRiscoIndiretoVendor(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("RiscoIndiretoVendor")));
						resumoDoClienteTraduzido.setRiscoTotal(BigDecimal.valueOf(resumoDoClienteTraduzidoJSON.getLong("RiscoTotal")));
						
						scResult.setResumoDoClienteTraduzido(resumoDoClienteTraduzido);				
					}
					
					if (getObjectJSON(myResponse, "ResumoModalidade")) {
						
						List<ResumoModalidade> listResumoModalidade = new ArrayList<ResumoModalidade>();
						
						JSONArray resumoModalidadeJSON = myResponse.getJSONArray("ResumoModalidade");
						
						for (int i = 0; i < resumoModalidadeJSON.length(); i++) {
							ResumoModalidade resumoModalidade = new ResumoModalidade();
	
							JSONObject objResumoModalidade = resumoModalidadeJSON.getJSONObject(i);
							
							resumoModalidade.setDominio(getStringJSON(objResumoModalidade, "dominio"));
							resumoModalidade.setModalidade(getStringJSON(objResumoModalidade, "modalidade"));
							resumoModalidade.setSubdominio(getStringJSON(objResumoModalidade, "subdominio"));
							resumoModalidade.setTipo(getStringJSON(objResumoModalidade, "tipo"));
							resumoModalidade.setValorVencimento(BigDecimal.valueOf(objResumoModalidade.getLong("valorVencimento")));
						}
						
						if (listResumoModalidade.size() > 0) {
							scResult.setResumoModalidade(listResumoModalidade);
						}
					}								
	
					System.out.println("SUCESSO NA GERAÇÃO DO SCR" );
					// gera pdf
					imprimeContrato(scResult.getResumoDoCliente(), scResult.getResumoDoClienteTraduzido());							
				} else {
					// TODO Auto-generated catch block
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"SCR: Ocorreu um problema ao consultar o SCR! (Mensagem: " + myResponse.getString("MensagemOperador") + ")",
							""));
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
			in = new BufferedReader(
					new InputStreamReader(inputStream, "UTF-8"));

			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			//READ JSON response and print
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

	public void imprimeContrato(ResumoDoCliente resumoDoCliente, ResumoDoClienteTraduzido resumoDoClienteTraduzido) {

		FacesContext context = FacesContext.getCurrentInstance();
		/*
		 * Referência iText - Gerador PDF
		 * http://www.dicas-l.com.br/arquivo/gerando_pdf_utilizando_java.php#.
		 * VGpT0_nF_h4
		 */
		
		this.pathPDF = "";
		this.nomePDF = "";
		this.file = null;
		this.pdfGerado = false;

		Document doc = null;
		OutputStream os = null;
		try {
			/*
			 * Fonts Utilizadas no PDF
			 */
			Font header = new Font(FontFamily.HELVETICA, 14, Font.BOLD);
			Font titulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font tituloBranco = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			tituloBranco.setColor(BaseColor.WHITE);
			Font normal = new Font(FontFamily.HELVETICA, 10);
			Font subtitulo = new Font(FontFamily.HELVETICA, 10, Font.BOLD);	    	
			Font subtituloIdent = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			Font destaque = new Font(FontFamily.HELVETICA, 8, Font.BOLD);

			TimeZone zone = TimeZone.getDefault();
			Locale locale = new Locale("pt", "BR");

			/*
			 * Formatadores de Data/Hora
			 */
			SimpleDateFormat sdfDataContrato = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss", locale);
			SimpleDateFormat sdfDataFormatada = new SimpleDateFormat("dd/MMM/yyyy", locale);
			SimpleDateFormat sdfDataFormatadaMesAno = new SimpleDateFormat("MMM/yyyy", locale);
			SimpleDateFormat sdfDataArquivo = new SimpleDateFormat("dd-MMM-yyyy", locale);
			
			DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");

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
			
			String documentoSemCaracters = this.documento.replace(".", "").replace("-", "").replace("/", "");
			
			this.pathPDF = pDao.findByFilter("nome", "LOCACAO_PATH_COBRANCA").get(0).getValorString();
			this.nomePDF = sdfDataArquivo.format(date.getTime()) + " SCR "
					+ documentoSemCaracters + ".pdf";
			
			os = new FileOutputStream(this.pathPDF + this.nomePDF);

			// Associa a stream de saída ao
			PdfWriter.getInstance(doc, os);

			// Abre o documento
			doc.open();

			PdfPTable table = new PdfPTable(new float[] {0.16f, 0.16f, 0.16f});
			table.setWidthPercentage(100.0f); 
			
			SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd"); 
			SimpleDateFormat formatoMesAno = new SimpleDateFormat("yyyy-MM"); 
			
			PdfPCell cell1 = new PdfPCell(new Phrase("Consulta SCR - Data Base Consultada (" + sdfDataFormatadaMesAno.format(formatoMesAno.parse(resumoDoCliente.getDataBaseConsultada())) + ")" , header));
			
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Documento Síntese", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Documento", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);			
			
			cell1 = new PdfPCell(new Phrase(this.documento, normal));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Dt. Inicio Relacionamento", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);			
			
			if (resumoDoClienteTraduzido.getDtInicioRelacionamento() != null && !resumoDoClienteTraduzido.getDtInicioRelacionamento().equals("")) { 
				cell1 = new PdfPCell(new Phrase(sdfDataFormatada.format(formato.parse(resumoDoClienteTraduzido.getDtInicioRelacionamento())), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Qtde. Instituições", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(String.valueOf(resumoDoClienteTraduzido.getQtdeInstituicoes()), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Qtde. Operações", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(String.valueOf(resumoDoClienteTraduzido.getQtdeOperacoes()), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Qtde. Operações Discordância", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(String.valueOf(resumoDoClienteTraduzido.getQtdeOperacoesDiscordancia()), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Vlr. Operações Discordância", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			if (resumoDoClienteTraduzido.getVlrOperacoesDiscordancia() != null && (resumoDoClienteTraduzido.getVlrOperacoesDiscordancia().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getVlrOperacoesDiscordancia()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Qtde. Operações Sob Judice", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase(String.valueOf(resumoDoClienteTraduzido.getQtdeOperacoesSobJudice()), normal));
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Vlr. Operações Sob Judice", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
						
			if (resumoDoClienteTraduzido.getVlrOperacoesSobJudice() != null && (resumoDoClienteTraduzido.getVlrOperacoesSobJudice().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getVlrOperacoesSobJudice()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(2);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Fluxo", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(15f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira Ativa (A)", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("A Vencer", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setPaddingLeft(25f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira a Vencer", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencer() != null && (resumoDoClienteTraduzido.getCarteiraVencer().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencer()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencer() != null && (resumoDoClienteTraduzido.getCarteiraVencer().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencer())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}	
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira a Vencer Até 30 dias e Vencidos Até 14 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencerAte30diasVencidosAte14dias() != null && (resumoDoClienteTraduzido.getCarteiraVencerAte30diasVencidosAte14dias().compareTo(BigDecimal.ZERO) > 0)) { 				
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencerAte30diasVencidosAte14dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencerAte30diasVencidosAte14dias() != null && (resumoDoClienteTraduzido.getCarteiraVencerAte30diasVencidosAte14dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencerAte30diasVencidosAte14dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira a Vencer 31 a 60 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencer31a60dias() != null && (resumoDoClienteTraduzido.getCarteiraVencer31a60dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencer31a60dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencer31a60dias() != null && (resumoDoClienteTraduzido.getCarteiraVencer31a60dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencer31a60dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}	
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira a Vencer 61 a 90 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencer61a90dias() != null && (resumoDoClienteTraduzido.getCarteiraVencer61a90dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencer61a90dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencer61a90dias() != null && (resumoDoClienteTraduzido.getCarteiraVencer61a90dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencer61a90dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira a Vencer 91 a 180 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencer91a180dias() != null && (resumoDoClienteTraduzido.getCarteiraVencer91a180dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencer91a180dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencer91a180dias() != null && (resumoDoClienteTraduzido.getCarteiraVencer91a180dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencer91a180dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira a Vencer 181 a 360 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencer181a360dias() != null && (resumoDoClienteTraduzido.getCarteiraVencer181a360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencer181a360dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencer181a360dias() != null && (resumoDoClienteTraduzido.getCarteiraVencer181a360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencer181a360dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}			
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Carteira a Vencer Acima 360 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencerAcima360dias() != null && (resumoDoClienteTraduzido.getCarteiraVencerAcima360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencerAcima360dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencerAcima360dias() != null && (resumoDoClienteTraduzido.getCarteiraVencerAcima360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencerAcima360dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira a Vencer Prazo Indeterminado", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencerPrazoIndeterminado() != null && (resumoDoClienteTraduzido.getCarteiraVencerPrazoIndeterminado().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencerPrazoIndeterminado()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencerPrazoIndeterminado() != null && (resumoDoClienteTraduzido.getCarteiraVencerPrazoIndeterminado().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencerPrazoIndeterminado())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Vencido", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setPaddingLeft(25f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira Vencido", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido() != null && (resumoDoClienteTraduzido.getCarteiraVencido().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencido()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido() != null && (resumoDoClienteTraduzido.getCarteiraVencido().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencido())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira Vencido 15 a 30 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido15a30dias() != null && (resumoDoClienteTraduzido.getCarteiraVencido15a30dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencido15a30dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido15a30dias() != null && (resumoDoClienteTraduzido.getCarteiraVencido15a30dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencido15a30dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira Vencido 31 a 60 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido31a60dias() != null && (resumoDoClienteTraduzido.getCarteiraVencido31a60dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencido31a60dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido31a60dias() != null && (resumoDoClienteTraduzido.getCarteiraVencido31a60dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencido31a60dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira Vencido 61 a 90 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido61a90dias() != null && (resumoDoClienteTraduzido.getCarteiraVencido61a90dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencido61a90dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido61a90dias() != null && (resumoDoClienteTraduzido.getCarteiraVencido61a90dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencido61a90dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Carteira Vencido 91 a 180 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido91a180dias() != null && (resumoDoClienteTraduzido.getCarteiraVencido91a180dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencido91a180dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido91a180dias() != null && (resumoDoClienteTraduzido.getCarteiraVencido91a180dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencido91a180dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}	
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira Vencido 181 a 360 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido181a360dias() != null && (resumoDoClienteTraduzido.getCarteiraVencido181a360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencido181a360dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencido181a360dias() != null && (resumoDoClienteTraduzido.getCarteiraVencido181a360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencido181a360dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira Vencido Acima 360 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencidoAcima360dias() != null && (resumoDoClienteTraduzido.getCarteiraVencidoAcima360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiraVencidoAcima360dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiraVencidoAcima360dias() != null && (resumoDoClienteTraduzido.getCarteiraVencidoAcima360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiraVencidoAcima360dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Prejuízo (B)", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Prejuízo", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getPrejuizo() != null && (resumoDoClienteTraduzido.getPrejuizo().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getPrejuizo()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getPrejuizo() != null && (resumoDoClienteTraduzido.getPrejuizo().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getPrejuizo())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Prejuizo Até 12 meses", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getPrejuizoAte12meses() != null && (resumoDoClienteTraduzido.getPrejuizoAte12meses().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getPrejuizoAte12meses()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getPrejuizoAte12meses() != null && (resumoDoClienteTraduzido.getPrejuizoAte12meses().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getPrejuizoAte12meses())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}			
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Prejuízo Acima 12 meses", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getPrejuizoAcima12meses() != null && (resumoDoClienteTraduzido.getPrejuizoAcima12meses().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getPrejuizoAcima12meses()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getPrejuizoAcima12meses() != null && (resumoDoClienteTraduzido.getPrejuizoAcima12meses().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getPrejuizoAcima12meses())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);

			cell1 = new PdfPCell(new Phrase("Carteira de Crédito Tomado (A+B=C)", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Carteira de Crédito Tomado", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiradeCredito() != null && (resumoDoClienteTraduzido.getCarteiradeCredito().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCarteiradeCredito()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCarteiradeCredito() != null && (resumoDoClienteTraduzido.getCarteiradeCredito().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCarteiradeCredito())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}			
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Repasses interfinanceiros (D)", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Repasses", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getRepasses() != null && (resumoDoClienteTraduzido.getRepasses().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getRepasses()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getRepasses() != null && (resumoDoClienteTraduzido.getRepasses().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getRepasses())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Coobrigações (E)", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Coobrigações", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCoobrigacoes() != null && (resumoDoClienteTraduzido.getCoobrigacoes().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCoobrigacoes()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCoobrigacoes() != null && (resumoDoClienteTraduzido.getCoobrigacoes().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCoobrigacoes())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Responsabilidade Total (C+D+E=F)", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Responsabilidade Total", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getResponsabilidadeTotal() != null && (resumoDoClienteTraduzido.getResponsabilidadeTotal().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getResponsabilidadeTotal()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getResponsabilidadeTotal() != null && (resumoDoClienteTraduzido.getResponsabilidadeTotal().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getResponsabilidadeTotal())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Créditos a liberar (G)", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Creditos a Liberar", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCreditosaLiberar() != null && (resumoDoClienteTraduzido.getCreditosaLiberar().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getCreditosaLiberar()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getCreditosaLiberar() != null && (resumoDoClienteTraduzido.getCreditosaLiberar().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getCreditosaLiberar())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Limites de Crédito Disponível (H)", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Limites de Crédito Disponível", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getLimitesdeCredito() != null && (resumoDoClienteTraduzido.getLimitesdeCredito().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getLimitesdeCredito()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getLimitesdeCredito() != null && (resumoDoClienteTraduzido.getLimitesdeCredito().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getLimitesdeCredito())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Limites de Crédito Até 360 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getLimitesdeCreditoAte360dias() != null && (resumoDoClienteTraduzido.getLimitesdeCreditoAte360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getLimitesdeCreditoAte360dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getLimitesdeCreditoAte360dias() != null && (resumoDoClienteTraduzido.getLimitesdeCreditoAte360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getLimitesdeCreditoAte360dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Limites de Crédito Acima 360 dias", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getLimitesdeCreditoAcima360dias() != null && (resumoDoClienteTraduzido.getLimitesdeCreditoAcima360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getLimitesdeCreditoAcima360dias()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getLimitesdeCreditoAcima360dias() != null && (resumoDoClienteTraduzido.getLimitesdeCreditoAcima360dias().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getLimitesdeCreditoAcima360dias())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}		
			cell1.setBorder(0);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Risco Indireto (I)", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Risco Indireto Vendor", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getRiscoIndiretoVendor() != null && (resumoDoClienteTraduzido.getRiscoIndiretoVendor().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getRiscoIndiretoVendor()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getRiscoIndiretoVendor() != null && (resumoDoClienteTraduzido.getRiscoIndiretoVendor().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(calcularPorcentagemValores(resumoDoClienteTraduzido.getRiscoTotal(), resumoDoClienteTraduzido.getRiscoIndiretoVendor())) + " %", normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}	
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Risco Total (F+G+H+I=J)", header));
			cell1.setBorder(0);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(10f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(3);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("Risco Total", subtitulo));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthLeft(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			if (resumoDoClienteTraduzido.getRiscoTotal() != null && (resumoDoClienteTraduzido.getRiscoTotal().compareTo(BigDecimal.ZERO) > 0)) { 
				cell1 = new PdfPCell(new Phrase(df.format(resumoDoClienteTraduzido.getRiscoTotal()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("--", normal));
			}
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("100 %", normal));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorTop(BaseColor.BLACK);
			cell1.setBorderWidthRight(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorBottom(BaseColor.BLACK);	
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(5f);
			cell1.setColspan(1);
			table.addCell(cell1);
			
			doc.add(table);
			
			this.pdfGerado = true;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"SCR: Ocorreu um problema ao gerar o PDF! " + e,
					""));
		} catch (Exception e) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"SCR: Ocorreu um problema ao gerar o PDF! " + e,
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

			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"SCR: PDF gerado com sucesso!",
							""));
		}
	}
	
	public BigDecimal calcularPorcentagemValores(BigDecimal valorBase, BigDecimal valorCampo) {
		BigDecimal porcentagem = BigDecimal.ZERO;
		
		if (valorBase.compareTo(BigDecimal.ZERO) > 0 && valorCampo.compareTo(BigDecimal.ZERO) > 0) {
			porcentagem = valorCampo.divide(valorBase, 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));
		}   
		
		return porcentagem;
	}

	public TransferenciasObservacoesIUGU getTransferenciasObservacoesIUGU() {
		return transferenciasObservacoesIUGU;
	}

	public void setTransferenciasObservacoesIUGU(TransferenciasObservacoesIUGU transferenciasObservacoesIUGU) {
		this.transferenciasObservacoesIUGU = transferenciasObservacoesIUGU;
	}

	public boolean isPdfGerado() {
		return pdfGerado;
	}

	public void setPdfGerado(boolean pdfGerado) {
		this.pdfGerado = pdfGerado;
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

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}
}