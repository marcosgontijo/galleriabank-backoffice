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

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.event.SelectEvent;

import com.webnowbr.siscoat.cobranca.db.model.BoletoKobana;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaDetalhes;
import com.webnowbr.siscoat.cobranca.db.model.DocketCidades;
import com.webnowbr.siscoat.cobranca.db.model.DocketEstados;
import com.webnowbr.siscoat.cobranca.db.model.FaturaIUGU;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesDao;
import com.webnowbr.siscoat.cobranca.db.op.DocketEstadosDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;

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
	
	private String tokenLogin = null;
	
	public void loginDocket() {		
		try {		
			FacesContext context = FacesContext.getCurrentInstance();
			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://sandbox-saas.docket.com.br/api/v2/auth/login");			

			JSONObject jsonObj = new JSONObject();
			
			jsonObj.put("login", "galleria-bank.api");
			jsonObj.put("senha", "5TM*sgZKJ3hoh@J");
			
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
			
			if (HTTP_COD_SUCESSO == 200) {
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
	
	public void getCidadesPorEstadoID(String estadoID) {		
		try {		
			FacesContext context = FacesContext.getCurrentInstance();
			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://sandbox-saas.docket.com.br/api/v2/galleria-bank/cidades?estadoId=" + estadoID);			

			// verifica se temos o token
			if (this.tokenLogin == null) {
				loginDocket();
			}

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
			
			this.tokenLogin = "";
			
			if (HTTP_COD_SUCESSO == 200) {				
				if (myResponse.has("cidades")) {					
					if (!myResponse.isNull("cidades")) {
						
						DocketEstadosDao docketEstadosDao= new DocketEstadosDao();
						
						DocketEstados estado = null;
						
						estado = docketEstadosDao.getEstado(estadoID);
						
						if (estado == null) {
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
}