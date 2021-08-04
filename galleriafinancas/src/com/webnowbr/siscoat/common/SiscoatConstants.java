package com.webnowbr.siscoat.common;

import java.math.BigDecimal;

public class SiscoatConstants {
	public static final int GALLERIA_FINANCAS_ID = 14;

	public static final String CONTRATO_QUITADO = "Quitado";
	public static final String CONTRATO_ATIVO = "Ativo";

	public static final BigDecimal SEGURO_MIP = BigDecimal.valueOf(0.021); // seguro MIP: 0,021% sobre saldo devedor parcela anterior;
	public static final BigDecimal SEGURO_DFI = BigDecimal.valueOf(0.0037); // seguro DFI: 0,0037% sobre o valor do imóvel;

	public static final BigDecimal CUSTO_EMISSAO_PERCENTUAL = BigDecimal.valueOf(3);
	public static final BigDecimal CUSTO_EMISSAO_MINIMO 	 = BigDecimal.valueOf(3500);

}
