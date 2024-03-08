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
		
		//PEGAR A CLASSE DO valoresAtuais
		String nomeClasse = valoresAtuais.getClass().getSimpleName();
		System.out.println("Objeto pertence a classe" + nomeClasse);
		
		
		List<String> camposParaVerificar = camposEsteira.stream()//
				.filter(c -> c.getValidarClasses().equals(nomeClasse))//
				.map(c -> c.getNome_propiedade()) //
				.collect(Collectors.toList());
		 
		
		List<ContratoCobrancaLogsAlteracaoDetalhe> listaDeAlteracoes = new ArrayList<>();
		Class<?> reflectionValues = valoresAtuais.getClass();

		for (Field field : reflectionValues.getDeclaredFields()) {
			Boolean verificaCamposAlterados = camposParaVerificar.contains(field.getName().toLowerCase()) ? true : false;			
			try {
			if(!verificaCamposAlterados) {
				String nomeClasseCampo = field.getType().getSimpleName();
				if (camposEsteira.stream().filter(c -> c.getValidarClasses().equals(nomeClasseCampo)).findAny().isPresent()) { //SE TIVER CAMPOS P VALIDAR
					field.setAccessible(true);
					Object currentValues = field.get(valoresAtuais); 
					Object originalValues = field.get(valoresBanco);
					
					listaDeAlteracoes.addAll(comparandoValores(currentValues, originalValues, camposEsteira, alteracao));
				}
				// VERIFICA SE A CLASE TEM CAMPOS A VALIDAR SE SIM FAZ CHAMDADA RECURSIVA
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