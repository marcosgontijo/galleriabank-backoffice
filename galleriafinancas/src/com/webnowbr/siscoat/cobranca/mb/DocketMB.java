package com.webnowbr.siscoat.cobranca.mb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;

import com.webnowbr.siscoat.cobranca.db.model.BoletoKobana;
import com.webnowbr.siscoat.cobranca.db.model.CcbParticipantes;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.DocumentosDocket;
import com.webnowbr.siscoat.cobranca.db.model.DocumentosPagadorDocket;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesDao;
import com.webnowbr.siscoat.cobranca.db.op.DocumentosDocketDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.SiscoatConstants;

import br.com.caelum.stella.boleto.Pagador;

@ManagedBean(name = "docketMB")
@SessionScoped
public class DocketMB {

	/****
	 * Token de Segurança
	 * 
	 * 
		Login: galleria-bank.api
		Senha: 5TM*sgZKJ3hoh@J
		
		https://sandbox-saas.docket.com.br
	 */
	
	private String urlHomologacao = "https://sandbox-saas.docket.com.br";
	private String kitIdGalleria = "02859d48-ff2a-45a4-922b-d6b9842affcc";
	private String kitNomeGalleria = "1 - GALLERIA BANK";
	private String login = "galleria-bank.api";
	private String senha = "5TM*sgZKJ3hoh@J";
	private String organizacao_url = "galleria-bank";	
	private String tokenLogin;
	
	private List<PagadorRecebedor> listRecebedorPagador = new ArrayList<PagadorRecebedor>();	//lista de consulta
	private List<ContratoCobranca> listaContratosConsultar = new ArrayList<ContratoCobranca>();	//lista de consulta
	private PagadorRecebedor selectedPagadorGenerico;
	private PagadorRecebedor selectedPagadorDocumentos;
	
	String updatePagadorRecebedor = ":form";
	private ContratoCobranca objetoContratoCobranca = new ContratoCobranca();
	private List<PagadorRecebedor> listaPagador = new ArrayList<PagadorRecebedor>();
	private List<DocumentosDocket> listaDococumentosDocket = new ArrayList<DocumentosDocket>(); 
	private List<DocumentosPagadorDocket> listaDococumentosPagador = new ArrayList<DocumentosPagadorDocket>(); 
		
