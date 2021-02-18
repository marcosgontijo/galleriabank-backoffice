package com.webnowbr.siscoat.common;

import java.math.BigDecimal;

public  class Util {

	public static BigDecimal zeroIsNull(BigDecimal valor) {
		if (valor == null)
			return new BigDecimal(0);
		else
			return valor;

	}
}
