package com.webnowbr.siscoat.cobranca.mb;

import com.starkbank.ellipticcurve.Signature;
import com.webnowbr.siscoat.cobranca.db.model.TransferenciasIUGU;
import com.webnowbr.siscoat.cobranca.db.model.TransferenciasObservacoesIUGU;
import com.webnowbr.siscoat.cobranca.db.op.TransferenciasObservacoesIUGUDao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.json.JSONArray;
import org.json.JSONObject;

import com.starkbank.Balance;
import com.starkbank.Project;
import com.starkbank.Settings;
import com.starkbank.ellipticcurve.Ecdsa;
import com.starkbank.ellipticcurve.PrivateKey;

@ManagedBean(name = "starkBankAPI")
@SessionScoped
public class StarkBankAPI{
    
	/*
	 * VALIDAÇÃO DAS CHAVES DE SEGURANÇA
    public static void main(String[] args){
    
        String publicKeyPem = File.read("src/resource/publicKey.pem");
        byte[] signatureBin = File.readBytes("src/resource/signatureBinary.txt");
        String message = File.read("src/resource/message.txt");

        ByteString byteString = new ByteString(signatureBin);

        PublicKey publicKey = PublicKey.fromPem(publicKeyPem);
        Signature signature = Signature.fromDer(byteString);

        // Get verification status:
        boolean verified = Ecdsa.verify(message, signature, publicKey);
        System.out.println("Verification status: " + verified);
    }
    */
	
    public static void main(String[] args){
        
    	getBalanceGalleriaBank();
    	getBalanceGalleriaCoban();
    }
	
    // galleriabank.starkbank.com
    public static void getBalanceGalleriaBank() {
    	String privateKeyContent = "-----BEGIN EC PRIVATE KEY-----\nMHQCAQEEICkEY8553LgXMJb6V0M14aIjTS3PD5uybPmky14jUXnYoAcGBSuBBAAKoUQDQgAEYZgMojBpGA7zunc5h/cvqvIv7rReFVS8YXMvPQI8JwBB16VnqfBlhAVNwqoSddJyy/p6F+qs4uBKGQO5txcdEw==\n-----END EC PRIVATE KEY-----";
    			
    	Settings.language = "pt-BR";
    	
    	Balance balance;
		try {
			Project project = new Project(
				    "production",
				    "6200575645450240",
				    privateKeyContent
				);
			
			balance = Balance.get(project);
			System.out.println(balance);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    // galleriabank.starkbank.com
    public static void getBalanceGalleriaCoban() {
    	String privateKeyContent = "-----BEGIN EC PRIVATE KEY-----\nMHQCAQEEIAKGZfO+lee7tdtcLGbCT++oGyUcmm/2Jdozg8D8mF4ioAcGBSuBBAAKoUQDQgAEUvi66NomZ1HeFqEwrXvnM/IjDQEJjVp6nYYojlOsTP1tYO34tW+bO1ypWln5lfkDNCcARQ710SmPPrLRHRbMAA==\n-----END EC PRIVATE KEY-----";
    			
    	Settings.language = "pt-BR";
    	
    	Balance balance;
		try {
			Project project = new Project(
				    "production",
				    "5092647223951360",
				    privateKeyContent
				);
			
			balance = Balance.get(project);
			System.out.println(balance);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
    
	public void getBalanceXXX() {
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

}