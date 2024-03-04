package com.webnowbr.siscoat.cobranca.db.model;

import java.util.Date;
import java.util.Set;

public class ContratoCobrancaLogsAlteracao {
	
	private long id;
	private Date dataAlteracao;
	private String usuario;
	private String observacao;
	private String statusEsteira;			
	private ContratoCobranca contratoCobranca;
	private Set<ContratoCobrancaLogsAlteracaoDetalhe> detalhes;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getDataAlteracao() {
		return dataAlteracao;
	}
	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public Set<ContratoCobrancaLogsAlteracaoDetalhe> getDetalhes() {
		return detalhes;
	}
	public void setDetalhes(Set<ContratoCobrancaLogsAlteracaoDetalhe> detalhes) {
		this.detalhes = detalhes;
	}
	public String getStatusEsteira() {
		return statusEsteira;
	}
	public void setStatusEsteira(String statusEsteira) {
		this.statusEsteira = statusEsteira;
	}
	public ContratoCobranca getContratoCobranca() {
		return contratoCobranca;
	}
	public void setContratoCobranca(ContratoCobranca contratoCobranca) {
		this.contratoCobranca = contratoCobranca;
	}
	
}
