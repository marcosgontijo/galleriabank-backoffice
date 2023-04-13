package com.webnowbr.siscoat.cobranca.ws.endpoint;

import java.util.List;

import com.webnowbr.siscoat.common.CommonsUtil;

public class ReaWebhookRetornoProprietarios {

	public List<ReaWebhookRetornoDados> dados;
	public boolean contemMenor;

	
	public String getNome() {		
		String proprietarioAtual = dados.stream().filter( d -> CommonsUtil.mesmoValor( d.getConstante(), "NOME_PROPRIETARIO")).map(d -> d.getValor()).findFirst().orElse(null);
		return proprietarioAtual;
	}
	public String getRG() {		
		String proprietarioAtual = dados.stream().filter( d -> CommonsUtil.mesmoValor( d.getConstante(), "RG")).map(d -> d.getValor()).findFirst().orElse(null);
		return proprietarioAtual;
	}
	public String getCNPJ() {		
		String proprietarioAtual = dados.stream().filter( d -> CommonsUtil.mesmoValor( d.getConstante(), "CNPJ")).map(d -> d.getValor()).findFirst().orElse(null);
		return proprietarioAtual;
	}
	public String getCPF() {		
		String proprietarioAtual = dados.stream().filter( d -> CommonsUtil.mesmoValor( d.getConstante(), "CPF")).map(d -> d.getValor()).findFirst().orElse(null);
		return proprietarioAtual;
	}
	public String getEndereco() {		
		String proprietarioAtual = dados.stream().filter( d -> CommonsUtil.mesmoValor( d.getConstante(), "ENDERECO_PROPRIETARIO")).map(d -> d.getValor()).findFirst().orElse(null);
		return proprietarioAtual;
	}
	public String getFisicaJuridica() {
		if (CommonsUtil.semValor( getCPF())) {
			return "PF";
		}else
			return "PJ";
	}
	
	
	
	
	public List<ReaWebhookRetornoDados> getDados() {
		return dados;
	}

	public void setDados(List<ReaWebhookRetornoDados> dados) {
		this.dados = dados;
	}

	public boolean isContemMenor() {
		return contemMenor;
	}

	public void setContemMenor(boolean contemMenor) {
		this.contemMenor = contemMenor;
	}

}
