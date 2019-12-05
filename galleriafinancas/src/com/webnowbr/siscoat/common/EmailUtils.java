package com.webnowbr.siscoat.common;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

//import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class EmailUtils {
	private static final String HOSTNAME = "smtp.gmail.com";
	private static final String USERNAME = "webnowbr";
	private static final String PASSWORD = "hvj28383";
	private static final String EMAILORIGEM = "webnowbr@gmail.com";

	private static Email conectaEmail() throws EmailException {
		Email email = new SimpleEmail();
		email.setHostName(HOSTNAME);
		email.setSmtpPort(587);
		email.setAuthentication(USERNAME,PASSWORD);
		email.setTLS(true);
		email.setFrom(EMAILORIGEM);
		return email;
	}

	public static void enviaEmail(Mensagem mensagem) throws EmailException {
		Email email = new SimpleEmail();
		email = conectaEmail();

		if (mensagem.getEmail() == null || mensagem.getEmail().length() <= 5) {
			mensagem.setEmail("webnowbr@gmail.com");
		}

		try {
			mensagem.setDestino(mensagem.getDestino().replaceAll(",", ";"));
			String[] emails = mensagem.getDestino().split(";");
			for (int t = 0; emails != null && t < emails.length; t++) {
				email.addTo(emails[t]);
			}
			
			if (emails == null || emails.length <= 0) {
				email.addTo(mensagem.getDestino());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		email.addReplyTo(mensagem.getEmail(), mensagem.getNome());
		email.addHeader("Reply-To", mensagem.getEmail());
		email.addHeader("Return-Receipt-To", mensagem.getEmail());
		
		email.setFrom(mensagem.getEmail(), mensagem.getEmail());
		email.setSubject("[ " + mensagem.getTipoEmail() + " ]:  " + mensagem.getAssunto());
		email.setMsg(mensagem.getConteudo() + "\n\n Att,\n " + mensagem.getNome() + "\n\n Telefone: " + mensagem.getTelefone()+ "\n Email: " + mensagem.getEmail());
		
		email.send();
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "E-mail enviado com sucesso. ", ""));
	}
}
