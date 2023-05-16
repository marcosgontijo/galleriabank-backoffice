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
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDetalhesDao;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.common.DateUtil;

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
	
	private Date dataHoje;
	
	private List<ContratoCobrancaDetalhes> selectedParcelas = new ArrayList<ContratoCobrancaDetalhes>();
	
	private List<ContratoCobrancaDetalhes> listContratoCobrancaDetalhes;
	
	private List<BoletoKobana> listBoletosKobana;
	
	private String filtroStatus;
	private String filtroData;
	private String filtroEmpresa;
	
	private ContratoCobranca contrato;
	private ContratoCobrancaDetalhes parcela;
	private BigDecimal valorBoleto;
	
	private PagadorRecebedor cedente;
	
	private BoletoKobana selectedBoletoKobana;
	
	public String clearFieldsConsultarBoleto() {
		
		this.listBoletosKobana = new ArrayList<BoletoKobana>();
		this.selectedParcelas = new ArrayList<ContratoCobrancaDetalhes>();
		//this.dtInicioConsulta = gerarDataHoje();
		//this.dtFimConsulta = gerarDataHoje();
		
		this.filtroStatus = "paid";
		this.filtroData = "Pagamento";
		this.filtroEmpresa = "";
		
		Date dataWorokingDayBeforeToday = DateUtil.getWorkingDayBeforeToday(gerarDataHoje());
		
		this.dtInicioConsulta = dataWorokingDayBeforeToday;
		this.dtFimConsulta = dataWorokingDayBeforeToday;
		
		return "/Atendimento/Cobranca/ContratoCobrancaConsultaBoletosKobana.xhtml";
	}
	
	public void cancelarBoleto() {
		try {		
			FacesContext context = FacesContext.getCurrentInstance();
			int HTTP_COD_SUCESSO = 200;
			
			String urlKobana = "";
			
			if (this.selectedBoletoKobana != null) {
				urlKobana = "https://api.kobana.com.br/v1/bank_billets/" + this.selectedBoletoKobana.getId() + "/cancel";	
			}

			URL myURL = new URL(urlKobana);			

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("PUT");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-type", "Application/JSON");
			myURLConnection.setRequestProperty("Authorization", "Bearer YFfBaw13zZ5CxOOwZIlmDevC2_O-MoVPQwzpz4ejrL8");
	
			int status = myURLConnection.getResponseCode();
			
			if (status == 204) {
				
				// limpa url kobana
				ContratoCobrancaDetalhesDao parcelaDao = new ContratoCobrancaDetalhesDao();
				this.parcela.setUrlBoletoKonana(null);
				parcelaDao.merge(this.parcela);
				
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"[Kobana - Geração Boleto] Boleto cancelado com sucesso!", ""));
			} else {
				if (status == 403) {
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"[Kobana - Geração Boleto] Este boleto não pode ser cancelado!", ""));
				}
				if (status == 404) {
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"[Kobana - Geração Boleto] Boleto não encontrado.", ""));
				}	
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"[Kobana - Consulta Boletos] Erro não conhecido!", ""));
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
	
	public JSONArray processaAPIKobana(String page) {
		JSONArray myResponse = new JSONArray();
		FacesContext context = FacesContext.getCurrentInstance();
		
		try {
				int HTTP_COD_SUCESSO = 200;
				
				String urlKobana = "https://api.kobana.com.br/v1/bank_billets";
				boolean temFiltro = false;
				
				// filtro Status
				if (!this.filtroStatus.equals("Todos")) {
					urlKobana = urlKobana + "?status=" + this.filtroStatus;
					
					temFiltro = true;
				} 
				
				// filtro Data
				if (temFiltro) {
					urlKobana = urlKobana + "&";
				} else {
					urlKobana = urlKobana + "?";
				}
				
				if (!this.filtroData.equals("Todos")) {
					
					Locale locale = new Locale("pt", "BR");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
					
					String dataInicio = sdf.format(this.dtInicioConsulta.getTime());;
					String dataFim = sdf.format(this.dtFimConsulta.getTime());;
					
					if (this.filtroData.equals("Vencimento")) {
						urlKobana = urlKobana + "expire_from=" + dataInicio + "&expire_to=" + dataFim;
					} 
					
					if (this.filtroData.equals("Pagamento")) {
						urlKobana = urlKobana + "paid_from=" + dataInicio + "&paid_to=" + dataFim;
					}
					
					if (this.filtroData.equals("Registro")) {
						urlKobana = urlKobana + "created_from=" + dataInicio + "&created_to=" + dataFim;
					}
				} 
				
				urlKobana = urlKobana + "&page=" + page + "&per_page=50";
		
				URL myURL = new URL(urlKobana);	
		
				HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("GET");
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
				myURLConnection.setRequestProperty("Content-type", "Application/JSON");
				myURLConnection.setRequestProperty("Authorization", "Bearer YFfBaw13zZ5CxOOwZIlmDevC2_O-MoVPQwzpz4ejrL8");
		
				int status = myURLConnection.getResponseCode();
				
				String result = IOUtils.toString(myURLConnection.getInputStream(), StandardCharsets.UTF_8.name());
				
				myResponse = new JSONArray(result);	
				
				if (status == 200) {
					return myResponse;
				} else {
					if (status == 401) {
						context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"[Kobana - Geração Boleto] Falha de autenticação. Token inválido!", ""));
					}
					if (status == 403) {
						context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"[Kobana - Geração Boleto] Falha de permissão. Você não tem o Scope obrigatório para essa chamada.", ""));
					}	
					context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"[Kobana - Consulta Boletos] Erro não conhecido!", ""));
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
	
	public void consultarBoletosKobana() {			
		ContratoCobrancaDao contratoDao = new ContratoCobrancaDao();
		ContratoCobrancaDetalhesDao parcelaDao = new ContratoCobrancaDetalhesDao();
		
		int countPagesKobana = 1;
		boolean processaAPIKobana = true;
		
		this.listBoletosKobana = new ArrayList<BoletoKobana>();
		
		while (processaAPIKobana) {
			JSONArray myResponse = processaAPIKobana(String.valueOf(countPagesKobana));		
			
			if (myResponse.length() == 50) {
				countPagesKobana = countPagesKobana + 1;
			} else {
				processaAPIKobana = false;
			}
			
			for (int i = 0; i < myResponse.length(); i++) {
				BoletoKobana boleto = new BoletoKobana();
				
				// atributo usado apenas para agrupamento de somatória na tela
				boleto.setIdFakeAgrupamentoSomatoria(777);
				
				JSONObject objetoBoleto = myResponse.getJSONObject(i);
			
				boleto.setId(objetoBoleto.getLong("id"));
				boleto.setExpireAt(processStringToDate(objetoBoleto.getString("expire_at")));
									
				if (!objetoBoleto.isNull("paid_at")) {
					boleto.setPaidAt(processStringToDate(objetoBoleto.getString("paid_at")));
				}
				
				boleto.setCreatedAt(processStringToDate(objetoBoleto.getString("created_at")));
				
				if (!objetoBoleto.isNull("document_number")) {
					boleto.setDocumentNumber(objetoBoleto.getString("document_number"));
				}
				
				if (objetoBoleto.getString("status").equals("paid")) {
					boleto.setStatus("Pago");
				} else if (objetoBoleto.getString("status").equals("opened")) {
					boleto.setStatus("Registrado");
				} else if (objetoBoleto.getString("status").equals("canceled")) {
					boleto.setStatus("Cancelado");
				} else if (objetoBoleto.getString("status").equals("overdue")) {
					boleto.setStatus("Vencido");
				} else {
					boleto.setStatus(objetoBoleto.getString("status"));
				}		
				
				//if (boleto.getStatus().equals("Pago")) {						
					if (objetoBoleto.has("custom_data")) {
						if (!objetoBoleto.isNull("custom_data")) {
							JSONObject objetoDataBoleto = objetoBoleto.getJSONObject("custom_data");
							ContratoCobranca contrato = new ContratoCobranca();
							contrato = contratoDao.findById(Long.valueOf(objetoDataBoleto.getString("idContrato")));
							boleto.setContrato(contrato); 
							
							boleto.setVlrParcela(BigDecimal.ZERO);
							
							if (objetoDataBoleto.has("qtdeParcelas")) { 
								if (objetoDataBoleto.getString("qtdeParcelas").equals("unica")) {
									if (objetoDataBoleto.has("idParcela")) {
										ContratoCobrancaDetalhes parcela = new ContratoCobrancaDetalhes();
										parcela = parcelaDao.findById(Long.valueOf(objetoDataBoleto.getString("idParcela")));
										boleto.setParcela(parcela);
										boleto.setVlrParcela(parcela.getVlrParcela());
									}
								} else {
									// se gerou para mais de parcela
									// aqui esta como baixar as parcelas
									if (objetoDataBoleto.getString("qtdeParcelas").equals("multiparcelas")
											|| objetoDataBoleto.getString("qtdeParcelas").equals("varias")) {
										List<ContratoCobrancaDetalhes> parcelasBoleto = new ArrayList<ContratoCobrancaDetalhes>();
										ContratoCobrancaDetalhesDao cDao = new ContratoCobrancaDetalhesDao();
										
										Iterator<String> contratoParcelas = objetoDataBoleto.keys();

										// Verifica se há mais alguma key
										while (contratoParcelas.hasNext()) {
											String nomeObjeto = contratoParcelas.next();
											
										    if (nomeObjeto.contains("idParcela")) {
										    	String valorObjeto = objetoDataBoleto.get(nomeObjeto).toString();
										    	parcelasBoleto.add(cDao.findById(Long.valueOf(valorObjeto)));
										    }
										}
				
										boleto.setMultiParcelas(parcelasBoleto);
										
										for (ContratoCobrancaDetalhes parcelas : boleto.getMultiParcelas()) {
											if (parcelas.getVlrParcela() != null) {
												// TODO VER COMO CAPTURAR O VALOR DAS PARCELAS DOS BOLETOS
												//boleto.setVlrParcela(boleto.getVlrParcela().add(parcelas.getVlrParcela()));
											}
										}
									}
								}
							} else {
								if (objetoDataBoleto.has("idParcela")) {
									ContratoCobrancaDetalhes parcela = new ContratoCobrancaDetalhes();
									parcela = parcelaDao.findById(Long.valueOf(objetoDataBoleto.getString("idParcela")));
									boleto.setParcela(parcela);
									boleto.setVlrParcela(parcela.getVlrParcela());
								}
							}
						}
					}
				//}
				
				boleto.setCustomerPersonName(objetoBoleto.getString("customer_person_name"));
				boleto.setCustomerPersonCNPJCPF(objetoBoleto.getString("customer_cnpj_cpf"));
				boleto.setCustomerEmail(objetoBoleto.getString("customer_email"));
				boleto.setPaidAmount(BigDecimal.valueOf(objetoBoleto.getDouble("paid_amount")));
				boleto.setUrlBoleto(objetoBoleto.getString("url"));
				boleto.setBeneficiaryName(objetoBoleto.getString("beneficiary_name"));
								
				if (!objetoBoleto.isNull("description")) {
					boleto.setDescription(objetoBoleto.getString("description"));
				}
				
				// soma valor das parcelas quando multiparcelas
				/*
				if (boleto.getMultiParcelas() != null) {
					BigDecimal valorTotalParcelas = BigDecimal.ZERO;
					
					for (ContratoCobrancaDetalhes parcelas : boleto.getMultiParcelas()) {
						valorTotalParcelas = valorTotalParcelas.add(parcelas.getVlrParcela());
					}	
					
					boleto.setVlrParcela(valorTotalParcelas);
				}
				*/
				
				// Filtra Empresa
				if (this.filtroEmpresa.equals("")) {
					this.listBoletosKobana.add(boleto);	
				} else {
					if (boleto.getContrato() != null) {
						if (boleto.getContrato().getEmpresa().equals(this.filtroEmpresa)) {
							this.listBoletosKobana.add(boleto);		
						}
					} else {
						this.listBoletosKobana.add(boleto);		
					}
				}				
			}
		}
	}
	
	public Date processStringToDate(String dateStr) {
		Date dataConvertida = null;
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd"); 
		
		try {
			dataConvertida = formato.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dataConvertida;
	}	
	
	/*
	 * 
	 * 
	 * 	
	"expire_at": "2022-06-09",
    "paid_at": "2022-06-07",
    "status": "paid",
    "customer_person_name": "Lucas Ardel Florencio Pinto",
    "customer_cnpj_cpf": "227.918.008-16",
    "customer_email": "lucas.dbsound2016@gmail.com",
    "paid_amount": 7969,
    "url": "https://bole.to/2/ayzpwod",
    "beneficiary_name": "True Securitizadora S.A.",
    "created_at": "2022-06-06T12:46:35-03:00",
    
	 */
	/**
	 * TODO
	 * @param valorBoleto
	 */
	public void gerarBoletoSimplesTela(ContratoCobranca contrato, ContratoCobrancaDetalhes parcela, Date vencimento,
			BigDecimal valor) {
		
		try {		
			FacesContext context = FacesContext.getCurrentInstance();
			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://api.kobana.com.br/v1/bank_billets");			

			JSONObject jsonObj = getJSONBoletoKobanaCustom(contrato, parcela, vencimento, valor, null);
			byte[] postDataBytes = jsonObj.toString().getBytes();

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setRequestProperty("Authorization", "Bearer YFfBaw13zZ5CxOOwZIlmDevC2_O-MoVPQwzpz4ejrL8");
			myURLConnection.setRequestProperty("User-Agent", "webnowbr@gmail.com");
		     
			myURLConnection.setDoOutput(true);
			//myURLConnection.getOutputStream().write(postDataBytes);
			
			try(OutputStream os = myURLConnection.getOutputStream()) {
			    byte[] input = jsonObj.toString().getBytes("utf-8");
			    os.write(input, 0, input.length);			
			}
	
			JSONObject myResponse = null;
			int status = myURLConnection.getResponseCode();
			
			myResponse = getJSONSucesso(myURLConnection.getInputStream());			
			
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
				
				TakeBlipMB tkblpMb = new TakeBlipMB();
				PagadorRecebedor pagador;
				pagador = contrato.getPagador();
		
				tkblpMb.sendWhatsAppMessagePagadorBoleto(pagador, "envio_boleto_cobranca", urlBoleto);
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
	
	public void gerarBoletoMaisParcelasKobana(ContratoCobranca contrato, List<ContratoCobrancaDetalhes> parcelasSelecionadas, Date vencimento,
			BigDecimal valor) {
		
		try {		
			FacesContext context = FacesContext.getCurrentInstance();
			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://api.kobana.com.br/v1/bank_billets");			

			JSONObject jsonObj = getJSONBoletoKobanaCustom(contrato, null, vencimento, valor, parcelasSelecionadas);
			byte[] postDataBytes = jsonObj.toString().getBytes();

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setRequestProperty("Authorization", "Bearer YFfBaw13zZ5CxOOwZIlmDevC2_O-MoVPQwzpz4ejrL8");
			myURLConnection.setRequestProperty("User-Agent", "webnowbr@gmail.com");
		     
			myURLConnection.setDoOutput(true);
			//myURLConnection.getOutputStream().write(postDataBytes);
			
			try(OutputStream os = myURLConnection.getOutputStream()) {
			    byte[] input = jsonObj.toString().getBytes("utf-8");
			    os.write(input, 0, input.length);			
			}
	
			JSONObject myResponse = null;
			int status = myURLConnection.getResponseCode();
			
			myResponse = getJSONSucesso(myURLConnection.getInputStream());			
			
			String urlBoleto = "";
			
			if (status == 201) {
				if (myResponse.has("url")) {					
					if (!myResponse.isNull("url")) {
						urlBoleto = myResponse.getString("url");
					}
				}
				
				for (ContratoCobrancaDetalhes parcela : parcelasSelecionadas) {
					updateParcelaBoletoKobana(parcela, urlBoleto);
				}
								
				enviaWhatsAppBoletoKobana(contrato, urlBoleto); 
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
	
	public void updateParcelaBoletoKobana(ContratoCobrancaDetalhes parcela, String urlBoleto) {
		ContratoCobrancaDetalhesDao parcelaDao = new ContratoCobrancaDetalhesDao();
		parcela.setUrlBoletoKonana(urlBoleto);
		parcelaDao.merge(parcela);
	}
	
	public void enviaWhatsAppBoletoKobana(ContratoCobranca contratoBoleto, String urlBoleto) {
		TakeBlipMB tkblpMb = new TakeBlipMB();
		PagadorRecebedor pagador;
		pagador = contratoBoleto.getPagador();

		tkblpMb.sendWhatsAppMessagePagadorBoleto(pagador, "envio_boleto_cobranca", urlBoleto);
	}
	
	public void gerarBoletoSimplesCustom(ContratoCobranca contrato, ContratoCobrancaDetalhes parcela, BigDecimal valorBoleto) {
		FacesContext context = FacesContext.getCurrentInstance();
		
		if (!validateParcelaGeracaoBoletoKobana(contrato, parcela)) {
			geraBoletoKobanaCustom(contrato, parcela, valorBoleto);
			
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
						"[Kobana - Geração Boleto] Boleto da parcela " + parcela.getContrato().getNumeroContrato() + " / " + parcela.getNumeroParcela() + " gerado com sucesso!!!",""));
		} else {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"[Kobana - Geração Boleto] Não foi possível gerar o boleto da parcela por algum problema de validação!!!",""));
		}		
	}
	
	public void geraBoletoKobanaCustom(ContratoCobranca contrato, ContratoCobrancaDetalhes parcela, BigDecimal valoBoleto) {
		try {		
			FacesContext context = FacesContext.getCurrentInstance();
			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://api.kobana.com.br/v1/bank_billets");			

			JSONObject jsonObj = getJSONBoletoKobanaCustom(contrato, parcela, null, valoBoleto, null);
			byte[] postDataBytes = jsonObj.toString().getBytes();

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setRequestProperty("Authorization", "Bearer YFfBaw13zZ5CxOOwZIlmDevC2_O-MoVPQwzpz4ejrL8");
			myURLConnection.setRequestProperty("User-Agent", "webnowbr@gmail.com");
		     
			myURLConnection.setDoOutput(true);
			//myURLConnection.getOutputStream().write(postDataBytes);
			
			try(OutputStream os = myURLConnection.getOutputStream()) {
			    byte[] input = jsonObj.toString().getBytes("utf-8");
			    os.write(input, 0, input.length);			
			}
	
			JSONObject myResponse = null;
			int status = myURLConnection.getResponseCode();
			
			myResponse = getJSONSucesso(myURLConnection.getInputStream());			
			
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
				
				TakeBlipMB tkblpMb = new TakeBlipMB();
				PagadorRecebedor pagador;
				pagador = contrato.getPagador();
				//pagador = new PagadorRecebedorDao().findById(10737l);
				tkblpMb.sendWhatsAppMessagePagadorBoleto(pagador, "envio_boleto_cobranca", urlBoleto);
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
	
	public JSONObject getJSONBoletoKobanaCustom(ContratoCobranca contrato, ContratoCobrancaDetalhes parcela, Date vencimento, BigDecimal valorBoleto, List<ContratoCobrancaDetalhes> parcelasSelecionadas) {
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
	 
	 	if (contrato.getEmpresa().equals("GALLERIA FINANÇAS SECURITIZADORA S.A.")) {
	 		jsonBoleto.put("bank_billet_account_id", 8557);
	 	}
		
		if (contrato.getEmpresa().equals("FIDC GALLERIA")) {
			jsonBoleto.put("bank_billet_account_id", 8555);
	 	}

		if (contrato.getEmpresa().equals("CRI 1")) {
			jsonBoleto.put("bank_billet_account_id", 8558);
		}
		
		if (contrato.getEmpresa().equals("CRI 2")) {
			jsonBoleto.put("bank_billet_account_id", 9251);
		}

		if (contrato.getEmpresa().equals("CRI 3")) {
			jsonBoleto.put("bank_billet_account_id", 9614);
		}

	    /*
	    Número do Contrato
	     */
	    jsonBoleto.put("document_number", contrato.getNumeroContrato());
	    
		/*
		Instruções
		*/
	    //jsonBoleto.put("first_instruction", "");
		//jsonBoleto.put("second_instruction", "");
		/*
		valor boleto
		 */
		jsonBoleto.put("amount", valorBoleto);
		/*
		Data vencimento		
		 */
		if (parcela != null) {
			if (vencimento != null) {
				jsonBoleto.put("expire_at", getDataFormatada(vencimento));
			} else {
				jsonBoleto.put("expire_at", getDataFormatada(parcela.getDataVencimento()));
			}
		} else {
			if (vencimento != null) {
				jsonBoleto.put("expire_at", getDataFormatada(vencimento));
			}
		}
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
	    	jsonBoleto.put("customer_state", "SP");
	    } else {
	    	jsonBoleto.put("customer_state", cliente.getEstado());
	    }
	    
	    if (cliente.getCidade() == null || cliente.getCidade().equals("")) {
	    	jsonBoleto.put("customer_city_name", "Cidade");
	    } else {
	    	jsonBoleto.put("customer_city_name", cliente.getCidade());
	    }
		
	    if (cliente.getCep() == null || cliente.getCep().equals("")) {
	    	jsonBoleto.put("customer_zipcode", "13091-611");
	    } else {
	    	jsonBoleto.put("customer_zipcode", cliente.getCep());
	    }
		
	    if (cliente.getEndereco() == null || cliente.getEndereco().equals("")) {
	    	jsonBoleto.put("customer_address", "Endereço");
	    } else {
	    	jsonBoleto.put("customer_address", cliente.getEndereco());
	    }
	
		jsonBoleto.put("customer_address_complement", cliente.getComplemento());

		jsonBoleto.put("customer_address_number", cliente.getNumero());
		
		if (cliente.getEmail() == null || cliente.getEmail().equals("")) {
			jsonBoleto.put("customer_email", "contato@gmail.com");
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
		
		if (contrato.getEmpresa().equals("GALLERIA FINANÇAS SECURITIZADORA S.A.")) {		
			jsonBoleto.put("customer_ignore_email", true);
		} else {
			jsonBoleto.put("customer_ignore_email", false);
		}
		
		jsonBoleto.put("ignore_email", false);			
		
		/*Tipo de juros/mora:
		0 Inexistente (Padrão)
		1 Para porcentagem diária
		2 Para valor diário*/
		jsonBoleto.put("interest_type", 2);
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
		if (parcela != null) {
			if (!parcela.isParcelaVencida()) {
				jsonBoleto.put("fine_type", 1);	
			} else {
				jsonBoleto.put("fine_type", 0);
			}
		} else {
			jsonBoleto.put("fine_type", 1);	
		}
		
		/*
		 * VALORES PADRÕES
		 */
		jsonBoleto.put("discount_type", 0);
		jsonBoleto.put("charge_type", 1);
		jsonBoleto.put("dispatch_type", 1);
		jsonBoleto.put("document_type", "02");
		jsonBoleto.put("acceptance", "N");
		
	    jsonBoleto.put("days_for_interest", days_for_interest);
		//jsonBoleto.put("interest_percentage", interest_percentage);
	    jsonBoleto.put("interest_value", (valorBoleto.multiply(BigDecimal.valueOf(0.033))).divide(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_UP));
		//jsonBoleto.put("interest_value", interest_value);
		jsonBoleto.put("days_for_fine", days_for_fine);
		jsonBoleto.put("fine_percentage", fine_percentage);
		//jsonBoleto.put("days_for_infine_valueterest", fine_value);
		
	    /*
	    Descrição
	     */
	    String parcelas = "";
	    if (parcela != null) {
	    	parcelas = parcela.getNumeroParcela();
	    } else {
	    	parcelas = "";
	    	
	    	for (ContratoCobrancaDetalhes parcelaSelecionada : parcelasSelecionadas) {
	    		if (parcelas.equals("")) {
	    			parcelas = parcelaSelecionada.getNumeroParcela();
	    		} else {
	    			parcelas = parcelas + ", " + parcelaSelecionada.getNumeroParcela();
	    		}
	    	}
	    }
		
		jsonBoleto.put("description", "Crédito com Imóvel em Garantia - Contrato: " + contrato.getNumeroContrato() + " / Parcela(s): " + parcelas);
		jsonBoleto.put("instructions", "Não receber após 30 dias do vencimento");
		
		JSONObject jsonCustomData = new JSONObject();
		
		if (parcela != null) {
			jsonCustomData.put("qtdeParcelas", "unica");
			
			jsonCustomData.put("idContrato", String.valueOf(contrato.getId()));
			jsonCustomData.put("idParcela", String.valueOf(parcela.getId()));
		} else {
			if (parcelasSelecionadas.size() > 0) {
				jsonCustomData.put("qtdeParcelas", "multiparcelas");
				
				jsonCustomData.put("idContrato", String.valueOf(contrato.getId()));
				
				for (int i = 0; i < parcelasSelecionadas.size(); i++) {
					jsonCustomData.put("idParcela/" + i, String.valueOf(parcelasSelecionadas.get(i).getId()));
		    	}
			}
		}
		
		jsonBoleto.put("custom_data", jsonCustomData);
		
		return jsonBoleto;
	}
		
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
		
		this.dataHoje = gerarDataHoje();
	}
	
	public boolean validateParcelaGeracaoBoletoKobana(ContratoCobranca contrato, ContratoCobrancaDetalhes parcela) {
		FacesContext context = FacesContext.getCurrentInstance();
		boolean retorno = false;
		
		if (parcela.getVlrParcela().compareTo(BigDecimal.ZERO) == 0) {
			retorno = true;
			
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"[Kobana - Geração Boleto] Há parcela(s) selecionada(s) com o valor igual a R$ 0,00!!!", ""));
		}
		
		if (!contrato.getEmpresa().equals("GALLERIA FINANÇAS SECURITIZADORA S.A.") && !contrato.getEmpresa().equals("FIDC GALLERIA")
				&& !contrato.getEmpresa().equals("CRI 1") && !contrato.getEmpresa().equals("CRI 2") && !contrato.getEmpresa().equals("CRI 3")) {
			retorno = true;
			
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"[Kobana - Geração Boleto] A empresa vinculada ao contrato não tem permissão para geração de boletos Kobana!!!", ""));
		}
		
		return retorno;
	}
	
	public void gerarBoletoSimples(ContratoCobranca contrato, ContratoCobrancaDetalhes parcela) {
		FacesContext context = FacesContext.getCurrentInstance();
		
		if (!validateParcelaGeracaoBoletoKobana(contrato, parcela)) {
			geraBoletoKobana(contrato, parcela);
			
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
						"[Kobana - Geração Boleto] Boleto da parcela " + parcela.getContrato().getNumeroContrato() + " / " + parcela.getNumeroParcela() + " gerado com sucesso!!!",""));
		} else {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"[Kobana - Geração Boleto] Não foi possível gerar o boleto da parcela por algum problema de validação!!!",""));
		}		
	}
	
	public void gerarBoletosLote() {		
		FacesContext context = FacesContext.getCurrentInstance();
		
		if (this.selectedParcelas.size() == 0) {
			context.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"[Kobana - Geração Boleto] Nenhum boleto foi selecionado para geração!!!",""));
		} else {
			// valida se tem alguma parcela selecionada com valor zerado
			boolean validaParcelaValorZerado = false;
			for (ContratoCobrancaDetalhes parcela : this.selectedParcelas) {
				if (validateParcelaGeracaoBoletoKobana(parcela.getContrato(), parcela)) {
					validaParcelaValorZerado = true;
					break;
				}
			}
			
			// se não tem parcelas zeradas
			if (!validaParcelaValorZerado) {
				for (ContratoCobrancaDetalhes parcela : this.selectedParcelas) {
					geraBoletoKobana(parcela.getContrato(), parcela);
				}
				
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
							"[Kobana - Geração Boleto] Boleto(s) gerado(s) com sucesso!!!",""));
			}
		}
	}
	
	public void geraBoletoKobana(ContratoCobranca contrato, ContratoCobrancaDetalhes parcela) {
		try {		
			FacesContext context = FacesContext.getCurrentInstance();
			int HTTP_COD_SUCESSO = 200;

			URL myURL = new URL("https://api.kobana.com.br/v1/bank_billets");			

			JSONObject jsonObj = getJSONBoletoKobana(contrato, parcela);
			byte[] postDataBytes = jsonObj.toString().getBytes();

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			myURLConnection.setUseCaches(false);
			myURLConnection.setRequestMethod("POST");
			myURLConnection.setRequestProperty("Accept", "application/json");
			myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setRequestProperty("Authorization", "Bearer YFfBaw13zZ5CxOOwZIlmDevC2_O-MoVPQwzpz4ejrL8");
			myURLConnection.setRequestProperty("User-Agent", "webnowbr@gmail.com");
		     
			myURLConnection.setDoOutput(true);
			//myURLConnection.getOutputStream().write(postDataBytes);
			
			try(OutputStream os = myURLConnection.getOutputStream()) {
			    byte[] input = jsonObj.toString().getBytes("utf-8");
			    os.write(input, 0, input.length);			
			}
	
			JSONObject myResponse = null;
			int status = myURLConnection.getResponseCode();
			
			myResponse = getJSONSucesso(myURLConnection.getInputStream());			
			
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
				
				TakeBlipMB tkblpMb = new TakeBlipMB();
				PagadorRecebedor pagador;
				pagador = contrato.getPagador();
				//pagador = new PagadorRecebedorDao().findById(10737l);
				tkblpMb.sendWhatsAppMessagePagadorBoleto(pagador, "envio_boleto_cobranca", urlBoleto);
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
	 
	 	if (contrato.getEmpresa().equals("GALLERIA FINANÇAS SECURITIZADORA S.A.")) {
	 		jsonBoleto.put("bank_billet_account_id", 8557);
	 	}
		
		if (contrato.getEmpresa().equals("FIDC GALLERIA")) {
			jsonBoleto.put("bank_billet_account_id", 8555);
	 	}

		if (contrato.getEmpresa().equals("CRI 1")) {
			jsonBoleto.put("bank_billet_account_id", 8558);
		}
		
		if (contrato.getEmpresa().equals("CRI 2")) {
			jsonBoleto.put("bank_billet_account_id", 9251);
		}

		if (contrato.getEmpresa().equals("CRI 3")) {
			jsonBoleto.put("bank_billet_account_id", 9614);
		}
	    /*
	    Número do Contrato
	     */
	    jsonBoleto.put("document_number", contrato.getNumeroContrato());
	    
	    /*
	    Descrição
	     */
	    jsonBoleto.put("description", contrato.getNumeroContrato() + "/" + parcela.getNumeroParcela());
		/*
		Instruções
		 */
	    //jsonBoleto.put("first_instruction", "");
		//jsonBoleto.put("second_instruction", "");
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
	    	jsonBoleto.put("customer_state", "SP");
	    } else {
	    	jsonBoleto.put("customer_state", cliente.getEstado());
	    }
	    
	    if (cliente.getCidade() == null || cliente.getCidade().equals("")) {
	    	jsonBoleto.put("customer_city_name", "Cidade");
	    } else {
	    	jsonBoleto.put("customer_city_name", cliente.getCidade());
	    }
		
	    if (cliente.getCep() == null || cliente.getCep().equals("")) {
	    	jsonBoleto.put("customer_zipcode", "13091-611");
	    } else {
	    	jsonBoleto.put("customer_zipcode", cliente.getCep());
	    }
		
	    if (cliente.getEndereco() == null || cliente.getEndereco().equals("")) {
	    	jsonBoleto.put("customer_address", "Endereço");
	    } else {
	    	jsonBoleto.put("customer_address", cliente.getEndereco());
	    }
	
		jsonBoleto.put("customer_address_complement", cliente.getComplemento());

		jsonBoleto.put("customer_address_number", cliente.getNumero());
		
		if (cliente.getEmail() == null || cliente.getEmail().equals("")) {
			jsonBoleto.put("customer_email", "contato@gmail.com");
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
		jsonBoleto.put("customer_ignore_email", false);
		jsonBoleto.put("ignore_email", false);			
		
		/*Tipo de juros/mora:
		0 Inexistente (Padrão)
		1 Para porcentagem diária
		2 Para valor diário*/
		jsonBoleto.put("interest_type", 2);
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
		//jsonBoleto.put("interest_percentage", interest_percentage);
	    jsonBoleto.put("interest_value", (parcela.getVlrParcela().multiply(BigDecimal.valueOf(0.033))).divide(BigDecimal.valueOf(100)));
		//jsonBoleto.put("interest_value", interest_value);
		jsonBoleto.put("days_for_fine", days_for_fine);
		jsonBoleto.put("fine_percentage", fine_percentage);
		//jsonBoleto.put("days_for_infine_valueterest", fine_value);
		
		jsonBoleto.put("description", "Crédito com Imóvel em Garantia");
		jsonBoleto.put("instructions", "Não receber após 30 dias do vencimento");
		
		JSONObject jsonCustomData = new JSONObject();
		jsonCustomData.put("idContrato", String.valueOf(contrato.getId()));
		jsonCustomData.put("idParcela", String.valueOf(parcela.getId()));
		
		jsonBoleto.put("custom_data", jsonCustomData);
		
		return jsonBoleto;
	}	
	
	public void validateParcelaSelected(SelectEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();
		ContratoCobrancaDetalhes parcela = (ContratoCobrancaDetalhes) event.getObject();
		
		if (parcela.getQtdParcelasVencidas() > 0) {
			this.selectedParcelas.remove(parcela);
			
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"[Kobana - Geração Boleto] Não é possível gerar boleto de contratos com parcelas vencidas!", ""));
		}		
	}
	
	public void validateTodasParcelasSelected() {
		FacesContext context = FacesContext.getCurrentInstance();
		
		for (ContratoCobrancaDetalhes parcelas : this.selectedParcelas) {
			if (parcelas.getQtdParcelasVencidas() > 0) {
				this.selectedParcelas.clear();
				break;
			}
		}
		
		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
				"[Kobana - Geração Boleto] Não é possível gerar boleto de contratos com parcelas vencidas!", ""));	
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
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", locale);
		
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

	public Date getDataHoje() {
		return dataHoje;
	}

	public void setDataHoje(Date dataHoje) {
		this.dataHoje = dataHoje;
	}

	public List<BoletoKobana> getListBoletosKobana() {
		return listBoletosKobana;
	}

	public void setListBoletosKobana(List<BoletoKobana> listBoletosKobana) {
		this.listBoletosKobana = listBoletosKobana;
	}

	public String getFiltroStatus() {
		return filtroStatus;
	}

	public void setFiltroStatus(String filtroStatus) {
		this.filtroStatus = filtroStatus;
	}

	public String getFiltroData() {
		return filtroData;
	}

	public void setFiltroData(String filtroData) {
		this.filtroData = filtroData;
	}

	public ContratoCobranca getContrato() {
		return contrato;
	}

	public void setContrato(ContratoCobranca contrato) {
		this.contrato = contrato;
	}

	public ContratoCobrancaDetalhes getParcela() {
		return parcela;
	}

	public void setParcela(ContratoCobrancaDetalhes parcela) {
		this.parcela = parcela;
	}

	public BigDecimal getValorBoleto() {
		return valorBoleto;
	}

	public void setValorBoleto(BigDecimal valorBoleto) {
		this.valorBoleto = valorBoleto;
	}

	public BoletoKobana getSelectedBoletoKobana() {
		return selectedBoletoKobana;
	}

	public void setSelectedBoletoKobana(BoletoKobana selectedBoletoKobana) {
		this.selectedBoletoKobana = selectedBoletoKobana;
	}

	public PagadorRecebedor getCedente() {
		return cedente;
	}

	public void setCedente(PagadorRecebedor cedente) {
		this.cedente = cedente;
	}

	public String getFiltroEmpresa() {
		return filtroEmpresa;
	}

	public void setFiltroEmpresa(String filtroEmpresa) {
		this.filtroEmpresa = filtroEmpresa;
	}
}