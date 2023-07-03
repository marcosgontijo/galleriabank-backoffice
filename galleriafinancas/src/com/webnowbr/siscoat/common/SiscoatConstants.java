
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
	public static final BigDecimal SEGURO_DFI_6_DIGITOS = BigDecimal.valueOf(0.000037); // seguro DFI: 0,0037% sobre o valor do imóvel;
	public static final BigDecimal SEGURO_MIP_5_DIGITOS = BigDecimal.valueOf(0.00021); // seguro MIP: 0,021% sobre saldo devedor parcela anterior;
	
	public static final BigDecimal CUSTO_EMISSAO_PERCENTUAL_LIQUIDO = BigDecimal.valueOf(4.3);
	public static final BigDecimal CUSTO_EMISSAO_PERCENTUAL_BRUTO_NOVO = BigDecimal.valueOf(5);
	public static final BigDecimal CUSTO_EMISSAO_PERCENTUAL_BRUTO = BigDecimal.valueOf(4);
	public static final BigDecimal CUSTO_EMISSAO_MINIMO = BigDecimal.valueOf(3500);
	
	public static final BigDecimal VALOR_FIDC = BigDecimal.valueOf(75000000);
	public static final BigDecimal TAXA_AA_FIDC = BigDecimal.valueOf(5.8);
	
	public static final BigDecimal TAXA_ADM = BigDecimal.valueOf(25);
	
	@SuppressWarnings("deprecation")
	public static Date TROCA_IOF = new Date(2021-1900, 9-1, 19);
	
	//public static final BigDecimal TARIFA_IOF_PJ_ANTIGA = BigDecimal.valueOf(0.0041);
	//public static final BigDecimal TARIFA_IOF_PF_ANTIGA = BigDecimal.valueOf(0.0082);
	
	public static final BigDecimal TARIFA_IOF_PJ_ANTIGA2 = BigDecimal.valueOf(0.00559);
	public static final BigDecimal TARIFA_IOF_PF_ANTIGA2 = BigDecimal.valueOf(0.01118);
	
	public static final BigDecimal TARIFA_IOF_PJ_ANTIGA = BigDecimal.valueOf(0.0041);
	public static final BigDecimal TARIFA_IOF_PF_ANTIGA = BigDecimal.valueOf(0.0082);
	
	
	//troca feita para bater valores com excel em 20/09/22
	public static final BigDecimal TARIFA_IOF_PJ = BigDecimal.valueOf(0.00410958904109589);
	public static final BigDecimal TARIFA_IOF_PF = BigDecimal.valueOf(0.00821917808219178);
	
	public static final boolean DEV = false;

	public static final BigDecimal TARIFA_IOF_ADICIONAL = BigDecimal.valueOf(0.38);

	public static final List<Long> PAGADOR_GALLERIA = Arrays.asList(15l, 34l, 14l, 182l, 417l, 803l);
	
	public static final List<Long> COMERCIAL_INTERNO = Arrays.asList(5l, 6l, 14l, 18l, 27l, 34l, 35l, 60l, 71l, 87l,
			102l, 256l, 357l, 359l, 376l, 393l, 571l, 689l, 826l, 844l, 944l, 960l);
	
	public static final String  URL_SISCOAT_REA_WEBHOOK = "https://backoffice.galleriabank.com.br/sistema/siscoat/rea/webhook?Token=";
	public static final String  URL_SISCOAT_ENGINE_WEBHOOK = "https://backoffice.galleriabank.com.br/sistema/siscoat/engine/webhook?Token=";
	public static final String  URL_SISCOAT_DOCKET_WEBHOOK	 = "https://backoffice.galleriabank.com.br/sistema/siscoat/docket/webhook?Token=";
	
}
