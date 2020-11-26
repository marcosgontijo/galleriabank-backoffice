package com.webnowbr.siscoat.cobranca.mb.directd;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.webnowbr.siscoat.cobranca.db.model.directd.Pendencias;
import com.webnowbr.siscoat.cobranca.db.model.directd.Processo1Grau;
import com.webnowbr.siscoat.cobranca.db.model.directd.Processo2Grau;
import com.webnowbr.siscoat.cobranca.db.model.directd.ProcessoDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.directd.ProcessoDetalhesDados;
import com.webnowbr.siscoat.cobranca.db.model.directd.ProcessoDetalhesPartes;
import com.webnowbr.siscoat.cobranca.db.model.directd.Processos;
import com.webnowbr.siscoat.cobranca.db.model.directd.TribunalJusticaSP;
import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;


@ManagedBean(name = "consultaTribunalJusticaSP")
@SessionScoped
public class ConsultaTribunalJusticaSP {

	/****
	 * 
	 * LIVE TOKEN
	 * 0d54a6b5fa28f6dc42ce76d0011952e3
	 */
	
	private String accessToken = "0d54a6b5fa28f6dc42ce76d0011952e3";

	private String cnpj;
	private String uf;
	private TribunalJusticaSP retornoConsultaTribunalJusticaSP;
	
	private boolean consultaGerada;

	public ConsultaTribunalJusticaSP() {

	}

	public String clearConsultaTribunalJusticaSP() {
		this.cnpj = "";
		this.uf = "SP";
		
		this.consultaGerada = false;
		
		clearPDF();

		return "/Atendimento/ConsultasDirectd/ConsultaTribunalJusticaSP.xhtml";
	}

