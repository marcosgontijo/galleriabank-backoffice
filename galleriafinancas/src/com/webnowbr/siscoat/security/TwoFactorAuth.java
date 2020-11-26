package com.webnowbr.siscoat.security;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Random;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "twoFactorAuth")
@SessionScoped
public class TwoFactorAuth {
		/*
				public static void main(String[] args) throws Exception {
		
					String base32Secret = generateBase32Secret();
		
					System.out.println("secret = " + base32Secret);
		
					// this is the name of the key which can be displayed by the authenticator program
					String keyId = "user@j256.com";
					// generate the QR code
					System.out.println("Image url = " + TimeBasedOneTimePasswordUtil.qrImageUrl(keyId, base32Secret));
					// we can display this image to the user to let them load it into their auth program
		
					// we can use the code here and compare it against user input
					String code = TimeBasedOneTimePasswordUtil.generateCurrentNumberString(base32Secret);
		
					while (true) {
						long diff = TimeBasedOneTimePasswordUtil.DEFAULT_TIME_STEP_SECONDS
								- ((System.currentTimeMillis() / 1000) % TimeBasedOneTimePasswordUtil.DEFAULT_TIME_STEP_SECONDS);
						code = TimeBasedOneTimePasswordUtil.generateCurrentNumberString(base32Secret);
						System.out.println("Secret code = " + code + ", change in " + diff + " seconds");
						Thread.sleep(1000);
					}
				}
		*/
		
		public String getCurrentCode(String base32Secret) {
			try {
				return TimeBasedOneTimePasswordUtil.generateCurrentNumberString(base32Secret);
			} catch (GeneralSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return "";
		}
	
		public String getQrCodeGoogle(String keyId, String base32Secret) {
			return TimeBasedOneTimePasswordUtil.qrImageUrl(keyId, base32Secret);
		}
		
		public String generateBase32Secret() {
			return generateBase32Secret(16);
		}

		public static String generateBase32Secret(int length) {
			StringBuilder sb = new StringBuilder(length);
			Random random = new SecureRandom();
			for (int i = 0; i < length; i++) {
				int val = random.nextInt(32);
				if (val < 26) {
					sb.append((char) ('A' + val));
				} else {
					sb.append((char) ('2' + (val - 26)));
				}
			}
			return sb.toString();
		}
	}