	public void login() {		
		try {		
			FacesContext context = FacesContext.getCurrentInstance();
			int HTTP_COD_SUCESSO = 200;

			URL myURL;	
			
			if(SiscoatConstants.DEV) {
				myURL = new URL(urlHomologacao + "/api/v2/auth/login");
			} else {
				myURL = new URL(urlHomologacao + "/api/v2/auth/login");
			}

			JSONObject jsonObj = new JSONObject();
			
			jsonObj.put("login", login);
			jsonObj.put("senha", senha);
			
			byte[] postDataBytes = jsonObj.toString().getBytes();

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
		     
			myURLConnection.setDoOutput(true);
			//myURLConnection.getOutputStream().write(postDataBytes);
			
			try(OutputStream os = myURLConnection.getOutputStream()) {
			    byte[] input = jsonObj.toString().getBytes("utf-8");
			    os.write(input, 0, input.length);			
			}
	
			JSONObject myResponse = null;
			int status = myURLConnection.getResponseCode();
			
			myResponse = getJSONSucesso(myURLConnection.getInputStream());			
			
			this.tokenLogin = "";
			
			if (status == HTTP_COD_SUCESSO) {
				if (myResponse.has("token")) {					
					if (!myResponse.isNull("token")) {
						this.tokenLogin = myResponse.getString("token");
					}
				}
			} else {
				if (status == 401) {
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"[Docket - Login] Falha de autenticação. Token inválido!", ""));
				}
				if (status == 400) {
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"[Docket - Login] Erro no login.", ""));
				}
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"[Docket - Login] Erro não conhecido!", ""));
			}
			
			System.out.println(this.tokenLogin);
						
			myURLConnection.disconnect();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public void criarPedido() {	
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			int HTTP_COD_SUCESSO = 200;
			URL myURL;
			if(SiscoatConstants.DEV) {
				myURL = new URL(urlHomologacao + "/api/v2/auth/login");
			}			
			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setDoOutput(true);		
			
			JSONObject myResponse = null;
			JSONObject jsonWhatsApp = getBodyJsonPedido(listaPagador);					
			try (OutputStream os = myURLConnection.getOutputStream()) {
				byte[] input = jsonWhatsApp.toString().getBytes("utf-8");
				os.write(input, 0, input.length);
			}
						
			/*try(BufferedReader br = new BufferedReader(
			  new InputStreamReader(myURLConnection.getInputStream(), "utf-8"))) {
			    StringBuilder response = new StringBuilder();
			    String responseLine = null;
			    while ((responseLine = br.readLine()) != null) {
			        response.append(responseLine.trim());
			    }
			    System.out.println(response.toString());
			}*/
								
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Docket: Falha  (Cod: " + myURLConnection.getResponseCode() + ")",""));
			} else {				
				myResponse = getJSONSucesso(myURLConnection.getInputStream());
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
		
	public JSONObject getBodyJsonPedido(List<PagadorRecebedor> listaPagador) {	
		JSONObject jsonDocketBodyPedido = new JSONObject();	
		jsonDocketBodyPedido.put("pedido", getJsonPedido(listaPagador));
		return jsonDocketBodyPedido;
	}
	
	public JSONObject getJsonPedido(List<PagadorRecebedor> listaPagador) {	
		JSONArray jsonDocumentosArray = new JSONArray();
		for(PagadorRecebedor pagador : listaPagador) {
			for(DocumentosPagadorDocket documentos : pagador.getDocumentosDocket()) {
				jsonDocumentosArray.put(getJsonDocumentos(pagador, documentos));
			}	
		}

		JSONObject jsonDocketPedido = new JSONObject();			
		String nomePedido = objetoContratoCobranca.getNumeroContrato() + " - " + objetoContratoCobranca.getPagador().getNome();
		//jsonDocketPedido = new JSONObject();		
		jsonDocketPedido.put("lead", nomePedido);
		jsonDocketPedido.put("documentos", jsonDocumentosArray);

		return jsonDocketPedido;
	}
	
	public JSONObject getJsonDocumentos(PagadorRecebedor pagador, DocumentosPagadorDocket documentosPagador) {	
		//PagadorRecebedor pagador = new PagadorRecebedor();
		JSONObject jsonDocketDocumentos = new JSONObject();	
		DocumentosDocket documento = documentosPagador.getDocumentoDocket();
		String estadoId = documentosPagador.getEstadoId();
		String cidadeId = documentosPagador.getCidadeId();
		//jsonDocketDocumentos = new JSONObject();
		jsonDocketDocumentos.put("documentKitId", documento.getDocumentKitId());
		jsonDocketDocumentos.put("produtoId", documento.getProdutoId());
		jsonDocketDocumentos.put("kitId", kitIdGalleria);
		jsonDocketDocumentos.put("kitNome", kitNomeGalleria);
		jsonDocketDocumentos.put("documentoNome", documento.getDocumentoNome());
		if(!CommonsUtil.semValor(pagador.getCpf())) {
			jsonDocketDocumentos.put("titularTipo", "PESSOA_FISICA"); 
		} else {
			jsonDocketDocumentos.put("titularTipo", "PESSOA_JURIDICA"); 
		}
		jsonDocketDocumentos.put("campos", getJsonCampos(pagador, estadoId, cidadeId));
		
		return jsonDocketDocumentos;
	}
	
	public JSONObject getJsonCampos(PagadorRecebedor pagador, String estadoId, String cidadeId) {	
		JSONObject jsonDocketCampos = new JSONObject();				
		//jsonDocketCampos = new JSONObject();		
		if(!CommonsUtil.semValor(pagador.getCpf())) {
			jsonDocketCampos.put("nomeCompleto", pagador.getNome());
			jsonDocketCampos.put("cpf", pagador.getCpf());
			jsonDocketCampos.put("nomeMae", pagador.getNomeMae());
			jsonDocketCampos.put("rg", pagador.getRg());
			jsonDocketCampos.put("dataNascimento", pagador.getDtNascimento()); //2003-01-10T02:00:00.000+0000
		} else {
			jsonDocketCampos.put("razaoSocial", pagador.getNome());
			jsonDocketCampos.put("cnpj", pagador.getCnpj());
		}
		jsonDocketCampos.put("cidade", cidadeId);
		jsonDocketCampos.put("estado", estadoId);
		
		return jsonDocketCampos;
	}
	
	/***
	 * 
	 * PARSE DO RETORNO SUCESSO
	 * 
	 * @param inputStream
	 * @return
	 */
	public JSONObject getJSONSucesso(InputStream inputStream) {
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
	
	public String clearFieldsDocket() {
		clearContratoCobranca();
		listaPagador = new ArrayList<PagadorRecebedor>();
		listaDococumentosPagador = new ArrayList<DocumentosPagadorDocket>(); 
		listaDococumentosDocket = new ArrayList<DocumentosDocket>(); 
		
		DocumentosDocketDao docDao = new DocumentosDocketDao();	
		listaDococumentosDocket = docDao.findAll();

		for(DocumentosDocket doc : listaDococumentosDocket) {
			DocumentosPagadorDocket docPagador = new DocumentosPagadorDocket();
			docPagador.setDocumentoDocket(doc);
			listaDococumentosPagador.add(docPagador);
		}		
		return "/Atendimento/Cobranca/Docket.xhtml";
	}
	
	public void clearDialogDoc(PagadorRecebedor pagador) {
		selectedPagadorDocumentos = pagador;
		DocumentosDocketDao docDao = new DocumentosDocketDao();	
		listaDococumentosDocket = new ArrayList<DocumentosDocket>();
		listaDococumentosDocket = docDao.findAll();
	}
	
	public void adicionaDoc(DocumentosDocket doc) {
		DocumentosPagadorDocket docPagador = new DocumentosPagadorDocket(doc);
		selectedPagadorDocumentos.getDocumentosDocket().add(docPagador);
		selectedPagadorDocumentos = new PagadorRecebedor();
	}

	public void pesquisaContratoCobranca() {
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		listaContratosConsultar = new ArrayList<ContratoCobranca>();
		this.listaContratosConsultar = cDao.consultaContratosDocket();
	}	
	
	public void pesquisaPagadorRecebedor() {
		PagadorRecebedorDao pDao = new PagadorRecebedorDao();
		listRecebedorPagador = new ArrayList<PagadorRecebedor>();
		this.listRecebedorPagador = pDao.getPagadoresRecebedores();
		selectedPagadorGenerico = new PagadorRecebedor();
	}
	
	public void clearContratoCobranca() {
		this.objetoContratoCobranca = new ContratoCobranca();
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		listaContratosConsultar = new ArrayList<ContratoCobranca>();
		this.listaContratosConsultar = cDao.consultaContratosDocket();
	}
	
	public void populateSelectedContratoCobranca() {
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		ContratoCobranca contrato = new ContratoCobranca();
		contrato = cDao.findById(this.getObjetoContratoCobranca().getId());
		if(CommonsUtil.semValor(this.objetoContratoCobranca)){
			this.objetoContratoCobranca = contrato;
		}
		adiconarDocumentospagador(contrato.getPagador());
		listaPagador.add(contrato.getPagador());
	}
	
	public void populateSelectedPagadorRecebedor() {
		PagadorRecebedorDao pDao = new PagadorRecebedorDao();
		selectedPagadorGenerico = pDao.findById(selectedPagadorGenerico.getId());
		adiconarDocumentospagador(selectedPagadorGenerico);
		listaPagador.add(selectedPagadorGenerico);
		selectedPagadorGenerico = new PagadorRecebedor();
	}
	
	public void removeDoc(PagadorRecebedor pagador, DocumentosPagadorDocket doc) {
		pagador.getDocumentosDocket().remove(doc);
	}
	
	public void removerPessoa(PagadorRecebedor pagador) {
		pagador.getDocumentosDocket().clear();
		listaPagador.remove(pagador);
	}
	
	public void adiconarDocumentospagador(PagadorRecebedor pagador) {
		DocumentosDocketDao docDao = new DocumentosDocketDao();
		List<DocumentosDocket> listaDocs = docDao.findAll();
		if(CommonsUtil.semValor(pagador.getDocumentosDocket())) {
			for(DocumentosDocket doc : listaDocs) {
				DocumentosPagadorDocket docPagador = new DocumentosPagadorDocket(doc);
				pagador.getDocumentosDocket().add(docPagador);
			}
		}		
	}
	
	public void clearPagadorRecebedor() {
		this.selectedPagadorGenerico = new PagadorRecebedor();
	}

	public List<ContratoCobranca> getListaContratosConsultar() {
		return listaContratosConsultar;
	}

	public void setListaContratosConsultar(List<ContratoCobranca> listaContratosConsultar) {
		this.listaContratosConsultar = listaContratosConsultar;
	}

	public List<PagadorRecebedor> getListaPagador() {
		return listaPagador;
	}

	public void setListaPagador(List<PagadorRecebedor> listaPagador) {
		this.listaPagador = listaPagador;
	}

	public ContratoCobranca getObjetoContratoCobranca() {
		return objetoContratoCobranca;
	}

	public void setObjetoContratoCobranca(ContratoCobranca objetoContratoCobranca) {
		this.objetoContratoCobranca = objetoContratoCobranca;
	}

	public List<PagadorRecebedor> getListRecebedorPagador() {
		return listRecebedorPagador;
	}

	public void setListRecebedorPagador(List<PagadorRecebedor> listRecebedorPagador) {
		this.listRecebedorPagador = listRecebedorPagador;
	}

	public PagadorRecebedor getSelectedPagadorGenerico() {
		return selectedPagadorGenerico;
	}

	public void setSelectedPagadorGenerico(PagadorRecebedor selectedPagadorGenerico) {
		this.selectedPagadorGenerico = selectedPagadorGenerico;
	}

	public String getUpdatePagadorRecebedor() {
		return updatePagadorRecebedor;
	}

	public void setUpdatePagadorRecebedor(String updatePagadorRecebedor) {
		this.updatePagadorRecebedor = updatePagadorRecebedor;
	}

	public List<DocumentosDocket> getListaDococumentosDocket() {
		return listaDococumentosDocket;
	}

	public void setListaDococumentosDocket(List<DocumentosDocket> listaDococumentosDocket) {
		this.listaDococumentosDocket = listaDococumentosDocket;
	}

	public List<DocumentosPagadorDocket> getListaDococumentosPagador() {
		return listaDococumentosPagador;
	}

	public void setListaDococumentosPagador(List<DocumentosPagadorDocket> listaDococumentosPagador) {
		this.listaDococumentosPagador = listaDococumentosPagador;
	}

	public PagadorRecebedor getSelectedPagadorDocumentos() {
		return selectedPagadorDocumentos;
	}

	public void setSelectedPagadorDocumentos(PagadorRecebedor selectedPagadorDocumentos) {
		this.selectedPagadorDocumentos = selectedPagadorDocumentos;
	}
	
	
	
}