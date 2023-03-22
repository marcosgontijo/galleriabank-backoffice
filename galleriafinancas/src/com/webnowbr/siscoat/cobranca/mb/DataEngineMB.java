package com.webnowbr.siscoat.cobranca.mb;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.model.StreamedContent;

import com.webnowbr.siscoat.cobranca.db.model.DataEngine;
import com.webnowbr.siscoat.cobranca.db.model.Docket;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.DataEngineDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.GeradorRelatorioDownloadCliente;
import com.webnowbr.siscoat.common.SiscoatConstants;
import com.webnowbr.siscoat.infra.db.dao.UserDao;
import com.webnowbr.siscoat.infra.db.model.User;
import com.webnowbr.siscoat.security.LoginBean;

@ManagedBean(name="dataEngineMB")
@SessionScoped
public class DataEngineMB {
	
	@ManagedProperty(value = "#{loginBean}")
	protected LoginBean loginBean;
	
	private String url = "https://api.dataengine.com.br/v2";	
	private String IdproviderFlowPF = "b5986017-3aa6-4564-a125-2b1897fe82b6";
	private String IdproviderFlowPJ = "2dd0d245-0ec3-4302-9e09-eed92818ba1d";
	private String chaveApi = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZXJ2aWNlX2F1dGhfcmVxdWVzdF92YWxpZGF0aW9uIiwia"
			+ "WF0IjoxNjc4Mzc2NTAyLjk2NDgxMiwiY2xpIjoiYjMyN2E5OGQtMDhhMC00NjFkLTkzNTQtN2Q2YTczZGNlYTNlIn0.IME_xDdVDZzx5LNE7"
			+ "xpEDBN-wHQOILlkgjuQd2mzLtf8mGeousbvLElPlCao2DATTCdpfmNFIFppmYwdBNzdFu9bu7wKsulcZlf5Xuu80M3jZJQp56kfrdmJ-x"
			+ "8Wrqx9DAJEb2ZQW4sz24eDr_q9zUvsvukCmbCIiZPaUngWYR53luh9_Q-OfORxGIwm6RwYf1dATs3GIuhPT8k80S7KfcBBETJ2384UV7Cf"
			+ "ochOvgiT7FUyoNqHhivdFbyPLv8M0q7tHe49yBSyk8qYLHiDm_tKrlhaTFFwQyx4_AryRUcw4z14LykdmgRKX03T7i1pSYmyI2g8d5z9RZQ"
			+ "HsbOLfSG0QIn31Ed3BBOTSA3p6i7La6CYfQI247tWofs7-hRNYMuaAO91NurkvtcbaVDGE3tVzs-IjCjfmsOTEcFI_v3jKgQgaIFLOgs9E7"
			+ "UVQQAynXKR2C-MvoY8YBEZi6636z_Gakqs0MlUZEEvsVIkzbM0teuS2K6DLzTm428MJZWQemNAR7Ib1GXI1BybLN1bAP3T9kUgfN5AWHkW"
			+ "DfQCQ3eXhKpCJbNLZwl2B2mHh36ouDW18upzyLs-MOBO1YHtkNeGkOqQzC83j3XeLsuD7hX5cf66hoh28OQIoWW_raJwklLzHmSQQwQD54M"
			+ "R2t-P-qwzvsS5ADEsY5vTLTqyWo0";
	
	/*Link Documentação API:
	https://docs.dataengine.com.br/#!/Consulta/get_api_ProviderFlow_RequestFields_IdProviderFlow*/
	
	private boolean tipoPessoaIsFisica;
	private List<DataEngine> listEngine;
	private PagadorRecebedor pagadorAdicionar;
	
	public DataEngineMB() {
		
	}
	
	public String clearFieldsDataEngine() {
		listEngine = new ArrayList<DataEngine>();
		return "/Atendimento/Cobranca/DataEngine.xhtml";
	}
	
