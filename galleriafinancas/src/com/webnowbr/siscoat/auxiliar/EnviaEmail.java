package com.webnowbr.siscoat.auxiliar;
 
import java.util.Properties;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.webnowbr.siscoat.infra.db.dao.ParametrosDao;

/**
 * Classe Utilitária que contém métodos para envio de Email 
 */
public class EnviaEmail {
 
	 
    /**
     * Variavel local para Sessao
     */
    Session session = null;
 
    String username = null;
    String senha = null;
    String comcopia = null;
    
    /**
     * Construtor sem parametros, ao ser chamado já instancia as configuraççoes
     *
     */
    public EnviaEmail() {

        ajustaParametros();
    }


    /**
     * Metodo para envio de mensagem com texto simples
     */
    public void enviarEmail(String remetente, String destinatario, String assunto, String conteudo) throws Exception {
 
        try {
 
            Message message = new MimeMessage(session);
 
            //Configura o Remetente da mensagem
            message.setFrom(new InternetAddress(remetente));
 
            //Configura o Destinatário da mensagem
            Address[] toUser = InternetAddress
                    .parse(destinatario);
 
            //Configura o Assunto da mensagem
            message.setRecipients(Message.RecipientType.TO, toUser);
            message.setSubject(assunto);
 
            //Configura o Conteudo da mensagem
            message.setText(conteudo);
 
            /**
             * Envia a mensagem criada
             */
            Transport.send(message);
 
            System.out.println("Email enviado com Sucesso; ");
 
        } catch (MessagingException e) {
            throw new Exception("Erro ao enviar email! \n" + e.getMessage());
        }
 
    }
 
    /**
     * Metodo para envio de mensagem padrao HTML ja formatado
     */
    public void enviarEmailHtmlResponsavelAdms(String destinatario, String assunto, String conteudoHtml) throws Exception {
        try {
 
        	String remetente = this.username;
        			
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remetente));
/*
            Address[] toUser = InternetAddress
                    .parse(destinatario);

            message.setRecipients(Message.RecipientType.TO, toUser);
            */
            
            String to = destinatario;
            InternetAddress[] parseTO = InternetAddress.parse(to , true);
            message.setRecipients(javax.mail.Message.RecipientType.TO,  parseTO);
            
            //String cc = "hv.junior@gmail.com,hjunior@cpqd.com.br";
            //String cc = "joao@galleriafinancas.com.br, joaomagatti@me.com";
            
            InternetAddress[] parseCC = InternetAddress.parse(this.comcopia, true);
            message.setRecipients(javax.mail.Message.RecipientType.CC,  parseCC);

            message.setSubject(assunto);
 
            Multipart multipart = new MimeMultipart("related");
            BodyPart htmlPart = new MimeBodyPart();
            htmlPart.setHeader("Content-Type", "text/html; charset=\"utf-8\"");  
            htmlPart.setContent(conteudoHtml, "text/html; charset=utf-8");
 
            multipart.addBodyPart(htmlPart);
 
            message.setContent(multipart);
            System.out.println("enviarEmailHtml msg preparada ");
            /**
             * Método para enviar a mensagem criada
             */
            Transport.send(message);
            System.out.println("enviarEmailHtml msg enviada ");
 
            System.out.println("Email enviado com Sucesso; ");
 
        } catch (MessagingException e) {
            throw new Exception("Erro ao enviar email! \n" + e.getMessage());
        }
 
    }
 
    /**
     * Configura aa propriedades da JVM com parametros do servidor
     *
     * Modificador de acesso 'private' pois não é necessário que este método
     * seja chamado de outras classes
     */
    private void ajustaParametros() {
    	
		ParametrosDao pDao = new ParametrosDao();
    	
    	this.username = pDao.findByFilter("nome", "EMAIL_USERNAME").get(0).getValorString();
    	this.senha = pDao.findByFilter("nome", "EMAIL_SENHA").get(0).getValorString();
    	this.comcopia = pDao.findByFilter("nome", "EMAIL_COM_COPIA").get(0).getValorString();
    	String host = pDao.findByFilter("nome", "EMAIL_HOST").get(0).getValorString();
    	String port = pDao.findByFilter("nome", "EMAIL_PORT").get(0).getValorString();
    	String auth = pDao.findByFilter("nome", "EMAIL_AUTH").get(0).getValorString();
    	String starttls = pDao.findByFilter("nome", "EMAIL_STARTTLS").get(0).getValorString();
 
        Properties props = new Properties();

        props.put("mail.smtp.host", host);
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.port", port);
        
        if (Boolean.valueOf(starttls)) {
        	props.put("mail.smtp.starttls.enable", "true");
        }

        /**
         * Associa autenticação a sessao de correio
         */
        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, senha);                    
                    }
                });
    }
}