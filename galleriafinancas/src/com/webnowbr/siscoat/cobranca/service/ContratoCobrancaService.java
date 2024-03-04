package com.webnowbr.siscoat.cobranca.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaLogsAlteracao;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaLogsAlteracaoDetalhe;
import com.webnowbr.siscoat.common.CommonsUtil;

public class ContratoCobrancaService {
	
	public void clonandoValoresObjetoParaVerificacao(ContratoCobranca original, ContratoCobranca copia) {
		Class<?> reflectionContratoCobranca = original.getClass();
		for (Field field : reflectionContratoCobranca.getDeclaredFields()) {
			try {
				field.setAccessible(true);
				Object value = field.get(original);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public List<ContratoCobrancaLogsAlteracaoDetalhe> comparandoValores(ContratoCobranca valoresAtuais,
			ContratoCobranca valoresBanco, List<String> camposParaVerificar,
			ContratoCobrancaLogsAlteracao alteracao) {
		
		List<ContratoCobrancaLogsAlteracaoDetalhe> listaDeAlteracoes = new ArrayList<>();
		Class<?> reflectionValues = valoresAtuais.getClass();

		for (Field field : reflectionValues.getDeclaredFields()) {
			Boolean verificaCamposAlterados = camposParaVerificar.contains(field.getName().toLowerCase()) ? true : false;
			if(!verificaCamposAlterados) continue;		
			try {
				field.setAccessible(true);
				Object currentValues = field.get(valoresAtuais); // valores que estao no banco
				Object originalValues = field.get(valoresBanco);// valores que esta vindo atual

				if (currentValues != null) {
					if (!currentValues.equals(originalValues)) {
						listaDeAlteracoes.add(new ContratoCobrancaLogsAlteracaoDetalhe(field.getName(), CommonsUtil.stringValue(originalValues), CommonsUtil.stringValue(currentValues), alteracao));
					}
				} 
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return listaDeAlteracoes;
	}
}