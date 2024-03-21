package com.webnowbr.siscoat.cobranca.faciltech;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.Base64;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import com.webnowbr.siscoat.common.CommonsUtil;

public class WsCredApiServerToServer {
	private final String baseServiceUrl;
	private final String loginOperadorDeComunicacaoServerToServer;
	private final PemSignature pemSignature;

	public WsCredApiServerToServer(
			String baseServiceUrl,
			String loginOperadorDeComunicacaoServerToServer,
			PemSignature pemSignature) throws NoSuchAlgorithmException,
                                              InvalidKeyException {

		this.baseServiceUrl = baseServiceUrl;
		this.loginOperadorDeComunicacaoServerToServer = loginOperadorDeComunicacaoServerToServer;
		this.pemSignature = pemSignature;
	}
	
	public JSONObject get(
			String loginoperadorUser,
			String impersonationBy,
			String apiUrlWithQueryString) throws ClientProtocolException,
                                                 IOException,
                                                 InvalidKeyException,
                                                 NoSuchAlgorithmException,
                                                 NoSuchProviderException,
                                                 SignatureException {
		String url = String.format("%s/wcf/rest%s", this.baseServiceUrl, apiUrlWithQueryString);

		HttpGet request = new HttpGet(url);
		
		String authorizationHeader = buildAuthorizationHeader();

		byte[] dataToSign = apiUrlWithQueryString.getBytes(StandardCharsets.UTF_8);
		String signatureInBase64 = buildSignature(dataToSign);

		request.addHeader("Accept", "application/json");
		request.addHeader("Content-Type", "application/json");
		request.addHeader("Authorization", authorizationHeader);
		request.addHeader("Signature", signatureInBase64);

		if (!CommonsUtil.semValor(loginoperadorUser)) {
			request.addHeader("Impersonate-Operator", loginoperadorUser);
		}
		
		if (!CommonsUtil.semValor(impersonationBy)) {
			request.addHeader("Impersonate-User", impersonationBy);
		}

		String responseText = WsCredHttpClient.getResponse(request);
		JSONObject result = new JSONObject(responseText);

		return result;
	}

	public JSONObject post(
			String loginoperadorUser,
			String impersonationBy,
			String apiUrlWithQueryString,
			JSONObject body) throws ClientProtocolException,
                                    IOException,
                                    InvalidKeyException,
                                    NoSuchAlgorithmException,
                                    NoSuchProviderException,
                                    SignatureException {
		String url = String.format("%s/wcf/rest%s", this.baseServiceUrl, apiUrlWithQueryString);

		HttpPost request = new HttpPost(url);

		String authorizationHeader = buildAuthorizationHeader();

		byte[] dataToSign = apiUrlWithQueryString.getBytes(StandardCharsets.UTF_8);

		if (null != body) {
			String bodyText = body.toString();
			dataToSign = bodyText.getBytes(StandardCharsets.UTF_8);
			HttpEntity requestEntity = new StringEntity(bodyText, StandardCharsets.UTF_8);
			request.setEntity(requestEntity);
		}

		String signatureInBase64 = buildSignature(dataToSign);

		request.addHeader("Accept", "application/json");
		request.addHeader("Content-Type", "application/json");
		request.addHeader("Authorization", authorizationHeader);
		request.addHeader("Signature", signatureInBase64);

		if (!CommonsUtil.semValor(loginoperadorUser)){
			request.addHeader("Impersonate-Operator", loginoperadorUser);
		}
		if (!CommonsUtil.semValor(impersonationBy)){
			request.addHeader("Impersonate-User", impersonationBy);
		}

		String responseText = WsCredHttpClient.getResponse(request);
		JSONObject result = new JSONObject(responseText);

		return result;
	}
	
	protected String buildAuthorizationHeader() throws InvalidKeyException,
                                                       NoSuchAlgorithmException,
                                                       NoSuchProviderException,
                                                       SignatureException {
		Date date = new Date();
		Base64.Encoder base64Encoder = Base64.getEncoder();

		JSONObject jwtClaims = new JSONObject();
		jwtClaims.put("sub", loginOperadorDeComunicacaoServerToServer);
		jwtClaims.put("jti", date.getTime());

		/// {"alg":"ES256","typ":"JWT"} -> ES256 = SHA256withECDSA = 1.2.840.10045.4.3.2 (OID - ASN.1) 
		String jwtHeader = "eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCJ9";

		String jwtBody = base64Encoder.encodeToString(jwtClaims.toString().getBytes(StandardCharsets.UTF_8))
				.replace('+', '-')
                .replace('/', '_')
                .replace("=", "");
		String jwtHeaderAndBody = String.format("%s.%s", jwtHeader, jwtBody);

		String jwtSignature = buildSignature(jwtHeaderAndBody.getBytes(StandardCharsets.UTF_8))
				.replace('+', '-')
                .replace('/', '_')
                .replace("=", "");

		String result = String.format("Bearer %s.%s", jwtHeaderAndBody, jwtSignature);
		return result;
	}
	
	protected String buildSignature(
			byte[] data) throws InvalidKeyException,
	                            NoSuchAlgorithmException,
	                            NoSuchProviderException,
	                            SignatureException {
		Base64.Encoder base64Encoder = Base64.getEncoder();
		byte[] signatureInBytes = this.pemSignature.Sign(data);
		String result = base64Encoder.encodeToString(signatureInBytes);
		return result;
	}
}
