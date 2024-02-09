package com.webnowbr.siscoat.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public enum DocumentosAnaliseEnum {
	REA("Rea"), RELATO("Relato"), CREDNET("Crednet"), ENGINE("Engine"), CENPROT("Cenprot"), SCR("SCR"), DOCKET("Docket"), 
	RECEITA_FEDERAL("Receita Federal"),
	PROCESSO("Processos"),PROCESSOB("ProcessosBigData"),
	PPE("Pessoa Exposta"), 
	CNDTTST("CNDTrabalhistaTST"),
	CNDFEDERAL("CNDFederal"),
	CNDESTADUAL("CNDEstadual"),
	RELACIONAMENTO("Relacionamentos"),
	FINANCASBB("FinancasBigData"),
	CADASTROBB("CadastroBasicoBigData"),
	VEICULOS("Veiculos");

	private String nome;

	private DocumentosAnaliseEnum(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return this.nome;
	}
	
	public static DocumentosAnaliseEnum parse(String nome) {

		if (nome != null) {			 
			List<DocumentosAnaliseEnum> documentosAnalise = new ArrayList<>( Arrays.asList( DocumentosAnaliseEnum.values() ));
			Optional<DocumentosAnaliseEnum> documentoAnalise = documentosAnalise.stream().filter( d -> CommonsUtil.mesmoValor(nome, d.getNome()) ).findFirst();
		
			if (documentoAnalise.isPresent())
				return documentoAnalise.get();
		}

		return null;

	}
}
