package com.webnowbr.siscoat.contab.db.model;

import java.util.List;

import com.webnowbr.siscoat.relatorio.vo.RelatorioBalanco;

public class TaskCalcularVariaveisReceber implements Runnable {
	 List<RelatorioBalanco> relatorioBalancoReceber;
	 BalancoPatrimonial objetoBalanco;
	 int inicio;
	 int fim;
	 
	 
	 
	public TaskCalcularVariaveisReceber(List<RelatorioBalanco> relatorioBalancoReceber,
			BalancoPatrimonial objetoBalanco,int inicio, int fim) {
		super();
		this.relatorioBalancoReceber = relatorioBalancoReceber;
		this.objetoBalanco = objetoBalanco;
		this.inicio = inicio;
		this.fim = fim;
	}


	public List<RelatorioBalanco> getRelatorioBalancoReceber() {
		return relatorioBalancoReceber;
	}


	public void setRelatorioBalancoReceber(List<RelatorioBalanco> relatorioBalancoReceber) {
		this.relatorioBalancoReceber = relatorioBalancoReceber;
	}


	public BalancoPatrimonial getObjetoBalanco() {
		return objetoBalanco;
	}


	public void setObjetoBalanco(BalancoPatrimonial objetoBalanco) {
		this.objetoBalanco = objetoBalanco;
	}


	@Override
	public void run() {
		objetoBalanco.calcularVariaveisReceber(relatorioBalancoReceber, inicio);
	}
}