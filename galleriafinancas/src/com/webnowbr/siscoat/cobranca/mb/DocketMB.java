package com.webnowbr.siscoat.cobranca.mb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.event.SelectEvent;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.DocketCidades;
import com.webnowbr.siscoat.cobranca.db.model.DocketEstados;
import com.webnowbr.siscoat.cobranca.db.model.DocumentosDocket;
import com.webnowbr.siscoat.cobranca.db.model.DocumentosPagadorDocket;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.DocketCidadesDao;
import com.webnowbr.siscoat.cobranca.db.op.DocketEstadosDao;
import com.webnowbr.siscoat.cobranca.db.op.DocumentosDocketDao;
import com.webnowbr.siscoat.cobranca.db.op.ImovelCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.common.BancosEnum;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.EstadosEnum;
import com.webnowbr.siscoat.common.SiscoatConstants;

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
	
	private List<PagadorRecebedor> listRecebedorPagador;	//lista de consulta
	private List<ContratoCobranca> listaContratosConsultar;	//lista de consulta
	private PagadorRecebedor selectedPagadorGenerico; //usado para consulta	de pessoa
	private PagadorRecebedor selectedPagadorDocumentos; //usado para consulta de docs
	String updatePagadorRecebedor = ":form";
	
	private List<DocumentosDocket> listaDococumentosDocket;  //listagem de docs
	private List<DocumentosPagadorDocket> listaDococumentosPagador; //listagem de docs
	List<SelectItem> listaEstados; // listagem de estados
		
	private ContratoCobranca objetoContratoCobranca; //op de referencia
	private List<PagadorRecebedor> listaPagador; //titulares pra enviar pedido
	private EstadosEnum estadoSelecionadoImovel;
	private List<DocketCidades> listaCidadesImovel;
	private String estadoImovel;
	private String cidadeImovel;
	
	public DocketMB() {
		urlHomologacao = "https://sandbox-saas.docket.com.br";
		kitIdGalleria = "02859d48-ff2a-45a4-922b-d6b9842affcc";
		kitNomeGalleria = "1 - GALLERIA BANK";
		login = "galleria-bank.api";
		senha = "5TM*sgZKJ3hoh@J";
		organizacao_url = "galleria-bank";
		
		listRecebedorPagador = new ArrayList<PagadorRecebedor>();
		listaContratosConsultar = new ArrayList<ContratoCobranca>();	
		listaDococumentosDocket = new ArrayList<DocumentosDocket>(); 
		listaDococumentosPagador = new ArrayList<DocumentosPagadorDocket>();
		listaEstados = pesquisaEstadosListaNome(); 
		objetoContratoCobranca = new ContratoCobranca(); 
		listaPagador = new ArrayList<PagadorRecebedor>(); 
		clearContratoCobranca();
	}
	
	public void loginDocket() {		
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
						
			myURLConnection.disconnect();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public void getListaCidades(DocumentosPagadorDocket doc) {
		DocketCidadesDao dcDao = new DocketCidadesDao();
		List<DocketCidades> lista = new ArrayList<DocketCidades>();
		lista = dcDao.getListaCidades(doc.getEstadoSelecionado().getUf());
		if(CommonsUtil.semValor(lista)) {
			getCidadesPorEstadoID(doc.getEstadoId());
			lista = dcDao.getListaCidades(doc.getEstadoSelecionado().getUf());
		}
		doc.setListaCidades(lista);
	}
	
	public void getListaCidadesImovel() {
		DocketCidadesDao dcDao = new DocketCidadesDao();
		List<DocketCidades> lista = new ArrayList<DocketCidades>();
		lista = dcDao.getListaCidades(estadoSelecionadoImovel.getUf());
		if(CommonsUtil.semValor(lista)) {
			getCidadesPorEstadoID(estadoSelecionadoImovel.getIdDocket());
			lista = dcDao.getListaCidades(estadoSelecionadoImovel.getUf());
		}
		this.setListaCidadesImovel(lista);
	}
	
	public void getCidadesPorEstadoID(String estadoID) {		
		try {		
			FacesContext context = FacesContext.getCurrentInstance();
			int HTTP_COD_SUCESSO = 200;

			URL myURL;
			if(SiscoatConstants.DEV) {
				myURL = new URL(urlHomologacao + "/api/v2/"+organizacao_url+"/cidades?estadoId=" + estadoID);
			} else {
				myURL = new URL(urlHomologacao + "/api/v2/"+organizacao_url+"/cidades?estadoId=" + estadoID);
			}

			// GET TOKEN Login
			loginDocket();

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setRequestProperty("Authorization", "Bearer " + this.tokenLogin);
		     
			myURLConnection.setDoOutput(true);

			JSONObject myResponse = null;
			int status = myURLConnection.getResponseCode();
			
			myResponse = getJSONSucesso(myURLConnection.getInputStream());
			
			if (status == HTTP_COD_SUCESSO) {				
				if (myResponse.has("cidades")) {					
					if (!myResponse.isNull("cidades")) {
						
						DocketEstadosDao docketEstadosDao= new DocketEstadosDao();
						
						DocketEstados estado = null;
						
						estado = docketEstadosDao.getEstado(estadoID);
						
						if (estado == null) {
							estado = new DocketEstados();
							
							estado.setIdDocket(estadoID);
						
							// TODO set NOME
							//estado.setNome(estadoID);
							// TODO set URL
							//estado.setUrl(estadoID);
						} 
						
						List<DocketCidades> cidades = new ArrayList<DocketCidades>();														
						JSONArray cidadesObj = myResponse.getJSONArray("cidades");		
						
						for (int i = 0; i < cidadesObj.length(); i++) {
							DocketCidades cidade = new DocketCidades();
							
							JSONObject cidadeObj = cidadesObj.getJSONObject(i);
							
							cidade.setIdDocket(cidadeObj.getString("id"));
							cidade.setNome(cidadeObj.getString("nome"));
							cidade.setUrl(cidadeObj.getString("url"));
							
							cidades.add(cidade);
						}
						
						estado.setCidades(cidades);
						
						docketEstadosDao.merge(estado);
					}
				}
			} else {
				if (status == 401) {
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"[Docket - getCidadesPorEstadoID] Falha de autenticação. Token inválido!", ""));
				}
				if (status == 403) {
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"[Docket - getCidadesPorEstadoID] Falha de autenticação.", ""));
				}
				if (status == 404) {
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"[Docket - getCidadesPorEstadoID] Não foram encontrados resultados para a sua busca.", ""));
				}
				if (status == 500) {
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"[Docket - getCidadesPorEstadoID] Erro na request.", ""));
				}
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"[Docket - Login] Erro não conhecido!", ""));
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
	
	public void criarPedido() {	
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			loginDocket();
			int HTTP_COD_SUCESSO = 200;
			
			URL myURL;
			if(SiscoatConstants.DEV) {
				myURL = new URL(urlHomologacao + "/api/v2/"+organizacao_url+"/shopping-documentos/alpha/pedidos");
			} else {
				myURL = new URL(urlHomologacao + "/api/v2/"+organizacao_url+"/shopping-documentos/alpha/pedidos");
			}
			
			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
			
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setRequestProperty("Authorization", "Bearer " + this.tokenLogin);
			myURLConnection.setDoOutput(true);		
			
			JSONObject myResponse = null;
			JSONObject jsonWhatsApp = getBodyJsonPedido(listaPagador);
			
			try (OutputStream os = myURLConnection.getOutputStream()) {
				byte[] input = jsonWhatsApp.toString().getBytes("utf-8");
				os.write(input, 0, input.length);
			}
						
			try(BufferedReader br = new BufferedReader(
			  new InputStreamReader(myURLConnection.getInputStream(), "utf-8"))) {
			    StringBuilder response = new StringBuilder();
			    String responseLine = null;
			    while ((responseLine = br.readLine()) != null) {
			        response.append(responseLine.trim());
			    }
			    System.out.println(response.toString());
			}
								
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
			jsonDocketCampos.put("dataNascimento", CommonsUtil.formataData(pagador.getDtNascimento(), "yyyy-MM-dd")); //2003-01-10T02:00:00.000+0000
		} else {
			jsonDocketCampos.put("razaoSocial", pagador.getNome());
			jsonDocketCampos.put("cnpj", pagador.getCnpj());
		}
		jsonDocketCampos.put("cidade", cidadeId);
		jsonDocketCampos.put("estado", estadoId);
		
		return jsonDocketCampos;
	}
	
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
		estadoSelecionadoImovel = null;
		listaCidadesImovel = new ArrayList<DocketCidades>(); 
		estadoImovel = null;
		cidadeImovel = null;
		
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
	
	public String clearFieldsContratoCobranca() {
		listaPagador = new ArrayList<PagadorRecebedor>();
		listaDococumentosPagador = new ArrayList<DocumentosPagadorDocket>(); 
		listaDococumentosDocket = new ArrayList<DocumentosDocket>(); 
		estadoSelecionadoImovel = null;
		listaCidadesImovel = new ArrayList<DocketCidades>(); 
		estadoImovel = null;
		cidadeImovel = null;
		
		DocumentosDocketDao docDao = new DocumentosDocketDao();	
		listaDococumentosDocket = docDao.findAll();

		for(DocumentosDocket doc : listaDococumentosDocket) {
			DocumentosPagadorDocket docPagador = new DocumentosPagadorDocket();
			docPagador.setDocumentoDocket(doc);
			listaDococumentosPagador.add(docPagador);
		}	
		populateSelectedContratoCobranca();
		return "/Atendimento/Cobranca/Docket.xhtml";
	}
	
	
	public void populateSelectedContratoCobranca() {
		ContratoCobrancaDao cDao = new ContratoCobrancaDao();
		ImovelCobrancaDao iDao = new ImovelCobrancaDao();
		ContratoCobranca contrato = new ContratoCobranca();
		contrato = cDao.findById(this.getObjetoContratoCobranca().getId());	
		ImovelCobranca imovel = new ImovelCobranca();
		imovel = iDao.findById(contrato.getImovel().getId());
		//if(CommonsUtil.semValor(this.objetoContratoCobranca)){
			this.objetoContratoCobranca = contrato;
			if(!CommonsUtil.semValor(imovel.getEstado())) {
				this.setEstadoSelecionadoImovel(EstadosEnum.getByUf(imovel.getEstado()));
			}
			if(!CommonsUtil.semValor(imovel.getCidade())) {
				setCidadeImovel(imovel.getCidade());
			}
			getListaCidadesImovel();
			
			adiconarDocumentospagador(contrato.getPagador());
			listaPagador.add(contrato.getPagador());
			
			//this.atualizaTodosDocumentos(); // PQ QUE ESSE CHAMADO QUEBRAAAAAAAAAAAAAAAAAAA
			
			if(CommonsUtil.semValor(listaPagador)) {
				return;
			}
			for(PagadorRecebedor pagador : listaPagador) {
				if(CommonsUtil.semValor(pagador.getDocumentosDocket()) 
						|| CommonsUtil.semValor(estadoSelecionadoImovel) 
						|| CommonsUtil.semValor(cidadeImovel)
						|| CommonsUtil.semValor(listaCidadesImovel)) {
					return;
				}
				for(DocumentosPagadorDocket doc : pagador.getDocumentosDocket()) {
					doc.setEstadoSelecionado(estadoSelecionadoImovel);
					doc.setCidade(cidadeImovel);
					doc.setListaCidades(listaCidadesImovel);
					doc.getCidadeDocketId();
				}		
			}
		//}
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
				if(!CommonsUtil.semValor(estadoSelecionadoImovel)) {
					docPagador.setEstadoSelecionado(estadoSelecionadoImovel);
					if(CommonsUtil.mesmoValor(estadoSelecionadoImovel.getUf(), "RJ")
						||CommonsUtil.mesmoValor(estadoSelecionadoImovel.getUf(), "PR")) {
						if(CommonsUtil.mesmoValor(doc.getDocumentoNome(), 
								"Certidão de Distribuição de Ações Criminais - Justiça Estadual (1° instância)")
							|| CommonsUtil.mesmoValor(doc.getDocumentoNome(),
									"Certidão de Distribuição de Ações Criminais - Justiça Estadual (1° instância) - Processos Judiciais Eletrônicos")
							|| CommonsUtil.mesmoValor(doc.getDocumentoNome(), 
									"Certidão de Distribuição de Ações Cíveis - Justiça Estadual (1° instância)")){
							continue;
						}	
					}
				}
				if(!CommonsUtil.semValor(cidadeImovel)) {
					docPagador.setCidade(cidadeImovel);
					docPagador.getCidadeDocketId();
				} 
				pagador.getDocumentosDocket().add(docPagador);
			}
		}		
	}
	
	public void clearPagadorRecebedor() {
		this.selectedPagadorGenerico = new PagadorRecebedor();
	}

	public List<SelectItem> pesquisaEstadosListaNome() {
		List<SelectItem> listaEstados= new ArrayList<>();
		for(EstadosEnum estado : EstadosEnum.values()) {
			SelectItem item = new SelectItem(estado);
			item.setLabel(estado.getNomeComposto());
			listaEstados.add(item);
		}
		return listaEstados;
	}
	
	/*public void changeEstado(DocumentosPagadorDocket docs) {
		docs.setEstadoId(docs.getEstadoSelecionado().getIdDocket());
		docs.setEstado(docs.getEstadoSelecionado().getNome());
	}*/
	
	public List<String> completeCidades(String query) {
		String queryLowerCase = query.toLowerCase();
		List<String> cidades = new ArrayList<>();
		FacesContext context = FacesContext.getCurrentInstance();
		DocumentosPagadorDocket doc = (DocumentosPagadorDocket) UIComponent.getCurrentComponent(context).getAttributes().get("documentoAtual");
		if(!CommonsUtil.semValor(doc.getListaCidades())) {
			for (DocketCidades cidade : doc.getListaCidades()) {
				String cidadeStr = cidade.getNome();
				cidades.add(cidadeStr);
				/*if(estado.getNomeComposto().contains(queryLowerCase)) {
					estados.add(estado);
				}*/
			}
		}
		return cidades.stream().filter(t -> t.toLowerCase().contains(queryLowerCase)).collect(Collectors.toList());
	 }
	
	public List<String> completeCidadesImovel(String query) {
		String queryLowerCase = query.toLowerCase();
		List<String> cidades = new ArrayList<>();
		if(!CommonsUtil.semValor(listaCidadesImovel)) {
			for (DocketCidades cidade : listaCidadesImovel) {
				String cidadeStr = cidade.getNome();
				cidades.add(cidadeStr);
			}
		}
		return cidades.stream().filter(t -> t.toLowerCase().contains(queryLowerCase)).collect(Collectors.toList());
	 }
	
	public void atualizaTodosDocumentos() {
		if(CommonsUtil.semValor(listaPagador)) {
			return;
		}
		for(PagadorRecebedor pagador : listaPagador) {
			if(CommonsUtil.semValor(pagador.getDocumentosDocket()) 
					&& CommonsUtil.semValor(estadoSelecionadoImovel) 
					&& CommonsUtil.semValor(cidadeImovel)
					&& CommonsUtil.semValor(listaCidadesImovel)) {
				return;
			}
			for(DocumentosPagadorDocket doc : pagador.getDocumentosDocket()) {
				doc.setEstadoSelecionado(estadoSelecionadoImovel);
				doc.setCidade(cidadeImovel);
				doc.setListaCidades(listaCidadesImovel);
				doc.getCidadeDocketId();
			}		
		}	                                         
		return;
	}
	
	
		
	/*public List<EstadosEnum> completeBancos(String query) {
		String queryLowerCase = query.toLowerCase();
		List<EstadosEnum> estados = new ArrayList<>();
		for (EstadosEnum estado : EstadosEnum.values()) {
			//String bancoStr = estado.getNomeComposto().toString();
			//bancos.add(bancoStr);
			if(estado.getNomeComposto().contains(queryLowerCase)) {
				estados.add(estado);
			}
		}
		return estados;//.stream().filter(t -> t.getNomeComposto().toLowerCase().contains(queryLowerCase)).collect(Collectors.toList());
	 }*/
	
	
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

	public List<SelectItem> getListaEstados() {
		return listaEstados;
	}

	public void setListaEstados(List<SelectItem> listaEstados) {
		this.listaEstados = listaEstados;
	}

	public EstadosEnum getEstadoSelecionadoImovel() {
		return estadoSelecionadoImovel;
	}

	public void setEstadoSelecionadoImovel(EstadosEnum estadoSelecionadoImovel) {
		this.estadoSelecionadoImovel = estadoSelecionadoImovel;
		this.estadoImovel = estadoSelecionadoImovel.getNome();
	}

	public void setListaCidadesImovel(List<DocketCidades> listaCidadesImovel) {
		this.listaCidadesImovel = listaCidadesImovel;
	}

	public String getEstadoImovel() {
		return estadoImovel;
	}

	public void setEstadoImovel(String estadoImovel) {
		this.estadoImovel = estadoImovel;
	}

	public String getCidadeImovel() {
		return cidadeImovel;
	}

	public void setCidadeImovel(String cidadeImovel) {
		this.cidadeImovel = cidadeImovel;
	}
	
	
	
}