package com.webnowbr.siscoat.cobranca.db.model.directd;

import java.math.BigDecimal;

public class PorcentagemImovel {
	
	private String descricao;
	
	private BigDecimal porcentagem;
	private boolean eBotao;
	private boolean personalizado;
	
	
	public PorcentagemImovel(String descricao, BigDecimal porcentagem, boolean eBotao, boolean personalizado) {
		this.descricao = descricao;
		this.porcentagem = porcentagem;
		this.eBotao = eBotao;
		this.personalizado = personalizado;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public BigDecimal getPorcentagem() {
		return porcentagem;
	}

	public void setPorcentagem(BigDecimal porcentagem) {
		this.porcentagem = porcentagem;
	}

	public boolean iseBotao() {
		return eBotao;
	}

	public void seteBotao(boolean eBotao) {
		this.eBotao = eBotao;
	}

	public boolean isPersonalizado() {
		return personalizado;
	}

	public void setPersonalizado(boolean personalizado) {
		this.personalizado = personalizado;
	}
	
	

}
