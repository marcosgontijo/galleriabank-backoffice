package com.webnowbr.siscoat.cobranca.auxiliar;

import java.math.BigDecimal;

public class UtilsMB {

	public static final String getBigdecimalAsString(BigDecimal valorBigDecimal) {
		BigDecimal valor = null;

		try {

			try {
				valor = (BigDecimal) valorBigDecimal;
			} catch (Exception e) {
				e.printStackTrace();
			}
			String str = valor.toString();
			String nValueFinal = "";

			String nValue = "";

			char[] array = str.toCharArray();

			for (int i = 0; i < array.length; i++) {
				if (array[i] == '.' && array[i] != ',') {
					nValue += ",";
				} else if (array[i] != '.' && array[i] != ',') {
					nValue += array[i];
				}
			}

			if (nValue.contains(",")) {
				// Insere os separadores de milhar
				String inteira = nValue.split(",")[0];
				String fracionaria = nValue.split(",")[1];
				int separadorMilharCount = 0;
				for (int i = 0; i <= inteira.length() - 1; i++) {
					if (separadorMilharCount == 3) {
						nValueFinal = nValueFinal + ".";
						separadorMilharCount = 0;
					}
					nValueFinal += inteira.charAt((inteira.length() - 1) - i);
					separadorMilharCount++;
				}
				String aux = "";
				for (int i = 0; i <= nValueFinal.length() - 1; i++) {
					aux += nValueFinal.charAt((nValueFinal.length() - 1) - i);
				}
				nValueFinal = aux + "," + fracionaria;
				return nValueFinal;
			} else {
				return nValue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "---";
	}
}
