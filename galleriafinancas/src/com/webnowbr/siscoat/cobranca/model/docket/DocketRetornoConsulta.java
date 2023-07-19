package com.webnowbr.siscoat.cobranca.model.docket;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DocketRetornoConsulta {

	@SerializedName("pedido")
	private DocketPedido pedido;

	public DocketPedido getPedido() {
		return pedido;
	}

	public void setPedido(DocketPedido pedido) {
		this.pedido = pedido;
	}

}