	public void consultaTribunalJusticaSP() {
		/*
		 *   https://api.directd.com.br/consultas/tj/v1/consulta-tribunal-justica-processos?token={token}&uf={UF}&cnpj={cnpj}&nomeRazaoSoc={nomeRazaoSoc}
		 */

		clearPDF();
		FacesContext context = FacesContext.getCurrentInstance();

		this.retornoConsultaTribunalJusticaSP = new TribunalJusticaSP();

		int HTTP_COD_SUCESSO = 200;

		boolean valid = true;
		
		//String cnpjNumeros = this.cnpj.replace(".", "").replace("/", "").replace("-", "");

		URL myURL;
		try {

			myURL = new URL("https://api.directd.com.br/consultas/tj/v1/consulta-tribunal-justica-processos?token=" + this.accessToken + "&uf=" + this.uf + "&cnpj=" + this.cnpj);	
			
			if (valid) {
				HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("GET");
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
				myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				myURLConnection.setDoOutput(true);

				String erro = "";
				JSONObject myResponse = null;

				if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Consulta Tribunal Justiça SP: Erro ao realizar a consulta TribunalJusticaSP! (Código: " + myURLConnection.getResponseCode() + ")!", ""));

				} else {							
					myResponse = getJsonSucesso(myURLConnection.getInputStream());				

					if (myResponse.getString("Tipo").equals("Sucesso")) {
						//this.retornoConsultaTribunalJusticaSP.getInfoServico().setConsultaUid(myResponse.getString("ConsultaUid"));
						this.retornoConsultaTribunalJusticaSP.getInfoServico().setIdTipo(myResponse.getLong("IdTipo"));
						this.retornoConsultaTribunalJusticaSP.getInfoServico().setTipo(myResponse.getString("Tipo"));
						//this.retornoConsultaTribunalJusticaSP.getInfoServico().setMensagem(myResponse.getString("Mensagem"));
						//this.retornoConsultaTribunalJusticaSP.getInfoServico().setTempoExecucaoMs(myResponse.getLong("TempoExecucaoMs"));
						this.retornoConsultaTribunalJusticaSP.getInfoServico().setCustoTotalEmCreditos(myResponse.getLong("CustoTotalEmCreditos"));
						//this.retornoConsultaTribunalJusticaSP.getInfoServico().setSaldoEmCreditos(myResponse.getLong("SaldoEmCreditos"));
						this.retornoConsultaTribunalJusticaSP.getInfoServico().setApiVersion(myResponse.getString("ApiVersion"));
						
						JSONObject retorno = myResponse.getJSONObject("Retorno");
	
						this.retornoConsultaTribunalJusticaSP.setDataConsulta(converteDate(retorno.getString("DataConsulta")));

						/**
						 * PROCESSOS 1 GRAU
						 */
						JSONArray listaProcessos1Grau = retorno.getJSONArray("Processos1Grau");
						List<Processo1Grau> listListaProcessos1Grau = new ArrayList<Processo1Grau>();

						for (int j = 0; j < listaProcessos1Grau.length(); j++) {
							JSONObject objProcessos1Grau = listaProcessos1Grau.getJSONObject(j);
							Processo1Grau processos1GrauObj = new Processo1Grau();
							
							processos1GrauObj.setNomeForo(objProcessos1Grau.getString("NomeForo"));
							
							// LISTA PROCESSOS 
							JSONArray listaProcessos = objProcessos1Grau.getJSONArray("Processos");
							List<Processos> listListaProcessos = new ArrayList<Processos>();
							
							for (int k = 0; k < listaProcessos.length(); k++) {
								JSONObject objProcesso = listaProcessos.getJSONObject(k);
								Processos processoObj = new Processos();
								
								if (!objProcesso.isNull("Numero")) {
									processoObj.setNumero(objProcesso.getString("Numero"));
								}
								
								if (!objProcesso.isNull("UrlProcesso")) {
									processoObj.setUrlProcesso(objProcesso.getString("UrlProcesso"));
								}
								
								if (!objProcesso.isNull("Motivo")) {
									processoObj.setMotivo(objProcesso.getString("Motivo"));
								}
								
								if (!objProcesso.isNull("Exeqte")) {
									processoObj.setExeqte(objProcesso.getString("Exeqte"));
								}
								
								if (!objProcesso.isNull("Reqdo")) {
									processoObj.setReqdo(objProcesso.getString("Reqdo"));
								}
								
								if (!objProcesso.isNull("Exectdo")) {
									processoObj.setExectdo(objProcesso.getString("Exectdo"));
								}
								
								if (!objProcesso.isNull("RecebidoEm")) {
									processoObj.setRecebidoEm(objProcesso.getString("RecebidoEm"));
								}					
								
								ProcessoDetalhes processoDetalhesObj = new ProcessoDetalhes();
								
								// LISTA PROCESSOS DETALHES
								JSONObject objProcessoDetalhes = objProcesso.getJSONObject("ProcessoDetalhe");
								
								// LISTA PROCESSOS DETALHES DADOS
								JSONObject objProcessoDetalhesDados = objProcessoDetalhes.getJSONObject("Dados");								
								ProcessoDetalhesDados processoDadosObj = new ProcessoDetalhesDados();
								
								if (!objProcessoDetalhesDados.isNull("Numero")) {
									processoDadosObj.setNumero(objProcessoDetalhesDados.getString("Numero"));
								}	
								
								if (!objProcessoDetalhesDados.isNull("Classe")) {
									processoDadosObj.setClasse(objProcessoDetalhesDados.getString("Classe"));
								}	
								
								if (!objProcessoDetalhesDados.isNull("Assunto")) {
									processoDadosObj.setAssunto(objProcessoDetalhesDados.getString("Assunto"));
								}	
								
								if (!objProcessoDetalhesDados.isNull("LocalFisico")) {
									processoDadosObj.setLocalFisico(objProcessoDetalhesDados.getString("LocalFisico"));
								}	

								if (!objProcessoDetalhesDados.isNull("OutrosAssuntos")) {
									processoDadosObj.setOutrosAssuntos(objProcessoDetalhesDados.getString("OutrosAssuntos"));
								}	
								
								if (!objProcessoDetalhesDados.isNull("Distribuicao")) {
									processoDadosObj.setDistribuicao(objProcessoDetalhesDados.getString("Distribuicao"));
								}	
								
								if (!objProcessoDetalhesDados.isNull("Controle")) {
									processoDadosObj.setControle(objProcessoDetalhesDados.getString("Controle"));
								}	
								
								if (!objProcessoDetalhesDados.isNull("Juiz")) {
									processoDadosObj.setJuiz(objProcessoDetalhesDados.getString("Juiz"));
								}	
								
								if (!objProcessoDetalhesDados.isNull("OutrosNumeros")) {
									processoDadosObj.setOutrosNumeros(objProcessoDetalhesDados.getString("OutrosNumeros"));
								}	
								
								if (!objProcessoDetalhesDados.isNull("ValorAcao")) {
									processoDadosObj.setValorAcao(objProcessoDetalhesDados.getString("ValorAcao"));
								}	
								
								processoDetalhesObj.setProcessoDetalhesDados(processoDadosObj);
								
								// LISTA PROCESSOS DETALHES PARTES
								JSONArray listaProcessosDetalhesPartes = objProcessoDetalhes.getJSONArray("Partes");
								List<ProcessoDetalhesPartes> listListarocessoDetalhesPartes = new ArrayList<ProcessoDetalhesPartes>();
								
								for (int z = 0; z < listaProcessosDetalhesPartes.length(); z++) {
									JSONObject objProcessoDetalhesPartes = listaProcessosDetalhesPartes.getJSONObject(z);
									ProcessoDetalhesPartes processoPartesObj = new ProcessoDetalhesPartes();
								
									/*
									if (!objProcessoDetalhesPartes.isNull("Exeqte")) {
										processoPartesObj.setAdvogado(objProcessoDetalhesPartes.getString("Exeqte"));
									}	
									
									if (!objProcessoDetalhesPartes.isNull("Advogado")) {
										processoPartesObj.setExectdo(objProcessoDetalhesPartes.getString("Advogado"));
									}	
									
									if (!objProcessoDetalhesPartes.isNull("Reqdo")) {
										processoPartesObj.setExeqte(objProcessoDetalhesPartes.getString("Reqdo"));
									}	
									
									if (!objProcessoDetalhesPartes.isNull("Exectdo")) {
										processoPartesObj.setReqdo(objProcessoDetalhesPartes.getString("Exectdo"));
									}	
									*/
									if (!objProcessoDetalhesPartes.isNull("Tipo")) {
										processoPartesObj.setTipo(objProcessoDetalhesPartes.getString("Tipo"));
									}
									
									if (!objProcessoDetalhesPartes.isNull("NomeParte")) {
										processoPartesObj.setNomeParte(objProcessoDetalhesPartes.getString("NomeParte"));
									}
									
									listListarocessoDetalhesPartes.add(processoPartesObj);
								}
								
								processoDetalhesObj.setProcessoDetalhesPartes(listListarocessoDetalhesPartes);

								processoObj.setProcessoDetalhes(processoDetalhesObj);

								listListaProcessos.add(processoObj);
						}
							
							processos1GrauObj.setProcessos(listListaProcessos);
							
							listListaProcessos1Grau.add(processos1GrauObj);
						}	
						
						this.retornoConsultaTribunalJusticaSP.setProcesso1Grau(listListaProcessos1Grau);
						
						/**
						 * PROCESSOS 1 GRAU
						 */
						JSONArray listaProcessos2Grau = retorno.getJSONArray("Processos2Grau");
						List<Processo2Grau> listListaProcessos2Grau = new ArrayList<Processo2Grau>();

						for (int j = 0; j < listaProcessos2Grau.length(); j++) {
							JSONObject objProcessos2Grau = listaProcessos2Grau.getJSONObject(j);
							Processo2Grau processos2GrauObj = new Processo2Grau();
							
							processos2GrauObj.setNomeForo(objProcessos2Grau.getString("NomeForo"));
							
							// LISTA PROCESSOS 
							JSONArray listaProcessos = objProcessos2Grau.getJSONArray("Processos");
							List<Processos> listListaProcessos = new ArrayList<Processos>();
							
							for (int k = 0; k < listaProcessos.length(); k++) {
								JSONObject objProcesso = listaProcessos.getJSONObject(k);
								Processos processoObj = new Processos();
								
								if (!objProcesso.isNull("Numero")) {
									processoObj.setNumero(objProcesso.getString("Numero"));
								}
								
								if (!objProcesso.isNull("UrlProcesso")) {
									processoObj.setUrlProcesso(objProcesso.getString("UrlProcesso"));
								}
								
								if (!objProcesso.isNull("Motivo")) {
									processoObj.setMotivo(objProcesso.getString("Motivo"));
								}
								
								if (!objProcesso.isNull("Exeqte")) {
									processoObj.setExeqte(objProcesso.getString("Exeqte"));
								}
								
								if (!objProcesso.isNull("Reqdo")) {
									processoObj.setReqdo(objProcesso.getString("Reqdo"));
								}
								
								if (!objProcesso.isNull("Exectdo")) {
									processoObj.setExectdo(objProcesso.getString("Exectdo"));
								}
								
								if (!objProcesso.isNull("RecebidoEm")) {
									processoObj.setRecebidoEm(objProcesso.getString("RecebidoEm"));
								}					
								
								ProcessoDetalhes processoDetalhesObj = new ProcessoDetalhes();
								
								// LISTA PROCESSOS DETALHES
								JSONObject objProcessoDetalhes = objProcesso.getJSONObject("ProcessoDetalhe");
								
								// LISTA PROCESSOS DETALHES DADOS
								JSONObject objProcessoDetalhesDados = objProcessoDetalhes.getJSONObject("Dados");								
								ProcessoDetalhesDados processoDadosObj = new ProcessoDetalhesDados();
								
								if (!objProcessoDetalhesDados.isNull("Numero")) {
									processoDadosObj.setNumero(objProcessoDetalhesDados.getString("Numero"));
								}	
								
								if (!objProcessoDetalhesDados.isNull("Classe")) {
									processoDadosObj.setClasse(objProcessoDetalhesDados.getString("Classe"));
								}	
								
								if (!objProcessoDetalhesDados.isNull("Assunto")) {
									processoDadosObj.setAssunto(objProcessoDetalhesDados.getString("Assunto"));
								}	
								
								if (!objProcessoDetalhesDados.isNull("LocalFisico")) {
									processoDadosObj.setLocalFisico(objProcessoDetalhesDados.getString("LocalFisico"));
								}	

								if (!objProcessoDetalhesDados.isNull("OutrosAssuntos")) {
									processoDadosObj.setOutrosAssuntos(objProcessoDetalhesDados.getString("OutrosAssuntos"));
								}	
								
								if (!objProcessoDetalhesDados.isNull("Distribuicao")) {
									processoDadosObj.setDistribuicao(objProcessoDetalhesDados.getString("Distribuicao"));
								}	
								
								if (!objProcessoDetalhesDados.isNull("Controle")) {
									processoDadosObj.setControle(objProcessoDetalhesDados.getString("Controle"));
								}	
								
								if (!objProcessoDetalhesDados.isNull("Juiz")) {
									processoDadosObj.setJuiz(objProcessoDetalhesDados.getString("Juiz"));
								}	
								
								if (!objProcessoDetalhesDados.isNull("OutrosNumeros")) {
									processoDadosObj.setOutrosNumeros(objProcessoDetalhesDados.getString("OutrosNumeros"));
								}	
								
								if (!objProcessoDetalhesDados.isNull("ValorAcao")) {
									processoDadosObj.setValorAcao(objProcessoDetalhesDados.getString("ValorAcao"));
								}	
								
								processoDetalhesObj.setProcessoDetalhesDados(processoDadosObj);
								
								// LISTA PROCESSOS DETALHES PARTES
								JSONArray listaProcessosDetalhesPartes = objProcessoDetalhes.getJSONArray("Partes");
								List<ProcessoDetalhesPartes> listListarocessoDetalhesPartes = new ArrayList<ProcessoDetalhesPartes>();
								
								for (int z = 0; z < listaProcessosDetalhesPartes.length(); z++) {
									JSONObject objProcessoDetalhesPartes = listaProcessosDetalhesPartes.getJSONObject(z);
									ProcessoDetalhesPartes processoPartesObj = new ProcessoDetalhesPartes();
								
									/*
									if (!objProcessoDetalhesPartes.isNull("Exeqte")) {
										processoPartesObj.setAdvogado(objProcessoDetalhesPartes.getString("Exeqte"));
									}	
									
									if (!objProcessoDetalhesPartes.isNull("Advogado")) {
										processoPartesObj.setExectdo(objProcessoDetalhesPartes.getString("Advogado"));
									}	
									
									if (!objProcessoDetalhesPartes.isNull("Reqdo")) {
										processoPartesObj.setExeqte(objProcessoDetalhesPartes.getString("Reqdo"));
									}	
									
									if (!objProcessoDetalhesPartes.isNull("Exectdo")) {
										processoPartesObj.setReqdo(objProcessoDetalhesPartes.getString("Exectdo"));
									}	
									*/
									if (!objProcessoDetalhesPartes.isNull("Tipo")) {
										processoPartesObj.setTipo(objProcessoDetalhesPartes.getString("Tipo"));
									}
									
									if (!objProcessoDetalhesPartes.isNull("NomeParte")) {
										processoPartesObj.setNomeParte(objProcessoDetalhesPartes.getString("NomeParte"));
									}
									
									listListarocessoDetalhesPartes.add(processoPartesObj);
								}
								
								processoDetalhesObj.setProcessoDetalhesPartes(listListarocessoDetalhesPartes);

								processoObj.setProcessoDetalhes(processoDetalhesObj);

								listListaProcessos.add(processoObj);
						}
							
							processos2GrauObj.setProcessos(listListaProcessos);
							
							listListaProcessos2Grau.add(processos2GrauObj);
						}	
						
						this.retornoConsultaTribunalJusticaSP.setProcesso2Grau(listListaProcessos2Grau);
						
						context.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_INFO, "Consulta Tribunal Justiça SP: Consulta " + this.cnpj + " efetuada com sucesso!", ""));
						
						this.consultaGerada = true;
					} else {
						context.addMessage(null, new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "Consulta Tribunal Justiça SP: Erro ao realizar a consulta! (Erro: " + myResponse.getString("Tipo") + " | Mensagem: " + myResponse.getString("Mensagem") + ").", ""));
					}
				}

				myURLConnection.disconnect();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Date converteDate(String dateStr) {
		Date retorno = new Date();
		
		//2019-08-27T13:38:10.1420086-03:00
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		DateFormat formatterOnlyDate = new SimpleDateFormat("dd/MM/yyyy");
		
		try {
			String dateParsedStr = dateStr.substring(8, 10) + "/" + dateStr.substring(5, 7) + "/" + dateStr.substring(0, 4) + 
					" " + dateStr.substring(11, 19) ;
			
			try {
				retorno = ((java.util.Date)formatter.parse(dateParsedStr));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			return retorno;
		} catch (StringIndexOutOfBoundsException e) {
			try {
				retorno = ((java.util.Date) formatterOnlyDate.parse(dateStr));
			} catch (ParseException e1) {
				retorno = gerarDataHoje();
				
				return retorno;
			}
			return retorno;
		}
	}
	
	/**
	 * GERA A DATA DE HOJE
	 * @return
	 */
	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();  
		Locale locale = new Locale("pt", "BR");  
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
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

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public TribunalJusticaSP getRetornoConsultaTribunalJusticaSP() {
		return retornoConsultaTribunalJusticaSP;
	}

	public void setRetornoConsultaTribunalJusticaSP(TribunalJusticaSP retornoConsultaTribunalJusticaSP) {
		this.retornoConsultaTribunalJusticaSP = retornoConsultaTribunalJusticaSP;
	}
	
	/****
	 * PDF
	 */
	private boolean pdfGerado;
	private String pathPDF;
	private String nomePDF;
	private StreamedContent file;
	
	public void clearPDF() {
		this.pdfGerado = false;
		this.pathPDF = "";
		this.nomePDF = "";
		this.file = null;
	}

	public void gerarPDF() {
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

			doc = new Document(PageSize.A4.rotate(), 10, 10, 10, 10);
			
			String cnpjSemPontos = this.cnpj.replace(".", "").replace("/", "").replace("-", "");
			
			this.nomePDF = "Consulta - CND Federeal - " + cnpjSemPontos + ".pdf";
			this.pathPDF = pDao.findByFilter("nome", "CONSULTAS_DIRECTD").get(0).getValorString();

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
			PdfPTable table = new PdfPTable(new float[] { 0.2f, 0.8f, 0.2f, 0.8f});
			table.setWidthPercentage(100.0f); 

			PdfPCell cell1 = new PdfPCell(new Phrase("Consulta TJSP", header));
			cell1.setBorder(0);
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(4);
			table.addCell(cell1);
			
			cell1 = new PdfPCell(new Phrase("ID Consulta: ", titulo));
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
			
			cell1 = new PdfPCell(new Phrase(this.retornoConsultaTribunalJusticaSP.getInfoServico().getConsultaUid(), normal));
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
			
			cell1 = new PdfPCell(new Phrase("Data Consulta: ", titulo));
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
			
			if (this.retornoConsultaTribunalJusticaSP.getDataConsulta() != null) {
				cell1 = new PdfPCell(new Phrase(sdfDataRel.format(this.retornoConsultaTribunalJusticaSP.getDataConsulta()), normal));
			} else {
				cell1 = new PdfPCell(new Phrase("", normal));
			}
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
			
			cell1 = new PdfPCell(new Phrase("Lista Processos 1 Grau", header));
			cell1.setBorder(0);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(4);
			table.addCell(cell1);

			for (Processo1Grau processo1Grau : this.retornoConsultaTribunalJusticaSP.getProcesso1Grau()) {
				cell1 = new PdfPCell(new Phrase("Foro: " + processo1Grau.getNomeForo(), header));
				cell1.setBorder(0);	
				cell1.setPaddingLeft(8f);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(15f);
				cell1.setColspan(4);
				table.addCell(cell1);
				
				for (Processos processo : processo1Grau.getProcessos()) {
					cell1 = new PdfPCell(new Phrase("Número: ", titulo));
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
					
					cell1 = new PdfPCell(new Phrase(processo.getNumero(), normal));
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
					
					cell1 = new PdfPCell(new Phrase("URL Processo: ", titulo));
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
					
					cell1 = new PdfPCell(new Phrase(processo.getUrlProcesso(), normal));
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
					
					cell1 = new PdfPCell(new Phrase("Motivo: ", titulo));
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
					
					cell1 = new PdfPCell(new Phrase(processo.getMotivo(), normal));
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
					
					cell1 = new PdfPCell(new Phrase("", titulo));
					cell1.setBorder(0);
					cell1.setPaddingLeft(8f);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingTop(5f);
					cell1.setPaddingBottom(15f);
					cell1.setColspan(2);
					table.addCell(cell1); 
					
					if (processo.getProcessoDetalhes() != null) {
						cell1 = new PdfPCell(new Phrase("Classe: ", titulo));
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
						
						cell1 = new PdfPCell(new Phrase(processo.getProcessoDetalhes().getProcessoDetalhesDados().getClasse(), normal));
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
						
						cell1 = new PdfPCell(new Phrase("Assunto: ", titulo));
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
						
						cell1 = new PdfPCell(new Phrase(processo.getProcessoDetalhes().getProcessoDetalhesDados().getAssunto(), normal));
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
						
						cell1 = new PdfPCell(new Phrase("Local Físico: ", titulo));
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
						
						cell1 = new PdfPCell(new Phrase(processo.getProcessoDetalhes().getProcessoDetalhesDados().getLocalFisico(), normal));
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
						
						cell1 = new PdfPCell(new Phrase("Outros Assuntos: ", titulo));
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
						
						cell1 = new PdfPCell(new Phrase(processo.getProcessoDetalhes().getProcessoDetalhesDados().getOutrosAssuntos(), normal));
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
						
						cell1 = new PdfPCell(new Phrase("Distribuição: ", titulo));
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
						
						cell1 = new PdfPCell(new Phrase(processo.getProcessoDetalhes().getProcessoDetalhesDados().getDistribuicao(), normal));
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
						
						cell1 = new PdfPCell(new Phrase("Controle: ", titulo));
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
						
						cell1 = new PdfPCell(new Phrase(processo.getProcessoDetalhes().getProcessoDetalhesDados().getControle(), normal));
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
						
						cell1 = new PdfPCell(new Phrase("Juiz: ", titulo));
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
						
						cell1 = new PdfPCell(new Phrase(processo.getProcessoDetalhes().getProcessoDetalhesDados().getJuiz(), normal));
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
						
						cell1 = new PdfPCell(new Phrase("Outros Números: ", titulo));
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
						
						cell1 = new PdfPCell(new Phrase(processo.getProcessoDetalhes().getProcessoDetalhesDados().getOutrosNumeros(), normal));
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
						
						cell1 = new PdfPCell(new Phrase("Valor Ação: ", titulo));
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
						
						cell1 = new PdfPCell(new Phrase(processo.getProcessoDetalhes().getProcessoDetalhesDados().getValorAcao(), normal));
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
						
						cell1 = new PdfPCell(new Phrase("Partes: " + processo1Grau.getNomeForo(), header));
						cell1.setBorder(0);	
						cell1.setPaddingLeft(8f);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingTop(5f);
						cell1.setPaddingBottom(15f);
						cell1.setColspan(4);
						table.addCell(cell1);
						
						for (ProcessoDetalhesPartes partes : processo.getProcessoDetalhes().getProcessoDetalhesPartes()) {
							cell1 = new PdfPCell(new Phrase("Tipo: ", titulo));
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
							
							cell1 = new PdfPCell(new Phrase(partes.getTipo(), normal));
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
							
							cell1 = new PdfPCell(new Phrase("Partes: ", titulo));
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
							
							cell1 = new PdfPCell(new Phrase(partes.getNomeParte(), normal));
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
						}
					} else {
						cell1 = new PdfPCell(new Phrase("", titulo));
						cell1.setBorder(0);
						cell1.setPaddingLeft(8f);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingTop(5f);
						cell1.setPaddingBottom(15f);
						cell1.setColspan(2);
						table.addCell(cell1); 
					}
				}
			}
			
			cell1 = new PdfPCell(new Phrase("Lista Processos 2 Grau", header));
			cell1.setBorder(0);
			cell1.setBorderWidthBottom(1);
			cell1.setBorderColorLeft(BaseColor.BLACK);
			cell1.setBorderWidthTop(1);
			cell1.setBorderColorRight(BaseColor.BLACK);	
			cell1.setPaddingLeft(8f);
			cell1.setBackgroundColor(BaseColor.WHITE);
			cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell1.setUseBorderPadding(true);
			cell1.setPaddingTop(5f);
			cell1.setPaddingBottom(15f);
			cell1.setColspan(4);
			table.addCell(cell1);

			for (Processo2Grau processo2Grau : this.retornoConsultaTribunalJusticaSP.getProcesso2Grau()) {
				cell1 = new PdfPCell(new Phrase("Foro: " + processo2Grau.getNomeForo(), header));
				cell1.setBorder(0);	
				cell1.setPaddingLeft(8f);
				cell1.setBackgroundColor(BaseColor.WHITE);
				cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell1.setUseBorderPadding(true);
				cell1.setPaddingTop(5f);
				cell1.setPaddingBottom(15f);
				cell1.setColspan(4);
				table.addCell(cell1);
				
				for (Processos processo : processo2Grau.getProcessos()) {
					cell1 = new PdfPCell(new Phrase("Número: ", titulo));
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
					
					cell1 = new PdfPCell(new Phrase(processo.getNumero(), normal));
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
					
					cell1 = new PdfPCell(new Phrase("URL Processo: ", titulo));
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
					
					cell1 = new PdfPCell(new Phrase(processo.getUrlProcesso(), normal));
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
					
					cell1 = new PdfPCell(new Phrase("Motivo: ", titulo));
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
					
					cell1 = new PdfPCell(new Phrase(processo.getMotivo(), normal));
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
					
					cell1 = new PdfPCell(new Phrase("", titulo));
					cell1.setBorder(0);
					cell1.setPaddingLeft(8f);
					cell1.setBackgroundColor(BaseColor.WHITE);
					cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell1.setUseBorderPadding(true);
					cell1.setPaddingTop(5f);
					cell1.setPaddingBottom(15f);
					cell1.setColspan(2);
					table.addCell(cell1); 
					
					if (processo.getProcessoDetalhes() != null) {
						cell1 = new PdfPCell(new Phrase("Classe: ", titulo));
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
						
						cell1 = new PdfPCell(new Phrase(processo.getProcessoDetalhes().getProcessoDetalhesDados().getClasse(), normal));
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
						
						cell1 = new PdfPCell(new Phrase("Assunto: ", titulo));
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
						
						cell1 = new PdfPCell(new Phrase(processo.getProcessoDetalhes().getProcessoDetalhesDados().getAssunto(), normal));
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
						
						cell1 = new PdfPCell(new Phrase("Local Físico: ", titulo));
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
						
						cell1 = new PdfPCell(new Phrase(processo.getProcessoDetalhes().getProcessoDetalhesDados().getLocalFisico(), normal));
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
						
						cell1 = new PdfPCell(new Phrase("Outros Assuntos: ", titulo));
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
						
						cell1 = new PdfPCell(new Phrase(processo.getProcessoDetalhes().getProcessoDetalhesDados().getOutrosAssuntos(), normal));
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
						
						cell1 = new PdfPCell(new Phrase("Distribuição: ", titulo));
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
						
						cell1 = new PdfPCell(new Phrase(processo.getProcessoDetalhes().getProcessoDetalhesDados().getDistribuicao(), normal));
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
						
						cell1 = new PdfPCell(new Phrase("Controle: ", titulo));
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
						
						cell1 = new PdfPCell(new Phrase(processo.getProcessoDetalhes().getProcessoDetalhesDados().getControle(), normal));
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
						
						cell1 = new PdfPCell(new Phrase("Juiz: ", titulo));
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
						
						cell1 = new PdfPCell(new Phrase(processo.getProcessoDetalhes().getProcessoDetalhesDados().getJuiz(), normal));
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
						
						cell1 = new PdfPCell(new Phrase("Outros Números: ", titulo));
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
						
						cell1 = new PdfPCell(new Phrase(processo.getProcessoDetalhes().getProcessoDetalhesDados().getOutrosNumeros(), normal));
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
						
						cell1 = new PdfPCell(new Phrase("Valor Ação: ", titulo));
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
						
						cell1 = new PdfPCell(new Phrase(processo.getProcessoDetalhes().getProcessoDetalhesDados().getValorAcao(), normal));
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
						
						cell1 = new PdfPCell(new Phrase("Partes: " + processo2Grau.getNomeForo(), header));
						cell1.setBorder(0);	
						cell1.setPaddingLeft(8f);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingTop(5f);
						cell1.setPaddingBottom(15f);
						cell1.setColspan(4);
						table.addCell(cell1);
						
						for (ProcessoDetalhesPartes partes : processo.getProcessoDetalhes().getProcessoDetalhesPartes()) {
							cell1 = new PdfPCell(new Phrase("Tipo: ", titulo));
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
							
							cell1 = new PdfPCell(new Phrase(partes.getTipo(), normal));
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
							
							cell1 = new PdfPCell(new Phrase("Partes: ", titulo));
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
							
							cell1 = new PdfPCell(new Phrase(partes.getNomeParte(), normal));
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
						}
					} else {
						cell1 = new PdfPCell(new Phrase("", titulo));
						cell1.setBorder(0);
						cell1.setPaddingLeft(8f);
						cell1.setBackgroundColor(BaseColor.WHITE);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell1.setUseBorderPadding(true);
						cell1.setPaddingTop(5f);
						cell1.setPaddingBottom(15f);
						cell1.setColspan(2);
						table.addCell(cell1); 
					}
				}
			}

			doc.add(table);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "PDF TJSP: Este contrato está aberto por algum outro programa, por favor, feche-o e tente novamente!" + e, ""));
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "PDF TJSP: Ocorreu um problema ao gerar o PDF!" + e, ""));
		} finally {
			this.pdfGerado = true;

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

	public boolean isConsultaGerada() {
		return consultaGerada;
	}

	public void setConsultaGerada(boolean consultaGerada) {
		this.consultaGerada = consultaGerada;
	}
}