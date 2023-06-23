package com.webnowbr.siscoat.cobranca.mb;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="consultaCenprotMB")
@SessionScoped
public class ConsultaCenprotMB {
	private String cpfCnpj;
	private String retornoCenprot;
	
	public String clear() {
		this.setCpfCnpj("");
	return "/Atendimento/ConsultasDirectd/ConsultaCenprot.xhtml";
	}

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

	public String getRetornoCenprot() {
		return retornoCenprot;
	}

	public void setRetornoCenprot(String retornoCenprot) {
		this.retornoCenprot = retornoCenprot;
	}

}
