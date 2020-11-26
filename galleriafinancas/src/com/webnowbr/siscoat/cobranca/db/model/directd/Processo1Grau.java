package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Processo1Grau implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String nomeForo;	
	private List<Processos> processos;
	
	public Processo1Grau() {
		this.processos = new ArrayList<Processos>();
	}
	
	public String getNomeForo() {
		return nomeForo;
	}
	public void setNomeForo(String nomeForo) {
		this.nomeForo = nomeForo;
	}
	public List<Processos> getProcessos() {
		return processos;
	}
	public void setProcessos(List<Processos> processos) {
		this.processos = processos;
	}
}