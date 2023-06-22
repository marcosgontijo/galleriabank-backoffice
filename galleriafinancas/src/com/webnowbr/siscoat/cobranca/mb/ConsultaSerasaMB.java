package com.webnowbr.siscoat.cobranca.mb;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "consultaSerasaMB")
@SessionScoped

public class ConsultaSerasaMB {
	private String cpf;
	
	
	
	public String clear() {
		return "/Atendimento/ConsultasDirectd/consultaSerasa.xhtml";
		
	}
	
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	

}
