package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Processos implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String numero;	
	private String urlProcesso;	
	private String motivo;		
	private String exeqte;	
	private String reqdo;	
	private String exectdo;		
	private String recebidoEm;			

	private ProcessoDetalhes processoDetalhes;
	
	public Processos() {
		this.processoDetalhes = new ProcessoDetalhes();
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getUrlProcesso() {
		return urlProcesso;
	}

	public void setUrlProcesso(String urlProcesso) {
		this.urlProcesso = urlProcesso;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String getExeqte() {
		return exeqte;
	}

	public void setExeqte(String exeqte) {
		this.exeqte = exeqte;
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

	public String getRecebidoEm() {
		return recebidoEm;
	}

	public void setRecebidoEm(String recebidoEm) {
		this.recebidoEm = recebidoEm;
	}

	public ProcessoDetalhes getProcessoDetalhes() {
		return processoDetalhes;
	}

	public void setProcessoDetalhes(ProcessoDetalhes processoDetalhes) {
		this.processoDetalhes = processoDetalhes;
	}
}