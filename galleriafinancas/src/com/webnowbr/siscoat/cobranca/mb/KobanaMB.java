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
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
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
import javax.xml.ws.Holder;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
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
import com.webnowbr.siscoat.cobranca.db.model.UniProof;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.TransferenciasObservacoesIUGUDao;
import com.webnowbr.siscoat.cobranca.db.op.UniProofDao;
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

@ManagedBean(name = "kobanaMB")
@SessionScoped
public class KobanaMB {

	/****
	 * Token de Segurança
		Integrações > API > Token de API.
		
		Sandbox
		8gCXkXseDkPQLLa6bzjaA-moKUol2TeXr8SDWCKAtBc
		
		Prod
		YFfBaw13zZ5CxOOwZIlmDevC2_O-MoVPQwzpz4ejrL8
		
		
	 */
	
	/***
	 * FIM ATRIBUTOS RECIBO
	 */
	
	/***
	 * BOLETOS
	 * 
     
     curl --request POST \
     --url https://api-sandbox.kobana.com.br/v1/bank_billets \
     --header 'Accept: application/json' \
     --header 'Authorization: Bearer 8gCXkXseDkPQLLa6bzjaA-moKUol2TeXr8SDWCKAtBc' \
     --header 'Content-Type: application/json' \
     --header 'User-Agent: webnowbr@gmail.com' \
     --data '
{
     "interest_type": 0,
     "interest_days_type": 0,
     "fine_type": 0,
     "discount_type": 0,
     "charge_type": 1,
     "dispatch_type": 1,
     "document_type": "02",
     "acceptance": "N",
     "amount": 10,
     "expire_at": "2022-05-25",
     "customer_person_name": "hermes junior",
     "customer_cnpj_cpf": "312.559.048-52",
     "customer_state": "SP",
     "customer_city_name": "Campinas",
     "customer_zipcode": "13073035",
     "customer_address": "Avenida XXXXX",
     "customer_address_complement": "BLOc4 ",
     "customer_address_number": "550",
     "customer_email": "hv.junior@gmail.com",
     "customer_neighborhood": "Vila Nova",
     "customer_ignore_email": true,
     "days_for_interest": 1,
     "interest_percentage": 1.5,
     "interest_value": 2,
     "days_for_fine": 1,
     "fine_percentage": 2,
     "fine_value": 15,
     "document_number": "01052_01",
     "first_instruction": "Instrução 1",
     "second_instruction": "Instrução 2",
     "ignore_email": false
}
     
	 * @return
	 */

	private Date dtInicioConsulta;
	private Date dtFimConsulta;
	
	private List<ContratoCobrancaDetalhes> selectedParcelas = new ArrayList<ContratoCobrancaDetalhes>();
	
	private List<ContratoCobrancaDetalhes> listContratoCobrancaDetalhes;
	
	
	public String clearFieldsParcelasBoleto() {
		
		this.listContratoCobrancaDetalhes = new ArrayList<ContratoCobrancaDetalhes>();
		this.dtInicioConsulta = gerarDataHoje();
		this.dtFimConsulta = gerarDataHoje();
		
		return "/Atendimento/Cobranca/ContratoCobrancaBoletosKobana.xhtml";
	}
	
	public void consultarParcelasBoleto() {
		
		this.listContratoCobrancaDetalhes = new ArrayList<ContratoCobrancaDetalhes>();
		
		ContratoCobrancaDetalhesDao cDao = new ContratoCobrancaDetalhesDao();

		this.listContratoCobrancaDetalhes = cDao.getParcelasPorVencimento(this.dtInicioConsulta, this.dtFimConsulta);
	}
	
