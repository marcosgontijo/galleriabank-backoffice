package com.webnowbr.siscoat.cobranca.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.webnowbr.siscoat.cobranca.db.model.ComparativoCamposEsteira;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaLogsAlteracao;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaLogsAlteracaoDetalhe;
import com.webnowbr.siscoat.cobranca.db.model.ImovelCobranca;
import com.webnowbr.siscoat.common.CommonsUtil;

public class ContratoCobrancaService {

	public List<ContratoCobrancaLogsAlteracaoDetalhe> comparandoValores(Object valoresAtuais,
			Object valoresBanco, List<ComparativoCamposEsteira> camposEsteira,
			ContratoCobrancaLogsAlteracao alteracao) {
		
		String nomeClasse = valoresAtuais.getClass().getSimpleName();
		
		List<String> camposParaVerificar = camposEsteira.stream()//
				.filter(c -> c.getValidarClasses().equals(nomeClasse))//
				.map(c -> c.getNome_propiedade()) //
				.collect(Collectors.toList());
		 
		
		List<ContratoCobrancaLogsAlteracaoDetalhe> listaDeAlteracoes = new ArrayList<>();
		Class<?> reflectionValues = valoresAtuais.getClass();

		for (Field field : reflectionValues.getDeclaredFields()) {
			Boolean verificaCamposAlterados = camposParaVerificar
					.stream().filter(f -> f.equalsIgnoreCase(field.getName().toLowerCase()))
					.findAny().isPresent();	
			try {
			if(!verificaCamposAlterados) {
				String nomeClasseCampo = field.getType().getSimpleName();
				if (camposEsteira.stream().filter(c -> c.getValidarClasses().equalsIgnoreCase(nomeClasseCampo.toLowerCase())).findAny().isPresent()) { 
					field.setAccessible(true);
					Object currentValues = field.get(valoresAtuais); 
					Object originalValues = field.get(valoresBanco);
					
					if (currentValues != null && originalValues == null) {
						listaDeAlteracoes.add(new ContratoCobrancaLogsAlteracaoDetalhe(field.getName(), nomeClasse,
								currentValues.toString(), "Não existia", alteracao));
					}
					if (currentValues == null && originalValues != null) {
						listaDeAlteracoes.add(new ContratoCobrancaLogsAlteracaoDetalhe(field.getName(), nomeClasse,
								"Removido", originalValues.toString(), alteracao));
					} else if (currentValues != null && originalValues != null) {
						listaDeAlteracoes
								.addAll(comparandoValores(currentValues, originalValues, camposEsteira, alteracao));
					}
				}
				continue;
			}
			
				field.setAccessible(true);
				Object currentValues = field.get(valoresAtuais); // valores que estao no banco
				Object originalValues = field.get(valoresBanco);// valores que esta vindo atual

				if (currentValues != null) {
					if (!currentValues.equals(originalValues)) {
						listaDeAlteracoes.add(new ContratoCobrancaLogsAlteracaoDetalhe(field.getName(), nomeClasse, CommonsUtil.stringValue(originalValues), CommonsUtil.stringValue(currentValues), alteracao));
					}
				} 
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return listaDeAlteracoes;
	}
}