	public String pedirEngineDocket(Docket docket) {
		listEngine = new ArrayList<DataEngine>();
		for(PagadorRecebedor pagador : docket.getListaPagador()) {
			pagadorAdicionar = pagador;
			inserirPessoa();
		}
		return "/Atendimento/Cobranca/DataEngine.xhtml";
	}
	
	/*public void testeConsulta() {
		PagadorRecebedorDao pDao = new PagadorRecebedorDao();
		PagadorRecebedor pagador = pDao.findById((long) 28081);
		criarConsulta(pagador);
	}*/
	
	public void baixarDocumento(DataEngine engine) {
		FacesContext context = FacesContext.getCurrentInstance();
		if(CommonsUtil.semValor(engine.getIdCallManager())) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, 
					"Consulta sem IdCallManager", ""));	
			return;
		}
		if(CommonsUtil.semValor(engine.getPdfBase64())) {
			PegarPDFDataEngine(engine);
		}
		decodarBaixarArquivo(engine.getPdfBase64());
	}
	
	public void criarConsulta(DataEngine engine) {	//POST para gerar consulta	
		FacesContext context = FacesContext.getCurrentInstance();
		DataEngineDao engineDao = new DataEngineDao();
		if(engineDao.findByFilter("pagador", engine.getPagador()).size() > 0) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta já existente!", ""));	
			return;
		}
		if(!CommonsUtil.semValor(engine.getIdCallManager())) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, 
					"Consulta já existente!" + engine.getIdCallManager(), ""));	
			return;
		}
		try {
			//loginDocket();
			int HTTP_COD_SUCESSO = 200;
			int HTTP_COD_SUCESSO2 = 201;
			
			URL myURL;
			if(SiscoatConstants.DEV) {
				myURL = new URL(url + "/api/callmanager");
			}
			
			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();		
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setDoOutput(true);
			myURLConnection.addRequestProperty("x-api-key",  this.chaveApi);
			
			JSONObject myResponse = null;
			JSONObject jsonWhatsApp = getBodyJsonEngine(engine.getPagador());
			
			try (OutputStream os = myURLConnection.getOutputStream()) {
				byte[] input = jsonWhatsApp.toString().getBytes("utf-8");
				os.write(input, 0, input.length);
			}
								
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO && 
					myURLConnection.getResponseCode() != HTTP_COD_SUCESSO2) {	
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Engine: Falha  (Cod: " + myURLConnection.getResponseCode() + ")",""));
				System.out.println(jsonWhatsApp.toString());
			} else {
				//docket = new Docket(objetoContratoCobranca, listaPagador, estadoImovel, "" , cidadeImovel, "", getNomeUsuarioLogado(), gerarDataHoje());
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", ""));	
				myResponse = getJSONSucesso(myURLConnection.getInputStream());
				engine.setIdCallManager(myResponse.get("idCallManager").toString());
				engine.setData(gerarDataHoje());
				engine.setUsuario(getNomeUsuarioLogado());
				if(engine.getId() <= 0) {
					engineDao.create(engine);
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
	
	public void PegarPDFDataEngine(DataEngine engine) {	//POST para pegar pdf	
		FacesContext context = FacesContext.getCurrentInstance();
		if(CommonsUtil.semValor(engine.getIdCallManager())) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Não Foi gerado ID da consulta!!!", ""));	
			return;
		}
			
		String idproviderFlow;
		if(!CommonsUtil.semValor(engine.getPagador().getCpf())) {
			idproviderFlow = IdproviderFlowPF;
		} else {
			idproviderFlow = IdproviderFlowPJ;
		}
		
		try {
			//loginDocket();
			int HTTP_COD_SUCESSO = 200;
			
			URL myURL;
			if(SiscoatConstants.DEV) {
				myURL = new URL(url + "/dossie/"+ idproviderFlow + "/"+ engine.getIdCallManager() + "?base64=true");
			}
	
			HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();		
			myURLConnection.setRequestMethod("GET");
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setDoOutput(true);
			myURLConnection.addRequestProperty("x-api-key",  this.chaveApi);
			
			JSONObject myResponse = null;	
								
			if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Engine: Falha  (Cod: " + myURLConnection.getResponseCode() + ")",""));
				System.out.println(myURL.toString());
			} else {
				//docket = new Docket(objetoContratoCobranca, listaPagador, estadoImovel, "" , cidadeImovel, "", getNomeUsuarioLogado(), gerarDataHoje());
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Consulta feita com sucesso", ""));
				BufferedReader in;
				in = new BufferedReader(
						new InputStreamReader(myURLConnection.getInputStream(), "UTF-8"));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				engine.setPdfBase64(response.toString());
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

	public JSONObject getBodyJsonEngine(PagadorRecebedor pagador) { 
		JSONObject jsonDocketBodyPedido = new JSONObject();	
		if(!CommonsUtil.semValor(pagador.getCpf())) {
			jsonDocketBodyPedido.put("idProviderFlow", IdproviderFlowPF);
			jsonDocketBodyPedido.put("fields", getJsonPF(pagador));
		} else {
			jsonDocketBodyPedido.put("idProviderFlow", IdproviderFlowPJ);
			jsonDocketBodyPedido.put("fields", getJsonPJ(pagador));
		}
		return jsonDocketBodyPedido;
	}
	
	public JSONArray getJsonPF(PagadorRecebedor pagador) {
		JSONArray jsonDocumentosArray = new JSONArray();
	
		JSONObject fieldsCPF = new JSONObject();	
		fieldsCPF.put("field", "cpf");
		fieldsCPF.put("value", pagador.getCpf());
		
		JSONObject fieldsNome = new JSONObject();	
		fieldsNome.put("field", "nome");
		fieldsNome.put("value", pagador.getNome());
		
		JSONObject fieldsDtNsc = new JSONObject();	
		fieldsDtNsc.put("field", "data_nascimento");
		fieldsDtNsc.put("value", CommonsUtil.formataData(pagador.getDtNascimento(), "dd/MM/yyy"));	
		
		jsonDocumentosArray.put(fieldsCPF);
		jsonDocumentosArray.put(fieldsNome);
		jsonDocumentosArray.put(fieldsDtNsc);
		
		return jsonDocumentosArray;
	}
	
	public JSONArray getJsonPJ(PagadorRecebedor pagador) {
		JSONArray jsonDocumentosArray = new JSONArray();
	
		JSONObject fieldsCPF = new JSONObject();	
		fieldsCPF.put("field", "cnpj");
		fieldsCPF.put("value", pagador.getCnpj());
		
		JSONObject fieldsNome = new JSONObject();	
		fieldsNome.put("field", "nome");
		fieldsNome.put("value", pagador.getNome());
		
		jsonDocumentosArray.put(fieldsCPF);
		jsonDocumentosArray.put(fieldsNome);
		
		return jsonDocumentosArray;
	}
	
	public JSONObject getJSONSucesso(InputStream inputStream) { //Pega resultado da API
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


	public StreamedContent decodarBaixarArquivo(String base64) {
		byte[] decoded = Base64.getDecoder().decode(base64);
		
		InputStream in = new ByteArrayInputStream(decoded);
		final GeradorRelatorioDownloadCliente gerador = new GeradorRelatorioDownloadCliente(FacesContext.getCurrentInstance());
		gerador.open(String.format("Galleria Bank - Data Engine %s.pdf", ""));
		gerador.feed(in);
		gerador.close();
		return null;
	}
	
	public void clearPessoaDialog() {
		pagadorAdicionar = new PagadorRecebedor();
		tipoPessoaIsFisica = true;
	}
	
	public void selectedTipoPessoaPublico() {
		this.pagadorAdicionar = new PagadorRecebedor();
	}
	
	public void procurarPF() {
		if(CommonsUtil.semValor(pagadorAdicionar.getCpf())) {
			return;
		}
		PagadorRecebedorDao pDao = new PagadorRecebedorDao();
		if(pDao.findByFilter("cpf", pagadorAdicionar.getCpf()).size() > 0) {
			pagadorAdicionar = pDao.findByFilter("cpf", pagadorAdicionar.getCpf()).get(0);	
		} else {
			return;
		}
	}
	
	public void procurarPJ() {
		if(CommonsUtil.semValor(pagadorAdicionar.getCnpj())) {
			return;
		}		
		PagadorRecebedorDao pDao = new PagadorRecebedorDao();
		if(pDao.findByFilter("cnpj", pagadorAdicionar.getCnpj()).size() > 0) {
			pagadorAdicionar = pDao.findByFilter("cnpj", pagadorAdicionar.getCnpj()).get(0);
		} else {
			return;
		}	
	}
	
	public void inserirPessoa() {
		DataEngineDao engineDao = new DataEngineDao();
		DataEngine engine = null;
		if(pagadorAdicionar.getId() <= 0) {
			PagadorRecebedorDao pDao = new PagadorRecebedorDao();
			if(!CommonsUtil.semValor(pagadorAdicionar.getCpf()) && pDao.findByFilter("cpf", pagadorAdicionar.getCpf()).size() > 0) {
				pagadorAdicionar = pDao.findByFilter("cpf", pagadorAdicionar.getCpf()).get(0);
			} else if(!CommonsUtil.semValor(pagadorAdicionar.getCnpj()) && pDao.findByFilter("cnpj", pagadorAdicionar.getCnpj()).size() > 0) {
				pagadorAdicionar = pDao.findByFilter("cnpj", pagadorAdicionar.getCnpj()).get(0);
			} else {
				pDao.create(pagadorAdicionar);
			}		
		} 
		if (engineDao.findByFilter("pagador", pagadorAdicionar).size() > 0) {
			engine = engineDao.findByFilter("pagador", pagadorAdicionar).get(0);
		} 
		if(CommonsUtil.semValor(engine)) {
			engine = new DataEngine(pagadorAdicionar);
		}	
		
		this.listEngine.add(engine);
		
		pagadorAdicionar = new PagadorRecebedor();
	}
	
	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}
	
	public String getNomeUsuarioLogado() {
		User usuario = getUsuarioLogado();

		if (usuario.getLogin() != null) {
			if (!usuario.getLogin().equals("")) {
				return usuario.getLogin();
			} else {
				return "";
			}
		} else {
			return "";
		}
	}
	
	public User getUsuarioLogado() {
		User usuario = new User();
		if (loginBean != null) {
			List<User> usuarioLogado = new ArrayList<User>();
			UserDao u = new UserDao();

			usuarioLogado = u.findByFilter("login", loginBean.getUsername());

			if (usuarioLogado.size() > 0) {
				usuario = usuarioLogado.get(0);
			}
		}

		return usuario;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getChaveApi() {
		return chaveApi;
	}

	public void setChaveApi(String chaveApi) {
		this.chaveApi = chaveApi;
	}

	public String getIdproviderFlowPF() {
		return IdproviderFlowPF;
	}

	public void setIdproviderFlowPF(String idproviderFlowPF) {
		IdproviderFlowPF = idproviderFlowPF;
	}

	public String getIdproviderFlowPJ() {
		return IdproviderFlowPJ;
	}

	public void setIdproviderFlowPJ(String idproviderFlowPJ) {
		IdproviderFlowPJ = idproviderFlowPJ;
	}

	


	public boolean isTipoPessoaIsFisica() {
		return tipoPessoaIsFisica;
	}

	public void setTipoPessoaIsFisica(boolean tipoPessoaIsFisica) {
		this.tipoPessoaIsFisica = tipoPessoaIsFisica;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public List<DataEngine> getListEngine() {
		return listEngine;
	}

	public void setListEngine(List<DataEngine> listEngine) {
		this.listEngine = listEngine;
	}

	public PagadorRecebedor getPagadorAdicionar() {
		return pagadorAdicionar;
	}

	public void setPagadorAdicionar(PagadorRecebedor pagadorAdicionar) {
		this.pagadorAdicionar = pagadorAdicionar;
	}	
	
	
}