package com.webnowbr.siscoat.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SiscoatException extends Exception {

	/** Logger instance. */
	private final Log LOGGER = LogFactory.getLog(SiscoatException.class);

	/**
	 * @see Exception#Exception(String)
	 */
	public SiscoatException(String message) {
		super(message);
		enviaEmail(message, null);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public SiscoatException(Throwable cause) {
		super(cause);
		enviaEmail(null, cause);
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public SiscoatException(String message, Throwable cause) {
		super(message, cause);
		enviaEmail(message, cause);
	}

	private void enviaEmail(String message, Throwable cause) {
//
//		if (CommonsUtil.sistemaWindows() || (CommonsUtil.semValor(message) && CommonsUtil.semValor(cause))) {
//			return;
//		}
//
//		if (!CommonsUtil.semValor(message) && CommonsUtil.mesmoValor(message, "Existem documentos fora do prazo.")) {
//			return;
//		}
//
//		Boolean baseProducao = false;
//		try {
//			if (!CommonsUtil.semValor(BusinessProperties.getCurrentInstance().get("baseProducao"))) {
//				baseProducao = CommonsUtil
//						.booleanValue(BusinessProperties.getCurrentInstance().get("baseProducao").toString());
//			} else {
//				baseProducao = false;
//			}
//		} catch (Exception e) {
//			LOGGER.error("BusinessException.enviaEmail: EXCEPTION", e);
//		}
//
//		if (!baseProducao) {
//			return;
//		}
//
//		String mensagem = "";
//
//		if (!CommonsUtil.semValor(message)) {
//			mensagem = "message:<br><br>" + message;
//		}
//
//		if (!CommonsUtil.semValor(cause) && !CommonsUtil.semValor(cause.getMessage())) {
//
//			if (!CommonsUtil.semValor(message)) {
//				mensagem = mensagem + "<br><br>";
//			}
//			mensagem = mensagem + "cause: <BR>";
//
//			// Throwable causeChild = null;
//			Throwable result = cause;
//
//			while (result != null) {
//
//				mensagem = mensagem + result.toString() + "<br>";
//
//				if (!CommonsUtil.semValor(result.getStackTrace())) {
//					String stack = "";
//					for (StackTraceElement se : result.getStackTrace()) {
//						boolean destaque = false;
//						if (!CommonsUtil.semValor(se.getFileName()) && (se.getFileName().contains("MBean")
//								|| se.getFileName().contains("DAO") || se.getFileName().contains("Service"))) {
//							destaque = true;
//						}
//						if (destaque) {
//							stack = stack + "<b><font color=red>";
//						}
//						if (!CommonsUtil.semValor(se.getFileName())) {
//							stack = stack + "<br>" + se.getFileName();
//						}
//						if (!CommonsUtil.semValor(se.getClassName())) {
//							stack = stack + "<br>" + se.getClassName();
//						}
//						if (!CommonsUtil.semValor(se.getMethodName())) {
//							stack = stack + " - " + se.getMethodName();
//						}
//						if (!CommonsUtil.semValor(se.getLineNumber())) {
//							stack = stack + " (" + se.getLineNumber() + ")<br>";
//						}
//
//						if (destaque) {
//							stack = stack + "</font></b>";
//						}
//					}
//
//					mensagem = mensagem + stack;
//				}
//				mensagem = mensagem + "<br>";
//				result = ExceptionUtils.getCause(result);
//			}
//		}
//
//		EmailData email = new EmailData();
//		email.setSubject("BusinessException");
//		if (CommonsUtil.semValor(mensagem)) {
//			return;
//		}
//		email.setMessage("text/html", mensagem);
//		email.setFrom("sistema@banicred.com.br");
//		email.addTo("sistema@banicred.com.br");
//
//		try {
//			EmailService commonsEmailSender = BusinessFactory.getService(EmailService.class);
//			commonsEmailSender.sendEmail(email);
//		} catch (BusinessException e) {
//			LOGGER.error("BusinessException.enviaEmail: EXCEPTION", e);
//		}

	}

}
