package com.webnowbr.siscoat.cobranca.ws.plexi;

import java.util.List;

import com.webnowbr.siscoat.common.CommonsUtil;
import com.webnowbr.siscoat.common.SiscoatConstants;

public class PlexiWebhookRetorno {

	String codigoVerificador;
	String endpoint;
	String pdf;
	String numeroCertidao;
	String requestId;
	String validadeCertidao;
	String nome;
	String cpfCnpj;
	List<Object> processos;
	String status;
	Boolean error;
	Integer totalProcessos;
	String mensagem;

	public String getSituacao() {
		if (!CommonsUtil.mesmoValorIgnoreCase("negativo", status))
			return SiscoatConstants.CND_SITUACAO_POSSUI_DEBITOS;
		else
			return SiscoatConstants.CND_SITUACAO_NAO_POSSUI_DEBITOS;
	}

	public String getCodigoVerificador() {
		return codigoVerificador;
	}

	public void setCodigoVerificador(String codigoVerificador) {
		this.codigoVerificador = codigoVerificador;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getPdf() {
		return pdf;
	}

	public void setPdf(String pdf) {
		this.pdf = pdf;
	}

	public String getNumeroCertidao() {
		return numeroCertidao;
	}

	public void setNumeroCertidao(String numeroCertidao) {
		this.numeroCertidao = numeroCertidao;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getValidadeCertidao() {
		return validadeCertidao;
	}

	public void setValidadeCertidao(String validadeCertidao) {
		this.validadeCertidao = validadeCertidao;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

	public List<Object> getProcessos() {
		return processos;
	}

	public void setProcessos(List<Object> processos) {
		this.processos = processos;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getError() {
		return error;
	}

	public void setError(Boolean error) {
		this.error = error;
	}

	public Integer getTotalProcessos() {
		return totalProcessos;
	}

	public void setTotalProcessos(Integer totalProcessos) {
		this.totalProcessos = totalProcessos;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

}
