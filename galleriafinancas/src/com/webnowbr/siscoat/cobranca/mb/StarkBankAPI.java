package com.webnowbr.siscoat.cobranca.mb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.json.JSONArray;
import org.json.JSONObject;

import com.starkbank.Balance;
import com.starkbank.BoletoPayment;
import com.starkbank.DictKey;
import com.starkbank.Project;
import com.starkbank.Settings;
import com.starkbank.Transfer;
import com.starkbank.ellipticcurve.Ecdsa;
import com.starkbank.ellipticcurve.PrivateKey;
import com.starkbank.ellipticcurve.Signature;
import com.starkbank.utils.Generator;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.PagadorRecebedor;
import com.webnowbr.siscoat.cobranca.db.model.StarkBankBoleto;
import com.webnowbr.siscoat.cobranca.db.model.StarkBankPix;
import com.webnowbr.siscoat.cobranca.db.model.StarkBankTax;
import com.webnowbr.siscoat.cobranca.db.op.PagadorRecebedorDao;
import com.webnowbr.siscoat.cobranca.db.op.StarkBankBoletoDAO;
import com.webnowbr.siscoat.cobranca.db.op.StarkBankPixDAO;
import com.webnowbr.siscoat.cobranca.db.op.StarkBankTaxDAO;
import com.webnowbr.siscoat.common.DateUtil;

@ManagedBean(name = "starkBankAPI")
@SessionScoped
public class StarkBankAPI{
	
	private Date dataInicio;
	private Date dataFim;
	private List<StarkBankBoleto> listBoletos;
    
	/*
	 * VALIDAÇÃO DAS CHAVES DE SEGURANÇA*/
    public static void main(String[] args){
    	//transfer();
    	getBalanceSDK();
    
    /*
        String publicKeyPem = File.read("src/resource/publicKey.pem");
        byte[] signatureBin = File.readBytes("src/resource/signatureBinary.txt");
        String message = File.read("src/resource/message.txt");

        ByteString byteString = new ByteString(signatureBin);

        PublicKey publicKey = PublicKey.fromPem(publicKeyPem);
        Signature signature = Signature.fromDer(byteString);

        // Get verification status:
        boolean verified = Ecdsa.verify(message, signature, publicKey);
        System.out.println("Verification status: " + verified);
        */
    }

    
    public void testeAutenticacaoManual() {
    	// Set your user Id
    	String accessId = "project/5465772415516672";
    	
    	// Get current Unix Time
    	Date date = new Date();
    	long time = date.getTime() / 1000;
    	String accessTime = String.valueOf(time);
    	
    	
    	// Get you json content in string format for POST/PUT/PATCH requests
    	// or empty string for GET/DELETE requests.
    	String bodyString = "";
    	
    	// Build the message in this format:
    	String message = accessId + ":" + accessTime + ":" + bodyString;
    	
    	// Load your private key from its pem string
    	PrivateKey privateKey = PrivateKey.fromPem("src/resource/privateKey.pem");
    	
    	// Use ECDSA to sign the message
    	Signature signature = Ecdsa.sign(message, privateKey);
    	
    	// Convert the signature to base 64
    	String accessSignature = signature.toBase64();
    	
    	
    }
    
