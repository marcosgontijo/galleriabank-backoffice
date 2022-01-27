package com.webnowbr.siscoat.common;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SiscoatConstants {
	public static final int GALLERIA_FINANCAS_ID = 14;

	public static final String CONTRATO_QUITADO = "Quitado";
	public static final String CONTRATO_ATIVO = "Ativo";

	public static final BigDecimal SEGURO_MIP = BigDecimal.valueOf(0.021); // seguro MIP: 0,021% sobre saldo devedor parcela anterior;
	public static final BigDecimal SEGURO_DFI = BigDecimal.valueOf(0.0037); // seguro DFI: 0,0037% sobre o valor do imóvel;

	public static final BigDecimal CUSTO_EMISSAO_PERCENTUAL_LIQUIDO = BigDecimal.valueOf(3.2);
	public static final BigDecimal CUSTO_EMISSAO_PERCENTUAL_BRUTO = BigDecimal.valueOf(3);
	public static final BigDecimal CUSTO_EMISSAO_MINIMO 	 = BigDecimal.valueOf(3500);
	
	@SuppressWarnings("deprecation")
	public static Date TROCA_IOF = new Date(2021-1900, 9-1, 19);
	
	//public static final BigDecimal TARIFA_IOF_PJ_ANTIGA = BigDecimal.valueOf(0.0041);
	//public static final BigDecimal TARIFA_IOF_PF_ANTIGA = BigDecimal.valueOf(0.0082);
	
	public static final BigDecimal TARIFA_IOF_PJ_ANTIGA = BigDecimal.valueOf(0.00559);
	public static final BigDecimal TARIFA_IOF_PF_ANTIGA = BigDecimal.valueOf(0.01118);
	
	public static final BigDecimal TARIFA_IOF_PJ = BigDecimal.valueOf(0.0041);
	public static final BigDecimal TARIFA_IOF_PF = BigDecimal.valueOf(0.0082);
	

	public static final BigDecimal TARIFA_IOF_ADICIONAL = BigDecimal.valueOf(0.38);

	public static final List<Long> PAGADOR_GALLERIA = Arrays.asList(15l, 34l, 14l, 182l, 417l, 803l);
}
