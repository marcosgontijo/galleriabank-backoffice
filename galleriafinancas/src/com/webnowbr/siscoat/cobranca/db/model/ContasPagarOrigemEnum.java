package com.webnowbr.siscoat.cobranca.db.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import com.webnowbr.siscoat.common.CommonsUtil;

public enum ContasPagarOrigemEnum {
	PRE("PRE"),
	POS("POS");

	private String nome;

	private ContasPagarOrigemEnum(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return this.nome;
	}
	
	public static ContasPagarOrigemEnum parse(String nome) {

		if (nome != null) {			 
			List<ContasPagarOrigemEnum> origems = new ArrayList<>( Arrays.asList( ContasPagarOrigemEnum.values() ));
			Optional<ContasPagarOrigemEnum> origem = origems.stream().filter( d -> CommonsUtil.mesmoValor(nome, d.getNome()) ).findFirst();
		
			if (origem.isPresent())
				return origem.get();
		}

		return null;

	}
}