	public void getBalance() {
    	// Set your user Id
    	String accessId = "project/5465772415516672";
    	
    	// Get current Unix Time
    	Date date = new Date();
    	long time = date.getTime() / 1000;
    	String accessTime = String.valueOf(time);
    	
    	
    	// Get you json content in string format for POST/PUT/PATCH requests
    	// or empty string for GET/DELETE requests.
    	String bodyString = "";
    	
    	// Build the message in this format:
    	String message = accessId + ":" + accessTime + ":" + bodyString;
    	
    	// Load your private key from its pem string
    	PrivateKey privateKey = PrivateKey.fromPem("src/resource/privateKey.pem");
    	
    	// Use ECDSA to sign the message
    	Signature signature = Ecdsa.sign(message, privateKey);
    	
    	// Convert the signature to base 64
    	String accessSignature = signature.toBase64();
    	
		try {						
			FacesContext context = FacesContext.getCurrentInstance();
			
			/*

			curl --location --request GET '{{baseUrl}}/v2/balance' \
			--header 'Access-Id: {{accessId}}' \
			--header 'Access-Time: {{accessTime}}' \
			--header 'Access-Signature: {{accessSignature}}'

			 */

			int HTTP_COD_SUCESSO = 200;

			boolean valid = true;

			URL myURL = new URL("https://api.starkbank.com/v2/balance");

			if (valid) {
				HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
				myURLConnection.setUseCaches(false);
				myURLConnection.setRequestMethod("GET");
				myURLConnection.setRequestProperty("Accept", "application/json");
				myURLConnection.setRequestProperty("Accept-Charset", "utf-8");
				myURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
				
				myURLConnection.setRequestProperty("Access-Id", accessId);
				myURLConnection.setRequestProperty("Access-Time", accessTime);
				myURLConnection.setRequestProperty("Access-Signature", accessSignature);
				myURLConnection.setDoOutput(true);

				String erro = "";
				JSONObject myResponse = null;

				/**
				 * TODO SALVAR NO BANCO O ID DE TODAS AS TRANSFERENCIAS
				 * USAR ESTE ID PARA BUSCAR O STATUS DA TRANSFERENCIA
				 * https://api.iugu.com/v1/withdraw_requests/id"
				 */
				if (myURLConnection.getResponseCode() != HTTP_COD_SUCESSO) {	

					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "EROOOOOO!", ""));

				} else {							
					myResponse = getJsonSucesso(myURLConnection.getInputStream());

					context.addMessage(null, new FacesMessage(
							FacesMessage.SEVERITY_INFO, "Consultar Transferências Bancárias: Consulta efetuada com sucesso!", ""));

				}

				myURLConnection.disconnect();
			}
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
	 * PARSE DO RETORNO SUCESSO IUGU
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
    
