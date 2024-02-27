package com.webnowbr.siscoat.cobranca.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ApplicationScoped;

import org.primefaces.PrimeFaces;

import com.webnowbr.siscoat.cobranca.db.model.ContratoCobranca;
import com.webnowbr.siscoat.cobranca.db.model.ContratoCobrancaLogsAlteracao;
import com.webnowbr.siscoat.cobranca.db.op.ContratoCobrancaDao;
import com.webnowbr.siscoat.common.CommonsUtil;

@ApplicationScoped
public class ContratoCobrancaService {
	
	private ContratoCobranca objetoContratoCobranca;
	private ContratoCobranca objetoContratoCobrancaCopiaCampos;
	
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

	public List<ContratoCobrancaLogsAlteracao> comparandoValores(ContratoCobranca valoresAtuais, ContratoCobranca valoresBanco, List<String> camposParaVerificar) {
		
		List<ContratoCobrancaLogsAlteracao> listaDeAlteracoes = new ArrayList<>();
		Class<?> reflectionValues = valoresAtuais.getClass();

		for (Field field : reflectionValues.getDeclaredFields()) {
			Boolean verificaCamposAlterados = camposParaVerificar.contains(field.getName().toLowerCase()) ? true : false;
			if(!verificaCamposAlterados) continue;		
			try {
				field.setAccessible(true);
				Object currentValues = field.get(valoresAtuais); // escrevi agora
				Object originalValues = field.get(valoresBanco);// estava no banco

				if (currentValues != null) {
					if (!currentValues.equals(originalValues)) {
						listaDeAlteracoes.add(new ContratoCobrancaLogsAlteracao(field.getName(), CommonsUtil.stringValue(originalValues), CommonsUtil.stringValue(currentValues)));
						
//						System.out.println("Campo " + field.getName() + " foi alterado.");
//						System.out.println("Valor antigo: " + originalValues);
//						System.out.println("Novo valor: " + currentValues);
					}
				} 
			} catch (IllegalAccessException e) {
				System.out.println("Caiu na exception");
				e.printStackTrace();
			}
		}
		return listaDeAlteracoes;
	}
}