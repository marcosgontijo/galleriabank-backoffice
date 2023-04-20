package com.webnowbr.siscoat.cobranca.ws.endpoint;

import java.util.ArrayList;
import java.util.List;

import com.webnowbr.siscoat.common.CommonsUtil;

public class ReaWebhookRetornoProprietarios {

	public List<ReaWebhookRetornoDados> dados;
	public boolean contemMenor;

	public List<ReaWebhookRetornoProprietario> getDadosProprietarios() {

		List<ReaWebhookRetornoProprietario> result = new ArrayList<>();
		ReaWebhookRetornoProprietario reaWebhookRetornoProprietario = null;
		for (ReaWebhookRetornoDados reaWebhookRetornoDados : dados) {
			if (CommonsUtil.mesmoValor(reaWebhookRetornoDados.getConstante(), "NOME_PROPRIETARIO")) {
				reaWebhookRetornoProprietario = new ReaWebhookRetornoProprietario();
				reaWebhookRetornoProprietario.setNome(reaWebhookRetornoDados.getValor());
				result.add(reaWebhookRetornoProprietario);
			} else if (CommonsUtil.mesmoValor(reaWebhookRetornoDados.getConstante(), "RG")) {
				if ( CommonsUtil.semValor(reaWebhookRetornoProprietario.getRg()) )
				reaWebhookRetornoProprietario.setRg(reaWebhookRetornoDados.getValor());
			} else if (CommonsUtil.mesmoValor(reaWebhookRetornoDados.getConstante(), "CNPJ")) {
				reaWebhookRetornoProprietario.setCnpj(reaWebhookRetornoDados.getValor());
				reaWebhookRetornoProprietario.setFisicaJuridica("PJ");
			} else if (CommonsUtil.mesmoValor(reaWebhookRetornoDados.getConstante(), "CPF")) {
				reaWebhookRetornoProprietario.setCpf(reaWebhookRetornoDados.getValor());
				reaWebhookRetornoProprietario.setFisicaJuridica("PF");
			} else if (CommonsUtil.mesmoValor(reaWebhookRetornoDados.getConstante(), "ENDERECO_PROPRIETARIO")) {
				reaWebhookRetornoProprietario.setEndereco(reaWebhookRetornoDados.getValor());
			}
		}
		return result;
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