    public static void getBalanceSDK() {
    	String privateKeyContent = "-----BEGIN EC PARAMETERS-----\nBgUrgQQACg==\n-----END EC PARAMETERS-----\n-----BEGIN EC PRIVATE KEY-----\nMHQCAQEEIAKGZfO+lee7tdtcLGbCT++oGyUcmm/2Jdozg8D8mF4ioAcGBSuBBAAKoUQDQgAEUvi66NomZ1HeFqEwrXvnM/IjDQEJjVp6nYYojlOsTP1tYO34tW+bO1ypWln5lfkDNCcARQ710SmPPrLRHRbMAA==\n-----END EC PRIVATE KEY-----";
    	
    	Settings.language = "pt-BR";
    	
    	Balance balance;
		try {
			Project project = new Project(
				    "production",
				    //"5465772415516672", // financas
				    "5092647223951360", // coban
				    privateKeyContent
				);
			
			balance = Balance.get(project);
			System.out.println(balance);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }    
 
    public String getLinhaDigitavel() {
		return linhaDigitavel;
	}


	public void setLinhaDigitavel(String linhaDigitavel) {
		this.linhaDigitavel = linhaDigitavel;
	}


	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public void consultarBoletos() {
		loginStarkBank();
		this.listBoletos = new ArrayList<StarkBankBoleto>();
		StarkBankBoleto boleto = new StarkBankBoleto();
		
		try {
			HashMap<String, Object> params = new HashMap<>();
			params.put("after", DateUtil.getDataAmericano(this.dataInicio));
			params.put("before", DateUtil.getDataAmericano(this.dataFim));
			Generator<BoletoPayment> payments;
			payments = BoletoPayment.query(params);

			for (BoletoPayment payment : payments){
				boleto = new StarkBankBoleto();
			    
	    		String tagsStr = "";
	    		if (payment.tags.length > 0) {
	    			for (String tag : payment.tags) {
	    				if (tag.equals("")) {
	    					tagsStr = tag;
	    				} else {
	    					tagsStr = tagsStr + " | " + tag;
	    				}
	    			}
	    		}
	    		
	    		String retornoLog = paymentBoletoLog(payment.id);
	    		
	    		String valorTratadoStr = payment.amount.toString();
	    		
	    		if (valorTratadoStr.length() >= 3) {
	    			valorTratadoStr = valorTratadoStr.substring(0, valorTratadoStr.length() - 2) + "." + valorTratadoStr.substring(valorTratadoStr.length() - 2, valorTratadoStr.length());
	    		}
	    		
	    		double valorTratadoLong = Double.valueOf(valorTratadoStr);
			    
			    boleto = new StarkBankBoleto(Long.valueOf(payment.id), BigDecimal.valueOf(valorTratadoLong).setScale(2), payment.taxId, tagsStr, payment.description, payment.scheduled,
	    				payment.line, payment.barCode, payment.fee, payment.status, DateUtil.convertDateTimeToDate(payment.created), retornoLog);
	    		
			    
			    this.listBoletos.add(boleto);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void listPaymentBoleto() {
		loginStarkBank();
		
		try {
			HashMap<String, Object> params = new HashMap<>();
			params.put("after", "2023-05-01");
			params.put("before", "2023-12-31");
			Generator<BoletoPayment> payments;
			payments = BoletoPayment.query(params);

			for (BoletoPayment payment : payments){
			    System.out.println(payment);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String paymentBoletoLog(String idBoleto) {
		
		loginStarkBank();
		
		String retorno = "";
	
		try {
			HashMap<String, Object> params = new HashMap<>();
			params.put("paymentIds", idBoleto);
			Generator<BoletoPayment.Log> logs = BoletoPayment.Log.query(params);
			
			for (BoletoPayment.Log log : logs){
			    if (log.type.equals("failed")) {
			    	for (String erro : log.errors) {
			    		retorno = retorno + erro;
			    	}
			    }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
		
		return retorno;
	}
	
	public void paymentBoletoLog() {
	
		loginStarkBank();
	
		try {
			HashMap<String, Object> params = new HashMap<>();
			params.put("paymentIds", "4991624148942848");
			Generator<BoletoPayment.Log> logs = BoletoPayment.Log.query(params);
			
			for (BoletoPayment.Log log : logs){
			    System.out.println(log);			    
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
	}
	
    public String linhaDigitavel = "";
    public String descricao = "";
    
    public void chamaBoletoStarkTela() { 
		FacesContext context = FacesContext.getCurrentInstance();
		
    	PagadorRecebedor pagador = new PagadorRecebedor();
    	PagadorRecebedorDao pDao = new PagadorRecebedorDao();
    	pagador = pDao.findById((long) 7);
    	
    	
    	paymentBoleto(linhaDigitavel, null, pagador, descricao, null, null);
    	
    	context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Boleto Stark Bank - Pago", ""));
    	
    }
    
	public StarkBankTax paymentTax(String barCode, String descricao) {

		FacesContext context = FacesContext.getCurrentInstance();
		StarkBankTax taxTransacao = null;
    	String[] tags = {""};
    	
    	loginStarkBank();

    	Settings.language = "pt-BR";

    	try {
	    	List<BoletoPayment> payments = new ArrayList<>();
	    	HashMap<String, Object> data = new HashMap<>();
	    	data.put("barCode", barCode);    	

	    	data.put("scheduled", DateUtil.getDataHojeAmericano());
	    	data.put("description", descricao);
	
			payments.add(new BoletoPayment(data));
	
	    	payments = BoletoPayment.create(payments);

	    	for (BoletoPayment payment : payments){
	    		String tagsStr = "";
	    		if (payment.tags.length > 0) {
	    			for (String tag : tags) {
	    				if (tag.equals("")) {
	    					tagsStr = tag;
	    				} else {
	    					tagsStr = tagsStr + " | " + tag;
	    				}
	    			}
	    		}
	    
	    		taxTransacao = new StarkBankTax(Long.valueOf(payment.id), BigDecimal.valueOf(payment.amount), tagsStr, payment.description, payment.scheduled,
	    				payment.line, payment.barCode, payment.status, DateUtil.convertDateTimeToDate(payment.created), null, null);
	    		
	    		StarkBankTaxDAO starkBankTaxDAO = new StarkBankTaxDAO();
	    		starkBankTaxDAO.create(taxTransacao);
	    		
	    		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"[StarkBank - Pagamento Imposto] Imposto Pago com Sucesso! Transação: " + payment.id, ""));
	    	}
    	
		} catch (Exception e) {
			JSONObject erroStarkBank = new JSONObject(e.getMessage());
			JSONArray erros = new JSONArray(erroStarkBank.getJSONArray("errors"));
			
			String errosStrStarkBank = "";
			for (int i = 0; i < erros.length(); i++) {
				JSONObject dados = erros.getJSONObject(i);
				
				if (errosStrStarkBank.equals("")) {
					errosStrStarkBank = i + "-" + dados.getString("message").replace("Element 0: ", "");
				} else {
					errosStrStarkBank = errosStrStarkBank + " | " + i + "-" + dados.getString("message").replace("Element 0: ", "");
				}
			}	
			
			if (!errosStrStarkBank.equals("")) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"[StarkBank - Pagamento Boleto] Falha no pagamento: " + errosStrStarkBank, ""));
			}
		}
    	
    	 return taxTransacao;
    }

	public StarkBankBoleto paymentBoleto(String boleto, ContratoCobranca contrato, PagadorRecebedor pessoa, String descricao, String documentoPagadorCustom, String descricaoConta) {

		FacesContext context = FacesContext.getCurrentInstance();
    	StarkBankBoleto boletoTransacao = null;
    	String[] tags = {""};
    	
    	if (descricaoConta == null || !descricaoConta.equals("Pagamento Carta Split")) {
    		loginStarkBank();
    	} else {
    		loginStarkBankSCD();
    	}
    	
    	Settings.language = "pt-BR";

    	try {
	    	List<BoletoPayment> payments = new ArrayList<>();
	    	HashMap<String, Object> data = new HashMap<>();
	    	data.put("line", boleto);
	    	
	    	if (documentoPagadorCustom == null || documentoPagadorCustom.equals("")) {
		    	if (pessoa.getCpf() != null && !pessoa.getCpf().equals("")) {
		    		data.put("taxId", pessoa.getCpf());	
		    	} else {
		    		data.put("taxId", pessoa.getCnpj());	
		    	}
	    	} else {
	    		data.put("taxId", documentoPagadorCustom);	
	    	}	    	

	    	data.put("scheduled", DateUtil.getDataHojeAmericano());
	    	data.put("description", descricao);
	    	
	    	String numeroContrato = "";
	    	
	    	// atribui tags
	    	if (contrato != null) {
	    	  	tags[0] = contrato.getNumeroContrato();
	    	  	data.put("tags", tags);
	    	}
	
			payments.add(new BoletoPayment(data));
	
	    	payments = BoletoPayment.create(payments);

	    	for (BoletoPayment payment : payments){
	    		String tagsStr = "";
	    		if (payment.tags.length > 0) {
	    			for (String tag : tags) {
	    				if (tag.equals("")) {
	    					tagsStr = tag;
	    				} else {
	    					tagsStr = tagsStr + " | " + tag;
	    				}
	    			}
	    		}
	    		
	    		/*
	    		 * TODO REVER PARSE DA DATA 
	    		 */
	    		
	    		boletoTransacao = new StarkBankBoleto(Long.valueOf(payment.id), BigDecimal.valueOf(payment.amount), payment.taxId, tagsStr, payment.description, payment.scheduled,
	    				payment.line, payment.barCode, payment.fee, payment.status, DateUtil.convertDateTimeToDate(payment.created), null, null);
	    		
	    		StarkBankBoletoDAO starkBankBoletoDAO = new StarkBankBoletoDAO();
	    		starkBankBoletoDAO.create(boletoTransacao);
	    		
	    		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"[StarkBank - Pagamento Boleto] Boleto Pago com Sucesso! Transação: " + payment.id, ""));
	    	}
    	
		} catch (Exception e) {
			JSONObject erroStarkBank = new JSONObject(e.getMessage());
			JSONArray erros = new JSONArray(erroStarkBank.getJSONArray("errors"));
			
			String errosStrStarkBank = "";
			for (int i = 0; i < erros.length(); i++) {
				JSONObject dados = erros.getJSONObject(i);
				
				if (errosStrStarkBank.equals("")) {
					errosStrStarkBank = i + "-" + dados.getString("message").replace("Element 0: ", "");
				} else {
					errosStrStarkBank = errosStrStarkBank + " | " + i + "-" + dados.getString("message").replace("Element 0: ", "");
				}
			}	
			
			if (!errosStrStarkBank.equals("")) {
				context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"[StarkBank - Pagamento Boleto] Falha no pagamento: " + errosStrStarkBank, ""));
			}
		}
    	
    	 return boletoTransacao;
    }
	
	public static void loginStarkBank() {
		System.out.println("processaPagamentoStarkBank - Login Correspondente");
    	Settings.language = "pt-BR";
    	
    	String privateKeyContent = "-----BEGIN EC PARAMETERS-----\nBgUrgQQACg==\n-----END EC PARAMETERS-----\n-----BEGIN EC PRIVATE KEY-----\nMHQCAQEEIAKGZfO+lee7tdtcLGbCT++oGyUcmm/2Jdozg8D8mF4ioAcGBSuBBAAKoUQDQgAEUvi66NomZ1HeFqEwrXvnM/IjDQEJjVp6nYYojlOsTP1tYO34tW+bO1ypWln5lfkDNCcARQ710SmPPrLRHRbMAA==\n-----END EC PRIVATE KEY-----";
    	
    	Project project;
		try {
			project = new Project(
				    "production",
				    //"5465772415516672", // financas
				    "5092647223951360", // coban
				    privateKeyContent
				);
			
	    	Settings.user = project;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void loginStarkBankSCD() {
		System.out.println("processaPagamentoStarkBank - Login SCD");
		
    	Settings.language = "pt-BR";
    	
    	String privateKeyContent = "-----BEGIN EC PRIVATE KEY-----\nMHQCAQEEIE56ofDNqzdDCrYEAnwpq2CiEfknPhBV+NTWBtpYEy90oAcGBSuBBAAK\noUQDQgAEsXCtulqc9kC5Tkmy/wuJ6JIq8R+GWJzlqmp/pO4r4i76BFivs4hVBZrS\nD5Sil3MxCjUjKbr95ZxDjuq4dYCBOA==\n-----END EC PRIVATE KEY-----";
    	
    	Project project;
		try {
			project = new Project(
				    "production",
				    //"5465772415516672", // financas
				    //"5092647223951360", // coban
				    "6562009682280448",
				    privateKeyContent
				);
			
	    	Settings.user = project;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
    
    public static void transfer() {
    	FacesContext context = FacesContext.getCurrentInstance();
    	
    	List<Transfer> transfers = new ArrayList<>();

    	List<Transfer.Rule> rules = new ArrayList<>();
    
    	loginStarkBank(); 
    	
    	try {
	    	rules.add(new Transfer.Rule("resendingLimit", 5));
	
	    	HashMap<String, Object> data = new HashMap<>();
	    	data.put("amount", 100);
	    	data.put("bankCode", "00000000");
	    	data.put("branchCode", "1515-6");
	    	data.put("accountNumber", "131094-1");
	    	data.put("taxId", "34.787.885/0001-32");
	    	data.put("name", "GALLERIA BANK");
	    	data.put("externalId", "transactionTest002");
	    	//data.put("scheduled", "2020-08-14");
	    	//data.put("tags", new String[]{"daenerys", "invoice/1234"});
	    	//data.put("rules", rules);
	    	
			transfers.add(new Transfer(data));
			
			transfers = Transfer.create(transfers);

	    	for (Transfer transfer : transfers){
	    	    System.out.println(transfer);
	    	}
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "StarkBank PIX: Ocorreu um problema ao fazer PIX/TED! Erro: " + e, ""));
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }    
    
    public StarkBankPix paymentPix(String codigoPixBanco, String agencia, String numeroConta, String documento, String nomeBeneficiario, BigDecimal valor, String tipoOperacao, String descricaoConta) {
    	FacesContext context = FacesContext.getCurrentInstance();
    	
    	List<Transfer> transfers = new ArrayList<>();

    	List<Transfer.Rule> rules = new ArrayList<>();
    
    	if (descricaoConta != null || descricaoConta.contains("Pagamento Carta Split")) {
    		loginStarkBankSCD();
    	} else {
    		loginStarkBank();    		
    	}
    	
    	Date dataHoje = DateUtil.gerarDataHoje();
    	
    	try {
	    	rules.add(new Transfer.Rule("resendingLimit", 5));
	    	
	    	// get dados chave PIX
			DictKey dictKey = DictKey.get(codigoPixBanco);
			
			if (dictKey != null && !dictKey.ispb.equals("")) {
		    	HashMap<String, Object> data = new HashMap<>();
		    	String valorStr = valor.toString();
		    	data.put("amount", Long.valueOf(valorStr.replace(".", "").replace(",", "")));		    	 
		    	data.put("bankCode", dictKey.ispb);
		    	data.put("branchCode", dictKey.branchCode);
		    	data.put("accountNumber", dictKey.accountNumber);
		    	data.put("taxId", documento);
		    	data.put("name", dictKey.name);
		    	data.put("externalId", "PagamentoPix-" + dictKey.name.replace(" ", "-") + "-" + DateUtil.todayInMilli());
		    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		    	data.put("scheduled", sdf.format(DateUtil.gerarDataHoje()));
		    	//data.put("tags", new String[]{"daenerys", "invoice/1234"});
		    	//data.put("rules", rules);
		    	
				transfers.add(new Transfer(data));
				
				transfers = Transfer.create(transfers);
	
				StarkBankPix pixTransacao = new StarkBankPix();
		    	for (Transfer transfer : transfers){
					 pixTransacao.setId(Long.valueOf(transfer.id));
					 pixTransacao.setCreated(DateUtil.convertDateTimeToDate(transfer.created));
					 pixTransacao.setScheduled(transfer.scheduled);
					 pixTransacao.setNomeComprovante(nomeBeneficiario);
					 pixTransacao.setAmount(valor);
					 pixTransacao.setTaxId(documento);
					 pixTransacao.setPathComprovante(null);
					 pixTransacao.setNomeComprovante(null);
		    	}
		    	
		    	StarkBankPixDAO starkBankPixDAO = new StarkBankPixDAO();
		    	starkBankPixDAO.create(pixTransacao);
		    	
		    	context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_INFO, "StarkBank PIX: Pagamento efetuado com sucesso!", ""));
		    	
		    	return pixTransacao;
			} else {
				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "StarkBank PIX: Ocorreu um problema ao fazer PIX com a chave: " + codigoPixBanco + "!", ""));
				
				return null;
			}
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "StarkBank PIX: Ocorreu um problema ao fazer PIX! Erro: " + e.getMessage(), "")); 
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return null;
    }
    	
