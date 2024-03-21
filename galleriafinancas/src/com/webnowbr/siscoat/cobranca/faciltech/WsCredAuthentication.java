package com.webnowbr.siscoat.cobranca.faciltech;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;

public class WsCredAuthentication {
	private final String baseServiceUrl;

	public WsCredAuthentication(String baseServiceUrl) {
		this.baseServiceUrl = baseServiceUrl;
	}

	public String getPublicKey() throws ClientProtocolException, IOException {
		String url = String.format("%s/wcf/rest/maintenance.svc/GetPublicKey", this.baseServiceUrl);

		HttpGet request = new HttpGet(url);
		request.addHeader("Accept", "application/json");
		request.addHeader("Content-Type", "application/json");

		String result = WsCredHttpClient.getResponse(request); 
		return result;
	}

	public String buildAuthenticationData(JSONObject publicKey, String username, String password, byte[] key, byte[] iv)
			throws InvalidCipherTextException {
		String modulusInBase64 = publicKey.getString("Modulus");
		BigInteger modulusInBigInteger = convertBase64StringToBigInteger(modulusInBase64);

		String exponentInBase64 = publicKey.getString("Exponent");
		BigInteger ExponentInBigInteger = convertBase64StringToBigInteger(exponentInBase64);

		JSONObject authenticationRequestObject = buildAuthenticationRequestObject(username, password, key, iv);
		String authenticationRequestJsonText = authenticationRequestObject.toString();
		byte[] authenticationRequestInBytes = authenticationRequestJsonText.getBytes(StandardCharsets.UTF_8);

		byte[] data = encryptToRsa(modulusInBigInteger, ExponentInBigInteger, authenticationRequestInBytes);
		Base64.Encoder base64Encoder = Base64.getEncoder();
		String result = base64Encoder.encodeToString(data);

		return result;
	}

	public String authenticate(String publicKeyUid, String authenticationData)
			throws ClientProtocolException, IOException {
		String url = String.format("%s/wcf/rest/maintenance.svc/Authenticate", this.baseServiceUrl);

		HttpPost request = new HttpPost(url);
		request.addHeader("Accept", "application/json");
		request.addHeader("Content-Type", "application/json");
		request.addHeader("PublicKeyUid", publicKeyUid);

		String formattedAuthenticationData = String.format("\"%s\"", authenticationData);
		HttpEntity requestEntity = new StringEntity(formattedAuthenticationData, StandardCharsets.UTF_8);
		request.setEntity(requestEntity);

		String result = WsCredHttpClient.getResponse(request); 
		return result;
	}
	
	public String decryptAuthentication(String encrypedtAuthenticationResponse,  byte[] key, byte[] iv)
			throws GeneralSecurityException, InvalidKeyException {
		Base64.Decoder base64Decoder = Base64.getDecoder();
		byte[] encrypedtAuthenticationResponseInBytes = base64Decoder.decode(encrypedtAuthenticationResponse);
		
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", new BouncyCastleProvider());
		
		SecretKey secretKey = new SecretKeySpec(key, "AES"); 
		AlgorithmParameterSpec ivParameterSpec = new IvParameterSpec(iv);
		
		cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
		byte[] decrypedtAuthenticationResponseInBytes = cipher.doFinal(encrypedtAuthenticationResponseInBytes);
		String result = new String(decrypedtAuthenticationResponseInBytes, StandardCharsets.UTF_8);

		return result;
	}
	
	private static JSONObject buildAuthenticationRequestObject(
			String username, String password, byte[] key, byte[] iv) {
		JSONObject userAuthentication = new JSONObject();
		userAuthentication.put("Username", username);
		userAuthentication.put("Password", password);

		Base64.Encoder base64Encoder = Base64.getEncoder();
		String keyInBase64 = base64Encoder.encodeToString(key);
		String ivInBase64 = base64Encoder.encodeToString(iv);

		JSONObject symmetricCryptoKey = new JSONObject();
		symmetricCryptoKey.put("Key", keyInBase64);
		symmetricCryptoKey.put("IV", ivInBase64);

		JSONObject result = new JSONObject();
		result.put("UserAuthentication", userAuthentication);
		result.put("SymmetricCryptoKey", symmetricCryptoKey);

		return result;
	}

	private static byte[] encryptToRsa(BigInteger modulus, BigInteger exponent, byte[] inputData)
			throws InvalidCipherTextException {
		RSAKeyParameters publicKey = new RSAKeyParameters(false, modulus, exponent);

		RSAEngine engine = new RSAEngine();
		PKCS1Encoding encodedEngine = new PKCS1Encoding(engine);

		encodedEngine.init(true, publicKey);

		int keySizeInBytes = modulus.bitLength() >> 3;
		int maxBlockLength = keySizeInBytes - 11;
		byte[][] blocks = divideArray(inputData, maxBlockLength);
		byte[] result = new byte[blocks.length * keySizeInBytes];

		for (int i = 0; i < blocks.length; i++) {
			int outputOffset = i * keySizeInBytes;
			byte[] encryptedData = encodedEngine.processBlock(blocks[i], 0, blocks[i].length);
			System.arraycopy(encryptedData, 0, result, outputOffset, encryptedData.length);
		}

		return result;
	}

	private static BigInteger convertBase64StringToBigInteger(String value) {
		Base64.Decoder base64Decoder = Base64.getDecoder();
		byte[] modulusInByteArray = base64Decoder.decode(value);
		BigInteger result = new BigInteger(1, modulusInByteArray);

		return result;
	}

	private static byte[][] divideArray(byte[] source, int chunksize) {
		int start = 0;
		byte[][] result = new byte[(int) Math.ceil(source.length / (double) chunksize)][chunksize];

		for (int i = 0; i < result.length; i++) {
			result[i] = Arrays.copyOfRange(source, start, start + chunksize);
			start += chunksize;
		}

		return result;
	}
}
