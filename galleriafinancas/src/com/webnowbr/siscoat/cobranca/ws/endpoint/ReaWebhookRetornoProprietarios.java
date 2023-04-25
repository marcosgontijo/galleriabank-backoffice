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
				if (reaWebhookRetornoProprietario != null && reaWebhookRetornoProprietario.getCpf() == null
						&& reaWebhookRetornoProprietario.getCnpj() == null)
					result.remove(reaWebhookRetornoProprietario);
				reaWebhookRetornoProprietario = new ReaWebhookRetornoProprietario();
				reaWebhookRetornoProprietario.setNome(reaWebhookRetornoDados.getValor());
				result.add(reaWebhookRetornoProprietario);
			} else if (CommonsUtil.mesmoValor(reaWebhookRetornoDados.getConstante(), "RG")) {
				if (CommonsUtil.semValor(reaWebhookRetornoProprietario.getRg()))
					reaWebhookRetornoProprietario.setRg(reaWebhookRetornoDados.getValor());
			} else if (CommonsUtil.mesmoValor(reaWebhookRetornoDados.getConstante(), "CNPJ")) {

				if (CommonsUtil.somenteNumeros(reaWebhookRetornoDados.getValor()).length() == 14)
					reaWebhookRetornoProprietario.setCnpj(
							CommonsUtil.formataCnpj(CommonsUtil.somenteNumeros(reaWebhookRetornoDados.getValor())));
				else
					reaWebhookRetornoProprietario.setCpf(CommonsUtil.somenteNumeros(reaWebhookRetornoDados.getValor()));
				reaWebhookRetornoProprietario.setFisicaJuridica("PF");
				reaWebhookRetornoProprietario.setCnpj(reaWebhookRetornoDados.getValor());
				reaWebhookRetornoProprietario.setFisicaJuridica("PJ");
			} else if (CommonsUtil.mesmoValor(reaWebhookRetornoDados.getConstante(), "CPF")) {
				if (CommonsUtil.somenteNumeros(reaWebhookRetornoDados.getValor()).length() == 11)
					reaWebhookRetornoProprietario.setCpf(
							CommonsUtil.formataCpf(CommonsUtil.somenteNumeros(reaWebhookRetornoDados.getValor())));
				else
					reaWebhookRetornoProprietario.setCpf(CommonsUtil.somenteNumeros(reaWebhookRetornoDados.getValor()));

				reaWebhookRetornoProprietario.setFisicaJuridica("PF");
			} else if (CommonsUtil.mesmoValor(reaWebhookRetornoDados.getConstante(), "ENDERECO_PROPRIETARIO")) {
				reaWebhookRetornoProprietario.setEndereco(reaWebhookRetornoDados.getValor());
			}
		}
		if (reaWebhookRetornoProprietario != null && reaWebhookRetornoProprietario.getCpf() == null
				&& reaWebhookRetornoProprietario.getCnpj() == null)
			result.remove(reaWebhookRetornoProprietario);
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