	public void gerarBoletoSimples(ContratoCobranca contrato, ContratoCobrancaDetalhes parcela) {
		FacesContext context = FacesContext.getCurrentInstance();
		
		geraBoletoKobana(contrato, parcela);
		
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO,
					"Boleto da parcela " + parcela.getContrato().getNumeroContrato() + " / " + parcela.getNumeroParcela() + " gerado com sucesso!!!",""));
	}
	
	public void gerarBoletosLote() {		
		FacesContext context = FacesContext.getCurrentInstance();
		
		if (this.selectedParcelas.size() == 0) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Nenhum boleto foi selecionado para geração!!!",""));
		} else {
			for (ContratoCobrancaDetalhes parcela : this.selectedParcelas) {
				System.out.println("Parcela" + parcela.getNumeroParcela() );
			}
			
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Boleto(s) gerado(s) com sucesso!!!",""));
		}
	}
	
	public void geraBoletoKobana(ContratoCobranca contrato, ContratoCobrancaDetalhes parcela) {
		try {		
			FacesContext context = FacesContext.getCurrentInstance();
			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://api.kobana.com.br/v1/bank_billets");
			
/*
 * 
  	8558	True Securitizadora S.A.	Itaú 109 CC: 53683-2	2		
		CRI 1
		
	8557	Galleria Finanças Securitizadora S.A.	Bradesco 09 CC: 41501-4	1002
		SEC
		
	8555	Galleria Home Equity FIDC	Itaú 109 CC: 25161-5 Padrão	965
		FIDC
		
		
		bank_billet_account_id
		ID da Carteira de Cobrança. Se não informado, usará a carteira padrão.
 */
			

			JSONObject jsonObj = getJSONBoletoKobana(contrato, parcela);
			byte[] postDataBytes = jsonObj.toString().getBytes();

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "*/*");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setRequestProperty("Authorization", "Bearer YFfBaw13zZ5CxOOwZIlmDevC2_O-MoVPQwzpz4ejrL8");
			myURLConnection.setRequestProperty("User-Agent", "webnowbr@gmail.com");
		     
			myURLConnection.setDoOutput(true);
			myURLConnection.getOutputStream().write(postDataBytes);
	
			JSONObject myResponse = null;
			myResponse = getJSONSucesso(myURLConnection.getInputStream());
			
			int status = myURLConnection.getResponseCode();
			
			String urlBoleto = "";
			
			if (status == 201) {
				if (myResponse.has("url")) {					
					if (!myResponse.isNull("url")) {
						urlBoleto = myResponse.getString("url");
					}
				}
			
				ContratoCobrancaDetalhesDao parcelaDao = new ContratoCobrancaDetalhesDao();
				parcela.setUrlBoletoKonana(urlBoleto);
				parcelaDao.merge(parcela);
				
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"[Kobana - Geração Boleto] Boleto Gerado com Sucesso!", ""));
			} else {
				if (status == 401) {
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"[Kobana - Geração Boleto] Falha de autenticação. Token inválido!", ""));
				}
				if (status == 403) {
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"[Kobana - Geração Boleto] Falha de permissão. Você não tem o Scope obrigatório para essa chamada.", ""));
				}
				if (status == 422) {
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"[Kobana - Geração Boleto] Boleto inválido!", ""));
				}			
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"[Kobana - Geração Boleto] Erro não conhecido!", ""));
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
	
	public JSONObject getJSONBoletoKobana(ContratoCobranca contrato, ContratoCobrancaDetalhes parcela) {
		PagadorRecebedor cliente = contrato.getPagador();
		
		// Quantidade de dias após o vencimento que a mora começará a incidir. 
		// O valor default é 1 dia (o dia posterior ao vencimento).
		double days_for_interest = 1;
		// Porcentagem diária de juros. De 0.0 a 100.0 (Ex 1.5% = 1.5) 
		// Obrigatório se interest_type é igual a 1.
		double interest_percentage = 0.033;
		// Valor diário de juros (R$). Obrigatório se interest_type é igual a 2.
		//double interest_value = 2;
		// Quantidade de dias após o vencimento que a multa começará a incidir. 
		// O valor default é 1 dia (o dia posterior ao vencimento).
		double days_for_fine = 1;
		// Porcentagem de Multa por Atraso Ex: 2% x R$ 250,00 = R$ 5,00. 
		// Obrigatória se fine_type é igual a 1
		double fine_percentage = 2;
		// Valor da multa (R$). Obrigatório se fine_type é igual a 2.
		//double fine_value = 15;
				
		JSONObject jsonBoleto = new JSONObject();
	     
	    /*
	    Número do Contrato
	     */
	    jsonBoleto.put("document_number", contrato.getNumeroContrato());
		/*
		Instruções
		 */
	    jsonBoleto.put("first_instruction", "");
		jsonBoleto.put("second_instruction", "");
		/*
		valor boleto
		 */
		jsonBoleto.put("amount", parcela.getVlrParcela());
		/*
		Data vencimento		
		 */
		jsonBoleto.put("expire_at", getDataFormatada(parcela.getDataVencimento()));
		/*
		DADOS CLIENTE
		 */	     	    
	    jsonBoleto.put("customer_person_name", cliente.getNome());
	    
	    if (cliente.getCpf() == null || cliente.getCpf().equals("")) {
	    	jsonBoleto.put("customer_cnpj_cpf", cliente.getCnpj());
	    } else {
	    	jsonBoleto.put("customer_cnpj_cpf", cliente.getCpf());
	    }
	    		
	    if (cliente.getEstado() == null || cliente.getEstado().equals("")) {
	    	jsonBoleto.put("customer_state", "Estado");
	    } else {
	    	jsonBoleto.put("customer_state", cliente.getEstado());
	    }
	    
	    if (cliente.getCidade() == null || cliente.getCidade().equals("")) {
	    	jsonBoleto.put("customer_city_name", "Cidade");
	    } else {
	    	jsonBoleto.put("customer_city_name", cliente.getCidade());
	    }
		
	    if (cliente.getCep() == null || cliente.getCep().equals("")) {
	    	jsonBoleto.put("customer_zipcode", "CEP");
	    } else {
	    	jsonBoleto.put("customer_zipcode", cliente.getCep());
	    }
		
	    if (cliente.getEndereco() == null || cliente.getEndereco().equals("")) {
	    	jsonBoleto.put("customer_address", "Endereço");
	    } else {
	    	jsonBoleto.put("customer_address", cliente.getEndereco());
	    }
	
		jsonBoleto.put("customer_address_complement", cliente.getComplemento());

		if (cliente.getEndereco() == null || cliente.getEndereco().equals("")) {
			jsonBoleto.put("customer_address_number", "00");
		} else {
			jsonBoleto.put("customer_address_number", "00");
		}
		
		if (cliente.getEmail() == null || cliente.getEmail().equals("")) {
			jsonBoleto.put("customer_email", "E-mail");
		} else {
			jsonBoleto.put("customer_email", cliente.getEmail());
		}
		
		if (cliente.getBairro() == null || cliente.getBairro().equals("")) {
			jsonBoleto.put("customer_neighborhood", "Bairro");
		} else {
			jsonBoleto.put("customer_neighborhood", cliente.getBairro());
		}
		
		/*
		Enviar este boleto por email para cliente e empresa?
		 */
		jsonBoleto.put("customer_ignore_email", true);
		jsonBoleto.put("ignore_email", false);			
		
		/*Tipo de juros/mora:
		0 Inexistente (Padrão)
		1 Para porcentagem diária
		2 Para valor diário*/
		jsonBoleto.put("interest_type", 1);
		/*
		Tipo de Dias para juros:
		0 Corridos
		1 Úteis
		*/
		jsonBoleto.put("interest_days_type", 0);
		/*
		Tipo de multa:
		0 Inexistente (Padrão)
		1 Para percentual do valor do boleto
		2 Para valor fixo  		 
		 */
		jsonBoleto.put("fine_type", 1);
		
		/*
		 * VALORES PADRÕES
		 */
		jsonBoleto.put("discount_type", 0);
		jsonBoleto.put("charge_type", 1);
		jsonBoleto.put("dispatch_type", 1);
		jsonBoleto.put("document_type", "02");
		jsonBoleto.put("acceptance", "N");
		
	    jsonBoleto.put("days_for_interest", days_for_interest);
		jsonBoleto.put("interest_percentage", interest_percentage);
		//jsonBoleto.put("interest_value", interest_value);
		jsonBoleto.put("days_for_fine", days_for_fine);
		jsonBoleto.put("fine_percentage", fine_percentage);
		//jsonBoleto.put("days_for_infine_valueterest", fine_value);
		
		return jsonBoleto;
	}	
	
	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}

	public String getDataFormatada(Date data) {		
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd", locale);
		
		String dataFormatada = sdf.format(data);
		
		return dataFormatada;
	}
	
	/* TODO*/
	public String getUFEstado(String Estado) {
		String uf = "";
		
		return uf;
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

	public Date getDtInicioConsulta() {
		return dtInicioConsulta;
	}

	public void setDtInicioConsulta(Date dtInicioConsulta) {
		this.dtInicioConsulta = dtInicioConsulta;
	}

	public Date getDtFimConsulta() {
		return dtFimConsulta;
	}

	public void setDtFimConsulta(Date dtFimConsulta) {
		this.dtFimConsulta = dtFimConsulta;
	}

	public List<ContratoCobrancaDetalhes> getListContratoCobrancaDetalhes() {
		return listContratoCobrancaDetalhes;
	}

	public void setListContratoCobrancaDetalhes(List<ContratoCobrancaDetalhes> listContratoCobrancaDetalhes) {
		this.listContratoCobrancaDetalhes = listContratoCobrancaDetalhes;
	}

	public List<ContratoCobrancaDetalhes> getSelectedParcelas() {
		return selectedParcelas;
	}

	public void setSelectedParcelas(List<ContratoCobrancaDetalhes> selectedParcelas) {
		this.selectedParcelas = selectedParcelas;
	}
}