    public StarkBankPix paymentTED(String codigoBanco, String agencia, String numeroConta, String documento, String nomeBeneficiario, BigDecimal valor, String tipoOperacao, String descricaoConta) {
    	FacesContext context = FacesContext.getCurrentInstance();
    	
    	List<Transfer> transfers = new ArrayList<>();

    	List<Transfer.Rule> rules = new ArrayList<>();
    
    	if (descricaoConta != null || descricaoConta.contains("Pagamento Carta Split")) {
    		loginStarkBankSCD();
    	} else {
    		loginStarkBank();    		
    	}
    	
    	Date dataHoje = DateUtil.gerarDataHoje();
    	
    	try {
	    	rules.add(new Transfer.Rule("resendingLimit", 5));
	
	    	HashMap<String, Object> data = new HashMap<>();
	    	String valorStr = valor.toString();
	    	data.put("amount", Long.valueOf(valorStr.replace(".", "").replace(",", "")));	
	    	data.put("bankCode", codigoBanco);
	    	data.put("branchCode", agencia);
	    	data.put("accountNumber", numeroConta);
	    	data.put("taxId", documento);
	    	data.put("name", nomeBeneficiario);
	    	data.put("externalId", "PagamentoTED-" + nomeBeneficiario.replace(" ", "") + DateUtil.todayInMilli());
	    	//data.put("scheduled", "2020-08-14");
	    	//data.put("tags", new String[]{"daenerys", "invoice/1234"});
	    	//data.put("rules", rules);
	    	
	    	System.out.println("Payment TED Payload - " + data);
	    	
			transfers.add(new Transfer(data));
			
			transfers = Transfer.create(transfers);

			StarkBankPix pixTransacao = new StarkBankPix();
	    	for (Transfer transfer : transfers){
				 pixTransacao.setId(Long.valueOf(transfer.id));
				 pixTransacao.setCreated(DateUtil.convertDateTimeToDate(transfer.created));
				 pixTransacao.setScheduled(transfer.scheduled);
				 pixTransacao.setNomeComprovante(nomeBeneficiario);
				 pixTransacao.setAmount(valor);
				 pixTransacao.setTaxId(documento);
				 pixTransacao.setPathComprovante(null);
				 pixTransacao.setNomeComprovante(null);
	    	}
	    	
	    	StarkBankPixDAO starkBankPixDAO = new StarkBankPixDAO();
	    	starkBankPixDAO.create(pixTransacao);
	    	
	    	context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_INFO, "StarkBank TED: Pagamento efetuado com sucesso!", ""));
	    	
