package com.webnowbr.siscoat.cobranca.faciltech;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class WsCredHttpClient {
	private static final CloseableHttpClient httpClient = HttpClients.createDefault();

	public static String getResponse(HttpRequestBase request) throws ClientProtocolException, IOException {
		String result = null;
		
		try (CloseableHttpResponse response = httpClient.execute(request)) {
			int statusCode = response.getStatusLine().getStatusCode();
			HttpEntity responseEntity = response.getEntity();

			if (responseEntity != null) {
				boolean hasException = false;
				String responseText = EntityUtils.toString(responseEntity);
				if ((responseText.startsWith("{")) && (responseText.endsWith("}"))) {
					JSONObject json = new JSONObject(responseText);
					hasException = (json.has("ExceptionType")) && (json.has("ExceptionMessage"));
				} else if ((responseText.startsWith("\"")) && (responseText.endsWith("\""))) {
					responseText = responseText.substring(1, responseText.length() - 1);
				}
				 
				if ((200 == statusCode) && (!hasException)) {
					result = responseText;
				} else {
					throw new RuntimeException(responseText);
				}
			}
		}
		
		return result;
	}
}
