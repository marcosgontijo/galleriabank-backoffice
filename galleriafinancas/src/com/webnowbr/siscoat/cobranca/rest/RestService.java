package com.webnowbr.siscoat.cobranca.rest;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class RestService {
	public static boolean verificarAutenticacao(String authorization) {
		if (authorization == null || !authorization.startsWith("Basic")) {
			return false;
		} else {

			// TODO AUTENTICACAO VIA TOKEN

			/// decoda token de autenticação
			String[] tokens;
			String username = "";
			String password = "";

			authorization = authorization.replace("Basic ", "");

			try {
				tokens = (new String(Base64.getDecoder().decode(authorization), "UTF-8")).split(":");

				username = tokens[0];
				password = tokens[1];
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			if (username.equals("webnowbr") && password.equals("!SisCoAt@2021*")) {
				return true;
			} else {
				return false;
			}
		}
	}
}
