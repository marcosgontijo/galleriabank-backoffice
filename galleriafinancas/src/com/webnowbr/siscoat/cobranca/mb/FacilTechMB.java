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
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesDao;
import com.webnowbr.siscoat.common.DateUtil;

@ManagedBean(name = "facilTechMB")
@SessionScoped
public class FacilTechMB {

	/****
	 * Token de Segurança
		Homologacao
		JWT - eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOjE2OTgyNDM3MjE3MTAsImlhdCI6MTY5ODI0MzcyMSwibmJmIjoxNjk4MjQzNzIxLCJleHAiOjE2OTgyNDM4NDEsInN1YiI6IkJBQ0tPRkZJQ0UifQ.MEYCIQCIdVL100bhrlRJK-41kNfptNUk05hHYkgpOc0RSXVj8gIhAIHD7I9UocUCzsOj7Il9HMzuPAkPWwCVtDoD27pdRoSF
		Header de Assinatura - MEQCIQCbR1H6d12c6XixlV-jF8Tumu9BRRU9VHtHYNEWvwV66AIfHnJ43KAJ_iUwadJKHtatgOtBWH66nPE3XQdQTJZZ7g
		URL Homolog
		https://wscredhomogalleria.facilinformatica.com.br/
		URL PROD
		https://wscredgalleria.facilinformatica.com.br/
	 */

	private String urlProducao = "https://wscredgalleria.facilinformatica.com.br/";
	
	private String urlHomologacao = "https://wscredhomogalleria.facilinformatica.com.br/wcf/rest/";
	private String tokenJWTHomologacao = "Bearer eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOjE2OTgyNDM3MjE3MTAsImlhdCI6MTY5ODI0MzcyMSwibmJmIjoxNjk4MjQzNzIxLCJleHAiOjE2OTgyNDM4NDEsInN1YiI6IkJBQ0tPRkZJQ0UifQ.MEYCIQCIdVL100bhrlRJK-41kNfptNUk05hHYkgpOc0RSXVj8gIhAIHD7I9UocUCzsOj7Il9HMzuPAkPWwCVtDoD27pdRoSF";
	private String assinaturaHeaderHomologacao = "MEQCIQCbR1H6d12c6XixlV-jF8Tumu9BRRU9VHtHYNEWvwV66AIfHnJ43KAJ_iUwadJKHtatgOtBWH66nPE3XQdQTJZZ7g";
	
	public JSONArray getTesteFacilTech() {
		JSONArray myResponse = new JSONArray();
		FacesContext context = FacesContext.getCurrentInstance();
		
		try {
				int HTTP_COD_SUCESSO = 200;

				boolean temFiltro = false;
				
				String urlFacilTech = urlHomologacao + "maintenance.svc/getpublickey";

				URL myURL = new URL(urlFacilTech);	
		
				HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("GET");
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
				myURLConnection.setRequestProperty("Content-type", "Application/JSON");
				myURLConnection.setRequestProperty("Authorization", tokenJWTHomologacao);
		
				int status = myURLConnection.getResponseCode();
				
				String result = IOUtils.toString(myURLConnection.getInputStream(), StandardCharsets.UTF_8.name());
				
				myResponse = new JSONArray(result);	
				
				if (status == 200) {
					return myResponse;
				} else {
					if (status == 401) {
						context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"[Facil Tech] Falha de autenticação. Token inválido!", ""));
					}
					if (status == 403) {
						context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"[Facil Tech] Falha de permissão. ", ""));
					}	
					
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"[Facil Tech] Erro não conhecido! Código: " + status, ""));
				}
							
				myURLConnection.disconnect();
		
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		return null;
	}
	
	
	
	public JSONObject getJSONCliente() {
				
		JSONObject jsonCliente = new JSONObject();
	 
		
		jsonCliente.put("DadosDePessoaFisica", "");
		
		JSONObject jsonDadosDePessoaFisica = new JSONObject();
		jsonDadosDePessoaFisica.put("EmpreendedorIndividual", "false");
		
		JSONObject jsonDadosPessoais = new JSONObject();
		jsonDadosPessoais.put("CPF", "111.111.111-90");
		jsonDadosPessoais.put("Nome", "João da Silva");
		jsonDadosPessoais.put("TipoDeDocumentoDeIdentificacao", 0);
		jsonDadosPessoais.put("NumeroDoDocumentoDeIdentificacao", "texto exemplo");
		jsonDadosPessoais.put("DataDeNascimento", "10/03/1981");
		jsonDadosPessoais.put("Naturalidade", "texto exemplo");
		jsonDadosPessoais.put("Nacionalidade", "texto exemplo");
		jsonDadosPessoais.put("EstadoCivil", 0);
		jsonDadosPessoais.put("Genero", 0);
		
		JSONObject jsonDocumentoIdentidade = new JSONObject();
		jsonDocumentoIdentidade.put("Numero", "999999");
		jsonDocumentoIdentidade.put("OrgaoExpedidor", "SSP");
		jsonDocumentoIdentidade.put("UF", "SP");
		jsonDocumentoIdentidade.put("DataDeEmissao", "10/01/1981");
		
		jsonDadosPessoais.put("DocumentoDeIdentidade", jsonDocumentoIdentidade);
		
		jsonDadosDePessoaFisica.put("DadosPessoais", jsonDadosPessoais);
		
		JSONObject jsonEnderecoResidencial = new JSONObject();
		
		jsonEnderecoResidencial.put("Logradouro", "Rua teste");
		jsonEnderecoResidencial.put("Numero", "999");
		jsonEnderecoResidencial.put("Complemento", "Complemento teste");
		jsonEnderecoResidencial.put("Bairro", "Bairro Teste");
		jsonEnderecoResidencial.put("Cidade", "Cidade Teste");
		jsonEnderecoResidencial.put("UF", "SP");
		jsonEnderecoResidencial.put("CEP", "99999-999");
		
		jsonDadosDePessoaFisica.put("EnderecoResidencial", jsonEnderecoResidencial);
		
		jsonDadosDePessoaFisica.put("DadosDePessoaFisica", jsonDadosDePessoaFisica);
		
		return jsonDadosDePessoaFisica;
	}
}