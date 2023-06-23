package com.webnowbr.siscoat.cobranca.mb;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="consultaProtestosMB")
@SessionScoped
public class ConsultaProtrestrosMB {
	private String cpfCnpj;
	private String retornoProtestos;
	
	
	public String clear() {
		this.cpfCnpj = "";
		return "/Atendimento/ConsultasDirectd/ConsultaProtestos.xhtml";
	}
	
	public String getCpfCnpj() {
		return cpfCnpj;
	}
	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}
	public String getRetornoProtestos() {
		return retornoProtestos;
	}
	public void setRetornoProtestos(String retornoProtestos) {
		this.retornoProtestos = retornoProtestos;
	}
	

}
