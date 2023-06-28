package com.webnowbr.siscoat.cobranca.mb;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "consultaPEPMB")
@SessionScoped
public class ConsultaPEPMB {
	
	private String cpfCnpj;
	private String retornoPEP;
	
	public String clear() {
		this.cpfCnpj = "";
		return "/Atendimento/ConsultasDirectd/ConsultaPEPNovo.xhtml";
		
	}
	public String getCpfCnpj() {
		return cpfCnpj;
	}
	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}
	public String getRetornoPEP() {
		return retornoPEP;
	}
	public void setRetornoPEP(String retornoPEP) {
		this.retornoPEP = retornoPEP;
	}
	
	

}
