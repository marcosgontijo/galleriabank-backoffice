package com.webnowbr.siscoat.common;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtil {

	public static String generateJWTReaWebwook(boolean incluirClaims) {
		//jwt com poucas informações para manter o tamnho da url em 256
		String jwtToken = "";

		try {
			
			Map<String, Object> claims = new HashMap<>();
			if (incluirClaims)
				claims.put("perfil", "reaWebwook");

			jwtToken = Jwts.builder().setClaims(claims)
					.setExpiration(
							Date.from(LocalDateTime.now().plusDays(5L).atZone(ZoneId.systemDefault()).toInstant()))
					.signWith(CommonsUtil.CHAVE_WEBHOOK, SignatureAlgorithm.HS256).compact();
			// .compact();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;

		}
		return jwtToken;
	}
}
