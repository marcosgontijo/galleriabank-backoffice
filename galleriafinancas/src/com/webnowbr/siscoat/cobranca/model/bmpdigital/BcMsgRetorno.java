package com.webnowbr.siscoat.cobranca.model.bmpdigital;

import com.google.gson.annotations.SerializedName;

public class BcMsgRetorno {

	@SerializedName("Codigo")
	private String codigo;
	@SerializedName("Mensagem")
	private String mensagem;
	
	public BcMsgRetorno() {
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
}
