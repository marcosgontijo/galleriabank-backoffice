package com.webnowbr.siscoat.cobranca.db.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class DocketRetorno implements Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 940099914385875686L;
	
	private DocketRetornoPedido pedido;

	public DocketRetornoPedido getPedido() {
		return pedido;
	}

	public void setPedido(DocketRetornoPedido pedido) {
		this.pedido = pedido;
	}
	
	
}