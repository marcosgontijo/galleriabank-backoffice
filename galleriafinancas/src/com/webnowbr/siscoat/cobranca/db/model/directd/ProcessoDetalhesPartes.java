package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ProcessoDetalhesPartes implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String tipo;	
	private String nomeParte;	
	private String exeqte;		
	private String advogado;		
	private String reqdo;		
	private String exectdo;
	public String getExeqte() {
		return exeqte;
	}
	public void setExeqte(String exeqte) {
		this.exeqte = exeqte;
	}
	public String getAdvogado() {
		return advogado;
	}
	public void setAdvogado(String advogado) {
		this.advogado = advogado;
	}
	public String getReqdo() {
		return reqdo;
	}
	public void setReqdo(String reqdo) {
		this.reqdo = reqdo;
	}
	public String getExectdo() {
		return exectdo;
	}
	public void setExectdo(String exectdo) {
		this.exectdo = exectdo;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getNomeParte() {
		return nomeParte;
	}
	public void setNomeParte(String nomeParte) {
		this.nomeParte = nomeParte;
	}
}