	    	return pixTransacao;
		} catch (Exception e) {
			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "StarkBank TED: Ocorreu um problema ao fazer TED! Erro: " + e, ""));
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return null;
    }
    
    public StarkBankPix paymentPixTest(String codigoPixBanco, String agencia, String numeroConta, String documento, String nomeBeneficiario, BigDecimal valor) {
    	StarkBankPix starkBankPix = new StarkBankPix();
    	
    	starkBankPix.setId(212);
    	starkBankPix.setCreated(DateUtil.gerarDataHoje());
    	starkBankPix.setScheduled("sadsad");
    	starkBankPix.setNomeComprovante("nome");
    	starkBankPix.setAmount(BigDecimal.TEN);
    	starkBankPix.setTaxId("docu");
    	starkBankPix.setPathComprovante(null);
    	 starkBankPix.setNomeComprovante(null);
    	 
    	 StarkBankPixDAO starkBankPixDAO = new StarkBankPixDAO();
	    	starkBankPixDAO.create(starkBankPix);
	    	
    	 return starkBankPix;
    }
    
	public Date gerarDataHoje() {
		TimeZone zone = TimeZone.getDefault();
		Locale locale = new Locale("pt", "BR");
		Calendar dataHoje = Calendar.getInstance(zone, locale);

		return dataHoje.getTime();
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}
	
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public List<StarkBankBoleto> getListBoletos() {
		return listBoletos;
	}

	public void setListBoletos(List<StarkBankBoleto> listBoletos) {
		this.listBoletos = listBoletos;
	}
}